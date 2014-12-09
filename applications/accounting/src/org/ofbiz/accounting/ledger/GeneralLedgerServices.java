/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.accounting.ledger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class GeneralLedgerServices {

    public static final String module = GeneralLedgerServices.class.getName();

    private static BigDecimal ZERO = BigDecimal.ZERO;

    public static Map<String, Object> createUpdateCostCenter(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        BigDecimal totalAmountPercentage = ZERO;
        Map<String, Object> createGlAcctCatMemFromCostCentersMap = null;
        String glAccountId = (String) context.get("glAccountId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, String> amountPercentageMap = UtilGenerics.checkMap(context.get("amountPercentageMap"));
        totalAmountPercentage = GeneralLedgerServices.calculateCostCenterTotal(amountPercentageMap);
        for (String rowKey : amountPercentageMap.keySet()) {
            String rowValue = amountPercentageMap.get(rowKey);
            if (UtilValidate.isNotEmpty(rowValue)) {
                createGlAcctCatMemFromCostCentersMap = UtilMisc.toMap("glAccountId", glAccountId,
                        "glAccountCategoryId", rowKey, "amountPercentage", new BigDecimal(rowValue),
                        "userLogin", userLogin, "totalAmountPercentage", totalAmountPercentage);
            } else {
                createGlAcctCatMemFromCostCentersMap = UtilMisc.toMap("glAccountId", glAccountId,
                        "glAccountCategoryId", rowKey, "amountPercentage", new BigDecimal(0),
                        "userLogin", userLogin, "totalAmountPercentage", totalAmountPercentage);
            }
            try {
                dispatcher.runSync("createGlAcctCatMemFromCostCenters", createGlAcctCatMemFromCostCentersMap);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static BigDecimal calculateCostCenterTotal(Map<String, String> amountPercentageMap) {
        BigDecimal totalAmountPercentage = ZERO;
        for (String rowKey : amountPercentageMap.keySet()) {
            if (UtilValidate.isNotEmpty(amountPercentageMap.get(rowKey))) {
                BigDecimal rowValue = new BigDecimal(amountPercentageMap.get(rowKey));
                if (rowValue != null)
                    totalAmountPercentage = totalAmountPercentage.add(rowValue);
            }
        }
        return totalAmountPercentage;
    }
    
    public static Map getGenericOpeningBalanceForParty(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyId");
		Timestamp tillDate = (Timestamp) context.get("tillDate");
		List exprListForParameters = FastList.newInstance();
		List boothPaymentsList = FastList.newInstance();
		List partyInvoicesList = FastList.newInstance();
		Map openingBalanceMap = FastMap.newInstance();
		BigDecimal invoicesTotalAmount = BigDecimal.ZERO;
		BigDecimal invoicesTotalDueAmount = BigDecimal.ZERO;
		Timestamp dayBegin = UtilDateTime.getDayStart(tillDate);
		try {
			GenericValue partyDetail = delegator.findOne("Party",UtilMisc.toMap("partyId", partyId), false);
			if (UtilValidate.isEmpty(partyDetail)) {
				Debug.logInfo(partyId+ "'is not a valid party", "");
				return ServiceUtil.returnError(partyId+ "'is not a valid party");
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		boolean isOBCallForAP = Boolean.FALSE;
		if(UtilValidate.isNotEmpty(context.get("isOBCallForAP"))){
			isOBCallForAP = (Boolean)context.get("isOBCallForAP");
		}
		List invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");
		//separate query for AR and AP
		if(isOBCallForAP){
			exprListForParameters.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			//conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			exprListForParameters.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		}else{//no need to send  For AR
			exprListForParameters.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"SALES_INVOICE"));
			//conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,"Company"));
			exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		}
		//exprListForParameters.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));
		//exprListForParameters.add(EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS, "SALES_INVOICE"));
		//exprListForParameters.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN, dayBegin));
		exprListForParameters.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN, dayBegin));
		exprListForParameters.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, invoiceStatusList));
		exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR, EntityCondition.makeCondition("paidDate",	EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
		EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		try {
			partyInvoicesList = delegator.findList("InvoiceAndType", paramCond,UtilMisc.toSet("invoiceId"), null, findOptions, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		BigDecimal openingBalance = BigDecimal.ZERO;
		BigDecimal invoicePendingAmount = BigDecimal.ZERO;
		BigDecimal advancePaymentAmount = BigDecimal.ZERO;
		if (!UtilValidate.isEmpty(partyInvoicesList)) {
			Set invoiceIdSet = new HashSet(EntityUtil.getFieldListFromEntityList(partyInvoicesList,"invoiceId", false));
			List invoiceIds = new ArrayList(invoiceIdSet);
			Debug.log("===invoiceIds=in==GenericPartyLedger=="+invoiceIds);
			// First compute the total invoice outstanding amount as of opening balance date.
			for (int i = 0; i < invoiceIds.size(); i++) {
				String invoiceId = (String) invoiceIds.get(i);
				List<GenericValue> pendingInvoiceList = FastList.newInstance();
				List exprList = FastList.newInstance();
				exprList.add(EntityCondition.makeCondition("invoiceId",	EntityOperator.EQUALS, invoiceId));
				exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate",EntityOperator.EQUALS, null),EntityOperator.OR, EntityCondition.makeCondition("paidDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
				exprList.add(EntityCondition.makeCondition("pmPaymentDate",	EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
				EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
				try {
					pendingInvoiceList = delegator.findList("InvoiceAndApplAndPayment", cond, null, null, null,	false);
					// no payment applications then add invoice total amount to OB or unapplied amount.
					
					Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("userLogin", userLogin, "invoiceId",invoiceId));
					if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
						Debug.logError(getInvoicePaymentInfoListResult.toString(),module);
						return ServiceUtil.returnError(null, null, null,getInvoicePaymentInfoListResult);
					}
					Map invoicePaymentInfo = (Map) ((List) getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
					BigDecimal outstandingAmount = (BigDecimal) invoicePaymentInfo.get("outstandingAmount");
					invoicePendingAmount = invoicePendingAmount.add(outstandingAmount);
					for (GenericValue pendingInvoice : pendingInvoiceList) {
						invoicePendingAmount = invoicePendingAmount.add(pendingInvoice.getBigDecimal("amountApplied"));
					}
				} catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.toString());
				}
			}
		}
		//Now handle any unapplied payments as of opening balance date
		// Here first get the payments that were made before opening balance date and have been partially applied.  
		// Compute the amount that has been applied after opening balance date plus any unapplied amount
		List exprList = FastList.newInstance();
		List<GenericValue> pendingPaymentsList = FastList.newInstance();
		if(isOBCallForAP){
			exprList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		}else{//no need to send  For AR
			exprList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		}
		//exprList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));
		//exprList.add(EntityCondition.makeCondition("dueDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		exprList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		exprList.add(EntityCondition.makeCondition("pmPaymentDate",EntityOperator.LESS_THAN, dayBegin));
		EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			pendingPaymentsList = delegator.findList("InvoiceAndApplAndPayment", cond, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		Set paymentSet = new HashSet(EntityUtil.getFieldListFromEntityList(pendingPaymentsList, "paymentId", false));
		for (GenericValue pendingPayments : pendingPaymentsList) {
			if (UtilValidate.isEmpty(pendingPayments.getTimestamp("paidDate")) || (UtilDateTime.getDayStart(pendingPayments.getTimestamp("paidDate"))).equals(UtilDateTime.getDayStart(pendingPayments.getTimestamp("invoiceDate")))
					|| (pendingPayments.getTimestamp("paidDate")).compareTo(dayBegin) >= 0) {
				advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amountApplied"));
			}

		}

		List paymentList = new ArrayList(paymentSet);
		for (int i = 0; i < paymentList.size(); i++) {
			try {
				Map result = dispatcher.runSync("getPaymentNotApplied",UtilMisc.toMap("userLogin", userLogin, "paymentId",(String) paymentList.get(i)));
				advancePaymentAmount = advancePaymentAmount.add((BigDecimal) result.get("unAppliedAmountTotal"));
			} catch (GenericServiceException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}

		}
		// here get the all the zero application paymentId's
		List<String> zeroAppPaymentIds = EntityUtil.getFieldListFromEntityList(pendingPaymentsList, "paymentId", true);
		// Next get payments that were made before opening balance date and have zero applications
		exprList.clear();
		if(isOBCallForAP){
			exprList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS, partyId));
		}else{//no need to send  For AR
			exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyId));
		}
		//exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyId));
		exprList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.NOT_EQUAL, "SECURITYDEPSIT_PAYIN"));
		exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentApplicationId", EntityOperator.EQUALS,null), EntityOperator.OR, EntityCondition.makeCondition(EntityCondition.makeCondition("isFullyApplied",EntityOperator.EQUALS, null), EntityOperator.OR,EntityCondition.makeCondition("isFullyApplied",EntityOperator.EQUALS, "N"))));
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("PMNT_VOID", "PMNT_CANCELLED", "PMNT_NOT_PAID")));
		exprList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, dayBegin));
		// exculde all the zero payment application payments
		if (UtilValidate.isNotEmpty(zeroAppPaymentIds)) {
			exprList.add(EntityCondition.makeCondition("paymentId",EntityOperator.NOT_IN, zeroAppPaymentIds));
		}
		EntityCondition paymentCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			EntityFindOptions findOption = new EntityFindOptions();
			findOption.setDistinct(true);
			pendingPaymentsList = delegator.findList("PaymentAndApplicationLftJoin", paymentCond,UtilMisc.toSet("paymentId"), null, findOption, false);
			for (GenericValue pendingPayments : pendingPaymentsList) {
				Map result = dispatcher.runSync("getPaymentNotApplied",UtilMisc.toMap("userLogin", userLogin, "paymentId",pendingPayments.getString("paymentId")));
				advancePaymentAmount = advancePaymentAmount.add((BigDecimal) result.get("unAppliedAmountTotal"));
				// advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amount"));
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
		// here get the cheque bounce amount till cancel of payments
		exprList.clear();
		if(isOBCallForAP){
			exprList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS, partyId));
		}else{//no need to send  For AR
			exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyId));
		}
		//exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyId));
		exprList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, "CHEQUE_PAYIN"));
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "PMNT_VOID"));
		exprList.add(EntityCondition.makeCondition("chequeReturns",EntityOperator.EQUALS, "Y"));
		exprList.add(EntityCondition.makeCondition("cancelDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		exprList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, dayBegin));

		EntityCondition payReturnCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			pendingPaymentsList = delegator.findList("Payment", payReturnCond,UtilMisc.toSet("amount"), null, null, false);
			for (GenericValue pendingPayments : pendingPaymentsList) {
				advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amount"));
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		openingBalance = invoicePendingAmount.subtract(advancePaymentAmount);
		openingBalanceMap.put("openingBalance", openingBalance);
		return openingBalanceMap;
	}
}
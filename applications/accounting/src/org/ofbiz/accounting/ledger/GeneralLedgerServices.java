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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.Map.Entry;
import java.text.ParseException;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.util.UtilAccounting;
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
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;

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
		String partyIdFrom = (String) context.get("partyIdFrom");
		Timestamp tillDate = (Timestamp) context.get("tillDate");
		String purposeTypeId=(String)context.get("purposeTypeId");
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
		List invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF","INVOICE_IN_PROCESS");
		//separate query for AR and AP
		if(isOBCallForAP){
			exprListForParameters.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			//conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			exprListForParameters.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
			exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom));
		}else{//no need to send  For AR
			exprListForParameters.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"SALES_INVOICE"));
			//conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,"Company"));
			exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			exprListForParameters.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
		}
		
		if(UtilValidate.isNotEmpty(context.get("purposeTypeId"))){
			exprListForParameters.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, purposeTypeId));
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
			//Debug.log("===invoiceIds=in==GenericPartyLedger=="+invoiceIds);
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
			exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyIdFrom));
		}else{//no need to send  For AR
			exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyId));
			exprList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS, partyIdFrom));
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
			exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyIdFrom));
		}else{//no need to send  For AR
			exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyId));
			exprList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS, partyIdFrom));
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
		openingBalanceMap.put("invoicePendingAmount", invoicePendingAmount);
		openingBalanceMap.put("advancePaymentAmount", advancePaymentAmount);
		return openingBalanceMap;
	}
    public static Map<String, Object> getGlAccountOpeningBalance(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        String glAccountId = (String) context.get("glAccountId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
    	      if(UtilValidate.isNotEmpty(fromDate)){
	    		 fromDate = UtilDateTime.getDayStart(fromDate);
	          }
    	      if(UtilValidate.isNotEmpty(thruDate)){
    	    	 thruDate = UtilDateTime.getDayEnd(thruDate);
	          }
 			    Map finYearContext = FastMap.newInstance();
 				finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
 				finYearContext.put("organizationPartyId", "Company");
 				finYearContext.put("userLogin", userLogin);
 				finYearContext.put("findDate", fromDate);
 				finYearContext.put("excludeNoOrganizationPeriods", "Y");
 				List customTimePeriodList = FastList.newInstance();
 				Map resultCtx = FastMap.newInstance();
 				try{
 					resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
 					if(ServiceUtil.isError(resultCtx)){
 						Debug.logError("Problem in fetching financial year ", module);
 						return ServiceUtil.returnError("Problem in fetching financial year ");
 					}
 				}catch(GenericServiceException e){
 					Debug.logError(e, module);
 					return ServiceUtil.returnError(e.getMessage());
 				}
 				customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
 				String finYearId = "";
 				Timestamp finYearFromDate=null;
 				if(UtilValidate.isNotEmpty(customTimePeriodList)){
 					GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
 					finYearId = (String)customTimePeriod.get("customTimePeriodId");
 					finYearFromDate=new java.sql.Timestamp(((Date) customTimePeriod.get("fromDate")).getTime());
 				}
    			List customTimePeriodIds =FastList.newInstance();
    			try {      
    			List conditionList = UtilMisc.toList(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
    			       conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_MONTH"));
    			       conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(finYearFromDate)));
    			       conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN ,UtilDateTime.toSqlDate(fromDate)));
    			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			customTimePeriodList = delegator.findList("CustomTimePeriod", condition, UtilMisc.toSet("customTimePeriodId"), null, null, true);
    			customTimePeriodIds = EntityUtil.getFieldListFromEntityList(customTimePeriodList, "customTimePeriodId", true);
    			}catch(Exception e){
 					Debug.logError(e, module);
 					return ServiceUtil.returnError(e.getMessage());
 				}
    			BigDecimal openingBalance = BigDecimal.ZERO;
    			for(int i=0;i<customTimePeriodIds.size();i++){
				    BigDecimal debits = BigDecimal.ZERO;
				    BigDecimal credits = BigDecimal.ZERO;
	 					GenericValue glAccountHistory = delegator.findOne("GlAccountHistory", UtilMisc.toMap("glAccountId", glAccountId,"organizationPartyId","Company","customTimePeriodId",customTimePeriodIds.get(i)),false);
	 					if(UtilValidate.isNotEmpty(glAccountHistory)){
	 						debits=(BigDecimal) glAccountHistory.get("postedDebits");
	 						credits=(BigDecimal)glAccountHistory.get("postedCredits");
		 					openingBalance =openingBalance.add(debits.subtract(credits));
		 		        } 
		 		}
        		result.put("openingBal", openingBalance);
        } catch (Exception e) {
            Debug.logError(e, "Problem getting openingBal for glAccountId" + glAccountId, module);
            return ServiceUtil.returnError("Problem getting openingBal for glAccountId" + glAccountId);
        }
        return result;
    }
    
    //glAccount opening balance for party
    
    public static Map<String, Object> getGlAccountOpeningBalanceForParty(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        //String glAccountId = (String) context.get("glAccountId");
        String partyId = (String) context.get("partyId");
        List glAccountIds = (List) context.get("glAccountIds");
        String organizationPartyId = "Company";
        GenericValue lastClosedTimePeriod=null;
        Timestamp lastClosedDate = null;
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
    	      if(UtilValidate.isNotEmpty(fromDate)){
	    		 fromDate = UtilDateTime.getDayStart(fromDate);
	          }
    	      if(UtilValidate.isNotEmpty(thruDate)){
    	    	 thruDate = UtilDateTime.getDayEnd(thruDate);
	          }
    	      // get lost closed period here
    	      Map lastFinContext = FastMap.newInstance();
    	      lastFinContext.put("periodTypeId","FISCAL_YEAR");
    	      lastFinContext.put("organizationPartyId", organizationPartyId);
    	      lastFinContext.put("userLogin", userLogin);
    	      lastFinContext.put("findDate", UtilDateTime.toSqlDate(fromDate));
    	      //lastFinContext.put("onlyFiscalPeriods", Boolean.FALSE);
			   Map resultCtx = FastMap.newInstance();
				try{
					resultCtx = dispatcher.runSync("findLastClosedDateForPartyLedger", lastFinContext);
					if(ServiceUtil.isError(resultCtx)){
						Debug.logError("Problem in fetching financial year ", module);
						return ServiceUtil.returnError("Problem in fetching financial year ");
					}
					lastClosedTimePeriod = (GenericValue)resultCtx.get("lastClosedTimePeriod");
					lastClosedDate = (Timestamp)resultCtx.get("lastClosedDate");
				}catch(GenericServiceException e){
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				} 
				List customTimePeriodList = FastList.newInstance();
    	      
 			  /*  Map finYearContext = FastMap.newInstance();
 				//finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
 				finYearContext.put("organizationPartyId", organizationPartyId);
 				finYearContext.put("userLogin", userLogin);
 				finYearContext.put("findDate", fromDate);
 				finYearContext.put("excludeNoOrganizationPeriods", "Y");
 				List customTimePeriodList = FastList.newInstance();
 				resultCtx = FastMap.newInstance();
 				try{
 					resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
 					if(ServiceUtil.isError(resultCtx)){
 						Debug.logError("Problem in fetching financial year ", module);
 						return ServiceUtil.returnError("Problem in fetching financial year ");
 					}
 				}catch(GenericServiceException e){
 					Debug.logError(e, module);
 					return ServiceUtil.returnError(e.getMessage());
 				}
 				customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
 				*/String finYearId = "";
 				Timestamp finYearFromDate=null;
 				Timestamp finYearthruDate=null;
 				Timestamp finYearNextFromDate=null;
 				if(UtilValidate.isNotEmpty(lastClosedTimePeriod)){
 					GenericValue customTimePeriod = lastClosedTimePeriod;
 					finYearId = (String)customTimePeriod.get("customTimePeriodId");
 					finYearFromDate=new java.sql.Timestamp(((Date) customTimePeriod.get("fromDate")).getTime());
 					Debug.log("finYearId============"+finYearId);
 					
 				}
    			
 				List extTransTypeIdsList1 = FastList.newInstance();
    			List extTransTypeIdsList2 = FastList.newInstance();
    			List assetAndLiabilityIdsList = FastList.newInstance();
    			 extTransTypeIdsList1=UtilMisc.toList("SALES","SALES_INVOICE","PURCHASE_INVOICE","INVOICE_APPL");
    			 extTransTypeIdsList2=UtilMisc.toList("RECEIPT","PAYMENT_ACCTG_TRANS","PAYMENT_APPL","OUTGOING_PAYMENT","INCOMING_PAYMENT");
    			 extTransTypeIdsList2.add("OB_TB");
    			 assetAndLiabilityIdsList=UtilMisc.toList("CURRENT_ASSET","LONGTERM_ASSET","CURRENT_LIABILITY","LONGTERM_LIABILITY","CASH_EQUIVALENT");
    			 BigDecimal finalOpeningBalance = BigDecimal.ZERO;
     			BigDecimal postedFinalDebits = BigDecimal.ZERO;
     			BigDecimal postedFinalCredits = BigDecimal.ZERO; 
     			BigDecimal postedDebits = BigDecimal.ZERO;
     			BigDecimal postedCredits = BigDecimal.ZERO; 
 	    		BigDecimal openingBalance = BigDecimal.ZERO;
 				List customTimePeriodIds =FastList.newInstance();
    			try {      
    			//List conditionList = UtilMisc.toList(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
    				   List conditionList =FastList.newInstance();
    			       conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_MONTH"));
    			       if(UtilValidate.isNotEmpty(lastClosedDate)){
    			    	   conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(lastClosedDate)));
    			       }
    			      
    			       conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN ,UtilDateTime.toSqlDate(fromDate)));
    			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			List<String> orderBy = UtilMisc.toList("-thruDate");
    			Debug.log("CustomTimePeriod condition==================="+condition);
    			customTimePeriodList = delegator.findList("CustomTimePeriod", condition, UtilMisc.toSet("customTimePeriodId"), orderBy, null, true);
    			
    			customTimePeriodIds = EntityUtil.getFieldListFromEntityList(customTimePeriodList, "customTimePeriodId", true);
    			Debug.log("customTimePeriodIds============"+customTimePeriodIds);
    			Timestamp nextDayStartDayEnd =null;
    			Timestamp fromDatePreviousDayEnd=null;
    			if(UtilValidate.isNotEmpty(customTimePeriodIds)){
    				String lastCustPeriodId=(String)customTimePeriodIds.get(0);    				
    				GenericValue lastCustPeriodDetails = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", lastCustPeriodId), false);
    	    	     Timestamp lastCustPeriodThru = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(lastCustPeriodDetails.getDate("thruDate")));
    	    	      nextDayStartDayEnd = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(lastCustPeriodThru, 1));
    	    	    
    	    	      fromDatePreviousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
    	    	    
    			}else{
    				nextDayStartDayEnd = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(lastClosedDate, 1));
    				fromDatePreviousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
    			}
	    			if(UtilValidate.isNotEmpty(nextDayStartDayEnd)&&UtilValidate.isNotEmpty(fromDatePreviousDayEnd)){
	    				//getting betweent date entries
	    				
	    	    	     List andExprs1 = FastList.newInstance();
	    	    	     if(UtilValidate.isEmpty(organizationPartyId)){
	    	    	    	 organizationPartyId = "Company";
	    	    	     }
	    	    	     andExprs1.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
	    	    	     andExprs1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	    	    	     if(UtilValidate.isNotEmpty(organizationPartyId)){
	    	    	    	 //get internal orgs and do in query here
	    	    	    	 andExprs1.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
	    	    	     }
	    	    	     
	    	    	    	    	    	     
	    	    	     andExprs1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, nextDayStartDayEnd));
	    	    	     andExprs1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDatePreviousDayEnd));
	    	    	     EntityCondition andCond1 = EntityCondition.makeCondition(andExprs1, EntityOperator.AND);
	    	    	     
	    	    	     EntityListIterator allPostedTransactionTotalItr1 = delegator.find("AcctgTransAndEntries", andCond1, null, null, null, null);
	    	    	     GenericValue transTotalEntry1;
	    	    	     
	    	    	     while(allPostedTransactionTotalItr1 != null && (transTotalEntry1 = allPostedTransactionTotalItr1.next()) != null) {
	    	    	    	// Debug.log("transTotalEntry1======="+transTotalEntry1);
	    	    	    	 String glAcntId = transTotalEntry1.getString("glAccountId");
	    	    	    	 String acctgTransTypeId = transTotalEntry1.getString("acctgTransTypeId");
	    	    	    	 String flag=transTotalEntry1.getString("debitCreditFlag");
	    	    	    	 BigDecimal amtVal=BigDecimal.ZERO;
	    	    	    	 if(UtilValidate.isNotEmpty(transTotalEntry1.getBigDecimal("amount"))){
	    	    	    		 amtVal=transTotalEntry1.getBigDecimal("amount");
	    	    	    	 }
	    	    	    	 
	    	    	    	 
	    	    	    	 GenericValue glAccntDetails = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId",glAcntId), false);
		 		    			
	 		    			 if(UtilValidate.isEmpty(glAccntDetails.get("isControlAcctg"))||(UtilValidate.isNotEmpty(glAccntDetails.get("isControlAcctg"))&&(!"Y".equals(glAccntDetails.get("isControlAcctg"))))){
	 		    				String glAccountClassId=(String)glAccntDetails.get("glAccountClassId");
	 		    				 if(extTransTypeIdsList1.contains(acctgTransTypeId)){
	 		    					 if(glAcntId.equals("121000")||glAcntId.equals("210000")){	
	 		    						if(flag.equals("D")){
					 					  postedDebits = postedDebits.add(amtVal);
	 		    						}else{
					 					   postedCredits = postedCredits.add(amtVal);
	 		    						}
	 		    					 }
	 		    				 }
	 		    				 if(extTransTypeIdsList2.contains(acctgTransTypeId)){
	 		    					 if(assetAndLiabilityIdsList.contains(glAccountClassId)){
	 		    						if(flag.equals("D")){
						 					  postedDebits = postedDebits.add(amtVal);
		 		    						}else{
						 					   postedCredits = postedCredits.add(amtVal);
		 		    						}
	 		    					 }
	 		    				 }
	 		    				 if(acctgTransTypeId.equals("JOURNAL")){
	 		    					 if(glAcntId.equals("121000")||glAcntId.equals("210000")){
	 		    						if(flag.equals("D")){
						 				   postedDebits = postedDebits.add(amtVal);
	 		    						}else{
					 					   postedCredits = postedCredits.add(amtVal);
	 		    						}
	 		    					 }
	 		    				 }
	 		    				 if(acctgTransTypeId.equals("ADJUSTMENT")){
	 		    					if(assetAndLiabilityIdsList.contains(glAccountClassId)){
	 		    						if(flag.equals("D")){
							 				   postedDebits = postedDebits.add(amtVal);
	 		    						}else{
					 					   postedCredits = postedCredits.add(amtVal);
	 		    						}
	 		    					 }
	 		    				 }
	 		    			 }   	    	 
	    	    	     }
	    	    	     allPostedTransactionTotalItr1.close();
	    			}
    			}catch(Exception e){
 					Debug.logError(e, module);
 					return ServiceUtil.returnError(e.getMessage());
 				}    			
    			//Debug.log("postedCredits==============="+postedCredits);
    			//Debug.log("postedDebits==============="+postedDebits);
    			Debug.log("lastClosedTimePeriod============"+lastClosedTimePeriod);
		    			if(UtilValidate.isNotEmpty(lastClosedTimePeriod)){
		    				Map lastClosedGlBalances = UtilAccounting.getLastClosedGlBalanceForParty(ctx, UtilMisc.toMap("organizationPartyId", organizationPartyId,"customTimePeriodId",finYearId,"partyId",partyId));
		    				List lastClosedGlBalanceList = (List)lastClosedGlBalances.get("openingGlHistory");
		    				Debug.log("lastClosedGlBalanceList==============="+lastClosedGlBalanceList);
		    				if(UtilValidate.isNotEmpty(lastClosedGlBalanceList)){
		    					for(int l=0;l<lastClosedGlBalanceList.size();l++){
		    						Map lastClosedPartyGlBal=(Map) lastClosedGlBalanceList.get(l);
		    						String glAcId=(String)lastClosedPartyGlBal.get("glAccountId");
		    						String clsGlClassId=(String)lastClosedPartyGlBal.get("glAccountClassId");
		    						String clsAcctgTransTypeId=(String)lastClosedPartyGlBal.get("acctgTransTypeId");
		    						GenericValue glAccount = delegator.findOne("GlAccount",UtilMisc.toMap("glAccountId", glAcId), false);
		    						
		    						BigDecimal openBal = (BigDecimal)lastClosedPartyGlBal.get("endingBalance");
		    						if(UtilValidate.isEmpty(glAccount.get("isControlAcctg"))||(UtilValidate.isNotEmpty(glAccount.get("isControlAcctg"))&&(!"Y".equals(glAccount.get("isControlAcctg"))))){
		    							//Debug.log("glAcId==="+glAcId);
		    							if(UtilValidate.isNotEmpty(openBal)){
			    							boolean isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
				    		    			if (isDebitAccount) {
				    		    				if(extTransTypeIdsList1.contains(clsAcctgTransTypeId)){
					 		    					 if(glAcId.equals("121000")||glAcId.equals("210000")){	
						    		    				if(openBal.compareTo(BigDecimal.ZERO)<0){
						    		    					postedCredits = postedCredits.add(openBal.negate());
						    		    				}else {
						    		    					postedDebits = postedDebits.add(openBal);
						    		    				}
					 		    					 }
				    		    				}
				    		    				if(extTransTypeIdsList2.contains(clsAcctgTransTypeId)){
					 		    					 if(assetAndLiabilityIdsList.contains(clsGlClassId)){
					 		    						if(openBal.compareTo(BigDecimal.ZERO)<0){
						    		    					postedCredits = postedCredits.add(openBal.negate());
						    		    				}else {
						    		    					postedDebits = postedDebits.add(openBal);
						    		    				}
					 		    					 }
				    		    				}
				    		    				 if(clsAcctgTransTypeId.equals("JOURNAL")){
					 		    					 if(glAcId.equals("121000")||glAcId.equals("210000")){
					 		    						if(openBal.compareTo(BigDecimal.ZERO)<0){
						    		    					postedCredits = postedCredits.add(openBal.negate());
						    		    					
						    		    				}else {
						    		    					postedDebits = postedDebits.add(openBal);						    		    					
						    		    				}
					 		    					 }
				    		    				 }
				    		    				 if(clsAcctgTransTypeId.equals("ADJUSTMENT")){
						 		    					if(assetAndLiabilityIdsList.contains(clsGlClassId)){
						 		    						if(openBal.compareTo(BigDecimal.ZERO)<0){
							    		    					postedCredits = postedCredits.add(openBal.negate());
							    		    				}else {
							    		    					postedDebits = postedDebits.add(openBal);
							    		    				}
						 		    					}
				    		    				 }
				    		    				
				    		    				
				    		    			}else {
				    		    				
				    		    				
				    		    				if(extTransTypeIdsList1.contains(clsAcctgTransTypeId)){
					 		    					 if(glAcId.equals("121000")||glAcId.equals("210000")){	
					 		    						if (openBal.compareTo(BigDecimal.ZERO)<0) {
						    		    					postedDebits = postedDebits.add(openBal.negate());
						    		    				} else {
						    		    					postedCredits = postedCredits.add(openBal);
						    		    				}
					 		    					 }
				    		    				}
				    		    				if(extTransTypeIdsList2.contains(clsAcctgTransTypeId)){
					 		    					 if(assetAndLiabilityIdsList.contains(clsGlClassId)){
					 		    						if (openBal.compareTo(BigDecimal.ZERO)<0) {
						    		    					postedDebits = postedDebits.add(openBal.negate());
						    		    				} else {
						    		    					postedCredits = postedCredits.add(openBal);
						    		    				}
					 		    					 }
				    		    				}
				    		    				 if(clsAcctgTransTypeId.equals("JOURNAL")){
					 		    					 if(glAcId.equals("121000")||glAcId.equals("210000")){
					 		    						if (openBal.compareTo(BigDecimal.ZERO)<0) {
						    		    					postedDebits = postedDebits.add(openBal.negate());
						    		    				} else {
						    		    					postedCredits = postedCredits.add(openBal);
						    		    				}
					 		    					 }
				    		    				 }
				    		    				 if(clsAcctgTransTypeId.equals("ADJUSTMENT")){
				    		    					 if (openBal.compareTo(BigDecimal.ZERO)<0) {
						    		    					postedDebits = postedDebits.add(openBal.negate());
						    		    				} else {
						    		    					postedCredits = postedCredits.add(openBal);
						    		    				}
				    		    				 }  				
				    		    				
				    		    				
				    		    			}
				    		    			
			    						}
		    						}
		    					}
		    					
		    				}
		    				
		    			}
		    			//Debug.log("customTimePeriodIds==============="+customTimePeriodIds);
		    			for(int i=0;i<customTimePeriodIds.size();i++){				   
						         
			 					//GenericValue glAccountHistory = delegator.findOne("GlAccountHistory", UtilMisc.toMap("glAccountId", glAccountId,"organizationPartyId","Company","customTimePeriodId",customTimePeriodIds.get(i)),false);
			 					 List andExprs=FastList.newInstance();
			 					 andExprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			 					// andExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccId));
			 					 andExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
			 		    	     andExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodIds.get(i)));
			 		    	     List glPartyWiseHistoryList = delegator.findList("GlAccountHistoryPartyWise", EntityCondition.makeCondition(andExprs,EntityOperator.AND), null, null, null, false);
			 		    	     //Debug.log("glPartyWiseHistoryList==============="+glPartyWiseHistoryList);
			 		    	     if(UtilValidate.isNotEmpty(glPartyWiseHistoryList)){
				 		    	    for(int k=0;k<glPartyWiseHistoryList.size();k++){		 		   			
				 		    	    	Map partyWiseEnty = (Map)glPartyWiseHistoryList.get(k);
				 		    	    	String glAccntId=(String)partyWiseEnty.get("glAccountId");
				 		    	    	String accntTransTypeId=(String)partyWiseEnty.get("acctgTransTypeId");
				 		    	    	
				 		    	    	 BigDecimal debits = BigDecimal.ZERO;
					 					    BigDecimal credits = BigDecimal.ZERO;
		 		    						if(UtilValidate.isNotEmpty(partyWiseEnty.get("postedDebits"))){
					 					    	debits=(BigDecimal) partyWiseEnty.get("postedDebits");
					 					    }
					 					   if(UtilValidate.isNotEmpty(partyWiseEnty.get("postedCredits"))){
					 						   credits=(BigDecimal)partyWiseEnty.get("postedCredits");
					 					   }
				 		    	    	
				 		    	    	
				 		    			 //finAccountTransId=accntDetails.finAccountTransId;
				 		    			GenericValue glAccntDetails = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId",glAccntId), false);
				 		    			
				 		    			 if(UtilValidate.isEmpty(glAccntDetails.get("isControlAcctg"))||(UtilValidate.isNotEmpty(glAccntDetails.get("isControlAcctg"))&&(!"Y".equals(glAccntDetails.get("isControlAcctg"))))){
				 		    				 String glAccountClassId=(String)glAccntDetails.get("glAccountClassId");
				 		    				 if(extTransTypeIdsList1.contains(accntTransTypeId)){
				 		    					 if(glAccntId.equals("121000")||glAccntId.equals("210000")){				 		    						
								 					  postedDebits = postedDebits.add(debits);
								 					   postedCredits = postedCredits.add(credits);
				 		    					 }
				 		    				 }
				 		    				 if(extTransTypeIdsList2.contains(accntTransTypeId)){
				 		    					 if(assetAndLiabilityIdsList.contains(glAccountClassId)){
				 		    						
				 		    						 postedDebits = postedDebits.add(debits);
							 					     postedCredits = postedCredits.add(credits);
				 		    					 }
				 		    				 }
				 		    				 if(accntTransTypeId.equals("JOURNAL")){
				 		    					 if(glAccntId.equals("121000")||glAccntId.equals("210000")){
				 		    						
							 					   postedDebits = postedDebits.add(debits);
							 					   postedCredits = postedCredits.add(credits);
				 		    					 }
				 		    				 }
				 		    				 if(accntTransTypeId.equals("ADJUSTMENT")){
				 		    					if(assetAndLiabilityIdsList.contains(glAccountClassId)){
				 		    						
				 		    						 postedDebits = postedDebits.add(debits);
							 					     postedCredits = postedCredits.add(credits);
				 		    					 }
				 		    				 }
				 		    			 }	    	
				 		    	    	
				 		    	    	/* BigDecimal debits = BigDecimal.ZERO;
				 					    BigDecimal credits = BigDecimal.ZERO;
				 					    if(UtilValidate.isNotEmpty(partyWiseEnty.get("postedDebits"))){
				 					    	debits=(BigDecimal) partyWiseEnty.get("postedDebits");
				 					    }
				 					   if(UtilValidate.isNotEmpty(partyWiseEnty.get("postedCredits"))){
				 						   credits=(BigDecimal)partyWiseEnty.get("postedCredits");
				 					   }*/
				 					   //postedDebits = postedDebits.add(debits);
				 					   //postedCredits = postedCredits.add(credits);
				 		    	    }
			 					}
			 					/*if(UtilValidate.isNotEmpty(glAccountHistory)){
			 						debits=(BigDecimal) glAccountHistory.get("postedDebits");
			 						credits=(BigDecimal)glAccountHistory.get("postedCredits");
			 						postedDebits = postedDebits.add(debits);
			 						postedCredits = postedCredits.add(credits);
				 					//openingBalance =openingBalance.add(debits.subtract(credits));
				 		        } */
				 		}
		    			
		    			/*if(isDebitAccount){
		    				openingBalance = postedDebits.subtract(postedCredits);
		    			}else{
		    				openingBalance = postedCredits.subtract(postedDebits);
		    			}*/
		    			 postedFinalDebits = postedFinalDebits.add(postedDebits);
		    			 postedFinalCredits = postedFinalCredits.add(postedCredits); 
		    			 finalOpeningBalance = postedFinalDebits.subtract(postedFinalCredits);
		    		
	    		//Debug.log("postedFinalDebits=="+postedFinalDebits);
	    		//Debug.log("postedFinalCredits=="+postedFinalCredits);
	    		//Debug.log("finalOpeningBalance=="+finalOpeningBalance);
	    	result.put("postedDebits", postedFinalDebits);
	    	result.put("postedCredits", postedFinalCredits);	
        	result.put("openingBal", finalOpeningBalance);
        	
        } catch (Exception e) {
            Debug.logError(e, "Problem getting openingBal for partyId" + partyId, module);
            return ServiceUtil.returnError("Problem getting openingBal for partyId" + partyId);
        }
        return result;
    }
    
//glAccount opening balance for CostCenter    
    
    public static Map<String, Object> getGlAccountOpeningBalanceForCostCenter(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        String glAccountId = (String) context.get("glAccountId");
        String costCenterId = (String) context.get("costCenterId");
        String segmentId = (String) context.get("segmentId");
        List glAccountIds = (List) context.get("glAccountIds");
        List<String> roBranchList = (List) context.get("roBranchList");
        String organizationPartyId = "Company";
        GenericValue lastClosedTimePeriod=null;
        Timestamp lastClosedDate = null;
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
    	      if(UtilValidate.isNotEmpty(fromDate)){
	    		 fromDate = UtilDateTime.getDayStart(fromDate);
	          }
    	      if(UtilValidate.isNotEmpty(thruDate)){
    	    	 thruDate = UtilDateTime.getDayEnd(thruDate);
	          }
    	      // get lost closed period here
    	      Map lastFinContext = FastMap.newInstance();
    	      lastFinContext.put("periodTypeId","FISCAL_YEAR");
    	      lastFinContext.put("organizationPartyId", organizationPartyId);
    	      lastFinContext.put("userLogin", userLogin);
    	      lastFinContext.put("findDate", UtilDateTime.toSqlDate(fromDate));
    	      //lastFinContext.put("onlyFiscalPeriods", Boolean.FALSE);
			   Map resultCtx = FastMap.newInstance();
				try{
					resultCtx = dispatcher.runSync("findLastClosedDateForPartyLedger", lastFinContext);
					if(ServiceUtil.isError(resultCtx)){
						Debug.logError("Problem in fetching financial year ", module);
						return ServiceUtil.returnError("Problem in fetching financial year ");
					}
					lastClosedTimePeriod = (GenericValue)resultCtx.get("lastClosedTimePeriod");
					lastClosedDate = (Timestamp)resultCtx.get("lastClosedDate");
				}catch(GenericServiceException e){
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				} 
				List customTimePeriodList = FastList.newInstance();
    	      
 			  /*  Map finYearContext = FastMap.newInstance();
 				//finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
 				finYearContext.put("organizationPartyId", organizationPartyId);
 				finYearContext.put("userLogin", userLogin);
 				finYearContext.put("findDate", fromDate);
 				finYearContext.put("excludeNoOrganizationPeriods", "Y");
 				List customTimePeriodList = FastList.newInstance();
 				resultCtx = FastMap.newInstance();
 				try{
 					resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
 					if(ServiceUtil.isError(resultCtx)){
 						Debug.logError("Problem in fetching financial year ", module);
 						return ServiceUtil.returnError("Problem in fetching financial year ");
 					}
 				}catch(GenericServiceException e){
 					Debug.logError(e, module);
 					return ServiceUtil.returnError(e.getMessage());
 				}
 				customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
 				*/String finYearId = "";
 				Timestamp finYearFromDate=null;
 				Timestamp finYearthruDate=null;
 				Timestamp finYearNextFromDate=null;
 				if(UtilValidate.isNotEmpty(lastClosedTimePeriod)){
 					GenericValue customTimePeriod = lastClosedTimePeriod;
 					finYearId = (String)customTimePeriod.get("customTimePeriodId");
 					finYearFromDate=new java.sql.Timestamp(((Date) customTimePeriod.get("fromDate")).getTime());
 					Debug.log("finYearId============"+finYearId);
 					
 				}
    			
 				List extTransTypeIdsList1 = FastList.newInstance();
    			List extTransTypeIdsList2 = FastList.newInstance();
    			List assetAndLiabilityIdsList = FastList.newInstance();
    			 extTransTypeIdsList1=UtilMisc.toList("SALES","SALES_INVOICE","PURCHASE_INVOICE","INVOICE_APPL");
    			 extTransTypeIdsList2=UtilMisc.toList("RECEIPT","PAYMENT_ACCTG_TRANS","PAYMENT_APPL","OUTGOING_PAYMENT","INCOMING_PAYMENT");
    			 extTransTypeIdsList2.add("OB_TB");
    			 extTransTypeIdsList2.add("CAPITALIZATION");
    			 assetAndLiabilityIdsList=UtilMisc.toList("CURRENT_ASSET","LONGTERM_ASSET","CURRENT_LIABILITY","LONGTERM_LIABILITY","CASH_EQUIVALENT");
    			 BigDecimal finalOpeningBalance = BigDecimal.ZERO;
     			BigDecimal postedFinalDebits = BigDecimal.ZERO;
     			BigDecimal postedFinalCredits = BigDecimal.ZERO; 
     			BigDecimal postedDebits = BigDecimal.ZERO;
     			BigDecimal postedCredits = BigDecimal.ZERO; 
 	    		BigDecimal openingBalance = BigDecimal.ZERO;
 				List customTimePeriodIds =FastList.newInstance();
    			try {      
    			//List conditionList = UtilMisc.toList(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
    				   List conditionList =FastList.newInstance();
    			       conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_MONTH"));
    			       conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
    			       if(UtilValidate.isNotEmpty(lastClosedDate)){
    			    	   conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(lastClosedDate)));
    			       }
    			      
    			       conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN ,UtilDateTime.toSqlDate(fromDate)));
    			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			List<String> orderBy = UtilMisc.toList("-thruDate");
    			customTimePeriodList = delegator.findList("CustomTimePeriod", condition, UtilMisc.toSet("customTimePeriodId"), orderBy, null, true);
    			
    			customTimePeriodIds = EntityUtil.getFieldListFromEntityList(customTimePeriodList, "customTimePeriodId", true);
    			Timestamp nextDayStartDayEnd =null;
    			Timestamp fromDatePreviousDayEnd=null;
    			if(UtilValidate.isNotEmpty(customTimePeriodIds)){
    				String lastCustPeriodId=(String)customTimePeriodIds.get(0);    				
    				GenericValue lastCustPeriodDetails = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", lastCustPeriodId), false);
    	    	     Timestamp lastCustPeriodThru = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(lastCustPeriodDetails.getDate("thruDate")));
    	    	      nextDayStartDayEnd = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(lastCustPeriodThru, 1));
    	    	    
    	    	      fromDatePreviousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
    	    	    
    			}else{
    				nextDayStartDayEnd = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(lastClosedDate, 1));
    				fromDatePreviousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
    			}
	    			if(UtilValidate.isNotEmpty(nextDayStartDayEnd)&&UtilValidate.isNotEmpty(fromDatePreviousDayEnd)){
	    				//getting betweent date entries
	    				 /*List acctgRoleExp = FastList.newInstance();
	    	    	     acctgRoleExp.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
	    	    	     acctgRoleExp.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, costCenterId));
	    	    	     EntityCondition andCondRole = EntityCondition.makeCondition(acctgRoleExp, EntityOperator.AND);
	    	    	     EntityListIterator acctngTransRoleItr  = delegator.find("AcctgTransRole",andCondRole,null, null, null, null);
	    	    	     List acctgTrnsList=FastList.newInstance();
	    	    	     if(acctngTransRoleItr != null){
	    	    	    	 acctgTrnsList = EntityUtil.getFieldListFromEntityListIterator(acctngTransRoleItr,"acctgTransId", false);
	    	    	     }	 
	    	    	     acctngTransRoleItr.close();*/
	    	    	     List andExprs1 = FastList.newInstance();
	    	    	     if(UtilValidate.isEmpty(organizationPartyId)){
	    	    	    	 organizationPartyId = "Company";
	    	    	     }
	    	    	     andExprs1.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
	    	    	     if(UtilValidate.isNotEmpty(costCenterId)){
	    	    	    	 andExprs1.add(EntityCondition.makeCondition("costCenterId", EntityOperator.EQUALS, costCenterId));
	    	    	     }
	    	    	     if(UtilValidate.isNotEmpty(roBranchList)){
	    	    	    	 andExprs1.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,roBranchList));
	    	    	     }
	    	    	     if(UtilValidate.isNotEmpty(segmentId)){
	    	    	    	 if(segmentId.equals("YARN_SALE")){
	    	    	    		 andExprs1.add(EntityCondition.makeCondition("purposeTypeId",EntityOperator.IN,UtilMisc.toList("YARN_SALE","DEPOT_YARN_SALE")));
	    	    	    	 }else{
	    	    	    		 andExprs1.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, segmentId));
	    	    	    	 }
	    	    	     }
	    	    	     if(UtilValidate.isNotEmpty(organizationPartyId)){
	    	    	    	 //get internal orgs and do in query here
	    	    	    	 andExprs1.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
	    	    	     }   	    	     
	    	    	    	    	    	     
	    	    	     andExprs1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, nextDayStartDayEnd));
	    	    	     andExprs1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDatePreviousDayEnd));
	    	    	     EntityCondition andCond1 = EntityCondition.makeCondition(andExprs1, EntityOperator.AND);
	    	    	     
	    	    	     EntityListIterator allPostedTransactionTotalItr1 = delegator.find("AcctgTransAndEntries", andCond1, null, null, null, null);
	    	    	     GenericValue transTotalEntry1;
	    	    	     
	    	    	     while(allPostedTransactionTotalItr1 != null && (transTotalEntry1 = allPostedTransactionTotalItr1.next()) != null) {
	    	    	    	// Debug.log("transTotalEntry1======="+transTotalEntry1);
	    	    	    	 String glAcntId = transTotalEntry1.getString("glAccountId");
	    	    	    	 String acctgTransTypeId = transTotalEntry1.getString("acctgTransTypeId");
	    	    	    	 String flag=transTotalEntry1.getString("debitCreditFlag");
	    	    	    	 BigDecimal amtVal=BigDecimal.ZERO;
	    	    	    	 if(UtilValidate.isNotEmpty(transTotalEntry1.getBigDecimal("amount"))){
	    	    	    		 amtVal=transTotalEntry1.getBigDecimal("amount");
	    	    	    	 }
	    	    	    	 GenericValue glAccntDetails = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId",glAcntId), false);
		 		    			
	 		    			 if(UtilValidate.isEmpty(glAccntDetails.get("isControlAcctg"))||(UtilValidate.isNotEmpty(glAccntDetails.get("isControlAcctg"))&&(!"Y".equals(glAccntDetails.get("isControlAcctg"))))){
	 		    				String glAccountClassId=(String)glAccntDetails.get("glAccountClassId");
	 		    				 if(extTransTypeIdsList1.contains(acctgTransTypeId)){
	 		    					 //if(glAcntId.equals("120000")||glAcntId.equals("211000")){	
	 		    					if("ACCOUNTS_PAYABLE".equals(glAccntDetails.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccntDetails.get("glAccountTypeId"))){
	 		    						if(flag.equals("D")){
					 					  postedDebits = postedDebits.add(amtVal);
	 		    						}else{
					 					   postedCredits = postedCredits.add(amtVal);
	 		    						}
	 		    					 }
	 		    				 }
	 		    				 if(extTransTypeIdsList2.contains(acctgTransTypeId)){
	 		    					 if(assetAndLiabilityIdsList.contains(glAccountClassId)){
	 		    						if(flag.equals("D")){
						 					  postedDebits = postedDebits.add(amtVal);
		 		    						}else{
						 					   postedCredits = postedCredits.add(amtVal);
		 		    						}
	 		    					 }
	 		    				 }
	 		    				 if(acctgTransTypeId.equals("JOURNAL")){
	 		    					// if(glAcntId.equals("120000")||glAcntId.equals("211000")){
	 		    					if("ACCOUNTS_PAYABLE".equals(glAccntDetails.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccntDetails.get("glAccountTypeId"))){
	 		    						if(flag.equals("D")){
						 				   postedDebits = postedDebits.add(amtVal);
	 		    						}else{
					 					   postedCredits = postedCredits.add(amtVal);
	 		    						}
	 		    					 }
	 		    				 }
	 		    				 if(acctgTransTypeId.equals("ADJUSTMENT")){
	 		    					if(assetAndLiabilityIdsList.contains(glAccountClassId)){
	 		    						if(flag.equals("D")){
							 				   postedDebits = postedDebits.add(amtVal);
	 		    						}else{
					 					   postedCredits = postedCredits.add(amtVal);
	 		    						}
	 		    					 }
	 		    				 }
	 		    			 }   	    	 
	    	    	     }
	    	    	     allPostedTransactionTotalItr1.close();
	    			}
    			}catch(Exception e){
 					Debug.logError(e, module);
 					return ServiceUtil.returnError(e.getMessage());
 				}    		
    					Map lastClosedGlBalances = FastMap.newInstance();
		    			if(UtilValidate.isNotEmpty(lastClosedTimePeriod)){
		    				if(UtilValidate.isNotEmpty(costCenterId)){
		    					lastClosedGlBalances = UtilAccounting.getLastClosedGlBalanceForCostCenter(ctx, UtilMisc.toMap("organizationPartyId", organizationPartyId,"customTimePeriodId",finYearId,"costCenterId",costCenterId,"segmentId",segmentId,"glAccountId",glAccountId));
		    				}
		    				else{
		    					lastClosedGlBalances = UtilAccounting.getLastClosedGlBalanceForCostCenter(ctx, UtilMisc.toMap("organizationPartyId", organizationPartyId,"customTimePeriodId",finYearId, "roBranchList", roBranchList,"segmentId",segmentId,"glAccountId",glAccountId));
		    				}
		    				List lastClosedGlBalanceList = (List)lastClosedGlBalances.get("openingGlHistory");
		    				if(UtilValidate.isNotEmpty(lastClosedGlBalanceList)){
		    					for(int l=0;l<lastClosedGlBalanceList.size();l++){
		    						Map lastClosedPartyGlBal=(Map) lastClosedGlBalanceList.get(l);
		    						String glAcId=(String)lastClosedPartyGlBal.get("glAccountId");
		    						String clsGlClassId=(String)lastClosedPartyGlBal.get("glAccountClassId");
		    						String clsAcctgTransTypeId=(String)lastClosedPartyGlBal.get("acctgTransTypeId");
		    						GenericValue glAccount = delegator.findOne("GlAccount",UtilMisc.toMap("glAccountId", glAcId), false);
		    						
		    						BigDecimal openBal = (BigDecimal)lastClosedPartyGlBal.get("totalEndingBalance");
		    						if(UtilValidate.isEmpty(glAccount.get("isControlAcctg"))||(UtilValidate.isNotEmpty(glAccount.get("isControlAcctg"))&&(!"Y".equals(glAccount.get("isControlAcctg"))))){
		    							if(UtilValidate.isNotEmpty(openBal)){
			    							boolean isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
				    		    			if (isDebitAccount) {
				    		    				if(extTransTypeIdsList1.contains(clsAcctgTransTypeId)){
					 		    					 //if(glAcId.equals("120000")||glAcId.equals("211000")){	
					 		    					   if("ACCOUNTS_PAYABLE".equals(glAccount.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccount.get("glAccountTypeId"))){
						    		    				if(openBal.compareTo(BigDecimal.ZERO)<0){
						    		    					postedCredits = postedCredits.add(openBal.negate());
						    		    				}else {
						    		    					postedDebits = postedDebits.add(openBal);
						    		    				}
					 		    					 }
				    		    				}
				    		    				if(extTransTypeIdsList2.contains(clsAcctgTransTypeId)){
					 		    					 if(assetAndLiabilityIdsList.contains(clsGlClassId)){
					 		    						if(openBal.compareTo(BigDecimal.ZERO)<0){
						    		    					postedCredits = postedCredits.add(openBal.negate());
						    		    				}else {
						    		    					postedDebits = postedDebits.add(openBal);
						    		    				}
					 		    					 }
				    		    				}
				    		    				 if(clsAcctgTransTypeId.equals("JOURNAL")){
					 		    					 //if(glAcId.equals("120000")||glAcId.equals("211000")){
					 		    					 if("ACCOUNTS_PAYABLE".equals(glAccount.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccount.get("glAccountTypeId"))){
					 		    						if(openBal.compareTo(BigDecimal.ZERO)<0){
						    		    					postedCredits = postedCredits.add(openBal.negate());
						    		    					
						    		    				}else {
						    		    					postedDebits = postedDebits.add(openBal);						    		    					
						    		    				}
					 		    					 }
				    		    				 }
				    		    				 if(clsAcctgTransTypeId.equals("ADJUSTMENT")){
						 		    					if(assetAndLiabilityIdsList.contains(clsGlClassId)){
						 		    						if(openBal.compareTo(BigDecimal.ZERO)<0){
							    		    					postedCredits = postedCredits.add(openBal.negate());
							    		    				}else {
							    		    					postedDebits = postedDebits.add(openBal);
							    		    				}
						 		    					}
				    		    				 }
				    		    				
				    		    				
				    		    			}else {
				    		    				
				    		    				
				    		    				if(extTransTypeIdsList1.contains(clsAcctgTransTypeId)){
					 		    					 //if(glAcId.equals("120000")||glAcId.equals("211000")){	
					 		    					if("ACCOUNTS_PAYABLE".equals(glAccount.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccount.get("glAccountTypeId"))){
					 		    						if (openBal.compareTo(BigDecimal.ZERO)<0) {
						    		    					postedDebits = postedDebits.add(openBal.negate());
						    		    				} else {
						    		    					postedCredits = postedCredits.add(openBal);
						    		    				}
					 		    					 }
				    		    				}
				    		    				if(extTransTypeIdsList2.contains(clsAcctgTransTypeId)){
					 		    					 if(assetAndLiabilityIdsList.contains(clsGlClassId)){
					 		    						if (openBal.compareTo(BigDecimal.ZERO)<0) {
						    		    					postedDebits = postedDebits.add(openBal.negate());
						    		    				} else {
						    		    					postedCredits = postedCredits.add(openBal);
						    		    				}
					 		    					 }
				    		    				}
				    		    				 if(clsAcctgTransTypeId.equals("JOURNAL")){
					 		    					// if(glAcId.equals("120000")||glAcId.equals("211000")){
				    		    					 if("ACCOUNTS_PAYABLE".equals(glAccount.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccount.get("glAccountTypeId"))){
					 		    						if (openBal.compareTo(BigDecimal.ZERO)<0) {
						    		    					postedDebits = postedDebits.add(openBal.negate());
						    		    				} else {
						    		    					postedCredits = postedCredits.add(openBal);
						    		    				}
					 		    					 }
				    		    				 }
				    		    				 if(clsAcctgTransTypeId.equals("ADJUSTMENT")){
				    		    					 if (openBal.compareTo(BigDecimal.ZERO)<0) {
						    		    					postedDebits = postedDebits.add(openBal.negate());
						    		    				} else {
						    		    					postedCredits = postedCredits.add(openBal);
						    		    				}
				    		    				 }  				
				    		    				
				    		    			}
				    		    			
			    						}
		    						}
		    					}
		    					
		    				}
		    				
		    			}
		    			for(int i=0;i<customTimePeriodIds.size();i++){				   
						    
			 					//GenericValue glAccountHistory = delegator.findOne("GlAccountHistory", UtilMisc.toMap("glAccountId", glAccountId,"organizationPartyId","Company","customTimePeriodId",customTimePeriodIds.get(i)),false);
			 					 List andExprs=FastList.newInstance();
			 					 if(UtilValidate.isNotEmpty(costCenterId)){
			 					 andExprs.add(EntityCondition.makeCondition("costCenterId", EntityOperator.EQUALS, costCenterId));
			 					 }
			 					if(UtilValidate.isNotEmpty(segmentId)){
			 						if(segmentId.equals("YARN_SALE")){
			 							andExprs.add(EntityCondition.makeCondition("segmentId",EntityOperator.IN,UtilMisc.toList("YARN_SALE","DEPOT_YARN_SALE")));
			 		    			}
			 		    			else{
			 		    				andExprs.add(EntityCondition.makeCondition("segmentId", EntityOperator.EQUALS, segmentId));
			 		    			}
			 					}
			 					if(roBranchList!=null){
			 						andExprs.add(EntityCondition.makeCondition("costCenterId", EntityOperator.IN, roBranchList));
			 					}
			 					if(glAccountId!=null){
			 						andExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId)); 
			 					}
			 					 andExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
			 		    	     andExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodIds.get(i)));
			 		    	     List glPartyWiseHistoryList = delegator.findList("GlAccountHistoryPartyWise", EntityCondition.makeCondition(andExprs,EntityOperator.AND), null, null, null, false);
			 					if(UtilValidate.isNotEmpty(glPartyWiseHistoryList)){
				 		    	    for(int k=0;k<glPartyWiseHistoryList.size();k++){		 		   			
				 		    	    	Map partyWiseEnty = (Map)glPartyWiseHistoryList.get(k);
				 		    	    	String glAccntId=(String)partyWiseEnty.get("glAccountId");
				 		    	    	String accntTransTypeId=(String)partyWiseEnty.get("acctgTransTypeId");
				 		    	    	
				 		    	    	 BigDecimal debits = BigDecimal.ZERO;
					 					    BigDecimal credits = BigDecimal.ZERO;
		 		    						if(UtilValidate.isNotEmpty(partyWiseEnty.get("postedDebits"))){
					 					    	debits=(BigDecimal) partyWiseEnty.get("postedDebits");
					 					    }
					 					   if(UtilValidate.isNotEmpty(partyWiseEnty.get("postedCredits"))){
					 						   credits=(BigDecimal)partyWiseEnty.get("postedCredits");
					 					   }
				 		    	    	
				 		    	    	
				 		    			 //finAccountTransId=accntDetails.finAccountTransId;
				 		    			GenericValue glAccntDetails = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId",glAccntId), false);
				 		    			
				 		    			 if(UtilValidate.isEmpty(glAccntDetails.get("isControlAcctg"))||(UtilValidate.isNotEmpty(glAccntDetails.get("isControlAcctg"))&&(!"Y".equals(glAccntDetails.get("isControlAcctg"))))){
				 		    				 String glAccountClassId=(String)glAccntDetails.get("glAccountClassId");
				 		    				 if(extTransTypeIdsList1.contains(accntTransTypeId)){
				 		    					// if(glAccntId.equals("120000")||glAccntId.equals("211000")){	
				 		    					if("ACCOUNTS_PAYABLE".equals(glAccntDetails.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccntDetails.get("glAccountTypeId"))){	 
								 					  postedDebits = postedDebits.add(debits);
								 					   postedCredits = postedCredits.add(credits);
				 		    					 }
				 		    				 }
				 		    				 if(extTransTypeIdsList2.contains(accntTransTypeId)){
				 		    					 if(assetAndLiabilityIdsList.contains(glAccountClassId)){
				 		    						
				 		    						 postedDebits = postedDebits.add(debits);
							 					     postedCredits = postedCredits.add(credits);
				 		    					 }
				 		    				 }
				 		    				 if(accntTransTypeId.equals("JOURNAL")){
				 		    					// if(glAccntId.equals("120000")||glAccntId.equals("211000")){
				 		    					if("ACCOUNTS_PAYABLE".equals(glAccntDetails.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccntDetails.get("glAccountTypeId"))){	
							 					   postedDebits = postedDebits.add(debits);
							 					   postedCredits = postedCredits.add(credits);
				 		    					 }
				 		    				 }
				 		    				 if(accntTransTypeId.equals("ADJUSTMENT")){
				 		    					if(assetAndLiabilityIdsList.contains(glAccountClassId)){
				 		    						
				 		    						 postedDebits = postedDebits.add(debits);
							 					     postedCredits = postedCredits.add(credits);
				 		    					 }
				 		    				 }
				 		    			 }	    	
				 		    	    	/* BigDecimal debits = BigDecimal.ZERO;
				 					    BigDecimal credits = BigDecimal.ZERO;
				 					    if(UtilValidate.isNotEmpty(partyWiseEnty.get("postedDebits"))){
				 					    	debits=(BigDecimal) partyWiseEnty.get("postedDebits");
				 					    }
				 					   if(UtilValidate.isNotEmpty(partyWiseEnty.get("postedCredits"))){
				 						   credits=(BigDecimal)partyWiseEnty.get("postedCredits");
				 					   }*/
				 					   //postedDebits = postedDebits.add(debits);
				 					   //postedCredits = postedCredits.add(credits);
				 		    	    }
			 					}
			 					/*if(UtilValidate.isNotEmpty(glAccountHistory)){
			 						debits=(BigDecimal) glAccountHistory.get("postedDebits");
			 						credits=(BigDecimal)glAccountHistory.get("postedCredits");
			 						postedDebits = postedDebits.add(debits);
			 						postedCredits = postedCredits.add(credits);
				 					//openingBalance =openingBalance.add(debits.subtract(credits));
				 		        } */
				 		}
		    			
		    			/*if(isDebitAccount){
		    				openingBalance = postedDebits.subtract(postedCredits);
		    			}else{
		    				openingBalance = postedCredits.subtract(postedDebits);
		    			}*/
		    			 postedFinalDebits = postedFinalDebits.add(postedDebits);
		    			 postedFinalCredits = postedFinalCredits.add(postedCredits); 
		    			 finalOpeningBalance = postedFinalDebits.subtract(postedFinalCredits);
		    		
	    		Debug.log("postedFinalDebits=="+postedFinalDebits);
	    		Debug.log("postedFinalCredits=="+postedFinalCredits);
	    		Debug.log("finalOpeningBalance=="+finalOpeningBalance);
	    	result.put("postedDebits", postedFinalDebits);
	    	result.put("postedCredits", postedFinalCredits);	
        	result.put("openingBal", finalOpeningBalance);
        	
        } catch (Exception e) {
            Debug.logError(e, "Problem getting openingBal for costCenterId" + costCenterId, module);
            return ServiceUtil.returnError("Problem getting openingBal for costCenterId" + costCenterId);
        }
        return result;
    }
    //reCalculateGlHistoryForPeriod
    public static Map<String, Object> reCalculateGlHistoryForPeriod(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
	    Delegator delegator = ctx.getDelegator();
	    LocalDispatcher dispatcher = ctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    String customTimePeriodId = (String) context.get("customTimePeriodId");
	    String organizationPartyId = (String)context.get("organizationPartyId");
	    String glFiscalTypeId = (String)context.get("glFiscalTypeId");
	    String partyWiseFlag = (String)context.get("partyWiseFlag");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    try {
	    	 GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",customTimePeriodId), false);
		     //emtpy or not a fiscal period then return
	    	 if(UtilValidate.isNotEmpty(customTimePeriod.getString("organizationPartyId"))){
	    		 organizationPartyId =  customTimePeriod.getString("organizationPartyId");
	    	 }
	    	 
	    	 Map finYearContext = FastMap.newInstance();
	 		finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList(customTimePeriod.getString("periodTypeId")));
	 		finYearContext.put("organizationPartyId", organizationPartyId);
	 		finYearContext.put("userLogin", userLogin);
	 		finYearContext.put("findDate", UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
	 		finYearContext.put("excludeNoOrganizationPeriods", "Y");
	 		List<GenericValue> customTimePeriodList = FastList.newInstance();
	 		Map resultCtx = FastMap.newInstance();
	 		try{
	 			resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
	 			if(ServiceUtil.isError(resultCtx)){
	 				Debug.logError("Problem in fetching financial year ", module);
	 				return ServiceUtil.returnError("Problem in fetching financial year ");
	 			}
	 		}catch(GenericServiceException e){
	 			Debug.logError(e, module);
	 			return ServiceUtil.returnError(e.getMessage());
	 		}
	 		customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
	 		
	    	 
		     Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
		     Timestamp thruDate = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
				
		     if((UtilValidate.isEmpty(partyWiseFlag))||(UtilValidate.isNotEmpty(partyWiseFlag)&&!"Y".equals(partyWiseFlag))){
		    	 if(UtilValidate.isEmpty(customTimePeriod) || !(customTimePeriod.getString("periodTypeId").contains("FISCAL")) || (customTimePeriod.getString("isClosed").equals("Y"))){
	    	    	 return result;
	    	     }
	    	     List andExprs = FastList.newInstance();
	    	     if(UtilValidate.isEmpty(organizationPartyId)){
	    	    	 organizationPartyId = "Company";
	    	     }
	    	     andExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
	    	     if(UtilValidate.isNotEmpty(organizationPartyId)){
	    	    	 //get internal orgs and do in query here
	    	    	 andExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
	    	     }
	    	     
	    	     if(UtilValidate.isNotEmpty(glFiscalTypeId)){
	    	    	 andExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
	    	     }
	    	     
	    	     andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	    	     andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	    	     EntityCondition andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
	    	     Debug.log("andCond======="+andCond);
	    	     EntityListIterator allPostedTransactionTotalItr = delegator.find("AcctgTransEntrySums", andCond, null, null, null, null);
	    	     GenericValue transTotalEntry;
	    	     Map<String ,Object> postedTransactionTotalsMap = FastMap.newInstance();
	    	     
	    	     while(allPostedTransactionTotalItr != null && (transTotalEntry = allPostedTransactionTotalItr.next()) != null) {
	    	    	 String glAccountId = transTotalEntry.getString("glAccountId");
	    	    	 String costCenterId = transTotalEntry.getString("costCenterId");
	    	    	 String purposeTypeId = transTotalEntry.getString("purposeTypeId");
	    	    	 Map accountMap = (Map)postedTransactionTotalsMap.get(transTotalEntry.getString("glAccountId"));
	    	    	 if (UtilValidate.isEmpty(accountMap)) {
	    	    		 accountMap = FastMap.newInstance();
	    	    		 accountMap.put("glAccountId", glAccountId);
	    	    		 accountMap.put("purposeTypeId", purposeTypeId);
	    	    		 accountMap.put("costCenterId", costCenterId);
		                 accountMap.put("D", BigDecimal.ZERO);
		                 accountMap.put("C", BigDecimal.ZERO);
		             }
	    	    	 
	    	         UtilMisc.addToBigDecimalInMap(accountMap , transTotalEntry.getString("debitCreditFlag"), transTotalEntry.getBigDecimal("amount"));
	    	         postedTransactionTotalsMap.put(glAccountId, accountMap);
	    	     }
	    	     allPostedTransactionTotalItr.close();
	    	     andExprs.clear();
	    	     andExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
	    	     andExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
	    	     List glHistoryList = delegator.findList("GlAccountHistory", EntityCondition.makeCondition(andExprs,EntityOperator.AND), null, null, null, false);
	    	     delegator.removeAll(glHistoryList);
	    	     for ( Map.Entry<String, Object> entry : postedTransactionTotalsMap.entrySet() ) {
	    	    	  Map postedTotalEntry = (Map)entry.getValue();
	    	    	 GenericValue glAccountHistory = delegator.makeValue("GlAccountHistory");
	    	    	 glAccountHistory.set("glAccountId", (String)postedTotalEntry.get("glAccountId"));
	    	    	 glAccountHistory.set("costCenterId", (String)postedTotalEntry.get("costCenterId"));
	    	    	 glAccountHistory.set("segmentId", (String)postedTotalEntry.get("purposeTypeId"));
	    	    	 glAccountHistory.set("organizationPartyId",organizationPartyId);
	    	    	 glAccountHistory.set("customTimePeriodId",customTimePeriodId);
	    	    	 glAccountHistory.set("postedDebits",(BigDecimal)postedTotalEntry.get("D") );
	    	    	 glAccountHistory.set("postedCredits", (BigDecimal)postedTotalEntry.get("C"));
	    	    	 delegator.createOrStore(glAccountHistory);
	    	    	 //Debug.log("glAccountHistory============="+glAccountHistory);
	    	    	 
	    	    	 
	    	    	 
	    	    	 //Let's populate child org histories
	        	     for(GenericValue cstTimePeriod : customTimePeriodList){
	        	    	 
	        	    	 if(!customTimePeriodId.equals(cstTimePeriod.getString("customTimePeriodId"))){
	        	    		 Map glMap = UtilMisc.toMap("customTimePeriodId",cstTimePeriod.getString("customTimePeriodId"),
		        	    			 "glAccountId",(String)postedTotalEntry.get("glAccountId"),"organizationPartyId",organizationPartyId,"costCenterId",(String)postedTotalEntry.get("costCenterId"),"segmentId",(String)postedTotalEntry.get("purposeTypeId"));
		        	    	 GenericValue parentGlAccountHistory = delegator.findOne("GlAccountHistory",glMap , false);
		        	    	 if(UtilValidate.isEmpty(parentGlAccountHistory)){
		        	    		 GenericValue newEntity = delegator.makeValue("GlAccountHistory");
		        	    		 newEntity.putAll(glMap);
		        	    		 newEntity.set("postedDebits",BigDecimal.ZERO );
		        	    		 newEntity.set("postedCredits", BigDecimal.ZERO);
		    	    	    	 delegator.createOrStore(newEntity);
		    	    	    	 parentGlAccountHistory = delegator.findOne("GlAccountHistory",glMap , false);
		        	    	 }
		        	    	 parentGlAccountHistory.set("postedDebits",parentGlAccountHistory.getBigDecimal("postedDebits").add((BigDecimal)postedTotalEntry.get("D")));
		        	    	 parentGlAccountHistory.set("postedCredits", parentGlAccountHistory.getBigDecimal("postedCredits").add((BigDecimal)postedTotalEntry.get("C")));
			    	    	 delegator.createOrStore(parentGlAccountHistory);
			    	    	 
	        	    	 }
	        	    	 
	        	     }
	    	     }	 
		     }
		   
		    
		     
		     //Lets Papulate GlAccountHistoryPartyWise entity	     	     
		     
		     List andExprs1 = FastList.newInstance();
		     if(UtilValidate.isEmpty(organizationPartyId)){
		    	 organizationPartyId = "Company";
		     }
		     andExprs1.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
		     if(UtilValidate.isNotEmpty(organizationPartyId)){
		    	 //get internal orgs and do in query here
		    	 andExprs1.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
		     }
		     
		     if(UtilValidate.isNotEmpty(glFiscalTypeId)){
		    	 andExprs1.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
		     }
		     
		     andExprs1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		     andExprs1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		     EntityCondition andCond1 = EntityCondition.makeCondition(andExprs1, EntityOperator.AND);
		     //Debug.log("andCond======="+andCond);
		     EntityListIterator allPostedTransactionTotalItr1 = delegator.find("AcctgTransAndEntries", andCond1, null, null, null, null);
		     GenericValue transTotalEntry1;
		     Map postedTransactionTotalsMap1 = FastMap.newInstance();
		     
		     while(allPostedTransactionTotalItr1 != null && (transTotalEntry1 = allPostedTransactionTotalItr1.next()) != null) {
		    	 String glAccountId = transTotalEntry1.getString("glAccountId");
		    	 String acctgTransTypeId = transTotalEntry1.getString("acctgTransTypeId");
		    	 String partyId=transTotalEntry1.getString("partyId");
		    	 String acctgTransId=transTotalEntry1.getString("acctgTransId");
		    	 String costCenterId=transTotalEntry1.getString("costCenterId");
		    	 String segmentId=transTotalEntry1.getString("purposeTypeId");
		    	/* //getting acctgTransRole
		    	 List acctgRoleExp = FastList.newInstance();
	    	     acctgRoleExp.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
	    	     acctgRoleExp.add(EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS, acctgTransId));
	    	     EntityCondition andCondRole = EntityCondition.makeCondition(acctgRoleExp, EntityOperator.AND);
	    	     EntityListIterator acctngTransRoleItr  = delegator.find("AcctgTransRole",andCondRole,null, null, null, null);
	    	     List acctgTrnsList=FastList.newInstance();
	    	     String costCenterId=null;
	    	     if(acctngTransRoleItr != null){
	    	    	 //acctgTrnsList = EntityUtil.getFieldListFromEntityListIterator(acctngTransRoleItr,"partyId", false);
	    	    	 acctgTrnsList = acctngTransRoleItr.getCompleteList();
	    	    	  GenericValue trnsRole = EntityUtil.getFirst(acctgTrnsList);
	    	    	  costCenterId=(String)trnsRole.get("costCenterId");  	    	  
	    	     }*/
	    	     Debug.log("costCenterId==========="+costCenterId);
		    	 String flag=transTotalEntry1.getString("debitCreditFlag");
		    	 BigDecimal amtVal=BigDecimal.ZERO;
		    	 if(UtilValidate.isNotEmpty(transTotalEntry1.getBigDecimal("amount"))){
		    		 amtVal=transTotalEntry1.getBigDecimal("amount");
		    	 }
		    	 if(UtilValidate.isNotEmpty(partyId)&&UtilValidate.isNotEmpty(costCenterId)){
	    	    	 if(UtilValidate.isEmpty(postedTransactionTotalsMap1.get(partyId))){
	    	    		 Map tempMapGlMap=FastMap.newInstance();
	    	    		 Map tempMapTransTypeMap=FastMap.newInstance();
	    	    		 Map tempMapTransCostMap=FastMap.newInstance();
	    	    		 Map tempMapTransSegMap=FastMap.newInstance();
	    	    		 Map tempMapDetails=FastMap.newInstance();
	    	    		 	if(flag.equals("D")){
	    	    		 		tempMapDetails.put("D", amtVal);
	    	    		 	}else{
	    	    		 		tempMapDetails.put("C", amtVal);
	    	    		 	}
	    	    		 	tempMapTransSegMap.put(segmentId,tempMapDetails);
	    	    		 	tempMapTransCostMap.put(costCenterId, tempMapTransSegMap);
	    	    		 	tempMapTransTypeMap.put(acctgTransTypeId, tempMapTransCostMap);
	    	    		 	tempMapGlMap.put(glAccountId, tempMapTransTypeMap);
	    	    		 	postedTransactionTotalsMap1.put(partyId, tempMapGlMap);
	    	    	 }else{
	    	    		 Map tempGlWiseDetailsMap=FastMap.newInstance();
	    	    		 	 tempGlWiseDetailsMap.putAll((Map)postedTransactionTotalsMap1.get(partyId));
	    	    		 	 if(UtilValidate.isEmpty(tempGlWiseDetailsMap.get(glAccountId))){
	    	    		 		 Map tempGlWiseMap=FastMap.newInstance();
	    	    		 		 Map tempTrnsCostWiseMap=FastMap.newInstance();
	    	    		 		Map tempTrnsSegWiseMap=FastMap.newInstance();
	    	    		 		 Map tempTrnsTypeWiseMap=FastMap.newInstance();
	    	    		 		 Map tempGlDetailsWise=FastMap.newInstance();
	    	    		 		if(flag.equals("D")){
	    	    		 			tempGlDetailsWise.put("D", amtVal);
	        	    		 	}else{
	        	    		 		tempGlDetailsWise.put("C", amtVal);
	        	    		 	}
	    	    		 		tempTrnsSegWiseMap.put(segmentId,tempGlDetailsWise);
	    	    		 		tempTrnsCostWiseMap.put(costCenterId, tempTrnsSegWiseMap);
	    	    		 		tempTrnsTypeWiseMap.put(acctgTransTypeId, tempTrnsCostWiseMap);
	    	    		 		tempGlWiseDetailsMap.put(glAccountId, tempTrnsTypeWiseMap);    	    		 		 
	    	    		 	 }else{
	    	    		 		 Map glWiseDetailsMap=FastMap.newInstance();
	    	    		 		 glWiseDetailsMap.putAll((Map)tempGlWiseDetailsMap.get(glAccountId));
	    	    		 		 if(UtilValidate.isEmpty(glWiseDetailsMap.get(acctgTransTypeId))){
	    	    		 			 Map tempTransType=FastMap.newInstance();
	    	    		 			 Map tempTransCost=FastMap.newInstance();
	    	    		 			Map tempTransSeg=FastMap.newInstance();
		    	    		 			if(flag.equals("D")){
		    	    		 				tempTransType.put("D", amtVal);
		        	    		 		 }else{
		        	    		 			tempTransType.put("C", amtVal);
		        	    		 		 }
		    	    		 			tempTransSeg.put(segmentId,tempTransType);
		    	    		 			tempTransCost.put(costCenterId, tempTransSeg);
		    	    		 			glWiseDetailsMap.put(acctgTransTypeId,tempTransCost);
	    	    		 		 }else{
	    	    		 			 Map  accntTransTypeMap=FastMap.newInstance();
	    	    		 			 accntTransTypeMap.putAll((Map)glWiseDetailsMap.get(acctgTransTypeId));    	    		 			 
	        	    		 		 
	    	    		 		     if(UtilValidate.isEmpty(accntTransTypeMap.get(costCenterId))){
	    	    		 		    	Map tempCostMap=FastMap.newInstance();
	    	    		 		    	Map tempSegMap=FastMap.newInstance();
	    	    		 		    	if(flag.equals("D")){
	    	    		 		    		tempCostMap.put("D", amtVal);
		        	    		 		 }else{
		        	    		 			tempCostMap.put("C", amtVal);
		        	    		 		 }
	    	    		 		    	tempSegMap.put(segmentId,tempCostMap);
	    	    		 		    	accntTransTypeMap.put(costCenterId, tempSegMap);
	    	    		 		    	//glWiseDetailsMap.put(acctgTransTypeId, accntTransTypeMap);
	    	    		 		     }else{
	    	    		 		    	 Map acctCostCodeMap=FastMap.newInstance();
	    	    		 		    	Map acctSegMap=FastMap.newInstance();
	    	    		 		    	 acctCostCodeMap.putAll((Map)accntTransTypeMap.get(costCenterId));	    	    		 		    	
		        	    		 		 
		        	    		 		 if(UtilValidate.isEmpty(acctCostCodeMap.get(segmentId))){
		        	    		 			 Map tempSeg=FastMap.newInstance();
		        	    		 			if(flag.equals("D")){
		        	    		 				tempSeg.put("D", amtVal);
			        	    		 		 }else{
			        	    		 			tempSeg.put("C", amtVal);
			        	    		 		 }
		        	    		 			acctCostCodeMap.put(segmentId,tempSeg);
		        	    		 			accntTransTypeMap.put(costCenterId,acctCostCodeMap);
		        	    		 		 }else{
		        	    		 			 Map segMap=FastMap.newInstance();
		        	    		 			 	segMap.putAll((Map)acctCostCodeMap.get(segmentId));
		        	    		 			 	 BigDecimal glDebitAmt=BigDecimal.ZERO;	        	    		 		
				        	    		 		 BigDecimal glCrAmt=BigDecimal.ZERO;
		        	    		 			if(UtilValidate.isNotEmpty(segMap.get("D"))){
		 	        	    		 			glDebitAmt=(BigDecimal)segMap.get("D");
		 	        	    		 		 }
		    	    		 		    	if(UtilValidate.isNotEmpty(segMap.get("C"))){
			        	    		 			glCrAmt=(BigDecimal)segMap.get("C");
			        	    		 		 }
		    	    		 		    	if(flag.equals("D")){
		    	    		 		    		segMap.put("D", amtVal.add(glDebitAmt));
			        	    		 		 }else{
			        	    		 			segMap.put("C", amtVal.add(glCrAmt));
			        	    		 		 }
		    	    		 		    	acctCostCodeMap.put(segmentId,segMap);
		    	    		 		    	accntTransTypeMap.put(costCenterId, acctCostCodeMap);
		        	    		 			 
		        	    		 		 }   		 		    	 
	    	    		 		    	
	    	    		 		     }	    	    		 			 
	        	    		 		
	        	    		 		glWiseDetailsMap.put(acctgTransTypeId,accntTransTypeMap);
	    	    		 		 }   		 		
	    	    		 		 
	    	    		 		 tempGlWiseDetailsMap.put(glAccountId, glWiseDetailsMap);
	    	    		 	 }
	    	    		 	postedTransactionTotalsMap1.put(partyId, tempGlWiseDetailsMap);
	    	    	 }
		    	 
		     }
		    	 /*Map accountMap1 = (Map)postedTransactionTotalsMap1.get(partyId);
		    	 if (UtilValidate.isEmpty(accountMap1)) {    	    		 
		    		 accountMap1 = FastMap.newInstance();
		    		 accountMap1.put("glAccountId", glAccountId);
		    		 accountMap1.put("partyId", partyId);
	                 accountMap1.put("D", BigDecimal.ZERO);
	                 accountMap1.put("C", BigDecimal.ZERO);
	             }
		    	 
		         UtilMisc.addToBigDecimalInMap(accountMap1 , transTotalEntry1.getString("debitCreditFlag"), transTotalEntry1.getBigDecimal("amount"));
		         postedTransactionTotalsMap1.put(glAccountId, accountMap1);*/
		     }    	    
		     allPostedTransactionTotalItr1.close();
		     andExprs1.clear();
		     andExprs1.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
		     andExprs1.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		     List glHistoryPartyWiseList = delegator.findList("GlAccountHistoryPartyWise", EntityCondition.makeCondition(andExprs1,EntityOperator.AND), null, null, null, false);
		     delegator.removeAll(glHistoryPartyWiseList);
		     if(UtilValidate.isNotEmpty(postedTransactionTotalsMap1)){
		    	 Iterator tempIter = postedTransactionTotalsMap1.entrySet().iterator();
					while (tempIter.hasNext()) {
						Map.Entry tempEntry = (Entry) tempIter.next();
						String party=(String)tempEntry.getKey();
						Map tempEntryDetails=(Map)tempEntry.getValue();
						Iterator tempEntDetailIter = tempEntryDetails.entrySet().iterator();
						while (tempEntDetailIter.hasNext()) {
							Map.Entry tempGlEntry = (Entry) tempEntDetailIter.next();
							Map tempGlDetailsVal=(Map)tempGlEntry.getValue();
		 					String glAccId=(String)tempGlEntry.getKey();
		 					
		 					Iterator tempEntTypeDetailIter = tempGlDetailsVal.entrySet().iterator();
		 					
		 					while (tempEntTypeDetailIter.hasNext()) {
		 						Map.Entry tempTransTypeEntry = (Entry) tempEntTypeDetailIter.next();
		 						Map tempTransTypeDetailsVal=(Map)tempTransTypeEntry.getValue();
		 	 					String transTypeId=(String)tempTransTypeEntry.getKey();
		 					
		 	 					Iterator tempEntTypeCostDetailIter = tempTransTypeDetailsVal.entrySet().iterator();
		 	 				while (tempEntTypeCostDetailIter.hasNext()) {	
		 	 					Map.Entry tempTransCostEntry = (Entry) tempEntTypeCostDetailIter.next();
		 	 					String costCenterId=(String)tempTransCostEntry.getKey();
		 	 					Debug.log("costCenterId========="+costCenterId);
		 	 					Map costCenterMap=(Map)tempTransCostEntry.getValue();
		 	 					
		 	 					Iterator tempEntTypeSegDetailIter = costCenterMap.entrySet().iterator();
		 	 					while (tempEntTypeSegDetailIter.hasNext()){ 
			 	 					Map.Entry tempTransSegEntry = (Entry) tempEntTypeSegDetailIter.next();
			 	 					Map segMap=(Map)tempTransSegEntry.getValue();
			 	 					String segId=(String)tempTransSegEntry.getKey();
		 	 					BigDecimal debitVal=BigDecimal.ZERO;
		 	 					if(UtilValidate.isNotEmpty(segMap.get("D"))){
		 	 						 debitVal=(BigDecimal)segMap.get("D");
		 	 					}
		 	 					BigDecimal creditVal=BigDecimal.ZERO;
		 	 					if(UtilValidate.isNotEmpty(segMap.get("C"))){
		 	 						 creditVal=(BigDecimal)segMap.get("C");
		 	 					}
		 	 					GenericValue glAccountHistoryPartyWise = delegator.makeValue("GlAccountHistoryPartyWise");
		 	 	    	    	 glAccountHistoryPartyWise.set("glAccountId", glAccId);
		 	 	    	    	 glAccountHistoryPartyWise.set("organizationPartyId",organizationPartyId);
		 	 	    	    	 glAccountHistoryPartyWise.set("partyId",party);
		 	 	    	    	 glAccountHistoryPartyWise.set("costCenterId",costCenterId);
		 	 	    	    	 glAccountHistoryPartyWise.set("segmentId",segId);
		 	 	    	    	 glAccountHistoryPartyWise.set("acctgTransTypeId",transTypeId);
		 	 	    	    	 glAccountHistoryPartyWise.set("customTimePeriodId",customTimePeriodId);
		 	 	    	    	 glAccountHistoryPartyWise.set("postedDebits",debitVal );
		 	 	    	    	 glAccountHistoryPartyWise.set("postedCredits", creditVal);
		 	 	    	    	 delegator.createOrStore(glAccountHistoryPartyWise);
		 	 				}
		 					}
		 					}
						}
					}
		     }
		     
		     
		   
	    } catch (Exception e) {
	        Debug.logError(e, "Problem in reCalculateGlHistory", module);
	        return ServiceUtil.returnError("Problem in reCalculateGlHistory");
	    }
	    return result;
    }
    
    public static Map<String, Object> reCalculateGlHistoryForPeriodForAllInt(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String customTimePeriodId = (String) context.get("customTimePeriodId");
        String partyIdFrom = (String) context.get("partyIdFrom");
        String organizationPartyId = (String)context.get("organizationPartyId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        
        List<GenericValue> internalOrgList = FastList.newInstance();
        List conditionList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(partyIdFrom)){
        	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
        }else{
        	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company"));
        }
        conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
        conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
        conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        internalOrgList = delegator.findList("PartyRelationship",condition,null, UtilMisc.toList("-fromDate"), null, false);
    	
    	List intOrgIdsList = FastList.newInstance();
    	if(UtilValidate.isNotEmpty(internalOrgList)){
    		intOrgIdsList = EntityUtil.getFieldListFromEntityList(internalOrgList, "partyIdTo", true);
    	}
    	intOrgIdsList.add(organizationPartyId);
        Debug.log("intOrgIdsList==========="+intOrgIdsList);
    	intOrgIdsList.add("Company");
    	
    	for(int i=0;i<intOrgIdsList.size();i++){
    		
    		String organizationPartyIdStr = (String) intOrgIdsList.get(i);
    		
    		if(UtilValidate.isNotEmpty(organizationPartyIdStr)){
	            Map<String, Object> getTelParams = FastMap.newInstance();
	        	getTelParams.put("customTimePeriodId", customTimePeriodId);
	        	getTelParams.put("organizationPartyId", organizationPartyIdStr);
	            getTelParams.put("userLogin", userLogin);  
	    		
	    		try {
	                dispatcher.runSync("reCalculateGlHistoryForPeriod", getTelParams);
	            } catch (GenericServiceException e) {
	                Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
	            }
    		}	
    	}
    	return result;
    }
    
	public static Map getLedgerAmountByInvoiceAndPayments(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		//String glaccountId = (String) context.get("glaccountId");
		String invoiceTypeId = (String) context.get("invoiceTypeId");
		String purposeTypeId = (String) context.get("purposeTypeId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String glAccountTypeId ="ACCOUNTS_RECEIVABLE";
		List exprListForParameters = FastList.newInstance();
		List invoiceTypeList = FastList.newInstance();
		Map accountMap = FastMap.newInstance();
		BigDecimal invoiceTotal = BigDecimal.ZERO;
		//BigDecimal invoiceReadyTotal = BigDecimal.ZERO;
		BigDecimal invoiceCancelTotal = BigDecimal.ZERO;
		BigDecimal paymentAppTotal = BigDecimal.ZERO;
		String glAccountId = null;
		Timestamp dayBegin = UtilDateTime.getDayStart(fromDate);
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		
		try {
			/*GenericValue glaccount = delegator.findOne("Glaccount",UtilMisc.toMap("glaccountId", glaccountId), false);
			if (UtilValidate.isEmpty(glaccount)) {
				Debug.logInfo(glaccount+ "'is not a valid glaccountId:"+glaccountId, "");
				return ServiceUtil.returnError(glaccount+ "'is not a valid glaccountId:"+glaccountId);
			}*/
			GenericValue invoiceType = delegator.findOne("InvoiceType",UtilMisc.toMap("invoiceTypeId", invoiceTypeId), false);
			if (UtilValidate.isEmpty(invoiceType)) {
				Debug.logError(invoiceType+ "'is not a valid invoiceTypeId:"+invoiceTypeId, "");
				return ServiceUtil.returnError(invoiceType+ "'is not a valid invoiceTypeId:"+invoiceTypeId);
			}
			
			if(invoiceTypeId.equals("PURCHASE_INVOICE")){
				glAccountTypeId = "ACCOUNTS_PAYABLE";
			}
			
			GenericValue glAccountTypeDefault = delegator.findOne("GlAccountTypeDefault",UtilMisc.toMap("glAccountTypeId", glAccountTypeId,"organizationPartyId","Company"), false);
			glAccountId = glAccountTypeDefault.getString("glAccountId");
			
			invoiceTypeList.add(invoiceType);
			invoiceTypeList.addAll(delegator.findList("InvoiceType", EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,invoiceTypeId),null, null, null, false));
		
		
		EntityListIterator invoicesListItr = null;
		List invoiceTypeIds = EntityUtil.getFieldListFromEntityList(invoiceTypeList, "invoiceTypeId", true);
		//List invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");
		exprListForParameters.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.IN, invoiceTypeIds));
		
		exprListForParameters.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		//exprListForParameters.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_READY"));
		exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.EQUALS, "INVOICE_READY"));
		if(UtilValidate.isNotEmpty(purposeTypeId)){
			exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("purposeTypeId", EntityOperator.NOT_EQUAL, null),EntityOperator.AND, EntityCondition.makeCondition("purposeTypeId",	EntityOperator.EQUALS, purposeTypeId)));
		}
		
		EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		
		try {
			invoicesListItr = delegator.find("InvoiceAndStatus", paramCond,null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		List invoiceIds =  new ArrayList();
		if (!UtilValidate.isEmpty(invoicesListItr)) {
			Set invoiceIdSet = new HashSet(EntityUtil.getFieldListFromEntityListIterator(invoicesListItr,"invoiceId", false));
			invoiceIds = new ArrayList(invoiceIdSet);
			// First compute the total invoice  amount.
			invoiceTotal = InvoiceWorker.getInvoiceTotal(delegator, invoiceIds);
			
		}
		try {
			invoicesListItr.close();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		/*invoicesListItr = null;
		//cancel invoices amount
		exprListForParameters.clear();
		exprListForParameters.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN, invoiceIds));
		exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isEnableAcctg",EntityOperator.EQUALS, "Y"),EntityOperator.OR,EntityCondition.makeCondition("isEnableAcctg",EntityOperator.EQUALS, null)));
		//exprListForParameters.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		exprListForParameters.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_CANCELLED"));
		if(UtilValidate.isNotEmpty(purposeTypeId)){
			exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("purposeTypeId", EntityOperator.NOT_EQUAL, null),EntityOperator.AND, EntityCondition.makeCondition("purposeTypeId",	EntityOperator.EQUALS, purposeTypeId)));
		}
		paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		
		try {
			invoicesListItr = delegator.find("InvoiceAndStatus", paramCond,null, null, null, null);
			//Debug.log("invoicesList==================="+invoicesListItr.getCompleteList());
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
		if (!UtilValidate.isEmpty(invoicesListItr)) {
			Set invoiceIdSet = new HashSet(EntityUtil.getFieldListFromEntityListIterator(invoicesListItr,"invoiceId", false));
			invoiceIds = new ArrayList(invoiceIdSet);
			// First compute the total invoice  amount.
			invoiceCancelTotal = InvoiceWorker.getInvoiceTotal(delegator, invoiceIds);
			
		}*/

		invoicesListItr.close();
		
		
		//payment for period
		/*EntityListIterator paymentListItr = null;
		exprListForParameters.clear();
		exprListForParameters.add(EntityCondition.makeCondition("paymentDate", EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		
		paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		
		try {
			paymentListItr = delegator.find("Payment", paramCond ,null, null, null, null);
			//Debug.log("invoicesList==================="+invoicesListItr.getCompleteList());
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
		List periodPaymentIdList = FastList.newInstance();
		if (!UtilValidate.isEmpty(paymentListItr)) {
			Set paymentIdSet = new HashSet(EntityUtil.getFieldListFromEntityListIterator(paymentListItr,"paymentId", false));
			periodPaymentIdList = new ArrayList(paymentIdSet);
		}
		paymentListItr.close();
		*/
		//here get payment application on that of current period invoices and payment OR current period payments applications
		
		List exprList = FastList.newInstance();
		List<GenericValue> pendingPaymentsList = FastList.newInstance();
		EntityListIterator paymentAppListItr = null;
		EntityCondition orCond1 = EntityCondition.makeCondition(EntityCondition.makeCondition("pmPaymentDate",EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)),
				EntityOperator.OR,EntityCondition.makeCondition("invoiceDate", EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		
		exprList.add(EntityCondition.makeCondition("pmPaymentDate",EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		exprList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN, dayBegin));
		EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		
		
		List orExprList = FastList.newInstance();
		orExprList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		orExprList.add(EntityCondition.makeCondition("pmPaymentDate",EntityOperator.LESS_THAN, dayBegin));
		
		EntityCondition orCond = EntityCondition.makeCondition(orExprList,EntityOperator.AND);
	    
		EntityCondition orCommonCond = EntityCondition.makeCondition(orCond, EntityOperator.OR ,orCond1);
		
		EntityCondition commonCond = EntityCondition.makeCondition(cond, EntityOperator.OR,orCommonCond);
		
		//Debug.log("commonCond appp============"+commonCond);
		
		paymentAppListItr = delegator.find("InvoiceAndApplAndPayment", commonCond, null, UtilMisc.toSet("paymentId","amountApplied"), null, null);
		
		/*Set paymentIdSet = new HashSet(EntityUtil.getFieldListFromEntityListIterator(paymentAppListItr,"paymentId", false));
		List paymentsList = new ArrayList(paymentIdSet);
		*/
		/*GenericValue paymentApp = null;
		//Debug.log("paymentApp============"+paymentAppListItr.next());
		while( UtilValidate.isNotEmpty(paymentAppListItr) && (paymentApp = paymentAppListItr.next()) != null) {
			BigDecimal amountApplied = paymentApp.getBigDecimal("amountApplied");
			paymentAppTotal = paymentAppTotal.add(amountApplied);
			Debug.log("paymentAppTotal in loop============"+paymentAppTotal);
			
		}*/
		List<GenericValue> paymentAppList = paymentAppListItr.getCompleteList();
		//Debug.log("paymentAppList============"+paymentAppList.size());
		for(GenericValue paymentApp : paymentAppList){
			BigDecimal amountApplied = paymentApp.getBigDecimal("amountApplied");
			paymentAppTotal = paymentAppTotal.add(amountApplied);
			//Debug.log("paymentAppTotal in loop============"+paymentAppTotal);
			
		}
		//Debug.log("paymentAppListItr appp size============"+paymentAppListItr.getCompleteList());
		paymentAppListItr.close();
		
		//AcctgTransAndEntries
		//lets get payment appl reversal entries
		//Debug.log("paymentAppTotal============"+paymentAppTotal);
		exprList.clear();
		
		/*EntityListIterator accntTransItr = null;
		exprList.add(EntityCondition.makeCondition("paymentId",EntityOperator.NOT_EQUAL, null));
		exprList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN, invoiceIds));
		exprList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.EQUALS, glAccountId));
		exprList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS, "Y"));
		exprListForParameters.add(EntityCondition.makeCondition("transactionDate", EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		
		orExprList = FastList.newInstance();
		orExprList.add(EntityCondition.makeCondition("paymentId",EntityOperator.IN, periodPaymentIdList));
		orExprList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.NOT_EQUAL, null));
		
	    orCond = EntityCondition.makeCondition(orExprList,EntityOperator.AND);
		
		commonCond = EntityCondition.makeCondition(cond, EntityOperator.OR,orCond);
		Debug.log("commonCond rev============"+commonCond);
		
		try {
			accntTransItr = delegator.find("AcctgTransAndEntries", commonCond, null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
		Set acctgTransIdSet = new HashSet(EntityUtil.getFieldListFromEntityListIterator(accntTransItr,"acctgTransId", false));
		List acctgTransIdList = new ArrayList(acctgTransIdSet);
		accntTransItr.close();
		
		exprList.clear();
		List attrNameList = UtilMisc.toList("PAYMENT_APPL_REVERTED_ACCTG_TRANS_ID","PAYMENT_APPL_PRE_REVERTED_ACCTG_TRANS_ID");
		EntityListIterator accntTransAttrItr = null;
		exprList.add(EntityCondition.makeCondition("acctgTransId",EntityOperator.IN, acctgTransIdList));
		exprList.add(EntityCondition.makeCondition("attrName",EntityOperator.IN, attrNameList));
		
		cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			accntTransAttrItr = delegator.find("AcctgTransAttribute", cond, null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
		Set acctgTransRevIdSet = new HashSet(EntityUtil.getFieldListFromEntityListIterator(accntTransAttrItr,"acctgTransId", false));
		List acctgTransIdRevList = new ArrayList(acctgTransRevIdSet);
		
		accntTransAttrItr.close();
		exprList.clear();
		EntityListIterator accntTransRevItr = null;
		//Debug.log("acctgTransIdRevList==========="+acctgTransIdRevList);
		exprList.add(EntityCondition.makeCondition("acctgTransId",EntityOperator.IN, acctgTransIdRevList));
		exprList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.EQUALS, glAccountId));
		exprList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS, "Y"));
		cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		//Debug.log("cond rev==========="+cond);
		try {
			accntTransRevItr = delegator.find("AcctgTransAndEntries", cond, null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		GenericValue accntTrans= null;
		Map<String ,BigDecimal> revMap = FastMap.newInstance();
		revMap.put("C", BigDecimal.ZERO);
		revMap.put("D", BigDecimal.ZERO);
		while( accntTransRevItr != null && (accntTrans = accntTransRevItr.next()) != null) {
			String debitCreditFlag = accntTrans.getString("debitCreditFlag");
			revMap.put(debitCreditFlag,(revMap.get(debitCreditFlag)).add(accntTrans.getBigDecimal("amount")));
		}
		
	    
		accntTransRevItr.close();*/
	    BigDecimal debitAmount = invoiceTotal;
	    BigDecimal creditAmount = invoiceCancelTotal.add(paymentAppTotal);
	  //let add rev amounts
	   /* debitAmount = debitAmount.add(revMap.get("D"));
	    creditAmount = creditAmount.add(revMap.get("C"));*/
	    
	    BigDecimal endingBalance = debitAmount.subtract(creditAmount);
	    
	    accountMap.put("debitAmount", debitAmount);
	    accountMap.put("creditAmount", creditAmount);
	    accountMap.put("endingBalance", endingBalance);
	    
	    accountMap.put("invoiceTotal", invoiceTotal);
	    accountMap.put("invoiceCancelTotal", invoiceCancelTotal);
	    accountMap.put("paymentAppTotal", paymentAppTotal);
	    //accountMap.put("paymentAppRevTotal", revMap);
	    Debug.log("accountMap=============="+accountMap);
	} catch (GenericEntityException e) {
		Debug.logError(e, module);
		return ServiceUtil.returnError(e.toString());
	}	

	    return accountMap;
	}
	
	
	public static Map<String, Object> getAcctgTransOpeningBalances(DispatchContext dctx, Map<String, Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        List<String> partyIdList = (List) context.get("partyIds");
        String glAccountTypeId =(String) context.get("glAccountTypeId");
        List<String> glAccountTypeIds = (List) context.get("glAccountTypeIds");
        Timestamp transactionDate = (Timestamp) context.get("transactionDate");
        String acctgTransTypeId = (String) context.get("acctgTransTypeId");
        String fromGlAccountId =(String) context.get("glAccountId");
        String costCenterId =(String) context.get("costCenterId");
        String segmentId =(String) context.get("segmentId");
        List<String> roBranchList = (List) context.get("roBranchList");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(transactionDate, -1));
        List partyIds = FastList.newInstance();
        Map<String, Object> openingBalMap = FastMap.newInstance();
        List conditionList = FastList.newInstance();
        List<GenericValue> acctgTransList=null;
        EntityListIterator acctgTransEntryList=null;
        List glAccountIds = FastList.newInstance();
        BigDecimal credit = BigDecimal.ZERO;
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal openingBalance = BigDecimal.ZERO;
        try{
        	if(UtilValidate.isNotEmpty(partyId)){
        		partyIds.add(partyId);
        	}
        	if(UtilValidate.isNotEmpty(partyIdList)){
        		partyIds=partyIdList;
        	}
        	String glAccountId="";
        	if(UtilValidate.isNotEmpty(glAccountTypeId)){
	        	GenericValue glAccountTypeDefault = delegator.findOne("GlAccountTypeDefault", UtilMisc.toMap("glAccountTypeId",glAccountTypeId,"organizationPartyId","Company"), false);
	        	glAccountId = glAccountTypeDefault.getString("glAccountId");
        	}
        	if(UtilValidate.isNotEmpty(glAccountTypeIds)){
	        	List<GenericValue> glAccountTypeDefaultList = delegator.findList("GlAccountTypeDefault",EntityCondition.makeCondition("glAccountTypeId",EntityOperator.IN,glAccountTypeIds),UtilMisc.toSet("glAccountId"),null,null,false);
	        	glAccountIds = EntityUtil.getFieldListFromEntityList(glAccountTypeDefaultList, "glAccountId", true);
        	}
        	if(UtilValidate.isNotEmpty(fromGlAccountId)){
	        	glAccountId = fromGlAccountId;
        	}
//        	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
        	/*conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,previousDayEnd));
        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	acctgTransList = delegator.findList("AcctgTrans",condition,null,null,null,false);
        	acctgTransIds = EntityUtil.getFieldListFromEntityList(acctgTransList, "acctgTransId", false);*/
        	conditionList.clear();
        	//conditionList.add(EntityCondition.makeCondition("acctgTransId",EntityOperator.IN,acctgTransIds));
        	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,previousDayEnd));
        	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyIds));
        	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
        	conditionList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
        	if(UtilValidate.isNotEmpty(glAccountTypeId)){
        		conditionList.add(EntityCondition.makeCondition("glAccountTypeId",EntityOperator.EQUALS,glAccountTypeId));
        	}
        	if((UtilValidate.isNotEmpty(glAccountTypeId) || UtilValidate.isNotEmpty(fromGlAccountId)) && UtilValidate.isNotEmpty(acctgTransTypeId)){
        		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountId",EntityOperator.EQUALS,glAccountId),EntityOperator.OR,
        				                                        EntityCondition.makeCondition("acctgTransTypeId",EntityOperator.EQUALS,acctgTransTypeId)));
        	}
        	
        	if(UtilValidate.isNotEmpty(glAccountTypeId) || UtilValidate.isNotEmpty(fromGlAccountId)){
        		conditionList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.EQUALS,glAccountId));
        	}  
        	if(UtilValidate.isNotEmpty(glAccountTypeIds)){
        		conditionList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.IN,glAccountIds));
        	} 
        	
        	if(UtilValidate.isNotEmpty(costCenterId)){
        		conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.EQUALS,costCenterId));
    		}
        	if(UtilValidate.isNotEmpty(roBranchList)){
        		conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,roBranchList));
    		}
        	if(UtilValidate.isNotEmpty(segmentId)){
    			if(segmentId.equals("YARN_SALE")){
    				conditionList.add(EntityCondition.makeCondition("purposeTypeId",EntityOperator.IN,UtilMisc.toList("YARN_SALE","DEPOT_YARN_SALE")));
    			}
    			else{
    				conditionList.add(EntityCondition.makeCondition("purposeTypeId",EntityOperator.EQUALS,segmentId));
    			}
    		}
        	
        	EntityCondition con = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	acctgTransEntryList = delegator.find("AcctgTransEntryPartyWiseSums",con , null, null, null,null);
            GenericValue acctgTrans;
            while((acctgTrans = acctgTransEntryList.next())!=null){
//        		for(GenericValue acctgTrans:acctgTransList){
//        			String acctgTransId=(String)acctgTrans.get("acctgTransId");
//        			conditionList.clear();
//        			conditionList.add(EntityCondition.makeCondition("acctgTransId",EntityOperator.EQUALS,acctgTransId));
        			
//        			EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
//        			List<GenericValue> acctgTransEntry = EntityUtil.filterByCondition(acctgTransEntryList, cond);
//        			Iterator<GenericValue> transEntry = acctgTransEntry.iterator();
//                    while (transEntry.hasNext()) {
//                        GenericValue TransEntry = transEntry.next();
                        if(((String)acctgTrans.get("debitCreditFlag")).equals("C") && UtilValidate.isNotEmpty((BigDecimal)acctgTrans.get("amount"))){
        					credit=credit.add((BigDecimal)acctgTrans.get("amount"));
        				}
        				if(((String)acctgTrans.get("debitCreditFlag")).equals("D") && UtilValidate.isNotEmpty((BigDecimal)acctgTrans.get("amount"))){
        					debit=debit.add((BigDecimal)acctgTrans.get("amount"));
        				}
        				BigDecimal partyCredit = BigDecimal.ZERO;
        				BigDecimal partyDebit = BigDecimal.ZERO;
        				if(((String)acctgTrans.get("debitCreditFlag")).equals("C") && UtilValidate.isNotEmpty((BigDecimal)acctgTrans.get("amount"))){
        					partyCredit=(BigDecimal)acctgTrans.get("amount");
        				}
        				if(((String)acctgTrans.get("debitCreditFlag")).equals("D") && UtilValidate.isNotEmpty((BigDecimal)acctgTrans.get("amount"))){
        					partyDebit=(BigDecimal)acctgTrans.get("amount");
        				}
        				Map tempMap = FastMap.newInstance();
        				tempMap.put("credit", partyCredit);
        				tempMap.put("debit", partyDebit);
        				if(UtilValidate.isEmpty(openingBalMap.get((acctgTrans.getString("partyId")).toUpperCase()))){
        					openingBalMap.put(acctgTrans.getString("partyId").toUpperCase(),tempMap);
        				}else{
        					Map existing = FastMap.newInstance();
        					existing = (Map)openingBalMap.get(acctgTrans.getString("partyId").toUpperCase());
        					tempMap.put("credit", partyCredit.add((BigDecimal)existing.get("credit")));
            				tempMap.put("debit", partyDebit.add((BigDecimal)existing.get("debit")));
            				openingBalMap.put(acctgTrans.getString("partyId").toUpperCase(),tempMap);
        				}
//                    }
        			/*for(GenericValue TransEntry:acctgTransEntry){
        				if(((String)TransEntry.get("debitCreditFlag")).equals("C") && UtilValidate.isNotEmpty((BigDecimal)TransEntry.get("amount"))){
        					credit=credit.add((BigDecimal)TransEntry.get("amount"));
        				}
        				if(((String)TransEntry.get("debitCreditFlag")).equals("D") && UtilValidate.isNotEmpty((BigDecimal)TransEntry.get("amount"))){
        					debit=debit.add((BigDecimal)TransEntry.get("amount"));
        				}
        			}*/
        		}
        		acctgTransEntryList.close();
        		openingBalance=(debit).subtract(credit);
        }catch (Exception e) {
	        Debug.logError(e, "Error While getting the Opening balace.!", module);
	        return ServiceUtil.returnError(e.getMessage());
	    }
        result.put("credit", credit);
		result.put("debit", debit);
		result.put("openingBalance", openingBalance);
		result.put("openingBalMap", openingBalMap);
        return result;
    }
	
	public static List consolidateAcctgTransEntries(List<GenericValue> normalizedAcctgTransEntries){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		/* consolidate entries by glAccountId ,partyId,debitCreditFlag*/
		List consolidateAcctgTransEntries =FastList.newInstance();
		List tempNormalizedAcctgTransEntries = FastList.newInstance();
		tempNormalizedAcctgTransEntries.addAll(normalizedAcctgTransEntries);
		for(int i=0;i<normalizedAcctgTransEntries.size();i++){
			
			Map transEntry = normalizedAcctgTransEntries.get(i);
			Map tempTransEntry = FastMap.newInstance();
			tempTransEntry.putAll(transEntry);
			
			String glAccountId = (String)transEntry.get("glAccountId");
			String partyId = (String)transEntry.get("partyId");
			String debitCreditFlag = (String)transEntry.get("debitCreditFlag");
			Map condMap = UtilMisc.toMap("glAccountId",glAccountId,"partyId",partyId,"debitCreditFlag",debitCreditFlag);
			
			List parList = EntityUtil.filterByAnd(tempNormalizedAcctgTransEntries, condMap);
			BigDecimal amount = BigDecimal.ZERO;
			if(UtilValidate.isEmpty(parList)){
				continue;
			}
			for(int j=0;j<parList.size();j++){
				Map tempEntry = (Map)parList.get(j);
				amount = amount.add((BigDecimal)tempEntry.get("amount"));
			}
			tempTransEntry.put("amount", amount);
			tempTransEntry.put("origAmount", amount);
			consolidateAcctgTransEntries.add(tempTransEntry);
			tempNormalizedAcctgTransEntries.removeAll(parList);
		}
		//Debug.log("consolidateAcctgTransEntries============"+consolidateAcctgTransEntries);
		return consolidateAcctgTransEntries;
	}
	
	public static Map<String, Object> getAcctgTransBalance(DispatchContext dctx, Map<String, Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        List<String> partyIdList = (List) context.get("partyIds");
        String glAccountTypeId =(String) context.get("glAccountTypeId");
        List<String> glAccountTypeIds = (List) context.get("glAccountTypeIds");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        String fromGlAccountId =(String) context.get("glAccountId");
        String costCenterId =(String) context.get("costCenterId");
        String segmentId =(String) context.get("segmentId");
        List<String> roBranchList = (List) context.get("roBranchList");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List partyIds = FastList.newInstance();
        Map<String, Object> openingBalMap = FastMap.newInstance();
        List conditionList = FastList.newInstance();
        List<GenericValue> acctgTransList=null;
        EntityListIterator acctgTransEntryList=null;
        List glAccountIds = FastList.newInstance();
        BigDecimal credit = BigDecimal.ZERO;
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal openingBalance = BigDecimal.ZERO;
        try{
        	if(UtilValidate.isNotEmpty(partyId)){
        		partyIds.add(partyId);
        	}
        	if(UtilValidate.isNotEmpty(partyIdList)){
        		partyIds=partyIdList;
        	}
        	String glAccountId="";
        	if(UtilValidate.isNotEmpty(glAccountTypeId)){
	        	GenericValue glAccountTypeDefault = delegator.findOne("GlAccountTypeDefault", UtilMisc.toMap("glAccountTypeId",glAccountTypeId,"organizationPartyId","Company"), false);
	        	glAccountId = glAccountTypeDefault.getString("glAccountId");
        	}
        	if(UtilValidate.isNotEmpty(glAccountTypeIds)){
	        	List<GenericValue> glAccountTypeDefaultList = delegator.findList("GlAccountTypeDefault",EntityCondition.makeCondition("glAccountTypeId",EntityOperator.IN,glAccountTypeIds),UtilMisc.toSet("glAccountId"),null,null,false);
	        	glAccountIds = EntityUtil.getFieldListFromEntityList(glAccountTypeDefaultList, "glAccountId", true);
        	}
        	if(UtilValidate.isNotEmpty(fromGlAccountId)){
	        	glAccountId = fromGlAccountId;
        	}

        	conditionList.clear();
        	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
        	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
        	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyIds));
        	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
        	conditionList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
        	if(UtilValidate.isNotEmpty(glAccountTypeId) || UtilValidate.isNotEmpty(fromGlAccountId)){
        		conditionList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.EQUALS,glAccountId));
        	}  
        	if(UtilValidate.isNotEmpty(glAccountTypeIds)){
        		conditionList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.IN,glAccountIds));
        	} 
        	if(UtilValidate.isNotEmpty(costCenterId)){
        		conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.EQUALS,costCenterId));
    		}
        	if(UtilValidate.isNotEmpty(roBranchList)){
        		conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,roBranchList));
    		}
        	if(UtilValidate.isNotEmpty(segmentId)){
    			if(segmentId.equals("YARN_SALE")){
    				conditionList.add(EntityCondition.makeCondition("purposeTypeId",EntityOperator.IN,UtilMisc.toList("YARN_SALE","DEPOT_YARN_SALE")));
    			}
    			else{
    				conditionList.add(EntityCondition.makeCondition("purposeTypeId",EntityOperator.EQUALS,segmentId));
    			}
    		}
        	EntityCondition con = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	acctgTransEntryList = delegator.find("AcctgTransEntryPartyWiseSums",con , null, null, null,null);
        	if(UtilValidate.isNotEmpty(acctgTransEntryList)){
        		while (acctgTransEntryList.hasNext()) {
                    GenericValue acctgTrans = acctgTransEntryList.next();

                        if(((String)acctgTrans.get("debitCreditFlag")).equals("C") && UtilValidate.isNotEmpty((BigDecimal)acctgTrans.get("amount"))){
        					credit=credit.add((BigDecimal)acctgTrans.get("amount"));
        				}
        				if(((String)acctgTrans.get("debitCreditFlag")).equals("D") && UtilValidate.isNotEmpty((BigDecimal)acctgTrans.get("amount"))){
        					debit=debit.add((BigDecimal)acctgTrans.get("amount"));
        				}
        				BigDecimal partyCredit = BigDecimal.ZERO;
        				BigDecimal partyDebit = BigDecimal.ZERO;
        				if(((String)acctgTrans.get("debitCreditFlag")).equals("C") && UtilValidate.isNotEmpty((BigDecimal)acctgTrans.get("amount"))){
        					partyCredit=(BigDecimal)acctgTrans.get("amount");
        				}
        				if(((String)acctgTrans.get("debitCreditFlag")).equals("D") && UtilValidate.isNotEmpty((BigDecimal)acctgTrans.get("amount"))){
        					partyDebit=(BigDecimal)acctgTrans.get("amount");
        				}
        				Map tempMap = FastMap.newInstance();
        				tempMap.put("credit", partyCredit);
        				tempMap.put("debit", partyDebit);
        				if(UtilValidate.isEmpty(openingBalMap.get((acctgTrans.getString("partyId")).toUpperCase()))){
        					openingBalMap.put(acctgTrans.getString("partyId").toUpperCase(),tempMap);
        				}else{
        					Map existing = FastMap.newInstance();
        					existing = (Map)openingBalMap.get(acctgTrans.getString("partyId").toUpperCase());
        					tempMap.put("credit", partyCredit.add((BigDecimal)existing.get("credit")));
            				tempMap.put("debit", partyDebit.add((BigDecimal)existing.get("debit")));
            				openingBalMap.put(acctgTrans.getString("partyId").toUpperCase(),tempMap);
        				}
        		}
        		acctgTransEntryList.close();
        		openingBalance=(debit).subtract(credit);
        	}
        }catch (Exception e) {
	        Debug.logError(e, "Error While getting the Opening balace.!", module);
	        return ServiceUtil.returnError(e.getMessage());
	    }
        result.put("credit", credit);
		result.put("debit", debit);
		result.put("openingBalance", openingBalance);
		result.put("openingBalMap", openingBalMap);
        return result;
    }	
}

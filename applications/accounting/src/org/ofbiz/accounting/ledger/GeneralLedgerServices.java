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
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
    //reCalculateGlHistoryForPeriod
    public static Map<String, Object> reCalculateGlHistoryForPeriod(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String customTimePeriodId = (String) context.get("customTimePeriodId");
        String organizationPartyId = (String)context.get("organizationPartyId");
        String glFiscalTypeId = (String)context.get("glFiscalTypeId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
        	 GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",customTimePeriodId), false);
    	     //emtpy or not a fiscal period then return
        	 if(UtilValidate.isEmpty(customTimePeriod) || !(customTimePeriod.getString("periodTypeId").contains("FISCAL")) || (customTimePeriod.getString("isClosed").equals("Y"))){
    	    	 return result;
    	     }
        	 
    	     Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
    	     Timestamp thruDate = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
 			 
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
    	     //Debug.log("andCond======="+andCond);
    	     EntityListIterator allPostedTransactionTotalItr = delegator.find("AcctgTransEntrySums", andCond, null, null, null, null);
    	     GenericValue transTotalEntry;
    	     Map<String ,Object> postedTransactionTotalsMap = FastMap.newInstance();
    	     
    	     while(allPostedTransactionTotalItr != null && (transTotalEntry = allPostedTransactionTotalItr.next()) != null) {
    	    	 String glAccountId = transTotalEntry.getString("glAccountId");
    	    	 Map accountMap = (Map)postedTransactionTotalsMap.get(transTotalEntry.getString("glAccountId"));
    	    	 if (UtilValidate.isEmpty(accountMap)) {
    	    		 accountMap = FastMap.newInstance();
    	    		 accountMap.put("glAccountId", glAccountId);
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
    	    	 glAccountHistory.set("organizationPartyId",organizationPartyId);
    	    	 glAccountHistory.set("customTimePeriodId",customTimePeriodId);
    	    	 glAccountHistory.set("postedDebits",(BigDecimal)postedTotalEntry.get("D") );
    	    	 glAccountHistory.set("postedCredits", (BigDecimal)postedTotalEntry.get("C"));
    	    	 delegator.createOrStore(glAccountHistory);
    	    	 //Debug.log("glAccountHistory============="+glAccountHistory);
    	     }	 
    	    	 
    	   
        } catch (Exception e) {
            Debug.logError(e, "Problem in reCalculateGlHistory", module);
            return ServiceUtil.returnError("Problem in reCalculateGlHistory");
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
		List invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");
		exprListForParameters.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.IN, invoiceTypeIds));
		
		exprListForParameters.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		exprListForParameters.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_READY"));
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
		invoicesListItr = null;
		//cancel invoices amount
		exprListForParameters.clear();
		exprListForParameters.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN, invoiceIds));
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
			
		}
		try {
			invoicesListItr.close();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
		List exprList = FastList.newInstance();
		List<GenericValue> pendingPaymentsList = FastList.newInstance();
		EntityListIterator paymentAppListItr = null;
		exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("pmPaymentDate",EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)),
				EntityOperator.OR,EntityCondition.makeCondition("invoiceDate", EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd))));
		EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			paymentAppListItr = delegator.find("InvoiceAndApplAndPayment", cond, null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		Set paymentIdSet = new HashSet(EntityUtil.getFieldListFromEntityListIterator(paymentAppListItr,"paymentId", false));
		List paymentsList = new ArrayList(paymentIdSet);
		
		GenericValue paymentApp = null;
		while( paymentAppListItr != null && (paymentApp = paymentAppListItr.next()) != null) {
			BigDecimal amountApplied = paymentApp.getBigDecimal("amountApplied");
			paymentAppTotal = paymentAppTotal.add(amountApplied);
		}
		try {
			paymentAppListItr.close();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		//AcctgTransAndEntries
		//lets get payment appl reversal entries
		exprList.clear();
		
		EntityListIterator accntTransItr = null;
		exprList.add(EntityCondition.makeCondition("paymentId",EntityOperator.NOT_EQUAL, null));
		exprList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN, invoiceIds));
		exprList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.EQUALS, glAccountId));
		exprList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS, "Y"));
		exprListForParameters.add(EntityCondition.makeCondition("transactionDate", EntityOperator.BETWEEN, UtilMisc.toList(dayBegin,dayEnd)));
		
		cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			accntTransItr = delegator.find("AcctgTransAndEntries", cond, null, null, null, null);
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
		
	    
		accntTransRevItr.close();
	    BigDecimal debitAmount = invoiceTotal;
	    BigDecimal creditAmount = invoiceCancelTotal.add(paymentAppTotal);
	  //let add rev amounts
	    debitAmount = debitAmount.add(revMap.get("D"));
	    creditAmount = creditAmount.add(revMap.get("C"));
	    
	    BigDecimal endingBalance = debitAmount.subtract(creditAmount);
	    
	    accountMap.put("debitAmount", debitAmount);
	    accountMap.put("creditAmount", creditAmount);
	    accountMap.put("endingBalance", endingBalance);
	    
	    accountMap.put("invoiceTotal", invoiceTotal);
	    accountMap.put("invoiceCancelTotal", invoiceCancelTotal);
	    accountMap.put("paymentAppTotal", paymentAppTotal);
	    accountMap.put("paymentAppRevTotal", revMap);
	    Debug.log("accountMap=============="+accountMap);
	} catch (GenericEntityException e) {
		Debug.logError(e, module);
		return ServiceUtil.returnError(e.toString());
	}	

	    return accountMap;
	}

}
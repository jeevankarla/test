	/*
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
	*/
	
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	
	import java.math.BigDecimal;
	import java.util.*;
	import java.sql.Timestamp;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.entity.util.*;
	import org.ofbiz.base.util.*;

	import java.util.*;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import net.sf.json.JSONArray;
	import java.util.SortedMap;
	import javolution.util.FastList;
	import org.ofbiz.service.ServiceUtil;
	import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;
	
	dayWiseCategorySalesMap = [:];
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	effectiveDate = UtilDateTime.nowTimestamp();
	fromDateStr="2014-04-30"
	
	
	if (UtilValidate.isNotEmpty(fromDateStr)) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	try {
		effectiveDate = new java.sql.Timestamp(dateFormat.parse(fromDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + fromDateStr, "");
	}
}
dayBegin = UtilDateTime.getDayStart(effectiveDate);
dayEnd = UtilDateTime.getDayEnd(effectiveDate)
	
	
/*select * from `PAYMENT` WHERE `PAYMENT_ID` IN (select `PAYMENT_ID` from `PAYMENT_APPLICATION` where `PAYMENT_ID` IN 
	(SELECT `PAYMENT_ID`  FROM `PAYMENT_APPLICATION` WHERE  `INVOICE_ID` IN
		 (select `INVOICE_ID` from `INVOICE` where `INVOICE_DATE`>'2014-04-30 23:59:59' ) AND `PAYMENT_ID` IN
		  (select `PAYMENT_ID` from `PAYMENT` where  `PAYMENT_METHOD_TYPE_ID` ='VBIZ_PAYIN'))*/
conditionList=[];
	Debug.log("=====Before=ServiceRun====dayEnd="+dayEnd);
	isSubmitted =true;
	if(UtilValidate.isNotEmpty(isSubmitted)){
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO , dayEnd));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		beforeInvoiceList = delegator.findList("Invoice",condition , null, ["dueDate"], null, false);
		beforeInvoiceIdsList = EntityUtil.getFieldListFromEntityList(beforeInvoiceList, "invoiceId", false);
		beforeInvoiceIdsList.each{ invoiceId->
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId" : invoiceId), false);
			Timestamp invDate=invoice.dueDate;
			invoice.set("invoiceDate",invDate);
			invoice.store();
		}
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN , dayEnd));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	afterInvoiceList = delegator.findList("Invoice",condition , null, ["invoiceDate"], null, false);
	afterInvoiceIdsList = EntityUtil.getFieldListFromEntityList(afterInvoiceList, "invoiceId", false);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS , "VBIZ_PAYIN"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID")));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	vbizPaymentsList = delegator.findList("Payment",condition , null, null, null, false);
	vbizPaymentIdsList = EntityUtil.getFieldListFromEntityList(vbizPaymentsList, "paymentId", false);
	//after application
	afterApplPayIdsList=[];
	if(UtilValidate.isNotEmpty(vbizPaymentsList)&&UtilValidate.isNotEmpty(vbizPaymentIdsList)){
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN , afterInvoiceIdsList));
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN , vbizPaymentIdsList));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	afterApplPayList = delegator.findList("PaymentApplication",condition , null, null, null, false);
	afterApplPayIdsList = EntityUtil.getFieldListFromEntityList(afterApplPayList, "paymentId", true);
	}
	//before
	//payments void list  afterApplPayIdsList
	
	//vbiz paymentsApplication After 30th 
	vbizExtraInvoiceIdsList=[];
	if(UtilValidate.isNotEmpty(afterApplPayIdsList)){
	conditionList.clear();
	//conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN , beforeInvoiceIdsList));
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN , afterApplPayIdsList));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	vbizExtraApplicationList = delegator.findList("PaymentApplication",condition , null, null, null, false);
	vbizExtraInvoiceIdsList = EntityUtil.getFieldListFromEntityList(vbizExtraApplicationList, "invoiceId", true);
	}
	Debug.log("====vbizExtraInvoiceIdsList==Size="+vbizExtraInvoiceIdsList.size());
	beforeTempInvoiceIdsList=[];
	if(UtilValidate.isNotEmpty(vbizExtraInvoiceIdsList)){ 
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO , dayEnd));
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN , vbizExtraInvoiceIdsList));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	beforeTempInvoiceList = delegator.findList("Invoice",condition , null, ["invoiceDate"], null, false);
	beforeTempInvoiceIdsList = EntityUtil.getFieldListFromEntityList(beforeTempInvoiceList, "invoiceId", false);
	}
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN , dayEnd));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID")));
	conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS , "VBIZ_PAYIN"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	afterPayList = delegator.findList("Payment",condition , null, ["paymentDate"], null, false);
	afterPayIdsList = EntityUtil.getFieldListFromEntityList(afterPayList, "paymentId", false);
	afterApplPayIdsList.addAll(afterPayIdsList);
	Debug.log("====afterApplPayIdsList==Size="+afterApplPayIdsList.size());
	Set voidedPayList=new HashSet(afterApplPayIdsList);
	Debug.log("====VoidedPayments==Size="+voidedPayList.size());
	//void payments
	voidedPayList.each{paymentId->
		Debug.log("==VoidPaymentId=="+paymentId);
		Map resultPayMap = dispatcher.runSync("voidPayment", UtilMisc.toMap("paymentId", paymentId, "userLogin", userLogin));
		if (ServiceUtil.isError(resultPayMap)) {
			return ServiceUtil.returnError("There was an error in cancelling credit note: " + ServiceUtil.getErrorMessage(resultPayMap));
		}
	}
	//create Payment Context
	Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", "SALES_PAYIN");
	paymentCtx.put("paymentMethodTypeId", "VBIZ_PAYIN");
	paymentCtx.put("organizationPartyId","Company");
	
	//knockoff invoics
	
	//outstanding foe invoice
	//create vbizpayin payment for outstaning amount
	// create payment application for invoice and payment
	beforeTempInvoiceIdsList.each{ invoiceId->
	
	 invoicePaymentInfoList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("invoiceId", invoiceId, "userLogin", userLogin));
	if(UtilValidate.isNotEmpty(invoicePaymentInfoList.get("invoicePaymentInfoList"))){
	 invoicePaymentInfo = invoicePaymentInfoList.get("invoicePaymentInfoList").get(0);
	 outStandingAmount=0;
		 if(UtilValidate.isNotEmpty(invoicePaymentInfo)){
		 outStandingAmount=invoicePaymentInfo.outstandingAmount;
		 }
		 if(outStandingAmount>0){
		 GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId" : invoiceId), false);
		  
		            paymentCtx.put("partyId",invoice.partyId);
		            paymentCtx.put("facilityId", invoice.facilityId);
		            paymentCtx.put("paymentPurposeType", "");
		            paymentCtx.put("instrumentDate", invoice.dueDate);
					paymentCtx.put("paymentDate", invoice.dueDate);
					paymentCtx.put("effectiveDate", invoice.dueDate);
		            paymentCtx.put("statusId", "PMNT_RECEIVED");
		            paymentCtx.put("isEnableAcctg", "N");
		            paymentCtx.put("amount", outStandingAmount);
		            paymentCtx.put("userLogin", userLogin); 
		            paymentCtx.put("invoices", UtilMisc.toList(invoiceId));
		    		try{
		            Map<String, Object> paymentResult = dispatcher.runSync("createPaymentAndApplicationForInvoices", paymentCtx);
		            if (ServiceUtil.isError(paymentResult)) {
		            	Debug.logError(paymentResult.toString(), module);
		                return ServiceUtil.returnError(null, null, null, paymentResult);
		            }
		            paymentId = (String)paymentResult.get("paymentId");
		            }catch (Exception e) {
		            Debug.logError(e, e.toString(), module);
		            return ServiceUtil.returnError(e.toString());
			        }
		 }
	 }
		
		
	}
	
	Debug.log("=====beforeTempInvoiceIdsList===="+beforeTempInvoiceIdsList);
	Debug.log("=====beforeTempInvoiceIdsList==Size=="+beforeTempInvoiceIdsList.size());
	Debug.log("*********************=NewService********StartsHERE==========>");
	//to void PM invoices VBIZ payments and..settle
	pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,"PM");
	Debug.log("=====pmShipmentIds==Size=="+pmShipmentIds.size());
	if(UtilValidate.isNotEmpty(pmShipmentIds)){
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_EQUAL , "INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , pmShipmentIds));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	pmTempInvoiceList = delegator.findList("OrderHeaderFacAndItemBillingInv",condition , null, ["invoiceId"], null, false);
	pmTempInvoiceIdsList = EntityUtil.getFieldListFromEntityList(pmTempInvoiceList, "invoiceId", true);
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS , "VBIZ_PAYIN"));
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN , pmTempInvoiceIdsList));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	vbizPMApplicationList = delegator.findList("PaymentAndApplication",condition , null, null, null, false);
	vbizPmPayIdsList = EntityUtil.getFieldListFromEntityList(vbizPMApplicationList, "paymentId", true);
	
	vbizNotPmInvIdsList=[];
	conditionList.clear();
	if(UtilValidate.isNotEmpty(vbizPmPayIdsList) &&UtilValidate.isNotEmpty(pmTempInvoiceIdsList)){
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN , vbizPmPayIdsList));
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.NOT_IN , pmTempInvoiceIdsList));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	vbizPMNotApplicationList = delegator.findList("PaymentApplication",condition , null, null, null, false);
	vbizNotPmInvIdsList = EntityUtil.getFieldListFromEntityList(vbizPMNotApplicationList, "invoiceId", true);
	}
	//Void this Payments List
	Debug.log("==vbizPmPayIdsList=="+vbizPmPayIdsList+"and SIZE="+vbizPmPayIdsList.size());
	vbizPmPayIdsList.each{paymentId->
		Map resultPayMap = dispatcher.runSync("voidPayment", UtilMisc.toMap("paymentId", paymentId, "userLogin", userLogin));
		if (ServiceUtil.isError(resultPayMap)) {
			return ServiceUtil.returnError("There was an error in cancelling credit note: " + ServiceUtil.getErrorMessage(resultPayMap));
		}
	}
	Debug.log("====vbizNotPmInvIdsList="+vbizNotPmInvIdsList+"=====AndSize=="+vbizNotPmInvIdsList.size());
	Debug.log("=====AndSize=="+vbizNotPmInvIdsList.size());
	//knockoff invoics
	vbizNotPmInvIdsList.each{ invoiceId->
	 invoicePaymentInfoList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("invoiceId", invoiceId, "userLogin", userLogin));
		if(UtilValidate.isNotEmpty(invoicePaymentInfoList.get("invoicePaymentInfoList"))){
			 invoicePaymentInfo = invoicePaymentInfoList.get("invoicePaymentInfoList").get(0);
			 outStandingAmount=0;
			 if(UtilValidate.isNotEmpty(invoicePaymentInfo)){
			 outStandingAmount=invoicePaymentInfo.outstandingAmount;
			 }
			 if(outStandingAmount>0){
			 GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId" : invoiceId), false);
			  
						paymentCtx.put("partyId",invoice.partyId);
						paymentCtx.put("facilityId", invoice.facilityId);
						paymentCtx.put("paymentPurposeType", "");
						paymentCtx.put("instrumentDate", invoice.dueDate);
						paymentCtx.put("paymentDate", invoice.dueDate);
						paymentCtx.put("effectiveDate", invoice.dueDate);
						paymentCtx.put("statusId", "PMNT_RECEIVED");
						paymentCtx.put("isEnableAcctg", "N");
						paymentCtx.put("amount", outStandingAmount);
						paymentCtx.put("userLogin", userLogin);
						paymentCtx.put("invoices", UtilMisc.toList(invoiceId));
						try{
						Map<String, Object> paymentResult = dispatcher.runSync("createPaymentAndApplicationForInvoices", paymentCtx);
						if (ServiceUtil.isError(paymentResult)) {
							Debug.logError(paymentResult.toString(), module);
							return ServiceUtil.returnError(null, null, null, paymentResult);
						}
						paymentId = (String)paymentResult.get("paymentId");
						}catch (Exception e) {
						Debug.logError(e, e.toString(), module);
						return ServiceUtil.returnError(e.toString());
						}
			 }
		 }
	  }//forEach Close
	}
	
	//Reset PaymentDate
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN , dayEnd));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID")));
	conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS , "VBIZ_PAYIN"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	afterPayList = delegator.findList("Payment",condition , null, ["paymentDate"], null, false);
	afterPayIdsList = EntityUtil.getFieldListFromEntityList(afterPayList, "paymentId", false);
	Set resetDatePayIdsList=new HashSet(afterPayIdsList);//Date needs to be reset
	Debug.log("resetDatePayIdsList========Size="+resetDatePayIdsList.size());
	resetDatePayIdsList.each{paymentId->
		GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId" : paymentId), false);
		Timestamp paymentDate=payment.paymentDate;
		payment.set("effectiveDate",paymentDate);
		payment.set("instrumentDate",paymentDate);
		payment.store();
	}
		Debug.log("Service Runs Sucessfully========!");
	    context.errorMessage = "Totally Invoies Size() '" + beforeTempInvoiceIdsList.size() + "' And Payments size="+voidedPayList.size() +" Find this Service!"+"==PmAppliedInvLsit=Size="+vbizNotPmInvIdsList.size();
		//context.sucess = "Service Runs Sucessfully !";
		return;
	
	}
	
	
	
	
	


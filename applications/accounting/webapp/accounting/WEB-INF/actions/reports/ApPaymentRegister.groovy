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
	
	import org.ofbiz.entity.GenericValue;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.entity.util.EntityUtil;
	import org.ofbiz.base.util.*;

	import java.text.SimpleDateFormat;
	import java.text.ParseException;
	import java.util.List;
	import java.math.BigDecimal;
	import java.sql.Timestamp;
	
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.accounting.payment.PaymentWorker;
	
	userLogin= context.userLogin;
	
	fromDateStr = parameters.fromDate;
	thruDateStr = parameters.thruDate;
	finAccountId = parameters.finAccountId;
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy, MMM dd");
	Timestamp fromDateTs = null;
	if(fromDateStr){
		try {
			fromDateTs = new java.sql.Timestamp(formatter.parse(fromDateStr).getTime());
		} catch (ParseException e) {
		}
	}
	Timestamp thruDateTs = null;
	if(thruDateStr){
		try {
			thruDateTs = new java.sql.Timestamp(formatter.parse(thruDateStr).getTime());
		} catch (ParseException e) {
		}
	}
	fromDate = UtilDateTime.getDayStart(fromDateTs, timeZone, locale);
	thruDate = UtilDateTime.getDayEnd(thruDateTs, timeZone, locale);
	context.fromDate = fromDate;
	context.thruDate = thruDate;
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("PMNT_SENT", "PMNT_CONFIRMED")));
	//conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIdsList));
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	List paymentList = delegator.findList("Payment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("paymentId", "amount", "paymentSequenceId"), UtilMisc.toList("paymentDate"), null, false);
	paymentIdsList = EntityUtil.getFieldListFromEntityList(paymentList, "paymentId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIdsList));
	conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
	
	finPaymentIdsList = [];
	List<GenericValue> finAccountTrans = delegator.findList("FinAccountTrans", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("paymentId"), ["paymentId"], null, false);
	if(UtilValidate.isNotEmpty(finAccountTrans)){
		finPaymentIdsList = EntityUtil.getFieldListFromEntityList(finAccountTrans, "paymentId", true);
	}
	requiredPayments = EntityUtil.filterByCondition(paymentList, EntityCondition.makeCondition("paymentId",EntityOperator.IN, finPaymentIdsList));
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIdsList));
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.NOT_EQUAL, null));
	List paymentApplicationList = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId", "paymentId", "amountApplied"), UtilMisc.toList("paymentDate"), null, false);
	invoiceIdsList = EntityUtil.getFieldListFromEntityList(paymentApplicationList, "invoiceId", true);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIdsList));
	List paymentInvApplicationList = delegator.findList("InvoiceAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId", "invoiceItemTypeId"), UtilMisc.toList("invoiceDate"), null, false);
	
	List<GenericValue> invoiceItemTypes = delegator.findList("InvoiceItemType", null, UtilMisc.toSet("invoiceItemTypeId", "description"), null, null, false);
	
	paymentRegisterList = [];
	
	for(i=0; i<requiredPayments.size(); i++){
		paymentId = (requiredPayments.get(i)).get("paymentId");
		String paymentSequenceId = (requiredPayments.get(i)).get("paymentSequenceId");
		amount = (requiredPayments.get(i)).get("amount");
		paymentNotApplied = PaymentWorker.getPaymentNotApplied(requiredPayments.get(i), true);
		
		paymentApplicationDetailsMap = [:];
		if(UtilValidate.isNotEmpty(paymentSequenceId)){
			paymentApplicationDetailsMap.put("paymentId", paymentSequenceId);
		}
		else{
			paymentApplicationDetailsMap.put("paymentId", paymentId);
		}
		
		paymentApplicationDetailsMap.put("amount", amount);
		paymentApplicationDetailsMap.put("amountOpen", paymentNotApplied);
		
		paymentApplication = EntityUtil.filterByAnd(paymentApplicationList, [paymentId : paymentId]);
		for(j=0; j<paymentApplication.size(); j++){
			
			eachApplication = paymentApplication.get(j);
			paymentApplicationDetailsMap.put("amountApplied", eachApplication.get("amountApplied"));
			paymentApplicationDetailsMap.put("invoiceId", eachApplication.get("invoiceId"));
			invoiceItems = EntityUtil.filterByAnd(paymentInvApplicationList, [invoiceId : eachApplication.get("invoiceId")]);
			for(k=0; k<invoiceItems.size(); k++){
				invoiceItem = invoiceItems.get(k);
				
				invoiceItemTypeId = invoiceItem.get("invoiceItemTypeId");
				invoiceItemTypeDescription = (EntityUtil.getFirst(EntityUtil.filterByAnd(invoiceItemTypes, [invoiceItemTypeId : invoiceItemTypeId]))).get("description");
				
				paymentApplicationDetailsMap.put("invoiceItemTypeId", invoiceItemTypeDescription);
				
				tempPmntMap = [:];
				tempPmntMap.putAll(paymentApplicationDetailsMap);
				
				paymentRegisterList.add(tempPmntMap);
				paymentApplicationDetailsMap.clear();
			}
		}
	}
	context.paymentRegisterList = paymentRegisterList;
	
	
	

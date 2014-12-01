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
	import java.math.BigDecimal;
	import java.sql.Timestamp;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.accounting.payment.PaymentWorker;
	
	userLogin= context.userLogin;
	
	fromDateStr = parameters.fromDate;
	thruDateStr = parameters.thruDate;
	Debug.log("fromDateStr ================"+fromDateStr);
	Debug.log("thruDateStr ================"+thruDateStr);
	context.fromDateStr = fromDateStr;
	context.thruDateStr = thruDateStr;
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy, MMM dd");
	Timestamp fromDateTs = null;
	if(fromDateStr){
		try {
			fromDateTs = new java.sql.Timestamp(formatter.parse(fromDateStr).getTime());
		} catch (ParseException e) {
		}
	}
	Debug.log("fromDateTs ================"+fromDateTs);
	Timestamp thruDateTs = null;
	if(thruDateStr){
		try {
			thruDateTs = new java.sql.Timestamp(formatter.parse(thruDateStr).getTime());
		} catch (ParseException e) {
		}
	}
	Debug.log("thruDateTs ================"+thruDateTs);
	fromDate = UtilDateTime.getDayStart(fromDateTs, timeZone, locale);
	thruDate = UtilDateTime.getDayEnd(thruDateTs, timeZone, locale);
	
	paymentTypeId = parameters.paymentTypeId;
	Debug.log("fromDate ================"+fromDate);
	Debug.log("thruDate ================"+thruDate);
	Debug.log("paymentTypeId ================"+paymentTypeId);
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_SENT"));
	conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, paymentTypeId));
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	List paymentList = delegator.findList("Payment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("paymentDate"), null, false);
	paymentIdsList = EntityUtil.getFieldListFromEntityList(paymentList, "paymentId", true);
	partyIdsList = EntityUtil.getFieldListFromEntityList(paymentList, "partyIdTo", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIdsList));
	List paymentApplicationList = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("paymentDate"), null, false);
	toPaymentIdsList = EntityUtil.getFieldListFromEntityList(paymentApplicationList, "toPaymentId", true);
	invoiceIdsList = EntityUtil.getFieldListFromEntityList(paymentApplicationList, "invoiceId", true);
	
	
	partyPaymentsMap = [:];
	for(i=0; i<partyIdsList.size(); i++){
		partyId = partyIdsList.get(i);
		Debug.log("partyId-------------------"+partyId);
		partyPaymentsList = EntityUtil.filterByAnd(paymentList, [partyIdTo : partyId]);
		partyPmntIdsList = EntityUtil.getFieldListFromEntityList(partyPaymentsList, "paymentId", true);
		Debug.log("partyPaymentsList=============="+partyPaymentsList);
		
		advDetailsMap = [:];
		
		// Calculate Opening Balance
		oldPmnts = EntityUtil.filterByCondition(partyPaymentsList, EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, fromDate));
		Debug.log("oldPmnts=============="+oldPmnts);
		oldPmntIdsList = EntityUtil.getFieldListFromEntityList(oldPmnts, "paymentId", true);
		
		amountPaid = BigDecimal.ZERO;
		for(j=0; j<oldPmnts.size(); j++){
			amountPaid = amountPaid.add((oldPmnts.get(j)).getBigDecimal("amount"));
		}
		
		condList = [];
		condList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, oldPmntIdsList));
		condList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, fromDate));
		condExpr = EntityCondition.makeCondition(condList, EntityOperator.AND);
		oldPmntAppList = EntityUtil.filterByCondition(paymentApplicationList, condExpr);
		
		amountApplied = BigDecimal.ZERO;
		for(j=0; j<oldPmntAppList.size(); j++){
			amountApplied = amountApplied.add((oldPmntAppList.get(j)).getBigDecimal("amountApplied"));
		}
		openingBalance = amountPaid.subtract(amountApplied);
		
		obMap = [:];
		obMap.put("debit", openingBalance);
		obMap.put("credit", BigDecimal.ZERO);
		tempMap = [:];
		tempMap.putAll(obMap);
		advDetailsMap.put("openingBalance", tempMap);
		
		// Calculate During period credit and debit
		
		durationPmnts = EntityUtil.filterByCondition(partyPaymentsList, EntityCondition.makeCondition("paymentId",EntityOperator.NOT_IN, oldPmntIdsList));
		duringAmountPaid = BigDecimal.ZERO;
		for(j=0; j<durationPmnts.size(); j++){
			duringAmountPaid = duringAmountPaid.add((durationPmnts.get(j)).getBigDecimal("amount"));
		}
		
		condList = [];
		condList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, partyPmntIdsList));
		condList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		condExpr1 = EntityCondition.makeCondition(condList, EntityOperator.AND);
		durationPmntApps = EntityUtil.filterByCondition(paymentApplicationList, condExpr1);
		
		duringAmountApplied = BigDecimal.ZERO;
		for(j=0; j<durationPmntApps.size(); j++){
			duringAmountApplied = duringAmountApplied.add((durationPmntApps.get(j)).getBigDecimal("amountApplied"));
		}
		
		dpMap = [:];
		dpMap.put("debit", duringAmountPaid);
		dpMap.put("credit", duringAmountApplied);
		tempMap = [:];
		tempMap.putAll(dpMap);
		advDetailsMap.put("duringPeriod", tempMap);
		
		// Calculate Closing Balance
		totalDebit = openingBalance.add(duringAmountPaid);
		totalCredit = duringAmountApplied;
		
		closingBalance = totalDebit.subtract(totalCredit);
		if(closingBalance > 0){
			cbMap = [:];
			cbMap.put("debit", closingBalance);
			cbMap.put("credit", BigDecimal.ZERO);
			tempMap = [:];
			tempMap.putAll(cbMap);
			advDetailsMap.put("closingBalance", tempMap);
		}
		else{
			cbMap = [:];
			cbMap.put("debit", BigDecimal.ZERO);
			cbMap.put("credit", closingBalance.negate());
			tempMap = [:];
			tempMap.putAll(cbMap);
			advDetailsMap.put("closingBalance", tempMap);
		}
		
		tempPmntAppMap = [:];
		tempPmntAppMap.putAll(advDetailsMap);
		
		partyPaymentsMap.put(partyId, tempPmntAppMap);
	}
	Debug.log("partyPaymentsMap============="+partyPaymentsMap);
	context.partyPaymentsMap = partyPaymentsMap;
	//context.invoiceDetailsMap = invoiceDetailsMap;
	//context.paymentDetailsMap = paymentDetailsMap;
	
	
	
	//adsfadsfl;
	

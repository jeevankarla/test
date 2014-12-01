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
		partyPaymentsList = EntityUtil.filterByAnd(paymentList, [partyIdTo : partyId]);
		
		duringPeriodPayments = [];
		duringPeriodPayments.addAll(partyPaymentsList);
		
		duringPaymentApplications = [];
		duringPaymentApplications.addAll(paymentApplicationList);
		
		advDetailsMap = [:];
		
		// Calculate Opening Balance
		oldPmnts = EntityUtil.filterByCondition(partyPaymentsList, EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, fromDate));
		oldPmntIdsList = EntityUtil.getFieldListFromEntityList(oldPmnts, "paymentId", true);
		
		amountPaid = BigDecimal.ZERO;
		for(j=0; j<oldPmnts.size(); j++){
			amountPaid = amountPaid.add((oldPmnts.get(j)).getBigDecimal("amount"));
		}
		duringPeriodPayments.remove(oldPmnts);
		
		condList = [];
		condList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, oldPmntIdsList));
		condList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, fromDate));
		condExpr = EntityCondition.makeCondition(condList, EntityOperator.AND);
		oldPmntAppList = EntityUtil.filterByCondition(paymentApplicationList, condExpr);
		duringPaymentApplications.remove(oldPmntAppList);
		
		amountApplied = BigDecimal.ZERO;
		for(j=0; j<oldPmntAppList.size(); j++){
			amountApplied = amountApplied.add((oldPmntAppList.get(j)).getBigDecimal("amountApplied"));
		}
		openingBalance = amountPaid.subtract(amountApplied);
		
		obMap = [:];
		obMap.put("debit", openingBalance);
		obMap.put("credit", BigDecimal.ZERO);
		
		advDetailsMap.put("openingBalance", obMap);
		
		// Calculate During period credit and debit
		
		duringAmountPaid = BigDecimal.ZERO;
		for(j=0; j<oldPmnts.size(); j++){
			duringAmountPaid = duringAmountPaid.add((duringPeriodPayments.get(j)).getBigDecimal("amount"));
		}
		duringAmountApplied = BigDecimal.ZERO;
		for(j=0; j<duringPaymentApplications.size(); j++){
			duringAmountApplied = duringAmountApplied.add((duringPaymentApplications.get(j)).getBigDecimal("amountApplied"));
		}
		advDetailsMap.put("openingBalance", UtilMisc.toMap("debit", duringAmountPaid, "credit", duringAmountApplied));
		
		// Calculate Closing Balance
		totalDebit = openingBalance.add(duringAmountPaid);
		totalCredit = duringAmountApplied;
		
		closingBalance = totalDebit.subtract(totalCredit);
		if(closingBalance > 0){
			advDetailsMap.put("duringPeriod", UtilMisc.toMap("debit", closingBalance, "credit", BigDecimal.ZERO));
		}
		else{
			advDetailsMap.put("closingBalance", UtilMisc.toMap("debit", BigDecimal.ZERO, "credit", closingBalance));
		}
		
		tempPmntAppMap = [:];
		tempPmntAppMap.putAll(advDetailsMap);
		
		partyPaymentsMap.put(partyId, tempPmntAppMap);
	}
	Debug.log("partyPaymentsMap============="+partyPaymentsMap);
	context.partyPaymentsMap = partyPaymentsMap;
	//context.invoiceDetailsMap = invoiceDetailsMap;
	//context.paymentDetailsMap = paymentDetailsMap;
	
	
	
	adsfadsfl;
	

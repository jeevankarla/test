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
	paymentTypeId = parameters.paymentTypeId;
	
	paymentType = delegator.findOne("PaymentType", [paymentTypeId : paymentTypeId], false);
	context.paymentType = paymentType;
	
	paymentGlAccountTypeMap = delegator.findList("PaymentGlAccountTypeMap", EntityCondition.makeCondition([paymentTypeId : paymentTypeId]), null, null, null, false);
	glList = EntityUtil.getFieldListFromEntityList(paymentGlAccountTypeMap, "glAccountId", true);
	
	GlAccount = delegator.findOne("GlAccount", [glAccountId : glList.get(0)], false);
	context.GlAccount = GlAccount;
	
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
	
	// Get Applied Invoice Details
	
	allPmntIdsList = [];
	allPmntIdsList.addAll(paymentIdsList);
	allPmntIdsList.addAll(toPaymentIdsList);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIdsList));
	List paymentInvApplicationList = delegator.findList("InvoiceAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","invoiceItemSeqId","invoiceItemTypeId","quantity","amount","description", "invoiceDate","invoiceMessage"), UtilMisc.toList("invoiceDate"), null, false);
	
	invoiceDetailsMap = [:];
	for(i=0; i<invoiceIdsList.size(); i++){
		invoiceId = invoiceIdsList.get(i);
		filteredInvoiceList = EntityUtil.filterByAnd(paymentInvApplicationList, [invoiceId : invoiceId]);
		invoiceDetailsMap.put(invoiceId, filteredInvoiceList);
	}
	
	// Get Applied Payment Details
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, allPmntIdsList));
	List appliedPmntList = delegator.findList("PaymentAndType", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("paymentId","paymentTypeId","paymentMethodId","paymentDate","paymentRefNum","amount", "finAccountTransId", "comments"), UtilMisc.toList("paymentDate"), null, false);
	
	paymentDetailsMap = [:];
	for(i=0; i<appliedPmntList.size(); i++){
		appPmntId = (appliedPmntList.get(i)).get("paymentId");
		filteredPmntList = EntityUtil.filterByAnd(appliedPmntList, [paymentId : appPmntId]);
		paymentDetailsMap.put(appPmntId, filteredPmntList);
	}
	
	partyPaymentsMap = [:];
	partyPaymentDetailsMap = [:];
	for(i=0; i<partyIdsList.size(); i++){
		partyId = partyIdsList.get(i);
		partyPaymentsList = EntityUtil.filterByAnd(paymentList, [partyIdTo : partyId]);
		partyPmntIdsList = EntityUtil.getFieldListFromEntityList(partyPaymentsList, "paymentId", true);
		
		advDetailsMap = [:];
		
		// Calculate Opening Balance
		oldPmnts = EntityUtil.filterByCondition(partyPaymentsList, EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, fromDate));
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
		
		//durationPmntApps
		paymentAndApplicationsList = [];
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		paymentApplicationMap = [:]
		for(j=0; j<durationPmnts.size(); j++){
			partyPayment = durationPmnts.get(j);
			partyPaymentId = partyPayment.get("paymentId");
			
			dateWisePmntApplications = [:];
			dateWisePmntApplications.put("date", partyPayment.getTimestamp("paymentDate"));
			dateWisePmntApplications.put("paymentId", partyPayment.get("paymentId"));
			
			tempDateWisePmntMap = [:];
			tempDateWisePmntMap.putAll(dateWisePmntApplications);
			
			paymentAndApplicationsList.add(tempDateWisePmntMap);
			
		}
		
		
		for(j=0; j<durationPmntApps.size(); j++){
			dateWisePmntApplications = [:];
			durationPmntApp = durationPmntApps.get(j);
			if(UtilValidate.isNotEmpty(durationPmntApp.get("toPaymentId"))){
				dateWisePmntApplications.put("date", (paymentDetailsMap.get(durationPmntApp.get("toPaymentId"))).getTimestamp("paymentDate"));
				dateWisePmntApplications.put("paymentId", durationPmntApp.get("toPaymentId"));
			}
			if(UtilValidate.isNotEmpty(durationPmntApp.get("invoiceId"))){
				dateWisePmntApplications.put("date", ((invoiceDetailsMap.get(durationPmntApp.get("invoiceId"))).get(0)).get("invoiceDate"));
				dateWisePmntApplications.put("invoiceId", durationPmntApp.get("invoiceId"));
			}
			tempDateWisePmntMap = [:];
			tempDateWisePmntMap.putAll(dateWisePmntApplications);
			
			paymentAndApplicationsList.add(tempDateWisePmntMap);
		}
		paymentAndApplicationsList=UtilMisc.sortMaps(paymentAndApplicationsList, UtilMisc.toList("date"));
		
		tempPmntAppDetList = [];
		tempPmntAppDetList.addAll(paymentAndApplicationsList);
		
		partyPaymentDetailsMap.put(partyId, tempPmntAppDetList);
		
	}
	
	context.partyPaymentsMap = partyPaymentsMap;
	context.partyPaymentDetailsMap = partyPaymentDetailsMap;
	context.invoiceDetailsMap = invoiceDetailsMap;
	context.paymentDetailsMap = paymentDetailsMap;
	
	
	

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
	import org.ofbiz.party.party.PartyHelper;
	
	userLogin= context.userLogin;
	
	fromDateStr = parameters.fromDate;
	thruDateStr = parameters.thruDate;
	partyId = parameters.partyId;
	
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
	if(UtilValidate.isNotEmpty(glList)){
	GlAccount = delegator.findOne("GlAccount", [glAccountId : glList.get(0)], false);
	context.GlAccount = GlAccount;
	}
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("PMNT_SENT", "PMNT_CONFIRMED")));
	if(UtilValidate.isNotEmpty(partyId)){
		conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
	}
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
	List paymentInvApplicationList = delegator.findList("InvoiceAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("invoiceDate"), null, false);
	
	invoiceDetailsMap = [:];
	for(i=0; i<invoiceIdsList.size(); i++){
		invoiceId = invoiceIdsList.get(i);
		filteredInvoiceList = EntityUtil.filterByAnd(paymentInvApplicationList, [invoiceId : invoiceId]);
		invoiceDetailsMap.put(invoiceId, filteredInvoiceList);
	}
	
	// Get Applied Payment Details
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, allPmntIdsList));
	List appliedPmntList = delegator.findList("PaymentAndType", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("paymentDate"), null, false);
	finAccTransSeqIds = EntityUtil.getFieldListFromEntityList(appliedPmntList, "finAccountTransId", true);
	paymentDetailsMap = [:];
	for(i=0; i<appliedPmntList.size(); i++){
		appPmntId = (appliedPmntList.get(i)).get("paymentId");
		filteredPmntList = EntityUtil.filterByAnd(appliedPmntList, [paymentId : appPmntId]);
		paymentDetailsMap.put(appPmntId, filteredPmntList);
	}
	
	// Get Trans Sequence Id
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("finAccountTransId", EntityOperator.IN, finAccTransSeqIds));
	List finAccTransList = delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("finAccountTransId", "transSequenceId"), UtilMisc.toList("finAccountTransId"), null, false);
	
	finTransSeqMap = [:];
	for(i=0; i<finAccTransList.size(); i++){
		finTransSeq = finAccTransList.get(i);
		finTransSeqMap.put(finTransSeq.get("finAccountTransId"), finTransSeq.get("transSequenceId"));
	}
	context.finTransSeqMap = finTransSeqMap;
	
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
		obMap.put("debit", 0);
		obMap.put("credit", 0);
		if(openingBalance>0){
			obMap.put("debit", openingBalance);
		}else{
			obMap.put("credit", openingBalance);
		}
		
		
		
		
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
		
     	obMap.put("totalDebit", totalDebit);
		obMap.put("totalCredit", totalCredit);
		
		tempMap = [:];
		tempMap.putAll(obMap);
		advDetailsMap.put("openingBalance", tempMap);
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
			if(UtilValidate.isEmpty(partyPayment.get("paymentDate"))){
				dateWisePmntApplications.put("date", "");
			}
			else{
				dateWisePmntApplications.put("date", partyPayment.get("paymentDate"));
			}
			dateWisePmntApplications.put("paymentId", partyPayment.get("paymentId"));
			
			tempDateWisePmntMap = [:];
			tempDateWisePmntMap.putAll(dateWisePmntApplications);
			
			paymentAndApplicationsList.add(tempDateWisePmntMap);
			
		}
		
		
		for(j=0; j<durationPmntApps.size(); j++){
			dateWisePmntApplications = [:];
			durationPmntApp = durationPmntApps.get(j);
			if(UtilValidate.isNotEmpty(durationPmntApp.get("toPaymentId"))){
				toPaymentId = durationPmntApp.get("toPaymentId");
				pmntDetails = (paymentDetailsMap.get(toPaymentId)).get(0);
				dateWisePmntApplications.put("date", pmntDetails.get("paymentDate"));
				dateWisePmntApplications.put("paymentId", toPaymentId);
			}
			if(UtilValidate.isNotEmpty(durationPmntApp.get("invoiceId"))){
				toInvoiceId = durationPmntApp.get("invoiceId");
				invoiceDetails = (invoiceDetailsMap.get(toInvoiceId)).get(0);
				dateWisePmntApplications.put("date", invoiceDetails.get("invoiceDate"));
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
	
	// Advaces Csv
	AdvancesCsvList=[];
	for(Map.Entry entryParty : partyPaymentsMap.entrySet()){
		partyId = entryParty.getKey();
		paymentDetails = entryParty.getValue();
		tempMap=[:];
		tempMap.partyId=partyId;
		tempMap.name = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);
		openingBalance=paymentDetails.get("openingBalance");
		duringPeriod = paymentDetails.get("duringPeriod");
		closingBalance = paymentDetails.get("closingBalance");
		openingDebit=openingBalance.get("debit");
		openingCredit=openingBalance.get("credit");
		duringPeriodDebit=duringPeriod.get("debit");
		duringPeriodCredit=duringPeriod.get("credit");
		closingDebit=closingBalance.get("debit");
		closingCredit=closingBalance.get("credit");
		tempMap.openingDebit=openingDebit;
		tempMap.openingCredit=openingCredit;
		tempMap.duringPeriodDebit=duringPeriodDebit;
		tempMap.duringPeriodCredit=duringPeriodCredit;
		tempMap.closingDebit=closingDebit;
		tempMap.closingCredit=closingCredit;
		AdvancesCsvList.add(tempMap);
	}
context.AdvancesCsvList=AdvancesCsvList;	

//Subledger Csv
subledgerCsvList=[];
for(Map.Entry entryParty :partyPaymentDetailsMap.entrySet()){
	partyId = entryParty.getKey();
	paymentDetailsList=entryParty.getValue();
	name=org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);
	partyOpeningBalance=partyPaymentsMap.get(partyId);
	openingDebit=partyOpeningBalance.get("openingBalance").get("debit");
	openingCredit=partyOpeningBalance.get("openingBalance").get("credit");
	paymentDetailsList.each{paymentDetail->
		if(UtilValidate.isNotEmpty(paymentDetail.paymentId)){
			tempMap=[:];
			paymentId=paymentDetail.paymentId;
			paymentInfo=(paymentDetailsMap.get(paymentId)).get(0);
			paymentDate=UtilDateTime.toDateString(paymentInfo.get("paymentDate"), "dd-MM-yyyy");
			particulars="";
			paymentRefNum="";
			paymentMethodId="";
			invoiceId="";
			finAccountTransId="";
			debit=0;
			credit=0;
			if(UtilValidate.isNotEmpty(paymentInfo.get("paymentRefNum"))){
				paymentRefNum=paymentInfo.get("paymentRefNum");
			}
			if(UtilValidate.isNotEmpty(paymentInfo.get("comments"))){
				particulars=paymentInfo.get("comments")+" "+paymentRefNum;
			}else{
			  if(UtilValidate.isNotEmpty(paymentInfo.get("paymentMethodTypeId"))){
				  paymentMothedType=delegator.findOne("PaymentMethodType",[paymentMethodTypeId:paymentInfo.get("paymentMethodTypeId")],false);
				  particulars=paymentMothedType.description+" "+paymentRefNum;
			  }else{
			      particulars=paymentRefNum;
			  }
			}
			if(UtilValidate.isNotEmpty(paymentInfo.get("paymentMethodId"))){
				paymentMethod = delegator.findOne("PaymentMethod",[paymentMethodId:paymentInfo.get("paymentMethodId")],false);
				paymentMethodId=paymentMethod.description;
			}
			if(UtilValidate.isNotEmpty(paymentInfo.get("finAccountTransId"))){
				finAccountTransId=finTransSeqMap.get(paymentInfo.get("finAccountTransId"));
			}
			if((paymentInfo.get("paymentTypeId")).indexOf("PAYOUT") != -1){
				debit=paymentInfo.get("amount");
			}else{
			   credit=paymentInfo.get("amount");
			}
			tempMap.date=paymentDate;
			tempMap.parytId=partyId;
			tempMap.name=name;
			tempMap.openingDebit=openingDebit;
			tempMap.openingCredit=openingCredit;
			tempMap.particulars=particulars;
			tempMap.invoiceId=invoiceId;
			tempMap.paymentId=paymentId;
			tempMap.paymentMethodId=paymentMethodId;
			tempMap.finAccountTransId=finAccountTransId;
			tempMap.debit=debit;
			tempMap.credit=credit;
			subledgerCsvList.add(tempMap);
		}
		if(UtilValidate.isNotEmpty(paymentDetail.invoiceId)){
			invoicesList=invoiceDetailsMap.get(paymentDetail.invoiceId);
			invoicesList.each{invoice->
				tempMap=[:];
				particulars="";
				quantity=1;
				debit=0;
				credit=0;
				paymentId="";
				paymentMethodId="";
				finAccountTransId="";
				invoiceId=invoice.invoiceId;
				invoiceDate=UtilDateTime.toDateString(invoice.invoiceDate, "dd-MM-yyyy");
				if(UtilValidate.isNotEmpty(invoice.invoiceTypeId)){
					invoiceType=delegator.findOne("InvoiceType",[invoiceTypeId:invoice.invoiceTypeId],false);
					particulars=invoiceType.description;
				}else{
				particulars=invoice.description;
				}
				if(UtilValidate.isNotEmpty(invoice.quantity)){
					quantity=invoice.quantity;
				}
				credit=credit+(invoice.amount*quantity);
				tempMap.date=invoiceDate;
				tempMap.parytId=partyId;
				tempMap.name=name;
				tempMap.particulars=particulars;
				tempMap.openingDebit=openingDebit;
				tempMap.openingCredit=openingCredit;
				tempMap.invoiceId=invoiceId;
				tempMap.paymentId=paymentId;
				tempMap.paymentMethodId=paymentMethodId;
				tempMap.finAccountTransId=finAccountTransId;
				tempMap.debit=debit;
				tempMap.credit=credit;
				subledgerCsvList.add(tempMap);
			}
		}
	}
}

context.subledgerCsvList=subledgerCsvList;










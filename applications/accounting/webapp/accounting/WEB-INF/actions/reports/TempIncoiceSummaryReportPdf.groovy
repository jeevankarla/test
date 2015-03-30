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
	parentTypeId = parameters.parentTypeId;
	invoiceTypeId = parameters.invoiceTypeId;
	glAccountClassId = parameters.glAccountClassId;
	
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
	intOrgName = PartyHelper.getPartyName(delegator, partyId, false);
	
	context.intOrgName = intOrgName;
	context.partyId = partyId;
	context.parentTypeId = parentTypeId;
	context.fromDate = fromDate;
	context.thruDate = thruDate;
	List conditionList=[];
	
	invTypeList = delegator.findList("InvoiceType", null, null, null, null, false );
	
	conditionList.clear();
	if(parentTypeId){
		conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentTypeId));
	}
	if(invoiceTypeId){
		conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceTypeId));
	}
	invoiceTypeCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceTypeList = EntityUtil.filterByCondition(invTypeList, invoiceTypeCondition);
	invoiceTypeIdsList = EntityUtil.getFieldListFromEntityList(invoiceTypeList, "invoiceTypeId", true);
	
	statusItemList = delegator.findList("StatusItem", null, UtilMisc.toSet("description","statusId"), null, null, false );
	
	conditionList.clear();
	if(parentTypeId == "SALES_INVOICE"){
		conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
	}
	if(parentTypeId == "PURCHASE_INVOICE"){
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	}
	
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.IN, invoiceTypeIdsList));
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	invoiceItemCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceItemList = delegator.findList("InvoiceAndItem", invoiceItemCondition, UtilMisc.toSet("invoiceTypeId","invoiceDate","invoiceId","invoiceItemTypeId","statusId"), ["invoiceTypeId","invoiceDate","statusId","invoiceItemTypeId"], null, false );
	
	invoiceItemTypeIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemList, "invoiceItemTypeId", true);
	invoiceIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemList, "invoiceId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, invoiceItemTypeIdsList));
	invoiceItemTypeCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceItemTypeList = delegator.findList("InvoiceItemType", invoiceItemTypeCondition, null, null, null, false );
	
	glAccountIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemTypeList, "defaultGlAccountId", true);
	
	conditionList.clear();
	if(glAccountClassId){
		conditionList.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.EQUALS, glAccountClassId));
	}
	acctCondition2=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	acctgList2 = delegator.findList("GlAccount", acctCondition2, UtilMisc.toSet("glAccountId","accountName"), ["glAccountId"], null, false );
	glIdsList = EntityUtil.getFieldListFromEntityList(acctgList2, "glAccountId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIdsList));
	conditionList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, glAccountIdsList));
	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	acctEntryCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	
	requiredFields = UtilMisc.toSet("isPosted", "glFiscalTypeId","acctgTransTypeId", "transactionDate","invoiceId", "glAccountId");
	requiredFields1 = UtilMisc.toSet("acctgTransId", "debitCreditFlag","amount", "organizationPartyId","origAmount");
	requiredFields.addAll(requiredFields1);
	
	
	
	acctgTransEntryList = delegator.findList("AcctgTransAndEntries", acctEntryCondition, requiredFields, ["glAccountId","invoiceId","acctgTransId"], null, false );
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("acctgTransTypeId", EntityOperator.EQUALS, "JOURNAL"));
	conditionList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, glAccountIdsList));
	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	acctJournalCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	acctgJournalList = delegator.findList("AcctgTransAndEntries", acctJournalCondition, requiredFields, ["glAccountId"], null, false );
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, glIdsList));
	acctCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	acctgList = delegator.findList("GlAccount", acctCondition, UtilMisc.toSet("glAccountId","accountName"), null, null, false );
	
	invoiceDatesList = EntityUtil.getFieldListFromEntityList(invoiceItemList, "invoiceDate", true);
	
	dateList = [];
	for(d=0;d<invoiceDatesList.size();d++){
		
		invoiceDate =  invoiceDatesList[d];
		
		invoiceStartDate = UtilDateTime.getDayStart(invoiceDate, timeZone, locale);
		
		dateList.add(invoiceStartDate);
		
	}
	finalInvoiceDates = [];
	for(e=0;e<dateList.size();e++){
		tempInvoiceDate =  dateList[e];
		if(finalInvoiceDates.contains(tempInvoiceDate)){
		}else{
		finalInvoiceDates.add(tempInvoiceDate);
		}
	}
	
	
	invoiceFinalList = [];
	glFinalList = [];
	
	glTotMap = [:];
	invTotMap = [:];
	
	totPostedDrTotal = 0;
	totUnPostedDrTotal = 0;
	totPostedCrTotal = 0;
	totUnPostedDrTotal = 0;
	
	totJpostedDrTotal = 0;
	totJunPostedDrTotal = 0;
	totJpostedCrTotal = 0;
	totJunPostedDrTotal = 0;
	
	
	for(a=0;a<glIdsList.size();a++){
		glAccountId = glIdsList[a];
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
		
		Condition4=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		filteredacctgList = EntityUtil.filterByCondition(acctgList, Condition4);
		
		accountName = filteredacctgList[0].get("accountName");
		
		postedDrTotal = 0;
		unPostedDrTotal = 0;
		postedCrTotal = 0;
		unPostedDrTotal = 0;
		
		JpostedDrTotal = 0;
		JunPostedDrTotal = 0;
		JpostedCrTotal = 0;
		JunPostedDrTotal = 0;
		
		for(c=0;c<finalInvoiceDates.size();c++){
			
			eachInvoiceDate =  finalInvoiceDates[c];
			
			eachInvoiceDateEnd = UtilDateTime.getDayEnd(eachInvoiceDate, timeZone, locale);
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, eachInvoiceDate),EntityOperator.AND,
				EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, eachInvoiceDateEnd)));
			conditionList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIdsList));
			Condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			filteredAcctgTransEntryList = EntityUtil.filterByCondition(acctgTransEntryList, Condition1);
			
			conditionList.clear();
			
			conditionList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, eachInvoiceDate),EntityOperator.AND,
				EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, eachInvoiceDateEnd)));
			Condition2=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			filteredAcctgJournalList = EntityUtil.filterByCondition(acctgJournalList, Condition2);
			
			for(b=0;b<filteredAcctgTransEntryList.size();b++){
				eachFilteredAcctgTransEntry = filteredAcctgTransEntryList[b];
				
				
				transactionDate = eachFilteredAcctgTransEntry.get("transactionDate");
				invoiceId = eachFilteredAcctgTransEntry.get("invoiceId");
				isPosted = eachFilteredAcctgTransEntry.get("isPosted");
				debitCreditFlag = eachFilteredAcctgTransEntry.get("debitCreditFlag");
				postedDrAmount = 0;
				unPostedDrAmount = 0;
				postedCrAmount = 0;
				unPostedCrAmount = 0;
				amount = eachFilteredAcctgTransEntry.get("amount");
				
				
				origAmount = eachFilteredAcctgTransEntry.get("origAmount");
				
				if((isPosted == "Y") && (debitCreditFlag == "D")){
					
					postedDrAmount = amount;
					
				}else if((isPosted == "N") && (debitCreditFlag == "D")){
				
					unPostedDrAmount = amount;
				}else if((isPosted == "Y") && (debitCreditFlag == "C")){
				
					postedCrAmount = amount;
				
				}else{
				
					unPostedCrAmount = amount;
				}
				
				invMap = [:];
				invMap.put("GlAccountId", glAccountId);
				invMap.put("InvoiceId", invoiceId);
				invMap.put("accountTransId", "");
				invMap.put("InvoiceDate", transactionDate);
				invMap.put("InvoicepostedDrAmount", postedDrAmount);
				invMap.put("AcctpostedDrAmount", 0);
				invMap.put("InvoiceunPostedDrAmount", unPostedDrAmount);
				invMap.put("AcctunPostedDrAmount", 0);
				invMap.put("InvoicepostedCrAmount", postedCrAmount);
				invMap.put("AcctpostedCrAmount", 0);
				invMap.put("InvoiceunPostedCrAmount", unPostedCrAmount);
				invMap.put("AcctunPostedCrAmount", 0);
				
				
				tempInvMap = [:];
				tempInvMap.putAll(invMap);
				invoiceFinalList.addAll(tempInvMap);
				
				postedDrTotal = postedDrTotal + postedDrAmount;
				unPostedDrTotal = unPostedDrTotal + unPostedDrAmount;
				postedCrTotal = postedCrTotal + postedCrAmount;
				unPostedDrTotal = unPostedDrTotal + unPostedCrAmount;
				
			}
			
			for(d=0;d<filteredAcctgJournalList.size();d++){
				eachFilteredAcctgJournal = filteredAcctgJournalList[d];
				
				acctgTransId = eachFilteredAcctgJournal.get("acctgTransId");
				transactionDate = eachFilteredAcctgTransEntry.get("transactionDate");
				isPosted = eachFilteredAcctgJournal.get("isPosted");
				debitCreditFlag = eachFilteredAcctgJournal.get("debitCreditFlag");
				postedDrAmount = 0;
				unPostedDrAmount = 0;
				postedCrAmount = 0;
				unPostedCrAmount = 0;
				amount = eachFilteredAcctgJournal.get("amount");
				origAmount = eachFilteredAcctgJournal.get("origAmount");
				
				if((isPosted == "Y") && (debitCreditFlag == "D")){
					
					postedDrAmount = amount;
					
				}else if((isPosted == "N") && (debitCreditFlag == "D")){
				
					unPostedDrAmount = amount;
				}else if((isPosted == "Y") && (debitCreditFlag == "C")){
				
					postedCrAmount = amount;
				
				}else{
				
					unPostedCrAmount = amount;
				}
				
				invMap = [:];
				invMap.put("GlAccountId", glAccountId);
				invMap.put("InvoiceId", "");
				invMap.put("accountTransId", acctgTransId);
				invMap.put("InvoiceDate", transactionDate);
				
				invMap.put("InvoicepostedDrAmount", 0);
				invMap.put("AcctpostedDrAmount", postedDrAmount);
				
				invMap.put("InvoiceunPostedDrAmount", 0);
				invMap.put("AcctunPostedDrAmount", unPostedDrAmount);
				
				invMap.put("InvoicepostedCrAmount", 0);
				invMap.put("AcctpostedCrAmount", postedCrAmount);
				
				invMap.put("InvoiceunPostedCrAmount", 0);
				invMap.put("AcctunPostedCrAmount", unPostedCrAmount);
				
				
				tempInvMap = [:];
				tempInvMap.putAll(invMap);
				invoiceFinalList.addAll(tempInvMap);
				
				JpostedDrTotal = JpostedDrTotal + postedDrAmount;
				JunPostedDrTotal = JunPostedDrTotal + unPostedDrAmount;
				JpostedCrTotal = JpostedCrTotal + postedCrAmount;
				JunPostedDrTotal = JunPostedDrTotal + unPostedCrAmount;
				
			}
			
		}
		
		totPostedDrTotal = totPostedDrTotal + postedDrTotal;
		totUnPostedDrTotal = totUnPostedDrTotal + unPostedDrTotal;
		totPostedCrTotal = totPostedCrTotal + postedCrTotal;
		totUnPostedDrTotal = totUnPostedDrTotal + unPostedDrTotal;
		
		totJpostedDrTotal = totJpostedDrTotal + JpostedDrTotal;
		totJunPostedDrTotal = totJunPostedDrTotal + JunPostedDrTotal;
		totJpostedCrTotal = totJpostedCrTotal + JpostedCrTotal;
		totJunPostedDrTotal = totJunPostedDrTotal + JunPostedDrTotal;
			
		glMap = [:];
		glMap.put("glAccountId", glAccountId);
		glMap.put("glAccountIdDes", accountName);
		glMap.put("postedDrAmount", postedDrTotal);
		glMap.put("unPostedDrAmount", unPostedDrTotal);
		glMap.put("postedCrAmount", postedCrTotal);
		glMap.put("unPostedCrAmount", unPostedDrTotal);
		
		glMap.put("JpostedDrAmount", JpostedDrTotal);
		glMap.put("JunPostedDrAmount", JunPostedDrTotal);
		glMap.put("JpostedCrAmount", JpostedCrTotal);
		glMap.put("JunPostedCrAmount", JunPostedDrTotal);
		
		invSubMap = [:];
		invSubMap.put("GlAccountId", "SUBTOTAL");
		
		
		invSubMap.put("InvoicepostedDrAmount", postedDrTotal);
		invSubMap.put("AcctpostedDrAmount", JpostedDrTotal);
		
		invSubMap.put("InvoiceunPostedDrAmount", unPostedDrTotal);
		invSubMap.put("AcctunPostedDrAmount", JunPostedDrTotal);
		
		invSubMap.put("InvoicepostedCrAmount", postedCrTotal);
		invSubMap.put("AcctpostedCrAmount", JpostedCrTotal);
		
		invSubMap.put("InvoiceunPostedCrAmount", unPostedDrTotal);
		invSubMap.put("AcctunPostedCrAmount", JunPostedDrTotal);
		
		invTotMap.put("GlAccountId", "TOTAL");
		invTotMap.put("InvoicepostedDrAmount", totPostedDrTotal);
		invTotMap.put("AcctpostedDrAmount", totJpostedDrTotal);
		invTotMap.put("InvoiceunPostedDrAmount", totUnPostedDrTotal);
		invTotMap.put("AcctunPostedDrAmount", totJunPostedDrTotal);
		invTotMap.put("InvoicepostedCrAmount", totPostedCrTotal);
		invTotMap.put("AcctpostedCrAmount", totJpostedCrTotal);
		invTotMap.put("InvoiceunPostedCrAmount", totUnPostedDrTotal);
		invTotMap.put("AcctunPostedCrAmount", totJunPostedDrTotal);
		
		
		glTotMap.put("postedDrAmount", totPostedDrTotal);
		glTotMap.put("unPostedDrAmount", totUnPostedDrTotal);
		glTotMap.put("postedCrAmount", totPostedCrTotal);
		glTotMap.put("unPostedCrAmount", totUnPostedDrTotal);
		glTotMap.put("JpostedDrAmount", totJpostedDrTotal);
		glTotMap.put("JunPostedDrAmount", totJunPostedDrTotal);
		glTotMap.put("JpostedCrAmount", totJpostedCrTotal);
		glTotMap.put("JunPostedCrAmount", totJunPostedDrTotal);
		
		tempGlMap = [:];
		tempGlMap.putAll(glMap);
		glFinalList.addAll(tempGlMap);
		
		if( ((postedDrTotal + JpostedDrTotal) == 0) && ((postedDrTotal + JpostedDrTotal) == 0) && ((postedDrTotal + JpostedDrTotal) == 0) && ((postedDrTotal + JpostedDrTotal) == 0) ){
			
		}else{
			tempGlTotMap = [:];
			tempGlTotMap.putAll(invSubMap);
			invoiceFinalList.addAll(tempGlTotMap);
		}
		
		
		
	}
	
	tempGltotMap = [:];
	tempGltotMap.putAll(glTotMap);
	glFinalList.addAll(tempGltotMap);
	
	
	tempTotInvMap = [:];
	tempTotInvMap.putAll(invTotMap);
	invoiceFinalList.addAll(tempTotInvMap);
	
	context.glFinalList = glFinalList;
	context.invoiceFinalList = invoiceFinalList;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
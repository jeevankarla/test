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
	invoiceItemList = delegator.findList("InvoiceAndItem", invoiceItemCondition, null, ["invoiceTypeId","invoiceDate","statusId","invoiceItemTypeId"], null, false );
	
	
	invoiceItemTypeIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemList, "invoiceItemTypeId", true);
	invoiceIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemList, "invoiceId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, invoiceItemTypeIdsList));
	invoiceItemTypeCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceItemTypeList = delegator.findList("InvoiceItemType", invoiceItemTypeCondition, null, null, null, false );
	
	glAccountIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemTypeList, "defaultGlAccountId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIdsList));
	conditionList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, glAccountIdsList));
	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	acctEntryCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	acctgTransEntryList = delegator.findList("AcctgTransAndEntries", acctEntryCondition, null, null, null, false );
	
	finalInvoiceItemList = [];
	glSummaryFinalList = [];
	grandPostedDrAmount = 0;
	grandUnPostedDrAmount = 0;
	grandPostedCrAmount = 0;
	grandUnPostedCrAmount = 0;
	tempGrandTotMap = [:];
	invItemTypeTotMap = [:];
	invItemTypeMap = [:];
	
	for(a=0;a<invoiceItemTypeIdsList.size();a++){
		invoiceItemTypeId =  invoiceItemTypeIdsList[a];
		Debug.log("invoiceItemTypeId===2222======="+invoiceItemTypeId);
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, invoiceItemTypeId));
		filteredInvoiceItemCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		filteredInvoiceItemList = EntityUtil.filterByCondition(invoiceItemList, filteredInvoiceItemCondition);
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, invoiceItemTypeId));
		filteredInvoiceItemTypeCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		filteredInvoiceItemTypeList = EntityUtil.filterByCondition(invoiceItemTypeList, filteredInvoiceItemTypeCondition);
		
		invoiceItemTypeName = filteredInvoiceItemTypeList[0].get("description");
		
		filteredGlAccountIdsList = EntityUtil.getFieldListFromEntityList(filteredInvoiceItemTypeList, "defaultGlAccountId", true);
		
		invoiceDatesList = EntityUtil.getFieldListFromEntityList(filteredInvoiceItemList, "invoiceDate", true);
		
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
		totPostedDrAmount = 0;
		totUnPostedDrAmount = 0;
		totPostedCrAmount = 0;
		totUnPostedCrAmount = 0;
		
		totMap = [:];
		
		for(f=0;f<finalInvoiceDates.size();f++){
			eachInvoiceDate =  finalInvoiceDates[f];
			
			eachInvoiceDateEnd = UtilDateTime.getDayEnd(eachInvoiceDate, timeZone, locale);
			
			
			
			conditionList.clear();
			
			
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, eachInvoiceDate),EntityOperator.AND,
				EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, eachInvoiceDateEnd)));
			
			dateInvoiceItemCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			dateFilteredInvoiceItemList = EntityUtil.filterByCondition(filteredInvoiceItemList, dateInvoiceItemCondition);
			
			statusIdsList = EntityUtil.getFieldListFromEntityList(dateFilteredInvoiceItemList, "statusId", true);
			
			totDayPostedDrAmount = 0;
			totDayUnPostedDrAmount = 0;
			totDayPostedCrAmount = 0;
			totDayUnPostedCrAmount = 0;
			
			dayMap = [:];
			
			for(g=0;g<statusIdsList.size();g++){
				statusId =  statusIdsList[g];
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
				statusInvoiceItemCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				statusFilteredInvoiceItemList = EntityUtil.filterByCondition(dateFilteredInvoiceItemList, statusInvoiceItemCondition);
				
				totStPostedDrAmount = 0;
				totStUnPostedDrAmount = 0;
				totStPostedCrAmount = 0;
				totStUnPostedCrAmount = 0;
				
				statusMap = [:];
				prevInvoiceId = null;
				
				testInvoiceList = [];
				
				for(h=0;h<statusFilteredInvoiceItemList.size();h++){
					eachInvoiceItem =  statusFilteredInvoiceItemList[h];
					invoiceId = eachInvoiceItem.invoiceId;
					
					invoiceDate = eachInvoiceItem.invoiceDate;
					statusId = eachInvoiceItem.statusId;
					invoiceTypeId = eachInvoiceItem.invoiceTypeId;
					
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
					statCon = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					filteredStatusItemList = EntityUtil.filterByCondition(statusItemList, statCon);
					
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceTypeId));
					invCon = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					invType = delegator.findList("InvoiceType", null, null, null, null, false );
					
					invoiceType = invType[0].get("description");
					statusDes = filteredStatusItemList[0].get("description");
					
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
					conditionList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, filteredGlAccountIdsList));
					condition2 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					
					accEnteryList2 = EntityUtil.filterByCondition(acctgTransEntryList, condition2);
					
					if((testInvoiceList.contains(invoiceId))){
					}else{	
						testInvoiceList.add(invoiceId);
						for(c=0;c<accEnteryList2.size();c++){
							eachAccEntery = accEnteryList2[c];
							glAccountId = eachAccEntery.get("glAccountId");
							isPosted = eachAccEntery.get("isPosted");
							debitCreditFlag = eachAccEntery.get("debitCreditFlag");
							postedDrAmount = 0;
							unPostedDrAmount = 0;
							postedCrAmount = 0;
							unPostedCrAmount = 0;
							
							amount = eachAccEntery.get("amount");
							
							origAmount = eachAccEntery.get("origAmount");
							
							if((isPosted == "Y") && (debitCreditFlag == "D")){
								
								postedDrAmount = amount;
								
							}else if((isPosted == "N") && (debitCreditFlag == "D")){
							
								unPostedDrAmount = amount;
							}else if((isPosted == "Y") && (debitCreditFlag == "C")){
							
								postedCrAmount = amount;
							
							}else{
							
								unPostedCrAmount = amount;
							}
							
							
							totStPostedDrAmount = totStPostedDrAmount + postedDrAmount;
							totStUnPostedDrAmount = totStUnPostedDrAmount + unPostedDrAmount;
							totStPostedCrAmount = totStPostedCrAmount + postedCrAmount;
							totStUnPostedCrAmount = totStUnPostedCrAmount + unPostedCrAmount;
							
							totDayPostedDrAmount = totDayPostedDrAmount + postedDrAmount;
							totDayUnPostedDrAmount = totDayUnPostedDrAmount + unPostedDrAmount;
							totDayPostedCrAmount = totDayPostedCrAmount + postedCrAmount;
							totDayUnPostedCrAmount = totDayUnPostedCrAmount + unPostedCrAmount;
							
							totPostedDrAmount = totPostedDrAmount + postedDrAmount;
							totUnPostedDrAmount = totUnPostedDrAmount + unPostedDrAmount;
							totPostedCrAmount = totPostedCrAmount + postedCrAmount;
							totUnPostedCrAmount = totUnPostedCrAmount + unPostedCrAmount;
							
							grandPostedDrAmount = grandPostedDrAmount + postedDrAmount;
							grandUnPostedDrAmount = grandUnPostedDrAmount + unPostedDrAmount;
							grandPostedCrAmount = grandPostedCrAmount + postedCrAmount;
							grandUnPostedCrAmount = grandUnPostedCrAmount + unPostedCrAmount;
							
							invMap = [:];
							invMap.put("invoiceItemTypeId", invoiceItemTypeName);
							invMap.put("statusId", statusDes);
							invMap.put("invoiceTypeId", invoiceType);
							invMap.put("invoiceDate", invoiceDate);
							invMap.put("glAccountId", glAccountId);
							invMap.put("postedDrAmount", postedDrAmount);
							invMap.put("unPostedDrAmount", unPostedDrAmount);
							invMap.put("postedCrAmount", postedCrAmount);
							invMap.put("unPostedCrAmount", unPostedCrAmount);
							
							statusMap.put("statusId", "STATUSTOTAL");
							statusMap.put("postedDrAmount", totStPostedDrAmount);
							statusMap.put("unPostedDrAmount", totStUnPostedDrAmount);
							statusMap.put("postedCrAmount", totStPostedCrAmount);
							statusMap.put("unPostedCrAmount", totStUnPostedCrAmount);
							
							dayMap.put("statusId", "DAYTOTAL");
							dayMap.put("postedDrAmount", totDayPostedDrAmount);
							dayMap.put("unPostedDrAmount", totDayUnPostedDrAmount);
							dayMap.put("postedCrAmount", totDayPostedCrAmount);
							dayMap.put("unPostedCrAmount", totDayUnPostedCrAmount);
							
							totMap.put("statusId", "SUBTOTAL");											
							totMap.put("postedDrAmount", totPostedDrAmount);
							totMap.put("unPostedDrAmount", totUnPostedDrAmount);
							totMap.put("postedCrAmount", totPostedCrAmount);
							totMap.put("unPostedCrAmount", totUnPostedCrAmount);
							
							invItemTypeMap.put("invoiceItemTypeId", invoiceItemTypeName);
							invItemTypeMap.put("glAccountId", glAccountId);
							invItemTypeMap.put("postedDrAmount", totPostedDrAmount);
							invItemTypeMap.put("unPostedDrAmount", totUnPostedDrAmount);
							invItemTypeMap.put("postedCrAmount", totPostedCrAmount);
							invItemTypeMap.put("unPostedCrAmount", totUnPostedCrAmount);
							
							invItemTypeTotMap.put("invoiceItemTypeId", "TOTAL");
							invItemTypeTotMap.put("postedDrAmount", grandPostedDrAmount);
							invItemTypeTotMap.put("unPostedDrAmount", grandUnPostedDrAmount);
							invItemTypeTotMap.put("postedCrAmount", grandPostedCrAmount);
							invItemTypeTotMap.put("unPostedCrAmount", grandUnPostedCrAmount);
							
							
							tempGrandTotMap.put("statusId", "TOTAL");							
							tempGrandTotMap.put("postedDrAmount", grandPostedDrAmount);
							tempGrandTotMap.put("unPostedDrAmount", grandUnPostedDrAmount);
							tempGrandTotMap.put("postedCrAmount", grandPostedCrAmount);
							tempGrandTotMap.put("unPostedCrAmount", grandUnPostedCrAmount);
							
							tempAppMap = [:];
							tempAppMap.putAll(invMap);
							finalInvoiceItemList.addAll(tempAppMap);
							
						}
					}
				}
				
				stTempAppMap = [:];
				stTempAppMap.putAll(statusMap);
				finalInvoiceItemList.addAll(stTempAppMap);
				
			}
			
			dayTempAppMap = [:];
			dayTempAppMap.putAll(dayMap);
			finalInvoiceItemList.addAll(dayTempAppMap);
			
		}
		
		itemTempAppMap = [:];
		itemTempAppMap.putAll(totMap);
		finalInvoiceItemList.addAll(itemTempAppMap);
		
		tempInvItemTypeMap = [:];
		tempInvItemTypeMap.putAll(invItemTypeMap);
		glSummaryFinalList.addAll(tempInvItemTypeMap);
	}		
	
	
	grandTempAppMap = [:];
	grandTempAppMap.putAll(tempGrandTotMap);
	finalInvoiceItemList.addAll(grandTempAppMap);
	
	grandItemTempAppMap = [:];
	grandItemTempAppMap.putAll(invItemTypeTotMap);
	glSummaryFinalList.addAll(grandItemTempAppMap);
	
	context.finalInvoiceItemList = finalInvoiceItemList;
	context.glSummaryFinalList = glSummaryFinalList;

	
	
	
	
	
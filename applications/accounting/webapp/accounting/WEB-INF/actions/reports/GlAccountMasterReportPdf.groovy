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
	glAccountId = parameters.glAccountId;
	
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
	context.fromDate = fromDate;
	context.thruDate = thruDate;
	
	List conditionList=[];
	
	
	conditionList.clear();
	
	if(glAccountId){
		conditionList.add(EntityCondition.makeCondition("defaultGlAccountId", EntityOperator.EQUALS, glAccountId));
	}
	invoiceItemTypeCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceItemTypeList = delegator.findList("InvoiceItemType", invoiceItemTypeCondition, null, null, null, false );
	invoiceItemTypeIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemTypeList, "invoiceItemTypeId", true);
	
	conditionList.clear();
	if(partyId){
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),EntityOperator.OR,
			EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)));
	}
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	invoiceItemCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceItemList = delegator.findList("InvoiceAndItem", invoiceItemCondition, UtilMisc.toSet("invoiceItemTypeId","description","invoiceId","invoiceTypeId","invoiceDate","amount"), null, null, false );
	invoiceIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemList, "invoiceId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIdsList));
	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	acctCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	acctgTransList = delegator.findList("AcctgTrans", acctCondition, null, null, null, false );
	acctgTransIdsList = EntityUtil.getFieldListFromEntityList(acctgTransList, "acctgTransId", true);
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("acctgTransId", EntityOperator.IN, acctgTransIdsList));
	if(glAccountId){
		conditionList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
	}
	if(partyId){
		conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyId));
	}
	
	acctEntryCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	acctgTransEntryList = delegator.findList("AcctgTransAndEntries", acctEntryCondition, null, null, null, false );
	
	glTotal = 0;
	for(k=0;k<acctgTransEntryList.size();k++){
		eachGlTotal = acctgTransEntryList[k];
		glTotal = glTotal + eachGlTotal.get("amount");
	}
	
	finalInvoiceItemList = [];
	
	grandTotal = 0;
	tempGrandTotMap = [:];
	
	for(i=0;i<invoiceItemTypeIdsList.size();i++){
		
		invoiceItemTypeId = invoiceItemTypeIdsList[i];
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, invoiceItemTypeId));
		filteredCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		filteredinvoiceItemList = EntityUtil.filterByCondition(invoiceItemList, filteredCondition);
		
		filteredInvoiceIdsList = EntityUtil.getFieldListFromEntityList(filteredinvoiceItemList, "invoiceId", true);
		
		total = 0;
		totMap = [:];
		
		for(j=0;j<filteredInvoiceIdsList.size();j++){
			
			invoiceId = filteredInvoiceIdsList[j];
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
			filteredInvoiceCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			
			filteredInvoiceList = EntityUtil.filterByCondition(filteredinvoiceItemList, filteredInvoiceCondition);
			
			invoiceDate = filteredInvoiceList[0].get("invoiceDate");
			invoiceTypeId = filteredInvoiceList[0].get("invoiceTypeId");
			
			filteredAcctgTransList = EntityUtil.filterByCondition(acctgTransList, filteredInvoiceCondition);
			filteredAcctgTransIdsList = EntityUtil.getFieldListFromEntityList(filteredAcctgTransList, "acctgTransId", true);
			
			for(k=0;k<filteredAcctgTransIdsList.size();k++){
				acctgTransId = filteredAcctgTransIdsList[k];
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS, acctgTransId));
				filAccCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				
				filtAcctgTransEntryList = EntityUtil.filterByCondition(acctgTransEntryList, filAccCondition);
				
				for(t=0;t<filtAcctgTransEntryList.size();t++){
					
					eachAcctgTransEntry = filtAcctgTransEntryList[t];
					
					amount = eachAcctgTransEntry.get("amount");
					debitCreditFlag = eachAcctgTransEntry.get("debitCreditFlag");
					
					
					isPosted = eachAcctgTransEntry.get("isPosted");
					
					if(isPosted == "Y"){
						status = "POSTED";
					}else{
					status = "UNPOSTED";
					}
					
					
					if(debitCreditFlag == "D"){
						crFlag = "";
						drFlag = "D";
					}else{
						crFlag = "C";
						drFlag = "";
					}
					
					total = total + amount;
					grandTotal = grandTotal + amount;
					
					invMap = [:];
					invMap.put("invoiceItemTypeId", invoiceItemTypeId);
					invMap.put("invoiceId", invoiceId);
					invMap.put("invoiceTypeId", invoiceTypeId);
					invMap.put("invoiceDate", invoiceDate);
					invMap.put("drFlag", drFlag);
					invMap.put("crFlag", crFlag);
					invMap.put("amount", amount);
					invMap.put("status", status);
					
					
					
					totMap.put("invoiceItemTypeId", "Subtotal");
					totMap.put("invoiceTypeId", "");
					totMap.put("invoiceDate", "");
					totMap.put("amount", total);
					
					tempGrandTotMap.put("invoiceItemTypeId", "Total");
					tempGrandTotMap.put("amount", grandTotal);
					
					tempAppMap = [:];
					tempAppMap.putAll(invMap);
					
					finalInvoiceItemList.addAll(tempAppMap);
					
				}
				
			}
			
		}
		
		TempTotMap = [:];
		TempTotMap.putAll(totMap);
		finalInvoiceItemList.addAll(TempTotMap);
		
	}
	
	grandTotMap = [:];
	grandTotMap.putAll(tempGrandTotMap);
	finalInvoiceItemList.addAll(grandTotMap);
	
	context.grandTotal = grandTotal;
	context.glTotal = glTotal;
	context.glAccountId = glAccountId;
	context.finalInvoiceItemList = finalInvoiceItemList;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
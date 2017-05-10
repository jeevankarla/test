
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

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.base.util.*
import org.ofbiz.minilang.SimpleMapProcessor
import org.ofbiz.content.ContentManagementWorker
import org.ofbiz.content.content.ContentWorker
import org.ofbiz.content.data.DataResourceWorker
import java.sql.Date;
import java.sql.Timestamp;

import javolution.util.FastList;
Debug.log("parameters===="+parameters.invoiceIds);
acctgTransId = "";
accountingTransEntryList = [];
accountingTransEntries1 = [:];
accountingTransEntries = [:];
finAccountTransId = parameters.finAccountTransId;
acctgTransId = parameters.acctgTransId;
finAccountId = parameters.finAccountId;

//getting invoice tax type
taxType = "";
partyIdForAdd="";
invoice = delegator.findOne("Invoice",[invoiceId : parameters.invoiceId] , false);
if(UtilValidate.isNotEmpty(invoice)){
	if(invoice.invoiceTypeId=="ADMIN_OUT" || invoice.invoiceTypeId=="PURCHASE_INVOICE"){
		partyIdForAdd=invoice.partyId;
		
	}
	else if(invoice.invoiceTypeId=="MIS_INCOME_IN" || invoice.invoiceTypeId=="SALES_INVOICE"){
		partyIdForAdd=invoice.partyIdFrom;
		
	}
}

conditionList=[];
allAcctgTransIds=[];
//finding on AcctgTrans for invoice,payment and finAccnTransId
if(UtilValidate.isNotEmpty(parameters.invoiceIds)  ){
	//for invoiceId
	if(UtilValidate.isNotEmpty(parameters.invoiceIds)){
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN,parameters.invoiceIds));
	}
	
	conditionAcctgTrans = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	Debug.log("condition===="+conditionAcctgTrans);
	//finding on AcctgTrans
	acctgTransList = delegator.findList("AcctgTrans",conditionAcctgTrans , null, null, null, false );
	Debug.log("acctgTransList===="+acctgTransList);
	if(UtilValidate.isNotEmpty(acctgTransList)){
		allAcctgTransIds= EntityUtil.getFieldListFromEntityList(acctgTransList, "acctgTransId", true);
		acctgTrans = EntityUtil.getFirst(acctgTransList);
		if(UtilValidate.isNotEmpty(acctgTrans)){
			acctgTransId = acctgTrans.acctgTransId
		}
	}
	if(UtilValidate.isNotEmpty(acctgTransId)){
		if(UtilValidate.isNotEmpty(allAcctgTransIds)){
			accountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.IN , allAcctgTransIds)  , null, null, null, false );
		}else{
			accountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS , acctgTransId)  , null, null, null, false );
		}
		tempAccountingTransEntryList=[];
		for(int i=0;i<accountingTransEntryList.size();i++){
			accountingTransEntry = accountingTransEntryList.get(i);
			tempMap=[:];
			tempMap = UtilMisc.makeMapWritable(accountingTransEntry);
			int partyIdInt = onlyContainsNumbers(tempMap.partyId);
			if(UtilValidate.isEmpty(partyIdForAdd)){
				partyIdForAdd=tempMap.costCenterId;
			}
			tempMap.put("partyIdInt", partyIdInt);
			tempAccountingTransEntryList.add(tempMap);
		}
		tempAccountingTransEntryList = UtilMisc.sortMaps(tempAccountingTransEntryList, UtilMisc.toList("partyIdInt"));
		accountingTransEntryList.clear();
		accountingTransEntryList.addAll(tempAccountingTransEntryList);
	}
	
}
//end of acctngTrans find
GenericValue finAccntTransSequenceEntry;
finAccountTransAttributeDetails="";
if(UtilValidate.isNotEmpty(acctgTransId)){
	
	if(UtilValidate.isNotEmpty(allAcctgTransIds)){
		accountingTransEntries = (delegator.findList("AcctgTrans", EntityCondition.makeCondition("acctgTransId", EntityOperator.IN, allAcctgTransIds), null, null, null, false));
	}else{
		accountingTransEntries = delegator.findOne("AcctgTrans",[acctgTransId : acctgTransId] , false);
	}
	if(UtilValidate.isNotEmpty(finAccountTransId)){
		finAccountTransAttributeDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
	}
	if(finAccountTransAttributeDetails){
		finAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, finAccountTransAttributeDetails.attrValue), null, null, null, false));
	}
	else{
		finAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, accountingTransEntries[0].finAccountTransId ), null, null, null, false));
	}
	}else{
	if(UtilValidate.isNotEmpty(finAccountTransId)){
		finAccountTransAttributeDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
		
		finTransAttr = delegator.findOne("FinAccountTransAttribute",[finAccountTransId : finAccountTransId,attrName:"INFAVOUR_OF"] , false);
		if(UtilValidate.isEmpty(finTransAttr)){
			finTransAttr = delegator.findOne("FinAccountTransAttribute",[finAccountTransId : finAccountTransAttributeDetails.attrValue,attrName:"INFAVOUR_OF"] , false);
		}
		finTransEntries = delegator.findOne("FinAccountTrans",[finAccountTransId : finAccountTransId] , false);
		
		String cheqInFavour="";
		String comments="";
		if(finTransAttr){
		cheqInFavour=finTransAttr.attrValue;
		}
		if(finTransEntries){
			comments=finTransEntries.comments;
			}
		context.cheqInFavour=cheqInFavour;
		context.comments=comments;
		accountingTransList = delegator.findList("AcctgTrans",EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS , finAccountTransId)  , null, null, null, false );
		if(UtilValidate.isNotEmpty(accountingTransList)){
			accountingTransEntries = EntityUtil.getFirst(accountingTransList);
			if(UtilValidate.isNotEmpty(accountingTransEntries)){
				acctgTransId = accountingTransEntries.acctgTransId
			}
		}
		finAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, finAccountTransId), null, null, null, false));
	}
}
finAccntTransSequence = "";
finAccountId="";
BankName="";
if(UtilValidate.isNotEmpty(finAccntTransSequenceEntry)){
	finAccntTransSequence = finAccntTransSequenceEntry.transSequenceId;
	finAccountId=finAccntTransSequenceEntry.finAccountId;
	if(finAccountId){
		finAccount=delegator.findOne("FinAccount",[finAccountId : finAccountId] , false);
		if(finAccount.finAccountName){
			BankName=finAccount.finAccountName;
		}
	}
}
context.BankName=BankName;
context.finAccntTransSequence = finAccntTransSequence;
Debug.log("accountingTransEntries======"+accountingTransEntries.size());


Debug.log("accountingTransEntryList======"+accountingTransEntryList.size());
context.put("accountingTransEntryList",accountingTransEntryList);

context.put("accountingTransEntries1",accountingTransEntries);
private int onlyContainsNumbers(String text) {
	try {
		 Integer.parseInt(text);
		return Integer.parseInt(text);
	} catch (NumberFormatException ex) {
		return 0;
	}
}
//for Deposit
payAcctgTransId = "";
payFinAccountTransId = "";
payAccountingTransEntryList = [];
payAccountingTransEntries = [:];

payAcctgTransId = parameters.acctgTransId;

finAccountTransAttributeDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
if(UtilValidate.isNotEmpty(finAccountTransAttributeDetails)){
	if(!(finAccountTransAttributeDetails.attrValue).equals("UNION")){
		payFinAccountTransId = finAccountTransAttributeDetails.attrValue;
	}
}
conditionList.clear();
//finding on AcctgTrans for invoice,payment and finAccnTransId
allPayAcctgTransIds=[];
if(UtilValidate.isNotEmpty(parameters.invoiceIds) || UtilValidate.isNotEmpty(parameters.invoiceId) ||UtilValidate.isNotEmpty(parameters.paymentId) ||UtilValidate.isNotEmpty(payFinAccountTransId) ){
	//for invoiceId
	if(UtilValidate.isNotEmpty(parameters.invoiceId)){
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,parameters.invoiceId));
	}
	if(UtilValidate.isNotEmpty(parameters.invoiceIds)){
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN,parameters.invoiceIds));
		}
	//for paymentId
	if(UtilValidate.isNotEmpty(parameters.invoiceId)){
		conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS,parameters.paymentId));
	}
	//for finAccountTrans
	if(UtilValidate.isNotEmpty(payFinAccountTransId)){
		conditionList.add(EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS,payFinAccountTransId));
	}
	conditionAcctgTrans = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	//finding on AcctgTrans
	paymentAcctgTransList = delegator.findList("AcctgTrans",conditionAcctgTrans , null, null, null, false );
	if(UtilValidate.isNotEmpty(paymentAcctgTransList)){
		allPayAcctgTransIds= EntityUtil.getFieldListFromEntityList(paymentAcctgTransList, "acctgTransId", true);
		acctgTrans = EntityUtil.getFirst(paymentAcctgTransList);
		if(UtilValidate.isNotEmpty(acctgTrans)){
			payAcctgTransId = acctgTrans.acctgTransId
		}
	}
	
}

GenericValue payFinAccntTransSequenceEntry;
if(UtilValidate.isNotEmpty(payAcctgTransId) && (payAcctgTransId != acctgTransId)){
	payAccountingTransEntries = delegator.findOne("AcctgTrans",[acctgTransId : payAcctgTransId] , false);
	payFinAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, payAccountingTransEntries.finAccountTransId), null, null, null, false));
}else{
	if(UtilValidate.isNotEmpty(payFinAccountTransId)){
		finAccountTransAttributeDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : payFinAccountTransId, attrName : "FATR_CONTRA"], false);
		
		finTransAttr = delegator.findOne("FinAccountTransAttribute",[finAccountTransId : payFinAccountTransId,attrName:"INFAVOUR_OF"] , false);
		if(UtilValidate.isEmpty(finTransAttr)){
			finTransAttr = delegator.findOne("FinAccountTransAttribute",[finAccountTransId : finAccountTransAttributeDetails.attrValue,attrName:"INFAVOUR_OF"] , false);
		}
		finTransEntries = delegator.findOne("FinAccountTrans",[finAccountTransId : payFinAccountTransId] , false);
		
		String cheqInFavour="";
		String comments="";
		if(finTransAttr){
		cheqInFavour=finTransAttr.attrValue;
		}
		if(finTransEntries){
			comments=finTransEntries.comments;
			}
		context.payCheqInFavour=cheqInFavour;
		context.payComments=comments;
		accountingTransList = delegator.findList("AcctgTrans",EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS , payFinAccountTransId)  , null, null, null, false );
		if(UtilValidate.isNotEmpty(accountingTransList)){
			payAccountingTransEntries = EntityUtil.getFirst(accountingTransList);
			if(UtilValidate.isNotEmpty(payAccountingTransEntries)){
				payAcctgTransId = payAccountingTransEntries.acctgTransId
			}
		}
		payFinAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, payFinAccountTransId), null, null, null, false));
	}
}
payFinAccntTransSequence = "";
if(UtilValidate.isNotEmpty(payFinAccntTransSequenceEntry)){
	payFinAccntTransSequence = payFinAccntTransSequenceEntry.transSequenceNo;
}
context.payFinAccntTransSequence = payFinAccntTransSequence;
context.put("payAccountingTransEntries",payAccountingTransEntries);

if(UtilValidate.isNotEmpty(payAcctgTransId)){
	if(UtilValidate.isNotEmpty(allPayAcctgTransIds)){
		payAccountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.IN , allPayAcctgTransIds)  , null, null, null, false );
	}else{
		payAccountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS , payAcctgTransId)  , null, null, null, false );
	}
}
context.put("payAccountingTransEntryList",payAccountingTransEntryList);

if(UtilValidate.isNotEmpty(parameters.reportTypeFlag)){
	reportTypeFlag = parameters.reportTypeFlag;
	context.put("reportTypeFlag",reportTypeFlag);
}
context.partyIdForAdd=partyIdForAdd;

/*invSequenceNum = "";

if(parameters.invoiceId){
	condList = [];
	condList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS , parameters.invoiceId));
	cond2 = EntityCondition.makeCondition(condList,EntityOperator.AND);
	invsequenceList = delegator.findList("InvoiceSequence", cond2, null, null, null, false);
	invsequenceList.each{eachItem ->
		invSequenceNum = eachItem.sequenceId;
	}
}
context.invSequenceNum = invSequenceNum;*/



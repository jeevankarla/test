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
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.common.*;
import org.ofbiz.webapp.control.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastMap;
import java.util.Calendar;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
partyCode = parameters.partyId;
dctx = dispatcher.getDispatchContext();
purposeTypeId=parameters.purposeTypeId;

isLedgerCallFor="ArOnly";
if(parameters.isLedgerCallFor){
	isLedgerCallFor=parameters.isLedgerCallFor;
}
fromDateTime = null;
thruDateTime = null;
if(UtilValidate.isNotEmpty(partyfromDate)){
	def sdf = new SimpleDateFormat("yyyy, MMM dd");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(partyfromDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+fromDate, "");
	}
}
//UtilDateTime.getDayStart(fromDateTime)
dayBegin=UtilDateTime.getDayStart(fromDateTime);
if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
}

partyFinHistoryMap=[:];

Boolean actualCurrency = new Boolean(context.actualCurrency);
if (actualCurrency == null) {
	actualCurrency = true;
}
actualCurrencyUomId = context.actualCurrencyUomId;
if (!actualCurrencyUomId) {
	actualCurrencyUomId = context.defaultOrganizationPartyCurrencyUomId;
}

//from finHistory for InterUnit Ledger

partyDebits=0;
partyCredits=0;
partyFinHistryDayWiseMap=[];

Debug.log("====fromDateTime=====>"+fromDateTime+"==dayStart=="+dayBegin+"==purposeTypeId=="+purposeTypeId);
//Debug.log("====partyFinHistryDayWiseMap=====>"+partyFinHistryDayWiseMap);
partyIds=[];
//partyIdsList
	if(UtilValidate.isNotEmpty(context.partyIdsList) ){
		partyIds=context.partyIdsList;
	}

//if roleType is empty and partyIdsList is empty then get Parties from invoice
	
if(UtilValidate.isEmpty(context.partyIdsList) && UtilValidate.isEmpty(parameters.partyRoleTypeId)){
	conditionList=[];
	
	findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);

	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_IN_PROCESS"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_WRITEOFF"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	if(UtilValidate.isNotEmpty(partyfromDate)){
		conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(dayBegin)))
	}
	newInvCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List<String> invOrderBy = UtilMisc.toList("invoiceDate");
	invIterator = delegator.find("InvoiceAndType", newInvCondition, null, null, invOrderBy, findOpts);
	if("ArOnly"==isLedgerCallFor){
		partyIds=EntityUtil.getFieldListFromEntityListIterator(invIterator, "partyId", true);
	}else{
		partyIds=EntityUtil.getFieldListFromEntityListIterator(invIterator, "partyIdFrom", true);
	}
	invIterator.close();
	Debug.log("==========partyIds=FROM INVOICEE====="+partyIds);
}
//Abstract Ledger Map
partyLedgerAbstractCsvList=[];
partyWiseLedgerAbstractMap=[:]
partyIds.each{partyId->
	abstractInnerMap=[:];
	abstractInnerMap["OB"]=0;
	abstractInnerMap["invoicePendingAmount"]=0;
	abstractInnerMap["advancePaymentAmount"]=0;
	abstractInnerMap["partyId"]=partyId;
	partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);
	abstractInnerMap["partyName"]=partyName;
	abstractInnerMap["invTotal"]=0;
	abstractInnerMap["paymentTotal"]=0;
	abstractInnerMap["debitValue"]=0;
	abstractInnerMap["creditValue"]=0;
	arPartyOB  =BigDecimal.ZERO;
	arPartyCB  =BigDecimal.ZERO;
	
	apPartyOB  =BigDecimal.ZERO;
	apPartyCB  =BigDecimal.ZERO;
	
	pendingAmount  =BigDecimal.ZERO;
	
	if("ArOnly"==isLedgerCallFor){
		arOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate: dayBegin, partyId:partyId,purposeTypeId:purposeTypeId]));
		if(UtilValidate.isNotEmpty(arOpeningBalanceRes)){
			arPartyOB=arOpeningBalanceRes.get("openingBalance");
			//abstractInnerMap.putAt("openingBalance", arOpeningBalanceRes.get("openingBalance"));
			pendingAmount=arOpeningBalanceRes.get("invoicePendingAmount");
			abstractInnerMap.putAt("invoicePendingAmount",  arOpeningBalanceRes.get("invoicePendingAmount"));
			abstractInnerMap.putAt("advancePaymentAmount", arOpeningBalanceRes.get("advancePaymentAmount"));
		}
		//arPartyCB=(arPartyOB+partyAbstractTotalInner.get("invTotal"))-(partyAbstractTotalInner.get("paymentTotal"));
		abstractInnerMap.putAt("OB",arPartyOB);
		abstractInnerMap.putAt("CB",arPartyCB);
	}else{
		apOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate: dayBegin, partyId:partyId,purposeTypeId:purposeTypeId,isOBCallForAP:Boolean.TRUE]));
		if(UtilValidate.isNotEmpty(apOpeningBalanceRes)){
			apPartyOB=apOpeningBalanceRes.get("openingBalance");
			pendingAmount=apOpeningBalanceRes.get("invoicePendingAmount");
			//abstractInnerMap.putAt("openingBalance", apOpeningBalanceRes.get("openingBalance"));
			abstractInnerMap.putAt("invoicePendingAmount",  apOpeningBalanceRes.get("invoicePendingAmount"));
			abstractInnerMap.putAt("advancePaymentAmount", apOpeningBalanceRes.get("advancePaymentAmount"));
			
		}
		//apPartyCB=(apPartyCB+partyAbstractTotalInner.get("invTotal"))-(partyAbstractTotalInner.get("paymentTotal"));
		abstractInnerMap.putAt("OB",apPartyOB);
		abstractInnerMap.putAt("CB",apPartyCB);
	}
	if(pendingAmount>0){
	partyWiseLedgerAbstractMap[partyId]=abstractInnerMap;
	partyLedgerAbstractCsvList.addAll(abstractInnerMap);
	}
}

context.partyWiseLedgerAbstractMap=partyWiseLedgerAbstractMap;

Debug.log("===partyLedgerAbstractCsvList==IN==CSVVVVV===size()==="+partyLedgerAbstractCsvList.size());
context.partyLedgerAbstractCsvList=partyLedgerAbstractCsvList;
	//Debug.log("=====partyWiseLedgerAbstractMap================>"+partyWiseLedgerAbstractMap);
	
//transferAmount = totalInvSaApplied.add(totalInvSaNotApplied).subtract(totalInvPuApplied.add(totalInvPuNotApplied)).subtract(totalPayInApplied.add(totalPayInNotApplied).add(totalPayOutApplied.add(totalPayOutNotApplied)));



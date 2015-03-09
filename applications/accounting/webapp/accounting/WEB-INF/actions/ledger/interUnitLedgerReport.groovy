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
partyCode = parameters.partyId;
dctx = dispatcher.getDispatchContext();

//Check for Party is Valid or Not
result = [:];
GenericValue partyDetail = delegator.findOne("Party",UtilMisc.toMap("partyId", parameters.partyId), false);
if (UtilValidate.isEmpty(partyDetail)) {
	Debug.logError("Party Id:"+parameters.partyId+ "'is not a valid Party!!", "");
	context.errorMessage ="Party Id:"+parameters.partyId+"'is not a valid Party!!";
	return "error" ;
}
//finAccount Reconcilation Report will use this logic....
SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM dd");

partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
Debug.log("=======partyfromDate========>"+partyfromDate+"==partythruDate=="+partythruDate);
if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
   Timestamp daystart;
	try {
		daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		 } catch (ParseException e) {
			 Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			 }
   parameters.fromDateReport=UtilDateTime.getDayStart(daystart);
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   Timestamp dayend;
   try {
	   dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
   } catch (ParseException e) {
	   Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		}
   parameters.thruDateReport=UtilDateTime.getDayEnd(dayend);
}

reconciledDate=parameters.reconciledDate;
if(UtilValidate.isNotEmpty(reconciledDate)){
Timestamp fromDateTs = null;
if(reconciledDate){
		SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
	try {
		fromDateTs = new java.sql.Timestamp(sdfo.parse(reconciledDate).getTime());	} catch (ParseException e) {
	}
}
parameters.reconciledDateStart = UtilDateTime.getDayStart(fromDateTs);
parameters.reconciledDateEnd = UtilDateTime.getDayEnd(fromDateTs);
}
Debug.log("==arameters.partyId=INTER==UNIT="+parameters.partyId);
partyFinHistoryMap=[:];

interUnitAccountsList=[];
conditionList = [];
   if(UtilValidate.isNotEmpty(parameters.partyId)){
	if(!parameters.multifinAccount == "Y"){
	  conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "INTERUNIT_ACCOUNT"));
	  conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
	  
	}
	  conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.partyId));
	}
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
	interUnitAccountsList = delegator.findList("FinAccount", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	Debug.log("interUnitAccountsList======================"+interUnitAccountsList);
	finAccountIdList= EntityUtil.getFieldListFromEntityList(interUnitAccountsList,"finAccountId", true);
	Debug.log("finAccountIdList=================="+finAccountIdList);
	context.interUnitAccountsList=interUnitAccountsList;
	interUnitFinAccount=EntityUtil.getFirst(interUnitAccountsList);
	
	finAccountId="";
	if(UtilValidate.isNotEmpty(interUnitFinAccount)){
		context.finAccountId=interUnitFinAccount.finAccountId;
		parameters.finAccountId=interUnitFinAccount.finAccountId;
		finAccountId=interUnitFinAccount.finAccountId;
		context.finAccount=interUnitFinAccount;
	}else{
	Debug.logError("Party Id:"+parameters.partyId+ "' Don't have any Accounts !!", "");
	context.errorMessage ="Party Id:"+parameters.partyId+"'Don't have any Accounts!!";
	return "error";
	}
	
	finAccountTransInputMap=[:];
	finAccountTransInputMap["fromTransactionDate"]=parameters.fromDateReport;
	finAccountTransInputMap["thruTransactionDate"]=parameters.thruDateReport;
	finAccountTransInputMap["statusId"]="FINACT_TRNS_CREATED";
	finAccountTransInputMap["userLogin"]=userLogin;
	List finAccountTransList = [];
	Map resultCtx = [:];
	if(parameters.multifinAccount == "Y"){
		finAccountIdList.each{ eachfinAccountId ->
			finAccountTransInputMap["finAccountId"]=eachfinAccountId;
			Debug.log("finAccountTransInputMap====================="+finAccountTransInputMap);
				Map finAccountTransMap=resultCtx = dispatcher.runSync("getFinAccountTransListAndTotals", finAccountTransInputMap);
				Debug.log("finAccountTransMap====================="+finAccountTransMap);
				finAccountTransList.addAll(finAccountTransMap.get("finAccountTransList"));
		}
		Debug.log("finAccountTransList==================="+finAccountTransList);
		context.finAccountTransList=finAccountTransList;
	}else{
	finAccountTransInputMap["finAccountId"]=finAccountId;
	if(UtilValidate.isNotEmpty(finAccountId)){
		Map	finAccountTransListAndTotals=resultCtx = dispatcher.runSync("getFinAccountTransListAndTotals", finAccountTransInputMap);
		finAccountTransList=finAccountTransListAndTotals.get("finAccountTransList");
		context.searchedNumberOfRecords=finAccountTransListAndTotals.searchedNumberOfRecords;
		context.grandTotal=finAccountTransListAndTotals.grandTotal;
		context.createdGrandTotal=finAccountTransListAndTotals.createdGrandTotal;
		context.totalCreatedTransactions=finAccountTransListAndTotals.totalCreatedTransactions;
		context.approvedGrandTotal=finAccountTransListAndTotals.approvedGrandTotal;
		context.totalApprovedTransactions=finAccountTransListAndTotals.totalApprovedTransactions;
		context.createdApprovedGrandTotal=finAccountTransListAndTotals.createdApprovedGrandTotal;
		context.totalCreatedApprovedTransactions=finAccountTransListAndTotals.totalCreatedApprovedTransactions;
		
	}
	context.finAccountTransList=finAccountTransList;
	}
	//Debug.log("finAccountTransList===================="+finAccountTransList);
	/*<set field="finAccountId" from-field="parameters.finAccountId"/>
	<set field="parameters.statusId" value="FINACT_TRNS_CREATED"/>
	<set field="fromTransactionDate" value="${parameters.fromDateReport}"/>
	 <set field="thruTransactionDate" value="${parameters.thruDateReport}"/>
	<set field="glReconciliationId" value="${glReconciliationId}"/>
	<set field="parameters.noConditionFind" value="Y"/>
   <service service-name="getFinAccountTransListAndTotals" result-map="finAccountTransListAndTotals" auto-field-map="true"/>
   <set field="finAccountTransList" type="List" from-field="finAccountTransListAndTotals.finAccountTransList"/>
   <set field="searchedNumberOfRecords" type="Integer" from-field="finAccountTransListAndTotals.searchedNumberOfRecords"/>
   <set field="grandTotal" type="BigDecimal" from-field="finAccountTransListAndTotals.grandTotal"/>
   <set field="createdGrandTotal" type="BigDecimal" from-field="finAccountTransListAndTotals.createdGrandTotal"/>
   <set field="totalCreatedTransactions" type="Long" from-field="finAccountTransListAndTotals.totalCreatedTransactions"/>
   <set field="approvedGrandTotal" type="BigDecimal" from-field="finAccountTransListAndTotals.approvedGrandTotal"/>
   <set field="totalApprovedTransactions" type="Long" from-field="finAccountTransListAndTotals.totalApprovedTransactions"/>
   <set field="createdApprovedGrandTotal" type="BigDecimal" from-field="finAccountTransListAndTotals.createdApprovedGrandTotal"/>
   <set field="totalCreatedApprovedTransactions" type="Long" from-field="finAccountTransListAndTotals.totalCreatedApprovedTransactions"/>
	<set field="finAccountId" from-field="parameters.finAccountId"/>
   <entity-one entity-name="FinAccount"  value-field="finAccount"/>
   <set field="glReconciliationId" from-field="parameters.glReconciliationId"/>
   <set field="finAccountTransId" from-field="parameters.finAccountTransId"/>*/
   
//Debug.log("==interUnitAccountsList=="+interUnitAccountsList+"==finAccountTransList=="+finAccountTransList);
return  "success";

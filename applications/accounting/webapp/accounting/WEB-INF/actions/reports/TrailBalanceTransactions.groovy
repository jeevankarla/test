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

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.Delegator;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastSet;
import org.ofbiz.accounting.util.*;

condList = [];
allAcctgCodeTransEntryMap=[:];
glAccountIdList=[];
glAccountId=parameters.AccountCode;
if(UtilValidate.isEmpty(glAccountId)){
	glAccountIdList=context.get("accountCodeList");
}else{
glAccountIdList.add(glAccountId);
}
reportTypeFlag=parameters.reportTypeFlag;
//Debug.log("ACCOUNT CODE=======From==NEw==GROOVY=============="+glAccountId);
 GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
 //Debug.log("tempDetails===================="+tempDetails);
 fromDate=customTimePeriod.fromDate;
 thruDate=customTimePeriod.thruDate;
result=dispatcher.runSync("getGlAccountOpeningBalance", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)),userLogin:userLogin]);
context.put("openingBal",result.get("openingBal"));
 findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
  conditionList = [];
 glAccountIdList.each{glAccountId->
 conditionList.clear();
 conditionList.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.EQUALS,parameters.organizationPartyId));
 if(reportTypeFlag.equals("condensed")){
  conditionList.add(EntityCondition.makeCondition("isPosted" , EntityOperator.EQUALS,"Y"));
 }
 conditionList.add(EntityCondition.makeCondition("glAccountId" , EntityOperator.EQUALS,glAccountId));
 conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate))));
 conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate))));
 //Debug.log("conditionList========="+conditionList);
 acctgTransEntryListIter = delegator.find("AcctgTransAndEntries", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, findOpts);
 // List acctgTransEntryList = delegator.find("AcctgTransAndEntries", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
 // filterByCondition
 acctgTransEntryList=[];
 if(UtilValidate.isNotEmpty(acctgTransEntryListIter)){
	// Debug.log("acctgTransEntryListIter===================="+acctgTransEntryListIter);
	 while(acctgTransEntry= acctgTransEntryListIter.next()) {
	  acctgTransEntryList.addAll(acctgTransEntry);
	 }
	 if(UtilValidate.isNotEmpty(acctgTransEntryList)){
		 allAcctgCodeTransEntryMap[glAccountId]=acctgTransEntryList;
	 }
	 
	 acctgTransEntryListIter.close();
 }
 }
 //invIterator.close();

 
 //Debug.log("allAcctgCodeTransEntryMap===================="+allAcctgCodeTransEntryMap);
  context.allAcctgCodeTransEntryMap=allAcctgCodeTransEntryMap;
  context.fromDate=fromDate;
  context.thruDate=thruDate;
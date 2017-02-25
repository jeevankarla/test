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
List allAcctgCodeTransEntryList=FastList.newInstance();
glAccountIdList=[];
glAccountId=parameters.AccountCode;
if(UtilValidate.isEmpty(glAccountId)){
	glAccountIdList=context.get("accountCodeList");
}else{
glAccountIdList.add(glAccountId);
}
reportTypeFlag=parameters.reportTypeFlag;
condList = [];
roId = parameters.division;
 context.roId = roId;
 segmentId = parameters.segment;
 branchList = [];
 condList.clear();
 if(UtilValidate.isNotEmpty(roId)&& !roId.equals("Company")){
	 condList.add(EntityCondition.makeCondition("partyIdFrom" , EntityOperator.EQUALS,roId));
	 condList.add(EntityCondition.makeCondition("roleTypeIdFrom" , EntityOperator.EQUALS,"PARENT_ORGANIZATION"));
	 condList.add(EntityCondition.makeCondition("roleTypeIdTo" , EntityOperator.EQUALS,"ORGANIZATION_UNIT"));
	 condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" , EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
	 List roWiseBranchaList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
	 if(UtilValidate.isNotEmpty(roWiseBranchaList)){
		 branchList= EntityUtil.getFieldListFromEntityList(roWiseBranchaList,"partyIdTo", true);
		 branchList.add(roId);
	 }
 }
 
//Debug.log("ACCOUNT CODE=======From==NEw==GROOVY=============="+glAccountId);
 GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
 //Debug.log("tempDetails===================="+tempDetails);
 fromDate=customTimePeriod.fromDate;
 thruDate=customTimePeriod.thruDate;
 openingBalance = 0;
 if(roId.equals("Company") && segmentId.equals("All")){
	 result=dispatcher.runSync("getGlAccountOpeningBalanceForCostCenter", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)), costCenterId:null, segmentId:null,userLogin:userLogin]);
	 openingBalance = result.get("openingBal");
 }
 else if(roId.equals("Company") && !segmentId.equals("All")){
	 if(segmentId.equals("YARN_SALE")){
		 result=dispatcher.runSync("getGlAccountOpeningBalanceForCostCenter", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)), costCenterId: null, segmentId: "YARN_SALE", userLogin:userLogin]);
		 openingBalance = openingBalance+result.get("openingBal");
		 result=dispatcher.runSync("getGlAccountOpeningBalanceForCostCenter", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)), costCenterId:null, segmentId: "DEPOT_YARN_SALE", userLogin:userLogin]);
		 openingBalance = openingBalance+result.get("openingBal");
	 }
	 else{
		 result=dispatcher.runSync("getGlAccountOpeningBalanceForCostCenter", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)), costCenterId: null, segmentId:segmentId, userLogin:userLogin]);
		 openingBalance = openingBalance+result.get("openingBal");
	 }
 }
 else if(!roId.equals("Company") && segmentId.equals("All")){
	 for (String eachParty : branchList) {
		 result=dispatcher.runSync("getGlAccountOpeningBalanceForCostCenter", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)), costCenterId:eachParty, segmentId:null, userLogin:userLogin]);
		 openingBalance = openingBalance+result.get("openingBal");
	 }
 }
 else{
	 for (String eachParty : branchList) {
		 if(segmentId.equals("YARN_SALE")){
			 result=dispatcher.runSync("getGlAccountOpeningBalanceForCostCenter", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)), costCenterId: eachParty, segmentId: "YARN_SALE", userLogin:userLogin]);
			 openingBalance = openingBalance+result.get("openingBal");
			 result=dispatcher.runSync("getGlAccountOpeningBalanceForCostCenter", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)), costCenterId:eachParty, segmentId: "DEPOT_YARN_SALE", userLogin:userLogin]);
			 openingBalance = openingBalance+result.get("openingBal");
		 }
		 else{
			 result=dispatcher.runSync("getGlAccountOpeningBalanceForCostCenter", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)), costCenterId: eachParty, segmentId: segmentId, userLogin:userLogin]);
			 openingBalance = openingBalance+result.get("openingBal");
		 }
	 }
 }
 
 findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);


 division = parameters.division;
 acctgTransIds = [];
  conditionList = [];
 glAccountIdList.each{glAccountId->
 conditionList.clear();
 conditionList.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.EQUALS,parameters.organizationPartyId));
 if(reportTypeFlag.equals("condensed")){
  conditionList.add(EntityCondition.makeCondition("isPosted" , EntityOperator.EQUALS,"Y"));
 }

/* if(UtilValidate.isNotEmpty(acctgTransIds)){
	 conditionList.add(EntityCondition.makeCondition("acctgTransId" , EntityOperator.IN, acctgTransIds));
 }*/
 if(UtilValidate.isNotEmpty(roId)&& !roId.equals("Company"))
 conditionList.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.IN, branchList));
 if(!segmentId.equals("All") && !segmentId.equals("YARN_SALE"))
 conditionList.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.EQUALS, segmentId));
if(segmentId.equals("YARN_SALE"))
 conditionList.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.IN, UtilMisc.toList("YARN_SALE", "DEPOT_YARN_SALE")));
 
 conditionList.add(EntityCondition.makeCondition("glAccountId" , EntityOperator.EQUALS,glAccountId));
 conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate))));
 conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate))));
 //Debug.log("conditionList========="+conditionList);
 acctgTransEntryListIter = delegator.find("AcctgTransAndEntries", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, ['transactionDate','postedDate'], findOpts);
 // List acctgTransEntryList = delegator.find("AcctgTransAndEntries", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
 // filterByCondition
 acctgTransEntryList=[];
 if(UtilValidate.isNotEmpty(acctgTransEntryListIter)){
	 BigDecimal totDebtAmount = BigDecimal.ZERO;
	 BigDecimal totCrdtAmount = BigDecimal.ZERO;
     Map totalDebtMap= FastMap.newInstance();
	 Map totalCrdtMap= FastMap.newInstance();
	// Debug.log("acctgTransEntryListIter===================="+acctgTransEntryListIter);
	 while(acctgTransEntry= acctgTransEntryListIter.next()) {
	  acctgTransEntryList.addAll(acctgTransEntry);
	  if(UtilValidate.isNotEmpty(acctgTransEntry.debitCreditFlag) && (acctgTransEntry.debitCreditFlag).equals("C")){
		  totCrdtAmount=totCrdtAmount+acctgTransEntry.amount;
	  }
	  if(UtilValidate.isNotEmpty(acctgTransEntry.debitCreditFlag) && (acctgTransEntry.debitCreditFlag).equals("D")){
		  totDebtAmount=totDebtAmount+acctgTransEntry.amount;
	  }
	  allAcctgCodeTransEntryList.addAll(acctgTransEntry);
	 }
	 totalDebtMap.put("partyId","Tootal DebitAmount");
	 totalDebtMap.put("productId",totDebtAmount);
	 allAcctgCodeTransEntryList.addAll(totalDebtMap);
	 
	 totalCrdtMap.put("partyId","Tootal CreditAmount");
	 totalCrdtMap.put("productId",totCrdtAmount);
	 allAcctgCodeTransEntryList.addAll(totalCrdtMap);
	 
	 
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
  context.allAcctgCodeTransEntryList=allAcctgCodeTransEntryList;
  

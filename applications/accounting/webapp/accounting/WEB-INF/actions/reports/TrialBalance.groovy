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
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.accounting.util.*;
import org.ofbiz.party.party.PartyHelper;


partyNameList = [];
parties.each { party ->
	partyName = PartyHelper.getPartyName(party);
	partyNameList.add(partyName);
}
context.partyNameList = partyNameList;
division = parameters.division;
asOnDate = parameters.asOnDate;
  dctx = dispatcher.getDispatchContext();
 glAccountAndHistories =[];
 condList = [];
 condList.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.EQUALS, "Company"));
 if(UtilValidate.isNotEmpty(division) && !division.equals("Company"))
 condList.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.EQUALS, division));
 condList.add(EntityCondition.makeCondition("customTimePeriodId" , EntityOperator.EQUALS,parameters.customTimePeriodId));
 List tempGlAccountAndHistories = delegator.findList("GlAccountAndPartyHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
  //Map lastClosedGlBalances = UtilAccounting.getLastClosedGlBalance(dctx, UtilMisc.toMap("organizationPartyId", parameters.organizationPartyId,"customTimePeriodId",parameters.customTimePeriodId));
lastClosedGlBalanceList =[];
//lastClosedGlBalanceList = lastClosedGlBalances.get("openingGlHistory");
Set<String> partySet = new HashSet<String>(partyIds);

if(UtilValidate.isNotEmpty(division) && !division.equals("Company")){
	Map lastClosedGlBalancesforCostCenter = UtilAccounting.getLastClosedGlBalanceForCostCenter(dctx, UtilMisc.toMap("organizationPartyId", "Company","customTimePeriodId",parameters.customTimePeriodId,"costCenterId",division));
	lastClosedGlBalanceList.addAll(lastClosedGlBalancesforCostCenter.get("openingGlHistory"));
}
if(UtilValidate.isNotEmpty(division) && division.equals("Company")){
	for (String eachParty : partySet) {
		Map	lastClosedGlBalances = UtilAccounting.getLastClosedGlBalance(dctx, UtilMisc.toMap("organizationPartyId", eachParty,"customTimePeriodId",parameters.customTimePeriodId));
		lastClosedGlBalanceList.addAll(lastClosedGlBalances.get("openingGlHistory"));
	
	}
}
if(UtilValidate.isEmpty(division)){
	for (String eachParty : partySet) {
		Map	lastClosedGlBalances = UtilAccounting.getLastClosedGlBalance(dctx, UtilMisc.toMap("organizationPartyId", eachParty,"customTimePeriodId",parameters.customTimePeriodId));
		lastClosedGlBalanceList.addAll(lastClosedGlBalances.get("openingGlHistory"));
	
	}
}
tempGlAccountAndHistories.each { tempGlAccountAndHistory ->
	 tempGlAccountAndHistoryMap =[:];
	 tempGlAccountAndHistoryMap.putAll(tempGlAccountAndHistory);
	 lastClosedGlBalance = EntityUtil.getFirst(EntityUtil.filterByAnd(lastClosedGlBalanceList, UtilMisc.toMap("glAccountId",tempGlAccountAndHistory.get("glAccountId"))))
	 tempGlAccountAndHistoryMap.putAt("openingC",0);
	 tempGlAccountAndHistoryMap.putAt("openingD",0);
	 
	 glAccount = delegator.findOne("GlAccount", [glAccountId : tempGlAccountAndHistory.get("glAccountId")], false);
	 isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
	 
	 if(UtilValidate.isNotEmpty(lastClosedGlBalance)){
		 if(UtilValidate.isNotEmpty(lastClosedGlBalance.getBigDecimal("totalEndingBalance"))){
			 if (isDebitAccount) {
				 if( (lastClosedGlBalance.getBigDecimal("totalEndingBalance")) < 0){
					tempGlAccountAndHistoryMap.putAt("openingC", (lastClosedGlBalance.getBigDecimal("totalEndingBalance")).negate());
				}
				else{
					 tempGlAccountAndHistoryMap.putAt("openingD", lastClosedGlBalance.getBigDecimal("totalEndingBalance"));
				 }
			}
			else{
				 if( (lastClosedGlBalance.getBigDecimal("totalEndingBalance")) < 0){
					 tempGlAccountAndHistoryMap.putAt("openingD", (lastClosedGlBalance.getBigDecimal("totalEndingBalance")).negate());
				 }
				 else{
					 tempGlAccountAndHistoryMap.putAt("openingC", lastClosedGlBalance.getBigDecimal("totalEndingBalance"));
				 }
			 }
			 
			lastClosedGlBalanceList.remove(lastClosedGlBalance);
		}
	}
	tempGlAccountAndHistoryMap.putAt("totalEndingBalance", ((tempGlAccountAndHistoryMap.get("totalPostedDebits")+tempGlAccountAndHistoryMap.get("openingD"))-(tempGlAccountAndHistoryMap.get("totalPostedCredits")+tempGlAccountAndHistoryMap.get("openingC"))));
	glAccountAndHistories.add(tempGlAccountAndHistoryMap);
}
if(UtilValidate.isNotEmpty(lastClosedGlBalanceList)){
	 lastClosedGlBalanceList.each{ tempGlAccountAndHistory ->
		 
		 glAccount = delegator.findOne("GlAccount", [glAccountId : tempGlAccountAndHistory.get("glAccountId")], false);
		 isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
		 tempGlAccountAndHistoryMap =[:];
		 tempGlAccountAndHistoryMap.putAll(tempGlAccountAndHistory);
		 lastClosedGlBalance = EntityUtil.getFirst(EntityUtil.filterByAnd(lastClosedGlBalanceList, UtilMisc.toMap("glAccountId",tempGlAccountAndHistory.get("glAccountId"))))
		 tempGlAccountAndHistoryMap.putAt("openingC",0);
		 tempGlAccountAndHistoryMap.putAt("openingD",0);
		 if(UtilValidate.isNotEmpty(lastClosedGlBalance.getBigDecimal("totalEndingBalance"))){
			 if (isDebitAccount) {
				 if( (tempGlAccountAndHistory.getBigDecimal("totalEndingBalance")) < 0){
					 tempGlAccountAndHistoryMap.putAt("openingC", (tempGlAccountAndHistory.getBigDecimal("totalEndingBalance")).negate());
				 }
				 else{
					 tempGlAccountAndHistoryMap.putAt("openingD", tempGlAccountAndHistory.getBigDecimal("totalEndingBalance"));
				 }
			 }
			 else{
				 if( (tempGlAccountAndHistory.getBigDecimal("totalEndingBalance")) < 0){
					 tempGlAccountAndHistoryMap.putAt("openingD", (tempGlAccountAndHistory.getBigDecimal("totalEndingBalance")).negate());
				 }
				 else{
					 tempGlAccountAndHistoryMap.putAt("openingC", tempGlAccountAndHistory.getBigDecimal("totalEndingBalance"));
				 }
			 }
			 tempGlAccountAndHistoryMap.putAt("totalPostedDebits",0 );
			 tempGlAccountAndHistoryMap.putAt("totalPostedCredits",0 );
			 tempGlAccountAndHistoryMap.putAt("totalEndingBalance", ((tempGlAccountAndHistoryMap.get("totalPostedDebits")+tempGlAccountAndHistoryMap.get("openingD"))-(tempGlAccountAndHistoryMap.get("totalPostedCredits")+tempGlAccountAndHistoryMap.get("openingC"))));
			 glAccountAndHistories.add(tempGlAccountAndHistoryMap);
		}
	 }
}
context.glAccountAndHistories = glAccountAndHistories;

//adding gl totals

tempGlAccountAndHistories = [];
prevGlList = [];
prevTmp = [:];

for(k=0;k<glAccountAndHistories.size();k++){
  eachGlAccountAndHistories = glAccountAndHistories[k];
  
  glAccountId = eachGlAccountAndHistories.glAccountId;
  temp = [:];
  if(prevGlList.contains(glAccountId)){
	  
	  exs = prevTmp.get(glAccountId);
	  exTotalPostedDebits = exs.get("totalPostedDebits");
	  exTotalPostedCredits = exs.get("totalPostedCredits");
	  exTotalEndingBalance = exs.get("totalEndingBalance");
	  exOpeningC = exs.get("openingC");
	  exOpeningD = exs.get("openingD");
	  
	  totalPostedDebits = eachGlAccountAndHistories.totalPostedDebits;
	  totalPostedCredits = eachGlAccountAndHistories.totalPostedCredits;
	  totalEndingBalance = eachGlAccountAndHistories.totalEndingBalance;
	  openingC = eachGlAccountAndHistories.openingC;
	  openingD = eachGlAccountAndHistories.openingD;
	  
	  temp.put("totalPostedDebits", totalPostedDebits + exTotalPostedDebits);
	  temp.put("totalPostedCredits", totalPostedCredits + exTotalPostedCredits);
	  temp.put("totalEndingBalance", totalEndingBalance + exTotalEndingBalance);
	  temp.put("glAccountId",eachGlAccountAndHistories.glAccountId);
	  temp.put("accountCode",eachGlAccountAndHistories.accountCode);
	  temp.put("accountName",eachGlAccountAndHistories.accountName);
	  temp.put("openingC", openingC + exOpeningC);
	  temp.put("openingD", openingD + exOpeningD);
	  
	  prevTmp.put(glAccountId, temp);
	  
  }else{
  
	  prevGlList.add(glAccountId);
  
	  temp.put("totalPostedDebits",eachGlAccountAndHistories.totalPostedDebits);
	  temp.put("totalPostedCredits",eachGlAccountAndHistories.totalPostedCredits);
	  temp.put("totalEndingBalance",eachGlAccountAndHistories.totalEndingBalance);
	  temp.put("glAccountId",eachGlAccountAndHistories.glAccountId);
	  temp.put("accountCode",eachGlAccountAndHistories.accountCode);
	  temp.put("accountName",eachGlAccountAndHistories.accountName);
	  temp.put("openingC",eachGlAccountAndHistories.openingC);
	  temp.put("openingD",eachGlAccountAndHistories.openingD);
	  
	  prevTmp.put(glAccountId, temp);
  }
}

finalList = [];
grandTotMap = [:];
totOpeningBal = 0;
totOpeningD = 0;
totOpeningC = 0;
totDebtAmt = 0;
totCredAmt = 0;
totEndingBal = 0;
for(t=0;t<prevGlList.size();t++){
  glAccountId = prevGlList[t];
  eachGllist = prevTmp.get(glAccountId);
  
  tMap = [:];
  tMap.put("totalPostedDebits",eachGllist.get("totalPostedDebits"));
  tMap.put("totalPostedCredits",eachGllist.get("totalPostedCredits"));
  tMap.put("glAccountId",eachGllist.get("glAccountId"));
  tMap.put("accountCode",eachGllist.get("accountCode"));
  tMap.put("accountName",eachGllist.get("accountName"));
  tMap.put("totalEndingBalance",eachGllist.get("totalEndingBalance"));
  tMap.put("openingC",eachGllist.get("openingC"));
  tMap.put("openingD",eachGllist.get("openingD"));
  
  
  totOpeningBal = totOpeningBal + (eachGllist.get("openingD") - eachGllist.get("openingC"));
  totOpeningD = totOpeningD + eachGllist.get("openingD");
  totOpeningC = totOpeningC + eachGllist.get("openingC");
  totDebtAmt = totDebtAmt + eachGllist.get("totalPostedDebits");
  totCredAmt = totCredAmt + eachGllist.get("totalPostedDebits");
  totEndingBal = totEndingBal + eachGllist.get("totalEndingBalance");
  
  tempMap = [:];
  tempMap.putAll(tMap);
  finalList.add(tempMap);
}
grandTotMap.put("accountName","TOTAL");
grandTotMap.put("totalOpening",totOpeningBal);
grandTotMap.put("openingD",totOpeningD);
grandTotMap.put("openingC",totOpeningC);
grandTotMap.put("totalPostedDebits",totDebtAmt);
grandTotMap.put("totalPostedCredits",totCredAmt);
grandTotMap.put("totalEndingBalance",totEndingBal);
finalList.add(grandTotMap);
context.finalList = finalList;
accountCodeList=[];
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
  GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
  dayBegin = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.fromDate));
  dayEnd = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.thruDate));
  totalDays=UtilDateTime.getIntervalInDays(dayBegin,dayEnd);
 
}
JSONArray dataJSONList= new JSONArray();
if (glAccountAndHistories.size() > 0) {
  JSONObject retObj = new JSONObject();
  if(totalDays > 32){
	  //no need to showAll if then
 }else{
 JSONObject forall = new JSONObject();
 forall.put("value","");
 forall.put("text","All");
 dataJSONList.add(forall);
 }
  glAccountAndHistories.eachWithIndex {sub, idx ->
	  //retObj.put("id",idx+1);
	// retObj.put("item", "");
	  retObj.put("value", sub.accountCode);
	  retObj.put("text", sub.accountName+" ["+sub.accountCode+" ]");
	  dataJSONList.add(retObj);
	  accountCodeList.add(sub.accountCode);
  }
}
context.put("accountCodeList", accountCodeList);
if (dataJSONList.size() > 0) {
  context.dataJSON = dataJSONList.toString();
  Debug.logInfo("dataJSONList="+dataJSONList,"");
  request.setAttribute("dataJSON",dataJSONList);
}

if(UtilValidate.isNotEmpty(parameters.includingGrandTotals)){
	glAccountAndHistoriesTotals=[];
		grandopeningC=0;
		grandopeningD=0
		grandDebits=0;
		grandCredits=0;
		grandEndingBal=0;
		grandTotalMap=[:];
		glAccountAndHistories.each{ eachglacct ->
			grandEndingBal=grandEndingBal+eachglacct.totalEndingBalance;
			
			grandopeningC=grandopeningC+eachglacct.openingC;
			grandopeningD=grandopeningD+eachglacct.openingD;
			grandDebits=grandDebits+eachglacct.totalPostedDebits;
			grandCredits=grandCredits+eachglacct.totalPostedCredits;
		}
		grandTotalMap.put("accountCode", ".");
		 grandTotalMap.put("accountName", "GrandTotals");
		grandTotalMap.put("openingD", grandopeningD);
		grandTotalMap.put("openingC", grandopeningC);
		grandTotalMap.put("totalPostedDebits", grandDebits);
		grandTotalMap.put("totalPostedCredits", grandCredits);
		grandTotalMap.put("totalEndingBalance", grandEndingBal);	
		if("N".equals(parameters.includingGrandTotals)){
		glAccountAndHistoriesTotals.add(grandTotalMap);
		}else if("Y".equals(parameters.includingGrandTotals)){
		glAccountAndHistories.add(grandTotalMap);
		}
   context.glAccountAndHistoriesTotals=glAccountAndHistoriesTotals;
}


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
import javolution.util.FastList;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
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
  asOnDate = parameters.asOnDate;

  dctx = dispatcher.getDispatchContext();
 glAccountAndHistories =[];
 condList = [];
 condList.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.EQUALS,parameters.organizationPartyId));
 condList.add(EntityCondition.makeCondition("customTimePeriodId" , EntityOperator.EQUALS,parameters.customTimePeriodId));
  List tempGlAccountAndHistories = delegator.findList("GlAccountAndHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
	 Map lastClosedGlBalances = UtilAccounting.getLastClosedGlBalance(dctx, UtilMisc.toMap("organizationPartyId", parameters.organizationPartyId,"customTimePeriodId",parameters.customTimePeriodId));
	 lastClosedGlBalanceList =[];
	 lastClosedGlBalanceList = lastClosedGlBalances.get("openingGlHistory");
	 tempGlAccountAndHistories.each { tempGlAccountAndHistory ->
		 
		 tempGlAccountAndHistoryMap =[:];
		 tempGlAccountAndHistoryMap.putAll(tempGlAccountAndHistory);
		 lastClosedGlBalance = EntityUtil.getFirst(EntityUtil.filterByAnd(lastClosedGlBalanceList, UtilMisc.toMap("glAccountId",tempGlAccountAndHistory.get("glAccountId"))))
		 tempGlAccountAndHistoryMap.putAt("openingC",0);
		 tempGlAccountAndHistoryMap.putAt("openingD",0);
		 
		 glAccount = delegator.findOne("GlAccount", [glAccountId : tempGlAccountAndHistory.get("glAccountId")], false);
		 isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
		 
		 if(UtilValidate.isNotEmpty(lastClosedGlBalance)){
			 
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
		tempGlAccountAndHistoryMap.putAt("totalEndingBalance", ((tempGlAccountAndHistoryMap.get("totalPostedDebits")+tempGlAccountAndHistoryMap.get("openingD"))-(tempGlAccountAndHistoryMap.get("totalPostedCredits")+tempGlAccountAndHistoryMap.get("openingC"))));
		glAccountAndHistories.add(tempGlAccountAndHistoryMap);
	 }
	 
	 if(UtilValidate.isNotEmpty(lastClosedGlBalanceList)){
		 lastClosedGlBalanceList.each{ tempGlAccountAndHistory ->
			 
			 glAccount = delegator.findOne("GlAccount", [glAccountId : tempGlAccountAndHistory.get("glAccountId")], false);
			 isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
			 tempGlAccountAndHistoryMap =[:];
			 tempGlAccountAndHistoryMap.putAll(tempGlAccountAndHistory);
			 tempGlAccountAndHistoryMap.putAt("openingC",0);
			 tempGlAccountAndHistoryMap.putAt("openingD",0);
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
  context.glAccountAndHistories = glAccountAndHistories;
  accountCodeList=[];
  if(UtilValidate.isNotEmpty(parameters.customTimePeriodId))
  {
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

  
  
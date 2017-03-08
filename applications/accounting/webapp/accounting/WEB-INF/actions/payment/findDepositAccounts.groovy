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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilMisc;
import java.text.ParseException;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

//Debug.log("parameters.noConditionFind================"+parameters.noConditionFind);

if ("Y".equals(parameters.noConditionFind)) {
   List exprListForParameters = [];
   conditionlist=[];
   finAccountTypeId="";
   finAccountTypeId=parameters.finAccountTypeId;
   AccDate=parameters.AccDate;
   Timestamp Accdate = null;
   if(UtilValidate.isNotEmpty(AccDate)){
   if(AccDate){
		   SimpleDateFormat sdfo = new SimpleDateFormat("dd-mm-yy");
	   try {
		   Accdate = UtilDateTime.toTimestamp(sdfo.parse(parameters.AccDate));
		   	} catch (ParseException e) {
			   Debug.logError(e, "Cannot parse date string: " + AccDate, "");
	   }
   }
   AccDateStart = UtilDateTime.getDayStart(Accdate);
   AccDateEnd = UtilDateTime.getDayEnd(Accdate);
   }
   context.Accdate = UtilDateTime.toDateString(Accdate, "dd-mm-yy");
   if (parameters.finAccountId) {
	   exprListForParameters.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, parameters.finAccountId));
	   }
   if (parameters.finAccountName) {
	   exprListForParameters.add(EntityCondition.makeCondition("finAccountName", EntityOperator.EQUALS, parameters.finAccountName));
	   }
   if (finAccountTypeId) {
	   exprListForParameters.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, finAccountTypeId));
	   }else{
	   exprListForParameters.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.IN,finAccountTypes.finAccountTypeId));
     }
    if (parameters.AccDate) {
		   exprListForParameters.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, AccDateStart));
		   exprListForParameters.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, AccDateEnd));
	   }

	if (parameters.ownerPartyId) {
		   exprListForParameters.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.ownerPartyId));
	   }
	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
	List<String> orderBy = UtilMisc.toList("-fromDate");				
	finAccountList = delegator.findList("FinAccount", paramCond, null, orderBy, null, false);
	List depositAccounts = [];
	for(GenericValue finAccountEntry:finAccountList){
		tempMap = [:];
		tempMap["finAccountId"]=finAccountEntry.finAccountId;
		tempMap["finAccountTypeId"]=finAccountEntry.finAccountTypeId;
		tempMap["finAccountName"]=finAccountEntry.finAccountTypeId;
		tempMap["ownerPartyId"]=finAccountEntry.ownerPartyId;
		tempMap["fromDate"]=finAccountEntry.fromDate;
		tempMap["actualBalance"]=finAccountEntry.actualBalance;
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountEntry.finAccountId));
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, finAccountEntry.ownerPartyId));
		condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List finAccountTransList = delegator.findList("FinAccountTrans", condition, null, null, null, false);
		List depositFinAccTransList = EntityUtil.filterByCondition(finAccountTransList, EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, "DEPOSIT"));
		List withDrawFinAccTransList = EntityUtil.filterByCondition(finAccountTransList, EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, "WITHDRAWAL"));
		BigDecimal depositAmt = BigDecimal.ZERO;
		BigDecimal adjustAmt = BigDecimal.ZERO;
		for(GenericValue depositFinAccTransEntry:depositFinAccTransList){
			depositAmt += depositFinAccTransEntry.amount;
		}
		for(GenericValue withDrawFinAccTransEntry:withDrawFinAccTransList){
			adjustAmt += withDrawFinAccTransEntry.amount;
		}
		tempMap["depositAmt"]=depositAmt;
		tempMap["adjustAmt"]=adjustAmt;
		depositAccounts.add(tempMap);
	}
	   context.depositAccounts=depositAccounts;
	   parameters.AccDate=null;
  }

condList = [];
condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.IN, ["BANK_ACCOUNT","CASH","TAX_CREDIT"]));
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
if(UtilValidate.isNotEmpty(parameters.ownerPartyId) && parameters.ownerPartyId!=null){
	condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.ownerPartyId));
}
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

finAccounts = delegator.findList("FinAccount", cond, null, null, null, false);

context.companyBanksList = finAccounts;

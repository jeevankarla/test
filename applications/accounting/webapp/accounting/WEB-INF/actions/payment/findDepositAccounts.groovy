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
   if(UtilValidate.isNotEmpty(AccDate)){
   Timestamp fromDateTs = null;
   if(AccDate){
		   SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
	   try {
		   fromDateTs = new java.sql.Timestamp(sdfo.parse(AccDate).getTime());	} catch (ParseException e) {
	   }
   }
   AccDateStart = UtilDateTime.getDayStart(fromDateTs);
   AccDateEnd = UtilDateTime.getDayEnd(fromDateTs);
   }
   if (parameters.finAccountId) {
	   exprListForParameters.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, parameters.finAccountId));
	   }
   if (parameters.finAccountName) {
	   exprListForParameters.add(EntityCondition.makeCondition("finAccountName", EntityOperator.EQUALS, parameters.finAccountName));
	   }
   if (finAccountTypeId) {
	   exprListForParameters.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, finAccountTypeId));
	   }
    if (parameters.AccDate) {
		   exprListForParameters.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, AccDateStart));
		   exprListForParameters.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, AccDateEnd));
	   }

	if (parameters.ownerPartyId) {
		   exprListForParameters.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.ownerPartyId));
	   }
	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
	depositAccounts = delegator.findList("FinAccount", paramCond, null, null, null, false);
	   context.depositAccounts=depositAccounts;
	   parameters.AccDate=null;
   	   }

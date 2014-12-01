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


userLogin= context.userLogin;
finAccountId = parameters.finAccountId;
if (!finAccountId && request.getAttribute("finAccountId")) {
  finAccountId = request.getAttribute("finAccountId");
}
//Debug.log("===finAccountId==sadasd="+finAccountId);
finAccount = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);
context.finAccount = finAccount;
context.finAccountId = finAccountId;
tabButtonItem="FindFinAccountAlt";

if (context.finAccount != null) {
	session.setAttribute("ctxFinAccountId",context.finAccountId);
}
context.ctxFinAccountId = session.getAttribute("ctxFinAccountId");
if (context.ctxFinAccountId != null) {
	ctxFinAccount = delegator.findByPrimaryKey("FinAccount", [finAccountId : context.ctxFinAccountId]);
	if(UtilValidate.isNotEmpty(ctxFinAccount)){
		if("BANK_ACCOUNT"==ctxFinAccount.finAccountTypeId){
			tabButtonItem="FindFinAccountAlt";
			context.tabButtonItem="FindFinAccountAlt";
			parameters.tabButtonItem="FindFinAccountAlt";
		}
		else if(parameters.screenFlag && parameters.screenFlag=="NON_BANKACCOUNT"){
			tabButtonItem="FindNonBankAccount";
			context.tabButtonItem="FindNonBankAccount";
			parameters.tabButtonItem="FindNonBankAccount";
		}
	}
	
	context.ctxFinAccount = ctxFinAccount;
}
//Debug.log("===finAccountId==="+finAccountId);
//Debug.log("===tabButtonItem==="+tabButtonItem);

finAccountTypes = delegator.findList("FinAccountAndType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, UtilMisc.toList("DEPOSIT_RECEIPT", "DEPOSIT_PAID")), UtilMisc.toSet("finAccountTypeId"), null, null, false);
finAccountTypeIds = EntityUtil.getFieldListFromEntityList(finAccountTypes, "finAccountTypeId", true);
if(UtilValidate.isNotEmpty(parameters.screenFlag)){
	conditionList = [];
	if(parameters.screenFlag == "DEPOSIT_ACCOUNT"){
		conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.IN, finAccountTypeIds));
	}
	else{
		conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.NOT_EQUAL, "BANK_ACCOUNT"));
		conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.NOT_IN, finAccountTypeIds));
	}
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
	List nonBankAccountsList = delegator.findList("FinAccount", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	
	context.nonBankAccountsList=nonBankAccountsList;
}
if(UtilValidate.isNotEmpty(parameters.findPaymentMethodType)){
	conditionList = [];
	if(UtilValidate.isNotEmpty(finAccount) && ("BANK_ACCOUNT"==finAccount.finAccountTypeId)){
	conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.NOT_LIKE, "%_PAYOUT%"));
	conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "MONEY"));
	}
	if(UtilValidate.isNotEmpty(finAccount) && ("BANK_ACCOUNT"!=finAccount.finAccountTypeId) ){
	conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.NOT_LIKE, "%_PAYOUT%"));
	conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "STATUTORY"));
	}
	List paymentMethodTypeList = delegator.findList("PaymentMethodType", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	context.paymentMethodTypeList=paymentMethodTypeList;
}
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
if(UtilValidate.isNotEmpty(parameters.fromTransDate)){
	Timestamp daystart;
try {
	daystart = UtilDateTime.toTimestamp(dateFormat.parse(parameters.fromTransDate));
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + parameters.fromTransDate, ""); 
} 
	parameters.fromTransactionDate=UtilDateTime.getDayStart(daystart);
	parameters.fromTransDate=null;
}
if(UtilValidate.isNotEmpty(parameters.thruTransDate)){
	Timestamp dayend;
	try {
		dayend = UtilDateTime.toTimestamp(dateFormat.parse(parameters.thruTransDate));
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.thruTransDate, "");	   
	}
	parameters.thruTransactionDate=UtilDateTime.getDayEnd(dayend);
	parameters.thruTransDate=null;
}



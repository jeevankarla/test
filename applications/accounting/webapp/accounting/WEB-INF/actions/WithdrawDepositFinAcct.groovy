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

userLogin= context.userLogin;

finAccountId = parameters.finAccountId;
if (!finAccountId && request.getAttribute("finAccountId")) {
  finAccountId = request.getAttribute("finAccountId");
}
finAccount = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);

context.finAccount = finAccount;
context.finAccountId = finAccountId;

if (context.finAccount != null) {
	session.setAttribute("ctxFinAccountId",context.finAccountId);
}

context.ctxFinAccountId = session.getAttribute("ctxFinAccountId");

ownerParty = "";
if(finAccount){
	ownerParty = finAccount.ownerPartyId;
}
partyAccountsList = [];
conditionList = [];
if(screenFlag == "DEPOSIT_ACCOUNT" && ownerParty){
	/*conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "DEPOSIT_ACCOUNT"));
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerParty));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
	*/
	//partyAccountsList = delegator.findList("FinAccount", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	partyAccountsList.add(finAccount);
}

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
companyAccountsList = delegator.findList("FinAccount", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
if(parameters.finAccountTransTypeId == "DEPOSIT"){
	context.partyAccountsList = companyAccountsList;
	context.companyAccountsList = partyAccountsList;
}
else{
	context.partyAccountsList = partyAccountsList;
	context.companyAccountsList = companyAccountsList;
}

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
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilMisc;

conditionList = [];
conditionList.add(EntityCondition.makeCondition("glAccountTypeId", EntityOperator.NOT_EQUAL, "FIN_ACCOUNT"));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentTypeId", EntityOperator.NOT_EQUAL, "FIN_ACCOUNT"),EntityOperator.OR, 
	EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, null)));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
glAcctType = delegator.findList("GlAccountType", cond, null, null, null, false);

glAcctTypeIds = EntityUtil.getFieldListFromEntityList(glAcctType, "glAccountTypeId", false);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountTypeId", EntityOperator.IN, glAcctTypeIds), EntityOperator.OR, EntityCondition.makeCondition("glAccountTypeId", EntityOperator.EQUALS, null)));
conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
conditionList.add(EntityCondition.makeCondition("accountCode", EntityOperator.NOT_EQUAL, null));
condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
glAccounts = delegator.findList("GlAccountOrganizationAndClass", condExpr, null, null, null, false);
context.glAccounts = glAccounts;

partyClsGrpList =  delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, UtilMisc.toList("REGIONAL_OFFICE","BRANCH_OFFICE","COMPANY")),null,null,null,false);
context.partyClsGrpList=partyClsGrpList;

JSONObject glAccountDescriptionJSON = new JSONObject();
JSONArray glAccountJSON = new JSONArray();
glAccounts.each{eachItem ->
	
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.glAccountId);
	newObj.put("label", eachItem.glAccountId+" [ "+eachItem.glAccountClassId+"]"+" [ "+eachItem.accountName+"]");
	glAccountJSON.add(newObj);
	glAccountDescriptionJSON.put(eachItem.glAccountId, eachItem.accountName);
}
context.glAccountJSON = glAccountJSON;
context.glAccountDescriptionJSON = glAccountDescriptionJSON;


acctgTransId = parameters.acctgTransId;
tempCostCenterId="";
acctgTransconditionList = [];
acctgTransconditionList.add(EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS, acctgTransId));
acctgTransconditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
acctgTranscond = EntityCondition.makeCondition(acctgTransconditionList, EntityOperator.AND);
acctgTrans = EntityUtil.getFirst(delegator.findList("AcctgTransRole", acctgTranscond, null, null, null, false));
if(UtilValidate.isNotEmpty(acctgTrans)){
	tempCostCenterId = acctgTrans.partyId;
}
context.tempCostCenterId=tempCostCenterId;



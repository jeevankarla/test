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
conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.NOT_EQUAL, "FIN_ACCOUNT"));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
glAcctType = delegator.findList("GlAccountType", cond, null, null, null, false);

glAcctTypeIds = EntityUtil.getFieldListFromEntityList(glAcctType, "glAccountTypeId", false);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("glAccountTypeId", EntityOperator.IN, glAcctTypeIds));
conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
conditionList.add(EntityCondition.makeCondition("accountCode", EntityOperator.NOT_EQUAL, null));
condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
glAccounts = delegator.findList("GlAccountOrganizationAndClass", condExpr, null, null, null, false);
context.glAccounts = glAccounts;
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

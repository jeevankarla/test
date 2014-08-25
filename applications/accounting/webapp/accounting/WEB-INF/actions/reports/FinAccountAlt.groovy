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

import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
condList = [];
finAccLst=[];
condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS , "BANK_ACCOUNT"));
condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS , "Company"));
cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
finAccList = delegator.findList("FinAccount", cond, null, null, null, false);
if (UtilValidate.isNotEmpty(finAccList)) {
finAccList.each{ finAcc->
	finAccMap=[:];
	finAccMap["finAccountId"]=finAcc.get("finAccountId");
	finAccMap["finAccountName"]=finAcc.get("finAccountName");
	finAccMap["actualBalance"]=finAcc.get("actualBalance");;
	finAccMap["availableBalance"]=finAcc.get("availableBalance");
	finAccLst.add(finAccMap);
}
}
context.finAccList=finAccLst;


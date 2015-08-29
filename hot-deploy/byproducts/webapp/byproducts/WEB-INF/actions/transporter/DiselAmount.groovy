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
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.service.DispatchContext;
dctx = dispatcher.getDispatchContext();
userLogin= context.userLogin;

conditionList =[];
facilityRatefinalMap=[:];
facilityRateList=[];
if(parameters.rateTypeId){
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, parameters.rateTypeId)));
}else{
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, "TRANSPORTER_MRGN")));
}
if(parameters.facilityId){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, parameters.facilityId)));
}
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<String> orderBy = UtilMisc.toList("facilityId");
facilityRateList = delegator.findList("FacilityRate", condition, null, orderBy, null, false);
//facilityRateList = EntityUtil.filterByDate(facilityRateList);
context.facilityRateList=facilityRateList;

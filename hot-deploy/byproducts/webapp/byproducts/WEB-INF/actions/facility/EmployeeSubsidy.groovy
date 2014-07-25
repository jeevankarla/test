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
import org.ofbiz.party.party.PartyHelper;
dctx = dispatcher.getDispatchContext();
userLogin= context.userLogin;

conditionList =[];
facilityfinalMap=[:];
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "EMPSUBISDY_ROLE")));
facilityList=[];
if(parameters.facilityId){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, parameters.facilityId)));
}
if(parameters.partyId){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,parameters.partyId)));
}
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
facilityPartyList = delegator.findList("FacilityParty", condition, null, UtilMisc.toList("facilityId"), null, false);
if(UtilValidate.isNotEmpty(facilityPartyList)){
   partyIds =EntityUtil.getFieldListFromEntityList(facilityPartyList, "partyId", true);
}

conditionList.clear();
if(parameters.unionCode){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.unionCode)));
}
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "Unions")));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE")));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIds)));
cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
partyList = delegator.findList("PartyRelationship", cond, null, null, null, false);



partyIdsList = 	EntityUtil.getFieldListFromEntityList(partyList, "partyIdTo", true);
if(parameters.unionCode){
	facilityPartyList = EntityUtil.filterByAnd(facilityPartyList, [EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsList)]);
}

facilityPartyList.each{ facility->
		facilityMap=[:];
		facilityMap["facilityId"]=facility.get("facilityId");
		partyId=facility.get("partyId");
		
		
		facilityMap["partyId"]=partyId;
		partyName=PartyHelper.getPartyName(delegator, partyId, false);
		facilityMap["partyName"]=partyName;
		facilityMap["fromDate"]=UtilDateTime.toDateString(facility.get("fromDate"), "dd/MM/yyyy");
		if(UtilValidate.isNotEmpty(facilityPartyList)){
		facilityMap["thruDate"]=UtilDateTime.toDateString(facility.get("thruDate"), "dd/MM/yyyy");
		}
		partyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId), null, null, null, false);
		if(UtilValidate.isNotEmpty(partyRelationship)){
			facilityMap["unionCode"]=partyRelationship.get(0).get("partyIdFrom");
		    partyGroup = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyRelationship.get(0).get("partyIdFrom")), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyGroup)){
					facilityMap["unionName"]=partyGroup.get(0).get("groupName");
			}
		}
		facilityList.add(facilityMap);
}
context.facilityPartyList=facilityList;
context.facilityList=facilityList;
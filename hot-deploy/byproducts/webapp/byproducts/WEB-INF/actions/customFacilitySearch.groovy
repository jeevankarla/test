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


import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

facilityId = parameters.facilityId;
facilityId_op = parameters.facilityId_op;
ownerPartyId = parameters.ownerPartyId;
facilityName = parameters.facilityName;
facilityName_op = parameters.facilityName_op;
categoryTypeEnum = parameters.categoryTypeEnum;
if(UtilValidate.isNotEmpty(context.listIt)){
	facilities = (context.listIt).getCompleteList();
	conditionList = [];
	if(facilityName){
		if(facilityName_op == "contains"){
			conditionList.add(EntityCondition.makeCondition("facilityName", EntityOperator.LIKE, "%"+facilityName+"%"));
		}
		else if(facilityName_op == "notEqual"){
			conditionList.add(EntityCondition.makeCondition("facilityName", EntityOperator.NOT_EQUAL, facilityName));
		}
		else if(facilityName_op == "equal"){
			conditionList.add(EntityCondition.makeCondition("facilityName", EntityOperator.EQUALS, facilityName));
		}
		else if(facilityName_op == "empty"){
			conditionList.add(EntityCondition.makeCondition("facilityName", EntityOperator.EQUALS, null));
		}
		else{
			conditionList.add(EntityCondition.makeCondition("facilityName", EntityOperator.LIKE, facilityName+"%"));
		}
		
	}
	if(categoryTypeEnum){
		conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, categoryTypeEnum));
	}
	if(ownerPartyId){
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId));
	}
	if(facilityId){
		if(facilityName_op == "contains"){
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.LIKE, "%"+facilityId+"%"));
		}
		else if(facilityName_op == "notEqual"){
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.NOT_EQUAL, facilityId));
		}
		else if(facilityName_op == "equal"){
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		}
		else if(facilityName_op == "empty"){
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, null));
		}
		else{
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.LIKE, facilityId+"%"));
		}
	}
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "BOOTH"));
	conditionList.add(EntityCondition.makeCondition("byProdRouteId", EntityOperator.NOT_EQUAL, null));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	finalFacilities = delegator.findList("Facility", condition, null, null, null, false);
	context.listIt = finalFacilities;
}
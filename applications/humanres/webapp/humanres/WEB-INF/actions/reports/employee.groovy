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
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityConditionBuilder;

orgId=parameters.get("partyId");
List employeeListInner = [];
List employeeList =[];
List internalOrgs=[];
exprBldr = new org.ofbiz.entity.condition.EntityConditionBuilder();
if(orgId != null){
	internalOrgs = delegator.findByAnd("PartyRelationshipAndDetail", [partyIdFrom : orgId, partyRelationshipTypeId : "GROUP_ROLLUP"],["groupName"]);
	internalOrgs.each { internalOrg ->
		 condList = exprBldr.AND() {
        EQUALS(partyIdFrom: internalOrg.partyIdTo)
        EQUALS(roleTypeIdTo: "EMPLOYEE")        
    }
		depEmployeeList= delegator.findList("EmploymentAndPerson", condList, null, null,  null, false);		
		employeeListInner.addAll(depEmployeeList);	
	}
	condList = exprBldr.AND() {
        EQUALS(partyIdFrom: orgId )
        EQUALS(roleTypeIdTo: "EMPLOYEE") 
    }
      
	employeeListInner.addAll(delegator.findList("EmploymentAndPerson", condList,null, null, null, false));
	employeeListInner.each{ employee ->
		Map contactDetailsMap=[:];
		List conditionList=[];
		Map employeeSub=[:];
		employee.fromDate = UtilDateTime.toDateString(employee.fromDate, "dd/MM/yyyy");
		
		conditionList = UtilMisc.toList(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee.partyIdTo));
        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedFromDate", EntityOperator.EQUALS, null), EntityOperator.OR,
        	EntityCondition.makeCondition("estimatedFromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())));
        	
        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
        		EntityCondition.makeCondition("estimatedThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
		List<GenericValue> employeePositionList = delegator.findList("EmplPosition", condition, null, null, null, false);
		if(employeePositionList){
			contactDetailsMap.employeePosition=(employeePositionList.get(0)).emplPositionId;
		}
		partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: employee.partyIdTo, userLogin: userLogin]);
		contactDetailsMap.phoneNumber = partyTelephone.contactNumber;
		partyEmail= dispatcher.runSync("getPartyEmail", [partyId: employee.partyIdTo, userLogin: userLogin]);
		contactDetailsMap.emailAddress = partyEmail.emailAddress;
		partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId: employee.partyIdTo, userLogin: userLogin]);
		contactDetailsMap.postalAddress = partyPostalAddress.address1+partyPostalAddress.address2+partyPostalAddress.city+"-"+partyPostalAddress.postalCode;
		employeeSub.putAll(employee);
		employeeSub.putAll(contactDetailsMap);
		employeeList.add(employeeSub);
	}
	
}
employeeList = UtilMisc.sortMaps(employeeList, UtilMisc.toList("partyIdTo"));
context.employeeList=employeeList;

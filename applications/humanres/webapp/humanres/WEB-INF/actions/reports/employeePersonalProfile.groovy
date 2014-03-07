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

partyId=parameters.employeeId;
orgId=parameters.partyIdFrom;
List employeeProfileList =[];
List conditionList =[];
Map contactDetailsMap=[:];

exprBldr = new org.ofbiz.entity.condition.EntityConditionBuilder();
if(partyId != null  && UtilValidate.isNotEmpty(partyId)){   
		conditionList = UtilMisc.toList(
		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	   depEmployeeList= delegator.findList("EmploymentAndPerson", condition, null, conditionList = UtilMisc.toList("-fromDate"),  null, false);
	   
	   if(depEmployeeList){
		   partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: partyId, userLogin: userLogin]);
		   contactDetailsMap.phoneNumber = partyTelephone.contactNumber;
		   partyEmail= dispatcher.runSync("getPartyEmail", [partyId: partyId, userLogin: userLogin]);
		   contactDetailsMap.emailAddress = partyEmail.emailAddress;
		   partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId: partyId, userLogin: userLogin]);
		   contactDetailsMap.postalAddress = partyPostalAddress.address1+partyPostalAddress.address2+partyPostalAddress.city+"-"+partyPostalAddress.postalCode;
	  
		   contactDetailsMap.putAll(depEmployeeList.get(0));
	   }
	   employeeProfileList.add( contactDetailsMap);
	   
   
 
   
}
if(orgId != null && (partyId == null || UtilValidate.isEmpty(partyId))){
	List employeeListInner=[];
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
	  
	employeeListInner.addAll(delegator.findList("EmploymentAndPerson", condList, null, null, null, false));
	employeeListInner.each{ employee ->
		
		Map employeeSub=[:];		
	
		
		partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: employee.partyIdTo, userLogin: userLogin]);
		contactDetailsMap.phoneNumber = partyTelephone.contactNumber;
		partyEmail= dispatcher.runSync("getPartyEmail", [partyId: employee.partyIdTo, userLogin: userLogin]);
		contactDetailsMap.emailAddress = partyEmail.emailAddress;
		partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId: employee.partyIdTo, userLogin: userLogin]);
		contactDetailsMap.postalAddress = partyPostalAddress.address1+partyPostalAddress.address2+partyPostalAddress.city+"-"+partyPostalAddress.postalCode;
		contactDetailsMap.postalAddress = (contactDetailsMap.postalAddress).toString().replace("null", "");
		employeeSub.putAll(employee);
		employeeSub.putAll(contactDetailsMap);
		employeeProfileList.add(employeeSub);
	}
	
}

employeeProfileList = UtilMisc.sortMaps(employeeProfileList, UtilMisc.toList("partyIdTo"));
context.employeeProfileList=employeeProfileList;
context.employeeId = partyId;
context.partyIdFrom = orgId;
context.orgParties=employeeProfileList.partyIdTo;
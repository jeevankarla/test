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
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.party.contact.ContactMechWorker;


List employment=[];
List conditionList =[];

customTimePeriod=delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("customTimePeriodId", parameters.customTimePeriodId), null,null, null, false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod[0].fromDate);
thruDate=UtilDateTime.toTimestamp(customTimePeriod[0].thruDate);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);

conditionList =UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyIdFrom),
EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate),
EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
employment = delegator.findList("Employment",condition,null,null,null,false);
context.employment=employment;

empdata = delegator.findList("PartyPersonAndEmployeeDetail", EntityCondition.makeCondition("partyId", EntityOperator.IN, employment.partyIdTo), null, null, null, false);
context.empdata=empdata;





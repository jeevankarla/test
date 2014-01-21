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

import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

facility = context.facility;
List exprList = [];
List facilitySubscriptionDetails = [];
List listSubscriptionProducts = [];
List listSubscriptionProductCardType = [];
exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facility.facilityId));
exprList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "AM"),
				EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "PM"),EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)],EntityOperator.OR));

cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
facilitySubscriptionDetails = delegator.findList("Subscription", cond, null, null, null, false);
subscriptionIds = EntityUtil.getFieldListFromEntityList(facilitySubscriptionDetails, "subscriptionId", true);
exprList.clear();
exprList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.IN, subscriptionIds));
exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.NOT_EQUAL, "CARD"));
def orderBy = UtilMisc.toList("-fromDate");
cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
listSubscriptionProducts = delegator.findList("SubscriptionProduct", cond, null, orderBy, null, false);

// populating card type list
exprList.clear();
exprList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.IN, subscriptionIds));
exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CARD"));

cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
listSubscriptionProductCardType = delegator.findList("SubscriptionProduct", cond, null, orderBy, null, false);



context.listSubscriptionProductCardType = listSubscriptionProductCardType;
context.listSubscriptionProducts = listSubscriptionProducts;
context.facilitySubscriptionDetails = facilitySubscriptionDetails;

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
import org.ofbiz.network.NetworkServices;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.byproducts.ByProductServices;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

facilityId = parameters.facilityId;
orderTypeId = parameters.orderTypeId;
conditionList = [];
productList = [];
lastIndentDate = null;
//JSONArray prodList = new JSONArray();
if(facilityId){
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	subscription = delegator.findList("Subscription", condition, null, null, null, false);
	if(subscription.size()==1){
		subscriptionId = subscription.getAt(0).getAt("subscriptionId");
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
		conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, orderTypeId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		subscriptionProdList = delegator.findList("SubscriptionProduct", condition, null, ["-fromDate"], null, false);
		if(subscriptionProdList){
			productId = subscriptionProdList.getAt(0).getAt("productId");
			lastIndentDate = subscriptionProdList.getAt(0).getAt("fromDate");
			fromDate = UtilDateTime.getDayStart(lastIndentDate);
			thruDate = UtilDateTime.getDayEnd(lastIndentDate);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
			conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, orderTypeId));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO , fromDate));
			conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO , thruDate));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			prodList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, null, null, false);
			if(prodList.size()>0){
				prodList.each{eachItem ->
					prodMap = [:];
					lastIndentDate = eachItem.getAt("fromDate");
					productId = eachItem.getAt("productId");
					quantity = eachItem.getAt("quantity");
					prodMap.productId = productId;
					prodMap.lastQuantity = quantity;
					productList.add(prodMap);
				}
			}
		}
	}
}
if(lastIndentDate){
	lastIndentDate = UtilDateTime.toDateString(lastIndentDate, "MMMM dd, yyyy");
}
int indentSize = productList.size();
context.productList = productList;
context.facilityId = facilityId;
context.orderTypeId = orderTypeId;
context.lastIndentDate = lastIndentDate;
context.indentSize = indentSize;
products = ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
JSONArray productItemsJSON = new JSONArray();
products.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.productId);
	newObj.put("label",eachItem.productId + " [" + eachItem.productName + "]");
	productItemsJSON.add(newObj);
}
context.productItemsJSON = productItemsJSON;


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

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;
import  org.ofbiz.network.NetworkServices;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.math.RoundingMode;

rounding = RoundingMode.HALF_UP;
productList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.productList = productList;

List exprList = [];

if (parameters.supplyDate) {
	supplyDate = UtilDateTime.getTimestamp(parameters.supplyDate);
}
else {
	supplyDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), 1)
}
context.supplyDate = UtilDateTime.toDateString(supplyDate, "dd MMMMM, yyyy");

if (parameters.productSubscriptionTypeId) {
	productSubscriptionTypeId = parameters.productSubscriptionTypeId;
}
else {
	productSubscriptionTypeId = "ALL";
}
if(parameters.facilityId){
	facilityId = parameters.facilityId;
}else{
	facilityId = context.facilityId;
}
if(facilityId){
	facility = delegator.findOne("Facility",[facilityId : facilityId], false);
	if (facility == null || (!facility.getString("facilityTypeId").equals("ZONE") && (!facility.getString("facilityTypeId").equals("ROUTE")) &&  !facility.getString("facilityTypeId").equals("BOOTH")) ) {
		Debug.logInfo("Facility Id '" + facilityId + "' is not a booth, route or zone!","");
		context.errorMessage = "Facility Id '" + facilityId + "' is not a booth, route or zone!";
		return;
	}
	if(facility.getString("facilityTypeId").equals("ZONE")){
		exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, NetworkServices.getZoneBooths(delegator,facilityId)));
	}else if (facility.getString("facilityTypeId").equals("ROUTE")){
		exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, NetworkServices.getRouteBooths(delegator,facilityId)));
	}else{
		exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	}
}
hideSearch ="Y";
if(parameters.hideSearch){
	hideSearch = parameters.hideSearch;
}
if(context.hideSearch){
	hideSearch = context.hideSearch;
}
dayBegin = UtilDateTime.getDayStart(supplyDate, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(supplyDate, timeZone, locale);
supplyTypeMap =[:];
supplyTypeMap["SPECIAL_ORDER"] = "Spl. Order";
supplyTypeMap["CASH"] = "Cash";
supplyTypeMap["CASH_FS"] = "Festival";
supplyTypeMap["CARD"] = "Card";
supplyTypeMap["CREDIT"] = "Credit";
supplyTypeMap["ALL"] = "All";

boothsResultMap = [:];

if(hideSearch == "N") {
	prodQuantityInitMap = [:];
	productList.each{ product ->
		prodQuantityInitMap[product.productId] = 0;
	}
	exprList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
	exprList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd),
				   EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));
	if (productSubscriptionTypeId != "ALL") {
		exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
	}
	if (parameters.subscriptionTypeId) {
		subscriptionTypeId = parameters.subscriptionTypeId;
		if(subscriptionTypeId.equals("AM")){
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId) ,EntityOperator.OR ,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
			
		}else{
			exprList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
		}		
	}
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);   
	quotaSubProdList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, ["parentFacilityId", "facilityId"], null, false);
  
//Debug.logInfo("quotaSubProdList="+quotaSubProdList,"");
	totalsMap =[:];
	totalsMap.putAll(prodQuantityInitMap);
	for (int i=0; i < quotaSubProdList.size(); i++) {
		quotaSubProd = quotaSubProdList.get(i);
		boothId = quotaSubProd.facilityId;
		tempQuantity = (quotaSubProd.quantity).setScale(1,rounding);
		quantity = tempQuantity;
		totalsMap[quotaSubProd.productId] += quantity;
		boothTotalsMap = boothsResultMap[boothId];
		if (boothTotalsMap == null) {
			boothTotalsMap = [:];
			boothTotalsMap.putAll(prodQuantityInitMap);
			boothTotalsMap[quotaSubProd.productId] = quantity;
			boothsResultMap[boothId] = [:];
			boothsResultMap[boothId].putAll(boothTotalsMap);
			boothsResultMap[boothId].put("routeId", (quotaSubProd.parentFacilityId).toUpperCase());
			boothsResultMap[boothId].put("boothName", (quotaSubProd.facilityName).toUpperCase());
			boothsResultMap[boothId].put("supplyType", supplyTypeMap[productSubscriptionTypeId]);
			continue;
		}
		boothTotalsMap[quotaSubProd.productId] += quantity;
	}
	boothsResultMap["Total"] = [:];
	boothsResultMap["Total"].putAll(totalsMap);
}
context.boothsResultMap = boothsResultMap;
//Debug.logInfo("boothsResultMap="+boothsResultMap,"");
JSONArray dataJSONList= new JSONArray();
Iterator mapIter = boothsResultMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	boothId =entry.getKey();
	boothTotalsMap = boothsResultMap[boothId];
	JSONObject newObj = new JSONObject(boothTotalsMap);
	newObj.put("id",boothId);
	newObj.put("boothId",boothId);
	dataJSONList.add(newObj);
}
//Debug.logInfo("dataJSONList="+dataJSONList.toString(),"");
context.dataJSON = dataJSONList.toString();

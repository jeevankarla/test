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
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.math.RoundingMode;
import org.ofbiz.service.ServiceUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.Delegator;

result = ServiceUtil.returnSuccess();

rounding = RoundingMode.HALF_UP;

List exprList = [];

if (parameters.supplyDate) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMMM, yyyy");
	try {
		supplyDate = UtilDateTime.toTimestamp(dateFormat.parse(parameters.supplyDate));
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.supplyDate, "");
	}
}
else {
	supplyDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), 1)
}
context.supplyDate = UtilDateTime.toDateString(supplyDate, "dd MMMMM, yyyy");

if(parameters.facilityId){
	facilityId = parameters.facilityId;
}else{
	facilityId = context.facilityId;
}

hideSearch ="Y";
if(parameters.hideSearch){
	hideSearch = parameters.hideSearch;
}
if(context.hideSearch){
	hideSearch = context.hideSearch;
}
context.hideSearch = hideSearch;

dayBegin = UtilDateTime.getDayStart(supplyDate);
dayEnd = UtilDateTime.getDayEnd(supplyDate);

boothsResultMap = [:];
routeMap = [:];
if(hideSearch == "N") {
	facilityId = parameters.facilityId;
	productId = parameters.productId;
	exprList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
	exprList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd));
	if(facilityId){
		exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	}
	if(productId){
		exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	}
	if(parameters.routeId){
		exprList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, parameters.routeId));
	}
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);  
	quotaSubProdList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, ["productId", "facilityId"], null, false);
	prodList = EntityUtil.getFieldListFromEntityList(quotaSubProdList,"productId",true);
	prodQuantityInitMap = [:];
	prodList.each{prod ->
		prodQuantityInitMap[prod] = 0;
	}
	productList = prodList;
	productList = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, prodList), UtilMisc.toSet("productId", "brandName"), ["productId"], null, false);
	context.productList = productList;
	totalsMap =[:];
	totalsMap.putAll(prodQuantityInitMap);
	
	for (int i=0; i < quotaSubProdList.size(); i++) {
		routesList = [];
		quotaSubProd = quotaSubProdList.get(i);
		boothId = quotaSubProd.facilityId;
		routeId = quotaSubProd.sequenceNum;
		if(routeMap.containsKey(boothId)){
			tempRouteList = routeMap.get(boothId);
			if(!tempRouteList.contains(routeId)){
				tempRouteList.add(routeId);
				routeMap.putAt(boothId, tempRouteList);
			}
		}
		else{
			routesList.add(routeId);
			routeMap.putAt(boothId, routesList);
		}
		tempQuantity = (quotaSubProd.quantity).setScale(1,rounding);
		quantity = tempQuantity;
		totalsMap[quotaSubProd.productId] += quantity;
		boothTotalsMap = boothsResultMap[boothId];
		if (boothTotalsMap == null) {
			boothTotalsMap = [:];
			boothTotalsMap.putAll(prodQuantityInitMap);
			boothTotalsMap[quotaSubProd.productId] = quantity;
			boothTotalsMap["createdUser"] = quotaSubProd.lastModifiedByUserLogin;
			boothTotalsMap["entryDate"] = quotaSubProd.lastModifiedDate;
			boothTotalsMap["routeId"] = quotaSubProd.sequenceNum;
			boothsResultMap[boothId] = [:];
			boothsResultMap[boothId].putAll(boothTotalsMap);
			continue;
		}
		boothTotalsMap[quotaSubProd.productId] += quantity;
	}
	boothsResultMap["Total"] = [:];
	boothsResultMap["Total"].putAll(totalsMap);
}
context.boothsResultMap = boothsResultMap;
context.indentCount = boothsResultMap.size() - 1;

JSONArray dataJSONList= new JSONArray();
Iterator mapIter = boothsResultMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	boothId =entry.getKey();
	boothTotalsMap = boothsResultMap[boothId];
	entryDate = boothTotalsMap.get("entryDate");
	rtList = routeMap.get(boothId);
	routeStr = "";
	index = 0;
	if(rtList){
		rtList.each{eachRoute ->
			index += 1;
			if(index == rtList.size()){
				routeStr += eachRoute;
			}else{
				routeStr = routeStr+eachRoute+",";
			}
		}
	}
	createdUser = boothTotalsMap.get("createdUser");
	JSONObject newObj = new JSONObject(boothTotalsMap);
	newObj.put("id",boothId);
	newObj.put("boothId",boothId);
	newObj.put("routeId",routeStr);
	entryDate = UtilDateTime.toDateString(entryDate, "dd/MM/yyyy");
	newObj.put("entryDate",entryDate);
	newObj.put("createdUser",createdUser);
	dataJSONList.add(newObj);
}
context.dataJSON = dataJSONList.toString();
result.boothsResultMap = boothsResultMap; 
return result;

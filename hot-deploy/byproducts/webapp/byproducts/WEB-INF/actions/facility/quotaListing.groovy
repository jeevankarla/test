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
import java.util.List;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
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
List boothInActiveList = (List) (ByProductNetworkServices.getAllActiveOrInactiveBooths(delegator ,null ,supplyDate)).get("boothInActiveList");

stopShipList =[];
if(UtilValidate.isNotEmpty(boothInActiveList)){
	stopShipList.addAll(EntityUtil.getFieldListFromEntityList(boothInActiveList, "facilityId", true));
}
boothsResultMap = [:];
routeMap = [:];
tripMap = [:];
BoothRouteWiseMap= [:]
boothFinalList = [];
if(hideSearch == "N") {
	facilityId = parameters.facilityId;
	productId = parameters.productId;
	tripId = parameters.tripId;
	exprList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
	exprList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd),
				   EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));
	if(facilityId ){
		exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	}
	if(productId){
		exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	}
	if(tripId){
		exprList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
	}
	if(parameters.routeIds){
		exprList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.IN, parameters.routeIds));
	}
	if(parameters.routeId){
		exprList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, parameters.routeId));
	}
	if (parameters.productSubscriptionTypeId) {
		productSubscriptionTypeId = parameters.productSubscriptionTypeId;
	}
	else {
		productSubscriptionTypeId = "ALL";
	}
	
	
	if (productSubscriptionTypeId != "ALL") {
		exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
	}else if( parameters.productSubscriptionTypeIds){
		exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, parameters.productSubscriptionTypeIds));
	}
	if (parameters.subscriptionTypeId) {
		subscriptionTypeId = parameters.subscriptionTypeId;
		if(subscriptionTypeId.equals("AM")){
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId) ,EntityOperator.OR ,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
			
		}else{
			exprList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
		}
	}
	facilityList = [];
	
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND); 
	quotaSubProdList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, ["sequenceNum", "tripNum", "facilityId"], null, false);
	// filter out inactive facilities
	quotaSubProdList = EntityUtil.filterByCondition(quotaSubProdList, EntityCondition.makeCondition("facilityId", EntityOperator.NOT_IN ,stopShipList));
	prodList = EntityUtil.getFieldListFromEntityList(quotaSubProdList,"productId",true);
	facilityList = EntityUtil.getFieldListFromEntityList(quotaSubProdList, "facilityId", true);
	routeList = EntityUtil.getFieldListFromEntityList(quotaSubProdList, "sequenceNum", true);
	facilityList.addAll(routeList);
	
	facilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityList), UtilMisc.toSet("facilityId", "facilityName"), null, null, false);
	facilityMap = [:];
	facilities.each{ eachFac ->
		facilityMap.put(eachFac.facilityId, eachFac.facilityName);
	}
	prodQuantityInitMap = [:];
	prodList.each{prod ->
		prodQuantityInitMap[prod] = 0;
	}
	productList = prodList;
	productList = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, prodList), UtilMisc.toSet("productId", "description","brandName"), ["sequenceNum"], null, false);
	context.productList = productList;
	
	totalsMap =[:];
	totalsMap.putAll(prodQuantityInitMap);
	
	
	for (int i=0; i < quotaSubProdList.size(); i++) {
		routesList = [];
		tripsList = [];
		quotaSubProd = quotaSubProdList.get(i);
		boothId = quotaSubProd.facilityId;
		routeId = quotaSubProd.sequenceNum;
		tripId = quotaSubProd.tripNum;
		if(routeMap.containsKey(boothId)){
			tempRouteList = routeMap.get(boothId);
			if(!tempRouteList.contains(routeId)){
				tempRouteList.add(routeId);
				routeMap.putAt(boothId, tempRouteList);
			}
		}else{
			routesList.add(routeId);
			routeMap.putAt(boothId, routesList);
		}
		if(tripMap.containsKey(boothId)){
			tempTripList = tripMap.get(boothId);
			if(!tempTripList.contains(tripId)){
				tempTripList.add(tripId);
				tripMap.putAt(boothId, tempTripList);
			}
		}else{
			tripsList.add(tripId);
			tripMap.putAt(boothId, tripsList);
		}
		tempQuantity = (quotaSubProd.quantity).setScale(1,rounding);
		quantity = tempQuantity;
		
		// populate export indent
		tempBoothRouteMap =[:];
		tempboothRouteWiseProdMap = [:];
		if(UtilValidate.isNotEmpty(BoothRouteWiseMap.get(boothId))){
			tempBoothRouteMap =BoothRouteWiseMap.get(boothId);
			if(UtilValidate.isNotEmpty(tempBoothRouteMap.get(routeId))){
				tempboothRouteWiseProdMap = tempBoothRouteMap.get(routeId);
			}
			
		}
		
		if(UtilValidate.isEmpty(tempboothRouteWiseProdMap[quotaSubProd.productId])){
			tempboothRouteWiseProdMap[quotaSubProd.productId] =0;
		}
		
		tempboothRouteWiseProdMap[quotaSubProd.productId] += quantity;
		tempBoothRouteMap[routeId] = tempboothRouteWiseProdMap;
		BoothRouteWiseMap[boothId]=tempBoothRouteMap;
		
		totalsMap[quotaSubProd.productId] += quantity;
		boothTotalsMap = boothsResultMap[boothId];
		if (boothTotalsMap == null) {
			boothTotalsMap = [:];
			boothTotalsMap.putAll(prodQuantityInitMap);
			boothTotalsMap[quotaSubProd.productId] = quantity;
			boothTotalsMap["createdUser"] = quotaSubProd.lastModifiedByUserLogin;
			boothTotalsMap["entryDate"] = quotaSubProd.lastModifiedDate;
			boothTotalsMap["routeId"] = quotaSubProd.sequenceNum;
			boothTotalsMap["tripId"] = quotaSubProd.tripNum;
			boothsResultMap[boothId] = [:];
			boothsResultMap[boothId].putAll(boothTotalsMap);
			continue;
		}
		boothTotalsMap[quotaSubProd.productId] += quantity;
	}
	boothsResultMap["Total"] = [:];
	boothsResultMap["Total"].putAll(totalsMap);
	
	// for separate routes for same booth
	
	finalTotalsMap =[:];
	finalTotalsMap.putAll(prodQuantityInitMap);
	for(int j=0; j < routeList.size();j++){
		route = routeList.get(j);
		routeProductList = EntityUtil.filterByCondition(quotaSubProdList, EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS ,route));
		routeBooths = EntityUtil.getFieldListFromEntityList(routeProductList, "facilityId", true);
		for(int k=0; k < routeBooths.size();k++){
			routeBoothId = routeBooths.get(k);
			routeBoothProductList = EntityUtil.filterByCondition(routeProductList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,routeBoothId));
			routeBoothTotalsMap = [:];
			routeBoothTotalsMap.putAll(prodQuantityInitMap);
			tempBoothQty = [:];
			tempBoothQty.putAll(prodQuantityInitMap);
			for(int l=0; l < routeBoothProductList.size();l++){
				routesList = [];
				tripsList = [];
				quotaSubProd = routeBoothProductList.get(l);
				boothId = quotaSubProd.facilityId;
				routeId = quotaSubProd.sequenceNum;
				tripId = quotaSubProd.tripNum;
				tempQuantity = (quotaSubProd.quantity).setScale(1,rounding);
				quantity = tempQuantity;
				if(l==0){
					routeBoothTotalsMap["boothId"] = quotaSubProd.facilityId;
					routeBoothTotalsMap["createdUser"] = quotaSubProd.lastModifiedByUserLogin;
					routeBoothTotalsMap["entryDate"] = quotaSubProd.lastModifiedDate;
					routeBoothTotalsMap["routeId"] = quotaSubProd.sequenceNum;
					routeBoothTotalsMap["tripId"] = quotaSubProd.tripNum;
				}
				finalTotalsMap[quotaSubProd.productId] += quantity;
				tempBoothQty[quotaSubProd.productId] = quantity;
				routeBoothTotalsMap[quotaSubProd.productId] = quantity;
			}
			routeBoothTotalsMap["prodQty"] = tempBoothQty;
			boothFinalList.add(routeBoothTotalsMap);
		}
	}
	boothFinalTotalsMap = [:];
	boothFinalTotalsMap["prodQty"] = finalTotalsMap;
	boothFinalTotalsMap["createdUser"] = "";
	boothFinalTotalsMap["boothId"] = "Total";
	boothFinalTotalsMap["routeId"] = "";
	boothFinalTotalsMap["entryDate"] = "";
	boothFinalTotalsMap["tripId"] = "";
	boothFinalTotalsMap["id"] = "Total";
	boothFinalList.add(boothFinalTotalsMap);
}
context.boothsResultMap = boothsResultMap;
context.BoothRouteWiseMap = BoothRouteWiseMap;
context.indentCount = boothsResultMap.size() - 1;
context.newIndentCount = boothFinalList.size() - 1;

JSONArray dataJSONList= new JSONArray();
Iterator mapIter = boothsResultMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	boothId =entry.getKey();
	boothName = facilityMap.get(boothId);
	boothTotalsMap = boothsResultMap[boothId];
	entryDate = boothTotalsMap.get("entryDate");
	tripId = boothTotalsMap.get("tripId");
	rtList = routeMap.get(boothId);
	trpList = tripMap.get(boothId);
	routeStr = "";
	tripStr = "";
	index = 0;
	if(rtList){
		rtList.each{eachRoute ->
			routeName = facilityMap.get(eachRoute);
			index += 1;
			if(index == rtList.size()){
				routeStr += routeName;
			}else{
				routeStr = routeStr+routeName+",";
			}
		}
	}
	
	index = 0;
	if(trpList){
		trpList.each{eachTrip ->
			index += 1;
			if(index == trpList.size()){
				tripStr += eachTrip;
			}else{
				tripStr = tripStr+eachTrip+",";
			}
		}
	}
	
	createdUser = boothTotalsMap.get("createdUser");
	JSONObject newObj = new JSONObject(boothTotalsMap);
	newObj.put("id",boothId);
	newObj.put("boothId",boothId+" ["+boothName+"]");
	newObj.put("routeId",routeStr);
	newObj.put("tripId",tripStr);
	entryDate = UtilDateTime.toDateString(entryDate, "dd/MM/yyyy");
	newObj.put("entryDate",entryDate);
	newObj.put("createdUser",createdUser);
	dataJSONList.add(newObj);
}
context.dataJSON = dataJSONList.toString();

// for separate routes for same booth

JSONArray newDataJSONList= new JSONArray();
boothFinalList.eachWithIndex {eachItem, idx ->
	createdUser = eachItem.get("createdUser");
	prodQty = eachItem.get("prodQty");
	JSONObject newObj = new JSONObject(prodQty);
	newObj.put("id",idx+1);
	newObj.put("routeId",eachItem.routeId);
	newObj.put("tripId",eachItem.tripId);
	newObj.put("createdUser",createdUser);
	newObj.put("boothId",eachItem.boothId);
	if(eachItem.routeId != ""){
		boothName = facilityMap.get(eachItem.boothId);
		newObj.put("boothId",eachItem.boothId+" ["+boothName+"]");
		entryDate = UtilDateTime.toDateString(eachItem.entryDate, "dd/MM/yyyy");
		newObj.put("entryDate",entryDate);
	}
	newDataJSONList.add(newObj);
}
context.newDataJSON = newDataJSONList.toString();
result.boothsResultMap = boothsResultMap;
result.productList = context.productList;
result.BoothRouteWiseMap = context.BoothRouteWiseMap;
return result;




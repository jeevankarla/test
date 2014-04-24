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

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

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
import net.sf.json.JSONArray;
import java.util.SortedMap;
import java.math.RoundingMode;
import javolution.util.FastList;
import in.vasista.vbiz.byproducts.ByProductReportServices;

rounding = RoundingMode.HALF_UP;

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
totalQuantity = 0;
totalRevenue = 0;
dctx = dispatcher.getDispatchContext();

Map boothsRegionMap = ByProductNetworkServices.getAllBoothsRegionsMap(dctx ,context);

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		context.froDate = parameters.fromDate;
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	else {
		froDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		context.froDate = froDate
		fromDate = froDate;
	}
	if (parameters.thruDate) {
		context.toDate = parameters.thruDate;
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
	else {
		context.toDate = UtilDateTime.nowDate();
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

subscriptionTypeId = parameters.subscriptionTypeId;

shipmentIds = [];
if(subscriptionTypeId && subscriptionTypeId != "All"){
	condList = [];
	condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	
	if(subscriptionTypeId != "ADHOC"){
		condList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, subscriptionTypeId+"_SHIPMENT"));
	}
	else{
		condList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "RM_DIRECT_SHIPMENT"));
	}
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	shipments = delegator.findList("Shipment", cond, ["shipmentId"] as Set, null, null, false);
	shipmentIds = EntityUtil.getFieldListFromEntityList(shipments, "shipmentId", true);
}



filterProductSale = [];
prodCategory = parameters.productCategoryId;
if(parameters.productCategoryId != 'allProducts' && parameters.productCategoryId){
	catProd = delegator.findList("Product", EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, parameters.productCategoryId), ["productId"] as Set, null, null, false);
	filterProductSale = EntityUtil.getFieldListFromEntityList(catProd, "productId", true);
}
dailySalesRevenueTrend = context.dailySalesRevenueTrend;
if(dailySalesRevenueTrend){
	try {
		if (parameters.fromDate) {
			context.froDate = parameters.fromDate;
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
		}
		else {
			froDate = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			context.froDate = froDate;
			fromDate = froDate;
		}
		if (parameters.thruDate) {
			context.toDate = parameters.thruDate;
			thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
		}
		else {
			context.toDate = UtilDateTime.nowDate();
			thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
}

dctx = dispatcher.getDispatchContext();
conditionList = [];
conditionList.clear();
shipments = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
addShipments = ByProductNetworkServices.getShipmentIdsByType(delegator, fromDate, thruDate, "RM_DIRECT_SHIPMENT");
if(addShipments){
	shipments.addAll(addShipments);
}
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipments));
List boothsList = FastList.newInstance();
if(UtilValidate.isNotEmpty(parameters.facilityId)){
	facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", parameters.facilityId), false);
	if(!facility && facility.facilityTypeId == "ROUTE" && facility.facilityTypeId == "BOOTH"){
		context.errorMessage = "'"+parameters.facilityId+"' is not a Route or Booth.";
		return;
	}
	
	if(facility.facilityTypeId == "ROUTE"){
		boothsList = (ByProductNetworkServices.getRouteBooths(delegator , facility.facilityId));
		
	}else{
		boothsList.add(facility.facilityId);
	}
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, boothsList));
	context.facilityId = parameters.facilityId;
}
/*if(UtilValidate.isNotEmpty(parameters.productCategoryId)){
	Map result = ByProductReportServices.getCategoryProducts(dctx, UtilMisc.toMap("productCategoryId", parameters.productCategoryId));
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, result.productIdsList));
	context.productCategoryId = parameters.productCategoryId;
}*/
if(UtilValidate.isNotEmpty(parameters.productId)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
	context.productId = parameters.productId;
}
if(UtilValidate.isNotEmpty(filterProductSale)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, filterProductSale));
}
if(subscriptionTypeId && subscriptionTypeId != "All"){
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
}
conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_IN, UtilMisc.toList("BYPROD_GIFT","REPLACEMENT_BYPROD")));
context.putAt("salesDate", fromDate);
productList = ByProductNetworkServices.getByProductProducts(dctx, context);
//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(productList, "productId", true)));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderItemList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, null, null, false);
dctx = dispatcher.getDispatchContext();
tempBoothList = EntityUtil.getFieldListFromEntityList(orderItemList, "originFacilityId", true);
distinctFacility = [];
if(tempBoothList){
	tempBoothList.each{eachBooth ->
		eachBooth = eachBooth.toUpperCase();
		if(!distinctFacility.contains(eachBooth)){
			distinctFacility.add(eachBooth);
		}
	}
}
facilityPriceMap = [:];
classificationMap = [:];
/*if(distinctFacility){
	distinctFacility.each{eachFacility ->
		classifyGroupId = "";
		facilityParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", eachFacility), false);
		if(facilityParty){
			partyId = facilityParty.getString("ownerPartyId");
			partyClassificationGroup = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false);
			//partyClassificationGroup = EntityUtil.filterByDate(partyClassificationGroup, fromDate);
			if(partyClassificationGroup){
				classifyGroupId = EntityUtil.getFirst(partyClassificationGroup).get("partyClassificationGroupId");
			}
		}
		if(classificationMap.get(classifyGroupId)){
			tempMap = [:];
			tempMap = classificationMap.get(classifyGroupId);
			facilityPriceMap.put(eachFacility, tempMap);
		}else{
			productsPrice = ByProductReportServices.getByProductPricesForFacility(dctx, UtilMisc.toMap( "userLogin",userLogin,"facilityId", eachFacility, "priceDate", fromDate)).get("productsPrice");
			facilityPriceMap.put(eachFacility, productsPrice);
			classificationMap.put(classifyGroupId, productsPrice);
		}
		
	}
}*/

SortedMap DataMap = new TreeMap();
productMap = [:];
productRevenueMap = [:];
productCatRevenueMap = [:];
productCatQuantityMap = [:];
regionWiseSalesMap = [:];
productQuantIncluded = [:];
productBrandNameMap=[:];
productQuantInc = delegator.findList("Product", null, null, null, null, false);
if(productQuantInc){
	productQuantInc.each{eachProd ->
		productQuantIncluded.putAt(eachProd.productId, eachProd.quantityIncluded);
		productBrandNameMap.putAt(eachProd.productId, eachProd.brandName);
	}
}
if(dailySalesRevenueTrend){	
	if(orderItemList){
		orderItemList.each{eachItem ->
			estimatedDeliveryDate = eachItem.getAt("estimatedDeliveryDate");
			booth = (eachItem.getAt("originFacilityId")).toUpperCase();
			
			// for now lets take unit price without vat
			unitPrice = eachItem.getAt("unitListPrice");
			quantity = eachItem.getAt("quantity");
			/*unitPrice = pricesMap.getAt(productId);*/
			totalAmount = quantity * unitPrice;
			estDayStart = UtilDateTime.getDayStart(estimatedDeliveryDate);
			if(DataMap.containsKey(estDayStart)){
				totAmount = DataMap.get(estDayStart);
				resultAmount = totAmount+totalAmount;
				DataMap.putAt(estDayStart, resultAmount);
			}
			else{
				DataMap.putAt(estDayStart, totalAmount);
			}
		}
	}
	JSONArray listRevJSON= new JSONArray();
	for(Map.Entry entry : DataMap.entrySet()){
		estDayStart = entry.getKey();
		totalAmount = entry.getValue();
		JSONArray dayRevList= new JSONArray();		
		dayRevList.add(estDayStart.getTime());
		dayRevList.add(totalAmount.divide(100000));
		listRevJSON.add(dayRevList);
	}
	context.listRevJSON=listRevJSON;
	
}else{
	
	productCatMap = ByProductNetworkServices.getProductCategoryMap(dctx, UtilMisc.toMap("productCategoryId","CONTINUES_INDENT","salesDate",thruDate ));
	productCatMap.putAll(ByProductNetworkServices.getProductCategoryMap(dctx, UtilMisc.toMap("productCategoryId","DAILY_INDENT" ,"salesDate",thruDate)));
	if(orderItemList){
		orderItemList.each{ eachItem ->	
			boothRegionMap =[:];
			if(UtilValidate.isNotEmpty(boothsRegionMap)){
				boothRegionMap = boothsRegionMap.get((eachItem.getAt("originFacilityId")));
			}
			
			regionId = null;
			if(boothRegionMap){
				regionId = boothRegionMap.getAt("regionId");
			}
			
			productId = eachItem.getAt("productId").toUpperCase();
			if(eachItem.getAt("categoryTypeEnum") != null){
				categoryTypeEnum = eachItem.getAt("categoryTypeEnum");
				booth = (eachItem.getAt("originFacilityId")).toUpperCase();
				// for now lets take unit price without vat
				unitPrice = eachItem.getAt("unitListPrice");
				quantity = eachItem.getAt("quantity");
				totalAmount = quantity * unitPrice;
				tempQuant = productQuantIncluded.get(productId).multiply(quantity)
				/*itemDescription = eachItem.getAt("itemDescription");*/
				itemDescription = productId;
				productCategoryId = productCatMap.get(productId).getAt("primaryProductCategoryId");
				if(DataMap.containsKey(categoryTypeEnum)){
					totAmount = DataMap.get(categoryTypeEnum);
					resultAmount = totAmount+totalAmount;
					DataMap.putAt(categoryTypeEnum, resultAmount);
				}
				else{
					DataMap.putAt(categoryTypeEnum, totalAmount);
				}
				if(productMap.containsKey(itemDescription)){
					
					totQuantity = productMap.get(itemDescription);
					resultQuantity = totQuantity+tempQuant;
					productMap.putAt(itemDescription, resultQuantity);
					productRevenueMap[itemDescription] += totalAmount;
				}
				else{
					productMap.putAt(itemDescription, tempQuant);
					productRevenueMap[itemDescription] = totalAmount;
				}
				if(productCatRevenueMap.containsKey(productCategoryId)){
					totQuantity = productCatQuantityMap.get(productCategoryId);
					resultQuantity = totQuantity+tempQuant;
					productCatQuantityMap.putAt(productCategoryId, resultQuantity);
					productCatRevenueMap[productCategoryId] += totalAmount;
				}
				else{
					productCatQuantityMap.putAt(productCategoryId, tempQuant);
					productCatRevenueMap.putAt(productCategoryId, totalAmount);					
				}
				
				
				//populate regionwise map
				if(regionId){
					if(regionWiseSalesMap.containsKey(regionId)){
						regionWiseSalesMap[regionId] += totalAmount;
					}else{
						regionWiseSalesMap.putAt(regionId, totalAmount);
					}
				}				
				totalQuantity += tempQuant;
				totalRevenue +=totalAmount;
			}
			
		}
	}
	categoryTypeEnumList = delegator.findByAnd("Enumeration",["enumTypeId" :"BOOTH_CAT_TYPE"]);
	categoryTypeEnumMap=[:];
	for(GenericValue categoryTypeEnum:categoryTypeEnumList){
		categoryTypeEnumMap[categoryTypeEnum.enumId] = categoryTypeEnum.description;
	}

	// Region list json
	regionReportList = [];
	for(Map.Entry entry : regionWiseSalesMap){
		tempMap = [:];
		regionId = entry.getKey();
		revenue = entry.getValue();
		
		tempMap.putAt("regionId", regionId);
		tempMap.putAt("name", regionId);
		tempMap.putAt("revenue", revenue);
		regionReportList.add(tempMap);
	}
	regionReportList=UtilMisc.sortMaps(regionReportList, UtilMisc.toList("-revenue"));
	context.regionReportList = regionReportList;
	
	
	channelReportList = [];
	for(Map.Entry entry : DataMap.entrySet()){
		tempMap = [:];
		categoryTypeEnum = entry.getKey();
		revenue = entry.getValue();
		if(categoryTypeEnumMap[categoryTypeEnum]){
			categoryTypeEnum = categoryTypeEnumMap[categoryTypeEnum];
		}
		tempMap.putAt("name", categoryTypeEnum);
		tempMap.putAt("revenue", revenue);
		channelReportList.add(tempMap);
	}
	channelReportList=UtilMisc.sortMaps(channelReportList, UtilMisc.toList("-revenue"));
	context.channelReportList = channelReportList;
	context.channelSize = channelReportList.size();
	
	productReportList = [];
	for(Map.Entry entry : productMap.entrySet()){
		tempMap = [:];
		itemDescription = entry.getKey();
		quantity = entry.getValue();
		tempMap.putAt("name",productBrandNameMap[itemDescription]);
		tempMap.putAt("quantity", quantity);
		tempMap.putAt("revenue", productRevenueMap[itemDescription]);
		productReportList.add(tempMap);
	}
	
	productCatReportList = [];
	for(Map.Entry entry : productCatRevenueMap.entrySet()){
		tempMap = [:];
		itemDescription = entry.getKey();
		tempMap.putAt("name",itemDescription);
		tempMap.putAt("quantity", productCatQuantityMap[itemDescription]);
		tempMap.putAt("revenue", productCatRevenueMap[itemDescription]);
		productCatReportList.add(tempMap);
	}
	productReportList=UtilMisc.sortMaps(productReportList, UtilMisc.toList("-revenue"));
	productCatReportList=UtilMisc.sortMaps(productCatReportList, UtilMisc.toList("-revenue"));
	context.productReportList = productReportList;
	context.prodSize = productReportList.size();
	
	context.productCatReportList = productCatReportList;
	Debug.logInfo("productCatReportList=" + productCatReportList, "");	
	
}	
totalRevenue = (new BigDecimal(totalRevenue)).setScale(0, rounding);
context.totalQuantity = totalQuantity;
context.totalRevenue = totalRevenue;

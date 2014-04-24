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
import org.ofbiz.network.NetworkServices;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import java.util.SortedMap;
import in.vasista.vbiz.byproducts.ByProductReportServices;


fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
totalQuantity = 0;
totalRevenue = 0;
dctx = dispatcher.getDispatchContext();

Map boothsRegionMap = NetworkServices.getAllBoothsRegionsMap(dctx ,context);

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
dailySalesRevenueTrend = context.dailySalesRevenueTrend;
if(dailySalesRevenueTrend){
	try {
		if (parameters.fromDate) {
			context.froDate = parameters.fromDate;
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
		}
		else {
			froDate = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
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
}

dctx = dispatcher.getDispatchContext();
conditionList = [];
conditionList.clear();
shipments = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipments));
conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, null),
	EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.NOT_IN, UtilMisc.toList("REPLACEMENT_BYPROD", "BYPROD_GIFT"))],EntityOperator.OR));
if(UtilValidate.isNotEmpty(parameters.facilityId)){
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, parameters.facilityId));
	context.facilityId = parameters.facilityId;
}
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.NOT_EQUAL, "AMBATTURPFTRY"));
if(UtilValidate.isNotEmpty(parameters.productId)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
	context.productId = parameters.productId;
}
conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
context.putAt("salesDate", fromDate);
productList = ByProductNetworkServices.getByProductProducts(dctx, context);
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(productList, "productId", true)));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
shipmentReceipts = delegator.findList("ShipmentReceiptAndItem", condition, null, null, null, false);
dctx = dispatcher.getDispatchContext();
tempBoothList = EntityUtil.getFieldListFromEntityList(shipmentReceipts, "facilityId", true);
distinctFacility = [];
if(tempBoothList){
	tempBoothList.each{eachBooth ->
		eachBooth = eachBooth.toUpperCase();
		if(!distinctFacility.contains(eachBooth)){
			distinctFacility.add(eachBooth);
		}
	}
}
/*facilityPriceMap = [:];
classificationMap = [:];*/
productsPrice = ByProductReportServices.getByProductPricesForPartyClassification(dispatcher.getDispatchContext(), UtilMisc.toMap("userLogin", userLogin, "partyClassificationId", "PM_RC_P")).get("productsPrice");
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
			productsPrice = ByProductReportServices.getByProductPricesForFacility(dctx, UtilMisc.toMap("facilityId", eachFacility, "priceDate", fromDate)).get("productsPrice");
			facilityPriceMap.put(eachFacility, productsPrice);
			classificationMap.put(classifyGroupId, productsPrice);
		}
		
	}
}*/
productsDesc = [:];
products = delegator.findList("Product", null, ["productId","description"]as Set, null, null, false);
if(products){
	products.each{eachProduct ->
		productsDesc.putAt(eachProduct.productId, eachProduct.description);
	}
}
SortedMap DataMap = new TreeMap();
productMap = [:];
productRevenueMap = [:];
productCatRevenueMap = [:];
regionWiseSalesMap = [:];

productCatMap = ByProductNetworkServices.getProductCategoryMap(dctx, UtilMisc.toMap("productCategoryId","BYPROD_CAT" ,"salesDate",thruDate));
if(shipmentReceipts){
	shipmentReceipts.each{eachItem ->
		boothRegionMap = boothsRegionMap.get((eachItem.getAt("facilityId")));
		regionId = null;
		if(boothRegionMap){
			regionId = boothRegionMap.getAt("regionId");
		}
		categoryTypeEnum = "PARLOUR";
		productId = eachItem.getAt("productId").toUpperCase();
		pricesMap = [:];
		/*booth = (eachItem.getAt("facilityId")).toUpperCase();
		*/
		/*pricesMap = facilityPriceMap.get(booth);*/
		
		prodPriceMap = productsPrice.get(productId);
		productId = eachItem.getAt("productId");
		unitPrice = prodPriceMap.getAt("totalAmount");
			
		quantity = eachItem.getAt("quantityAccepted");
		totalAmount = quantity * unitPrice;
		itemDescription = productsDesc.get(productId);
		productCategoryId = productCatMap.get(productId).getAt("productCategoryId");
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
			resultQuantity = totQuantity+quantity;
			productMap.putAt(itemDescription, resultQuantity);
			productRevenueMap[itemDescription] += totalAmount;
		}
		else{
			productMap.putAt(itemDescription, quantity);
			productRevenueMap[itemDescription] = totalAmount;
		}
		if(productCatRevenueMap.containsKey(productCategoryId)){
			productCatRevenueMap[productCategoryId] += totalAmount;
		}
		else{
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
		totalQuantity += quantity;
		totalRevenue +=totalAmount;
	}
}
categoryTypeEnumList = delegator.findByAnd("Enumeration",["enumTypeId" :"BYPROD_FA_CAT"]);
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
	tempMap.putAt("name", itemDescription);
	tempMap.putAt("quantity", quantity);
	tempMap.putAt("revenue", productRevenueMap[itemDescription]);
	productReportList.add(tempMap);
}
	
productCatReportList = [];
for(Map.Entry entry : productCatRevenueMap.entrySet()){
	tempMap = [:];
	itemDescription = entry.getKey();
	tempMap.putAt("name", itemDescription);
	tempMap.putAt("revenue", productCatRevenueMap[itemDescription]);
	productCatReportList.add(tempMap);
}
productReportList=UtilMisc.sortMaps(productReportList, UtilMisc.toList("-revenue"));
productCatReportList=UtilMisc.sortMaps(productCatReportList, UtilMisc.toList("-revenue"));
context.productReportList = productReportList;
context.prodSize = productReportList.size();

context.productCatReportList = productCatReportList;
Debug.logInfo("productCatReportList=" + productCatReportList, "");
context.totalQuantity = totalQuantity;
context.totalRevenue = totalRevenue;

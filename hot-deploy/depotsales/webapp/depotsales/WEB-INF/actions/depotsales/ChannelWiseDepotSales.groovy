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
returnCondition = [];
conditionList.clear();

roleTypeAndPartyList = delegator.findByAnd("RoleTypeAndParty",["parentTypeId" :"CUSTOMER_TRADE_TYPE"]);

//conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipments));
//returnCondition.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipments));
List boothsList = FastList.newInstance();
routeId = "";
roleTypeId=parameters.roleTypeId;
Debug.log("===roleTypeId==============>"+roleTypeId);
if(UtilValidate.isNotEmpty(roleTypeId)){
	inputMap = [:];
	inputMap.put("userLogin", userLogin);
	inputMap.put("roleTypeId", roleTypeId);
	if(UtilValidate.isNotEmpty(parameters.partyStatusId)){
			inputMap.put("statusId", parameters.partyStatusId);
	}
	Map unionPartyDetailsMap = ByProductNetworkServices.getPartyByRoleType(dctx,inputMap);
	partyIds = unionPartyDetailsMap.get("partyIds");
	Debug.log("===partyIds=====>"+partyIds+"==for=Role======>"+roleTypeId);
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
}
/*if(UtilValidate.isNotEmpty(parameters.productCategoryId)){
	Map result = ByProductReportServices.getCategoryProducts(dctx, UtilMisc.toMap("productCategoryId", parameters.productCategoryId));
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, result.productIdsList));
	context.productCategoryId = parameters.productCategoryId;
}*/


if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate) ){
	conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
}


if(UtilValidate.isNotEmpty(parameters.productId)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
	returnCondition.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , parameters.productId));
	context.productId = parameters.productId;
}
if(UtilValidate.isNotEmpty(filterProductSale)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, filterProductSale));
	returnCondition.add(EntityCondition.makeCondition("productId", EntityOperator.IN, filterProductSale));
}
if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
	returnCondition.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
}
/*if(subscriptionTypeId && subscriptionTypeId != "All"){
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	returnCondition.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
}*/
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
returnCondition.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS, "RETURN_ACCEPTED"));
/*conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_IN, UtilMisc.toList("BYPROD_GIFT","REPLACEMENT_BYPROD")));*/
context.putAt("salesDate", fromDate);
productList = ByProductNetworkServices.getByProductProducts(dctx, context);
//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(productList, "productId", true)));


conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderItemList = delegator.findList("OrderHeaderItemAndRoles", condition, null, null, null, false);

returnCondExpr = EntityCondition.makeCondition(returnCondition,EntityOperator.AND);
returnItemsList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", returnCondExpr, null, null, null, false);

//Debug.log("===orderItemList=="+orderItemList+"===returnItemsList="+returnItemsList);
returnItemList = [];
returnItemsList.each{ eachItem ->
	returnPrice = 0;
	if(eachItem.returnPrice){
		returnPrice = eachItem.returnPrice; 
	}
	GenericValue ordReturnValue  = delegator.makeValue("OrderHeaderItemProductShipmentAndFacility");
	ordReturnValue.shipmentId = eachItem.shipmentId;
	ordReturnValue.originFacilityId = eachItem.originFacilityId;
	ordReturnValue.productId = eachItem.productId;
	ordReturnValue.quantity = -(eachItem.returnQuantity);
	ordReturnValue.estimatedShipDate = eachItem.estimatedShipDate;
	ordReturnValue.shipmentTypeId = eachItem.shipmentTypeId;
	ordReturnValue.routeId = eachItem.routeId;
	ordReturnValue.unitPrice = eachItem.returnPrice;
	ordReturnValue.unitListPrice = returnPrice;
	ordReturnValue.categoryTypeEnum = eachItem.categoryTypeEnum;
	ordReturnValue.ownerPartyId = eachItem.ownerPartyId;
	returnItemList.add(ordReturnValue);
}
orderItemList.addAll(returnItemList);


dctx = dispatcher.getDispatchContext();
//tempBoothList = EntityUtil.getFieldListFromEntityList(orderItemList, "originFacilityId", true);

tempPartyIdsList = EntityUtil.getFieldListFromEntityList(orderItemList, "partyId", true);


facilityPriceMap = [:];
classificationMap = [:];


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
	
	productCatMap = ByProductNetworkServices.getProductCategoryMap(dctx, UtilMisc.toMap("productCategoryId","BYPROD","salesDate",fromDate ));
	//productCatMap.putAll(ByProductNetworkServices.getProductCategoryMap(dctx, UtilMisc.toMap("productCategoryId","DAILY_INDENT" ,"salesDate",fromDate)));
	/*Debug.log("orderItems ###########################"+orderItemList);*/
	subsidyOrderItems = EntityUtil.filterByCondition(orderItemList, EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "EMP_SUBSIDY"));
	subsidyOrderIds = EntityUtil.getFieldListFromEntityList(subsidyOrderItems, "orderId", true);

	List<GenericValue>  adjustmentsList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.IN, subsidyOrderIds), null , null, null, false);
						
	if(orderItemList){
		orderItemList.each{ eachItem ->	
			boothPartyId=eachItem.getAt("partyId");
			//Debug.log("==boothPartyId=="+boothPartyId);
			partyRoleType=null;
			booth=null;
			
			partyRoleList=EntityUtil.filterByCondition(roleTypeAndPartyList, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, boothPartyId));
			//Debug.log("==partyRoleList=="+partyRoleList);
			partyRoleDetails=EntityUtil.getFirst(partyRoleList);
			if(UtilValidate.isNotEmpty(partyRoleDetails)){
				partyRoleType=partyRoleDetails.roleTypeId;
			}
			//Debug.log("==partyRoleType=="+partyRoleType);
			boothRegionMap =[:];
			if(UtilValidate.isNotEmpty(boothsRegionMap) && UtilValidate.isNotEmpty(booth)){
				boothRegionMap = boothsRegionMap.get((booth));
			}
			//Debug.log("boothRegionMap=========AFTERRRR="+boothRegionMap);
			regionId = null;
			if(boothRegionMap){
				regionId = boothRegionMap.getAt("regionId");
			}
			
			productId = eachItem.getAt("productId").toUpperCase();
			//Debug.log("productId=========AFTERRRR="+productId);
			
			
			if(partyRoleType != null){
			
				// for now lets take unit price without vat
				//Debug.log("partyRoleType=========="+partyRoleType);
				unitPrice = eachItem.getAt("unitListPrice");
				quantity = eachItem.getAt("quantity");
				totalAmount = quantity * unitPrice;
				
				if(eachItem.get("orderId") && eachItem.get("productSubscriptionTypeId") && ((eachItem.get("productSubscriptionTypeId")).equals("EMP_SUBSIDY"))){
					List<GenericValue>  orderAdjustList = EntityUtil.filterByCondition(adjustmentsList, EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, eachItem.getString("orderId")));
					exclAmt = 0;
					for(GenericValue adjustemnt :orderAdjustList){
						if(adjustemnt.amount){
							exclAmt = exclAmt+adjustemnt.amount;
						}
					}
					totalAmount = totalAmount + exclAmt;
				}

				
				tempQuant = productQuantIncluded.get(productId).multiply(quantity)
				/*itemDescription = eachItem.getAt("itemDescription");*/
				itemDescription = productId;
				
				if(UtilValidate.isNotEmpty(productCatMap.get(productId))){
					productCategoryId = productCatMap.get(productId).getAt("primaryProductCategoryId");
				}else{
					productCategoryId ="Other Products";
					//Debug.log("productId=========="+productId);
				}
				
				if(DataMap.containsKey(partyRoleType)){
					totAmount = DataMap.get(partyRoleType);
					resultAmount = totAmount+totalAmount;
					DataMap.putAt(partyRoleType, resultAmount);
				}
				else{
					DataMap.putAt(partyRoleType, totalAmount);
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
	categoryTypeEnumList = delegator.findByAnd("RoleType",["parentTypeId" :"CUSTOMER_TRADE_TYPE"]);
	categoryTypeEnumMap=[:];
	for(GenericValue categoryTypeEnum:categoryTypeEnumList){
		categoryTypeEnumMap[categoryTypeEnum.roleTypeId] = categoryTypeEnum.description;
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
	Debug.log("productMap=========="+productMap);
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
	Debug.log("productCatReportList=" + productCatReportList, "");
	
}	
totalRevenue = (new BigDecimal(totalRevenue)).setScale(0, rounding);
context.totalQuantity = totalQuantity;
context.totalRevenue = totalRevenue;

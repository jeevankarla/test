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
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.util.EntityUtil;
	import java.util.*;
	import java.lang.*;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import java.sql.*;
	import java.util.Calendar;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import org.ofbiz.service.DispatchContext;
	import java.math.BigDecimal;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
	import org.ofbiz.network.NetworkServices;
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	
	reportTypeFlag = context.reportTypeFlag;
	String shipmentTypeId = null;
	
	routesHeader = new LinkedHashSet();
	routesList = ByProductServices.getByproductRoutes(delegator).get("routeIdsList");
	for(int j=0 ; j < routesList.size(); j++){
		routeCode =  routesList.get(j);
		if(Integer.parseInt(routeCode) > 14){
			routeCode = "OTHER";
		}
		routesHeader.add(routeCode);
	}
	
	if(parameters.shipmentTypeId){
			shipmentTypeId = parameters.shipmentTypeId;
	}
		
	Timestamp sqlTimestamp = null;
	Debug.log("report Type Falg ###################"+reportTypeFlag);
	Debug.log("estimatedShipDate Type Falg ###################"+parameters.estimatedShipDate);
	if(parameters.estimatedShipDate){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sqlTimestamp = new java.sql.Timestamp(formatter.parse(parameters.estimatedShipDate).getTime());
		} catch (ParseException e) {
		}
	}
		
	fromDate = UtilDateTime.getDayStart(sqlTimestamp, timeZone, locale);
	thruDate = UtilDateTime.getDayEnd(sqlTimestamp, timeZone, locale);
	context.put("estimatedDeliveryDate", fromDate);
	List shipments = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, fromDate, thruDate, shipmentTypeId, routesList);
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	context.put("effectiveDate", fromDate);
	
	routeMap = [:];
	activeProdList = [];
	productList = [];
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	
	if(reportTypeFlag == "CD-UNION"){
		productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("UNION_PRODUCTS");
	}
	else if(reportTypeFlag == "CD-DAIRY"){
		productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("DAIRY_PRODUCTS");
	}
	else{
		productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
	}
	
	grandTotmap = [:];
	
	conditionList=[];
	
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipments));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	fieldsToSelect = ["productId", "quantity", "productSubscriptionTypeId", "unitPrice","orderId","originFacilityId", "shipmentId"] as Set;
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, fieldsToSelect, ["productId"], null, false);
	
	conditionList.clear();
	
	conditionList.add(EntityCondition.makeCondition("shipmentId",  EntityOperator.IN, shipments));
	conditionList.add(EntityCondition.makeCondition("isCancelled",  EntityOperator.EQUALS, null));
	shipReceiptCondition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	shipReceiptList = delegator.findList("ShipmentReceiptAndItem", shipReceiptCondition1, ["facilityId", "productId", "quantityAccepted", "unitCost", "productSubscriptionTypeId", "shipmentId"] as Set, ["productId"], null, false);
	
	tempShipRecList = [];
	shipReceiptList.each { shipReceipt ->
		recMap = [:];
		recMap["originFacilityId"] = shipReceipt.get("facilityId");
		recMap["shipmentId"] = shipReceipt.get("shipmentId");
		recMap["productId"] = shipReceipt.get("productId");
		recMap["quantity"] = shipReceipt.get("quantityAccepted");
		recMap["unitPrice"] = shipReceipt.get("unitCost");
		recMap["productSubscriptionTypeId"] = shipReceipt.get("productSubscriptionTypeId");
		tempMap = [:];
		tempMap.putAll(recMap);
		tempShipRecList.addAll(tempMap);
	}
	
	boothOrderItemsList.addAll(tempShipRecList);
	
	/*conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO ,effectiveDate));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["facilityId", "sequenceNum", "productId", "quantity"] as Set;
	subDetailList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["productId"], null, false);*/
	
	for(i=0;i<boothOrderItemsList.size();i++){
		
		shipmentId = boothOrderItemsList.get(i).get("shipmentId");
		shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId" : shipmentId), false);
		routeId = shipment.get("routeId");
		
		if(Integer.parseInt(routeId) > 14){
			routeId = "OTHER";
		}
		
		quantity = boothOrderItemsList.get(i).get("quantity");
		productId = boothOrderItemsList.get(i).get("productId");
		if(!productList.contains(productId)){
			continue;
		}
		if(!activeProdList.contains(productId)){
			activeProdList.add(productId);
		}
		
		if(UtilValidate.isEmpty(routeMap[routeId])){
			productMap = [:];
			productMap[productId] = quantity;
			tempProdMap = [:];
			tempProdMap.putAll(productMap);
		}else{
			prodUpdateMap = routeMap[routeId];
			if(UtilValidate.isEmpty(prodUpdateMap[productId])){
				prodUpdateMap[productId] = quantity;
				tempProdMap = [:];
				tempProdMap.putAll(prodUpdateMap);
			}else{
				updateQty = prodUpdateMap[productId];
				updateQty = updateQty.add(quantity);
				
				prodUpdateMap[productId] = updateQty;
				tempProdMap = [:];
				tempProdMap.putAll(prodUpdateMap);
			}
		}
		
		if(UtilValidate.isEmpty(grandTotmap[productId])){
			grandTotmap[productId] = (BigDecimal)quantity;
		}else{
			updateGrdQty = (BigDecimal)grandTotmap[productId];
			updateGrdQty = (BigDecimal)updateGrdQty.add((BigDecimal)quantity);
			grandTotmap[productId] = (BigDecimal)updateGrdQty;
		}
		routeMap.put(routeId, tempProdMap);
	}
	
	context.put("routeMap", routeMap);
	context.put("productList", activeProdList);
	context.put("grandTotmap", grandTotmap);
	context.put("routesHeader", routesHeader);
	return "success";
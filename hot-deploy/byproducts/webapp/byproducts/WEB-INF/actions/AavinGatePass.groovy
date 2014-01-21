	import org.ofbiz.entity.util.EntityUtil;

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
	import  org.ofbiz.network.NetworkServices;
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	import org.ofbiz.product.price.PriceServices;
	
	rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	context.rounding = rounding;
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	conditionList=[];
	shipmentId = null;
	shipmentTypeId = null;
	routesList = [];
	routeMap = [:];
	
	if(parameters.shipmentTypeId){
		shipmentTypeId = parameters.shipmentTypeId;
	}
	
	Timestamp sqlTimestamp = null;
	
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
	
	
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	
	routeList = [];
	
	if(reportTypeFlag == "GATE_PASS_UNION"){
		productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("UNION_PRODUCTS");
	}
	else if(reportTypeFlag == "GATE_PASS_DAIRY"){
		productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("DAIRY_PRODUCTS");
	}
	else{
		productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
	}
	
	requestedFacilityId = null;
	if(parameters.facilityId){
		requestedFacilityId = parameters.facilityId;
	}
	
	if(requestedFacilityId == "All-Routes" || requestedFacilityId == null){
		routesList = ByProductServices.getByproductRoutes(delegator).get("routeIdsList");
	}
	else{
		routesList.add(requestedFacilityId);
	}
	List shipments = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, fromDate, thruDate, shipmentTypeId, routesList);
	
	shipmentRouteMap = [:];
	if(shipments){
		shipments.each{eachItem ->
			shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId" : eachItem), false);
			if(shipment){
				routeId = shipment.get("routeId");
				shipmentRouteMap.putAt(routeId,eachItem);
			}
		}
		context.shipmentRouteMap = shipmentRouteMap;
	}
	
	for(i=0; i< shipments.size(); i++){
		
		shipmentId = shipments.get(i);
		shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId" : shipmentId), false);
		routeId = shipment.get("routeId");
		routeMap = [:];
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
		conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO ,thruDate));
		conditionList.add(EntityCondition.makeCondition("quantity", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		fieldsToSelect = ["facilityId", "sequenceNum", "productId", "quantity"] as Set;
		subProdDetailList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["productId"], null, false);
		tempList = [];
		productMap =[:];
		if(subProdDetailList){
			subProdDetailList.each{eachItem ->
				productId = eachItem.productId;
				quantity = eachItem.quantity;
				if(UtilValidate.isEmpty(productMap[productId])){
					productMap.putAt(productId, quantity);
				}else{
					tempQty = productMap[productId];
					tempQty = tempQty.add(quantity);
					productMap[productId]= tempQty;	
				}
			}
		}
		routeMap.put(routeId, productMap);
		routeList.add(routeMap);
	}
	context.put("routeList", routeList);
	
	return "success";
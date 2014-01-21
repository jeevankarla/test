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
	shipmentId = null;
	
	Timestamp sqlTimestamp = null;
	
	dayBegin = null;
	dayEnd = null;
	if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
		customTimePeriod =delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
		fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));	
		thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
		dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);
	}
	
	
	List productList = [];
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	List unionProductList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("UNION_PRODUCTS");
	if(reportTypeFlag == "CD-15_UNION" || reportTypeFlag == "GATE_PASS_UNION"){
		productList = unionProductList;
	}
	else if(reportTypeFlag == "CD-15_DAIRY" || reportTypeFlag == "GATE_PASS_DAIRY"){
		productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("DAIRY_PRODUCTS");
	}
	else{
		productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
	}
	
	routesList = ByProductServices.getByproductRoutes(delegator).get("routeIdsList");
	
	routesHeader = new LinkedHashSet();
	for(int j=0 ; j < routesList.size(); j++){
		routeCode =  routesList.get(j);
		if(Integer.parseInt(routeCode) > 14){
			routeCode = "OTHER";
		}
		routesHeader.add(routeCode);
	}
	
	productGenericList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productList");
	
	prodQuantMap = [:];
	prodNameMap = [:];
	for(int i=0 ; i < productGenericList.size(); i++){
		prodQuantMap.put((productGenericList.get(i)).get("productId"), (productGenericList.get(i)).get("quantityIncluded"));
		prodNameMap.put((productGenericList.get(i)).get("productId"), (productGenericList.get(i)).get("productName"));
	}
	context.prodNameMap = prodNameMap;
	
	List shipments = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, dayBegin, dayEnd, "BYPRODUCTS", routesList);
	
	shipmentRouteMap = [:];
	if(shipments){
		shipments.each{eachShipment ->
			shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId" : eachShipment), false);
			if(shipment){
				routeId = shipment.get("routeId");
				shipmentRouteMap.putAt(eachShipment,routeId);
			}
		}
	}
	
	List conditionList= FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipments));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
	if(UtilValidate.isNotEmpty(productList)){
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
	}
	EntityCondition orderItemCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderItemCondition,  ["productId", "quantity", "categoryTypeEnum", "originFacilityId", "ownerPartyId", "quantityIncluded", "shipmentId"] as Set, ["productId"], null, false);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipments));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, null),EntityOperator.OR,
		EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.NOT_EQUAL, "REPLACEMENT_BYPROD")));
	conditionList.add(EntityCondition.makeCondition("quantityAccepted", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
	conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
	if(UtilValidate.isNotEmpty(productList)){
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
	}
	shipReceiptCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	shipmentReceiptList = delegator.findList("ShipmentReceiptAndItem", shipReceiptCondition, ["facilityId", "productId", "quantityAccepted", "unitCost", "shipmentId", "productSubscriptionTypeId"] as Set , ["productId"], null, false);
	
	tempShipRecList = [];
	if(UtilValidate.isNotEmpty(shipmentReceiptList)){
		shipmentReceiptList.each { shipReceipt ->
			recMap = [:];
			recMap["productId"] = shipReceipt.get("productId");
			recMap["originFacilityId"] = shipReceipt.get("facilityId");
			recMap["quantity"] = shipReceipt.get("quantityAccepted");
			recMap["unitPrice"] = shipReceipt.get("unitCost");
			recMap["shipmentId"] = shipReceipt.get("shipmentId");
			
			tempMap = [:];
			tempMap.putAll(recMap);
			tempShipRecList.addAll(tempMap);
		}
	}
	
	boothOrderItemsList.addAll(tempShipRecList);

	productMap = [:];
	productWiseTotalsMap = [:];
	routeWiseTotalsMap = [:];	
	
	for(i=0 ; i < boothOrderItemsList.size(); i++){
		
		orderItem = boothOrderItemsList.get(i);
		facilityId = orderItem.get("originFacilityId");
		shipmentId = orderItem.get("shipmentId");
		routeId = shipmentRouteMap.get(shipmentId);
		if(Integer.parseInt(routeId) > 14){
			routeId = "OTHER";
		}
		productId = orderItem.get("productId");
		quantity = orderItem.get("quantity");
		qtyInc = (prodQuantMap.get(productId)).multiply(quantity);
		
		/*==================POPULATE PRODUCT WISE AND ROUTE WISE TOTALS================*/
		
		if(UtilValidate.isEmpty(productMap.get(productId))){
			routeMap = [:];
			routeMap.put(routeId, quantity);
		}
		else{
			routeMap = [:];
			routeMap = productMap.get(productId);
			
			if(UtilValidate.isEmpty(routeMap.get(routeId))){
				routeMap.put(routeId, quantity);
			}
			else{
				BigDecimal updateQty = routeMap.get(routeId);
				updateQty = updateQty.add(quantity);
				
				routeMap.put(routeId, updateQty);
			}
		}
		tempProdMap = [:];
		tempProdMap.putAll(routeMap);
		
		productMap.put(productId, tempProdMap);
		
		
		/*==================POPULATE PRODUCT WISE TOTALS================*/
		
		if(UtilValidate.isEmpty(productWiseTotalsMap.get(productId))){
			prodSaleMap = [:];
			prodSaleMap.put("quantity", quantity);
			prodSaleMap.put("qtyInc", qtyInc);
		}
		else{
			prodSaleMap = [:];
			prodSaleMap = productWiseTotalsMap.get(productId);
			
			BigDecimal updateQty = prodSaleMap.get("quantity");
			updateQty = updateQty.add(quantity);
			
			BigDecimal updateQtyInc = prodSaleMap.get("qtyInc");
			updateQtyInc = updateQtyInc.add(qtyInc);
			
			prodSaleMap.put("quantity", updateQty);
			prodSaleMap.put("qtyInc", updateQtyInc);
		}
		tempProductWiseTotalsMap = [:];
		tempProductWiseTotalsMap.putAll(prodSaleMap);
		
		productWiseTotalsMap.put(productId, tempProductWiseTotalsMap);
		
	}
	context.put("productMap", productMap);
	context.put("productWiseTotalsMap", productWiseTotalsMap);
	context.put("routeWiseTotalsMap", routeWiseTotalsMap);
	context.put("routesHeader", routesHeader);
	return "success";

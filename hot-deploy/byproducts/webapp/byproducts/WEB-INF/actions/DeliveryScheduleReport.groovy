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
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	import org.ofbiz.product.price.PriceServices;
	
	rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	context.rounding = rounding;
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	conditionList=[];
	grandTotalMap =[:];
	shipmentId = null;
	shipmentTypeId = null;
	routesList = [];
	routeMap = [:];
	routeDetailList = [];
	grandTotalMap = [:];
	grandTotmap = [:];
	
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
	
	activeRouteList = [];
	List productList = [];
	List activeProdList = [];
	destinationFacMap = [:];
	cd15RouteMap = [:];
	
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
	
	routesHeader = new LinkedHashSet();
	for(int j=0 ; j < routesList.size(); j++){
		routeCode =  routesList.get(j);
		/*if(Integer.parseInt(routeCode) > 14){
			routeCode = "OTHER";
		}*/
		routesHeader.add(routeCode);
	}

	List shipments = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate, routesList);
	
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
		cd15RouteId = routeId;
		
		/*if(Integer.parseInt(cd15RouteId) > 14){
			cd15RouteId = "OTHER";
		}*/
		 
		cd15ProductMap = [:];
		boothDetailList = [];
		routeOrderDetailMap = [:];
		boothMap = [:];
		boothsList = [];
		existingBoothsList = [];
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
		conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
		
		condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		boothItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition1, ["originFacilityId"] as Set , ["productId"], null, false);
		existingBoothsList = EntityUtil.getFieldListFromEntityList(boothItemsList,"originFacilityId",true);
		
		conditionList.clear();
		
		conditionList.add(EntityCondition.makeCondition("shipmentId",  EntityOperator.EQUALS, shipmentId));
		conditionList.add(EntityCondition.makeCondition("isCancelled",  EntityOperator.EQUALS, null));
		shipReceiptCondition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		shipReceiptList = delegator.findList("ShipmentReceiptAndItem", shipReceiptCondition1, ["facilityId"] as Set , ["productId"], null, false);
		
		conditionList.clear();
		
		parloursList = EntityUtil.getFieldListFromEntityList(shipReceiptList,"facilityId",true);
		existingBoothsList.addAll(parloursList);
		
		for(a = 0; a < existingBoothsList.size(); a++){
			booth = existingBoothsList.get(a);
			boothUppercase = booth.toUpperCase();
			if(!boothsList.contains(boothUppercase)){
				boothsList.add(boothUppercase);
			}
		}
		
		for(j=0; j< boothsList.size(); j++){
			area = "";
			boothId = boothsList[j];
			boothOrderItemsList=[];
			tempShipRecList = [];
			conditionList=[];
			boothsDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId" : boothId), false);
			boothCategory = (String)boothsDetail.get("categoryTypeEnum");
			ownerPartyId = (String)boothsDetail.get("ownerPartyId");
			partyAddress = dispatcher.runSync("getPartyPostalAddress", [partyId: ownerPartyId, userLogin: userLogin]);
			if(partyAddress.address1 || partyAddress.city){
				area = partyAddress.address1;
			}
			
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, boothId));
			conditionList.add(EntityCondition.makeCondition("quantity", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
			conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
			
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			fieldsToSelect = ["productId", "quantity", "productSubscriptionTypeId", "unitPrice","orderId","routeId", "originFacilityId", "estimatedShipDate"] as Set;
			boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, fieldsToSelect , ["productId"], null, false);
			
			String  invoiceNumber=null;
			if(UtilValidate.isNotEmpty(boothOrderItemsList)){
				orderId = boothOrderItemsList.get(0).get("orderId");
				orderInvoiceList =delegator.findList("OrderItemBilling",EntityCondition.makeCondition("orderId",EntityOperator.EQUALS, orderId) , null , ["orderId"], null, false);
				if (UtilValidate.isNotEmpty(orderInvoiceList)){
					invoiceNumber=orderInvoiceList.get(0).getAt("invoiceId");
				}
			}
			
			conditionList.clear();
			
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, null),EntityOperator.OR,
				EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.NOT_EQUAL, "REPLACEMENT_BYPROD")));
			conditionList.add(EntityCondition.makeCondition("quantityAccepted", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
			conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
			shipReceiptCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			
			shipmentReceiptList = delegator.findList("ShipmentReceiptAndItem", shipReceiptCondition, ["facilityId", "productId", "quantityAccepted", "unitCost", "productSubscriptionTypeId"] as Set , ["productId"], null, false);
			
			shipmentReceiptList.each { shipReceipt ->
				recMap = [:];
				recMap["productId"] = shipReceipt.get("productId");
				recMap["quantity"] = shipReceipt.get("quantityAccepted");
				recMap["unitPrice"] = shipReceipt.get("unitCost");
				recMap["productSubscriptionTypeId"] = shipReceipt.get("productSubscriptionTypeId");
				tempMap = [:];
				tempMap.putAll(recMap);
				tempShipRecList.addAll(tempMap);
			}
			
			boothOrderItemsList.addAll(tempShipRecList);
			
			productMap = [:];
			prodDetailList = [];
			
			for(k=0;k<boothOrderItemsList.size();k++){
				
				productId = boothOrderItemsList.get(k).get("productId");
				
				productSubscriptionTypeId = boothOrderItemsList.get(k).get("productSubscriptionTypeId");
				if(productSubscriptionTypeId == "REPLACEMENT_BYPROD"){
					boothId = boothOrderItemsList.get(k).get("originFacilityId");
					routeId = boothOrderItemsList.get(k).get("routeId");
					estimatedShipDate = boothOrderItemsList.get(k).get("estimatedShipDate");
					exprList = [];
					exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
					exprList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
					exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "REPLACEMENT_BYPROD"));
					exprCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
					destinationFac = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", exprCond, ["destinationFacilityId"] as Set, null, null, false);
					destinationFac = EntityUtil.filterByDate(destinationFac, estimatedShipDate);
					if(destinationFac){
						destinationFacility = EntityUtil.getFirst(destinationFac);
						destinationFacMap.put(boothId, destinationFacility.destinationFacilityId);
					}
				}
				if(!productList.contains(productId)){
					continue;
				}
				if(!activeProdList.contains(productId)){
					activeProdList.add(productId);
				}
				
				product = delegator.findOne("Product", UtilMisc.toMap("productId" : productId), false);
				
				exd = 0;
				vatAmt = 0;
				vatPercentage = 0;
				edCess = 0;
				higherSecCess = 0;
				
				priceContext = [:];
				priceResult = [:];
				Map<String, Object> priceResult;
				priceContext.put("userLogin", userLogin);
				priceContext.put("productStoreId", productStoreId);
				priceContext.put("productId", productId);			
				priceContext.put("priceDate", fromDate);
				if(productSubscriptionTypeId == "REPLACEMENT_BYPROD"){
					if(unionProductList.contains(productId)){
						priceContext.put("productPriceTypeId", "PM_RC_U_PRICE");
					}
					else{
						priceContext.put("productPriceTypeId", "PM_RC_W_PRICE");
					}
				}
				priceContext.put("facilityId", boothId);
				priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);
				
				if (priceResult) {
					basicPrice = (BigDecimal)priceResult.get("basicPrice");
					taxList = priceResult.get("taxList");
					if(taxList){						
						for(m = 0; m < taxList.size(); m++ ){
							
							if(taxList.get(m).get("taxType") == "BED_SALE"){
								exd = taxList.get(m).get("amount");
							}
							if(taxList.get(m).get("taxType") == "BEDCESS_SALE"){
								edCess = taxList.get(m).get("amount");
							}
							if(taxList.get(m).get("taxType") == "BEDSECCESS_SALE"){
								higherSecCess = taxList.get(m).get("amount");
							}
							if(taxList.get(m).get("taxType") == "VAT_SALE"){
								vatAmt = taxList.get(m).get("amount");
								vatPercentage = taxList.get(m).get("percentage");
							}
						}
					}
				}
				unitPrice = basicPrice + exd + edCess + higherSecCess;
				BigDecimal boothQty = (BigDecimal) boothOrderItemsList.get(k).get("quantity");
				
				BigDecimal basicRate = unitPrice;
				basicRate = basicRate.setScale(2, rounding);
				BigDecimal vatRate = vatAmt;
				BigDecimal basicValue = basicRate.multiply(boothQty);
				BigDecimal vatValue = vatRate.multiply(boothQty);
				BigDecimal totalValue = basicValue.add(vatValue);
				
				quantityIncluded = product.get("quantityIncluded");
				IncQtyTotal = quantityIncluded.multiply(boothQty);
				detailMap = [:];
				detailMap["productId"] = productId;
				detailMap["productName"] = product.get("brandName");
				detailMap["quantity"] = boothQty;
				detailMap["subscriptionType"] = boothOrderItemsList.get(k).get("productSubscriptionTypeId");
				detailMap["VAT_Percentage"] = vatPercentage;
				detailMap["basicRate"] = basicRate;
				detailMap["vatRate"] = vatRate;
				detailMap["basicValue"] = basicValue;
				detailMap["vatValue"] = vatValue;
				detailMap["totalValue"] = totalValue;
				detailMap["incQty"] = IncQtyTotal;
				detailMap["exd"] = exd;
				detailMap["edCess"] = edCess;
				detailMap["higherSecCess"] = higherSecCess;
				tempDetailMap = [:];
				tempDetailMap.putAll(detailMap);
				
				tempProdDetailMap = [:];
				tempProdDetailMap.putAll(detailMap);
				
				prodDetailList.addAll(tempProdDetailMap);
				
				tempTotalsMap = [:];
				tempTotalsMap.putAll(detailMap);
				if(UtilValidate.isEmpty(routeOrderDetailMap[productId])){
					routeOrderDetailMap[productId] = tempDetailMap;
				}
				else{
					updateDetailMap = routeOrderDetailMap[productId];
					updateDetailMap["quantity"] += boothQty;
					updateDetailMap["incQty"] += IncQtyTotal;
					updateDetailMap["basicValue"] += basicValue;
					updateDetailMap["vatValue"] += vatValue;
					updateDetailMap["totalValue"] += totalValue;
					
					tempUpdateMap = [:];
					tempUpdateMap.putAll(updateDetailMap);
					routeOrderDetailMap.put(productId, tempUpdateMap);
				}
				if(UtilValidate.isEmpty(grandTotalMap[productId])){
					grandTotalMap[productId] = tempTotalsMap;
				}
				else{
					updateDetailMap = grandTotalMap[productId];
					updateDetailMap["quantity"] += boothQty;
					updateDetailMap["incQty"] += IncQtyTotal;
					updateDetailMap["basicValue"] += basicValue;
					updateDetailMap["vatValue"] += vatValue;
					updateDetailMap["totalValue"] += totalValue;
					
					tempUpdateMap = [:];
					tempUpdateMap.putAll(updateDetailMap);
					grandTotalMap.put(productId, tempUpdateMap);
				}
				
				/* populate cd15 Route Map */
				if(UtilValidate.isEmpty(cd15RouteMap[cd15RouteId])){
					cd15ProductMap = [:];
					cd15ProductMap[productId] = boothQty;
					tempProdMap = [:];
					tempProdMap.putAll(cd15ProductMap);
				}else{
					prodUpdateMap = cd15RouteMap[cd15RouteId];
					if(UtilValidate.isEmpty(prodUpdateMap[productId])){
						prodUpdateMap[productId] = boothQty;
						tempProdMap = [:];
						tempProdMap.putAll(prodUpdateMap);
					}else{
						updateQty = prodUpdateMap[productId];
						updateQty = updateQty.add(boothQty);
						
						prodUpdateMap[productId] = updateQty;
						tempProdMap = [:];
						tempProdMap.putAll(prodUpdateMap);
					}
				}
				if(UtilValidate.isEmpty(grandTotmap[productId])){
					grandTotmap[productId] = (BigDecimal)boothQty;
				}else{
					updateGrdQty = (BigDecimal)grandTotmap[productId];
					updateGrdQty = (BigDecimal)updateGrdQty.add((BigDecimal)boothQty);
					grandTotmap[productId] = (BigDecimal)updateGrdQty;
				}
				cd15RouteMap.put(cd15RouteId, tempProdMap);
			   /* ********* END ********* */
				
			}
			if(UtilValidate.isEmpty(prodDetailList)){
				continue;
			}
			
			tempProdList = [];
			tempProdList.addAll(prodDetailList);
			productMap.put("booth", boothId);
			
			productMap.put("invoiceNumber", invoiceNumber);
			productMap.put("boothCategory", boothCategory);
			productMap.put("area", area);
			productMap.put("ownerPartyId", ownerPartyId);
			productMap.put("productList", tempProdList);
			
			tempBoothList = [];
			tempBoothList.addAll(productMap);
			boothDetailList.addAll(tempBoothList);
		}
		
		if(UtilValidate.isEmpty(boothDetailList)){
			continue;
		}
		tempBoothDetailList = [];
		tempBoothDetailList.addAll(boothDetailList);
		
		tempTotalsMap = [:];
		tempTotalsMap.putAll(routeOrderDetailMap);
		
		tempTotalsList = [];
		
		Iterator treeMapIter = tempTotalsMap.entrySet().iterator();
		while (treeMapIter.hasNext()) {
			Map.Entry entry = treeMapIter.next();
			tempTotalsList.addAll(entry.getValue());
		}
		
		tempTotalsMap.clear();
		tempTotalsList = UtilMisc.sortMaps(tempTotalsList, UtilMisc.toList("productId"));
		tempTotalsMap.put("productList", tempTotalsList);
	
		activeRouteList.addAll(routeId);
		boothMap.put("route", routeId);
		boothMap.put("boothList", tempBoothDetailList);
		boothMap.put("routeList", tempTotalsMap);
		tempRouteList = [];
		tempRouteList.addAll(boothMap);
		routeDetailList.addAll(tempRouteList);
		
	}
	tempRouteDetailList = [];
	tempRouteDetailList.addAll(routeDetailList);
	
	tempGrandTotalsMap = [:];
	tempGrandTotalsMap.putAll(grandTotalMap);
	
	tempGrandTotalsList = [];
	
	Iterator grandTotMapIter = tempGrandTotalsMap.entrySet().iterator();
	while (grandTotMapIter.hasNext()) {
		Map.Entry grandTotEntry = grandTotMapIter.next();
		tempGrandTotalsList.addAll(grandTotEntry.getValue());
	}
	
	tempGrandTotalsMap.clear();
	tempGrandTotalsMap.put("productList", tempGrandTotalsList)
	
	routeMap.put("routeList", tempRouteDetailList);
	routeMap.put("grandTotals", tempGrandTotalsMap);
	context.put("activeRouteList", activeRouteList);
	context.put("routeMap", routeMap);
	context.putAt("destinationFacMap", destinationFacMap);
	Collections.sort(activeProdList);
	context.put("productList", activeProdList);
	context.put("grandTotmap", grandTotmap);
	context.put("cd15RouteMap", cd15RouteMap);
	context.put("routesHeader", routesHeader);
	
	
	return "success";
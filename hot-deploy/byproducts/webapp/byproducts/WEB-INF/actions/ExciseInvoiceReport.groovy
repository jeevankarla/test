	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.util.EntityUtil;
	
	import java.text.SimpleDateFormat;
	import java.util.*;
	import java.lang.*;
	import java.math.BigDecimal;
	
	
	import java.text.ParseException;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import org.ofbiz.base.util.UtilDateTime;
	import org.ofbiz.network.NetworkServices;
	import org.ofbiz.network.LmsServices;
	import org.ofbiz.entity.util.EntityFindOptions;
	import in.vasista.vbiz.byproducts.ByProductServices;
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	
	rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	context.rounding = rounding;
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	conditionList=[];
	grandTotalMap =[:];
	shipmentId = null;
	shipmentIds = [];
	shipment = null;
	if(parameters.shipmentId && parameters.shipmentId != "allRoutes"){
		shipmentId = parameters.shipmentId;
		shipmentIds.add(shipmentId);
		shipment =delegator.findOne("Shipment", [shipmentId: shipmentId], false);
	}
	
	Timestamp sqlTimestamp = null;
	
	if(parameters.estimatedShipDate){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sqlTimestamp = new java.sql.Timestamp(formatter.parse(parameters.estimatedShipDate).getTime());
			
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + e, "");
			context.errorMessage = "Cannot parse date string: " + e;
			return;
		}
	}
	
	fromDate = UtilDateTime.getDayStart(sqlTimestamp, timeZone, locale);
	thruDate = UtilDateTime.getDayEnd(sqlTimestamp, timeZone, locale);
	context.put("estimatedDeliveryDate", fromDate);
	requestedFacilityId = null;
	if(parameters.facilityId){
		requestedFacilityId = parameters.facilityId;
	}
	
	activeRouteList = [];
	
	routesList = [];
	if(requestedFacilityId == "All-Routes" || requestedFacilityId == null){
		routesList = ByProductServices.getByproductRoutes(delegator).get("routeIdsList");
	}
	else{
		routesList.add(requestedFacilityId);
	}
	
	List shipments = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate, routesList);
	Boolean isOldShipment = false;
	if(!shipments){
		conditionList=[];
		
		conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
		
		shipCond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		oldShipments = delegator.findList("Shipment", shipCond, ["shipmentId"] as Set , null, null, false);
		if(oldShipments){
			shipments = EntityUtil.getFieldListFromEntityList(oldShipments, "shipmentId", false);
			isOldShipment = true;
		}
	}
	
	context.rounding = rounding;
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	List unionProductList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("UNION_PRODUCTS");
	
	boothOrderItemsList=[];
	tempShipRecList = [];
	conditionList = [];
	
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipments));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId", "quantity", "productSubscriptionTypeId", "unitPrice","orderId", "originFacilityId", "shipmentId"] as Set;
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, fieldsToSelect , ["productId"], null, false);
	
	conditionList.clear();
	
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipments));
	conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
	shipReceiptCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	shipmentReceiptList = delegator.findList("ShipmentReceiptAndItem", shipReceiptCondition, ["facilityId", "productId", "quantityAccepted", "unitCost", "shipmentId"] as Set , ["productId"], null, false);
	
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
	routeMap = [:];
	exciseDutyMap = [:];
	
	detailMap = [:];
	
	for(i=0 ; i < boothOrderItemsList.size(); i++){
		
		shipmentReceipt = boothOrderItemsList.get(i);
		facilityId = shipmentReceipt.get("originFacilityId");
		routeId = null;
		
		facility = delegator.findOne("Facility", [facilityId: facilityId], false);
		
		if(isOldShipment){
			routeId = facility.get("byProdRouteId");
		}
		else{
			shipmentRoute = delegator.findOne("Shipment", [shipmentId: shipmentReceipt.get("shipmentId")], false);
			routeId = shipmentRoute.get("routeId");
		}
		boothCategory = null;
		if(UtilValidate.isNotEmpty(facility.get("categoryTypeEnum"))){
			boothCategory = facility.get("categoryTypeEnum");
		}
		else{
			boothCategory = "Direct";
		}
		productId = shipmentReceipt.get("productId");
		if(unionProductList.contains(productId)){
			continue;
		}
		quantity = shipmentReceipt.get("quantity");
		
		product = delegator.findOne("Product", [productId: productId], false);
		
		detailMap["productId"] = productId;
		detailMap["productName"] = product.get("brandName");
		detailMap["quantity"] = quantity;
		detailMap["qtyInc"] = product.get("quantityIncluded");
		
		priceContext = [:];
		Map<String, Object> priceResult;
		priceContext.put("userLogin", userLogin);
		priceContext.put("productStoreId", productStoreId);
		priceContext.put("productId", productId);
		priceContext.put("priceDate", fromDate);
		priceContext.put("facilityId", facilityId);
		priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);
		if (priceResult) {
			
			detailMap["unitPrice"] = priceResult.get("basicPrice");
			
			taxList = priceResult.get("taxList");
			for(m = 0; m < taxList.size(); m++ ){
				
				if(taxList.get(m).get("taxType") == "BED_SALE"){
					exd = taxList.get(m).get("amount");
					detailMap["BED_SALE"] = exd*quantity;
					detailMap["BED_PERCENT"] = taxList.get(m).get("percentage");
				}
				if(taxList.get(m).get("taxType") == "BEDCESS_SALE"){
					edCess = taxList.get(m).get("amount");
					detailMap["BEDCESS_SALE"] = edCess*quantity;
					detailMap["BEDCESS_PERCENT"] = taxList.get(m).get("percentage");
				}
				if(taxList.get(m).get("taxType") == "BEDSECCESS_SALE"){
					higherSecCess = taxList.get(m).get("amount");
					detailMap["BEDSECCESS_SALE"] = higherSecCess*quantity;
					detailMap["BEDSECCESS_PERCENT"] = taxList.get(m).get("percentage");
				}
				
			}
			
		}
		if(  (UtilValidate.isEmpty(detailMap["BED_SALE"])) && (UtilValidate.isEmpty(detailMap["BEDCESS_SALE"])) && (UtilValidate.isEmpty(detailMap["BEDSECCESS_SALE"]))      ){
			continue;
		}
		
		tempDetailMap = [:];
		tempDetailMap.putAll(detailMap);
		
		if(UtilValidate.isEmpty(routeMap[routeId])){
			
			productMap = [:];
			productMap[productId] = tempDetailMap;
			
			tempProdMap = [:];
			tempProdMap.putAll(productMap);
			exciseDutyMap.put(boothCategory, tempProdMap);
			
			tempRouteMap = [:];
			tempRouteMap.putAll(exciseDutyMap);
			routeMap.put(routeId, tempRouteMap);
		}
		else{
			categoryUpdateMap = routeMap[routeId];
			if(UtilValidate.isEmpty(categoryUpdateMap[boothCategory])){
				
				productMap = [:];
				productMap[productId] = tempDetailMap;
				
				tempProdMap = [:];
				tempProdMap.putAll(productMap);
				
				categoryUpdateMap.put(boothCategory, tempProdMap);
				
				tempRouteMap = [:];
				tempRouteMap.putAll(categoryUpdateMap);
				
				routeMap.put(routeId, tempRouteMap);
			}
			else{
				
				productUpdateMap = categoryUpdateMap[boothCategory];
				
				if(UtilValidate.isEmpty(productUpdateMap[productId])){
					productUpdateMap[productId] = tempDetailMap;
					tempProdUpdateMap = [:];
					tempProdUpdateMap.putAll(productUpdateMap);
					categoryUpdateMap.put(boothCategory, tempProdUpdateMap);
					
					tempRouteMap = [:];
					tempRouteMap.putAll(categoryUpdateMap);
					routeMap.put(routeId, tempRouteMap);
				}
				else{
					detailsUpdateMap = productUpdateMap[productId];
					
					detailsUpdateMap["quantity"] += tempDetailMap.get("quantity");
					detailsUpdateMap["BED_SALE"] += tempDetailMap.get("BED_SALE");
					detailsUpdateMap["BEDCESS_SALE"] += tempDetailMap.get("BEDCESS_SALE");
					detailsUpdateMap["BEDSECCESS_SALE"] += tempDetailMap.get("BEDSECCESS_SALE");
					
					tempUpdateMap = [:];
					tempUpdateMap.putAll(detailsUpdateMap);
					productUpdateMap.put(productId, tempUpdateMap);
					tempProdUpdateMap = [:];
					tempProdUpdateMap.putAll(productUpdateMap);
					categoryUpdateMap.put(boothCategory, tempProdUpdateMap);
					
					tempRouteMap = [:];
					tempRouteMap.putAll(categoryUpdateMap);
					routeMap.put(routeId, tempRouteMap);
				}
			}
		}
		detailMap.clear();
		
	}
	context.put("routeMap", routeMap);

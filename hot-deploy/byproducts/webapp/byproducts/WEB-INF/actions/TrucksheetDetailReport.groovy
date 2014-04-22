	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
    import org.ofbiz.entity.GenericEntityException;
    import org.ofbiz.entity.GenericValue;
	import java.util.*;
	import java.lang.*;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.entity.util.EntityUtil;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import java.sql.*;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import org.ofbiz.service.DispatchContext;
    import org.ofbiz.service.ServiceUtil;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	import org.ofbiz.product.product.ProductWorker;
	dctx = dispatcher.getDispatchContext();
	effectiveDate = null;
	effectiveDateStr = parameters.supplyDate;
	if (UtilValidate.isEmpty(effectiveDateStr)) {
		effectiveDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			effectiveDate = new java.sql.Timestamp(dateFormat.parse(effectiveDateStr+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, "");
		}
	}
	context.put("effectiveDateStr",effectiveDateStr);
	dayBegin = UtilDateTime.getDayStart(effectiveDate);
	dayEnd = UtilDateTime.getDayEnd(effectiveDate);
	
	shipmentIds=[];
	shipmentIdList = [];
	
	if(parameters.subscriptionTypeId == "ALL"){
		 shipmentIds  = ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
		 shipmentIdList.addAll(shipmentIds);
	}
	else{
		shipmentIds = ByProductNetworkServices.getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),parameters.subscriptionTypeId);
		shipmentIdList.addAll(shipmentIds);
	}
	
	productName = [:];
	products = delegator.findList("Product", null, null, null, null, false);
	products.each{prodItem ->
		productName.put(prodItem.productId, prodItem.brandName);
	}
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIdList));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "RETURN_ACCEPTED"));
	returnCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	returnsList = delegator.findList("ReturnHeader", returnCondition, null , null, null, false);
	boothReturnMap = [:];

	returnIdsList = EntityUtil.getFieldListFromEntityList(returnsList, "returnId", true);
	
	returnShipments = EntityUtil.getFieldListFromEntityList(returnsList, "shipmentId", true);
	returnFacility = EntityUtil.getFieldListFromEntityList(returnsList, "originFacilityId", true);
	
	returnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition("returnId", EntityOperator.IN, returnIdsList), null, null, null, false);
	shipmentRoutes = delegator.findList("Shipment", EntityCondition.makeCondition("shipmentId", EntityOperator.IN, returnShipments), null, null, null, false);
	returnRoutes = EntityUtil.getFieldListFromEntityList(shipmentRoutes, "routeId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIdList));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
	orderCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	orderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, null , ["routeId"], null, false);
	
	routeIdsList = EntityUtil.getFieldListFromEntityList(orderItemsList, "routeId", true);
	trucksheetDetailList = [];
	routeIdsList.each{eachRouteId ->
		routeOrderItems = EntityUtil.filterByCondition(orderItemsList, EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, eachRouteId));
		boothIdsList = EntityUtil.getFieldListFromEntityList(routeOrderItems, "originFacilityId", true);
		boothIdsList.each{ eachBoothId ->
			String returnId = "";
			returnFlag = false;
			if(returnRoutes.contains(eachRouteId) && returnFacility.contains(eachBoothId)){
				returnFlag = true;
			}
			if(returnFlag){
				retExpr = [];
				retExpr.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, eachBoothId));
				retExpr.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, tempShipId));
				cond = EntityCondition.makeCondition(retExpr, EntityOperator.AND);
				returnDetail = EntityUtil.filterByCondition(returnsList, cond);
				
				if(returnDetail){
					returnId = (EntityUtil.getFirst(returnDetail)).get("returnId");
				}
				
			}
			boothOrderItems = EntityUtil.filterByCondition(routeOrderItems, EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, eachBoothId));
			tempShipId = (EntityUtil.getFirst(boothOrderItems)).get("shipmentId");
			productIdsList = EntityUtil.getFieldListFromEntityList(boothOrderItems, "productId", true);
			productIdsList.each{prodId ->
				tempMap = [:];
				prodOrderItems = EntityUtil.filterByCondition(boothOrderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
				tempMap.routeId = eachRouteId;
				tempMap.facilityId = eachBoothId;
				tempMap.productId = productName.get(prodId);
				index = 0;
				tempMap.subsidyQty = 0;
				tempMap.subAmount = 0;
				tempMap.dispatchQty = 0;
				tempMap.amount = 0;
				tempMap.returnedQty = 0;
				tempMap.receivedQty = 0;
				tempMap.unitPrice = 0;
				prodOrderItems.each{ eachItem->
					unitListPrice = eachItem.unitListPrice;
					tempMap.vatPercent = eachItem.vatPercent;
					if(eachItem.productSubscriptionTypeId == "EMP_SUBSIDY"){
						tempMap.subsidyQty = eachItem.quantity;
						tempMap.subAmount = eachItem.quantity*unitListPrice;
					}
					else{
						tempMap.dispatchQty = eachItem.quantity;
						tempMap.amount = eachItem.quantity*unitListPrice;
						tempMap.unitPrice = unitListPrice;
					}
					index++;
					returnQty = 0
					if(returnId && prodOrderItems.size() == index){
						exprCond = [];
						exprCond.add(EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId));
						exprCond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
						cond1 = EntityCondition.makeCondition(exprCond, EntityOperator.AND);
						returnItem = EntityUtil.filterByCondition(returnItems, cond1);
						if(returnItem){
							retItem = EntityUtil.getFirst(returnItem);
							returnQty = retItem.returnQuantity;
							tempMap.returnedQty = returnQty;
							tempMap.receivedQty = tempMap.dispatchQty+tempMap.subsidyQty - returnQty;
							tempMap.amount = tempMap.amount-(returnQty*unitListPrice);
						}
					}
					tempMap.receivedQty = tempMap.dispatchQty+tempMap.subsidyQty-returnQty;
					
				}
				trucksheetDetailList.add(tempMap);
			}
		}
	}
	
	context.trucksheetDetailList = trucksheetDetailList;
	
	
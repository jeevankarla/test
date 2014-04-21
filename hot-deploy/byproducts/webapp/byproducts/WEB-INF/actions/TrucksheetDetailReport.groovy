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
	orderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, null , null, null, false);
	
	routeIdsList = EntityUtil.getFieldListFromEntityList(orderItemsList, "routeId", true);
	trucksheetDetailList = [];
	routeIdsList.each{eachRouteId ->
		returnFlag = false;
		
		routeOrderItems = EntityUtil.filterByCondition(orderItemsList, EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, eachRouteId));
		boothIdsList = EntityUtil.getFieldListFromEntityList(routeOrderItems, "originFacilityId", true);
		boothIdsList.each{ eachBoothId ->
			
			if(returnRoutes.contains(eachRouteId) && returnFacility.contains(eachBoothId)){
				returnFlag = true;
			}
			boothOrderItems = EntityUtil.filterByCondition(routeOrderItems, EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, eachBoothId));
			productIdsList = EntityUtil.getFieldListFromEntityList(boothOrderItems, "productId", true);
			productIdsList.each{prodId ->
				tempMap = [:];
				returnProducts = [];
				if(returnFlag){
					returnProducts = EntityUtil.filterByCondition(returnItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
				}
				
				prodOrderItems = EntityUtil.filterByCondition(boothOrderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
				tempMap.routeId = eachRouteId;
				tempMap.facilityId = eachBoothId;
				tempMap.productId = productName.get(prodId);
				prodOrderItems.each{ eachItem->
					unitListPrice = eachItem.unitListPrice;
					tempMap.vatPercent = eachItem.vatPercent;
					flag = 0;
					if(eachItem.productSubscriptionTypeId == "EMP_SUBSIDY"){
						tempMap.subsidyQty = eachItem.quantity;
						tempMap.subamount = eachItem.quantity*unitListPrice;
						flag = 1;
					}
					else{
						tempMap.dispatchQty = eachItem.quantity;
						tempMap.amount = eachItem.quantity*unitListPrice;
						if(flag == 0){
							tempMap.subsidyQty = 0;
							tempMap.subamount = 0;
						}
					}
					if(returnProducts){
						returnQty = (EntityUtil.getFirst(returnProducts)).get("returnQuantity");
						tempMap.returnedQty = returnQty;
						tempMap.receivedQty = eachItem.quantity - returnQty;
						tempMap.amount = eachItem.quantity*unitListPrice - returnQty*unitListPrice;
						
					}
					else{
						tempMap.returnedQty = 0;
						tempMap.receivedQty = eachItem.quantity;
					}
					tempMap.unitPrice = unitListPrice;
				}
				trucksheetDetailList.add(tempMap);
			}
		}
	}
	/*returnsList.each{ eachReturnHeader ->
		returnId = eachReturnHeader.returnId;
		shipId = eachReturnHeader.shipmentId;
		facilityId = eachReturnHeader.originFacilityId;
		routeId = returnShipmentMap.get(shipId)
		trucksheetDetailList
	}*/
	context.trucksheetDetailList = trucksheetDetailList;
	
	
	
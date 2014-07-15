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
	
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);

effectiveDate = null;
thruEffectiveDate = null;
conditionList=[];
if(reportTypeFlag=="IndentVsDispatchReportPDF"){
effectiveDateStr = parameters.indentDate;
thruEffectiveDateStr = parameters.indentThruDate;
if (UtilValidate.isEmpty(effectiveDateStr)) {
	effectiveDate = UtilDateTime.nowTimestamp();
}
else{
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		effectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(effectiveDateStr));
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
	}
}
if (UtilValidate.isEmpty(thruEffectiveDateStr)) {
	thruEffectiveDate = UtilDateTime.nowTimestamp();
}
else{
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		thruEffectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(thruEffectiveDateStr));
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + thruEffectiveDate, "");
	}
}
intervalDays = (UtilDateTime.getIntervalInDays(effectiveDate, thruEffectiveDate)+1);

context.put("effectiveDate", effectiveDate);
formattedEffDate = UtilDateTime.toDateString(effectiveDate, "dd MMMMM yyyy");
formattedThruEffDate = UtilDateTime.toDateString(thruEffectiveDate, "dd MMMMM yyyy");
if(intervalDays > 1){
	context.reportDate = formattedEffDate+" - "+formattedThruEffDate;
}
else{
	context.reportDate = formattedEffDate;	
}
dayBegin = UtilDateTime.getDayStart(effectiveDate, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(effectiveDate, timeZone, locale);

thruDateBegin = UtilDateTime.getDayStart(thruEffectiveDate, timeZone, locale);
thruDateEnd = UtilDateTime.getDayEnd(thruEffectiveDate, timeZone, locale);

productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");

conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.IN, ["AM","PM"]));
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDateEnd));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
fieldsToSelect = ["facilityId", "sequenceNum", "productId", "quantity", "preRevisedQuantity"] as Set;
indentList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["productId"], null, false);

//List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, dayBegin, thruDateEnd, "BYPRODUCTS");
List shipmentList =ByProductNetworkServices.getByProdShipmentIds(delegator, dayBegin, dayEnd);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
orderCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
fieldsToSelect = ["productId","originFacilityId", "quantity","unitPrice","shipmentId","categoryTypeEnum"] as Set;

boothOrderCorrectedItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, fieldsToSelect , ["productId"], null, false);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
receiptCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
fieldsToSelect = ["productId", "quantityAccepted"] as Set;
shipmentReceiptList = delegator.findList("ShipmentReceiptAndItem", receiptCondition,  fieldsToSelect, null, null, false);

prodIndentAndDispatchMap = [:];

for(int i = 0; i < productList.size(); i++){
	
	BigDecimal totalIndentQty = BigDecimal.ZERO;
	BigDecimal totalDispatchQty = BigDecimal.ZERO;
	BigDecimal totalReceiptQty = BigDecimal.ZERO;
	
	productId = productList.get(i);
	List prodIndentList = EntityUtil.filterByAnd(indentList, UtilMisc.toMap("productId", productId));
	List prodOrderItemsList = EntityUtil.filterByAnd(boothOrderCorrectedItemsList, UtilMisc.toMap("productId", productId));
	//List prodReceiptList = EntityUtil.filterByAnd(shipmentReceiptList, UtilMisc.toMap("productId", productId));
	
	for(j = 0; j < prodIndentList.size(); j++){
		if(UtilValidate.isNotEmpty((prodIndentList.get(j)).get("preRevisedQuantity"))){
			totalIndentQty = totalIndentQty.add((prodIndentList.get(j)).get("preRevisedQuantity"));
		}
		else{
			totalIndentQty = totalIndentQty.add((prodIndentList.get(j)).get("quantity"));
		}
	}
	for(k = 0; k < prodOrderItemsList.size(); k++){
		totalDispatchQty = totalDispatchQty.add((prodOrderItemsList.get(k)).get("quantity"));
	}
	/*for(m = 0; m < prodReceiptList.size(); m++){
		totalReceiptQty = totalReceiptQty.add((prodReceiptList.get(m)).get("quantityAccepted"));
	}*/
	
	
	indentAndDispatchMap = [:];
	indentAndDispatchMap["indentQty"] = totalIndentQty;
	indentAndDispatchMap["dispatchQty"] = totalDispatchQty.add(totalReceiptQty);
	
	tempIndentAndDispatchMap = [:];
	tempIndentAndDispatchMap.putAll(indentAndDispatchMap);
	
	prodIndentAndDispatchMap.put(productId, tempIndentAndDispatchMap);
	
}
context.put("prodIndentAndDispatchMap", prodIndentAndDispatchMap);
}

routeProdDispatchAndDeliveredMap = [:];
if(reportTypeFlag=="TruckSheetCorrectionsReport"){
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
	List shipmentIds=[];
	shipmentIds=[];
	shipmentIdList = [];
	boothOrderCorrectedItemsList=[];
	boothOrderApprovedItemsList=[];
	List truckSheetCorrectionList=[];
	
	if(parameters.subscriptionTypeId == "ALL"){
		 if(parameters.routeId == "All-Routes"){//to get all shipments for that day
			 shipmentIds  = ByProductNetworkServices.getByProdShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
			 shipmentIdList.addAll(shipmentIds);
		 }else{
			shipment = delegator.findList("Shipment", EntityCondition.makeCondition([routeId : parameters.routeId, statusId: "GENERATED", estimatedShipDate : dayBegin]), null, null, null, false);
			shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
			shipmentIdList.addAll(shipmentIds);
		}
	}
	else{
		if(parameters.routeId == "All-Routes"){
			shipmentIds = ByProductNetworkServices.getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),parameters.subscriptionTypeId);
			shipmentIdList.addAll(shipmentIds);
		}else{
			shipType = parameters.subscriptionTypeId+"_SHIPMENT";
			shipment = delegator.findList("Shipment", EntityCondition.makeCondition([routeId : parameters.routeId, shipmentTypeId: shipType, statusId: "GENERATED", estimatedShipDate : dayBegin]), null, null, null, false);
			shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
			shipmentIdList.addAll(shipmentIds);
		}
	}
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIdList));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_CANCELLED"));
	orderCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["orderId","productId","routeId","originFacilityId","parentFacilityId","quantity","unitPrice","shipmentId","categoryTypeEnum","shipmentTypeId","changeByUserLoginId","changeDatetime"] as Set;
	boothOrderCorrectedItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, fieldsToSelect , ["changeDatetime"], null, false);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIdList));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	orderCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId","routeId","originFacilityId","parentFacilityId","quantity","unitPrice","shipmentId","categoryTypeEnum","shipmentTypeId","changeByUserLoginId","changeDatetime"] as Set;
	boothOrderApprovedItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, fieldsToSelect , ["productId"], null, false);

	Set boothSet = new HashSet(EntityUtil.getFieldListFromEntityList(boothOrderCorrectedItemsList, "originFacilityId", false));
	boothSet.each{ boothId->
		List allBoothList = EntityUtil.filterByCondition(boothOrderCorrectedItemsList, EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, boothId));
		orderIdList = EntityUtil.getFirst(allBoothList);
		orderId = orderIdList.get("orderId");
		newBoothOrderCorrectedItemsList = EntityUtil.orderBy(EntityUtil.filterByCondition(boothOrderCorrectedItemsList, EntityCondition.makeCondition("orderId",EntityOperator.EQUALS , orderId)),["changeDatetime"]);
		List boothApprovedOrdrList = EntityUtil.filterByAnd(boothOrderApprovedItemsList, UtilMisc.toMap("originFacilityId", boothId));
		extraProductList = [];
		routeId = "";
		boothId = "";
		for(int i=0; i< newBoothOrderCorrectedItemsList.size();i++){
			boothProdDispacthCorrctdMap = [:];
			boothOrder = newBoothOrderCorrectedItemsList.get(i);
			curntShipmentId=boothOrder.get("shipmentId");
			boothId=boothOrder.get("originFacilityId");
			routeId=boothOrder.get("routeId");
			boothProdDispacthCorrctdMap["Date"]=dayBegin;
			boothProdDispacthCorrctdMap["routeId"]=routeId;
			boothProdDispacthCorrctdMap["boothId"]=boothId;
			productId=boothOrder.productId;
			boothProdDispacthCorrctdMap["productId"]=productId;
			orginalQuantity=boothOrder.quantity;
			boothProdDispacthCorrctdMap["orginalQuantity"]=orginalQuantity;
			extraProductList.add(productId);
			revisedQuantity=0;
			userLoginId="";
			changedDate="";
			List boothApprovedItemList = EntityUtil.filterByAnd(boothApprovedOrdrList, UtilMisc.toMap("productId", productId));
			if(UtilValidate.isNotEmpty(boothApprovedItemList)){
				orderItem=boothApprovedItemList.get(0);
				revisedQuantity=orderItem.quantity;
				if(UtilValidate.isNotEmpty(orderItem.changeByUserLoginId)){
					userLoginId=orderItem.changeByUserLoginId;
				}
				if(UtilValidate.isNotEmpty(orderItem.changeDatetime)){
					changeDatetime=orderItem.changeDatetime;
				}
			}
			boothProdDispacthCorrctdMap["revisedQuantity"]=revisedQuantity;
			boothProdDispacthCorrctdMap["userLoginId"]=userLoginId;
			boothProdDispacthCorrctdMap["changedDate"]=changeDatetime;
			truckSheetCorrectionList.add(boothProdDispacthCorrctdMap);
		}
		
		List revisedApprovedProductList =  EntityUtil.filterByCondition(boothApprovedOrdrList, EntityCondition.makeCondition("productId",EntityOperator.NOT_IN , extraProductList));
		for(int i=0; i< revisedApprovedProductList.size();i++){
			boothProdDispacthCorrctdMap = [:];
			boothApprovedOrder = revisedApprovedProductList.get(i);
			boothId=boothApprovedOrder.get("originFacilityId");
			routeId=boothApprovedOrder.get("routeId");
			boothProdDispacthCorrctdMap["Date"]=dayBegin;
			boothProdDispacthCorrctdMap["routeId"]=routeId;
			boothProdDispacthCorrctdMap["boothId"]=boothId;
			productId=boothApprovedOrder.productId;
			boothProdDispacthCorrctdMap["productId"]=productId;
			orginalQuantity=boothApprovedOrder.quantity;
			boothProdDispacthCorrctdMap["orginalQuantity"]=0;
			revisedQuantity=0;
			userLoginId="";
			changedDate="";
			boothProdDispacthCorrctdMap["revisedQuantity"]=boothApprovedOrder.quantity;
			boothProdDispacthCorrctdMap["userLoginId"]=boothApprovedOrder.changeByUserLoginId;
			boothProdDispacthCorrctdMap["changedDate"]=boothApprovedOrder.changeDatetime;
			truckSheetCorrectionList.add(boothProdDispacthCorrctdMap);
		}
		
	}
	truckSheetCorrectionList = UtilMisc.sortMaps(truckSheetCorrectionList, UtilMisc.toList("routeId"));
	context.put("truckSheetCorrectionList", truckSheetCorrectionList);
}


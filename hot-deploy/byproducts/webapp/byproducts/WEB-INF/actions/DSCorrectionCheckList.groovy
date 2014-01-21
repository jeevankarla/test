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
	import in.vasista.vbiz.byproducts.ByProductReportServices;
	
	correctionList = [];
	
	fromDateTime = UtilDateTime.nowTimestamp();
	thruDateTime = UtilDateTime.nowTimestamp();
	
	if(UtilValidate.isNotEmpty(parameters.saleDate)){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			fromDateTime = new java.sql.Timestamp(sdf.parse(parameters.saleDate).getTime());
			thruDateTime = new java.sql.Timestamp(sdf.parse(parameters.saleDate).getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + parameters.fromDate, "");
		}
	}	
		
	dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	
	context.saleDate = fromDateTime;
	
	//List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, dayBegin, dayEnd, "BYPRODUCTS");
	
	List shipmentList  = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
	
	shipmentMap = [:];
	routeMap = [:];
	routeList = [];
	
	for(i=0; i<shipmentList.size(); i++){
		shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId" : shipmentList.get(i)), false);
		shipmentMap.put(shipmentList.get(i), shipment.get("routeId"));
		routeList.add(shipment.get("routeId"));
		routeMap.put(shipment.get("routeId"), shipmentList.get(i));
	}
	
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.IN, UtilMisc.toList("AM","PM")));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
	conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.IN, routeList));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId","facilityId", "quantity", "sequenceNum"] as Set;
	
	
	indentList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["productId"], null, false);
	indFacilityList = EntityUtil.getFieldListFromEntityList(indentList, "facilityId", true);
	indProdList = EntityUtil.getFieldListFromEntityList(indentList, "productId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
	//conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	orderCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId","originFacilityId", "quantity", "shipmentId", "changeByUserLoginId", "changeDatetime", "lastUpdatedStamp", "routeId", "orderStatusId"] as Set;
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, fieldsToSelect, ["productId", "orderStatusId", "changeDatetime"], null, false);
	
	ordFacilityList = EntityUtil.getFieldListFromEntityList(boothOrderItemsList, "originFacilityId", true);
	ordProdList = EntityUtil.getFieldListFromEntityList(boothOrderItemsList, "productId", true);
	
	/*conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
	conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
	receiptCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId","facilityId", "quantityAccepted", "receivedByUserLoginId", "lastUpdatedStamp"] as Set;
	shipmentReceiptList = delegator.findList("ShipmentReceiptAndItem", receiptCondition,  null, null, null, false);
	Debug.log("shipmentReceiptList===============DSCORRECTIONS. REPORT....+"+shipmentReceiptList);
	recFacilityList = EntityUtil.getFieldListFromEntityList(shipmentReceiptList, "facilityId", true);
	recProdList = EntityUtil.getFieldListFromEntityList(shipmentReceiptList, "productId", true);*/
	
	facilityList = [];
	facilityList.addAll(indFacilityList); 
	facilityList.addAll(ordFacilityList);
    //facilityList.addAll(recFacilityList);
	Debug.log("facilityList===============+"+facilityList);
	Set facilitySet = new HashSet(facilityList);
	List facilityList = new ArrayList(facilitySet);
	
	facilityMap = [:];
	for(i=0; i<facilityList.size(); i++){
		facilityId = facilityList.get(i);
		facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId" : facilityId), false)
		facilityMap.put(facilityId, facility.get("categoryTypeEnum"));
	}
	productList = [];
	productList.addAll(indProdList);
	productList.addAll(ordProdList);
	//productList.addAll(recProdList);
	
	Set productSet = new HashSet(productList);
	List productList = new ArrayList(productSet);
	for(int i = 0; i < productList.size(); i++){
		
		existingFacilityList = new ArrayList();
		for(int j = 0; j < facilityList.size(); j++){
			
			productId = productList.get(i);
			facilityId = ((facilityList.get(j))).toUpperCase();
			
			if(existingFacilityList.contains(facilityId)){
				continue;
			}
			existingFacilityList.add(facilityId);
			category = facilityMap.get(facilityId);
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("facilityId"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)facilityId).toUpperCase())));
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			indCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			
			List prodIndentList = EntityUtil.filterByCondition(indentList, indCondition);
			
			if(UtilValidate.isEmpty(prodIndentList)){
				routeId = null;
				totalDispatchQty = BigDecimal.ZERO;
				
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("originFacilityId"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)facilityId).toUpperCase())));
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
					OrdCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				
					List prodOrderItemsList = EntityUtil.filterByCondition(boothOrderItemsList, OrdCondition);
					for(m = 0; m < prodOrderItemsList.size(); m++){
						totalDispatchQty = (prodOrderItemsList.get(m)).get("quantity");
						modifiedTime = (prodOrderItemsList.get(m)).get("lastUpdatedStamp");
						user = (prodOrderItemsList.get(m)).get("changeByUserLoginId");
						routeId = (prodOrderItemsList.get(m)).get("routeId");
					}
				
				/*else{
					
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("facilityId"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)facilityId).toUpperCase())));
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					RecCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					
					List prodReceiptList = EntityUtil.filterByCondition(shipmentReceiptList, RecCondition);
					
					for(m = 0; m < prodReceiptList.size(); m++){
						totalDispatchQty = (prodReceiptList.get(m)).get("quantityAccepted");
						modifiedTime = (prodReceiptList.get(m)).get("lastUpdatedStamp");
						user = (prodReceiptList.get(m)).get("receivedByUserLoginId");
						
						routeId = shipmentMap.get((prodReceiptList.get(m)).get("shipmentId"));
					}
				}*/
				if(totalDispatchQty > BigDecimal.ZERO){
					prodCorrectionMap = [:];
					prodCorrectionMap.put("facilityId", facilityId);
					prodCorrectionMap.put("routeId", routeId);
					prodCorrectionMap.put("productId", productId);
					prodCorrectionMap.put("indentQty", BigDecimal.ZERO);
					prodCorrectionMap.put("correctedQty", totalDispatchQty);
					prodCorrectionMap.put("modifiedTime", modifiedTime);
					prodCorrectionMap.put("modifiedByUser", user);
					
					tempCorrectionMap = [:];
					tempCorrectionMap.putAll(prodCorrectionMap);
					
					correctionList.add(tempCorrectionMap);
				}
			}
			else{
				
				for(k = 0; k < prodIndentList.size(); k++){
					
					shipmentId = null;
				
					routeId = (prodIndentList.get(k)).get("sequenceNum");
					indentQty = (prodIndentList.get(k)).get("quantity");
					shipmentId = routeMap.get(routeId);
				
					totalDispatchQty = BigDecimal.ZERO;
					modifiedTime = null;
					user = null;
					//if(category != "PARLOUR"){
						
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("originFacilityId"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)facilityId).toUpperCase())));
						conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
						conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
						
						OrdCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
						
						List prodOrderItemsList = EntityUtil.filterByCondition(boothOrderItemsList, OrdCondition);
						
						Boolean hasApprovedOrder = false;
						
						for(m = 0; m < prodOrderItemsList.size(); m++){
							if((prodOrderItemsList.get(m)).get("orderStatusId") == "ORDER_CANCELLED"){
								if(!hasApprovedOrder){
									totalDispatchQty = BigDecimal.ZERO;
									modifiedTime = (prodOrderItemsList.get(m)).get("lastUpdatedStamp");
									user = (prodOrderItemsList.get(m)).get("changeByUserLoginId");
								}
							}
							else{
								totalDispatchQty = totalDispatchQty.add((prodOrderItemsList.get(m)).get("quantity"));
								modifiedTime = (prodOrderItemsList.get(m)).get("lastUpdatedStamp");
								user = (prodOrderItemsList.get(m)).get("changeByUserLoginId");
								hasApprovedOrder = true;
							}
							
						}
					//}
					/*else{
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("facilityId"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)facilityId).toUpperCase())));
						conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
						conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
						RecCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
						
						List prodReceiptList = EntityUtil.filterByCondition(shipmentReceiptList, RecCondition);
						for(m = 0; m < prodReceiptList.size(); m++){
							totalDispatchQty = totalDispatchQty.add((prodReceiptList.get(m)).get("quantityAccepted"));
							modifiedTime = (prodReceiptList.get(m)).get("lastUpdatedStamp");
							user = (prodReceiptList.get(m)).get("receivedByUserLoginId");
						}
					}*/
					
					if(indentQty != totalDispatchQty){
						prodCorrectionMap = [:];
						prodCorrectionMap.put("routeId", routeId);
						prodCorrectionMap.put("facilityId", facilityId);
						prodCorrectionMap.put("productId", productId);
						prodCorrectionMap.put("indentQty", indentQty);
						prodCorrectionMap.put("correctedQty", totalDispatchQty);
						prodCorrectionMap.put("modifiedTime", modifiedTime);
						prodCorrectionMap.put("modifiedByUser", user);
						
						tempCorrectionMap = [:];
						tempCorrectionMap.putAll(prodCorrectionMap);
						
						correctionList.add(tempCorrectionMap);
					}
				}
			}
		}
	}
	correctionList = UtilMisc.sortMaps(correctionList, UtilMisc.toList("routeId", "facilityId", "productId"));
	
	context.put("correctionList", correctionList);
	
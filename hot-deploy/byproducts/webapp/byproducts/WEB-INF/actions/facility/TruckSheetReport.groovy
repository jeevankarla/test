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
import org.ofbiz.product.price.PriceServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;


import in.vasista.vbiz.byproducts.ByProductServices;

rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
userLogin= context.userLogin;
context.rounding = rounding;
reportTypeFlag=parameters.reportTypeFlag;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
conditionList=[];
grandTotalMap =[:];
	productList= [];
shipmentId = null;
shipmentIds = [];
requestedFacilityId = null;
Timestamp estimatedDeliveryDateTime = null;

if(parameters.estimatedShipDate){
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	try {
		estimatedDeliveryDateTime = new java.sql.Timestamp(formatter.parse(parameters.estimatedShipDate).getTime());
		
	} catch (ParseException e) {
	}
}

context.put("estimatedDeliveryDate", estimatedDeliveryDateTime);

if(parameters.facilityId){
	requestedFacilityId = parameters.facilityId;
}
if(parameters.shipmentId){
	if(parameters.shipmentId == "allRoutes"){
		shipments = delegator.findByAnd("Shipment", [estimatedShipDate : estimatedDeliveryDateTime , shipmentTypeId : parameters.shipmentTypeId ],["routeId"]);
		shipmentIds.addAll(EntityUtil.getFieldListFromEntityList(shipments, "shipmentId", false));
	}else{
		shipmentId = parameters.shipmentId;
		shipmentIds.add(shipmentId);
	}
	
}
if(context.shipmentId){
	shipmentId = context.shipmentId;
	shipmentIds.add(shipmentId);
}
if(parameters.shipmentIds){
	shipmentIds = parameters.shipmentIds;
	shipmentId=shipmentIds[0];
}
if(context.shipmentIds){
	shipmentIds = context.shipmentIds;
	shipmentId=shipmentIds[0];
}

context.put("shipmentTypeId", parameters.shipmentTypeId);
//shipment =delegator.findOne("Shipment", [shipmentId: shipmentId], false);

	
if(!shipmentId && !shipmentIds){
	context.errorMessage = "No Shipment  Found";
	return;
}


productSubscriptionTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "SUB_PROD_TYPE"), ["enumId"] as Set, UtilMisc.toList("sequenceId"), null, false);
def adjustTotalsMap(currentFacilityMap,totalsMap){
	if(currentFacilityMap != null){
		
		facilityType = currentFacilityMap["facilityType"];
		Iterator treeMapIter = currentFacilityMap.entrySet().iterator();
		while (treeMapIter.hasNext()) {
			Map.Entry entry = treeMapIter.next();
			productDetail = delegator.findOne("Product", UtilMisc.toMap("productId"  ,entry.getKey()) ,false);
			//crates calculation for booths
			if(entry.getKey() != "facilityId" && entry.getKey() != "facilityType" && entry.getKey() != "PREV_DUE" && entry.getKey() != "paidAmount" && entry.getKey() != "routePrevDues"){
				productList.add(entry.getKey());
				cratesTotalSub =((entry.getValue().getAt("TOTAL")/(int)(12/productDetail.quantityIncluded)));
				noPacketsexc =((entry.getValue().getAt("TOTAL"))%((12/productDetail.quantityIncluded).intValue()));
				String crateValue = entry.getValue().getAt("CRATES");
				entry.getValue().putAt("LITRES", ((entry.getValue().getAt("TOTAL"))*(productDetail.quantityIncluded)));
				if(facilityType == "ROUTE"){
					if( noPacketsexc > 0){
						entry.getValue().putAt("NOPKTS", (noPacketsexc));
						entry.getValue().putAt("NOCRATES", cratesTotalSub.intValue());
					}else{
						entry.getValue().putAt("NOPKTS", noPacketsexc);
						entry.getValue().putAt("NOCRATES", cratesTotalSub.intValue());
					}
				}
				if(crateValue.indexOf('-') == -1 ){
					entry.getValue().putAt("CRATES",cratesTotalSub.doubleValue());
				}
				if(totalsMap[entry.getKey()] == null){
					valueMap =[:];
					valueMap.putAll(entry.getValue());
					totalsMap[entry.getKey()] = valueMap;
							
				} else{
						tempTotalMap =[:];
						tempTotalMap = totalsMap[entry.getKey()];
						Iterator entryIter = entry.getValue().entrySet().iterator();
						while (entryIter.hasNext()) {
							Map.Entry innerentry = entryIter.next();
							if(tempTotalMap[innerentry.getKey()] == null){
								tempTotalMap[innerentry.getKey()] = innerentry.getValue();
								
							} else{
								tempTotalMap[innerentry.getKey()] +=innerentry.getValue();
							}
												
						}
						totalsMap[entry.getKey()] = tempTotalMap;
						
				}//end of else
				//crates calculation for routes,zones, Distributors
				if((totalsMap[entry.getKey()])["TOTAL"]){
					if(facilityType == "BOOTH"  || facilityType == "ZONE"){
						cratesTotalSub =(((totalsMap[entry.getKey()])["TOTAL"]/(int)(12/productDetail.quantityIncluded)));
						noPacketsexc =(((totalsMap[entry.getKey()])["TOTAL"])%((12/productDetail.quantityIncluded).intValue()));
						if( noPacketsexc > 0){
							cratesTotalSub = cratesTotalSub+1;
							noPacketsexc = ((int)(12/productDetail.quantityIncluded)-noPacketsexc);
						}
						(totalsMap[entry.getKey()])["CRATES"] = cratesTotalSub.intValue()+"-"+noPacketsexc;
					}else{
						(totalsMap[entry.getKey()])["CRATES"] = (totalsMap[entry.getKey()])["NOCRATES"]+"-"+(totalsMap[entry.getKey()])["NOPKTS"];
					}
				}
			}
		}
	}
}//end of adjustTotalsMap method

def populateBoothOrders(boothId ,boothOrdersMap){
	productMap = [:];
	boothOrderItemsList=[];
	conditionList.clear();
	BigDecimal boothGrandTotal = BigDecimal.ZERO;
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS , boothId));
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["orderId", "orderDate", "productId","productStoreId" , "quantity" ,"grandTotal" ,"productSubscriptionTypeId" ,"originFacilityId" ,"unitPrice" ,"categoryTypeEnum" ,"quantityIncluded"] as Set;
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, fieldsToSelect , ["productId"], null, false);
	boothOrderItemsList.each { boothOrderItem ->
					tempQty = boothOrderItem.quantity;
					tempUnitPrice = boothOrderItem.unitPrice;
					tempTotalAmount = tempQty.multiply(tempUnitPrice);
					tempTotalQtyLtrs = tempQty.multiply(boothOrderItem.quantityIncluded);
					//caliculating vat amount
					
					vatAmt = BigDecimal.ZERO;;
					vatPercentage = 0;
					if(reportTypeFlag=="trucksheet"){
					priceContext = [:];
					priceResult = [:];
					Map<String, Object> priceResult;
					priceContext.put("userLogin", userLogin);
					priceContext.put("productStoreId", boothOrderItem.productStoreId);
					priceContext.put("productId",boothOrderItem.productId);
					priceContext.put("facilityId", boothId);
					priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);
					
					if (priceResult) {
						basicPrice = (BigDecimal)priceResult.get("basicPrice");
						taxList = priceResult.get("taxList");
						if(taxList){
							for(m = 0; m < taxList.size(); m++ ){
								if(taxList.get(m).get("taxType") == "VAT_SALE"){
									if(UtilValidate.isNotEmpty(taxList.get(m).get("amount"))){
									vatAmt = taxList.get(m).get("amount");
									}
									if(UtilValidate.isNotEmpty(taxList.get(m).get("percentage"))){
									vatPercentage = taxList.get(m).get("percentage");
									}
								}
							}
						}
					}
					}//end of trucksheet flag
					if(productMap[boothOrderItem.productId] == null){
						typeAndCountMap =[:];
						productSubscriptionTypeList.each{ productSubscriptionType ->
							typeAndCountMap[productSubscriptionType.enumId] = 0;
						}
						typeAndCountMap["AGNTCS"] = 0;
						typeAndCountMap["PTCCS"] = 0;
						typeAndCountMap["TOTAL"] = 0;
						typeAndCountMap["CRATES"] = 0;
						typeAndCountMap["NOPKTS"] = 0;
						typeAndCountMap["NOCRATES"] = 0;
						typeAndCountMap["LITRES"] = 0;
						typeAndCountMap["TOTALAMOUNT"] = BigDecimal.ZERO;
						typeAndCountMap["CARD_AMOUNT"] = BigDecimal.ZERO;
						typeAndCountMap["BASIC"] = BigDecimal.ZERO;
						typeAndCountMap["VAT"] = BigDecimal.ZERO;
						typeAndCountMap["VAT_AMOUNT"] = BigDecimal.ZERO;
						typeAndCountMap["BASIC"] +=tempTotalAmount;
						
						vatQtyAmount = (vatAmt.multiply(tempQty)).setScale(2, rounding);
						if(vatPercentage>0&&vatAmt==0){
							vatQtyAmount=((vatPercentage/100)*tempTotalAmount).setScale(2, rounding);
						}
						typeAndCountMap["VAT_AMOUNT"] += vatQtyAmount;
						typeAndCountMap["VAT"] +=vatPercentage;
						
						tempTotalAmount = tempTotalAmount+typeAndCountMap.get("VAT_AMOUNT");
						
						typeAndCountMap[boothOrderItem.productSubscriptionTypeId] = tempTotalQtyLtrs.doubleValue().doubleValue();
						typeAndCountMap["TOTAL"] += tempTotalQtyLtrs.doubleValue();
						if(boothOrderItem.productSubscriptionTypeId == "CARD"){
							typeAndCountMap["CARD_AMOUNT"] = tempTotalAmount.setScale(2, rounding);
						}
						if(boothOrderItem.productSubscriptionTypeId == "CASH"){
							if(boothOrderItem.categoryTypeEnum == "PTC"){
								typeAndCountMap["PTCCS"] +=typeAndCountMap[boothOrderItem.productSubscriptionTypeId];
							}else{
								typeAndCountMap["AGNTCS"] +=typeAndCountMap[boothOrderItem.productSubscriptionTypeId];
							}
							typeAndCountMap["TOTALAMOUNT"] += tempTotalAmount.setScale(2, rounding);
							boothGrandTotal = boothGrandTotal.add(tempTotalAmount);
						}
						if((boothOrderItem.categoryTypeEnum == "CR_INST") || (boothOrderItem.categoryTypeEnum == "SO_INST") || (boothOrderItem.productSubscriptionTypeId == "SPECIAL_ORDER")){
							typeAndCountMap["TOTALAMOUNT"] += tempTotalAmount.setScale(2, rounding);
							boothGrandTotal = boothGrandTotal.add(tempTotalAmount);
						}
						productMap[boothOrderItem.productId] = typeAndCountMap ;
					}
					else{
						typeAndCountMap = productMap[boothOrderItem.productId];
						typeAndCountMap[boothOrderItem.productSubscriptionTypeId] += tempTotalQtyLtrs.doubleValue();
						typeAndCountMap["TOTAL"] += tempTotalQtyLtrs.doubleValue();
						
						typeAndCountMap["BASIC"] +=tempTotalAmount;
						typeAndCountMap["VAT_AMOUNT"] += (vatAmt.multiply(tempQty)).setScale(2, rounding);
						tempTotalAmount = tempTotalAmount+typeAndCountMap.get("VAT_AMOUNT");
						
						if(boothOrderItem.productSubscriptionTypeId == "CARD"){
							typeAndCountMap["CARD_AMOUNT"] += tempTotalAmount.setScale(2, rounding);
						}
						if(boothOrderItem.productSubscriptionTypeId == "CASH"){
							//Difrenciating PTC Cash and Agent cash
							if(boothOrderItem.categoryTypeEnum == "PTC"){
								typeAndCountMap["PTCCS"] +=typeAndCountMap[boothOrderItem.productSubscriptionTypeId];
							}else{
								typeAndCountMap["AGNTCS"] +=typeAndCountMap[boothOrderItem.productSubscriptionTypeId];
							}
							typeAndCountMap["TOTALAMOUNT"] += tempTotalAmount.setScale(2, rounding);
							boothGrandTotal = boothGrandTotal.add(tempTotalAmount);
						}
						if((boothOrderItem.categoryTypeEnum == "CR_INST") || (boothOrderItem.categoryTypeEnum == "SO_INST") || (boothOrderItem.productSubscriptionTypeId == "SPECIAL_ORDER")){
							typeAndCountMap["TOTALAMOUNT"] += tempTotalAmount.setScale(2, rounding);
							boothGrandTotal = boothGrandTotal.add(tempTotalAmount);
						}
					}
	}
	if(UtilValidate.isNotEmpty(productMap)){
		
		productMap["facilityId"] = boothId;
		productMap["facilityType"] = "BOOTH";
		productMap["PREV_DUE"] = BigDecimal.ZERO;
		BigDecimal paidAmount =BigDecimal.ZERO;
		BigDecimal obAmount =BigDecimal.ZERO;
		productMap["paidAmount"] = BigDecimal.ZERO;
		
		boothOrdersMap[boothId] = productMap;
	}
	
}

def populateRouteOrders(routeGroup ,routeOrdersMap){
	boothOrdersMap = [:];
	SortedMap totalsRouteMap = new TreeMap();
	//boothsList = delegator.findByAnd("FacilityGroupMemberAndFacility", [facilityGroupId : routeGroup.facilityGroupId],["sequenceNum","facilityName"]);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS , routeGroup.ownerFacilityId));
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	condition= EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	boothsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null , null, null, false);
	boothsList = EntityUtil.getFieldListFromEntityList(boothsList, "originFacilityId", true);
	boothsList.each{ booth ->
			populateBoothOrders(booth ,boothOrdersMap);
			adjustTotalsMap(boothOrdersMap[booth],totalsRouteMap);
			totalsRouteMap["facilityId"] = routeGroup.ownerFacilityId;
			totalsRouteMap["facilityType"] = "ROUTE";
		}
	if(UtilValidate.isNotEmpty(boothOrdersMap)){
		boothOrdersMap["Totals"] = totalsRouteMap;
		routeOrdersMap[routeGroup.ownerFacilityId]= boothOrdersMap;
		//populateRouteReport(routeOrdersMap,truckSheetReportList);
	}
}

/*def populateRouteGroupOrders(routeGroup ,zoneOrdersMap){
	routeOrdersMap = [:];
	SortedMap totalsZoneMap = new TreeMap();
	if(requestedFacilityId == "All-Routes" || requestedFacilityId == null){
		zonesList = delegator.findByAnd("Facility", [parentFacilityId : zoneId],["sequenceNum","facilityName"]);
		zonesList.each{ zone ->
			populateRouteOrders(zone.facilityId ,routeOrdersMap);
			if(routeOrdersMap[zone.facilityId] != null){
				adjustTotalsMap((routeOrdersMap[zone.facilityId])["Totals"],totalsZoneMap);
				totalsZoneMap["facilityId"] = zoneId;
				totalsZoneMap["facilityType"] = "ROUTE";
			}
		}
	}else{
		populateRouteOrders(requestedFacilityId ,routeOrdersMap);
		if(routeOrdersMap[requestedFacilityId] != null){
			adjustTotalsMap((routeOrdersMap[requestedFacilityId])["Totals"],totalsZoneMap);
			totalsZoneMap["facilityId"] = zoneId;
			totalsZoneMap["facilityType"] = "ROUTE";
		}
	}
	
	if(UtilValidate.isNotEmpty(routeOrdersMap)){
		routeOrdersMap["Totals"] = totalsZoneMap;
		zoneOrdersMap[zoneId]= routeOrdersMap;
	}
}*/
/*def populateDistributorOrders(distributorId ,distributorOrdersList ,distributorTotalOrdersMap){
	zoneOrdersMap = [:];
	SortedMap totalsDistributorMap = new TreeMap();
	distributorOrdersMap =[:];
	distributorsList = delegator.findByAnd("Facility", [parentFacilityId : distributorId],["sequenceNum","facilityName"]);
		distributorsList.each{ distributor ->
			populateZoneOrders(distributor.facilityId ,zoneOrdersMap);
			if(zoneOrdersMap[distributor.facilityId] != null){
				adjustTotalsMap((zoneOrdersMap[distributor.facilityId])["Totals"],totalsDistributorMap);
				totalsDistributorMap["facilityId"] = distributorId;
				totalsDistributorMap["facilityType"] = "DISTRIBUTOR";
			}
		}
	if(UtilValidate.isNotEmpty(zoneOrdersMap)){
		zoneOrdersMap["Totals"] = totalsDistributorMap;
		distributorTotalOrdersMap[distributorId] = zoneOrdersMap;
		distributorOrdersMap[distributorId]= zoneOrdersMap;
		distributorOrdersList.add(distributorOrdersMap);
		
	}
	
}*/
//routeGroupList =[];
truckSheetReportList =[];
routesList =[];
//mainFacilityList = delegator.findByAnd("Facility", [parentFacilityId : null],["sequenceNum","facilityName"]);
if(UtilValidate.isNotEmpty(parameters.facilityId) && (parameters.facilityId)!="allRoutes"){
	shipment =delegator.findOne("Shipment", [shipmentId: parameters.facilityId], false);
	routesList.add(shipment.routeId);
}else{
    routesList = ByProductNetworkServices.getRoutes(dctx,context).get("routesList");
}
SortedMap totalMainFacilityMap = new TreeMap();
routeOrdersMap = [:];
routesList.each{ routeFacility ->
	routGroupList=delegator.findByAnd("FacilityGroup", [ownerFacilityId : routeFacility],null);
	//routGroupList = EntityUtil.filterByDate(routGroupList, estimatedDeliveryDateTime);
	routGroupList.each{ routeGroup ->
		populateRouteOrders(routeGroup ,routeOrdersMap);
		if(routeOrdersMap[routeGroup.ownerFacilityId] != null){
			adjustTotalsMap((routeOrdersMap[routeGroup.ownerFacilityId])["Totals"],totalMainFacilityMap);
			totalMainFacilityMap["facilityId"] = "DISTRIBUTORS";
			totalMainFacilityMap["facilityType"] = "DISTRIBUTOR";
		}
	}
}
if(UtilValidate.isNotEmpty(routeOrdersMap)){
	grandTotalMap["Totals"] =  totalMainFacilityMap;
	context.put("grandTotalMap", grandTotalMap);
}

def populateRouteReport(routeReport,truckSheetReportList){
	Iterator treeMapIter = routeReport.entrySet().iterator();
	while (treeMapIter.hasNext()) {
		Map.Entry entry = treeMapIter.next();
			truckSheetReportList.add(entry.getValue());
	}
	
}
	

def populateZoneReport(zoneReport,truckSheetReportList){
	
	Iterator treeMapIter = zoneReport.entrySet().iterator();
	while (treeMapIter.hasNext()) {
		Map.Entry entry = treeMapIter.next();
		if(entry.getKey() == "Totals"){
			truckSheetReportList.add(entry.getValue());
		}
		else{
			populateRouteReport(entry.getValue(),truckSheetReportList);
		}
	}
}
	
def populateDistributorReport(distributorReport,truckSheetReportList){
	
Iterator treeMapIter = distributorReport.entrySet().iterator();
while (treeMapIter.hasNext()) {
	Map.Entry entry = treeMapIter.next();
	if(entry.getKey() == "Totals"){
		truckSheetReportList.add(entry.getValue());
	}
	else{
		populateZoneReport(entry.getValue(),truckSheetReportList);
	}
}
}
	
		Iterator treeMapIter = routeOrdersMap.entrySet().iterator();
		while (treeMapIter.hasNext()) {
			Map.Entry entry = treeMapIter.next();
			if(entry.getKey() == "Totals"){
				truckSheetReportList.add(entry.getValue());
			}
			else{
				populateRouteReport(entry.getValue(),truckSheetReportList);
			}
		}
		
/*else{
	zoneFacilityList = delegator.findByAnd("Facility", [facilityTypeId : "ZONE"],["facilityId"]);
	
	zoneFacilityList.each{ zoneFacility ->
		
		distributorOrdersList.each{ distributorOrders ->
			
		Iterator treeMapIter = distributorOrders.entrySet().iterator();
		while (treeMapIter.hasNext()) {
			Map.Entry entry = treeMapIter.next();
			if( entry.getKey() == zoneFacility.parentFacilityId){
				
				if(entry.getValue().get(zoneFacility.facilityId)){
					populateZoneReport(entry.getValue().get(zoneFacility.facilityId),truckSheetReportList);
				}
			}
		}
	}
		
	}
}*/
context.put("truckSheetReportList", truckSheetReportList);


Set productSet=(Set)productList;
context.put("productSet", productSet);

//context.put("grandTotalMap", grandTotalMap);
return "success";
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
import org.ofbiz.product.product.ProductWorker;



rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
context.rounding = rounding;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
conditionList=[];
grandTotalMap =[:];
shipmentId = null;
shipmentIds = [];
requestedFacilityId = null;
if(parameters.facilityId){
	requestedFacilityId = parameters.facilityId;
}
if(parameters.shipmentId){
	shipmentId = parameters.shipmentId;
	shipmentIds.add(shipmentId);
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

shipment =delegator.findOne("Shipment", [shipmentId: shipmentId], false);
estimatedDeliveryDate = null;
context.put("shipmentTypeId", shipment.shipmentTypeId);
if (shipment) {
	context.put("estimatedDeliveryDate", shipment.estimatedShipDate);
	estimatedDeliveryDate = shipment.estimatedShipDate;
}

if(!shipmentId && !shipmentIds){
	context.errorMessage = "No Shipment  Found";
	return;
}
productSubscriptionTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "SUB_PROD_TYPE"), ["enumId"] as Set, UtilMisc.toList("sequenceId"), null, false);
productsLmsBulkList = ProductWorker.getProductsByCategory(delegator ,"LMS_BULK" ,null);
flavredProductList = ProductWorker.getProductsByCategory(delegator ,"FLAVERD_MILK" ,null);
butterProductList = ProductWorker.getProductsByCategory(delegator ,"BUTTER_MILK" ,null);
bulkMilkProducts = EntityUtil.getFieldListFromEntityList(productsLmsBulkList, "productId", true);
flavredProductList = EntityUtil.getFieldListFromEntityList(flavredProductList, "productId", true);
butterProductList = EntityUtil.getFieldListFromEntityList(butterProductList, "productId", true);
context.put("bulkMilkProducts", bulkMilkProducts);
context.put("flavredProductList", flavredProductList);
context.put("butterProductList", butterProductList);
productList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.put("productList", productList);
def adjustTotalsMap(currentFacilityMap,totalsMap){	
	if(currentFacilityMap != null){		
		facilityType = currentFacilityMap["facilityType"];		
		Iterator treeMapIter = currentFacilityMap.entrySet().iterator();		
		while (treeMapIter.hasNext()) {
			Map.Entry entry = treeMapIter.next();
			productDetail = delegator.findOne("Product", UtilMisc.toMap("productId"  ,entry.getKey()) ,false);						
			//crates calculation for booths			
			if(entry.getKey() != "facilityId" && entry.getKey() != "facilityType" && entry.getKey() != "PREV_DUE" && entry.getKey() != "paidAmount" && entry.getKey() != "routePrevDues"){
				cratesTotalSub =((entry.getValue().getAt("TOTAL")/(int)(12/productDetail.quantityIncluded)));
				noPacketsexc =((entry.getValue().getAt("TOTAL"))%((12/productDetail.quantityIncluded).intValue()));
				String crateValue = entry.getValue().getAt("CRATES");
				entry.getValue().putAt("LITRES", ((entry.getValue().getAt("TOTAL"))*(productDetail.quantityIncluded)));
		      //Cans calciculation starts here
				if((facilityType=="ROUTE")&&(bulkMilkProducts.contains(entry.getKey()))){
					entry.getValue().putAt("CANS40", 0);
					entry.getValue().putAt("CANS30", 0);
					entry.getValue().putAt("CANS20", 0);//setting cans value to zero for Route level
						if(entry.getValue().getAt("LITRES")>40){
							cansTotal =(entry.getValue().getAt("LITRES")/40).intValue();
							entry.getValue().putAt("CANS40", cansTotal.intValue()+0);
							remingTotal =((entry.getValue().getAt("LITRES").intValue())%40);
							if((remingTotal>=1)&&(remingTotal<=20)){
								entry.getValue().putAt("CANS20",entry.getValue().getAt("CANS20").intValue()+1);
							}else if((remingTotal>20)&&(remingTotal<=30)){
							entry.getValue().putAt("CANS30",entry.getValue().getAt("CANS30").intValue()+1);
							}else if ((remingTotal>30)&&(remingTotal<=40)){
								entry.getValue().putAt("CANS40",entry.getValue().getAt("CANS40").intValue()+1);
							}
					    }else if((entry.getValue().getAt("LITRES")<=40)&&(entry.getValue().getAt("LITRES")>30)){
							entry.getValue().putAt("CANS40",entry.getValue().getAt("CANS40").intValue()+1);
						}else if((entry.getValue().getAt("LITRES")<=30)&&(entry.getValue().getAt("LITRES")>20)){
							entry.getValue().putAt("CANS30",entry.getValue().getAt("CANS30").intValue()+1);
						}else if((entry.getValue().getAt("LITRES")>=1)&&(entry.getValue().getAt("LITRES")<=20)){
							entry.getValue().putAt("CANS20",entry.getValue().getAt("CANS20").intValue()+1);
						}
						}//end of Cans calculation for LooskmilkProducts
				if(facilityType == "ROUTE"){
					if( noPacketsexc > 0){
						entry.getValue().putAt("NOPKTS", noPacketsexc);
						entry.getValue().putAt("NOCRATES",cratesTotalSub.intValue());
						/*entry.getValue().putAt("NOPKTS", ((int)(12/productDetail.quantityIncluded)-noPacketsexc));
						entry.getValue().putAt("NOCRATES", cratesTotalSub.intValue()+1);*/
						
					}else{
						entry.getValue().putAt("NOPKTS", noPacketsexc);
						entry.getValue().putAt("NOCRATES", cratesTotalSub.intValue());
					}
				}	
				if(crateValue.indexOf('-') == -1 ){
					entry.getValue().putAt("CRATES",cratesTotalSub.intValue()+"."+noPacketsexc);
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
							//cratesTotalSub = cratesTotalSub+1;
							//noPacketsexc = ((int)(12/productDetail.quantityIncluded)-noPacketsexc);
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
	if((UtilValidate.isNotEmpty(reportTypeFlag))&&(reportTypeFlag=="crInstDeliveryChalan"))
   {
	conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS , "CR_INST"));
   }
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["orderId", "orderDate", "productId", "quantity" ,"grandTotal" ,"productSubscriptionTypeId" ,"originFacilityId" ,"unitPrice" ,"categoryTypeEnum" ,"quantityIncluded"] as Set;		
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, fieldsToSelect , ["productId"], null, false);
	boothOrderItemsList.each { boothOrderItem ->
					tempQty = boothOrderItem.quantity;
					tempUnitPrice = boothOrderItem.unitPrice;
					tempTotalAmount = tempQty.multiply(tempUnitPrice);
					tempTotalQtyLtrs = tempQty.multiply(boothOrderItem.quantityIncluded);
					if(productMap[boothOrderItem.productId] == null){
						typeAndCountMap =[:];
						productSubscriptionTypeList.each{ productSubscriptionType ->
							typeAndCountMap[productSubscriptionType.enumId] = 0;
						}
						typeAndCountMap["AGNTCS"] = 0;
						typeAndCountMap["PTCCS"] = 0;
						typeAndCountMap["TOTAL"] = 0;
						typeAndCountMap["NOPKTS"] = 0;
						typeAndCountMap["NOCRATES"] = 0;
						typeAndCountMap["LITRES"] = 0;
						typeAndCountMap["CRATES"] = 0;
						typeAndCountMap["TOTALAMOUNT"] = BigDecimal.ZERO;
						typeAndCountMap["CARD_AMOUNT"] = BigDecimal.ZERO;
						typeAndCountMap["CANS20"] = 0;
						typeAndCountMap["CANS30"] = 0;
						typeAndCountMap["CANS40"] = 0;
						
						typeAndCountMap[boothOrderItem.productSubscriptionTypeId] = (boothOrderItem.quantity).intValue();
						typeAndCountMap["TOTAL"] += (boothOrderItem.quantity).intValue();
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
						typeAndCountMap[boothOrderItem.productSubscriptionTypeId] += (boothOrderItem.quantity).intValue();
						typeAndCountMap["TOTAL"] += (boothOrderItem.quantity).intValue();
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
		
		Map<String, Object> BoothPaidPayments = NetworkServices.getBoothPaidPayments( dctx , [facilityId : boothId , paymentDate:(UtilDateTime.toDateString(estimatedDeliveryDate, "yyyy-MM-dd HH:mm:ss"))]);
		Map boothTotalPayments = FastMap.newInstance();
		List paymentsList = (List) BoothPaidPayments.get("boothPaymentsList");
		if (paymentsList.size() != 0) {
			boothTotalPayments = (Map)paymentsList.get(0);
			paidAmount = (BigDecimal)boothTotalPayments.get("amount");
			productMap["paidAmount"] = paidAmount.setScale(2, rounding);
		}
		
		/*obAmount =	(NetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: estimatedDeliveryDate, facilityId:boothId])).get("openingBalance");
		if(obAmount.compareTo(BigDecimal.ZERO) < 0 ){
						
			productMap["PREV_DUE"] = obAmount.subtract(boothGrandTotal);
		}else{
			productMap["PREV_DUE"] = obAmount;
		}*/
		boothDuesDetail = NetworkServices.getDaywiseBoothDues(dctx, [userLogin: userLogin, facilityId: boothId]);
		duesList = boothDuesDetail["boothDuesList"];	
		duesList.each { due ->			
			int days = UtilDateTime.getIntervalInDays(estimatedDeliveryDate, due.supplyDate);
			if(days == 0){
				productMap["PREV_DUE"] = (boothDuesDetail["totalAmount"])-(boothGrandTotal);
			}	
		}
		boothOrdersMap[boothId] = productMap;
	}
	
}

def populateRouteOrders(routeId ,routeOrdersMap){
	boothOrdersMap = [:];
	
	SortedMap totalsRouteMap = new TreeMap();	
	boothsList = delegator.findByAnd("Facility", [parentFacilityId : routeId],["sequenceNum","facilityName"]);
		boothsList.each{ booth ->
			populateBoothOrders(booth.facilityId ,boothOrdersMap);
			adjustTotalsMap(boothOrdersMap[booth.facilityId],totalsRouteMap);
			totalsRouteMap["facilityId"] = routeId;
			totalsRouteMap["facilityType"] = "ROUTE";
		}
	if(UtilValidate.isNotEmpty(boothOrdersMap)){
		boothOrdersMap["Totals"] = totalsRouteMap;		
		routeOrdersMap[routeId]= boothOrdersMap;					
		}
	
}
def populateZoneOrders(zoneId ,zoneOrdersMap){
	routeOrdersMap = [:];	
	SortedMap totalsZoneMap = new TreeMap();
	if(requestedFacilityId == "All-Routes" || requestedFacilityId == null){
		zonesList = delegator.findByAnd("Facility", [parentFacilityId : zoneId],["sequenceNum","facilityName"]);
		zonesList.each{ zone ->
			populateRouteOrders(zone.facilityId ,routeOrdersMap);
			if(routeOrdersMap[zone.facilityId] != null){
				adjustTotalsMap((routeOrdersMap[zone.facilityId])["Totals"],totalsZoneMap);
				totalsZoneMap["facilityId"] = zoneId;
				totalsZoneMap["facilityType"] = "ZONE";
			}
		}
	}else{
		populateRouteOrders(requestedFacilityId ,routeOrdersMap);
		if(routeOrdersMap[requestedFacilityId] != null){
			adjustTotalsMap((routeOrdersMap[requestedFacilityId])["Totals"],totalsZoneMap);
			totalsZoneMap["facilityId"] = zoneId;
			totalsZoneMap["facilityType"] = "ZONE";
		}
	}
	if(UtilValidate.isNotEmpty(routeOrdersMap)){
		routeOrdersMap["Totals"] = totalsZoneMap;		
		zoneOrdersMap[zoneId]= routeOrdersMap;	
	}	
}
def populateDistributorOrders(distributorId ,distributorOrdersList ,distributorTotalOrdersMap){	
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
	
}
distributorOrdersList =[];

mainFacilityList = delegator.findByAnd("Facility", [parentFacilityId : null],["sequenceNum","facilityName"]);
distributorTotalOrdersMap =[:];
SortedMap totalMainFacilityMap = new TreeMap();
mainFacilityList.each{ mainFacility ->
	mainDistributorsFacilityList=delegator.findByAnd("Facility", [parentFacilityId : mainFacility.facilityId],["sequenceNum","facilityName"]);
	mainDistributorsFacilityList.each{ mainDistributorsFacility ->
		populateDistributorOrders(mainDistributorsFacility.facilityId ,distributorOrdersList ,distributorTotalOrdersMap);
		if(distributorOrdersMap[mainDistributorsFacility.facilityId] != null){
			
			adjustTotalsMap((distributorTotalOrdersMap[mainDistributorsFacility.facilityId])["Totals"],totalMainFacilityMap);
			totalMainFacilityMap["facilityId"] = "DISTRIBUTORS";
			totalMainFacilityMap["facilityType"] = "DISTRIBUTOR";
		}
	}	
}
if(UtilValidate.isNotEmpty(distributorTotalOrdersMap)){
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

truckSheetReportList =[];

if( reportTypeFlag != "trucksheet" && reportTypeFlag!="abstract"){
	distributorOrdersList.each{ distributorOrders ->
		
		Iterator treeMapIter = distributorOrders.entrySet().iterator();
		while (treeMapIter.hasNext()) {
			Map.Entry entry = treeMapIter.next();
			if(entry.getKey() == "Totals"){
				truckSheetReportList.add(entry.getValue());
			}
			else{
				populateDistributorReport(entry.getValue(),truckSheetReportList);
			}
		}
		
	}
}else{
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
}
context.put("truckSheetReportList", truckSheetReportList);
if(reportTypeFlag=="consolidatedLoadingStatement")
{
	//Retrieve NonGrouped Routes
	groupedRouteMembersList=[];
	groupedRoutes=[];
	nonGroupedList=[];
	groupsGrandTotal=[:];
	SortedMap allGroupsTotal= new TreeMap();
	fields = new HashSet(["facilityId"]);
	groupedRouteMembersList = delegator.findList("FacilityGroupMember",null , fields, null, null, false);
	groupedRoutes=EntityUtil.getFieldListFromEntityList(groupedRouteMembersList,"facilityId" ,true);
	Map routesMap = NetworkServices.getRoutes(dctx , UtilMisc.toMap());
	nonGroupedList = routesMap.getAt("routesList");
	nonGroupedList.removeAll(groupedRoutes);//Removing Grouped Routes from All Routes...So result will be nonGrouped
	//End of NonGrouped	
	trucksheetReportList = context.get("truckSheetReportList");
	groupDetailsMap =[:];
	nonGroupedMap=[:];
	totalsMapSUM =[:];

	EntityCondition cond;
	if(shipment.shipmentTypeId.startsWith("AM")){
		cond = EntityCondition.makeCondition(UtilMisc.toList(
			EntityCondition.makeCondition("facilityGroupTypeId", EntityOperator.EQUALS, "DAIRY_LMD_TYPE"),
			EntityCondition.makeCondition("facilityGroupId", EntityOperator.LIKE, "%_AM%")), EntityOperator.AND);
		
	//facilityGroupList = delegator.findList("FacilityGroup",EntityCondition.makeCondition("facilityGroupTypeId",EntityOperator.LIKE, "%_AM%") , null, null, null, false);
	}else{
	cond = EntityCondition.makeCondition(UtilMisc.toList(
			EntityCondition.makeCondition("facilityGroupTypeId", EntityOperator.EQUALS, "DAIRY_LMD_TYPE"),
			EntityCondition.makeCondition("facilityGroupId", EntityOperator.LIKE, "%_PM%")), EntityOperator.AND);
		
	}
	
	facilityGroupList = delegator.findList("FacilityGroup",cond , null, null, null, false);
	facilityGroupList.each{ facilityGroup ->
		routeMembersList = delegator.findList("FacilityGroupMember",EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS , facilityGroup.facilityGroupId) , null, null, null, false);
		routeMembersMap =[:];
		notRouteMembersMap =[:];
		SortedMap groupTotalMap = new TreeMap();
		routeMembersList.each{ route ->
			routeId = route.facilityId;
			trucksheetReportList.each{ trucksheetReport ->
				facilityTypeId = trucksheetReport.facilityType;
				if(facilityTypeId =="ROUTE"){
					facilityId = trucksheetReport.facilityId;
					if(routeId == facilityId){
						adjustTotalsMap(trucksheetReport,groupTotalMap);
						routeMembersMap.put(routeId, trucksheetReport);
					}
					if(nonGroupedList.contains(facilityId)){
						nonGroupedMap.put(facilityId, trucksheetReport);
						nonGroupedList.remove(facilityId);
					}
				}
			}
		}
		if(UtilValidate.isNotEmpty(groupTotalMap)){//add total only when itis not empty
		//routeMembersMap.put("Total", groupTotalMap);
		adjustTotalsMap(groupTotalMap,allGroupsTotal);
		}
		if(UtilValidate.isNotEmpty(routeMembersMap)){//add facilityGroup when it is not emty
		groupDetailsMap.put(facilityGroup.facilityGroupId, routeMembersMap);
		}
		
	}//end of GroupedRoutes Logic
	if(UtilValidate.isNotEmpty(nonGroupedMap)){
		groupDetailsMap.put("NonGrouped",nonGroupedMap);
	}
	context.groupDetailsMap =groupDetailsMap;
	
	groupsGrandTotal["Totals"] =  allGroupsTotal;
	context.put("groupsGrandTotal", groupsGrandTotal);

}
return "success";
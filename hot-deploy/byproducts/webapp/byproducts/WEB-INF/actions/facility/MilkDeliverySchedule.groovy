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


import java.io.ObjectOutputStream.DebugTraceInfoStack;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
rounding = RoundingMode.HALF_UP;

shipmentId=parameters.shipmentId;
GenericValue shipment =null;
if(! UtilValidate.isEmpty(shipmentId)){
shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
}
dctx = dispatcher.getDispatchContext();
productList = ByProductNetworkServices.getAllLmsAndByProdProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.productList = productList;

allChanges= false;
if (parameters.all == 'Y') {
	allChanges = true;
}
checkListType= parameters.checkListType;
context.checkListType = "";
List exprList = [];
List checkListReportList = [];
Map scheduleListReportMap = [:];
Map routeWiseProdTotals = [:];
List lastChangeSubProdList =FastList.newInstance();
lastChangeSubProdMap = [:];
List milkCardTypeList =[];

if(! UtilValidate.isEmpty(shipmentId)){
dayBegin = UtilDateTime.getDayStart(shipment.estimatedShipDate);
}else{
dayBegin = UtilDateTime.getNextDayStart(UtilDateTime.nowTimestamp());
}

context.put("estimatedShipDate", dayBegin);
dayEnd = UtilDateTime.getDayEnd(dayBegin, timeZone, locale);
supplyTypeMap =[:];
supplyTypeMap["SPECIAL_ORDER"] = "SO";
supplyTypeMap["CASH"] = "CS";
supplyTypeMap["CARD"] = "CD";
supplyTypeMap["CREDIT"] = "CR";

	
	List shipmentIds = ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
	
	exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN ,shipmentIds));
	exprList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS  ,"SALES_ORDER"));
	exprList.add(EntityCondition.makeCondition("shipmentStatusId", EntityOperator.EQUALS , "GENERATED"));
	
	if(parameters.facilityId != "All-Routes" && parameters.facilityId != null ){
		exprList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS , parameters.facilityId));
		orderBy=null;
	}else{
		orderBy = ["originFacilityId","parentFacilityId"];
	}
	/*exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO  ,dayBegin));
	exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));*/
	/*exprList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	exprList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));	*/
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	checkListItemList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, orderBy , null, false);
	checkListItemList=UtilMisc.sortMaps(checkListItemList, UtilMisc.toList("parentFacilityId"));
	if(! UtilValidate.isEmpty(checkListItemList)){
		context.put("facilityId", (checkListItemList.get(0)).originFacilityId);
	}
	productTotalsMap=[:];
if (checkListItemList.size() > 0) {
		
	productList.each{ product ->
		lastChangeSubProdMap[product.productId] = '';
	}
	checkListReportList.add(lastChangeSubProdMap);
	lastChangeSubProdList.add(lastChangeSubProdMap);
}

tempOrderId = "";
tempBoothId = "";
tempRouteId = "";
tempPrevBoothId = "";
List templist = [];

 
 /*=================== BEGIN Initializing Product Totals Maps ==================*/
 productList.each{ product ->
	 productTotalsMap[product.productId]=BigDecimal.ZERO;
 }
 productTypeTotalsMap=[:];
 productTypeTotalsMap["CASH"]=[:];
 productTypeTotalsMap["CARD"]=[:];
 productTypeTotalsMap["CASH_CARD"]=[:];
 productTypeTotalsMap["CASH"].putAll(productTotalsMap);
 productTypeTotalsMap["CARD"].putAll(productTotalsMap);
 productTypeTotalsMap["CASH_CARD"].putAll(productTotalsMap);
 
 routeProductTotalsMap=[:];
 routeProductTotalsMap["CASH"]=[:];
 routeProductTotalsMap["CARD"]=[:];
 routeProductTotalsMap["CASH_CARD"]=[:];
 routeProductTotalsMap["CASH"].putAll(productTotalsMap);
 routeProductTotalsMap["CARD"].putAll(productTotalsMap);
 routeProductTotalsMap["CASH_CARD"].putAll(productTotalsMap);
 /*============= END==========*/
 

for (int i=0; i < checkListItemList.size(); i++) {
	checkListItemProd = checkListItemList.get(i);
	
	if (tempOrderId == "") {
		tempOrderId = checkListItemProd.orderId;
		tempBoothId = checkListItemProd.originFacilityId;
		tempRouteId = checkListItemProd.parentFacilityId;
		lastChangeSubProdMap["boothId"] = checkListItemProd.originFacilityId;
		lastChangeSubProdMap["supplyType"] = supplyTypeMap[checkListItemProd.productSubscriptionTypeId];
		lastChangeSubProdMap["routeId"] = checkListItemProd.parentFacilityId;
		
		Map<String, Object> boothPayments = ByProductNetworkServices.getBoothPayments(delegator, dctx.getDispatcher(), userLogin,
			UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"), null, checkListItemProd.originFacilityId ,null ,Boolean.FALSE);
		Map<String, Object> currentBoothPayments = ByProductNetworkServices.getBoothReceivablePayments(delegator, dctx.getDispatcher(), userLogin,
			UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"), null, checkListItemProd.originFacilityId ,null ,Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
		Map boothTotalDues = FastMap.newInstance();
		boothTotalDues["totalDue"]=BigDecimal.ZERO;
		boothTotalDues["grandTotal"]=BigDecimal.ZERO;
		List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
		if (UtilValidate.isNotEmpty(boothPaymentsList)) {
			 boothTotalDues = (Map)boothPaymentsList.get(0);
		}
		Map currentBoothTotalDues = FastMap.newInstance();
		currentBoothTotalDues["grandTotal"]=BigDecimal.ZERO;
		List currentBoothPaymentsList = (List) currentBoothPayments.get("boothPaymentsList");
		if (UtilValidate.isNotEmpty(currentBoothPaymentsList)) {
			 currentBoothTotalDues = (Map)currentBoothPaymentsList.get(0);
		}
		lastChangeSubProdMap["PREV_DUE"] = ((BigDecimal)boothTotalDues.getAt("totalDue")).subtract((BigDecimal)boothTotalDues.getAt("grandTotal"));
		lastChangeSubProdMap["TODAY_DUE"] = (BigDecimal)currentBoothTotalDues.getAt("grandTotal");
	}
	if(tempBoothId != checkListItemProd.originFacilityId){
		scheduleListReportMap.putAt(tempBoothId, lastChangeSubProdList);
		tempBoothId = checkListItemProd.originFacilityId;
		lastChangeSubProdList = FastList.newInstance();
	}
	
	
	
	/*============= BEGIN Calc Product Totals and adding them to respective maps ==========*/
	if(tempRouteId != checkListItemProd.parentFacilityId){
		Map tempMap = [:];
		tempMap.putAll(routeProductTotalsMap);
		routeWiseProdTotals.putAt(tempRouteId, tempMap);
		tempRouteId = checkListItemProd.parentFacilityId;
		routeProductTotalsMap.clear();
		routeProductTotalsMap["CASH"]=[:];
		routeProductTotalsMap["CARD"]=[:];
		routeProductTotalsMap["CASH_CARD"]=[:];
		routeProductTotalsMap["CASH"].putAll(productTotalsMap);
		routeProductTotalsMap["CARD"].putAll(productTotalsMap);
		routeProductTotalsMap["CASH_CARD"].putAll(productTotalsMap);
	}
	tempProductTotalsMap=[:];
	tempGrandTotalsMap=[:];
	tempRouteProductTotalsMap=[:];
	tempProdTypeTotalsMap=[:];
	if(checkListItemProd.productSubscriptionTypeId == "CASH"){
		tempProductTotalsMap.putAll(productTypeTotalsMap["CASH"]);
		tempProductTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempProductTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		
		tempGrandTotalsMap.putAll(productTypeTotalsMap["CASH_CARD"]);
		tempGrandTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempGrandTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		
		productTypeTotalsMap["CASH"].putAll(tempProductTotalsMap);
		productTypeTotalsMap["CASH_CARD"].putAll(tempGrandTotalsMap);
		
		tempRouteProductTotalsMap.putAll(routeProductTotalsMap["CASH"]);
		tempRouteProductTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempRouteProductTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		
		tempProdTypeTotalsMap.putAll(routeProductTotalsMap["CASH_CARD"]);
		tempProdTypeTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempProdTypeTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		
		routeProductTotalsMap["CASH"].putAll(tempRouteProductTotalsMap);
		routeProductTotalsMap["CASH_CARD"].putAll(tempProdTypeTotalsMap);
	}
	 if(checkListItemProd.productSubscriptionTypeId == "CARD"){
		 tempProductTotalsMap.putAll( productTypeTotalsMap["CARD"]);
		 tempProductTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempProductTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		 
		 tempGrandTotalsMap.putAll(productTypeTotalsMap["CASH_CARD"]);
		 tempGrandTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempGrandTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		 
		 productTypeTotalsMap["CARD"].putAll(tempProductTotalsMap);
		 productTypeTotalsMap["CASH_CARD"].putAll(tempGrandTotalsMap);
		 
		 tempRouteProductTotalsMap.putAll(routeProductTotalsMap["CARD"]);
		 tempRouteProductTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempRouteProductTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		 
		 tempProdTypeTotalsMap.putAll(routeProductTotalsMap["CASH_CARD"]);
		 tempProdTypeTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempProdTypeTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		 
		 routeProductTotalsMap["CARD"].putAll(tempRouteProductTotalsMap);
		 routeProductTotalsMap["CASH_CARD"].putAll(tempProdTypeTotalsMap);
	}
	 if(checkListItemProd.productSubscriptionTypeId == "CREDIT"){
		 tempGrandTotalsMap.putAll(productTypeTotalsMap["CASH_CARD"]);
		 tempGrandTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempGrandTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		 
		 productTypeTotalsMap["CASH_CARD"].putAll(tempGrandTotalsMap);
		 
		 tempProdTypeTotalsMap.putAll(routeProductTotalsMap["CASH_CARD"]);
		 tempProdTypeTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempProdTypeTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		 
		 routeProductTotalsMap["CASH_CARD"].putAll(tempProdTypeTotalsMap);
	}
	 if(checkListItemProd.productSubscriptionTypeId == "SPECIAL_ORDER"){
		 tempGrandTotalsMap.putAll(productTypeTotalsMap["CASH_CARD"]);
		 tempGrandTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempGrandTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		 
		 productTypeTotalsMap["CASH_CARD"].putAll(tempGrandTotalsMap);
		 
		 tempProdTypeTotalsMap.putAll(routeProductTotalsMap["CASH_CARD"]);
		 tempProdTypeTotalsMap[checkListItemProd.productId] = (BigDecimal)( tempProdTypeTotalsMap[checkListItemProd.productId]).add(checkListItemProd.quantity);
		 
		 routeProductTotalsMap["CASH_CARD"].putAll(tempProdTypeTotalsMap);
	}
	 tempProductTotalsMap.clear();
	 tempGrandTotalsMap.clear();
	 tempRouteProductTotalsMap.clear();
	 tempProdTypeTotalsMap.clear();
	 /*============= END==========*/
	 
	
	if (tempOrderId != checkListItemProd.orderId)  {
		tempOrderId = checkListItemProd.orderId;
		lastChangeSubProdMap = [:];
		checkListReportList.add(lastChangeSubProdMap);
		lastChangeSubProdList.add(lastChangeSubProdMap);
		productList.each{ product ->
			lastChangeSubProdMap[product.productId] = '';
		}
		lastChangeSubProdMap["boothId"] = checkListItemProd.originFacilityId;
		lastChangeSubProdMap["supplyType"] = supplyTypeMap[checkListItemProd.productSubscriptionTypeId];
		lastChangeSubProdMap["routeId"] = checkListItemProd.parentFacilityId;
		
		Map<String, Object> boothPayments = ByProductNetworkServices.getBoothPayments(delegator, dctx.getDispatcher(), userLogin,
			UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"), null, checkListItemProd.originFacilityId ,null ,Boolean.FALSE);
		Map<String, Object> currentBoothPayments = ByProductNetworkServices.getBoothReceivablePayments(delegator, dctx.getDispatcher(), userLogin,
			UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"), null, checkListItemProd.originFacilityId ,null ,Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
		Map boothTotalDues = FastMap.newInstance();
		boothTotalDues["totalDue"]=BigDecimal.ZERO;
		boothTotalDues["grandTotal"]=BigDecimal.ZERO;
		List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
		if (UtilValidate.isNotEmpty(boothPaymentsList)) {
			 boothTotalDues = (Map)boothPaymentsList.get(0);
		}
		Map currentBoothTotalDues = FastMap.newInstance();
		currentBoothTotalDues["grandTotal"]=BigDecimal.ZERO;
		List currentBoothPaymentsList = (List) currentBoothPayments.get("boothPaymentsList");
		if (UtilValidate.isNotEmpty(currentBoothPaymentsList)) {
			 currentBoothTotalDues = (Map)currentBoothPaymentsList.get(0);
		}
		lastChangeSubProdMap["PREV_DUE"] = ((BigDecimal)boothTotalDues.getAt("totalDue")).subtract((BigDecimal)boothTotalDues.getAt("grandTotal"));
		lastChangeSubProdMap["TODAY_DUE"] = (BigDecimal)currentBoothTotalDues.getAt("grandTotal");
	}
		
	lastChangeSubProdMap[checkListItemProd.productId] = (checkListItemProd.quantity).setScale(1, rounding);
	if((i == checkListItemList.size()-1)){
		scheduleListReportMap.putAt(tempBoothId, lastChangeSubProdList);
		Map tempMap1 = [:];
		tempMap1.putAll(routeProductTotalsMap);
		routeWiseProdTotals.putAt(tempRouteId, tempMap1);
	}
}
context.routeWiseProdTotals = routeWiseProdTotals;
context.productTypeTotalsMap = productTypeTotalsMap;
context.checkListReportList = checkListReportList;
context.scheduleListReportMap = scheduleListReportMap;


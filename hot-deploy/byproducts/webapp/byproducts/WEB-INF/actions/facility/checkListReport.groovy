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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
productList = ByProductNetworkServices.getAllLmsAndByProdProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.productList = productList;

allChanges= false;
if (parameters.all == 'Y') {
	allChanges = true;
}
checkListType= parameters.checkListType;
context.checkListType = checkListType;
List exprList = [];
List checkListReportList = [];
lastChangeSubProdMap = [:];
List milkCardTypeList =[];
List finalProductList =[];
dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);
supplyTypeMap =[:];
supplyTypeMap["SPECIAL_ORDER"] = "SO";
supplyTypeMap["CASH"] = "CS";
supplyTypeMap["CASH_FS"] = "FS";
supplyTypeMap["CARD"] = "CD";
supplyTypeMap["CREDIT"] = "CR";

shipmentTypeMap =[:];
shipmentTypeMap["AM_SHIPMENT"] = "AM Shipment";
shipmentTypeMap["PM_SHIPMENT"] = "PM Shipment";
shipmentTypeMap["AM_SHIPMENT_SUPPL"] = "AM";
shipmentTypeMap["PM_SHIPMENT_SUPPL"] = "PM";
if(("gatepass".equals(checkListType))){
	exprList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN , UtilMisc.toList("AM_SHIPMENT_SUPPL","PM_SHIPMENT_SUPPL")));
/*	List shipmentIds = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),"AM_SHIPMENT_SUPPL");
	shipmentIds.addAll(NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp(dayBegin,-1), "yyyy-MM-dd HH:mm:ss"),"PM_SHIPMENT_SUPPL"));
	exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN ,shipmentIds));*/
}
if(("trucksheetcorrection".equals(checkListType))){
	exprList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS , "AM_SHIPMENT"));
	/*List shipmentIds = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),"AM_SHIPMENT");
	exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN ,shipmentIds));*/
}
if("cardsale".equals(checkListType)){
	exprList.add(EntityCondition.makeCondition([
		EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
		EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
	   ], EntityOperator.OR));
	exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "ORDER_CREATED"));
	if (!allChanges) {
		exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS, userLogin.userLoginId));
	}
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	checkListItemList = delegator.findList("MilkCardOrderAndItemAndFacility", condition, null, ["lastModifiedDate"], null, false);
}

if(("gatepass".equals(checkListType)) || ("trucksheetcorrection".equals(checkListType))){
	exprList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS  ,"SALES_ORDER"));
	exprList.add(EntityCondition.makeCondition("shipmentStatusId", EntityOperator.EQUALS , "GENERATED"));
	/*exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO  ,dayBegin));
	exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));*/
	exprList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	exprList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
	exprList.add(EntityCondition.makeCondition("changeByUserLoginId", EntityOperator.EQUALS, userLogin.userLoginId));	
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	checkListItemList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, ["changeDatetime"], null, false);
}

if(("changeindent".equals(checkListType))){
	exprList.add(EntityCondition.makeCondition([
		EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
		EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
	   ], EntityOperator.OR));
   if (!allChanges) {
	   exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS, userLogin.userLoginId));
   }
   condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);   
   
   checkListItemList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, ["lastModifiedDate"], null, false);
   checkListItemList = EntityUtil.filterByDate(checkListItemList,UtilDateTime.getNextDayStart(dayBegin));
  
}

if (checkListItemList.size() > 0) {
	
	if("cardsale".equals(checkListType)){
		milkCardTypeList = delegator.findList("MilkCardType", EntityCondition.makeCondition([isLMS:'Y']), null, null, null, false);		
		milkCardTypeList.each{ milkCardType ->
			lastChangeSubProdMap[milkCardType.milkCardTypeId] = '';
		}
		context.milkCardTypeList = milkCardTypeList;
		
	}else{
		productList.each{ product ->
			lastChangeSubProdMap[product.productId] = '';			
		}		
	}
	checkListReportList.add(lastChangeSubProdMap);
}	

tempOrderId = "";
if(("gatepass".equals(checkListType)) || ("trucksheetcorrection".equals(checkListType))){
	for (int i=0; i < checkListItemList.size(); i++) {
		checkListItemProd = checkListItemList.get(i);
		
		if (tempOrderId == "") {
			tempOrderId = checkListItemProd.orderId;
			lastChangeSubProdMap["boothId"] = checkListItemProd.originFacilityId;
			lastChangeSubProdMap["supplyType"] = supplyTypeMap[checkListItemProd.productSubscriptionTypeId];
			lastChangeSubProdMap["lastModifiedBy"] = checkListItemProd.changeByUserLoginId;
			lastChangeSubProdMap["shipmentTypeId"] = shipmentTypeMap[checkListItemProd.shipmentTypeId];
			lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(checkListItemProd.changeDatetime, "HH:mm:ss");
			lastChangeSubProdMap["routeId"] = checkListItemProd.parentFacilityId;	
		}
		if (tempOrderId != checkListItemProd.orderId)  {
			tempOrderId = checkListItemProd.orderId;
			lastChangeSubProdMap = [:];
			checkListReportList.add(lastChangeSubProdMap);
			productList.each{ product ->
				lastChangeSubProdMap[product.productId] = '';
			}
			lastChangeSubProdMap["boothId"] = checkListItemProd.originFacilityId;		
			lastChangeSubProdMap["supplyType"] = supplyTypeMap[checkListItemProd.productSubscriptionTypeId];
			lastChangeSubProdMap["lastModifiedBy"] = checkListItemProd.changeByUserLoginId;
			lastChangeSubProdMap["shipmentTypeId"] = shipmentTypeMap[checkListItemProd.shipmentTypeId];
			lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(checkListItemProd.changeDatetime, "HH:mm:ss");			
			lastChangeSubProdMap["routeId"] = checkListItemProd.parentFacilityId;	
		}
			
		lastChangeSubProdMap[checkListItemProd.productId] = (checkListItemProd.quantity).intValue();
		
	}	
}

if(("cardsale".equals(checkListType))){
	for (int i=0; i < checkListItemList.size(); i++) {
		checkListItemProd = checkListItemList.get(i);
		
		if (tempOrderId == "") {
			tempOrderId = checkListItemProd.orderId;
			lastChangeSubProdMap["boothId"] = checkListItemProd.boothId;
			lastChangeSubProdMap["supplyType"] = "CARD";
			lastChangeSubProdMap["lastModifiedBy"] = checkListItemProd.createdByUserLogin;
			lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(checkListItemProd.lastModifiedDate, "HH:mm:ss");
			lastChangeSubProdMap["routeId"] = checkListItemProd.parentFacilityId;
		}
		if (tempOrderId != checkListItemProd.orderId)  {
			tempOrderId = checkListItemProd.orderId;
			lastChangeSubProdMap = [:];
			checkListReportList.add(lastChangeSubProdMap);
			milkCardTypeList.each{ milkCardType ->
			lastChangeSubProdMap[milkCardType.milkCardTypeId] = '';
			}
			lastChangeSubProdMap["boothId"] = checkListItemProd.boothId;	
			lastChangeSubProdMap["supplyType"] = "CARD";	
			lastChangeSubProdMap["lastModifiedBy"] = checkListItemProd.createdByUserLogin;
			lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(checkListItemProd.lastModifiedDate, "HH:mm:ss");
			lastChangeSubProdMap["routeId"] = checkListItemProd.parentFacilityId;

		}	
		lastChangeSubProdMap[checkListItemProd.milkCardTypeId] = (checkListItemProd.quantity).intValue();		
	}
	
}
tempSupplyType ="";
tempSequenceNum ="";
if(("changeindent".equals(checkListType))){
	for (int i=0; i < checkListItemList.size(); i++) {
		checkListItemProd = checkListItemList.get(i);
		
		if (tempOrderId == "") {
			tempOrderId = checkListItemProd.subscriptionId;
			tempSupplyType = checkListItemProd.productSubscriptionTypeId;
			tempSequenceNum = checkListItemProd.sequenceNum;
			lastChangeSubProdMap["boothId"] = checkListItemProd.facilityId;
			lastChangeSubProdMap["supplyType"] = supplyTypeMap[checkListItemProd.productSubscriptionTypeId];
			lastChangeSubProdMap["lastModifiedBy"] = checkListItemProd.lastModifiedByUserLogin;
			lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(checkListItemProd.lastModifiedDate, "HH:mm:ss");
			lastChangeSubProdMap["routeId"] = checkListItemProd.sequenceNum;
		}
		if ((tempOrderId != checkListItemProd.subscriptionId) || (tempSupplyType != checkListItemProd.productSubscriptionTypeId) || (tempSequenceNum != checkListItemProd.sequenceNum))  {
			tempOrderId = checkListItemProd.subscriptionId;
			tempSupplyType = checkListItemProd.productSubscriptionTypeId;
			tempSequenceNum = checkListItemProd.sequenceNum;
			lastChangeSubProdMap = [:];
			checkListReportList.add(lastChangeSubProdMap);
			productList.each{ product ->
				lastChangeSubProdMap[product.productId] = '';
			}
			lastChangeSubProdMap["boothId"] = checkListItemProd.facilityId;			
			lastChangeSubProdMap["supplyType"] = supplyTypeMap[checkListItemProd.productSubscriptionTypeId];
			lastChangeSubProdMap["lastModifiedBy"] = checkListItemProd.lastModifiedByUserLogin;
			lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(checkListItemProd.lastModifiedDate, "HH:mm:ss");
			lastChangeSubProdMap["routeId"] = checkListItemProd.sequenceNum;
		}
			
		     lastChangeSubProdMap[checkListItemProd.productId] = (checkListItemProd.quantity).intValue();
			 if((checkListItemProd.quantity).intValue()>0){
			 finalProductList.addAll(checkListItemProd.productId);
			 context.finalProductList = finalProductList.unique();
			}
		
		
	}
}

context.checkListReportList = checkListReportList;

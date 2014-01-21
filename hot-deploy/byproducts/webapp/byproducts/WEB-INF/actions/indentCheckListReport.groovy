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

checkListItemList = [];
productList = [];
allChanges= false;
if (parameters.all == 'Y') {
	allChanges = true;
}
checkListType= context.checkListType;
context.checkListType = checkListType;
List exprList = [];
List checkListReportList = [];
lastChangeSubProdMap = [:];
dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);
entrySize = 0;
checkListReportList = [];
if(("indentEntry".equals(checkListType))){
   exprList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
   exprList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
   if(allChanges == false){
	   exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS, userLogin.userLoginId));
   }
   condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
   checkListItemList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, ["-lastModifiedDate"], null, false);
   entryList = EntityUtil.getFieldListFromEntityList(checkListItemList, "subscriptionId", true)
   entrySize = entryList.size();
   subProdList = EntityUtil.getFieldListFromEntityList(checkListItemList, "productId", true);
   context.productList = subProdList;
   if(subProdList){
	   subProdList.each{ product ->
		   lastChangeSubProdMap[product] = '';
	   }
	   checkListReportList.add(lastChangeSubProdMap);
   }
   tempOrderId = "";
   if(checkListItemList){
	   checkListItemList.each{eachItem ->
		   if (tempOrderId == "") {
			   tempOrderId = eachItem.subscriptionId;
			   tempSupplyType = eachItem.productSubscriptionTypeId;
			   lastChangeSubProdMap["boothId"] = eachItem.facilityId;
			   lastChangeSubProdMap["lastModifiedBy"] = eachItem.lastModifiedByUserLogin;
			   lastChangeSubProdMap["supplyDate"] = UtilDateTime.toDateString(eachItem.fromDate, "dd/MM/yyyy");
			   lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(eachItem.lastModifiedDate, "HH:mm:ss");
			   lastChangeSubProdMap["routeId"] = eachItem.sequenceNum;
			   
		   }
		   if (tempOrderId != eachItem.subscriptionId)  {
			   tempOrderId = eachItem.subscriptionId;
			   tempSupplyType = eachItem.productSubscriptionTypeId;
			   lastChangeSubProdMap = [:];
			   checkListReportList.add(lastChangeSubProdMap);
			   subProdList.each{ product ->
				   lastChangeSubProdMap[product] = '';
			   }
			   lastChangeSubProdMap["boothId"] = eachItem.facilityId;
			   lastChangeSubProdMap["lastModifiedBy"] = eachItem.lastModifiedByUserLogin;
			   lastChangeSubProdMap["supplyDate"] = UtilDateTime.toDateString(eachItem.fromDate, "dd/MM/yyyy");
			   lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(eachItem.lastModifiedDate, "HH:mm:ss");
			   lastChangeSubProdMap["routeId"] = eachItem.sequenceNum;
		   }
			   
		   lastChangeSubProdMap[eachItem.productId] = (eachItem.quantity).intValue();
	   }
   }
   
}
if(("parlorEntry".equals(checkListType))){
   exprList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
   exprList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
   exprList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
   exprList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "BYPRODUCTS_PRSALE"));
   if(allChanges == false){
	   exprList.add(EntityCondition.makeCondition("changeByUserLoginId", EntityOperator.EQUALS, userLogin.userLoginId));
   }
   condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
   OrderEntryList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, null, null, false);
   
   entryList = EntityUtil.getFieldListFromEntityList(OrderEntryList, "orderId", true)
   entrySize = entryList.size();
   
   subProdList = EntityUtil.getFieldListFromEntityList(OrderEntryList, "productId", true);
   context.productList = subProdList;
   
   if(subProdList){
	   subProdList.each{ product ->
		   lastChangeSubProdMap[product] = '';
	   }
	   checkListReportList.add(lastChangeSubProdMap);
   }
   tempOrderId = "";
   if(OrderEntryList){
	   OrderEntryList.each{eachItem ->
		   if (tempOrderId == "") {
			   tempOrderId = eachItem.orderId;
			   lastChangeSubProdMap["boothId"] = eachItem.originFacilityId;
			   lastChangeSubProdMap["lastModifiedBy"] = eachItem.changeByUserLoginId;
			   lastChangeSubProdMap["supplyDate"] = UtilDateTime.toDateString(eachItem.estimatedDeliveryDate, "dd/MM/yyyy");
			   lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(eachItem.changeDatetime, "HH:mm:ss");
			   lastChangeSubProdMap["routeId"] = "";
			   
		   }
		   if (tempOrderId != eachItem.orderId)  {
			   tempOrderId = eachItem.orderId;
			   lastChangeSubProdMap = [:];
			   checkListReportList.add(lastChangeSubProdMap);
			   subProdList.each{ product ->
				   lastChangeSubProdMap[product] = '';
			   }
			   lastChangeSubProdMap["boothId"] = eachItem.originFacilityId;
			   lastChangeSubProdMap["lastModifiedBy"] = eachItem.changeByUserLoginId;
			   lastChangeSubProdMap["supplyDate"] = UtilDateTime.toDateString(eachItem.estimatedDeliveryDate, "dd/MM/yyyy");
			   lastChangeSubProdMap["lastModifiedDate"] = UtilDateTime.toDateString(eachItem.changeDatetime, "HH:mm:ss");
			   lastChangeSubProdMap["routeId"] = "";
		   }
			   
		   lastChangeSubProdMap[eachItem.productId] = (eachItem.quantity).intValue();
	   }
   }
}
context.checkListReportList = checkListReportList;
context.entrySize = entrySize;
context.supplyDate = UtilDateTime.getNextDayStart(UtilDateTime.nowTimestamp());


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
import  org.ofbiz.network.NetworkServices;

productList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
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
dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);


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

if (checkListItemList.size() > 0) {
	
		milkCardTypeList = delegator.findList("MilkCardType", EntityCondition.makeCondition([isLMS:'Y']), null, null, null, false);		
		milkCardTypeList.each{ milkCardType ->

			lastChangeSubProdMap[milkCardType.milkCardTypeId] = '';
		}
		context.milkCardTypeList = milkCardTypeList;
	checkListReportList.add(lastChangeSubProdMap);
}	

tempOrderId = "";

	for (int i=0; i < checkListItemList.size(); i++) {
		checkListItemProd = checkListItemList.get(i);
		if (tempOrderId == "") {
			tempOrderId = checkListItemProd.orderId;
			lastChangeSubProdMap["boothId"] = checkListItemProd.boothId;
			lastChangeSubProdMap["supplyType"] = "CARD";
			lastChangeSubProdMap["lastModifiedBy"] = checkListItemProd.createdByUserLogin;
			lastChangeSubProdMap["lastModifiedDate"] = checkListItemProd.lastModifiedDate;
			lastChangeSubProdMap["routeId"] = checkListItemProd.parentFacilityId;
			lastChangeSubProdMap["grandTotal"] = checkListItemProd.grandTotal;
			lastChangeSubProdMap["bookNumber"] = checkListItemProd.bookNumber;
			lastChangeSubProdMap["paymentTypeId"] = checkListItemProd.paymentTypeId;
			lastChangeSubProdMap["saleLocation"] = checkListItemProd.saleLocation;
			lastChangeSubProdMap["counterNumber"] = checkListItemProd.counterNumber;
			milkCardType = delegator.findOne("MilkCardType", [milkCardTypeId : checkListItemProd.milkCardTypeId], false);
			lastChangeSubProdMap["milkCardTypeId"] = milkCardType.description;
			lastChangeSubProdMap["quantity"] = (checkListItemProd.quantity);
			
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
			lastChangeSubProdMap["lastModifiedDate"] = checkListItemProd.lastModifiedDate;
			lastChangeSubProdMap["routeId"] = checkListItemProd.parentFacilityId;
			lastChangeSubProdMap["grandTotal"] = checkListItemProd.grandTotal;
			lastChangeSubProdMap["bookNumber"] = checkListItemProd.bookNumber;
			lastChangeSubProdMap["paymentTypeId"] = checkListItemProd.paymentTypeId;
			lastChangeSubProdMap["counterNumber"] = checkListItemProd.counterNumber;
			lastChangeSubProdMap["saleLocation"] = checkListItemProd.saleLocation;
			milkCardType = delegator.findOne("MilkCardType", [milkCardTypeId : checkListItemProd.milkCardTypeId], false);
			lastChangeSubProdMap["milkCardTypeId"] = milkCardType.description;
			lastChangeSubProdMap["quantity"] = (checkListItemProd.quantity);
		}	
		lastChangeSubProdMap[checkListItemProd.milkCardTypeId] = (checkListItemProd.quantity);		
	}

context.checkListReportList = checkListReportList;
//Debug.logInfo("checkListReportList=========================>"+checkListReportList,"");

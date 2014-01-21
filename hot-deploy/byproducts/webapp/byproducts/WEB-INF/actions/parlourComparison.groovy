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

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.network.NetworkServices;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import java.util.SortedMap;

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
totalQuantity = 0;
totalRevenue = 0;
dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		context.froDate = parameters.fromDate;
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	else {
		froDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		context.froDate = froDate
		fromDate = froDate;
	}
	if (parameters.thruDate) {
		context.toDate = parameters.thruDate;
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
	else {
		context.toDate = UtilDateTime.nowDate();
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}


dctx = dispatcher.getDispatchContext();
conditionList = [];
conditionList.clear();
shipments = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
/*shipments = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, fromDate, thruDate, "BYPRODUCTS_PRSALE");*/
/*conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO , fromDate));
conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , thruDate));*/
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipments));
if(UtilValidate.isNotEmpty(parameters.facilityId)){
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, parameters.facilityId));
	context.facilityId = parameters.facilityId;
}
if(UtilValidate.isNotEmpty(parameters.productId)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
	context.productId = parameters.productId;
}
else {
	productList = ByProductNetworkServices.getByProductProducts(dctx, context);
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(productList, "productId", true)));
}
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "SHOPPEE"));
conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
/*conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));*/
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderItemList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, null, null, false);
SortedMap parlourMap = new TreeMap();
orderItemList.each{eachItem ->
	facilityId = eachItem.getAt("originFacilityId").trim().toUpperCase();
	quantity = eachItem.getAt("quantity");
	unitPrice = eachItem.getAt("unitPrice");
	amount = quantity * unitPrice;
	if(parlourMap.containsKey(facilityId)){
		totalAmount = amount + parlourMap.get(facilityId);
		parlourMap.put(facilityId, totalAmount);
	}
	else{
		parlourMap.put(facilityId, amount);
	}
}

Debug.logInfo("parlourMap="+ parlourMap, "");

JSONArray parlourDataListJSON = new JSONArray();
JSONArray labelsJSON = new JSONArray();
int i = 1;
Iterator mapIter = parlourMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	parlourId =entry.getKey();
	JSONArray dayList= new JSONArray();
	dayList.add(i);
	dayList.add(entry.getValue());
	parlourDataListJSON.add(dayList);
	facility = delegator.findOne("Facility",[ facilityId : parlourId ], false);
	JSONArray labelsList= new JSONArray();
	labelsList.add(i);
	label = facility.facilityName  + " [" + parlourId + "]";
	labelsList.add(label);
	labelsJSON.add(labelsList);
	++i;
}
//Debug.logInfo("parlourDataListJSON="+ parlourDataListJSON, "");
//Debug.logInfo("labelsJSON="+ labelsJSON, "");
context.parlourDataListJSON = parlourDataListJSON;
context.labelsJSON = labelsJSON;

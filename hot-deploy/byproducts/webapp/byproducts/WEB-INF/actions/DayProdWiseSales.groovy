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

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.network.NetworkServices;

conditionList = [];
salesDate = parameters.salesDate;
if (UtilValidate.isEmpty(salesDate) || salesDate == "NaN") {
	salesDate = UtilDateTime.nowTimestamp();
}
else {
	salesDate = UtilDateTime.getTimestamp(salesDate);
}
context.salesDate = salesDate;
fieldsToSelectIndItem = ["shipmentId","shipmentItemSeqId"] as Set;
dayBegin = UtilDateTime.getDayStart(salesDate);
dayEnd = UtilDateTime.getDayEnd(salesDate);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO , dayBegin));
conditionList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO , dayEnd));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
shippedItemsList = delegator.findList("OrderShipment", condition, fieldsToSelectIndItem, null, null, false);
fieldsSelect = ["productId","quantity"] as Set;
productQuantMap = [:];
if(shippedItemsList){
	shippedItemsList.each{eachItem ->
		shipmentId = eachItem.getAt("shipmentId");
		shipmentItemSeqId = eachItem.getAt("shipmentItemSeqId");
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		conditionList.add(EntityCondition.makeCondition("shipmentItemSeqId", EntityOperator.EQUALS, shipmentItemSeqId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		productQuantityList = delegator.findList("ShipmentAndItem", condition, fieldsSelect, null, null, false);
		if(productQuantityList){
			productId = productQuantityList.getAt(0).getAt("productId");
			quantity = productQuantityList.getAt(0).getAt("quantity");
			if(productQuantMap.containsKey(productId)){
				quant = productQuantMap.getAt(productId);
				totalQuantity = quantity + quant;
				productQuantMap.putAt(productId, totalQuantity);
			}else{
				productQuantMap.putAt(productId, quantity);
			}
		}
	}
}
productReportList = [];
for(Map.Entry entry : productQuantMap.entrySet()){
	dataMap = [:];
	prodId = entry.getKey();
	total = entry.getValue();
	productDetails = delegator.findOne("Product",[productId :prodId ], false);
	productName=productDetails.productName;
	dataMap.putAt("name", productName);
	dataMap.putAt("totalQuantity", total);
	productReportList.add(dataMap);
}
context.productReportList = productReportList;
context.prodSize = productReportList.size();
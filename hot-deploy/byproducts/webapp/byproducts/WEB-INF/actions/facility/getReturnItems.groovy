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
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.order.order.OrderReadHelper;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;

returnId = parameters.returnId;
if(returnId){
	returnHeader = delegator.findByPrimaryKey("ReturnHeader", [returnId : returnId]);
	context.returnHeader = returnHeader;
	retuynHeaderTypeId = returnHeader.returnHeaderTypeId;
	shipmentId = returnHeader.shipmentId;
	
	shipment = delegator.findByPrimaryKey("Shipment", [shipmentId : shipmentId]);
	
	context.routeId = shipment.routeId;
	context.tripId = shipment.tripNum;
	context.shipmentDate = shipment.estimatedShipDate;
	
	returnItems = delegator.findByAnd("ReturnItem", [returnId : returnId]);
	productIds = EntityUtil.getFieldListFromEntityList(returnItems, "productId", true);
	productNames = [:];
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), UtilMisc.toSet("productId", "productName"), null, null, false);
	products.each{ eachProd ->
		productNames.put(eachProd.productId, eachProd.productName);
	}
	orderReturnList = [];
	JSONArray dataJSONList = new JSONArray();
	returnItems.each{ eachItem ->
		JSONObject tempObj = new JSONObject();
		tempObj.put("cProductId", eachItem.productId);
		tempObj.put("cProductName", productNames.get(eachItem.productId));
		tempObj.put("cQuantity", "");
		tempObj.put("returnQuantity", eachItem.returnQuantity);
		dataJSONList.add(tempObj);
	}
	
	context.dataJSON = dataJSONList.toString();
	context.returnType = "sales";
	
}
JSONArray returnReasonJSON = new JSONArray();
JSONObject returnReasonIdsJSON = new JSONObject();
//resonListIds=UtilMisc.toList("RTN_CNTR_FAULT","RTN_DAIRY_FAULT","RTN_INDNT_FAULT","RTN_TRSPT_FAULT")
returnResonList = delegator.findList("ReturnReason", EntityCondition.makeCondition("parentReasonId", EntityOperator.EQUALS, "PROD_RTN_FAULT"), null, null, null, false);
returnResonList.each{eachReason->
	JSONObject newReasonObj = new JSONObject();
	newReasonObj.put("value",eachReason.returnReasonId);
	newReasonObj.put("label",eachReason.description);
	returnReasonIdsJSON.put(eachReason.description, eachReason.returnReasonId);
	
	returnReasonJSON.add(newReasonObj);
	
}
context.returnReasonJSON = returnReasonJSON;
context.returnReasonIdsJSON = returnReasonIdsJSON;

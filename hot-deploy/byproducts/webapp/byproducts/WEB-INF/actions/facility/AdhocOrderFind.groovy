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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.service.ServiceUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;








dctx = dispatcher.getDispatchContext();

GenericValue userLogin = (GenericValue) context.get("userLogin");

String orderId = parameters.orderId;
String statusId = parameters.statusId;
String changeFlag=parameters.changeFlag;
String originFacilityId = parameters.originFacilityId;
displayGrid = true;
dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());

boothOrderItemMap=[:];
JSONArray dataJSONList= new JSONArray();
	 boothOrderItemsList = delegator.findList("OrderHeaderAndItems", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	 boothOrderItemsList.eachWithIndex { boothOrderItem, idx ->
		 dayOfMonth = UtilDateTime.getDayOfMonth(boothOrderItem.estimatedDeliveryDate, timeZone, locale);
		 typeAndCount =[:];
		 amount = ((boothOrderItem.quantity) * (boothOrderItem.unitPrice));
		 litrs=(boothOrderItem.quantity);
		 typeAndCount[boothOrderItem.productId] = boothOrderItem.productId;
		 typeAndCount["QTY"] = litrs;
		 typeAndCount["TOTALAMOUNT"] = amount;
		
		 boothOrderItemMap[boothOrderItem.productId]=typeAndCount;
		 
		 
		//for Correction this map will Use
			 JSONObject quotaObj = new JSONObject();
				 quotaObj.put("id",idx+1);
				 quotaObj.put("title", "");
				 quotaObj.put("productId", boothOrderItem.productId);
				 quotaObj.put("lastQuantity", boothOrderItem.quantity);
				 quotaObj.put("quantity", boothOrderItem.quantity);
				 dataJSONList.add(quotaObj);
}
	 shipDate=parameters.estimatedDeliveryDate;
	 
	 def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 try {
		 shipDateTime = new java.sql.Timestamp(sdf.parse(shipDate+" 00:00:00").getTime());
		 context.put("orderDate", shipDateTime);
		 dayBegin = UtilDateTime.getDayStart(shipDateTime);
		 dayEnd = UtilDateTime.getDayEnd(shipDateTime);
	 } catch (ParseException e) {
		 Debug.logError(e, "Cannot parse date string: "+shipDate, "");
		
	 }
	 
	 
if(UtilValidate.isNotEmpty(changeFlag)&&("AdhocOrderCorrection"==changeFlag)){
	if (dataJSONList.size() > 0) {
		context.dataJSON = dataJSONList.toString();
		Debug.logInfo("dataJSONList="+dataJSONList.toString(),"");
	}
	prodList = ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
	JSONArray productItemsJSON = new JSONArray();
	JSONObject productIdLabelJSON = new JSONObject();
	context.productList = prodList;
	prodList.each{eachItem ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachItem.productId);
		newObj.put("label",eachItem.productId + " [" + eachItem.productName + "]");
		productItemsJSON.add(newObj);
		productIdLabelJSON.put(eachItem.productId, eachItem.productId + " [" + eachItem.productName + "]");
	}
	productPrices = [];

	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	inMap = [:];
	inMap.productStoreId = productStoreId;
	result = ByProductServices.getProdStoreProducts(dctx, inMap)
	productsList = result.productIdsList;

	productsList.each{ eachProd ->
		prodPrice = [:];
		priceContext = [:];
		priceResult = [:];
		Map<String, Object> priceResult;
		priceContext.put("userLogin", userLogin);
		priceContext.put("productStoreId", productStoreId);
		priceContext.put("productId", eachProd);
		priceContext.put("priceDate", dayBegin);
		priceContext.put("facilityId", originFacilityId);
		priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);
		if(!ServiceUtil.isError(priceResult)){
			if (priceResult) {
				unitCost = (BigDecimal)priceResult.get("basicPrice");
				taxList = priceResult.get("taxList");
				totalAmount = BigDecimal.ZERO;
				if(taxList){
					taxList.each{eachItem ->
						taxAmount = (BigDecimal)eachItem.get("amount");
						totalAmount = totalAmount.add(taxAmount);
					}
				}
				prodPrice.productId = eachProd;
				prodPrice.unitCost = (unitCost.add(totalAmount));
				productPrices.add(prodPrice);
			}
		}
	}
	JSONObject productCostJSON = new JSONObject();
	productPrices.each{eachProdPrice ->
		productCostJSON.put(eachProdPrice.productId,eachProdPrice.unitCost);
	}


	context.productItemsJSON = productItemsJSON;
	context.productIdLabelJSON = productIdLabelJSON;
	context.productCostJSON = productCostJSON;
	context.screenFlag = "DSCorrection";
	if(displayGrid){
		context.partyCode = originFacilityId;
	}
	facility = delegator.findOne("Facility",[facilityId : originFacilityId], false);
	if (facility == null) {
		Debug.logInfo("Booth '" + boothId + "' does not exist!","");
		context.errorMessage = "Booth '" + boothId + "' does not exist!";
		displayGrid = false;
		return;
	}else{
		routeId=facility.get("parentFacilityId");
		parameters.routeId=routeId;
	}
	context.booth = facility;
	parameters.boothId = originFacilityId;
}

context.put("boothOrderItemMap", boothOrderItemMap);



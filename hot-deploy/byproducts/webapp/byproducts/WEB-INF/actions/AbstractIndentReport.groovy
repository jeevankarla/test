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
	import  org.ofbiz.network.NetworkServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	/*rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	context.rounding = rounding;*/
	
	
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	
	List productList = [];
	
	/*if(parameters.reportTypeFlag){
		reportTypeFlag = parameters.reportTypeFlag;
		if(reportTypeFlag == "UNION"){
			productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("UNION_PRODUCTS");
		}
		else if(reportTypeFlag == "DAIRY"){
			productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("DAIRY_PRODUCTS");
		}
	}
	else{*/
		productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
	//}
	
	effectiveDate = null;
	effectiveDateStr = parameters.supplyDate;
	
	if (UtilValidate.isEmpty(effectiveDateStr)) {
		effectiveDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			effectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(effectiveDateStr));
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
		}
	}
	dayBegin = UtilDateTime.getDayStart(effectiveDate);
	dayEnd = UtilDateTime.getDayEnd(effectiveDate);
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	conditionList=[];
	grandTotalMap =[:];
	routesList = [];
	routeMap = [:];
	routeDetailList = [];
	
	conditionList=[];
	
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "BOOTH"));
	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.IN, ["AM","PM"]));
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN ,productList));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId", "quantity","productSubscriptionTypeId"] as Set;
	subscriptionsItemsList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["productId"], null, false);
	
	if(subscriptionsItemsList){
		subscriptionsItemsList.each { eachItem ->
			productId = eachItem.get("productId");
			product = delegator.findOne("Product", UtilMisc.toMap("productId" : productId), false);
			BigDecimal indentQty = (BigDecimal) eachItem.get("quantity");
			productSubscriptionTypeId = eachItem.get("productSubscriptionTypeId");
			
			detailMap = [:];
			detailMap["productId"] = productId;
			detailMap["productName"] = product.get("brandName");
			
			if(productSubscriptionTypeId == "SPECIAL_ORDER"){
				detailMap["otherQuantity"] = BigDecimal.ZERO;
				detailMap["splQuantity"] = indentQty;
			}
			else{
				detailMap["otherQuantity"] = indentQty;
				detailMap["splQuantity"] = BigDecimal.ZERO;
			}
			tempTotalsMap = [:];
			tempTotalsMap.putAll(detailMap);
			
			if(UtilValidate.isEmpty(grandTotalMap[productId])){
				grandTotalMap[productId] = tempTotalsMap;
			}
			else{
				updateDetailMap = grandTotalMap[productId];
				if(productSubscriptionTypeId == "SPECIAL_ORDER"){
					updateDetailMap["splQuantity"] += indentQty;
				}
				else{
					updateDetailMap["otherQuantity"] += indentQty;
				}
				tempUpdateMap = [:];
				tempUpdateMap.putAll(updateDetailMap);
				grandTotalMap.put(productId, tempUpdateMap);
			}
		}
	}
	context.grandTotalMap = grandTotalMap;
	context.indentDate = UtilDateTime.toDateString(effectiveDate, "dd.MM.yyyy");
	return "success";
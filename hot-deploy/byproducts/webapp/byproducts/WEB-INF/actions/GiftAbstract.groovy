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
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	import in.vasista.vbiz.byproducts.ByProductReportServices;
	
	customTimePeriod =delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);
	
	conditionList=[];
	List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, dayBegin, dayEnd, "BYPRODUCTS");
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN, UtilMisc.toList("BYPROD_GIFT","REPLACEMENT_BYPROD")));
	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
	orderCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId","originFacilityId", "quantity","unitPrice","estimatedDeliveryDate"] as Set;
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, fieldsToSelect , ["productId"], null, false);
	prodList = EntityUtil.getFieldListFromEntityList(boothOrderItemsList, "productId",true);
	context.put("prodList", prodList);
	/*conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
	conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
	conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "REPLACEMENT_BYPROD"));
	receiptCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId", "quantityAccepted","facilityId"] as Set;
	shipmentReceiptList = delegator.findList("ShipmentReceiptAndItem", receiptCondition,  fieldsToSelect, null, null, false);*/
	
	productsPrice = ByProductReportServices.getByProductPricesForPartyClassification(dispatcher.getDispatchContext(), UtilMisc.toMap("userLogin", userLogin, "partyClassificationId", "PM_RC_G")).get("productsPrice");
	context.put("productsPrice", productsPrice);
	prodTotalMap = [:];
	
	for(prodValue in boothOrderItemsList){
		if(prodTotalMap.containsKey(prodValue.productId)){
			qty = prodTotalMap.get(prodValue.productId);
			qty = qty+prodValue.quantity;
			prodTotalMap.put(prodValue.productId, qty);
		}else{
			prodTotalMap[prodValue.productId]=prodValue.quantity;
		}
	}
	context.put("prodTotalMap", prodTotalMap);
	
	//Gift supply details 
	totalsMap = [:];
	for(prodValue in boothOrderItemsList){
		facility = delegator.findOne("Facility", [facilityId : prodValue.originFacilityId], false);
		dateWiseMap = [:];
		dateWiseMap["supplyDate"]=prodValue.estimatedDeliveryDate;
		dateWiseMap["partyName"]=facility.facilityName;
		dateWiseMap["quantity"]=prodValue.quantity;
		if(totalsMap.containsKey(prodValue.productId)){
			tempProdList = totalsMap.get(prodValue.productId);
			tempProdList.addAll(dateWiseMap);
			totalsMap.putAt(prodValue.productId, tempProdList);
		}else{
			tempList = [];
			tempList.add(dateWiseMap);
			totalsMap.putAt(prodValue.productId, tempList);
		}
	}
	context.put("totalsMap", totalsMap);
	//Debug.log("=========================================>"+prodTotalMap);

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
import org.ofbiz.product.price.PriceServices;



import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

conditionList = [];
orderList = [];
dctx = dispatcher.getDispatchContext();
shipmentId = null;
shipmentIds = [];
requestedFacilityId = null;
Timestamp estimatedDeliveryDateTime = null;


if(parameters.estimatedShipDate){
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	try {
		estimatedDeliveryDateTime = new java.sql.Timestamp(formatter.parse(parameters.estimatedShipDate).getTime());
		
	} catch (ParseException e) {
	}
}

context.put("estimatedDeliveryDateTime", estimatedDeliveryDateTime);

partyPONumMap = [:];

partyIdentification = delegator.findList("PartyIdentification", EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PO_NUMBER"), null, null, null, false);

partyIdentification.each{eachPO ->
	partyPONumMap.put(eachPO.partyId, eachPO.idValue);
}
context.partyPONumMap = partyPONumMap;



facilityList=[];
if(parameters.facilityId){
	requestedFacilityId = parameters.facilityId;
}
if(parameters.shipmentId){
	if(parameters.shipmentId == "allRoutes"){
		shipments = delegator.findByAnd("Shipment", [estimatedShipDate : estimatedDeliveryDateTime , shipmentTypeId : parameters.shipmentTypeId ],["routeId"]);
		shipmentIds.addAll(EntityUtil.getFieldListFromEntityList(shipments, "shipmentId", false));
	}else{
		shipmentId = parameters.shipmentId;
		shipmentIds.add(shipmentId);
	}
}
if(context.shipmentId){
	shipmentId = context.shipmentId;
	shipmentIds.add(shipmentId);
}
if(parameters.shipmentIds){
	shipmentIds = parameters.shipmentIds;
	shipmentId=shipmentIds[0];
}
if(context.shipmentIds){
	shipmentIds = context.shipmentIds;
	shipmentId=shipmentIds[0];
}

if(parameters.invoiceId){
	invoiceList.add(parameters.invoiceId);
}else{
	shipmentId = parameters.shipmentId;
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "CR_INST"));
	
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["orderId"] as Set;
	orderListRes = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, ["routeId","originFacilityId"], null, false);
	orderList = EntityUtil.getFieldListFromEntityList(orderListRes, "orderId", true);
	facilityList = EntityUtil.getFieldListFromEntityList(orderListRes, "originFacilityId", true);
}
	orderDeatilList = [];
	vatMap = [:];
	reportTitle = [:];
	orderList.each { orderId ->
	   ordersMap = [:];
	   orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
	   ordersMap.orderHeader = orderHeader;
	   
	   temp = [];
		
	   currency = parameters.currency;  // allow the display of the orderHeader in the original currency, the default is to display the orderHeader in the default currency
	   BigDecimal conversionRate = new BigDecimal("1");
	   ZERO = BigDecimal.ZERO;
	   decimals = UtilNumber.getBigDecimalScale("orderHeader.decimals");
	   rounding = UtilNumber.getBigDecimalRoundingMode("orderHeader.rounding");
	   
	   if (orderHeader) {
		   orderItems = orderHeader.getRelatedOrderBy("OrderItem", ["orderItemSeqId"]);
		   orderItemsConv = [];
		   orderItems.each { orderItem ->
				 //orderItem.amount = orderItem.getBigDecimal("amount").multiply(conversionRate);
				 orderItemsConv.add(orderItem);
		   }
		   if(UtilValidate.isNotEmpty(orderItemsConv)){
			   ordersMap.orderItems = orderItemsConv;
		   }
	   
	  }
		 orderDeatilList.add(ordersMap);
	}
	context.OrderDetailsList = orderDeatilList;
	context.invoiceVatMap = vatMap;
	context.reportTitle = reportTitle;
	
	facilityRouteMap=[:];
	subscriptionTypeId="";
	shipmentTypeId=parameters.shipmentTypeId;
	if(parameters.shipmentTypeId=="AM_SHIPMENT"){
		subscriptionTypeId="AM";
	}else if(parameters.shipmentTypeId=="AM_SHIPMENT"){
	   subscriptionTypeId="PM";
	}
	if(UtilValidate.isNotEmpty(facilityList)){
	facilityRouteMap=boothDetailsRes=ByProductNetworkServices.getBoothsRouteMap(delegator, [facilityIdsList:facilityList]).get("boothRouteIdsMap");
	}
		
	
	context.facilityRouteMap=facilityRouteMap;
	return "success";
	
	
	
	
	
	
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
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductServices;
import java.util.regex.*;

effectiveDate = null;
effectiveDateStr = parameters.supplyDate;
routeId = parameters.routeId;
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
productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
conditionList=[];
conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.IN, ["AM","PM"]));
if(parameters.routeId){
	conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, parameters.routeId));
}
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
fieldsToSelect = ["facilityId", "sequenceNum"] as Set;
subDetailList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["sequenceNum","facilityId"], null, false);
routesList = [];
if(subDetailList){
	routesList = EntityUtil.getFieldListFromEntityList(subDetailList,"sequenceNum",true);
	Collections.sort(routesList);
}
routeMap = [:];
shipmentId = null;
if(routesList){
	routesList.each{eachRoute ->
		boothInRoute = EntityUtil.filterByCondition(subDetailList, EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, eachRoute));
		boothInRoute = EntityUtil.getFieldListFromEntityList(boothInRoute,"facilityId",true);
		shipmentCond = [];
		conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN, ["AM_SHIPMENT","AM_SHIPMENT"]));
		shipmentCond.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		shipmentCond.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		shipmentCond.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
		shipmentCond.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS ,eachRoute));
		cond = EntityCondition.makeCondition(shipmentCond,EntityOperator.AND);
		shipmentList = delegator.findList("Shipment", cond, null , null, null, false);
		if(shipmentList){
			shipment = EntityUtil.getFirst(shipmentList);
			shipmentId = shipment.get("shipmentId");
		}
		if(boothInRoute){
			routesWiseSalesList = [];
			boothInRoute.each{eachBooth ->
				boothInvoiceTotal = [:];
				invoiceIds = [];
				facilityType = delegator.findOne("Facility", UtilMisc.toMap("facilityId", eachBooth),false);
				categoryType = facilityType.getString("categoryTypeEnum");
				if(!categoryType.equalsIgnoreCase("PARLOUR")){
					condList=[];
					condList.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, "BYPROD_SALES_CHANNEL"));
					condList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
					condList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
					condList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
					condList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
					condList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS ,eachRoute));
					condList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS ,eachBooth));
					cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
					orderList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", cond, null, null, null, false);
					if(orderList){
						orderList = EntityUtil.getFieldListFromEntityList(orderList,"orderId",true);
						orderList.each{order ->
							invoiceList = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,order), null , null, null, false);
							invoiceIds = EntityUtil.getFieldListFromEntityList(invoiceList, "invoiceId",true);
							if(invoiceIds){
								boothInvoiceTotal.putAt("facilityId", eachBooth);
								boothInvoiceTotal.putAt("invoiceId", invoiceIds.getAt(0));
								totalAmount = dispatcher.runSync("getInvoiceRunningTotal", [invoiceIds: invoiceIds, organizationPartyId: "Company", userLogin: userLogin]);
								Pattern escaper = Pattern.compile("([^0-9.])");
								invoiceRunningTotal = escaper.matcher(totalAmount.invoiceRunningTotal).replaceAll("");
								invoicePaidTotal = escaper.matcher(totalAmount.invoicePaidTotal).replaceAll("");
								invoiceRunningTotal = new BigDecimal(invoiceRunningTotal);
								invoicePaidTotal = new BigDecimal(invoicePaidTotal);
								invoiceTotal = invoiceRunningTotal.add(invoicePaidTotal);
								boothInvoiceTotal.putAt("salesValue", invoiceTotal);
								routesWiseSalesList.addAll(boothInvoiceTotal);
							}
						}
					}
				}
				else{
					if(shipmentId){
						List exprList1 = [];
						exprList1.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachBooth));
						exprList1.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
						exprList1.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
						condition = EntityCondition.makeCondition(exprList1, EntityOperator.AND);
						shipmentReceipts = delegator.findList("ShipmentReceiptAndItem", condition, null, null, null, false);
						if(shipmentReceipts){
							totalInvoiceAmount = BigDecimal.ZERO;
							shipmentReceipts.each{eachReceipt ->
								productId  = eachReceipt.get("productId");
								quantity = eachReceipt.get("quantityAccepted");
								priceContext = [:];
								priceContext.put("userLogin", userLogin);
								priceContext.put("productStoreId", productStoreId);
								priceContext.put("productId", productId);
								priceContext.put("priceDate", dayBegin);
								priceContext.put("facilityId", eachBooth);
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
										unitTotalCost = unitCost.add(totalAmount);
										productQuantCost = unitTotalCost.multiply(quantity);
										totalInvoiceAmount = totalInvoiceAmount.add(productQuantCost);
										
									}
								}
							}
							boothInvoiceTotal.putAt("facilityId", eachBooth);
							boothInvoiceTotal.putAt("invoiceId", "");
							boothInvoiceTotal.putAt("salesValue", totalInvoiceAmount);
							routesWiseSalesList.addAll(boothInvoiceTotal);
						}
					}
				}
				
			}
			routeMap.putAt(eachRoute, routesWiseSalesList);
		}
	}
}
/*Debug.log("routeMap @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+routeMap);*/
context.routeMap = routeMap;
context.indentDate = UtilDateTime.toDateString(effectiveDate, "dd/MM/yyyy");
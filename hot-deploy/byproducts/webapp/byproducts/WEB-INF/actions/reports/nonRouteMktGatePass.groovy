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

import java.text.DateFormat;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.Debug;
import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
invoiceDetailList = [];
vatMap = [:];
reportTitle = [:];
today = UtilDateTime.nowTimestamp();
screenFlag = "";
orderDetailMap = [:];
shipmentId = parameters.shipmentId;
shipment = null;
orderHeaders = [];
orderIds = [];
conditionList = [];
if(shipmentId){
	shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
	
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	orderHeaders = delegator.findList("OrderHeader", condition, null, null, null, false);
	
	orderIds = EntityUtil.getFieldListFromEntityList(orderHeaders, "orderId", true);
	screenFlag = "gatePass";
}
else{
	orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", parameters.orderId), false);
	orderHeaders.add(orderHeader);
	orderIds.add(parameters.orderId);
	screenFlag = "dc";
}

orderIds.each{ eachOrderId ->
	ordersMap = [:];
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderRole = delegator.findList("OrderRole", cond, null, null, null, false);
	orderDetail = null;
	orderHeaders = EntityUtil.filterByCondition(orderHeaders, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
	
	if(orderHeaders){
		orderDetail = EntityUtil.getFirst(orderHeaders);
	}
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	orderItemBill = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond1, UtilMisc.toSet("invoiceId"), null, null, false);
	invoiceIds = EntityUtil.getFieldListFromEntityList(orderItemBill, "invoiceId", true);
	invoices = delegator.findList("Invoice", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds), null, null, null, false);
	
	invoice = null;
	if(invoices){
		invoice = invoices.get(0);
	}
	
	partyId = "";
	if(orderRole){
		partyId = (EntityUtil.getFirst(orderRole)).getString("partyId")
	}
	partyAddress = dispatcher.runSync("getPartyPostalAddress", [partyId: partyId, userLogin: userLogin]);
	partyName = dispatcher.runSync("getPartyNameForDate", [partyId: partyId, userLogin: userLogin]);
	orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId), null, null, null, false);
	orderItemsList = [];
	orderItems.each{eachItem ->
		tempMap = [:];
		tempMap.put("productId",eachItem.productId);
		tempMap.put("itemDescription",eachItem.itemDescription);
		tempMap.put("quantity",eachItem.quantity);
		/*tempMap.put("quantityLtr",eachItem.quantity*eachItem.quantityIncluded);*/
		orderItemsList.add(tempMap);
	}
	ordersMap.put("orderItems", orderItemsList);
	ordersMap.put("orderHeader", orderDetail);
	ordersMap.put("shipment", shipment);
	ordersMap.put("partyAddress", partyAddress);
	ordersMap.put("partyName", partyName);
	ordersMap.put("partyCode", partyId);
	ordersMap.put("invoice", invoice);
	ordersMap.put("screenFlag", screenFlag);
	orderDetailMap.put(eachOrderId, ordersMap);
}
context.orderDetailMap = orderDetailMap;


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
import java.util.Date;
import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


invoiceDetailList = [];
vatMap = [:];
reportTitle = [:];

today = UtilDateTime.nowTimestamp()
partyId = "";

invoiceId = parameters.invoiceId;
invoice = delegator.findOne("Invoice", [invoiceId : invoiceId], false);
invoiceItemList = delegator.findByAnd("OrderItemBillingAndInvoiceAndInvoiceItem", [invoiceId : invoice.invoiceId], null);\
orderIds = EntityUtil.getFieldListFromEntityList(invoiceItemList, "orderId", true);
orderId = "";
if(orderIds){
	orderId = orderIds.get(0);
}
partyIdFrom = invoice.partyIdFrom;
partyIdTo = invoice.partyId;
fromPartyDetailList  = delegator.findList("PartyProfileDefault", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom), null, null, null, false);
buyerPartyDetailList  = delegator.findList("PartyProfileDefault", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdTo), null, null, null, false);

fromPartyDetailList = EntityUtil.filterByDate(fromPartyDetailList, today);
buyerPartyDetailList = EntityUtil.filterByDate(buyerPartyDetailList, today);

if(fromPartyDetailList){
	context.fromPartyDetail = fromPartyDetailList.get(0);
}
if(buyerPartyDetailList){
	context.buyerPartyDetail = buyerPartyDetailList.get(0);
	
}

OrderHeader = delegator.findOne("OrderHeader", [orderId: orderId], false);
shipmentId = OrderHeader.shipmentId;

shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
billingAddress = [:];
if ("PURCHASE_INVOICE".equals(invoice.invoiceTypeId)) {
	billingAddress = InvoiceWorker.getSendFromAddress(invoice);
} else {
	billingAddress = InvoiceWorker.getBillToAddress(invoice);
}
context.invoice = invoice;
context.shipment = shipment;
context.billingAddress = billingAddress;

orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

orderItemAttributes = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
invoiceItems = [];
orderItems.each{ eachItem ->
	tempMap = [:];
	tempMap.put("productId", eachItem.productId);
	tempMap.put("itemDescription", eachItem.itemDescription);
	tempMap.put("quantity", eachItem.quantity);
	tempMap.put("unitListPrice", eachItem.unitListPrice);
	tempMap.put("unitPrice", eachItem.unitPrice);
	batchList = EntityUtil.filterByAnd(orderItemAttributes, UtilMisc.toMap("orderId", eachItem.orderId, "orderItemSeqId", eachItem.orderItemSeqId));
	if(batchList){
		batchItem = batchList.get(0);
		tempMap.put("batchNo", batchItem.getString("attrValue"));
	}else{
		tempMap.put("batchNo", "");
	}
	invoiceItems.add(tempMap);
}
context.invoiceItems = invoiceItems;

taxParty = delegator.findOne("Party", UtilMisc.toMap("partyId", "TAX2"), false);
taxAuthority = delegator.findOne("TaxAuthority", UtilMisc.toMap("taxAuthGeoId","IND", "taxAuthPartyId","TAX2"), false);

context.taxParty = taxParty;
context.taxAuthority = taxAuthority;


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
import java.math.RoundingMode;
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
reportFlag = "";
screenFlag = parameters.screenFlag;
orderDetailMap = [:];
shipmentId = parameters.shipmentId;
shipment = null;
orderHeaders = [];
orderIds = [];
rounding = RoundingMode.HALF_UP;
dctx = dispatcher.getDispatchContext();
conditionList = [];
if(shipmentId){
	shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
	
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	orderHeaders = delegator.findList("OrderHeader", condition, null, null, null, false);
	
	orderIds = EntityUtil.getFieldListFromEntityList(orderHeaders, "orderId", true);
	reportFlag = "gatePass";
}
else{
	orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", parameters.orderId), false);
	orderHeaders.add(orderHeader);
	orderIds.add(parameters.orderId);
	reportFlag = "dc";
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
	if(orderDetail){
		salesChannelEnumId = orderDetail.salesChannelEnumId;
		if(salesChannelEnumId == "ICP_NANDINI_CHANNEL"){
			reportFlag = "NANDINI";
		}
		if(salesChannelEnumId == "ICP_AMUL_CHANNEL"){
			reportFlag = "AMUL";
		}
		if(salesChannelEnumId == "ICP_BELLARY_CHANNEL"){
			reportFlag = "BELLARY";
		}
		if(salesChannelEnumId == "FGS_PRODUCT_CHANNEL"){
			reportFlag = "FGS";
		}
		if(salesChannelEnumId == "DEPOT_CHANNEL"){
			reportFlag = "DEPOT";
		}
		if(salesChannelEnumId == "INTUNIT_TR_CHANNEL"){
			reportFlag = "INTERUNIT";
		}
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
	companyDetails = (Map)(PartyWorker.getPartyIdentificationDetails(delegator, "Company")).get("partyDetails");
	partyAddress = dispatcher.runSync("getPartyPostalAddress", [partyId: partyId, userLogin: userLogin]);
	partyName = dispatcher.runSync("getPartyNameForDate", [partyId: partyId, userLogin: userLogin]);
	orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId), null, null, null, false);
	productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	conversionResult = ByProductNetworkServices.getProductQtyConversions(dctx, UtilMisc.toMap("productList", products, "userLogin", userLogin));
	productConversionDetails = [:];
	if(conversionResult){
		productConversionDetails = conversionResult.get("productConversionDetails");
	}
	condList = [];
	condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
	condList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));
	condExpr = EntityCondition.makeCondition(condList, EntityOperator.AND);
	orderBatchNumbers = delegator.findList("OrderItemAttribute", condExpr, null, null, null, false);
	orderItemsList = [];
	orderItems.each{eachItem ->
		productConvDetail = productConversionDetails.get(eachItem.productId);
		
		batchList = EntityUtil.filterByCondition(orderBatchNumbers, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		batchNo = "";
		if(batchList){
			batchValue = EntityUtil.getFirst(batchList);
			batchNo = batchValue.get("attrValue");
		}
		prodCrateValue = 1;
		if(productConvDetail && productConvDetail.get("CRATE")){
			prodCrateValue = productConvDetail.get("CRATE");
		}
		List prodDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
		prodDetail = EntityUtil.getFirst(prodDetails);
		crateQty = (eachItem.quantity).divide(new BigDecimal(prodCrateValue) , 2, rounding);
		qtyLtr = (eachItem.quantity).multiply(prodDetail.quantityIncluded).setScale(2, rounding);
		tempMap = [:];
		tempMap.put("productId",eachItem.productId);
		tempMap.put("description",prodDetail.description);
//		tempMap.put("itemDescription",eachItem.itemDescription);
		tempMap.put("batchNo", batchNo);
		tempMap.put("qty", eachItem.quantity);
		tempMap.put("qtyInCrate", crateQty);
		tempMap.put("qtyPerCrate", prodCrateValue);
		tempMap.put("qtyLtr", qtyLtr);
		tempMap.put("unitPrice", eachItem.unitListPrice);
		tempMap.put("totalAmt", eachItem.quantity*eachItem.unitListPrice);
		orderItemsList.add(tempMap);
	}
	ordersMap.put("orderItems", orderItemsList);
	ordersMap.put("orderHeader", orderDetail);
	ordersMap.put("shipment", shipment);
	ordersMap.put("partyAddress", partyAddress);
	ordersMap.put("companyDetail", companyDetails);
	ordersMap.put("partyName", partyName);
	ordersMap.put("partyCode", partyId);
	ordersMap.put("invoice", invoice);
	ordersMap.put("reportFlag", reportFlag);
	ordersMap.put("screenFlag", screenFlag);
	orderDetailMap.put(eachOrderId, ordersMap);
}
context.orderDetailMap = orderDetailMap;


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
/*
cxt = [:];
cxt.put("userLogin", userLogin);
cxt.put("productId", "518");
cxt.put("partyId", "ABDUL");
cxt.put("priceDate", UtilDateTime.nowTimestamp());
cxt.put("productStoreId", "1006");
cxt.put("productPriceTypeId", "MRP_IS");
cxt.put("geoTax", "VAT");

result = ByProductNetworkServices.calculateStoreProductPrices(delegator, dispatcher, cxt);
Debug.log("result############"+result);
*/
/*finalProdList = [];
prodList= ProductWorker.getProductsByCategory(delegator ,"ICE_CREAM_AMUL" ,null);
finalProdList.addAll(prodList);
prodList= ProductWorker.getProductsByCategory(delegator ,"ICE_CREAM_NANDINI" ,null);
finalProdList.addAll(prodList);
finalProdIdsList = EntityUtil.getFieldListFromEntityList(finalProdList, "productId", true);
productPrices = delegator.findList("ProductPrice", EntityCondition.makeCondition("productId", EntityOperator.IN, finalProdIdsList), null, null, null, false);
duplicateProdList = [];
finalProdIdsList.each{ eachProdId ->
	prodPriceList = EntityUtil.filterByCondition(productPrices, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProdId));
	if(prodPriceList.size()>5){
		duplicateProdList.add(eachProdId);
	}
	
}*/
//Debug.log("##########duplicateProdList######"+duplicateProdList);

invoiceSlipsMap = [:];
shipmentId = parameters.shipmentId;
conditionList = [];
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderHeaders = delegator.findList("OrderHeader", condition, UtilMisc.toSet("orderId"), null, null, false);
orderIds = EntityUtil.getFieldListFromEntityList(orderHeaders, "orderId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoices = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, UtilMisc.toSet("invoiceId"), null, null, false);
invoiceIds = EntityUtil.getFieldListFromEntityList(invoices, "invoiceId", true);

invoiceIds.each { invoiceId ->
	invoiceDetailMap = [:];
	invoice = delegator.findOne("Invoice", [invoiceId : invoiceId], false);
	invoiceOrderItemList = delegator.findByAnd("OrderItemBillingAndInvoiceAndInvoiceItem", [invoiceId : invoiceId], null);
	orderIds = EntityUtil.getFieldListFromEntityList(invoiceOrderItemList, "orderId", true);
	orderId = "";
	if(orderIds){
		orderId = orderIds.get(0);
	}
	partyIdFrom = invoice.partyIdFrom;
	partyIdTo = invoice.partyId;
	fromPartyDetail = (Map)(PartyWorker.getPartyIdentificationDetails(delegator, partyIdFrom)).get("partyDetails");
	toPartyDetail = (Map)(PartyWorker.getPartyIdentificationDetails(delegator, partyIdTo)).get("partyDetails");
	invoiceDetailMap.put("fromPartyDetail", fromPartyDetail);
	invoiceDetailMap.put("toPartyDetail", toPartyDetail);
	
	invoiceSequence = delegator.findList("BillOfSaleInvoiceSequence", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId), null, null,null, false);
	
	OrderHeader = delegator.findOne("OrderHeader", [orderId: orderId], false);
	shipmentId = OrderHeader.shipmentId;
	
	shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
	billingAddress = [:];
	if ("PURCHASE_INVOICE".equals(invoice.invoiceTypeId)) {
		billingAddress = InvoiceWorker.getSendFromAddress(invoice);
	} else {
		billingAddress = InvoiceWorker.getBillToAddress(invoice);
	}
	invoiceDetailMap.put("invoice", invoice);
	invoiceDetailMap.put("shipment", shipment);
	invoiceDetailMap.put("billingAddress", billingAddress);
	invoiceItems = delegator.findList("InvoiceItem", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId), null, null, null, false);
	productIds = EntityUtil.getFieldListFromEntityList(invoiceItems, "productId", true);
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	prodCategories = ["ICE_CREAM_NANDINI", "ICE_CREAM_AMUL", "MILK_POWDER", "FG_STORE"];
	condList = [];
	condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, prodCategories));
	condList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	condExpr = EntityCondition.makeCondition(condList, EntityOperator.AND);
	productCategories = delegator.findList("ProductCategoryAndMember", condExpr, null, null, null, false);
	
	chapters = EntityUtil.getFieldListFromEntityList(productCategories, "chapterNum", true);
	chapterMap = [:];
	chapters.each{ eachChap ->
		chapterDetails = EntityUtil.filterByCondition(productCategories, EntityCondition.makeCondition("chapterNum", EntityOperator.EQUALS, eachChap));
		chapDetail = EntityUtil.getFirst(chapterDetails);
		chapter = "";
		subHeading = "";
		displayStr = "";
		description = chapDetail.productCategoryId;
		if(chapDetail.description){
			description = chapDetail.description;
		}
		if(chapDetail.chapterNum){
			chapter = chapDetail.chapterNum;
			displayStr += chapter;
		}
		if(chapDetail.subHeading){
			subHeading = chapDetail.subHeading;
			displayStr += "/"+ subHeading;
		}
		chapterMap.put(description, displayStr)
	}
	
	
	orderItemAttributes = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	invoiceItemDetail = [];
	invoiceTaxItems = [:];
	invoiceItems.each{ eachItem ->
		
		if(eachItem.productId){
			
			cxt = [:];
			cxt.put("userLogin", userLogin);
			cxt.put("productId", eachItem.productId);
			cxt.put("partyId", partyIdTo);
			cxt.put("priceDate", UtilDateTime.nowTimestamp());
			cxt.put("productStoreId", "_NA_");
			cxt.put("productPriceTypeId", "MRP_IS");
			cxt.put("geoTax", "VAT");
			
			result = ByProductNetworkServices.calculateStoreProductPrices(delegator, dispatcher, cxt);
			mrpPrice = result.get("totalPrice");
		
			orderSeqList = EntityUtil.filterByAnd(invoiceOrderItemList, UtilMisc.toMap("invoiceId", eachItem.invoiceId, "invoiceItemSeqId", eachItem.invoiceItemSeqId));
			tempMap = [:];
			List prodDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
			prodDetail = EntityUtil.getFirst(prodDetails);
			tempMap.put("productId", eachItem.productId);
			tempMap.put("itemDescription", prodDetail.description);
			tempMap.put("quantityLtr", (eachItem.quantity)*prodDetail.quantityIncluded);
			tempMap.put("quantity", eachItem.quantity);
			tempMap.put("mrpPrice", mrpPrice);
			tempMap.put("defaultPrice", eachItem.unitPrice);
			if(orderSeqList){
				orderItemSeqId = (EntityUtil.getFirst(orderSeqList)).get("orderItemSeqId");
				batchList = EntityUtil.filterByAnd(orderItemAttributes, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
				if(batchList){
					batchItem = batchList.get(0);
					tempMap.put("batchNo", batchItem.getString("attrValue"));
				}else{
					tempMap.put("batchNo", "");
				}
			}
			invoiceItemDetail.add(tempMap);
		}
		else{
			invoiceTaxItems.put(eachItem.invoiceItemTypeId, eachItem.amount);
		}
	}
	if(invoiceSequence){
		invoiceNo = (invoiceSequence.get(0)).get("sequenceId");
	}
	else{
		invoiceNo = invoiceId
	}
	invoiceDetailMap.put("invoiceNo", invoiceNo);
	invoiceDetailMap.put("chapterMap", chapterMap);
	invoiceDetailMap.put("invoiceItems", invoiceItemDetail);
	invoiceDetailMap.put("invoiceTaxItems", invoiceTaxItems);
	invoiceSlipsMap.put(partyIdTo, invoiceDetailMap);
}

taxParty = delegator.findOne("Party", UtilMisc.toMap("partyId", "TAX10"), false);
taxAuthority = delegator.findOne("TaxAuthority", UtilMisc.toMap("taxAuthGeoId","IND", "taxAuthPartyId","TAX10"), false);
context.invoiceSlipsMap = invoiceSlipsMap;
context.taxParty = taxParty;
context.taxAuthority = taxAuthority;


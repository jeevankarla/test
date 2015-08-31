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
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import java.util.SortedMap;
import javolution.util.FastList;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.byproducts.SalesHistoryServices;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
	
effectiveDateStr = parameters.fromDate;
thruEffectiveDateStr = parameters.thruDate;

if (UtilValidate.isEmpty(effectiveDateStr)) {
	effectiveDate = UtilDateTime.nowTimestamp();
}
else{
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
	}
}
if (UtilValidate.isEmpty(thruEffectiveDateStr)) {
	thruEffectiveDate = effectiveDate;
}
else{
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		thruEffectiveDate = new java.sql.Timestamp(sdf.parse(thruEffectiveDateStr+" 00:00:00").getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + thruEffectiveDate, "");
	}
}

dayBegin = UtilDateTime.getDayStart(effectiveDate);
dayEnd = UtilDateTime.getDayEnd(thruEffectiveDate);
context.put("dayBegin",dayBegin);
context.put("dayEnd",dayEnd);

List conditionList= FastList.newInstance();
conditionList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.NOT_EQUAL, null));
conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "VAT_SALE"));
EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
Set fieldsToSelect = UtilMisc.toSet("productId", "taxPercentage");

EntityFindOptions findOptions = new EntityFindOptions();
findOptions.setDistinct(true);

productPrice = delegator.findList("ProductPrice", condition1,  fieldsToSelect, ["taxPercentage"], findOptions, false);
List vatList = EntityUtil.getFieldListFromEntityList(productPrice, "taxPercentage", true);
context.vatList = vatList;

resultMap = [:];
shipmentIds = [];
amShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,"AM");
shipmentIds.addAll(amShipmentIds);
pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,"PM");
shipmentIds.addAll(pmShipmentIds);
maxIntervalDays=UtilDateTime.getIntervalInDays(dayBegin,dayEnd);
for(k = 1;k<=maxIntervalDays;k++){
	Timestamp currDay=UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(dayBegin, k));
	List adhocShipments  = ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(currDay, "yyyy-MM-dd HH:mm:ss"),"RM_DIRECT_SHIPMENT",null);
	if(UtilValidate.isNotEmpty(adhocShipments)){
		shipmentIds.addAll(adhocShipments);
	}
}

if(UtilValidate.isNotEmpty(shipmentIds)){
	resultMap = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds,fromDate:dayBegin, thruDate:dayEnd,includeReturnOrders:true]).get("productTotals");
}
/*boothsList=ByProductNetworkServices.getBoothList(delegator ,null);
dayTotals = SalesHistoryServices.getSalesDayPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:UtilMisc.toList(boothsList),fromDate:dayBegin, thruDate:dayEnd,includeReturnOrders:true,"periodTypeId":"SALES_DAY"]);
resultMap = dayTotals.get("productTotals");*/

// handle subsidy ghee invoices
condExpr = [];
condExpr.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
condExpr.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
condExpr.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "SUB_GHEE"));
condExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
subGheeInvoices = delegator.findList("Invoice", cond, UtilMisc.toSet("invoiceId"), null, null, false);

gheeInvoiceIds = EntityUtil.getFieldListFromEntityList(subGheeInvoices, "invoiceId", true);
gheeInvoiceItems = delegator.findList("InvoiceItem", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, gheeInvoiceIds), null, null, null, false);
productIds = EntityUtil.getFieldListFromEntityList(gheeInvoiceItems, "productId", true);
gheeVatPercents = EntityUtil.getFieldListFromEntityList(gheeInvoiceItems, "vatPercent", true);

vatMap = [:];
vatList.each { vat->
	productIdList = EntityUtil.filterByDate(delegator.findByAnd("ProductPrice", [taxPercentage : vat ,productPriceTypeId : "VAT_SALE"]));
	if(UtilValidate.isNotEmpty(productIdList)){
		List vatProdList = EntityUtil.getFieldListFromEntityList(productIdList, "productId", true);
		productList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, vatProdList) , null, null, null, false);
		productList = UtilMisc.sortMaps(productList, UtilMisc.toList("brandName"));
		productValueMap = [:];
		productList.each{ product->
			if(UtilValidate.isNotEmpty(resultMap.get(product.productId))){
				prodMap = resultMap.get(product.productId);
				productValueMap.put(product.productId,prodMap);
			}
		}
		productCategoryMap = [:];
		productValueMap.each{ productValue ->
			if(UtilValidate.isNotEmpty(productValue)){
				currentProduct = productValue.getKey();
				product = delegator.findOne("Product", [productId : currentProduct], false);
				tempVariantMap =[:];
				productAssoc = EntityUtil.getFirst(delegator.findList("ProductAssoc", EntityCondition.makeCondition(["productAssocTypeId": "PRODUCT_VARIANT", "productIdTo": currentProduct,"thruDate":null]), null, ["-fromDate"], null, false));
				virtualProductId = currentProduct;
				if(UtilValidate.isNotEmpty(productAssoc)){
					virtualProductId = productAssoc.productId;
				}
				if(UtilValidate.isEmpty(tempVariantMap[virtualProductId])){
					tempMap = [:];
					tempProdMap = [:];
					tempProdMap["quantity"] = productValue.getValue().get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
					if(currentProduct == "15"){
						if(UtilValidate.isNotEmpty(productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total"))){
							tempProdMap["subsidy"] = productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
							tempProdMap["subsidyRevenue"] = productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
						}
					}
					tempProdMap["revenue"] = productValue.getValue().get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
					tempProdMap["vatRevenue"] = productValue.getValue().get("vatRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
					tempMap[currentProduct] = tempProdMap;
					tempVariantMap[virtualProductId] = tempMap;
				}else{
					tempMap = [:];
					productQtyMap = [:];
					tempMap.putAll(tempVariantMap.get(virtualProductId));
					productQtyMap.putAll(tempMap);
					productQtyMap["quantity"] += productValue.getValue().get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
					if(currentProduct == "15"){
						if(UtilValidate.isNotEmpty(productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total"))){
							productQtyMap["subsidy"] += productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
							productQtyMap["subsidyRevenue"] += productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
						}
					}
					productQtyMap["revenue"] += productValue.getValue().get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
					productQtyMap["vatRevenue"] += productValue.getValue().get("vatRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
					tempMap[currentProduct] = productQtyMap;
					tempVariantMap[virtualProductId] = tempMap;
				}
				if(UtilValidate.isEmpty(productCategoryMap[product.primaryProductCategoryId])){
					productCategoryMap.put(product.primaryProductCategoryId,tempVariantMap);
				}else{
					tempCatMap = [:];
					tempCatMap.putAll(productCategoryMap[product.primaryProductCategoryId]);
					if(UtilValidate.isEmpty(tempCatMap[virtualProductId])){
						tempMap = [:];
						tempProdMap = [:];
						tempProdMap["quantity"] = productValue.getValue().get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
						if(currentProduct == "15"){
							if(UtilValidate.isNotEmpty(productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total"))){
								tempProdMap["subsidy"] = productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
								tempProdMap["subsidyRevenue"] = productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
							}
						}
						tempProdMap["revenue"] = productValue.getValue().get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
						tempProdMap["vatRevenue"] = productValue.getValue().get("vatRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
						tempMap[currentProduct] = tempProdMap;
						tempCatMap[virtualProductId] = tempMap;
					}else{
						tempMap = [:];
						tempMap.putAll(tempCatMap.get(virtualProductId));
							if(UtilValidate.isEmpty(tempMap.get(currentProduct))){
								currentTempMap = [:];
								currentTempMap["quantity"] = productValue.getValue().get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
								if(currentProduct == "15"){
									if(UtilValidate.isNotEmpty(productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total"))){
										currentTempMap["subsidy"] = productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
										currentTempMap["subsidyRevenue"] = productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
									}
								}
								currentTempMap["revenue"] = productValue.getValue().get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
								currentTempMap["vatRevenue"] = productValue.getValue().get("vatRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
								tempMap[currentProduct] = currentTempMap;
							}else{
								currentTempMap = [:];
								currentTempMap["quantity"] += productValue.getValue().get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
								if(currentProduct == "15"){
									if(UtilValidate.isNotEmpty(productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total"))){
										currentTempMap["subsidy"] += productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("total").setScale(0,BigDecimal.ROUND_HALF_UP);
										currentTempMap["subsidyRevenue"] += productValue.getValue().get("supplyTypeTotals").get("EMP_SUBSIDY").get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
									}
								}
								currentTempMap["revenue"] += productValue.getValue().get("totalRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
								currentTempMap["vatRevenue"] += productValue.getValue().get("vatRevenue").setScale(0,BigDecimal.ROUND_HALF_UP);
								tempMap[currentProduct] = currentTempMap;
							}
						tempCatMap[virtualProductId] = tempMap;
					}
					if(UtilValidate.isNotEmpty(tempCatMap)){
						productCategoryMap.put(product.primaryProductCategoryId,tempCatMap);
					}
				}
			}
		}
	}
	if(UtilValidate.isNotEmpty(productCategoryMap)){
		vatMap.put(vat,productCategoryMap);
	}
}
//subsidy ghee vat
condList = [];
condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, gheeInvoiceIds));
condList.add(EntityCondition.makeCondition("vatAmount", EntityOperator.NOT_EQUAL, null));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
gheeInvoiceItems = delegator.findList("InvoiceItem", cond, null, null, null, false);
gheeVatPercents = EntityUtil.getFieldListFromEntityList(gheeInvoiceItems, "vatPercent", true);
gheeVatPercents.each{ eachVat ->
	productIds = EntityUtil.getFieldListFromEntityList(gheeInvoiceItems, "productId", true);
	productIds.each{ eachProd ->
		prodInvoiceItems = EntityUtil.filterByCondition(gheeInvoiceItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd));
		product = delegator.findOne("Product", UtilMisc.toMap("productId", eachProd), false);
		primaryCategory = "";
		virtualProdId = "Other Products";
		condList = [];
		condList.add(EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_VARIANT"));
		condList.add(EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, eachProd));
		cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
		prodAssoc = delegator.findList("ProductAssoc", cond, null, null, null, false);
		prodAssoc = EntityUtil.filterByDate(prodAssoc, dayEnd);
		if(prodAssoc){
			virtualProdId = (EntityUtil.getFirst(prodAssoc)).productId;
		}
		
		if(product){
			primaryCategory = product.primaryProductCategoryId;
		}
		
		prodInvoiceItems.each { eachItem ->
			vatAmt = eachItem.vatAmount;
			totRevenue = eachItem.vatAmount+eachItem.amount;
			if(vatMap.get(eachVat)){
				vatPercentMap = vatMap.get(eachVat);
				if(vatPercentMap.get(primaryCategory)){
					vatVirtualProdMap = vatPercentMap.get(primaryCategory);
					if(vatVirtualProdMap.get(virtualProdId)){
						productDetailMap = 	vatVirtualProdMap.get(virtualProdId);
						if(productDetailMap.get(eachProd)){
							productRevenueMap = productDetailMap.get(eachProd);
							tempMap = [:];
							tempMap["quantity"] = eachItem.quantity+productRevenueMap["quantity"];
							tempMap["revenue"] = totRevenue+productRevenueMap["revenue"];
							tempMap["vatRevenue"] = vatAmt+productRevenueMap["vatRevenue"];
							productDetailMap.put(eachProd, tempMap);
							vatVirtualProdMap.put(virtualProdId, productDetailMap);
							vatPercentMap.put(primaryCategory, vatVirtualProdMap);
							vatMap.put(eachVat, vatPercentMap);
						}
						else{
							tempDetailMap = [:];
							tempDetailMap["quantity"] = eachItem.quantity;
							tempDetailMap["revenue"] = eachItem.amount;
							tempDetailMap["vatRevenue"] = eachItem.vatAmount;
							productDetailMap.put(eachProd, tempDetailMap);
							vatVirtualProdMap.put(virtualProdId, productDetailMap);
							vatPercentMap.put(primaryCategory, vatVirtualProdMap);
							vatMap.put(eachVat, vatPercentMap);
						}
					}else{
						tempDetailMap = [:];
						prodDetailMap = [:];
						tempDetailMap["quantity"] = eachItem.quantity;
						tempDetailMap["revenue"] = eachItem.amount;
						tempDetailMap["vatRevenue"] = eachItem.vatAmount;
						prodDetailMap.put(eachProd, tempDetailMap);
						vatVirtualProdMap.put(virtualProdId, prodDetailMap);
						vatMap.put(eachVat, vatVirtualProdMap);
					}
				}else{
					tempDetailMap = [:];
					prodDetailMap = [:];
					subCatDetailMap = [:];
					tempDetailMap["quantity"] = eachItem.quantity;
					tempDetailMap["revenue"] = eachItem.amount;
					tempDetailMap["vatRevenue"] = eachItem.vatAmount;
					prodDetailMap.put(eachProd, tempDetailMap);
					subCatDetailMap.put(virtualProdId, prodDetailMap);
					vatPercentMap.put(primaryCategory, subCatDetailMap);
					vatMap.put(eachVat, vatPercentMap);
				}
				
			}
			else{
				tempDetailMap = [:];
				prodDetailMap = [:];
				subCatDetailMap = [:];
				categoryDetailMap = [:];
				tempDetailMap["quantity"] = eachItem.quantity;
				tempDetailMap["revenue"] = eachItem.amount;
				tempDetailMap["vatRevenue"] = eachItem.vatAmount;
				prodDetailMap.put(eachProd, tempDetailMap);
				subCatDetailMap.put(virtualProdId, prodDetailMap);
				categoryDetailMap.put(primaryCategory, subCatDetailMap);
				vatMap.put(eachVat, categoryDetailMap);
			}
		}
	}
}
// for CSV
vatReportCsvList = [];
if(UtilValidate.isNotEmpty(vatMap)){
	vatMap.each { vat->
		vatKey = vat.getKey();
		product = vat.getValue();
		Iterator mapIter = product.entrySet().iterator();
		while (mapIter.hasNext()) {
			Map.Entry entry = mapIter.next();
			productValues = entry.getValue();
			Iterator prodMapIter = productValues.entrySet().iterator();
			while (prodMapIter.hasNext()) {
				Map.Entry prodEntry = prodMapIter.next();
				prodKey = prodEntry.getKey();
				prodValues = prodEntry.getValue();
				Iterator productMapIter = prodValues.entrySet().iterator();
				while (productMapIter.hasNext()) {
					Map.Entry productEntry = productMapIter.next();
					productKey = productEntry.getKey();
					productDetails = delegator.findOne("Product", ["productId" : productKey], true);
					prodValue = productEntry.getValue();
					quantity = 0;
					revenue = 0;
					vatRevenue = 0;
					quantity = prodValue.get("quantity");
					revenue = prodValue.get("revenue");
					vatRevenue = prodValue.get("vatRevenue");
					csvVatMap = [:];
					if(UtilValidate.isNotEmpty(productDetails)){
						csvVatMap["product"] = productDetails.brandName;
					}
					csvVatMap["vat"] = vatKey;
					csvVatMap["saleRevenue"] = (revenue - vatRevenue);
					csvVatMap["vatRevenue"] = vatRevenue;
					csvVatMap["revenue"] = revenue;
					csvVatMap["quantity"] = quantity;
					tempMap = [:];
					tempMap.putAll(csvVatMap);
					vatReportCsvList.add(tempMap);
				}
			}
		}
	}
}
context.put("vatMap",vatMap);
context.put("vatReportCsvList",vatReportCsvList);

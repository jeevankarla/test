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
	finalVatList = [];
	
	productReturnMap = [:];
	resultMap = [:];
	shipmentIds = [];
	amShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,"AM");
	shipmentIds.addAll(amShipmentIds);
	pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,"PM");
	shipmentIds.addAll(pmShipmentIds);
	List adhocShipments  = ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),"RM_DIRECT_SHIPMENT",null);
	if(UtilValidate.isNotEmpty(adhocShipments)){
		shipmentIds.addAll(adhocShipments);
	}
	if(UtilValidate.isNotEmpty(shipmentIds)){
		resultMap = ByProductNetworkServices.getPeriodTotals(dctx, [shipmentIds:shipmentIds, fromDate:dayBegin, thruDate:dayEnd]).get("productTotals");
	}
	returnConditionList=[];
	returnConditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	returnConditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
	returnConditionList.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS, "RETURN_ACCEPTED"));
	returnCondition = EntityCondition.makeCondition(returnConditionList,EntityOperator.AND);
	returnHeaderItemsList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", returnCondition, null, null, null, false);
	if(UtilValidate.isNotEmpty(returnHeaderItemsList)){
		returnHeaderItemsList.each{ returnItem->		
			returnQuantity =0;
			returnPrice = 0;
			returnRevenue = 0;
			returnProductId = 0;
			if(UtilValidate.isNotEmpty(returnItem.returnQuantity)){
				returnQuantity = returnItem.returnQuantity;
			}
			if(UtilValidate.isNotEmpty(returnItem.returnPrice)){
				returnPrice = returnItem.returnPrice;
			}
			if(UtilValidate.isNotEmpty(returnItem.productId)){
				returnProductId = returnItem.productId;
			}
			if(UtilValidate.isNotEmpty(returnQuantity)){
				returnRevenue = (returnQuantity*returnPrice);
			}
			if(UtilValidate.isEmpty(productReturnMap[returnProductId])){
				productReturnMap[returnProductId] = returnRevenue;
			}else{
				productReturnMap[returnProductId] += returnRevenue;
			}
		}
	}
	vatList.each { vat->
		vatMap = [:];
		productIdList = EntityUtil.filterByDate(delegator.findByAnd("ProductPrice", [taxPercentage : vat ,productPriceTypeId : "VAT_SALE"]));
		if(UtilValidate.isNotEmpty(productIdList)){
			List productIdList = EntityUtil.getFieldListFromEntityList(productIdList, "productId", true);
			
			productList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, productIdList) , null, null, null, false);
			productList = UtilMisc.sortMaps(productList, UtilMisc.toList("brandName"));
			if(UtilValidate.isNotEmpty(productList)){
				productMap = [:];
				productList.each { product->
					prodValMap=[:];
					prodValue=0;
					if(UtilValidate.isNotEmpty(resultMap.get(product.productId))){
						prodValue = resultMap.get(product.productId).get("totalRevenue");
					}
					returnProdValue=0;
					if(UtilValidate.isNotEmpty(productReturnMap.get(product.productId))){
						returnProdValue = productReturnMap.get(product.productId);
					}
					if(UtilValidate.isNotEmpty(prodValue)&&(prodValue!=0)){
						prodValMap["prodValue"]=prodValue;
					}
					if(UtilValidate.isNotEmpty(returnProdValue)&&(returnProdValue!=0)){
						prodValMap["returnProdValue"]=returnProdValue;
					}
					if(UtilValidate.isNotEmpty(prodValMap)){
						productMap[product.productId] = prodValMap;
					}
				}
				tempMap = [:];
				tempMap.putAll(productMap);
				if(UtilValidate.isNotEmpty(tempMap)){
					vatMap.put(vat,tempMap);
					finalVatList.add(vatMap);
				}
			}
		}
	}
	context.put("finalVatList",finalVatList);
/*	vatWiseProductTotals = [:];
	
	categoryProductMap = [:];
	categoryList = [];
	
	prodCatAttrList = [];
	prodCatAttrList = UtilMisc.toList("UNION_TAXABLE", "DAIRY_TAXABLE");
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.IN, prodCatAttrList));
		
	EntityCondition AttrCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	productCategoryAttribute = delegator.findList("ProductCategoryAttribute", AttrCondition, null, ["attrName","attrValue","productCategoryId"], null, false);
	categoryGroupingMap = [:];
	for(i=0; i<productCategoryAttribute.size(); i++){
		
		productCategoryAtt = productCategoryAttribute.get(i);
		catGroupingDetailsMap = [:];
		catGroupingDetailsMap.put("origin", productCategoryAtt.get("attrName"));
		catGroupingDetailsMap.put("vatPercent", productCategoryAtt.get("attrValue"));
		tempMap = [:];
		tempMap.putAll(catGroupingDetailsMap);
		categoryGroupingMap.put(productCategoryAtt.get("productCategoryId"), tempMap);
	}
	
	resultMap = ByProductNetworkServices.getPeriodTotals(dctx, UtilMisc.toMap("fromDate", monthBegin, "thruDate", monthEnd));
	periodTotalsMap = resultMap.get("productTotals");
	
	
	
	Iterator periodTotalsIter = periodTotalsMap.entrySet().iterator();
	while (periodTotalsIter.hasNext()) {
		Map.Entry periodTotalsEntry = periodTotalsIter.next();
		productCategory = periodTotalsEntry.getKey();
		
		groupingDetailMap = categoryGroupingMap.get(productCategory);
		
		
		groupingDetails = groupingDetailMap.get("origin") + "_" +groupingDetailMap.get("vatPercent");
		vatPercentage = groupingDetailMap.get("vatPercent");
		
		prodSaleValue = (((periodTotalsEntry.getValue()).get("prodCategoryTotals")).get("sale")).get("totals");
		
		prodSaleDetailMap = [:];
		prodSaleDetailMap.put("prodCategory", productCategory);
		prodSaleDetailMap.put("vatPercentage", vatPercentage);
		prodSaleDetailMap.put("basicValue", prodSaleValue.get("catSalebasicValue"));
		prodSaleDetailMap.put("vatValue", prodSaleValue.get("catSaleVatValue"));
		prodSaleDetailMap.put("totalValue", prodSaleValue.get("catSalebasicValue")+prodSaleValue.get("catSaleVatValue"));
		tempProdSaleDetailList = [];
		tempProdSaleDetailList.addAll(prodSaleDetailMap);
		
		if(UtilValidate.isEmpty(categoryProductMap[groupingDetails])){
			categoryProductMap[groupingDetails] = tempProdSaleDetailList;
		}
		else{
			updateProdSaleList = categoryProductMap[groupingDetails];
			updateProdSaleList.addAll(tempProdSaleDetailList);
			
			categoryProductMap[groupingDetails] = updateProdSaleList;
		}
		
	}
	context.categoryProductMap = categoryProductMap;
	
	List categoryGroupingList = [];
	categoryGroupingList = UtilMisc.toList("UNION_TAXABLE_0","UNION_TAXABLE_5","UNION_TAXABLE_14.5","DAIRY_TAXABLE_0","DAIRY_TAXABLE_5","DAIRY_TAXABLE_14.5");
	
	context.categoryGroupingList = categoryGroupingList;*/
	
	
	


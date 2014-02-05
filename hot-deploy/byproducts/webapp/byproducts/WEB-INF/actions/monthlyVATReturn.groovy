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
		
	customTimePeriod = parameters.customTimePeriodId;
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	List unionProductList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("UNION_PRODUCTS");
	unionProducts = parameters.unionProductList;
	
	context.putAt("fromDateTime", fromDateTime);
	
	monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	
	monthDate = UtilDateTime.toDateString(monthBegin, "MMMMM - yyyy");
	context.monthDate = monthDate;
	
	List conditionList= FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "BYPROD_FA_CAT"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "BYPROD_SO"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "BYPROD_GIFT"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "SP_SALES"));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	facilityCatEnum = delegator.findList("Enumeration", condition, null, ["sequenceId"], null, false);
	
	facilityCategoryList = EntityUtil.getFieldListFromEntityList(facilityCatEnum, "enumId", false);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "%VAT_AMT"));
	EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	EntityFindOptions findOptions = new EntityFindOptions();
	findOptions.setDistinct(true);
	
	Set fieldsToSelect = UtilMisc.toSet("productId", "taxPercentage");
	productPrice = delegator.findList("ProductPrice", condition1,  fieldsToSelect, ["taxPercentage"], findOptions, false);
	
	List vatList = EntityUtil.getFieldListFromEntityList(productPrice, "taxPercentage", true);
	context.vatList = vatList;
	
	vatWiseProductTotals = [:];
	
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
	
	resultMap = ByProductReportServices.getByProductPeriodTotals(dctx, UtilMisc.toMap("fromDate", monthBegin, "thruDate", monthEnd));
	periodTotalsMap = resultMap.get("periodTotalsMap");
	
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
	
	context.categoryGroupingList = categoryGroupingList;
	
	
	


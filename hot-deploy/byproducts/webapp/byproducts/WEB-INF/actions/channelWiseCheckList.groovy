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
	import in.vasista.vbiz.byproducts.ByProductReportServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
		
	customTimePeriod = parameters.customTimePeriodId;
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	
	/*fromDateTime=UtilDateTime.nowTimestamp();
	thruDateTime=UtilDateTime.nowTimestamp();*/
	
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
	
	conditionList =[];
	conditionList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "%VAT_SALE"));
	EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	EntityFindOptions findOptions = new EntityFindOptions();
	findOptions.setDistinct(true);
	Set fieldsToSelect = UtilMisc.toSet("productId", "taxPercentage");
	productPrice = delegator.findList("ProductPrice", condition1,  fieldsToSelect, ["taxPercentage"], findOptions, false);
	
	List vatList = EntityUtil.getFieldListFromEntityList(productPrice, "taxPercentage", true);
	context.vatList = vatList;
	
	vatWiseProductTotals = [:];
	
	for(i=0; i<vatList.size(); i++){
		
		List vatWiseProductList = EntityUtil.filterByAnd(productPrice, UtilMisc.toMap("taxPercentage", vatList.get(i)));
		List vatWiseProducts = EntityUtil.getFieldListFromEntityList(vatWiseProductList, "productId", true);
		
		resultMap = ByProductReportServices.getByProductPeriodTotals(dctx, UtilMisc.toMap("userLogin",userLogin,"fromDate", monthBegin, "thruDate", monthEnd, "productList", vatWiseProducts));
		periodTotalsMap = (resultMap.get("periodTotalsMap")).get("prodListCat");
		
		if(UtilValidate.isNotEmpty(periodTotalsMap)){
			tempPeriodTotalsMap = [:];
			tempPeriodTotalsMap.putAll(periodTotalsMap);
		
			vatWiseProductTotals.put(vatList.get(i), tempPeriodTotalsMap);
		}
		
	}
	context.vatWiseProductTotals = vatWiseProductTotals;
	
	conditionList.clear();
/*	conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "BYPROD_FA_CAT"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "BYPROD_SO"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "BYPROD_GIFT"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "SP_SALES"));
	
	EntityCondition condition2=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	facilityCatEnum = delegator.findList("Enumeration", condition2, null, ["sequenceId"], null, false);*/
	
	conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "BOOTH_CAT_TYPE"));
	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	facilityCatEnum = delegator.findList("Enumeration", condition, null, ["sequenceId"], null, false);
	
	facilityCategoryList = EntityUtil.getFieldListFromEntityList(facilityCatEnum, "enumId", false);
	
	//facilityCategoryList.addAll("SOrderAndOthers");
	context.facilityCategoryList = facilityCategoryList;
	
	
	
	


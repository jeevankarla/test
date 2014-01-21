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
	import javolution.util.FastList;
	import org.ofbiz.service.ServiceUtil;
	import in.vasista.vbiz.byproducts.ByProductServices;
    import in.vasista.vbiz.byproducts.ByProductReportServices;
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
		
	customTimePeriod = parameters.customTimePeriodId;
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	
	/*fromDateTime=UtilDateTime.nowTimestamp();
	thruDateTime=UtilDateTime.nowTimestamp();*/
	
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	
	context.putAt("fromDateTime", fromDateTime);
	
	monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	
	monthDate = UtilDateTime.toDateString(monthBegin, "MMMMM - yyyy");
	context.monthDate = monthDate;
	
    List productList = [];
    productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
		
	resultMap = ByProductReportServices.getByProductPeriodTotals(dctx, UtilMisc.toMap("userLogin",userLogin,"fromDate", monthBegin, "thruDate", monthEnd,"productList",productList));
	
	periodTotalsMap = resultMap.get("periodTotalsMap");
	context.periodTotalsMap = periodTotalsMap;
	List conditionList= FastList.newInstance();
	categoryNotIn = ["SHOPS","RENTAL","WHOLESALES_DEALER"];
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_IN, categoryNotIn));
	conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "BOOTH_CAT_TYPE"));
	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	facilityCatEnum = delegator.findList("Enumeration", condition, null, ["sequenceId"], null, false);
	
	facilityCategoryList = EntityUtil.getFieldListFromEntityList(facilityCatEnum, "enumId", false);
	
	grandTotalsMap = [:];
	for(i=0; i<facilityCategoryList.size(); i++){
		
		categoryTypeEnum = facilityCategoryList.get(i);
		
		ProductTotalsMap = (ByProductReportServices.getByProductPeriodTotals(dctx, UtilMisc.toMap("userLogin",userLogin,"fromDate", monthBegin, "thruDate", monthEnd, "categoryTypeEnum", categoryTypeEnum))).get("productWiseTotalsMap");
		categoryWiseTotalsMap = ProductTotalsMap.get("Total");
		
		grandTotalsMap.put(categoryTypeEnum, categoryWiseTotalsMap);
	}
	context.grandTotalsMap = grandTotalsMap;
	context.facilityCategoryList = facilityCategoryList;
	unionCategoriesList=[];
	/*unionCategories = delegator.findList("ProductCategoryAttribute", EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "UNION_TAXABLE"),null, null, null, false);
	unionCatList = EntityUtil.getFieldListFromEntityList(unionCategories, "productCategoryId", false);
	
	unionCategoriesSeqList = delegator.findList("ProductCategory", EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, unionCatList),UtilMisc.toSet("productCategoryId"), ["sequenceNum"], null, false);
	unionCategoriesList = EntityUtil.getFieldListFromEntityList(unionCategoriesSeqList, "productCategoryId", false);*/
	
	context.unionCategoriesList = unionCategoriesList;


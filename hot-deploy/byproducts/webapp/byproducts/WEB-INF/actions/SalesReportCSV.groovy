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
	productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
	
	context.putAt("fromDateTime", fromDateTime);
	
	monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	
	monthDate = UtilDateTime.toDateString(monthBegin, "MMMMM - yyyy");
	context.monthDate = monthDate;
	
		
	resultMap = ByProductReportServices.getByProductPeriodTotals(dctx, UtilMisc.toMap("fromDate", monthBegin, "thruDate", monthEnd, "productList", productList));
	periodTotalsMap = (resultMap.get("periodTotalsMap")).get("prodListCat");
	
	
	
	context.periodTotalsMap = periodTotalsMap;
	
	productDetailMap = periodTotalsMap.get("B01");
	Debug.log("productDetailMap==============================================="+productDetailMap);
	
	/*for(i=0; i<1; i++){
		
		productDetailMap = [:];
		
		
	}*/
	
	
	asdfasfd;
	
	
	
	
	List conditionList= FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "BYPROD_FA_CAT"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "BYPROD_SO"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "BYPROD_GIFT"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "SP_SALES"));
	
	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	facilityCatEnum = delegator.findList("Enumeration", condition, null, ["sequenceId"], null, false);
	
	facilityCategoryList = EntityUtil.getFieldListFromEntityList(facilityCatEnum, "enumId", false);
	facilityCategoryList.addAll("SOrderAndOthers");
	context.facilityCategoryList = facilityCategoryList;
	
	
	
	
		


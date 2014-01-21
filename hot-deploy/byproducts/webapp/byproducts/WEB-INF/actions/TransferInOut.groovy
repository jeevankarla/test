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
	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.util.EntityUtil;
	import java.util.*;
	import java.lang.*;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import java.sql.*;
	import java.util.Calendar;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import org.ofbiz.service.DispatchContext;
	import java.math.BigDecimal;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
	import  org.ofbiz.network.NetworkServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	import in.vasista.vbiz.byproducts.ByProductReportServices;

	transferType = parameters.transferType;
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	
	monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	reportForMonth = UtilDateTime.toDateString(monthBegin, "MMMMM, yyyy");
	context.reportForMonth = reportForMonth;
	dctx = dispatcher.getDispatchContext();
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	inventoryFacility = delegator.findList("ProductStore", EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId), null, null, null, false);
	inventoryFacilityId = EntityUtil.getFirst(inventoryFacility).get("inventoryFacilityId");

		intervelDays = (UtilDateTime.getIntervalInDays(monthBegin, monthEnd)+1);
	daysList = [];
	for(k =1;k<=intervelDays;k++){
		day = UtilDateTime.getDayOfMonth(UtilDateTime.addDaysToTimestamp(monthBegin, k-1), timeZone, locale);
		daysList.add(day);
	}
	context.put("daysList",daysList);
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("saleDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin));
	conditionList.add(EntityCondition.makeCondition("saleDate", EntityOperator.LESS_THAN_EQUAL_TO, monthEnd));
	if(transferType == "transferIn"){
		conditionList.add(EntityCondition.makeCondition("xferIn", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
	}else{
		conditionList.add(EntityCondition.makeCondition("xferOut", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.NOT_EQUAL, inventoryFacilityId));
	}
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	inventorySummaryDetails = delegator.findList("InventorySummary", condition, null, null, null, false);
	boothList = [];
	transferMap = [:];
	facilityPriceMap = [:];
	classificationMap = [:];
	
	if(inventorySummaryDetails){
		boothList = EntityUtil.getFieldListFromEntityList(inventorySummaryDetails, "facilityId", true);
		
		if(boothList){
			boothList.each{eachFacility ->
				classifyGroupId = "";
				facilityParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", eachFacility), false);
				if(facilityParty){
					partyId = facilityParty.getString("ownerPartyId");
					partyClassificationGroup = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false);
					partyClassificationGroup = EntityUtil.filterByDate(partyClassificationGroup, monthBegin);
					if(partyClassificationGroup){
						classifyGroupId = EntityUtil.getFirst(partyClassificationGroup).get("partyClassificationGroupId");
					}
				}
				if(classificationMap.get(classifyGroupId)){
					tempMap = [:];
					tempMap = classificationMap.get(classifyGroupId);
					facilityPriceMap.put(eachFacility, tempMap);
				}else{
					productsPrice = ByProductReportServices.getByProductPricesForFacility(dctx, UtilMisc.toMap("facilityId", eachFacility, "priceDate", monthBegin)).get("productsPrice");
					facilityPriceMap.put(eachFacility, productsPrice);
					classificationMap.put(classifyGroupId, productsPrice);
				}
			}
		}
		context.facilityPriceMap = facilityPriceMap;
		boothList.each{eachBooth ->
			productMap = [:];
			boothTransferDetail = EntityUtil.filterByCondition(inventorySummaryDetails, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachBooth));
			boothTransferDetail.each{ eachTransfer ->
				salesDate = eachTransfer.get("saleDate");
				day = UtilDateTime.getDayOfMonth(salesDate, timeZone, locale);
				productId = eachTransfer.get("productId");
				transferQty = BigDecimal.ZERO;
				if(transferType == "transferIn"){
					transferQty = eachTransfer.get("xferIn");
				}else{
					transferQty = eachTransfer.get("xferOut");
				}
				if(productMap.containsKey(productId)){
					tempTransfer = productMap.get(productId);
					temp = [:];
					temp.putAt(day,transferQty);
					tempTransfer.putAll(temp);
					productMap.putAt(productId, tempTransfer);
				}else{
					tempMap = [:];	
					tempMap.putAt(day, transferQty);
					productMap.putAt(productId, tempMap)
				}
			}
			transferMap.putAt(eachBooth, productMap);
		}
	}
	context.transferMap = transferMap;
	products = delegator.findList("Product", null, ["productId", "productName"]as Set, null, null, false);
	if(products){
		productDesc = [:];
		products.each{eachProd ->
			productDesc.putAt(eachProd.productId, eachProd.productName);
		}
		context.productDesc = productDesc;
	}
	
	return "success";
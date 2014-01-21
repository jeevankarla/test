	
	import org.ofbiz.base.util.UtilMisc;
	
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
	import org.ofbiz.base.util.UtilNumber;
	import org.ofbiz.network.NetworkServices;
	import java.math.RoundingMode;
	import java.util.Map;
	import org.ofbiz.entity.util.EntityFindOptions;
	import org.ofbiz.service.ServiceUtil;
	
	result = ServiceUtil.returnError(null);
	reportTypeFlag = null;
	if(parameters.reportTypeFlag){
		reportTypeFlag = parameters.reportTypeFlag;
	}
	if(reportTypeFlag == "vendorMarginReport"){
		if (!(security.hasEntityPermission("ACCOUNTING", "_ADMIN", session))) {
			context.errorMessage = "You don't have permission to run this report.";
			return "error";
		}
	}
	rounding = RoundingMode.HALF_UP;
	periodBillingId = null;
	if(parameters.periodBillingId){
		periodBillingId = parameters.periodBillingId;
	}else{
		context.errorMessage = "No PeriodBillingId Found";
		return;
	}
	facilityCommissionList = [];
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	context.put("fromDateTime",fromDateTime);
	dctx = dispatcher.getDispatchContext();
	monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	
	totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
	context.put("totalDays", totalDays+1);
	
	conditionList = [];
	boothsList=[];
	masterList=[];
	boothMarginRates =[:];
	
	conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS ,"SUB_PROD_TYPE"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL ,"CREDIT"));
	conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL ,"CASH_FS"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	productSubscriptionTypeList = delegator.findList("Enumeration", condition, ["enumId"] as Set, null, null, false);
	
	conditionList.clear();
	
	conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS , periodBillingId));
	conditionList.add(EntityCondition.makeCondition("commissionDate", EntityOperator.EQUALS , monthBegin));
	condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	EntityFindOptions findOptions = new EntityFindOptions();
	boothsList = delegator.findList("FacilityAndCommission",condition1,["facilityId"] as Set, UtilMisc.toList("parentFacilityId","facilityId"),findOptions,false);
	
	//intilizing map
	productMap = [:];
	dayTotalsMap = [:];
	typeAndCountMap =[:];
	
		productSubscriptionTypeList.each{ productSubscriptionTypeEntry ->
			typeAndCountMap[productSubscriptionTypeEntry.enumId] = 0;
		}
		typeAndCountMap["TOTAL"] = 0;
		productSubscriptionTypeList.each{ productSubscriptionTypeEntry ->
			typeAndCountMap[productSubscriptionTypeEntry.enumId+"_MR"] = 0;
		}
		typeAndCountMap["TOTAL_MR"] = 0;
		supplyDate = monthBegin;
		Map boothMargins= new LinkedHashMap();
		
		boothsList.each{booth ->
			vendorMarginReportList =[];
			dayTotalsMap = [:];
			
			for(int i=1 ; i <= (UtilDateTime.getIntervalInDays(monthBegin,monthEnd)+1); i++){
				dayOfMonth = i;
				dayTotalsMap[(String)i] = [:];
				dayTotalsMap[(String)i].putAll(typeAndCountMap);
			}
			dayTotalsMap["facilityId"] = booth.facilityId;
			dayTotalsMap["Tot"] =[:];
			dayTotalsMap["Tot"].putAll(typeAndCountMap);
			dayTotalsMap["Tot"].putAt("CASH_DUE",BigDecimal.ZERO);
			
			vendorMarginReportList.add(dayTotalsMap);
			boothMargins[booth.facilityId]=vendorMarginReportList;
			
		}
		facilityCommissionList = delegator.findList("FacilityCommission",EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId) , null, ["commissionDate"], null, false);
		
		facilityCommissionList.each { facilityCommission ->
			facilityId = facilityCommission.facilityId;
			
			dayTotalsList = boothMargins[facilityId];
			dayTotalsMap = dayTotalsList.get(0);
			totalsMap =[:];
			totalsMap = dayTotalsMap["Tot"];
			int dayInteger = UtilDateTime.getDayOfMonth((facilityCommission.commissionDate),timeZone, locale);
			typeAndCount =[:];
			typeAndCount = dayTotalsMap[(String)dayInteger];
			
			typeAndCount["CARD"] = (facilityCommission.cardQty).setScale(1, rounding);
			totalsMap["CARD"] += (facilityCommission.cardQty).setScale(1, rounding);
			typeAndCount["CASH"] = (facilityCommission.cashQty).setScale(1, rounding);
			totalsMap["CASH"] += (facilityCommission.cashQty).setScale(1, rounding);
			typeAndCount["SPECIAL_ORDER"] = (facilityCommission.splOrderQty).setScale(1, rounding);
			totalsMap["SPECIAL_ORDER"] += (facilityCommission.splOrderQty).setScale(1, rounding);
			typeAndCount["TOTAL"] = (facilityCommission.totalQty).setScale(1, rounding);
			totalsMap["TOTAL"] += (facilityCommission.totalQty).setScale(1, rounding);
			typeAndCount["CARD_MR"] = (facilityCommission.cardAmount).setScale(2, rounding);
			totalsMap["CARD_MR"] += (facilityCommission.cardAmount).setScale(2, rounding);
			typeAndCount["CASH_MR"] = (facilityCommission.cashAmount).setScale(2, rounding);
			totalsMap["CASH_MR"] += (facilityCommission.cashAmount).setScale(2, rounding);
			typeAndCount["SPECIAL_ORDER_MR"] = (facilityCommission.splOrderAmount).setScale(2, rounding);
			totalsMap["SPECIAL_ORDER_MR"] += (facilityCommission.splOrderAmount).setScale(2, rounding);
			typeAndCount["TOTAL_MR"] = facilityCommission.totalAmount;
			totalsMap["TOTAL_MR"] += (facilityCommission.totalAmount);
			
			if(dayInteger == totalDays+1){
			   totalsMap["CASH_DUE"] = (facilityCommission.dues);
		    }
		}
	masterList.add(boothMargins);
	context.put("masterList", masterList);

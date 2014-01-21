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
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.util.EntityUtil;
	import java.util.*;
	import org.ofbiz.base.util.UtilValidate;
	import java.lang.*;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import java.sql.*;
	import java.util.Calendar;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.SimpleDateFormat;
	import org.ofbiz.base.util.UtilNumber;
	import java.math.RoundingMode;
	import java.util.Map;
	import org.ofbiz.entity.util.EntityFindOptions;
	import org.ofbiz.party.party.PartyHelper;

	conditionList = [];
	ecsDataMap = [:];
	ecsReportList = [];
	ecsHeader = [:];
	totalMonthMargin = 0;
	errorList = [];
	
	date = new Date();
	int day = date.getAt(Calendar.DAY_OF_MONTH);
	int month = date.getAt(Calendar.MONTH);
	int year = date.getAt(Calendar.YEAR);
	
	customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDate = customTimePeriod.getDate("fromDate");
	custYear = (fromDate).toString().substring(2,4);
	custMonth = (fromDate).toString().substring(5,7);
	
	try{
		periodBillingDetail =  delegator.findOne("PeriodBilling", [periodBillingId:parameters.periodBillingId], false);
		
		if (UtilValidate.isEmpty(periodBillingDetail)) {
			request.setAttribute("_ERROR_MESSAGE_", "PeriodBillingId Does not exist!");
			context.showScreen = "message";
			return;
		}
		conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"BOOTH"));
		conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS ,"VENDOR"));
		conditionList.add(EntityCondition.makeCondition("useEcs", EntityOperator.EQUALS ,"Y"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		boothsList = delegator.findList("Facility",condition,["facilityId","ownerPartyId","parentFacilityId"] as Set,["parentFacilityId", "facilityId"],null,false);
		
		for(int j=0; j < boothsList.size(); j++){
			booth = boothsList.get(j);
			parentFacilityId = booth.parentFacilityId;
			facilityId = booth.facilityId;
				
			conditionFinList = [];
			conditionFinList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,booth.ownerPartyId));
			conditionFinList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
			conditionFinList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
			conditionFinList.add(EntityCondition.makeCondition("micrNumber", EntityOperator.NOT_EQUAL,null));
			conditionFinList.add(EntityCondition.makeCondition("finAccountCode", EntityOperator.NOT_EQUAL,null));
			cond = EntityCondition.makeCondition(conditionFinList,EntityOperator.AND);
			finAccountsVendorList = delegator.findList("FinAccount",cond,null,null,null,false);
				
			if(finAccountsVendorList){
				//for mic and acc number
				int count = 0;
				finAccountsVendorList.each { finAccItem ->
					if(finAccItem.get("categoryTypeEnum").equals("SAVINGS")){
						micrNo = (finAccountsVendorList.micrNumber).toString();
						micrNumber = (micrNo.concat("10")).replace(']', '').replace('[', '').replace(' ', '');
					}
					if(finAccItem.get("categoryTypeEnum").equals("CURRENT")){
						micrNo = (finAccountsVendorList.micrNumber).toString();
						micrNumber = micrNo.concat("11").replace(']', '').replace('[', '').replace(' ', '');
					}
					ecsDataMap["micrNumber"] = micrNumber;
					ecsDataMap["accNumber"] = finAccountsVendorList.getAt("finAccountCode").get(count);
					count++;
				}
					//for route and booth
					//to get AB01 from route AB1
					
				ecsDataMap["vendorName"] = PartyHelper.getPartyName(delegator, booth.ownerPartyId, true);
					
				routeId= booth.parentFacilityId;
				length = routeId.length();
					
				if(length<4){
					routeIdNum = routeId.substring(2,3);
				}
				else{
					routeIdNum = routeId.substring(2,4);
				}
					
				routeIdNumPad = routeIdNum.toString().padLeft(2, '0');
				routeIdFinal = (routeId.substring(0,2)).concat(routeIdNumPad);
				ecsDataMap["routeId"] = routeIdFinal;
				ecsDataMap["boothId"] = facilityId;
				//for netmargin
					
				conditionFCList = [];
				conditionFCList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,facilityId));
				conditionFCList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,periodBillingDetail.periodBillingId));
				condFc = EntityCondition.makeCondition(conditionFCList,EntityOperator.AND);
				facilityCommissionList = delegator.findList("FacilityCommission",condFc,null,null,null,false);
					
				if (UtilValidate.isEmpty(facilityCommissionList)) {
					Debug.logWarning("facilityCommission empty for facility "+facilityId, null);
					continue;
				}
					
				totMargin = 0;
				totCashDue = 0;
				for (int i=0; i < facilityCommissionList.size(); i++) {
					facilityCommissionItem = facilityCommissionList.get(i);
					cashDue = 0.00;
					margin = 0.00;
					if((facilityCommissionItem.totalAmount) != null){
						margin = facilityCommissionItem.totalAmount;
					}
					if((facilityCommissionItem.dues) != null){
						cashDue = facilityCommissionItem.dues;
					}
					totMargin += margin;
					totCashDue += cashDue;
				}
				netMargin = totMargin - totCashDue;
				netMarginRound = Math.round(netMargin);
				netMarPad= netMarginRound.toString().padLeft(11, '0');
					
				if (netMargin > 0) {
					totalMonthMargin += netMarginRound;
					ecsDataMap["netMargin"] = netMarPad;
					tempMap = [:];
					tempMap.putAll(ecsDataMap);
					ecsReportList.add(tempMap);
				}
				       
			}else{    
				errorList.add(facilityId);
			}
		}
	} catch (Exception e) {
	//ignore
	}
	ecsHeader["totalMarginForMonth"] = totalMonthMargin*100;
	ecsHeader["day"] = day;
	ecsHeader["month"] = month;
	ecsHeader["year"] = year;
	ecsHeader["custYear"] = custYear;
	ecsHeader["custMonth"] = custMonth;

	//for control on spaces for formatting we use stringbuffer
	String BR = System.getProperty("line.separator");
	StringBuffer ecsBuffer = new StringBuffer();
	
	ecsBuffer.append("115008152APDAIRY DEVELOPMENT CO-OP FEDERATION LTDVENDORS MARGIN         500240002000212320005869   00001"+(ecsHeader.get("totalMarginForMonth")).toString().padLeft(21, '0')+(ecsHeader.get("day")).toString().padLeft(2, '0')+(ecsHeader.get("month")+1).toString().padLeft(2, '0')+(ecsHeader.get("year"))).append(BR);
	
	for (int i=0; i < ecsReportList.size(); i++){
		ecsmasterItem = ecsReportList.get(i);
		ecsBuffer.append("22"+(ecsmasterItem.get("micrNumber")).toString().padRight(14, ' '));
		ecsBuffer.append((ecsmasterItem.get("accNumber")).toString().padRight(15, ' ').subSequence(0, 15));
		ecsBuffer.append((ecsmasterItem.get("vendorName")).replace(',', '').padRight(40, ' '));
		ecsBuffer.append(("5002400025008152APDDCF-LALAPET-HYD").padRight(36, ' '));
		ecsBuffer.append(ecsmasterItem.get("routeId"));
		ecsBuffer.append(ecsmasterItem.get("boothId")+"-");
		ecsBuffer.append((ecsHeader.get("custMonth")).toString()+(ecsHeader.get("custYear")).toString()+(ecsmasterItem.get("netMargin")).toString()+"00");
		ecsBuffer.append(BR);
	}
	context.put("ecsBuffer", ecsBuffer);
	//Debug.logInfo("ecsBuffer=========================================================>"+ecsBuffer,"");
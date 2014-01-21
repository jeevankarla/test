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
chequesDataMap = [:];
chequesReportList = [];

customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate = customTimePeriod.getDate("fromDate");
custYear = (fromDate).toString().substring(0,4);
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
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("useEcs", EntityOperator.EQUALS,null),
			EntityCondition.makeCondition("useEcs", EntityOperator.EQUALS,"N")],EntityOperator.OR));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	boothsList = delegator.findList("Facility",condition,["facilityId","ownerPartyId","parentFacilityId"] as Set,["parentFacilityId", "facilityId"],null,false);
	
	for(int j=0; j < boothsList.size(); j++){
		booth = boothsList.get(j);
		facilityId = booth.facilityId;
		vendorName = PartyHelper.getPartyName(delegator, booth.ownerPartyId, true);
		chequesDataMap["allotee"] =  vendorName.replace(',', '');
		routeId= booth.parentFacilityId;
		length = routeId.length();
		if(length<4){
			routeIdNum = routeId.substring(2,3);
		}
		else{
			routeIdNum = routeId.substring(2,4);
		}
		zone = routeId.substring(0,2);
		boothId = facilityId.padRight(4);
		routeIdNumPad = routeIdNum.toString().padLeft(2, ' ');
		routeIdFinal = (zone).concat("-").concat(routeIdNumPad);
		ref = routeIdFinal.concat("-").concat(boothId).padRight(5, ' ');
		date = custMonth.concat("/").concat(custYear);
		chequesDataMap["ref"] = ref.concat("-").concat(date);
			
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
		
		if (netMargin > 0) {
			chequesDataMap["draftAmount"] = netMarginRound;
			chequesDataMap["beneficiaryBankName"] = "HYDERABAD";
			tempChequesDataMap = [:];
			tempChequesDataMap.putAll(chequesDataMap);
			chequesReportList.add(tempChequesDataMap);
		}
	}	 
} catch (Exception e) {
//ignore
}
context.chequesReportList = chequesReportList;
//Debug.logInfo("chequesDataMapReportList=========================================================>"+chequesReportList,"");
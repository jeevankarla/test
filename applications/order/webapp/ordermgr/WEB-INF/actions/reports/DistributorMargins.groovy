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
import java.util.Map;
import org.ofbiz.base.util.UtilMisc;
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
import org.ofbiz.network.NetworkServices;
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
context.put("fromDateTime",fromDateTime);
conditionList = [];
distributorsMarginReportList=[];
distributorMarginMap =[:];
masterList=[];

monthBegin = UtilDateTime.getMonthStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getMonthEnd(fromDateTime, timeZone, locale);


conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"DISTRIBUTOR"));
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.NOT_EQUAL,"Company"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
distributorsList = delegator.findList("Facility",condition,null,null,null,false);

//to get all zone Rate  amount into map
zones = NetworkServices.getZones(delegator);
zonesList = zones.zonesList;
zonesRateAmount =[:];
Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
inputRateAmt.put("periodTypeId", "RATE_HOUR");
inputRateAmt.put("rateCurrencyUomId", context.get("currencyUomId"));

for( j=0 ; j < zonesList.size() ; j++){
	inputRateAmt.put("rateTypeId", zonesList.getAt(j)+"_ZN_MRGN");	
	rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);
	zonesRateAmount[zonesList.getAt(j)]=rateAmount.rateAmount;
}
zoneReportList =[];
for( k=0 ; k <= (UtilDateTime.getIntervalInDays(monthBegin,monthEnd)); k++){	
	populateDistributorMargins(UtilDateTime.addDaysToTimestamp(monthBegin,k));
}

def populateDistributorMargins(supplyDate){	
	
	dayOfMonth = UtilDateTime.getDayOfMonth(supplyDate, timeZone, locale);
	dayTotals = NetworkServices.getDayTotals(dispatcher.getDispatchContext(), supplyDate, false, false);
	zonesMap = dayTotals.zoneTotals;	
	Iterator mapIter = zonesMap.entrySet().iterator();	
	while (mapIter.hasNext()) {		
		dayOfMonthMap = [:];
		zoneMarginMap =[:];
		totalsMap = [:];
		Map.Entry entry = mapIter.next();
		GenericValue zoneDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", entry.getKey()), true);
		distributorId =  zoneDetail.parentFacilityId;		
		zoneMarginMap["total"] = entry.getValue().getAt("total");
		zoneMarginMap["TOTAL_MR"] = BigDecimal.ZERO;
		if(zonesRateAmount[entry.getKey()]){
			if(zoneDetail.isUpcountry == "Y" && zoneDetail.facilityId != "ZB"){
				zoneMarginMap["TOTAL_MR"] = new BigDecimal((zonesRateAmount[entry.getKey()])).setScale(2,BigDecimal.ROUND_HALF_UP);				
				
			}else{
				zoneMarginMap["TOTAL_MR"] = new BigDecimal((zoneMarginMap["total"] * (zonesRateAmount[entry.getKey()]))).setScale(2,BigDecimal.ROUND_HALF_UP);
			}
			
		}			
		
		if( distributorMarginMap[distributorId ] == null){
			dayOfMonthMap[dayOfMonth] =  zoneMarginMap;	
			
		}else{			
			runningTotalMap =[:];
			runningTotalMap = distributorMarginMap[distributorId];
			dayOfMonthMap.putAll(runningTotalMap);			
			if(runningTotalMap[dayOfMonth] == null){
				dayOfMonthMap[dayOfMonth] =  zoneMarginMap;				
			}else{
				tempRunningTotalMap =[:];
				tempRunningTotalMap = runningTotalMap[dayOfMonth];				
				tempRunningTotalMap["total"] += zoneMarginMap["total"];
				tempRunningTotalMap["TOTAL_MR"] += zoneMarginMap["TOTAL_MR"];
				dayOfMonthMap[dayOfMonth] =  tempRunningTotalMap;
			}
				
		}	
		distributorMarginMap[distributorId] =   dayOfMonthMap;	
		
	}
	
}
distributorsMarginReportList.add(distributorMarginMap);
context.put("masterList", distributorsMarginReportList);



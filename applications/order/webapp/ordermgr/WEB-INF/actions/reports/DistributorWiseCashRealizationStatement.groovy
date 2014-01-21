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

import java.io.ObjectOutputStream.DebugTraceInfoStack;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

dctx = dispatcher.getDispatchContext();
dayTotals =[:];
zoneTotals =[:];
zoneWiseTotalsMap=[:];
conditionList =[];
 isPreviousFlag = 'N';
 rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
//Grand total Map
distributorGRTotalValueMap = [:];

distributorGRTotalValueMap["PTC_VAL"] = 0;
distributorGRTotalValueMap["CASH_QTY"] =0;
distributorGRTotalValueMap["CASH_VAL"] = 0;
distributorGRTotalValueMap["CARD_QTY"] =0;
distributorGRTotalValueMap["RNDIFF_VAL"] = 0;
distributorGRTotalValueMap["TRSP_DISC"] = 0;
distributorGRTotalValueMap["TDS"] = 0;
distributorGRTotalValueMap["REM_ESEVA"] = 0;
distributorGRTotalValueMap["REM_APONLN"] = 0;
distributorGRTotalValueMap["CHRG_ESEVA"] = 0;
distributorGRTotalValueMap["CHRG_APONLN"] = 0;

distributorGRTotalValueMap["TOTAL_QTY"] = 0;
distributorGRTotalValueMap["DDCCASH_VAL"] =0;
distributorGRTotalValueMap["CCASH_RND_VAL"] = 0;
distributorGRTotalValueMap["NET_VAL"]=0;

if(parameters.supplyDate){
	supplyDate = parameters.supplyDate;
}
if(context.supplyDate){
	supplyDate = context.supplyDate;
}

if(parameters.isPreviousFlag){
	isPreviousFlag = parameters.isPreviousFlag;
}
if(context.isPreviousFlag){
	isPreviousFlag = context.isPreviousFlag;
}


def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
try {
	supplyDateTime = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
	
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+supplyDate, "");
   
}
if(isPreviousFlag == 'Y'){
	supplyDateTime=UtilDateTime.getDayStart(supplyDateTime,-1);	
}
previousSupplyDateStr=UtilDateTime.toDateString(supplyDateTime, "yyyy-MM-dd");
context.put("supplyDateTime",supplyDateTime);
pmShipDate=UtilDateTime.getDayStart(supplyDateTime,-1);
context.put("pmShipDate",pmShipDate);
conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS ,"SUB_PROD_TYPE"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL ,"CREDIT"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productSubscriptionTypeList = delegator.findList("Enumeration", condition, ["enumId"] as Set, null, null, false);

dayTotals = NetworkServices.getDayTotals(dctx, supplyDateTime,false ,false);
shipmentIds = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(supplyDateTime, "yyyy-MM-dd HH:mm:ss"), null);
distributorTotals = dayTotals["distributorTotals"];
zoneTotals = dayTotals["zoneTotals"];

totalApddcfCollectionCashValue = 0;
distributorWiseTotalsMap =[:];

Iterator treeMapIter = distributorTotals.entrySet().iterator();
while (treeMapIter.hasNext()) {
	Map.Entry entry = treeMapIter.next();
	upCountryValue = 0;
	upCountryRoundedValue = 0;
	
		Iterator treeMapIterInner = entry.getValue().entrySet().iterator();
		distributorTotalValueMap =[:];
		ptcBoothIds =[];	
		
		List<GenericValue>  zoneFacilities= delegator.findList("Facility", EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, entry.getKey()), null, UtilMisc.toList("sequenceNum","facilityName"), null, false);
		//Debug.logInfo("zoneFacilities==================>"+zoneFacilities,"");
		zoneFacilities.each{ zoneFacility ->
			//Need to clean up this code add new flag to Facility Entity to specify the upcountry
			if(zoneFacility.isUpcountry == "Y"){
				upCountryValue += ((zoneTotals.get(zoneFacility.facilityId))["CASH"]).getAt("totalRevenue");
				upCountryRoundedValue +=(new BigDecimal(((zoneTotals.get(zoneFacility.facilityId))["CASH"]).getAt("totalRevenue"))).setScale(0,BigDecimal.ROUND_HALF_UP);
			}else{
				ptcBoothIds.addAll(NetworkServices.getZoneBooths(delegator,zoneFacility.facilityId ,"PTC"));				
			}
		}	
		ptcCashTotal =0;
		ptcCashRoundedTotal =0;
		boothPaymentsList=FastList.newInstance();
		//PTC BOOTHS Total Amount calculation
		for(i=0;i< ptcBoothIds.size() ;i++){
			Map boothResult = NetworkServices.getBoothPayments( delegator, dctx.getDispatcher(), userLogin, previousSupplyDateStr, null, ptcBoothIds[i], null, Boolean.FALSE);
			if (boothResult) {
				boothPaymentsList  = boothResult.get("boothPaymentsList");
				boothPaymentsList.each{ boothPayments ->
				   boothDueAmt = boothPayments.get("grandTotal");				   
				   if ((Double)boothDueAmt != 0) {
					ptcCashTotal +=boothDueAmt;
					ptcCashRoundedTotal += (new BigDecimal(boothDueAmt)).setScale(0,rounding);				
				   }
				}
			}		
		}// end of for 
		distributorTotalValueMap["PTC_VAL"] = ptcCashTotal;	
		distributorGRTotalValueMap["PTC_VAL"] += distributorTotalValueMap["PTC_VAL"];
		
		distributorTotalValueMap["RNDIFF_VAL"] = (ptcCashRoundedTotal-ptcCashTotal)+(upCountryRoundedValue-upCountryValue);
		distributorTotalValueMap["TRSP_DISC"] = 0;
		distributorTotalValueMap["TDS"] = 0;
		distributorTotalValueMap["REM_ESEVA"] = 0;
		distributorTotalValueMap["REM_APONLN"] = 0;
		distributorTotalValueMap["CHRG_ESEVA"] = 0;
		distributorTotalValueMap["CHRG_APONLN"] = 0;
		distributorTotalValueMap["CASH_VAL"] =BigDecimal.ZERO;
		distributorTotalValueMap["DDCCASH_VAL"] =0;
		distributorTotalValueMap["CASH_QTY"] = 0;
		
		while (treeMapIterInner.hasNext()) {
			Map.Entry typeEntry = treeMapIterInner.next();
			if((typeEntry.getKey()).equals("CASH")){
				
				if(entry.getKey() != "APDDCF"){
					distributorTotalValueMap["CASH_VAL"] = (new BigDecimal(typeEntry.getValue().getAt("totalRevenue"))).setScale(2, BigDecimal.ROUND_UP);
					distributorGRTotalValueMap["CASH_VAL"] += distributorTotalValueMap["CASH_VAL"];
					distributorTotalValueMap["CASH_QTY"] = typeEntry.getValue().getAt("total");
					distributorGRTotalValueMap["CASH_QTY"] += distributorTotalValueMap["CASH_QTY"];
				}			
				
			}
			if((typeEntry.getKey()).equals("CARD")){
				distributorTotalValueMap["CARD_QTY"] = typeEntry.getValue().getAt("total");
				distributorGRTotalValueMap["CARD_QTY"] += distributorTotalValueMap["CARD_QTY"];
			}
			if((typeEntry.getKey()).equals("total")){
				distributorTotalValueMap["TOTAL_QTY"] = typeEntry.getValue();
				distributorGRTotalValueMap["TOTAL_QTY"] += distributorTotalValueMap["TOTAL_QTY"];
			}
		}
		if(entry.getKey() != "APDDCF-B"){
			distributorTotalValueMap["DDCCASH_VAL"] = new BigDecimal(distributorTotalValueMap["CASH_VAL"] -(upCountryValue+distributorTotalValueMap["PTC_VAL"])).setScale(2 ,BigDecimal.ROUND_HALF_UP);
		}
		
		distributorGRTotalValueMap["DDCCASH_VAL"] += distributorTotalValueMap["DDCCASH_VAL"];
		totalApddcfCollectionCashValue += distributorTotalValueMap["DDCCASH_VAL"];
		
		distributorTotalValueMap["CCASH_RND_VAL"] = (distributorTotalValueMap["RNDIFF_VAL"] +distributorTotalValueMap["PTC_VAL"]+upCountryValue);
		distributorGRTotalValueMap["CCASH_RND_VAL"] += distributorTotalValueMap["CCASH_RND_VAL"];
		distributorTotalValueMap["TRSP_DISC"] = 0;	
		
		distributorTotalValueMap["NET_VAL"] = (distributorTotalValueMap["CCASH_RND_VAL"] - distributorTotalValueMap["TRSP_DISC"]+distributorTotalValueMap["TDS"]-(distributorTotalValueMap["REM_ESEVA"]+distributorTotalValueMap["REM_APONLN"])+(distributorTotalValueMap["CHRG_ESEVA"]+distributorTotalValueMap["CHRG_APONLN"]));
		distributorGRTotalValueMap["NET_VAL"] += distributorTotalValueMap["NET_VAL"];
		distributorWiseTotalsMap[entry.getKey()] = distributorTotalValueMap;
	
}

//APDDC Collection MAP KEY AS APDDCF_COL
distributorTotalValueMap =[:];
distributorTotalValueMap["PTC_VAL"] = 0;
distributorTotalValueMap["RNDIFF_VAL"] = 0;
distributorTotalValueMap["TRSP_DISC"] = 0;
distributorTotalValueMap["TDS"] = 0;
distributorTotalValueMap["REM_ESEVA"] = 0;
distributorTotalValueMap["REM_APONLN"] = 0;
distributorTotalValueMap["CHRG_ESEVA"] = 0;
distributorTotalValueMap["CHRG_APONLN"] = 0;
distributorTotalValueMap["CARD_QTY"] = 0;
distributorTotalValueMap["CASH_QTY"] =0;
distributorTotalValueMap["CASH_VAL"] = 0;

distributorTotalValueMap["TOTAL_QTY"] = 0;

distributorTotalValueMap["DDCCASH_VAL"] = 0;

distributorTotalValueMap["CCASH_RND_VAL"] = totalApddcfCollectionCashValue;
distributorGRTotalValueMap["CCASH_RND_VAL"] += distributorTotalValueMap["CCASH_RND_VAL"];

if(isPreviousFlag == 'Y'){
	
	boothsPaymentsEsevaDetail = NetworkServices.getBoothPaidPayments( dctx , [paymentDate:previousSupplyDateStr , paymentMethodTypeId:"ESEVA_PAYIN"]);
	boothsPaymentsApOnlineDetail = NetworkServices.getBoothPaidPayments( dctx , [paymentDate:previousSupplyDateStr , paymentMethodTypeId:"APONLINE_PAYIN"]);
	apOnLineBoothpaymentsList=boothsPaymentsApOnlineDetail.get("boothPaymentsList");
	EsevaBoothpaymentList=boothsPaymentsEsevaDetail.get("boothPaymentsList");
	
		distributorTotalValueMap["REM_ESEVA"] = boothsPaymentsEsevaDetail.get("invoicesTotalAmount");
		distributorTotalValueMap["REM_APONLN"] = boothsPaymentsApOnlineDetail.get("invoicesTotalAmount");
		distributorTotalValueMap["CHRG_ESEVA"] = (EsevaBoothpaymentList.size()*0.70);
		distributorTotalValueMap["CHRG_APONLN"] = (apOnLineBoothpaymentsList.size()*2);
		distributorGRTotalValueMap["REM_ESEVA"] += distributorTotalValueMap["REM_ESEVA"];
		distributorGRTotalValueMap["REM_APONLN"] += distributorTotalValueMap["REM_APONLN"];
		distributorGRTotalValueMap["CHRG_ESEVA"] += distributorTotalValueMap["CHRG_ESEVA"];
		distributorGRTotalValueMap["CHRG_APONLN"] += distributorTotalValueMap["CHRG_APONLN"];
}
distributorTotalValueMap["NET_VAL"] = (distributorTotalValueMap["CCASH_RND_VAL"] - distributorTotalValueMap["TRSP_DISC"]+distributorTotalValueMap["TDS"]-(distributorTotalValueMap["REM_ESEVA"]+	distributorTotalValueMap["REM_APONLN"]));
distributorGRTotalValueMap["NET_VAL"] += distributorTotalValueMap["NET_VAL"];

distributorWiseTotalsMap["APDDCF_COL"] = distributorTotalValueMap;
distributorGRTotalValueMap["CCASH_RND_VAL"] = (new BigDecimal(distributorGRTotalValueMap["CCASH_RND_VAL"])).setScale(2,rounding);
distributorGRTotalValueMap["NET_VAL"] = (new BigDecimal(distributorGRTotalValueMap["CCASH_RND_VAL"] -(distributorTotalValueMap["REM_ESEVA"]+distributorTotalValueMap["REM_APONLN"]))).setScale(2,rounding);
distributorWiseTotalsMap["DayTotals"] = distributorGRTotalValueMap;
context.distributorWiseTotalsMap = distributorWiseTotalsMap;








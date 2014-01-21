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
import java.math.BigDecimal;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.service.DispatchContext;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

dctx = dispatcher.getDispatchContext();
dayTotals =[:];
zoneTotals =[:];
zoneWiseTotalsMap=[:];
conditionList =[];

supplyDate = parameters.supplyDate;
if(parameters.supplyDate){
	supplyDate = parameters.supplyDate;
}
if(context.supplyDate){
	supplyDate = context.supplyDate;
}
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Timestamp supplyDateTime = UtilDateTime.nowTimestamp();
//Timestamp thruDateTime = UtilDateTime.nowTimestamp();
try {
	supplyDateTime = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
	context.put("supplyDateTime",supplyDateTime);
	pmShipDate=UtilDateTime.getDayStart(supplyDateTime,-1);
    context.put("pmShipDate",pmShipDate);
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+supplyDate, "");
   
}
conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS ,"SUB_PROD_TYPE"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL ,"CREDIT"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL ,"CASH_FS"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productSubscriptionTypeList = delegator.findList("Enumeration", condition, ["enumId"] as Set, null, null, false);

dayTotals = NetworkServices.getDayTotals(dctx, supplyDateTime,false ,true);
shipmentIds = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(supplyDateTime, "yyyy-MM-dd HH:mm:ss"), null);
zoneTotals = dayTotals["zoneTotals"];
boothTotals = dayTotals["boothTotals"];
amBoothTotals = [:];

//getting default margin  value for card and cash
Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
inputRateAmt.put("rateTypeId", "VENDOR_MRGN");
inputRateAmt.put("periodTypeId", "RATE_HOUR");
inputRateAmt.put("rateCurrencyUomId", context.get("currencyUomId"));
rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);
normalMargin = rateAmount.rateAmount;

inputRateAmt.put("rateTypeId", "VENDOR_CD_ADTL_MRGN");
rateAmountForCard = dispatcher.runSync("getRateAmount", inputRateAmt);
cardMargin = rateAmountForCard.rateAmount;
zonesGRTotalValueMap = [:];

//intilizing Grand total Value Map 
zonesGRTotalValueMap["MRADJ"] = 0;
productSubscriptionTypeList.each { productSubscriptionType ->
	zonesGRTotalValueMap[productSubscriptionType.enumId+"_QTY"] =0;
	zonesGRTotalValueMap[productSubscriptionType.enumId+"_REVAL"] =0;
	zonesGRTotalValueMap[productSubscriptionType.enumId+"_MR"] = 0;	
	zonesGRTotalValueMap[productSubscriptionType.enumId+"_NETVAL"]=0; 
	
}
	
zonesGRTotalValueMap["CASH_VAL"] = 0;
zonesGRTotalValueMap["TOTAL_MR"] = 0;
zonesGRTotalValueMap["TOTAL_INCENTIVE"] = 0;
zonesGRTotalValueMap["TOTALCASH_MR"] = BigDecimal.ZERO;
zonesGRTotalValueMap["TOTALCARD_MR"] = BigDecimal.ZERO;
zonesGRTotalValueMap["TOTALSO_MR"] = BigDecimal.ZERO;


Iterator treeMapIter = zoneTotals.entrySet().iterator();
while (treeMapIter.hasNext()) {		
	Map.Entry entry = treeMapIter.next();
	
	zoneDetails = delegator.findOne("Facility", [facilityId : entry.getKey()], false);
	
	Iterator treeMapIterInner = entry.getValue().entrySet().iterator();
	zoneTotalValueMap = [:];
	totalMarginValue = BigDecimal.ZERO;
	totalCashMargin = BigDecimal.ZERO;
	totalCardMargin = BigDecimal.ZERO;
	totalSoMargin = BigDecimal.ZERO;
	totalIncentiveValue = BigDecimal.ZERO;
	totalCashMarginInCen = BigDecimal.ZERO;
	totalSoMarginInCen = BigDecimal.ZERO;
	totalCardMarginInCen = BigDecimal.ZERO;
	totalRoundedCashValue = BigDecimal.ZERO;
	totalRoundedCardValue = BigDecimal.ZERO;
	
	zoneTotalValueMap["MRADJ"] = BigDecimal.ZERO;
	ptcBoothIds =  NetworkServices.getZoneBooths(delegator,entry.getKey() ,"PTC");
	zoneBoothIds = NetworkServices.getZoneBooths(delegator,entry.getKey());
	List conditionList= FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, ptcBoothIds));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH"));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	//Debug.logInfo("condition=" + condition, module);
	orderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, null, null, false);
	// Margin calculation for evening sales we need adjust PTC margin for PTC and non PTC booths for evening sales
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, zoneBoothIds));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH"));
	conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL, "PTC"));
	conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "PM_SHIPMENT_SUPPL"));
	EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	pmOrderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition1, null, null, null, false);
	orderItems.addAll(pmOrderItems);
	ptcTotalQty =0;
	orderItems.each{ orderItem ->
		ptcTotalQty +=((orderItem.quantity)*(orderItem.quantityIncluded));			
	}
	zoneTotalValueMap["MRADJ"] = ptcTotalQty*normalMargin;
	zonesGRTotalValueMap["MRADJ"] +=zoneTotalValueMap["MRADJ"];
	//calculating Margins
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, zoneBoothIds));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.NOT_EQUAL, "CREDIT"));
	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "73"));
	//conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL, "PTC"));
	conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "AM_SHIPMENT"));
	EntityCondition con = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	amOrderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", con, null, null, null, false);
	amOrderItems.each { orderItem ->
		String boothId = orderItem.getString("originFacilityId");			
		String prodSubscriptionTypeId = orderItem.getString("productSubscriptionTypeId");
		BigDecimal quantity  = (orderItem.getBigDecimal("quantity")).multiply(orderItem.getBigDecimal("quantityIncluded"));
		BigDecimal price  = orderItem.getBigDecimal("unitPrice");
		BigDecimal revenue = price.multiply(quantity);
		if (amBoothTotals.get(boothId) == null) {
			Map<String, Object> newMap = FastMap.newInstance();
			
			newMap.put("total", quantity);
			newMap.put("totalRevenue", revenue);
			newMap.put("excludeIncentive", orderItem.getString("excludeIncentive"));
			newMap.put("categoryTypeEnum", orderItem.getString("categoryTypeEnum"));
			
			Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
			while(typeIter.hasNext()) {
				// initialize type maps
				GenericValue type = typeIter.next();
				Map<String, Object> typeMap = FastMap.newInstance();
				typeMap.put("total", BigDecimal.ZERO);
				typeMap.put("totalRevenue", BigDecimal.ZERO);
				newMap.put(type.getString("enumId"), typeMap);
			}
			Map typeMap = (Map)newMap.get(prodSubscriptionTypeId);
			typeMap.put("total", quantity);
			typeMap.put("totalRevenue", revenue);
			newMap.put(prodSubscriptionTypeId, typeMap);
			amBoothTotals.put(boothId, newMap);				
		}
		else {
			Map boothMap = (Map)amBoothTotals.get(boothId);
			BigDecimal runningTotal = (BigDecimal)boothMap.get("total");
			runningTotal = runningTotal.add(quantity);
			boothMap.put("total", runningTotal);
			BigDecimal runningTotalRevenue = (BigDecimal)boothMap.get("totalRevenue");
			runningTotalRevenue = runningTotalRevenue.add(revenue);
			boothMap.put("totalRevenue", runningTotalRevenue);
			// next handle type totals
			Map typeMap = (Map)boothMap.get(prodSubscriptionTypeId);
			runningTotal = (BigDecimal) typeMap.get("total");
			runningTotal = runningTotal.add(quantity);
			typeMap.put("total", runningTotal);
			runningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
			runningTotalRevenue = runningTotalRevenue.add(revenue);
			typeMap.put("totalRevenue", runningTotalRevenue);
		}	
		
}
	
	for(i=0 ;i< zoneBoothIds.size();i++){			
		boothMap = amBoothTotals.getAt(zoneBoothIds[i]);			
		boothMargin = normalMargin;			
		if(boothMap){
			if(boothMap["categoryTypeEnum"] !="VENDOR"){
				boothMargin =0;
			}
			incentiveValue =0;
			// checking wheather the booth has excludeIncentive
			if(boothMap["categoryTypeEnum"] =="VENDOR" && boothMap["excludeIncentive"] !="Y"){
				incentivesResult = dispatcher.runSync("evaluateAccountFormula", [acctgFormulaId: "LMS_VOL_INCNTV", variableValues: "QUANTITY="+"1", slabAmount : (boothMap["total"]), userLogin: userLogin]);
				
				if( incentivesResult.formulaResult > 0){
					incentiveValue = (incentivesResult.formulaResult);		
					
				}							
		}	
		totalCashMargin += (((boothMap["CASH"]).getAt("total"))*(boothMargin));
		totalSoMargin += (((boothMap["SPECIAL_ORDER"]).getAt("total"))*(boothMargin));
		totalCardMargin += (((boothMap["CARD"]).getAt("total"))*(boothMargin+cardMargin));
		totalIncentiveValue +=(boothMap["total"]*incentiveValue);
		
		totalCashMarginInCen += (((boothMap["CASH"]).getAt("total"))*(boothMargin+incentiveValue));
		totalSoMarginInCen += (((boothMap["SPECIAL_ORDER"]).getAt("total"))*(boothMargin+incentiveValue));
		totalCardMarginInCen +=(((boothMap["CARD"]).getAt("total"))*(boothMargin+cardMargin+incentiveValue));
			
		}
		if(boothTotals[zoneBoothIds[i]]){			
			totalRoundedCashValue = totalRoundedCashValue.add((new BigDecimal(((boothTotals[zoneBoothIds[i]]).getAt("supplyTypeTotals").getAt("CASH")).getAt("totalRevenue"))).setScale(0,BigDecimal.ROUND_HALF_UP));
			totalRoundedCardValue = totalRoundedCardValue.add((new BigDecimal(((boothTotals[zoneBoothIds[i]]).getAt("supplyTypeTotals").getAt("CARD")).getAt("totalRevenue"))).setScale(0,BigDecimal.ROUND_HALF_UP));
		}		
	}
	
	while (treeMapIterInner.hasNext()) {
		Map.Entry typeEntry = treeMapIterInner.next();
		productSubscriptionTypeList.each { productSubscriptionType ->
			if((typeEntry.getKey()).equals(productSubscriptionType.enumId)){
				
				zoneTotalValueMap[productSubscriptionType.enumId+"_QTY"] = typeEntry.getValue().getAt("total");
				zonesGRTotalValueMap[productSubscriptionType.enumId+"_QTY"] +=zoneTotalValueMap[productSubscriptionType.enumId+"_QTY"];
				
				zoneTotalValueMap[productSubscriptionType.enumId+"_REVAL"] = typeEntry.getValue().getAt("totalRevenue");
				
				
				
				if((productSubscriptionType.enumId).equals("CARD")){
					zoneTotalValueMap[productSubscriptionType.enumId+"_REVAL"] = totalRoundedCardValue;
					zoneTotalValueMap[productSubscriptionType.enumId+"_MR"] = new BigDecimal(totalCardMargin).setScale(2 ,BigDecimal.ROUND_HALF_UP);
					zoneTotalValueMap[productSubscriptionType.enumId+"_MRIN"] = new BigDecimal(totalCardMarginInCen).setScale(2 ,BigDecimal.ROUND_HALF_UP);
					zonesGRTotalValueMap[productSubscriptionType.enumId+"_MR"] +=zoneTotalValueMap[productSubscriptionType.enumId+"_MR"];
					
				}
				if((productSubscriptionType.enumId).equals("SPECIAL_ORDER")){
					zoneTotalValueMap[productSubscriptionType.enumId+"_MR"] = new BigDecimal(totalSoMargin).setScale(2 ,BigDecimal.ROUND_HALF_UP);
					zoneTotalValueMap[productSubscriptionType.enumId+"_MRIN"] = new BigDecimal(totalSoMarginInCen).setScale(2 ,BigDecimal.ROUND_HALF_UP);
					zonesGRTotalValueMap[productSubscriptionType.enumId+"_MR"] +=zoneTotalValueMap[productSubscriptionType.enumId+"_MR"];
					
				}
				if((productSubscriptionType.enumId).equals("CASH")){
					zoneTotalValueMap[productSubscriptionType.enumId+"_REVAL"] = totalRoundedCashValue;					
					zoneTotalValueMap[productSubscriptionType.enumId+"_MR"] = new BigDecimal(totalCashMargin).setScale(2 ,BigDecimal.ROUND_HALF_UP);
					zoneTotalValueMap[productSubscriptionType.enumId+"_MRIN"] = new BigDecimal(totalCashMarginInCen).setScale(2 ,BigDecimal.ROUND_HALF_UP);
					zonesGRTotalValueMap[productSubscriptionType.enumId+"_MR"] +=zoneTotalValueMap[productSubscriptionType.enumId+"_MR"];
					zoneTotalValueMap[productSubscriptionType.enumId+"_VAL"] = typeEntry.getValue().getAt("totalRevenue")+zoneTotalValueMap["MRADJ"] ;
					zonesGRTotalValueMap[productSubscriptionType.enumId+"_VAL"] +=zoneTotalValueMap[productSubscriptionType.enumId+"_VAL"];
				}
				zonesGRTotalValueMap[productSubscriptionType.enumId+"_REVAL"] +=zoneTotalValueMap[productSubscriptionType.enumId+"_REVAL"];
				zoneTotalValueMap[productSubscriptionType.enumId+"_NETVAL"] = zoneTotalValueMap[productSubscriptionType.enumId+"_REVAL"]-zoneTotalValueMap[productSubscriptionType.enumId+"_MRIN"];
				zonesGRTotalValueMap[productSubscriptionType.enumId+"_NETVAL"] +=zoneTotalValueMap[productSubscriptionType.enumId+"_NETVAL"];
				totalMarginValue += zoneTotalValueMap[productSubscriptionType.enumId+"_MR"];
			}
			
		}
	}
	zoneTotalValueMap["TOTALCASH_MR"] = new BigDecimal(totalCashMarginInCen-zoneTotalValueMap["CASH_MR"]).setScale(2 ,BigDecimal.ROUND_HALF_UP);
	zoneTotalValueMap["TOTALCARD_MR"] = new BigDecimal(totalCardMarginInCen-zoneTotalValueMap["CARD_MR"]).setScale(2 ,BigDecimal.ROUND_HALF_UP);
	zoneTotalValueMap["TOTALSO_MR"] = new BigDecimal(totalSoMarginInCen-zoneTotalValueMap["SPECIAL_ORDER_MR"]).setScale(2 ,BigDecimal.ROUND_HALF_UP);
	zoneTotalValueMap["TOTAL_MR"] = new BigDecimal(totalMarginValue+totalIncentiveValue).setScale(2 ,BigDecimal.ROUND_HALF_UP);		
	zonesGRTotalValueMap["TOTAL_MR"] +=zoneTotalValueMap["TOTAL_MR"];
	zonesGRTotalValueMap["TOTALCASH_MR"] +=zoneTotalValueMap["TOTALCASH_MR"]; 
	zonesGRTotalValueMap["TOTALCARD_MR"] +=zoneTotalValueMap["TOTALCARD_MR"];
	zonesGRTotalValueMap["TOTALSO_MR"] +=zoneTotalValueMap["TOTALSO_MR"];
	zoneTotalValueMap["TOTAL_INCENTIVE"] = new BigDecimal(totalIncentiveValue).setScale(2 ,BigDecimal.ROUND_HALF_UP);
	zonesGRTotalValueMap["TOTAL_INCENTIVE"] += zoneTotalValueMap["TOTAL_INCENTIVE"];
	zoneWiseTotalsMap[entry.getKey()]= zoneTotalValueMap;
	
}
zoneWiseTotalsMap["GRTOTAL"]= zonesGRTotalValueMap;
context.zoneWiseTotalsMap = zoneWiseTotalsMap;








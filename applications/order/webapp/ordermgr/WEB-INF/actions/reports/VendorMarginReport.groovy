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
if (!(security.hasEntityPermission("ACCOUNTING", "_ADMIN", session))) {
	context.errorMessage = "You don't have permission to run this report.";
	
	return "error";
}
rounding = RoundingMode.HALF_UP;
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime",fromDateTime);
dctx = dispatcher.getDispatchContext();
monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);

totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
context.put("totalDays", totalDays+1);
/*thruDate = parameters.thruDate;
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Timestamp fromDateTime = UtilDateTime.nowTimestamp();
//Timestamp thruDateTime = UtilDateTime.nowTimestamp();
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());
	//thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
   
}*/
conditionList = [];
boothsList=[];
masterList=[];
boothMarginRates =[:];

//Debug.logImportant("context.marginAbstract======================"+context.marginAbsract,"");

conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS ,"SUB_PROD_TYPE"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL ,"CREDIT"));
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL ,"CASH_FS"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productSubscriptionTypeList = delegator.findList("Enumeration", condition, ["enumId"] as Set, null, null, false);

conditionList.clear();

conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"BOOTH"));
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS ,"VENDOR"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
EntityFindOptions findOptions = new EntityFindOptions();
//findOptions.setMaxRows(2);
boothsList = delegator.findList("Facility",condition,null,UtilMisc.toList("parentFacilityId","facilityId"),findOptions,false);

conditionList.clear();

conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, "100"));
conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, "MARGIN_PRICE"));
discontinuationDateCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

productMarginPriceList =delegator.findList("ProductAndPriceView", discontinuationDateCondition, null, null, null, false);
productMarginPriceMap = FastMap.newInstance();
// Here we are populating product wise margins if  any
productMarginPriceList.each{ productMarginPrice ->
	productMarginPriceMap[productMarginPrice.productId] = productMarginPrice.price;	
}



	
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
	incentiveValue=BigDecimal.ZERO;
	supplyDate = monthBegin;
	Map boothMargins= new LinkedHashMap();
	
	boothsList.each{booth ->
		vendorMarginReportList =[];
		dayTotalsMap = [:];
		
		facilityDetail = delegator.findOne("Facility",[facilityId :booth.facilityId] ,false);
		//populateBoothOrders(booths.facilityId,vendorMarginReportList);
		for(int i=1 ; i <= (UtilDateTime.getIntervalInDays(monthBegin,monthEnd)+1); i++){
			dayOfMonth = i;
			dayTotalsMap[(String)i] = [:];
			dayTotalsMap[(String)i].putAll(typeAndCountMap);		
		}
		dayTotalsMap["facilityId"] = booth.facilityId;
		dayTotalsMap["Tot"] =[:];
		dayTotalsMap["Tot"].putAll(typeAndCountMap);
		dayTotalsMap["Tot"].putAt("CASH_DUE",BigDecimal.ZERO);
		// getting previous dues if any			
		Map<String, Object> boothPayments = NetworkServices.getBoothPayments(delegator, dctx.getDispatcher(), userLogin,
			UtilDateTime.toDateString(monthEnd, "yyyy-MM-dd"), null, booth.facilityId ,null ,Boolean.FALSE);
		Map boothTotalDues = FastMap.newInstance();
		List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
		if (boothPaymentsList.size() != 0) {
			 boothTotalDues = (Map)boothPaymentsList.get(0);
		}		
		dayTotalsMap["Tot"].putAt("CASH_DUE",(BigDecimal)boothTotalDues["totalDue"]);			
		
		/*if(facilityDetail.cashDues != null){
			dayTotalsMap["Tot"].putAt("CASH_DUE",(BigDecimal)facilityDetail.cashDues);
		}*/
		
		vendorMarginReportList.add(dayTotalsMap);
		boothMargins[booth.facilityId]=vendorMarginReportList;
		
		//get booth specfic Margins if any
		
		Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
		inputRateAmt.put("rateTypeId", "VENDOR_MRGN");
		inputRateAmt.put("periodTypeId", "RATE_HOUR");
		inputRateAmt.put("partyId", facilityDetail.ownerPartyId);
		inputRateAmt.put("rateCurrencyUomId", context.get("currencyUomId"));
		rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);
		normalMargin = rateAmount.rateAmount;
		inputRateAmt.put("rateTypeId", "VENDOR_CD_ADTL_MRGN");
		rateAmountForCard = dispatcher.runSync("getRateAmount", inputRateAmt);
		cardMargin = rateAmountForCard.rateAmount;
		marginMap =[:];
		marginMap["normalMargin"] = normalMargin;
		marginMap["cardMargin"] = cardMargin;
		marginMap["excludeIncentive"] = facilityDetail.excludeIncentive;
		boothMarginRates[booth.facilityId] = [:];
		boothMarginRates[booth.facilityId].putAll(marginMap);
		
	}
	
	for(int i=1 ; i <= (UtilDateTime.getIntervalInDays(monthBegin,monthEnd)+1); i++){
		dayOfMonth = i;				
		dayTotals = NetworkServices.getDayTotals(dispatcher.getDispatchContext(), supplyDate, "AM" , false, false, EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", false));
		//dayPMTotals = NetworkServices.getDayTotals(dispatcher.getDispatchContext(), supplyDate, "PM" , false, false, EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", false));
		
		boothTotals = dayTotals.boothTotals;
		//boothPMTotals = dayPMTotals.boothTotals;
		if(boothTotals == null){
			continue;
		}
		Iterator treeMapBoothIter = boothTotals.entrySet().iterator();
		while (treeMapBoothIter.hasNext()) {
			Map.Entry boothEntry = treeMapBoothIter.next();			
			marginMap = boothMarginRates[boothEntry.getKey()];
			BigDecimal normalMargin = marginMap["normalMargin"];
			BigDecimal cardMargin = marginMap["cardMargin"];			
			dayTotalsList = boothMargins[boothEntry.getKey()];
			dayTotalsMap = dayTotalsList.get(0);
			totalsMap =[:];
			totalsMap = dayTotalsMap["Tot"];			
			dayTotalQty = (boothEntry.getValue().total).setScale(1,rounding);;
			productTotals = boothEntry.getValue().productTotals;
			//productPMTotals =[:];
			/*if(boothPMTotals[boothEntry.getKey()]){
				productPMTotals = boothPMTotals[boothEntry.getKey()].productTotals;
			}
			*/
			dayIncTotalQty = 0;
			vtmQty = 0;
			// excluding vitamilk from the incentive totals
			if(productTotals["73"] != null){				
				productMap = productTotals["73"];
				
				vtmQty = productMap["total"];
				dayIncTotalQty = dayTotalQty-vtmQty;
			}else{
				dayIncTotalQty = dayTotalQty;
			}
			//incentive calculation
			incentivesResult = dispatcher.runSync("evaluateAccountFormula", [acctgFormulaId: "LMS_VOL_INCNTV", variableValues: "QUANTITY="+"1" , slabAmount : dayIncTotalQty , userLogin: userLogin]);
			incentiveValue=(new BigDecimal(incentivesResult.formulaResult)).setScale(2,rounding);
			if(marginMap["excludeIncentive"].equals("Y")){
				incentiveValue = BigDecimal.ZERO;
			}
			typeAndCount =[:];
			typeAndCount = dayTotalsMap[(String)dayOfMonth];
			typeAndCount["TOTAL"] = dayTotalQty;
			totalsMap["TOTAL"] +=dayTotalQty;
			productTotalsList = productTotals.entrySet();
			Iterator treeMapIter = productTotals.entrySet().iterator();
			while (treeMapIter.hasNext()) {
				Map.Entry entry = treeMapIter.next();
				productId =entry.getKey();
				productDetailMap =entry.getValue();
				//productPMDetailMap =productPMTotals[productId];
				productSubscriptionTypeList.each{ productSubscriptionType ->
					supplyTypeTotalQty = BigDecimal.ZERO;
					//supplyTypePMTotalQty = BigDecimal.ZERO;
					Map supplyTypeTotalMap = productDetailMap["supplyTypeTotals"];
					if(productDetailMap && supplyTypeTotalMap[productSubscriptionType.enumId] != null){
						Map supplyTypeDetailMap = supplyTypeTotalMap[productSubscriptionType.enumId];
						supplyTypeTotalQty = (new BigDecimal((supplyTypeDetailMap["total"]))).setScale(1, rounding);
					}
					/*if(productPMDetailMap && productPMDetailMap[productSubscriptionType.enumId] != null){
						supplyTypePMTotalQty = (new BigDecimal((productPMDetailMap[productSubscriptionType.enumId]))).setScale(1, rounding);						
					}*/
					typeAndCount[productSubscriptionType.enumId] += supplyTypeTotalQty;
					totalsMap[productSubscriptionType.enumId] += supplyTypeTotalQty;
					
					if((productSubscriptionType.enumId).equals("CASH") || (productSubscriptionType.enumId).equals("SPECIAL_ORDER")){
						// checking for Margin price specific to product if any
						if(productMarginPriceMap [productId] != null){
							tempMargin =((supplyTypeTotalQty).multiply(productMarginPriceMap [productId])).setScale(2, rounding);
							tempMarginValue = tempMargin;
							typeAndCount[productSubscriptionType.enumId+"_MR"] += tempMarginValue;
							typeAndCount["TOTAL_MR"] += tempMarginValue;
							totalsMap[productSubscriptionType.enumId+"_MR"] += tempMarginValue;
							totalsMap["TOTAL_MR"] += tempMarginValue;
						}else{
							tempMargin =(supplyTypeTotalQty.multiply(normalMargin)).setScale(2, rounding);							
							tempIncentive = (supplyTypeTotalQty.multiply(incentiveValue)).setScale(2, rounding);
							tempMarginValue = tempMargin.add(tempIncentive);
							typeAndCount[productSubscriptionType.enumId+"_MR"] += tempMarginValue;
							typeAndCount["TOTAL_MR"] += tempMarginValue;
							totalsMap[productSubscriptionType.enumId+"_MR"] += tempMarginValue;
							totalsMap["TOTAL_MR"] += tempMarginValue;
						}
					}
					
					//card margin calculation
					if(((productSubscriptionType.enumId).equals("CARD"))){
						
						// checking for Margin price specific to product if any
						if(productMarginPriceMap [productId] != null){
							tempMargin =((supplyTypeTotalQty).multiply(productMarginPriceMap [productId]+cardMargin)).setScale(2, rounding);
							tempMarginValue = tempMargin;
							typeAndCount[productSubscriptionType.enumId+"_MR"] += tempMarginValue;
							typeAndCount["TOTAL_MR"] += tempMarginValue;
							totalsMap[productSubscriptionType.enumId+"_MR"] += tempMarginValue;
							totalsMap["TOTAL_MR"] += tempMarginValue;
						}else{
							tempMargin =((supplyTypeTotalQty).multiply(normalMargin+cardMargin)).setScale(2, rounding);
							tempIncentive = (supplyTypeTotalQty.multiply(incentiveValue)).setScale(2, rounding);
							tempMarginValue = tempMargin.add(tempIncentive);
							typeAndCount[productSubscriptionType.enumId+"_MR"] += tempMarginValue;
							typeAndCount["TOTAL_MR"] += tempMarginValue;
							totalsMap[productSubscriptionType.enumId+"_MR"] += tempMarginValue;
							totalsMap["TOTAL_MR"] += tempMarginValue;
						}
					}//End of Card Type
				}//End of productSubscriptionTypeList
			}// product totals	
			
		}	
		Debug.logImportant(i+"    Days Completed.", "");
		supplyDate = UtilDateTime.getNextDayStart(supplyDate);
	}
	
masterList.add(boothMargins);
context.put("masterList", masterList);


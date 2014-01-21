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


import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.io.ObjectOutputStream.DebugTraceInfoStack;


import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;

import javax.swing.text.html.parser.Entity;
import org.ofbiz.product.product.ProductWorker;



userLogin= context.userLogin;
dctx = dispatcher.getDispatchContext();
effectiveDate = null;
effectiveDateStr = parameters.saleDate;
if (UtilValidate.isEmpty(effectiveDateStr)) {
	effectiveDate = UtilDateTime.nowTimestamp();
}
else{
	def sdf = new SimpleDateFormat("yyyy-MM-dd");
	try {
		effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr+" 00:00:00").getTime());
	}catch (Exception e) {
		Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
	}
}


fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
fromDateTime = null;
thruDateTime = null;
if ((UtilValidate.isEmpty(fromDate))&&(UtilValidate.isEmpty(thruDate))) {
	fromDateTime = UtilDateTime.nowTimestamp();
	thruDateTime= UtilDateTime.nowTimestamp();
}else{
	def sdf = new SimpleDateFormat("yyyy-MM-dd");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());
	}catch (Exception e) {
		Debug.logError(e, "Cannot parse date string: "+thruDate, "");
	}
}
dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
context.put("dayBegin",dayBegin);
context.put("dayEnd",dayEnd);



lmsproductList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
allProductsIdsList = EntityUtil.getFieldListFromEntityList(lmsproductList, "productId", false);//for all Products
byProductsList=ProductWorker.getProductsByCategory(delegator ,"BYPROD" ,null);

lmsproductList=  EntityUtil.filterByCondition(lmsproductList, EntityCondition.makeCondition("productId",EntityOperator.NOT_IN , byProductsList.productId));


context.put("lmsProductList", lmsproductList);
context.put("byProductsList", byProductsList);

conditionList =[];

boothsList=[];
if(UtilValidate.isNotEmpty(parameters.boothId)){
	boothDetails = delegator.findOne("Facility", [facilityId :  parameters.boothId], false);
	if(UtilValidate.isNotEmpty(boothDetails)){
		boothId=boothDetails.facilityId;
		boothsList.add(boothId);
	}

}else{
	boothsList=NetworkServices.getBoothList(delegator ,parameters.routeId);//getting list of Booths
}

lmsProductIdsList = EntityUtil.getFieldListFromEntityList(lmsproductList, "productId", false);
byProductIdsList= EntityUtil.getFieldListFromEntityList(byProductsList, "productId", false);

boothWiseTotalMap=[:];
boothWiseGrandTotalMap =[:];//to add both Prodouct totals
boothsList.each{ boothId->
	dayTotals = NetworkServices.getPeriodTotals(dctx, [facilityIds:[boothId],fromDate:dayBegin, thruDate:dayEnd]);
	dayWiseTotalsMap =dayTotals.get("dayWiseTotals");
	if(UtilValidate.isNotEmpty(dayWiseTotalsMap)){//checking emty for whole period
		
		allDayProductsMap=[:];//for all Days Map

		lmsProdGrandTotMap=[:];
		byProdGrandTotMap=[:];
		boothGrandTotMap=[:];

		lmsProductIdsList.each{ product ->
			lmsProdGrandTotMap[product] = 0;
		}
		byProductIdsList.each{ product ->
			byProdGrandTotMap[product] = 0;
		}
		byProdGrandTotMap["Total"] = BigDecimal.ZERO;
		byProdGrandTotMap["DespVal"] = BigDecimal.ZERO;
		byProdGrandTotMap["PaidAmt"] = BigDecimal.ZERO;
		byProdGrandTotMap["Due"] = BigDecimal.ZERO;
		byProdGrandTotMap["OpeningBal"] =BigDecimal.ZERO;
		byProdGrandTotMap["ClosingBal"] =BigDecimal.ZERO;
		byProdGrandTotMap["SplDiscount"] = BigDecimal.ZERO;

		boothSplDiscount = BigDecimal.ZERO;
		facilityDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId", boothId), false);
		Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
		rateTypeId = facilityDetails.categoryTypeEnum+"_MRGN";
		if("VENDOR".equals(facilityDetails.categoryTypeEnum)){
			rateTypeId = "VENDOR_DEDUCTION";
		}
		inputRateAmt.put("rateTypeId", rateTypeId);
		inputRateAmt.put("periodTypeId", "RATE_HOUR");
		inputRateAmt.put("partyId", facilityDetails.get("ownerPartyId"));
		inputRateAmt.put("rateCurrencyUomId","INR");
		inputRateAmt.put("fromDate",dayBegin);
		productMarginsMap=[:];
		allProductsIdsList.each{ productId->
			productMargin =BigDecimal.ZERO;
			inputRateAmt.put("productId", productId);//setting each product for forSpecial Discount
			rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);//Run Serivice for each product
			if(UtilValidate.isNotEmpty(rateAmount)){
				normalMargin =  rateAmount.get("rateAmount");
			}
			productMarginsMap[productId]=normalMargin.setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		obAmount=BigDecimal.ZERO;
		closingBal=BigDecimal.ZERO;

		for(int j=0 ; j < (UtilDateTime.getIntervalInDays(dayBegin,dayEnd)+1); j++){
			Timestamp saleDate = UtilDateTime.addDaysToTimestamp(dayBegin, j);
			dayLmsTotalQty = 0;
			daySplDiscount=BigDecimal.ZERO;
			curntDay=UtilDateTime.toDateString(saleDate ,"yyyy-MM-dd");
			curntDaySalesMap=[:];


			boothDayTotal=[:];

			lmsProductsMap=[:];
			byProductsMap=[:];

			lmsProductIdsList.each{ product ->
				lmsProductsMap[product] = 0;
			}
			byProductIdsList.each{ product ->
				byProductsMap[product] = 0;
			}

			//these keys repeted for each Day
			byProductsMap["Total"] = BigDecimal.ZERO;
			byProductsMap["DespVal"] = BigDecimal.ZERO;
			byProductsMap["PaidAmt"] = BigDecimal.ZERO;
			byProductsMap["Due"] = BigDecimal.ZERO;
			byProductsMap["OpeningBal"] =BigDecimal.ZERO;
			byProductsMap["ClosingBal"] =BigDecimal.ZERO;
			byProductsMap["SplDiscount"] = BigDecimal.ZERO;


			curntDaySalesMap=dayWiseTotalsMap.getAt(curntDay);

			if(UtilValidate.isNotEmpty(curntDaySalesMap)){
				productTotList=curntDaySalesMap.get("productTotals");

				productTotList.each{ product->
					productId=product.getKey();
					if(lmsProductIdsList.contains(productId)){
						lmsProductsMap[productId]=product.getValue().get("total");
						lmsProdGrandTotMap[productId]+=product.getValue().get("total");
					}
					if(byProductIdsList.contains(productId)){
						byProductsMap[productId]=product.getValue().get("total");
						byProdGrandTotMap[productId]+=product.getValue().get("total");
					}
					productMargin=BigDecimal.ZERO;
					productMargin=productMarginsMap.getAt(productId);//for getting products Margin

					splDiscount=productMargin*product.getValue().get("total");
					daySplDiscount+=splDiscount;
				}//prodTotList(for EachDay) ends here
				
				byProductsMap["Total"]=curntDaySalesMap.getAt("total");
				byProdGrandTotMap["Total"] +=curntDaySalesMap.getAt("total")
				totalSale = BigDecimal.ZERO;
				totalSale=curntDaySalesMap.getAt("totalRevenue");

				byProductsMap["DespVal"]=((new BigDecimal(totalSale+daySplDiscount)).setScale(2,BigDecimal.ROUND_HALF_UP));

				byProdGrandTotMap["DespVal"] +=((new BigDecimal(totalSale+daySplDiscount)).setScale(2,BigDecimal.ROUND_HALF_UP));
				//get Booth paid Amount for the given Day
				boothPaidDetail = NetworkServices.getBoothPaidPayments( dctx , [fromDate:saleDate ,thruDate:saleDate , facilityId:boothId]);

				reciepts = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(boothPaidDetail)){
					reciepts = boothPaidDetail.get("invoicesTotalAmount");
				}
				byProductsMap["PaidAmt"] = ((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
				byProdGrandTotMap["PaidAmt"] +=((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));

				byProductsMap["Due"]=((new BigDecimal(totalSale)).setScale(2,BigDecimal.ROUND_HALF_UP));
				byProdGrandTotMap["Due"] +=((new BigDecimal(totalSale)).setScale(2,BigDecimal.ROUND_HALF_UP));

				if(j==0){//Opeinig Balance called only  for firstDay  in whole period
					obAmount =	( NetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: saleDate , facilityId:boothId])).get("openingBalance");
					closingBal=obAmount+totalSale-reciepts;
				}else{
					obAmount=closingBal;
					closingBal=obAmount+totalSale-reciepts;
				}
				byProductsMap["OpeningBal"]=((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
				byProdGrandTotMap["OpeningBal"] +=((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));

				byProductsMap["ClosingBal"]=((new BigDecimal(obAmount+totalSale-reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
				byProdGrandTotMap["ClosingBal"] +=((new BigDecimal(obAmount+totalSale-reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));

				byProductsMap["SplDiscount"]=((new BigDecimal(daySplDiscount)).setScale(2,BigDecimal.ROUND_HALF_UP));
				byProdGrandTotMap["SplDiscount"] +=((new BigDecimal(daySplDiscount)).setScale(2,BigDecimal.ROUND_HALF_UP));

			}//curnt Day caliculation

			boothDayTotal["LMS"]=lmsProductsMap;
			boothDayTotal["BYPROD"]=byProductsMap;

			tempDayMap = [:];
			tempDayMap.putAll(boothDayTotal);
			allDayProductsMap[curntDay]=tempDayMap;
		}//for loop end
		tempBoothTotMap=[:];
		tempBoothTotMap.putAll(allDayProductsMap);
		boothWiseTotalMap[boothId]=tempBoothTotMap;

		boothGrandTotMap["LMS"]=lmsProdGrandTotMap;
		boothGrandTotMap["BYPROD"]=byProdGrandTotMap;

		tempGrandTotalMap=[:];
		tempGrandTotalMap.putAll(boothGrandTotMap);

		boothWiseGrandTotalMap[boothId]=tempGrandTotalMap;
	}//if close
}
context.put("boothWiseTotalMap", boothWiseTotalMap);
context.put("boothWiseGrandTotalMap", boothWiseGrandTotalMap);






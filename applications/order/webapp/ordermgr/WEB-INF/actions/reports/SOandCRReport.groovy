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
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.service.ServiceUtil;



categoryTypeEnum = parameters.categoryTypeEnum;
context.put("categoryTypeEnum", categoryTypeEnum);
/*if(UtilValidate.isEmpty(parameters.fromDate)){
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
context.put("fromDateTime",fromDateTime);
}*/
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
try {
    fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());
	context.put("fromDateTime", fromDateTime);
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());
	context.put("thruDateTime", thruDateTime);
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
   
}
conditionList = [];
boothsList=[];
masterList=[];
productList =[];
FinalInvoiceList=[];
boothInvoiceReportList = [];
monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);

productList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("salesDate",monthBegin));
context.put("productList", productList);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"BOOTH"));
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS , categoryTypeEnum));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
boothsList = delegator.findList("Facility",condition,null,null,null,false);
boothsList.each{booths ->
	boothSales=[:];
	boothInvoiceMap=[:];
	vendorSalesReportList = [];
	
	previousListSize = vendorSalesReportList.size();
	populateBoothSOOrders(booths.facilityId,vendorSalesReportList,boothInvoiceReportList);
	currentListSize =vendorSalesReportList.size();
	if(previousListSize != currentListSize){
		boothSales[booths.facilityId]=vendorSalesReportList;
	}
	masterList.add(boothSales);
	
}
def populateBoothSOOrders(boothId,vendorSalesReportList,boothInvoiceReportList){ 
	GenericValue userLogin = (GenericValue) context.get("userLogin");
	
	obAmount=BigDecimal.ZERO;//service will run only For CreditInstitutions Bill/Invoice Report
	if(categoryTypeEnum != "CR_INST"){
		obAmount =	( NetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: monthBegin , facilityId:boothId])).get("openingBalance");
	}
	productMap = [:];
	boothOrderItemsList=[];
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS , boothId));
	conditionList.add(EntityCondition.makeCondition("shipmentStatusId", EntityOperator.EQUALS , "GENERATED"));
	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
	if(categoryTypeEnum != "CR_INST"){
		conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS ,"SPECIAL_ORDER"));
	}
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["orderId", "orderDate", "productId", "quantity" ,"grandTotal" ,"productSubscriptionTypeId" ,"originFacilityId" ,"unitPrice" ,"shipmentId","estimatedShipDate" ,"quantityIncluded"] as Set;
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, fieldsToSelect , [ "estimatedShipDate","shipmentId"], null, false);
	tempEstimatedShipDate = monthBegin;
	dayTotalsMap = [:];
	typeAndCountMap =[:];
	totalsMap = [:];
    invoiceTotMap=[:];
	
	facilityDetail = delegator.findOne("Facility",[facilityId : boothId] ,false);	
	partyId=facilityDetail.ownerPartyId;
	productList.each{ product ->
		typeAndCountMap[product.productId] = 0;
		totalsMap[product.productId]=0;
		
	}
	typeAndCountMap["TOTALAMOUNT"] = 0;
	totalsMap["TOTALAMOUNT"] = BigDecimal.ZERO;
	
	
	for( i=1 ; i <= (UtilDateTime.getIntervalInDays(monthBegin,monthEnd)+1); i++){
		dayOfMonth = UtilDateTime.getDayOfMonth(UtilDateTime.addDaysToTimestamp(monthBegin, i-1), timeZone, locale);
		dayTotalsMap[(String)dayOfMonth] = [:];
		dayTotalsMap[(String)dayOfMonth].putAll(typeAndCountMap);
	}
	
	productTotalSalesMap = [:];
	boothOrderItemsList.each { boothOrderItem ->
				
		dayOfMonth = UtilDateTime.getDayOfMonth(boothOrderItem.estimatedShipDate, timeZone, locale);
						typeAndCount =[:];
						typeAndCount.putAll(dayTotalsMap[(String)dayOfMonth]);
						amount = ((boothOrderItem.quantity).intValue() * (boothOrderItem.unitPrice).doubleValue());
						litrs=(boothOrderItem.quantity).intValue()*(boothOrderItem.quantityIncluded).doubleValue();
						typeAndCount[boothOrderItem.productId] += (boothOrderItem.quantity).intValue();
						typeAndCount["TOTALAMOUNT"] += amount;
						
						typeAndCount["supplyDate"] = boothOrderItem.estimatedShipDate;
						totalsMap[boothOrderItem.productId] += (boothOrderItem.quantity).intValue();
						totalsMap["TOTALAMOUNT"] += amount;
						totalsMap["TOTALAMOUNT"] =(new BigDecimal(totalsMap["TOTALAMOUNT"])).setScale(2,BigDecimal.ROUND_HALF_UP);
						
							dayTotalsMap[(String)dayOfMonth] = typeAndCount;
							
						
						//adding TotalSale for EachProduct
							if(UtilValidate.isEmpty(productTotalSalesMap[boothOrderItem.productId]))
							{ invoiceProductMap=[:]
								invoiceProductMap["LITRES"] = litrs;
								invoiceProductMap["UNITRATE"]=(boothOrderItem.unitPrice).intValue();
								//Caliculating Discount for each Product only once
								rateTypeId = categoryTypeEnum + "_MRGN";
								inputRateAmt=[:];
								inputRateAmt.put("userLogin", userLogin);
								inputRateAmt.put("partyId", partyId);
								inputRateAmt.put("rateTypeId", rateTypeId);
								inputRateAmt.put("fromDate", fromDateTime);
								inputRateAmt.put("productId", boothOrderItem.productId);
								Map<String, Object> serviceResults = dispatcher.runSync("getRateAmount", inputRateAmt);
								if (ServiceUtil.isError(serviceResults)) {
									context.errorMessage = "Error While Getting Discount";
									   context.put("serviceResults", serviceResults);
									   return ;
								 }
								discountAmount = (BigDecimal)serviceResults.get("rateAmount");
								// since the discounts are per litre, adjust proportionally
								discountAmount = discountAmount.multiply((boothOrderItem.quantityIncluded).doubleValue());
								
								invoiceProductMap["DISCOUNT"] = discountAmount;
								invoiceProductMap["AMOUNT"] = amount;
								tempInvMap = [:];
								tempInvMap.putAll(invoiceProductMap);
								productTotalSalesMap[boothOrderItem.productId]=tempInvMap;
								
							}else{
							invoiceUpdateProductMap=productTotalSalesMap[boothOrderItem.productId];
							invoiceUpdateProductMap["AMOUNT"] +=amount;
							invoiceUpdateProductMap["LITRES"]+=litrs;
							tempInvMap1 = [:];
							tempInvMap1.putAll(invoiceUpdateProductMap);
							productTotalSalesMap.put(boothOrderItem.productId,tempInvMap1);
							
						 }
						
	 }
	if(UtilValidate.isNotEmpty(dayTotalsMap) && ((totalsMap["TOTALAMOUNT"].compareTo(BigDecimal.ZERO)) != 0)){	
		dayTotalsMap["facilityId"] = boothId;
		dayTotalsMap["Tot"] = totalsMap;
		vendorSalesReportList.add(dayTotalsMap);
	}
	if(UtilValidate.isNotEmpty(productTotalSalesMap)){
	invoiceTotMap["facilityId"]=boothId;
	invoiceTotMap["INVOICE"] = productTotalSalesMap;
	invoiceTotMap["TotAmount"] = productTotalSalesMap;
	invoiceTotMap["OpeningBAL"] = ((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
	boothInvoiceReportList.add(invoiceTotMap);
	}
}
context.put("masterList", masterList);
context.put("boothInvoiceReportList", boothInvoiceReportList);
//Debug.log("the MasterList is*********"+masterList);



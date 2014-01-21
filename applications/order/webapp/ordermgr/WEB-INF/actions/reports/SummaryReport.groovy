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
import org.ofbiz.entity.GenericValue;
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

summaryDetailsMap = [:];
GrTotalMap=[:];
productDetailsMap = [:];
productDetailsList = [];
fields = new HashSet(["productId", "brandName", "quantityIncluded"]);
productDetailsList = delegator.findList("Product", null, fields, null, null, false);

productDetailsList.each{ eachProduct ->
	tempProdDetailsMap = [:];
	tempProdDetailsMap["quantityIncluded"] =  eachProduct.quantityIncluded;
	tempProdDetailsMap["prodName"] =  eachProduct.brandName;
	productDetailsMap[eachProduct.productId] = tempProdDetailsMap;
}
populateSupplyTypeSummaryDetails("AM");
populateSupplyTypeSummaryDetails("PM");
context.put("summaryDetailsMap", summaryDetailsMap);

def populateSupplyTypeSummaryDetails(String supplyType){
	
    Map DateWiseSummaryDetailsMap = [:];
	List productSubscriptionTypeList = [];
	quantityIncluded = 0;
	prodName = null;
	fromDateTime = null;
	thruDateTime = null;
	
	/*customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);*/
	fromDate = parameters.fromDate;
	thruDate = parameters.thruDate;
	def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());		
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+fromDate, "");	   
	}
	if(supplyType == "AM"){
		fromDateTime = fromDateTime;
		thruDateTime = thruDateTime;
	}
	else{
		PMfromDateTime = fromDateTime;
		PMthruDateTime = thruDateTime;
		fromDateTime = UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(PMfromDateTime), -1);
		thruDateTime = UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(PMthruDateTime), -1);
	}
	
	context.put("fromDateTime",fromDateTime);
	
	dctx = dispatcher.getDispatchContext();
	monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	
	totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
	context.put("totalDays", totalDays+1);
	
	java.sql.Date startDate = new java.sql.Date(fromDateTime.getTime());
	java.sql.Date endDate = new java.sql.Date(thruDateTime.getTime());
	
	productSubscriptionTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "SUB_PROD_TYPE"), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"), null, false);
	
	List conditionList = UtilMisc.toList(
					EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, supplyType));
	conditionList.add(EntityCondition.makeCondition("salesDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
	conditionList.add(EntityCondition.makeCondition("salesDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
	EntityCondition AMcondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	LMSSalesHistorySummaryDetailList = delegator.findList("LMSSalesHistorySummaryDetail", AMcondition, null, null, null, false);
				
	LMSSalesHistorySummaryDetailList.each{ product ->
		def salesDate = product.salesDate;
		def productId = product.productId;		
		quantityIncluded = 0;
		prodName = null;
		
		Iterator prodIter = productDetailsMap.entrySet().iterator();
		while (prodIter.hasNext()) {
			Map.Entry entry = prodIter.next();
			if(entry.getKey() == productId){
				productValuesMap = [:];
				productValuesMap = entry.getValue();
				Iterator valuesIter = productValuesMap.entrySet().iterator();
				while (valuesIter.hasNext()) {
					Map.Entry valueEntry = valuesIter.next();
					if(valueEntry.getKey() == "quantityIncluded"){
						quantityIncluded = valueEntry.getValue();
					}
					if(valueEntry.getKey() == "prodName"){
						prodName = valueEntry.getValue();
					}
					
				}
			}
		}
		Map productSalesMap = [:];
		Map tempSummaryDetailsMap = [:];
		GrtotalValuesMap=[:];
		
		if(GrTotalMap[productId] != null){
			GrtotalValuesMap.putAll(GrTotalMap[productId]);
		}else{
			GrtotalValuesMap["productName"]=prodName;
			GrtotalValuesMap["shipmentTypeId"]=product.shipmentTypeId;
			GrtotalValuesMap["totalQuantity"]=0;
		}
		def productSubscriptionTypeId = product.productSubscriptionTypeId;
		productSalesMap["productName"] = prodName;
		productSalesMap["shipmentTypeId"] = product.shipmentTypeId;
		
		
		Iterator<GenericValue> supplyTypeIter = productSubscriptionTypeList.iterator();
		while(supplyTypeIter.hasNext()) {
			GenericValue type = supplyTypeIter.next();
			totalQuantity = 0;
			productSalesMap.put(type.getString("enumId"), totalQuantity);
			if(GrtotalValuesMap[type.getString("enumId")] == null){
				GrtotalValuesMap.put(type.getString("enumId"), totalQuantity);
			}
			
		}
		productSalesMap[productSubscriptionTypeId] = product.totalQuantity/quantityIncluded;
		productSalesMap["totalQuantity"] = product.totalQuantity;
		GrtotalValuesMap["totalQuantity"] += product.totalQuantity;
		GrtotalValuesMap[productSubscriptionTypeId] += product.totalQuantity/quantityIncluded;
		GrTotalMap.putAt(productId, GrtotalValuesMap);
		if(DateWiseSummaryDetailsMap[salesDate] == null ) {
			if(tempSummaryDetailsMap[productId] == null ) {
				tempSummaryDetailsMap[productId] = productSalesMap;
				DateWiseSummaryDetailsMap[salesDate] = tempSummaryDetailsMap;
			}
			else {
				Map tempMap = (Map)(tempSummaryDetailsMap.get(productId));
				tempMap[productSubscriptionTypeId] = product.totalQuantity/quantityIncluded;
				def runningTotalQuantity = tempMap["totalQuantity"];
				runningTotalQuantity = runningTotalQuantity + product.totalQuantity;
				tempMap["totalQuantity"] = runningTotalQuantity;
				tempSummaryDetailsMap[productId] = tempMap;
				DateWiseSummaryDetailsMap[salesDate] = tempSummaryDetailsMap;
			}
		}
		else {
			Map tempDateMap = (Map)(DateWiseSummaryDetailsMap.get(salesDate));
			if(tempDateMap[productId] == null ) {
				tempDateMap[productId] = productSalesMap;
				DateWiseSummaryDetailsMap[salesDate] = tempDateMap;
			}
			else {
				Map tempMap = (Map)(tempDateMap.get(productId));
				tempMap[productSubscriptionTypeId] = product.totalQuantity/quantityIncluded;
				def runningTotalQuantity = tempMap["totalQuantity"];
				runningTotalQuantity = runningTotalQuantity + product.totalQuantity;
				tempMap["totalQuantity"] = runningTotalQuantity;
				tempDateMap[productId] = tempMap;
				DateWiseSummaryDetailsMap[salesDate] = tempDateMap;
			}
		}
	}
	summaryDetailsMap[supplyType] = DateWiseSummaryDetailsMap;
}
context.put("GrTotalMap",GrTotalMap);







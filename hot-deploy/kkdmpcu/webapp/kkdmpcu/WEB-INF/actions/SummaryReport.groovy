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
import javax.naming.Context;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;

summaryDetailsMap = [:];
productDetailsMap = [:];
productDetailsList = [];
productDetailsList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.productDetailsList = productDetailsList;
prodMap = [:];
dataMap = [:];
supplyTotals = 0;
grandTotalQuantity = 0;

List productSubscriptionTypeList = [];
productSubscriptionTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "SUB_PROD_TYPE"), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"), null, false);

dataMap["productName"]="";
dataMap["shipmentTypeId"]="";
dataMap["totalQuantity"]=0;
Iterator<GenericValue> supplyTypeIter = productSubscriptionTypeList.iterator();
while(supplyTypeIter.hasNext()) {
	GenericValue type = supplyTypeIter.next();
	totalQuantity = 0;
	dataMap.put(type.getString("enumId"), totalQuantity);
}

productDetailsList.each{ eachProduct ->
	tempProdDetailsMap = [:];
	tempProdDetailsMap["quantityIncluded"] =  eachProduct.quantityIncluded;
	tempProdDetailsMap["prodName"] =  eachProduct.brandName;
	productDetailsMap[eachProduct.productId] = tempProdDetailsMap;
	prodMap[eachProduct.productId] = dataMap;
}
grandTotalMap = [:];
grandTotalMap.putAll(prodMap);

populateSupplyTypeSummaryDetails("AM");
populateSupplyTypeSummaryDetails("PM");
context.put("summaryDetailsMap", summaryDetailsMap);

def populateSupplyTypeSummaryDetails(String supplyType){
	
    Map DateWiseSummaryDetailsMap = [:];
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
	try{
		List conditionList = UtilMisc.toList(
					EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, supplyType));
				conditionList.add(EntityCondition.makeCondition("salesDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
		conditionList.add(EntityCondition.makeCondition("salesDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
		EntityCondition AMcondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
		LMSSalesHistorySummaryDetailList = delegator.findList("LMSSalesHistorySummaryDetail", AMcondition, null, ["salesDate","productId"], null, false);
		//lets check sales product is availble in Lms product map if not append missing product details
		tempProductList = EntityUtil.getFieldListFromEntityList(LMSSalesHistorySummaryDetailList, "productId", true);
		for(int i=0;i< tempProductList.size();i++){
			String tempProductId = tempProductList.get(i);
			if(UtilValidate.isEmpty(prodMap[tempProductId])){
				prodMap[tempProductId] = dataMap;
				
				prodNotInSaleDate = delegator.findOne("Product", [productId : tempProductId], false);
				tempNotInSaleDateDetailMap = [:];
				tempNotInSaleDateDetailMap["quantityIncluded"] =  prodNotInSaleDate.quantityIncluded;
				tempNotInSaleDateDetailMap["prodName"] =  prodNotInSaleDate.brandName;
				productDetailsMap[tempProductId] = tempNotInSaleDateDetailMap;
				grandTotalMap.put(tempProductId, dataMap);
			}
		}
		LMSSalesHistorySummaryDetailList.each{ product ->
			def salesDate = product.salesDate;
			def productId = product.productId;	

			if(DateWiseSummaryDetailsMap[salesDate] == null){ 
				DateWiseSummaryDetailsMap[salesDate] = prodMap;
			}
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
			def productSubscriptionTypeId = product.productSubscriptionTypeId;
		
			Map prodDetailsSummaryMap = FastMap.newInstance();
			Map prodWiseSummaryMap = FastMap.newInstance();
			Map tempGrandTotalMap = FastMap.newInstance();
			tempGrTotMap = FastMap.newInstance();
			Map temporaryMap = FastMap.newInstance();
			
			prodWiseSummaryMap.putAll(DateWiseSummaryDetailsMap.get(salesDate));
			
			prodDetailsSummaryMap.putAll(prodWiseSummaryMap.get(productId));
			prodDetailsSummaryMap[productSubscriptionTypeId] = product.totalQuantity/quantityIncluded;
			def runningTotalQuantity = prodDetailsSummaryMap["totalQuantity"];
			prodDetailsSummaryMap["totalQuantity"] = runningTotalQuantity + product.totalQuantity;
			prodDetailsSummaryMap["productName"] = prodName;
			temporaryMap.putAll(prodDetailsSummaryMap);
			prodWiseSummaryMap.put(productId, temporaryMap);
			DateWiseSummaryDetailsMap[salesDate] = prodWiseSummaryMap;
			
			//GrandTotal
			tempGrandTotalMap.putAll(grandTotalMap.get(productId));
			supplyTotals = tempGrandTotalMap.get(productSubscriptionTypeId);
			supplyTotals = supplyTotals + product.totalQuantity/quantityIncluded;
			grandTotalQuantity = tempGrandTotalMap.get("totalQuantity");
			grandTotalQuantity = grandTotalQuantity + (product.totalQuantity/quantityIncluded);
			tempGrandTotalMap[productSubscriptionTypeId] = product.totalQuantity/quantityIncluded;
			tempGrandTotalMap.put(productSubscriptionTypeId, supplyTotals);
			tempGrandTotalMap.put("totalQuantity", grandTotalQuantity);
			tempGrTotMap.putAll(tempGrandTotalMap);
			grandTotalMap.put(productId, tempGrTotMap);
		}
		summaryDetailsMap[supplyType] = DateWiseSummaryDetailsMap;
	} catch (Exception e) {
	// Ignore the quantity if there's a problem with null object
	}
}
context.put("GrTotalMap",grandTotalMap);
//Debug.logInfo(" GrTotalMap====================================="+ grandTotalMap, "");
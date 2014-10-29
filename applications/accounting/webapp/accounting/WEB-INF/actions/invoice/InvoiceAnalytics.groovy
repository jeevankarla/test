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

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import java.util.SortedMap;

import javolution.util.FastList;

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
organizationPartyId = "Company";
context.organizationPartyId = organizationPartyId;
context.apInvoiceListSize =0;
dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		context.fromDate = parameters.fromDate;
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}else {
		fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),0));
		context.fromDate = fromDate
		fromDate = fromDate;
	}
	if (parameters.thruDate) {
		context.thruDate = parameters.thruDate;
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}else {
		context.thruDate = UtilDateTime.nowDate();
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

dctx = dispatcher.getDispatchContext();
conditionList = [];
conditionList.clear();
statusMap = [:];
if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)){
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
}

conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED","INVOICE_PAID")));

condition = EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
invoiceList = delegator.findList("InvoiceAndType", condition, null, null, null, false);
SortedMap apDataMap = new TreeMap();
SortedMap arDataMap = new TreeMap();
totalApAmount =BigDecimal.ZERO;
totalArAmount =BigDecimal.ZERO;
List statusIdList = FastList.newInstance();
if(invoiceList){
	apInvoiceList = EntityUtil.filterByAnd(invoiceList, UtilMisc.toMap("parentTypeId","PURCHASE_INVOICE"));	
	arInvoiceList = EntityUtil.filterByAnd(invoiceList, UtilMisc.toMap("parentTypeId","SALES_INVOICE"));
	Set invoiceStatuIds = new HashSet(apInvoiceList.statusId);
	invoiceStatuIds.addAll(new HashSet(arInvoiceList.statusId));
	
	statusIdList.addAll(invoiceStatuIds);
	List<GenericValue> statusList = delegator.findList("StatusItem", EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIdList), null, null, null, false);
	
	statusList.each{ status->
		statusMap[status.statusId] = status.description;
		
	}
	statusIdList.each{ statusId ->
			apDataMap.put(statusId, 0);
			arDataMap.put(statusId, 0);
			apInvoiceByStatusList = EntityUtil.filterByAnd(apInvoiceList, UtilMisc.toMap("statusId", statusId));
			arInvoiceByStatusList = EntityUtil.filterByAnd(arInvoiceList , UtilMisc.toMap("statusId", statusId));
			apStatusAmount = dispatcher.runSync("getInvoiceRunningTotal", [invoiceIds: apInvoiceByStatusList.invoiceId, organizationPartyId: organizationPartyId, userLogin: userLogin]).get("invoiceRunningTotal");
			arStatusAmount = dispatcher.runSync("getInvoiceRunningTotal", [invoiceIds: arInvoiceByStatusList.invoiceId, organizationPartyId: organizationPartyId, userLogin: userLogin]).get("invoiceRunningTotal");
			if (apStatusAmount) {
				apStatusAmount = apStatusAmount.replace("Rs" ,"");
				apStatusAmount = apStatusAmount.replace("," ,"");
				apStatusAmount = (new BigDecimal(apStatusAmount)).setScale(0,0);
				apDataMap.put(statusId, apStatusAmount);
				totalApAmount += apStatusAmount;
			}
			if (arStatusAmount) {				
				arStatusAmount = arStatusAmount.replace("Rs" ,"");
				arStatusAmount = arStatusAmount.replace("," ,"");
				arStatusAmount = (new BigDecimal(arStatusAmount)).setScale(0,0);
				arDataMap.put(statusId, arStatusAmount);
				totalArAmount += arStatusAmount;
			}
			
		}	
	apInvoiceReportList = [];
	for(Map.Entry entry : apDataMap.entrySet()){
		tempMap = [:];
		statusId = entry.getKey();
		totalAmount = entry.getValue();		
		/*if(categoryTypeEnumMap[categoryTypeEnum]){
			categoryTypeEnum = categoryTypeEnumMap[categoryTypeEnum];
		}*/
		tempMap.putAt("statusId", statusMap[statusId]);
		tempMap.putAt("totalAmount", totalAmount);
		apInvoiceReportList.add(tempMap);
	}
	arInvoiceReportList = [];
	for(Map.Entry entry : arDataMap.entrySet()){
		tempMap = [:];
		statusId = entry.getKey();
		totalAmount = entry.getValue();
		/*if(categoryTypeEnumMap[categoryTypeEnum]){
			categoryTypeEnum = categoryTypeEnumMap[categoryTypeEnum];
		}*/
		tempMap.putAt("statusId",  statusMap[statusId]);
		tempMap.putAt("totalAmount", totalAmount);
		arInvoiceReportList.add(tempMap);
	}
	context.apInvoiceReportList = apInvoiceReportList;
	context.arInvoiceReportList = arInvoiceReportList;
	context.apInvoiceListSize = apInvoiceReportList.size();
	context.arInvoiceListSize = arInvoiceReportList.size();	
}
context.totalApAmount = totalApAmount;
context.totalArAmount = totalArAmount;

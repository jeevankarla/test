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
import org.ofbiz.network.NetworkServices;

def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
if(parameters.estimatedDeliveryDate){
	estimatedDeliveryDate = parameters.estimatedDeliveryDate;
}
if(context.estimatedDeliveryDate){
	estimatedDeliveryDate = context.estimatedDeliveryDate;
}
conditionList=[];
duesReportList =[];

Timestamp estimatedDeliveryDateTime = UtilDateTime.nowTimestamp();

try {
	estimatedDeliveryDateTime = new java.sql.Timestamp(sdf.parse(estimatedDeliveryDate).getTime());	
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+estimatedDeliveryDate, "");   
}
dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDateTime, timeZone, locale);

def adjustTotalsMap(currentFacilityMap,totalsMap){
	if(currentFacilityMap != null){
		Iterator treeMapIter = currentFacilityMap.entrySet().iterator();
		while (treeMapIter.hasNext()) {		
			Map.Entry entry = treeMapIter.next();			
			if(entry.getKey() == "grandTotal"){
				totalsMap["grandTotal"] += entry.getValue();
			}
		}	
						
	}
}//end of adjustTotalsMap method


def populateBoothPaymentDues(boothId ,boothDuesMap){	
	boothOrderItemsList=[];
	dueDetailsMap = [:];
	
	facilityDetails = delegator.findOne("Facility",[facilityId :boothId ], false);
	routeNo=facilityDetails.parentFacilityId;
	rtNo=routeNo.substring(2,routeNo.length());
	dueDetailsMap["routeNo"]=rtNo;
	dueDetailsMap["facilityTypeId"] = facilityDetails.facilityTypeId;
	dueDetailsMap["facilityName"] = facilityDetails.facilityName;
	dueDetailsMap["originFacilityId"]=boothId;
	dueDetailsMap["grandTotal"] = 0;	
	boothPaymentsList=[];	
	boothsPaymentsDetail = NetworkServices.getBoothPayments(delegator, dispatcher, userLogin, UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"), "INVOICE_APPROVED", boothId,null,Boolean.TRUE);
	boothPaymentsList = boothsPaymentsDetail.get("boothPaymentsList");
	if (boothPaymentsList.size() != 0) {
		Map boothPayment = (Map)boothPaymentsList.get(0);
		dueDetailsMap["grandTotal"] = (new BigDecimal(boothPayment.get("grandTotal"))).setScale(0 ,BigDecimal.ROUND_HALF_UP);	
	}
	if(UtilValidate.isNotEmpty(dueDetailsMap)){
		boothDuesMap.putAll(dueDetailsMap);
		if(dueDetailsMap["grandTotal"] !=0){
			duesReportList.add(dueDetailsMap);			
		}
		
	}
	
}

def populateRoutePaymentDues(routeId ,routeDuesMap){
	boothDuesMap=[:];
	totalsRouteMap=[:];
	routeDetails = delegator.findOne("Facility",[facilityId : routeId ], false);
	
	totalsRouteMap["facilityTypeId"] = routeDetails.facilityTypeId;
	totalsRouteMap["facilityName"] = routeDetails.facilityName;
	totalsRouteMap["originFacilityId"]=routeId;
	totalsRouteMap["grandTotal"] = 0;
	boothsList = delegator.findByAnd("Facility", [ parentFacilityId : routeId],["sequenceNum","facilityName"]);
		boothsList.each{ booth ->			
			populateBoothPaymentDues(booth.facilityId ,boothDuesMap);
			adjustTotalsMap(boothDuesMap,totalsRouteMap);			
		}
	if(UtilValidate.isNotEmpty(boothDuesMap)){
		routeDuesMap.putAll(totalsRouteMap);
		if(totalsRouteMap["grandTotal"] != 0 ){
			duesReportList.add(totalsRouteMap);
		}		
	}	
	
}

//populateRoutePaymentDues("TR1" ,routeOrdersMap);
def populateZonePaymentDues(zoneId){
	totalsZoneMap = [:];
	zoneDetails = delegator.findOne("Facility",[facilityId : zoneId ], false);
	
	totalsZoneMap["facilityTypeId"] = zoneDetails.facilityTypeId;
	totalsZoneMap["facilityName"] = zoneDetails.facilityName;
	totalsZoneMap["originFacilityId"]=zoneId;
	totalsZoneMap["grandTotal"] = 0;
	routesList = delegator.findByAnd("Facility", [parentFacilityId : zoneId],["sequenceNum","facilityName"]);
	routesList.each{ route ->
		routeDuesMap = [:];
		populateRoutePaymentDues(route.facilityId ,routeDuesMap);			
		adjustTotalsMap(routeDuesMap,totalsZoneMap);		
	}
	if(totalsZoneMap["grandTotal"] != 0 ){
		duesReportList.add(totalsZoneMap);
	}			
}
zoneDuesMap=[:];
zoneFacilityList = delegator.findByAnd("Facility", [facilityTypeId : "ZONE"],["sequenceNum","facilityName"]);
zoneFacilityList.each{ zoneFacility ->	
		populateZonePaymentDues(zoneFacility.facilityId);
	}
context.put("estimatedDeliveryDateTime", estimatedDeliveryDateTime);
pmShipDate=UtilDateTime.getDayStart(estimatedDeliveryDateTime,-1);
context.put("pmShipDate",pmShipDate);
context.duesReportList=duesReportList;






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



conditionList =[];
if(parameters.facilityId !="All-Routes"){
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.facilityId));
}
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
routeList = delegator.findList("Facility",condition,null,null,null,false);



routeWiseTotalMap=[:];
grandTotalMap =[:];
grandTotalMap["Leaks"] = BigDecimal.ZERO;
grandTotalMap["Frees"] = BigDecimal.ZERO;
grandTotalMap["Samples"] = BigDecimal.ZERO;
grandTotalMap["Auto_Short"] = BigDecimal.ZERO;
grandTotalMap["Van_Short"] =BigDecimal.ZERO;
grandTotalMap["Spoilage"] =BigDecimal.ZERO;
grandTotalMap["Total"] = BigDecimal.ZERO;
for(int i=0; i< routeList.size();i++){
	route = routeList.get(i);

	allBoothList=[];//all type of Booths will be added
	leakBooths=NetworkServices.getRouteBooths(delegator , route.facilityId,"LEAKS");
	allBoothList.addAll(leakBooths);
	freeBooths=NetworkServices.getRouteBooths(delegator , route.facilityId,"FREES");
	allBoothList.addAll(freeBooths);
	sampleBooths=NetworkServices.getRouteBooths(delegator , route.facilityId,"SAMPLES");
	allBoothList.addAll(sampleBooths);
	autoShortBooths=NetworkServices.getRouteBooths(delegator , route.facilityId,"AUTO_SHORT");
	allBoothList.addAll(autoShortBooths);
	vanShortBooths=NetworkServices.getRouteBooths(delegator , route.facilityId,"VAN_SHORT");
	allBoothList.addAll(vanShortBooths);
	spoilBooths=NetworkServices.getRouteBooths(delegator , route.facilityId,"SPOILS");
	allBoothList.addAll(spoilBooths);

	if(UtilValidate.isNotEmpty(allBoothList)){
		dayTotals = NetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:allBoothList,fromDate:dayBegin, thruDate:dayEnd]);
	}
	boothTotalsMap=dayTotals.get("boothTotals");

	if(UtilValidate.isNotEmpty(boothTotalsMap)){
		routeWiseBoothList=[];
		routeTotalMap =[:];//to add totals
		routeTotalMap["Leaks"] = BigDecimal.ZERO;
		routeTotalMap["Frees"] = BigDecimal.ZERO;
		routeTotalMap["Samples"] = BigDecimal.ZERO;
		routeTotalMap["Auto_Short"] = BigDecimal.ZERO;
		routeTotalMap["Van_Short"] =BigDecimal.ZERO;
		routeTotalMap["Spoilage"] =BigDecimal.ZERO;
		routeTotalMap["Total"] = BigDecimal.ZERO;

		allBoothList.each{ boothId->
			if(boothTotalsMap.getAt(boothId)){
				curntBoothTotal=boothTotalsMap.getAt(boothId).getAt("total");
				if(leakBooths.contains(boothId)){
					routeTotalMap["Leaks"]+=curntBoothTotal;
				}
				if(freeBooths.contains(boothId)){
					routeTotalMap["Frees"]+=curntBoothTotal;
				}
				if(sampleBooths.contains(boothId)){
					routeTotalMap["Samples"]+=curntBoothTotal;
				}
				if(autoShortBooths.contains(boothId)){
					routeTotalMap["Auto_Short"]+=curntBoothTotal;
				}
				if(vanShortBooths.contains(boothId)){
					routeTotalMap["Van_Short"]+=curntBoothTotal;
				}
				if(spoilBooths.contains(boothId)){
					routeTotalMap["Spoilage"]+=curntBoothTotal;
				}
				routeTotalMap["Total"]+=curntBoothTotal;//irrespective of categorytotal adding present totals
			}
		}
		grandTotalMap["Leaks"] += routeTotalMap.getAt("Leaks");
		grandTotalMap["Frees"] +=routeTotalMap.getAt("Frees");
		grandTotalMap["Samples"] += routeTotalMap.getAt("Samples");
		grandTotalMap["Auto_Short"] += routeTotalMap.getAt("Auto_Short");
		grandTotalMap["Van_Short"] +=routeTotalMap.getAt("Van_Short");
		grandTotalMap["Spoilage"] +=routeTotalMap.getAt("Spoilage");
		grandTotalMap["Total"] += routeTotalMap.getAt("Total");

		tempTotalMap=[:];
		tempTotalMap.putAll(routeTotalMap);

		routeWiseTotalMap[route.facilityId]=tempTotalMap;
	}
}
context.put("routeWiseTotalMap", routeWiseTotalMap);
context.put("grandTotalMap", grandTotalMap);
//context.put("grandTotalMap", grandTotalMap);



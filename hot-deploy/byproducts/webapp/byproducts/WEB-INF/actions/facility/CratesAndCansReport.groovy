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
	
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	
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
	import org.ofbiz.service.ServiceUtil;
	import in.vasista.vbiz.byproducts.ByProductServices;
	import in.vasista.vbiz.byproducts.ByProductReportServices;
	import javolution.util.FastMap;
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
		
	effectiveDateStr = parameters.fromDate;
	thruEffectiveDateStr = parameters.thruDate;
	
	if (UtilValidate.isEmpty(effectiveDateStr)) {
		effectiveDate = UtilDateTime.nowTimestamp();
	}
	else{
		def sdf = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
		}
	}
	if (UtilValidate.isEmpty(thruEffectiveDateStr)) {
		thruEffectiveDate = effectiveDate;
	}
	else{
		def sdf = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			thruEffectiveDate = new java.sql.Timestamp(sdf.parse(thruEffectiveDateStr+" 00:00:00").getTime());
		}catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + thruEffectiveDate, "");
		}
	}
	def sf = new SimpleDateFormat("dd MMMMM, yyyy");
	startDate = UtilDateTime.getDayStart(effectiveDate);
	endDate = UtilDateTime.getDayEnd(thruEffectiveDate);
	context.put("dayBegin",effectiveDateStr);
	context.put("dayEnd",thruEffectiveDateStr);
	
routeWiseCratesMap = [:];
routeMap=[:];
conditionList=[];
shipmentIds=[];
routeIdsList=[];
routeWiseSaleMap =[:];
estimatedShipDateList=[];
List<GenericValue> vehicleTripStatusList=FastList.newInstance();
List routeVehicleCratesList=FastList.newInstance();
DayWiseSaleMap = [:];
		
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,startDate));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,endDate));
	
		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<GenericValue> shipmentList = delegator.findList("Shipment", cond, null,UtilMisc.toList("routeId"), null, false);
		shipmentIds.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
		routeIdsList.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "routeId", false));
		estimatedShipDateList.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "estimatedShipDate", false));
		if(UtilValidate.isNotEmpty(shipmentList)){
			for(i=0;i<shipmentIds.size();i++){
				Map cratesMap = FastMap.newInstance();
				shipmentId=shipmentIds.get(i);
				routeId=routeIdsList.get(i);
				estimatedShipDate=(Timestamp)estimatedShipDateList.get(i);
				if(UtilValidate.isNotEmpty(shipmentId)){
					cratesSent=0;cratesReceived=0;cansSent=0;cansReceived=0;
					GenericValue crateCanAcct = delegator.findOne("CrateCanAccount", UtilMisc.toMap("shipmentId", shipmentId), false);
					if(UtilValidate.isNotEmpty(crateCanAcct)){
						cratesSent=crateCanAcct.cratesSent;
						cratesReceived=crateCanAcct.cratesReceived;
						cansSent=crateCanAcct.cansSent;
						cansReceived=crateCanAcct.cansReceived;
					}
					cratesMap.put("cratesSent",cratesSent);
					cratesMap.put("cratesReceived",cratesReceived);
					cratesMap.put("cansSent",cansSent);
					cratesMap.put("cansReceived",cansReceived);
					
					routeId = routeId;
					shipDate = estimatedShipDate;
					if(UtilValidate.isEmpty(routeWiseCratesMap[routeId])){
						dayWiseMap = [:];
						dayWiseMap[shipDate] = cratesMap;
						tempRouteCrateMap = [:];
						tempRouteCrateMap.putAll(dayWiseMap);
						routeWiseCratesMap[routeId] = tempRouteCrateMap;
					}
					else{
						dayWiseMap = routeWiseCratesMap[routeId];
						dayWiseMap[shipDate] = cratesMap;
						tempRouteCrateMap = [:];
						tempRouteCrateMap.putAll(dayWiseMap);
						routeWiseCratesMap[routeId] = tempRouteCrateMap;
					}
					if(UtilValidate.isEmpty(routeWiseSaleMap[routeId])){
						tempMap = [:];
						tempMap["cratesSent"]=cratesMap.get("cratesSent");
						tempMap["cratesReceived"]=cratesMap.get("cratesReceived");
						tempMap["cansSent"]=cratesMap.get("cansSent");
						tempMap["cansReceived"]=cratesMap.get("cansReceived");
						routeWiseSaleMap[routeId]=tempMap;
					 }else{
						 tempMap = [:];
						 tempMap.putAll(routeWiseSaleMap.get(routeId));
						 tempMap["cratesSent"] += cratesMap.get("cratesSent");
						 tempMap["cratesReceived"] += cratesMap.get("cratesReceived");
						 tempMap["cansSent"]=cratesMap.get("cansSent");
						 tempMap["cansReceived"]=cratesMap.get("cansReceived");
						 routeWiseSaleMap[routeId] = tempMap;
					 }
				   }
			}
		}
context.routeWiseSaleMap = routeWiseSaleMap;
context.routeWiseCratesMap = routeWiseCratesMap;
	
	
	


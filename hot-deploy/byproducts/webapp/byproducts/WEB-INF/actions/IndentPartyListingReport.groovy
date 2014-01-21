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
	import org.ofbiz.service.DispatchContext;
	import java.math.BigDecimal;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
	import in.vasista.vbiz.byproducts.ByProductServices;
	
	/*rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	context.rounding = rounding;*/
	
	effectiveDate = null;
	effectiveDateStr = parameters.supplyDate;
	
	if (UtilValidate.isEmpty(effectiveDateStr)) {
		effectiveDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			effectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(effectiveDateStr));
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
		}
	}
	dayBegin = UtilDateTime.getDayStart(effectiveDate);
	dayEnd = UtilDateTime.getDayEnd(effectiveDate);
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	partyList = [:];
	routeMap = [:];
	conditionList=[];
	
	
	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.IN, ["AM","PM"]));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["facilityId", "sequenceNum","ownerPartyId","facilityName"] as Set;
	subscriptionsItemsList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["sequenceNum","facilityId"], null, false);
	routes = ByProductServices.getByproductRoutes(delegator).getAt("routeIdsList");
	if(routes){
		routes.each{eachRoute ->
			subscriptionNewItemsList= EntityUtil.filterByCondition(subscriptionsItemsList, EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, eachRoute));
			
			routeBoothDetailList = [];
			List activeFacilityList = [];
			
			if(subscriptionNewItemsList){
				subscriptionNewItemsList.each { eachItem ->
					area="";
					detailMap = [:];
					facilityId = eachItem.get("facilityId");
					ownerPartyId = eachItem.get("ownerPartyId");
					facilityName = eachItem.get("facilityName");
					
					
					
					if(!activeFacilityList.contains(facilityId)){
						activeFacilityList.add(facilityId);
						detailMap["facilityId"] = facilityId;
						detailMap["facilityName"] = facilityName;
						partyAddress = dispatcher.runSync("getPartyPostalAddress", [partyId: ownerPartyId, userLogin: userLogin]);
						if(partyAddress.address1 || partyAddress.city){
							area = partyAddress.address1;
							detailMap["area"] = area;
						}
      					tempRouteBoothMap = [:];
						tempRouteBoothMap.putAll(detailMap);
						
						routeBoothDetailList.addAll(tempRouteBoothMap);
						
						
					}
					
					
					
					
					
					
					/*if(partyList.containsKey(facilityId)){
						facList = partyList.getAt(facilityId);
						seqNum = facList.get("byProdRouteId");
						if(!seqNum.contains(byProdRouteId)){
							seqNum.add(byProdRouteId);
							facList.byProdRouteId = seqNum;
							partyList.putAt(facilityId,facList);
						}
					}
					else{
						detailMap["byProdRouteId"] = [byProdRouteId];
						detailMap["facilityId"] = facilityId;
						detailMap["facilityName"] = facilityName;
						partyAddress = dispatcher.runSync("getPartyPostalAddress", [partyId: ownerPartyId, userLogin: userLogin]);
						if(partyAddress.address1 || partyAddress.city){
							area = partyAddress.address1;
							detailMap["area"] = area;
						}
						partyList.putAt(facilityId, detailMap);
		}*/
				}
        }
			tempRouteList = [];
			tempRouteList.addAll(routeBoothDetailList);
			routeBoothDetailList.clear();
			routeMap.put(eachRoute,tempRouteList);
		}
	}
	context.routeMap = routeMap;
	context.indentDate = UtilDateTime.toDateString(effectiveDate, "dd.MM.yyyy");
	return "success";
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
	
	fromDate = null;
	thruDate = null;
	fromDateStr = parameters.fromDate;
	thruDateStr = parameters.thruDate;
	partyList = [:];
	if (UtilValidate.isEmpty(fromDateStr) && UtilValidate.isEmpty(thruDateStr)) {
		fromDate = UtilDateTime.nowTimestamp();
		thruDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			fromDate = UtilDateTime.toTimestamp(dateFormat.parse(fromDateStr));
			thruDate = UtilDateTime.toTimestamp(dateFormat.parse(thruDateStr));
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
		}
	}
	fromDateDayBegin = UtilDateTime.getDayStart(fromDate);
	thruDateDayEnd = UtilDateTime.getDayEnd(thruDate);
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	conditionList=[];
	
	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.IN, ["AM","PM"]));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateDayBegin));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDateDayEnd));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	subscriptionsItemsList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null , ["sequenceNum","facilityId"], null, false);
	routes = ByProductServices.getByproductRoutes(delegator).getAt("routeIdsList");
	
	if(routes){
		routes.each{eachRoute ->
			subscriptionNewItemsList= EntityUtil.filterByCondition(subscriptionsItemsList, EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, eachRoute));
			if(subscriptionNewItemsList){
				subscriptionNewItemsList.each { eachItem ->
					area="";
					detailMap = [:];
					facilityId = eachItem.get("facilityId");
					byProdRouteId = eachItem.get("sequenceNum");
					ownerPartyId = eachItem.get("ownerPartyId");
					facilityName = eachItem.get("facilityName");
					if(partyList.containsKey(facilityId)){
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
					}
				}
			}
		}
	}
	
	context.partyList = partyList;
	context.indentDate = UtilDateTime.toDateString(fromDate, "MMMMM.yyyy");
	return "success";
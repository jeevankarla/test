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
import org.ofbiz.network.NetworkServices;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import java.util.SortedMap;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;

customTimePeriod = parameters.customTimePeriodId;
parlourId = parameters.parlourId;

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.putAt("fromDateTime", fromDateTime);
monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
monthDate = UtilDateTime.toDateString(monthBegin, "MMMMM - yyyy");
context.monthDate = monthDate;

dctx = dispatcher.getDispatchContext();
finalParloursList =  [];
productsPrice_Parlour = ByProductReportServices.getByProductPricesForPartyClassification(dctx, UtilMisc.toMap("userLogin", userLogin, "partyClassificationId", "PM_RC_P")).get("productsPrice");
productsPrice_WS = ByProductReportServices.getByProductPricesForPartyClassification(dctx, UtilMisc.toMap("userLogin", userLogin, "partyClassificationId", "PM_RC_W")).get("productsPrice");
		
conditionList = [];
if(parameters.parlourId){
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, parameters.parlourId));
}
conditionList.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, "PARLOR_SALES_CHANNEL"));
conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL,"ORDER_REJECTED"));
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin));
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, monthEnd));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
ordersList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, ["productId","quantity","originFacilityId"] as Set, ["originFacilityId","productId"], null, false);
parlourList = EntityUtil.getFieldListFromEntityList(ordersList, "originFacilityId", true);
if(parlourList){
	parlourList.each{ eachParlour ->
		parlourMap = [:];
		eachParlourOrdersList = EntityUtil.filterByCondition(ordersList, EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, eachParlour));
		qtyMap = [:];
		if(eachParlourOrdersList){
			eachParlourOrdersList.each{eachItem ->
				prodId = eachItem.getAt("productId");
				quantity = eachItem.getAt("quantity");
				if(qtyMap.containsKey(prodId)){
					getTempMap = [:];
					getTempMap = qtyMap.get(prodId);
					extQty = getTempMap.getAt("quantity");
					totalQuantity = extQty.add(quantity);
					getTempMap.quantity = totalQuantity;
					qtyMap.putAt(prodId,getTempMap);
				}else{
					tempMap = [:];
					tempMap.quantity = eachItem.getAt("quantity");
					if(productsPrice_Parlour){
						parlourPriceList = productsPrice_Parlour.get(prodId);
						basic = parlourPriceList.get("basicPrice");
						bed = parlourPriceList.get("BED");
						bedcess = parlourPriceList.get("BEDCESS");
						bedseccess = parlourPriceList.get("BEDSECCESS");
						unitCost = basic+bed+bedcess+bedseccess;
						vat = parlourPriceList.get("VAT");
						vatPercentage = parlourPriceList.get("vatPercentage");
						tempMap.putAt("BasicParlourPrice", unitCost);
						tempMap.putAt("P_VAT_amount", vat);
						tempMap.putAt("P_VAT_percent", vatPercentage);
					}
					if(productsPrice_WS){
						WSPriceList = productsPrice_WS.get(prodId);
						basic = WSPriceList.get("basicPrice");
						bed = WSPriceList.get("BED");
						bedcess = WSPriceList.get("BEDCESS");
						bedseccess = WSPriceList.get("BEDSECCESS");
						unitCost = basic+bed+bedcess+bedseccess;
						vat = WSPriceList.get("VAT");
						vatPercentage = WSPriceList.get("vatPercentage");
						tempMap.putAt("BasicWholeSalePrice", unitCost);
						tempMap.putAt("W_VAT_amount", vat);
						tempMap.putAt("W_VAT_percent", vatPercentage);
					}
					qtyMap.putAt(prodId, tempMap);
				}
			}
		}
		parlourMap.putAt(eachParlour, qtyMap);
		finalParloursList.addAll(parlourMap);
	}
}
context.finalParloursList=finalParloursList;

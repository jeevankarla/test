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


fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
fromDateTime = null;
thruDateTime = null;
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());
			thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());
		}catch (Exception e) {
			Debug.logError(e, "Cannot parse date string: "+thruDate, "");
		 }
	dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	context.put("dayBegin",dayBegin);
	context.put("dayEnd",dayEnd);
	
	lmsproductList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
	List exprList = [];	
	exprList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS  ,"SALES_ORDER"));
	exprList.add(EntityCondition.makeCondition("shipmentStatusId", EntityOperator.EQUALS , "GENERATED"));
	exprList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	exprList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
	exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
	if(parameters.facilityId != "All-Routes" && parameters.facilityId != null ){
		exprList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS , parameters.facilityId));
		orderBy = ["originFacilityId","parentFacilityId"];
	}else{
		orderBy = ["originFacilityId","parentFacilityId"];
	}
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	fieldsToSelect = ["orderId", "orderDate", "productId","parentFacilityId", "quantity" ,"grandTotal" ,"productSubscriptionTypeId" ,"originFacilityId" ,"unitPrice" ,"shipmentId","estimatedShipDate" ,"quantityIncluded"] as Set;
	orderItemProductList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, fieldsToSelect, orderBy , null, false);
	
	Map quantityMap =FastMap.newInstance();
	quantityMap["Qty"]=0;
	quantityMap["disc"]=0;
	Map productsMap =FastMap.newInstance();
	context.put("lmsproductList",lmsproductList);
	lmsproductList.each{ product ->
		productsMap[product.productId] = quantityMap;		
	}
	productsMap["TOTAL"]=0;
	productsMap["TotComn"]=0;
	conditionList =[];
	if(parameters.facilityId !="All-Routes"){
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.facilityId));
	}
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	routeList = delegator.findList("Facility",condition,null,null,null,false);
	routeTotalsMap =[:];
	Map routeWiseMap = FastMap.newInstance();
	for(int i=0; i< routeList.size();i++){		
		route = routeList.get(i);
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS , route.facilityId));
		conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"BOOTH"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		boothsList = delegator.findList("Facility",condition,null,null,null,false);
		orderItemList = EntityUtil.orderBy(EntityUtil.filterByCondition(orderItemProductList, EntityCondition.makeCondition("originFacilityId",EntityOperator.IN , boothsList.facilityId)),["originFacilityId"]);
		tempBoothId = "";
		routeWiseProductMap =[:];
		routeWiseProductMap.putAll(productsMap);
		routeTotalsMap.put(route.facilityId, routeWiseProductMap);
		tempRouteSubMap =[:];
		tempRouteSubMap = routeTotalsMap.get(route.facilityId);
		Map boothWiseProductsMap =FastMap.newInstance();
		
		if(UtilValidate.isNotEmpty(orderItemList)){
			for(int j=0; j< orderItemList.size(); j++){
				orderItem = orderItemList.get(j);				
				productId = orderItem.productId;
				quantity = (orderItem.quantity*orderItem.quantityIncluded);				
				if(tempBoothId ==""){
					tempBoothId = orderItem.originFacilityId;
					boothWiseProductsMap.put(tempBoothId, productsMap);					
				}
				if(tempBoothId != orderItem.originFacilityId){
					boothWiseProductsMap.put(tempBoothId, tempSubMap);
					tempBoothId = orderItem.originFacilityId;
					if(UtilValidate.isEmpty(boothWiseProductsMap.get(tempBoothId))){
						boothWiseProductsMap.put(tempBoothId, productsMap);
					}
					
				}
				
				Map productWiseQtyMap =FastMap.newInstance();
				Map productWiseMap = FastMap.newInstance();
				productWiseMap.putAll(boothWiseProductsMap.get(tempBoothId));				
				productWiseQtyMap.putAll(productWiseMap.get(productId));
				productWiseMap["TOTAL"] +=quantity;								
				productWiseQtyMap["Qty"] +=quantity;
				
				tempRouteProductMap =[:];
				tempRouteProductMap.putAll(tempRouteSubMap.get(productId));
				tempRouteProductMap["Qty"] += quantity;
				
				
				
				facilityDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId", tempBoothId), false);
				Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);	
				rateTypeId = facilityDetails.categoryTypeEnum+"_MRGN";
				if("VENDOR".equals(facilityDetails.categoryTypeEnum)){
					rateTypeId = "VENDOR_DEDUCTION";
				}		
				inputRateAmt.put("rateTypeId", rateTypeId);
				inputRateAmt.put("periodTypeId", "RATE_HOUR");
				inputRateAmt.put("partyId", facilityDetails.get("ownerPartyId"));
				inputRateAmt.put("productId", productId);			
				inputRateAmt.put("rateCurrencyUomId","INR");				
				 
				rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);
				 normalMargin =0;
				 if(UtilValidate.isNotEmpty(rateAmount)){
					 normalMargin =  rateAmount.get("rateAmount");
				 }
				 productWiseQtyMap["disc"] =normalMargin;	
				 tempRouteProductMap["disc"] += normalMargin;				 
				 productWiseMap["TotComn"] +=(quantity*normalMargin);
				 
				 tempRouteSubMap.put(productId, tempRouteProductMap);
				 tempRouteSubMap["TOTAL"] +=quantity;
				 tempRouteSubMap["TotComn"] +=(quantity*normalMargin);
				
				tempMap =[:];
				tempMap.putAll(productWiseQtyMap);
				productWiseMap.put(productId, tempMap);
				tempSubMap=[:];
				tempSubMap.putAll(productWiseMap);
				boothWiseProductsMap.put(tempBoothId, tempSubMap);
				if((j == orderItemList.size()-1)){
					boothWiseProductsMap.put(tempBoothId, tempSubMap);
					tempBoothId = orderItem.originFacilityId;
				}
			}
			tempBoothMap=[:];
			tempBoothMap.putAll(boothWiseProductsMap);
			if(UtilValidate.isNotEmpty(tempBoothMap)){
				routeWiseMap.put(route.facilityId, tempBoothMap);
			}
		}
		routeTotalsMap.put(route.facilityId, tempRouteSubMap);	
		
	}
	
	context.put("routeWiseMap",routeWiseMap);
	context.put("routeTotalsMap",routeTotalsMap);
	
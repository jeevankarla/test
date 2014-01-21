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

shipmentIds=[];
shipmentId=parameters.shipmentId;
GenericValue shipment =null;
if(! UtilValidate.isEmpty(shipmentId)){
	shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
}
if(parameters.shipmentId){
	shipmentId = parameters.shipmentId;
	shipmentIds.add(shipmentId);
}
supplyDate = parameters.supplyDate;
supplyDateTime =UtilDateTime.nowTimestamp();
if(! UtilValidate.isEmpty(supplyDate)){
	def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			supplyDateTime = new java.sql.Timestamp(sdf.parse(supplyDate+" 00:00:00").getTime());
			
		} catch (Exception e) {
			Debug.logError(e, "Cannot parse date string: "+supplyDate, "");
		}
}
dayBegin= UtilDateTime.getDayStart(supplyDateTime);
context.putAt("supplyDateTime", dayBegin);
dctx = dispatcher.getDispatchContext();

lmsproductList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
List<GenericValue> byProductsList =FastList.newInstance();
List condList =FastList.newInstance();
productsLmsBulkList = ProductWorker.getProductsByCategory(delegator ,"LMS_BULK" ,null);
flavredProductList = ProductWorker.getProductsByCategory(delegator ,"FLAVERD_MILK" ,null);
butterProductList = ProductWorker.getProductsByCategory(delegator ,"BUTTER_MILK" ,null);
bulkMilkProducts = EntityUtil.getFieldListFromEntityList(productsLmsBulkList, "productId", true);
flavredProductList = EntityUtil.getFieldListFromEntityList(flavredProductList, "productId", true);
butterProductList = EntityUtil.getFieldListFromEntityList(butterProductList, "productId", true);
context.put("bulkMilkProducts", bulkMilkProducts);
context.put("flavredProductList", flavredProductList);
context.put("butterProductList", butterProductList);
condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, "BYPROD"));
condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);

byProductsList =delegator.findList("ProductAndCategoryMember", discontinuationDateCondition,null, ["sequenceNum"], null, false);
context.byProductsList =byProductsList;
context.lmsproductList = EntityUtil.filterByCondition(lmsproductList, EntityCondition.makeCondition("productId",EntityOperator.NOT_IN , byProductsList.productId));

if(! UtilValidate.isEmpty(shipment)){
		dayBegin = UtilDateTime.getDayStart(shipment.estimatedShipDate);
		context.putAt("supplyDateTime", dayBegin);
	}
	
	if(!UtilValidate.isEmpty(parameters.supplyDate)){
		dayBegin= UtilDateTime.getDayStart(supplyDateTime);
		context.putAt("supplyDateTime", dayBegin);
		shipmentIds = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
	}
	List exprList = [];
	exprList.add(EntityCondition.makeCondition("shipmentId",  EntityOperator.IN ,shipmentIds));
	exprList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS  ,"SALES_ORDER"));
	exprList.add(EntityCondition.makeCondition("shipmentStatusId", EntityOperator.EQUALS , "GENERATED"));
	exprList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	exprList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	
	if(parameters.facilityId != "All-Routes" && parameters.facilityId != null ){
		exprList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS , parameters.facilityId));
		orderBy=null;
	}else{
		orderBy = ["originFacilityId","parentFacilityId"];
	}
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	orderItemlmsproductList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, orderBy , null, false);
	
	lmsProductsMap =[:];
	subscriptionTypeMap =[:];
	List productSubscriptionTypeList = [];
	productSubscriptionTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "SUB_PROD_TYPE"), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"), null, false);
	
	//initialization of products
	lmsproductList.each{ product ->
		lmsProductsMap[product.productId] = '';
	}	
	productSubscriptionTypeList.each{ subscriptionType ->
		subscriptionTypeMap.put(subscriptionType.enumId, lmsProductsMap);
	}
	conditionList = [];
	if(parameters.facilityId !="All-Routes"){
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.facilityId));
	}
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	routeList = delegator.findList("Facility",condition,null,null,null,false);
		
	routeWiseMap =[:];	
	routeTotalsMap =[:];   
	routeCratesCansMap=[:];
	for(int i=0; i< routeList.size();i++){
		routeWiseSubsciptionMap =[:];
		routeWiseSubsciptionMap.putAll(subscriptionTypeMap);
		
		tempBoothId = "";
		Map boothWiseProductsMap =FastMap.newInstance();
		route = routeList.get(i);	
		
		conditionList.clear();	
		conditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS , route.facilityId));
		conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"BOOTH"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		boothsList = delegator.findList("Facility",condition,null,null,null,false);
		orderItemList = EntityUtil.filterByCondition(orderItemlmsproductList, EntityCondition.makeCondition("originFacilityId",EntityOperator.IN , boothsList.facilityId));
		
		
		routeTotalsMap.put(route.facilityId, routeWiseSubsciptionMap);
		tempWiseRouteSubMap =[:];
		tempWiseRouteSubMap = routeTotalsMap.get(route.facilityId);
		productWiseMap =[:];
		Map subscriptionMap =FastMap.newInstance();
		subscriptionMap["TOTAMT"] =0;
		subscriptionMap["PREVDUE"] =0;
		if(UtilValidate.isNotEmpty(orderItemList)){
		for(int j=0; j< orderItemList.size(); j++){
			orderItem = orderItemList.get(j);
			productSubscriptionMap =[:];		
			productId = orderItem.productId;
			quantity = orderItem.quantity;
			if(tempBoothId ==""){
				tempBoothId = orderItem.originFacilityId;
				subscriptionMap.putAll(subscriptionTypeMap);
				subscriptionMap["TOTAMT"] =0;
				subscriptionMap["PREVDUE"] =0;
			}
			if(tempBoothId != orderItem.originFacilityId){			
				boothWiseProductsMap.put(tempBoothId, pushSubMap);
				subscriptionMap.clear();
				subscriptionMap.putAll(subscriptionTypeMap);
				subscriptionMap["TOTAMT"] =0;
				subscriptionMap["PREVDUE"] =0;
				tempBoothId = orderItem.originFacilityId;
			}
			productSubscriptionMap.putAll(subscriptionMap[orderItem.productSubscriptionTypeId]);
			if(UtilValidate.isEmpty(productSubscriptionMap[orderItem.productId])){
				productSubscriptionMap[orderItem.productId] =quantity;
			}else{
				if(productSubscriptionMap[orderItem.productId] !=null){
					productSubscriptionMap[orderItem.productId] +=quantity;
				}
			}
			tempMap =[:];
			tempMap.putAll(productSubscriptionMap);
			subscriptionMap.put(orderItem.productSubscriptionTypeId, tempMap);
			subscriptionMap["TOTAMT"] += (quantity * orderItem.unitPrice);	
			//getting previous due
			Map<String, Object> boothPayments = NetworkServices.getBoothPayments(delegator, dctx.getDispatcher(), userLogin, UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"), null, tempBoothId ,null ,Boolean.FALSE);
			Map<String, Object> currentBoothPayments = NetworkServices.getBoothReceivablePayments(delegator, dctx.getDispatcher(), userLogin, UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"), null, tempBoothId ,null ,Boolean.TRUE, Boolean.FALSE);
			Map boothTotalDues = FastMap.newInstance();
			boothTotalDues["totalDue"]=BigDecimal.ZERO;
			boothTotalDues["grandTotal"]=BigDecimal.ZERO;
			List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
			if (UtilValidate.isNotEmpty(boothPaymentsList)) {
				 boothTotalDues = (Map)boothPaymentsList.get(0);
			}
			Map currentBoothTotalDues = FastMap.newInstance();
			currentBoothTotalDues["grandTotal"]=BigDecimal.ZERO;
			List currentBoothPaymentsList = (List) currentBoothPayments.get("boothPaymentsList");
			if (UtilValidate.isNotEmpty(currentBoothPaymentsList)) {
				 currentBoothTotalDues = (Map)currentBoothPaymentsList.get(0);
			}
			subscriptionMap["PREVDUE"] =((BigDecimal)boothTotalDues.getAt("totalDue")).subtract((BigDecimal)boothTotalDues.getAt("grandTotal"));
			tempRouteProductMap =[:];
			tempRouteProductMap.putAll(tempWiseRouteSubMap.get(orderItem.productSubscriptionTypeId));
			if(UtilValidate.isEmpty(tempRouteProductMap.get(orderItem.productId))){
				tempRouteProductMap[orderItem.productId]= quantity;
			}else{
				tempRouteProductMap[orderItem.productId] = tempRouteProductMap.get(orderItem.productId)+quantity;
			}
			tempWiseRouteSubMap.put(orderItem.productSubscriptionTypeId, tempRouteProductMap);
			pushSubMap=[:];
			pushSubMap.putAll(subscriptionMap);
			if((j == orderItemList.size()-1)){
				boothWiseProductsMap.put(tempBoothId, pushSubMap);
				tempBoothId = orderItem.originFacilityId;
			}			
		}	
		//crates and cans logic starts here 
		typeAndCountMap =[:];
		typeAndCountMap["NOCRATES"] = 0;
		typeAndCountMap["CANS20"] = 0;
		typeAndCountMap["CANS30"] = 0;
		typeAndCountMap["CANS40"] = 0;
		Iterator treeMapIter = tempWiseRouteSubMap.entrySet().iterator();
		while(treeMapIter.hasNext()){
			Map.Entry entry = treeMapIter.next();
		Iterator productTreeMapIter = entry.getValue().entrySet().iterator();
			while(productTreeMapIter.hasNext()){
				Map.Entry productEntry = productTreeMapIter.next();
				if(UtilValidate.isNotEmpty(productEntry.getValue())){
					productDetail = delegator.findOne("Product", UtilMisc.toMap("productId"  ,productEntry.getKey()) ,false);
					cratesTotalSub =((productEntry.getValue()/(int)(12/productDetail.quantityIncluded)));
				litres= (productEntry.getValue()*(productDetail.quantityIncluded));
				if(bulkMilkProducts.contains(productEntry.getKey())){
				//setting cans value to zero for Route level
						if(litres>40){
							cansTotal =(litres/40).intValue();
							typeAndCountMap.putAt("CANS40", typeAndCountMap.getAt("CANS40").intValue()+cansTotal+0);
							remingTotal =((litres.intValue())%40);
							if((remingTotal>=1)&&(remingTotal<=20)){
								typeAndCountMap.putAt("CANS20",typeAndCountMap.getAt("CANS20").intValue()+1);
							}else if((remingTotal>20)&&(remingTotal<=30)){
							typeAndCountMap.putAt("CANS30",typeAndCountMap.getAt("CANS30").intValue()+1);
							}else if ((remingTotal>30)&&(remingTotal<=40)){
								typeAndCountMap.putAt("CANS40",typeAndCountMap.getAt("CANS40").intValue()+1);
							}
						}else if((litres<=40)&&(litres>30)){
							typeAndCountMap.putAt("CANS40",typeAndCountMap.getAt("CANS40").intValue()+1);
						}else if((litres<=30)&&(litres>20)){
							typeAndCountMap.putAt("CANS30",typeAndCountMap.getAt("CANS30").intValue()+1);
						}else if((litres>=1)&&(litres<=20)){
							typeAndCountMap.putAt("CANS20",typeAndCountMap.getAt("CANS20").intValue()+1);
						}
						}else{
						  if((!flavredProductList.contains(productEntry.getKey()))&&(!butterProductList.contains(productEntry.getKey()))){//flavredmilk and ButterMilk will not have crates
						  typeAndCountMap.putAt("NOCRATES",typeAndCountMap.getAt("NOCRATES").intValue()+cratesTotalSub.intValue());
						 }
					   }
				   }
			    }//while
		     }//outer while
		 routeCratesCansMap.put(route.facilityId,typeAndCountMap);
		    }//notEmptyCheck
		tempBoothMap=[:];
		tempBoothMap.putAll(boothWiseProductsMap);
		if(UtilValidate.isNotEmpty(tempBoothMap)){
			routeWiseMap.put(route.facilityId, tempBoothMap);
			}
		routeTotalsMap.put(route.facilityId, tempWiseRouteSubMap);		
	}	
	context.putAt("routeTotalsMap", routeTotalsMap);
	context.putAt("routeCratesCansMap", routeCratesCansMap);
	context.routeWiseMap =routeWiseMap;

	
	

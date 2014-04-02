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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;
/*rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
context.rounding = rounding;*/

routesList = [];
conditionList=[];
if(parameters.routeId !="All-Routes"){
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.routeId));
}
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
routesList = delegator.findList("Facility",condition,null,null,null,false);


List productList = [];


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
context.put("effectiveDate",effectiveDate);

piecesPerCrate=[:];
piecesPerCan=[:];
result =ByProductNetworkServices.getProductCratesAndCans(dctx, UtilMisc.toMap("userLogin",userLogin, "saleDate", effectiveDate));
piecesPerCrate = result.get("piecesPerCrate");
piecesPerCan = result.get("piecesPerCan");

crateProductsList=ProductWorker.getProductsByCategory(delegator ,"CRATE" ,null);
canProductsList=ProductWorker.getProductsByCategory(delegator ,"CAN" ,null);

crateProductsIdsList=EntityUtil.getFieldListFromEntityList(crateProductsList, "productId", false);
canProductsIdsList=EntityUtil.getFieldListFromEntityList(canProductsList, "productId", false);

conditionList=[];
routeMap = [:];
routeWiseIndentMap = [:];
grandProdTotal=[:];
allProdGrandTotal=[:];


for(i=0; i<routesList.size(); i++){
	grandTotalMap =[:];
	productFinalMap = [:];
	routeTotQty=0;
	rtCrates = 0;
	rtExcessPkts = 0;
	rtLooseCrates = 0;
	rtCans = 0;
	rtLooseCans = 0;
	route = routesList.get(i);
	routeId=route.get("facilityId");
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "BOOTH"));
	if(UtilValidate.isEmpty(parameters.subscriptionTypeId)){
	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.IN, ["AM","PM"]));
	}else{
	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, parameters.subscriptionTypeId));
	}
	conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd),
				   EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId", "quantity","productSubscriptionTypeId", "sequenceNum"] as Set;
	subscriptionsItemsList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["productId"], null, false);
	if(subscriptionsItemsList){
		subscriptionsItemsList.each { eachItem ->
			productId = eachItem.get("productId");
			product = delegator.findOne("Product", UtilMisc.toMap("productId" : productId), false);
			BigDecimal indentQty = (BigDecimal) eachItem.get("quantity");
			productSubscriptionTypeId = eachItem.get("productSubscriptionTypeId");
			
			detailMap = [:];
			detailMap["productId"] = productId;
			detailMap["productName"] = product.get("brandName");
			
			if(productSubscriptionTypeId == "SPECIAL_ORDER"){
				detailMap["otherQuantity"] = BigDecimal.ZERO;
				detailMap["splQuantity"] = indentQty;
			}else{
				detailMap["otherQuantity"] = indentQty;
				detailMap["splQuantity"] = BigDecimal.ZERO;
			}
			tempTotalsMap = [:];
			tempTotalsMap.putAll(detailMap);
			
			if(UtilValidate.isEmpty(grandTotalMap[productId])){
				grandTotalMap[productId] = tempTotalsMap;
			}else{
				updateDetailMap = grandTotalMap[productId];
				if(productSubscriptionTypeId == "SPECIAL_ORDER"){
					updateDetailMap["splQuantity"] += indentQty;
				}else{
					updateDetailMap["otherQuantity"] += indentQty;
				}
				tempUpdateMap = [:];
				tempUpdateMap.putAll(updateDetailMap);
				grandTotalMap.put(productId, tempUpdateMap);
			}
		}
	}
	else{
		continue;
	}
	tempRouteMap = [:];
	tempRouteMap.putAll(grandTotalMap);
	
	grandTotalMap.each{prodEntry->
		productId=prodEntry.getKey();
		qty=prodEntry.getValue().get("otherQuantity");
		if(UtilValidate.isEmpty(grandProdTotal.get(productId))){
			grandProdTotal[productId]=qty;
		}else{
		tempQty=grandProdTotal.get(productId);
		tempQty=tempQty+qty;
		grandProdTotal[productId]=tempQty;
		}
		
		prodMap =[:];
		prodMap["qty"]=qty;
		routeTotQty=routeTotQty+qty;
		if(crateProductsIdsList.contains(productId)){
			if(piecesPerCrate && piecesPerCrate.get(productId)){
				int crateDivisior=(piecesPerCrate.get(productId)).intValue();
			   tempCrates = (qty/(crateDivisior)).intValue();
			   tempExcess=((qty.intValue())%(crateDivisior.intValue()));
			   crateMap=[:];
			   prodMap["crates"]=tempCrates;
			   prodMap["loosePkts"]=tempExcess;
			   cratesMap =[:];
			   cratesMap["crates"]=tempCrates;
			   cratesMap["loosePkts"]=tempExcess;
			   rtCrates = rtCrates+tempCrates;
			   rtExcessPkts = rtExcessPkts+tempExcess;
			   prodMap["crateMap"]=cratesMap;
			   if(tempExcess>0){
			   rtLooseCrates=rtLooseCrates+1;
			   }
		   }
			
		}
		if(canProductsIdsList.contains(productId)){
			if(piecesPerCan && piecesPerCan.get(productId)){
				int canDivisior=(piecesPerCan.get(productId)).intValue();
			   tempCan = (qty/(canDivisior)).intValue();
			   tempCanExcess=((qty.intValue())%(canDivisior.intValue()));
			   cansMap=[:];
			   cansMap["cans"]=tempCan;
			   rtCans = rtCans+tempCan;
			   prodMap["cansMap"]=cansMap;
		   }
			
		}
		productFinalMap[productId]=prodMap;
	}
	allProdTotal=[:];
	allProdTotal["routeTotQty"]=routeTotQty;
	allProdTotal["rtCrates"]=rtCrates;
	allProdTotal["rtExcessPkts"]=rtExcessPkts;
	allProdTotal["rtLooseCrates"]=rtLooseCrates;
	allProdTotal["rtCans"]=rtCans;
	
	productFinalMap["Total"]=allProdTotal;
	
	tempFinalMap=[:];
	tempFinalMap.putAll(productFinalMap);
	routeWiseIndentMap.put(routeId, tempFinalMap);
	routeMap.put(routeId, tempRouteMap);
}
//all routes GrandTotal
GrandTotalProdMap=[:];
grandProdTotal.each{prodEntry->
	productId=prodEntry.getKey();
	qty=prodEntry.getValue();
	prodMap =[:];
	prodMap["qty"]=qty;
	routeTotQty=routeTotQty+qty;
	if(crateProductsIdsList.contains(productId)){
		if(piecesPerCrate && piecesPerCrate.get(productId)){
			int crateDivisior=(piecesPerCrate.get(productId)).intValue();
		   tempCrates = (qty/(crateDivisior)).intValue();
		   tempExcess=((qty.intValue())%(crateDivisior.intValue()));
		   prodMap["crates"]=tempCrates;
		   prodMap["loosePkts"]=tempExcess;
		  }
		
	}
	if(canProductsIdsList.contains(productId)){
		if(piecesPerCan && piecesPerCan.get(productId)){
			int canDivisior=(piecesPerCan.get(productId)).intValue();
		   tempCan = (qty/(canDivisior)).intValue();
		   tempCanExcess=((qty.intValue())%(canDivisior.intValue()));
		   prodMap["cans"]=tempCan;
	   }
		
	}
	GrandTotalProdMap[productId]=prodMap;
}
context.routeMap = routeMap;
context.routeWiseIndentMap = routeWiseIndentMap;
context.indentDate = UtilDateTime.toDateString(effectiveDate, "dd.MM.yyyy");

context.GrandTotalProdMap=GrandTotalProdMap;

return "success";
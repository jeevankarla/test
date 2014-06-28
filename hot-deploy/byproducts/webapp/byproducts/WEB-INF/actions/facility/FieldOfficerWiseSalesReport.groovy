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
import java.math.RoundingMode;
import java.util.SortedMap;
import org.ofbiz.party.party.PartyHelper;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
rounding = RoundingMode.HALF_UP;
try {
	if (parameters.fromDate) {
		context.froDate = parameters.fromDate;
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	else {
		froDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		context.froDate = froDate
		fromDate = froDate;
	}
	if (parameters.thruDate) {
		context.toDate = parameters.thruDate;
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}else {
		context.toDate = UtilDateTime.nowDate();
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
productNames = [:];
allProductsList = ByProductNetworkServices.getAllProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("salesDate",fromDate));

allProductsList.each{ eachProd ->
	productNames.put(eachProd.productId, eachProd.brandName);
}
context.productNames = productNames;
dctx = dispatcher.getDispatchContext();
conditionList = [];
shipmentIds = [];

amShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,fromDate,thruDate,"AM");
shipmentIds.addAll(amShipmentIds);
pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,fromDate,thruDate,"PM");
shipmentIds.addAll(pmShipmentIds);

if(UtilValidate.isNotEmpty(shipmentIds)){
facilityFieldStaff = (ByProductNetworkServices.getFacilityFieldStaff(dctx, context));
facilityFieldStaffMap = facilityFieldStaff.getAt("facilityFieldStaffMap");
fieldStaffAndFacility = facilityFieldStaff.getAt("fieldStaffAndFacility");

conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
conditionList.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS, "RETURN_ACCEPTED"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderItemList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", condition, null, null, null, false);

SortedMap fieldStaffMap = new TreeMap();
SortedMap facilityMap = new TreeMap();
fieldStaffNamesMap =[:];
fieldStaffIdList=[];
retailsList=[];
facilityNamesMap=[:];
finalMap=[:];
Iterator mapIter = fieldStaffAndFacility.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	fieldStaffId =entry.getKey();
	fieldStaffIdList.add(fieldStaffId);
	label = PartyHelper.getPartyName(delegator, fieldStaffId, false);
	fieldStaffNamesMap.put(fieldStaffId, label)
}
fieldStaffIdList.each { fieldStaffId ->
	retailsList = fieldStaffAndFacility.get(fieldStaffId.trim());
	quantityMap=[:];
	retailsList.each{eachFacilityId->
		returnMilkSaleTotal = 0;
		returnCurdSaleTotal = 0;
		returnOtherSaleTotal = 0;
		returnItemsList = EntityUtil.filterByAnd(orderItemList,UtilMisc.toMap("originFacilityId", eachFacilityId ));
		dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:UtilMisc.toList(eachFacilityId),shipmentIds:shipmentIds, fromDate:fromDate, thruDate:thruDate]);
		if(UtilValidate.isNotEmpty(returnItemsList)){
			returnItemsList.each{ returnItem->
				    returnQtyIncluded =0;
				    returnQuantity=0;
					retunProduct=0;
					returnQuantity = returnItem.returnQuantity;
					retunProduct = delegator.findOne("Product", ["productId" : returnItem.productId], true);
					returnQtyIncluded = retunProduct.quantityIncluded;
					if("Milk".equals(retunProduct.primaryProductCategoryId)){
						returnMilkSaleTotal=returnMilkSaleTotal+(returnQuantity*returnQtyIncluded);
					}
					if("Curd".equals(retunProduct.primaryProductCategoryId)){
						returnCurdSaleTotal=returnCurdSaleTotal+(returnQuantity*returnQtyIncluded);
				    }
					if(!("Milk".equals(retunProduct.primaryProductCategoryId))&&!("Curd".equals(retunProduct.primaryProductCategoryId))){
						returnOtherSaleTotal=returnCurdSaleTotal+(returnQuantity*returnQtyIncluded);
					}
			}
			
		}
		if(UtilValidate.isNotEmpty(dayTotals)){
			prodTotals = dayTotals.get("productTotals");
			milkSalesMap = [:];
			if(UtilValidate.isNotEmpty(prodTotals)){
				Set prodKeys = prodTotals.keySet();
				productList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, prodKeys) , null, null, null, false);
				productList = UtilMisc.sortMaps(productList, UtilMisc.toList("brandName"));
				productValueMap = [:];
				productList.each{ product->
					if(UtilValidate.isNotEmpty(prodTotals.get(product.productId))){
						prodMap = prodTotals.get(product.productId);
						productValueMap.put(product.productId,prodMap);
					}
				}
				BigDecimal milkSaleTotal = 0;
				BigDecimal curdSaleTotal = 0;
				BigDecimal otherSaleTotal = 0;
				total=0; 
				productCategoryMap = [:];
				finalSalesList=[];
				productValueMap.each{ productValue ->
					if(UtilValidate.isNotEmpty(productValue)){
						currentProduct = productValue.getKey();
						
						curdSalesMap=[:];
						product = delegator.findOne("Product", [productId : currentProduct], false);
						if("Milk".equals(product.primaryProductCategoryId)){
							milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
						}
						if("Curd".equals(product.primaryProductCategoryId)){
							curdSaleTotal=curdSaleTotal+productValue.getValue().get("total");
						}
						if(!("Milk".equals(product.primaryProductCategoryId))&&!("Curd".equals(product.primaryProductCategoryId))){
							otherSaleTotal=otherSaleTotal+productValue.getValue().get("total");
						}
				  }
			   }
				milkSaleTotal=(milkSaleTotal-returnMilkSaleTotal).setScale(0, rounding);
			    curdSaleTotal=(curdSaleTotal-returnCurdSaleTotal).setScale(0, rounding)
				otherSaleTotal=(otherSaleTotal-returnOtherSaleTotal).setScale(0, rounding)
				
				if(milkSaleTotal!=0){
				   milkSalesMap.putAt("Milk",milkSaleTotal );
			    }
				if(curdSaleTotal!=0){
				   milkSalesMap.putAt("Curd", curdSaleTotal);
				}
				if(otherSaleTotal!=0){
					milkSalesMap.putAt("OtherProducts", otherSaleTotal);
				 }
				if(curdSaleTotal!=0 || milkSaleTotal!=0 || otherSaleTotal!=0){
					total=milkSaleTotal+curdSaleTotal+otherSaleTotal;
					milkSalesMap.putAt("Total", total);
				 }
			}
			if(UtilValidate.isNotEmpty(milkSalesMap)){
			   quantityMap.put(eachFacilityId, milkSalesMap);
			}
		}
		 facilityDetails = delegator.findOne("Facility", [facilityId : eachFacilityId], false);
		 if(UtilValidate.isNotEmpty(facilityDetails)){
			facilityName = facilityDetails.facilityName;
			facilityNamesMap.put(eachFacilityId, facilityName)
			context.facilityNamesMap = facilityNamesMap;
		 }
		 if(UtilValidate.isNotEmpty(quantityMap)){
			fieldStaffMap.put(fieldStaffId,quantityMap);
		 }
	}
}
facilityList = [];
context.fieldStaffMap = fieldStaffMap;
context.fieldStaffNamesMap = fieldStaffNamesMap;
}


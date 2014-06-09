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
import org.ofbiz.party.party.PartyHelper;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
totalQuantity = 0;
totalRevenue = 0;
dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
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
conditionList.clear();
shipmentIds = ByProductNetworkServices.getShipmentIds(delegator, fromDate, thruDate);
JSONArray fieldStaffDataListJSON = new JSONArray();
JSONArray labelsJSON = new JSONArray();
if(UtilValidate.isNotEmpty(shipmentIds)){
facilityFieldStaff = (ByProductNetworkServices.getFacilityFieldStaff(dctx, context));
facilityFieldStaffMap = facilityFieldStaff.getAt("facilityFieldStaffMap");
fieldStaffAndFacility = facilityFieldStaff.getAt("fieldStaffAndFacility");
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderItemList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, null, null, false);
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
	JSONArray labelsList= new JSONArray();
	fieldStaffIdList.add(fieldStaffId);
	label = PartyHelper.getPartyName(delegator, fieldStaffId, false);
	fieldStaffNamesMap.put(fieldStaffId, label)
	labelsList.add(label);
	labelsJSON.add(labelsList);
}
fieldStaffIdList.each { fieldStaffId ->
	retailsList = fieldStaffAndFacility.get(fieldStaffId.trim().toUpperCase());
	quantityMap=[:];
	retailsList.each{eachFacilityId->
		dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:UtilMisc.toList(eachFacilityId),shipmentIds:shipmentIds, fromDate:fromDate, thruDate:thruDate]);
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
				milkSaleTotal = 0;
				curdSaleTotal = 0;
				Total=0; 
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
				  }
			   }
				if(milkSaleTotal!=0){
				   milkSalesMap.putAt("Milk", milkSaleTotal);
			   }
				if(curdSaleTotal!=0){
				   milkSalesMap.putAt("Curd", curdSaleTotal);
				}
				if(curdSaleTotal!=0 || milkSaleTotal!=0 ){
					Total=milkSaleTotal+curdSaleTotal;
					milkSalesMap.putAt("Total", Total);
				 }
				
			}
			if(UtilValidate.isNotEmpty(milkSalesMap)){
			   quantityMap.put(eachFacilityId, milkSalesMap);
			}
			facilityDetails = delegator.findOne("Facility", [facilityId : eachFacilityId], false);
			if(UtilValidate.isNotEmpty(facilityDetails)){
				facilityName = facilityDetails.facilityName;
				facilityNamesMap.put(eachFacilityId, facilityName)
				context.facilityNamesMap = facilityNamesMap;
			}
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


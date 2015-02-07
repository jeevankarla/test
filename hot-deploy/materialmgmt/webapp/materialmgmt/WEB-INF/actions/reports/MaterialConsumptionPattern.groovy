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
import java.math.RoundingMode;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.product.inventory.InventoryWorker;
import in.vasista.vbiz.purchase.MaterialHelperServices;
 rounding = RoundingMode.HALF_UP;
 
 fromDate = parameters.fromDate;
 thruDate = parameters.thruDate;
 totalQuantity = 0;
 totalRevenue = 0;
 dctx = dispatcher.getDispatchContext();
 
 
 def sdf = new SimpleDateFormat("MMMM dd, yyyy");
 try {
	 if (UtilValidate.isNotEmpty(parameters.fromDate)) {
		 context.froDate = parameters.fromDate;
		 fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
		 context.froDate = fromDate;
		 froDate = UtilDateTime.addDaysToTimestamp(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()),-100);
	 }else {
	    //froDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		 froDate = UtilDateTime.addDaysToTimestamp(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()),-100);
		 context.froDate = froDate
		 fromDate = froDate;
	 }
	 
	 if (UtilValidate.isNotEmpty(parameters.thruDate)) {
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
 
 
 
 
 
 filterProductSale = [];
 
 
 dctx = dispatcher.getDispatchContext();
 conditionList = [];
 
 
 if(UtilValidate.isNotEmpty(parameters.productId)){
	 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
	 context.productId = parameters.productId;
 }
 productId = parameters.productId;
 orgPartyId = parameters.orgPartyId;
 context.productId = productId;
 int i=1;
 if (productId) {
	 JSONArray productDataListJSON = new JSONArray();
	 JSONArray labelsJSON = new JSONArray();
	
	 
	 GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId",productId),false);
	 context.product = product;
	 Map issueCtxMap = FastMap.newInstance();
		issueCtxMap.put("productId", productId);
		issueCtxMap.put("fromDate", fromDate);
		issueCtxMap.put("thruDate", thruDate);
		issueCtxMap.put("orgPartyId", orgPartyId);
		//Debug.log("issueCtxMap============"+issueCtxMap);
		 // Find oustanding purchase orders for this item.
		issues = MaterialHelperServices.getCustRequestIssuancesForPeriod(dctx, issueCtxMap);
		Debug.log("issues.productTotals============"+issues);
		if(UtilValidate.isNotEmpty(issues) && UtilValidate.isNotEmpty(issues.productTotals) &&
			 UtilValidate.isNotEmpty((issues.productTotals).get(productId)) && UtilValidate.isNotEmpty((issues.productTotals).get(productId).dayWiseMap)){
			
			 dayWiseMap=(issues.productTotals).get(productId).dayWiseMap;
			 i=0;
			 if(dayWiseMap){
				 for(Map.Entry entry : dayWiseMap.entrySet()){
					 tempMap = [:];
					 date = entry.getKey();
					
					 /*tempMap.putAt("name", date);
					 tempMap.putAt("count", entry.getValue().quantity);
					 requirmentByStatusList.add(tempMap);*/
					 jsonArray= new JSONArray();
					 jsonArray.add(i++);
					 jsonArray.add(entry.getValue().quantity);
					 productDataListJSON.add(jsonArray);
					 labelArray= new JSONArray();
					 labelArray.add(jsonArray.get(0));
					 labelArray.add(date);
					 labelsJSON.add(labelArray);
				 }
			 }
			 
		}
		
		
				
	 
	context.productDataListJSON = productDataListJSON;
	context.labelsJSON = labelsJSON;
	Debug.log("labelsJSON============"+labelsJSON);
}
 
 Debug.logError("productDataListJSON: " + context.productDataListJSON, "");
 
 
/* String productId = (String) context.get("productId");
 String facilityId = (String) context.get("facilityId");
 Timestamp fromDate = (Timestamp) context.get("fromDate");
 Timestamp thruDate = (Timestamp) context.get("thruDate");*/
 
 //resultMap = MaterialHelperServices.getCustRequestIssuancesForPeriod(dctx,UtilMisc.toMap("productId","RM1210","facilityId","STORE","fromDate",fromDate,"thruDate",thruDate));
 
 
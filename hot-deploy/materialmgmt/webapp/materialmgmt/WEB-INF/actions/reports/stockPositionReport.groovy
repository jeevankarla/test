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
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.product.inventory.InventoryWorker;
import in.vasista.vbiz.purchase.MaterialHelperServices;

rounding = RoundingMode.HALF_UP;
  
fromDate = parameters.fromDate;
totalQuantity = 0;
totalRevenue = 0;
dctx = dispatcher.getDispatchContext();
 
 def sdf = new SimpleDateFormat("MMMM dd, yyyy");
 try {
	 if (UtilValidate.isNotEmpty(fromDate)) {
		 fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
	 }
 } catch (ParseException e) {
	 Debug.logError(e, "Cannot parse date string: " + e, "");
 context.errorMessage = "Cannot parse date string: " + e;
	 return;
 }
 context.fromDate = fromDate;
 stockPositionMap =[:];
 ledgerFolioList = delegator.findList("ProductAttribute", EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "LEDGERFOLIONO"), null, null, null, false);
 ledgerFolioList = UtilMisc.sortMaps(ledgerFolioList, UtilMisc.toList("attrValue"));
 if(UtilValidate.isNotEmpty(ledgerFolioList)){
	 ledgerFolioNumList = EntityUtil.getFieldListFromEntityList(ledgerFolioList,"attrValue",true);
	 if(UtilValidate.isNotEmpty(ledgerFolioNumList)){
		 productMap = [:];
		 ledgerFolioNumList.each { ledgerFolioNum ->
			 productAttributeList = delegator.findByAnd("ProductAttribute", [attrValue : ledgerFolioNum ,attrName : "LEDGERFOLIONO"]);
			 if(UtilValidate.isNotEmpty(productAttributeList)){
				 List productIdList = EntityUtil.getFieldListFromEntityList(productAttributeList, "productId", true);
				 if(UtilValidate.isNotEmpty(productIdList)){
					 List conditionList = [];
					 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdList));
					 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, UtilMisc.toList("STORE","ICP_STORE")));
					 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					 productFacilityList = delegator.findList("ProductFacility", condition, null, null, null, false);
					 productValueMap = [:];
					 if(UtilValidate.isNotEmpty(productFacilityList)){
						 productFacilityList.each{ productFacility ->
							 productId = productFacility.get("productId");
							 productDetails = delegator.findOne("Product",[productId : productId], false);
							 internalName = "";
							 if(UtilValidate.isNotEmpty(productDetails)){
								 internalName = productDetails.internalName;
							 }
							 productName = "";
							 uomDescription = "";
							 quantityOnHandTotal = BigDecimal.ZERO;
							 availableToPromiseTotal = BigDecimal.ZERO;
							 if(UtilValidate.isNotEmpty(productDetails)){
								 productName = productDetails.productName;
								 quantityUomId = productDetails.quantityUomId;
								 if(UtilValidate.isNotEmpty(quantityUomId)){
									 uomDetails = delegator.findOne("Uom",[uomId : quantityUomId], false);
									 if(UtilValidate.isNotEmpty(uomDetails)){
										 uomDescription = uomDetails.description;
									 }
								 }
							 }
							 inventoryItemList = delegator.findList("InventoryItem", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
							 if(UtilValidate.isNotEmpty(inventoryItemList)){
								 inventoryItem = EntityUtil.getFirst(inventoryItemList);
								 if(UtilValidate.isNotEmpty(inventoryItem)){
									 quantityOnHandTotal = inventoryItem.quantityOnHandTotal;
									 availableToPromiseTotal = inventoryItem.availableToPromiseTotal;
								 }
							 }
							 tempMap = [:];
							 tempMap["productName"] = productName;
							 tempMap["internalName"] = internalName;
							 tempMap["uomDescription"] = uomDescription;
							 tempMap["availableToPromiseTotal"] = availableToPromiseTotal;
							 tempMap["quantityOnHandTotal"] = quantityOnHandTotal;
							 if(availableToPromiseTotal>0 || quantityOnHandTotal>0){
								 if(UtilValidate.isNotEmpty(tempMap)){
									 productValueMap.put(productId,tempMap);
								 }
							 }
						 }
					 }
				 }
			 }
			 if(UtilValidate.isNotEmpty(productValueMap)){
				 stockPositionMap.put(ledgerFolioNum,productValueMap);
			 }
		 }
	 }
 }
context.put("stockPositionMap",stockPositionMap);
			 
 
 
 
 
 
 
 
 
 
 
 
 
 
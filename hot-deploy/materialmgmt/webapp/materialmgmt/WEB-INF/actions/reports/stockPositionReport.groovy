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
import javolution.util.FastMap;
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
 
 Map finalStockPositionMap = FastMap.newInstance();
 List conditionList = [];
 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, UtilMisc.toList("STORE","ICP_STORE")));
 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 productFacilityList = delegator.findList("ProductFacility", condition, null, null, null, false);
 if(UtilValidate.isNotEmpty(productFacilityList)){
	 List productIdList = EntityUtil.getFieldListFromEntityList(productFacilityList, "productId", true);
	 if(UtilValidate.isNotEmpty(productIdList)){
		 //productAttrList = delegator.findList("ProductAttribute", EntityCondition.makeCondition("productId", EntityOperator.IN, productIdList), null, null, null, false);
		 //if(UtilValidate.isNotEmpty(productAttrList)){
			 productIdList.each{ productId ->
				 productId = productId;
				 productAttr = delegator.findOne("ProductAttribute", [productId : productId , attrName : "LEDGERFOLIONO"], false);
				 String attrName = null;
				 String attrValue = null;
				 if(UtilValidate.isNotEmpty(productAttr)){
					 attrName = productAttr.get("attrName");
					 attrValue = productAttr.get("attrValue");
				 }
				 List tempList = FastList.newInstance();
				 Map productValueMap = FastMap.newInstance();
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
				 tempMap["productId"] = productId;
				 tempMap["productName"] = productName;
				 tempMap["internalName"] = internalName;
				 tempMap["uomDescription"] = uomDescription;
				 tempMap["availableToPromiseTotal"] = availableToPromiseTotal;
				 tempMap["quantityOnHandTotal"] = quantityOnHandTotal;
				 if(availableToPromiseTotal>0 || quantityOnHandTotal>0){
					 if(UtilValidate.isNotEmpty(tempMap)){
						 productValueMap.putAll(tempMap);
					 }
				 }
				 if(UtilValidate.isNotEmpty(productValueMap)){
					 tempList.add(productValueMap);
					 if(UtilValidate.isNotEmpty(attrName)){
						 if(attrName.equalsIgnoreCase("LEDGERFOLIONO")){
							 if(UtilValidate.isEmpty(finalStockPositionMap.get(attrValue))){
								 finalStockPositionMap.put(attrValue,tempList);
							 }else{
								 List existingList = FastList.newInstance();
								 existingList = finalStockPositionMap.get(attrValue);
								 existingList.add(productValueMap);
								 finalStockPositionMap.put(attrValue,existingList);
							 }
						 }
					 }else{
					     if(UtilValidate.isEmpty(finalStockPositionMap.get("Others"))){
							 finalStockPositionMap.put("Others",tempList);
						 }else{
							 List existingList = FastList.newInstance();
							 existingList = finalStockPositionMap.get("Others");
							 existingList.add(productValueMap);
							 finalStockPositionMap.put("Others",existingList);
						 }
					 }
				 }
			 }
	 }
 }
 /*Map sortedMap = FastMap.newInstance();
 List sortedListofMaps = FastList.newInstance();
 if(UtilValidate.isNotEmpty(finalStockPositionMap)){
	 	for(String key in finalStockPositionMap.keySet()){
			 if(!key.equalsIgnoreCase("Others")){
				 Map tempMap = FastMap.newInstance();
				 tempMap.put("attrValInt",Integer.parseInt(key));
				 sortedListofMaps.add(tempMap);
			 }
		 }
		 sortedListofMaps = UtilMisc.sortMaps(sortedListofMaps, UtilMisc.toList("attrValInt"));
		 Map tempMap = FastMap.newInstance();
		 tempMap.put("attrValInt","Others");
		 sortedListofMaps.add(tempMap);
 }
 for(sortMap in sortedListofMaps){
	 String key = (sortMap.get("attrValInt")).toString();
	 if(UtilValidate.isNotEmpty(finalStockPositionMap) && UtilValidate.isNotEmpty(finalStockPositionMap.get(key))){
		 sortedMap.put(key,finalStockPositionMap.get(key));
	 }
 }*/
 context.put("finalStockPositionMap",finalStockPositionMap);
 
 
 /*ledgerFolioList = delegator.findList("ProductAttribute", EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "LEDGERFOLIONO"), null, null, null, false);
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
context.put("stockPositionMap",stockPositionMap);*/
			 
 
 
 
 
 
 
 
 
 
 
 
 
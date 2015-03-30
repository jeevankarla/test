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
import org.ofbiz.product.inventory.InventoryServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;

rounding = RoundingMode.HALF_UP;
  
fromDate = parameters.fromDate;
ledgerFolioNo=parameters.ledgerFolioNo;
facilityId=parameters.issueToFacilityId;
context.facilityId=facilityId;
flag=parameters.reportTypeFlag;
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
 dayEnd = UtilDateTime.getDayEnd(fromDate);
 
 Map finalStockPositionMap = FastMap.newInstance();
 Map otherStockPositionMap = FastMap.newInstance();
 
 Map finalStockPositionValMap = FastMap.newInstance();
 Map otherStockPositionValMap = FastMap.newInstance();
 if(UtilValidate.isNotEmpty(ledgerFolioNo)){	 
 ProductAttributeId = delegator.findList("ProductAttribute",EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS , ledgerFolioNo)  , UtilMisc.toSet("productId"), null, null, false );
 if(UtilValidate.isNotEmpty(ProductAttributeId)){
 List prodIds= EntityUtil.getFieldListFromEntityList(ProductAttributeId, "productId", true);  
 if(UtilValidate.isNotEmpty(prodIds)){	 

 List conditionList = [];
 if(UtilValidate.isNotEmpty(facilityId)){
	 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId));
 }
 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN,prodIds));
 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 productFacilityList = delegator.findList("ProductFacility", condition, null, null, null, false);
    }
   }
 }else{ 
 List conditionList = [];
 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId));
 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 productFacilityList = delegator.findList("ProductFacility", condition, null, null, null, false);
   }   
 if(UtilValidate.isNotEmpty(productFacilityList)){
	 List productIdList = EntityUtil.getFieldListFromEntityList(productFacilityList, "productId", true);
	 condition=EntityCondition.makeCondition([EntityCondition.makeCondition("productId",EntityOperator.IN,productIdList),EntityCondition.makeCondition("productTypeId",EntityOperator.EQUALS,"RAW_MATERIAL")],EntityOperator.AND);
	 productsList=delegator.findList("Product",condition,null,null,null,false);
	List products=EntityUtil.getFieldListFromEntityList(productsList,"productId", true);
	 if(UtilValidate.isNotEmpty(products)){
		 products.each{ productId ->
			 productId = productId;
			 productAttr = delegator.findOne("ProductAttribute", [productId : productId , attrName : "LEDGERFOLIONO"], false);
			 String attrName = null;
			 String attrValue = null;
			 if(UtilValidate.isNotEmpty(productAttr)){
				 attrName = productAttr.get("attrName");
				 attrValue = productAttr.get("attrValue");
			 }
			 List tempList = FastList.newInstance();
			 List tempValList = FastList.newInstance();
			 Map productValueMap = FastMap.newInstance();
			 Map productValMap = FastMap.newInstance();
			 productDetails = delegator.findOne("Product",[productId : productId], false);
			 internalName = "";
			 if(UtilValidate.isNotEmpty(productDetails)){
				 internalName = productDetails.internalName;
			 }
			 productName = "";
			 uomDescription = "";
			 quantityOnHandTotal = BigDecimal.ZERO;
			 availableToPromiseTotal = BigDecimal.ZERO;
			 qcQuantity = BigDecimal.ZERO;
			 receivedQty = BigDecimal.ZERO;
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
			 /*inventoryItemList = delegator.findList("InventoryItem", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
			 if(UtilValidate.isNotEmpty(inventoryItemList)){
				 inventoryItem = EntityUtil.getFirst(inventoryItemList);
				 if(UtilValidate.isNotEmpty(inventoryItem)){
					 quantityOnHandTotal = inventoryItem.quantityOnHandTotal;
					 availableToPromiseTotal = inventoryItem.availableToPromiseTotal;
				 }
			 }*/
			 inventoryItem = InventoryServices.getProductInventoryOpeningBalance(dctx, [effectiveDate:dayEnd,productId:productId,ownerPartyId:"Company"]);
			 if(UtilValidate.isNotEmpty(inventoryItem)){
				 quantityOnHandTotal = inventoryItem.inventoryCount;
				 availableToPromiseTotal = inventoryItem.inventoryCount;
			 }
			 ecl = EntityCondition.makeCondition([
								   EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId),
								   EntityCondition.makeCondition("statusId",EntityOperator.IN,UtilMisc.toList("SR_RECEIVED","SR_QUALITYCHECK"))],
								   EntityOperator.AND);
			 shipmentReceipts=delegator.findList("ShipmentReceipt",ecl,UtilMisc.toSet("statusId","quantityAccepted","quantityRejected"),null,null,false);
			 if(UtilValidate.isNotEmpty(shipmentReceipts)){
				 shipmentReceipts.each{receipt->
					 if((UtilValidate.isNotEmpty(receipt.statusId)) && (receipt.statusId == "SR_RECEIVED") && UtilValidate.isNotEmpty(receipt.quantityAccepted)){
						 receivedQty+=receipt.quantityAccepted;
					 }
					 if((UtilValidate.isNotEmpty(receipt.statusId)) && (receipt.statusId == "SR_QUALITYCHECK") && UtilValidate.isNotEmpty(receipt.quantityAccepted)){
						 qcQuantity+=receipt.quantityAccepted;
					 }
				 }
			 }
			 /*inventoryItemWithQC = InventoryServices.getProductInventoryOpeningBalance(dctx, [effectiveDate:dayEnd,productId:productId]);
			 if(UtilValidate.isNotEmpty(inventoryItemWithQC)){
				 qcQuantity = inventoryItemWithQC.inventoryCount;
			 }*/
			 /*qcQuantityDiff = BigDecimal.ZERO;
			 if(UtilValidate.isNotEmpty(qcQuantity)){
				 qcQuantityDiff = qcQuantity-quantityOnHandTotal;
			 }*/
			 tempMap = [:];
			 tempMap["productId"] = productId;
			 tempMap["productName"] = productName;
			 tempMap["internalName"] = internalName;
			 tempMap["uomDescription"] = uomDescription;
			 tempMap["availableToPromiseTotal"] = availableToPromiseTotal;
			 tempMap["quantityOnHandTotal"] = quantityOnHandTotal;
			 tempMap["qcQuantity"] = qcQuantity;
			 tempMap["receivedQty"]=receivedQty;
			 if(quantityOnHandTotal>0 || qcQuantity>0 || receivedQty>0){
				 if(UtilValidate.isNotEmpty(tempMap)){
					 productValueMap.putAll(tempMap);
				 }
			 }else{
			 	productValMap.putAll(tempMap);
			 }
			 if(UtilValidate.isNotEmpty(productValueMap)){
				 tempList.add(productValueMap);
				 if(UtilValidate.isNotEmpty(attrName)){
					 if(attrName.equalsIgnoreCase("LEDGERFOLIONO")){
						 if(UtilValidate.isNotEmpty(attrValue)){
							 if(UtilValidate.isEmpty(finalStockPositionMap.get(Integer.parseInt(attrValue)))){
								 finalStockPositionMap.put(Integer.parseInt(attrValue),tempList);
							 }else{
								 List existingList = FastList.newInstance();
								 existingList = finalStockPositionMap.get(Integer.parseInt(attrValue));
								 existingList.add(productValueMap);
								 finalStockPositionMap.put(Integer.parseInt(attrValue),existingList);
							 }
						 }
					 }
				 }else{
				     if(UtilValidate.isEmpty(otherStockPositionMap.get("Others"))){
						 otherStockPositionMap.put("Others",tempList);
					 }else{
						 List existingList = FastList.newInstance();
						 existingList = otherStockPositionMap.get("Others");
						 existingList.add(productValueMap);
						 otherStockPositionMap.put("Others",existingList);
					 }
				 }
			 }
			 if(UtilValidate.isNotEmpty(productValMap)){
				 tempValList.add(productValMap);
				 if(UtilValidate.isNotEmpty(attrName)){
					 if(attrName.equalsIgnoreCase("LEDGERFOLIONO")){
						 if(UtilValidate.isNotEmpty(attrValue)){
							 if(UtilValidate.isEmpty(finalStockPositionValMap.get(Integer.parseInt(attrValue)))){
								 finalStockPositionValMap.put(Integer.parseInt(attrValue),tempValList);
							 }else{
								 List existingList = FastList.newInstance();
								 existingList = finalStockPositionValMap.get(Integer.parseInt(attrValue));
								 existingList.add(productValMap);
								 finalStockPositionValMap.put(Integer.parseInt(attrValue),existingList);
							 }
						 }
					 }
				 }else{
					 if(UtilValidate.isEmpty(otherStockPositionValMap.get("Others"))){
						 otherStockPositionValMap.put("Others",tempValList);
					 }else{
						 List existingList = FastList.newInstance();
						 existingList = otherStockPositionValMap.get("Others");
						 existingList.add(productValMap);
						 otherStockPositionValMap.put("Others",existingList);
					 }
				 }
			 }
		 }
	 }
	 
 }
 if(UtilValidate.isNotEmpty(flag) && flag=="WITHZEROS"){
	 finalStockPositionMap=finalStockPositionValMap;
	 otherStockPositionMap=otherStockPositionValMap;
 }
Map sortedMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(finalStockPositionMap)){
	for(String key in finalStockPositionMap.keySet()){
			List tempList = FastList.newInstance();
			tempList.addAll(finalStockPositionMap.get(key));
			tempList=UtilMisc.sortMaps(tempList, UtilMisc.toList("-internalName"));
			sortedMap.put(key,tempList);
	}
}
Map<String, List> newSortedMap = new TreeMap<String, List>(sortedMap);
context.put("finalStockPositionMap",newSortedMap);
context.otherStockPositionMap=otherStockPositionMap;


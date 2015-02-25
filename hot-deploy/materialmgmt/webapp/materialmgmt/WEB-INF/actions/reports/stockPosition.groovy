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
 context.productId = productId;
 int i=1;
 if (productId) {
	 JSONArray productDataListJSON = new JSONArray();
	 JSONArray labelsJSON = new JSONArray();
	/* for(int i=0;i<2;i++){
		 JSONArray dayList= new JSONArray();
		 dayList.add(i);
		 dayList.add(50+(i*10));
		 productDataListJSON.add(dayList);
		 labelsJSON.add(dayList);
	 }*/
	 
	 GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId",productId),false);
	 context.product = product;
	 
	 //finding UOM---------------
	 productQuantityUomId="";
	 productUOMdescription="";
	 if(UtilValidate.isNotEmpty(product.quantityUomId)){
		 productQuantityUomId= product.quantityUomId;
	  }
	 GenericValue productUOM = delegator.findOne("Uom", UtilMisc.toMap("uomId",productQuantityUomId),false);
	 if(UtilValidate.isNotEmpty(productUOM)){
		 if(UtilValidate.isNotEmpty(productUOM.description)){
			productUOMdescription= productUOM.description;
		 }
	 }
	 context.uom = productUOMdescription;
	 //----------------
	 boolean isMarketingPackage = EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.productTypeId, "parentTypeId", "MARKETING_PKG");
	 context.isMarketingPackage = (isMarketingPackage? "true": "false");
	 //If product is virtual gather summary data from variants
	 if (product.isVirtual && "Y".equals(product.isVirtual)) {
		 //Get the virtual product feature types
		 result = dispatcher.runSync("getProductFeaturesByType", [productId : productId, productFeatureApplTypeId : 'SELECTABLE_FEATURE']);
		 featureTypeIds = result.productFeatureTypes;
	 
		 //Get the variants
		 result = dispatcher.runSync("getAllProductVariants", [productId : productId]);
		 variants = result.assocProducts;
		 variantIterator = variants.iterator();
		 variantInventorySummaries = [];
		 while (variantIterator) {
			 variant = variantIterator.next();
	 
			 //create a map of each variant id and inventory summary (all facilities)
			 inventoryAvailable = dispatcher.runSync("getProductInventoryAvailable", [productId : variant.productIdTo]);
	 
			 variantInventorySummary = [productId : variant.productIdTo,
										availableToPromiseTotal : inventoryAvailable.availableToPromiseTotal,
										quantityOnHandTotal : inventoryAvailable.quantityOnHandTotal];
	 
			 //add the applicable features to the map
			 featureTypeIdsIterator = featureTypeIds.iterator();
			 while (featureTypeIdsIterator) {
				 featureTypeId = featureTypeIdsIterator.next();
				 result = dispatcher.runSync("getProductFeatures", [productId : variant.productIdTo, type : 'STANDARD_FEATURE', distinct : featureTypeId]);
				 variantFeatures = result.productFeatures;
				 if (variantFeatures) {
					 //there should only be one result in this collection
					 variantInventorySummary.put(featureTypeId, variantFeatures.get(0));
				 }
			 }
			 variantInventorySummaries.add(variantInventorySummary);
		 }
		 context.featureTypeIds = featureTypeIds;
		 context.variantInventorySummaries = variantInventorySummaries;
	 } else { //Gather information for a non virtual product
		 quantitySummaryByFacility = [:];
		 manufacturingInQuantitySummaryByFacility = [:];
		 manufacturingOutQuantitySummaryByFacility = [:];
		 // The warehouse list is selected
		 showAllFacilities = parameters.showAllFacilities;
		 if (showAllFacilities && "Y".equals(showAllFacilities)) {
			 facilityList = delegator.findList("Facility", null, null, null, null, false);
		 } else {
			 facilityList = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
		 }
		 facilityIterator = facilityList.iterator();
		 dispatcher = request.getAttribute("dispatcher");
		 Map contextInput = null;
		 Map resultOutput = null;
	     Map qcInventoryTotal = [:];
		 // inventory quantity summary by facility: For every warehouse the product's atp and qoh
		 // are obtained (calling the "getInventoryAvailableByFacility" service)
		 while (facilityIterator) {
			 facility = facilityIterator.next();
			 resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", [productId : productId, facilityId : facility.facilityId ,ownerPartyId :"Company"]);
			 totalInventory = dispatcher.runSync("getInventoryAvailableByFacility", [productId : productId, facilityId : facility.facilityId]);
			 quantitySummary = [:];
			 quantitySummary.facilityId = facility.facilityId;
			 quantitySummary.totalQuantityOnHand = resultOutput.quantityOnHandTotal;
			 quantitySummary.totalAvailableToPromise = resultOutput.availableToPromiseTotal;
			 quantitySummary.totalQuantityInQcHand = 0;// totalInventory.quantityOnHandTotal-resultOutput.quantityOnHandTotal;
			 quantitySummary.receivedQty = 0;
			 ecl = 	EntityCondition.makeCondition([
				 	EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId),
					EntityCondition.makeCondition("statusId", EntityOperator.IN,UtilMisc.toList("SR_RECEIVED","SR_QUALITYCHECK"))],
			 		EntityOperator.AND);
			 shipmentReceipts=delegator.findList("ShipmentReceipt",ecl,UtilMisc.toSet("productId","statusId","quantityAccepted"),null,null,false);
			 if(UtilValidate.isNotEmpty(shipmentReceipts)){
				 shipmentReceipts.each{ receipt->
					 if(receipt.statusId == "SR_RECEIVED"){
						 quantitySummary.receivedQty+=receipt.quantityAccepted;
					 }
					 if(receipt.statusId == "SR_QUALITYCHECK"){
						 quantitySummary.totalQuantityInQcHand+=receipt.quantityAccepted;
					 }
					 
				 }
			 }
			 JSONArray jsonArray= new JSONArray();
			 jsonArray.add(i++);
			 jsonArray.add(resultOutput.quantityOnHandTotal);
			 productDataListJSON.add(jsonArray);
			 JSONArray labelArray= new JSONArray();
			 labelArray.add(jsonArray.get(0));
			 labelArray.add("Inventory");
			 labelsJSON.add(labelArray);
			 // if the product is a MARKETING_PKG_AUTO/PICK, then also get the quantity which can be produced from components
			 if (isMarketingPackage) {
				 resultOutput = dispatcher.runSync("getMktgPackagesAvailable", [productId : productId, facilityId : facility.facilityId]);
				 quantitySummary.mktgPkgQOH = resultOutput.quantityOnHandTotal;
				 quantitySummary.mktgPkgATP = resultOutput.availableToPromiseTotal;
			 }
	         /*if(quantitySummary.totalQuantityOnHand && quantitySummary.totalAvailableToPromise && quantitySummary.totalQuantityOnHand !=0 && quantitySummary.totalAvailableToPromise !=0 ){
				quantitySummaryByFacility.put(facility.facilityId, quantitySummary);
			}*/
			 quantitySummaryByFacility.put(facility.facilityId, quantitySummary);
		 }
		 
		 productInventoryItems = delegator.findByAnd("InventoryItem",
				 [productId : productId],
				 ['facilityId', '-datetimeReceived', '-inventoryItemId']);
	 
		 // TODO: get all incoming shipments not yet arrived coming into each facility that this product is in, use a view entity with ShipmentAndItem
		 findIncomingShipmentsConds = [];
	 
		 findIncomingShipmentsConds.add(EntityCondition.makeCondition('productId', EntityOperator.EQUALS, productId));
	 
		 findIncomingShipmentsTypeConds = [];
		 findIncomingShipmentsTypeConds.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "INCOMING_SHIPMENT"));
		 findIncomingShipmentsTypeConds.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "PURCHASE_SHIPMENT"));
		 findIncomingShipmentsTypeConds.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "SALES_RETURN"));
		 findIncomingShipmentsConds.add(EntityCondition.makeCondition(findIncomingShipmentsTypeConds, EntityOperator.OR));
	 
		 findIncomingShipmentsStatusConds = [];
		 findIncomingShipmentsStatusConds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_DELIVERED"));
		 findIncomingShipmentsStatusConds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
		 findIncomingShipmentsStatusConds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PURCH_SHIP_RECEIVED"));
		 findIncomingShipmentsConds.add(EntityCondition.makeCondition(findIncomingShipmentsStatusConds, EntityOperator.AND));
	 
		 findIncomingShipmentsStatusCondition = EntityCondition.makeCondition(findIncomingShipmentsConds, EntityOperator.AND);
		 incomingShipmentAndItems = delegator.findList("ShipmentAndItem", findIncomingShipmentsStatusCondition, null, ['-estimatedArrivalDate'], null, false);
		 incomingShipmentAndItemIter = incomingShipmentAndItems.iterator();
		 receiptQty = 0;
		 while (incomingShipmentAndItemIter) {
			 
			 incomingShipmentAndItem = incomingShipmentAndItemIter.next();
			 facilityId = incomingShipmentAndItem.destinationFacilityId;
			 quantity =  incomingShipmentAndItem.quantity;
			 
			 quantitySummary = quantitySummaryByFacility.get(facilityId);
			 if (!quantitySummary) {
				 quantitySummary = [:];
				 quantitySummary.facilityId = facilityId;
				 quantitySummaryByFacility.facilityId = quantitySummary;
			 }
	 
			 incomingShipmentAndItemList = quantitySummary.incomingShipmentAndItemList;
			 if (!incomingShipmentAndItemList) {
				 incomingShipmentAndItemList = [];
				 quantitySummary.incomingShipmentAndItemList = incomingShipmentAndItemList;
			 }
	 
			 incomingShipmentAndItemList.add(incomingShipmentAndItem);
			 receiptQty = receiptQty+quantity;
		 }
		 
		 jsonArray= new JSONArray();
		 jsonArray.add(i++);
		 jsonArray.add(receiptQty);
		 productDataListJSON.add(jsonArray);
		 JSONArray labelArray= new JSONArray();
		 labelArray.add(jsonArray.get(0));
		 labelArray.add("Receipts");
		 labelsJSON.add(labelArray);
		 // --------------------
		 // Production Runs
		/* resultOutput = dispatcher.runSync("getProductManufacturingSummaryByFacility",
						[productId : productId, userLogin : userLogin]);
		 // incoming products
		 manufacturingInQuantitySummaryByFacility = resultOutput.summaryInByFacility;
		 // outgoing products (materials)
		 manufacturingOutQuantitySummaryByFacility = resultOutput.summaryOutByFacility;*/
	 
		 showEmpty = "true".equals(request.getParameter("showEmpty"));
	 
		 // Find oustanding purchase orders for this item.
		 purchaseOrders = InventoryWorker.getOutstandingPurchaseOrders(productId, delegator);
	 
		 context.productInventoryItems = productInventoryItems;
		 context.quantitySummaryByFacility = quantitySummaryByFacility;
		 context.qcInventoryTotal = qcInventoryTotal;
		/* context.manufacturingInQuantitySummaryByFacility = manufacturingInQuantitySummaryByFacility;
		 context.manufacturingOutQuantitySummaryByFacility = manufacturingOutQuantitySummaryByFacility;*/
		 context.showEmpty = showEmpty;
		 context.purchaseOrders = purchaseOrders;
		purchaseOrderQty = 0;
		for(GenericValue purchaseOrder : purchaseOrders){
			purchaseOrderQty = purchaseOrderQty+purchaseOrder.quantity;
		}
		jsonArray= new JSONArray();
		jsonArray.add(i++);
		jsonArray.add(purchaseOrderQty);
		productDataListJSON.add(jsonArray);
		labelArray= new JSONArray();
		labelArray.add(jsonArray.get(0));
		labelArray.add("PO");
		labelsJSON.add(labelArray);
	 }
	 
	 // get requirments here
	 condList =[];
	 condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	 condList.add(EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, "PRODUCT_REQUIREMENT"));
	 condList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	 condList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	 EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
	 requirmentsList = delegator.findList("Requirement", cond, null, ['-createdDate'], null, false);
	 context.requirmentsList = requirmentsList;
	 requirmentBystatusMap =[:];
	 for(GenericValue requirment : requirmentsList){
		 String statusId = requirment.getString("statusId");
		 quantity = requirment.quantity;
		 if(UtilValidate.isEmpty(requirmentBystatusMap.get(statusId))){
			 requirmentBystatusMap.put(statusId , quantity);
		 }else{
		 	requirmentBystatusMap.put(statusId , requirmentBystatusMap.get(statusId)+quantity);
		 }
	 }
	 requirmentByStatusList = [];
	 if(requirmentBystatusMap){
		 for(Map.Entry entry : requirmentBystatusMap.entrySet()){
			 tempMap = [:];
			 statusId = entry.getKey();
			
			 tempMap.putAt("name", statusId);
			 tempMap.putAt("count", entry.getValue());
			 requirmentByStatusList.add(tempMap);
		 }
	 }
	 
	 context.requirmentByStatusList = requirmentByStatusList;
	 // get requests here 
	 condList.clear();
	 condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	 condList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "PRODUCT_REQUIREMENT"));
	 condList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	 condList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	 cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
	 custRequestsList = delegator.findList("CustRequestAndItemAndAttribute", cond, null, ['-custRequestDate'], null, false);
	 context.custRequestsList = custRequestsList;
	 custRequestsByStatusMap =[:];
	 indentQty = 0;
	 for(GenericValue custRequest : custRequestsList){
		 String statusId = custRequest.getString("itemStatusId");
		 quantity = custRequest.quantity;
		 if(UtilValidate.isEmpty(custRequestsByStatusMap.get(statusId))){
			 custRequestsByStatusMap.put(statusId , quantity);
		 }else{
			 custRequestsByStatusMap.put(statusId , custRequestsByStatusMap.get(statusId)+quantity);
		 }
		 indentQty = indentQty+quantity;
	 }
	 JSONArray jsonArray= new JSONArray();
	 jsonArray.add(i++);
	 jsonArray.add(indentQty);
	 productDataListJSON.add(jsonArray);
	 JSONArray labelArray= new JSONArray();
	 labelArray.add(jsonArray.get(0));
	 labelArray.add("Indent");
	 labelsJSON.add(labelArray);
	 
	 custRequestsByStatusList = [];
	 if(custRequestsByStatusMap){
		 for(Map.Entry entry : custRequestsByStatusMap.entrySet()){
			 tempMap = [:];
			 statusId = entry.getKey();
			
			 tempMap.putAt("name", statusId);
			 tempMap.putAt("count", entry.getValue());
			 custRequestsByStatusList.add(tempMap);
		 }
	 }
	 
	context.custRequestsByStatusList = custRequestsByStatusList; 
	context.productDataListJSON = productDataListJSON;
	context.labelsJSON = labelsJSON;
}
 
 Debug.logError("productDataListJSON: " + context.productDataListJSON, "");
 
 
/* String productId = (String) context.get("productId");
 String facilityId = (String) context.get("facilityId");
 Timestamp fromDate = (Timestamp) context.get("fromDate");
 Timestamp thruDate = (Timestamp) context.get("thruDate");*/
 
 //resultMap = MaterialHelperServices.getCustRequestIssuancesForPeriod(dctx,UtilMisc.toMap("productId","RM1210","facilityId","STORE","fromDate",fromDate,"thruDate",thruDate));
 
 
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

import org.ofbiz.service.ServiceUtil 
import org.ofbiz.entity.condition.*
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.party.party.PartyHelper;

facilityId = parameters.facilityId;

//find shipmentIds by filter criteria
conditionList =[];
if(UtilValidate.isNotEmpty(parameters.noConditionFind) && parameters.noConditionFind=="Y"){
	conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "DEPOT_SHIPMENT"));
	if(UtilValidate.isNotEmpty(parameters.partyId)){
		conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,parameters.partyId));
	}
   if(UtilValidate.isNotEmpty(parameters.estimatedShipDate)){
	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.EQUALS, parameters.estimatedShipDate));
	}
    if(UtilValidate.isNotEmpty(parameters.shipmentId)){
		conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,parameters.shipmentId));
	}
	/*if(UtilValidate.isNotEmpty(parameters.primaryOrderId)){
		conditionList.add(EntityCondition.makeCondition("primaryOrderId",EntityOperator.EQUALS,parameters.primaryOrderId));
	}*/
	if(UtilValidate.isNotEmpty(parameters.statusId)){
		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,parameters.statusId));
	}
	shipmentCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	shipmentList = delegator.findList("Shipment", shipmentCondition, null, ['shipmentId'], null, false);

  shipmentIdsList= EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", true);

  // fields to search by
  productId = parameters.productId ? parameters.productId.trim() : null;
  internalName = parameters.internalName ? parameters.internalName.trim() : null;

	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"SR_ACCEPTED"));
	if (productId) {
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE, productId + "%"));
	}
	if(facilityId){
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	}
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN,shipmentIdsList));
	shipmentReceiptCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
  	shipmentReceiptList=delegator.findList("ShipmentReceiptAndItem", shipmentReceiptCondition, null, ['receiptId'], null, false);
	
	inventoryItemIdsList= EntityUtil.getFieldListFromEntityList(shipmentReceiptList, "inventoryItemId", true);
//inventoryItemFind
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("inventoryItemTypeId", EntityOperator.EQUALS, "NON_SERIAL_INV_ITEM"));
    conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.IN, inventoryItemIdsList));


    ecl = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    physicalInventory = delegator.findList("InventoryItem", ecl, null, ['productId'], null, false);

	//shipmentReceiptList
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("inventoryItemTypeId", EntityOperator.EQUALS, "NON_SERIAL_INV_ITEM"));
	conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.IN, inventoryItemIdsList));
	
	
    // also need the overal product QOH and ATP for each product
    atpMap = [:];
    qohMap = [:];

    // build a list of productIds
    productIds = [] as Set;
    physicalInventory.each { iter ->
		//get receiptId and shipmentId for each inventoryItem
		tempMap=[:];
		tempMap.putAll(iter);
        productIds.add(iter.productId);
    }

    // for each product, call the inventory counting service
    productIds.each { productId ->
		if(facilityId){
			result = dispatcher.runSync("getInventoryAvailableByFacility", [facilityId : facilityId, productId : productId]);
			if (!ServiceUtil.isError(result)) {
				atpMap.put(productId, result.availableToPromiseTotal);
				qohMap.put(productId, result.quantityOnHandTotal);
			}
		}
    }
	
	condList = [];
	condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(physicalInventory, "facilityId", true)));
	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
		
    productStoreList = delegator.findList("FacilityAndProductStoreFacility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	
    // associate the quantities to each row and store the combined data as our list
    physicalInventoryCombined = [];
    physicalInventory.each { iter ->
        row = iter.getAllFields();
		row.productATP = atpMap.get(row.productId);
		row.productQOH = qohMap.get(row.productId);
		inventoryShipmentList = EntityUtil.filterByCondition(shipmentReceiptList, EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, iter.inventoryItemId));
		
		inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", iter.inventoryItemId), false);
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS , iter.inventoryItemId));
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_EQUAL, null));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		inventoryItemDetails = delegator.findList("InventoryItemDetail",  cond,null, null, null, false );
		String uom ="";
		bundleWeight =0;
		bundleUnitPrice =0;
		if(UtilValidate.isNotEmpty(inventoryItemDetails)){
		   inventoryItemDetails = EntityUtil.getFirst(inventoryItemDetails);
		   uom =inventoryItemDetails.uom;
		   bundleWeight = inventoryItemDetails.bundleWeight;
		   bundleUnitPrice = inventoryItemDetails.bundleUnitPrice;
		}
		row.putAt("uom", uom);
		row.putAt("bundleWeight", bundleWeight);
		row.putAt("bundleUnitPrice", bundleUnitPrice);
		inventoryProdStore = EntityUtil.filterByCondition(productStoreList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, inventoryItem.facilityId));
		
		shipmentReceiptEach = EntityUtil.getFirst(inventoryShipmentList);
		if(shipmentReceiptEach) {
			row.putAt("shipmentId", shipmentReceiptEach.shipmentId);
			//Debug.log("shipmentId=============================="+shipmentReceiptEach.shipmentId+"==iter.inventoryItemId="+iter.inventoryItemId);
			shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentReceiptEach.shipmentId), false);
			facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", inventoryItem.facilityId), false);
			product = delegator.findOne("Product", UtilMisc.toMap("productId", row.productId), false);
			
			partyName=PartyHelper.getPartyName(delegator, shipment.partyIdFrom, false);
			row.putAt("shipmentTypeId", shipment.shipmentTypeId);
			row.putAt("fromPartyId", shipment.partyIdFrom);
			row.putAt("facilityId", inventoryItem.facilityId);
			if(UtilValidate.isNotEmpty(inventoryProdStore)){
				row.putAt("productStoreId", (inventoryProdStore.get(0)).get("productStoreId"));
			}
			
			row.putAt("facilityName", facility.facilityName);
			row.putAt("partyName", partyName);
			row.putAt("productName", product.productName);
			row.putAt("estimatedShipDate", shipment.estimatedShipDate);
		}else{
			row.putAt("shipmentId", "");
		}
        physicalInventoryCombined.add(row);
    }
    context.physicalInventory = physicalInventoryCombined;
}
if(UtilValidate.isNotEmpty(isInventorySales)){
	dctx = dispatcher.getDispatchContext();
	JSONObject partyNameObj = new JSONObject();
	inputMap = [:];
	inputMap.put("userLogin", userLogin);
	//get Parties to make SalesOrder
	JSONArray billToPartyIdsJSON = new JSONArray();
	inputMap.put("roleTypeId", "EMPANELLED_CUSTOMER");
	Map partyDetailsMap = ByProductNetworkServices.getPartyByRoleType(dctx, inputMap);
	billToPartyDetailsList = partyDetailsMap.get("partyDetails");
	billToPartyDetailsList.each{eachParty ->
		JSONObject newPartyObj = new JSONObject();
		partyName=PartyHelper.getPartyName(delegator, eachParty.partyId, false);
		newPartyObj.put("value",eachParty.partyId);
		newPartyObj.put("label",partyName+" ["+eachParty.partyId+"]");
		partyNameObj.put(eachParty.partyId,partyName);
		billToPartyIdsJSON.add(newPartyObj);
	}
	context.billToPartyIdsJSON = billToPartyIdsJSON;
}

facilityList = delegator.findList("Facility", null, null, null, null, false);
context.facilityList = facilityList;



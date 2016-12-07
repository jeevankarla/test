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

import java.math.RoundingMode;

import javolution.util.FastList;

import org.ofbiz.party.party.PartyHelper;

facilityId = parameters.facilityId;
supplierInvId=parameters.supplierInvId;
//find shipmentIds by filter criteria


dctx = dispatcher.getDispatchContext();
Map boothsPaymentsDetail = [:];

partyId = userLogin.get("partyId");
rounding = RoundingMode.HALF_UP;

//debug.log("partyId=============="+partyId);


resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));


////debug.log("resultCtx=============="+resultCtx);


Map formatMap = [:];
List formatList = [];
List productStoreList = resultCtx.get("productStoreList");
context.productStoreList = productStoreList;

for (eachList in productStoreList) {
	formatMap = [:];
	formatMap.put("productStoreName",eachList.get("storeName"));
	formatMap.put("payToPartyId",eachList.get("payToPartyId"));
	formatList.addAll(formatMap);
}

roList = dispatcher.runSync("getRegionalOffices",UtilMisc.toMap("userLogin",userLogin));
roPartyList = roList.get("partyList");

for(eachRO in roPartyList){
	formatMap = [:];
	formatMap.put("productStoreName",eachRO.get("groupName"));
	formatMap.put("payToPartyId",eachRO.get("partyId"));
	formatList.addAll(formatMap);
}
context.formatList = formatList;


branchList = EntityUtil.getFieldListFromEntityList(productStoreList, "payToPartyId", true);
if(UtilValidate.isNotEmpty(parameters.partyIdFrom)){
	branchList.clear();
	branchList.add(parameters.partyIdFrom)
}


conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchList));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["BILL_TO_CUSTOMER"]));
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);

purorderIds = EntityUtil.getFieldListFromEntityList(OrderRoleList, "orderId", true);


/*
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.IN, purorderIds));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["BILL_TO_CUSTOMER"]));
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);
*/


conditionList =[];
if(UtilValidate.isNotEmpty(parameters.noConditionFind) && parameters.noConditionFind=="Y"){
	conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "DEPOT_SHIPMENT"));
	
	if(purorderIds)
	conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.IN, purorderIds));
	
	if(UtilValidate.isNotEmpty(parameters.partyId)){
		conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,parameters.partyId));
	}
   if(UtilValidate.isNotEmpty(parameters.estimatedShipDate)){
	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.EQUALS, parameters.estimatedShipDate));
	}
    if(UtilValidate.isNotEmpty(parameters.shipmentId)){
		conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,parameters.shipmentId));
	}
	if(UtilValidate.isNotEmpty(parameters.referenceNo)){
		poReferNumDetails = delegator.findList("OrderAttribute",EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS , parameters.referenceNo)  , UtilMisc.toSet("orderId"), null, null, false );
		poReferNumDetails = EntityUtil.getFirst(poReferNumDetails);
		orderId = poReferNumDetails.orderId;
		conditionList.add(EntityCondition.makeCondition("primaryOrderId",EntityOperator.EQUALS,orderId));
	}
	/*if(UtilValidate.isNotEmpty(parameters.primaryOrderId)){
		conditionList.add(EntityCondition.makeCondition("primaryOrderId",EntityOperator.EQUALS,parameters.primaryOrderId));
	}*/
	if(UtilValidate.isNotEmpty(parameters.statusId)){
		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,parameters.statusId));
	}
	shipmentCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	shipmentList = delegator.findList("Shipment", shipmentCondition, null, ['shipmentId'], null, false);

  shipmentIds= EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", true);
  
  shipmentIdsList = [];
  for (eachShipmentId in shipmentIds) {
	  conditionList.clear();
	  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, eachShipmentId));
	  conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
	  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	  
	  cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	  shipmentListForPOInvoiceId = delegator.findList("Invoice", cond, null, null, null, false);
	  
	  if(shipmentListForPOInvoiceId)
	  shipmentIdsList.add(eachShipmentId);
	  
  }
  
  
  

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
	conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
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
	physicalInventoryCombined2 = [];
    physicalInventory.each { iter ->
        row = iter.getAllFields();
		unitCost=row.get("unitCost");
		row.putAt("unitCost", unitCost.setScale(2, rounding));
		row.productATP = atpMap.get(row.productId);
		row.productQOH = qohMap.get(row.productId);
		inventoryShipmentList = EntityUtil.filterByCondition(shipmentReceiptList, EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, iter.inventoryItemId));
		
		inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", iter.inventoryItemId), false);
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS , iter.inventoryItemId));
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_EQUAL, null));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		inventoryItemDetails = delegator.findList("InventoryItemDetail",  cond,null, null, null, false );
	
		double bookedQuantity = 0;
		//==============================calculate available Stock===================
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS , "ORDRITEM_INVENTORY_ID"));
		conditionList.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, iter.inventoryItemId));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		OrderItemAttribute = delegator.findList("OrderItemAttribute",  cond,null, null, null, false );
		
		if(OrderItemAttribute){
		relaventOrderIds = EntityUtil.getFieldListFromEntityList(OrderItemAttribute, "orderId", true);
		
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN , relaventOrderIds));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		orderIdsWithOutCancelledList = delegator.findList("OrderHeader",  cond,null, null, null, false );
		
		activeOrderIds = EntityUtil.getFieldListFromEntityList(orderIdsWithOutCancelledList, "orderId", true);
		
		bookedOrdersList = EntityUtil.filterByCondition(orderIdsWithOutCancelledList, EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, null));
		bookedOrderIds = EntityUtil.getFieldListFromEntityList(bookedOrdersList, "orderId", true);
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN , bookedOrderIds));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		OrderItemDetailList = delegator.findList("OrderItem",  cond,null, null, null, false );
		
		for (eachOrderItem in OrderItemDetailList) {
			bookedQuantity = bookedQuantity+eachOrderItem.quantity;
		}
		
		}
		row.putAt("bookedQuantity", bookedQuantity);
		
		row.putAt("availbleQuantity", row.get("quantityOnHandTotal")-bookedQuantity);
		
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
			row.putAt("supplierInvoiceId", shipment.supplierInvoiceId); 
			row.putAt("fromPartyId", shipment.partyIdFrom);
			poRefNum = "";
			orderAttributes = delegator.findOne("OrderAttribute", [orderId : shipment.primaryOrderId,attrName:"REF_NUMBER"], false);
			if(UtilValidate.isNotEmpty(orderAttributes)){
				poRefNum=orderAttributes.attrValue;
			}
			
			/*conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipment.primaryOrderId));
			conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"BILL_TO_CUSTOMER"));
			expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);
		  
			OrderRole = EntityUtil.getFirst(OrderRoleList);
			
			branchId = OrderRole.partyId;
			  
			
			
			PartyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", branchId), false);
			
			branchName = PartyGroup.groupName;
			
			row.putAt("branchName", branchName);
			*/
			
			row.putAt("poRefNum", poRefNum);
			row.putAt("facilityId", inventoryItem.facilityId);
			if(UtilValidate.isNotEmpty(inventoryProdStore)){
				row.putAt("branchId", (inventoryProdStore.get(0)).get("productStoreId"));
			}
			
			row.putAt("productStoreId", (inventoryProdStore.get(0)).get("productStoreId"));
			
			row.putAt("facilityName", facility.facilityName);
			row.putAt("partyName", partyName);
			row.putAt("productName", product.productName);
			row.putAt("estimatedShipDate", shipment.estimatedShipDate);
		}else{
			row.putAt("shipmentId", "");
		}
        physicalInventoryCombined.add(row);
    }
    if(UtilValidate.isNotEmpty(supplierInvId)){
        for(eachList in physicalInventoryCombined){
			if(eachList.supplierInvoiceId==supplierInvId){
				physicalInventoryCombined2.add(eachList)
			}
		}
		physicalInventoryCombined=physicalInventoryCombined2;
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
	//Map partyDetailsMap = ByProductNetworkServices.getPartyByRoleType(dctx, inputMap);
	
	//Debug.log("partyDetailsMap===================="+partyDetailsMap);
	
	//billToPartyDetailsList = partyDetailsMap.get("partyDetails");
	/*billToPartyDetailsList.each{eachParty ->
		JSONObject newPartyObj = new JSONObject();
		partyName=PartyHelper.getPartyName(delegator, eachParty.partyId, false);
		newPartyObj.put("value",eachParty.partyId);
		newPartyObj.put("label",partyName+" ["+eachParty.partyId+"]");
		partyNameObj.put(eachParty.partyId,partyName);
		billToPartyIdsJSON.add(newPartyObj);
	}*/
	
	partyDetailsMap = [];
	
	context.billToPartyIdsJSON = billToPartyIdsJSON;
}
/*facilityList = delegator.findList("Facility", null, null, null, null, false);
context.facilityList = facilityList;
*/


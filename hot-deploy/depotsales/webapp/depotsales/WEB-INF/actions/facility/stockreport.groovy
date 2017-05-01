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
import java.util.SortedMap;
 
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.util.Map.Entry;
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

branchId=parameters.branchId;
dctx = dispatcher.getDispatchContext();
Map boothsPaymentsDetail = [:];
branchIdForAdd="";
partyId = userLogin.get("partyId");
rounding = RoundingMode.HALF_UP;

resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));

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

if(branchId){
	branchList.clear();
	branchList = [];
	condListb = [];
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.branchId));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
	
	branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
	
	if(!branchList)
		branchList.add(parameters.branchId);
		
}
	/*branchContext=[:];
	branchContext.put("branchId",branchId);
	BOAddress="";
	try{
		resultCtx = dispatcher.runSync("getBoHeader", branchContext);
		if(ServiceUtil.isError(resultCtx)){
			return ServiceUtil.returnError("Problem in fetching financial year ");
		}
		if(resultCtx.get("boHeaderMap")){
			boHeaderMap=resultCtx.get("boHeaderMap");
			 
			if(boHeaderMap.get("header0")){
				BOAddress=boHeaderMap.get("header0");
			}
			if(boHeaderMap.get("header1")){
				BOEmail=boHeaderMap.get("header1");
			}
		}
	}catch(GenericServiceException e){
		return ServiceUtil.returnError(e.getMessage());
	}
	context.BOAddress=BOAddress;*/

conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchList));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["BILL_TO_CUSTOMER"]));
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);

purorderIds = EntityUtil.getFieldListFromEntityList(OrderRoleList, "orderId", true);


conditionList =[];
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
	if(UtilValidate.isNotEmpty(parameters.statusId)){
		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,parameters.statusId));
	}
	//conditionList.add(EntityCondition.makeCondition("shipmentPurposeTypeId",EntityOperator.EQUALS,"DC_DEPOT_SHIPMENT"));
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
		
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("inventoryItemTypeId", EntityOperator.EQUALS, "NON_SERIAL_INV_ITEM"));
	conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.IN, inventoryItemIdsList));
	
	atpMap = [:];
	qohMap = [:];

	productIds = [] as Set;
	physicalInventory.each { iter ->
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
	
	physicalInventoryCombined = [];
	physicalInventoryCombined2 = [];
	physicalInventoryPDF = [];
	stylesMap=[:];
	if(branchId){
		stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.");
		//stylesMap.put("mainHeader2", BOAddress);
		stylesMap.put("mainHeader3", "Stock Report");
	}
	else{
		stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.");
		stylesMap.put("mainHeader2", "Stock Report");
	}
	stylesMap.put("mainHeaderFontName","Arial");
	stylesMap.put("mainHeadercellHeight",300);
	stylesMap.put("mainHeaderFontSize",10);
	stylesMap.put("mainHeadingCell",2);
	stylesMap.put("mainHeaderBold",true);
	stylesMap.put("columnHeaderBgColor",false);
	stylesMap.put("columnHeaderFontName","Arial");
	stylesMap.put("columnHeaderFontSize",10);
	stylesMap.put("autoSizeCell",true);
	stylesMap.put("columnHeaderCellHeight",300);
	request.setAttribute("stylesMap", stylesMap);
	request.setAttribute("enableStyles", true);
	
	headingMap=[:];
	headingMap.put("shipmentId", "ShipmentId");
	headingMap.put("supplierInvoiceId", "Supplier InvoiceId");
	headingMap.put("facilityName", "Depot");
	headingMap.put("partyName", "Supplier");
	headingMap.put("poRefNum", "Po Ref Num");
	headingMap.put("estimatedShipDate","Received Date");
	headingMap.put("productName","Product Name");
	headingMap.put("quantityOnHandTotal","Stock Available");
	headingMap.put("availbleQuantity","Availble Quantity(Kgs)");
	headingMap.put("unitCost","Unit Cost(Kgs)");
	
	physicalInventoryCombined.add(stylesMap);
	physicalInventoryCombined.add(headingMap);
	
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
		
		row.putAt("bookedQuantity", row.get("quantityOnHandTotal")-row.get("availableToPromiseTotal"));
		
		row.putAt("availbleQuantity", row.get("availableToPromiseTotal"));
		
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
			row.putAt("poRefNum", poRefNum);
			row.putAt("facilityId", inventoryItem.facilityId);
			
			if(UtilValidate.isNotEmpty(inventoryProdStore)){
				row.putAt("branchId", (inventoryProdStore.get(0)).get("productStoreId"));
			}
			row.putAt("productStoreId", (inventoryProdStore.get(0)).get("productStoreId"));
			
			row.putAt("facilityName", facility.facilityName);
			row.putAt("partyName", partyName);
			row.putAt("productName", product.description);
			row.putAt("estimatedShipDate", shipment.estimatedShipDate);
		}else{
			row.putAt("shipmentId", "");
		}
		physicalInventoryCombined.add(row);
		physicalInventoryPDF.add(row);
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
	context.physicalInventoryPDF = physicalInventoryPDF;


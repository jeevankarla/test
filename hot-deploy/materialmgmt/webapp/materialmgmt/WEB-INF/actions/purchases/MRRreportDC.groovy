import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();
shipmentId = parameters.shipmentId;
receiptId = parameters.receiptId;
orderId = parameters.orderId;
dateReceived = parameters.datetimeReceived;
productStoreId ="";
branchContext=[:];
OrderHeaderDetails = delegator.findOne("OrderHeader",["orderId":orderId],false);
if((OrderHeaderDetails) && (OrderHeaderDetails.orderDate)){
	orderDate =  OrderHeaderDetails.orderDate;
	productStoreId = OrderHeaderDetails.get("productStoreId");
	context.orderDate=orderDate;
}
branchId="";
if (productStoreId) {
	productStore = delegator.findByPrimaryKey("ProductStore", [productStoreId : productStoreId]);
	branchId=productStore.payToPartyId;
}
BOAddress="";
BOEmail="";
branchContext.put("branchId",branchId);
try{
	resultCtx = dispatcher.runSync("getBoHeader", branchContext);
	if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in BO Header ", module);
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
	Debug.logError(e, module);
	return ServiceUtil.returnError(e.getMessage());
}
context.BOAddress=BOAddress;
context.BOEmail=BOEmail;
orderAttributeList = delegator.findList("OrderAttribute",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
fileNumberDetails = EntityUtil.filterByCondition(orderAttributeList, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "FILE_NUMBER"));
fileNumberDet=EntityUtil.getFirst(fileNumberDetails);
if((fileNumberDet) && (fileNumberDet.attrValue)){
	 fileNumber = fileNumberDet.attrValue;
	 context.fileNumber=fileNumber;
}
refNumberDetails = EntityUtil.filterByCondition(orderAttributeList, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REF_NUMBER"));
refNumberDet=EntityUtil.getFirst(refNumberDetails);
if((refNumberDet) && (refNumberDet.attrValue)){
	 refNumber = refNumberDet.attrValue;
	 context.refNumber=refNumber;
}
destinationDetails = EntityUtil.filterByCondition(orderAttributeList, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DST_ADDR"));
destinationDetail=EntityUtil.getFirst(destinationDetails);
if((destinationDetail) && (destinationDetail.attrValue)){
	 destination = destinationDetail.attrValue;
	 context.destination=destination;
}
shipmentMap=[:];
shipmentList=[];

shipmentMap.put("shipmentId",shipmentId);
orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(orderHeaderSequences)){
	orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
	salesOrder = orderSeqDetails.orderNo;
	shipmentMap.put("ordId",salesOrder);
}else{
	shipmentMap.put("ordId",orderId);
}
shipmentMap.put("dateReceived",dateReceived);
shipmentMap["total"]=BigDecimal.ZERO;

//get vehicleId
//vehicleDetails = delegator.findList("Shipment",EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS , shipmentId)  , null, null, null, false );
//vehicleDetail=EntityUtil.getFirst(vehicleDetails);

invoiceDetails = delegator.findOne("Shipment",["shipmentId":shipmentId],false);
invoiceNo=invoiceDetails.get("supplierInvoiceId");
invoiceDate=invoiceDetails.get("supplierInvoiceDate");
dcNo=invoiceDetails.get("deliveryChallanNumber");
dcDate=invoiceDetails.get("deliveryChallanDate");
description=invoiceDetails.get("description");
carrierName = invoiceDetails.get("carrierName");
destination = invoiceDetails.get("destination");
shipmentMap.put("invoiceNo",invoiceNo);
shipmentMap.put("invoiceDate",invoiceDate);
shipmentMap.put("dcNo",dcNo);
shipmentMap.put("carrierName",carrierName);
shipmentMap.put("dcDate",dcDate);
shipmentMap.put("description",description);
shipmentMap.put("destination",destination);
estimatedShipCost=0;
if(invoiceDetails.get("estimatedShipCost")){
	estimatedShipCost=invoiceDetails.get("estimatedShipCost");
}
shipmentMap.put("estimatedShipCost",estimatedShipCost);

////get PartyId from Role for Dept
//List conditionlist=[];
//conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
//conditionlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"ISSUE_TO_DEPT"));
//condition=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
//deptDetails = delegator.findList("OrderRole", condition , null, null, null, false );
//partyId=deptDetails.partyId;
//if(UtilValidate.isNotEmpty(partyId)){
//	deptName =  PartyHelper.getPartyName(delegator, partyId, false);
//	shipmentMap.put("deptName",deptName);
//}

//get PartyId from Role for Vendor
partyId="";
billToPartyId="";
billToPartyName="";
List conlist=[];
conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN , UtilMisc.toList("SUPPLIER_AGENT","BILL_FROM_VENDOR") ));
cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
billToPartyIdList=EntityUtil.filterByCondition(vendorDetails, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
if(billToPartyIdList){
billToPartyId=(EntityUtil.getFirst(billToPartyIdList)).getString("partyId");
}
billToPartyName =  PartyHelper.getPartyName(delegator, billToPartyId, false);

if(UtilValidate.isNotEmpty(orderId)){
if(UtilValidate.isNotEmpty(vendorDetails)){
supplierPartyIdList=EntityUtil.filterByCondition(vendorDetails, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER_AGENT"));
if(supplierPartyIdList){
partyId = (EntityUtil.getFirst(supplierPartyIdList)).getString("partyId");
}
shipmentMap.put("partyId",partyId);
	partyName =  PartyHelper.getPartyName(delegator, partyId, false);
	shipmentMap.put("partyName",partyName);
  }
}
if(UtilValidate.isEmpty(orderId)){
fromPartyIdData = delegator.findOne("Shipment",["shipmentId":shipmentId],false);
if(UtilValidate.isNotEmpty(fromPartyIdData)){
partyId=fromPartyIdData.get("partyIdFrom");
shipmentMap.put("partyId",partyId);
partyName =  PartyHelper.getPartyName(delegator, partyId, false);
shipmentMap.put("partyName",partyName);
}
}
if(billToPartyId==shipmentMap.partyId){
	shipmentMap.put("billToPartyId","");
	shipmentMap.put("billToPartyName","");
}else{
shipmentMap.put("billToPartyId",billToPartyId);
shipmentMap.put("billToPartyName",billToPartyName);
}
//orderSequenceNO
OrderHeaderSequenceData = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(OrderHeaderSequenceData)){
OrderHeaderSequenceData=EntityUtil.getFirst(OrderHeaderSequenceData);
sequenceId=OrderHeaderSequenceData.sequenceId;
orderNo=OrderHeaderSequenceData.orderNo;
shipmentMap.put("sequenceId",sequenceId);
shipmentMap.put("orderNo",orderNo);
	}
//shipmentSequenceNO
shipmentSequenceData = delegator.findList("ShipmentSequence",EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS , shipmentId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(shipmentSequenceData)){
shipmentSequenceData=EntityUtil.getFirst(shipmentSequenceData);
shipmentSequenceId=shipmentSequenceData.sequenceId;
shipmentMap.put("shipmentSequenceId",shipmentSequenceId);
	}
grnDetails=[];
if(UtilValidate.isNotEmpty(shipmentId)){
	grnDetailsList = delegator.findList("ShipmentReceipt",EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS , shipmentId)  , null, null, null, false );
	//grnDetailsList = delegator.findOne("ShipmentReceipt",["shipmentId":shipmentId],false);
	
	productIds = EntityUtil.getFieldListFromEntityList(grnDetailsList, "productId", true);

	List facilityIds = FastList.newInstance();
	conlist.clear();
	conlist.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "STORE"));
	conlist.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	facility = delegator.findList("Facility", cond , null, null, null, false );
	if(UtilValidate.isNotEmpty(facility)){
	facilityIds = EntityUtil.getFieldListFromEntityList(facility, "facilityId", true);
	}
	if(UtilValidate.isNotEmpty(facilityIds)){
		conlist.clear();
		conlist.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds));
		conlist.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
		cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
		productStore = delegator.findList("ProductFacility", cond , null, null, null, false );
		if(UtilValidate.isNotEmpty(productStore)){
			productStore=EntityUtil.getFirst(productStore);
			Store=productStore.facilityId;
			shipmentMap.put("store",Store);
		}
	}
		 
}

 grnList=[];
 grnDetailsList.each{grnData->
	grnDetailsMap=[:];
	
	inventoryItemId = grnData.inventoryItemId;
	
	unitPrice = 0;
	List colist=[];
	colist.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, grnData.shipmentId));
	colist.add(EntityCondition.makeCondition("shipmentItemSeqId", EntityOperator.EQUALS,grnData.shipmentItemSeqId));
	cod=EntityCondition.makeCondition(colist,EntityOperator.AND);
	shipmtItemList = delegator.findList("ShipmentItem", cod , null, null, null, false );
	
	
	
	shipmtItemList=EntityUtil.getFirst(shipmtItemList);
	grnDetailsMap["receivedQty"]=shipmtItemList.quantity;
	grnDetailsMap["productId"]=grnData.productId;
	grnDetailsMap["orderId"]=grnData.orderId;
	grnDetailsMap["unitPrice"]=0;
	grnDetailsMap["quantity"]=0;
	grnDetailsMap["amount"]=0;
	grnDetailsMap["deliveryChallanQty"]=0;
	vehicleNo=invoiceDetails.get("vehicleId");
	context.vehicleNo=vehicleNo;
	
	// productDetails
	product = delegator.findOne("Product",["productId":grnData.productId],false);
	if(product){
	grnDetailsMap["internalName"]=product.get("internalName");
	grnDetailsMap["description"]=product.get("description");
	uomId=product.get("quantityUomId");
	}
	if(UtilValidate.isNotEmpty(uomId)){
		unitDesciption = delegator.findOne("Uom",["uomId":uomId],false);
	 grnDetailsMap["unit"]=unitDesciption.get("description");
	}
	// productFolioNo
	productFolioNo = delegator.findOne("ProductAttribute",["productId":grnData.productId,"attrName":"LEDGERFOLIONO"],false);
	if(productFolioNo){
	grnDetailsMap["folioNo"]=productFolioNo.get("attrValue");
	}
	
	//DC QTY
	grnDetailsMap["deliveryChallanQty"]=grnData.deliveryChallanQty;
	
	
	
	// unitPrice
	/*if(UtilValidate.isNotEmpty(inventoryItemId)){
		inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
		unitPrice = inventoryItem.unitCost;
		if(UtilValidate.isNotEmpty(unitPrice)){
		grnDetailsMap["unitPrice"]= unitPrice;
		}
	}*/
	
	
	
	colist.clear();
	colist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, grnData.orderId));
	colist.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,grnData.orderItemSeqId));
	cod=EntityCondition.makeCondition(colist,EntityOperator.AND);
	orderItemList = delegator.findList("OrderItem", cod , null, null, null, false );
	
	
	if(orderItemList){
	unitPrice = orderItemList[0].unitPrice
	
	grnDetailsMap["unitPrice"]= unitPrice;
	
	}
	
	
	if(UtilValidate.isNotEmpty(orderId)){
		List conditionlist=[];
		conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, grnData.orderId));
		conditionlist.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, grnData.orderItemSeqId));
		conditionlist.add(EntityCondition.makeCondition("changeTypeEnumId", EntityOperator.EQUALS, "ODR_ITM_AMEND"));
		conditionMain=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
		def orderBy = UtilMisc.toList("-changeDatetime");
		OrderItemChangeDetails = delegator.findList("OrderItemChange", conditionMain , null ,orderBy, null, false );
		OrderItemChangeDetails=EntityUtil.getFirst(OrderItemChangeDetails);
		if(UtilValidate.isNotEmpty(OrderItemChangeDetails)){
			grnDetailsMap["quantity"]=OrderItemChangeDetails.quantity;
		}
			
	if(UtilValidate.isEmpty(OrderItemChangeDetails)){
	// OrderItems Details
	List condlist=[];
	condlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, grnData.orderId));
	condlist.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, grnData.orderItemSeqId));
	condlist.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, grnData.productId));
	condition=EntityCondition.makeCondition(condlist,EntityOperator.AND);
	ordDetails = delegator.findList("OrderItem", condition , null, null, null, false );
	orderDetails=EntityUtil.getFirst(ordDetails);
	quantity=orderDetails.quantity;
	grnDetailsMap["quantity"]=quantity;
	}
	}
	// Received and Accepted Quantity
	List cList=[];
	cList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, grnData.shipmentId));
	cList.add(EntityCondition.makeCondition("shipmentItemSeqId", EntityOperator.EQUALS,grnData.shipmentItemSeqId));
	cList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN,['SR_ACCEPTED','SR_REJECTED']));
	condit=EntityCondition.makeCondition(cList,EntityOperator.AND);
	
	shipmtReciptList = delegator.findList("ShipmentReceipt", condit , null, null, null, false );
	shipmtReciptList=EntityUtil.getFirst(shipmtReciptList);
	if(UtilValidate.isNotEmpty(shipmtReciptList)){
		grnDetailsMap["quantityAccepted"]=shipmtReciptList.quantityAccepted;
		grnDetailsMap["quantityRejected"]=shipmtReciptList.quantityRejected;
		if(UtilValidate.isNotEmpty(orderId)){
			amount=((shipmtReciptList.quantityAccepted)*(unitPrice));
			grnDetailsMap["amount"]=amount;
			shipmentMap["total"]+=amount;
		}
	}
	if(UtilValidate.isNotEmpty(orderId)){
	if(UtilValidate.isEmpty(shipmtReciptList)){
	grnDetailsMap["quantityAccepted"]=0;
	grnDetailsMap["quantityRejected"]=0;
	amount=((shipmtItemList.quantity)*(unitPrice));
	grnDetailsMap["amount"]=amount;
	shipmentMap["total"]+=amount;
	  }
	}
	grnList.addAll(grnDetailsMap);
}
 
context.shipmentMap=shipmentMap;
context.grnList=grnList;
//Debug.log("shipmentList=================shipmentMap=================="+shipmentMap);


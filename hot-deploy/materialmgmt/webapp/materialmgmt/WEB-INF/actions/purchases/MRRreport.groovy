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

shipmentMap=[:];
shipmentList=[];

shipmentMap.put("receiptId",receiptId);
shipmentMap.put("ordId",orderId);
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
shipmentMap.put("invoiceNo",invoiceNo);
shipmentMap.put("invoiceDate",invoiceDate);
shipmentMap.put("dcNo",dcNo);
shipmentMap.put("dcDate",dcDate);
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
if(UtilValidate.isNotEmpty(orderId)){
List conlist=[];
conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER_AGENT"));
cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
vendorDetail=EntityUtil.getFirst(vendorDetails);
if(UtilValidate.isNotEmpty(vendorDetail.partyId)){
partyId=vendorDetail.partyId;
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
shipmentSequenceData = delegator.findList("ShipmentReceiptSequence",EntityCondition.makeCondition("receiptId", EntityOperator.EQUALS , receiptId)  , null, null, null, false );
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
	productStore = delegator.findList("ProductFacility",EntityCondition.makeCondition("productId", EntityOperator.IN , productIds)  , null, null, null, false );
	productStore=EntityUtil.getFirst(productStore);
	Store=productStore.facilityId;
	shipmentMap.put("store",Store);
	
	}

 grnList=[];
 grnDetailsList.each{grnData->
	grnDetailsMap=[:];
	
	
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
	grnDetailsMap["vehicleId"]=invoiceDetails.get("vehicleId");
	
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
	
	// OrderItems Details
	if(UtilValidate.isNotEmpty(orderId)){
	List condlist=[];
	condlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, grnData.orderId));
	condlist.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, grnData.orderItemSeqId));
	condlist.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, grnData.productId));
	condition=EntityCondition.makeCondition(condlist,EntityOperator.AND);
	ordDetails = delegator.findList("OrderItem", condition , null, null, null, false );
	orderDetails=EntityUtil.getFirst(ordDetails);
	quantity=orderDetails.quantity;
	unitPrice=orderDetails.unitPrice;
	grnDetailsMap["unitPrice"]=unitPrice;
	grnDetailsMap["quantity"]=quantity;
	}
	// Received and Accepted Quantity
	List cList=[];
	cList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, grnData.shipmentId));
	cList.add(EntityCondition.makeCondition("shipmentItemSeqId", EntityOperator.EQUALS,grnData.shipmentItemSeqId));
	cList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"SR_QUALITYCHECK"));
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
	}}
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

//Debug.log("grnList=============="+grnList);
//Debug.log("shipmentList=================shipmentMap=================="+shipmentMap);


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
ordId = parameters.orderId;
dateReceived = parameters.datetimeReceived;


shipmentMap=[:];
shipmentList=[];

//get vehicleId
vehicleDetails = delegator.findList("Shipment",EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS , shipmentId)  , null, null, null, false );
vehicleDetail=EntityUtil.getFirst(vehicleDetails);

shipmentMap.put("receiptId",receiptId);
shipmentMap.put("ordId",ordId);
shipmentMap.put("dateReceived",dateReceived);
shipmentMap["total"]=BigDecimal.ZERO;

//get PartyId from Role for Dept
List conditionlist=[];
conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"ISSUE_TO_DEPT"));
condition=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
deptDetails = delegator.findList("OrderRole", condition , null, null, null, false );
partyId=deptDetails.partyId;

//get deptName
if(UtilValidate.isNotEmpty(partyId)){
	deptName =  PartyHelper.getPartyName(delegator, partyId, false);
	shipmentMap.put("deptName",deptName);	
}

//get PartyId from Role for Vendor
List conlist=[];
conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER_AGENT"));
cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
vendorDetail=EntityUtil.getFirst(vendorDetails);
partyId=vendorDetail.partyId;
shipmentMap.put("partyId",partyId);
//get PartyName 
if(UtilValidate.isNotEmpty(partyId)){
	partyName =  PartyHelper.getPartyName(delegator, partyId, false);
	shipmentMap.put("partyName",partyName);
}

grnDetails=[];
if(UtilValidate.isNotEmpty(shipmentId)){
	grnDetailsList = delegator.findList("ShipmentReceipt",EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS , shipmentId)  , null, null, null, false );
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
	grnDetailsMap["vehicleId"]=vehicleDetail.vehicleId;
	
	// productDetails
	productUomDetails = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.EQUALS , grnData.productId)  , null, null, null, false );
	productUomDetails=EntityUtil.getFirst(productUomDetails);
	grnDetailsMap["internalName"]=productUomDetails.internalName;
	grnDetailsMap["description"]=productUomDetails.description;
	uomId=productUomDetails.quantityUomId;
	if(UtilValidate.isNotEmpty(uomId)){
	uomDesc = delegator.findList("Uom",EntityCondition.makeCondition("uomId", EntityOperator.EQUALS , uomId)  , null, null, null, false );
	uomDesc=EntityUtil.getFirst(uomDesc);
	grnDetailsMap["unit"]=uomDesc.abbreviation;
	  }
	
	// OrderItems Details
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
	amount=((shipmtReciptList.quantityAccepted)*(unitPrice));
	grnDetailsMap["amount"]=amount;
	shipmentMap["total"]+=amount;
	}
	if(UtilValidate.isEmpty(shipmtReciptList)){
	grnDetailsMap["quantityAccepted"]=0;
	grnDetailsMap["quantityRejected"]=0;
	amount=((shipmtItemList.quantity)*(unitPrice));
	grnDetailsMap["amount"]=amount;
	shipmentMap["total"]+=amount;
	}
	
	grnList.addAll(grnDetailsMap);
}
context.shipmentMap=shipmentMap;
context.grnList=grnList;

//Debug.log("grnList=============="+grnList);
//Debug.log("shipmentList=================shipmentMap=================="+shipmentMap);



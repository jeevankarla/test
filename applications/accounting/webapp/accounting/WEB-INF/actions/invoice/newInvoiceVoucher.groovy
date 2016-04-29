import java.util.*;
import java.lang.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.MathContext;

import org.ofbiz.base.util.UtilNumber;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.party.party.PartyHelper;


import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.finder.EntityFinderUtil.ConditionList;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;




invoiceId = parameters.invoiceId;
context.invoiceId = invoiceId;
invoiceList = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
partyId = invoiceList.get("partyId");
context.partyId = partyId;
invoiceDate = invoiceList.get("invoiceDate");
context.invoiceDate = invoiceDate;
shipmentId = invoiceList.get("shipmentId");
partyIdFrom = invoiceList.partyIdFrom;
context.partyIdFrom = partyIdFrom;


passNo = "";
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
if(PartyIdentificationList){
passNo = PartyIdentificationList[0].get("idValue");
}




poNumber = "";
orderId  = "";
shipmentDate = "";
lrNumber = "";
supplierInvoiceId = "";
supplierInvoiceDate = "";
carrierName = "";
estimatedShipCost = "";

if(shipmentId){
shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
orderId = shipmentList.get("primaryOrderId");
deliveryChallanDate = shipmentList.get("deliveryChallanDate");
lrNumber = shipmentList.get("lrNumber");
supplierInvoiceId = shipmentList.get("supplierInvoiceId");
carrierName = shipmentList.get("carrierName");
estimatedShipCost = shipmentList.get("estimatedShipCost");
if(UtilValidate.isNotEmpty(shipmentList.get("supplierInvoiceDate"))){
supplierInvoiceDate = shipmentList.get("supplierInvoiceDate");
}
}

context.deliveryChallanDate = deliveryChallanDate;
context.poNumber = orderId;
context.supplierInvoiceId = supplierInvoiceId;
context.supplierInvoiceDate = supplierInvoiceDate;
context.lrNumber = lrNumber;
context.carrierName = carrierName;
context.estimatedShipCost = estimatedShipCost;
context.passNo = passNo;



conditionList = [];
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
//conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoiceItemLists = delegator.findList("InvoiceItem", cond, null, null, null, false);

invoiceItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
invoiceAdjItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_FPROD_ITEM"));

orderAttrForPo = [];
if(orderId){
orderAttrForPo = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
OrderHeaderList = delegator.findOne("OrderHeader",[orderId : orderId] , false);
}

grandTotal = OrderHeaderList.get("grandTotal")

context.grandTotal = grandTotal;

actualOrderId = "";
destination = "";


if(UtilValidate.isNotEmpty(orderAttrForPo)){
	
	orderAttrForPo.each{ eachAttr ->
		if(eachAttr.attrName == "PO_NUMBER"){
			actualOrderId =  eachAttr.attrValue;
		}
		if(eachAttr.attrName == "DST_ADDR"){
			destination =  eachAttr.attrValue;
		}
	}
}


context.indentNo = actualOrderId;

OrderHeader = [];
OrderRoleList = [];
indentDate = "";
soceity = "";
poDate = "";
scheme = "";
supplier = "";
onbehalf = "";
finalDetails = [];
if(actualOrderId){
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, [orderId,actualOrderId]));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
OrderHeader = delegator.findList("OrderHeader", cond, UtilMisc.toSet("orderId","orderDate"), null, null, false);

indentDetails = EntityUtil.filterByCondition(OrderHeader, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
indentDate = indentDetails[0].get("orderDate");
PODetails = EntityUtil.filterByCondition(OrderHeader, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
poDate = PODetails[0].get("orderDate");


conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["SUPPLIER","ON_BEHALF_OF","BILL_TO_CUSTOMER"]));
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);


	orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId), null, null, null, false);
	
	if(UtilValidate.isNotEmpty(orderAttr)){
		orderAttr.each{ eachAttr ->
			if(eachAttr.attrName == "SCHEME_CAT"){
				scheme =  eachAttr.attrValue;
			}
			
		}
	   }
	}

onbehalf = "";

for (eachRole in OrderRoleList) {
	
	if(eachRole.roleTypeId == "SUPPLIER")
	 supplier = eachRole.get("partyId");
	if(eachRole.roleTypeId == "BILL_TO_CUSTOMER")
	 soceity = eachRole.get("partyId");
	if(eachRole.roleTypeId == "ON_BEHALF_OF")
	     onbehalf = true;
}



context.poDate = poDate;
context.indentDate = indentDate;
context.scheme = scheme;
context.supplier = supplier;
context.destination = destination;
context.soceity = soceity;
context.onbehalf = onbehalf;


//if(onbehalf == true){
  // forOnbeHalf()
//}else{
  // context.reportTypeFlag = "DIRECT";
	
	SchemeQtyMap = [:];
	SchemeAmtMap = [:];
	
	double schemeDeductionAmt = 0;
	
	for (eachInvoiceList in invoiceAdjItemList) {
		if(UtilValidate.isNotEmpty(eachInvoiceList.parentInvoiceItemSeqId) && UtilValidate.isNotEmpty(eachInvoiceList.quantity))
		{ 
		SchemeQtyMap.put(eachInvoiceList.parentInvoiceItemSeqId, eachInvoiceList.quantity);
		}
		if(UtilValidate.isNotEmpty(eachInvoiceList.parentInvoiceItemSeqId) && UtilValidate.isNotEmpty(eachInvoiceList.amount))
		{
		SchemeAmtMap.put(eachInvoiceList.parentInvoiceItemSeqId, eachInvoiceList.amount);
		schemeDeductionAmt = schemeDeductionAmt+Math.abs(eachInvoiceList.amount);
		}
	}
	
	context.schemeDeductionAmt = Math.round(schemeDeductionAmt);
	
	double grandTotal = 0;
	if(invoiceItemList){
	
		
		List productIds = EntityUtil.getFieldListFromEntityList(invoiceItemList, "productId", true);
		
		
		
		for(eachProd in productIds)
		{

			eachInvoiceItemList = EntityUtil.filterByCondition(invoiceItemList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd));
			
			tempMap = [:];
			double schemeAmt = 0;
			double quantity = 0;
			double amount = 0;
		for (eachInvoiceList in eachInvoiceItemList) {
	   
		 tempMap.put("productId", eachInvoiceList.productId);
		 tempMap.put("prodDescription", eachInvoiceList.description);
		 tempMap.put("rateKg", eachInvoiceList.unitPrice);
		 tempMap.put("amount", eachInvoiceList.amount);
		 /*String seq = String.format("%05d", i);
		 
		 
		 OrderItemAttribute = EntityUtil.filterByCondition(OrderItemAttributeList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, seq));
		 
		  baleQty = "";
		  if(OrderItemAttribute){
		  OrderItemAttribute.each{ eachAttr ->
			 if(eachAttr.attrName == "BALE_QTY"){
				 baleQty =  eachAttr.attrValue;
			 }
	      }
	    }*/
		 
		 
		// String seq = String.format("%05d", i);
		 
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
		 conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachInvoiceList.invoiceItemSeqId));
		 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 OrderItemBilling = delegator.findList("OrderItemBilling", cond, null, null, null, false);
		
		 String seq = OrderItemBilling[0].get("orderItemSeqId");
		 
		 
		 
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
		 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, seq));
		 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 OrderItemAttributeList1 = delegator.findList("OrderItemAttribute", cond, null, null, null, false);
		 
		 
		 if(OrderItemAttributeList1){
		 OrderItemAttributeList1.each{ eachAttr ->
			if(eachAttr.attrName == "quotaQty"){
				schemeAmt =  schemeAmt+Double.valueOf(eachAttr.attrValue);
			}
		 }
	   }
		 
		 
		 quantity = quantity+eachInvoiceList.quantity;
		 amount = eachInvoiceList.amount;
		 
	
	   }
		
		tempMap.put("baleQty", 0);
		tempMap.put("unit", "");
		tempMap.put("quantity", quantity);
		
		//String schemeAmt = (String)SchemeQtyMap.get(eachInvoiceList.invoiceItemSeqId);
		
		if(UtilValidate.isNotEmpty(schemeAmt))
		  tempMap.put("schemeQty", Double.valueOf(schemeAmt));
		else
		  tempMap.put("schemeQty", 0);
		  
		  
		  tempMap.put("ToTamount", quantity*amount);
		
		  grandTotal = grandTotal+(quantity*amount);
		  
		  
		finalDetails.add(tempMap);
		}		
		
	 }
		
	context.grandTotal = Math.round(grandTotal);
	context.finalDetails = finalDetails;
	
//}




//==============Address Details===================

finalAddresList=[];
address1="";
address2="";
city="";
postalCode="";
panId="";
tanId="";

partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);

if(partyPostalAddress){
	
   if(partyPostalAddress.address1){
	   address1=partyPostalAddress.address1;
   }
   tempMap=[:];
   tempMap.put("key1","Road / Street / Lane");
   tempMap.put("key2",address1);
   finalAddresList.add(tempMap);
   if(partyPostalAddress.address2){
	   address2=partyPostalAddress.address2;
   }
   tempMap=[:];
   tempMap.put("key1","Area / Locality");
   tempMap.put("key2",address2);
   finalAddresList.add(tempMap);
   if(partyPostalAddress.city){
	   
	   city=partyPostalAddress.city;
   }
   tempMap=[:];
   tempMap.put("key1","Town / District / City");
   tempMap.put("key2",city);
   finalAddresList.add(tempMap);
   
   if(partyPostalAddress.postalCode){
	   postalCode=partyPostalAddress.postalCode;
   }
   tempMap=[:];
   tempMap.put("key1","PIN Code");
   tempMap.put("key2",postalCode);
   finalAddresList.add(tempMap);
   
}

context.finalAddresList = finalAddresList;
//============================================================


//============================on beHalf================


//def forOnbeHalf(){
	OrderItemAttributeList = [];
	finaOnbehalflDetails = [];
	
	//context.reportTypeFlag = "ONBEHALF";
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderItemList = delegator.findList("OrderItem", cond, null, null, null, false);
	
	if(actualOrderId){
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		OrderItemAttributeList = delegator.findList("OrderItemAttribute", cond, null, null, null, false);
		orderItemSeqIdList = EntityUtil.getFieldListFromEntityList(OrderItemAttributeList, "orderItemSeqId", true);
		
		for (eachSeq in orderItemSeqIdList) {
			
			OrderItemAttribute = EntityUtil.filterByCondition(OrderItemAttributeList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachSeq));
			
			partyId = "";
			productId = "";
			itemDescription = "";
			passNo = "";
			quantity = 0;
			unitPrice = 0;
			quotaQty = 0;
			amount = 0;
			tempMap = [:];
			
			Debug.log("OrderItemAttribute============"+OrderItemAttribute);
			
			if(UtilValidate.isNotEmpty(OrderItemAttribute)){
				OrderItemAttribute.each{ eachAttr ->
					if(eachAttr.attrName == "WIEVER_CUSTOMER"){
						partyId =  eachAttr.attrValue;
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
						conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
						cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
						if(PartyIdentificationList){
						passNo = PartyIdentificationList[0].get("idValue");
						}
					}
					if(eachAttr.attrName == "quotaQty"){
						quotaQty =  eachAttr.attrValue;
						
					}
				}
			   }
			
			   tempMap.put("partyId", partyId);
			   
			   
			   tempMap.put("passNo", passNo);
			   
			eachOrderItemList = EntityUtil.filterByCondition(OrderItemList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachSeq));
			
			if(eachOrderItemList){
				
				itemDescription = eachOrderItemList[0].get("itemDescription");
				tempMap.put("itemDescription", itemDescription);
				quantity = eachOrderItemList[0].get("quantity");
				tempMap.put("quantity", quantity);
				unitPrice = eachOrderItemList[0].get("unitPrice");
				tempMap.put("quotaQty", Math.round(((Double.valueOf(quotaQty)*unitPrice)*10)/100));
				amount = quantity*unitPrice;
				tempMap.put("amount", amount);
			}
			finaOnbehalflDetails.add(tempMap);
		}
		
	}
	context.finaOnbehalflDetails = finaOnbehalflDetails;
	
//}

	

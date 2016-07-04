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
billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
	invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
	invoiceSequence = invoiceSeqDetails.invoiceSequence;
	context.invoiceId = invoiceSequence;
}else{
	context.invoiceId = invoiceId;
}
invoiceList = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
partyId = invoiceList.get("partyId");
context.partyId = partyId;
invoiceDate = invoiceList.get("invoiceDate");
context.invoiceDate = invoiceDate;
shipmentId = invoiceList.get("shipmentId");
partyIdFrom = invoiceList.partyIdFrom;
context.partyIdFrom = partyIdFrom;

conditionList = [];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
fcond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

FacilityList = delegator.findList("Facility", fcond, null, null, null, false);



isDepot = "";
if(FacilityList)
isDepot ="Y"
else
isDepot ="N"


context.isDepot = isDepot;

passNo = "";
conditionList.clear();
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

deliveryChallanDate = "";

if(shipmentList.get("deliveryChallanDate"))
 deliveryChallanDate = shipmentList.get("deliveryChallanDate");

 lrNumber = "";
 if(shipmentList.get("lrNumber"))
lrNumber = shipmentList.get("lrNumber");

supplierInvoiceId ="";
if(shipmentList.get("supplierInvoiceId"))
supplierInvoiceId = shipmentList.get("supplierInvoiceId");

carrierName = "";

if(shipmentList.get("carrierName"))
carrierName = shipmentList.get("carrierName");


estimatedShipCost = shipmentList.get("estimatedShipCost");


if(UtilValidate.isNotEmpty(shipmentList.get("supplierInvoiceDate"))){
supplierInvoiceDate = shipmentList.get("supplierInvoiceDate");
}
}

context.deliveryChallanDate = deliveryChallanDate;
orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , UtilMisc.toSet("orderNo"), null, null, false );



if(UtilValidate.isNotEmpty(orderHeaderSequences)){
	orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
	draftPoNum = orderSeqDetails.orderNo;
	context.poNumber = draftPoNum;
}else{
	context.poNumber = orderId;
}
context.supplierInvoiceId = supplierInvoiceId;
context.supplierInvoiceDate = supplierInvoiceDate;
context.lrNumber = lrNumber;
context.carrierName = carrierName;
context.estimatedShipCost = estimatedShipCost;
context.passNo = passNo;



conditionList = [];
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoiceItemLists = delegator.findList("InvoiceItem", cond, null, null, null, false);

invoiceItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
invoiceAdjItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_FPROD_ITEM"));

invoiceRemainigAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,null));
invoiceItemLevelAdjustments = [:];


Debug.log("invoiceAdjItemList==============="+invoiceAdjItemList);


double totTaxAmount = 0;

double mgpsAmt = 0;

int i=0;
for (eachList in invoiceItemList) {
	
	 
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachList.invoiceId));
	conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachList.invoiceItemSeqId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    invoiceInnerAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, cond);
	
	// Debug.log("invoiceAdjItemList====+"+i+"======="+invoiceAdjItemList);
	
	
	
	Debug.log("invoiceInnerAdjItemList==============="+invoiceInnerAdjItemList);
	
	
	 itemAdjustList = [];
	  
	 if(invoiceInnerAdjItemList){
	 for (eachItem in invoiceInnerAdjItemList) {
		
		  if(eachItem.description != "Service Charge" && eachItem.invoiceItemTypeId != "TEN_PERCENT_SUBSIDY"){
		  tempMap = [:];
		  
		  Debug.log("eachItem.amount==============="+eachItem.amount);
		  
		  
		  tempMap.put("invoiceId", eachItem.invoiceId);
		  tempMap.put("invoiceItemTypeId", eachItem.invoiceItemTypeId);
		  tempMap.put("description", eachItem.description);
		  tempMap.put("quantity", eachItem.quantity);
		  tempMap.put("amount", eachItem.amount);
		  
		  totTaxAmount = totTaxAmount+(eachItem.amount*eachItem.quantity);
		  
		  itemAdjustList.add(tempMap);
		  }
		  if(eachItem.invoiceItemTypeId == "TEN_PERCENT_SUBSIDY"){
		  mgpsAmt = mgpsAmt+eachItem.amount;
		  }
	}
	 }
	 
	 
	 Debug.log("totTaxAmount==============="+totTaxAmount);
	 
	if(itemAdjustList){
		invoiceItemLevelAdjustments.put(i, itemAdjustList);
	}else{
	    invoiceItemLevelAdjustments.put(i,"NoAdjustments");
	}
	i++;
	
}
context.invoiceItemLevelAdjustments = invoiceItemLevelAdjustments;

context.invoiceRemainigAdjItemList = invoiceRemainigAdjItemList;

context.totTaxAmount = totTaxAmount;

context.mgpsAmt = mgpsAmt;



orderAttrForPo = [];
if(orderId){
orderAttrForPo = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
OrderHeaderList = delegator.findOne("OrderHeader",[orderId : orderId] , false);

tallyRefNo = OrderHeaderList.get("tallyRefNo");

context.tallyRefNo = tallyRefNo;

productStoreId = OrderHeaderList.get("productStoreId");
branchId="";
if (productStoreId) {
	productStore = delegator.findByPrimaryKey("ProductStore", [productStoreId : productStoreId]);
	branchId=productStore.payToPartyId;
	
}
//get Report Header
branchContext=[:];

if(branchId == "INT12" || branchId == "INT49" || branchId == "INT55")
branchId = "INT12";
else if(branchId == "INT8" || branchId == "INT16" || branchId == "INT20" || branchId == "INT21" || branchId == "INT22" || branchId == "INT44")
branchId = "INT8";

Debug.log("branchId=================="+branchId);

branchContext.put("branchId",branchId);
BOAddress="";
BOEmail="";

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
}

grandTotal = OrderHeaderList.get("grandTotal")

context.grandTotal = grandTotal;

actualOrderId = "";
destination = "";

Debug.log("grandTotal=================="+grandTotal);


if(UtilValidate.isNotEmpty(orderAttrForPo)){
	
	orderAttrForPo.each{ eachAttr ->
		if(eachAttr.attrName == "PO_NUMBER"){
			actualOrderId =  eachAttr.attrValue;
		}
		if(eachAttr.attrName == "DST_ADDR"){
			destination =  eachAttr.attrValue;
			
			/*if(districtGeoId){
				districtName = districtGeoId ;
				GenericValue geo = delegator.findOne("Geo", [geoId:districtGeoId], false);
				if(UtilValidate.isNotEmpty(geo)&& UtilValidate.isNotEmpty(geo.get("geoName"))){
					destination = geo.get("geoName");
					}
			}*/
			
		}
	}
}

indentOrderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , actualOrderId)  , UtilMisc.toSet("orderNo"), null, null, false );
if(UtilValidate.isNotEmpty(indentOrderSequences)){
	indentOrderSeqDetails = EntityUtil.getFirst(indentOrderSequences);
	salesOrder = indentOrderSeqDetails.orderNo;
	context.indentNo = salesOrder;
}else{
	context.indentNo = actualOrderId;
}
orderHeaderSequences = delegator.findList("OrderHeaderSequence", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId), null, null, null, false);
if(UtilValidate.isNotEmpty(orderHeaderSequences)){
	orderSquences = EntityUtil.getFirst(orderHeaderSequences);
	orderNo= orderSquences.orderNo;
	context.indentNo = orderNo;
}



Debug.log("indentOrderSequences=================="+indentOrderSequences);


Debug.log("orderHeaderSequences=================="+orderHeaderSequences);


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
OrderHeader = delegator.findList("OrderHeader", cond, UtilMisc.toSet("orderId","orderDate","externalId"), null, null, false);

indentDetails = EntityUtil.filterByCondition(OrderHeader, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
indentDate = indentDetails[0].get("orderDate");

externalOrderId = indentDetails[0].get("externalId");
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
	
	
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderItemAttributeList = delegator.findList("OrderItemAttribute", cond, null, null, null, false);
	C2E2Form = "";
	
	
	if(OrderItemAttributeList){
		for (eachAttr in OrderItemAttributeList) {
			if(eachAttr.attrName == "checkCForm"){
				C2E2Form = eachAttr.attrValue;
				break;
			}
			else if(eachAttr.attrName == "checkE2Form"){
				C2E2Form = eachAttr.attrValue;
				break;
			}
		}
	}
	
	context.C2E2Form = C2E2Form;
	
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
context.externalOrderId = externalOrderId;



//if(onbehalf == true){
  // forOnbeHalf()
//}else{
  // context.reportTypeFlag = "DIRECT";
	
	SchemeQtyMap = [:];
	SchemeAmtMap = [:];
	
	double schemeDeductionAmt = 0;
	
	/*for (eachInvoiceList in invoiceAdjItemList) {
		if(UtilValidate.isNotEmpty(eachInvoiceList.parentInvoiceItemSeqId) && UtilValidate.isNotEmpty(eachInvoiceList.quantity))
		{ 
		SchemeQtyMap.put(eachInvoiceList.parentInvoiceItemSeqId, eachInvoiceList.quantity);
		}
		if(UtilValidate.isNotEmpty(eachInvoiceList.parentInvoiceItemSeqId) && UtilValidate.isNotEmpty(eachInvoiceList.amount))
		{
		SchemeAmtMap.put(eachInvoiceList.parentInvoiceItemSeqId, eachInvoiceList.amount);
		schemeDeductionAmt = schemeDeductionAmt+Math.abs(eachInvoiceList.amount);
		}
		
		
		
		
		
		
		
	}*/
	
	
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
			
			OrderItemDetail = [];
		for (eachInvoiceList in eachInvoiceItemList) {
	   
		 tempMap.put("productId", eachInvoiceList.productId);
		 tempMap.put("prodDescription", eachInvoiceList.description);
		 tempMap.put("rateKg", eachInvoiceList.unitPrice);
		// tempMap.put("amount", eachInvoiceList.amount);
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
		
		 		 
		 itemOrderId  = OrderItemBilling[0].orderId;
		 orderItemSeqId  = OrderItemBilling[0].orderItemSeqId;
		 
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,itemOrderId));
		 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
		 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
	
		 
	/*	 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
		 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, seq));
		 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 OrderItemAttributeList1 = delegator.findList("OrderItemAttribute", cond, null, null, null, false);
		 baleQty="";
		 unit="";
		 if(OrderItemAttributeList1){
		 OrderItemAttributeList1.each{ eachAttr ->
			if(eachAttr.attrName == "quotaQty"){
				schemeAmt =  schemeAmt+Double.valueOf(eachAttr.attrValue);
			}
			if(eachAttr.attrName == "BALE_QTY"){
				baleQty =  eachAttr.attrValue;
			}
			if(eachAttr.attrName == "YARN_UOM"){
				unit =  eachAttr.attrValue;
			}
		 }
	   }*/
		 
		 
		 quantity = quantity+eachInvoiceList.quantity;
		 amount = eachInvoiceList.amount;
		
		 double baleQty = 0; 
		 double unit = 0;
		 double quotaQuantity = 0;
	  
		 for (eachOrderItemDetail in OrderItemDetail) {
		
			 if(eachOrderItemDetail.baleQuantity)
			 baleQty = baleQty+Double.valueOf(eachOrderItemDetail.baleQuantity);
			 
			 
			 if(eachOrderItemDetail.unitPrice)
			 unit = unit+Double.valueOf(eachOrderItemDetail.unitPrice);
			 
			 
			 if(eachOrderItemDetail.quotaQuantity)
			 quotaQuantity = quotaQuantity+Double.valueOf(eachOrderItemDetail.quotaQuantity);
			 
		}
		 
		 tempMap.put("unit", unit);
		 
		 
		    if(baleQty)
			tempMap.put("baleQty",baleQty);
			else
			tempMap.put("baleQty","");
			
			
		    tempMap.put("quantity", quantity);
		
		//String schemeAmt = (String)SchemeQtyMap.get(eachInvoiceList.invoiceItemSeqId);
			double tenPerQty = 0;
			
			if(quantity > quotaQuantity)
			{
			  tempMap.put("schemeQty", quotaQuantity);
			  tenPerQty = quotaQuantity;
			}
			else
			{
			  tempMap.put("schemeQty", quantity);
			  tenPerQty = quantity;
			}
		
		/*if(UtilValidate.isNotEmpty(schemeAmt))
		  tempMap.put("schemeQty", Double.valueOf(schemeAmt));
		else
		  tempMap.put("schemeQty", 0);
*/		  
		  
		  if(scheme == "General")
		  tempMap.put("mgpsQty", 0);
		  else
		  tempMap.put("mgpsQty", quantity-tenPerQty);
			
		  
		 double serviceAmt = 0;
		 double sourcePercentage = 0;
		 
		  if(scheme == "General"){
			  
			  conditionList.clear();
			  conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
			  conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachInvoiceList.invoiceItemSeqId));
			  conditionList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"Service Charge"));
			  cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			  invoiceInnerAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, cond);
			  
			  if(invoiceInnerAdjItemList){
			  serviceAmt = serviceAmt+invoiceInnerAdjItemList[0].amount;
			  //sourcePercentage = sourcePercentage+invoiceInnerAdjItemList[0].sourcePercentage;
			  
			  }
		  }
		  
		  
		  if(scheme == "General"){
			  
			  sourcePercentage = (serviceAmt/(quantity*amount))*100;
			  double perAmt = (eachInvoiceList.amount*sourcePercentage)/100;
			  
			  tempMap.put("amount",(eachInvoiceList.amount+perAmt));
			  }else{
			  tempMap.put("amount", eachInvoiceList.amount);
		   }
			  
		  
		  tempMap.put("ToTamount", (quantity*amount)+serviceAmt);
		  grandTotal = grandTotal+(quantity*amount)+serviceAmt;
		  
		 /* double mgpsQty = 0;
		  if(quantity > schemeAmt)
		    mgpsQty = schemeAmt;
		  else  
		    mgpsQty = quantity;
		    tempMap.put("mgpsQty", mgpsQty);*/
		  
		  
		  
		finalDetails.add(tempMap);
		 }
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
	
	/*conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderItemList = delegator.findList("OrderItem", cond, null, null, null, false);
*/	
	if(actualOrderId){
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
		
		
		for (eachOrderItemList in OrderItemDetail) {
			
			//OrderItemAttribute = EntityUtil.filterByCondition(OrderItemAttributeList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachSeq));
			
			partyId = "";
			productId = "";
			itemDescription = "";
			passNo = "";
			quantity = 0;
			unitPrice = 0;
			quotaQty = 0;
			amount = 0;
			tempMap = [:];
			
			//Debug.log("OrderItemAttribute============"+OrderItemAttribute);
			
	/*		if(UtilValidate.isNotEmpty(OrderItemAttribute)){
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
			   }*/
			
			
			    partyId = eachOrderItemList.get("partyId");
			 
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
				cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
				if(PartyIdentificationList){
				passNo = PartyIdentificationList[0].get("idValue");
				}
			   
			   tempMap.put("partyId", partyId);
			   
			   tempMap.put("passNo", passNo);
			   
			//eachOrderItemList = EntityUtil.filterByCondition(OrderItemList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachSeq));
			
			if(eachOrderItemList){
				
				//itemDescription = eachOrderItemList[0].get("itemDescription");
				tempMap.put("itemDescription", "");
				quantity = eachOrderItemList.get("quantity");
				tempMap.put("quantity", quantity);
				unitPrice = eachOrderItemList.get("unitPrice");
				tempMap.put("quotaQty", Math.round(((Double.valueOf(quotaQty)*unitPrice)*10)/100));
				amount = quantity*unitPrice;
				tempMap.put("amount", amount);
			}
			finaOnbehalflDetails.add(tempMap);
		}
		
	}
	context.finaOnbehalflDetails = finaOnbehalflDetails;
	
//}

	

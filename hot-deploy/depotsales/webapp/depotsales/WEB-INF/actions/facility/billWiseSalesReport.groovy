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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.util.Map.Entry;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.contact.ContactMechWorker;
 
SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM dd");
Timestamp fromDate;
Timestamp thruDate;
 
DateList=[];
DateMap = [:];
partyfromDate=parameters.billReportfromDate;
partythruDate=parameters.billReportthruDate;
branchId = parameters.branchId;
state=parameters.state;
productCategory=parameters.productCategory;
partyId=parameters.partyId;

DateMap.put("partyfromDate", partyfromDate);
DateMap.put("partythruDate", partythruDate);
DateList.add(DateMap);
context.DateList=DateList;
 
branchName = "";
if(branchId){
	branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
	branchName = branch.get("groupName");
	DateMap.put("branchName", branchName);
}
branchList = [];
condListb = [];
if(branchId){
condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
 
branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
 
if(!branchList)
	branchList.add(branchId);
}
 
branchBasedWeaversList = [];
condListb = [];
if(branchId){
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
	branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
}
 
productIds = [];
productCategoryIds = [];
condListCat = [];
if(!partyId){
	if(productCategory != "OTHER"){
		condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS, productCategory));
		condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
		ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
		productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	}else if(productCategory == "OTHER"){
		condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_IN, ["SILK","JUTE_YARN"]));
		condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
		ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
		productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	}
	branchContext=[:];
	branchContext.put("branchId",branchId);
	BOAddress="";
	BOEmail="";
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
	context.BOAddress=BOAddress;
	context.BOEmail=BOEmail;
	condListCat.clear();
	condListCat.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategoryMember = delegator.findList("ProductCategoryMember", condList1,UtilMisc.toSet("productId"), null, null, false);
	productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
}
   
daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(parameters.billReportfromDate)){
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.billReportfromDate).getTime());
		daystart = UtilDateTime.getDayStart(fromDate);
	} catch (ParseException e) {
	}
}
if(UtilValidate.isNotEmpty(parameters.billReportthruDate)){
   try {
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.billReportthruDate).getTime());
	   dayend = UtilDateTime.getDayEnd(thruDate);
   } catch (ParseException e) {
   }
}
context.daystart = daystart;
context.dayend = dayend;
   
reimbursmentPercentage = [:];
reimbursmentPercentage.put("SILK", 1);
reimbursmentPercentage.put("JUTE_YARN", 10);
branchpartyIdsList = [];
if(state && !partyId){
	condListb4 = [];
	if(branchBasedWeaversList)
	condListb4.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
	condListb4.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
	if(state == "IN-TS")
	condListb4.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, "IN-TNG"));
	else if(state)
	condListb4.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
	condListb = EntityCondition.makeCondition(condListb4, EntityOperator.AND);
	PartyContactDetailByPurposeList = delegator.find("PartyContactDetailByPurpose", condListb, null, UtilMisc.toSet("partyId"), null, null);
	branchBasedWeaversList = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByPurposeList, "partyId", true);
	if(UtilValidate.isEmpty(branchBasedWeaversList)){
	 return "Selected Worng State";
	}
}
condList = [];
if(UtilValidate.isNotEmpty(daystart)){
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
}
condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
if(productIds)
condList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
if(partyId){
	condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
}else if(!partyId && branchBasedWeaversList){
	condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
}
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
fieldsToSelect = ["invoiceId"] as Set;
invoice = delegator.find("InvoiceAndItem", cond, null, fieldsToSelect, null, null);
invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);
 
conditionList1 = [];
conditionList1.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
conditionList1.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
cond = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
 
orderIds=EntityUtil.getFieldListFromEntityList(OrderItemBilling, "orderId", true);
conditionList1.clear();
conditionList1.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
conditionList1.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
conditionList1.add(EntityCondition.makeCondition("attrValue", EntityOperator.IN, ["MGPS_10Pecent","MGPS"]));
orderAttr = delegator.findList("OrderAttribute",EntityCondition.makeCondition(conditionList1, EntityOperator.AND), null, null, null, false);
orderIds=EntityUtil.getFieldListFromEntityList(orderAttr, "orderId", true);
OrderItemBillingFilter = EntityUtil.filterByCondition(OrderItemBilling, EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
invoiceIds=EntityUtil.getFieldListFromEntityList(OrderItemBillingFilter, "invoiceId", true);
condList = [];
condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
Invoice = delegator.findList("Invoice", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
partyIds=EntityUtil.getFieldListFromEntityList(Invoice, "partyId", true);
partyPassMap= [:];
finalMap = [:];
partyWiseTotalsMap = [:];
 
finalList = [];
for(int i=0;i < Invoice.size();i++){
	eachInvoice = Invoice.get(i);
	invoiceDetailMap = [:];
	invoiceDetailMap.put("invoiceId", eachInvoice.invoiceId);
	billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , eachInvoice.invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
	if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
		invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
		invoiceSequence = invoiceSeqDetails.invoiceSequence;
		invoiceDetailMap.put("billno", invoiceSequence);
	}else{
		invoiceDetailMap.put("billno", eachInvoice.invoiceId);
	}
	invoiceDetailMap.put("invoiceDate",UtilDateTime.toDateString(eachInvoice.invoiceDate,"dd/MM/yyyy"));
	invoiceDetailMap.put("partyId",eachInvoice.partyId);
	custPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, eachInvoice.partyId, false);
	invoiceDetailMap.put("partyName",custPartyName);
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachInvoice.partyId));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, eachInvoice.invoiceDate));
	
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, eachInvoice.invoiceDate),
		EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));	
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	partyClassification = delegator.findList("PartyClassification",condition,null,null,null,false);
	if(UtilValidate.isNotEmpty(partyClassification)){
		PartyClassificationDetails = EntityUtil.getFirst(partyClassification);
		partyClassificationGroupIdList = delegator.findOne("PartyClassificationGroup",UtilMisc.toMap("partyClassificationGroupId", PartyClassificationDetails.get("partyClassificationGroupId")), false);
	}
	
	invoiceDetailMap.put("amount", eachInvoice.invoiceGrandTotal);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoice.invoiceId));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	invoiceItems = delegator.findList("InvoiceItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
	qty = 0;
	if(UtilValidate.isNotEmpty(invoiceItems)){
		invoiceItems.each{ eachInvoice->
			qty = qty + eachInvoice.quantity;
		}
	}
	invoiceDetailMap.put("qty", qty);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoice.invoiceId));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,UtilMisc.toList("TEN_PERCENT_SUBSIDY","TEN_PER_DISCOUNT")));
	subsidyItems = delegator.findList("InvoiceItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
	subsidyAmount = 0;
	subsidyQty = 0;
	scheme = "MGP Scheme";
	if(UtilValidate.isNotEmpty(subsidyItems)){
		subsidyItems.each{ eachSubsidy->
			subsidyAmount = subsidyAmount+eachSubsidy.amount;
		}
		subsidyQty = qty;
		scheme = "MGP 10% Scheme";
	}
	invoiceDetailMap.put("subsidyAmount", (subsidyAmount*-1));
	invoiceDetailMap.put("subsidyQty", subsidyQty);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,eachInvoice.partyId));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	facilityDepo = delegator.findList("Facility",condition,null,null,null,false);
	if(UtilValidate.isNotEmpty(facilityDepo)){
		scheme = scheme + " (Depot)"
	} else{
		scheme = scheme + " (Non Depot)"
	}
	invoiceDetailMap.put("scheme", scheme);
	Debug.log("eachInvoice===================="+invoiceDetailMap);
	finalList.add(invoiceDetailMap);
}	

/*for(eachPartyId in partyIds){
	invoiceList = EntityUtil.filterByCondition(Invoice, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachPartyId));
	finalList = [];
	double TotalQuantiy = 0;
	double TotalAmount = 0;
	double totClaimAmt=0;
	double totElgibleAmt=0;
	totalTempMap = [:];
	for (eachInvoiceList in invoiceList) {
	tempMap=[:];
	tempMap.put("invoiceId", eachInvoiceList.invoiceId);
	billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , eachInvoiceList.invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
	if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
		invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
		invoiceSequence = invoiceSeqDetails.invoiceSequence;
		tempMap.put("billno", invoiceSequence);
	}else{
		tempMap.put("billno", eachInvoiceList.invoiceId);
	}
	tempMap.put("invoiceDate",UtilDateTime.toDateString(eachInvoiceList.invoiceDate,"dd/MM/yyyy"));
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN,UtilMisc.toList("VAT_SALE","CST_SALE","CST_SURCHARGE","VAT_SURCHARGE")));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	InvoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
	if(InvoiceItem){
		passNo = "";
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
		if(PartyIdentificationList){
			passNo = PartyIdentificationList[0].get("idValue");
		}
		if(passNo)
			partyPassMap.put(partyId, passNo);
		productId = InvoiceItem[0].productId;
		double invoiceAMT = 0;
		double invoiceQTY = 0;
		for(eachInvoiceItem in InvoiceItem){
			invoiceAMT = invoiceAMT+(eachInvoiceItem.itemValue);
			invoiceQTY = invoiceQTY+(eachInvoiceItem.quantity);
			TotalQuantiy = TotalQuantiy + (eachInvoiceItem.quantity);
			TotalAmount = TotalAmount + (eachInvoiceItem.itemValue);
		}
		Debug.log("invoiceAMT================="+invoiceAMT);
		tempMap.put("invoiceAmount", invoiceAMT);
	condListCat.clear();
	condListCat.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_EQUAL,null));
	condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategoryMember = delegator.findList("ProductCategoryAndMember", condList1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryId = ProductCategoryMember[0].productCategoryId;
	ProductCategory = delegator.findOne("ProductCategory",[productCategoryId : productCategoryId] , false);
	primaryParentCategoryId = ProductCategory.primaryParentCategoryId;
	percentage = reimbursmentPercentage.get(primaryParentCategoryId);
	if(!percentage)
		percentage = 2.5;
	double eligibleAMT = 0;
	maxAmt = (invoiceAMT*percentage)/100;
	tempMap.put("invoiceQTY", invoiceQTY);
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
	itemOrderId  = OrderItemBilling[0].orderId;
	  
	 conditionList = [];
	 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, itemOrderId));
	 conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["SUPPLIER","ON_BEHALF_OF","BILL_TO_CUSTOMER"]));
	 expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	 OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);
	  
	 partyId = "";
	 supplier = "";
	  
	 for (eachRole in OrderRoleList) {
		 if(eachRole.roleTypeId == "SUPPLIER")
		  supplier = eachRole.get("partyId");
		 if(eachRole.roleTypeId == "BILL_TO_CUSTOMER")
		  partyId = eachRole.get("partyId");
	 }
	 
	  
	 partyName = "";
	 if(partyId)
	  partyName = PartyHelper.getPartyName(delegator, partyId, false);
	  
	  
	 supplierName = "";
	 if(supplier)
	 supplierName = PartyHelper.getPartyName(delegator, supplier, false);
	  
	 //Debug.log("supplierName================="+supplierName);
	  
	 tempMap.put("supplierName", supplierName);
	 tempMap.put("partyName", partyName);
	 shipmentId = eachInvoiceList.shipmentId;
	 if(shipmentId){
		 shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
		   
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		 expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 ShipmentReimbursement = delegator.findList("ShipmentReimbursement", expr, null, null, null, false);
		 
		 double reimbursentAMT = 0;
		 if(ShipmentReimbursement){
			  
			 tempList = [];
			 for (eachReimbursement in ShipmentReimbursement) {
				  
				 tempMap1 = [:];
				  
				// reimbursentAMT = reimbursentAMT+Double.valueOf(eachReimbursement.receiptAmount);
				  
				 tempMap1.put("shipmentId", eachReimbursement.shipmentId)
				 if(eachReimbursement.claimId)
				 tempMap1.put("claimId", eachReimbursement.claimId)
				 else
				 tempMap1.put("claimId", "")
				 if(eachReimbursement.receiptNo)
				 tempMap1.put("receiptNo", eachReimbursement.receiptNo)
				 else
				 tempMap1.put("receiptNo", "")
				 if(eachReimbursement.receiptAmount)
				 tempMap1.put("receiptAmount", eachReimbursement.receiptAmount)
				 else
				 tempMap1.put("receiptAmount", "")
				 if(eachReimbursement.receiptDate)
				 tempMap1.put("receiptDate", eachReimbursement.receiptDate)
				 else
				 tempMap1.put("receiptDate", "")
				  
				 if(eachReimbursement.description)
				 tempMap1.put("description", eachReimbursement.description)
				 else
				 tempMap1.put("description", "")
				  
				  
				 tempList.add(tempMap1);
				  
			 }
			  
			  
			// shipmentReimbursementJson.put(shipmentId, tempList);
			  
			  
		 }
		  
		  
		 primaryOrderId = shipmentList.get("primaryOrderId");
		  
		 
		 //Debug.log("primaryOrderId=================="+primaryOrderId);
		 
		 DstAddr = delegator.findOne("OrderAttribute",["orderId":primaryOrderId,"attrName":"DST_ADDR"],false);
		 if(DstAddr){
			 destAddr=DstAddr.get("attrValue");
			 tempMap.put("destAddr", destAddr);
		 }else{
			 tempMap.put("destAddr", "");
		 }
		  
		 //Debug.log("destAddr=================="+destAddr);
		 
		  
		 lrNumber = shipmentList.get("lrNumber");
		  
		 if(lrNumber)
		 tempMap.put("lrNumber", lrNumber);
		 else
		 tempMap.put("lrNumber", "");
		  
		 deliveryChallanDate = shipmentList.get("deliveryChallanDate");
		  
		 if(deliveryChallanDate)
		 tempMap.put("lrDate",UtilDateTime.toDateString(deliveryChallanDate,"dd/MM/yyyy") );
		 else
		 tempMap.put("lrDate", "");
		  
		 //Debug.log("deliveryChallanDate=================="+deliveryChallanDate);
		  
		 double claimAmt = 0;
		 
		 double estimatedShipCost=0;
		 
		 if(shipmentList.get("estimatedShipCost")){
		 estimatedShipCost = shipmentList.get("estimatedShipCost");
		 reimbursentAMT = shipmentList.get("estimatedShipCost");
		 }
		 
		 if(reimbursentAMT){
		 claimAmt = Double.valueOf(reimbursentAMT);
		 totClaimAmt=totClaimAmt+claimAmt;
		 tempMap.put("claim", claimAmt);
		 } else{
		 tempMap.put("claim", "");
		 }
		  
		 //Debug.log("totClaimAmt=================="+totClaimAmt);
		 
		 if(maxAmt > reimbursentAMT){
			 totElgibleAmt=totElgibleAmt+reimbursentAMT;
			 tempMap.put("eligibleAMT", reimbursentAMT);
		 } else{
			 totElgibleAmt=totElgibleAmt+maxAmt;
			 tempMap.put("eligibleAMT", maxAmt);
		 }
 
		 
		 estimatedShipDate = shipmentList.get("estimatedShipDate");
		  
		 //Debug.log("estimatedShipDate=================="+estimatedShipDate);
		 
		 
		 if(estimatedShipDate)
		 tempMap.put("estimatedShipDate", UtilDateTime.toDateString(estimatedShipDate,"dd/MM/yyyy"));
		 else
		 tempMap.put("estimatedShipDate", "");
		  
		 carrierName = shipmentList.get("carrierName");
		 //Debug.log("carrierName================="+carrierName);
		  
		 if(carrierName)
		 tempMap.put("transporter", carrierName);
		 else
		 tempMap.put("transporter", "");
		  
		 supplierInvoiceId = ""
		 supplierInvoiceId = shipmentList.get("supplierInvoiceId");
		  
		 if(supplierInvoiceId)
		 tempMap.put("supplierInvoiceId", supplierInvoiceId);
		 else
		 tempMap.put("supplierInvoiceId", "");
		  
		  
		 supplierInvoiceDate = "";
		 supplierInvoiceDate = shipmentList.get("supplierInvoiceDate");
		  
		 if(supplierInvoiceDate)
		 tempMap.put("supplierInvoiceDate", UtilDateTime.toDateString(supplierInvoiceDate,"dd-MM-yyyy"));
		 else
		 tempMap.put("supplierInvoiceDate", "");
		  
		  
		  
		  
		  
		 //==============Address Details===================
		 if(partyId){
		 finalAddresList=[];
		 address1="";
		 address2="";
		 city="";
		 postalCode="";
		 panId="";
		 tanId="";
		  
		 partyPostalAddress = dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
		  
		 if(partyPostalAddress[0]){
			  
			if(partyPostalAddress.address1){
				address1=partyPostalAddress.address1;
			
				 
			tempMapAdd=[:];
			tempMapAdd.put("key1","Road / Street / Lane");
			tempMapAdd.put("key2",address1);
			finalAddresList.add(tempMapAdd);
			if(partyPostalAddress.address2){
				address2=partyPostalAddress.address2;
			}
			tempMapAdd=[:];
			tempMapAdd.put("key1","Area / Locality");
			tempMapAdd.put("key2",address2);
			finalAddresList.add(tempMapAdd);
			if(partyPostalAddress.city){
				 
				city=partyPostalAddress.city;
			}
			tempMapAdd=[:];
			tempMapAdd.put("key1","Town / District / City");
			tempMapAdd.put("key2",city);
			finalAddresList.add(tempMapAdd);
			 
			if(partyPostalAddress.postalCode){
				postalCode=partyPostalAddress.postalCode;
			}
			tempMapAdd=[:];
			tempMapAdd.put("key1","PIN Code");
			tempMapAdd.put("key2",postalCode);
			finalAddresList.add(tempMapAdd);
			 
			 }else{
				 contactMench = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
				 partyPostalAddress = contactMench.postalAddress;
				  
				 if(partyPostalAddress[0]){
					 address1=partyPostalAddress[0].address1;
				  
				 tempMapAdd1=[:];
				 tempMapAdd1.put("key1","Road / Street / Lane");
				 tempMapAdd1.put("key2",address1);
				 finalAddresList.add(tempMapAdd1);
				 if(partyPostalAddress[0].address2){
					 address2=partyPostalAddress[0].address2;
				 }
				 tempMapAdd1=[:];
				 tempMapAdd1.put("key1","Area / Locality");
				 tempMapAdd1.put("key2",address2);
				 finalAddresList.add(tempMapAdd1);
				 if(partyPostalAddress[0].city){
					 city=partyPostalAddress[0].city;
				 }
				 tempMapAdd1=[:];
				 tempMapAdd1.put("key1","Town / District / City");
				 tempMapAdd1.put("key2",city);
				 finalAddresList.add(tempMapAdd1);
				  
				 if(partyPostalAddress[0].postalCode){
					 postalCode=partyPostalAddress[0].postalCode;
				 }
				 tempMapAdd1=[:];
				 tempMapAdd1.put("key1","PIN Code");
				 tempMapAdd1.put("key2",postalCode);
				 finalAddresList.add(tempMapAdd1);
				 }
			 }
			 
		 }else{
				 contactMench = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
				 partyPostalAddress = contactMench.postalAddress;
				  
				  
				 if(partyPostalAddress[0]){
					 address1=partyPostalAddress[0].address1;
				  
				 tempMapAdd2=[:];
				 tempMapAdd2.put("key1","Road / Street / Lane");
				 tempMapAdd2.put("key2",address1);
				 finalAddresList.add(tempMapAdd2);
				 if(partyPostalAddress[0].address2){
					 address2=partyPostalAddress[0].address2;
				 }
				 tempMapAdd2=[:];
				 tempMapAdd2.put("key1","Area / Locality");
				 tempMapAdd2.put("key2",address2);
				 finalAddresList.add(tempMapAdd2);
				 if(partyPostalAddress[0].city){
					 city=partyPostalAddress[0].city;
				 }
				 tempMapAdd2=[:];
				 tempMapAdd2.put("key1","Town / District / City");
				 tempMapAdd2.put("key2",city);
				 finalAddresList.add(tempMapAdd2);
				  
				 if(partyPostalAddress[0].postalCode){
					 postalCode=partyPostalAddress[0].postalCode;
				 }
				 tempMapAdd2=[:];
				 tempMapAdd2.put("key1","PIN Code");
				 tempMapAdd2.put("key2",postalCode);
				 finalAddresList.add(tempMapAdd2);
				 }
		 }

		 if(finalAddresList)
		 	tempMap.put("finalAddresList", finalAddresList);
		 else
		 	tempMap.put("finalAddresList", "");
		 }
		 }
	}
	
  finalList.add(tempMap);
}
	 
	totalTempMap.put("TotalQuantiy", TotalQuantiy);
	totalTempMap.put("TotalAmount", TotalAmount);
	totalTempMap.put("totClaimAmt", totClaimAmt);
	totalTempMap.put("totElgibleAmt", totElgibleAmt);
	if(finalList){
		finalMap.put(eachPartyId, finalList);
		partyWiseTotalsMap.put(eachPartyId, totalTempMap);
	}
}
*/

context.finalMap = finalMap;
context.partyWiseTotalsMap = partyWiseTotalsMap;
context.finalList = finalList;

Debug.log("======d========"+daystart);
Debug.log("======e========"+dayend);

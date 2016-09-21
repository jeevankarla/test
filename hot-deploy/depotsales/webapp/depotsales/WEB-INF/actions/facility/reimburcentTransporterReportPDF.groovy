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
dayend = null;
daystart = null;

Timestamp fromDate;
Timestamp thruDate;

DateList=[];
DateMap = [:];
partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
partyId=parameters.partyId;
state=parameters.state;
productCategory=parameters.productCategory;

DateMap.put("partyfromDate", partyfromDate);
DateMap.put("partythruDate", partythruDate);



DateList.add(DateMap);
context.DateList=DateList;
branchId = parameters.branchId;

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



//////Debug.log("branchList=================="+branchList);
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
	
	condListCat.clear();
	condListCat.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategoryMember = delegator.findList("ProductCategoryMember", condList1,UtilMisc.toSet("productId"), null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
	
}
//////Debug.log("productIds================"+productIds.size());
  
daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
  
	try {
		//daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
		daystart = UtilDateTime.getDayStart(fromDate);
		 } catch (ParseException e) {
			 //////////Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			 }
   
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   
   try {
	 //  dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
	   
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime());
	   
	   dayend = UtilDateTime.getDayEnd(thruDate);
	   
   } catch (ParseException e) {
	   //////////Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
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
//condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, "11821"));

if(UtilValidate.isNotEmpty(daystart)){
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
}

condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));

if(productIds)
condList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));

/*if(branchList)
condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
if(partyId)
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
if(!partyId && branchpartyIdsList)
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchpartyIdsList));
*/
if(partyId){
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
}else if(!partyId && branchBasedWeaversList){
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
}

condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));


cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

////////Debug.log("cond================="+cond);


fieldsToSelect = ["invoiceId"] as Set;

invoice = delegator.find("InvoiceAndItem", cond, null, fieldsToSelect, null, null);
//////////////Debug.log("invoice========================="+invoice);
invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);




Debug.log("invoiceIds======2222==========="+invoiceIds.size());


conditionList1 = [];
conditionList1.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
conditionList1.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
cond = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);


orderIds=EntityUtil.getFieldListFromEntityList(OrderItemBilling, "orderId", true);


Debug.log("orderIds==============="+orderIds.size());


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



//Debug.log("partyIds================="+partyIds.size());

partyPassMap= [:];
finalMap = [:];

partyWiseTotalsMap = [:];


dupliInvoices = []as Set;
for (eachPartyId in partyIds) {
	
	
	invoiceList = EntityUtil.filterByCondition(Invoice, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachPartyId));
	
	finalList = [];
	
	
	
	double TotalQuantiy = 0;
	
	double TotalAmount = 0;
	
	totalTempMap = [:];
	
	for (eachInvoiceList in invoiceList) {
	//eachInvoiceList = delegator.findOne("Invoice",[invoiceId : eachInvoice.invoiceId] , false);
	
	tempMap=[:];
	
	tempMap.put("invoiceId", eachInvoiceList.invoiceId);
	
	
	billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , eachInvoiceList.invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
	if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
		invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
		invoiceSequence = invoiceSeqDetails.invoiceSequence;
		
		//////Debug.log("invoiceSequence======2121==========="+invoiceSequence);
		
		tempMap.put("billno", invoiceSequence);
	}else{
		tempMap.put("billno", eachInvoiceList.invoiceId);
	}
	
	 tempMap.put("invoiceDate",UtilDateTime.toDateString(eachInvoiceList.invoiceDate,"dd/MM/yyyy"));
		   
	 ////////Debug.log("eachInvoiceList.invoiceId================="+eachInvoiceList.invoiceId);
	 
	 
   
	condList.clear();
	
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
	//condList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	InvoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
	  
	
	//Debug.log("InvoiceItem================="+InvoiceItem.size());
	
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
	for (eachInvoiceItem in InvoiceItem) {
		
		invoiceAMT = invoiceAMT+(eachInvoiceItem.itemValue);
		if(eachInvoiceItem.invoiceItemTypeId == "INV_FPROD_ITEM")
		invoiceQTY = invoiceQTY+(eachInvoiceItem.quantity);
		
		if(eachInvoiceItem.invoiceItemTypeId == "INV_FPROD_ITEM")
		TotalQuantiy = TotalQuantiy + (eachInvoiceItem.quantity);
		
		TotalAmount = TotalAmount + (eachInvoiceItem.itemValue);
		
	}
	
	
	
	
	tempMap.put("invoiceAmount", invoiceAMT);
	
	//Debug.log("productId================="+productId);
	
	
	condListCat.clear();
	condListCat.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_EQUAL,null));
	condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategoryMember = delegator.findList("ProductCategoryAndMember", condList1,UtilMisc.toSet("productCategoryId"), null, null, false);

	
	//Debug.log("ProductCategoryMember================="+ProductCategoryMember);
	
	productCategoryId = ProductCategoryMember[0].productCategoryId;
	
	//Debug.log("productCategoryId================="+productCategoryId);
	
	
	ProductCategory = delegator.findOne("ProductCategory",[productCategoryId : productCategoryId] , false);
	
	primaryParentCategoryId = ProductCategory.primaryParentCategoryId;
	
	percentage = reimbursmentPercentage.get(primaryParentCategoryId);
	
	if(!percentage)
	percentage = 2.5;
	
	
	//Debug.log("percentage==============="+percentage);
	
	
	double eligibleAMT = 0;
	
	maxAmt = (invoiceAMT*percentage)/100;
	
	//Debug.log("eligibleAMT==============="+eligibleAMT);
	
	
	tempMap.put("invoiceQTY", invoiceQTY);
	
	
	////////Debug.log("invoiceAMT================="+invoiceAMT);
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	//conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
   
	////////Debug.log("OrderItemBilling======================"+OrderItemBilling);
		   
	 itemOrderId  = OrderItemBilling[0].orderId;
	 
	 //////Debug.log("itemOrderId================="+itemOrderId);
	 
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
	 
	 //////Debug.log("supplierName================="+supplierName);
	 
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
				 
				 reimbursentAMT = reimbursentAMT+Double.valueOf(eachReimbursement.receiptAmount);
				 
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
		 
		 DstAddr = delegator.findOne("OrderAttribute",["orderId":primaryOrderId,"attrName":"DST_ADDR"],false);
		 if(DstAddr){
			 destAddr=DstAddr.get("attrValue");
			 tempMap.put("destAddr", destAddr);
		 }else{
			 tempMap.put("destAddr", "");
		 }
		 
		 
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
		 
		 
		 double claimAmt = 0;
		 estimatedShipCost = shipmentList.get("estimatedShipCost");
					 
		 if(reimbursentAMT){
		 claimAmt = Double.valueOf(reimbursentAMT);
		 tempMap.put("claim", claimAmt);
		 } else{
		 tempMap.put("claim", "");
		 }
		 
		 if(maxAmt > reimbursentAMT)
		 tempMap.put("eligibleAMT", reimbursentAMT);
		 else
		 tempMap.put("eligibleAMT", maxAmt);
	 
		 estimatedShipDate = shipmentList.get("estimatedShipDate");
		 
		 if(estimatedShipDate)
		 tempMap.put("estimatedShipDate", UtilDateTime.toDateString(estimatedShipDate,"dd/MM/yyyy"));
		 else
		 tempMap.put("estimatedShipDate", "");
		 
		 carrierName = shipmentList.get("carrierName");
		 //////Debug.log("carrierName================="+carrierName);
		 
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
		 
		 //context.finalAddresList = finalAddresList;
		 //============================================================
		 
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
	
	if(finalList){
	finalMap.put(eachPartyId, finalList);
	partyWiseTotalsMap.put(eachPartyId, totalTempMap);
	}
	
}

context.finalMap = finalMap;

context.partyWiseTotalsMap = partyWiseTotalsMap;


//Debug.log("finalMap==============="+finalMap);




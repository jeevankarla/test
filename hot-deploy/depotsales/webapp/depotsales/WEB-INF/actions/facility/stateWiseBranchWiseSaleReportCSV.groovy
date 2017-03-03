import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.jdbc.JdbcValueHandler.BigDecimalJdbcValueHandler;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
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


productIds = [];

productCategoryIds = [];

condListCat = [];

//if(!partyId){
	if(productCategory == "ALL"){
		productCategoris = delegator.findList("ProductCategory", EntityCondition.makeCondition("productCategoryTypeId" ,EntityOperator.EQUALS,"NATURAL_FIBERS"), null, null, null ,false);		
		productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoris, "productCategoryId", true);
		
		productPrimaryCategories = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.IN,productCategoryIds), null, null, null ,false);
		productCategoryIds=EntityUtil.getFieldListFromEntityList(productPrimaryCategories, "productCategoryId", true);
	}else if(productCategory == "OTHER"){
		productCategoris = delegator.findList("ProductCategory", EntityCondition.makeCondition([EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "NATURAL_FIBERS"), EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_IN, UtilMisc.toList("COTTON","SILK"))], EntityOperator.AND), UtilMisc.toSet("productCategoryId"), null, null ,false);
		productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoris, "productCategoryId", true);
		
		productPrimaryCategories = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.IN,productCategoryIds), null, null, null ,false);
		productCategoryIds=EntityUtil.getFieldListFromEntityList(productPrimaryCategories, "productCategoryId", true);
	}else{
		productCategoris = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.EQUALS,productCategory), UtilMisc.toSet("productCategoryId","primaryParentCategoryId"), null, null ,false);
		productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoris, "productCategoryId", true);
		}
	
	condListCat.clear();
	condListCat.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategoryMember = delegator.findList("ProductCategoryMember", condList1,UtilMisc.toSet("productId"), null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
	
//}
daystart = null;
dayend = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (UtilValidate.isNotEmpty(parameters.partyfromDate)) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime()));
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
context.errorMessage = "Cannot parse date string: " + e;
	return;
}

daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);
  
fromDateForCSV=UtilDateTime.toDateString(daystart, "dd/MM/yyyy");
thruDateForCSV=UtilDateTime.toDateString(dayend, "dd/MM/yyyy");

context.fromDateForCSV=fromDateForCSV;
context.thruDateForCSV=thruDateForCSV;
/*daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
	
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
		//Debug.log("fromDate================"+fromDate);
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
}*/


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
if(!partyId && branchBasedWeaversList)
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));

condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));


cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

////////Debug.log("cond================="+cond);


fieldsToSelect = ["invoiceId"] as Set;

invoice = delegator.find("InvoiceAndItem", cond, null, fieldsToSelect, null, null);
////////////////Debug.log("invoice========================="+invoice);
invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);


condList = [];
condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
Invoice = delegator.findList("Invoice", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);


partyIds=EntityUtil.getFieldListFromEntityList(Invoice, "partyId", true);



finalList = [];
dupliInvoices = []as Set;
totInvQTY = 0;
totInvAMT = 0;
tempTotMap=[:];

stylesMap=[:];
if(branchId){
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", BOAddress);
	stylesMap.put("mainHeader3", "STATE WISE BRANCH WISE SALES REPORT");
	stylesMap.put("mainHeader4", "From "+ fromDateForCSV +" to "+thruDateForCSV);
}
else{
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", "STATE WISE BRANCH WISE SALES REPORT");
	stylesMap.put("mainHeader3", "From "+ fromDateForCSV +" to "+thruDateForCSV);
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
headingMap.put("billno","Sale Invoice No")
headingMap.put("invoiceDate","Invoice Date")
headingMap.put("productName","Product Name")
headingMap.put("invoiceQTY","Quantity(kg)")
headingMap.put("invoicprice","Invoice Price")
headingMap.put("invoiceAmount","Amount")
headingMap.put("supplierName","Name Of Supplier")
headingMap.put("destAddr","Destination")
headingMap.put("transporter","Transporter")
headingMap.put("lrNumber","Lr Number")
headingMap.put("lrDate","Lr Date")

finalList.add(stylesMap);
finalList.add(headingMap);


for (eachInvoiceList in Invoice) {
	
	
		
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
	//condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
//condList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	InvoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
	
	BigDecimal invoiceAMT = 0;
	BigDecimal invoiceQTY = 0;
	BigDecimal invoicprice=0;
	
	
		
	if(InvoiceItem){

		productId = InvoiceItem[0].productId;
	
	//invoicprice=invoiceAMT.divide(invoiceQTY);
	//invoiceAMT.divide(invoiceQTY);
	for (eachInvoiceItem in InvoiceItem) {
		
		
		
		if(UtilValidate.isNotEmpty(eachInvoiceItem.itemValue)){
			invoiceAMT = invoiceAMT+(eachInvoiceItem.itemValue);
			totInvAMT=totInvAMT+eachInvoiceItem.itemValue;
			
		}
		
		if(UtilValidate.isNotEmpty(eachInvoiceItem.quantity)){
			invoiceQTY = invoiceQTY+(eachInvoiceItem.quantity);
			//Debug.log("invoiceQTY================="+invoiceQTY);
			totInvQTY=totInvQTY+eachInvoiceItem.quantity;
			
		}
		
	}
		product = delegator.findList("OrderHeaderItemAndRoles",EntityCondition.makeCondition("productId", EntityOperator.EQUALS , InvoiceItem[0].productId)  , null, null, null, false );
		productdetails=EntityUtil.getFirst(product);
			if(UtilValidate.isNotEmpty(productdetails)){
				productName=productdetails.itemDescription;
			}
	  }
	tempMap.put("invoiceAmount", invoiceAMT);
	tempMap.put("productName",productName);
	tempMap.put("invoiceQTY", invoiceQTY);
	if(invoiceQTY.compareTo(BigDecimal.ZERO)==1){
		invoicprice=invoiceAMT.divide(invoiceQTY,4,BigDecimal.ROUND_HALF_UP);
	}
	tempMap.put("invoicprice", invoicprice);
	//context.invoicprice=invoicprice;
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	//conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
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
			 
			 
			 //shipmentReimbursementJson.put(shipmentId, tempList);
			 
			 
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
		 tempMap.put("supplierInvoiceDate", UtilDateTime.toDateString(supplierInvoiceDate,"dd/MM/yyyy"));
		 else
		 tempMap.put("supplierInvoiceDate", "");
		 
		 }
	
	 finalList.add(tempMap);
}
tempTotMap.put("billno", "TOTAL");
tempTotMap.put("invoiceQTY", totInvQTY);
tempTotMap.put("invoiceAmount", totInvAMT);
finalList.add(tempTotMap);
context.finalList = finalList;

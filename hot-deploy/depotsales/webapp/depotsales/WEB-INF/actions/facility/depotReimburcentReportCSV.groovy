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



SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
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
branchIdForAdd="";
branchList = [];

conditionList = [];
if(branchId){
conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));

PartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("partyIdTo"), null, null, false);

branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
if(!branchList){
	conditionList.clear()
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, branchId));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	
	PartyRelationship1 = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("partyIdFrom"), null, null, false);
	branchDetails = EntityUtil.getFirst(PartyRelationship1);
	branchIdForAdd=branchDetails.partyIdFrom;
}
else{
	branchIdForAdd=branchId;
}
if(!branchList)
branchList.add(branchId);
}
branchBasedWeaversList = [];
conditionList.clear()
if(branchId){
conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));

PartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("partyIdTo"), null, null, false);
branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);

}
productIds = [];
productCategoryIds = [];
conditionList.clear()

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
	
	conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	ProductCategoryMember = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("productId"), null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
	
//}
  
daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
  
	try {
		//daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
		daystart = UtilDateTime.getDayStart(fromDate);
		 } catch (ParseException e) {
			 //////Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			 }
   
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   
   try {
	 //  dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
	   
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime());
	   
	   dayend = UtilDateTime.getDayEnd(thruDate);
	   
   } catch (ParseException e) {
	   //////Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		}
}
context.daystart=daystart
context.dayend=dayend
  
daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);
fromDateForCSV=UtilDateTime.toDateString(daystart, "dd/MM/yyyy");
thruDateForCSV=UtilDateTime.toDateString(dayend, "dd/MM/yyyy");
context.fromDateForCSV=fromDateForCSV;
context.thruDateForCSV=thruDateForCSV;

branchContext=[:];
branchContext.put("branchId",branchIdForAdd);
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
branchpartyIdsList = [];
if(state && !partyId){
	conditionList.cleaar()
	if(branchBasedWeaversList)
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
	conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
	if(state == "IN-TS")
	conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, "IN-TNG"));
	else if(state)
	conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
	PartyContactDetailByPurposeList = delegator.find("PartyContactDetailByPurpose", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toSet("partyId"), null, null);
	branchBasedWeaversList = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByPurposeList, "partyId", true);
	if(UtilValidate.isEmpty(branchBasedWeaversList)){
	 return "Selected Worng State";
	}
}
isDepotPartyIds = [];
conditionList.clear()
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
if(branchBasedWeaversList)
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN,branchBasedWeaversList));
FacilityList = delegator.find("Facility", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toSet("ownerPartyId"), null, null);
branchBasedWeaversList = EntityUtil.getFieldListFromEntityListIterator(FacilityList, "ownerPartyId", true);
conditionList.clear()
if(UtilValidate.isNotEmpty(daystart)){
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
}
conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
if(productIds)
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));

if(!partyId && branchBasedWeaversList)
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));

conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));
fieldsToSelect = ["invoiceId"] as Set;
invoice = delegator.find("InvoiceAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, fieldsToSelect, null, null);
invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);
conditionList.clear()
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
orderIds=EntityUtil.getFieldListFromEntityList(OrderItemBilling, "orderId", true);
conditionList.clear()
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
conditionList.add(EntityCondition.makeCondition("attrValue", EntityOperator.IN, ["MGPS_10Pecent","MGPS"]));
orderAttr = delegator.findList("OrderAttribute",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
orderIds=EntityUtil.getFieldListFromEntityList(orderAttr, "orderId", true);
OrderItemBillingFilter = EntityUtil.filterByCondition(OrderItemBilling, EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
invoiceIds=EntityUtil.getFieldListFromEntityList(OrderItemBillingFilter, "invoiceId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
Invoice = delegator.findList("Invoice", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
customersList=EntityUtil.getFieldListFromEntityList(Invoice, "partyId", true);


finalList = [];

if(UtilValidate.isNotEmpty(parameters.header)&&parameters.header.equals("required")){
stylesMap=[:];
if(branchId){
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", BOAddress);
	stylesMap.put("mainHeader3", "DEPOT REIMBURSMENT REPORT");
	stylesMap.put("mainHeader4", "From "+ fromDateForCSV +" to "+thruDateForCSV);
}
else{
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", "DEPOT REIMBURSMENT REPORT");
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
headingMap.put("billno" ,"Sale Inv No")
headingMap.put("invoiceDate" ,"Invoice Date")
headingMap.put("invoiceQTY" ,"Quantity")
headingMap.put("invoiceAmount" ,"Amount")
headingMap.put("supplierName" ,"Name Of Supplier")
headingMap.put("partyName" ,"Party Name")
headingMap.put("destAddr" ,"Destination")
headingMap.put("transporter" ,"Transporter")
headingMap.put("depotCharges" ,"Depot Charges")
headingMap.put("lrNumber" ,"LR Number")
headingMap.put("lrDate" ,"LR Date")

finalList.add(stylesMap);
finalList.add(headingMap);
}
allCustomersMap=[:]
destinationMap=[:];
reimbursementMap=[:];
for(eachCustomer in customersList){
	eachCustomerList=[]
	totalsMap=[:];
	totQty=0
	totAmt=0
	sNo=1
	totdepotChargs=0
	destAddr=""
	Invoice1 = EntityUtil.filterByCondition(Invoice, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachCustomer));
	for (eachInvoiceList in Invoice1) {
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
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
		 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
		 InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		 if(InvoiceItem){
		 double invoiceAMT = 0;
		 double invoiceQTY = 0;
		 for (eachInvoiceItem in InvoiceItem) {
			invoiceAMT = invoiceAMT+(eachInvoiceItem.itemValue);
			invoiceQTY = invoiceQTY+(eachInvoiceItem.quantity);
			totQty=totQty+eachInvoiceItem.quantity
			totAmt=totAmt+eachInvoiceItem.itemValue
		}
		tempMap.put("invoiceAmount", invoiceAMT);
		double maxAmt = 0;
		maxAmt = (invoiceAMT*2)/100;
		tempMap.put("invoiceQTY", invoiceQTY);
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		itemOrderId  = OrderItemBilling[0].orderId;
		 conditionList.clear()
		 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, itemOrderId));
		 conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["SUPPLIER","ON_BEHALF_OF","BILL_TO_CUSTOMER"]));
		 OrderRoleList = delegator.findList("OrderRole", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		 partyId = "";
		 supplier = "";
		 for (eachRole in OrderRoleList) {
			 if(eachRole.roleTypeId == "SUPPLIER")
			  supplier = eachRole.get("partyId");
			 if(eachRole.roleTypeId == "BILL_TO_CUSTOMER")
			  partyId = eachRole.get("partyId");
		 }
		 partyName = "";
		 if(partyId) eachInvoiceList
		 partyName = PartyHelper.getPartyName(delegator, partyId, false);
		 supplierName = "";
		 if(supplier)
		 supplierName = PartyHelper.getPartyName(delegator, supplier, false);
		 tempMap.put("supplierName", supplierName);
		 tempMap.put("partyName", partyName);
		 tempMap.put("depotCharges", maxAmt);
		 totdepotChargs=totdepotChargs+maxAmt;
		 shipmentId = eachInvoiceList.shipmentId;
		 if(shipmentId){
			 shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
			 primaryOrderId = shipmentList.get("primaryOrderId");
			 DstAddr = delegator.findOne("OrderAttribute",["orderId":primaryOrderId,"attrName":"DST_ADDR"],false);
			 if(DstAddr){
				 destAddr=DstAddr.get("attrValue");
				 tempMap.put("destAddr", destAddr);
			 }else{
				 tempMap.put("destAddr", "");
			 }
			
			 if(estimatedShipDate)
			 tempMap.put("estimatedShipDate", UtilDateTime.toDateString(estimatedShipDate,"dd/MM/yyyy"));
			 else
			 tempMap.put("estimatedShipDate", "");
			 carrierName = shipmentList.get("carrierName");
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
			 }
		 	stateName= dispatcher.runSync("getCustomerState", [partyId:eachCustomer, userLogin: userLogin]);
			tempMap.put("stateName",stateName);
			tempMap.put("sNo", sNo);
	 		tempMap.put("ob", "nill");
			tempMap.put("obValue", "nill");
			tempMap.put("otherQty", "nill");
			tempMap.put("otherValue", "nill");
			tempMap.put("Mill", "nill");
			tempMap.put("cbQty", "nill");
			tempMap.put("cbValue", "nill");
			sNo=sNo+1
		}
		 eachCustomerList.add(tempMap);
	}
	totalsMap.put("stateName","TOTAL");
	totalsMap.put("invoiceQTY",totQty);
	totalsMap.put("invoiceAmount",totAmt);
	totalsMap.put("depotCharges",totdepotChargs);
	eachCustomerList.add(totalsMap);
	allCustomersMap.put(eachCustomer, eachCustomerList)
	destinationMap.put(eachCustomer,destAddr)
	
}
context.allCustomersMap=allCustomersMap
context.customersList=customersList
context.destinationMap=destinationMap 

context.finalList = finalList;










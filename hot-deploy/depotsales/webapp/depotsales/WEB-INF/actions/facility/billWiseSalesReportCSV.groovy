import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
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

SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
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
/*if(branchId){
	branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
	branchName = branch.get("groupName");
	DateMap.put("branchName", branchName);
}*/
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
	if(productCategory != "OTHER" && productCategory != "ALL"){
		condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS, productCategory));
		condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
		ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
		productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	}else if(productCategory == "OTHER"){
		condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_IN, ["SILK","COTTON"]));
		condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
		ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
		productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	}else{
	condListCat.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "NATURAL_FIBERS"));
	condition1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategory = delegator.findList("ProductCategory", condition1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	ProductCategory = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.IN,productCategoryIds),UtilMisc.toSet("productCategoryId"), null, null, false);
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

daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);
  
fromDateForCSV=UtilDateTime.toDateString(daystart, "dd/MM/yyyy");
thruDateForCSV=UtilDateTime.toDateString(dayend, "dd/MM/yyyy");

context.fromDateForCSV=fromDateForCSV;
context.thruDateForCSV=thruDateForCSV;
   
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
//finalMap = [:];
partyWiseTotalsMap = [:];
totQty=0;
totAmount=0;
totsubquant=0;
totsubAmount=0;
finalList = [];
tempTotMap=[:];

stylesMap=[:];
if(branchId){
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", BOAddress);
	stylesMap.put("mainHeader3", "BILL WISE SALE REPORT");
	stylesMap.put("mainHeader4", "From "+ fromDateForCSV +" to "+thruDateForCSV);
}
else{
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", "BILL WISE SALE REPORT");
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
headingMap.put("invoiceDate", "Date");
headingMap.put("billno", "Bill No");
headingMap.put("partyName", "Party Name");
headingMap.put("partyType", "Type");
headingMap.put("district", "District");
headingMap.put("branch", "Branch");
headingMap.put("state", "State");
headingMap.put("ro", "Ro");
headingMap.put("qty", "Qty");
headingMap.put("amount", "Amount");
headingMap.put("VatOrOtherAmt", "Vat/Other Amt");
headingMap.put("subsidyQty", "Subsidy Qty");
headingMap.put("subsidyAmount", "10% Subsidy");
headingMap.put("scheme", "Scheme");

finalList.add(stylesMap);
finalList.add(headingMap);

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
	branchName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, eachInvoice.costCenterId, false);
	invoiceDetailMap.put("partyName",custPartyName);
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachInvoice.partyId));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	partyClassification = delegator.findList("PartyClassification",condition,null,null,null,false);
	partyType = "";
	if(UtilValidate.isNotEmpty(partyClassification)){
		PartyClassificationDetails = EntityUtil.getFirst(partyClassification);
		partyClassificationGroupIdList = delegator.findOne("PartyClassificationGroup",UtilMisc.toMap("partyClassificationGroupId", PartyClassificationDetails.get("partyClassificationGroupId")), false);
		if(UtilValidate.isNotEmpty(partyClassificationGroupIdList)){
			partyType = partyClassificationGroupIdList.get("description");
		}
	
		}
	
	invoiceDetailMap.put("amount", eachInvoice.invoiceGrandTotal);
	totAmount+= eachInvoice.invoiceGrandTotal;
	invoiceDetailMap.put("partyType",partyType);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoice.invoiceId));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	invoiceItems = delegator.findList("InvoiceItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
	qty = 0;
	if(UtilValidate.isNotEmpty(invoiceItems)){
		invoiceItems.each{ eachInvoice->
			qty = qty + eachInvoice.quantity;
		}
		totQty+=qty;
	}
	
	invoiceDetailMap.put("qty", qty);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoice.invoiceId));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,UtilMisc.toList("TEN_PERCENT_SUBSIDY")));
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
		totsubquant+=subsidyQty;
	}
	invoiceDetailMap.put("subsidyAmount", (subsidyAmount*-1));
	totsubAmount+=(subsidyAmount*-1);
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
	partyId = eachInvoice.partyId;
	state = ""
	district = "";
	//==============================================================================================================
	contactMechesDetails = [];
	conditionListAddress = [];
	conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
	conditionListAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
	List<String> orderBy = UtilMisc.toList("-contactMechId");
	contactMech = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy, null, false);
	if(contactMech){
		contactMechesDetails = contactMech;
	}
	else{
		conditionListAddress.clear();
		conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		conditionListAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
		List<String> orderBy2 = UtilMisc.toList("-contactMechId");
		contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy2, null, false);
	}
	shipingAdd = [:];
	address1="";
	address2="";
	state="";
	city="";
	postalCode="";
	if(contactMechesDetails){
		contactMec=contactMechesDetails.getFirst();
		if(contactMec){
			partyPostalAddress=contactMec;
			if(partyPostalAddress){
				if(partyPostalAddress.get("address1")){
					address1=partyPostalAddress.get("address1");
				}
				if(partyPostalAddress.get("address2")){
					address2=partyPostalAddress.get("address2");
				}
				if(partyPostalAddress.get("city")){
					city=partyPostalAddress.get("city");
				}
				if(partyPostalAddress.get("stateGeoName")){
					state=partyPostalAddress.get("stateGeoName");
				}
				if(partyPostalAddress.get("postalCode")){
					postalCode=partyPostalAddress.get("postalCode");
				}
				state = state;
				dsitrict = city;
			}
		}
	}
	invoiceDetailMap.put("district",city);
	invoiceDetailMap.put("state",state);
	//==============================================================================================================
	invoiceDetailMap.put("branch",branchName);
	
	roName = "";
	condListb = [];
	condListb.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, branchId));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdFrom","partyIdTo"), null, null, false);
	
	if(UtilValidate.isNotEmpty(PartyRelationship)){
		roId = EntityUtil.getFirst(PartyRelationship).get("partyIdFrom");
		
		if(roId != "Company")
		roId = EntityUtil.getFirst(PartyRelationship).get("partyIdFrom");
		else
		roId = EntityUtil.getFirst(PartyRelationship).get("partyIdTo");
		
		roDetails = delegator.findOne("PartyGroup",[partyId : roId] , false);
		roName = roDetails.groupName;
	}
	
	invoiceDetailMap.put("ro",roName);
	invoiceDetailMap.put("VatOrOtherAmt","Vat/Other Amt");

	finalList.add(invoiceDetailMap);
}
	
	tempTotMap.put("invoiceDate", "TOTAL");
	tempTotMap.put("qty", totQty);
	tempTotMap.put("amount", totAmount);
	tempTotMap.put("subsidyQty", totsubquant);
	tempTotMap.put("subsidyAmount", totsubAmount);
	finalList.add(tempTotMap)
//context.finalMap = finalMap;
context.partyWiseTotalsMap = partyWiseTotalsMap;
context.finalList = finalList;
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
import org.ofbiz.accounting.invoice.InvoiceWorker;
import java.math.RoundingMode;

SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");

Timestamp fromDate;
Timestamp thruDate;

Timestamp fStMnthfromDate;
Timestamp fStMnththruDate;

Timestamp sndMnthfromDate;
Timestamp sndMnththruDate;

Timestamp thrdMnthfromDate;
Timestamp thrdMnththruDate;

Timestamp frthMnthfromDate;
Timestamp frthMnththruDate;

Timestamp fromfifthMnthfromDate;
Timestamp fromfifthMnththruDate;

Timestamp firstyearfromDate;
Timestamp firstyearthruDate;

Timestamp secondyearfromDate;
Timestamp secondyearthruDate;

dateStr=parameters.AWIORDate;
reportType=parameters.reportType;
days=parameters.days;
Agencyreport=parameters.Agencyreport;
rounding = RoundingMode.HALF_UP;

if(UtilValidate.isNotEmpty(dateStr)){
	if(days == "30days"){
	thruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
	fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-1460);
	
	fStMnththruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
	fStMnthfromDate = UtilDateTime.addDaysToTimestamp(fStMnththruDate,-30);
	
	sndMnththruDate = UtilDateTime.addDaysToTimestamp(fStMnthfromDate,-1);
	sndMnthfromDate = UtilDateTime.addDaysToTimestamp(sndMnththruDate,-30);
	
	thrdMnththruDate = UtilDateTime.addDaysToTimestamp(sndMnthfromDate,-1);
	thrdMnthfromDate = UtilDateTime.addDaysToTimestamp(thrdMnththruDate,-30);
	
	frthMnththruDate = UtilDateTime.addDaysToTimestamp(thrdMnthfromDate,-1);
	frthMnthfromDate = UtilDateTime.addDaysToTimestamp(frthMnththruDate,-90);
	
	fromfifthMnththruDate = UtilDateTime.addDaysToTimestamp(frthMnthfromDate,-1);
	fromfifthMnthfromDate = UtilDateTime.addDaysToTimestamp(fromfifthMnththruDate,-181);
	
	firstyearthruDate = UtilDateTime.addDaysToTimestamp(fromfifthMnthfromDate,-1);
	firstyearfromDate = UtilDateTime.addDaysToTimestamp(firstyearthruDate,-364);
	
	secondyearthruDate = UtilDateTime.addDaysToTimestamp(firstyearfromDate,-1);
	secondyearfromDate = UtilDateTime.addDaysToTimestamp(secondyearthruDate,-364);
	}
	else{
		thruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
		fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-365);
		
		fStMnththruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
		fStMnthfromDate = UtilDateTime.addDaysToTimestamp(fStMnththruDate,-45);
		
		sndMnththruDate = UtilDateTime.addDaysToTimestamp(fStMnthfromDate,-1);
		sndMnthfromDate = UtilDateTime.addDaysToTimestamp(sndMnththruDate,-45);
		
		thrdMnththruDate = UtilDateTime.addDaysToTimestamp(sndMnthfromDate,-1);
		thrdMnthfromDate = UtilDateTime.addDaysToTimestamp(thrdMnththruDate,-90);
		
		frthMnththruDate = UtilDateTime.addDaysToTimestamp(thrdMnthfromDate,-1);
		frthMnthfromDate = UtilDateTime.addDaysToTimestamp(frthMnththruDate,-181);
		
		fromfifthMnththruDate = UtilDateTime.addDaysToTimestamp(frthMnthfromDate,-1);
		fromfifthMnthfromDate = UtilDateTime.addDaysToTimestamp(fromfifthMnththruDate,-364);
		
		firstyearthruDate = UtilDateTime.addDaysToTimestamp(fromfifthMnthfromDate,-1);
		firstyearfromDate = UtilDateTime.addDaysToTimestamp(firstyearthruDate,-364);
		
	}
}

/*BOAddress="";
BOEmail="";
boHeaderMap=[:];
condList=[];
condList.add(EntityCondition.makeCondition("propertyName", EntityOperator.LIKE,"HO%"));
condList.add(EntityCondition.makeCondition("propertyTypeEnumId", EntityOperator.EQUALS, "COMPANY_HEADER"));
condList.add(EntityCondition.makeCondition("propertyValue", EntityOperator.EQUALS, "Y"));
EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
tenantConfigCheck = delegator.findList("TenantConfiguration",cond, null , null, null, false);
if (UtilValidate.isNotEmpty(tenantConfigCheck)) {
	for (int i = 0; i < tenantConfigCheck.size(); i++) {						
		GenericValue eachProductList = (GenericValue)tenantConfigCheck.get(i);					
		String header=(String)eachProductList.get("description");
		boHeaderMap.put("header"+i,header);
	}
	
}
BOAddress=boHeaderMap["header0"]+boHeaderMap["header1"];
BOEmail=boHeaderMap["header2"];
context.BOAddress=BOAddress;
context.BOEmail=BOEmail;*/

branchId = parameters.branchId;
branchIdForAdd="";
branchList = [];
condListb = [];
if(branchId){
condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);

branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
if(!branchList){
	condListb2 = [];
	condListb2.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, branchId));
	condListb2.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb2.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	cond = EntityCondition.makeCondition(condListb2, EntityOperator.AND);
	
	PartyRelationship1 = delegator.findList("PartyRelationship", cond,UtilMisc.toSet("partyIdFrom"), null, null, false);
	if(PartyRelationship1){
	branchDetails = EntityUtil.getFirst(PartyRelationship1);
	branchIdForAdd=branchDetails.partyIdFrom;
	}
}
else{
	if(branchId){
	branchIdForAdd=branchId;
	}
}
if(!branchList)
branchList.add(branchId);
}
BOAddress="";
if(branchIdForAdd){
branchContextForADD=[:];
branchContextForADD.put("branchId",branchIdForAdd);
try{
	resultCtx = dispatcher.runSync("getBoHeader", branchContextForADD);
	if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in BO Header ", module);
		return ServiceUtil.returnError("Problem in fetching financial year ");
	}
	if(resultCtx.get("boHeaderMap")){
		boHeaderMap=resultCtx.get("boHeaderMap");
		
		if(boHeaderMap.get("header0")){
			BOAddress=boHeaderMap.get("header0");
		}
	}
}catch(GenericServiceException ee){
	Debug.logError(ee, module);
	return ServiceUtil.returnError(ee.getMessage());
}
context.BOAddress=BOAddress;
}

conditionList = [];

invoiceTypes = delegator.findList("InvoiceType", EntityCondition.makeCondition(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["PURCHASE_INVOICE","SALES_INVOICE"])),UtilMisc.toSet("invoiceTypeId","parentTypeId"), null, null, false);
purchaseinvoiceTypesList = EntityUtil.filterByCondition(invoiceTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
salesinvoiceTypesList = EntityUtil.filterByCondition(invoiceTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"SALES_INVOICE"));
purchaseinvoiceTypes=EntityUtil.getFieldListFromEntityList(purchaseinvoiceTypesList, "invoiceTypeId", true);
salesinvoiceTypes=EntityUtil.getFieldListFromEntityList(salesinvoiceTypesList, "invoiceTypeId", true);

conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_READY"));
if(reportType=="CREDITORS"){
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.IN, purchaseinvoiceTypes));
}else{
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.IN, salesinvoiceTypes));
}
conditionList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.IN, branchList));
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
fieldsToSelect = ["invoiceId","partyIdFrom","partyId","amount","quantity","invoiceDate","costCenterId"] as Set;
invoiceAndItems = delegator.findList("InvoiceAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND),fieldsToSelect, null, null, false);
invoiceIds=EntityUtil.getFieldListFromEntityList(invoiceAndItems, "invoiceId", true);
invoicePaymentsList = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition("invoiceId", EntityOperator.IN,invoiceIds),UtilMisc.toSet("invoiceId","paymentId","amount","partyIdFrom","paymentDate","partyIdTo"), null, null, false);

invoiceslist = delegator.findList("Invoice", EntityCondition.makeCondition("invoiceId", EntityOperator.IN,invoiceIds),null, null, null, false);

if(reportType=="CREDITORS"){
	partyIds=EntityUtil.getFieldListFromEntityList(invoiceAndItems, "partyIdFrom", true);
}else{
	partyIds=EntityUtil.getFieldListFromEntityList(invoiceAndItems, "partyId", true);
}


invoiceDetailedList=[];
partyId="";
partyName="";
invocieList=[];
totalsMap=[:];
totfstMntInvTotals =0;
totsecMntInvTotals =0;
totthrdMntInvTotals =0;
totfrthMntInvTotals =0;
totfifthMntInvTotals =0;
totoneyearInvTotals =0;
tottwoyearInvTotals =0;
totabove3years=0;
totallMonthsTotal=0

if(UtilValidate.isNotEmpty(parameters.header)&&parameters.header.equals("required")){
stylesMap=[:];
if(branchId){
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", BOAddress);
	stylesMap.put("mainHeader3", "Agency Wise Invoice Outstanding Report");
	stylesMap.put("mainHeader4", "As On  "+ dateStr);
}
else{
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", "Agency Wise Invoice Outstanding Report");
	stylesMap.put("mainHeader3", "As On  "+ dateStr);
}
stylesMap.put("mainHeaderFontName","Arial");
stylesMap.put("mainHeadercellHeight",300);
stylesMap.put("mainHeaderFontSize",10);
stylesMap.put("mainHeadingCell",1);
stylesMap.put("mainHeaderBold",true);
stylesMap.put("columnHeaderFontName","Arial");
stylesMap.put("columnHeaderFontSize",10);
stylesMap.put("autoSizeCell",true);
stylesMap.put("columnHeaderCellHeight",300);
stylesMap.put("columnHeaderBgColor",false);
request.setAttribute("stylesMap", stylesMap);
request.setAttribute("enableStyles", true);

if(days=="30days"){
	headingMap=[:];
	headingMap.put("partyName", "Debitor/Creditor");
	headingMap.put("partyId", "partyId");
	if(Agencyreport == "DETAILED"){
		headingMap.put("invoiceId", "InvoiceId");
		headingMap.put("invoiceDate", "InvoiceDate");
	}
	headingMap.put("fstMntInvTotals", "0-30 Days");
	headingMap.put("secMntInvTotals", "31-60 Days");
	headingMap.put("thrdMntInvTotals", "61-90 Days");
	headingMap.put("frthMntInvTotals", "91-180 Days");
	headingMap.put("fifthMntInvTotals", "181Days-1year");
	headingMap.put("oneyearInvTotals", "1-2years");
	headingMap.put("twoyearInvTotals", "2-3years");
	headingMap.put("above3years","Above 3years");
	if(Agencyreport == "ABSTRACT"){
	headingMap.put("allMonthsTotal","Total Value");
	}
}
if(days=="45days"){
	headingMap=[:];
	headingMap.put("partyName", "Debitor/Creditor");
	headingMap.put("partyId", "partyId");
	if(Agencyreport == "DETAILED"){
		headingMap.put("invoiceId", "InvoiceId");
		headingMap.put("invoiceDate", "InvoiceDate");
	}
	headingMap.put("fstMntInvTotals", "0-45 Days");
	headingMap.put("secMntInvTotals", "45-90 Days");
	headingMap.put("thrdMntInvTotals", "91-180 Days");
	headingMap.put("frthMntInvTotals", "181Days-1year");
	headingMap.put("fifthMntInvTotals", "1-2years");
	headingMap.put("oneyearInvTotals", "2-3years");
	headingMap.put("above3years","Above 3years");
	if(Agencyreport == "ABSTRACT"){
	headingMap.put("allMonthsTotal","Total Value");
	}
}
invocieList.add(stylesMap);
invocieList.add(headingMap);
invoiceDetailedList.add(stylesMap);
invoiceDetailedList.add(headingMap);
}

if(Agencyreport == "ABSTRACT"){
for(partyId in partyIds){
	tempMap=[:];
	fstMntInvTotals =0;
	secMntInvTotals =0;
	thrdMntInvTotals =0;
	frthMntInvTotals =0;
	fifthMntInvTotals =0;
	oneyearInvTotals =0;
	twoyearInvTotals =0;
	above3years=0;
	
	fstMntPaidTotals =0;
	secMntPaidTotals =0;
	thrdMntPaidTotals =0;
	frthMntPaidTotals =0;
	fifthMntPaidTotals =0;
	oneyearPaidTotals =0;
	twoyearPaidTotals =0;
	above3yearsPaid=0;
	
	allMonthsTotal=0
	if(reportType=="CREDITORS"){
		invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItems, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
	}else{
		invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	}
	firstMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, fStMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,fStMnththruDate)));
	secndMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, sndMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,sndMnththruDate)));
	thirdMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, thrdMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,thrdMnththruDate)));
	fourthMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, frthMnthfromDate) ,EntityOperator.AND,
																							 EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnththruDate)));
	fifthMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromfifthMnthfromDate) ,EntityOperator.AND,
																							 EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,fromfifthMnththruDate)));
	oneyearInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, firstyearfromDate) ,EntityOperator.AND,
			 																				EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearthruDate)));
	if(days=="30days"){
	twoyearInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, secondyearfromDate) ,EntityOperator.AND,
						 																	EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearthruDate)));
	above3yearsInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearfromDate)));
																							    //EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
	}
	if(days=="45days"){
		above3yearsInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearfromDate)));
	}
	for(invoice in firstMntInvoiceAndItemsForParty){  
		if(UtilValidate.isNotEmpty(invoice)&& invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		fstMntInvTotals= fstMntInvTotals+total;
	}
	for(invoice in secndMntInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		secMntInvTotals= secMntInvTotals+total;
	}
	for(invoice in thirdMntInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		thrdMntInvTotals= thrdMntInvTotals+total;
	}
	for(invoice in fourthMntInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		frthMntInvTotals= frthMntInvTotals+total;
	}
	for(invoice in fifthMntInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		fifthMntInvTotals= fifthMntInvTotals+total;
	}
	for(invoice in oneyearInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		oneyearInvTotals= oneyearInvTotals+total;
	}
	if(days=="30days"){
	for(invoice in twoyearInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		twoyearInvTotals= twoyearInvTotals+total;
	}
	}
	for(invoice in above3yearsInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		above3years= above3years+total;
	}
	invoiceItemsAndPaymentsForParty=[];
	if(reportType=="CREDITORS"){
		invoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoicePaymentsList, EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
	}else{
		invoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoicePaymentsList, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
	}
	
	firstMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fStMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,fStMnththruDate)));
	secndMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, sndMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,sndMnththruDate)));
	thirdMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, thrdMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,thrdMnththruDate)));
	fourthMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, frthMnthfromDate) ,EntityOperator.AND,
		 																					EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnththruDate)));
	fifthMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromfifthMnthfromDate) ,EntityOperator.AND,
																							 EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,fromfifthMnththruDate)));
	oneyearInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, firstyearfromDate) ,EntityOperator.AND,
			 																				EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearthruDate)));
	if(days=="30days"){																					 
	twoyearInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, secondyearfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearthruDate)));
 	above3yearsInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearfromDate)));
	}
	if(days=="45days"){
		above3yearsInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
			EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearfromDate)));
	}
	for(eachPayment in firstMntInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		fstMntPaidTotals=fstMntPaidTotals+total
	}
	
	for(eachPayment in secndMntInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		secMntPaidTotals=secMntPaidTotals+total
	}
	
	for(eachPayment in thirdMntInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		thrdMntPaidTotals=thrdMntPaidTotals+total
	}
	
	for(eachPayment in fourthMntInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		frthMntPaidTotals=frthMntPaidTotals+total
	}
	
	for(eachPayment in fifthMntInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		fifthMntPaidTotals=fifthMntPaidTotals+total
	}
	
	for(eachPayment in oneyearInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		oneyearPaidTotals=oneyearPaidTotals+total
	}
	if(days=="30days"){
	for(eachPayment in twoyearInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		twoyearPaidTotals=twoyearPaidTotals+total
	}
	}
	for(eachPayment in above3yearsInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		above3yearsPaid=above3yearsPaid+total
	}
	
	fstMntInvTotals =fstMntInvTotals-fstMntPaidTotals;
	secMntInvTotals =secMntInvTotals-secMntPaidTotals;
	thrdMntInvTotals =thrdMntInvTotals-thrdMntPaidTotals;
	frthMntInvTotals =frthMntInvTotals-frthMntPaidTotals;
	fifthMntInvTotals =fifthMntInvTotals-fifthMntPaidTotals;
	oneyearInvTotals =oneyearInvTotals-oneyearPaidTotals;
	if(days=="30days"){
	twoyearInvTotals =twoyearInvTotals-twoyearPaidTotals;
	}
	above3years=above3years-above3yearsPaid;
	if(days=="30days"){
	allMonthsTotal=fstMntInvTotals+secMntInvTotals+thrdMntInvTotals+frthMntInvTotals+fifthMntInvTotals+oneyearInvTotals+twoyearInvTotals+above3years;
	}
	if(days=="45days"){
		allMonthsTotal=fstMntInvTotals+secMntInvTotals+thrdMntInvTotals+frthMntInvTotals+fifthMntInvTotals+oneyearInvTotals+above3years;
	}
	String partyName = PartyHelper.getPartyName(delegator,partyId,false);
	tempMap.put("partyName", partyName);
	tempMap.put("partyId", partyId);
	tempMap.put("fstMntInvTotals", fstMntInvTotals);
	tempMap.put("secMntInvTotals", secMntInvTotals);
	tempMap.put("thrdMntInvTotals", thrdMntInvTotals);
	tempMap.put("frthMntInvTotals", frthMntInvTotals);
	tempMap.put("fifthMntInvTotals", fifthMntInvTotals);
	tempMap.put("oneyearInvTotals", oneyearInvTotals);
	if(days=="30days"){
		tempMap.put("twoyearInvTotals", twoyearInvTotals);
	}
	tempMap.put("above3years", above3years);
	tempMap.put("allMonthsTotal", allMonthsTotal);
	if(allMonthsTotal>0){
		invocieList.add(tempMap);
		
		totfstMntInvTotals =totfstMntInvTotals+fstMntInvTotals;
		totsecMntInvTotals =totsecMntInvTotals+secMntInvTotals;
		totthrdMntInvTotals =totthrdMntInvTotals+thrdMntInvTotals;
		totfrthMntInvTotals =totfrthMntInvTotals+frthMntInvTotals;
		totfifthMntInvTotals =totfifthMntInvTotals+fifthMntInvTotals;
		totoneyearInvTotals =totoneyearInvTotals+oneyearInvTotals;
		if(days=="30days"){
		tottwoyearInvTotals =tottwoyearInvTotals+twoyearInvTotals;
		}
		totabove3years=totabove3years+above3years;
		totallMonthsTotal=totallMonthsTotal+allMonthsTotal;
	}
	
}

totalsMap.put("partyName", "TOTAL");
totalsMap.put("partyId", partyId);
totalsMap.put("fstMntInvTotals", totfstMntInvTotals);
totalsMap.put("secMntInvTotals", totsecMntInvTotals);
totalsMap.put("thrdMntInvTotals", totthrdMntInvTotals);
totalsMap.put("frthMntInvTotals", totfrthMntInvTotals);
totalsMap.put("fifthMntInvTotals", totfifthMntInvTotals);
totalsMap.put("oneyearInvTotals", totoneyearInvTotals);
if(days=="30days"){
	totalsMap.put("twoyearInvTotals", tottwoyearInvTotals);
}
totalsMap.put("above3years", totabove3years);
totalsMap.put("allMonthsTotal", totallMonthsTotal);
if(totallMonthsTotal>0){
	invocieList.add(totalsMap);
}
}else{
	for(partyId in partyIds){
		fstMntInvTotals =0;
		secMntInvTotals =0;
		thrdMntInvTotals =0;
		frthMntInvTotals =0;
		fifthMntInvTotals =0;
		oneyearInvTotals =0;
		twoyearInvTotals =0;
		above3years=0;
		
		if(reportType=="CREDITORS"){
			invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceslist, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		}else{
			invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceslist, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		}
		//invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceslist, EntityCondition.makeCondition("costCenterId", EntityOperator.EQUALS, partyId));
		
		firstMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, fStMnthfromDate) ,EntityOperator.AND,
																								EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,fStMnththruDate)));
		secndMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, sndMnthfromDate) ,EntityOperator.AND,
																								EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,sndMnththruDate)));
		thirdMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, thrdMnthfromDate) ,EntityOperator.AND,
																								EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,thrdMnththruDate)));
		fourthMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, frthMnthfromDate) ,EntityOperator.AND,
																								 EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnththruDate)));
		fifthMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromfifthMnthfromDate) ,EntityOperator.AND,
																								 EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,fromfifthMnththruDate)));
		oneyearInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, firstyearfromDate) ,EntityOperator.AND,
																								 EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearthruDate)));
		if(days=="30days"){
		twoyearInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, secondyearfromDate) ,EntityOperator.AND,
																								 EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearthruDate)));
		above3yearsInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearfromDate)));
																									//EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
		}
		if(days=="45days"){
			above3yearsInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearfromDate)));
		}
		
		
		
		
		
		invoiceItemsAndPaymentsForParty=[];
		if(reportType=="CREDITORS"){
			invoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoicePaymentsList, EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
		}else{
			invoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoicePaymentsList, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		}
		
		firstMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fStMnthfromDate) ,EntityOperator.AND,
																								EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,fStMnththruDate)));
		secndMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, sndMnthfromDate) ,EntityOperator.AND,
																								EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,sndMnththruDate)));
		thirdMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, thrdMnthfromDate) ,EntityOperator.AND,
																								EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,thrdMnththruDate)));
		fourthMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, frthMnthfromDate) ,EntityOperator.AND,
																								 EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnththruDate)));
		fifthMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromfifthMnthfromDate) ,EntityOperator.AND,
																								 EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,fromfifthMnththruDate)));
		oneyearInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, firstyearfromDate) ,EntityOperator.AND,
																								 EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearthruDate)));
		if(days=="30days"){
		twoyearInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, secondyearfromDate) ,EntityOperator.AND,
																								EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearthruDate)));
		 above3yearsInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
																								EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,secondyearfromDate)));
		}
		if(days=="45days"){
			above3yearsInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
				EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,firstyearfromDate)));
		}
		
		invoiceAmount="";
		paidAmount="";
		String partyName = PartyHelper.getPartyName(delegator,partyId,false);
		
		for(invoice in firstMntInvoiceAndItemsForParty){
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoicepaymentslist = EntityUtil.filterByCondition(firstMntInvoiceItemsAndPaymentsForParty, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId));
			if(UtilValidate.isNotEmpty(invoicepaymentslist)){
				for(eachinv in invoicepaymentslist){
					paidAmount = InvoiceWorker.getInvoiceTotal(delegator,eachinv.invoiceId);
				}
				fstMntInvTotals=invoiceAmount-paidAmount;
			}
			
			fstMntInvTotals=invoiceAmount;
			totfstMntInvTotals =totfstMntInvTotals+fstMntInvTotals;
			tempMap=[:];
			tempMap.put("invoiceId", invoice.invoiceId);
			tempMap.put("invoiceDate", invoice.invoiceDate);
			tempMap.put("fstMntInvTotals", fstMntInvTotals);
			tempMap.put("partyName", partyName);
			tempMap.put("partyId", partyId);
			invoiceDetailedList.add(tempMap);
			totalsMap.put("fstMntInvTotals", totfstMntInvTotals);
		}
		for(invoice in secndMntInvoiceAndItemsForParty){
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
				invoicepaymentslist = EntityUtil.filterByCondition(secndMntInvoiceItemsAndPaymentsForParty, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId));
				if(UtilValidate.isNotEmpty(invoicepaymentslist)){
					for(eachinv in invoicepaymentslist){
						paidAmount = InvoiceWorker.getInvoiceTotal(delegator,eachinv.invoiceId);
					}
					secMntInvTotals=invoiceAmount-paidAmount;
				}
				secMntInvTotals=invoiceAmount;
				totsecMntInvTotals =totsecMntInvTotals+secMntInvTotals;
				tempMap=[:];
				tempMap.put("invoiceId", invoice.invoiceId);
				tempMap.put("invoiceDate", invoice.invoiceDate);
				tempMap.put("secMntInvTotals", secMntInvTotals);
				tempMap.put("partyName", partyName);
				tempMap.put("partyId", partyId);
				invoiceDetailedList.add(tempMap);
				totalsMap.put("secMntInvTotals", totsecMntInvTotals);
		}
		for(invoice in thirdMntInvoiceAndItemsForParty){
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
				invoicepaymentslist = EntityUtil.filterByCondition(thirdMntInvoiceItemsAndPaymentsForParty, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId));
				if(UtilValidate.isNotEmpty(invoicepaymentslist)){
					for(eachinv in invoicepaymentslist){
						paidAmount = InvoiceWorker.getInvoiceTotal(delegator,eachinv.invoiceId);
					}
					thrdMntInvTotals=invoiceAmount-paidAmount;
				}
				thrdMntInvTotals=invoiceAmount;
				totthrdMntInvTotals =totthrdMntInvTotals+thrdMntInvTotals;
				tempMap=[:];
				tempMap.put("invoiceId", invoice.invoiceId);
				tempMap.put("invoiceDate", invoice.invoiceDate);
				tempMap.put("thrdMntInvTotals", thrdMntInvTotals);
				tempMap.put("partyName", partyName);
				tempMap.put("partyId", partyId);
				invoiceDetailedList.add(tempMap);
				totalsMap.put("thrdMntInvTotals", totthrdMntInvTotals);
		}
		for(invoice in fourthMntInvoiceAndItemsForParty){
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);			
				invoicepaymentslist = EntityUtil.filterByCondition(fourthMntInvoiceItemsAndPaymentsForParty, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId));
				if(UtilValidate.isNotEmpty(invoicepaymentslist)){
					for(eachinv in invoicepaymentslist){
						paidAmount = InvoiceWorker.getInvoiceTotal(delegator,eachinv.invoiceId);
					}
					frthMntInvTotals=invoiceAmount-paidAmount;
				}
				frthMntInvTotals=invoiceAmount;
				totfrthMntInvTotals =totfrthMntInvTotals+frthMntInvTotals;
				tempMap=[:];
				tempMap.put("invoiceId", invoice.invoiceId);
				tempMap.put("invoiceDate", invoice.invoiceDate);
				tempMap.put("frthMntInvTotals", frthMntInvTotals);
				tempMap.put("partyName", partyName);
				tempMap.put("partyId", partyId);
				invoiceDetailedList.add(tempMap);
				totalsMap.put("frthMntInvTotals", totfrthMntInvTotals);
		}
		for(invoice in fifthMntInvoiceAndItemsForParty){
			tempMap=[:];
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
				invoicepaymentslist = EntityUtil.filterByCondition(fifthMntInvoiceItemsAndPaymentsForParty, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId));
				if(UtilValidate.isNotEmpty(invoicepaymentslist)){
					for(eachinv in invoicepaymentslist){
						paidAmount = InvoiceWorker.getInvoiceTotal(delegator,eachinv.invoiceId);
					}
					fifthMntInvTotals=invoiceAmount-paidAmount;
				}
				fifthMntInvTotals=invoiceAmount;
				totfifthMntInvTotals =totfifthMntInvTotals+fifthMntInvTotals;
				tempMap=[:];
				tempMap.put("invoiceId", invoice.invoiceId);
				tempMap.put("invoiceDate", invoice.invoiceDate);
				tempMap.put("fifthMntInvTotals", fifthMntInvTotals);
				tempMap.put("partyName", partyName);
				tempMap.put("partyId", partyId);
				invoiceDetailedList.add(tempMap);
				totalsMap.put("fifthMntInvTotals", totfifthMntInvTotals);
		}
		for(invoice in oneyearInvoiceAndItemsForParty){
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
				invoicepaymentslist = EntityUtil.filterByCondition(oneyearInvoiceItemsAndPaymentsForParty, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId));
				if(UtilValidate.isNotEmpty(invoicepaymentslist)){
					for(eachinv in invoicepaymentslist){
						paidAmount = InvoiceWorker.getInvoiceTotal(delegator,eachinv.invoiceId);
					}
					oneyearInvTotals=invoiceAmount-paidAmount;
				}
				oneyearInvTotals=invoiceAmount;
				totoneyearInvTotals =totoneyearInvTotals+oneyearInvTotals;
				tempMap=[:];
				tempMap.put("invoiceId", invoice.invoiceId);
				tempMap.put("invoiceDate", invoice.invoiceDate);
				tempMap.put("oneyearInvTotals", oneyearInvTotals);
				tempMap.put("partyName", partyName);
				tempMap.put("partyId", partyId);
				invoiceDetailedList.add(tempMap);
				totalsMap.put("oneyearInvTotals", totoneyearInvTotals);
		}
		if(days=="30days"){
			for(invoice in twoyearInvoiceAndItemsForParty){
				invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
					invoicepaymentslist = EntityUtil.filterByCondition(twoyearInvoiceItemsAndPaymentsForParty, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId));
					if(UtilValidate.isNotEmpty(invoicepaymentslist)){
						for(eachinv in invoicepaymentslist){
							paidAmount = InvoiceWorker.getInvoiceTotal(delegator,eachinv.invoiceId);
						}
						twoyearInvTotals=invoiceAmount-paidAmount;
					}
					twoyearInvTotals=invoiceAmount;
					tottwoyearInvTotals =tottwoyearInvTotals+twoyearInvTotals;
					tempMap=[:];
					tempMap.put("invoiceId", invoice.invoiceId);
					tempMap.put("invoiceDate", invoice.invoiceDate);
					tempMap.put("twoyearInvTotals", twoyearInvTotals);
					tempMap.put("partyName", partyName);
					tempMap.put("partyId", partyId);
					invoiceDetailedList.add(tempMap);
					totalsMap.put("twoyearInvTotals", tottwoyearInvTotals);
			}
		}
		for(invoice in above3yearsInvoiceAndItemsForParty){
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
				invoicepaymentslist = EntityUtil.filterByCondition(above3yearsInvoiceItemsAndPaymentsForParty, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId));
				if(UtilValidate.isNotEmpty(invoicepaymentslist)){
					for(eachinv in invoicepaymentslist){
						paidAmount = InvoiceWorker.getInvoiceTotal(delegator,eachinv.invoiceId);
					}
					above3years=invoiceAmount-paidAmount;
				}
				above3years=invoiceAmount;
				totabove3years=totabove3years+above3years;
				tempMap=[:];
				tempMap.put("invoiceId", invoice.invoiceId);
				tempMap.put("invoiceDate", invoice.invoiceDate);
				tempMap.put("above3years", above3years);
				tempMap.put("partyName", partyName);
				tempMap.put("partyId", partyId);
				invoiceDetailedList.add(tempMap);
				totalsMap.put("above3years", totabove3years);
		}
	}
	invoiceDetailedList.add(totalsMap);
}
context.invoiceDetailedList=invoiceDetailedList;
context.invocieList=invocieList;
context.reportType=reportType;
context.dateStr=dateStr;
context.days=days;
context.Agencyreport=Agencyreport;







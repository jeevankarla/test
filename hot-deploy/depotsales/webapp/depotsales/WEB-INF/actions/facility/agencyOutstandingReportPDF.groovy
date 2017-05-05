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

dateStr=parameters.AWORDate;
reportType=parameters.reportType;
Agencyreport=parameters.Agencyreport;
rounding = RoundingMode.HALF_UP;

if(UtilValidate.isNotEmpty(dateStr)){
	thruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
	fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-1095);
}
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
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
fieldsToSelect = ["invoiceId","partyIdFrom","partyId","amount","quantity","invoiceDate","costCenterId"] as Set;
invoiceAndItems = delegator.findList("InvoiceAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND),fieldsToSelect, null, null, false);
invoiceIds=EntityUtil.getFieldListFromEntityList(invoiceAndItems, "invoiceId", true);
invoicePaymentsList = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition("invoiceId", EntityOperator.IN,invoiceIds),UtilMisc.toSet("invoiceId","paymentId","amount","partyIdFrom","paymentDate","partyIdTo"), null, null, false);
Debug.log("-------invoicePaymentsList--------------"+invoicePaymentsList);
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

totabove3years=0;

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

	headingMap=[:];
	headingMap.put("partyName", "Debitor/Creditor");
	headingMap.put("partyId", "partyId");
	if(Agencyreport == "DETAILED"){
		headingMap.put("invoiceId", "InvoiceId");
		headingMap.put("invoiceDate", "InvoiceDate");
	}
	headingMap.put("above3years","Above 3years");

invocieList.add(stylesMap);
invocieList.add(headingMap);
invoiceDetailedList.add(stylesMap);
invoiceDetailedList.add(headingMap);
}
if(Agencyreport == "ABSTRACT"){
for(partyId in partyIds){
	tempMap=[:];
	above3years=0;
	above3yearsPaid=0;
	
	if(reportType=="CREDITORS"){
		invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItems, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
	}else{
		invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	}
	above3yearsInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, fromDate)));
		
		
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
	above3yearsInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
			EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate)));
	
	
	for(eachPayment in above3yearsInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		above3yearsPaid=above3yearsPaid+total
	}
	above3years=above3years-above3yearsPaid;

	String partyName = PartyHelper.getPartyName(delegator,partyId,false);
	if(above3years > 100000){
	tempMap.put("partyName", partyName);
	tempMap.put("partyId", partyId);
	tempMap.put("above3years", above3years);
	invocieList.add(tempMap);
	
	totabove3years=totabove3years+above3years;
	}
}
	totalsMap.put("partyName", "TOTAL");
	totalsMap.put("partyId", partyId);
	totalsMap.put("above3years", totabove3years);

	invocieList.add(totalsMap);

}else{
	for(partyId in partyIds){
		above3years=0;
		
		if(reportType=="CREDITORS"){
			invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceslist, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		}else{
			invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceslist, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		}
		
		above3yearsInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, fromDate)));
		
		
		invoiceItemsAndPaymentsForParty=[];
		if(reportType=="CREDITORS"){
			invoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoicePaymentsList, EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
		}else{
			invoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoicePaymentsList, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		}
		
		above3yearsInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
				EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate)));
		
		invoiceAmount="";
		paidAmount="";
		String partyName = PartyHelper.getPartyName(delegator,partyId,false);
		
		for(invoice in above3yearsInvoiceAndItemsForParty){
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
				invoicepaymentslist = EntityUtil.filterByCondition(above3yearsInvoiceItemsAndPaymentsForParty, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId));
				if(UtilValidate.isNotEmpty(invoicepaymentslist)){
					for(eachinv in invoicepaymentslist){
						paidAmount = InvoiceWorker.getInvoiceTotal(delegator,eachinv.invoiceId);
					}
					above3years=invoiceAmount-paidAmount;
				}else{
					above3years=invoiceAmount;
				}
				tempMap=[:];
				if(above3years > 100000){
					tempMap.put("invoiceId", invoice.invoiceId);
					tempMap.put("invoiceDate", invoice.invoiceDate);
					tempMap.put("above3years", above3years);
					tempMap.put("partyName", partyName);
					tempMap.put("partyId", partyId);
					invoiceDetailedList.add(tempMap);
					
					totabove3years=totabove3years+above3years;
					totalsMap.put("partyName", "TOTAL");
					totalsMap.put("above3years", totabove3years);
				}
		}
	}
	invoiceDetailedList.add(totalsMap);
}
context.invoiceDetailedList=invoiceDetailedList;
context.invocieList=invocieList;
context.reportType=reportType;
context.dateStr=dateStr;
context.Agencyreport=Agencyreport;







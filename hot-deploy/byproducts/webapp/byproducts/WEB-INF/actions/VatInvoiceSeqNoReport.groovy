import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;

dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}

fromDateTime = UtilDateTime.getDayStart(fromDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = fromDateTime;
context.thruDate = thruDateTime;
maxIntervalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
/*if(maxIntervalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}*/

boothList = ByProductNetworkServices.getAllBooths(delegator, null).get("boothsList");
finYearContext = [:];
finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
finYearContext.put("organizationPartyId", "Company");
finYearContext.put("userLogin", userLogin);
finYearContext.put("findDate", dayBegin);
finYearContext.put("excludeNoOrganizationPeriods", "Y");
List customTimePeriodList = FastList.newInstance();
Map resultCtx = FastMap.newInstance();
try{
	resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
	if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in fetching financial year ", module);
		return ServiceUtil.returnError("Problem in fetching financial year ");
	}
}catch(GenericServiceException e){
	Debug.logError(e, module);
	return ServiceUtil.returnError(e.getMessage());
}
customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
String finYearId = "";
if(UtilValidate.isNotEmpty(customTimePeriodList)){
	GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
	finYearId = (String)customTimePeriod.get("customTimePeriodId");
}
invoiceSequenceNumMap = [:];
conditionList = [];
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, boothList));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF")));
conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoices = delegator.findList("InvoiceAndFacility", condExpr, null, ["dueDate","facilityId"], null, false);
//Debug.log("invoices====="+invoices);
invoiceDates = EntityUtil.getFieldListFromEntityList(invoices, "dueDate", true);
invoiceIds = EntityUtil.getFieldListFromEntityList(invoices, "invoiceId", true);
crInvoices = [:];
invoiceDates.each{eachDate ->
	sameDayInvoices = EntityUtil.filterByCondition(invoices, EntityCondition.makeCondition("dueDate", EntityOperator.EQUALS, eachDate));
	eachDayInvoiceDetails = [];
	sameDayInvoices.each{eachInvoiceDetail ->
		invDetailMap = [:];
		invoiceId = eachInvoiceDetail.invoiceId;
		amount=InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
		taxAmount=InvoiceWorker.getInvoiceTaxTotal((GenericValue)eachInvoiceDetail,"VAT_SALE");
		invDetailMap["amount"]= amount-taxAmount;
		invDetailMap["taxAmount"]= taxAmount;
		invDetailMap["invoiceId"] = invoiceId;
		invDetailMap["facilityId"] = eachInvoiceDetail.facilityId;
		invDetailMap["facilityName"] = eachInvoiceDetail.facilityName;
		eachDayInvoiceDetails.add(invDetailMap);
	}
	crInvoices.put(eachDate, eachDayInvoiceDetails);
}
//Debug.log("crInvoices====="+crInvoices);
invoiceSequenceNumMap = [:];
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("billOfSaleTypeId", EntityOperator.EQUALS , "VAT_INV"));
conditionList.add(EntityCondition.makeCondition("finYearId", EntityOperator.EQUALS , finYearId));
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN , invoiceIds));
cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
sequenceList = delegator.findList("BillOfSaleInvoiceSequence", cond, null, null, null, false);
sequenceList.each{eachItem ->
	invoiceSequenceNumMap.put(eachItem.invoiceId, eachItem.sequenceId);
}
context.crInvoices = crInvoices;
context.invoiceSequenceNumMap = invoiceSequenceNumMap;


import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.GenericEntityException;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;

import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.ofbiz.service.ServiceUtil;
import java.math.RoundingMode;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;

import org.ofbiz.product.product.ProductWorker;

import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;
rounding = RoundingMode.HALF_UP;
schemeBillingId = parameters.schemeBillingId;
dctx = dispatcher.getDispatchContext();

schemeTimePeriod = delegator.findOne("SchemeTimePeriod",[schemeTimePeriodId : schemeBillingId] , false);
/*invoice = delegator.findList("Invoice",EntityCondition.makeCondition("periodBillingId",EntityOperator.EQUALS,schemeBillingId), UtilMisc.toSet("invoiceId"), null, null, false);
if(UtilValidate.isNotEmpty(invoice))
{
	request.setAttribute("isInvoiceGen","Y");
	return "success";
}
*/
frmDateStr=schemeTimePeriod.getString("fromDate"); schemeTimePeriod
toDateStr=schemeTimePeriod.getString("thruDate");
fromDate=null;
thruDate=null;

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
if(UtilValidate.isNotEmpty(frmDateStr)){
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(frmDateStr).getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + frmDateStr, "");
		request.setAttribute("_ERROR_MESSAGE_","Cannot parse date string: "+ frmDateStr);
	}
	fromDate = UtilDateTime.getDayStart(fromDate);
}
if(UtilValidate.isNotEmpty(toDateStr)){
	try {
		thruDate = new java.sql.Timestamp(sdf.parse(toDateStr).getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + toDateStr, "");
		request.setAttribute("_ERROR_MESSAGE_","Cannot parse date string: "+ toDateStr);
	}
	thruDate = UtilDateTime.getDayEnd(thruDate);
}
partyIdToList=[];
roPartIds=["INT1","INT2","INT3","INT4","INT5","INT6","INT26","INT28","INT47"];
roPartIds.each{ eachRo ->
	resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",eachRo));
	if(resultCtx && resultCtx.get("partyList")){
		partyList=resultCtx.get("partyList");
		partyList.each{eachparty ->
			partyIdToList.add(eachparty.partyIdTo);
		}
	}
	partyIdToList.add(eachRo);
}
boolean beganTransaction = false;
try {
	beganTransaction = TransactionUtil.begin();
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,partyIdToList));
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.IN,["TEN_PERCENT_SUBSIDY"]));
	tenPerInvoiceListBtPeriodDates = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","itemValue","quantity","amount","invoiceItemTypeId"), null, null, false);
	tenPerinvoiceIds=EntityUtil.getFieldListFromEntityList(tenPerInvoiceListBtPeriodDates, "invoiceId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerinvoiceIds));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.IN,["TEN_PERCENT_SUBSIDY","INV_FPROD_ITEM"]));
	allInvoiceListBtPeriodDates = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("itemValue","quantity","amount","invoiceItemTypeId"), null, null, false);
	
	BigDecimal totaltenPerSubAmount=BigDecimal.ZERO;
	BigDecimal totalSerChrgAmount=BigDecimal.ZERO;
	BigDecimal totalInvoiceAmount=BigDecimal.ZERO;
	
	for(eachRecord  in allInvoiceListBtPeriodDates){
		subsidyAmt=0;
		invoiceItemTypeId=eachRecord.invoiceItemTypeId;
		if("TEN_PERCENT_SUBSIDY".equals(invoiceItemTypeId) && UtilValidate.isNotEmpty(eachRecord.amount) && UtilValidate.isNotEmpty(eachRecord.quantity)){
			subsidyAmt =(eachRecord.amount*eachRecord.quantity)*(-1);
			totaltenPerSubAmount=totaltenPerSubAmount.add(subsidyAmt)
		}
		totalInvoiceAmount=totalInvoiceAmount.add(subsidyAmt*10)
		
	}
	totalSerChrgAmount=totalInvoiceAmount.multiply(0.005);
	Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin) 
	input.put("invoiceTypeId", "SALES_INVOICE")
	input.put("statusId","INVOICE_IN_PROCESS")
	input.put("periodBillingId",schemeBillingId) 
	input.put("partyIdFrom", "HO");
	input.put("partyId", "MIN_OF_TEXTILE"); 
	input.put( "invoiceDate", UtilDateTime.nowTimestamp())
	input.put("purposeTypeId","TEN_PER_SUB_SER_CHRG")
	input.put("createdByUserLogin",userLogin.getString("userLoginId"))
	Map<String, Object> result = dispatcher.runSync("createInvoice", input);
	if (ServiceUtil.isError(result)) {
		request.setAttribute("_ERROR_MESSAGE_","Error while creating invoic");
		return "error";
	}
	String invoiceId = (String)result.get("invoiceId");
	
	Map<String, Object> inputItem = UtilMisc.toMap("userLogin", userLogin)
	inputItem.put("invoiceId", invoiceId)
	inputItem.put("invoiceItemTypeId","TEN_PER_SUB_REMB")
	inputItem.put("quantity", BigDecimal.ONE)
	inputItem.put("amount",totaltenPerSubAmount)
	inputItem.put("itemValue",totaltenPerSubAmount.setScale(2,rounding))
	Map<String, Object> result2 = dispatcher.runSync("createInvoiceItem", inputItem);
	if (ServiceUtil.isError(result2)) {
		request.setAttribute("_ERROR_MESSAGE_","Error while creating invoicItem For Ten Percent subsidy item");
		return "error";
	}
	Map<String, Object> inputItem2 = UtilMisc.toMap("userLogin", userLogin)
	inputItem2.put("invoiceId", invoiceId)
	inputItem2.put("invoiceItemTypeId","SER_CHRG_REMB");
	inputItem2.put( "quantity", BigDecimal.ONE)
	inputItem2.put("amount",totalSerChrgAmount)
	inputItem2.put("itemValue",totalSerChrgAmount.setScale(2,rounding))
	Map<String, Object> result3 = dispatcher.runSync("createInvoiceItem", inputItem2);
	if (ServiceUtil.isError(result3)) {
		request.setAttribute("_ERROR_MESSAGE_","Error while creating invoicItem For Service Charge item");
		return "error";
	}
	schemeTimePeriod.set("isClosed","Y")
	delegator.store(schemeTimePeriod)
	
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "TEN_SUB_REIMB_PERIOD"));
	schemeTimePeriod = delegator.findList("SchemeTimePeriod",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("periodName","fromDate","thruDate","schemeTimePeriodId","isClosed"), null, null, false);
	
	List<GenericValue> openschemeTimePeriod = EntityUtil.filterByCondition(schemeTimePeriod, EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
	List<GenericValue> closeschemeTimePeriod = EntityUtil.filterByCondition(schemeTimePeriod, EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "Y"));
	
	JSONArray openbillingPeriodsList = new JSONArray();
	JSONArray closebillingPeriodsList = new JSONArray();
	
	for(eachId in openschemeTimePeriod)
	{
		JSONObject newObj = new JSONObject();
		fromDateStr=eachId.getString("fromDate");
		thruDateStr=eachId.getString("thruDate");
		newObj.put("label", fromDateStr + "-" + thruDateStr + "[" + eachId.periodName +"]")
		newObj.put("value", eachId.schemeTimePeriodId)
		openbillingPeriodsList.add(newObj);
	}
	
	for(eachId in closeschemeTimePeriod)
	{
		JSONObject newObj = new JSONObject();
		fromDateStr=eachId.getString("fromDate");
		thruDateStr=eachId.getString("thruDate");
		newObj.put("label", fromDateStr + "-" + thruDateStr + "[" + eachId.periodName +"]")
		newObj.put("value", eachId.schemeTimePeriodId)
		closebillingPeriodsList.add(newObj);
	}
	request.setAttribute("isInvoiceGen","N");
	request.setAttribute("billingPeriodsList",openbillingPeriodsList);
	request.setAttribute("closebillingPeriodsList",closebillingPeriodsList);
	
	return "success";

} catch (Exception e) {
	Debug.logError(e, "Failure in operation, rolling back transaction", "generateBillingForPeriod.groovy");
	try {
		TransactionUtil.rollback(beganTransaction, "Error looking up entity values in WebTools Entity Data Maintenance", e);
	} catch (GenericEntityException e2) {
		Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), "generateBillingForPeriod.groovy");
	}
	throw e;
} finally {
	TransactionUtil.commit(beganTransaction);
}













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
import org.ofbiz.accounting.invoice.InvoiceWorker;

import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;
rounding = RoundingMode.HALF_UP;
schemeBillingId = parameters.schemeBillingId;
dctx = dispatcher.getDispatchContext();

rowiseTsPercentageMap=[:];
hydRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
cmbRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
kolRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
kanRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
vjyRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
panRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
gwhRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
varRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
bhuRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);

rowiseTsPercentageMap= UtilMisc.toMap("INT1",varRoMap,"INT2",panRoMap,"INT3",kolRoMap,"INT4",cmbRoMap,"INT5",hydRoMap,"INT6",kanRoMap,"INT26",bhuRoMap,"INT28",gwhRoMap,"INT47",vjyRoMap);

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
	//conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,partyIdToList));
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	salesInvoicesForPeriod = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","partyId","productId","invoiceDate","shipmentId","costCenterId"), null, null, false);
	invocieIds=EntityUtil.getFieldListFromEntityList(salesInvoicesForPeriod, "invoiceId", true);
	allcustomerIds=EntityUtil.getFieldListFromEntityList(salesInvoicesForPeriod, "partyId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invocieIds));
	ordersListForInvoices = delegator.findList("OrderItemBilling",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("orderId"), null, null, false);
	orderIds=EntityUtil.getFieldListFromEntityList(ordersListForInvoices, "orderId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,orderIds));
	conditionList.add(EntityCondition.makeCondition("attrName",EntityOperator.EQUALS,"SCHEME_CAT"));
	conditionList.add(EntityCondition.makeCondition("attrValue",EntityOperator.IN,["MGPS_10Pecent","MGPS"]));
	ordersListForInvoices = delegator.findList("OrderAttribute",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("orderId"), null, null, false);
	orderIds=EntityUtil.getFieldListFromEntityList(ordersListForInvoices, "orderId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invocieIds));
	conditionList.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,orderIds));
	ordersListForInvoices = delegator.findList("OrderItemBilling",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId"), null, null, false);
	mgpsAnd10perinvoiceIdsForPeriod=EntityUtil.getFieldListFromEntityList(ordersListForInvoices, "invoiceId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,mgpsAnd10perinvoiceIdsForPeriod));
	allInvoiceListBtPeriodDates = EntityUtil.filterByCondition(salesInvoicesForPeriod, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,allcustomerIds));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"DEPOT_SOCIETY"));
	facilityList = delegator.findList("Facility",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("ownerPartyId"), null, null, false);
	depotcustomerIds=EntityUtil.getFieldListFromEntityList(facilityList, "ownerPartyId", true);
	
	BigDecimal TotalTRAmount=BigDecimal.ZERO;
	BigDecimal totalDepoChrgAmount=BigDecimal.ZERO;
	BigDecimal totalInvoiceAmount=BigDecimal.ZERO;
	
	for(eachRecord  in allInvoiceListBtPeriodDates){
		invoiceId=eachRecord.invoiceId;
		BigDecimal transSchemePercentageForParty = BigDecimal.ZERO;
		BigDecimal depoSchemePercentageForParty = BigDecimal.ZERO;
		BigDecimal transportaionCost=BigDecimal.ZERO;
		BigDecimal invoiceAmount = BigDecimal.ZERO;
		shipment = delegator.findOne("Shipment",[shipmentId : eachRecord.shipmentId] , false);
		if(shipment.estimatedShipCost){
			transportaionCost=transportaionCost.add(shipment.estimatedShipCost);
		}
		invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoiceId);
		/*Map<String, Object> inputParams = UtilMisc.toMap("userLogin", userLogin)
		Debug.log("eachRecord.productId====="+ eachRecord.productId);
		inputParams.put("partyId", eachRecord.partyId)
		inputParams.put("productId", eachRecord.productId)
		inputParams.put("schemeTypeId", "SHIP_REIMBURSEMENT")
		inputParams.put("invoiceDate", eachRecord.invoiceDate)
		Map<String, Object> result = dispatcher.runSync("getReimbursmentPercentage", inputParams);
		if (ServiceUtil.isError(result)) {
			request.setAttribute("_ERROR_MESSAGE_","Unable to get scheme percentage");
			return "error";
		}*/
		percentageMap=rowiseTsPercentageMap.get(eachRecord.partyIdFrom);
		product = delegator.findOne("Product",[productId : eachRecord.productId] , false);
		productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
		transSchemePercentageForParty=percentageMap.get(productCategory.primaryParentCategoryId);
		
		Debug.log("transSchemePercentageForParty====="+ transSchemePercentageForParty);
		//transSchemePercentageForParty= percentageMap.get("schemePercent");
		invoiceAmount=invoiceAmount.multiply(transSchemePercentageForParty);
		eligiableAmtForParty=invoiceAmount.divide(100);
		
		if(eligiableAmtForParty.compareTo(transportaionCost)>0){
			TotalTRAmount=TotalTRAmount.add(transportaionCost);
		}else{
			TotalTRAmount=TotalTRAmount.add(eligiableAmtForParty);
		}
		
		if(depotcustomerIds.contains(eachRecord.partyId)){
			inputParams.put("schemeTypeId", "DEPOT_REIMBURSEMENT");
			Map<String, Object> result2 = dispatcher.runSync("getReimbursmentPercentage", inputParams);
			if (ServiceUtil.isError(result2)) {
				request.setAttribute("_ERROR_MESSAGE_","Error while creating invoic");
				return "error";
			}
			depoSchemePercentageForParty= result2.get("schemePercent");
			invoiceAmount=invoiceAmount.multiply(depoSchemePercentageForParty)
			eligiableDepoAmtForParty=invoiceAmount.divide(100);
			totalDepoChrgAmount=totalDepoChrgAmount.add(eligiableDepoAmtForParty);
		}
	}
	Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin)
	input.put("invoiceTypeId", "SALES_INVOICE")
	input.put("statusId","INVOICE_IN_PROCESS")
	input.put("periodBillingId",schemeBillingId)
	input.put("partyIdFrom", "HO");
	input.put("partyId", "MIN_OF_TEXTILE");
	input.put( "invoiceDate", UtilDateTime.nowTimestamp())
	input.put("purposeTypeId","DEP_SHIP_REMB_CHARG")
	input.put("createdByUserLogin",userLogin.getString("userLoginId"))
	Map<String, Object> result = dispatcher.runSync("createInvoice", input);
	if (ServiceUtil.isError(result)) {
		request.setAttribute("_ERROR_MESSAGE_","Error while creating invoic");
		return "error";
	}
	String invoiceId = (String)result.get("invoiceId");
	Map<String, Object> inputItem = UtilMisc.toMap("userLogin", userLogin)
	inputItem.put("invoiceId", invoiceId)
	inputItem.put("invoiceItemTypeId","SHIP_CHARG_REMB")
	inputItem.put("quantity", BigDecimal.ONE)
	inputItem.put("amount",TotalTRAmount)
	inputItem.put("itemValue",TotalTRAmount.setScale(2,rounding))
	Map<String, Object> result2 = dispatcher.runSync("createInvoiceItem", inputItem);
	if (ServiceUtil.isError(result2)) {
		request.setAttribute("_ERROR_MESSAGE_","Error while creating invoicItem For Transportation item");
		return "error";
	}
	Map<String, Object> inputItem2 = UtilMisc.toMap("userLogin", userLogin)
	inputItem2.put("invoiceId", invoiceId)
	inputItem2.put("invoiceItemTypeId","DEPO_CHRG_REMB");
	inputItem2.put( "quantity", BigDecimal.ONE)
	inputItem2.put("amount",totalDepoChrgAmount)
	inputItem2.put("itemValue",totalDepoChrgAmount.setScale(2,rounding))
	Map<String, Object> result3 = dispatcher.runSync("createInvoiceItem", inputItem2);
	if (ServiceUtil.isError(result3)) {
		request.setAttribute("_ERROR_MESSAGE_","Error while creating invoicItem For Depot Charge item");
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












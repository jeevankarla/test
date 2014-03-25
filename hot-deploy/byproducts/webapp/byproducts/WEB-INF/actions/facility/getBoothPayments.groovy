import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

dctx = dispatcher.getDispatchContext();
Map boothsPaymentsDetail = [:];
paymentDate ="";
hideSearch ="Y";
paymentIds = FastList.newInstance();
onlyCurrentDues = Boolean.TRUE;
if(parameters.paymentDate){
	paymentDate = parameters.paymentDate;
}else{
	paymentDate = context.paymentDate;
}
if(parameters.statusId){
	statusId = parameters.statusId;
}else{
	statusId = context.statusId;
}
if(parameters.facilityId){
	facilityId = parameters.facilityId;
}else{
	facilityId = context.facilityId;
}
if(facilityId){
	facility = delegator.findOne("Facility",[facilityId : facilityId], false);
	if (facility == null) {
		Debug.logInfo("Booth '" + facilityId + "' does not exist!","");
		context.errorMessage = "Booth '" + facilityId + "' does not exist!";
		return;
	}
}

if(parameters.paymentMethodTypeId){
	paymentMethodTypeId = parameters.paymentMethodTypeId;
}else{
	paymentMethodTypeId = context.paymentMethodTypeId;
}

Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();

SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
try {
	paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + paymentDate, "");
   
}
JSONObject boothsDuesDaywiseJSON = new JSONObject();

Map resultCtx = ByProductNetworkServices.getPartyProfileDafult(dctx, UtilMisc.toMap("boothIds", []));
Map paymentTypeFacilityMap = (Map)resultCtx.get("paymentTypeFacilityMap");
filterFacilityList = [];
if(paymentMethodTypeId){
	filterFacilityList =  paymentTypeFacilityMap.get(paymentMethodTypeId);
}

if(parameters.hideSearch){
	hideSearch = parameters.hideSearch;
}
if(context.hideSearch){
	hideSearch = context.hideSearch;
}
if(parameters.onlyCurrentDues){
	onlyCurrentDues = Boolean.parseBoolean(parameters.onlyCurrentDues);
}
if(context.onlyCurrentDues){
	onlyCurrentDues = Boolean.parseBoolean(context.onlyCurrentDues);
}
if(statusId =="PAID"){
	invoiceStatusId = "INVOICE_PAID";	
}else{
	invoiceStatusId = null;
}
if(parameters.paymentIds){
	paymentIds = parameters.paymentIds;	
}
partyProfileDefault = delegator.findList("PartyProfileDefault", null, UtilMisc.toSet("defaultPayMeth"), null, null, false);
paymentTypes = EntityUtil.getFieldListFromEntityList(partyProfileDefault, "defaultPayMeth", true);
paymentMethodType = delegator.findList("PaymentMethodType", EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.IN, paymentTypes), null, null, null, false);
context.paymentMethodType = paymentMethodType;

boothPaymentsList=[];
if(hideSearch == "N"){
	if (statusId == "PAID") {
		boothsPaymentsDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , [paymentDate:paymentDate , facilityId:facilityId , paymentMethodTypeId:paymentMethodTypeId , paymentIds : paymentIds]);
		boothTempPaymentsList = boothsPaymentsDetail["paymentsList"];
	}
	else {
		boothsPaymentsDetail = ByProductNetworkServices.getBoothPayments( delegator ,dispatcher, userLogin ,paymentDate , invoiceStatusId,facilityId ,paymentMethodTypeId , onlyCurrentDues);
		boothTempPaymentsList = boothsPaymentsDetail["boothPaymentsList"];
	}
	
	boothPaymentsList = [];
	boothTempPaymentsList.each{boothPay ->
		facilityId = boothPay.get("facilityId");
		if(!filterFacilityList || (filterFacilityList && filterFacilityList.contains(facilityId))){
			boothPaymentsList.add(boothPay);
		}
	}
	
	context.boothPaymentsList = boothPaymentsList;	
	context.paymentDate= paymentDate;
	context.paymentTimestamp= paymentTimestamp;
	context.invoicesTotalAmount = UtilFormatOut.formatCurrency(boothsPaymentsDetail["invoicesTotalAmount"], context.get("currencyUomId"), locale);
	if(boothsPaymentsDetail["invoicesTotalDueAmount"] != null){
		context.invoicesTotalDueAmount = UtilFormatOut.formatCurrency(boothsPaymentsDetail["invoicesTotalDueAmount"], context.get("currencyUomId"), locale);
	}
	
	// now get the past dues breakup
	boothsDuesDayWise = [:];
	
	if (!onlyCurrentDues) {
		boothPaymentsList.each { booth ->
			boothDuesDetail = ByProductNetworkServices.getDaywiseBoothDues(dctx, [userLogin: userLogin, facilityId:booth.facilityId]);	
			duesList = boothDuesDetail["boothDuesList"];
			JSONArray boothDuesList= new JSONArray();
			duesList.each { due ->
				JSONObject dueJSON = new JSONObject();
				dueJSON.put("supplyDate", UtilDateTime.toDateString(due.supplyDate, "dd MMM, yyyy"));
				dueJSON.put("amount", due.amount);
				dueJSON.put("amount", UtilFormatOut.formatCurrency(due.amount, context.get("currencyUomId"), locale));
				boothDuesList.add(dueJSON);
			}
			JSONObject boothDuesMap = new JSONObject();
			boothDuesMap.put("totalAmount", UtilFormatOut.formatCurrency(boothDuesDetail["totalAmount"], context.get("currencyUomId"), locale));
			boothDuesMap.put("boothDuesList", boothDuesList);
			boothsDuesDaywiseJSON.put(booth.facilityId, boothDuesMap);
		}
//Debug.logInfo("boothsDuesDaywiseJSON="+boothsDuesDaywiseJSON,"");		
	}	
}
context.boothsDuesDaywiseJSON = boothsDuesDaywiseJSON;

context.statusId = statusId;



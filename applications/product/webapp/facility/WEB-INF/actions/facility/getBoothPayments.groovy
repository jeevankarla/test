import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

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
if(hideSearch == "N"){	
	if (statusId == "PAID") {
		boothsPaymentsDetail = NetworkServices.getBoothPaidPayments( dctx , [paymentDate:paymentDate , facilityId:facilityId , paymentMethodTypeId:paymentMethodTypeId , paymentIds : paymentIds]);
	}
	else {
		boothsPaymentsDetail = NetworkServices.getBoothPayments( delegator ,dispatcher, userLogin ,paymentDate , invoiceStatusId,facilityId ,paymentMethodTypeId , onlyCurrentDues);	
	}
	boothPaymentsList = boothsPaymentsDetail["boothPaymentsList"];
	
	//now change the boothPaymentsList to show rounded or unrounded values based on the tenant configuration
	enablePaymentRounding = delegator.findOne("TenantConfiguration", [propertyTypeEnumId:"LMS", propertyName:"enablePaymentRounding"], true);
	if( enablePaymentRounding && enablePaymentRounding.propertyValue == 'N'  && boothsPaymentsDetail["boothPaymentsUnRoundedList"] != null ){
		boothPaymentsList = boothsPaymentsDetail["boothPaymentsUnRoundedList"];
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
			boothDuesDetail = NetworkServices.getDaywiseBoothDues(dctx, [userLogin: userLogin, facilityId:booth.facilityId]);	
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
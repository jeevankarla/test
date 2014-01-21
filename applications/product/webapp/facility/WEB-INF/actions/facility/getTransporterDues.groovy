import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import org.ofbiz.network.LmsServices;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();
Map boothsPaymentsDetail = [:];
supplyDate = UtilDateTime.nowTimestamp();
hideSearch ="Y";
paymentIds = FastList.newInstance();
onlyCurrentDues = Boolean.TRUE;
Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();

SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
if(parameters.fromDate){
	try {
		fromDate = UtilDateTime.toTimestamp(dateFormat.parse(parameters.fromDate));
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.fromDate, "");
	   
	}
	//supplyDate = parameters.supplyDate;
}else{
	fromDate = context.fromDate;
}

if(parameters.thruDate){
	try {
		thruDate = UtilDateTime.toTimestamp(dateFormat.parse(parameters.thruDate));
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.thruDate, "");
	   
	}
	//supplyDate = parameters.supplyDate;
}else{
	thruDate = context.thruDate;
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
		context.errorMessage = "Route '" + facilityId + "' does not exist!";
		return;
	}
}

if(parameters.hideSearch){
	hideSearch = parameters.hideSearch;
}
if(context.hideSearch){
	hideSearch = context.hideSearch;
}


if(hideSearch == "N"){
	context.fromDate = fromDate;
	context.thruDate = thruDate;
	context.facilityId = facilityId;
	transporterDuesMap = LmsServices.getTransporterDues(dctx , context);	
	context.transporterDuesList = transporterDuesMap["transporterDuesList"];
	context.transporterDueInvoiceList =   transporterDuesMap["transporterDueInvoiceList"];
}



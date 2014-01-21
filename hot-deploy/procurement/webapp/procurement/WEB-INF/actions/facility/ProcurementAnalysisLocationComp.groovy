import org.ofbiz.base.util.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementReports;
import net.sf.json.JSONArray;

JSONArray procDataListJSON = new JSONArray();
JSONArray procFatDataListJSON = new JSONArray();
JSONArray procSnfDataListJSON = new JSONArray();
JSONArray labelsJSON = new JSONArray();
context.procDataListJSON = procDataListJSON;
context.procFatDataListJSON = procFatDataListJSON;
context.procSnfDataListJSON = procSnfDataListJSON;
context.labelsJSON = labelsJSON;
dctx = dispatcher.getDispatchContext();
fromDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.procurementDate) {
		procurementDate = new java.sql.Timestamp(sdf.parse(parameters.procurementDate).getTime());
		fromDate = UtilDateTime.getDayStart(procurementDate);
		thruDate = UtilDateTime.getDayEnd(procurementDate);
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
facilityId = parameters.facilityId;
//Debug.logInfo("fromDate="+ fromDate + "; thruDate=" + thruDate + "; facilityId=" + facilityId, "");

if(facilityId){
	facility = delegator.findOne("Facility",[facilityId : facilityId], false);
	if (facility == null) {
		Debug.logInfo("Facility '" + facilityId + "' does not exist!","");
		context.errorMessage = "Facility '" + facilityId + "' does not exist!";
		return;
	}
	if (facility.facilityTypeId != "SHED" && facility.facilityTypeId != "UNIT" && facility.facilityTypeId != "PLANT" && facility.facilityTypeId != "PROC_ROUTE") {
		Debug.logInfo("Facility '" + facilityId + "' is not of type plant/shed/unit/route!","");
		context.errorMessage = "Facility '" + facilityId + "' is not of type plant/unit/route!";
		return;
	}
}
else {
	return;
}


int i = 1;
childFacilities = delegator.findByAnd("Facility", [parentFacilityId : facility.facilityId]);
childFacilities.each { childFacility ->	
	totalsMap = ProcurementReports.getPeriodTotals(dctx, [fromDate:fromDate, thruDate:thruDate, facilityId:childFacility.facilityId]);
//Debug.logInfo("totalsMap="+ totalsMap, "");
	facilityMap = totalsMap.get(childFacility.facilityId);
	if (facilityMap != null) {
		dayTotalsMap = facilityMap.get("dayTotals");
		dateMap = dayTotalsMap.get("TOT");
		supplyMap = dateMap.get("TOT");
		productMap = supplyMap.get("TOT");
		qtyKgs = productMap.get("qtyKgs");
		fat = productMap.get("fat");
		snf = productMap.get("snf");
		JSONArray dayList= new JSONArray();
		JSONArray dayFatList= new JSONArray();
		JSONArray daySnfList= new JSONArray();
		dayList.add(i);
		dayList.add(qtyKgs);
		procDataListJSON.add(dayList);
		dayFatList.add(i);
		dayFatList.add(fat);
		procFatDataListJSON.add(dayFatList);
		daySnfList.add(i);
		daySnfList.add(snf);
		procSnfDataListJSON.add(daySnfList);
		JSONArray labelsList= new JSONArray();
		labelsList.add(i);
		labelsList.add(childFacility.facilityName);
		labelsJSON.add(labelsList);
		++i;
	}
}
//Debug.logInfo("procDataListJSON="+ procDataListJSON, "");
//Debug.logInfo("labelsJSON="+ labelsJSON, "");
context.facility = facility;
context.procDataListJSON = procDataListJSON;
context.procFatDataListJSON = procFatDataListJSON;
context.procSnfDataListJSON = procSnfDataListJSON;
context.labelsJSON = labelsJSON;



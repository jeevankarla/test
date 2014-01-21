import org.ofbiz.base.util.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementReports;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	else {
		fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	}
	if (parameters.thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
	else {
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
facilityId = parameters.facilityId;
//Debug.logInfo("fromDate="+ fromDate + "; thruDate=" + thruDate + "; facilityId=" + facilityId, "");

procurementEntryList = [];
JSONArray listJSON= new JSONArray();
JSONArray listCMJSON= new JSONArray();
JSONArray listBMJSON= new JSONArray();
if (facilityId == null) {
	// Don't do anything for now
	context.procurementEntryList = procurementEntryList;
	context.listJSON=listJSON;
	context.listBMJSON=listBMJSON;
	context.listCMJSON=listCMJSON;
	return
}
facility = delegator.findOne("Facility", [facilityId : facilityId], false);
if (!facility) {
	// Don't do anything for now
	context.errorMessage = "Facility '" + facilityId + "'does not exist!";
	context.procurementEntryList = procurementEntryList;
	context.listJSON=listJSON;
	context.listBMJSON=listBMJSON;
	context.listCMJSON=listCMJSON;
	return
}

totalsMap = ProcurementReports.getPeriodTotals(dctx, [fromDate:fromDate, thruDate:thruDate, facilityId:facilityId]);

//Debug.logInfo("totalsMap="+ totalsMap, "");
if(UtilValidate.isNotEmpty(totalsMap)){
	facilityMap = totalsMap.get(facilityId);
	if (facilityMap != null) {
		dayTotalsMap = facilityMap.get("dayTotals");
		dateIter = dayTotalsMap.entrySet().iterator();
		while (dateIter.hasNext()) {
			Map.Entry dateEntry = dateIter.next();
			Map supplyTypeMap= new LinkedHashMap();
			supplyTypeMapTemp = dateEntry.getValue();
			supplyTypeMap.put("TOT", supplyTypeMapTemp.get("TOT"));
			supplyTypeMap.put("AM", supplyTypeMapTemp.get("AM"));
			supplyTypeMap.put("PM", supplyTypeMapTemp.get("PM"));
			supplyTypeIter = supplyTypeMap.entrySet().iterator();
			while (supplyTypeIter.hasNext()) {
				Map.Entry supplyTypeEntry = supplyTypeIter.next();
				productMap = supplyTypeEntry.getValue();
				productMapIter = productMap.entrySet().iterator();
				while (productMapIter.hasNext()) {
					Map.Entry productMapEntry = productMapIter.next();
					fieldsMap = productMapEntry.getValue();
					JSONArray dayList= new JSONArray();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
					allFieldsMap = [:];
					allFieldsMap.put("date", dateEntry.getKey());
					allFieldsMap.put("supplyType", supplyTypeEntry.getKey());
					allFieldsMap.put("product", productMapEntry.getKey());
					allFieldsMap.putAll(fieldsMap);
					if ((supplyTypeEntry.getKey() != "TOT" && productMapEntry.getKey() != "TOT")|| (supplyTypeEntry.getKey() == "TOT" && productMapEntry.getKey() == "TOT")) {
						procurementEntryList.add(allFieldsMap);
					}
					if (dateEntry.getKey() != "TOT" && supplyTypeEntry.getKey() == "TOT") {
						try {
							dateTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(dateEntry.getKey()));
							dayList.add(dateTimestamp.getTime());
							dayList.add(allFieldsMap.qtyKgs);
							if (productMapEntry.getKey() == "Buffalo Milk") {
								listBMJSON.add(dayList);
							}
							else if (productMapEntry.getKey() == "Cow Milk") {
								listCMJSON.add(dayList);
							}
							else if (productMapEntry.getKey() == "TOT") {
								listJSON.add(dayList);
							}							
						} catch (ParseException e) {
							Debug.logError(e, "Cannot parse date string: " + dateEntry.getKey(), "");
						}	   
					}
				}  //while
			}  //while
		}  //while
	}  //if
}//if
//Debug.logInfo("procurementEntryList="+ procurementEntryList, "");
//Debug.logInfo("listJSON="+ listJSON, "");
//Debug.logInfo("listBMJSON="+ listBMJSON, "");
//Debug.logInfo("listCMJSON="+ listCMJSON, "");
context.facility = facility;
context.procurementEntryList = procurementEntryList;
context.listJSON=listJSON;
context.listBMJSON=listBMJSON;
context.listCMJSON=listCMJSON;


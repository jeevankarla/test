import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import java.sql.Date;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import in.vasista.vbiz.procurement.ProcurementReports;
import net.sf.json.JSONArray;
import java.text.ParseException;
import java.text.SimpleDateFormat;

dctx = dispatcher.getDispatchContext();

//::TODO:: for now limit this to only 2 weeks
startTime =  UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -4);
endTime =  UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		startTime = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	if (parameters.thruDate) {
		endTime = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
//Debug.logInfo("startTime="+startTime+"; endTime=" + endTime, "");
JSONArray listJSON= new JSONArray();
iterTime = startTime;
if(UtilValidate.isEmpty(parameters.facilityId)){
	parameters.facilityId = "MAIN_PLANT";
}

facilityId = parameters.facilityId;
facility = delegator.findOne("Facility", [facilityId : facilityId], false);
context.facility = facility;

while (iterTime <= endTime) {
	JSONArray dayList= new JSONArray();
	dayList.add(iterTime.getTime());
	fromDate = iterTime;
	thruDate = UtilDateTime.getDayEnd(iterTime);
	totalsMap = ProcurementReports.getPeriodTotals(dctx, [fromDate:fromDate, thruDate:thruDate, facilityId:facilityId]);
	//Debug.logInfo("totalsMap="+ totalsMap, "");
	facilityMap = totalsMap.get(facilityId);
	if (facilityMap != null) {
		dayTotalsMap = facilityMap.get("dayTotals");
		dateMap = dayTotalsMap.get("TOT");
		supplyMap = dateMap.get("TOT");
		productMap = supplyMap.get("TOT");
		qtyKgs = productMap.get("qtyKgs");
		dayList.add(qtyKgs);
	}
	listJSON.add(dayList);
	iterTime = UtilDateTime.addDaysToTimestamp(iterTime, 1);
}

//Debug.logInfo("listJSON="+listJSON, "");
context.listJSON=listJSON;

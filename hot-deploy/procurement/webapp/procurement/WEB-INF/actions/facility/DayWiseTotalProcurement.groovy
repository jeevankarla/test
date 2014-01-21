import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import java.sql.Date;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import in.vasista.vbiz.procurement.ProcurementReports;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();

//::TODO:: for now limit this to only 2 weeks
startTime =  UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -30);
endTime =  UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);

//Debug.logInfo("startTime="+startTime+"; endTime=" + endTime, "");
JSONArray listJSON= new JSONArray();
iterTime = startTime;
while (iterTime <= endTime) {
	JSONArray dayList= new JSONArray();
	dayList.add(iterTime.getTime());
	fromDate = iterTime;
	thruDate = UtilDateTime.getDayEnd(iterTime);
	totalsMap = ProcurementReports.getPeriodTotals(dctx, [fromDate:fromDate, thruDate:thruDate, facilityId:"MAIN_PLANT"]);
	//Debug.logInfo("totalsMap="+ totalsMap, "");
	facilityMap = totalsMap.get("MAIN_PLANT");
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

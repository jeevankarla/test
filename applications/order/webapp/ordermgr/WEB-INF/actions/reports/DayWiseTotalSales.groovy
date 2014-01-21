import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import java.sql.Date;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.network.NetworkServices;
import net.sf.json.JSONArray;
import java.math.RoundingMode;

rounding = RoundingMode.HALF_UP;
startTime =  UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);
endTime =  UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);
ecl = EntityCondition.makeCondition([
	EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"),
	EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
    EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_EQUAL, null)],
EntityOperator.AND);
EntityFindOptions findOptions = new EntityFindOptions();
findOptions.setMaxRows(1);
headers = delegator.findList("OrderHeader", ecl, null, [ "estimatedDeliveryDate"], findOptions, false);
//Debug.logInfo("headers="+headers, "");

if (headers.size() == 1) {	
	firstOrderDate = headers.get(0).estimatedDeliveryDate;
	startTime = UtilDateTime.getDayEnd(firstOrderDate, timeZone, locale);
	startTime = UtilDateTime.addDaysToTimestamp(startTime, 1);
}
//Debug.logInfo("startTime="+startTime+"; endTime=" + endTime, "");
JSONArray listJSON= new JSONArray();
JSONArray listRevJSON= new JSONArray();

iterTime = startTime;
while (iterTime <= endTime) {
	salesDate = new Date(iterTime.getTime());
	salesSummary = delegator.findOne("LMSSalesHistorySummary", UtilMisc.toMap("salesDate", salesDate), false);
	JSONArray dayList= new JSONArray();
	dayList.add(iterTime.getTime());
	JSONArray dayRevList= new JSONArray();
	dayRevList.add(iterTime.getTime());
	if (salesSummary) {
		dayList.add(salesSummary.totalQuantity);
		dayRevList.add(((salesSummary.totalRevenue).divide(new BigDecimal(100000))).setScale(1, rounding));
//Debug.logInfo("salesSummary="+salesSummary, "");
	}
	else {
		dayTotals = NetworkServices.getDayTotals(dispatcher.getDispatchContext(), iterTime, true, false);
//Debug.logInfo("dayTotals="+dayTotals, "");
		dayList.add(dayTotals.totalQuantity);
		dayRevList.add(((dayTotals.totalRevenue).divide(new BigDecimal(100000))).setScale(2, rounding));
	}
	listJSON.add(dayList);
	listRevJSON.add(dayRevList);
	iterTime = UtilDateTime.addDaysToTimestamp(iterTime, 1);
}

//Debug.logInfo("listJSON="+listJSON, "");
context.listJSON=listJSON;
context.listRevJSON=listRevJSON;


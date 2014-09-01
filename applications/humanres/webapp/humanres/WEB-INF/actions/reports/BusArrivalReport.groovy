import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.EmplLeaveService;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresApiService;
import org.ofbiz.party.party.PartyHelper;
import javolution.util.FastList;
import javolution.util.FastMap;

dctx = dispatcher.getDispatchContext();

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.BusfromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.BusfromDate).getTime()));
	}
	if (parameters.BusthruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.BusthruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

context.put("fromDate",fromDate);
context.put("thruDate",thruDate);

totalDays=UtilDateTime.getIntervalInDays(fromDate,thruDate);
if(totalDays > 31){
Debug.logError("You Cannot Choose More Than 31 Days.","");
context.errorMessage = "You Cannot Choose More Than 31 Days";
return;
}
List currentDateKeysList = [];
totalDays=totalDays+1;
for(int i=0; i <totalDays; i++){
currentDayTimeStart = UtilDateTime.getDayStart(fromDate, i);
currentDayTimeEnd = UtilDateTime.getDayEnd(currentDayTimeStart);
date = UtilDateTime.toDateString(currentDayTimeStart);
currentDateKeysList.add(date);
}
context.putAt("currentDateKeysList", currentDateKeysList);

shiftList = delegator.findList("WorkShiftType", null, null, null, null, false);
shiftTypeIds = EntityUtil.getFieldListFromEntityList(shiftList, "shiftTypeId", true);

Map finalMap=FastMap.newInstance();
currentDateKeysList.each{ date ->
	Map BusArrivalMap=FastMap.newInstance();
	Map finalBusArrivalMap=FastMap.newInstance();
	shiftTypeIds.each{ shift ->
		List conditionList=[];
		conditionList.add(EntityCondition.makeCondition("date", EntityOperator.EQUALS, UtilDateTime.toSqlDate(date)));
		conditionList.add(EntityCondition.makeCondition("shiftType", EntityOperator.EQUALS, shift));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		BusTimingsDetails = delegator.findList("DailyBusTimings", condition , null, null, null, false );
		if(UtilValidate.isNotEmpty(BusTimingsDetails)){
			BusTimingsDetails.each{ BusDetail ->
				Map shiftMap=FastMap.newInstance();
				List shiftConditionList=[];
				shiftConditionList.add(EntityCondition.makeCondition("shiftTypeId", EntityOperator.EQUALS, BusDetail.shiftType));
				shiftConditionList.add(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"));
				shiftCondition=EntityCondition.makeCondition(shiftConditionList,EntityOperator.AND);
				shiftTimingsDetails = delegator.findList("WorkShiftTypePeriodAndMap", shiftCondition , null, null, null, false );
				if(UtilValidate.isNotEmpty(shiftTimingsDetails)){
					shiftTimingsDetails.each{ shiftDetail ->
						shiftTime=shiftDetail.startTime;
						shiftMap.put("shiftTime",shiftTime);
					}
				}
				arrivalTime=BusDetail.inTime;
				shiftMap.put("arrivalTime",arrivalTime);
				shiftMap.put("shift",BusDetail.shiftType);
				BusArrivalMap.put(shift,shiftMap);
				
			}
		}
	}
	if(UtilValidate.isNotEmpty(BusArrivalMap)){
		finalMap.put(date,BusArrivalMap);
	}
}
context.put("finalMap",finalMap);

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

holidyDate = parameters.holidayDate;
organizaionId = parameters.organizationPartyId;
customTimePeriodId = parameters.customTimePeriodId;

dateStr = null;
holidayDatestart = null;
def sdf1 = new SimpleDateFormat("dd-MM-yyyy");
if(UtilValidate.isNotEmpty(holidyDate)){
	try {
		if (holidyDate) {
			dateTimestamp = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(holidyDate).getTime()));
			//dateTimestamp = new java.sql.Timestamp(sdf1.parse(holidyDate).getTime());
			dateStr = UtilDateTime.toDateString(dateTimestamp,"yyyy-MM-dd");
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	def sdf = new SimpleDateFormat("yyyy-MM-dd");
	holidayDatestart = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
}


weekMap=[:];
weekMap["1"]="Sunday";
weekMap["2"]="Monday";
weekMap["3"]="Tuesday";
weekMap["4"]="Wednesday";
weekMap["5"]="Thursday";
weekMap["6"]="Friday";
weekMap["7"]="Saturday";
holidayDetailsList = [];

//holidayDatestart = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(dateStr));
//holidayDatestart = UtilDateTime.toTimestamp(dateStr);
holidayDetails = [];
if(UtilValidate.isNotEmpty(customTimePeriodId)){
	conditionList = [];
	if(UtilValidate.isNotEmpty(customTimePeriodId)){
		conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId ));
	}
	if(UtilValidate.isNotEmpty(organizaionId)){
		conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizaionId ));
	}
	if(UtilValidate.isNotEmpty(holidayDatestart)){
		conditionList.add(EntityCondition.makeCondition("holiDayDate", EntityOperator.EQUALS,holidayDatestart));
	}
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	//holidayList = [];
	holidayDetails = delegator.findList("HolidayCalendar", condition , null, null, null, false );
}

if(UtilValidate.isNotEmpty(holidayDetails)){
	holidayDetails.each { holiday ->
		holidayDetailsMap = [:];
		holidayDetailsMap.put("customTimePeriodId", holiday.get("customTimePeriodId"));
		holidayDetailsMap.put("organizationPartyId", holiday.get("organizationPartyId"));
		holidayDetailsMap.put("holiDayDate", holiday.get("holiDayDate"));
		holidayDetailsMap.put("description", holiday.get("description"));
		holidayDate = holiday.get("holiDayDate");
		String dayOfWeek = (UtilDateTime.getDayOfWeek(holidayDate, timeZone, locale)).toString();
		String weekDay = weekMap[dayOfWeek];
		if(UtilValidate.isNotEmpty(weekDay)){
			holidayDetailsMap.put("weekDay", weekDay);
		}
		holidayDetailsList.addAll(holidayDetailsMap);
	}
}

context.put("holidayDetailsList",holidayDetailsList);

String yearStart ="";
String yearEnd ="";
String year ="";

GenericValue customTimePeriodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId : customTimePeriodId], false);
if(UtilValidate.isNotEmpty(customTimePeriodDetails)){
	timePeriodStart=UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("fromDate"));
	timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("thruDate"));
	yearStart = UtilDateTime.toDateString(timePeriodStart,"MMM");
	yearEnd = UtilDateTime.toDateString(timePeriodEnd,"MMM");
	year = UtilDateTime.toDateString(timePeriodEnd,"yyyy");
}

context.put("yearStart",yearStart);
context.put("yearEnd",yearEnd);
context.put("year",year);


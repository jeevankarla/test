import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.RowFilter.NotFilter;

import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();
emplList=[];
holidaysList=[];
workedHolidaysList=[];
holidays=[];
partyId=parameters.partyId;
timePeriodId=parameters.customTimePeriodId;
if(UtilValidate.isNotEmpty(timePeriodId)){
	context.timePeriodId=timePeriodId;
		dates=delegator.findOne("CustomTimePeriod", [customTimePeriodId:timePeriodId], false);
		fromDate=UtilDateTime.toDateString(dates.get("fromDate"), "MMM dd, yyyy");
		thruDate=UtilDateTime.toDateString(dates.get("thruDate"), "MMM dd, yyyy");
		fromDateStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		thruDateEnd= UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		def sdf = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			if (fromDate) {
				fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
			}
			if (thruDate) {
				thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
			}
		} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
		}
		
	partyId=parameters.partyId;
	
	resultMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:fromDateStart,timePeriodEnd:thruDateEnd,timePeriodId:timePeriodId,locale:locale]);
	lastClosePeriod=resultMap.get("lastCloseAttedancePeriod");
	if(UtilValidate.isNotEmpty(lastClosePeriod)){
		customTimePeriodId=lastClosePeriod.get("customTimePeriodId");
		context.customTimePeriodId=customTimePeriodId;
		attenDates=delegator.findOne("CustomTimePeriod", [customTimePeriodId:customTimePeriodId], false);
		fromDate=UtilDateTime.toDateString(attenDates.get("fromDate"), "MMM dd, yyyy");
		thruDate=UtilDateTime.toDateString(attenDates.get("thruDate"), "MMM dd, yyyy");
		fromDateStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		thruDateEnd= UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		def sdfn = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			if (fromDate) {
				fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdfn.parse(fromDate).getTime()));
			}
			if (thruDate) {
				thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdfn.parse(thruDate).getTime()));
			}
		} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
		}
		List conditionList=[];
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
		conditionList.add(EntityCondition.makeCondition("date",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(fromDateStart)));
		conditionList.add(EntityCondition.makeCondition("date",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(thruDateEnd)));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		emplList = delegator.findList("EmplDailyAttendanceDetail", condition ,null,null, null, false );
		
		
		List holidayconditionList=[];
		holidayconditionList.add(EntityCondition.makeCondition("holiDayDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDateStart));
		holidayconditionList.add(EntityCondition.makeCondition("holiDayDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDateEnd));
		holidaycondition=EntityCondition.makeCondition(holidayconditionList,EntityOperator.AND);
		holidaysList = delegator.findList("HolidayCalendar", holidaycondition ,null,null, null, false );
		
		secondSaturDay = UtilDateTime.addDaysToTimestamp(UtilDateTime.getWeekStart(UtilDateTime.getMonthStart(thruDateEnd),0,2,timeZone,locale), -1);
		if(UtilValidate.isNotEmpty(holidaysList)){
			holidaysList.each{ days ->
				holidays.add(UtilDateTime.toSqlDate(days.get("holiDayDate")));
			}
		}
		holidays.add(UtilDateTime.toSqlDate(secondSaturDay));
		List conList=[];
		conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
		conList.add(EntityCondition.makeCondition("date",EntityOperator.IN,holidays));
		con=EntityCondition.makeCondition(conList,EntityOperator.AND);
		workedHolidaysList = delegator.findList("EmplDailyAttendanceDetail", con ,null,null, null, false );
		
		
	}
}
context.emplList=emplList;
context.holidaysList=workedHolidaysList;


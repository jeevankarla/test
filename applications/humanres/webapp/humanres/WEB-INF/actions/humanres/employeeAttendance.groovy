import java.util.Locale;
import java.sql.Timestamp;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import java.util.concurrent.TimeUnit;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.PayrollService;

dctx = dispatcher.getDispatchContext();
SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
def dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

def sdf = new SimpleDateFormat("dd/MM/yyyy");
Timestamp fromDate = null;
Timestamp thruDate = null;
employeeId = parameters.employeeId;
Locale locale = new Locale("en","IN");

try {
	if (parameters.fromDate) {
		context.fromDate = parameters.fromDate;
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	if (parameters.thruDate) {
		context.thruDate = parameters.thruDate;
		thruDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
}
if (thruDate == null) {
	thruDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
}
if (fromDate == null) {
	fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(thruDate, -45));
}

attendanceDaysSet = [:] as HashSet;
//String punchDateStr = UtilDateTime.toDateString(punchDate, "dd/MM/yyyy");			
String employeeName = PartyHelper.getPartyName(delegator, employeeId, false);

Timestamp timePeriodStart = UtilDateTime.getDayStart(fromDate);
Timestamp timePeriodEnd = UtilDateTime.getDayEnd(thruDate);
JSONArray punchListJSON = new JSONArray();
JSONArray oodPunchListJSON = new JSONArray();
conditionList=[];
conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodStart)));
conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodEnd)));
// currently other punctypes such as OOD are not handled
//conditionList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS , "Normal")); 
conditionList.add(EntityCondition.makeCondition("partyId", employeeId));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderBy = UtilMisc.toList("-punchdate","-punchtime");
punchList = delegator.findList("EmplPunch", condition, null, orderBy, null, false);

normalPunchList=  EntityUtil.filterByCondition(punchList, EntityCondition.makeCondition("PunchType",EntityOperator.EQUALS , "Normal"));
def outTimestamp = null;
normalPunchList.each { punch->
	String partyId= punch.getString("partyId");
	String partyName = PartyHelper.getPartyName(delegator, partyId, false);
	String punchTime = timeFormat.format(punch.get("punchtime"));
	String inOut = "";
	if (punch.getString("InOut")) {
		inOut = punch.getString("InOut");
	}
	if (inOut.equals("OUT")) {
		outTimestamp = UtilDateTime.toDateString(punch.get("punchdate"), "dd/MM/yyyy") + " " + punchTime;
		return;
	}
	if (inOut.equals("IN")) {
		inTimestamp = UtilDateTime.toDateString(punch.get("punchdate"), "dd/MM/yyyy") + " " + punchTime;
		JSONArray punchJSON = new JSONArray();	
		//punchJSON.add(partyId);
		//punchJSON.add(partyName);
		punchJSON.add(inTimestamp);
		if (outTimestamp == null) {
			punchJSON.add("");
			punchJSON.add("");
		}
		else {
			punchJSON.add(outTimestamp);
			elapsedHours = (int)(UtilDateTime.getInterval(new java.sql.Timestamp(dateTimeFormat.parse(inTimestamp).getTime()), 
								new java.sql.Timestamp(dateTimeFormat.parse(outTimestamp).getTime()))/(1000*60*60));
							
			elapsedMinutes = (int)((UtilDateTime.getInterval(new java.sql.Timestamp(dateTimeFormat.parse(inTimestamp).getTime()),
								new java.sql.Timestamp(dateTimeFormat.parse(outTimestamp).getTime()))%(1000*60*60))/(1000*60));
			elapsedMinutes = String.format( "%02d", elapsedMinutes );
			punchJSON.add(elapsedHours+":"+elapsedMinutes);				
		}
		punchListJSON.add(punchJSON);
		attendanceDaysSet.add(UtilDateTime.toDateString(punch.get("punchdate"), "dd/MM/yyyy"));
		outTimestamp = null;
	}
}


oodPunchList=  EntityUtil.filterByCondition(punchList, EntityCondition.makeCondition("PunchType",EntityOperator.EQUALS , "Ood"));
def inTimestamp = null;
oodPunchList.each { punch->
	String partyId= punch.getString("partyId");
	String partyName = PartyHelper.getPartyName(delegator, partyId, false);
	String punchTime = timeFormat.format(punch.get("punchtime"));
	String inOut = "";
	if (punch.getString("InOut")) {
		inOut = punch.getString("InOut");
	}
	if (inOut.equals("IN")) {
		inTimestamp = UtilDateTime.toDateString(punch.get("punchdate"), "dd/MM/yyyy") + " " + punchTime;
		return;
	}
	if (inOut.equals("OUT")) {
		outTimestamp = UtilDateTime.toDateString(punch.get("punchdate"), "dd/MM/yyyy") + " " + punchTime;
		JSONArray punchJSON = new JSONArray();
		//punchJSON.add(partyId);
		//punchJSON.add(partyName);
		punchJSON.add(outTimestamp);
		if (inTimestamp == null) {   
			punchJSON.add("");
			punchJSON.add("");
		}
		else {
			punchJSON.add(inTimestamp);
			/*elapsedHours = (UtilDateTime.getInterval(new java.sql.Timestamp(dateTimeFormat.parse(outTimestamp).getTime()),
								new java.sql.Timestamp(dateTimeFormat.parse(inTimestamp).getTime()))/(1000*60*60));
							
							
							
			punchJSON.add(String.format( "%.0f", elapsedHours ));*/
			elapsedHours = (int)(UtilDateTime.getInterval(new java.sql.Timestamp(dateTimeFormat.parse(outTimestamp).getTime()),
				new java.sql.Timestamp(dateTimeFormat.parse(inTimestamp).getTime()))/(1000*60*60));
			
			elapsedMinutes = (int)((UtilDateTime.getInterval(new java.sql.Timestamp(dateTimeFormat.parse(outTimestamp).getTime()),
				new java.sql.Timestamp(dateTimeFormat.parse(inTimestamp).getTime()))%(1000*60*60))/(1000*60));
			elapsedMinutes = String.format( "%02d", elapsedMinutes );
			punchJSON.add(elapsedHours+":"+elapsedMinutes);
		}
		oodPunchListJSON.add(punchJSON);
		attendanceDaysSet.add(UtilDateTime.toDateString(punch.get("punchdate"), "dd/MM/yyyy"));
		inTimestamp = null;
	}
}

JSONArray leaveListJSON = new JSONArray();
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
conditionList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
	EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
//Debug.logError("condition="+condition,"");

leaveList = delegator.findList("EmplLeave", condition , null, null, null, false );
//Debug.logError("leaveList="+leaveList,"");

leaveList.each { leave->
	float leaveCount = 1;
	JSONArray leaveJSON = new JSONArray();
	leaveFromDate = UtilDateTime.toDateString(leave.get("fromDate"), "dd/MM/yyyy");
	leaveJSON.add(leaveFromDate);
	leaveThruDate = "";
	if(UtilValidate.isNotEmpty(leave.get("thruDate"))) {
		leaveThruDate = UtilDateTime.toDateString(leave.get("thruDate"), "dd/MM/yyyy");
	}
	leaveJSON.add(leaveThruDate);
	leaveType = leave.get("leaveTypeId");
	if(UtilValidate.isNotEmpty(leaveType) && UtilValidate.isNotEmpty(leave.getString("dayFractionId"))){
		leaveType = leaveType + " (" + leave.getString("dayFractionId") + ")";
	}
	dayFractionId = leave.get("dayFractionId");
	leaveStartDate = leave.get("fromDate");
	leaveEndDate = leave.get("thruDate");
	leaveCountDays = UtilDateTime.getIntervalInDays(leaveStartDate,leaveEndDate)+1;
	if((leave.get("leaveTypeId")).equals("CL")){
		for(int i=1 ;i < leaveCountDays; i++){
			nextDay = UtilDateTime.addDaysToTimestamp(leaveFromDate, 1);
			String dayOfWeek = (UtilDateTime.getDayOfWeek(nextDay, timeZone, locale)).toString();
			String weekDay = weekMap[dayOfWeek];
			leaveFromDate = nextDay;
			leaveDate = "";
			secondSatDate = "";
			leaveDate = UtilDateTime.toDateString(nextDay,"dd");
			if(weekDay.equals("Saturday")){
				secondSaturDay = UtilDateTime.addDaysToTimestamp(UtilDateTime.getWeekStart(UtilDateTime.getMonthStart(nextDay),0,2,timeZone,locale), -1);
				secondSatDate = UtilDateTime.toDateString(secondSaturDay,"dd");
			}
			if(!leaveDate.equals(secondSatDate) && !weekDay.equals("Sunday")){
				leaveCount = leaveCount + 1;
			}
		}
		if(UtilValidate.isNotEmpty(dayFractionId)){
			if((dayFractionId.equals("FIRST_HALF")) || (dayFractionId.equals("SECOND_HALF"))){
				leaveCount = leaveCount - 0.5;
			}
		}
	}else{
		leaveCount = leaveCountDays;
	}
	
	leaveJSON.add(leaveType);
	leaveJSON.add(leaveCount);
	leaveListJSON.add(leaveJSON);
	Calendar c1=Calendar.getInstance();
	c1.setTime(UtilDateTime.toSqlDate(leave.get("fromDate")));
	Calendar c2=Calendar.getInstance();
	tmpThruDate = leave.get("thruDate");
	if (UtilValidate.isEmpty(tmpThruDate)) {
		tmpThruDate = thruDate;
	}
	c2.setTime(UtilDateTime.toSqlDate(tmpThruDate));
	while(c2.equals(c1) || c2.after(c1)) {
		Timestamp cTime = new Timestamp(c1.getTimeInMillis());
		leaveDate = UtilDateTime.toDateString(cTime, "dd/MM/yyyy");
		attendanceDaysSet.add(leaveDate);
//Debug.logError("leaveDate="+leaveDate,"");		
		c1.add(Calendar.DATE,1);
	}
}


// get companyBus details and weekly off days
companyBus = "";
JSONArray holidaysListJSON = new JSONArray();
employeeDetail = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",employeeId), true);
if(UtilValidate.isNotEmpty(employeeDetail)) { 
	if(UtilValidate.isNotEmpty(employeeDetail.getString("companyBus"))) { 
		if (employeeDetail.getString("companyBus").equalsIgnoreCase("Y")){
			companyBus = "[Company Bus: Yes]";
		}
		else {
			companyBus = "[Company Bus: No]";
		}
	}		
	if (UtilValidate.isNotEmpty(employeeDetail.getString("weeklyOff"))) {
		Calendar c1=Calendar.getInstance();
		c1.setTime(UtilDateTime.toSqlDate(fromDate));
		Calendar c2=Calendar.getInstance();
		c2.setTime(UtilDateTime.toSqlDate(thruDate));
		emplWeeklyOffDay = employeeDetail.getString("weeklyOff");
		while(c2.after(c1)){
			Timestamp cTime = new Timestamp(c1.getTimeInMillis());
			String weekName = (c1.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale));
			if(emplWeeklyOffDay.equalsIgnoreCase(weekName)){
				JSONArray woJSON = new JSONArray();
				woDate = UtilDateTime.toDateString(cTime, "dd/MM/yyyy");			
				woJSON.add(woDate);
				woJSON.add("Weekly Off")
				holidaysListJSON.add(woJSON);
				attendanceDaysSet.add(woDate);
			}
	    	Timestamp secondSaturday = UtilDateTime.addDaysToTimestamp(UtilDateTime.getWeekStart(UtilDateTime.getMonthStart(cTime),0,2,timeZone,locale), -1);
			if (secondSaturday.equals(cTime)) {
				JSONArray ssJSON = new JSONArray();
				ssDate = UtilDateTime.toDateString(cTime, "dd/MM/yyyy");
				ssJSON.add(ssDate);
				ssJSON.add("Second Saturday")
				holidaysListJSON.add(ssJSON);
				attendanceDaysSet.add(ssDate);
			}
			c1.add(Calendar.DATE,1);
		}
	}
}

inputMap = [userLogin:userLogin, fromDate:fromDate, thruDate:thruDate];
resultMap = HumanresService.getGeneralHoliDays(dctx, inputMap);
if (resultMap) {
	generalHolidaysList = resultMap.get("holiDayList");
	generalHolidaysList.each { generalHoliday->
		JSONArray ghJSON = new JSONArray();
		ghDate = UtilDateTime.toDateString(generalHoliday.get("holiDayDate"), "dd/MM/yyyy");
		ghDescription = generalHoliday.get("description");
		ghJSON.add(ghDate);
		ghJSON.add(ghDescription);
		holidaysListJSON.add(ghJSON);
		attendanceDaysSet.add(ghDate);
	}
}

// Check for missing days (no punch, no leaves, no holidays)
JSONArray missingListJSON = new JSONArray();
Calendar c1=Calendar.getInstance();
c1.setTime(UtilDateTime.toSqlDate(fromDate));
Calendar c2=Calendar.getInstance();
c2.setTime(UtilDateTime.toSqlDate(thruDate));
while(c2.after(c1)){
	Timestamp cTime = new Timestamp(c1.getTimeInMillis());
	curDate = UtilDateTime.toDateString(cTime, "dd/MM/yyyy");
	if (!attendanceDaysSet.contains(curDate)) {
		JSONArray missingJSON = new JSONArray();
		missingJSON.add(curDate);
		missingListJSON.add(missingJSON);
	}
	c1.add(Calendar.DATE,1);
}

timePeiodIdsList = [];
Timestamp fromMonthStart=UtilDateTime.getMonthStart(fromDate);
fromMonthEnd = UtilDateTime.getMonthEnd(fromMonthStart, timeZone, locale);
i = 0;

while(i <= 1){
	List fromDateConList=[];
	fromDateConList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
	fromDateConList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromMonthStart)));
	fromDateConList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromMonthEnd)));
	fromMonthCond=EntityCondition.makeCondition(fromDateConList,EntityOperator.AND);
	fromMonthsList = delegator.findList("CustomTimePeriod", fromMonthCond , null, null, null, false );
	if(UtilValidate.isNotEmpty(fromMonthsList)){
		fromMonthsList = EntityUtil.getFirst(fromMonthsList);
		timePeriodId = fromMonthsList.get("customTimePeriodId");
		timePeiodIdsList.add(timePeriodId);
	}
	
	Timestamp thruMonthStart=UtilDateTime.getMonthStart(thruDate);
	thruMonthEnd = UtilDateTime.getMonthEnd(thruMonthStart, timeZone, locale);
	
	fromMonthStart = thruMonthStart;
	fromMonthEnd = thruMonthEnd;
	i=i+1;
}


JSONArray elEncashmentJSON = new JSONArray();
if(UtilValidate.isNotEmpty(timePeiodIdsList)) {
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
	conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, "EL"));
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, timePeiodIdsList));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	emplLeaveELDetails = delegator.findList("EmplLeaveBalanceStatus", condition , null, null, null, false );
	if(UtilValidate.isNotEmpty(emplLeaveELDetails)){
		emplLeaveELDetails.each { eachItem->
			JSONArray elJSON = new JSONArray();
			customTimePeriodId = eachItem.get("customTimePeriodId");
			encashedDays = eachItem.get("encashedDays");

			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : customTimePeriodId], false);
			fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			String monthName = UtilDateTime.toDateString(fromDateStart, "MMM");
			
			if(UtilValidate.isNotEmpty(encashedDays)){
				elJSON.add(monthName);
				elJSON.add(encashedDays);
			}
			if(UtilValidate.isNotEmpty(elJSON)){
				elEncashmentJSON.add(elJSON);
			}
		}
	}
}

//Debug.logError("punchListJSON="+punchListJSON,"");
//Debug.logError("oodPunchListJSON="+oodPunchListJSON,"");
//Debug.logError("leaveListJSON="+leaveListJSON,"");
//Debug.logError("holidaysListJSON="+holidaysListJSON,"");

//Debug.logError("attendanceDaysSet="+attendanceDaysSet,"");
//Debug.logError("missingListJSON="+missingListJSON,"");
context.employeeId = employeeId;
context.employeeName = employeeName;
context.companyBus = companyBus;
context.fromDate = fromDate;
context.thruDate = thruDate;
context.punchListJSON = punchListJSON;
context.oodPunchListJSON = oodPunchListJSON;
context.leaveListJSON = leaveListJSON;
context.holidaysListJSON = holidaysListJSON;
context.missedListJSON = missingListJSON;
context.elEncashmentJSON = elEncashmentJSON;

import org.ofbiz.base.util.UtilValidate;

import java.sql.Timestamp;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();
SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
def dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

def sdf = new SimpleDateFormat("dd/MM/yyyy");
Timestamp fromDate = null;
Timestamp thruDate = null;
employeeId = parameters.employeeId;

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
			elapsedHours = UtilDateTime.getInterval(new java.sql.Timestamp(dateTimeFormat.parse(inTimestamp).getTime()), 
								new java.sql.Timestamp(dateTimeFormat.parse(outTimestamp).getTime()))/(1000*60*60);
			punchJSON.add(String.format( "%.2f", elapsedHours ));				
		}
		punchListJSON.add(punchJSON);
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
			elapsedHours = UtilDateTime.getInterval(new java.sql.Timestamp(dateTimeFormat.parse(outTimestamp).getTime()),
								new java.sql.Timestamp(dateTimeFormat.parse(inTimestamp).getTime()))/(1000*60*60);
			punchJSON.add(String.format( "%.2f", elapsedHours ));
		}
		oodPunchListJSON.add(punchJSON);
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
Debug.logError("condition="+condition,"");

leaveList = delegator.findList("EmplLeave", condition , null, null, null, false );
Debug.logError("leaveList="+leaveList,"");

leaveList.each { leave->
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
	leaveJSON.add(leaveType);
	leaveListJSON.add(leaveJSON);
}


// get companyBus details and weekly off days
companyBus = "No";
JSONArray woListJSON = new JSONArray();
employeeDetail = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",employeeId), true);
if(UtilValidate.isNotEmpty(employeeDetail)) { 
	if(UtilValidate.isNotEmpty(employeeDetail.getString("companyBus")) && 
		employeeDetail.getString("companyBus").equalsIgnoreCase("Y")){
		companyBus = "Yes";
	}
		
	if (UtilValidate.isNotEmpty(employeeDetail.getString("weeklyOff"))) {
		Calendar c1=Calendar.getInstance();
		c1.setTime(UtilDateTime.toSqlDate(fromDate));
		Calendar c2=Calendar.getInstance();
		c2.setTime(UtilDateTime.toSqlDate(thruDate));
		emplWeeklyOffDay = employeeDetail.getString("weeklyOff");
		while(c2.after(c1)){
			String weekName = (c1.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale));
			if(emplWeeklyOffDay.equalsIgnoreCase(weekName)){
				JSONArray woJSON = new JSONArray();
				Timestamp cTime = new Timestamp(c1.getTimeInMillis());			
				woDate = UtilDateTime.toDateString(cTime, "dd/MM/yyyy")			
				woJSON.add(woDate);
				woListJSON.add(woJSON);
			}
			c1.add(Calendar.DATE,1);
		}
	}
}


//Debug.logError("punchListJSON="+punchListJSON,"");
//Debug.logError("oodPunchListJSON="+oodPunchListJSON,"");
//Debug.logError("leaveListJSON="+leaveListJSON,"");
Debug.logError("woListJSON="+woListJSON,"");

context.employeeId = employeeId;
context.employeeName = employeeName;
context.companyBus = companyBus;
context.fromDate = fromDate;
context.thruDate = thruDate;
context.punchListJSON = punchListJSON;
context.oodPunchListJSON = oodPunchListJSON;
context.leaveListJSON = leaveListJSON;
context.woListJSON = woListJSON;
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


fromDate = parameters.fDate;
thruDate = parameters.tDate;

def sdf = new SimpleDateFormat("yyyy-MM-dd");
try {
	if (fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
		parameters.fromDate = fromDate;
	}
	if (thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
		parameters.thruDate =thruDate;
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

if(UtilValidate.isEmpty(parameters.fDate)){
	parameters.fromDate = UtilDateTime.toSqlDate(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-40));
}
/*if(UtilValidate.isEmpty(parameters.tDate)){
	parameters.thruDate =UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp());
}*/

if(UtilValidate.isNotEmpty(parameters.fromDate)){
	fromDate = parameters.fromDate;
}
if(UtilValidate.isNotEmpty(parameters.thruDate)){
	thruDate = parameters.thruDate;
}

/*if (security.hasEntityPermission("MYPORTAL", "_HREMPLVIEW", session)) {
	parameters.partyId = userLogin.partyId;
}*/
List employeeLeaveList = [];
List conditionList=[];

if(UtilValidate.isNotEmpty(parameters.noConditionFind)){
	approverPartyId = parameters.approverPartyId;
}else{
	approverPartyId = context.approverPartyId;
}

if(UtilValidate.isNotEmpty(parameters.emplLeaveApplId)){
	conditionList.add(EntityCondition.makeCondition("emplLeaveApplId", EntityOperator.EQUALS, parameters.emplLeaveApplId));
}

if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
}

if(UtilValidate.isNotEmpty(parameters.leaveTypeId)){
	conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, parameters.leaveTypeId));
}
if(UtilValidate.isNotEmpty(parameters.emplLeaveReasonTypeId)){
	conditionList.add(EntityCondition.makeCondition("emplLeaveReasonTypeId", EntityOperator.EQUALS, parameters.emplLeaveReasonTypeId));
}
if(UtilValidate.isNotEmpty(approverPartyId)){
	conditionList.add(EntityCondition.makeCondition("approverPartyId", EntityOperator.EQUALS, approverPartyId));
}
if(UtilValidate.isEmpty(parameters.leaveStatus)){
	parameters.leaveStatus = "LEAVE_CREATED";
}/*else{
   
	conditionList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_CREATED"));
}*/
conditionList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS,parameters.leaveStatus ));
if(UtilValidate.isNotEmpty(fromDate)){
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
}
if(UtilValidate.isNotEmpty(thruDate)){
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)));
}
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);

leaveDetails = delegator.findList("EmplLeave", condition , null, UtilMisc.toList("-fromDate"), null, false );


weekMap=[:];
weekMap["1"]="Sunday";
weekMap["2"]="Monday";
weekMap["3"]="Tuesday";
weekMap["4"]="Wednesday";
weekMap["5"]="Thursday";
weekMap["6"]="Friday";
weekMap["7"]="Saturday";

employeeLeaveList = [];
compDateMap = [:];
if(UtilValidate.isNotEmpty(leaveDetails)){
	leaveDetails.each{ leave->
		float leaveCount = 1;
		emplLeaveApplId = leave.emplLeaveApplId;
		dayFractionId = leave.dayFractionId;
		leaveFromDate = leave.fromDate;
		leaveThruDate = leave.thruDate;
		leaveCountDays = UtilDateTime.getIntervalInDays(leaveFromDate,leaveThruDate)+1;
		
		if((leave.leaveTypeId).equals("CL")){
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
		
		leaveDetailsMap = [:];
		leaveDetailsMap.put("emplLeaveApplId", leave.emplLeaveApplId);
		leaveDetailsMap.put("partyId", leave.partyId);
		leaveDetailsMap.put("leaveCountDays", leaveCount);
		leaveDetailsMap.put("leaveTypeId", leave.leaveTypeId);
		leaveDetailsMap.put("emplLeaveReasonTypeId", leave.emplLeaveReasonTypeId);
		leaveDetailsMap.put("fromDate", leave.fromDate);
		leaveDetailsMap.put("thruDate", leave.thruDate);
		leaveDetailsMap.put("approverPartyId", leave.approverPartyId);
		leaveDetailsMap.put("leaveStatus", leave.leaveStatus);
		leaveDetailsMap.put("effectedCreditDays", leave.effectedCreditDays);
		leaveDetailsMap.put("lossOfPayDays", leave.lossOfPayDays);
		leaveDetailsMap.put("dayFractionId", leave.dayFractionId);
		leaveDetailsMap.put("GHSSdays", leave.GHSSdays);
		leaveDetailsMap.put("documentsProduced", leave.documentsProduced);
		leaveDetailsMap.put("comment", leave.comment);
		leaveDetailsMap.put("appliedBy", leave.appliedBy);
		employeeLeaveList.addAll(leaveDetailsMap);
		compDateList = [];
		if(UtilValidate.isNotEmpty(emplLeaveApplId)){
			emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail",EntityCondition.makeCondition("emplLeaveApplId", EntityOperator.EQUALS, emplLeaveApplId) , null, null, null, false);
			if(UtilValidate.isNotEmpty(emplDailyAttendanceDetailList)){
				compDateList = EntityUtil.getFieldListFromEntityList(emplDailyAttendanceDetailList,"date",true);
			}
		}
		if(UtilValidate.isNotEmpty(compDateList)){
			tempMap = [:];
			tempMap.put(emplLeaveApplId,compDateList);
			if(UtilValidate.isNotEmpty(tempMap)){
				compDateMap.putAll(tempMap);
			}
		}
	}
}
context.put("employeeLeaveList",employeeLeaveList);
context.put("compDateMap",compDateMap);

emplLeaveApplId = parameters.emplLeaveApplId;
if(UtilValidate.isNotEmpty(emplLeaveApplId)){
	dateList = [];
	if(UtilValidate.isNotEmpty(emplLeaveApplId)){
		emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail",EntityCondition.makeCondition("emplLeaveApplId", EntityOperator.EQUALS, emplLeaveApplId) , null, null, null, false);
		if(UtilValidate.isNotEmpty(emplDailyAttendanceDetailList)){
			dateList = EntityUtil.getFieldListFromEntityList(emplDailyAttendanceDetailList,"date",true);
			if(UtilValidate.isNotEmpty(dateList)){
				context.put("dateList",dateList);
			}
		}
	}
}


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

if (security.hasEntityPermission("MYPORTAL", "_HREMPLVIEW", session)) {
	parameters.partyId = userLogin.partyId;
}
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
}else{
	conditionList.add(EntityCondition.makeCondition("approverPartyId", EntityOperator.EQUALS, userLogin.partyId));
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
context.put("employeeLeaveList",leaveDetails);

compDateMap = [:];
leaveDetails.each{ leave->
	emplLeaveApplId = leave.emplLeaveApplId;
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


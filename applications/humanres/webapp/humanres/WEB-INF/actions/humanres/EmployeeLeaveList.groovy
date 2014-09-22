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
	}
	if (thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

if(UtilValidate.isNotEmpty(parameters.fromDate)){
	fromDate = parameters.fromDate;
}
if(UtilValidate.isNotEmpty(parameters.thruDate)){
	thruDate = parameters.thruDate;
}

List employeeLeaveList = [];
List conditionList=[];

if(UtilValidate.isNotEmpty(parameters.noConditionFind)){
	approverPartyId = parameters.approverPartyId;
}else{
	approverPartyId = context.approverPartyId;
}

if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
}

if(UtilValidate.isNotEmpty(parameters.leaveTypeId)){
	conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, parameters.leaveTypeId));
}else{
	
}
if(UtilValidate.isNotEmpty(parameters.emplLeaveReasonTypeId)){
	conditionList.add(EntityCondition.makeCondition("emplLeaveReasonTypeId", EntityOperator.EQUALS, parameters.emplLeaveReasonTypeId));
}
if(UtilValidate.isNotEmpty(approverPartyId)){
	conditionList.add(EntityCondition.makeCondition("approverPartyId", EntityOperator.EQUALS, approverPartyId));
}
if(UtilValidate.isNotEmpty(parameters.leaveStatus)){
	conditionList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS,parameters.leaveStatus ));
}/*else{
	conditionList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_CREATED"));
}*/
if(UtilValidate.isNotEmpty(fromDate)){
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
}
if(UtilValidate.isNotEmpty(thruDate)){
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)));
}


condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
LeaveDetails = delegator.findList("EmplLeave", condition , null, UtilMisc.toList("-fromDate"), null, false );
context.put("employeeLeaveList",LeaveDetails);
Debug.log("LeaveDetails========="+LeaveDetails);
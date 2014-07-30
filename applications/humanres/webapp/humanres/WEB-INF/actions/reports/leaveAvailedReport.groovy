import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;

import freemarker.core.SequenceBuiltins.sort_byBI;
import in.vasista.vbiz.humanres.EmplLeaveService;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresApiService;


dctx = dispatcher.getDispatchContext();
orderDate=UtilDateTime.nowTimestamp();
context.orderDate=orderDate;

fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
int year=UtilDateTime.getYear(UtilDateTime.nowTimestamp(),timeZone,locale);
context.year=year;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.larFromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.larFromDate).getTime()));
	}
	if (parameters.larThruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.larThruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.fromlarDate=UtilDateTime.toDateString(fromDate,"dd-MM-yyyy");
context.thrularDate=UtilDateTime.toDateString(thruDate,"dd-MM-yyyy");

leaveTypeIds=[];
if(parameters.leaveTypeId=="ALL"){
	leaveList=delegator.findList("EmplLeaveType",null,null,null,null,false);
	leaveList.each{ leaveType ->
		leaveTypeIds.add(leaveType.get("leaveTypeId"));
	}
}
else{
	leaveTypeIds.add(parameters.leaveTypeId);
}


employeesResult=[];
employeeList=[];
empIds=[];
employeesMap=[:];
activeEmpMap=HumanresApiService.getActiveEmployees(dctx,[userLogin:userLogin]);
employeesResult=activeEmpMap.get("employeesResult");
employeeList=employeesResult.get("employeeList");
employeeList.each {employee ->
	empIds.add(employee.get("employeeId"));
	employeesMap.put(employee.get("employeeId"), employee.get("name"));
}

finalMap=[:];
leaveTypeIds.each { leaveTypeId ->
	employeesList=[];
	List conditionList=[];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,empIds));
	conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveTypeId));
	conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	empLeavesList = delegator.findList("EmplLeave", condition ,null,null, null, false );
	empLeavesList.each { empLeaves ->
		employeeMap=[:];
		empLeaveMap=EmplLeaveService.fetchLeaveDaysForPeriod(dctx,[partyId:empLeaves.get("partyId"),leaveTypeId:empLeaves.get("leaveTypeId"),timePeriodStart:fromDate, timePeriodEnd: thruDate,userLogin:userLogin]);
		leaveDetailmap=empLeaveMap.get("leaveDetailmap");
		employeeMap.put("employeeId",empLeaves.get("partyId"));
		employeeMap.put("name", employeesMap.get(empLeaves.get("partyId")));
		employeeMap.put("leaveFrom",UtilDateTime.toDateString(empLeaves.get("fromDate"), "dd-MM-yyyy"));
		employeeMap.put("leaveThru",UtilDateTime.toDateString(empLeaves.get("thruDate"), "dd-MM-yyyy"));
		employeeMap.put("noOfDays",leaveDetailmap.get(leaveTypeId));
		employeeMap.put("leaveTypeId",empLeaves.get("leaveTypeId"));
		leaveBalances = delegator.findByAnd("EmplLeaveBalanceStatus",[partyId:empLeaves.get("partyId"),leaveTypeId:empLeaves.get("leaveTypeId")],["openingBalance"]);
		if(UtilValidate.isNotEmpty(leaveBalances) && leaveTypeId=="CL" || leaveTypeId=="EL" || leaveTypeId=="HPL"){
			employeeMap.put("balance",leaveBalances.get(0).openingBalance);
		}
		if(UtilValidate.isNotEmpty(employeeMap))
		employeesList.add(employeeMap);
	}
	if(UtilValidate.isNotEmpty(employeesList))
	finalMap.put(leaveTypeId, employeesList);
}
if(UtilValidate.isEmpty(finalMap)){
	Debug.logError("No Leaves Found.","");
	context.errorMessage = "No Leaves Found.......!";
	return;
}

context.put("finalMap",finalMap);





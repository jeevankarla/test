
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
import in.vasista.vbiz.humanres.HumanresService;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();
String Date = parameters.lbDate;
employeeId = parameters.partyIdTo;
fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.mclFromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.lbDate).getTime()));
	}
	if (parameters.mclThruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.lbDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.Date=Date;
context.fromDate=UtilDateTime.toDateString(fromDate, "MMM dd, yyyy");
context.thruDate=UtilDateTime.toDateString(thruDate, "MMM dd, yyyy");

employeeList = [];

employmentsList = [];
if(UtilValidate.isEmpty(employeeId)){
	emplInputMap = [:];
	emplInputMap.put("userLogin", userLogin);
	emplInputMap.put("orgPartyId", "Company");
	emplInputMap.put("fromDate", fromDate);
	emplInputMap.put("thruDate", thruDate);
	Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
	employments=EmploymentsMap.get("employementList");
	employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyIdTo"));
	
	if(UtilValidate.isNotEmpty(employments)){
		employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
	}
}else{
	employmentsList.add(employeeId);
}
employmentsList.each { employeeId ->
	tempMap = [:];
	tempMap["employeeId"] = "";
	tempMap["name"] = "";
	tempMap["CL"] = "";
	tempMap["EL"] = "";
	tempMap["HPL"] = "";

	tempMap["employeeId"] = employeeId;
	tempMap["name"] = PartyHelper.getPartyName(delegator, employeeId, false);

		cLBalance=0.00;
		leaveType = "CL";
		inputMap = [:];
		inputMap.put("balanceDate", UtilDateTime.toSqlDate(thruDate));
		inputMap.put("employeeId", employeeId);
		inputMap.put("leaveTypeId", leaveType);
		Map cLEmplLeaveBalanceMap = EmplLeaveService.getEmployeeLeaveBalance(dctx,inputMap);
		if(UtilValidate.isNotEmpty(cLEmplLeaveBalanceMap.get("leaveBalances").get(leaveType))){
			cLBalance=cLEmplLeaveBalanceMap.get("leaveBalances").get(leaveType);
		}
		tempMap.put("CL", cLBalance);
		
	eLBalance=0.00;
		leaveType = "EL";
		inputMap = [:];
		inputMap.put("balanceDate", UtilDateTime.toSqlDate(thruDate));
		inputMap.put("employeeId", employeeId);
		inputMap.put("leaveTypeId", leaveType);
		Map eLEmplLeaveBalanceMap = EmplLeaveService.getEmployeeLeaveBalance(dctx,inputMap);
		if(UtilValidate.isNotEmpty(eLEmplLeaveBalanceMap.get("leaveBalances").get(leaveType))){
			eLBalance=eLEmplLeaveBalanceMap.get("leaveBalances").get(leaveType);
		}
		tempMap.put("EL", eLBalance);
		
		hPLBalance=0.00;
		leaveType = "HPL";
		inputMap = [:];
		inputMap.put("balanceDate", UtilDateTime.toSqlDate(thruDate));
		inputMap.put("employeeId", employeeId);
		inputMap.put("leaveTypeId", leaveType);
		Map hPLEmplLeaveBalanceMap = EmplLeaveService.getEmployeeLeaveBalance(dctx,inputMap);
		if(UtilValidate.isNotEmpty(hPLEmplLeaveBalanceMap.get("leaveBalances").get(leaveType))){
			hPLBalance=hPLEmplLeaveBalanceMap.get("leaveBalances").get(leaveType);
		}
		tempMap.put("HPL", hPLBalance);
	employeeList.add(tempMap);
}
context.put("employeeList",employeeList);

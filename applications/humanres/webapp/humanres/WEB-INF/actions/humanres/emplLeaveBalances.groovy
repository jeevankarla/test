import org.apache.derby.impl.sql.compile.OrderByColumn;
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
import in.vasista.vbiz.humanres.HumanresService;


dctx = dispatcher.getDispatchContext();
partyId = parameters.partyId;
context.put("partyId",partyId);
customTimePeriodId = parameters.customTimePeriodId;
context.put("customTimePeriodId",customTimePeriodId);
leaveTypeId = parameters.leaveTypeId;
context.put("leaveTypeId",leaveTypeId);
closingBalance = 0;
openingBalance = 0;
ob = 0; 
allotedDays = 0;
availedDays = 0;
adjustedDays = 0;
encashedDays = 0;
lapsedDays = 0;
fromDateStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDateEnd= UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
if(UtilValidate.isNotEmpty(customTimePeriodId)){
	customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId:customTimePeriodId], false);
	fromDate=UtilDateTime.toDateString(customTimePeriod.get("fromDate"), "MMM dd, yyyy");
	thruDate=UtilDateTime.toDateString(customTimePeriod.get("thruDate"), "MMM dd, yyyy");
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
}
Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDateStart, -1));
leaveBalancesList = [];
if(UtilValidate.isNotEmpty(partyId)){
	Map getEmplLeaveBalMap = [:];
	getEmplLeaveBalMap.put("userLogin",userLogin);
	getEmplLeaveBalMap.put("leaveTypeId",leaveTypeId);
	getEmplLeaveBalMap.put("employeeId",partyId);
	getEmplLeaveBalMap.put("flag","creditLeaves");
	getEmplLeaveBalMap.put("balanceDate",new java.sql.Date(previousDayEnd.getTime()));
	if(UtilValidate.isNotEmpty(getEmplLeaveBalMap)){
		serviceResult = dispatcher.runSync("getEmployeeLeaveBalance", getEmplLeaveBalMap);
		Map leaveBalances = (Map)serviceResult.get("leaveBalances");
		if(UtilValidate.isNotEmpty(leaveBalances)){
			openingBalance = (BigDecimal) leaveBalances.get(leaveTypeId);
		}
	} 
	emplLeaveBalanceStatus = delegator.findOne("EmplLeaveBalanceStatus", [customTimePeriodId:customTimePeriodId,partyId:partyId,leaveTypeId:leaveTypeId], false);
	if(UtilValidate.isNotEmpty(emplLeaveBalanceStatus)){
		ob = emplLeaveBalanceStatus.openingBalance;
		allotedDays = emplLeaveBalanceStatus.allotedDays;
		availedDays = emplLeaveBalanceStatus.availedDays;
		adjustedDays = emplLeaveBalanceStatus.adjustedDays;
		encashedDays = emplLeaveBalanceStatus.encashedDays;
		lapsedDays = emplLeaveBalanceStatus.lapsedDays;
	}
	if(UtilValidate.isEmpty(openingBalance) || openingBalance == 0){
		if(UtilValidate.isNotEmpty(ob)){
			openingBalance = ob;
		}else{
			openingBalance = 0;
		}
	}
	context.put("openingBalance",openingBalance);
	if(UtilValidate.isEmpty(allotedDays)){
		allotedDays	 = 0;
	}
	context.put("allotedDays",allotedDays);
	if(UtilValidate.isEmpty(availedDays)){
		availedDays = 0;
	}
	context.put("availedDays",availedDays);
	if(UtilValidate.isEmpty(adjustedDays)){
		adjustedDays = 0;
	}
	context.put("adjustedDays",adjustedDays);
	if(UtilValidate.isEmpty(encashedDays)){
		encashedDays = 0;
	}
	context.put("encashedDays",encashedDays);
	if(UtilValidate.isEmpty(lapsedDays)){
		lapsedDays = 0;
	}
	context.put("lapsedDays",lapsedDays);
}
















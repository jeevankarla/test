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
userpartyId = userLogin.partyId;
context.userpartyId = userpartyId;
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
	
	
	emplLeaveBalanceStatus = delegator.findOne("EmplLeaveBalanceStatus", [customTimePeriodId:customTimePeriodId,partyId:partyId,leaveTypeId:leaveTypeId], false);
	if(UtilValidate.isNotEmpty(emplLeaveBalanceStatus)){
		openingBalance = emplLeaveBalanceStatus.openingBalance;
		allotedDays = emplLeaveBalanceStatus.allotedDays;
		availedDays = emplLeaveBalanceStatus.availedDays;
		adjustedDays = emplLeaveBalanceStatus.adjustedDays;
		encashedDays = emplLeaveBalanceStatus.encashedDays;
		lapsedDays = emplLeaveBalanceStatus.lapsedDays;
	}
	if(UtilValidate.isNotEmpty(leaveBalanceFlag) && leaveBalanceFlag.equals("leaveBalanceStatus")){
		EmplLeaveBalanceStatusList = [];
		employmentsList = [];
		if(UtilValidate.isEmpty(partyId)){
			emplInputMap = [:];
			emplInputMap.put("userLogin", userLogin);
			emplInputMap.put("orgPartyId", "Company");
			emplInputMap.put("fromDate", fromDateStart);
			emplInputMap.put("thruDate", thruDateEnd);
			Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
			employments=EmploymentsMap.get("employementList");
			employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyIdTo"));
			
			if(UtilValidate.isNotEmpty(employments)){
				employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
			}
		}else{
			employmentsList.add(partyId);
		}
		for(int i=0;i<employmentsList.size();i++){
			emplLeaveMap = [:];
			inputMap =[:]
			employeeId = employmentsList.get(i);
			inputMap.put("balanceDate", UtilDateTime.toSqlDate(thruDateEnd));
			inputMap.put("employeeId", employeeId);
			if(UtilValidate.isNotEmpty(leaveTypeId)){
				inputMap.put("leaveTypeId", leaveTypeId);
			}
			inputMap.put("flag","creditLeaves");
			
			Map cLEmplLeaveBalanceMap = EmplLeaveService.getEmployeeLeaveBalance(dctx,inputMap);
			if(UtilValidate.isNotEmpty(cLEmplLeaveBalanceMap.get("leaveBalances").get(leaveTypeId))){
				leaveBalances=cLEmplLeaveBalanceMap.get("leaveBalances").get(leaveTypeId);
				emplLeaveMap.put("leaveBalances", leaveBalances);
			}
			emplLeaveMap.put("partyId", employeeId);
			emplLeaveMap.put("leaveTypeId", leaveTypeId);
			if(UtilValidate.isNotEmpty(customTimePeriodId)){
				emplLeaveMap.put("customTimePeriodId", customTimePeriodId);
			}
		
			if(UtilValidate.isNotEmpty(emplLeaveMap)){
				EmplLeaveBalanceStatusList.addAll(emplLeaveMap);
			}
		}
		context.EmplLeaveBalanceStatusList = EmplLeaveBalanceStatusList;
		
	}
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
			ob = (BigDecimal) leaveBalances.get(leaveTypeId);
		}
	} 
	if(UtilValidate.isEmpty(emplLeaveBalanceStatus)){
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

condList = [];
condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
typeCondition=EntityCondition.makeCondition(condList, EntityOperator.AND);
customTimePeriodList = delegator.findList("CustomTimePeriod", typeCondition , null, ["-fromDate"], null, false);
context.customTimePeriodList = customTimePeriodList;







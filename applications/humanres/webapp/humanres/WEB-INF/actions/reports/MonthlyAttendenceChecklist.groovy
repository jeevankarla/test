
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

dctx = dispatcher.getDispatchContext();


fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.mclFromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.mclFromDate).getTime()));
	}
	if (parameters.mclThruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.mclThruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

context.fromDate=UtilDateTime.toDateString(fromDate, "MMM dd, yyyy");
context.thruDate=UtilDateTime.toDateString(thruDate, "MMM dd, yyyy");

employeeList = [];

activeEmpMap=HumanresApiService.getActiveEmployees(dctx,[userLogin:userLogin]);
if(UtilValidate.isNotEmpty(activeEmpMap)){
	Iterator empIter = activeEmpMap.entrySet().iterator();
	while(empIter.hasNext()){
		Map.Entry empEntry = empIter.next();
		employeesMap=empEntry.getValue();
		if(UtilValidate.isNotEmpty(employeesMap)){
			Iterator employeeIter=employeesMap.entrySet().iterator();
			while(employeeIter.hasNext()){
				Map.Entry employeeEntry=employeeIter.next();
				allEmployees=employeeEntry.getValue();
				allEmployees.each { employee ->
					employeeMap=[:];
						employeeMap.put("employeeId",employee.employeeId);
						employeeMap.put("name",employee.name);
						empLeaveMap=EmplLeaveService.fetchLeaveDaysForPeriod(dctx,[partyId:employee.employeeId,timePeriodStart:fromDate, timePeriodEnd: thruDate,userLogin : userLogin]);
						leaveDetailmap=empLeaveMap.get("leaveDetailmap");
						if(UtilValidate.isNotEmpty(leaveDetailmap)){
							Iterator leaveIter=leaveDetailmap.entrySet().iterator();
								while(leaveIter.hasNext()){
								 Map.Entry leaveEntry=leaveIter.next();
								 employeeMap.put(leaveEntry.getKey(), leaveEntry.getValue());
							}
						}
						employeePayrollAttedance=PayrollService.getEmployeePayrollAttedance(dctx,[employeeId:employee.employeeId,timePeriodStart:fromDate, timePeriodEnd: thruDate,userLogin : userLogin]);
						shiftDetails=employeePayrollAttedance.get("shiftDetailMap");
						if(UtilValidate.isNotEmpty(shiftDetails)){
							Iterator shiftIter=shiftDetails.entrySet().iterator();
								while(shiftIter.hasNext()){
								 Map.Entry shiftEntry=shiftIter.next();
								 employeeMap.put(shiftEntry.getKey(),shiftEntry.getValue());
							}
						}
						employeeMap.put("workedHolidays",employeePayrollAttedance.get("noOfAttendedHoliDays"));
						employeeMap.put("workedSsDays",employeePayrollAttedance.get("noOfAttendedSsDays"));
						employeeMap.put("payableDays",employeePayrollAttedance.get("noOfPayableDays"));
						employeeMap.put("arrearDays",employeePayrollAttedance.get("noOfArrearDays"));
						leaveBalances = delegator.findByAnd("EmplLeaveBalanceStatus",[partyId:employee.employeeId],["leaveTypeId"]);
						leaveBalances.each{ leaveType ->
							if(leaveType.leaveTypeId=="CL")
							employeeMap.put("BCL", leaveType.openingBalance);
							if(leaveType.leaveTypeId=="EL")
							employeeMap.put("BEL", leaveType.openingBalance);
							if(leaveType.leaveTypeId=="HPL")
							employeeMap.put("BHP", leaveType.openingBalance);
						}
						nowDate=UtilDateTime.nowTimestamp();
						nowfromDate = UtilDateTime.getMonthStart(nowDate);
						nowthruDate = UtilDateTime.getMonthEnd(nowDate,timeZone,locale);
						resultMap=dispatcher.runSync("getCustomTimePeriodId", [periodTypeId:"HR_MONTH",fromDate:nowfromDate,thruDate:nowthruDate,userLogin:userLogin]);
							if(UtilValidate.isNotEmpty(resultMap)){
							cldAmount=0;
							cldDays=0;
							cldAmountList=casteIds=delegator.findByAnd("PartyBenefit", [partyIdTo:employee.employeeId,benefitTypeId:"PAYROL_BEN_COLDALLOW"],["benefitTypeId"]);
							if(UtilValidate.isNotEmpty(cldAmountList)){
								cldAmountIds=cldAmountList.get(0).benefitTypeId;
								cldAmountMap=PayrollService.getPayHeadAmount(dctx,[userLogin:userLogin,payHeadTypeId:cldAmountIds,employeeId:employee.employeeId,customTimePeriodId:resultMap.get("customTimePeriodId"),locale:locale]);
								cldAmount=cldAmountMap.get("amount");
								if(UtilValidate.isNotEmpty(cldAmount) && cldAmount!=null)
									cldDays=employeePayrollAttedance.get("noOfPayableDays");
							}
							employeeMap.put("cldDays",cldDays);
							caAmount=0;
							caDays=0;
							caAmountList=casteIds=delegator.findByAnd("PartyBenefit", [partyIdTo:employee.employeeId,benefitTypeId:"PAYROL_BEN_CASH"],["benefitTypeId"]);
							if(UtilValidate.isNotEmpty(caAmountList)){
								caAmountIds=caAmountList.get(0).benefitTypeId;
								caAmountMap=PayrollService.getPayHeadAmount(dctx,[userLogin:userLogin,payHeadTypeId:caAmountIds,employeeId:employee.employeeId,customTimePeriodId:resultMap.get("customTimePeriodId"),locale:locale]);
								caAmount=caAmountMap.get("amount");
								if(UtilValidate.isNotEmpty(caAmount) && caAmount!=null)
								caDays=employeePayrollAttedance.get("noOfPayableDays");
							}
							employeeMap.put("caDays",caDays);
						}
					employeeList.add(employeeMap);
					}
				
				}
			}
	}
}
	
context.put("employeeList",employeeList);		
	




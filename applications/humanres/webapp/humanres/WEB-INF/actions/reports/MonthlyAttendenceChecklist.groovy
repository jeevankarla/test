
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
				allEmployees.each{ employee ->
					employeeMap=[:];
						employeeMap.put("employeeId",employee.employeeId);
						employeeMap.put("name",employee.name);
						empLeaveMap=EmplLeaveService.fetchLeaveDaysForPeriod(dctx,[partyId:employee.employeeId,timePeriodStart:fromDate, timePeriodEnd: thruDate,userLogin : userLogin]);
						leaveDetailmap=empLeaveMap.get("leaveDetailmap");
						hpl=leaveDetailmap.get("HPL");
						if(hpl==null){hpl=0;}
						employeeMap.put("HPL",hpl);
						cl=leaveDetailmap.get("Casual Leave");
						if(cl==null){cl=0;}
						employeeMap.put("CL",cl);
						el=leaveDetailmap.get("EL");
						if(el==null){el=0;}
						employeeMap.put("EL",el);
						employeePayrollAttedance=PayrollService.getEmployeePayrollAttedance(dctx,[employeeId:employee.employeeId,timePeriodStart:fromDate, timePeriodEnd: thruDate,userLogin : userLogin]);
						shiftDetails=employeePayrollAttedance.get("shiftDetailMap");
						shift_01=shiftDetails.get("SHIFT_01");
						if(shift_01==null){shift_01=0;}
						employeeMap.put("shift_01",shift_01);
						shift_02=shiftDetails.get("SHIFT_02");
						if(shift_02==null){shift_02=0;}
						employeeMap.put("shift_02",shift_02);
						shift_03=shiftDetails.get("SHIFT_03");
						if(shift_03==null){shift_03=0;}
						employeeMap.put("shift_03",shift_03);
						shift_gen=shiftDetails.get("SHIFT_GEN");
						if(shift_gen==null){shift_gen=0;}
						employeeMap.put("shift_gen",shift_gen);
						employeeMap.put("workedHolidays",employeePayrollAttedance.get("noOfAttendedHoliDays"));
						employeeMap.put("workedSsDays",employeePayrollAttedance.get("noOfAttendedSsDays"));
						if(employee.casualLeaveBalance==null){employee.casualLeaveBalance=0;}
						employeeMap.put("BCL",employee.casualLeaveBalance);
						if(employee.halfPayLeaveBalance==null){employee.halfPayLeaveBalance=0;}
						employeeMap.put("BHP",employee.halfPayLeaveBalance);
						if(employee.earnedLeaveBalance==null){employee.earnedLeaveBalance=0;}
						employeeMap.put("BEL",employee.earnedLeaveBalance);
						employeeMap.put("payableDays",employeePayrollAttedance.get("noOfPayableDays"));
						
						nowDate=UtilDateTime.nowTimestamp();
						nowfromDate = UtilDateTime.getMonthStart(nowDate);
						nowthruDate = UtilDateTime.getMonthEnd(nowDate,timeZone,locale);
						resultMap=dispatcher.runSync("getCustomTimePeriodId", [periodTypeId:"HR_MONTH",fromDate:nowfromDate,thruDate:nowthruDate,userLogin:userLogin]);
						cldAmount=0;
						cldAmountList=casteIds=delegator.findByAnd("PartyBenefit", [partyIdTo:employee.employeeId,benefitTypeId:"PAYROL_BEN_COLDALLOW"],["benefitTypeId"]);
						if(UtilValidate.isNotEmpty(cldAmountList)){
							cldAmountIds=cldAmountList.get(0).benefitTypeId;
							cldAmountMap=PayrollService.getPayHeadAmount(dctx,[userLogin:userLogin,payHeadTypeId:cldAmountIds,employeeId:employee.employeeId,customTimePeriodId:resultMap.get("customTimePeriodId"),locale:locale]);
							cldAmount=cldAmountMap.get("amount");
							
						}
						employeeMap.put("cldallow",cldAmount)
						caAmount=0;
						caAmountList=casteIds=delegator.findByAnd("PartyBenefit", [partyIdTo:employee.employeeId,benefitTypeId:"PAYROL_BEN_CASH"],["benefitTypeId"]);
						if(UtilValidate.isNotEmpty(caAmountList)){
							caAmountIds=caAmountList.get(0).benefitTypeId;
							caAmountMap=PayrollService.getPayHeadAmount(dctx,[userLogin:userLogin,payHeadTypeId:caAmountIds,employeeId:employee.employeeId,customTimePeriodId:resultMap.get("customTimePeriodId"),locale:locale]);
							caAmount=caAmountMap.get("amount");
						}
						employeeMap.put("cashallow",caAmount)
						employeeList.add(employeeMap);
						
					}
				
				}
			}
	}
}
	
context.put("employeeList",employeeList);		
	






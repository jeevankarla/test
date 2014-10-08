import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.RowFilter.NotFilter;

import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.humanres.HumanresHelperServices;

dctx = dispatcher.getDispatchContext();
emplList=[];
holidaysList=[];
workedHolidaysList=[];
List EncashmentList=[];
employments=[];
holidays=[];
empIds=[];
empName=[:];
orderDate=UtilDateTime.nowTimestamp();
context.orderDate=orderDate;
partyId=parameters.partyId;
deptId=parameters.deptId;
timePeriodId=parameters.customTimePeriodId;
employeeList = [];
internalOrgs=[];
context.internalOrgs=internalOrgs;
context.employeeList=employeeList;
company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : "Company"]);
populateChildren(company, employeeList);
def populateChildren(org, employeeList) {
	EmploymentsMap=HumanresService.getActiveEmployements(dctx,[userLogin:userLogin,orgPartyId:deptId]);
	employments=EmploymentsMap.get("employementList");
	employments.each{ employment->
		empIds.add(employment.partyId);
		String lastName="";
		if(employment.lastName!=null){
			lastName=employment.lastName;
		}
		name=employment.firstName+" "+lastName;
		empName.put(employment.partyId,name);
		
		List conditionList=[];
		if(UtilValidate.isNotEmpty(employment.partyId)){
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, deptId));
			conditionList.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS,"PARTY_GROUP"));
		}
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		orgDetails= delegator.findList("PartyRelationshipAndDetail", condition, UtilMisc.toSet("groupName"), null, null, false );
		if(UtilValidate.isNotEmpty(orgDetails)){
			details=EntityUtil.getFirst(orgDetails);
			orgName=details.get("groupName");
		}
		
	}
}
if(UtilValidate.isNotEmpty(deptId))
	context.orgName=orgName;

if(UtilValidate.isNotEmpty(timePeriodId)){
	context.timePeriodId=timePeriodId;
		dates=delegator.findOne("CustomTimePeriod", [customTimePeriodId:timePeriodId], false);
		fromDate=UtilDateTime.toDateString(dates.get("fromDate"), "MMM dd, yyyy");
		thruDate=UtilDateTime.toDateString(dates.get("thruDate"), "MMM dd, yyyy");
		fromDateStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		thruDateEnd= UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
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
	partyId=parameters.partyId;
	resultMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:fromDateStart,timePeriodEnd:thruDateEnd,timePeriodId:timePeriodId,locale:locale]);
	lastClosePeriod=resultMap.get("lastCloseAttedancePeriod");
	if(UtilValidate.isNotEmpty(lastClosePeriod)){
		customTimePeriodId=lastClosePeriod.get("customTimePeriodId");
		context.customTimePeriodId=customTimePeriodId;
		attenDates=delegator.findOne("CustomTimePeriod", [customTimePeriodId:customTimePeriodId], false);
		fromDate=UtilDateTime.toDateString(attenDates.get("fromDate"), "MMM dd, yyyy");
		thruDate=UtilDateTime.toDateString(attenDates.get("thruDate"), "MMM dd, yyyy");
		context.fromlarDate=fromDate;
		context.thrularDate=thruDate;
		fromDateStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		thruDateEnd= UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		def sdfn = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			if (fromDate) {
				fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdfn.parse(fromDate).getTime()));
			}
			if (thruDate) {
				thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdfn.parse(thruDate).getTime()));
			}
		} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
		}
		List conditionList=[];
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
		conditionList.add(EntityCondition.makeCondition("date",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(fromDateStart)));
		conditionList.add(EntityCondition.makeCondition("date",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(thruDateEnd)));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		emplList = delegator.findList("EmplDailyAttendanceDetail", condition ,null,null, null, false );
		
		//Approval Holiday
		List holidayconditionList=[];
		holidayconditionList.add(EntityCondition.makeCondition("holiDayDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDateStart));
		holidayconditionList.add(EntityCondition.makeCondition("holiDayDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDateEnd));
		holidaycondition=EntityCondition.makeCondition(holidayconditionList,EntityOperator.AND);
		holidaysList = delegator.findList("HolidayCalendar", holidaycondition ,null,null, null, false );
		
		secondSaturDay = UtilDateTime.addDaysToTimestamp(UtilDateTime.getWeekStart(UtilDateTime.getMonthStart(thruDateEnd),0,2,timeZone,locale), -1);
		if(UtilValidate.isNotEmpty(holidaysList)){
			holidaysList.each{ days ->
				holidays.add(UtilDateTime.toSqlDate(days.get("holiDayDate")));
			}
		}
		holidays.add(UtilDateTime.toSqlDate(secondSaturDay));
		//get employee EmployeeWeeklyOff Days and exclude those
		employeeWeeklyOffInpuMap = [:];
		employeeWeeklyOffInpuMap.put("employeeId",partyId);
		employeeWeeklyOffInpuMap.put("fromDate",fromDateStart);
		employeeWeeklyOffInpuMap.put("thruDate",thruDateEnd);
		List employeeWeeklyOffDays = (List)((HumanresHelperServices.getEmployeeWeeklyOffDays(dctx,employeeWeeklyOffInpuMap)).get("weeklyOffDays"));
		List conList=[];
		conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
		conList.add(EntityCondition.makeCondition("date",EntityOperator.IN,holidays));
		conList.add(EntityCondition.makeCondition("date",EntityOperator.NOT_IN,employeeWeeklyOffDays));
		conList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("encashmentStatus",EntityOperator.NOT_EQUAL,"LEAVE_ENCASHMENT"),EntityOperator.OR,
			EntityCondition.makeCondition("encashmentStatus",EntityOperator.EQUALS,null)));
		con=EntityCondition.makeCondition(conList,EntityOperator.AND);
		workedHolidaysList = delegator.findList("EmplDailyAttendanceDetail", con ,null,null, null, false );
		
		
		//GH and SS Encashment
		
		List conGHSSList=[];
		conGHSSList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,empIds));
		conGHSSList.add(EntityCondition.makeCondition("date",EntityOperator.IN,holidays));
		conGHSSList.add(EntityCondition.makeCondition("encashmentStatus",EntityOperator.EQUALS,"CASH_ENCASHMENT"));
		conGHSS=EntityCondition.makeCondition(conGHSSList,EntityOperator.AND);
		workedGHSSList = delegator.findList("EmplDailyAttendanceDetail", conGHSS ,UtilMisc.toSet("partyId","date","shiftType","encashmentStatus"),null, null, false );
		leaveType="";
		workedGHSSList.each{ workedGHSS->
			employee=[:];
			if(workedGHSS.date==UtilDateTime.toSqlDate(secondSaturDay))
				leaveType="SS";
			else
				leaveType="GH";
				
			employee.put("partyId",workedGHSS.partyId);
			employee.put("date",workedGHSS.date);
			employee.put("leaveType",leaveType);
			employee.put("name",empName.get(workedGHSS.partyId));
			employee.put("encashmentStatus",workedGHSS.encashmentStatus);
			employee.put("shiftType",workedGHSS.shiftType);
			EncashmentList.add(employee);
			
		}
	}
}

context.emplList=emplList;
context.holidaysList=workedHolidaysList;
context.EncashmentList=EncashmentList;

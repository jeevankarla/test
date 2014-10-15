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

hrMonthDates=[];
List conList=[];
conList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS,"HR_MONTH"));
conList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(thruDate)));
con=EntityCondition.makeCondition(conList,EntityOperator.AND);
hrMonthDates = delegator.findList("CustomTimePeriod", con ,null,UtilMisc.toList("-thruDate"), null, false );
customTimePeriodIds=EntityUtil.getFirst(hrMonthDates);
customTimePeriodId=customTimePeriodIds.get("customTimePeriodId");

leaveTypeIds=[];
if(parameters.leaveTypeId=="ALL"){
	leaveList=delegator.findList("EmplLeaveType",null,null,null,null,false);
	if(UtilValidate.isNotEmpty(leaveList)){
		leaveList.each{ leaveType ->
			leaveTypeIds.add(leaveType.get("leaveTypeId"));
		}
	}
}
else{
	leaveTypeIds.add(parameters.leaveTypeId);
}
employeesResult=[];
employeeList=[];
empIds=[];
employeesMap=[:];
orgName="";

employeeList = [];
internalOrgs=[];
context.internalOrgs=internalOrgs;
context.employeeList=employeeList;
company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : "Company"]);
populateChildren(company, employeeList);

def populateChildren(org, employeeList) {
	EmploymentsMap=HumanresService.getActiveEmployements(dctx,[userLogin:userLogin,orgPartyId:parameters.partyId]);
	employments=EmploymentsMap.get("employementList");
	employments.each { employment ->
		String lastName="";
		if(employment.lastName!=null){
			lastName=employment.lastName;
		}
		empIds.add(employment.partyId);

		List conditionList=[];
		if(UtilValidate.isNotEmpty(employment.partyId)){
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
			conditionList.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS,"PARTY_GROUP"));
		}
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		orgDetails= delegator.findList("PartyRelationshipAndDetail", condition, UtilMisc.toSet("groupName"), null, null, false );
		if(UtilValidate.isNotEmpty(orgDetails)){
			details=EntityUtil.getFirst(orgDetails);
			orgName=details.get("groupName");
		}
		
		
		
		employeesMap.put(employment.partyId, employment.firstName + " " + lastName);
	}
}
context.orgName=orgName;


finalMap=[:];


if(UtilValidate.isNotEmpty(leaveTypeIds)){
		leaveTypeIds.each { leaveTypeId ->
			employeesList=[];
			List conditionList=[];
			if(UtilValidate.isNotEmpty(parameters.employeeId)){
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,parameters.employeeId));
			}else{
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,empIds));
			}
			conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveTypeId));
			conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
			conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			empLeavesList = delegator.findList("EmplLeave", condition ,null,null, null, false );
			if(UtilValidate.isNotEmpty(empLeavesList)){
				emplLeaveBalance=[:];
				leaveBalanceMap=[:];
				empLeavesList.each { empLeaves ->
					employeeMap=[:];
					empLeaveMap=EmplLeaveService.fetchLeaveDaysForPeriod(dctx,[partyId:empLeaves.get("partyId"),leaveTypeId:empLeaves.get("leaveTypeId"),timePeriodStart:fromDate, timePeriodEnd: thruDate,userLogin:userLogin]);
					if(UtilValidate.isNotEmpty(empLeaveMap)){
						leaveDetailmap=empLeaveMap.get("leaveDetailmap");
						employeeMap.put("employeeId",empLeaves.get("partyId"));
						employeeMap.put("name", employeesMap.get(empLeaves.get("partyId")));
						employeeMap.put("leaveFrom",UtilDateTime.toDateString(empLeaves.get("fromDate"), "dd-MM-yyyy"));
						employeeMap.put("leaveThru",UtilDateTime.toDateString(empLeaves.get("thruDate"), "dd-MM-yyyy"));

						int interval=0;
						interval=(UtilDateTime.getIntervalInDays(empLeaves.get("fromDate"), empLeaves.get("thruDate"))+1);
						BigDecimal intv=new BigDecimal(interval);
						if(empLeaves.get("dayFractionId")=="FIRST_HALF" || empLeaves.get("dayFractionId")=="SECOND_HALF"){
							intv=interval/2;
							employeeMap.put("noOfDays",intv);
							
						}
						employeeMap.put("noOfDays",intv);
						employeeMap.put("leaveTypeId",empLeaves.get("leaveTypeId"));
						
						//balance 
						balance=0;
						emplLeaveBalance=[:];
						if(UtilValidate.isNotEmpty(leaveBalanceMap.get(empLeaves.get("partyId")))){
							emplLeaveBalance=leaveBalanceMap.get(empLeaves.get("partyId"));
						}
						
						if(UtilValidate.isNotEmpty(emplLeaveBalance)){
								balance = emplLeaveBalance.getAt(empLeaves.get("leaveTypeId"));
						}else{
							leaveBalances = delegator.findByAnd("EmplLeaveBalanceStatus",[partyId:empLeaves.get("partyId"),customTimePeriodId:customTimePeriodId,leaveTypeId:empLeaves.get("leaveTypeId")],["openingBalance"]);
							if(UtilValidate.isNotEmpty(leaveBalances) && leaveTypeId=="CL" || leaveTypeId=="EL" || leaveTypeId=="HPL"){
								balance=leaveBalances.get(0).openingBalance;
							}
						}
						balance = balance-intv;
						emplLeaveBalance.putAt(empLeaves.get("leaveTypeId"), balance);
						leaveBalanceMap.put(empLeaves.get("partyId"), emplLeaveBalance);
						employeeMap.put("balance", balance);
						if(UtilValidate.isNotEmpty(employeeMap)){
							employeesList.add(employeeMap);
						}
					}
				}
				if(UtilValidate.isNotEmpty(employeesList)){
					finalMap.put(leaveTypeId, employeesList);
				}
			}
		}
}
if(UtilValidate.isEmpty(finalMap)){
	Debug.logError("No Leaves Found.","");
	context.errorMessage = "No Leaves Found.......!";
	return;
}

context.put("finalMap",finalMap);





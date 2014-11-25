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
import in.vasista.vbiz.humanres.EmplLeaveService;

dctx = dispatcher.getDispatchContext();
employeeList = [];
fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	condList=[];
	condList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,parameters.customTimePeriodId));
	condList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS,"HR_MONTH"));
	cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
	dateList=delegator.findList("CustomTimePeriod",cond,null,null,null,false);
	if(dateList){
		dates=EntityUtil.getFirst(dateList);
		fromDate=UtilDateTime.toTimestamp(dates.get("fromDate"));
		thruDate=UtilDateTime.toTimestamp(dates.get("thruDate"));
	}
}
context.fromDate=UtilDateTime.toDateString(fromDate,"dd-MMM-yyyy");
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDate);
emplInputMap.put("thruDate", thruDate);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
	employementList.each { employment ->
		employee = [:];
		group=delegator.findByAnd("PartyRelationshipAndDetail", [partyId: employment.partyIdFrom, partyTypeId : "PARTY_GROUP"],["groupName"]);
		daAmount=0;
			daAmountList=casteIds=delegator.findByAnd("PartyBenefit", [partyIdTo: employment.partyId,benefitTypeId:"PAYROL_BEN_DA"],["benefitTypeId"]);
			if(UtilValidate.isNotEmpty(daAmountList)){
				daAmountIds=daAmountList.get(0).benefitTypeId;
				daAmountMap=PayrollService.getPayHeadAmount(dctx,[userLogin:userLogin,payHeadTypeId:daAmountIds,employeeId:employment.partyId,customTimePeriodId:parameters.customTimePeriodId,locale:locale]);
				daAmount=daAmountMap.get("amount");
			}
		employee.put("daAmount",daAmount);
		employeePosition = "";
		emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", ["employeePartyId" : employment.partyId]));
		emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
		if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionTypeId") != null){
			emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionAndFulfillment.getString("emplPositionTypeId")], true);
			if (emplPositionType != null) {
				employeePosition = emplPositionType.getString("description");
			}
			else {
				employeePosition = emplPositionAndFulfillment.getString("emplPositionId");
			}
		}
		employee.put("position", employeePosition);
		String lastName="";
		if(employment.lastName!=null){
			lastName=employment.lastName;
		}
		employee.put("name", employment.firstName + " " + lastName);
		employee.put("employeeId", employment.partyId);
		joinDate = UtilDateTime.toDateString(employment.appointmentDate, "dd-MMM-yyyy");
		employee.put("joinDate", joinDate)
		dob="";
		dob=UtilDateTime.toDateString(employment.birthDate, "dd-MMM-yyyy");
		employee.put("birthDate",dob);
		
		basicSalAndGradeMap=PayrollService.fetchBasicSalaryAndGrade(dctx,[employeeId:employment.partyIdTo,timePeriodStart:fromDate, timePeriodEnd: thruDate, userLogin : userLogin, proportionalFlag:"N"]);
		employee.put("amount",basicSalAndGradeMap.get("amount"));
			String leaveTypeId="EL";
			int balance=0;
			int basicSal=0;
			basicSal=basicSalAndGradeMap.get("amount");
			int total=basicSal+daAmount;
			inputMap = [:];
			inputMap.put("balanceDate", UtilDateTime.toSqlDate(fromDate));
			inputMap.put("employeeId", employment.partyIdTo);
			inputMap.put("leaveTypeId", leaveTypeId);
			Map EmplLeaveBalanceMap = EmplLeaveService.getEmployeeLeaveBalance(dctx,inputMap);
			if(UtilValidate.isNotEmpty(EmplLeaveBalanceMap.get("leaveBalances").get(leaveTypeId))){
				balance=EmplLeaveBalanceMap.get("leaveBalances").get(leaveTypeId);
			}
			employee.put("balance", balance);
			employee.put("total", total);
		employeeList.add(employee);
	}
	if(UtilValidate.isEmpty(employeeList)){
		Debug.logError("No Records Found.","");
		context.errorMessage = "No No Records Found.......!";
		return;
	}
	
context.employeeList=employeeList;

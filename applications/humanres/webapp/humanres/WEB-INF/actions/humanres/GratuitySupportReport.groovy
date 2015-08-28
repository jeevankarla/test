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

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;


def sdf = new SimpleDateFormat("MMMM dd,yyyy");
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


String fromDateMonthName = UtilDateTime.toDateString(fromDate,"MMM");
String thruDateMonthName = UtilDateTime.toDateString(thruDate,"MMM");

if(!fromDateMonthName.equals("Sep")){
	Debug.logError("Gratuity period is from  September to August","");
	context.errorMessage = "Gratuity period is from  September to August";
	return;
}
if(!thruDateMonthName.equals("Aug")){
	Debug.logError("Gratuity period is from  September to August","");
	context.errorMessage = "Gratuity period is from  September to August";
	return;
}

fromDateStart = UtilDateTime.getMonthStart(thruDate);
thruDateEnd = UtilDateTime.getMonthEnd(fromDateStart,timeZone, locale);
context.fromDate=fromDateStart;
context.thruDate=thruDateEnd;

context.fromDate=UtilDateTime.toDateString(fromDate,"dd-MMM-yyyy");

timePeriodId = "";
periodBillingId = "";
periodConList = [];
periodConList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
periodConList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDateStart)));
periodConList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(thruDateEnd)));
periodCond=EntityCondition.makeCondition(periodConList,EntityOperator.AND);
customTimePeriodIdsList = delegator.findList("CustomTimePeriod", periodCond , null, ["-fromDate"], null, false );
if(UtilValidate.isNotEmpty(customTimePeriodIdsList)){
	period = EntityUtil.getFirst(customTimePeriodIdsList);
	timePeriodId = period.get("customTimePeriodId");
	List monthConditionList=[];
	monthConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
	monthConditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PAYROLL_BILL"));
	monthConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("GENERATED","APPROVED")));
	monthConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, timePeriodId));
	monthCondition=EntityCondition.makeCondition(monthConditionList,EntityOperator.AND);
	monthPeriodList = delegator.findList("PeriodBillingAndCustomTimePeriod", monthCondition , null, null, null, false );
	if(UtilValidate.isNotEmpty(monthPeriodList)){
		monthPeriodList = EntityUtil.getFirst(monthPeriodList);
		periodBillingId = monthPeriodList.get("periodBillingId");
	}
}




sNo = 1;
employeeList = [];

employmentList = [];
employmentList.add( EntityCondition.makeCondition([
	EntityCondition.makeCondition([
		EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)
		],EntityOperator.OR),
	EntityCondition.makeCondition(
		EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate))
	],EntityOperator.AND));
employmentList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
EntityCondition empCondition = EntityCondition.makeCondition(employmentList, EntityOperator.AND);
List<GenericValue> employementList = delegator.findList("EmploymentAndPerson", empCondition, null, UtilMisc.toList("birthDate"), null, false);
if(UtilValidate.isNotEmpty(employementList)){
	employementList.each { employment ->
		employee = [:];
		group=delegator.findByAnd("PartyRelationshipAndDetail", [partyId: employment.partyIdFrom, partyTypeId : "PARTY_GROUP"],["groupName"]);
		daAmount=0;
		if(UtilValidate.isNotEmpty(periodBillingId)){
			List payrollHeaderItemList=[];
			payrollHeaderItemList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employment.partyIdTo));
			payrollHeaderItemList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
			payrollHeaderItemList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS,"PAYROL_BEN_DA"));
			headerItemCondition=EntityCondition.makeCondition(payrollHeaderItemList,EntityOperator.AND);
			daAmountList = delegator.findList("PayrollHeaderAndHeaderItem", headerItemCondition , null, null, null, false );
			if(UtilValidate.isNotEmpty(daAmountList)){
				daAmountList = EntityUtil.getFirst(daAmountList);
				daAmount=daAmountList.get("amount");
			}
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
		employee.put("employeeId", employment.partyIdTo);
		partyIdenctnCondList = [];
		partyIdenctnCondList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "GRATUITY_ID"));
		partyIdenctnCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employment.partyIdTo));
		partyIdenctnCond=EntityCondition.makeCondition(partyIdenctnCondList,EntityOperator.AND);
		partyIdencationList = delegator.findList("PartyIdentification", partyIdenctnCond , null, ["idValue"], null, false );
		if(UtilValidate.isNotEmpty(partyIdencationList)){
			partyIdencation = EntityUtil.getFirst(partyIdencationList);
			employee.put("gratuityIdNo",partyIdencation.idValue);
		}
		if(reportFlag.equals("csv")){
			employee.put("joinDate", employment.appointmentDate);
			employee.put("birthDate",employment.birthDate);
		}else{
			joinDate = UtilDateTime.toDateString(employment.appointmentDate, "dd-MMM-yyyy");
			employee.put("joinDate", joinDate);
			dob="";
			dob=UtilDateTime.toDateString(employment.birthDate, "dd-MMM-yyyy");
			employee.put("birthDate",dob);
		}
		basicSalAndGradeMap=PayrollService.fetchBasicSalaryAndGrade(dctx,[employeeId:employment.partyIdTo,timePeriodStart:fromDateStart, timePeriodEnd: thruDateEnd, userLogin : userLogin, proportionalFlag:"N"]);
		employee.put("amount",basicSalAndGradeMap.get("amount"));
		String leaveTypeId="EL";
		int balance=0;
		int basicSal=0;
		basicSal=basicSalAndGradeMap.get("amount");
		int total=basicSal+daAmount;
		inputMap = [:];
		inputMap.put("balanceDate", UtilDateTime.toSqlDate(fromDateStart));
		inputMap.put("employeeId", employment.partyIdTo);
		inputMap.put("leaveTypeId", leaveTypeId);
		Map EmplLeaveBalanceMap = EmplLeaveService.getEmployeeLeaveBalance(dctx,inputMap);
		if(UtilValidate.isNotEmpty(EmplLeaveBalanceMap)){
			if(UtilValidate.isNotEmpty(EmplLeaveBalanceMap.get("leaveBalances").get(leaveTypeId))){
				balance=EmplLeaveBalanceMap.get("leaveBalances").get(leaveTypeId);
			}
		}
		employee.put("balance", balance);
		employee.put("total", total);
		employee.put("sNo", sNo);
		sNo = sNo + 1;
		i = 0;
		if(UtilValidate.isNotEmpty(employeeList)){
			employeeList.each { employee ->
				if((employee.employeeId).equals(employment.partyIdTo)){
					i = i +1;
				}
			}
			if(i == 0){
				employeeList.add(employee);
			}
		}else{
			employeeList.add(employee);
		}
	}
}
	if(UtilValidate.isEmpty(employeeList)){
		Debug.logError("No Records Found.","");
		context.errorMessage = "No Records Found.......!";
		return;
	}
	
	sortGratuidIdMap = [:]as TreeMap;
	for (eachOne in employeeList) {
		String gratuityIdNo = eachOne.gratuityIdNo;
		if(UtilValidate.isNotEmpty(gratuityIdNo)){
			int gratuityIdNumber = Integer.parseInt(gratuityIdNo);
			sortGratuidIdMap.put(gratuityIdNumber, eachOne);
		}else{
			employeeId = eachOne.employeeId;
			int employeeIdVal = Integer.parseInt(employeeId);
			sortGratuidIdMap.put(employeeIdVal, eachOne);
		}
	}
	finalList = [];
	employeeList = sortGratuidIdMap.values();
context.employeeList=employeeList;

import org.apache.derby.impl.sql.compile.OrderByColumn;
import org.apache.avalon.framework.parameters.Parameters;
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
import org.ofbiz.party.party.PartyHelper;
import javolution.util.FastList;
import javolution.util.FastMap;

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

if(!fromDateMonthName.equals("Mar")){
	Debug.logError("EDLIS period is from  March to February","");
	context.errorMessage = "EDLIS period is from  March to February";
	return;
}
if(!thruDateMonthName.equals("Feb")){
	Debug.logError("EDLIS period is from  March to February","");
	context.errorMessage = "EDLIS period is from  March to February";
	return;
}


periodIdsList = [];
fromDateStart = UtilDateTime.getMonthStart(thruDate);
thruDateEnd = UtilDateTime.getMonthEnd(fromDateStart,timeZone, locale);
context.fromDate=fromDateStart;
context.thruDate=thruDateEnd;

timePeriodId = "";
periodConList = [];
periodConList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
periodConList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDateStart)));
periodConList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(thruDateEnd)));
periodCond=EntityCondition.makeCondition(periodConList,EntityOperator.AND);
customTimePeriodIdsList = delegator.findList("CustomTimePeriod", periodCond , null, ["-fromDate"], null, false );
if(UtilValidate.isNotEmpty(customTimePeriodIdsList)){
	period = EntityUtil.getFirst(customTimePeriodIdsList);
	timePeriodId = period.get("customTimePeriodId");
}

List employmentList = FastList.newInstance();
employmentList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd)));
EntityCondition empCondition = EntityCondition.makeCondition(employmentList, EntityOperator.AND);
List<GenericValue> employementList = delegator.findList("EmploymentAndPerson", empCondition, null, UtilMisc.toList("birthDate"), null, false);

finalList=[];
sNo = 1;
if(UtilValidate.isNotEmpty(employementList)){
	employementList.each{employment->
		employee=[:];
		//name
		String lastName="";
		if(employment.lastName!=null){
			lastName=employment.lastName;
		}
		employee.put("employeeCode",employment.partyIdTo);
		employee.put("employeeName", employment.firstName + " " + lastName);
		employee.put("DateOfBirth",employment.birthDate);
	    employee.put("DateOfJoining",employment.appointmentDate);
		basic = 0;
		basicSalAndGradeMap=PayrollService.fetchBasicSalaryAndGrade(dctx,[employeeId:employment.partyIdTo,timePeriodStart:fromDateStart, timePeriodEnd: thruDateEnd, userLogin : userLogin, proportionalFlag:"N"]);
		if(basicSalAndGradeMap.get("amount")!=null){
			basic=basicSalAndGradeMap.get("amount");
		}
		daAmount=0;
		daAmountList=casteIds=delegator.findByAnd("PartyBenefit", [partyIdTo: employment.partyId,benefitTypeId:"PAYROL_BEN_DA"],["benefitTypeId"]);
		if(UtilValidate.isNotEmpty(daAmountList)){
			daAmountIds=daAmountList.get(0).benefitTypeId;
			if(UtilValidate.isNotEmpty(timePeriodId)){
				daAmountMap=PayrollService.getPayHeadAmount(dctx,[userLogin:userLogin,payHeadTypeId:daAmountIds,employeeId:employment.partyId,customTimePeriodId:timePeriodId,locale:locale]);
				daAmount=daAmountMap.get("amount");
			}
		}
		salary=basic+daAmount;
		employee.put("Salary",salary);
		condList=[];
		condList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,employment.partyId));
		cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
		pfNumberList=delegator.findList("EmployeeDetail",cond,null,null,null,false);
		if(UtilValidate.isNotEmpty(pfNumberList)){
			pfNumberList = EntityUtil.getFirst(pfNumberList);
			pfNumber = pfNumberList.presentEpf;
		}
		employee.put("pfNumber",pfNumber);
		employee.put("sNo",sNo);
		sNo = sNo + 1;
		finalList.add(employee);
	}
}
context.finalList=finalList;

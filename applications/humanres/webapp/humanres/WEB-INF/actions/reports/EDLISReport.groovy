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
timePeriodId=parameters.customTimePeriodId;
fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
condList=[];
condList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,timePeriodId));
condList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS,"HR_MONTH"));
cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
dateList=delegator.findList("CustomTimePeriod",cond,null,null,null,false);
if(dateList){
	dates=EntityUtil.getFirst(dateList);
	fromDate=UtilDateTime.toTimestamp(dates.get("fromDate"));
	thruDate=UtilDateTime.toTimestamp(dates.get("thruDate"));
}
context.fromDate=fromDate;
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDate);
emplInputMap.put("thruDate", thruDate);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
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
			basicSalAndGradeMap=PayrollService.fetchBasicSalaryAndGrade(dctx,[employeeId:employment.partyIdTo,timePeriodStart:fromDate, timePeriodEnd: thruDate, userLogin : userLogin, proportionalFlag:"N"]);
			if(basicSalAndGradeMap.get("amount")!=null){
			basic=basicSalAndGradeMap.get("amount");
			}
			daAmount=0;
			daAmountList=casteIds=delegator.findByAnd("PartyBenefit", [partyIdTo: employment.partyId,benefitTypeId:"PAYROL_BEN_DA"],["benefitTypeId"]);
			if(UtilValidate.isNotEmpty(daAmountList)){
				daAmountIds=daAmountList.get(0).benefitTypeId;
				daAmountMap=PayrollService.getPayHeadAmount(dctx,[userLogin:userLogin,payHeadTypeId:daAmountIds,employeeId:employment.partyId,customTimePeriodId:timePeriodId,locale:locale]);
				daAmount=daAmountMap.get("amount");
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

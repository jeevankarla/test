import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.entity.Delegator;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import java.lang.Integer;
import java.util.Calendar;
import org.ofbiz.base.util.UtilNumber;






dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate",fromDateStart);
context.put("thruDate",thruDateEnd);


List firstMonthList=FastList.newInstance();
firstMonthList.add("Jan");
firstMonthList.add("Feb");
firstMonthList.add("Mar");

List secondMonthList=FastList.newInstance();
secondMonthList.add("Apr");
secondMonthList.add("May");
secondMonthList.add("Jun");
secondMonthList.add("Jul");
secondMonthList.add("Aug");
secondMonthList.add("Sep");
secondMonthList.add("Oct");
secondMonthList.add("Nov");
secondMonthList.add("Dec");

Timestamp monthBegin;
String monthName = UtilDateTime.toDateString(fromDateStart,"MMM");

if(firstMonthList.contains(monthName)){
	currentYearStart =  UtilDateTime.getYearStart(fromDateStart);
	prevFromDate = UtilDateTime.addDaysToTimestamp(currentYearStart, -1);
	prevYearStart = UtilDateTime.getYearStart(prevFromDate);
	prevDate = UtilDateTime.addDaysToTimestamp(prevYearStart, 91);
	monthBegin = UtilDateTime.getMonthStart(prevDate);
}else{
	currentYearStart =  UtilDateTime.getYearStart(fromDateStart);
	tempFromDate = UtilDateTime.addDaysToTimestamp(currentYearStart, 91);
	monthBegin = UtilDateTime.getMonthStart(tempFromDate);
}


LicFinalMap=[:];
CumulativeFinalList=[:];
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDateStart);
emplInputMap.put("thruDate", thruDateEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");

//middleName:J, lastName:B
employeeIdsList=[];
employeeNameMap=[:];

if(UtilValidate.isNotEmpty(employments)){
	employments.each { employment ->
		nameMap=[:];
		employeeIdsList.add(employment.get("partyId"));
		nameMap.put("firstName",employment.get("firstName"));
		nameMap.put("middleName",employment.get("middleName"));
		nameMap.put("lastName",employment.get("lastName"));
		employeeNameMap.putAt(employment.get("partyId"),nameMap);
		
	}
}

finalList = [];

List LICconditionList=[];
LICconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employeeIdsList));
LICconditionList.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, (parameters.insuranceTypeId)));
LICconditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd));
LICconditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart)));
LICcondition=EntityCondition.makeCondition(LICconditionList,EntityOperator.AND);
orderInsuranceDetails = delegator.findList("PartyInsurance", LICcondition , null, null, null, false);
if(UtilValidate.isNotEmpty(orderInsuranceDetails)){
	sNo = 0 ;
	List tempLicDetailsList = FastList.newInstance();
	orderInsuranceDetails.each { insurance ->
		LicDetailsMap=[:];
		insuranceId=insurance.get("insuranceId");
		employeeFirstName=employeeNameMap.get(insurance.get("partyId")).get("firstName");
		employeeMiddleName=employeeNameMap.get(insurance.get("partyId")).get("middleName");
		employeeLastName=employeeNameMap.get(insurance.get("partyId")).get("lastName");
		if(UtilValidate.isNotEmpty(insurance.get("insuranceNumber"))){
			 referenceNo=new BigDecimal(insurance.get("insuranceNumber"));
		}
		employeeNo=insurance.get("partyId");
		amount=insurance.get("premiumAmount");
		LicDetailsMap.put("employeeFirstName",employeeFirstName);
		LicDetailsMap.put("employeeMiddleName",employeeMiddleName);
		LicDetailsMap.put("employeeLastName",employeeLastName);
		LicDetailsMap.put("referenceNo",referenceNo);
		LicDetailsMap.put("employeeNo",employeeNo);
		LicDetailsMap.put("amount",amount);
		LicDetailsMap.put("insuranceId",insuranceId);
		LicDetailsMap.put("sNo",sNo);
		tempLicDetailsList.add(LicDetailsMap);
	}
	tempLICList = UtilMisc.sortMaps(tempLicDetailsList, UtilMisc.toList("referenceNo"));
	tempLICList.each{ policy ->
		sNo = sNo + 1;
		LicInsuranceMap=[:];
		LicInsuranceMap.put("employeeFirstName",policy.employeeFirstName);
		LicInsuranceMap.put("employeeMiddleName",policy.employeeMiddleName);
		LicInsuranceMap.put("employeeLastName",policy.employeeLastName);
		LicInsuranceMap.put("referenceNo",policy.referenceNo);
		LicInsuranceMap.put("employeeNo",policy.employeeNo);
		LicInsuranceMap.put("amount",policy.amount);
		LicInsuranceMap.put("sNo",sNo);
		finalList.add(LicInsuranceMap);
		LicFinalMap.put(policy.insuranceId,LicInsuranceMap);
	}
}

context.put("finalList",finalList);
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

dctx = dispatcher.getDispatchContext();

fromDateStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDateStart = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.FromDate) {
		fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.FromDate).getTime()));
	}
	if (parameters.ThruDate) {
		thruDateStart = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.ThruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
orgId=parameters.partyId;

nowDate=UtilDateTime.nowTimestamp();
fromDate = UtilDateTime.getMonthStart(nowDate);
thruDate = UtilDateTime.getMonthEnd(nowDate,timeZone,locale);
Flag=parameters.reportFlag;
if(Flag=="daAmount")
result=dispatcher.runSync("getCustomTimePeriodId", [periodTypeId:"HR_MONTH",fromDate:fromDate,thruDate:thruDate,userLogin:userLogin]);

def populateChildren(org, employeeList) {
		EmploymentsMap=HumanresService.getActiveEmployements(dctx,[userLogin:userLogin,orgPartyId:parameters.partyId,fromDate:fromDateStart,thruDate:thruDateStart]);
		employments=EmploymentsMap.get("employementList");
	employments.each { employment ->
		employee = [:];
		group=delegator.findByAnd("PartyRelationshipAndDetail", [partyId: employment.partyIdFrom, partyTypeId : "PARTY_GROUP"],["groupName"]);
		employee.put("department", group.get(0).groupName);
		casteName="";
		casteIds=delegator.findByAnd("PartyClassification", [partyId: employment.partyId],["partyClassificationGroupId"]);
		if(UtilValidate.isNotEmpty(casteIds)){
			casteId=casteIds.get(0).partyClassificationGroupId;
			casteList=delegator.findByAnd("PartyClassificationGroup", [partyClassificationGroupId: casteId],["description"]);
			casteName=casteList.get(0).description;
			employee.put("caste",casteName);
		}
		else{employee.put("caste",casteName);}
		qual="";
		qualifications=delegator.findByAnd("PartyQual",[partyId:employment.partyId],["title"]);
		if(UtilValidate.isNotEmpty(qualifications)){
			qual=qualifications.get(0).title;
		 }
		employee.put("qual",qual);
		daAmount=0;
		if(Flag=="daAmount"){
			daAmountList=casteIds=delegator.findByAnd("PartyBenefit", [partyIdTo: employment.partyId,benefitTypeId:"PAYROL_BEN_DA"],["benefitTypeId"]);
			if(UtilValidate.isNotEmpty(daAmountList)){
				daAmountIds=daAmountList.get(0).benefitTypeId;
				daAmountMap=PayrollService.getPayHeadAmount(dctx,[userLogin:userLogin,payHeadTypeId:daAmountIds,employeeId:employment.partyId,customTimePeriodId:result.get("customTimePeriodId"),locale:locale]);
				daAmount=daAmountMap.get("amount");
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
		employee.put("employeeId", employment.partyId);
		joinDate = UtilDateTime.toDateString(employment.appointmentDate, "dd/MM/yyyy");
		employee.put("joinDate", joinDate)
		resignDate=UtilDateTime.toDateString(employment.resignationDate,"dd/MM/yyyy");
		employee.put("resignDate",resignDate);
		partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: employment.partyId, userLogin: userLogin]);
		phoneNumber = "";
		if (partyTelephone != null && partyTelephone.contactNumber != null) {
			phoneNumber = partyTelephone.contactNumber;
		}
		employee.put("phoneNumber", phoneNumber);
		partyEmail= dispatcher.runSync("getPartyEmail", [partyId: employment.partyId, userLogin: userLogin]);
		emailAddress="";
		emailAddress=partyEmail.emailAddress;
		employee.put("emailAddress",emailAddress);
		employee.put("locationGeoId",employment.locationGeoId);
		employee.put("gender",employment.gender);
		employee.put("bloodGroup",employment.bloodGroup);
		address = "";
		if (employment.birthDate) {
			int day =  UtilDateTime.getDayOfMonth(UtilDateTime.toTimestamp(employment.birthDate), timeZone, locale);
			int month = UtilDateTime.getMonth(UtilDateTime.toTimestamp(employment.birthDate), timeZone, locale) + 1;
			if (day == 1) { // need to take the prev month last date
				month--;
			}
			int year = UtilDateTime.getYear(UtilDateTime.toTimestamp(employment.birthDate), timeZone, locale) + 60;
			retirementDate = UtilDateTime.toTimestamp(month, day, year, 0, 0, 0);
			retirementDate = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(retirementDate), timeZone, locale);
			retirementDate=UtilDateTime.toDateString(retirementDate,"dd/MM/yyyy");
			employee.put("retirementDate",retirementDate);
		}
		basicSalAndGradeMap=PayrollService.fetchBasicSalaryAndGrade(dctx,[employeeId:employment.partyIdTo,timePeriodStart:fromDate, timePeriodEnd: thruDate, userLogin : userLogin, proportionalFlag:"N"]);
		partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId: employment.partyId, userLogin: userLogin]);
		address="";
		if (partyPostalAddress != null) {
			if(partyPostalAddress.address1==null){partyPostalAddress.address1="";}
			if(partyPostalAddress.address2==null){partyPostalAddress.address2="";}
			if(partyPostalAddress.city==null){partyPostalAddress.city="";}
			if(partyPostalAddress.postalCode==null){partyPostalAddress.postalCode="";}
			address = partyPostalAddress.address1+partyPostalAddress.address2+partyPostalAddress.city+partyPostalAddress.postalCode;
		}
		employee.put("address", address);
		employee.put("amount",basicSalAndGradeMap.get("amount"));
		employee.put("payGradeId",basicSalAndGradeMap.get("payGradeId"));
		employment.fromDate=UtilDateTime.toDateString(employment.fromDate,"dd/MM/yyyy");
		employee.put("fromDate",employment.fromDate);
		employeeList.add(employee);
	}
}
	
employeeList = [];
internalOrgs=[];
context.internalOrgs=internalOrgs;
context.employeeList=employeeList;
if(UtilValidate.isEmpty(parameters.partyId)){
	parameters.partyId = "Company";
	
}
company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : parameters.partyId]);
populateChildren(company, employeeList);
JSONArray employeesJSON = new JSONArray();
employeeList.each {employee ->
	JSONArray employeeJSON = new JSONArray();
	employeeJSON.add(employee.name);
	employeeJSON.add(employee.employeeId);
	employeeJSON.add(employee.department);
	employeeJSON.add(employee.position);
	employeeJSON.add(employee.joinDate);
	employeeJSON.add(employee.phoneNumber);
	//employeeJSON.add(employee.address);
	employeesJSON.add(employeeJSON);
}
context.employeesJSON = employeesJSON;
//Debug.logError("employeesJSON="+employeesJSON,"");



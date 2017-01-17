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

Flag=parameters.reportFlag;
if(Flag=="daAmount")
result=dispatcher.runSync("getCustomTimePeriodId", [periodTypeId:"HR_MONTH",fromDate:fromDateStart,thruDate:thruDateStart,userLogin:userLogin]);
def populateChildren(org, employeeList) {
		EmploymentsMap=HumanresService.getActiveEmployements(dctx,[userLogin:userLogin,orgPartyId:parameters.partyId,fromDate:fromDateStart,thruDate:thruDateStart]);
		employments=EmploymentsMap.get("employementList");
	employments.each { employment ->
		
		employee = [:];
		group=delegator.findByAnd("PartyRelationshipAndDetail", [partyId: employment.partyIdFrom, partyTypeId : "PARTY_GROUP"],["groupName"]);
		if(UtilValidate.isNotEmpty(group))
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
		panId="";
		panIds=delegator.findByAnd("PartyIdentification",[partyId:employment.partyId, partyIdentificationTypeId:"PAN_NUMBER"],["idValue"]);
		if(UtilValidate.isNotEmpty(panIds)){
			panId=panIds.get(0).idValue;
		 }
		employee.put("panId",panId);
		aadharId = "";
		aadharIds=delegator.findByAnd("PartyIdentification",[partyId:employment.partyId, partyIdentificationTypeId:"ADR_NUMBER"],["idValue"]);
		Debug.log("aadharIds==========================="+aadharIds);
		if(UtilValidate.isNotEmpty(aadharIds)){
			aadharId=aadharIds.get(0).idValue;
		}
		employee.put("aadharId",aadharId);
		
		
		
		personaldetails = [];
		String fatherName="";
		String motherName="";
		String spouseName="";
		String passportNumber="";
		String  religion="";
		if(UtilValidate.isNotEmpty(employment.partyId)){
			
		personaldetails = delegator.findOne("Person", [partyId : employment.partyId], false);
			
		// Debug.log("personaldetails=================="+personaldetails);
		 if(UtilValidate.isNotEmpty(employment.partyId)){
			fatherName =personaldetails.fatherName;
			motherName =personaldetails.motherName;
			spouseName =personaldetails.spouseName;
			passportNumber =personaldetails.passportNumber;
			religion = personaldetails.religion;
			/*Debug.log("fatherName=================="+fatherName);
			Debug.log("motherName=================="+motherName);
			Debug.log("spouseName=================="+spouseName);
			Debug.log("passportNumber=================="+passportNumber);
			employee.put("fatherName",fatherName);*/
			employee.put("motherName",motherName);
			employee.put("spouseName",spouseName);
			employee.put("passportNumber",passportNumber);
			employee.put("religion",religion);
		 }
		}
		
		
		
		
		 conditionpayList=[];
		 PayHistoryDetails = [];
		//List payGradeDetails=[];
		if(UtilValidate.isNotEmpty(employment.partyId)){
			conditionpayList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employment.partyId));
		}
			conditionpayList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		
		condition1=EntityCondition.makeCondition(conditionpayList,EntityOperator.AND);
		PayHistoryDetails= delegator.findList("PayHistory", condition1, null, null, null, false );
		String emplpayGradeId = "";
		String PayScale="";
		if(UtilValidate.isNotEmpty(PayHistoryDetails)){
		
		emplpayGradeId = EntityUtil.getFirst(PayHistoryDetails).get("payGradeId");
		}
		payGradeDetails = delegator.findOne("PayGrade", [payGradeId : emplpayGradeId], false);
		if(UtilValidate.isNotEmpty(payGradeDetails)){
				PayScale=payGradeDetails.payScale;
		}
		//employeeList.add(PayScale);
		context.PayScale=PayScale;
		employee.put("PayScale", PayScale);
	//	Debug.log("payScale============"+PayScale);
		
		
		
		
		
		
		
		
		
		
		
		daAmount=0;
		if(UtilValidate.isNotEmpty(Flag) && Flag=="daAmount"){
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
		dateofBirth = UtilDateTime.toDateString(employment.birthDate, "dd/MM/yyyy");
		employee.put("dateofBirth",dateofBirth);
		if(UtilValidate.isNotEmpty(employment.bloodGroup)){
		bloodGroups=delegator.findByAnd("Enumeration",[enumId:employment.bloodGroup]);
		if(UtilValidate.isNotEmpty(bloodGroups)){
		bloodGroup=bloodGroups.get(0).description;
		employee.put("bloodGroup",bloodGroup);
		}
		else
		employee.put("bloodGroup",employment.bloodGroup);
		}
		
		 geodetails=[];
		String geoname="";
		if(UtilValidate.isNotEmpty(employment.partyId)){
			
		geodetails = delegator.findOne("Geo", [geoId : employment.locationGeoId], false);
		
		if(UtilValidate.isNotEmpty(geodetails)){
		geoname=geodetails.geoName;
		employee.put("geoName",geoname);
		}
		
		}
		
		
		
		
		
		 exprList = [];
		deptName = "";
		exprList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"DEPATMENT_NAME"));
		exprList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
		exprList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,employment.partyId));
		exprList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		exprCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		partyRelationshipList = delegator.findList("PartyRelationship", exprCond, null, null, null, false);
		
		if(UtilValidate.isNotEmpty(partyRelationshipList)){
			deptId = (EntityUtil.getFirst(partyRelationshipList)).get("partyIdFrom");
			partyGroupDetails = delegator.findOne("PartyGroup", [partyId : deptId], false);
			if(UtilValidate.isNotEmpty(partyGroupDetails)){
				deptName = 	partyGroupDetails.groupName;
			
				Debug.log("deptName==============="+deptName);
				}
		}
		employee.put("deptName",deptName);
		//Debug.log("deptName=============="+deptName);
		
		finAccountId="";
		finAccountName="";
		finAccountName="";
		finAccountIds =delegator.findByAnd("FinAccount",[ownerPartyId:employment.partyId]);
		if(UtilValidate.isNotEmpty(finAccountIds)){
			finAccountCode=finAccountIds.get(0).finAccountCode;
			finAccountName=finAccountIds.get(0).finAccountName;
			ifscCode=finAccountIds.get(0).ifscCode;
			employee.put("finAccountCode",finAccountCode);
			employee.put("finAccountName",finAccountName);
			employee.put("ifscCode",ifscCode);
			
		 }
		
		address = "";
		if (employment.birthDate) {
			int day =  UtilDateTime.getDayOfMonth(UtilDateTime.toTimestamp(employment.birthDate), timeZone, locale);
			int month = UtilDateTime.getMonth(UtilDateTime.toTimestamp(employment.birthDate), timeZone, locale) + 1;
			if (day == 1) { // need to take the prev month last date
				month--;
			}
			int year = UtilDateTime.getYear(UtilDateTime.toTimestamp(employment.birthDate), timeZone, locale) + 58;
			retirementDate = UtilDateTime.toTimestamp(month, day, year, 0, 0, 0);
			retirementDate = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(retirementDate), timeZone, locale);
			retirementDate=UtilDateTime.toDateString(retirementDate,"dd/MM/yyyy");
			employee.put("retirementDate",retirementDate);
		}
		if(UtilValidate.isNotEmpty(fromDateStart)){
			newFromDate=fromDateStart;
		}else{
				newFromDate=fromDate;
		}
		
		if(UtilValidate.isNotEmpty(thruDateStart)){
			newThruDate=thruDateStart;
		}else{
			newThruDate=thruDate;
		}
		
		basicSalAndGradeMap=PayrollService.fetchBasicSalaryAndGrade(dctx,[employeeId:employment.partyIdTo,timePeriodStart:newFromDate, timePeriodEnd: newThruDate, userLogin : userLogin, proportionalFlag:"N"]);
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
		List conditionList=[];
		if(UtilValidate.isNotEmpty(employment.partyId)){
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employment.partyId));
		}
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		empWeeklyOffDetails= delegator.findList("EmployeeDetail", condition, UtilMisc.toSet("weeklyOff","canteenFacin","companyBus"), null, null, false );
		weeklyOff="";
		canteenFacin="";
		companyBus="";
		if(UtilValidate.isNotEmpty(empWeeklyOffDetails)){
			details=EntityUtil.getFirst(empWeeklyOffDetails);
			weeklyOff=details.get("weeklyOff");
			canteenFacin=details.get("canteenFacin");
			companyBus=details.get("companyBus");
		}
		employee.put("weeklyOff",weeklyOff);
		employee.put("canteenFacin",canteenFacin);
		employee.put("companyBus",companyBus);
		
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
	//Debug.log("employeesJSON=========="+employeesJSON);
}
context.employeesJSON = employeesJSON;
//Debug.logError("employeesJSON="+employeesJSON,"");

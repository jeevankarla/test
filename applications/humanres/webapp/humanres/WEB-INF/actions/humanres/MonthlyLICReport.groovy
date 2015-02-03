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

import java.util.Calendar;
import org.ofbiz.base.util.UtilNumber;

dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");

GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
currentMonthEnd = UtilDateTime.getMonthEnd(fromDateStart, timeZone, locale);
context.put("fromDate",fromDateStart);
context.put("thruDate",thruDateEnd);
employeeNameMap = [:];
employeeIdsList=[];
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDateStart);
emplInputMap.put("thruDate", thruDateEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");

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

finalMap = [:];
policyDeletionMap = [:];
policyAddedMap = [:];
totalDeletnAmount = 0;
totalAddtnAmount = 0;
List LICDeletionconditionList=[];
LICDeletionconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employeeIdsList));
LICDeletionconditionList.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, (parameters.insuranceTypeId)));
//LICDeletionconditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd));
LICDeletionconditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart), EntityOperator.AND,
		EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, currentMonthEnd)));
LICDeletioncondition=EntityCondition.makeCondition(LICDeletionconditionList,EntityOperator.AND);
def orderBy = UtilMisc.toList("insuranceNumber");
deletionInsuranceDetails = delegator.findList("PartyInsurance", LICDeletioncondition , null, orderBy, null, false);
if(UtilValidate.isNotEmpty(deletionInsuranceDetails)){
	deletionInsuranceDetails.each { deletionPloicy ->
		detailsMap = [:];
		policyNo = deletionPloicy.get("insuranceNumber");
		partyId = deletionPloicy.get("partyId");
		employeeFirstName=employeeNameMap.get(partyId).get("firstName");
		employeeMiddleName=employeeNameMap.get(partyId).get("middleName");
		employeeLastName=employeeNameMap.get(partyId).get("lastName");
		premiumAmount = deletionPloicy.get("premiumAmount");
		insuranceId = deletionPloicy.get("insuranceId");
		totalDeletnAmount = totalDeletnAmount + premiumAmount;
		detailsMap.put("policyNo",policyNo);
		detailsMap.put("partyId",partyId);
		detailsMap.put("employeeFirstName",employeeFirstName);
		detailsMap.put("employeeMiddleName",employeeMiddleName);
		detailsMap.put("employeeLastName",employeeLastName);
		detailsMap.put("premiumAmount",premiumAmount);
		policyDeletionMap.put(insuranceId,detailsMap);
	}
}

List LICAddedconditionList=[];
LICAddedconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employeeIdsList));
LICAddedconditionList.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, (parameters.insuranceTypeId)));
//LICDeletionconditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd));
LICAddedconditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart), EntityOperator.AND,
		EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd)));
LICAdditioncondition=EntityCondition.makeCondition(LICAddedconditionList,EntityOperator.AND);
def orderBy1 = UtilMisc.toList("insuranceNumber");
additionInsuranceDetails = delegator.findList("PartyInsurance", LICAdditioncondition , null, orderBy1, null, false);
if(UtilValidate.isNotEmpty(additionInsuranceDetails)){
	additionInsuranceDetails.each { addednPloicy ->
		detailsMap1 = [:];
		policyNo = addednPloicy.get("insuranceNumber");
		partyId = addednPloicy.get("partyId");
		employeeFirstName=employeeNameMap.get(partyId).get("firstName");
		employeeMiddleName=employeeNameMap.get(partyId).get("middleName");
		employeeLastName=employeeNameMap.get(partyId).get("lastName");
		premiumAmount = addednPloicy.get("premiumAmount");
		insuranceId = addednPloicy.get("insuranceId");
		totalAddtnAmount = totalAddtnAmount + premiumAmount;
		detailsMap1.put("policyNo",policyNo);
		detailsMap1.put("partyId",partyId);
		detailsMap1.put("employeeFirstName",employeeFirstName);
		detailsMap1.put("employeeMiddleName",employeeMiddleName);
		detailsMap1.put("employeeLastName",employeeLastName);
		detailsMap1.put("premiumAmount",premiumAmount);
		policyAddedMap.put(insuranceId,detailsMap1);
	}
}
previousMonth =UtilDateTime.addDaysToTimestamp(fromDateStart, -1);
previousMonthStart = UtilDateTime.getMonthStart(previousMonth);
previousMonthEnd = UtilDateTime.getMonthEnd(previousMonth, timeZone, locale);

totalPreviousBalnace = 0;
List previousMonthClosingList=[];
previousMonthClosingList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employeeIdsList));
previousMonthClosingList.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, (parameters.insuranceTypeId)));
//LICDeletionconditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd));
previousMonthClosingList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, previousMonthStart), EntityOperator.AND,
		EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, previousMonthEnd)));
previousMonthCondition=EntityCondition.makeCondition(previousMonthClosingList,EntityOperator.AND);
previousClosingBalDetails = delegator.findList("PartyInsurance", previousMonthCondition , null, null, null, false);
if(UtilValidate.isNotEmpty(previousClosingBalDetails)){
	previousClosingBalDetails.each { previousPloicy ->
		previousAmount = previousPloicy.get("premiumAmount");
		totalPreviousBalnace = totalPreviousBalnace + previousAmount;
	}
}


context.put("totalPreviousBalnace",totalPreviousBalnace);

finalMap.put("policyDelitions",policyDeletionMap);
finalMap.put("policyAdditions",policyAddedMap);

context.put("employeeNameMap",employeeNameMap);
context.put("finalMap",finalMap);

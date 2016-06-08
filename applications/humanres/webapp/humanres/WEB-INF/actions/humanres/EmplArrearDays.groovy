import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.math.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import java.math.BigDecimal;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.humanres.HumanresHelperServices;


dctx = dispatcher.getDispatchContext();
customTimePeriodId = parameters.customTimePeriodId;
emplType = parameters.emplType;
Debug.log("emplType==="+emplType);
partyId=parameters.partyId;

temporaryFlag = "";

Timestamp timePeriodStart = null;
Timestamp timePeriodEnd = null;
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if(UtilValidate.isNotEmpty(customTimePeriod)) {
	timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
	timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
	
	fromDateMonth = UtilDateTime.toDateString(timePeriodStart, "MMMMM,yyyy");
	context.put("fromDateMonth",fromDateMonth);
}

context.put("customTimePeriodId",customTimePeriodId);


employementIds = [];
if(UtilValidate.isNotEmpty(customTimePeriod)) {
	if(emplType.equals("Regular")){
		List employmentList = FastList.newInstance();
		employmentList.add(EntityCondition.makeCondition("fromDate" ,EntityOperator.LESS_THAN_EQUAL_TO ,timePeriodEnd ));
		employmentList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
		employmentList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("temporaryFlag", EntityOperator.EQUALS, "N"), EntityOperator.OR, EntityCondition.makeCondition("temporaryFlag", EntityOperator.EQUALS, null)));
		EntityCondition empCondition = EntityCondition.makeCondition(employmentList, EntityOperator.AND);
		List<GenericValue> employments = delegator.findList("Employment", empCondition, null, UtilMisc.toList("partyIdTo"), null, false);
		if(UtilValidate.isNotEmpty(employments)){
			employementIds = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
		}
	}else{
		List employmentList = FastList.newInstance();
		employmentList.add(EntityCondition.makeCondition("fromDate" ,EntityOperator.LESS_THAN_EQUAL_TO ,timePeriodEnd ));
		employmentList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
		employmentList.add(EntityCondition.makeCondition("temporaryFlag" ,EntityOperator.EQUALS ,"Y"));
		EntityCondition empCondition = EntityCondition.makeCondition(employmentList, EntityOperator.AND);
		List<GenericValue> employments = delegator.findList("Employment", empCondition, null, UtilMisc.toList("partyIdTo"), null, false);
		if(UtilValidate.isNotEmpty(employments)){
			employementIds = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
			
			
		}
	}
}


/*Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
if(UtilValidate.isEmpty(customTimePeriod)) {
	emplInputMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-60)));
	emplInputMap.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()));
}else{
	emplInputMap.put("fromDate", timePeriodStart);
	emplInputMap.put("thruDate", timePeriodEnd);
}
if(UtilValidate.isNotEmpty(temporaryFlag) && temporaryFlag.equals("Y")){
	emplInputMap.put("temporaryFlag", temporaryFlag);
}
Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);*/


if(parameters.partyId){
	employementList=UtilMisc.toList(parameters.partyId);
}else{
	employementList=employementIds;
}

JSONArray employeeArrearDaysJSON = new JSONArray();
employementList.each{employee ->
	JSONObject arrearObj = new JSONObject();
	employeeName = "";
	
	personDetails = delegator.findOne("Person", UtilMisc.toMap("partyId",employee), false);
	if(UtilValidate.isNotEmpty(personDetails)){
		String firstName="";
		String middleName="";
		String lastName="";
		
		if(UtilValidate.isNotEmpty(personDetails.get("firstName"))){
			firstName = personDetails.get("firstName");
		}
		if(UtilValidate.isNotEmpty(personDetails.get("middleName"))){
			middleName = personDetails.get("middleName");
		}
		if(UtilValidate.isNotEmpty(personDetails.get("lastName"))){
			lastName = personDetails.get("lastName");
		}
		employeeName = firstName +" "+ middleName +" "+ lastName;
	}
	
	if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
		conditionList1 = [];
		conditionList1.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,employee));
		conditionList1.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,parameters.customTimePeriodId));
		condition1 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
		payrollAttendanceArrears = delegator.findList("PayrollAttendanceArrears", condition1 , null, null, null, false );
		if(UtilValidate.isNotEmpty(payrollAttendanceArrears)){
			payrollAttendanceArrears.each{arrear ->
				noOfArrearDays = arrear.get("noOfArrearDays");
				basicSalCustomTimePeriodId = arrear.get("basicSalCustomTimePeriodId");
				basicPay = 0;
				Timestamp basicTimePeriodStart = null;
				GenericValue customTimePeriodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId : basicSalCustomTimePeriodId], false);
				Debug.log("customTimePeriodDetails===="+customTimePeriodDetails);
				if(UtilValidate.isNotEmpty(customTimePeriodDetails)) {
					basicTimePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("fromDate")));
					Debug.log("basicTimePeriodStart===="+basicTimePeriodStart);
					basicTimePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("thruDate")));
					Debug.log("basicTimePeriodEnd===="+basicTimePeriodEnd);
					List conditionList1 = FastList.newInstance();
					conditionList1.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,employee));
					conditionList1.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"INTERNAL_ORGANIZATIO"));
					conditionList1.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
					conditionList1.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, basicTimePeriodEnd));
					conditionList1.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, basicTimePeriodStart), EntityOperator.OR,
						EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
					EntityCondition condition1 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
					Debug.log("condition1====="+condition1);
					PayHistory = delegator.findList("PayHistory", condition1, null, null, null, false);
					if(UtilValidate.isNotEmpty(PayHistory)){
						GenericValue PayHistoryList = EntityUtil.getFirst(PayHistory);
						salaryStepSeqId = PayHistoryList.get("salaryStepSeqId");
						payGradeId = PayHistoryList.get("payGradeId");
						payRateSalary = delegator.findOne("SalaryStep", [salaryStepSeqId : salaryStepSeqId,payGradeId : payGradeId], false);
						if(UtilValidate.isNotEmpty(payRateSalary)){
							basicPay = payRateSalary.get("amount");
						}
					}
				}
				
				basicSalDateStr = UtilDateTime.toDateString(basicTimePeriodStart ,"dd/MM/yyyy");
				arrearObj.put("id",employee + " [" + employeeName + "]");
				arrearObj.put("employeeId",employee + " [" + employeeName + "]");
				arrearObj.put("noOfArrearDays",noOfArrearDays);
				arrearObj.put("basicSalDate",basicSalDateStr);
				arrearObj.put("actualBasic",basicPay);
				employeeArrearDaysJSON.add(arrearObj);
			}
			
		}
	}
}


JSONArray employeesJSON = new JSONArray();
JSONObject emplIdLabelJSON = new JSONObject();
JSONObject emplLabelIdJSON=new JSONObject();
employementIds.each{employeeId ->
	JSONObject newObj = new JSONObject();
	
	employeeName = "";
	
	personDetails = delegator.findOne("Person", UtilMisc.toMap("partyId",employeeId), false);
	if(UtilValidate.isNotEmpty(personDetails)){
		String firstName="";
		String middleName="";
		String lastName="";
		
		if(UtilValidate.isNotEmpty(personDetails.get("firstName"))){
			firstName = personDetails.get("firstName");
		}
		if(UtilValidate.isNotEmpty(personDetails.get("middleName"))){
			middleName = personDetails.get("middleName");
		}
		if(UtilValidate.isNotEmpty(personDetails.get("lastName"))){
			lastName = personDetails.get("lastName");
		}
		employeeName = firstName +" "+ middleName +" "+ lastName;
	}
	
	
	newObj.put("EmployeeId",employeeId + " [" + employeeName + "]");
	newObj.put("label",employeeId + " [" + employeeName + "]");
	employeesJSON.add(newObj);
	emplIdLabelJSON.put(employeeId, employeeName);
	emplLabelIdJSON.put(employeeId + " [" + employeeName + "]", employeeId);
}
context.employeesJSON = employeesJSON;
context.emplIdLabelJSON = emplIdLabelJSON;
context.emplLabelIdJSON = emplLabelIdJSON;
context.employeeArrearDaysJSON = employeeArrearDaysJSON;


/*JSONArray periodJSON = new JSONArray();
JSONObject periodLabelIdJSON = new JSONObject();
customTimePeriodList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"), null, ["-fromDate"], null, false);
if(UtilValidate.isNotEmpty(customTimePeriodList)){
	customTimePeriodList.each {  periodList ->
		JSONObject newObj1 = new JSONObject();
		fromDate = UtilDateTime.toDateString(periodList.fromDate, "dd/MM/yyyy");
		thruDate = UtilDateTime.toDateString(periodList.thruDate, "dd/MM/yyyy");
		newObj1.put("id",periodList.customTimePeriodId);
		newObj1.put("label",fromDate + " - " +thruDate);
		periodJSON.add(newObj1);
		periodLabelIdJSON.put(fromDate + " - " +thruDate, periodList.customTimePeriodId);
	}
}

context.periodJSON = periodJSON;
context.periodLabelIdJSON = periodLabelIdJSON;*/





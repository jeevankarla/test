import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();

GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate",timePeriodStart);
context.put("thruDate",timePeriodEnd);

emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}
TypeconditionList=[];
if((parameters.EmplType).equals("MDStaff")){
	TypeconditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"EMPLOYEE"));
}
if((parameters.EmplType).equals("DeputationStaff")){
	TypeconditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"DEPUTATION_EMPLY"));
}
TypeconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN ,employmentsList));
emplTypecondition = EntityCondition.makeCondition(TypeconditionList,EntityOperator.AND);
StaffList = delegator.findList("PartyRole", emplTypecondition, null, null, null, false);

if((parameters.EmplType).equals("MDStaff")){
	condList = [];
	condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN ,EntityUtil.getFieldListFromEntityList(StaffList, "partyId", true)));
	condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"DEPUTATION_EMPLY"));
	typecondn = EntityCondition.makeCondition(condList,EntityOperator.AND);
	depStaffList = delegator.findList("PartyRole", typecondn, null, null, null, false);
	StaffList = EntityUtil.filterByCondition(StaffList, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, EntityUtil.getFieldListFromEntityList(depStaffList, "partyId", true)));
}
if(UtilValidate.isNotEmpty(StaffList)){
	partyIdsList = EntityUtil.getFieldListFromEntityList(StaffList, "partyId", true);
}

finalGSLISMap=[:];
conditionList=[];
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN ,UtilMisc.toList("GENERATED","APPROVED")));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
payrollDetailsList = delegator.findList("PeriodBillingAndCustomTimePeriod", condition, null, null, null, false);
if(UtilValidate.isNotEmpty(payrollDetailsList)){
	payrollDetailsList.each { payrollDetails->
		billingId=payrollDetails.get("periodBillingId");
		deductionConditionList=[];
		deductionConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,billingId));
		deductionConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,partyIdsList));
		deductionConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS ,"PAYROL_DD_GR_SAVG"));
		typecondition = EntityCondition.makeCondition(deductionConditionList,EntityOperator.AND);
		def orderBy = UtilMisc.toList("amount","partyIdFrom");
		GSLISTypeList = delegator.findList("PayrollHeaderAndHeaderItem", typecondition, null, orderBy, null, false);
		if(UtilValidate.isNotEmpty(GSLISTypeList)){
			GSLISTypeList.each { GSLISemployee->
				GSLISMap=[:];
				typeId=GSLISemployee.get("payrollHeaderItemTypeId");
				partyId=GSLISemployee.get("partyIdFrom");
				amount=GSLISemployee.get("amount");
				if(amount<0 && amount!=0){
					amount=amount*(-1);
				}
				String partyName = PartyHelper.getPartyName(delegator, partyId, false);
				GSLISMap.put("partyId",partyId);
				GSLISMap.put("amount",amount);
				GSLISMap.put("partyName",partyName);
				GSLISMap.put("typeId",typeId);
				finalGSLISMap.put(partyId,GSLISMap);
			}
		}
	}
}
else{
	deductionList=[];
	deductionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO ,timePeriodStart));
	deductionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,partyIdsList));
	deductionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodEnd)));
	deductionList.add(EntityCondition.makeCondition("deductionTypeId", EntityOperator.EQUALS , "PAYROL_DD_GR_SAVG"));
	cond =EntityCondition.makeCondition(deductionList,EntityOperator.AND);
	def partyorderBy = UtilMisc.toList("cost","partyIdTo");
	partyDeductionList = delegator.findList("PartyDeduction", cond, null, partyorderBy, null, false);
	if(UtilValidate.isNotEmpty(partyDeductionList)){
		partyDeductionList.each { employee->
			payrollMap=[:];
			typeId=employee.get("payrollHeaderItemTypeId");
			partyId=employee.get("partyIdTo");
			amount=employee.get("cost");
			String partyName = PartyHelper.getPartyName(delegator, partyId, false);
			payrollMap.put("partyId",partyId);
			payrollMap.put("amount",amount);
			payrollMap.put("partyName",partyName);
			payrollMap.put("typeId",typeId);
			finalGSLISMap.put(partyId,payrollMap);
		}
	}
}
context.putAt("finalGSLISMap",finalGSLISMap);


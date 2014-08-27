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

GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate",timePeriodStart);
context.put("thruDate",timePeriodEnd);

finalGSLISMap=[:];
conditionList=[];
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"GENERATED"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
payrollDetailsList = delegator.findList("PeriodBillingAndCustomTimePeriod", condition, null, null, null, false);
if(UtilValidate.isNotEmpty(payrollDetailsList)){
	payrollDetailsList.each { payrollDetails->
		billingId=payrollDetails.get("periodBillingId");
		deductionConditionList=[];
		deductionConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,billingId));
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


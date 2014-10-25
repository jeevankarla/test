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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.humanres.PayrollService;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;
dctx = dispatcher.getDispatchContext();

periodList = [];
customTimePeriodId=parameters.customTimePeriodId;

if (UtilValidate.isEmpty(customTimePeriodId)) {
	Debug.logError("customTimePeriodId cannot be empty");
	context.errorMessage = "customTimePeriodId cannot be empty";
	return;
}
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
context.timePeriodStart= timePeriodStart;
context.timePeriodEnd= timePeriodEnd;

benefitTypeList = delegator.findList("BenefitsDetails", null, null, ["sequenceNum"], null, false);

benefitTypeIds = EntityUtil.getFieldListFromEntityList(benefitTypeList, "benefitTypeId", true);
if(benefitTypeIds.contains(parameters.benefitTypeId)){
	benefitTypeIds=UtilMisc.toList(parameters.benefitTypeId);
}else{
	benefitTypeIds=benefitTypeIds;
}
context.benefitTypeIds=benefitTypeIds;

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

Map benefitFinalMap=FastMap.newInstance();
periodBillingIdMap=[:];
List periodbillingConditionList=[];
periodbillingConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
periodbillingConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
periodbillingConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , parameters.partyId));
periodbillingCondition = EntityCondition.makeCondition(periodbillingConditionList,EntityOperator.AND);
BillingList = delegator.findList("PeriodBillingAndCustomTimePeriod", periodbillingCondition, null, null, null, false);
if(UtilValidate.isNotEmpty(BillingList)){
	BillingId = BillingList.periodBillingId;
	benefitTypeIds.each{ benTypeId->
		if(UtilValidate.isNotEmpty(employmentsList)){
			periodTotalsMap=[:];
			employmentsList.each{ employeeId ->
				detailsMap=[:];
				if(UtilValidate.isNotEmpty(BillingId)){
					List headerConditionList=[];
					headerConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, BillingId));
					headerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
					headerCondition = EntityCondition.makeCondition(headerConditionList,EntityOperator.AND);
					headerIdsList = delegator.findList("PayrollHeader", headerCondition, null, null, null, false);
					if(UtilValidate.isNotEmpty(headerIdsList)){
						benefitAmt=0;
						headerIdsList.each{ headerId ->
							headerId = headerId.payrollHeaderId;
							benefitAmount = 0;
							List benefitsList=[];
							benefitsList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
							benefitsList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
							benefitsList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, benTypeId));
							benefitCondition = EntityCondition.makeCondition(benefitsList,EntityOperator.AND);
							payrollHeaderList = delegator.findList("PayrollHeaderAndHeaderItem", benefitCondition, null, null, null, false);
							if(UtilValidate.isNotEmpty(payrollHeaderList)){
								payrollHeaderList.each{ payrollList ->
									benefitAmount = payrollList.amount;
									benefitAmt = benefitAmt+benefitAmount;
								}
							}
							detailsMap.put("benefitAmt",benefitAmt);
						}
					}
				}
				if(UtilValidate.isNotEmpty(detailsMap)){
					periodTotalsMap.put(employeeId,detailsMap);
				}
			}
		}
		if(UtilValidate.isNotEmpty(periodTotalsMap)){
			benefitFinalMap.put(benTypeId,periodTotalsMap);
		}
	}
	context.put("benefitFinalMap",benefitFinalMap);
}else{
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN , employmentsList));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	def orderBy = UtilMisc.toList("cost","partyIdTo");
	List<GenericValue> partyBenefitList = delegator.findList("PartyBenefit", condition, null, orderBy, null, false);
	periodTotalsMap=[:];
	if(UtilValidate.isNotEmpty(partyBenefitList)){
		partyBenefitList.each{ partyBenefit->
			benefitEmployeeId= partyBenefit.partyIdTo;
			headerItemTypeId=partyBenefit.benefitTypeId;
			benefitTypeIds.each{ benTypeId->
				if(benTypeId==headerItemTypeId){
					if(UtilValidate.isNotEmpty(employmentsList)){
						employmentsList.each{ employeeId ->
							detailsMap=[:];
							if(benefitEmployeeId==employeeId){
								benefitAmt=0;
								benefitAmt= partyBenefit.cost;
								if(UtilValidate.isEmpty(benefitAmt)){
									benefitAmt = 0;
								}else{
									detailsMap.put("benefitAmt", benefitAmt);
								}
							}
							if(UtilValidate.isNotEmpty(detailsMap)){
								periodTotalsMap.put(employeeId,detailsMap);
							}
						}
					}
					if(UtilValidate.isNotEmpty(periodTotalsMap)){
						benefitFinalMap.put(benTypeId,periodTotalsMap);
					}
				}
			}
		}
	}
}
context.put("benefitFinalMap",benefitFinalMap);

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
if (parameters.customTimePeriodId == null) {
	return;
}
dctx = dispatcher.getDispatchContext();
context.put("type",parameters.type);
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
context.timePeriodStart= timePeriodStart;

context.timePeriodEnd= timePeriodEnd;

//getting benefits
benefitTypeList = delegator.findList("BenefitType", EntityCondition.makeCondition("benefitTypeId", EntityOperator.NOT_EQUAL ,"PAYROL_BEN_SALARY"), null, ["sequenceNum"], null, false);
benefitDescMap=[:];
if(UtilValidate.isNotEmpty(benefitTypeList)){
	benefitTypeList.each{ benefit->
		benefitName =  benefit.get("benefitName");
		benefitType = benefit.get("benefitTypeId");
		benefitDescMap.put(benefitType,benefitName);
	}
}
benefitTypeIds = EntityUtil.getFieldListFromEntityList(benefitTypeList, "benefitTypeId", true);

if(benefitTypeIds.contains(parameters.benefitTypeId)){
	context.benefitTypeIds=UtilMisc.toList(parameters.benefitTypeId);
}else{
	context.benefitTypeIds=benefitTypeIds;
}
context.benefitDescMap=benefitDescMap;
//getting deductions

deductionTypeList = delegator.findList("DeductionType", null, null, ["sequenceNum"], null, false);
dedDescMap=[:];
if(UtilValidate.isNotEmpty(deductionTypeList)){
	deductionTypeList.each{ deduction->
		dedName =  deduction.get("deductionName");
		dedType = deduction.get("deductionTypeId");
		dedDescMap.put(dedType,dedName);
	}
}
dedTypeIds = EntityUtil.getFieldListFromEntityList(deductionTypeList, "deductionTypeId", true);
if(dedTypeIds.contains(parameters.dedTypeId)){
	context.dedTypeIds=UtilMisc.toList(parameters.dedTypeId);
}else{
	context.dedTypeIds=dedTypeIds;
}
context.dedDescMap=dedDescMap;

conditionList = [];
if (UtilValidate.isNotEmpty(parameters.employeeId)) {
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , parameters.employeeId));
}
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodStart));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodEnd)));
EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<GenericValue> partyBenefitList = delegator.findList("PartyBenefit", condition, null, ["partyIdTo"], null, false);

Debug.logError("partyBenefitList="+partyBenefitList,"");

List<GenericValue> partyDeductionList = delegator.findList("PartyDeduction", condition, null, ["partyIdTo"], null, false);

Map benefitTypeFinalMap=FastMap.newInstance();

if(UtilValidate.isNotEmpty(partyBenefitList)){
	partyBenefitList.each{ partyBenefit->
		employeeId= partyBenefit.partyIdTo;
		amount= partyBenefit.cost;
		headerItemTypeId=partyBenefit.benefitTypeId;
		if(UtilValidate.isNotEmpty(amount)){
			if(UtilValidate.isEmpty(benefitTypeFinalMap.get(employeeId))){
				Map tempBenefitMap=FastMap.newInstance();
				tempBenefitMap.put(headerItemTypeId,amount);
				benefitTypeFinalMap.put(employeeId,tempBenefitMap);
			}else{
				Map tempBenfMap=FastMap.newInstance();
					tempBenfMap.putAll(benefitTypeFinalMap.get(employeeId));
					tempBenfMap.put(headerItemTypeId,amount);
					benefitTypeFinalMap.put(employeeId,tempBenfMap);
			}
		}		
		
	}
}
Debug.logError("benefitTypeFinalMap="+benefitTypeFinalMap,"");

JSONArray headBenefitItemsJSON = new JSONArray();
Map totalBenefitsMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(benefitTypeFinalMap)){
	Iterator BenfIter = benefitTypeFinalMap.entrySet().iterator();
	while(BenfIter.hasNext()){
		Map.Entry entry = BenfIter.next();
		emplyId= entry.getKey();
		JSONObject newObj = new JSONObject();
		partyName=PartyHelper.getPartyName(delegator, emplyId, false);
		departmentDetails=delegator.findByAnd("Employment", [partyIdTo : emplyId]);
		deptName="";
		if(departmentDetails){
			deptPartyId=departmentDetails[0].partyIdFrom;
			deptName=PartyHelper.getPartyName(delegator, deptPartyId, false);
		}
		newObj.put("id",emplyId+"["+partyName+"]");		
		newObj.put("partyId",emplyId);
		newObj.put("periodId",parameters.customTimePeriodId);
		if(UtilValidate.isNotEmpty(deptName)){
			newObj.put("deptName",deptName);
		}
		if(UtilValidate.isNotEmpty(entry.getValue())){
			Iterator headerItemIter = (entry.getValue()).entrySet().iterator();
			while(headerItemIter.hasNext()){
				Map.Entry itemEntry = headerItemIter.next();
				benefitAmt=((itemEntry.getValue())).setScale(0,BigDecimal.ROUND_HALF_UP);
				if(UtilValidate.isEmpty(totalBenefitsMap[itemEntry.getKey()])){
					totalBenefitsMap[itemEntry.getKey()]=benefitAmt;
				}else{
					totalBenefitsMap[itemEntry.getKey()]+=benefitAmt;
				}
				newObj.put(itemEntry.getKey(),((itemEntry.getValue())).setScale(0,BigDecimal.ROUND_HALF_UP));
			}
		}
		headBenefitItemsJSON.add(newObj);
	}
}
Map deductionTypeValueMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(partyDeductionList)){
	partyDeductionList.each{ partyDed->
		employeeId= partyDed.partyIdTo;
		amount= partyDed.cost;
		headerItemTypeId=partyDed.deductionTypeId;
		if(UtilValidate.isNotEmpty(amount)){
			if(UtilValidate.isEmpty(deductionTypeValueMap.get(employeeId))){
				Map tempDedMap=FastMap.newInstance();
				tempDedMap.put(headerItemTypeId,amount);
				deductionTypeValueMap.put(employeeId,tempDedMap);
			}else{
				Map tempDedWiseMap=FastMap.newInstance();
					tempDedWiseMap.putAll(deductionTypeValueMap.get(employeeId));
					tempDedWiseMap.put(headerItemTypeId,amount);
					deductionTypeValueMap.put(employeeId,tempDedWiseMap);
			}
		}
		
	}
}

JSONArray headItemsJSON = new JSONArray();
if(UtilValidate.isNotEmpty(deductionTypeValueMap)){
	Iterator dedIter = deductionTypeValueMap.entrySet().iterator();
	while(dedIter.hasNext()){
		Map.Entry entry = dedIter.next();
		emplyId= entry.getKey();
		JSONObject newObj = new JSONObject();
		partyName=PartyHelper.getPartyName(delegator, emplyId, false);
		newObj.put("id",emplyId+"["+partyName+"]");	
		partyName=PartyHelper.getPartyName(delegator, emplyId, false);
		departmentDetails=delegator.findByAnd("Employment", [partyIdTo : emplyId]);
		deptName="";
		if(departmentDetails){
			deptPartyId=departmentDetails[0].partyIdFrom;
			deptName=PartyHelper.getPartyName(delegator, deptPartyId, false);
		}
		if(UtilValidate.isNotEmpty(deptName)){
			newObj.put("deptName",deptName);
		}
		newObj.put("periodId",parameters.customTimePeriodId);
		newObj.put("partyId",emplyId);
		if(UtilValidate.isNotEmpty(entry.getValue())){
			Iterator headerItemIter = (entry.getValue()).entrySet().iterator();
			while(headerItemIter.hasNext()){
				Map.Entry itemEntry = headerItemIter.next();
				newObj.put(itemEntry.getKey(),((itemEntry.getValue())).setScale(0,BigDecimal.ROUND_HALF_UP));
			}
		}
		headItemsJSON.add(newObj);
	}
}
if("benefits".equals(parameters.type)){
	context.headItemsJson=headBenefitItemsJSON;
}else{
	context.headItemsJson=headItemsJSON;
}

Debug.logError("context.headItemsJson="+context.headItemsJson,"");

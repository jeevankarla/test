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
import in.vasista.vbiz.humanres.HumanresHelperServices;
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

orgId=parameters.partyId;
if(parameters.partyId){
	orgId=parameters.partyId;
}


Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
if(UtilValidate.isNotEmpty(orgId)){
	emplInputMap.put("orgPartyId", orgId);
}else{
	emplInputMap.put("orgPartyId", "Company");
}
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
if(parameters.partyIdTo){
	employementIds=UtilMisc.toList(parameters.partyIdTo);
}else{
	employementIds=employementIds;
}
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
	benefitTypeIds=UtilMisc.toList(parameters.benefitTypeId);
}else{
	benefitTypeIds=benefitTypeIds;
}
context.benefitTypeIds=benefitTypeIds;
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
	dedTypeIds=UtilMisc.toList(parameters.dedTypeId);
}else{
	dedTypeIds=dedTypeIds;
}
context.dedTypeIds=dedTypeIds;

context.dedDescMap=dedDescMap;

conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN , employementIds));
conditionList.add(EntityCondition.makeCondition("benefitTypeId", EntityOperator.IN , benefitTypeIds));
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<GenericValue> partyBenefitList = delegator.findList("PartyBenefit", condition, null, ["partyIdTo"], null, false);

//Debug.logError("partyBenefitList="+partyBenefitList,"");
conditionDedList = [];
conditionDedList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN , employementIds));
conditionDedList.add(EntityCondition.makeCondition("deductionTypeId", EntityOperator.IN , dedTypeIds));
conditionDedList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
conditionDedList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
EntityCondition dedCondition=EntityCondition.makeCondition(conditionDedList,EntityOperator.AND);
List<GenericValue> partyDeductionList = delegator.findList("PartyDeduction", dedCondition, null, ["partyIdTo"], null, false);

Map headerDetailsMap=FastMap.newInstance();
List benfitItemIdsList=FastList.newInstance();
Map benefitTypeFinalMap=FastMap.newInstance();
Map benefitWiseMap=FastMap.newInstance();
Map hederFinalBenfMap=FastMap.newInstance();
Map totalBenefitsMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(partyBenefitList)){
	partyBenefitList.each{ partyBenefit->
		employeeId= partyBenefit.partyIdTo;
		amount= partyBenefit.cost;
		headerItemTypeId=partyBenefit.benefitTypeId;
		if(UtilValidate.isNotEmpty(amount)){
			//this is for Benefits/Deductions Report
			if(parameters.benefitTypeId==headerItemTypeId){
				if(UtilValidate.isEmpty(benefitWiseMap.get(employeeId))){
					Map tempBenf=FastMap.newInstance();
					tempBenf.put(headerItemTypeId, amount);		
					if(UtilValidate.isNotEmpty(tempBenf)){
						benefitWiseMap.put(employeeId,tempBenf);
					}	
				}else{				
						Map tempMap=FastMap.newInstance();
						tempMap.putAll(benefitWiseMap.get(employeeId));
						tempMap.put(headerItemTypeId,amount);
						benefitWiseMap.put(employeeId,tempMap);
				}
			}else{			
				if(UtilValidate.isEmpty(hederFinalBenfMap.get(employeeId))){
					Map headBenf=FastMap.newInstance();
					headBenf.put(headerItemTypeId, amount);
					if(UtilValidate.isNotEmpty(headBenf)){
						hederFinalBenfMap.put(employeeId,headBenf);
					}
				}else{
						Map headBenfMap=FastMap.newInstance();
						headBenfMap.putAll(hederFinalBenfMap.get(employeeId));
						headBenfMap.put(headerItemTypeId,amount);
						hederFinalBenfMap.put(employeeId,headBenfMap);
				}
			}
			benfitItemIdsList.addAll(headerItemTypeId);
			if(UtilValidate.isEmpty(totalBenefitsMap[headerItemTypeId])){
				totalBenefitsMap[headerItemTypeId]=amount;
			}else{
				totalBenefitsMap[headerItemTypeId]+=amount;
			}			
			
		}
		if(UtilValidate.isNotEmpty(amount)){
			amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
		}else{
			amount=" ";
		}
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
Set benefitIds = new HashSet(benfitItemIdsList);
List benfitIdsList =  benefitIds.toList();
benfitItemIdsList=benfitIdsList;

if(UtilValidate.isEmpty(benefitTypeFinalMap) && ("benefits".equals(parameters.type)) && (UtilValidate.isNotEmpty(parameters.benefitTypeId))){
	employementIds.each{ emplId->
		Map temBenfMap=FastMap.newInstance();
		temBenfMap.put(parameters.benefitTypeId, "");
		benefitTypeFinalMap.put(emplId,temBenfMap);
	}
}



//Debug.logError("benefitTypeFinalMap="+benefitTypeFinalMap,"");

JSONArray headBenefitItemsJSON = new JSONArray();
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
				benefitAmt=((itemEntry.getValue()));
				newObj.put(itemEntry.getKey(),((itemEntry.getValue())));
			}
		}
		headBenefitItemsJSON.add(newObj);
	}
}
Map deductionTypeValueMap=FastMap.newInstance();
List dedItemIdsList=FastList.newInstance();
Map deductionWiseMap=FastMap.newInstance();
Map hederFinalDedMap=FastMap.newInstance();
Map totalDeductionsMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(partyDeductionList)){
	partyDeductionList.each{ partyDed->
		employeeId= partyDed.partyIdTo;
		amount= partyDed.cost;
		headerItemTypeId=partyDed.deductionTypeId;
		if(UtilValidate.isNotEmpty(amount)){
			//this is for Benefits/Deductions Report
			if(parameters.dedTypeId==headerItemTypeId){
				if(UtilValidate.isEmpty(deductionWiseMap.get(employeeId))){
					Map tempDed=FastMap.newInstance();
					tempDed.put(headerItemTypeId, amount);
					if(UtilValidate.isNotEmpty(tempDed)){
						deductionWiseMap.put(employeeId,tempDed);
					}
				}else{
						Map tempMap=FastMap.newInstance();
						tempMap.putAll(deductionWiseMap.get(employeeId));
						tempMap.put(headerItemTypeId,amount);
						deductionWiseMap.put(employeeId,tempMap);
				}
			}else{			
				if(UtilValidate.isEmpty(hederFinalDedMap.get(employeeId))){
					Map headDed=FastMap.newInstance();
					headDed.put(headerItemTypeId, amount);
					if(UtilValidate.isNotEmpty(headDed)){
						hederFinalDedMap.put(employeeId,headDed);
					}
				}else{
						Map headMap=FastMap.newInstance();
						headMap.putAll(hederFinalDedMap.get(employeeId));
						headMap.put(headerItemTypeId,amount);
						hederFinalDedMap.put(employeeId,headMap);
				}
			}
			dedItemIdsList.add(headerItemTypeId);
			if(UtilValidate.isEmpty(totalDeductionsMap[headerItemTypeId])){
				totalDeductionsMap[headerItemTypeId]=amount;
			}else{
				totalDeductionsMap[headerItemTypeId]+=amount;
			}		
			
		}		
		if(UtilValidate.isNotEmpty(amount)){
			amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
		}else{
			amount=" ";
		}
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
if(UtilValidate.isEmpty(deductionTypeValueMap) && ("deductions".equals(parameters.type)) && (UtilValidate.isNotEmpty(parameters.dedTypeId))){
	employementIds.each{ emplId->
		Map temMap=FastMap.newInstance();
		temMap.put(parameters.dedTypeId, "");
		deductionTypeValueMap.put(emplId,temMap);
	}
}
Set deductionIds = new HashSet(dedItemIdsList);
List dedIdsList =  deductionIds.toList();
dedItemIdsList=dedIdsList;
//this is for report purpose
if(UtilValidate.isNotEmpty(parameters.benefitTypeId)){
	benfitItemIdsList=UtilMisc.toList(parameters.benefitTypeId);
}else{
	benfitItemIdsList=benfitItemIdsList;
}
if(UtilValidate.isNotEmpty(parameters.dedTypeId)){
	dedItemIdsList=UtilMisc.toList(parameters.dedTypeId);
}else{
	dedItemIdsList=dedItemIdsList;
}

List conditionList=[];
conditionList.add(EntityCondition.makeCondition("propertyName", EntityOperator.EQUALS, "showClosingBalance"));
conditionList.add(EntityCondition.makeCondition("propertyTypeEnumId", EntityOperator.EQUALS , "CLOSING_BALANCE"));
TenantCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
TenantConfigList = delegator.findList("TenantConfiguration", TenantCondition, null, null, null, false);
showClosingBalanceMap=[:];
if(UtilValidate.isNotEmpty(TenantConfigList)){
	TenantConfigList.each{ TenantConfig ->
		if(UtilValidate.isNotEmpty(TenantConfig)){
			description = TenantConfig.description;
			propertyValue = TenantConfig.propertyValue;
			if(UtilValidate.isEmpty(propertyValue)){
				propertyValue = "N";
			}
			showClosingBalanceMap.put("showCB",propertyValue);
		}
	}
}
else{
	showClosingBalanceMap.put("showCB","N");
}
context.put("showClosingBalanceMap",showClosingBalanceMap);

List quarterDedList = UtilMisc.toList("PAYROL_DD_ELECT","PAYROL_DD_WATR");
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
		empDetails = delegator.findOne("EmployeeDetail", [partyId : emplyId],true);
		if(UtilValidate.isNotEmpty(empDetails) && UtilValidate.isEmpty(empDetails.quarterType) && UtilValidate.isNotEmpty(parameters.dedTypeId) && (quarterDedList.contains(parameters.dedTypeId))){
			continue;
		}
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
		PartyClosingBalList = [];
		employeeInpuMap = [:];
		employeeInpuMap.put("loanTypeId",parameters.dedTypeId);
		employeeInpuMap.put("customTimePeriodId",parameters.customTimePeriodId);
		employeeInpuMap.put("partyId",parameters.partyIdTo);
		emplInputMap.put("userLogin", userLogin);
		PartyClosingBalList = (HumanresHelperServices.getLoanClosingBalanceByLoanType(dctx,employeeInpuMap));
		if(UtilValidate.isNotEmpty(PartyClosingBalList)){
			partyClosingDetails = PartyClosingBalList.get("loanClosingBalMap");
		}
		closingBalance = partyClosingDetails.get(parameters.partyIdTo);
		newObj.put("closingBalance",closingBalance);
		if(UtilValidate.isNotEmpty(entry.getValue())){
			Iterator headerItemIter = (entry.getValue()).entrySet().iterator();
			while(headerItemIter.hasNext()){
				Map.Entry itemEntry = headerItemIter.next();
				newObj.put(itemEntry.getKey(),((itemEntry.getValue())));
			}
		}
		headItemsJSON.add(newObj);
	}
}

if("benefits".equals(parameters.type)){
	context.headItemsJson=headBenefitItemsJSON;
	if(UtilValidate.isNotEmpty(parameters.benefitTypeId)){
		context.headerDetailsMap=benefitWiseMap;
	}else{
		context.headerDetailsMap=hederFinalBenfMap;	
	}
	context.headerItemIdsList=benfitItemIdsList;
	context.totalBenefitsMap=totalBenefitsMap;
}else{
	context.headItemsJson=headItemsJSON;
	if(UtilValidate.isNotEmpty(parameters.dedTypeId)){
		context.headerDetailsMap=deductionWiseMap;
	}else{
		context.headerDetailsMap=hederFinalDedMap;
	}
	context.headerItemIdsList=dedItemIdsList;
	context.totalDeductionsMap=totalDeductionsMap;
}
//Debug.logError("context.headItemsJson="+context.headItemsJson,"");

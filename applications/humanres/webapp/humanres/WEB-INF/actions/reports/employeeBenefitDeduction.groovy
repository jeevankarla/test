/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityConditionBuilder;

employeeId=parameters.employeeId;
customTimePeriodId = parameters.customTimePeriodId;
payHeadTypeId = parameters.payHeadTypeId;
amount = "";
benefitTypeDescription= null;
deductionTypeDescription = null;

if (UtilValidate.isNotEmpty(employeeId)) {
	benefitType = [:];
	deductionType = [:];
	if(UtilValidate.isNotEmpty(payHeadTypeId)){
		benefitType = delegator.findOne("BenefitType", [benefitTypeId : payHeadTypeId], false);
		deductionType = delegator.findOne("DeductionType", [deductionTypeId : payHeadTypeId], false);
	}
	if(UtilValidate.isNotEmpty(benefitType)){
		benefitTypeDescription = benefitType.description;
	}else{
		deductionTypeDescription = deductionType.description;
	}
	if(UtilValidate.isNotEmpty(payHeadTypeId)){
		payHeadAmount = dispatcher.runSync("getPayHeadAmount", [employeeId: employeeId, customTimePeriodId: customTimePeriodId,payHeadTypeId: payHeadTypeId,userLogin: userLogin]);
		Debug.log("payHeadTypeId ######################"+payHeadTypeId+"payHeadAmount ######"+payHeadAmount);
		amount = payHeadAmount.amount;
	}
	benefitDeductionList = [];
	tempMap = [:];
	tempMap["employeeId"]=employeeId;
	tempMap["customTimePeriodId"]=customTimePeriodId;
	if(UtilValidate.isNotEmpty(benefitTypeDescription)){
		tempMap["payHeadTypeId"]=benefitTypeDescription;
	}else{
		tempMap["payHeadTypeId"]=deductionTypeDescription;
	}
	if(UtilValidate.isNotEmpty(amount)){
		tempMap["amount"]=amount;
		tempMap["priceInfos"]=payHeadAmount.priceInfos;
		Debug.log("tempMap ######################"+tempMap);
		benefitDeductionList.add(tempMap);
		context.put("benefitDeductionList",benefitDeductionList);
	}
}

// for Party Benefit and Deduction Ui
partyId = parameters.partyId;
if (UtilValidate.isNotEmpty(customTimePeriodId)) {
	employDetailsList = [];
	finalBenList=[];
	finalDedList=[];
	fromDateStart = null;
	thruDateEnd = null;
	customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
	if (UtilValidate.isNotEmpty(customTimePeriod)) {
		fromDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		thruDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		fromDateStart = UtilDateTime.getDayStart(fromDateTime);
		thruDateEnd = UtilDateTime.getDayEnd(thruDateTime);
	}
	conditionList = [];
	if (UtilValidate.isNotEmpty(partyId)) {
		conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyId));
	}
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDateStart));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDateEnd)));
	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List<GenericValue> partyBenefitList = delegator.findList("PartyBenefit", condition, null, null, null, false);
	if(UtilValidate.isNotEmpty(partyBenefitList)){
		partyBenefit = partyBenefitList[0];
		if(UtilValidate.isNotEmpty(partyBenefit)){
			amount = partyBenefit.cost;
			benefitTypeId = partyBenefit.benefitTypeId;
			if(UtilValidate.isNotEmpty(benefitTypeId)){
				benefitType = delegator.findOne("BenefitType", [benefitTypeId : benefitTypeId], false);
			}
			if(UtilValidate.isNotEmpty(benefitType)){
				benefitTypeDescription = benefitType.description;
			}
			tempPartyBenMap =[:];
			tempPartyBenMap["partyId"]=partyBenefit.partyIdTo;
			tempPartyBenMap["customTimePeriodId"]=customTimePeriodId;
			tempPartyBenMap["description"]=benefitTypeDescription;
			tempPartyBenMap["amount"]=amount;
			finalMap =[:];
			finalMap.putAll(tempPartyBenMap);
			finalBenList.add(finalMap);
		}
	}
	List<GenericValue> partyDeductionList = delegator.findList("PartyDeduction", condition, null, null, null, false);
	if(UtilValidate.isNotEmpty(partyDeductionList)){
		partyDeduction = partyDeductionList[0];
		if(UtilValidate.isNotEmpty(partyDeduction)){
			amount = partyDeduction.cost;
			deductionTypeId = partyDeduction.deductionTypeId;
			if(UtilValidate.isNotEmpty(deductionTypeId)){
				deductionType = delegator.findOne("DeductionType", [deductionTypeId : deductionTypeId], false);
			}
			if(UtilValidate.isNotEmpty(deductionType)){
				deductionTypeDescription = deductionType.description;
			}
			tempPartyDedMap =[:];
			tempPartyDedMap["partyId"]=partyDeduction.partyIdTo;
			tempPartyDedMap["customTimePeriodId"]=customTimePeriodId;
			tempPartyDedMap["description"]=deductionTypeDescription;
			tempPartyDedMap["amount"]=amount;
			finalMap =[:];
			finalMap.putAll(tempPartyDedMap);
			finalDedList.add(finalMap);
		}
	}
	employDetailsList.addAll(finalBenList);
	employDetailsList.addAll(finalDedList);
	context.put("employDetailsList",employDetailsList);
}










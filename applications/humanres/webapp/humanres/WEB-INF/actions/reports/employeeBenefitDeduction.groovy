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

benefitType = [:];
deductionType = [:];
benefitTypeDescription= null;
deductionTypeDescription = null;
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
	benefitDeductionList.add(tempMap);
	context.put("benefitDeductionList",benefitDeductionList);
}










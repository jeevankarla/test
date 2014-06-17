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
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;
import in.vasista.vbiz.humanres.PayrollService;


partyId=parameters.employeeId;
customTimePeriodId = parameters.customTimePeriodId;
List salaryDetailsList = [];
List salaryDetailsListCsv = [];
List partyBenefitList = [];
List partyDeductionList = [];
Map party=[:];
List<GenericValue> payHistory=[];
exprBldr = new org.ofbiz.entity.condition.EntityConditionBuilder();
if(partyId != null && UtilValidate.isNotEmpty(partyId)){
	dctx = dispatcher.getDispatchContext();
	party.partyId=partyId;
	
	benefitTypeList = [];
	deductionTypeList=[];
	benefitTypeIds=[];
	deductionTypeIds=[];
	
	benefitTypeList=delegator.findList("BenefitType", null, null,null, null, false);
	benefitTypeIds = EntityUtil.getFieldListFromEntityList(benefitTypeList, "benefitTypeId", true);
	deductionTypeList=delegator.findList("DeductionType", null, null,null, null, false);
	deductionTypeIds = EntityUtil.getFieldListFromEntityList(deductionTypeList, "deductionTypeId", true);
	
	payGradeDescription = null;
	benefitTypeDescription = null;
	deductionTypeDescription = null;
	
	basicAmount = null;
	benefitAmount = null;
	deductionAmount = null;
	
	if(UtilValidate.isNotEmpty(partyId)){
		resultMap = PayrollService.preparePayrolItems(dctx, [partyId:partyId, timePeriodId:customTimePeriodId, userLogin : userLogin]);
		if(UtilValidate.isNotEmpty(resultMap)){
			itemsList = resultMap.get("itemsList");
			if(UtilValidate.isNotEmpty(itemsList)){
				itemsList.each{ item->
					payrollItemTypeId = item.payrollItemTypeId;
					if(benefitTypeIds.contains(payrollItemTypeId)){
						benefitType = delegator.findOne("BenefitType", [benefitTypeId : payrollItemTypeId], false);
						benefitTypeDescription = benefitType.description;
						benefitAmount = item.amount;
						partyBenefitList.add([ payGradeName : benefitTypeDescription,
						amount : benefitAmount]);
						salaryDetailsListCsv.add([ payGradeName : benefitTypeDescription,
						amount : benefitAmount]);
					}
					if(deductionTypeIds.contains(payrollItemTypeId)){
						deductionType = delegator.findOne("DeductionType", [deductionTypeId : payrollItemTypeId], false);
						deductionTypeDescription = deductionType.description;
						deductionAmount = item.amount;
						partyDeductionList.add([ payGradeName : deductionTypeDescription,
						amount : deductionAmount]);
						salaryDetailsListCsv.add([ payGradeName : deductionTypeDescription,
						amount : deductionAmount]);
					}
				}
				salaryDetailsList.add(partyBenefitList : partyBenefitList , partyDeductionList : partyDeductionList);
			}
		}
	}
}
context.employeeId = partyId;
context.salaryDetailsList = salaryDetailsList;
context.salaryDetailsListCsv = salaryDetailsListCsv;
context.partyBenefitList = partyBenefitList;
context.partyDeductionList = partyDeductionList;
	
	












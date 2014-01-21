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


partyId=parameters.employeeId;
List salaryDetailsList = [];
List salaryDetailsListCsv = [];
List partyBenefitList = [];
List partyDeductionList = [];
Map party=[:];
List<GenericValue> payHistory=[];
exprBldr = new org.ofbiz.entity.condition.EntityConditionBuilder();
if(partyId != null && UtilValidate.isNotEmpty(partyId)){
				List conditionList=[];
				dctx = dispatcher.getDispatchContext();
				party.partyId=partyId;
				BasicSalary = InvoicePayrolWorker.fetchBasicSalary(dctx,party);
				payGrade=delegator.findOne("PayGrade",[payGradeId : "BASIC_PAY"],false);
				partyBenefitList.add([ payGradeName : payGrade.payGradeName  , amount : BasicSalary.amount]);
				salaryDetailsListCsv.add([ payGradeName :  payGrade.payGradeName , amount : BasicSalary.amount]);
				totalSalary=BasicSalary.amount;
				conditionList = UtilMisc.toList(
                EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
		List<GenericValue> partyBenefits = delegator.findList("PartyBenefit", condition, null, null, null, false);
		
		if(partyBenefits){
			
			partyBenefits.each{ partyBenefit ->
			benefitType=partyBenefit.getRelatedOne("BenefitType");
			partyBenefitList.add([ payGradeName : benefitType.description , amount : partyBenefit.cost]);
			salaryDetailsListCsv.add([ payGradeName :  benefitType.description , 
			amount : partyBenefit.cost]);
			totalSalary=totalSalary+partyBenefit.cost;
			}
		}
		conditionList = UtilMisc.toList(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())));
        condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  				
		List<GenericValue> partyDeductions = delegator.findList("PayrollPreference",condition, null, null, null, false);
		
		if(partyDeductions){			
			partyDeductions.each{ partyDeduction ->
			deductionType=partyDeduction.getRelatedOne("DeductionType");
			partyDeductionList.add([ payGradeName : deductionType.description, 
			amount : partyDeduction.flatAmount]);
			salaryDetailsListCsv.add([ payGradeName : deductionType.description , 
			amount : partyDeduction.flatAmount]);
			totalSalary=totalSalary+partyDeduction.flatAmount;			
			}
		}		
	   salaryDetailsList.add( partyBenefitList : partyBenefitList , partyDeductionList : partyDeductionList);    
    }
    
    context.employeeId = partyId;
	context.salaryDetailsList = salaryDetailsList;
	context.salaryDetailsListCsv = salaryDetailsListCsv;
	context.partyBenefitList = partyBenefitList;
	context.partyDeductionList = partyDeductionList;	


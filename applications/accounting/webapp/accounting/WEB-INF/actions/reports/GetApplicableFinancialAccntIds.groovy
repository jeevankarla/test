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
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import org.ofbiz.entity.util.EntityUtil;
	import org.ofbiz.base.util.UtilDateTime;
	import org.ofbiz.base.util.UtilValidate;
	import org.ofbiz.base.util.UtilNumber;
	import org.ofbiz.accounting.util.UtilAccounting;
	import java.math.BigDecimal;
	import com.ibm.icu.util.Calendar;
	import org.ofbiz.base.util.*;
	
	
	if(UtilValidate.isNotEmpty(context.flag) && context.flag == "Y"){
		finalFinAccntList = [];
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS,"BANK_ACCOUNT"));
		conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
		financialAccntList = delegator.findList("FinAccount",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("finAccountId"), null, false);
		finalFinAccntList.addAll(financialAccntList);
		
		condList = [];
		condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS,"INTERUNIT_ACCOUNT"));
		condList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
		interFinancialAccntList = delegator.findList("FinAccount",EntityCondition.makeCondition(condList, EntityOperator.AND), null, UtilMisc.toList("finAccountId"), null, false);
		finalFinAccntList.addAll(interFinancialAccntList);
		
		context.finalFinAccntList = finalFinAccntList;
	}else{
		if (organizationPartyId) {
			glAccnt = delegator.findList("GlAccountOrganizationAndClass", EntityCondition.makeCondition(["organizationPartyId" : organizationPartyId]), null, null, null, true);
			glAccntIds = EntityUtil.getFieldListFromEntityList(glAccnt, "glAccountId", true);
				conditionList = [];
				 if(UtilValidate.isNotEmpty(parameters.screenFlag)){
				conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
				}
				 else if(UtilValidate.isNotEmpty("Y".equals(parameters.screenfinIdFlag))){
					 conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "CASH"));
					 }
				//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
				conditionList.add( EntityCondition.makeCondition("postToGlAccountId", EntityOperator.IN, glAccntIds));
				List<String> orderBy = UtilMisc.toList("finAccountName");				
			financialAccnt = delegator.findList("FinAccount",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, orderBy, null, false);
			finalList=[];
			financialAccnt.each{ eachvalue ->
				if(eachvalue.finAccountName){
					finalList.addAll(eachvalue);
				}
			}
		//Debug.log("financialAccnt==========new================"+financialAccnt);
			//glAccntIds = EntityUtil.getFieldListFromEntityList(glAccnt, "glAccountId", true);
			context.financialAccnt = financialAccnt;
			context.finalList = finalList;
		}
	}
	
	
	
	
	
	
	
	
	
	
	

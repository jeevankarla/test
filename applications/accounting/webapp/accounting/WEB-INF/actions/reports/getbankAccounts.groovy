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

organizationPartyId = "Company";


Debug.log("parameters.ownerPartyId=====bank acs========"+parameters.ownerPartyId);

if(UtilValidate.isNotEmpty(context.flag) && context.flag == "Y"){
	finalFinAccntList = [];
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS,"BANK_ACCOUNT"));
	//conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
	if(UtilValidate.isNotEmpty(parameters.ownerPartyId) &&  parameters.ownerPartyId!=null){
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.ownerPartyId));
	}
	financialAccntList = delegator.findList("FinAccount",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("finAccountId"), null, false);
	finalFinAccntList.addAll(financialAccntList);
	
	condList = [];
	condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS,"INTERUNIT_ACCOUNT"));
	//condList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
	if(UtilValidate.isNotEmpty(parameters.ownerPartyId) &&  parameters.ownerPartyId!=null){
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.ownerPartyId));
	}
	interFinancialAccntList = delegator.findList("FinAccount",EntityCondition.makeCondition(condList, EntityOperator.AND), null, UtilMisc.toList("finAccountId"), null, false);
	finalFinAccntList.addAll(interFinancialAccntList);
	
	context.finalFinAccntList = finalFinAccntList;
}else{
	if (organizationPartyId) {
		glAccnt = delegator.findList("GlAccountOrganizationAndClass", EntityCondition.makeCondition(["organizationPartyId" : organizationPartyId]), null, null, null, true);
		glAccntIds = EntityUtil.getFieldListFromEntityList(glAccnt, "glAccountId", true);
			conditionList = [];
			 /*if(UtilValidate.isNotEmpty(parameters.screenFlag)){
			
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"),EntityOperator.OR,
				EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "CASH")));
			}*/
			//conditionList.add( EntityCondition.makeCondition("postToGlAccountId", EntityOperator.IN, glAccntIds));
			
			conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.IN, ["BANK_ACCOUNT","CASH"]));
			if(UtilValidate.isNotEmpty(parameters.ownerPartyId) &&  parameters.ownerPartyId!=null){
				conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.ownerPartyId));
			}
			List<String> orderBy = UtilMisc.toList("finAccountName");
		financialAccnt = delegator.findList("FinAccount",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, orderBy, null, false);
		finalList=[];
		financialAccnt.each{ eachvalue ->
			if(eachvalue.finAccountName){
				finalList.addAll(eachvalue);
			}
		}
	
		context.financialAccnt = financialAccnt;
		context.finalList = finalList;
	}
}


parameters.ownerPartyId = "";










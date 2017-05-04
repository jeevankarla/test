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
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilMisc;
import java.text.ParseException;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


List outstandingList = [];
AsOnDate=parameters.AsOnDate;
//Debug.log("asondate ===="+AsOnDate);
Timestamp AsOndate = null;
if(UtilValidate.isNotEmpty(AsOnDate)){
if(AsOnDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM dd");
	try {
		AsOndate = new java.sql.Timestamp(sdf.parse(AsOnDate).getTime());
		
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + AsOnDate, "");
	}
}

}
context.AsOnDate=AsOndate;
  // Debug.log("ownerPartyId===="+parameters.ownerPartyId);
   List exprListForParameters = [];
   conditionlist=[];
   finAccountTypeList = delegator.findList("FinAccountType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "EMPLOYEE_ADV") , null, null, null, false);
   finAccountTypeIdList = EntityUtil.getFieldListFromEntityList(finAccountTypeList, "finAccountTypeId", false);
   // Debug.log("finAccountTypeIdList======="+finAccountTypeIdList);
    if (AsOndate) {
		   exprListForParameters.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, AsOndate));
	   }

	if(parameters.ownerPartyId && parameters.ownerPartyId!=null){
		exprListForParameters.add(EntityCondition.makeCondition("costCenterId", EntityOperator.EQUALS, parameters.ownerPartyId));
	}
	if(finAccountTypeIdList){
	exprListForParameters.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.IN, finAccountTypeIdList));
	}
	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
	List<String> orderBy = UtilMisc.toList("fromDate");				
	finAccountList = delegator.findList("FinAccount", paramCond, null, orderBy, null, false);
	//Debug.log("condition===="+paramCond);
	if(finAccountList.size()>0){
	for(GenericValue finAccountEntry:finAccountList){
		noOfDays=UtilDateTime.getIntervalInDays(finAccountEntry.fromDate,AsOndate);
		if(noOfDays>45 && finAccountEntry.actualBalance>0){   // no of days more than 45 and balance > 0
			//Debug.log("finAccount list======="+finAccountEntry);
			tempMap = [:];
			tempMap["finAccountId"]=finAccountEntry.finAccountId;
			finAccountTypeDes = delegator.findOne("FinAccountType", ["finAccountTypeId":finAccountEntry.finAccountTypeId], false);
			//Debug.log("finAccountTypeDes======="+finAccountTypeDes);
			if(finAccountTypeDes.size()>0){
				if(finAccountTypeDes.description){
					tempMap["description"]=finAccountTypeDes.description;
				}
			}
			else{
				tempMap["description"]=finAccountEntry.finAccountTypeId;
			}

			tempMap["finAccountName"]=finAccountEntry.finAccountTypeId;
			tempMap["ownerPartyId"]=finAccountEntry.ownerPartyId;
			tempMap["fromDate"]=finAccountEntry.fromDate;
			tempMap["actualBalance"]=finAccountEntry.actualBalance;
			conditionList = [];
			conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountEntry.finAccountId));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, finAccountEntry.ownerPartyId));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List finAccountTransList = delegator.findList("FinAccountTrans", condition, null, null, null, false);
			List depositFinAccTransList = EntityUtil.filterByCondition(finAccountTransList, EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, "DEPOSIT"));
			List withDrawFinAccTransList = EntityUtil.filterByCondition(finAccountTransList, EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, "WITHDRAWAL"));
			BigDecimal depositAmt = BigDecimal.ZERO;
			BigDecimal adjustAmt = BigDecimal.ZERO;
			for(GenericValue depositFinAccTransEntry:depositFinAccTransList){
				depositAmt += depositFinAccTransEntry.amount;
			}
			for(GenericValue withDrawFinAccTransEntry:withDrawFinAccTransList){
				adjustAmt += withDrawFinAccTransEntry.amount;
			}
			tempMap["depositAmt"]=depositAmt;
			tempMap["adjustAmt"]=adjustAmt;
			outstandingList.add(tempMap);
		}
	}
	}
	//Debug.log("outstandinglist======="+outstandingList);
	  context.outstandingList=outstandingList;
	  context.currencyUomId="INR";

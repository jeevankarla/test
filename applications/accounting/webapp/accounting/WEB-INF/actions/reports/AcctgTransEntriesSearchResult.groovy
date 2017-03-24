
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

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.*
import org.ofbiz.minilang.SimpleMapProcessor
import org.ofbiz.content.ContentManagementWorker
import org.ofbiz.content.content.ContentWorker
import org.ofbiz.content.data.DataResourceWorker
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import java.text.ParseException;


fromDateStr = parameters.fromDate;
thruDateStr = parameters.fromDate;
glAccountId=parameters.glAccountId;
purposeTypeId = parameters.purposeTypeId;
costCenterId = parameters.costCenterId;
branchList = [];
condList = [];
if(UtilValidate.isNotEmpty(costCenterId)){
	condList.add(EntityCondition.makeCondition("partyIdFrom" , EntityOperator.EQUALS,costCenterId));
	condList.add(EntityCondition.makeCondition("roleTypeIdFrom" , EntityOperator.EQUALS,"PARENT_ORGANIZATION"));
	condList.add(EntityCondition.makeCondition("roleTypeIdTo" , EntityOperator.EQUALS,"ORGANIZATION_UNIT"));
	condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" , EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
	List roWiseBranchaList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
	if(UtilValidate.isNotEmpty(roWiseBranchaList)){
		branchList= EntityUtil.getFieldListFromEntityList(roWiseBranchaList,"partyIdTo", true);
	}
	branchList.add(costCenterId);
}
else{
	branchList.add("Company");
}
def sdf = new SimpleDateFormat("yyyy-MM-dd");
if (UtilValidate.isEmpty(fromDateStr)) {
	fromDate = UtilDateTime.nowTimestamp();
}
else{
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + fromDateStr, "");
	}
}
if (UtilValidate.isEmpty(thruDateStr)) {
	thruDate = fromDate;
}
else{
	try {
		thruDate = new java.sql.Timestamp(sdf.parse(thruDateStr).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + thruDateStr, "");
	}
}

result=dispatcher.runSync("getGlAccountOpeningBalance", [glAccountId:glAccountId,fromDate:UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate)),thruDate:UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate)), roBranchList: branchList, segmentId: purposeTypeId, userLogin:userLogin]);

//result=dispatcher.runSync("getGlAccountOpeningBalance", [glAccountId:glAccountId,fromDate:fromDate,thruDate:thruDate,userLogin:userLogin]);
Debug.log("parameters.customTimePeriodId==="+parameters.customTimePeriodId);
context.put("openingBal",result.get("openingBal"));





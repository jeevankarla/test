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

import java.util.List;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

String fromDate = null;
String thruDate = null;
String hideSearch = parameters.hideSearch;
List milkDetailslist = FastList.newInstance();
if(UtilValidate.isNotEmpty(hideSearch) && (hideSearch.equalsIgnoreCase("N"))){
	fromDate = parameters.fromDate;
	thruDate = parameters.thruDate;
	java.sql.Timestamp fromStartTime =null;
	java.sql.Timestamp thruEndTime =null;
	if(fromDate){
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMMM dd, yyyy");
		java.util.Date fromParsedDate = dateFormat.parse(fromDate);	
		java.sql.Timestamp fromTimestamp = new java.sql.Timestamp(fromParsedDate.getTime());
		 fromStartTime =  UtilDateTime.getDayStart(fromTimestamp);	
		if(thruDate){
			java.util.Date thruParsedDate = dateFormat.parse(thruDate);
			java.sql.Timestamp thruTimestamp = new java.sql.Timestamp(thruParsedDate.getTime());
			 thruEndTime =  UtilDateTime.getDayEnd(thruTimestamp);
			thruDateTimeStamp = (String)thruEndTime ;
		}
		
	}
	if(UtilValidate.isEmpty(fromStartTime)){
		fromStartTime = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -15));
	}
	if(UtilValidate.isEmpty(thruEndTime)){
		thruEndTime = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
	milkDetailslist=[];
	List conditionList=FastList.newInstance();
	if(UtilValidate.isNotEmpty(parameters.partyId)){
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , parameters.partyId));
	}
	if(UtilValidate.isNotEmpty(parameters.milkTransferId)){
		conditionList.add(EntityCondition.makeCondition("milkTransferId", EntityOperator.EQUALS , parameters.milkTransferId));
	}
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , "MD"));
	if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="APPROVE_RECEIPTS"){
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
	}
	if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="FINALIZATION"){
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_APPROVED"));
	}
	//conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS , "Y"),EntityOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS ,"N")));
	if(UtilValidate.isNotEmpty(parameters.productId)){
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , parameters.productId));
	}
		/*conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromStartTime)]));
		conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruEndTime));*/
	conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromStartTime));
	conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruEndTime));
	
	if(UtilValidate.isNotEmpty(parameters.createdByUserLogin)){
		conditionList.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS ,parameters.createdByUserLogin));
	}
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	milkDetailslist = delegator.findList("MilkTransferAndMilkTransferItem",condition,null,UtilMisc.toList("-createdStamp"),null,false);
}
context.milkDetailslist=milkDetailslist;


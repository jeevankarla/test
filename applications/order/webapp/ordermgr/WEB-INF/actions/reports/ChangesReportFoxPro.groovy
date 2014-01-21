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


import java.io.ObjectOutputStream.DebugTraceInfoStack;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/*		DTM200	11
		DTMHLF	12
DTMONE	COWHLF	13
		TM200	21
		TMHLF	22
		TMONE	23
		SMHLF	32
		SMONE	33
		WMHLF	42
		WMONE	43
		WMBLK	44
		VDMHLF	52
VSM200	SBSHLF	61
VSMHLF	TFMHLF	62
VSMONE	VEFHLF	63
VPMHLF	FMLHLF	72
VPMONE	VTMHLF	73 */


productList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
Debug.logInfo("productList================="+productList,"");
context.productList = productList;
supplyTypeMap =[:];
supplyTypeMap["SPECIAL_ORDER"] = "SO";
supplyTypeMap["CASH"] = "CS";
supplyTypeMap["CARD"] = "CD";
supplyTypeMap["CREDIT"] = "CR";

productNameMap =[:];
productNameMap["11"] = "DTM200";
productNameMap["12"] = "DTMHLF";
productNameMap["13"] = "DTMONE";
productNameMap["21"] = "TM200";
productNameMap["22"] = "TMHLF";
productNameMap["23"] = "TMONE";
productNameMap["32"] = "SMHLF";
productNameMap["33"] = "SMONE";
productNameMap["42"] = "WMHLF";
productNameMap["43"] = "WMONE";
productNameMap["44"] = "WMBLK";
productNameMap["52"] = "VDMHLF";
productNameMap["61"] = "VSM200";
productNameMap["62"] = "VSMHLF";
productNameMap["63"] = "VSMONE";
productNameMap["72"] = "VPMHLF";
productNameMap["73"] = "VPMONE";
productNameMap["93"] = "WMBLK";


checkListType= parameters.checkListType;
context.checkListType = checkListType;
List exprList = [];
List checkListReportList = [];
lastChangeSubProdMap = [:];

supplyDate = parameters.supplyDate;
if(parameters.supplyDate){
	supplyDate = parameters.supplyDate;
}
if(context.supplyDate){
	supplyDate = context.supplyDate;
}
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Timestamp supplyDateTime = UtilDateTime.nowTimestamp();
//Timestamp thruDateTime = UtilDateTime.nowTimestamp();
try {
	supplyDateTime = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());

} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+supplyDate, "");
   
}
/*dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);*/
dayBegin = UtilDateTime.getDayStart(supplyDateTime, -1);
dayEnd = UtilDateTime.getDayEnd(supplyDateTime,-1);
	exprList.add(EntityCondition.makeCondition([
		EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO , dayBegin),
		EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
	   ], EntityOperator.OR));
   exprList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
   condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);   
   checkListItemList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, ["lastModifiedDate"], null, false);
   checkListItemList = EntityUtil.filterByDate(checkListItemList,UtilDateTime.getNextDayStart(dayBegin));
if (checkListItemList.size() > 0) {	
		productList.each{ product ->
			lastChangeSubProdMap[productNameMap[product.productId]] = '';
		}
		lastChangeSubProdMap["supplyDate"] = UtilDateTime.getDayStart(supplyDateTime);
		lastChangeSubProdMapEmpty =[:];
		lastChangeSubProdMapEmpty.putAll(lastChangeSubProdMap);
		lastChangeSubProdMapEmpty["boothId"]= "0000";
		lastChangeSubProdMapEmpty["supplyType"]= "CS";
		checkListReportList.add(lastChangeSubProdMapEmpty);
		checkListReportList.add(lastChangeSubProdMap);
}

tempOrderId = "";
tempSupplyType ="";
tempSequenceNum ="";

	for (int i=0; i < checkListItemList.size(); i++) {
		checkListItemProd = checkListItemList.get(i);
		
		if (tempOrderId == "") {
			tempOrderId = checkListItemProd.subscriptionId;
			tempSupplyType = checkListItemProd.productSubscriptionTypeId;
			tempSequenceNum = checkListItemProd.sequenceNum;
			lastChangeSubProdMap["boothId"] = checkListItemProd.facilityId;			
			lastChangeSubProdMap["supplyType"] = supplyTypeMap[checkListItemProd.productSubscriptionTypeId];			
		}
		if ((tempOrderId != checkListItemProd.subscriptionId) || (tempSupplyType != checkListItemProd.productSubscriptionTypeId) || (tempSequenceNum != checkListItemProd.sequenceNum))  {
			tempOrderId = checkListItemProd.subscriptionId;
			tempSupplyType = checkListItemProd.productSubscriptionTypeId;
			tempSequenceNum = checkListItemProd.sequenceNum;
			lastChangeSubProdMap = [:];
			checkListReportList.add(lastChangeSubProdMap);
			productList.each{ product ->
				
				lastChangeSubProdMap[productNameMap[product.productId]] = '';
			}
			lastChangeSubProdMap["boothId"] = checkListItemProd.facilityId;			
			lastChangeSubProdMap["supplyType"] = supplyTypeMap[checkListItemProd.productSubscriptionTypeId];
			lastChangeSubProdMap["supplyDate"] = checkListItemProd.fromDate;
			
		}			
		lastChangeSubProdMap[productNameMap[checkListItemProd.productId]] = (checkListItemProd.quantity).intValue();		
	}

context.checkListReportList = checkListReportList;

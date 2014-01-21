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
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.util.EntityUtil;
	import java.util.*;
	import java.lang.*;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import java.sql.*;
	import java.util.Calendar;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import org.ofbiz.service.DispatchContext;
	import java.math.BigDecimal;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
	import  org.ofbiz.network.NetworkServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	
	totalMap = [:];
	effectiveDate = null;
	/*rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	context.rounding = rounding;*/
	remittanceDate = parameters.remittanceDate;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	if(UtilValidate.isEmpty(remittanceDate)){
		remittanceDate = UtilDateTime.nowTimestamp();
	}
	else{
		try {
			remittanceDate = UtilDateTime.toTimestamp(dateFormat.parse(remittanceDate));
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + remittanceDate, "");
		}
	}
	
	
	effectiveDateStr = parameters.paymentDate;
	dateType = parameters.dateType;
	if (UtilValidate.isEmpty(effectiveDateStr)) {
		effectiveDate = UtilDateTime.nowTimestamp();
	}
	else{
		try {
			effectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(effectiveDateStr));
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
		}
	}
	dayBegin = UtilDateTime.getDayStart(effectiveDate);
	dayEnd = UtilDateTime.getDayEnd(effectiveDate);
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	paymentList = [];
	grandTotalMap = [:]; 
	conditionList=[];
	categoryMap = [:];
	categoryType = null;
	
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentPurposeType", EntityOperator.EQUALS, "BYPROD_PAYMENT"), EntityOperator.OR, EntityCondition.makeCondition("paymentPurposeType", EntityOperator.EQUALS, null)));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_RECEIVED"));
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, "Company"));
	conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL, "PARLOUR"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	facilityPaymentList = delegator.findList("PaymentAndFacility", condition, null , ["facilityId"], null, false);
	if(facilityPaymentList){
		facilityPaymentList.each {eachItem ->
			facilityCategory = eachItem.getAt("categoryTypeEnum");
			paymentMethodTypeId = eachItem.getAt("paymentMethodTypeId");
			if(UtilValidate.isEmpty(facilityCategory)){
				categoryType = "OTHER";
			}else{
				categoryType = facilityCategory;
			}
			
			itemDetail = [:];
			itemList = [];
			facilityId = eachItem.getAt("facilityId");
			partyId = eachItem.getAt("partyIdFrom");
			partyList = delegator.findOne("PartyNameView", ["partyId":partyId], false);
			partyName = "";
			branchName = "";
			bankName = "";
			paymentDate = "";
			if(partyList){
				if(partyList.firstName){
					firstName = partyList.firstName;
					partyName = firstName;
				}
				if(partyList.groupName){
					groupName = partyList.groupName;
					partyName = partyName+groupName;
				}
			}
			
			paymentRefNum = eachItem.getAt("paymentRefNum");
			if(paymentMethodTypeId == "CHEQUE_PAYIN"){
				bankName = eachItem.getAt("issuingAuthority");
				branchName = eachItem.getAt("issuingAuthorityBranch");
				paymentDate = eachItem.getAt("effectiveDate");
				paymentDate = UtilDateTime.toDateString(paymentDate, "dd.MM.yyyy");
			}
			amount = eachItem.getAt("amount");
			if(UtilValidate.isEmpty(grandTotalMap)){
				grandTotalMap["grandTotal"] = amount;
			}
			else{
				totalAmount = grandTotalMap["grandTotal"];
				resTotal = totalAmount.add(amount);
				grandTotalMap["grandTotal"] = resTotal;
			}
			if(!totalMap[categoryType]){
				totalMap.putAt(categoryType, amount);
			}
			else{
				tempTotal = totalMap.getAt(categoryType);
				resultTotal = tempTotal.add(amount);
				totalMap.putAt(categoryType, resultTotal);	
			}
			itemDetail.putAt("facilityId", facilityId);
			itemDetail.putAt("partyId", partyName);
			itemDetail.putAt("bankName", bankName);
			itemDetail.putAt("branchName", branchName);
			itemDetail.putAt("paymentRefNum", paymentRefNum);
			itemDetail.putAt("effectiveDate", paymentDate);
			itemDetail.putAt("amount", amount);
			itemList.add(itemDetail);
			if(categoryMap.containsKey(categoryType)){
				catPayList = categoryMap.get(categoryType);
				catPayList.addAll(itemList);
				categoryMap.putAt(categoryType, catPayList);
			}
			else{
				categoryMap.putAt(categoryType, itemList);
			}
		}
	}
	
	context.categoryPaymentMap = categoryMap;
	context.totalMap = totalMap;
	context.grandTotalMap = grandTotalMap;
	context.effectiveDate = UtilDateTime.toDateString(remittanceDate, "dd.MM.yyyy");
	return "success";
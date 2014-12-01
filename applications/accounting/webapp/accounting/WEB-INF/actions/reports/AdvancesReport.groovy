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
	
	import java.text.SimpleDateFormat;
	import java.math.BigDecimal;
	import java.sql.Timestamp;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.accounting.payment.PaymentWorker;
	
	userLogin= context.userLogin;
	
	fromDate = UtilDateTime.getDayStart(parameters.fromDate);
	thruDate = UtilDateTime.getDayEnd(parameters.thruDate);
	paymentTypeId = parameters.paymentTypeId;
	Debug.log("fromDate ================"+fromDate);
	Debug.log("thruDate ================"+thruDate);
	Debug.log("paymentTypeId ================"+paymentTypeId);
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_SENT"));
	conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, paymentTypeId));
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	List paymentList = delegator.findList("Payment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toSet("paymentDate"), null, false);
	paymentIdsList = EntityUtil.getFieldListFromEntityList(paymentList, "paymentId", true);
	partyIdsList = EntityUtil.getFieldListFromEntityList(paymentList, "partyIdTo", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIdsList));
	List paymentApplicationList = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toList("paymentId","paymentApplicationId","invoiceId","invoiceItemSeqId","toPaymentId","amountApplied", "paymentDate"), UtilMisc.toSet("paymentDate"), null, false);
	toPaymentIdsList = EntityUtil.getFieldListFromEntityList(paymentApplicationList, "toPaymentId", true);
	invoiceIdsList = EntityUtil.getFieldListFromEntityList(paymentApplicationList, "invoiceId", true);
	
	
	partyPaymentsMap = [:];
	for(i=0; i<partyIdsList.size(); i++){
		partyId = partyIdsList.get(i);
		partyPaymentsList = EntityUtil.filterByAnd(paymentList, [partyIdTo : partyId]);
		
		paymentApplicationMap = [:]
		for(j=0; j<partyPaymentsList.size(); j++){
			partyPayment = partyPaymentsList.get(j);
			partyPaymentId = partyPayment.get("paymentId");
			partyPaymentApplicationsList = EntityUtil.filterByAnd(paymentApplicationList, [paymentId : partyPaymentId]);
			
			// Calculate Opening Balance
			oldPmntApplications = EntityUtil.filterByCondition(partyPaymentApplicationsList, EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, fromDate));
			amountApplied = BigDecimal.ZERO;
			for(k=0; k<oldPmntApplications.size(); k++){
				amountApplied = amountApplied.add((oldPmntApplications.get(k)).getBigDecimal("amountApplied"));
			}
			openingBalance = (partyPayment.getBigDecimal("amount")).subtract(amountApplied);
			
			// Calculate Closing Balance
			totalAmountApplied = BigDecimal.ZERO;
			for(k=0; k<partyPaymentApplicationsList.size(); k++){
				totalAmountApplied = amountApplied.add((partyPaymentApplicationsList.get(k)).getBigDecimal("amountApplied"));
			}
			closingBalance = (partyPayment.getBigDecimal("amount")).subtract(totalAmountApplied);
			
			obAndAppMap = [:];
			obAndAppMap.put("openingBalance", openingBalance);
			obAndAppMap.put("paymentApplications", partyPaymentApplicationsList);
			obAndAppMap.put("closingBalance", closingBalance);
			
			tempMap = [:];
			tempMap.putAll(obAndAppMap);
			
			paymentApplicationMap.put(partyPaymentId, tempMap);
		}
		tempPmntAppMap = [:];
		tempPmntAppMap.putAll(paymentApplicationMap);
		
		partyPaymentsMap.put(partyId, tempPmntAppMap);
	}
	
	allPmntIdsList = [];
	allPmntIdsList.addAll(paymentIdsList);
	allPmntIdsList.addAll(toPaymentIdsList);
	
	
	
	
	
	
	
	
	
	adsfadsfl;
	/*
	 * partyIdTo = parameters.partyIdTo;
	partyIdFrom = parameters.partyIdFrom;
	paymentTypeId = parameters.paymentTypeId;
	paymentMethodId = parameters.paymentMethodId;
	statusId = parameters.statusId;
	searchParentTypeId = context.searchParentType;
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, searchParentTypeId));
	List paymentTypes = delegator.findList("PaymentType", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	context.paymentTypes = paymentTypes;
	paymentTypeIdsList = EntityUtil.getFieldListFromEntityList(paymentTypes, "paymentTypeId", true);
	
	refundParentType = context.refundParentType;
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, refundParentType));
	List refundPaymentTypes = delegator.findList("PaymentType", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	context.refundPaymentTypes = refundPaymentTypes;

	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdTo));
	List paymentMethodsList = delegator.findList("PaymentMethod", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	context.paymentMethodsList = paymentMethodsList;	
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.LIKE, "%_PAYIN%"));
	List paymentMethodTypeList = delegator.findList("PaymentMethodType", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, ["-paymentMethodTypeId"], null, false);
	context.paymentMethodTypeList = paymentMethodTypeList;	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
	if(UtilValidate.isNotEmpty(paymentTypeId)){
		conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, paymentTypeId));
	}
	else{
		conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.IN, paymentTypeIdsList));
	}
	if(UtilValidate.isNotEmpty(paymentMethodId)){
		conditionList.add(EntityCondition.makeCondition("paymentMethodId", EntityOperator.EQUALS, paymentMethodId));
	}
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
	List paymentList = delegator.findList("Payment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	paymentIdsList = EntityUtil.getFieldListFromEntityList(paymentList, "paymentId", true);
	
	refundAvblPaymentList = [];
	for(i=0; i<paymentList.size(); i++){
		eachPayment = paymentList[i];
		
		// Get PaymentTypeId Attribute
		
		paymentTypeAttribute = delegator.findOne("PaymentTypeAttribute", ["paymentTypeId" : eachPayment.paymentTypeId, "attrName" : "REFUND_PMNT_TYPE"], false);
			
		paymentApplied = PaymentWorker.getPaymentApplied(eachPayment);
	    paymentAmount = eachPayment.getBigDecimal("amount");
	    paymentToApply = paymentAmount.subtract(paymentApplied);
	    
	    paymentDetailMap = [:];
		paymentDetailMap["paymentId"] = eachPayment.paymentId;
		paymentDetailMap["paymentTypeId"] = eachPayment.paymentTypeId;
		if(UtilValidate.isNotEmpty(paymentTypeAttribute)){
			paymentDetailMap["refundPaymentTypeId"] = paymentTypeAttribute.attrValue;
		}
		paymentDetailMap["paymentMethodTypeId"] = eachPayment.paymentMethodTypeId;
		paymentDetailMap["paymentMethodId"] = eachPayment.paymentMethodId;
		paymentDetailMap["partyIdFrom"] = eachPayment.partyIdFrom;
		paymentDetailMap["partyIdTo"] = eachPayment.partyIdTo;
		paymentDetailMap["amount"] = eachPayment.amount;
		//paymentDetailMap["refundedAmt"] = totalRefundedAmt;
		paymentDetailMap["availableAmtToRefund"] = paymentToApply;
		//paymentDetailMap["refundedPmntIds"] = EntityUtil.getFieldListFromEntityList(refundPaymentsList, "paymentId", true);
	    
	    if (paymentToApply <= 0 ) {
             continue;
        } 
		
		tempMap = [:];
		tempMap.putAll(paymentDetailMap);
		
		refundAvblPaymentList.add(tempMap);
		
	}*/
	
	context.partyPaymentsMap = partyPaymentsMap;
	
	
	
	
	
	

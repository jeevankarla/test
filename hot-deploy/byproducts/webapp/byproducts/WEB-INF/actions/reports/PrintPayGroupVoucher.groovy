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

import java.util.ArrayList;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
import in.vasista.vbiz.byproducts.TransporterServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.GenericServiceException;

printPaymentsList = [];
partyPaymentMap = [:];
paymentGroupId = parameters.paymentGroupId;
context.put("paymentGroupId",paymentGroupId);
if(UtilValidate.isNotEmpty(paymentGroupId)){
	paymentGroupMemberList = delegator.findList("PaymentGroupMember",EntityCondition.makeCondition("paymentGroupId", EntityOperator.EQUALS , paymentGroupId)  , null, null, null, false );
	if(UtilValidate.isNotEmpty(paymentGroupMemberList)){
		paymentGroupMemberList.each{ paymentGroupMember ->
			paymentId = paymentGroupMember.paymentId;
			if(UtilValidate.isNotEmpty(paymentId)){
				tempprintPaymentsList = delegator.findList("Payment",EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS , paymentId)  , null, null, null, false );
				tempprintPaymentsList.each{paymentRecipt->
					
					partyIdFromList=EntityUtil.getFirst(tempprintPaymentsList);
					branchId = partyIdFromList.partyIdFrom;
					branchIdForAdd="";
					branchList = [];
					condListb = [];
					if(branchId){
					condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
					condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
					condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
					
					PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
					
					branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
					if(!branchList){
						condListb2 = [];
						//condListb2.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,"%"));
						condListb2.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, branchId));
						condListb2.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
						condListb2.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
						cond = EntityCondition.makeCondition(condListb2, EntityOperator.AND);
						
						PartyRelationship1 = delegator.findList("PartyRelationship", cond,UtilMisc.toSet("partyIdFrom"), null, null, false);
						if(PartyRelationship1){
						branchDetails = EntityUtil.getFirst(PartyRelationship1);
						branchIdForAdd=branchDetails.partyIdFrom;
						}
					}
					else{
						if(branchId){
						branchIdForAdd=branchId;
						}
					}
					if(!branchList)
					branchList.add(branchId);
					}
					
					branchBasedWeaversList = [];
					condListb1 = [];
					if(branchId){
					condListb1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
					condListb1.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
					condListb = EntityCondition.makeCondition(condListb1, EntityOperator.AND);
					
					PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
					branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
					
					if(!branchBasedWeaversList)
					branchBasedWeaversList.add(branchId);
					}
					BOAddress="";
					if(branchIdForAdd){
					branchContextForADD=[:];
					branchContextForADD.put("branchId",branchIdForAdd);
					try{
						resultCtx = dispatcher.runSync("getBoHeader", branchContextForADD);
						if(ServiceUtil.isError(resultCtx)){
							Debug.logError("Problem in BO Header ", module);
							return ServiceUtil.returnError("Problem in fetching financial year ");
						}
						if(resultCtx.get("boHeaderMap")){
							boHeaderMap=resultCtx.get("boHeaderMap");
							
							if(boHeaderMap.get("header0")){
								BOAddress=boHeaderMap.get("header0");
							}
						}
					}catch(GenericServiceException ee){
						Debug.logError(ee, module);
						return ServiceUtil.returnError(ee.getMessage());
					}
					context.BOAddress=BOAddress;
					}
					
					invDts = delegator.findList("PaymentAndApplication",EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS , paymentId)  , null, null, null, false );
					tempprintPaymentMap=[:];
					if(UtilValidate.isNotEmpty(invDts)){
						invoiceId = invDts[0].invoiceId;
						tempprintPaymentMap.put("invoiceId",invoiceId);
					}
					tempprintPaymentMap.putAll(paymentRecipt);
					totalAmount=paymentRecipt.amount;
					amountwords=UtilNumber.formatRuleBasedAmount(totalAmount,"%rupees-and-paise", locale).toUpperCase();
					tempprintPaymentMap.put("amountWords",amountwords);
					finalPaymentMap = [:];
					finalPaymentMap.putAll(tempprintPaymentMap);
					printPaymentsList.add(finalPaymentMap);
					context.put("printPaymentsList",printPaymentsList);
					partyIdTo = paymentRecipt.partyIdTo;
					if(UtilValidate.isEmpty(partyPaymentMap[partyIdTo])){
						tempList=[];
						tempList.add(tempprintPaymentMap);
						partyIdTo = paymentRecipt.partyIdTo;
						partyPaymentMap[partyIdTo]=tempList;
					}else{
						tempList=[];
						tempList=partyPaymentMap[partyIdTo];
						tempList.add(tempprintPaymentMap);
						partyPaymentMap[partyIdTo]=tempList;
					}
				}
			}
		}
	}
}
context.partyMap = partyPaymentMap;
paymentGroup = delegator.findOne("PaymentGroup", UtilMisc.toMap("paymentGroupId", paymentGroupId), false);
abstractDetails = [:];
tempAmount = 0;
if(paymentGroup.amount){
	tempAmount = paymentGroup.amount;
}
amountInWords=UtilNumber.formatRuleBasedAmount(tempAmount,"%rupees-and-paise", locale).toUpperCase();
abstractDetails.put("amount", tempAmount);
abstractDetails.put("amountInWords", amountInWords);
context.abstractDetails = abstractDetails;


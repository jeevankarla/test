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

//${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalAmount?string("#0")), "%rupees-and-paise", locale).toUpperCase()}
reportTypeFlag = context.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "depositCheque"){
	finAccountId = parameters.finAccountId;
	description = null;
	if(UtilValidate.isNotEmpty(finAccountId)){
		finAccountDetails = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);
		if(UtilValidate.isNotEmpty(finAccountId)){
			finAccountTypeId = finAccountDetails.finAccountTypeId;
			if(UtilValidate.isNotEmpty(finAccountTypeId)){
				finAccountTypeDetails = delegator.findOne("FinAccountType", [finAccountTypeId : finAccountTypeId], false);
				if(UtilValidate.isNotEmpty(finAccountTypeDetails.description)){
					description = finAccountTypeDetails.description;
					context.put("description",description);
				}
			}
		}
		finAccountTransList = delegator.findList("FinAccountTrans", EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId), null, null, null, false);
		if(UtilValidate.isNotEmpty(finAccountTransList)){
			finAccountTransDetails = EntityUtil.getFirst(finAccountTransList);
			if(UtilValidate.isNotEmpty(finAccountTransDetails)){
				finAccountTransId = finAccountTransDetails.finAccountTransId;
				if(UtilValidate.isNotEmpty(finAccountTransId)){
					finAccountTransAttributeDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
					finAccountTransTypeId = null;
					amount = BigDecimal.ZERO;
					paymentDate = null;
					comments = null;
					contraRefNum = null;
					performedByPartyId = null;
					partyName = null;
					amountWords = null;
					finAccountName = null;
					newFinAccountTransId = null;
					if(UtilValidate.isNotEmpty(finAccountTransAttributeDetails)){
						newFinAccountTransId = finAccountTransAttributeDetails.attrValue;
						if(UtilValidate.isNotEmpty(finAccountTransAttributeDetails)){
							newfinAccountTransDetails = delegator.findOne("FinAccountTrans", [finAccountTransId : newFinAccountTransId], false);
							if(UtilValidate.isNotEmpty(newfinAccountTransDetails)){
								finAccountId = newfinAccountTransDetails.finAccountId;
								if(UtilValidate.isNotEmpty(finAccountId)){
									finAccountDetails = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);
									if(UtilValidate.isNotEmpty(finAccountId)){
										finAccountName = finAccountDetails.finAccountName;
										if(UtilValidate.isNotEmpty(finAccountName)){
											context.put("finAccountName",finAccountName);
										}
									}
								}
								finAccountTransTypeId = newfinAccountTransDetails.finAccountTransTypeId;
								amount = newfinAccountTransDetails.amount;
								paymentDate = newfinAccountTransDetails.transactionDate;
								comments = newfinAccountTransDetails.comments;
								contraRefNum = newfinAccountTransDetails.contraRefNum;
								performedByPartyId = newfinAccountTransDetails.performedByPartyId;
								partyName = PartyHelper.getPartyName(delegator, performedByPartyId, false);
								amountWords=UtilNumber.formatRuleBasedAmount(amount,"%rupees-and-paise", locale).toUpperCase();
								
								if(UtilValidate.isNotEmpty(finAccountTransTypeId)){
									context.put("finAccountTransTypeId",finAccountTransTypeId);
								}
								if(UtilValidate.isNotEmpty(newFinAccountTransId)){
									context.put("newFinAccountTransId",newFinAccountTransId);
								}
								if(UtilValidate.isNotEmpty(comments)){
									context.put("comments",comments);
								}
								if(UtilValidate.isNotEmpty(paymentDate)){
									context.put("paymentDate",paymentDate);
								}
								if(UtilValidate.isNotEmpty(amount)){
									context.put("amount",amount);
								}
								if(UtilValidate.isNotEmpty(amountWords)){
									context.put("amountWords",amountWords);
								}
								if(UtilValidate.isNotEmpty(performedByPartyId)){
									context.put("partyId",performedByPartyId);
								}
								if(UtilValidate.isNotEmpty(partyName)){
									context.put("partyName",partyName);
								}
								if(UtilValidate.isNotEmpty(contraRefNum)){
									context.put("contraRefNum",contraRefNum);
								}
							}
						}
					}
				}
			}
		}
	}
}else{
	paymentIds=FastList.newInstance();
	tempPaymentIds=FastList.newInstance();
	conditionList=[];
	if(parameters.paymentId){
		paymentId=parameters.paymentId;
		tempPaymentIds.add(paymentId);
		parameters.paymentIds = tempPaymentIds;
	}
	paymentMethodTypeId = "";
	paymentDescription = "";
	printPaymentsList = FastList.newInstance();
	if(parameters.paymentIds){
		
		paymentIds.addAll(parameters.paymentIds);
		tempprintPaymentsList = delegator.findList("Payment",EntityCondition.makeCondition("paymentId", EntityOperator.IN , paymentIds)  , null, null, null, false );
		tempprintPaymentsList.each{paymentRecipt->
			tempprintPaymentMap=[:];
			tempprintPaymentMap.putAll(paymentRecipt);
			totalAmount=paymentRecipt.amount;
			paymentMethodTypeId = paymentRecipt.paymentMethodTypeId;
			context.put("paymentMethodTypeId",paymentMethodTypeId);
			paymentMethodTypeDetails = delegator.findOne("PaymentMethodType", [paymentMethodTypeId : paymentMethodTypeId], false);
			if(UtilValidate.isNotEmpty(paymentMethodTypeDetails)){
				paymentDescription = paymentMethodTypeDetails.description;
				context.put("paymentDescription",paymentDescription);
			}
		
			amountwords=UtilNumber.formatRuleBasedAmount(totalAmount,"%rupees-and-paise", locale).toUpperCase();
			tempprintPaymentMap.put("amountWords",amountwords);
			printPaymentsList.add(tempprintPaymentMap);
		}
	}
	
	paymentMethodType = delegator.findList("PaymentMethodType", EntityCondition.makeCondition(["paymentMethodTypeId" : printPaymentsList[0].get("paymentMethodTypeId")]), null, null, null, false);
	paymentMethodTypeDesc = paymentMethodType[0].get("description");
	context.paymentMethodTypeDesc = paymentMethodTypeDesc;
	
	paymentType = delegator.findList("PaymentType", EntityCondition.makeCondition(["paymentTypeId" : printPaymentsList[0].get("paymentTypeId")]), null, null, null, false);
	parentTypeId = paymentType[0].get("parentTypeId");
	
	reportType = null;
	
	if( (printPaymentsList[0].get("paymentTypeId")).contains("PAYIN")){
		reportType = "Receipt";
	}
	else{
		reportType = "Payment";
	}
	
	//if(parentTypeId == "RECEIPT"){
	//	reportType = "RECEIPT";
	//}
	//else{
	//	reportType = "PAYMENT";
	//}
	
	
	context.paymentTypeDescription = paymentType[0].get("description");
	context.reportType = reportType;
	context.put("printPaymentsList",printPaymentsList);
}




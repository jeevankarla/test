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
	finAccountFinalTransList = [];
	finAccountTransMap = [:];
	finAccountId = parameters.finAccountId;
	description = null;
	partyId = null;
	partyName = null;
	tempMap = [:];
	if(UtilValidate.isNotEmpty(finAccountId)){
		finAccountDetails = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);
		if(UtilValidate.isNotEmpty(finAccountDetails)){
			finAccountTypeId = finAccountDetails.finAccountTypeId;
			partyId = finAccountDetails.ownerPartyId;
			if(UtilValidate.isNotEmpty(partyId)){
				partyName = PartyHelper.getPartyName(delegator, partyId, false);
			}
			if(UtilValidate.isNotEmpty(partyId)){
				tempMap.put("partyId",partyId);
			}
			if(UtilValidate.isNotEmpty(partyName)){
				tempMap.put("partyName",partyName);
			}
			
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
			finAccountTransList.each{ finAccountTransDetails->
				tempFinAccountTransMap = [:];
				if(UtilValidate.isNotEmpty(finAccountTransDetails)){
					finAccountTransId = finAccountTransDetails.finAccountTransId;
					if(UtilValidate.isNotEmpty(finAccountTransId)){
						finAccountTransAttributeDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
						finAccountTransTypeId = null;
						amount = BigDecimal.ZERO;
						paymentDate = null;
						comments = null;
						contraRefNum = null;
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
												tempMap.put("finAccountName",finAccountName);
											}
										}
									}
									if(UtilValidate.isNotEmpty(newFinAccountTransId)){
										finAccntTransSequenceList = delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, newFinAccountTransId), null, null, null, false);
										if(UtilValidate.isNotEmpty(finAccntTransSequenceList)){
											finAccntTransSequence = EntityUtil.getFirst(finAccntTransSequenceList);
											if(UtilValidate.isNotEmpty(finAccntTransSequence)){
												paymentTransSequenceId = finAccntTransSequence.transSequenceId;
											}
										}
									}
									finAccountTransTypeId = newfinAccountTransDetails.finAccountTransTypeId;
									amount = newfinAccountTransDetails.amount;
									paymentDate = newfinAccountTransDetails.transactionDate;
									comments = newfinAccountTransDetails.comments;
									contraRefNum = newfinAccountTransDetails.contraRefNum;
									amountWords=UtilNumber.formatRuleBasedAmount(amount,"%rupees-and-paise", locale).toUpperCase();
									if(UtilValidate.isNotEmpty(paymentTransSequenceId)){
										tempMap.put("paymentTransSequenceId",paymentTransSequenceId);
									}
									if(UtilValidate.isNotEmpty(finAccountTransTypeId)){
										tempMap.put("finAccountTransTypeId",finAccountTransTypeId);
									}
									if(UtilValidate.isNotEmpty(newFinAccountTransId)){
										tempMap.put("newFinAccountTransId",newFinAccountTransId);
									}
									if(UtilValidate.isNotEmpty(comments)){
										tempMap.put("comments",comments);
									}
									if(UtilValidate.isNotEmpty(paymentDate)){
										tempMap.put("paymentDate",paymentDate);
									}
									if(UtilValidate.isNotEmpty(amount)){
										tempMap.put("amount",amount);
									}
									if(UtilValidate.isNotEmpty(amountWords)){
										tempMap.put("amountWords",amountWords);
									}
									if(UtilValidate.isNotEmpty(contraRefNum)){
										tempMap.put("contraRefNum",contraRefNum);
									}
									tempFinAccountTransMap.putAll(tempMap);
								}
							}
						}
					}
				}
				if(UtilValidate.isNotEmpty(tempFinAccountTransMap)){
					finAccountFinalTransList.addAll(tempFinAccountTransMap);
					context.put("finAccountFinalTransList",finAccountFinalTransList);
				}
			}
		}
	}
}else{
	if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "loanRecovery"){
		finAccountTransId = parameters.finAccountTransId;
		if(UtilValidate.isNotEmpty(finAccountTransId)){
			finAccountTransAttributeDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
			finAccountTransTypeId = null;
			amount = BigDecimal.ZERO;
			paymentDate = null;
			comments = null;
			contraRefNum = null;
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
						amountWords=UtilNumber.formatRuleBasedAmount(amount,"%rupees-and-paise", locale).toUpperCase();
						
						if(UtilValidate.isNotEmpty(finAccountTransTypeId)){
							context.put("finAccountTransTypeId",finAccountTransTypeId);
						}
						if(UtilValidate.isNotEmpty(finAccountTransId)){
							context.put("newFinAccountTransId",finAccountTransId);
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
						if(UtilValidate.isNotEmpty(parameters.employeeId)){
							context.put("partyId",parameters.employeeId);
						}
						if(UtilValidate.isNotEmpty(parameters.partyName)){
							context.put("partyName",parameters.partyName);
						}
						if(UtilValidate.isNotEmpty(contraRefNum)){
							context.put("contraRefNum",contraRefNum);
						}
						if(UtilValidate.isNotEmpty(parameters.deducteePartyId)){
							context.put("deducteePartyId",parameters.deducteePartyId);
						}
						
						loanTypeId = parameters.loanTypeId;
						if(UtilValidate.isNotEmpty(loanTypeId)){
							employeeLoanType = delegator.findOne("LoanType", [loanTypeId : loanTypeId], false);
							if(UtilValidate.isNotEmpty(employeeLoanType)){
								description = employeeLoanType.description;
								if(UtilValidate.isNotEmpty(description)){
									context.put("loanRecoveryType",description);
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
		paymentApplication = delegator.findList("PaymentApplication",EntityCondition.makeCondition("toPaymentId", EntityOperator.IN , paymentIds)  , null, null, null, false );
		if(UtilValidate.isNotEmpty(paymentApplication)){
			paymentApplication.each{ payApplication ->
				paymentIdApp = payApplication.get("paymentId");
				paymentIds.addAll(paymentIdApp);
			}
		}
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
	
}




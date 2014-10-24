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


// rounding mode
decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
context.decimals = decimals;
context.rounding = rounding;

// first ensure ability to print
security = request.getAttribute("security");
context.put("security", security);
if (!security.hasEntityPermission("ACCOUNTING", "_PRINT_CHECKS", session)) {
	context.payments = payments; // if no permission, just pass an empty list for now
	return;
}


attrValue = "";
amount = BigDecimal.ZERO;
finAccountId = "";

partyName = "";
paymentDate = "";
amountStr = BigDecimal.ZERO;
comments = "";
// for contra 
reportTypeFlag = context.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "contraCheque"){
	finAccountId = parameters.finAccountId;
	finAccountTransId = parameters.finAccountTransId;
	if(finAccountId.contains("FIN_ACCNT")){
		if(UtilValidate.isNotEmpty(finAccountId)){
			amount = parameters.amount;
			BigDecimal amount = new BigDecimal(amount);
			amountWords = UtilFormatOut.formatCurrency(amount, context.get("currencyUomId"), locale);
			amountStr = amountWords.replace("Rs"," ");
			comments = parameters.comments;
			Timestamp transactionDate = (Timestamp)
			String transactionDateStr = parameters.transactionDate;
			def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				paymentDate = new java.sql.Timestamp(sdf.parse(transactionDateStr+" 00:00:00").getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + transactionDateStr, "");
			}
			
			if(UtilValidate.isNotEmpty(finAccountTransId)){
				finAccountTransAttrDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "INFAVOUR_OF"], false);
				if(UtilValidate.isNotEmpty(finAccountTransAttrDetails)){
					attrValue = finAccountTransAttrDetails.attrValue;
				}
			}
			
			if(UtilValidate.isNotEmpty(finAccountId)){
				context.put("finAccountId",finAccountId);
			}
			if(UtilValidate.isNotEmpty(attrValue)){
				context.put("attrValue",attrValue);
			}else{
				context.put("attrValue",comments);
			}
			if(UtilValidate.isNotEmpty(paymentDate)){
				context.put("paymentDate",paymentDate);
			}
			if(UtilValidate.isNotEmpty(amount)){
				context.put("amount",amount);
			}
			if(UtilValidate.isNotEmpty(amountStr)){
				context.put("amountStr",amountStr);
			}
		}
	}else{
		Debug.logError("finAccountId Cannot Be Empty","");
		context.errorMessage = "Not a valid bank financial account...!";
		return;
	}
}else{
	// for Deposit Cheque
	if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "depositCheque"){
		finAccountId = parameters.finAccountId;
		if(UtilValidate.isNotEmpty(finAccountId)){
			finAccountTransList = delegator.findList("FinAccountTrans", EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId), null, null, null, false);
			if(UtilValidate.isNotEmpty(finAccountTransList)){
				finAccountTransDetails = EntityUtil.getFirst(finAccountTransList);
				if(UtilValidate.isNotEmpty(finAccountTransDetails)){
					finAccountTransId = finAccountTransDetails.finAccountTransId;
					if(UtilValidate.isNotEmpty(finAccountTransId)){
						finAccountTransAttributeDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
						if(UtilValidate.isNotEmpty(finAccountTransAttributeDetails)){
							newFinAccountTransId = finAccountTransAttributeDetails.attrValue;
							if(UtilValidate.isNotEmpty(finAccountTransAttributeDetails)){
								newfinAccountTransDetails = delegator.findOne("FinAccountTrans", [finAccountTransId : newFinAccountTransId], false);
								if(UtilValidate.isNotEmpty(newfinAccountTransDetails)){
									finAccountId = newfinAccountTransDetails.finAccountId;
									amount = newfinAccountTransDetails.amount;
									paymentDate = newfinAccountTransDetails.transactionDate;
									comments = newfinAccountTransDetails.comments;
									
									if(UtilValidate.isNotEmpty(newFinAccountTransId)){
										finAccountTransAttrDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : newFinAccountTransId, attrName : "INFAVOUR_OF"], false);
										if(UtilValidate.isNotEmpty(finAccountTransAttrDetails)){
											attrValue = finAccountTransAttrDetails.attrValue;
										}
									}
									
									BigDecimal amount = new BigDecimal(amount);
									amountWords = UtilFormatOut.formatCurrency(amount, context.get("currencyUomId"), locale);
									amountStr = amountWords.replace("Rs"," ");
									
									if(UtilValidate.isNotEmpty(finAccountId)){
										context.put("finAccountId",finAccountId);
									}
									if(UtilValidate.isNotEmpty(attrValue)){
										context.put("attrValue",attrValue);
									}else{
										context.put("attrValue",comments);
									}
									if(UtilValidate.isNotEmpty(paymentDate)){
										context.put("paymentDate",paymentDate);
									}
									if(UtilValidate.isNotEmpty(amount)){
										context.put("amount",amount);
									}
									if(UtilValidate.isNotEmpty(amountStr)){
										context.put("amountStr",amountStr);
									}
								}
							}
						}
					}
				}
			}
		}
	}else{
	//for cheque printing of Ap and Ar Screens
	paymentMethodId = "";
	paymentId = parameters.paymentId;
	invoiceId = parameters.invoiceId;
	
	if(UtilValidate.isEmpty(paymentId)){
		paymentAppls = delegator.findByAnd("PaymentApplication", [invoiceId : invoiceId]);
		if(UtilValidate.isNotEmpty(paymentAppls)){
			paymentDetails = EntityUtil.getFirst(paymentAppls);
			if(UtilValidate.isNotEmpty(paymentDetails)){
				paymentId = paymentDetails.paymentId;
			}
		}
	}
	if(UtilValidate.isNotEmpty(paymentId)){
		paymentDetails = delegator.findOne("Payment", [paymentId : paymentId], false);
		if(UtilValidate.isNotEmpty(paymentDetails)){
			paymentMethodId = paymentDetails.paymentMethodId;
			paymentDate = paymentDetails.instrumentDate;
		}
		paymentAttrDetails = delegator.findOne("PaymentAttribute", [paymentId : paymentId, attrName : "INFAVOUR_OF"], false);
		if(UtilValidate.isNotEmpty(paymentAttrDetails)){
			attrValue = paymentAttrDetails.attrValue;
		}
		if(UtilValidate.isNotEmpty(paymentDetails)){
			amount = paymentDetails.amount;
			amountWords = UtilFormatOut.formatCurrency(amount, context.get("currencyUomId"), locale);
			amountStr = amountWords.replace("Rs"," ");
		}
		if(UtilValidate.isNotEmpty(paymentDetails.partyIdFrom) && (paymentDetails.partyIdFrom == "Company")){
			partyId = paymentDetails.partyIdTo;
		}else{
			partyId = paymentDetails.partyIdFrom;
		}
		partyName = PartyHelper.getPartyName(delegator, partyId, false);
		finAccountTransList = delegator.findList("FinAccountTrans", EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, paymentId), null, null, null, false);
		if(UtilValidate.isNotEmpty(finAccountTransList)){
			finAccountTransDetails = EntityUtil.getFirst(finAccountTransList);
			if(UtilValidate.isNotEmpty(finAccountTransDetails)){
				finAccountId = finAccountTransDetails.finAccountId;
			}
		}
		if(UtilValidate.isNotEmpty(attrValue)){
			context.put("attrValue",attrValue);
		}else{
			context.put("attrValue",partyName);
		}
	}
	context.put("finAccountId",finAccountId);
	context.put("amount",amount);
	context.put("amountStr",amountStr);
	context.put("paymentId",paymentId);
	context.put("paymentDate",paymentDate);
	if(UtilValidate.isEmpty(finAccountId)){
		Debug.logError("finAccountId Cannot Be Empty","");
		context.errorMessage = "Accounting Transactions not done...!";
		return;
	}
	if(UtilValidate.isNotEmpty(finAccountId) && finAccountId == "FIN_ACCNT1"){
		Debug.logError("cash payment","");
		context.errorMessage = "Not a Cheque Payment....!";
		return;
	}
	// list of payments
	payments = [];
	
	// in the case of a single payment, the paymentId will be supplied
	paymentId = context.paymentId;
	if (paymentId) {
		payment = delegator.findByPrimaryKey("Payment", [paymentId : paymentId]);
		if (payment) payments.add(payment);
		context.payments = payments;
		return;
	}
	
	// in the case of a multi form, parse the multi data and get all of the selected payments
	selected = UtilHttp.parseMultiFormData(parameters);
	selected.each { row ->
		payment = delegator.findByPrimaryKey("Payment", [paymentId : row.paymentId]);
		if (payment) {
			payments.add(payment);
		}
	}
	paymentGroupMembers = EntityUtil.filterByDate(delegator.findList("PaymentGroupMember", EntityCondition.makeCondition("paymentGroupId", EntityOperator.EQUALS, parameters.paymentGroupId), null, null, null, false));
	//in the case of a multiple payments, paymentId List is supplied.
	paymentGroupMembers.each { paymentGropupMember->
		payments.add(paymentGropupMember.getRelatedOne("Payment"));
	}
	context.payments = payments;
	}
}




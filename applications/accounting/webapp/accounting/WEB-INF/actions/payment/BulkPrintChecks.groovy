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


    // list of payments
   payments = [];
	//for cheque printing of Ap and Ar Screens
	
	
	Debug.log("------===="+paymentIds);
	paymentIds.each{paymentId ->
      TempMap=[:];
		Debug.log("------===="+paymentId);
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
		/*if(UtilValidate.isNotEmpty(paymentDetails.partyIdFrom) && (paymentDetails.partyIdFrom == "Company")){
			partyId = paymentDetails.partyIdTo;
		}else{
			partyId = paymentDetails.partyIdFrom;
		}*/
		if(UtilValidate.isNotEmpty(paymentDetails.partyIdTo)){
			partyId = paymentDetails.partyIdTo;
			}	
		partyName="";
		if(partyId){
			partyName = PartyHelper.getPartyName(delegator, partyId, false);
		}
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
		TempMap.put("attrValue", context.attrValue);
		//context.put("finAccountId",finAccountId);
		TempMap.put("finAccountId", finAccountId);
		//context.put("amount",amount);
		TempMap.put("amount", amount);
		if(amount){
			amountinWords=UtilNumber.formatRuleBasedAmount(amount,"%indRupees-and-paiseRupees", locale).toUpperCase();
			context.put("amountinWords",amountinWords);
		}
		//context.put("amountStr",amountStr);
		TempMap.put("amountStr", amountStr);
		//context.put("paymentId",paymentId);
		TempMap.put("paymentId", paymentId);
		//context.put("paymentDate",paymentDate);
		TempMap.put("paymentDate", paymentDate);
		
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
		
		payments.add(TempMap);
		

	}
  context.payments=payments;
	Debug.log("payments==="+payments);


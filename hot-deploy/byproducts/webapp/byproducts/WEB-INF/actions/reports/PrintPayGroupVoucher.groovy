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

printPaymentsList = [];
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
					tempprintPaymentMap=[:];
					tempprintPaymentMap.putAll(paymentRecipt);
					totalAmount=paymentRecipt.amount;
					amountwords=UtilNumber.formatRuleBasedAmount(totalAmount,"%rupees-and-paise", locale).toUpperCase();
					tempprintPaymentMap.put("amountWords",amountwords);
					finalPaymentMap = [:];
					finalPaymentMap.putAll(tempprintPaymentMap);
					printPaymentsList.add(finalPaymentMap);
					context.put("printPaymentsList",printPaymentsList);
				}
			}
		}
	}
}
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



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
// for batch Payment

finAccntTransGroupId = parameters.finAccntTransGroupId;

paymentGroup = delegator.findOne("FinAccountTransGroup", UtilMisc.toMap("finAccntTransGroupId", finAccntTransGroupId), false);
	finAccountId = paymentGroup.finAccountId;
	amount = paymentGroup.amount;
	amountWords = UtilFormatOut.formatCurrency(amount, context.get("currencyUomId"), locale);
	amountStr = amountWords.replace("Rs"," ");
	Timestamp transactionDate = (Timestamp)
	paymentDate = paymentGroup.finAccntTransDate;
	if(UtilValidate.isNotEmpty(finAccntTransGroupId)){
		ContraGroupMemberList = delegator.findList("FinAccountTransGroupMember",EntityCondition.makeCondition("finAccntTransGroupId", EntityOperator.EQUALS , finAccntTransGroupId)  , null, null, null, false );
	   Debug.log("ContraGroupMemberList==================="+ContraGroupMemberList);
		 if(UtilValidate.isNotEmpty(ContraGroupMemberList)){
			ContraGroupMemberList.each{ paymentGroupMember ->
				finAccountTransId = paymentGroupMember.finAccountTransId;
				Debug.log("finAccountTransId=================="+finAccountTransId);
				if(UtilValidate.isNotEmpty(finAccountTransId)){
					cheqInFavour="";
					finAccountTransAttributeDetails = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
					
					if(UtilValidate.isNotEmpty(finAccountTransAttributeDetails)){
					finAccountTranscheqInFavour= delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransAttributeDetails.finAccountTransId, attrName : "INFAVOUR_OF"], false);
						if(UtilValidate.isEmpty(finAccountTranscheqInFavour)){
							finAccountTranscheqInFavour= delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransAttributeDetails.attrValue, attrName : "INFAVOUR_OF"], false);
						}
				
					}
					transIds=[];
					transIds.add(finAccountTransAttributeDetails.finAccountTransId);
					transIds.add(finAccountTransAttributeDetails.attrValue);
					if(finAccountTranscheqInFavour){
						cheqInFavour=finAccountTranscheqInFavour.attrValue;
					}
					tempprintContraList = delegator.findList("FinAccountTrans",EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS , finAccountTransId)  , null, null, null, false );
					tempprintContraList.each{paymentRecipt->
						inFavor="";
						inFavor = paymentRecipt.comments;
						if(cheqInFavour){
							inFavor=cheqInFavour;
						}
					}
				}
			}
		 }
	}
	
	if(UtilValidate.isNotEmpty(finAccountId)){
		context.put("finAccountId",finAccountId);
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
	if(UtilValidate.isNotEmpty(inFavor)){
		context.put("attrValue",inFavor);
	}


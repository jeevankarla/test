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
import java.text.ParseException;

formDateTime=UtilDateTime.nowTimestamp();
thruDateTime=UtilDateTime.nowTimestamp();
finAccountTransId=parameters.finAccountTransId;
reportTypeFlag = context.reportTypeFlag;
	finAccountFinalTransList = [];
	finAccountTransMap = [:];
	finAccountId = parameters.finAccountId;
	context.finAccountId=finAccountId;
	description = null;
	partyId = null;
	partyName = null;
	cheqInFavour="";
	tempMap = [:];
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
	context.cheqInFavour=cheqInFavour;
	transIds.each{ finAccountTransId ->
		finAccountTransDetails = delegator.findOne("FinAccountTrans", [finAccountTransId : finAccountTransId], false);
		if(UtilValidate.isNotEmpty(finAccountTransDetails)){
			tempFinAccountTransMap = [:];
				finAccountTransId = finAccountTransDetails.finAccountTransId;
				finAccountId=finAccountTransDetails.finAccountId;
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
				}
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
					paymentTransSequenceId = null;
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
											if(UtilValidate.isNotEmpty(paymentTransSequenceId)){
												tempMap.put("paymentTransSequenceId",paymentTransSequenceId);
											}
										}
									}
								}
								finAccountTransTypeId = newfinAccountTransDetails.finAccountTransTypeId;
								amount = newfinAccountTransDetails.amount;
								paymentDate = newfinAccountTransDetails.transactionDate;
								dayBegin = UtilDateTime.getDayStart(paymentDate);
								finYearContext = [:];
								finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
								finYearContext.put("organizationPartyId", "Company");
								finYearContext.put("userLogin", userLogin);
								finYearContext.put("findDate", dayBegin);
								finYearContext.put("excludeNoOrganizationPeriods", "Y");
								List customTimePeriodList = FastList.newInstance();
								Map resultCtx = FastMap.newInstance();
								try{
									resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
									if(ServiceUtil.isError(resultCtx)){
										Debug.logError("Problem in fetching financial year ", module);
										return ServiceUtil.returnError("Problem in fetching financial year ");
									}
								}catch(GenericServiceException e){
									Debug.logError(e, module);
									return ServiceUtil.returnError(e.getMessage());
								}
								customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
								if(UtilValidate.isNotEmpty(customTimePeriodList)){
									GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
									fromDate = (String)customTimePeriod.get("fromDate");
									thruDate = (String)customTimePeriod.get("thruDate");
									def sdf = new SimpleDateFormat("yyyy-MM-dd");
									try {
										if (fromDate) {
											fromDateTime = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
										}
										if (thruDate) {
											thruDateTime = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
										}
									} catch (ParseException e) {
										Debug.logError(e, "Cannot parse date string: " + e, "");
										context.errorMessage = "Cannot parse date string: " + e;
										return;
									}
									context.from=UtilDateTime.toDateString(fromDateTime,"yy");
									context.thru=UtilDateTime.toDateString(thruDateTime,"yy");
								}
								
								comments = newfinAccountTransDetails.comments;
								contraRefNum = newfinAccountTransDetails.contraRefNum;
								amountWords=UtilNumber.formatRuleBasedAmount(amount,"%rupees-and-paise", locale).toUpperCase();
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
			
			if(UtilValidate.isNotEmpty(tempFinAccountTransMap)){
				finAccountFinalTransList.addAll(tempFinAccountTransMap);
			}
		
		}
	
	}
	
	context.put("finAccountFinalTransList",finAccountFinalTransList);
	



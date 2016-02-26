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


import org.ofbiz.base.util.*
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

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.finaccount.FinAccountServices;


dctx = dispatcher.getDispatchContext();
reportTypeFlag = parameters.reportTypeFlag;
partyTotalDebits=0;
partyTotalCredits=0;
//finalpartyDayWiseFinHistryMap=[:];
totalPartyDayWiseFinHistryOpeningBal=[:];
dummyMap=[:];

localMultipleFinAccountHistoryMap=[:];
getmultipleFinAccountList = [];
if(UtilValidate.isNotEmpty(context.multipleFinAccountHistoryMap)){
		localMultipleFinAccountHistoryMap=context.multipleFinAccountHistoryMap;
	}
if(parameters.multifinAccount == "Y"){
		localMultipleFinAccountHistoryMap.each{ eachFinAccount ->
			getmultipleFinAccountList(eachFinAccount.getKey(),eachFinAccount.getValue());
			}
}else{
//	Debug.log("===finAccountTransList==IN==FindFinAccntttt======"+finAccountTransList);
	finAccountIdList= EntityUtil.getFieldListFromEntityList(finAccountTransList,"finAccountId", true);
getmultipleFinAccountList("",finAccountTransList);
}

	def getmultipleFinAccountList(finAccountId, finAccountTransList){
		if(UtilValidate.isNotEmpty(finAccountTransList)){
			partyDayWiseFinHistryMap=[:];
			finopeningBalMap=[:];
			FinTotalMap=[:];
			FinTotalObMap=[:];
			finCBMap=[:];
			openingBal=[:];
			totalOpenigbal=[:];
			finAccountReconciliationList=[];
			finAccountReconciliationListMap=[:];
			partyFinAccountTransList=[];
			partyFinAccountTransListMap=[:];
			finAccTransMap=[:];
		if(UtilValidate.isEmpty(parameters.reconsilationCsvReport)){
			finAccountopeningBal= delegator.findOne("FinAccount", ["finAccountId" :finAccountId], false);
			fromDateTime = null;
			if(UtilValidate.isNotEmpty(parameters.partyfromDate)&& UtilValidate.isNotEmpty(parameters.partythruDate)){
				def sdf = new SimpleDateFormat("yyyy, MMM dd");
				try {
					fromDateTime = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
					thruDateTime = new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: "+fromDate, "");
				}
				}
			finAccTransMap = FinAccountServices.getFinAccountTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"finAccountId",finAccountId,"transactionDate",fromDateTime));
			}
			oB=0;
			if(finAccTransMap){
			oB=finAccTransMap.get("openingBalance");
			}else{
			if(UtilValidate.isEmpty(parameters.reconsilationCsvReport)){
			oB= finAccountopeningBal.actualBalance;
			}
			}
			finopeningBalMap["debitValue"]=BigDecimal.ZERO;
			finopeningBalMap["creditValue"]=BigDecimal.ZERO;
			FinTotalMap["debitValue"]=BigDecimal.ZERO;
			FinTotalMap["creditValue"]=BigDecimal.ZERO;
			FinTotalObMap["debitValue"]=BigDecimal.ZERO;
			FinTotalObMap["creditValue"]=BigDecimal.ZERO;
			finCBMap["debitValue"]=BigDecimal.ZERO;
			finCBMap["creditValue"]=BigDecimal.ZERO;
			if(oB>0){
				finopeningBalMap["debitValue"]=oB;
				FinTotalObMap["debitValue"]+=oB;
			}else{
			  finopeningBalMap["creditValue"]= (-1*oB);
			  FinTotalObMap["creditValue"]+=(-1*oB);
			}
			openingBal.put("openingBal", finopeningBalMap);
		
			finAccountTransList.eachWithIndex {finAccountTrans, idx ->
				tempFinAccountTransMap=[:];
				tempFinAccountTransMap["sNo"]=idx+1;
				tempFinAccountTransMap["finAccountTransId"]=finAccountTrans.finAccountTransId;
				tempFinAccountTransMap["finAccountTransTypeId"]=finAccountTrans.finAccountTransTypeId;
				tempFinAccountTransMap["partyId"]=finAccountTrans.partyId;
				tempFinAccountTransMap["paymentPartyId"]="";
				tempFinAccountTransMap["paymentPartyName"]="";
				tempFinAccountTransMap["transactionDate"]=UtilDateTime.toDateString(finAccountTrans.transactionDate,"dd-MM-yy hh:mm:ss");
				tempFinAccountTransMap["instrumentNo"]="";
				tempFinAccountTransMap["amount"]=finAccountTrans.amount;
				tempFinAccountTransMap["paymentId"]=finAccountTrans.paymentId;
				tempFinAccountTransMap["paymentType"]="";
				tempFinAccountTransMap["paymentMethodTypeId"]="";
				tempFinAccountTransMap["statusId"]=finAccountTrans.statusId;
				tempFinAccountTransMap["comments"]=finAccountTrans.comments;
		
		
				//preparing AnotherMap for PartyLedger
				curntDay=UtilDateTime.toDateString(finAccountTrans.transactionDate,"dd-MM-yyyy");
				innerMap=[:];
				innerMap["partyId"]=finAccountTrans.partyId;
				innerMap["date"]=curntDay;
				innerMap["paymentDate"]=finAccountTrans.transactionDate;
				innerMap["paymentId"]=finAccountTrans.paymentId;
				innerMap["vchrType"]="";
				innerMap["vchrCode"]="";
				//here IntsrumentNO adding
				innerMap["instrumentNo"]="";
				innerMap["paymentMethodTypeId"]="";
				innerMap["description"]="";
				innerMap["amount"]=0;
				innerMap["crOrDbId"]="";
				innerMap["debitValue"]=0;
				innerMap["creditValue"]=0;
		
		        //for payment   
				if(UtilValidate.isNotEmpty(finAccountTrans.paymentId)){
					payment = delegator.findOne("Payment", [paymentId : finAccountTrans.paymentId], true);
					if(UtilValidate.isNotEmpty(payment.paymentTypeId)){
						paymentType=delegator.findOne("PaymentType", [paymentTypeId : payment.paymentTypeId], true);
						if(UtilValidate.isNotEmpty(paymentType)){
							tempFinAccountTransMap["paymentType"]=paymentType.description;
						}
					}
					if(UtilValidate.isNotEmpty(finAccountTrans.paymentId) && UtilValidate.isNotEmpty(finAccountTrans.finAccountTransTypeId)){
						if( finAccountTrans.finAccountTransTypeId=="WITHDRAWAL"){
							tempFinAccountTransMap["paymentPartyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, payment.partyIdTo, false);
							tempFinAccountTransMap["paymentPartyId"] = payment.partyIdTo;
							//if deposit for partyLedger it is Cr
							innerMap["creditValue"]=finAccountTrans.amount;
							partyTotalCredits+=finAccountTrans.amount;
							innerMap["crOrDbId"]="C";
						}else if(finAccountTrans.finAccountTransTypeId=="DEPOSIT"){
							tempFinAccountTransMap["paymentPartyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, payment.partyIdFrom, false);
							tempFinAccountTransMap["paymentPartyId"] = payment.partyIdFrom;
							//if deposit for partyLedger it is Dr
						innerMap["debitValue"]=finAccountTrans.amount;
						partyTotalDebits+=finAccountTrans.amount;
						innerMap["crOrDbId"]="D";
					}
					}
					tempFinAccountTransMap["instrumentNo"]=payment.paymentRefNum;
			        paymentMethodType = delegator.findOne("PaymentMethodType", ["paymentMethodTypeId" : payment.paymentMethodTypeId], true);
			          if(UtilValidate.isNotEmpty(paymentMethodType)){
			            tempFinAccountTransMap["paymentMethodTypeId"]=paymentMethodType.description;
			          }
				   }else if(UtilValidate.isNotEmpty(finAccountTrans.reasonEnumId) &&(finAccountTrans.reasonEnumId=="FATR_CONTRA")){
				          contraFinTransEntry = delegator.findOne("FinAccountTransAttribute", ["finAccountTransId" : finAccountTrans.finAccountTransId,"attrName" : "FATR_CONTRA"], true);
				          if(UtilValidate.isNotEmpty(contraFinTransEntry)){
								contraFinAccountTrans = delegator.findOne("FinAccountTrans", ["finAccountTransId" : contraFinTransEntry.finAccountTransId], false);
								if(UtilValidate.isNotEmpty(contraFinTransEntry)){
									if(UtilValidate.isNotEmpty(contraFinAccountTrans.finAccountTransTypeId)){						
										if( contraFinAccountTrans.finAccountTransTypeId=="WITHDRAWAL"){
											//if deposit for partyLedger it is Cr
											innerMap["creditValue"]=contraFinAccountTrans.amount;
											partyTotalCredits+=contraFinAccountTrans.amount;							
											innerMap["crOrDbId"]="C";
										}else if(contraFinAccountTrans.finAccountTransTypeId=="DEPOSIT"){
											//if deposit for partyLedger it is Dr
											innerMap["debitValue"]=contraFinAccountTrans.amount;							
											partyTotalDebits+=contraFinAccountTrans.amount;
											innerMap["crOrDbId"]="D";
										}
									}
									contraFinAccount= delegator.findOne("FinAccount", ["finAccountId" :contraFinAccountTrans.finAccountId], false);
									tempFinAccountTransMap["paymentPartyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, contraFinAccount.ownerPartyId, false);
									tempFinAccountTransMap["paymentPartyId"] = contraFinAccount.ownerPartyId;
								}
							}
				        tempFinAccountTransMap["instrumentNo"]=finAccountTrans.finAccountTransId;
			    }
				   //adding required fields to partyLedgerInnerMap
				   innerMap["instrumentNo"]=tempFinAccountTransMap["instrumentNo"];
				   innerMap["partyId"]=tempFinAccountTransMap["paymentPartyId"];
				   innerMap["description"]=tempFinAccountTransMap["paymentMethodTypeId"];
				   FinTotalMap["debitValue"]+=innerMap["debitValue"];
				   FinTotalMap["creditValue"]+=innerMap["creditValue"];
				   
				   partyFinAccountTransList.addAll(innerMap);
				   //partyFinAccountTransListMap.put(finAccountId, partyFinAccountTransList);
				   //preparing Map here
				   dayPaymentList=[];
				   dayPaymentList=partyDayWiseFinHistryMap[curntDay];
				   //Debug.log("=curntDay=="+curntDay+"==dayInvoiceList=="+dayPaymentList);
				   if(UtilValidate.isEmpty(dayPaymentList)){
					   dayTempPaymentList=[];
					   dayTempPaymentList.addAll(innerMap);
					   dayPaymentList=dayTempPaymentList;
					   partyDayWiseFinHistryMap.put(curntDay, dayPaymentList);
				   }else{
						dayPaymentList.addAll(innerMap);
					   partyDayWiseFinHistryMap.put(curntDay, dayPaymentList);
				   }
				   //finalpartyDayWiseFinHistryMap.put(finAccountId,partyDayWiseFinHistryMap);
				   finAccountReconciliationList.addAll(tempFinAccountTransMap);
			}
			//Debug.log("partyDayWiseFinHistryMap====================="+partyDayWiseFinHistryMap);
			FinTotalObMap["debitValue"]+= FinTotalMap["debitValue"];
			FinTotalObMap["creditValue"]+=FinTotalMap["creditValue"];
			fcB=FinTotalObMap["debitValue"]-FinTotalObMap["creditValue"];
			if(fcB>0){
				finCBMap["debitValue"]=fcB;
			}else{
			  finCBMap["creditValue"]= (-1*fcB);
			}
			openingBal.put("FinTransMap", FinTotalMap);
			openingBal.put("FinClsBalMap", finCBMap);
			openingBal.put("partyDayWiseFinHistryMap", partyDayWiseFinHistryMap);
			totalPartyDayWiseFinHistryOpeningBal.put(finAccountId, openingBal);
			
			context.finAccountReconciliationList=finAccountReconciliationList;
			//result for PartyLedger
			context.partyFinAccountTransList=partyFinAccountTransList;
				if(parameters.multifinAccount != "Y"){
					context.partyDayWiseFinHistryMap=partyDayWiseFinHistryMap;
					context.partyTotalDebits=partyTotalDebits;
					context.partyTotalCredits=partyTotalCredits;
				}
		}
}
		if(parameters.multifinAccount == "Y"){
			if(parameters.financialTransactions == "Y"){
				context.totalPartyDayWiseFinHistryOpeningBal=totalPartyDayWiseFinHistryOpeningBal;
				
				context.partyTotalDebits=partyTotalDebits;
				context.partyTotalCredits=partyTotalCredits;
			}
		}else{
		totalPartyDayWiseFinHistryOpeningBal=[:];
		context.totalPartyDayWiseFinHistryOpeningBal=totalPartyDayWiseFinHistryOpeningBal;
		
		}
		
//Debug.log("==finAccountReconciliationList=="+finAccountReconciliationList);
//Debug.log("==partyFinAccountTransList=="+partyFinAccountTransList);






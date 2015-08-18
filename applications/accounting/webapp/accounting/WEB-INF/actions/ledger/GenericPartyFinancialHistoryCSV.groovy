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

reportTypeFlag = parameters.reportTypeFlag;
finAccountReconciliationList=[];
partyFinAccountTransList=[];

partyDayWiseFinHistryMap=[:];
partyTotalDebits=0;
partyTotalCredits=0;

partyLedgerDayWiseCsvList=[];
partyDayWiseDetailCsvMap=[:];


if(UtilValidate.isNotEmpty(context.partyDayWiseDetailMap)){
	partyDayWiseDetailCsvMap=context.partyDayWiseDetailMap;
}
tempMap=[:];
tempMap.put("paymentId","OpeningBalance");
if(UtilValidate.isNotEmpty(context.partyOBMap)){
	partyOBMap=context.partyOBMap;
tempMap.put("debitValue",partyOBMap.get("debitValue"));
tempMap.put("creditValue",partyOBMap.get("creditValue"));
}
else{
	tempMap.put("debitValue",0);
	tempMap.put("creditValue",0);
	}
partyLedgerDayWiseCsvList.addAll(tempMap);
partyDayWiseDetailCsvMap.each{ eachDayPartyLedger->

	invList = eachDayPartyLedger.getValue().get("invoiceList");
	payList = eachDayPartyLedger.getValue().get("paymentList");
	if(UtilValidate.isNotEmpty(invList) || UtilValidate.isNotEmpty(payList)){
		//preparing Anothor List
		if(UtilValidate.isNotEmpty(invList)){
			partyLedgerDayWiseCsvList.addAll(invList);
		}
		if(UtilValidate.isNotEmpty(payList)){
			partyLedgerDayWiseCsvList.addAll(payList);
		}
	}
}
Debug.log("===partyLedgerDayWiseCsvList==IN==CSVVVVV======"+partyLedgerDayWiseCsvList);
temptranstotsMap=[:];
temptranstotsMap.put("paymentId","TransactionTotals");
if(UtilValidate.isNotEmpty(context.partyTrTotalMap)){
	partyTrTotalMap=context.partyTrTotalMap;
temptranstotsMap.put("debitValue",partyTrTotalMap.get("debitValue"));
temptranstotsMap.put("creditValue",partyTrTotalMap.get("creditValue"));
}
else{
	temptranstotsMap.put("debitValue",0);
	temptranstotsMap.put("creditValue",0);
	}
partyLedgerDayWiseCsvList.addAll(temptranstotsMap);

tempcloseBalMap=[:];
tempcloseBalMap.put("paymentId","ClosingBalance");
if(UtilValidate.isNotEmpty(context.partyCBMap)){
	partyCBMap=context.partyCBMap;
tempcloseBalMap.put("debitValue",partyCBMap.get("debitValue"));
tempcloseBalMap.put("creditValue",partyCBMap.get("creditValue"));
}
else{
	tempcloseBalMap.put("debitValue",0);
	tempcloseBalMap.put("creditValue",0);
	}
partyLedgerDayWiseCsvList.addAll(tempcloseBalMap);

if((parameters.financialTransactions == "Y") && (UtilValidate.isNotEmpty(context.totalPartyDayWiseFinHistryOpeningBal))){
	totalPartyDayWiseFinHistryOpeningBal=context.totalPartyDayWiseFinHistryOpeningBal;
	partyFinHistryWiseCsvList = [];
	if(totalPartyDayWiseFinHistryOpeningBal){
		Iterator partyDayWiseFin = totalPartyDayWiseFinHistryOpeningBal.entrySet().iterator();
		partyDayWiseFin.each{ partyDayWiseFinTrans ->
			tempMap=[:];
			tempMap.put("paymentId","***OpeningBalance");
			if(UtilValidate.isNotEmpty(partyDayWiseFinTrans.getValue().get("openingBal"))){
				FinOpenBal=partyDayWiseFinTrans.getValue().get("openingBal");
				tempMap.put("debitValue",FinOpenBal.get("debitValue"));
				tempMap.put("creditValue",FinOpenBal.get("creditValue"));
			}
			else{
				tempMap.put("debitValue",0);
				tempMap.put("creditValue",0);
			}
			partyFinHistryWiseCsvList.add(tempMap);
			spaceMap = [:];
			partyFinHistryWiseCsvList.add(spaceMap);
			descriptionMap = [:];
			description = "";
			vchrCode = "";
			finAccount=delegator.findOne("FinAccount",[finAccountId : partyDayWiseFinTrans.getKey()], false);
			finAccountType=delegator.findOne("FinAccountType",[finAccountTypeId : finAccount.finAccountTypeId], false);
			if(UtilValidate.isNotEmpty(finAccountType)){
				description = finAccountType.description;
			}
			descriptionCode = partyDayWiseFinTrans.getKey();
			descriptionMap.put("date", description+(descriptionCode));
			partyFinHistryWiseCsvList.add(descriptionMap);
			FinDayWiseTrans = partyDayWiseFinTrans.getValue().get("partyDayWiseFinHistryMap");
			FinDayWiseTrans.each{ FinTransDaywise ->
				FinAccTrans = FinTransDaywise.getValue();
				FinAccTrans.each{ FinTransDay ->
					tempFinnHisMap=[:];
					date = FinTransDaywise.getKey();
					description = FinTransDay.get("description");
					invoiceId = FinTransDay.get("instrumentNo");
					paymentId = FinTransDay.get("paymentId");
					vchrCode = FinTransDay.get("vchrCode");
					debitValue=FinTransDay.get("debitValue");
					creditValue=FinTransDay.get("creditValue");
					tempFinnHisMap.put("date", date);
					tempFinnHisMap.put("description", description);
					tempFinnHisMap.put("vchrCode", vchrCode);
					tempFinnHisMap.put("invoiceId", invoiceId);
					tempFinnHisMap.put("paymentId", paymentId);
					tempFinnHisMap.put("debitValue", debitValue);
					tempFinnHisMap.put("creditValue", creditValue);
					partyFinHistryWiseCsvList.add(tempFinnHisMap);
				}
			}
			spaceMap = [:];
			partyFinHistryWiseCsvList.add(spaceMap);
			temptranstotsMap=[:];
			temptranstotsMap.put("paymentId","TransactionTotals");
			if(UtilValidate.isNotEmpty(partyDayWiseFinTrans.getValue().get("FinTransMap"))){
				FinTransTotal=partyDayWiseFinTrans.getValue().get("FinTransMap");
				temptranstotsMap.put("debitValue",FinTransTotal.get("debitValue"));
				temptranstotsMap.put("creditValue",FinTransTotal.get("creditValue"));
			}
			else{
				temptranstotsMap.put("debitValue",0);
				temptranstotsMap.put("creditValue",0);
			}
			partyFinHistryWiseCsvList.add(temptranstotsMap);
			tempcloseBalMap=[:];
			tempcloseBalMap.put("paymentId","ClosingBalance");
			if(UtilValidate.isNotEmpty(partyDayWiseFinTrans.getValue().get("FinClsBalMap"))){
				FinCloseBal=partyDayWiseFinTrans.getValue().get("FinClsBalMap");
				tempcloseBalMap.put("debitValue",FinCloseBal.get("debitValue"));
				tempcloseBalMap.put("creditValue",FinCloseBal.get("creditValue"));
			}
			else{
				tempcloseBalMap.put("debitValue",0);
				tempcloseBalMap.put("creditValue",0);
			}
			partyFinHistryWiseCsvList.add(tempcloseBalMap);
		}
		spaceMap = [:];
		partyFinHistryWiseCsvList.add(spaceMap);
		finalTotalBussinesTrMap = [:];
		finalTotalBussinesTrMap.put("invoiceId", "***Total Business Transaction Total:");
		if(UtilValidate.isNotEmpty(partyTotalTrasnsMap)){
			finalTotalBussinesTrMap.put("debitValue", partyTotalTrasnsMap.get("debitValue"));
			finalTotalBussinesTrMap.put("creditValue", partyTotalTrasnsMap.get("creditValue"));
		}
		partyFinHistryWiseCsvList.add(finalTotalBussinesTrMap);
		finalTotalFinnTrMap = [:];
		finalTotalFinnTrMap.put("invoiceId", "***Total Financial Transaction Total:");
		if(UtilValidate.isNotEmpty(finalTotalFinnTrMap)){
			finalTotalFinnTrMap.put("debitValue", FinTotalMap.get("debitValue"));
			finalTotalFinnTrMap.put("creditValue", FinTotalMap.get("creditValue"));
		}
		partyFinHistryWiseCsvList.add(finalTotalFinnTrMap);
		spaceMap = [:];
		partyFinHistryWiseCsvList.add(spaceMap);
		finalTotalTrMap = [:];
		finalTotalTrMap.put("invoiceId", "***Total Transaction Total:");
		if(UtilValidate.isNotEmpty(partyTrTotalMap)){
			finalTotalTrMap.put("debitValue", partyTrTotalMap.get("debitValue"));
			finalTotalTrMap.put("creditValue", partyTrTotalMap.get("creditValue"));
		}
		partyFinHistryWiseCsvList.add(finalTotalTrMap);
		finalTotalCsMap = [:];
		finalTotalCsMap.put("invoiceId", "***Total Closing Balance:");
		if(UtilValidate.isNotEmpty(partyCBMap)){
			finalTotalCsMap.put("debitValue", partyCBMap.get("debitValue"));
			finalTotalCsMap.put("creditValue", partyCBMap.get("creditValue"));
		}
		partyFinHistryWiseCsvList.add(finalTotalCsMap);
		context.partyFinHistryWiseCsvList=partyFinHistryWiseCsvList;
	}
}
context.partyLedgerDayWiseCsvList=partyLedgerDayWiseCsvList;

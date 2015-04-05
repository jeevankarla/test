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

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
import java.lang.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.sql.*;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;

import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.accounting.invoice.InvoiceWorker;

import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.party.party.PartyHelper;

userLogin= context.userLogin;

fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;

SimpleDateFormat formatter = new SimpleDateFormat("yyyy, MMM dd");
Timestamp fromDateTs = null;
if(fromDateStr){
	try {
		fromDateTs = new java.sql.Timestamp(formatter.parse(fromDateStr).getTime());
	} catch (ParseException e) {
	}
}
Timestamp thruDateTs = null;
if(thruDateStr){
	try {
		thruDateTs = new java.sql.Timestamp(formatter.parse(thruDateStr).getTime());
	} catch (ParseException e) {
	}
}
fromDate = UtilDateTime.getDayStart(fromDateTs, timeZone, locale);
thruDate = UtilDateTime.getDayEnd(thruDateTs, timeZone, locale);

List conditionList=FastList.newInstance();
List loanRecoveryList=FastList.newInstance();
List finAccountTransList=FastList.newInstance();
List finalList=FastList.newInstance();
conditionList.add(EntityCondition.makeCondition("recoveryDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("recoveryDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("deducteePartyId",EntityOperator.NOT_EQUAL,null),EntityOperator.AND,
	                                            EntityCondition.makeCondition("deducteePartyId",EntityOperator.NOT_EQUAL,"Company")));

condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
Set fieldToSelect =UtilMisc.toSet("loanId","loanFinAccountId","partyId","loanTypeId","description","recoveryDate");
fieldToSelect.add("finAccountTransId");
fieldToSelect.add("deducteePartyId");
fieldToSelect.add("sequenceNum");
fieldToSelect.add("principalInstNum");
fieldToSelect.add("principalAmount");
loanRecoveryList=delegator.findList("LoanAndRecoveryAndType",condition,fieldToSelect,null,null,false);
if(UtilValidate.isNotEmpty(loanRecoveryList)){
	finAccountTransIds=EntityUtil.getFieldListFromEntityList(loanRecoveryList,"finAccountTransId",true);
	cond=EntityCondition.makeCondition([EntityCondition.makeCondition("finAccountTransId",EntityOperator.IN,finAccountTransIds),
		                                EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"FINACT_TRNS_CANCELED")],EntityOperator.AND);
	finAccountTransList=delegator.findList("FinAccountTrans",cond,null,null,null,false);
	loanRecoveryList.each{eachRecovery->
		tempMap=[:];
		recoveryList=EntityUtil.filterByCondition(finAccountTransList,EntityCondition.makeCondition("finAccountTransId",EntityOperator.EQUALS,eachRecovery.finAccountTransId));
		recovery=EntityUtil.getFirst(recoveryList);
		tempMap.finAccountTransId=recovery.finAccountTransId;
		tempMap.finAccountTransTypeId=recovery.finAccountTransTypeId;
		tempMap.finAccountId=recovery.finAccountId;
		tempMap.transactionDate=UtilDateTime.toDateString(recovery.transactionDate,"dd-MM-yyyy");
		tempMap.entryDate=UtilDateTime.toDateString(recovery.entryDate,"dd-MM-yyyy");
		tempMap.amount=recovery.amount;
		tempMap.contraRefNum=recovery.contraRefNum;
		tempMap.performedByPartyId=recovery.performedByPartyId;
		tempMap.statusId=recovery.statusId;
		tempMap.comments=recovery.comments;
		tempMap.loanId=eachRecovery.loanId;
		tempMap.loanFinAccountId=eachRecovery.loanFinAccountId;
		tempMap.partyId=eachRecovery.partyId;
		tempMap.loanTypeId=eachRecovery.loanTypeId;
		tempMap.description=eachRecovery.description;
		tempMap.recoveryDate=UtilDateTime.toDateString(eachRecovery.recoveryDate,"dd-MM-yyyy");
		tempMap.sequenceNum=eachRecovery.sequenceNum;
		tempMap.principalInstNum=eachRecovery.principalInstNum;
		tempMap.principalAmount=eachRecovery.principalAmount;
		tempMap.closingBalance=eachRecovery.closingBalance;
		tempMap.deducteePartyId=eachRecovery.deducteePartyId;
		finalList.add(tempMap);
	}
}
context.finAccountTransList=finalList;




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
import org.ofbiz.entity.util.EntityListIterator;
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
typeId = parameters.typeId;
dctx = dispatcher.getDispatchContext();
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
context.dateFrom = UtilDateTime.toDateString(fromDate, "MMM dd, yyyy");
context.dateThru = UtilDateTime.toDateString(thruDate, "MMM dd, yyyy");
invoiceType = delegator.findList("InvoiceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, typeId), null, null, null, false);

invoiceTypeIds = EntityUtil.getFieldListFromEntityList(invoiceType, "invoiceTypeId", true);


List conditionList=FastList.newInstance();
if(invoiceTypeIds){
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.IN, invoiceTypeIds));
}
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);

EntityListIterator<GenericValue> invoicesIter = delegator.find("Invoice", condition, null, UtilMisc.toSet("invoiceId", "invoiceTypeId", "invoiceMessage", "invoiceDate", "paidDate", "partyId", "partyIdFrom", "statusId"), null, null);

invoiceApplicationDetailList = [];
distinctPartyIds = [];
partyNamesMap = [:];
invoiceIds = [];
invoiceDetailList = [];
invoicesIter.each{ eachItem ->
	invoiceAmt= InvoiceWorker.getInvoiceTotal(delegator,eachItem.invoiceId);
	invoiceId = eachItem.invoiceId;
	partyId = eachItem.partyId;
	if(partyId.equalsIgnoreCase("Company")){
		partyId = eachItem.partyIdFrom
	}
	partyName = "";
	if(!distinctPartyIds.contains(partyId)){
		distinctPartyIds.add(partyId);
		partyName=PartyHelper.getPartyName(delegator, partyId, false);
		partyNamesMap.put(partyId, partyName);
	}
	else{
		partyName = partyNamesMap.get(partyId);
	}
	invoiceIds.add(invoiceId);
	tempMap = [:];
	tempMap["invoiceId"] = invoiceId;
	tempMap["invoiceType"] = eachItem.invoiceTypeId;
	tempMap["invoiceDate"] = eachItem.invoiceDate;
	tempMap["partyId"] = partyId;
	tempMap["partyName"] = partyName;
	tempMap["paidDate"] = eachItem.paidDate;
	tempMap["invoiceAmount"] = invoiceAmt;
	tempMap["paymentId"] = "";
	tempMap["paymentDate"] = "";
	tempMap["paymentAmount"] = "";
	tempMap["applicationAmount"] = "";
	invoiceDetailList.add(tempMap);
		
}
invoicesIter.close();
EntityListIterator<GenericValue> invoiceSequenceIter = delegator.find("BillOfSaleInvoiceSequence", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds), null, UtilMisc.toSet("invoiceId", "billOfSaleTypeId", "sequenceId"), null, null);
sequenceMap = [:];
vatInvoices = [];
exciseInvoices = [];
invoiceSequenceIter.each{ eachSeq ->
	if(eachSeq.billOfSaleTypeId == "VAT_INV"){
		vatInvoices.add(eachSeq.invoiceId);
	}else{
		exciseInvoices.add(eachSeq.invoiceId);
	}
	sequenceMap.put(eachSeq.invoiceId, eachSeq.sequenceId);
}
invoiceSequenceIter.close();

EntityListIterator<GenericValue> invoicesApplicationIter = delegator.find("PaymentAndApplication", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds), null, null, null, null);
applicationMap = [:];
invoicesApplicationIter.each{ eachApp ->
	invoiceId = eachApp.invoiceId;
	
	tempMap = [:];
	tempMap.put("paymentId", eachApp.paymentId);
	tempMap.put("paymentDate", eachApp.paymentDate);
	tempMap.put("paymentAmount", eachApp.amount);
	tempMap.put("applicationAmount", eachApp.amountApplied);
	
	if(applicationMap.get(invoiceId)){
		tempResList = applicationMap.get(invoiceId);
		tempResList.add(tempMap);
		applicationMap.put(invoiceId, tempResList);
	}
	else{
		tempList = [];
		tempList.add(tempMap);
		applicationMap.put(invoiceId, tempList);
	}
}
finalInvoiceDetailList = [];
invoiceDetailList.each{ eachInvoiceDetail ->
	invoiceId = eachInvoiceDetail.get("invoiceId");
	seqMap = [:];
	seqId = "";
	seqType = "";
	if(sequenceMap.get(invoiceId)){
		seqId = sequenceMap.get(invoiceId);
	}
	
	if(vatInvoices.contains(invoiceId)){
		seqType = "VAT"
	}
	if(exciseInvoices.contains(invoiceId)){
		seqType = "EXCISE"
	}
	
	if(applicationMap.get(invoiceId)){
		applicationList = applicationMap.get(invoiceId);
		applicationList.each{ eachApp ->
			tempMap = [:];
			tempMap["invoiceId"] = invoiceId;
			tempMap["sequenceId"] = seqId;
			tempMap["sequenceType"] = seqType;
			tempMap["invoiceType"] = eachInvoiceDetail.get("invoiceType");
			tempMap["invoiceDate"] = UtilDateTime.toDateString(eachInvoiceDetail.get("invoiceDate"), "dd/MM/yyyy");
			tempMap["partyId"] = eachInvoiceDetail.get("partyId");
			tempMap["partyName"] = eachInvoiceDetail.get("partyName");
			tempMap["paidDate"] = "";
			if(eachInvoiceDetail.get("paidDate")){
				tempMap["paidDate"] = UtilDateTime.toDateString(eachInvoiceDetail.get("paidDate"), "dd/MM/yyyy");
			}
			tempMap["invoiceAmount"] = eachInvoiceDetail.get("invoiceAmount");
			tempMap["paymentId"] = eachApp.get("paymentId");
			tempMap["paymentDate"] = UtilDateTime.toDateString(eachApp.get("paymentDate"), "dd/MM/yyyy");
			tempMap["paymentAmount"] = eachApp.get("paymentAmount");
			tempMap["applicationAmount"] =  eachApp.get("applicationAmount");
			finalInvoiceDetailList.add(tempMap);
		}
	}
	else{
		eachInvoiceDetail.put("sequenceId", seqId);
		eachInvoiceDetail.put("sequenceType", seqType);
		eachInvoiceDetail.put("invoiceDate", UtilDateTime.toDateString(eachInvoiceDetail.get("invoiceDate"), "dd/MM/yyyy"));
		finalInvoiceDetailList.add(eachInvoiceDetail);
	}
}

invoicesApplicationIter.close();
context.invoiceApplicationDetailList=finalInvoiceDetailList;

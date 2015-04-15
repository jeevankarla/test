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
purposeTypeId = parameters.purposeTypeId;

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
conditionList.add(EntityCondition.makeCondition("purposeTypeId",EntityOperator.EQUALS,purposeTypeId));
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);

EntityListIterator<GenericValue> invoicesRefIter = delegator.find("Invoice", condition, null, null, null, null);

invoiceIds = EntityUtil.getFieldListFromEntityListIterator(invoicesRefIter,"invoiceId", true);

invoicesRefIter.close();

EntityListIterator<GenericValue> invoicesIter = delegator.find("Invoice", EntityCondition.makeCondition("invoiceId",EntityOperator.IN, invoiceIds), null, null, null, null);

List<GenericValue> paymentApplications = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds), null, null, null ,false);

invoiceApplicationDetailList = [];

invoicesIter.each{ eachItem ->
	invoiceAmt= InvoiceWorker.getInvoiceTotal(delegator,eachItem.invoiceId);
	invoiceId = eachItem.invoiceId;
	partyId = eachItem.partyId;
	if(partyId.equalsIgnoreCase("Company")){
		partyId = eachItem.partyIdFrom
	}
	invoiceApplications = EntityUtil.filterByCondition(paymentApplications, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
	
	if(invoiceApplications){
		invoiceApplications.each{ eachApp ->
			tempMap = [:];
			tempMap["invoiceId"] = invoiceId;
			tempMap["invoiceDate"] = eachItem.invoiceDate;
			tempMap["partyId"] = partyId;
			tempMap["paidDate"] = eachItem.paidDate;
			tempMap["invoiceAmount"] = invoiceAmt;
			tempMap["paymentId"] = eachApp.paymentId;
			tempMap["paymentAmount"] = eachApp.amount;
			tempMap["applicationAmount"] = eachApp.amountApplied;
			invoiceApplicationDetailList.add(tempMap);
		}
	}else{
		tempMap = [:];
		tempMap["invoiceId"] = invoiceId;
		tempMap["invoiceDate"] = eachItem.invoiceDate;
		tempMap["partyId"] = partyId;
		tempMap["paidDate"] = eachItem.paidDate;
		tempMap["invoiceAmount"] = invoiceAmt;
		tempMap["paymentId"] = "";
		tempMap["paymentAmount"] = "";
		tempMap["applicationAmount"] = "";
		invoiceApplicationDetailList.add(tempMap);
	}
}
invoicesIter.close();
context.invoiceApplicationDetailList=invoiceApplicationDetailList;

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
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.common.*;
import org.ofbiz.webapp.control.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastMap;
import java.util.Calendar;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
partyCode = parameters.partyId;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
}
Timestamp invoiceFirstDate=null;
Timestamp invoiceLastDate=null;
Timestamp paymentFirstDate=null;
Timestamp paymentLastDate=null;


partyFinHistoryMap=[:];

Boolean actualCurrency = new Boolean(context.actualCurrency);
if (actualCurrency == null) {
    actualCurrency = true;
}
actualCurrencyUomId = context.actualCurrencyUomId;
if (!actualCurrencyUomId) {
    actualCurrencyUomId = context.defaultOrganizationPartyCurrencyUomId;
}
findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
//get total/unapplied/applied invoices separated by sales/purch amount:
totalInvSaApplied         = BigDecimal.ZERO;
totalInvSaNotApplied     = BigDecimal.ZERO;
totalInvPuApplied         = BigDecimal.ZERO;
totalInvPuNotApplied     = BigDecimal.ZERO;
conditionList=[];

conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_IN_PROCESS"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_WRITEOFF"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
}
conditionList.add( EntityCondition.makeCondition([
            EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company")
                ],EntityOperator.AND),
            EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId)
                ],EntityOperator.AND)
            ],EntityOperator.OR));

newInvCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);

/*invExprs =
    EntityCondition.makeCondition([
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_IN_PROCESS"),
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_WRITEOFF"),
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"),
		if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
			EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)),
			EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)),
		}
        EntityCondition.makeCondition([
            EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company")
                ],EntityOperator.AND),
            EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId)
                ],EntityOperator.AND)
            ],EntityOperator.OR)
        ],EntityOperator.AND);
        
        invIterator = delegator.find("InvoiceAndType", invExprs, null, null, payOrderBy, findOpts);
        */

	List<String> payOrderBy = UtilMisc.toList("invoiceDate");
tempInvIterator=null;
invIterator = delegator.find("InvoiceAndType", newInvCondition, null, null, payOrderBy, findOpts);
//Debug.log("==invIterator===="+invIterator+"=invExprs==="+newInvCondition);
tempInvIterator=invIterator;

/*invoiceIdsList=EntityUtil.getFieldListFromEntityListIterator(tempInvIterator, "invoiceId", true);

Debug.log("==invoiceIdsList**************=="+invoiceIdsList+"====");

invoiceDateList=EntityUtil.getFieldListFromEntityListIterator(tempInvIterator, "invoiceDate", true);*/
/*invoiceFirstDate=invoiceDateList.getFirst();
invoiceLastDate=invoiceDateList.getLast();
Debug.log("==invoiceDateList**************=="+invoiceDateList+"====");
Debug.log("==invoiceDate=FF=="+invoiceDateList.getFirst());
Debug.log("==invoiceDate=EE=="+invoiceDateList.getLast());*/


arInvoiceDetailsMap=[:];
arInvoiceDetailsMap.put("invTotal", BigDecimal.ZERO);
apInvoiceDetailsMap=[:];
apInvoiceDetailsMap.put("invTotal", BigDecimal.ZERO);
invCounter=0;
while (invoice = invIterator.next()) {
	invoiceDate=invoice.invoiceDate;
	if(invCounter==0){
		invoiceFirstDate=invoice.invoiceDate;
	}
	invCounter=invCounter+1;
	innerMap=[:];
	curntDay=UtilDateTime.toDateString(invoiceDate ,"dd-MM-yyyy");
	//Debug.log("=curntDay=="+curntDay);
	innerMap["partyId"]="";
	innerMap["date"]=curntDay;
	innerMap["purposeTypeId"]=invoice.purposeTypeId;
	innerMap["invoiceDate"]=invoice.invoiceDate;
	innerMap["invoiceId"]=invoice.invoiceId;
	innerMap["tinNumber"]="";
	innerMap["vchrType"]="";
	innerMap["crOrDbId"]="D";
	invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
	//invTotalVal=invTotalVal-vatRevenue;
	innerMap["invTotal"]=invTotalVal;
    if ("PURCHASE_INVOICE".equals(invoice.parentTypeId)) {
		innerMap["partyId"]=invoice.partyIdFrom;
		innerMap["vchrType"]="PURCHASE";
		//preparing Map here
		dayInvoiceList=[];
		dayInvoiceList=apInvoiceDetailsMap[curntDay];
		//Debug.log("=curntDay=="+curntDay+"==dayInvoiceList=="+dayInvoiceList);
		if(UtilValidate.isEmpty(dayInvoiceList)){
			dayTempInvoiceList=[];
			dayTempInvoiceList.addAll(innerMap);
			dayInvoiceList=dayTempInvoiceList;
			apInvoiceDetailsMap.put(curntDay, dayInvoiceList);
		}else{
		     dayInvoiceList.addAll(innerMap);
			apInvoiceDetailsMap.put(curntDay, dayInvoiceList);
		}
		apInvoiceDetailsMap["invTotal"]+=invTotalVal;
    }
    else if ("SALES_INVOICE".equals(invoice.parentTypeId)) {
		innerMap["vchrType"]="SALES";
		innerMap["partyId"]=invoice.partyId;
		//preparing Map here
		dayInvoiceList=[];
		dayInvoiceList=arInvoiceDetailsMap[curntDay];
		//Debug.log("=curntDay=="+curntDay+"==dayInvoiceList=="+dayInvoiceList);
		if(UtilValidate.isEmpty(dayInvoiceList)){
			dayTempInvoiceList=[];
			dayTempInvoiceList.addAll(innerMap);
			dayInvoiceList=dayTempInvoiceList;
			arInvoiceDetailsMap.put(curntDay, dayInvoiceList);
		}else{
			 dayInvoiceList.addAll(innerMap);
			arInvoiceDetailsMap.put(curntDay, dayInvoiceList);
		}
		arInvoiceDetailsMap["invTotal"]+=invTotalVal;
    }
    else {
        Debug.logError("InvoiceType: " + invoice.invoiceTypeId + " without a valid parentTypeId: " + invoice.parentTypeId + " !!!! Should be either PURCHASE_INVOICE or SALES_INVOICE", "");
    }
}

invIterator.close();

//get total/unapplied/applied payment in/out total amount:

arPaymentDetailsMap=[:];
arPaymentDetailsMap.put("amount",BigDecimal.ZERO);
apPaymentDetailsMap=[:];
apPaymentDetailsMap.put("amount",BigDecimal.ZERO);

totalPayInApplied         = BigDecimal.ZERO;
totalPayInNotApplied     = BigDecimal.ZERO;
totalPayOutApplied         = BigDecimal.ZERO;
totalPayOutNotApplied     = BigDecimal.ZERO;


payCounter=0;
conditionList.clear();
conditionList.add( EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_NOTPAID"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_VOID"));
if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
}
conditionList.add(EntityCondition.makeCondition([
               EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, parameters.partyId),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company")
                ], EntityOperator.AND),
            EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, "Company"),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId)
                ], EntityOperator.AND)
            ], EntityOperator.OR));

newPayCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);

/*payExprs =
    EntityCondition.makeCondition([
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_NOTPAID"),
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_CANCELLED"),
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_VOID"),  
		if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
			EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)),
			EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime);),
		}
        EntityCondition.makeCondition([
               EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, parameters.partyId),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company")
                ], EntityOperator.AND),
            EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, "Company"),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId)
                ], EntityOperator.AND)
            ], EntityOperator.OR)
        ], EntityOperator.AND);
	payIterator = delegator.find("PaymentAndType", payExprs, null, null, orderBy, findOpts);*/
	
	List<String> orderBy = UtilMisc.toList("paymentDate");
payIterator = delegator.find("PaymentAndType", newPayCondition, null, null, orderBy, findOpts);

while (payment = payIterator.next()) {
	
	paymentDate=payment.paymentDate;
	if(payCounter==0){
		paymentFirstDate=payment.paymentDate;
	}
	payCounter=payCounter+1;
	innerMap=[:];
	curntDay=UtilDateTime.toDateString(paymentDate ,"dd-MM-yyyy");
	//Debug.log("=curntDay===in=Payment=="+curntDay);
	innerMap["partyId"]="";
	innerMap["date"]=curntDay;
	innerMap["paymentDate"]=payment.paymentDate;
	innerMap["paymentId"]=payment.paymentId;
	innerMap["vchrType"]="";
	innerMap["paymentMethodTypeId"]=payment.paymentMethodTypeId;
	innerMap["crOrDbId"]="D";
	innerMap["amount"]=payment.amount;
	
    if ("DISBURSEMENT".equals(payment.parentTypeId) || "TAX_PAYMENT".equals(payment.parentTypeId)) {
		innerMap["partyId"]=payment.partyIdTo;
		innerMap["vchrType"]="DISBURSEMENT";
		//preparing Map here
		dayPaymentList=[];
		dayPaymentList=apPaymentDetailsMap[curntDay];
		//Debug.log("=curntDay=="+curntDay+"==dayInvoiceList=="+dayPaymentList);
		if(UtilValidate.isEmpty(dayPaymentList)){
			dayTempPaymentList=[];
			dayTempPaymentList.addAll(innerMap);
			dayPaymentList=dayTempPaymentList;
			apPaymentDetailsMap.put(curntDay, dayPaymentList);
		}else{
			 dayPaymentList.addAll(innerMap);
			apPaymentDetailsMap.put(curntDay, dayPaymentList);
		}
		apPaymentDetailsMap.put("amount",arPaymentDetailsMap.get("amount").add(payment.amount));
    }
    else if ("RECEIPT".equals(payment.parentTypeId)) {
		innerMap["partyId"]=payment.partyIdFrom;
		innerMap["vchrType"]="RECEIPT";
		//preparing Map here
		dayPaymentList=[];
		dayPaymentList=arPaymentDetailsMap[curntDay];
		//Debug.log("=curntDay=="+curntDay+"==dayInvoiceList=="+dayPaymentList);
		if(UtilValidate.isEmpty(dayPaymentList)){
			dayTempPaymentList=[];
			dayTempPaymentList.addAll(innerMap);
			dayPaymentList=dayTempPaymentList;
			arPaymentDetailsMap.put(curntDay, dayPaymentList);
		}else{
			 dayPaymentList.addAll(innerMap);
			arPaymentDetailsMap.put(curntDay, dayPaymentList);
		}
		arPaymentDetailsMap.put("amount",arPaymentDetailsMap.get("amount").add(payment.amount));
		
    }
    else {
        Debug.logError("PaymentTypeId: " + payment.paymentTypeId + " without a valid parentTypeId: " + payment.parentTypeId + " !!!! Should be either DISBURSEMENT, TAX_PAYMENT or RECEIPT", "");
    }
}
payIterator.close();
//finalMap prepration
if(UtilValidate.isEmpty(fromDate)){
	if(paymentFirstDate<invoiceFirstDate){
		fromDateTime=paymentFirstDate;
	}else{
	fromDateTime=invoiceFirstDate;
	}
}
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate=dayBegin;
context.thruDate=dayEnd;
totalDays= UtilDateTime.getIntervalInDays(dayBegin,dayEnd)+1;
dayWiseArDetailMap=[:];
dayWiseApDetailMap=[:];
for(int j=0 ; j < (totalDays); j++){
	Timestamp saleDate = UtilDateTime.addDaysToTimestamp(dayBegin, j);
	reciepts = BigDecimal.ZERO;
	totalValue = BigDecimal.ZERO;
	curntDay=UtilDateTime.toDateString(saleDate ,"dd-MM-yyyy");
	//ar map preparation
	newTempMap=[:];
	invList=arInvoiceDetailsMap.get(curntDay);
	payList=arPaymentDetailsMap.get(curntDay);
	if(UtilValidate.isNotEmpty(invList) || UtilValidate.isNotEmpty(payList)){
		newTempMap.put("invoiceList", invList);
		newTempMap.put("paymentList", payList);
		dayWiseArDetailMap[curntDay]=newTempMap;
	}
	//AP map preparation
	newApTempMap=[:];
	invList=apInvoiceDetailsMap.get(curntDay);
	payList=apPaymentDetailsMap.get(curntDay);
	if(UtilValidate.isNotEmpty(invList) || UtilValidate.isNotEmpty(payList)){
		newApTempMap.put("invoiceList", invList);
		newApTempMap.put("paymentList", payList);
		dayWiseApDetailMap[curntDay]=newApTempMap;
	}
}
/*dayWiseArDetailMap["totInvoiceValue"]=arInvoiceDetailsMap.get("invTotal");
dayWiseArDetailMap["totPaymentValue"]=arPaymentDetailsMap.get("amount");

dayWiseApDetailMap["totInvoiceValue"]=apInvoiceDetailsMap.get("invTotal");
dayWiseApDetailMap["totPaymentValue"]=apInvoiceDetailsMap.get("amount");*/
context.arInvoiceDetailsMap=arInvoiceDetailsMap;
context.apInvoiceDetailsMap=apInvoiceDetailsMap;
context.arPaymentDetailsMap=arPaymentDetailsMap;
context.apPaymentDetailsMap=apPaymentDetailsMap;
context.dayWiseArDetailMap=dayWiseArDetailMap;
context.dayWiseApDetailMap=dayWiseApDetailMap;


//transferAmount = totalInvSaApplied.add(totalInvSaNotApplied).subtract(totalInvPuApplied.add(totalInvPuNotApplied)).subtract(totalPayInApplied.add(totalPayInNotApplied).add(totalPayOutApplied.add(totalPayOutNotApplied)));



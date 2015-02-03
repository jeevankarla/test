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

import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.accounting.invoice.*;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
invoiceList = [:];
invoiceitemMap=[:];
paymentApplicationMap=[:];
payment=[];
refundpaymentlist=[];
transSequenceIdMap=[:];
paymentId=parameters.paymentId;
payment = delegator.findOne("Payment", [paymentId : paymentId], true);
paymentApplication = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition(["paymentId" : paymentId]), null, null, null, true);
paymentApplication.each{ eachpaymentApplication ->
	paymentApplicationMap.put(eachpaymentApplication.invoiceId,eachpaymentApplication);
	if(eachpaymentApplication.toPaymentId)
	{
		refundlist=delegator.findOne("Payment", [paymentId : eachpaymentApplication.toPaymentId], true);
		refundpaymentlist.addAll(refundlist);
		if(UtilValidate.isNotEmpty(eachpaymentApplication.toPaymentId)){
			finAccountTransList = delegator.findList("FinAccountTrans",EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS ,paymentId)  , null, null, null, false );
			finAccountTransList.each { finAccountTrans ->
				 finAccountTransId = finAccountTrans.finAccountTransId;
				 if(UtilValidate.isNotEmpty(finAccountTransId)){
					 finAccntTransSequenceList = delegator.findList("FinAccntTransSequence",EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS ,finAccountTransId)  , null, null, null, false );
					 finAccntTransSequenceList.each { finAccntTransSequence ->
						 transSeqId = finAccntTransSequence.transSequenceId;
						 transSequenceIdMap.put(eachpaymentApplication.toPaymentId,transSeqId);
					 }
				}
			}
		}
		context.transSequenceIdMap=transSequenceIdMap;
		}
}
if(UtilValidate.isNotEmpty(paymentId)){
	finAccountTransList = delegator.findList("FinAccountTrans",EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS ,paymentId)  , null, null, null, false );
	finAccountTransList.each { finAccountTrans ->
		 finAccountTransId = finAccountTrans.finAccountTransId;
		 if(UtilValidate.isNotEmpty(finAccountTransId)){
			 finAccntTransSequenceList = delegator.findList("FinAccntTransSequence",EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS ,finAccountTransId)  , null, null, null, false );
			 finAccntTransSequenceList.each { finAccntTransSequence ->
				 transSequenceId = finAccntTransSequence.transSequenceId;
				 Debug.log("transSequenceId==============="+transSequenceId);
				 context.transSequenceId=transSequenceId;
			 }
		}
	}
}
//Debug.log("=========refundpaymentlist=============="+refundpaymentlist.paymentId);
context.refundpaymentlist=refundpaymentlist;
invoiceIds = EntityUtil.getFieldListFromEntityList(paymentApplication, "invoiceId", true);
glAccntIdslist=[:];
invoiceIds.each{ eachinvoiceId ->
	invoice= delegator.findByPrimaryKey("Invoice", [invoiceId : eachinvoiceId]);
	invoiceList.put(eachinvoiceId,invoice);
	invoiceItems = invoice.getRelatedOrderBy("InvoiceItem", ["-productId","invoiceItemSeqId"]);
	invoiceitemMap.put(eachinvoiceId,invoiceItems);
	List conditionList=[];
	producctIds=[];
	producctIds=invoiceItems.productId;
	producctIds.each{eachproductId ->
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachinvoiceId));
	if(UtilValidate.isNotEmpty(invoiceItems.productId)){
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachproductId));
	}else{
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, null));
	}
	conditionList.add(EntityCondition.makeCondition("debitCreditFlag", EntityOperator.EQUALS, "D"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	acctngTransEntriesList=[];
	acctngTransEntriesList = delegator.findList("AcctgTransAndEntries", condition , null, null, null, false );
	condition=null;
	
	if(UtilValidate.isNotEmpty(acctngTransEntriesList)){
		acctngTransEntries = EntityUtil.getFirst(acctngTransEntriesList);
		glAccountId = acctngTransEntries.glAccountId;
		glAccntIdslist.put(eachinvoiceId,glAccountId);
	}
	}
}
context.invoiceitemMap=invoiceitemMap;
context.glAccntIdslist=glAccntIdslist;
context.payment=payment;
context.paymentApplicationMap=paymentApplicationMap;
context.invoiceList=invoiceList;


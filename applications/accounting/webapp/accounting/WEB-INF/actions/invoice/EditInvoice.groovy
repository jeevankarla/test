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
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

paymentId = parameters.paymentId;
invoiceId = parameters.get("invoiceId");
reportTypeFlag = context.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "debitNote"){
	shipmentId = parameters.shipmentId;
	conditionList = [];
	if(UtilValidate.isNotEmpty(parameters.orderId)){
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
	}else{
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	}
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderHeaders = delegator.findList("OrderHeader", condition, UtilMisc.toSet("orderId"), null, null, false);
	orderIds = EntityUtil.getFieldListFromEntityList(orderHeaders, "orderId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	invoices = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, UtilMisc.toSet("invoiceId"), null, null, false);
	invoiceIds = EntityUtil.getFieldListFromEntityList(invoices, "invoiceId", true);
	invoiceId = invoiceIds.get(0);
}
//for debit credit note
if(UtilValidate.isEmpty(invoiceId)){
	paymentApplication = delegator.findByAnd("PaymentApplication", [paymentId :paymentId]);
	if(UtilValidate.isNotEmpty(paymentApplication)){
		invoiceDetails = EntityUtil.getFirst(paymentApplication);
		if(UtilValidate.isNotEmpty(invoiceDetails)){
			invoiceId = invoiceDetails.invoiceId;
			if(UtilValidate.isEmpty(invoiceId)){
				Debug.logError("no InvoiceId","");
				context.errorMessage = "No Invoices Found....!";
				return;
			}
		}
	}
}
invoice = delegator.findByPrimaryKey("Invoice", [invoiceId : invoiceId]);
context.invoice = invoice;
currency = parameters.currency;        // allow the display of the invoice in the original currency, the default is to display the invoice in the default currency
BigDecimal conversionRate = new BigDecimal("1");
ZERO = BigDecimal.ZERO;
decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
if (invoice) {
	// each invoice of course has two billing addresses, but the one that is relevant for purchase invoices is the PAYMENT_LOCATION of the invoice
	// (ie Accounts Payable address for the supplier), while the right one for sales invoices is the BILLING_LOCATION (ie Accounts Receivable or
	// home of the customer.)
	invoiceType=delegator.findOne("InvoiceType",[invoiceTypeId : invoice.invoiceTypeId] , false);
	parentInvoiceType=delegator.findOne("InvoiceType",[invoiceTypeId : invoiceType.parentTypeId] , false);
	context.parentInvoiceType=parentInvoiceType;
	 if (("PURCHASE_INVOICE".equals(invoiceType.parentTypeId)) ||( "PURCHASE_INVOICE".equals(invoice.invoiceTypeId))) {
		billingAddress = InvoiceWorker.getSendFromAddress(invoice);
	} else {
		billingAddress = InvoiceWorker.getBillToAddress(invoice);
	}
	if (billingAddress) {
		context.billingAddress = billingAddress;
	}
	billingParty = InvoiceWorker.getBillToParty(invoice);
	Debug.logInfo(" billingParty==="+ billingParty,"");
	context.billingParty = billingParty;
	sendingParty = InvoiceWorker.getSendFromParty(invoice);
	context.sendingParty = sendingParty;
	if (("PURCHASE_INVOICE".equals(invoiceType.parentTypeId)) ||( "PURCHASE_INVOICE".equals(invoice.invoiceTypeId))) {
		 context.dispalyParty= sendingParty;
	}
	else{
		context.dispalyParty= billingParty;;
	}
   

	if (currency && !invoice.getString("currencyUomId").equals(currency)) {
		conversionRate = InvoiceWorker.getInvoiceCurrencyConversionRate(invoice);
		invoice.currencyUomId = currency;
		invoice.invoiceMessage = " converted from original with a rate of: " + conversionRate.setScale(8, rounding);
	}

	invoiceItems = invoice.getRelatedOrderBy("InvoiceItem", ["-productId","invoiceItemSeqId"]);
	invoiceItemsConv = FastList.newInstance();
	invoiceItemList = FastList.newInstance();
	vatTaxesByType = FastMap.newInstance();
	invoiceItems.each { invoiceItem ->
		invoiceItem.amount = invoiceItem.getBigDecimal("amount").multiply(conversionRate).setScale(decimals, rounding);
		invoiceItemsConv.add(invoiceItem);
		glAccountId = null;
		quantity = 0;
		unitPrice= 0;
		amount = 0;
		List conditionList=[];
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceItem.invoiceId));
		if(UtilValidate.isNotEmpty(invoiceItem.productId)){
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, invoiceItem.productId));
		}else{
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, null));
		}
		conditionList.add(EntityCondition.makeCondition("debitCreditFlag", EntityOperator.EQUALS, "D"));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		acctngTransEntriesList = delegator.findList("AcctgTransAndEntries", condition , null, null, null, false );
		if(UtilValidate.isNotEmpty(acctngTransEntriesList)){
			acctngTransEntries = EntityUtil.getFirst(acctngTransEntriesList);
			glAccountId = acctngTransEntries.glAccountId;
		}
		quantity = invoiceItem.quantity;
		unitPrice = invoiceItem.unitPrice;
		
		if(UtilValidate.isNotEmpty(unitPrice) && unitPrice != 0){
			amount = quantity*unitPrice;
		}else{
			amount = invoiceItem.amount;
		}
		
		invoiceItemMap = [:];
		invoiceItemMap["description"] = invoiceItem.description;
		invoiceItemMap["invoiceItemTypeId"] = invoiceItem.invoiceItemTypeId;
		invoiceItemMap["amount"] = amount;
		invoiceItemMap["glAccountId"] = glAccountId;
		if(UtilValidate.isNotEmpty(invoiceItemMap)){
			invoiceItemList.add(invoiceItemMap);
		}
		
		// get party tax id for VAT taxes: they are required in invoices by EU
		// also create a map with tax grand total amount by VAT tax: it is also required in invoices by UE
		taxRate = invoiceItem.getRelatedOne("TaxAuthorityRateProduct");
		if (taxRate && "VAT_TAX".equals(taxRate.taxAuthorityRateTypeId)) {
			taxInfos = EntityUtil.filterByDate(delegator.findByAnd("PartyTaxAuthInfo", [partyId : billingParty.partyId, taxAuthGeoId : taxRate.taxAuthGeoId, taxAuthPartyId : taxRate.taxAuthPartyId]), invoice.invoiceDate);
			taxInfo = EntityUtil.getFirst(taxInfos);
			if (taxInfo) {
				context.billingPartyTaxId = taxInfo.partyTaxId;
			}
			vatTaxesByTypeAmount = vatTaxesByType[taxRate.taxAuthorityRateSeqId];
			if (!vatTaxesByTypeAmount) {
				vatTaxesByTypeAmount = 0.0;
			}
			vatTaxesByType.put(taxRate.taxAuthorityRateSeqId, vatTaxesByTypeAmount + invoiceItem.amount);
		}
	}
	context.put("invoiceItemList",invoiceItemList);
	context.vatTaxesByType = vatTaxesByType;
	context.vatTaxIds = vatTaxesByType.keySet().asList();
	Debug.log("invoiceItemsConv==========="+invoiceItemsConv);
	context.invoiceItems = invoiceItemsConv;
	invoiceTotal = InvoiceWorker.getInvoiceTotal(invoice).multiply(conversionRate).setScale(decimals, rounding);
	invoiceNoTaxTotal = InvoiceWorker.getInvoiceNoTaxTotal(invoice).multiply(conversionRate).setScale(decimals, rounding);
	context.invoiceTotal = invoiceTotal;
	context.invoiceNoTaxTotal = invoiceNoTaxTotal;

				//*________________this snippet was added for adding Tax ID in invoice header if needed _________________

			   sendingTaxInfos = sendingParty.getRelated("PartyTaxAuthInfo");
			   billingTaxInfos = billingParty.getRelated("PartyTaxAuthInfo");
			   sendingPartyTaxId = null;
			   billingPartyTaxId = null;

			   if (billingAddress) {
				   sendingTaxInfos.eachWithIndex { sendingTaxInfo, i ->
					   if (sendingTaxInfo.taxAuthGeoId.equals(billingAddress.countryGeoId)) {
							sendingPartyTaxId = sendingTaxInfos[i-1].partyTaxId;
					   }
				   }
				   billingTaxInfos.eachWithIndex { billingTaxInfo, i ->
					   if (billingTaxInfo.taxAuthGeoId.equals(billingAddress.countryGeoId)) {
							billingPartyTaxId = billingTaxInfos[i-1].partyTaxId;
					   }
				   }
			   }
			   if (sendingPartyTaxId) {
				   context.sendingPartyTaxId = sendingPartyTaxId;
			   }
			   if (billingPartyTaxId && !context.billingPartyTaxId) {
				   context.billingPartyTaxId = billingPartyTaxId;
			   }
			   //________________this snippet was added for adding Tax ID in invoice header if needed _________________*/


	terms = invoice.getRelated("InvoiceTerm");
	context.terms = terms;

	//for payment
	printPaymentsList = FastList.newInstance();
	paymentAppls = delegator.findByAnd("PaymentApplication", [invoiceId : invoiceId]);
	if(UtilValidate.isNotEmpty(paymentAppls)){
		paymentAppls.each{ paymentDet ->
			paymentId = paymentDet.paymentId;
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
	context.payments = paymentAppls;
	
	orderItemBillings = delegator.findByAnd("OrderItemBilling", [invoiceId : invoiceId], ['orderId']);
	orders = new LinkedHashSet();
	orderItemBillings.each { orderIb ->
		orders.add(orderIb.orderId);
	}
	context.orders = orders;

	invoiceStatus = invoice.getRelatedOne("StatusItem");
	context.invoiceStatus = invoiceStatus;

	edit = parameters.editInvoice;
	if ("true".equalsIgnoreCase(edit)) {
		invoiceItemTypes = delegator.findList("InvoiceItemType", null, null, null, null, false);
		context.invoiceItemTypes = invoiceItemTypes;
		context.editInvoice = true;
	}

	// format the date
	if (invoice.invoiceDate) {
		invoiceDate = DateFormat.getDateInstance(DateFormat.LONG).format(invoice.invoiceDate);
		context.invoiceDate = invoiceDate;
	} else {
		context.invoiceDate = "N/A";
	}
}
context.paymentId = paymentId;







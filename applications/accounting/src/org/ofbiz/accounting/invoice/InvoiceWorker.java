/*******************************************************************************
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
 *******************************************************************************/
package org.ofbiz.accounting.invoice;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.minilang.method.entityops.NowDateToEnv.NowDateFactory;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * InvoiceWorker - Worker methods of invoices
 */
public class InvoiceWorker {

    public static String module = InvoiceWorker.class.getName();
    private static BigDecimal ZERO = BigDecimal.ZERO;
    private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
    private static int taxDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    private static int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");

    /**
     * Return the total amount of the invoice (including tax) using the the invoiceId as input.
     * @param delegator
     * @param invoiceId
     * @return
     */
    public static BigDecimal getInvoiceTotal(Delegator delegator, String invoiceId) {
        return getInvoiceTotal(delegator, invoiceId, Boolean.TRUE);
    }

    /**
     * Return the total amount of the invoice (including tax) using the the invoiceId as input.
     * with the ability to specify if the actual currency is required.
     * @param delegator
     * @param invoiceId
     * @param actualCurrency true: provide the actual currency of the invoice (could be different from the system currency)
     *                       false: if required convert the actual currency into the system currency.
     * @return
     */
    public static BigDecimal getInvoiceTotal(Delegator delegator, String invoiceId, Boolean actualCurrency) {
        if (delegator == null) {
            throw new IllegalArgumentException("Null delegator is not allowed in this method");
        }

        GenericValue invoice = null;
        try {
            invoice = delegator.findByPrimaryKey("Invoice", UtilMisc.toMap("invoiceId", invoiceId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Invoice", module);
        }

        if (invoice == null) {
            throw new IllegalArgumentException("The passed invoiceId [" +invoiceId + "] does not match an existing invoice");
        }

        return getInvoiceTotal(invoice, actualCurrency);
    }

    /**
     * Method to return the total amount of an invoice item i.e. quantity * amount
     * @param invoice GenericValue object of the Invoice
     * @return the invoice total as BigDecimal
     */
    public static BigDecimal getInvoiceItemTotal(GenericValue invoiceItem) {
        BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
        if (quantity == null) {
            quantity = BigDecimal.ONE;
        }
        BigDecimal amount = invoiceItem.getBigDecimal("amount");
        if (amount == null) {
            amount = ZERO;
        }
        return quantity.multiply(amount).setScale(decimals, rounding);
    }

    /** Method to get the taxable invoice item types as a List of invoiceItemTypeIds.  These are identified in Enumeration with enumTypeId TAXABLE_INV_ITM_TY. */
    public static List<String> getTaxableInvoiceItemTypeIds(Delegator delegator) throws GenericEntityException {
        List<String> typeIds = FastList.newInstance();
        List<GenericValue> invoiceItemTaxTypes = delegator.findByAndCache("Enumeration", UtilMisc.toMap("enumTypeId", "TAXABLE_INV_ITM_TY"));
        for (GenericValue invoiceItemTaxType : invoiceItemTaxTypes) {
            typeIds.add(invoiceItemTaxType.getString("enumId"));
        }
        return typeIds;
    }

    public static BigDecimal getInvoiceTaxTotal(GenericValue invoice) {
        BigDecimal taxTotal = ZERO;
        Map<String, Set<String>> taxAuthPartyAndGeos = InvoiceWorker.getInvoiceTaxAuthPartyAndGeos(invoice);
        for (Map.Entry<String, Set<String>> taxAuthPartyGeos : taxAuthPartyAndGeos.entrySet()) {
            String taxAuthPartyId = taxAuthPartyGeos.getKey();
            for (String taxAuthGeoId : taxAuthPartyGeos.getValue()) {
                taxTotal = taxTotal.add(InvoiceWorker.getInvoiceTaxTotalForTaxAuthPartyAndGeo(invoice, taxAuthPartyId, taxAuthGeoId));
            }
        }
        taxTotal = taxTotal.add(InvoiceWorker.getInvoiceUnattributedTaxTotal(invoice));
        return taxTotal;
    }

    public static BigDecimal getInvoiceNoTaxTotal(GenericValue invoice) {
        return getInvoiceTotal(invoice, Boolean.TRUE).subtract(getInvoiceTaxTotal(invoice));
    }

    /**
     * Method to return the total amount of an invoice
     * @param invoice GenericValue object of the Invoice
     * @return the invoice total as BigDecimal
     */
     public static BigDecimal getInvoiceTotal(GenericValue invoice) {
        return getInvoiceTotal(invoice, Boolean.TRUE);
    }

     /**
      *
      * Return the total amount of the invoice (including tax) using the the invoice GenericValue as input.
      * with the ability to specify if the actual currency is required.
      * @param invoice
      * @param actualCurrency true: provide the actual currency of the invoice (could be different from the system currency)
      *                       false: if required convert the actual currency into the system currency.
      * @return
      */
     public static BigDecimal getInvoiceTotal(GenericValue invoice, Boolean actualCurrency) {
        BigDecimal invoiceTotal = ZERO;
        BigDecimal invoiceTaxTotal = ZERO;
        invoiceTaxTotal = InvoiceWorker.getInvoiceTaxTotal(invoice);

        List<GenericValue> invoiceItems = null;
        try {
            invoiceItems = invoice.getRelated("InvoiceItem");
            invoiceItems = EntityUtil.filterByAnd(
                    invoiceItems, UtilMisc.toList(
                            EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, getTaxableInvoiceItemTypeIds(invoice.getDelegator()))
                    ));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting InvoiceItem list", module);
        }
        if (invoiceItems != null) {
            for (GenericValue invoiceItem : invoiceItems) {
                invoiceTotal = invoiceTotal.add(getInvoiceItemTotal(invoiceItem)).setScale(decimals,rounding);
            }
        }
        invoiceTotal = invoiceTotal.add(invoiceTaxTotal).setScale(decimals, rounding);
        if (UtilValidate.isNotEmpty(invoiceTotal) && !actualCurrency) {
            invoiceTotal = invoiceTotal.multiply(getInvoiceCurrencyConversionRate(invoice)).setScale(decimals,rounding);
        }
        return invoiceTotal;
    }

    /**
     * Method to obtain the bill to party for an invoice. Note that invoice.partyId is the bill to party.
     * @param invoice GenericValue object of the Invoice
     * @return GenericValue object of the Party
     */
    public static GenericValue getBillToParty(GenericValue invoice) {
        try {
            GenericValue billToParty = invoice.getRelatedOne("Party");
            if (billToParty != null) {
                return billToParty;
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting Party from Invoice", module);
        }

        // remaining code is the old method, which we leave here for compatibility purposes
        List<GenericValue> billToRoles = null;
        try {
            billToRoles = invoice.getRelated("InvoiceRole", UtilMisc.toMap("roleTypeId", "BILL_TO_CUSTOMER"),
                UtilMisc.toList("-datetimePerformed"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting InvoiceRole list", module);
        }

        if (billToRoles != null) {
            GenericValue role = EntityUtil.getFirst(billToRoles);
            GenericValue party = null;
            try {
                party = role.getRelatedOne("Party");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting Party from InvoiceRole", module);
            }
            if (party != null)
                return party;
        }
        return null;
    }

    /** Convenience method to obtain the bill from party for an invoice. Note that invoice.partyIdFrom is the bill from party. */
    public static GenericValue getBillFromParty(GenericValue invoice) {
        try {
            return invoice.getRelatedOne("FromParty");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting FromParty from Invoice", module);
        }
        return null;
    }

    /**
      * Method to obtain the send from party for an invoice
      * @param invoice GenericValue object of the Invoice
      * @return GenericValue object of the Party
      */
    public static GenericValue getSendFromParty(GenericValue invoice) {
        GenericValue billFromParty = getBillFromParty(invoice);
        if (billFromParty != null) {
            return billFromParty;
        }

        // remaining code is the old method, which we leave here for compatibility purposes
        List<GenericValue> sendFromRoles = null;
        try {
            sendFromRoles = invoice.getRelated("InvoiceRole", UtilMisc.toMap("roleTypeId", "BILL_FROM_VENDOR"),
                UtilMisc.toList("-datetimePerformed"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting InvoiceRole list", module);
        }

        if (sendFromRoles != null) {
            GenericValue role = EntityUtil.getFirst(sendFromRoles);
            GenericValue party = null;
            try {
                party = role.getRelatedOne("Party");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting Party from InvoiceRole", module);
            }
            if (party != null)
                return party;
        }
        return null;
    }

    /**
      * Method to obtain the billing address for an invoice
      * @param invoice GenericValue object of the Invoice
      * @return GenericValue object of the PostalAddress
      */
    public static GenericValue getBillToAddress(GenericValue invoice) {
        return getInvoiceAddressByType(invoice, "BILLING_LOCATION");
    }

    /**
      * Method to obtain the sending address for an invoice
      * @param invoice GenericValue object of the Invoice
      * @return GenericValue object of the PostalAddress
      */
    public static GenericValue getSendFromAddress(GenericValue invoice) {
        return getInvoiceAddressByType(invoice, "PAYMENT_LOCATION");
    }

    public static GenericValue getInvoiceAddressByType(GenericValue invoice, String contactMechPurposeTypeId) {
        Delegator delegator = invoice.getDelegator();
        List<GenericValue> locations = null;
        GenericValue invoiceType = null;
        // first try InvoiceContactMech to see if we can find the address needed
        try {
            locations = invoice.getRelated("InvoiceContactMech", UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId), null);
        } catch (GenericEntityException e) {
            Debug.logError("Touble getting InvoiceContactMech entity list", module);
        }

        if (UtilValidate.isEmpty(locations))    {
            // if no locations found get it from the PartyAndContactMech using the from and to party on the invoice
            String destinationPartyId = null;           
            try {
            	invoiceType=delegator.findOne("InvoiceType",UtilMisc.toMap("invoiceTypeId",invoice.getString("invoiceTypeId")) , false);
            }
            catch (Exception e) {
            	Debug.logError("Touble getting parentInvoiceId", module);
			}
            if ((invoice.getString("invoiceTypeId").equals("SALES_INVOICE") )|| (invoiceType.getString("parentTypeId").equals("SALES_INVOICE") ))
                destinationPartyId = invoice.getString("partyId");
            if ((invoice.getString("invoiceTypeId").equals("PURCHASE_INVOICE"))|| (invoiceType.getString("parentTypeId").equals("PURCHASE_INVOICE") ))
                destinationPartyId = invoice.getString("partyIdFrom");
            try {
                locations = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose",
                        UtilMisc.toMap("partyId", destinationPartyId, "contactMechPurposeTypeId", contactMechPurposeTypeId)));
            } catch (GenericEntityException e) {
                Debug.logError("Trouble getting contact party purpose list", module);
            }
            //if still not found get it from the general location
            if (UtilValidate.isEmpty(locations))    {
                try {
                    locations = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose",
                            UtilMisc.toMap("partyId", destinationPartyId, "contactMechPurposeTypeId", "GENERAL_LOCATION")));
                } catch (GenericEntityException e) {
                    Debug.logError("Trouble getting contact party purpose list", module);
                }
            }
        }

        // now return the first PostalAddress from the locations
        GenericValue postalAddress = null;
        GenericValue contactMech = null;
        if (UtilValidate.isNotEmpty(locations)) {
            try {
                contactMech = locations.get(0).getRelatedOne("ContactMech");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting Contact for contactMechId: " + locations.get(0).getString("contactMechId"), module);
            }

            if (contactMech != null && contactMech.getString("contactMechTypeId").equals("POSTAL_ADDRESS"))    {
                try {
                    postalAddress = contactMech.getRelatedOne("PostalAddress");
                    return postalAddress;
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Trouble getting PostalAddress for contactMechId: " + contactMech.getString("contactMechId"), module);
                }
            }
        }
        return contactMech;
    }

    /**
     * Method to return the total amount of an invoice which is not yet applied to a payment
     * @param invoice GenericValue object of the Invoice
     * @return the invoice total as BigDecimal
     */
    public static BigDecimal getInvoiceNotApplied(Delegator delegator, String invoiceId, Boolean actualCurrency) {
        return InvoiceWorker.getInvoiceTotal(delegator, invoiceId, actualCurrency).subtract(getInvoiceApplied(delegator, invoiceId,  UtilDateTime.nowTimestamp(), actualCurrency));
    }
    public static BigDecimal getInvoiceNotApplied(Delegator delegator, String invoiceId) {
        return InvoiceWorker.getInvoiceTotal(delegator, invoiceId).subtract(getInvoiceApplied(delegator, invoiceId));
    }
    public static BigDecimal getInvoiceNotApplied(GenericValue invoice) {
        return InvoiceWorker.getInvoiceTotal(invoice, Boolean.TRUE).subtract(getInvoiceApplied(invoice));
    }
    public static BigDecimal getInvoiceNotApplied(GenericValue invoice, Boolean actualCurrency) {
        return InvoiceWorker.getInvoiceTotal(invoice, actualCurrency).subtract(getInvoiceApplied(invoice, actualCurrency));
    }
    /**
     * Returns amount not applied (i.e., still outstanding) of an invoice at an asOfDate, based on Payment.effectiveDate <= asOfDateTime
     *
     * @param invoice
     * @param asOfDateTime
     * @return
     */
    public static BigDecimal getInvoiceNotApplied(GenericValue invoice, Timestamp asOfDateTime) {
        return InvoiceWorker.getInvoiceTotal(invoice, Boolean.TRUE).subtract(getInvoiceApplied(invoice, asOfDateTime));
    }


    /**
     * Method to return the total amount of an invoice which is applied to a payment
     * @param invoice GenericValue object of the Invoice
     * @return the invoice total as BigDecimal
     */
    public static BigDecimal getInvoiceApplied(Delegator delegator, String invoiceId) {
        return getInvoiceApplied(delegator, invoiceId, UtilDateTime.nowTimestamp(), Boolean.TRUE);
    }

    /**
     * Returns amount applied to invoice before an asOfDateTime, based on Payment.effectiveDate <= asOfDateTime
     *
     * @param delegator
     * @param invoiceId
     * @param asOfDateTime - a Timestamp
     * @return
     */
    public static BigDecimal getInvoiceApplied(Delegator delegator, String invoiceId, Timestamp asOfDateTime, Boolean actualCurrency) {
        if (delegator == null) {
            throw new IllegalArgumentException("Null delegator is not allowed in this method");
        }

        BigDecimal invoiceApplied = ZERO;
        List<GenericValue> paymentApplications = null;

        // lookup payment applications which took place before the asOfDateTime for this invoice
        EntityConditionList<EntityExpr> dateCondition = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("effectiveDate", EntityOperator.EQUALS, null),
                EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, asOfDateTime)), EntityOperator.OR);
        EntityConditionList<EntityCondition> conditions = EntityCondition.makeCondition(UtilMisc.toList(
                dateCondition,
                EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId)),
                EntityOperator.AND);

        try {
            paymentApplications = delegator.findList("PaymentAndApplication", conditions, null, UtilMisc.toList("effectiveDate"), null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting paymentApplicationlist", module);
        }
        if (paymentApplications != null) {
            for (GenericValue paymentApplication : paymentApplications) {
                invoiceApplied = invoiceApplied.add(paymentApplication.getBigDecimal("amountApplied")).setScale(decimals,rounding);
            }
        }
        if (UtilValidate.isNotEmpty(invoiceApplied) && !actualCurrency) {
            invoiceApplied = invoiceApplied.multiply(getInvoiceCurrencyConversionRate(delegator, invoiceId)).setScale(decimals,rounding);
        }
        return invoiceApplied;
    }
    /**
     * Method to return the total amount of an invoice which is applied to a payment
     * @param invoice GenericValue object of the Invoice
     * @return the applied total as BigDecimal
     */
    public static BigDecimal getInvoiceApplied(GenericValue invoice) {
        return getInvoiceApplied(invoice, UtilDateTime.nowTimestamp());
    }

    /**
     * @param delegator
     * @param invoiceId
     * @param invoiceItemSeqId
     * @return
     */
    public static BigDecimal getInvoiceApplied(GenericValue invoice, Boolean actualCurrency) {
        return getInvoiceApplied(invoice.getDelegator(), invoice.getString("invoiceId"), UtilDateTime.nowTimestamp(), actualCurrency);
    }
    public static BigDecimal getInvoiceApplied(GenericValue invoice, Timestamp asOfDateTime) {
        return getInvoiceApplied(invoice.getDelegator(), invoice.getString("invoiceId"), asOfDateTime, Boolean.TRUE);
    }
    /**
     * Method to return the amount of an invoiceItem which is applied to a payment
     * @param invoice GenericValue object of the Invoice
     * @return the invoice total as BigDecimal
     */
    public static BigDecimal getInvoiceItemApplied(Delegator delegator, String invoiceId, String invoiceItemSeqId) {
        if (delegator == null) {
            throw new IllegalArgumentException("Null delegator is not allowed in this method");
        }

        GenericValue invoiceItem = null;
        try {
            invoiceItem = delegator.findByPrimaryKey("Invoice", UtilMisc.toMap("invoiceId", invoiceId,"invoiceItemSeqId", invoiceItemSeqId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting InvoiceItem", module);
        }

        if (invoiceItem == null) {
            throw new IllegalArgumentException("The invoiceId/itemSeqId passed does not match an existing invoiceItem");
        }

        return getInvoiceItemApplied(invoiceItem);
    }

    /**
     * Method to return the total amount of an invoiceItem which is applied to a payment
     * @param invoice GenericValue object of the Invoice
     * @return the applied total as BigDecimal
     */
    public static BigDecimal getInvoiceItemApplied(GenericValue invoiceItem) {
        BigDecimal invoiceItemApplied = ZERO;
        List<GenericValue> paymentApplications = null;
        try {
            paymentApplications = invoiceItem.getRelated("PaymentApplication");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting paymentApplicationlist", module);
        }
        if (paymentApplications != null) {
            for (GenericValue paymentApplication : paymentApplications) {
                invoiceItemApplied = invoiceItemApplied.add(paymentApplication.getBigDecimal("amountApplied")).setScale(decimals,rounding);
            }
        }
        return invoiceItemApplied;
    }
    public static BigDecimal getInvoiceCurrencyConversionRate(GenericValue invoice) {
        BigDecimal conversionRate = null;
        Delegator delegator = invoice.getDelegator();
        String otherCurrencyUomId = null;
        // find the organization party currencyUomId which different from the invoice currency
        try {
            GenericValue party  = delegator.findByPrimaryKey("PartyAcctgPreference", UtilMisc.toMap("partyId", invoice.getString("partyIdFrom")));
            if (UtilValidate.isEmpty(party) || party.getString("baseCurrencyUomId").equals(invoice.getString("currencyUomId"))) {
                party  = delegator.findByPrimaryKey("PartyAcctgPreference", UtilMisc.toMap("partyId", invoice.getString("partyId")));
            }
            if (UtilValidate.isNotEmpty(party) && party.getString("baseCurrencyUomId") != null) {
                otherCurrencyUomId = party.getString("baseCurrencyUomId");
            } else {
                otherCurrencyUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default");
            }
            if (otherCurrencyUomId == null) {
                otherCurrencyUomId = "INR"; // final default
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting database records....", module);
        }
        if (invoice.getString("currencyUomId").equals(otherCurrencyUomId)) {
            return BigDecimal.ONE;  // organization party has the same currency so conversion not required.
        }

        try {
            // check if the invoice is posted and get the conversion from there
            List<GenericValue> acctgTransEntries = invoice.getRelated("AcctgTrans");
            if (UtilValidate.isNotEmpty(acctgTransEntries)) {
                GenericValue acctgTransEntry = ((GenericValue) acctgTransEntries.get(0)).getRelated("AcctgTransEntry").get(0);
                conversionRate = acctgTransEntry.getBigDecimal("amount").divide(acctgTransEntry.getBigDecimal("origAmount"), new MathContext(100)).setScale(decimals,rounding);
            }
            // check if a payment is applied and use the currency conversion from there
            if (UtilValidate.isEmpty(conversionRate)) {
                List<GenericValue> paymentAppls = invoice.getRelated("PaymentApplication");
                for (GenericValue paymentAppl : paymentAppls) {
                    GenericValue payment = paymentAppl.getRelatedOne("Payment");
                    if (UtilValidate.isNotEmpty(payment.getBigDecimal("actualCurrencyAmount"))) {
                        if (UtilValidate.isEmpty(conversionRate)) {
                            conversionRate = payment.getBigDecimal("amount").divide(payment.getBigDecimal("actualCurrencyAmount"),new MathContext(100)).setScale(decimals,rounding);
                        } else {
                            conversionRate = conversionRate.add(payment.getBigDecimal("amount").divide(payment.getBigDecimal("actualCurrencyAmount"),new MathContext(100))).divide(new BigDecimal("2"),new MathContext(100)).setScale(decimals,rounding);
                        }
                    }
                }
            }
            // use the dated conversion entity
            if (UtilValidate.isEmpty(conversionRate)) {
                List<GenericValue> rates = EntityUtil.filterByDate(delegator.findByAnd("UomConversionDated", UtilMisc.toMap("uomIdTo", invoice.getString("currencyUomId"), "uomId", otherCurrencyUomId)), invoice.getTimestamp("invoiceDate"));
                if (UtilValidate.isNotEmpty(rates)) {
                    conversionRate = (BigDecimal.ONE).divide(((GenericValue) rates.get(0)).getBigDecimal("conversionFactor"), new MathContext(100)).setScale(decimals,rounding);
                } else {
                    Debug.logError("Could not find conversionrate for invoice: " + invoice.getString("invoiceId"), module);
                    return new BigDecimal("1");
                }
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting database records....", module);
        }
        return(conversionRate);
    }

    public static BigDecimal getInvoiceCurrencyConversionRate(Delegator delegator, String invoiceId) {
        if (delegator == null) {
            throw new IllegalArgumentException("Null delegator is not allowed in this method");
        }

        GenericValue invoice = null;
        try {
            invoice = delegator.findByPrimaryKey("Invoice", UtilMisc.toMap("invoiceId", invoiceId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Invoice", module);
        }

        if (invoice == null) {
            throw new IllegalArgumentException("The invoiceId passed does not match an existing invoice");
        }

        return getInvoiceCurrencyConversionRate(invoice);
    }

    /**
     * Return a list of taxes separated by Geo and party and return the tax grand total
     * @param invoice Generic Value
     * @return  Map: taxByTaxAuthGeoAndPartyList(List) and taxGrandTotal(BigDecimal)
     */
    @Deprecated
    public static Map<String, Object> getInvoiceTaxByTaxAuthGeoAndParty(GenericValue invoice) {
        BigDecimal taxGrandTotal = ZERO;
        List<Map<String, Object>> taxByTaxAuthGeoAndPartyList = FastList.newInstance();
        List<GenericValue> invoiceItems = null;
        if (UtilValidate.isNotEmpty(invoice)) {
            try {
                invoiceItems = invoice.getRelated("InvoiceItem");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting InvoiceItem list", module);
            }
            if ("SALES_INVOICE".equals(invoice.getString("invoiceTypeId"))) {
                invoiceItems = EntityUtil.filterByOr(
                        invoiceItems, UtilMisc.toList(
                                EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_SALES_TAX"),
                                EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "ITM_SALES_TAX")));
            } else if (("PURCHASE_INVOICE".equals(invoice.getString("invoiceTypeId")))) {
                invoiceItems = EntityUtil.filterByOr(
                        invoiceItems, UtilMisc.toList(
                                EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "PINV_SALES_TAX"),
                                EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "PITM_SALES_TAX")));
            } else {
                invoiceItems = null;
            }
            if (UtilValidate.isNotEmpty(invoiceItems)) {
                invoiceItems = EntityUtil.orderBy(invoiceItems, UtilMisc.toList("taxAuthGeoId","taxAuthPartyId"));
                // get the list of all distinct taxAuthGeoId and taxAuthPartyId. It is for getting the number of taxAuthGeoId and taxAuthPartyId in invoiceItems.
                List<String> distinctTaxAuthGeoIdList = EntityUtil.getFieldListFromEntityList(invoiceItems, "taxAuthGeoId", true);
                List<String> distinctTaxAuthPartyIdList = EntityUtil.getFieldListFromEntityList(invoiceItems, "taxAuthPartyId", true);
                for (String taxAuthGeoId : distinctTaxAuthGeoIdList ) {
                    for (String taxAuthPartyId : distinctTaxAuthPartyIdList) {
                        //get all records for invoices filtered by taxAuthGeoId and taxAurhPartyId
                        List<GenericValue> invoiceItemsByTaxAuthGeoAndPartyIds = EntityUtil.filterByAnd(invoiceItems, UtilMisc.toMap("taxAuthGeoId", taxAuthGeoId, "taxAuthPartyId", taxAuthPartyId));
                        if (UtilValidate.isNotEmpty(invoiceItemsByTaxAuthGeoAndPartyIds)) {
                            BigDecimal totalAmount = ZERO;
                            //Now for each invoiceItem record get and add amount.
                            for (GenericValue invoiceItem : invoiceItemsByTaxAuthGeoAndPartyIds) {
                                BigDecimal amount = invoiceItem.getBigDecimal("amount");
                                if (amount == null) {
                                    amount = ZERO;
                                }
                                totalAmount = totalAmount.add(amount).setScale(taxDecimals, taxRounding);
                            }
                            totalAmount = totalAmount.setScale(UtilNumber.getBigDecimalScale("salestax.calc.decimals"), UtilNumber.getBigDecimalRoundingMode("salestax.rounding"));
                            taxByTaxAuthGeoAndPartyList.add(UtilMisc.<String, Object>toMap("taxAuthPartyId", taxAuthPartyId, "taxAuthGeoId", taxAuthGeoId, "totalAmount", totalAmount));
                            taxGrandTotal = taxGrandTotal.add(totalAmount);
                        }
                    }
                }
            }
        }
        Map<String, Object> result = FastMap.newInstance();
        result.put("taxByTaxAuthGeoAndPartyList", taxByTaxAuthGeoAndPartyList);
        result.put("taxGrandTotal", taxGrandTotal);
        return result;
    }

    /**
     * Returns a List of the TaxAuthority Party and Geos for the given Invoice.
     * @param invoice GenericValue object representing the Invoice
     * @return A Map containing the each taxAuthPartyId as a key and a Set of taxAuthGeoIds for that taxAuthPartyId as the values.  Note this method
     *         will not account for tax lines that do not contain a taxAuthPartyId
     */
    public static Map<String, Set<String>> getInvoiceTaxAuthPartyAndGeos (GenericValue invoice) {
        Map<String, Set<String>> result = FastMap.newInstance();

        if (invoice == null)
           throw new IllegalArgumentException("Invoice cannot be null.");
        List<GenericValue> invoiceTaxItems = null;
        try {
            Delegator delegator = invoice.getDelegator();
            EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("invoiceId", invoice.getString("invoiceId")),
                    EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, getTaxableInvoiceItemTypeIds(delegator))),
                    EntityOperator.AND);
            invoiceTaxItems = delegator.findList("InvoiceItem", condition, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting InvoiceItem list", module);
            return null;
        }
        if (invoiceTaxItems != null) {
            for (GenericValue invoiceItem : invoiceTaxItems) {
                String taxAuthPartyId = invoiceItem.getString("taxAuthPartyId");
                String taxAuthGeoId = invoiceItem.getString("taxAuthGeoId");
                if (UtilValidate.isNotEmpty(taxAuthPartyId)) {
                    if (!result.containsKey(taxAuthPartyId)) {
                        Set<String> taxAuthGeos = FastSet.newInstance();
                        taxAuthGeos.add(taxAuthGeoId);
                        result.put(taxAuthPartyId, taxAuthGeos);
                    } else {
                        Set<String> taxAuthGeos = result.get(taxAuthPartyId);
                        taxAuthGeos.add(taxAuthGeoId);
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param invoice GenericValue object representing the invoice
     * @param taxAuthPartyId
     * @param taxAuthGeoId
     * @return The invoice tax total for a given tax authority and geo location
     */
    public static BigDecimal getInvoiceTaxTotalForTaxAuthPartyAndGeo(GenericValue invoice, String taxAuthPartyId, String taxAuthGeoId) {
        List<GenericValue> invoiceTaxItems = null;
        try {
            Delegator delegator = invoice.getDelegator();
            EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("invoiceId", invoice.getString("invoiceId")),
                    EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, getTaxableInvoiceItemTypeIds(delegator)),
                    EntityCondition.makeCondition("taxAuthPartyId", taxAuthPartyId),
                    EntityCondition.makeCondition("taxAuthGeoId", taxAuthGeoId)),
                    EntityOperator.AND);
            invoiceTaxItems = delegator.findList("InvoiceItem", condition, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting InvoiceItem list", module);
            return null;
        }
       return getTaxTotalForInvoiceItems(invoiceTaxItems);
    }

    /** Returns the invoice tax total for unattributed tax items, that is items which have no taxAuthPartyId value
     * @param invoice GenericValue object representing the invoice
     * @return
     */
    public static BigDecimal getInvoiceUnattributedTaxTotal(GenericValue invoice) {
         List<GenericValue> invoiceTaxItems = null;
         try {
             Delegator delegator = invoice.getDelegator();
             EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
                     EntityCondition.makeCondition("invoiceId", invoice.getString("invoiceId")),
                     EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, getTaxableInvoiceItemTypeIds(delegator)),
                     EntityCondition.makeCondition("taxAuthPartyId", null)),
                     EntityOperator.AND);
             invoiceTaxItems = delegator.findList("InvoiceItem", condition, null, null, null, false);
         } catch (GenericEntityException e) {
             Debug.logError(e, "Trouble getting InvoiceItem list", module);
             return null;
         }
        return getTaxTotalForInvoiceItems(invoiceTaxItems);
    }

    /** Returns the tax total for a given list of tax typed InvoiceItem records
     * @param taxInvoiceItems
     * @return
     */
    private static BigDecimal getTaxTotalForInvoiceItems(List<GenericValue> taxInvoiceItems) {
        if (taxInvoiceItems == null) {
            return ZERO;
        }
        BigDecimal taxTotal = ZERO;
        for (GenericValue taxInvoiceItem : taxInvoiceItems) {
            BigDecimal amount = taxInvoiceItem.getBigDecimal("amount");
            if (amount == null) {
                amount = ZERO;
            }
            BigDecimal quantity = taxInvoiceItem.getBigDecimal("quantity");
            if (quantity == null) {
                quantity = BigDecimal.ONE;
            }
            amount = amount.multiply(quantity);
            amount = amount.setScale(taxDecimals, taxRounding);
            taxTotal = taxTotal.add(amount);
        }
        return taxTotal.setScale(decimals, rounding);
    }
    

	public static Map<String, Object> createTaxInvoice(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    String errorMsg = "createStatutoryInvoice failed ";
	    Map<String, Object> serviceResults = ServiceUtil.returnError(errorMsg, null, null, null);  
		String invoiceId = (String) context.get("invoiceId");
		String invoiceItemTypeId=(String)context.get("invoiceItemTypeId");
		String taxAuthPartyId =(String)context.get("taxAuthPartyId");
		BigDecimal amount=(BigDecimal)context.get("amount");
		BigDecimal quantity=BigDecimal.ONE;
		String taxPayingPartyId= null;
		if(context.get("quantity") != null){
		quantity=new BigDecimal((String)context.get("quantity"));
		}		
		String invoiceItemSeqId=(String)context.get("invoiceItemSeqId");		
	    Map input = UtilMisc.toMap("userLogin", context.get("userLogin"));
	    Map taxInvoiceItemAssocTypeMap = UtilMisc.toMap("userLogin", context.get("userLogin"));
	    taxInvoiceItemAssocTypeMap.put("invoiceIdFrom",invoiceId);
	    taxInvoiceItemAssocTypeMap.put("invoiceItemSeqIdFrom",invoiceItemSeqId);
	    taxInvoiceItemAssocTypeMap.put("invoiceItemAssocTypeId",invoiceItemTypeId);
	    taxInvoiceItemAssocTypeMap.put("fromDate",UtilDateTime.nowTimestamp());
	    taxInvoiceItemAssocTypeMap.put("quantity",quantity);
	    
		try {
			GenericValue invoiceRow = delegator.findOne("Invoice",
					UtilMisc.toMap("invoiceId", invoiceId), false);
			if (invoiceRow != null) {
				String partyId=invoiceRow.getString("partyId");
				String partyIdFrom=invoiceRow.getString("partyIdFrom");
				String invoiceTypeId=invoiceRow.getString("invoiceTypeId");
				GenericValue invoiceTypeRow = delegator.findOne("InvoiceType",
						UtilMisc.toMap("invoiceTypeId", invoiceTypeId),
						false);
				if (invoiceTypeId.equals("STATUTORY_OUT")  || invoiceTypeId.equals("STATUTORY_IN")) {	
					//Breaking the recursion of creating Tax invoices ...if the party id is TaxAuthority Party then break the loop. 
						Debug.logInfo("don't create invoice", module);
						serviceResults = ServiceUtil.returnSuccess();
						return serviceResults;			
				
				}				
					// trying to get taxAuthPartyId for the InvoiceItemType
				// based on the invoiceItemTypeId
				GenericValue invoiceItemTypeRow = delegator.findOne(
						"InvoiceItemType",
						UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId),
						false);				
				input.put("statusId", "INVOICE_IN_PROCESS");
				String invoiceParentTypeId=invoiceTypeRow.getString("parentTypeId");
				Debug.logInfo("create invoice invoiceTypeRow in loop"+invoiceParentTypeId, module);
				if(invoiceParentTypeId.equals("SALES_INVOICE")){
					taxPayingPartyId=partyId;
					input.put("partyId",partyIdFrom);
					input.put("invoiceTypeId", "STATUTORY_OUT");
					input.put("partyIdFrom",taxAuthPartyId);
					taxInvoiceItemAssocTypeMap.put("partyIdFrom",taxPayingPartyId);
					taxInvoiceItemAssocTypeMap.put("partyIdTo",taxAuthPartyId);
				}
				if(invoiceParentTypeId.equals("PURCHASE_INVOICE")){
					taxPayingPartyId=partyIdFrom;
					input.put("partyId",taxAuthPartyId);
					input.put("invoiceTypeId", "STATUTORY_IN");
					taxInvoiceItemAssocTypeMap.put("partyIdFrom",taxAuthPartyId);
					taxInvoiceItemAssocTypeMap.put("partyIdTo",taxPayingPartyId);
					input.put("partyIdFrom",partyId);
					if(amount.intValue() < 0){
						input.put("partyId",partyId);
						input.put("invoiceTypeId", "STATUTORY_OUT");
						input.put("partyIdFrom",taxAuthPartyId);
						amount=amount.negate();
						taxInvoiceItemAssocTypeMap.put("partyIdFrom",taxPayingPartyId);
						taxInvoiceItemAssocTypeMap.put("partyIdTo",taxAuthPartyId);
						
					}
				}
				
				// getting Tax dueDay of the Month from TaxAuthParty table
				List<GenericValue> taxAuthPartyDuedates = delegator
						.findByAnd("TaxAuthority", UtilMisc.toMap(
								"taxAuthPartyId",taxAuthPartyId));
				// Convert attributes to the corresponding data types
				Locale locale = null;
				TimeZone timeZone = null;
				locale = Locale.getDefault();
				timeZone = TimeZone.getDefault();
				// to get Current month End
				Timestamp monthEnd = UtilDateTime.getMonthEnd(UtilDateTime.monthBegin(), timeZone, locale);
				Timestamp dueDate = UtilDateTime.getDayStart(monthEnd,(taxAuthPartyDuedates.get(0).getLong("taxDueDayOfMonth")).intValue());
				input.put("dueDate", dueDate);
				input.put("currencyUomId",invoiceRow.getString("currencyUomId"));
				
				serviceResults = dispatcher.runSync("createInvoice", input);
				if (ServiceUtil.isError(serviceResults)) {
					return ServiceUtil.returnError(errorMsg, null, null,
							serviceResults);
				}
				String taxInvoiceId = (String) serviceResults.get("invoiceId");
				
				taxInvoiceItemAssocTypeMap.put("invoiceIdTo",taxInvoiceId);
				taxInvoiceItemAssocTypeMap.put("amount",amount);
				
				Map taxInputItemMap = UtilMisc.toMap("userLogin",
						context.get("userLogin"), "invoiceId", taxInvoiceId);
				taxInputItemMap.put("invoiceItemTypeId", invoiceItemTypeId);
				taxInputItemMap.put("amount", amount);
				taxInputItemMap.put("quantity", quantity);
				taxInputItemMap.put("parentInvoiceId", invoiceId);
				taxInputItemMap.put("parentInvoiceItemSeqId", invoiceItemSeqId);
				taxInputItemMap.put("quantity", quantity);
				taxInputItemMap.put("description",invoiceItemTypeRow.getString("description"));
				serviceResults = dispatcher.runSync("createInvoiceItem",taxInputItemMap);
				if (ServiceUtil.isError(serviceResults)) {
					return ServiceUtil.returnError(errorMsg, null, null,
							serviceResults);
				}
				String taxInvoiceinvoiceItemSeqId=(String)serviceResults.get("invoiceItemSeqId");				
				Map taxInvoiceStatusMap = UtilMisc.toMap("userLogin", context.get("approverUserLogin"));
				 taxInvoiceStatusMap.put("invoiceId", taxInvoiceId);
				 taxInvoiceStatusMap.put("statusId", "INVOICE_APPROVED");
				 serviceResults = dispatcher.runSync("setInvoiceStatus",taxInvoiceStatusMap);				
					if (ServiceUtil.isError(serviceResults)) {
						return ServiceUtil.returnError(errorMsg, null, null,
								serviceResults);
					}
				taxInvoiceItemAssocTypeMap.put("invoiceItemSeqIdTo",taxInvoiceinvoiceItemSeqId);
				serviceResults = dispatcher.runSync("createInvoiceItemAssoc",taxInvoiceItemAssocTypeMap);				
				if (ServiceUtil.isError(serviceResults)) {
					return ServiceUtil.returnError(errorMsg, null, null,
							serviceResults);
				}
				 
			}

		

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil
					.returnError("Unable to create statutory Invoice record");
		} catch (GenericServiceException e) {
			Debug.logError(e, errorMsg + e.getMessage(), module);
			return ServiceUtil.returnError(errorMsg + e.getMessage());
		}
		
		return serviceResults;
	}
	
	public static Map<String, Object> createInvoiceAttribute(DispatchContext dctx, Map<String, Object> context) {
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();  
		String invoiceId = (String) context.get("invoiceId");	
        String invoiceAttrName = (String)context.get("invoiceAttrName");
        String invoiceAttrValue = (String)context.get("invoiceAttrValue");
	    String errorMsg = "createInvoiceAttribute failed [" + invoiceId + ";" + invoiceAttrValue + "]: "; 
	    
        // create the invoice attribute
        GenericValue attr = delegator.makeValue("InvoiceAttribute");
        attr.set("invoiceId", invoiceId);
        attr.set("attrName", invoiceAttrName);
        attr.set("attrValue", invoiceAttrValue);
        try {
            delegator.create(attr);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(errorMsg + e.getMessage());
        }
	    return ServiceUtil.returnSuccess();	    
	}
	
	public static Map<String, Object> ensureInvoiceNotAlreadyExists(DispatchContext dctx, Map<String, Object> context) 
	throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String invoiceTypeId = (String) context.get("invoiceTypeId");	
		String partyId = (String) context.get("partyId");			
		String partyIdFrom = (String) context.get("partyIdFrom");					
		String invoiceAttrName = (String) context.get("invoiceAttrName");
		String invoiceAttrValue = (String) context.get("invoiceAttrValue");	
		String infoMsg = "[" + invoiceTypeId + ",[" + partyId + "<-->" + partyIdFrom + "]," 
			+ invoiceAttrName + "=" + invoiceAttrValue + "]";
Debug.logInfo("==========>ensureInvoiceAlreadyNotExists=" + infoMsg, module);   				
		try {
			GenericValue invoiceTypeRow = delegator.findOne("InvoiceType",
					UtilMisc.toMap("invoiceTypeId", invoiceTypeId), false);
			if (invoiceTypeRow == null) {
	        	return ServiceUtil.returnError("Unable to retrive invoiceTypeId '" + invoiceTypeId + "'");				
			}
			List conditionList = UtilMisc.toList(
					EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceTypeId));			
            if ((invoiceTypeRow.getString("invoiceTypeId").equals("SALES_INVOICE") )|| 
            		(invoiceTypeRow.getString("parentTypeId").equals("SALES_INVOICE") )) {
        		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));				
            }
            if ((invoiceTypeRow.getString("invoiceTypeId").equals("PURCHASE_INVOICE"))|| 
            	(invoiceTypeRow.getString("parentTypeId").equals("PURCHASE_INVOICE") )) {
        		conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));							
            }
            conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));				
            conditionList.add(EntityCondition.makeCondition("invoiceAttrName", EntityOperator.EQUALS, invoiceAttrName));		
            conditionList.add(EntityCondition.makeCondition("invoiceAttrValue", EntityOperator.EQUALS, invoiceAttrValue));
            EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
            List<GenericValue> invoices = delegator.findList("InvoiceAndAttribute", condition, null, null, null, false);
            if (!invoices.isEmpty()) {					
    			return ServiceUtil.returnError("Invoice for this period already exists. Please cancel earlier invoice and try again: "  
    					+ infoMsg);	
            }
		}
		catch (GenericEntityException e) {
            String errMsg = "ensureInvoiceNotAlreadyExists check failed: " + e.getMessage();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        return ServiceUtil.returnSuccess();
	}		
	
	public static Map<String, Object> fetchTimePeriodDetails(DispatchContext dctx, Map<String, Object> context) 
	throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String errorMsg = "fetchTimePeriodDetails failed";
		String timePeriodId = (String) context.get("timePeriodId");	
       
    	GenericValue periodRow = delegator.findOne("CustomTimePeriod", UtilMisc.toMap(
    			"customTimePeriodId", timePeriodId), false);	
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = TimeZone.getDefault();// ::TODO    	
        Date fromDate = periodRow.getDate("fromDate");
        Timestamp from = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fromDate), timeZone, locale); 
        Date thruDate = periodRow.getDate("thruDate");
        Timestamp thru = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(thruDate), timeZone, locale); 
        context.put("timePeriodStart", from);
        context.put("timePeriodEnd", thru); 
        context.put("periodTypeId", periodRow.getString("periodTypeId"));
        context.put("periodName", periodRow.getString("periodName"));        
        return ServiceUtil.returnSuccess(); 
	}	
}

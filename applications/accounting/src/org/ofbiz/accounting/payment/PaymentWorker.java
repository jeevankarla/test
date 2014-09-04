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
package org.ofbiz.accounting.payment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.ServletRequest;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.AccountingException;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;	
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.product.ProductEvents;



/**
 * Worker methods for Payments
 */
public class PaymentWorker {

    public static final String module = PaymentWorker.class.getName();
    private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");

    // to be able to use in minilanguage where Boolean cannot be used
    public static List<Map<String, GenericValue>> getPartyPaymentMethodValueMaps(Delegator delegator, String partyId) {
        return(getPartyPaymentMethodValueMaps(delegator, partyId, false));
    }

    public static List<Map<String, GenericValue>> getPartyPaymentMethodValueMaps(Delegator delegator, String partyId, Boolean showOld) {
        List<Map<String, GenericValue>> paymentMethodValueMaps = FastList.newInstance();
        try {
            List<GenericValue> paymentMethods = delegator.findByAnd("PaymentMethod", UtilMisc.toMap("partyId", partyId));

            if (!showOld) paymentMethods = EntityUtil.filterByDate(paymentMethods, true);

            for (GenericValue paymentMethod : paymentMethods) {
                Map<String, GenericValue> valueMap = FastMap.newInstance();

                paymentMethodValueMaps.add(valueMap);
                valueMap.put("paymentMethod", paymentMethod);
                if ("CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {
                    GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");
                    if (creditCard != null) valueMap.put("creditCard", creditCard);
                } else if ("GIFT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {
                    GenericValue giftCard = paymentMethod.getRelatedOne("GiftCard");
                    if (giftCard != null) valueMap.put("giftCard", giftCard);
                } else if ("EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {
                    GenericValue eftAccount = paymentMethod.getRelatedOne("EftAccount");
                    if (eftAccount != null) valueMap.put("eftAccount", eftAccount);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return paymentMethodValueMaps;
    }

    public static Map<String, Object> getPaymentMethodAndRelated(ServletRequest request, String partyId) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> results = FastMap.newInstance();

        Boolean tryEntity = true;
        if (request.getAttribute("_ERROR_MESSAGE_") != null) tryEntity = false;

        String donePage = request.getParameter("DONE_PAGE");
        if (donePage == null || donePage.length() <= 0)
            donePage = "viewprofile";
        results.put("donePage", donePage);

        String paymentMethodId = request.getParameter("paymentMethodId");

        // check for a create
        if (request.getAttribute("paymentMethodId") != null) {
            paymentMethodId = (String) request.getAttribute("paymentMethodId");
        }

        results.put("paymentMethodId", paymentMethodId);

        GenericValue paymentMethod = null;
        GenericValue creditCard = null;
        GenericValue giftCard = null;
        GenericValue eftAccount = null;

        if (UtilValidate.isNotEmpty(paymentMethodId)) {
            try {
                paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));
                creditCard = delegator.findByPrimaryKey("CreditCard", UtilMisc.toMap("paymentMethodId", paymentMethodId));
                giftCard = delegator.findByPrimaryKey("GiftCard", UtilMisc.toMap("paymentMethodId", paymentMethodId));
                eftAccount = delegator.findByPrimaryKey("EftAccount", UtilMisc.toMap("paymentMethodId", paymentMethodId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        if (paymentMethod != null) {
            results.put("paymentMethod", paymentMethod);
        } else {
            tryEntity = false;
        }

        if (creditCard != null) {
            results.put("creditCard", creditCard);
        }
        if (giftCard != null) {
            results.put("giftCard", giftCard);
        }
        if (eftAccount != null) {
            results.put("eftAccount", eftAccount);
        }

        String curContactMechId = null;

        if (creditCard != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? creditCard.getString("contactMechId") : request.getParameter("contactMechId"));
        } else if (giftCard != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? giftCard.getString("contactMechId") : request.getParameter("contactMechId"));
        } else if (eftAccount != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? eftAccount.getString("contactMechId") : request.getParameter("contactMechId"));
        }
        if (curContactMechId != null) {
            results.put("curContactMechId", curContactMechId);
        }

        results.put("tryEntity", tryEntity);

        return results;
    }

    public static GenericValue getPaymentAddress(Delegator delegator, String partyId) {
        List<GenericValue> paymentAddresses = null;
        try {
            paymentAddresses = delegator.findByAnd("PartyContactMechPurpose",
                UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PAYMENT_LOCATION"),
                UtilMisc.toList("-fromDate"));
            paymentAddresses = EntityUtil.filterByDate(paymentAddresses);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting PartyContactMechPurpose entity list", module);
        }

        // get the address for the primary contact mech
        GenericValue purpose = EntityUtil.getFirst(paymentAddresses);
        GenericValue postalAddress = null;
        if (purpose != null) {
            try {
                postalAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", purpose.getString("contactMechId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting PostalAddress record for contactMechId: " + purpose.getString("contactMechId"), module);
            }
        }

        return postalAddress;
    }

    /**
     * Returns the total from a list of Payment entities
     *
     * @param payments List of Payment GenericValue items
     * @return total payments as BigDecimal
     */

    public static BigDecimal getPaymentsTotal(List<GenericValue> payments) {
        if (payments == null) {
            throw new IllegalArgumentException("Payment list cannot be null");
        }

        BigDecimal paymentsTotal = BigDecimal.ZERO;
        for (GenericValue payment : payments) {
            paymentsTotal = paymentsTotal.add(payment.getBigDecimal("amount")).setScale(decimals, rounding);
        }
        return paymentsTotal;
    }

    /**
     * Method to return the total amount of an payment which is applied to a payment
     * @param payment GenericValue object of the Payment
     * @return the applied total as BigDecimal
     */
    public static BigDecimal getPaymentApplied(Delegator delegator, String paymentId) {
        return getPaymentApplied(delegator, paymentId, false);
    }

    public static BigDecimal getPaymentApplied(Delegator delegator, String paymentId, Boolean actual) {
        if (delegator == null) {
            throw new IllegalArgumentException("Null delegator is not allowed in this method");
        }

        GenericValue payment = null;
        try {
            payment = delegator.findByPrimaryKey("Payment", UtilMisc.toMap("paymentId", paymentId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Payment", module);
        }

        if (payment == null) {
            throw new IllegalArgumentException("The paymentId passed does not match an existing payment");
        }

        return getPaymentApplied(payment, actual);
    }
    /**
     * Method to return the amount applied converted to the currency of payment
     * @param String paymentApplicationId
     * @return the applied amount as BigDecimal
     */
    public static BigDecimal getPaymentAppliedAmount(Delegator delegator, String paymentApplicationId) {
        GenericValue paymentApplication = null;
        BigDecimal appliedAmount = BigDecimal.ZERO;
        try {
            paymentApplication = delegator.findByPrimaryKey("PaymentApplication", UtilMisc.toMap("paymentApplicationId", paymentApplicationId));
            appliedAmount = paymentApplication.getBigDecimal("amountApplied");
            if (paymentApplication.get("paymentId") != null) {
                GenericValue payment = paymentApplication.getRelatedOne("Payment");
                if (paymentApplication.get("invoiceId") != null && payment.get("actualCurrencyAmount") != null && payment.get("actualCurrencyUomId") != null) {
                    GenericValue invoice = paymentApplication.getRelatedOne("Invoice");
                    if (payment.getString("actualCurrencyUomId").equals(invoice.getString("currencyUomId"))) {
                           appliedAmount = appliedAmount.multiply(payment.getBigDecimal("amount")).divide(payment.getBigDecimal("actualCurrencyAmount"),new MathContext(100));
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Payment", module);
        }
        return appliedAmount;
    }

    /**
     * Method to return the total amount of an payment which is applied to a payment
     * @param payment GenericValue object of the Payment
     * @return the applied total as BigDecimal in the currency of the payment
     */
    public static BigDecimal getPaymentApplied(GenericValue payment) {
        return getPaymentApplied(payment, false);
    }

    /**
     * Method to return the total amount of an payment which is applied to a payment
     * @param payment GenericValue object of the Payment
     * @param false for currency of the payment, true for the actual currency
     * @return the applied total as BigDecimal in the currency of the payment
     */
    public static BigDecimal getPaymentApplied(GenericValue payment, Boolean actual) {
        BigDecimal paymentApplied = BigDecimal.ZERO;
        List<GenericValue> paymentApplications = null;
        try {
            List<EntityExpr> cond = UtilMisc.toList(
                    EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, payment.getString("paymentId")),
                    EntityCondition.makeCondition("toPaymentId", EntityOperator.EQUALS, payment.getString("paymentId"))
                   );
            EntityCondition partyCond = EntityCondition.makeCondition(cond, EntityOperator.OR);
            paymentApplications = payment.getDelegator().findList("PaymentApplication", partyCond, null, UtilMisc.toList("invoiceId", "billingAccountId"), null, false);
            if (UtilValidate.isNotEmpty(paymentApplications)) {
                for (GenericValue paymentApplication : paymentApplications) {
                    BigDecimal amountApplied = paymentApplication.getBigDecimal("amountApplied");
                    // check currency invoice and if different convert amount applied for display
                    if (actual.equals(Boolean.FALSE) && paymentApplication.get("invoiceId") != null && payment.get("actualCurrencyAmount") != null && payment.get("actualCurrencyUomId") != null) {
                        GenericValue invoice = paymentApplication.getRelatedOne("Invoice");
                        if (payment.getString("actualCurrencyUomId").equals(invoice.getString("currencyUomId"))) {
                               amountApplied = amountApplied.multiply(payment.getBigDecimal("amount")).divide(payment.getBigDecimal("actualCurrencyAmount"),new MathContext(100));
                        }
                    }
                    paymentApplied = paymentApplied.add(amountApplied).setScale(decimals,rounding);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting entities", module);
        }
        return paymentApplied;
    }

    public static BigDecimal getPaymentNotApplied(GenericValue payment) {
        return payment.getBigDecimal("amount").subtract(getPaymentApplied(payment)).setScale(decimals,rounding);
    }

    public static BigDecimal getPaymentNotApplied(GenericValue payment, Boolean actual) {
        if (actual.equals(Boolean.TRUE) && UtilValidate.isNotEmpty(payment.getBigDecimal("actualCurrencyAmount"))) {
            return payment.getBigDecimal("actualCurrencyAmount").subtract(getPaymentApplied(payment, actual)).setScale(decimals,rounding);
        }
           return payment.getBigDecimal("amount").subtract(getPaymentApplied(payment)).setScale(decimals,rounding);
    }

    public static BigDecimal getPaymentNotApplied(Delegator delegator, String paymentId) {
        return getPaymentNotApplied(delegator,paymentId, false);
    }

    public static BigDecimal getPaymentNotApplied(Delegator delegator, String paymentId, Boolean actual) {
        if (delegator == null) {
            throw new IllegalArgumentException("Null delegator is not allowed in this method");
        }

        GenericValue payment = null;
        try {
            payment = delegator.findByPrimaryKey("Payment", UtilMisc.toMap("paymentId", paymentId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Payment", module);
        }

        if (payment == null) {
            throw new IllegalArgumentException("The paymentId passed does not match an existing payment");
        }
        return payment.getBigDecimal("amount").subtract(getPaymentApplied(delegator,paymentId, actual)).setScale(decimals,rounding);
    }
    
    public static Map<String, Object>  sendPaymentSms(DispatchContext dctx, Map<String, Object> context)  {
        String paymentId = (String) context.get("paymentId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");      
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();        
        Map<String, Object> serviceResult;
		try {
			GenericValue payment = delegator.findByPrimaryKey("Payment", UtilMisc.toMap("paymentId", paymentId));
            if (payment == null) {
                Debug.logError("Invalid payment id  " + paymentId, module);
                return ServiceUtil.returnSuccess();
                //return ServiceUtil.returnError("Invalid payment id  " + paymentId);            	
            }
            GenericValue paymentType=delegator.findOne("PaymentType",UtilMisc.toMap("paymentTypeId",payment.getString("paymentTypeId")) , false);
        	String destinationPartyId = "";
            if (paymentType.getString("parentTypeId").equals("RECEIPT")) {
                destinationPartyId = payment.getString("partyIdFrom");
            }
            if (paymentType.getString("parentTypeId").equals("DISBURSEMENT")) {
                destinationPartyId = payment.getString("partyIdTo");
            }
            if (UtilValidate.isEmpty(destinationPartyId)) {
                Debug.logError("Invalid destination party id for payment " + paymentId, module);
                return ServiceUtil.returnSuccess();             
            }
            
            Map<String, Object> getTelParams = FastMap.newInstance();
        	getTelParams.put("partyId", destinationPartyId);
            getTelParams.put("userLogin", userLogin);                    	
            serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
            if (ServiceUtil.isError(serviceResult)) {
            	 Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
                return ServiceUtil.returnSuccess();
            } 
            String contactNumberTo ="";
            if(!UtilValidate.isEmpty(serviceResult.get("contactNumber"))){
            	contactNumberTo = (String) serviceResult.get("contactNumber");
            	if(!UtilValidate.isEmpty(serviceResult.get("countryCode"))){
            		contactNumberTo = (String) serviceResult.get("countryCode") + (String) serviceResult.get("contactNumber");
            	}
            	
            }
            String text =   "Received your ";
            	if(UtilValidate.isNotEmpty(payment.get("paymentMethodTypeId"))){
                	if((payment.getString("paymentMethodTypeId")).contains("CHALLAN") || (payment.getString("paymentMethodTypeId")).contains("AXISHTOH_PAYIN")){
                		text += "challan payment(ref#"+payment.getString("paymentId") +") amount of Rs. "+payment.get("amount");
                	}
                	if((payment.getString("paymentMethodTypeId")).contains("CASH")){
                		text += "cash payment(ref#"+payment.getString("paymentId") +") amount of Rs. "+payment.get("amount");
                	}
                	if((payment.getString("paymentMethodTypeId")).contains("CHEQUE")){
                		text += "cheque payment(ref#"+payment.getString("paymentId") +") amount of Rs. "+payment.get("amount")+"(subj to realisation)";
                	}
                }
            text += ". Automated message from Mother Dairy.";
            Map<String, Object> sendSmsParams = FastMap.newInstance();      
            sendSmsParams.put("contactNumberTo", contactNumberTo);                     
            sendSmsParams.put("text",text);  
            serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);       
            if (ServiceUtil.isError(serviceResult)) {
                Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);               
            }             
        } catch (Exception e) {
            Debug.logError(e, "Problem getting Invoice", module);
           
        }
        
        return ServiceUtil.returnSuccess();
    }
    /**
     * Method to return the List  payment which is  un applied  amount (open amount ) >0
     * @param payment GenericValue object of the Payment     
     */
    public static List<GenericValue> getNotAppliedPaymentsForParty(DispatchContext dctx, Map<String, Object> context) {
    	Delegator delegator = dctx.getDelegator();
        List<GenericValue> paymentsNotAppliedList = FastList.newInstance();
        try {
        	List condList = FastList.newInstance();
        	condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, (String)context.get("partyIdFrom")));
        	condList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, (String)context.get("partyIdTo")));
        	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isFullyApplied", EntityOperator.EQUALS,null),EntityOperator.OR,EntityCondition.makeCondition("isFullyApplied", EntityOperator.EQUALS,"N")));
        	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED" ,"PMNT_CONFIRMED")));
        	EntityCondition cond = EntityCondition.makeCondition(condList ,EntityOperator.AND);
            List<GenericValue> tempPaymentsNotAppliedList = delegator.findList("Payment", cond, null, null, null, false);
            for (GenericValue payment : tempPaymentsNotAppliedList) {            	
	        	BigDecimal paymentNotAppliedAmount = PaymentWorker.getPaymentNotApplied(payment);
	        	if(paymentNotAppliedAmount.compareTo(BigDecimal.ZERO) == 0){
	        		// for now change  the payment to confirm directly through the delegator , since no bank reconciliation 
	        		payment.set("isFullyApplied", "Y");	        		
	        		delegator.store(payment);
	        		continue;	        		
	        	}
	        	paymentsNotAppliedList.add(payment);
               
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return paymentsNotAppliedList;
    }
    public static Map<String, Object> createVoucherPayment(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String facilityId = (String) context.get("facilityId");
        String invoiceId = (String) context.get("invoiceId");
        String orderId = (String) context.get("orderId");
        BigDecimal paymentAmount = ProductEvents.parseBigDecimalForEntity((String) context.get("amount"));
        String paymentMethodType = (String) context.get("paymentMethodTypeId");
        String paymentMethodId = (String) context.get("paymentMethodId");
        String instrumentDateStr=(String) context.get("instrumentDate");
        String paymentType = (String) context.get("paymentTypeId");
        String invParentTypeId = (String) context.get("parentTypeId");
        String comments = (String) context.get("comments");
        String paymentRefNum = (String) context.get("paymentRefNum");
        String finAccountId = (String) context.get("finAccountId");
        String inFavourOf = (String) context.get("inFavourOf");//to be stored in PaymentAttribute
        String paymentPurposeType=(String)context.get("paymentPurposeType");
        String issuingAuthority=(String)context.get("issuingAuthority");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map<String, Object> result = ServiceUtil.returnSuccess();
        boolean useFifo = Boolean.FALSE;       
        if(UtilValidate.isNotEmpty(context.get("useFifo"))){
        	useFifo = (Boolean)context.get("useFifo");
        }
        //String paymentType = "SALES_PAYIN";
        String partyIdTo =(String)context.get("partyIdTo") ;   //"Company";
        String partyIdFrom =(String)context.get("partyIdFrom");
        
        String paymentId = "";
        boolean roundingAdjustmentFlag =Boolean.TRUE;
       
        List exprListForParameters = FastList.newInstance();
        List boothOrdersList = FastList.newInstance();
        Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
      
        Timestamp instrumentDate=UtilDateTime.nowTimestamp();
        if (UtilValidate.isNotEmpty(instrumentDateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
			try {
				instrumentDate = new java.sql.Timestamp(sdf.parse(instrumentDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ instrumentDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "	+ instrumentDateStr, module);
			}
		}
        try {
        Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", paymentType);
       // Debug.log("===paymentMethodType===="+paymentMethodType+"===partyIdFrom==="+partyIdFrom+"===partyId=="+partyIdTo+"==paymentMethodType=="+paymentMethodType+"===paymentType=="+paymentType);
        paymentCtx.put("paymentMethodTypeId", paymentMethodType);//from AR mandatory
        paymentCtx.put("paymentMethodId", paymentMethodId);//from AP mandatory
        paymentCtx.put("organizationPartyId", partyIdTo);
        paymentCtx.put("partyId", partyIdFrom);
        paymentCtx.put("facilityId", facilityId);
        paymentCtx.put("comments", comments);
        paymentCtx.put("paymentPurposeType", paymentPurposeType);
        /*if (!UtilValidate.isEmpty(paymentLocationId) ) {
            paymentCtx.put("paymentLocationId", paymentLocationId);                        	
        }   */         
        if (!UtilValidate.isEmpty(paymentRefNum) ) {
            paymentCtx.put("paymentRefNum", paymentRefNum);                        	
        }
        paymentCtx.put("issuingAuthority", issuingAuthority);  
       // paymentCtx.put("issuingAuthorityBranch", issuingAuthorityBranch);  
        paymentCtx.put("instrumentDate", instrumentDate);
        
        paymentCtx.put("statusId", "PMNT_RECEIVED");
        //paymentCtx.put("isEnableAcctg", "N");
        if (UtilValidate.isNotEmpty(finAccountId) ) {
            paymentCtx.put("finAccountId", finAccountId);                        	
        }
        
        paymentCtx.put("amount", paymentAmount);
        paymentCtx.put("userLogin", userLogin); 
        paymentCtx.put("invoices", UtilMisc.toList(invoiceId));
		
        Map<String, Object> paymentResult = dispatcher.runSync("createPaymentAndApplicationForInvoices", paymentCtx);
        if (ServiceUtil.isError(paymentResult)) {
        	Debug.logError(paymentResult.toString(), module);
            return ServiceUtil.returnError(null, null, null, paymentResult);
        }
        paymentId = (String)paymentResult.get("paymentId");
        //store attribute
        GenericValue paymentAttribute = delegator.makeValue("PaymentAttribute", UtilMisc.toMap("paymentId", paymentId, "attrName", "INFAVOUR_OF"));
        paymentAttribute.put("attrValue",inFavourOf);
        paymentAttribute.create();
        }catch (Exception e) {
        Debug.logError(e, e.toString(), module);
        return ServiceUtil.returnError(e.toString());
        }
         result = ServiceUtil.returnSuccess("Payment successfully done for Party "+partyIdTo+" ..!");
         result.put("invoiceId",invoiceId);
         result.put("parentTypeId",invParentTypeId);
         result.put("paymentId",paymentId);
         result.put("noConditionFind","Y");
         result.put("hideSearch","Y");
        return result; 
   }
    
    public static String makeMassInvoicePayments(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
	      Timestamp todayDayStart = UtilDateTime.getDayStart(nowTimeStamp);
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  BigDecimal totalAmount = BigDecimal.ZERO;
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  String paymentId = "";
	  	  String inFavourOf = "";
	  	  String paymentMethodId = "";
	  	  String paymentType = "";
	  	  String finAccountId = "";
	  	  String instrumentDateStr = "";
	  	  String paymentRefNum = "";
	  	  String invoiceId = "";
	  	  String partyIdTo = "";
	  	  String partyIdFrom = "";
  	
		  	Map invoiceAmountMap = FastMap.newInstance();
		  	List invoicesList = FastList.newInstance();
			
			  	for (int i = 0; i < rowCount; i++){
		  		  
			  	  Map paymentMap = FastMap.newInstance();
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  
		  		  BigDecimal amount = BigDecimal.ZERO;
		  		  String amountStr = "";
		  		  
		  		  if (paramMap.containsKey("invoiceId" + thisSuffix)) {
		  			invoiceId = (String) paramMap.get("invoiceId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("amt" + thisSuffix)) {
		  			amountStr = (String) paramMap.get("amt"+thisSuffix);
		  		  }
		  		  if(UtilValidate.isNotEmpty(amountStr)){
					  try {
			  			  amount = new BigDecimal(amountStr);
			  		  } catch (Exception e) {
			  			  Debug.logError(e, "Problems parsing amount string: " + amountStr, module);
			  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + amountStr);
			  			  return "error";
			  		  }
		  		  }
			  	  invoiceAmountMap.put(invoiceId,amount);
			  	  invoicesList.add(invoiceId);
			  	  totalAmount = totalAmount.add(amount);
			  	}
			  	paymentType = (String) paramMap.get("paymentTypeId");
			  	inFavourOf = (String) paramMap.get("partyIdName");
			  	partyIdTo = (String) paramMap.get("fromPartyId");
			  	partyIdFrom = (String) paramMap.get("partyId");
			  	paymentMethodId = (String) paramMap.get("paymentMethodId");
			  	finAccountId = (String) paramMap.get("finAccountId");
			  	instrumentDateStr = (String) paramMap.get("instrumentDate");
			  	paymentRefNum = (String) paramMap.get("paymentRefNum");
	  	
		  	
			  	Timestamp instrumentDate=UtilDateTime.nowTimestamp();
		        if (UtilValidate.isNotEmpty(instrumentDateStr)) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
					try {
						instrumentDate = new java.sql.Timestamp(sdf.parse(instrumentDateStr).getTime());
					} catch (ParseException e) {
						Debug.logError(e, "Cannot parse date string: "+ instrumentDateStr, module);
					} catch (NullPointerException e) {
						Debug.logError(e, "Cannot parse date string: "	+ instrumentDateStr, module);
					}
				} 
			       
		  		try {
		  			if(UtilValidate.isNotEmpty(totalAmount) && totalAmount.compareTo(BigDecimal.ZERO) > 0){
		  				Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", paymentType);
			  	        paymentCtx.put("paymentMethodId", paymentMethodId);//from AP mandatory
			  	        paymentCtx.put("organizationPartyId", partyIdTo);
			            paymentCtx.put("partyId", partyIdFrom);
			  	        if (!UtilValidate.isEmpty(paymentRefNum) ) {
			  	            paymentCtx.put("paymentRefNum", paymentRefNum);                        	
			  	        }
			  	        paymentCtx.put("instrumentDate", instrumentDate);
			  	        paymentCtx.put("statusId", "PMNT_RECEIVED");
			  	        if (UtilValidate.isNotEmpty(finAccountId) ) {
			  	            paymentCtx.put("finAccountId", finAccountId);                        	
			  	        }
			  	        paymentCtx.put("userLogin", userLogin);
			  	        paymentCtx.put("amount", totalAmount);
			  	        paymentCtx.put("invoices", invoicesList);
			  	        paymentCtx.put("invoiceAmountMap", invoiceAmountMap);
			  			try{
			  				Map<String, Object> paymentResult = dispatcher.runSync("createPaymentAndApplicationForInvoices", paymentCtx);
			  	  	        if (ServiceUtil.isError(paymentResult)) {
			  	  	            Debug.logError("Problems in service createPaymentAndApplicationForInvoices", module);
			  		  			request.setAttribute("_ERROR_MESSAGE_", "Error in service createPaymentAndApplicationForInvoices");
			  		  			return "error";
			  	  	        }
			  	  	        paymentId = (String)paymentResult.get("paymentId");
			  			}catch (Exception e) {
			  		        Debug.logError(e, e.toString(), module);
			  		        return "error";
			  		        }
			  	        //store attribute
			  	        GenericValue paymentAttribute = delegator.makeValue("PaymentAttribute", UtilMisc.toMap("paymentId", paymentId, "attrName", "INFAVOUR_OF"));
			  	        paymentAttribute.put("attrValue",inFavourOf);
			  	        paymentAttribute.create();
		  			}
		  		}catch (GenericEntityException e2) {
		  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
		  		}
		  		 result = ServiceUtil.returnSuccess("Payment successfully done for Party "+partyIdTo+" ..!");
		         result.put("paymentId",paymentId);
		         result.put("partyIdFrom",partyIdTo);
		         result.put("noConditionFind","Y");
		         result.put("hideSearch","Y"); 
		         request.setAttribute("_EVENT_MESSAGE_", "Payment successfully done for Party "+partyIdTo);
		         return "success"; 
	}
    
    
    public static Map<String, Object> depositReceiptPayment(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String paymentId = (String) context.get("paymentId");
        String finAccountId = (String) context.get("finAccountId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
			GenericValue payment = delegator.findByPrimaryKey("Payment", UtilMisc.toMap("paymentId", paymentId));
			/*if(!UtilAccounting.isPaymentType(payment, "RECEIPT")){
				return result; 
			}*/
			
				List<EntityExpr> condList = FastList.newInstance();
				if(UtilAccounting.isPaymentMethodType(payment, "CASH")){
					condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"CASH"));
					condList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.NOT_EQUAL,"PETTY_CASH"));
					EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
		            List cashFinAccountList = delegator.findList("FinAccount", cond, null, null, null, true);
		            GenericValue cashAccount = EntityUtil.getFirst(cashFinAccountList);
		            if(UtilValidate.isNotEmpty(cashAccount)){
		            	finAccountId = cashAccount.getString("finAccountId");
		            }
				}
				if(UtilValidate.isEmpty(finAccountId)){
					return result;  
				}
	          Map depositWithdrawPaymentCtx = UtilMisc.toMap("userLogin", userLogin);
	          depositWithdrawPaymentCtx.put("paymentIds", UtilMisc.toList(paymentId));
	          depositWithdrawPaymentCtx.put("transactionDate",payment.getTimestamp("transactionDate"));
	          if(UtilValidate.isEmpty(payment.getString("transactionDate"))){
	        	  depositWithdrawPaymentCtx.put("transactionDate",payment.getTimestamp("effectiveDate"));
	          }
	          depositWithdrawPaymentCtx.put("finAccountId",finAccountId);
	          Map<String, Object> depositResult = dispatcher.runSync("depositWithdrawPayments", depositWithdrawPaymentCtx);
	          if (ServiceUtil.isError(depositResult)) {
	          	Debug.logError(depositResult.toString(), module);
	              return ServiceUtil.returnError(null, null, null, depositResult);
	          }
        }catch(Exception e){
        	 Debug.logError(e, e.toString(), module);
             return ServiceUtil.returnError(e.toString());
        }
        return result; 
   }
    
    public static Map<String, Object> refundAdvanceUnappliedPayments(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        BigDecimal paymentAmount = ProductEvents.parseBigDecimalForEntity((String) context.get("amountToRefund"));
        String paymentMethodType = (String) context.get("paymentMethodTypeId");
        String paymentMethodId = (String) context.get("paymentMethodId");
        String instrumentDateStr=(String) context.get("instrumentDate");
        String paymentTypeId = (String) context.get("paymentTypeId");
        String comments = (String) context.get("comments");
        String paymentRefNum = (String) context.get("paymentRefNum");
        String issuingAuthority = (String) context.get("issuingAuthority");//to be stored in PaymentAttribute
        String partyIdTo =(String)context.get("partyIdTo");
        String partyIdFrom =(String)context.get("partyIdFrom");
        String statusId =(String)context.get("statusId");
        String paymentId =(String)context.get("paymentId");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        
        Timestamp instrumentDate=UtilDateTime.nowTimestamp();
        if (UtilValidate.isNotEmpty(instrumentDateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
			try {
				instrumentDate = new java.sql.Timestamp(sdf.parse(instrumentDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ instrumentDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "	+ instrumentDateStr, module);
			}
		}
        
        String toPaymentId = "";
        Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
        
        // Create Payment
        
        Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", paymentTypeId);
        paymentCtx.put("paymentMethodTypeId", paymentMethodType);
        paymentCtx.put("partyIdTo", partyIdTo);
        paymentCtx.put("partyIdFrom", partyIdFrom);
        if (!UtilValidate.isEmpty(paymentMethodId)) {
     	   paymentCtx.put("paymentMethodId", paymentMethodId);                       	
        }   
        if (!UtilValidate.isEmpty(context.get("isEnableAcctg"))) {
        	paymentCtx.put("isEnableAcctg", context.get("isEnableAcctg"));                       	
        }   
        /*if (!UtilValidate.isEmpty(paymentLocationId) ) {
            paymentCtx.put("paymentLocation", paymentLocationId);                        	
        }*/ 
        if (!UtilValidate.isEmpty(paymentRefNum) ) {
            paymentCtx.put("paymentRefNum", paymentRefNum);                        	
        }
        if (!UtilValidate.isEmpty(issuingAuthority) ) {
            paymentCtx.put("issuingAuthority", issuingAuthority);                        	
        }
        if (!UtilValidate.isEmpty(instrumentDate) ) {
            paymentCtx.put("effectiveDate", instrumentDate);                        	
        }
        /*if (!UtilValidate.isEmpty(issuingAuthorityBranch) ) {
            paymentCtx.put("issuingAuthorityBranch", issuingAuthorityBranch);                        	
        }
        if (!UtilValidate.isEmpty(paymentPurposeType)) {
            paymentCtx.put("paymentPurposeType", paymentPurposeType);                        	
        }*/
        if (UtilValidate.isNotEmpty(instrumentDate) ) {
            paymentCtx.put("paymentDate", instrumentDate);                        	
        }
        else{
        	paymentCtx.put("paymentDate", UtilDateTime.nowTimestamp());
        }
        paymentCtx.put("statusId", statusId);            
        paymentCtx.put("amount", paymentAmount);
        paymentCtx.put("userLogin", userLogin);
        paymentCtx.put("comments", comments);
        paymentCtx.put("createdByUserLogin", userLogin.getString("userLoginId"));
        paymentCtx.put("lastModifiedByUserLogin",  userLogin.getString("userLoginId"));
        paymentCtx.put("createdDate", UtilDateTime.nowTimestamp());
        paymentCtx.put("lastModifiedDate", UtilDateTime.nowTimestamp());
        try {       	
            Map<String, Object> paymentResult = dispatcher.runSync("createPayment", paymentCtx);
            if (ServiceUtil.isError(paymentResult)) {
            	Debug.logError(paymentResult.toString(), module);    			
                return ServiceUtil.returnError(null, null, null, paymentResult);
            }
            toPaymentId = (String)paymentResult.get("paymentId");
        }catch (Exception e) {
            Debug.logError(e, e.toString(), module);
            return ServiceUtil.returnError(e.toString());
        }
        // Payment Application To Payment
        
        Map<String, Object> paymentApplicationCtx = UtilMisc.<String, Object>toMap("paymentId", paymentId);
        paymentApplicationCtx.put("toPaymentId", toPaymentId);
        paymentApplicationCtx.put("amountApplied", paymentAmount);
        paymentApplicationCtx.put("userLogin", userLogin);
        try {       	
            Map<String, Object> paymentApplicationResult = dispatcher.runSync("updatePaymentApplicationDef", paymentApplicationCtx);
            if (ServiceUtil.isError(paymentApplicationResult)) {
            	Debug.logError(paymentApplicationResult.toString(), module);    			
                return ServiceUtil.returnError(null, null, null, paymentApplicationResult);
            }
            String paymentApplicationId = (String)paymentApplicationResult.get("paymentApplicationId");
        }catch (Exception e) {
            Debug.logError(e, e.toString(), module);
            return ServiceUtil.returnError(e.toString());
        }
        
        result = ServiceUtil.returnSuccess("Refund has been successfully issued to Party "+partyIdTo+" ..!");
        result.put("paymentId",paymentId);
        result.put("noConditionFind","N");
        result.put("hideSearch","N");
        return result; 
    }
    
}

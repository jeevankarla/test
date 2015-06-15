package org.ofbiz.accounting.invoice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.TimeZone;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.accounting.util.formula.Evaluator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.TimeDuration;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * InvoicePayrolWorkerNew - Worker service methods for payroll invoices
 */
public class InvoicePayrolWorkerNew {
	public static String module = InvoicePayrolWorkerNew.class.getName();

	/**
	 * 
	 * Assumptions:
	 * 1) The deduction type id and benefit type 
	 * ids match the invoice item ids.  Currently the db design cannot guarantee this 
	 * referential integrity. 
	 * @param dctx
	 * @param context
	 * @return
	 */
	
	public static Map<String, Object> createPayrolInvoiceItems(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
		String invoiceId = (String) context.get("invoiceId");
		String payrollHeaderId = (String) context.get("payrollHeaderId");
		String partyId = (String) context.get("partyId");		
        String errorMsg = "createPayrolInvoiceItems failed for party '" + partyId + "': ";			
        GenericValue userLogin = (GenericValue) context.get("userLogin");            
		Map<String, Object> serviceResults;
		// Create invoice items
		try {
			    List<GenericValue> payrollHeaderItems = delegator.findByAnd("PayrollHeaderItem", UtilMisc.toMap("payrollHeaderId",payrollHeaderId));
			   // here exclude internal loan payHeads and create invoice for gross(includes taxes and external loans) amount 
			    List<GenericValue> loanTypes = delegator.findList("LoanType", EntityCondition.makeCondition(EntityCondition.makeCondition("isExternal",EntityOperator.EQUALS,null),EntityOperator.OR ,EntityCondition.makeCondition("isExternal",EntityOperator.NOT_EQUAL,"Y")), UtilMisc.toSet("loanTypeId","payHeadTypeId"), null, null, false);
			    if(UtilValidate.isNotEmpty(loanTypes) && UtilValidate.isNotEmpty(EntityUtil.getFieldListFromEntityList(loanTypes, "payHeadTypeId", true))){
			    	payrollHeaderItems = EntityUtil.filterByCondition(payrollHeaderItems, EntityCondition.makeCondition("payrollHeaderItemTypeId",EntityOperator.NOT_IN,EntityUtil.getFieldListFromEntityList(loanTypes, "payHeadTypeId", true)));
			    }
			    
			    for (int i = 0; i < payrollHeaderItems.size(); ++i) {		
					GenericValue payrollHeaderItem = payrollHeaderItems.get(i);
					String payrollHeaderItemTypeId = payrollHeaderItem.getString("payrollHeaderItemTypeId");				
					Map input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);				
					input.put("invoiceItemTypeId", payrollHeaderItemTypeId);			  
					input.put("quantity", BigDecimal.ONE);
					input.put("amount", payrollHeaderItem.getBigDecimal("amount"));
		            serviceResults = dispatcher.runSync("createInvoiceItem", input);
		            if (ServiceUtil.isError(serviceResults)) {
		            	Debug.logError(errorMsg+"===invoiceItemTypeId ::"+payrollHeaderItemTypeId, module);
		                return ServiceUtil.returnError(errorMsg+"===invoiceItemTypeId ::"+payrollHeaderItemTypeId, null, null, serviceResults);
	            }				
			 }  
			
	        			
		}catch (Exception e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records: " + e.getMessage());
	    }   		
				
		return ServiceUtil.returnSuccess();   	
	}
	
	
 public static Map<String, Object> createPayrolInvoiceForPeriodBilling(DispatchContext dctx, Map<String, Object> context) {
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    String periodBillingId = (String) context.get("periodBillingId");
		/*String partyId = (String) context.get("partyId");	
		String partyIdFrom = (String) context.get("partyIdFrom");	
        String timePeriodId = (String)context.get("timePeriodId");	*/
	    String invoiceTypeId = "PAYROL_INVOICE";
	    String errorMsg = "createPayrolInvoice failed"; 		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
        Map<String, Object> serviceResults = ServiceUtil.returnError(errorMsg, null, null, null);    
		List<GenericValue> invoiceList = FastList.newInstance();
        Map input = UtilMisc.toMap("userLogin", userLogin);
       
        input.put("invoiceTypeId", invoiceTypeId);        
        input.put("statusId", "INVOICE_IN_PROCESS");	
        input.put("currencyUomId", context.get("currencyUomId"));	
        input.put("dueDate", context.get("dueDate"));  
       
        
        Boolean enablePayrollInvAcctg = Boolean.TRUE;
		try {   
			
			GenericValue periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId",periodBillingId), false);
			if(UtilValidate.isEmpty(periodBilling)){
				Debug.logError(errorMsg, module);
				return serviceResults;
			}
			String timePeriodId = periodBilling.getString("customTimePeriodId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",timePeriodId), false);
			
			input.put("timePeriodId", timePeriodId);
	        Timestamp timePeriodStart = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	        Timestamp timePeriodEnd = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
	        input.put("timePeriodId", timePeriodId); 
	        input.put("invoiceAttrValue", timePeriodId);
	        input.put("invoiceAttrName", "TIME_PERIOD_ID");
            SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");	        
	        input.put("description", "Payroll [" + sd.format(UtilDateTime.toCalendar(timePeriodStart).getTime()) 
	        		+ " - " + sd.format(UtilDateTime.toCalendar(timePeriodEnd).getTime()) + "]");	
	        
	        // check Accounting tenant configration for payroll invoice here
	        GenericValue tenantConfigEnablePayrollInvAcctg = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","ACCOUNT_INVOICE", "propertyName","enablePayrollInvAcctg"), true);
			 if (UtilValidate.isNotEmpty(tenantConfigEnablePayrollInvAcctg) && (tenantConfigEnablePayrollInvAcctg.getString("propertyValue")).equals("N")) {
				 enablePayrollInvAcctg = Boolean.FALSE;
			 }
			 if(!enablePayrollInvAcctg){
				 input.put("isEnableAcctg", "N");
			  }
			 if(UtilValidate.isNotEmpty(periodBilling.getString("billingTypeId")) && (periodBilling.getString("billingTypeId")).equals("SP_DA_ARREARS")){
     			input.put("allowMultipleInvForPeriod", "Y");
     		}
			  input.put("periodBillingId",periodBillingId);
			  input.put("referenceNumber",periodBilling.getString("billingTypeId")+"_"+periodBillingId);
	         	List conditionList = UtilMisc.toList(
	        			EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
	        	
	        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
	        	List<GenericValue> payrollHeaders = delegator.findList("PayrollHeader", condition, null, null, null, false);
			 //List<GenericValue> payrollHeaders = delegator.findByAnd("PayrollHeader", UtilMisc.toMap(""));
	        	Timestamp startTimestamp = UtilDateTime.nowTimestamp();
	        	int emplCounter =0;
	        	double elapsedSeconds;
	        	for (int i = 0; i < payrollHeaders.size(); ++i) {		
	        		GenericValue payrollHeader = payrollHeaders.get(i);
	        		input.put("partyId", payrollHeader.getString("partyId"));
	        		input.put("partyIdFrom", payrollHeader.getString("partyIdFrom"));
	        		
	        		serviceResults = dispatcher.runSync("createInvoice", input);
	        		if (ServiceUtil.isError(serviceResults)) {
	        			Debug.logError(errorMsg+"***invoiceDetails==="+input, null, null, serviceResults);
	        			return ServiceUtil.returnError(errorMsg+"***invoiceDetails==="+input, null, null, serviceResults);
	        		}	 
	        		String invoiceId = (String)serviceResults.get("invoiceId");
	        		GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap(
	        				"invoiceId", invoiceId), false);
	        		Map inputItemCtx = UtilMisc.toMap("userLogin",userLogin);
	        		inputItemCtx.put("partyId", payrollHeader.getString("partyId"));
	        		inputItemCtx.put("invoiceId", invoiceId);
	        		inputItemCtx.put("payrollHeaderId", payrollHeader.getString("payrollHeaderId"));
	        		serviceResults = createPayrolInvoiceItems(dctx,inputItemCtx);
	        		if (ServiceUtil.isError(serviceResults)) {
	        			Debug.logError(ServiceUtil.getErrorMessage(serviceResults)+"***invoiceDetails==="+input, module);
	        			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResults), null, null, serviceResults);
	        		}
	        		Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
	                invoiceCtx.put("userLogin", userLogin);
	                invoiceCtx.put("statusId","INVOICE_APPROVED");
                	Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
                	if (ServiceUtil.isError(invoiceResult)) {
                		Debug.logError(invoiceResult.toString()+"***invoiceDetails==="+input, module);
                        return ServiceUtil.returnError(null, null, null, invoiceResult);
                    }
                	invoiceCtx.put("userLogin", userLogin);
	                invoiceCtx.put("statusId","INVOICE_READY");
                	invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
                	if (ServiceUtil.isError(invoiceResult)) {
                		Debug.logError(invoiceResult.toString(), module);
                        return ServiceUtil.returnError(null, null, null, invoiceResult);
                    }
                	Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
                	paymentCtx.put("userLogin", userLogin);
                	paymentCtx.put("payrollHeaderId", payrollHeader.getString("payrollHeaderId"));
                	Map<String, Object> paymentResult = dispatcher.runSync("createPayrolPaymentAndAppclications",paymentCtx);
                	if (ServiceUtil.isError(paymentResult)) {
                		Debug.logError(paymentResult.toString(), module);
                        return ServiceUtil.returnError(ServiceUtil.getErrorMessage(paymentResult)+"***invoiceDetails==="+input, null, null, paymentResult);
                    }
                	
	        		invoiceList.add(invoice);
	        		emplCounter++;
               		if ((emplCounter % 20) == 0) {
               			elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
               			Debug.logImportant("Completed " + emplCounter + " employee [ in " + elapsedSeconds + " seconds]", module);
               		}
	        	}
	        //create consolidated tax invoices here	
		}catch(GenericEntityException e) {
			Debug.logError(e, module);
	        return ServiceUtil.returnError("Unable to create payroll Invoice record");			
		}		
		catch (GenericServiceException e) {
			Debug.logError(e, errorMsg + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + e.getMessage());
	    }
		serviceResults.clear();
		//serviceResults.put("invoices", invoiceList);
		return serviceResults;      
	}
 
 public static Map<String, Object> createPayrolTaxInvoices(DispatchContext dctx, Map<String, Object> context) {
     Delegator delegator = dctx.getDelegator();
     LocalDispatcher dispatcher = dctx.getDispatcher();
     String periodBillingId = (String) context.get("periodBillingId");
	 String invoiceId = (String) context.get("invoiceId");
     String errorMsg = "createPayrol Tax Invoice failed";		
     GenericValue userLogin = (GenericValue) context.get("userLogin");            
	 Map<String, Object> serviceResults;
		try {
				GenericValue periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId",periodBillingId), false);
				if(UtilValidate.isEmpty(periodBilling)){
					Debug.logError(errorMsg, module);
				}
			    List<GenericValue> payrollHeaderItems = delegator.findByAnd("PayrollHeaderAndHeaderItem", UtilMisc.toMap("periodBillingId",periodBillingId));
			    List condList  = FastList.newInstance();
			    condList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.IN,EntityUtil.getFieldListFromEntityList(payrollHeaderItems, "payrollHeaderItemTypeId", true)));
			    condList.add(EntityCondition.makeCondition("taxAuthPartyId",EntityOperator.NOT_EQUAL,null));
			    EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			    
			    List<GenericValue> invoiceItemTypes = delegator.findList("InvoiceItemType", cond, null, null, null, false);
			    for(GenericValue invoiceItemType : invoiceItemTypes){
			    	String invoiceItemTypeId = invoiceItemType.getString("invoiceItemTypeId");
			    	BigDecimal amount = BigDecimal.ZERO;
			    	List<GenericValue> headerItems = EntityUtil.filterByCondition(payrollHeaderItems, EntityCondition.makeCondition("payrollHeaderItemTypeId",EntityOperator.EQUALS,invoiceItemTypeId));
			    	for(GenericValue headerItem : headerItems){
			    		amount = amount.add(headerItem.getBigDecimal("amount"));
			    	}
			    	Map input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);
			    	input.put("periodBillingId",periodBillingId);
					input.put("referenceNumber",periodBilling.getString("billingTypeId")+"_"+periodBillingId);
					input.put("invoiceItemTypeId", invoiceItemTypeId);
					input.put("taxAuthPartyId", invoiceItemType.getString("taxAuthPartyId"));
					input.put("quantity","1");
					input.put("amount", amount);
					Debug.log("input===="+input);
		            serviceResults = dispatcher.runSync("createTaxInvoice", input);
		            if (ServiceUtil.isError(serviceResults)) {
		            	Debug.logError(errorMsg+"===invoiceItemTypeId ::"+invoiceItemTypeId, module);
		                return ServiceUtil.returnError(errorMsg+"===invoiceItemTypeId ::"+invoiceItemTypeId, null, null, serviceResults);
		            }	
			    	
			    }
			    /*for (int i = 0; i < payrollHeaderItems.size(); ++i) {		
				GenericValue payrollHeaderItem = payrollHeaderItems.get(i);
				String payrollHeaderItemTypeId = payrollHeaderItem.getString("payrollHeaderItemTypeId");				
				Map input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);				
				input.put("invoiceItemTypeId", payrollHeaderItemTypeId);			  
				input.put("quantity", BigDecimal.ONE);
				input.put("amount", payrollHeaderItem.getBigDecimal("amount"));
	            serviceResults = dispatcher.runSync("createInvoiceItem", input);
	            if (ServiceUtil.isError(serviceResults)) {
	            	Debug.logError(errorMsg+"===invoiceItemTypeId ::"+payrollHeaderItemTypeId, module);
	                return ServiceUtil.returnError(errorMsg+"===invoiceItemTypeId ::"+payrollHeaderItemTypeId, null, null, serviceResults);
	            }				
			}  */
			
	        			
		}catch (Exception e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records: " + e.getMessage());
	    }   		
				
		return ServiceUtil.returnSuccess();   	
	}
 
 public static Map<String, Object> createPayrolPaymentAndAppclications(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
		String invoiceId = (String) context.get("invoiceId");
		String payrollHeaderId = (String) context.get("payrollHeaderId");
			
       String errorMsg = "create Payment for PayrolInvoice failed for invoiceId '" + invoiceId + "': ";			
       GenericValue userLogin = (GenericValue) context.get("userLogin");            
		Map<String, Object> serviceResults= ServiceUtil.returnSuccess();
		// Create invoice items
		try {
			 GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			  List<GenericValue> payrollLoanHeaderItems = FastList.newInstance();
			  List<GenericValue> payrollHeaderItems = delegator.findByAnd("PayrollHeaderItem", UtilMisc.toMap("payrollHeaderId",payrollHeaderId));
		     // here exclude internal loan payHeads and create invoice for gross(includes taxes and external loans) amount 
		     List<GenericValue> loanTypes = delegator.findList("LoanType", EntityCondition.makeCondition(EntityCondition.makeCondition("isExternal",EntityOperator.EQUALS,null),EntityOperator.OR ,EntityCondition.makeCondition("isExternal",EntityOperator.NOT_EQUAL,"Y")), UtilMisc.toSet("loanTypeId","payHeadTypeId"), null, null, false);
		     if(UtilValidate.isNotEmpty(loanTypes) && UtilValidate.isNotEmpty(EntityUtil.getFieldListFromEntityList(loanTypes, "payHeadTypeId", true))){
		    	payrollLoanHeaderItems = EntityUtil.filterByCondition(payrollHeaderItems, EntityCondition.makeCondition("payrollHeaderItemTypeId",EntityOperator.IN,EntityUtil.getFieldListFromEntityList(loanTypes, "payHeadTypeId", true)));
		     }
		     BigDecimal loanRecoveryAmount = BigDecimal.ZERO;
			for(GenericValue payrollLoanHeaderItem : payrollLoanHeaderItems){
				List<GenericValue> loanAndRecoveryAndTypes = delegator.findList("LoanAndRecoveryAndType", EntityCondition.makeCondition(EntityCondition.makeCondition("payrollHeaderId",EntityOperator.EQUALS,payrollLoanHeaderItem.getString("payrollHeaderId")),EntityOperator.AND ,EntityCondition.makeCondition("payrollItemSeqId",EntityOperator.EQUALS,payrollLoanHeaderItem.getString("payrollItemSeqId"))),null, null, null, false);
				GenericValue loanAndRecoveryAndType = EntityUtil.getFirst(loanAndRecoveryAndTypes);
				BigDecimal amount  = BigDecimal.ZERO;
				/*if(UtilValidate.isEmpty(loanAndRecoveryAndType)){
					continue;
				}*/
				/*if(UtilValidate.isNotEmpty(loanAndRecoveryAndType.getBigDecimal("principalAmount"))){
					amount  = amount.add(loanAndRecoveryAndType.getBigDecimal("principalAmount"));
				}
				if(UtilValidate.isNotEmpty(loanAndRecoveryAndType.getBigDecimal("interestAmount"))){
					amount  = amount.add(loanAndRecoveryAndType.getBigDecimal("interestAmount"));
				}*/
				amount = payrollLoanHeaderItem.getBigDecimal("amount");
				amount = amount.negate();
				loanRecoveryAmount = loanRecoveryAmount.add(amount);
				/*if(UtilValidate.isEmpty(loanAndRecoveryAndType)){
					continue;
				}*/
				 Map newp = UtilMisc.toMap("userLogin",userLogin);
				 newp.put("partyIdFrom", invoice.getString("partyId"));
				 newp.put("partyIdTo", invoice.getString("partyIdFrom"));
				 newp.put("paymentMethodId", "DEBITNOTE");
				 newp.put("paymentTypeId", "LOANRECOVERY_PAYOUT");
				 newp.put("statusId", "PMNT_NOT_PAID");
				 if(UtilValidate.isNotEmpty(loanAndRecoveryAndType)){
					 newp.put("comments",loanAndRecoveryAndType.getString("description")+"(loanId :"+loanAndRecoveryAndType.getString("loanId") +")" +"Loan Recovery,for Payroll Period["+"]");
				 }
				 newp.put("paymentRefNum", invoice.getString("referenceNumber"));
				 newp.put("amount", amount);
				 Map<String, Object> paymentResult = dispatcher.runSync("createPayment",newp);
            	if (ServiceUtil.isError(paymentResult)) {
            		Debug.logError(paymentResult.toString(), module);
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(paymentResult), null, null, paymentResult);
                }
            	Map<String, Object> setPaymentStatusMap = UtilMisc.<String, Object>toMap("userLogin", userLogin);
	        	setPaymentStatusMap.put("paymentId", paymentResult.get("paymentId"));
	        	setPaymentStatusMap.put("statusId", "PMNT_SENT");
	        	if(UtilValidate.isNotEmpty(loanAndRecoveryAndType.getString("loanFinAccountId"))){
	        		setPaymentStatusMap.put("finAccountId", loanAndRecoveryAndType.getString("loanFinAccountId"));
	        	}
	            Map<String, Object> pmntResults = dispatcher.runSync("setPaymentStatus", setPaymentStatusMap);
            	serviceResults.put("paymentId",paymentResult.get("paymentId"));
            	Map newPayappl = UtilMisc.toMap("userLogin",userLogin);
            	newPayappl.put("invoiceId", invoiceId);
            	newPayappl.put("paymentId", paymentResult.get("paymentId"));
            	newPayappl.put("amountApplied", amount);
            	
            	Map<String, Object> paymentApplResult = dispatcher.runSync("createPaymentApplication",newPayappl);
           	if (ServiceUtil.isError(paymentApplResult)) {
           		Debug.logError(paymentResult.toString(), module);
                   return ServiceUtil.returnError(ServiceUtil.getErrorMessage(paymentApplResult), null, null, paymentApplResult);
               }
           	// here update loan recover record and withdraw from loan finaccount
				GenericValue loanRecovery = delegator.findOne("LoanRecovery", UtilMisc.toMap("loanId",loanAndRecoveryAndType.get("loanId"),"sequenceNum",loanAndRecoveryAndType.get("sequenceNum")), false);
				loanRecovery.set("paymentId", paymentResult.get("paymentId"));
				delegator.store(loanRecovery);
				/*if(UtilValidate.isNotEmpty(loanAndRecoveryAndType.getString("loanFinAccountId"))){
					Map<String, Object> depositPaymentCtx = UtilMisc.<String, Object>toMap("userLogin", userLogin);
					depositPaymentCtx.put("paymentIds", UtilMisc.toList((String)paymentResult.get("paymentId")));
					depositPaymentCtx.put("finAccountId", loanAndRecoveryAndType.getString("loanFinAccountId")); 
		        	Debug.log("depositPaymentCtx======="+depositPaymentCtx);
		            Map<String, Object> paymentDepositResult = dispatcher.runSync("depositWithdrawPayments", depositPaymentCtx);
		            
		            if (ServiceUtil.isError(paymentDepositResult)) {
		            	Debug.logError(paymentDepositResult.toString(), module);    			
		                return ServiceUtil.returnError(null, null, null, paymentDepositResult);
		            }
				}*/
				
			}	
			
			//List<GenericValue> paymentApplication = delegator.findByAnd("PaymentApplication", UtilMisc.toMap("invoiceId",invoiceId));
			//only generate payment if no application exist yet
			//if(UtilValidate.isEmpty(paymentApplication)){
				 Map newp = UtilMisc.toMap("userLogin",userLogin);
				 newp.put("partyIdFrom", invoice.getString("partyId"));
				 newp.put("partyIdTo", invoice.getString("partyIdFrom"));
				 newp.put("paymentMethodTypeId", "CHEQUE_PAYIN");
				 newp.put("paymentTypeId", "PAYROL_PAYMENT");
				 newp.put("statusId", "PMNT_NOT_PAID");
				 newp.put("paymentRefNum", invoice.getString("referenceNumber"));
				 
				 BigDecimal amount = InvoiceWorker.getInvoiceTotal(invoice);
				 amount = amount.subtract(loanRecoveryAmount);
				 newp.put("amount", amount);
				 Map<String, Object> paymentResult = dispatcher.runSync("createPayment",newp);
             	if (ServiceUtil.isError(paymentResult)) {
             		Debug.logError(paymentResult.toString(), module);
                     return ServiceUtil.returnError(ServiceUtil.getErrorMessage(paymentResult), null, null, paymentResult);
                 }
             	serviceResults.put("paymentId",paymentResult.get("paymentId"));
             	Map newPayappl = UtilMisc.toMap("userLogin",userLogin);
             	newPayappl.put("invoiceId", invoiceId);
             	newPayappl.put("paymentId", paymentResult.get("paymentId"));
             	newPayappl.put("amountApplied", amount);
				paymentResult = dispatcher.runSync("createPaymentApplication",newPayappl);
            	if (ServiceUtil.isError(paymentResult)) {
            		Debug.logError(paymentResult.toString(), module);
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(paymentResult), null, null, paymentResult);
                }
				
			//}
			
	        			
		}catch (Exception e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records: " + e.getMessage());
	    }   		
				
		return serviceResults;   	
	}
 
 	public static Map<String, Object> createInvoiceAndPaymentForSubsidyGhee(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    String periodBillingId = (String) context.get("periodBillingId");
	    String errorMsg = "createPayrolInvoice failed"; 		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
	    Map<String, Object> serviceResults = ServiceUtil.returnError(errorMsg, null, null, null);    
	    
	    Boolean enablePayrollInvAcctg = Boolean.TRUE;
	    GenericValue facilityDetails =null;
	     
		try {   
			
			GenericValue periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId",periodBillingId), false);
			if(UtilValidate.isEmpty(periodBilling)){
				Debug.logError(errorMsg, module);
				return serviceResults;
			}
			String timePeriodId = periodBilling.getString("customTimePeriodId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",timePeriodId), false);
	        Timestamp timePeriodStart = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	        Timestamp timePeriodEnd = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
	        
	        // check Accounting tenant configration for payroll invoice here
			  
			 List conditionList = UtilMisc.toList(
			 EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
			 EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
			 List<GenericValue> payrollHeaders = delegator.findList("PayrollHeader", condition, null, null, null, false);
			 Timestamp startTimestamp = UtilDateTime.nowTimestamp();
			 int emplCounter =0;
			 double elapsedSeconds;
			 for (int i = 0; i < payrollHeaders.size(); ++i) {		
				 GenericValue payrollHeader = payrollHeaders.get(i);
				 String partyId = payrollHeader.getString("partyIdFrom");
	        		
				 List conditionList1 = FastList.newInstance();
				 conditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
				 conditionList1.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,timePeriodId));
				 conditionList1.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "83"));
				 EntityCondition condition1=EntityCondition.makeCondition(conditionList1,EntityOperator.AND); 		
				 List<GenericValue> employeeSubsidyProductIssue = delegator.findList("EmployeeSubsidyProductIssue", condition1, null, null, null, false);
				 if(UtilValidate.isNotEmpty(employeeSubsidyProductIssue)){
					 GenericValue employeeSubsidyProductValue = EntityUtil.getFirst(employeeSubsidyProductIssue);
					 BigDecimal totalAmount = employeeSubsidyProductValue.getBigDecimal("amount");
					 String boothId = employeeSubsidyProductValue.getString("facilityId");
					 
					 facilityDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId", boothId),false);
					 if(UtilValidate.isEmpty(facilityDetails)){	    			
						 return ServiceUtil.returnError("Booth Id does not exists");
					 }
	    				
					 Timestamp invoiceDate = UtilDateTime.getDayStart(timePeriodStart);
					 String partyIdFrom = "Company";
					 Map<String, Object> createInvoiceContext = FastMap.newInstance();
					 createInvoiceContext.put("partyId", partyId);
					 createInvoiceContext.put("partyIdFrom", partyIdFrom);
					 createInvoiceContext.put("invoiceDate", invoiceDate);
	    	  		 createInvoiceContext.put("facilityId", facilityDetails.getString("ownerPartyId"));
	    	  		 createInvoiceContext.put("invoiceTypeId", "MIS_INCOME_IN");
	    	  		 createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
	    	  		 createInvoiceContext.put(""+ "userLogin", userLogin);
	    	  		 createInvoiceContext.put("periodBillingId",periodBillingId);
	    	  		 createInvoiceContext.put("timePeriodId", timePeriodId);
	    	  		 createInvoiceContext.put("invoiceAttrValue", timePeriodId);
	    	  		 createInvoiceContext.put("invoiceAttrName", "TIME_PERIOD_ID");
	    	  		 createInvoiceContext.put("referenceNumber",periodBilling.getString("billingTypeId")+"_"+periodBillingId);
	    		     SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");	        
	    		     createInvoiceContext.put("description", "Payroll [" + sd.format(UtilDateTime.toCalendar(timePeriodStart).getTime()) 
	    		        		+ " - " + sd.format(UtilDateTime.toCalendar(timePeriodEnd).getTime()) + "]");	

	    	  		 GenericValue tenantConfigEnablePayrollInvAcctg = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","ACCOUNT_INVOICE", "propertyName","enablePayrollInvAcctg"), true);
	    	  		 if (UtilValidate.isNotEmpty(tenantConfigEnablePayrollInvAcctg) && (tenantConfigEnablePayrollInvAcctg.getString("propertyValue")).equals("N")) {
	    	  			 enablePayrollInvAcctg = Boolean.FALSE;
	    	  		 }
	    	  		 if(!enablePayrollInvAcctg){
	    	  			 createInvoiceContext.put("isEnableAcctg", "N");
	    	  		 }
	    	  		 
	    			
	    	  		 String invoiceId = null;
	    	  		 try {
	    	  			 serviceResults = dispatcher.runSync("createInvoice",createInvoiceContext);
	    	  			 if (ServiceUtil.isError(serviceResults)) {
	    	  				 return ServiceUtil.returnError("There was an error while creating Invoice"+ ServiceUtil.getErrorMessage(serviceResults));
	    	  			 }
	    	  			 invoiceId = (String) serviceResults.get("invoiceId");
	    	  		 } catch (GenericServiceException e) {
	    	  			 Debug.logError(e, module);
	    	  			 return ServiceUtil.returnError("Unable to create payroll Invoice record");	
	    	  		 }
	      			
	    	  		 GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
	      			
	    	  		 Map inputItemCtx = UtilMisc.toMap("userLogin",userLogin);
	    	  		 inputItemCtx.put("amount", totalAmount);
	    	  		 inputItemCtx.put("invoiceId", invoiceId);
	    	  		 inputItemCtx.put("invoiceItemTypeId", "PAYROL_DD_GH_DED");
	    	  		 
	    	  		 try {
	    	  			 serviceResults = dispatcher.runSync("createInvoiceItem", inputItemCtx);
	    	  			 if (ServiceUtil.isError(serviceResults)) {
	    	  				 return ServiceUtil.returnError("Unable to create Invoice Item");
	    	  			 }
	    	  		 } catch (GenericServiceException e) {
	    	  			 Debug.logError(e, e.toString(), module);
	    	  			 return ServiceUtil.returnError(e.toString());
	    	  		 }
	    	  		 
	    	  		 
	    	  		 try {
	    	  			 serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId",	"INVOICE_APPROVED", "userLogin", userLogin));
	    	  			 if (ServiceUtil.isError(serviceResults)) {
	    	  				 return ServiceUtil.returnError("Unable to set Invoice Status",null, null, serviceResults);
	    	  			 }
	    	  			 serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId","INVOICE_READY", "userLogin", userLogin));
	    	  			 if (ServiceUtil.isError(serviceResults)) {
	    	  				 return ServiceUtil.returnError("Unable to set Invoice Status",null, null, serviceResults);
	    	  			 }
	    	  		 } catch (GenericServiceException e) {
	    	  			 Debug.logError(e, e.toString(), module);
	    	  			 return ServiceUtil.returnError(e.toString());
	    	  		 }
	                
	    	  		 Map newp = UtilMisc.toMap("userLogin",userLogin);
	    	  		 newp.put("partyIdFrom", invoice.getString("partyId"));
	    	  		 newp.put("partyIdTo", invoice.getString("partyIdFrom"));
	    	  		 newp.put("paymentMethodId", "DEBITNOTE");
	    	  		 newp.put("paymentTypeId", "MIS_INCOME_PAYIN");
	    	  		 newp.put("statusId", "PMNT_NOT_PAID");
	    	  		 newp.put("paymentRefNum", invoice.getString("referenceNumber"));
	    	  		 newp.put("amount", totalAmount);
	    	  		 Map<String, Object> paymentResult = dispatcher.runSync("createPayment",newp);
	    	  		 //Debug.log("paymentResult================="+paymentResult);
	    	  		 if (ServiceUtil.isError(paymentResult)) {
	    	       		return ServiceUtil.returnError("Unable to Create Payment");
	    	  		 }
	                
	    	  		 Map<String, Object> setPaymentStatusMap = UtilMisc.<String, Object>toMap("userLogin", userLogin);
	    	  		 setPaymentStatusMap.put("paymentId", paymentResult.get("paymentId"));
	    	  		 setPaymentStatusMap.put("statusId", "PMNT_RECEIVED");
	    	  		 Map<String, Object> pmntResults = dispatcher.runSync("setPaymentStatus", setPaymentStatusMap);
	    	       	
	    	  		 Map newPayappl = UtilMisc.toMap("userLogin",userLogin);
	    	  		 newPayappl.put("invoiceId", invoiceId);
	    	  		 newPayappl.put("paymentId", paymentResult.get("paymentId"));
	    	  		 newPayappl.put("amountApplied", totalAmount);
	    	  		 Map<String, Object> paymentApplResult = dispatcher.runSync("createPaymentApplication",newPayappl);
	    	  		 if (ServiceUtil.isError(paymentApplResult)) {
	    	  			 return ServiceUtil.returnError("Unable to Create Payment Application");
	    	  		 }
				 }
			}
		}catch(GenericEntityException e) {
			Debug.logError(e, module);
	        return ServiceUtil.returnError("Unable to create payroll Invoice record");			
		}		
		catch (GenericServiceException e) {
			Debug.logError(e, errorMsg + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + e.getMessage());
	    }
		//serviceResults.put("invoices", invoiceList);
		return ServiceUtil.returnSuccess();      
	}
 
}




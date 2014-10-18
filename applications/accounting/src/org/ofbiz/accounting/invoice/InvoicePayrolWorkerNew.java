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
	        			return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
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
	        			Debug.logError(ServiceUtil.getErrorMessage(serviceResults), module);
	        			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResults), null, null, serviceResults);
	        		}
	        		Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
	                invoiceCtx.put("userLogin", userLogin);
	                invoiceCtx.put("statusId","INVOICE_APPROVED");
                	Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
                	if (ServiceUtil.isError(invoiceResult)) {
                		Debug.logError(invoiceResult.toString(), module);
                        return ServiceUtil.returnError(null, null, null, invoiceResult);
                    }
                	Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
                	paymentCtx.put("userLogin", userLogin);
                	Map<String, Object> paymentResult = dispatcher.runSync("createPayrolPaymentAndAppclications",paymentCtx);
                	if (ServiceUtil.isError(paymentResult)) {
                		Debug.logError(paymentResult.toString(), module);
                        return ServiceUtil.returnError(ServiceUtil.getErrorMessage(paymentResult), null, null, paymentResult);
                    }
	        		invoiceList.add(invoice);
	        		emplCounter++;
               		if ((emplCounter % 20) == 0) {
               			elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
               			Debug.logImportant("Completed " + emplCounter + " employee [ in " + elapsedSeconds + " seconds]", module);
               		}
	        	}
		}
		catch(GenericEntityException e) {
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
 
 
 public static Map<String, Object> createPayrolPaymentAndAppclications(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
		String invoiceId = (String) context.get("invoiceId");
			
       String errorMsg = "create Payment for PayrolInvoice failed for invoiceId '" + invoiceId + "': ";			
       GenericValue userLogin = (GenericValue) context.get("userLogin");            
		Map<String, Object> serviceResults= ServiceUtil.returnSuccess();
		// Create invoice items
		try {
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			List<GenericValue> paymentApplication = delegator.findByAnd("PaymentApplication", UtilMisc.toMap("invoiceId",invoiceId));
			//only generate payment if no application exist yet
			if(UtilValidate.isEmpty(paymentApplication)){
				 Map newp = UtilMisc.toMap("userLogin",userLogin);
				 newp.put("partyIdFrom", invoice.getString("partyId"));
				 newp.put("partyIdTo", invoice.getString("partyIdFrom"));
				 newp.put("paymentMethodTypeId", "CHEQUE_PAYIN");
				 newp.put("paymentTypeId", "PAYROL_PAYMENT");
				 newp.put("statusId", "PMNT_NOT_PAID");
				 BigDecimal amount = InvoiceWorker.getInvoiceTotal(invoice);
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
				
			}
			
	        			
		}catch (Exception e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records: " + e.getMessage());
	    }   		
				
		return serviceResults;   	
	}
 
}




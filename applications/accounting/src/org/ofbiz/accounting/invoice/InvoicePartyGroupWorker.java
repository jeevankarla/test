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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * InvoicePayrolWorker - Worker service methods for payroll invoices
 */
public class InvoicePartyGroupWorker {
	public static String module = InvoicePartyGroupWorker.class.getName();

    private static Map<String, Object> getPartyGroupInvoiceRateTypes(DispatchContext dctx, Map<String, Object> context) 
    throws GenericEntityException {
    	String invoiceTypeId = (String) context.get("invoiceTypeId");
        Delegator delegator = dctx.getDelegator();
		List<GenericValue> resultList = FastList.newInstance();        
		List<GenericValue> mapItems = delegator.findByAndCache("InvoiceItemTypeMap",
				UtilMisc.toMap("invoiceTypeId", invoiceTypeId));
		if (mapItems.size() == 0) {
        	return ServiceUtil.returnError("invoice type '" + invoiceTypeId + "' is not mapped to any invoice item types", 
        			null, null, null);	
		}
		// now check if these item types have corresponding rate types, else raise error
		for (int i = 0; i < mapItems.size(); ++i) {		
			GenericValue mapItem = mapItems.get(i);
			String itemType = mapItem.getString("invoiceItemTypeId");
			List<GenericValue> rateTypes = delegator.findByAndCache("RateType",
					UtilMisc.toMap("invoiceItemTypeId", itemType));
			if (rateTypes.size() != 1) {
            	return ServiceUtil.returnError("invoiceItemType '" + itemType + "' not properly associated with RateType", 
            			null, null, null);				
			}
			resultList.add(rateTypes.get(0));
		}
		
		Map<String, Object> response = ServiceUtil.returnSuccess();
	    response.put("rateTypes", resultList);	   
	    return response;
    }

    public static GenericValue getPartyRateForRateType(List<GenericValue> partyRates, String rateType) {
    	GenericValue result = null;
    	for (int i = 0; i < partyRates.size(); ++i) {		
			GenericValue partyRate = partyRates.get(i);
			if (rateType.equals(partyRate.getString("rateTypeId"))) {
				return partyRate;
			}
		}
    	return result;
    }
	
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createPartyGroupInvoiceItems(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
		String invoiceId = (String) context.get("invoiceId");
		String invoiceTypeId = (String) context.get("invoiceTypeId");			
		String partyId = (String) context.get("partyId");	
        String errorMsg = "createPartyGroupInvoiceItems failed for party '" + partyId + 
        	"'; invoice=[" + invoiceId + "," + invoiceTypeId + "]: ";		
        GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> serviceResults;
		
		try {			
			serviceResults = getPartyGroupInvoiceRateTypes(dctx, context);
	        if (ServiceUtil.isError(serviceResults)) {
	        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
	        }
	        List<GenericValue> rateTypes = (List<GenericValue>)serviceResults.get("rateTypes");
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			List conditionList = UtilMisc.toList(
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp)));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  					
			List<GenericValue> partyRates = delegator.findList("PartyRate",condition, null, null, null, false);
			for (int i = 0; i < rateTypes.size(); ++i) {
				GenericValue rateTypeRow = rateTypes.get(i);
				String rateTypeId = rateTypeRow.getString("rateTypeId");
				GenericValue partyRate = getPartyRateForRateType(partyRates, rateTypeId);
				if (partyRate == null) {
	                return ServiceUtil.returnError(errorMsg + "rateType '" + rateTypeId + "' missing for party");					
				}
				String periodTypeId = partyRate.getString("periodTypeId");							
				Map input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);	
				input.put("invoiceId", invoiceId);
				String invoiceItemTypeId = rateTypeRow.getString("invoiceItemTypeId");
				input.put("invoiceItemTypeId", invoiceItemTypeId);
				input.put("description", InvoicePayrolWorker.fetchInvoiceItemTypeDescription(dctx, invoiceItemTypeId));

				Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin,"partyId", partyId);
				inputRateAmt.put("rateTypeId", rateTypeId);
				inputRateAmt.put("periodTypeId", periodTypeId);	
		        inputRateAmt.put("rateCurrencyUomId", context.get("currencyUomId"));				
	            serviceResults = dispatcher.runSync("getRateAmount", inputRateAmt);
	            if (ServiceUtil.isError(serviceResults)) {
	                return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
	            }		
	            BigDecimal quantity = BigDecimal.ONE;
				input.put("quantity", quantity);
				input.put("amount", serviceResults.get("rateAmount"));
	            serviceResults = dispatcher.runSync("createInvoiceItem", input);
	            if (ServiceUtil.isError(serviceResults)) {
	                return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
	            }					
			}
				
		}
		catch(GenericEntityException e) {
			Debug.logError(e, errorMsg + "Unable to create PartyGroup InvoiceItem records: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create PartyGroup InvoiceItem records: " + e.getMessage());			
		}				
		catch (GenericServiceException e) {
			Debug.logError(e, errorMsg + "Unable to create PartyGroup InvoiceItem records: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create PartyGroup InvoiceItem records: " + e.getMessage());
	    }		
		return ServiceUtil.returnSuccess();   	
	}
	
	public static Map<String, Object> createPartyGroupInvoice(DispatchContext dctx, Map<String, Object> context) {
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher(); 
		String partyIdFrom = (String) context.get("partyIdFrom");	
		String partyClassificationGroupId = (String) context.get("partyClassificationGroupId");	
	    String errorMsg = "createPartyGroupInvoice failed [" + partyIdFrom + "-->" + partyClassificationGroupId + "]";  		
        Map<String, Object> serviceResults = ServiceUtil.returnError(errorMsg, null, null, null);        
		List<GenericValue> invoiceList = FastList.newInstance();	        
		try {  
			serviceResults = getPartyGroupInvoiceRateTypes(dctx, context);
	        if (ServiceUtil.isError(serviceResults)) {
	        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
	        }  			
			serviceResults = InvoiceWorker.fetchTimePeriodDetails(dctx, context);
	        if (ServiceUtil.isError(serviceResults)) {
				return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
			}  
	        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
	        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");	        
            SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");	        	
	        
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			GenericValue partyClassGroupRow = delegator.findOne("PartyClassificationGroup", UtilMisc.toMap(
    			"partyClassificationGroupId", partyClassificationGroupId), false);
			String invoiceDesc = partyClassGroupRow.getString("description") + " [" 
					+ sd.format(UtilDateTime.toCalendar(timePeriodStart).getTime()) 
			        + " - " + sd.format(UtilDateTime.toCalendar(timePeriodEnd).getTime()) + "]";
	        Map input = UtilMisc.toMap("userLogin", context.get("userLogin"));
	        input.put("invoiceTypeId", context.get("invoiceTypeId"));        
	        input.put("partyIdFrom", partyIdFrom);	
	        input.put("statusId", context.get("statusId"));	
	        input.put("currencyUomId", context.get("currencyUomId"));
	        input.put("dueDate", context.get("dueDate")); 	        
	        input.put("description", invoiceDesc);	
	        input.put("timePeriodId", (String)context.get("timePeriodId")); 	        
	        input.put("isPartyGroupInvoice", "Y");	        
			List conditionList = UtilMisc.toList(
					EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, partyClassificationGroupId));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp)));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
			List<GenericValue> parties = delegator.findList("PartyClassification", condition, null, null, null, false);
			for (int i = 0; i < parties.size(); ++i) {		

				GenericValue party = parties.get(i);			
		        input.put("partyId", party.getString("partyId"));
				serviceResults = dispatcher.runSync("createInvoice", input);
				if (ServiceUtil.isError(serviceResults)) {
					return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
				}	        
				String invoiceId = (String)serviceResults.get("invoiceId");
				GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap(
	        			"invoiceId", invoiceId), false);
				invoiceList.add(invoice);
			}
		}
		catch(GenericEntityException e) {
			Debug.logError(e, errorMsg + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + e.getMessage());			
		}		
		catch (GenericServiceException e) {
			Debug.logError(e, errorMsg + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + e.getMessage());
	    }	
		serviceResults.clear();
		serviceResults.put("invoices", invoiceList);
		return serviceResults;      
	}
}




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
 * InvoicePayrolWorker - Worker service methods for payroll invoices
 */
public class InvoicePayrolWorker {
	public static String module = InvoicePayrolWorker.class.getName();

	private static double fetchBasicSalaryInternal(DispatchContext dctx, String partyId) {
		double result = 0;
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        Delegator delegator = dctx.getDelegator();
        try {
            List conditionList = UtilMisc.toList(
                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
            conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
            conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
            EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);        	
            // sort by -fromDate to get the newest (largest) first, just in case there is more than one, should not happen but...
            List<GenericValue> payHistory = delegator.findList("PayHistory", condition, null, UtilMisc.toList("-fromDate"), null, false);
            if (payHistory.size() > 0) {
            	GenericValue row = payHistory.get(0);
            	String payGradeId = row.getString("payGradeId");
            	String salaryStepSeqId = row.getString("salaryStepSeqId");
            	GenericValue salaryStep = delegator.findOne("SalaryStep", UtilMisc.toMap(
            			"payGradeId", payGradeId, "salaryStepSeqId", salaryStepSeqId), false);
                if (salaryStep != null) {
                    result = salaryStep.getDouble("amount");
                }
                else {
    	            Debug.logWarning("Zero SalaryStep records for partyId '" + partyId + "'", module);
                }	
            }
            else {
                Debug.logWarning("Zero PayHistory records for partyId '" + partyId + "'", module);            	
            }
        }
        catch (GenericEntityException e) {
            Debug.logError(e, "Error retrieving PayHistory records for partyId '" + partyId + "'", module);
        }        
        return result;
	}
	
	private static double calculateBenefitAmount(DispatchContext dctx, String benefitTypeId, String partyId) {
		double result = 0;

        Delegator delegator = dctx.getDelegator();
        // fetch the formula associated with this benefit, if it exists
        try {
        	GenericValue benefitTypeRow = delegator.findOne("BenefitType", UtilMisc.toMap(
        			"benefitTypeId", benefitTypeId), false);
            if (benefitTypeRow != null) {
                String formulaId = benefitTypeRow.getString("acctgFormulaId");
                if (formulaId == null) {
                	// nothing more to do
                	return result;
                }
                double basicSalary = fetchBasicSalaryInternal(dctx, partyId);
        		Evaluator evltr = new Evaluator(dctx);
        		evltr.setFormulaIdAndSlabAmount(formulaId, basicSalary);
				HashMap<String, Double> variables = new HashMap<String, Double>();
				variables.put("BASIC", basicSalary);
				evltr.addVariableValues(variables);        		
				result = evltr.evaluate();
            }
            else {
	            Debug.logWarning("Zero BenefitType records for benefitTypeId '" + 
	            		benefitTypeId + "'", module);
            }	
        }
        catch (GenericEntityException e) {
            Debug.logError(e, "Error retrieving BenefitType record for benefitTypeId '" + 
            		benefitTypeId + "'", module);
        }
		return result;
	}
	
	private static double calculateDeductionAmount(DispatchContext dctx, String deductionTypeId, String partyId) {
		double result = 0;

        Delegator delegator = dctx.getDelegator();
        // fetch the formula associated with this benefit, if it exists
        try {
        	GenericValue deductionTypeRow = delegator.findOne("DeductionType", UtilMisc.toMap(
        			"deductionTypeId", deductionTypeId), false);
            if (deductionTypeRow != null) {
                String formulaId = deductionTypeRow.getString("acctgFormulaId");
                if (formulaId == null) {
                	// nothing more to do
                	return result;
                }
                double basicSalary = fetchBasicSalaryInternal(dctx, partyId);
        		Evaluator evltr = new Evaluator(dctx);
        		evltr.setFormulaIdAndSlabAmount(formulaId, basicSalary);
				HashMap<String, Double> variables = new HashMap<String, Double>();
				variables.put("BASIC", basicSalary);
				evltr.addVariableValues(variables);        		
				result = evltr.evaluate();
            }
            else {
	            Debug.logWarning("Zero DeductionType records for deductionTypeId '" + 
	            		deductionTypeId + "'", module);
            }	
        }
        catch (GenericEntityException e) {
            Debug.logError(e, "Error retrieving BenefitType record for benefitTypeId '" + 
            		deductionTypeId + "'", module);
        }
		return result;
	}	
	
	public static Map<String, Object> calculatePayrolInvoiceItemAmount(DispatchContext dctx, Map<String, Object> context) {
		String invoiceItemTypeId = (String) context.get("invoiceItemTypeId");
		String partyId = (String) context.get("partyId");	
Debug.logInfo("====> invoiceItemTypeId=" + invoiceItemTypeId + ";partyId=" + partyId, module);
		double result = 0;
		if (invoiceItemTypeId.startsWith("PAYROL_BEN")) {
			result = calculateBenefitAmount(dctx, invoiceItemTypeId, partyId);
		}
		else if (invoiceItemTypeId.startsWith("PAYROL_DD")) {
			result = calculateDeductionAmount(dctx, invoiceItemTypeId, partyId);			
		}	
		else {
			String errMsg = "Unknown payroll invoice item type ==> invoiceItemTypeId=" + 
				invoiceItemTypeId + ";partyIdFrom=" + partyId;
			Debug.logWarning(errMsg, module);
            return ServiceUtil.returnError(errMsg);
		}
		Map<String, Object> response = ServiceUtil.returnSuccess();
	    response.put("amount", result);	   
	    return response;
	}
	
	public static Map<String, Object> fetchBasicSalary(DispatchContext dctx, Map<String, Object> context) {
		String partyId = (String) context.get("partyId");	
		Map<String, Object> response = ServiceUtil.returnSuccess();
	    response.put("amount", fetchBasicSalaryInternal(dctx, partyId));	   
	    return response;	
	}

	public static String fetchInvoiceItemTypeDescription(DispatchContext dctx, String itemTypeId) 
	throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        String result = "";
        Debug.logInfo("InvoiceItemType description does not exist for " +
        		"invoiceItemTypeId '" + itemTypeId + "'", module);
    	GenericValue itemTypeRow = delegator.findOne("InvoiceItemType", UtilMisc.toMap(
    			"invoiceItemTypeId", itemTypeId), false);
        if (itemTypeRow != null) {
            String description = itemTypeRow.getString("description");
            result = description;
            if (description == null) {
            	// log warning, nothing more to do
	            Debug.logWarning("InvoiceItemType description does not exist for " +
	            		"invoiceItemTypeId '" + itemTypeId + "'", module);                	
            	return result;
            }
        }
        else {
            Debug.logWarning("Zero InvoiceItemType records for invoiceItemTypeId '" + 
            		itemTypeId + "'", module);
        }	
        return result;
	}	


	/**
	 * Adjusts for partial duration and loss of pay days.
	 * Assumption: It's assumed that the amount validity period overlaps with the input HR time period
	 * @param context
	 * @param amount
	 * @param amountValidityStart
	 * @param amountValidityEnd
	 * @return
	 */
	private static Map<String, Object> adjustAmount(Map<String, Object> context, BigDecimal amount, Timestamp amountValidityStart, Timestamp amountValidityEnd) {
        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
        String proportionalFlagStr = (String)context.get("proportionalFlag");
        boolean propFlag = 
        	(proportionalFlagStr != null && "N".equals(proportionalFlagStr))? false: true;
        TimeDuration payrollPeriod = new TimeDuration(UtilDateTime.toCalendar(timePeriodStart),
        		UtilDateTime.toCalendar(timePeriodEnd));        
        TimeDuration period = payrollPeriod;
		if (propFlag && amountValidityStart.compareTo(timePeriodStart) > 0) {
			if ((amountValidityEnd != null) && (amountValidityEnd.compareTo(timePeriodEnd) < 0)) {
				period = new TimeDuration(UtilDateTime.toCalendar(amountValidityStart),
		        		UtilDateTime.toCalendar(amountValidityEnd));
			}
			else {
				period = new TimeDuration(UtilDateTime.toCalendar(amountValidityStart),
		        		UtilDateTime.toCalendar(timePeriodEnd));				
			}
		}
		else if (propFlag && ((amountValidityEnd != null) && (amountValidityEnd.compareTo(timePeriodEnd) < 0))) {
			if (amountValidityStart.compareTo(timePeriodStart) > 0) {
				period = new TimeDuration(UtilDateTime.toCalendar(amountValidityStart),
		        		UtilDateTime.toCalendar(amountValidityEnd));
			}
			else {
				period = new TimeDuration(UtilDateTime.toCalendar(timePeriodStart),
		        		UtilDateTime.toCalendar(amountValidityEnd));				
			}
		}
		BigDecimal periodDays;
		int payrollPeriodDays;
		periodDays = BigDecimal.valueOf(period.days() + 1); // to include the end day as well	
		payrollPeriodDays = payrollPeriod.days() + 1; // to include the end day as well			
		//amount = amount.divide(BigDecimal.valueOf(payrollPeriodDays), 2, BigDecimal.ROUND_HALF_UP);	//::TODO:: re-visit	
		if (propFlag) {
			// loss of pay days adjustment
			BigDecimal lossOfPayDays = (BigDecimal)context.get("lossOfPayDays");
			BigDecimal payDays = periodDays.subtract(lossOfPayDays);
			amount = amount.multiply(payDays).divide(BigDecimal.valueOf(payrollPeriodDays), 0, BigDecimal.ROUND_HALF_UP);
		}
		Map result = UtilMisc.toMap("amount", amount);
		result.put("quantity", BigDecimal.ONE);	
//Debug.logInfo("==========>periodDays=" + periodDays, module);	
//Debug.logInfo("==========>adjusted amount=" + amount, module);	
		return result;
	}
	
	private static Map<String, Object> createPayrolBenefitInvoiceItems(DispatchContext dctx, Map<String, Object> context) 
		throws GenericServiceException,GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String errorMsg = "createPayrolBenefitInvoiceItems failed";
		String invoiceId = (String) context.get("invoiceId");	
		String partyId = (String) context.get("partyId");	
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");        
		Map<String, Object> serviceResults;
		List conditionList = UtilMisc.toList(
                EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
		List<GenericValue> partyBenefits = delegator.findList("PartyBenefit", condition, null, null, null, false);
		for (int i = 0; i < partyBenefits.size(); ++i) {		
			GenericValue benefit = partyBenefits.get(i);
			String benefitTypeId = benefit.getString("benefitTypeId");				
			Map input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);				
			input.put("invoiceItemTypeId", benefitTypeId);			  
			Timestamp from = benefit.getTimestamp("fromDate");
			Timestamp thru = benefit.getTimestamp("thruDate");
			Map<String, Object> adjustment = adjustAmount(context,benefit.getBigDecimal("cost"), from, thru);			
			input.put("quantity", adjustment.get("quantity"));
			input.put("amount", adjustment.get("amount"));
            serviceResults = dispatcher.runSync("createInvoiceItem", input);
            if (ServiceUtil.isError(serviceResults)) {
                return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
            }				
		}   
		return ServiceUtil.returnSuccess(); 
	}
	
	
	private static Map<String, Object> createPayrolDeductionInvoiceItems(DispatchContext dctx, Map<String, Object> context) 
		throws GenericServiceException,GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String errorMsg = "createPayrolDeductionInvoiceItems failed";
		String invoiceId = (String) context.get("invoiceId");	
		String partyId = (String) context.get("partyId");	
        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd"); 		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> serviceResults;
		List conditionList = UtilMisc.toList(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  				
		List<GenericValue> partyDeductions = delegator.findList("PayrollPreference",condition, null, null, null, false);
		for (int i = 0; i < partyDeductions.size(); ++i) {		
			GenericValue deduction = partyDeductions.get(i);
			Map input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);	
			input.put("invoiceId", invoiceId);
			String deductionTypeId = deduction.getString("deductionTypeId");
			input.put("invoiceItemTypeId", deductionTypeId);			
        	GenericValue deductionTypeRow = delegator.findOne("DeductionType", UtilMisc.toMap(
        			"deductionTypeId", deductionTypeId), false);
        	context.put("proportionalFlag", deductionTypeRow.getString("proportionalFlag"));
			Timestamp from = deduction.getTimestamp("fromDate");
			Timestamp thru = deduction.getTimestamp("thruDate");
			Map<String, Object> adjustment = adjustAmount(context,deduction.getBigDecimal("flatAmount"), from, thru);			
			input.put("quantity", adjustment.get("quantity"));
			BigDecimal amount = (BigDecimal)adjustment.get("amount");
			input.put("amount", amount.negate());
            serviceResults = dispatcher.runSync("createInvoiceItem", input);
            if (ServiceUtil.isError(serviceResults)) {
                return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
            }					
		}	
		return ServiceUtil.returnSuccess(); 		
	}

	/**
	 * Typically this should result in one invoice.  But in certain cases such as pay hikes
	 * during the period, there could be more than 1 item.
	 * @param dctx
	 * @param context
	 * @return
	 * @throws GenericServiceException
	 * @throws GenericEntityException
	 */
	private static Map<String, Object> createPayrolBasicSalaryInvoiceItems(DispatchContext dctx, Map<String, Object> context) 
	throws GenericServiceException,GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String errorMsg = "createPayrolBasicSalaryInvoiceItems failed";
		String invoiceId = (String) context.get("invoiceId");	
		String partyId = (String) context.get("partyId");	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
		Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");        
		Map<String, Object> serviceResults;
		List conditionList = UtilMisc.toList(
                EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
        List<GenericValue> payHistory = delegator.findList("PayHistory", condition, null, null, null, false);
        for (int i = 0; i < payHistory.size(); ++i) {
        	GenericValue row = payHistory.get(i);
        	String payGradeId = row.getString("payGradeId");
        	String salaryStepSeqId = row.getString("salaryStepSeqId");
        	GenericValue salaryStep = delegator.findOne("SalaryStep", UtilMisc.toMap(
        			"payGradeId", payGradeId, "salaryStepSeqId", salaryStepSeqId), false);			
            Map input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);
            input.put("invoiceItemTypeId", "PAYROL_BEN_SALARY");              
            Timestamp from = row.getTimestamp("fromDate");
            Timestamp thru = row.getTimestamp("thruDate");
            Map<String, Object> adjustment = adjustAmount(context,salaryStep.getBigDecimal("amount"), from, thru);			
            input.put("quantity", adjustment.get("quantity"));
            input.put("amount", adjustment.get("amount"));
            serviceResults = dispatcher.runSync("createInvoiceItem", input);
            if (ServiceUtil.isError(serviceResults)) {
            	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
            }
        } 
        return ServiceUtil.returnSuccess(); 
	}	

	private static Map<String, Object> fetchLossOfPayDays(DispatchContext dctx, Map<String, Object> context) 
	throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String errorMsg = "fetchLossOfPayDays failed";
		String partyId = (String) context.get("partyId");	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
		Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");        
		Map<String, Object> serviceResults;
		BigDecimal lossOfPayDays = BigDecimal.ZERO;
		List conditionList = UtilMisc.toList(
            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
    	EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
		List<GenericValue> leaves = delegator.findList("EmplLeave", condition, null, null, null, false);
		for (int i = 0; i < leaves.size(); ++i) {		
			GenericValue leave = leaves.get(i);
            Timestamp from = leave.getTimestamp("fromDate");
            Timestamp thru = leave.getTimestamp("thruDate");			
			if (from.compareTo(timePeriodStart) < 0 || thru.compareTo(timePeriodEnd) > 0) {
            	return ServiceUtil.returnError(errorMsg + ": lossOfPayDays cannot span multiple payroll periods", 
            			null, null, null);				
			}
			BigDecimal temp = leave.getBigDecimal("lossOfPayDays");		
			lossOfPayDays = lossOfPayDays.add(temp);							
		}   
		context.put("lossOfPayDays", lossOfPayDays);
        return ServiceUtil.returnSuccess(); 
	}	
	
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
		String partyId = (String) context.get("partyId");		
        String errorMsg = "createPayrolInvoiceItems failed for party '" + partyId + "': ";			
        GenericValue userLogin = (GenericValue) context.get("userLogin");            
		Map<String, Object> serviceResults;
		try {
			serviceResults = fetchPayrolBoundary(dctx, context);
			if (ServiceUtil.isError(serviceResults)) {
				return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
			}  
		}
		catch (Exception e) {
			Debug.logError(e, errorMsg + "Unable to payroll boundary needed to generate payroll: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to payroll boundary needed to generate payroll: " + e.getMessage());
	    } 			
        // get the begin and end timestamps for this payroll month         
        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");	
        Debug.logInfo("==========>timePeriodStart=" + timePeriodStart.toString(), module);     
        Debug.logInfo("==========>timePeriodEnd=" + timePeriodEnd.toString(), module);   
        context.put("timePeriodStart", timePeriodStart);
        context.put("timePeriodEnd", timePeriodEnd);        
		// First check for any loss of pay days
		try {
			serviceResults = fetchLossOfPayDays(dctx, context);
	        if (ServiceUtil.isError(serviceResults)) {
	        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
	        }			
		} 
		catch(GenericEntityException e) {
			Debug.logError(e, errorMsg + "Unable to fetch loss of pay days needed to generate payroll: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to fetch loss of pay days needed to generate payroll");			
		}
		catch (Exception e) {
			Debug.logError(e, errorMsg + "Unable to fetch loss of pay days needed to generate payroll: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to fetch loss of pay days needed to generate payroll: " + e.getMessage());
	    }   		
Debug.logInfo("==========>lossOfPayDays=" + context.get("lossOfPayDays"), module);   		
		// First, lets generate invoice item(s) for basic salary
		try {
			serviceResults = createPayrolBasicSalaryInvoiceItems(dctx, context);
	        if (ServiceUtil.isError(serviceResults)) {
	        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
	        }			
		} 
		catch(GenericEntityException e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for basic salary: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for basic salary: " + e.getMessage());			
		}			
		catch (GenericServiceException e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for basic salary: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for basic salary: " + e.getMessage());
	    }
		catch (Exception e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for basic salary: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for basic salary: " + e.getMessage());
	    }        
		// Create invoice items for benefits
		try {
			serviceResults = createPayrolBenefitInvoiceItems(dctx, context);
	        if (ServiceUtil.isError(serviceResults)) {
	        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
	        }				
		}
		catch(GenericEntityException e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage());			
		}	
		catch (GenericServiceException e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage());
	    }	
		catch (Exception e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage());
	    }   		

		// Create invoice items for deductions
		try {
			serviceResults = createPayrolDeductionInvoiceItems(dctx, context);
	        if (ServiceUtil.isError(serviceResults)) {
	        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
	        }	
		}
		catch(GenericEntityException e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for deductions: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for deductions: " + e.getMessage());			
		}				
		catch (GenericServiceException e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for deductions: " + e.getMessage());
	    }
		catch (Exception e) {
			Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage(), module);
	        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for deductions: " + e.getMessage());
	    }   		
		return ServiceUtil.returnSuccess();   	
	}
	
	private static Map<String, Object> fetchPayrolBoundary(DispatchContext dctx, Map<String, Object> context) 
	throws GenericEntityException {
		Map<String, Object> serviceResults;		
		serviceResults = InvoiceWorker.fetchTimePeriodDetails(dctx, context);
    	if (!ServiceUtil.isError(serviceResults) && !"HR_MONTH".equals((String)context.get("periodTypeId"))) {
    		return ServiceUtil.returnError("fetchPayrolBoundary failed: Invalid period type", null, null, null);
    	}			     
        return serviceResults; 
	}			
	
	public static Map<String, Object> createPayrolInvoice(DispatchContext dctx, Map<String, Object> context) {
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();  
		String partyId = (String) context.get("partyId");	
		String partyIdFrom = (String) context.get("partyIdFrom");	
        String timePeriodId = (String)context.get("timePeriodId");		
	    String errorMsg = "createPayrolInvoice failed [" + partyIdFrom + "-->" + partyId + "]"; 		
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = TimeZone.getDefault();// ::TODO   		
        Map<String, Object> serviceResults = ServiceUtil.returnError(errorMsg, null, null, null);    
		List<GenericValue> invoiceList = FastList.newInstance();
        Map input = UtilMisc.toMap("userLogin", context.get("userLogin"));
        input.put("invoiceTypeId", context.get("invoiceTypeId"));        
        input.put("partyId", partyId);	
        input.put("partyIdFrom", partyIdFrom);	
        input.put("statusId", context.get("statusId"));	
        input.put("currencyUomId", context.get("currencyUomId"));	
        input.put("dueDate", context.get("dueDate"));  
        Map inputTP = UtilMisc.toMap("invoiceTypeId", context.get("invoiceTypeId"));        		
        inputTP.put("statusId", context.get("statusId"));	
        inputTP.put("timePeriodId", timePeriodId);	        
		try {   
			serviceResults = fetchPayrolBoundary(dctx, context);
	        if (ServiceUtil.isError(serviceResults)) {
				return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
			}   
	        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
	        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
	        input.put("timePeriodId", timePeriodId); 
	        input.put("invoiceAttrValue", timePeriodId);
	        input.put("invoiceAttrName", "TIME_PERIOD_ID");
            SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");	        
	        input.put("description", "Payroll [" + sd.format(UtilDateTime.toCalendar(timePeriodStart).getTime()) 
	        		+ " - " + sd.format(UtilDateTime.toCalendar(timePeriodEnd).getTime()) + "]");	
	        boolean isGroup = true;
            GenericValue partyFromRole = delegator.findByPrimaryKeyCache("PartyRole", UtilMisc.toMap("partyId", partyIdFrom, "roleTypeId", "INTERNAL_ORGANIZATIO"));
            if (UtilValidate.isEmpty(partyFromRole)) {
                isGroup = false;
            }	        

			if (!isGroup) {
				List conditionList = UtilMisc.toList(
						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdFrom));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
			    EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
				List<GenericValue> employments = delegator.findList("Employment", condition, null, null, null, false);	
				if (employments.size() == 0)
				{
					return ServiceUtil.returnError("No employment record found for party " + partyIdFrom, 
							null, null, serviceResults);					
				}        		        
				serviceResults = dispatcher.runSync("createInvoice", input);
				if (ServiceUtil.isError(serviceResults)) {
					return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
				}
				return serviceResults;
			}
			
			// First we get any child orgs that need to be rolled up and then traverse through all the employments
			// for the orgs.
	        List<String> internalOrgs = PartyWorker.getAssociatedPartyIdsByRelationshipType(delegator, partyIdFrom, "GROUP_ROLLUP");
	        if (internalOrgs == null) {
	        	internalOrgs = FastList.newInstance();
	        }
	        internalOrgs.add(partyIdFrom);
	        for (String internalOrg : internalOrgs) {
	        	List conditionList = UtilMisc.toList(
	        			EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, internalOrg));
	        	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
	        	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
	        			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
	        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
	        	List<GenericValue> employments = delegator.findList("Employment", condition, null, null, null, false);
	        	for (int i = 0; i < employments.size(); ++i) {		
	        		GenericValue employment = employments.get(i);
	        		input.put("partyIdFrom", employment.getString("partyIdTo"));
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
		serviceResults.put("invoices", invoiceList);
		return serviceResults;      
	}
}




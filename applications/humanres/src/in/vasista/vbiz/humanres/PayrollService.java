package in.vasista.vbiz.humanres;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;


import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.util.formula.Evaluator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.TimeDuration;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;

public class PayrollService {
	
				public static final String module = PayrollService.class.getName();
				
			public static Map<String, Object> createPayrollBilling(DispatchContext dctx, Map<String, Object> context) throws Exception{
				GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String partyIdFrom= (String) context.get("orgPartyId");
				String customTimePeriodId= (String) context.get("customTimePeriodId");				 
				String periodBillingId = null;
				String billingTypeId = "PAYROLL_BILL";			
				List conditionList = FastList.newInstance();
		        List periodBillingList = FastList.newInstance();
		        String partyId = (String) context.get("partyId");
		        if(UtilValidate.isEmpty(partyId)){
					partyId = "Company";
				}
		        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS")));
		        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
		    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , billingTypeId));
		    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		    	try {
		    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
		    		if(!UtilValidate.isEmpty(periodBillingList)){
		    			Debug.logError("Failed to create 'MarginReport': Already generated or In-process for the specified period", module);
		    			return ServiceUtil.returnError("Failed to create 'MarginReport': Already generated or In-process for the specified period");
		    		}
		    	}catch (GenericEntityException e) {
		    		 Debug.logError(e, module);             
		             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
				} 
		    	
		    	GenericValue newEntity = delegator.makeValue("PeriodBilling");
		        newEntity.set("billingTypeId", billingTypeId);
		        newEntity.set("customTimePeriodId", customTimePeriodId);
		        newEntity.set("statusId", "IN_PROCESS");
		        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
		        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
		        newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			    try {     
			        delegator.createSetNextSeqId(newEntity);
					periodBillingId = (String) newEntity.get("periodBillingId");	
					Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"userLogin", userLogin);
					runSACOContext.put("partyIdFrom", partyIdFrom);
					runSACOContext.put("partyId", partyId);
					dispatcher.runAsync("generatePayrollBilling", runSACOContext);
					if(ServiceUtil.isError(result)){
       					Debug.logError("Problems in service Parol Header", module);
			  			return ServiceUtil.returnError("Problems in service Parol Header");
       				}
		    	} catch (GenericEntityException e) {
					Debug.logError(e,"Failed To Create New Period_Billing", module);
					return ServiceUtil.returnError("Problems in service Parol Header");
				}
		        catch (GenericServiceException e) {
		            Debug.logError(e, "Error in calling 'generateVendorMargin' service", module);
		            return ServiceUtil.returnError(e.getMessage());
		        } 
		        result.put("periodBillingId", periodBillingId);
		        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		    	return result;
		
			}
			public static Map<String, Object> generatePayrollBilling(DispatchContext dctx, Map<String, Object> context) throws Exception{
				GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String partyIdFrom= (String) context.get("partyIdFrom");
				String partyId = (String) context.get("partyId");
				String periodBillingId = (String) context.get("periodBillingId");
				TimeZone timeZone = TimeZone.getDefault();
				List masterList = FastList.newInstance();
				Locale locale = Locale.getDefault();
				GenericValue periodBilling = null;
				String customTimePeriodId = null;
				boolean generationFailed = false;		
				try{
					try {
						periodBilling =delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
						customTimePeriodId = periodBilling.getString("customTimePeriodId");
					} catch (GenericEntityException e1) {
						Debug.logError(e1,"Error While Finding PeriodBilling");
						return ServiceUtil.returnError("Error While Finding PeriodBilling" + e1);
					}
					GenericValue customTimePeriod;
					try {
						customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
					} catch (GenericEntityException e1) {
						Debug.logError(e1,"Error While Finding Customtime Period");
				    	periodBilling.set("statusId", "GENERATION_FAIL");
						periodBilling.store();
						return ServiceUtil.returnError("Error While Finding Customtime Period" + e1);
					}
					if(customTimePeriod == null){
						generationFailed = true;
					}
					Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
					Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
					
					Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
					Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
					
					int totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);	
					try {
						Map input = FastMap.newInstance();
						//input.put(arg0, arg1);
					   
						if(UtilValidate.isEmpty(partyId)){
							partyId = "Company";
						}
						input.put("userLogin", userLogin);
						input.put("partyId", partyId);
						input.put("partyIdFrom", partyIdFrom); 
						input.put("currencyUomId", "INR");
						input.put("dueDate", UtilDateTime.nowTimestamp());
						input.put("timePeriodId", customTimePeriodId);
						Map payHeadResult = preparePayrolHeaders(dctx,input);
	       				if(ServiceUtil.isError(payHeadResult)){
	       					Debug.logError("Problems in service Parol Header", module);
					    	periodBilling.set("statusId", "GENERATION_FAIL");
							periodBilling.store();
				  			return ServiceUtil.returnError("Problems in service Parol Header");
	       				}
	       				List payHeaderList = (List)payHeadResult.get("itemsList");
	       				for(int i=0;i<payHeaderList.size();i++){
	       					Map payHeaderValue = (Map)payHeaderList.get(i);
	       					GenericValue payHeader = delegator.makeValue("PayrollHeader");
	       					payHeader.set("periodBillingId", periodBillingId);
	       					payHeader.set("partyId",payHeaderValue.get("partyId"));
	       					payHeader.set("partyIdFrom", payHeaderValue.get("partyIdFrom"));
	       					payHeader.setNextSeqId();
	       					payHeader.create();
	       					//create payroll header Items here
	       					Map inputItem = FastMap.newInstance();
	       					inputItem.put("userLogin", userLogin);
							inputItem.put("partyId", payHeaderValue.get("partyIdFrom"));
							inputItem.put("timePeriodId", customTimePeriodId);
							Map payHeadItemResult = preparePayrolItems(dctx,inputItem);
							if(ServiceUtil.isError(payHeadItemResult)){
		       					Debug.logError("Problems in service Parol Header Item", module);
						    	periodBilling.set("statusId", "GENERATION_FAIL");
								periodBilling.store();
					  			return ServiceUtil.returnError("Problems in service Parol Header Item");
		       				}
							List payHeaderItemList = (List)payHeadItemResult.get("itemsList");
							for(int j=0;j< payHeaderItemList.size();j++){
								Map payHeaderItemValue = (Map)payHeaderItemList.get(j);
		       					GenericValue payHeaderItem = delegator.makeValue("PayrollHeaderItem");
		       					payHeaderItem.set("payrollHeaderId", payHeader.get("payrollHeaderId"));
		       					payHeaderItem.set("payrollHeaderItemTypeId",payHeaderItemValue.get("payrollItemTypeId"));
		       					payHeaderItem.set("amount", payHeaderItemValue.get("amount"));
		       				    delegator.setNextSubSeqId(payHeaderItem, "payrollItemSeqId", 5, 1);
					            delegator.create(payHeaderItem);
							}
	       				}
						
					}catch (Exception e) {
						Debug.logError(e, module);
						periodBilling.set("statusId", "GENERATION_FAIL");
						periodBilling.store();
						return ServiceUtil.returnError("Error While generating PeriodBilling" + e);
					}
					if (generationFailed) {
						periodBilling.set("statusId", "GENERATION_FAIL");
					} else {
						periodBilling.set("statusId", "GENERATED");
						periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					}
					periodBilling.store();	
					
			}catch (GenericEntityException e) {
					Debug.logError(e, module);
					periodBilling.set("statusId", "GENERATION_FAIL");
					periodBilling.store();
					return ServiceUtil.returnError("Error While generating PeriodBilling" + e);
			}
			result.put("periodBillingId", periodBillingId);
				return result;
		
			}
			public static Map<String, Object>  cancelPayrollBilling(DispatchContext dctx, Map<String, ? extends Object> context)  {
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = FastMap.newInstance();	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String periodBillingId = (String) context.get("periodBillingId");
				GenericValue periodBilling = null;
		    	try {
		    		try {
						periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
					} catch (GenericEntityException e1) {
						Debug.logError(e1,"Error While Finding PeriodBilling");
						return ServiceUtil.returnError("Error While Finding PeriodBilling" + e1);
					}
		    		List<GenericValue> payrollHeaderList = delegator.findList("PayrollHeader", EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId), null, null, null, false);
		    		if(UtilValidate.isNotEmpty(payrollHeaderList)){
		    			List payrollHeaderIds = EntityUtil.getFieldListFromEntityList(payrollHeaderList, "payrollHeaderId", false);
		    			if(UtilValidate.isNotEmpty(payrollHeaderIds)){
		    				List<GenericValue> payrollHeaderItemList = delegator.findList("PayrollHeaderItem", EntityCondition.makeCondition("payrollHeaderId", EntityOperator.IN, payrollHeaderIds), null, null, null, false);
		    				if(UtilValidate.isNotEmpty(payrollHeaderItemList)){
		    	    		    delegator.removeAll(payrollHeaderItemList);
		    	    		}
		    			}
		    			periodBilling.set("statusId", "COM_CANCELLED");
		    			periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    			periodBilling.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						periodBilling.store();
		    		}else{
		    			periodBilling.set("statusId", "CANCEL_FAILED");
		    			periodBilling.store();
		    		}
		    	}catch (GenericEntityException e) {
		    		 Debug.logError(e, module);
		             return ServiceUtil.returnError("Failed to find payrollHeaderItemList " + e);
				} 
				result = ServiceUtil.returnSuccess("PayRoll Billing Successfully Cancelled..");
				return result;
		}// end of service
			private static  Map<String, Object> fetchBasicSalaryAndGrade(DispatchContext dctx, String partyId) {
				double amount = 0;
		        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		        Delegator delegator = dctx.getDelegator();
		        FastMap result = FastMap.newInstance();
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
		            	
		            	GenericValue payGrade = delegator.findOne("PayGrade", UtilMisc.toMap(
		            			"payGradeId", payGradeId), true);
		            	result.put("payGradeId", payGrade.get("seqId"));
		                if (salaryStep != null) {
		                    amount = salaryStep.getDouble("amount");
		                }else {
		    	            Debug.logWarning("Zero SalaryStep records for partyId '" + partyId + "'", module);
		                }
		                result.put("amount", amount);
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
		        	(proportionalFlagStr != null && "Y".equals(proportionalFlagStr))? true: false;
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
					if(UtilValidate.isNotEmpty(lossOfPayDays)){
						BigDecimal payDays = periodDays.subtract(lossOfPayDays);
						amount = amount.multiply(payDays).divide(BigDecimal.valueOf(payrollPeriodDays), 0, BigDecimal.ROUND_HALF_UP);
					}
				}
				Map result = UtilMisc.toMap("amount", amount);
				result.put("quantity", BigDecimal.ONE);	
		//Debug.logInfo("==========>periodDays=" + periodDays, module);	
		//Debug.logInfo("==========>adjusted amount=" + amount, module);	
				return result;
			}
			
			private static Map<String, Object> createPayrolBenefitItems(DispatchContext dctx, Map<String, Object> context) 
				throws GenericServiceException,GenericEntityException {
		        Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();
		        String errorMsg = "createPayrolBenefitItems failed";
				String partyId = (String) context.get("partyId");	
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
		        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
		        String timePeriodId = (String) context.get("timePeriodId");
		        Boolean isCalc = Boolean.FALSE;
		        List itemsList = FastList.newInstance();
		        
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
					Map input = UtilMisc.toMap("userLogin", userLogin);				
					input.put("payrollItemTypeId", benefitTypeId);			  
					Timestamp from = benefit.getTimestamp("fromDate");
					Timestamp thru = benefit.getTimestamp("thruDate");
					
					//if empty cost then look in rule engine
					if(UtilValidate.isEmpty(benefit.getBigDecimal("cost"))){
						Map payHeadCtx = UtilMisc.toMap("userLogin", userLogin);				
						payHeadCtx.put("payHeadTypeId", benefitTypeId);
						payHeadCtx.put("timePeriodStart", timePeriodStart);
						payHeadCtx.put("timePeriodEnd", timePeriodEnd);
						payHeadCtx.put("timePeriodId", timePeriodId);
						payHeadCtx.put("employeeId", partyId);
						Map<String, Object> result = calculatePayHeadAmount(dctx,payHeadCtx);
						Debug.log("result========="+result);
						if(UtilValidate.isNotEmpty(result)){
							benefit.set("cost" ,result.get("amount"));
						}
					}
					
					//Map<String, Object> adjustment = adjustAmount(context,benefit.getBigDecimal("cost"), from, thru);			
					input.put("quantity", BigDecimal.ONE);
					input.put("amount", benefit.getBigDecimal("cost"));
					
					itemsList.add(input);
				} 
				Map<String, Object> results = ServiceUtil.returnSuccess();
				results.put("itemsList", itemsList);
				return results; 
			}
			
			
			private static Map<String, Object> createPayrolDeductionItems(DispatchContext dctx, Map<String, Object> context) 
				throws GenericServiceException,GenericEntityException {
				Delegator delegator = dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				String errorMsg = "createPayrolDeductionItems failed";
					
				String partyId = (String) context.get("partyId");
				String timePeriodId = (String) context.get("timePeriodId");	
		        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
		        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd"); 		
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Map<String, Object> serviceResults;
				Boolean isCalc = Boolean.FALSE;
		        List itemsList = FastList.newInstance();

				List conditionList = UtilMisc.toList(
		                EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
		        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
		        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
		        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
		        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
				List<GenericValue> partyDeductions = delegator.findList("PartyDeduction", condition, null, null, null, false);
				
				for (int i = 0; i < partyDeductions.size(); ++i) {		
					GenericValue deduction = partyDeductions.get(i);
					Map input = UtilMisc.toMap("userLogin", userLogin);	
					String deductionTypeId = deduction.getString("deductionTypeId");
					input.put("payrollItemTypeId", deductionTypeId);			
		        	GenericValue deductionTypeRow = delegator.findOne("DeductionType", UtilMisc.toMap(
		        			"deductionTypeId", deductionTypeId), false);
		        	context.put("proportionalFlag", deductionTypeRow.getString("proportionalFlag"));
					Timestamp from = deduction.getTimestamp("fromDate");
					Timestamp thru = deduction.getTimestamp("thruDate");

					//if empty flatAmount then look in rule engine
					if(UtilValidate.isEmpty(deduction.getBigDecimal("cost"))){
						Map payHeadCtx = UtilMisc.toMap("userLogin", userLogin);				
						payHeadCtx.put("payHeadTypeId", deductionTypeId);
						payHeadCtx.put("timePeriodStart", timePeriodStart);
						payHeadCtx.put("timePeriodEnd", timePeriodEnd);
						payHeadCtx.put("timePeriodId", timePeriodId);
						payHeadCtx.put("employeeId", partyId);
						Map<String, Object> result = calculatePayHeadAmount(dctx,payHeadCtx);
						Debug.log("result========="+result);
						if(UtilValidate.isNotEmpty(result)){
							deduction.set("cost" ,result.get("amount"));
						}
					}
					
					input.put("quantity", BigDecimal.ONE);
					input.put("amount", (deduction.getBigDecimal("cost")).negate());
					itemsList.add(input);
								
				}	
				
				Map<String, Object> results = ServiceUtil.returnSuccess();
				results.put("itemsList", itemsList);
				return results; 		
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
			private static Map<String, Object> createPayrolBasicSalaryItems(DispatchContext dctx, Map<String, Object> context) 
			throws GenericServiceException,GenericEntityException {
				Delegator delegator = dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				String errorMsg = "createPayrolBasicSalaryItems failed";
				String partyId = (String) context.get("partyId");	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
				Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");        
				Map<String, Object> serviceResults;
				Boolean isCalc = Boolean.FALSE;
		        List itemsList = FastList.newInstance();
		        
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
		            Map input = UtilMisc.toMap("payrollItemTypeId", "PAYROL_BEN_SALARY");
		            Timestamp from = row.getTimestamp("fromDate");
		            Timestamp thru = row.getTimestamp("thruDate");
		            context.put("proportionalFlag", "Y");
		            Map<String, Object> adjustment = adjustAmount(context,salaryStep.getBigDecimal("amount"), from, thru);			
		            context.put("proportionalFlag", null);
		            input.put("quantity", adjustment.get("quantity"));
		            input.put("amount", adjustment.get("amount"));
					itemsList.add(input);
					
		        } 
		        Map<String, Object> results = ServiceUtil.returnSuccess();
				results.put("itemsList", itemsList);
				return results; 
			}	

			/*private static Map<String, Object> fetchLossOfPayDays(DispatchContext dctx, Map<String, Object> context) 
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
			}*/
			
			/*private static Map<String, Object> fetchAttendance(DispatchContext dctx, Map<String, Object> context) 
			throws GenericEntityException {
				Delegator delegator = dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				String errorMsg = "fetch attedance failed";
				String partyId = (String) context.get("employeeId");
				String customTimePeriodId = (String) context.get("timePeriodId");	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				String timePeriodId = (String) context.get("timePeriodId");
				Map<String, Object> serviceResults = ServiceUtil.returnSuccess();
				
				
				
		        return serviceResults; 
			}*/
			
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
			public static Map<String, Object> preparePayrolItems(DispatchContext dctx, Map<String, Object> context) {
		        Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();
				
				String partyId = (String) context.get("partyId");
		        String errorMsg = "createPayrolInvoiceItems failed for party '" + partyId + "': ";			
		        GenericValue userLogin = (GenericValue) context.get("userLogin");            
				Map<String, Object> serviceResults;
				List itemsList = FastList.newInstance();
				
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
				/*try {
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
		Debug.logInfo("==========>lossOfPayDays=" + context.get("lossOfPayDays"), module); */  		
				// First, lets generate invoice item(s) for basic salary
				try {
					serviceResults = createPayrolBasicSalaryItems(dctx, context);
			        if (ServiceUtil.isError(serviceResults)) {
			        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
			        }
			       if(UtilValidate.isNotEmpty(serviceResults.get("itemsList"))){
			    	   itemsList.addAll((List)serviceResults.get("itemsList"));
			       }
				} 
				catch(GenericEntityException e) {
					Debug.logError(e, errorMsg + "Unable to create payroll Item records for basic salary: " + e.getMessage(), module);
			        return ServiceUtil.returnError(errorMsg + "Unable to create payroll Item records for basic salary: " + e.getMessage());			
				}			
				catch (GenericServiceException e) {
					Debug.logError(e, errorMsg + "Unable to create payroll Item records for basic salary: " + e.getMessage(), module);
			        return ServiceUtil.returnError(errorMsg + "Unable to create payroll Item records for basic salary: " + e.getMessage());
			    }
				catch (Exception e) {
					Debug.logError(e, errorMsg + "Unable to create payroll Item records for basic salary: " + e.getMessage(), module);
			        return ServiceUtil.returnError(errorMsg + "Unable to create payroll Item records for basic salary: " + e.getMessage());
			    }        
				// Create invoice items for benefits
				try {
					serviceResults = createPayrolBenefitItems(dctx, context);
			        if (ServiceUtil.isError(serviceResults)) {
			        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
			        }
			        if(UtilValidate.isNotEmpty(serviceResults.get("itemsList"))){
				    	   itemsList.addAll((List)serviceResults.get("itemsList"));
				    	   
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
					serviceResults = createPayrolDeductionItems(dctx, context);
			        if (ServiceUtil.isError(serviceResults)) {
			        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
			        }
			        if(UtilValidate.isNotEmpty(serviceResults.get("itemsList"))){
				    	   itemsList.addAll((List)serviceResults.get("itemsList"));
				    	   
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
			    Map result = ServiceUtil.returnSuccess();
			    result.put("itemsList",itemsList);
				return result;   	
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
			
			public static Map<String, Object> preparePayrolHeaders(DispatchContext dctx, Map<String, Object> context) {
			    Delegator delegator = dctx.getDelegator();
			    LocalDispatcher dispatcher = dctx.getDispatcher();  
				String partyId = (String) context.get("partyId");	
				String partyIdFrom = (String) context.get("partyIdFrom");	
		        String timePeriodId = (String)context.get("timePeriodId");		
			    String errorMsg = "createPayroll Header failed [" + partyIdFrom + "-->" + partyId + "]"; 		
				Locale locale = (Locale) context.get("locale");
				List itemsList = FastList.newInstance();
				TimeZone timeZone = TimeZone.getDefault();// ::TODO   		
		        Map<String, Object> serviceResults = ServiceUtil.returnError(errorMsg, null, null, null);    
				List<GenericValue> invoiceList = FastList.newInstance();
		        Map input = UtilMisc.toMap("userLogin", context.get("userLogin"));
		        input.put("invoiceTypeId", context.get("invoiceTypeId"));        
		        input.put("partyId", partyId);	
		        input.put("partyIdFrom", partyIdFrom);	
		        input.put("statusId", context.get("statusId"));	
		        input.put("currencyUomId", context.get("currencyUomId"));	
		      
				try {   
					serviceResults = fetchPayrolBoundary(dctx, context);
			        if (ServiceUtil.isError(serviceResults)) {
						return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
					}   
			        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
			        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
			       
			        boolean isGroup = true;
			        
			     /*   // check Accounting tenant configration for payroll invoice here
			        GenericValue tenantConfigEnablePayrollInvAcctg = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","ACCOUNT_INVOICE", "propertyName","enablePayrollInvAcctg"), true);
					 if (UtilValidate.isNotEmpty(tenantConfigEnablePayrollInvAcctg) && (tenantConfigEnablePayrollInvAcctg.getString("propertyValue")).equals("N")) {
						 enablePayrollInvAcctg = Boolean.FALSE;
					 }
					 if(!enablePayrollInvAcctg){
						 input.put("isEnableAcctg", "N");
					  }*/
					 
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
						if (employments.size() == 0){
							return ServiceUtil.returnError("No employment record found for party " + partyIdFrom, 
									null, null, serviceResults);					
						} 
						itemsList.add(input);
						serviceResults.put("itemsList", itemsList);
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
							Map tempInputMap = FastMap.newInstance();
							tempInputMap.putAll(input);
							itemsList.add(tempInputMap);
			        	}
			        }
				}
				catch(GenericEntityException e) {
					Debug.logError(e, module);
			        return ServiceUtil.returnError("Unable to create payroll Invoice record");			
				}		
				catch (Exception e) {
					Debug.logError(e, errorMsg + e.getMessage(), module);
			        return ServiceUtil.returnError(errorMsg + e.getMessage());
			    }
				serviceResults.clear();
				serviceResults.put("itemsList", itemsList);
				return serviceResults;      
			}
			
			public static Map<String, Object> getPayHeadAmount(DispatchContext dctx, Map<String, ? extends Object> context) {

		        Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();
		        Map<String, Object> result = FastMap.newInstance();
		        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        String payHeadTypeId = (String) context.get("payHeadTypeId");
		        String employeeId = (String) context.get("employeeId");
		        String customTimePeriodId = (String)context.get("customTimePeriodId");
		        Locale locale = (Locale) context.get("locale");
		        BigDecimal amount = BigDecimal.ZERO;
		        Debug.log("In getPayHeadAmount ####################"+payHeadTypeId);
		        try{
		        	GenericValue customTimePeriod;
					try {
						customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
					} catch (GenericEntityException e1) {
						Debug.logError(e1,"Error While Finding Customtime Period");
						return ServiceUtil.returnError("Error While Finding Customtime Period" + e1);
					}
					Timestamp timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
					Timestamp timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
					List conditionList = UtilMisc.toList(
			                EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId));
					conditionList.add(EntityCondition.makeCondition("benefitTypeId", EntityOperator.EQUALS, payHeadTypeId));
			        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
			        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
			        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
			        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
					List<GenericValue> payHeadTypesList = delegator.findList("PartyBenefit", condition, null, null, null, false);
					
					conditionList.clear();
					conditionList.add( EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId));
					conditionList.add(EntityCondition.makeCondition("deductionTypeId", EntityOperator.EQUALS, payHeadTypeId));
			        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
			        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
			        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
			        condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  
			        payHeadTypesList.addAll(delegator.findList("PartyDeduction", condition, null, null, null, false));
			        if(UtilValidate.isEmpty(payHeadTypesList)){
			        	 Debug.logWarning("Payhead not applicable", module);
			        	 result.put("amount", BigDecimal.ZERO);
			        	 result.put("priceInfos",UtilMisc.toList("Payhead not applicable"));
			        	 return result;
			        }
			        GenericValue paheadType = EntityUtil.getFirst(payHeadTypesList);
			        if(UtilValidate.isNotEmpty(paheadType) && UtilValidate.isNotEmpty(paheadType.getBigDecimal("cost"))){
			        	 result.put("amount", paheadType.getBigDecimal("cost"));
			        	 result.put("priceInfos",UtilMisc.toList("Party level price"));
			        	 return result;
			        }
					Map payheadAmtCtx = FastMap.newInstance();
                    payheadAmtCtx.put("userLogin", userLogin);
                    payheadAmtCtx.put("employeeId", employeeId);
                    payheadAmtCtx.put("timePeriodStart", timePeriodStart);
                    payheadAmtCtx.put("timePeriodEnd", timePeriodEnd);
                    payheadAmtCtx.put("timePeriodId", customTimePeriodId);
                    payheadAmtCtx.put("payHeadTypeId", payHeadTypeId);
	                Map<String, Object> calcResults = calculatePayHeadAmount(dctx,payheadAmtCtx);
	                Debug.log("calcResults in calculatePayHeadAmount ############################"+calcResults);
	                result.putAll(calcResults);
		                
		            } catch (Exception e) {
		                Debug.logError(e, "Error getting rules from the database while calculating price", module);
		                return ServiceUtil.returnError(e.toString());
		            }
		        //end of price rules

		       //Debug.log("priceInfos==="+result.get("priceInfos"));
		        // utilTimer.timerString("Finished price calc [productId=" + productId + "]", module);
		        return result;
		    }

		    /**
		     * <p>Calculates the pay head amount from pricing rules given the following input, and of course access to the database:</p>
		     * <ul>
		     *   <li>payheadType(Ben or Ded)
		     *   <li>geoId
		     *   <li>date
		     *   <li>employeeId
		     *   <li>orgPartyId
		     *   <li>timePeriodStart
		     *   <li>timePeriodEnd
		     * </ul>
		     */
		    public static Map<String, Object> calculatePayHeadAmount(DispatchContext dctx, Map<String, ? extends Object> context) {

		        Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();
		        Map<String, Object> result = FastMap.newInstance();
		        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        String payHeadTypeId = (String) context.get("payHeadTypeId");
		        String employeeId = (String) context.get("employeeId");
		        String orgPartyId = (String) context.get("orgPartyId");
		        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
				Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
				String timePeriodId = (String) context.get("timePeriodId");
		        Locale locale = (Locale) context.get("locale");
		        BigDecimal amount = BigDecimal.ZERO;
		        Debug.log("In calculatePayHeadAmount ##################################"+payHeadTypeId);
		        try{
			           		         
		        	    Map makePayHedPrice = FastMap.newInstance();
		        	    makePayHedPrice.put("userLogin",userLogin);
		        	    makePayHedPrice.put("payHeadTypeId",payHeadTypeId);
		        	    makePayHedPrice.put("fromDate",timePeriodStart);
		        	    //Debug.log("makePayHedPrice ######"+makePayHedPrice);
		                List<GenericValue> allBenDedPriceRules = makePayHeadPriceRuleList(dctx, makePayHedPrice);
		                Debug.log("allBenDedPriceRules ######"+allBenDedPriceRules);
		                allBenDedPriceRules = EntityUtil.filterByDate(allBenDedPriceRules, true);
	               
	                    Map priceResultRuleCtx = FastMap.newInstance();
	                    priceResultRuleCtx.putAll(context);
	                    priceResultRuleCtx.put("payHeadPriceRules", allBenDedPriceRules);
	                   // Debug.log("priceResultRuleCtx ####################### ######"+priceResultRuleCtx);
	                    Map<String, Object> calcResults = calcPriceResultFromRules(dctx,priceResultRuleCtx);
	                    Debug.log("calcResults ######"+calcResults);
	                    result.putAll(calcResults);
		            } catch (GenericEntityException e) {
		                Debug.logError(e, "Error getting rules from the database while calculating price", module);
		                return ServiceUtil.returnError(e.toString());
		            }
		       

		      // Debug.log("priceInfos"+result.get("priceInfos"));
		        // utilTimer.timerString("Finished price calc [productId=" + productId + "]", module);
		        return result;
		    }

	    
	    public static List<GenericValue> makePayHeadPriceRuleList(DispatchContext dctx,Map<String, Object> context) throws GenericEntityException {
	        List<GenericValue> payHeadPriceRules = null;
	        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			String payHeadTypeId= (String) context.get("payHeadTypeId");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Debug.log("In makePayHeadPriceRuleList############"+payHeadTypeId);
	        EntityCondition condition = EntityCondition.makeCondition("payHeadTypeId", EntityOperator.EQUALS,payHeadTypeId); 				
        	payHeadPriceRules = delegator.findList("PayrollBenDedRule", condition, null, null, null, true);
            if (payHeadPriceRules == null) payHeadPriceRules = FastList.newInstance();
	        
            payHeadPriceRules = EntityUtil.filterByDate(payHeadPriceRules, fromDate);
	        return payHeadPriceRules;
	    } 
	    public static Map<String, Object> calcPriceResultFromRules(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {

	            Map<String, Object> calcResults = FastMap.newInstance();
    		    GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();
				Locale locale = (Locale) context.get("locale");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String employeeId= (String) context.get("employeeId");
				Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
				Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
				String timePeriodId = (String) context.get("timePeriodId");
				Map condParms = (Map) context.get("condParms");
				List<GenericValue> payHeadPriceRules= (List<GenericValue>) context.get("payHeadPriceRules");
				 calcResults.put("amount", BigDecimal.ZERO);
                Timestamp nowTimestamp =  UtilDateTime.nowTimestamp();
                List priceInfos = FastList.newInstance();
                Debug.log("In calcPriceResultFromRules ######################");
                if(UtilValidate.isEmpty(payHeadPriceRules)){
                	Debug.logImportant("no rules found for given payheadType", module);
                    return calcResults;
                }
	            for (GenericValue payHeadPriceRule: payHeadPriceRules) {
	                String payHeadPriceRuleId = payHeadPriceRule.getString("payrollBenDedRuleId");

	                // check all conditions
	                boolean allTrue = true;
	                StringBuilder condsDescription = new StringBuilder();
	                List<GenericValue> payrollBenDedCondList = delegator.findList("PayrollBenDedCond", EntityCondition.makeCondition("payrollBenDedRuleId",EntityOperator.EQUALS ,payHeadPriceRuleId ), null,UtilMisc.toList("payrollBenDedCondSeqId") , null, false);	
	                for (GenericValue payrollBenDedCond : payrollBenDedCondList) {
	                	
                         if(UtilValidate.isNotEmpty(condParms)){
                        	 if (!checkPriceCondition(payrollBenDedCond,employeeId, dctx, delegator, timePeriodStart ,timePeriodEnd ,condParms)) {
     	                        allTrue = false;
     	                        break;
     	                    }
                         }else{
                        	 if (!checkPriceCondition(payrollBenDedCond,employeeId, dctx, delegator, timePeriodStart ,timePeriodEnd ,null)) {
     	                        allTrue = false;
     	                        break;
     	                    }
                         }
                         Debug.log("###allTrue each#########"+allTrue+"==================="+payrollBenDedCond);
                         condsDescription.append("[");
     	                GenericValue inputParamEnum = payrollBenDedCond.getRelatedOneCache("InputParamEnumeration");

     	                condsDescription.append(inputParamEnum.getString("enumCode"));
     	                condsDescription.append(" ");
     	                GenericValue operatorEnum = payrollBenDedCond.getRelatedOneCache("OperatorEnumeration");
     	                condsDescription.append(operatorEnum.getString("description"));
     	                condsDescription.append(" ");
     	                condsDescription.append(payrollBenDedCond.getString("condValue"));
     	                condsDescription.append("] ");
	                    
	                }
	                Debug.log("allTrue #############################################"+allTrue);
	                // if all true, perform all actions
	                BigDecimal modifyAmount = BigDecimal.ZERO;
	                if (allTrue) {
	                	StringBuilder priceInfoDescription = new StringBuilder();
	                	Map fetchBasicSalaryAndGradeMap = fetchBasicSalaryAndGrade(dctx, employeeId);
	                    List<GenericValue> payHeadPriceActions = delegator.findByAndCache("PayHeadPriceAction", UtilMisc.toMap("payrollBenDedRuleId", payHeadPriceRuleId));
	                    Debug.log("payHeadPriceActions ######################################"+payHeadPriceActions.size());
	                    for (GenericValue payHeadPriceAction: payHeadPriceActions) {
	                        // yeah, finally here, perform the action, ie, modify the amount
	     	                    priceInfoDescription.append(condsDescription.toString());
	     	                    priceInfoDescription.append("[");
	     	                    priceInfoDescription.append("type:");
	     	                    priceInfoDescription.append(payHeadPriceAction.getString("payHeadPriceActionTypeId"));
	     	                    priceInfoDescription.append("]\n");
	     	                    Debug.log(payHeadPriceAction+"########payHeadPriceAction ############");
	                        if ("PRICE_FLAT".equals(payHeadPriceAction.getString("payHeadPriceActionTypeId"))) {
	                            String formulaId = payHeadPriceAction.getString("acctgFormulaId");
	                            if (UtilValidate.isNotEmpty(formulaId)) {
	                            
		    		        		Evaluator evltr = new Evaluator(dctx);
		    		        		Debug.log("*********** formulaId ================"+formulaId);
		    		        		evltr.setFormulaIdAndSlabAmount(formulaId, modifyAmount.doubleValue());
		    						HashMap<String, Double> variables = new HashMap<String, Double>();
		    						Map formulaVaribules = evltr.getVariableValues();
		    						double attendance = 1;
		    						Set<String> varibuleKeySet = formulaVaribules.keySet();
		    						
		    						ArrayList varibuleKeyList = new ArrayList(varibuleKeySet);
		    						List payheadTypeIdsList = (List)((getPayheadTypes(dctx , context)).get("payheadTypeIdsList"));
		    						//Debug.log("*********** varibuleKeySet ================"+varibuleKeySet);
		    						for(int i= 0;i<varibuleKeyList.size();i++){
		    							// this to support no.of days in the accounting formula
		    							//  NOOFCALENDERDAYS      NOOFATTENDEDDAYS      LOSSOFPAYDAYS
		    							//  NOOFATTENDEDHOLIDAYS  NOOFATTENDEDSS        NOOFATTENDEDWEEKLYOFF
		    							//  NOOFLEAVEDAYS         NOOFCOMPOFFSAVAILED
		    							Set supportedVaribules = UtilMisc.toSet("NOOFCALENDERDAYS","NOOFATTENDEDDAYS","LOSSOFPAYDAYS",
		    									"NOOFATTENDEDHOLIDAYS" ,"NOOFATTENDEDSS" ,"NOOFATTENDEDWEEKLYOFF" ,"NOOFLEAVEDAYS","NOOFCOMPOFFSAVAILED");
		    							if(supportedVaribules.contains(varibuleKeySet) ){
			    							Map attendanceMap = getEmployeePayrollAttedance(dctx ,context);
			    							variables.put("NOOFCALENDERDAYS", (Double)attendanceMap.get("noOfCalenderDays"));
			    							variables.put("NOOFATTENDEDDAYS", (Double)attendanceMap.get("noOfAttendedDays"));
			    							variables.put("NOOFATTENDEDHOLIDAYS", (Double)attendanceMap.get("noOfAttendedHoliDays"));
			    							variables.put("LOSSOFPAYDAYS", (Double)attendanceMap.get("lossOfPayDays"));
			    							variables.put("NOOFATTENDEDSS", (Double)attendanceMap.get("noOfAttendedSsDays"));
			    							variables.put("NOOFATTENDEDWEEKLYOFF", (Double)attendanceMap.get("noOfAttendedWeeklyOffDays"));
			    							variables.put("NOOFLEAVEDAYS", (Double)attendanceMap.get("noOfLeaveDays"));
			    							variables.put("NOOFCOMPOFFSAVAILED", (Double)attendanceMap.get("noOfCompoffAvailed"));
			    							
			    							double noOfAttendedDays = ((Double)attendanceMap.get("noOfAttendedDays")).doubleValue();
			    							evltr.setFormulaIdAndSlabAmount(formulaId, noOfAttendedDays);
			    						}
		    							
		    							// this to support dependent benefit or deductions
		    							String varibuleKey = (String)varibuleKeyList.get(i);
		    							if(payheadTypeIdsList.contains(varibuleKey)){
		    								Map payheadAmtCtx = FastMap.newInstance();
		    			                    payheadAmtCtx.put("userLogin", userLogin);
		    			                    payheadAmtCtx.put("employeeId", employeeId);
		    			                    payheadAmtCtx.put("timePeriodStart", timePeriodStart);
		    			                    payheadAmtCtx.put("timePeriodEnd", timePeriodEnd);
		    			                    payheadAmtCtx.put("timePeriodId", timePeriodId);
		    			                    payheadAmtCtx.put("payHeadTypeId", varibuleKey);
		    				                Map<String, Object> innerCalcResults = calculatePayHeadAmount(dctx,payheadAmtCtx);
		    								variables.put(varibuleKey, ((BigDecimal)innerCalcResults.get("amount")).doubleValue());
			    						}
		    							// this to support GROSSSALARY 
		    							if(varibuleKeySet.contains("GROSSSALARY") ){
		    								Map grossAmtCtx = FastMap.newInstance();
		    			                    grossAmtCtx.put("userLogin", userLogin);
		    			                    grossAmtCtx.put("employeeId", employeeId);
		    			                    grossAmtCtx.put("timePeriodStart", timePeriodStart);
		    			                    grossAmtCtx.put("timePeriodEnd", timePeriodEnd);
		    			                    grossAmtCtx.put("timePeriodId", timePeriodId);
			    							Map grossSalaryMap  = getEmployeeGrossSalary(dctx ,grossAmtCtx);
			    							variables.put(varibuleKey, ((BigDecimal)grossSalaryMap.get("amount")).doubleValue());
			    						}
		    							
		    						}
		    						double basicSalary = ((Double)fetchBasicSalaryAndGradeMap.get("amount")).doubleValue();
		    						variables.put("BASIC", basicSalary);
		    						evltr.addVariableValues(variables);        		
		    						modifyAmount = new BigDecimal( evltr.evaluate());
		    						//amount info 
		    						priceInfoDescription.append("[");
		     	                    priceInfoDescription.append("formulaId:");
		     	                    priceInfoDescription.append(formulaId);
		     	                    priceInfoDescription.append(", formula :");
		     	                    GenericValue formula = payHeadPriceAction.getRelatedOneCache("AcctgFormula");
		     	                   priceInfoDescription.append(formula.getString("formula"));
		     	                    priceInfoDescription.append("\n ,variables values:");
		     	                    priceInfoDescription.append(variables);
		     	                    priceInfoDescription.append("]\n");
	                           }
	                         
	                        }else if ("PRICE_SERVICE".equals(payHeadPriceAction.getString("payHeadPriceActionTypeId"))) {
	                        	   // customPriceCalcService
		                          if(UtilValidate.isNotEmpty(payHeadPriceAction.getString("customPriceCalcService"))) {
		                            	String customPriceCalcService = payHeadPriceAction.getString("customPriceCalcService");
		                            	try{
		                            		Map customPriceCalcServiceResult = dispatcher.runSync(customPriceCalcService, context);
		                            		if(ServiceUtil.isError(customPriceCalcServiceResult)){
		                            			 Debug.logError("Error while calculating price service:"+customPriceCalcService, module);
				             		             return ServiceUtil.returnError(ServiceUtil.getErrorMessage(customPriceCalcServiceResult));
		                            		}
		                            		modifyAmount = (BigDecimal)customPriceCalcServiceResult.get("amount");
		                            		priceInfoDescription.append("[");
				     	                    priceInfoDescription.append("called custom service :");
				     	                    priceInfoDescription.append(customPriceCalcService);
				     	                    priceInfoDescription.append(" service result :");
				     	                    priceInfoDescription.append(customPriceCalcServiceResult.get("priceInfos"));
				     	                    priceInfoDescription.append("]\n");
		                            		//priceInfos.add(customPriceCalcServiceResult.get("priceInfos"));
		                            	}catch (Exception e) {
											// TODO: handle exception
		                            		 Debug.logError(e, "Error while calculating price"+customPriceCalcService, module);
		             		                return ServiceUtil.returnError(e.toString());
										}
		                          }
	                        }
                         // if amount not null take that
                           if (payHeadPriceAction.get("amount") != null) {
	                               modifyAmount = payHeadPriceAction.getBigDecimal("amount");
	                               priceInfoDescription.append("[");
		     	                    priceInfoDescription.append("amount:");
		     	                    priceInfoDescription.append(payHeadPriceAction.getBigDecimal("amount"));
	                       } 
                      
	                    }
	                    
	                    calcResults.put("amount", modifyAmount);
	                    priceInfos.add(priceInfoDescription.toString());
	                    calcResults.put("priceInfos", priceInfos);
	                    return calcResults;
	                }
	                calcResults.put("amount", modifyAmount);
	            }
	            
/*
	            calcResults.put("basePrice", price);
	            calcResults.put("price", price);
	            calcResults.put("listPrice", listPrice);
	            calcResults.put("defaultPrice", defaultPrice);
	            calcResults.put("averageCost", averageCost);
	            calcResults.put("orderItemPriceInfos", orderItemPriceInfos);
	            calcResults.put("isSale", Boolean.valueOf(isSale));
	            calcResults.put("validPriceFound", Boolean.valueOf(validPriceFound));*/
	            calcResults.put("priceInfos", priceInfos);
	            return calcResults;
	        }  
	 public static boolean checkPriceCondition(GenericValue payrollBenDedCond, String employeeId,DispatchContext dctx,Delegator delegator, Timestamp fromDate ,Timestamp thruDate , Map condParms) throws GenericEntityException {
	        if (Debug.verboseOn()) Debug.logVerbose("Checking price condition: " + payrollBenDedCond, module);
	        
	      //get Employee Payroll Cond Parms details here
	        String geoId = null;
			String emplPositionTypeId = null;
			String departmentId = null;
			String shiftTypeId = null;
			String otherCond = null;
			String payGradeId = null;
	        if(UtilValidate.isNotEmpty(condParms)){
	        	 geoId = (String)condParms.get("geoId");
				 emplPositionTypeId = (String)condParms.get("emplPositionTypeId");
				 departmentId = (String)condParms.get("departmentId");
				 shiftTypeId = (String)condParms.get("shiftTypeId");
				 otherCond = (String)condParms.get("otherCond");
	        }else{
	        	Map empPositionDetails = getEmployeePayrollCondParms(dctx, UtilMisc.toMap("employeeId",employeeId,"timePeriodStart",fromDate,"timePeriodEnd" ,thruDate));
				geoId = (String)empPositionDetails.get("geoId");
				emplPositionTypeId = (String)empPositionDetails.get("emplPositionTypeId");
				departmentId = (String)empPositionDetails.get("departmentId");
				shiftTypeId = (String)empPositionDetails.get("shiftTypeId");
				payGradeId = (String)empPositionDetails.get("payGradeId");
	        }

	        int compare = 0;
            Debug.log("checking condtion for ::"+payrollBenDedCond);
	        if ("PAYHD_BEDE_EMPID".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	            compare = employeeId.compareTo(payrollBenDedCond.getString("condValue"));
	        } else if ("PAYHD_BEDE_POS".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	            if (UtilValidate.isNotEmpty(emplPositionTypeId)) {
	                compare = emplPositionTypeId.compareTo(payrollBenDedCond.getString("condValue"));
	            } else {
	                compare = 1;
	            }
	        }else if ("PAYHD_BEDE_GEO".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	            if (UtilValidate.isNotEmpty(geoId)) {
	                compare = geoId.compareTo(payrollBenDedCond.getString("condValue"));
	            } else {
	                compare = 1;
	            }
	        }else if ("PAYHD_BEDE_DEPT".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	            if (UtilValidate.isNotEmpty(departmentId)) {
	                compare = departmentId.compareTo(payrollBenDedCond.getString("condValue"));
	            } else {
	                compare = 1;
	            }
	        }else if ("PAYHD_BEDE_SHIFT".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	            if (UtilValidate.isNotEmpty(shiftTypeId)) {
	                compare = shiftTypeId.compareTo(payrollBenDedCond.getString("condValue"));
	            } else {
	                compare = 1;
	            }
	        }else if ("PAYHD_BEDE_GRADE".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	            if (UtilValidate.isNotEmpty(payGradeId)) {
	                compare = payGradeId.compareTo(payrollBenDedCond.getString("condValue"));
	            } else {
	                compare = 1;
	            }
	        }else if ("PAYHD_BEDE_OTHER".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	            if (UtilValidate.isNotEmpty(otherCond)) {
	                compare = otherCond.compareTo(payrollBenDedCond.getString("condValue"));
	            } else {
	                compare = 1;
	            }
	        }


	        if (Debug.verboseOn()) Debug.logVerbose("Pay head price condition compare done, compare=" + compare, module);
	        Debug.log("compare ::"+compare);
	        if ("PRC_EQ".equals(payrollBenDedCond.getString("operatorEnumId"))) {
	            if (compare == 0) return true;
	        } else if ("PRC_NEQ".equals(payrollBenDedCond.getString("operatorEnumId"))) {
	            if (compare != 0) return true;
	        } else if ("PRC_LT".equals(payrollBenDedCond.getString("operatorEnumId"))) {
	            if (compare < 0) return true;
	        } else if ("PRC_LTE".equals(payrollBenDedCond.getString("operatorEnumId"))) {
	            if (compare <= 0) return true;
	        } else if ("PRC_GT".equals(payrollBenDedCond.getString("operatorEnumId"))) {
	            if (compare > 0) return true;
	        } else if ("PRC_GTE".equals(payrollBenDedCond.getString("operatorEnumId"))) {
	            if (compare >= 0) return true;
	        } else {
	            Debug.logWarning("An un-supported PayheadPriceCond condition was used: " + payrollBenDedCond.getString("operatorEnumId") + ", returning false, ie check failed", module);
	            return false;
	        }
	        return false;
	    }
	 public static Map<String, Object> getEmployeePayrollCondParms(DispatchContext dctx, Map<String, ? extends Object> context) {

	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Map<String, Object> result = FastMap.newInstance();
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String employeeId = (String) context.get("employeeId");
	        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
	        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
	        Locale locale = (Locale) context.get("locale");
	        try{
	        	List conditionList = UtilMisc.toList(
						EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS, employeeId));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
			    EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	        	
	        	List<GenericValue> emplPositionAndFulfillments = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition("employeePartyId",EntityOperator.EQUALS,employeeId), null,null, null, false);
	        	emplPositionAndFulfillments = EntityUtil.filterByDate(emplPositionAndFulfillments, timePeriodStart);
	        	GenericValue emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
	        	if(UtilValidate.isNotEmpty(emplPositionAndFulfillment)){
	        		result.put("emplPositionTypeId", emplPositionAndFulfillment.getString("emplPositionTypeId"));
	        	}
	        	//lets populate geoId based on employement
	        	conditionList.clear();
	        	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
			    EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
				condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
				List<GenericValue> employments = delegator.findList("Employment", condition, null, null, null, false);
				employments = EntityUtil.filterByDate(employments, timePeriodStart);
	            GenericValue employment = EntityUtil.getFirst(employments);
	            if(UtilValidate.isNotEmpty(employment)){
	        		result.put("geoId", employment.getString("locationGeoId"));
	        		result.put("departmentId", employment.getString("partyIdFrom"));
	        	}
	            
	            GenericValue employeeDetail = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",employeeId), true);
	            
	            result.put("vehicleType", employeeDetail.getString("vehicleType"));
	            // get pay grade here
	            Map fetchBasicSalaryAndGradeMap = fetchBasicSalaryAndGrade(dctx, employeeId);
	            result.put("payGradeId", (String)fetchBasicSalaryAndGradeMap.get("payGradeId"));
	            
	            } catch (GenericEntityException e) {
	                Debug.logError(e, "Error getting rules from the database while calculating price", module);
	                return ServiceUtil.returnError(e.toString());
	            }
	        //end of price rules

	       
	        // utilTimer.timerString("Finished price calc [productId=" + productId + "]", module);
	         //Debug.log("result get cond parms====="+result);
	        return result;
	    }
	    
	 public static Map<String, Object> getEmployeePayrollAttedance(DispatchContext dctx, Map<String, ? extends Object> context) {

	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Map<String, Object> result = FastMap.newInstance();
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String employeeId = (String) context.get("employeeId");
	        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
			Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
			String timePeriodId = (String) context.get("timePeriodId");
	        Locale locale = (Locale) context.get("locale");
	        Map shiftDetailMap = FastMap.newInstance();
	        int availedVehicleDays =0;
	        Map availedCanteenDetailMap = FastMap.newInstance();
        	List conditionList = FastList.newInstance();
	        List<GenericValue> emplDailyAttendanceDetailList = FastList.newInstance();
	        GenericValue lastCloseAttedancePeriod= null;
	        try{
	        	 result = dispatcher.runSync("findLastClosedDate", UtilMisc.toMap("organizationPartyId", "Company", "periodTypeId", "ATTENDANCE_MONTH","userLogin", userLogin));
	  	    	if(ServiceUtil.isError(result)){
	 	 	    	Debug.logError("Error in service findLastClosedDate ", module);    			
	 	 		    return ServiceUtil.returnError("Error in service findLastClosedDate");
	 	 	    }
	  	    	lastCloseAttedancePeriod = ((GenericValue)result.get("lastClosedTimePeriod"));
	        }catch (Exception e) {
				// TODO: handle exception
			}
	       Debug.log("lastCloseAttedancePeriod==========="+lastCloseAttedancePeriod);
	        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,employeeId));
	        if(UtilValidate.isNotEmpty(lastCloseAttedancePeriod)){
	        	conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(lastCloseAttedancePeriod.getDate("fromDate"))));
	 	    	conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(lastCloseAttedancePeriod.getDate("thruDate"))));
	        	
	        }else{
	        	conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodStart)));
	 	    	conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodEnd)));
	        }
	        	
	       
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	try {
	    		emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition, null,null, null, false);
	    		availedVehicleDays = (EntityUtil.filterByAnd(emplDailyAttendanceDetailList, UtilMisc.toMap("availedVehicleAllowance" ,"Y"))).size();
	    		for( GenericValue  emplDailyAttendanceDetail : emplDailyAttendanceDetailList){
	    			String shiftType = emplDailyAttendanceDetail.getString("shiftType");
	    			//String availedVehicleAllowance = emplDailyAttendanceDetail.getString("availedVehicleAllowance");
	    			String availedCanteen = emplDailyAttendanceDetail.getString("availedCanteen");
	    			if(UtilValidate.isEmpty(shiftDetailMap.get(shiftType))){
	    				shiftDetailMap.put(shiftType,1);
	    			}else{
	    				shiftDetailMap.put(shiftType,(((Integer)(shiftDetailMap.get(shiftType))).intValue()+1));
	    			}
	    			// availedCanteen
	    			if(UtilValidate.isNotEmpty(availedCanteen) && availedCanteen.equalsIgnoreCase("Y")){
	    				if(UtilValidate.isEmpty(availedCanteenDetailMap.get(shiftType))){
		    				availedCanteenDetailMap.put(shiftType,1);
		    			}else{
		    				availedCanteenDetailMap.put(shiftType,(((Integer)(shiftDetailMap.get(shiftType))).intValue()+1));
		    			}
	    			}
	    		}
	    			
			GenericValue payrollAttendance = delegator.findOne("PayrollAttendance", UtilMisc.toMap("partyId",employeeId, "customTimePeriodId",timePeriodId), false);
			result.put("lossOfPayDays", 0.0);
			result.put("noOfAttendedDays",0.0);
			result.put("noOfCalenderDays", 0.0);
			result.put("noOfAttendedHoliDays", 0.0);
			result.put("noOfAttendedSsDays", 0.0);
			result.put("noOfAttendedWeeklyOffDays", 0.0);
			result.put("noOfCompoffAvailed", 0.0);
			result.put("noOfLeaveDays", 0.0);
			
			if(UtilValidate.isNotEmpty(payrollAttendance)){
				if(UtilValidate.isNotEmpty(payrollAttendance.get("lossOfPayDays"))){
					result.put("lossOfPayDays", (payrollAttendance.getBigDecimal("lossOfPayDays")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfAttendedDays"))){
					result.put("noOfAttendedDays", (payrollAttendance.getBigDecimal("noOfAttendedDays")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfCalenderDays"))){
					result.put("noOfCalenderDays", (payrollAttendance.getBigDecimal("noOfCalenderDays")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfAttendedHoliDays"))){
					result.put("noOfAttendedHoliDays", (payrollAttendance.getBigDecimal("noOfAttendedHoliDays")).doubleValue());
				}
				
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfLeaveDays"))){
					result.put("noOfLeaveDays", (payrollAttendance.getBigDecimal("noOfLeaveDays")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfAttendedWeeklyOffDays"))){
					result.put("noOfAttendedWeeklyOffDays", (payrollAttendance.getBigDecimal("noOfAttendedWeeklyOffDays")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfCompoffAvailed"))){
					result.put("noOfCompoffAvailed", (payrollAttendance.getBigDecimal("noOfCompoffAvailed")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfAttendedSsDays"))){
					result.put("noOfAttendedSsDays", (payrollAttendance.getBigDecimal("noOfAttendedSsDays")).doubleValue());
				}
				
			}
    		
	    }catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
			} 
            result.put("shiftDetailMap" ,shiftDetailMap);
            result.put("availedCanteenDetailMap" , availedCanteenDetailMap);
            result.put("availedVehicleDays" , availedVehicleDays);
            //Debug.log("getEmployeePayrollAttendance result:" + result);
	    
	        return result;
	    }
	 
	 public static Map<String, Object> getEmployeeGrossSalary(DispatchContext dctx, Map<String,  Object> context) {

	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Map<String, Object> result = FastMap.newInstance();
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String employeeId = (String) context.get("employeeId");
	        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
	        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
	        Locale locale = (Locale) context.get("locale");
	        BigDecimal amount = BigDecimal.ZERO;
	        List itemsList = FastList.newInstance();
	        try{
	        	context.put("partyId", employeeId);
	        	result = createPayrolBasicSalaryItems(dctx, context);
		       
		       if(UtilValidate.isNotEmpty(result.get("itemsList"))){
		    	   itemsList.addAll((List)result.get("itemsList"));
		       }
	           result = createPayrolBenefitItems(dctx, context);
		        
	           if(UtilValidate.isNotEmpty(result.get("itemsList"))){
		    	   itemsList.addAll((List)result.get("itemsList"));
		       }
	           Debug.log("itemsList size gross salary====="+itemsList.size());
	           
		       for(int i=0;i<itemsList.size();i++){
		        	Map itemEntry =  (Map)itemsList.get(i);
		        	BigDecimal tempAmount = new BigDecimal((Double)itemEntry.get("amount"));
		        	amount = amount.add(tempAmount);
		        	
		        }
	            
	           } catch (Exception e) {
	                Debug.logError(e, "Error getting rules from the database while calculating price", module);
	                return ServiceUtil.returnError(e.toString());
	            }
	        
	        result.put("amount",amount);
	         Debug.log("result gross salary====="+result);
	        return result;
	    }
	  
	    public static Map<String, Object> calculateShiftBasePayHeadAmount(DispatchContext dctx, Map<String, ? extends Object> context) {

	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Map<String, Object> result = FastMap.newInstance();
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String payHeadTypeId = (String) context.get("payHeadTypeId");
	        String employeeId = (String) context.get("employeeId");
	        String orgPartyId = (String) context.get("orgPartyId");
	        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
			Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
			String timePeriodId = (String) context.get("timePeriodId");
	        Locale locale = (Locale) context.get("locale");
	        BigDecimal amount = BigDecimal.ZERO;
	        Debug.log("in calculateShiftBasePayHeadAmount======");
	        List priceInfos =FastList.newInstance();
	        try{
	        	Map employeePayrollAttedance = getEmployeePayrollAttedance(dctx,context);
	        	
	        	Map shiftDetailMap = (Map)employeePayrollAttedance.get("shiftDetailMap");
	        	Map availedCanteenDetailMap = (Map)employeePayrollAttedance.get("availedCanteenDetailMap");
	        	int availedVehicleDays = ((Integer)employeePayrollAttedance.get("availedVehicleDays")).intValue();
	        	if(payHeadTypeId.equals("PAYROL_BEN_SHIFT")){
	        		Iterator tempIter = shiftDetailMap.entrySet().iterator();
		        	String shiftTypeId = "";
					while (tempIter.hasNext()) {
						Map.Entry tempEntry = (Entry) tempIter.next();
						BigDecimal value= new BigDecimal(((Integer)tempEntry.getValue()).intValue());
						shiftTypeId = (String)tempEntry.getKey();
						Map condParms = FastMap.newInstance();
						condParms.put("employeeId", employeeId);
						condParms.put("shiftTypeId", shiftTypeId);
						Map priceResultRuleCtx = FastMap.newInstance();
	                    priceResultRuleCtx.putAll(context);
	                    priceResultRuleCtx.put("condParms", condParms);
	                    Map<String, Object> calcResults = calculatePayHeadAmount(dctx,priceResultRuleCtx);
						amount = amount.add(((BigDecimal)calcResults.get("amount")).multiply(value));
						priceInfos.add(calcResults.get("priceInfos"));
					}
	        	}
	        	if(payHeadTypeId.equals("PAYROL_BEN_CONVEY")){
		        	Map empPositionDetails = getEmployeePayrollCondParms(dctx, UtilMisc.toMap("employeeId",employeeId,"timePeriodStart",timePeriodStart,"timePeriodEnd" ,timePeriodEnd));
		        	String vehicleType = (String)empPositionDetails.get("vehicleType");
		        	if(UtilValidate.isNotEmpty(vehicleType)){
		        		Map condParms = FastMap.newInstance();
						condParms.put("employeeId", employeeId);
						condParms.put("otherCond", vehicleType);
						Map priceResultRuleCtx = FastMap.newInstance();
	                    priceResultRuleCtx.putAll(context);
	                    priceResultRuleCtx.put("condParms", condParms);
	                    Map<String, Object> calcResults = calculatePayHeadAmount(dctx,priceResultRuleCtx);
						amount = amount.add(((BigDecimal)calcResults.get("amount")).multiply(new BigDecimal(availedVehicleDays)));
						priceInfos.add(calcResults.get("priceInfos"));
		        	}
					
	        	}
	        	if(payHeadTypeId.equals("PAYROL_DD_SAL_CANT")){
	        		Iterator tempIter = availedCanteenDetailMap.entrySet().iterator();
		        	String shiftTypeId = "";
					while (tempIter.hasNext()) {
						Map.Entry tempEntry = (Entry) tempIter.next();
						BigDecimal value= new BigDecimal(((Integer)tempEntry.getValue()).intValue());
						shiftTypeId = (String)tempEntry.getKey();
						Map condParms = FastMap.newInstance();
						condParms.put("employeeId", employeeId);
						condParms.put("shiftTypeId", shiftTypeId);
						Map priceResultRuleCtx = FastMap.newInstance();
	                    priceResultRuleCtx.putAll(context);
	                    priceResultRuleCtx.put("condParms", condParms);
	                    Map<String, Object> calcResults = calculatePayHeadAmount(dctx,priceResultRuleCtx);
						amount = amount.add(((BigDecimal)calcResults.get("amount")).multiply(value));
						priceInfos.add(calcResults.get("priceInfos"));
					}
	        	}
	        	
	            } catch (Exception e) {
	                Debug.logError(e, "Error getting rules from the database while calculating price", module);
	                return ServiceUtil.returnError(e.toString());
	            }
	        //end of price rules
	            
	          result.put("amount", amount);
	          result.put("priceInfos", priceInfos);
	        // utilTimer.timerString("Finished price calc [productId=" + productId + "]", module);
	        return result;
	    }
	 public static Map<String, Object> getPayheadTypes(DispatchContext dctx, Map<String, ? extends Object> context) {

	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Map<String, Object> result = FastMap.newInstance();
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String employeeId = (String) context.get("employeeId");
	        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
	        Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
	        Locale locale = (Locale) context.get("locale");
	        FastList payheadTypeList = FastList.newInstance();
	        FastList payheadTypeIdsList = FastList.newInstance();
	        try{
	        	
	        	List<GenericValue> benefitTypes = delegator.findList("BenefitType",null, null,null, null, true);
	        	List<GenericValue> deductionTypes = delegator.findList("DeductionType",null, null,null, null, true);
	        	payheadTypeList.addAll(benefitTypes);
	        	payheadTypeList.addAll(deductionTypes);
	        	payheadTypeIdsList.addAll(EntityUtil.getFieldListFromEntityList(benefitTypes, "benefitTypeId", true));
	        	payheadTypeIdsList.addAll(EntityUtil.getFieldListFromEntityList(deductionTypes, "deductionTypeId", true));
	            
	            } catch (GenericEntityException e) {
	                Debug.logError(e, "Error getting payhead types", module);
	                return ServiceUtil.returnError(e.toString());
	            }
	        //end of price rules

	        result.put("payheadTypeList", payheadTypeList);
	        result.put("payheadTypeIdsList", payheadTypeIdsList);
	        return result;
	    }
	 public static Map<String, Object> createorUpdateEmployeeDailyAttendance(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String partyId = (String) context.get("partyId");
	        String availedVehicleAllowance = (String)context.get("availedVehicleAllowance");
	        String availedCanteen = (String)context.get("availedCanteen");
	        String shiftType = (String)context.get("shiftType");
	        Timestamp timePeriodStart = (Timestamp)context.get("fromDate");
	        Locale locale = (Locale) context.get("locale");
	        Map result = ServiceUtil.returnSuccess();
	        
			try {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
		        conditionList.add(EntityCondition.makeCondition("date", EntityOperator.EQUALS , UtilDateTime.toSqlDate(timePeriodStart)));
		    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
				List<GenericValue> emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition, null, null, null, false);
				if(UtilValidate.isEmpty(emplDailyAttendanceDetailList)){
					GenericValue newEntity = delegator.makeValue("EmplDailyAttendanceDetail");
					newEntity.set("partyId", partyId);
					newEntity.set("date", UtilDateTime.toSqlDate(timePeriodStart));
					newEntity.set("availedVehicleAllowance", availedVehicleAllowance);
					newEntity.set("availedCanteen", availedCanteen);
					newEntity.set("shiftType", shiftType);
			        try {		
			        	delegator.setNextSubSeqId(newEntity, "seqId", 5, 1);
			        	delegator.create(newEntity);
			        } catch (Exception e) {
			        	Debug.logError("", module);
			            return ServiceUtil.returnError(e.getMessage());
			        }
				}else{	
					for (int i = 0; i < emplDailyAttendanceDetailList.size(); ++i) {
						GenericValue employDetails = emplDailyAttendanceDetailList.get(i);
						employDetails.set("partyId",employDetails.getString("partyId"));
						employDetails.set("availedVehicleAllowance", availedVehicleAllowance);
						employDetails.set("availedCanteen", availedCanteen);
						employDetails.set("shiftType", shiftType);
						employDetails.store();
					}
				}
					
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
	        result = ServiceUtil.returnSuccess("Successfully Updated!!");
	        return result;
	    }//end of service
}

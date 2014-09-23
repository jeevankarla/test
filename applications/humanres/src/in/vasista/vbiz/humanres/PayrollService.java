package in.vasista.vbiz.humanres;

import java.util.ArrayList;
import java.util.Calendar;
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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.util.formula.Evaluator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.TimeDuration;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
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
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
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
				List<GenericValue> billingParty = FastList.newInstance();
				String billingTypeId = "PAYROLL_BILL";			
				List conditionList = FastList.newInstance();
		        List periodBillingList = FastList.newInstance();
		        String partyId = (String) context.get("partyId");
		        if(UtilValidate.isEmpty(partyId)){
					partyId = "Company";
				}	        
		        
		        //code to handle department wise pay bill
		     try{   
		    	 GenericValue tenantConfiguration = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyName", "PAYBILL_DEPTWISE_GEN","propertyTypeEnumId","HUMANRES"), false);
		    	 if(UtilValidate.isNotEmpty(tenantConfiguration)&& ("Y".equals(tenantConfiguration.get("propertyValue")))){
		    		 if(partyIdFrom.equals("Company")){
 			        	List conList= FastList.newInstance();
 			        		conList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,partyIdFrom));
 			        		conList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS , "GROUP_ROLLUP"));
 			        		conList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "ORGANIZATION_UNIT"));
 			        		EntityCondition cond=EntityCondition.makeCondition(conList,EntityOperator.AND);
 				    	try {
 				    		List<GenericValue> internalOrganizations = delegator.findList("PartyRelationship", cond, null,null, null, false);
 				    		billingParty.addAll(internalOrganizations);
 				    	}catch (GenericEntityException e) {
 				    		 Debug.logError(e, module);             
 				             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
 						}			    	
 				    }else{
 				    	List conList= FastList.newInstance();
 		        		conList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyIdFrom));
 		        		conList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS , "GROUP_ROLLUP"));
 		        		conList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "ORGANIZATION_UNIT"));
 		        		EntityCondition cond=EntityCondition.makeCondition(conList,EntityOperator.AND);
 				    	try {
 				    		List<GenericValue> internalOrganizations = delegator.findList("PartyRelationship", cond, null,null, null, false);
 				    		if(UtilValidate.isNotEmpty(internalOrganizations)){
 				    			billingParty.addAll(internalOrganizations);
 				    		}
 				    		
 				    	}catch (GenericEntityException e) {
 				    		 Debug.logError(e, module);             
 				             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
 						}
 				    }   
 			        for(GenericValue eachFacility: billingParty){ 				    	
 				    	List conditionPeriodList = FastList.newInstance();
 				    	String billingPartyId = eachFacility.getString("partyIdTo");
 				    	partyIdFrom=eachFacility.getString("partyIdFrom");
 				        List periodBillingDeptList = FastList.newInstance();
 				        conditionPeriodList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS","APPROVED")));
 				        conditionPeriodList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
 				        conditionPeriodList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , billingTypeId));
 				        conditionPeriodList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , billingPartyId));
 				    	EntityCondition periodCondition=EntityCondition.makeCondition(conditionPeriodList,EntityOperator.AND);
 				    	GenericValue billingId =null;
 				    	try {
 				    		periodBillingDeptList = delegator.findList("PeriodBilling", periodCondition, null,null, null, false);
 				    		if(UtilValidate.isNotEmpty(periodBillingDeptList)){
 				    			continue;	    			
 				    		}
 				    		GenericValue newEntity = delegator.makeValue("PeriodBilling");
 					        newEntity.set("billingTypeId", billingTypeId);
 					        newEntity.set("customTimePeriodId", customTimePeriodId);
 					        newEntity.set("statusId", "IN_PROCESS");
 					        newEntity.set("partyId", billingPartyId);
 					        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
 					        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
 					        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
 					        newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
 					        delegator.createSetNextSeqId(newEntity); 					        
 							periodBillingId = (String) newEntity.get("periodBillingId");	
 							Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"userLogin", userLogin);
 							runSACOContext.put("partyIdFrom", billingPartyId);
 							runSACOContext.put("partyId",  partyIdFrom);
 							runSACOContext.put("periodBillingId", periodBillingId);
 							dispatcher.runAsync("generatePayrollBilling", runSACOContext,false);
 							if(ServiceUtil.isError(result)){
 		       					Debug.logError("Problems in service Parol Header", module);
 					  			return ServiceUtil.returnError("Problems in service Parol Header");
 		       				}
 				    	} catch (GenericEntityException e) {
 							Debug.logError(e,"Failed To Create New Period_Billing", module);
 							return ServiceUtil.returnError("Problems in service Parol Header");
 						}
 				        catch (GenericServiceException e) {
 				            Debug.logError(e, "Error in calling 'period Billing' service", module);
 				            return ServiceUtil.returnError(e.getMessage());
 				        } 
 				        result.put("periodBillingId", periodBillingId);
 				        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);  
 				    	
 				    }	
 			   }else{
	 				  	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS","APPROVED")));
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
	 						dispatcher.runAsync("generatePayrollBilling", runSACOContext,false);
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
 			   }
		     } catch (GenericEntityException e) {
					Debug.logError(e,"Unable  To get Tenant Configuration", module);
						return ServiceUtil.returnError("Unable  To get Tenant Configuration");
			}	       
		    	return result;
		
			}
			public static Map<String, Object> generatePayrollBilling(DispatchContext dctx, Map<String, Object> context) throws Exception{
				GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				String periodBillingId = (String) context.get("periodBillingId");
				GenericValue periodBilling = null;
				Map result = ServiceUtil.returnSuccess();
				result.put("periodBillingId", periodBillingId);
				boolean beganTransaction = false;
		        	
				try {
					beganTransaction = TransactionUtil.begin(72000);
					periodBilling =delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
				
					result =  generatePayrollBillingInternal(dctx, context);
	        		if(ServiceUtil.isError(result)){
	        			try {
			                // only rollback the transaction if we started one...
			                TransactionUtil.rollback();
			            } catch (Exception e2) {
			                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
			            }
	        			 Debug.logError(ServiceUtil.getErrorMessage(result), module);
	        			 periodBilling.set("statusId", "GENERATION_FAIL");
						 periodBilling.store();
						 result = ServiceUtil.returnSuccess(ServiceUtil.getErrorMessage(result));
					     result.put("periodBillingId", periodBillingId);
	 		             return result;
	        		}
        		
				} catch (Exception e1) {
					try {
		                // only rollback the transaction if we started one...
		                TransactionUtil.rollback();
		            } catch (Exception e2) {
		                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
		            }
					Debug.logError(e1,"Error While Finding PeriodBilling");
					 periodBilling.set("statusId", "GENERATION_FAIL");
					 periodBilling.store();
					 result = ServiceUtil.returnSuccess("Error While Finding PeriodBilling" + e1);
				     result.put("periodBillingId", periodBillingId);
					return result;
				}
				return result;
				
			}
			
			public static Map<String, Object> generatePayrollBillingInternal(DispatchContext dctx, Map<String, Object> context) throws Exception{
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
				boolean beganTransaction = false;
				try{
					beganTransaction = TransactionUtil.begin(7200);
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
						 TransactionUtil.rollback();
						Debug.logError(e1,"Error While Finding Customtime Period");
						return ServiceUtil.returnError("Error While Finding Customtime Period" + e1);
					}
					if(customTimePeriod == null){
						generationFailed = true;
					}
					Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
					Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
					
					Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
					Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
					try {
						Map input = FastMap.newInstance();
					   
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
				  			return ServiceUtil.returnError("Problems in service Parol Header");
	       				}
	       				int emplCounter = 0;
	    	    		double elapsedSeconds;
	    	    	    Timestamp startTimestamp = UtilDateTime.nowTimestamp();
	    	    	    
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
							inputItem.put("periodBillingId", periodBillingId);
							emplCounter++;
		               		if ((emplCounter % 20) == 0) {
		               			elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
		               			Debug.logImportant("Completed " + emplCounter + " employee's [ in " + elapsedSeconds + " seconds]", module);
		               		}
							Map payHeadItemResult = preparePayrolItems(dctx,inputItem);
							if(ServiceUtil.isError(payHeadItemResult)){
		       					Debug.logError("Problems in service Parol Header Item", module);
					  			return ServiceUtil.returnError("Problems in service Parol Header Item");
		       				}
							List payHeaderItemList = (List)payHeadItemResult.get("itemsList");
							for(int j=0;j< payHeaderItemList.size();j++){
								Map payHeaderItemValue = (Map)payHeaderItemList.get(j);
								if(UtilValidate.isEmpty(payHeaderItemValue.get("amount")) || (((BigDecimal)payHeaderItemValue.get("amount")).compareTo(BigDecimal.ZERO) ==0) ){
									continue;
								}
		       					GenericValue payHeaderItem = delegator.makeValue("PayrollHeaderItem");
		       					payHeaderItem.set("payrollHeaderId", payHeader.get("payrollHeaderId"));
		       					payHeaderItem.set("payrollHeaderItemTypeId",payHeaderItemValue.get("payrollItemTypeId"));
		       					payHeaderItem.set("amount", ((BigDecimal)payHeaderItemValue.get("amount")).setScale(0, BigDecimal.ROUND_HALF_UP));
		       				    delegator.setNextSubSeqId(payHeaderItem, "payrollItemSeqId", 5, 1);
					            delegator.create(payHeaderItem);
					            //now populate item ref to loan recovery ,if the current head is loan deduction
					            List condList = FastList.newInstance();
					            condList.add(EntityCondition.makeCondition("payHeadTypeId" ,EntityOperator.EQUALS ,payHeaderItemValue.get("payrollItemTypeId")));
					            condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.EQUALS ,"LOAN_DISBURSED"));
					            condList.add(EntityCondition.makeCondition("customTimePeriodId" ,EntityOperator.EQUALS ,customTimePeriodId));
					            condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS ,payHeader.get("partyIdFrom")));
					            EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
					            List<GenericValue> revoveryList = delegator.findList("LoanAndRecoveryAndType", cond, null, null, null, false);
					            if(UtilValidate.isNotEmpty(revoveryList)){
					            	GenericValue entry = EntityUtil.getFirst(revoveryList);
					            	GenericValue recovery = delegator.findOne("LoanRecovery",UtilMisc.toMap("loanId",entry.getString("loanId"),"sequenceNum",entry.getString("sequenceNum")),false);
					            	recovery.set("payrollHeaderId", payHeaderItem.getString("payrollHeaderId"));
					            	recovery.set("payrollItemSeqId", payHeaderItem.getString("payrollItemSeqId"));
					            	delegator.store(recovery);
					            	
					            }
					            
							}
	       				}
						
					}catch (Exception e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError("Error While generating PeriodBilling" + e);
					}
					if (generationFailed) {
						periodBilling.set("statusId", "GENERATION_FAIL");
						Debug.logError("Error While generating PeriodBilling", module);
						return ServiceUtil.returnError("Error While generating PeriodBilling");
					} else {
						periodBilling.set("statusId", "GENERATED");
						periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					}
					periodBilling.store();	
					
			}catch (GenericEntityException e) {
					Debug.logError(e, module);
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
		    				if(UtilValidate.isNotEmpty(payrollHeaderItemList) && (periodBilling.getString("statusId").equals("GENERATED"))){
		    					List<GenericValue> loanRecoveryList = delegator.findList("LoanRecovery", EntityCondition.makeCondition("payrollHeaderId",EntityOperator.IN,
		    							EntityUtil.getFieldListFromEntityList(payrollHeaderList, "payrollHeaderId", true)), null, null, null, false);
		    					if(UtilValidate.isNotEmpty(loanRecoveryList)){
		    						delegator.removeAll(loanRecoveryList);
		    					}
		    					delegator.removeAll(payrollHeaderItemList);
		    	    		    delegator.removeAll(payrollHeaderList);
		    	    		}
		    			}
		    			periodBilling.set("statusId", "COM_CANCELLED");
		    			periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    			periodBilling.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						periodBilling.store();
		    		}else{
		    			periodBilling.set("statusId", "CANCEL_FAILED");
		    			periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    			periodBilling.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		    			periodBilling.store();
		    		}
		    	}catch (GenericEntityException e) {
		    		 Debug.logError(e, module);
		             return ServiceUtil.returnError("Failed to find payrollHeaderItemList " + e);
				} 
				result = ServiceUtil.returnSuccess("PayRoll Billing Successfully Cancelled..");
				return result;
		}// end of service
			private static  Map<String, Object> fetchBasicSalaryAndGrade(DispatchContext dctx, Map context) {
				 	GenericValue userLogin = (GenericValue) context.get("userLogin");
			        String payHeadTypeId = (String) context.get("payHeadTypeId");
			        String employeeId = (String) context.get("employeeId");
			        String orgPartyId = (String) context.get("orgPartyId");
			        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
					Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
					String timePeriodId = (String) context.get("timePeriodId");
					double amount = 0;
			        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			        Delegator delegator = dctx.getDelegator();
			        FastMap result = FastMap.newInstance();
			        try {
			            List conditionList = UtilMisc.toList(
			                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId));
			            conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
			            conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
			            EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);        	
			            // sort by -fromDate to get the newest (largest) first, just in case there is more than one, should not happen but...
			            List<GenericValue> payHistory = delegator.findList("PayHistory", condition, null, UtilMisc.toList("-fromDate"), null, false);
			            if(UtilValidate.isEmpty(payHistory)){
			            	result.put("amount", amount);
			            	result.put("payGradeId", "-1");
			            	Debug.logWarning("Zero PayHistory records for partyId '" + employeeId + "'", module);            	
			            	return result;
			            }
		            	GenericValue row = payHistory.get(0);
		            	String payGradeId = row.getString("payGradeId");
		            	String salaryStepSeqId = row.getString("salaryStepSeqId");
		            	GenericValue salaryStep = delegator.findOne("SalaryStep", UtilMisc.toMap(
		            			"payGradeId", payGradeId, "salaryStepSeqId", salaryStepSeqId), false);
		            	if(UtilValidate.isEmpty(salaryStep)){
			            	Debug.logError("Invalid SalaryStep record ::"+UtilMisc.toMap(
			            			"payGradeId", payGradeId, "salaryStepSeqId", salaryStepSeqId), module);
			            	
			            	return ServiceUtil.returnError("Invalid SalaryStep record ::"+UtilMisc.toMap(
		            			"payGradeId", payGradeId, "salaryStepSeqId", salaryStepSeqId));
		            	}
		            	GenericValue payGrade = delegator.findOne("PayGrade", UtilMisc.toMap(
		            			"payGradeId", payGradeId), true);
		            	result.put("payGradeId", payGrade.get("seqId"));
		                amount = salaryStep.getDouble("amount");
		                
		            	//adjust basic here
		                Map employeePayrollAttedance = getEmployeePayrollAttedance(dctx,context);
		                //context.put("lossOfPayDays",employeePayrollAttedance.get("lossOfPayDays"));
		                context.put("noOfPayableDays",employeePayrollAttedance.get("noOfPayableDays"));
		            	Timestamp from = row.getTimestamp("fromDate");
			            Timestamp thru = row.getTimestamp("thruDate");
			            if(UtilValidate.isEmpty(context.get("proportionalFlag"))){
			            	context.put("proportionalFlag", "Y");
			            }
			            Map<String, Object> adjustment = adjustAmount(context,salaryStep.getBigDecimal("amount"), from, thru);
		                result.put("amount", ((BigDecimal)adjustment.get("amount")).doubleValue());
			        }
			        catch (GenericEntityException e) {
			            Debug.logError(e, "Error retrieving PayHistory records for partyId '" + employeeId + "'", module);
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
		        Debug.logInfo("proportionalFlagStr==========="+proportionalFlagStr ,module);
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
				BigDecimal noOfPayableDays = periodDays;
				if(UtilValidate.isNotEmpty(context.get("noOfPayableDays"))){
					noOfPayableDays = new BigDecimal((Double)context.get("noOfPayableDays"));
				}
				if (propFlag) {
					// loss of pay days adjustment
					//BigDecimal lossOfPayDays = new BigDecimal((Double)context.get("lossOfPayDays"));
					
					if(UtilValidate.isNotEmpty(noOfPayableDays)){
						BigDecimal payDays = noOfPayableDays;
						amount = amount.multiply(payDays).divide(BigDecimal.valueOf(payrollPeriodDays), 0, BigDecimal.ROUND_HALF_UP);
					}
				}
				//this to handle derived basic and actual basic no.ofpayable days is then make actual basic zero
				if(noOfPayableDays.compareTo(BigDecimal.ZERO) ==0 ){
					BigDecimal payDays = noOfPayableDays;
					amount = amount.multiply(payDays).divide(BigDecimal.valueOf(payrollPeriodDays), 0, BigDecimal.ROUND_HALF_UP);
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
		        String periodBillingId= (String) context.get("periodBillingId");
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
					GenericValue benefitTypeRow = delegator.findOne("BenefitType", UtilMisc.toMap(
		        			"benefitTypeId", benefitTypeId), false);
		        	context.put("proportionalFlag", benefitTypeRow.getString("proportionalFlag"));
					//if empty cost then look in rule engine
					/*if(UtilValidate.isEmpty(benefit.getBigDecimal("cost"))){
						Map payHeadCtx = UtilMisc.toMap("userLogin", userLogin);				
						payHeadCtx.put("payHeadTypeId", benefitTypeId);
						payHeadCtx.put("timePeriodStart", timePeriodStart);
						payHeadCtx.put("timePeriodEnd", timePeriodEnd);
						payHeadCtx.put("timePeriodId", timePeriodId);
						payHeadCtx.put("employeeId", partyId);
						payHeadCtx.put("proportionalFlag",context.get("proportionalFlag"));
						Map<String, Object> result = calculatePayHeadAmount(dctx,payHeadCtx);
						if(ServiceUtil.isError(result)){
		        			 Debug.logError(ServiceUtil.getErrorMessage(result), module);
		 		             return result;
		        		}
						if(UtilValidate.isNotEmpty(result)){
							benefit.set("cost" ,result.get("amount"));
						}
					}*/
		        	Map payHeadCtx = UtilMisc.toMap("userLogin", userLogin);				
					payHeadCtx.put("payHeadTypeId", benefitTypeId);
					payHeadCtx.put("timePeriodStart", timePeriodStart);
					payHeadCtx.put("timePeriodEnd", timePeriodEnd);
					payHeadCtx.put("timePeriodId", timePeriodId);
					payHeadCtx.put("employeeId", partyId);
					payHeadCtx.put("periodBillingId", periodBillingId);
					payHeadCtx.put("proportionalFlag",context.get("proportionalFlag"));
					Map<String, Object> result = getPayHeadAmount(dctx,payHeadCtx);
					if(ServiceUtil.isError(result)){
	        			 Debug.logError(ServiceUtil.getErrorMessage(result), module);
	 		             return result;
	        		}
					if(UtilValidate.isNotEmpty(result)){
						benefit.set("cost" ,result.get("amount"));
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
				String periodBillingId = (String) context.get("periodBillingId");
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
					/*if(UtilValidate.isEmpty(deduction.getBigDecimal("cost"))){
						Map payHeadCtx = UtilMisc.toMap("userLogin", userLogin);				
						payHeadCtx.put("payHeadTypeId", deductionTypeId);
						payHeadCtx.put("timePeriodStart", timePeriodStart);
						payHeadCtx.put("timePeriodEnd", timePeriodEnd);
						payHeadCtx.put("timePeriodId", timePeriodId);
						payHeadCtx.put("employeeId", partyId);
						payHeadCtx.put("proportionalFlag",context.get("proportionalFlag"));
						Map<String, Object> result = calculatePayHeadAmount(dctx,payHeadCtx);
						if(ServiceUtil.isError(result)){
		        			 Debug.logError(ServiceUtil.getErrorMessage(result), module);
		 		             return result;
		        		}
						if(UtilValidate.isNotEmpty(result)){
							deduction.set("cost" ,result.get("amount"));
						}
					}*/
					Map payHeadCtx = UtilMisc.toMap("userLogin", userLogin);				
					payHeadCtx.put("payHeadTypeId", deductionTypeId);
					payHeadCtx.put("timePeriodStart", timePeriodStart);
					payHeadCtx.put("timePeriodEnd", timePeriodEnd);
					payHeadCtx.put("timePeriodId", timePeriodId);
					payHeadCtx.put("employeeId", partyId);
					payHeadCtx.put("periodBillingId", periodBillingId);
					payHeadCtx.put("proportionalFlag",context.get("proportionalFlag"));
					Map<String, Object> result = getPayHeadAmount(dctx,payHeadCtx);
					if(ServiceUtil.isError(result)){
	        			 Debug.logError(ServiceUtil.getErrorMessage(result), module);
	 		             return result;
	        		}
					if(UtilValidate.isNotEmpty(result)){
						deduction.set("cost" ,result.get("amount"));
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
				List itemsList = FastList.newInstance();
				
				context.put("employeeId", partyId);
				Map fetchBasicSalaryAndGradeMap = fetchBasicSalaryAndGrade(dctx, context);
				Map input = UtilMisc.toMap("payrollItemTypeId", "PAYROL_BEN_SALARY");
			    input.put("quantity", BigDecimal.ONE);
	            input.put("amount",new BigDecimal((Double)fetchBasicSalaryAndGradeMap.get("amount")));
				itemsList.add(input);
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
				
				// First, lets generate pay Header item(s) for basic salary
				try {
					serviceResults = createPayrolBasicSalaryItems(dctx, context);
			        if (ServiceUtil.isError(serviceResults)) {
			        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
			        }
			       if(UtilValidate.isNotEmpty(serviceResults.get("itemsList"))){
			    	   itemsList.addAll((List)serviceResults.get("itemsList"));
			       }
				}catch (Exception e) {
					Debug.logError(e, errorMsg + "Unable to create payroll Item records for basic salary: " + e.getMessage(), module);
			        return ServiceUtil.returnError(errorMsg + "Unable to create payroll Item records for basic salary: " + e.getMessage());
			    }        
				// Create payhead items for benefits
				try {
					serviceResults = createPayrolBenefitItems(dctx, context);
			        if (ServiceUtil.isError(serviceResults)) {
			        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
			        }
			        if(UtilValidate.isNotEmpty(serviceResults.get("itemsList"))){
				    	   itemsList.addAll((List)serviceResults.get("itemsList"));
				    	   
				     }
				}catch (Exception e) {
					Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage(), module);
			        return ServiceUtil.returnError(errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage());
			    }   		
				// Create payhead items for deductions
				try {
					serviceResults = createPayrolDeductionItems(dctx, context);
			        if (ServiceUtil.isError(serviceResults)) {
			        	return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
			        }
			        if(UtilValidate.isNotEmpty(serviceResults.get("itemsList"))){
				    	   itemsList.addAll((List)serviceResults.get("itemsList"));
				    	   
				     }
 				}catch (Exception e) {
					Debug.logError(e, errorMsg + "Unable to create payroll InvoiceItem records for benefits: " + e.getMessage(), module);
			        return ServiceUtil.returnError(errorMsg + "Unable to create payroll payHeadItem records for deductions: " + e.getMessage());
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
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				List itemsList = FastList.newInstance();
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
			       /* List<String> internalOrgs = PartyWorker.getAssociatedPartyIdsByRelationshipType(delegator, partyIdFrom, "GROUP_ROLLUP");
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
			        }*/
					Map emplInputMap = FastMap.newInstance();
					emplInputMap.put("userLogin", userLogin);
					emplInputMap.put("orgPartyId", partyIdFrom);
					emplInputMap.put("fromDate", timePeriodStart);
					emplInputMap.put("thruDate", timePeriodEnd);
		        	Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
		        	List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
		        	for (int i = 0; i < employementList.size(); ++i) {		
		        		GenericValue employment = employementList.get(i);
		        		String employeeId = employment.getString("partyIdTo");
		        		context.put("employeeId", employeeId);
		        		Map employeePayrollAttedance = getEmployeePayrollAttedance(dctx,context);
		        		if(UtilValidate.isNotEmpty(employeePayrollAttedance.get("noOfPayableDays")) &&
		        				(new BigDecimal((Double)employeePayrollAttedance.get("noOfPayableDays"))).compareTo(BigDecimal.ZERO)==0){
		        			  continue;
		        		}
		        		input.put("partyIdFrom", employment.getString("partyIdTo"));
						Map tempInputMap = FastMap.newInstance();
						tempInputMap.putAll(input);
						itemsList.add(tempInputMap);
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
		        String orgPartyId = (String) context.get("orgPartyId");
		        String timePeriodId = (String)context.get("customTimePeriodId");
		        String periodBillingId = (String)context.get("periodBillingId");
		        
		        if(UtilValidate.isNotEmpty(context.get("timePeriodId"))){
		        	timePeriodId = (String)context.get("timePeriodId");
		        }
		        Debug.logInfo("calculating ########:"+payHeadTypeId,module);
		        Locale locale = (Locale) context.get("locale");
		        BigDecimal amount = BigDecimal.ZERO;
		        String proportionalFormulaId = null;
		        try{
		        	GenericValue customTimePeriod;
					try {
						customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", timePeriodId), false);
						 Debug.logInfo("timePeriodId ########:"+timePeriodId,module);
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
			        
					Map payheadAmtCtx = FastMap.newInstance();
                    payheadAmtCtx.put("userLogin", userLogin);
                    payheadAmtCtx.put("employeeId", employeeId);
                    payheadAmtCtx.put("timePeriodStart", timePeriodStart);
                    payheadAmtCtx.put("timePeriodEnd", timePeriodEnd);
                    payheadAmtCtx.put("timePeriodId", timePeriodId);
                    payheadAmtCtx.put("payHeadTypeId", payHeadTypeId);
                    payheadAmtCtx.put("periodBillingId", periodBillingId);
                    
                    GenericValue deductionTypeRow = delegator.findOne("DeductionType", UtilMisc.toMap(
		        			"deductionTypeId", payHeadTypeId), false);
                    if(UtilValidate.isNotEmpty(deductionTypeRow)){
                    	 payheadAmtCtx.put("proportionalFlag", deductionTypeRow.getString("proportionalFlag"));
                    	 proportionalFormulaId = deductionTypeRow.getString("proportionalFormulaId");
                    }
                    GenericValue benifitTypeRow = delegator.findOne("BenefitType", UtilMisc.toMap(
		        			"benefitTypeId", payHeadTypeId), false);
                    if(UtilValidate.isNotEmpty(benifitTypeRow)){
                    	 payheadAmtCtx.put("proportionalFlag", benifitTypeRow.getString("proportionalFlag"));
                    	 proportionalFormulaId = benifitTypeRow.getString("proportionalFormulaId");
                    }
                    if(UtilValidate.isNotEmpty(context.get("proportionalFlag"))){
                    	payheadAmtCtx.put("proportionalFlag", context.get("proportionalFlag"));
		            }
                    
                    if(UtilValidate.isNotEmpty(paheadType) && UtilValidate.isNotEmpty(paheadType.getBigDecimal("cost"))){
			        	 result.put("amount", paheadType.getBigDecimal("cost"));
			        	 if(UtilValidate.isNotEmpty(proportionalFormulaId)){
			        		 //Debug.log("proportionalFormulaId==="+proportionalFormulaId);
			        		 Map payAttCtx = FastMap.newInstance();
			        		 payAttCtx.put("userLogin", userLogin);
			        		 payAttCtx.put("employeeId", employeeId);
			        		 payAttCtx.put("timePeriodStart", timePeriodStart);
			        		 payAttCtx.put("timePeriodEnd", timePeriodEnd);
			        		 payAttCtx.put("timePeriodId", timePeriodId);
			        		Map attendanceMap = getEmployeePayrollAttedance(dctx ,payAttCtx);
			        		//Debug.log("attendanceMap==="+attendanceMap);
			        		Evaluator evltr = new Evaluator(dctx);
	    					HashMap<String, Double> variables = new HashMap<String, Double>();
 							variables.put("NOOFCALENDERDAYS", (Double)attendanceMap.get("noOfCalenderDays"));
 							variables.put("NOOFATTENDEDDAYS", (Double)attendanceMap.get("noOfAttendedDays"));
 							variables.put("NOOFATTENDEDHOLIDAYS", (Double)attendanceMap.get("noOfAttendedHoliDays"));
 							variables.put("LOSSOFPAYDAYS", (Double)attendanceMap.get("lossOfPayDays"));
 							variables.put("NOOFATTENDEDSS", (Double)attendanceMap.get("noOfAttendedSsDays"));
 							variables.put("NOOFATTENDEDWEEKLYOFF", (Double)attendanceMap.get("noOfAttendedWeeklyOffDays"));
 							variables.put("NOOFLEAVEDAYS", (Double)attendanceMap.get("noOfLeaveDays"));
 							variables.put("NOOFCOMPOFFSAVAILED", (Double)attendanceMap.get("noOfCompoffAvailed"));
 							variables.put("NOOFAVAILEDVEHICLEDAYS", (new Double((Integer)attendanceMap.get("availedVehicleDays"))));
 							variables.put("NOOFPAYABLEDAYS", (Double)attendanceMap.get("noOfPayableDays"));
 							//Debug.log("variables==="+variables);
 							double noOfAttendedDays = ((Double)attendanceMap.get("noOfAttendedDays")).doubleValue();
 							evltr.setFormulaIdAndSlabAmount(proportionalFormulaId, noOfAttendedDays);
 							evltr.addVariableValues(variables);  
 							//Debug.log("variables==="+variables);
 							BigDecimal proportionalFactor = new BigDecimal(evltr.evaluate());
 							result.put("amount", (paheadType.getBigDecimal("cost").multiply(proportionalFactor)).setScale(0, BigDecimal.ROUND_HALF_UP));
 							
			        	 }
			        	 result.put("priceInfos",UtilMisc.toList("Party level price"));
			        	 return result;
			        }
                    
                    
	                Map<String, Object> calcResults = calculatePayHeadAmount(dctx,payheadAmtCtx);
	                Debug.logInfo("calcResults in calculatePayHeadAmount ############################"+calcResults ,module);
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
		        //Debug.log("In calculatePayHeadAmount ##################################"+payHeadTypeId);
		        try{
			           		         
		        	    Map makePayHedPrice = FastMap.newInstance();
		        	    makePayHedPrice.put("userLogin",userLogin);
		        	    makePayHedPrice.put("payHeadTypeId",payHeadTypeId);
		        	    makePayHedPrice.put("fromDate",timePeriodStart);
		        	    //Debug.log("makePayHedPrice ######"+makePayHedPrice);
		                List<GenericValue> allBenDedPriceRules = makePayHeadPriceRuleList(dctx, makePayHedPrice);
		                Debug.logInfo("allBenDedPriceRules ######"+allBenDedPriceRules , module);
		                allBenDedPriceRules = EntityUtil.filterByDate(allBenDedPriceRules, true);
	               
	                    Map priceResultRuleCtx = FastMap.newInstance();
	                    priceResultRuleCtx.putAll(context);
	                    priceResultRuleCtx.put("payHeadPriceRules", allBenDedPriceRules);
	                   // Debug.log("priceResultRuleCtx ####################### ######"+priceResultRuleCtx);
	                    Map<String, Object> calcResults = calcPriceResultFromRules(dctx,priceResultRuleCtx);
	                    Debug.logInfo("calcResults ######"+calcResults , module);
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
	        EntityCondition condition = EntityCondition.makeCondition("payHeadTypeId", EntityOperator.EQUALS,payHeadTypeId); 				
        	payHeadPriceRules = delegator.findList("PayrollBenDedRule", condition, null, null, null, false);
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
                if(UtilValidate.isEmpty(payHeadPriceRules)){
                	Debug.logImportant("no rules found for given payheadType ::"+context.get("payHeadTypeId"), module);
                    return calcResults;
                }
	            for (GenericValue payHeadPriceRule: payHeadPriceRules) {
	                String payHeadPriceRuleId = payHeadPriceRule.getString("payrollBenDedRuleId");

	                // check all conditions
	                boolean allTrue = true;
	                
	                StringBuilder condsDescription = new StringBuilder();
	                List<GenericValue> payrollBenDedCondList = delegator.findList("PayrollBenDedCond", EntityCondition.makeCondition("payrollBenDedRuleId",EntityOperator.EQUALS ,payHeadPriceRuleId ), null,UtilMisc.toList("payrollBenDedCondSeqId") , null, false);	
	              //this is to support rule with no condition 
	                if(UtilValidate.isNotEmpty(condParms) && UtilValidate.isEmpty(payrollBenDedCondList)){
	                	allTrue = false; 
	                }
	                for (GenericValue payrollBenDedCond : payrollBenDedCondList) {
	                	
                         if(UtilValidate.isNotEmpty(condParms)){
                        	 if (!checkPriceCondition(payrollBenDedCond,userLogin,employeeId, dctx, delegator, timePeriodStart ,timePeriodEnd ,timePeriodId , condParms)) {
     	                        allTrue = false;
     	                        break;
     	                    }
                         }else{
                        	 if (!checkPriceCondition(payrollBenDedCond, userLogin ,employeeId, dctx, delegator, timePeriodStart ,timePeriodEnd, timePeriodId ,null)) {
     	                        allTrue = false;
     	                        break;
     	                    }
                         }
                         Debug.logInfo("###allTrue each#########"+allTrue+"==================="+payrollBenDedCond , module);
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
	                //Debug.log("allTrue #############################################"+allTrue);
	                // if all true, perform all actions
	                BigDecimal modifyAmount = BigDecimal.ZERO;
	                if (allTrue) {
	                	StringBuilder priceInfoDescription = new StringBuilder();
	                	Map fetchBasicSalaryAndGradeMap = fetchBasicSalaryAndGrade(dctx, context);
	                    List<GenericValue> payHeadPriceActions = delegator.findByAndCache("PayHeadPriceAction", UtilMisc.toMap("payrollBenDedRuleId", payHeadPriceRuleId));
	                    payHeadPriceActions = EntityUtil.filterByDate(payHeadPriceActions,timePeriodStart);
	                    for (GenericValue payHeadPriceAction: payHeadPriceActions) {
	                        // yeah, finally here, perform the action, ie, modify the amount
	     	                    priceInfoDescription.append(condsDescription.toString());
	     	                    priceInfoDescription.append("[");
	     	                    priceInfoDescription.append("type:");
	     	                    priceInfoDescription.append(payHeadPriceAction.getString("payHeadPriceActionTypeId"));
	     	                    priceInfoDescription.append("]\n");
	     	                    //Debug.log(payHeadPriceAction+"########payHeadPriceAction ############");
	                        if ("PRICE_FLAT".equals(payHeadPriceAction.getString("payHeadPriceActionTypeId"))) {
	                            String formulaId = payHeadPriceAction.getString("acctgFormulaId");
	                            if (UtilValidate.isNotEmpty(formulaId)) {
	                            
		    		        		Evaluator evltr = new Evaluator(dctx);
		    		        		//Debug.log("*********** formulaId ================"+formulaId);
		    		        		evltr.setFormulaIdAndSlabAmount(formulaId, modifyAmount.doubleValue());
		    						HashMap<String, Double> variables = new HashMap<String, Double>();
		    						Map formulaVaribules = evltr.getVariableValues();
		    						double attendance = 1;
		    						Set<String> varibuleKeySet = formulaVaribules.keySet();
		    						
		    						ArrayList varibuleKeyList = new ArrayList(varibuleKeySet);
		    						List payheadTypeIdsList = (List)((getPayheadTypes(dctx , context)).get("payheadTypeIdsList"));
		    						//Debug.log("*********** varibuleKeySet ================"+varibuleKeySet);
		    						// this to support no.of days in the accounting formula
	    							//  NOOFCALENDERDAYS      NOOFATTENDEDDAYS      LOSSOFPAYDAYS
	    							//  NOOFATTENDEDHOLIDAYS  NOOFATTENDEDSS        NOOFATTENDEDWEEKLYOFF
	    							//  NOOFLEAVEDAYS         NOOFCOMPOFFSAVAILED   GROSSSALARY
		    						// NOOFAVAILEDVEHICLEDAYS  
		    						boolean getAttendance = false;
	    							Set supportedVaribules = UtilMisc.toSet("NOOFCALENDERDAYS","NOOFATTENDEDDAYS","LOSSOFPAYDAYS",
	    									"NOOFATTENDEDHOLIDAYS" ,"NOOFATTENDEDSS" ,"NOOFATTENDEDWEEKLYOFF" ,"NOOFLEAVEDAYS","NOOFCOMPOFFSAVAILED");
	    							supportedVaribules.add("NOOFAVAILEDVEHICLEDAYS");
	    							supportedVaribules.add("NOOFPAYABLEDAYS");
	    							supportedVaribules.add("NOOFARREARDAYS");
	    							
	    							for(int i= 0;i<varibuleKeyList.size();i++){
	    								String varibuleKey = (String)varibuleKeyList.get(i);
	    								if(supportedVaribules.contains(varibuleKey)){
	    									getAttendance =true;
	    								}
	    							}
	    							if(getAttendance){
		    							Map attendanceMap = getEmployeePayrollAttedance(dctx ,context);
		    							
		    							variables.put("NOOFCALENDERDAYS", (Double)attendanceMap.get("noOfCalenderDays"));
		    							variables.put("NOOFATTENDEDDAYS", (Double)attendanceMap.get("noOfAttendedDays"));
		    							variables.put("NOOFATTENDEDHOLIDAYS", (Double)attendanceMap.get("noOfAttendedHoliDays"));
		    							variables.put("LOSSOFPAYDAYS", (Double)attendanceMap.get("lossOfPayDays"));
		    							variables.put("NOOFATTENDEDSS", (Double)attendanceMap.get("noOfAttendedSsDays"));
		    							variables.put("NOOFATTENDEDWEEKLYOFF", (Double)attendanceMap.get("noOfAttendedWeeklyOffDays"));
		    							variables.put("NOOFLEAVEDAYS", (Double)attendanceMap.get("noOfLeaveDays"));
		    							variables.put("NOOFCOMPOFFSAVAILED", (Double)attendanceMap.get("noOfCompoffAvailed"));
		    							variables.put("NOOFAVAILEDVEHICLEDAYS", (new Double((Integer)attendanceMap.get("availedVehicleDays"))));
		    							variables.put("NOOFPAYABLEDAYS", (Double)attendanceMap.get("noOfPayableDays"));
		    							variables.put("NOOFARREARDAYS", (Double)attendanceMap.get("noOfArrearDays"));
		    							
		    							double noOfAttendedDays = ((Double)attendanceMap.get("noOfAttendedDays")).doubleValue();
		    							evltr.setFormulaIdAndSlabAmount(formulaId, noOfAttendedDays);
		    						}
		    						for(int i= 0;i<varibuleKeyList.size();i++){
		    							
		    							// this to support dependent benefit or deductions
		    							String varibuleKey = (String)varibuleKeyList.get(i);
		    							if(payheadTypeIdsList.contains(varibuleKey)){
		    								Map payheadAmtCtx = FastMap.newInstance();
		    			                    payheadAmtCtx.put("userLogin", userLogin);
		    			                    payheadAmtCtx.put("employeeId", employeeId);
		    			                    payheadAmtCtx.put("timePeriodStart", timePeriodStart);
		    			                    payheadAmtCtx.put("timePeriodEnd", timePeriodEnd);
		    			                    payheadAmtCtx.put("timePeriodId", timePeriodId);
		    			                    payheadAmtCtx.put("customTimePeriodId", timePeriodId);
		    			                    payheadAmtCtx.put("payHeadTypeId", varibuleKey);
		    			                    if(UtilValidate.isNotEmpty(context.get("proportionalFlag"))){
		    			                    	payheadAmtCtx.put("proportionalFlag", context.get("proportionalFlag"));
		    					            }
		    			                   // Debug.log("in dependent flag"+payheadAmtCtx);
		    				                Map<String, Object> innerCalcResults = getPayHeadAmount(dctx,payheadAmtCtx);
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
	                    
	                    calcResults.put("amount", modifyAmount.setScale(0, BigDecimal.ROUND_HALF_UP));
	                    priceInfos.add(priceInfoDescription.toString());
	                    calcResults.put("priceInfos", priceInfos);
	                    return calcResults;
	                }
	                calcResults.put("amount", modifyAmount);
	            }

            calcResults.put("priceInfos", priceInfos);
            return calcResults;
	        }  
	 public static boolean checkPriceCondition(GenericValue payrollBenDedCond,GenericValue userLogin, String employeeId,DispatchContext dctx,Delegator delegator, Timestamp fromDate ,Timestamp thruDate ,String timePeriodId , Map condParms) throws GenericEntityException {
	        if (Debug.verboseOn()) Debug.logVerbose("Checking price condition: " + payrollBenDedCond, module);
	        
	      //get Employee Payroll Cond Parms details here
	        String geoId = null;
			String emplPositionTypeId = null;
			String departmentId = null;
			String shiftTypeId = null;
			String otherCond = null;
			String payGradeId = null;
			String stateId =null;
			//String grossSalary = null;
			Map paramCtxMap = UtilMisc.toMap("userLogin",userLogin,"employeeId",employeeId,"timePeriodStart",fromDate,"timePeriodEnd" ,thruDate ,"timePeriodId",timePeriodId);
	        if(UtilValidate.isNotEmpty(condParms)){
	        	 geoId = (String)condParms.get("geoId");
				 emplPositionTypeId = (String)condParms.get("emplPositionTypeId");
				 departmentId = (String)condParms.get("departmentId");
				 shiftTypeId = (String)condParms.get("shiftTypeId");
				 otherCond = (String)condParms.get("otherCond");
				 stateId = (String)condParms.get("stateId");
	        }else{
	        	Map empPositionDetails = getEmployeePayrollCondParms(dctx, paramCtxMap);
	        	if(ServiceUtil.isError(empPositionDetails)){
	            	Debug.logError(ServiceUtil.getErrorMessage(empPositionDetails), module);
	                return false;
	            }
	        	geoId = (String)empPositionDetails.get("geoId");
	        	stateId = (String)empPositionDetails.get("stateId");
				emplPositionTypeId = (String)empPositionDetails.get("emplPositionTypeId");
				departmentId = (String)empPositionDetails.get("departmentId");
				shiftTypeId = (String)empPositionDetails.get("shiftTypeId");
				payGradeId = (String)empPositionDetails.get("payGradeId");
	        }

	        int compare = 0;
            //Debug.log("checking condtion for ::"+payrollBenDedCond);
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
	        }else if ("PAYHD_BEDE_STATE".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	            if (UtilValidate.isNotEmpty(geoId)) {
	                compare = stateId.compareTo(payrollBenDedCond.getString("condValue"));
	            } else {
	                compare = 1;
	            }
	        }
	        else if ("PAYHD_BEDE_DEPT".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
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
	        }else if ("PAYHD_BEDE_GROSS_SAL".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	        	Map grossSalaryMap  = getEmployeeGrossSalary(dctx ,paramCtxMap);
	        	BigDecimal grossSalary = ((BigDecimal)grossSalaryMap.get("amount"));
	        	BigDecimal condValue = new BigDecimal(payrollBenDedCond.getString("condValue"));
	            if (UtilValidate.isNotEmpty(grossSalary)) {
	                compare = grossSalary.compareTo(condValue);
	            } else {
	                compare = 1;
	            }
	        }else if ("PAYHD_BEDE_LEAVEDAYS".equals(payrollBenDedCond.getString("inputParamEnumId"))) {
	        	Map employeePayrollAttedance = getEmployeePayrollAttedance(dctx,paramCtxMap);
	        	int noOfLeaveDays = (((Double)employeePayrollAttedance.get("noOfLeaveDays")).intValue());
	        	int condValue = Integer.parseInt(payrollBenDedCond.getString("condValue"));
	            if (UtilValidate.isNotEmpty(noOfLeaveDays)) {
	                compare = noOfLeaveDays-condValue;
	            } else {
	                compare = 1;
	            }
	        }


	        if (Debug.verboseOn()) Debug.logVerbose("Pay head price condition compare done, compare=" + compare, module);
	        //Debug.log("compare ::"+compare);
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
	        		result.put("stateId", employment.getString("stateGeoId"));
	        		result.put("departmentId", employment.getString("partyIdFrom"));
	        	}
	            
	            GenericValue employeeDetail = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",employeeId), true);
	            if(UtilValidate.isNotEmpty(employeeDetail)){
	            	result.put("vehicleType", employeeDetail.getString("vehicleType"));
	            }
	            
	            // get pay grade here
	            Map fetchBasicSalaryAndGradeMap = fetchBasicSalaryAndGrade(dctx, context);
	            if(ServiceUtil.isError(fetchBasicSalaryAndGradeMap)){
	            	Debug.logError(ServiceUtil.getErrorMessage(fetchBasicSalaryAndGradeMap), module);
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fetchBasicSalaryAndGradeMap));
	            }
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
	    
	 public static Map<String, Object> getPayrollAttedancePeriod(DispatchContext dctx, Map<String, ? extends Object> context) {

	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Map<String, Object> result = FastMap.newInstance();
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
			Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");
			String timePeriodId = (String) context.get("timePeriodId");
	        Locale locale = (Locale) context.get("locale");
	        Map shiftDetailMap = FastMap.newInstance();
	        int availedVehicleDays =0;
	        int disAvailedVehicleDays =0;
	        Map availedCanteenDetailMap = FastMap.newInstance();
     	List conditionList = FastList.newInstance();
	        List<GenericValue> emplDailyAttendanceDetailList = FastList.newInstance();
	        GenericValue lastCloseAttedancePeriod= null;
	        String attendancePeriodId = timePeriodId;
	        try{
	        	EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, 1, 1, true);
	        	conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "ATTENDANCE_MONTH"));
	        	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(timePeriodEnd)));
	        	EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	        	List<GenericValue> attendancePeriodList = delegator.findList("CustomTimePeriod",cond, null, UtilMisc.toList("-thruDate"), null, false);
	        	 //result = dispatcher.runSync("findLastClosedDate", UtilMisc.toMap("organizationPartyId", "Company", "periodTypeId", "ATTENDANCE_MONTH","userLogin", userLogin));
	  	    	if(ServiceUtil.isError(result)){
	 	 	    	Debug.logError("Error in service findLastClosedDate ", module);    			
	 	 		    return ServiceUtil.returnError("Error in service findLastClosedDate");
	 	 	    }
	  	    	//lastCloseAttedancePeriod = ((GenericValue)result.get("lastClosedTimePeriod"))
	  	    	if(UtilValidate.isNotEmpty(attendancePeriodList)){
	  	    		lastCloseAttedancePeriod = EntityUtil.getFirst(attendancePeriodList);
	  	    	}else{
	  	    		lastCloseAttedancePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",timePeriodEnd), true);
	  	    	}
	  	    	
	        }catch (Exception e) {
				// TODO: handle exception
			}
	      result.put("lastCloseAttedancePeriod", lastCloseAttedancePeriod);
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
	        int disAvailedVehicleDays =0;
	        Map availedCanteenDetailMap = FastMap.newInstance();
        	List conditionList = FastList.newInstance();
	        List<GenericValue> emplDailyAttendanceDetailList = FastList.newInstance();
	        GenericValue lastCloseAttedancePeriod= null;
	        String attendancePeriodId = timePeriodId;
	        try{
	        	 result = getPayrollAttedancePeriod(dctx,context);
	  	    	if(ServiceUtil.isError(result)){
	 	 	    	Debug.logError("Error in service findLastClosed Attedance Date ", module);    			
	 	 		    return ServiceUtil.returnError("Error in service findLast Closed Attedance Date");
	 	 	    }
	  	    	//lastCloseAttedancePeriod = ((GenericValue)result.get("lastClosedTimePeriod"))
	  	    	if(UtilValidate.isNotEmpty(result.get("lastCloseAttedancePeriod"))){
	  	    		lastCloseAttedancePeriod = (GenericValue)result.get("lastCloseAttedancePeriod");
		  	    	attendancePeriodId = lastCloseAttedancePeriod.getString("customTimePeriodId");
	  	    	}
	  	    	
	        }catch (Exception e) {
				// TODO: handle exception
			}
	       /*Debug.logInfo("lastCloseAttedancePeriod==========="+lastCloseAttedancePeriod,module);
	        conditionList.clear();
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
	    		Map input =FastMap.newInstance();
	    		input.put("userLogin", userLogin);
	    		input.put("partyId", employeeId);
	    		input.put("timePeriodStart", timePeriodStart);
	    		input.put("timePeriodEnd", timePeriodEnd);
	    		input.put("leaveTypeId", "HPL");
	    		Map resultMap = EmplLeaveService.fetchLeaveDaysForPeriod(dctx, input);
	    		List<GenericValue> leaves = (List)resultMap.get("leaves");
	    		input.put("leaveTypeId", "CML");
	    		resultMap = EmplLeaveService.fetchLeaveDaysForPeriod(dctx, input);
	    		leaves.addAll((List)resultMap.get("leaves"));
	    		emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition, null,null, null, false);
	    		if(UtilValidate.isNotEmpty(emplDailyAttendanceDetailList)){
	    			availedVehicleDays = (EntityUtil.filterByAnd(emplDailyAttendanceDetailList, UtilMisc.toMap("availedVehicleAllowance" ,"Y"))).size();
	    			disAvailedVehicleDays = (EntityUtil.filterByAnd(emplDailyAttendanceDetailList, UtilMisc.toMap("availedVehicleAllowance" ,"N"))).size();
		    		for( GenericValue  emplDailyAttendanceDetail : emplDailyAttendanceDetailList){
		    			String shiftType = emplDailyAttendanceDetail.getString("shiftType");
		    			if(UtilValidate.isNotEmpty(leaves)){
		    				List cDayLeaves = EntityUtil.filterByDate(leaves, UtilDateTime.toTimestamp(emplDailyAttendanceDetail.getString("date")));
			    			if(UtilValidate.isNotEmpty(cDayLeaves)){
			    				continue;
			    			}
		    			}
		    			
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
			    				availedCanteenDetailMap.put(shiftType,(((Integer)(availedCanteenDetailMap.get(shiftType))).intValue()+1));
			    			}
		    			}
		    		}
	    		}
	    		
*/	    
	        
	        
	     try {
	    	 
			List<GenericValue> payrollAttendanceShiftWiseList = delegator.findByAnd("PayrollAttendanceShiftWise", UtilMisc.toMap("partyId",employeeId,"customTimePeriodId",attendancePeriodId));
			if(UtilValidate.isNotEmpty(payrollAttendanceShiftWiseList)){
	    		for( GenericValue  payrollAttendanceShiftWise : payrollAttendanceShiftWiseList){
	    			String shiftType = payrollAttendanceShiftWise.getString("shiftTypeId");
	    			shiftDetailMap.put(shiftType, payrollAttendanceShiftWise.getBigDecimal("noOfDays").intValue());
	    			availedCanteenDetailMap.put(shiftType,0);
	    			if(UtilValidate.isNotEmpty(payrollAttendanceShiftWise.getBigDecimal("availedCanteenDays"))){
	    				availedCanteenDetailMap.put(shiftType, payrollAttendanceShiftWise.getBigDecimal("availedCanteenDays").intValue());
	    			}
	    			
	    		}
    		}
			
			GenericValue payrollAttendance = delegator.findOne("PayrollAttendance", UtilMisc.toMap("partyId",employeeId,"customTimePeriodId",attendancePeriodId), false);
			result.put("lossOfPayDays", 0.0);
			result.put("noOfAttendedDays",0.0);
			result.put("noOfCalenderDays", (new Double((UtilDateTime.getIntervalInDays(timePeriodStart, timePeriodEnd))+1)).doubleValue());
			result.put("noOfAttendedHoliDays", 0.0);
			result.put("noOfAttendedSsDays", 0.0);
			result.put("noOfAttendedWeeklyOffDays", 0.0);
			result.put("noOfCompoffAvailed", 0.0);
			result.put("noOfLeaveDays", 0.0);
			result.put("noOfPayableDays",result.get("noOfCalenderDays"));
			result.put("noOfArrearDays", 0.0);
			result.put("lateMin", 0.0);
			result.put("extraMin", 0.0);
			result.put("availedVehicleDays" , availedVehicleDays);
			if(UtilValidate.isNotEmpty(payrollAttendance)){
				if(UtilValidate.isNotEmpty(payrollAttendance.get("lossOfPayDays"))){
					result.put("lossOfPayDays", (payrollAttendance.getBigDecimal("lossOfPayDays")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfAttendedDays"))){
					result.put("noOfAttendedDays", (payrollAttendance.getBigDecimal("noOfAttendedDays")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfCalenderDays"))){
					result.put("noOfCalenderDays", (payrollAttendance.getBigDecimal("noOfCalenderDays")).doubleValue());
					result.put("noOfPayableDays", (payrollAttendance.getBigDecimal("noOfCalenderDays")).doubleValue());
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
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfPayableDays"))){
					result.put("noOfPayableDays", (payrollAttendance.getBigDecimal("noOfPayableDays")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfArrearDays"))){
					result.put("noOfArrearDays", (payrollAttendance.getBigDecimal("noOfArrearDays")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("lateMin"))){
					result.put("lateMin", (payrollAttendance.getBigDecimal("lateMin")).doubleValue());
				}
				if(UtilValidate.isNotEmpty(payrollAttendance.get("extraMin"))){
					result.put("extraMin", (payrollAttendance.getBigDecimal("extraMin")).doubleValue());
				}
			}
    		
	    }catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
			} 
            result.put("shiftDetailMap" ,shiftDetailMap);
            result.put("availedCanteenDetailMap" , availedCanteenDetailMap);
            //result.put("disAvailedVehicleDays" , disAvailedVehicleDays);
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
	           Debug.logInfo("gross itemsList size====="+itemsList.size(),module);
		       for(int i=0;i<itemsList.size();i++){
		        	Map itemEntry =  (Map)itemsList.get(i);
		        	BigDecimal tempAmount = BigDecimal.ZERO;
		        	if((itemEntry.get("amount")) instanceof BigDecimal){
		        		tempAmount = (BigDecimal)itemEntry.get("amount");
		        	}else{
		        		tempAmount = new BigDecimal((Double)itemEntry.get("amount"));
		        	}
		        	amount = amount.add(tempAmount);
		        	
		        }
	            
	           } catch (Exception e) {
	                Debug.logError(e, "Error getting rules from the database while calculating price", module);
	                return ServiceUtil.returnError(e.toString());
	            }
	        
	        result.put("amount",amount);
	        Debug.logInfo("result gross salary====="+result,module);
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
	        //Debug.log("in calculateShiftBasePayHeadAmount======");
	        List priceInfos =FastList.newInstance();
	        try{
	        	Map employeePayrollAttedance = getEmployeePayrollAttedance(dctx,context);
	        	StringBuilder priceInfoDescription = new StringBuilder();
	        	Map shiftDetailMap = (Map)employeePayrollAttedance.get("shiftDetailMap");
	        	Map availedCanteenDetailMap = (Map)employeePayrollAttedance.get("availedCanteenDetailMap");
	        	int availedVehicleDays = ((Integer)employeePayrollAttedance.get("availedVehicleDays")).intValue();
	        	//int disAvailedVehicleDays = ((Integer)employeePayrollAttedance.get("disAvailedVehicleDays")).intValue();
	        	
	        	priceInfoDescription.append("\n \n[ Attendance Details ::"+employeePayrollAttedance);
				priceInfoDescription.append("  ]\n \n ");
	        	if(payHeadTypeId.equals("PAYROL_BEN_SHIFT") && UtilValidate.isNotEmpty(shiftDetailMap)){
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
		        	Map empPositionDetails = getEmployeePayrollCondParms(dctx, UtilMisc.toMap("employeeId",employeeId,"timePeriodStart",timePeriodStart,"timePeriodEnd" ,timePeriodEnd ,"userLogin",userLogin));
		        	if(ServiceUtil.isError(empPositionDetails)){
		            	Debug.logError(ServiceUtil.getErrorMessage(empPositionDetails), module);
		                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(empPositionDetails));
			         }
		        	String vehicleType = (String)empPositionDetails.get("vehicleType");
		        	if(UtilValidate.isNotEmpty(vehicleType)){
		        		Map condParms = FastMap.newInstance();
						condParms.put("employeeId", employeeId);
						condParms.put("otherCond", vehicleType);
						Map priceResultRuleCtx = FastMap.newInstance();
	                    priceResultRuleCtx.putAll(context);
	                    priceResultRuleCtx.put("condParms", condParms);
	                    Map<String, Object> calcResults = calculatePayHeadAmount(dctx,priceResultRuleCtx);
						amount = amount.add((BigDecimal)calcResults.get("amount"));
						priceInfos.add(calcResults.get("priceInfos"));
		        	}
					
	        	}
	        	if(payHeadTypeId.equals("PAYROL_DD_SAL_CANT")&& UtilValidate.isNotEmpty(availedCanteenDetailMap)){
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
	        	priceInfos.add(priceInfoDescription);
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
	    public static Map<String, Object> calculateAttendanceBonus(DispatchContext dctx, Map<String, ? extends Object> context) {

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
	        //Debug.log("in calculateShiftBasePayHeadAmount======");
	        List priceInfos =FastList.newInstance();
	        try{
	        	Map input = FastMap.newInstance();
	        	input.put("timePeriodId", timePeriodId);
	        	input.put("timePeriodStart", timePeriodStart);
	        	input.put("timePeriodEnd", timePeriodEnd);
	        	Timestamp attdTimePeriodStart = timePeriodStart;
	        	Timestamp attdTimePeriodEnd = timePeriodEnd;
	        	
	        	Map resultMap = getPayrollAttedancePeriod(dctx,input);
	  	    	if(ServiceUtil.isError(resultMap)){
	 	 	    	Debug.logError("Error in service findLastClosed Attedance Date ", module);    			
	 	 		    return ServiceUtil.returnError("Error in service findLast Closed Attedance Date");
	 	 	    }
	  	    	//lastCloseAttedancePeriod = ((GenericValue)result.get("lastClosedTimePeriod"))
	  	    	if(UtilValidate.isNotEmpty(resultMap.get("lastCloseAttedancePeriod"))){
	  	    		GenericValue lastCloseAttedancePeriod = (GenericValue)resultMap.get("lastCloseAttedancePeriod");
	  	    		attdTimePeriodStart = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(lastCloseAttedancePeriod.getDate("fromDate")));
	  	    		if(UtilDateTime.getIntervalInDays(attdTimePeriodStart, timePeriodStart) > 32){
	  	    			Debug.logError("invalid  Attedance Period : "+lastCloseAttedancePeriod.getString("customTimePeriodId") +",startDate :"+ lastCloseAttedancePeriod.getDate("fromDate")+",endDate:"+lastCloseAttedancePeriod.getDate("thruDate"), module);    			
		 	 		    return ServiceUtil.returnError("invalid  Attedance Period : "+lastCloseAttedancePeriod.getString("customTimePeriodId") +",startDate :"+ lastCloseAttedancePeriod.getDate("fromDate")+",endDate:"+lastCloseAttedancePeriod.getDate("thruDate"));
	  	    		}
	  	    		attdTimePeriodEnd = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(lastCloseAttedancePeriod.getDate("thruDate")));
	  	    	}
	  	    	input.clear();
	    		input.put("userLogin", userLogin);
	    		input.put("partyId", employeeId);
	    		input.put("timePeriodStart", attdTimePeriodStart);
	    		input.put("timePeriodEnd", attdTimePeriodEnd);
	    		resultMap = EmplLeaveService.fetchLeaveDaysForPeriod(dctx, input);
	    		if(ServiceUtil.isError(resultMap)){
	    			Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultMap), 
	            			null, null, null);
	    		}
	    		Map leaveDetailmap = (Map)resultMap.get("leaveDetailmap");
	    		StringBuilder priceInfoDescription = new StringBuilder();
				priceInfoDescription.append(" \n ");
	        	priceInfoDescription.append("\n \n[ Leave Details ::"+leaveDetailmap);
				priceInfoDescription.append("  ]\n \n ");
	        	Iterator tempIter = leaveDetailmap.entrySet().iterator();
	        	Boolean bonusFlag = Boolean.TRUE;
				
	        	while (tempIter.hasNext()) {
						Map.Entry tempEntry = (Entry) tempIter.next();
						BigDecimal value = (BigDecimal)tempEntry.getValue();
						String leaveTypeId = (String)tempEntry.getKey();
						List excludeLeaveTypes = UtilMisc.toList("CH","CHGH","CHSS","RL");
						if((!(excludeLeaveTypes.contains(leaveTypeId))) && value.compareTo(BigDecimal.ZERO) !=0){
							bonusFlag = Boolean.FALSE;
						}
						
					}
	        	 Map employeePayrollAttedance = getEmployeePayrollAttedance(dctx,context);
	        	 if(UtilValidate.isNotEmpty(employeePayrollAttedance) && ((Double)employeePayrollAttedance.get("lateMin")) !=0 ){
	        		 bonusFlag = Boolean.FALSE;
	        	 }
				if(bonusFlag){
					Map condParms = FastMap.newInstance();
					condParms.put("employeeId", employeeId);
					condParms.put("otherCond", "NONE");
					Map priceResultRuleCtx = FastMap.newInstance();
                    priceResultRuleCtx.putAll(context);
                    priceResultRuleCtx.put("condParms", condParms);
                    Map<String, Object> calcResults = calculatePayHeadAmount(dctx,priceResultRuleCtx);
					amount = (BigDecimal)calcResults.get("amount");
					priceInfos.add(calcResults.get("priceInfos"));
				}
	        	priceInfos.add(priceInfoDescription);
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
	   public static Map<String, Object> calculateLICPayHeadAmount(DispatchContext dctx, Map<String, ? extends Object> context) {

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
	        List priceInfos =FastList.newInstance();
	        try{
	        	StringBuilder priceInfoDescription = new StringBuilder();
	        	
				priceInfoDescription.append(" \n ");
	        	List condList = FastList.newInstance();
	        	condList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, employeeId));
	        	condList.add(EntityCondition.makeCondition("deductionTypeId",EntityOperator.EQUALS, payHeadTypeId));
	        	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
	        	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
			        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
	        	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
	        	
	        	List<GenericValue> partyInsuranceList = delegator.findList("PartyInsuranceAndType", cond, null, null, null, false);
				for(GenericValue partyInsurance : partyInsuranceList){
					if(UtilValidate.isNotEmpty(partyInsurance.getBigDecimal("premiumAmount"))){
						amount = amount.add(partyInsurance.getBigDecimal("premiumAmount"));
					}
					
				}
				priceInfoDescription.append("found "+ partyInsuranceList.size()+" active LIC's");
	        	
	        	priceInfos.add(priceInfoDescription);
	            } catch (Exception e) {
	                Debug.logError(e, "Error getting rules from the database while calculating price", module);
	                return ServiceUtil.returnError(e.toString());
	            }
	           
	          result.put("amount", amount);
	          result.put("priceInfos", priceInfos);
	        return result;
	    }
	   
	   public static Map<String, Object> calculateLoanPayHeadAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
     
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
	        String periodBillingId = (String)context.get("periodBillingId");
	        BigDecimal amount = BigDecimal.ZERO;
	        List priceInfos =FastList.newInstance();
	        try{
	        	StringBuilder priceInfoDescription = new StringBuilder();
	        	
				priceInfoDescription.append(" \n ");
	        	List condList = FastList.newInstance();
	        	condList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, employeeId));
	        	condList.add(EntityCondition.makeCondition("payHeadTypeId",EntityOperator.EQUALS, payHeadTypeId));
	        	condList.add(EntityCondition.makeCondition("disbDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodStart));
	        	condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.EQUALS ,"LOAN_DISBURSED"));
	        	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("setlDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
			        		EntityCondition.makeCondition("setlDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodEnd)));
	        	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
	        	
	        	List<GenericValue> loanList = delegator.findList("LoanAndType", cond, null, null, null, false);
	        	GenericValue loan = EntityUtil.getFirst(loanList);
				if(UtilValidate.isNotEmpty(loan)){
					DynamicViewEntity dynamicView = new DynamicViewEntity();
					dynamicView.addMemberEntity("LR", "LoanRecovery");
                    dynamicView.addAliasAll("LR", null, null);
                    dynamicView.addMemberEntity("CT", "CustomTimePeriod");
                    dynamicView.addAliasAll("CT", null, null);
                    dynamicView.addAlias("CT", "customTimePeriodId");
                    dynamicView.addAlias("CT", "fromDate");
                    dynamicView.addAlias("CT", "thruDate");
                    dynamicView.addViewLink("LR", "CT", Boolean.FALSE, ModelKeyMap.makeKeyMapList("customTimePeriodId"));
                    String isExternal =  loan.getString("isExternal");
                    GenericValue newEntityLoanRecovery = delegator.makeValue("LoanRecovery");
                    newEntityLoanRecovery.set("loanId", loan.getString("loanId"));
                    newEntityLoanRecovery.set("customTimePeriodId", timePeriodId);
                    EntityFindOptions findOpts = new EntityFindOptions();
                    findOpts.setFetchSize(1);
                    findOpts.setMaxRows(1);
                    EntityListIterator pli = delegator.findListIteratorByCondition(dynamicView, EntityCondition.makeCondition("loanId",EntityOperator.EQUALS, loan.getString("loanId")), null, null, UtilMisc.toList("-thruDate"), findOpts);
                    List<GenericValue> loanRecoveryList = pli.getCompleteList();
					GenericValue loanRecovery = EntityUtil.getFirst(loanRecoveryList);
					if(UtilValidate.isNotEmpty(isExternal) && isExternal.equalsIgnoreCase("Y")){
						newEntityLoanRecovery.set("principalInstNum", new Long(1));
						amount = loan.getBigDecimal("principalAmount");
						newEntityLoanRecovery.set("principalAmount", amount);
					}else{
						if(UtilValidate.isEmpty(loanRecovery)){
							newEntityLoanRecovery.set("principalInstNum", new Long(1));
							amount = loan.getBigDecimal("principalAmount").divide(new BigDecimal(loan.getLong("numPrincipalInst")), 0 ,BigDecimal.ROUND_UP);
							newEntityLoanRecovery.set("principalAmount", amount);
						}else{
							if(UtilValidate.isNotEmpty(loanRecovery.getLong("principalInstNum")) && (loanRecovery.getLong("principalInstNum")).compareTo(loan.getLong("numPrincipalInst"))<0){
								amount = loan.getBigDecimal("principalAmount").divide(new BigDecimal(loan.getLong("numPrincipalInst")), 0,BigDecimal.ROUND_UP);
								newEntityLoanRecovery.set("principalInstNum", new Long(loanRecovery.getLong("principalInstNum").intValue()+1));
								newEntityLoanRecovery.set("principalAmount", amount);
							}else{
								
								if((UtilValidate.isEmpty(loanRecovery.getLong("interestInstNum")) && (loan.getLong("numInterestInst")).intValue() !=0 ) || (UtilValidate.isNotEmpty(loanRecovery.getLong("interestInstNum")) && UtilValidate.isNotEmpty(loan.getBigDecimal("interestAmount")) && ((loan.getLong("numInterestInst")).intValue() !=0 ) &&  (loanRecovery.getLong("interestInstNum")).compareTo(loan.getLong("numInterestInst"))<0)){
									amount = loan.getBigDecimal("interestAmount").divide(new BigDecimal(loan.getLong("numInterestInst")), 0,BigDecimal.ROUND_UP);
									newEntityLoanRecovery.set("interestInstNum",new Long(1));
									if(UtilValidate.isNotEmpty(loanRecovery.getLong("interestInstNum"))){
										newEntityLoanRecovery.set("interestInstNum", new Long(loanRecovery.getLong("interestInstNum").intValue()+1));
									}
									newEntityLoanRecovery.set("interestAmount", amount);
								}
							}
						}
					}
					
					pli.close();
					if(UtilValidate.isNotEmpty(periodBillingId)){
						delegator.setNextSubSeqId(newEntityLoanRecovery,"sequenceNum", 5, 1);
						delegator.createOrStore(newEntityLoanRecovery);
					}
					
				}
				priceInfoDescription.append("found "+ loanList.size()+" active loans");
	        	
	        	priceInfos.add(priceInfoDescription);
	            } catch (Exception e) {
	                Debug.logError(e, "Error getting rules fr" +
	                		"om the database while calculating price , employeeId:"+employeeId, module);
	                return ServiceUtil.returnError(e.toString());
	            }
	          result.put("amount", amount);
	          result.put("priceInfos", priceInfos);
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
	 
	 public static Map<String, Object> getPartyIdFromEmployment(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String partyIdTo = (String)context.get("partyIdTo");
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	String customTimePeriodId = (String)context.get("customTimePeriodId");
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String partyIdFrom = null;
	    	Timestamp fromDateStart  = null;
	    	try {
		        GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
	        	if (UtilValidate.isNotEmpty(customTimePeriod)) {
	        		Timestamp fromDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	        		Timestamp thruDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	        		fromDateStart = UtilDateTime.getDayStart(fromDateTime);
	        	}
	        }catch (GenericEntityException e) {
	        	Debug.logError(e, module);
	        	return ServiceUtil.returnError(e.getMessage());
			}
	    	try {
	    		List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyIdTo));
				conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND); 	
				List<GenericValue> employmentList = delegator.findList("Employment", condition, null, null, null, false);
				if(UtilValidate.isNotEmpty(employmentList)){
					List activeEmploymentList = EntityUtil.filterByDate(employmentList, fromDateStart);
					if(UtilValidate.isNotEmpty(activeEmploymentList)){
						GenericValue activeEmployment = EntityUtil.getFirst(activeEmploymentList);
						partyIdFrom = activeEmployment.getString("partyIdFrom");
					}
				}
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        result.put("partyIdFrom", partyIdFrom);
	        return result;
	    }
/*	 public static Map<String, Object> createOrUpdatePartyBenefitOrDeduction(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String partyId = (String) context.get("partyId");
	        String customTimePeriodId = (String)context.get("customTimePeriodId");
	        String payHeadTypeId = (String)context.get("payHeadTypeId");
	        BigDecimal amount = (BigDecimal)context.get("amount");
	        Locale locale = (Locale) context.get("locale");
	        Map result = ServiceUtil.returnSuccess();
	        Timestamp fromDateStart  = null;
	        Timestamp thruDateEnd  = null;
	        
			String benefitTypeId = null;
			String deductionTypeId = null;
			
			String partyIdFrom = null;
			
			Map employmentDetails = getPartyIdFromEmployment(dctx,UtilMisc.toMap("userLogin",userLogin,"customTimePeriodId",customTimePeriodId,"partyIdTo",partyId));
			if(UtilValidate.isNotEmpty(employmentDetails)){
				partyIdFrom =(String)employmentDetails.get("partyIdFrom");
			}
	        try {
		        GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
	        	if (UtilValidate.isNotEmpty(customTimePeriod)) {
	        		Timestamp fromDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	        		Timestamp thruDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	        		fromDateStart = UtilDateTime.getDayStart(fromDateTime);
	        		thruDateEnd = UtilDateTime.getDayEnd(thruDateTime);
	        		//previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDateTime, -1));
	        	}
	        }catch (GenericEntityException e) {
	        	Debug.logError(e, module);
	        	return ServiceUtil.returnError(e.getMessage());
			}
			try {
				if(UtilValidate.isNotEmpty(payHeadTypeId)){
					try {
						GenericValue benefitType = delegator.findOne("BenefitType", UtilMisc.toMap("benefitTypeId", payHeadTypeId), false);
						if(UtilValidate.isNotEmpty(benefitType)){
							benefitTypeId = benefitType.getString("benefitTypeId");
						}
						GenericValue deductionType = delegator.findOne("DeductionType", UtilMisc.toMap("deductionTypeId", payHeadTypeId), false);
						if(UtilValidate.isNotEmpty(deductionType)){
							deductionTypeId = deductionType.getString("deductionTypeId");
						}
					}catch (GenericEntityException e) {
			        	Debug.logError(e, module);
			        	return ServiceUtil.returnError(e.getMessage());
					}
				}
				if(UtilValidate.isNotEmpty(benefitTypeId)){
					GenericValue partyBenefit = delegator.makeValue("PartyBenefit");
					partyBenefit.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
					partyBenefit.set("roleTypeIdTo", "EMPLOYEE");
					partyBenefit.set("partyIdFrom", partyIdFrom);
					partyBenefit.set("partyIdTo", partyId);
					partyBenefit.set("benefitTypeId", benefitTypeId);
					partyBenefit.set("periodTypeId", "RATE_MONTH");
					partyBenefit.set("fromDate", fromDateStart);
					partyBenefit.set("thruDate", thruDateEnd);
					partyBenefit.set("cost", amount);
					delegator.createOrStore(partyBenefit);
				}else{
					GenericValue partyDeduction = delegator.makeValue("PartyDeduction");
					partyDeduction.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
					partyDeduction.set("roleTypeIdTo", "EMPLOYEE");
					partyDeduction.set("partyIdFrom", partyIdFrom);
					partyDeduction.set("partyIdTo", partyId);
					partyDeduction.set("deductionTypeId", deductionTypeId);
					partyDeduction.set("periodTypeId", "RATE_MONTH");
					partyDeduction.set("fromDate", fromDateStart);
					partyDeduction.set("thruDate", thruDateEnd);
					partyDeduction.set("cost", amount);
					delegator.createOrStore(partyDeduction);
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
	        result = ServiceUtil.returnSuccess("Successfully Created!!");
	        return result;
	 }//end of service
*/	 
	 public static String editPayrollAttendance(HttpServletRequest request, HttpServletResponse response) {
	    	Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        Locale locale = UtilHttp.getLocale(request);
	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        HttpSession session = request.getSession();
	        Map paramMap = UtilHttp.getParameterMap(request);
	        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");		
	        String partyId = (String) request.getParameter("partyId");	
	        String customTimePeriodId = (String) request.getParameter("customTimePeriodId");
	        String timePeriodId = (String) request.getParameter("timePeriodId");
	        
	        String noOfPayableDaysStr=(String)request.getParameter("noOfPayableDays");
	        BigDecimal noOfPayableDays=new BigDecimal(noOfPayableDaysStr);
	        
	        String noOfAttendedDaysStr=(String)request.getParameter("noOfAttendedDays");
	        BigDecimal noOfAttendedDays=new BigDecimal(noOfAttendedDaysStr);
	        
	        String noOfCalenderDaysStr=(String)request.getParameter("noOfCalenderDays");
	        BigDecimal noOfCalenderDays=new BigDecimal(noOfCalenderDaysStr);
	        
	        String casualLeaveDaysStr=(String)request.getParameter("casualLeaveDays");
	        BigDecimal casualLeaveDays=new BigDecimal(casualLeaveDaysStr);
	        
	        String earnedLeaveDaysStr=(String)request.getParameter("earnedLeaveDays");
	        BigDecimal earnedLeaveDays=new BigDecimal(earnedLeaveDaysStr);
	        
	        String noOfAttendedHoliDaysStr=(String)request.getParameter("noOfAttendedHoliDays");
	        BigDecimal noOfAttendedHoliDays=new BigDecimal(noOfAttendedHoliDaysStr);
	        
	        String noOfAttendedSsDaysStr=(String)request.getParameter("noOfAttendedSsDays");
	        BigDecimal noOfAttendedSsDays=new BigDecimal(noOfAttendedSsDaysStr);
	        
	        String noOfAttendedWeeklyOffDaysStr=(String)request.getParameter("noOfAttendedWeeklyOffDays");
	        BigDecimal noOfAttendedWeeklyOffDays=new BigDecimal(noOfAttendedWeeklyOffDaysStr);
	        
	        String noOfCompoffAvailedStr=(String)request.getParameter("noOfCompoffAvailed");
	        BigDecimal noOfCompoffAvailed=new BigDecimal(noOfCompoffAvailedStr);
	        
	        String noOfArrearDaysStr=(String)request.getParameter("noOfArrearDays");
	        BigDecimal noOfArrearDays=new BigDecimal(noOfArrearDaysStr);
	        
	        String noOfNightAllowanceDaysStr=(String)request.getParameter("noOfNightAllowanceDays");
	        BigDecimal noOfNightAllowanceDays=new BigDecimal(noOfNightAllowanceDaysStr);
	        
	        String coldOrBoiledAllowanceDaysStr=(String)request.getParameter("coldOrBoiledAllowanceDays");
	        BigDecimal coldOrBoiledAllowanceDays=new BigDecimal(coldOrBoiledAllowanceDaysStr);
	        
	        String noOfRiskAllowanceDaysStr=(String)request.getParameter("noOfRiskAllowanceDays");
	        BigDecimal noOfRiskAllowanceDays=new BigDecimal(noOfRiskAllowanceDaysStr);
	        
	        String heavyTankerAllowanceDaysStr=(String)request.getParameter("heavyTankerAllowanceDays");
	        BigDecimal heavyTankerAllowanceDays=new BigDecimal(heavyTankerAllowanceDaysStr);
	        
	        String trTankerAllowanceDaysStr=(String)request.getParameter("trTankerAllowanceDays");
	        BigDecimal trTankerAllowanceDays=new BigDecimal(trTankerAllowanceDaysStr);
	        
	        String operatingAllowanceDaysStr=(String)request.getParameter("operatingAllowanceDays");
	        BigDecimal operatingAllowanceDays=new BigDecimal(operatingAllowanceDaysStr);
	        
	        String inChargeAllowanceDaysStr=(String)request.getParameter("inChargeAllowanceDays");
	        BigDecimal inChargeAllowanceDays=new BigDecimal(inChargeAllowanceDaysStr);
	        
	        String lossOfPayDaysStr=(String)request.getParameter("lossOfPayDays");
	        BigDecimal lossOfPayDays=new BigDecimal(lossOfPayDaysStr);
	        
	        String noOfHalfPayDaysStr=(String)request.getParameter("noOfHalfPayDays");
	        BigDecimal noOfHalfPayDays=new BigDecimal(noOfHalfPayDaysStr);
	        
	        String billingTypeId = "PAYROLL_BILL";	
	        // Returning error if payroll already generated
	        List conditionList = FastList.newInstance();
	        List periodBillingList = FastList.newInstance();
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS","APPROVED")));
	        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,timePeriodId));
	    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , billingTypeId));
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	try {
	    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
	    			    		
	    	}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
			} 
	        if(UtilValidate.isNotEmpty(periodBillingList)){	    			
 			request.setAttribute("_ERROR_MESSAGE_", "Payroll Already generated for this period, you can not edit values");
				return "error";
	        }
	        try {
      			List conList = FastList.newInstance();
      			conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
      			conList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS , customTimePeriodId));
      			EntityCondition con=EntityCondition.makeCondition(conList,EntityOperator.AND); 
      			List<GenericValue> emplPayrollAttendanceDetailList = delegator.findList("PayrollAttendance", con, null, null, null, false);
      			GenericValue employPayrollDetails = null;	  
      			if(UtilValidate.isNotEmpty(emplPayrollAttendanceDetailList)){
      					employPayrollDetails = emplPayrollAttendanceDetailList.get(0);
      			 }else{
      				employPayrollDetails = delegator.makeValue("PayrollAttendance");
      			 }
      			    employPayrollDetails.set("partyId",partyId);
					employPayrollDetails.set("customTimePeriodId",customTimePeriodId);
						
      			
      					if(UtilValidate.isNotEmpty(employPayrollDetails)){
      						employPayrollDetails.set("noOfPayableDays",noOfPayableDays);
      						employPayrollDetails.set("noOfAttendedDays",noOfAttendedDays);
      						employPayrollDetails.set("noOfCalenderDays",noOfCalenderDays);
      						employPayrollDetails.set("casualLeaveDays",casualLeaveDays);
      						employPayrollDetails.set("earnedLeaveDays",earnedLeaveDays);
      						employPayrollDetails.set("noOfAttendedHoliDays",noOfAttendedHoliDays);
      						employPayrollDetails.set("noOfAttendedSsDays",noOfAttendedSsDays);
      						employPayrollDetails.set("noOfAttendedWeeklyOffDays",noOfAttendedWeeklyOffDays);
      						employPayrollDetails.set("noOfCompoffAvailed",noOfCompoffAvailed);
      						employPayrollDetails.set("lossOfPayDays",lossOfPayDays);
      						employPayrollDetails.set("noOfArrearDays",noOfArrearDays);
      						employPayrollDetails.set("noOfNightAllowanceDays",noOfNightAllowanceDays);
      						employPayrollDetails.set("coldOrBoiledAllowanceDays",coldOrBoiledAllowanceDays);
      						employPayrollDetails.set("noOfRiskAllowanceDays",noOfRiskAllowanceDays);
      						employPayrollDetails.set("heavyTankerAllowanceDays",heavyTankerAllowanceDays);
      						employPayrollDetails.set("trTankerAllowanceDays",trTankerAllowanceDays);
      						employPayrollDetails.set("operatingAllowanceDays",operatingAllowanceDays);
      						employPayrollDetails.set("inChargeAllowanceDays",inChargeAllowanceDays);
      						employPayrollDetails.set("noOfHalfPayDays",noOfHalfPayDays);
      				        delegator.createOrStore(employPayrollDetails);
      					}
    	      	
      		} catch (GenericEntityException e) {
      				Debug.logError(e, module);
      			}
	      	 return "success";
	    }
	 
	 
	 
	 public static String updateBenefitsOrDeductions(HttpServletRequest request, HttpServletResponse response) {
	    	Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        Locale locale = UtilHttp.getLocale(request);
	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        HttpSession session = request.getSession();
	        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");		
	        String partyId = (String) request.getParameter("partyId");	
	        String periodId = (String) request.getParameter("periodId");
	        String billingTypeId = "PAYROLL_BILL";	
	        
	        Map paramMap = UtilHttp.getParameterMap(request);
	        FastList payheadTypeIdsList = FastList.newInstance();
	        // Returning error if payroll already generated
	        List conditionList = FastList.newInstance();
	        List periodBillingList = FastList.newInstance();
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS","APPROVED")));
	        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,periodId));
	    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , billingTypeId));
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	try {
	    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
	    			    		
	    	}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
			} 
	        if(UtilValidate.isNotEmpty(periodBillingList)){	    			
    			request.setAttribute("_ERROR_MESSAGE_", "Payroll Already generated for this period, you can not edit values");
				return "error";
    		}
	        try{
	        	List<GenericValue> benefitTypes = delegator.findList("BenefitType",null, null,null, null, true);
	        	List<GenericValue> deductionTypes = delegator.findList("DeductionType",null, null,null, null, true);
	        	payheadTypeIdsList.addAll(EntityUtil.getFieldListFromEntityList(benefitTypes, "benefitTypeId", true));
	        	payheadTypeIdsList.addAll(EntityUtil.getFieldListFromEntityList(deductionTypes, "deductionTypeId", true));
         } catch (GenericEntityException e) {
             Debug.logError(e, "Error getting payhead types", module);
         }
	        if(UtilValidate.isNotEmpty(payheadTypeIdsList)){
	        	for(int i=0;i<payheadTypeIdsList.size();i++){
	        		String payheadTypeId= (String)payheadTypeIdsList.get(i);
	        		
	        		if(UtilValidate.isNotEmpty(paramMap.get(payheadTypeId))){
	        			Map<String, Object> payItemMap=FastMap.newInstance();
	        			String amountStr=(String)paramMap.get(payheadTypeId);
	        			if((!" ".equals(amountStr))){
		        			BigDecimal amount= BigDecimal.ZERO;
		    				if (UtilValidate.isNotEmpty(amountStr)) {	
		    					amount = new BigDecimal(amountStr);
		    				}
		    				payItemMap.put("userLogin",userLogin);
		    				payItemMap.put("customTimePeriodId",periodId);
		    				payItemMap.put("amount",amount);
		    				payItemMap.put("partyId",partyId);
		    				payItemMap.put("payHeadTypeId",payheadTypeId);	    				
		    				try {
		    					if(amount.compareTo(BigDecimal.ZERO) >=0){
		    						Map resultValue = dispatcher.runSync("createOrUpdatePartyBenefitOrDeduction", payItemMap);
		    						if( ServiceUtil.isError(resultValue)) {
		    							String errMsg =  ServiceUtil.getErrorMessage(resultValue);
		    							Debug.logWarning(errMsg , module);
		    							request.setAttribute("_ERROR_MESSAGE_",errMsg);
		    							
		    							return "error";
		    						}
		    					}
		    					
		    				} catch (GenericServiceException s) {
		    					s.printStackTrace();
		    				} 
	        			}
	        		}
	        	}
	        }
	      	 return "success";
	    }
	 public static Map<String, Object> createOrUpdatePartyBenefitOrDeduction(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String partyId = (String) context.get("partyId");
	        String customTimePeriodId = (String)context.get("customTimePeriodId");
	        String payHeadTypeId = (String)context.get("payHeadTypeId");
	        BigDecimal amount = (BigDecimal)context.get("amount");
	        String amountNullFlag = (String)context.get("amountNullFlag");
	        Locale locale = (Locale) context.get("locale");
	        Map result = ServiceUtil.returnSuccess();
	        Timestamp fromDateTime  = null;
	        Timestamp thruDateTime  = null;
	        Timestamp previousDayEnd = null;
	        Timestamp fromDateStart  = null;
	        Timestamp thruDateEnd  = null;
	        String partyIdFrom = null;
	        List benefitTypeIds = FastList.newInstance();
	        List deductionTypeIds = FastList.newInstance();
	        if(UtilValidate.isEmpty(amountNullFlag) && UtilValidate.isEmpty(amount)){
	        	return ServiceUtil.returnError("Amount cannot be Empty");
	        }
	        try {
		        GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
	        	if (UtilValidate.isNotEmpty(customTimePeriod)) {
	        		fromDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	        		thruDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	        		fromDateStart = UtilDateTime.getDayStart(fromDateTime);
	        		thruDateEnd = UtilDateTime.getDayEnd(thruDateTime);
				    previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDateTime, -1));
	        	}
	        }catch (GenericEntityException e) {
	        	Debug.logError(e, module);
	        	return ServiceUtil.returnError(e.getMessage());
			}
	        Map employmentDetails = getPartyIdFromEmployment(dctx,UtilMisc.toMap("userLogin",userLogin,"customTimePeriodId",customTimePeriodId,"partyIdTo",partyId));
	        if(UtilValidate.isNotEmpty(employmentDetails.get("partyIdFrom"))){
				partyIdFrom =(String)employmentDetails.get("partyIdFrom");
			}
			try {
				if(UtilValidate.isEmpty(partyIdFrom)){
					return ServiceUtil.returnError("Employee is Inactive or doesn't have Employment");
				}
				List<GenericValue> benefitTypes = delegator.findList("BenefitType",null, null,null, null, true);
				benefitTypeIds = EntityUtil.getFieldListFromEntityList(benefitTypes, "benefitTypeId", true);
	        	List<GenericValue> deductionTypes = delegator.findList("DeductionType",null, null,null, null, true);
	        	deductionTypeIds = EntityUtil.getFieldListFromEntityList(deductionTypes, "deductionTypeId", true);
	        	if(benefitTypeIds.contains(payHeadTypeId)){
	        		GenericValue benefitTypeValue = delegator.findOne("BenefitType",UtilMisc.toMap("benefitTypeId", payHeadTypeId), false);
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyId));
					conditionList.add(EntityCondition.makeCondition("benefitTypeId", EntityOperator.EQUALS ,payHeadTypeId));
					conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDateStart));
					conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
						    EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDateEnd)));
			    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
					List<GenericValue> partyBenefitList = delegator.findList("PartyBenefit", condition, null, null, null, false);
					if(UtilValidate.isEmpty(partyBenefitList)){
						List condList = FastList.newInstance();
						condList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"INTERNAL_ORGANIZATIO"));
						condList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
						condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,partyIdFrom));
						condList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyId));
						condList.add(EntityCondition.makeCondition("benefitTypeId", EntityOperator.EQUALS ,payHeadTypeId));
				    	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 
						List<GenericValue> activePartyBenefitList = delegator.findList("PartyBenefit", cond, null, UtilMisc.toList("-fromDate"), null, false);
						if(UtilValidate.isNotEmpty(activePartyBenefitList)){
							GenericValue activePartyBenefit = EntityUtil.getFirst(activePartyBenefitList);
							activePartyBenefit.set("thruDate", previousDayEnd);
							activePartyBenefit.store();
						}
						GenericValue newEntity = delegator.makeValue("PartyBenefit");
						newEntity.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
						newEntity.set("roleTypeIdTo", "EMPLOYEE");
						newEntity.set("partyIdFrom", partyIdFrom);
						newEntity.set("partyIdTo", partyId);
						newEntity.set("benefitTypeId", payHeadTypeId);
						newEntity.set("periodTypeId", "RATE_MONTH");
						newEntity.set("fromDate", fromDateStart);
						if(!(UtilValidate.isNotEmpty(benefitTypeValue.get("isContinuous")) && ("Y".equals(benefitTypeValue.get("isContinuous"))))){
							newEntity.set("thruDate", thruDateEnd);
						}
						newEntity.set("cost", amount);
						newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
				        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						newEntity.create();
					}else{	
						GenericValue partyBenefit = partyBenefitList.get(0);
						BigDecimal prevAmount = partyBenefit.getBigDecimal("cost");
						Timestamp prevFromDate = partyBenefit.getTimestamp("fromDate");
						Timestamp prevThruDate = partyBenefit.getTimestamp("thruDate");
						if(UtilValidate.isEmpty(prevAmount)){
							prevAmount = BigDecimal.ZERO;
						}
						if(prevFromDate.compareTo(fromDateStart)== 0){
							if((prevAmount.compareTo(amount)) != 0 || (prevAmount.compareTo(BigDecimal.ZERO) ==0) ){
								// Update existing one
								partyBenefit.set("partyIdTo",partyBenefit.getString("partyIdTo"));
								partyBenefit.set("benefitTypeId",partyBenefit.getString("benefitTypeId"));
								partyBenefit.set("cost", amount);
								if(!(UtilValidate.isNotEmpty(benefitTypeValue.get("isContinuous")) && ("Y".equals(benefitTypeValue.get("isContinuous"))))){
									partyBenefit.set("thruDate", thruDateEnd);
								}
								partyBenefit.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
								partyBenefit.store();
							}
						}else{	
							// Create New One
							if(UtilValidate.isEmpty(prevAmount) || prevAmount.compareTo(amount)!= 0){
								GenericValue newEntity = delegator.makeValue("PartyBenefit");
								newEntity.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
								newEntity.set("roleTypeIdTo", "EMPLOYEE");
								newEntity.set("partyIdFrom", partyIdFrom);
								newEntity.set("partyIdTo", partyId);
								newEntity.set("benefitTypeId", payHeadTypeId);
								newEntity.set("periodTypeId", "RATE_MONTH");
								newEntity.set("fromDate", fromDateStart);
								newEntity.set("cost", amount);
								newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
						        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
								newEntity.create();
								
								partyBenefit.set("thruDate", previousDayEnd);
								partyBenefit.store();
							}
						}
					}
				}
	        	BigDecimal amountToCompare = BigDecimal.ZERO;
	        	if(UtilValidate.isNotEmpty(amountNullFlag)){
	        		amountToCompare = BigDecimal.ZERO;
	        	}else{
	        		amountToCompare = amount;
	        	}
				if(deductionTypeIds.contains(payHeadTypeId)){
					GenericValue deductionTypeValue = delegator.findOne("DeductionType",UtilMisc.toMap("deductionTypeId", payHeadTypeId), false);
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyId));
					conditionList.add(EntityCondition.makeCondition("deductionTypeId", EntityOperator.EQUALS ,payHeadTypeId));
					conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDateStart));
					conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
						    EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDateEnd)));
			    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
					List<GenericValue> partyDeductionList = delegator.findList("PartyDeduction", condition, null, null, null, false);
					if(UtilValidate.isNotEmpty(amountNullFlag)){
		        		thruDateEnd = null;
		        	}else{
		        		if(!(UtilValidate.isNotEmpty(deductionTypeValue.get("isContinuous")) && ("Y".equals(deductionTypeValue.get("isContinuous"))))){
		        			thruDateEnd = thruDateEnd;
						}
		        	}
					if(UtilValidate.isEmpty(partyDeductionList)){
						List condList = FastList.newInstance();
						condList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"INTERNAL_ORGANIZATIO"));
						condList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
						condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,partyIdFrom));
						condList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyId));
						condList.add(EntityCondition.makeCondition("deductionTypeId", EntityOperator.EQUALS ,payHeadTypeId));
				    	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 
						List<GenericValue> activePartyDeductionList = delegator.findList("PartyDeduction", cond, null, UtilMisc.toList("-fromDate"), null, false);
						if(UtilValidate.isNotEmpty(activePartyDeductionList)){
							GenericValue activePartyDeduction = EntityUtil.getFirst(activePartyDeductionList);
							activePartyDeduction.set("thruDate", previousDayEnd);
							activePartyDeduction.store();
						}
						GenericValue newEntity = delegator.makeValue("PartyDeduction");
						newEntity.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
						newEntity.set("roleTypeIdTo", "EMPLOYEE");
						newEntity.set("partyIdFrom", partyIdFrom);
						newEntity.set("partyIdTo", partyId);
						newEntity.set("deductionTypeId", payHeadTypeId);
						newEntity.set("periodTypeId", "RATE_MONTH");
						newEntity.set("fromDate", fromDateStart);	
						newEntity.set("thruDate", thruDateEnd);
						newEntity.set("cost", amount);
						newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
				        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						newEntity.create();
					}else{	
						GenericValue partyDeduction = partyDeductionList.get(0);
						BigDecimal prevAmount = partyDeduction.getBigDecimal("cost");
						Timestamp prevFromDate = partyDeduction.getTimestamp("fromDate");
						Timestamp prevThruDate = partyDeduction.getTimestamp("thruDate");
						if(UtilValidate.isEmpty(prevAmount)){
							prevAmount = BigDecimal.ZERO;
						}
						if(prevFromDate.compareTo(fromDateStart)== 0){
							if(prevAmount.compareTo(amountToCompare)!= 0 || (prevAmount.compareTo(BigDecimal.ZERO) ==0)){
								// Update existing one
								partyDeduction.set("partyIdTo",partyDeduction.getString("partyIdTo"));
								partyDeduction.set("deductionTypeId",partyDeduction.getString("deductionTypeId"));
								partyDeduction.set("cost", amount);
								partyDeduction.set("thruDate", thruDateEnd);
								partyDeduction.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
								partyDeduction.store();
							}else{
								if(UtilValidate.isNotEmpty(amountNullFlag)){
									partyDeduction.set("thruDate", thruDateEnd);
									partyDeduction.store();
								}
							}
						}else{	
							// Create New One		
							if(UtilValidate.isEmpty(prevAmount) || prevAmount.compareTo(amountToCompare)!= 0){
								GenericValue newEntity = delegator.makeValue("PartyDeduction");
								newEntity.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
								newEntity.set("roleTypeIdTo", "EMPLOYEE");
								newEntity.set("partyIdFrom", partyIdFrom);
								newEntity.set("partyIdTo", partyId);
								newEntity.set("deductionTypeId", payHeadTypeId);
								newEntity.set("periodTypeId", "RATE_MONTH");
								newEntity.set("fromDate", fromDateStart);
								newEntity.set("cost", amount);
								newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
						        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
								newEntity.create();
								partyDeduction.set("thruDate", previousDayEnd);
								partyDeduction.store();
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
	        result = ServiceUtil.returnSuccess("Successfully Updated!!");
	        return result;
	 }//end of service
	
	 public static Map<String, Object>  populatePeriodBillingForAttendanceFinalization(DispatchContext dctx, Map<String, ? extends Object> context)  {
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String periodBillingId = null;
			String partyId = (String) context.get("partyId");
			String customTimePeriodId = (String) context.get("customTimePeriodId");
			
			List conditionList = FastList.newInstance();
	        List periodBillingList = FastList.newInstance();
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS")));
	        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
	    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PB_HR_ATTN_FINAL"));
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	try {
	    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
	    		if(!UtilValidate.isEmpty(periodBillingList)){
	    			Debug.logError("Failed to create 'AttendanceFinalization': Already generated or In-process for the specified period", module);
	    			return ServiceUtil.returnError("Failed to create 'AttendanceFinalization': Already generated or In-process for the specified period");
	    		}
	    	}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
			} 
	    	GenericValue customTimePeriod;
			try {
				customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			} catch (GenericEntityException e1) {
				Debug.logError(e1, e1.getMessage());
				return ServiceUtil.returnError("Error in customTimePeriod" + e1);
			}			
			Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	    	
	    	GenericValue newEntity = delegator.makeValue("PeriodBilling");
	        newEntity.set("billingTypeId", "PB_HR_ATTN_FINAL");
	        newEntity.set("customTimePeriodId", customTimePeriodId);
	        newEntity.set("statusId", "IN_PROCESS");
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
	        newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    try {     
		        delegator.createSetNextSeqId(newEntity);		        
				periodBillingId = (String) newEntity.get("periodBillingId");	
				Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("payrollPeriodId", customTimePeriodId, "orgPartyId", partyId, "periodBillingId",periodBillingId, "userLogin", userLogin);
		        dispatcher.runAsync("populatePayrollAttedanceInternal", runSACOContext);
	    	} catch (GenericEntityException e) {
				Debug.logError(e,"Failed To Create New Period_Billing", module);
				e.printStackTrace();
			}
	        catch (GenericServiceException e) {
	            Debug.logError(e, "Error in calling 'populatePayrollAttedance' service", module);
	            return ServiceUtil.returnError(e.getMessage());
	        } 
	        //result.put("periodBillingId", periodBillingId);
	        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
	    	return result;
	  }
	 
	public static Map<String, Object> populatePayrollAttedanceInternal(DispatchContext dctx, Map<String, ? extends Object> context) {
         
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
			String payrollPeriodId = (String) context.get("payrollPeriodId");
			String orgPartyId =  (String)context.get("orgPartyId");
			String periodBillingId = (String)context.get("periodBillingId");
	        List conditionList = FastList.newInstance();
	        Map input = FastMap.newInstance();
	        Map result = ServiceUtil.returnSuccess();
	        GenericValue periodBilling = null;
	        boolean beganTransaction = false;
	        try {     
	        	beganTransaction = TransactionUtil.begin(72000);
	        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId",periodBillingId),false);
		        if(UtilValidate.isEmpty(periodBilling)){
		        	return ServiceUtil.returnError("invalid period billing");
		        }
				Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("payrollPeriodId", payrollPeriodId, "orgPartyId", orgPartyId, "periodBillingId",periodBillingId, "userLogin", userLogin);
				Map resultAttd = dispatcher.runSync("populatePayrollAttedance", runSACOContext);
				if(ServiceUtil.isError(resultAttd)){
					try {
		                // only rollback the transaction if we started one...
		                TransactionUtil.rollback();
		            } catch (Exception e2) {
		                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
		            }
					periodBilling.set("statusId", "GENERATION_FAIL");
					delegator.store(periodBilling);
					return ServiceUtil.returnSuccess(ServiceUtil.getErrorMessage(resultAttd));
				}
				periodBilling.set("statusId", "GENERATED");
				delegator.store(periodBilling);
		        
	    	} catch (Exception e) {
				Debug.logError(e,"Failed To run payroll attendance", module);
				try {
	                // only rollback the transaction if we started one...
	                TransactionUtil.rollback();
	            } catch (Exception e2) {
	                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	            }
				try{
					periodBilling.set("statusId", "GENERATION_FAIL");
					delegator.store(periodBilling);
				}catch(Exception ex){
					Debug.logError(e,"Failed To run payroll attendance", module);
				}
			}
	        return result;
	        
	 }       
	 
	 public static Map<String, Object> populatePayrollAttedance(DispatchContext dctx, Map<String, ? extends Object> context) {
               
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
			String payrollPeriodId = (String) context.get("payrollPeriodId");
			String orgPartyId =  (String)context.get("orgPartyId");
			String periodBillingId = (String)context.get("periodBillingId");
			//Locale locale = (Locale) context.get("locale");
			Locale locale = new Locale("en","IN");
			TimeZone timeZone = TimeZone.getDefault();
	        List conditionList = FastList.newInstance();
	        GenericValue lastCloseAttedancePeriod= null;
	        String attendancePeriodId = payrollPeriodId;
	        Map input = FastMap.newInstance();
	        
	        try{
	        	
	        	GenericValue payrollPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",payrollPeriodId), false);
	        	if(UtilValidate.isEmpty(payrollPeriod) || !(payrollPeriod.getString("periodTypeId").equals("HR_MONTH"))){
	        		Debug.logError("invalid CustomTimePeriod ::"+payrollPeriodId, module);    			
	 	 		    return ServiceUtil.returnError("invalid CustomTimePeriod");
	        	}
	        	Timestamp timePeriodStart = UtilDateTime.toTimestamp(payrollPeriod.getDate("fromDate"));
	        	Timestamp timePeriodEnd	= UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(payrollPeriod.getDate("thruDate")));	
	        	double noOfCalenderDays = UtilDateTime.getIntervalInDays(timePeriodStart, timePeriodEnd)+1;
	        	// get attendance period here 
	        	input.put("timePeriodId", payrollPeriodId);
	        	input.put("timePeriodStart", timePeriodStart);
	        	input.put("timePeriodEnd", timePeriodEnd);
	        	Timestamp attdTimePeriodStart = timePeriodStart;
	        	Timestamp attdTimePeriodEnd = timePeriodEnd;
	        	
	        	Map resultMap = getPayrollAttedancePeriod(dctx,input);
	  	    	if(ServiceUtil.isError(resultMap)){
	 	 	    	Debug.logError("Error in service findLastClosed Attedance Date ", module);    			
	 	 		    return ServiceUtil.returnError("Error in service findLast Closed Attedance Date");
	 	 	    }
	  	    	//lastCloseAttedancePeriod = ((GenericValue)result.get("lastClosedTimePeriod"))
	  	    	if(UtilValidate.isNotEmpty(resultMap.get("lastCloseAttedancePeriod"))){
	  	    		lastCloseAttedancePeriod = (GenericValue)resultMap.get("lastCloseAttedancePeriod");
	  	    		attdTimePeriodStart = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(lastCloseAttedancePeriod.getDate("fromDate")));
	  	    		if(UtilDateTime.getIntervalInDays(attdTimePeriodStart, timePeriodStart) > 32){
	  	    			Debug.logError("invalid  Attedance Period : "+lastCloseAttedancePeriod.getString("customTimePeriodId") +",startDate :"+ lastCloseAttedancePeriod.getDate("fromDate")+",endDate:"+lastCloseAttedancePeriod.getDate("thruDate"), module);    			
		 	 		    return ServiceUtil.returnError("invalid  Attedance Period : "+lastCloseAttedancePeriod.getString("customTimePeriodId") +",startDate :"+ lastCloseAttedancePeriod.getDate("fromDate")+",endDate:"+lastCloseAttedancePeriod.getDate("thruDate"));
	  	    		}
	  	    		attdTimePeriodEnd = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(lastCloseAttedancePeriod.getDate("thruDate")));
	  	    	}
	        	
	  	    	//Active employes
	  	    	input.clear();
	        	input.put("userLogin", userLogin);
	        	input.put("orgPartyId", orgPartyId);
	        	input.put("fromDate", attdTimePeriodStart);
	        	input.put("thruDate", attdTimePeriodEnd);
	        	resultMap = HumanresService.getActiveEmployements(dctx,input);
	        	List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
	        	Debug.log("employementList============"+employementList.size());
	            //employementList = EntityUtil.filterByAnd(employementList, UtilMisc.toMap("partyIdTo","6851"));
	        	//general holidays in that period
	        	input.clear();
	    		input.put("userLogin", userLogin);
	    		input.put("orgPartyId", orgPartyId);
	    		input.put("fromDate", attdTimePeriodStart);
	    		input.put("thruDate", attdTimePeriodEnd);
	    		resultMap = HumanresService.getGeneralHoliDays(dctx, input);
	    		List<GenericValue> holiDayList = (List<GenericValue>)resultMap.get("holiDayList");
	    		List lopCalDates = FastList.newInstance();
	    		int emplCounter = 0;
	    		double elapsedSeconds;
	    	    Timestamp startTimestamp = UtilDateTime.nowTimestamp();
	    		// second saturday
	    		Timestamp secondSaturDay = UtilDateTime.addDaysToTimestamp(UtilDateTime.getWeekStart(UtilDateTime.getMonthStart(attdTimePeriodEnd),0,2,timeZone,locale), -1);
	    		//Debug.log("employementList===="+employementList.size());
	    		
	        	for(GenericValue employement : employementList) {
	        		String employeeId = employement.getString("partyIdTo");
	        		GenericValue newEntity = delegator.makeValue("PayrollAttendance");
	        		newEntity.set("customTimePeriodId", lastCloseAttedancePeriod.getString("customTimePeriodId"));
	        		newEntity.set("partyId",employeeId);
	        		newEntity.set("noOfCalenderDays", new BigDecimal(noOfCalenderDays));
	        		newEntity.set("noOfAttendedHoliDays", BigDecimal.ZERO);
	        		newEntity.set("noOfAttendedSsDays", BigDecimal.ZERO);
	        		newEntity.set("noOfArrearDays", BigDecimal.ZERO);
	        		newEntity.set("noOfCompoffAvailed", BigDecimal.ZERO);
	        		newEntity.set("lateMin", BigDecimal.ZERO);
	        		newEntity.set("extraMin", BigDecimal.ZERO);
	        		double noOfAttendedSsDays = 0;
	        		double lossOfPayDays =0;
	        		double lateMin =0;
	        		double extraMin =0;
	        		emplCounter++;
               		if ((emplCounter % 20) == 0) {
               			elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
               			Debug.logImportant("Completed " + emplCounter + " employee [ in " + elapsedSeconds + " seconds]", module);
               		}
	        		BigDecimal noOfEmployementDays = new BigDecimal(noOfCalenderDays);
	        		Timestamp employementFromaDate = UtilDateTime.getDayStart(employement.getTimestamp("fromDate"));
	        		Timestamp employementThruDate = employement.getTimestamp("thruDate");
	        		if(UtilValidate.isNotEmpty(employementThruDate) && (employementThruDate.compareTo(timePeriodEnd) <=0)){
	        			Map inputMap = FastMap.newInstance();
	        			inputMap.put("userLogin", userLogin);
	        			inputMap.put("orgPartyId", employeeId);
	        			inputMap.put("fromDate", UtilDateTime.addDaysToTimestamp(employementThruDate,1));
	    	        	resultMap = HumanresService.getActiveEmployements(dctx,inputMap);
	    	        	List<GenericValue> empEmployementList = (List<GenericValue>)resultMap.get("employementList");
	    	        	GenericValue empEmployement = EntityUtil.getFirst(empEmployementList);
	    	        	if(UtilValidate.isEmpty(empEmployement)){
	    	        		noOfEmployementDays = new BigDecimal(UtilDateTime.getIntervalInDays(timePeriodStart, employementThruDate)+1);
	    	        	}
	        			
	        		}
	        		//Debug.log("noOfEmployementDays==========="+noOfEmployementDays);
	        		conditionList.clear();
			        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,employeeId));
			    	conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(attdTimePeriodStart)));
			    	conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(attdTimePeriodEnd)));
			    	EntityCondition condition= EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			    	try {
			    		List<GenericValue> punchList = delegator.findList("EmplPunch", condition, null,null, null, false);
			    		//Debug.log("punchList size========"+punchList.size());
			    		
			    		conditionList.clear();
			    		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,employeeId));
					    conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(attdTimePeriodStart)));
					    conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(attdTimePeriodEnd)));
					    condition= EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					    List<GenericValue> emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition, null,null, null, false);
			    		if(UtilValidate.isEmpty(emplDailyAttendanceDetailList)){
			    			Debug.logWarning("No shift details for employee"+employeeId, module);
			    		}
			    		
			    		//get leaves for the month
			    		input.clear();
			    		input.put("userLogin", userLogin);
			    		input.put("partyId", employeeId);
			    		input.put("timePeriodStart", attdTimePeriodStart);
			    		input.put("timePeriodEnd", attdTimePeriodEnd);
			    		resultMap = EmplLeaveService.fetchLeaveDaysForPeriod(dctx, input);
			    		if(ServiceUtil.isError(resultMap)){
			    			Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultMap), 
			            			null, null, null);
			    		}
			    		
			    		newEntity.set("noOfLeaveDays", resultMap.get("noOfLeaveDays"));
			    		List<GenericValue> leaves = (List)resultMap.get("leaves");
			    		GenericValue employeeDetail = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",employeeId), true);
			    		//here handle no punch's in second half off the attendance month 
			    		//List<GenericValue> payrollPeriodPunchList = EntityUtil.filterByCondition(punchList, EntityCondition.makeCondition("punchDateTime",EntityOperator.BETWEEN,UtilMisc.toList(timePeriodStart,attdTimePeriodEnd)));
			    		List<GenericValue> payrollPeriodPunchList = EntityUtil.filterByCondition(punchList, EntityCondition.makeCondition(EntityCondition.makeCondition("punchdate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(timePeriodStart)),EntityOperator.AND
			    				                                     ,EntityCondition.makeCondition("punchdate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(attdTimePeriodEnd))));
			    		
			    		//here handle no punch and no leaves for the period then populate noOfPayableDays zero
			    		if(UtilValidate.isEmpty(payrollPeriodPunchList) && (((BigDecimal)resultMap.get("noOfLeaveDays")).compareTo(BigDecimal.ZERO) ==0) &&
			    				   (UtilValidate.isNotEmpty(employeeDetail.getString("punchType")) && !(employeeDetail.getString("punchType").equalsIgnoreCase("N")))){
			    			Debug.logWarning("No punchs for employee"+employeeId, module);
			    			newEntity.set("lossOfPayDays", noOfEmployementDays);
				    		newEntity.set("noOfAttendedHoliDays", BigDecimal.ZERO);
				    		newEntity.set("noOfAttendedSsDays", BigDecimal.ZERO);
				    		newEntity.set("noOfAttendedWeeklyOffDays", BigDecimal.ZERO);
				    		newEntity.set("noOfPayableDays", BigDecimal.ZERO);
				    		newEntity.set("lateMin", BigDecimal.ZERO);
			        		newEntity.set("extraMin", BigDecimal.ZERO);
				    		delegator.createOrStore(newEntity);
				    		continue;
			    		}
			    		lossOfPayDays = lossOfPayDays+ ((BigDecimal)resultMap.get("lossOfPayDays")).doubleValue();
			    		
			    		double noOfAttendedHoliDays =0;
			    		
			    		// get employee weekly off day weeklyOff
			    		
			    		double noOfAttendedWeeklyOffDays =0;
			    		Calendar c1=Calendar.getInstance();
			    		c1.setTime(UtilDateTime.toSqlDate(attdTimePeriodStart));
			    		Calendar c2=Calendar.getInstance();
			    		c2.setTime(UtilDateTime.toSqlDate(attdTimePeriodEnd));
			    		String emplWeeklyOffDay = "SUNDAY";
			    		
				        if(UtilValidate.isNotEmpty(employeeDetail) && UtilValidate.isNotEmpty(employeeDetail.getString("weeklyOff"))){
				        	emplWeeklyOffDay = employeeDetail.getString("weeklyOff");
				         }
			    		while(c2.after(c1)){
			    			String weekName = (c1.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale));
			    			Timestamp cTime = new Timestamp(c1.getTimeInMillis());
			    			Timestamp cTimeEnd = UtilDateTime.getDayEnd(cTime);
			    			//Debug.log("cTime==========="+cTime);
			    			List<GenericValue> dayPunchList = EntityUtil.filterByCondition(punchList, EntityCondition.makeCondition(EntityCondition.makeCondition("punchdate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(cTime)) , EntityOperator.AND,EntityCondition.makeCondition("punchdate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(cTime))));
			    			//dayPunchList = EntityUtil.filterByCondition(punchList, EntityCondition.makeCondition(EntityCondition.makeCondition("shiftType",EntityOperator.NOT_EQUAL,"SHIFT_NIGHT") , EntityOperator.AND,EntityCondition.makeCondition("InOut",EntityOperator.NOT_EQUAL,"OUT")));
			    			// filter by normal punchType
			    			//call edit punch to re-calculate late min
			    			if(UtilValidate.isNotEmpty(dayPunchList) && UtilValidate.isNotEmpty(periodBillingId)){
			    				for(GenericValue punchEntry : dayPunchList){
			    					Map punchCtx = UtilMisc.toMap("userLogin",userLogin);
			    					punchCtx.putAll(punchEntry);
			    					Map result = dispatcher.runSync("emplPunch", punchCtx);
			    					if(ServiceUtil.isError(result)){
			    						Debug.logError(ServiceUtil.getErrorMessage(result), module);
			    						return result;
			    					}
			    				}
			    			}
			    			dayPunchList = EntityUtil.filterByCondition(dayPunchList, EntityCondition.makeCondition("PunchType",EntityOperator.IN,UtilMisc.toList("Normal","Ood")));
			    			/*if((EntityUtil.filterByCondition(dayPunchList, EntityCondition.makeCondition("PunchType",EntityOperator.EQUALS,"Ood"))).size() >0){
			    				dayPunchList.addAll(EntityUtil.filterByCondition(dayPunchList, EntityCondition.makeCondition("PunchType",EntityOperator.EQUALS,"Ood")));
			    			}*/
			    			
			    			List cHoliDayList = EntityUtil.filterByCondition(holiDayList, EntityCondition.makeCondition(EntityCondition.makeCondition("holiDayDate",EntityOperator.LESS_THAN_EQUAL_TO,cTimeEnd) , EntityOperator.AND,EntityCondition.makeCondition("holiDayDate",EntityOperator.GREATER_THAN_EQUAL_TO,cTime)));
			    			List cDayLeaves = EntityUtil.filterByDate(leaves, cTime);
			    			List cDayLeaveFraction = EntityUtil.getFieldListFromEntityList(cDayLeaves, "dayFractionId", true);
			    			List<GenericValue> dayShiftList = EntityUtil.filterByCondition(emplDailyAttendanceDetailList, EntityCondition.makeCondition(EntityCondition.makeCondition("date",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(cTime)) , EntityOperator.AND,EntityCondition.makeCondition("date",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(cTime))));
			    			// handle no punch employees here
			    			if(((UtilValidate.isNotEmpty(employeeDetail.getString("punchType")) && (employeeDetail.getString("punchType").equalsIgnoreCase("N"))))
			    					||  (UtilValidate.isNotEmpty(cDayLeaves) && UtilValidate.isEmpty(cDayLeaveFraction))){
			    				c1.add(Calendar.DATE,1);
		    					continue;
		    				}
			    			//TO:DO need to handle SHIFT_NIGHT mispunch
			    			Boolean shiftFalg = Boolean.FALSE;
			    			if(UtilValidate.isNotEmpty(dayShiftList)){
			    				for(GenericValue dayShift :dayShiftList){
			    					if(UtilValidate.isEmpty(cDayLeaveFraction) && (UtilValidate.isEmpty(employeeDetail.getString("punchType"))||((UtilValidate.isNotEmpty(employeeDetail.getString("punchType")) 
			    										&& (!(employeeDetail.getString("punchType").equalsIgnoreCase("O")))))) && (!(emplWeeklyOffDay.equalsIgnoreCase(weekName))) && UtilValidate.isEmpty(cHoliDayList) && (cTime.compareTo(secondSaturDay) != 0)){
			    						if(UtilValidate.isNotEmpty(dayShift.getBigDecimal("overrideLateMin"))){
				    						lossOfPayDays = lossOfPayDays+(((dayShift.getBigDecimal("overrideLateMin")).doubleValue())/480);
				    						lateMin= lateMin+(((dayShift.getBigDecimal("overrideLateMin")).doubleValue())/480);
				    					}else{
				    						if(UtilValidate.isNotEmpty(dayShift.getBigDecimal("lateMin"))){
				    							lossOfPayDays = lossOfPayDays+(((dayShift.getBigDecimal("lateMin")).doubleValue())/480);
				    							lateMin= lateMin+(((dayShift.getBigDecimal("lateMin")).doubleValue())/480);
				    						}
				    						
				    					}
				    					
			    					}
			    					extraMin=extraMin+(((dayShift.getBigDecimal("extraMin")).doubleValue())/480);
			    				}
			    				List dayShifts = EntityUtil.getFieldListFromEntityList(dayShiftList, "shiftType", true);
			    				List<GenericValue> inPunch = EntityUtil.filterByAnd(dayPunchList, UtilMisc.toMap("PunchType","Normal","InOut","IN"));
			    				if((inPunch.size() >= 1) && (dayShifts.contains("SHIFT_NIGHT") || (UtilValidate.isNotEmpty(employeeDetail.getString("punchType")) && (employeeDetail.getString("punchType").equalsIgnoreCase("O"))))){
			    					shiftFalg = Boolean.TRUE;
			    				}else{
			    					// if no night shift then ignore night shift punch outs ,since those are prevoius day shift related punchs
			    					List tempCondList = FastList.newInstance();
			    					tempCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("shiftType",EntityOperator.EQUALS,"SHIFT_NIGHT"),EntityOperator.AND,
	    									EntityCondition.makeCondition("InOut",EntityOperator.EQUALS,"OUT")));
			    					EntityCondition tempCond = EntityCondition.makeCondition(tempCondList ,EntityOperator.AND);
			    					List dayPunchNightList = EntityUtil.filterByCondition(dayPunchList,tempCond);
			    					dayPunchList.removeAll(dayPunchNightList);
			    					//Debug.log("ctime===="+cTime);
			    					//Debug.log("dayPunchList==========="+dayPunchList);
			    				}
			    				/*// here handle multiple shifts on same day
				    			if(UtilValidate.isNotEmpty(dayShifts) && dayShifts.size() >1){
				    				noOfEmployementDays = noOfEmployementDays.add(new BigDecimal(dayShifts.size()-1));
				    			}*/
			    			}
			    			
			    			List encashmentStatusList = EntityUtil.getFieldListFromEntityList(dayShiftList, "encashmentStatus", true);
			    			if((UtilValidate.isNotEmpty(dayPunchList) && dayPunchList.size() >=2) || shiftFalg){
			    				if(!shiftFalg && (dayPunchList.size()%2) !=0){
			    					// mis punch consider it as loss of pay
			    					//Debug.log("mis punch lossOfPayDays===="+cTime);
			    					lossOfPayDays = lossOfPayDays+1;
			    					c1.add(Calendar.DATE,1);
			    					continue;
			    				}
			    				if(emplWeeklyOffDay.equalsIgnoreCase(weekName)){
			    					//noOfAttendedWeeklyOffDays = noOfAttendedWeeklyOffDays+(dayPunchList.size()/2);
			    					noOfAttendedWeeklyOffDays = noOfAttendedWeeklyOffDays+1;
			    				}
			    				if(cTime.compareTo(secondSaturDay)== 0 && UtilValidate.isNotEmpty(encashmentStatusList) && encashmentStatusList.contains("CASH_ENCASHMENT")){
			    					//noOfAttendedSsDays = noOfAttendedSsDays+(dayPunchList.size()/2);
			    					noOfAttendedSsDays = noOfAttendedSsDays+1;
			    				}
			    				
			    				if(UtilValidate.isNotEmpty(cHoliDayList) && UtilValidate.isNotEmpty(encashmentStatusList) && encashmentStatusList.contains("CASH_ENCASHMENT")){
			    					//noOfAttendedHoliDays = noOfAttendedHoliDays+(dayPunchList.size()/2);
			    					noOfAttendedHoliDays = noOfAttendedHoliDays+1;
			    				}
			    				
			    			}else if((!(emplWeeklyOffDay.equalsIgnoreCase(weekName))) && (cTime.compareTo(secondSaturDay) != 0) 
			    					 && UtilValidate.isEmpty(cHoliDayList) &&  UtilValidate.isEmpty(cDayLeaves)){
			    				// no punch ,not weekly off ,not secondSaturDay, not general holiday  and no leave then consider it as lossOfPay
			    				lossOfPayDays = lossOfPayDays+1;
			    				//Debug.log("no punch lossOfPayDays===="+cTime);
			    			}else if(UtilValidate.isNotEmpty(cDayLeaves) && UtilValidate.isNotEmpty(cDayLeaveFraction)){
			    				lossOfPayDays = lossOfPayDays+0.5;
			    			}
			    			c1.add(Calendar.DATE,1);
			    		}
			    		newEntity.set("lateMin",  new BigDecimal(lateMin));
		        		newEntity.set("extraMin", new BigDecimal(extraMin));
			    		newEntity.set("lossOfPayDays", new BigDecimal(lossOfPayDays));
			    		newEntity.set("noOfAttendedHoliDays", new BigDecimal(noOfAttendedHoliDays));
			    		newEntity.set("noOfAttendedSsDays", new BigDecimal(noOfAttendedSsDays));
			    		newEntity.set("noOfAttendedWeeklyOffDays", new BigDecimal(noOfAttendedWeeklyOffDays));
			    		newEntity.set("noOfPayableDays",noOfEmployementDays.subtract(newEntity.getBigDecimal("lossOfPayDays")));
			    		//Debug.log("newEntity============="+newEntity);
			    		delegator.createOrStore(newEntity);
			    		
			    		//here populate shift wise details
			    		conditionList.clear();
				        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,employeeId));
				        if(UtilValidate.isNotEmpty(lastCloseAttedancePeriod)){
				        	conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(lastCloseAttedancePeriod.getDate("fromDate"))));
				 	    	conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(lastCloseAttedancePeriod.getDate("thruDate"))));
				        	
				        }else{
				        	conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(attdTimePeriodStart)));
				 	    	conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(attdTimePeriodEnd)));
				        }
				        	
				       
				    	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			    	
			    		Map inputCtx =FastMap.newInstance();
			    		Map shiftDetailMap = FastMap.newInstance();
			    		Map availedCanteenDetailMap = FastMap.newInstance();
			    		inputCtx.put("userLogin", userLogin);
			    		inputCtx.put("partyId", employeeId);
			    		inputCtx.put("timePeriodStart", attdTimePeriodStart);
			    		inputCtx.put("timePeriodEnd", attdTimePeriodEnd);
			    		//inputCtx.put("leaveTypeId", "HPL");
			    		resultMap = EmplLeaveService.fetchLeaveDaysForPeriod(dctx, input);
			    		List<GenericValue> leavesList = (List)resultMap.get("leaves");
			    		//input.put("leaveTypeId", "CML");
			    		//resultMap = EmplLeaveService.fetchLeaveDaysForPeriod(dctx, input);
			    		//leavesList.addAll((List)resultMap.get("leaves"));
			    		emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition, null,null, null, false);
			    		if(UtilValidate.isNotEmpty(emplDailyAttendanceDetailList)){
				    		for( GenericValue  emplDailyAttendanceDetail : emplDailyAttendanceDetailList){
				    			//Debug.log("emplDailyAttendanceDetail==========="+emplDailyAttendanceDetail);
				    			String shiftType = emplDailyAttendanceDetail.getString("shiftType");
				    			if(UtilValidate.isNotEmpty(leavesList)){
				    				List cDayLeaves = EntityUtil.filterByDate(leavesList, UtilDateTime.toTimestamp(emplDailyAttendanceDetail.getDate("date")));
					    			List cDayLeaveFraction = EntityUtil.getFieldListFromEntityList(cDayLeaves, "dayFractionId", true);
				    				if(UtilValidate.isNotEmpty(cDayLeaves) &&  UtilValidate.isNotEmpty(cDayLeaveFraction)){
					    				continue;
					    			}
				    			}
				    			
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
					    				availedCanteenDetailMap.put(shiftType,(((Integer)(availedCanteenDetailMap.get(shiftType))).intValue()+1));
					    			}
				    			}
				    		}
			    		}
                       if(UtilValidate.isNotEmpty(shiftDetailMap)){
                    	   Iterator entries = shiftDetailMap.entrySet().iterator();
                    	   GenericValue newEntityShift = delegator.makeValue("PayrollAttendanceShiftWise");
                    	   newEntityShift.set("customTimePeriodId", lastCloseAttedancePeriod.getString("customTimePeriodId"));
                    	   newEntityShift.set("partyId",employeeId);
                    	  
                           while (entries.hasNext()) {
                             Entry entry = (Entry) entries.next();
                             String shiftTypeId = (String)entry.getKey();
                             Integer noOfDays = (Integer)entry.getValue();
                             newEntityShift.set("shiftTypeId", shiftTypeId);
                      	     newEntityShift.set("noOfDays", new BigDecimal(noOfDays));
                      	     newEntityShift.set("availedCanteenDays",BigDecimal.ZERO);
                      	     if(UtilValidate.isNotEmpty(availedCanteenDetailMap.get(shiftTypeId))){
                      	    	newEntityShift.set("availedCanteenDays", new BigDecimal((Integer)availedCanteenDetailMap.get(shiftTypeId)));
                      	     }
                      	     delegator.createOrStore(newEntityShift);
                             
                           }
                    	   
                       }
		    		
			    		
			    	}catch (GenericEntityException e) {
			    		 Debug.logError(e, module);             
			             return ServiceUtil.returnError("Failed to populate payrollattedance " + e);
					} 
	        	}
	        	//populate calenderdays
	  	    	
	        }catch (Exception e) {
				// TODO: handle exception
	        	 Debug.logError(e, module);             
	            
	        	 return ServiceUtil.returnError("Failed to populate payrollattedance " + e);
			}
	       Map<String, Object> result = ServiceUtil.returnSuccess();
	      //result.put("lastCloseAttedancePeriod", lastCloseAttedancePeriod);
	      return result;  
	 }	 
	 public static Map<String, Object>  setAttendanceFinalizationStatus(DispatchContext dctx, Map<String, ? extends Object> context)  {
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = FastMap.newInstance();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String periodBillingId = (String) context.get("periodBillingId");
			String statusId = (String) context.get("statusId");
			
			GenericValue periodBilling = null;
	    	try {
	    		try {
					periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
				} catch (GenericEntityException e1) {
					Debug.logError(e1,"Error While Finding PeriodBilling");
					return ServiceUtil.returnError("Error While Finding PeriodBilling" + e1);
				}
	    		
	    		String payrollPeriodId = periodBilling.getString("customTimePeriodId");
	    		List condList =FastList.newInstance();
	    		condList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,payrollPeriodId));
	    		condList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("COM_CANCELLED","CANCEL_FAILED" ,"GENERATION_FAIL")));
	    		condList.add(EntityCondition.makeCondition("billingTypeId",EntityOperator.EQUALS,"PAYROLL_BILL"));
      			
	    		
	    		EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
	    		List<GenericValue> payrollBillg = delegator.findList("PeriodBilling",cond,null,null,null,false);
	    		if(UtilValidate.isNotEmpty(payrollBillg)){
	    			Debug.logError("payroll allready generated ,please cancel payroll and do attendance finalization or cancel  ::"+payrollPeriodId, module);    			
	 	 		    return ServiceUtil.returnError("payroll allready generated ,please cancel payroll and do attendance finalization or cancel :"+payrollBillg);
	    		}
	    		
	    		if(statusId.equals("COM_CANCELLED")){
	    			GenericValue payrollPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",payrollPeriodId), false);
		        	if(UtilValidate.isEmpty(payrollPeriod) || !(payrollPeriod.getString("periodTypeId").equals("HR_MONTH"))){
		        		Debug.logError("invalid CustomTimePeriod ::"+payrollPeriodId, module);    			
		 	 		    return ServiceUtil.returnError("invalid CustomTimePeriod");
		        	}
		        	Timestamp timePeriodStart = UtilDateTime.toTimestamp(payrollPeriod.getDate("fromDate"));
		        	Timestamp timePeriodEnd	= UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(payrollPeriod.getDate("thruDate")));	
		        	// get attendance period here 
		        	Map input = UtilMisc.toMap("userLogin",userLogin);
		        	input.put("timePeriodId", payrollPeriodId);
		        	input.put("timePeriodStart", timePeriodStart);
		        	input.put("timePeriodEnd", timePeriodEnd);
		        	Timestamp attdTimePeriodStart = timePeriodStart;
		        	Timestamp attdTimePeriodEnd = timePeriodEnd;
		        	
		        	Map resultMap = getPayrollAttedancePeriod(dctx,input);
		  	    	if(ServiceUtil.isError(resultMap)){
		 	 	    	Debug.logError("Error in service findLastClosed Attedance Date ", module);    			
		 	 		    return ServiceUtil.returnError("Error in service findLast Closed Attedance Date");
		 	 	    }
		  	    	String attendancePeriodId = null;
		  	    	if(UtilValidate.isNotEmpty(resultMap.get("lastCloseAttedancePeriod"))){
		  	    		GenericValue lastCloseAttedancePeriod = (GenericValue)resultMap.get("lastCloseAttedancePeriod");
			  	    	attendancePeriodId = lastCloseAttedancePeriod.getString("customTimePeriodId");
		  	    	}
		  	    	List<GenericValue> payrollAttendance = delegator.findByAnd("PayrollAttendance", UtilMisc.toMap("customTimePeriodId",attendancePeriodId));
		  	    	List<GenericValue> payrollAttendanceshiftWise = delegator.findByAnd("PayrollAttendanceShiftWise", UtilMisc.toMap("customTimePeriodId",attendancePeriodId));
		  	    	if(UtilValidate.isNotEmpty(payrollAttendance)){
		    			delegator.removeAll(payrollAttendance);
		    			delegator.removeAll(payrollAttendanceshiftWise);
		    			/*periodBilling.set("statusId", "COM_CANCELLED");
		    			periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    			periodBilling.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						periodBilling.store();*/
		    		}else{
		    			periodBilling.set("statusId", "CANCEL_FAILED");
		    			periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    			periodBilling.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		    			periodBilling.store();
		    			return ServiceUtil.returnSuccess();
		    		}
	    		}
	    		Map input = UtilMisc.toMap("userLogin",userLogin);
	    		input.put("periodBillingId", periodBillingId);
	    		input.put("statusId", statusId);
	    		input.put("lastModifiedDate", UtilDateTime.nowTimestamp());
	    		input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	    		result = dispatcher.runSync("SetPeriodBillingStatus", input);
        		if(ServiceUtil.isError(result)){
        			 Debug.logError("Error while updating attendance status:"+result, module);
 		             return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
        		}
	    		
	    	}catch (Exception e) {
	    		 Debug.logError(e, module);
	             return ServiceUtil.returnError("Failed to find payrollHeaderItemList " + e);
			} 
			result = ServiceUtil.returnSuccess("PayRoll Attendance status successfully changed..");
			return result;
	}// end of service
	 
	 public static Map<String, Object> UpdateAttendance(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      String partyId = (String) context.get("partyId");
	      String customTimePeriodId = (String)context.get("customTimePeriodId");
	      String timePeriodId = (String)context.get("timePeriodId");
	      BigDecimal noOfArrearDays=(BigDecimal)context.get("noOfArrearDays");
	      BigDecimal lossOfPayDays=(BigDecimal)context.get("lossOfPayDays");
	      BigDecimal noOfAttendedSsDays=(BigDecimal)context.get("noOfAttendedSsDays");
	      BigDecimal noOfAttendedHoliDays=(BigDecimal)context.get("noOfAttendedHoliDays");
	      Map result = ServiceUtil.returnSuccess();
	      BigDecimal lateMin=(BigDecimal)context.get("lateMin");
	      lateMin = lateMin.divide(BigDecimal.valueOf(480), 4, BigDecimal.ROUND_HALF_UP);
	      try{
	      			List conditionLis=FastList.newInstance();
	      			conditionLis.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,timePeriodId));
	      			conditionLis.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("COM_CANCELLED","CANCEL_FAILED","GENERATION_FAIL")));
	      			conditionLis.add(EntityCondition.makeCondition("billingTypeId",EntityOperator.EQUALS,"PAYROLL_BILL"));
	      			
	      			EntityCondition conditon=EntityCondition.makeCondition(conditionLis,EntityOperator.AND);
	      			List<GenericValue> statusList=delegator.findList("PeriodBilling",conditon, null, null,null,false);
	      	if(UtilValidate.isEmpty(statusList)){
	      		try {
	      			List conditionList = FastList.newInstance();
	      			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
	      			conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS , customTimePeriodId));
	      			EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
	      			List<GenericValue> emplPayrollAttendanceDetailList = delegator.findList("PayrollAttendance", condition, null, null, null, false);
	      				for (int i = 0; i < emplPayrollAttendanceDetailList.size(); ++i) {
	      					GenericValue employPayrollDetails = emplPayrollAttendanceDetailList.get(i);
	      					BigDecimal arrearDays=employPayrollDetails.getBigDecimal("noOfArrearDays");
	      					BigDecimal noOfPayableDays= employPayrollDetails.getBigDecimal("noOfPayableDays");
	      					if(UtilValidate.isEmpty(lossOfPayDays)){
	      						lossOfPayDays = employPayrollDetails.getBigDecimal("lossOfPayDays");
	      					}
	      					
	      					if(UtilValidate.isEmpty(arrearDays)){
	      						employPayrollDetails.set("noOfArrearDays",noOfArrearDays);
	      						noOfPayableDays=noOfPayableDays.add(noOfArrearDays);
	      						employPayrollDetails.set("noOfPayableDays",noOfPayableDays);
	      						employPayrollDetails.store();
	      					}
	      					else{
	      						  if(UtilValidate.isNotEmpty(noOfArrearDays)){
	      							noOfPayableDays=noOfPayableDays.subtract(arrearDays);
	      							noOfPayableDays=noOfPayableDays.add(noOfArrearDays);
	      							employPayrollDetails.set("noOfPayableDays",noOfPayableDays);
	      							employPayrollDetails.set("noOfArrearDays",noOfArrearDays);
	      							employPayrollDetails.store();
	      						  }
	      						    
	      					}
	      					if(UtilValidate.isNotEmpty(lossOfPayDays) && lossOfPayDays.compareTo(BigDecimal.ZERO)>=0){
	      						BigDecimal empLopDays=employPayrollDetails.getBigDecimal("lossOfPayDays");
	      						if(UtilValidate.isEmpty(empLopDays)){
	      							empLopDays = BigDecimal.ZERO;
	      						}
	      						noOfPayableDays=noOfPayableDays.add(empLopDays);
      							noOfPayableDays=noOfPayableDays.subtract(lossOfPayDays);
      							employPayrollDetails.set("noOfPayableDays",noOfPayableDays);
      							employPayrollDetails.set("lossOfPayDays",lossOfPayDays);
      							employPayrollDetails.store();
	      					}
	      					if(UtilValidate.isNotEmpty(noOfAttendedSsDays) && noOfAttendedSsDays.compareTo(BigDecimal.ZERO)>=0){
      							employPayrollDetails.set("noOfAttendedSsDays",noOfAttendedSsDays);
      							employPayrollDetails.store();
	      					}
	      					if(UtilValidate.isNotEmpty(noOfAttendedHoliDays) && noOfAttendedHoliDays.compareTo(BigDecimal.ZERO)>=0){
      							employPayrollDetails.set("noOfAttendedHoliDays",noOfAttendedHoliDays);
      							employPayrollDetails.store();
	      					}
	      					if(UtilValidate.isNotEmpty(lateMin) && lateMin.compareTo(BigDecimal.ZERO)>=0){
	      						BigDecimal empLateMin=employPayrollDetails.getBigDecimal("lateMin");
	      						if(UtilValidate.isEmpty(empLateMin)){
	      							empLateMin = BigDecimal.ZERO;
	      						}
	      						noOfPayableDays=noOfPayableDays.add(empLateMin);
	      						noOfPayableDays=noOfPayableDays.subtract(lateMin);
	      						lossOfPayDays = lossOfPayDays.subtract(empLateMin);
	      						lossOfPayDays = lossOfPayDays.add(lateMin);
	      						
	      						employPayrollDetails.set("noOfPayableDays",noOfPayableDays);
	      						employPayrollDetails.set("lossOfPayDays",lossOfPayDays);
	      						employPayrollDetails.set("lateMin",lateMin);
	      						employPayrollDetails.store();
	      					}
	    	      	}
	      		} catch (GenericEntityException e) {
	      			Debug.logError(e, module);
	      			return ServiceUtil.returnError(e.toString());
	      		}
	      		result = ServiceUtil.returnSuccess("Successfully Updated!!");
	      		return result;
	      		}
			
	      	}
	      	catch (GenericEntityException e) {
	      			Debug.logError(e, module);
	      			return ServiceUtil.returnError(e.toString());
	      	}
	      	GenericValue customTimePeriod;
	      		try {
	      			customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
	      		} catch (GenericEntityException e1) {
	      			Debug.logError(e1,"Error While Finding Customtime Period");
	      			return ServiceUtil.returnError("Error While Finding Customtime Period" + e1);
	      		}
	      		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	      		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	      		return ServiceUtil.returnError("Already Payroll Generated For The TimePeriod"+UtilDateTime.toDateString(fromDateTime,"dd MMMMM, yyyy")+"-"+UtilDateTime.toDateString(thruDateTime,"dd MMMMM, yyyy"));
	  }
	 
	 
	 public static Map<String, Object> UpdateLateMins(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      String partyId = (String) context.get("partyId");
	      Date date=(Date)context.get("date");
	      String timePeriodId = (String)context.get("timePeriodId");
	      String overrideReason= (String)context.get("overrideReason");
	      String encashmentStatus=(String) context.get("encashmentStatus");
	      BigDecimal overrideLateMin=(BigDecimal)context.get("overrideLateMin");
	      Map result = ServiceUtil.returnSuccess();
  		try{
  			
  			String userId= (String) userLogin.get("userLoginId");
  			
  			List conditionLis=FastList.newInstance();
  			conditionLis.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,timePeriodId));
  			conditionLis.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("COM_CANCELLED","CANCEL_FAILED")));
  			EntityCondition conditon=EntityCondition.makeCondition(conditionLis,EntityOperator.AND);
  			List<GenericValue> statusList=delegator.findList("PeriodBilling",conditon, null, null,null,false);
		  	if(UtilValidate.isEmpty(statusList)){
		  		List conditionList = FastList.newInstance();
  				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
  				conditionList.add(EntityCondition.makeCondition("date", EntityOperator.EQUALS , date));
  				EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
  				List<GenericValue> EmplDailyAttendanceDetail = delegator.findList("EmplDailyAttendanceDetail", condition, null, null, null, false);
  				for (int i = 0; i < EmplDailyAttendanceDetail.size(); ++i) {
  					GenericValue DailyAttendanceDetail = EmplDailyAttendanceDetail.get(i);
  					if(UtilValidate.isNotEmpty(encashmentStatus))
  						DailyAttendanceDetail.set("encashmentStatus",encashmentStatus);
  					if(UtilValidate.isNotEmpty(overrideLateMin)){
	  					DailyAttendanceDetail.set("overrideLateMin",overrideLateMin);
	  					DailyAttendanceDetail.set("overrideReason",overrideReason);
	  					DailyAttendanceDetail.set("overridenBy", userId);
  					}
  					DailyAttendanceDetail.store();
  				}
		  	}
		  	else{
		  		Debug.logError("Already Payroll Generated ",module);
		  		return ServiceUtil.returnError("Already Payroll Generated ");
		  	}
  			
  		}catch (GenericEntityException e) {
  				Debug.logError(e, module);
  				return ServiceUtil.returnError(e.toString());
  			}
  		result = ServiceUtil.returnSuccess("Successfully Updated!!");
  		return result;
  	}
	 
	 
	/*public static  Map<String, Object> getCustomTimePeriodIdForDate(DispatchContext dctx, Map context) {
		 	GenericValue userLogin = (GenericValue) context.get("userLogin");
		 	Date date =  (Date)context.get("date");
		 	Timestamp punchDate = (Timestamp)context.get("punchdate");
	        Delegator delegator = dctx.getDelegator();
	        Timestamp dateTime=null;
	        if(UtilValidate.isNotEmpty(punchDate)){
	        	 dateTime = punchDate;
	        }else{
	        	 dateTime = UtilDateTime.toTimestamp(date);
	        }
	    	Timestamp dateStart = UtilDateTime.getDayStart(dateTime);
	    	Timestamp dateEnd = UtilDateTime.getDayEnd(dateTime);
	        Map result = ServiceUtil.returnSuccess();
	        String customTimePeriodId = null;
	        try {
	        	List condList = FastList.newInstance();
				condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
				condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(dateStart.getTime())));
				condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(dateEnd.getTime())));
				EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 	
				List<GenericValue> customTimePeriodList = delegator.findList("CustomTimePeriod", cond, null, null, null, false);
				if(UtilValidate.isNotEmpty(customTimePeriodList)){
					GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
					customTimePeriodId = customTimePeriod.getString("customTimePeriodId");
				}
				
				if(UtilValidate.isNotEmpty(customTimePeriodId)){
			        List conditionList = FastList.newInstance();
			        List periodBillingList = FastList.newInstance();
			        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS","APPROVED")));
			        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
			    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
			    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			    	periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
			    	if(UtilValidate.isNotEmpty(periodBillingList)){	    			
			    		return ServiceUtil.returnError("Already Payroll Generated ");
			        }
				}
	        }
	        catch (GenericEntityException e) {
	            Debug.logError(e, "Error retrieving CustomTimePeriodId");
	        }        
	        return result;
	}*/
	
	 public static Map<String, Object> getEmployeeSalaryTotalsForPeriod(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      String partyId = (String) context.get("partyId");
	      Timestamp fromDate = (Timestamp) context.get("fromDate");
	      if (UtilValidate.isEmpty(fromDate)) {
	    	  Debug.logError("fromDate cannot be empty", module);
	    	  return ServiceUtil.returnError("fromDate cannot be empty");
	      }
	      Timestamp thruDate = (Timestamp) context.get("thruDate");
	      if (UtilValidate.isEmpty(thruDate)) {
	    	  Debug.logError("thruDate cannot be empty", module);
	    	  return ServiceUtil.returnError("thruDate cannot be empty");
	      }
	      Map result = ServiceUtil.returnSuccess();
	      
	      Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
	      Timestamp thruDateEnd = UtilDateTime.getDayEnd(thruDate);
	      
	      Map customTimePeriodMap = FastMap.newInstance();
	      try{
	    	  List condList = FastList.newInstance();
	    	  condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
	    	  condList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
	    	  condList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
	    	  condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,new java.sql.Date(fromDateStart.getTime())));
	    	  condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(thruDateEnd.getTime())));
	    	  EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 	
	    	  List<GenericValue> PeriodBillingAndCustomTimePeriodList = delegator.findList("PeriodBillingAndCustomTimePeriod", cond, null, null, null, false);
	    	  if(UtilValidate.isNotEmpty(PeriodBillingAndCustomTimePeriodList)){
	    		  List<GenericValue> customTimePeriodIdList = null;
	    		  List<String> customTimePeriodIds = EntityUtil.getFieldListFromEntityList(PeriodBillingAndCustomTimePeriodList, "customTimePeriodId", false);
	    		  Map customTimePeriodTotalsMap = FastMap.newInstance();
	    		  for(String customTimePeriodId : customTimePeriodIds){
	    			  customTimePeriodIdList = EntityUtil.filterByCondition(PeriodBillingAndCustomTimePeriodList,EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
	    			  Map periodBillingMap = FastMap.newInstance();
	    			  Map periodBillingTotalsMap = FastMap.newInstance();
		    		  for(GenericValue periodBilling : customTimePeriodIdList){
		    			    String periodBillingId = periodBilling.getString("periodBillingId");
		  					List payHeadCondList = FastList.newInstance();
		  					payHeadCondList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
		  					payHeadCondList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		  					EntityCondition payHeadCond = EntityCondition.makeCondition(payHeadCondList,EntityOperator.AND);
		  					List<GenericValue> payrollHeaderAndHeaderItemList = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCond, null, null, null, false);
		  					Map payHeadTotalsMap = FastMap.newInstance();
		  					if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemList)){
		  						for(GenericValue payrollHeaderAndHeaderItem : payrollHeaderAndHeaderItemList){
		  							String payrollHeaderItemTypeId = payrollHeaderAndHeaderItem.getString("payrollHeaderItemTypeId");
		  							BigDecimal amount = payrollHeaderAndHeaderItem.getBigDecimal("amount");
		  							if(payHeadTotalsMap.containsKey(payrollHeaderItemTypeId)){
		  								BigDecimal existAmount = (BigDecimal) payHeadTotalsMap.get(payrollHeaderItemTypeId);
		  								BigDecimal totalAmount = existAmount.add(amount);
		  								payHeadTotalsMap.put(payrollHeaderItemTypeId,totalAmount);
		  							}else{
		  								payHeadTotalsMap.put(payrollHeaderItemTypeId,amount);
		  							}
		  							if(periodBillingTotalsMap.containsKey(payrollHeaderItemTypeId)){
		  								BigDecimal existAmount = (BigDecimal) periodBillingTotalsMap.get(payrollHeaderItemTypeId);
		  								BigDecimal totalAmount = existAmount.add(amount);
		  								periodBillingTotalsMap.put(payrollHeaderItemTypeId,totalAmount);
		  							}else{
		  								periodBillingTotalsMap.put(payrollHeaderItemTypeId,amount);
		  							}
		  							if(customTimePeriodTotalsMap.containsKey(payrollHeaderItemTypeId)){
		  								BigDecimal existAmount = (BigDecimal) customTimePeriodTotalsMap.get(payrollHeaderItemTypeId);
		  								BigDecimal totalAmount = existAmount.add(amount);
		  								customTimePeriodTotalsMap.put(payrollHeaderItemTypeId,totalAmount);
		  							}else{
		  								customTimePeriodTotalsMap.put(payrollHeaderItemTypeId,amount);
		  							}
		  						}
		  					}
		  					periodBillingMap.put(periodBillingId, payHeadTotalsMap);
		  					periodBillingMap.put("periodTotals",periodBillingTotalsMap);
		    		  }
		    		  customTimePeriodMap.put(customTimePeriodId,periodBillingMap);
		    		  customTimePeriodMap.put("customTimePeriodTotals",customTimePeriodTotalsMap);
	    		  }
	    	  }	
	      }catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
	    result.put("periodTotalsForParty",customTimePeriodMap);
		return result;
	}
	 
}//end of service

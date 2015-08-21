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
	package in.vasista.vbiz.milkReceipts;
	


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.sql.Date;
import java.sql.Timestamp;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.fortuna.ical4j.util.Strings;

import org.apache.tools.ant.filters.TokenFilter.ContainsString;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.network.LmsServices;
import org.ofbiz.product.product.ProductEvents;

import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptsTransporterServices;
import in.vasista.vbiz.procurement.ProcurementReports;
	
 	public class MilkReceiptsSLTransporterServices {
		
		public static final String module = MilkReceiptsSLTransporterServices.class.getName();

		    
		  public static Map<String, Object>  populateSLPtcPeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context)  {
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String periodBillingId = null;
				String customTimePeriodId = (String) context.get("customTimePeriodId");
				String milkToPartyId = (String) context.get("milkToPartyId");
				String billingTypeId = "";
				if(UtilValidate.isNotEmpty(milkToPartyId)){
					billingTypeId = "SL_PB_PTC_TRSPT_MRGN";
				}
				
				List conditionList = FastList.newInstance();
		        List periodBillingList = FastList.newInstance();
		        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS","APPROVED","APPROVED_PAYMENT")));
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
		        newEntity.set("facilityId", milkToPartyId);
		        newEntity.set("statusId", "IN_PROCESS");
		        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
		        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
		        newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			    try {     
			        delegator.createSetNextSeqId(newEntity);
					periodBillingId = (String) newEntity.get("periodBillingId");	
					Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"billingTypeId", billingTypeId, "customTimePeriodId", customTimePeriodId,"userLogin", userLogin,"milkToPartyId",milkToPartyId);
					Map resultMap = dispatcher.runSync("generateSLPtcTranporterMargin", runSACOContext);
					if(ServiceUtil.isError(resultMap)){
						delegator.removeValue(newEntity);
						Debug.logError("Failed genrate Billing ::"+resultMap, module);
						return ServiceUtil.returnError("Failed genrate Billing ::"+ServiceUtil.getErrorMessage(resultMap));
					}
		    	} catch (GenericEntityException e) {
					Debug.logError(e,"Failed To Create New Period_Billing", module);
					e.printStackTrace();
				}
		        catch (GenericServiceException e) {
		            Debug.logError(e, "Error in calling 'generatePtcMargin' service", module);
		            return ServiceUtil.returnError(e.getMessage());
		        } 
			    catch (Exception e) {
		            Debug.logError(e, "Error in calling 'generatePtcMargin' service", module);
		            return ServiceUtil.returnError(e.getMessage());
		        } 
			    
		        result.put("periodBillingId", periodBillingId);
		        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		    	return result;
		    }
		 
		  
		  public static Map<String, Object> generateSLPtcTranporterMargin(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericServiceException {
				Delegator delegator = dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();
				TimeZone timeZone = TimeZone.getDefault();
				List masterList = FastList.newInstance();
				Locale locale = Locale.getDefault();
				List conditionList= FastList.newInstance(); 
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String customTimePeriodId = (String) context.get("customTimePeriodId");
				String periodBillingId = (String) context.get("periodBillingId");
				String billingTypeId = (String) context.get("billingTypeId");
				String milkToPartyId = (String) context.get("milkToPartyId");
				
				RoundingMode rounding = RoundingMode.HALF_UP;
				Map resultValue = ServiceUtil.returnSuccess();
				boolean generationFailed = false;		
				
				GenericValue periodBilling = null;
				try{
					try {
						periodBilling =delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
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
					
					Map masterResultMap = FastMap.newInstance(); 
					try{
						Map inMap = FastMap.newInstance();
						inMap.put("fromDate", monthBegin);
						inMap.put("thruDate", monthEnd);
						inMap.put("userLogin", userLogin);
						inMap.put("customTimePeriodId",customTimePeriodId);
						inMap.put("milkToPartyId",milkToPartyId);
						masterResultMap = getSLPtcMasterList(dctx,inMap);
						if(ServiceUtil.isError(masterResultMap)){
							Debug.logError("Error while Preparing masterList ::"+masterResultMap,module);
							return ServiceUtil.returnError("Error while Preparing masterLis ::"+ServiceUtil.getErrorMessage(masterResultMap));
						}
						masterList.addAll((List)masterResultMap.get("masterList"));
						if(UtilValidate.isEmpty(masterList)){
							Debug.logError("No Order to process for the given period",module);
							return ServiceUtil.returnError("No Order to process for the given period ::");
						}
					}catch(Exception e){
						Debug.logError("Error while getting tanker wise details : "+e,module);
						return ServiceUtil.returnError("Error while getting tanker wise details : "+e);
					}
					try{
						result =MilkReceiptsSLTransporterServices.populateSLPtcCommissiions(dctx, context, masterList,periodBillingId);
						if (ServiceUtil.isError(result)) {
				    		generationFailed = true;
			                Debug.logWarning("There was an error while creating  the TransporterCommission: " + ServiceUtil.getErrorMessage(result), module);
			        		return ServiceUtil.returnError("There was an error while creating the TransporterCommission: " + ServiceUtil.getErrorMessage(result));          	            
			            } 
						
						
					}catch(Exception e){
						Debug.logError("Error while generating transporter bill ."+e,module);
						return ServiceUtil.returnError("Error while generating transporter bill .");
					}
					
					/*Map<String, Object> updateRecvoryRes=MilkReceiptsTransporterServices.updateFineRecvoryWithBilling(dctx , UtilMisc.toMap("customTimePeriodId", customTimePeriodId,"periodBillingId",periodBillingId,"userLogin",userLogin));
					if (ServiceUtil.isError(updateRecvoryRes)) {
			    		generationFailed = true;
		                Debug.logWarning("There was an error while populating updateRecvory: " + ServiceUtil.getErrorMessage(updateRecvoryRes), module);
		        		return ServiceUtil.returnError("There was an error while populating updateRecvory: " + ServiceUtil.getErrorMessage(updateRecvoryRes));          	            
		            } */
					
					
					if (generationFailed) {
						periodBilling.set("statusId", "GENERATION_FAIL");
					} else {
						periodBilling.set("statusId", "GENERATED");
						periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					}
					periodBilling.store();	
					
			}catch (GenericEntityException e) {
					Debug.logError(e, module);
			}
				return resultValue;		
		}
			
		public static Map<String, Object>  getSLPtcMasterList(DispatchContext dctx, Map<String, ? extends Object> context)  {
			
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			List<GenericValue> milkTransferList = FastList.newInstance();
			List<GenericValue> shipmentList = FastList.newInstance();
			List masterList = FastList.newInstance();
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			Timestamp thruDate = (Timestamp)context.get("thruDate");
			String customTimePeriodId = (String)context.get("customTimePeriodId");
			String facilityId = (String)context.get("milkToPartyId");
			
			
			if((UtilValidate.isEmpty(fromDate)|| UtilValidate.isEmpty(thruDate))&&(UtilValidate.isEmpty(customTimePeriodId))){
				Debug.logError("fromDate or thruDate or TimePeriod is Empty ",module);
				return ServiceUtil.returnError("fromDate or thruDate or TimePeriod is Empty");
			}
			
			Timestamp dayBegin =  UtilDateTime.getDayStart(fromDate);
     		Timestamp dayEnd =  UtilDateTime.getDayEnd(thruDate);
			
			Map unitAgentsMap = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", facilityId));	    			
			List tempAgents = (List)unitAgentsMap.get("facilityIds");
			   
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,tempAgents));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> vehicleRoleList = FastList.newInstance();
			try{
				vehicleRoleList = delegator.findList("VehicleRole", condition, null, null, null,false);
			}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
			} 
			
			List<String> vehicleIds = EntityUtil.getFieldListFromEntityList(vehicleRoleList,"vehicleId", true);
			
			for(int i=0; i<vehicleIds.size(); i++){
				
				String vehicleId = (String)vehicleIds.get(i);
				List<GenericValue> filteredVehicleRoleList = EntityUtil.filterByCondition(vehicleRoleList,EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
				
				List<String> facilityIds = EntityUtil.getFieldListFromEntityList(filteredVehicleRoleList,"facilityId", true);
				String partyId = (String)(EntityUtil.getFirst(filteredVehicleRoleList)).get("partyId");
				
				
				BigDecimal quantityProcTot = BigDecimal.ZERO;
				for(int j=0; j< facilityIds.size(); j++){
									
					String eachFacilityId = (String) facilityIds.get(j);
					BigDecimal quantityProc = BigDecimal.ZERO;
					
					Map<String,Object> procurementPeriodTotals = FastMap.newInstance();
					procurementPeriodTotals = ProcurementReports.getPeriodTotals(dctx , UtilMisc.toMap("fromDate", dayBegin, "thruDate", dayEnd, "facilityId", eachFacilityId,"userLogin",userLogin));
					
					if(!procurementPeriodTotals.isEmpty()){
						Map tempDayTotalsMap = (Map)((Map)procurementPeriodTotals.get(eachFacilityId)).get("dayTotals");
						if(!tempDayTotalsMap.isEmpty()){
							quantityProc = (BigDecimal)((Map)((Map)((Map)tempDayTotalsMap.get("TOT")).get("TOT")).get("TOT")).get("qtyLtrs");
						}
					}
					quantityProcTot = quantityProcTot.add(quantityProc);
				
				}
				
				BigDecimal amount = BigDecimal.ZERO;
				
				Map rateMap = FastMap.newInstance();
				rateMap.put("userLogin",userLogin);
				rateMap.put("vehicleId",vehicleId);
				rateMap.put("fromDate",fromDate);
				
				Map rateResultMap = MilkReceiptsSLTransporterServices.getVehicleRate(dctx, rateMap);
				if(ServiceUtil.isError(rateResultMap)){
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rateResultMap));
				}
				if(ServiceUtil.isSuccess(rateResultMap)){
					amount = amount.add((BigDecimal)rateResultMap.get("amount"));
				}
				amount = amount.multiply(quantityProcTot);
				
				Map masterMap = FastMap.newInstance();
				masterMap.put("partyId",partyId);
				masterMap.put("quantity",quantityProcTot);
				masterMap.put("amount",amount);
				masterMap.put("vehicleId",vehicleId);
				masterList.add(masterMap);
			}
			
			if(UtilValidate.isEmpty(masterList)){
				result= ServiceUtil.returnError("Error while getting masterList");
			}
			result.put("masterList", masterList);
			return result;
		}
		
		public static Map<String, Object>  getVehicleRate(DispatchContext dctx, Map<String, ? extends Object> context)  {
			
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			List<GenericValue> milkTransferList = FastList.newInstance();
			List<GenericValue> shipmentList = FastList.newInstance();
			
			List masterList = FastList.newInstance();
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			String vehicleId = (String)context.get("vehicleId");
			
			Timestamp dayBegin =  UtilDateTime.getDayStart(fromDate);
     		
			List conditionList = FastList.newInstance();
	        List vehicleRateList = FastList.newInstance();
	        conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , vehicleId));
	        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayBegin));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null),EntityOperator.OR,EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin)));
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	try {
	    		vehicleRateList = delegator.findList("VehicleRate", condition, null,null, null, false);
	    		
	    	}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find vehicleRate " + e);
			} 
	    	
	    	BigDecimal amount = BigDecimal.ZERO;
	    	if(UtilValidate.isNotEmpty(vehicleRateList)){
	    		
	    		GenericValue vehicleRate = (GenericValue)(EntityUtil.getFirst(vehicleRateList));
				
	    		amount = (BigDecimal)vehicleRate.get("rateAmount");
	    	}
	    	result.put("amount", amount);
			
	    	return result;
			
		
		}
		
		public static Map<String, Object> updateSLPTCStatus (DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = dctx.getDelegator();
	    	TimeZone timeZone = TimeZone.getDefault();
	        LocalDispatcher dispatcher = dctx.getDispatcher();       
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	Locale locale = (Locale) context.get("locale");
	        Map<String, Object> result = new HashMap<String, Object>();
	        result = ServiceUtil.returnSuccess();
	        String periodBillingId = (String) context.get("periodBillingId");
	        String statusId = (String) context.get("statusId");
	        if(UtilValidate.isEmpty(statusId)){
	        	statusId="GENERATED";
	        }
	        GenericValue periodBilling = null;
	        String customTimePeriodId="";
	        try{
	        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
	        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
	        	  periodBilling.set("statusId", statusId);
				  periodBilling.store();
	        }catch (GenericEntityException e) {
	    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
			}   
	        GenericValue customTimePeriod;
			try {
				customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			} catch (GenericEntityException e1) {
				Debug.logError(e1,"Error While Finding Customtime Period");
				return ServiceUtil.returnError("Error While Finding Customtime Period" + e1);
			}
			if("APPROVED".equalsIgnoreCase(statusId)){
				Map ptcInvoiceResult=createSLPTCInvoice(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
				result.putAll(ptcInvoiceResult);
	        }
			if("APPROVED_PAYMENT".equalsIgnoreCase(statusId)){
				Map ptcPaymentResult=MilkReceiptsTransporterServices.createTransporterPayment(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
				try{
		        	  periodBilling.set("statusId", "APPROVED_PAYMENT");
					  periodBilling.store();
		        }catch (GenericEntityException e) {
		    		Debug.logError("Unable to Make Payment Process For PTC Billing"+e, module);
		    		return ServiceUtil.returnError("Unable to Make Payment Process For PTC Billing! "); 
				}   
				result.putAll(ptcPaymentResult);
		       }
			if("REJECT_PAYMENT".equalsIgnoreCase(statusId)){
				Map ptcPaymentCancelResult=MilkReceiptsTransporterServices.cancelPTCTransporterPayment(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
				try{
		        	  periodBilling.set("statusId", "REJECT_PAYMENT");
					  periodBilling.store();
		        }catch (GenericEntityException e) {
		    		Debug.logError("Unable To Cancel DTC Bill Payment"+e, module);
		    		return ServiceUtil.returnError("Unable To Cancel DTC Bill Payment.. "); 
				}   
				result.putAll(ptcPaymentCancelResult);
		       }
			
			return result;
	    }
		public static Map<String, Object> createSLPTCInvoice (DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = dctx.getDelegator();
	    	TimeZone timeZone = TimeZone.getDefault();
	        LocalDispatcher dispatcher = dctx.getDispatcher();       
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	Locale locale = (Locale) context.get("locale");
	        Map<String, Object> result = new HashMap<String, Object>();
	        String periodBillingId = (String) context.get("periodBillingId");
	        
	        GenericValue periodBilling = null;
	        String customTimePeriodId="";
	        try{
	        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
	        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
	        }catch (GenericEntityException e) {
	    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
			}   
	        GenericValue customTimePeriod;
			try {
				customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			} catch (GenericEntityException e1) {
				Debug.logError(e1,"Error While Finding Customtime Period");
		    	periodBilling.set("statusId", "GENERATION_FAIL");
				try{
		    	periodBilling.store();
				}catch(Exception e){
					Debug.logError("Error while creating PTCINVOICE"+e,module);
					return ServiceUtil.returnError("Error while creating PTCINVOICE"+e.getMessage());
				}
				
				
				return ServiceUtil.returnError("Error While Finding Customtime Period" + e1);
			}
			
			Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
			
			Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
			Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
			
			List conditionList = FastList.newInstance();
			List masterList = FastList.newInstance();
			
			if(UtilValidate.isNotEmpty(periodBillingId)){
	
				conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
	        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	        	try{
	        		List<GenericValue> ptcBillingCommissionList = FastList.newInstance();
	        		ptcBillingCommissionList = delegator.findList("PtcBillingCommission", condition, null,null, null, false);	        		
	            	
	        		if(!UtilValidate.isEmpty(ptcBillingCommissionList)){
	        			
	        			Map masterResultMap = FastMap.newInstance(); 
	        			for(int i=0; i<ptcBillingCommissionList.size(); i++){
	        				
	        				GenericValue ptcBillingCommission = (GenericValue)ptcBillingCommissionList.get(i);
	        				
	        				String partyId = (String)ptcBillingCommission.get("partyId");
	        				BigDecimal amount = (BigDecimal)ptcBillingCommission.get("commissionAmount");
	        				BigDecimal quantity = BigDecimal.ONE;
	        				
	        				Map<String, Object> createInvoiceContext = FastMap.newInstance();
			                createInvoiceContext.put("partyId", "Company");
			                createInvoiceContext.put("partyIdFrom", partyId);
			                createInvoiceContext.put("dueDate", monthEnd);
			                createInvoiceContext.put("invoiceDate", monthEnd);
			                createInvoiceContext.put("invoiceTypeId", "COGS_OUT");
			                createInvoiceContext.put("referenceNumber", "PTC_TRSPT_MRGN_"+periodBillingId);
			                createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
			                createInvoiceContext.put("currencyUomId", "INR");
			                createInvoiceContext.put("userLogin", userLogin);
			                
			                String invoiceId = null;
			                try{
				                Map<String, Object> createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
				                if (ServiceUtil.isError(result)) {
				    	                Debug.logWarning("There was an error while creating  Invoice For TransporterCommission: " + ServiceUtil.getErrorMessage(result), module);
				    	        		return ServiceUtil.returnError("There was an error while creating Invoice for  TransporterCommission: " + ServiceUtil.getErrorMessage(result));          	            
				    	            } 
				                invoiceId = (String) createInvoiceResult.get("invoiceId");
			                }catch(GenericServiceException e){
		    	             	 Debug.logError(e, e.toString(), module);
		    	                 return ServiceUtil.returnError(e.toString());
		    	            }
			                Map<String, Object> resMap = null;
			                try{
			                    resMap = dispatcher.runSync("createInvoiceItem", 
			                    		UtilMisc.toMap("invoiceId", invoiceId,"invoiceItemTypeId", "PTC_MRGN","quantity",quantity,"amount", amount,"userLogin", userLogin));
			                    if (ServiceUtil.isError(result)) {
			    	                Debug.logWarning("There was an error while creating  the InvoiceItem: " + ServiceUtil.getErrorMessage(result), module);
			                    }
		                    }catch(GenericServiceException e){
		    	             	 Debug.logError(e, e.toString(), module);
		    	                 return ServiceUtil.returnError(e.toString());
		    	             }
	        				
		                    Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
		    	             invoiceCtx.put("userLogin", userLogin);
		    	             invoiceCtx.put("statusId","INVOICE_APPROVED");
		    	             try{
		    	             	Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
		    	             	if (ServiceUtil.isError(invoiceResult)) {
		    	             		Debug.logError(invoiceResult.toString(), module);
		    	                     return ServiceUtil.returnError(null, null, null, invoiceResult);
		    	                 }	             	
		    	             }catch(GenericServiceException e){
		    	             	 Debug.logError(e, e.toString(), module);
		    	                 return ServiceUtil.returnError(e.toString());
		    	             }  
		    	             //set to Ready for Posting
		    	             invoiceCtx.put("userLogin", userLogin);
		    	             invoiceCtx.put("statusId","INVOICE_READY");
		    	             try{
		    	             	Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
		    	             	if (ServiceUtil.isError(invoiceResult)) {
		    	             		Debug.logError(invoiceResult.toString(), module);
		    	                     return ServiceUtil.returnError(null, null, null, invoiceResult);
		    	                 }	             	
		    	             }catch(GenericServiceException e){
		    	             	 Debug.logError(e, e.toString(), module);
		    	                 return ServiceUtil.returnError(e.toString());
		    	             }
	        				
	        			}
	        			
	        		}
	        		
	        	}catch(GenericEntityException e){
	        		Debug.logError(e, module);
	        	}
		}
			
		return result;
	}	
		public static Map<String, Object>  populateSLPtcCommissiions(DispatchContext dctx, Map<String, ? extends Object> context, List masterList, String periodBillingId)  {
			
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String billingTypeId = (String) context.get("billingTypeId");			
			
			if(billingTypeId.equals("SL_PB_PTC_TRSPT_MRGN")){	
				for(int i=0; i< masterList.size(); i++){
					
					Map ptcCommissionDetails = FastMap.newInstance();
					ptcCommissionDetails.putAll((Map) masterList.get(i));
					BigDecimal commissionAmount = BigDecimal.ZERO;
					
	            	String vehicleId = (String)ptcCommissionDetails.get("vehicleId");
	            	String partyId = (String)ptcCommissionDetails.get("partyId");
	            	
					try{	
						List conditionList = FastList.newInstance();
				        conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS , periodBillingId));
				        conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS ,vehicleId));
				        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
				    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				    	List<GenericValue> ptcBillingCommissionList = delegator.findList("PtcBillingCommission", condition, null,null, null, false);
				    	
				    	if (UtilValidate.isEmpty(ptcBillingCommissionList)) {
				    		
				    		if(UtilValidate.isNotEmpty(ptcCommissionDetails.get("amount"))){
				    			commissionAmount=(BigDecimal)ptcCommissionDetails.get("amount");
					    	}
				    		
				    		GenericValue ptcCommission = delegator.makeValue("PtcBillingCommission");
				    		ptcCommission.put("periodBillingId", periodBillingId );
				    		ptcCommission.put("vehicleId", vehicleId);
				    		ptcCommission.put("partyId", partyId);
				    		ptcCommission.put("commissionAmount", commissionAmount);
				    		delegator.createSetNextSeqId(ptcCommission);
				    		
				    	}else { 
							Debug.logError("ptc Billing Already Exists", module);
							return ServiceUtil.returnError("ptc Billing Already Exists");
						}
				    	
						
					} catch (GenericEntityException e) {
							Debug.logError("Error While Creating New FacilityCommistion", module);
							return ServiceUtil.returnError("Error While Creating New FacilityCommistion");
					}
				}
			}
	        return result;
	    }	
		
	    
}



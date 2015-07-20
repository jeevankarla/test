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

	
 	public class MilkReceiptsTransporterServices {
		
		public static final String module = MilkReceiptsTransporterServices.class.getName();

		
		
		/*public static void populateTotals(DispatchContext dctx, Map context,
				
			String salesDate, Map<String, Object> salesTotals, String shipmentTypeId  ) {

			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Timestamp sqlTimestamp = null;
			java.sql.Date saleDate = null;
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			try {
				sqlTimestamp = new java.sql.Timestamp(formatter.parse(salesDate).getTime());
			} catch (ParseException e) {
			}
			saleDate = new java.sql.Date(sqlTimestamp.getTime());
			
			String productId = null;
			Object quantity = null;
			Object revenue = null;
			Object productSubscriptionTypeId = null;
			Map productsMap = FastMap.newInstance();
			List productSubscriptionTypeList = new ArrayList();
			List productSubscriptionFieldList = new ArrayList();
			
			try {
				productSubscriptionTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "SUB_PROD_TYPE"), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"), null, false);
				productSubscriptionFieldList = EntityUtil.getFieldListFromEntityList(productSubscriptionTypeList, "enumId", false);
			} catch (GenericEntityException e1) {
				Debug.logError(e1, module);
				e1.printStackTrace();
			}
			HashSet productSubscriptionTypeSet = new HashSet(productSubscriptionFieldList);
			
			productsMap = (Map)salesTotals.get("productTotals");
				Iterator mapIterator = productsMap.entrySet().iterator();
				while (mapIterator.hasNext()) {
					Map.Entry entry = (Entry) mapIterator.next();
					List productsSalesList = new ArrayList();
					productsSalesList.clear();
					productsSalesList.add(entry.getValue());
					Map productsSalesMap = FastMap.newInstance();
					productsSalesMap.clear();
					productsSalesMap = (Map) productsSalesList.get(0);
					productId = (String) entry.getKey();
					Map tempMap = FastMap.newInstance();
					tempMap = (Map) productsSalesMap.get("supplyTypeTotals");
					
					Iterator supplyTypeIter = tempMap.entrySet().iterator(); 
					while (supplyTypeIter.hasNext()) {
						Map.Entry entry2 = (Entry) supplyTypeIter.next();
						Map supplyTypeMap = FastMap.newInstance();
						supplyTypeMap = (Map) (entry2.getValue());
						productSubscriptionTypeId = supplyTypeMap.get("name");
						quantity = supplyTypeMap.get("total");
						revenue = supplyTypeMap.get("totalRevenue");
						
						    try {
					    		GenericValue salesSummary = delegator.findOne("LMSSalesHistorySummaryDetail", UtilMisc.toMap("salesDate", saleDate, "shipmentTypeId", shipmentTypeId, "productSubscriptionTypeId",productSubscriptionTypeId, "productId", productId), false);
					    		if (salesSummary == null) {
					    			salesSummary = delegator.makeValue("LMSSalesHistorySummaryDetail");
					                salesSummary.put("salesDate", saleDate );
					                salesSummary.put("totalQuantity", quantity);
					                salesSummary.put("totalRevenue", revenue); 
					                salesSummary.put("productId", productId);
					                salesSummary.put("productSubscriptionTypeId", productSubscriptionTypeId);
					                salesSummary.put("shipmentTypeId", shipmentTypeId);
					                salesSummary.create();    
					            }
					    		else {  
					    			salesSummary.clear();
				    			    List conditionList= FastList.newInstance(); 
				    	        	conditionList.add(EntityCondition.makeCondition("salesDate", EntityOperator.EQUALS, saleDate));
				    	        	conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, shipmentTypeId));    	
				    	        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				    	        	
				    				List<GenericValue> salesSummaryList = delegator.findList("LMSSalesHistorySummaryDetail", condition, null, null, null, false);
				                    delegator.removeAll(salesSummaryList);
				                	salesSummary = delegator.makeValue("LMSSalesHistorySummaryDetail");
				    			    salesSummary.put("salesDate", saleDate);
				                    salesSummary.put("totalQuantity", quantity);
				                    salesSummary.put("totalRevenue", revenue); 
				                    salesSummary.put("productId", productId);
				                    salesSummary.put("productSubscriptionTypeId", productSubscriptionTypeId); 
				                    salesSummary.put("shipmentTypeId", shipmentTypeId);
				                    salesSummary.create();
					            }
					    	} catch (GenericEntityException e) {
					            Debug.logError(e, module);
					        }
						}
				}
		}*/
		
		 
		
		
		
			
		 public static Map<String, Object> cancelPtcTransporterMarginInvoice(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();       
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        Map<String, Object> result = new HashMap<String, Object>();
		        Locale locale = (Locale) context.get("locale");
		        boolean cancelationFailed = false;	
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
					Debug.logError(e1, e1.getMessage());
					return ServiceUtil.returnError("Error in customTimePeriod" + e1);
				}
				Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
				Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
				
				Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, TimeZone.getDefault(), locale);
				Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, TimeZone.getDefault(), locale);
				
				if(customTimePeriod == null){
					cancelationFailed = true;
				}
				
			try{
				List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
				  conditionList.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS,  "PTC_TRSPT_MRGN_"+periodBillingId));
				  
			     conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PTC_TRNSPORTOR_INV"));
			     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
			     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
	             conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED")); 
	            
	        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
	        	List<GenericValue> invoiceRows = delegator.findList("Invoice", condition, null, null, null, false);
		        List invoiceIdsList = EntityUtil.getFieldListFromEntityList(invoiceRows, "invoiceId", false);
		        Debug.logInfo("==invoiceIdsList======Before==Cacnelation==="+invoiceIdsList,module);
		        	//cancel mass invoice
		        	Map<String, Object> cancelInvoiceInput = UtilMisc.<String, Object>toMap("invoiceIds",invoiceIdsList);
	         	    cancelInvoiceInput.put("userLogin", userLogin);
	                cancelInvoiceInput.put("statusId", "INVOICE_CANCELLED");
	             	Map<String, Object> invoiceResult = dispatcher.runSync("massChangeInvoiceStatus",cancelInvoiceInput);
	             	if (ServiceUtil.isError(invoiceResult)) {
	             		Debug.logError("There was an error while cancel Invoice: " + ServiceUtil.getErrorMessage(invoiceResult), module);
	             		  try{
	          	        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
	          	        }catch (GenericEntityException e) {
	          	    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	          	    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
	          			}     
	             		periodBilling.set("statusId", "CANCEL_FAILED");
	    		    	try{
	    		    		periodBilling.store();    		
	    		    	}catch (Exception e) {
	    		    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
	    		    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
	    				}
	    		    	return ServiceUtil.returnError("There was an error while cancel Invoice:");
	                 }	
	             	
		        	
		        	if (cancelationFailed) {
						periodBilling.set("statusId", "CANCEL_FAILED");
					} else {
						periodBilling.set("statusId", "COM_CANCELLED");
						periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					}
					periodBilling.store();	
		        
		        }catch(GenericServiceException e){
			        	Debug.logError("Unable to Cancel transporter margin"+e, module);
			    		return ServiceUtil.returnError("Unable to Cancel transporter margin "); 
			    }catch(GenericEntityException e){
		        	Debug.logError("Unable to Cancel transporter margin"+e, module);
		    		return ServiceUtil.returnError("Unable to Cancel transporter margin "); 
		        }
		        return result;
		    }
		    
		 public static Map<String, Object> createPtcTransporterRecovery(DispatchContext dctx, Map context) {
		    	Map<String, Object> result = ServiceUtil.returnSuccess();
		    	String vehicleId = (String) context.get("vehicleId");
		    	String customTimePeriodId = (String) context.get("customTimePeriodId");
		    	String recoveryTypeId = (String)context.get("recoveryTypeId");
		    	Timestamp incidentDate = (Timestamp)context.get("incidentDate");
		    	BigDecimal amount = (BigDecimal)context.get("amount");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String description=(String)context.get("description");
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				List conditionList=FastList.newInstance();
				try {
	                conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_PTC_TRSPT_MRGN"));
	                conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	                conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        		List<GenericValue> periodBillingList = FastList.newInstance();
		        		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);	 
		        		if(UtilValidate.isNotEmpty(periodBillingList)){
		        			 Debug.logError("Billing Is Already Generated For This Period", module);
		        			 return ServiceUtil.returnError("Billing Is Already Generated For This Period  and You Can Not Create Additions or Deductions!");   
		        		}
					
		        	conditionList.clear();
		        	conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS, vehicleId));
		        	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		        	conditionList.add(EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS, recoveryTypeId));
		        	if(UtilValidate.isNotEmpty(incidentDate)){
		        		conditionList.add(EntityCondition.makeCondition("incidentDate", EntityOperator.EQUALS, incidentDate));
		        	}
		        	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        	
		        	List<GenericValue> recoveryList = delegator.findList("FineRecovery", condition, null,null, null, false);
		        	
		        	if(UtilValidate.isNotEmpty(recoveryList)){
		        		Debug.logError("Recovery exists for the same date",module);
		        		return ServiceUtil.returnError("Recovery exists for the same date");
		        	}
		        		
		        	GenericValue fineRecovery = delegator.makeValue("FineRecovery");
					fineRecovery.put("vehicleId", vehicleId );
					fineRecovery.put("customTimePeriodId", customTimePeriodId);
					fineRecovery.put("incidentDate", incidentDate);
					fineRecovery.put("recoveryTypeId", recoveryTypeId); 
					fineRecovery.put("amount", amount);
	    			fineRecovery.put("description", description);
	    			fineRecovery.put("createdDate", UtilDateTime.nowTimestamp());
	    			fineRecovery.put("lastModifiedDate", UtilDateTime.nowTimestamp());
	    			fineRecovery.put("createdByUserLogin", userLogin.get("userLoginId"));
	    			delegator.createSetNextSeqId(fineRecovery);            
	    			String recoveryId = (String) fineRecovery.get("recoveryId");
					
		        }catch(GenericEntityException e){
					Debug.logError("Error while creating Transporter Recovery"+e.getMessage(), module);
				}
		      //  result = ServiceUtil.returnSuccess("Transporter amount Created Sucessfully");
		       /* result.put("createdDate",UtilDateTime.nowTimestamp());
		        result.put("incidentDate",incidentDate);*/
		        return ServiceUtil.returnSuccess("Transporter amount Created Sucessfully");
		    }// end of the service
		    
		 
		 public static Map<String, Object> getPtcTransporterMarginDuesForPeriodBilling(DispatchContext dctx, Map<String, Object> context) {
				List conditionList= FastList.newInstance(); 
				LocalDispatcher dispatcher = dctx.getDispatcher();
		        Delegator delegator = dctx.getDelegator();
		        Map<String, Object> result = ServiceUtil.returnSuccess();
				String periodBillingId = (String) context.get("periodBillingId");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Map<String, Object> routeTradingMap = new HashMap<String, Object>();
	        	String routeId = null;
				if(!UtilValidate.isEmpty(periodBillingId)){
					conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        	try{
		        		List<GenericValue> dayWiseRouteMargins = FastList.newInstance();
		        		dayWiseRouteMargins = delegator.findList("PtcBillingCommission", condition, null,null, null, false);	        		
	                	if(!UtilValidate.isEmpty(dayWiseRouteMargins)){
	                		for(GenericValue eachRoute : dayWiseRouteMargins){
	                			BigDecimal totalMargin = BigDecimal.ZERO;  
	                			BigDecimal totalDue = BigDecimal.ZERO;
	                			Map<String, Object> marginDuesMap = FastMap.newInstance();
	                			BigDecimal totalMarginAmount = BigDecimal.ZERO; 
	                			BigDecimal totalDueAmount = BigDecimal.ZERO;
	                			routeId = (String)eachRoute.get("facilityId");
	                			if(!UtilValidate.isEmpty(eachRoute.get("totalAmount"))){
	                				totalMargin = (BigDecimal)eachRoute.get("totalAmount");
	                			}    
	                			if(!UtilValidate.isEmpty(eachRoute.get("dues"))){
	                				totalDue = (BigDecimal)eachRoute.get("dues");
	                			}                			
	                			if(routeTradingMap.containsKey(routeId)){
	                				BigDecimal margin = BigDecimal.ZERO; 
	                				BigDecimal due = BigDecimal.ZERO;
	                				marginDuesMap = (Map)routeTradingMap.get(routeId);
	                				margin = (BigDecimal)marginDuesMap.get("totalMargin");
	                				due = (BigDecimal)marginDuesMap.get("totalDue");
	                				totalMarginAmount = margin.add(totalMargin);
	                				totalDueAmount = due.add(totalDue);
	                				marginDuesMap.put("totalMargin",totalMarginAmount); 
	                				marginDuesMap.put("totalDue",totalDueAmount);
	                				routeTradingMap.put(routeId,marginDuesMap);              				
	                			}
	                			else{
	                				marginDuesMap.put("totalMargin",totalMargin); 
	                				marginDuesMap.put("totalDue", totalDue);
	                				routeTradingMap.put(routeId, marginDuesMap);
	                			}
	                		}
	                	}
		        	}catch(GenericEntityException e){
		        		Debug.logError(e, module);
		        	}
				}			
				result.put("routeTradingMap",routeTradingMap);
	        	return result;
			}
		 
		 public static Map<String, Object> updatePTCStatus (DispatchContext dctx, Map<String, ? extends Object> context) {
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
				Map ptcInvoiceResult=createPTCInvoice(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
				result.putAll(ptcInvoiceResult);
		       }
				if("APPROVED_PAYMENT".equalsIgnoreCase(statusId)){
					Map ptcPaymentResult=createTransporterPayment(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
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
					Map ptcPaymentCancelResult=cancelPTCTransporterPayment(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
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
		 
		 public static Map<String, Object> createTransporterPayment(DispatchContext dctx, Map<String, ? extends Object> context) {
				Delegator delegator = dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				 GenericValue userLogin = (GenericValue) context.get("userLogin");
				 String periodBillingId = (String) context.get("periodBillingId");
				Map<String, Object> result = ServiceUtil.returnSuccess();
				
				List<String> billingInvoiceIdsList=(List<String>)getPTCBillingInvoices(dctx, UtilMisc.toMap("periodBillingId", periodBillingId,"userLogin", userLogin)).get("invoiceIdsList");
				boolean useFifo = Boolean.FALSE;
				if (UtilValidate.isNotEmpty(context.get("useFifo"))) {
					useFifo = (Boolean) context.get("useFifo");
				}
				Locale locale = (Locale) context.get("locale");
				String paymentMethodType = (String) context.get("paymentMethodTypeId");
				
				String facilityId = (String) context.get("facilityId");
			
				String orderId = (String) context.get("orderId");
				BigDecimal paymentAmount = ProductEvents.parseBigDecimalForEntity((String) context.get("amount"));
				String paymentRef = "PTC_TRSPT_MRGN_"+periodBillingId;
				String paymentId = "";
				boolean roundingAdjustmentFlag = Boolean.TRUE;
				List exprListForParameters = FastList.newInstance();
				List boothOrdersList = FastList.newInstance();
				Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
				Timestamp instrumentDate = UtilDateTime.nowTimestamp();
				
				Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", "EXPENSE_PAYOUT");
				paymentCtx.put("paymentMethodTypeId", "CHEQUE_PAYIN");
				paymentCtx.put("paymentMethodId", "");
				paymentCtx.put("partyId","Company");	
			 try { 
				for(String invoiceId:billingInvoiceIdsList){
						
					Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("userLogin", userLogin, "invoiceId",invoiceId));
					if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
						Debug.logError(getInvoicePaymentInfoListResult.toString(),module);
						return ServiceUtil.returnError(null, null, null,getInvoicePaymentInfoListResult);
					}
					Map invoicePaymentInfo = (Map) ((List) getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
					BigDecimal outStandingAmount = (BigDecimal) invoicePaymentInfo.get("outstandingAmount");
					  
					if(UtilValidate.isNotEmpty(invoicePaymentInfo)){
						 if(outStandingAmount.compareTo(BigDecimal.ZERO)>0){
						 GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId",invoiceId), false);
						           // paymentCtx.put("partyIdFrom","Company");
						            paymentCtx.put("organizationPartyId",invoice.getString("partyIdFrom"));
						            paymentCtx.put("facilityId", invoice.getString("facilityId"));
						            paymentCtx.put("paymentPurposeType", "");
						            paymentCtx.put("paymentRefNum", paymentRef); 
						            paymentCtx.put("instrumentDate", invoice.getTimestamp("dueDate"));
									paymentCtx.put("paymentDate", invoice.getTimestamp("dueDate"));
									paymentCtx.put("effectiveDate", invoice.getTimestamp("dueDate"));
						            //paymentCtx.put("statusId", "PMNT_RECEIVED");
						            paymentCtx.put("statusId", "PMNT_NOT_PAID");
						            paymentCtx.put("isEnableAcctg", "Y");
						            paymentCtx.put("amount", outStandingAmount);
						            paymentCtx.put("userLogin", userLogin); 
						            paymentCtx.put("invoices", UtilMisc.toList(invoiceId));
						    		try{
						            Map<String, Object> paymentResult = dispatcher.runSync("createPaymentAndApplicationForInvoices", paymentCtx);
						            if (ServiceUtil.isError(paymentResult)) {
						            	Debug.logError(paymentResult.toString(), module);
						                return ServiceUtil.returnError(null, null, null, paymentResult);
						            }
						            paymentId = (String)paymentResult.get("paymentId");
						            }catch (Exception e) {
						            Debug.logError(e, e.toString(), module);
						            return ServiceUtil.returnError(e.toString());
							        }
						 }
					 }
				}
			 }catch (GenericEntityException e) {
					Debug.logError("Error while Creating Payment for DTC"+ e.getMessage(), module);
					return ServiceUtil.returnError("Error while Creating Payment for DTC");
				} catch (GenericServiceException e) {
					Debug.logError("Error while Creating Payment for DTC" + e.getMessage(),module);
					return ServiceUtil.returnError("Error while Creating Payment for DTC");
				}
				result = ServiceUtil.returnSuccess("Payment successfully done for This Billing ..!");
				return result;
			}//end of the service
		 
		 
		 /**
		  * 
		  * @param dctx
		  * @param context
		  * @return
		  */
		 public static Map<String, Object> makeTransporterMarginPaymentAdjustments(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();
				String periodBillingId = (String) context.get("periodBillingId");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String userLoginId = (String)userLogin.get("userLoginId");
				String routeId = null;
				BigDecimal marginAmount = BigDecimal.ZERO;
				BigDecimal marginAmt = BigDecimal.ZERO;
				BigDecimal dueAmount = BigDecimal.ZERO;
				BigDecimal payAmount = BigDecimal.ZERO;
				RoundingMode rounding = RoundingMode.HALF_UP;
				Map<String, Object> paymentMap = FastMap.newInstance();
				boolean adjustmentFailed = false;
				Map<String, Object> routeTradingMap = FastMap.newInstance();
				List<GenericValue> dayWiseBoothDues = FastList.newInstance();
				Map<String, Object> duesMap = FastMap.newInstance();
				Map<String, Object> routeMargin = FastMap.newInstance();
				Map marginDuesMap = FastMap.newInstance();
				GenericValue periodBilling = null;
		        Map<String, Object> inMap = FastMap.newInstance();
				inMap.put("periodBillingId",periodBillingId);
				inMap.put("userLogin",userLogin);
				try{
					routeTradingMap = dispatcher.runSync("getPtcTransporterMarginDuesForPeriodBilling", inMap);				
					if(ServiceUtil.isError(routeTradingMap)){
						Debug.logError("Unable to fetch route margins and dues", module);
						return ServiceUtil.returnError("Unable to fetch route margins and dues");
					}
					routeMargin = (Map)routeTradingMap.get("routeTradingMap");	
				}catch(GenericServiceException e){
					Debug.logError(e, module);
				}
				GenericValue customTimePeriod;
				try{
	    	       	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);   
	    	       	if(UtilValidate.isEmpty(periodBilling)){
		        		return ServiceUtil.returnError("Error in getting periodBilling record from Database "); 
		        	}	       
		        	for(Map.Entry<String, Object> entry : routeMargin.entrySet()){
	    	       		Map<String, Object> paymentInputMap = FastMap.newInstance();
	    	       		routeId = (String)entry.getKey();
	    	       		marginDuesMap = (Map)entry.getValue();
	    	       		marginAmount = ((BigDecimal)marginDuesMap.get("totalMargin")).setScale(0,rounding);  
	    	       		dueAmount = (BigDecimal)marginDuesMap.get("totalDue");
	    	       		if(marginAmount.compareTo(BigDecimal.ZERO) == 0 || dueAmount.compareTo(BigDecimal.ZERO) == 0){
	    	       			continue;
	    	       		}
	    	       		else{
	    	       				if(marginAmount != null && dueAmount !=null){  	       				
			    	       			if(marginAmount.compareTo(dueAmount) > 0){
			        	       			payAmount = dueAmount;
			        	       		}
			        	       		else if(marginAmount.compareTo(dueAmount) < 0){
			        	       			payAmount = marginAmount;
			        	       		}
			        	       		else{
			        	       			payAmount = dueAmount;
			        	       		}	    	       			
			    	       			if(payAmount.compareTo(BigDecimal.ZERO) > 0){		    	       					
			        	       			paymentInputMap.put("facilityId",routeId);
			        	       			paymentInputMap.put("amount",payAmount);  
			        	       			paymentInputMap.put("userLogin", userLogin);
			        	       			paymentInputMap.put("periodBillingId", periodBillingId);
			        	       			try{
			        	       				paymentMap = dispatcher.runSync("createTransporterMarginDuePayment", paymentInputMap);		        	       						        	       				
			        	       				if(ServiceUtil.isError(paymentMap)){
			        	       					adjustmentFailed = true;
			        	       					periodBilling.set("statusId", "ADJUSTMENT_FAIL");
			        	       					periodBilling.store();
			        	       					return ServiceUtil.returnError("Adjustment Failed!");
			        	       				}
			        	       			}catch(GenericServiceException e){
			        	       				Debug.logError(e, module);
			        	       				adjustmentFailed = true;
			        	       			}
			    	       			}
	    	       				}
	    	       			}
	    	       		}
	    	       	if (!adjustmentFailed) {
	    	    		periodBilling.set("statusId", "ADJUSTED");
	    	    		periodBilling.set("lastModifiedByUserLogin",userLoginId);
	        	       	periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
	    	    	}else{
	    	       		periodBilling.set("statusId", "ADJUSTMENT_FAIL");   					
	    	       	}
	    	       	periodBilling.store();
	    	    }catch (GenericEntityException e) {
	    	    	Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	    	    	return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
	    		}
	        	result = ServiceUtil.returnSuccess("Margins Adjusted Successfully");
	        	return result;
			}
		 
		   	    
		    
		 public static Map<String, Object> getPTCBillingInvoices(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();       
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        Map<String, Object> result = new HashMap<String, Object>();
		        Locale locale = (Locale) context.get("locale");
		        boolean cancelationFailed = false;	
		        String periodBillingId = (String) context.get("periodBillingId");
		        List invoiceIdsList=FastList.newInstance();
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
					Debug.logError(e1, e1.getMessage());
					return ServiceUtil.returnError("Error in customTimePeriod" + e1);
				}
				Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
				Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
				
				Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, TimeZone.getDefault(), locale);
				Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, TimeZone.getDefault(), locale);
				
				try{
					List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
					  conditionList.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS,  "PTC_TRSPT_MRGN_"+periodBillingId));
					  
				     conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PTC_TRNSPORTOR_INV"));
				     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
				     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
		             conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED")); 
		            
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
		        	List<GenericValue> invoiceRows = delegator.findList("Invoice", condition, null, null, null, false);
			        invoiceIdsList = EntityUtil.getFieldListFromEntityList(invoiceRows, "invoiceId", false);
			        
				  }catch(GenericEntityException e){
			        	Debug.logError("Unable to get PTC invoices"+e, module);
			    		return ServiceUtil.returnError("Unable to get PTC invoices "); 
			      }
			     result.put("invoiceIdsList", invoiceIdsList);
		        return result;
		    }
		    
		        
		   
		    
		    
		    
		  /**
		   * 
		   *   
		   * @param dctx
		   * @param context
		   * @return
		   */
		    
		  public static Map<String, Object>  populatePtcPeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context)  {
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String periodBillingId = null;
				String customTimePeriodId = (String) context.get("customTimePeriodId");
				String billingTypeId =  "PB_PTC_TRSPT_MRGN";
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
		        newEntity.set("statusId", "IN_PROCESS");
		        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
		        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
		        newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			    try {     
			        delegator.createSetNextSeqId(newEntity);
					periodBillingId = (String) newEntity.get("periodBillingId");	
					Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"billingTypeId", billingTypeId, "customTimePeriodId", customTimePeriodId,"userLogin", userLogin);
					Map resultMap = dispatcher.runSync("generatePtcTranporterMargin", runSACOContext);
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
		 
		  public static Map<String, Object>  populatePtcCommissiions(DispatchContext dctx, Map<String, ? extends Object> context, List masterList, String periodBillingId)  {
				
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String billingTypeId = (String) context.get("billingTypeId");			
						
				if(billingTypeId.equals("PB_PTC_TRSPT_MRGN")){	
					for(int i=0; i< masterList.size(); i++){
						Map ptcCommissionDetails = FastMap.newInstance();
						ptcCommissionDetails.putAll((Map) masterList.get(i));
						
		            	 String milkTransferId = (String)ptcCommissionDetails.get("milkTransferId");
						try{	
							GenericValue ptcCommission = delegator.findOne("PtcBillingCommission", 
									UtilMisc.toMap("periodBillingId", periodBillingId, "milkTransferId", milkTransferId), false);
							if (ptcCommission == null) {
					    		BigDecimal dayTotQtyKgs = BigDecimal.ZERO;
					    		BigDecimal dayTotQtyLtrs = BigDecimal.ZERO;
					    		BigDecimal commissionAmount = BigDecimal.ZERO;
					    		
					    		if(UtilValidate.isNotEmpty(ptcCommissionDetails.get("amount"))){
					    			commissionAmount=(BigDecimal)ptcCommissionDetails.get("amount");
						    	}
					    		
					    		ptcCommission = delegator.makeValue("PtcBillingCommission");
					    		ptcCommission.put("periodBillingId", periodBillingId );
					    		ptcCommission.put("milkTransferId", milkTransferId);
					    		ptcCommission.put("commissionAmount", commissionAmount);
					    		ptcCommission.create();    
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
		    
		  public static Map<String, Object> generatePtcTranporterMargin(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericServiceException {
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
					
					//her we need to prepare masterList for party and transporter wise
					Map masterResultMap = FastMap.newInstance(); 
					try{
						Map inMap = FastMap.newInstance();
						inMap.put("fromDate", monthBegin);
						inMap.put("thruDate", monthEnd);
						inMap.put("userLogin", userLogin);
						inMap.put("customTimePeriodId",customTimePeriodId);
						
						masterResultMap = getPtcMasterList(dctx,inMap);
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
						result =populatePtcCommissiions(dctx, context, masterList,periodBillingId);
						if (ServiceUtil.isError(result)) {
				    		generationFailed = true;
			                Debug.logWarning("There was an error while creating  the TransporterCommission: " + ServiceUtil.getErrorMessage(result), module);
			        		return ServiceUtil.returnError("There was an error while creating the TransporterCommission: " + ServiceUtil.getErrorMessage(result));          	            
			            } 
						
						
					}catch(Exception e){
						Debug.logError("Error while generating transporter bill ."+e,module);
						return ServiceUtil.returnError("Error while generating transporter bill .");
					}
					
					Map<String, Object> updateRecvoryRes=updateFineRecvoryWithBilling(dctx , UtilMisc.toMap("customTimePeriodId", customTimePeriodId,"periodBillingId",periodBillingId,"userLogin",userLogin));
					if (ServiceUtil.isError(updateRecvoryRes)) {
			    		generationFailed = true;
		                Debug.logWarning("There was an error while populating updateRecvory: " + ServiceUtil.getErrorMessage(updateRecvoryRes), module);
		        		return ServiceUtil.returnError("There was an error while populating updateRecvory: " + ServiceUtil.getErrorMessage(updateRecvoryRes));          	            
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
			}
		return resultValue;		
		}
		/**
		 * 
		 * 
		 * @param dctx
		 * @param context
		 * @return
		 */
			public static Map<String, Object>  getPtcMasterList(DispatchContext dctx, Map<String, ? extends Object> context)  {
				
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				List<GenericValue> milkTransferList = FastList.newInstance();
				List masterList = FastList.newInstance();
				Timestamp fromDate = (Timestamp)context.get("fromDate");
				Timestamp thruDate = (Timestamp)context.get("thruDate");
				String customTimePeriodId = (String)context.get("customTimePeriodId");
				if((UtilValidate.isEmpty(fromDate)|| UtilValidate.isEmpty(thruDate))&&(UtilValidate.isEmpty(customTimePeriodId))){
					Debug.logError("fromDate or thruDate or TimePeriod is Empty ",module);
					return ServiceUtil.returnError("fromDate or thruDate or TimePeriod is Empty");
				}
				
				try{
					List conditionList = UtilMisc.toList(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
					conditionList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
					conditionList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,"MD"));
					conditionList.add(EntityCondition.makeCondition("purposeTypeId",EntityOperator.EQUALS,"INTERNAL"));
					List<String> stausList = UtilMisc.toList("MXF_APPROVED");
					stausList.add("MXF_RECD");
					conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.IN,stausList));
					conditionList.add(EntityCondition.makeCondition("receivedQuantity",EntityOperator.GREATER_THAN,BigDecimal.ZERO));
					EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
					milkTransferList = delegator.findList("MilkTransfer", condition, null, null, null, false);
				}catch(GenericEntityException e){
					Debug.logError("Error While Preparing Master List :"+e,module);
					result= ServiceUtil.returnError("Error while getting masterList");
				}
				if(UtilValidate.isNotEmpty(milkTransferList)){
					List<String> partyKeys = EntityUtil.getFieldListFromEntityList(milkTransferList,"partyId", false);
					Set<String> partyIds = new HashSet(partyKeys);
					for(String partyId : partyIds){
						List<GenericValue> partyWiseTransfers = FastList.newInstance();
						partyWiseTransfers = EntityUtil.filterByCondition(milkTransferList,EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
						if(UtilValidate.isNotEmpty(partyWiseTransfers)){
							List<String> containerIdKeys = EntityUtil.getFieldListFromEntityList(milkTransferList,"containerId", false);
							Set<String> containerIds = new HashSet(containerIdKeys);
							for(String containerId : containerIds){
								List<GenericValue> containerWiseTransfers = FastList.newInstance();
								containerWiseTransfers = EntityUtil.filterByCondition(partyWiseTransfers,EntityCondition.makeCondition("containerId",EntityOperator.EQUALS,containerId));
								Map containerDayWiseTotals = FastMap.newInstance();
								
								for(GenericValue containerWiseTransfer : containerWiseTransfers){
									Map masterMap = FastMap.newInstance();
									String milkTransferId = (String) containerWiseTransfer.get("milkTransferId");
									BigDecimal amount = BigDecimal.ZERO;
									BigDecimal recoveryAmount = BigDecimal.ZERO;
									BigDecimal quantity = (BigDecimal) containerWiseTransfer.get("receivedQuantity");
									BigDecimal quantityLtrs = (BigDecimal) containerWiseTransfer.get("receivedQuantityLtrs");
									Timestamp priceDate = (Timestamp) containerWiseTransfer.get("receiveDate");
									Map rateMap = FastMap.newInstance();
									rateMap.put("userLogin",userLogin);
									rateMap.put("partyId",partyId);
									rateMap.put("vehicleId",containerId);
									rateMap.put("quantityKgs",quantity);
									rateMap.put("quantityLtrs",quantityLtrs);
									rateMap.put("priceDate",priceDate);
									rateMap.put("customTimePeriodId",customTimePeriodId);
									Map rateResultMap = calculateTankerMarginRate(dctx, rateMap);
									if(ServiceUtil.isError(rateResultMap)){
										return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rateResultMap));
									}
									if(ServiceUtil.isSuccess(rateResultMap)){
										amount = amount.add((BigDecimal)rateResultMap.get("amount"));
									}
									masterMap.put("milkTransferId", milkTransferId);
									masterMap.put("amount",amount);
									masterList.add(masterMap);
									
								}
							}
						}
					}
				}
				
				if(UtilValidate.isEmpty(masterList)){
					result= ServiceUtil.returnError("Error while getting masterList");
				}
				result.put("masterList", masterList);
				return result;
			}	
			
			public static Map<String, Object>  calculateTankerMarginRate(DispatchContext dctx, Map<String, ? extends Object> context)  {
				
		    	String unionId = "";
				GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				// this partyId references from where vehicle is coming
				String partyId = (String) context.get("partyId");
				String vehicleId = (String) context.get("vehicleId");
				Timestamp priceDate = (Timestamp)context.get("priceDate");
				BigDecimal quantityKgs = (BigDecimal) context.get("quantityKgs");
				BigDecimal quantityLtrs = (BigDecimal) context.get("quantityLtrs");
				List<GenericValue> partyDetails = null;
				List conditionList = FastList.newInstance();
				Boolean hasRelation = false;
				try{
					List relationShipCondList = FastList.newInstance();
					relationShipCondList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"UNION"));
					relationShipCondList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,partyId));
					EntityCondition relCondtion = EntityCondition.makeCondition(relationShipCondList);
					List<GenericValue> relatedParties = delegator.findList("PartyRelationship", relCondtion, null, null, null, false);
					List filterdParties = EntityUtil.filterByDate(relatedParties, priceDate);
					if(UtilValidate.isNotEmpty(filterdParties)){
						unionId = (String)(EntityUtil.getFirst(filterdParties)).get("partyIdFrom");
						hasRelation = true;
					}
					
				}catch(Exception e){
					Debug.logError("Error while finding relationship with union:"+e,module);
					return ServiceUtil.returnError("Error while finding relationship with union:"+e.getMessage());
				}
				try{
					conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
					conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"UNION"));
					EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
					partyDetails = delegator.findList("PartyRoleAndPartyDetail",condition,null,null,null,false);
					if(UtilValidate.isEmpty(partyDetails)){
						// checking it has any relation ship with union.If it has relation then we need to consider it 
						if(!hasRelation){
							Debug.logError("Relationship with union not found for :"+partyId,module);
							return ServiceUtil.returnError("Relationship with union not found for "+partyId);
						}
						
					}
					
				}catch(GenericEntityException e){
					Debug.logError("Error while getting union details"+e,module);
					return ServiceUtil.returnError("Error while getting union details");
				}
				BigDecimal amount = BigDecimal.ZERO;
				BigDecimal recoveryAmount = BigDecimal.ZERO;
				//here we need to add  calculation logic 
				
				BigDecimal distance = BigDecimal.ZERO;
				BigDecimal capacity = BigDecimal.ZERO;
				BigDecimal rate = BigDecimal.ZERO;
				Map inMap = FastMap.newInstance();
				inMap.put("partyId", partyId);
				inMap.put("userLogin", userLogin);
				inMap.put("fromDate",priceDate);
				try{
					// here we are getting  distance 
					inMap.put("rateTypeId", "DISTANCE_FROM_MD");
					Map<String, Object> serviceResults = dispatcher.runSync("getFacilityPartyRate", inMap);
					distance = (BigDecimal)serviceResults.get("rateAmount");
				}catch(Exception e){
					Debug.logError("Error while getting distance of the union :"+e,module);
					return ServiceUtil.returnError("Error while getting distance of the union :"+partyId);
				}
				try{
					GenericValue vehicleDeails = delegator.findOne("Vehicle", UtilMisc.toMap("vehicleId",vehicleId), false);
					if(UtilValidate.isNotEmpty(vehicleDeails) && UtilValidate.isNotEmpty(vehicleDeails.get("vehicleCapacity"))){
						capacity = (BigDecimal)vehicleDeails.get("vehicleCapacity");
					}
				}catch(Exception e){
					Debug.logError("Error while getting Capacity of vehicle :"+e,module);
					return ServiceUtil.returnError("Error while getting Capacity of vehicle :"+vehicleId);
				}
				
				String acctgFormulaId = ""; 
				try{
					//rateAmountEntry
					inMap.put("rateTypeId", "PTC_RATE");
					Map<String, Object> serviceResults = dispatcher.runSync("getFacilityPartyRate", inMap);
					rate = (BigDecimal)serviceResults.get("rateAmount");
					if(UtilValidate.isNotEmpty(serviceResults) && UtilValidate.isNotEmpty(rate) && rate.compareTo(BigDecimal.ZERO)==0){
						GenericValue VehicleRate = (GenericValue)serviceResults.get("rateAmountEntry");
						if(UtilValidate.isNotEmpty(VehicleRate)){
							rate = (BigDecimal)VehicleRate.get("rateAmount");
							
							if(UtilValidate.isNotEmpty(VehicleRate.get("acctgFormulaId")) ){
								acctgFormulaId = (String)VehicleRate.get("acctgFormulaId");
							}
						}
					}
					
				}catch(Exception e){
					Debug.logError("Error while getting rate of the union ::"+e,module);
					return ServiceUtil.returnError("Error while getting rate of the union ::"+partyId);
				}
				
				if(UtilValidate.isNotEmpty(rate) && rate.compareTo(BigDecimal.ZERO)==0 && UtilValidate.isEmpty(acctgFormulaId)){
					inMap.put("rateTypeId", "PTC_RATE");
					inMap.put("partyId", unionId);
					try{
						Map<String, Object> serviceResults = dispatcher.runSync("getFacilityPartyRate", inMap);
						rate = (BigDecimal)serviceResults.get("rateAmount");
						if(UtilValidate.isEmpty(rate) || (UtilValidate.isNotEmpty(rate) && rate.compareTo(BigDecimal.ZERO)==0)){
							GenericValue VehicleRate = (GenericValue)serviceResults.get("rateAmountEntry");
							if(UtilValidate.isNotEmpty(VehicleRate)){
								rate = (BigDecimal)VehicleRate.get("rateAmount");
								if(UtilValidate.isNotEmpty(VehicleRate.get("acctgFormulaId")) ){
									acctgFormulaId = (String)VehicleRate.get("acctgFormulaId");
								}
							}
						}
						
					}catch(Exception e){
						Debug.logError("Error while getting related Union Rate details ::"+e,module);
						return ServiceUtil.returnError("Error while getting related Union Rate details ::"+e.getMessage());
						
					}
				}
				if(UtilValidate.isNotEmpty(rate) && rate.compareTo(BigDecimal.ZERO)==1){
						// formula is rate*recdQtyKgs
						amount = (rate.multiply(quantityKgs)).setScale(2,BigDecimal.ROUND_HALF_UP);
				}else{
					if(UtilValidate.isNotEmpty(acctgFormulaId) && UtilValidate.isNotEmpty(capacity) && capacity.compareTo(BigDecimal.ZERO)==1){
						try{
							String variableValues = "QUANTITYKGS="+quantityKgs+","+"QUANTITYLTRS="+quantityLtrs+","+"CAPACITY="+capacity+","+"DISTANCE="+distance+","+"RATE="+rate ;
							Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin,"slabAmount",capacity, "acctgFormulaId",acctgFormulaId,"variableValues",variableValues);
						  	Map<String, Object> rateResult = dispatcher.runSync("evaluateAccountFormula", input);
						  	if (ServiceUtil.isError(rateResult)) {
			        			Debug.logError("unable to evaluate AccountFormula"+acctgFormulaId, module);	
			                    return ServiceUtil.returnError("unable to evaluate AccountFormula"+acctgFormulaId);	
			                }
			        		double formulaValue = (Double) rateResult.get("formulaResult");
			        		amount = new BigDecimal(formulaValue);
						}catch(Exception e){
							Debug.logError("Error while evaluating Acctg Formula :"+e,module);
							return ServiceUtil.returnError("Error while evaluating Acctg Formula :"+acctgFormulaId);
						}
					}else{
						Debug.logError("Vehicle Capacity Not Found for :: "+vehicleId,module);
						result = ServiceUtil.returnError("Vehicle Capacity Not Found for :: "+vehicleId);
						
					}
				}
				result.put("amount", amount.setScale(2,BigDecimal.ROUND_HALF_UP));
				return result;
			}
			public static Map<String, Object> updateFineRecvoryWithBilling(DispatchContext dctx, Map<String, Object> context) {
				List conditionList= FastList.newInstance(); 
				LocalDispatcher dispatcher = dctx.getDispatcher();
		        Delegator delegator = dctx.getDelegator();
		        Map<String, Object> result = ServiceUtil.returnSuccess();
				String periodBillingId = (String) context.get("periodBillingId");
				String customTimePeriodId = (String) context.get("customTimePeriodId");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
	        	//String facilityId= null;
				List<String> facilityIdsList=FastList.newInstance();
				if(UtilValidate.isNotEmpty(periodBillingId) &&(UtilValidate.isNotEmpty(customTimePeriodId))){
					conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        	try{
		        		List<GenericValue> fineRecoveryList = FastList.newInstance();
		        		fineRecoveryList = delegator.findList("FineRecovery", condition, null,null, null, false);	 

	                	if(UtilValidate.isNotEmpty(fineRecoveryList)){
	                		for(GenericValue fineRecovery : fineRecoveryList){
	                			fineRecovery.set("periodBillingId",periodBillingId);
	                			fineRecovery.set("lastModifiedDate", UtilDateTime.nowTimestamp());
	                			fineRecovery.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	                			fineRecovery.store();
	                		}
	                	}
		        	}catch(GenericEntityException e){
		        		Debug.logError(e, module);
		        	}
				}			
	        	return result;
			}
			public static Map<String, Object> cancelPtcTranporterMargin(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();       
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		    	Locale locale = (Locale) context.get("locale");
		        //Map<String, Object> result = ServiceUtil.returnSuccess();
		        
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
		    	periodBilling.set("statusId", "CANCEL_INPROCESS");
		        boolean cancelationFailed = false;	
		    	try{
		    		periodBilling.store();    		
		    	}catch (Exception e) {
		    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
		    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
				}
		    	//first Cancel PTC payments
	    		Map ptcPaymentCancelResult=cancelPTCTransporterPayment(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
	    		if (ServiceUtil.isError(ptcPaymentCancelResult)) {
	    			cancelationFailed = true;
	    			periodBilling.set("statusId", "CANCEL_FAILED");
	    			try{
	    	    		periodBilling.store();    		
	    	    	}catch (Exception e) {
	    	    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
	    	    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
	    			}
	                Debug.logWarning("There was an error while Canceling Payment: " + ServiceUtil.getErrorMessage(ptcPaymentCancelResult), module);
	        		return ServiceUtil.returnError("There was an error while Canceling Payment: " + ServiceUtil.getErrorMessage(ptcPaymentCancelResult));          	            
	            } 
		           
		    	try{
		    		Map<String,  Object> inputContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"userLogin", userLogin);
					Map cancelPtcTranInvoiceResult = dispatcher.runSync("cancelPtcTransporterMarginInvoice", inputContext);
					if(ServiceUtil.isError(cancelPtcTranInvoiceResult)){
						Debug.logError("Error whiel cancelling PTC Transporter Invoice",module);
						return ServiceUtil.returnError("Error whiel cancelling PTC Transporter Invoice");
					}
					
		    	} catch (GenericServiceException e) {
		            Debug.logError(e, "Error in canceling 'transporterMargin' service", module);
		            return ServiceUtil.returnError(e.getMessage());
		        } 
		    	result = ServiceUtil.returnSuccess("Successfully cancelled");
		        return result;
		    }//end of the service
			
			public static Map<String, Object> cancelPTCTransporterPayment(DispatchContext dctx, Map<String, ? extends Object> context) {
				Delegator delegator = dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String periodBillingId = (String) context.get("periodBillingId");
				Map<String, Object> result = ServiceUtil.returnSuccess();
				List<String> billingPaymentIdsList=(List<String>)getPTCBillingPayments(dctx, UtilMisc.toMap("periodBillingId", periodBillingId,"userLogin", userLogin)).get("paymentIdsList");
				Locale locale = (Locale) context.get("locale");
				if(UtilValidate.isEmpty(billingPaymentIdsList)){
					result = ServiceUtil.returnSuccess("No Payments Found To Cancel ..!");
					return result;
				}
			 try { 
				for(String paymentId:billingPaymentIdsList){
				            	 Map<String, Object> removePaymentApplResult = dispatcher.runSync("voidPayment", UtilMisc.toMap("userLogin" ,userLogin ,"paymentId", paymentId));
								 if (ServiceUtil.isError(removePaymentApplResult)) {
						            	Debug.logError(removePaymentApplResult.toString(), module);    			
						                return ServiceUtil.returnError(null, null, null, removePaymentApplResult);
						         }
				    }
			    }catch (GenericServiceException e) {
					Debug.logError("Error while Cancel Payment for PTC" + e.getMessage(),module);
					return ServiceUtil.returnError("Error while Cancel Payment for PTC");
				}
				result = ServiceUtil.returnSuccess("Payment  Cancelled For Billing ..!");
				return result;
			}// end of service
			
			public static Map<String, Object> getPTCBillingPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();       
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        Map<String, Object> result = new HashMap<String, Object>();
		        Locale locale = (Locale) context.get("locale");
		        boolean cancelationFailed = false;	
		        String periodBillingId = (String) context.get("periodBillingId");
		        List paymentIdsList=FastList.newInstance();
		    	List ptcPaymentsList = FastList.newInstance();
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
					Debug.logError(e1, e1.getMessage());
					return ServiceUtil.returnError("Error in customTimePeriod" + e1);
				}
				Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
				Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
				
				Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, TimeZone.getDefault(), locale);
				Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, TimeZone.getDefault(), locale);
				
				try{
					List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company"));
					  conditionList.add(EntityCondition.makeCondition("paymentRefNum", EntityOperator.EQUALS,  "PTC_TRSPT_MRGN_"+periodBillingId));
				     conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, "EXPENSE_PAYOUT"));
		             conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
		            
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
		        	List<GenericValue> paymentsList = delegator.findList("Payment", condition, null, null, null, false);
			        paymentIdsList = EntityUtil.getFieldListFromEntityList(paymentsList, "paymentId", false);
			        ptcPaymentsList.addAll(paymentsList);
				  }catch(GenericEntityException e){
			        	Debug.logError("Unable to get PTC Payments"+e, module);
			    		return ServiceUtil.returnError("Unable to get PTC Payments "); 
			      }
			     result.put("paymentIdsList", paymentIdsList);
			     result.put("ptcPaymentsList", ptcPaymentsList);
		        return result;
		    }// end of the service
		
		/**
		 * 	
		 * @param dctx
		 * @param context
		 * @return
		 */
			
			public static Map<String, Object> createPTCInvoice (DispatchContext dctx, Map<String, ? extends Object> context) {
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
				Map fineRecoveryResultMap =(Map) getFineRecvoryForPeriodBilling(dctx,UtilMisc.toMap("periodBillingId",periodBillingId,"fromDate",monthBegin,"userLogin",userLogin));
				
				Map partyWiseRecvoryMap= FastMap.newInstance();
				partyWiseRecvoryMap.putAll((Map)fineRecoveryResultMap.get("contractorRecoveryInfoMap"));
				Map partyVehicleMap = FastMap.newInstance();
				partyVehicleMap.putAll((Map)fineRecoveryResultMap.get("partyVehicleMap"));
			   
				List<Strings> ptcAdditionTypeIds  = FastList.newInstance();
				List<Strings> ptcDeductionTypeIds  = FastList.newInstance();
				ptcAdditionTypeIds.addAll((List)fineRecoveryResultMap.get("ptcAdditionTypeIds"));
				ptcDeductionTypeIds.addAll((List)fineRecoveryResultMap.get("ptcDeductionTypeIds"));
			   
				Map getTransporterTotalsInMap = FastMap.newInstance();
				getTransporterTotalsInMap.put("userLogin",userLogin);
				getTransporterTotalsInMap.put("partyVehicleMap",partyVehicleMap);
				getTransporterTotalsInMap.put("periodBillingId",periodBillingId);
				Map transporterTradeResultMap=getTransporterTotalsForPeriodBilling(dctx, getTransporterTotalsInMap);
				//Map totalRouteTradingTotalMap=(Map)transporterTradeResultMap.get("routeTradingMap");
				Map totalPartyTradingTotalMap=(Map)transporterTradeResultMap.get("partyTradingMap");
		//		Map totalPartyTradingTotalMap=(Map)transporterTradeResultMap.get("roundedContractorRecoveryInfoMap");
				
				
				if(UtilValidate.isNotEmpty(totalPartyTradingTotalMap)){
					Iterator partyMarginsIter = totalPartyTradingTotalMap.entrySet().iterator();
					try{
						while (partyMarginsIter.hasNext()) {
							Map.Entry partyEntry = (Entry) partyMarginsIter.next();	
							String invoiceId="";
							//String facilityId = (String) routeEntry.getKey();	
							
							String partyIdTo = (String) partyEntry.getKey();	
							Map partyWiseValues = (Map) partyEntry.getValue();
							BigDecimal totalMargin = (BigDecimal) partyWiseValues.get("margin");
							BigDecimal quantity = BigDecimal.ONE;
							// here Invoice Raised by Transporter
							 if (UtilValidate.isEmpty(invoiceId)&& UtilValidate.isNotEmpty(partyIdTo)) {
					                Map<String, Object> createInvoiceContext = FastMap.newInstance();
					                createInvoiceContext.put("partyId", "Company");
					                createInvoiceContext.put("partyIdFrom", partyIdTo);
					                //createInvoiceContext.put("billingAccountId", billingAccountId);
					               // createInvoiceContext.put("invoiceDate", UtilDateTime.nowTimestamp());
					                createInvoiceContext.put("dueDate", monthEnd);
					                createInvoiceContext.put("invoiceDate", monthEnd);
					                createInvoiceContext.put("invoiceTypeId", "COGS_OUT");
					                createInvoiceContext.put("referenceNumber", "PTC_TRSPT_MRGN_"+periodBillingId);
					                // start with INVOICE_IN_PROCESS, in the INVOICE_READY we can't change the invoice (or shouldn't be able to...)
					                createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
					                createInvoiceContext.put("currencyUomId", "INR");
					                createInvoiceContext.put("userLogin", userLogin);
			
					                // store the invoice first
					                Map<String, Object> createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
					                if (ServiceUtil.isError(result)) {
					    		    		//generationFailed = true;
					    	                Debug.logWarning("There was an error while creating  Invoice For TransporterCommission: " + ServiceUtil.getErrorMessage(result), module);
					    	        		return ServiceUtil.returnError("There was an error while creating Invoice for  TransporterCommission: " + ServiceUtil.getErrorMessage(result));          	            
					    	            } 
					                // call service for creation invoice);
					                invoiceId = (String) createInvoiceResult.get("invoiceId");
					                Map<String, Object> resMap = null;
				                    resMap = dispatcher.runSync("createInvoiceItem", 
				                    		UtilMisc.toMap("invoiceId", invoiceId,"invoiceItemTypeId", "PTC_MRGN","quantity",quantity,"amount", totalMargin,"userLogin", userLogin));
				                    if (ServiceUtil.isError(result)) {
				                    	//generationFailed = true;
				    	                Debug.logWarning("There was an error while creating  the InvoiceItem: " + ServiceUtil.getErrorMessage(result), module);
				                    }
				                    
				                    if(UtilValidate.isNotEmpty(partyWiseRecvoryMap) && UtilValidate.isNotEmpty(partyWiseRecvoryMap.get(partyIdTo))){
				                    	Map tempPartyRecoveries = FastMap.newInstance();
				                    	tempPartyRecoveries.putAll((Map)partyWiseRecvoryMap.get(partyIdTo));
				                    	if(UtilValidate.isNotEmpty(tempPartyRecoveries)){
					                    	for(Object recKey : tempPartyRecoveries.keySet()){
					                    		String recKeyStr = recKey.toString();
					                    		BigDecimal amount = BigDecimal.ZERO;
					                    		String invoiceItemTypeId = recKeyStr;
					                    		amount = (BigDecimal)tempPartyRecoveries.get(recKeyStr);
					                    		if(ptcDeductionTypeIds.contains(recKeyStr)){
					                    			amount = amount.negate();
					                    			if("PTC_TDS".equalsIgnoreCase(recKeyStr)){
					                    				invoiceItemTypeId = "TDS_194H";
					                    			}
					                    		}else{
					                    			if(!ptcAdditionTypeIds.contains(recKeyStr)){
					                    				continue;
					                    			}
					                    		}
					                    		// here we are creating Invoice Items
					                    		resMap = dispatcher.runSync("createInvoiceItem", 
							                    		UtilMisc.toMap("invoiceId", invoiceId,"invoiceItemTypeId",
							                    				invoiceItemTypeId,"quantity",quantity,"amount", amount,"userLogin", userLogin));
							                    if (ServiceUtil.isError(result)) {
							    	                Debug.logWarning("There was an error while creating  the "+recKeyStr+" InvoiceItem: " + ServiceUtil.getErrorMessage(result), module);
							                    }
					                    	}
				                    	}
				                    	
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
						
					}//end of while
				}catch(GenericServiceException e){
	            	 Debug.logError(e, e.toString(), module);
	                return ServiceUtil.returnError(e.toString());
				}
			}//end of if
			return result;
		}	// end of the service			
			
			
			
	    public static Map<String, Object> getFineRecvoryForPeriodBilling(DispatchContext dctx, Map<String, Object> context) {
				List conditionList= FastList.newInstance(); 
				LocalDispatcher dispatcher = dctx.getDispatcher();
		        Delegator delegator = dctx.getDelegator();
		        Map<String, Object> result = ServiceUtil.returnSuccess();
				String periodBillingId = (String) context.get("periodBillingId");
				
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Map<String, Object> vehicleFineRecoveryInfoMap = new HashMap<String, Object>();
				//Map<String, Object> fineRecoveryInfoMap = new HashMap<String, Object>();
				Map<String, Object> contractorRecoveryInfoMap = new HashMap<String, Object>();
				Timestamp monthBegin =UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				if(UtilValidate.isNotEmpty((Timestamp) context.get("fromDate"))){
					monthBegin=(Timestamp) context.get("fromDate");
				}
				
				Timestamp monthEnd =UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
				if(UtilValidate.isNotEmpty((Timestamp) context.get("thruDate"))){
					monthEnd=(Timestamp) context.get("thruDate");
				}
	        	//String facilityId= null;
				List<String> vehicleIdsList=FastList.newInstance();
				if(!UtilValidate.isEmpty(periodBillingId)){
					conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        	try{
		        		List<GenericValue> fineRecoveryList = FastList.newInstance();
		        		fineRecoveryList = delegator.findList("FineRecovery", condition, null,null, null, false);	 
		        		vehicleIdsList = EntityUtil.getFieldListFromEntityList(fineRecoveryList, "vehicleId", false);
		        		vehicleIdsList = new ArrayList<String>(new HashSet(vehicleIdsList));
		        		
			        	List<String> enumTypeList = UtilMisc.toList("PTC_ADDN","PTC_DED");
			        	
			        	List<GenericValue> ptcAjustmentTypes = delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId",EntityOperator.IN,enumTypeList), null, null, null, true);
			        	
			        	List<GenericValue> ptcAdditionTypes = FastList.newInstance();
			        	List<GenericValue> ptcDeductionTypes = FastList.newInstance();
			        	ptcAdditionTypes = EntityUtil.filterByCondition(ptcAjustmentTypes, EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PTC_ADDN"));
			        	ptcDeductionTypes = EntityUtil.filterByCondition(ptcAjustmentTypes, EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PTC_DED"));
			        	
			        	List<String> ptcAdditionTypeIds = EntityUtil.getFieldListFromEntityList(ptcAdditionTypes, "enumId", false);
			        	List<String> ptcDeductionTypeIds = EntityUtil.getFieldListFromEntityList(ptcDeductionTypes, "enumId", false);
			        	
			        	result.put("ptcAdditionTypeIds",ptcAdditionTypeIds);
			        	result.put("ptcDeductionTypeIds",ptcDeductionTypeIds);
			        	if(!UtilValidate.isEmpty(vehicleIdsList)){
	                		for(String vehicleId : vehicleIdsList){
	                			Map<String, Object> vehicleFineTempMap = FastMap.newInstance();
	                			List<GenericValue> vehicleFines = FastList.newInstance();
	                			vehicleFines = EntityUtil.filterByCondition(fineRecoveryList,EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
	                			BigDecimal totalAdditon = BigDecimal.ZERO;
	                			BigDecimal totalDeduction = BigDecimal.ZERO;
	                			for(GenericValue vehicleFine : vehicleFines){
	                				String recoveryTypeId = (String) vehicleFine.get("recoveryTypeId");
	                				BigDecimal fineAmount = (BigDecimal)vehicleFine.get("amount");
	                				if(UtilValidate.isEmpty(vehicleFineTempMap) || (UtilValidate.isNotEmpty(vehicleFineTempMap) && UtilValidate.isEmpty(vehicleFineTempMap.get(recoveryTypeId)))){
	                					vehicleFineTempMap.put(recoveryTypeId, fineAmount);
	                				}else{
	                					vehicleFineTempMap.put(recoveryTypeId, fineAmount.add((BigDecimal)vehicleFineTempMap.get(recoveryTypeId)));
	                				}
	                				if(ptcAdditionTypeIds.contains(recoveryTypeId)){
	                					totalAdditon=totalAdditon.add(fineAmount);
	                				}else{
	                					totalDeduction=totalDeduction.add(fineAmount);
	                				}
	                			}
	                			vehicleFineTempMap.put("totAddn", totalAdditon);
	                			vehicleFineTempMap.put("totDedn", totalDeduction);
	                			vehicleFineRecoveryInfoMap.put(vehicleId, vehicleFineTempMap);
	                			
	                		}
	                	}
		        	}catch(GenericEntityException e){
		        		Debug.logError("Error While getting vehicleFineRecoveries "+e, module);
		        		ServiceUtil.returnError("Error While getting vehicleFineRecoveries "+e.getMessage());
		        	}
				}			
				result.put("vehicleFineRecoveryInfoMap",vehicleFineRecoveryInfoMap);
				Map inMap= FastMap.newInstance();
				inMap.put("fromDate", monthBegin);
				inMap.put("thruDate", monthEnd);
				Map partyVehicleMap=(Map)getMdContractorWiseVehicle(dctx,inMap).get("contractorWiseMap");
				
				if(UtilValidate.isNotEmpty(partyVehicleMap)){	
				   for ( Object partyVehicleKey : partyVehicleMap.keySet() ) {
					   String  partyId=partyVehicleKey.toString();
					   List<String> partyVehicleList = (List<String>)partyVehicleMap.get(partyId);
					   Map partyFineTempMap = FastMap.newInstance();
					   if(UtilValidate.isNotEmpty(partyVehicleList)){
				        	 for(String vehicleId:partyVehicleList){//adding each party Total fines which are having Routes
				        		 if(UtilValidate.isNotEmpty(vehicleFineRecoveryInfoMap) && UtilValidate.isNotEmpty(vehicleFineRecoveryInfoMap.get(vehicleId))){
				        			Map fineMap= FastMap.newInstance();
				        			fineMap.putAll((Map)vehicleFineRecoveryInfoMap.get(vehicleId));
			        				if(UtilValidate.isEmpty(partyFineTempMap)){
			        					partyFineTempMap.putAll(fineMap);
			        				}else{
					        			for(Object fineKey : fineMap.keySet()){
					        				String fineKeyStr = fineKey.toString();
					        					if(UtilValidate.isEmpty(partyFineTempMap.get(fineKeyStr))){
					        						partyFineTempMap.put(fineKey, fineMap.get(fineKeyStr));
					        					}else{
					        						partyFineTempMap.put(fineKeyStr,( (BigDecimal)fineMap.get(fineKeyStr)).add((BigDecimal)partyFineTempMap.get(fineKeyStr)));
					        					}
					        					
					        				}
				        			}		
				        					
				        		 }
				        	 }
				  		}
            			if(UtilValidate.isNotEmpty(partyFineTempMap)){
            				contractorRecoveryInfoMap.put(partyId, partyFineTempMap);
            			}
				  }
	    	}
				result.put("contractorRecoveryInfoMap",contractorRecoveryInfoMap);
				result.put("partyVehicleMap",partyVehicleMap);
	        	return result;
			}// end of the service
			
		public static Map<String, Object> getMdContractorWiseVehicle(DispatchContext dctx, Map<String, Object> context) {
				List conditionList= FastList.newInstance(); 
				LocalDispatcher dispatcher = dctx.getDispatcher();
		        Delegator delegator = dctx.getDelegator();
		        Map<String, Object> result = ServiceUtil.returnSuccess();
		        List<GenericValue> unionsList = FastList.newInstance();
		        List<String> unionIdsList=FastList.newInstance();
		        Timestamp fromDate = (Timestamp) context.get("fromDate");
		        Timestamp thruDate = (Timestamp) context.get("thruDate");
		        try{
		        	
		        	unionsList = delegator.findList("PartyRoleAndPartyDetail",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"UNION"), null, null, null, true);
		        	if(UtilValidate.isNotEmpty(unionsList)){
		        		unionIdsList = EntityUtil.getFieldListFromEntityList(unionsList, "partyId", false);
		        		unionIdsList.remove("MD");
		        	}else{
		        		return ServiceUtil.returnError("Unions are not found::");
		        	}
		        }catch(Exception e){
		        	Debug.logError("Error while getting unions information"+e, module);
		        	return ServiceUtil.returnError("Error while getting unions information"+e.getMessage());
		        }
		        
		        List contractorsList = FastList.newInstance();
		        try{
		        	conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PTC_VEHICLE"));
			        conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_IN,unionIdsList));
			        
			        EntityCondition condition = EntityCondition.makeCondition(conditionList);
			        
			        contractorsList = delegator.findList("VehicleRole",condition, null, null, null, true);
		        }catch(Exception e){
		        	
		        	Debug.logError("Error while getting contractors List "+e, module);
		        	return ServiceUtil.returnError("Error while getting contractors List"+e.getMessage());
		        }
		        
		        if(UtilValidate.isEmpty(contractorsList)){
		        	return ServiceUtil.returnError("Contractors not Found");
		        }
		         
		        List<String> partiesList = EntityUtil.getFieldListFromEntityList(contractorsList, "partyId",false);
		        Set<String> contractorIdList=new HashSet(partiesList);
		        Map contractorWiseMap = FastMap.newInstance();
		        for(String contractorId : contractorIdList){
		        	List<String> contractorVehiclesList = FastList.newInstance();
		        	conditionList.clear();
		        	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,contractorId));
		        	conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,fromDate));
		        	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null),EntityOperator.OR,EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,thruDate)));
		        	EntityCondition contractorVehicleCondition = EntityCondition.makeCondition(conditionList);
		        	List<GenericValue> contractorVehicles = EntityUtil.filterByCondition(contractorsList, contractorVehicleCondition); 
		        	List<String> contractorVehicleIdsList = FastList.newInstance();
		        	contractorVehicleIdsList =EntityUtil.getFieldListFromEntityList(contractorVehicles, "vehicleId", false);
		        	Set<String> tempVehiclesSet=new HashSet(contractorVehicleIdsList);
		        	List<String> newVehiclesList = new ArrayList<String>(tempVehiclesSet);
		        	contractorWiseMap.put(contractorId, newVehiclesList);
		        }
		        
		        result.put("contractorWiseMap", contractorWiseMap);
		        return result;
			}//end of service
		
		
		 public static Map<String, Object> getTransporterTotalsForPeriodBilling(DispatchContext dctx, Map<String, Object> context) {
				List conditionList= FastList.newInstance(); 
				LocalDispatcher dispatcher = dctx.getDispatcher();
		        Delegator delegator = dctx.getDelegator();
		        Map<String, Object> result = ServiceUtil.returnSuccess();
				String periodBillingId = (String) context.get("periodBillingId");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Map<String, Object> routeTradingMap = new HashMap<String, Object>();
				Map<String, Object> partyTradingMap = new HashMap<String, Object>();
				Map partyAndVehicleListMap = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(context.get("partyVehicleMap"))){
					partyAndVehicleListMap.putAll((Map)context.get("partyVehicleMap"));
				}
				if(UtilValidate.isNotEmpty(periodBillingId)){
					conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        	try{
		        		List<GenericValue> ptcBillingCommissions = FastList.newInstance();
		        		ptcBillingCommissions = delegator.findList("PtcBillingCommissionAndMilkTransfer", condition, null,null, null, false);	        		
	                	
		        		if(!UtilValidate.isEmpty(ptcBillingCommissions)){
	                		if(UtilValidate.isNotEmpty(partyAndVehicleListMap)){
	                			for(Object partyKey : partyAndVehicleListMap.keySet()){
	                				Map partyWiseCommissionMap = FastMap.newInstance();
	                				String partyKeyStr = partyKey.toString();
	                				List partyVehicleList = FastList.newInstance();
	                				partyVehicleList.addAll((List) partyAndVehicleListMap.get(partyKeyStr));
	                				conditionList.clear();
	                				conditionList.add(EntityCondition.makeCondition("containerId", EntityOperator.IN, partyVehicleList));
	                				condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	                				
	                				List<GenericValue> partyPtcBillingCommissions = FastList.newInstance();
	                				
	                				partyPtcBillingCommissions = EntityUtil.filterByCondition(ptcBillingCommissions, condition);
	                				if(UtilValidate.isNotEmpty(partyPtcBillingCommissions)){
		                				for(GenericValue partyCommission : partyPtcBillingCommissions){
		                					//here we are storing quantity and commission in partyWiseCommissionMap 
		                					BigDecimal quantity = BigDecimal.ZERO;
		                					BigDecimal margin = BigDecimal.ZERO;
		                					
		                					if(UtilValidate.isEmpty(partyWiseCommissionMap)){
		                						quantity = (BigDecimal) partyCommission.get("receivedQuantity");
		                						margin = (BigDecimal) partyCommission.get("commissionAmount");
		                						
		                					}else{
		                						quantity = ((BigDecimal) partyCommission.get("receivedQuantity")).add((BigDecimal)partyWiseCommissionMap.get("quantity"));
		                						margin = ((BigDecimal) partyCommission.get("commissionAmount")).add((BigDecimal)partyWiseCommissionMap.get("margin"));
		                						
		                					}
		                					partyWiseCommissionMap.put("quantity", quantity);
	                						partyWiseCommissionMap.put("margin",margin);
		                				}
		                				partyTradingMap.put(partyKeyStr, partyWiseCommissionMap);
	                				}
	                			}
	                			
	                		}
		        			
	                	}
		        	}catch(GenericEntityException e){
		        		Debug.logError(e, module);
		        	}
				}			
				result.put("partyTradingMap",partyTradingMap);
	        	return result;
			}// end of the service
		    
		    
		    
			
		 /**
		  * Service for TransporterAdjustments    
		  * @param dctx
		  * @param context
		  * @return
		  */

		 public static Map<String, Object> createPtcTransporterAdjustment(DispatchContext dctx, Map context) {
		    	Map<String, Object> result = ServiceUtil.returnSuccess();
		    	String vehicleId = (String) context.get("vehicleId");
		    	String customTimePeriodId = (String) context.get("customTimePeriodId");
		    	String recoveryTypeId = (String)context.get("recoveryTypeId");
		    	Timestamp incidentDate = (Timestamp)context.get("incidentDate");
		    	BigDecimal amount = (BigDecimal)context.get("amount");
		 		GenericValue userLogin = (GenericValue) context.get("userLogin");
		 		String description=(String)context.get("description");
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		 		LocalDispatcher dispatcher = dctx.getDispatcher();
		 		List conditionList=FastList.newInstance();
		 		try {
		            conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_PTC_TRSPT_MRGN"));
		            conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		            conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        		List<GenericValue> periodBillingList = FastList.newInstance();
		        		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);	 
		        		if(UtilValidate.isNotEmpty(periodBillingList)){
		        			 Debug.logError("Billing Is Already Generated For This Period", module);
		        			 return ServiceUtil.returnError("Billing Is Already Generated For This Period  and You Can Not Create Additions or Deductions!");   
		        		}
		 			
		        	conditionList.clear();
		        	conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS, vehicleId));
		        	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		        	conditionList.add(EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS, recoveryTypeId));
		        	if(UtilValidate.isNotEmpty(incidentDate)){
		        		conditionList.add(EntityCondition.makeCondition("incidentDate", EntityOperator.EQUALS, incidentDate));
		        	}
		        	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        	
		        	List<GenericValue> recoveryList = delegator.findList("FineRecovery", condition, null,null, null, false);
		        	
		        	if(UtilValidate.isNotEmpty(recoveryList)){
		        		Debug.logError("Recovery exists for the same period and for  same date:",module);
		        		return ServiceUtil.returnError("Recovery exists for the same period and for same date");
		        	}
		        		
		        	GenericValue fineRecovery = delegator.makeValue("FineRecovery");
		 			fineRecovery.put("vehicleId", vehicleId );
		 			fineRecovery.put("customTimePeriodId", customTimePeriodId);
		 			fineRecovery.put("incidentDate", incidentDate);
		 			fineRecovery.put("recoveryTypeId", recoveryTypeId); 
		 			fineRecovery.put("amount", amount);
		 			fineRecovery.put("description", description);
		 			fineRecovery.put("createdDate", UtilDateTime.nowTimestamp());
		 			fineRecovery.put("lastModifiedDate", UtilDateTime.nowTimestamp());
		 			fineRecovery.put("createdByUserLogin", userLogin.get("userLoginId"));
		 			delegator.createSetNextSeqId(fineRecovery);            
		 			String recoveryId = (String) fineRecovery.get("recoveryId");
		 			
		        }catch(GenericEntityException e){
		        	Debug.logError("Error while creating Transporter Recovery"+e.getMessage(), module);
		 			return ServiceUtil.returnError("Error while creating Transporter Recovery");
		 		}
		        result = ServiceUtil.returnSuccess("Transporter amount Created Sucessfully");
		        result.put("createdDate",UtilDateTime.nowTimestamp());
		        result.put("incidentDate",incidentDate);
		        return result;
		 }// end of the service  	    


		 /**
		  * Service for updating TransporterAdjustment
		  * @param dctx
		  * @param context
		  * @return
		  */
		 public static Map<String, Object> updatePtcTransporterAdjustment(DispatchContext dctx, Map context) {
		 	Map<String, Object> result = ServiceUtil.returnSuccess();
		 	String recoveryId = (String) context.get("recoveryId");
		 	String vehicleId = (String) context.get("vehicleId");
		 	String customTimePeriodId = (String) context.get("customTimePeriodId");
		 	String recoveryTypeId = (String)context.get("recoveryTypeId");
		 	String dateOfIncident = (String) context.get("incidentDate");
		 	//Timestamp incidentDate = (Timestamp)context.get("incidentDate");
		 	BigDecimal amount = (BigDecimal)context.get("amount");
		 	GenericValue userLogin = (GenericValue) context.get("userLogin");
		 	String description=(String)context.get("description");
		 	Locale locale = (Locale) context.get("locale");
		 	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		 	LocalDispatcher dispatcher = dctx.getDispatcher();
		 	List conditionList=FastList.newInstance();
		 	if(amount.compareTo(BigDecimal.ZERO)<0){
		 		amount=amount.multiply(new BigDecimal(-1));
		 	}
		 	Timestamp incidentDate = null;
			//DateFormat givenFormatter = new SimpleDateFormat("dd:MM:yyyy");
	        DateFormat reqformatter = new SimpleDateFormat("yyyy-MM-dd");
	        if(UtilValidate.isNotEmpty(dateOfIncident)){
		        try {
		        //Date givenReceiptDate = (Date) givenFormatter.parse(incidentDateStr);
		        incidentDate = new java.sql.Timestamp(reqformatter.parse(dateOfIncident).getTime());
		        }catch (ParseException e) {
			  		Debug.logError(e, "Cannot parse date string: " + dateOfIncident, module);
			  	} catch (NullPointerException e) {
		  			Debug.logError(e, "Cannot parse date string: " + dateOfIncident, module);
			  	}
	        }
	        incidentDate = UtilDateTime.getDayStart(incidentDate, TimeZone.getDefault(), locale); 
		 	try {
		        conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_PTC_TRSPT_MRGN"));
		        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		    	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		    		List<GenericValue> periodBillingList = FastList.newInstance();
		    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);	 
		    		if(UtilValidate.isNotEmpty(periodBillingList)){
		    			 Debug.logError("Billing Is Already Generated For This Period. You Can Not update Additions or Deductions!  ", module);
		    			 return ServiceUtil.returnError("Billing Is Already Generated For This Period  and You Can Not update Additions or Deductions!");   
		    		}
		    	 GenericValue fineRecovery = delegator.findOne("FineRecovery", UtilMisc.toMap("recoveryId",recoveryId),false);	
		 		 if(UtilValidate.isEmpty(fineRecovery)){
		 			 Debug.logError("recovery not found with the recovery Id ::"+recoveryId,module);
		 			 return ServiceUtil.returnError("recovery not found with the recovery Id ::"+recoveryId);
		 			 
		 		 }
		 		 if(!incidentDate.equals(fineRecovery.getTimestamp("incidentDate"))){
		 			 conditionList.clear();
		 			 conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		 			 conditionList.add(EntityCondition.makeCondition("incidentDate", EntityOperator.EQUALS, incidentDate));
		 			 conditionList.add(EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS, recoveryTypeId));
		 			 conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS, vehicleId));
		 			 EntityCondition con = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		 			 List<GenericValue> fineRecoveList= delegator.findList("FineRecovery", con, null, null, null, false);
		 			 if(UtilValidate.isEmpty(fineRecoveList)){
		 				fineRecovery.set("incidentDate", incidentDate);
		 				fineRecovery.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		 				fineRecovery.store(); 
		 			 }else{
		 				 Debug.logError("This Adjustment already added for this Date.",module);
		 				 return ServiceUtil.returnError("This Adjustment already added for this Date ");
		 			 }
		 		 }else{
			 	 	 fineRecovery.set("vehicleId", vehicleId );
			 		 fineRecovery.set("customTimePeriodId", customTimePeriodId);
			 		 fineRecovery.set("incidentDate", incidentDate);
			 		 fineRecovery.set("recoveryTypeId", recoveryTypeId); 
			 		 fineRecovery.set("amount", amount);
			 		 fineRecovery.set("description", description);
			 		 fineRecovery.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			    	 fineRecovery.store(); 
		 		 }
		    	 
		    }catch(GenericEntityException e){
		 		Debug.logError("Error while creating Transporter Recovery"+e.getMessage(), module);
		 		return ServiceUtil.returnError("Error while creating Transporter Recovery");
		 	}
		    result = ServiceUtil.returnSuccess("Transporter adjustment Updated Sucessfully");
		    return result;
		 }// end of the service  	    	 
		 /**
		  * Delete  Transporter Adjustment
		  * @param dctx
		  * @param context
		  * @return
		  */

		 public static Map<String, Object> deletePtcTransporterAdjustment(DispatchContext dctx, Map context) {
		 	Map<String, Object> result = ServiceUtil.returnSuccess();
		 	String recoveryId = (String) context.get("recoveryId");
		 	String vehicleId = (String) context.get("vehicleId");
		 	String customTimePeriodId = (String) context.get("customTimePeriodId");
		 	String recoveryTypeId = (String)context.get("recoveryTypeId");
		 	Timestamp incidentDate = (Timestamp)context.get("incidentDate");
		 	BigDecimal amount = (BigDecimal)context.get("amount");
		 	GenericValue userLogin = (GenericValue) context.get("userLogin");
		 	String description=(String)context.get("description");
		 	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		 	LocalDispatcher dispatcher = dctx.getDispatcher();
		 	List conditionList=FastList.newInstance();
		 	try {
		        conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_PTC_TRSPT_MRGN"));
		        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		    	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		 		List<GenericValue> periodBillingList = FastList.newInstance();
		 		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);	 
		 		if(UtilValidate.isNotEmpty(periodBillingList)){
		 			 Debug.logError("Billing Is Already Generated For This Period. You Can Not delete Additions or Deductions!  ", module);
		 			 return ServiceUtil.returnError("Billing Is Already Generated For This Period  and You Can Not delete Additions or Deductions!");   
		 		}
		    	GenericValue fineRecovery = delegator.findOne("FineRecovery", UtilMisc.toMap("recoveryId",recoveryId),false);	
		 		if(UtilValidate.isEmpty(fineRecovery)){
		 			 Debug.logError("recovery not found with the recovery Id ::"+recoveryId,module);
		 			 return ServiceUtil.returnError("recovery not found with the recovery Id ::"+recoveryId);
		 		 }
		 		fineRecovery.remove();
		    }catch(GenericEntityException e){
		 		Debug.logError("Error while removing Transporter Adjustment"+e.getMessage(), module);
		 		return ServiceUtil.returnError("Error while removing Transporter Adjustment");
		 	}
		    result = ServiceUtil.returnSuccess("Transporter adjustment removed Sucessfully");
		    return result;
		 }// end of the service
		
		 public static String AddPTCAdjustment(HttpServletRequest request, HttpServletResponse response) {
				Delegator delegator = (Delegator) request.getAttribute("delegator");
				LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
				DispatchContext dctx =  dispatcher.getDispatchContext();
				Locale locale = UtilHttp.getLocale(request);
				Map<String, Object> result = ServiceUtil.returnSuccess();
			    HttpSession session = request.getSession();
			    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			    String customTimePeriodId = (String) request.getParameter("customTimePeriodId");
			    String incidentDateStr = (String) request.getParameter("incidentDate");
			    String tankerNo = (String) request.getParameter("tankerNo");
			    String tankerName = (String) request.getParameter("tankerName");
			    String vehicleId = null;
			    if(UtilValidate.isNotEmpty(tankerName)){
			    	vehicleId = tankerName;
			    }else{
			    	vehicleId = tankerNo;
			    }
			    List adjustmentDetials = FastList.newInstance();
				Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
				int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
				if (rowCount < 1) {
					Debug.logError("No rows to process, as rowCount = " + rowCount, module);
					return "error";
				}
			  	Timestamp incidentDate = null;
				//DateFormat givenFormatter = new SimpleDateFormat("dd:MM:yyyy");
		        DateFormat reqformatter = new SimpleDateFormat("dd:MM:yyyy");
		        if(UtilValidate.isNotEmpty(incidentDateStr)){
			        try {
			        //Date givenReceiptDate = (Date) givenFormatter.parse(incidentDateStr);
			        incidentDate = new java.sql.Timestamp(reqformatter.parse(incidentDateStr).getTime());
			        }catch (ParseException e) {
				  		Debug.logError(e, "Cannot parse date string: " + incidentDateStr, module);
				  	} catch (NullPointerException e) {
			  			Debug.logError(e, "Cannot parse date string: " + incidentDateStr, module);
				  	}
		        }
		        if(UtilValidate.isNotEmpty(incidentDate)){
					incidentDate = UtilDateTime.getDayStart(incidentDate, TimeZone.getDefault(), locale); 
					GenericValue customTimePeriod;
					try {
						customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
					} catch (GenericEntityException e1) {
						Debug.logWarning("customTimeperiodId not found.", module);
						request.setAttribute("_ERROR_MESSAGE_", "customTimeperiodId not found.");
						return "success";
					}
					Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
					Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
					
					Timestamp dateBegin = UtilDateTime.getDayStart(fromDateTime, TimeZone.getDefault(), locale);
					Timestamp datehEnd = UtilDateTime.getDayEnd(thruDateTime, TimeZone.getDefault(), locale);
					if(incidentDate.after(datehEnd) || incidentDate.before(dateBegin)){
						Debug.logWarning(incidentDate+"This Date is Not in this TimePeriod.", module);
						request.setAttribute("_ERROR_MESSAGE_", incidentDate+"This Date is Not in this TimePeriod.");
						return "success";
					}
		        }
			        String adjustmentTypeId = "";
			        String amountStr = "";
					for (int i = 0; i < rowCount; i++) {
							BigDecimal amount = BigDecimal.ZERO; 
							String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
							
							if (paramMap.containsKey("adjustmentTypeId" + thisSuffix)) {
								adjustmentTypeId = (String) paramMap.get("adjustmentTypeId" + thisSuffix);
							}
							else {
								request.setAttribute("_ERROR_MESSAGE_", "Missing adjustmentTypeId");
							}
							
							if (paramMap.containsKey("amount" + thisSuffix)) {
								amountStr = (String) paramMap.get("amount" + thisSuffix);
								if(UtilValidate.isNotEmpty(amountStr)){
								amountStr = amountStr.replace(",", "");
								amount = new BigDecimal(amountStr);
								}
							}
							else {
								request.setAttribute("_ERROR_MESSAGE_", "Missing amount");
							}
							if(!amount.equals(BigDecimal.ZERO)){	
								Map<String ,Object> inputMap = FastMap.newInstance();
								inputMap.put("customTimePeriodId",customTimePeriodId);
								inputMap.put("vehicleId",vehicleId);
								inputMap.put("amount",amount);
								inputMap.put("recoveryTypeId",adjustmentTypeId);
								inputMap.put("incidentDate",incidentDate);
								
								adjustmentDetials.add(inputMap);
							}
		 				}
					if(UtilValidate.isEmpty(adjustmentDetials)){
						Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
						request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
						return "success";
					}
					Map processAdjustments = FastMap.newInstance();
					processAdjustments.put("adjustmentDetials", adjustmentDetials);
					processAdjustments.put("userLogin",userLogin);
					
					result = processAdjustments(dctx,processAdjustments);
					if(ServiceUtil.isError(result)){
						Debug.logError("Error while creating the Adjustments: " + ServiceUtil.getErrorMessage(result), module);
						request.setAttribute("_ERROR_MESSAGE_", "Error while creating the Adjustments:" +ServiceUtil.getErrorMessage(result));
						return "error";
					}
			        request.setAttribute("_EVENT_MESSAGE_", "Adjusments Successfully Added.");
					
				return "success";
		    }	
		 public static Map<String, Object> processAdjustments(DispatchContext dctx,Map<String, ? extends Object> context) { 
			 	Map<String, Object> result = ServiceUtil.returnSuccess();
			 	List<Map> adjustmentDetials = (List) context.get("adjustmentDetials");
			 	GenericValue userLogin = (GenericValue) context.get("userLogin");
			 	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			 	LocalDispatcher dispatcher = dctx.getDispatcher();
			 	for (Map<String, Object> adjustment : adjustmentDetials) {
			 		try{
						Map<String ,Object> inputMap = FastMap.newInstance();
						inputMap.put("customTimePeriodId",(String) adjustment.get("customTimePeriodId"));
						inputMap.put("vehicleId",(String) adjustment.get("vehicleId"));
						inputMap.put("amount",(BigDecimal) adjustment.get("amount"));
						inputMap.put("recoveryTypeId",(String) adjustment.get("recoveryTypeId"));
						inputMap.put("incidentDate",(Timestamp) adjustment.get("incidentDate"));
						inputMap.put("userLogin",userLogin);
						
						 Map resultMap = dispatcher.runSync("createPtcTransporterRecovery",inputMap);
					        if (ServiceUtil.isError(resultMap)) {
					        	Debug.logError("Problem creating TransporterRecovery for:"+(String) adjustment.get("vehicleId"), module);
								TransactionUtil.rollback();
						  		return ServiceUtil.returnError("Problem creating TransporterRecovery for:"+(String) adjustment.get("vehicleId"));
					        }
					}catch (Exception e) {
						// TODO: handle exception
						Debug.logError(e, module);
			  			return ServiceUtil.returnError(e.toString());
					}
			 	}	
			 	return result;
		 }
		    
}



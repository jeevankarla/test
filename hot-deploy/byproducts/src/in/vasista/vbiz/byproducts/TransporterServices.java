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
	package in.vasista.vbiz.byproducts;



	import java.math.BigDecimal;
	import java.math.RoundingMode;
	import java.text.ParseException;
	import java.util.*;
	import java.util.Map.Entry;
	import java.sql.Date;
	import java.sql.Timestamp;

	import javolution.util.FastList;
	import javolution.util.FastMap;
	import javolution.util.FastSet;

	import org.apache.tools.ant.filters.TokenFilter.ContainsString;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.entity.util.*;
	import org.ofbiz.base.util.*;
	import org.ofbiz.network.DeprecatedNetworkServices;
	import org.ofbiz.service.DispatchContext;
	import org.ofbiz.service.GenericServiceException;
	import org.ofbiz.service.LocalDispatcher;
	import org.ofbiz.service.ModelService;
	import org.ofbiz.service.ServiceUtil;
	
	import org.ofbiz.network.LmsServices;

	import in.vasista.vbiz.byproducts.SalesHistoryServices;


import java.text.SimpleDateFormat;

	public class TransporterServices {
		
		public static final String module = TransporterServices.class.getName();

		
		
		public static void populateTotals(DispatchContext dctx, Map context,
				
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
		}
		
		 
		public static Map<String, Object> generateTranporterMargin(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericServiceException {
			Delegator delegator = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
			TimeZone timeZone = TimeZone.getDefault();
			List masterList = FastList.newInstance();
			Locale locale = Locale.getDefault();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String customTimePeriodId = (String) context.get("customTimePeriodId");
			String periodBillingId = (String) context.get("periodBillingId");
			String billingTypeId = (String) context.get("billingTypeId");
			RoundingMode rounding = RoundingMode.HALF_UP;
			Map resultValue = ServiceUtil.returnSuccess();
			boolean generationFailed = false;		
			GenericValue tenantConfiguration = null;
			
			GenericValue periodBilling = null;
			try{
				try {
					periodBilling =delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
				} catch (GenericEntityException e1) {
					Debug.logError(e1,"Error While Finding PeriodBilling");
					return ServiceUtil.returnError("Error While Finding PeriodBilling" + e1);
				}
				try{        
			    	tenantConfiguration = delegator.findOne("TenantConfiguration",UtilMisc.toMap("propertyName", "enableTranporterDuePayment", "propertyTypeEnumId", "LMS"),false);
			    	tenantConfiguration.set("propertyValue", "N");
			    	tenantConfiguration.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
			    	tenantConfiguration.store();   
			    }catch(GenericEntityException e){
			    	Debug.logError("Unable to set tenant configuration  record in database"+e, module);
			    	periodBilling.set("statusId", "GENERATION_FAIL");
					periodBilling.store();
					return ServiceUtil.returnError("Unable to set tenant configuration  record in database "); 
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
				Map routes = ByProductNetworkServices.getRoutes(dctx , UtilMisc.toMap("facilityTypeId", "ROUTE"));		
				List routesList = (List) routes.get("routesList");
				Map transporterMap =FastMap.newInstance();
				try {
					for (int i = 0; i < routesList.size(); i++) {
						String route = (String) routesList.get(i);
						List boothsList=ByProductNetworkServices.getRouteBooths(delegator ,route);//getting list of Booths
						
						GenericValue facilityDetail = delegator.findOne("Facility",UtilMisc.toMap("facilityId", route) ,false);
						String isUpCountry = facilityDetail.getString("isUpcountry");
						BigDecimal facilitySize=new BigDecimal(1);
						
						if(UtilValidate.isNotEmpty(facilityDetail.getString("facilitySize"))){
							facilitySize=(BigDecimal)facilityDetail.getBigDecimal("facilitySize");
						}
						if(UtilValidate.isEmpty(isUpCountry)){
							isUpCountry="N";
						}
						BigDecimal narmalMargin = BigDecimal.ZERO;
						 //to get normalMargin for each date
						 Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
							inputRateAmt.put("rateCurrencyUomId", context.get("currencyUomId"));
							inputRateAmt.put("facilityId", facilityDetail.get("facilityId"));
							inputRateAmt.put("fromDate",monthBegin );
							inputRateAmt.put("rateTypeId", "TRANSPORTER_MRGN");
							Map<String, Object> facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
							
							if (ServiceUtil.isError(facilityRateResult)) {
				    			generationFailed = true;
				    			Debug.logWarning("There was an error while creating  the TransporterCommission: " + ServiceUtil.getErrorMessage(result), module);
				        		return ServiceUtil.returnError("There was an error while creating the TransporterCommission: " + ServiceUtil.getErrorMessage(result));          	            
				            }
							String uomId="";
							BigDecimal monthBeginMargin = BigDecimal.ZERO;
				    		if(UtilValidate.isNotEmpty(facilityRateResult)){
				    			monthBeginMargin = (BigDecimal) facilityRateResult.get("rateAmount");
				    			 uomId=(String)facilityRateResult.get("uomId");
				    		}		
				    		inputRateAmt.put("fromDate",monthEnd );
	                        facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
							if (ServiceUtil.isError(facilityRateResult)) {
				    			generationFailed = true;
				    			Debug.logWarning("There was an error while creating  the TransporterCommission: " + ServiceUtil.getErrorMessage(result), module);
				        		return ServiceUtil.returnError("There was an error while creating the TransporterCommission: " + ServiceUtil.getErrorMessage(result));          	            
				            }
							
							BigDecimal monthEndMargin = BigDecimal.ZERO;
				    		if(UtilValidate.isNotEmpty(facilityRateResult)){
				    			monthEndMargin = (BigDecimal) facilityRateResult.get("rateAmount");
				    			 uomId=(String)facilityRateResult.get("uomId");
				    		}	
										
			    		BigDecimal dayTotQty = BigDecimal.ZERO;
			    		BigDecimal tripSize = BigDecimal.ZERO;
						
						Map dayTotalsMap = FastMap.newInstance();
						Map dayWiseTotalsMap = FastMap.newInstance();
						//getting
					/*	Map dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), UtilMisc.toMap("facilityIds",boothsList,"fromDate",monthBegin, "thruDate",monthEnd));
						dayWiseTotalsMap=(Map)dayTotals.get("dayWiseTotals");*/
						
						Timestamp supplyDate = monthBegin;
				        for (int k = 0; k <= (totalDays); k++) {
							Map routeMarginMap = FastMap.newInstance();
							routeMarginMap.put("quantity",BigDecimal.ZERO );
							routeMarginMap.put("saleAmount", BigDecimal.ZERO);
							routeMarginMap.put("cashQty",BigDecimal.ZERO );
							routeMarginMap.put("cashAmount", BigDecimal.ZERO);
							routeMarginMap.put("commision", BigDecimal.ZERO);
							
							supplyDate = UtilDateTime.addDaysToTimestamp(monthBegin, k);	
							List shipmentIds =ByProductNetworkServices.getShipmentIds(delegator,supplyDate,supplyDate);
						
							 String curntDay=UtilDateTime.toDateString(supplyDate ,"yyyy-MM-dd");
							 Map curntDaySalesMap=(Map)dayWiseTotalsMap.get(curntDay);
							 if(UtilValidate.isNotEmpty(curntDaySalesMap)){/*
							
							BigDecimal saleAmount = (BigDecimal)curntDaySalesMap.get("totalRevenue");
							BigDecimal totalQty=(BigDecimal)curntDaySalesMap.get("total");
							
							Map supplyTypeTotalsMap=(Map)curntDaySalesMap.get("supplyTypeTotals");
			                //cash
							Map supplyTypeCashMap=(Map)supplyTypeTotalsMap.get("CASH");
							BigDecimal cashQty=(BigDecimal)supplyTypeCashMap.get("total");
							BigDecimal cashAmount= (BigDecimal)supplyTypeCashMap.get("totalRevenue");
							//leak
							Map supplyTypeLeakMap=(Map)supplyTypeTotalsMap.get("LEAK");
							
							BigDecimal leakQty=(BigDecimal)supplyTypeLeakMap.get("total");
							BigDecimal leakAmount= (BigDecimal)supplyTypeLeakMap.get("totalRevenue");
							
							totalQty=totalQty.subtract(leakQty);
							saleAmount=saleAmount.subtract(leakAmount);
							
							routeMarginMap.put("cashQty",cashQty );
							routeMarginMap.put("cashAmount", cashAmount);
							
							routeMarginMap.put("quantity",totalQty );
							routeMarginMap.put("saleAmount", saleAmount);					
							*/}	
								
							  narmalMargin = monthBeginMargin;
				    			//normal Margin for Each Day if monthStart and MonthEnd is Different
				    			if(monthBeginMargin.compareTo(monthEndMargin) != 0){
				    				inputRateAmt.put("fromDate",supplyDate );
				    				facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
						    	    if (ServiceUtil.isError(facilityRateResult)) {
						    			generationFailed = true;
						    			Debug.logWarning("There was an error while creating  the TransporterCommission: " + ServiceUtil.getErrorMessage(result), module);
						        		return ServiceUtil.returnError("There was an error while creating the TransporterCommission: " + ServiceUtil.getErrorMessage(result));          	            
						            }
						    	    if(UtilValidate.isNotEmpty(facilityRateResult)){
						    	    	narmalMargin = (BigDecimal) facilityRateResult.get("rateAmount");
						    			 uomId=(String)facilityRateResult.get("uomId");
						    		}	
				    			}
							 
							 if(UtilValidate.isNotEmpty(shipmentIds)){
								 tripSize=new BigDecimal(shipmentIds.size());
							 }
							 BigDecimal actualCommision = tripSize.multiply(narmalMargin);
	                         if(uomId.equals("LEN_km")){
	                      	   actualCommision = actualCommision.multiply(facilitySize);
				    		}
	                         /*if(uomId.equals("PER_LTR")){
	                        	   actualCommision = actualCommision.multiply(facilitySize);
	  			    		}*/
							Timestamp lastDay = UtilDateTime.getDayEnd(supplyDate, timeZone, locale);
							routeMarginMap.put("dueAmount", BigDecimal.ZERO);
							
							routeMarginMap.put("commision",actualCommision);
							
							int Days=UtilDateTime.getIntervalInDays(lastDay,monthEnd);
							if(Days==0){						
								Map transporterDuesMap = LmsServices.getTransporterDues(dctx, UtilMisc.toMap("userLogin", userLogin, "fromDate", supplyDate, "thruDate", supplyDate, "facilityId", route));
								BigDecimal transporterDueAmt = BigDecimal.ZERO;
								if(UtilValidate.isNotEmpty(transporterDuesMap)){
									transporterDueAmt =(BigDecimal) transporterDuesMap.get("invoicesTotalAmount");
								}						
								routeMarginMap.put("dueAmount", transporterDueAmt);
							}
							Map tempMap = FastMap.newInstance();
							tempMap.putAll(routeMarginMap);
							dayTotalsMap.put(supplyDate, tempMap);					
						}
						transporterMap.put(route, dayTotalsMap);
					}
					masterList.add(transporterMap);
					//Debug.log("====masterList======="+masterList);
					result =SalesHistoryServices.populateFacilityCommissiions(dctx, context, masterList,periodBillingId);	
					if (ServiceUtil.isError(result)) {
			    		generationFailed = true;
		                Debug.logWarning("There was an error while creating  the TransporterCommission: " + ServiceUtil.getErrorMessage(result), module);
		        		return ServiceUtil.returnError("There was an error while creating the TransporterCommission: " + ServiceUtil.getErrorMessage(result));          	            
		            } 
					if (generationFailed) {
						periodBilling.set("statusId", "GENERATION_FAIL");
					} 
					periodBilling.store();	
					
					Map transporterTradeResultMap=getTransporterTotalsForPeriodBilling(dctx, UtilMisc.toMap("periodBillingId",(Object)periodBillingId));
					Map totalRouteTradingTotalMap=(Map)transporterTradeResultMap.get("routeTradingMap");
					if(UtilValidate.isNotEmpty(totalRouteTradingTotalMap)){
					Iterator routeMarginsIter = totalRouteTradingTotalMap.entrySet().iterator();
					
					while (routeMarginsIter.hasNext()) {
						Map.Entry routeEntry = (Entry) routeMarginsIter.next();	
						String invoiceId="";
						String facilityId = (String) routeEntry.getKey();	
						Map routeValues = (Map) routeEntry.getValue();
						BigDecimal totalMargin = (BigDecimal) routeValues.get("totalMargin");
						
						BigDecimal quantity = BigDecimal.ONE;
						
						GenericValue facilityDetail = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId) ,false);
						
						String partyIdTo = facilityDetail.getString("ownerPartyId");
						
						 if (UtilValidate.isEmpty(invoiceId)) {
				                Map<String, Object> createInvoiceContext = FastMap.newInstance();
				                createInvoiceContext.put("partyId", partyIdTo);
				                createInvoiceContext.put("partyIdFrom", "Company");
				                //createInvoiceContext.put("billingAccountId", billingAccountId);
				                createInvoiceContext.put("invoiceDate", UtilDateTime.nowTimestamp());
				                createInvoiceContext.put("dueDate", monthEnd);
				                createInvoiceContext.put("invoiceTypeId", "TRANSPORTER_OUT");
				                createInvoiceContext.put("referenceNumber", "TRSPT_MRGN_"+periodBillingId);
				                // start with INVOICE_IN_PROCESS, in the INVOICE_READY we can't change the invoice (or shouldn't be able to...)
				                createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
				                createInvoiceContext.put("currencyUomId", "INR");
				                createInvoiceContext.put("userLogin", userLogin);
	
				                // store the invoice first
				                Map<String, Object> createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
				                	if (ServiceUtil.isError(result)) {
				    		    		generationFailed = true;
				    	                Debug.logWarning("There was an error while creating  Invoice For TransporterCommission: " + ServiceUtil.getErrorMessage(result), module);
				    	        		return ServiceUtil.returnError("There was an error while creating Invoice for  TransporterCommission: " + ServiceUtil.getErrorMessage(result));          	            
				    	            } 
				    				if (generationFailed) {
				    					periodBilling.set("statusId", "GENERATION_FAIL");
				    					periodBilling.store();	
				    				} 
				                // call service for creation invoice);
				                invoiceId = (String) createInvoiceResult.get("invoiceId");
				                Map<String, Object> resMap = null;
				                Map<String, Object> invoiceItemAssocResultMap = null;
				                    resMap = dispatcher.runSync("createInvoiceItem", UtilMisc.toMap("invoiceId", invoiceId,"invoiceItemTypeId", "TRANSPORTER_INV_ITEM","quantity",quantity,"amount", totalMargin,"userLogin", userLogin));
				                    if (ServiceUtil.isError(result)) {
				                    	generationFailed = true;
				    	                Debug.logWarning("There was an error while creating  the InvoiceItem: " + ServiceUtil.getErrorMessage(result), module);
				                    }
				                    BigDecimal tdsMargin= totalMargin.divide(new BigDecimal(10));//for tax
				                    resMap = dispatcher.runSync("createInvoiceItem", UtilMisc.toMap("invoiceId", invoiceId,"invoiceItemTypeId", "TDS_194H","quantity",quantity,"amount", tdsMargin,"userLogin", userLogin));
				                    if (ServiceUtil.isError(result)) {
				                    	generationFailed = true;
				    	                Debug.logWarning("There was an error while creating  the TDS InvoiceItem: " + ServiceUtil.getErrorMessage(result), module);
				                    }
				            }
						
					}//end of while
				}
					
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
					
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
		
		
		public static Map<String, Object> cancelTransporterMarginReport(DispatchContext dctx, Map<String, ? extends Object> context) {
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
	    	try{
	    		periodBilling.store();    		
	    	}catch (Exception e) {
	    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
	    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
			}
	    	try{
	    		Map<String,  Object> inputContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"userLogin", userLogin);
				dispatcher.runAsync("cancelTransporterMarginInvoice", inputContext);
	    	} catch (GenericServiceException e) {
	            Debug.logError(e, "Error in canceling 'transporterMargin' service", module);
	            return ServiceUtil.returnError(e.getMessage());
	        } 
	        return result;
	    }
		   
		
		
		 public static Map<String, Object> cancelTransporterMarginInvoice(DispatchContext dctx, Map<String, ? extends Object> context) {
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
				List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company"));
				  conditionList.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS,  "TRSPT_MRGN_"+periodBillingId));
				  
			     conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "TRANSPORTER_OUT"));
			    /* conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
			     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));*/
	             conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED")); 
	            
	        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
	        	List<GenericValue> invoiceRows = delegator.findList("Invoice", condition, null, null, null, false);
		        List invoiceIdsList = EntityUtil.getFieldListFromEntityList(invoiceRows, "invoiceId", false);
		        Debug.log("==invoiceIdsList======Before==Cacnelation==="+invoiceIdsList);
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
	             	
		    	    GenericValue tenantConfiguration = null;
		        	tenantConfiguration = delegator.findOne("TenantConfiguration",UtilMisc.toMap("propertyName", "enableTranporterDuePayment", "propertyTypeEnumId", "LMS"),false);
		        	tenantConfiguration.set("propertyValue", "Y");
		        	tenantConfiguration.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
		        	tenantConfiguration.store(); 
		        	
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
		    
		    public static Map<String, Object> cancelVendorMarginReport(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();       
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        Map<String, Object> result = new HashMap<String, Object>();
		        String periodBillingId = (String) context.get("periodBillingId");
		        GenericValue periodBilling = null;
		        try{
		        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
		        }catch (GenericEntityException e) {
		    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
		    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
				}        
		    	periodBilling.set("statusId", "COM_CANCELLED");
		    	try{
		    		periodBilling.store();    		
		    	}catch (Exception e) {
		    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
		    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
				}
		        return result;
		    }
		    
		     
		    
		    
		    public static Map<String, Object> getTransporterMarginDuesForPeriodBilling(DispatchContext dctx, Map<String, Object> context) {
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
		        		dayWiseRouteMargins = delegator.findList("FacilityCommission", condition, null,null, null, false);	        		
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
		    public static Map<String, Object> makeVendorMarginPaymentAdjustments(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = ServiceUtil.returnSuccess();
				String periodBillingId = (String) context.get("periodBillingId");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String userLoginId = (String)userLogin.get("userLoginId");
				String boothId = null;
				BigDecimal marginAmount = BigDecimal.ZERO;
				BigDecimal marginAmt = BigDecimal.ZERO;
				BigDecimal dueAmount = BigDecimal.ZERO;
				BigDecimal payAmount = BigDecimal.ZERO;
				RoundingMode rounding = RoundingMode.HALF_UP;
				Map<String, Object> paymentMap = FastMap.newInstance();
				boolean adjustmentFailed = false;
				Map<String, Object> boothTradingMap = FastMap.newInstance();
				List<GenericValue> dayWiseBoothDues = FastList.newInstance();
				Map<String, Object> duesMap = FastMap.newInstance();
				Map<String, Object> boothDuesMargin = FastMap.newInstance();
				Map marginDuesMap = FastMap.newInstance();
				GenericValue periodBilling = null;
		        Map<String, Object> inMap = FastMap.newInstance();
				inMap.put("periodBillingId",periodBillingId);
				inMap.put("userLogin",userLogin);
				try{
					boothTradingMap = dispatcher.runSync("getBoothMarginDuesForPeriodBilling", inMap);
					if(ServiceUtil.isError(boothTradingMap)){
						Debug.logError("Unable to fetch booth margins and dues", module);
						return ServiceUtil.returnError("Unable to fetch booth margins and dues");
					}
					boothDuesMargin = (Map)boothTradingMap.get("boothTradingMap");
				}catch(GenericServiceException e){
					Debug.logError(e, module);
				}
				try{
	    	       	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
	    	       	for(Map.Entry<String, Object> entry : boothDuesMargin.entrySet()){
	    	       		Map<String, Object> paymentInputMap = FastMap.newInstance();
	    	       		boothId = (String)entry.getKey();
	    	       		marginDuesMap = (Map)entry.getValue();
	    	       		marginAmount = ((BigDecimal)marginDuesMap.get("totalMargin")).setScale(0,rounding);
	    	       		dueAmount = (BigDecimal)marginDuesMap.get("totalDue");
	    	       		if(marginAmount.compareTo(BigDecimal.ZERO) == 0 || dueAmount.compareTo(BigDecimal.ZERO) == 0){
	    	       			continue;
	    	       		}
	    	       		else{
	    	       			if(marginAmount.compareTo(dueAmount) > 0){
	        	       			payAmount = dueAmount;
	        	       		}
	        	       		else if(marginAmount.compareTo(dueAmount) < 0){
	        	       			payAmount = marginAmount;
	        	       		}
	        	       		else{
	        	       			payAmount = dueAmount;
	        	       		}
	        	       		String paymentRef = "MRG_ADJUST_"+periodBillingId;
	        	       		paymentInputMap.put("userLogin", userLogin);
	        	       		paymentInputMap.put("facilityId",boothId);
	        	       		paymentInputMap.put("supplyDate",UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"));
	        	       		paymentInputMap.put("paymentMethodTypeId","LMS_CREDITNOTE_PAYIN");
	        	       		paymentInputMap.put("amount",payAmount.toString());
	        	       		paymentInputMap.put("paymentRefNum",paymentRef);
	        	       		paymentInputMap.put("useFifo",true);
	        	       		if(payAmount.compareTo(BigDecimal.ZERO) > 0){
	        	       			try{
	        	       				paymentMap = dispatcher.runSync("createPaymentForBooth", paymentInputMap);
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
					routeTradingMap = dispatcher.runSync("getTransporterMarginDuesForPeriodBilling", inMap);				
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
	        	GenericValue tenantConfiguration = null;
	    		try{        
	            	tenantConfiguration = delegator.findOne("TenantConfiguration",UtilMisc.toMap("propertyName", "enableTranporterDuePayment", "propertyTypeEnumId", "LMS"),false);
	            	tenantConfiguration.set("propertyValue", "Y");
	            	tenantConfiguration.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
	            	tenantConfiguration.store();   
	            }catch(GenericEntityException e){
	            	Debug.logError("Unable to set tenant configuration  record in database"+e, module);
	        		return ServiceUtil.returnError("Unable to set tenant configuration  record in database "); 
	            }
	        	return result;
			}
		    public static Map<String, Object> cancelTransporterMarginAdjustment(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();       
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        String userLoginId = (String)userLogin.get("userLoginId");
		        Map<String, Object> result = new HashMap<String, Object>();
		        String periodBillingId = (String) context.get("periodBillingId");
		        GenericValue periodBilling = null;
		        try{
		        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
		        	periodBilling.set("statusId", "CANCEL_ADJ_INPROCESS");
		        	periodBilling.store();
		        }catch(GenericEntityException e){
		        	Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	        		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		        }
		        Map<String, Object> inMap = FastMap.newInstance();
				inMap.put("periodBillingId",periodBillingId);
				inMap.put("userLogin",userLogin);
		        try{
		        	dispatcher.runAsync("cancelTransporterMarginAdjustmentPayment",inMap);
		        }catch(GenericServiceException e){
		        	Debug.logError("Unable to cancel the adjustment"+e, module);
		    		return ServiceUtil.returnError("Unable to cancel margin adjustment in Payments");
		        }
		        return result;
		    }
		    public static Map<String, Object> cancelTransporterMarginAdjustmentPayment(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();       
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        String userLoginId = (String)userLogin.get("userLoginId");
		        Map<String, Object> result = new HashMap<String, Object>();
		        String periodBillingId = (String) context.get("periodBillingId");
		        String paymentRefNum = "MRG_ADJUST_"+periodBillingId;
		        Map<String, Object> cancelResults = FastMap.newInstance();
		        GenericValue periodBilling = null;
		        List<GenericValue> paymentAdjustmentList = null;
		        
		        List conditionList= FastList.newInstance();        	
	        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_RECEIVED"));
	        	conditionList.add(EntityCondition.makeCondition("paymentRefNum", EntityOperator.EQUALS, paymentRefNum));
	        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	        	try{
	        		paymentAdjustmentList = delegator.findList("Payment",condition,null,null,null,false);
	        	}catch(GenericEntityException e){
	        		Debug.logError("Unable to get Payment record from DataBase"+e, module);
	        		return ServiceUtil.returnError("Unable to get Payment record from DataBase "); 
	        	}
	        	String paymentId = null;
	        	if(paymentAdjustmentList.size()>0){
	        		for(GenericValue eachPayment : paymentAdjustmentList){
	        			Map<String, Object> inMap = FastMap.newInstance();
	        			paymentId = (String)eachPayment.get("paymentId");
	        			inMap.put("paymentId", paymentId);
	        			inMap.put("userLogin", userLogin);
	        			try{
	        				cancelResults = dispatcher.runSync("voidPayment",inMap);
	        				if (ServiceUtil.isError(cancelResults)) {
	        					throw new GenericServiceException(ServiceUtil.getErrorMessage(cancelResults));
	        					//return ServiceUtil.returnError("Cancel Margin Adjustment Failed");
	                        }
	        			}catch(GenericServiceException e){
	        				Debug.logError("Unable to cancel the adjustment"+e, module);
	        	    		return ServiceUtil.returnError("Unable to cancel margin adjustment in Payments");
	        			}
	        		}
	        	}
	        	try{
		        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
		        	periodBilling.set("statusId", "GENERATED");
		        	periodBilling.store();
		        }catch(GenericEntityException e){
		        	Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	        		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		        }
		        result = ServiceUtil.returnSuccess("Transporter Margins Adjustment Cancelled Sucessfully");
		        GenericValue tenantConfiguration = null;
				try{        
		        	tenantConfiguration = delegator.findOne("TenantConfiguration",UtilMisc.toMap("propertyName", "enableTranporterDuePayment", "propertyTypeEnumId", "LMS"),false);
		        	tenantConfiguration.set("propertyValue", "N");
		        	tenantConfiguration.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
		        	tenantConfiguration.store();        	
		        
		        }catch(GenericEntityException e){
		        	Debug.logError("Unable to set tenant configuration  record in database"+e, module);
		    		return ServiceUtil.returnError("Unable to set tenant configuration  record in database "); 
		        }
		        return result;
		    }
		    public static Map<String, Object> CancelMarginAdjustment(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();       
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        String userLoginId = (String)userLogin.get("userLoginId");
		        Map<String, Object> result = new HashMap<String, Object>();
		        String periodBillingId = (String) context.get("periodBillingId");
		        GenericValue periodBilling = null;
		        try{
		        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
		        	periodBilling.set("statusId", "CANCEL_ADJ_INPROCESS");
		        	periodBilling.store();
		        }catch(GenericEntityException e){
		        	Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	        		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		        }
		        Map<String, Object> inMap = FastMap.newInstance();
				inMap.put("periodBillingId",periodBillingId);
				inMap.put("userLogin",userLogin);
		        try{
		        	dispatcher.runAsync("cancelMarginAdjustmentPayment",inMap);
		        }catch(GenericServiceException e){
		        	Debug.logError("Unable to cancel the adjustment"+e, module);
		    		return ServiceUtil.returnError("Unable to cancel margin adjustment in Payments");
		        }
		        return result;
		    }
		    public static Map<String, Object> cancelMarginAdjustmentPayment(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();       
		        GenericValue userLogin = (GenericValue) context.get("userLogin");
		        String userLoginId = (String)userLogin.get("userLoginId");
		        Map<String, Object> result = new HashMap<String, Object>();
		        String periodBillingId = (String) context.get("periodBillingId");
		        String paymentRefNum = "MRG_ADJUST_"+periodBillingId;
		        Map<String, Object> cancelResults = FastMap.newInstance();
		        GenericValue periodBilling = null;
		        List<GenericValue> paymentAdjustmentList = null;
		        
		        List conditionList= FastList.newInstance();
		        conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "LMS_CREDITNOTE_PAYIN"));
	        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_RECEIVED"));
	        	conditionList.add(EntityCondition.makeCondition("paymentRefNum", EntityOperator.EQUALS, paymentRefNum));
	        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	        	try{
	        		paymentAdjustmentList = delegator.findList("Payment",condition,null,null,null,false);
	        	}catch(GenericEntityException e){
	        		Debug.logError("Unable to get Payment record from DataBase"+e, module);
	        		return ServiceUtil.returnError("Unable to get Payment record from DataBase "); 
	        	}
	        	String paymentId = null;
	        	if(paymentAdjustmentList.size()>0){
	        		for(GenericValue eachPayment : paymentAdjustmentList){
	        			Map<String, Object> inMap = FastMap.newInstance();
	        			paymentId = (String)eachPayment.get("paymentId");
	        			inMap.put("paymentId", paymentId);
	        			inMap.put("userLogin", userLogin);
	        			try{
	        				cancelResults = dispatcher.runSync("voidPayment",inMap);
	        				if (ServiceUtil.isError(cancelResults)) {
	        					throw new GenericServiceException(ServiceUtil.getErrorMessage(cancelResults));
	        					//return ServiceUtil.returnError("Cancel Margin Adjustment Failed");
	                        }
	        			}catch(GenericServiceException e){
	        				Debug.logError("Unable to cancel the adjustment"+e, module);
	        	    		return ServiceUtil.returnError("Unable to cancel margin adjustment in Payments");
	        			}
	        		}
	        	}
	        	try{
		        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
		        	periodBilling.set("statusId", "GENERATED");
		        	periodBilling.store();
		        }catch(GenericEntityException e){
		        	Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	        		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		        }
		        result = ServiceUtil.returnSuccess("Vendor Margins Adjustment Cancelled Sucessfully");	        
		        return result;
		    }
		    
		    
		    public static Map<String, Object> transporterMarginAdjustments(DispatchContext dctx, Map<String, ? extends Object> context) {
		    	Delegator delegator = dctx.getDelegator();
		    	LocalDispatcher dispatcher = dctx.getDispatcher();
		    	Map<String, Object> result = ServiceUtil.returnSuccess();
		    	TimeZone timeZone = TimeZone.getDefault();
				Locale locale = Locale.getDefault();
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String customTimePeriodId = (String) context.get("customTimePeriodId");
				String periodBillingId = (String) context.get("periodBillingId");			
				GenericValue periodBilling = null;
				String userLoginId = (String)userLogin.get("userLoginId");
				String statusId = null;
				try{
		        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);       	
		        	
		        	if(UtilValidate.isEmpty(periodBilling)){
		        		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		        	}
		        	if(!(periodBilling.get("statusId")).equals("GENERATED")){
			        		Debug.logError("PeriodBilling should be in Generated State", module);
		        			return ServiceUtil.returnError("PeriodBilling should be in Generated Status");
		        	}
	        		periodBilling.set("statusId", "ADJUST_IN_PROCESS");
		        	periodBilling.set("lastModifiedByUserLogin",userLoginId);
		        	periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		        	periodBilling.store();
		        }catch (GenericEntityException e) {
		    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
		    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
				}
				try{
					Map<String,  Object> adjustInputMap = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId, "userLogin", userLogin);
					dispatcher.runAsync("makeTransporterMarginPaymentAdjustments", adjustInputMap);
				}
				catch (GenericServiceException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());			
				}	 
				return result;	
		    }

		    public static Map<String, Object> getTransporterTotalsForPeriodBilling(DispatchContext dctx, Map<String, Object> context) {
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
		        		dayWiseRouteMargins = delegator.findList("FacilityCommission", condition, null,null, null, false);	        		
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
		    
		    
		    
		    public static BigDecimal sum(List<BigDecimal> list) {
		    	BigDecimal sum = BigDecimal.ZERO; 
		        for (BigDecimal i:list)
		        	sum = sum.add(i);
		        return sum;
		    }
	}



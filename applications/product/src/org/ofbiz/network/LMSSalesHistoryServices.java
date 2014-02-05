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
package org.ofbiz.network;

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


import java.text.SimpleDateFormat;

public class LMSSalesHistoryServices {
	
	public static final String module = LMSSalesHistoryServices.class.getName();

	public static Map<String, Object>  sendSalesSummarySms(DispatchContext dctx, Map<String, Object> context)  {
        LocalDispatcher dispatcher = dctx.getDispatcher();		
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		Timestamp yesterdaysTimestamp = UtilDateTime.addDaysToTimestamp(nowTimestamp, -1);		
		Map<String, Object> todaysSalesTotals = DeprecatedNetworkServices.getDayTotals(dctx, nowTimestamp, null, true, false, null);
		Map<String, Object> yesterdaysSalesTotals = DeprecatedNetworkServices.getDayTotals(dctx, yesterdaysTimestamp, null, true, false, null);
		BigDecimal todaysSales = ((BigDecimal)todaysSalesTotals.get("totalQuantity")).setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
		BigDecimal yesterdaysSales = ((BigDecimal)yesterdaysSalesTotals.get("totalQuantity")).setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
		BigDecimal diff = (todaysSales.subtract(yesterdaysSales)).setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
		try {
			// Send SMS notification to list
			String text = "Today's (" + UtilDateTime.toDateString(nowTimestamp, "dd/MM/yyyy") + ") milk sales: " +  
				todaysSales.setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding")) + 
				" ltrs.  Yesterday's milk sales: " +
				yesterdaysSales.setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding")) + 
				" ltrs. [Diff: " + diff.setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding")) + 
				" ltrs].  Message sent by MIS.";
			Debug.logInfo("Sms text: " + text, module);
			Map<String,  Object> sendSmsContext = UtilMisc.<String, Object>toMap("contactListId", "SALES_NOTIFY_LST", 
				"text", text, "userLogin", userLogin);			
			dispatcher.runAsync("sendSmsToContactListNoCommEvent", sendSmsContext);
		}
		catch (GenericServiceException e) {
			Debug.logError(e, "Error calling sendSmsToContactListNoCommEvent service", module);
			return ServiceUtil.returnError(e.getMessage());			
		} 
        return ServiceUtil.returnSuccess("Sms successfully sent!");		
	}
	
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
	
	public static Map<String, Object> LMSSalesHistorySummaryDetail(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		java.util.Date PMDate = null;
		java.util.Date salesDate = (java.util.Date) context.get("salesDate");
		if(salesDate == null){
			salesDate = UtilDateTime.nowDate();
		}
		String salesDateString = salesDate.toString();
		String productId = null;
		ArrayList zonesSalesList = new ArrayList();
		Map productsMap = FastMap.newInstance();
		String tempPMDate = salesDateString;
		// lets check the tenant configuration for enableSameDayPmEntry
		Boolean enableSameDayPmEntry = Boolean.FALSE;
		try{
			 GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSameDayPmEntry"), false);
			 if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry) && (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
				 enableSameDayPmEntry = Boolean.TRUE;
			}
		 }catch (GenericEntityException e) {
			// TODO: handle exception
			 Debug.logError(e, module);
		}
		if(!enableSameDayPmEntry){
			tempPMDate = UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(salesDate), -1), "yyyy-MM-dd");
		}
		
		try{
			Map<String, Object> AMSalesTotals = DeprecatedNetworkServices.getDayTotals(dctx, UtilDateTime.toTimestamp(salesDate), "AM", false, false, null);
			populateTotals(dctx, context, salesDateString, AMSalesTotals, "AM" );
			
			Map<String, Object> PMSalesTotals = DeprecatedNetworkServices.getDayTotals(dctx, UtilDateTime.toTimestamp(salesDate), "PM", false, false, null);
			//for 'PM' supply type getDayTotals return the previous day PM Totals if  enableSameDayPmEntry set 'TRUE' 
			populateTotals(dctx, context, tempPMDate, PMSalesTotals, "PM");
		
		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil
			.returnError("Error while finding SalesTotals" + e);
		}
		return result;
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
		try{        
        	tenantConfiguration = delegator.findOne("TenantConfiguration",UtilMisc.toMap("propertyName", "enableTranporterDuePayment", "propertyTypeEnumId", "LMS"),false);
        	tenantConfiguration.set("propertyValue", "N");
        	tenantConfiguration.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
        	tenantConfiguration.store();        
        }catch(GenericEntityException e){
        	Debug.logError("Unable to set tenant configuration  record in database"+e, module);
    		return ServiceUtil.returnError("Unable to set tenant configuration  record in database "); 
        }
		GenericValue customTimePeriod;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, e1.getMessage());
			return ServiceUtil.returnError("Error in customTimePeriod" + e1);
		}
		if(customTimePeriod == null){
			generationFailed = true;
		}
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
		Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
		
		int totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);	
		Map routes = DeprecatedNetworkServices.getRoutes(dctx , UtilMisc.toMap("facilityTypeId", "ROUTE"));		
		List routesList = (List) routes.get("routesList");
		Map transporterMap =FastMap.newInstance();
		try {
			for (int i = 0; i < routesList.size(); i++) {
				String route = (String) routesList.get(i);
				Map dayTotalsMap = FastMap.newInstance();
				Timestamp supplyDate = monthBegin;
		        for (int k = 0; k <= (totalDays); k++) {
					Map routeMarginMap = FastMap.newInstance();
					supplyDate = UtilDateTime.addDaysToTimestamp(monthBegin, k);					
					
					//getting the Valid agents which are in the particular route 
					
					List conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS , route));
			        conditionList.add((EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO , supplyDate)));
			        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, supplyDate)));
			        
			       EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			       EntityFindOptions findOptions = new EntityFindOptions();
			        //findOptions.setMaxRows(2);
					List<GenericValue> boothsList = delegator.findList("FacilityParty", condition, null, null, findOptions, false);
			        Set agentIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", true));
					List agentIds = new ArrayList(agentIdsSet);
					
					if(UtilValidate.isNotEmpty(agentIds)){
					Map dayTotals = DeprecatedNetworkServices.getDayTotals(dispatcher.getDispatchContext(), supplyDate, null, true, false, UtilMisc.toList(agentIds));
					BigDecimal saleAmount = (BigDecimal)dayTotals.get("totalRevenue");
					routeMarginMap.put("quantity", dayTotals.get("totalQuantity"));
					routeMarginMap.put("saleAmount", saleAmount);					
					}					
					Timestamp lastDay = UtilDateTime.getDayEnd(supplyDate, timeZone, locale);
					routeMarginMap.put("dueAmount", BigDecimal.ZERO);
					int Days=UtilDateTime.getIntervalInDays(lastDay,monthEnd);
					if(Days==0){						
						Map transporterDuesMap = LmsServices.getTransporterDues(dctx, UtilMisc.toMap("userLogin", userLogin, "fromDate", monthBegin, "thruDate", monthEnd, "facilityId", route));
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
			result = populateFacilityCommissiions(dctx, context, masterList,periodBillingId);			
			GenericValue periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
			if (generationFailed) {
				periodBilling.set("statusId", "GENERATION_FAIL");
			} else {
				periodBilling.set("statusId", "GENERATED");
				periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			}
			periodBilling.store();						
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}
		return resultValue;		
	}
	public static Map<String, Object> cancelTransporterMarginReport(DispatchContext dctx, Map<String, ? extends Object> context) {
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
	 public static Map<String, Object> generateVendorMargin(DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = dctx.getDelegator();
	    	LocalDispatcher dispatcher = dctx.getDispatcher();
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	TimeZone timeZone = TimeZone.getDefault();
			Locale locale = Locale.getDefault();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String customTimePeriodId = (String) context.get("customTimePeriodId");
			String periodBillingId = (String) context.get("periodBillingId");
			RoundingMode rounding = RoundingMode.HALF_UP;
			boolean generationFailed = false;
			
			GenericValue customTimePeriod;
			try {
				customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			} catch (GenericEntityException e1) {
				Debug.logError(e1, e1.getMessage());
				return ServiceUtil.returnError("Error in customTimePeriod" + e1);
			}
			if(customTimePeriod == null){
				generationFailed = true;
			}
			Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
			Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
			Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
			
			int totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
			BigDecimal suppDaysBD = new BigDecimal(totalDays+1);
			
			List masterList = new ArrayList();
			Map boothMarginRates = FastMap.newInstance();
			BigDecimal dailyMarginValue = BigDecimal.ZERO;
			BigDecimal cashDue = BigDecimal.ZERO;
			
			List conditionList = UtilMisc.toList(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS, "SUB_PROD_TYPE"));
	        conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL ,"CREDIT"));
	        conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL ,"CASH_FS"));
	        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	        List<GenericValue> productSubscriptionTypeList;
			try {
				productSubscriptionTypeList = delegator.findList("Enumeration", condition, UtilMisc.toSet("enumId"), null, null, false);
				
		        conditionList.clear();
		        
		        conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"BOOTH"));
		        conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS ,"VENDOR"));
		        EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        List<GenericValue> boothsList = delegator.findList("Facility",condition1,null,UtilMisc.toList("parentFacilityId","facilityId"),null,false);
		        conditionList.clear();
		        
		        // Get Sachets Product Ids
		        List sachetsProdIds = FastList.newInstance();
		        List<GenericValue> productList = DeprecatedNetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
		        
		        for (GenericValue prod : productList) {
		        	String prodId = (String) prod.get("productId");
		        	
		        	conditionList = UtilMisc.toList(EntityCondition.makeCondition("acctgFormulaId", EntityOperator.EQUALS, "LMS_VOL_INC_"+prodId));
					EntityCondition acctngFormulaCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					
					List<GenericValue> acctgFormulaIds = delegator.findList("AcctgFormula",acctngFormulaCond,UtilMisc.toSet("acctgFormulaId"),null,null,false);
					conditionList.clear();
					
					if(UtilValidate.isEmpty(acctgFormulaIds)){
						sachetsProdIds.add(prodId);
					}
		        }
		    	HashSet productIdListSet = new HashSet(sachetsProdIds);
		        
		        // Get Fridge Recovery Amount
		        Set<String> fieldsToSelect = UtilMisc.toSet("invoiceId", "facilityId");
		        conditionList = UtilMisc.toList(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
			        conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
			        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL ,"INVOICE_CANCELLED"));
			        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL ,"INVOICE_WRITEOFF"));
			        conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS ,"LMS_RECOVERY_IN"));
		        
		        EntityCondition recoveryAmtCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        List<GenericValue> recoveryInvoices = delegator.findList("Invoice",recoveryAmtCondition,fieldsToSelect,UtilMisc.toList("invoiceId","facilityId"),null,false);
		        conditionList.clear();
		        Map boothRecoveryMap = FastMap.newInstance();
		        BigDecimal recoveryAmt = BigDecimal.ZERO;
		        String recoveryBooth = null;
		        
		        if(!UtilValidate.isEmpty(recoveryInvoices)){
		        	for (GenericValue recoveryInvoice : recoveryInvoices) {
		        		String invoiceId = (String) recoveryInvoice.get("invoiceId");
		        		recoveryBooth = (String) recoveryInvoice.get("facilityId");
		        		if(UtilValidate.isEmpty(recoveryBooth)){
		        			continue;
		        		}
		        		Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "invoiceId", recoveryInvoice.get("invoiceId"));
		    			Map<String, Object> invoicesResult = dispatcher.runSync("getInvoiceTotal", input);
		        		if (ServiceUtil.isError(invoicesResult)) {
		        			generationFailed = true;
		        			Debug.logError(ServiceUtil.getErrorMessage(invoicesResult), module);
		                }
		        		recoveryAmt = (BigDecimal) invoicesResult.get("amountTotal");
		        		if(UtilValidate.isEmpty(boothRecoveryMap.get(recoveryBooth))){
		        			boothRecoveryMap.put(recoveryBooth, recoveryAmt);
		        		}else{
		        			BigDecimal updateRecoveryAmt = (BigDecimal) boothRecoveryMap.get(recoveryBooth);
		        			updateRecoveryAmt = updateRecoveryAmt.add(recoveryAmt);
		        			boothRecoveryMap.put(recoveryBooth, updateRecoveryAmt);
		        		}
		        	}
		        }
		        // Get PeriodWise Product Totals for incentive calculations
		        List boothIdsList = EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", false);
		        
		        Map boothPeriodTotals = FastMap.newInstance();
		        
		        Map<String, Object> periodDayTotals = DeprecatedNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), UtilMisc.<String, Object>toMap("facilityIds", UtilMisc.toList(boothIdsList), "fromDate", fromDateTime, "thruDate", thruDateTime));
		        Map boothProdTotals = (Map)periodDayTotals.get("boothTotals");
		        
		        BigDecimal periodTotalQty = BigDecimal.ZERO;
				Iterator boothTotalsIter = boothProdTotals.entrySet().iterator();
		        while (boothTotalsIter.hasNext()) {
					Map.Entry boothProdEntry = (Entry) boothTotalsIter.next();
					
					Map periodProdTotalsMap = FastMap.newInstance();
					Map ProdTot = (Map) boothProdEntry.getValue();
					Map periodProductTotals = (Map) ProdTot.get("productTotals");
					Iterator productTotalsIter = periodProductTotals.entrySet().iterator();
					while (productTotalsIter.hasNext()) {
						Map.Entry productEntry = (Entry) productTotalsIter.next();
						Map prodDetailMap = FastMap.newInstance();
						prodDetailMap = (Map) (productEntry.getValue());
						
						if(productIdListSet.contains(productEntry.getKey())){
							periodTotalQty = periodTotalQty.add((BigDecimal)(prodDetailMap.get("total")));
						}
						periodProdTotalsMap.put(productEntry.getKey(), prodDetailMap.get("total"));
						
					}
					periodProdTotalsMap.put("totalIncProds", periodTotalQty);
					periodTotalQty = BigDecimal.ZERO;
					Map tempPeriodMap = FastMap.newInstance();
					tempPeriodMap.putAll(periodProdTotalsMap);
					boothPeriodTotals.put(boothProdEntry.getKey(), tempPeriodMap);
					periodProdTotalsMap.clear();
					
				}
		        conditionList.clear();
		        
		        conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, "100"));
		        //conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, "MARGIN_PRICE"));
		        conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(DeprecatedNetworkServices.getLmsProducts(dctx, context),"productId" ,false)));
		        EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		        List<GenericValue> productPriceList =delegator.findList("ProductAndPriceView", discontinuationDateCondition, null, null, null, false);
		        Map productMarginPriceMap = FastMap.newInstance();
		        
		        Set excludeIncProdSet = FastSet.newInstance();
		        // Here we are populating product wise margins if  any
		        for (GenericValue productPrice : productPriceList) {
	    			
		        	//lets populate  Margin price for the products
		        	if((productPrice.getString("productPriceTypeId")).equals("MARGIN_PRICE")){
		        		productMarginPriceMap.put(productPrice.get("productId"), productPrice.get("price"));
		        	}
		        	//lets populate  Set with the products if any has isExcludeFromIncTotal flag true
		        	if(UtilValidate.isNotEmpty(productPrice.getBoolean("isExcludedFromIncTotal")) && productPrice.getBoolean("isExcludedFromIncTotal")){		        		
		        		excludeIncProdSet.add(productPrice.getString("productId"));
		        	}
		        	
		        }
		        List excludeIncProdList = new ArrayList(excludeIncProdSet);		        
		        Map typeAndCountMap = FastMap.newInstance();
		       
		        for (GenericValue productSubscriptionTypeEntry : productSubscriptionTypeList) {
		        	typeAndCountMap.put(productSubscriptionTypeEntry.get("enumId"), BigDecimal.ZERO);
		        }
		        typeAndCountMap.put("TOTAL", BigDecimal.ZERO);
		        
		        for (GenericValue productSubscriptionTypeEntry : productSubscriptionTypeList) {
		        	typeAndCountMap.put(productSubscriptionTypeEntry.get("enumId")+"_MR", BigDecimal.ZERO);
		        }
		        typeAndCountMap.put("TOTAL_MR", BigDecimal.ZERO);
		        typeAndCountMap.put("CASH_DUE", BigDecimal.ZERO);
		        typeAndCountMap.put("RECOVERY", BigDecimal.ZERO);
		    	BigDecimal incentiveValue = BigDecimal.ZERO;
		    	Timestamp supplyDate = monthBegin;
		    	Map boothMargins= new LinkedHashMap();
		    	
		    	Map boothSupplyDays = FastMap.newInstance();
		    	
		    	for (GenericValue booth : boothsList) {
		    		
		    		List vendorMarginReportList = new ArrayList();
		    		Map dayTotalsMap = FastMap.newInstance();
		    		GenericValue facilityDetail = delegator.findOne("Facility",UtilMisc.toMap("facilityId", booth.get("facilityId")) ,false);
		    		Timestamp suppDate = monthBegin;
		    		
		    		BigDecimal recovery = BigDecimal.ZERO;
		    		if(!UtilValidate.isEmpty(boothRecoveryMap.get(booth.get("facilityId")))){
		    			recovery = (BigDecimal) boothRecoveryMap.get(booth.get("facilityId"));
		    		}
		    		
		    		// getting previous dues if any	
		    		Map<String, Object> boothPayments = DeprecatedNetworkServices.getBoothPayments(delegator, dctx.getDispatcher(), userLogin,
		    				UtilDateTime.toDateString(monthEnd, "yyyy-MM-dd"), null, (String) booth.get("facilityId") ,null ,Boolean.FALSE);
	    			Map boothTotalDues = FastMap.newInstance();
	    			List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
	    			if (boothPaymentsList.size() != 0) {
	    				 boothTotalDues = (Map)boothPaymentsList.get(0);
	    			}
	    			cashDue = (BigDecimal)boothTotalDues.get("totalDue");
	    			
		    		for(int i=1 ; i <= (totalDays+1); i++){
		    			dayTotalsMap.put(suppDate, 0);
		    			Map tempMap = FastMap.newInstance();
		    			tempMap.putAll(typeAndCountMap);
		    			dayTotalsMap.put(suppDate, tempMap);
		    			int suppDateInt = UtilDateTime.getDayOfMonth(suppDate,timeZone, locale);
		    			int monthEndInt = UtilDateTime.getDayOfMonth(monthEnd,timeZone, locale);
		    			if(suppDateInt == monthEndInt){
		    				Map tempRecoveryMap = FastMap.newInstance();
		    				tempRecoveryMap = (Map) dayTotalsMap.get(suppDate);
		    				tempRecoveryMap.put("RECOVERY", recovery);
		    				dayTotalsMap.put(suppDate, tempRecoveryMap);
		    				
		    				Map tempCashDueMap = FastMap.newInstance();
		    				tempCashDueMap = (Map) dayTotalsMap.get(suppDate);
		    				tempCashDueMap.put("CASH_DUE", cashDue);
		    				dayTotalsMap.put(suppDate, tempCashDueMap);
		    			}
		    			suppDate = UtilDateTime.getNextDayStart(suppDate);
		    		}
	    			vendorMarginReportList.add(dayTotalsMap);
	        		boothMargins.put(booth.get("facilityId"), vendorMarginReportList);
	        		
	        		//get booth specfic Margins if any
		        	Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
		    		inputRateAmt.put("rateTypeId", "VENDOR_MRGN");
		    		inputRateAmt.put("periodTypeId", "RATE_HOUR");
		    		inputRateAmt.put("partyId", facilityDetail.get("ownerPartyId"));
		    		inputRateAmt.put("rateCurrencyUomId", context.get("currencyUomId"));
		    		Map<String, Object> rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);
		    		if (ServiceUtil.isError(rateAmount)) {
		    			generationFailed = true;
		            }
		    		BigDecimal normalMargin = (BigDecimal) rateAmount.get("rateAmount");
		    		inputRateAmt.put("rateTypeId", "VENDOR_CD_ADTL_MRGN");
		    		Map<String, Object> rateAmountForCard = dispatcher.runSync("getRateAmount", inputRateAmt);
		    		if (ServiceUtil.isError(rateAmountForCard)) {
		    			generationFailed = true;
		            }
		    		BigDecimal cardMargin = (BigDecimal) rateAmountForCard.get("rateAmount");
		    		
		    		Map marginMap = FastMap.newInstance();
		    		marginMap.put("normalMargin", normalMargin);
		    		marginMap.put("cardMargin", cardMargin);
		    		marginMap.put("excludeIncentive", 0);
		    		String excludeIncentive = (String) facilityDetail.get("excludeIncentive");
		    		if(excludeIncentive != null){
		    		   marginMap.put("excludeIncentive", excludeIncentive);
		    		}
		    		boothMarginRates.put(booth.get("facilityId"), 0);
		    		Map tempMarginMap = FastMap.newInstance();
		    		tempMarginMap.putAll(marginMap);
		    		boothMarginRates.put(booth.get("facilityId"), tempMarginMap);
		    	}
		    	
		    	Boolean isOpeningBalanceDay = true;
		    	
		    	for(int i=1 ; i <= (totalDays+1); i++){
		    		int dayOfMonth = i;
		    		Map<String, Object> dayTotals = DeprecatedNetworkServices.getDayTotals(dispatcher.getDispatchContext(), supplyDate, "AM" , false, false, EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", false));
		    		Map boothTotals = (Map) dayTotals.get("boothTotals");
		    		if(boothTotals == null){
		    			continue;
		    		}
		    		Iterator treeMapBoothIter = boothTotals.entrySet().iterator();
		    		while (treeMapBoothIter.hasNext()) {
		    			Map.Entry boothEntry = (Entry) treeMapBoothIter.next();
		    			Map marginMap = FastMap.newInstance();
		    			marginMap = (Map) boothMarginRates.get(boothEntry.getKey());
		    			BigDecimal normalMargin = (BigDecimal) marginMap.get("normalMargin");
		    			BigDecimal cardMargin = (BigDecimal) marginMap.get("cardMargin");
			    		
		    			List dayTotalsList = (List) boothMargins.get(boothEntry.getKey());
		    			Map dayTotalsMap = (Map) dayTotalsList.get(0);
		    			Map tempProdTot = (Map) boothEntry.getValue();
		    			BigDecimal dayTotalQty = ((BigDecimal) tempProdTot.get("total")).setScale(1,rounding);
		    			BigDecimal dayTotalRevenue = ((BigDecimal) tempProdTot.get("totalRevenue")).setScale(1,rounding);
		    			
		    			Map<String, Object> paidPaymentCtx = UtilMisc.<String, Object>toMap("facilityId", (String)(boothEntry.getKey()));    
			        	paidPaymentCtx.put("paymentDate", UtilDateTime.toDateString(supplyDate, "yyyy-MM-dd"));
						
		    			Map productTotals = (Map) tempProdTot.get("productTotals");		    			
		    			BigDecimal dayIncTotalQty = BigDecimal.ZERO;
		    			BigDecimal prodQty = BigDecimal.ZERO;
		    			
		    			dayIncTotalQty = dayTotalQty;
		    			
		    			// excluding products totals from the incentive totals excludeIncProdSet has products		    			
		    			for(int j=0 ; j < excludeIncProdList.size(); j++){
			    			if(productTotals.get(excludeIncProdList.get(j)) != null){				
			    				Map productMap = (Map) productTotals.get(excludeIncProdList.get(j));
			    				prodQty = (BigDecimal) productMap.get("total");
			    				dayIncTotalQty = dayIncTotalQty.subtract(prodQty);			    				
			    			}
		    			}
		    			
		    			boolean isExcludeIncBooth = false;
		    			if(marginMap.get("excludeIncentive").equals("Y")){
		        			isExcludeIncBooth = true;
		    			}
		    			
		    			BigDecimal IncentiveRateAmount = BigDecimal.ZERO;
		    			
		    			Map periodProductTotals = (Map) boothPeriodTotals.get(boothEntry.getKey());
	    				
	    				BigDecimal prodTotQty = (BigDecimal) periodProductTotals.get("totalIncProds");
	    				BigDecimal prodIncentiveQty = BigDecimal.ZERO;
	    				
	    				prodIncentiveQty = prodTotQty.divide(suppDaysBD, 1);
	    				
	    				Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "acctgFormulaId","LMS_VOL_INCNTV", "variableValues","QUANTITY="+"1", "slabAmount", prodIncentiveQty);
		    			Map<String, Object> incentivesResult = dispatcher.runSync("evaluateAccountFormula", input);
		        		if (ServiceUtil.isError(incentivesResult)) {
		        			generationFailed = true;
		                }
		        		double tempIncentiveValue = (Double) incentivesResult.get("formulaResult");
		        		IncentiveRateAmount = (new java.math.BigDecimal( tempIncentiveValue )).setScale(2,rounding);
		        		
		        		Map typeAndCount = FastMap.newInstance();
		        		typeAndCount = (Map) dayTotalsMap.get(supplyDate);
		    			typeAndCount.put("TOTAL", dayTotalQty.setScale(2, rounding));
		    			BigDecimal totalCashMarginValue = BigDecimal.ZERO; 
		    			BigDecimal totalCardMarginValue = BigDecimal.ZERO; 
		    			BigDecimal totalSOMarginValue = BigDecimal.ZERO; 
		    			BigDecimal cashTotal = BigDecimal.ZERO;
		    			BigDecimal cardTotal = BigDecimal.ZERO;
		    			BigDecimal specialOrderTotal = BigDecimal.ZERO;
		    			
		    			Iterator treeMapIter = productTotals.entrySet().iterator();
		    			while (treeMapIter.hasNext()) {
		    				Map.Entry entry = (Entry) treeMapIter.next();
		    				String productId = (String) entry.getKey();
		    				Map productDetailMap =(Map) entry.getValue();		    				
		    				BigDecimal tempProcAdlDiscValue = BigDecimal.ZERO;
		    				BigDecimal tempProcDiscValue = BigDecimal.ZERO;
		    				BigDecimal tempIncentiveRateAmount = IncentiveRateAmount;
		    				
		    				BigDecimal prodBulkQty = (BigDecimal) periodProductTotals.get(productId);
		    				
		    				BigDecimal prodBulkIncentiveQty = BigDecimal.ZERO;
		    				prodBulkIncentiveQty = prodBulkQty.divide(suppDaysBD, 1);
		    				
		    				if(isExcludeIncBooth){
			    				tempIncentiveRateAmount = BigDecimal.ZERO;
			    			}
		    				
		    				String procAcctgFormulaId = "LMS_VOL_INC_"+productId;	    					
	    					GenericValue procAcctgFormula = delegator.findOne("AcctgFormula",UtilMisc.toMap("acctgFormulaId", procAcctgFormulaId), false);
	    					if(UtilValidate.isNotEmpty(procAcctgFormula)){
	    						Map<String, Object> procAddDiscinput = UtilMisc.toMap("userLogin", userLogin, "acctgFormulaId", procAcctgFormulaId, "variableValues","QUANTITY="+"1", "slabAmount", prodBulkIncentiveQty);
				    			Map<String, Object> procAddDiscResult = dispatcher.runSync("evaluateAccountFormula", procAddDiscinput);				    			
				    			tempProcDiscValue = new java.math.BigDecimal((Double) procAddDiscResult.get("formulaResult"));				    			
				    			tempIncentiveRateAmount = 	tempProcDiscValue;	
				    			if(isExcludeIncBooth){
				    				tempIncentiveRateAmount = BigDecimal.ZERO;
				    			}
	    					}	
	    					String procAdlAcctgFormulaId = "LMS_VOL_INC_ADL_"+productId;	    					
	    					GenericValue procAdlAcctgFormula = delegator.findOne("AcctgFormula",UtilMisc.toMap("acctgFormulaId", procAdlAcctgFormulaId), false);
	    					if(UtilValidate.isNotEmpty(procAdlAcctgFormula)){
	    						Map<String, Object> procAddDiscinput = UtilMisc.toMap("userLogin", userLogin, "acctgFormulaId", procAdlAcctgFormulaId, "variableValues","QUANTITY="+"1", "slabAmount", prodBulkIncentiveQty);
				    			Map<String, Object> procAddDiscResult = dispatcher.runSync("evaluateAccountFormula", procAddDiscinput);				    			
				    			tempProcAdlDiscValue = new java.math.BigDecimal((Double) procAddDiscResult.get("formulaResult"));
				    			if(isExcludeIncBooth){
				    				tempProcAdlDiscValue = BigDecimal.ZERO;
				    			}
	    					}	    
		    				for (GenericValue productSubscriptionType : productSubscriptionTypeList) {		    					
		    					BigDecimal supplyTypeTotalQty = BigDecimal.ZERO;		    					
		    					Map supplyTypeTotalMap = (Map) productDetailMap.get("supplyTypeTotals");
		    					if(productDetailMap != null && supplyTypeTotalMap.get(productSubscriptionType.get("enumId")) != null){
		    						Map supplyTypeDetailMap = (Map) supplyTypeTotalMap.get(productSubscriptionType.get("enumId"));
		    						supplyTypeTotalQty = ((BigDecimal) supplyTypeDetailMap.get("total")).setScale(1, rounding);
		    					}
		    					// Let's get any product specific discounts  if any
		    					
		    					if((productSubscriptionType.get("enumId")).equals("CASH")){
		    						cashTotal = cashTotal.add(supplyTypeTotalQty);
		    						typeAndCount.put(productSubscriptionType.get("enumId"), cashTotal.setScale(2, rounding));
		    						if(productMarginPriceMap.get(productId) != null){
		    							BigDecimal tempMargin =((supplyTypeTotalQty).multiply((BigDecimal) productMarginPriceMap.get(productId))).setScale(2, rounding);
		    							BigDecimal tempMarginValue = tempMargin;
		    							totalCashMarginValue = totalCashMarginValue.add(tempMarginValue);
		    							dailyMarginValue = dailyMarginValue.add(tempMarginValue);
		    							typeAndCount.put(productSubscriptionType.get("enumId")+"_MR", totalCashMarginValue) ;
		    							typeAndCount.put("TOTAL_MR", dailyMarginValue) ;
		    						}else{
		    							BigDecimal tempMargin =(supplyTypeTotalQty.multiply(normalMargin.add(tempProcAdlDiscValue))).setScale(2, rounding);							
		    							BigDecimal tempIncentive = (supplyTypeTotalQty.multiply(tempIncentiveRateAmount)).setScale(2, rounding);
		    							BigDecimal tempMarginValue = tempMargin.add(tempIncentive);
		    							totalCashMarginValue = totalCashMarginValue.add(tempMarginValue);
		    							dailyMarginValue = dailyMarginValue.add(tempMarginValue);
		    							typeAndCount.put(productSubscriptionType.get("enumId")+"_MR", totalCashMarginValue) ;
		    							typeAndCount.put("TOTAL_MR", dailyMarginValue) ;
		    						}
		    					}
		    					else if((productSubscriptionType.get("enumId")).equals("SPECIAL_ORDER")){
		    						specialOrderTotal = specialOrderTotal.add(supplyTypeTotalQty);
		    						typeAndCount.put(productSubscriptionType.get("enumId"), specialOrderTotal.setScale(2, rounding));
		    						if(productMarginPriceMap.get(productId) != null){
		    							BigDecimal tempMargin =((supplyTypeTotalQty).multiply((BigDecimal) productMarginPriceMap.get(productId))).setScale(2, rounding);
		    							BigDecimal tempMarginValue = tempMargin;
		    							totalSOMarginValue = totalSOMarginValue.add(tempMarginValue);
		    							dailyMarginValue = dailyMarginValue.add(tempMarginValue);
		    							typeAndCount.put(productSubscriptionType.get("enumId")+"_MR", totalSOMarginValue) ;
		    							typeAndCount.put("TOTAL_MR", dailyMarginValue) ;
		    						}else{
		    							BigDecimal tempMargin =(supplyTypeTotalQty.multiply(normalMargin.add(tempProcAdlDiscValue))).setScale(2, rounding);							
		    							BigDecimal tempIncentive = (supplyTypeTotalQty.multiply(tempIncentiveRateAmount)).setScale(2, rounding);
		    							BigDecimal tempMarginValue = tempMargin.add(tempIncentive);
		    							totalSOMarginValue = totalSOMarginValue.add(tempMarginValue);
		    							dailyMarginValue = dailyMarginValue.add(tempMarginValue);
		    							typeAndCount.put(productSubscriptionType.get("enumId")+"_MR", totalSOMarginValue) ;
		    							typeAndCount.put("TOTAL_MR", dailyMarginValue) ;
		    						}
		    					}
		    					else if((productSubscriptionType.get("enumId")).equals("CARD")){
		    						cardTotal = cardTotal.add(supplyTypeTotalQty);
		    						typeAndCount.put(productSubscriptionType.get("enumId"), cardTotal.setScale(2, rounding));
		    						if(productMarginPriceMap.get(productId) != null){
		    							BigDecimal tempMargin =((supplyTypeTotalQty).multiply(((BigDecimal) productMarginPriceMap.get(productId)).add(cardMargin))).setScale(2, rounding);
		    							BigDecimal tempMarginValue = tempMargin;
		    							totalCardMarginValue = totalCardMarginValue.add(tempMarginValue);
		    							dailyMarginValue = dailyMarginValue.add(tempMarginValue);
		    							typeAndCount.put(productSubscriptionType.get("enumId")+"_MR", totalCardMarginValue) ;
		    							typeAndCount.put("TOTAL_MR", dailyMarginValue) ;
		    							
		    						}else{
		    							BigDecimal tempMargin =(supplyTypeTotalQty.multiply(normalMargin.add(cardMargin.add(tempProcAdlDiscValue)))).setScale(2, rounding);							
		    							BigDecimal tempIncentive = (supplyTypeTotalQty.multiply(tempIncentiveRateAmount)).setScale(2, rounding);
		    							BigDecimal tempMarginValue = tempMargin.add(tempIncentive);
		    							totalCardMarginValue = totalCardMarginValue.add(tempMarginValue);
		    							dailyMarginValue = dailyMarginValue.add(tempMarginValue);
		    							typeAndCount.put(productSubscriptionType.get("enumId")+"_MR", totalCardMarginValue) ;
		    							typeAndCount.put("TOTAL_MR", dailyMarginValue) ;
		    						}
		    					}
		    				}
		    				
		    			}// product totals	
		    			dailyMarginValue = BigDecimal.ZERO;
		    		}
		    		Debug.logImportant(i+"    Days Completed.", "");
		    		supplyDate = UtilDateTime.getNextDayStart(supplyDate);
		    	}
		    	masterList.add(boothMargins);
		    	result = populateFacilityCommissiions(dctx, context, masterList, periodBillingId);
		    	if (ServiceUtil.isError(result)) {
		    		generationFailed = true;
	                Debug.logWarning("There was an error while creating  the FacilityCommission: " + ServiceUtil.getErrorMessage(result), module);
	        		return ServiceUtil.returnError("There was an error while creating the FacilityCommission: " + ServiceUtil.getErrorMessage(result));          	            
	            } 
		    	GenericValue periodBilling=delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
	        	if (periodBilling.size() == 0) {
	        		Debug.logError("Period does not exist " + periodBilling, module);
	        		return ServiceUtil.returnError("Period does not exist " + periodBilling);                    	
	        	}
	        	if (generationFailed) {
	        		periodBilling.set("statusId", "GENERATION_FAIL");
	    		}
	    		else {
	    			periodBilling.set("statusId", "GENERATED");
	    			periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
	    		}
	        	periodBilling.store();
		       
		    	result.put("masterList", masterList);
		    	
			}catch (GenericServiceException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error while populating FacilityCommission" + e);
			}catch (GenericEntityException e) {
				Debug.logError(e, module);
				e.printStackTrace();
			}
	      return result;
	    }
	    
	    public static Map<String, Object>  populateFacilityCommissiions(DispatchContext dctx, Map<String, ? extends Object> context, List masterList, String periodBillingId)  {
			
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String billingTypeId = (String) context.get("billingTypeId");			
			if(billingTypeId.equals("PB_LMS_VNDR_MRGN")){
				if(masterList.size() == 0){
					Debug.logError("masterList is empty", module);
					return ServiceUtil.returnError("masterList is empty");
				}
				for(int i=0; i< masterList.size(); i++){
					Map boothMargins = FastMap.newInstance();
					boothMargins = (Map) masterList.get(i);
					Iterator boothMarginsIter = boothMargins.entrySet().iterator();
					while (boothMarginsIter.hasNext()) {
						Map.Entry boothEntry = (Entry) boothMarginsIter.next();
						String facilityId = (String) boothEntry.getKey();
						List dayWiseBoothValues = (List) boothEntry.getValue();
						for(int j=0; j< dayWiseBoothValues.size(); j++ ){
							Map dayTotalsMap = (Map) dayWiseBoothValues.get(j);
							Iterator dayTotalsIter = dayTotalsMap.entrySet().iterator();
							while (dayTotalsIter.hasNext()) {
								Map.Entry dayEntry = (Entry) dayTotalsIter.next();
								Timestamp commissionDate =  (Timestamp) dayEntry.getKey();
								Map<String, BigDecimal> valuesMap = (Map<String, BigDecimal>) dayEntry.getValue();
								try {
									GenericValue facilityCommission = delegator.findOne("FacilityCommission", UtilMisc.toMap("periodBillingId", periodBillingId, "commissionDate", commissionDate, "facilityId",facilityId), false);
									if (facilityCommission == null) {
										facilityCommission = delegator.makeValue("FacilityCommission");
										facilityCommission.put("periodBillingId", periodBillingId );
										facilityCommission.put("commissionDate", commissionDate);
										facilityCommission.put("facilityId", facilityId); 
										facilityCommission.put("totalQty", valuesMap.get("TOTAL"));
										facilityCommission.put("cardQty", valuesMap.get("CARD"));
										facilityCommission.put("cashQty", valuesMap.get("CASH"));
										facilityCommission.put("splOrderQty", valuesMap.get("SPECIAL_ORDER"));
										facilityCommission.put("totalAmount", valuesMap.get("TOTAL_MR"));
										facilityCommission.put("cardAmount", valuesMap.get("CARD_MR"));
										facilityCommission.put("cashAmount", valuesMap.get("CASH_MR"));
										facilityCommission.put("splOrderAmount", valuesMap.get("SPECIAL_ORDER_MR"));
										facilityCommission.put("dues", valuesMap.get("CASH_DUE"));
										facilityCommission.put("recovery", valuesMap.get("RECOVERY"));
		    		                
										facilityCommission.create();    
									}
									else { 
										Debug.logError("facilityCommission Already Exists", module);
										return ServiceUtil.returnError("facilityCommission Already Exists");
									}
								} catch (GenericEntityException e) {
									Debug.logError("Error While Creating New FacilityCommistion", module);
									return ServiceUtil.returnError("Error While Creating New FacilityCommistion");
								}
							}
						}
					}
				}
			}				
			if(billingTypeId.equals("PB_LMS_TRSPT_MRGN")){	
				for(int i=0; i< masterList.size(); i++){
					Map routeMargins = FastMap.newInstance();					
					routeMargins = (Map) masterList.get(i);
					Iterator routeMarginsIter = routeMargins.entrySet().iterator();					
					while (routeMarginsIter.hasNext()) {
						Map.Entry routeEntry = (Entry) routeMarginsIter.next();						
						String facilityId = (String) routeEntry.getKey();		
						Map dayWiseRouteValues = (Map) routeEntry.getValue();
						Iterator dayWiseRouteIter = dayWiseRouteValues.entrySet().iterator();
						while (dayWiseRouteIter.hasNext()) {
							Map.Entry dayRouteEntry = (Entry) dayWiseRouteIter.next();									
								Timestamp dateValue = (Timestamp) dayRouteEntry.getKey();
								Map dayRouteEntryMap = (Map) dayRouteEntry.getValue();								
							try{	
								GenericValue facilityCommission = delegator.findOne("FacilityCommission", UtilMisc.toMap("periodBillingId", periodBillingId, "commissionDate", dateValue, "facilityId",facilityId), false);
								if (facilityCommission == null) {
									GenericValue facilityDetail = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId) ,false);
									String isUpCountry = facilityDetail.getString("isUpcountry");
									Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
									inputRateAmt.put("periodTypeId", "RATE_HOUR");
									inputRateAmt.put("rateCurrencyUomId", context.get("currencyUomId"));
									inputRateAmt.put("partyId", facilityDetail.get("ownerPartyId"));
									inputRateAmt.put("rateTypeId", facilityId+"_MRGN");
									Map<String, Object> rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);
									BigDecimal normalMargin = BigDecimal.ZERO;
						    		if(UtilValidate.isNotEmpty(rateAmount)){
						    			 normalMargin = (BigDecimal) rateAmount.get("rateAmount");
						    		}						
						    		BigDecimal dayTotQty = (BigDecimal)dayRouteEntryMap.get("quantity");
									facilityCommission = delegator.makeValue("FacilityCommission");
									facilityCommission.put("periodBillingId", periodBillingId );
									facilityCommission.put("commissionDate", dateValue);
									facilityCommission.put("facilityId", facilityId); 
									facilityCommission.put("totalQty", dayRouteEntryMap.get("quantity"));									
									if("Y".equals(isUpCountry)){
										facilityCommission.put("totalAmount", normalMargin);
									}else{		
										facilityCommission.put("totalAmount", dayTotQty.multiply(normalMargin));									
									}
									facilityCommission.put("dues", dayRouteEntryMap.get("dueAmount"));
									facilityCommission.create();    
								}else { 
									Debug.logError("facilityCommission Already Exists", module);
									return ServiceUtil.returnError("facilityCommission Already Exists");
								}
							}catch (GenericServiceException e) {
								Debug.logError(e, module);
								return ServiceUtil.returnError("Error while populating FacilityCommission" + e);
							} catch (GenericEntityException e) {
									Debug.logError("Error While Creating New FacilityCommistion", module);
									return ServiceUtil.returnError("Error While Creating New FacilityCommistion");
							}						
							
						}					 
					}
				}
			}
	        return result;
	    }
	    
	    public static Map<String, Object>  populatePeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context)  {
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String periodBillingId = null;
			String customTimePeriodId = (String) context.get("customTimePeriodId");
			String billingTypeId = (String) context.get("billingTypeId");			
			List conditionList = FastList.newInstance();
	        List periodBillingList = FastList.newInstance();
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
				Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"billingTypeId", billingTypeId, "customTimePeriodId", customTimePeriodId,"userLogin", userLogin);
				if(billingTypeId.equals("PB_LMS_VNDR_MRGN")){
					dispatcher.runAsync("generateVendorMargin", runSACOContext);					
				}
				if(billingTypeId.equals("PB_LMS_TRSPT_MRGN")){					
					dispatcher.runAsync("generateTranporterMargin", runSACOContext);
				}
	    	} catch (GenericEntityException e) {
				Debug.logError(e,"Failed To Create New Period_Billing", module);
				e.printStackTrace();
			}
	        catch (GenericServiceException e) {
	            Debug.logError(e, "Error in calling 'generateVendorMargin' service", module);
	            return ServiceUtil.returnError(e.getMessage());
	        } 
	        result.put("periodBillingId", periodBillingId);
	        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
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
	    
	    public static Map<String, Object>  populateLMSSalesHistorySummary(DispatchContext dctx, Map<String, Object> context)  {
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");		
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			if(UtilValidate.isNotEmpty(context.get("saleDate"))){
				nowTimestamp = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("saleDate")).getTime()));
			}
			Map<String, Object> salesTotals = DeprecatedNetworkServices.getDayTotals(dctx, nowTimestamp, null, true, false, null);			
			BigDecimal totalQuantity = ((BigDecimal)salesTotals.get("totalQuantity")).setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
			BigDecimal totalRevenue = ((BigDecimal)salesTotals.get("totalRevenue")).setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
			try {
				Date summaryDate = new Date(nowTimestamp.getTime());				
				//Debug.logInfo("salesDate=" + salesDate + "shipmentIds=" + shipmentIds, module);
				GenericValue salesSummary = delegator.findOne("LMSSalesHistorySummary", UtilMisc.toMap("salesDate", summaryDate), false);
				if (salesSummary == null) {
					// add to summary table
					salesSummary = delegator.makeValue("LMSSalesHistorySummary");
					salesSummary.put("salesDate", summaryDate);
					salesSummary.put("totalQuantity", totalQuantity);
					salesSummary.put("totalRevenue", totalRevenue);                
					salesSummary.create();  
					LMSSalesHistoryServices.LMSSalesHistorySummaryDetail(dctx,  UtilMisc.toMap("salesDate", summaryDate));
				}
				else {
					// check and see if we need to update for whatever reason
					BigDecimal summaryQuantity  = salesSummary.getBigDecimal("totalQuantity");
					BigDecimal summaryRevenue  = salesSummary.getBigDecimal("totalRevenue");     
					if (summaryQuantity.compareTo(totalQuantity) != 0 || summaryRevenue.compareTo(totalRevenue) != 0) {
						salesSummary.put("totalQuantity", totalQuantity);
						salesSummary.put("totalRevenue", totalRevenue);  
						salesSummary.store();
						LMSSalesHistoryServices.LMSSalesHistorySummaryDetail(dctx,  UtilMisc.toMap("salesDate", summaryDate));
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}	
	        return ServiceUtil.returnSuccess("update successfully done!");		
		}    
	    
	    public static Map<String, Object> getBoothMarginDuesForPeriodBilling(DispatchContext dctx, Map<String, Object> context) {
			List conditionList= FastList.newInstance(); 
			LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        Map<String, Object> result = ServiceUtil.returnSuccess();
			String periodBillingId = (String) context.get("periodBillingId");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> boothTradingMap = new HashMap<String, Object>();
        	String boothId = null;
			if(!UtilValidate.isEmpty(periodBillingId)){
				conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
	        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	        	try{
	        		List<GenericValue> dayWiseBoothMargins = FastList.newInstance();
                	dayWiseBoothMargins = delegator.findList("FacilityCommission", condition, null,null, null, false);
                	if(!UtilValidate.isEmpty(dayWiseBoothMargins)){
                		for(GenericValue eachBooth : dayWiseBoothMargins){
                			BigDecimal totalMargin = BigDecimal.ZERO;
                        	BigDecimal totalDue = BigDecimal.ZERO;
                			Map<String, Object> marginDuesMap = FastMap.newInstance();
                			BigDecimal totalMarginAmount = BigDecimal.ZERO;
                			BigDecimal totalDueAmount = BigDecimal.ZERO;
                			boothId = (String)eachBooth.get("facilityId");
                			if(!UtilValidate.isEmpty(eachBooth.get("totalAmount"))){
                				totalMargin = (BigDecimal)eachBooth.get("totalAmount");
                			}
                			if(!UtilValidate.isEmpty(eachBooth.get("dues"))){
                				totalDue = (BigDecimal)eachBooth.get("dues");
                			}
                			if(boothTradingMap.containsKey(boothId)){
                				BigDecimal margin = BigDecimal.ZERO;
                				BigDecimal due = BigDecimal.ZERO;
                				marginDuesMap = (Map)boothTradingMap.get(boothId);
                				margin = (BigDecimal)marginDuesMap.get("totalMargin");
                				due = (BigDecimal)marginDuesMap.get("totalDue");
                				totalMarginAmount = margin.add(totalMargin);
                				totalDueAmount = due.add(totalDue);
                				marginDuesMap.put("totalMargin",totalMarginAmount);
                				marginDuesMap.put("totalDue",totalDueAmount);
                				boothTradingMap.put(boothId,marginDuesMap);
                				
                			}
                			else{
                				marginDuesMap.put("totalMargin",totalMargin);
                				marginDuesMap.put("totalDue", totalDue);
                				boothTradingMap.put(boothId, marginDuesMap);
                			}
                		}
                	}
	        	}catch(GenericEntityException e){
	        		Debug.logError(e, module);
	        	}
			}
			result.put("boothTradingMap",boothTradingMap);
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
	    public static Map<String, Object> vendorMarginTenantConfigCheck(DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();       
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String userLoginId = (String)userLogin.get("userLoginId");
	        Map<String, Object> result = new HashMap<String, Object>();
	        String periodBillingId = (String) context.get("periodBillingId");
	        GenericValue periodBilling = null;
	        String statusId = null;
	        GenericValue tenantConfiguration = null;
	        try{
	        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
	        
	        if(UtilValidate.isNotEmpty(periodBilling)){
	        	statusId = (String)periodBilling.get("statusId");
	        	tenantConfiguration = delegator.findOne("TenantConfiguration",UtilMisc.toMap("propertyName", "enablePastPaymentService", "propertyTypeEnumId", "LMS"),false);
	        	if(statusId.equals("ADJUSTED") || statusId.equals("COM_CANCELLED")){
	        		tenantConfiguration.set("propertyValue", "Y");
	        		tenantConfiguration.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
	        		tenantConfiguration.store();
	        	}
	        	else{
	        		tenantConfiguration.set("propertyValue", "N");
	        		tenantConfiguration.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
	        		tenantConfiguration.store();
	        	}
	        }
	        }catch(GenericEntityException e){
	        	Debug.logError("Unable to set tenant configuration  record in database"+e, module);
        		return ServiceUtil.returnError("Unable to set tenant configuration  record in database "); 
	        }
	        result = ServiceUtil.returnSuccess("Tenant Configuration set Sucessfully");
	        return result;
	    }
	    public static Map<String, Object> vendorMarginAdjustments(DispatchContext dctx, Map<String, ? extends Object> context) {
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
				dispatcher.runAsync("makeVendorMarginPaymentAdjustments", adjustInputMap);
			}
			catch (GenericServiceException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());			
			}	 
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
	    public static Map<String, Object>  populateLMSPeriodSalesSummary(DispatchContext dctx, Map<String, Object> context)  {
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	
	        // Convert attributes to the corresponding data types
			Locale locale = null;
			TimeZone timeZone = null;
			locale = Locale.getDefault();
			timeZone = TimeZone.getDefault();
	        
	        Timestamp fromDate = UtilDateTime.nowTimestamp(); 
	        if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
	        	fromDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("fromDate")).getTime()));	
	        }           
	          
	        Timestamp thruDate = UtilDateTime.nowTimestamp(); 
	        if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
	        	thruDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("thruDate")).getTime()));	
	        }           
	        
	        int IntervalDays = UtilDateTime.getIntervalInDays(fromDate, thruDate);
			List shipmentList = FastList.newInstance(); 
			shipmentList.add("AM");
			shipmentList.add("PM");
			String productId = null;
			String boothProductId = null;
			String boothId = null;
			Object quantity = null;
			Object revenue = null;
			Object productSubscriptionTypeId = null;
			Object boothProductSubscriptionTypeId = null;
			Object boothQuantity = null;
			Object boothRevenue = null;
			Map productsMap = FastMap.newInstance();
			Map boothTotals = FastMap.newInstance();
			
			Map<String, Object> zonesMap = DeprecatedNetworkServices.getZones(delegator);
        	List<String> zones = (List)zonesMap.get("zonesList");
			
			try{
				for(int i=0;i<=IntervalDays;i++){
					Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDate, +i);
					Date summaryDate = new Date(saleDate.getTime());
					for(Object shipment : shipmentList){
						Map<String, Object> salesTotals = DeprecatedNetworkServices.getDayTotals(dctx, saleDate, shipment.toString() , false, false, null);
						productsMap = (Map)salesTotals.get("productTotals");
						Iterator mapIterator = productsMap.entrySet().iterator();
						while (mapIterator.hasNext()) {//product wise
							Map.Entry entry = (Entry) mapIterator.next();
							Map productsSalesMap = FastMap.newInstance();
							productsSalesMap.clear();
							productsSalesMap = (Map) entry.getValue();
							productId = (String) entry.getKey();
							Map tempMap = FastMap.newInstance();
							tempMap = (Map) productsSalesMap.get("supplyTypeTotals");
							Iterator supplyTypeIter = tempMap.entrySet().iterator(); 
							while (supplyTypeIter.hasNext()) {//supply type wise
								Map.Entry entrySupplyType = (Entry) supplyTypeIter.next();
								Map supplyTypeMap = FastMap.newInstance();
								supplyTypeMap = (Map) (entrySupplyType.getValue());
								productSubscriptionTypeId = supplyTypeMap.get("name");
								quantity = supplyTypeMap.get("total");
								revenue = supplyTypeMap.get("totalRevenue");
								try{
									GenericValue salesSummary = delegator.makeValue("LMSPeriodSalesSummary");
									salesSummary.put("salesDate", summaryDate);
									salesSummary.put("totalQuantity", quantity);
									salesSummary.put("totalRevenue", revenue);  
									salesSummary.put("shipmentTypeId", shipment.toString());
									salesSummary.put("productSubscriptionTypeId", productSubscriptionTypeId);
									salesSummary.put("productId", productId);
									salesSummary.put("periodTypeId", "SALES_DAY");
									delegator.createOrStore(salesSummary);
								} catch (GenericEntityException e) {
									Debug.logError(e, module);
								}
							}
						}//product 
						//populating LMSPeriodSalesSummaryDetail
						boothTotals = (Map)salesTotals.get("boothTotals");
						for(String zone : zones){
	    	        		Map<String, Object> zoneTotMap = new TreeMap<String, Object>();
	    	        		List<String> zoneRoutes = FastList.newInstance();
	    	        		zoneRoutes = DeprecatedNetworkServices.getZoneRoutes(delegator,zone);
	    	        		for(String route : zoneRoutes){
	    	        			Map<String, Object> routesTotMap = new TreeMap<String, Object>();
	    	        			List<String> routeBooths = FastList.newInstance();
	    	        			routeBooths = DeprecatedNetworkServices.getRouteBooths(delegator,route,null);
	    	        			for(String booth : routeBooths){
    								Map boothSalesMap = (Map) boothTotals.get(booth);
	    	        				if(boothSalesMap != null){
	    	        					Map boothProductsMap = FastMap.newInstance();
	    	        					boothProductsMap = (Map)boothSalesMap.get("productTotals");
	    								Iterator mapProdIterator = boothProductsMap.entrySet().iterator();
	    								while (mapProdIterator.hasNext()) {//product wise
	    									Map.Entry prodEntry = (Entry) mapProdIterator.next();
	    									Map productsBoothSalesMap = FastMap.newInstance();
	    									productsBoothSalesMap.clear();
	    									productsBoothSalesMap = (Map) prodEntry.getValue();
	    									boothProductId = (String) prodEntry.getKey();
	    									Map tempMapSupplyType = FastMap.newInstance();
	    									tempMapSupplyType = (Map) productsBoothSalesMap.get("supplyTypeTotals");
	    									Iterator boothSupplyTypeIter = tempMapSupplyType.entrySet().iterator(); 
	    									while (boothSupplyTypeIter.hasNext()) {//supply type wise
	    										Map.Entry entry = (Entry) boothSupplyTypeIter.next();
	    										Map supplyTypeMap = FastMap.newInstance();
	    										supplyTypeMap = (Map) (entry.getValue());
	    										boothProductSubscriptionTypeId = supplyTypeMap.get("name");
	    										boothQuantity = supplyTypeMap.get("total");
	    										boothRevenue = supplyTypeMap.get("totalRevenue");
	    										
	    										if((boothRevenue != BigDecimal.ZERO) && (boothQuantity != BigDecimal.ZERO)){
	    											
	    											//populating route wise map 
	        						    	  		if(routesTotMap.get(route) != null){
	    						    					Map routeDataMap = (Map)routesTotMap.get(route);
	        						    				if(routeDataMap.get(boothProductSubscriptionTypeId)!= null){
	        						    					Map routeProdSubTypeMap = (Map)routeDataMap.get(boothProductSubscriptionTypeId);
	        						    					if(routeProdSubTypeMap.get(boothProductId)!= null){
	    						    							Map routeProdMap = (Map)routeProdSubTypeMap.get(boothProductId);
	    						    							BigDecimal runningTotalProductQty = (BigDecimal)routeProdMap.get("totalQty");
	    	    						    					BigDecimal runningProductRevenue = (BigDecimal)routeProdMap.get("totalRevenue");
	    	    						    					runningTotalProductQty = runningTotalProductQty.add((BigDecimal)boothQuantity);
	    	    						    					runningProductRevenue = runningProductRevenue.add((BigDecimal)boothRevenue);
	    	    						    					routeProdMap.put("totalQty", runningTotalProductQty);
	    	    						        				routeProdMap.put("totalRevenue", runningProductRevenue);
	    	    						        				routeProdSubTypeMap.put(boothProductId, routeProdMap);
	    						    						}else{
	    						    							Map<String, Object> routeProdMap = FastMap.newInstance();
	    						    							routeProdMap.put("totalQty", boothQuantity);
	    	    						        				routeProdMap.put("totalRevenue", boothRevenue);
	    	    						        				routeProdSubTypeMap.put(boothProductId, routeProdMap);
	    						    						}	
	        						    					routeDataMap.put(boothProductSubscriptionTypeId,routeProdSubTypeMap);
	        						    				}else{
	        						    					Map<String, Object> routeProdSubTypeMap = FastMap.newInstance();
	        						    					Map<String, Object> routeProdMap = FastMap.newInstance();
	        						    					routeProdMap.put("totalQty", boothQuantity);
	        						    					routeProdMap.put("totalRevenue", boothRevenue);
	        						    					routeProdSubTypeMap.put(boothProductId, routeProdMap);
	        						    					routeDataMap.put(boothProductSubscriptionTypeId, routeProdSubTypeMap);
	        						    				}
	        						    				routesTotMap.put(route, routeDataMap);
	    						    				}else{
	    						    					Map routeDataMap = FastMap.newInstance();
	    						    					Map<String, Object> routeProdSubTypeMap = FastMap.newInstance();
	    						    					Map<String, Object> routeProdMap = FastMap.newInstance();
	    						    					routeProdMap.put("totalQty", boothQuantity);
	    						    					routeProdMap.put("totalRevenue", boothRevenue);
	    						    					routeProdSubTypeMap.put(boothProductId, routeProdMap);
	    						    					routeDataMap.put(boothProductSubscriptionTypeId, routeProdSubTypeMap);
	    						    					routesTotMap.put(route, routeDataMap);
	    						    				}
	        						    	  		
	        						    	  		
	        						    	  	//populating zone wise map 
	        						    	  		if(zoneTotMap.get(zone) != null){
	    						    					Map zoneDataMap = (Map)zoneTotMap.get(zone);
	        						    				if(zoneDataMap.get(boothProductSubscriptionTypeId)!= null){
	        						    					Map zoneProdSubTypeMap = (Map)zoneDataMap.get(boothProductSubscriptionTypeId);
	        						    					if(zoneProdSubTypeMap.get(boothProductId)!= null){
	    						    							Map zoneProdMap = (Map)zoneProdSubTypeMap.get(boothProductId);
	    						    							BigDecimal runningTotalProductQty = (BigDecimal)zoneProdMap.get("totalQty");
	    	    						    					BigDecimal runningProductRevenue = (BigDecimal)zoneProdMap.get("totalRevenue");
	    	    						    					runningTotalProductQty = runningTotalProductQty.add((BigDecimal)boothQuantity);
	    	    						    					runningProductRevenue = runningProductRevenue.add((BigDecimal)boothRevenue);
	    	    						    					zoneProdMap.put("totalQty", runningTotalProductQty);
	    	    						    					zoneProdMap.put("totalRevenue", runningProductRevenue);
	    	    						        				zoneProdSubTypeMap.put(boothProductId, zoneProdMap);
	    						    						}else{
	    						    							Map<String, Object> zoneProdMap = FastMap.newInstance();
	    						    							zoneProdMap.put("totalQty", boothQuantity);
	    						    							zoneProdMap.put("totalRevenue", boothRevenue);
	    	    						        				zoneProdSubTypeMap.put(boothProductId, zoneProdMap);
	    						    						}	
	        						    					zoneDataMap.put(boothProductSubscriptionTypeId,zoneProdSubTypeMap);
	        						    				}else{
	        						    					Map<String, Object> zoneProdSubTypeMap = FastMap.newInstance();
	        						    					Map<String, Object> zoneProdMap = FastMap.newInstance();
	        						    					zoneProdMap.put("totalQty", boothQuantity);
	        						    					zoneProdMap.put("totalRevenue", boothRevenue);
	        						    					zoneProdSubTypeMap.put(boothProductId, zoneProdMap);
	        						    					zoneDataMap.put(boothProductSubscriptionTypeId, zoneProdSubTypeMap);
	        						    				}
	        						    				zoneTotMap.put(zone, zoneDataMap);
	    						    				}else{
	    						    					Map zoneDataMap = FastMap.newInstance();
	    						    					Map<String, Object> zoneProdSubTypeMap = FastMap.newInstance();
	    						    					Map<String, Object> zoneProdMap = FastMap.newInstance();
	    						    					zoneProdMap.put("totalQty", boothQuantity);
	    						    					zoneProdMap.put("totalRevenue", boothRevenue);
	    						    					zoneProdSubTypeMap.put(boothProductId, zoneProdMap);
	    						    					zoneDataMap.put(boothProductSubscriptionTypeId, zoneProdSubTypeMap);
	    						    					zoneTotMap.put(zone, zoneDataMap);
	    						    				}
	        						    	  
	        						    	  		//booth
	    											GenericValue salesSummaryDetail = delegator.makeValue("LMSPeriodSalesSummaryDetail");
		    										salesSummaryDetail.put("salesDate", summaryDate);
		    										salesSummaryDetail.put("totalQuantity", boothQuantity);
		    										salesSummaryDetail.put("totalRevenue", boothRevenue);  
		    										salesSummaryDetail.put("facilityId",booth);
		    										salesSummaryDetail.put("shipmentTypeId", shipment.toString());
		    										salesSummaryDetail.put("productSubscriptionTypeId", boothProductSubscriptionTypeId);
		    										salesSummaryDetail.put("productId", boothProductId);
		    										salesSummaryDetail.put("periodTypeId", "SALES_DAY");
		    										delegator.createOrStore(salesSummaryDetail);
	    										}//check nulls
	    									}//product
	    								}//subProd
	    							}//check for nulls in booth wise totals
	    	        			}//booth
	    	        			//route wise Monthly sale
	    	        			if (routesTotMap.get(route) != null) {
	    	        				Map routeDataMap = (Map)routesTotMap.get(route);
	        						Iterator routeIterator = routeDataMap.entrySet().iterator();
	        						while (routeIterator.hasNext()) {//booth wise
	        							Map.Entry routeEntry = (Entry) routeIterator.next();
	        							Map routeProdSubTypeMap = FastMap.newInstance();
	        							routeProdSubTypeMap.clear();
	        							routeProdSubTypeMap = (Map) routeEntry.getValue();
	        							String routeProductSubscriptionTypeId = (String) routeEntry.getKey();
	        							Iterator mapProdIterator = routeProdSubTypeMap.entrySet().iterator();
	        							while (mapProdIterator.hasNext()) {//product wise
	        								Map.Entry entry = (Entry) mapProdIterator.next();
	        								Map productsSalesMap = FastMap.newInstance();
	        								productsSalesMap.clear();
	        								productsSalesMap = (Map) entry.getValue();
	        								String routeProductId = (String) entry.getKey();
	        								BigDecimal routeQuantity = (BigDecimal)productsSalesMap.get("totalQty");
	        								BigDecimal routeRevenue = (BigDecimal)productsSalesMap.get("totalRevenue");
	        								GenericValue salesSummaryRouteDetail = delegator.makeValue("LMSPeriodSalesSummaryDetail");
	        	    	        			salesSummaryRouteDetail.put("salesDate", summaryDate);
	        				    	  		salesSummaryRouteDetail.put("totalQuantity", routeQuantity);
	        				    	  		salesSummaryRouteDetail.put("totalRevenue", routeRevenue);  
	        				    	  		salesSummaryRouteDetail.put("shipmentTypeId", shipment.toString());
	        				    	  		salesSummaryRouteDetail.put("facilityId",route);
	        				    	  		salesSummaryRouteDetail.put("productSubscriptionTypeId", routeProductSubscriptionTypeId);
	        				    	  		salesSummaryRouteDetail.put("productId", routeProductId);
	        				    	  		salesSummaryRouteDetail.put("periodTypeId", "SALES_DAY");		
	        	    	        			delegator.createOrStore(salesSummaryRouteDetail);
	        							}
	        						}
	    	        			}
	    	        		}//route
	    	        		
	    	        		//zone wise Monthly sale
	    	        		if (zoneTotMap.get(zone) != null) {
		        				Map zoneDataMap = (Map)zoneTotMap.get(zone);
	    						Iterator zoneIterator = zoneDataMap.entrySet().iterator();
	    						while (zoneIterator.hasNext()) {//booth wise
	    							Map.Entry zoneEntry = (Entry) zoneIterator.next();
	    							Map zoneProdSubTypeMap = FastMap.newInstance();
	    							zoneProdSubTypeMap.clear();
	    							zoneProdSubTypeMap = (Map) zoneEntry.getValue();
	    							String zoneProductSubscriptionTypeId = (String) zoneEntry.getKey();
	    							Iterator mapProdIterator = zoneProdSubTypeMap.entrySet().iterator();
	    							while (mapProdIterator.hasNext()) {//product wise
	    								Map.Entry entry = (Entry) mapProdIterator.next();
	    								Map productsSalesMap = FastMap.newInstance();
	    								productsSalesMap.clear();
	    								productsSalesMap = (Map) entry.getValue();
	    								String zoneProductId = (String) entry.getKey();
	    								BigDecimal zoneQuantity = (BigDecimal)productsSalesMap.get("totalQty");
	    								BigDecimal zoneRevenue = (BigDecimal)productsSalesMap.get("totalRevenue");
	    								GenericValue salesSummaryzoneDetail = delegator.makeValue("LMSPeriodSalesSummaryDetail");
	    	    	        			salesSummaryzoneDetail.put("salesDate", summaryDate);
	    				    	  		salesSummaryzoneDetail.put("totalQuantity", zoneQuantity);
	    				    	  		salesSummaryzoneDetail.put("totalRevenue", zoneRevenue);  
	    				    	  		salesSummaryzoneDetail.put("shipmentTypeId", shipment.toString());
	    				    	  		salesSummaryzoneDetail.put("facilityId",zone);
	    				    	  		salesSummaryzoneDetail.put("productSubscriptionTypeId", zoneProductSubscriptionTypeId);
	    				    	  		salesSummaryzoneDetail.put("productId", zoneProductId);
	    				    	  		salesSummaryzoneDetail.put("periodTypeId", "SALES_DAY");		
	    	    	        			delegator.createOrStore(salesSummaryzoneDetail);
	    							}
	    						}
		        			}
	    	        		
						}//zone
					}//shipment
					Timestamp monthStart = UtilDateTime.getMonthStart(saleDate);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(saleDate, timeZone, locale);
					Timestamp currentDate = UtilDateTime.getDayEnd(saleDate);
					if(monthEnd.equals(currentDate)){
						//populate here the total 
						LMSSalesHistoryServices.populateLMSMonthlySalesSummary(dctx, context, "SALES_MONTH",monthStart,monthEnd);
					}
				}//days
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}	
	        return ServiceUtil.returnSuccess("update successfully done!");		
		}
	    public static Map<String, Object> populateLMSMonthlySalesSummary(DispatchContext dctx, Map context, String periodType,Timestamp periodStart,Timestamp periodEnd ) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();	
			
			List productSubscriptionTypeList = FastList.newInstance();
			List<GenericValue> productList = FastList.newInstance();
			List shipmentList= FastList.newInstance(); 
			
			shipmentList.add("AM");
			shipmentList.add("PM");
			List<GenericValue> lmsSalesSummaryList = FastList.newInstance(); 
			List<GenericValue> lmsSalesSummaryDetailList = FastList.newInstance();
			Date startDate = new Date(periodStart.getTime());
			Date endDate = new Date(periodEnd.getTime());
			List conditionsList = FastList.newInstance();
			conditionsList.add(EntityCondition.makeCondition("salesDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
			conditionsList.add(EntityCondition.makeCondition("salesDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
			conditionsList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_DAY"));
        	EntityCondition condition = EntityCondition.makeCondition(conditionsList,EntityOperator.AND);
        	List<GenericValue> shipmentWise = FastList.newInstance();
        	List<GenericValue> productSubscriptionTypeWise= FastList.newInstance();
        	List<GenericValue> productWise = FastList.newInstance();
        	List<GenericValue> boothWise = FastList.newInstance();
        	
        	Map<String, Object> zonesMap = DeprecatedNetworkServices.getZones(delegator);
        	List<String> zones = (List)zonesMap.get("zonesList");
        	
        	try{
    			productSubscriptionTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "SUB_PROD_TYPE"), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"), null, false);
    			productList  = DeprecatedNetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
        		lmsSalesSummaryList = delegator.findList("LMSPeriodSalesSummary", condition, null,null, null, false);
        		lmsSalesSummaryDetailList = delegator.findList("LMSPeriodSalesSummaryDetail", condition, null,null, null, false);
        	
        		for(Object shipment : shipmentList){
            		shipmentWise = EntityUtil.filterByAnd(lmsSalesSummaryList, UtilMisc.toList(EntityCondition.makeCondition("shipmentTypeId", shipment.toString())));
            		//summary
            		Iterator<GenericValue> shipTypeIter = productSubscriptionTypeList.iterator();
    		    	while(shipTypeIter.hasNext()) {
    		            GenericValue type = shipTypeIter.next();
    		            String productSubscriptionTypeId = type.getString("enumId");
    		            productSubscriptionTypeWise = EntityUtil.filterByAnd(shipmentWise, UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId", productSubscriptionTypeId)));
    		            
    		            Iterator<GenericValue> prodSubTypeIter = productList.iterator();
    			    	while(prodSubTypeIter.hasNext()) {
    			    		
    			    		List<BigDecimal> revenuForPeriod = FastList.newInstance();
    			        	List<BigDecimal> qtyForPeriod = FastList.newInstance();
    			         	BigDecimal totQty = BigDecimal.ZERO;
    			        	BigDecimal totRevenu = BigDecimal.ZERO;
    			        	
    			            GenericValue prodType = prodSubTypeIter.next();
    			            String productId = prodType.getString("productId");
    			            productWise = EntityUtil.filterByAnd(productSubscriptionTypeWise, UtilMisc.toList(EntityCondition.makeCondition("productId", productId)));
    			            Iterator<GenericValue> prodTypeIter = productWise.iterator();
    				    	while(prodTypeIter.hasNext()) {
    				    		GenericValue Type = prodTypeIter.next();
    				    		revenuForPeriod.add(Type.getBigDecimal("totalRevenue"));
    				    		qtyForPeriod.add(Type.getBigDecimal("totalQuantity"));
    				    	}
    				    	totQty = LMSSalesHistoryServices.sum(qtyForPeriod);
    				    	totRevenu = LMSSalesHistoryServices.sum(revenuForPeriod);
    				    	if((totQty != BigDecimal.ZERO) && (totRevenu != BigDecimal.ZERO)){
    				    		GenericValue salesSummary = delegator.makeValue("LMSPeriodSalesSummary");
								salesSummary.put("salesDate", startDate);
								salesSummary.put("totalQuantity", totQty);
								salesSummary.put("totalRevenue", totRevenu);  
								salesSummary.put("shipmentTypeId", shipment.toString());
								salesSummary.put("productSubscriptionTypeId", productSubscriptionTypeId);
								salesSummary.put("productId", productId);
								salesSummary.put("periodTypeId", "SALES_MONTH");
								delegator.createOrStore(salesSummary);	
    				    	}
    			    	}
    		    	}
    		    	
    		    	shipmentWise.clear();
    		    	productSubscriptionTypeWise.clear();
    	        	productWise.clear();
    	        	
    	        	//summary Detail
    		    	shipmentWise = EntityUtil.filterByAnd(lmsSalesSummaryDetailList, UtilMisc.toList(EntityCondition.makeCondition("shipmentTypeId", shipment.toString())));
    	        	for(String zone : zones){
    	        		Map<String, Object> zoneTotMap = new TreeMap<String, Object>();
    	        		List<String> zoneRoutes = FastList.newInstance();
    	        		zoneRoutes = DeprecatedNetworkServices.getZoneRoutes(delegator,zone);
    	        		for(String route : zoneRoutes){
    	        			Map<String, Object> routesTotMap = new TreeMap<String, Object>();
    	        			List<String> routeBooths = FastList.newInstance();
    	        			routeBooths = DeprecatedNetworkServices.getRouteBooths(delegator,route,null);
    	        			for(String booth : routeBooths){
    	        				boothWise = EntityUtil.filterByAnd(shipmentWise, UtilMisc.toList(EntityCondition.makeCondition("facilityId", booth))); 
    	    		    		Iterator<GenericValue> shipTypeIterForDetail = productSubscriptionTypeList.iterator();
    	    			    	while(shipTypeIterForDetail.hasNext()) {
    	    			            GenericValue type = shipTypeIterForDetail.next();
    	    			            String productSubscriptionTypeId = type.getString("enumId");
    	    			            productSubscriptionTypeWise = EntityUtil.filterByAnd(boothWise, UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId", productSubscriptionTypeId)));
    	    			            
    	    			            Iterator<GenericValue> prodSubTypeIterForDetail = productList.iterator();
    	    				    	while(prodSubTypeIterForDetail.hasNext()) {
    	    				            GenericValue prodType = prodSubTypeIterForDetail.next();
    	    				            String productId = prodType.getString("productId");
    	    				            productWise = EntityUtil.filterByAnd(productSubscriptionTypeWise, UtilMisc.toList(EntityCondition.makeCondition("productId", productId)));
    	    				            
    	    				    		List<BigDecimal> revenuForPeriod = FastList.newInstance();
    	    				        	List<BigDecimal> qtyForPeriod = FastList.newInstance();
    	    				         	BigDecimal totQty = BigDecimal.ZERO;
    	    				        	BigDecimal totRevenu = BigDecimal.ZERO;
    	    				        	
    	    				            Iterator<GenericValue> prodTypeIterForDetail = productWise.iterator();
    	    					    	while(prodTypeIterForDetail.hasNext()) {
    	    					    		GenericValue Type = prodTypeIterForDetail.next();
    	    					    		revenuForPeriod.add(Type.getBigDecimal("totalRevenue"));
    	    					    		qtyForPeriod.add(Type.getBigDecimal("totalQuantity"));
    	    					    	}
    	    					    	totQty = LMSSalesHistoryServices.sum(qtyForPeriod);
    	    					    	totRevenu = LMSSalesHistoryServices.sum(revenuForPeriod);
    	    					    	
    						    	  	if((totQty != BigDecimal.ZERO) && (totQty != BigDecimal.ZERO)){
    						    	  		
    						    	  		//populating route wise map 
    						    	  		if(routesTotMap.get(route) != null){
						    					Map routeDataMap = (Map)routesTotMap.get(route);
    						    				if(routeDataMap.get(productSubscriptionTypeId)!= null){
    						    					Map routeProdSubTypeMap = (Map)routeDataMap.get(productSubscriptionTypeId);
    						    					if(routeProdSubTypeMap.get(productId)!= null){
						    							Map routeProdMap = (Map)routeProdSubTypeMap.get(productId);
						    							BigDecimal runningTotalProductQty = (BigDecimal)routeProdMap.get("totalQty");
	    						    					BigDecimal runningProductRevenue = (BigDecimal)routeProdMap.get("totalRevenue");
	    						    					runningTotalProductQty = runningTotalProductQty.add(totQty);
	    						    					runningProductRevenue = runningProductRevenue.add(totRevenu);
	    						    					routeProdMap.put("totalQty", runningTotalProductQty);
	    						        				routeProdMap.put("totalRevenue", runningProductRevenue);
	    						        				routeProdSubTypeMap.put(productId, routeProdMap);
						    						}else{
						    							Map<String, Object> routeProdMap = FastMap.newInstance();
						    							routeProdMap.put("totalQty", totQty);
	    						        				routeProdMap.put("totalRevenue", totRevenu);
	    						        				routeProdSubTypeMap.put(productId, routeProdMap);
						    						}	
    						    					routeDataMap.put(productSubscriptionTypeId,routeProdSubTypeMap);
    						    				}else{
    						    					Map<String, Object> routeProdSubTypeMap = FastMap.newInstance();
    						    					Map<String, Object> routeProdMap = FastMap.newInstance();
    						    					routeProdMap.put("totalQty", totQty);
    						    					routeProdMap.put("totalRevenue", totRevenu);
    						    					routeProdSubTypeMap.put(productId, routeProdMap);
    						    					routeDataMap.put(productSubscriptionTypeId, routeProdSubTypeMap);
    						    				}
    						    				routesTotMap.put(route, routeDataMap);
						    				}else{
						    					Map routeDataMap = FastMap.newInstance();
						    					Map<String, Object> routeProdSubTypeMap = FastMap.newInstance();
						    					Map<String, Object> routeProdMap = FastMap.newInstance();
						    					routeProdMap.put("totalQty", totQty);
						    					routeProdMap.put("totalRevenue", totRevenu);
						    					routeProdSubTypeMap.put(productId, routeProdMap);
						    					routeDataMap.put(productSubscriptionTypeId, routeProdSubTypeMap);
						    					routesTotMap.put(route, routeDataMap);
						    				}
    						    	  		
    						    	  		
    						    	  		//populating zone wise map 
    						    	  		if(zoneTotMap.get(zone) != null){
						    					Map zoneDataMap = (Map)zoneTotMap.get(zone);
    						    				if(zoneDataMap.get(productSubscriptionTypeId)!= null){
    						    					Map zoneProdSubTypeMap = (Map)zoneDataMap.get(productSubscriptionTypeId);
    						    					if(zoneProdSubTypeMap.get(productId)!= null){
						    							Map zoneProdMap = (Map)zoneProdSubTypeMap.get(productId);
						    							BigDecimal runningTotalProductQty = (BigDecimal)zoneProdMap.get("totalQty");
	    						    					BigDecimal runningProductRevenue = (BigDecimal)zoneProdMap.get("totalRevenue");
	    						    					runningTotalProductQty = runningTotalProductQty.add(totQty);
	    						    					runningProductRevenue = runningProductRevenue.add(totRevenu);
	    						    					zoneProdMap.put("totalQty", runningTotalProductQty);
	    						    					zoneProdMap.put("totalRevenue", runningProductRevenue);
	    						        				zoneProdSubTypeMap.put(productId, zoneProdMap);
						    						}else{
						    							Map<String, Object> zoneProdMap = FastMap.newInstance();
						    							zoneProdMap.put("totalQty", totQty);
						    							zoneProdMap.put("totalRevenue", totRevenu);
	    						        				zoneProdSubTypeMap.put(productId, zoneProdMap);
						    						}	
    						    					zoneDataMap.put(productSubscriptionTypeId,zoneProdSubTypeMap);
    						    				}else{
    						    					Map<String, Object> zoneProdSubTypeMap = FastMap.newInstance();
    						    					Map<String, Object> zoneProdMap = FastMap.newInstance();
    						    					zoneProdMap.put("totalQty", totQty);
    						    					zoneProdMap.put("totalRevenue", totRevenu);
    						    					zoneProdSubTypeMap.put(productId, zoneProdMap);
    						    					zoneDataMap.put(productSubscriptionTypeId, zoneProdSubTypeMap);
    						    				}
    						    				zoneTotMap.put(zone, zoneDataMap);
						    				}else{
						    					Map zoneDataMap = FastMap.newInstance();
						    					Map<String, Object> zoneProdSubTypeMap = FastMap.newInstance();
						    					Map<String, Object> zoneProdMap = FastMap.newInstance();
						    					zoneProdMap.put("totalQty", totQty);
						    					zoneProdMap.put("totalRevenue", totRevenu);
						    					zoneProdSubTypeMap.put(productId, zoneProdMap);
						    					zoneDataMap.put(productSubscriptionTypeId, zoneProdSubTypeMap);
						    					zoneTotMap.put(zone, zoneDataMap);
						    				}
    						    	  		
    						    	  		
    						    	  		//booth wise Monthly sale 
    						    	  		GenericValue salesSummaryDetail = delegator.makeValue("LMSPeriodSalesSummaryDetail");
        									salesSummaryDetail.put("salesDate", startDate);
        									salesSummaryDetail.put("totalQuantity", totQty);
        									salesSummaryDetail.put("totalRevenue", totRevenu);  
        									salesSummaryDetail.put("shipmentTypeId", shipment.toString());
        									salesSummaryDetail.put("facilityId",booth);
        									salesSummaryDetail.put("productSubscriptionTypeId", productSubscriptionTypeId);
        									salesSummaryDetail.put("productId", productId);
        									salesSummaryDetail.put("periodTypeId", "SALES_MONTH");
        									delegator.createOrStore(salesSummaryDetail);
    						    	  	}	
    	    				    	}//prod
    	    			    	}//prodSubTyp
    				    	}//booth
    	        			
    	        			//route wise Monthly sale
    	        			if (routesTotMap.get(route) != null) {
    	        				Map routeDataMap = (Map)routesTotMap.get(route);
        						Iterator routeIterator = routeDataMap.entrySet().iterator();
        						while (routeIterator.hasNext()) {//booth wise
        							Map.Entry routeEntry = (Entry) routeIterator.next();
        							Map routeProdSubTypeMap = FastMap.newInstance();
        							routeProdSubTypeMap.clear();
        							routeProdSubTypeMap = (Map) routeEntry.getValue();
        							String productSubscriptionTypeId = (String) routeEntry.getKey();
        							Iterator mapProdIterator = routeProdSubTypeMap.entrySet().iterator();
        							while (mapProdIterator.hasNext()) {//product wise
        								Map.Entry entry = (Entry) mapProdIterator.next();
        								Map productsSalesMap = FastMap.newInstance();
        								productsSalesMap.clear();
        								productsSalesMap = (Map) entry.getValue();
        								String productId = (String) entry.getKey();
        								BigDecimal routeQuantity = (BigDecimal)productsSalesMap.get("totalQty");
        								BigDecimal routeRevenue = (BigDecimal)productsSalesMap.get("totalRevenue");
        								GenericValue salesSummaryRouteDetail = delegator.makeValue("LMSPeriodSalesSummaryDetail");
        	    	        			salesSummaryRouteDetail.put("salesDate", startDate);
        				    	  		salesSummaryRouteDetail.put("totalQuantity", routeQuantity);
        				    	  		salesSummaryRouteDetail.put("totalRevenue", routeRevenue);  
        				    	  		salesSummaryRouteDetail.put("shipmentTypeId", shipment.toString());
        				    	  		salesSummaryRouteDetail.put("facilityId",route);
        				    	  		salesSummaryRouteDetail.put("productSubscriptionTypeId", productSubscriptionTypeId);
        				    	  		salesSummaryRouteDetail.put("productId", productId);
        				    	  		salesSummaryRouteDetail.put("periodTypeId", "SALES_MONTH");		
        	    	        			delegator.createOrStore(salesSummaryRouteDetail);
        							}
        						}
    	        			}
            			}//route	
    	        		
    	        		//zone wise Monthly sale
    	        		if (zoneTotMap.get(zone) != null) {
	        				Map zoneDataMap = (Map)zoneTotMap.get(zone);
    						Iterator zoneIterator = zoneDataMap.entrySet().iterator();
    						while (zoneIterator.hasNext()) {//booth wise
    							Map.Entry zoneEntry = (Entry) zoneIterator.next();
    							Map zoneProdSubTypeMap = FastMap.newInstance();
    							zoneProdSubTypeMap.clear();
    							zoneProdSubTypeMap = (Map) zoneEntry.getValue();
    							String productSubscriptionTypeId = (String) zoneEntry.getKey();
    							Iterator mapProdIterator = zoneProdSubTypeMap.entrySet().iterator();
    							while (mapProdIterator.hasNext()) {//product wise
    								Map.Entry entry = (Entry) mapProdIterator.next();
    								Map productsSalesMap = FastMap.newInstance();
    								productsSalesMap.clear();
    								productsSalesMap = (Map) entry.getValue();
    								String productId = (String) entry.getKey();
    								BigDecimal zoneQuantity = (BigDecimal)productsSalesMap.get("totalQty");
    								BigDecimal zoneRevenue = (BigDecimal)productsSalesMap.get("totalRevenue");
    								GenericValue salesSummaryzoneDetail = delegator.makeValue("LMSPeriodSalesSummaryDetail");
    	    	        			salesSummaryzoneDetail.put("salesDate", startDate);
    				    	  		salesSummaryzoneDetail.put("totalQuantity", zoneQuantity);
    				    	  		salesSummaryzoneDetail.put("totalRevenue", zoneRevenue);  
    				    	  		salesSummaryzoneDetail.put("shipmentTypeId", shipment.toString());
    				    	  		salesSummaryzoneDetail.put("facilityId",zone);
    				    	  		salesSummaryzoneDetail.put("productSubscriptionTypeId", productSubscriptionTypeId);
    				    	  		salesSummaryzoneDetail.put("productId", productId);
    				    	  		salesSummaryzoneDetail.put("periodTypeId", "SALES_MONTH");		
    	    	        			delegator.createOrStore(salesSummaryzoneDetail);
    							}
    						}
	        			}
    	        	}//zone
            	}//shipment
        	} catch (GenericEntityException e) {
        		Debug.logError(e, module);
        	}
	        	
			return ServiceUtil.returnSuccess("update successfully done!");		
		}

	    public static BigDecimal sum(List<BigDecimal> list) {
	    	BigDecimal sum = BigDecimal.ZERO; 
	        for (BigDecimal i:list)
	        	sum = sum.add(i);
	        return sum;
	    }
}
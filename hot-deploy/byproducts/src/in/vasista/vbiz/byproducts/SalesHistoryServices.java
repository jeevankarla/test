

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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;







import java.text.SimpleDateFormat;

public class SalesHistoryServices {
	
	public static final String module = SalesHistoryServices.class.getName();  
	private static BigDecimal ZERO = BigDecimal.ZERO;
	private static int decimals;
	private static int rounding;
	private static String obInvoiceType = "OBINVOICE_IN";
	public static final String resource_error = "OrderErrorUiLabels";
	static {
		decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
		rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

		// set zero to the proper scale
		if (decimals != -1) ZERO = ZERO.setScale(decimals);
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
		        Debug.log("====Populate periodBilling is callled=====================in new services...====");
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
						    		BigDecimal dayTotQty = BigDecimal.ZERO;
						    		BigDecimal commision = BigDecimal.ZERO;
						    		
						    		if(UtilValidate.isNotEmpty(dayRouteEntryMap.get("quantity"))){
						    		 dayTotQty=(BigDecimal)dayRouteEntryMap.get("quantity");
						    		}
						    		if(UtilValidate.isNotEmpty(dayRouteEntryMap.get("commision"))){
						    			commision=(BigDecimal)dayRouteEntryMap.get("commision");
							    	}
						    		
									facilityCommission = delegator.makeValue("FacilityCommission");
									facilityCommission.put("periodBillingId", periodBillingId );
									facilityCommission.put("commissionDate", dateValue);
									facilityCommission.put("facilityId", facilityId); 
									facilityCommission.put("partyId", dayRouteEntryMap.get("partyId")); 
									facilityCommission.put("totalQty", dayTotQty);									
									facilityCommission.put("totalAmount", commision);
									facilityCommission.put("dues", dayRouteEntryMap.get("dueAmount"));
									facilityCommission.create();    
								}else { 
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
			shipmentList.add("DIRECT");
			String productId = null;
			String facProductId = null;
			String boothId = null;
			Object quantity = null;
			Object revenue = null;
			Object productSubscriptionTypeId = null;
			Object boothProductSubscriptionTypeId = null;
			Object facQuantity = null;
			Object facRevenue = null;
			Map productsMap = FastMap.newInstance();
			Map productsReturnMap = FastMap.newInstance();
			Map boothTotals = FastMap.newInstance();
			Map boothReturnTotals = FastMap.newInstance();
			List shipmentIds=FastList.newInstance();
			Map<String, Object> zonesMap = ByProductNetworkServices.getZones(delegator);
      	List<String> zones = FastList.newInstance(); //(List)zonesMap.get("zonesList");
      	Map routes = ByProductNetworkServices.getRoutes(dctx , UtilMisc.toMap("facilityTypeId", "ROUTE"));		
      	List<String> routesList = (List<String>) routes.get("routesList");
			try{
				for(int i=0;i<=IntervalDays;i++){
					Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDate, +i);
					Date summaryDate = new Date(saleDate.getTime());
					for(Object shipment : shipmentList){
						Debug.log("SummaryDate==="+summaryDate+"===========SupplyType=="+shipment.toString());
						Map<String, Object> salesTotals = ByProductNetworkServices.getDayTotals(dctx, saleDate, shipment.toString() , false, false, null);
						productsMap = (Map)salesTotals.get("productTotals");
						shipmentIds=(List)salesTotals.get("shipmentIds");
						//returns for eachDay
						Map<String, Object> salesReturnTotals = ByProductNetworkServices.getDayReturnTotals(dctx, UtilMisc.toMap("salesDate",saleDate,"subscriptionType",shipment.toString()));
						productsReturnMap= (Map)salesReturnTotals.get("productTotals");
						
						Iterator mapIterator = productsMap.entrySet().iterator();
						while (mapIterator.hasNext()) {//product wise
							Map.Entry entry = (Entry) mapIterator.next();
							Map productsSalesMap = FastMap.newInstance();
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
						//populating Return Product Totals
						if(!UtilValidate.isEmpty(productsReturnMap)){
							Iterator mapIteratorReturn = productsReturnMap.entrySet().iterator();
							while (mapIteratorReturn.hasNext()) {//product wise
								Map.Entry entry = (Entry) mapIteratorReturn.next();
								Map productsSalesMap = FastMap.newInstance();
								productsSalesMap = (Map) entry.getValue();
								productId = (String) entry.getKey();
								Map tempMap = FastMap.newInstance();
								productSubscriptionTypeId = "_NA_";
								quantity = productsSalesMap.get("total");
								revenue = productsSalesMap.get("totalRevenue");
								try{
									GenericValue salesSummary = delegator.makeValue("LMSPeriodSalesSummary");
									salesSummary.put("salesDate", summaryDate);
									salesSummary.put("totalQuantity", quantity);
									salesSummary.put("totalRevenue", revenue);  
									salesSummary.put("shipmentTypeId", shipment.toString());
									salesSummary.put("productSubscriptionTypeId", productSubscriptionTypeId);
									salesSummary.put("productId", productId);
									salesSummary.put("periodTypeId", "SALES_DAY");
									salesSummary.put("isReturn", "Y");
									delegator.createOrStore(salesSummary);
								} catch (GenericEntityException e) {
									Debug.logError(e, module);
								}
							}//product
						}
						
						//populating LMSPeriodSalesSummaryDetail
						boothTotals = (Map)salesTotals.get("boothTotals");
						boothReturnTotals = (Map)salesReturnTotals.get("boothTotals");
					
						if(!UtilValidate.isEmpty(boothTotals)){
	    	        		for(String route : routesList){
	    	        			Map<String, Object> routesTotReturnMap = new TreeMap<String, Object>();
	    	        			Map<String, Object> routesTotMap = new TreeMap<String, Object>();
	    	        			List<String> routeBooths = FastList.newInstance();
	    	        			//routeBooths = ByProductNetworkServices.getRouteBooths(delegator,route,null);
	    	        			routeBooths =(List<String>) ByProductNetworkServices.getBoothsRouteByShipment(delegator,UtilMisc.toMap("facilityId", route,"shipmentIds",shipmentIds,"effectiveDate",UtilDateTime.getDayStart(saleDate))).get("boothIdsList");
	    	        			
	    	        			for(String booth : routeBooths){
	    	        				//populating Booths
	    	        				Map boothSalesMap = (Map) boothTotals.get(booth);
	    	        				if(boothSalesMap != null){
	    	        					Map boothProductsMap = FastMap.newInstance();
	    	        					boothProductsMap = (Map)boothSalesMap.get("productTotals");
	    								Iterator mapProdIterator = boothProductsMap.entrySet().iterator();
	    								while (mapProdIterator.hasNext()) 
	    								{//product wise
	    									Map.Entry prodEntry = (Entry) mapProdIterator.next();
	    									Map productsBoothSalesMap = FastMap.newInstance();
	    									productsBoothSalesMap = (Map) prodEntry.getValue();
	    									facProductId = (String) prodEntry.getKey();
	    									Map tempMapSupplyType = FastMap.newInstance();
	    									tempMapSupplyType = (Map) productsBoothSalesMap.get("supplyTypeTotals");
	    									Iterator boothSupplyTypeIter = tempMapSupplyType.entrySet().iterator(); 
	    									while (boothSupplyTypeIter.hasNext()) {//supply type wise
	    										Map.Entry entry = (Entry) boothSupplyTypeIter.next();
	    										Map supplyTypeMap = FastMap.newInstance();
	    										supplyTypeMap = (Map) (entry.getValue());
	    										boothProductSubscriptionTypeId = supplyTypeMap.get("name");
	    										facQuantity = supplyTypeMap.get("total");
	    										facRevenue = supplyTypeMap.get("totalRevenue");
	    										
	    										if((facRevenue != BigDecimal.ZERO) && (facQuantity != BigDecimal.ZERO)){
	    											//populating route wise map 
	        						    	  		if(routesTotMap.get(route) != null){
	    						    					Map routeDataMap = (Map)routesTotMap.get(route);
	        						    				if(routeDataMap.get(boothProductSubscriptionTypeId)!= null){
	        						    					Map routeProdSubTypeMap = (Map)routeDataMap.get(boothProductSubscriptionTypeId);
	        						    					if(routeProdSubTypeMap.get(facProductId)!= null){
	    						    							Map routeProdMap = (Map)routeProdSubTypeMap.get(facProductId);
	    						    							BigDecimal runningTotalProductQty = (BigDecimal)routeProdMap.get("totalQty");
	    	    						    					BigDecimal runningProductRevenue = (BigDecimal)routeProdMap.get("totalRevenue");
	    	    						    					runningTotalProductQty = runningTotalProductQty.add((BigDecimal)facQuantity);
	    	    						    					runningProductRevenue = runningProductRevenue.add((BigDecimal)facRevenue);
	    	    						    					routeProdMap.put("totalQty", runningTotalProductQty);
	    	    						        				routeProdMap.put("totalRevenue", runningProductRevenue);
	    	    						        				routeProdSubTypeMap.put(facProductId, routeProdMap);
	    						    						}else{
	    						    							Map<String, Object> routeProdMap = FastMap.newInstance();
	    						    							routeProdMap.put("totalQty", facQuantity);
	    	    						        				routeProdMap.put("totalRevenue", facRevenue);
	    	    						        				routeProdSubTypeMap.put(facProductId, routeProdMap);
	    						    						}	
	        						    					routeDataMap.put(boothProductSubscriptionTypeId,routeProdSubTypeMap);
	        						    				}else{
	        						    					Map<String, Object> routeProdSubTypeMap = FastMap.newInstance();
	        						    					Map<String, Object> routeProdMap = FastMap.newInstance();
	        						    					routeProdMap.put("totalQty", facQuantity);
	        						    					routeProdMap.put("totalRevenue", facRevenue);
	        						    					routeProdSubTypeMap.put(facProductId, routeProdMap);
	        						    					routeDataMap.put(boothProductSubscriptionTypeId, routeProdSubTypeMap);
	        						    				}
	        						    				routesTotMap.put(route, routeDataMap);
	    						    				}else{
	    						    					Map routeDataMap = FastMap.newInstance();
	    						    					Map<String, Object> routeProdSubTypeMap = FastMap.newInstance();
	    						    					Map<String, Object> routeProdMap = FastMap.newInstance();
	    						    					routeProdMap.put("totalQty", facQuantity);
	    						    					routeProdMap.put("totalRevenue", facRevenue);
	    						    					routeProdSubTypeMap.put(facProductId, routeProdMap);
	    						    					routeDataMap.put(boothProductSubscriptionTypeId, routeProdSubTypeMap);
	    						    					routesTotMap.put(route, routeDataMap);
	    						    				}
	        						    	  		
	        						    	  		
	        						    	  	//populating zone wise map 
	        						    	/*  		if(zoneTotMap.get(zone) != null){
	    						    					Map zoneDataMap = (Map)zoneTotMap.get(zone);
	        						    				if(zoneDataMap.get(boothProductSubscriptionTypeId)!= null){
	        						    					Map zoneProdSubTypeMap = (Map)zoneDataMap.get(boothProductSubscriptionTypeId);
	        						    					if(zoneProdSubTypeMap.get(facProductId)!= null){
	    						    							Map zoneProdMap = (Map)zoneProdSubTypeMap.get(facProductId);
	    						    							BigDecimal runningTotalProductQty = (BigDecimal)zoneProdMap.get("totalQty");
	    	    						    					BigDecimal runningProductRevenue = (BigDecimal)zoneProdMap.get("totalRevenue");
	    	    						    					runningTotalProductQty = runningTotalProductQty.add((BigDecimal)facQuantity);
	    	    						    					runningProductRevenue = runningProductRevenue.add((BigDecimal)facRevenue);
	    	    						    					zoneProdMap.put("totalQty", runningTotalProductQty);
	    	    						    					zoneProdMap.put("totalRevenue", runningProductRevenue);
	    	    						        				zoneProdSubTypeMap.put(facProductId, zoneProdMap);
	    						    						}else{
	    						    							Map<String, Object> zoneProdMap = FastMap.newInstance();
	    						    							zoneProdMap.put("totalQty", facQuantity);
	    						    							zoneProdMap.put("totalRevenue", facRevenue);
	    	    						        				zoneProdSubTypeMap.put(facProductId, zoneProdMap);
	    						    						}	
	        						    					zoneDataMap.put(boothProductSubscriptionTypeId,zoneProdSubTypeMap);
	        						    				}else{
	        						    					Map<String, Object> zoneProdSubTypeMap = FastMap.newInstance();
	        						    					Map<String, Object> zoneProdMap = FastMap.newInstance();
	        						    					zoneProdMap.put("totalQty", facQuantity);
	        						    					zoneProdMap.put("totalRevenue", facRevenue);
	        						    					zoneProdSubTypeMap.put(facProductId, zoneProdMap);
	        						    					zoneDataMap.put(boothProductSubscriptionTypeId, zoneProdSubTypeMap);
	        						    				}
	        						    				zoneTotMap.put(zone, zoneDataMap);
	    						    				}else{
	    						    					Map zoneDataMap = FastMap.newInstance();
	    						    					Map<String, Object> zoneProdSubTypeMap = FastMap.newInstance();
	    						    					Map<String, Object> zoneProdMap = FastMap.newInstance();
	    						    					zoneProdMap.put("totalQty", facQuantity);
	    						    					zoneProdMap.put("totalRevenue", facRevenue);
	    						    					zoneProdSubTypeMap.put(facProductId, zoneProdMap);
	    						    					zoneDataMap.put(boothProductSubscriptionTypeId, zoneProdSubTypeMap);
	    						    					zoneTotMap.put(zone, zoneDataMap);
	    						    				} */
	    											//booth
	    											GenericValue salesSummaryDetail = delegator.makeValue("LMSPeriodSalesSummaryDetail");
		    										salesSummaryDetail.put("salesDate", summaryDate);
		    										salesSummaryDetail.put("totalQuantity", facQuantity);
		    										salesSummaryDetail.put("totalRevenue", facRevenue);  
		    										salesSummaryDetail.put("facilityId",booth);
		    										salesSummaryDetail.put("shipmentTypeId", shipment.toString());
		    										salesSummaryDetail.put("productSubscriptionTypeId", boothProductSubscriptionTypeId);
		    										salesSummaryDetail.put("productId", facProductId);
		    										salesSummaryDetail.put("periodTypeId", "SALES_DAY");
		    										delegator.createOrStore(salesSummaryDetail);
	    										}//check nulls
	    									}//product
	    								}//ProductSubscriptionType
	    							}//check for nulls in booth wise totals
	    	        			}//booth
	    	        			//route wise Monthly sale
								Map<String, Object> populateRouteTotMap = FastMap.newInstance();
								populateRouteTotMap.put("facilityId", route);
								populateRouteTotMap.put("facilityDataMap", routesTotMap);
								populateRouteTotMap.put("salesDate", summaryDate);
								populateRouteTotMap.put("periodTypeId", "SALES_DAY");
								populateRouteTotMap.put("shipmentTypeId", shipment.toString());
								SalesHistoryServices.populateFacilitySummaryDetailsMap(dctx, populateRouteTotMap);
								
								//populatingReturn Totals 
								for(String booth : routeBooths){
	    	        				//populating Booths
	    	        				Map boothReturnSalesMap = (Map) boothReturnTotals.get(booth);
	    	        				if(boothReturnSalesMap != null){
	    	        					Map boothProductsMap = FastMap.newInstance();
	    	        					boothProductsMap = (Map)boothReturnSalesMap.get("productTotals");
	    	        				//	Debug.log("==boothProductsReturnMap===="+boothProductsMap+"====BoothId=="+booth);
	    								Iterator mapProdIterator = boothProductsMap.entrySet().iterator();
	    								while (mapProdIterator.hasNext()) 
	    								{//product wise
	    									Map.Entry prodEntry = (Entry) mapProdIterator.next();
	    									Map productsBoothSalesMap = FastMap.newInstance();
	    									productsBoothSalesMap = (Map) prodEntry.getValue();
	    									facProductId = (String) prodEntry.getKey();
    										boothProductSubscriptionTypeId = "_NA_";
    										facQuantity = productsBoothSalesMap.get("total");
    										facRevenue = productsBoothSalesMap.get("totalRevenue");
    										//negate them because return values reduced from main values directly
    										BigDecimal tempFacQuantity = (BigDecimal)productsBoothSalesMap.get("total");
					    					BigDecimal tempFacRevenue = (BigDecimal)productsBoothSalesMap.get("totalRevenue");
					    					
    										facQuantity=(BigDecimal)tempFacQuantity.negate();
    										facRevenue=(BigDecimal)tempFacRevenue.negate();
    										
	    										if((facRevenue != BigDecimal.ZERO) && (facQuantity != BigDecimal.ZERO)){
	    											//populating route wise map 
	        						    	  		if(routesTotReturnMap.get(route) != null){
	    						    					Map routeDataMap = (Map)routesTotReturnMap.get(route);
	        						    				if(routeDataMap.get(boothProductSubscriptionTypeId)!= null){
	        						    					Map routeProdSubTypeMap = (Map)routeDataMap.get(boothProductSubscriptionTypeId);
	        						    					if(routeProdSubTypeMap.get(facProductId)!= null){
	    						    							Map routeProdMap = (Map)routeProdSubTypeMap.get(facProductId);
	    						    							BigDecimal runningTotalProductQty = (BigDecimal)routeProdMap.get("totalQty");
	    	    						    					BigDecimal runningProductRevenue = (BigDecimal)routeProdMap.get("totalRevenue");
	    	    						    					runningTotalProductQty = runningTotalProductQty.add((BigDecimal)facQuantity);
	    	    						    					runningProductRevenue = runningProductRevenue.add((BigDecimal)facRevenue);
	    	    						    					routeProdMap.put("totalQty", runningTotalProductQty);
	    	    						        				routeProdMap.put("totalRevenue", runningProductRevenue);
	    	    						        				routeProdSubTypeMap.put(facProductId, routeProdMap);
	    						    						}else{
	    						    							Map<String, Object> routeProdMap = FastMap.newInstance();
	    						    							routeProdMap.put("totalQty", facQuantity);
	    	    						        				routeProdMap.put("totalRevenue", facRevenue);
	    	    						        				routeProdSubTypeMap.put(facProductId, routeProdMap);
	    						    						}	
	        						    					routeDataMap.put(boothProductSubscriptionTypeId,routeProdSubTypeMap);
	        						    				}else{
	        						    					Map<String, Object> routeProdSubTypeMap = FastMap.newInstance();
	        						    					Map<String, Object> routeProdMap = FastMap.newInstance();
	        						    					routeProdMap.put("totalQty", facQuantity);
	        						    					routeProdMap.put("totalRevenue", facRevenue);
	        						    					routeProdSubTypeMap.put(facProductId, routeProdMap);
	        						    					routeDataMap.put(boothProductSubscriptionTypeId, routeProdSubTypeMap);
	        						    				}
	        						    				routesTotReturnMap.put(route, routeDataMap);
	    						    				}else{
	    						    					Map routeDataMap = FastMap.newInstance();
	    						    					Map<String, Object> routeProdSubTypeMap = FastMap.newInstance();
	    						    					Map<String, Object> routeProdMap = FastMap.newInstance();
	    						    					routeProdMap.put("totalQty", facQuantity);
	    						    					routeProdMap.put("totalRevenue", facRevenue);
	    						    					routeProdSubTypeMap.put(facProductId, routeProdMap);
	    						    					routeDataMap.put(boothProductSubscriptionTypeId, routeProdSubTypeMap);
	    						    					routesTotReturnMap.put(route, routeDataMap);
	    						    				}
	        						    	
	    											//booth
	    											GenericValue salesSummaryDetail = delegator.makeValue("LMSPeriodSalesSummaryDetail");
		    										salesSummaryDetail.put("salesDate", summaryDate);
		    										salesSummaryDetail.put("totalQuantity", facQuantity);
		    										salesSummaryDetail.put("totalRevenue", facRevenue);  
		    										salesSummaryDetail.put("facilityId",booth);
		    										salesSummaryDetail.put("shipmentTypeId", shipment.toString());
		    										salesSummaryDetail.put("productSubscriptionTypeId", boothProductSubscriptionTypeId);
		    										salesSummaryDetail.put("productId", facProductId);
		    										salesSummaryDetail.put("periodTypeId", "SALES_DAY");
		    										salesSummaryDetail.put("isReturn", "Y");
		    										delegator.createOrStore(salesSummaryDetail);
	    										}//check nulls
	    								}//product return
	    							}//check for nulls in boothReturn wise totals
	    	        			}//booth return end
								//route wise Monthly sale
								Map<String, Object> populateRouteRetTotMap = FastMap.newInstance();
								populateRouteRetTotMap.put("facilityId", route);
								populateRouteRetTotMap.put("facilityDataMap", routesTotReturnMap);
								populateRouteRetTotMap.put("salesDate", summaryDate);
								populateRouteRetTotMap.put("periodTypeId", "SALES_DAY");
								populateRouteRetTotMap.put("shipmentTypeId", shipment.toString());
								populateRouteRetTotMap.put("isReturn", "Y");
								
								SalesHistoryServices.populateFacilitySummaryDetailsMap(dctx, populateRouteRetTotMap);
	    	        		}//route
					}//Booth Totals NotEmpty 
	    	        						
	    	        }//shipment
					
					Debug.logImportant(i+1+"    Days Completed For AM & PM", "");
					
					Timestamp monthStart = UtilDateTime.getMonthStart(saleDate);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(saleDate, timeZone, locale);
					Timestamp currentDate = UtilDateTime.getDayEnd(saleDate);
					if(monthEnd.equals(currentDate)){
						//populating monthly totals at start date of the month.
						SalesHistoryServices.populateLMSMonthlySalesSummary(dctx, context, "SALES_MONTH",monthStart,monthEnd);
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
			shipmentList.add("DIRECT");
			List<GenericValue> lmsSalesSummaryList = FastList.newInstance(); 
			List<GenericValue> lmsSalesSummaryDetailList = FastList.newInstance();
			List<GenericValue> lmsSalesSummaryBoothsList = FastList.newInstance();
			List<GenericValue> lmsSalesBoothDayWiseDataList = FastList.newInstance();
			Date startDate = new Date(periodStart.getTime());
			Date endDate = new Date(periodEnd.getTime());
			List<GenericValue> shipmentWise = FastList.newInstance();
      	List<GenericValue> productSubscriptionTypeWise= FastList.newInstance();
      	List<GenericValue> productWise = FastList.newInstance();
      	List<GenericValue> boothWise = FastList.newInstance();
      	List<GenericValue> summaryBooths = FastList.newInstance();
      	List<GenericValue> facilitiesToClearList = FastList.newInstance();
      	Map<String, Object> zonesMap = ByProductNetworkServices.getZones(delegator);
      	List<String> zones = (List)zonesMap.get("zonesList");
      	
      	Map routes = ByProductNetworkServices.getRoutes(dctx , UtilMisc.toMap("facilityTypeId", "ROUTE"));		
      	List<String> routesList = (List<String>) routes.get("routesList");
      	Debug.log("Populating Monthly Data From======> "+startDate+" AND EndDate== "+endDate, "");
      	try{
      		List conditionsList = FastList.newInstance();
  			conditionsList.add(EntityCondition.makeCondition("salesDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
  			conditionsList.add(EntityCondition.makeCondition("salesDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
  			conditionsList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_DAY"));
          	EntityCondition condition = EntityCondition.makeCondition(conditionsList,EntityOperator.AND);
          	productSubscriptionTypeList = delegator.findList("Enumeration",	EntityCondition.makeCondition("enumId", EntityOperator.IN, UtilMisc.toList("CASH","CREDIT","EMP_SUBSIDY","_NA_")), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"),null, false);
  			//productSubscriptionTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "SUB_PROD_TYPE"), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"), null, false);
  			productList  = ByProductNetworkServices.getAllProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
      		lmsSalesSummaryList = delegator.findList("LMSPeriodSalesSummary", condition, null,null, null, false);
      		lmsSalesSummaryDetailList = delegator.findList("LMSPeriodSalesSummaryDetail", condition, null,null, null, false);
      		Debug.log("lmsSalesSummaryDetailList Size======> "+lmsSalesSummaryDetailList.size()+"===Bfr=Facility===", "");
      		lmsSalesSummaryBoothsList = EntityUtil.getFieldListFromEntityList(lmsSalesSummaryDetailList, "facilityId", true);
      		
      		Debug.logImportant("Populating Monthly Data From ======>"+lmsSalesSummaryBoothsList.size()+" Facilities", "");
      		//Debug.log("Populating Monthly Data From======> "+lmsSalesSummaryBoothsList.size()+" Facilities", "");
      		conditionsList.clear();
      		conditionsList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, lmsSalesSummaryBoothsList));
  			conditionsList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "BOOTH"));
          	EntityCondition conditionClrSummaryFac = EntityCondition.makeCondition(conditionsList,EntityOperator.AND);
      		facilitiesToClearList = delegator.findList("Facility",conditionClrSummaryFac , UtilMisc.toSet("facilityId"), null, null, false);

      		summaryBooths = EntityUtil.getFieldListFromEntityList(facilitiesToClearList, "facilityId", true);
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
  			        	String isReturn="";
  			        	
  			            GenericValue prodType = prodSubTypeIter.next();
  			            String productId = prodType.getString("productId");
  			            productWise = EntityUtil.filterByAnd(productSubscriptionTypeWise, UtilMisc.toList(EntityCondition.makeCondition("productId", productId)));
  			            Iterator<GenericValue> prodTypeIter = productWise.iterator();
  				    	while(prodTypeIter.hasNext()) {
  				    		GenericValue Type = prodTypeIter.next();
  				    		revenuForPeriod.add(Type.getBigDecimal("totalRevenue"));
  				    		qtyForPeriod.add(Type.getBigDecimal("totalQuantity"));
  				    	}
  				    	totQty = SalesHistoryServices.sum(qtyForPeriod);
  				    	totRevenu = SalesHistoryServices.sum(revenuForPeriod);
  				    	if((totQty != BigDecimal.ZERO) && (totRevenu != BigDecimal.ZERO)){
  				    		GenericValue salesSummary = delegator.makeValue("LMSPeriodSalesSummary");
								salesSummary.put("salesDate", startDate);
								salesSummary.put("totalQuantity", totQty);
								salesSummary.put("totalRevenue", totRevenu);  
								salesSummary.put("shipmentTypeId", shipment.toString());
								salesSummary.put("productSubscriptionTypeId", productSubscriptionTypeId);
								salesSummary.put("productId", productId);
								salesSummary.put("periodTypeId", "SALES_MONTH");
								salesSummary.put("isReturn", prodType.getString("isReturn"));
								delegator.createOrStore(salesSummary);	
  				    	}
  			    	}
  		    	}
  		    	
  		    	shipmentWise.clear();
  		    	productSubscriptionTypeWise.clear();
  	        	productWise.clear();
  	        	
  	        	//summary Detail
  		    	shipmentWise = EntityUtil.filterByAnd(lmsSalesSummaryDetailList, UtilMisc.toList(EntityCondition.makeCondition("shipmentTypeId", shipment.toString())));
  	        /*	for(String zone : zones){
  	        		Map<String, Object> zoneTotMap = new TreeMap<String, Object>();
  	        		List<String> zoneRoutes = FastList.newInstance();
  	        		zoneRoutes = ByProductNetworkServices.getZoneRoutes(delegator,zone);*/
  		    	if(("AM".equals(shipment.toString())) || ("PM".equals(shipment.toString()))){
  	  		    	routesList=(List<String>)ByProductNetworkServices.getRoutesByAMPM(dctx , UtilMisc.toMap("supplyType", shipment.toString())).get("routeIdsList");
  	  		    	}
  	  		    	//for Direct Sales Default we will get AM ROUTES
  	  		    	if("DIRECT".equals(shipment.toString())){
  	  	  		    	routesList=(List<String>)ByProductNetworkServices.getRoutesByAMPM(dctx , UtilMisc.toMap("supplyType", "AM")).get("routeIdsList");
  	  	  		    }
  	        		for(String route : routesList){
  	        			Map<String, Object> routesTotMap = new TreeMap<String, Object>();
  	        			List<String> routeBooths = FastList.newInstance();
  	        			routeBooths = ByProductNetworkServices.getRouteBooths(delegator,route,null);
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
  	    					    	totQty = SalesHistoryServices.sum(qtyForPeriod);
  	    					    	totRevenu = SalesHistoryServices.sum(revenuForPeriod);
  	    					    	
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
	    						        				routeProdMap.put("isReturn",prodType.getString("isReturn"));
	    						        				routeProdSubTypeMap.put(productId, routeProdMap);
						    						}	
  						    					routeDataMap.put(productSubscriptionTypeId,routeProdSubTypeMap);
  						    				}else{
  						    					Map<String, Object> routeProdSubTypeMap = FastMap.newInstance();
  						    					Map<String, Object> routeProdMap = FastMap.newInstance();
  						    					routeProdMap.put("totalQty", totQty);
  						    					routeProdMap.put("totalRevenue", totRevenu);
  						    					routeProdMap.put("isReturn", prodType.getString("isReturn"));
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
						    					routeProdMap.put("isReturn", prodType.getString("isReturn"));
						    					routeProdSubTypeMap.put(productId, routeProdMap);
						    					routeDataMap.put(productSubscriptionTypeId, routeProdSubTypeMap);
						    					routesTotMap.put(route, routeDataMap);
						    				}
  						    	  		
  						    	  		
  						    	  		//populating zone wise map 
  						    	  	/*	if(zoneTotMap.get(zone) != null){
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
						    				}*/
  						    	  		
  						    	  		
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
      									salesSummaryDetail.put("isReturn",prodType.getString("isReturn"));
      									delegator.createOrStore(salesSummaryDetail);
  						    	  	}	
  	    				    	}//prod
  	    			    	}//prodSubTyp
  				    	}//booth
  	        			
  	        			//route wise Monthly sale
  	        			Map<String, Object> populateRouteTotMap = FastMap.newInstance();
							populateRouteTotMap.put("facilityId", route);
							populateRouteTotMap.put("facilityDataMap", routesTotMap);
							populateRouteTotMap.put("salesDate", startDate);
							populateRouteTotMap.put("periodTypeId", "SALES_MONTH");
							populateRouteTotMap.put("shipmentTypeId", shipment.toString());
							SalesHistoryServices.populateFacilitySummaryDetailsMap(dctx, populateRouteTotMap);
          			}//route	
  	        		
  	        		/*//zone wise Monthly sale
  	        		Map<String, Object> populateZoneTotMap = FastMap.newInstance();
						populateZoneTotMap.put("facilityId", zone);
						populateZoneTotMap.put("facilityDataMap", zoneTotMap);
						populateZoneTotMap.put("salesDate", startDate);
						populateZoneTotMap.put("periodTypeId", "SALES_MONTH");
						populateZoneTotMap.put("shipmentTypeId", shipment.toString());
						SalesHistoryServices.populateFacilitySummaryDetailsMap(dctx, populateZoneTotMap);
  	        	}//zone
*/            	}//shipment
      		
      		   lmsSalesSummaryDetailList.clear();
      		Debug.logImportant("Monthly Data Completed For AM & PM", "");
      		
      		/*List conditionsClearList = FastList.newInstance();
      		conditionsClearList.add(EntityCondition.makeCondition("salesDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
      		conditionsClearList.add(EntityCondition.makeCondition("salesDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
      		conditionsClearList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_DAY"));
      		conditionsClearList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,  UtilMisc.toList(summaryBooths)));
          	EntityCondition conditionClear = EntityCondition.makeCondition(conditionsClearList,EntityOperator.AND);
      		lmsSalesBoothDayWiseDataList = delegator.findList("LMSPeriodSalesSummaryDetail", conditionClear, null,null, null, false);
      		
      		//clearing All dayWise Data for current month
              delegator.removeAll(lmsSalesSummaryList);
              delegator.removeAll(lmsSalesBoothDayWiseDataList);*/
      		
      	} catch (GenericEntityException e) {
      		Debug.logError(e, module);
      	}
	        	
			return ServiceUtil.returnSuccess("update successfully done!");		
		}
	    public static Map<String, Object>  populateMonthlySalesSummary(DispatchContext dctx, Map<String, Object> context)  {
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
	    	Timestamp monthStart = UtilDateTime.getMonthStart(fromDate);
			Timestamp monthEnd = UtilDateTime.getMonthEnd(thruDate, timeZone, locale);
			
	        Debug.log("====fromDate=="+fromDate+"==thruDate=="+thruDate);
				//populating monthly totals at start date of the month.
				SalesHistoryServices.populateLMSMonthlySalesSummary(dctx, context, "SALES_MONTH",monthStart,monthEnd);
			
	        return ServiceUtil.returnSuccess("update successfully done!");		
		}
	    public static Map<String, Object> populateFacilitySummaryDetailsMap(DispatchContext dctx, Map<String, Object> context) {
			List conditionList= FastList.newInstance(); 
			LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        
	        String facilityId = (String)context.get("facilityId");
	        Map<String, Object> facilityDataMap = (Map)context.get("facilityDataMap");
	        Date salesDate = (Date)context.get("salesDate");
	        String shipmentTypeId = (String)context.get("shipmentTypeId");
	        String periodTypeId = (String)context.get("periodTypeId");
	        String isReturn = (String)context.get("isReturn");
	        try{ 
	        	if (UtilValidate.isNotEmpty(facilityDataMap)) {
					Map facDataMap = (Map)facilityDataMap.get(facilityId);
					Iterator facilityIterator = facDataMap.entrySet().iterator();
					while (facilityIterator.hasNext()) {//booth wise
						Map.Entry facilityEntry = (Entry) facilityIterator.next();
						Map facProdSubTypeMap = FastMap.newInstance();
						facProdSubTypeMap = (Map) facilityEntry.getValue();
						String productSubscriptionTypeId = (String) facilityEntry.getKey();
						Iterator mapProdIterator = facProdSubTypeMap.entrySet().iterator();
						while (mapProdIterator.hasNext()) {//product wise
							Map.Entry entry = (Entry) mapProdIterator.next();
							Map productsSalesMap = FastMap.newInstance();
							productsSalesMap = (Map) entry.getValue();
							String productId = (String) entry.getKey();
							BigDecimal Quantity = (BigDecimal)productsSalesMap.get("totalQty");
							BigDecimal Revenue = (BigDecimal)productsSalesMap.get("totalRevenue");
							if(!UtilValidate.isEmpty(isReturn)){
							isReturn=(String)productsSalesMap.get("isReturn");
							Quantity=Quantity.negate();
							Revenue=Revenue.negate();
							}
							GenericValue periodSalesSummaryDetail = delegator.makeValue("LMSPeriodSalesSummaryDetail");
		        			periodSalesSummaryDetail.put("salesDate", salesDate);
		        			periodSalesSummaryDetail.put("totalQuantity", Quantity);
		        			periodSalesSummaryDetail.put("totalRevenue", Revenue);  
		        			periodSalesSummaryDetail.put("shipmentTypeId", shipmentTypeId);
		        			periodSalesSummaryDetail.put("facilityId",facilityId);
		        			periodSalesSummaryDetail.put("productSubscriptionTypeId", productSubscriptionTypeId);
		        			periodSalesSummaryDetail.put("productId", productId);
		        			periodSalesSummaryDetail.put("periodTypeId", periodTypeId);	
		        			periodSalesSummaryDetail.put("isReturn", isReturn);	
		        			delegator.createOrStore(periodSalesSummaryDetail);
						}
					}
				}
		    } catch (Exception e) {
	    		Debug.logError(e, module);
	    	}
	    	return result;
		}
	    public static Map<String, Object>  resetLMSPeriodSalesSummary(DispatchContext dctx, Map<String, Object> context)  {
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	
	        // Convert attributes to the corresponding data types
			Locale locale = null;
			TimeZone timeZone = null;
			locale = Locale.getDefault();
			timeZone = TimeZone.getDefault();
			
			List<GenericValue> lmsSalesSummaryList = FastList.newInstance(); 
			List<GenericValue> lmsSalesSummaryDetailList = FastList.newInstance();
			
	        Timestamp fromDate = UtilDateTime.nowTimestamp(); 
	        if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
	        	fromDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("fromDate")).getTime()));	
	        }else{
	        	fromDate = UtilDateTime.getDayStart(fromDate);
	        }
	        Timestamp thruDate = UtilDateTime.nowTimestamp(); 
	        if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
	        	thruDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("thruDate")).getTime()));	
	        }else{
	        	thruDate = UtilDateTime.getDayStart(thruDate);
	        }        
	        
			Timestamp monthStart = UtilDateTime.getMonthStart(fromDate);
			Timestamp monthEnd = UtilDateTime.getMonthEnd(thruDate, timeZone, locale);
			Debug.log("======monthStart=="+monthStart+"=monthEnd===="+monthEnd);
			try{
				Date mntStartDate = new Date(monthStart.getTime());
				Date mntEndDate = new Date(monthEnd.getTime());

				List conditionsList = FastList.newInstance();
				conditionsList.add(EntityCondition.makeCondition("salesDate", EntityOperator.GREATER_THAN_EQUAL_TO, mntStartDate));
				conditionsList.add(EntityCondition.makeCondition("salesDate", EntityOperator.LESS_THAN_EQUAL_TO, mntEndDate));
				conditionsList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, UtilMisc.toList("SALES_DAY","SALES_MONTH")));
	        	EntityCondition condition = EntityCondition.makeCondition(conditionsList,EntityOperator.AND);
	        	lmsSalesSummaryList = delegator.findList("LMSPeriodSalesSummary", condition, null,null, null, false);
      		lmsSalesSummaryDetailList = delegator.findList("LMSPeriodSalesSummaryDetail", condition, null,null, null, false);
      		delegator.removeAll(lmsSalesSummaryList);
              delegator.removeAll(lmsSalesSummaryDetailList);
				
              Debug.logImportant("Data Cleared From "+mntStartDate+" TO "+mntEndDate, "");
              //Re-Populating LMSPeriodSalesSummary and LMSPeriodSalesSummaryDetail
				Map<String, Object> serviceResult = SalesHistoryServices.populateLMSPeriodSalesSummary(dctx,UtilMisc.toMap("fromDate", mntStartDate.clone(), "thruDate", mntEndDate.clone()));
				if(ServiceUtil.isError(serviceResult)){
					Debug.logError("Unable to Populate LMSPeriodSalesSummary", module);
					return ServiceUtil.returnError("Unable to Populate LMSPeriodSalesSummary");
				}	
				
			} catch (Exception e) {
				Debug.logError(e, module);
			}	
	        return ServiceUtil.returnSuccess("Reset successfully done!");		
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
		/*	Boolean enableSameDayPmEntry = Boolean.FALSE;
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
			}*/
			
			try{
				Map<String, Object> AMSalesTotals = ByProductNetworkServices.getDayTotals(dctx, UtilDateTime.toTimestamp(salesDate), "AM", false, false, null);
				populateTotals(dctx, context, salesDateString, AMSalesTotals, "AM" );
				
				Map<String, Object> PMSalesTotals = ByProductNetworkServices.getDayTotals(dctx, UtilDateTime.toTimestamp(salesDate), "PM", false, false, null);
				//for 'PM' supply type getDayTotals return the previous day PM Totals if  enableSameDayPmEntry set 'TRUE' 
				populateTotals(dctx, context, tempPMDate, PMSalesTotals, "PM");
			
			} catch (Exception e) {
				Debug.logError(e, e.getMessage());
				return ServiceUtil
				.returnError("Error while finding SalesTotals" + e);
			}
			return result;
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

	    public static BigDecimal sum(List<BigDecimal> list) {
	    	BigDecimal sum = BigDecimal.ZERO; 
	        for (BigDecimal i:list)
	        	sum = sum.add(i);
	        return sum;
	    }
	    
	    public static Map<String, Object> getSalesSummaryPeriodTotals(DispatchContext ctx,Map<String, ? extends Object> context) {
			Delegator delegator = ctx.getDelegator();
			List<String> facilityIds = (List<String>) context.get("facilityIds");
			List<String> shipmentIds = (List<String>) context.get("shipmentIds");
			boolean isByParty = Boolean.FALSE;
			if(UtilValidate.isNotEmpty(context.get("isByParty"))){
				isByParty = (Boolean)context.get("isByParty");
			}
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
			
			Date startDate = new java.sql.Date(fromDate.getTime());
			Date endDate = new java.sql.Date(thruDate.getTime());
			
			String subscriptionType = (String) context.get("subscriptionType");
			Boolean onlyVendorAndPTCBooths = (Boolean) context.get("onlyVendorAndPTCBooths");
			String periodTypeId=(String)context.get("periodTypeId");
			List ownerPartyIds = FastList.newInstance();
			if(UtilValidate.isNotEmpty(facilityIds) && isByParty){
				try{
					List<GenericValue> ownerPartyList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds), UtilMisc.toSet("ownerPartyId"), null, null ,false);
					ownerPartyIds = EntityUtil.getFieldListFromEntityList(ownerPartyList, "ownerPartyId", true);
				}catch(GenericEntityException e){
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}
			}
			
			boolean includeReturnOrders = Boolean.FALSE;// always excluding if externally not set
			if (context.get("includeReturnOrders") != null) {
				includeReturnOrders = (Boolean) context.get("includeReturnOrders");
			}
//			List<GenericValue> orderItems = FastList.newInstance();
	    	EntityListIterator orderItemsIter = null;
			List<GenericValue> returnItemsList = FastList.newInstance();

			Map productAttributes = new TreeMap<String, Object>();
			List productSubscriptionTypeList = FastList.newInstance();
			Map<String, String> dayShipmentMap = FastMap.newInstance();
			List adjustmentOrderList = FastList.newInstance();
			try {
				List exprListForParameters = FastList.newInstance();
				exprListForParameters.add(EntityCondition.makeCondition("attrName",	EntityOperator.EQUALS, "FAT"));
				exprListForParameters.add(EntityCondition.makeCondition("attrName",EntityOperator.EQUALS, "SNF"));
				EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.OR);
				List<GenericValue> productAttribtutesList = delegator.findList("ProductAttribute", paramCond, null, null, null, false);
				Iterator<GenericValue> productAttrIter = productAttribtutesList.iterator();
				while (productAttrIter.hasNext()) {
					GenericValue productAttrItem = productAttrIter.next();
					if (!productAttributes.containsKey(productAttrItem.getString("productId"))) {
						productAttributes.put(productAttrItem.getString("productId"),new TreeMap<String, Object>());
					}
					Map value = (Map) productAttributes.get(productAttrItem.getString("productId"));
					value.put(productAttrItem.getString("attrName"),productAttrItem.getString("attrValue"));
				}

				productSubscriptionTypeList = delegator.findList("Enumeration",	EntityCondition.makeCondition("enumId", EntityOperator.IN, UtilMisc.toList("CASH","CREDIT","EMP_SUBSIDY","_NA_")), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"),null, false);

				// lets populate sales date shipmentId Map
				int intervalDays = (UtilDateTime.getIntervalInDays(fromDate,thruDate)) + 1;
				for (int i = 0; i < intervalDays; i++) {
					Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDate,i);
					// List dayShipments = getShipmentIds(delegator, saleDate, saleDate);
						dayShipmentMap.put(UtilDateTime.toDateString(saleDate, "yyyy-MM-dd"),UtilDateTime.toDateString(saleDate, "yyyy-MM-dd"));
				}
				//Debug.log("thruDate========" + thruDate + "shipmentIds=" +shipmentIds, module);
				List conditionList = FastList.newInstance();
				if (!UtilValidate.isEmpty(onlyVendorAndPTCBooths)) {
					if (onlyVendorAndPTCBooths.booleanValue()) {
						conditionList.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("categoryTypeEnum", "VENDOR"),EntityCondition.makeCondition("categoryTypeEnum","PTC")));
					}
				}
				/*if(isByParty){
					if (UtilValidate.isNotEmpty(ownerPartyIds)) {
						conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, ownerPartyIds));
					}
				}*/
				
				if (!UtilValidate.isEmpty(facilityIds)) {
					conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds));
				}
				
				
				if (!UtilValidate.isEmpty(periodTypeId)) {
					conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
				}else{
					conditionList.add(EntityCondition.makeCondition("periodTypeId",  EntityOperator.EQUALS, "SALES_DAY"));
				}
			
				conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","CREDIT","EMP_SUBSIDY","_NA_")));
				conditionList.add(EntityCondition.makeCondition("salesDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
				conditionList.add(EntityCondition.makeCondition("salesDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
				if (includeReturnOrders) {
				//conditionList.add(EntityCondition.makeCondition("isReturn", EntityOperator.EQUALS, 'Y'));
				}else{
					conditionList.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("isReturn", "N"),EntityCondition.makeCondition("isReturn","")));
				}
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				// Debug.logInfo("condition=" + condition, module);
				if (!UtilValidate.isEmpty(fromDate)) {
					orderItemsIter = delegator.find("LMSPeriodSalesSummaryDetail", condition,	null, null, null, null);
				}
				//Debug.log("condition====IN==orderItemsIter=" + orderItemsIter+"===conditionList=="+conditionList);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			BigDecimal totalQuantity = ZERO;
			BigDecimal totalRevenue = ZERO;
			BigDecimal totalPacket = ZERO;
			BigDecimal totalFat = ZERO;
			BigDecimal totalSnf = ZERO;
			BigDecimal totalVatRevenue = ZERO;

			Map<String, Object> boothZoneMap = FastMap.newInstance();
			// Debug.logInfo("boothZoneMap=" + boothZoneMap, module);
			Map<String, Object> boothTotals = new TreeMap<String, Object>();
			// Map<String, Object> zoneTotals = new TreeMap<String, Object>();
			//Map<String, Object> distributorTotals = new TreeMap<String, Object>();
			Map<String, Object> productTotals = new TreeMap<String, Object>();
			Map<String, Object> supplyTypeTotals = new TreeMap<String, Object>();
			Map<String, Object> dayWiseTotals = new TreeMap<String, Object>();
	        GenericValue orderItem;
	    	while( orderItemsIter != null && (orderItem = orderItemsIter.next()) != null) {
				String boothId = "";
				if(isByParty){
					boothId = orderItem.getString("facilityId");
				}
				else{
					boothId = orderItem.getString("facilityId");
				}
				//Debug.log("boothId====IN========>" + boothId);
				String productId = orderItem.getString("productId");    		
				String prodSubscriptionTypeId = orderItem.getString("productSubscriptionTypeId");
				/*BigDecimal quantity = orderItem.getBigDecimal("quantity");
				BigDecimal packetQuantity = orderItem.getBigDecimal("quantity");
				BigDecimal price = orderItem.getBigDecimal("unitListPrice");
				BigDecimal revenue = price.multiply(quantity);*/
				BigDecimal quantity = orderItem.getBigDecimal("totalQuantity");
				BigDecimal packetQuantity = orderItem.getBigDecimal("totalQuantity");
				BigDecimal price = BigDecimal.ONE;//orderItem.getBigDecimal("unitPrice");
				BigDecimal revenue = price.multiply(quantity);
				if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("totalRevenue"))){
					revenue = orderItem.getBigDecimal("totalRevenue");
				}
			   // Debug.log("===return=Revenue###"+revenue+"===and Qty=####"+quantity+"====productId=="+productId+"==isReturn=="+orderItem.getString("isReturn"));
				totalRevenue = totalRevenue.add(revenue);
				totalPacket = totalPacket.add(packetQuantity);
				totalQuantity = totalQuantity.add(quantity);
				
				BigDecimal vatAmount = ZERO;
				BigDecimal vatRevenue=ZERO;
				totalVatRevenue = totalVatRevenue.add(vatAmount);
				
				BigDecimal fat = ZERO;
				BigDecimal snf = ZERO;
				String productName = "";
				Map prodAttrMap = (Map) productAttributes.get(orderItem.getString("productId"));
				// Debug.logInfo("orderItem=" + orderItem, module);
				if (prodAttrMap != null) {
					double fatPercent = Double.parseDouble((String) prodAttrMap.get("FAT"));
					fat = quantity.multiply(BigDecimal.valueOf(fatPercent));
					fat = fat.multiply(BigDecimal.valueOf(1.03));
					fat = fat.divide(BigDecimal.valueOf(100));
					double snfPercent = Double.parseDouble((String) prodAttrMap.get("SNF"));
					snf = quantity.multiply(BigDecimal.valueOf(snfPercent));
					snf = snf.multiply(BigDecimal.valueOf(1.03));
					snf = snf.divide(BigDecimal.valueOf(100));
				}
				totalFat = totalFat.add(fat);
				totalSnf = totalSnf.add(snf);
				
				// Handle booth totals
				if (boothTotals.get(boothId) == null) {
					Map<String, Object> newMap = FastMap.newInstance();

					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("totalRevenue", revenue);
					newMap.put("vatRevenue", vatRevenue);
					newMap.put("excludeIncentive","");
					newMap.put("categoryTypeEnum","");
					Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
					Map<String, Object> iteratorMap = FastMap.newInstance();
					while (typeIter.hasNext()) {
						// initialize type maps
						GenericValue type = typeIter.next();
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", type.getString("enumId"));
						supplyTypeDetailsMap.put("total", ZERO);
						supplyTypeDetailsMap.put("packetQuantity", ZERO);
						supplyTypeDetailsMap.put("totalRevenue", ZERO);
						supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
						iteratorMap.put(type.getString("enumId"),supplyTypeDetailsMap);
						newMap.put("supplyTypeTotals", iteratorMap);
					}
					Map supplyTypeMap = (Map) newMap.get("supplyTypeTotals");
					Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					supplyTypeMap.put(prodSubscriptionTypeId, supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);

					Map<String, Object> productItemMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();

					productItemMap.put("name", productName);
					productSupplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
					productSupplyTypeDetailsMap.put("total", quantity);
					productSupplyTypeDetailsMap.put("packetQuantity",packetQuantity);
					productSupplyTypeDetailsMap.put("totalRevenue", revenue);
					productSupplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),productSupplyTypeDetailsMap);
					productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
					productItemMap.put("total", quantity);
					productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("totalRevenue", revenue);
					productItemMap.put("vatRevenue", vatRevenue);

					Map<String, Object> productMap = FastMap.newInstance();
					productMap.put(productId, productItemMap);
					newMap.put("productTotals", productMap);
					boothTotals.put(boothId, newMap);
				} 
				else {
					Map boothMap = (Map) boothTotals.get(boothId);
					BigDecimal runningTotal = (BigDecimal) boothMap.get("total");
					runningTotal = runningTotal.add(quantity);
					boothMap.put("total", runningTotal);

					BigDecimal runningPacketTotal = (BigDecimal) boothMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					boothMap.put("packetQuantity", runningPacketTotal);

					BigDecimal runningTotalRevenue = (BigDecimal) boothMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					boothMap.put("totalRevenue", runningTotalRevenue);

					BigDecimal runningVatRevenue = (BigDecimal) boothMap.get("vatRevenue");
					runningVatRevenue = runningVatRevenue.add(vatRevenue);
					boothMap.put("vatRevenue", runningVatRevenue);

					// next handle type totals
					Map tempMap = (Map) boothMap.get("supplyTypeTotals");
					Map typeMap = (Map) tempMap.get(prodSubscriptionTypeId);
					BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
					typeRunningTotal = typeRunningTotal.add(quantity);
					BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
					typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);

					BigDecimal typeRunningPacketTotal = (BigDecimal) typeMap.get("packetQuantity");
					typeRunningPacketTotal = typeRunningPacketTotal.add(packetQuantity);

					BigDecimal typeRunningVatRevenue = (BigDecimal) typeMap.get("vatRevenue");
					typeRunningVatRevenue = typeRunningVatRevenue.add(vatRevenue);

					typeMap.put("name", prodSubscriptionTypeId);
					typeMap.put("total", typeRunningTotal);
					typeMap.put("packetQuantity", typeRunningPacketTotal);
					typeMap.put("totalRevenue", typeRunningTotalRevenue);
					typeMap.put("vatRevenue", typeRunningVatRevenue);
					// next handle product totals
					Map boothProductTotals = (Map) boothMap.get("productTotals");
					Map productMap = (Map) boothProductTotals.get(productId);
					if (UtilValidate.isEmpty(productMap)) {
						Map<String, Object> productItemMap = FastMap.newInstance();
						Map<String, Object> supplyTypeMap = FastMap.newInstance();
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
						productItemMap.put("name", productName);
						productItemMap.put("supplyTypeTotals", supplyTypeMap);
						productItemMap.put("total", quantity);
						productItemMap.put("packetQuantity", packetQuantity);
						productItemMap.put("totalRevenue", revenue);
						productItemMap.put("vatRevenue", vatRevenue);
						boothProductTotals.put(productId, productItemMap);

					} else {
						BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
						productRunningTotal = productRunningTotal.add(quantity);
						productMap.put("total", productRunningTotal);
						BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
						productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
						productMap.put("totalRevenue", productRunningTotalRevenue);

						BigDecimal productRunningPacketTotals = (BigDecimal) productMap.get("packetQuantity");
						productRunningPacketTotals = productRunningPacketTotals.add(packetQuantity);
						productMap.put("packetQuantity", productRunningPacketTotals);

						BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
						productRunningVatRevenue = productRunningVatRevenue.add(productRunningVatRevenue);
						productMap.put("vatRevenue", productRunningVatRevenue);

						Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
						if (supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId")) != null) {
							Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
							BigDecimal runningTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("total");
							runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
							BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("totalRevenue");
							runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);
							BigDecimal runningPacketTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("packetQuantity");
							runningPacketTotalproductSubscriptionType = runningPacketTotalproductSubscriptionType.add(packetQuantity);
							BigDecimal runningVatRevenueProductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("vatRevenue");
							runningVatRevenueProductSubscriptionType = runningVatRevenueProductSubscriptionType.add(vatRevenue);

							supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
							supplyTypeDetailsMap.put("total",runningTotalproductSubscriptionType);
							supplyTypeDetailsMap.put("packetQuantity",runningPacketTotalproductSubscriptionType);
							supplyTypeDetailsMap.put("totalRevenue",runningTotalRevenueproductSubscriptionType);
							supplyTypeDetailsMap.put("vatRevenue",runningVatRevenueProductSubscriptionType);
							supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
							productMap.put("supplyTypeTotals", supplyTypeMap);
							boothProductTotals.put(productId, productMap);

						} else {
							Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
							supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
							supplyTypeDetailsMap.put("total", quantity);
							supplyTypeDetailsMap.put("packetQuantity",packetQuantity);			
							supplyTypeDetailsMap.put("totalRevenue", revenue);
							supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
							supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
							productMap.put("supplyTypeTotals", supplyTypeMap);
							boothProductTotals.put(productId, productMap);
						}
					}
				}
				// handle dayWise Totals if empty ignore this type of totals
				if (UtilValidate.isNotEmpty(orderItem.getDate("salesDate"))) {
					Timestamp salesDate=UtilDateTime.toTimestamp(orderItem.getDate("salesDate"));
					String currentSaleDate = UtilDateTime.toDateString(salesDate,"yyyy-MM-dd");
					if (dayWiseTotals.get(currentSaleDate) == null) {
						Map<String, Object> newMap = FastMap.newInstance();
						newMap.put("total", quantity);
						newMap.put("packetQuantity", packetQuantity);
						newMap.put("totalRevenue", revenue);
						newMap.put("vatRevenue", vatRevenue);
						newMap.put("excludeIncentive",orderItem.getString("excludeIncentive"));
						newMap.put("categoryTypeEnum",orderItem.getString("categoryTypeEnum"));
						Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
						Map<String, Object> iteratorMap = FastMap.newInstance();
						while (typeIter.hasNext()) {
							// initialize type maps
							GenericValue type = typeIter.next();
							Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
							supplyTypeDetailsMap.put("name",type.getString("enumId"));
							supplyTypeDetailsMap.put("total", ZERO);
							supplyTypeDetailsMap.put("packetQuantity", ZERO);
							supplyTypeDetailsMap.put("totalRevenue", ZERO);
							supplyTypeDetailsMap.put("vatRevenue", ZERO);
							iteratorMap.put(type.getString("enumId"),supplyTypeDetailsMap);
							newMap.put("supplyTypeTotals", iteratorMap);
						}
						Map supplyTypeMap = (Map) newMap.get("supplyTypeTotals");
						Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(prodSubscriptionTypeId);
						supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
						supplyTypeMap.put(prodSubscriptionTypeId,supplyTypeDetailsMap);
						newMap.put("supplyTypeTotals", supplyTypeMap);

						Map<String, Object> productItemMap = FastMap.newInstance();
						Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
						Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();
						productItemMap.put("name", productName);
						productSupplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
						productSupplyTypeDetailsMap.put("total", quantity);
						productSupplyTypeDetailsMap.put("packetQuantity",packetQuantity);
						productSupplyTypeDetailsMap.put("totalRevenue", revenue);
						productSupplyTypeDetailsMap.put("vatRevenue", vatRevenue);
						productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),productSupplyTypeDetailsMap);
						productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
						productItemMap.put("total", quantity);
						productItemMap.put("packetQuantity", packetQuantity);
						productItemMap.put("totalRevenue", revenue);
						productItemMap.put("vatRevenue", vatRevenue);

						Map<String, Object> productMap = FastMap.newInstance();
						productMap.put(productId, productItemMap);
						newMap.put("productTotals", productMap);
						dayWiseTotals.put(currentSaleDate, newMap);
					} 
					else {
						Map dayWiseMap = (Map) dayWiseTotals.get(currentSaleDate);
						BigDecimal runningTotal = (BigDecimal) dayWiseMap.get("total");
						runningTotal = runningTotal.add(quantity);
						dayWiseMap.put("total", runningTotal);
						BigDecimal runningTotalRevenue = (BigDecimal) dayWiseMap.get("totalRevenue");
						runningTotalRevenue = runningTotalRevenue.add(revenue);
						dayWiseMap.put("totalRevenue", runningTotalRevenue);
						BigDecimal runningPacketTotal = (BigDecimal) dayWiseMap.get("packetQuantity");
						runningPacketTotal = runningPacketTotal.add(packetQuantity);
						dayWiseMap.put("packetQuantity", runningPacketTotal);
						BigDecimal runningVatRevenue = (BigDecimal) dayWiseMap.get("vatRevenue");
						runningVatRevenue = runningVatRevenue.add(vatRevenue);
						dayWiseMap.put("vatRevenue", runningVatRevenue);
						// next handle type totals
						Map tempMap = (Map) dayWiseMap.get("supplyTypeTotals");
						Map typeMap = (Map) tempMap.get(prodSubscriptionTypeId);
						BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
						typeRunningTotal = typeRunningTotal.add(quantity);
						BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
						typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);
						BigDecimal typeRunningPacketTotal = (BigDecimal) typeMap.get("packetQuantity");
						typeRunningPacketTotal = typeRunningPacketTotal.add(packetQuantity);
						BigDecimal typeRunningVatRevenue = (BigDecimal) typeMap.get("vatRevenue");
						typeRunningVatRevenue = typeRunningVatRevenue.add(vatRevenue);

						typeMap.put("name", prodSubscriptionTypeId);
						typeMap.put("total", typeRunningTotal);
						typeMap.put("totalRevenue", typeRunningTotalRevenue);
						typeMap.put("packetQuantity", typeRunningPacketTotal);
						typeMap.put("vatRevenue", typeRunningVatRevenue);

						// next handle product totals
						Map dayWiseProductTotals = (Map) dayWiseMap.get("productTotals");
						Map productMap = (Map) dayWiseProductTotals.get(productId);

						if (UtilValidate.isEmpty(productMap)) {
							Map<String, Object> productItemMap = FastMap.newInstance();
							Map<String, Object> supplyTypeMap = FastMap.newInstance();
							Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
							supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
							supplyTypeDetailsMap.put("total", quantity);
							supplyTypeDetailsMap.put("packetQuantity",packetQuantity);
							supplyTypeDetailsMap.put("totalRevenue", revenue);
							supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
							supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
							productItemMap.put("name", productName);
							productItemMap.put("supplyTypeTotals", supplyTypeMap);
							productItemMap.put("total", quantity);
							productItemMap.put("packetQuantity", packetQuantity);
							productItemMap.put("totalRevenue", revenue);
							productItemMap.put("vatRevenue", vatRevenue);
							dayWiseProductTotals.put(productId, productItemMap);

						} else {
							BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
							productRunningTotal = productRunningTotal.add(quantity);
							productMap.put("total", productRunningTotal);
							BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
							productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
							productMap.put("totalRevenue",productRunningTotalRevenue);
							BigDecimal productRunningPacketTotal = (BigDecimal) productMap.get("packetQuantity");
							productRunningPacketTotal = productRunningPacketTotal.add(packetQuantity);
							productMap.put("packetQuantity",productRunningPacketTotal);
							BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
							productRunningVatRevenue = productRunningVatRevenue.add(vatRevenue);
							productMap.put("vatRevenue", productRunningVatRevenue);

							Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
							if (supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId")) != null) {
								Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
								BigDecimal runningTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("total");
								runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);

								BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("totalRevenue");
								runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);

								BigDecimal runningTotalPacketSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("packetQuantity");
								runningTotalPacketSubscriptionType = runningTotalPacketSubscriptionType.add(packetQuantity);
								BigDecimal runningVatRevenueProductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("vatRevenue");
								runningVatRevenueProductSubscriptionType = runningVatRevenueProductSubscriptionType.add(vatRevenue);

								supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
								supplyTypeDetailsMap.put("total",runningTotalproductSubscriptionType);
								supplyTypeDetailsMap.put("packetQuantity",runningTotalPacketSubscriptionType);
								supplyTypeDetailsMap.put("totalRevenue",runningTotalRevenueproductSubscriptionType);
								supplyTypeDetailsMap.put("vatRevenue",runningVatRevenueProductSubscriptionType);
								supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
								productMap.put("supplyTypeTotals", supplyTypeMap);
								dayWiseProductTotals.put(productId, productMap);

							} else {
								Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
								supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
								supplyTypeDetailsMap.put("total", quantity);
								supplyTypeDetailsMap.put("packetQuantity",packetQuantity);
								supplyTypeDetailsMap.put("totalRevenue", revenue);
								supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
								supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
								productMap.put("supplyTypeTotals", supplyTypeMap);
								dayWiseProductTotals.put(productId, productMap);
							}
						}
					}
				}
				// Handle product totals
				if (productTotals.get(productId) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					newMap.put("name", productName);
					Map<String, Object> supplyTypeMap = FastMap.newInstance();
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);
					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("totalRevenue", revenue);
					newMap.put("vatRevenue", vatRevenue);
					newMap.put("totalFat", fat);
					newMap.put("totalSnf", snf);
					productTotals.put(productId, newMap);
				} 
				else {
					Map productMap = (Map) productTotals.get(productId);
					BigDecimal runningTotal = (BigDecimal) productMap.get("total");
					runningTotal = runningTotal.add(quantity);
					productMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					productMap.put("totalRevenue", runningTotalRevenue);
					BigDecimal runningPacketTotal = (BigDecimal) productMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					productMap.put("packetQuantity", runningPacketTotal);
					BigDecimal runningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
					runningVatRevenue = runningVatRevenue.add(vatRevenue);
					productMap.put("vatRevenue", runningVatRevenue);

					BigDecimal runningTotalFat = (BigDecimal) productMap.get("totalFat");
					runningTotalFat = runningTotalFat.add(fat);
					productMap.put("totalFat", runningTotalFat);
					BigDecimal runningTotalSnf = (BigDecimal) productMap.get("totalSnf");
					runningTotalSnf = runningTotalSnf.add(snf);
					productMap.put("totalSnf", runningTotalSnf);
					Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
					if (supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId")) != null) {
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap = (Map<String, Object>) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
						BigDecimal runningTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("total");
						BigDecimal runningPacketTotalSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("packetQuantity");
						BigDecimal runningRevenueproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("totalRevenue");
						BigDecimal runningVatRevenueProductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("vatRevenue");
						runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
						runningRevenueproductSubscriptionType = runningRevenueproductSubscriptionType.add(revenue);
						runningPacketTotalSubscriptionType = runningPacketTotalSubscriptionType.add(packetQuantity);
						runningVatRevenueProductSubscriptionType = runningVatRevenueProductSubscriptionType.add(vatRevenue);
						supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total",runningTotalproductSubscriptionType);
						supplyTypeDetailsMap.put("packetQuantity",runningPacketTotalSubscriptionType);
						supplyTypeDetailsMap.put("totalRevenue",runningRevenueproductSubscriptionType);
						supplyTypeDetailsMap.put("vatRevenue",runningVatRevenueProductSubscriptionType);

						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
						productMap.put("supplyTypeTotals", supplyTypeMap);
					} else {
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
						productMap.put("supplyTypeTotals", supplyTypeMap);
					}

				}
				// Handle supply type totals
				if (supplyTypeTotals.get(prodSubscriptionTypeId) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					newMap.put("name", prodSubscriptionTypeId);
					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("totalRevenue", revenue);
					newMap.put("vatRevenue", vatRevenue);
					supplyTypeTotals.put(prodSubscriptionTypeId, newMap);
				} 
				else {
					Map supplyTypeMap = (Map) supplyTypeTotals.get(prodSubscriptionTypeId);
					BigDecimal runningTotal = (BigDecimal) supplyTypeMap.get("total");
					runningTotal = runningTotal.add(quantity);
					supplyTypeMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal) supplyTypeMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					supplyTypeMap.put("totalRevenue", runningTotalRevenue);
					BigDecimal runningPacketTotal = (BigDecimal) supplyTypeMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					supplyTypeMap.put("packetQuantity", runningPacketTotal);
					BigDecimal runningVatRevenue = (BigDecimal) supplyTypeMap.get("vatRevenue");
					runningVatRevenue = runningVatRevenue.add(vatRevenue);
					supplyTypeMap.put("vatRevenue", runningVatRevenue);
					supplyTypeTotals.put(prodSubscriptionTypeId, supplyTypeMap);
				}
				// Debug.log("===INENDDDDDDD==boothId=="+boothId+"===productId=="+productId+"====OrderId=="+orderItem.getString("orderId")+"==qty=="+orderItem.getString("quantity"));
			}
	    	
	        if (orderItemsIter != null) {
	            try {
	            	orderItemsIter.close();
	            } catch (GenericEntityException e) {
	                Debug.logWarning(e, module);
	            }
	        }
	        
			totalQuantity = totalQuantity.setScale(decimals, rounding);
			totalRevenue = totalRevenue.setScale(decimals, rounding);
			totalPacket = totalPacket.setScale(decimals, rounding);
			totalFat = totalFat.setScale(decimals, rounding);
			totalSnf = totalSnf.setScale(decimals, rounding);
			totalVatRevenue = totalVatRevenue.setScale(decimals, rounding);

			for (Map.Entry<String, Object> entry : productTotals.entrySet()) {
				Map<String, Object> productValue = (Map<String, Object>) entry.getValue();
				BigDecimal tempVal = (BigDecimal) productValue.get("total");
				tempVal = tempVal.setScale(decimals, rounding);
				productValue.put("total", tempVal);

				tempVal = (BigDecimal) productValue.get("packetQuantity");
				tempVal = tempVal.setScale(decimals, rounding);
				productValue.put("packetQuantity", tempVal);

				tempVal = (BigDecimal) productValue.get("totalRevenue");
				tempVal = tempVal.setScale(decimals, rounding);
				productValue.put("totalRevenue", tempVal);
				tempVal = (BigDecimal) productValue.get("totalFat");
				tempVal = tempVal.setScale(decimals, rounding);
				productValue.put("totalFat", tempVal);
				tempVal = (BigDecimal) productValue.get("totalSnf");
				tempVal = tempVal.setScale(decimals, rounding);
				productValue.put("totalSnf", tempVal);
				tempVal = (BigDecimal) productValue.get("vatRevenue");
				tempVal = tempVal.setScale(decimals, rounding);
				productValue.put("vatRevenue", tempVal);
			}

			for (Map.Entry<String, Object> entry : supplyTypeTotals.entrySet()) {
				Map<String, Object> supplyTypeValue = (Map<String, Object>) entry.getValue();
				BigDecimal tempVal = (BigDecimal) supplyTypeValue.get("total");
				tempVal = tempVal.setScale(decimals, rounding);
				supplyTypeValue.put("total", tempVal);

				tempVal = (BigDecimal) supplyTypeValue.get("packetQuantity");
				tempVal = tempVal.setScale(decimals, rounding);
				supplyTypeValue.put("packetQuantity", tempVal);

				tempVal = (BigDecimal) supplyTypeValue.get("totalRevenue");
				tempVal = tempVal.setScale(decimals, rounding);
				supplyTypeValue.put("totalRevenue", tempVal);
				tempVal = (BigDecimal) supplyTypeValue.get("vatRevenue");
				tempVal = tempVal.setScale(decimals, rounding);
				supplyTypeValue.put("vatRevenue", tempVal);

			}
			Map<String, Object> result = FastMap.newInstance();
			result.put("totalQuantity", totalQuantity);
			result.put("totalRevenue", totalRevenue);
			result.put("totalVatRevenue", totalVatRevenue);
			result.put("totalPacket", totalPacket);
			result.put("totalFat", totalFat);
			result.put("totalSnf", totalSnf);
			result.put("boothTotals", boothTotals);
			result.put("dayWiseTotals", dayWiseTotals);
			result.put("productTotals", productTotals);
			result.put("supplyTypeTotals", supplyTypeTotals);
			return result;
		}

	    
}

package in.vasista.vbiz.tankersales;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.math.RoundingMode;

import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.accounting.util.UtilAccounting;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import org.ofbiz.accounting.period.PeriodServices;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import org.ofbiz.network.LmsServices;

import in.vasista.vbiz.byproducts.SalesHistoryServices;

import java.util.Map.Entry;

public class TransporterSalesHelperServices{
	
	public static final String module = TransporterSalesHelperServices.class.getName();
	public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
	public static final BigDecimal ONE_BASE = BigDecimal.ONE;
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
	public static int purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
	public static int purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
	
	public static int purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
	
	public static Map<String, Object> createCustTimePeriodMM(DispatchContext dctx,Map<String, ? extends Object> context) {
     
	Map<String, Object> resultMap = FastMap.newInstance();
	LocalDispatcher dispatcher = dctx.getDispatcher();
//    Delegator delegator = dctx.getDelegator();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
   
    Date fromDate = (Date) context.get("fromDate");
    Date thruDate = (Date) context.get("thruDate");

	String organizationPartyId = (String)context.get("organizationPartyId");
	String periodTypeId = (String)context.get("periodTypeId");
	String isClosed = (String)context.get("isClosed");
	Long periodNum = (Long)context.get("periodNum");
	String periodName = (String)context.get("periodName");
	Map<String,Object> inMap = FastMap.newInstance();
	inMap.put("fromDate",fromDate);
	inMap.put("thruDate",thruDate);
	inMap.put("organizationPartyId",organizationPartyId);
	inMap.put("periodTypeId",periodTypeId);
	inMap.put("isClosed",isClosed);
	inMap.put("periodNum",periodNum);
	inMap.put("periodName",periodName);
	inMap.put("userLogin",userLogin);
    
	Map<String, Object> result = ServiceUtil.returnSuccess();
	Map<String,Object> customTime = FastMap.newInstance();

	try{
		customTime = dispatcher.runSync("createCustomTimePeriod",inMap);
		if(ServiceUtil.isError(customTime)){
		Debug.logError("Error while creating customTimePeriod ::"+ServiceUtil.getErrorMessage(customTime),module);
		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(customTime));
		}
	
		if((isClosed).equals("Y")){
		Map resultCtx = populateInventoryPeriodSummary(dctx, UtilMisc.toMap("customTimePeriodId", customTime.get("customTimePeriodId"), "userLogin", userLogin));
	      	if(ServiceUtil.isError(resultCtx)){
			String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
			return ServiceUtil.returnError(errMsg);
      		}
		}
	}catch(GenericServiceException e){
		Debug.logError(e,module);
			return ServiceUtil.returnError("Error While creating customTimePeriod");
		}	
		result = ServiceUtil.returnSuccess("Successfully created for timeperiod:" +customTime.get("customTimePeriodId"));
		result.put("customTimePeriodId", (String)customTime.get("customTimePeriodId"));
		return result;
	}
	public static Map<String, Object> populateInventoryPeriodSummary(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if(UtilValidate.isEmpty(customTimePeriod)){
				Debug.logError("No Period defined with id: "+customTimePeriod, module);
				return ServiceUtil.returnError("No Period defined with id: "+customTimePeriod);
			}
			if(!((customTimePeriod.getString("periodTypeId")).equals("INVTRY_PERIOD_CLOSE"))){
				Debug.logError("Period is not of type inventory period closure", module);
				return ServiceUtil.returnError("Period is not of type inventory period closure :"+customTimePeriod);
			}
			if(!((customTimePeriod.getString("isClosed")).equals("Y"))){
				Debug.logError("Period is not closed ", module);
				return ServiceUtil.returnError("Period is not closed ");
				}
			
			Map lastClosedCtx = FastMap.newInstance();
            lastClosedCtx.put("organizationPartyId", "Company");
            lastClosedCtx.put("periodTypeId", customTimePeriod.getString("periodTypeId"));
            lastClosedCtx.put("findDate", customTimePeriod.getDate("fromDate"));
            lastClosedCtx.put("onlyFiscalPeriods", Boolean.FALSE);
            
            Map lastClosedPeriodResult = PeriodServices.findLastClosedDate(ctx, lastClosedCtx);
            GenericValue lastClosedPeriod = (GenericValue)lastClosedPeriodResult.get("lastClosedTimePeriod");
            String lastClosedPeriodId = "";
            if(UtilValidate.isNotEmpty(lastClosedPeriod)){
            	lastClosedPeriodId = lastClosedPeriod.getString("customTimePeriodId");
            	if(UtilDateTime.getIntervalInDays(UtilDateTime.toTimestamp(lastClosedPeriod.getDate("thruDate")), UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"))) != 1){
            		Debug.logError("Previous inventory closure period not closed", module);
    				return ServiceUtil.returnError("Previous inventory closure period not closed");
        		}
            }
            
            List<GenericValue> inventoryPeriodSummary = delegator.findList("InventoryPeriodSummary", EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId), null, null, null, false);
           
            if(UtilValidate.isNotEmpty(inventoryPeriodSummary)){
            	delegator.removeAll(inventoryPeriodSummary);
            }
            
            Timestamp transFromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
            Timestamp transThruDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
            Map resultCtx = getStoreIssuesAndReceiptsForPeriod(ctx, UtilMisc.toMap("userLogin", userLogin, "fromDate", transFromDate, "thruDate", transThruDate, "previousTimePeriodId", lastClosedPeriodId));
            
            if(ServiceUtil.isError(resultCtx)){
            	String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
  		  		Debug.logError(errMsg , module);
  		  		return ServiceUtil.returnError(errMsg);
            }
            
            Map receiptIssueMap = (Map)resultCtx.get("receiptIssueMap");
            
            Iterator invIter = receiptIssueMap.entrySet().iterator();
			while (invIter.hasNext()) {
				Map.Entry tempEntry = (Entry) invIter.next();
				Map eachStore = (Map) tempEntry.getValue();
				String storeId = (String) tempEntry.getKey();
				Iterator eachStoreIter = eachStore.entrySet().iterator();
				while (eachStoreIter.hasNext()) {
					Map.Entry tempItemEntry = (Entry) eachStoreIter.next();
					Map eachStoreItem = (Map) tempItemEntry.getValue();
					String productId = (String) tempItemEntry.getKey();
					
					GenericValue newEntity = delegator.makeValue("InventoryPeriodSummary");
					newEntity.set("facilityId", storeId);
					newEntity.set("customTimePeriodId", customTimePeriodId);
					newEntity.set("productId", productId);
					newEntity.set("issuedQty", (BigDecimal)eachStoreItem.get("issueQty"));
					newEntity.set("receivedQty", (BigDecimal)eachStoreItem.get("receiptQty"));
					newEntity.set("closingBalanceQty", (BigDecimal)eachStoreItem.get("closingBalanceQty"));
					newEntity.set("closingCost", (BigDecimal)eachStoreItem.get("closingCost"));
					newEntity.create();
				}
			}
            
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
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
		List conditionList= FastList.newInstance(); 
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
			Map<String, Object> updateRecvoryRes=updateFineRecvoryWithBilling(dctx , UtilMisc.toMap("customTimePeriodId", customTimePeriodId,"periodBillingId",periodBillingId,"userLogin",userLogin));
			if (ServiceUtil.isError(updateRecvoryRes)) {
	    		generationFailed = true;
                Debug.logWarning("There was an error while populating updateRecvory: " + ServiceUtil.getErrorMessage(updateRecvoryRes), module);
        		return ServiceUtil.returnError("There was an error while populating updateRecvory: " + ServiceUtil.getErrorMessage(updateRecvoryRes));          	            
            } 
			Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
			
			Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
			Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
			
			int totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);	
			Map routes = ByProductNetworkServices.getRoutes(dctx , UtilMisc.toMap("facilityTypeId", "ROUTE"));		
			List routesList = (List) routes.get("routesList");
			Map transporterMap =FastMap.newInstance();
			Map<String, Object> populateTripRes=populateVehicleTripParty(dctx , UtilMisc.toMap("fromDate", monthBegin,"thruDate",monthEnd,"userLogin",userLogin));
			if (ServiceUtil.isError(populateTripRes)) {
	    		generationFailed = true;
                Debug.logWarning("There was an error while populating vehicleTripUpdate: " + ServiceUtil.getErrorMessage(populateTripRes), module);
        		return ServiceUtil.returnError("There was an error while populating vehicleTripUpdate: " + ServiceUtil.getErrorMessage(populateTripRes));          	            
            } 
			Debug.log("=====populateTripRes====AfterServiceRun==="+populateTripRes);
			try {
				for (int i = 0; i < routesList.size(); i++) {
					String route = (String) routesList.get(i);
					List boothsList=ByProductNetworkServices.getRouteBooths(delegator ,route);//getting list of Booths
					
					GenericValue facilityDetail = delegator.findOne("Facility",UtilMisc.toMap("facilityId", route) ,false);
					String isUpCountry = facilityDetail.getString("isUpcountry");
					BigDecimal facilitySize=new BigDecimal(1);
					
					/*if(UtilValidate.isNotEmpty(facilityDetail.getString("facilitySize"))){
						facilitySize=(BigDecimal)facilityDetail.getBigDecimal("facilitySize");
					}*/
					if(UtilValidate.isEmpty(isUpCountry)){
						isUpCountry="N";
					}
					BigDecimal narmalMargin = BigDecimal.ZERO;
					 BigDecimal totalPeriodComission = BigDecimal.ZERO;
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
					//input map for FacilitySize
					 Map inputDistMap = UtilMisc.toMap("userLogin", userLogin);
					 inputDistMap.put("rateCurrencyUomId", "LEN_km");
					 inputDistMap.put("facilityId", facilityDetail.get("facilityId"));
					 inputDistMap.put("rateTypeId", "FACILITY_SIZE");
						
					Timestamp supplyDate = monthBegin;
			        for (int k = 0; k <= (totalDays); k++) {
						Map routeMarginMap = FastMap.newInstance();
						routeMarginMap.put("partyId","");
						routeMarginMap.put("quantity",BigDecimal.ZERO );
						routeMarginMap.put("saleAmount", BigDecimal.ZERO);
						routeMarginMap.put("cashQty",BigDecimal.ZERO );
						routeMarginMap.put("cashAmount", BigDecimal.ZERO);
						routeMarginMap.put("commision", BigDecimal.ZERO);
						
						supplyDate = UtilDateTime.addDaysToTimestamp(monthBegin, k);
						
						//List shipmentIds =ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(supplyDate, "yyyy-MM-dd HH:mm:ss"),null,route);	//route Specific shipments.
						List shipmentIds = ByProductNetworkServices.getByProdShipmentIds(delegator, supplyDate, supplyDate,UtilMisc.toList(route));//this will give Today AM+PM as SALE
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
						conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, route));
						EntityCondition vhCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						List<GenericValue> vehicleTrpList = delegator.findList("VehicleTrip", vhCondition, null, UtilMisc.toList("originFacilityId"), null, false);
						 GenericValue vehicleTrip=EntityUtil.getFirst(vehicleTrpList);
						//List shipmentIds =ByProductNetworkServices.getShipmentIds(delegator,supplyDate,supplyDate);
						 String curntDay=UtilDateTime.toDateString(supplyDate ,"yyyy-MM-dd");
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
						 if(UtilValidate.isNotEmpty(vehicleTrpList)){
							 tripSize=new BigDecimal(vehicleTrpList.size());
							 routeMarginMap.put("partyId",vehicleTrip.getString("partyId"));
						 }
						 if(UtilValidate.isEmpty(shipmentIds)){//filled with zero
							 tripSize=new BigDecimal(shipmentIds.size());
							}
						 BigDecimal actualCommision = tripSize.multiply(narmalMargin);
                         if(uomId.equals("LEN_km")){
                        	 inputDistMap.put("fromDate",supplyDate );
                        	 Map<String, Object> facilitySizeResult = dispatcher.runSync("getRouteDistance", inputDistMap);
					    	    if (ServiceUtil.isError(facilitySizeResult)) {
					    			generationFailed = true;
					    			Debug.logWarning("There was an error while getting FacilitySize !: " + ServiceUtil.getErrorMessage(facilitySizeResult), module);
					        		return ServiceUtil.returnError("There was an error while getting FacilitySize !: " + ServiceUtil.getErrorMessage(facilitySizeResult));          	            
					            }
					    	    if(UtilValidate.isNotEmpty(facilitySizeResult)){
					    	    	 BigDecimal tempFacilitySize=(BigDecimal) facilitySizeResult.get("facilitySize");
									   if (tempFacilitySize.compareTo(BigDecimal.ZERO) > 0){
							   			facilitySize = (BigDecimal) facilitySizeResult.get("facilitySize");
							   		}	
					    		}	
                      	   actualCommision = actualCommision.multiply(facilitySize);
			    		}
                         /*if(uomId.equals("PER_LTR")){
                        	   actualCommision = actualCommision.multiply(facilitySize);
  			    		}*/
						Timestamp lastDay = UtilDateTime.getDayEnd(supplyDate, timeZone, locale);
						routeMarginMap.put("dueAmount", BigDecimal.ZERO);
						routeMarginMap.put("commision",actualCommision);
						totalPeriodComission=totalPeriodComission.add(actualCommision);
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
			       
					 if(totalPeriodComission.compareTo(BigDecimal.ZERO) !=0){//for whole period if commission is zero dont add
							transporterMap.put(route, dayTotalsMap);
					   }
				}
				masterList.add(transporterMap);
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
        		List<GenericValue> facilityRecoveryList = FastList.newInstance();
        		facilityRecoveryList = delegator.findList("FineRecovery", condition, null,null, null, false);	 

            	if(!UtilValidate.isEmpty(facilityRecoveryList)){
            		for(GenericValue facilityRecovery : facilityRecoveryList){
            			facilityRecovery.set("periodBillingId",periodBillingId);
            			facilityRecovery.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    			facilityRecovery.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            			facilityRecovery.store();
            		}
            	}
        	}catch(GenericEntityException e){
        		Debug.logError(e, module);
        	}
		}			
    	return result;
	}
    public static Map<String, Object>  cancelTranporterRecovery(DispatchContext dctx, Map<String, ? extends Object> context)  {
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
    	String customTimePeriodId = (String) context.get("customTimePeriodId");
    	String recoveryTypeId = (String)context.get("recoveryTypeId");
    	String recoveryId = (String)context.get("recoveryId");
    	GenericValue customTimePeriod = null;
		List conditionList = FastList.newInstance();
		List periodBillingList = FastList.newInstance();


    	try {
    		try {
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS","APPROVED","APPROVED_PAYMENT")));
		        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
		    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		    	periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);

			} catch (GenericEntityException e1) {
				Debug.logError(e1,"Error While Finding PeriodBilling");
				return ServiceUtil.returnError("Error While Finding PeriodBilling" + e1);
			}
			if(UtilValidate.isNotEmpty(periodBillingList)){
				 return ServiceUtil.returnError("Failed to cancel Recovery. Billing already generated! ");
			}
			GenericValue facilityRecovery = delegator.findOne("FineRecovery", UtilMisc.toMap("recoveryId",recoveryId), false);
			if(UtilValidate.isNotEmpty(facilityRecovery)){
				delegator.removeValue(facilityRecovery);    
            }
				
    	}catch (GenericEntityException e) {
    		 Debug.logError(e, module);
             return ServiceUtil.returnError("Failed to find FineRecovery " + e);
		} 
		result = ServiceUtil.returnSuccess("Recovery Successfully Cancelled..");
		return result;
    }
    public static Map<String, Object> populateVehicleTripParty(DispatchContext ctx, Map<String, ? extends Object> context ) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        List<String> facilityIds = (List<String>) context.get("facilityIds");
        List<String> shipmentIds = FastList.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	TimeZone timeZone = TimeZone.getDefault();
    	Locale locale = Locale.getDefault();
        Map<String, Object> vehicleTripResult = new HashMap<String, Object>();
        Map<String, Object> vehicleTripStatusResult = new HashMap<String, Object>();
        Map<String, Object> vehicleTripEntity = FastMap.newInstance(); 
        Map<String, Object> vehicleTripStatusEntity = FastMap.newInstance();
        Map<String, Object> result = FastMap.newInstance(); 
        List conditionList= FastList.newInstance(); 
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
        String  seqId="";
        Debug.log("=====FromDate=="+fromDate+"=====thruDate===="+thruDate);
        boolean setVehicleStatusFinal = Boolean.FALSE;
		try{  //getting configuration for vehcileStsus Set
			 GenericValue tenantConfigVehicleStatusSet = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","setVehicleStatusFinal"), true);
			 if (UtilValidate.isNotEmpty(tenantConfigVehicleStatusSet) && (tenantConfigVehicleStatusSet.getString("propertyValue")).equals("Y")) {
				 setVehicleStatusFinal = Boolean.TRUE;
			 	} 
    	Timestamp monthBegin = UtilDateTime.getDayStart(fromDate, timeZone, locale);
		Timestamp monthEnd = UtilDateTime.getDayEnd(thruDate, timeZone, locale);
		
		int totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
		Timestamp supplyDate = monthBegin;
        for (int k = 0; k <= (totalDays); k++) {
			supplyDate = UtilDateTime.addDaysToTimestamp(monthBegin, k);
			Map facilityParty=(Map)ByProductNetworkServices.getFacilityPartyContractor(ctx, UtilMisc.toMap("saleDate",supplyDate)).get("facilityPartyMap");
			 //we have to change this shipmentIds helper
		     shipmentIds = ByProductNetworkServices.getByProdShipmentIds(delegator, supplyDate, supplyDate);//this will give Today AM+PM as SALE
			//shipmentIds =ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(supplyDate, "yyyy-MM-dd HH:mm:ss"),null,null);	//get Day Shipments
			 for (int i = 0; i <(shipmentIds.size()); i++) {
				  String shipmentId=shipmentIds.get(i);
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
					EntityCondition vhCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> vehicleTrpList = delegator.findList("VehicleTrip", vhCondition, null, UtilMisc.toList("originFacilityId"), null, false);
					if(UtilValidate.isEmpty(vehicleTrpList)){//if empty create vehicleTrip this time.
						GenericValue shipment=delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
						 String routeId = shipment.getString("routeId");
			           	if (shipment == null) {
			           		Debug.logError("Shipment does not exist " + shipmentId, module);
			           		return ServiceUtil.returnError("Shipment does not exist " + shipmentId);                    	
			           	}
						Map vehicleCtx = UtilMisc.toMap("facilityId",routeId);
				        vehicleCtx.put("supplyDate", supplyDate);
				        Map vehicleRoleResult =  (Map) ByProductNetworkServices.getVehicleRole(ctx,vehicleCtx);
				        if(UtilValidate.isNotEmpty(vehicleRoleResult.get("vehicleRole"))){
				        	 GenericValue vehicleRole= (GenericValue) vehicleRoleResult.get("vehicleRole");
							String  vehicleId=vehicleRole.getString("vehicleId");
					        if(UtilValidate.isNotEmpty(vehicleId)){
					        	vehicleTripEntity.put("vehicleId", vehicleId);
					        	vehicleTripStatusEntity.put("vehicleId", vehicleId);
					        }
					        vehicleTripEntity.put("originFacilityId", routeId);
					        vehicleTripEntity.put("partyId", facilityParty.get(routeId));
					        vehicleTripEntity.put("userLogin", userLogin);
					        vehicleTripEntity.put("shipmentId", shipmentId);
					        vehicleTripEntity.put("createdDate", nowTimeStamp);
					        vehicleTripEntity.put("createdByUserLogin", userLogin.get("userLoginId"));
					        vehicleTripEntity.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
					        try {
					        	vehicleTripResult=dispatcher.runSync("createVehicleTrip", vehicleTripEntity);
					            seqId =(String) vehicleTripResult.get("sequenceNum");
					            if (ServiceUtil.isError(vehicleTripResult)){
					  		  		String errMsg =  ServiceUtil.getErrorMessage(vehicleTripResult);
					  		  		Debug.logError(errMsg , module);
					  		  	    return ServiceUtil.returnError("createVehicleTrip service" + vehicleId);    
					  		  	}
					          
					        } catch (GenericServiceException e) {
					            Debug.logError(e, "Error calling createVehicleTrip service", module);
					            return ServiceUtil.returnError(e.getMessage());
					        } 
					        vehicleTripStatusEntity.put("facilityId",routeId);
					        vehicleTripStatusEntity.put("sequenceNum", seqId);
					        vehicleTripStatusEntity.put("userLogin", userLogin);
					        vehicleTripStatusEntity.put("statusId", "VEHICLE_OUT");
					        if(setVehicleStatusFinal){//if configuration set StatusFinal Y then stsus is Returned
					        	 vehicleTripStatusEntity.put("statusId", "VEHICLE_RETURNED");
					        }
					        vehicleTripStatusEntity.put("createdDate", nowTimeStamp);
					        vehicleTripStatusEntity.put("createdByUserLogin", userLogin.get("userLoginId"));
					        vehicleTripStatusEntity.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
					        try {
					        	vehicleTripStatusResult= dispatcher.runSync("createVehicleTripStatus", vehicleTripStatusEntity);
					            if (ServiceUtil.isError(vehicleTripStatusResult)) {
					  		  		String errMsg =  ServiceUtil.getErrorMessage(vehicleTripStatusResult);
					  		  		Debug.logError(errMsg , module);
					  		  	    return ServiceUtil.returnError("createVehicleTripStatus service" + vehicleId);   
					  		  	}
					        }catch (GenericServiceException e) {
					            Debug.logError(e, "Error calling createVehicleTripStatus service", module);
					            return ServiceUtil.returnError(e.getMessage());
					        }
					      }
					}else{
						 GenericValue updateVehicleTrip=EntityUtil.getFirst(vehicleTrpList);
						 String routeId=updateVehicleTrip.getString("originFacilityId");
						 if(UtilValidate.isNotEmpty(routeId)){//rewrite with upperCase
							 routeId=routeId.toUpperCase(locale);
						 }
						 try{ updateVehicleTrip.set("partyId",facilityParty.get(routeId));
						      updateVehicleTrip.set("originFacilityId",routeId);
							  updateVehicleTrip.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
						      updateVehicleTrip.set("lastModifiedDate", UtilDateTime.nowTimestamp());
							   updateVehicleTrip.store(); 
						    }catch(GenericEntityException e){
						    	Debug.logError("Unable to set vehicleTrip  record in database"+e, module);
								return ServiceUtil.returnError("Unable to set vehicleTrip   record in database "); 
						    }
						 
					}
					
			 }
        }
		}catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}	
		result = ServiceUtil.returnSuccess("Service For UpdateVehicleStatus Runs Sucessfully");	        
        return result;
      }
	
    public static Map<String, Object> getFacilityRateAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String facilityId = (String) context.get("facilityId");
		String rateTypeId = (String) context.get("rateTypeId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String rateCurrencyUomId = "INR";
		if (UtilValidate.isNotEmpty(context.get("rateCurrencyUomId"))) {
			rateCurrencyUomId = (String) context.get("rateCurrencyUomId");
		}
		// if from date is null then lets take now timestamp as default
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.nowTimestamp();
		}
		Map result = ServiceUtil.returnSuccess();
		BigDecimal rateAmount = BigDecimal.ZERO;
		String uomId = "";
		// lets get the active rateAmount
		List facilityRates = FastList.newInstance();
		List exprList = FastList.newInstance();
		// facility level
		exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
		exprList.add(EntityCondition.makeCondition("rateTypeId",EntityOperator.EQUALS, rateTypeId));
		exprList.add(EntityCondition.makeCondition("rateCurrencyUomId",	EntityOperator.EQUALS, rateCurrencyUomId));

		EntityCondition paramCond = EntityCondition.makeCondition(exprList,	EntityOperator.AND);
		try {
			facilityRates = delegator.findList("FacilityRate", paramCond, null,	null, null, false);

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		try {
			facilityRates = EntityUtil.filterByDate(facilityRates, fromDate);
			// if no rates at facility level then, lets check for default rate
			if (UtilValidate.isEmpty(facilityRates)) {
				exprList.clear();
				// Default level
				exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, "_NA_"));
				exprList.add(EntityCondition.makeCondition("rateTypeId",EntityOperator.EQUALS, rateTypeId));
				exprList.add(EntityCondition.makeCondition("rateCurrencyUomId",	EntityOperator.EQUALS, rateCurrencyUomId));

				EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
				try {
					facilityRates = delegator.findList("FacilityRate",paramCond, null, null, null, false);

				} catch (GenericEntityException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.toString());
				}
			}

			GenericValue validFacilityRate = EntityUtil.getFirst(facilityRates);
			if (UtilValidate.isNotEmpty(validFacilityRate)) {
				if (UtilValidate.isNotEmpty(validFacilityRate.getString("acctgFormulaId"))) {
					String acctgFormulaId = validFacilityRate.getString("acctgFormulaId");
					BigDecimal slabAmount = (BigDecimal) context.get("slabAmount");
					uomId = validFacilityRate.getString("uomId");
					if (UtilValidate.isEmpty(slabAmount)) {
						slabAmount = BigDecimal.ZERO;
						Debug.logWarning("no slab amount found for acctgFormulaId taking zero as default ",	module);
					}
					Map<String, Object> input = UtilMisc.toMap("userLogin",	userLogin, "acctgFormulaId", acctgFormulaId,"variableValues", "QUANTITY=" + "1", "slabAmount",slabAmount);
					Map<String, Object> incentivesResult = dispatcher.runSync("evaluateAccountFormula", input);
					if (ServiceUtil.isError(incentivesResult)) {
						Debug.logError("unable to evaluate AccountFormula"+ acctgFormulaId, module);
						return ServiceUtil.returnError("unable to evaluate AccountFormula"+ acctgFormulaId);
					}
					double formulaValue = (Double) incentivesResult.get("formulaResult");
					rateAmount = new BigDecimal(formulaValue);

				} else {
					rateAmount = validFacilityRate.getBigDecimal("rateAmount");
					uomId = validFacilityRate.getString("uomId");
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("rateAmount", rateAmount);
		result.put("uomId", uomId);

		return result;
	}	
    public static Map<String, Object> getRouteDistance(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map result = ServiceUtil.returnSuccess();
		  Map<String , Object> inputRateAmt = FastMap.newInstance();
		  inputRateAmt.putAll(context);
			BigDecimal facilitySize = BigDecimal.ZERO;
		  try{
			   Map<String, Object> facilitySizeResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
			   if(UtilValidate.isNotEmpty(facilitySizeResult)){
				   BigDecimal tempFacilitySize=(BigDecimal) facilitySizeResult.get("rateAmount");
				   if (tempFacilitySize.compareTo(BigDecimal.ZERO) > 0)
		   			facilitySize = (BigDecimal) facilitySizeResult.get("rateAmount");
		   		}	
				if (ServiceUtil.isError(facilitySizeResult)) {
	   			Debug.logWarning("There was an error while getting Facility Size: " + ServiceUtil.getErrorMessage(result), module);
	       		return ServiceUtil.returnError("There was an error while getting Facility Size: " + ServiceUtil.getErrorMessage(result));          	            
	           }
			} catch (Exception e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
		   result.put("facilitySize", facilitySize);
		return result;
	}
    public static Map<String, Object> createTransporterRecovery(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String facilityId = (String) context.get("facilityId");
    	String customTimePeriodId = (String) context.get("customTimePeriodId");
    	String recoveryTypeId = (String)context.get("recoveryTypeId");
    	Timestamp incidentDate = (Timestamp)context.get("incidentDate");
    	BigDecimal amount = (BigDecimal)context.get("amount");
    	
    	
    	Debug.log("amount==========="+amount);
    	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String description=(String)context.get("description");
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		List conditionList=FastList.newInstance();
		try {
            conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_LMS_TRSPT_MRGN"));
            conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
            conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        		List<GenericValue> periodBillingList = FastList.newInstance();
        		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);	 
        		if(UtilValidate.isNotEmpty(periodBillingList)){
        			 Debug.logError("Billing Is Already Generated For This Period", module);
        			 return ServiceUtil.returnError("Billing Is Already Generated For This Period  and You Can Not Create Recovery!");   
        		}
			GenericValue facilityRecovery = delegator.makeValue("FineRecovery");
			facilityRecovery.put("facilityId", facilityId );
			facilityRecovery.put("customTimePeriodId", customTimePeriodId);
			facilityRecovery.put("incidentDate", incidentDate);
			facilityRecovery.put("recoveryTypeId", recoveryTypeId); 
			facilityRecovery.put("amount", amount);
			facilityRecovery.put("description", description);
			facilityRecovery.put("createdDate", UtilDateTime.nowTimestamp());
			facilityRecovery.put("lastModifiedDate", UtilDateTime.nowTimestamp());
			facilityRecovery.put("createdByUserLogin", userLogin.get("userLoginId"));
			delegator.createSetNextSeqId(facilityRecovery);            
			String recoveryId = (String) facilityRecovery.get("recoveryId");
    		
        }catch(GenericEntityException e){
			Debug.logError("Error while creating Transporter Recovery"+e.getMessage(), module);
		}
        result = ServiceUtil.returnSuccess("Transporter Recovery Created Sucessfully");
        result.put("createdDate",UtilDateTime.nowTimestamp());
        result.put("incidentDate",incidentDate);
        return result;
    }
    public static Map<String, Object> createOrUpdateDiselAmount(DispatchContext ctx,Map<String, Object> context) {
        Map<String, Object> resultMap = FastMap.newInstance();
        Map<String, Object> result = FastMap.newInstance();
        Map<String, Object> input = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        String facilityId = (String) context.get("facilityId");
        String productId = (String) context.get("productId");
        String rateTypeId = (String) context.get("rateTypeId");
        String facilityTypeId = (String) context.get("facilityTypeId");
        String uomId = (String) context.get("uomId");
        BigDecimal kilometers = (BigDecimal) context.get("kilometers");
        BigDecimal rateAmount = (BigDecimal) context.get("rateAmount");
        String rateCurrencyUomId = (String) context.get("rateCurrencyUomId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
        List<GenericValue> facilityRateList = FastList.newInstance();
        List<GenericValue> activeFacilityRate = FastList.newInstance();
        List<GenericValue> futureFacilityRate = FastList.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp tempfromDate=null;
        boolean isNewFacility = true;
	    try {
	        if(fromDate==null){
	        	fromDate= UtilDateTime.nowTimestamp();
	        }
	        if(UtilValidate.isNotEmpty(thruDate)){
	        	thruDate=UtilDateTime.getDayEnd(thruDate);
	        }
	        fromDate=UtilDateTime.getDayStart(fromDate);
	       
	       
	        if(UtilValidate.isNotEmpty(uomId) && uomId.equals("LEN_km") || rateTypeId.equals("FACILITY_SIZE") ){
				input = UtilMisc.toMap("userLogin", userLogin, "fromDate",fromDate,"facilityId", facilityId,"thruDate",thruDate,
							 "rateTypeId", "FACILITY_SIZE","productId","_NA_","kilometers",kilometers,"supplyTypeEnumId","_NA_", "rateCurrencyUomId","LEN_km","uomId","LEN_km");
				resultMap =dispatcher.runSync("createOrUpdateFacilityRate",input);
		        if (ServiceUtil.isError(resultMap)) {
					  Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			          return resultMap;
			    }
	        }  
	        if(!rateTypeId.equals("FACILITY_SIZE") ){
		        input.clear();
		    	input = UtilMisc.toMap("userLogin", userLogin, "fromDate",fromDate,"thruDate",thruDate,"facilityId", facilityId,
						 "rateTypeId", "TRANSPORTER_MRGN","productId","_NA_","rateAmount",rateAmount,"supplyTypeEnumId","_NA_","rateCurrencyUomId","INR","uomId","LEN_km");
			    resultMap =dispatcher.runSync("createOrUpdateFacilityRate",input);
	   
		        if (ServiceUtil.isError(resultMap)) {
				  Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		          return resultMap;
		         }	 
	        }
				
			result = ServiceUtil.returnSuccess("FacilityRate Amount is successfully updated");
		
	    }catch (Exception e) {
	    	Debug.logError(e, module);
			return ServiceUtil.returnError("Error while updating FacilityRate" + e);
	    }
        return result;
    }
    public static Map<String, Object> getStoreIssuesAndReceiptsForPeriod(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String previousCustomTimePeriodId = (String) context.get("previousTimePeriodId");
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		Map receiptIssueRegisterMap = FastMap.newInstance();
		try{
			
			Map productInventoryBalance = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(previousCustomTimePeriodId)){
				List<GenericValue> inventorySummary = delegator.findList("InventoryPeriodSummary", EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, previousCustomTimePeriodId), null, null, null, false);
				List<String> stores = EntityUtil.getFieldListFromEntityList(inventorySummary, "facilityId", true);
				for(String eachStore : stores){
					List<GenericValue> storeProducts = EntityUtil.filterByCondition(inventorySummary, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachStore));
					Map productBalanceMap = FastMap.newInstance();
					for(GenericValue storeProduct : storeProducts){
						Map tempMap = FastMap.newInstance();
						tempMap.put("productId", storeProduct.getString("productId"));
						tempMap.put("closingBalanceQty", storeProduct.getBigDecimal("closingBalanceQty"));
						tempMap.put("closingCost", storeProduct.getBigDecimal("closingCost"));
						productBalanceMap.put(storeProduct.getString("productId"), tempMap);
					}
					productInventoryBalance.put(eachStore, productBalanceMap);
				}
			}
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			EntityListIterator invItemDetailItr = delegator.find("InventoryItemAndDetail", condition, null, null, null,null);
		    
			GenericValue itemDetail =null; 
			while( invItemDetailItr != null && (itemDetail = invItemDetailItr.next()) != null) {
		        String facilityId = itemDetail.getString("facilityId");
		        String productId = itemDetail.getString("productId");
		        BigDecimal qty = itemDetail.getBigDecimal("quantityOnHandDiff");
		        BigDecimal unitCost = itemDetail.getBigDecimal("unitCost");
		        BigDecimal transAmt = qty.multiply(unitCost);
		        if(UtilValidate.isNotEmpty(receiptIssueRegisterMap.get(facilityId))){
		        	Map storeProductMap = (Map)receiptIssueRegisterMap.get(facilityId);
		           	if(UtilValidate.isNotEmpty(storeProductMap.get(productId))){
			           	Map prodMap = (Map)storeProductMap.get(productId);
			           	boolean isReceipt = Boolean.FALSE;
			           	if(qty.compareTo(BigDecimal.ZERO)>0){
			           		isReceipt = Boolean.TRUE;
			        	}
			           	if(isReceipt){
			           		BigDecimal extReceiptQty = (BigDecimal)prodMap.get("receiptQty");
			           		BigDecimal totalQty = extReceiptQty.add(qty);
			           		prodMap.put("receiptQty", totalQty);
			           		
			           	}
			           	else{
			           		BigDecimal extIssueQty = (BigDecimal)prodMap.get("issueQty");
			           		BigDecimal totalQty = extIssueQty.add(qty);
			           		prodMap.put("issueQty", extIssueQty.add(qty));
			           	}
			           	BigDecimal extUnitAmt = (BigDecimal)prodMap.get("closingCost");
		           		prodMap.put("closingCost", transAmt.add(extUnitAmt));
		           		
			        	storeProductMap.put(productId, prodMap);
			        	receiptIssueRegisterMap.put(facilityId, storeProductMap);
		           		
			        }
			        else{
			        	Map tempMap = FastMap.newInstance();
			        	if(qty.compareTo(BigDecimal.ZERO)>0){
			        		tempMap.put("receiptQty", qty);
			        		tempMap.put("issueQty", BigDecimal.ZERO);
			        	}
			        	else{
			        		tempMap.put("receiptQty", BigDecimal.ZERO);
			        		tempMap.put("issueQty", qty);
			        	}
			        	tempMap.put("closingCost", transAmt);
			        	storeProductMap.put(productId, tempMap);
			        	receiptIssueRegisterMap.put(facilityId, storeProductMap);
			            	
			        }
		        }
		        else{
		        	Map tempMap = FastMap.newInstance();
		        	Map productMap = FastMap.newInstance();
		        	if(qty.compareTo(BigDecimal.ZERO)>0){
		        		tempMap.put("receiptQty", qty);
		        		tempMap.put("issueQty", BigDecimal.ZERO);
		        	}
		        	else{
		        		tempMap.put("receiptQty", BigDecimal.ZERO);
		        		tempMap.put("issueQty", qty);
		        	}
		        	tempMap.put("closingCost", transAmt);
		        	productMap.put(productId, tempMap);
		        	receiptIssueRegisterMap.put(facilityId, productMap);
		        }
			}
			invItemDetailItr.close();
			Map finalReceiptIssueMap = FastMap.newInstance();
			Iterator invIter = receiptIssueRegisterMap.entrySet().iterator();
			while (invIter.hasNext()) {
				Map.Entry tempEntry = (Entry) invIter.next();
				Map eachStore = (Map) tempEntry.getValue();
				String storeId = (String) tempEntry.getKey();
				Map storeProductBalance = (Map)productInventoryBalance.get(storeId);
				Iterator eachStoreIter = eachStore.entrySet().iterator();
				Map prodQtyTrans = FastMap.newInstance();
				while (eachStoreIter.hasNext()) {
					Map.Entry tempItemEntry = (Entry) eachStoreIter.next();
					Map eachStoreItem = (Map) tempItemEntry.getValue();
					String productId = (String) tempItemEntry.getKey();
					BigDecimal prevCBQty = BigDecimal.ZERO;
					BigDecimal prevCBCost = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(storeProductBalance) && UtilValidate.isNotEmpty(storeProductBalance.get(productId))){
						Map prodCBMap = (Map) storeProductBalance.get(productId);
						prevCBQty = (BigDecimal)prodCBMap.get("closingBalanceQty");
						prevCBCost = (BigDecimal)prodCBMap.get("closingCost");
					}
					
					BigDecimal receipt = (BigDecimal) eachStoreItem.get("receiptQty");
					BigDecimal issues = ((BigDecimal) eachStoreItem.get("issueQty")).negate();
					BigDecimal closingBalance = (receipt.subtract(issues)).add(prevCBQty);
					BigDecimal closingCost = ((BigDecimal) eachStoreItem.get("closingCost")).add(prevCBCost);
					eachStoreItem.put("closingBalanceQty", closingBalance);
					eachStoreItem.put("closingCost", closingCost);
					prodQtyTrans.put(productId, eachStoreItem);
				}
				finalReceiptIssueMap.put(storeId, prodQtyTrans);
			}
			result.put("receiptIssueMap", finalReceiptIssueMap);
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}
    public static Map<String, Object>  sendFacilityRecoverySms(DispatchContext dctx, Map<String, Object> context)  {
        LocalDispatcher dispatcher = dctx.getDispatcher();	
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String facilityId = (String) context.get("facilityId");
        String partyId = (String) context.get("partyId");
        Timestamp createdDate = (Timestamp) context.get("incidentDate");
        String fromDate = (UtilDateTime.toDateString(createdDate, "MMMM dd,yyyy")).toString();
        String recoveryTypeId = (String)context.get("recoveryTypeId");
        BigDecimal amount = (BigDecimal)context.get("amount");
        
        Debug.log("recoveryTypeId============"+recoveryTypeId);
        Map<String, Object> serviceResult;
        Map<String, Object> userServiceResult;
        String countryCode = "91";
        String contactNumberTo = null;
        String description = null;
        try {
        	GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", recoveryTypeId),false);
        	if (UtilValidate.isNotEmpty(enumeration)) {
        		description = enumeration.getString("description");
        	}
        }catch (GenericEntityException e) {
           Debug.logError(e, module);
           return ServiceUtil.returnError(e.getMessage());
		}
        try {
        	// Send SMS notification to contractor
        	Map facilityParty=(Map)ByProductNetworkServices.getFacilityPartyContractor(dctx, UtilMisc.toMap("saleDate",createdDate ,"facilityId",facilityId)).get("facilityPartyMap");
        	
        	Debug.log("facilityParty=========="+facilityParty);
        	if(UtilValidate.isEmpty(facilityParty.get(facilityId))){
    			 Debug.logError("'Route "+facilityId+" Expired or No Contarctor Assigned To This Route....!'Entry Not Possible", module);
    			 return ServiceUtil.returnError("'Route "+facilityId+" Expired or No Contarctor Assigned To This Route....!'Entry Not Possible");   
    		}
        	Map<String, Object> getTelParams = FastMap.newInstance();
        	 getTelParams.put("partyId", facilityParty.get(facilityId));
        	 if(UtilValidate.isNotEmpty(partyId)){
        		 getTelParams.put("partyId", partyId);
        	 }
             getTelParams.put("userLogin", userLogin);                    	
             serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
             if (ServiceUtil.isError(serviceResult)) {
             	 Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
                 return ServiceUtil.returnSuccess();
             } 
             Debug.log("serviceResult============"+serviceResult);
             if(!UtilValidate.isEmpty(serviceResult.get("contactNumber"))){
             	contactNumberTo = (String) serviceResult.get("contactNumber");
             	if(!UtilValidate.isEmpty(serviceResult.get("countryCode"))){
             		contactNumberTo = (String) serviceResult.get("countryCode") + (String) serviceResult.get("contactNumber");
             	}
             }	
        	String text = " A penalty("+description+")" +" of " + " Rs."+amount.setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding"))
        	+" has been levied for route("+facilityId+")"+" on "+fromDate+"."+
			" Automated message from Mother Dairy.";
			Debug.logInfo("Sms text: " + text, module);
			Map<String, Object> sendSmsParams = FastMap.newInstance();      
		   if(UtilValidate.isNotEmpty(contactNumberTo)){
				 sendSmsParams.put("contactNumberTo", contactNumberTo);                     
		         sendSmsParams.put("text",text);  
		         dispatcher.runAsync("sendSms", sendSmsParams,false); 
			 }
			
		}catch (Exception e) {
			Debug.logError(e, "Error calling sendSmsToContactListNoCommEvent service", module);
			return ServiceUtil.returnError(e.getMessage());			
		} 
        return ServiceUtil.returnSuccess("Sms successfully sent!");		
    }
	
}
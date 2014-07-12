package in.vasista.vbiz.byproducts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;


public class POSApiServices {

    public static final String module = POSApiServices.class.getName();

    /*
     * Security check to make userLogin partyId must equal facility owner party Id if the user
     * is a retailer (has MOB_RTLR_DB_VIEW). If user is a sales rep (MOB_SREP_DB_VIEW permission), 
     * then we just return true.
     */

    static boolean hasFacilityAccess(DispatchContext dctx, Map<String, ? extends Object> context) {  
        Security security = dctx.getSecurity();

    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	GenericValue facility = (GenericValue) context.get("facility");

        if (security.hasEntityPermission("MOB_SREP_DB", "_VIEW", userLogin)) {
            return true;
        } 		
        if (security.hasEntityPermission("MOB_RTLR_DB", "_VIEW", userLogin)) {
        	if (userLogin != null && userLogin.get("partyId") != null) {
        		String userLoginParty = (String)userLogin.get("partyId");
        		String ownerParty = (String)facility.get("ownerPartyId");
        		if (userLoginParty.equals(ownerParty)) {
        			return true;
        		}
        	}
        } 		
    	return false;
    }

    public static Map<String, Object> getMobilePermissions(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();			
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
		List permissionList = FastList.newInstance();

        if (security.hasEntityPermission("MOB_SREP_DB", "_VIEW", userLogin)) {
        	permissionList.add("MOB_SREP_DB_VIEW");
        } 	
        if (security.hasEntityPermission("MOB_RTLR_DB", "_VIEW", userLogin)) {
        	permissionList.add("MOB_RTLR_DB_VIEW");
        }         
        if (security.hasEntityPermission("MOB_HR_DB", "_VIEW", userLogin)) {
        	permissionList.add("MOB_HR_DB_VIEW");
        }   
        
		Map result = FastMap.newInstance();  	
		Map permissions = FastMap.newInstance();
		permissions.put("permissionList", permissionList);
		result.put("permissionResults", permissions);
		
Debug.logInfo("result:" + result, module);
    	return result;
    }
    
    public static Map<String, Object> getProductPrices(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String boothId = (String) context.get("boothId");
		if (UtilValidate.isEmpty(boothId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id");	   
		}	
  		GenericValue facility = null;
  		try{
  			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",boothId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching facility " +boothId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + boothId);	   
  		} 		
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_CATALOG", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to view catalog!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }		
        if (!hasFacilityAccess(dctx, UtilMisc.toMap("userLogin", userLogin, "facility", facility))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access facility: " + boothId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        }
 
		Map result = FastMap.newInstance();  		
    	Map priceResult = ByProductReportServices.getByProductPricesForFacility(dctx, 
    			UtilMisc.toMap("userLogin", userLogin, "facilityId", boothId, "productCategoryId", "INDENT"));
		 if(!ServiceUtil.isError(priceResult)){
			 result.put("productsPrice", (Map)priceResult.get("productsPrice"));
		 }  
	    Debug.logInfo("productsPrice:" + result.get("productsPrice"), module);
		 
    	return result;
    }

    public static Map<String, Object> getFacilityIndent(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String facilityId = (String) context.get("facilityId");
		if (UtilValidate.isEmpty(facilityId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id");	   
		}	
  		GenericValue facility = null;
  		try{
  			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching facility " +facilityId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + facilityId);	   
  		} 		
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_INDENT", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to view indent!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }
        if (!hasFacilityAccess(dctx, UtilMisc.toMap("userLogin", userLogin, "facility", facility))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access facility: " + facilityId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        }        
    	Timestamp amSupplyDate = (Timestamp) context.get("supplyDate");
    	String amSupplyDateStr= "";
    	if(UtilValidate.isNotEmpty(amSupplyDate)){
    		amSupplyDateStr = UtilDateTime.toDateString(amSupplyDate, "dd MMMMM, yyyy");
    	}
		Map indentMap = FastMap.newInstance();  
		Map<String, Object> inputParamMap = FastMap.newInstance();
		inputParamMap.put("userLogin", userLogin);  
		inputParamMap.put("boothId", facilityId);  
		inputParamMap.put("supplyDate", amSupplyDateStr); 
		inputParamMap.put("subscriptionTypeId", "AM"); 	
		inputParamMap.put("productSubscriptionTypeId", "CASH"); 
Debug.logInfo("inputParamMap:" + inputParamMap, module);		 		
    	Map indentResultsAM = ByProductNetworkServices.getBoothChandentIndent(dctx, inputParamMap);
		if(!ServiceUtil.isError(indentResultsAM)){
			indentMap.put("AM", indentResultsAM.get("changeIndentProductList"));
		}  
		Timestamp pmSupplyDate = UtilDateTime.addDaysToTimestamp(amSupplyDate, -1);
    	String pmSupplyDateStr= "";
    	if(UtilValidate.isNotEmpty(pmSupplyDate)){
    		pmSupplyDateStr = UtilDateTime.toDateString(pmSupplyDate, "dd MMMMM, yyyy");
    	}		
		inputParamMap.put("supplyDate", pmSupplyDateStr);     	
		inputParamMap.put("subscriptionTypeId", "PM"); 			
    	Map indentResultsPM = ByProductNetworkServices.getBoothChandentIndent(dctx, inputParamMap);
		if(!ServiceUtil.isError(indentResultsPM)){
			indentMap.put("PM", indentResultsPM.get("changeIndentProductList"));
		}  
		indentMap.put("amSupplyDate", UtilDateTime.toDateString(amSupplyDate,"yyyy-MM-dd"));
		indentMap.put("pmSupplyDate", UtilDateTime.toDateString(pmSupplyDate,"yyyy-MM-dd"));		
		Map result = FastMap.newInstance();  		
		result.put("indentResults", indentMap);
Debug.logInfo("indentResults:" + indentMap, module);		 
    	return result;
    }
    
    public static Map<String, Object> processChangeIndent(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String boothId = (String) context.get("boothId");
		if (UtilValidate.isEmpty(boothId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id " + boothId);
		}	
		GenericValue facility = null;
		try {
			facility = delegator.findOne("Facility", UtilMisc.toMap(
					"facilityId", boothId), false);
		} catch (GenericEntityException e) {
			Debug.logWarning("Error fetching facility " + boothId + " " + e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + boothId);
		}		
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_INDENT", "_EDIT", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to update indent!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }		
        if (!hasFacilityAccess(dctx, UtilMisc.toMap("userLogin", userLogin, "facility", facility))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access facility: " + boothId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        } 
		Timestamp supplyDate = (Timestamp) context.get("supplyDate");
		String subscriptionTypeId = (String) context.get("subscriptionTypeId");        
        // Next check for indent cut-off times, if configured
		try{
			GenericValue tenantConfigMobileIndentEndTime = null;
			if (subscriptionTypeId.equals("AM")) {
				tenantConfigMobileIndentEndTime = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","mobileAMIndentEndTime"), false);
			} else if (subscriptionTypeId.equals("PM")) {
				tenantConfigMobileIndentEndTime = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","mobilePMIndentEndTime"), false);			
			}
			if (UtilValidate.isNotEmpty(tenantConfigMobileIndentEndTime)) {
				// e.g. 09:30 or 17:00
				 String tenantConfigMobileIndentEndTimeStr = tenantConfigMobileIndentEndTime.getString("propertyValue");
				 String currentTimeStr = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "HH:mm");
				 if (currentTimeStr.compareTo(tenantConfigMobileIndentEndTimeStr) > 0) {
			            Debug.logWarning("**** Indent cutoff time exceeded [" + currentTimeStr + " > " + 
			            		tenantConfigMobileIndentEndTimeStr + "]", module);
			            return ServiceUtil.returnError("Indent cutoff time exceeded [" + currentTimeStr + " > " + 
			            		tenantConfigMobileIndentEndTimeStr + "]");					 
				 }
			}
		 }catch (GenericEntityException e) {
			// TODO: handle exception
			 Debug.logError(e, module);
		}        
        
		List<Map<String, Object>> indentItems = (List<Map<String, Object>>) context
				.get("indentItems");
		String infoString = "processChangeIndent:: indentItems: " + indentItems;
Debug.logInfo(infoString, module);
		if (indentItems.isEmpty()) {
			Debug.logError("No indent items found; " + infoString, module);
			return ServiceUtil.returnError("No indent items found; "
					+ infoString);
		}
		Map result = ServiceUtil
				.returnSuccess("Indent items successfully processed.");
		Map<String, Object> indentResults = FastMap.newInstance();


		String shipmentTypeId = subscriptionTypeId + "_SHIPMENT";
		String productSubscriptionTypeId = "CASH";
		String routeChangeFlag = "";
		Map boothDetails = (Map) (ByProductNetworkServices.getBoothRoute(dctx, UtilMisc.toMap("boothId", boothId, "subscriptionTypeId", subscriptionTypeId, "userLogin", userLogin))).get("boothDetails");
		String sequenceNum = (String) boothDetails.get("routeId"); // ::TODO:: for now fix to default route
		List<GenericValue> subscriptionList = FastList.newInstance();
		GenericValue subscription = null;
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("facilityId",
					EntityOperator.EQUALS, boothId));
			if (subscriptionTypeId.equals("AM")) {
				conditionList.add(EntityCondition.makeCondition(EntityCondition
						.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId), EntityOperator.OR, EntityCondition.makeCondition(
								"subscriptionTypeId", EntityOperator.EQUALS,
								null)));

			} else {
				conditionList.add(EntityCondition.makeCondition( "subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
			}
			EntityCondition subCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			subscriptionList = delegator.findList("SubscriptionAndFacility", subCond, null, null, null, false);
			subscriptionList = EntityUtil.filterByDate(subscriptionList, supplyDate);
			if (UtilValidate.isEmpty(subscriptionList)) {
				return ServiceUtil.returnError("Booth subscription does not exist for " + boothId);
			}
			subscription = EntityUtil.getFirst(subscriptionList);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Problem getting Booth subscription for " + boothId, module);
			return ServiceUtil.returnError("Problem getting Booth subscription for "+ boothId);
		}
		List<Map> productQtyList = FastList.newInstance();
		for (int i = 0; i < indentItems.size(); ++i) {
			Map prodQuant = FastMap.newInstance();
			Map indentItem = indentItems.get(i);
			String productId = (String) indentItem.get("productId");
			int quantityInt = ((Integer) indentItem.get("qty")).intValue();
			BigDecimal quantity = BigDecimal.valueOf(quantityInt);
			prodQuant.put("productId", productId);
			if (UtilValidate.isEmpty(quantity)) {
				Debug.logError("quantity is empty for the product " + productId, module);
				return ServiceUtil.returnError("quantity is empty for the product " + productId);
			}
			prodQuant.put("quantity", quantity);			
			prodQuant.put("sequenceNum", sequenceNum);
			productQtyList.add(prodQuant);

		}// end of loop

		Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin", userLogin);
		processChangeIndentHelperCtx.put("subscriptionId", subscription.getString("subscriptionId"));
		processChangeIndentHelperCtx.put("boothId", boothId);
		processChangeIndentHelperCtx.put("shipmentTypeId", shipmentTypeId);
		processChangeIndentHelperCtx.put("effectiveDate", supplyDate);
		processChangeIndentHelperCtx.put("productQtyList", productQtyList);
		processChangeIndentHelperCtx.put("productSubscriptionTypeId", productSubscriptionTypeId);
		try {
			Map svcResult = FastMap.newInstance(); 
			svcResult = dispatcher.runSync("processChangeIndentHelper", processChangeIndentHelperCtx);
			if (ServiceUtil.isError(svcResult)) {
				String errMsg = ServiceUtil.getErrorMessage(svcResult);
				Debug.logError(errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem updating subscription for booth " + boothId, module);
			return ServiceUtil.returnError("Problem updating subscription for booth " + boothId);
		}
  		indentResults.put("numIndentItems", indentItems.size());
  		result.put("indentResults", indentResults);		
  		return result;   
    }

    public static Map<String, Object> getFacilityAccountSummary(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String facilityId = (String) context.get("facilityId");
		if (UtilValidate.isEmpty(facilityId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id");	   
		}	
  		GenericValue facility = null;
  		try{
  			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching facility " +facilityId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + facilityId);	   
  		} 		
        if (!hasFacilityAccess(dctx, UtilMisc.toMap("userLogin", userLogin, "facility", facility))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access facility: " + facilityId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        } 		
		
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
		BigDecimal obAmount = BigDecimal.ZERO;    	
		BigDecimal receipts = BigDecimal.ZERO;
		BigDecimal saleAmt = BigDecimal.ZERO;	
		BigDecimal closingAmt = BigDecimal.ZERO;		
		
		Map<String, Object> facilityLedgerMap = FastMap.newInstance();  		
    	
		// get the OB
		Map<String, Object> obParamMap = FastMap.newInstance();
		obParamMap.put("userLogin", userLogin);  
		obParamMap.put("facilityId", facilityId);  
		obParamMap.put("saleDate", fromDate);     
		obAmount =	(BigDecimal)ByProductNetworkServices.getOpeningBalanceForBooth( dctx , obParamMap).get("openingBalance");
		facilityLedgerMap.put("openingAmt", obAmount.setScale(2,BigDecimal.ROUND_HALF_UP));

		// get receipts
		Map<String, Object> receiptsParamMap = FastMap.newInstance();
		receiptsParamMap.put("facilityId", facilityId);  
		receiptsParamMap.put("fromDate", fromDate);  
		receiptsParamMap.put("thruDate", thruDate);   
		
		Map boothsPaidDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , receiptsParamMap);
		if(UtilValidate.isNotEmpty(boothsPaidDetail)){
			receipts = (BigDecimal)boothsPaidDetail.get("invoicesTotalAmount");
		}
		facilityLedgerMap.put("receipts", receipts.setScale(2,BigDecimal.ROUND_HALF_UP));

		// get saleamount
		Map<String, Object> salesParamMap = FastMap.newInstance();
		salesParamMap.put("facilityIds", UtilMisc.toList(facilityId));  
		salesParamMap.put("fromDate", fromDate);  
		salesParamMap.put("thruDate", thruDate); 
		Map dayTotals = ByProductNetworkServices.getPeriodTotals(dctx, salesParamMap);
		saleAmt= (BigDecimal)dayTotals.get("totalRevenue");
		facilityLedgerMap.put("salesAmt", saleAmt.setScale(2,BigDecimal.ROUND_HALF_UP));
		
		// compute closing balance
		closingAmt = ((BigDecimal)facilityLedgerMap.get("openingAmt")).add((BigDecimal)facilityLedgerMap.get("salesAmt")).subtract((BigDecimal)facilityLedgerMap.get("receipts")).setScale(2,BigDecimal.ROUND_HALF_UP);	
		facilityLedgerMap.put("closingAmt", closingAmt);
		
		Map result = FastMap.newInstance();  		
		result.put("accountSummary", facilityLedgerMap);
Debug.logInfo("accountSummary:" + facilityLedgerMap, module);		 
    	return result;
    }
    
    
    public static Map<String, Object> getFacilityPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String facilityId = (String) context.get("facilityId");
		if (UtilValidate.isEmpty(facilityId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id");	   
		}	 
  		GenericValue facility = null;
  		try{
  			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching facility " +facilityId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + facilityId);	   
  		} 		
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_PAYMENT", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to fetch payments!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }
        if (!hasFacilityAccess(dctx, UtilMisc.toMap("userLogin", userLogin, "facility", facility))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access facility: " + facilityId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        } 	
        
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
		if (UtilValidate.isEmpty(fromDate)) {
			Debug.logError("Empty fromDate", module);
			return ServiceUtil.returnError("Empty fromDate");	   
		}    	
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
		if (UtilValidate.isEmpty(thruDate)) {
			Debug.logError("Empty thruDate", module);
			return ServiceUtil.returnError("Empty thruDate");	   
		}   
		Map result = FastMap.newInstance();  		
		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("userLogin", userLogin);  
		paramMap.put("facilityId", facilityId);  
		paramMap.put("fromDate", fromDate);     
		paramMap.put("thruDate", thruDate);  
		Map<String, Object> facilityPayments = FastMap.newInstance();
		facilityPayments = ByProductNetworkServices.getBoothPaidPayments( dctx , paramMap);
		result.put("paymentsResult", facilityPayments);
Debug.logInfo("paymentResults:" + facilityPayments, module);		 
    	return result;
    }    
    
    public static Map<String, Object> getFacilityOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String facilityId = (String) context.get("facilityId");
		if (UtilValidate.isEmpty(facilityId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id");	   
		}	
  		GenericValue facility = null;
  		try{
  			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching facility " +facilityId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + facilityId);	   
  		} 			
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_ORDER", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to fetch orders!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }	
        if (!hasFacilityAccess(dctx, UtilMisc.toMap("userLogin", userLogin, "facility", facility))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access facility: " + facilityId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        } 
        
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
		if (UtilValidate.isEmpty(fromDate)) {
			Debug.logError("Empty fromDate", module);
			return ServiceUtil.returnError("Empty fromDate");	   
		}    
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
		if (UtilValidate.isEmpty(thruDate)) {
			Debug.logError("Empty thruDate", module);
			return ServiceUtil.returnError("Empty thruDate");	   
		}   
		Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
		Timestamp thruDateEnd = UtilDateTime.getDayEnd(thruDate);

		List amShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator ,fromDateStart,thruDateEnd,"AM");
		List pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator ,fromDateStart,thruDateEnd,"PM");

		Map allDaySaleMap = FastMap.newInstance(); 
		Map amBoothDayTotals = FastMap.newInstance(); 
		Map pmBoothDayTotals = FastMap.newInstance(); 

		if(UtilValidate.isNotEmpty(amShipmentIds)){
			Map dayTotals = ByProductNetworkServices.getPeriodTotals(dctx, 
					UtilMisc.toMap("facilityIds", UtilMisc.toList(facilityId), "shipmentIds", amShipmentIds,
							"fromDate", fromDateStart, "thruDate", thruDateEnd, "includeReturnOrders", true));
			if(UtilValidate.isNotEmpty(dayTotals)){
				amBoothDayTotals = (Map)dayTotals.get("dayWiseTotals");
			}
		}

		//pmShipments
		if(UtilValidate.isNotEmpty(pmShipmentIds)){
			Map dayTotals = ByProductNetworkServices.getPeriodTotals(dctx, 
					UtilMisc.toMap("facilityIds", UtilMisc.toList(facilityId), "shipmentIds", pmShipmentIds,
							"fromDate", fromDateStart, "thruDate", thruDateEnd, "includeReturnOrders", true));
			if(UtilValidate.isNotEmpty(dayTotals)){
				pmBoothDayTotals = (Map)dayTotals.get("dayWiseTotals");
			}
		}
		
		for(int j=0 ; j < (UtilDateTime.getIntervalInDays(fromDateStart,thruDateEnd)+1); j++){
			Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDateStart, j);
			String curntDay=UtilDateTime.toDateString(saleDate ,"yyyy-MM-dd");
			Map curntDaySalesMap = FastMap.newInstance(); 
			if(UtilValidate.isNotEmpty(amBoothDayTotals.get(curntDay))){
				curntDaySalesMap.put("AM", ((Map)amBoothDayTotals.get(curntDay)).get("productTotals"));
			}
			if(UtilValidate.isNotEmpty(pmBoothDayTotals.get(curntDay))){
				curntDaySalesMap.put("PM", ((Map)pmBoothDayTotals.get(curntDay)).get("productTotals"));
			}
			allDaySaleMap.put(curntDay, curntDaySalesMap);
		}
		Map result = FastMap.newInstance();  		
		result.put("ordersResult", allDaySaleMap);
Debug.logInfo("result:" + result, module);		 
    	return result;
    }        
    
    public static Map<String, Object> getAllRMFacilities(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_FACILITY", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to fetch facilities!", module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");
        }    	
		Map boothsDetails = ByProductNetworkServices.getAllBoothsDetails(dctx, UtilMisc.toMap("userLogin", userLogin));
		Map result = FastMap.newInstance();  		
		result.put("facilitiesResult", boothsDetails);
Debug.logInfo("result:" + result, module);		 
    	return result;
    }            
    

    public static Map<String, Object> getFacilityDues(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	String facilityId = (String) context.get("boothId");
		if (UtilValidate.isEmpty(facilityId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id");	   
		}	
  		GenericValue facility = null;
  		try{
  			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching facility " +facilityId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + facilityId);	   
  		} 			
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Security security = dctx.getSecurity();
        // security check
        if (!hasFacilityAccess(dctx, UtilMisc.toMap("userLogin", userLogin, "facility", facility))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access facility: " + facilityId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        }     
		Map svcResult = FastMap.newInstance(); 
		try {
			svcResult = dispatcher.runSync("getBoothDues", context);
			if (ServiceUtil.isError(svcResult)) {
				String errMsg = ServiceUtil.getErrorMessage(svcResult);
				Debug.logError(errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem fetching dues for booth " + facilityId, module);
			return ServiceUtil.returnError("Problem fetching dues for booth " + facilityId);
		}	
		return svcResult;   
    }
}
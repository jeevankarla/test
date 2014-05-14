package in.vasista.vbiz.byproducts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class POSApiServices {

    public static final String module = POSApiServices.class.getName();

	
    /**
     * Create POS Order
     * @param ctx the dispatch context
     * @param context 
     */
    public static Map<String, Object> createPOSOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();

		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<Map<String, Object>> orderItems = (List<Map<String, Object>>) context.get("orderItems");
		String infoString = "createPOSOrder:: orderItems: " + orderItems;
Debug.logInfo(infoString, module);
		if (orderItems.isEmpty()) {
			Debug.logError("No order items found; " + infoString, module);
			return ServiceUtil.returnError("No order items found; " + infoString);	   
		}	
		Map result = ServiceUtil.returnSuccess("Order items successfully processed.");
		Map<String, Object> orderResults = FastMap.newInstance();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String boothId = (String) context.get("boothId");
    	Timestamp saleDate = (Timestamp) context.get("saleDate");
		if (UtilValidate.isEmpty(boothId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id" + infoString);	   
		}	    	
  		GenericValue facility = null;
  		try{
  			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",boothId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching facility " +boothId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + boothId);	   
  		}
  		String categoryTypeEnum = (String)facility.get("categoryTypeEnum");
  		if(!categoryTypeEnum.equals("PARLOUR")){
  			Debug.logWarning("Facility not a parlour [" +boothId + "]", module);
			return ServiceUtil.returnError("Facility not a parlour [" +boothId + "]");			
  		}    	
    	

		Map<String, Object> inputParamMap = FastMap.newInstance();
		inputParamMap.put("userLogin", userLogin);
  		Map prodQuant = FastMap.newInstance();			
		for (int i = 0; i < orderItems.size(); ++i) {	
			Map orderItem = orderItems.get(i);
		  	String productId = (String)orderItem.get("productId");
		  	int quantityInt = ((Integer)orderItem.get("qty")).intValue();
		  	BigDecimal quantity = BigDecimal.valueOf(quantityInt);
		  	prodQuant.put(productId, quantity);		  	
		}
  		if(UtilValidate.isEmpty(prodQuant)){
  			Debug.logError("No Products to Process for Order", module);
			return ServiceUtil.returnError("No Products to Process for Order");
  		}		
  		inputParamMap.put("facilityId", boothId);
  		inputParamMap.put("productPriceTypeId", "PM_RC_P");
  		//::TODO::
	  	inputParamMap.put("supplyDate", saleDate);
	  	inputParamMap.put("prodQuant", prodQuant);	  	
Debug.logInfo("Before calling processParlorSalesOrder inputParamMap:" + inputParamMap, module);
	  	try {
	  		Map<String, Object> serviceResult = dispatcher.runSync("processParlorSalesOrder", inputParamMap);
			if(ServiceUtil.isError(serviceResult)){
				Debug.logError("ERROR in processParlorSalesOrder service; " + (String)serviceResult.get("errorMessage") , module);
				return ServiceUtil.returnError((String)serviceResult.get("errorMessage"));
			}
			orderResults.put("orderId", serviceResult.get("orderId"));		
		} catch (GenericServiceException e) {
			Debug.logError(e, "Trouble calling processParlorSalesOrder service; " + e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());

		}
		result.put("orderResults", orderResults);		
		return result; 
    }
    
    /**
     * Create POS Order
     * @param ctx the dispatch context
     * @param context 
     */
    public static Map<String, Object> cancelPOSOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();

		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> cancelResults = FastMap.newInstance();
		Map result = ServiceUtil.returnSuccess("Order successfully cancelled.");

    	GenericValue userLogin = (GenericValue) context.get("userLogin");
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
		Map<String, Object> inputParamMap = FastMap.newInstance();
		inputParamMap.put("userLogin", userLogin);  
		inputParamMap.put("productStoreId", boothId);  
		inputParamMap.put("statusId", ""); //::TODO::  		
		inputParamMap.put("orderId", (String) context.get("orderId"));  
	  	try {
	  		Map<String, Object> serviceResult = dispatcher.runSync("cancelParlorSalesOrder", inputParamMap);
			if(ServiceUtil.isError(serviceResult)){
				Debug.logError("ERROR in cancelParlorSalesOrder service; " + (String)serviceResult.get("errorMessage") , module);
				return ServiceUtil.returnError((String)serviceResult.get("errorMessage"));
			}
			cancelResults.put("orderId", serviceResult.get("orderId"));					
		} catch (GenericServiceException e) {
			Debug.logError(e, "Trouble calling cancelParlorSalesOrder service; " + e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());

		}		
		result.put("cancelResults", cancelResults);		
		
		return result;

    }
    
    /**
     * ::TODO:: This is a temp copy-n-paste from ByProductService.  We need to refactor the method
     * so that the core logic is callable from any client (web, xmlrpc, etc)
     */
    public static Map<String, Object> createSubscriptionIndent(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();

		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<Map<String, Object>> indentItems = (List<Map<String, Object>>) context.get("indentItems");
		String infoString = "createSubscriptionIndent:: indentItems: " + indentItems;
Debug.logInfo(infoString, module);
		if (indentItems.isEmpty()) {
			Debug.logError("No order items found; " + infoString, module);
			return ServiceUtil.returnError("No indent items found; " + infoString);	   
		}	
		Map result = ServiceUtil.returnSuccess("Indent items successfully processed.");
		Map<String, Object> indentResults = FastMap.newInstance();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String boothId = (String) context.get("boothId");
		if (UtilValidate.isEmpty(boothId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id" + infoString);	   
		}	    	
    	Timestamp supplyDate = (Timestamp) context.get("supplyDate");
  		GenericValue facility = null;
  		try{
  			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",boothId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching facility " +boothId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + boothId);	   
  		}  		
		Map<String, Object> inputParamMap = FastMap.newInstance();
		inputParamMap.put("userLogin", userLogin);  
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
  		Timestamp effectiveDate = supplyDate; 	  	      
  		String destinationFacilityId = null;
  		String productSubscriptionTypeId = null;
  		String routeId = "1"; //::TODO::
  		GenericValue newValue = null;
  		String subscriptionId = null;
  		boolean beganTransaction = false;
  		try{
  			beganTransaction = TransactionUtil.begin();
	  		productSubscriptionTypeId = "CASH_BYPROD";
			List conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
			conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
			List<GenericValue> subscription = null;
			try {
				subscription = delegator.findList("Subscription", condition, null, null, null, false);
			}catch (GenericEntityException e1) {
				Debug.logError(e1, module);
				TransactionUtil.rollback();
				return ServiceUtil.returnError(e1.getMessage());				  
			}
			subscription = EntityUtil.filterByDate(subscription, effectiveDate);
			if(UtilValidate.isNotEmpty(subscription)){
				if(subscription.size() == 1){
					GenericValue subscribe = subscription.get(0);
					subscriptionId =  subscribe.getString("subscriptionId");
				}
			}
			if(UtilValidate.isEmpty(subscriptionId)){
				Debug.logError("There are no 'active subscriptions' for Party Code  :"+boothId, module);
				TransactionUtil.rollback();
				return ServiceUtil.returnError("There are no 'active subscriptions' for Party Code  :"+boothId);				  
			}
			conditionList.clear();
			conditionList = UtilMisc.toList(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
			conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
			conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.getDayStart(effectiveDate)));
			conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.getDayEnd(effectiveDate)));
			EntityCondition condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try{
				List subscriptionprodList = delegator.findList("SubscriptionProduct", condition1, null, null, null, false);
				if(UtilValidate.isNotEmpty(subscriptionprodList)){
					int rows_deleted = delegator.removeAll(subscriptionprodList);
					Debug.log(rows_deleted+" rows deleted from subscription entity");
				}
			}catch (GenericEntityException e1) {
				Debug.logError(e1, module);
				TransactionUtil.rollback();
				return ServiceUtil.returnError(e1.getMessage());				  
			}	
	  		Map prodQuant = FastMap.newInstance();			
			for (int i = 0; i < indentItems.size(); ++i) {	
				Map indentItem = indentItems.get(i);
			  	String productId = (String)indentItem.get("productId");
			  	int quantityInt = ((Integer)indentItem.get("qty")).intValue();
			  	BigDecimal quantity = BigDecimal.valueOf(quantityInt);
			  	prodQuant.put(productId, quantity);		  				  	
		  		if(UtilValidate.isEmpty(quantity)){
		  			Debug.logError("quantity is empty for the product "+productId, module);
			  		TransactionUtil.rollback();
					return ServiceUtil.returnError("quantity is empty for the product "+productId);				  
			    }			  			  		  
		  		if(quantity.compareTo(BigDecimal.ZERO)>0  && UtilValidate.isNotEmpty(productId)){	    
		  			newValue = delegator.makeValue("SubscriptionProduct");
		  			newValue.set("productId", productId);
		  			newValue.set("quantity", quantity);
		  			newValue.set("fromDate", UtilDateTime.getDayStart(effectiveDate));
		  			newValue.set("thruDate", UtilDateTime.getDayEnd(effectiveDate));		  				
		  			newValue.set("subscriptionId", subscriptionId);
		  			newValue.set("productSubscriptionTypeId", productSubscriptionTypeId);
		  			newValue.set("sequenceNum", routeId);
		  			newValue.set("destinationFacilityId", destinationFacilityId);
		  			newValue.put("createdByUserLogin",userLogin.get("userLoginId"));
		  			newValue.put("createdDate",nowTimeStamp);   
		  			newValue.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
		  			newValue.put("lastModifiedDate",nowTimeStamp); 		  				
		  			try {
		  				delegator.create(newValue);
			  		} catch (GenericEntityException e) {
			  			Debug.logError("Error in storing Indent for Product : "+productId+ "\t"+e.toString(),module);
					  	TransactionUtil.rollback();
						return ServiceUtil.returnError("Error in storing Indent for Product : "+productId+ "\t"+e.toString());				  
			  		}
		  		  }
			}//end of loop	
  		} catch (GenericEntityException e) {
  			try {
  				// only rollback the transaction if we started one...
  				TransactionUtil.rollback(beganTransaction, "Error saving subscription product", e);
  			} catch (GenericEntityException e2) {
  				Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		}
	  		Debug.logError("An entity engine error occurred while saving  subscription indent", module);
	  		return ServiceUtil.returnError("An entity engine error occurred while saving  subscription indent: "+e.toString());				  	  		  
  		}
	    // only commit the transaction if we started one... this will throw an exception if it fails
  		try {
  			TransactionUtil.commit(beganTransaction);
  		} catch (GenericEntityException e) {
  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while saving subscription product", module);
  			return ServiceUtil.returnError("Could not commit transaction for entity engine error occurred while saving subscription product: "+e.toString());				  	  		  
  		}
  		indentResults.put("numIndentItems", indentItems.size());
  		result.put("indentResults", indentResults);		
  		return result; 
	}    
    
    public static Map<String, Object> getProductPrices(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
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
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String facilityId = (String) context.get("facilityId");
		if (UtilValidate.isEmpty(facilityId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id");	   
		}	    	
    	Timestamp supplyDate = (Timestamp) context.get("supplyDate");
    	String supplyDateStr= "";
    	if(UtilValidate.isNotEmpty(supplyDate)){
    		supplyDateStr = UtilDateTime.toDateString(supplyDate, "dd MMMMM, yyyy");
    	}
		Map indentMap = FastMap.newInstance();  
		Map<String, Object> inputParamMap = FastMap.newInstance();
		inputParamMap.put("userLogin", userLogin);  
		inputParamMap.put("boothId", facilityId);  
		inputParamMap.put("supplyDate", supplyDateStr); 
		inputParamMap.put("subscriptionTypeId", "AM"); 	
		inputParamMap.put("productSubscriptionTypeId", "CASH"); 				
    	Map indentResultsAM = ByProductNetworkServices.getBoothChandentIndent(dctx, inputParamMap);
		if(!ServiceUtil.isError(indentResultsAM)){
			indentMap.put("AM", indentResultsAM.get("changeIndentProductList"));
		}  
		inputParamMap.put("subscriptionTypeId", "PM"); 			
    	Map indentResultsPM = ByProductNetworkServices.getBoothChandentIndent(dctx, inputParamMap);
		if(!ServiceUtil.isError(indentResultsPM)){
			indentMap.put("PM", indentResultsAM.get("changeIndentProductList"));
		}  
		
		Map result = FastMap.newInstance();  		
		result.put("indentResults", indentMap);
	    Debug.logInfo("indentResults:" + indentMap, module);		 
    	return result;
    }
    
    public static Map<String, Object> processChangeIndent(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
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
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String boothId = (String) context.get("boothId");
		if (UtilValidate.isEmpty(boothId)) {
			Debug.logError("Empty facility Id", module);
			return ServiceUtil.returnError("Empty facility Id" + infoString);
		}
		Timestamp supplyDate = (Timestamp) context.get("supplyDate");
		String subscriptionTypeId = (String) context.get("subscriptionTypeId");
		String shipmentTypeId = subscriptionTypeId + "_SHIPMENT";
		String productSubscriptionTypeId = "CASH";
		String routeChangeFlag = "";
		Map boothDetails = (Map) (ByProductNetworkServices.getBoothRoute(dctx, UtilMisc.toMap("boothId", boothId, "userLogin", userLogin))).get("boothDetails");
		String sequenceNum = (String) boothDetails.get("routeId"); // ::TODO:: for now fix to default route
		GenericValue facility = null;
		try {
			facility = delegator.findOne("Facility", UtilMisc.toMap(
					"facilityId", boothId), false);
		} catch (GenericEntityException e) {
			Debug.logWarning("Error fetching facility " + boothId + " " + e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching facility " + boothId);
		}

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
		Map prodQuant = FastMap.newInstance();
		List<Map> productQtyList = FastList.newInstance();
		for (int i = 0; i < indentItems.size(); ++i) {
			Map indentItem = indentItems.get(i);
			String productId = (String) indentItem.get("productId");
			int quantityInt = ((Integer) indentItem.get("qty")).intValue();
			BigDecimal quantity = BigDecimal.valueOf(quantityInt);
			prodQuant.put(productId, quantity);
			if (UtilValidate.isEmpty(quantity)) {
				Debug.logError("quantity is empty for the product " + productId, module);
				return ServiceUtil.returnError("quantity is empty for the product " + productId);
			}
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
			result = dispatcher.runSync("processChangeIndentHelper", processChangeIndentHelperCtx);
			if (ServiceUtil.isError(result)) {
				String errMsg = ServiceUtil.getErrorMessage(result);
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
    
}
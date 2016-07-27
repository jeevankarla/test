package in.vasista.vbiz.depotsales;
import java.text.DateFormat;

import in.vasista.vbiz.depotsales.DepotPurchaseServices;

import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.accounting.util.UtilAccounting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.Calendar;
import java.util.TreeMap;

import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericDelegator;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.util.Iterator;

import org.ofbiz.entity.util.EntityListIterator;


import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.order.order.OrderReadHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

public class DepotHelperServices{

   public static final String module = DepotHelperServices.class.getName();
   private static int decimals;
   private static int rounding;
    public static final String resource = "AccountingUiLabels";
	public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
	public static final BigDecimal ONE_BASE = BigDecimal.ONE;
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
	public static int purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
	public static int purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
	
	public static int purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
    
    static {
        decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        //if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
    public static Map<String, Object> getRoBranchList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        Map<String, Object> result = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
       
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        String productStoreId = (String) context.get("productStoreId");
	    	List societyList = FastList.newInstance();

        // If productStoreId is not empty, fetch only courses related to the productStore
        List partyList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(productStoreId)){
        	List condList =FastList.newInstance();
   	    	condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, productStoreId));
   	    	
   	    	/*condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
   	    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimeStamp)));*/
   	    	EntityCondition prodStrFacCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
   	    	try{
   	    		partyList = delegator.findList("PartyRelationship", prodStrFacCondition, null, null, null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
   	    }
        result.put("partyList", partyList);
        return result;
    }
	public static Map<String, Object> getBranchSocietyPartyList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        Map<String, Object> result = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
       
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        String productStoreId = (String) context.get("productStoreId");
	    	List societyList = FastList.newInstance();

        // If productStoreId is not empty, fetch only courses related to the productStore
        List partyList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(productStoreId)){
        	List condList =FastList.newInstance();
   	    	condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, productStoreId));
   	    	
   	    	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
   	    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimeStamp)));
   	    	EntityCondition prodStrFacCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
   	    	try{
   	    		partyList = delegator.findList("PartyRelationship", prodStrFacCondition, null, null, null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
   	    }
        List branchSocietyList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(partyList)){
        	// Get all branch Society
        	List condList =FastList.newInstance();
   	    	condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN,EntityUtil.getFieldListFromEntityList(partyList, "partyIdTo", true)));
   	    	condList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
   	    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   	    			 EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN, nowTimeStamp)));
   	    	try{
   	    		societyList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
        	
        }
		result.put("societyList", societyList);
		result.put("partyList", partyList);

        return result;
    }
	public static Map<String, Object> getOrderTaxComponentBreakUp(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String taxType = (String) context.get("taxType");
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		BigDecimal taxRate = (BigDecimal) context.get("taxRate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		Map taxComponents = FastMap.newInstance();
		if(UtilValidate.isEmpty(effectiveDate)){
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		try{
			condList.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, taxType));
			condList.add(EntityCondition.makeCondition("taxRate", EntityOperator.EQUALS, taxRate.setScale(6)));
			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> taxRateComponent = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
			taxRateComponent = EntityUtil.filterByDate(taxRateComponent, effectiveDate);
			for(GenericValue eachTaxRate : taxRateComponent){
				String componentType = eachTaxRate.getString("componentType");
				BigDecimal componentRate = eachTaxRate.getBigDecimal("componentRate");
				taxComponents.put(componentType, componentRate);
			}
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("taxComponents", taxComponents);
		return result;
	}

	
	public static Map<String, Object> getOrderTaxRateForComponentRate(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String taxType = (String) context.get("taxType");
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		BigDecimal componentRate = (BigDecimal) context.get("componentRate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		Map taxComponents = FastMap.newInstance();
		if(UtilValidate.isEmpty(effectiveDate)){
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		BigDecimal taxRate = BigDecimal.ZERO;
		try{
			condList.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, taxType));
			condList.add(EntityCondition.makeCondition("componentRate", EntityOperator.EQUALS, componentRate.setScale(6)));
			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> taxRateComponent = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
			taxRateComponent = EntityUtil.filterByDate(taxRateComponent, effectiveDate);
			
			if(UtilValidate.isNotEmpty(taxRateComponent)){
				GenericValue taxRateValue = EntityUtil.getFirst(taxRateComponent);
				taxRate = taxRateValue.getBigDecimal("taxRate");
			}
			
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("taxRate", taxRate);
		return result;
	}
	
	public static Map<String, Object> getLastSupplyMaterialDetails(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String productId = (String) context.get("productId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		Map supplyDetailMap = FastMap.newInstance();
		Timestamp now = UtilDateTime.nowTimestamp();
		try{
			condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
			EntityFindOptions opts = new EntityFindOptions();
	        opts.setMaxRows(1);
	        opts.setFetchSize(1);
	        
	        List<String> orderBy = UtilMisc.toList("-datetimeReceived");

	        List<GenericValue> receipts = null;
	        try {
	        	receipts = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(condList, EntityOperator.AND), null, orderBy, opts, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	        String supplierPartyId = "";
        	BigDecimal supplyRate = BigDecimal.ZERO;
        	
        	condList.clear();
    		condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    		condList.add(EntityCondition.makeCondition("availableFromDate", EntityOperator.LESS_THAN_EQUAL_TO,now));
    		condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("availableThruDate", EntityOperator.EQUALS,null),EntityOperator.OR,EntityCondition.makeCondition("availableThruDate",EntityOperator.GREATER_THAN_EQUAL_TO,now)));
    		EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
    		List<GenericValue> supplierProducts = delegator.findList("SupplierProduct",cond,UtilMisc.toSet("partyId","lastPrice"),null,null,false);
    		
	        if(UtilValidate.isNotEmpty(receipts)){
	        	GenericValue receipt = EntityUtil.getFirst(receipts);
        		GenericValue supplierProduct = null;
        		if(UtilValidate.isNotEmpty(supplierProducts)){
        		 supplierProduct = EntityUtil.getFirst(supplierProducts);
        		 supplierPartyId = supplierProduct.getString("partyId");
        		 supplyRate = supplierProduct.getBigDecimal("lastPrice");
        		}
	        	supplyDetailMap.put("supplyProduct", receipt.getString("productId"));
	        	supplyDetailMap.put("supplyQty", receipt.getBigDecimal("quantityAccepted"));
	        	supplyDetailMap.put("supplyDate", receipt.getTimestamp("datetimeReceived"));
	        }else{
        		GenericValue supplierProduct = null;
        		if(UtilValidate.isNotEmpty(supplierProducts)){
        		 supplierProduct = EntityUtil.getFirst(supplierProducts);
        		 supplierPartyId = supplierProduct.getString("partyId");
        		 supplyRate = supplierProduct.getBigDecimal("lastPrice");
        		}
	        }
	        supplyDetailMap.put("supplierPartyId", supplierPartyId);
        	supplyDetailMap.put("supplyRate", supplyRate);	  
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("productSupplyDetails", supplyDetailMap);
		return result;
	}
	
	public static Map<String, Object> populateGRNLandingCostForPeriod(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		if(UtilValidate.isEmpty(thruDate)){
			thruDate = UtilDateTime.nowTimestamp();
		}
		try{
			condList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
			condList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			condList.add(EntityCondition.makeCondition("quantityAccepted", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
			condList.add(EntityCondition.makeCondition("orderId", EntityOperator.NOT_EQUAL, null));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue>	shipmentReceiptList = delegator.findList("ShipmentReceipt", cond, null,null, null,false);
			
			List<String> orderIds = EntityUtil.getFieldListFromEntityList(shipmentReceiptList, "orderId", true);
			Map resultMap = FastMap.newInstance();
			for(String orderId : orderIds){
				resultMap = getOrderItemAndTermsMapForCalculation(ctx, UtilMisc.toMap("userLogin", userLogin, "orderId", orderId));
				if (ServiceUtil.isError(resultMap)) {
	  		  		String errMsg =  ServiceUtil.getErrorMessage(resultMap);
	  		  		Debug.logError(errMsg , module);
	  		  		return ServiceUtil.returnError(errMsg);
	  		  	}
				List<Map> otherCharges = (List)resultMap.get("otherCharges");
				List<Map> productQty = (List)resultMap.get("productQty");
				Map resultCtx = getMaterialItemValuationDetails(ctx, UtilMisc.toMap("userLogin", userLogin, "productQty", productQty, "otherCharges", otherCharges, "incTax", ""));
				if(ServiceUtil.isError(resultCtx)){
	  		  		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
	  		  		Debug.logError(errMsg , module);
	  		  		return ServiceUtil.returnError(errMsg);
				}
				List<Map> itemDetails = (List)resultCtx.get("itemDetail");
				Map itemDetailRef = FastMap.newInstance();
				Map itemLandingCostMap = FastMap.newInstance();
				for(Map item : itemDetails){
					itemLandingCostMap.put((String)item.get("productId"), (BigDecimal)item.get("unitListPrice"));
					itemDetailRef.put((String)item.get("productId"), item);
				}
				Debug.log("landing cost Map : "+itemLandingCostMap);
				
				List<GenericValue> shipReceiptForOrder = EntityUtil.filterByCondition(shipmentReceiptList, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				List<String> shipmentIds = EntityUtil.getFieldListFromEntityList(shipReceiptForOrder, "shipmentId", true);
				
				List<GenericValue> shipmentAttr = delegator.findList("ShipmentAttribute", EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds), null, null, null, false);
				if(UtilValidate.isNotEmpty(shipmentAttr)){
					for(String shipId : shipmentIds){
						List<GenericValue> shipAttr = EntityUtil.filterByCondition(shipmentAttr, EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipId));
						List<GenericValue> receiptForShipment = EntityUtil.filterByCondition(shipReceiptForOrder, EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipId));
						if(UtilValidate.isNotEmpty(shipAttr)){
							List<Map> tempProdQty = FastList.newInstance();
							List<Map> tempOtherCharges = FastList.newInstance();
							for(GenericValue eachReceipt : receiptForShipment){
								Map tempMap = FastMap.newInstance();
								String prodId = eachReceipt.getString("productId");
								tempMap.put("productId", prodId);
								tempMap.put("quantity", eachReceipt.getBigDecimal("quantityAccepted"));
								tempMap.put("unitPrice", (BigDecimal)itemLandingCostMap.get(prodId));
								tempMap.put("bedPercent", BigDecimal.ZERO);
								tempMap.put("vatPercent", BigDecimal.ZERO);
								tempMap.put("cstPercent", BigDecimal.ZERO);
								tempProdQty.add(tempMap);
							}
							for(GenericValue eachAttr : shipAttr){
								Map tempMap = FastMap.newInstance();
								
								tempMap.put("otherTermId", eachAttr.getString("attrName"));
								tempMap.put("applicableTo", "ALL");
								tempMap.put("termValue", new BigDecimal(eachAttr.getString("attrValue")));
								tempMap.put("termDays", null);
								tempMap.put("uomId", "INR");
								tempMap.put("description", "");
								tempOtherCharges.add(tempMap);
							}
							resultCtx = getMaterialItemValuationDetails(ctx, UtilMisc.toMap("userLogin", userLogin, "productQty", tempProdQty, "otherCharges", tempOtherCharges, "incTax", ""));
							if(ServiceUtil.isError(resultCtx)){
				  		  		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				  		  		Debug.logError(errMsg , module);
				  		  		return ServiceUtil.returnError(errMsg);
							}
							List<Map> revisedItemDetails = (List)resultCtx.get("itemDetail");
							Map revisedItemLandingCostMap = FastMap.newInstance();
							for(Map revItem : revisedItemDetails){
								revisedItemLandingCostMap.put((String)revItem.get("productId"), (BigDecimal)revItem.get("unitListPrice"));
							}
							for(GenericValue eachReceipt : receiptForShipment){
								String inventoryItemId = eachReceipt.getString("inventoryItemId");
								String productId = eachReceipt.getString("productId");
								if(UtilValidate.isNotEmpty(revisedItemLandingCostMap.get(productId))){
									BigDecimal landingCost = (BigDecimal)revisedItemLandingCostMap.get(productId);
									GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
									inventoryItem.set("unitCost", landingCost);
									inventoryItem.store();
									
								}
							}
							
						}
						else{
							for(GenericValue eachReceipt : receiptForShipment){
								String inventoryItemId = eachReceipt.getString("inventoryItemId");
								String productId = eachReceipt.getString("productId");
								if(UtilValidate.isNotEmpty(itemLandingCostMap.get(productId))){
									BigDecimal landingCost = (BigDecimal)itemLandingCostMap.get(productId);
									GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
									inventoryItem.set("unitCost", landingCost);
									inventoryItem.store();
								}
							}
						}
					}
				}
				else{
					
					for(GenericValue shipReceipt : shipReceiptForOrder){
						String inventoryItemId = shipReceipt.getString("inventoryItemId");
						String productId = shipReceipt.getString("productId");
						if(UtilValidate.isNotEmpty(itemLandingCostMap.get(productId))){
							BigDecimal landingCost = (BigDecimal)itemLandingCostMap.get(productId);
							GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
							inventoryItem.set("unitCost", landingCost);
							inventoryItem.store();
						}
					}
				}
				
			}
				  
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}
	
	public static Map<String, Object> populateQuoteTotal(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		if(UtilValidate.isEmpty(thruDate)){
			thruDate = UtilDateTime.nowTimestamp();
		}
		try{
			condList.add(EntityCondition.makeCondition("issueDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
			condList.add(EntityCondition.makeCondition("issueDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue>	quotes = delegator.findList("Quote", cond, UtilMisc.toSet("quoteId"), null, null, false);
			List<String> quoteIds = EntityUtil.getFieldListFromEntityList(quotes, "quoteId", true);
			
			Map resultCtx = FastMap.newInstance();
			for(String quoteId : quoteIds){
				resultCtx = dispatcher.runSync("calculateQuoteGrandTotal", UtilMisc.toMap("userLogin", userLogin, "quoteId", quoteId));
				if (ServiceUtil.isError(resultCtx)) {
	  		  		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
	  		  		Debug.logError(errMsg , module);
	  		  		return ServiceUtil.returnError(errMsg);
	  		  	}
			}
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}
	
	public static Map<String,Object> getOrderItemAndTermsMapForCalculation(DispatchContext ctx, Map<String, ? extends Object> context){
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String)context.get("orderId");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        try{
        	
        	GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
        	
        	List<GenericValue> extOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
        	
        	List condExprList = FastList.newInstance();
        	condExprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
        	condExprList.add(EntityCondition.makeCondition("effectiveDatetime", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
        	EntityCondition cond = EntityCondition.makeCondition(condExprList, EntityOperator.AND);
        	List<GenericValue> orderItemChange = delegator.findList("OrderItemChange", cond, null, UtilMisc.toList("-effectiveDatetime"), null, false);
        	List<GenericValue> orderItems = FastList.newInstance();									
        	if(UtilValidate.isNotEmpty(orderItemChange)){
        		
        		for(GenericValue itemChange : orderItemChange){
        			List<GenericValue>  extOrdItm= EntityUtil.filterByCondition(extOrderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, itemChange.getString("orderItemSeqId")));
        			if(UtilValidate.isNotEmpty(extOrdItm)){
        				GenericValue ordItm = EntityUtil.getFirst(extOrdItm);
        				ordItm.set("quantity", itemChange.getBigDecimal("quantity"));
        				ordItm.set("unitPrice", itemChange.getBigDecimal("unitPrice"));
        				orderItems.add(ordItm);
        				
        			}
        		}
        	}
        	else{
        		orderItems.addAll(extOrderItems);
        	}
        	
        	List<GenericValue> otherTermTypes = delegator.findList("TermType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "OTHERS"), null, null, null, false);
        	List<String> otherTermTypeIds = EntityUtil.getFieldListFromEntityList(otherTermTypes, "termTypeId", true);
        	
        	List conditionList = FastList.newInstance();
        	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
        	conditionList.add(EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "BED_PUR"));
        	EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        	List<GenericValue> bedTaxOrderTerms = delegator.findList("OrderTerm", condExpr, null, null, null, false);
        	
        	conditionList.clear();
        	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
        	conditionList.add(EntityCondition.makeCondition("termTypeId", EntityOperator.IN, otherTermTypeIds));
        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        	List<GenericValue> otherChargesOrderTerms = delegator.findList("OrderTerm", condition, null, null, null, false);
        	
        	List<Map> otherCharges = FastList.newInstance();
        	List<Map> productQty = FastList.newInstance();
        	Map productItemRef = FastMap.newInstance();

        	for(GenericValue eachItem : orderItems){
        		Map tempMap = FastMap.newInstance();
        		String productId = eachItem.getString("productId");
        		tempMap.put("productId", productId);
        		tempMap.put("quantity", eachItem.getBigDecimal("quantity"));
        		tempMap.put("unitPrice", eachItem.getBigDecimal("unitPrice"));
        		tempMap.put("cstPercent", BigDecimal.ZERO);
        		tempMap.put("vatPercent", BigDecimal.ZERO);
        		tempMap.put("bedPercent", BigDecimal.ZERO);
        		if(UtilValidate.isNotEmpty(eachItem.get("cstPercent"))){
        			tempMap.put("cstPercent", eachItem.getBigDecimal("cstPercent"));
        		}
        		if(UtilValidate.isNotEmpty(eachItem.get("vatPercent"))){
        			tempMap.put("vatPercent", eachItem.getBigDecimal("vatPercent"));
        		}
        		if(UtilValidate.isNotEmpty(eachItem.get("bedPercent"))){
        			BigDecimal componentRate = (BigDecimal)eachItem.get("bedPercent");
        			BigDecimal bedPercentage = BigDecimal.ZERO;
        			if(componentRate.compareTo(BigDecimal.ZERO)>0){
        				String sequenceId = "";
        				List<GenericValue> orderSeqList = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        				if(UtilValidate.isNotEmpty(orderSeqList)){
        					sequenceId = (EntityUtil.getFirst(orderSeqList)).getString("orderItemSeqId");
        				}
        				List<GenericValue> bedTax = EntityUtil.filterByCondition(bedTaxOrderTerms, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, sequenceId));
        				if(UtilValidate.isNotEmpty(bedTax)){
        					bedPercentage = (EntityUtil.getFirst(bedTax)).getBigDecimal("termValue");
        				}
        				else{
        					bedTax = EntityUtil.filterByCondition(bedTaxOrderTerms, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, "_NA_"));
        					if(UtilValidate.isEmpty(bedTax)){
        						bedPercentage = (EntityUtil.getFirst(bedTaxOrderTerms)).getBigDecimal("termValue");
        					}
        					else{
        						bedPercentage = (EntityUtil.getFirst(bedTax)).getBigDecimal("termValue");
        					}
        				}
        			}
        			tempMap.put("bedPercent", bedPercentage);
        		}
        		productQty.add(tempMap);
        		productItemRef.put(eachItem.getString("productId"), tempMap);
        	}
        	
			for(GenericValue otherTerm : otherChargesOrderTerms){
				
				String applicableTo = "ALL";
				String sequenceId = otherTerm.getString("orderItemSeqId");
				if(UtilValidate.isNotEmpty(sequenceId) && !(sequenceId.equals("_NA_"))){
					List<GenericValue> orderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, sequenceId));
        			if(UtilValidate.isNotEmpty(orderItem)){
        				applicableTo = (EntityUtil.getFirst(orderItem)).getString("productId");
        			}
				}
        		Map tempMap = FastMap.newInstance();
        		tempMap.put("otherTermId", otherTerm.getString("termTypeId"));
        		tempMap.put("applicableTo", applicableTo);
        		tempMap.put("termValue", otherTerm.getBigDecimal("termValue"));
        		tempMap.put("uomId", otherTerm.getString("uomId"));
        		tempMap.put("termDays", null);
        		tempMap.put("description", "");
        		otherCharges.add(tempMap);
        	}
			result.put("otherCharges", otherCharges);
			result.put("productQty", productQty);
        }
        catch(Exception e){
        	Debug.logError("Error calculating order value for order : "+orderId, module);
		    return ServiceUtil.returnError("Error calculating order value for order : "+orderId);
        }
        return result;
	}
	
public static Map<String, Object> getMaterialStores(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List productFacilities=FastList.newInstance();
		List<GenericValue> storesList=FastList.newInstance();
		try{
			List<GenericValue> productFacilitiesList=delegator.findList("ProductFacility",null,null,null,null,false);
			if(UtilValidate.isNotEmpty(productFacilitiesList)){
				productFacilities=EntityUtil.getFieldListFromEntityList(productFacilitiesList, "facilityId", true);
			}
			if(UtilValidate.isNotEmpty(productFacilities)){
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, productFacilities));
				conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "STORE"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> facilities = delegator.findList("Facility", condition, null, null, null, false);
				if(UtilValidate.isNotEmpty(facilities)){
					storesList.addAll(facilities);
				}
				/*for(int i=0;i<productFacilities.size();i++){
					GenericValue Facility=delegator.findOne("Facility",UtilMisc.toMap("facilityId",productFacilities.get(i)),false);
					
				}*/
			}
			result.put("storesList",storesList);
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}	

	public static Map<String, Object> checkValidChangeOrNot(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		String statusId = (String) context.get("statusId");
		String statusIdTo = (String) context.get("statusIdTo");
		Map result = ServiceUtil.returnSuccess();
		try{
			GenericValue StatusValidChange= delegator.findOne("StatusValidChange",UtilMisc.toMap("statusId",statusId,"statusIdTo",statusIdTo),false);
			if(UtilValidate.isEmpty(StatusValidChange)){
				return ServiceUtil.returnError("This is not a Valid Change.");
			}
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}
	
	public static Map<String, Object> getMaterialItemValuationDetails(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		String incTax = (String) context.get("incTax");
		List<Map> productQty = (List) context.get("productQty");
		List<Map> otherCharges = (List) context.get("otherCharges");
		Map result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		BigDecimal grandTotal = BigDecimal.ZERO;
		List<Map> itemDetail = FastList.newInstance();
		List<Map> adjustmentDetail = FastList.newInstance();
		List<Map> termsDetail = FastList.newInstance();
		List condExpr = FastList.newInstance();
		Map productItemRef = FastMap.newInstance();
		List productItemDetails = FastList.newInstance();
		Map productAdjustmentPerUnit = FastMap.newInstance();
		try{
			
			if(UtilValidate.isNotEmpty(incTax)){
				Map tempMap = FastMap.newInstance();
				tempMap.put("termTypeId", "INC_TAX");
				tempMap.put("applicableTo", "_NA_");
				tempMap.put("termValue", null);
				tempMap.put("uomId", "");
				tempMap.put("termDays", null);
				tempMap.put("description", "");
				termsDetail.add(tempMap);
			}
			String productId = "";
			Map taxInputAmountMap = FastMap.newInstance();
			BigDecimal quantity = BigDecimal.ZERO;
			for (Map<String, Object> prodQtyMap : productQty) {
				
				Map productItemMap = FastMap.newInstance();
				BigDecimal unitPrice = BigDecimal.ZERO;
				BigDecimal totalTaxAmt =  BigDecimal.ZERO;
				BigDecimal bedInputAmount = null;
				BigDecimal vatInputAmount = null;
				BigDecimal cstInputAmount = null;
				if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
					productId = (String)prodQtyMap.get("productId");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
					quantity = (BigDecimal)prodQtyMap.get("quantity");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
					unitPrice = (BigDecimal)prodQtyMap.get("unitPrice");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
					bedInputAmount = (BigDecimal)prodQtyMap.get("bedAmount");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
					vatInputAmount = (BigDecimal)prodQtyMap.get("vatAmount");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
					cstInputAmount = (BigDecimal)prodQtyMap.get("cstAmount");
				}
				if(UtilValidate.isNotEmpty(bedInputAmount) || UtilValidate.isNotEmpty(vatInputAmount) || UtilValidate.isNotEmpty(cstInputAmount)){
					Map tempMap = FastMap.newInstance();
					tempMap.put("bedInputAmount", bedInputAmount);
					tempMap.put("vatInputAmount", vatInputAmount);
					tempMap.put("cstInputAmount", cstInputAmount);
					taxInputAmountMap.put(productId, tempMap);
				}
				// this is to calculate inclusive tax
				BigDecimal vatUnitRate = BigDecimal.ZERO;
				BigDecimal cstUnitRate = BigDecimal.ZERO;
				BigDecimal bedUnitRate = BigDecimal.ZERO;
				BigDecimal bedCessUnitRate  = BigDecimal.ZERO;
				BigDecimal bedSecCessUnitRate  = BigDecimal.ZERO;
				
				BigDecimal vatPercent =(BigDecimal)prodQtyMap.get("vatPercent");
				BigDecimal cstPercent =(BigDecimal)prodQtyMap.get("cstPercent");
				BigDecimal bedPercent =(BigDecimal)prodQtyMap.get("bedPercent");
				
				BigDecimal bedTaxPercent = BigDecimal.ZERO;
				BigDecimal bedcessTaxPercent = BigDecimal.ZERO;
				BigDecimal bedseccessTaxPercent = BigDecimal.ZERO;
				BigDecimal vatTaxPercent = BigDecimal.ZERO;
				BigDecimal cstTaxPercent = BigDecimal.ZERO;
				
				if(UtilValidate.isNotEmpty(bedPercent) && bedPercent.compareTo(BigDecimal.ZERO)>0){
					
					Map resultCtx = getOrderTaxComponentBreakUp(ctx, UtilMisc.toMap("userLogin", userLogin, "taxType", "EXCISE_DUTY_PUR", "taxRate", bedPercent));
					Map taxComponent = (Map)resultCtx.get("taxComponents");
					if(UtilValidate.isEmpty(taxComponent)){
						Debug.logError("Tax component configuration for Excise duty missing ", module);
						return ServiceUtil.returnError("Tax component configuration for Excise duty missing ");
					}
					
					if(UtilValidate.isNotEmpty(taxComponent.get("BED_PUR"))){
						bedTaxPercent = (BigDecimal)taxComponent.get("BED_PUR");
					}
					if(UtilValidate.isNotEmpty(taxComponent.get("BEDCESS_PUR"))){
						bedcessTaxPercent = (BigDecimal)taxComponent.get("BEDCESS_PUR");
					}
					if(UtilValidate.isNotEmpty(taxComponent.get("BEDSECCESS_PUR"))){
						bedseccessTaxPercent = (BigDecimal)taxComponent.get("BEDSECCESS_PUR");
					}
					
					Map termTempMap = FastMap.newInstance();
					termTempMap.put("applicableTo", productId);
					termTempMap.put("termTypeId", "BED_PUR");
					termTempMap.put("termValue", bedPercent);
					termTempMap.put("uomId", "PERCENT");
					termTempMap.put("termDays", null);
					termTempMap.put("description", "");
					termsDetail.add(termTempMap);
					
				}
				
				if(UtilValidate.isNotEmpty(vatPercent) && vatPercent.compareTo(BigDecimal.ZERO)>0){
					
					Map resultCtx = getOrderTaxComponentBreakUp(ctx, UtilMisc.toMap("userLogin", userLogin, "taxType", "VAT_PUR", "taxRate", vatPercent));
					Map taxComponent = (Map)resultCtx.get("taxComponents");
					if(UtilValidate.isEmpty(taxComponent)){
						Debug.logError("Tax component configuration for VAT missing ", module);
						return ServiceUtil.returnError("Tax component configuration for VAT missing ");
					}
					
					if(UtilValidate.isNotEmpty(taxComponent.get("VAT_PUR"))){
						vatTaxPercent = (BigDecimal)taxComponent.get("VAT_PUR");
					}
					
					Map termTempMap = FastMap.newInstance();
					termTempMap.put("applicableTo", productId);
					termTempMap.put("termTypeId", "VAT_PUR");
					termTempMap.put("termValue", vatPercent);
					termTempMap.put("uomId", "PERCENT");
					termTempMap.put("termDays", null);
					termTempMap.put("description", "");
					termsDetail.add(termTempMap);
					
				}
				
				if(UtilValidate.isNotEmpty(cstPercent) && cstPercent.compareTo(BigDecimal.ZERO)>0){
					
					Map resultCtx = getOrderTaxComponentBreakUp(ctx, UtilMisc.toMap("userLogin", userLogin, "taxType", "CST_PUR", "taxRate", cstPercent));
					Map taxComponent = (Map)resultCtx.get("taxComponents");
					if(UtilValidate.isEmpty(taxComponent)){
						Debug.logError("Tax component configuration for VAT missing ", module);
						return ServiceUtil.returnError("Tax component configuration for VAT missing ");
					}
					
					if(UtilValidate.isNotEmpty(taxComponent.get("CST_PUR"))){
						cstTaxPercent = (BigDecimal)taxComponent.get("CST_PUR");
					}
					
					Map termTempMap = FastMap.newInstance();
					termTempMap.put("applicableTo", productId);
					termTempMap.put("termTypeId", "CST_PUR");
					termTempMap.put("termValue", cstPercent);
					termTempMap.put("uomId", "PERCENT");
					termTempMap.put("termDays", null);
					termTempMap.put("description", "");
					termsDetail.add(termTempMap);
						
				}
				
				productItemMap.put("productId", productId);
				productItemMap.put("quantity", quantity);
				productItemMap.put("bedPercent", bedTaxPercent);
				productItemMap.put("bedcessPercent", bedcessTaxPercent);
				productItemMap.put("bedseccessPercent", bedseccessTaxPercent);
				productItemMap.put("vatPercent", vatTaxPercent);
				productItemMap.put("cstPercent", cstTaxPercent);
				
				BigDecimal basePriceAmt = (unitPrice.multiply(quantity)).setScale(purchaseTaxFinalDecimals, purchaseTaxRounding);
				
				BigDecimal exCstRate = BigDecimal.ZERO;
				BigDecimal exVatRate = BigDecimal.ZERO;
				BigDecimal exBedRate = BigDecimal.ZERO;
				BigDecimal exBedCessRate = BigDecimal.ZERO;
				BigDecimal exBedSecCessRate = BigDecimal.ZERO;
				BigDecimal unitListPrice = BigDecimal.ZERO;
				
				if(UtilValidate.isNotEmpty(incTax)){
					
					unitListPrice = unitPrice;
					if(UtilValidate.isNotEmpty(vatTaxPercent) && vatTaxPercent.compareTo(BigDecimal.ZERO)>0){
						Map<String,Object> exVatRateMap = UtilAccounting.getExclusiveTaxRate(basePriceAmt,vatTaxPercent);
						exVatRate = (BigDecimal)exVatRateMap.get("taxAmount");
						vatUnitRate = exVatRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exVatRate);
					}
					
					if(UtilValidate.isNotEmpty(cstTaxPercent) && cstTaxPercent.compareTo(BigDecimal.ZERO)>0){
						Map<String,Object> exCstRateMap = UtilAccounting.getExclusiveTaxRate(basePriceAmt,cstTaxPercent);
						exCstRate = (BigDecimal)exCstRateMap.get("taxAmount");
						cstUnitRate = exCstRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exCstRate);
					}
					
					if(UtilValidate.isNotEmpty(bedTaxPercent) && bedTaxPercent.compareTo(BigDecimal.ZERO)>0){
						
						Map<String,Object> exBedRateMap = UtilAccounting.getExclusiveTaxRate(basePriceAmt.subtract(exVatRate.add(exCstRate)),bedTaxPercent);
						exBedRate = (BigDecimal)exBedRateMap.get("taxAmount");
						bedUnitRate = exBedRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exBedRate);
						Map<String,Object> exBedCessRateMap = UtilAccounting.getExclusiveTaxRate(exBedRate,bedcessTaxPercent);
						exBedCessRate = (BigDecimal)exBedCessRateMap.get("taxAmount");
						bedCessUnitRate = exBedCessRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exBedCessRate);
						
						Map<String,Object> exBedSecCessRateMap = UtilAccounting.getExclusiveTaxRate(exBedRate,bedseccessTaxPercent);
						exBedSecCessRate = (BigDecimal)exBedSecCessRateMap.get("taxAmount");
						bedSecCessUnitRate = exBedSecCessRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exBedSecCessRate);
					}
				}
				else{
					
					if(UtilValidate.isNotEmpty(bedTaxPercent) && bedTaxPercent.compareTo(BigDecimal.ZERO)>0){
						
						Map<String,Object> exBedRateMap = UtilAccounting.getInclusiveTaxRate(basePriceAmt, bedTaxPercent);
						exBedRate = (BigDecimal)exBedRateMap.get("taxAmount");
						bedUnitRate = exBedRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exBedRate);
						
						Map<String,Object> exBedCessRateMap = UtilAccounting.getInclusiveTaxRate(exBedRate, bedcessTaxPercent);
						exBedCessRate = (BigDecimal)exBedCessRateMap.get("taxAmount");
						bedCessUnitRate = exBedCessRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exBedCessRate);
						
						Map<String,Object> exBedSecCessRateMap = UtilAccounting.getInclusiveTaxRate(exBedRate,bedseccessTaxPercent);
						exBedSecCessRate = (BigDecimal)exBedSecCessRateMap.get("taxAmount");
						bedSecCessUnitRate = exBedSecCessRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exBedSecCessRate);
						
					}
					
					BigDecimal baseValue = unitPrice.add((bedUnitRate.add(bedCessUnitRate)).add(bedSecCessUnitRate));
					BigDecimal baseValueAmt = basePriceAmt.add((exBedRate.add(exBedCessRate)).add(exBedSecCessRate));
					
					if(UtilValidate.isNotEmpty(vatTaxPercent) && vatTaxPercent.compareTo(BigDecimal.ZERO)>0){
						Map<String,Object> exVatRateMap = UtilAccounting.getInclusiveTaxRate(baseValueAmt,vatTaxPercent);
						exVatRate = (BigDecimal)exVatRateMap.get("taxAmount");
						vatUnitRate = exVatRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exVatRate);
					}
					
					if(UtilValidate.isNotEmpty(cstTaxPercent) && cstTaxPercent.compareTo(BigDecimal.ZERO)>0){
						Map<String,Object> exCstRateMap = UtilAccounting.getInclusiveTaxRate(baseValueAmt,cstTaxPercent);
						exCstRate = (BigDecimal)exCstRateMap.get("taxAmount");
						cstUnitRate = exCstRate.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						totalTaxAmt = totalTaxAmt.add(exCstRate);
					}
					
				}

				BigDecimal totalTaxUnitAmt = totalTaxAmt.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
				if(UtilValidate.isNotEmpty(incTax)){
					unitListPrice = unitPrice;
					unitPrice = unitPrice.subtract(totalTaxUnitAmt);
				}
				else{
					unitListPrice = unitPrice.add(totalTaxUnitAmt);
				}
				productItemMap.put("bedAmount", exBedRate);
				productItemMap.put("bedcessAmount", exBedCessRate);
				productItemMap.put("bedseccessAmount", exBedSecCessRate);
				productItemMap.put("vatAmount", exVatRate);
				productItemMap.put("cstAmount", exCstRate);
				productItemMap.put("bedUnitRate", bedUnitRate);
				productItemMap.put("bedcessUnitRate", bedCessUnitRate);
				productItemMap.put("bedseccessUnitRate", bedSecCessUnitRate);
				productItemMap.put("vatUnitRate", vatUnitRate);
				productItemMap.put("cstUnitRate", cstUnitRate);
				productItemMap.put("unitPrice", unitPrice);
				productItemMap.put("unitListPrice", unitListPrice);
				itemDetail.add(productItemMap);
				productItemRef.put(productId, productItemMap);
			}
			String otherTermId = "";
			String applicableTo = "";
			String uomId = "";
			String description = "";
			BigDecimal termDays = null;
			BigDecimal termValue = BigDecimal.ZERO;
			
			for (Map<String, Object> eachItem : otherCharges) {
				Map adjustmentItemMap = FastMap.newInstance();
				Map termItemMap = FastMap.newInstance();
				
				if(UtilValidate.isNotEmpty(eachItem.get("otherTermId"))){
					otherTermId = (String)eachItem.get("otherTermId");
				}
				
				if(UtilValidate.isNotEmpty(eachItem.get("applicableTo"))){
					applicableTo = (String)eachItem.get("applicableTo");
				}
				if(UtilValidate.isNotEmpty(eachItem.get("termValue"))){
					termValue = (BigDecimal)eachItem.get("termValue");
				}
				if(UtilValidate.isNotEmpty(eachItem.get("termDays"))){
					termDays = (BigDecimal)eachItem.get("termDays");
				}
				if(UtilValidate.isNotEmpty(eachItem.get("uomId"))){
					uomId = (String)eachItem.get("uomId");
				}
				if(UtilValidate.isNotEmpty(eachItem.get("description"))){
					description = (String)eachItem.get("description");
				}
				
				/* Adding terms*/
				termItemMap.put("termTypeId", otherTermId);
				termItemMap.put("applicableTo", applicableTo);
				termItemMap.put("termValue", termValue);
				termItemMap.put("termDays", termDays);
				termItemMap.put("uomId", uomId);
				termItemMap.put("description", description);
				termsDetail.add(termItemMap);
			}
			Map adjInputCtx = UtilMisc.toMap("productItems", productItemRef, "otherCharges", otherCharges, "inputTaxAmount", taxInputAmountMap, "userLogin", userLogin, "incTax", incTax);
			
			Map adjResult = getItemAdjustments(ctx, adjInputCtx);
			
			if (ServiceUtil.isError(adjResult)) {
  		  		String errMsg =  ServiceUtil.getErrorMessage(adjResult);
  		  		Debug.logError(errMsg , module);
  		  		return ServiceUtil.returnError(errMsg);
  		  	}
			Map revisedProdItems = (Map)adjResult.get("productItemsRevised");
			adjustmentDetail = (List)adjResult.get("adjustmentTerms");
			productAdjustmentPerUnit = (Map)adjResult.get("productAdjustmentPerUnit");
			Iterator tempIter = revisedProdItems.entrySet().iterator();
			while (tempIter.hasNext()) {
				Map.Entry tempEntry = (Entry) tempIter.next();
				Map eachItem = (Map) tempEntry.getValue();
				productItemDetails.add(eachItem);
				grandTotal = grandTotal.add(((BigDecimal)eachItem.get("unitListPrice")).multiply((BigDecimal)eachItem.get("quantity")));
			}
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		
		result.put("grandTotal", grandTotal);
		result.put("itemDetail", itemDetail);
		result.put("adjustmentDetail", adjustmentDetail);
		result.put("termsDetail", termsDetail);
		result.put("productAdjustmentPerUnit", productAdjustmentPerUnit);
		return result;
	}
	 
	public static Map<String, Object> getTermValuePerProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
	   	   
		Delegator delegator = ctx.getDelegator();
		Map productItems = (Map) context.get("productItems");
		String incTax = (String) context.get("incTax");
		Map inputTaxAmount = (Map) context.get("inputTaxAmount");
		List<Map> adjustmentTerms = (List) context.get("adjustmentTerms");
		Map result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		BigDecimal grandTotal = BigDecimal.ZERO;
		Map tempUpdateMap = FastMap.newInstance();
		Map productAdjPerUnit = FastMap.newInstance();
		Map adjValueMap = FastMap.newInstance();
		// iterate adjustment to calculate landing cost of the item and recalculate tax when there is term before tax
		for(Map eachAdj : adjustmentTerms){
			String applicableTo = (String)eachAdj.get("applicableTo");
			BigDecimal amount = (BigDecimal)eachAdj.get("amount");
			String termTypeId = (String)eachAdj.get("adjustmentTypeId");
			boolean recalculateVAT = Boolean.FALSE;
			boolean recalculateBEDAndVAT = Boolean.FALSE;
			if(termTypeId.equals("COGS_DISC") || termTypeId.equals("COGS_PCK_FWD")){
				recalculateVAT = Boolean.TRUE;
			}
			if(termTypeId.equals("COGS_DISC_BASIC")){
				recalculateBEDAndVAT = Boolean.TRUE;
			}
			BigDecimal poValue = BigDecimal.ZERO;
			
			Iterator prodPOIter = productItems.entrySet().iterator();
			// loop to get PO Value
			while (prodPOIter.hasNext()) {
				Map.Entry tempEntry = (Entry) prodPOIter.next();
				Map prodItemTemp = (Map) tempEntry.getValue();
				String productId = (String) tempEntry.getKey();
				BigDecimal unitListPriceAmt = ((BigDecimal)prodItemTemp.get("unitListPrice")).multiply((BigDecimal)prodItemTemp.get("quantity"));
				BigDecimal extTaxesAmt = ((BigDecimal)prodItemTemp.get("vatAmount")).add((BigDecimal)prodItemTemp.get("cstAmount"));
				if(recalculateVAT){
					unitListPriceAmt = unitListPriceAmt.subtract(extTaxesAmt);
				}
				if(recalculateBEDAndVAT){
					BigDecimal extBedAmt = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(prodItemTemp.get("bedAmount"))){
						extBedAmt = extBedAmt.add((BigDecimal)prodItemTemp.get("bedAmount"));
					}
					if(UtilValidate.isNotEmpty(prodItemTemp.get("bedcessAmount"))){
						extBedAmt = extBedAmt.add((BigDecimal)prodItemTemp.get("bedcessAmount"));
					}
					if(UtilValidate.isNotEmpty(prodItemTemp.get("bedseccessAmount"))){
						extBedAmt = extBedAmt.add((BigDecimal)prodItemTemp.get("bedseccessAmount"));
					}
					extTaxesAmt = extTaxesAmt.add(extBedAmt);
					unitListPriceAmt = unitListPriceAmt.subtract(extTaxesAmt);
				}
		    	poValue = poValue.add(unitListPriceAmt);
			}
			
			boolean perProdAdjFlag = Boolean.FALSE;
			Iterator prodIter = null;
			Map tempIterator = FastMap.newInstance();
			if(applicableTo.equals("_NA_")){
				prodIter = productItems.entrySet().iterator();
			}
			else{
				perProdAdjFlag = Boolean.TRUE;
				Map prodItem = (Map)productItems.get(applicableTo);
				String productId = (String) prodItem.get("productId");
				tempIterator.put(productId, prodItem);
				prodIter = tempIterator.entrySet().iterator();
			}
			//Apportioned for single item or entire order
			while (prodIter.hasNext()) {
				BigDecimal totalItemValue = BigDecimal.ZERO;
				Map.Entry tempEntry = (Entry) prodIter.next();
				Map prodItem = (Map) tempEntry.getValue();
				String productId = (String) tempEntry.getKey();
				BigDecimal quantity = (BigDecimal) prodItem.get("quantity"); 
				Map prodMap = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(tempUpdateMap.get(productId))){
					prodMap = (Map)tempUpdateMap.get(productId);
				}else{
					prodMap.putAll(prodItem);
				}
				BigDecimal recalcAdjPrice = BigDecimal.ZERO;
				BigDecimal unitListPrice = BigDecimal.ZERO;
				BigDecimal itemValue = BigDecimal.ZERO;
				
				BigDecimal bedItemTotal = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(prodMap.get("bedAmount"))){
					bedItemTotal = bedItemTotal.add((BigDecimal)prodMap.get("bedAmount"));
				}
				if(UtilValidate.isNotEmpty(prodMap.get("bedcessAmount"))){
					bedItemTotal = bedItemTotal.add((BigDecimal)prodMap.get("bedcessAmount"));
				}
				if(UtilValidate.isNotEmpty(prodMap.get("bedseccessAmount"))){
					bedItemTotal = bedItemTotal.add((BigDecimal)prodMap.get("bedseccessAmount"));
				}
				BigDecimal bedUnitAmt = bedItemTotal.divide((BigDecimal)prodMap.get("quantity"), purchaseTaxFinalDecimals, purchaseTaxRounding);
				BigDecimal vatUnitAmt = ((BigDecimal)prodMap.get("vatAmount")).divide((BigDecimal)prodMap.get("quantity"), purchaseTaxFinalDecimals, purchaseTaxRounding);
				BigDecimal cstUnitAmt = ((BigDecimal)prodMap.get("cstAmount")).divide((BigDecimal)prodMap.get("quantity"), purchaseTaxFinalDecimals, purchaseTaxRounding);
				itemValue = ((BigDecimal)prodMap.get("unitPrice")).multiply((BigDecimal)prodMap.get("quantity"));

				
				if(UtilValidate.isNotEmpty(prodMap.get("bedAmount"))){
					itemValue = itemValue.add((BigDecimal)prodMap.get("bedAmount"));
				}
				if(UtilValidate.isNotEmpty(prodMap.get("bedcessAmount"))){
					itemValue = itemValue.add((BigDecimal)prodMap.get("bedcessAmount"));
				}
				if(UtilValidate.isNotEmpty(prodMap.get("bedseccessAmount"))){
					itemValue = itemValue.add((BigDecimal)prodMap.get("bedseccessAmount"));
				}
				
				/*if(!recalculateBEDAndVAT){
					itemValue = itemValue.add(bedItemTotal);
				}*/
				
				if(recalculateVAT){
					//BigDecimal listAmt = ((BigDecimal)prodMap.get("unitPrice")).multiply((BigDecimal)prodMap.get("quantity"));
					unitListPrice = ((BigDecimal)prodMap.get("unitListPrice")).subtract(vatUnitAmt.add(cstUnitAmt));
				}
				else if(recalculateBEDAndVAT){
					unitListPrice = ((BigDecimal)prodMap.get("unitListPrice")).subtract(bedUnitAmt.add(vatUnitAmt.add(cstUnitAmt)));
				}
				else{
					
					if(UtilValidate.isNotEmpty(prodMap.get("vatAmount"))){
						itemValue = itemValue.add((BigDecimal)prodMap.get("vatAmount"));
					}
					if(UtilValidate.isNotEmpty(prodMap.get("cstAmount"))){
						itemValue = itemValue.add((BigDecimal)prodMap.get("cstAmount"));
					}
					unitListPrice = (BigDecimal)prodMap.get("unitListPrice");
				}
				recalcAdjPrice = (itemValue.multiply(amount)).divide(poValue, purchaseTaxFinalDecimals, purchaseTaxRounding);
				if(perProdAdjFlag){
					recalcAdjPrice = amount;
				}
				    
				totalItemValue = totalItemValue.add(recalcAdjPrice);
				BigDecimal adjUnitAmt = recalcAdjPrice.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
				BigDecimal uPrice = unitListPrice.add(adjUnitAmt);
				BigDecimal basicPrice = uPrice.multiply(quantity);
				BigDecimal bedPercent = (BigDecimal) prodMap.get("bedPercent");
				BigDecimal bedcessPercent = (BigDecimal) prodMap.get("bedcessPercent");
				BigDecimal bedseccessPercent = (BigDecimal) prodMap.get("bedseccessPercent");
				BigDecimal vatPercent = (BigDecimal) prodMap.get("vatPercent");
				BigDecimal cstPercent = (BigDecimal) prodMap.get("cstPercent");
				
				if(recalculateVAT || recalculateBEDAndVAT){
					
					BigDecimal totBedAmt = BigDecimal.ZERO;
					if(recalculateBEDAndVAT && UtilValidate.isNotEmpty(bedPercent) && bedPercent.compareTo(BigDecimal.ZERO)>0){

						BigDecimal bedReCalc = (basicPrice.multiply(bedPercent)).divide(new BigDecimal(100), purchaseTaxFinalDecimals, purchaseTaxRounding);
						BigDecimal bedUnitPrice = bedReCalc.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						uPrice = uPrice.add(bedUnitPrice);
						prodMap.put("bedAmount", bedReCalc);
						prodMap.put("unitListPrice", uPrice);
						totBedAmt = totBedAmt.add(bedReCalc);
						
						if(UtilValidate.isNotEmpty(bedcessPercent) && bedcessPercent.compareTo(BigDecimal.ZERO)>0){
							BigDecimal bedcessReCalc = (bedReCalc.multiply(bedcessPercent)).divide(new BigDecimal(100), purchaseTaxFinalDecimals, purchaseTaxRounding);
							BigDecimal bedcessUnitPrice = bedcessReCalc.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
							uPrice = uPrice.add(bedcessUnitPrice);
							prodMap.put("bedcessAmount", bedcessReCalc);
							prodMap.put("unitListPrice", uPrice);
							totBedAmt = totBedAmt.add(bedcessReCalc);
						}
						if(UtilValidate.isNotEmpty(bedseccessPercent) && bedseccessPercent.compareTo(BigDecimal.ZERO)>0){
							BigDecimal bedseccessReCalc = (bedReCalc.multiply(bedseccessPercent)).divide(new BigDecimal(100), purchaseTaxFinalDecimals, purchaseTaxRounding);
							BigDecimal bedseccessUnitPrice = bedseccessReCalc.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
							uPrice = uPrice.add(bedseccessUnitPrice);
							prodMap.put("bedseccessAmount", bedseccessReCalc);
							prodMap.put("unitListPrice", uPrice);
							totBedAmt = totBedAmt.add(bedseccessReCalc);
						}
					}
					
					basicPrice = basicPrice.add(totBedAmt);
					if(UtilValidate.isNotEmpty(vatPercent) && vatPercent.compareTo(BigDecimal.ZERO)>0){
						
						BigDecimal vatReCalc = (basicPrice.multiply(vatPercent)).divide(new BigDecimal(100), purchaseTaxFinalDecimals, purchaseTaxRounding);
						BigDecimal vatUnitPrice = vatReCalc.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						uPrice = uPrice.add(vatUnitPrice);
						prodMap.put("vatAmount", vatReCalc);
						prodMap.put("unitListPrice", uPrice);
					}
					if(UtilValidate.isNotEmpty(cstPercent) && cstPercent.compareTo(BigDecimal.ZERO)>0){
						
						BigDecimal cstReCalc = (basicPrice.multiply(cstPercent)).divide(new BigDecimal(100), purchaseTaxFinalDecimals, purchaseTaxRounding);
						BigDecimal cstUnitPrice = cstReCalc.divide(quantity, purchaseTaxFinalDecimals, purchaseTaxRounding);
						uPrice = uPrice.add(cstUnitPrice);
						prodMap.put("cstAmount", cstReCalc);
						prodMap.put("unitListPrice", uPrice);
					}
				}
				else{
					prodMap.put("unitListPrice", uPrice);
				}

				
				if(UtilValidate.isNotEmpty(adjValueMap.get(productId))){
					BigDecimal extAmt = (BigDecimal)adjValueMap.get(productId);
					adjValueMap.put(productId, extAmt.add(totalItemValue));
				}else{
					adjValueMap.put(productId, totalItemValue);
				}
				
				if(UtilValidate.isNotEmpty(productAdjPerUnit.get(productId))){
					Map extAdjUnitMap = (Map)productAdjPerUnit.get(productId);
					extAdjUnitMap.put(termTypeId, adjUnitAmt);
					productAdjPerUnit.put(productId, extAdjUnitMap);
				}else{
					Map tempMap = FastMap.newInstance();
					tempMap.put(termTypeId, adjUnitAmt);
					productAdjPerUnit.put(productId, tempMap);
				}
				
				tempUpdateMap.put(productId, prodMap);
			}
		}
		
		// adding rest of the items that doesn't have adjustments to the map
		Iterator prodItemIter = productItems.entrySet().iterator();
		while (prodItemIter.hasNext()) {
			Map.Entry tempProdEntry = (Entry) prodItemIter.next();
			String productId = (String) tempProdEntry.getKey();
			if(UtilValidate.isEmpty(tempUpdateMap.get(productId))){
				tempUpdateMap.put(productId, (Map)tempProdEntry.getValue());
			}
		}
		// adding rest of the items that doesnot have adjustments to the map
		Iterator prodTaxItemIter = tempUpdateMap.entrySet().iterator();
		while (prodTaxItemIter.hasNext()) {
			Map.Entry tempProdEntry = (Entry) prodTaxItemIter.next();
			String productId = (String) tempProdEntry.getKey();
			Map tempMap = (Map) tempProdEntry.getValue();
			if(UtilValidate.isNotEmpty(inputTaxAmount.get(productId))){
				Map inputTaxMap = (Map)inputTaxAmount.get(productId);
				if(UtilValidate.isNotEmpty(inputTaxMap.get("bedInputAmount"))){
					tempMap.put("bedAmount", (BigDecimal)inputTaxMap.get("bedInputAmount"));
				}
				if(UtilValidate.isNotEmpty(inputTaxMap.get("vatInputAmount"))){
					tempMap.put("vatAmount", (BigDecimal)inputTaxMap.get("vatInputAmount"));
				}
				if(UtilValidate.isNotEmpty(inputTaxMap.get("cstInputAmount"))){
					tempMap.put("cstAmount", (BigDecimal)inputTaxMap.get("cstInputAmount"));
				}
			}
			tempUpdateMap.put(productId, tempMap);
			
		}
		
		// calculating unitListPrice based on unitprice+tax+adjustment
		Map listPriceRevisedMap = FastMap.newInstance();
		Iterator itemIter = tempUpdateMap.entrySet().iterator();
		while (itemIter.hasNext()) {
			Map.Entry tempProdEntry = (Entry) itemIter.next();
			String productId = (String) tempProdEntry.getKey();
			Map itemDetail = (Map) tempProdEntry.getValue();
			if(UtilValidate.isNotEmpty(adjValueMap.get(productId))){
				BigDecimal itemAdjValue = (BigDecimal)adjValueMap.get(productId);
				BigDecimal qty = (BigDecimal)itemDetail.get("quantity");
				BigDecimal totalAmt = ((BigDecimal)itemDetail.get("unitPrice")).multiply(qty);
				
				if(UtilValidate.isNotEmpty(itemDetail.get("bedAmount"))){
					totalAmt = totalAmt.add((BigDecimal)itemDetail.get("bedAmount"));
				}
				if(UtilValidate.isNotEmpty(itemDetail.get("bedcessAmount"))){
					totalAmt = totalAmt.add((BigDecimal)itemDetail.get("bedcessAmount"));
				}
				if(UtilValidate.isNotEmpty(itemDetail.get("bedseccessAmount"))){
					totalAmt = totalAmt.add((BigDecimal)itemDetail.get("bedseccessAmount"));
				}
				if(UtilValidate.isNotEmpty(itemDetail.get("vatAmount"))){
					totalAmt = totalAmt.add((BigDecimal)itemDetail.get("vatAmount"));
				}
				if(UtilValidate.isNotEmpty(itemDetail.get("cstAmount"))){
					totalAmt = totalAmt.add((BigDecimal)itemDetail.get("cstAmount"));
				}
				totalAmt = totalAmt.add(itemAdjValue);
				itemDetail.put("unitListPrice", totalAmt.divide(qty, purchaseTaxFinalDecimals, purchaseTaxRounding));
			}
			listPriceRevisedMap.put(productId, itemDetail);
		}
		
		result.put("productItemsRevised", listPriceRevisedMap);
		result.put("productAdjustmentPerUnit", productAdjPerUnit);
		return result;
	   }
		
	   public static Map<String, Object> calculatePOTermValue(DispatchContext ctx, Map<String, ? extends Object> context) {
	   	   
		   BigDecimal termAmount = BigDecimal.ZERO;
	       String termTypeId = (String)context.get("termTypeId");
	       String applicableTo = (String)context.get("applicableTo");
	       String uomId = (String)context.get("uomId");
	       Map productItems = (Map) context.get("productItems");
	       BigDecimal termValue   = (BigDecimal)context.get("termValue");
	       Map result = ServiceUtil.returnSuccess();
	       Map adjustmentMap = FastMap.newInstance();
	       if(UtilValidate.isEmpty(termTypeId)){
	    	   return ServiceUtil.returnError("Term Type cannot be empty");
	       }
	       //this to handle non derived terms
	       termAmount = termValue;
	       
	       adjustmentMap.put("adjustmentTypeId", termTypeId);
	       
	       BigDecimal basicAmount = BigDecimal.ZERO;
	       BigDecimal exciseDuty = BigDecimal.ZERO;
	       BigDecimal poValue = BigDecimal.ZERO;
	       BigDecimal vatAmt = BigDecimal.ZERO;
	       BigDecimal cstAmt = BigDecimal.ZERO;
	       
	       if(applicableTo.equals("ALL")){
	    	   Iterator tempIter = productItems.entrySet().iterator();
			   while (tempIter.hasNext()) {
					Map.Entry tempEntry = (Entry) tempIter.next();
					Map prodItem = (Map) tempEntry.getValue();
					
					basicAmount = basicAmount.add(((BigDecimal)prodItem.get("unitPrice")).multiply((BigDecimal)prodItem.get("quantity")));
					exciseDuty = exciseDuty.add(((BigDecimal)prodItem.get("bedAmount")).add(((BigDecimal)prodItem.get("bedcessAmount")).add((BigDecimal)prodItem.get("bedseccessAmount"))));
					vatAmt = vatAmt.add((BigDecimal)prodItem.get("vatAmount"));
					cstAmt = cstAmt.add((BigDecimal)prodItem.get("cstAmount"));
				}
				poValue = basicAmount.add(exciseDuty.add(vatAmt.add(cstAmt)));
				adjustmentMap.put("applicableTo", "_NA_");
	       }
	       else{
	    	   
	    	   Map prodItem = (Map)productItems.get(applicableTo);
	    	   
	    	   basicAmount = ((BigDecimal)prodItem.get("unitPrice")).multiply((BigDecimal)prodItem.get("quantity"));
	    	   exciseDuty = ((BigDecimal)prodItem.get("bedAmount")).add(((BigDecimal)prodItem.get("bedcessAmount")).add((BigDecimal)prodItem.get("bedseccessAmount")));
	    	   vatAmt = (BigDecimal)prodItem.get("vatAmount");
	    	   cstAmt = (BigDecimal)prodItem.get("cstAmount");
	    	   adjustmentMap.put("applicableTo", (String)prodItem.get("productId"));
	       }
	       
	       poValue = basicAmount.add(exciseDuty.add(vatAmt.add(cstAmt)));
	       if(termTypeId.equals("COGS_DISC_BASIC")){
	    	   if(UtilValidate.isNotEmpty(uomId) && uomId.equals("PERCENT")){
	    		   termAmount = (basicAmount.multiply(termValue)).divide(new BigDecimal("100"), purchaseTaxFinalDecimals, purchaseTaxRounding);
	    		   termAmount = termAmount.negate();
	    	   }else{
	    		   termAmount = termValue.negate();
	    	   }
	       }
	       else if(termTypeId.equals("COGS_DISC")){
	    	   if(UtilValidate.isNotEmpty(uomId) && uomId.equals("PERCENT")){
	    		   termAmount = ((basicAmount.add(exciseDuty)).multiply(termValue)).divide(new BigDecimal("100"), purchaseTaxFinalDecimals, purchaseTaxRounding);
	    		   termAmount = termAmount.negate();
	    	   }else{
	    		   termAmount = termValue.negate();
	    	   }
	       }
	       //Discount  After Tax
    	   else if(termTypeId.equals("COGS_DISC_ATR")){
	    	   if(UtilValidate.isNotEmpty(uomId) && uomId.equals("PERCENT")){
	    		   termAmount = (poValue.multiply(termValue)).divide(new BigDecimal("100"),purchaseTaxFinalDecimals, purchaseTaxRounding);
	    		   termAmount = termAmount.negate();
	    	   }else{
	    		   termAmount = termValue.negate();
	    	   }
	       }
	       //Packing And Forwarding Charges Before Tax
	       
    	   else if(termTypeId.equals("COGS_PCK_FWD")){
	    	   if(UtilValidate.isNotEmpty(uomId) && uomId.equals("PERCENT")){
	    		   termAmount = ((basicAmount.add(exciseDuty)).multiply(termValue)).divide(new BigDecimal("100"),purchaseTaxFinalDecimals, purchaseTaxRounding);
	    	   }else{
	    		   termAmount = termValue;
	    	   }
	       }
	       //Packing And Forwarding Charges After Tax
    	   else if(termTypeId.equals("COGS_PCK_FWD_ATR")){
	    	   if(UtilValidate.isNotEmpty(uomId) && uomId.equals("PERCENT")){
	    		   termAmount = (poValue.multiply(termValue)).divide(new BigDecimal("100"),purchaseTaxFinalDecimals, purchaseTaxRounding);
	    	   }else{
	    		   termAmount = termValue;
	    	   }
	       }
    	   else if(termTypeId.equals("COGS_INSURANCE")){
	    	   if(UtilValidate.isNotEmpty(uomId) && uomId.equals("PERCENT")){
	    		   termAmount = ((basicAmount.add(exciseDuty)).multiply(termValue)).divide(new BigDecimal("100"),purchaseTaxFinalDecimals, purchaseTaxRounding);
	    	   }else{
	    		   termAmount = termValue;
	    	   }
	       }
    	   else{
    		   if(UtilValidate.isNotEmpty(uomId) && uomId.equals("PERCENT")){
	    		   termAmount = (poValue.multiply(termValue)).divide(new BigDecimal("100"),purchaseTaxFinalDecimals, purchaseTaxRounding);
	    	   }else{
	    		   termAmount = termValue;
	    	   }
    	   }

	       adjustmentMap.put("amount", termAmount);
	       result.put("termAmount", termAmount); 
	       result.put("adjustmentMap", adjustmentMap);
	       return result;
	   }
	
	public static Map<String, Object> getItemAdjustments(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map productItems = (Map) context.get("productItems");
		String incTax = (String) context.get("incTax");
		Map inputTaxAmount = (Map) context.get("inputTaxAmount");
		List<Map> otherCharges = (List) context.get("otherCharges");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List<Map> adjustmentTerms = FastList.newInstance();
		Map productItemsRevised = FastMap.newInstance();
		Map productAdjustmentPerUnit = FastMap.newInstance();
		try{
			
			for(Map eachItem : otherCharges){
				
				String adjustmentTypeId = (String)eachItem.get("otherTermId");
				String applicableTo = (String)eachItem.get("applicableTo");
				
				BigDecimal termValue = (BigDecimal)eachItem.get("termValue");
				String uomId = (String)eachItem.get("uomId");
				
				BigDecimal termAmount =BigDecimal.ZERO;
				Map inputMap = UtilMisc.toMap("userLogin",userLogin);
	    		inputMap.put("termTypeId", adjustmentTypeId);
	    		inputMap.put("uomId", uomId);
	    		inputMap.put("termValue", termValue);
	    		inputMap.put("applicableTo", applicableTo);
	    		inputMap.put("productItems", productItems);
				Map resultTerm = calculatePOTermValue(ctx,inputMap);
				if (ServiceUtil.isError(resultTerm)) {
	  		  		String errMsg =  ServiceUtil.getErrorMessage(resultTerm);
	  		  		Debug.logError(errMsg , module);
	  		  		return ServiceUtil.returnError(errMsg);
	  		  	}
				Map adjustmentMap = (Map)resultTerm.get("adjustmentMap");
				adjustmentTerms.add(adjustmentMap);
	    		
			}
			Map inputCtx = FastMap.newInstance();
			inputCtx.put("userLogin", userLogin);
			inputCtx.put("productItems", productItems);
			inputCtx.put("adjustmentTerms", adjustmentTerms);
			inputCtx.put("inputTaxAmount", inputTaxAmount);
			inputCtx.put("incTax", incTax);
    		Map resultCtx = getTermValuePerProduct(ctx,inputCtx);
    		if (ServiceUtil.isError(resultCtx)) {
  		  		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
  		  		Debug.logError(errMsg , module);
  		  		return ServiceUtil.returnError(errMsg);
  		  	}
    		productItemsRevised = (Map)resultCtx.get("productItemsRevised");
    		productAdjustmentPerUnit = (Map)resultCtx.get("productAdjustmentPerUnit");
    		BigDecimal vatAmount = BigDecimal.ZERO;
    		BigDecimal cstAmount = BigDecimal.ZERO;
    		BigDecimal bedAmount = BigDecimal.ZERO;
    		BigDecimal bedcessAmount = BigDecimal.ZERO;
    		BigDecimal bedseccessAmount = BigDecimal.ZERO;
    		
    		Iterator revisedItemsIter = productItemsRevised.entrySet().iterator();
			while (revisedItemsIter.hasNext()) {
				Map.Entry tempEntry = (Entry) revisedItemsIter.next();
				Map eachItem = (Map) tempEntry.getValue();
				vatAmount = vatAmount.add((BigDecimal)eachItem.get("vatAmount"));
				cstAmount = cstAmount.add((BigDecimal)eachItem.get("cstAmount"));
				bedAmount = bedAmount.add((BigDecimal)eachItem.get("bedAmount"));
				bedcessAmount = bedcessAmount.add((BigDecimal)eachItem.get("bedcessAmount"));
				bedseccessAmount = bedseccessAmount.add((BigDecimal)eachItem.get("bedseccessAmount"));
			}
			if(vatAmount.compareTo(BigDecimal.ZERO)>0){
				Map tempAdjMap = FastMap.newInstance();
				tempAdjMap.put("applicableTo", "_NA_");
				tempAdjMap.put("adjustmentTypeId", "VAT_PUR");
				tempAdjMap.put("amount", vatAmount);
				adjustmentTerms.add(tempAdjMap);
			}
			if(cstAmount.compareTo(BigDecimal.ZERO)>0){
				Map tempAdjMap = FastMap.newInstance();
				tempAdjMap.put("applicableTo", "_NA_");
				tempAdjMap.put("adjustmentTypeId", "CST_PUR");
				tempAdjMap.put("amount", cstAmount);
				adjustmentTerms.add(tempAdjMap);
			}
			if(bedAmount.compareTo(BigDecimal.ZERO)>0){
				Map tempAdjMap = FastMap.newInstance();
				tempAdjMap.put("applicableTo", "_NA_");
				tempAdjMap.put("adjustmentTypeId", "BED_PUR");
				tempAdjMap.put("amount", bedAmount);
				adjustmentTerms.add(tempAdjMap);
			}
			if(bedcessAmount.compareTo(BigDecimal.ZERO)>0){
				Map tempAdjMap = FastMap.newInstance();
				tempAdjMap.put("applicableTo", "_NA_");
				tempAdjMap.put("adjustmentTypeId", "BEDCESS_PUR");
				tempAdjMap.put("amount", bedcessAmount);
				adjustmentTerms.add(tempAdjMap);
			}
			if(bedseccessAmount.compareTo(BigDecimal.ZERO)>0){
				Map tempAdjMap = FastMap.newInstance();
				tempAdjMap.put("applicableTo", "_NA_");
				tempAdjMap.put("adjustmentTypeId", "BEDSECCESS_PUR");
				tempAdjMap.put("amount", bedseccessAmount);
				adjustmentTerms.add(tempAdjMap);
			}
			
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("productItemsRevised", productItemsRevised);
		result.put("adjustmentTerms", adjustmentTerms);
		result.put("productAdjustmentPerUnit", productAdjustmentPerUnit);
		return result;
	}
	
	
	public static Map getProductUOM(Delegator delegator, List productIds) {
		
		Map uomLabelMap = FastMap.newInstance();
		Map productUomMap = FastMap.newInstance();
		List<Map> productUOMList = FastList.newInstance();
		List conditionList = FastList.newInstance();
		Map outCtx = FastMap.newInstance();
        try {
        	
        	List<GenericValue> uomDetails = delegator.findList("Uom", null, null, null, null, false);
        	
        	conditionList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
        	if(UtilValidate.isNotEmpty(productIds)){
        		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
        	}
        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            EntityListIterator eli = delegator.find("Product", condition, null, null, null, null);
            GenericValue product = null;
            List uniqueUOMs = FastList.newInstance();
            String productId = "";
            String uomId = "";
            List<GenericValue> uomDetail = FastList.newInstance();
            while ((product = eli.next()) != null) {
            	uomId = product.getString("quantityUomId");
            	if(UtilValidate.isNotEmpty(uomId)){
            		uomDetail = EntityUtil.filterByCondition(uomDetails, EntityCondition.makeCondition("uomId", EntityOperator.EQUALS, uomId));
                	GenericValue uom = EntityUtil.getFirst(uomDetail);
                	String description = "";
                	String uomTypeId = "";
                	if(UtilValidate.isNotEmpty(uom) && UtilValidate.isNotEmpty(uom.get("description"))){
                		description = uom.getString("description");
                		uomTypeId = uom.getString("uomTypeId");
                	}
                	productUomMap.put(product.getString("productId"), uomId);
                	uomLabelMap.put(uomId, description);
                	
                	Map tempMap = FastMap.newInstance();
                	tempMap.put("productId", product.getString("productId"));
                	tempMap.put("quantityUomId", uomId);
                	tempMap.put("uomTypeId", uomTypeId);
                	tempMap.put("uomDescription", description);
                	productUOMList.add(tempMap);
            	}
            	
            }
            eli.close();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        outCtx.put("uomLabel", uomLabelMap);
        outCtx.put("productUom", productUomMap);
        outCtx.put("productUomDetail", productUOMList);
		return outCtx;
	}
  public static Map<String, Object> getMaterialProducts(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		
		Map result = ServiceUtil.returnSuccess();
		List<GenericValue> productList = FastList.newInstance();
		result.put("productList", productList);
		try{
			List<GenericValue> productCategoryList = delegator.findByAnd("ProductCategory", UtilMisc.toMap("productCategoryTypeId","RAW_MATERIAL"));
			List productCategoryIdsList = EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
			productList = ProductWorker.getProductsByCategoryList(delegator, productCategoryIdsList, null);
			List<String> prodIdsList = FastList.newInstance();
			List<GenericValue> uniqueProducts = FastList.newInstance();
			for(GenericValue product : productList){
				String productId = product.getString("productId");
				if(!prodIdsList.contains(productId)){
					prodIdsList.add(productId);
					uniqueProducts.add(product);
				}
			}
			
			result.put("productList", uniqueProducts);
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}
	
  //Get RecevidQty and RemainingQty for selected PO
  
  public static Map<String, Object> getBalanceAndReceiptQtyForPO(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		//String productId = (String) context.get("productId");
		String orderId = (String) context.get("orderId");
		String orderItemSeqId = (String) context.get("orderItemSeqId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		 Map<String, Object> receiptItemTotals = new TreeMap<String, Object>();
		 Map<String, Object> productTotals = new TreeMap<String, Object>();
		try{
			 EntityListIterator shipmentReceiptItr = null;
			 if(orderId!=null){
				 
			 if(UtilValidate.isNotEmpty(fromDate) &&UtilValidate.isNotEmpty(thruDate))
					condList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.BETWEEN, UtilMisc.toList(fromDate,thruDate)));
		     condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		     if(UtilValidate.isNotEmpty(orderItemSeqId)){
			     condList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
		     }
			 EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			 Set fieldsToSelect = UtilMisc.toSet("receiptId","facilityId","datetimeReceived" ,"quantityAccepted","unitCost");
			 fieldsToSelect.add("orderId");
			 fieldsToSelect.add("orderItemSeqId");
			 fieldsToSelect.add("productId");
			 shipmentReceiptItr = delegator.find("ShipmentReceiptAndItem", cond, null,fieldsToSelect, null,null);
			 
			 GenericValue receiptItem;
			 while( shipmentReceiptItr != null && (receiptItem = shipmentReceiptItr.next()) != null) {
				    Map tempMap = FastMap.newInstance();
		            String receiptId = receiptItem.getString("receiptId");
		            String tmpProductId = receiptItem.getString("productId");
		            BigDecimal quantity  = receiptItem.getBigDecimal("quantityAccepted");
		            BigDecimal price  = receiptItem.getBigDecimal("unitCost");
		            BigDecimal amount = price.multiply(quantity);
		         // Handle product totals   			
	    			if (receiptItemTotals.get(tmpProductId) == null) {
	    				Map<String, Object> newMap = FastMap.newInstance();
	    				newMap.put("receivedQty", quantity);
	    				newMap.put("receivedQtyValue", amount);
	    				receiptItemTotals.put(tmpProductId, newMap);
	    			}else {
	    				Map productMap = (Map)receiptItemTotals.get(tmpProductId);
	    				BigDecimal runningQuantity = (BigDecimal)productMap.get("receivedQty");
	    				runningQuantity = runningQuantity.add(quantity);
	    				productMap.put("receivedQty", runningQuantity);
	    				BigDecimal runningTotalAmount = (BigDecimal)productMap.get("receivedQtyValue");
	    				runningTotalAmount = runningTotalAmount.add(amount);
	    				productMap.put("receivedQtyValue", runningTotalAmount);
	    				receiptItemTotals.put(tmpProductId, productMap);
	    			}
			  }    
			  shipmentReceiptItr.close();
			  List<GenericValue> orderItemsList = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId",orderId));
			  for(GenericValue orderItem:orderItemsList) {
				  String productId=orderItem.getString("productId");
				 
					  BigDecimal orderdQty = (BigDecimal)orderItem.getBigDecimal("quantity");
					  BigDecimal checkQty = (orderdQty.multiply(new BigDecimal(1.1))).setScale(0, BigDecimal.ROUND_CEILING);
					  BigDecimal receivedQty=BigDecimal.ZERO;
					  BigDecimal receivedQtyValue=BigDecimal.ZERO;
					  Map productMap=FastMap.newInstance();
					  if(UtilValidate.isNotEmpty(receiptItemTotals)){
						  Map productInnerMap= (Map)receiptItemTotals.get(productId);
						  if(productInnerMap!=null){
							  receivedQty=(BigDecimal)productInnerMap.get("receivedQty");
							  receivedQtyValue=(BigDecimal)productInnerMap.get("receivedQtyValue");
						  }
					  }
					 
					  BigDecimal toBeReceived=orderdQty.subtract(receivedQty);
					  productMap.put("receivedQty",receivedQty);
					  productMap.put("receivedQtyValue",receivedQtyValue);
					  productMap.put("orderedQty",orderdQty);
					  productMap.put("toBeReceivedQty",toBeReceived);
					  productMap.put("maxReceivedQty",checkQty);
					  productTotals.put(productId,productMap);
				  }
			  }
			  
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		 result.put("productTotals",productTotals);
		 result.put("receiptItemTotals", receiptItemTotals);  
		return result;
	}
  	
  public static Map<String, Object> getDivisionDepartments(DispatchContext dctx, Map<String, ? extends Object> context) {
  	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
      GenericValue userLogin = (GenericValue) context.get("userLogin");
      Timestamp fromDate =  (Timestamp)context.get("fromDate");
      Timestamp thruDate = (Timestamp)context.get("thruDate");
      String partyIdFrom = (String) context.get("partyIdFrom");
      String partyStatusId = (String) context.get("partyStatusId"); 
      List partyRelationshipAndDetailList = FastList.newInstance();
      List partyIds = FastList.newInstance();
      Security security = dctx.getSecurity();
      Map result = ServiceUtil.returnSuccess();
      try {
    	  List conditionList = FastList.newInstance();
    	  conditionList.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PARTY_GROUP"));
    	  conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
    	  if(UtilValidate.isNotEmpty(partyIdFrom)){
    		  conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
    	  }
    	  conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "DIVISION"));
    	  conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "SUB_DIVISION"));
    	  conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));	
    	  if(UtilValidate.isNotEmpty(thruDate)){
    		  conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
    	  }
    	  if(UtilValidate.isNotEmpty(partyStatusId)){
     		  conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyStatusId", EntityOperator.EQUALS, null),
   					EntityOperator.OR, EntityCondition.makeCondition("partyStatusId", EntityOperator.EQUALS, partyStatusId)));
           }
    	  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
    	  partyRelationshipAndDetailList = delegator.findList("PartyRelationshipAndDetail", condition, null, null, null, false);
    	  if(UtilValidate.isNotEmpty(partyRelationshipAndDetailList)){
    		   partyIds = EntityUtil.getFieldListFromEntityList(partyRelationshipAndDetailList, "partyId", true);
    	  }
      }catch(GenericEntityException e){
		Debug.logError("Error fetching employments " + e.getMessage(), module);
      }
  		result.put("subDivisionDepartmentList", partyRelationshipAndDetailList);
  		result.put("subDivisionPartyIds", partyIds);
  		return result;
  } 
  public static  Map<String, Object> getUnitPriceAndQuantity(DispatchContext dctx, Map context) {
	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
	 	Date date =  (Date)context.get("date");
	 	Timestamp dateTime = (Timestamp)context.get("dateTime");
	 	String custRequestId = (String)context.get("custRequestId");
	 	String custRequestItemSeqId = (String)context.get("custRequestItemSeqId");
        Delegator delegator = dctx.getDelegator();
        Timestamp issuedDateTime=null;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalUnitPrice = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(dateTime)){
        	issuedDateTime = UtilDateTime.getDayEnd(dateTime);
        }else if(UtilValidate.isNotEmpty(date)){
        	issuedDateTime = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(date));
        }else{
        	issuedDateTime = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
        }
      Map result = ServiceUtil.returnSuccess();
      try {
    	  List conditionList = FastList.newInstance();
    	  conditionList.add(EntityCondition.makeCondition("custRequestItemSeqId",EntityOperator.EQUALS,custRequestItemSeqId));
    	  conditionList.add(EntityCondition.makeCondition("issuedDateTime",EntityOperator.LESS_THAN_EQUAL_TO,issuedDateTime));
    	  conditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId));
    	  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
    	  List<GenericValue> itemIssuances=delegator.findList("ItemIssuance",condition,null,null,null,false);
			if(UtilValidate.isNotEmpty(itemIssuances)){
				for(GenericValue issuedItem:itemIssuances){
					BigDecimal quantity=BigDecimal.ZERO;
					BigDecimal unitCost=BigDecimal.ZERO;
					GenericValue inventoryItem = delegator.findOne("InventoryItem",UtilMisc.toMap("inventoryItemId",issuedItem.get("inventoryItemId")),false);
					if(UtilValidate.isNotEmpty(inventoryItem.get("unitCost"))){
						unitCost = (BigDecimal)inventoryItem.get("unitCost");
					}
					totalUnitPrice = totalUnitPrice.add(unitCost);
					quantity = quantity.add(issuedItem.getBigDecimal("quantity"));
					if(UtilValidate.isNotEmpty(issuedItem.getBigDecimal("cancelQuantity"))){
						quantity = quantity.subtract(issuedItem.getBigDecimal("cancelQuantity"));
					}
					totalValue = totalValue.add((unitCost).multiply(quantity));
					totalQty = totalQty.add(quantity);
				}
				totalUnitPrice = totalUnitPrice.divide(new BigDecimal(itemIssuances.size()),purchaseTaxFinalDecimals,purchaseTaxRounding);
			}
			result.put("totalUnitPrice",totalUnitPrice);
			result.put("totalQty",totalQty);
			result.put("totalValue",totalValue);
      }
      catch (GenericEntityException e) {
          Debug.logError(e, "Error While getting the Quantity and UnitPrice.!");
      }        
      return result;
}
  
  public static Map<String, Object> getDepartmentByUserLogin(DispatchContext dctx, Map context) {
	  Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
	  Map resultMap = ServiceUtil.returnSuccess();
	  GenericValue userLogin = (GenericValue)context.get("userLogin");
	  String partyId = (String)userLogin.get("partyId") ;
	  String roleTypeIdTo = (String)context.get("roleTypeIdTo");
	  if(UtilValidate.isEmpty(partyId)){
		  resultMap = ServiceUtil.returnError("No departyment is associated with this user");
	  return resultMap;
	  }
	  List<GenericValue> departmentDetails = FastList.newInstance();
	  GenericValue activeDepartmentParty = null;
	  try{
		  List conditionList = FastList.newInstance();
		  conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS, partyId)));
		  conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS,roleTypeIdTo)));
		  EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		  departmentDetails = delegator.findList("PartyRelationship",condition, null, null, null,false);
		  List<GenericValue> activeFacilityParties = (List<GenericValue>)EntityUtil.filterByDate(departmentDetails,UtilDateTime.nowTimestamp()); 
		  List<String> departmentsList = EntityUtil.getFieldListFromEntityList(activeFacilityParties, "partyIdFrom", true); 
		  /*activeDepartmentParty = EntityUtil.getFirst((List<GenericValue>)EntityUtil.filterByDate(departmentDetails,UtilDateTime.nowTimestamp()));
		  if(UtilValidate.isEmpty(activeFacilityParties)){
			  resultMap = ServiceUtil.returnError("No department is associated with this user ::"+userLogin.get("loginId"));
		  return resultMap;
		  }*/
		  resultMap.put("deptId", departmentsList);
	  
	  	}catch(GenericEntityException e){
		  Debug.logError("Error while getting department associated with this user"+e.getMessage(), module);
		  resultMap = ServiceUtil.returnError("Error while getting department associated with this user ::"+userLogin.get("partyId"));
		  return resultMap;
	  	}
	  return resultMap;
	  
	 }
  
  
  	public static Map<String, Object> updateOrderStatusToComplete(DispatchContext dctx, Map<String, ? extends Object> context) {
  	
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();   
		Map resultMap = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		List<GenericValue> purchaseOrderList = FastList.newInstance();
		List purchaseOrdersList = FastList.newInstance();
		List condList =FastList.newInstance();
		condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
		condList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
	    try{
	    	purchaseOrderList = delegator.findList("OrderHeader", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("orderId"), null, null, false);
	    	purchaseOrdersList = EntityUtil.getFieldListFromEntityList(purchaseOrderList, "orderId", true);
	    }catch (GenericEntityException e) {
			// TODO: handle exception
	    	Debug.logError(e, module);
		}
	    
	    List salesOrderIdsList = FastList.newInstance();
	    condList.clear();
		condList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, purchaseOrdersList));
		condList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
	    try{
	    	List orderAssocList = delegator.findList("OrderAssoc", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("toOrderId"), null, null, false);
	    	salesOrderIdsList = EntityUtil.getFieldListFromEntityList(orderAssocList, "toOrderId", true);
	    }catch (GenericEntityException e) {
			// TODO: handle exception
	    	Debug.logError(e, module);
		}
	    
	    List approvedSalesOrderIdsList = FastList.newInstance();
		List salesOrdersList = FastList.newInstance();
		condList.clear();
		condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
		condList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, salesOrderIdsList));
	    try{
	    	salesOrdersList = delegator.findList("OrderHeader", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("orderId", "statusId"), null, null, false);
	    	approvedSalesOrderIdsList = EntityUtil.getFieldListFromEntityList(salesOrdersList, "orderId", true);
	    }catch (GenericEntityException e) {
			// TODO: handle exception
	    	Debug.logError(e, module);
		}
	    
	    for(int i=0; i<salesOrderIdsList.size(); i++){
	    	String orderId = (String) salesOrderIdsList.get(i);
	    	if(!approvedSalesOrderIdsList.contains(orderId)){
	    		continue;
	    	}
	    	
	    	Map<String, Object> serviceApprResult = null;
	        try {
	        	serviceApprResult = dispatcher.runSync("changeOrderStatus", UtilMisc.toMap("orderId", orderId, "statusId", "ORDER_COMPLETED", "userLogin", userLogin));
	        } catch (GenericServiceException e) {
	            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        if (ServiceUtil.isError(serviceApprResult)) {
	            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceApprResult));
	        }
	    }
	    return resultMap;
  	}


    
    
}
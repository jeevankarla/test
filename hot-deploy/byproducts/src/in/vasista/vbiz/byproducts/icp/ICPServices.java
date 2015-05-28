package in.vasista.vbiz.byproducts.icp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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

import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
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

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
public class ICPServices {

    public static final String module = ICPServices.class.getName();

	    
    public static Map<String, Object> getIceCreamFactoryStore(Delegator delegator){
        
    	Map<String, Object> result = FastMap.newInstance(); 
    	String productStoreGroupId = "ICECREAM_PRODUCTS";
        List<GenericValue> byProdStores =FastList.newInstance();
            try{
           	if(UtilValidate.isNotEmpty(productStoreGroupId)){
           		byProdStores = delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(EntityOperator.AND, "productStoreGroupId", productStoreGroupId), null, null, null, false);
            }
         }catch (GenericEntityException e) {
            	Debug.logError(e, module);
            }
         for (GenericValue byProdStore : byProdStores) {
        	 GenericValue productStore = null;
     		try {
     			productStore = byProdStore.getRelatedOne("ProductStore");
     		} catch (GenericEntityException e) {
     			Debug.logError(e, module); 
     		}
     		if(UtilValidate.isNotEmpty(productStore.getString("isFactoryStore")) && (productStore.getString("isFactoryStore").equals("Y")  )){
     			String productStoreId = productStore.getString("productStoreId");
     			result.put("factoryStore", productStore);
     			result.put("factoryStoreId", productStoreId);
     			continue;
     		}
         }
         
    	return result;
	}
    
    public static Map<String, Object> getProductStoreByGroupId(Delegator delegator, String productStoreGroupId){
        
    	Map<String, Object> result = FastMap.newInstance(); 
        List<GenericValue> byProdStores =FastList.newInstance();
        if(UtilValidate.isEmpty(productStoreGroupId)){
        	productStoreGroupId = "BYPRODUCTS";
        }
        try{
           	if(UtilValidate.isNotEmpty(productStoreGroupId)){
           		byProdStores = delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(EntityOperator.AND, "productStoreGroupId", productStoreGroupId), null, null, null, false);
            }
        }catch (GenericEntityException e) {
            	Debug.logError(e, module);
        }
        for (GenericValue byProdStore : byProdStores) {
        	GenericValue productStore = null;
     		try {
     			productStore = byProdStore.getRelatedOne("ProductStore");
     		} catch (GenericEntityException e) {
     			Debug.logError(e, module); 
     		}
     		if(UtilValidate.isNotEmpty(productStore.getString("isFactoryStore")) && (productStore.getString("isFactoryStore").equals("Y")  )){
     			String productStoreId = productStore.getString("productStoreId");
     			result.put("factoryStore", productStore);
     			result.put("factoryStoreId", productStoreId);
     			continue;
     		}
         }
    	return result;
	}
    
public static Map<String, Object> approveICPOrder(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String salesChannelEnumId = (String) context.get("salesChannelEnumId");
		String orderId = (String) context.get("orderId");
        boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
        result.put("salesChannelEnumId", salesChannelEnumId);
        return result;
	}
	
	public static Map<String, Object> cancelICPOrder(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");
		String salesChannelEnumId = (String) context.get("salesChannelEnumId");	
		try{
			if(UtilValidate.isNotEmpty(orderId)){
				result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", UtilMisc.toList(orderId),"userLogin", userLogin));
				if (ServiceUtil.isError(result)) {
					Debug.logError("Problem cancelling orders in Correction", module);	 		  		  
			 		return ServiceUtil.returnError("Problem cancelling orders in Correction");
				} 
			}
			  			
		}catch (GenericServiceException e) {
			  Debug.logError(e, e.toString(), module);
			  return ServiceUtil.returnError("Problem cancelling order");
		}
		result.put("salesChannelEnumId", salesChannelEnumId);
		return result;
	}
	
	public static Map<String, Object> processICPSaleOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    List<Map> productQtyList = (List) context.get("productQtyList");
	    Timestamp supplyDate = (Timestamp) context.get("supplyDate");
	    Locale locale = (Locale) context.get("locale");
	    String productStoreId = (String) context.get("productStoreId");
	  	String salesChannel = (String) context.get("salesChannel");
	  	List productIds = (List) context.get("productIds");
	  	String orderTaxType = (String) context.get("orderTaxType");
	  	String partyId = (String) context.get("partyId");
		String billToCustomer = (String) context.get("billToCustomer");
	  	String orderId = (String) context.get("orderId");
	  	String PONumber = (String) context.get("PONumber");
	  	String promotionAdjAmt = (String) context.get("promotionAdjAmt");
	  	String orderMessage = (String) context.get("orderMessage");
	  	String disableAcctgFlag = (String) context.get("disableAcctgFlag");
	  	List<Map> orderAdjChargesList = (List) context.get("orderAdjChargesList");
	  	String currencyUomId = "INR";
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		boolean isSale = Boolean.TRUE;
		boolean batchNumExists = Boolean.FALSE;
		boolean daysToStoreExists = Boolean.FALSE;
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create order without partyId: "+ partyId, module);
			return ServiceUtil.returnError("partyId is empty");
		}
		List conditionList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(orderId)){
			
			boolean indentNotChanged = true; 
			Map resultCtx = ByProductNetworkServices.getOrderDetails(dctx, UtilMisc.toMap("userLogin", userLogin, "orderId", orderId));
			Map orderDetails = (Map)resultCtx.get("orderDetails");
			List<GenericValue> extOrderItems = (List)orderDetails.get("orderItems");
			
			List<Map> prevQtyList = FastList.newInstance();
			for(GenericValue extItem : extOrderItems){
				Map prevQtyMap = FastMap.newInstance();
				prevQtyMap.put("productId", extItem.getString("productId"));
				prevQtyMap.put("quantity", extItem.getBigDecimal("quantity"));
				prevQtyList.add(prevQtyMap);
			}
			if((UtilValidate.isNotEmpty(prevQtyList) &&  !prevQtyList.equals(productQtyList))){
				indentNotChanged = false;
 	        }
			
			try{
				if(!indentNotChanged){
					result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", UtilMisc.toList(orderId),"userLogin", userLogin));
					if (ServiceUtil.isError(result)) {
						Debug.logError("Problem cancelling orders in Correction", module);	 		  		  
				 		return ServiceUtil.returnError("Problem cancelling orders in Correction");
					} 
				}
				else{
					List condList = FastList.newInstance();
					
					for(Map prodBatch : productQtyList){
						String prod = (String)prodBatch.get("productId");
						String batchNum = null;
						if(UtilValidate.isNotEmpty(prodBatch.get("batchNo"))){
							batchNum = (String)prodBatch.get("batchNo");
						}
						List<GenericValue> orderItem = EntityUtil.filterByCondition(extOrderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prod));
						if(UtilValidate.isNotEmpty(orderItem) && batchNum != null){
							String orderItemSeqId = (EntityUtil.getFirst(orderItem)).getString("orderItemSeqId");
							
							GenericValue orderItemBatch = delegator.makeValue("OrderItemAttribute");
							orderItemBatch.set("orderId", orderId);
							orderItemBatch.set("orderItemSeqId", orderItemSeqId);
							orderItemBatch.set("attrName", "batchNumber");
							orderItemBatch.set("attrValue", batchNum);
							delegator.createOrStore(orderItemBatch);
							
						}
						
					}
					
				}
				  			
			}catch (GenericServiceException e) {
				  Debug.logError(e, e.toString(), module);
				  return ServiceUtil.returnError("Problem cancelling order");
			}
			catch (GenericEntityException e1) {
				  Debug.logError(e1, e1.toString(), module);
				  return ServiceUtil.returnError("Failed fetching existing order details");
			}
		}
		GenericValue product =null;
		String productPriceTypeId = null;
		String geoTax = "";
		if(UtilValidate.isNotEmpty(orderTaxType)){
			if(orderTaxType.equals("INTER")){
				geoTax = "CST";
			}else{
				geoTax = "VAT";
			}
		}
		
		BigDecimal promoAmt = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(promotionAdjAmt)){
			promoAmt = new BigDecimal(promotionAdjAmt);
		}
		
		
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
		
		try {
			//get inventoryFacility details through productStore.
			String  inventoryFacilityId="";
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if(UtilValidate.isNotEmpty(productStore)){
			inventoryFacilityId=productStore.getString("inventoryFacilityId");
			}
			cart.setOrderType("SALES_ORDER");
			cart.setIsEnableAcctg("Y");
			if("Y".equals(disableAcctgFlag)){
				cart.setIsEnableAcctg("N");
			}
	        cart.setExternalId(PONumber);
	        cart.setProductStoreId(productStoreId);
			cart.setChannelType(salesChannel);
			//cart.setBillToCustomerPartyId("GCMMF");
			cart.setBillToCustomerPartyId(partyId);
			cart.setFacilityId(inventoryFacilityId);//for store inventory we need this so that inventoryItem query by this orginFacilityId
			if(UtilValidate.isNotEmpty(billToCustomer)){
				cart.setBillToCustomerPartyId(billToCustomer);
			}
			cart.setPlacingCustomerPartyId(partyId);
			cart.setShipToCustomerPartyId(partyId);
			cart.setEndUserCustomerPartyId(partyId);
			//cart.setShipmentId(shipmentId);
			cart.setEstimatedDeliveryDate(effectiveDate);
			cart.setOrderDate(effectiveDate);
			cart.setUserLogin(userLogin, dispatcher);
			//cart.setOrderMessage(orderMessage);
		} catch (Exception e) {
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}
		
		List<GenericValue> applicableTaxTypes = null;
		try {
			applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive ProductPriceType ", module);
			return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
		}
		
	  	List applTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);
	  	if(UtilValidate.isNotEmpty(geoTax)){
			if(geoTax.equals("VAT")){
				applTaxTypeList.remove("CST_SALE");
			}
			else{
				applTaxTypeList.remove("VAT_SALE");
			}
		}
	  	if(UtilValidate.isEmpty(geoTax) && UtilValidate.isNotEmpty(salesChannel) && (salesChannel.equals("INTUNIT_TR_CHANNEL") || salesChannel.equals("ICP_TRANS_CHANNEL"))){
	  		isSale = Boolean.FALSE;
		}
	  	List<GenericValue> productPriceTaxCalc = FastList.newInstance();
		List<GenericValue> prodPriceType = null;
		
	  	List condsList = FastList.newInstance();
	  	
	  	condsList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	  	condsList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.IN, applTaxTypeList));
	  	condsList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "TAX"));
		EntityCondition priceCond = EntityCondition.makeCondition(condsList,EntityOperator.AND);
		
		try {
			prodPriceType = delegator.findList("ProductPriceAndType", priceCond, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive ProductPriceType ", module);
			return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
		}
		prodPriceType = EntityUtil.filterByDate(prodPriceType, effectiveDate);

		String productId = "";
		BigDecimal quantity = BigDecimal.ZERO;
		String batchNo = "";
		String daysToStore = "";
		for (Map<String, Object> prodQtyMap : productQtyList) {
			
			BigDecimal basicPrice = BigDecimal.ZERO;
			BigDecimal bedPrice = BigDecimal.ZERO;
			BigDecimal vatPrice = BigDecimal.ZERO;
			BigDecimal cstPrice = BigDecimal.ZERO;
			BigDecimal tcsPrice = BigDecimal.ZERO;
			BigDecimal serviceTaxPrice = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
				productId = (String)prodQtyMap.get("productId");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
				quantity = (BigDecimal)prodQtyMap.get("quantity");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("batchNo"))){
				batchNo = (String)prodQtyMap.get("batchNo");
				batchNumExists = true;
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("daysToStore"))){
				daysToStore = (String)prodQtyMap.get("daysToStore");
				daysToStoreExists = true;
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("basicPrice"))){
				basicPrice = (BigDecimal)prodQtyMap.get("basicPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("bedPrice"))){
				bedPrice = (BigDecimal)prodQtyMap.get("bedPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPrice"))){
				vatPrice = (BigDecimal)prodQtyMap.get("vatPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPrice"))){
				cstPrice = (BigDecimal)prodQtyMap.get("cstPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("tcsPrice"))){
				tcsPrice = (BigDecimal)prodQtyMap.get("tcsPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("serviceTaxPrice"))){
				serviceTaxPrice = (BigDecimal)prodQtyMap.get("serviceTaxPrice");
			}
			
			//add percentages
			BigDecimal bedPercent=BigDecimal.ZERO;
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO;
			BigDecimal tcsPercent=BigDecimal.ZERO;
			BigDecimal serviceTaxPercent=BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(prodQtyMap.get("bedPercent"))){
				bedPercent = (BigDecimal)prodQtyMap.get("bedPercent");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
				vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
				cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("tcsPercent"))){
				tcsPercent = (BigDecimal)prodQtyMap.get("tcsPercent");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("serviceTaxPercent"))){
				serviceTaxPercent = (BigDecimal)prodQtyMap.get("serviceTaxPercent");
			}
			
			Map<String, Object> priceResult;
			Map<String, Object> priceContext = FastMap.newInstance();
			priceContext.put("userLogin", userLogin);
			priceContext.put("productId", productId);	
			
			priceContext.put("priceDate", effectiveDate);
			priceContext.put("geoTax", geoTax);
			priceContext.put("productStoreId", productStoreId);
			if(UtilValidate.isNotEmpty(basicPrice) && basicPrice.compareTo(BigDecimal.ZERO)>0){
				priceContext.put("basicPrice", basicPrice);
				priceContext.put("bedPrice", bedPrice);
				priceContext.put("vatPrice", vatPrice);
				priceContext.put("cstPrice", cstPrice);
				priceContext.put("tcsPrice", tcsPrice);
				priceContext.put("serviceTaxPrice", serviceTaxPrice);
				priceContext.put("bedPercent", bedPercent);
				priceContext.put("vatPercent", vatPercent);
				priceContext.put("cstPercent", cstPercent);
				priceContext.put("tcsPercent", tcsPercent);
				priceContext.put("serviceTaxPercent", serviceTaxPercent);
				priceResult = ByProductNetworkServices.calculateUserDefinedProductPrice(delegator, dispatcher, priceContext);
			}
			else{
				priceContext.put("isSale", isSale);
				priceContext.put("partyId", partyId);
				priceResult = ByProductNetworkServices.calculateStoreProductPrices(delegator, dispatcher, priceContext);
			}
			
			if (ServiceUtil.isError(priceResult)) {
				Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
				return ServiceUtil.returnError("There was an error while calculating the price");
			}
			BigDecimal totalPrice = (BigDecimal)priceResult.get("totalPrice");
			List<Map> taxList = (List)priceResult.get("taxList");
				ShoppingCartItem item = null;
				try{
					int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null,	quantity, (BigDecimal)priceResult.get("basicPrice"),
					            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
					            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
					
					item = cart.findCartItem(itemIndx);
					item.setListPrice(totalPrice);
	        		item.setTaxDetails(taxList);
				}
				catch (Exception exc) {
					Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
					return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: ");
		        }
				List<GenericValue> productTaxes = EntityUtil.filterByCondition(prodPriceType, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				for (Map eachTaxType : taxList) {
					String taxId = (String)eachTaxType.get("taxType");
					BigDecimal amount = (BigDecimal) eachTaxType.get("amount");
					List<GenericValue> productTaxTypesList = EntityUtil.filterByCondition(productTaxes, EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, taxId));
					if(UtilValidate.isNotEmpty(productTaxTypesList)){
						GenericValue prodTaxType = EntityUtil.getFirst(productTaxTypesList);
						if(UtilValidate.isNotEmpty(amount) && amount.compareTo(BigDecimal.ZERO)>0){
							prodTaxType.set("price", amount);
						}
						productPriceTaxCalc.add(prodTaxType);
					}
					else{
						GenericValue productPrice = delegator.makeValue("ProductPrice");        	 
						productPrice.set("productId", productId);
						productPrice.set("productPriceTypeId", taxId);
						productPrice.set("productPricePurposeId", "SALE_PRICE");
						productPrice.set("productStoreGroupId", "_NA_");
						productPrice.set("currencyUomId", "INR");
						productPrice.set("price", amount);
						productPriceTaxCalc.add(productPrice);
					}
					
				}
			}
		cart.setDefaultCheckoutOptions(dispatcher);
        ProductPromoWorker.doPromotions(cart, dispatcher);
        CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
		try {
			if(isSale || UtilValidate.isNotEmpty(productPriceTaxCalc)){
				checkout.calcAndAddTax(productPriceTaxCalc);
			}
			
		} catch (Exception e1) {
		// TODO Auto-generated catch block
			Debug.logError(e1, "Error in CalcAndAddTax",module);
		}
		Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
		orderId = (String) orderCreateResult.get("orderId");
		
		if(promoAmt.compareTo(BigDecimal.ZERO)>0){
			Map promoAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			promoAdjCtx.put("orderId", orderId);
			promoAdjCtx.put("promoAdjAmt", promoAmt);
		  	 	  	 
		  	try{
		  		Map resultCtx = dispatcher.runSync("adjustPromotionAmtForOrder",promoAdjCtx);  		  		 
		  		if (ServiceUtil.isError(resultCtx)) {
		  	 		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
		  	 		Debug.logError(errMsg , module);
		  		}	
	         }catch (GenericServiceException e) {
	        	 Debug.logError(e , module);
	             return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
	         }
		}
		//creating adjustemnts by list
		//Debug.log("=====orderAdjChargesList="+orderAdjChargesList);
		if(UtilValidate.isNotEmpty(orderAdjChargesList)){
			Map inputAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			inputAdjCtx.put("orderId", orderId);
			inputAdjCtx.put("orderAdjChargesList", orderAdjChargesList);
			result = createOrderAdjustmentByTypeList(dctx, inputAdjCtx);
			if (ServiceUtil.isError(result)) {
				Debug.logError("Unable to generate Adjustments: " + ServiceUtil.getErrorMessage(result), module);
				 return ServiceUtil.returnError(" Unable to generate Adjustments:");
	  		}	
		}
		
		if(UtilValidate.isNotEmpty(orderId) && (batchNumExists || daysToStoreExists)){
			try{
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if(geoTax.equals("CST")){
					orderHeader.set("isInterState", "N");
				}else{
					orderHeader.set("isInterState", "Y");
				}
				orderHeader.store();
				
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("orderId", "productId", "quantity", "orderItemSeqId"), null, null, false);
				for(GenericValue orderItem : orderItems){
					if(UtilValidate.isNotEmpty(productQtyList)){
						Map batchMap = (Map)productQtyList.get(0);
						GenericValue newItemAttr = delegator.makeValue("OrderItemAttribute");        	 
						newItemAttr.set("orderId", orderItem.getString("orderId"));
						newItemAttr.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
						newItemAttr.set("attrName", "batchNumber");
						newItemAttr.set("attrValue", (String)batchMap.get("batchNo"));
						newItemAttr.create();
						
						GenericValue newDayStoreAttr = delegator.makeValue("OrderItemAttribute");        	 
						newDayStoreAttr.set("orderId", orderItem.getString("orderId"));
						newDayStoreAttr.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
						newDayStoreAttr.set("attrName", "daysToStore");
						newDayStoreAttr.set("attrValue", (String)batchMap.get("daysToStore"));
						newDayStoreAttr.create();
						
						productQtyList.remove(0);
					}
				}
			}catch (GenericEntityException e) {
				Debug.logError("Error in creating shipmentId for DirectOrder", module);
				return ServiceUtil.returnError("Error in creating shipmentId for DirectOrder");
			}
			
		}
		//store OrderMessage
		if(UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(orderMessage )){
			try{
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				orderHeader.set("orderMessage", orderMessage.trim());
				orderHeader.store();
			}catch (GenericEntityException e) {
				Debug.logError("Error While Saving Order Message ", module);
				return ServiceUtil.returnError("Error While Saving Order Message");
			}
			
		}
		
		result.put("orderId", orderId);
		return result;
    }
	public static Map<String, Object> adjustPromotionAmtForOrder(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        BigDecimal promoAdjAmt = (BigDecimal) context.get("promoAdjAmt");
        Locale locale = (Locale) context.get("locale");     
        Map result = ServiceUtil.returnSuccess();
		try {
			
			if(UtilValidate.isEmpty(promoAdjAmt) || promoAdjAmt.compareTo(BigDecimal.ZERO)<=0){
				Debug.logWarning("promoAdjAmt cannot be zero", module);
         		return ServiceUtil.returnError("promoAdjAmt cannot be zero");
			}
			BigDecimal taxAdj = (promoAdjAmt.multiply(new BigDecimal(14.5))).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
			 String promoAdjustmentTypeId = "PPD_PROMO_ADJ";
			 Map createOrderAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
	    	 createOrderAdjustmentCtx.put("orderId", orderId);
	    	 createOrderAdjustmentCtx.put("orderAdjustmentTypeId", promoAdjustmentTypeId);    	
	    	 createOrderAdjustmentCtx.put("amount", promoAdjAmt.negate());
	    	 result = dispatcher.runSync("createOrderAdjustment", createOrderAdjustmentCtx);
	     	 if (ServiceUtil.isError(result)) {
	                Debug.logWarning("There was an error while creating  promotion adjustment: " + ServiceUtil.getErrorMessage(result), module);
	         		return ServiceUtil.returnError("There was an error while creating promotion adjustment: " + ServiceUtil.getErrorMessage(result));          	            
	         }
	     	 
	     	 
	     	 String taxAdjustmentTypeId = "VAT_SALE";
			 Map createTaxAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
			 createTaxAdjustmentCtx.put("orderId", orderId);
			 createTaxAdjustmentCtx.put("orderAdjustmentTypeId", taxAdjustmentTypeId);    	
			 createTaxAdjustmentCtx.put("amount", taxAdj.negate());
	    	 result = dispatcher.runSync("createOrderAdjustment", createTaxAdjustmentCtx);
	     	 if (ServiceUtil.isError(result)) {
	                Debug.logWarning("There was an error while creating  tax adjustment: " + ServiceUtil.getErrorMessage(result), module);
	         		return ServiceUtil.returnError("There was an error while creating tax adjustment: " + ServiceUtil.getErrorMessage(result));          	            
	         }
		
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
        result = ServiceUtil.returnSuccess("Successfully added the adjustment!!");
        return result;
    }
	
	
	public static String createShipmentAndInvoiceForOrders(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String shipDateStr = (String) request.getParameter("shipDate");
		String vehicleId = (String) request.getParameter("vehicleId");
		String carrierName = (String) request.getParameter("carrierName");
		String lrNumber = (String) request.getParameter("lrNumber");
		String modeOfDespatch = (String) request.getParameter("modeOfDespatch");
		String salesChannelEnumId = (String) request.getParameter("salesChannelEnumId");
		String shipmentTypeId = (String) request.getParameter("shipmentTypeId");
		String orderStatusId = (String) request.getParameter("orderStatusId");
		
		Map resultMap = FastMap.newInstance();
		String partyIdFrom = "";
		String shipmentId = "";
		Map processOrderContext = FastMap.newInstance();
		boolean beganTransaction = false;
		Timestamp effectiveDate=null;
		BigDecimal quantity = BigDecimal.ZERO;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if (UtilValidate.isNotEmpty(shipDateStr)) { //2011-12-25 18:09:45
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
			try {
				effectiveDate = new java.sql.Timestamp(sdf.parse(shipDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + shipDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + shipDateStr, module);
			}
		}
		else{
			effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		if( UtilValidate.isEmpty(vehicleId)){
			Debug.logWarning("Vehicle  number is empty ", module);
			request.setAttribute("_ERROR_MESSAGE_", "Vehicle number is empty");
			return "error";
		}
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process");
			return "error";
		}
		try{
			
			beganTransaction = TransactionUtil.begin(7200);
			if(UtilValidate.isEmpty(shipmentId)){
			    
				GenericValue newDirShip = delegator.makeValue("Shipment");        	 
				newDirShip.set("estimatedShipDate", effectiveDate);
				newDirShip.set("shipmentTypeId", shipmentTypeId);
				newDirShip.set("statusId", "GENERATED");
				newDirShip.set("vehicleId", vehicleId);
				newDirShip.set("carrierName", carrierName);
				newDirShip.set("lrNumber", lrNumber);
				newDirShip.set("modeOfDespatch", modeOfDespatch);
				newDirShip.set("createdDate", UtilDateTime.nowTimestamp());
				newDirShip.set("createdByUserLogin", userLogin.get("userLoginId"));
				newDirShip.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				delegator.createSetNextSeqId(newDirShip);            
				shipmentId = (String) newDirShip.get("shipmentId");
			}
			
			List<String> orderIdsList = FastList.newInstance();
			String orderId = "";
			for (int i = 0; i < rowCount; i++) {
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("orderId" + thisSuffix)) {
					orderId = (String) paramMap.get("orderId" + thisSuffix);
					orderIdsList.add(orderId);
				}
			}//end row count for loop
		  
			if( UtilValidate.isEmpty(orderIdsList)){
				Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
				request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
				TransactionUtil.rollback();
				return "error";
			}
			
			Boolean enableAdvancePaymentApp  = Boolean.FALSE;
    		GenericValue tenantConfigEnableAdvancePaymentApp = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableAdvancePaymentApp"), true);
       		if (UtilValidate.isNotEmpty(tenantConfigEnableAdvancePaymentApp) && (tenantConfigEnableAdvancePaymentApp.getString("propertyValue")).equals("Y")) {
       			enableAdvancePaymentApp = Boolean.TRUE;
       		}
       		
       		for(String eachOrderId : orderIdsList){

       			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", eachOrderId), false);
    			orderHeader.set("shipmentId", shipmentId);
    			
    			if(UtilValidate.isNotEmpty(orderStatusId)){
    			orderHeader.set("statusId", orderStatusId);
    			}
    			orderHeader.store();
    			
    			String invoiceId = "";
    			result = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", eachOrderId,"eventDate", effectiveDate,"userLogin", userLogin, "purposeTypeId", salesChannelEnumId));
    	        if (ServiceUtil.isError(result)) {
    	        	Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(result), module);
    	        	String errorMessageResult=result.toString();
    	        	request.setAttribute("_ERROR_MESSAGE_", "There was an error while creating the invoice for order: "+eachOrderId+" And "+ServiceUtil.getErrorMessage(result));
    	        	if(errorMessageResult.contains("INV_UN_AVAILABLE") ){
    	        		request.setAttribute("_ERROR_MESSAGE_", "Inventory Not Available To Issue For OrderId:"+eachOrderId+" Maintain Inventory !");
    	        	}
    	        	TransactionUtil.rollback();
    	        	return "error";
    	        }
    	        
    	        invoiceId = (String) result.get("invoiceId");
    	        
    	        Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
		        invoiceCtx.put("userLogin", userLogin);
		        invoiceCtx.put("statusId","INVOICE_READY");
		            
		        Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
		        if (ServiceUtil.isError(invoiceResult)) {
		           	Debug.logError(invoiceResult.toString(), module);
			       	request.setAttribute("_ERROR_MESSAGE_", "There was an error setting invoice status");
			       	TransactionUtil.rollback();
			       	return "error";
		        }
		        
		        GenericValue invoice= delegator.findOne("Invoice",UtilMisc.toMap("invoiceId", invoiceId), true);
				if (UtilValidate.isEmpty(invoice)) {
					Debug.logError("Invoice doesn't exists with Id: " + invoiceId,module);
					return "error";
				}
				if (UtilValidate.isNotEmpty(invoice)) {
					partyIdFrom = invoice.getString("partyId");
				}
		        //creditNote is created here
				Boolean turnOnCreditOrDebitNote  = Boolean.FALSE;
	    		GenericValue tenantConfigTurnOnCreditOrDebitNote = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","ACCOUNT_INVOICE", "propertyName","turnOnCreditOrDebitNote"), true);
	       		if (UtilValidate.isNotEmpty(tenantConfigTurnOnCreditOrDebitNote) && (tenantConfigTurnOnCreditOrDebitNote.getString("propertyValue")).equals("Y")) {
	       			turnOnCreditOrDebitNote = Boolean.TRUE;
	       		}
		       	 if(turnOnCreditOrDebitNote){
			        if("INTUNIT_TR_CHANNEL".equals(salesChannelEnumId) || "ICP_TRANS_CHANNEL".equals(salesChannelEnumId)){
			    		  Map paymentInputMap = FastMap.newInstance();
				  		  paymentInputMap.put("userLogin", userLogin);
				  		  paymentInputMap.put("paymentTypeId", "SALES_PAYIN");
				  		 // paymentInputMap.put("paymentType", "SALES_PAYIN");
				  		  paymentInputMap.put("paymentMethodTypeId", "CREDITNOTE_PAYIN");
				  		  paymentInputMap.put("paymentPurposeType","NON_ROUTE_MKTG");
				  		  paymentInputMap.put("statusId", "PMNT_NOT_PAID");
				  		  paymentInputMap.put("invoiceIds",UtilMisc.toList(invoiceId));
				  		  String finAccountId =  null;
				  		  List conditionList = FastList.newInstance();
				  		  conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,partyIdFrom));
				  		  conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"INTERUNIT_ACCOUNT"));
				  		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
				  		  EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
				  		  List<GenericValue> finAccountList = delegator.findList("FinAccount", condition, null, null, null, false);
				  		  if(UtilValidate.isNotEmpty(finAccountList)){
				  			  GenericValue finAccount = EntityUtil.getFirst(finAccountList);
					  			if(UtilValidate.isNotEmpty(finAccount)){
					  				finAccountId = finAccount.getString("finAccountId");
					  			}
				  		  }
					  	  if(UtilValidate.isNotEmpty(finAccountId)){
					  		  paymentInputMap.put("finAccountId", finAccountId);
						  }
				  		  
				  		  Map paymentResult = dispatcher.runSync("createCreditNoteOrDebitNoteForInvoice", paymentInputMap);
				  		  if(ServiceUtil.isError(paymentResult)){
			    			     Debug.logError(paymentResult.toString(), module);
		    			        request.setAttribute("_ERROR_MESSAGE_", "There was an error in service createCreditNoteOrDebitNoteForInvoice");
		    			        TransactionUtil.rollback();
		    			        return "error";
				  		  }
				  		  List paymentIds = (List)paymentResult.get("paymentsList");
	    	        Debug.log("+++++++===paymentIds====="+paymentIds);
			        }
		       	 }//Credit Note functionality check
		        
    	        if(enableAdvancePaymentApp){
    		            // apply invoice if any adavance payments from this  party
    	     		/*Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", invoiceId,"userLogin", userLogin));
    	     		if (ServiceUtil.isError(resultPaymentApp)) {						  
    	     			Debug.logError(resultPaymentApp.toString(), module);
    			        request.setAttribute("_ERROR_MESSAGE_", "There was an error in service settleInvoiceAndPayments");
    			        TransactionUtil.rollback();
    			        return "error";
    	     		}*/
    	    	}
    		}
			
		}catch(GenericEntityException e){
			//Debug.logWarning("Error associating order to shipment ", module);
			//request.setAttribute("_ERROR_MESSAGE_", "Error associating order to shipment ");
			try {
				// only rollback the transaction if we started one...
	  			TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
			return "error";
		}
		catch(GenericServiceException e){
			//Debug.logWarning("Error creating invoice for the order "+eachOrderId, module);
			//request.setAttribute("_ERROR_MESSAGE_", "Error creating invoice for the order "+eachOrderId);
			try {
				// only rollback the transaction if we started one...
	  			TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
			} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		}
			return "error";
		}
		finally {
	  		  // only commit the transaction if we started one... this will throw an exception if it fails
	  		  try {
	  			  TransactionUtil.commit(beganTransaction);
	  		  } catch (GenericEntityException e) {
	  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  		  }
	  	}
		request.setAttribute("_EVENT_MESSAGE_", "Successfully processed shipment ");	  	 
		return "success";
	}
	
	public static String processIcpSale(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String partyId = (String) request.getParameter("partyId");
		String billToCustomer = (String)request.getParameter("billToCustomer");//using For Amul Sales
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String productStoreId = (String) request.getParameter("productStoreId");
		String orderTaxType = (String) request.getParameter("orderTaxType");
		String orderId = (String) request.getParameter("orderId");
		String PONumber = (String) request.getParameter("PONumber");
		String promotionAdjAmt = (String) request.getParameter("promotionAdjAmt");
		String orderMessage=(String) request.getParameter("orderMessage");
		String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
		String disableAcctgFlag = (String) request.getParameter("disableAcctgFlag");
		String subscriptionTypeId = "AM";
		String partyIdFrom = "";
		String shipmentId = "";
		Map processOrderContext = FastMap.newInstance();
		String salesChannel = (String)request.getParameter("salesChannel");
		if(UtilValidate.isEmpty(productSubscriptionTypeId)){
			productSubscriptionTypeId = "CASH";      	
		}
		
		String productId = null;
		String batchNo = null;
		String daysToStore = null;
		String quantityStr = null;
		String basicPriceStr = null;
		String vatPriceStr = null;
		String bedPriceStr = null;
		String cstPriceStr = null;
		String tcsPriceStr = null;
		String serTaxPriceStr = null;
		Timestamp effectiveDate=null;
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal basicPrice = BigDecimal.ZERO;
		BigDecimal cstPrice = BigDecimal.ZERO;
		BigDecimal tcsPrice = BigDecimal.ZERO;
		BigDecimal vatPrice = BigDecimal.ZERO;
		BigDecimal bedPrice = BigDecimal.ZERO;
		BigDecimal serviceTaxPrice = BigDecimal.ZERO;
		//percentage fields
		String bedPercentStr = null;
		String vatPercentStr = null;
		String cstPercentStr = null;
		String tcsPercentStr = null;
		String serviceTaxPercentStr = null;
		
		BigDecimal bedPercent=BigDecimal.ZERO;
		BigDecimal vatPercent=BigDecimal.ZERO;
		BigDecimal cstPercent=BigDecimal.ZERO;
		BigDecimal tcsPercent=BigDecimal.ZERO;
		BigDecimal serviceTaxPercent=BigDecimal.ZERO;
		
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
			try {
				effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			}
		}
		else{
			effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		if (partyId == "") {
			request.setAttribute("_ERROR_MESSAGE_","Party Id is empty");
			return "error";
		}
		try{
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			if(UtilValidate.isEmpty(party)){
				request.setAttribute("_ERROR_MESSAGE_","Not a valid Party");
				return "error";
			}
		}catch(GenericEntityException e){
			Debug.logError(e, "Error fetching partyId " + partyId, module);
			request.setAttribute("_ERROR_MESSAGE_","Invalid party Id");
			return "error";
		}
		if(UtilValidate.isNotEmpty(request.getAttribute("estimatedDeliveryDate"))) {
			effectiveDate = (Timestamp) request.getAttribute("estimatedDeliveryDate");
		}

		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		List productIds = FastList.newInstance();
		List indentProductList = FastList.newInstance();
		for (int i = 0; i < rowCount; i++) {
		  
			Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productInput= (String) paramMap.get("productId" + thisSuffix);
			//invoke if only not empty
			if (UtilValidate.isNotEmpty(productInput)) {

				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
					productIds.add(productId);
				} else {
					request.setAttribute("_ERROR_MESSAGE_",
							"Missing product id");
					return "error";
				}

				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap
							.get("quantity" + thisSuffix);
				} else {
					request.setAttribute("_ERROR_MESSAGE_",
							"Missing product quantity");
					return "error";
				}
				if (quantityStr.equals("")) {
					request.setAttribute("_ERROR_MESSAGE_",
							"Empty product quantity");
					return "error";
				}
				if (paramMap.containsKey("batchNo" + thisSuffix)) {
					batchNo = (String) paramMap.get("batchNo" + thisSuffix);
				}
				if (paramMap.containsKey("daysToStore" + thisSuffix)) {
					daysToStore = (String) paramMap.get("daysToStore"
							+ thisSuffix);
				}

				if (paramMap.containsKey("basicPrice" + thisSuffix)) {
					basicPriceStr = (String) paramMap.get("basicPrice"
							+ thisSuffix);
				}
				if (paramMap.containsKey("vatPrice" + thisSuffix)) {
					vatPriceStr = (String) paramMap
							.get("vatPrice" + thisSuffix);
				}
				if (paramMap.containsKey("bedPrice" + thisSuffix)) {
					bedPriceStr = (String) paramMap
							.get("bedPrice" + thisSuffix);
				}
				if (paramMap.containsKey("cstPrice" + thisSuffix)) {
					cstPriceStr = (String) paramMap
							.get("cstPrice" + thisSuffix);
				}
				if (paramMap.containsKey("tcsPrice" + thisSuffix)) {
					tcsPriceStr = (String) paramMap
							.get("tcsPrice" + thisSuffix);
				}
				if (paramMap.containsKey("serviceTaxPrice" + thisSuffix)) {
					serTaxPriceStr = (String) paramMap.get("serviceTaxPrice"
							+ thisSuffix);
				}

				if (paramMap.containsKey("bedPercent" + thisSuffix)) {
					bedPercentStr = (String) paramMap.get("bedPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("vatPercent" + thisSuffix)) {
					vatPercentStr = (String) paramMap.get("vatPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("cstPercent" + thisSuffix)) {
					cstPercentStr = (String) paramMap.get("cstPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("tcsPercent" + thisSuffix)) {
					tcsPercentStr = (String) paramMap.get("tcsPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("serviceTaxPercent" + thisSuffix)) {
					serviceTaxPercentStr = (String) paramMap
							.get("serviceTaxPercent" + thisSuffix);
				}

				try {
					quantity = new BigDecimal(quantityStr);
					if (UtilValidate.isNotEmpty(basicPriceStr)) {
						basicPrice = new BigDecimal(basicPriceStr);
					}
					if (UtilValidate.isNotEmpty(cstPriceStr)) {
						cstPrice = new BigDecimal(cstPriceStr);
					}
					if (UtilValidate.isNotEmpty(tcsPriceStr)) {
						tcsPrice = new BigDecimal(tcsPriceStr);
					}
					if (UtilValidate.isNotEmpty(bedPriceStr)) {
						bedPrice = new BigDecimal(bedPriceStr);
					}
					if (UtilValidate.isNotEmpty(vatPriceStr)) {
						vatPrice = new BigDecimal(vatPriceStr);
					}
					if (UtilValidate.isNotEmpty(serTaxPriceStr)) {
						serviceTaxPrice = new BigDecimal(serTaxPriceStr);
					}

					if (UtilValidate.isNotEmpty(bedPercentStr)) {
						bedPercent = new BigDecimal(bedPercentStr);
					}
					if (UtilValidate.isNotEmpty(vatPercentStr)) {
						vatPercent = new BigDecimal(vatPercentStr);
					}
					if (UtilValidate.isNotEmpty(cstPercentStr)) {
						cstPercent = new BigDecimal(cstPercentStr);
					}
					if (UtilValidate.isNotEmpty(tcsPercentStr)) {
						tcsPercent = new BigDecimal(tcsPercentStr);
					}
					if (UtilValidate.isNotEmpty(serviceTaxPercentStr)) {
						serviceTaxPercent = new BigDecimal(serviceTaxPercentStr);
					}
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing quantity string: "
							+ quantityStr, module);
					request.setAttribute("_ERROR_MESSAGE_",
							"Problems parsing quantity string: " + quantityStr);
					return "error";
				}
				
				productQtyMap.put("productId", productId);
				productQtyMap.put("quantity", quantity);
				productQtyMap.put("batchNo", batchNo);
				productQtyMap.put("daysToStore", daysToStore);
				productQtyMap.put("basicPrice", basicPrice);
				productQtyMap.put("bedPrice", bedPrice);
				productQtyMap.put("cstPrice", cstPrice);
				productQtyMap.put("tcsPrice", tcsPrice);
				productQtyMap.put("vatPrice", vatPrice);
				productQtyMap.put("serviceTaxPrice", serviceTaxPrice);

				productQtyMap.put("bedPercent", bedPercent);
				productQtyMap.put("vatPercent", vatPercent);
				productQtyMap.put("cstPercent", cstPercent);
				productQtyMap.put("tcsPercent", tcsPercent);
				productQtyMap.put("serviceTaxPercent", serviceTaxPercent);
				
				if(quantity.compareTo(BigDecimal.ZERO)>0){
					indentProductList.add(productQtyMap);
				}

			}//end of productQty check
		}//end row count for loop
	  
		if( UtilValidate.isEmpty(indentProductList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "error";
		}
		//adding list of adjustments
		List orderAdjChargesList = FastList.newInstance();
		for (int i = 0; i < rowCount; i++) {
			Map orderAdjMap = FastMap.newInstance();
			String orderAdjTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			if (paramMap.containsKey("orderAdjTypeId" + thisSuffix)) {
				orderAdjTypeId = (String) paramMap.get("orderAdjTypeId" + thisSuffix);
			}
			
			if(UtilValidate.isNotEmpty(orderAdjTypeId)){
				if (paramMap.containsKey("adjAmt" + thisSuffix)) {
					adjAmtStr = (String) paramMap.get("adjAmt" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing Adjustment Amount");
					return "error";			  
				}
				try {
					adjAmt = new BigDecimal(adjAmtStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
				
				orderAdjMap.put("orderAdjTypeId", orderAdjTypeId);
				orderAdjMap.put("adjAmount", adjAmt);
				orderAdjChargesList.add(orderAdjMap);	

			}
			//end of adjustment check
		}
		
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("productQtyList", indentProductList);
		processOrderContext.put("partyId", partyId);
		processOrderContext.put("billToCustomer", billToCustomer);
		processOrderContext.put("productIds", productIds);
		processOrderContext.put("supplyDate", effectiveDate);
		processOrderContext.put("salesChannel", salesChannel);
		processOrderContext.put("orderTaxType", orderTaxType);
		processOrderContext.put("orderId", orderId);
		processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
		processOrderContext.put("productStoreId", productStoreId);
		processOrderContext.put("PONumber", PONumber);
		processOrderContext.put("promotionAdjAmt", promotionAdjAmt);
		processOrderContext.put("orderMessage", orderMessage);
		processOrderContext.put("orderAdjChargesList", orderAdjChargesList);
		processOrderContext.put("disableAcctgFlag", disableAcctgFlag);
		
		
		result = processICPSaleOrder(dctx, processOrderContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
			return "error";
		}
		
		orderId = (String)result.get("orderId");
		if(UtilValidate.isEmpty(orderId)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
			return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", "Order Entry successfully for party : "+partyId);
		return "success";
	}
	
	public static String editBatchNumber(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String orderId = (String) request.getParameter("orderId");
		Map resultMap = FastMap.newInstance();
		Map processOrderContext = FastMap.newInstance();
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if (orderId == "") {
			request.setAttribute("_ERROR_MESSAGE_","Order Id is empty");
			return "error";
		}
		String shipmentTypeId = "";
		String salesChannelEnumId = "";
		
		try{
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			salesChannelEnumId = orderHeader.getString("salesChannelEnumId");
			if(UtilValidate.isEmpty(orderHeader)){
				request.setAttribute("_ERROR_MESSAGE_","Not a valid order");
				return "error";
			}
		}catch(GenericEntityException e){
			Debug.logError(e, "Error fetching orderId " + orderId, module);
			request.setAttribute("_ERROR_MESSAGE_","Invalid order Id");
			return "error";
		}
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		
		try{
			
			String orderItemSeqId = "";
			
			for (int i = 0; i < rowCount; i++) {
				
				String batchNo = "";
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("orderId" + thisSuffix)) {
					orderId = (String) paramMap.get("orderId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing order id");
					return "error";			  
				}
				
				if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
					orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing orderItemSeq id");
					return "error";			  
				}
				
				if (paramMap.containsKey("batchNo" + thisSuffix)) {
					batchNo = (String) paramMap.get("batchNo" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing Batch Number");
					return "error";			  
				}

				GenericValue orderItemAttribute = delegator.makeValue("OrderItemAttribute");
				orderItemAttribute.set("orderId", orderId);
				orderItemAttribute.set("orderItemSeqId", orderItemSeqId);
				orderItemAttribute.set("attrName", "batchNumber");
				orderItemAttribute.set("attrValue", batchNo);
				delegator.createOrStore(orderItemAttribute);
				
			}//end row count for loop
		}catch(GenericEntityException e){
			Debug.logError(e, "Error fetching order details for id: " + orderId, module);
			request.setAttribute("_ERROR_MESSAGE_","Error fetching order details for id: ");
			return "error";
		}
		return "success";
	}
	
	public static Map<String, Object> cancelICPShipment(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Locale locale = (Locale) context.get("locale");
	    String shipmentId = (String) context.get("shipmentId");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		List<String> orderIds = FastList.newInstance();
		List<String> invoiceIds = FastList.newInstance();
		try{
			
			List<GenericValue> orderHeaders = delegator.findList("OrderHeader", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false);
			orderIds = EntityUtil.getFieldListFromEntityList(orderHeaders, "orderId", true);
			
			for(GenericValue orderHeader : orderHeaders){
				
				if("ORDER_COMPLETED".equals(orderHeader.getString("statusId"))){
					result = dispatcher.runSync("cancelItemIssuanceForOrder", UtilMisc.toMap("orderId", orderHeader.getString("orderId"),"shipmentId",orderHeader.getString("shipmentId"),"userLogin", userLogin));
					if (ServiceUtil.isError(result)) {
		   		 		Debug.logError("There was an error while Cancel  Item Issuance for Order: " + ServiceUtil.getErrorMessage(result), module);	               
			            return ServiceUtil.returnError("There was an error while Cancel  Item Issuance for Order: ");   			 
		   		 	}
					orderHeader.set("needsInventoryIssuance", "Y");// set this flag to issue again bcz above we are canceling issuance for order
					orderHeader.set("statusId", "ORDER_APPROVED"); //change to old status
				}
				orderHeader.set("shipmentId", null);
				orderHeader.store();
			}
			
       		List<GenericValue> partyInvoiceList = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds), UtilMisc.toSet("invoiceId") , null, null, false);   
       		invoiceIds = EntityUtil.getFieldListFromEntityList(partyInvoiceList, "invoiceId", true);    		

			result = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", invoiceIds, "statusId","INVOICE_CANCELLED","userLogin", userLogin));
   		 
   		 	if (ServiceUtil.isError(result)) {
   		 		Debug.logError("There was an error while Cancel  the invoices: " + ServiceUtil.getErrorMessage(result), module);	               
	            return ServiceUtil.returnError("There was an error while Cancel  the invoices: ");   			 
   		 	}
   		 	
   		 	GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
   		 	
   		 	shipment.set("statusId", "SHIPMENT_CANCELLED");
   		 	shipment.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
   		 	shipment.put("lastModifiedDate", UtilDateTime.nowTimestamp());
     		shipment.store();
     		
     		List conditionList = FastList.newInstance();
     		conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "CREDITNOTE_PAYIN"));
     		conditionList.add(EntityCondition.makeCondition("paymentPurposeType", EntityOperator.EQUALS, "NON_ROUTE_MKTG"));
     		conditionList.add(EntityCondition.makeCondition("paymentRefNum", EntityOperator.IN, invoiceIds));
     		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
     		List<GenericValue> creditNotes = delegator.findList("Payment", cond, null, null, null, false);
     		List paymentIds = EntityUtil.getFieldListFromEntityList(creditNotes, "paymentId", true);
     		if(UtilValidate.isNotEmpty(paymentIds)){
     			for(int i=0;i<paymentIds.size();i++){
     				String paymentId = (String)paymentIds.get(i);
     				Map resultPayMap = dispatcher.runSync("voidPayment", UtilMisc.toMap("paymentId", paymentId, "userLogin", userLogin));
    				if (ServiceUtil.isError(resultPayMap)) {
    					Debug.logError("There was an error in cancelling credit note: " + ServiceUtil.getErrorMessage(resultPayMap), module);
	                    return ServiceUtil.returnError("There was an error in cancelling credit note: " + ServiceUtil.getErrorMessage(resultPayMap));          	            
	                }
     			}
     		}
			
		}catch(GenericEntityException e){
			Debug.logError("Error in fetching shipment : "+ shipmentId, module);
			return ServiceUtil.returnError("Error in fetching shipment : "+shipmentId);
		}
		catch(GenericServiceException e){
			Debug.logError("Error cancelling invoices adn shipment : "+ shipmentId, module);
			return ServiceUtil.returnError("Error cancelling invoices adn shipment : "+shipmentId);
		}
		result = ServiceUtil.returnSuccess("Shipment: "+shipmentId+" Cancelled Successfully !");
		return result;
    }
	
	public static Map<String, Object> createOrderAdjustmentByTypeList(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        List<Map> orderAdjChargesList = (List<Map>) context.get("orderAdjChargesList");
        Locale locale = (Locale) context.get("locale");     
        Map result = ServiceUtil.returnSuccess();
		try {
	     	for (Map<String, Object> adjItemMap : orderAdjChargesList) {
				
				String orderAdjTypeId = "";
				BigDecimal amount = BigDecimal.ZERO;
				Map invoiceItemCtx = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(adjItemMap.get("orderAdjTypeId"))){
					
					orderAdjTypeId = (String)adjItemMap.get("orderAdjTypeId");
					if(UtilValidate.isNotEmpty(adjItemMap.get("adjAmount"))){
						amount = (BigDecimal)adjItemMap.get("adjAmount");
					}
					 Map createTaxAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
					 createTaxAdjustmentCtx.put("orderId", orderId);
					 createTaxAdjustmentCtx.put("orderAdjustmentTypeId", orderAdjTypeId);    	
					 createTaxAdjustmentCtx.put("amount",amount );
			    	 result = dispatcher.runSync("createOrderAdjustment", createTaxAdjustmentCtx);
			    	 Debug.log("===result==After===adjustment=creation"+result);
			     	 if (ServiceUtil.isError(result)) {
			                Debug.logWarning("There was an error while creating  tax adjustment: " + ServiceUtil.getErrorMessage(result), module);
			         		return ServiceUtil.returnError("There was an error while creating tax adjustment: " + ServiceUtil.getErrorMessage(result));          	            
			         }
				}
			}
		
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
        result = ServiceUtil.returnSuccess("Successfully added the adjustment!!");
        return result;
    }
}
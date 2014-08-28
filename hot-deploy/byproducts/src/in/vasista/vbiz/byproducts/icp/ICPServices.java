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
	  	String orderTaxType = (String) context.get("orderTaxType");
	  	String packingType = (String) context.get("packingType");
	  	String partyId = (String) context.get("partyId");
	  	String orderId = (String) context.get("orderId");
	  	String PONumber = (String) context.get("PONumber");
	  	String currencyUomId = "INR";
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		boolean batchNumExists = Boolean.FALSE;
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
		
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
		
		try {
			cart.setOrderType("SALES_ORDER");
	        cart.setIsEnableAcctg("Y");
	        cart.setExternalId(PONumber);
	        cart.setProductStoreId(productStoreId);
			cart.setChannelType(salesChannel);
			cart.setBillToCustomerPartyId(partyId);
			cart.setPlacingCustomerPartyId(partyId);
			cart.setShipToCustomerPartyId(partyId);
			cart.setEndUserCustomerPartyId(partyId);
			//cart.setShipmentId(shipmentId);
			cart.setEstimatedDeliveryDate(effectiveDate);
			cart.setOrderDate(effectiveDate);
			cart.setUserLogin(userLogin, dispatcher);
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
	  	List<GenericValue> productPriceTaxCalc = FastList.newInstance();
		List<GenericValue> prodPriceType = null;
		
	  	List condsList = FastList.newInstance();
	  	
	  	/*condsList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));*/
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
		
		List productList = FastList.newInstance();
		for (Map<String, Object> prodQtyMap : productQtyList) {
			
			BigDecimal basicPrice = BigDecimal.ZERO;
			BigDecimal bedPrice = BigDecimal.ZERO;
			BigDecimal vatPrice = BigDecimal.ZERO;
			BigDecimal cstPrice = BigDecimal.ZERO;
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
			productList.add(productId);
			
			Map<String, Object> priceResult;
			Map<String, Object> priceContext = FastMap.newInstance();
			priceContext.put("userLogin", userLogin);
			priceContext.put("productId", productId);	
			if(UtilValidate.isNotEmpty(packingType)){
				priceContext.put("productPriceTypeId", packingType);
			}
			priceContext.put("priceDate", effectiveDate);
			priceContext.put("geoTax", geoTax);
			priceContext.put("productStoreId", productStoreId);
			if(UtilValidate.isNotEmpty(basicPrice) && basicPrice.compareTo(BigDecimal.ZERO)>0){
				priceContext.put("basicPrice", basicPrice);
				priceContext.put("bedPrice", bedPrice);
				priceContext.put("vatPrice", vatPrice);
				priceContext.put("cstPrice", cstPrice);
				priceResult = ByProductNetworkServices.calculateUserDefinedProductPrice(delegator, dispatcher, priceContext);
			}
			else{
				
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
			}
		}
		cart.setDefaultCheckoutOptions(dispatcher);
        ProductPromoWorker.doPromotions(cart, dispatcher);
        CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
		try {
			checkout.calcAndAddTax(productPriceTaxCalc);
		} catch (Exception e1) {
		// TODO Auto-generated catch block
			Debug.logError(e1, "Error in CalcAndAddTax",module);
		}
		Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
		orderId = (String) orderCreateResult.get("orderId");
		
		
		if(UtilValidate.isNotEmpty(orderId) && batchNumExists){
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
						productQtyList.remove(0);
					}
				}
			}catch (GenericEntityException e) {
				Debug.logError("Error in creating shipmentId for DirectOrder", module);
				return ServiceUtil.returnError("Error in creating shipmentId for DirectOrder");
			}
			
		}
		
		result.put("orderId", orderId);
		return result;
    }
	
	public static String createShipmentAndInvoiceForOrders(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String shipDateStr = (String) request.getParameter("shipDate");
		String vehicleId = (String) request.getParameter("vehicleId");
		String modeOfDespatch = (String) request.getParameter("modeOfDespatch");
		String salesChannelEnumId = (String) request.getParameter("salesChannelEnumId");
		String shipmentTypeId = (String) request.getParameter("shipmentTypeId");
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
    			orderHeader.store();
    			
    			
    			String invoiceId = "";
    			result = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", eachOrderId,"eventDate", effectiveDate,"userLogin", userLogin, "purposeTypeId", salesChannelEnumId));
    	        if (ServiceUtil.isError(result)) {
    	        	Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(result), module);
    	        	request.setAttribute("_ERROR_MESSAGE_", "There was an error while creating the invoice for order: "+eachOrderId);
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
		        //creditNote is created here
		        if("INTUNIT_TR_CHANNEL".equals(salesChannelEnumId)){
		    		  Map paymentInputMap = FastMap.newInstance();
			  		  paymentInputMap.put("userLogin", userLogin);
			  		  paymentInputMap.put("paymentTypeId", "SALES_PAYIN");
			  		 // paymentInputMap.put("paymentType", "SALES_PAYIN");
			  		  paymentInputMap.put("paymentMethodTypeId", "CREDITNOTE_PAYIN");
			  		  paymentInputMap.put("paymentPurposeType","NON_ROUTE_MKTG");
			  		  paymentInputMap.put("statusId", "PMNT_RECEIVED");
			  		  paymentInputMap.put("invoiceIds",UtilMisc.toList(invoiceId));
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
		        
    	        if(enableAdvancePaymentApp){
    		            // apply invoice if any adavance payments from this  party
    	     		Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", invoiceId,"userLogin", userLogin));
    	     		if (ServiceUtil.isError(resultPaymentApp)) {						  
    	     			Debug.logError(resultPaymentApp.toString(), module);
    			        request.setAttribute("_ERROR_MESSAGE_", "There was an error in service settleInvoiceAndPayments");
    			        TransactionUtil.rollback();
    			        return "error";
    	     		}
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
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String productStoreId = (String) request.getParameter("productStoreId");
		String orderTaxType = (String) request.getParameter("orderTaxType");
		String packingType = (String) request.getParameter("packingType");
		String orderId = (String) request.getParameter("orderId");
		String PONumber = (String) request.getParameter("PONumber");
		String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
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
		String quantityStr = null;
		String basicPriceStr = null;
		String vatPriceStr = null;
		String bedPriceStr = null;
		String cstPriceStr = null;
		Timestamp effectiveDate=null;
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal basicPrice = BigDecimal.ZERO;
		BigDecimal cstPrice = BigDecimal.ZERO;
		BigDecimal vatPrice = BigDecimal.ZERO;
		BigDecimal bedPrice = BigDecimal.ZERO;
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
	  
		List indentProductList = FastList.newInstance();
		for (int i = 0; i < rowCount; i++) {
		  
			Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.get("productId" + thisSuffix);
			}
			else {
				request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
				return "error";			  
			}
		  
			if (paramMap.containsKey("quantity" + thisSuffix)) {
				quantityStr = (String) paramMap.get("quantity" + thisSuffix);
			}
			else {
				request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
				return "error";			  
			}		  
			if (quantityStr.equals("")) {
				request.setAttribute("_ERROR_MESSAGE_", "Empty product quantity");
				return "error";	
			}
			if (paramMap.containsKey("batchNo" + thisSuffix)) {
				batchNo = (String) paramMap.get("batchNo" + thisSuffix);
			}
			
			if (paramMap.containsKey("basicPrice" + thisSuffix)) {
				basicPriceStr = (String) paramMap.get("basicPrice" + thisSuffix);
			}
			if (paramMap.containsKey("vatPrice" + thisSuffix)) {
				vatPriceStr = (String) paramMap.get("vatPrice" + thisSuffix);
			}
			if (paramMap.containsKey("bedPrice" + thisSuffix)) {
				bedPriceStr = (String) paramMap.get("bedPrice" + thisSuffix);
			}
			if (paramMap.containsKey("cstPrice" + thisSuffix)) {
				cstPriceStr = (String) paramMap.get("cstPrice" + thisSuffix);
			}
			try {
				quantity = new BigDecimal(quantityStr);
				if(UtilValidate.isNotEmpty(basicPriceStr)){
					basicPrice = new BigDecimal(basicPriceStr);
				}
				if(UtilValidate.isNotEmpty(cstPriceStr)){
					cstPrice = new BigDecimal(cstPriceStr);
				}
				if(UtilValidate.isNotEmpty(bedPriceStr)){
					bedPrice = new BigDecimal(bedPriceStr);
				}
				if(UtilValidate.isNotEmpty(vatPriceStr)){
					vatPrice = new BigDecimal(vatPriceStr);
				}
				
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
				return "error";
			} 
		
			productQtyMap.put("productId", productId);
			productQtyMap.put("quantity", quantity);
			productQtyMap.put("batchNo", batchNo);
			productQtyMap.put("basicPrice", basicPrice);
			productQtyMap.put("bedPrice", bedPrice);
			productQtyMap.put("cstPrice", cstPrice);
			productQtyMap.put("vatPrice", vatPrice);
			indentProductList.add(productQtyMap);
		}//end row count for loop
	  
		if( UtilValidate.isEmpty(indentProductList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "error";
		}
		
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("productQtyList", indentProductList);
		processOrderContext.put("partyId", partyId);
		processOrderContext.put("supplyDate", effectiveDate);
		processOrderContext.put("salesChannel", salesChannel);
		processOrderContext.put("orderTaxType", orderTaxType);
		processOrderContext.put("orderId", orderId);
		processOrderContext.put("packingType", packingType);
		processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
		processOrderContext.put("productStoreId", productStoreId);
		processOrderContext.put("PONumber", PONumber);
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
			
		}catch(GenericEntityException e){
			Debug.logError("Error in fetching shipment : "+ shipmentId, module);
			return ServiceUtil.returnError("Error in fetching shipment : "+shipmentId);
		}
		catch(GenericServiceException e){
			Debug.logError("Error cancelling invoices adn shipment : "+ shipmentId, module);
			return ServiceUtil.returnError("Error cancelling invoices adn shipment : "+shipmentId);
		}
		return result;
    }
	
}
package in.vasista.vbiz.byproducts.icp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
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

import in.vasista.vbiz.byproducts.ByProductServices;
public class icpServices {

    public static final String module = icpServices.class.getName();

   
    
	    
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
	  	String vehicleId = (String) context.get("vehicleId");
	  	String partyId = (String) context.get("partyId");
	  	String currencyUomId = "INR";
	  	String shipmentId = (String) context.get("shipmentId");
	  	String shipmentTypeId = (String) context.get("shipmentTypeId");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		String orderId = "";
		boolean batchNumExists = Boolean.FALSE;
		if(UtilValidate.isEmpty(shipmentId)){
		    
			GenericValue newDirShip = delegator.makeValue("Shipment");        	 
			newDirShip.set("estimatedShipDate", effectiveDate);
			newDirShip.set("shipmentTypeId", shipmentTypeId);
			newDirShip.set("statusId", "GENERATED");
			newDirShip.set("vehicleId", vehicleId);
			newDirShip.set("createdDate", nowTimeStamp);
			newDirShip.set("createdByUserLogin", userLogin.get("userLoginId"));
			newDirShip.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			try {
				delegator.createSetNextSeqId(newDirShip);            
				shipmentId = (String) newDirShip.get("shipmentId");
			} catch (GenericEntityException e) {
				Debug.logError("Error in creating shipmentId for DirectOrder", module);
				return ServiceUtil.returnError("Error in creating shipmentId for DirectOrder");
			}  
		}
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create order without partyId: "+ partyId, module);
			return ServiceUtil.returnError("partyId is empty");
		}
		GenericValue product =null;
		String productPriceTypeId = null;
		GenericValue shipment = null;
		try{
			shipment=delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
			
		}catch(GenericEntityException e){
			Debug.logError("Error in fetching shipment : "+ shipmentId, module);
			return ServiceUtil.returnError("Error in fetching shipment : "+shipmentId);
		}
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
		
		try {
			cart.setOrderType("SALES_ORDER");
	        cart.setIsEnableAcctg("N");
	        cart.setProductStoreId(productStoreId);
			cart.setChannelType(salesChannel);
			cart.setBillToCustomerPartyId(partyId);
			cart.setPlacingCustomerPartyId(partyId);
			cart.setShipToCustomerPartyId(partyId);
			cart.setEndUserCustomerPartyId(partyId);
			cart.setShipmentId(shipmentId);
			cart.setEstimatedDeliveryDate(effectiveDate);
			cart.setOrderDate(effectiveDate);
			cart.setUserLogin(userLogin, dispatcher);
		} catch (Exception e) {
			shipment.set("statusId", "GENERATION_FAIL");
			try{
				shipment.store();
			}catch(GenericEntityException e1){
				Debug.logError(e1, module);
				return ServiceUtil.returnError("Error in storing shipment status");
			}
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}
		String productId = "";
		BigDecimal quantity = BigDecimal.ZERO;
		String batchNo = "";
		for (Map<String, Object> prodQtyMap : productQtyList) {
			
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
			Map<String, Object> priceResult;
			Map<String, Object> priceContext = FastMap.newInstance();
			priceContext.put("userLogin", userLogin);
			priceContext.put("productStoreId", productStoreId);
			priceContext.put("productId", productId);
			priceContext.put("partyId", partyId);
			priceContext.put("priceDate", effectiveDate);
			priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);
			if (ServiceUtil.isError(priceResult)) {
					Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
					return ServiceUtil.returnError("There was an error while calculating the price");
			}
			BigDecimal totalPrice = (BigDecimal)priceResult.get("totalPrice");
			List taxList = (List)priceResult.get("taxList");
			ShoppingCartItem item = null;
			try{
				int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null,	quantity, (BigDecimal)priceResult.get("price"),
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
			
		}
		
		cart.setDefaultCheckoutOptions(dispatcher);
        ProductPromoWorker.doPromotions(cart, dispatcher);
        CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
		
		List<GenericValue> applicableTaxTypes = null;
		try {
			applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive ProductPriceType ", module);
			return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
		}
	
		List applicableTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);

		List<GenericValue> prodPriceType = null;
		List condList = FastList.newInstance();
		condList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, productPriceTypeId+"%"));
		condList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, applicableTaxTypeList));
		EntityCondition condition2 = EntityCondition.makeCondition(condList,EntityOperator.AND);
		try {
			prodPriceType = delegator.findList("ProductPriceAndType", condition2, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive ProductPriceAndType ", module);
			return ServiceUtil.returnError("Failed to retrive ProductPriceAndType " + e);
		}
		prodPriceType = EntityUtil.filterByDate(prodPriceType, effectiveDate);
		try {
			checkout.calcAndAddTax(prodPriceType);
		} catch (Exception e1) {
		// TODO Auto-generated catch block
			Debug.logError(e1, "Error in CalcAndAddTax",module);
		}
		Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
		orderId = (String) orderCreateResult.get("orderId");
		
		if(UtilValidate.isNotEmpty(orderId) && batchNumExists){
			try{
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
		// let's handle order rounding here
        // approve the order
        if (UtilValidate.isNotEmpty(orderId)) {
            boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
           	try{
           		result = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", orderId,"eventDate", effectiveDate,"userLogin", userLogin));
            	if (ServiceUtil.isError(result)) {
            		Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(result), module);
                	return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(result));          	            
                }
            	Boolean enableAdvancePaymentApp  = Boolean.FALSE;
            	try{        	 	
            		GenericValue tenantConfigEnableAdvancePaymentApp = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableAdvancePaymentApp"), true);
               		if (UtilValidate.isNotEmpty(tenantConfigEnableAdvancePaymentApp) && (tenantConfigEnableAdvancePaymentApp.getString("propertyValue")).equals("Y")) {
               			enableAdvancePaymentApp = Boolean.TRUE;
               		} 
       	        }catch (GenericEntityException e) {
       	        	Debug.logError(e, module);
       			}
          		if(context.get("enableAdvancePaymentApp") != null){
          			enableAdvancePaymentApp = (Boolean)context.get("enableAdvancePaymentApp");
           		}
       	      	if(enableAdvancePaymentApp){
       	      		Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", result.get("invoiceId"));
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
    	            // apply invoice if any adavance payments from this  party
	     			Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", (String)result.get("invoiceId"),"userLogin", userLogin));
	     			if (ServiceUtil.isError(resultPaymentApp)) {						  
	     				Debug.logError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp), module);			             
	     	            return ServiceUtil.returnError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp));  
	     		    }
        		}//end of advance payment appl   
            }catch (Exception e) {
            	Debug.logError(e, module);
                try{
                	shipment.set("statusId", "GENERATION_FAIL");
             		shipment.store();
                }catch (Exception ex) {
                	Debug.logError(e, module);        
                	return ServiceUtil.returnError(e.toString());
       			}
                return ServiceUtil.returnError(e.toString()); 
            }
        }
		result.put("orderId", orderId);
		return result;
    }


	public static String processIcpSale(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String vehicleId = (String) request.getParameter("vehicleId");
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
		String shipmentTypeId = (String) request.getParameter("shipmentTypeId");
		String subscriptionTypeId = "AM";
		String partyIdFrom = "";
		String shipmentId = "";
		Map processOrderContext = FastMap.newInstance();
		String salesChannel = (String)request.getParameter("salesChannel");
	   
		if(UtilValidate.isNotEmpty(shipmentTypeId) && shipmentTypeId.equals("RM_DIRECT_SHIPMENT") && UtilValidate.isEmpty(salesChannel)){
			salesChannel = "RM_DIRECT_CHANNEL";      	
		}
		if(UtilValidate.isNotEmpty(shipmentTypeId) && shipmentTypeId.equals("ICP_DIRECT_SHIPMENT") && UtilValidate.isEmpty(salesChannel)){
			salesChannel = "ICP_NANDINI_CHANNEL";      	
		}
	  
		String productId = null;
		String batchNo = null;
		String quantityStr = null;
		Timestamp effectiveDate=null;
		BigDecimal quantity = BigDecimal.ZERO;
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
			return "success";
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
			else {
				request.setAttribute("_ERROR_MESSAGE_", "Missing Batch Number");
				return "error";			  
			}
		  
			try {
				quantity = new BigDecimal(quantityStr);
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
				return "error";
			} 
		
			productQtyMap.put("productId", productId);
			productQtyMap.put("quantity", quantity);
			productQtyMap.put("batchNo", batchNo);
			indentProductList.add(productQtyMap);
		}//end row count for loop
	  
		if( UtilValidate.isEmpty(indentProductList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		String productStoreId = (String) (getIceCreamFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
	 
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("productQtyList", indentProductList);
		processOrderContext.put("partyId", partyId);
		processOrderContext.put("supplyDate", effectiveDate);
		processOrderContext.put("salesChannel", salesChannel);
		processOrderContext.put("vehicleId", vehicleId);
		processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
		processOrderContext.put("shipmentTypeId", shipmentTypeId);
		processOrderContext.put("productStoreId", productStoreId);

		result = processICPSaleOrder(dctx, processOrderContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
			return "error";
		}
		
		request.setAttribute("_EVENT_MESSAGE_", "Entry successful for party: "+partyId);	  	 
		return "success";
	}
    
}
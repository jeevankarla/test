package in.vasista.vbiz.purchase;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.sf.json.JSONObject;

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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;

import org.ofbiz.base.conversion.JSONConverters.JSONToList;

public class MaterialPurchaseServices {

	public static final String module = MaterialPurchaseServices.class.getName();
	
	public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
	public static final BigDecimal ONE_BASE = BigDecimal.ONE;
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
	public static int salestaxFinalDecimals = UtilNumber.getBigDecimalScale("salestax.final.decimals");
	public static int salestaxCalcDecimals = 2;//UtilNumber.getBigDecimalScale("salestax.calc.decimals");
	
	public static int salestaxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
	

	public static String processReceiptItems(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String receiptDateStr = (String) request.getParameter("receiptDate");
	    String orderId = (String) request.getParameter("orderId");
	    String vehicleId = (String) request.getParameter("vehicleId");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		if (UtilValidate.isEmpty(orderId)) {
			Debug.logError("Cannot process receipts without orderId: "+ orderId, module);
			return "error";
		}
		
		Timestamp receiptDate = null;
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
	  	if(UtilValidate.isNotEmpty(receiptDateStr)){
	  		try {
	  			receiptDate = new java.sql.Timestamp(sdf.parse(receiptDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	}
	  	}
	  	else{
	  		receiptDate = UtilDateTime.nowTimestamp();
	  	}
		boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			GenericValue newEntity = delegator.makeValue("Shipment");
	        newEntity.set("estimatedShipDate", receiptDate);
	        newEntity.set("shipmentTypeId", "MATERIAL_SHIPMENT");
	        newEntity.set("statusId", "GENERATED");
	        newEntity.put("vehicleId",vehicleId);
	        newEntity.put("primaryOrderId",orderId);
	        newEntity.set("createdDate", nowTimeStamp);
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            delegator.createSetNextSeqId(newEntity);            
            String shipmentId = (String) newEntity.get("shipmentId");
	       
	        List productList = FastList.newInstance();
			
			/*List<Map> prodQtyList = FastList.newInstance();*/
			
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			
			productList = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> orderRole = delegator.findList("OrderRole", condition, null, null, null, false);
			
			if(UtilValidate.isEmpty(orderRole)){
				Debug.logError("No Vendor for the order : "+orderId, module);
				request.setAttribute("_ERROR_MESSAGE_", "No Vendor for the order : "+orderId);	
				TransactionUtil.rollback();
		  		return "error";
			}
			
			String supplierId = (EntityUtil.getFirst(orderRole)).getString("partyId");
			
			List<GenericValue> productsFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.IN, productList), null, null, null, false);

			
			for (int i = 0; i < rowCount; i++) {
				
				String productId = "";
		        String quantityStr = "";
				BigDecimal quantity = BigDecimal.ZERO;
				Map productQtyMap = FastMap.newInstance();
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
				if(UtilValidate.isNotEmpty(quantityStr)){
					quantity = new BigDecimal(quantityStr);
				}
				/*productList.add(productId);
				productQtyMap.put("productId", productId);
				productQtyMap.put("quantity", quantity);
				prodQtyList.add(productQtyMap);
				*/
				Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("shipmentId",shipmentId);
		        itemInMap.put("userLogin",userLogin);
		        itemInMap.put("productId",productId);
		        itemInMap.put("quantity",quantity);
		        Map resultMap = dispatcher.runSync("createShipmentItem",itemInMap);
		        
		        if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError("Problem creating shipment Item for orderId :"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem creating shipment Item for orderId :"+orderId);	
					TransactionUtil.rollback();
			  		return "error";
		        }
		        
		        String shipmentItemSeqId = (String)resultMap.get("shipmentItemSeqId");
		        
				List<GenericValue> filteredOrderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				GenericValue ordItm = null;
				if(UtilValidate.isNotEmpty(filteredOrderItem)){
					ordItm = EntityUtil.getFirst(filteredOrderItem);
				}
				
				List<GenericValue> filterProdFacility = EntityUtil.filterByCondition(productsFacility, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				GenericValue facilityProd = EntityUtil.getFirst(filterProdFacility);
				
				Map inventoryReceiptCtx = FastMap.newInstance();
				
				inventoryReceiptCtx.put("userLogin", userLogin);
				inventoryReceiptCtx.put("productId", productId);
				inventoryReceiptCtx.put("datetimeReceived", receiptDate);
				inventoryReceiptCtx.put("quantityAccepted", quantity);
				inventoryReceiptCtx.put("quantityRejected", BigDecimal.ZERO);
				inventoryReceiptCtx.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				inventoryReceiptCtx.put("ownerPartyId", "Company");
				/*inventoryReceiptCtx.put("consolidateInventoryReceive", "Y");*/
				inventoryReceiptCtx.put("facilityId", facilityProd.getString("facilityId"));
				inventoryReceiptCtx.put("unitCost", ordItm.getBigDecimal("unitPrice"));
				inventoryReceiptCtx.put("orderId", ordItm.getString("orderId"));
				inventoryReceiptCtx.put("orderItemSeqId", ordItm.getString("orderItemSeqId"));
				/*inventoryReceiptCtx.put("shipmentId", shipmentId);
				inventoryReceiptCtx.put("shipmentItemSeqId", shipmentItemSeqId);*/
				Map<String, Object> receiveInventoryResult;
				receiveInventoryResult = dispatcher.runSync("receiveInventoryProduct", inventoryReceiptCtx);
				
				if (ServiceUtil.isError(receiveInventoryResult)) {
					Debug.logWarning("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
					request.setAttribute("_ERROR_MESSAGE_", "There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult));	
					TransactionUtil.rollback();
			  		return "error";
	            }
				
				String receiptId = (String)receiveInventoryResult.get("receiptId");
				GenericValue shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
				if(UtilValidate.isNotEmpty(shipmentReceipt)){
					shipmentReceipt.set("shipmentId", shipmentId);
					shipmentReceipt.set("shipmentItemSeqId", shipmentItemSeqId);
					shipmentReceipt.store();
				}

			}
			
		}
		catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  	}
  	  	catch (GenericServiceException e) {
  	  		try {
  			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
  	  		} catch (GenericEntityException e2) {
  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  	  		}
  	  		Debug.logError("An entity engine error occurred while calling services", module);
  	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beganTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  		}
	  	}
		request.setAttribute("_EVENT_MESSAGE_", "Successfully made request entries ");
		return "success";
	}
	
	public static String CreateMaterialPOEvent(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String poNumber = (String) request.getParameter("poNumber");
	    String supplierId = (String) request.getParameter("supplierId");
	    String orderTypeId = (String) request.getParameter("orderTypeId");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
	    
	    List productQtyList = FastList.newInstance();
	    
	    Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		
		
		for (int i = 0; i < rowCount; i++) {
			String productId =null;
			String  quantityStr = null;
			String unitPriceStr = null;
			BigDecimal quantity = BigDecimal.ZERO;
			BigDecimal unitPrice = BigDecimal.ZERO;
			Map productQtyMap = FastMap.newInstance();
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
			if(UtilValidate.isNotEmpty(quantityStr)){
				quantity = new BigDecimal(quantityStr);
			}
			
			
			if (paramMap.containsKey("unitPrice" + thisSuffix)) {
				unitPriceStr = (String) paramMap.get("unitPrice" + thisSuffix);
			}
			else {
				request.setAttribute("_ERROR_MESSAGE_", "Missing product unitPrice");
				return "error";			  
			}		  
			if(UtilValidate.isNotEmpty(unitPriceStr)){
				unitPrice = new BigDecimal(unitPriceStr);
			}
			
			productQtyMap.put("productId", productId);
			productQtyMap.put("quantity", quantity);
			productQtyMap.put("unitPrice", unitPrice);
			
			productQtyList.add(productQtyMap);
		}
		try{
			Map<String,Object> inputMap = FastMap.newInstance();
			inputMap.put("orderTypeId",orderTypeId);
			inputMap.put("userLogin",userLogin);
			inputMap.put("poNumber",poNumber);
			inputMap.put("supplierId",supplierId);
			inputMap.put("productQtyList",productQtyList);
			Debug.log("inputMap=========="+inputMap);
	        Map resultMap = dispatcher.runSync("CreateMaterialPO",inputMap);
	        
	        if (ServiceUtil.isError(resultMap)) {
	        	Debug.logError("Problem creating shipment Item for orderId :", module);
				request.setAttribute("_ERROR_MESSAGE_", "Problem creating shipment Item for orderId :");	
		  		return "error";
	        }
	        request.setAttribute("_EVENT_MESSAGE_", "Successfully made PO :"+resultMap.get("orderId"));
			
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.toString());
			return "error";
		}
		
		
		return "success";
	}
		
   public static Map<String, Object> CreateMaterialPO(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		String PONumber = (String) context.get("poNumber");
		String partyId = (String) context.get("supplierId");
		String orderTypeId = (String)context.get("orderTypeId");
		List<Map<String, Object>> productQtyList = (List)context.get("productQtyList");
		if(UtilValidate.isNotEmpty("orderTypeId")){
			orderTypeId = "PURCHASE_ORDER";
		}
		
        String currencyUomId = (String) context.get("defaultOrganizationPartyCurrencyUomId");
        if(UtilValidate.isEmpty(currencyUomId)){
        	currencyUomId ="INR";
        }
        
        String channelTypeId = null;
        Timestamp estimatedDeliveryDate = (Timestamp) context.get("estimatedDeliveryDate");
		result = ServiceUtil.returnSuccess("Successfully created PO");
		
		try{
			
			ShoppingCart cart = new ShoppingCart(delegator, null, locale, currencyUomId);
			cart.setOrderType(orderTypeId);
	        //cart.setIsEnableAcctg("N");
			
	        cart.setChannelType(channelTypeId);
	        cart.setBillToCustomerPartyId(partyId);
	        cart.setPlacingCustomerPartyId(partyId);
	        cart.setShipToCustomerPartyId(partyId);
	        cart.setEndUserCustomerPartyId(partyId);
	        cart.setExternalId(PONumber);
	        cart.setProductStoreId("_NA_");
	        cart.setEstimatedDeliveryDate(estimatedDeliveryDate);
	        try {
	            cart.setUserLogin(userLogin, dispatcher);
	        } catch (Exception exc) {
	            Debug.logError("Error setting userLogin in the cart: " + exc.getMessage(), module);
	           
	    		return ServiceUtil.returnError("Error setting userLogin in the cart: " + exc.getMessage());          	            
	        }

	    	String productId = null;
	    	BigDecimal quantity = null;
	    	BigDecimal unitPrice = null;
	    	String orderId = null;
	    	if(UtilValidate.isNotEmpty(productQtyList)){
	    		//Debug.logError("empty product List", module);
	    		//return ServiceUtil.returnError("empty product List");
	    	
	    	
	    	for (Map<String, Object> prodQtyMap : productQtyList) {
	    		List taxList=FastList.newInstance();
	    		BigDecimal totalTaxAmt =  BigDecimal.ZERO;
	    		
	    		if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
	    			productId = (String)prodQtyMap.get("productId");
	    		}
	    		if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
	    			quantity = (BigDecimal)prodQtyMap.get("quantity");
	    		}
	    		if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
	    			unitPrice = (BigDecimal)prodQtyMap.get("unitPrice");
	    		}
	    		BigDecimal tempPrice = BigDecimal.ZERO;
	    		tempPrice = tempPrice.add(unitPrice);
	    		    	
	    		
	    		BigDecimal totalPrice = unitPrice;//as of now For PurchaseOrder listPrice is same like unitPrice
	    		
	    		ShoppingCartItem item = null;
	    		try{
	    			int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null,	quantity, unitPrice,
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
	   }	
	    	cart.setDefaultCheckoutOptions(dispatcher);
	        ProductPromoWorker.doPromotions(cart, dispatcher);
	        CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);

	    	//Debug.log("==freightCharges=="+freightCharges+"===");
	    	
	    	Map<String, Object> orderCreateResult=checkout.createOrder(userLogin);
	    	if (ServiceUtil.isError(orderCreateResult)) {
	    		String errMsg =  ServiceUtil.getErrorMessage(orderCreateResult);
	    		Debug.logError(errMsg, "While Creating Order",module);
	    		return ServiceUtil.returnError(" Error While Creating Order !"+errMsg);
	    	}
	    		
	    	orderId = (String) orderCreateResult.get("orderId");
	    	result.put("orderId", orderId);
	    		
	        // approve the order
	        if (UtilValidate.isNotEmpty(orderId)) {
	            boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
	        }
	        
	    } catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		return result;
	}
   	
   	public static Map<String, Object> createInvoicesForMaterialShipment(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String shipmentId = (String) context.get("shipmentId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		String invoiceId = "";
		try{
			
			Map inputCtx = FastMap.newInstance();
			
			inputCtx.put("shipmentId", shipmentId);
			inputCtx.put("userLogin", userLogin);
			result = dispatcher.runSync("createInvoicesFromShipment", inputCtx);
			if (ServiceUtil.isError(result)) {
				Debug.logError("Error creating invoice for shipment : "+shipmentId, module);
				return ServiceUtil.returnError("Error creating invoice for shipment : "+shipmentId);
			}
			List invoiceIds = (List)result.get("invoicesCreated");
			invoiceId = (String)invoiceIds.get(0);
			
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			invoice.set("shipmentId", shipmentId);
			invoice.store();
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result = ServiceUtil.returnSuccess("Successfully Created invoice  "+invoiceId);
		return result;
	}
   	
   	public static Map<String, Object> acceptReceiptQtyByQC(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String statusId = (String) context.get("statusIdTo");
		String receiptId = (String) context.get("receiptId");
		String shipmentId = (String) context.get("shipmentId");
		String shipmentItemSeqId = (String) context.get("shipmentItemSeqId");
		BigDecimal quantityAccepted = (BigDecimal) context.get("quantityAccepted");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		if(UtilValidate.isEmpty(quantityAccepted)){
			return ServiceUtil.returnError("Quantity accepted cannot be ZERO ");
		}
		if(quantityAccepted.compareTo(BigDecimal.ZERO) ==0){
			statusId = "SR_REJECTED";
		}
		try{
			
			GenericValue shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
			
			GenericValue shipmentItem = delegator.findOne("ShipmentItem", UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", shipmentItemSeqId), false);
			
			BigDecimal origReceiptQty = shipmentItem.getBigDecimal("quantity");
			BigDecimal rejectedQty = origReceiptQty.subtract(quantityAccepted);
			
			shipmentReceipt.put("quantityAccepted", quantityAccepted);
			shipmentReceipt.put("quantityRejected", rejectedQty);
			shipmentReceipt.put("statusId", statusId);
			shipmentReceipt.store();
			
			List conditionList = FastList.newInstance();
			/*conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("shipmentItemSeqId", EntityOperator.EQUALS, shipmentItemSeqId));*/
			conditionList.add(EntityCondition.makeCondition("receiptId", EntityOperator.EQUALS, receiptId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> inventoryItemDetails = delegator.findList("InventoryItemDetail", condition, null, null, null, false);
			
			
			String inventoryItemId = (EntityUtil.getFirst(inventoryItemDetails)).getString("inventoryItemId");
			Map createInvDetail = FastMap.newInstance();
			createInvDetail.put("shipmentId", shipmentId);
			createInvDetail.put("shipmentItemSeqId", shipmentItemSeqId);
			createInvDetail.put("userLogin", userLogin);
			createInvDetail.put("inventoryItemId", inventoryItemId);
			createInvDetail.put("receiptId", receiptId);
			createInvDetail.put("quantityOnHandDiff", rejectedQty.negate());
			createInvDetail.put("availableToPromiseDiff", rejectedQty.negate());
			result = dispatcher.runSync("createInventoryItemDetail", createInvDetail);
			if (ServiceUtil.isError(result)) {
				Debug.logError("Problem decrementing inventory for rejected quantity ", module);
				return ServiceUtil.returnError("Problem decrementing inventory for rejected quantity");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result = ServiceUtil.returnSuccess("Quality check passed for GRN no: "+receiptId);
		return result;
	}	
}
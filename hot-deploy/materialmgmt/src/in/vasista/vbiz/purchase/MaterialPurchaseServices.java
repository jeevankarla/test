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
	    /*
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
		*/
		//old PO flow starts from here
		String partyId = (String) request.getParameter("supplierId");
		String billFromPartyId = (String) request.getParameter("billToPartyId");
		String issueToDeptId = (String) request.getParameter("issueToDeptId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String vehicleId = (String) request.getParameter("vehicleId");
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
		String shipmentTypeId = (String) request.getParameter("shipmentTypeId");

		String freightChargesStr=(String) request.getParameter("freightCharges");
		String discountStr=(String) request.getParameter("discount");
		String PONumber=(String) request.getParameter("PONumber");
		String mrnNumber=(String) request.getParameter("mrnNumber");
		String SInvNumber=(String) request.getParameter("SInvNumber");
		String SInvoiceDateStr=(String) request.getParameter("SInvoiceDate");
		String insurenceStr=(String) request.getParameter("insurence");
		
		String packAndFowdgStr=(String) request.getParameter("packAndFowdg");
		String otherChargesStr=(String) request.getParameter("otherCharges");
			
		String subscriptionTypeId = "AM";
		String partyIdFrom = "";
		String shipmentId = "";
		Map processOrderContext = FastMap.newInstance();
		String salesChannel = (String)request.getParameter("salesChannel");
	  
		String productId = null;
		String batchNo = null;
		String quantityStr = null;
		String unitPriceStr=null;
		
		Timestamp effectiveDate=null;
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal uPrice = BigDecimal.ZERO;
		
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
		Timestamp SInvoiceDate=null;
		if (UtilValidate.isNotEmpty(SInvoiceDateStr)) { //2011-12-25 18:09:45
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
			try {
				SInvoiceDate = new java.sql.Timestamp(sdf.parse(SInvoiceDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse SupplierInvoice Date String: " + SInvoiceDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse SupplierInvoice Date String: " + SInvoiceDateStr, module);
			}
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
		BigDecimal freightCharges = BigDecimal.ZERO;
		BigDecimal discount = BigDecimal.ZERO;
		BigDecimal insurence = BigDecimal.ZERO;
		BigDecimal packAndFowdg = BigDecimal.ZERO;
		BigDecimal otherCharges = BigDecimal.ZERO;
		
		try {
			if (!freightChargesStr.equals("")) {
				freightCharges = new BigDecimal(freightChargesStr);
				}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing freightCharges string: " + freightChargesStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing freightCharges string: " + freightChargesStr);
			return "error";
		}
		try {
			if (!discountStr.equals("")) {
			discount = new BigDecimal(discountStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing discount string: " + discountStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing discount string: " + discountStr);
			return "error";
		}
		try {
			if (!insurenceStr.equals("")) {
				insurence = new BigDecimal(insurenceStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing insurence string: " + insurenceStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing insurence string: " + insurenceStr);
			return "error";
		}
		try {
			if (!packAndFowdgStr.equals("")) {
				packAndFowdg = new BigDecimal(packAndFowdgStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing packAndFowdg string: " + packAndFowdgStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing packAndFowdg string: " + packAndFowdgStr);
			return "error";
		}
		try {
			if (!otherChargesStr.equals("")) {
				otherCharges = new BigDecimal(otherChargesStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing otherCharges string: " + otherChargesStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing otherCharges string: " + otherChargesStr);
			return "error";
		}
		
		List indentProductList = FastList.newInstance();
		for (int i = 0; i < rowCount; i++) {
			
			String vatStr=null;
			String exciseStr=null;
			String bedCessStr=null;
			String bedSecCessStr=null;
			String cstStr=null;
			
			BigDecimal vat = BigDecimal.ZERO;
			BigDecimal cst = BigDecimal.ZERO;
			BigDecimal excise = BigDecimal.ZERO;
			BigDecimal bedCessAmount = BigDecimal.ZERO;
			BigDecimal bedSecCessAmount = BigDecimal.ZERO;
			
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
			
			/*if (paramMap.containsKey("batchNo" + thisSuffix)) {
				batchNo = (String) paramMap.get("batchNo" + thisSuffix);
			}
			else {
				request.setAttribute("_ERROR_MESSAGE_", "Missing Batch Number");
				return "error";			  
			}*/
			if (paramMap.containsKey("UPrice" + thisSuffix)) {
			   unitPriceStr = (String) paramMap.get("UPrice" + thisSuffix);
			}
			if (paramMap.containsKey("VAT" + thisSuffix)) {
				vatStr = (String) paramMap.get("VAT" + thisSuffix);
			}
			if (paramMap.containsKey("excise" + thisSuffix)) {
				exciseStr = (String) paramMap.get("excise" + thisSuffix);
			}
			if (paramMap.containsKey("bedCess" + thisSuffix)) {
				bedCessStr = (String) paramMap.get("bedCess" + thisSuffix);
			}
			if (paramMap.containsKey("bedSecCess" + thisSuffix)) {
				bedSecCessStr = (String) paramMap.get("bedSecCess" + thisSuffix);
			}
			
			if (paramMap.containsKey("CST" + thisSuffix)) {
				cstStr = (String) paramMap.get("CST" + thisSuffix);
			}
			//percenatge of TAXes
			
			String VatPercentStr=null;
			String ExcisePercentStr=null;
			String bedCessPercentStr=null;
			String bedSecCessPercentStr=null;
			String CSTPercentStr=null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal excisePercent=BigDecimal.ZERO;
			BigDecimal bedCessPercent=BigDecimal.ZERO;
			BigDecimal bedSecCessPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO; 
			
			if (paramMap.containsKey("VatPercent" + thisSuffix)) {
				VatPercentStr = (String) paramMap.get("VatPercent" + thisSuffix);
			}
			if (paramMap.containsKey("ExcisePercent" + thisSuffix)) {
				ExcisePercentStr = (String) paramMap.get("ExcisePercent" + thisSuffix);
			}
			if (paramMap.containsKey("bedCessPercent" + thisSuffix)) {
				bedCessPercentStr = (String) paramMap.get("bedCessPercent" + thisSuffix);
			}
			if (paramMap.containsKey("bedSecCessPercent" + thisSuffix)) {
				bedSecCessPercentStr = (String) paramMap.get("bedSecCessPercent" + thisSuffix);
			}
			if (paramMap.containsKey("CSTPercent" + thisSuffix)) {
				CSTPercentStr = (String) paramMap.get("CSTPercent" + thisSuffix);
			}
			
			try {
				quantity = new BigDecimal(quantityStr);
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
				return "error";
			}
			try {
				if (!unitPriceStr.equals("")) {
				uPrice = new BigDecimal(unitPriceStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing UnitPrice string: " + unitPriceStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing UnitPrice string: " + unitPriceStr);
				return "error";
			} 
			try {
				if (!vatStr.equals("")) {
				vat = new BigDecimal(vatStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing VAT string: " + vatStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing VAT string: " + vatStr);
				return "error";
			}
			try {
				if (!exciseStr.equals("")) {
				excise = new BigDecimal(exciseStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing excise string: " + exciseStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing excise string: " + exciseStr);
				return "error";
			}
			try {
				if (!bedCessStr.equals("")) {
					bedCessAmount = new BigDecimal(bedCessStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing bedCess string: " + bedCessStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bedCess string: " + bedCessStr);
				return "error";
			}
			try {
				if (!bedSecCessStr.equals("")) {
					bedSecCessAmount = new BigDecimal(bedSecCessStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing bedSecCess string: " + bedSecCessStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bedSecCess string: " + bedSecCessStr);
				return "error";
			}
			
			try {
				if (!cstStr.equals("")) {
				cst = new BigDecimal(cstStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing CST string: " + cstStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CST string: " + cstStr);
				return "error";
			}
			
			//percenatges population
			try {
				if (!VatPercentStr.equals("")) {
					vatPercent = new BigDecimal(VatPercentStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing VatPercent string: " + VatPercentStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing VatPercent string: " + VatPercentStr);
				return "error";
			}
			try {
				if (!ExcisePercentStr.equals("")) {
					excisePercent = new BigDecimal(ExcisePercentStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing excisePercent string: " + ExcisePercentStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing excisePercent string: " + ExcisePercentStr);
				return "error";
			}
			try {
				if (!bedCessPercentStr.equals("")) {
					bedCessPercent = new BigDecimal(bedCessPercentStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing bedCessPercent string: " + bedCessPercentStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bedCessPercent string: " + bedCessPercentStr);
				return "error";
			}
			try {
				if (!bedSecCessPercentStr.equals("")) {
					bedSecCessPercent = new BigDecimal(bedSecCessPercentStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing bedSecCessPercent string: " + bedSecCessPercentStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bedSecCessPercent string: " + bedSecCessPercentStr);
				return "error";
			}
			try {
				if (!CSTPercentStr.equals("")) {
					cstPercent = new BigDecimal(CSTPercentStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing CSTPercent string: " + CSTPercentStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + CSTPercentStr);
				return "error";
			}
			
			productQtyMap.put("productId", productId);
			productQtyMap.put("quantity", quantity);
			productQtyMap.put("unitPrice", uPrice);
			/*productQtyMap.put("vatPercentage", vat);
			productQtyMap.put("cstPercentage", cst);
			productQtyMap.put("excisePercentage", excise);*/
			productQtyMap.put("vatAmount", vat);
			productQtyMap.put("bedAmount", excise);
			productQtyMap.put("bedCessAmount",bedCessAmount );
			productQtyMap.put("bedSecCessAmount", bedSecCessAmount);
			productQtyMap.put("cstAmount", cst);
			//productQtyMap.put("batchNo", batchNo);
			productQtyMap.put("vatPercent", vatPercent);
			productQtyMap.put("excisePercent", excisePercent);
			productQtyMap.put("bedCessPercent",bedCessPercent );
			productQtyMap.put("bedSecCessPercent", bedSecCessPercent);
			productQtyMap.put("cstPercent", cstPercent);
		
			indentProductList.add(productQtyMap);
		}//end row count for loop
		if( UtilValidate.isEmpty(indentProductList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		//getting productStoreId 
		String productStoreId = (String) (in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
	 
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("productQtyList", indentProductList);
		processOrderContext.put("partyId", partyId);
		processOrderContext.put("billFromPartyId", billFromPartyId);
		processOrderContext.put("issueToDeptId", issueToDeptId);
		processOrderContext.put("supplyDate", effectiveDate);
		processOrderContext.put("salesChannel", salesChannel);
		processOrderContext.put("vehicleId", vehicleId);
		processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
		processOrderContext.put("shipmentTypeId", shipmentTypeId);
		processOrderContext.put("productStoreId", productStoreId);
		processOrderContext.put("freightCharges", freightCharges);
		processOrderContext.put("discount", discount);
		processOrderContext.put("insurence", insurence);
		processOrderContext.put("packAndFowdg", packAndFowdg);
		processOrderContext.put("otherCharges", otherCharges);
		processOrderContext.put("mrnNumber", mrnNumber);
		processOrderContext.put("PONumber", PONumber);
		processOrderContext.put("SInvNumber", SInvNumber);
		processOrderContext.put("SInvoiceDate", SInvoiceDate);
		

		result = CreateMaterialPO(dctx, processOrderContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";
		}
		
		request.setAttribute("_EVENT_MESSAGE_", "Entry successful for party: "+partyId+" and  PO :"+result.get("orderId"));	  	 
		
		return "success";
	}
		
   public static Map<String, Object> CreateMaterialPO(DispatchContext ctx,Map<String, ? extends Object> context) {
		
/*		Delegator delegator = ctx.getDelegator();
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
		
		return result;*/
		
		
		//Old PO flow starts

	    Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    List<Map> productQtyList = (List) context.get("productQtyList");
	    Timestamp supplyDate = (Timestamp) context.get("supplyDate");
	    Locale locale = (Locale) context.get("locale");
	    String productStoreId = (String) context.get("productStoreId");
	  	String salesChannel = (String) context.get("salesChannel");
	  	String vehicleId = (String) context.get("vehicleId");
	  	String partyId = (String) context.get("partyId");
	  	String billFromPartyId = (String) context.get("billFromPartyId");
		String issueToDeptId = (String) context.get("issueToDeptId");
	  	
	  	boolean beganTransaction = false;
	  	//fright Charges
	  	BigDecimal freightCharges = (BigDecimal) context.get("freightCharges");
	  	BigDecimal discount = (BigDecimal) context.get("discount");
	  	BigDecimal packAndFowdg = (BigDecimal) context.get("packAndFowdg");
		BigDecimal otherCharges = (BigDecimal) context.get("otherCharges");
		/*BigDecimal freightCharges = BigDecimal.ZERO;
		BigDecimal discount = BigDecimal.ZERO;*/
	  	String mrnNumber = (String) context.get("mrnNumber");
	  	String PONumber=(String) context.get("PONumber");
	  	String SInvNumber = (String) context.get("SInvNumber");
		BigDecimal insurence = (BigDecimal) context.get("insurence");

	  	String currencyUomId = "INR";
	  	String shipmentId = (String) context.get("shipmentId");
	  	String shipmentTypeId = (String) context.get("shipmentTypeId");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		String orderId = "";
		String billToPartyId="Company";
		if(UtilValidate.isEmpty(shipmentId)){
			GenericValue newDirShip = delegator.makeValue("Shipment");        	 
			newDirShip.set("estimatedShipDate", effectiveDate);
			newDirShip.set("shipmentTypeId", shipmentTypeId);
			newDirShip.set("statusId", "GENERATED");
			newDirShip.set("vehicleId", vehicleId);
			newDirShip.set("createdDate", nowTimeStamp);
			newDirShip.set("createdByUserLogin", userLogin.get("userLoginId"));
			newDirShip.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			/*try {
				delegator.createSetNextSeqId(newDirShip);            
				shipmentId = (String) newDirShip.get("shipmentId");
			} catch (GenericEntityException e) {
				Debug.logError("Error in creating shipmentId for DirectOrder", module);
				return ServiceUtil.returnError("Error in creating shipmentId for DirectOrder");
			}  */
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
			cart.setOrderType("PURCHASE_ORDER");
	       // cart.setIsEnableAcctg("N");
			cart.setExternalId(PONumber);
	        cart.setProductStoreId(productStoreId);
			cart.setChannelType(salesChannel);
			cart.setBillToCustomerPartyId(billToPartyId);
			cart.setPlacingCustomerPartyId(billToPartyId);
			cart.setShipToCustomerPartyId(billToPartyId);
			cart.setEndUserCustomerPartyId(billToPartyId);
			//cart.setShipmentId(shipmentId);
			//for PurchaseOrder we have to use for SupplierId
			if(UtilValidate.isNotEmpty(billFromPartyId)){
				 cart.setBillFromVendorPartyId(billFromPartyId);
			}else{
		    cart.setBillFromVendorPartyId(partyId);
			}
		    cart.setShipFromVendorPartyId(partyId);
		    cart.setSupplierAgentPartyId(partyId);
			
			cart.setEstimatedDeliveryDate(effectiveDate);
			cart.setOrderDate(effectiveDate);
			cart.setUserLogin(userLogin, dispatcher);
		} catch (Exception e) {
			
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}
		String productId = "";
		BigDecimal quantity = BigDecimal.ZERO;
		String batchNo = "";
		BigDecimal unitPrice = BigDecimal.ZERO;
		BigDecimal vat = BigDecimal.ZERO;
		BigDecimal cst = BigDecimal.ZERO;
		BigDecimal excise = BigDecimal.ZERO;
		BigDecimal bedCessAmount = BigDecimal.ZERO;
		BigDecimal bedSecCessAmount = BigDecimal.ZERO;

		List<GenericValue> prodPriceTypeList = FastList.newInstance();
		try{
		beganTransaction = TransactionUtil.begin(7200);
		
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
			if(unitPrice.compareTo(BigDecimal.ZERO)>0){
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
					excise = (BigDecimal)prodQtyMap.get("bedAmount");
			        BigDecimal excisePercent=(BigDecimal)prodQtyMap.get("excisePercent");
					
					BigDecimal taxRate = excise;
					BigDecimal taxAmount = BigDecimal.ZERO;
					
		        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
		        		//taxAmount = (unitPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
		        		taxAmount = (taxRate).setScale(salestaxCalcDecimals, salestaxRounding);
		        		Map taxDetailMap = FastMap.newInstance();
			    		taxDetailMap.put("taxType", "BED_PUR");
			    		//taxDetailMap.put("taxType", "BED_SALE");
			    		taxDetailMap.put("amount", taxAmount);
			    		taxDetailMap.put("percentage", excisePercent);
			    		taxList.add(taxDetailMap);

			    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
			    		newProdPriceType.set("fromDate", effectiveDate);
			    		newProdPriceType.set("parentTypeId", "TAX");
			    		newProdPriceType.set("productId", productId);
			    		newProdPriceType.set("productStoreGroupId", "_NA_");
			    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
			    		newProdPriceType.set("productPriceTypeId", "BED_PUR");
			    		newProdPriceType.set("taxPercentage", excisePercent);
			    		newProdPriceType.set("taxAmount", taxAmount);
			    		newProdPriceType.set("currencyUomId", "INR");
			    		prodPriceTypeList.add(newProdPriceType);
		        	}
		        	tempPrice=tempPrice.add(taxAmount);
		        	totalTaxAmt=totalTaxAmt.add(taxAmount);
				}
				/*productQtyMap.put("vatPercent", vatPercent);
				productQtyMap.put("excisePercent", excisePercent);
				productQtyMap.put("bedCessPercent",bedCessPercent );
				productQtyMap.put("bedSecCessPercent", bedSecCessPercent);
				productQtyMap.put("cstPercent", cstPercent);*/
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedCessAmount"))){
				    BigDecimal taxRate = (BigDecimal)prodQtyMap.get("bedCessAmount");
				    BigDecimal bedCessPercent=(BigDecimal)prodQtyMap.get("bedCessPercent");
					BigDecimal taxAmount = BigDecimal.ZERO;
		        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
		        		//taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
		        		taxAmount=taxRate.setScale(salestaxCalcDecimals, salestaxRounding);
		        		Map taxDetailMap = FastMap.newInstance();
			    		taxDetailMap.put("taxType", "BEDCESS_PUR");
			    		taxDetailMap.put("amount", taxAmount);
			    		taxDetailMap.put("percentage", bedCessPercent);
			    		taxList.add(taxDetailMap);
			    		
			    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
			    		newProdPriceType.set("fromDate", effectiveDate);
			    		newProdPriceType.set("parentTypeId", "TAX");
			    		newProdPriceType.set("productId", productId);
			    		newProdPriceType.set("productStoreGroupId", "_NA_");
			    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
			    		newProdPriceType.set("productPriceTypeId", "BEDCESS_PUR");
			    		newProdPriceType.set("taxPercentage", bedCessPercent);
			    		newProdPriceType.set("taxAmount", taxAmount);
			    		newProdPriceType.set("currencyUomId", "INR");
			    		prodPriceTypeList.add(newProdPriceType);
		        	}
		        	totalTaxAmt=totalTaxAmt.add(taxAmount);
				}
				
			    if(UtilValidate.isNotEmpty(prodQtyMap.get("bedSecCessAmount"))){
				    BigDecimal taxRate = (BigDecimal)prodQtyMap.get("bedSecCessAmount");
				    BigDecimal bedSecCessPercent=(BigDecimal)prodQtyMap.get("bedSecCessPercent");
					BigDecimal taxAmount = BigDecimal.ZERO;
		        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
		        		//taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
		        		taxAmount=taxRate.setScale(salestaxCalcDecimals, salestaxRounding);
		        		Map taxDetailMap = FastMap.newInstance();
			    		taxDetailMap.put("taxType", "BEDSECCESS_PUR");
			    		taxDetailMap.put("amount", taxAmount);
			    		taxDetailMap.put("percentage", bedSecCessPercent);
			    		taxList.add(taxDetailMap);
			        	
			    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
			    		newProdPriceType.set("fromDate", effectiveDate);
			    		newProdPriceType.set("parentTypeId", "TAX");
			    		newProdPriceType.set("productId", productId);
			    		newProdPriceType.set("productStoreGroupId", "_NA_");
			    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
			    		newProdPriceType.set("productPriceTypeId", "BEDSECCESS_PUR");
			    		newProdPriceType.set("taxPercentage", bedSecCessPercent);
			    		newProdPriceType.set("taxAmount", taxAmount);
			    		newProdPriceType.set("currencyUomId", "INR");
			    		prodPriceTypeList.add(newProdPriceType);
		        	}
		        	totalTaxAmt=totalTaxAmt.add(taxAmount);
				}
			    if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
				    BigDecimal taxRate = (BigDecimal)prodQtyMap.get("vatAmount");
				    BigDecimal vatPercent=(BigDecimal)prodQtyMap.get("vatPercent");
					BigDecimal taxAmount = BigDecimal.ZERO;
		        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
		        		//taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
		        		taxAmount=taxRate.setScale(salestaxCalcDecimals, salestaxRounding);
		        		Map taxDetailMap = FastMap.newInstance();
			    		//taxDetailMap.put("taxType", "VAT_SALE");
			    		taxDetailMap.put("taxType", "VAT_PUR");
			    		taxDetailMap.put("amount", taxAmount);
			    		taxDetailMap.put("percentage", vatPercent);
			    		taxList.add(taxDetailMap);
			        	/*if(taxPrice.compareTo(BigDecimal.ZERO)>0){
			        		taxAmount = itemQuantity.multiply(taxPrice).setScale(salestaxCalcDecimals, salestaxRounding);
			        	}*/
			    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
			    		newProdPriceType.set("fromDate", effectiveDate);
			    		newProdPriceType.set("parentTypeId", "TAX");
			    		newProdPriceType.set("productId", productId);
			    		newProdPriceType.set("productStoreGroupId", "_NA_");
			    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
			    		newProdPriceType.set("productPriceTypeId", "VAT_PUR");
			    		newProdPriceType.set("taxPercentage", vatPercent);
			    		newProdPriceType.set("taxAmount", taxAmount);
			    		newProdPriceType.set("currencyUomId", "INR");
			    		prodPriceTypeList.add(newProdPriceType);
		        	}
		        	totalTaxAmt=totalTaxAmt.add(taxAmount);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
					cst = (BigDecimal)prodQtyMap.get("cstAmount");
					BigDecimal cstPercent=(BigDecimal)prodQtyMap.get("cstPercent");
					BigDecimal taxRate = cst;
					BigDecimal taxAmount = BigDecimal.ZERO;
		        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
		        		//taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
		        		taxAmount=taxRate.setScale(salestaxCalcDecimals, salestaxRounding);
		        		Map taxDetailMap = FastMap.newInstance();
			    		//taxDetailMap.put("taxType", "CST_SALE");
			    		taxDetailMap.put("taxType", "CST_PUR");
			    		taxDetailMap.put("amount", taxAmount);
			    		taxDetailMap.put("percentage", cstPercent);
			    		taxList.add(taxDetailMap);
			    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
			    		newProdPriceType.set("fromDate", effectiveDate);
			    		newProdPriceType.set("parentTypeId", "TAX");
			    		newProdPriceType.set("productId", productId);
			    		newProdPriceType.set("productStoreGroupId", "_NA_");
			    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
			    		newProdPriceType.set("productPriceTypeId", "CST_PUR");
			    		newProdPriceType.set("taxPercentage", cstPercent);
			    		newProdPriceType.set("taxAmount", taxAmount);
			    		newProdPriceType.set("currencyUomId", "INR");
			    		prodPriceTypeList.add(newProdPriceType);
		        	}
		        	totalTaxAmt=totalTaxAmt.add(taxAmount);
				}
			}
		
			//BigDecimal totalPrice = unitPrice.add(totalTaxAmt);
			
			BigDecimal totalPrice = unitPrice;//as of now For PurchaseOrder listPrice is same like unitPrice
			
			//BigDecimal totalTaxAmt = BigDecimal.ZERO;
			Debug.log("==totalPrice==="+totalPrice+"==totalTaxAmt="+totalTaxAmt+"=unitPrice="+unitPrice);
			//List taxList = (List)priceResult.get("taxList");
			//Debug.log("=========taxList====="+taxList);
			//Debug.log("==prodPriceTypeList=====>"+prodPriceTypeList);
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
		cart.setDefaultCheckoutOptions(dispatcher);
	    ProductPromoWorker.doPromotions(cart, dispatcher);
	    CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
		
		try {
			//checkout.calcAndAddTax(prodPriceTypeList);
			checkout.calcAndAddTaxPurchase(prodPriceTypeList);
		} catch (Exception e1) {
		// TODO Auto-generated catch block
			Debug.logError(e1, "Error in CalcAndAddTax",module);
			return ServiceUtil.returnError(" Error While Creating Adjustment for Purchase Order !");
		}
		
		Map<String, Object> orderCreateResult=checkout.createOrder(userLogin);
		Debug.log("===orderCreateResult=====>"+orderCreateResult);
		if (ServiceUtil.isError(orderCreateResult)) {
			String errMsg =  ServiceUtil.getErrorMessage(orderCreateResult);
			Debug.logError(errMsg, "While Creating Order",module);
			return ServiceUtil.returnError(" Error While Creating Order !"+errMsg);
		}
			
		orderId = (String) orderCreateResult.get("orderId");
		//let's create Fright Adhustment here
		// handle employee subsidies here 
	    //if(productSubscriptionTypeId.equals("EMP_SUBSIDY")){
			if(freightCharges.compareTo(BigDecimal.ZERO)>0){
		    	Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
		    	adjustCtx.put("orderId", orderId);
		    	adjustCtx.put("adjustmentTypeId", "FREIGHT_CHARGES");
		    	adjustCtx.put("adjustmentAmount", freightCharges);
		    	Map adjResultMap=FastMap.newInstance();
			  	 	try{
			  	 		 adjResultMap = dispatcher.runSync("createAdjustmentForPurchaseOrder",adjustCtx);  		  		 
			  	 		if (ServiceUtil.isError(adjResultMap)) {
			  	 			String errMsg =  ServiceUtil.getErrorMessage(adjResultMap);
			  	 			Debug.logError(errMsg , module);
			  	 		 return ServiceUtil.returnError(" Error While Creating Adjustment for Purchase Order !");
			  	 		}
			  	 	}catch (Exception e) {
			  			  Debug.logError(e, "Error While Creating Adjustment for Purchase Order ", module);
			  			  return adjResultMap;			  
			  	 	}
		    }
			if(discount.compareTo(BigDecimal.ZERO)>0){
		    	Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
		    	adjustCtx.put("orderId", orderId);
		    	adjustCtx.put("adjustmentTypeId", "DISCOUNT");
		    	adjustCtx.put("adjustmentAmount", discount);
		    	Map adjResultMap=FastMap.newInstance();
			  	 	try{
			  	 		 adjResultMap = dispatcher.runSync("createAdjustmentForPurchaseOrder",adjustCtx);  		  		 
			  	 		if (ServiceUtil.isError(adjResultMap)) {
			  	 			String errMsg =  ServiceUtil.getErrorMessage(adjResultMap);
			  	 			Debug.logError(errMsg , module);
			  	 		 return ServiceUtil.returnError(" Error While discount Adjustment for Purchase Order !");
			  	 		}
			  	 	}catch (Exception e) {
			  			  Debug.logError(e, "Error While Creating discount Adjustment for Purchase Order ", module);
			  			  return adjResultMap;			  
			  	 	}
		    }
			if(insurence.compareTo(BigDecimal.ZERO)>0){
		    	Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
		    	adjustCtx.put("orderId", orderId);
		    	adjustCtx.put("adjustmentTypeId", "INSURENCE");
		    	adjustCtx.put("adjustmentAmount", insurence);
		    	Map adjResultMap=FastMap.newInstance();
			  	 	try{
			  	 		 adjResultMap = dispatcher.runSync("createAdjustmentForPurchaseOrder",adjustCtx);  		  		 
			  	 		if (ServiceUtil.isError(adjResultMap)) {
			  	 			String errMsg =  ServiceUtil.getErrorMessage(adjResultMap);
			  	 			Debug.logError(errMsg , module);
			  	 		 return ServiceUtil.returnError(" Error While discount Adjustment for Purchase Order !");
			  	 		}
			  	 	}catch (Exception e) {
			  			  Debug.logError(e, "Error While Creating discount Adjustment for Purchase Order ", module);
			  			  return adjResultMap;			  
			  	 	}
		    }
			if(packAndFowdg.compareTo(BigDecimal.ZERO)>0){
		    	Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
		    	adjustCtx.put("orderId", orderId);
		    	adjustCtx.put("adjustmentTypeId", "PACKAndFOWDG");
		    	adjustCtx.put("adjustmentAmount", packAndFowdg);
		    	Map adjResultMap=FastMap.newInstance();
			  	 	try{
			  	 		 adjResultMap = dispatcher.runSync("createAdjustmentForPurchaseOrder",adjustCtx);  		  		 
			  	 		if (ServiceUtil.isError(adjResultMap)) {
			  	 			String errMsg =  ServiceUtil.getErrorMessage(adjResultMap);
			  	 			Debug.logError(errMsg , module);
			  	 		 return ServiceUtil.returnError(" Error While packAndFowdg Adjustment for Purchase Order !");
			  	 		}
			  	 	}catch (Exception e) {
			  			  Debug.logError(e, "Error While Creating packAndFowdg Adjustment for Purchase Order ", module);
			  			  return adjResultMap;			  
			  	 	}
		    }
			if(otherCharges.compareTo(BigDecimal.ZERO)>0){
		    	Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
		    	adjustCtx.put("orderId", orderId);
		    	adjustCtx.put("adjustmentTypeId", "OTHERCHARGES"); 
		    	adjustCtx.put("adjustmentAmount", otherCharges);
		    	Map adjResultMap=FastMap.newInstance();
			  	 	try{
			  	 		 adjResultMap = dispatcher.runSync("createAdjustmentForPurchaseOrder",adjustCtx);  		  		 
			  	 		if (ServiceUtil.isError(adjResultMap)) {
			  	 			String errMsg =  ServiceUtil.getErrorMessage(adjResultMap);
			  	 			Debug.logError(errMsg , module);
			  	 		 return ServiceUtil.returnError(" Error While otherCharges Adjustment for Purchase Order !");
			  	 		}
			  	 	}catch (Exception e) {
			  			  Debug.logError(e, "Error While Creating otherCharges Adjustment for Purchase Order ", module);
			  			  return adjResultMap;			  
			  	 	}
		    }
			
			/*String mrnNumber = (String) context.get("mrnNumber");
		  	String PONumber=(String) context.get("PONumber");
		  	String SInvNumber = (String) context.get("SInvNumber");
		  	
		  	*/
			//before save OrderRole save partyRole
			if(UtilValidate.isNotEmpty(issueToDeptId)){
				try{
					GenericValue issuePartyRole	=delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", issueToDeptId, "roleTypeId", "ISSUE_TO_DEPT"));
					delegator.createOrStore(issuePartyRole);
					}catch (Exception e) {
						  Debug.logError(e, "Error While Creating PartyRole(ISSUE_TO_DEPT)  for Purchase Order ", module);
						  return ServiceUtil.returnError("Error While Creating PartyRole(ISSUE_TO_DEPT)  for Purchase Order : "+orderId);
			  	 	}
					//creating OrderRole for issue to Dept
					try{
					GenericValue issueOrderRole	=delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", issueToDeptId, "roleTypeId", "ISSUE_TO_DEPT"));
					delegator.createOrStore(issueOrderRole);
					}catch (Exception e) {
						  Debug.logError(e, "Error While Creating OrderRole(ISSUE_TO_DEPT)  for Purchase Order ", module);
						  return ServiceUtil.returnError("Error While Creating OrderRole(ISSUE_TO_DEPT)  for Purchase Order : "+orderId);
			  	 	}
			}
			
			try{
			GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
			orderAttribute.set("orderId", orderId);
			orderAttribute.set("attrName", "MRN_NUMBER");
			orderAttribute.set("attrValue", mrnNumber);
			delegator.createOrStore(orderAttribute);
			}catch (Exception e) {
				  Debug.logError(e, "Error While Creating Attribute(MRN_NUMBER)  for Purchase Order ", module);
				  return ServiceUtil.returnError("Error While Creating Attribute(MRN_NUMBER)  for Purchase Order : "+orderId);
	  	 	}
			try{
				GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
				orderAttribute.set("orderId", orderId);
				orderAttribute.set("attrName", "SUP_INV_NUMBER");
				orderAttribute.set("attrValue", SInvNumber);
				delegator.createOrStore(orderAttribute);
				}catch (Exception e) {
					  Debug.logError(e, "Error While Creating Attribute(SUP_INV_NUMBER)  for Purchase Order ", module);
					  return ServiceUtil.returnError("Error While Creating Attribute(SUP_INV_NUMBER)  for Purchase Order : "+orderId);
		  	 	}
				//supplier invoice date
				try{
					GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
					orderAttribute.set("orderId", orderId);
					orderAttribute.set("attrName", "SUP_INV_DATE");
					orderAttribute.set("attrValue", UtilDateTime.toDateString((Timestamp)context.get("SInvoiceDate"),null));
					delegator.createOrStore(orderAttribute);
					}catch (Exception e) {
						  Debug.logError(e, "Error While Creating Attribute(SUP_INV_DATE)  for Purchase Order ", module);
						  return ServiceUtil.returnError("Error While Creating Attribute(SUP_INV_DATE)  for Purchase Order : "+orderId);
			  	 	}
				
			try{
				GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
				orderAttribute.set("orderId", orderId);
				orderAttribute.set("attrName", "PO_NUMBER");
				orderAttribute.set("attrValue", PONumber);
				delegator.createOrStore(orderAttribute);
				}catch (Exception e) {
					  Debug.logError(e, "Error While Creating Attribute(PO_NUMBER)  for Purchase Order ", module);
					  return ServiceUtil.returnError("Error While Creating Attribute(PO_NUMBER)  for Purchase Order : "+orderId);
		  	 	}
				//update PurposeType
				try{
				GenericValue orderHeaderPurpose = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				orderHeaderPurpose.set("purposeTypeId", salesChannel);
				orderHeaderPurpose.store();
				}catch (Exception e) {
					  Debug.logError(e, "Error While Updating purposeTypeId for Order ", module);
					  return ServiceUtil.returnError("Error While Updating purposeTypeId for Order : "+orderId);
		  	 	}
				
			
		// let's handle order rounding here
	    try{   
	    	Map roundAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
	    	roundAdjCtx.put("orderId", orderId);
	  	 	result = dispatcher.runSync("adjustRoundingDiffForOrder",roundAdjCtx);  		  		 
	  	 	if (ServiceUtil.isError(result)) {
	  	 		String errMsg =  ServiceUtil.getErrorMessage(result);
	  	 		Debug.logError(errMsg , module);
		      	  	return ServiceUtil.returnError(errMsg+"==Error While  Rounding Order !");
		 		}
		 	}catch (Exception e) {
		 		Debug.logError(e, "Error while Creating Order", module);
	        return ServiceUtil.returnError(e+"==Error While  Rounding Order !");
	  		//return resultMap;			  
	  	}
	    // approve the order
	    if (UtilValidate.isNotEmpty(orderId)) {
	        boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
	        
	       	/*try{
	       		result = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", orderId,"eventDate", effectiveDate,"userLogin", userLogin));
	        	if (ServiceUtil.isError(result)) {
	        		Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(result), module);
	            	return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(result));          	            
	            }
	        	Debug.log("result invoiceId  #################################"+result);
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
		            //use Tenant configuration
		            Boolean enablePurchseInvoiceReady  = Boolean.FALSE;
		        	try{        	 	
		        		GenericValue tenantConfigEnableAdvancePaymentApp = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","PURCHASE_OR_STORES", "propertyName","Enable-PurchseInv-ReadyStatus"), true);
		           		if (UtilValidate.isNotEmpty(tenantConfigEnableAdvancePaymentApp) && (tenantConfigEnableAdvancePaymentApp.getString("propertyValue")).equals("Y")) {
		           			enableAdvancePaymentApp = Boolean.TRUE;
		           		} 
		   	        }catch (GenericEntityException e) {
		   	        	Debug.logError(e, module);
		   			}
			   	 	if(enablePurchseInvoiceReady){
			   	 	 invoiceCtx.put("statusId","INVOICE_READY");
			   	 	}else{
			   	 	 invoiceCtx.put("statusId","INVOICE_IN_PROCESS");
			   	 	}
		           
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
		            //raising DebitNote Here for UNITS PURCHASES  
		            
		            if("INTER_PRCHSE_CHANNEL".equals(salesChannel)){
			    		  Map paymentInputMap = FastMap.newInstance();
				  		  paymentInputMap.put("userLogin", userLogin);
				  		  paymentInputMap.put("paymentTypeId", "EXPENSE_PAYOUT");
				  		 // paymentInputMap.put("paymentType", "SALES_PAYIN");
				  		  paymentInputMap.put("paymentMethodTypeId", "DEBITNOTE_TRNSF");
				  		  paymentInputMap.put("paymentPurposeType","INTER_PRCHSE_CHANNEL");
				  		  paymentInputMap.put("statusId", "PMNT_NOT_PAID");
				  		  paymentInputMap.put("invoiceIds",UtilMisc.toList((String)result.get("invoiceId")));
				  		  Map paymentResult = dispatcher.runSync("createCreditNoteOrDebitNoteForInvoice", paymentInputMap);
				  		  if(ServiceUtil.isError(paymentResult)){
			    			     Debug.logError(paymentResult.toString(), module);
			    			     return ServiceUtil.returnError("There was an error in service createCreditNoteOrDebitNoteForInvoice" + ServiceUtil.getErrorMessage(paymentResult));  
				  		  }
				  		  List paymentIds = (List)paymentResult.get("paymentsList");
	  	                  Debug.log("+++++++===paymentIds===AfterDEbitNote=="+paymentIds);
			        }else{
		     			Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", (String)result.get("invoiceId"),"userLogin", userLogin));
		     			if (ServiceUtil.isError(resultPaymentApp)) {						  
		     				Debug.logError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp), module);			             
		     	            return ServiceUtil.returnError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp));  
		     		    }
			        }
	    		}//end of advance payment appl   
	        }catch (Exception e) {
	        	Debug.logError(e, module);
	            return ServiceUtil.returnError(e.toString()); 
	        }*/
	    }
		}catch(Exception e){
			try {
				// only rollback the transaction if we started one...
	  			TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
			} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		}
			return ServiceUtil.returnError(e.toString()); 
		}
		finally {
	  		  // only commit the transaction if we started one... this will throw an exception if it fails
	  		  try {
	  			  TransactionUtil.commit(beganTransaction);
	  		  } catch (GenericEntityException e) {
	  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  		  }
	  	}
		result.put("orderId", orderId);
		Debug.log("result successful  #################################");
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
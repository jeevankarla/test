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
import java.util.Map.Entry;

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
	    String supplierInvoiceId = (String) request.getParameter("supplierInvoiceId");
	    String supplierInvoiceDateStr = (String) request.getParameter("supplierInvoiceDate");
	    String withoutPO = (String) request.getParameter("withoutPO");
	    //GRN on PO then override this supplier with PO supplier
	    String supplierId = (String) request.getParameter("supplierId");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		if (UtilValidate.isEmpty(orderId) && UtilValidate.isEmpty(withoutPO)) {
			Debug.logError("Cannot process receipts without orderId: "+ orderId, module);
			return "error";
		}
		
		Timestamp receiptDate = null;
		Timestamp supplierInvoiceDate = null;
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
		receiptDate = UtilDateTime.nowTimestamp();
	  	if(UtilValidate.isNotEmpty(receiptDateStr)){
	  		try {
	  			receiptDate = new java.sql.Timestamp(sdf.parse(receiptDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	}
	  	}
	  	
	  	if(UtilValidate.isNotEmpty(supplierInvoiceDateStr)){
	  		try {
	  			supplierInvoiceDate = new java.sql.Timestamp(sdf.parse(supplierInvoiceDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + supplierInvoiceDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + supplierInvoiceDateStr, module);
		  	}
	  	}
	  	
	  	boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			GenericValue newEntity = delegator.makeValue("Shipment");
	        newEntity.set("estimatedShipDate", receiptDate);
	        newEntity.set("shipmentTypeId", "MATERIAL_SHIPMENT");
	        newEntity.set("statusId", "GENERATED");
	        newEntity.put("vehicleId",vehicleId);
	        newEntity.put("supplierInvoiceId",supplierInvoiceId);
	        newEntity.put("supplierInvoiceDate",supplierInvoiceDate);
	        newEntity.put("primaryOrderId",orderId);
	        newEntity.set("createdDate", nowTimeStamp);
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            delegator.createSetNextSeqId(newEntity);            
            String shipmentId = (String) newEntity.get("shipmentId");
	       
	        List productList = FastList.newInstance();
			
			/*List<Map> prodQtyList = FastList.newInstance();*/
	        List<GenericValue> orderItems = FastList.newInstance();
			if(UtilValidate.isNotEmpty(orderId)){
				orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
				
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
				
			  supplierId = (EntityUtil.getFirst(orderRole)).getString("partyId");
			}
			
			
			
			
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
		        List<GenericValue> productsFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
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
				inventoryReceiptCtx.put("ownerPartyId", supplierId);
				/*inventoryReceiptCtx.put("consolidateInventoryReceive", "Y");*/
				inventoryReceiptCtx.put("facilityId", facilityProd.getString("facilityId"));
				inventoryReceiptCtx.put("unitCost", BigDecimal.ZERO);
				if(UtilValidate.isNotEmpty(ordItm)){
					inventoryReceiptCtx.put("unitCost", ordItm.getBigDecimal("unitPrice"));
					inventoryReceiptCtx.put("orderId", ordItm.getString("orderId"));
					inventoryReceiptCtx.put("orderItemSeqId", ordItm.getString("orderItemSeqId"));
				}
				
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
	
	public static String processPurchaseInvoice(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String vehicleId = (String) request.getParameter("vehicleId");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");

		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purposeTypeId = "MATERIAL_PUR_CHANNEL";
	  
		
		Timestamp invoiceDate = null;
		Timestamp suppInvDate = null;
		
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if (UtilValidate.isNotEmpty(invoiceDateStr)) { //2011-12-25 18:09:45
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
			try {
				invoiceDate = new java.sql.Timestamp(sdf.parse(invoiceDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + invoiceDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + invoiceDateStr, module);
			}
		}
		else{
			invoiceDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
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
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		
		List productQtyList = FastList.newInstance();
		List invoiceAdjChargesList = FastList.newInstance();
		for (int i = 0; i < rowCount; i++) {
			
			Map prodQtyMap = FastMap.newInstance();
			Map invItemMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
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
			
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productId = "";
			String quantityStr = "";
			String unitPriceStr = "";
			
			BigDecimal quantity = BigDecimal.ZERO;
			BigDecimal uPrice = BigDecimal.ZERO;
			
			if (paramMap.containsKey("invoiceItemTypeId" + thisSuffix)) {
				invoiceItemTypeId = (String) paramMap.get("invoiceItemTypeId" + thisSuffix);
			}
			
			if(UtilValidate.isNotEmpty(invoiceItemTypeId)){
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
				
				invItemMap.put("invoiceItemTypeId", invoiceItemTypeId);
				invItemMap.put("adjAmount", adjAmt);
				invoiceAdjChargesList.add(invItemMap);	

			}
			
			
			
			
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.get("productId" + thisSuffix);
			}
			
			if(UtilValidate.isNotEmpty(productId)){
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
				}
				if(UtilValidate.isEmpty(quantityStr)){
					request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
					return "error";	
				}
				
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
			}
			
			
			if(UtilValidate.isNotEmpty(productId)){
				prodQtyMap.put("productId", productId);
				prodQtyMap.put("quantity", quantity);
				prodQtyMap.put("unitPrice", uPrice);
				prodQtyMap.put("vatAmount", vat);
				prodQtyMap.put("bedAmount", excise);
				prodQtyMap.put("bedCessAmount",bedCessAmount );
				prodQtyMap.put("bedSecCessAmount", bedSecCessAmount);
				prodQtyMap.put("cstAmount", cst);
				prodQtyMap.put("vatPercent", vatPercent);
				prodQtyMap.put("excisePercent", excisePercent);
				prodQtyMap.put("bedCessPercent",bedCessPercent );
				prodQtyMap.put("bedSecCessPercent", bedSecCessPercent);
				prodQtyMap.put("cstPercent", cstPercent);
			
				productQtyList.add(prodQtyMap);
			}
				
		}//end row count for loop
		if( UtilValidate.isEmpty(productQtyList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		
		Map processInvoiceContext = FastMap.newInstance();
		processInvoiceContext.put("userLogin", userLogin);
		processInvoiceContext.put("productQtyList", productQtyList);
		processInvoiceContext.put("partyId", partyId);
		processInvoiceContext.put("purposeTypeId", purposeTypeId);
		processInvoiceContext.put("vehicleId", vehicleId);
		processInvoiceContext.put("orderId", orderId);
		processInvoiceContext.put("shipmentId", shipmentId);
		processInvoiceContext.put("invoiceDate", invoiceDate);
		processInvoiceContext.put("invoiceAdjChargesList", invoiceAdjChargesList);
		result = createMaterialInvoice(dctx, processInvoiceContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate invoice  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";
		}
		
		String invoiceId =  (String)result.get("invoiceId");
		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId);	  	 
		
		return "success";
	}
	
	public static Map<String, Object> createMaterialInvoice(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		    Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    Map<String, Object> result = ServiceUtil.returnSuccess();
		    List<Map> productQtyList = (List) context.get("productQtyList");
		    List<Map> invoiceAdjChargesList = (List) context.get("invoiceAdjChargesList");
		    Timestamp invoiceDate = (Timestamp) context.get("invoiceDate");
		    Locale locale = (Locale) context.get("locale");
		  	String purposeTypeId = (String) context.get("purposeTypeId");
		  	String vehicleId = (String) context.get("vehicleId");
		  	String partyIdFrom = (String) context.get("partyId");
		  	String orderId = (String) context.get("orderId");
		  	String shipmentId = (String) context.get("shipmentId");
		  	boolean beganTransaction = false;
		  	String currencyUomId = "INR";
			Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
			String partyId="Company";
			
			if (UtilValidate.isEmpty(partyIdFrom)) {
				Debug.logError("Cannot create order without partyId: "+ partyIdFrom, module);
				return ServiceUtil.returnError("partyId is empty");
			}

			try{
				beganTransaction = TransactionUtil.begin(7200);
				
				if(UtilValidate.isEmpty(shipmentId)){
					Debug.logError("ShipmentId required to create invoice ", module);
					return ServiceUtil.returnError("ShipmentId required to create invoice ");
				}
				
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
				EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> shipmentReceipts = delegator.findList("Invoice", condExpr, null, null, null, false);
				if(UtilValidate.isNotEmpty(shipmentReceipts)){
					Debug.logError("GRN not found for the shipment: "+shipmentId, module);
					return ServiceUtil.returnError("GRN not found for the shipment: "+shipmentId);
				}
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF")));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> invoices = delegator.findList("Invoice", condition, null, null, null, false);
				
				if(UtilValidate.isNotEmpty(invoices)){
					Debug.logError("Invoices already generated for shipment : "+shipmentId, module);
					return ServiceUtil.returnError("Invoices already generated for shipment : "+shipmentId);
				}
				
				Map input = UtilMisc.toMap("userLogin", userLogin);
		        input.put("invoiceTypeId", "PURCHASE_INVOICE");        
		        input.put("partyIdFrom", partyIdFrom);	
		        input.put("statusId", "INVOICE_IN_PROCESS");	
		        input.put("currencyUomId", currencyUomId);
		        input.put("invoiceDate", invoiceDate);
		        input.put("dueDate", invoiceDate); 	        
		        input.put("partyId", partyId);
		        input.put("purposeTypeId", purposeTypeId);
		        input.put("createdByUserLogin", userLogin.getString("userLoginId"));
		        input.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
		        result = dispatcher.runSync("createInvoice", input);
				if (ServiceUtil.isError(result)) {
					return ServiceUtil.returnError("Error while creating invoice for party : "+partyId, null, null, result);
				}	        
				String invoiceId = (String)result.get("invoiceId");
				Map<String, BigDecimal> taxInvoiceItems = FastMap.newInstance();
				for (Map<String, Object> prodQtyMap : productQtyList) {
					
					String productId = "";
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal amount = BigDecimal.ZERO;
					Map invoiceItemCtx = FastMap.newInstance();
					
					if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
						productId = (String)prodQtyMap.get("productId");
						invoiceItemCtx.put("productId", productId);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
						quantity = (BigDecimal)prodQtyMap.get("quantity");
						invoiceItemCtx.put("quantity", quantity);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
						amount = (BigDecimal)prodQtyMap.get("unitPrice");
						invoiceItemCtx.put("amount", amount);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
						BigDecimal vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
						if(vatPercent.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("vatPercent", vatPercent);
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
						BigDecimal vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
						if(vatAmount.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("vatAmount", vatAmount);
							String invItemType = "VAT_PUR";
							if(UtilValidate.isNotEmpty(taxInvoiceItems.get(invItemType))){
								BigDecimal tempAmt = (BigDecimal)taxInvoiceItems.get(invItemType);
								tempAmt = tempAmt.add(vatAmount);
								taxInvoiceItems.put(invItemType, tempAmt);
							}
							else{
								taxInvoiceItems.put(invItemType, vatAmount);
							}	
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
						BigDecimal cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
						if(cstPercent.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("cstPercent", cstPercent);
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
						BigDecimal cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
						if(cstAmount.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("cstAmount", cstAmount);
							String invItemType = "CST_PUR";
							if(UtilValidate.isNotEmpty(taxInvoiceItems.get(invItemType))){
								BigDecimal tempAmt = (BigDecimal)taxInvoiceItems.get(invItemType);
								tempAmt = tempAmt.add(cstAmount);
								taxInvoiceItems.put(invItemType, tempAmt);
							}
							else{
								taxInvoiceItems.put(invItemType, cstAmount);
							}
							
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("excisePercent"))){
						BigDecimal bedPercent = (BigDecimal)prodQtyMap.get("excisePercent");
						if(bedPercent.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("bedPercent", bedPercent);
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
						BigDecimal bedAmount = (BigDecimal)prodQtyMap.get("bedAmount");
						if(bedAmount.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("bedAmount", bedAmount);
							
							String invItemType = "BED_PUR";
							if(UtilValidate.isNotEmpty(taxInvoiceItems.get(invItemType))){
								BigDecimal tempAmt = (BigDecimal)taxInvoiceItems.get(invItemType);
								tempAmt = tempAmt.add(bedAmount);
								taxInvoiceItems.put(invItemType, tempAmt);
							}
							else{
								taxInvoiceItems.put(invItemType, bedAmount);
							}
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedCessPercent"))){
						BigDecimal bedcessPercent = (BigDecimal)prodQtyMap.get("bedCessPercent");
						if(bedcessPercent.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("bedcessPercent", bedcessPercent);
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedCessAmount"))){
						BigDecimal bedcessAmount = (BigDecimal)prodQtyMap.get("bedCessAmount");
						if(bedcessAmount.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("bedcessAmount", bedcessAmount);
							
							String invItemType = "BEDCESS_PUR";
							if(UtilValidate.isNotEmpty(taxInvoiceItems.get(invItemType))){
								BigDecimal tempAmt = (BigDecimal)taxInvoiceItems.get(invItemType);
								tempAmt = tempAmt.add(bedcessAmount);
								taxInvoiceItems.put(invItemType, tempAmt);
							}
							else{
								taxInvoiceItems.put(invItemType, bedcessAmount);
							}
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedSecCessPercent"))){
						BigDecimal bedseccessPercent = (BigDecimal)prodQtyMap.get("bedSecCessPercent");
						if(bedseccessPercent.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("bedseccessPercent", bedseccessPercent);
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedSecCessAmount"))){
						BigDecimal bedseccessAmount = (BigDecimal)prodQtyMap.get("bedSecCessAmount");
						if(bedseccessAmount.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("bedseccessAmount", bedseccessAmount);
							
							String invItemType = "BEDSECCESS_PUR";
							if(UtilValidate.isNotEmpty(taxInvoiceItems.get(invItemType))){
								BigDecimal tempAmt = (BigDecimal)taxInvoiceItems.get(invItemType);
								tempAmt = tempAmt.add(bedseccessAmount);
								taxInvoiceItems.put(invItemType, tempAmt);
							}
							else{
								taxInvoiceItems.put(invItemType, bedseccessAmount);
							}
						}
					}
					invoiceItemCtx.put("invoiceId", invoiceId);
					invoiceItemCtx.put("invoiceItemTypeId", "INV_RAWPROD_ITEM");
					invoiceItemCtx.put("userLogin", userLogin);
					result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
					
					if (ServiceUtil.isError(result)) {
						Debug.logError("Error creating Invoice item for product : "+productId, module);	
						return ServiceUtil.returnError("Error creating Invoice item for product : "+productId);
					}
					String invItemSeqId = (String) result.get("invoiceItemSeqId");
				}
				
				Iterator taxIter = taxInvoiceItems.entrySet().iterator();
				String invItemTypeId = "";
				while (taxIter.hasNext()) {
					Map.Entry taxEntry = (Entry) taxIter.next();
					invItemTypeId = (String) taxEntry.getKey();
					BigDecimal amt = (BigDecimal) taxEntry.getValue();
					Map invItemCtx = FastMap.newInstance();
					invItemCtx.put("invoiceId", invoiceId);
					invItemCtx.put("invoiceItemTypeId", invItemTypeId);
					invItemCtx.put("quantity", BigDecimal.ONE);
					invItemCtx.put("amount", amt);
					invItemCtx.put("userLogin", userLogin);
					result = dispatcher.runSync("createInvoiceItem", invItemCtx);
					
					if (ServiceUtil.isError(result)) {
						Debug.logError("Error creating Invoice item for Tax : "+invItemTypeId, module);	
						return ServiceUtil.returnError("Error creating Invoice item for Tax : "+invItemTypeId);
					}
					String invItemSeqId = (String) result.get("invoiceItemSeqId");
				}
				
				for (Map<String, Object> adjItemMap : invoiceAdjChargesList) {
					
					String invAdjTypeId = "";
					BigDecimal amount = BigDecimal.ZERO;
					Map invoiceItemCtx = FastMap.newInstance();
					
					if(UtilValidate.isNotEmpty(adjItemMap.get("invoiceItemTypeId"))){
						invAdjTypeId = (String)adjItemMap.get("invoiceItemTypeId");
						invoiceItemCtx.put("invoiceItemTypeId", invAdjTypeId);
					}
					if(UtilValidate.isNotEmpty(adjItemMap.get("adjAmount"))){
						amount = (BigDecimal)adjItemMap.get("adjAmount");
						invoiceItemCtx.put("amount", amount);
					}
					if(UtilValidate.isNotEmpty(invAdjTypeId) && amount.compareTo(BigDecimal.ZERO)>0){
						invoiceItemCtx.put("userLogin", userLogin);
						invoiceItemCtx.put("invoiceId", invoiceId);
						result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
						
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Tax : "+invItemTypeId, module);	
							return ServiceUtil.returnError("Error creating Invoice item for Tax : "+invItemTypeId);
						}
						String invItemSeqId = (String) result.get("invoiceItemSeqId");
					}
					
				}
				GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
				invoice.set("shipmentId", shipmentId);
				invoice.store();
				
				result.put("invoiceId", invoiceId);
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
			return result;

		}
	
	public static String CreateMaterialPOEvent(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	   
		//old PO flow starts from here
		String partyId = (String) request.getParameter("supplierId");
		String orderId = (String) request.getParameter("orderId");
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
		String orderName = (String) request.getParameter("orderName");
		String fileNo = (String) request.getParameter("fileNo");
		String refNo = (String) request.getParameter("refNo");
		String orderDateStr = (String) request.getParameter("orderDate");
		String orderTypeId = (String) request.getParameter("orderTypeId");
		//String effectiveDate = (String) request.getParameter("effectiveDate");
		String estimatedDeliveryDateStr = (String) request.getParameter("estimatedDeliveryDate");
		
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
		Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();
		Timestamp orderDate = UtilDateTime.nowTimestamp();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy"); 
		if (UtilValidate.isNotEmpty(effectiveDateStr)) { 
			try {
				effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			}
		}else{
			effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		Debug.log("estimatedDeliveryDateStr======"+estimatedDeliveryDateStr);
		if (UtilValidate.isNotEmpty(estimatedDeliveryDateStr)) { 
			try {
				estimatedDeliveryDate = new java.sql.Timestamp(sdf.parse(estimatedDeliveryDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			}
		}
		
		if (UtilValidate.isNotEmpty(orderDateStr)) { 
			try {
				orderDate = new java.sql.Timestamp(sdf.parse(orderDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			}
		}
		
		Timestamp SInvoiceDate=null;
		if (UtilValidate.isNotEmpty(SInvoiceDateStr)) { //2011-12-25 18:09:45
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
		
		List productQtyList = FastList.newInstance();
		//Debug.log("rowCount============="+rowCount);
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
				/*request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
				return "error";	*/	
				continue;
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
		
			productQtyList.add(productQtyMap);
		}//end row count for loop
		if( UtilValidate.isEmpty(productQtyList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		List termsList = FastList.newInstance();
		for (int i = 0; i < rowCount; i++) {
			String termTypeId =null;
			String  termDaysStr = null;
			String termValueStr = null;
			String termUom = null;
			Long termDays = Long.valueOf(0);
			String termDescription = null;
			BigDecimal termValue = BigDecimal.ZERO;
			Map<String, Object> termTypeMap = FastMap.newInstance();
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			if (paramMap.containsKey("paymentTermTypeId" + thisSuffix)) {
				termTypeId = (String) paramMap.get("paymentTermTypeId" + thisSuffix);
			}else{
				continue;
			}
			if (paramMap.containsKey("paymentTermDays" + thisSuffix)) {
				termDaysStr = (String) paramMap.get("paymentTermDays" + thisSuffix);
			}
			if (UtilValidate.isNotEmpty(termDaysStr)) {
				termDays = new Long(termDaysStr);
			}  
						
			if (paramMap.containsKey("paymentTermValue" + thisSuffix)) {
				termValueStr = (String) paramMap.get("paymentTermValue" + thisSuffix);
			}
			if (UtilValidate.isNotEmpty(termValueStr)) {
				termValue = new BigDecimal(termValueStr);
			}  
			if (paramMap.containsKey("paymentTermUom" + thisSuffix)) {
				termUom = (String) paramMap.get("paymentTermUom" + thisSuffix);
			}
			if (paramMap.containsKey("paymentTermDescription" + thisSuffix)) {
				termDescription = (String) paramMap.get("paymentTermDescription" + thisSuffix);
			}
			
			termTypeMap.put("termTypeId", termTypeId);
			termTypeMap.put("termDays", termDays);
			termTypeMap.put("termValue", termValue);
			termTypeMap.put("uomId", termUom);
			termTypeMap.put("description", termDescription);
			if(UtilValidate.isNotEmpty(termTypeId)){
				termsList.add(termTypeMap);
			}
			if (paramMap.containsKey("paymentTermDays" + thisSuffix)) {
				termDaysStr = (String) paramMap.get("paymentTermDays" + thisSuffix);
			}
		}
		
		for (int i = 0; i < rowCount; i++) {
			String termTypeId =null;
			String  termDaysStr = null;
			String termValueStr = null;
			Long termDays = Long.valueOf(0);
			BigDecimal termValue = BigDecimal.ZERO;
			String termUom = null;
			String termDescription = null;
			
			Map termTypeMap = FastMap.newInstance();
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			if (paramMap.containsKey("deliveryTermTypeId" + thisSuffix)) {
				termTypeId = (String) paramMap.get("deliveryTermTypeId" + thisSuffix);
			}else{
				continue;
			}
			
			if (paramMap.containsKey("deliveryTermDays" + thisSuffix)) {
				termDaysStr = (String) paramMap.get("deliveryTermDays" + thisSuffix);
			}
			if (UtilValidate.isNotEmpty(termDaysStr)) {
				termDays = new Long(termDaysStr);
			}    
						
			if (paramMap.containsKey("deliveryTermValue" + thisSuffix)) {
				termValueStr = (String) paramMap.get("deliveryTermValue" + thisSuffix);
			}
			if (UtilValidate.isNotEmpty(termValueStr)) {
				termValue = new BigDecimal(termValueStr);
			}  
			
			if (paramMap.containsKey("deliveryTermUom" + thisSuffix)) {
				termUom = (String) paramMap.get("deliveryTermUom" + thisSuffix);
			}
			if (paramMap.containsKey("deliveryTermDescription" + thisSuffix)) {
				termDescription = (String) paramMap.get("deliveryTermDescription" + thisSuffix);
			}
			
			termTypeMap.put("termTypeId", termTypeId);
			termTypeMap.put("termDays", termDays);
			termTypeMap.put("termValue", termValue);
			termTypeMap.put("uomId", termUom);
			termTypeMap.put("description", termDescription);
			if(UtilValidate.isNotEmpty(termTypeId)){
				termsList.add(termTypeMap);
			}
		}
		
		//getting productStoreId 
		String productStoreId = (String) (in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
	 
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("productQtyList", productQtyList);
		processOrderContext.put("orderTypeId", orderTypeId);
		processOrderContext.put("orderId", orderId);
		processOrderContext.put("termsList", termsList);
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
		processOrderContext.put("PONumber", PONumber);
		processOrderContext.put("orderName", orderName);
		processOrderContext.put("fileNo", fileNo);
		processOrderContext.put("refNo", refNo);
		processOrderContext.put("orderDate", orderDate);
		processOrderContext.put("estimatedDeliveryDate", estimatedDeliveryDate);
		if(UtilValidate.isNotEmpty(orderId)){
			result = updateMaterialPO(dctx, processOrderContext);
			if(ServiceUtil.isError(result)){
				Debug.logError("Unable to update order: " + ServiceUtil.getErrorMessage(result), module);
				request.setAttribute("_ERROR_MESSAGE_", "Unable to update order  :" + orderId+"....! "+ServiceUtil.getErrorMessage(result));
				return "error";
			}
		}
		else{
			result = CreateMaterialPO(dctx, processOrderContext);
			if(ServiceUtil.isError(result)){
				Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
				request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
				return "error";
			}
		}
		
		
		request.setAttribute("_EVENT_MESSAGE_", "Entry successful for party: "+partyId+" and  PO :"+result.get("orderId"));	  	 
		
		return "success";
	}
		
   public static Map<String, Object> CreateMaterialPO(DispatchContext ctx,Map<String, ? extends Object> context) {
		
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
	  	List termsList = (List)context.get("termsList");
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
        Timestamp orderDate = (Timestamp)context.get("orderDate");
        Timestamp estimatedDeliveryDate = (Timestamp)context.get("estimatedDeliveryDate");
		String orderName = (String)context.get("orderName");
		String fileNo = (String)context.get("fileNo");
		String refNo = (String)context.get("refNo");
		String orderId = "";		
	  	String currencyUomId = "INR";
	  	String shipmentId = (String) context.get("shipmentId");
	  	String shipmentTypeId = (String) context.get("shipmentTypeId");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		String billToPartyId="Company";
		String orderTypeId = (String)context.get("orderTypeId");
		if(UtilValidate.isEmpty(orderTypeId)){
			orderTypeId = "PURCHASE_ORDER";
		}
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create order without partyId: "+ partyId, module);
			return ServiceUtil.returnError("partyId is empty");
		}
		GenericValue product =null;
		String productPriceTypeId = null;
		
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
		
		try {
			cart.setOrderType(orderTypeId);
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
			
			cart.setEstimatedDeliveryDate(estimatedDeliveryDate);
			//cart.setOrderDate(effectiveDate);
			cart.setOrderDate(orderDate);
			cart.setUserLogin(userLogin, dispatcher);
			
			//set orderterms
			for(int i=0;i<termsList.size();i++){
				Map<String,Object> termMap = FastMap.newInstance();
				termMap = (Map)termsList.get(i);
				cart.addOrderTerm((String)termMap.get("termTypeId"), null, (BigDecimal)termMap.get("termValue"),(Long)termMap.get("termDays"), null, (String)termMap.get("uomId"), (String)termMap.get("description"));
			}
			//set attributes here
			/*if(UtilValidate.isNotEmpty(mrnNumber))
				cart.setOrderAttribute("MRN_NUMBER",mrnNumber);
			if(UtilValidate.isNotEmpty(SInvNumber))
				cart.setOrderAttribute("SUP_INV_NUMBER",SInvNumber);
			if(UtilValidate.isNotEmpty(context.get("SInvoiceDate")))
				cart.setOrderAttribute("SUP_INV_DATE",UtilDateTime.toDateString((Timestamp)context.get("SInvoiceDate"),null));*/
			if(UtilValidate.isNotEmpty(PONumber))
				cart.setOrderAttribute("PO_NUMBER",PONumber);
			if(UtilValidate.isNotEmpty(fileNo))
				cart.setOrderAttribute("FILE_NUMBER",fileNo);
			if(UtilValidate.isNotEmpty(refNo))
				cart.setOrderAttribute("REF_NUMBER",refNo);
			
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
		
			BigDecimal totalPrice = unitPrice.add(totalTaxAmt);
			
			//BigDecimal totalPrice = unitPrice;//as of now For PurchaseOrder listPrice is same like unitPrice
			
		
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
		//Debug.log("===orderCreateResult=====>"+orderCreateResult);
		if (ServiceUtil.isError(orderCreateResult)) {
			String errMsg =  ServiceUtil.getErrorMessage(orderCreateResult);
			Debug.logError(errMsg, "While Creating Order",module);
			return ServiceUtil.returnError(" Error While Creating Order !"+errMsg);
		}
			
		orderId = (String) orderCreateResult.get("orderId");
		//let's create Fright Adhustment here
    	 
		if(freightCharges.compareTo(BigDecimal.ZERO)>0){
	    	Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
	    	adjustCtx.put("orderId", orderId);
	    	adjustCtx.put("orderAdjustmentTypeId", "COGS_FREIGHT");
	    	adjustCtx.put("amount", freightCharges);
	    	Map adjResultMap=FastMap.newInstance();
		  	 	try{
		  	 		adjResultMap = dispatcher.runSync("createOrderAdjustment",adjustCtx);  		  		 
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
	    	adjustCtx.put("orderAdjustmentTypeId", "COGS_DISC");
	    	adjustCtx.put("amount", discount);
	    	Map adjResultMap=FastMap.newInstance();
	  	 	try{
	  	 		adjResultMap = dispatcher.runSync("createOrderAdjustment",adjustCtx);  		  		 
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
	    	adjustCtx.put("orderAdjustmentTypeId", "COGS_INSURANCE");
	    	adjustCtx.put("amount", insurence);
	    	Map adjResultMap=FastMap.newInstance();
		  	 	try{
		  	 		 adjResultMap = dispatcher.runSync("createOrderAdjustment",adjustCtx);  		  		 
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
	    	adjustCtx.put("orderAdjustmentTypeId", "COGS_PCK_FWD");
	    	adjustCtx.put("amount", packAndFowdg);
	    	Map adjResultMap=FastMap.newInstance();
		  	 	try{
		  	 		 adjResultMap = dispatcher.runSync("createOrderAdjustment",adjustCtx);  		  		 
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
	    	adjustCtx.put("orderAdjustmentTypeId", "COGS_OTH_CHARGES"); 
	    	adjustCtx.put("amount", otherCharges);
	    	Map adjResultMap=FastMap.newInstance();
		  	 	try{
		  	 		 adjResultMap = dispatcher.runSync("createOrderAdjustment",adjustCtx);  		  		 
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
	    /*try{   
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
	  	}*/
	    
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
		return result;

	}
   	
   	
   	public static Map<String, Object> updateMaterialPO(DispatchContext ctx,Map<String, ? extends Object> context) {
		
	    Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    List<Map> productQtyList = (List) context.get("productQtyList");
	    Timestamp supplyDate = (Timestamp) context.get("supplyDate");
	    Locale locale = (Locale) context.get("locale");
	    String productStoreId = (String) context.get("productStoreId");
	  	String salesChannel = (String) context.get("salesChannel");
	  	String partyId = (String) context.get("partyId");
	  	String billFromPartyId = (String) context.get("billFromPartyId");
		String issueToDeptId = (String) context.get("issueToDeptId");
	  	List termsList = (List)context.get("termsList");
	  	boolean beganTransaction = false;
	  	BigDecimal freightCharges = (BigDecimal) context.get("freightCharges");
	  	BigDecimal discount = (BigDecimal) context.get("discount");
	  	BigDecimal packAndFowdg = (BigDecimal) context.get("packAndFowdg");
		BigDecimal otherCharges = (BigDecimal) context.get("otherCharges");
		BigDecimal insurence = (BigDecimal) context.get("insurence");
	  	String PONumber=(String) context.get("PONumber");
	  	String orderId = (String) context.get("orderId");
        Timestamp orderDate = (Timestamp)context.get("orderDate");
        Timestamp estimatedDeliveryDate = (Timestamp)context.get("estimatedDeliveryDate");
		String orderName = (String)context.get("orderName");
		String fileNo = (String)context.get("fileNo");
		String refNo = (String)context.get("refNo");
	  	String currencyUomId = "INR";
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		String billToPartyId="Company";
		String orderTypeId = (String)context.get("orderTypeId");
		if(UtilValidate.isEmpty(orderTypeId)){
			orderTypeId = "PURCHASE_ORDER";
		}
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create order without partyId: "+ partyId, module);
			return ServiceUtil.returnError("partyId is empty");
		}
		GenericValue product =null;
		String productPriceTypeId = null;
		
		try {
				
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			
			orderHeader.set("orderTypeId", orderTypeId);
			orderHeader.set("orderName", orderName);
			orderHeader.set("externalId", PONumber);
			orderHeader.set("salesChannelEnumId", salesChannel);
			orderHeader.set("orderDate", orderDate);
			orderHeader.set("estimatedDeliveryDate", estimatedDeliveryDate);
			orderHeader.set("productStoreId", productStoreId);
			orderHeader.store();
			
			//set orderAttributes and terms
			
			
			if(UtilValidate.isNotEmpty(fileNo)){
				GenericValue orderAttr = delegator.makeValue("OrderAttribute");        	 
				orderAttr.set("orderId", orderId);
				orderAttr.set("attrName", "FILE_NUMBER");
				orderAttr.set("attrValue", fileNo);
				delegator.createOrStore(orderAttr);
			}
			
			if(UtilValidate.isNotEmpty(refNo)){
				GenericValue orderAttr = delegator.makeValue("OrderAttribute");        	 
				orderAttr.set("orderId", orderId);
				orderAttr.set("attrName", "REF_NUMBER");
				orderAttr.set("attrValue", refNo);
				delegator.createOrStore(orderAttr);
			}
			
			List<GenericValue> orderTerms = delegator.findList("OrderTerm", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			delegator.removeAll(orderTerms);
			for(int i=0;i<termsList.size();i++){
				Map<String,Object> termMap = FastMap.newInstance();
				termMap = (Map)termsList.get(i);
				GenericValue orderTerm = delegator.makeValue("OrderTerm");        	 
				orderTerm.set("orderId", orderId);
				orderTerm.set("orderItemSeqId", "_NA_");
				orderTerm.set("termTypeId", (String)termMap.get("termTypeId"));
				orderTerm.set("termDays", (Long)termMap.get("termDays"));
				orderTerm.set("termValue", (BigDecimal)termMap.get("termValue"));
				orderTerm.set("description", (String)termMap.get("description"));
				orderTerm.set("uomId", (String)termMap.get("uomId"));
				delegator.createOrStore(orderTerm);
			}
			
		} catch (Exception e) {
			
			Debug.logError(e, "Error in updating order", module);
			return ServiceUtil.returnError("Error in updating order");
		}
		String productId = "";
		
		List<GenericValue> prodPriceTypeList = FastList.newInstance();
		try{
			beganTransaction = TransactionUtil.begin(7200);
		
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			BigDecimal totalBedAmount = BigDecimal.ZERO;
			BigDecimal totalBedCessAmount = BigDecimal.ZERO;
			BigDecimal totalBedSecCessAmount = BigDecimal.ZERO;
			BigDecimal totalVatAmount = BigDecimal.ZERO;
			BigDecimal totalCstAmount = BigDecimal.ZERO;
			for (Map<String, Object> prodQtyMap : productQtyList) {
				
				List taxList=FastList.newInstance();
				BigDecimal totalTaxAmt =  BigDecimal.ZERO;
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
					productId = (String)prodQtyMap.get("productId");
				}
				List<GenericValue> orderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				
				GenericValue orderItemDetail = EntityUtil.getFirst(orderItem);
				if(UtilValidate.isNotEmpty(orderItemDetail)){
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal unitPrice = BigDecimal.ZERO;
					BigDecimal vatAmount = BigDecimal.ZERO;
					BigDecimal cstAmount = BigDecimal.ZERO;
					BigDecimal bedAmount = BigDecimal.ZERO;
					BigDecimal bedcessAmount = BigDecimal.ZERO;
					BigDecimal bedseccessAmount = BigDecimal.ZERO;
					BigDecimal unitListPrice = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
						quantity = (BigDecimal)prodQtyMap.get("quantity");
						orderItemDetail.set("quantity", quantity);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
						unitPrice = (BigDecimal)prodQtyMap.get("unitPrice");
						orderItemDetail.set("unitPrice", unitPrice);
						unitListPrice = unitListPrice.add(unitPrice);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
						vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
						if(vatAmount.compareTo(BigDecimal.ZERO)>0){
							if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
								BigDecimal vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
								orderItemDetail.set("vatPercent", vatPercent);
								orderItemDetail.set("vatAmount", vatAmount);
							}
							unitListPrice = unitListPrice.add(vatAmount.divide(quantity, 3, salestaxRounding));
							totalVatAmount = totalVatAmount.add(vatAmount);
						}
						
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
						cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
						if(cstAmount.compareTo(BigDecimal.ZERO)>0){
							if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
								BigDecimal cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
								orderItemDetail.set("cstPercent", cstPercent);
								orderItemDetail.set("cstAmount", cstAmount);
							}
							unitListPrice = unitListPrice.add(cstAmount.divide(quantity, 3, salestaxRounding));
							totalCstAmount = totalCstAmount.add(cstAmount);
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
						bedAmount = (BigDecimal)prodQtyMap.get("bedAmount");
						if(bedAmount.compareTo(BigDecimal.ZERO)>0){
							if(UtilValidate.isNotEmpty(prodQtyMap.get("excisePercent"))){
								BigDecimal excisePercent = (BigDecimal)prodQtyMap.get("excisePercent");
								orderItemDetail.set("bedPercent", excisePercent);
								orderItemDetail.set("bedAmount", bedAmount);
							}
							unitListPrice = unitListPrice.add(bedAmount.divide(quantity, 3, salestaxRounding));
							totalBedAmount = totalBedAmount.add(bedAmount);
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedCessAmount"))){
						bedcessAmount = (BigDecimal)prodQtyMap.get("bedCessAmount");
						if(bedcessAmount.compareTo(BigDecimal.ZERO)>0){
							if(UtilValidate.isNotEmpty(prodQtyMap.get("bedCessPercent"))){
								BigDecimal bedcessPercent = (BigDecimal)prodQtyMap.get("bedCessPercent");
								orderItemDetail.set("bedcessPercent", bedcessPercent);
								orderItemDetail.set("bedcessAmount", bedcessAmount);
							}
							unitListPrice = unitListPrice.add(bedcessAmount.divide(quantity, 3, salestaxRounding));
							totalBedCessAmount = totalBedCessAmount.add(bedcessAmount);
						}
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedCessAmount"))){
						bedseccessAmount = (BigDecimal)prodQtyMap.get("bedSecCessAmount");
						if(bedseccessAmount.compareTo(BigDecimal.ZERO)>0){
							if(UtilValidate.isNotEmpty(prodQtyMap.get("bedSecCessPercent"))){
								BigDecimal bedseccessPercent = (BigDecimal)prodQtyMap.get("bedSecCessPercent");
								orderItemDetail.set("bedseccessPercent", bedseccessPercent);
								orderItemDetail.set("bedseccessAmount", bedseccessAmount);
							}
							unitListPrice = unitListPrice.add(bedseccessAmount.divide(quantity, 3, salestaxRounding));
							totalBedSecCessAmount = totalBedSecCessAmount.add(bedseccessAmount);
						}
					}
					orderItemDetail.set("unitListPrice", unitListPrice);
					orderItemDetail.set("changeByUserLoginId", userLogin.getString("userLoginId"));
					orderItemDetail.set("changeDatetime", UtilDateTime.nowTimestamp());
					orderItemDetail.store();
				}
			}
			
			List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			List<Map> adjTypeItems = FastList.newInstance();
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "COGS_FREIGHT", "amount", freightCharges));
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "COGS_DISC", "amount", discount));
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "COGS_INSURANCE", "amount", packAndFowdg));
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "COGS_PCK_FWD", "amount", insurence));
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "COGS_OTH_CHARGES", "amount", otherCharges));
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "BED_PUR", "amount", totalBedAmount));
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "BEDCESS_PUR", "amount", totalBedCessAmount));
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "BEDSECCESS_PUR", "amount", totalBedSecCessAmount));
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "VAT_PUR", "amount", totalVatAmount));
			adjTypeItems.add(UtilMisc.toMap("orderAdjustmentTypeId", "CST_PUR", "amount", totalCstAmount));
			
			for(Map entryMap : adjTypeItems){
				String orderAdjustmentTypeId = (String)entryMap.get("orderAdjustmentTypeId");
				BigDecimal amount = (BigDecimal) entryMap.get("amount");
				List<GenericValue> adjItems = EntityUtil.filterByCondition(orderAdjustments, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, orderAdjustmentTypeId));
				if(UtilValidate.isNotEmpty(adjItems) && amount.compareTo(BigDecimal.ZERO)==0){
					delegator.removeAll(adjItems);
				}
				else{
					
					if(amount.compareTo(BigDecimal.ZERO)>0){

						if(UtilValidate.isNotEmpty(adjItems)){
							GenericValue eachAdjItem = EntityUtil.getFirst(adjItems);
							eachAdjItem.set("amount", amount);
							eachAdjItem.store();
						}
						else{
							Map adjustCtx = FastMap.newInstance();	  	
							adjustCtx.put("userLogin", userLogin);
							adjustCtx.put("orderId", orderId);
							adjustCtx.put("orderAdjustmentTypeId", orderAdjustmentTypeId);
							adjustCtx.put("amount", amount);
							Map adjResultMap=FastMap.newInstance();
				  	 		try{
				  	 			adjResultMap = dispatcher.runSync("createOrderAdjustment",adjustCtx);
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
					}
				}
			}
			
			Map resetTotalCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			resetTotalCtx.put("orderId", orderId);
			resetTotalCtx.put("userLogin", userLogin);
			Map resetMap=FastMap.newInstance();
  	 		try{
  	 			resetMap = dispatcher.runSync("resetGrandTotal",resetTotalCtx);  		  		 
	  	 		if (ServiceUtil.isError(resetMap)) {
	  	 			String errMsg =  ServiceUtil.getErrorMessage(resetMap);
	  	 			Debug.logError(errMsg , module);
	  	 			return ServiceUtil.returnError(" Error While reseting order totals for Purchase Order !"+orderId);
	  	 		}
  	 		}catch (Exception e) {
  	 			Debug.logError(e, " Error While reseting order totals for Purchase Order !"+orderId, module);
  	 			return resetMap;			  
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
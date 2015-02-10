package in.vasista.vbiz.purchase;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
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
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.order.OrderServices;
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
import org.ofbiz.accounting.util.UtilAccounting;
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
import org.ofbiz.order.order.OrderServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;
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
	    String deliveryChallanDateStr = (String) request.getParameter("deliveryChallanDate");
	    String deliveryChallanNo = (String) request.getParameter("deliveryChallanNo");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		String shipmentId ="";
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
		SimpleDateFormat SimpleDF = new SimpleDateFormat("dd:mm:yyyy hh:mm");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
		receiptDate = UtilDateTime.nowTimestamp();
	  	/*if(UtilValidate.isNotEmpty(receiptDateStr)){
	  		try {
	  			receiptDate = new java.sql.Timestamp(SimpleDF.parse(receiptDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	}
	  	}*/
        DateFormat givenFormatter = new SimpleDateFormat("dd:MM:yyyy hh:mm");
        DateFormat reqformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(UtilValidate.isNotEmpty(receiptDateStr)){
	        try {
	        Date givenReceiptDate = (Date)givenFormatter.parse(receiptDateStr);
	        receiptDate = new java.sql.Timestamp(givenReceiptDate.getTime());
	        }catch (ParseException e) {
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
	  	Timestamp deliveryChallanDate=UtilDateTime.nowTimestamp();
	  	if(UtilValidate.isNotEmpty(deliveryChallanDateStr)){
	  		try {
	  			deliveryChallanDate = new java.sql.Timestamp(sdf.parse(deliveryChallanDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + deliveryChallanDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + deliveryChallanDateStr, module);
		  	}
	  	}
	  	boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			List conditionList = FastList.newInstance();
			List<GenericValue> extPOItems = FastList.newInstance();
			List<GenericValue> extReciptItems = FastList.newInstance();
			
			boolean directPO = Boolean.TRUE;
			String extPOId = "";
			List productList = FastList.newInstance();
			
			if(UtilValidate.isNotEmpty(orderId)){
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				
				String statusId = orderHeader.getString("statusId");
				String orderTypeId = orderHeader.getString("orderTypeId");
				if(statusId.equals("ORDER_CANCELLED")){
					Debug.logError("Cannot create GRN for cancelled orders : "+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Cannot create GRN for cancelled orders : "+orderId);	
					TransactionUtil.rollback();
			  		return "error";
				}
				
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, orderTypeId));
				List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				
				if(UtilValidate.isNotEmpty(orderAssoc)){
					extPOId = (EntityUtil.getFirst(orderAssoc)).getString("toOrderId");
					directPO = Boolean.FALSE;
				}
				if(UtilValidate.isNotEmpty(extPOId)){
					List<GenericValue> annualContractPOAsso = delegator.findList("OrderAssoc", EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, extPOId), UtilMisc.toSet("orderId"), null, null, false);
					List orderIds = EntityUtil.getFieldListFromEntityList(annualContractPOAsso, "orderId", true);
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
					conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, extPOId));
					extPOItems = delegator.findList("OrderItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
					
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
					conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
					extReciptItems = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				}
				
				
				conditionList.clear();
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
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			
			productList = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
			GenericValue newEntity = delegator.makeValue("Shipment");
	        newEntity.set("estimatedShipDate", receiptDate);
	        newEntity.set("shipmentTypeId", "MATERIAL_SHIPMENT");
	        newEntity.set("statusId", "GENERATED");
	        newEntity.put("vehicleId",vehicleId);
	        newEntity.put("partyIdFrom",supplierId);
	        newEntity.put("supplierInvoiceId",supplierInvoiceId);
	        newEntity.put("supplierInvoiceDate",supplierInvoiceDate);
	        newEntity.put("deliveryChallanNumber",deliveryChallanNo);
	        newEntity.put("deliveryChallanDate",deliveryChallanDate);
	        newEntity.put("primaryOrderId",orderId);
	        newEntity.set("createdDate", nowTimeStamp);
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            delegator.createSetNextSeqId(newEntity);            
            shipmentId = (String) newEntity.get("shipmentId");
	       
			/*List<Map> prodQtyList = FastList.newInstance();*/
			
			for (int i = 0; i < rowCount; i++) {
				
				String productId = "";
		        String quantityStr = "";
		        String deliveryChallanQtyStr = "";
		        String oldRecvdQtyStr = "";
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal deliveryChallanQty = BigDecimal.ZERO;
				BigDecimal oldRecvdQty = BigDecimal.ZERO;
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
				//DC qty here
				if (paramMap.containsKey("deliveryChallanQty" + thisSuffix)) {
					deliveryChallanQtyStr = (String) paramMap.get("deliveryChallanQty" + thisSuffix);
				}
				if(UtilValidate.isNotEmpty(deliveryChallanQtyStr)){
					deliveryChallanQty = new BigDecimal(deliveryChallanQtyStr);
				}else{
					deliveryChallanQty = quantity;
				}
				//old recived qty oldRecvdQty
				if (paramMap.containsKey("oldRecvdQty" + thisSuffix)) {
					oldRecvdQtyStr = (String) paramMap.get("oldRecvdQty" + thisSuffix);
				}
				if(UtilValidate.isNotEmpty(oldRecvdQtyStr)){
					oldRecvdQty = new BigDecimal(oldRecvdQtyStr);
				}
				if(UtilValidate.isEmpty(withoutPO)){
					if(directPO){
						GenericValue checkOrderItem = null;
						List<GenericValue> ordItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
						if(UtilValidate.isNotEmpty(ordItems)){
							checkOrderItem = EntityUtil.getFirst(ordItems);
						}
						
						if(UtilValidate.isNotEmpty(checkOrderItem)){
							BigDecimal orderQty = checkOrderItem.getBigDecimal("quantity");
							BigDecimal checkQty = (orderQty.multiply(new BigDecimal(1.1))).setScale(0, BigDecimal.ROUND_CEILING);
							BigDecimal maxQty=oldRecvdQty.add(quantity);
							Debug.log("=orderQty=="+orderQty+"==checkQty="+checkQty+"==maxQty=="+maxQty+"==quantity="+quantity);
							//if(quantity.compareTo(checkQty)>0){
							if(maxQty.compareTo(checkQty)>0){	
								Debug.logError("Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId, module);
								request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId);	
								TransactionUtil.rollback();
						  		return "error";
							}
						}
					}
					else{
						List<GenericValue> poItems = EntityUtil.filterByCondition(extPOItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
						List<GenericValue> receiptItems = EntityUtil.filterByCondition(extReciptItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
						BigDecimal poQty = BigDecimal.ZERO;
						if(UtilValidate.isNotEmpty(poItems)){
							GenericValue poItem = EntityUtil.getFirst(poItems);
							poQty = poItem.getBigDecimal("quantity");
							
						}
						BigDecimal receiptQty = BigDecimal.ZERO;
						for(GenericValue item : receiptItems){
							receiptQty = receiptQty.add(item.getBigDecimal("quantityAccepted"));
						}
						BigDecimal checkQty = poQty.subtract(receiptQty);
						
						if(quantity.compareTo(checkQty)>0){
							Debug.logError("Quantity cannot be more than ARC/CPC for PO : "+orderId, module);
							request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be more than ARC/CPC for PO : "+orderId);	
							TransactionUtil.rollback();
					  		return "error";
						}
					}

				}
				
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
				//Product should mapped to any one of facility
				 if (UtilValidate.isEmpty(facilityProd)) {
			        	Debug.logError("Problem creating shipment Item for ProductId :"+productId+" Not Mapped To Store Facility !", module);
						request.setAttribute("_ERROR_MESSAGE_", "Problem creating shipment Item for ProductId :"+productId+" Not Mapped To Store Facility!");	
						TransactionUtil.rollback();
				  		return "error";
			        }
				
				Map inventoryReceiptCtx = FastMap.newInstance();
				
				inventoryReceiptCtx.put("userLogin", userLogin);
				inventoryReceiptCtx.put("productId", productId);
				inventoryReceiptCtx.put("datetimeReceived", receiptDate);
				inventoryReceiptCtx.put("quantityAccepted", quantity);
				inventoryReceiptCtx.put("quantityRejected", BigDecimal.ZERO);
				if(deliveryChallanQty.compareTo(BigDecimal.ZERO)>0){
					inventoryReceiptCtx.put("deliveryChallanQty",deliveryChallanQty);
		        }
				inventoryReceiptCtx.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				inventoryReceiptCtx.put("ownerPartyId", supplierId);
				/*inventoryReceiptCtx.put("consolidateInventoryReceive", "Y");*/
				inventoryReceiptCtx.put("facilityId", facilityProd.getString("facilityId"));
				inventoryReceiptCtx.put("unitCost", BigDecimal.ZERO);
				if(UtilValidate.isNotEmpty(ordItm)){
					inventoryReceiptCtx.put("unitCost", ordItm.getBigDecimal("unitListPrice"));
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
		request.setAttribute("_EVENT_MESSAGE_", "Successfully made shipment with ID:"+shipmentId);
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
				
				GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
				
				if(UtilValidate.isNotEmpty(shipment) && shipment.equals("SHIPMENT_CANCELLED")){
					Debug.logError("Cannot create invoice for cancelled shipment", module);
					return ServiceUtil.returnError("Cannot create invoice for cancelled shipment");
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
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String PONumber=(String) request.getParameter("PONumber");
		// Inclusive tax and Exclusive tax
		String incTax=(String) request.getParameter("incTax");
		String orderName = (String) request.getParameter("orderName");
		String fileNo = (String) request.getParameter("fileNo");
		String refNo = (String) request.getParameter("refNo");
		String orderDateStr = (String) request.getParameter("orderDate");
		String fromDate = (String) request.getParameter("fromDate");
		String thruDate = (String) request.getParameter("thruDate");
		String orderTypeId = (String) request.getParameter("orderTypeId");
		//String effectiveDate = (String) request.getParameter("effectiveDate");
		String estimatedDeliveryDateStr = (String) request.getParameter("estimatedDeliveryDate");
		
		String partyIdFrom = "";
		Map processOrderContext = FastMap.newInstance();
		String salesChannel = (String)request.getParameter("salesChannel");
	  
		String productId = null;
		String quantityStr = null;
		String unitPriceStr=null;
		
		Timestamp effectiveDate=null;
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal unitPrice = BigDecimal.ZERO;
		Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();
		Timestamp orderDate = UtilDateTime.nowTimestamp();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy"); 
		
		try {
			if(UtilValidate.isNotEmpty(PONumber)){
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", PONumber), false);
				if (UtilValidate.isNotEmpty(orderHeader)) { 
				String statusId = orderHeader.getString("statusId");
				if(statusId.equals("ORDER_CANCELLED")){
					Debug.logError("Cannot create PurchaseOrder for cancelled orderId : "+PONumber, module);
					request.setAttribute("_ERROR_MESSAGE_", "Cannot create PurchaseOrder for cancelled orderId : "+PONumber);	
			  		return "error";
				}}
			  }
		}catch(GenericEntityException e){
			Debug.logError("Cannot create PurchaseOrder for cancelled orderId : "+PONumber, module);
			request.setAttribute("_ERROR_MESSAGE_", "Cannot create PurchaseOrder for cancelled orderId : "+PONumber);	
			return "error";
		}
				
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
				Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
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
		
		List termsList = FastList.newInstance();
		Map taxTermsMap = FastMap.newInstance();
		List productQtyList = FastList.newInstance();
		
		//Debug.log("rowCount============="+rowCount);
		for (int i = 0; i < rowCount; i++) {
			
			List<Map> taxTermsPerOrderItem = FastList.newInstance();
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
			if (paramMap.containsKey("unitPrice" + thisSuffix)) {
			   unitPriceStr = (String) paramMap.get("unitPrice" + thisSuffix);
			}
			//percenatge of TAXes
			
			String vatPercentStr=null;
			String bedPercentStr=null;
			String cstPercentStr=null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal bedPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO; 
			
			if (paramMap.containsKey("vatPercent" + thisSuffix)) {
				vatPercentStr = (String) paramMap.get("vatPercent" + thisSuffix);
			}
			if (paramMap.containsKey("bedPercent" + thisSuffix)) {
				bedPercentStr = (String) paramMap.get("bedPercent" + thisSuffix);
			}
			if (paramMap.containsKey("cstPercent" + thisSuffix)) {
				cstPercentStr = (String) paramMap.get("cstPercent" + thisSuffix);
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
					unitPrice = new BigDecimal(unitPriceStr);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing UnitPrice string: " + unitPriceStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing UnitPrice string: " + unitPriceStr);
				return "error";
			} 
			try {
				if (!vatPercentStr.equals("")) {
					vatPercent = new BigDecimal(vatPercentStr);
				}
				if(vatPercent.compareTo(BigDecimal.ZERO)>0){
					Map tempTermMap = FastMap.newInstance();
					tempTermMap.put("termTypeId", "VAT_PUR");
					tempTermMap.put("termValue", vatPercent);
					tempTermMap.put("uomId", "PERCENT");
					taxTermsPerOrderItem.add(tempTermMap);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing VatPercent string: " + vatPercentStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing VatPercent string: " + vatPercentStr);
				return "error";
			}
			try {
				if (!bedPercentStr.equals("")) {
					bedPercent = new BigDecimal(bedPercentStr);
				}
				if(bedPercent.compareTo(BigDecimal.ZERO)>0){
					Map tempTermMap = FastMap.newInstance();
					tempTermMap.put("termTypeId", "BED_PUR");
					tempTermMap.put("termValue", bedPercent);
					tempTermMap.put("uomId", "PERCENT");
					taxTermsPerOrderItem.add(tempTermMap);
				}

			} catch (Exception e) {
				Debug.logError(e, "Problems parsing excisePercent string: " + bedPercentStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing excisePercent string: " + bedPercentStr);
				return "error";
			}
			
			try {
				if (!cstPercentStr.equals("")) {
					cstPercent = new BigDecimal(cstPercentStr);
				}
				if(cstPercent.compareTo(BigDecimal.ZERO)>0){
					Map tempTermMap = FastMap.newInstance();
					tempTermMap.put("termTypeId", "CST_PUR");
					tempTermMap.put("termValue", cstPercent);
					tempTermMap.put("uomId", "PERCENT");
					taxTermsPerOrderItem.add(tempTermMap);
				}
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing CSTPercent string: " + cstPercentStr, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + cstPercentStr);
				return "error";
			}
			
			productQtyMap.put("productId", productId);
			productQtyMap.put("quantity", quantity);
			productQtyMap.put("unitPrice", unitPrice);
			productQtyMap.put("vatPercent", vatPercent);
			productQtyMap.put("bedPercent", bedPercent);
			productQtyMap.put("cstPercent", cstPercent);
			productQtyList.add(productQtyMap);
			
			if(UtilValidate.isNotEmpty(taxTermsPerOrderItem)){
				taxTermsMap.put(productId, taxTermsPerOrderItem);
			}
		}//end row count for loop
		if( UtilValidate.isEmpty(productQtyList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		
		if(UtilValidate.isNotEmpty(incTax)){
			Map tempTermMap = FastMap.newInstance();
			tempTermMap.put("termTypeId", "INC_TAX");
			termsList.add(tempTermMap);
		}
		
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
		
		List<Map> otherAdjList = FastList.newInstance();
		
		for (int i = 0; i < rowCount; i++) {
			Map tempMap = FastMap.newInstance();
			String termTypeId =null;
			String  termDaysStr = null;
			String termValueStr = null;
			Long termDays = Long.valueOf(0);
			BigDecimal termValue = BigDecimal.ZERO;
			String termUom = null;
			String termDescription = null;
			
			Map termTypeMap = FastMap.newInstance();
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			if (paramMap.containsKey("otherTermTypeId" + thisSuffix)) {
				termTypeId = (String) paramMap.get("otherTermTypeId" + thisSuffix);
			}else{
				continue;
			}
			
			if (paramMap.containsKey("otherTermDays" + thisSuffix)) {
				termDaysStr = (String) paramMap.get("otherTermDays" + thisSuffix);
			}
			if (UtilValidate.isNotEmpty(termDaysStr)) {
				termDays = new Long(termDaysStr);
			}    
						
			if (paramMap.containsKey("otherTermValue" + thisSuffix)) {
				termValueStr = (String) paramMap.get("otherTermValue" + thisSuffix);
			}
			if (UtilValidate.isNotEmpty(termValueStr)) {
				termValue = new BigDecimal(termValueStr);
			}  
			
			if (paramMap.containsKey("otherTermUom" + thisSuffix)) {
				termUom = (String) paramMap.get("otherTermUom" + thisSuffix);
			}
			if (paramMap.containsKey("otherTermDescription" + thisSuffix)) {
				termDescription = (String) paramMap.get("otherTermDescription" + thisSuffix);
			}
			
			termTypeMap.put("termTypeId", termTypeId);
			termTypeMap.put("termDays", termDays);
			termTypeMap.put("termValue", termValue);
			termTypeMap.put("uomId", termUom);
			termTypeMap.put("description", termDescription);
			if(UtilValidate.isNotEmpty(termTypeId) && (termValue.compareTo(BigDecimal.ZERO)>0 || termValue.compareTo(BigDecimal.ZERO)==0)){
				termsList.add(termTypeMap);
				tempMap.put("adjustmentTypeId", termTypeId);
				tempMap.put("amount", termValue);
				tempMap.put("uomId", termUom);
				otherAdjList.add(tempMap);
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
		processOrderContext.put("otherChargesAdjustment", otherAdjList);
		processOrderContext.put("taxTermsMap", taxTermsMap);
		processOrderContext.put("billFromPartyId", billFromPartyId);
		processOrderContext.put("issueToDeptId", issueToDeptId);
		processOrderContext.put("supplyDate", effectiveDate);
		processOrderContext.put("salesChannel", salesChannel);
		processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
		processOrderContext.put("productStoreId", productStoreId);
		processOrderContext.put("PONumber", PONumber);
		processOrderContext.put("orderName", orderName);
		processOrderContext.put("fileNo", fileNo);
		processOrderContext.put("refNo", refNo);
		processOrderContext.put("orderDate", orderDate);
		processOrderContext.put("fromDate", fromDate);
		processOrderContext.put("thruDate", thruDate);
		processOrderContext.put("estimatedDeliveryDate", estimatedDeliveryDate);
		processOrderContext.put("incTax", incTax);
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
		if(UtilValidate.isNotEmpty(PONumber)){
		Map<String, Object> orderAssocMap = FastMap.newInstance();
		orderAssocMap.put("orderId", result.get("orderId"));
		orderAssocMap.put("toOrderId", PONumber);
		orderAssocMap.put("userLogin", userLogin);
		result = createOrderAssoc(dctx,orderAssocMap);
			if(ServiceUtil.isError(result)){
				Debug.logError("Unable do Order Assoc: " + ServiceUtil.getErrorMessage(result), module);
				request.setAttribute("_ERROR_MESSAGE_", "Unable do Order Assoc...! "+ServiceUtil.getErrorMessage(result));
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
	  	Map taxTermsMap = (Map) context.get("taxTermsMap");
	  	String partyId = (String) context.get("partyId");
	  	String billFromPartyId = (String) context.get("billFromPartyId");
		String issueToDeptId = (String) context.get("issueToDeptId");
	  	List termsList = (List)context.get("termsList");
	  	List<Map> otherChargesAdjustment = (List)context.get("otherChargesAdjustment");
	  	String incTax = (String)context.get("incTax");
	  	boolean beganTransaction = false;
	  	String PONumber=(String) context.get("PONumber");
        Timestamp orderDate = (Timestamp)context.get("orderDate");
        Timestamp estimatedDeliveryDate = (Timestamp)context.get("estimatedDeliveryDate");
		String orderName = (String)context.get("orderName");
		String fileNo = (String)context.get("fileNo");
		String refNo = (String)context.get("refNo");
		String orderId = "";		
	  	String currencyUomId = "INR";
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		String fromDate = (String)context.get("fromDate");
		String thruDate = (String)context.get("thruDate");
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
		//these are input param to calcultae teramount based on order terms 
		BigDecimal basicAmount = BigDecimal.ZERO;
		//exciseDuty includes BED,CESS,SECESS
		BigDecimal exciseDuty = BigDecimal.ZERO;
		BigDecimal discountBeforeTax = BigDecimal.ZERO;
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
			cart.setOrderName(orderName);
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
			if(UtilValidate.isNotEmpty(PONumber))
				cart.setOrderAttribute("PO_NUMBER",PONumber);
			if(UtilValidate.isNotEmpty(fileNo))
				cart.setOrderAttribute("FILE_NUMBER",fileNo);
			if(UtilValidate.isNotEmpty(refNo))
				cart.setOrderAttribute("REF_NUMBER",refNo);
			
			if(UtilValidate.isNotEmpty(fromDate)){
				cart.setOrderAttribute("VALID_FROM",fromDate);
			}
			if(UtilValidate.isNotEmpty(thruDate)){
				cart.setOrderAttribute("VALID_THRU",thruDate);
			}
			
		} catch (Exception e) {
			
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}
		String productId = "";
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal unitPrice = BigDecimal.ZERO;
		BigDecimal vatAmount = BigDecimal.ZERO;
		BigDecimal cstAmount = BigDecimal.ZERO;
		BigDecimal bedAmount = BigDecimal.ZERO;
		BigDecimal bedCessAmount = BigDecimal.ZERO;
		BigDecimal bedSecCessAmount = BigDecimal.ZERO;

		List<GenericValue> prodPriceTypeList = FastList.newInstance();
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			List condExpr = FastList.newInstance();
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
					BigDecimal compareBedPercent = bedPercent.setScale(6);
					
					condExpr.clear();
					condExpr.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, "EXCISE_DUTY_PUR"));
					condExpr.add(EntityCondition.makeCondition("taxRate", EntityOperator.EQUALS, compareBedPercent));
					EntityCondition cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
					
					List<GenericValue> taxComponentMap = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
					taxComponentMap = EntityUtil.filterByDate(taxComponentMap, UtilDateTime.nowTimestamp());
					
					if(UtilValidate.isEmpty(taxComponentMap)){
						Debug.logError("Tax component configuration for Excise duty missing ", module);
						return ServiceUtil.returnError("Tax component configuration for Excise duty missing ");
					}
					
					List<GenericValue> bedTaxComponent = EntityUtil.filterByCondition(taxComponentMap, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS, "BED_PUR"));
					if(UtilValidate.isNotEmpty(bedTaxComponent)){
						bedTaxPercent = (EntityUtil.getFirst(bedTaxComponent)).getBigDecimal("componentRate");
					}
					
					List<GenericValue> bedcessTaxComponents = EntityUtil.filterByCondition(taxComponentMap, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS, "BEDCESS_PUR"));
					if(UtilValidate.isNotEmpty(bedcessTaxComponents)){
						bedcessTaxPercent = (EntityUtil.getFirst(bedcessTaxComponents)).getBigDecimal("componentRate");
					}
					
					List<GenericValue> bedseccessTaxComponents = EntityUtil.filterByCondition(taxComponentMap, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS, "BEDSECCESS_PUR"));
					if(UtilValidate.isNotEmpty(bedseccessTaxComponents)){
						bedseccessTaxPercent = (EntityUtil.getFirst(bedseccessTaxComponents)).getBigDecimal("componentRate");
					}
					
				}
				
				if(UtilValidate.isNotEmpty(vatPercent) && vatPercent.compareTo(BigDecimal.ZERO)>0){
					BigDecimal compareVatPercent = vatPercent.setScale(6);
					
					condExpr.clear();
					condExpr.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, "VAT_PUR"));
					condExpr.add(EntityCondition.makeCondition("taxRate", EntityOperator.EQUALS, compareVatPercent));
					EntityCondition cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
					
					List<GenericValue> taxComponentMap = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
					taxComponentMap = EntityUtil.filterByDate(taxComponentMap, UtilDateTime.nowTimestamp());
					
					if(UtilValidate.isEmpty(taxComponentMap)){
						Debug.logError("Tax component configuration for VAT missing ", module);
						return ServiceUtil.returnError("Tax component configuration for VAT missing ");
					}
					
					List<GenericValue> vatTaxComponent = EntityUtil.filterByCondition(taxComponentMap, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS, "VAT_PUR"));
					if(UtilValidate.isNotEmpty(vatTaxComponent)){
						vatTaxPercent = (EntityUtil.getFirst(vatTaxComponent)).getBigDecimal("componentRate");
					}
					
				}
				
				if(UtilValidate.isNotEmpty(cstPercent) && cstPercent.compareTo(BigDecimal.ZERO)>0){
					BigDecimal compareCstPercent = cstPercent.setScale(6);
					
					condExpr.clear();
					condExpr.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, "CST_PUR"));
					condExpr.add(EntityCondition.makeCondition("taxRate", EntityOperator.EQUALS, compareCstPercent));
					EntityCondition cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
					
					List<GenericValue> taxComponentMap = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
					taxComponentMap = EntityUtil.filterByDate(taxComponentMap, UtilDateTime.nowTimestamp());
					
					if(UtilValidate.isEmpty(taxComponentMap)){
						Debug.logError("Tax component configuration for CST missing ", module);
						return ServiceUtil.returnError("Tax component configuration for CST missing ");
					}
					
					List<GenericValue> cstTaxComponent = EntityUtil.filterByCondition(taxComponentMap, EntityCondition.makeCondition("componentType", EntityOperator.EQUALS, "CST_PUR"));
					if(UtilValidate.isNotEmpty(cstTaxComponent)){
						cstTaxPercent = (EntityUtil.getFirst(cstTaxComponent)).getBigDecimal("componentRate");
					}
					
				}
				
				if(UtilValidate.isNotEmpty(incTax)){
					
					if(UtilValidate.isNotEmpty(vatTaxPercent) && vatTaxPercent.compareTo(BigDecimal.ZERO)>0){
						Map<String,Object> exVatRateMap = UtilAccounting.getExclusiveTaxRate(unitPrice,vatTaxPercent);
						vatUnitRate = (BigDecimal)exVatRateMap.get("taxAmount");
					}
					
					if(UtilValidate.isNotEmpty(cstTaxPercent) && cstTaxPercent.compareTo(BigDecimal.ZERO)>0){
						Map<String,Object> exCstRateMap = UtilAccounting.getExclusiveTaxRate(unitPrice,cstTaxPercent);
						cstUnitRate = (BigDecimal)exCstRateMap.get("taxAmount");
					}
					
					if(UtilValidate.isNotEmpty(bedTaxPercent) && bedTaxPercent.compareTo(BigDecimal.ZERO)>0){
						
						Map<String,Object> exBedRateMap = UtilAccounting.getExclusiveTaxRate(unitPrice.subtract(vatUnitRate.add(cstUnitRate)),bedTaxPercent);
						bedUnitRate = (BigDecimal)exBedRateMap.get("taxAmount");
						
						
						Map<String,Object> exBedCessRateMap = UtilAccounting.getExclusiveTaxRate(bedUnitRate,bedcessTaxPercent);
						bedCessUnitRate = (BigDecimal)exBedCessRateMap.get("taxAmount");
						
						
						Map<String,Object> exBedSecCessRateMap = UtilAccounting.getExclusiveTaxRate(bedUnitRate,bedseccessTaxPercent);
						bedSecCessUnitRate = (BigDecimal)exBedSecCessRateMap.get("taxAmount");
						
					}
					
				}
				else{
					if(UtilValidate.isNotEmpty(bedTaxPercent) && bedTaxPercent.compareTo(BigDecimal.ZERO)>0){
						
						Map<String,Object> exBedRateMap = UtilAccounting.getInclusiveTaxRate(unitPrice, bedTaxPercent);
						bedUnitRate = (BigDecimal)exBedRateMap.get("taxAmount");
	
						Map<String,Object> exBedCessRateMap = UtilAccounting.getInclusiveTaxRate(bedUnitRate, bedcessTaxPercent);
						bedCessUnitRate = (BigDecimal)exBedCessRateMap.get("taxAmount");
						
						Map<String,Object> exBedSecCessRateMap = UtilAccounting.getInclusiveTaxRate(bedUnitRate,bedseccessTaxPercent);
						bedSecCessUnitRate = (BigDecimal)exBedSecCessRateMap.get("taxAmount");
						
					}
					BigDecimal baseValue = unitPrice.add((bedUnitRate.add(bedCessUnitRate)).add(bedSecCessUnitRate));
					
					if(UtilValidate.isNotEmpty(vatTaxPercent) && vatTaxPercent.compareTo(BigDecimal.ZERO)>0){
						Map<String,Object> exVatRateMap = UtilAccounting.getInclusiveTaxRate(baseValue,vatTaxPercent);
						vatUnitRate = (BigDecimal)exVatRateMap.get("taxAmount");
					}
					
					if(UtilValidate.isNotEmpty(cstTaxPercent) && cstTaxPercent.compareTo(BigDecimal.ZERO)>0){
						Map<String,Object> exCstRateMap = UtilAccounting.getExclusiveTaxRate(baseValue,cstTaxPercent);
						cstUnitRate = (BigDecimal)exCstRateMap.get("taxAmount");
					}
				}
				if(unitPrice.compareTo(BigDecimal.ZERO)>0){
					if(!bedUnitRate.equals(BigDecimal.ZERO)){
						
						BigDecimal taxAmount = bedUnitRate.multiply(quantity);
						
						if(taxAmount.compareTo(BigDecimal.ZERO)>0){
			        		taxAmount = (taxAmount).setScale(salestaxCalcDecimals, salestaxRounding);
			        		Map taxDetailMap = FastMap.newInstance();
				    		taxDetailMap.put("taxType", "BED_PUR");
				    		taxDetailMap.put("amount", taxAmount);
				    		taxDetailMap.put("percentage", bedTaxPercent);
				    		taxList.add(taxDetailMap);
	             
				    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
				    		newProdPriceType.set("fromDate", effectiveDate);
				    		newProdPriceType.set("parentTypeId", "TAX");
				    		newProdPriceType.set("productId", productId);
				    		newProdPriceType.set("productStoreGroupId", "_NA_");
				    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
				    		newProdPriceType.set("productPriceTypeId", "BED_PUR");
				    		newProdPriceType.set("taxPercentage", bedTaxPercent);
				    		newProdPriceType.set("taxAmount", taxAmount);
				    		newProdPriceType.set("currencyUomId", "INR");
				    		prodPriceTypeList.add(newProdPriceType);
				    		totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
			        	}
			        	exciseDuty = exciseDuty.add(taxAmount);
			        	
					}

					if(!bedCessUnitRate.equals(BigDecimal.ZERO)){
					    
						BigDecimal taxAmount = bedCessUnitRate.multiply(quantity);

						if(taxAmount.compareTo(BigDecimal.ZERO)>0){
			        		//taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
			        		taxAmount=taxAmount.setScale(salestaxCalcDecimals, salestaxRounding);
			        		Map taxDetailMap = FastMap.newInstance();
				    		taxDetailMap.put("taxType", "BEDCESS_PUR");
				    		taxDetailMap.put("amount", taxAmount);
				    		taxDetailMap.put("percentage", bedcessTaxPercent);
				    		taxList.add(taxDetailMap);
				    		
				    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
				    		newProdPriceType.set("fromDate", effectiveDate);
				    		newProdPriceType.set("parentTypeId", "TAX");
				    		newProdPriceType.set("productId", productId);
				    		newProdPriceType.set("productStoreGroupId", "_NA_");
				    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
				    		newProdPriceType.set("productPriceTypeId", "BEDCESS_PUR");
				    		newProdPriceType.set("taxPercentage", bedcessTaxPercent);
				    		newProdPriceType.set("taxAmount", taxAmount);
				    		newProdPriceType.set("currencyUomId", "INR");
				    		prodPriceTypeList.add(newProdPriceType);
				    		totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
			        	}
			        	exciseDuty = exciseDuty.add(taxAmount);
					}

				    if(!bedSecCessUnitRate.equals(BigDecimal.ZERO)){
					    
				    	BigDecimal taxAmount = bedSecCessUnitRate.multiply(quantity);
						
						if(taxAmount.compareTo(BigDecimal.ZERO)>0){
			        		taxAmount=taxAmount.setScale(salestaxCalcDecimals, salestaxRounding);
			        		Map taxDetailMap = FastMap.newInstance();
				    		taxDetailMap.put("taxType", "BEDSECCESS_PUR");
				    		taxDetailMap.put("amount", taxAmount);
				    		taxDetailMap.put("percentage", bedseccessTaxPercent);
				    		taxList.add(taxDetailMap);
				        	
				    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
				    		newProdPriceType.set("fromDate", effectiveDate);
				    		newProdPriceType.set("parentTypeId", "TAX");
				    		newProdPriceType.set("productId", productId);
				    		newProdPriceType.set("productStoreGroupId", "_NA_");
				    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
				    		newProdPriceType.set("productPriceTypeId", "BEDSECCESS_PUR");
				    		newProdPriceType.set("taxPercentage", bedseccessTaxPercent);
				    		newProdPriceType.set("taxAmount", taxAmount);
				    		newProdPriceType.set("currencyUomId", "INR");
				    		prodPriceTypeList.add(newProdPriceType);
				    		totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
			        	}
			        	exciseDuty = exciseDuty.add(taxAmount);
					}
				    //discount before tax
				    for(Map eachAdj : otherChargesAdjustment){
						String adjustmentTypeId=(String)eachAdj.get("adjustmentTypeId");
						BigDecimal termValue =(BigDecimal)eachAdj.get("amount");
				    	//adjustCtx.put("amount", termValue);
				    	//Debug.log("eachAdj==========="+eachAdj);
				    	String uomId = (String)eachAdj.get("uomId");
				    	
				    	if(adjustmentTypeId.equals("COGS_DISC")){
				    		Map inputMap = UtilMisc.toMap("userLogin",userLogin);
				    		inputMap.put("termTypeId", adjustmentTypeId);
				    		inputMap.put("basicAmount", unitPrice.multiply(quantity));
				    		inputMap.put("exciseDuty", exciseDuty);
				    		inputMap.put("uomId", uomId);
				    		inputMap.put("termValue", termValue);
				    		//Debug.log("inputMap==========="+inputMap);
				    		BigDecimal termAmount = OrderServices.calculatePurchaseOrderTermValue(ctx,inputMap);
				    		discountBeforeTax = discountBeforeTax.add(termAmount);
					  	 	//modify the vat unit rate here
					  	 	if(uomId.equals("PERCENT") ){
					  	 		if(!cstUnitRate.equals(BigDecimal.ZERO)){
					  	 			cstUnitRate = cstUnitRate.subtract((cstUnitRate.multiply(termValue)).divide(new BigDecimal("100"), 3, BigDecimal.ROUND_HALF_UP));
					  	 		}
					  	 		if(!vatUnitRate.equals(BigDecimal.ZERO)){
					  	 			vatUnitRate = vatUnitRate.subtract((vatUnitRate.multiply(termValue)).divide(new BigDecimal("100"), 3, BigDecimal.ROUND_HALF_UP));
					  	 		}
					  	 		
					  	 	}
					  	 	basicAmount = basicAmount.add(termAmount);
				    	}	
					}
				    
				    if( !vatUnitRate.equals(BigDecimal.ZERO)){
					    
				    	BigDecimal taxAmount = vatUnitRate.multiply(quantity);
						//if ED not zero then add ED% value to tax amount
				    	//taxAmount = taxAmount.add(exciseDuty.multiply(vatPercent).divide(PERCENT_SCALE, 3, BigDecimal.ROUND_HALF_UP));
				    	
				    	if(taxAmount.compareTo(BigDecimal.ZERO)>0){
			        		taxAmount=taxAmount.setScale(salestaxCalcDecimals, salestaxRounding);
			        		Map taxDetailMap = FastMap.newInstance();
				    		taxDetailMap.put("taxType", "VAT_PUR");
				    		taxDetailMap.put("amount", taxAmount);
				    		taxDetailMap.put("percentage", vatPercent);
				    		taxList.add(taxDetailMap);

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
				    		totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
			        	}
					}
					if( !cstUnitRate.equals(BigDecimal.ZERO)){
						
						BigDecimal taxAmount = cstUnitRate.multiply(quantity);

						if(taxAmount.compareTo(BigDecimal.ZERO)>0){
			        		taxAmount=taxAmount.setScale(salestaxCalcDecimals, salestaxRounding);
			        		Map taxDetailMap = FastMap.newInstance();
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
				    		totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
			        	}
					}
				}
				BigDecimal totalPrice = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(incTax)){
					totalPrice = unitPrice;
					unitPrice = unitPrice.subtract(totalTaxAmt);
					
				}else{
					 totalPrice = unitPrice.add(totalTaxAmt);
				}
			//BigDecimal totalPrice = unitPrice;//as of now For PurchaseOrder listPrice is same like unitPrice
			basicAmount = basicAmount.add(unitPrice.multiply(quantity));
		
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
	    //ProductPromoWorker.doPromotions(cart, dispatcher);
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
		
		//set order Tax terms
		List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("orderItemSeqId", "productId"), null, null, false);
		for(GenericValue orderItem : orderItems){
			String prodId = orderItem.getString("productId");
			String orderItemSeqId = orderItem.getString("orderItemSeqId");
			List<Map> taxTerm = (List)taxTermsMap.get(prodId);
			if(UtilValidate.isNotEmpty(taxTerm)){
				Map orderTermResult = FastMap.newInstance();
				for(Map eachTaxTerm : taxTerm){
					Map termCreateCtx = FastMap.newInstance();
					termCreateCtx.put("userLogin", userLogin);
					termCreateCtx.put("orderId", orderId);
					termCreateCtx.put("orderItemSeqId", orderItemSeqId);
					termCreateCtx.put("termTypeId", (String)eachTaxTerm.get("termTypeId"));
					termCreateCtx.put("termValue", (BigDecimal)eachTaxTerm.get("termValue"));
					termCreateCtx.put("uomId", (String)eachTaxTerm.get("uomId"));
					orderTermResult = dispatcher.runSync("createOrderTerm",termCreateCtx);
					if (ServiceUtil.isError(orderTermResult)) {
						String errMsg =  ServiceUtil.getErrorMessage(orderTermResult);
						Debug.logError(errMsg, "While Creating Order Tax Term",module);
						return ServiceUtil.returnError(" Error While Creating Order Tax Term !"+errMsg);
					}
				}
			}
		}
		
		
		//let's create Fright Adjustment here
    	 Boolean COGS_DISC_ATR = Boolean.FALSE;
    	 Map COGS_DISC_ATR_Map = FastMap.newInstance();
    	 Boolean COGS_PCK_FWD_ATR = Boolean.FALSE;
    	 Map COGS_PCK_FWD_ATR_Map = FastMap.newInstance();
    	 
		for(Map eachAdj : otherChargesAdjustment){
			Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	
			String adjustmentTypeId=(String)eachAdj.get("adjustmentTypeId");
			BigDecimal termValue =(BigDecimal)eachAdj.get("amount");
	    	adjustCtx.put("orderId", orderId);
	    	adjustCtx.put("orderAdjustmentTypeId", adjustmentTypeId);
	    	//adjustCtx.put("amount", termValue);
	    	//Debug.log("eachAdj==========="+eachAdj);
	    	String uomId = (String)eachAdj.get("uomId");
	    	if(adjustmentTypeId.equals("COGS_DISC_ATR")){
	    		COGS_DISC_ATR = Boolean.TRUE;
	    		COGS_DISC_ATR_Map.putAll(eachAdj);
	    	}
	    	if(adjustmentTypeId.equals("COGS_PCK_FWD_ATR")){
	    		COGS_PCK_FWD_ATR = Boolean.TRUE;
	    		COGS_PCK_FWD_ATR_Map.putAll(eachAdj);
	    	}
	    	if(!adjustmentTypeId.equals("COGS_DISC_ATR") && !adjustmentTypeId.equals("COGS_PCK_FWD_ATR")){
	    		Map inputMap = UtilMisc.toMap("userLogin",userLogin);
	    		inputMap.put("termTypeId", adjustmentTypeId);
	    		inputMap.put("basicAmount", basicAmount);
	    		inputMap.put("exciseDuty", exciseDuty);
	    		inputMap.put("uomId", uomId);
	    		inputMap.put("termValue", termValue);
	    		//Debug.log("inputMap==========="+inputMap);
	    		BigDecimal termAmount =BigDecimal.ZERO;
	    		if(adjustmentTypeId.equals("COGS_DISC")){
	    			termAmount = discountBeforeTax;
	    		}else{
	    			termAmount = OrderServices.calculatePurchaseOrderTermValue(ctx,inputMap);
	    		}
	    		adjustCtx.put("amount", termAmount);
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
		//check discount after tax
		if(COGS_DISC_ATR){
			
			//GenericValue eachAdj = delegator.findOne("OrderTerm", UtilMisc.toMap("orderId",orderId,"orderItemSeqId","_NA_","termTypeId","COGS_DISC_ATR"), false);
			//Debug.log("COGS_DISC_ATR_Map attr==========="+COGS_DISC_ATR_Map);
			Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	
			String adjustmentTypeId=(String)COGS_DISC_ATR_Map.get("adjustmentTypeId");
			BigDecimal termValue =(BigDecimal)COGS_DISC_ATR_Map.get("amount");
	    	adjustCtx.put("orderId", orderId);
	    	adjustCtx.put("orderAdjustmentTypeId", adjustmentTypeId);
			String uomId = (String)COGS_DISC_ATR_Map.get("uomId");
			Map inputMap = UtilMisc.toMap("userLogin",userLogin);
    		inputMap.put("termTypeId", adjustmentTypeId);
    		inputMap.put("basicAmount", basicAmount);
    		inputMap.put("exciseDuty", exciseDuty);
    		inputMap.put("uomId", uomId);
    		inputMap.put("termValue", termValue);
    		OrderReadHelper orh = null;
    		try {
    			  orh = new OrderReadHelper(delegator, orderId);
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
    		BigDecimal poValue = orh.getOrderGrandTotal();
    		inputMap.put("poValue", poValue);
    		BigDecimal termAmount = OrderServices.calculatePurchaseOrderTermValue(ctx,inputMap);
    		adjustCtx.put("amount", termAmount);
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
			
		//check discount after tax
				if(COGS_PCK_FWD_ATR){
					
					//GenericValue eachAdj = delegator.findOne("OrderTerm", UtilMisc.toMap("orderId",orderId,"orderItemSeqId","_NA_","termTypeId","COGS_DISC_ATR"), false);
					//Debug.log("COGS_DISC_ATR_Map attr==========="+COGS_DISC_ATR_Map);
					Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	
					String adjustmentTypeId=(String)COGS_PCK_FWD_ATR_Map.get("adjustmentTypeId");
					BigDecimal termValue =(BigDecimal)COGS_PCK_FWD_ATR_Map.get("amount");
			    	adjustCtx.put("orderId", orderId);
			    	adjustCtx.put("orderAdjustmentTypeId", adjustmentTypeId);
					String uomId = (String)COGS_PCK_FWD_ATR_Map.get("uomId");
					Map inputMap = UtilMisc.toMap("userLogin",userLogin);
		    		inputMap.put("termTypeId", adjustmentTypeId);
		    		inputMap.put("basicAmount", basicAmount);
		    		inputMap.put("exciseDuty", exciseDuty);
		    		inputMap.put("uomId", uomId);
		    		inputMap.put("termValue", termValue);
		    		OrderReadHelper orh = null;
		    		try {
		    			  orh = new OrderReadHelper(delegator, orderId);
		            } catch (IllegalArgumentException e) {
		                return ServiceUtil.returnError(e.getMessage());
		            }
		    		BigDecimal poValue = orh.getOrderGrandTotal();
		    		inputMap.put("poValue", poValue);
		    		Debug.log("inputMap=========="+inputMap);
		    		BigDecimal termAmount = OrderServices.calculatePurchaseOrderTermValue(ctx,inputMap);
		    		adjustCtx.put("amount", termAmount);
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
	    
		}catch(Exception e){
			try {
				// only rollback the transaction if we started one...
	  			TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
			} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		}
			Debug.logError(e, "Could not rollback transaction: " + e.toString(), module);
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
	  	Map taxTermsMap = (Map) context.get("taxTermsMap");
	  	String incTax = (String) context.get("incTax");
	  	String billFromPartyId = (String) context.get("billFromPartyId");
		String issueToDeptId = (String) context.get("issueToDeptId");
	  	List termsList = (List)context.get("termsList");
	  	boolean beganTransaction = false;
	  	List<Map> otherChargesAdjustment = (List) context.get("otherChargesAdjustment");
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
		String fromDate = (String)context.get("fromDate");
		String thruDate = (String)context.get("thruDate");
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
		//these are input param to calcultae teramount based on order terms 
		BigDecimal basicAmount = BigDecimal.ZERO;
		//exciseDuty includes BED,CESS,SECESS
		BigDecimal exciseDuty = BigDecimal.ZERO;
		BigDecimal discountBeforeTax = BigDecimal.ZERO;
		try {
				
			List conList= FastList.newInstance();
     		conList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
     		conList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "BILL_FROM_VENDOR"));
     		EntityCondition cond=EntityCondition.makeCondition(conList,EntityOperator.AND);
			List<GenericValue> orderRoles = delegator.findList("OrderRole", cond, null, null, null, false);
			if(UtilValidate.isNotEmpty(orderRoles)){
				GenericValue orderRole = EntityUtil.getFirst(orderRoles); 
				String oldPartyId = orderRole.getString("partyId");
				if(UtilValidate.isEmpty(billFromPartyId)){
					billFromPartyId=partyId;
				}
				if(!billFromPartyId.equals(oldPartyId)){
					delegator.removeAll(orderRoles);
					GenericValue roleOrder = delegator.makeValue("OrderRole");   
					roleOrder.set("orderId", orderId);
					roleOrder.set("partyId", billFromPartyId);
					roleOrder.set("roleTypeId", "BILL_FROM_VENDOR");
					delegator.createOrStore(roleOrder);
				}
			}
			
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
			
			if(UtilValidate.isNotEmpty(fromDate)){
				GenericValue orderAttr = delegator.makeValue("OrderAttribute");
				orderAttr.set("orderId", orderId);
				orderAttr.set("attrName", "VALID_FROM");
				orderAttr.set("attrValue", fromDate);
				delegator.createOrStore(orderAttr);
			}
			if(UtilValidate.isNotEmpty(thruDate)){
				GenericValue orderAttr = delegator.makeValue("OrderAttribute");
				orderAttr.set("orderId", orderId);
				orderAttr.set("attrName", "VALID_THRU");
				orderAttr.set("attrValue", thruDate);
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
			
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("orderItemSeqId", "productId"), null, null, false);
			for(GenericValue orderItem : orderItems){
				String prodId = orderItem.getString("productId");
				String orderItemSeqId = orderItem.getString("orderItemSeqId");
				List<Map> taxTerm = (List)taxTermsMap.get(prodId);
				if(UtilValidate.isNotEmpty(taxTerm)){
					Map orderTermResult = FastMap.newInstance();
					for(Map eachTaxTerm : taxTerm){
						Map termCreateCtx = FastMap.newInstance();
						termCreateCtx.put("userLogin", userLogin);
						termCreateCtx.put("orderId", orderId);
						termCreateCtx.put("orderItemSeqId", orderItemSeqId);
						termCreateCtx.put("termTypeId", (String)eachTaxTerm.get("termTypeId"));
						termCreateCtx.put("termValue", (BigDecimal)eachTaxTerm.get("termValue"));
						termCreateCtx.put("uomId", (String)eachTaxTerm.get("uomId"));
						orderTermResult = dispatcher.runSync("createOrderTerm",termCreateCtx);
						if (ServiceUtil.isError(orderTermResult)) {
							String errMsg =  ServiceUtil.getErrorMessage(orderTermResult);
							Debug.logError(errMsg, "While Creating Order Tax Term",module);
							return ServiceUtil.returnError(" Error While Creating Order Tax Term !"+errMsg);
						}
					}
				}
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
					
					BigDecimal bedTaxPercent = BigDecimal.ZERO;
					BigDecimal bedcessTaxPercent = BigDecimal.ZERO;
					BigDecimal bedseccessTaxPercent = BigDecimal.ZERO;
					BigDecimal vatTaxPercent = BigDecimal.ZERO;
					BigDecimal cstTaxPercent = BigDecimal.ZERO;
					
					if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
						quantity = (BigDecimal)prodQtyMap.get("quantity");
						orderItemDetail.set("quantity", quantity);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
						unitPrice = (BigDecimal)prodQtyMap.get("unitPrice");
						orderItemDetail.set("unitPrice", unitPrice);
					}

					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedPercent"))){
						
						BigDecimal bedPercent = (BigDecimal)prodQtyMap.get("bedPercent");
						
						if(bedPercent.compareTo(BigDecimal.ZERO)>0){
							
							Map tempMap = FastMap.newInstance();
							tempMap.put("userLogin", userLogin);
							tempMap.put("taxRate", bedPercent);
							tempMap.put("taxType", "EXCISE_DUTY_PUR");
							Map resultCtx = MaterialHelperServices.getOrderTaxComponentBreakUp(ctx, tempMap);
							if(ServiceUtil.isError(resultCtx)){
								String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				  	 			Debug.logError(errMsg , module);
				  	 			return ServiceUtil.returnError(" Error while striping tax component for Purchase Order !");
							}
							
							Map taxComponent = (Map)resultCtx.get("taxComponents");
							
							if(UtilValidate.isEmpty(taxComponent)){
								Debug.logError("Tax component configuration for Excise Duty is missing ", module);
								return ServiceUtil.returnError("Tax component configuration for Excise Duty is missing ");
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
						}
					}	
					
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
						BigDecimal vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
						
						if(vatPercent.compareTo(BigDecimal.ZERO)>0){
							
							Map tempMap = FastMap.newInstance();
							tempMap.put("userLogin", userLogin);
							tempMap.put("taxRate", vatPercent);
							tempMap.put("taxType", "VAT_PUR");
							Map resultCtx = MaterialHelperServices.getOrderTaxComponentBreakUp(ctx, tempMap);
							if(ServiceUtil.isError(resultCtx)){
								String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				  	 			Debug.logError(errMsg , module);
				  	 			return ServiceUtil.returnError(" Error while striping tax component for Purchase Order !");
							}
							
							Map taxComponent = (Map)resultCtx.get("taxComponents");
							if(UtilValidate.isNotEmpty(taxComponent.get("VAT_PUR"))){
								vatTaxPercent = (BigDecimal)taxComponent.get("VAT_PUR");
							}
							else{
								Debug.logError("Tax component configuration for VAT is missing ", module);
								return ServiceUtil.returnError("Tax component configuration for VAT is missing ");
							}
						}
					}
					
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
						
						BigDecimal cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
						
						if(cstPercent.compareTo(BigDecimal.ZERO)>0){
							Map tempMap = FastMap.newInstance();
							tempMap.put("userLogin", userLogin);
							tempMap.put("taxRate", cstPercent);
							tempMap.put("taxType", "CST_PUR");
							Map resultCtx = MaterialHelperServices.getOrderTaxComponentBreakUp(ctx, tempMap);
							if(ServiceUtil.isError(resultCtx)){
								String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				  	 			Debug.logError(errMsg , module);
				  	 			return ServiceUtil.returnError(" Error while striping tax component for Purchase Order !");
							}
							
							Map taxComponent = (Map)resultCtx.get("taxComponents");
							
							if(UtilValidate.isNotEmpty(taxComponent.get("CST_PUR"))){
								cstTaxPercent = (BigDecimal)taxComponent.get("CST_PUR");
							}
							else{
								Debug.logError("Tax component configuration for CST is missing ", module);
								return ServiceUtil.returnError("Tax component configuration for CST is missing ");
							}
						}
					}

					if(UtilValidate.isNotEmpty(incTax)){
						
						if(UtilValidate.isNotEmpty(vatTaxPercent) && vatTaxPercent.compareTo(BigDecimal.ZERO)>0){
							Map<String,Object> exVatRateMap = UtilAccounting.getExclusiveTaxRate(unitPrice,vatTaxPercent);
							vatAmount = (BigDecimal)exVatRateMap.get("taxAmount");
						}
						
						if(UtilValidate.isNotEmpty(cstTaxPercent) && cstTaxPercent.compareTo(BigDecimal.ZERO)>0){
							Map<String,Object> exCstRateMap = UtilAccounting.getExclusiveTaxRate(unitPrice,cstTaxPercent);
							cstAmount = (BigDecimal)exCstRateMap.get("taxAmount");
						}
						
						if(UtilValidate.isNotEmpty(bedTaxPercent) && bedTaxPercent.compareTo(BigDecimal.ZERO)>0){
							
							Map<String,Object> exBedRateMap = UtilAccounting.getExclusiveTaxRate(unitPrice.subtract(vatAmount.add(cstAmount)),bedTaxPercent);
							bedAmount = (BigDecimal)exBedRateMap.get("taxAmount");
							
							Map<String,Object> exBedCessRateMap = UtilAccounting.getExclusiveTaxRate(bedAmount,bedcessTaxPercent);
							bedcessAmount = (BigDecimal)exBedCessRateMap.get("taxAmount");
							
							Map<String,Object> exBedSecCessRateMap = UtilAccounting.getExclusiveTaxRate(bedAmount,bedseccessTaxPercent);
							bedseccessAmount = (BigDecimal)exBedSecCessRateMap.get("taxAmount");
							
						}
						
					}
					else{
						if(UtilValidate.isNotEmpty(bedTaxPercent) && bedTaxPercent.compareTo(BigDecimal.ZERO)>0){
							
							Map<String,Object> exBedRateMap = UtilAccounting.getInclusiveTaxRate(unitPrice, bedTaxPercent);
							bedAmount = (BigDecimal)exBedRateMap.get("taxAmount");
		
							Map<String,Object> exBedCessRateMap = UtilAccounting.getInclusiveTaxRate(bedAmount, bedcessTaxPercent);
							bedcessAmount = (BigDecimal)exBedCessRateMap.get("taxAmount");
							
							Map<String,Object> exBedSecCessRateMap = UtilAccounting.getInclusiveTaxRate(bedAmount,bedseccessTaxPercent);
							bedseccessAmount = (BigDecimal)exBedSecCessRateMap.get("taxAmount");
							
						}
						
						BigDecimal baseValue = unitPrice.add((bedAmount.add(bedcessAmount)).add(bedseccessAmount));
						
						if(UtilValidate.isNotEmpty(vatTaxPercent) && vatTaxPercent.compareTo(BigDecimal.ZERO)>0){
							Map<String,Object> exVatRateMap = UtilAccounting.getInclusiveTaxRate(baseValue,vatTaxPercent);
							vatAmount = (BigDecimal)exVatRateMap.get("taxAmount");
						}
						
						if(UtilValidate.isNotEmpty(cstTaxPercent) && cstTaxPercent.compareTo(BigDecimal.ZERO)>0){
							Map<String,Object> exCstRateMap = UtilAccounting.getExclusiveTaxRate(baseValue,cstTaxPercent);
							cstAmount = (BigDecimal)exCstRateMap.get("taxAmount");
						}
					}
					if(unitPrice.compareTo(BigDecimal.ZERO)>0){

						if(!bedAmount.equals(BigDecimal.ZERO)){
							
							BigDecimal taxAmount = bedAmount.multiply(quantity);
							if(taxAmount.compareTo(BigDecimal.ZERO)>0){
				        		taxAmount = (taxAmount).setScale(salestaxCalcDecimals, salestaxRounding);
							}
							totalBedAmount = totalBedAmount.add(taxAmount);
							orderItemDetail.set("bedPercent", bedTaxPercent);
							orderItemDetail.set("bedAmount", taxAmount);
							totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
							exciseDuty = exciseDuty.add(taxAmount);
						}
						else{
							orderItemDetail.set("bedPercent", null);
							orderItemDetail.set("bedAmount", null);
						}
						
						if(!bedcessAmount.equals(BigDecimal.ZERO)){
							
							BigDecimal taxAmount = bedcessAmount.multiply(quantity);
							if(taxAmount.compareTo(BigDecimal.ZERO)>0){
				        		taxAmount = (taxAmount).setScale(salestaxCalcDecimals, salestaxRounding);
							}
							totalBedCessAmount = totalBedCessAmount.add(taxAmount);
							orderItemDetail.set("bedcessPercent", bedcessTaxPercent);
							orderItemDetail.set("bedcessAmount", taxAmount);
							totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
							exciseDuty = exciseDuty.add(taxAmount);
						}
						else{
							orderItemDetail.set("bedcessPercent", null);
							orderItemDetail.set("bedcessAmount", null);
						}
						
						if(!bedseccessAmount.equals(BigDecimal.ZERO)){
							
							BigDecimal taxAmount = bedseccessAmount.multiply(quantity);
							if(taxAmount.compareTo(BigDecimal.ZERO)>0){
				        		taxAmount = (taxAmount).setScale(salestaxCalcDecimals, salestaxRounding);
							}
							totalBedSecCessAmount = totalBedSecCessAmount.add(taxAmount);
							orderItemDetail.set("bedseccessPercent", bedseccessTaxPercent);
							orderItemDetail.set("bedseccessAmount", taxAmount);
							totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
							exciseDuty = exciseDuty.add(taxAmount);
						}
						else{
							orderItemDetail.set("bedseccessPercent", null);
							orderItemDetail.set("bedseccessAmount", null);
						}
						
						//discount before tax
					    for(Map eachAdj : otherChargesAdjustment){
							String adjustmentTypeId=(String)eachAdj.get("adjustmentTypeId");
							BigDecimal termValue =(BigDecimal)eachAdj.get("amount");
					    	//adjustCtx.put("amount", termValue);
					    	//Debug.log("eachAdj==========="+eachAdj);
					    	String uomId = (String)eachAdj.get("uomId");
					    	
					    	if(adjustmentTypeId.equals("COGS_DISC")){
					    		Map inputMap = UtilMisc.toMap("userLogin",userLogin);
					    		inputMap.put("termTypeId", adjustmentTypeId);
					    		inputMap.put("basicAmount", unitPrice.multiply(quantity));
					    		inputMap.put("exciseDuty", exciseDuty);
					    		inputMap.put("uomId", uomId);
					    		inputMap.put("termValue", termValue);
					    		//Debug.log("inputMap==========="+inputMap);
					    		BigDecimal termAmount = OrderServices.calculatePurchaseOrderTermValue(ctx,inputMap);
					    		discountBeforeTax = discountBeforeTax.add(termAmount);
						  	 	//modify the vat unit rate here
						  	 	if(uomId.equals("PERCENT") ){
						  	 		if(!cstAmount.equals(BigDecimal.ZERO)){
						  	 			cstAmount = cstAmount.subtract((cstAmount.multiply(termValue)).divide(new BigDecimal("100"), 3, BigDecimal.ROUND_HALF_UP));
						  	 		}
						  	 		if(!vatAmount.equals(BigDecimal.ZERO)){
						  	 			vatAmount = vatAmount.subtract((vatAmount.multiply(termValue)).divide(new BigDecimal("100"), 3, BigDecimal.ROUND_HALF_UP));
						  	 		}
						  	 		
						  	 	}
						  	 	basicAmount = basicAmount.add(termAmount);
					    	}	
						}
						
						if(!vatAmount.equals(BigDecimal.ZERO)){
							
							BigDecimal taxAmount = vatAmount.multiply(quantity);
							if(taxAmount.compareTo(BigDecimal.ZERO)>0){
				        		taxAmount = (taxAmount).setScale(salestaxCalcDecimals, salestaxRounding);
							}
							totalVatAmount = totalVatAmount.add(taxAmount);
							orderItemDetail.set("vatPercent", vatTaxPercent);
							orderItemDetail.set("vatAmount", taxAmount);
							totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
						}
						else{
							orderItemDetail.set("vatPercent", null);
							orderItemDetail.set("vatAmount", null);
						}
						
						if(!cstAmount.equals(BigDecimal.ZERO)){
							
							BigDecimal taxAmount = cstAmount.multiply(quantity);
							if(taxAmount.compareTo(BigDecimal.ZERO)>0){
				        		taxAmount = (taxAmount).setScale(salestaxCalcDecimals, salestaxRounding);
							}
							totalCstAmount = totalCstAmount.add(taxAmount);
							orderItemDetail.set("cstPercent", cstTaxPercent);
							orderItemDetail.set("cstAmount", taxAmount);
							totalTaxAmt=totalTaxAmt.add(taxAmount.divide(quantity, 3, BigDecimal.ROUND_HALF_UP));
						}
						else{
							orderItemDetail.set("cstPercent", null);
							orderItemDetail.set("cstAmount", null);
						}
					}
					
					BigDecimal totalPrice = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(incTax)){
						totalPrice = unitPrice;
						unitPrice = unitPrice.subtract(totalTaxAmt);
						
					}else{
						 totalPrice = unitPrice.add(totalTaxAmt);
					}
					orderItemDetail.set("unitListPrice", unitListPrice);
					orderItemDetail.set("changeByUserLoginId", userLogin.getString("userLoginId"));
					orderItemDetail.set("changeDatetime", UtilDateTime.nowTimestamp());
					orderItemDetail.store();
				}
			}
			
			List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			delegator.removeAll(orderAdjustments);
			
			if(totalBedAmount.compareTo(BigDecimal.ZERO)>0){
				Map taxAdjMap = FastMap.newInstance();
				taxAdjMap.put("adjustmentTypeId", "BED_PUR");
				taxAdjMap.put("amount", totalBedAmount);
				taxAdjMap.put("uomId", "INR");
				otherChargesAdjustment.add(taxAdjMap);
			}
			if(totalBedCessAmount.compareTo(BigDecimal.ZERO)>0){
				Map taxAdjMap = FastMap.newInstance();
				taxAdjMap.put("adjustmentTypeId", "BEDCESS_PUR");
				taxAdjMap.put("amount", totalBedCessAmount);
				taxAdjMap.put("uomId", "INR");
				otherChargesAdjustment.add(taxAdjMap);
			}
			if(totalBedSecCessAmount.compareTo(BigDecimal.ZERO)>0){
				Map taxAdjMap = FastMap.newInstance();
				taxAdjMap.put("adjustmentTypeId", "BEDSECCESS_PUR");
				taxAdjMap.put("amount", totalBedSecCessAmount);
				taxAdjMap.put("uomId", "INR");
				otherChargesAdjustment.add(taxAdjMap);
			}
			if(totalVatAmount.compareTo(BigDecimal.ZERO)>0){
				Map taxAdjMap = FastMap.newInstance();
				taxAdjMap.put("adjustmentTypeId", "VAT_PUR");
				taxAdjMap.put("amount", totalVatAmount);
				taxAdjMap.put("uomId", "INR");
				otherChargesAdjustment.add(taxAdjMap);
			}
			if(totalCstAmount.compareTo(BigDecimal.ZERO)>0){
				Map taxAdjMap = FastMap.newInstance();
				taxAdjMap.put("adjustmentTypeId", "CST_PUR");
				taxAdjMap.put("amount", totalCstAmount);
				taxAdjMap.put("uomId", "INR");
				otherChargesAdjustment.add(taxAdjMap);
			}
			
			Boolean COGS_DISC_ATR = Boolean.FALSE;
	    	Map COGS_DISC_ATR_Map = FastMap.newInstance();
            Boolean COGS_PCK_FWD_ATR =Boolean.FALSE;
            Map COGS_PCK_FWD_ATR_Map = FastMap.newInstance();
            
			for(Map orderAdj : otherChargesAdjustment){
				String adjustmentTypeId = (String)orderAdj.get("adjustmentTypeId");
				BigDecimal amount = (BigDecimal)orderAdj.get("amount");
				String uomId = (String)orderAdj.get("uomId");
		    	if(adjustmentTypeId.equals("COGS_DISC_ATR")){
		    		COGS_DISC_ATR = Boolean.TRUE;
		    		COGS_DISC_ATR_Map.putAll(orderAdj);
		    	}
		    	
		    	if(adjustmentTypeId.equals("COGS_PCK_FWD_ATR")){
		    		COGS_PCK_FWD_ATR = Boolean.TRUE;
		    		COGS_PCK_FWD_ATR_Map.putAll(orderAdj);
		    	}
		    	if(!adjustmentTypeId.equals("COGS_DISC_ATR") && !adjustmentTypeId.equals("COGS_PCK_FWD_ATR")){
		    		Map adjustCtx = FastMap.newInstance();	  	
					adjustCtx.put("userLogin", userLogin);
					adjustCtx.put("orderId", orderId);
					adjustCtx.put("orderAdjustmentTypeId", adjustmentTypeId);
		    		Map inputMap = UtilMisc.toMap("userLogin",userLogin);
		    		inputMap.put("termTypeId", adjustmentTypeId);
		    		inputMap.put("basicAmount", basicAmount);
		    		inputMap.put("exciseDuty", exciseDuty);
		    		inputMap.put("uomId", uomId);
		    		inputMap.put("termValue", amount);
		    		BigDecimal termAmount =BigDecimal.ZERO;
		    		if(adjustmentTypeId.equals("COGS_DISC")){
		    			termAmount = discountBeforeTax;
		    		}else{
		    			termAmount = OrderServices.calculatePurchaseOrderTermValue(ctx,inputMap);
		    		}
		    		adjustCtx.put("amount", termAmount);
		    		amount = OrderServices.calculatePurchaseOrderTermValue(ctx,inputMap);
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
			
			//check discount after tax
			if(COGS_DISC_ATR){
				//Debug.log("COGS_DISC_ATR_Map attr==========="+COGS_DISC_ATR_Map);
				Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	
				String adjustmentTypeId=(String)COGS_DISC_ATR_Map.get("adjustmentTypeId");
				BigDecimal termValue =(BigDecimal)COGS_DISC_ATR_Map.get("amount");
		    	adjustCtx.put("orderId", orderId);
		    	adjustCtx.put("orderAdjustmentTypeId", adjustmentTypeId);
				String uomId = (String)COGS_DISC_ATR_Map.get("uomId");
				Map inputMap = UtilMisc.toMap("userLogin",userLogin);
	    		inputMap.put("termTypeId", adjustmentTypeId);
	    		inputMap.put("basicAmount", basicAmount);
	    		inputMap.put("exciseDuty", exciseDuty);
	    		inputMap.put("uomId", uomId);
	    		inputMap.put("termValue", termValue);
	    		OrderReadHelper orh = null;
	    		try {
	    			  orh = new OrderReadHelper(delegator, orderId);
	            } catch (IllegalArgumentException e) {
	                return ServiceUtil.returnError(e.getMessage());
	            }
	    		BigDecimal poValue = orh.getOrderGrandTotal();
	    		inputMap.put("poValue", poValue);
	    		Debug.log("inputMap==========="+inputMap);
	    		BigDecimal termAmount = OrderServices.calculatePurchaseOrderTermValue(ctx,inputMap);
	    		adjustCtx.put("amount", termAmount);
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
			if(COGS_PCK_FWD_ATR){
				
				//GenericValue eachAdj = delegator.findOne("OrderTerm", UtilMisc.toMap("orderId",orderId,"orderItemSeqId","_NA_","termTypeId","COGS_DISC_ATR"), false);
				//Debug.log("COGS_DISC_ATR_Map attr==========="+COGS_DISC_ATR_Map);
				Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	
				String adjustmentTypeId=(String)COGS_PCK_FWD_ATR_Map.get("adjustmentTypeId");
				BigDecimal termValue =(BigDecimal)COGS_PCK_FWD_ATR_Map.get("amount");
		    	adjustCtx.put("orderId", orderId);
		    	adjustCtx.put("orderAdjustmentTypeId", adjustmentTypeId);
				String uomId = (String)COGS_PCK_FWD_ATR_Map.get("uomId");
				Map inputMap = UtilMisc.toMap("userLogin",userLogin);
	    		inputMap.put("termTypeId", adjustmentTypeId);
	    		inputMap.put("basicAmount", basicAmount);
	    		inputMap.put("exciseDuty", exciseDuty);
	    		inputMap.put("uomId", uomId);
	    		inputMap.put("termValue", termValue);
	    		OrderReadHelper orh = null;
	    		try {
	    			  orh = new OrderReadHelper(delegator, orderId);
	            } catch (IllegalArgumentException e) {
	                return ServiceUtil.returnError(e.getMessage());
	            }
	    		BigDecimal poValue = orh.getOrderGrandTotal();
	    		inputMap.put("poValue", poValue);
	    		BigDecimal termAmount = OrderServices.calculatePurchaseOrderTermValue(ctx,inputMap);
	    		adjustCtx.put("amount", termAmount);
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
		if(quantityAccepted.compareTo(BigDecimal.ZERO) ==-1){
			return ServiceUtil.returnError("negative value not allowed");
		}
		try{
			
			GenericValue shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
			
			GenericValue shipmentItem = delegator.findOne("ShipmentItem", UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", shipmentItemSeqId), false);
			BigDecimal origReceiptQty=BigDecimal.ZERO;
			origReceiptQty = shipmentItem.getBigDecimal("quantity");
			BigDecimal rejectedQty = origReceiptQty.subtract(quantityAccepted);
			
			if(quantityAccepted.compareTo(origReceiptQty) >0){
				return ServiceUtil.returnError("not accept more than the received quantity");
			}
			shipmentReceipt.put("quantityAccepted", quantityAccepted);
			shipmentReceipt.put("quantityRejected", rejectedQty);
			shipmentReceipt.put("statusId", statusId);
			shipmentReceipt.store();
			
			String inventoryItemId = shipmentReceipt.getString("inventoryItemId");
			
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
			
			if(UtilValidate.isNotEmpty(statusId) && !statusId.equals("SR_REJECTED")){
				GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
				inventoryItem.set("ownerPartyId", "Company");
				inventoryItem.store();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result = ServiceUtil.returnSuccess("Quality check passed for GRN no: "+receiptId);
		return result;
	}
   	public static Map<String, Object> createOrderAssoc(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String) context.get("orderId");
		String toOrderId = (String) context.get("toOrderId");
		String orderAssocTypeId = "";
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try{
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", toOrderId), false);
			if(UtilValidate.isEmpty(orderHeader)){
				Debug.logError("Please Enter Valid PO Number", module);
				return ServiceUtil.returnError("Please Enter Valid PO Number");
			}
			orderAssocTypeId = orderHeader.getString("orderTypeId");
			GenericValue orderAssoc = delegator.findOne("OrderAssoc", UtilMisc.toMap("orderId", orderId, "toOrderId", toOrderId, "orderAssocTypeId", orderAssocTypeId), false);
			if(UtilValidate.isEmpty(orderAssoc)){
				GenericValue newEntity = delegator.makeValue("OrderAssoc");
				newEntity.set("orderId", orderId);
				newEntity.set("toOrderId", toOrderId);
				newEntity.set("orderAssocTypeId", orderAssocTypeId);
				newEntity.create();
			}else{
				String oldOrderId = orderAssoc.getString("orderId");
				String oldtoOrderId = orderAssoc.getString("toOrderId");
				String oldorderAssocTypeId = orderAssoc.getString("orderAssocTypeId");
				if(!oldOrderId.equals(orderId)){
					orderAssoc.set("orderId",orderId);
				}
				if(!oldtoOrderId.equals(toOrderId)){
					orderAssoc.set("toOrderId",toOrderId);
				}
				if(!oldorderAssocTypeId.equals(orderAssocTypeId)){
					orderAssoc.set("orderAssocTypeId",orderAssocTypeId);
				}
				orderAssoc.store();
			}
		} catch(Exception e){
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("orderId", orderId);
		return result;
	}
   	
   	public static String amendPOItemEvent(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String effectiveDateStr = (String) request.getParameter("amendedDate");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	    String reasonEnumId = (String) request.getParameter("reasonEnumId");
	    String changeComments = (String) request.getParameter("changeComments");
	    
		Timestamp effectiveDate = null;
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
		effectiveDate = UtilDateTime.nowTimestamp();
	  	if(UtilValidate.isNotEmpty(effectiveDateStr)){
	  		try {
	  			effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
		  	}
	  	}
	  	
	  	try{
	  		//
		  	for (int i = 1; i < rowCount; i++) {
				
		  		String orderId = "";
		        String orderItemSeqId = "";
				String productId = "";
		        String amendedPriceStr = "";
		        String amendedQuantityStr= "";
				BigDecimal amendedQuantity = BigDecimal.ZERO;
				BigDecimal amendedPrice = BigDecimal.ZERO;
				Map productQtyMap = FastMap.newInstance();
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				
				if (paramMap.containsKey("orderId" + thisSuffix)) {
					orderId = (String) paramMap.get("orderId" + thisSuffix);
				}else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing order id");
					return "error";			  
				}
				request.setAttribute("orderId",orderId);
				if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
					orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
				}else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing orderItemSeq Id");
					return "error";			  
				}
				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
				}else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
					return "error";			  
				}
			  
				if (paramMap.containsKey("amendedQuantity" + thisSuffix)) {
					amendedQuantityStr = (String) paramMap.get("amendedQuantity" + thisSuffix);
				}
						  
				if(UtilValidate.isNotEmpty(amendedQuantityStr)){
					amendedQuantity = new BigDecimal(amendedQuantityStr);
				}
				if (paramMap.containsKey("amendedPrice" + thisSuffix)) {
					amendedPriceStr = (String) paramMap.get("amendedPrice" + thisSuffix);
				}
						  
				if(UtilValidate.isNotEmpty(amendedPriceStr)){
					amendedPrice = new BigDecimal(amendedPriceStr);
				}
				GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",orderId,"orderItemSeqId",orderItemSeqId), false);
				//GenericValue orderItemChange = delegator.makeValue("OrderItemChange");
				Map<String, Object> orderItemChange = FastMap.newInstance();
				orderItemChange.put("userLogin", userLogin);
                //
				orderItemChange.put("quantity", orderItem.getBigDecimal("quantity"));
				orderItemChange.put("unitPrice", orderItem.getBigDecimal("unitPrice"));
				
				orderItemChange.put("changeTypeEnumId", "ODR_ITM_AMEND");
				orderItemChange.put("orderId", orderId);
				orderItemChange.put("orderItemSeqId", orderItemSeqId);
				
				if(amendedQuantity.compareTo(BigDecimal.ZERO) !=0){
					orderItemChange.put("quantity", amendedQuantity);
				}
				if(amendedPrice.compareTo(BigDecimal.ZERO) !=0){
					orderItemChange.put("unitPrice", amendedPrice);
				}
				
				orderItemChange.put("effectiveDatetime",effectiveDate);
				orderItemChange.put("changeDatetime", UtilDateTime.nowTimestamp());
				orderItemChange.put("changeUserLogin",userLogin.getString("userLoginId"));
				orderItemChange.put("reasonEnumId", reasonEnumId);
				orderItemChange.put("changeComments", changeComments);
			    
				Map resultMap = dispatcher.runSync("createOrderItemChange",orderItemChange);
		        
		        if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError("Problem creating order Item  change for orderId :"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem creating order Item  change for orderId :"+orderId);	
					TransactionUtil.rollback();
			  		return "error";
		        }
		        request.setAttribute("orderId",orderId);
		  	}
	  	}catch(Exception e){
	  		
	  	}
	  	request.setAttribute("_EVENT_MESSAGE_", "Successfully Done");	
		return "sucess";
   	}
   	public static String processReturnItems(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String requestDateStr = (String) request.getParameter("requestDate");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	    String partyId = (String) request.getParameter("partyId");
	    String returnReasonId = (String) request.getParameter("returnReasonId");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create request without partyId: "+ partyId, module);
			return "error";
		}
		Timestamp requestDate = null;
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
	  	if(UtilValidate.isNotEmpty(requestDateStr)){
	  		try {
	  			requestDate = new java.sql.Timestamp(sdf.parse(requestDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + requestDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + requestDateStr, module);
		  	}
	  	}
	  	else{
	  		requestDate = UtilDateTime.nowTimestamp();
	  	}

	  	boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			GenericValue party = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "INTERNAL_ORGANIZATIO"), false);
			if(UtilValidate.isEmpty(party)){
				Debug.logError("Request can only made by departments", module);
				request.setAttribute("_ERROR_MESSAGE_", "Request can only made by departments");
				TransactionUtil.rollback();
		  		return "error";
			}
			
			Map returnHeaderCtx = FastMap.newInstance();
			returnHeaderCtx.put("returnHeaderTypeId", "DEPARTMENT_RETURN");
			returnHeaderCtx.put("fromPartyId", partyId);
			returnHeaderCtx.put("toPartyId", "INT16");
			returnHeaderCtx.put("entryDate", nowTimeStamp);
			returnHeaderCtx.put("returnDate", requestDate);
			returnHeaderCtx.put("statusId", "RETURN_ACCEPTED");
			returnHeaderCtx.put("userLogin", userLogin);
			result = dispatcher.runSync("createReturnHeader", returnHeaderCtx);
			if (ServiceUtil.isError(result)) {
    		  	Debug.logError("Problem creating return from party :"+partyId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problem creating return from party :"+partyId);	
				TransactionUtil.rollback();
		  		return "error";          	            
			} 
			String returnId = (String)result.get("returnId");
    	  
	        String productId = "";
	        String quantityStr = "";
			BigDecimal quantity = BigDecimal.ZERO;
			for (int i = 0; i < rowCount; i++) {
				  
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
			
				GenericValue returnItem = delegator.makeValue("ReturnItem");
	    		returnItem.put("returnReasonId", "RTN_DEFECTIVE_ITEM");
	    		if(UtilValidate.isNotEmpty(returnReasonId)){
	    			returnItem.put("returnReasonId", returnReasonId);
	    		}
	    		returnItem.put("statusId", "RETURN_REQUESTED");
	    		returnItem.put("returnId", returnId);
	    		returnItem.put("productId", productId);
	    		returnItem.put("returnQuantity", quantity);
	    		returnItem.put("returnTypeId", "RTN_CREDIT");
	    		returnItem.put("returnItemTypeId", "RET_FPROD_ITEM");
    		    delegator.setNextSubSeqId(returnItem, "returnItemSeqId", 5, 1);
	    		delegator.create(returnItem);
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
		request.setAttribute("_EVENT_MESSAGE_", "Successfully made return goods entries ");
		return "success";
	}
   	
   	public static Map<String, Object> acceptReturnItemForReceipt(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String returnId = (String) context.get("returnId");
		String returnItemSeqId = (String) context.get("returnItemSeqId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			
			GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
			
			GenericValue returnItem = delegator.findOne("ReturnItem", UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId), false);
			
			if(UtilValidate.isEmpty(returnItem)){
				Debug.logError("No Items found with Id: [" + returnId+" : "+returnItemSeqId+"]", module);
				return ServiceUtil.returnError("No Items found with Id: [" + returnId+" : "+returnItemSeqId+"]");
			}
			
			String productId = returnItem.getString("productId");
			List<GenericValue> productsFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
			
			GenericValue facilityProd = EntityUtil.getFirst(productsFacility);
			//Product should mapped to any one of facility
			if (UtilValidate.isEmpty(facilityProd)) {
				Debug.logError("Problem creating receipt for productId :"+productId+" Not Mapped To Store Facility !", module);
		        return ServiceUtil.returnError("Problem creating receipt for ProductId :"+productId+" Not Mapped To Store Facility!");	
		    }
			 
			Map inventoryReceiptCtx = FastMap.newInstance();
			
			inventoryReceiptCtx.put("userLogin", userLogin);
			inventoryReceiptCtx.put("productId", returnItem.getString("productId"));
			inventoryReceiptCtx.put("datetimeReceived", returnHeader.getTimestamp("returnDate"));
			inventoryReceiptCtx.put("quantityAccepted", returnItem.getBigDecimal("returnQuantity"));
			inventoryReceiptCtx.put("quantityRejected", BigDecimal.ZERO);
			inventoryReceiptCtx.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
			inventoryReceiptCtx.put("ownerPartyId", "Company");
			inventoryReceiptCtx.put("partyId", returnHeader.getString("fromPartyId"));
			/*inventoryReceiptCtx.put("consolidateInventoryReceive", "Y");*/
			inventoryReceiptCtx.put("facilityId", facilityProd.getString("facilityId"));
			inventoryReceiptCtx.put("unitCost", BigDecimal.ZERO);
			if(UtilValidate.isNotEmpty(returnItem.getBigDecimal("returnPrice"))){
				inventoryReceiptCtx.put("unitCost", returnItem.getBigDecimal("returnPrice"));
			}
			Map<String, Object> receiveInventoryResult;
			receiveInventoryResult = dispatcher.runSync("receiveInventoryProduct", inventoryReceiptCtx);
			
			if (ServiceUtil.isError(receiveInventoryResult)) {
				Debug.logWarning("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
				return ServiceUtil.returnError("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult));	
            }
			
			String receiptId = (String)receiveInventoryResult.get("receiptId");
			GenericValue shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
			if(UtilValidate.isNotEmpty(shipmentReceipt)){
				shipmentReceipt.set("returnId", returnId);
				shipmentReceipt.set("statusId", "SR_ACCEPTED");
				shipmentReceipt.set("returnItemSeqId", returnItemSeqId);
				shipmentReceipt.store();
			}
			
			returnItem.set("statusId", "SR_ACCEPTED");
			returnItem.store();
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
   	
	public static Map<String, Object> setRequestStatus(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String statusId = (String) context.get("statusId");
		String custRequestId = (String) context.get("custRequestId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			Map statusCtx = FastMap.newInstance();
			statusCtx.put("statusId", statusId);
			statusCtx.put("custRequestId", custRequestId);
			statusCtx.put("userLogin", userLogin);
			Map resultCtx = dispatcher.runSync("setCustRequestStatus", statusCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Request set status failed for RequestId: " + custRequestId, module);
				return resultCtx;
			}
			
			List<GenericValue> custRequestItems = delegator.findList("CustRequestItem", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId), null, null, null, false);
			for(GenericValue custReq : custRequestItems){
				statusCtx.clear();
				statusCtx.put("statusId", statusId);
				statusCtx.put("custRequestId", custRequestId);
				statusCtx.put("custRequestItemSeqId", custReq.getString("custRequestItemSeqId"));
				statusCtx.put("userLogin", userLogin);
				statusCtx.put("description", "");
				resultCtx = dispatcher.runSync("setCustRequestItemStatus", statusCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("RequestItem set status failed for Request: " + custRequestId+" : "+custReq.getString("custRequestItemSeqId"), module);
					return resultCtx;
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static String processUpdateGRNWithPO(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String grnId = (String) request.getParameter("grnId");
	    String orderId = (String) request.getParameter("orderId");
	    String shipmentId = (String) request.getParameter("shipmentId");
	    List productList = FastList.newInstance();
	    List<String> shipmentProductList = FastList.newInstance();
	    String supplierId="";
		/*List<Map> prodQtyList = FastList.newInstance();*/
	    //update Shipment
	    boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
		GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
		if(UtilValidate.isNotEmpty(shipment)){
			shipment.set("primaryOrderId", orderId);
			shipment.store();
		}
		Debug.log("orderId=="+orderId+"==shipmentId=="+shipmentId);
		if(UtilValidate.isEmpty(shipmentId) && UtilValidate.isEmpty(orderId)){
			Debug.logError("PurchaseOrderId or shipmentId can not be empty !", module);
			request.setAttribute("_ERROR_MESSAGE_", "PurchaseOrderId  or shipmentId can not be empty !"+shipmentId);	
			TransactionUtil.rollback();
	  		return "error";
		}
	    //get OrderItem to update ShipmentReceipt
        List<GenericValue> orderItems = FastList.newInstance();
		if(UtilValidate.isNotEmpty(orderId)){
			orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			productList = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
			if(UtilValidate.isEmpty(orderItems)){
				Debug.logError("No Items for the selected PO number : "+orderId, module);
				request.setAttribute("_ERROR_MESSAGE_", "No Items for the selected PO number : "+orderId);	
				TransactionUtil.rollback();
		  		return "error";
			}
		}
		List<GenericValue> shipmentReceipts = FastList.newInstance();
		if(UtilValidate.isNotEmpty(shipmentId)){
			shipmentReceipts = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false);
			shipmentProductList = EntityUtil.getFieldListFromEntityList(shipmentReceipts, "productId", true);
			if(UtilValidate.isEmpty(shipmentReceipts)){
				Debug.logError("No Receipts for the Shipment : "+shipmentId, module);
				request.setAttribute("_ERROR_MESSAGE_", "No Receipts for the Shipment  : "+shipmentId);	
				TransactionUtil.rollback();
		  		return "error";
			}
		}
		//comparing PO items against shipment items if any mismatch throw an error
		if(shipmentProductList.size()==productList.size()){
			for(String shipmentProductId:shipmentProductList){
				if(!productList.contains(shipmentProductId)){
					Debug.logError("GRN shipment product Id : "+shipmentProductId+" not exists in seleced PO number:"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "GRN shipment product Id : "+shipmentProductId+" not exists in seleced PO number:"+orderId);	
					TransactionUtil.rollback();
			  		return "error";
				}
			}
		}else{
			Debug.logError("GRN shipment Items :"+shipmentProductList.size()+" not matched with the seleced PO Items:"+productList.size(), module);
			request.setAttribute("_ERROR_MESSAGE_", "GRN shipment Items :"+shipmentProductList.size()+" not matched with the seleced PO Items:"+productList.size());	
			TransactionUtil.rollback();
	  		return "error";
		}
		
		for (GenericValue orderItem : orderItems) {
			//if(UtilValidate.isNotEmpty(orderItem)){
				String productId=orderItem.getString("productId");
				BigDecimal unitCost=orderItem.getBigDecimal("unitPrice");
				String orderItemSeqId=orderItem.getString("orderItemSeqId");
				//update shipmentReceiptItem
				List<GenericValue> filterShipmentReceiptList = EntityUtil.filterByCondition(shipmentReceipts, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				GenericValue shipmentReceipt = EntityUtil.getFirst(filterShipmentReceiptList);
				
				if(UtilValidate.isEmpty(filterShipmentReceiptList)){
					Debug.logError("PO ProductId "+ productId +" not found in given MRN  ! ", module);
					request.setAttribute("_ERROR_MESSAGE_", "PO ProductId "+ productId +" not found in given MRN  !" );	
					TransactionUtil.rollback();
			  		return "error";
				}
				
				if(UtilValidate.isNotEmpty(shipmentReceipt)){
				shipmentReceipt.set("orderId", orderId);
				shipmentReceipt.set("orderItemSeqId", orderItemSeqId);
				shipmentReceipt.store();
				//update inventory
				GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", shipmentReceipt.getString("inventoryItemId")), false);
				if(UtilValidate.isNotEmpty(inventoryItem)){
					inventoryItem.set("unitCost", unitCost);
					inventoryItem.store();
				} 
				}
		     }
		}catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  	}finally {
	  		try {
	  			TransactionUtil.commit(beganTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  		}
	  	}
	  	request.setAttribute("_EVENT_MESSAGE_", "PO Number :"+orderId +" Linked With GRN Shipment Number"+shipmentId+" Successfully !");
	  	return "success";
	}
	
	public static Map<String, Object> cancelPOStatus(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
        
		String statusId = (String) context.get("statusId");
		String orderId = (String) context.get("orderId");
		String changeReason = (String) context.get("changeReason");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			
			if(UtilValidate.isEmpty(orderHeader)){
				Debug.logError("Order doesn't exists with Id : "+orderId , module);
  	 			return ServiceUtil.returnError("Order doesn't exists with Id : "+orderId);
			}
			
			Map statusCtx = FastMap.newInstance();
			statusCtx.put("statusId", statusId);
			statusCtx.put("orderId", orderId);
			statusCtx.put("userLogin", userLogin);
			Map resultCtx = OrderServices.setOrderStatus(ctx, statusCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Order set status failed for orderId: " + orderId, module);
				return resultCtx;
			}
			String oldStatusId = (String)resultCtx.get("oldStatusId");
			result.put("oldStatusId", oldStatusId);
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("quoteId"), null, null, false);
			
			List<String> quoteIds = EntityUtil.getFieldListFromEntityList(orderItems, "quoteId", true);
			
			if(UtilValidate.isNotEmpty(quoteIds)){

				String quoteId = (String)quoteIds.get(0);
				GenericValue quote = delegator.findOne("Quote", UtilMisc.toMap("quoteId", quoteId), false);
				List<GenericValue> quoteItems = delegator.findList("QuoteItem", EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId), null, null, null, false);
				
				boolean quoteStatusChange = Boolean.FALSE; 
				for(GenericValue quoteItem : quoteItems){
					String itemStatusId = quoteItem.getString("statusId");
					
					if(itemStatusId.equals("QTITM_ORDERED")){
						Map inputMap = FastMap.newInstance();
			        	inputMap.put("userLogin", userLogin);
			        	inputMap.put("quoteId", quoteItem.getString("quoteId"));
			        	inputMap.put("quoteItemSeqId", quoteItem.getString("quoteItemSeqId"));
			        	inputMap.put("statusId", "QTITM_QUALIFIED");
			        	
			        	Map statusResult = dispatcher.runSync("setQuoteAndItemStatus", inputMap);
			        	if(ServiceUtil.isError(statusResult)){
			        		Debug.logError("Error updating QuoteStatus", module);
			  	  			return ServiceUtil.returnError("Error updating QuoteStatus");
			        	}
			        	quoteStatusChange = Boolean.TRUE;
					}
					
				}
				if(quoteStatusChange){
					Map inputMap = FastMap.newInstance();
		        	inputMap.put("userLogin", userLogin);
		        	inputMap.put("quoteId", quoteId);
		        	inputMap.put("statusId", "QUO_ACCEPTED");
		        	
		        	Map statusResult = dispatcher.runSync("setQuoteAndItemStatus", inputMap);
		        	if(ServiceUtil.isError(statusResult)){
		        		Debug.logError("Error updating QuoteStatus", module);
		  	  			return ServiceUtil.returnError("Error updating QuoteStatus");
		        	}
				}
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> sendReceiptQtyForQC(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String statusId = (String) context.get("statusIdTo");
		String receiptId = (String) context.get("receiptId");
		String partyId = (String) context.get("partyId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			
			GenericValue shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
			shipmentReceipt.put("statusId", statusId);
			shipmentReceipt.store();
			
			//creating shipmentReceiptRole here
			if(UtilValidate.isNotEmpty(partyId)){
				GenericValue shipmentReceiptRole = delegator.makeValue("ShipmentReceiptRole");
				shipmentReceiptRole.put("receiptId", receiptId);
				shipmentReceiptRole.put("partyId", partyId);
				shipmentReceiptRole.put("roleTypeId", "DIVISION");
				delegator.createOrStore(shipmentReceiptRole);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result = ServiceUtil.returnSuccess("GRN no: "+receiptId+" Send For Quality Check ");
		return result;
	}
	
	public static Map<String, Object> suspendPO(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String) context.get("orderId");
		String statusUserLogin = (String) context.get("statusUserLogin");
		String changeReason = (String) context.get("changeReason");
		Timestamp statusDatetime = (Timestamp) context.get("statusDatetime");
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			if(UtilValidate.isNotEmpty(orderId)){
				GenericValue orderStatus = delegator.makeValue("OrderStatus");
				orderStatus.set("orderId", orderId);
				orderStatus.set("statusUserLogin", statusUserLogin);
				orderStatus.set("changeReason", changeReason);
				orderStatus.set("statusDatetime", statusDatetime);
				orderStatus.set("statusId", "ORDER_SUSPENDED");
				delegator.createSetNextSeqId(orderStatus);
				
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if(UtilValidate.isNotEmpty(orderHeader)){
					orderHeader.put("statusId", "ORDER_SUSPENDED");
					orderHeader.store();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception	
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result = ServiceUtil.returnSuccess("Order"+orderId+" Suspended sucessfully");
		return result;
	}
}
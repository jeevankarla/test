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
import java.util.Set;
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

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.base.conversion.JSONConverters.JSONToList;
import org.ofbiz.entity.util.EntityFindOptions;

public class MaterialPurchaseServices {

	public static final String module = MaterialPurchaseServices.class.getName();
	
	public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
	public static final BigDecimal ONE_BASE = BigDecimal.ONE;
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
	public static int purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
	public static int purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
	
	public static int purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
	
	public static String processReceiptItems(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String receiptDateStr = (String) request.getParameter("receiptDate");
	    String orderId = (String) request.getParameter("orderId");
	    String vehicleId = (String) request.getParameter("vehicleId");
	    String lrNumber = (String) request.getParameter("lrNumber");
	    String destination = (String) request.getParameter("destination");
	    
	    
	    
	    String carrierName = (String) request.getParameter("carrierName");
	    if(UtilValidate.isEmpty(carrierName)){
	    	carrierName = "_NA_";
	    }
	    String supplierInvoiceId = (String) request.getParameter("suppInvoiceId");
	    String supplierInvoiceDateStr = (String) request.getParameter("suppInvoiceDate");
	    String withoutPO = (String) request.getParameter("withoutPO");
	    //GRN on PO then override this supplier with PO supplier
	    String supplierId = (String) request.getParameter("supplierId");
	    String deliveryChallanDateStr = (String) request.getParameter("deliveryChallanDate");
	    String lrDateStr = (String) request.getParameter("lrDate");
	    String deliveryChallanNo = (String) request.getParameter("deliveryChallanNo");
	    String remarks = (String) request.getParameter("remarks");
	    String hideQCflow = (String) request.getParameter("hideQCflow");
	    String freightCharges = (String) request.getParameter("freightCharges");
	    String allowedGraterthanTheOrdered = (String) request.getParameter("allowedGraterthanTheOrdered");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		String shipmentId ="";
		String receiptId="";
		String purposeTypeId="";
		GenericValue shipmentReceipt=null;
		String smsContent="";
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
		Timestamp estimatedDate = UtilDateTime.addDaysToTimestamp(receiptDate,6);
		String estimatedDateStr=UtilDateTime.toDateString(estimatedDate,"dd-MM-yyyy");
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
	  			SimpleDateFormat dateSdf = new SimpleDateFormat("dd MMMMM, yyyy");    
	  			supplierInvoiceDate = new java.sql.Timestamp(dateSdf.parse(supplierInvoiceDateStr).getTime());
	  			
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + supplierInvoiceDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + supplierInvoiceDateStr, module);
		  	}
	  	}
	  	Timestamp lrDateTimeStamp = null;
	  	if(UtilValidate.isNotEmpty(lrDateStr)){
	  		try {
	  			SimpleDateFormat dateSdf = new SimpleDateFormat("dd MMMMM, yyyy");    
	  			lrDateTimeStamp = new java.sql.Timestamp(dateSdf.parse(lrDateStr).getTime());
	  			
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + lrDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + lrDateStr, module);
		  	}
	  	}
	  	
	  	Timestamp deliveryChallanDate=null;
	  	if(UtilValidate.isNotEmpty(deliveryChallanDateStr)){
	  		try {
	  			SimpleDateFormat dateSdf = new SimpleDateFormat("dd MMMMM, yyyy");    
	  			deliveryChallanDate = new java.sql.Timestamp(dateSdf.parse(deliveryChallanDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + deliveryChallanDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + deliveryChallanDateStr, module);
		  	}
	  	}else{
		  	 deliveryChallanDate=UtilDateTime.nowTimestamp();
	  	}
	  	BigDecimal poValue = BigDecimal.ZERO;
	  	boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			List conditionList = FastList.newInstance();
			List<GenericValue> extPOItems = FastList.newInstance();
			List<GenericValue> extReciptItems = FastList.newInstance();
			
			boolean directPO = Boolean.TRUE;
			String extPOId = "";
			List productList = FastList.newInstance();
			
			if((UtilDateTime.getIntervalInDays(UtilDateTime.getDayStart(receiptDate), UtilDateTime.getDayStart(nowTimeStamp))) != 0){
	    		Debug.logError("Check local system date", module);
	    		request.setAttribute("_ERROR_MESSAGE_", "Check local system date");	
				TransactionUtil.rollback();
		  		return "error";
			}
			String originFacilityId ="";
			GenericValue orderHeader = null;
			if(UtilValidate.isNotEmpty(orderId)){
				orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				
				String statusId = orderHeader.getString("statusId");
				String orderTypeId = orderHeader.getString("orderTypeId");
				purposeTypeId = orderHeader.getString("purposeTypeId");
				if(UtilValidate.isNotEmpty(orderHeader.getString("originFacilityId")))
				  {
					originFacilityId = orderHeader.getString("originFacilityId");
				  }
				if(statusId.equals("ORDER_CANCELLED")){
					Debug.logError("Cannot create GRN for cancelled orders : "+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Cannot create GRN for cancelled orders : "+orderId);	
					TransactionUtil.rollback();
			  		return "error";
				}
				poValue = orderHeader.getBigDecimal("grandTotal");
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
			List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			List changeExprList = FastList.newInstance();
			changeExprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			changeExprList.add(EntityCondition.makeCondition("effectiveDatetime", EntityOperator.LESS_THAN_EQUAL_TO, receiptDate));
			EntityCondition condExpr1 = EntityCondition.makeCondition(changeExprList, EntityOperator.AND);
			List<GenericValue> orderItemChanges = delegator.findList("OrderItemChange", condExpr1, null, UtilMisc.toList("-effectiveDatetime"), null, false);
			
			Timestamp effectiveDatetime = null;
			if(UtilValidate.isNotEmpty(orderItemChanges)){
				effectiveDatetime = (EntityUtil.getFirst(orderItemChanges)).getTimestamp("effectiveDatetime");
				orderItemChanges = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("effectiveDatetime", EntityOperator.EQUALS, effectiveDatetime));
			}

			Map orderItemSeq = FastMap.newInstance();
			for(GenericValue orderItemsValues : orderItems){
				String productId = orderItemsValues.getString("productId");
				String orderItemSeqId = orderItemsValues.getString("orderItemSeqId");
				orderItemSeq.put(productId, orderItemSeqId);
			}

			List orderAdjustmentTypes = EntityUtil.getFieldListFromEntityList(orderAdjustments, "orderAdjustmentTypeId", true);
			
			productList = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
			GenericValue newEntity = delegator.makeValue("Shipment");
	        newEntity.set("estimatedShipDate", lrDateTimeStamp);
	        //Debug.log("purposeTypeId========@@@========"+purposeTypeId);
	        if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("BRANCH_PURCHASE")){
		        newEntity.set("shipmentTypeId", "BRANCH_SHIPMENT");
		        newEntity.set("shipmentPurposeTypeId", "YARN_SHIPMENT");
	        }else if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("DC_PURCHASE")){
	        	newEntity.set("shipmentTypeId", "BRANCH_SHIPMENT");
	        	newEntity.set("shipmentPurposeTypeId", "DC_SHIPMENT");
	        }
	        else if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("DEPOT_PURCHASE")){
	        	newEntity.set("shipmentTypeId", "DEPOT_SHIPMENT");
	        	newEntity.set("shipmentPurposeTypeId", "DEPOT_YARN_SHIPMENT");
	        }
	        else if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("DC_DEPOT_PURCHASE")){
	        	newEntity.set("shipmentTypeId", "DEPOT_SHIPMENT");
	        	newEntity.set("shipmentPurposeTypeId", "DC_DEPOT_SHIPMENT");
	        }
	        newEntity.set("statusId", "DISPATCHED");
	        newEntity.put("vehicleId",vehicleId);
	        newEntity.put("lrNumber",lrNumber);
	        newEntity.put("destination",destination);
	        newEntity.put("carrierName",carrierName);
	        newEntity.put("partyIdFrom",supplierId);
	        newEntity.put("supplierInvoiceId",supplierInvoiceId);
	        newEntity.put("supplierInvoiceDate",supplierInvoiceDate);
	        newEntity.put("estimatedReadyDate",lrDateTimeStamp);
	        newEntity.put("deliveryChallanNumber",deliveryChallanNo);
	        newEntity.put("description",remarks);
	        newEntity.put("deliveryChallanDate",deliveryChallanDate);
	        newEntity.put("primaryOrderId",orderId);
	        if(UtilValidate.isNotEmpty(freightCharges))
            newEntity.set("estimatedShipCost", new BigDecimal(freightCharges));
	        newEntity.set("createdDate", nowTimeStamp);
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            delegator.createSetNextSeqId(newEntity);            
            shipmentId = (String) newEntity.get("shipmentId");
	       
			/*List<Map> prodQtyList = FastList.newInstance();*/
			
            BigDecimal landingCharges = BigDecimal.ZERO;
			for (int i = 0; i < rowCount; i++) {
				
				String productId = "";
		        String quantityStr = "";
		        String deliveryChallanQtyStr = "";
		        String oldRecvdQtyStr = "";
		        String orderItemSeqId = "";
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal deliveryChallanQty = BigDecimal.ZERO;
				BigDecimal oldRecvdQty = BigDecimal.ZERO;
				Map productQtyMap = FastMap.newInstance();
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	    if (paramMap.containsKey("productId" + thisSuffix)) {
				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
					return "error";			  
				}
				if (productId.equals("") || UtilValidate.isEmpty(productId)) {
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
				if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
					orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
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
						GenericValue ordChangesApo = null;
						BigDecimal orderChangeQty=BigDecimal.ZERO;
						BigDecimal orderQty=BigDecimal.ZERO;
						String orderSeqNo="";
						List<GenericValue> ordItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
						if(UtilValidate.isNotEmpty(orderItemSeqId)){
							orderSeqNo=(String)orderItemSeq.get(productId);
							List<GenericValue> ordersequenceChangeList = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				 			if(UtilValidate.isNotEmpty(ordersequenceChangeList)){
				 				orderChangeQty = (EntityUtil.getFirst(ordersequenceChangeList)).getBigDecimal("quantity");

				 			}	
						}
						if(UtilValidate.isNotEmpty(ordItems)){
							checkOrderItem = EntityUtil.getFirst(ordItems);

						}
						
						if(UtilValidate.isNotEmpty(checkOrderItem)){
							     orderQty = checkOrderItem.getBigDecimal("quantity");
						    if(orderChangeQty.compareTo(BigDecimal.ZERO)>0 ){
								 orderQty = orderChangeQty;
						    }
							BigDecimal checkQty = (orderQty.multiply(new BigDecimal(1.1))).setScale(0, BigDecimal.ROUND_CEILING);
							BigDecimal maxQty=oldRecvdQty.add(quantity);
							Debug.log("=orderQty=="+orderQty+"==checkQty="+checkQty+"==maxQty=="+maxQty+"==quantity="+quantity);
							//if(quantity.compareTo(checkQty)>0){
							if(UtilValidate.isEmpty(allowedGraterthanTheOrdered) || (UtilValidate.isNotEmpty(allowedGraterthanTheOrdered) && "Y".equals(allowedGraterthanTheOrdered))){								
								if(maxQty.compareTo(checkQty)>0){	
									Debug.logError("Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId, module);
									request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId);	
									TransactionUtil.rollback();
							  		return "error";
								}
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
	        }
          		String termTypeId = "";
				BigDecimal amount = BigDecimal.ZERO;
				String amountStr = "";
				if (paramMap.containsKey("termTypeId" + thisSuffix)) {
					termTypeId = (String) paramMap.get("termTypeId" + thisSuffix);
				}
			  
				if (paramMap.containsKey("amount" + thisSuffix)) {
					amountStr = (String) paramMap.get("amount" + thisSuffix);
				}
					  
				if(UtilValidate.isNotEmpty(amountStr)){
					amount = new BigDecimal(amountStr);
				}
				
				if(UtilValidate.isNotEmpty(termTypeId) && amount.compareTo(BigDecimal.ZERO)>0){
					if(!orderAdjustmentTypes.contains(termTypeId)){
						landingCharges = landingCharges.add(amount);
					}
					GenericValue shipmentAttribute = delegator.makeValue("ShipmentAttribute");
					shipmentAttribute.set("shipmentId", shipmentId);
					shipmentAttribute.set("attrName", termTypeId);
					shipmentAttribute.set("attrValue", amountStr);
					delegator.createOrStore(shipmentAttribute);
				}
				//Debug.log("quantityStr======="+quantityStr);
				GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
				String	desc=product.getString("description");
				//Debug.log("desc============"+desc);
				 smsContent = smsContent + quantityStr + " KGs of " + desc + ",";
           if (paramMap.containsKey("productId" + thisSuffix)) {
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
		        //List<GenericValue> productsFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
				List<GenericValue> filteredOrderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				GenericValue ordItm = null;
				if(UtilValidate.isNotEmpty(filteredOrderItem)){
					ordItm = EntityUtil.getFirst(filteredOrderItem);
				    orderItemSeqId = ordItm.getString("orderItemSeqId");
					List<GenericValue> itemChanges = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
					if(UtilValidate.isNotEmpty(itemChanges)){
						ordItm = EntityUtil.getFirst(itemChanges);
					}
				}
				
				/*List<GenericValue> filterProdFacility = EntityUtil.filterByCondition(productsFacility, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				List prodFacilityIds = EntityUtil.getFieldListFromEntityList(filterProdFacility, "facilityId", true);
				List facilityConditionList = FastList.newInstance();
				facilityConditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "STORE"));
				facilityConditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
				facilityConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,prodFacilityIds));
				EntityCondition facilityCondition = EntityCondition.makeCondition(facilityConditionList, EntityOperator.AND);
				List<GenericValue> facilities = delegator.findList("Facility", facilityCondition, null, null, null, false);
				GenericValue facilityProd = EntityUtil.getFirst(facilities);*/
				//Product should mapped to any one of facility
				/* if (UtilValidate.isEmpty(facilityProd)) {
			        	Debug.logError("Problem creating shipment Item for ProductId :"+productId+" Not Mapped To Store Facility !", module);
						request.setAttribute("_ERROR_MESSAGE_", "Problem creating shipment Item for ProductId :"+productId+" Not Mapped To Store Facility!");	
						TransactionUtil.rollback();
				  		return "error";
			        }*/
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
				if(UtilValidate.isNotEmpty(originFacilityId)){

				inventoryReceiptCtx.put("facilityId", originFacilityId);
				}else{
					inventoryReceiptCtx.put("facilityId", "BRANCH1");
				}
				//facilityProd.getString("facilityId"));
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
				
				receiptId = (String)receiveInventoryResult.get("receiptId");
				shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
				if(UtilValidate.isNotEmpty(shipmentReceipt)){
					shipmentReceipt.set("shipmentId", shipmentId);
					shipmentReceipt.set("shipmentItemSeqId", shipmentItemSeqId);
					shipmentReceipt.store();
				}
				//storing shipment receipt status Here 
				if(UtilValidate.isNotEmpty(receiptId)){
					GenericValue shipmentReceiptStatus = delegator.makeValue("ShipmentReceiptStatus");
					shipmentReceiptStatus.set("receiptId", receiptId);
					shipmentReceiptStatus.set("statusId", (String) shipmentReceipt.get("statusId"));
					shipmentReceiptStatus.set("changedByUserLogin", userLogin.getString("userLoginId"));
					shipmentReceiptStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
					delegator.createSetNextSeqId(shipmentReceiptStatus);
				}
           }
              
           }
			
			if(UtilValidate.isNotEmpty(orderId) && landingCharges.compareTo(BigDecimal.ZERO)>0){
				List condExpr = FastList.newInstance();
				condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				condExpr.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				EntityCondition cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
				List<GenericValue> shipReceipts = delegator.findList("ShipmentReceipt", cond, null, null, null, false);
				
				// recalculating landing cost for handling transportation/frieght at actuals and installation charges
				for(GenericValue shipReceipt : shipReceipts){
					condExpr.clear();
					condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipReceipt.getString("orderId")));
					condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, shipReceipt.getString("orderItemSeqId")));
					EntityCondition condition = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
					
					List<GenericValue> orderItem = EntityUtil.filterByCondition(orderItems, condition);
					
					condExpr.clear();
					condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipReceipt.getString("orderId")));
					condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, shipReceipt.getString("orderItemSeqId")));
					EntityCondition condition1 = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
					
					List<GenericValue> ordItmChange = EntityUtil.filterByCondition(orderItemChanges, condition);

					
					if(UtilValidate.isNotEmpty(orderItem)){
						BigDecimal unitListPrice = (EntityUtil.getFirst(orderItem)).getBigDecimal("unitListPrice");
						if(UtilValidate.isNotEmpty(ordItmChange)){
							unitListPrice = (EntityUtil.getFirst(ordItmChange)).getBigDecimal("unitListPrice");
						}
						BigDecimal qty = shipReceipt.getBigDecimal("quantityAccepted");
						
						BigDecimal itemValue = unitListPrice.multiply(qty);
						BigDecimal calcAmt = (itemValue.multiply(landingCharges)).divide(poValue, purchaseTaxFinalDecimals, purchaseTaxRounding);
						BigDecimal adjUnitAmt = calcAmt.divide(qty, purchaseTaxFinalDecimals, purchaseTaxRounding);
						BigDecimal updatedLandingCost = unitListPrice.add(adjUnitAmt);
						String inventoryItemId = shipReceipt.getString("inventoryItemId");
						GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
						inventoryItem.set("unitCost", updatedLandingCost);
						inventoryItem.store();
					}
					
				}
			}
			GenericValue orderHeaderDetails = null;
			GenericValue orderHeaderDetailsSale = null;
			String orderStatusId = null;
			orderHeaderDetails = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
	        String statusId = orderHeaderDetails.getString("statusId");
	        if(statusId.equals("ORDER_COMPLETED")){
				List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("toOrderId"), null, null, false);
				String toOrderId ="";
				if(UtilValidate.isNotEmpty(orderAssoc)){
					toOrderId = (EntityUtil.getFirst(orderAssoc)).getString("toOrderId");
					orderHeaderDetailsSale = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", toOrderId), false);
					orderStatusId = orderHeaderDetailsSale.getString("statusId");
				}
				if(!orderStatusId.equals("ORDER_COMPLETED")){	
	        	Map statusCtx = FastMap.newInstance();
	 			statusCtx.put("statusId", statusId);
	 			statusCtx.put("orderId", toOrderId);
	 			statusCtx.put("userLogin", userLogin);
	 			Map resultCtx = OrderServices.setOrderStatus(dctx, statusCtx);
	 			if (ServiceUtil.isError(resultCtx)) {
	 				Debug.logError("Order set status failed for orderId: " + orderId, module);
	 				return "error";
	 			} 
	 			
				}
	 			
	        }
	if(UtilValidate.isNotEmpty(hideQCflow)&&("Y".equals(hideQCflow))){
				
			Map inputMap = FastMap.newInstance();
			inputMap.put("statusIdTo","SR_QUALITYCHECK");
			inputMap.put("receiptId",receiptId);
			inputMap.put("partyId","DEPOT");
			inputMap.put("userLogin",userLogin);
			Map resultMap = dispatcher.runSync("sendReceiptQtyForQC",inputMap);
			
			if (ServiceUtil.isError(resultMap)) {
				Debug.logWarning("There was an error while sending to QC: " + ServiceUtil.getErrorMessage(resultMap), module);
				request.setAttribute("_ERROR_MESSAGE_", "There was an error sending to QC: " + ServiceUtil.getErrorMessage(resultMap));	
				TransactionUtil.rollback();
		  		return "error";
            }
			inputMap.clear();
			resultMap.clear();
			
			inputMap.put("statusIdTo","SR_ACCEPTED");
			inputMap.put("receiptId",receiptId);
			inputMap.put("shipmentId",shipmentId);
			inputMap.put("shipmentItemSeqId",shipmentReceipt.get("shipmentItemSeqId") );
			inputMap.put("quantityAccepted",shipmentReceipt.get("quantityAccepted"));
			inputMap.put("userLogin",userLogin);
			resultMap = dispatcher.runSync("acceptReceiptQtyByQC", inputMap);
			if (ServiceUtil.isError(resultMap)) {
				Debug.logWarning("There was an error while Accepting in QC: " + ServiceUtil.getErrorMessage(resultMap), module);
				request.setAttribute("_ERROR_MESSAGE_", "There was an error Accepting in QC: " + ServiceUtil.getErrorMessage(resultMap));	
				TransactionUtil.rollback();
		  		return "error";
            }
		}
	}
		catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
		     	request.setAttribute("_ERROR_MESSAGE_", "Could not rollback transaction: " );
				return "error";
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  		request.setAttribute("_ERROR_MESSAGE_", "An entity engine error occurred while fetching data: " );
			return "error";

	  	}
  	  	catch (GenericServiceException e) {
  	  		try {
  			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
  	  		} catch (GenericEntityException e2) {
  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  		      request.setAttribute("_ERROR_MESSAGE_", "Could not rollback transaction: " );
		      return "error";
  	  		}
  	  		Debug.logError("An entity engine error occurred while calling services", module);
  	     	request.setAttribute("_ERROR_MESSAGE_", "An entity engine error occurred while calling services" );
		    return "error";
  	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beganTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			request.setAttribute("_ERROR_MESSAGE_", "Could not commit transaction for entity engine error occurred while fetching data" );
				return "error";
	  		}
	  	}
		try {
        	List condExpr = FastList.newInstance();
			condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condExpr.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
			EntityCondition cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
			List<GenericValue> orderRoleList = delegator.findList("OrderRole", cond, null, null, null, false);
		 
			GenericValue orderRoleFirstList = EntityUtil.getFirst(orderRoleList);
			String partyIdTo = (String) orderRoleFirstList.getString("partyId");
			GenericValue ShipmentList = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);  
			
			
			 if (UtilValidate.isNotEmpty(ShipmentList)) {
	 			  
				 ShipmentList.set("shipmentId",shipmentId);
				 ShipmentList.set("partyIdTo",partyIdTo);
				 
 		        try {
 		        	ShipmentList.store();
 		        } catch (GenericEntityException e) {
		  			Debug.logError(e, "Could not Update partyIdTo in Shipment entity", module);
		  			request.setAttribute("_ERROR_MESSAGE_", "Could not get date from OrderAttribute" );
					return "error";
 		        }
			 }
        } catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not get date from OrderAttribute", module);
	  			request.setAttribute("_ERROR_MESSAGE_", "Could not get date from OrderAttribute" );
				return "error";
    }

		String rlatedId="";
		String customerId="";
		List condiList = FastList.newInstance();
		try{
			condiList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condiList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
			List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition(condiList, EntityOperator.AND), null, null, null, false);
	
			if(UtilValidate.isNotEmpty(orderAssoc)){
				rlatedId = (EntityUtil.getFirst(orderAssoc)).getString("toOrderId");
			
			}
		}catch (GenericEntityException e) {
			Debug.logError(e, "Could not get date from OrderAssoc", module);
			//request.setAttribute("_ERROR_MESSAGE_", "Could not get date from OrderAssoc" );
			//return "error";
		}
		//Debug.log("orderID=================="+orderId+"==rlatedId=================="+rlatedId);

		if(UtilValidate.isNotEmpty(rlatedId)){
			try{
			condiList.clear();
			condiList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, rlatedId));
			condiList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
			EntityCondition condi = EntityCondition.makeCondition(condiList, EntityOperator.AND);
			List<GenericValue> orderRoleForCustomer = delegator.findList("OrderRole", condi, null, null, null, false);
			
			if(UtilValidate.isEmpty(orderRoleForCustomer)){
				Debug.logError("No Vendor for the order : "+rlatedId, module);
				request.setAttribute("_ERROR_MESSAGE_", "No Vendor for the order : "+rlatedId);	
				TransactionUtil.rollback();
		  		return "error";
			}
				
			customerId = (EntityUtil.getFirst(orderRoleForCustomer)).getString("partyId");
			}catch (GenericEntityException e) {
				Debug.logError(e, "Could not get date from OrderRole", module);
				//request.setAttribute("_ERROR_MESSAGE_", "Could not get date from OrderRole" );
				//return "error";
			}
		}

		String shipmentMessageToWeaver = UtilProperties.getMessage("ProductUiLabels", "ShipmentMessageToWeaver", locale);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("orderId", rlatedId);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("material", smsContent);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("transporter", carrierName);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("lrNo", lrNumber);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("lrDate", deliveryChallanDateStr);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("expectedDeliveryDate", estimatedDateStr);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("estimatedReadyDate", lrDateStr);
		if(UtilValidate.isNotEmpty(customerId)){
			String customerName=org.ofbiz.party.party.PartyHelper.getPartyName(delegator,supplierId, false);
			Map<String, Object> getTelParams = FastMap.newInstance();
			if(UtilValidate.isEmpty(customerName)){
				customerName=supplierId;
			}
        	getTelParams.put("partyId", customerId);
            getTelParams.put("userLogin", userLogin); 
            try{
            	Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	                return "error";
	            }
	            
	            String contactNumberTo = (String) serviceResult.get("contactNumber");            
	            String countryCode = (String) serviceResult.get("countryCode");
	           /* if(UtilValidate.isEmpty(contactNumberTo)){
	            	contactNumberTo = "7330776928";
	            }
	            if(UtilValidate.isEmpty(carrierName)){
	            	carrierName = "_";
	            }
	            contactNumberTo = "7330776928";*/
	            Debug.log("contactNumberTo = "+contactNumberTo);
	            if(UtilValidate.isNotEmpty(contactNumberTo)){
	            	 if(UtilValidate.isNotEmpty(countryCode)){
	            		 contactNumberTo = countryCode + contactNumberTo;
	            	 }
	            	 Debug.log("contactNumberTo ===== "+contactNumberTo);
	            	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                 sendSmsParams.put("contactNumberTo", contactNumberTo);
	                 sendSmsParams.put("text", shipmentMessageToWeaver); 
	                 //Debug.log("sendSmsParams====================="+sendSmsParams);
	                 serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	                 if (ServiceUtil.isError(serviceResult)) {
	                     Debug.logError("unable to send Sms", module);
	     				//request.setAttribute("_ERROR_MESSAGE_", "unable to send Sms : "+rlatedId);	
	                     //return "error";
	                 }
	            	
	            	/*Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                sendSmsParams.put("contactNumberTo", contactNumberTo);
	                sendSmsParams.put("text", "Order placed on M/s. "+ suppPartyId +" against your Indent No. "+orderId);            
	                serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);       
	                if (ServiceUtil.isError(serviceResult)) {
	                    Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                    return serviceResult;
	                }*/
	            }
            }catch(GenericServiceException e1){
	         	Debug.log("Problem in sending sms to user agency");
			}
			
		}
		
		
		String Scheam = "";
 		try{
     		List<GenericValue> orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, rlatedId), null, null, null, false);
			
			List<GenericValue> scheamList = EntityUtil.filterByCondition(orderAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
			
			if(UtilValidate.isNotEmpty(scheamList)){
				GenericValue orderScheme = EntityUtil.getFirst(scheamList);
				Scheam = (String) orderScheme.get("attrValue");
			}
 		 }catch(Exception e){
			 Debug.logError("problem While Fetching OrderAttribute : "+e, module);
		 } 
		
 		
 		if(UtilValidate.isNotEmpty(Scheam) && Scheam != "General")
 		{
	        GenericValue Shipment = null;
	
	  		try{
	  			Shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId",shipmentId),false);
	  			
	  			
	  			GenericValue ShipmentReimbursement = delegator.makeValue("ShipmentReimbursement");
				
					BigDecimal estimatedShipCost=Shipment.getBigDecimal("estimatedShipCost");
					
					Timestamp receiptDate1=Shipment.getTimestamp("estimatedShipDate");
					if(UtilValidate.isEmpty(receiptDate1))
						receiptDate1=UtilDateTime.nowTimestamp();
	  				if(UtilValidate.isNotEmpty(estimatedShipCost)){
		  				ShipmentReimbursement.set("shipmentId", shipmentId);
		  				ShipmentReimbursement.set("receiptAmount", estimatedShipCost);
		  				ShipmentReimbursement.set("receiptDate", receiptDate1);
						delegator.createSetNextSeqId(ShipmentReimbursement);
		  				
	  				}
	  			
	  		}catch(GenericEntityException e){
				Debug.logError(e, "Failed to get Shipment ", module);
			}
		
 		}
 		try{
 			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("shipmentId", shipmentId));
 			if (ServiceUtil.isError(serviceResult)) {
 				request.setAttribute("_ERROR_MESSAGE_", "Error While Updateing Indent Summary Details");
 				return "error";
            }
 			Map serviceResult2  = dispatcher.runSync("periodPopulateShipmentTotals", UtilMisc.toMap("shipmentId", shipmentId));
 			if (ServiceUtil.isError(serviceResult2)) {
 				request.setAttribute("_ERROR_MESSAGE_", "Error While Updateing Shipment GrandTotal");
 				return "error";
            }
  		}catch(GenericServiceException e){
			Debug.logError(e, "Exception cought while updating Indent Summary Details and Shipment Grand Total", module);
		}
 		
 		
		request.setAttribute("_EVENT_MESSAGE_", "Successfully made shipment with ID:"+shipmentId);
		return "success";
	}
	
	
	
	public static String processDepotReceiptItems(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String receiptDateStr = (String) request.getParameter("receiptDate");
	    String orderId = (String) request.getParameter("orderId");
	    String vehicleId = (String) request.getParameter("vehicleId");
	    String lrNumber = (String) request.getParameter("lrNumber");
	    String carrierName = (String) request.getParameter("carrierName");
	    if(UtilValidate.isEmpty(carrierName)){
	    	carrierName = "_NA_";
	    }
	    String supplierInvoiceId = (String) request.getParameter("suppInvoiceId");
	    String supplierInvoiceDateStr = (String) request.getParameter("suppInvoiceDate");
	    String withoutPO = (String) request.getParameter("withoutPO");
	    //GRN on PO then override this supplier with PO supplier
	    String supplierId = (String) request.getParameter("supplierId");
	    String deliveryChallanDateStr = (String) request.getParameter("deliveryChallanDate");
	    String lrDateStr = (String) request.getParameter("lrDate");
	    String deliveryChallanNo = (String) request.getParameter("deliveryChallanNo");
	    String remarks = (String) request.getParameter("remarks");
	    String hideQCflow = (String) request.getParameter("hideQCflow");
	    String freightCharges = (String) request.getParameter("freightCharges");
	    String allowedGraterthanTheOrdered = (String) request.getParameter("allowedGraterthanTheOrdered");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		String shipmentId ="";
		String receiptId="";
		String purposeTypeId="";
		GenericValue shipmentReceipt=null;
		String smsContent="";
		if (UtilValidate.isEmpty(orderId) && UtilValidate.isEmpty(withoutPO)) {
			////Debug.logError("Cannot process receipts without orderId: "+ orderId, module);
			return "error";
		}
		Timestamp receiptDate = null;
		Timestamp supplierInvoiceDate = null;
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			////Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		SimpleDateFormat SimpleDF = new SimpleDateFormat("dd:mm:yyyy hh:mm");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
		receiptDate = UtilDateTime.nowTimestamp();
		Timestamp estimatedDate = UtilDateTime.addDaysToTimestamp(receiptDate,6);
		String estimatedDateStr=UtilDateTime.toDateString(estimatedDate,"dd-MM-yyyy");
	  	/*if(UtilValidate.isNotEmpty(receiptDateStr)){
	  		try {
	  			receiptDate = new java.sql.Timestamp(SimpleDF.parse(receiptDateStr).getTime());
		  	} catch (ParseException e) {
		  		////Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	} catch (NullPointerException e) {
	  			////Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	}
	  	}*/
        DateFormat givenFormatter = new SimpleDateFormat("dd:MM:yyyy hh:mm");
        DateFormat reqformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(UtilValidate.isNotEmpty(receiptDateStr)){
	        try {
	        Date givenReceiptDate = (Date)givenFormatter.parse(receiptDateStr);
	        receiptDate = new java.sql.Timestamp(givenReceiptDate.getTime());
	        }catch (ParseException e) {
		  		////Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	} catch (NullPointerException e) {
	  			////Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	}
        }
        
	  	if(UtilValidate.isNotEmpty(supplierInvoiceDateStr)){
	  		try {
	  			SimpleDateFormat dateSdf = new SimpleDateFormat("dd MMMMM, yyyy");    
	  			supplierInvoiceDate = new java.sql.Timestamp(dateSdf.parse(supplierInvoiceDateStr).getTime());
	  			
		  	} catch (ParseException e) {
		  		////Debug.logError(e, "Cannot parse date string: " + supplierInvoiceDateStr, module);
		  	} catch (NullPointerException e) {
	  			////Debug.logError(e, "Cannot parse date string: " + supplierInvoiceDateStr, module);
		  	}
	  	}
	  	Timestamp lrDateTimeStamp = null;
	  	if(UtilValidate.isNotEmpty(lrDateStr)){
	  		try {
	  			SimpleDateFormat dateSdf = new SimpleDateFormat("dd MMMMM, yyyy");    
	  			lrDateTimeStamp = new java.sql.Timestamp(dateSdf.parse(lrDateStr).getTime());
	  			
		  	} catch (ParseException e) {
		  		////Debug.logError(e, "Cannot parse date string: " + lrDateStr, module);
		  	} catch (NullPointerException e) {
	  			////Debug.logError(e, "Cannot parse date string: " + lrDateStr, module);
		  	}
	  	}
	  	
	  	Timestamp deliveryChallanDate=null;
	  	if(UtilValidate.isNotEmpty(deliveryChallanDateStr)){
	  		try {
	  			SimpleDateFormat dateSdf = new SimpleDateFormat("dd MMMMM, yyyy");    
	  			deliveryChallanDate = new java.sql.Timestamp(dateSdf.parse(deliveryChallanDateStr).getTime());
		  	} catch (ParseException e) {
		  		////Debug.logError(e, "Cannot parse date string: " + deliveryChallanDateStr, module);
		  	} catch (NullPointerException e) {
	  			////Debug.logError(e, "Cannot parse date string: " + deliveryChallanDateStr, module);
		  	}
	  	}else{
		  	 deliveryChallanDate=UtilDateTime.nowTimestamp();
	  	}
	  	BigDecimal poValue = BigDecimal.ZERO;
	  	boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			List conditionList = FastList.newInstance();
			List<GenericValue> extPOItems = FastList.newInstance();
			List<GenericValue> extReciptItems = FastList.newInstance();
			
			boolean directPO = Boolean.TRUE;
			String extPOId = "";
			List productList = FastList.newInstance();
			
			if((UtilDateTime.getIntervalInDays(UtilDateTime.getDayStart(receiptDate), UtilDateTime.getDayStart(nowTimeStamp))) != 0){
	    		////Debug.logError("Check local system date", module);
	    		request.setAttribute("_ERROR_MESSAGE_", "Check local system date");	
				TransactionUtil.rollback();
		  		return "error";
			}
			String originFacilityId ="";
			GenericValue orderHeader = null;
			if(UtilValidate.isNotEmpty(orderId)){
				orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				
				String statusId = orderHeader.getString("statusId");
				String orderTypeId = orderHeader.getString("orderTypeId");
				purposeTypeId = orderHeader.getString("purposeTypeId");
				if(UtilValidate.isNotEmpty(orderHeader.getString("originFacilityId")))
				  {
					originFacilityId = orderHeader.getString("originFacilityId");
				  }
				if(statusId.equals("ORDER_CANCELLED")){
					////Debug.logError("Cannot create GRN for cancelled orders : "+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Cannot create GRN for cancelled orders : "+orderId);	
					TransactionUtil.rollback();
			  		return "error";
				}
				poValue = orderHeader.getBigDecimal("grandTotal");
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
					////Debug.logError("No Vendor for the order : "+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "No Vendor for the order : "+orderId);	
					TransactionUtil.rollback();
			  		return "error";
				}
					
				supplierId = (EntityUtil.getFirst(orderRole)).getString("partyId");
				
			}
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			List changeExprList = FastList.newInstance();
			changeExprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			changeExprList.add(EntityCondition.makeCondition("effectiveDatetime", EntityOperator.LESS_THAN_EQUAL_TO, receiptDate));
			EntityCondition condExpr1 = EntityCondition.makeCondition(changeExprList, EntityOperator.AND);
			List<GenericValue> orderItemChanges = delegator.findList("OrderItemChange", condExpr1, null, UtilMisc.toList("-effectiveDatetime"), null, false);
			
			Timestamp effectiveDatetime = null;
			if(UtilValidate.isNotEmpty(orderItemChanges)){
				effectiveDatetime = (EntityUtil.getFirst(orderItemChanges)).getTimestamp("effectiveDatetime");
				orderItemChanges = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("effectiveDatetime", EntityOperator.EQUALS, effectiveDatetime));
			}

			Map orderItemSeq = FastMap.newInstance();
			for(GenericValue orderItemsValues : orderItems){
				String productId = orderItemsValues.getString("productId");
				String orderItemSeqId = orderItemsValues.getString("orderItemSeqId");
				orderItemSeq.put(productId, orderItemSeqId);
			}

			List orderAdjustmentTypes = EntityUtil.getFieldListFromEntityList(orderAdjustments, "orderAdjustmentTypeId", true);
			
			productList = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
			GenericValue newEntity = delegator.makeValue("Shipment");
	        newEntity.set("estimatedShipDate", lrDateTimeStamp);
	        //Debug.log("purposeTypeId========@@@========"+purposeTypeId);
	        if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("BRANCH_PURCHASE")){
		        newEntity.set("shipmentTypeId", "BRANCH_SHIPMENT");
		        newEntity.set("shipmentPurposeTypeId", "YARN_SHIPMENT");
	        }else if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("DC_PURCHASE")){
	        	newEntity.set("shipmentTypeId", "BRANCH_SHIPMENT");
	        	newEntity.set("shipmentPurposeTypeId", "DC_SHIPMENT");
	        }
	        else if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("DEPOT_PURCHASE")){
	        	newEntity.set("shipmentTypeId", "DEPOT_SHIPMENT");
	        	newEntity.set("shipmentPurposeTypeId", "DEPOT_YARN_SHIPMENT");
	        }
	        else if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("DC_DEPOT_PURCHASE")){
	        	newEntity.set("shipmentTypeId", "DEPOT_SHIPMENT");
	        	newEntity.set("shipmentPurposeTypeId", "DC_DEPOT_SHIPMENT");
	        }
	        /*if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("BRANCH_PURCHASE")){
		        newEntity.set("shipmentTypeId", "BRANCH_SHIPMENT");
	        }else{
	        	newEntity.set("shipmentTypeId", "DEPOT_SHIPMENT");
	        }*/
	        newEntity.set("statusId", "DISPATCHED");
	        newEntity.put("vehicleId",vehicleId);
	        newEntity.put("lrNumber",lrNumber);
	        newEntity.put("carrierName",carrierName);
	        newEntity.put("partyIdFrom",supplierId);
	        newEntity.put("supplierInvoiceId",supplierInvoiceId);
	        newEntity.put("supplierInvoiceDate",supplierInvoiceDate);
	        newEntity.put("estimatedReadyDate",lrDateTimeStamp);
	        newEntity.put("deliveryChallanNumber",deliveryChallanNo);
	        newEntity.put("description",remarks);
	        newEntity.put("deliveryChallanDate",deliveryChallanDate);
	        newEntity.put("primaryOrderId",orderId);
	        if(UtilValidate.isNotEmpty(freightCharges))
            newEntity.set("estimatedShipCost", new BigDecimal(freightCharges));
	        newEntity.set("createdDate", nowTimeStamp);
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            delegator.createSetNextSeqId(newEntity);            
            shipmentId = (String) newEntity.get("shipmentId");
	       
			/*List<Map> prodQtyList = FastList.newInstance();*/
			
            BigDecimal landingCharges = BigDecimal.ZERO;
			for (int i = 0; i < rowCount; i++) {
				
				String productId = "";
		        String quantityStr = "";
		        String deliveryChallanQtyStr = "";
		        String oldRecvdQtyStr = "";
		        String orderItemSeqId = "";
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal deliveryChallanQty = BigDecimal.ZERO;
				BigDecimal oldRecvdQty = BigDecimal.ZERO;
				Map productQtyMap = FastMap.newInstance();
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	    if (paramMap.containsKey("productId" + thisSuffix)) {
				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
					return "error";			  
				}
				if (productId.equals("") || UtilValidate.isEmpty(productId)) {
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
				if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
					orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
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
						GenericValue ordChangesApo = null;
						BigDecimal orderChangeQty=BigDecimal.ZERO;
						BigDecimal orderQty=BigDecimal.ZERO;
						String orderSeqNo="";
						List<GenericValue> ordItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
						if(UtilValidate.isNotEmpty(orderItemSeqId)){
							orderSeqNo=(String)orderItemSeq.get(productId);
							List<GenericValue> ordersequenceChangeList = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				 			if(UtilValidate.isNotEmpty(ordersequenceChangeList)){
				 				orderChangeQty = (EntityUtil.getFirst(ordersequenceChangeList)).getBigDecimal("quantity");

				 			}	
						}
						if(UtilValidate.isNotEmpty(ordItems)){
							checkOrderItem = EntityUtil.getFirst(ordItems);

						}
						
						if(UtilValidate.isNotEmpty(checkOrderItem)){
							     orderQty = checkOrderItem.getBigDecimal("quantity");
						    if(orderChangeQty.compareTo(BigDecimal.ZERO)>0 ){
								 orderQty = orderChangeQty;
						    }
							BigDecimal checkQty = (orderQty.multiply(new BigDecimal(1.1))).setScale(0, BigDecimal.ROUND_CEILING);
							BigDecimal maxQty=oldRecvdQty.add(quantity);
							////Debug.log("=orderQty=="+orderQty+"==checkQty="+checkQty+"==maxQty=="+maxQty+"==quantity="+quantity);
							//if(quantity.compareTo(checkQty)>0){
							if(UtilValidate.isEmpty(allowedGraterthanTheOrdered) || (UtilValidate.isNotEmpty(allowedGraterthanTheOrdered) && "Y".equals(allowedGraterthanTheOrdered))){								
								if(maxQty.compareTo(checkQty)>0){	
									////Debug.logError("Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId, module);
									request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId);	
									TransactionUtil.rollback();
							  		return "error";
								}
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
							////Debug.logError("Quantity cannot be more than ARC/CPC for PO : "+orderId, module);
							request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be more than ARC/CPC for PO : "+orderId);	
							TransactionUtil.rollback();
					  		return "error";
						}
					}

				}
	        }
          		String termTypeId = "";
				BigDecimal amount = BigDecimal.ZERO;
				String amountStr = "";
				if (paramMap.containsKey("termTypeId" + thisSuffix)) {
					termTypeId = (String) paramMap.get("termTypeId" + thisSuffix);
				}
			  
				if (paramMap.containsKey("amount" + thisSuffix)) {
					amountStr = (String) paramMap.get("amount" + thisSuffix);
				}
					  
				if(UtilValidate.isNotEmpty(amountStr)){
					amount = new BigDecimal(amountStr);
				}
				
				if(UtilValidate.isNotEmpty(termTypeId) && amount.compareTo(BigDecimal.ZERO)>0){
					if(!orderAdjustmentTypes.contains(termTypeId)){
						landingCharges = landingCharges.add(amount);
					}
					GenericValue shipmentAttribute = delegator.makeValue("ShipmentAttribute");
					shipmentAttribute.set("shipmentId", shipmentId);
					shipmentAttribute.set("attrName", termTypeId);
					shipmentAttribute.set("attrValue", amountStr);
					delegator.createOrStore(shipmentAttribute);
				}
				////Debug.log("quantityStr======="+quantityStr);
				GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
				String	desc=product.getString("description");
				////Debug.log("desc============"+desc);
				 smsContent = smsContent + quantityStr + " KGs of " + desc + ",";
           if (paramMap.containsKey("productId" + thisSuffix)) {
				Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("shipmentId",shipmentId);
		        itemInMap.put("userLogin",userLogin);
		        itemInMap.put("productId",productId);
		        itemInMap.put("quantity",quantity);
		        Map resultMap = dispatcher.runSync("createShipmentItem",itemInMap);
		        
		        if (ServiceUtil.isError(resultMap)) {
		        	////Debug.logError("Problem creating shipment Item for orderId :"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem creating shipment Item for orderId :"+orderId);	
					TransactionUtil.rollback();
			  		return "error";
		        }
		        
				////Debug.log("resultMap============"+resultMap);

		        
		        String shipmentItemSeqId = (String)resultMap.get("shipmentItemSeqId");
		        //List<GenericValue> productsFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
				List<GenericValue> filteredOrderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				GenericValue ordItm = null;
				if(UtilValidate.isNotEmpty(filteredOrderItem)){
					ordItm = EntityUtil.getFirst(filteredOrderItem);
				    orderItemSeqId = ordItm.getString("orderItemSeqId");
					List<GenericValue> itemChanges = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
					if(UtilValidate.isNotEmpty(itemChanges)){
						ordItm = EntityUtil.getFirst(itemChanges);
					}
				}
				
				
				////Debug.log("ordItm============"+ordItm);

				
				/*List<GenericValue> filterProdFacility = EntityUtil.filterByCondition(productsFacility, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				List prodFacilityIds = EntityUtil.getFieldListFromEntityList(filterProdFacility, "facilityId", true);
				List facilityConditionList = FastList.newInstance();
				facilityConditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "STORE"));
				facilityConditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
				facilityConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,prodFacilityIds));
				EntityCondition facilityCondition = EntityCondition.makeCondition(facilityConditionList, EntityOperator.AND);
				List<GenericValue> facilities = delegator.findList("Facility", facilityCondition, null, null, null, false);
				GenericValue facilityProd = EntityUtil.getFirst(facilities);*/
				//Product should mapped to any one of facility
				/* if (UtilValidate.isEmpty(facilityProd)) {
			        	////Debug.logError("Problem creating shipment Item for ProductId :"+productId+" Not Mapped To Store Facility !", module);
						request.setAttribute("_ERROR_MESSAGE_", "Problem creating shipment Item for ProductId :"+productId+" Not Mapped To Store Facility!");	
						TransactionUtil.rollback();
				  		return "error";
			        }*/
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
				if(UtilValidate.isNotEmpty(originFacilityId)){

				inventoryReceiptCtx.put("facilityId", originFacilityId);
				}else{
					inventoryReceiptCtx.put("facilityId", "BRANCH1");
				}
				//facilityProd.getString("facilityId"));
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
				////Debug.log("receiveInventoryResult============"+receiveInventoryResult);

				if (ServiceUtil.isError(receiveInventoryResult)) {
					////Debug.logWarning("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
					request.setAttribute("_ERROR_MESSAGE_", "There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult));	
					TransactionUtil.rollback();
			  		return "error";
	            }
				
				receiptId = (String)receiveInventoryResult.get("receiptId");
				shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
				if(UtilValidate.isNotEmpty(shipmentReceipt)){
					shipmentReceipt.set("shipmentId", shipmentId);
					shipmentReceipt.set("shipmentItemSeqId", shipmentItemSeqId);
					shipmentReceipt.store();
				}
				//storing shipment receipt status Here 
				if(UtilValidate.isNotEmpty(receiptId)){
					GenericValue shipmentReceiptStatus = delegator.makeValue("ShipmentReceiptStatus");
					shipmentReceiptStatus.set("receiptId", receiptId);
					shipmentReceiptStatus.set("statusId", (String) shipmentReceipt.get("statusId"));
					shipmentReceiptStatus.set("changedByUserLogin", userLogin.getString("userLoginId"));
					shipmentReceiptStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
					delegator.createSetNextSeqId(shipmentReceiptStatus);
				}
           }
              
           }
			////Debug.log("orderId============"+orderId);

			if(UtilValidate.isNotEmpty(orderId) && landingCharges.compareTo(BigDecimal.ZERO)>0){
				List condExpr = FastList.newInstance();
				condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				condExpr.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				EntityCondition cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
				List<GenericValue> shipReceipts = delegator.findList("ShipmentReceipt", cond, null, null, null, false);
				
				
				////Debug.log("shipReceipts============"+shipReceipts);

				
				// recalculating landing cost for handling transportation/frieght at actuals and installation charges
				for(GenericValue shipReceipt : shipReceipts){
					
					
					////Debug.log("shipReceipts====orderId========"+shipReceipt.getString("orderId"));
					
					////Debug.log("shipReceipts====orderItemSeqId========"+shipReceipt.getString("orderItemSeqId"));

					
					
					condExpr.clear();
					condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipReceipt.getString("orderId")));
					condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, shipReceipt.getString("orderItemSeqId")));
					EntityCondition condition = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
					
					List<GenericValue> orderItem = EntityUtil.filterByCondition(orderItems, condition);
					
					condExpr.clear();
					condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipReceipt.getString("orderId")));
					condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, shipReceipt.getString("orderItemSeqId")));
					EntityCondition condition1 = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
					
					List<GenericValue> ordItmChange = EntityUtil.filterByCondition(orderItemChanges, condition);

					////Debug.log("ordItmChange============"+ordItmChange);

					
					if(UtilValidate.isNotEmpty(orderItem)){
						BigDecimal unitListPrice = (EntityUtil.getFirst(orderItem)).getBigDecimal("unitListPrice");
						if(UtilValidate.isNotEmpty(ordItmChange)){
							unitListPrice = (EntityUtil.getFirst(ordItmChange)).getBigDecimal("unitListPrice");
						}
						BigDecimal qty = shipReceipt.getBigDecimal("quantityAccepted");
						
						BigDecimal itemValue = unitListPrice.multiply(qty);
						BigDecimal calcAmt = (itemValue.multiply(landingCharges)).divide(poValue, purchaseTaxFinalDecimals, purchaseTaxRounding);
						BigDecimal adjUnitAmt = calcAmt.divide(qty, purchaseTaxFinalDecimals, purchaseTaxRounding);
						BigDecimal updatedLandingCost = unitListPrice.add(adjUnitAmt);
						String inventoryItemId = shipReceipt.getString("inventoryItemId");
						GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
						inventoryItem.set("unitCost", updatedLandingCost);
						inventoryItem.store();
					}
					
				}
			}
			GenericValue orderHeaderDetails = null;
			orderHeaderDetails = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			
			
			////Debug.log("orderHeaderDetails============"+orderHeaderDetails);

	        String statusId = orderHeaderDetails.getString("statusId");
	 /*       if(statusId.equals("ORDER_COMPLETED")){
				List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("toOrderId"), null, null, false);
				
				String toOrderId ="";
				if(UtilValidate.isNotEmpty(orderAssoc)){
					toOrderId = (EntityUtil.getFirst(orderAssoc)).getString("toOrderId");
				}
	        	Map statusCtx = FastMap.newInstance();
	 			statusCtx.put("statusId", statusId);
	 			statusCtx.put("orderId", toOrderId);
	 			statusCtx.put("userLogin", userLogin);
	 			Map resultCtx = OrderServices.setOrderStatus(dctx, statusCtx);
	 			if (ServiceUtil.isError(resultCtx)) {
	 				////Debug.logError("Order set status failed for orderId: " + orderId, module);
	 				return "error";
	 			} 
	        }*/
	if(UtilValidate.isNotEmpty(hideQCflow)&&("Y".equals(hideQCflow))){
				
			Map inputMap = FastMap.newInstance();
			inputMap.put("statusIdTo","SR_QUALITYCHECK");
			inputMap.put("receiptId",receiptId);
			inputMap.put("partyId","DEPOT");
			inputMap.put("userLogin",userLogin);
			Map resultMap = dispatcher.runSync("sendReceiptQtyForQC",inputMap);
			
			////Debug.log("resultMap============"+resultMap);

			
			if (ServiceUtil.isError(resultMap)) {
				////Debug.logWarning("There was an error while sending to QC: " + ServiceUtil.getErrorMessage(resultMap), module);
				request.setAttribute("_ERROR_MESSAGE_", "There was an error sending to QC: " + ServiceUtil.getErrorMessage(resultMap));	
				TransactionUtil.rollback();
		  		return "error";
            }
			inputMap.clear();
			resultMap.clear();
			
			inputMap.put("statusIdTo","SR_ACCEPTED");
			inputMap.put("receiptId",receiptId);
			inputMap.put("shipmentId",shipmentId);
			inputMap.put("shipmentItemSeqId",shipmentReceipt.get("shipmentItemSeqId") );
			inputMap.put("quantityAccepted",shipmentReceipt.get("quantityAccepted"));
			inputMap.put("userLogin",userLogin);
			resultMap = dispatcher.runSync("acceptReceiptQtyByQC", inputMap);
			
			////Debug.log("resultMap==3445=========="+resultMap);

			if (ServiceUtil.isError(resultMap)) {
				////Debug.logWarning("There was an error while Accepting in QC: " + ServiceUtil.getErrorMessage(resultMap), module);
				request.setAttribute("_ERROR_MESSAGE_", "There was an error Accepting in QC: " + ServiceUtil.getErrorMessage(resultMap));	
				TransactionUtil.rollback();
		  		return "error";
            }
		}
	}
		catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			////Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
		     	request.setAttribute("_ERROR_MESSAGE_", "Could not rollback transaction: " );
				return "error";
	  		}
	  		////Debug.logError("An entity engine error occurred while fetching data", module);
	  		request.setAttribute("_ERROR_MESSAGE_", "An entity engine error occurred while fetching data: " );
			return "error";

	  	}
  	  	catch (GenericServiceException e) {
  	  		try {
  			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
  	  		} catch (GenericEntityException e2) {
  			  ////Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  		      request.setAttribute("_ERROR_MESSAGE_", "Could not rollback transaction: " );
		      return "error";
  	  		}
  	  		////Debug.logError("An entity engine error occurred while calling services", module);
  	     	request.setAttribute("_ERROR_MESSAGE_", "An entity engine error occurred while calling services" );
		    return "error";
  	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beganTransaction);
	  		} catch (GenericEntityException e) {
	  			////Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			request.setAttribute("_ERROR_MESSAGE_", "Could not commit transaction for entity engine error occurred while fetching data" );
				return "error";
	  		}
	  	}
		
		////Debug.log("orderId==3445=========="+orderId);

		
		try {
        	List condExpr = FastList.newInstance();
			condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condExpr.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
			EntityCondition cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
			List<GenericValue> orderRoleList = delegator.findList("OrderRole", cond, null, null, null, false);
		 
			////Debug.log("orderRoleList==3445=========="+orderRoleList);

			
			GenericValue orderRoleFirstList = EntityUtil.getFirst(orderRoleList);
			String partyIdTo = (String) orderRoleFirstList.getString("partyId");
			GenericValue ShipmentList = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);  
			
			////Debug.log("ShipmentList==3445=========="+ShipmentList);

			 if (UtilValidate.isNotEmpty(ShipmentList)) {
	 			  
				 ShipmentList.set("shipmentId",shipmentId);
				 ShipmentList.set("partyIdTo",partyIdTo);
				 
 		        try {
 		        	ShipmentList.store();
 		        } catch (GenericEntityException e) {
		  			////Debug.logError(e, "Could not Update partyIdTo in Shipment entity", module);
		  			request.setAttribute("_ERROR_MESSAGE_", "Could not get date from OrderAttribute" );
					return "error";
 		        }
			 }
        } catch (GenericEntityException e) {
	  			////Debug.logError(e, "Could not get date from OrderAttribute", module);
	  			request.setAttribute("_ERROR_MESSAGE_", "Could not get date from OrderAttribute" );
				return "error";
    }

		String rlatedId="";
		String customerId="";
		List condiList = FastList.newInstance();
		try{
			condiList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condiList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
			List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition(condiList, EntityOperator.AND), null, null, null, false);
			////Debug.log("orderAssoc==3445=========="+orderAssoc);

			if(UtilValidate.isNotEmpty(orderAssoc)){
				rlatedId = (EntityUtil.getFirst(orderAssoc)).getString("toOrderId");
			
			}
		}catch (GenericEntityException e) {
			////Debug.logError(e, "Could not get date from OrderAssoc", module);
			//request.setAttribute("_ERROR_MESSAGE_", "Could not get date from OrderAssoc" );
			//return "error";
		}
		////Debug.log("orderID=================="+orderId+"==rlatedId=================="+rlatedId);

		if(UtilValidate.isNotEmpty(rlatedId)){
			try{
			condiList.clear();
			condiList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, rlatedId));
			condiList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
			EntityCondition condi = EntityCondition.makeCondition(condiList, EntityOperator.AND);
			List<GenericValue> orderRoleForCustomer = delegator.findList("OrderRole", condi, null, null, null, false);
			
			if(UtilValidate.isEmpty(orderRoleForCustomer)){
				////Debug.logError("No Vendor for the order : "+rlatedId, module);
				request.setAttribute("_ERROR_MESSAGE_", "No Vendor for the order : "+rlatedId);	
				TransactionUtil.rollback();
		  		return "error";
			}
				
			customerId = (EntityUtil.getFirst(orderRoleForCustomer)).getString("partyId");
			}catch (GenericEntityException e) {
				////Debug.logError(e, "Could not get date from OrderRole", module);
				//request.setAttribute("_ERROR_MESSAGE_", "Could not get date from OrderRole" );
				//return "error";
			}
		}

		String shipmentMessageToWeaver = UtilProperties.getMessage("ProductUiLabels", "ShipmentMessageToWeaver", locale);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("orderId", rlatedId);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("material", smsContent);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("transporter", carrierName);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("lrNo", lrNumber);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("lrDate", deliveryChallanDateStr);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("expectedDeliveryDate", estimatedDateStr);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("estimatedReadyDate", lrDateStr);
		if(UtilValidate.isNotEmpty(customerId)){
			String customerName=org.ofbiz.party.party.PartyHelper.getPartyName(delegator,supplierId, false);
			Map<String, Object> getTelParams = FastMap.newInstance();
			if(UtilValidate.isEmpty(customerName)){
				customerName=supplierId;
			}
        	getTelParams.put("partyId", customerId);
            getTelParams.put("userLogin", userLogin); 
            try{
            	Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	                return "error";
	            }
	            
	            String contactNumberTo = (String) serviceResult.get("contactNumber");            
	            String countryCode = (String) serviceResult.get("countryCode");
	           /* if(UtilValidate.isEmpty(contactNumberTo)){
	            	contactNumberTo = "7330776928";
	            }
	            if(UtilValidate.isEmpty(carrierName)){
	            	carrierName = "_";
	            }
	            contactNumberTo = "7330776928";*/
	            ////Debug.log("contactNumberTo = "+contactNumberTo);
	            if(UtilValidate.isNotEmpty(contactNumberTo)){
	            	 if(UtilValidate.isNotEmpty(countryCode)){
	            		 contactNumberTo = countryCode + contactNumberTo;
	            	 }
	            	 ////Debug.log("contactNumberTo ===== "+contactNumberTo);
	            	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                 sendSmsParams.put("contactNumberTo", contactNumberTo);
	                 sendSmsParams.put("text", shipmentMessageToWeaver); 
	                 ////Debug.log("sendSmsParams====================="+sendSmsParams);
	                 serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	                 if (ServiceUtil.isError(serviceResult)) {
	                     ////Debug.logError("unable to send Sms", module);
	     				//request.setAttribute("_ERROR_MESSAGE_", "unable to send Sms : "+rlatedId);	
	                     //return "error";
	                 }
	            	
	            	/*Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                sendSmsParams.put("contactNumberTo", contactNumberTo);
	                sendSmsParams.put("text", "Order placed on M/s. "+ suppPartyId +" against your Indent No. "+orderId);            
	                serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);       
	                if (ServiceUtil.isError(serviceResult)) {
	                    ////Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                    return serviceResult;
	                }*/
	            }
            }catch(GenericServiceException e1){
	         	////Debug.log("Problem in sending sms to user agency");
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
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
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
		String applicableTo = "ALL";
		for (int i = 0; i < rowCount; i++) {
			
			Map prodQtyMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String vatStr=null;
			String cstStr=null;
			
			BigDecimal vat = BigDecimal.ZERO;
			BigDecimal cst = BigDecimal.ZERO;
			//percenatge of TAXes
			
			String VatPercentStr=null;
			String CSTPercentStr=null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
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
			
			if (paramMap.containsKey("applicableTo" + thisSuffix)) {
				applicableTo = (String) paramMap.get("applicableTo" + thisSuffix);
			}
			
			if (paramMap.containsKey("adjAmt" + thisSuffix)) {
				adjAmtStr = (String) paramMap.get("adjAmt" + thisSuffix);
			}
			
			if(UtilValidate.isNotEmpty(adjAmtStr)){
				try {
					adjAmt = new BigDecimal(adjAmtStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			
			
			if(UtilValidate.isNotEmpty(invoiceItemTypeId) && adjAmt.compareTo(BigDecimal.ZERO)>0){
				Map invItemMap = FastMap.newInstance();
				invItemMap.put("otherTermId", invoiceItemTypeId);
				invItemMap.put("termValue", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
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
				
				if (paramMap.containsKey("CST" + thisSuffix)) {
					cstStr = (String) paramMap.get("CST" + thisSuffix);
				}
				
				if (paramMap.containsKey("VatPercent" + thisSuffix)) {
					VatPercentStr = (String) paramMap.get("VatPercent" + thisSuffix);
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
				prodQtyMap.put("cstAmount", cst);
				prodQtyMap.put("vatPercent", vatPercent);
				prodQtyMap.put("cstPercent", cstPercent);
				prodQtyMap.put("bedPercent", BigDecimal.ZERO);
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
		if(UtilValidate.isNotEmpty(isDisableAcctg)){
			processInvoiceContext.put("isDisableAcctg", isDisableAcctg);
		}
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
		  	String isDisableAcctg = (String) context.get("isDisableAcctg");
		  	String shipmentId = (String) context.get("shipmentId");
		  	Debug.log("#####context#########"+context);
		  	boolean beganTransaction = false;
		  	String currencyUomId = "INR";
			Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
			String partyId="Company";
			if (UtilValidate.isEmpty(partyIdFrom)) {
				Debug.logError("Cannot create invoice without partyId: "+ partyIdFrom, module);
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
				List<GenericValue> shipmentReceipts = delegator.findList("ShipmentReceipt", condExpr, null, null, null, false);
				if(UtilValidate.isEmpty(shipmentReceipts)){
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
				Map resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", productQtyList, "otherCharges", invoiceAdjChargesList, "userLogin", userLogin, "incTax", ""));
				if(ServiceUtil.isError(resultCtx)){
	  		  		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
	  		  		Debug.logError(errMsg , module);
	  		  		return ServiceUtil.returnError(errMsg);
				}
				List<Map> itemDetails = (List)resultCtx.get("itemDetail");
				List<Map> adjustmentDetail = (List)resultCtx.get("adjustmentDetail");
				Map input = FastMap.newInstance();
				input.put("userLogin", userLogin);
		        input.put("invoiceTypeId", "PURCHASE_INVOICE");        
		        input.put("partyIdFrom", partyIdFrom);	
		        input.put("statusId", "INVOICE_IN_PROCESS");	
		        input.put("currencyUomId", currencyUomId);
		        input.put("invoiceDate", invoiceDate);
		        input.put("dueDate", invoiceDate); 	        
		        input.put("partyId", partyId);
		        input.put("purposeTypeId", purposeTypeId);
		        if(UtilValidate.isNotEmpty(isDisableAcctg)){
			        input.put("isEnableAcctg", "N");
				}
		        input.put("createdByUserLogin", userLogin.getString("userLoginId"));
		        input.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
		        result = dispatcher.runSync("createInvoice", input);
				if (ServiceUtil.isError(result)) {
					return ServiceUtil.returnError("Error while creating invoice for party : "+partyId, null, null, result);
				}
				
				String invoiceId = (String)result.get("invoiceId");
				for (Map<String, Object> prodQtyMap : itemDetails) {
					
					String productId = "";
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal amount = BigDecimal.ZERO;
					Map invoiceItemCtx = FastMap.newInstance();
					BigDecimal unitListPrice = BigDecimal.ZERO;
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
						BigDecimal unitPrice = amount;
						if(UtilValidate.isNotEmpty(prodQtyMap.get("bedUnitRate"))){
							unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedUnitRate"));
						}
						if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessUnitRate"))){
							unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedcessUnitRate"));
						}
						if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessUnitRate"))){
							unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedseccessUnitRate"));
						}
						invoiceItemCtx.put("amount", unitPrice);
						invoiceItemCtx.put("unitPrice", unitPrice);
						
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
						unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
						invoiceItemCtx.put("unitListPrice", unitListPrice);
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
					
					List<GenericValue> receipts = EntityUtil.filterByCondition(shipmentReceipts, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					if(UtilValidate.isNotEmpty(receipts)){
						String inventoryItemId = (EntityUtil.getFirst(receipts)).getString("inventoryItemId");
						
						GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
						
						if(UtilValidate.isNotEmpty(inventoryItem)){
							inventoryItem.set("unitCost", unitListPrice);
							inventoryItem.store();
						}
					}
				}
				
				for (Map<String, Object> adjustMap : adjustmentDetail) {
					
					String adjustmentTypeId = "";
					BigDecimal amount = BigDecimal.ZERO;
					Map invoiceItemCtx = FastMap.newInstance();
					if(UtilValidate.isNotEmpty(adjustMap.get("adjustmentTypeId"))){
						adjustmentTypeId = (String)adjustMap.get("adjustmentTypeId");
						invoiceItemCtx.put("invoiceItemTypeId", adjustmentTypeId);
					}
					if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
						amount = (BigDecimal)adjustMap.get("amount");
						invoiceItemCtx.put("amount", amount);
					}
					if(UtilValidate.isNotEmpty(adjustmentTypeId) && !(amount.compareTo(BigDecimal.ZERO) == 0)){
						invoiceItemCtx.put("invoiceId", invoiceId);
						invoiceItemCtx.put("quantity", BigDecimal.ONE);
						invoiceItemCtx.put("userLogin", userLogin);
						result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Item : "+adjustmentTypeId, module);	
							return ServiceUtil.returnError("Error creating Invoice item for Item : "+adjustmentTypeId);
						}
						String invItemSeqId = (String) result.get("invoiceItemSeqId");
					}
					
				}
				
				GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
				invoice.set("shipmentId", shipmentId);
				invoice.store();
				
				result.put("invoiceId", invoiceId);
				 // creating invoiceRole for order
				 Map<String, Object> createInvoiceRoleContext = FastMap.newInstance();
			        createInvoiceRoleContext.put("invoiceId", result.get("invoiceId"));
			        createInvoiceRoleContext.put("userLogin", userLogin);
			   
			    	   List condLIst = FastList.newInstance();
			    	   condLIst.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
						EntityCondition condExpr1 = EntityCondition.makeCondition(condLIst, EntityOperator.AND);
						List<GenericValue> orderRoles = delegator.findList("OrderRole", condExpr1, null, null, null, false);
			   
			      for (GenericValue orderRole : orderRoles) {
				            createInvoiceRoleContext.put("partyId", orderRole.getString("partyId"));
				            createInvoiceRoleContext.put("roleTypeId", orderRole.getString("roleTypeId"));
				            Map<String, Object> createInvoiceRoleResult = dispatcher.runSync("createInvoiceRole", createInvoiceRoleContext);
				            if (ServiceUtil.isError(createInvoiceRoleResult)) {
				            	Debug.logError("Error creating InvoiceRole  for orderId : "+orderId, module);	
								return ServiceUtil.returnError("Error creating Invoice Role for orderId : "+orderId);
				            }
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
		String purposeTypeId = (String) request.getParameter("purposeTypeId");
		String billFromPartyId = (String) request.getParameter("billToPartyId");
		String shipToPartyId = (String) request.getParameter("facilityId");
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
		List<Map> otherChargesList = FastList.newInstance();
		String partyIdFrom = "";
		Map processOrderContext = FastMap.newInstance();
		String salesChannel = (String)request.getParameter("salesChannel");
        String partyGeoId = (String) request.getParameter("supplierGeoId");
		String purchaseTitleTransferEnumId = (String) request.getParameter("purchaseTitleTransferEnumId");
		String purchaseTaxType = (String) request.getParameter("purchaseTaxType");
	  
		String productId = null;
		String quantityStr = null;
		String unitPriceStr=null;
		String bundleUnitPriceStr=null;
		Timestamp effectiveDate=null;
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal unitPrice = BigDecimal.ZERO;
		BigDecimal bundleUnitPrice = BigDecimal.ZERO;
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
		if (rowCount < 0) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		List termsList = FastList.newInstance();
		String eventResult = getMaterialPOValue(request, response);
		if(eventResult.equals("error")){
			Debug.logError("Problems getting po data from grid", module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems getting po data from grid");
			return "error";
		}
		BigDecimal grandTotal = (BigDecimal)request.getAttribute("grandTotal");
		List<Map> otherTermDetail = (List)request.getAttribute("termsDetail");
		//List<Map> itemDetail = (List)request.getAttribute("itemDetail");
		List<Map> itemDetail = FastList.newInstance();
		List<Map> adjustmentDetail = (List)request.getAttribute("adjustmentDetail");
		/*if( UtilValidate.isEmpty(itemDetail)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}*/
		String orderItemSeqId = null;
		String batchNo = null;
		String daysToStore = null;
		String basicPriceStr = null;
		String vatPriceStr = null;
		String cessPriceStr = null;
		String bedPriceStr = null;
		String cstPriceStr = null;
		String remarksStr = null;
		String serTaxPriceStr = null;
		BigDecimal basicPrice = BigDecimal.ZERO;
		BigDecimal cstPrice = BigDecimal.ZERO;
		BigDecimal vatPrice = BigDecimal.ZERO;
		BigDecimal bedPrice = BigDecimal.ZERO;
		BigDecimal cessPrice = BigDecimal.ZERO;
		BigDecimal serviceTaxPrice = BigDecimal.ZERO;
		//percentage fields
		String bedPercentStr = null;
		String cessPercentStr = null;
		String vatPercentStr = null;
		String cstPercentStr = null;
		String tcsPercentStr = null;
		String serviceTaxPercentStr = null;
		String packagingStr = null;
		String packetStr = null;
		
		
		
		String purTaxListStr = null;
		
		BigDecimal bedPercent=BigDecimal.ZERO;
		BigDecimal cessPercent=BigDecimal.ZERO;
		BigDecimal vatPercent=BigDecimal.ZERO;
		BigDecimal cstPercent=BigDecimal.ZERO;
		BigDecimal tcsPercent=BigDecimal.ZERO;
		BigDecimal serviceTaxPercent=BigDecimal.ZERO;
		
		BigDecimal yarnUOM1=BigDecimal.ZERO;
		BigDecimal bundleWeight1=BigDecimal.ZERO;
		BigDecimal baleQuantity1=BigDecimal.ZERO;
		
		BigDecimal packaging=BigDecimal.ZERO;
		BigDecimal packet=BigDecimal.ZERO;
		
		
		String orderAdjustmentsListStr = null;
		for (int i = 0; i <= rowCount; i++) {
			
			List purTaxRateList = FastList.newInstance();
			List orderAdjustmentList = FastList.newInstance();
			
			Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;			
			List productIds = FastList.newInstance();
			String termTypeInput= (String) paramMap.get("otherTermId" + thisSuffix);
			if (UtilValidate.isNotEmpty(termTypeInput)) {
				String otherTermId = "";
				String applicableTo = "ALL";
				String termValueStr = "";
				String termDaysStr = "";
				String description = "";
				String uomId = "INR";
				String assessableValue = "";
				BigDecimal termValue = BigDecimal.ZERO;
				BigDecimal termDays = BigDecimal.ZERO;
				
				if (paramMap.containsKey("otherTermId" + thisSuffix)) {
					otherTermId = (String) paramMap.get("otherTermId" + thisSuffix);
				}
				Debug.log("otherTermId==============="+otherTermId);
				
				if (paramMap.containsKey("applicableTo" + thisSuffix)) {
					applicableTo = (String) paramMap.get("applicableTo" + thisSuffix);
				}
				Debug.log("applicableTo==============="+applicableTo);
				if (paramMap.containsKey("adjustmentValue" + thisSuffix)) {
					termValueStr = (String) paramMap.get("adjustmentValue" + thisSuffix);
				}
				Debug.log("termValueStr==============="+termValueStr);
				
				if (paramMap.containsKey("termDays" + thisSuffix)) {
					termDaysStr = (String) paramMap.get("termDays" + thisSuffix);
				}
				Debug.log("termDaysStr==============="+termDaysStr);
				if (paramMap.containsKey("description" + thisSuffix)) {
					description = (String) paramMap.get("description" + thisSuffix);
				}
				Debug.log("description==============="+description);
				if (paramMap.containsKey("uomId" + thisSuffix)) {
					uomId = (String) paramMap.get("uomId" + thisSuffix);
				}
				Debug.log("uomId==============="+uomId);
				if (paramMap.containsKey("assessableValue" + thisSuffix)) {
					assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
				}
				Debug.log("assessableValue==============="+assessableValue);
				if(UtilValidate.isNotEmpty(termValueStr)){
					try {
						termValue = new BigDecimal(termValueStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing term value string: " + termValueStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing term value string: " + termValueStr);
						return "error";
					}
				}
				Debug.log("termValue==============="+termValue);
				if(UtilValidate.isNotEmpty(termDaysStr)){
					try {
						termDays = new BigDecimal(termDaysStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing term days string: " + termDaysStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing term days string: " + termDaysStr);
						return "error";
					}
				}
				Debug.log("termDays==============="+termDays);
				if(UtilValidate.isNotEmpty(otherTermId) && termValue.compareTo(BigDecimal.ZERO)>0){
					Map otherChargesDetail = FastMap.newInstance();
					otherChargesDetail.put("otherTermId", otherTermId);
					otherChargesDetail.put("adjustmentValue", termValue);
					otherChargesDetail.put("applicableTo", applicableTo);
					otherChargesDetail.put("termDays", termDays);
					otherChargesDetail.put("uomId", uomId);
					otherChargesDetail.put("assessableValue", assessableValue);
					otherChargesDetail.put("description", description);
					otherChargesList.add(otherChargesDetail);
				}
			}
			String productInput= (String) paramMap.get("productId" + thisSuffix);
			//invoke if only not empty
			
			String yarnUOM = "";
			String bundleWeight = "";
			String baleQuantity = "";
			
			
			if (UtilValidate.isNotEmpty(productInput)) {

				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
					productIds.add(productId);
				} else {
					request.setAttribute("_ERROR_MESSAGE_",
							"Missing product id");
					return "error";
				}
				
				if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
					orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
				}
				Debug.log("orderItemSeqId==============="+orderItemSeqId);
				
				if (paramMap.containsKey("yarnUOM" + thisSuffix)) {
					yarnUOM = (String) paramMap.get("yarnUOM" + thisSuffix);
				}
				Debug.log("yarnUOM==============="+yarnUOM);
				if (paramMap.containsKey("bundleWeight" + thisSuffix)) {
					bundleWeight = (String) paramMap.get("bundleWeight" + thisSuffix);
				}
				Debug.log("bundleWeight==============="+bundleWeight);
				if (paramMap.containsKey("baleQuantity" + thisSuffix)) {
					baleQuantity = (String) paramMap.get("baleQuantity" + thisSuffix);
				}
				Debug.log("baleQuantity==============="+baleQuantity);
				
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
				} else {
					request.setAttribute("_ERROR_MESSAGE_",
							"Missing product quantity");
					return "error";
				}
				
				Debug.log("quantityStr==============="+quantityStr);
				if (quantityStr.equals("")) {
					request.setAttribute("_ERROR_MESSAGE_",
							"Empty product quantity");
					return "error";
				}
		
				Debug.log("quantityStr==============="+quantityStr);
				
				if (paramMap.containsKey("unitPrice" + thisSuffix)) {
					unitPriceStr = (String) paramMap.get("unitPrice"
							+ thisSuffix);
				}
				Debug.log("unitPriceStr==============="+unitPriceStr);
				if (paramMap.containsKey("bundleUnitPrice" + thisSuffix)) {
					bundleUnitPriceStr = (String) paramMap.get("bundleUnitPrice"
							+ thisSuffix);
				}
				Debug.log("bundleUnitPriceStr==============="+bundleUnitPriceStr);
				
				if (paramMap.containsKey("remarks" + thisSuffix)) {
					remarksStr = (String) paramMap.get("remarks"
							+ thisSuffix);
				}
				Debug.log("remarksStr==============="+remarksStr);

				if (paramMap.containsKey("basicPrice" + thisSuffix)) {
					basicPriceStr = (String) paramMap.get("basicPrice"
							+ thisSuffix);
				}
				Debug.log("basicPriceStr==============="+basicPriceStr);
				if (paramMap.containsKey("vatPrice" + thisSuffix)) {
					vatPriceStr = (String) paramMap
							.get("vatPrice" + thisSuffix);
				}
				Debug.log("vatPriceStr==============="+vatPriceStr);
				if (paramMap.containsKey("bedPrice" + thisSuffix)) {
					bedPriceStr = (String) paramMap
							.get("bedPrice" + thisSuffix);
				}
				Debug.log("bedPriceStr==============="+bedPriceStr);
				if (paramMap.containsKey("cstPrice" + thisSuffix)) {
					cstPriceStr = (String) paramMap
							.get("cstPrice" + thisSuffix);
				}
				Debug.log("cstPriceStr==============="+cstPriceStr);

				if (paramMap.containsKey("bedPercent" + thisSuffix)) {
					bedPercentStr = (String) paramMap.get("bedPercent"
							+ thisSuffix);
				}
				Debug.log("bedPercentStr==============="+bedPercentStr);
				if (paramMap.containsKey("cessPercent" + thisSuffix)) {
					bedPercentStr = (String) paramMap.get("cessPercent"
							+ thisSuffix);
				}
				Debug.log("bedPercentStr==============="+bedPercentStr);
				if (paramMap.containsKey("vatPercent" + thisSuffix)) {
					vatPercentStr = (String) paramMap.get("vatPercent"
							+ thisSuffix);
				}
				Debug.log("vatPercentStr==============="+vatPercentStr);
				if (paramMap.containsKey("cstPercent" + thisSuffix)) {
					cstPercentStr = (String) paramMap.get("cstPercent"
							+ thisSuffix);
				}
				Debug.log("cstPercentStr==============="+cstPercentStr);
				if (paramMap.containsKey("Packaging" + thisSuffix)) {
					packagingStr = (String) paramMap.get("Packaging"
							+ thisSuffix);
				}
				Debug.log("packagingStr==============="+packagingStr);
				if (paramMap.containsKey("packets" + thisSuffix)) {
					packetStr = (String) paramMap.get("packets"
							+ thisSuffix);
				}
				Debug.log("packetStr==============="+packetStr);
				
				
//Purchase tax list
				
				if (paramMap.containsKey("purTaxList" + thisSuffix)) {
					purTaxListStr = (String) paramMap.get("purTaxList"
							+ thisSuffix);
					String[] taxList = purTaxListStr.split(",");
					for (int j = 0; j < taxList.length; j++) {
						String taxType = taxList[j];
						Map taxRateMap = FastMap.newInstance();
						String purTaxType = taxType.replace("_SALE", "_PUR");
						taxRateMap.put("orderAdjustmentTypeId",purTaxType);
						taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
						taxRateMap.put("amount",BigDecimal.ZERO);
						taxRateMap.put("taxAuthGeoId", partyGeoId);
						
						if (paramMap.containsKey(taxType + "_PUR" + thisSuffix)) {
							String taxPercentage = (String) paramMap.get(taxType + "_PUR" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxPercentage) && !(taxPercentage.equals("NaN"))){
								taxRateMap.put("sourcePercentage",new BigDecimal(taxPercentage));
							}
							Debug.log("taxPercentage==============="+taxPercentage);
						}
						
						if (paramMap.containsKey(taxType+ "_PUR_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(taxType+ "_PUR_AMT" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								taxRateMap.put("amount",new BigDecimal(taxAmt));
							}
						}
						
						
						
						Map tempTaxMap = FastMap.newInstance();
						tempTaxMap.putAll(taxRateMap);
						
						purTaxRateList.add(tempTaxMap);
					}
				}
				
				if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
					orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
					
					String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
					for (int j = 0; j < orderAdjustmentsList.length; j++) {
						String orderAdjustmentType = orderAdjustmentsList[j];
						Map adjTypeMap = FastMap.newInstance();
						adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
						adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
						adjTypeMap.put("amount",BigDecimal.ZERO);
						//adjTypeMap.put("taxAuthGeoId", partyGeoId);
						
						if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
							String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
							if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
								adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
							}
							Debug.log("adjPercentage==============="+adjPercentage);	
						}
						
						if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								
								if(orderAdjustmentType.equals("PRICE_DISCOUNT"))
								adjTypeMap.put("amount",new BigDecimal(taxAmt).negate());
								else
								adjTypeMap.put("amount",new BigDecimal(taxAmt));	
							}
							Debug.log("taxAmt==============="+taxAmt);
						}
						
						if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
							String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
							if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
								if(isAssessableValue.equals("TRUE")){
									adjTypeMap.put("isAssessableValue", "Y");
									//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
								}
							}
							Debug.log("isAssessableValue==============="+isAssessableValue);
						}
						
						Map tempAdjMap = FastMap.newInstance();
						tempAdjMap.putAll(adjTypeMap);
						
						orderAdjustmentList.add(tempAdjMap);
					}
				}
				try {
					quantity = new BigDecimal(quantityStr);
					if (UtilValidate.isNotEmpty(unitPriceStr)) {
						unitPrice = new BigDecimal(unitPriceStr);
					}
					Debug.log("unitPrice==============="+unitPrice);
					if (UtilValidate.isNotEmpty(bundleUnitPriceStr)) {
						bundleUnitPrice = new BigDecimal(bundleUnitPriceStr);
					}
					Debug.log("bundleUnitPrice==============="+bundleUnitPrice);
					if (UtilValidate.isNotEmpty(basicPriceStr)) {
						basicPrice = new BigDecimal(basicPriceStr);
					}
					Debug.log("basicPrice==============="+basicPrice);
					if (UtilValidate.isNotEmpty(cstPriceStr)) {
						cstPrice = new BigDecimal(cstPriceStr);
					}
					Debug.log("cstPrice==============="+cstPrice);
					if (UtilValidate.isNotEmpty(bedPriceStr)) {
						bedPrice = new BigDecimal(bedPriceStr);
					}
					Debug.log("bedPrice==============="+bedPrice);
					if (UtilValidate.isNotEmpty(cessPriceStr)) {
						cessPrice = new BigDecimal(cessPriceStr);
					}
					Debug.log("cessPrice==============="+cessPrice);
					if (UtilValidate.isNotEmpty(vatPriceStr)) {
						vatPrice = new BigDecimal(vatPriceStr);
					}
					Debug.log("vatPrice==============="+vatPrice);
					if (UtilValidate.isNotEmpty(bedPercentStr)) {
						bedPercent = new BigDecimal(bedPercentStr);
					}
					Debug.log("bedPercent==============="+bedPercent);
					if (UtilValidate.isNotEmpty(cessPercentStr)) {
						cessPercent = new BigDecimal(cessPercentStr);
					}
					Debug.log("cessPercent==============="+cessPercent);
					if (UtilValidate.isNotEmpty(vatPercentStr)) {
						vatPercent = new BigDecimal(vatPercentStr);
					}
					Debug.log("vatPercent==============="+vatPercent);
					if (UtilValidate.isNotEmpty(cstPercentStr)) {
						cstPercent = new BigDecimal(cstPercentStr);
					}
					Debug.log("cstPercent==============="+cstPercent);
					if (UtilValidate.isNotEmpty(bundleWeight)) {
						bundleWeight1 = new BigDecimal(bundleWeight);
					}
					Debug.log("bundleWeight1==============="+bundleWeight1);
					if (UtilValidate.isNotEmpty(baleQuantity)) {
						baleQuantity1 = new BigDecimal(baleQuantity);
					}
					Debug.log("baleQuantity1==============="+baleQuantity1);
					if (UtilValidate.isNotEmpty(packagingStr)) {
						packaging = new BigDecimal(packagingStr);
					}
					Debug.log("packaging==============="+packaging);
					if (UtilValidate.isNotEmpty(packetStr)) {
						packet = new BigDecimal(packetStr);
					}
					Debug.log("packet==============="+packet);
					
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing quantity string: "
							+ quantityStr, module);
					request.setAttribute("_ERROR_MESSAGE_",
							"Problems parsing quantity string: " + quantityStr);
					return "error";
				}

				productQtyMap.put("productId", productId);
				productQtyMap.put("quantity", quantity);
				productQtyMap.put("unitPrice", unitPrice);
				productQtyMap.put("remarks", remarksStr);
				productQtyMap.put("yarnUOM", yarnUOM);
				productQtyMap.put("bundleWeight", bundleWeight1);
				productQtyMap.put("baleQuantity", baleQuantity1);
				productQtyMap.put("unitListPrice", unitPrice);
				productQtyMap.put("bundleUnitPrice",bundleUnitPrice);
				productQtyMap.put("basicPrice", basicPrice);
				productQtyMap.put("bedPrice", bedPrice);
				productQtyMap.put("cessPrice", bedPrice);
				productQtyMap.put("cstPrice", cstPrice);
				productQtyMap.put("vatPrice", vatPrice);
				productQtyMap.put("bedPercent", bedPercent);
				productQtyMap.put("cessPercent", bedPercent);
				productQtyMap.put("vatPercent", vatPercent);
				productQtyMap.put("cstPercent", cstPercent);
				productQtyMap.put("orderItemSeqId", orderItemSeqId);
				productQtyMap.put("purTaxRateList", purTaxRateList);
				productQtyMap.put("orderAdjustmentList", orderAdjustmentList);
				productQtyMap.put("packaging", packaging);
				productQtyMap.put("packet", packet);
				
				itemDetail.add(productQtyMap);
			}//end of productQty check
		}
		
		Debug.log("itemDetail==============="+itemDetail);
		
		//end row count for loop
			/*if (paramMap.containsKey("paymentTermTypeId" + thisSuffix)) {
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
			*/
		//}
		
		/*for (int i = 0; i < rowCount; i++) {
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
		*/
		//getting productStoreId 
		//String productStoreId = (String) (in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
		String productStoreId = (String) request.getParameter("productStoreId");
	    
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("productQtyList", itemDetail);
		processOrderContext.put("orderTypeId", orderTypeId);
		processOrderContext.put("orderId", orderId);
		processOrderContext.put("purposeTypeId", purposeTypeId);
		processOrderContext.put("termsList", termsList);
		processOrderContext.put("partyId", partyId);
		processOrderContext.put("grandTotal", grandTotal);
		processOrderContext.put("otherTerms", otherTermDetail);
		processOrderContext.put("otherChargesList", otherChargesList);
		processOrderContext.put("adjustmentDetail", adjustmentDetail);
		processOrderContext.put("billFromPartyId", billFromPartyId);
		processOrderContext.put("shipToPartyId", shipToPartyId);
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
		processOrderContext.put("incTax", "Y");	
		processOrderContext.put("purchaseTitleTransferEnumId", purchaseTitleTransferEnumId);
		processOrderContext.put("purchaseTaxType", purchaseTaxType);
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
		//creating supplier product here
		try{
			Map suppProdResult = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(result.get("orderId"))){
				Map suppProdMap = FastMap.newInstance();
				suppProdMap.put("userLogin", userLogin);
				suppProdMap.put("orderId", result.get("orderId"));
				suppProdResult = dispatcher.runSync("createSupplierProductFromOrder",suppProdMap);
				if(ServiceUtil.isError(suppProdResult)){
					Debug.logError("Unable do create supplier product: " + ServiceUtil.getErrorMessage(suppProdResult), module);
					request.setAttribute("_ERROR_MESSAGE_", "Unable do create supplier product...! "+ServiceUtil.getErrorMessage(suppProdResult));
					return "error";
				}
			}
		}catch (Exception e1) {
			Debug.logError(e1, "Error in supplier product",module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable do create supplier product...! ");
			return "error";
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
		request.setAttribute("orderId", result.get("orderId")); 
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
	  	String purposeTypeId = (String) context.get("purposeTypeId");
	  	String billFromPartyId = (String) context.get("billFromPartyId");
	  	String shipToPartyId = (String) context.get("shipToPartyId");
		String issueToDeptId = (String) context.get("issueToDeptId");
	  	List<Map> termsList = (List)context.get("termsList");
		List<Map> otherChargesList = (List) context.get("otherChargesList");
	  	List<Map> otherChargesAdjustment = (List)context.get("adjustmentDetail");
	  	List<Map> otherTermDetail = (List)context.get("otherTerms");
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
		String purchaseTitleTransferEnumId = (String) context.get("purchaseTitleTransferEnumId");
		String purchaseTaxType = (String) context.get("purchaseTaxType");
		String billToPartyId="Company";
		String orderTypeId = (String)context.get("orderTypeId");
		List<Map<String, Object>> poItemSeqProductList = FastList.newInstance();
		int itemIndex=productQtyList.size();
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
			cart.setBillToCustomerPartyId(billFromPartyId);
			cart.setPlacingCustomerPartyId(billFromPartyId);
			//cart.setShipToCustomerPartyId(shipToPartyId);
			cart.setEndUserCustomerPartyId(billFromPartyId);
			cart.setFacilityId(shipToPartyId);
			//cart.setShipmentId(shipmentId);
			//for PurchaseOrder we have to use for SupplierId
			cart.setBillFromVendorPartyId(partyId);
		    cart.setShipFromVendorPartyId(partyId);
		    cart.setSupplierAgentPartyId(partyId);
		    if(UtilValidate.isNotEmpty(purposeTypeId)){
		    	cart.setPurposeTypeId(purposeTypeId);
		    }
		    else{
		    	cart.setPurposeTypeId("DEPOT_PURCHASE");
		    }
			cart.setEstimatedDeliveryDate(estimatedDeliveryDate);
			cart.setOrderName(orderName);
			//cart.setOrderDate(effectiveDate);
			cart.setOrderDate(orderDate);
			cart.setUserLogin(userLogin, dispatcher);
			
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
			if(UtilValidate.isNotEmpty(purchaseTitleTransferEnumId)){
				cart.setOrderAttribute("purchaseTitleTransferEnumId",purchaseTitleTransferEnumId);
			}
			if(UtilValidate.isNotEmpty(purchaseTaxType)){
				cart.setOrderAttribute("purchaseTaxType",purchaseTaxType);
			}
			
		} catch (Exception e) {
			
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}

		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			String productId = "";
			int count = 0;
			BigDecimal totalBasicAmount = BigDecimal.ZERO;
			
			
			for (Map<String, Object> prodQtyMap : productQtyList) {
				List taxList=FastList.newInstance();
				String istemSeq=String.format("%05d", itemIndex);
				Map productQtyMap = FastMap.newInstance();
				
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal unitPrice = BigDecimal.ZERO;
				BigDecimal unitListPrice = BigDecimal.ZERO;
				BigDecimal bundleUnitPrice = BigDecimal.ZERO;
			    BigDecimal bundleWeight = BigDecimal.ZERO;
			    BigDecimal baleQty = BigDecimal.ZERO;
			    BigDecimal packaging = BigDecimal.ZERO;
			    BigDecimal packet = BigDecimal.ZERO;
				String remarks = "";
				String yarnUOM= "";
				BigDecimal vatPercent = BigDecimal.ZERO;
				BigDecimal vatAmount = BigDecimal.ZERO;
				//BigDecimal cstAmount = BigDecimal.ZERO;
				BigDecimal cstAmount = BigDecimal.ZERO;
				BigDecimal cstPercent = BigDecimal.ZERO;
				BigDecimal cessPercent = BigDecimal.ZERO;
				String orderItemSeqId = null;
				/*BigDecimal bedAmount = BigDecimal.ZERO;
				BigDecimal bedPercent = BigDecimal.ZERO;
				BigDecimal bedcessPercent = BigDecimal.ZERO;
				BigDecimal bedcessAmount = BigDecimal.ZERO;
				BigDecimal bedseccessAmount = BigDecimal.ZERO;
				BigDecimal bedseccessPercent = BigDecimal.ZERO;*/
				
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
					productId = (String)prodQtyMap.get("productId");
				}
				Debug.log("productId============"+productId);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
					quantity = (BigDecimal)prodQtyMap.get("quantity");
				}
				Debug.log("quantity============"+quantity);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
					unitPrice = (BigDecimal)prodQtyMap.get("unitPrice");
				}
				Debug.log("unitPrice============"+unitPrice);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
					unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
				}
				Debug.log("unitListPrice============"+unitListPrice);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("remarks"))){
					remarks = (String)prodQtyMap.get("remarks");
				}
				Debug.log("remarks============"+remarks);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("yarnUOM"))){
					yarnUOM = (String)prodQtyMap.get("yarnUOM");
				}
				Debug.log("yarnUOM============"+yarnUOM);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bundleUnitPrice"))){
					bundleUnitPrice = (BigDecimal)prodQtyMap.get("bundleUnitPrice");
				}
				Debug.log("bundleUnitPrice============"+bundleUnitPrice);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bundleWeight"))){
					bundleWeight = (BigDecimal)prodQtyMap.get("bundleWeight");
				}
				Debug.log("bundleWeight============"+bundleWeight);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("baleQuantity"))){
					baleQty = (BigDecimal)prodQtyMap.get("baleQuantity");
				}
				Debug.log("baleQty============"+baleQty);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cessPercent"))){
					cessPercent = (BigDecimal)prodQtyMap.get("cessPercent");
				}
				Debug.log("cessPercent============"+cessPercent);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
					vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
				}
				Debug.log("vatAmount============"+vatAmount);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
					vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
				}
				Debug.log("vatPercent============"+vatPercent);
				/*if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
					cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
				}*/
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
					cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
				}
				Debug.log("cstPercent============"+cstPercent);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("packaging"))){
					packaging = (BigDecimal)prodQtyMap.get("packaging");
				}
				Debug.log("packaging============"+packaging);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("packet"))){
					packet = (BigDecimal)prodQtyMap.get("packet");
				}
				Debug.log("packet============"+packet);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("orderItemSeqId"))){
					orderItemSeqId = (String)prodQtyMap.get("orderItemSeqId");
				}
				Debug.log("orderItemSeqId============"+orderItemSeqId);
				/*if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
					bedAmount = (BigDecimal)prodQtyMap.get("bedAmount");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedPercent"))){
					bedPercent = (BigDecimal)prodQtyMap.get("bedPercent");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessAmount"))){
					bedcessAmount = (BigDecimal)prodQtyMap.get("bedcessAmount");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessPercent"))){
					bedcessPercent = (BigDecimal)prodQtyMap.get("bedcessPercent");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessAmount"))){
					bedseccessAmount = (BigDecimal)prodQtyMap.get("bedseccessAmount");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessPercent"))){
					bedseccessPercent = (BigDecimal)prodQtyMap.get("bedseccessPercent");
				}
               */
				List purTaxRateList = FastList.newInstance();
				if(UtilValidate.isNotEmpty(prodQtyMap.get("purTaxRateList"))){
					purTaxRateList = (List)prodQtyMap.get("purTaxRateList");
				}
				Debug.log("purTaxRateList========"+purTaxRateList);
				List orderAdjustmentList = FastList.newInstance();
				if(UtilValidate.isNotEmpty(prodQtyMap.get("orderAdjustmentList"))){
					orderAdjustmentList = (List)prodQtyMap.get("orderAdjustmentList");
				}
				Debug.log("orderAdjustmentList========"+orderAdjustmentList);
				BigDecimal basePrice = BigDecimal.ZERO;
				BigDecimal itemAmount = quantity.multiply(unitPrice);
				
				basePrice = itemAmount;
				if(cessPercent.compareTo(BigDecimal.ZERO)>0){
					BigDecimal cessAmount = (itemAmount.multiply(cessPercent)).divide(new BigDecimal("100"));
					basePrice = itemAmount.add(cessAmount);
					
					Map taxDetailMap = FastMap.newInstance();
	        		taxDetailMap.put("orderAdjustmentTypeId","CESS_PUR");
	        		taxDetailMap.put("sourcePercentage",cessPercent);
	        		taxDetailMap.put("amount",cessAmount);
					//taxDetailMap.put("taxAuthGeoId", partyGeoId);
		    		taxList.add(taxDetailMap);
				}
				if(vatPercent.compareTo(BigDecimal.ZERO)>0){
	        		
	        		vatAmount = ((basePrice).multiply(vatPercent)).divide(new BigDecimal("100"));
	        		
	        		Map taxDetailMap = FastMap.newInstance();
	        		taxDetailMap.put("orderAdjustmentTypeId","VAT_PUR");
	        		taxDetailMap.put("sourcePercentage",vatPercent);
	        		taxDetailMap.put("amount",vatAmount);
					//taxDetailMap.put("taxAuthGeoId", partyGeoId);
		    		taxList.add(taxDetailMap);
				}
				
				if(cstPercent.compareTo(BigDecimal.ZERO)>0){
					
					cstAmount = ((basePrice).multiply(cstPercent)).divide(new BigDecimal("100"));
					
	        		Map taxDetailMap = FastMap.newInstance();
	        		taxDetailMap.put("orderAdjustmentTypeId","CST_PUR");
	        		taxDetailMap.put("sourcePercentage",cstPercent);
	        		taxDetailMap.put("amount",cstAmount);
	        		taxDetailMap.put("taxAuthGeoId", "IND");
		    		
		    		taxList.add(taxDetailMap);
				}
				Debug.log("taxList========"+taxList);
				
				/*productQtyMap.put("productId", productId);
				productQtyMap.put("customerId", partyId);
				productQtyMap.put("quantity", quantity);
				productQtyMap.put("basicPrice", unitPrice);
				productQtyMap.put("bundleUnitPrice", bundleUnitPrice);
				productQtyMap.put("bundleWeight", bundleWeight);
				productQtyMap.put("baleQuantity", baleQty);
				productQtyMap.put("yarnUOM", yarnUOM);
				productQtyMap.put("remarks", remarks);
				productQtyMap.put("orderItemSeqId",istemSeq);
				poItemSeqProductList.add(productQtyMap);
				itemIndex=itemIndex-1;*/
				/*if(bedAmount.compareTo(BigDecimal.ZERO)>0){
	        		Map taxDetailMap = FastMap.newInstance();
		    		taxDetailMap.put("taxType", "BED_PUR");
		    		taxDetailMap.put("amount", bedAmount);
		    		taxDetailMap.put("percentage", bedPercent);
		    		taxList.add(taxDetailMap);
				}
				if(vatAmount.compareTo(BigDecimal.ZERO)>0){
	        		Map taxDetailMap = FastMap.newInstance();
		    		taxDetailMap.put("taxType", "VAT_PUR");
		    		taxDetailMap.put("amount", vatAmount);
		    		taxDetailMap.put("percentage", vatPercent);
		    		taxList.add(taxDetailMap);
				}
				if(cstAmount.compareTo(BigDecimal.ZERO)>0){
	        		Map taxDetailMap = FastMap.newInstance();
		    		taxDetailMap.put("taxType", "CST_PUR");
		    		taxDetailMap.put("amount", cstAmount);
		    		taxDetailMap.put("percentage", cstPercent);
		    		taxList.add(taxDetailMap);
				}
				if(bedcessAmount.compareTo(BigDecimal.ZERO)>0){
	        		Map taxDetailMap = FastMap.newInstance();
		    		taxDetailMap.put("taxType", "BEDCESS_PUR");
		    		taxDetailMap.put("amount", bedcessAmount);
		    		taxDetailMap.put("percentage", bedcessPercent);
		    		taxList.add(taxDetailMap);
				}
				if(bedseccessAmount.compareTo(BigDecimal.ZERO)>0){
	        		Map taxDetailMap = FastMap.newInstance();
		    		taxDetailMap.put("taxType", "BEDSECCESS_PUR");
		    		taxDetailMap.put("amount", bedseccessAmount);
		    		taxDetailMap.put("percentage", bedseccessPercent);
		    		taxList.add(taxDetailMap);
				}
				*/
				ShoppingCartItem item = null;
				try{
					int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null, quantity, unitPrice,
				            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
				            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
				
					item = cart.findCartItem(itemIndx);
					item.setOrderItemAttribute("remarks",remarks);
					item.setOrderItemAttribute("packQuantity",packaging.toString());
					item.setOrderItemAttribute("packets",packet.toString());
					item.setListPrice(unitListPrice);
					item.setTaxDetails(taxList);
					count++;
					totalBasicAmount = totalBasicAmount.add(quantity.multiply(unitPrice));
					for(int i=0; i<taxList.size(); i++){
						Map taxMap = (Map) taxList.get(i);
						if(  ((BigDecimal) taxMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
							GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxMap);
			 				item.addAdjustment(orderAdjustment);
							
			 				unitListPrice.add((BigDecimal) taxMap.get("amount"));
						}
					}
					Debug.log("unitListPrice=============="+unitListPrice);
					for(int i=0; i<purTaxRateList.size(); i++){
						Map taxMap = (Map) purTaxRateList.get(i);
						if(  ((BigDecimal) taxMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
							GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxMap);
							item.addAdjustment(orderAdjustment);
							
							unitListPrice.add((BigDecimal) taxMap.get("amount"));
						}
					}
					
					for(int i=0; i<orderAdjustmentList.size(); i++){
						Map adjMap = (Map) orderAdjustmentList.get(i);
						if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
							GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", adjMap);
							item.addAdjustment(orderAdjustment);
							
							unitListPrice.add((BigDecimal) adjMap.get("amount"));
						}if(adjMap.get("orderAdjustmentTypeId").equals("PRICE_DISCOUNT")){
							
							GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", adjMap);
							item.addAdjustment(orderAdjustment);
							
							unitListPrice.add((BigDecimal) adjMap.get("amount"));
						}
					}
					
					Debug.log("unitListPrice=============="+unitListPrice);
					
					item.setListPrice(unitListPrice);	    		
				}
				catch (Exception exc) {
					Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
					return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: ");
		        }
			
			}
			cart.setDefaultCheckoutOptions(dispatcher);
			//ProductPromoWorker.doPromotions(cart, dispatcher);
			CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
		
			Map<String, Object> orderCreateResult=checkout.createOrder(userLogin);
			if (ServiceUtil.isError(orderCreateResult)) {
				String errMsg =  ServiceUtil.getErrorMessage(orderCreateResult);
				Debug.logError(errMsg, "While Creating Order",module);
				return ServiceUtil.returnError(" Error While Creating Order !"+errMsg);
			}
		
			orderId = (String) orderCreateResult.get("orderId");
			
			Debug.log("orderId================="+orderId);
			
			Debug.log("productQtyList================="+productQtyList);
			
			int orderItemDetailSeq = 1;
			//populating orderItem Detail
			for (Map<String, Object> prodQtyMap : productQtyList) {
				List taxList=FastList.newInstance();
				String orderItemSeq=String.format("%05d", orderItemDetailSeq);
				Map productQtyMap = FastMap.newInstance();
				
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal unitPrice = BigDecimal.ZERO;
				BigDecimal unitListPrice = BigDecimal.ZERO;
				BigDecimal bundleUnitPrice = BigDecimal.ZERO;
			    BigDecimal bundleWeight = BigDecimal.ZERO;
			    BigDecimal baleQuantity = BigDecimal.ZERO;
				String remarks = "";
				String yarnUOM= "";
				BigDecimal vatPercent = BigDecimal.ZERO;
				BigDecimal vatAmount = BigDecimal.ZERO;
				//BigDecimal cstAmount = BigDecimal.ZERO;
				BigDecimal cstAmount = BigDecimal.ZERO;
				BigDecimal cstPercent = BigDecimal.ZERO;
				BigDecimal cessPercent = BigDecimal.ZERO;
				String orderItemSeqId = null;
				/*BigDecimal bedAmount = BigDecimal.ZERO;
				BigDecimal bedPercent = BigDecimal.ZERO;
				BigDecimal bedcessPercent = BigDecimal.ZERO;
				BigDecimal bedcessAmount = BigDecimal.ZERO;
				BigDecimal bedseccessAmount = BigDecimal.ZERO;
				BigDecimal bedseccessPercent = BigDecimal.ZERO;*/
				
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
					productId = (String)prodQtyMap.get("productId");
				}
				Debug.log("productId===================="+productId);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
					quantity = (BigDecimal)prodQtyMap.get("quantity");
				}
				Debug.log("quantity===================="+quantity);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
					unitPrice = (BigDecimal)prodQtyMap.get("unitPrice");
				}
				Debug.log("unitPrice===================="+unitPrice);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
					unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
				}
				Debug.log("unitListPrice===================="+unitListPrice);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("remarks"))){
					remarks = (String)prodQtyMap.get("remarks");
				}
				Debug.log("remarks===================="+remarks);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("yarnUOM"))){
					yarnUOM = (String)prodQtyMap.get("yarnUOM");
				}
				Debug.log("yarnUOM===================="+yarnUOM);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bundleUnitPrice"))){
					bundleUnitPrice = (BigDecimal)prodQtyMap.get("bundleUnitPrice");
				}
				Debug.log("bundleUnitPrice===================="+bundleUnitPrice);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bundleWeight"))){
					bundleWeight = (BigDecimal)prodQtyMap.get("bundleWeight");
				}
				Debug.log("bundleWeight===================="+bundleWeight);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("baleQuantity"))){
					baleQuantity = (BigDecimal)prodQtyMap.get("baleQuantity");
				}
				Debug.log("baleQuantity===================="+baleQuantity);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cessPercent"))){
					cessPercent = (BigDecimal)prodQtyMap.get("cessPercent");
				}
				Debug.log("cessPercent===================="+cessPercent);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
					vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
				}
				Debug.log("vatAmount===================="+vatAmount);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
					vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
				}
				Debug.log("vatPercent===================="+vatPercent);
				/*if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
					cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
				}*/
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
					cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
				}
				Debug.log("cstPercent===================="+cstPercent);
				if(UtilValidate.isNotEmpty(prodQtyMap.get("orderItemSeqId"))){
					orderItemSeqId = (String)prodQtyMap.get("orderItemSeqId");
				}
				Debug.log("orderItemSeqId===================="+orderItemSeqId);
	        	Map<String, Object> orderItemDetail = FastMap.newInstance();
				//String orderItemSeqId="";
				//orderItemSeqId=(String)prodItemMap.get("orderItemSeqId");
				BigDecimal quotaQuantity = BigDecimal.ZERO;
				BigDecimal discountAmount = BigDecimal.ZERO;
				orderItemDetail.put("orderId",orderId);
				Debug.log("orderId===================="+orderId);
				orderItemDetail.put("orderItemSeqId",orderItemSeq);
				Debug.log("orderItemSeq===================="+orderItemSeq);
				orderItemDetail.put("userLogin",userLogin);
				orderItemDetail.put("partyId",partyId);
				Debug.log("partyId===================="+partyId);
				if(UtilValidate.isNotEmpty(unitPrice))
				orderItemDetail.put("unitPrice",unitPrice);
				
				Debug.log("unitPrice===================="+unitPrice);
				//if(UtilValidate.isNotEmpty(discountAmount))
				
				orderItemDetail.put("discountAmount",discountAmount);
				
				Debug.log("discountAmount===================="+discountAmount);
				
				orderItemDetail.put("Uom",yarnUOM);
				
				Debug.log("yarnUOM===================="+yarnUOM);
				orderItemDetail.put("productId",productId);
				 
				Debug.log("productId===================="+productId);
				if(UtilValidate.isNotEmpty(baleQuantity))
				orderItemDetail.put("baleQuantity",baleQuantity);
				
				Debug.log("baleQuantity===================="+baleQuantity);
				if(UtilValidate.isNotEmpty(bundleWeight))
				orderItemDetail.put("bundleWeight",bundleWeight);
				  
				Debug.log("bundleWeight===================="+bundleWeight);
				if(UtilValidate.isNotEmpty(bundleUnitPrice))
				orderItemDetail.put("bundleUnitPrice",bundleUnitPrice);
				
				Debug.log("bundleUnitPrice===================="+bundleUnitPrice);
				orderItemDetail.put("remarks",remarks);
				
				Debug.log("remarks===================="+remarks);
				//if(UtilValidate.isNotEmpty(quotaQuantity))
				orderItemDetail.put("quotaQuantity",quotaQuantity);
				
				Debug.log("quotaQuantity===================="+quotaQuantity);
				if(UtilValidate.isNotEmpty(quantity))
				orderItemDetail.put("quantity",quantity);
				
				Debug.log("quantity===================="+quantity);
				orderItemDetail.put("changeUserLogin",userLogin.getString("userLoginId"));
	
				try{
					Map resultMap = dispatcher.runSync("createOrderItemDetail",orderItemDetail);
			        
			       /* if (ServiceUtil.isError(resultMap)) {
			        	Debug.logError("Problem creating order Item  change for orderId :"+orderId, module);
			        	return ServiceUtil.returnError("Problem creating order Item  Detail for orderId :"+orderId);	
			        }*/
				}catch(Exception e){
			  		Debug.logError(e, "Error in Order Item Detail, module");
			  		return ServiceUtil.returnError( "Error in Order Item Detail");
			  	}
				
				orderItemDetailSeq++;
				
				
			}

			//end populating orderItem Detail
			
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("orderItemSeqId", "productId"), null, null, false);
			for(Map eachTermMap : termsList){
				Map termCreateCtx = FastMap.newInstance();
				termCreateCtx.put("userLogin", userLogin);
				termCreateCtx.put("orderId", orderId);
				termCreateCtx.put("termTypeId", (String)eachTermMap.get("termTypeId"));
				termCreateCtx.put("description", (String)eachTermMap.get("description"));
				Map orderTermResult = dispatcher.runSync("createOrderTerm",termCreateCtx);
				if (ServiceUtil.isError(orderTermResult)) {
					String errMsg =  ServiceUtil.getErrorMessage(orderTermResult);
					Debug.logError(errMsg, "While Creating Order Payment/Delivery Term",module);
					return ServiceUtil.returnError(" Error While Creating Order Payment/Delivery Term !"+errMsg);
				}
			}
			Debug.log("orderItems=========444==========="+orderItems);
			for(Map eachTermItem : otherTermDetail){
				
				String termId = (String)eachTermItem.get("otherTermId");
				String applicableTo = (String)eachTermItem.get("applicableTo");
				if(!applicableTo.equals("ALL")){
					List<GenericValue> sequenceItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, applicableTo));
					if(UtilValidate.isNotEmpty(sequenceItems)){
						GenericValue sequenceItem = EntityUtil.getFirst(sequenceItems);
						applicableTo = sequenceItem.getString("orderItemSeqId");
					}
				}
				else{
					applicableTo = "_NA_";
				}
				
				
				Map termCreateCtx = FastMap.newInstance();
				termCreateCtx.put("userLogin", userLogin);
				termCreateCtx.put("orderId", orderId);
				termCreateCtx.put("orderItemSeqId", applicableTo);
				termCreateCtx.put("termTypeId", (String)eachTermItem.get("termTypeId"));
				termCreateCtx.put("termValue", (BigDecimal)eachTermItem.get("termValue"));
				termCreateCtx.put("termDays", null);
				if(UtilValidate.isNotEmpty(eachTermItem.get("termDays"))){
					termCreateCtx.put("termDays", ((BigDecimal)eachTermItem.get("termDays")).longValue());
				}
				
				termCreateCtx.put("uomId", (String)eachTermItem.get("uomId"));
				termCreateCtx.put("description", (String)eachTermItem.get("description"));
				
				Map orderTermResult = dispatcher.runSync("createOrderTerm",termCreateCtx);
				if (ServiceUtil.isError(orderTermResult)) {
					String errMsg =  ServiceUtil.getErrorMessage(orderTermResult);
					Debug.logError(errMsg, "While Creating Order Adjustment Term",module);
					return ServiceUtil.returnError(" Error While Creating Order Adjustment Term !"+errMsg);
				}
					
			}
			
			Debug.log("otherTermDetail=========444==========="+otherTermDetail);
			/*for(Map eachAdj : otherChargesAdjustment){
				
				String adjustmentTypeId = (String)eachAdj.get("adjustmentTypeId");
				String applicableTo = (String)eachAdj.get("applicableTo");
				if(!applicableTo.equals("_NA_")){
					List<GenericValue> sequenceItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, applicableTo));
					if(UtilValidate.isNotEmpty(sequenceItems)){
						GenericValue sequenceItem = EntityUtil.getFirst(sequenceItems);
						applicableTo = sequenceItem.getString("orderItemSeqId");
					}
				}
				BigDecimal amount =(BigDecimal)eachAdj.get("amount");
				Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);
				adjustCtx.put("orderId", orderId);
		    	adjustCtx.put("orderItemSeqId", applicableTo);
		    	adjustCtx.put("orderAdjustmentTypeId", adjustmentTypeId);
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
			for(Map chargesMap : otherChargesList){

				Map chgDetailMap = FastMap.newInstance();
				
				BigDecimal adjustmentValue = (BigDecimal) chargesMap.get("adjustmentValue");
				BigDecimal adjAmt = (BigDecimal) chargesMap.get("adjustmentValue");
				if(UtilValidate.isNotEmpty(chargesMap.get("uomId")) && chargesMap.get("uomId").equals("PERCENT")){
					chgDetailMap.put("sourcePercentage",adjustmentValue);
					adjAmt = (adjustmentValue.divide(new BigDecimal("100"))).multiply(totalBasicAmount);
				}
				chgDetailMap.put("orderId",orderId);
				chgDetailMap.put("orderAdjustmentTypeId",chargesMap.get("otherTermId"));
				chgDetailMap.put("description",chargesMap.get("otherTermId"));
				chgDetailMap.put("amount",adjAmt);
				chgDetailMap.put("userLogin",userLogin);
				//chgDetailMap.put("taxAuthGeoId", "IND");
				Map adjResultMap=FastMap.newInstance();
				try{
		  	 		adjResultMap = dispatcher.runSync("createOrderAdjustment",chgDetailMap);  		  		 
		  	 		if (ServiceUtil.isError(adjResultMap)) {
		  	 			String errMsg =  ServiceUtil.getErrorMessage(adjResultMap);
		  	 			Debug.logError(errMsg , module);
		  	 			return ServiceUtil.returnError(" Error While Creating Adjustment for Purchase Order !");
		  	 		}
		  	 	}catch (Exception e) {
		  	 		Debug.logError(e, "Error While Creating Adjustment for Purchase Order ", module);
			  		return adjResultMap;			  
			  	}
				otherChargesList.remove(chargesMap);
			}*/
			Map resetTotalCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			resetTotalCtx.put("orderId", orderId);
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
			Debug.log("issueToDeptId=========444==========="+issueToDeptId);
			//update PurposeType
			/*try{
			GenericValue orderHeaderPurpose = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			orderHeaderPurpose.set("purposeTypeId", "DEPOT_PURCHASE");
			orderHeaderPurpose.store();
			}catch (Exception e) {
				  Debug.logError(e, "Error While Updating purposeTypeId for Order ", module);
				  return ServiceUtil.returnError("Error While Updating purposeTypeId for Order : "+orderId);
	  	 	}*/
	    
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
		Debug.log("orderId=========FINAL==========="+orderId);
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
		List<Map> otherTermDetail = (List)context.get("otherTerms");
	  	List<Map> termsList = (List)context.get("termsList");
	  	boolean beganTransaction = false;
	  	List<Map> otherChargesAdjustment = (List) context.get("adjustmentDetail");
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
			for(Map eachTermMap : termsList){
				Map termCreateCtx = FastMap.newInstance();
				termCreateCtx.put("userLogin", userLogin);
				termCreateCtx.put("orderId", orderId);
				termCreateCtx.put("termTypeId", (String)eachTermMap.get("termTypeId"));
				termCreateCtx.put("description", (String)eachTermMap.get("description"));
				Map orderTermResult = dispatcher.runSync("createOrderTerm",termCreateCtx);
				if (ServiceUtil.isError(orderTermResult)) {
					String errMsg =  ServiceUtil.getErrorMessage(orderTermResult);
					Debug.logError(errMsg, "While Creating Order Payment/Delivery Term",module);
					return ServiceUtil.returnError(" Error While Creating Order Payment/Delivery Term !"+errMsg);
				}
			}
			
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("orderItemSeqId", "productId"), null, null, false);
			
			for(Map eachTermItem : otherTermDetail){
				
				String termId = (String)eachTermItem.get("otherTermId");
				String applicableTo = (String)eachTermItem.get("applicableTo");
				if(!applicableTo.equals("ALL")){
					List<GenericValue> sequenceItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, applicableTo));
					if(UtilValidate.isNotEmpty(sequenceItems)){
						GenericValue sequenceItem = EntityUtil.getFirst(sequenceItems);
						applicableTo = sequenceItem.getString("orderItemSeqId");
					}
				}
				else{
					applicableTo = "_NA_";
				}
				
				Map termCreateCtx = FastMap.newInstance();
				termCreateCtx.put("userLogin", userLogin);
				termCreateCtx.put("orderId", orderId);
				termCreateCtx.put("orderItemSeqId", applicableTo);
				termCreateCtx.put("termTypeId", (String)eachTermItem.get("termTypeId"));
				termCreateCtx.put("termValue", (BigDecimal)eachTermItem.get("termValue"));
				termCreateCtx.put("termDays", null);
				if(UtilValidate.isNotEmpty(eachTermItem.get("termDays"))){
					termCreateCtx.put("termDays", ((BigDecimal)eachTermItem.get("termDays")).longValue());
				}
				
				termCreateCtx.put("uomId", (String)eachTermItem.get("uomId"));
				termCreateCtx.put("description", (String)eachTermItem.get("description"));
				
				Map orderTermResult = dispatcher.runSync("createOrderTerm",termCreateCtx);
				if (ServiceUtil.isError(orderTermResult)) {
					String errMsg =  ServiceUtil.getErrorMessage(orderTermResult);
					Debug.logError(errMsg, "While Creating Order Adjustment Term",module);
					return ServiceUtil.returnError(" Error While Creating Order Adjustment Term !"+errMsg);
				}
					
			}
			
		} catch (Exception e) {
			
			Debug.logError(e, "Error in updating order", module);
			return ServiceUtil.returnError("Error in updating order");
		}
		String productId = "";
		
		try{
			beganTransaction = TransactionUtil.begin(7200);
		
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			try{
				List<GenericValue> orderItemDetails = delegator.findList("OrderItemDetail", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
				if(UtilValidate.isNotEmpty(orderItemDetails)){
					delegator.removeAll(orderItemDetails);
				}
			}catch(GenericEntityException e){
					Debug.logError(e, "Failed to retrive orderItemDetail ", module);
			}
			BigDecimal totalBedAmount = BigDecimal.ZERO;
			BigDecimal totalBedCessAmount = BigDecimal.ZERO;
			BigDecimal totalBedSecCessAmount = BigDecimal.ZERO;
			BigDecimal totalVatAmount = BigDecimal.ZERO;
			BigDecimal totalCstAmount = BigDecimal.ZERO;
			if(UtilValidate.isEmpty(productQtyList)){
				return ServiceUtil.returnError("No Material exists in PO to edit");
			}
			GenericValue orderItemValue = null;
			for (Map<String, Object> prodQtyMap : productQtyList) {
				
				List taxList=FastList.newInstance();
				
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal unitPrice = BigDecimal.ZERO;
				BigDecimal unitListPrice = BigDecimal.ZERO;
				BigDecimal vatPercent = BigDecimal.ZERO;
				BigDecimal vatAmount = BigDecimal.ZERO;
				BigDecimal cstAmount = BigDecimal.ZERO;
				BigDecimal cstPercent = BigDecimal.ZERO;
				BigDecimal bedAmount = BigDecimal.ZERO;
				BigDecimal bedPercent = BigDecimal.ZERO;
				BigDecimal bedcessPercent = BigDecimal.ZERO;
				BigDecimal bedcessAmount = BigDecimal.ZERO;
				BigDecimal bedseccessAmount = BigDecimal.ZERO;
				BigDecimal bedseccessPercent = BigDecimal.ZERO;
				String orderItemSeqId="";
				String customerId = "";
				//BigDecimal basicPrice = BigDecimal.ZERO;
				String prodId="";
				BigDecimal budlWeight=BigDecimal.ZERO;
				BigDecimal budlUnitPrice=BigDecimal.ZERO;
				BigDecimal blQuantity=BigDecimal.ZERO;
				BigDecimal Kgquantity=BigDecimal.ZERO;
				BigDecimal prdPrice=BigDecimal.ZERO;
				String Uom="";
				String specification="";
				
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
					productId = (String)prodQtyMap.get("productId");
				}
				List<GenericValue> orderItem =null;

				if(UtilValidate.isNotEmpty(prodQtyMap.get("orderItemSeqId"))){
					orderItemSeqId = (String)prodQtyMap.get("orderItemSeqId");
					 orderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				}else{
					orderItem=EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));

				}
				
				if(UtilValidate.isNotEmpty(orderItem)){
					orderItemValue = EntityUtil.getFirst(orderItem);
				}
				else{
					continue;
				}
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
					quantity = (BigDecimal)prodQtyMap.get("quantity");
					orderItemValue.put("quantity", quantity);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
					unitPrice = (BigDecimal)prodQtyMap.get("unitPrice");
					orderItemValue.put("unitPrice", unitPrice);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
					unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
					orderItemValue.put("unitListPrice", unitListPrice);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
					vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
					orderItemValue.put("vatAmount", vatAmount);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
					vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
					orderItemValue.put("vatPercent", vatPercent);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
					cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
					orderItemValue.put("cstAmount", cstAmount);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
					cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
					orderItemValue.put("cstPercent", cstPercent);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
					bedAmount = (BigDecimal)prodQtyMap.get("bedAmount");
					orderItemValue.put("bedAmount", bedAmount);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedPercent"))){
					bedPercent = (BigDecimal)prodQtyMap.get("bedPercent");
					orderItemValue.put("bedPercent", bedPercent);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessAmount"))){
					bedcessAmount = (BigDecimal)prodQtyMap.get("bedcessAmount");
					orderItemValue.put("bedcessAmount", bedcessAmount);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessPercent"))){
					bedcessPercent = (BigDecimal)prodQtyMap.get("bedcessPercent");
					orderItemValue.put("bedcessPercent", bedcessPercent);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessAmount"))){
					bedseccessAmount = (BigDecimal)prodQtyMap.get("bedseccessAmount");
					orderItemValue.put("bedseccessAmount", bedseccessAmount);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessPercent"))){
					bedseccessPercent = (BigDecimal)prodQtyMap.get("bedseccessPercent");
					orderItemValue.put("bedseccessPercent", bedseccessPercent);
				}

				orderItemValue.set("changeByUserLoginId", userLogin.getString("userLoginId"));
				orderItemValue.set("changeDatetime", UtilDateTime.nowTimestamp());
				orderItemValue.store();
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("remarks"))){
					specification = (String)prodQtyMap.get("remarks");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("baleQty"))){
					blQuantity =(BigDecimal)  prodQtyMap.get("baleQty");
				}
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bundleUnitPrice"))){
					budlUnitPrice = (BigDecimal)(prodQtyMap.get("bundleUnitPrice"));
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
					Kgquantity =  (BigDecimal)(prodQtyMap.get("quantity"));
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("yarnUOM"))){
					Uom = (String)prodQtyMap.get("yarnUOM");
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("bundleWeight"))){
					budlWeight =  (BigDecimal)prodQtyMap.get("bundleWeight");
				}
				
				if(UtilValidate.isNotEmpty(specification)){
					GenericValue orderItemAttr = delegator.makeValue("OrderItemAttribute");
					orderItemAttr.set("orderId", orderId);
					orderItemAttr.set("orderItemSeqId",orderItemSeqId);
					orderItemAttr.set("attrName", "remarks");
					orderItemAttr.set("attrValue", specification);
					delegator.createOrStore(orderItemAttr);
				}
	        	Map<String, Object> orderItemDetail = FastMap.newInstance();
				orderItemDetail.put("orderId",orderId);
				orderItemDetail.put("orderItemSeqId",orderItemSeqId);
				orderItemDetail.put("userLogin",userLogin);
				orderItemDetail.put("partyId",billFromPartyId);
				orderItemDetail.put("unitPrice",unitPrice);
				orderItemDetail.put("Uom",Uom);
				orderItemDetail.put("productId",productId);
				orderItemDetail.put("baleQuantity",blQuantity);
				orderItemDetail.put("bundleWeight",budlWeight);
				orderItemDetail.put("bundleUnitPrice",budlUnitPrice);
				orderItemDetail.put("remarks",specification);
				orderItemDetail.put("quotaQuantity",BigDecimal.ZERO);
				orderItemDetail.put("quantity",Kgquantity);
				orderItemDetail.put("changeUserLogin",userLogin.getString("userLoginId"));
	
				try{
					Map resultMap = dispatcher.runSync("createOrderItemDetail",orderItemDetail);
			        if (ServiceUtil.isError(resultMap)) {
			        	Debug.logError("Problem creating order Item  change for orderId :"+orderId, module);
			        	return ServiceUtil.returnError("Problem creating order Item  Detail for orderId :"+orderId);	
			        }
				}catch(Exception e){
			  		Debug.logError(e, "Error in Order Item Detail, module");
			  		return ServiceUtil.returnError( "Error in Order Item Detail");
			  	}
				
			}
			
			List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			delegator.removeAll(orderAdjustments);
			
			for(Map eachAdj : otherChargesAdjustment){
				
				String adjustmentTypeId = (String)eachAdj.get("adjustmentTypeId");
				String applicableTo = (String)eachAdj.get("applicableTo");
				if(!applicableTo.equals("_NA_")){
					List<GenericValue> sequenceItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, applicableTo));
					if(UtilValidate.isNotEmpty(sequenceItems)){
						GenericValue sequenceItem = EntityUtil.getFirst(sequenceItems);
						applicableTo = sequenceItem.getString("orderItemSeqId");
					}
				}
				BigDecimal amount =(BigDecimal)eachAdj.get("amount");
				Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);
				adjustCtx.put("orderId", orderId);
		    	adjustCtx.put("orderItemSeqId", applicableTo);
		    	adjustCtx.put("orderAdjustmentTypeId", adjustmentTypeId);
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
			List condList = FastList.newInstance();
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
			String orderId = shipmentReceipt.getString("orderId");
			String orderItemSeqId = shipmentReceipt.getString("orderItemSeqId");
			condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
			List<GenericValue> orderItemDetails = delegator.findList("OrderItemDetail", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
			GenericValue orderItemDets = EntityUtil.getFirst(orderItemDetails);
            String uom = orderItemDets.getString("Uom");
            BigDecimal bundleWeight =BigDecimal.ZERO;
            bundleWeight = orderItemDets.getBigDecimal("bundleWeight");
            BigDecimal bundleUnitPrice =BigDecimal.ZERO;
            bundleUnitPrice = orderItemDets.getBigDecimal("bundleUnitPrice");

			Map createInvDetail = FastMap.newInstance();
			createInvDetail.put("shipmentId", shipmentId);
			createInvDetail.put("shipmentItemSeqId", shipmentItemSeqId);
			createInvDetail.put("userLogin", userLogin);
			createInvDetail.put("inventoryItemId", inventoryItemId);
			createInvDetail.put("receiptId", receiptId);
			createInvDetail.put("uom", uom);
			createInvDetail.put("bundleWeight", bundleWeight);
			createInvDetail.put("bundleUnitPrice", bundleUnitPrice);
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
			
			//storing shipment receipt status Here 
			if(UtilValidate.isNotEmpty(receiptId)){
				GenericValue shipmentReceiptStatus = delegator.makeValue("ShipmentReceiptStatus");
				shipmentReceiptStatus.set("receiptId", receiptId);
				shipmentReceiptStatus.set("statusId", statusId);
				shipmentReceiptStatus.set("changedByUserLogin", userLogin.getString("userLoginId"));
				shipmentReceiptStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
				delegator.createSetNextSeqId(shipmentReceiptStatus);
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
	  	String primaryOrderId = "";
	  	
	  	String primaryPurCahseOrderId = "";
	  	
	    BigDecimal totalDiscount=BigDecimal.ZERO;
	    String indentId=null;
	    
	  	try{
	  		//
		  	for (int i = 0; i < rowCount; i++) {
		  		
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
					primaryOrderId=orderId;
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
				GenericValue orderItemAttr = null;
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
				List<GenericValue> OrderItem =null;
				try {
				List conList1 = FastList.newInstance();
				conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
				conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
	     		EntityCondition cond1=EntityCondition.makeCondition(conList1,EntityOperator.AND);
				 OrderItem = delegator.findList("OrderItem", cond1, null, null, null, false);
				}catch (GenericEntityException e) {
						Debug.logError("Failed to retrive OrderItem"+orderId, module);
						request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderItem");	
				  		return "error";
						
				}
				
				////Debug.log("OrderItem=============="+OrderItem);
				
				BigDecimal orderQuantity = (EntityUtil.getFirst(OrderItem)).getBigDecimal("quantity");
				
				////Debug.log("orderQuantity=============="+orderQuantity);
				
				if(!amendedQuantity.equals(orderQuantity)){
				
				List<GenericValue> orderAssoc =null;
				try {
					/*List conList2 = FastList.newInstance();
					conList2.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS ,orderId));
		     		EntityCondition cond2=EntityCondition.makeCondition(conList2,EntityOperator.AND);
					 orderAssoc = delegator.findList("OrderAssoc", cond2, null, null, null, false);
					 indentId=(EntityUtil.getFirst(orderAssoc)).getString("orderId");
				
					 primaryPurCahseOrderId = indentId;*/
					 
					 Map<String, Object> resultCtx = dispatcher.runSync("getAssociateOrder",UtilMisc.toMap("userLogin",userLogin, "orderId", orderId));
					 
					 if(UtilValidate.isNotEmpty(resultCtx)){
						 indentId = (String) resultCtx.get("orderId");
						 primaryPurCahseOrderId = indentId;
					 }
					 
					}catch (Exception e) {
							Debug.logError("Failed to retrive Indent"+orderId, module);
							request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive indent");	
					  		return "error";
							
					}
				
				GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",orderId,"orderItemSeqId",orderItemSeqId), false);
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), false);

				////Debug.log("orderItem=============="+orderItem);
				////Debug.log("orderHeader=============="+orderHeader);
				
				GenericValue indentItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",indentId,"orderItemSeqId",orderItemSeqId), false);
				GenericValue indentHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",indentId), false);

				////Debug.log("indentItem=============="+indentItem);
				////Debug.log("indentHeader=============="+indentHeader);
				////////Debug.log("indentId=============="+indentId);
				
				////////Debug.log("orderItemSeqId=============="+orderItemSeqId);
				
				List OrderItemDetailList =null;
				GenericValue OrderItemDetail =null;
				BigDecimal QuotaQty = BigDecimal.ZERO;
				try {
				List conList3 = FastList.newInstance();
				conList3.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
				conList3.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
	     		EntityCondition cond3=EntityCondition.makeCondition(conList3,EntityOperator.AND);
	     		OrderItemDetailList = delegator.findList("OrderItemDetail", cond3, null, null, null, false);
	     		
	     		////Debug.log("OrderItemDetailList=============="+OrderItemDetailList);
	     		
	     		OrderItemDetail = (EntityUtil.getFirst(OrderItemDetailList));
	     		
	     		 QuotaQty =  OrderItemDetail.getBigDecimal("quotaQuantity");
	     		
	     		////Debug.log("QuotaQty=============="+QuotaQty);
	     		
				}catch (GenericEntityException e) {
						Debug.logError("Failed to retrive OrderItemDetail"+indentId, module);
						request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderItem");	
				  		return "error";
						
				}
				
				
				//GenericValue orderItemChange = delegator.makeValue("OrderItemChange");
				Map<String, Object> orderItemChange = FastMap.newInstance();
				orderItemChange.put("userLogin", userLogin);
                //
				orderItemChange.put("quantity", orderItem.getBigDecimal("quantity"));
				orderItemChange.put("unitPrice", orderItem.getBigDecimal("unitPrice"));
				
				orderItemChange.put("changeTypeEnumId", "ODR_ITM_AMEND");
				orderItemChange.put("orderId", orderId);
				orderItemChange.put("orderItemSeqId", orderItemSeqId);
				orderItemChange.put("effectiveDatetime",effectiveDate);
				orderItemChange.put("changeDatetime", UtilDateTime.nowTimestamp());
				orderItemChange.put("changeUserLogin",userLogin.getString("userLoginId"));
				orderItemChange.put("reasonEnumId", reasonEnumId);
				orderItemChange.put("changeComments", changeComments);
				if(UtilValidate.isNotEmpty(orderId)){
					List conditionlist = FastList.newInstance();
					conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					conditionlist.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
					conditionlist.add(EntityCondition.makeCondition("changeTypeEnumId", EntityOperator.EQUALS, "ODR_ITM_AMEND"));
					EntityCondition conditionMain=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
					List<GenericValue> OrderItemChangeDetails = delegator.findList("OrderItemChange", conditionMain , null ,null, null, false );
					////Debug.log("OrderItemChangeDetails================="+OrderItemChangeDetails);
					 //OrderItemChangeDetails=EntityUtil.getFirst(OrderItemChangeDetails);
					if(UtilValidate.isEmpty(OrderItemChangeDetails)){
						orderItemChange.put("effectiveDatetime",orderHeader.getTimestamp("entryDate"));
						orderItemChange.put("changeDatetime", orderHeader.getTimestamp("entryDate"));
						Map resultorderPrvMap = dispatcher.runSync("createOrderItemChange",orderItemChange);
				        if (ServiceUtil.isError(resultorderPrvMap)) {
				        	Debug.logError("Problem creating order Item  old change for orderId :"+orderId, module);
							request.setAttribute("_ERROR_MESSAGE_", "Problem creating order Item old change for orderId :"+orderId);	
							TransactionUtil.rollback();
					  		return "error";
				        }
						orderItemChange.put("orderId", indentId);
				        orderItemChange.put("effectiveDatetime",indentHeader.getTimestamp("entryDate"));
						orderItemChange.put("changeDatetime", indentHeader.getTimestamp("entryDate"));
				        orderItemChange.put("quantity", indentItem.getBigDecimal("quantity"));
						orderItemChange.put("unitPrice", indentItem.getBigDecimal("unitPrice")); 
						Map resultIndentPrvMap = dispatcher.runSync("createOrderItemChange",orderItemChange);
				        if (ServiceUtil.isError(resultIndentPrvMap)) {
				        	Debug.logError("Problem creating order Item  old change for indentId :"+indentId, module);
							request.setAttribute("_ERROR_MESSAGE_", "Problem creating indet Item old change for indentId :"+indentId);	
							TransactionUtil.rollback();
					  		return "error";
				        }
					}
				}
				
				if(amendedQuantity.compareTo(BigDecimal.ZERO) !=0){
					orderItemChange.put("quantity", amendedQuantity);
					orderItem.set("quantity", amendedQuantity);
					indentItem.set("quantity", amendedQuantity);
					OrderItemDetail.set("quantity", amendedQuantity);
					

				}
				if(amendedPrice.compareTo(BigDecimal.ZERO) !=0){
					orderItemChange.put("unitPrice", amendedPrice);
					orderItem.set("unitPrice", amendedPrice);
					orderItem.set("unitListPrice", amendedPrice);
					indentItem.set("unitPrice", amendedPrice);
					indentItem.set("unitListPrice", amendedPrice);
					OrderItemDetail.set("unitPrice", amendedPrice);
				}
				orderItemChange.put("orderId", orderId);
				Map resultMap = dispatcher.runSync("createOrderItemChange",orderItemChange);
		        if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError("Problem creating order Item  change for orderId :"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem creating order Item  change for orderId :"+orderId);	
					TransactionUtil.rollback();
			  		return "error";
		        }
		        orderItemChange.put("orderId", indentId);
				Map resultIndentMap = dispatcher.runSync("createOrderItemChange",orderItemChange);
				if (ServiceUtil.isError(resultIndentMap)) {
		        	Debug.logError("Problem creating order Item  change for indentId :"+indentId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem creating order Item  change for indentId :"+indentId);	
					TransactionUtil.rollback();
			  		return "error";
		        }
				orderItem.store();
				indentItem.store();
				String Scheam = "";
				String onBeHalfOf = "";
			List<GenericValue> orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
				
			List<GenericValue> scheamList = EntityUtil.filterByCondition(orderAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
			
			List<GenericValue> onbehalfOf = EntityUtil.filterByCondition(orderAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "ON_BEHALF_OF"));
			if(UtilValidate.isNotEmpty(scheamList)){
				GenericValue orderScheme = EntityUtil.getFirst(scheamList);
				Scheam = (String) orderScheme.get("attrValue");
			}
			
			
			if(UtilValidate.isNotEmpty(onbehalfOf)){
				GenericValue onbeha = EntityUtil.getFirst(onbehalfOf);
				onBeHalfOf = (String) onbeha.get("attrValue");
			}
			
			  List condLIst = FastList.newInstance();
	    	   condLIst.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	    	   condLIst.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
				EntityCondition condExpr1 = EntityCondition.makeCondition(condLIst, EntityOperator.AND);
				List<GenericValue> orderRoles = delegator.findList("OrderRole", condExpr1, null, null, null, false);
			
				////Debug.log("orderRoles================="+orderRoles);
				
				GenericValue orderRolesList = EntityUtil.getFirst(orderRoles);

				String partyId = (String)orderRolesList.getString("partyId");
				
			
			List schemeCategoryIds = FastList.newInstance();
		  	try{
		  		List productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), UtilMisc.toSet("productCategoryId"), null, null, false);
		  		schemeCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);
		   	}catch (GenericEntityException e) {
				Debug.logError("Failed to retrive ProductCategory"+orderId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive ProductCategory");	
		  		return "error";
			}
			
			// Scheme Calculation
			List productCategoriesList = FastList.newInstance();
			List condsList = FastList.newInstance();
		  	condsList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		  	condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, schemeCategoryIds));
		  	/*condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
		  	condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));*/
			try {
				List<GenericValue> prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(condsList,EntityOperator.AND), UtilMisc.toSet("productCategoryId"), null, null, true);
				productCategoriesList = EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productCategoryId", true);
			} catch (GenericEntityException e) {
				Debug.logError("Failed to retrive ProductCategory"+orderId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive ProductCategory");	
		  		return "error";
				
			}
			
			//////Debug.log("prodCategoryMembers================="+prodCategoryMembers);
			
			//BigDecimal balanceQuota = BigDecimal.ZERO;
			// Get first productCategoriesList. We got productCategoryId here
			BigDecimal actualQty = BigDecimal.ZERO;
			
			BigDecimal updateQuta = BigDecimal.ZERO;
			
			GenericValue OrderItemList = EntityUtil.getFirst(OrderItem);

			actualQty = (BigDecimal) OrderItemList.get("quantity");

			BigDecimal partyBalanceQuota = BigDecimal.ZERO;
			
			updateQuta = amendedQuantity.subtract(actualQty);
			
			int updateQuotaSign = updateQuta.signum();
			
			////Debug.log("updateQuotaSign=====Used====3333==============="+updateQuotaSign);
			
			if(Scheam.equals("MGPS_10Pecent") && onBeHalfOf.equals("N") && updateQuotaSign == 1){
				String schemeId="TEN_PERCENT_MGPS";
				String productCategoryId=(String)productCategoriesList.get(0);
				Timestamp supplyDate = UtilDateTime.nowTimestamp();
                Map<String,Object>  productCategoryQuotasMap = FastMap.newInstance();
                Map<String, Object> resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId,"effectiveDate",supplyDate,"productCategoryId",productCategoryId));
				productCategoryQuotasMap = (Map) resultCtx.get("schemesMap");
				partyBalanceQuota = (BigDecimal) productCategoryQuotasMap.get(productCategoryId);
				
				////////Debug.log("partyBalanceQuota=====Used====3333==============="+partyBalanceQuota);
				
                //int updateQuotaSign = updateQuta.signum();
	            if(updateQuta.compareTo(BigDecimal.ZERO)>0 && partyBalanceQuota.compareTo(BigDecimal.ZERO)>0){
	            Map partyBalanceHistoryContext = FastMap.newInstance();
				partyBalanceHistoryContext = UtilMisc.toMap("schemeId",schemeId,"partyId",partyId,"productCategoryId",productCategoryId,"dateTimeStamp", supplyDate,"quantity",updateQuta,"userLogin", userLogin);
	            try {
					Map<String, Object> resultMapquota = dispatcher.runSync("createPartyQuotaBalanceHistory", partyBalanceHistoryContext);
					//quota=(BigDecimal)resultMapquota.get("quota");
					//////////Debug.log("quota=====Used====3333==============="+quota);
				} catch (Exception e) {
					Debug.logError("Failed to retrive PartyQuotaBalanceHistory"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive PartyQuotaBalanceHistory");	
			  		return "error";
				}
	            
	            BigDecimal quotaQty = BigDecimal.ZERO;
	            BigDecimal discountAmount = BigDecimal.ZERO;
	            BigDecimal schemePercent = new BigDecimal("10");
				BigDecimal percentModifier = schemePercent.movePointLeft(2);
	            
	            quotaQty = QuotaQty.add(updateQuta);
	            OrderItemDetail.set("quotaQuantity",quotaQty);
				discountAmount = ((quotaQty.multiply(amendedPrice)).multiply(percentModifier)).negate();
	            OrderItemDetail.set("discountAmount",discountAmount);
	            
	            GenericValue orderAdjustment =null;
	            List condList = FastList.newInstance();
	           		
				try {
				    condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
		            condList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
		            condList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
					List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
					orderAdjustment = (EntityUtil.getFirst(orderAdjustments));
				} catch (GenericEntityException e) {
					Debug.logError("Failed to retrive OrderAdjustment"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderAdjustment");	
			  		return "error";
					
				}
				
				////Debug.log("orderAdjustment=====Used====3333==============="+orderAdjustment);
				
					if(UtilValidate.isNotEmpty(orderAdjustment) && onBeHalfOf.equals("N")){
						 discountAmount = ((quotaQty.multiply(amendedPrice)).multiply(percentModifier)).negate();
						 orderAdjustment.set("amount",discountAmount);
						 orderAdjustment.store();
					}
	            }//orderItemDetail
                
                OrderItemDetail.store();
  
			}else if(Scheam.equals("MGPS_10Pecent") && onBeHalfOf.equals("N") && updateQuotaSign == -1){
				////Debug.log("Scheam===================="+Scheam);
				
				if(Scheam.equals("MGPS_10Pecent")){
					condsList.clear();
					condsList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
					condsList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,orderItemSeqId));
					condsList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
					try{
					List<GenericValue> orderItemAndAdjustmentList =  delegator.findList("OrderAdjustment",EntityCondition.makeCondition(condsList,EntityOperator.AND),null, null, null, true);   
					//Debug.log("orderItemAndAdjustmentList cancel start==============="+orderItemAndAdjustmentList+"size============="+orderItemAndAdjustmentList.size());
					GenericValue orderHeaderDetail = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				    Timestamp  orderDate = orderHeaderDetail.getTimestamp("orderDate");
					if(UtilValidate.isNotEmpty(orderItemAndAdjustmentList)&& orderItemAndAdjustmentList.size()>0){
						List schemeCategoryIds1 = FastList.newInstance();
					  	try{
					  		List productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), UtilMisc.toSet("productCategoryId"), null, null, false);
					  		schemeCategoryIds1 = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);
					   	}catch (GenericEntityException e) {
							Debug.logError(e, "Failed to retrive ProductCategory ", module);
						}	
					  	
					  	
						for(GenericValue orderItemAndAdjustment : orderItemAndAdjustmentList){

						  	BigDecimal quota =BigDecimal.ZERO;
						  	try {

							  	Map partyBalanceHistoryContext = FastMap.newInstance();
							  	partyBalanceHistoryContext = UtilMisc.toMap("partyId",partyId,"orderItemAndAdjustment",orderItemAndAdjustment,"schemeCategoryIds",schemeCategoryIds1,"schemeCategory",Scheam,"quota",updateQuta.abs(), "userLogin", userLogin,"productId",productId,"orderDate",orderDate);
							  	dispatcher.runSync("cancelPartyQuotaBalanceHistory", partyBalanceHistoryContext);

				  	
						  	} catch (Exception e) {
								Debug.logError(e, "Failed to retrive ProductPriceType ", module);
							}
						}
						
						
						
						 BigDecimal quotaQty = BigDecimal.ZERO;
				            BigDecimal discountAmount = BigDecimal.ZERO;
				            BigDecimal schemePercent = new BigDecimal("10");
							BigDecimal percentModifier = schemePercent.movePointLeft(2);
				            
				            quotaQty = QuotaQty.add(updateQuta);
				            OrderItemDetail.set("quotaQuantity",quotaQty);
							discountAmount = ((quotaQty.multiply(amendedPrice)).multiply(percentModifier)).negate();
				            OrderItemDetail.set("discountAmount",discountAmount);
				            
				            GenericValue orderAdjustment =null;
				            List condList = FastList.newInstance();
				           		
							try {
							    condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
					            condList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
					            condList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
								List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
								orderAdjustment = (EntityUtil.getFirst(orderAdjustments));
							} catch (GenericEntityException e) {
								Debug.logError("Failed to retrive OrderAdjustment"+orderId, module);
								request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderAdjustment");	
						  		return "error";
								
							}
						
						
							if(UtilValidate.isNotEmpty(orderAdjustment) && onBeHalfOf.equals("N")){
								 discountAmount = ((quotaQty.multiply(amendedPrice)).multiply(percentModifier)).negate();
								 orderAdjustment.set("amount",discountAmount);
								 orderAdjustment.store();
							}
							
							OrderItemDetail.store();
						
					}
					}catch (Exception e) {
						Debug.logError(e, "Failed to retrive ProductCategory ", module);
					}
				}
				
			}
			
				}
		  	}
		  	
		  	//===============================================populate sale Adjustments===============
			
		  	////Debug.log("primaryOrderId================="+primaryOrderId);
		  	
			List<GenericValue> OrderItemSale =null;
			try {
				OrderItemSale = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError("Failed to retrive OrderItemPurchase"+primaryOrderId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderItemPurchase");	
		  		return "error";
				
			}
		  	
			////Debug.log("OrderItemSale================="+OrderItemSale);
			
     		for(GenericValue eachOrderItemSale : OrderItemSale){
			
     			String saleOrder = eachOrderItemSale.getString("orderId");
     			String saleOrderItemSeqId = eachOrderItemSale.getString("orderItemSeqId");
     			
     			 BigDecimal saleQuantity = BigDecimal.ZERO;
				 BigDecimal saleUnitPrice = BigDecimal.ZERO;
				 BigDecimal itemValue = BigDecimal.ZERO;
				 
				    saleQuantity = eachOrderItemSale.getBigDecimal("quantity");
		     		saleUnitPrice = eachOrderItemSale.getBigDecimal("unitPrice");
		     		itemValue = saleQuantity.multiply(saleUnitPrice);
     			
            List<GenericValue> orderAdjustmentsalechaseList = null;
			List condListsale = FastList.newInstance();
       		
			try {
				condListsale.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,saleOrder));
				condListsale.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,saleOrderItemSeqId));
				condListsale.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL,"TEN_PERCENT_SUBSIDY"));
				orderAdjustmentsalechaseList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(condListsale, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError("Failed to retrive OrderAdjustment"+primaryOrderId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderAdjustment");	
		  		return "error";
				
			}
			
			////Debug.log("orderAdjustmentsalechaseList================="+orderAdjustmentsalechaseList);
			
			if(UtilValidate.isNotEmpty(orderAdjustmentsalechaseList)){
				
				for(GenericValue eachOrderAdjSale : orderAdjustmentsalechaseList){
					
					BigDecimal sourcePercentage = eachOrderAdjSale.getBigDecimal("sourcePercentage");
					BigDecimal adjAmount = (itemValue.multiply(sourcePercentage)).divide(new BigDecimal("100"));
					
					eachOrderAdjSale.set("amount",adjAmount);
					eachOrderAdjSale.store();
					
				}
			}
		  	
     		}
		  	
		  	
		  	
             //===============populate Other Adjustments in Purchase Order==========================
			
     		////Debug.log("primaryPurCahseOrderId================="+primaryPurCahseOrderId);
		  	
		  	List<GenericValue> OrderItemPurchase =null;
			try {
				OrderItemPurchase = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryPurCahseOrderId), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError("Failed to retrive OrderItemPurchase"+primaryPurCahseOrderId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderItemPurchase");	
		  		return "error";
				
			}
			
			////Debug.log("OrderItemPurchase================="+OrderItemPurchase);
		  	
     		for(GenericValue eachOrderItemPur : OrderItemPurchase){
			
     			String purOrder = eachOrderItemPur.getString("orderId");
     			String purOrderItemSeqId = eachOrderItemPur.getString("orderItemSeqId");
     			
     			 BigDecimal purQuantity = BigDecimal.ZERO;
				 BigDecimal purUnitPrice = BigDecimal.ZERO;
				 BigDecimal itemValue = BigDecimal.ZERO;
				 
				    purQuantity = eachOrderItemPur.getBigDecimal("quantity");
		     		purUnitPrice = eachOrderItemPur.getBigDecimal("unitPrice");
		     		itemValue = purQuantity.multiply(purUnitPrice);
     			
            List<GenericValue> orderAdjustmentPurchaseList = null;
			List condListPur1 = FastList.newInstance();
       		
			try {
				condListPur1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,purOrder));
				condListPur1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,purOrderItemSeqId));
				orderAdjustmentPurchaseList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(condListPur1, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError("Failed to retrive OrderAdjustment"+primaryPurCahseOrderId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderAdjustment");	
		  		return "error";
				
			}
			
			////Debug.log("orderAdjustmentPurchaseList================="+orderAdjustmentPurchaseList);
			
			if(UtilValidate.isNotEmpty(orderAdjustmentPurchaseList)){
				
				for(GenericValue eachOrderAdjPur : orderAdjustmentPurchaseList){
					
					BigDecimal sourcePercentage = eachOrderAdjPur.getBigDecimal("sourcePercentage");
					
					////////Debug.log("sourcePercentage================="+sourcePercentage);
					
					BigDecimal adjAmount = (itemValue.multiply(sourcePercentage)).divide(new BigDecimal("100"));
					
					////////Debug.log("sourcePercentage================="+sourcePercentage);
					eachOrderAdjPur.set("amount",adjAmount);
					eachOrderAdjPur.store();
					
				}
			}
		  	
     		}
     		
     		//=============update Sale Grand Total =====================
     		
     		BigDecimal saleGrandTotal = BigDecimal.ZERO;
     		
     		
            List<GenericValue> OrderItemSale1 =null;
      			try {
      				OrderItemSale1 = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId), null, null, null, false);
      			} catch (GenericEntityException e) {
      				Debug.logError("Failed to retrive OrderItemPurchase"+primaryOrderId, module);
      				request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderItemPurchase");	
      		  		return "error";
      				
      			}
           		
      			////Debug.log("OrderItemSale1================="+OrderItemSale1);
      			
      			for(GenericValue eachAdj : OrderItemSale1){
      				
      				BigDecimal amount = eachAdj.getBigDecimal("unitPrice");
      				BigDecimal quantity = eachAdj.getBigDecimal("quantity");
      				BigDecimal itemValue = amount.multiply(quantity);
      				saleGrandTotal = saleGrandTotal.add(itemValue);
      				
      			}
     		
     		List<GenericValue> orderAdjustmentsSale = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId), null, null, null, false);
     		
     		////Debug.log("orderAdjustmentsSale================="+orderAdjustmentsSale);
			for(GenericValue eachAdj : orderAdjustmentsSale){
				BigDecimal amount = eachAdj.getBigDecimal("amount");
				saleGrandTotal = saleGrandTotal.add(amount);
			}
			
     		////Debug.log("saleGrandTotal================="+saleGrandTotal);

			
			GenericValue orderHeaderDetailSale = null;
			try{
				orderHeaderDetailSale = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", primaryOrderId), false);
				orderHeaderDetailSale.set("grandTotal", saleGrandTotal);
				orderHeaderDetailSale.store();
			}catch (Exception e) {
				Debug.logError(e, "Error in amending order, module");
				request.setAttribute("_ERROR_MESSAGE_", "Error in amending order");
				return "error";
	  	 	}
			
			
		//============================END================================
			
			
             BigDecimal purchaseGrandTotal = BigDecimal.ZERO;
             
             
             List<GenericValue> OrderItemPurchase1 =null;
 			try {
 				OrderItemPurchase1 = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryPurCahseOrderId), null, null, null, false);
 			} catch (GenericEntityException e) {
 				Debug.logError("Failed to retrive OrderItemPurchase"+primaryPurCahseOrderId, module);
 				request.setAttribute("_ERROR_MESSAGE_", "Failed to retrive OrderItemPurchase");	
 		  		return "error";
 				
 			}
 			
 			////Debug.log("OrderItemPurchase1================="+OrderItemPurchase1);
      		
 			for(GenericValue eachAdj : OrderItemPurchase1){
 				
 				BigDecimal amount = eachAdj.getBigDecimal("unitPrice");
 				BigDecimal quantity = eachAdj.getBigDecimal("quantity");
 				BigDecimal itemValue = amount.multiply(quantity);
 				purchaseGrandTotal = purchaseGrandTotal.add(itemValue);
 				
 			}
             
 			
     		
     		List<GenericValue> orderAdjustmentsPur = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryPurCahseOrderId), null, null, null, false);
     		
			for(GenericValue eachAdj : orderAdjustmentsPur){
				BigDecimal amount = eachAdj.getBigDecimal("amount");
				purchaseGrandTotal = purchaseGrandTotal.add(amount);
			}
			
			////Debug.log("purchaseGrandTotal================="+purchaseGrandTotal);
			GenericValue orderHeaderDetailPur = null;
			try{
				orderHeaderDetailPur = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", primaryPurCahseOrderId), false);
				orderHeaderDetailPur.set("grandTotal", purchaseGrandTotal);
				orderHeaderDetailPur.store();
			}catch (Exception e) {
				Debug.logError(e, "Error in amending order, module");
				request.setAttribute("_ERROR_MESSAGE_", "Error in amending order");
				return "error";
	  	 	}
 			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("orderId", indentId));
 			if (ServiceUtil.isError(serviceResult)) {
 				request.setAttribute("_ERROR_MESSAGE_", "Error While Updateing Indent Summary Details");
 				return "error";
 			}
			request.setAttribute("orderId",primaryOrderId);
		  	
	  	}catch(Exception e){
	  		Debug.logError(e, "Error in amending order, module");
			request.setAttribute("_ERROR_MESSAGE_", "Error in amending order");
			return "error";
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
			String roleTypeId = null;
			if(partyId.contains("SUB")){
				roleTypeId = "DIVISION";
			}else{
				roleTypeId = "INTERNAL_ORGANIZATIO";
			}
			GenericValue party = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
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
		List<GenericValue> orderItemChanges = delegator.findList("OrderItemChange", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, UtilMisc.toList("-effectiveDatetime"), null, false);
		if(UtilValidate.isNotEmpty(orderItemChanges)){
			Timestamp effectiveDatetime = (EntityUtil.getFirst(orderItemChanges)).getTimestamp("effectiveDatetime");
			orderItemChanges = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("effectiveDatetime", EntityOperator.EQUALS, effectiveDatetime));
		}
	    // shipmentOrders
	    List<GenericValue> shipmentOrders= FastList.newInstance();
		if(UtilValidate.isNotEmpty(orderId)){
			shipmentOrders = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		}
		for (GenericValue orderItem : orderItems) {
			//if(UtilValidate.isNotEmpty(orderItem)){
				String productId=orderItem.getString("productId");
				String orderItemSeqId=orderItem.getString("orderItemSeqId");
				
				List<GenericValue> eachChangedItem = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				
				BigDecimal unitCost=orderItem.getBigDecimal("unitListPrice");
				
				if(UtilValidate.isNotEmpty(eachChangedItem)){
					unitCost=(EntityUtil.getFirst(eachChangedItem)).getBigDecimal("unitListPrice");
				}
				
				BigDecimal orderQuantity=orderItem.getBigDecimal("quantity");
				
	 			if(UtilValidate.isNotEmpty(eachChangedItem) && ((EntityUtil.getFirst(eachChangedItem)).getBigDecimal("quantity")).compareTo(BigDecimal.ZERO)>0){
	 				orderQuantity = (EntityUtil.getFirst(eachChangedItem)).getBigDecimal("quantity");
	 			}

			 // Checking Order Qty > Received Qty 			
				BigDecimal totReceivedQty = BigDecimal.ZERO;
				List<GenericValue> OrdersequenceShipList = EntityUtil.filterByCondition(shipmentOrders, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
	 			if(UtilValidate.isNotEmpty(OrdersequenceShipList)){
	 				for (GenericValue Ordersequence : OrdersequenceShipList) {
	 					 totReceivedQty = totReceivedQty.add(Ordersequence.getBigDecimal("quantityAccepted"));

	 				}
	 			}
                List<GenericValue> filterShipmentReceiptProduct = EntityUtil.filterByCondition(shipmentReceipts, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	 			if(UtilValidate.isNotEmpty(filterShipmentReceiptProduct)){
	 				totReceivedQty = totReceivedQty.add((EntityUtil.getFirst(filterShipmentReceiptProduct)).getBigDecimal("quantityAccepted"));

	 			}
	 			BigDecimal checkQty = (orderQuantity.multiply(new BigDecimal(1.1))).setScale(0, BigDecimal.ROUND_CEILING);
				Debug.log("=orderQty=="+orderQuantity+"==checkQty="+checkQty+"==totReceivedQty=="+totReceivedQty);
				//if(quantity.compareTo(checkQty)>0){
				if(totReceivedQty.compareTo(checkQty)>0){	
					Debug.logError("Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId);	
					TransactionUtil.rollback();
			  		return "error";
				}
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
		String comments = (String) context.get("comments");
		String changeReason = (String) context.get("changeReason");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		String SaleOrderId = "";
		try{
			
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			
			if(UtilValidate.isEmpty(orderHeader)){
				Debug.logError("Order doesn't exists with Id : "+orderId , module);
  	 			return ServiceUtil.returnError("Order doesn't exists with Id : "+orderId);
			}
        	
        	List conditionList1 = FastList.newInstance();
        	conditionList1.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, orderId));
        	conditionList1.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
			List<GenericValue> Shipment1 = delegator.findList("Shipment", EntityCondition.makeCondition(conditionList1, EntityOperator.AND), null, null, null, false);
        	
			if(UtilValidate.isNotEmpty(Shipment1)){
				Debug.logError("Please Cancell the Shipment "+orderId , module);
  	 			return ServiceUtil.returnError("Please Cancell the Shipment: "+orderId);
			}
			
			Map statusCtx = FastMap.newInstance();
			statusCtx.put("statusId", statusId);
			statusCtx.put("orderId", orderId);
			statusCtx.put("comments", comments);
			statusCtx.put("userLogin", userLogin);
			Map resultCtx = OrderServices.setOrderStatus(ctx, statusCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Order set status failed for orderId: " + orderId, module);
				return resultCtx;
			}
			String oldStatusId = (String)resultCtx.get("orderStatusId");
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
		        	String custRequestId="";
		        	List<GenericValue>  quoteAndItemAndCustRequest = delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId), null , null, null, false);
		        	if(UtilValidate.isNotEmpty(quoteAndItemAndCustRequest)){
		        		custRequestId = (EntityUtil.getFirst(quoteAndItemAndCustRequest)).getString("custRequestId");
					}
		        	boolean isOrdered=true;
		        	statusCtx.clear();
			 		statusCtx.put("custRequestId", custRequestId);
			 		statusCtx.put("userLogin", userLogin);
			 		try {
				 		Map<String, Object> enquiryResult = (Map)dispatcher.runSync("enquiryStatusValidation", statusCtx);
				 		isOrdered=(Boolean)enquiryResult.get("isOrdered");
			 		}catch(Exception e){
		 	        	Debug.logError("Error in enquiryStatusValidation Service", module);
		 			    return ServiceUtil.returnError("Error in enquiryStatusValidation Service");
		 	        }
			 		if (!isOrdered) {
			 			statusCtx.clear();
			 			statusCtx.put("statusId", "ENQ_CREATED");
			 			statusCtx.put("custRequestId", custRequestId);
			 			statusCtx.put("userLogin", userLogin);
			 			try{
				 			resultCtx = dispatcher.runSync("setRequestStatus", statusCtx);
				 			if (ServiceUtil.isError(resultCtx)) {
				 				Debug.logError("Error While Updating Enquiry Status: " + custRequestId, module);
				 				return resultCtx;
				 			}
			 			}catch(Exception e){
			 	        	Debug.logError("Error While Updating Enquiry Status:" + custRequestId, module);
			 			    return ServiceUtil.returnError("Error While Updating Enquiry Status:");
			 	        }
			 		}
				}
				
			}
			
			
			/*List<GenericValue> orderAssocList = null;
			
			

            try {
            	orderAssocList = delegator.findList("OrderAssoc", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);            	
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                orderAssocList = null;
            }
            if (UtilValidate.isNotEmpty(orderAssocList)) {
	            try {
	            	GenericValue orderAssoc = EntityUtil.getFirst(orderAssocList);
	            	SaleOrderId = orderAssoc.getString("toOrderId");
	            	orderAssoc.remove();
	            	Debug.log("order Association Removed!");
	            } catch (GenericEntityException e) {
	 	        	Debug.logError("error while removing order Association" + orderId, module);
	
	            }
            }
            
            List<GenericValue> orderAssocItemList = null;
            try {
            	orderAssocItemList = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId), null, null, null, false);            	
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                orderAssocItemList = null;
            }
           
            if (UtilValidate.isNotEmpty(orderAssocItemList)) {
	            try {
	            	delegator.removeAll(orderAssocItemList);
	            	Debug.log("order Item Association Removed!");
	            } catch (GenericEntityException e) {
	 	        	Debug.logError("error while removing order Item Association" + orderId, module);
	
	            }
            }*/


			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		
		
		 try{
			 
		    Map resultCtx = FastMap.newInstance();
			resultCtx = dispatcher.runSync("getAssociateOrder", UtilMisc.toMap("userLogin", userLogin, "orderId", orderId));
			if(UtilValidate.isNotEmpty(resultCtx.get("orderId")))
			  SaleOrderId = (String) resultCtx.get("orderId");
		 
		} catch (Exception e) {
	        Debug.logWarning(e.getMessage(), module);
	    }
		 
		 List<String> purchaseOrderIds = FastList.newInstance();
		 
		 try{
			 
			    Map resultCtx = FastMap.newInstance();
				resultCtx = dispatcher.runSync("saleToPoDetails", UtilMisc.toMap("userLogin", userLogin, "orderId", SaleOrderId));
				if(UtilValidate.isNotEmpty(resultCtx.get("purcahseOrderId")))
					purchaseOrderIds = (List) resultCtx.get("purcahseOrderId");
				
				    purchaseOrderIds.remove(orderId);
			 
			} catch (Exception e) {
		        Debug.logWarning(e.getMessage(), module);
		    }
			 
		 
		 if (UtilValidate.isNotEmpty(purchaseOrderIds)) {
		 
		 for (String eachOrderId : purchaseOrderIds) {
			 
			 try{
					
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CREATED"));
					List<GenericValue> OrderStatus = delegator.findList("OrderStatus", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
						if(UtilValidate.isNotEmpty(OrderStatus)){
							delegator.removeAll(OrderStatus);
							
							GenericValue OrderHeder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", eachOrderId), false);
							OrderHeder.put("statusId", "ORDER_CREATED");
							OrderHeder.store();
							
						}
						
					}catch(GenericEntityException e){
						Debug.logError(e, "Failed to retrive orderItemDetail ", module);
					}
		}
		 
		 }
		 
		
		if (UtilValidate.isNotEmpty(SaleOrderId)) {
			try{
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, SaleOrderId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CREATED"));
			List<GenericValue> OrderStatus = delegator.findList("OrderStatus", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				if(UtilValidate.isNotEmpty(OrderStatus)){
					delegator.removeAll(OrderStatus);
					
					GenericValue OrderHeder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", SaleOrderId), false);
					OrderHeder.put("statusId", "ORDER_CREATED");
					OrderHeder.store();
					
				}
				
			}catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive orderItemDetail ", module);
			}
			
			
		}
		try{
 			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("orderId", SaleOrderId));
 			if (ServiceUtil.isError(serviceResult)) {
 				Debug.logError("Error While Updateing Indent Summary Details", module);
            }
  		}catch(GenericServiceException e){
			Debug.logError(e, "Error While Updateing Indent Summary Details ", module);
		}
		return result;
	}
	
	
	public static Map<String, Object> cancelPOStatusDC(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
        
		String statusId = (String) context.get("statusId");
		String orderId = (String) context.get("orderId");
		String comments = (String) context.get("comments");
		String changeReason = (String) context.get("changeReason");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		String SaleOrderId = "";
		try{
			
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			
			if(UtilValidate.isEmpty(orderHeader)){
				Debug.logError("Order doesn't exists with Id : "+orderId , module);
  	 			return ServiceUtil.returnError("Order doesn't exists with Id : "+orderId);
			}
        	
        	List conditionList1 = FastList.newInstance();
        	conditionList1.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, orderId));
        	conditionList1.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
			List<GenericValue> Shipment1 = delegator.findList("Shipment", EntityCondition.makeCondition(conditionList1, EntityOperator.AND), null, null, null, false);
        	
			if(UtilValidate.isNotEmpty(Shipment1)){
				Debug.logError("Please Cancell the Shipment "+orderId , module);
  	 			return ServiceUtil.returnError("Please Cancell the Shipment: "+orderId);
			}
			
			Map statusCtx = FastMap.newInstance();
			statusCtx.put("statusId", statusId);
			statusCtx.put("orderId", orderId);
			statusCtx.put("comments", comments);
			statusCtx.put("userLogin", userLogin);
			Map resultCtx = OrderServices.setOrderStatus(ctx, statusCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Order set status failed for orderId: " + orderId, module);
				return resultCtx;
			}
			String oldStatusId = (String)resultCtx.get("orderStatusId");
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
		        	String custRequestId="";
		        	List<GenericValue>  quoteAndItemAndCustRequest = delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId), null , null, null, false);
		        	if(UtilValidate.isNotEmpty(quoteAndItemAndCustRequest)){
		        		custRequestId = (EntityUtil.getFirst(quoteAndItemAndCustRequest)).getString("custRequestId");
					}
		        	boolean isOrdered=true;
		        	statusCtx.clear();
			 		statusCtx.put("custRequestId", custRequestId);
			 		statusCtx.put("userLogin", userLogin);
			 		try {
				 		Map<String, Object> enquiryResult = (Map)dispatcher.runSync("enquiryStatusValidation", statusCtx);
				 		isOrdered=(Boolean)enquiryResult.get("isOrdered");
			 		}catch(Exception e){
		 	        	Debug.logError("Error in enquiryStatusValidation Service", module);
		 			    return ServiceUtil.returnError("Error in enquiryStatusValidation Service");
		 	        }
			 		if (!isOrdered) {
			 			statusCtx.clear();
			 			statusCtx.put("statusId", "ENQ_CREATED");
			 			statusCtx.put("custRequestId", custRequestId);
			 			statusCtx.put("userLogin", userLogin);
			 			try{
				 			resultCtx = dispatcher.runSync("setRequestStatus", statusCtx);
				 			if (ServiceUtil.isError(resultCtx)) {
				 				Debug.logError("Error While Updating Enquiry Status: " + custRequestId, module);
				 				return resultCtx;
				 			}
			 			}catch(Exception e){
			 	        	Debug.logError("Error While Updating Enquiry Status:" + custRequestId, module);
			 			    return ServiceUtil.returnError("Error While Updating Enquiry Status:");
			 	        }
			 		}
				}
				
			}
			
			
			/*List<GenericValue> orderAssocList = null;
			
			

            try {
            	orderAssocList = delegator.findList("OrderAssoc", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);            	
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                orderAssocList = null;
            }
            if (UtilValidate.isNotEmpty(orderAssocList)) {
	            try {
	            	GenericValue orderAssoc = EntityUtil.getFirst(orderAssocList);
	            	SaleOrderId = orderAssoc.getString("toOrderId");
	            	orderAssoc.remove();
	            	Debug.log("order Association Removed!");
	            } catch (GenericEntityException e) {
	 	        	Debug.logError("error while removing order Association" + orderId, module);
	
	            }
            }
            
            List<GenericValue> orderAssocItemList = null;
            try {
            	orderAssocItemList = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId), null, null, null, false);            	
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                orderAssocItemList = null;
            }
           
            if (UtilValidate.isNotEmpty(orderAssocItemList)) {
	            try {
	            	delegator.removeAll(orderAssocItemList);
	            	Debug.log("order Item Association Removed!");
	            } catch (GenericEntityException e) {
	 	        	Debug.logError("error while removing order Item Association" + orderId, module);
	
	            }
            }*/


			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		
		
		 try{
			 
		    Map resultCtx = FastMap.newInstance();
			resultCtx = dispatcher.runSync("getAssociateOrder", UtilMisc.toMap("userLogin", userLogin, "orderId", orderId));
			if(UtilValidate.isNotEmpty(resultCtx.get("orderId")))
			  SaleOrderId = (String) resultCtx.get("orderId");
		 
		} catch (Exception e) {
	        Debug.logWarning(e.getMessage(), module);
	    }
		 
		 List<String> purchaseOrderIds = FastList.newInstance();
		 
		 try{
			 
			    Map resultCtx = FastMap.newInstance();
				resultCtx = dispatcher.runSync("saleToPoDetails", UtilMisc.toMap("userLogin", userLogin, "orderId", SaleOrderId));
				if(UtilValidate.isNotEmpty(resultCtx.get("purcahseOrderId")))
					purchaseOrderIds = (List) resultCtx.get("purcahseOrderId");
				
				    purchaseOrderIds.remove(orderId);
			 
			} catch (Exception e) {
		        Debug.logWarning(e.getMessage(), module);
		    }
			 
		 
		 if (UtilValidate.isNotEmpty(purchaseOrderIds)) {
		 
		 for (String eachOrderId : purchaseOrderIds) {
			 
			 try{
					
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CREATED"));
					List<GenericValue> OrderStatus = delegator.findList("OrderStatus", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
						if(UtilValidate.isNotEmpty(OrderStatus)){
							delegator.removeAll(OrderStatus);
							
							GenericValue OrderHeder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", eachOrderId), false);
							OrderHeder.put("statusId", "ORDER_CREATED");
							OrderHeder.store();
							
						}
						
					}catch(GenericEntityException e){
						Debug.logError(e, "Failed to retrive orderItemDetail ", module);
					}
		}
		 
		 }
		 
		
		if (UtilValidate.isNotEmpty(SaleOrderId)) {
			try{
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, SaleOrderId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CREATED"));
			List<GenericValue> OrderStatus = delegator.findList("OrderStatus", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				if(UtilValidate.isNotEmpty(OrderStatus)){
					delegator.removeAll(OrderStatus);
					
					GenericValue OrderHeder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", SaleOrderId), false);
					OrderHeder.put("statusId", "ORDER_CREATED");
					OrderHeder.store();
					
				}
				
			}catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive orderItemDetail ", module);
			}
			
			
		}
		try{
 			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("orderId", SaleOrderId));
 			if (ServiceUtil.isError(serviceResult)) {
 				Debug.logError("Error While Updateing Indent Summary Details", module);
            }
  		}catch(GenericServiceException e){
			Debug.logError(e, "Error While Updateing Indent Summary Details ", module);
		}
		return result;
	}
	
	
	public static String getMaterialPOValue(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	    String incTax = (String) request.getParameter("incTax");
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		
		HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	    List<Map> productQtyList = FastList.newInstance();
		List<Map> otherChargesList = FastList.newInstance();
		try{
			
			for (int i = 0; i < rowCount; i++) {
				
				String productId = "";
				String orderItemSeqId="";
		        String quantityStr = "";
				String unitPriceStr = "";
				String baleQtyStr = "";
				String remarks = "";
				String bundleUnitPriceStr= "";
		        String bundleWeightStr = "";
				String yarnUOM= "";
				String vatPercentStr = "";
				String bedPercentStr = "";
				String cstPercentStr = "";
				BigDecimal unitPrice = BigDecimal.ZERO;
		        BigDecimal quantity = BigDecimal.ZERO;
		        BigDecimal bundleUnitPrice = BigDecimal.ZERO;
		        BigDecimal bundleWeight = BigDecimal.ZERO;
		        BigDecimal baleQty = BigDecimal.ZERO;
		        BigDecimal bedPercent = BigDecimal.ZERO;
		        BigDecimal cstPercent = BigDecimal.ZERO;
		        BigDecimal vatPercent = BigDecimal.ZERO;
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
				}
				if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
					orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
				}
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
				}
				if (paramMap.containsKey("unitPrice" + thisSuffix)) {
					unitPriceStr = (String) paramMap.get("unitPrice" + thisSuffix);
				}if (paramMap.containsKey("baleQuantity" + thisSuffix)) {
					baleQtyStr = (String) paramMap.get("baleQuantity" + thisSuffix);
				}
				if (paramMap.containsKey("remarks" + thisSuffix)) {
					remarks = (String) paramMap.get("remarks" + thisSuffix);
				}
				if (paramMap.containsKey("yarnUOM" + thisSuffix)) {
					yarnUOM = (String) paramMap.get("yarnUOM" + thisSuffix);
				}
				if (paramMap.containsKey("bundleWeight" + thisSuffix)) {
					bundleWeightStr = (String) paramMap.get("bundleWeight" + thisSuffix);
				}
				if (paramMap.containsKey("bundleUnitPrice" + thisSuffix)) {
					bundleUnitPriceStr = (String) paramMap.get("bundleUnitPrice" + thisSuffix);
				}
				if (paramMap.containsKey("vatPercent" + thisSuffix)) {
					vatPercentStr = (String) paramMap.get("vatPercent" + thisSuffix);
				}
				if (paramMap.containsKey("bedPercent" + thisSuffix)) {
					bedPercentStr = (String) paramMap.get("bedPercent" + thisSuffix);
				}
				if (paramMap.containsKey("cstPercent" + thisSuffix)) {
					cstPercentStr = (String) paramMap.get("cstPercent" + thisSuffix);
				}
				if(UtilValidate.isNotEmpty(quantityStr)){
					try {
						quantity = new BigDecimal(quantityStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
						return "error";
					}
				}
				
				if(UtilValidate.isNotEmpty(unitPriceStr)){
					try {
						unitPrice = new BigDecimal(unitPriceStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing unit price string: " + unitPriceStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing unit price string: " + unitPriceStr);
						return "error";
					}
				}
				if(UtilValidate.isNotEmpty(bundleUnitPriceStr)){
					try {
						bundleUnitPrice = new BigDecimal(bundleUnitPriceStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing bundle unit price string: " + bundleUnitPriceStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bundle unit price string: " + bundleUnitPriceStr);
						return "error";
					}
				}
				if(UtilValidate.isNotEmpty(bundleWeightStr)){
					try {
						bundleWeight = new BigDecimal(bundleWeightStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing unit price string: " + bundleWeightStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing unit price string: " + bundleWeightStr);
						return "error";
					}
				}
				if(UtilValidate.isNotEmpty(baleQtyStr)){
					try {
						baleQty = new BigDecimal(baleQtyStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing unit price string: " + baleQtyStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing unit price string: " + baleQtyStr);
						return "error";
					}
				}
				
				
				if(UtilValidate.isNotEmpty(bedPercentStr)){
					try {
						bedPercent = new BigDecimal(bedPercentStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing bedPercent string: " + bedPercentStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bedPercent string: " + bedPercentStr);
						return "error";
					}
				}
				
				if(UtilValidate.isNotEmpty(cstPercentStr)){
					try {
						cstPercent = new BigDecimal(cstPercentStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing cstPercent string: " + cstPercentStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing cstPercent string: " + cstPercentStr);
						return "error";
					}
				}
				
				if(UtilValidate.isNotEmpty(vatPercentStr)){
					try {
						vatPercent = new BigDecimal(vatPercentStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing vatPercent string: " + vatPercentStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing vatPercent string: " + vatPercentStr);
						return "error";
					}
				}
				
				
				if(UtilValidate.isNotEmpty(productId) && unitPrice.compareTo(BigDecimal.ZERO)>0 && quantity.compareTo(BigDecimal.ZERO)>0){
					Map productDetail = FastMap.newInstance();
					productDetail.put("productId", productId);
					productDetail.put("orderItemSeqId", orderItemSeqId);
					productDetail.put("quantity", quantity);
					productDetail.put("unitPrice", unitPrice);
					productDetail.put("bundleUnitPrice", bundleUnitPrice);
					productDetail.put("bundleWeight", bundleWeight);
					productDetail.put("baleQty", baleQty);
					productDetail.put("yarnUOM", yarnUOM);
					productDetail.put("remarks", remarks);
					productDetail.put("bedPercent", bedPercent);
					productDetail.put("cstPercent", cstPercent);
					productDetail.put("vatPercent", vatPercent);
					productQtyList.add(productDetail);
				}
				
				String otherTermId = "";
				String applicableTo = "ALL";
				String termValueStr = "";
				String termDaysStr = "";
				String description = "";
				String uomId = "INR";
				BigDecimal termValue = BigDecimal.ZERO;
				BigDecimal termDays = BigDecimal.ZERO;
				
				if (paramMap.containsKey("otherTermId" + thisSuffix)) {
					otherTermId = (String) paramMap.get("otherTermId" + thisSuffix);
				}
				
				if (paramMap.containsKey("applicableTo" + thisSuffix)) {
					applicableTo = (String) paramMap.get("applicableTo" + thisSuffix);
				}
				if (paramMap.containsKey("adjustmentValue" + thisSuffix)) {
					termValueStr = (String) paramMap.get("adjustmentValue" + thisSuffix);
				}
				
				if (paramMap.containsKey("termDays" + thisSuffix)) {
					termDaysStr = (String) paramMap.get("termDays" + thisSuffix);
				}
				if (paramMap.containsKey("description" + thisSuffix)) {
					description = (String) paramMap.get("description" + thisSuffix);
				}
				if (paramMap.containsKey("uomId" + thisSuffix)) {
					uomId = (String) paramMap.get("uomId" + thisSuffix);
				}
				if(UtilValidate.isNotEmpty(termValueStr)){
					try {
						termValue = new BigDecimal(termValueStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing term value string: " + termValueStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing term value string: " + termValueStr);
						return "error";
					}
				}
				if(UtilValidate.isNotEmpty(termDaysStr)){
					try {
						termDays = new BigDecimal(termDaysStr);
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing term days string: " + termDaysStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing term days string: " + termDaysStr);
						return "error";
					}
				}
				
				if(UtilValidate.isNotEmpty(otherTermId) && termValue.compareTo(BigDecimal.ZERO)>0){
					Map otherChargesDetail = FastMap.newInstance();
					otherChargesDetail.put("otherTermId", otherTermId);
					otherChargesDetail.put("termValue", termValue);
					otherChargesDetail.put("applicableTo", applicableTo);
					otherChargesDetail.put("termDays", termDays);
					otherChargesDetail.put("uomId", uomId);
					otherChargesDetail.put("description", description);
					otherChargesList.add(otherChargesDetail);
				}
			}
			Map inputCtx = FastMap.newInstance();
			inputCtx.put("incTax", incTax);
			inputCtx.put("otherCharges", otherChargesList);
			inputCtx.put("productQty", productQtyList);
			inputCtx.put("userLogin", userLogin);
			Map resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", inputCtx);
			
			if(ServiceUtil.isError(resultCtx)){
				Debug.logError("Error in getting Material Values", module);
				request.setAttribute("_ERROR_MESSAGE_", "Error in getting Material Values");	
		  		return "error";
			}
			
			BigDecimal grandTotal = (BigDecimal)resultCtx.get("grandTotal");
			List itemDetail = (List)resultCtx.get("itemDetail");
			List adjustmentDetail = (List)resultCtx.get("adjustmentDetail");
			List termsDetail = (List)resultCtx.get("termsDetail");
			request.setAttribute("termsDetail", termsDetail);
		  	request.setAttribute("grandTotal", grandTotal);
		  	request.setAttribute("itemDetail", itemDetail);
		  	request.setAttribute("adjustmentDetail", adjustmentDetail);
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Unable to process request ", module);
			return "error";
		}
		return "success";
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
			
			//storing shipment receipt status Here 
			if(UtilValidate.isNotEmpty(receiptId)){
				GenericValue shipmentReceiptStatus = delegator.makeValue("ShipmentReceiptStatus");
				shipmentReceiptStatus.set("receiptId", receiptId);
				shipmentReceiptStatus.set("statusId", statusId);
				shipmentReceiptStatus.set("changedByUserLogin", userLogin.getString("userLoginId"));
				shipmentReceiptStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
				delegator.createSetNextSeqId(shipmentReceiptStatus);
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
	public static Map<String, Object> shipmentSendForQC(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String statusId = (String) context.get("statusIdTo");
		String shipmentId = (String) context.get("shipmentId");
		String partyId = (String) context.get("partyId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_RECEIVED"));
			List<GenericValue> shipmentReceipts = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			
			Debug.log("shipmentReceipts================"+shipmentReceipts);
			if(UtilValidate.isNotEmpty(shipmentReceipts)){
				for(GenericValue receipt:shipmentReceipts){
		        	GenericValue shipmentValue = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
					String shipmentTypeId=shipmentValue.getString("shipmentTypeId");
					if(UtilValidate.isNotEmpty(shipmentTypeId)){
						if( !shipmentTypeId.equals("BRANCH_SHIPMENT")){
							Map inputMap = FastMap.newInstance();
				        	inputMap.put("userLogin", userLogin);
				        	inputMap.put("partyId", partyId);
				        	inputMap.put("statusIdTo", statusId);
				        	inputMap.put("receiptId", receipt.get("receiptId"));

				        	Map	resultReceipt = dispatcher.runSync("sendReceiptQtyForQC", inputMap);
				        	
				        	Debug.log("resultReceipt=====sendReceiptQtyForQC==========="+resultReceipt);
				        	
				        	/*if(ServiceUtil.isError(resultReceipt)){
				        		Debug.logError("Error While Sending Receipt to QC", module);
				  	  			return ServiceUtil.returnError("Error While Sending Receipt to QC"+receipt.get("receiptId"));
				        	}*/
				        	if(ServiceUtil.isError(resultReceipt)){
				        		Debug.logError("Error While  While Accepting Receipt", module);
				  	  			return ServiceUtil.returnError("Error While Accepting Receipt "+receipt.get("receiptId"));
				        	}
			        	//send QC in same time
				        	
							Debug.log("resultReceipt=======acceptReceiptQtyByQC========="+resultReceipt);
							
							Debug.log("receiptId=======acceptReceiptQtyByQC========="+receipt.get("receiptId"));

							Debug.log("shipmentItemSeqId=======acceptReceiptQtyByQC========="+receipt.get("shipmentItemSeqId"));

							Debug.log("quantityAccepted=======acceptReceiptQtyByQC========="+receipt.get("quantityAccepted"));

							

				        	
			        	inputMap.clear();
			        	resultReceipt.clear();
						
						inputMap.put("statusIdTo","SR_ACCEPTED");
						inputMap.put("receiptId",receipt.get("receiptId"));
						inputMap.put("shipmentId",shipmentId);
						inputMap.put("shipmentItemSeqId",receipt.get("shipmentItemSeqId") );
						inputMap.put("quantityAccepted",receipt.get("quantityAccepted"));
						inputMap.put("userLogin",userLogin);
						resultReceipt = dispatcher.runSync("acceptReceiptQtyByQC", inputMap);
						
						
						Debug.log("resultReceipt=======acceptReceiptQtyByQC========="+resultReceipt);
						
						if (ServiceUtil.isError(resultReceipt)) {
							Debug.logError("Error While Accepting ", module);
			  	  			return ServiceUtil.returnError("Error While  While Accepting"+receipt.get("receiptId"));
			            }
					}else{
						BigDecimal quantityAccepted = (BigDecimal) receipt.get("quantityAccepted");
						String shipStatusId="SR_ACCEPTED";
						if(UtilValidate.isEmpty(quantityAccepted)){
							return ServiceUtil.returnError("Quantity accepted cannot be ZERO ");
						}
						if(quantityAccepted.compareTo(BigDecimal.ZERO) ==0){
							shipStatusId = "SR_REJECTED";
						}
						if(quantityAccepted.compareTo(BigDecimal.ZERO) ==-1){
							return ServiceUtil.returnError("negative value not allowed");
						}
						GenericValue shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receipt.get("receiptId")), false);
						
						GenericValue shipmentItem = delegator.findOne("ShipmentItem", UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", receipt.get("shipmentItemSeqId")), false);
						

						BigDecimal origReceiptQty=BigDecimal.ZERO;
						origReceiptQty = shipmentItem.getBigDecimal("quantity");
						BigDecimal rejectedQty = origReceiptQty.subtract(quantityAccepted);
						
						if(quantityAccepted.compareTo(origReceiptQty) >0){
							return ServiceUtil.returnError("not accept more than the received quantity");
						}
						//shipment receipts accept quantity populating
						shipmentReceipt.put("quantityAccepted", quantityAccepted);
						shipmentReceipt.put("quantityRejected", rejectedQty);
						shipmentReceipt.put("statusId", shipStatusId);
						shipmentReceipt.store();
						

						
						if(UtilValidate.isNotEmpty(receipt.get("receiptId"))){
							GenericValue shipmentReceiptStatus = delegator.makeValue("ShipmentReceiptStatus");
							shipmentReceiptStatus.set("receiptId", receipt.get("receiptId"));
							shipmentReceiptStatus.set("statusId", shipStatusId);
							shipmentReceiptStatus.set("changedByUserLogin", userLogin.getString("userLoginId"));
							shipmentReceiptStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
							delegator.createSetNextSeqId(shipmentReceiptStatus);
						}
						
						
						
					}
				}
				}
			}
			try {
	        	 Map<String, Object> updateShipmentCtx = FastMap.newInstance();
	        	 updateShipmentCtx.put("userLogin", context.get("userLogin"));
	        	 updateShipmentCtx.put("shipmentId", shipmentId);
	        	 updateShipmentCtx.put("statusId", "GOODS_RECEIVED");            
	             dispatcher.runSync("updateShipment", updateShipmentCtx);
	        }
	        catch (GenericServiceException e) {
	    		Debug.logError(e, "Failed to update shipment status " + shipmentId, module);
	    		return ServiceUtil.returnError("Failed to update shipment status " + shipmentId + ": " + e);          	
	        }
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		//result = ServiceUtil.returnSuccess("GRN no: "+shipmentId+" Send For Quality Check ");
		result = ServiceUtil.returnSuccess("GRN No:"+shipmentId+" Accepted Successfully  !!!! ");
		
		return result;
	}
	public static Map<String, Object>  createSupplier(DispatchContext dctx, Map<String, ? extends Object> context)  {
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        Locale locale = (Locale) context.get("locale");
        String contactMechId ="";
        String groupName = (String) context.get("groupName");
        String panId = (String) context.get("USER_PANID");
        String serviceTax = (String) context.get("USER_SERVICETAXNUM");
        String tinNumber= (String) context.get("USER_TINNUM");
        String cstNumber = (String) context.get("USER_CSTNUM");
        String adharNum = (String) context.get("ADR_NUMBER");
        String address1 = (String) context.get("address1");
        String address2 = (String) context.get("address2");
        String city = (String) context.get("city");
        String stateProvinceGeoId = (String) context.get("state");
        String districtGeoId = (String) context.get("distic");
        String postalCode = (String) context.get("postalCode");
		String email = (String) context.get("emailAddress");
		String AltemailAddress = (String) context.get("AltemailAddress");
		String mobileNumber = (String) context.get("mobileNumber");
		String contactNumber =(String)context.get("contactNumber");
		String countryCode = (String) context.get("countryCode");
		String roleTypeId = (String) context.get("roleTypeId");
		/*String accName= (String) context.get("accName");
        String accNo= (String) context.get("accNo");
        String accBranch= (String) context.get("accBranch");
     */   String IfscCode= (String) context.get("IfscCode");
		String suppRole = (String) context.get("suppRole");
		String bankName = (String) context.get("bankName");		
		String branch = (String) context.get("branch");		
		String ifscCode = (String) context.get("ifscCode");		
		String accNo = (String) context.get("accNo");
		String accName = (String) context.get("accName");



		Map<String, Object> outMap = FastMap.newInstance();
		
		try {
			result=dispatcher.runSync("createPartyGroup", UtilMisc.toMap("groupName",groupName,"userLogin", context.get("userLogin")));
			if (ServiceUtil.isError(result)) {
  		  		String errMsg =  ServiceUtil.getErrorMessage(result);
  		  		Debug.logError(errMsg , module);
  		  	}
            partyId = (String) result.get("partyId");
            if(UtilValidate.isNotEmpty(partyId)){
            	String defaultRoleType = "SUPPLIER";
            	Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId", defaultRoleType);
            	Map<String, Object>  resultMap = dispatcher.runSync("createPartyRole", input);
     			if (ServiceUtil.isError(resultMap)) {
     				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                     return resultMap;
                }
     			input.clear();
            	input = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId", roleTypeId);
            	resultMap = dispatcher.runSync("createPartyRole", input);
     			if (ServiceUtil.isError(resultMap)) {
     				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                     return resultMap;
                }
     			
     			if (UtilValidate.isNotEmpty(address1)){
     				input.clear();
    				input = UtilMisc.toMap("userLogin", userLogin, "partyId",partyId, "address1",address1, "address2", address2, "city", city, "stateProvinceGeoId", (String)context.get("stateProvinceGeoId"), "postalCode", postalCode);
    				input.put("stateProvinceGeoId",stateProvinceGeoId);
    				input.put("districtGeoId",districtGeoId);
    				resultMap =  dispatcher.runSync("createPartyPostalAddress", input);
    				if (ServiceUtil.isError(resultMap)) {
    					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
    	                return resultMap;
    	            }
    				contactMechId = (String) resultMap.get("contactMechId");
     				input.clear();
    				input = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "partyId",partyId, "contactMechPurposeTypeId", "BILLING_LOCATION");
    				resultMap =  dispatcher.runSync("createPartyContactMechPurpose", input);
    				if (ServiceUtil.isError(resultMap)) {
    				    Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
    	                return resultMap;
    	            }
    			 }
    			// create phone number
    			if (UtilValidate.isNotEmpty(mobileNumber)){
    				if (UtilValidate.isEmpty(countryCode)){
    					countryCode	="91";
    				}
    	            input.clear();
    	            input = UtilMisc.toMap("userLogin", userLogin, "contactNumber", mobileNumber,"countryCode",countryCode, "partyId",partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE");
    	            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
    	            if(ServiceUtil.isError(outMap)){
    	           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    	            }
    			}
                // create landLine number
    			if (UtilValidate.isNotEmpty(contactNumber)){
    	            input.clear();
    	            input = UtilMisc.toMap("userLogin", userLogin, "contactNumber", contactNumber, "partyId",partyId, "contactMechPurposeTypeId", "PHONE_HOME");
    	            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
    	            if(ServiceUtil.isError(outMap)){
    	           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    	            }
    			}
                // Create Party Email
    			if (UtilValidate.isNotEmpty(email)){
    	            input.clear();
    	            input = UtilMisc.toMap("userLogin", userLogin, "emailAddress", email, "partyId",partyId,"verified","Y", "fromDate",UtilDateTime.nowTimestamp(),"contactMechPurposeTypeId", "PRIMARY_EMAIL");
    	            outMap = dispatcher.runSync("createPartyEmailAddress", input);
    	            if(ServiceUtil.isError(outMap)){
    	           	 	Debug.logError("faild service create party Email:"+ServiceUtil.getErrorMessage(outMap), module);
    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    	            }
    			}
    			
    			if (UtilValidate.isNotEmpty(AltemailAddress)){
    	            input.clear();
    	            input = UtilMisc.toMap("userLogin", userLogin, "emailAddress", AltemailAddress, "partyId",partyId,"verified","Y", "fromDate",UtilDateTime.nowTimestamp(),"contactMechPurposeTypeId", "PRIMARY_EMAIL");
    	            outMap = dispatcher.runSync("createPartyEmailAddress", input);
    	            if(ServiceUtil.isError(outMap)){
    	           	 	Debug.logError("faild service create party Email:"+ServiceUtil.getErrorMessage(outMap), module);
    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    	            }
    			}
    			
    			
    			if(UtilValidate.isNotEmpty(suppRole)){
	    			try{
						GenericValue PartyClassification = delegator.makeValue("PartyClassification");
	    				PartyClassification.set("partyId", partyId);
		    			PartyClassification.set("partyClassificationGroupId", suppRole);
		                PartyClassification.set("fromDate", UtilDateTime.nowTimestamp());
						delegator.create(PartyClassification);
					}catch (Exception e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError("Error while creating  PartyClassification" + e);	
					}
    			}
				
	            if(UtilValidate.isNotEmpty(panId)){
	            	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","PAN_NUMBER","idValue",panId,"partyId",partyId,"userLogin", context.get("userLogin")));
	       	    }
	            if(UtilValidate.isNotEmpty(serviceTax)){
	           	     dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","SERVICETAX_NUMBER","idValue",serviceTax,"partyId",partyId,"userLogin", context.get("userLogin")));
	      	    }
	            if(UtilValidate.isNotEmpty(tinNumber)){
	              	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","TIN_NUMBER","idValue",tinNumber,"partyId",partyId,"userLogin", context.get("userLogin")));
	         	}
	            if(UtilValidate.isNotEmpty(cstNumber)){
	             	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","CST_NUMBER","idValue",cstNumber,"partyId",partyId,"userLogin", context.get("userLogin")));
	        	}
	            if(UtilValidate.isNotEmpty(adharNum)){
	             	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","ADR_NUMBER","idValue",adharNum,"partyId",partyId,"userLogin", context.get("userLogin")));
	        	}
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e.getMessage(), module);
            Debug.logError(e, "Error creating Group For Vendor",module);
			return ServiceUtil.returnError("Error creating Group For Vendor :"+groupName);
        }
		
		
		
		 if(UtilValidate.isNotEmpty(partyId)){
		        try{
					GenericValue BankAccount = delegator.makeValue("BankAccount");
						BankAccount.set("bankAccountName", accName);
						BankAccount.set("bankAccountCode", accNo);
						BankAccount.set("branchCode", ifscCode);
						BankAccount.set("ifscCode", ifscCode);
						BankAccount.set("ownerPartyId", partyId);
						delegator.createSetNextSeqId(BankAccount);
				}catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error while creating  Bank Details" + e);	
				}
	        }
		
		
		
		
		
		
		
		
		
		
		result.put("partyId", partyId);
		return result;
	}
	
	public static String createProduct(HttpServletRequest request, HttpServletResponse response) {
		String message;
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Delegator delegator = dctx.getDelegator();
		Locale locale = UtilHttp.getLocale(request);
		 Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String productTypeId = (String) request.getParameter("productTypeId");
		String primaryCategoryId = (String) request.getParameter("primaryCategoryId");
		String productCategoryId = (String) request.getParameter("productCategoryId");
	//	String[] productCategoryIds = request.getParameterValues("productCategoryId");
		String vatCategory = request.getParameter("vatCategory");
		String vatPurCategory = request.getParameter("vatPurCategory");
		//String materialCode = (String) request.getParameter("materialCode");
		String description = (String) request.getParameter("description");
		String productUOMtypeId = (String) request.getParameter("productUOMtypeId");
		String longDescription = (String) request.getParameter("specification");
		String facilityId = (String) request.getParameter("facilityId");
		String prodAttribute = (String) request.getParameter("attributeName");
		String attributeValue = (String) request.getParameter("attributeValue");
		/*if(UtilValidate.isNotEmpty(materialCode)){
		try
		{
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("internalName", EntityOperator.EQUALS, materialCode));
			EntityConditionList condition = EntityCondition.makeCondition(conditionList, EntityOperator.OR);
			List internalNameList = delegator.findList("Product", condition, null, null, null, false);
			if(UtilValidate.isNotEmpty(internalNameList))
			{
				request.setAttribute("_ERROR_MESSAGE_", "Material Code Already Exists  !" );	
				return "error";
			}
		}
		catch(Exception e)
			{
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());	
			return "error";
			}
		}*/
		/*List materialCategoryList = new ArrayList();
		if(UtilValidate.isNotEmpty(productCategoryIds)){
			for(int i=0;i<productCategoryIds.length;i++)
			{
				materialCategoryList.add(productCategoryIds[i]);
			}
		}*/
		try{
			String materialCode = "";
			result = dispatcher.runSync("getNextProductSeqID", UtilMisc.toMap("userLogin", userLogin));
			if (ServiceUtil.isError(result)) {
				request.setAttribute("_ERROR_MESSAGE_", "Error creating new product Sequence !" );	
				return "error";
			}
			if(UtilValidate.isNotEmpty(result)){
				materialCode = (String)result.get("internalName");
			}
			Map newProductMap = FastMap.newInstance();
			newProductMap.put("productTypeId", productTypeId);
			newProductMap.put("primaryCategoryId", primaryCategoryId);
			newProductMap.put("vatCategory", vatCategory);
			newProductMap.put("vatPurCategory", vatPurCategory);
			newProductMap.put("materialCode", materialCode);
			newProductMap.put("description", description);
			newProductMap.put("productUOMtypeId", productUOMtypeId);	
			newProductMap.put("longDescription", longDescription);
			newProductMap.put("facilityId", facilityId);		
			newProductMap.put("prodAttribute",prodAttribute);
			newProductMap.put("attributeValue",attributeValue);
			newProductMap.put("userLogin", userLogin);
			newProductMap.put("productCategoryId", productCategoryId);
			result = dispatcher.runSync("createNewProduct", newProductMap);
			if (ServiceUtil.isError(result)) {
				request.setAttribute("_ERROR_MESSAGE_", "Error creating new product  !" );	
				return "error";
			}
		}
			catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e, module);
				request.setAttribute("_ERROR_MESSAGE_", e);
	            return "error";		
	            }
		String productId = (String) result.get("productId");
	request.setAttribute("_EVENT_MESSAGE_", "Product sucessfully Added :"+productId);
    return "success";
	}
	public static Map<String, Object> createNewProduct(DispatchContext ctx, Map<String, Object> context) {
	LocalDispatcher dispatcher = ctx.getDispatcher();
    Delegator delegator = ctx.getDelegator();
	Map result = ServiceUtil.returnSuccess();

	String productTypeId = (String) context.get("productTypeId");
	String primaryCategoryId = (String) context.get("primaryCategoryId");
	String vatCategory = (String) context.get("vatCategory");
	String vatPurCategory = (String) context.get("vatPurCategory");
	String materialCode = (String) context.get("materialCode");
	String description = (String) context.get("description");
	String productUOMtypeId = (String) context.get("productUOMtypeId");
	String longDescription = (String) context.get("longDescription");
	GenericValue userLogin = (GenericValue) context.get("userLogin");
	String productCategoryId = (String) context.get("productCategoryId");
	String facilityId = (String) context.get("facilityId");
	String prodAttribute = (String) context.get("prodAttribute");
	String attributeValue = (String) context.get("attributeValue");
	String productId = null;
	try{
		Map newProduct = FastMap.newInstance();
		newProduct.put("userLogin", userLogin);
		newProduct.put("productTypeId", productTypeId);
		newProduct.put("primaryProductCategoryId", primaryCategoryId);
		newProduct.put("internalName", materialCode);
		newProduct.put("brandName", description);
		newProduct.put("productName", description);
		newProduct.put("description", description);
		newProduct.put("isVirtual", "N");
		newProduct.put("isVariant", "Y");
		newProduct.put("quantityIncluded",new BigDecimal(1));
		if(!productUOMtypeId.equals(null) || UtilValidate.isNotEmpty(productUOMtypeId))
		{	newProduct.put("quantityUomId", productUOMtypeId);	}
		newProduct.put("longDescription", longDescription);
		if(!facilityId.equals(null) || UtilValidate.isNotEmpty(facilityId))
		{	newProduct.put("facilityId", facilityId);	}
		result = dispatcher.runSync("createProduct", newProduct);
		productId = (String)result.get("productId");
		if(UtilValidate.isEmpty(materialCode)){
			GenericValue createdProduct = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			createdProduct.set("internalName", productId);
			createdProduct.store();
			}
		if (ServiceUtil.isError(result)) {
			return ServiceUtil.returnError("Error Occurred While Creating Product");
		}
	}
	catch (Exception e) {
		// TODO: handle exception
		return ServiceUtil.returnError(e.getMessage());
        }
//------------------------------------Updating the ProductCategoryMember Entity	
		Map productCatgMap = FastMap.newInstance();
	if(UtilValidate.isNotEmpty(productCategoryId)){
		try{
			productCatgMap.put("productCategoryId", productCategoryId);
			productCatgMap.put("productId", productId);
			productCatgMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
			productCatgMap.put("userLogin", userLogin);
			result = dispatcher.runSync("addProductToCategory", productCatgMap);
		}
		catch(Exception e){
			return ServiceUtil.returnError(e.getMessage());
			}
	}
	try{
		productCatgMap.put("productCategoryId", primaryCategoryId);
		productCatgMap.put("productId", productId);
		productCatgMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
		productCatgMap.put("userLogin", userLogin);
		result = dispatcher.runSync("addProductToCategory", productCatgMap);
	if (ServiceUtil.isError(result)) {
		return ServiceUtil.returnError("Error Occurred While updating Product Category");
			}
		if(UtilValidate.isNotEmpty(vatCategory)){
			productCatgMap.put("productCategoryId",vatCategory);
			productCatgMap.put("productId", productId);
			productCatgMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
			productCatgMap.put("userLogin", userLogin);
			result = dispatcher.runSync("addProductToCategory", productCatgMap);
			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("Error Occurred While updating Product Category");
				}
		  }
		if(UtilValidate.isNotEmpty(vatPurCategory)){
			productCatgMap.put("productCategoryId",vatPurCategory);
			productCatgMap.put("productId", productId);
			productCatgMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
			productCatgMap.put("userLogin", userLogin);
			result = dispatcher.runSync("addProductToCategory", productCatgMap);
			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("Error Occurred While updating Product Category");
				}
		  }
		
		}
catch(Exception e){
		return ServiceUtil.returnError(e.getMessage());
		}
	
//------------------------------------------------Updating the ProductFacility Entity	
	if(!facilityId.equals(null) && UtilValidate.isNotEmpty(facilityId)){
		try{
		Map productFacilityMap = FastMap.newInstance();
		productFacilityMap.put("productId",productId);
		productFacilityMap.put("facilityId",facilityId);
		productFacilityMap.put("userLogin", userLogin);
		result = dispatcher.runSync("createProductFacility", productFacilityMap);
		if (ServiceUtil.isError(result)) {
			return ServiceUtil.returnError("Error Occurred While updating Product Facility");
			}
		}
	catch(Exception e){
		return ServiceUtil.returnError(e.getMessage());
		}
	}
	//--------------------------------------------------------Updating the ProductAttribute Entity
	if(!attributeValue.equals(null) && UtilValidate.isNotEmpty(attributeValue)){
		try{
		Map productAttributeMap = FastMap.newInstance();
		productAttributeMap.put("productId",productId);
		productAttributeMap.put("attrName",prodAttribute);
		productAttributeMap.put("attrValue",attributeValue);
		productAttributeMap.put("userLogin", userLogin);
		result = dispatcher.runSync("createProductAttribute", productAttributeMap);
		if (ServiceUtil.isError(result)) {
			return ServiceUtil.returnError("Error Occurred While updating Product Attribute");
			}
		}
	catch(Exception e){
		return ServiceUtil.returnError(e.getMessage());
		}
	}
		result.put("productId",productId);
	return result;
	}
	public static Map<String, Object> updateProductDetails(DispatchContext ctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = ctx.getDispatcher();
	    Delegator delegator = ctx.getDelegator();
		Map result = ServiceUtil.returnSuccess();
        BigDecimal minimumStock = BigDecimal.ZERO;

		String longDescription = (String) context.get("longDescription");
		String facilityId = (String) context.get("facilityId");
		 minimumStock = (BigDecimal) context.get("minimumStock");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productId = (String) context.get("productId");
		String productName = (String) context.get("productName");
		String attrValue = (String) context.get("attrValue");
		String attrName = (String) context.get("attrName");
		String quantityUomId = (String) context.get("quantityUomId");
		String brandName = (String) context.get("brandName");
		String description = (String) context.get("description");
		Map<String, Object> inputMap = FastMap.newInstance();
		
		try{
			inputMap.put("userLogin",userLogin);
			inputMap.put("productId",productId);
			inputMap.put("productName", productName);
			inputMap.put("brandName", brandName);
			inputMap.put("description", description);
			inputMap.put("quantityUomId", quantityUomId);
			inputMap.put("longDescription", longDescription);
			result = dispatcher.runSync("updateProduct", inputMap);
			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("Error Occurred While updating Product.!");
				}
			}
		catch(Exception e){
				return ServiceUtil.returnError(e.getMessage());
			}
		if(UtilValidate.isNotEmpty(attrValue)){
			try{
					inputMap.clear();
					inputMap.put("userLogin",userLogin);
					inputMap.put("productId",productId);
					inputMap.put("attrValue", attrValue);
					inputMap.put("attrName", attrName);
					
					GenericValue productAttr = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName",attrName), false);
					if(UtilValidate.isNotEmpty(productAttr)){
						try{
							result = dispatcher.runSync("updateProductAttribute", inputMap);
							if (ServiceUtil.isError(result)) {
								return ServiceUtil.returnError("Error While updating the Product Attribute.!");
								}
						}catch(Exception e){
							return ServiceUtil.returnError(e.getMessage());
						}
					}else{
						try{
							result = dispatcher.runSync("createProductAttribute", inputMap);
							if (ServiceUtil.isError(result)) {
								return ServiceUtil.returnError("Error While creating the Product Attribute.!");
								}
						}catch(Exception e){
							return ServiceUtil.returnError(e.getMessage());
						}
					}
				
				}
			catch(Exception e){
					return ServiceUtil.returnError(e.getMessage());
				}
		}	
		if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(facilityId) && UtilValidate.isNotEmpty(minimumStock)){
			try{
				if (minimumStock.compareTo(BigDecimal.ZERO)<0 ){
				return ServiceUtil.returnError("Can not Update -ve values for minimum quantity ..!");
				}
				GenericValue ProductFacilityUpdate = delegator.findOne("ProductFacility",UtilMisc.toMap("productId", productId ,"facilityId",facilityId), false);
	            ProductFacilityUpdate.set("minimumStock", minimumStock);
				ProductFacilityUpdate.store();
	    }catch(GenericEntityException e){
	    	
			return ServiceUtil.returnError(e.getMessage());
	      }
		}
		result = ServiceUtil.returnSuccess("Product Id: "+productId+" Successfully Updated..!");
		return result;
	}	
		

	
	
	public static Map<String, Object>  createWeaver(DispatchContext dctx, Map<String, ? extends Object> context)  {
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        Locale locale = (Locale) context.get("locale");
        String contactMechId ="";
        String groupName = (String) context.get("groupName");
        String panId = (String) context.get("USER_PANID");
       // String serviceTax = (String) context.get("USER_SERVICETAXNUM");
        String tinNumber= (String) context.get("USER_TINNUMBER");
        String cstNumber = (String) context.get("USER_CSTNUMBER");
        String adharNumber = (String) context.get("USER_ADHNUMBER");
        String address1 = (String) context.get("address1");
        String address2 = (String) context.get("address2");
        String city = (String) context.get("city");
        String postalCode = (String) context.get("postalCode");
		String email = (String) context.get("emailAddress");
		String mobileNumber = (String) context.get("mobileNumber");
		String contactNumber =(String)context.get("contactNumber");
		String countryGeoId = (String) context.get("countryGeoId");
		String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
		String partyIdFrom = (String) context.get("productStoreId");
		String partyClassificationTypeId = (String) context.get("partyClassificationTypeId");
        String Depot= (String) context.get("Depot");
        String daoDateStr= (String) context.get("daoDate");
        String passBookIssueDateStr= (String) context.get("passBookIssueDate");
        String firstName= (String) context.get("firstName");
        String midName= (String) context.get("midName");
        String lastName= (String) context.get("lastName");
        String weaverCode= (String) context.get("weaverCode");
        String passBook= (String) context.get("passBook");
       /* String accName= (String) context.get("accName");
        String accNo= (String) context.get("accNo");
        String accBranch= (String) context.get("accBranch");
        String IfscCode= (String) context.get("IfscCode");
     */   BigDecimal CottonAbove= (BigDecimal) context.get("COTTON_40ABOVE");
        BigDecimal CottonUpto= (BigDecimal) context.get("COTTON_UPTO40");
        BigDecimal silkYarn= (BigDecimal) context.get("SILK_YARN");
        BigDecimal WoolST= (BigDecimal) context.get("WOOLYARN_10STO39NM");
        BigDecimal WoolSNM= (BigDecimal) context.get("WOOLYARN_40SNMABOVE");
        BigDecimal Woolbelow= (BigDecimal) context.get("WOOLYARN_BELOW10NM");
        String salutation= (String) context.get("salutation");
        String gender= (String) context.get("gender");
        Map<String, Object> inMap = FastMap.newInstance();
        Map<String, Object> outMap = FastMap.newInstance();
		String roleTypeId = (String) context.get("roleTypeId");	
		String bankName = (String) context.get("bankName");		
		String branch = (String) context.get("branch");		
		String ifscCode = (String) context.get("ifscCode");		
		String accNo = (String) context.get("accNo");
		String accName = (String) context.get("accName");

		
		
		
		/*String tenantId = (String) context.get("tenantId");
        
        delegator = DelegatorFactory.getDelegator("default#" + tenantId);
        dispatcher = dispatcher.getLocalDispatcher("materialmgmt#"+tenantId, delegator);*/
        Map loomsMap = FastMap.newInstance();
		if (CottonAbove.compareTo(BigDecimal.ZERO) > 0) {
			loomsMap.put("COTTON_40ABOVE",CottonAbove);
        }
		if (CottonUpto.compareTo(BigDecimal.ZERO) > 0) {
			loomsMap.put("COTTON_UPTO40",CottonUpto);

        }
		if (silkYarn.compareTo(BigDecimal.ZERO) > 0) {
			loomsMap.put("SILK_YARN",silkYarn);

        }
		if (WoolST.compareTo(BigDecimal.ZERO) > 0) {
			loomsMap.put("WOOLYARN_10STO39NM",WoolST);

        }
		if (WoolSNM.compareTo(BigDecimal.ZERO) > 0) {
			loomsMap.put("WOOLYARN_40SNMABOVE",WoolSNM);

        }
		if (Woolbelow.compareTo(BigDecimal.ZERO) > 0) {
			loomsMap.put("WOOLYARN_BELOW10NM",Woolbelow);

        }		
		
		if(partyClassificationTypeId.equals("INDIVIDUAL_WEAVERS")){	        
		     // lets Create party 
				Map inPartyMap = UtilMisc.toMap("userLogin", userLogin);
				inPartyMap.put("salutation", salutation);
				inPartyMap.put("firstName",firstName);
				inPartyMap.put("middleName", midName);
				inPartyMap.put("lastName", lastName);
				inPartyMap.put("gender", gender);
				inPartyMap.put("partyId", weaverCode);
				try{            	
					Map resultMap = dispatcher.runSync("createPerson", inPartyMap);
					if (ServiceUtil.isError(resultMap)) {
	  					String errMsg =  ServiceUtil.getErrorMessage(resultMap);
	  					Debug.logError(errMsg , module);
	  					return ServiceUtil.returnError(errMsg);
	                 }
					partyId = (String)resultMap.get("partyId");
					
	            }catch (GenericServiceException e) {
	             Debug.logError(e, module);
	             return ServiceUtil.returnError("Service Exception: " + e.getMessage());
	          }
				//Debug.log("partyId============================"+partyId);
		
		}else{
			try {
				 outMap=dispatcher.runSync("createPartyGroup", UtilMisc.toMap("groupName",groupName,"partyId",weaverCode,"userLogin", userLogin));
				if (ServiceUtil.isError(outMap)) {
	  		  		String errMsg =  ServiceUtil.getErrorMessage(outMap);
	  		  		Debug.logError(errMsg , module);
	  		  	}
	            partyId = (String) outMap.get("partyId");
			}catch(GenericServiceException e){
		  		Debug.logError(e, e.toString(), module);
		  		return ServiceUtil.returnError(e.toString());
	  		}

		}
		
		 // Create Party Role
        inMap.clear();
        inMap.put("userLogin", userLogin);
        inMap.put("partyId", partyId);
        inMap.put("roleTypeId",roleTypeId);
        try{
            outMap = dispatcher.runSync("createPartyRole", inMap);
            if(ServiceUtil.isError(outMap)){
           	 	Debug.logError("failed service create party role:"+ServiceUtil.getErrorMessage(outMap), module);
            }
	    }catch(GenericServiceException e){
	  		Debug.logError(e, e.toString(), module);
	  		return ServiceUtil.returnError(e.toString());
  		}
        
        
		 // Create Postal Address And Contact Mech Purpose
        inMap.clear();
        String postalContactId = null;
        inMap.put("partyId", partyId);
        inMap.put("address1", address1);
        inMap.put("address2", address2);
        if(UtilValidate.isEmpty(address2)){
        	inMap.put("address2", address1);
        }
        inMap.put("city", city);
        inMap.put("countryGeoId", countryGeoId);
        if(UtilValidate.isNotEmpty(stateProvinceGeoId)){
        	inMap.put("stateProvinceGeoId", stateProvinceGeoId);
        }
        if(UtilValidate.isNotEmpty(postalCode)){
        	inMap.put("postalCode", postalCode);
        }
        inMap.put("userLogin", userLogin);

        //inMap.put("geoPointId", geoPointId);
        inMap.put("fromDate", UtilDateTime.nowTimestamp());
        //String contactMechId = null;
        try{
        	outMap = dispatcher.runSync("createPartyPostalAddress", inMap);
            if(ServiceUtil.isError(outMap)){
           	 	Debug.logError("faild service create party postal Address:"+ServiceUtil.getErrorMessage(outMap), module);
           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
            }
            contactMechId = (String) outMap.get("contactMechId");
	    }catch(GenericServiceException e){
	  		Debug.logError(e, e.toString(), module);
	  		return ServiceUtil.returnError(e.toString());
  		}
        
        inMap.clear();
//        inMap.put("userLogin", userLoginToRunAs);
        inMap.put("userLogin", userLogin);
        inMap.put("contactMechId", contactMechId);
        inMap.put("partyId", partyId);
        inMap.put("contactMechPurposeTypeId", "BILLING_LOCATION");
        try{
        	outMap = dispatcher.runSync("createPartyContactMechPurpose", inMap);
            if(ServiceUtil.isError(outMap)){
           	 	Debug.logError("failed service create party postal Address contactmech Purpose:"+ServiceUtil.getErrorMessage(outMap), module);
           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
            }
        }catch(GenericServiceException e){
	  		Debug.logError(e, e.toString(), module);
	  		return ServiceUtil.returnError(e.toString());
  		}
        
        
        String taxContactMech = "";
        try{
        List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, stateProvinceGeoId));
		List<GenericValue> ContactMech = delegator.findList("ContactMech", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		
		GenericValue ContactMech1 = EntityUtil.getFirst(ContactMech);
		
		taxContactMech = ContactMech1.getString("contactMechId");
        }catch(Exception e){
	  		Debug.logError(e, e.toString(), module);
	  		return ServiceUtil.returnError(e.toString());
  		}
		//Debug.log("taxContactMech=================="+taxContactMech);
		
        inMap.clear();
        inMap.put("userLogin", userLogin);
        inMap.put("partyId", partyId);
        inMap.put("contactMechId", taxContactMech);
        inMap.put("fromDate",  UtilDateTime.nowTimestamp());
        
        try{
        	outMap = dispatcher.runSync("createPartyContactMech", inMap);
            if(ServiceUtil.isError(outMap)){
           	 	Debug.logError("faild service create party postal Address:"+ServiceUtil.getErrorMessage(outMap), module);
           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
            }
	    }catch(GenericServiceException e){
	  		Debug.logError(e, e.toString(), module);
	  		return ServiceUtil.returnError(e.toString());
  		}
        
        
        
        // create phone number
        inMap.clear();
//        inMap.put("userLogin", userLoginToRunAs);
        inMap.put("userLogin", userLogin);
        inMap.put("contactNumber",mobileNumber);
        inMap.put("contactMechPurposeTypeId","PRIMARY_PHONE");
        inMap.put("partyId", partyId);
        try{
        	outMap = dispatcher.runSync("createPartyTelecomNumber", inMap);
            if(ServiceUtil.isError(outMap)){
           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
            }
        }catch(GenericServiceException e){
	  		Debug.logError(e, e.toString(), module);
	  		return ServiceUtil.returnError(e.toString());
  		}
        
        //Create PartyClassification===============
        Map inPartyMapClass = UtilMisc.toMap("userLogin", userLogin);
        inPartyMapClass.put("partyClassificationGroupId", partyClassificationTypeId);
        inPartyMapClass.put("partyId", partyId);
		try{            	
			Map resultMap = dispatcher.runSync("createPartyClassification", inPartyMapClass);
			if (ServiceUtil.isError(resultMap)) {
					String errMsg =  ServiceUtil.getErrorMessage(resultMap);
					Debug.logError(errMsg , module);
					return ServiceUtil.returnError(errMsg);
             }
			//partyId = (String)resultMap.get("partyId");
			
        }catch (GenericServiceException e) {
         Debug.logError(e, module);
         return ServiceUtil.returnError("Service Exception: " + e.getMessage());
      }
        
        // create landLine number
        inMap.clear();
//        inMap.put("userLogin", userLoginToRunAs);
        inMap.put("userLogin", userLogin);
        inMap.put("contactNumber",contactNumber);
        inMap.put("contactMechPurposeTypeId","PHONE_HOME");
        inMap.put("partyId", partyId);
        try{
        	outMap = dispatcher.runSync("createPartyTelecomNumber", inMap);
            if(ServiceUtil.isError(outMap)){
           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
            }
        }catch(GenericServiceException e){
	  		Debug.logError(e, e.toString(), module);
	  		return ServiceUtil.returnError(e.toString());
  		}
        
        // Create Party Email
        if(UtilValidate.isNotEmpty(email)){
	        inMap.clear();
	        //inMap.put("userLogin", userLoginToRunAs);
	        inMap.put("userLogin", userLogin);
	        inMap.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
	        inMap.put("emailAddress", email);
	        inMap.put("partyId", partyId);
	        inMap.put("verified", "Y");
	        inMap.put("fromDate", UtilDateTime.nowTimestamp());
	        try{
	        	outMap = dispatcher.runSync("createPartyEmailAddress", inMap);
	            if(ServiceUtil.isError(outMap)){
	           	 	Debug.logError("faild service create party Email:"+ServiceUtil.getErrorMessage(outMap), module);
	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
	            }
	        }catch(GenericServiceException e){
		  		Debug.logError(e, e.toString(), module);
		  		return ServiceUtil.returnError(e.toString());
	  		}
        }
     try{
		     if(UtilValidate.isNotEmpty(panId)){
		       	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","PAN_NUMBER","idValue",panId,"partyId",partyId,"userLogin", context.get("userLogin")));
		  	  }
		     Timestamp issueDate = null;
		     if(UtilValidate.isNotEmpty(passBook)){
		    	 if(UtilValidate.isNotEmpty(context.get("passBookIssueDate"))){
		    		  SimpleDateFormat SimpleDF1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					  if(UtilValidate.isNotEmpty(passBookIssueDateStr)){
					  		try {
					  			issueDate = new java.sql.Timestamp(SimpleDF1.parse(passBookIssueDateStr).getTime());

						  	} catch (ParseException e) {
						  		Debug.logError(e, "Cannot parse date string: " + passBookIssueDateStr, module);
						  	} catch (NullPointerException e) {
					  			Debug.logError(e, "Cannot parse date string: " + passBookIssueDateStr, module);
						  	}
				  	   }
				 }else{
					 issueDate= UtilDateTime.nowTimestamp();
				 }
		        dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","PSB_NUMER","idValue",passBook,"partyId",partyId,"issueDate",issueDate,"userLogin", context.get("userLogin")));
		 	  }else{
			    dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","PSB_NUMER","idValue","_NA_","partyId",partyId,"userLogin", context.get("userLogin")));
		 	  }
		     if(UtilValidate.isNotEmpty(adharNumber)){
	      	     dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","ADR_NUMBER","idValue",adharNumber,"partyId",partyId,"userLogin", context.get("userLogin")));
		     }
		     if(UtilValidate.isNotEmpty(tinNumber)){
		         dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","TIN_NUMBER","idValue",tinNumber,"partyId",partyId,"userLogin", context.get("userLogin")));
		     }
		     if(UtilValidate.isNotEmpty(cstNumber)){
		         dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","CST_NUMBER","idValue",cstNumber,"partyId",partyId,"userLogin", context.get("userLogin")));
		   	 }
		    
        }catch(GenericServiceException e){
	  		Debug.logError(e, e.toString(), module);
	  		return ServiceUtil.returnError(e.toString());
  		}
        
        inMap.clear();
        inMap.put("userLogin", userLogin);
		inMap.put("partyIdFrom",partyIdFrom);
		inMap.put("partyIdTo",partyId);
		inMap.put("roleTypeIdFrom","ORGANIZATION_UNIT");
		inMap.put("partyRelationshipTypeId","BRANCH_CUSTOMER");
		inMap.put("roleTypeIdTo",roleTypeId);
        inMap.put("fromDate", UtilDateTime.nowTimestamp());
        try {
        	outMap= dispatcher.runSync("createPartyRelationship", inMap); // Create new one
        	 if(ServiceUtil.isError(outMap)){
            	 	Debug.logError("faild service create party RelationShip:"+ServiceUtil.getErrorMessage(outMap), module);
            	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
             }
        } catch (GenericServiceException e) {
        	Debug.logError(e, e.toString(), module);
	  		return ServiceUtil.returnError(e.toString());
        }
        Iterator entries = loomsMap.entrySet().iterator();
        
        
        Map<String,Integer> loomsTypeMap = FastMap.newInstance();
        
        loomsTypeMap.put("COTTON_40ABOVE",10);
        loomsTypeMap.put("COTTON_UPTO40",30);
        loomsTypeMap.put("SILK_YARN",4);
        loomsTypeMap.put("WOOLYARN_10STO39NM",10);
        loomsTypeMap.put("WOOLYARN_40SNMABOVE",10);
        loomsTypeMap.put("WOOLYARN_BELOW10NM",10);
        
        
        Timestamp targetDate =null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");  
			targetDate = new java.sql.Timestamp(sdf.parse("01 APRIL, 2016").getTime());
		} catch (Exception e) {
			Debug.logError(e, "Failed to covert date ", module);
			return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
		}
        
        
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String)entry.getKey();
            BigDecimal value = (BigDecimal)entry.getValue();
           Debug.log("Key = " + key + ", Value = " + value);
           
           
           int noOf = Integer.valueOf(loomsTypeMap.get(key));
           
           BigDecimal quotaPerLoom = value.multiply(new BigDecimal(noOf));
           
       /* // Create Party Loom
           inMap.clear();
           inMap.put("userLogin", userLogin);
           inMap.put("partyId", partyId);
           inMap.put("loomTypeId",key);
           inMap.put("quantity",value);
           try{
               outMap = dispatcher.runSync("createPartyLoom", inMap);
               if(ServiceUtil.isError(outMap)){
              	 	Debug.logError("failed service create party loom:"+ServiceUtil.getErrorMessage(outMap), module);
               }
   	    }catch(GenericServiceException e){
   	  		Debug.logError(e, e.toString(), module);
   	  		return ServiceUtil.returnError(e.toString());
     		}*/
           
           
           
           if(UtilValidate.isNotEmpty(partyId)){
   	        try{
   				GenericValue PartyLoom = delegator.makeValue("PartyLoom");
   				PartyLoom.set("partyId", partyId);
   				PartyLoom.set("loomTypeId", key);
   				PartyLoom.set("quantity", value);
   				PartyLoom.set("fromDate", targetDate);
   				PartyLoom.set("quotaPerLoom", quotaPerLoom);
   				
   					delegator.createOrStore(PartyLoom);
   			}catch (Exception e) {
   				Debug.logError(e, module);
   				return ServiceUtil.returnError("Error while creating  Bank Details" + e);	
   			}
           }
           
           
           
           
           
           
           
        }
		SimpleDateFormat SimpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(Depot.equals("Y")){
        try{
	        	inMap.clear();
	        	inMap.put("userLogin",userLogin);
	        	inMap.put("ownerPartyId",partyId);
	        	inMap.put("facilityTypeId","DEPOT_SOCIETY");
				String customerName=org.ofbiz.party.party.PartyHelper.getPartyName(delegator,partyId, false);
				if(UtilValidate.isNotEmpty(customerName)){
					inMap.put("facilityName",customerName);
					inMap.put("description",customerName);
				}else{
					inMap.put("facilityName","");
					inMap.put("description","");
				}
	        	
	        	//Timestamp openedDate = UtilDateTime.nowTimestamp();
			 if(UtilValidate.isNotEmpty(context.get("daoDate"))){
				 Timestamp openedDate = null;

				 if(UtilValidate.isNotEmpty(daoDateStr)){
			  		try {
			  			openedDate = new java.sql.Timestamp(SimpleDF.parse(daoDateStr).getTime());
				  	} catch (ParseException e) {
				  		Debug.logError(e, "Cannot parse date string: " + daoDateStr, module);
				  	} catch (NullPointerException e) {
			  			Debug.logError(e, "Cannot parse date string: " + daoDateStr, module);
				  	}
			  	}
				 inMap.put("openedDate",openedDate);	
			 }
			 Map resultFacilityMap =  dispatcher.runSync("createFacility", inMap);
				 if (ServiceUtil.isError(resultFacilityMap)) {
					 Debug.logError(ServiceUtil.getErrorMessage(resultFacilityMap), module);
		            return resultFacilityMap;
		        }
        }catch(GenericServiceException e){
   	  		Debug.logError(e, e.toString(), module);
   	  		return ServiceUtil.returnError(e.toString());
     		}
        
        
        }
        
        
        if(UtilValidate.isNotEmpty(partyId)){
	        try{
				GenericValue BankAccount = delegator.makeValue("BankAccount");
					BankAccount.set("bankAccountName", accName);
					BankAccount.set("bankAccountCode", accNo);
					BankAccount.set("branchCode", ifscCode);
					BankAccount.set("ifscCode", ifscCode);
					BankAccount.set("ownerPartyId", partyId);
					delegator.createSetNextSeqId(BankAccount);
			}catch (Exception e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error while creating  Bank Details" + e);	
			}
        }
        
        
        
        
        
        result.put("partyId",partyId);
			
		return result;
	}
		
	public static Map<String, Object>  createTransporter(DispatchContext dctx, Map<String, ? extends Object> context)  {
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        Locale locale = (Locale) context.get("locale");
        String contactMechId ="";
        String groupName = (String) context.get("groupName");
        List<String> productStoreList = (List) context.get("productStoreId");
        String panId = (String) context.get("USER_PANID");
        String serviceTax = (String) context.get("USER_SERVICETAXNUM");
        String tinNumber= (String) context.get("USER_TINNUM");
        String cstNumber = (String) context.get("USER_CSTNUM");
        String adharNum = (String) context.get("ADR_NUMBER");
        String address1 = (String) context.get("address1");
        String address2 = (String) context.get("address2");
        String city = (String) context.get("city");
        String postalCode = (String) context.get("postalCode");
		String email = (String) context.get("emailAddress");
		String AltemailAddress = (String) context.get("AltemailAddress");
		String mobileNumber = (String) context.get("mobileNumber");
		String contactNumber =(String)context.get("contactNumber");
		String countryCode = (String) context.get("countryCode");
		String roleTypeId = (String) context.get("roleTypeId");
		String accName= (String) context.get("accName");
        String accNo= (String) context.get("accNo");
        String accBranch= (String) context.get("accBranch");
        String IfscCode= (String) context.get("IfscCode");
		String suppRole = (String) context.get("suppRole");
		String personalDetail = (String) context.get("personalDetailsId");

		
	/*	JSONObject personalDetailMap = null;
        if (UtilValidate.isNotEmpty(personalDetail)) {
        	personalDetailMap = new JSONObject();
            // Transform JSON String to Object
        	personalDetailMap = (JSONObject) JSONSerializer.toJSON(personalDetail);
        }*/
		
		Map<String, Object> outMap = FastMap.newInstance();
		
		try {
			result=dispatcher.runSync("createPartyGroup", UtilMisc.toMap("groupName",groupName,"userLogin", context.get("userLogin")));
			if (ServiceUtil.isError(result)) {
  		  		String errMsg =  ServiceUtil.getErrorMessage(result);
  		  		Debug.logError(errMsg , module);
  		  	}
            partyId = (String) result.get("partyId");
            if(UtilValidate.isNotEmpty(partyId)){
            	String defaultRoleType = "TRANSPORT_CONTRACTOR";
            	Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId", defaultRoleType);
            	Map<String, Object>  resultMap = dispatcher.runSync("createPartyRole", input);
     			if (ServiceUtil.isError(resultMap)) {
     				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                     return resultMap;
                }
     			/*input.clear();
            	input = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId", roleTypeId);
            	resultMap = dispatcher.runSync("createPartyRole", input);
     			if (ServiceUtil.isError(resultMap)) {
     				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                     return resultMap;
                }*/
     			
     			List conditionList = FastList.newInstance();
     			List<GenericValue> ProductStoreList = FastList.newInstance();
     			try {
				conditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreList));
				ProductStoreList = delegator.findList("ProductStore", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("payToPartyId"), null, null, false);
     			}catch (Exception e) {
     	            Debug.logWarning(e.getMessage(), module);
     	            Debug.logError(e, "Error creating Group For Vendor",module);
     				return ServiceUtil.returnError("Error creating Group For Vendor :"+groupName);
     	        }
				List<String> branchIds = EntityUtil.getFieldListFromEntityList(ProductStoreList, "payToPartyId", true);

     			for (String eachBranch : branchIds) {
     				
     				Map inMap = FastMap.newInstance();
 	                inMap.put("userLogin", userLogin);
 	                inMap.put("partyId", partyId);
 	                inMap.put("partyIdFrom", eachBranch);
 	                inMap.put("partyIdTo", partyId);
 	                inMap.put("roleTypeIdTo","TRANSPORT_CONTRACTOR");
 	                inMap.put("roleTypeIdFrom","ORGANIZATION_UNIT");
 	                inMap.put("partyRelationshipTypeId","BRANCH_TRANSPORTER");
 	                outMap = dispatcher.runSync("createPartyRelationship", inMap);
 	                if(ServiceUtil.isError(outMap)){
 	               	 	Debug.logError("failed service create party relationship:"+ServiceUtil.getErrorMessage(outMap), module);
 	                }
     				
				}
     			
     			if (UtilValidate.isNotEmpty(address1)){
     				input.clear();
    				input = UtilMisc.toMap("userLogin", userLogin, "partyId",partyId, "address1",address1, "address2", address2, "city", city, "stateProvinceGeoId", (String)context.get("stateProvinceGeoId"), "postalCode", postalCode);
    				resultMap =  dispatcher.runSync("createPartyPostalAddress", input);
    				if (ServiceUtil.isError(resultMap)) {
    					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
    	                return resultMap;
    	            }
    				contactMechId = (String) resultMap.get("contactMechId");
     				input.clear();
    				input = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "partyId",partyId, "contactMechPurposeTypeId", "BILLING_LOCATION");
    				resultMap =  dispatcher.runSync("createPartyContactMechPurpose", input);
    				if (ServiceUtil.isError(resultMap)) {
    				    Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
    	                return resultMap;
    	            }
    			 }
    			// create phone number
    			if (UtilValidate.isNotEmpty(mobileNumber)){
    				if (UtilValidate.isEmpty(countryCode)){
    					countryCode	="91";
    				}
    	            input.clear();
    	            input = UtilMisc.toMap("userLogin", userLogin, "contactNumber", mobileNumber,"countryCode",countryCode, "partyId",partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE");
    	            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
    	            if(ServiceUtil.isError(outMap)){
    	           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    	            }
    			}
                // create landLine number
    			if (UtilValidate.isNotEmpty(contactNumber)){
    	            input.clear();
    	            input = UtilMisc.toMap("userLogin", userLogin, "contactNumber", contactNumber, "partyId",partyId, "contactMechPurposeTypeId", "PHONE_HOME");
    	            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
    	            if(ServiceUtil.isError(outMap)){
    	           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    	            }
    			}
                // Create Party Email
    			if (UtilValidate.isNotEmpty(email)){
    	            input.clear();
    	            input = UtilMisc.toMap("userLogin", userLogin, "emailAddress", email, "partyId",partyId,"verified","Y", "fromDate",UtilDateTime.nowTimestamp(),"contactMechPurposeTypeId", "PRIMARY_EMAIL");
    	            outMap = dispatcher.runSync("createPartyEmailAddress", input);
    	            if(ServiceUtil.isError(outMap)){
    	           	 	Debug.logError("faild service create party Email:"+ServiceUtil.getErrorMessage(outMap), module);
    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    	            }
    			}
    			
    			if (UtilValidate.isNotEmpty(AltemailAddress)){
    	            input.clear();
    	            input = UtilMisc.toMap("userLogin", userLogin, "emailAddress", AltemailAddress, "partyId",partyId,"verified","Y", "fromDate",UtilDateTime.nowTimestamp(),"contactMechPurposeTypeId", "PRIMARY_EMAIL");
    	            outMap = dispatcher.runSync("createPartyEmailAddress", input);
    	            if(ServiceUtil.isError(outMap)){
    	           	 	Debug.logError("faild service create party Email:"+ServiceUtil.getErrorMessage(outMap), module);
    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    	            }
    			}
     			//===============================================================
    			if(UtilValidate.isNotEmpty(suppRole)){
	    			try{
						GenericValue PartyClassification = delegator.makeValue("PartyClassification");
	    				PartyClassification.set("partyId", partyId);
		    			PartyClassification.set("partyClassificationGroupId", suppRole);
		                PartyClassification.set("fromDate", UtilDateTime.nowTimestamp());
						delegator.create(PartyClassification);
					}catch (Exception e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError("Error while creating  PartyClassification" + e);	
					}
    			}
    			
	            if(UtilValidate.isNotEmpty(panId)){
	            	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","PAN_NUMBER","idValue",panId,"partyId",partyId,"userLogin", context.get("userLogin")));
	       	    }
	            if(UtilValidate.isNotEmpty(serviceTax)){
	           	     dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","SERVICETAX_NUMBER","idValue",serviceTax,"partyId",partyId,"userLogin", context.get("userLogin")));
	      	    }
	            if(UtilValidate.isNotEmpty(tinNumber)){
	              	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","TIN_NUMBER","idValue",tinNumber,"partyId",partyId,"userLogin", context.get("userLogin")));
	         	}
	            if(UtilValidate.isNotEmpty(cstNumber)){
	             	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","CST_NUMBER","idValue",cstNumber,"partyId",partyId,"userLogin", context.get("userLogin")));
	        	}
	            if(UtilValidate.isNotEmpty(adharNum)){
	             	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","ADR_NUMBER","idValue",adharNum,"partyId",partyId,"userLogin", context.get("userLogin")));
	        	}
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e.getMessage(), module);
            Debug.logError(e, "Error creating Group For Vendor",module);
			return ServiceUtil.returnError("Error creating Group For Vendor :"+groupName);
        }
		result.put("partyId", partyId);
		return result;
	}
	public static Map<String, Object> updateIndentSummaryPO(DispatchContext ctx, Map<String, ? extends Object> context){ 
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String salesOrderId = (String)context.get("SalesOrder");
		String purchaseOrderId = (String)context.get("PurchaseOrder");
	  	boolean beganTransaction = false;
		try{
			Map<String, Object> orderDtlMap = FastMap.newInstance();
			orderDtlMap.put("orderId", salesOrderId);
			orderDtlMap.put("userLogin", userLogin);
			result = dispatcher.runSync("getOrderItemSummary",orderDtlMap);
			if(ServiceUtil.isError(result)){
				Debug.logError("Unable get Order item: " + ServiceUtil.getErrorMessage(result), module);
				return ServiceUtil.returnError(null, null, null,result);
			}
			Map productSummaryMap = (Map)result.get("productSummaryMap");
			Iterator eachProductIter = productSummaryMap.entrySet().iterator();
			try{
				beganTransaction = TransactionUtil.begin(7200);
			
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, purchaseOrderId), null, null, null, false);
				BigDecimal totalBedAmount = BigDecimal.ZERO;
				BigDecimal totalBedCessAmount = BigDecimal.ZERO;
				BigDecimal totalBedSecCessAmount = BigDecimal.ZERO;
				BigDecimal totalVatAmount = BigDecimal.ZERO;
				BigDecimal totalCstAmount = BigDecimal.ZERO;
				
				GenericValue orderItemValue = null;
				while(eachProductIter.hasNext()) {
					Map.Entry entry = (Entry)eachProductIter.next();
					String productId = (String)entry.getKey();
					Map prodQtyMap=(Map)entry.getValue();
					List taxList=FastList.newInstance();
					
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal unitPrice = BigDecimal.ZERO;
					BigDecimal unitListPrice = BigDecimal.ZERO;
					BigDecimal vatPercent = BigDecimal.ZERO;
					BigDecimal vatAmount = BigDecimal.ZERO;
					BigDecimal cstAmount = BigDecimal.ZERO;
					BigDecimal cstPercent = BigDecimal.ZERO;
					BigDecimal bedAmount = BigDecimal.ZERO;
					BigDecimal bedPercent = BigDecimal.ZERO;
					BigDecimal bedcessPercent = BigDecimal.ZERO;
					BigDecimal bedcessAmount = BigDecimal.ZERO;
					BigDecimal bedseccessAmount = BigDecimal.ZERO;
					BigDecimal bedseccessPercent = BigDecimal.ZERO;
					
					
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
						productId = (String)prodQtyMap.get("productId");
					}*/
					
					List<GenericValue> orderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					
					
					if(UtilValidate.isNotEmpty(orderItem)){
						orderItemValue = EntityUtil.getFirst(orderItem);
					}
					else{
						continue;
					}
					
					if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
						quantity = (BigDecimal)prodQtyMap.get("quantity");
						orderItemValue.put("quantity", quantity);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
						unitPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
						orderItemValue.put("unitPrice", unitPrice);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
						unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
						orderItemValue.put("unitListPrice", unitListPrice);
					}
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
						vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
						orderItemValue.put("vatAmount", vatAmount);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
						vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
						orderItemValue.put("vatPercent", vatPercent);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
						cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
						orderItemValue.put("cstAmount", cstAmount);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
						cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
						orderItemValue.put("cstPercent", cstPercent);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
						bedAmount = (BigDecimal)prodQtyMap.get("bedAmount");
						orderItemValue.put("bedAmount", bedAmount);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedPercent"))){
						bedPercent = (BigDecimal)prodQtyMap.get("bedPercent");
						orderItemValue.put("bedPercent", bedPercent);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessAmount"))){
						bedcessAmount = (BigDecimal)prodQtyMap.get("bedcessAmount");
						orderItemValue.put("bedcessAmount", bedcessAmount);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessPercent"))){
						bedcessPercent = (BigDecimal)prodQtyMap.get("bedcessPercent");
						orderItemValue.put("bedcessPercent", bedcessPercent);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessAmount"))){
						bedseccessAmount = (BigDecimal)prodQtyMap.get("bedseccessAmount");
						orderItemValue.put("bedseccessAmount", bedseccessAmount);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessPercent"))){
						bedseccessPercent = (BigDecimal)prodQtyMap.get("bedseccessPercent");
						orderItemValue.put("bedseccessPercent", bedseccessPercent);
					}
*/
					orderItemValue.set("changeByUserLoginId", userLogin.getString("userLoginId"));
					orderItemValue.set("changeDatetime", UtilDateTime.nowTimestamp());
					orderItemValue.store();
				}
/*				
				List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
				delegator.removeAll(orderAdjustments);
				
				for(Map eachAdj : otherChargesAdjustment){
					
					String adjustmentTypeId = (String)eachAdj.get("adjustmentTypeId");
					String applicableTo = (String)eachAdj.get("applicableTo");
					if(!applicableTo.equals("_NA_")){
						List<GenericValue> sequenceItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, applicableTo));
						if(UtilValidate.isNotEmpty(sequenceItems)){
							GenericValue sequenceItem = EntityUtil.getFirst(sequenceItems);
							applicableTo = sequenceItem.getString("orderItemSeqId");
						}
					}
					BigDecimal amount =(BigDecimal)eachAdj.get("amount");
					Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);
					adjustCtx.put("orderId", orderId);
			    	adjustCtx.put("orderItemSeqId", applicableTo);
			    	adjustCtx.put("orderAdjustmentTypeId", adjustmentTypeId);
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
				}*/
				Map resetTotalCtx = UtilMisc.toMap("userLogin",userLogin);	  	
				resetTotalCtx.put("orderId", purchaseOrderId);
				resetTotalCtx.put("userLogin", userLogin);
				Map resetMap=FastMap.newInstance();
	  	 		try{
	  	 			resetMap = dispatcher.runSync("resetGrandTotal",resetTotalCtx);  		  		 
		  	 		if (ServiceUtil.isError(resetMap)) {
		  	 			String errMsg =  ServiceUtil.getErrorMessage(resetMap);
		  	 			Debug.logError(errMsg , module);
		  	 			return ServiceUtil.returnError(" Error While reseting order totals for Purchase Order !"+purchaseOrderId);
		  	 		}
	  	 		}catch (Exception e) {
	  	 			Debug.logError(e, " Error While reseting order totals for Purchase Order !"+purchaseOrderId, module);
	  	 			return resetMap;			  
	  	 		}
	  	 		

		       /* if(UtilValidate.isNotEmpty(purchaseOrderId)){
			  		
			  		result = DepotHelperServices.getOrderItemAndTermsMapForCalculation(ctx, UtilMisc.toMap("userLogin", userLogin, "orderId", purchaseOrderId));
					Debug.log("resultMap==============11======================"+result);

			  		if (ServiceUtil.isError(result)) {
		  		  		String errMsg =  ServiceUtil.getErrorMessage(result);
		  		  		Debug.logError(errMsg , module);
		  	 			return ServiceUtil.returnError(" Error While getting Order Adjestment Details !"+errMsg);
		  		  	}
					List<Map> otherCharges = (List)result.get("otherCharges");
					List<Map> productQty = (List)result.get("productQty");
					result = DepotHelperServices.getMaterialItemValuationDetails(ctx, UtilMisc.toMap("userLogin", userLogin, "productQty", productQty, "otherCharges", otherCharges, "incTax", ""));
					if(ServiceUtil.isError(result)){
		  		  		String errMsg =  ServiceUtil.getErrorMessage(result);
		  		  		Debug.logError(errMsg , module);
		  	 			return ServiceUtil.returnError(" Error While getting getMaterialItemValuationDetails !"+errMsg);
					}
					List<Map> itemDetails = (List)result.get("itemDetail");
									
					
					
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
		 }catch(Exception e){
			 Debug.logError("-----------error-------------- : "+e.toString(), module);
		 } 
		result=ServiceUtil.returnSuccess("Depot reambursement Receipts added successfully !!");
	return result;
}

	
	public static Map<String, Object> updateWeaverDetails(DispatchContext ctx, Map<String, ? extends Object> context){ 
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String Depot = (String)context.get("Depot");
		String daoDateStr = (String)context.get("daoDate");
		String weaverId = (String)context.get("weaverId");
		if (UtilValidate.isEmpty(weaverId)){
			Debug.logError("Weaver Id is Empty !" , module);
			return ServiceUtil.returnError(" Weaver Id is Empty !");
		}
		SimpleDateFormat SimpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
		List conditionList = FastList.newInstance(); 
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, weaverId));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<GenericValue> facilities = delegator.findList("Facility", condition, null, null, null, false);
		    if(Depot.equals("Y")){
			    try{
					if(UtilValidate.isNotEmpty(facilities)){
							GenericValue facilitieEntity = EntityUtil.getFirst(facilities);
							if(UtilValidate.isNotEmpty(context.get("daoDate"))){
								 Timestamp openedDate = null;
									 if(UtilValidate.isNotEmpty(daoDateStr)){
								  		try {
								  			openedDate = new java.sql.Timestamp(SimpleDF.parse(daoDateStr).getTime());
									  	} catch (ParseException e) {
									  		Debug.logError(e, "Cannot parse date string: " + daoDateStr, module);
									  	} catch (NullPointerException e) {
								  			Debug.logError(e, "Cannot parse date string: " + daoDateStr, module);
									  	}
								  	}
							facilitieEntity.set("openedDate", openedDate);
							facilitieEntity.store();
						}
					}else{
						Map inMap = FastMap.newInstance();
						inMap.put("userLogin",userLogin);
			        	inMap.put("ownerPartyId",weaverId);
			        	inMap.put("facilityTypeId","DEPOT_SOCIETY");
						String customerName=org.ofbiz.party.party.PartyHelper.getPartyName(delegator,weaverId, false);
						if(UtilValidate.isNotEmpty(customerName)){
							inMap.put("facilityName",customerName);
							inMap.put("description",customerName);
						}else{
							inMap.put("facilityName","");
							inMap.put("description","");
						}
			        	
			        	//Timestamp openedDate = UtilDateTime.nowTimestamp();
						if(UtilValidate.isNotEmpty(context.get("daoDate"))){
						 Timestamp openedDate = null;
							 if(UtilValidate.isNotEmpty(daoDateStr)){
						  		try {
						  			openedDate = new java.sql.Timestamp(SimpleDF.parse(daoDateStr).getTime());
							  	} catch (ParseException e) {
							  		Debug.logError(e, "Cannot parse date string: " + daoDateStr, module);
							  	} catch (NullPointerException e) {
						  			Debug.logError(e, "Cannot parse date string: " + daoDateStr, module);
							  	}
						  	}
						 inMap.put("openedDate",openedDate);	
						}
						Map resultFacilityMap =  dispatcher.runSync("createFacility", inMap);
							 if (ServiceUtil.isError(resultFacilityMap)) {
								 Debug.logError(ServiceUtil.getErrorMessage(resultFacilityMap), module);
					            return resultFacilityMap;
					        }
					}
			    }catch(GenericServiceException e){
				  		Debug.logError(e, e.toString(), module);
				  		return ServiceUtil.returnError(e.toString());
			 	}
		    }else{
		    	delegator.removeAll(facilities);
		    }
		}catch (GenericEntityException e) {
			  Debug.logError(e, "Could not commit transaction for entity Facility", module);
		  }
		result=ServiceUtil.returnSuccess("updated successfully !!");
		result.put("partyId",weaverId);
		return result;
	}
	
	
	
	public static Map<String, Object> updateWeaverPartyClassification(DispatchContext ctx, Map<String, ? extends Object> context){ 
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String weaverId = (String)context.get("weaverId");
		String calssification = (String)context.get("calssification");
		
		if (UtilValidate.isEmpty(weaverId)){
			Debug.logError("Weaver Id is Empty !" , module);
			return ServiceUtil.returnError(" Weaver Id is Empty !");
		}
		SimpleDateFormat SimpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List conditionList = FastList.newInstance(); 
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, weaverId));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		
		List<GenericValue> PartyClassification = null;
		try{
		 PartyClassification = delegator.findList("PartyClassification", condition, null, null, null, false);

	      delegator.removeAll(PartyClassification);
	      
	      
	        Map inPartyMapClass = UtilMisc.toMap("userLogin", userLogin);
	        inPartyMapClass.put("partyClassificationGroupId", calssification);
	        inPartyMapClass.put("partyId", weaverId);
			try{            	
				Map resultMap = dispatcher.runSync("createPartyClassification", inPartyMapClass);
				if (ServiceUtil.isError(resultMap)) {
						String errMsg =  ServiceUtil.getErrorMessage(resultMap);
						Debug.logError(errMsg , module);
						return ServiceUtil.returnError(errMsg);
	             }
				//partyId = (String)resultMap.get("partyId");
				
	        }catch (GenericServiceException e) {
	         Debug.logError(e, module);
	         return ServiceUtil.returnError("Service Exception: " + e.getMessage());
	      }

		}catch (GenericEntityException e) {
			  Debug.logError(e, "Could not commit transaction for entity Facility", module);
		  }
		
		result=ServiceUtil.returnSuccess("updated Classification successfully !!");
		result.put("partyId",weaverId);
		return result;
	}
	
	
	
	public static Map<String, Object> updateWeaverPartyRelationShip(DispatchContext ctx, Map<String, ? extends Object> context){ 
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String weaverId = (String)context.get("weaverId");
		String calssification = (String)context.get("relation");
		
		if (UtilValidate.isEmpty(weaverId)){
			Debug.logError("Weaver Id is Empty !" , module);
			return ServiceUtil.returnError(" Weaver Id is Empty !");
		}
		SimpleDateFormat SimpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();

		 Map inPartyMapClass = UtilMisc.toMap("userLogin", userLogin);
	        inPartyMapClass.put("fromDate", nowTimeStamp);
	        inPartyMapClass.put("partyIdTo", weaverId);
			try{            	
				Map resultMap = dispatcher.runSync("updatePartyRelationship", inPartyMapClass);
				if (ServiceUtil.isError(resultMap)) {
						String errMsg =  ServiceUtil.getErrorMessage(resultMap);
						Debug.logError(errMsg , module);
						return ServiceUtil.returnError(errMsg);
	             }
				//partyId = (String)resultMap.get("partyId");
				
	        }catch (GenericServiceException e) {
	         Debug.logError(e, module);
	         return ServiceUtil.returnError("Service Exception: " + e.getMessage());
	      }
		
		
		result=ServiceUtil.returnSuccess("updated Classification successfully !!");
		result.put("partyId",weaverId);
		return result;
	}
	
	// Service to papulate Shipment Reimbursement for all Branch and Depot Shipments
	public static Map<String, Object> papulateShipmentReimbursment(DispatchContext ctx,Map<String, ? extends Object> context) {
	    Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess("Successfully Papulated all Shipments estimatedShipCost in ShipmentReimbursment...");
	    Locale locale = (Locale) context.get("locale");
	  	String roid = (String) context.get("roId");
	  	boolean beganTransaction = false;
		if (UtilValidate.isEmpty(roid)) {
			Debug.logError("Cannot Run Service Without Regional Id",module);
			return ServiceUtil.returnError("Cannot Run Service Without Regional Id");
		}
		try{
			beganTransaction = TransactionUtil.begin(10000);
			List roBranchIds = FastList.newInstance();
			List roBranchOrderIds = FastList.newInstance();
			List roBranchPoOrderIds = FastList.newInstance();
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, roid));
			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
			List<GenericValue> partyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isEmpty(partyRelationship)){
				Debug.logError("Unable Query Party Relationship",module);
				return ServiceUtil.returnError("Unable Query Party Relationship");
			}
			roBranchIds=EntityUtil.getFieldListFromEntityList(partyRelationship,"partyIdTo",true);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, roBranchIds));
			conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
			List<GenericValue> orderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isEmpty(orderRole)){
				Debug.logError("No Indents Found for Given Ro",module);
				return ServiceUtil.returnError("No Indents Found for Given Ro");
			}
			roBranchOrderIds=EntityUtil.getFieldListFromEntityList(orderRole,"orderId",true);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.IN, roBranchOrderIds));
			conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
			List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isEmpty(orderAssoc)){
				Debug.logError("No Purchase Order Found For Ro sales Orders",module);
				return ServiceUtil.returnError("No Purchase Order Found For Ro sales Orders");
			}
			roBranchPoOrderIds=EntityUtil.getFieldListFromEntityList(orderAssoc,"orderId",true);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, roBranchPoOrderIds));
			conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
			//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DOCUMENTS_ENDORSED"));
			EntityFindOptions findOptions = new EntityFindOptions();
            findOptions.setDistinct(true);
            Set<String> fieldsToSelect = UtilMisc.toSet("shipmentId", "estimatedShipCost","estimatedShipDate");
            List<GenericValue> shipmentAndOrderRole = delegator.findList("ShipmentAndOrderRole", EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, UtilMisc.toList("orderId"), findOptions, false);
	        for(GenericValue eachShipment:shipmentAndOrderRole) {
	        	try{
		  			GenericValue ShipmentReimbursement = delegator.makeValue("ShipmentReimbursement");
	  				if(UtilValidate.isNotEmpty(eachShipment.get("estimatedShipCost"))){
		  				ShipmentReimbursement.set("shipmentId", eachShipment.get("shipmentId"));
		  				if(UtilValidate.isEmpty(eachShipment.get("estimatedShipDate"))){
		  					ShipmentReimbursement.set("receiptDate", eachShipment.get("estimatedShipDate"));
						}else{
							ShipmentReimbursement.set("receiptDate", UtilDateTime.nowTimestamp());
						}
		  				ShipmentReimbursement.set("receiptAmount", eachShipment.get("estimatedShipCost"));
						delegator.createSetNextSeqId(ShipmentReimbursement);
	  				}
		  		}catch(GenericEntityException e){
					Debug.logError(e, "Failed to Upate  ShipmentReimbursment ", module);
				}
		     }
		}catch(Exception e){
			try {
	  			TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
			} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		}
			return ServiceUtil.returnError(e.toString()); 
		}
		finally {
	  		  try {
	  			  TransactionUtil.commit(beganTransaction);
	  		  } catch (GenericEntityException e) {
	  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  		  }
	  	}
		return result;

	}
}
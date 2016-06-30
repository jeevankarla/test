package in.vasista.vbiz.depotsales;

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
import org.ofbiz.party.party.PartyHelper;

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
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.party.contact.ContactMechWorker;


import java.util.Iterator;




import java.util.Collection;


public class DepotPurchaseServices{

   public static final String module = DepotPurchaseServices.class.getName();
   
   
	public static String processDepotPurchaseInvoice(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String vehicleId = (String) request.getParameter("vehicleId");
		String tallyrefNo = (String) request.getParameter("tallyrefNo");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purposeTypeId = "YARN_SALE";
	  
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
		List<GenericValue> orderItems=null;
		try{
		 orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		
		if(UtilValidate.isNotEmpty(tallyrefNo)){
				 /* List conditionList = FastList.newInstance();
		
				  conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
				  conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS,"BackToBackOrder"));
				  EntityCondition assoc = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	*/			  try{
				 // List  OrderAssocList = delegator.findList("OrderAssoc", assoc, null, null, null,false); 
				  
				   //if(UtilValidate.isNotEmpty(OrderAssocList)){
		
				 // GenericValue OrderAssoc = EntityUtil.getFirst(OrderAssocList);
				  //String actualOrderId = (String)OrderAssoc.get("toOrderId");
				  
				  GenericValue	OrderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId", orderId), false);
		
				  OrderHeader.set("tallyRefNo",tallyrefNo);
				  OrderHeader.store();
				// }
				
			}catch (Exception e) {
				Debug.logError(e, "Problems While Updating Tally Ref No: " + tallyrefNo, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems While Updating Tally Ref No: " + tallyrefNo);
				return "error";
			}
		}	
		
		
		
		List productQtyList = FastList.newInstance();
		List invoiceAdjChargesList = FastList.newInstance();
		List invoiceDiscountsList = FastList.newInstance();
		
		String applicableTo = "ALL";
		String applicableToDisc = "ALL";
		for (int i = 0; i < rowCount; i++) {
			
			Map prodQtyMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String invoiceItemDiscTypeId = "";
			String adjAmtDiscStr = "";
			String discQtyStr = "";
			BigDecimal adjDiscAmt = BigDecimal.ZERO;
			BigDecimal discQty = BigDecimal.ZERO;
			
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
				invItemMap.put("adjustmentTypeId", invoiceItemTypeId);
				invItemMap.put("amount", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
				invoiceAdjChargesList.add(invItemMap);	
			}
			
			
			if (paramMap.containsKey("invoiceItemTypeDiscId" + thisSuffix)) {
				invoiceItemDiscTypeId = (String) paramMap.get("invoiceItemTypeDiscId" + thisSuffix);
			}
			
			if (paramMap.containsKey("applicableToDisc" + thisSuffix)) {
				applicableToDisc = (String) paramMap.get("applicableToDisc" + thisSuffix);
			}
			
			if (paramMap.containsKey("adjDiscAmt" + thisSuffix)) {
				adjAmtDiscStr = (String) paramMap.get("adjDiscAmt" + thisSuffix);
			}
			if (paramMap.containsKey("discQty" + thisSuffix)) {
				discQtyStr = (String) paramMap.get("discQty" + thisSuffix);
			}
			
			if(UtilValidate.isNotEmpty(adjAmtDiscStr)){
				try {
					adjDiscAmt = new BigDecimal(adjAmtDiscStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			if(UtilValidate.isNotEmpty(discQtyStr)){
				try {
					discQty = new BigDecimal(discQtyStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + discQtyStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + discQtyStr);
					return "error";
				}
			}
			if(UtilValidate.isNotEmpty(invoiceItemDiscTypeId) && adjDiscAmt.compareTo(BigDecimal.ZERO)>0){
				
				BigDecimal adjQty = BigDecimal.ONE;
				if(UtilValidate.isNotEmpty(discQty)){
					if(discQty.compareTo(BigDecimal.ONE)>0){
						adjDiscAmt = adjDiscAmt.divide(discQty);
						adjQty = discQty;
					}
				}
				
				Map invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemDiscTypeId);
				invItemMap.put("amount", adjDiscAmt);
				invItemMap.put("quantity", adjQty);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableToDisc);
				invoiceDiscountsList.add(invItemMap);	
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
			GenericValue orderItemValue = null;

			if(UtilValidate.isNotEmpty(productId)){
				try{
					List<GenericValue> orderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));				
					if(UtilValidate.isNotEmpty(orderItem)){
						orderItemValue = EntityUtil.getFirst(orderItem);
						orderItemValue.put("unitPrice", uPrice);
						orderItemValue.put("unitListPrice", uPrice);
						orderItemValue.store();
					}else{
						continue;
					}
				}catch (Exception e) {
					  Debug.logError(e, "Error While Updating purchase Order ", module);
					  request.setAttribute("_ERROR_MESSAGE_", "Unable to update Purchase Order :" + orderId+"....! ");
						return "error";
				}
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
		
		// Get Purpose type based on product
		
		String invProdId = (String) ((Map) productQtyList.get(0)).get("productId");
		
		try{
	  		Map resultCtx = dispatcher.runSync("getPurposeTypeForProduct", UtilMisc.toMap("productId", invProdId, "userLogin", userLogin));  	
	  		purposeTypeId = (String)resultCtx.get("purposeTypeId");
	  	}catch (GenericServiceException e) {
	  		Debug.logError("Unable to analyse purpose type: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to analyse purpose type :"+ServiceUtil.getErrorMessage(result));
			return "error";
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
		processInvoiceContext.put("invoiceDiscountsList", invoiceDiscountsList);
		
		if(UtilValidate.isNotEmpty(isDisableAcctg)){
			processInvoiceContext.put("isDisableAcctg", isDisableAcctg);
		}
		result = createDepotInvoice(dctx, processInvoiceContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate invoice  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";
		}
		
		String invoiceId =  (String)result.get("invoiceId");
		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId);	  	 
		
		return "success";
	}
	
	
	
	public static String processDepotSalesInvoice(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		//String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String partyIdFrom = "";
		//String shipmentId = (String) request.getParameter("shipmentId");
	  
		String strInvoiceId = (String) request.getParameter("invoiceId");
		
		GenericValue shipment =null;
		GenericValue invoiceList=null;
		
		try{
		 invoiceList = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", strInvoiceId), false);
		
		 shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId",invoiceList.get("shipmentId")), false);
		}catch (Exception e) {
			Debug.logError(e, "Problems while changing Status: " + strInvoiceId, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + strInvoiceId);
			return "error";
		}
		
		Debug.log("invoiceList========================"+invoiceList);
		
		Debug.log("shipment========================"+shipment);
		
		String partyId = (String)shipment.getString("partyIdTo");
		
		String shipmentId = (String)shipment.getString("shipmentId");
		
		String primaryOrderId = (String)shipment.getString("primaryOrderId");
		
		Debug.log("primaryOrderId========================"+primaryOrderId);
		
		Debug.log("partyId========================"+partyId);

		
		if(UtilValidate.isNotEmpty(shipment) && shipment.equals("SHIPMENT_CANCELLED")){
			Debug.logError("Unable to generate Shipment: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate shipment  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";	
			}
		
		
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		BigDecimal grandTotal = BigDecimal.ZERO;
		List<GenericValue> orderParty=null;
		   if (UtilValidate.isNotEmpty(shipmentId)) {
	           Map<String, Object> createInvoiceContext = FastMap.newInstance();
	           createInvoiceContext.put("partyId", partyId);
	           try {
					orderParty = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", primaryOrderId, "roleTypeId", "BILL_TO_CUSTOMER"));
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
				}
				
				if (UtilValidate.isNotEmpty(orderParty)) {
					GenericValue custOrderRole = EntityUtil.getFirst(orderParty);
					partyIdFrom = custOrderRole.getString("partyId");
				}
				Debug.log("partyIdFrom====================="+partyIdFrom);
	           createInvoiceContext.put("partyIdFrom", partyIdFrom);
	           createInvoiceContext.put("shipmentId", shipmentId);
	           createInvoiceContext.put("invoiceTypeId", "SALES_INVOICE");
	           createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
	           createInvoiceContext.put("userLogin", userLogin);

	           // store the invoice first
	           Map<String, Object> createInvoiceResult = FastMap.newInstance();
	         
	           try{
		            createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
			        if(ServiceUtil.isError(createInvoiceResult)){
						Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
						request.setAttribute("_ERROR_MESSAGE_", "Unable to generate invoice  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
						return "error";
					}
	           } catch (Exception e) {
					Debug.logError(e, "Problems while calling createInvoice : " + partyId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems while calling createInvoice : " + partyId);
					return "error";
				}
	           
	           // call service, not direct entity op: delegator.create(invoice);
	          
	           String invoiceId = (String) createInvoiceResult.get("invoiceId");
	           
	           Debug.log("invoiceId========================"+invoiceId);
	           
	//invoiceitemmmmm
	           
	           List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, strInvoiceId));
				EntityCondition ficondExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> invoiceItemList =null;
				
				try{
			      invoiceItemList = delegator.findList("InvoiceItem", ficondExpr, null, null, null, false);
				} catch (Exception e) {
					Debug.logError(e, "Problems while getting invoiceItems : " + invoiceId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems while getting invoiceItems : " + invoiceId);
					return "error";
				}
				
				Debug.log("invoiceItemList======================"+invoiceItemList);
				
				for (int i = 0; i < invoiceItemList.size(); i++) {
					
				
					GenericValue eachInvoiceList = (GenericValue)invoiceItemList.get(i);
					
					Debug.log("TotalAmount========="+eachInvoiceList.getBigDecimal("amount"));
					
			  grandTotal = grandTotal.add(eachInvoiceList.getBigDecimal("amount").multiply(eachInvoiceList.getBigDecimal("quantity")));
	           Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	           createInvoiceItemContext.put("invoiceId",invoiceId);
	           createInvoiceItemContext.put("invoiceItemTypeId", eachInvoiceList.get("invoiceItemTypeId"));
	           createInvoiceItemContext.put("description", eachInvoiceList.get("description"));
	           createInvoiceItemContext.put("quantity",eachInvoiceList.get("quantity"));
	           createInvoiceItemContext.put("amount",eachInvoiceList.get("amount"));
	           createInvoiceItemContext.put("productId", eachInvoiceList.get("productId"));
	           createInvoiceItemContext.put("unitPrice", eachInvoiceList.get("unitPrice"));
	           createInvoiceItemContext.put("unitListPrice", eachInvoiceList.get("unitListPrice"));
	           //createInvoiceItemContext.put("uomId", "");
	           createInvoiceItemContext.put("userLogin", userLogin);

	          try{
	           Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	           
	           
	           if(ServiceUtil.isError(createInvoiceItemResult)){
	      			Debug.logError("Unable to create invoiceItems for invoice: " + ServiceUtil.getErrorMessage(result), module);
	      			request.setAttribute("_ERROR_MESSAGE_", "Unable to Change invoice Status For Shipment :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(result));
	      			return "error";
	      		}
	          } catch (Exception e) {
					Debug.logError(e, "Problems while calling createInvoiceItem : " + invoiceId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems while calling createInvoiceItem : " + invoiceId);
					return "error";
				}
				}
	       

		   Debug.log("invoiceItemList======================Success");
	           
	           String nextStatusId = "INVOICE_READY";
	           try {
	                   Map<String, Object> setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", nextStatusId, "userLogin", userLogin));
	                   if(ServiceUtil.isError(setInvoiceStatusResult)){
	           			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
	           			request.setAttribute("_ERROR_MESSAGE_", "Unable to Change invoice Status For Shipment :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(result));
	           			return "error";
	           		}
	           } catch (Exception e) {
					Debug.logError(e, "Problems while changing Status: " + invoiceId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + invoiceId);
					return "error";
				}
	           Debug.log("invoiceId=============2222==========="+invoiceId);
	           
	            nextStatusId = "INVOICE_APPROVED";
	           try {
	                   Map<String, Object> setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", nextStatusId, "userLogin", userLogin));
	                   if(ServiceUtil.isError(createInvoiceResult)){
	           			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
	           			request.setAttribute("_ERROR_MESSAGE_", "Unable to Change invoice Status For Shipment :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(result));
	           			return "error";
	           		}
	           } catch (Exception e) {
					Debug.logError(e, "Problems while changing Status: " + invoiceId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + invoiceId);
					return "error";
				}
	           
		    
	          
		   try{
			   
			   conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId));
			   EntityCondition condExpress = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", condExpress, null, null, null, false);
			
		   
			   String actualOrderId = (EntityUtil.getFirst(orderAssocList)).getString("toOrderId");
			   
			    conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
			   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_VOID"));
			   EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List<GenericValue> orderPreferenceList = delegator.findList("OrderPaymentPreference", condExpr, null, null, null, false);
			   List orderPreferenceIdList = EntityUtil.getFieldListFromEntityList(orderPreferenceList, "orderPaymentPreferenceId", true);
		   
			   conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("orderPaymentPreferenceId", EntityOperator.IN, orderPreferenceIdList));
			   EntityCondition condExpretion = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List<GenericValue> paymentList = delegator.findList("OrderPreferencePaymentApplication", condExpretion, null, null, null, false);

			  //enericValue orderHeaderList = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", primaryOrderId), false);
			
			  BigDecimal paidAmount = BigDecimal.ZERO;
			
			if (UtilValidate.isNotEmpty(paymentList)) {
				for (GenericValue eachPayment : paymentList) {
					
					 BigDecimal eachAmount = (BigDecimal)eachPayment.get("amountApplied");
					 paidAmount = paidAmount.add(eachAmount);
					 Map newPayappl = UtilMisc.toMap("userLogin",userLogin);
		            	newPayappl.put("invoiceId", invoiceId);
		            	newPayappl.put("paymentId", eachPayment.get("paymentId"));
		            	newPayappl.put("amountApplied", eachAmount);
		            	
		            Map<String, Object> paymentApplResult = dispatcher.runSync("createPaymentApplication",newPayappl);
		            if(ServiceUtil.isError(paymentApplResult)){
	           			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(paymentApplResult), module);
	           			//request.setAttribute("_ERROR_MESSAGE_", "Unable to Create Payment Application For :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(paymentApplResult));
	           			return "error";
	           		}
		           	
		           	
				}
			}
			
			BigDecimal balance = grandTotal.subtract(paidAmount);
		   
			Debug.log("grantTotal========"+grandTotal);
			Debug.log("paidAmount========"+paidAmount);
			Debug.log("balance========"+balance);
			
			 //for Sent SMS 
			
			Map<String, Object> getTelParams = FastMap.newInstance();
	    	getTelParams.put("partyId", partyId);
	        getTelParams.put("userLogin", userLogin);                    	
	        Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	        if (ServiceUtil.isError(serviceResult)) {
	        	Debug.logError("Unable to get telephone number for party : " + ServiceUtil.getErrorMessage(result), module);
				//request.setAttribute("_ERROR_MESSAGE_", "Problems while getting Telephone for: " + partyId);
				//return "error";
	        } 
	        String contactNumberTo = (String) serviceResult.get("contactNumber");
	        String countryCode = (String) serviceResult.get("countryCode");
	        Debug.log("contactNumberTo = "+contactNumberTo);
	        Debug.log("countryCode ===="+countryCode);
	        Debug.log("contactNumberTo = "+contactNumberTo);
	        if(UtilValidate.isEmpty(contactNumberTo)){
	        	contactNumberTo = "9502532897";
	        }
	        //contactNumberTo = "9440625565";
	        if(UtilValidate.isNotEmpty(contactNumberTo)){
	        	 if(UtilValidate.isNotEmpty(countryCode)){
	        		 contactNumberTo = countryCode + contactNumberTo;
	        	 }
	        	 
	        	 String grandTotalStr=String.valueOf(grandTotal.intValue());
	        	 String paidAmountStr=String.valueOf(paidAmount.intValue());
	        	 String balanceStr=String.valueOf(balance.intValue());
	        	 
	        	 String invoiceMsgToWeaver = UtilProperties.getMessage("ProductUiLabels", "InvoiceMsgToWeaver", locale);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("orderId", actualOrderId);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("InvoiceValue", grandTotalStr);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("advancePaid", paidAmountStr);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("balancePayable", balanceStr);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("invoiceNo", invoiceId);
	        	 
	        	 Debug.log("invoiceMsgToWeaver =============="+invoiceMsgToWeaver);
	        	 
	        	 
	        	 
	        	 Debug.log("contactNumberTo ===== "+contactNumberTo);
	        	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	             sendSmsParams.put("contactNumberTo", contactNumberTo);
	             sendSmsParams.put("text", invoiceMsgToWeaver); 
	             Debug.log("sendSmsParams========================"+sendSmsParams);
	             serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	             if (ServiceUtil.isError(serviceResult)) {
	            	 Debug.logError("Unable to Send SMS: " + ServiceUtil.getErrorMessage(result), module);
	        			//request.setAttribute("_ERROR_MESSAGE_", "Unable to Change invoice Status For Shipment :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(result));
	        			//return "error";
	             }
	        }
		    
		   }catch (Exception e) {
				Debug.logError(e, "Problems while Calculating balance Amount for order: " + partyId, module);
				//request.setAttribute("_ERROR_MESSAGE_", "Problems while Calculating balance Amount for order: " + partyId);
				return "error";
			}
		   
		   }
		   request.setAttribute("_EVENT_MESSAGE_", "Sales Invoice created sucessfully : "+partyId);   
		
		return "success";

	}  
	
	
	
	
	public static Map<String, Object> createDepotInvoice(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		    Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    Map<String, Object> result = ServiceUtil.returnSuccess();
		    List<Map> productQtyList = (List) context.get("productQtyList");
		    List<Map> invoiceAdjChargesList = (List) context.get("invoiceAdjChargesList");
		    List<Map> invoiceDiscountsList = (List) context.get("invoiceDiscountsList");
		    
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
	        List<GenericValue> orderParty = null;  

			try {
				orderParty = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR"));
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			
			if (UtilValidate.isNotEmpty(orderParty)) {
				GenericValue custOrderRole = EntityUtil.getFirst(orderParty);
				partyId = custOrderRole.getString("partyId");
			}
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
				conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.NOT_EQUAL, "SALES_INVOICE"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> invoices = delegator.findList("Invoice", condition, null, null, null, false);
				
				if(UtilValidate.isNotEmpty(invoices)){
					Debug.logError("Invoices already generated for shipment : "+shipmentId, module);
					return ServiceUtil.returnError("Invoices already generated for shipment : "+shipmentId);
				}
				
				/*Map resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", productQtyList, "otherCharges", invoiceAdjChargesList, "userLogin", userLogin, "incTax", ""));
				if(ServiceUtil.isError(resultCtx)){
	  		  		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
	  		  		Debug.logError(errMsg , module);
	  		  		return ServiceUtil.returnError(errMsg);
				}*/
				//Debug.log("#####resultCtx#########"+resultCtx);
				//List<Map> itemDetails = (List)resultCtx.get("itemDetail");
				//List<Map> adjustmentDetail = (List)resultCtx.get("adjustmentDetail");
				
				//invoiceDiscountsList
				
				
				Map input = FastMap.newInstance();
				input.put("userLogin", userLogin);
		        input.put("invoiceTypeId", "PURCHASE_INVOICE");        
		        input.put("partyIdFrom", partyId);	
		        input.put("statusId", "INVOICE_IN_PROCESS");	
		        input.put("currencyUomId", currencyUomId);
		        input.put("invoiceDate", invoiceDate);
		        input.put("dueDate", invoiceDate); 	        
		        input.put("partyId",partyIdFrom );
		        input.put("shipmentId",shipmentId );
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
				
				Map itemSeqMap = FastMap.newInstance();
				int i=0;
				for (Map<String, Object> prodQtyMap : productQtyList) {
					
					String productId = "";
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal amount = BigDecimal.ZERO;
					Map invoiceItemCtx = FastMap.newInstance();
					Map vatItemCtx = FastMap.newInstance();
					Map cstItemCtx = FastMap.newInstance();
					
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
						/*if(UtilValidate.isNotEmpty(prodQtyMap.get("bedUnitRate"))){
							unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedUnitRate"));
						}
						if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessUnitRate"))){
							unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedcessUnitRate"));
						}
						if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessUnitRate"))){
							unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedseccessUnitRate"));
						}*/
						invoiceItemCtx.put("amount", unitPrice);
						invoiceItemCtx.put("unitPrice", unitPrice);
						
					}
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
						unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
						invoiceItemCtx.put("unitListPrice", unitListPrice);
					}*/
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
						BigDecimal vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
						if(vatPercent.compareTo(BigDecimal.ZERO)>0){
							vatItemCtx.put("vatPercent", vatPercent);
						}
					}*/
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
						BigDecimal vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
						if(vatAmount.compareTo(BigDecimal.ZERO)>0){
							vatItemCtx.put("invoiceItemTypeId", "VAT_PUR");
							vatItemCtx.put("amount", vatAmount);
							vatItemCtx.put("quantity", BigDecimal.ONE);
							vatItemCtx.put("description", "VAT");
						}
						
					}
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
						BigDecimal cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
						if(cstPercent.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("cstPercent", cstPercent);
						}
					}*/
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
						BigDecimal cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
						if(cstAmount.compareTo(BigDecimal.ZERO)>0){
							cstItemCtx.put("invoiceItemTypeId", "CST_PUR");
							cstItemCtx.put("amount", cstAmount);
							cstItemCtx.put("quantity", BigDecimal.ONE);
							cstItemCtx.put("description", "CST");
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
					
					
					GenericValue shipmentRece = (GenericValue)shipmentReceipts.get(i);
					String orderItemSeqNo = (String)shipmentRece.get("orderItemSeqId");
					
					Map<String, Object> createOrderItemBillingContext = FastMap.newInstance();
	                createOrderItemBillingContext.put("invoiceId", invoiceId);
	                createOrderItemBillingContext.put("invoiceItemSeqId", invItemSeqId);
	                createOrderItemBillingContext.put("orderId", orderId);
	                createOrderItemBillingContext.put("orderItemSeqId", orderItemSeqNo);
	               /* //createOrderItemBillingContext.put("itemIssuanceId", itemIssuanceId);
	                createOrderItemBillingContext.put("quantity", billingQuantity);
	                createOrderItemBillingContext.put("amount", billingAmount);
*/	                createOrderItemBillingContext.put("userLogin", userLogin);
	               /* if ((shipmentReceipt != null) && (shipmentReceipt.getString("receiptId") != null)) {
	                    createOrderItemBillingContext.put("shipmentReceiptId", shipmentReceipt.getString("receiptId"));
	                }*/

	                Map<String, Object> createOrderItemBillingResult = dispatcher.runSync("createOrderItemBilling", createOrderItemBillingContext);
	                if (ServiceUtil.isError(createOrderItemBillingResult)) {
	                	Debug.logError("Error creating OrderItem Billing", module);	
						return ServiceUtil.returnError("Error creating OrderItem Billing");
	                }
					
					itemSeqMap.put(productId, invItemSeqId);
					
					if(UtilValidate.isNotEmpty(vatItemCtx)){
						vatItemCtx.put("invoiceId", invoiceId);
						vatItemCtx.put("parentInvoiceId", invoiceId);
						vatItemCtx.put("parentInvoiceItemSeqId", invItemSeqId);
						vatItemCtx.put("userLogin", userLogin);
						result = dispatcher.runSync("createInvoiceItem", vatItemCtx);
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Item : VAT PUR", module);	
							return ServiceUtil.returnError("Error creating Invoice item for Item : VAT PUR");
						}
					}
					if(UtilValidate.isNotEmpty(cstItemCtx)){
						cstItemCtx.put("invoiceId", invoiceId);
						cstItemCtx.put("parentInvoiceId", invoiceId);
						cstItemCtx.put("parentInvoiceItemSeqId", invItemSeqId);
						cstItemCtx.put("userLogin", userLogin);
						result = dispatcher.runSync("createInvoiceItem", cstItemCtx);
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Item: CST PUR", module);	
							return ServiceUtil.returnError("Error creating Invoice item for Item: CST PUR");
						}
					}
					
					List<GenericValue> receipts = EntityUtil.filterByCondition(shipmentReceipts, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					if(UtilValidate.isNotEmpty(receipts)){
						String inventoryItemId = (EntityUtil.getFirst(receipts)).getString("inventoryItemId");
						
						GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
						
						if(UtilValidate.isNotEmpty(inventoryItem)){
							inventoryItem.set("unitCost", unitListPrice);
							inventoryItem.store();
						}
					}
					
					i++;
				}
				
				
				
				for (Map<String, Object> adjustMap : invoiceAdjChargesList) {
					
					String adjustmentTypeId = "";
					String applicableTo = "";
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
						
						invoiceItemCtx.put("parentInvoiceId", invoiceId);
						if( (UtilValidate.isNotEmpty(adjustMap.get("applicableTo")))  &&    (!(adjustMap.get("applicableTo")).equals("ALL"))  ){
							applicableTo = (String)adjustMap.get("applicableTo");
							if( UtilValidate.isNotEmpty(itemSeqMap.get(applicableTo))) {
								String seqId = (String)itemSeqMap.get(applicableTo);
								invoiceItemCtx.put("parentInvoiceItemSeqId", seqId);
							}
						}
						
						result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Item : "+adjustmentTypeId, module);	
							return ServiceUtil.returnError("Error creating Invoice item for Item : "+adjustmentTypeId);
						}
						String invItemSeqId = (String) result.get("invoiceItemSeqId");
					}
					
				}
				
				for (Map<String, Object> adjustMap : invoiceDiscountsList) {
					
					String adjustmentTypeId = "";
					String applicableTo = "";
					BigDecimal amount = BigDecimal.ZERO;
					Map invoiceItemCtx = FastMap.newInstance();
					if(UtilValidate.isNotEmpty(adjustMap.get("adjustmentTypeId"))){
						adjustmentTypeId = (String)adjustMap.get("adjustmentTypeId");
						invoiceItemCtx.put("invoiceItemTypeId", adjustmentTypeId);
					}
					if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
						amount = (BigDecimal)adjustMap.get("amount");
						invoiceItemCtx.put("amount", amount.negate());
					}
					if(UtilValidate.isNotEmpty(adjustmentTypeId) && !(amount.compareTo(BigDecimal.ZERO) == 0)){
						invoiceItemCtx.put("invoiceId", invoiceId);
						invoiceItemCtx.put("quantity", adjustMap.get("quantity"));
						invoiceItemCtx.put("userLogin", userLogin);
						
						invoiceItemCtx.put("parentInvoiceId", invoiceId);
						if( (UtilValidate.isNotEmpty(adjustMap.get("applicableTo")))  &&    (!(adjustMap.get("applicableTo")).equals("ALL"))  ){
							applicableTo = (String)adjustMap.get("applicableTo");
							if( UtilValidate.isNotEmpty(itemSeqMap.get(applicableTo))) {
								String seqId = (String)itemSeqMap.get(applicableTo);
								invoiceItemCtx.put("parentInvoiceItemSeqId", seqId);
							}
						}
						
						result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Item : "+adjustmentTypeId, module);	
							return ServiceUtil.returnError("Error creating Invoice item for Item : "+adjustmentTypeId);
						}
						String invItemSeqId = (String) result.get("invoiceItemSeqId");
					}
					
				}
				
				GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
				/*invoice.set("shipmentId", shipmentId);
				invoice.store();
				*/
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
			      //approve invoice
			      /*Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
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
		            } */ 
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
	
	
	public static String CreatePOByOrder(HttpServletRequest request, HttpServletResponse response) {
	
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		//old PO flow starts from here
		String partyId = (String) request.getParameter("supplierId");
		String billFromVendorPartyId = (String) request.getParameter("billToPartyId");
		String productStoreId = (String) request.getParameter("productStoreId");
		String orderId = (String) request.getParameter("orderId");
		String refNo = (String) request.getParameter("refNo");
		String quotationNo = (String) request.getParameter("quotationNo");
		String orderDateStr = (String) request.getParameter("orderDate");
		String effectiveDateStr = (String) request.getParameter("orderDate");
		String partyIdTo = (String) request.getParameter("shipToPartyId");
		// contact Details
		String city = (String) request.getParameter("city");
		String address1 = (String) request.getParameter("address1");
		String address2 = (String) request.getParameter("address2");
		String country = (String) request.getParameter("country");
		String postalCode = (String) request.getParameter("postalCode");
		String stateProvinceGeoId = (String) request.getParameter("stateProvinceGeoId");
		String districtGeoId = (String) request.getParameter("districtGeoId");
		String contactMechId = null;

		Map<String, Object> resultContatMap = FastMap.newInstance();
		Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();

		String orderTypeId="";
		String orderName ="";
		Timestamp effectiveDate = UtilDateTime.nowTimestamp();
		Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		List<Map> itemDetail = FastList.newInstance();;
		Timestamp orderDate = UtilDateTime.nowTimestamp();
		BigDecimal grandTotal =BigDecimal.ZERO;
		Map resultMap = FastMap.newInstance();
		try {
			if(UtilValidate.isNotEmpty(orderId)){
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if (UtilValidate.isNotEmpty(orderHeader)) { 
					String statusId = orderHeader.getString("statusId");
					//orderTypeId=orderHeader.getString("orderTypeId");
					orderName=orderHeader.getString("orderName");
					estimatedDeliveryDate=orderHeader.getTimestamp("estimatedDeliveryDate");
					grandTotal=orderHeader.getBigDecimal("grandTotal");
					if(statusId.equals("ORDER_CANCELLED")){
						Debug.logError("Cannot create PurchaseOrder for cancelled orderId : "+orderId, module);
						request.setAttribute("_ERROR_MESSAGE_", "Cannot create PurchaseOrder for cancelled orderId : "+orderId);	
				  		return "error";
					}
				}
				String productId = null;
				String orderItemSeqId = null;
				String batchNo = null;
				String daysToStore = null;
				String quantityStr = null;
				String basicPriceStr = null;
				String vatPriceStr = null;
				String bedPriceStr = null;
				String cstPriceStr = null;
				String unitPriceStr = null;
				String remarksStr = null;
				String serTaxPriceStr = null;
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal basicPrice = BigDecimal.ZERO;
				BigDecimal cstPrice = BigDecimal.ZERO;
				BigDecimal unitPrice = BigDecimal.ZERO;
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
						
						if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
							orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
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
						
						if (paramMap.containsKey("unitPrice" + thisSuffix)) {
							unitPriceStr = (String) paramMap.get("unitPrice"
									+ thisSuffix);
						}
						
						
						if (paramMap.containsKey("remarks" + thisSuffix)) {
							remarksStr = (String) paramMap.get("remarks"
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
						
						

						try {
							quantity = new BigDecimal(quantityStr);
							if (UtilValidate.isNotEmpty(unitPriceStr)) {
								unitPrice = new BigDecimal(unitPriceStr);
							}
							if (UtilValidate.isNotEmpty(basicPriceStr)) {
								basicPrice = new BigDecimal(basicPriceStr);
							}
							if (UtilValidate.isNotEmpty(cstPriceStr)) {
								cstPrice = new BigDecimal(cstPriceStr);
							}
							
							if (UtilValidate.isNotEmpty(bedPriceStr)) {
								bedPrice = new BigDecimal(bedPriceStr);
							}
							if (UtilValidate.isNotEmpty(vatPriceStr)) {
								vatPrice = new BigDecimal(vatPriceStr);
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
						productQtyMap.put("unitListPrice", unitPrice);
						productQtyMap.put("basicPrice", basicPrice);
						productQtyMap.put("bedPrice", bedPrice);
						productQtyMap.put("cstPrice", cstPrice);
						productQtyMap.put("vatPrice", vatPrice);
						productQtyMap.put("bedPercent", bedPercent);
						productQtyMap.put("vatPercent", vatPercent);
						productQtyMap.put("cstPercent", cstPercent);
						productQtyMap.put("orderItemSeqId", orderItemSeqId);
						
						itemDetail.add(productQtyMap);

					}//end of productQty check
				}//end row count for loop
				//Debug.log("indentProductList============================"+indentProductList);
				
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

				Map orderItemSeq = FastMap.newInstance();
				/*for(GenericValue orderItemsValues : orderItems){
					String productId = orderItemsValues.getString("productId");
					BigDecimal quantity=orderItemsValues.getBigDecimal("quantity");
					BigDecimal unitListPrice=BigDecimal.ZERO;
					BigDecimal unitPrice=BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(orderItemsValues.getBigDecimal("unitListPrice"))){
						unitListPrice=orderItemsValues.getBigDecimal("unitListPrice");
					}
					if(UtilValidate.isNotEmpty(orderItemsValues.getBigDecimal("unitPrice"))){
						unitPrice=orderItemsValues.getBigDecimal("unitPrice");
					}
					Map tempMap=FastMap.newInstance();
					tempMap.put("productId",productId);
					tempMap.put("quantity",quantity);
					tempMap.put("unitPrice",unitPrice);
					tempMap.put("unitListPrice",unitListPrice);
					itemDetail.add(tempMap);
					String orderItemSeqId = orderItemsValues.getString("orderItemSeqId");
					orderItemSeq.put(productId, orderItemSeqId);
				}*/
			
				if (UtilValidate.isNotEmpty(orderDateStr)) { 
					try {
						orderDate = new java.sql.Timestamp(sdf.parse(orderDateStr).getTime());
					} catch (ParseException e) {
						Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
					} catch (NullPointerException e) {
						Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
					}
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
			
				Map processOrderContext = FastMap.newInstance();

				//String productStoreId = (String) (in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
				//String productStoreId ="STORE";
				List termsList = FastList.newInstance();
				List otherTermDetail = FastList.newInstance();
				List adjustmentDetail = FastList.newInstance();

				processOrderContext.put("userLogin", userLogin);
				processOrderContext.put("productQtyList", itemDetail);
				processOrderContext.put("orderTypeId", orderTypeId);
				processOrderContext.put("orderId", orderId);
				processOrderContext.put("termsList", termsList);
				processOrderContext.put("partyId", partyId);
				processOrderContext.put("grandTotal", grandTotal);
				processOrderContext.put("otherTerms", otherTermDetail);
				processOrderContext.put("adjustmentDetail", adjustmentDetail);
				processOrderContext.put("billFromPartyId", partyId);
				processOrderContext.put("issueToDeptId", "");
				processOrderContext.put("shipToPartyId", partyIdTo);
				processOrderContext.put("billFromVendorPartyId", billFromVendorPartyId);
				processOrderContext.put("supplyDate", effectiveDate);
				processOrderContext.put("salesChannel", "WEB_SALES_CHANNEL");
				processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
				processOrderContext.put("productStoreId", productStoreId);
				processOrderContext.put("PONumber", orderId);
				processOrderContext.put("refNo", refNo);
				processOrderContext.put("quotationNo", quotationNo);
				processOrderContext.put("orderName", orderName);
				processOrderContext.put("districtGeoId", districtGeoId);
				//processOrderContext.put("refNo", refNo);
				processOrderContext.put("orderDate", orderDate);
				//processOrderContext.put("fromDate", (String)UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
				//processOrderContext.put("thruDate", (String)UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()));
				processOrderContext.put("estimatedDeliveryDate", estimatedDeliveryDate);
				processOrderContext.put("incTax", "Y");
				result = CreateMaterialPO(dctx, processOrderContext);
				if(ServiceUtil.isError(result)){
					Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
					request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
					return "error";
				}
			  
			}
		}catch(GenericEntityException e){
			Debug.logError("Cannot create PurchaseOrder for cancelled orderId : "+orderId, module);
			request.setAttribute("_ERROR_MESSAGE_", "Cannot create PurchaseOrder for cancelled orderId : "+orderId);	
			return "error";
		}
		
		/*//creating supplier product here
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
				}*/
				
				if(UtilValidate.isNotEmpty(orderId)){
					Map<String, Object> orderAssocMap = FastMap.newInstance();
					orderAssocMap.put("orderId", result.get("orderId"));
					orderAssocMap.put("toOrderId", orderId);
					orderAssocMap.put("userLogin", userLogin);
					result = createOrderAssoc(dctx,orderAssocMap);
					if(ServiceUtil.isError(result)){
						Debug.logError("Unable do Order Assoc: " + ServiceUtil.getErrorMessage(result), module);
						request.setAttribute("_ERROR_MESSAGE_", "Unable do Order Assoc...! "+ServiceUtil.getErrorMessage(result));
						return "error";
					}
				}
				if(UtilValidate.isNotEmpty(orderId)){
				    Map<String, Object> orderStatusMap = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", "DRAFTPO_PROPOSAL", "userLogin", userLogin);
				    Map<String, Object> statusResult = null;
			        try{
				         statusResult = dispatcher.runSync("changeOrderStatus", orderStatusMap);
				    }catch (Exception e) {
					     Debug.logError("Problems adjusting order header status for order #" + orderId, module);
	                }
			    }

			try{
				
				if (UtilValidate.isNotEmpty(address1)){
					input = UtilMisc.toMap("userLogin", userLogin, "partyId",partyIdTo, "address1",address1, "address2", address2, "city", city, "stateProvinceGeoId", stateProvinceGeoId, "postalCode", postalCode, "contactMechPurposeTypeId","SHIPPING_LOCATION");
					resultContatMap =  dispatcher.runSync("createPartyPostalAddress", input);
					if (ServiceUtil.isError(resultContatMap)) {
						Debug.logError(ServiceUtil.getErrorMessage(resultContatMap), module);
		            }
					contactMechId = (String) resultContatMap.get("contactMechId");
					 
					Object tempInput = "SHIPPING_LOCATION";
					input = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "partyId",partyIdTo, "contactMechPurposeTypeId", tempInput);
					resultContatMap =  dispatcher.runSync("createPartyContactMechPurpose", input);
					if (ServiceUtil.isError(resultContatMap)) {
					    Debug.logError(ServiceUtil.getErrorMessage(resultContatMap), module);
		            }
					partyIdTo = (String) resultContatMap.get("partyId"); 
				 }else{
					 
					 try {
		                    GenericValue orderParty = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyIdTo));
		                    Collection<GenericValue> shippingContactMechList = ContactHelper.getContactMech(orderParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
		                    if (UtilValidate.isNotEmpty(shippingContactMechList)) {
		                        GenericValue shippingContactMech = (shippingContactMechList.iterator()).next();
		                        contactMechId= shippingContactMech.getString("contactMechId");
		                    }
		                } catch (GenericEntityException e) {
		                    Debug.logError(e, "Error setting shippingContactMechId in setDefaultCheckoutOptions() method.", module);
		                }
					 
				 }
				
				
				}catch (GenericServiceException e) {
					Debug.logError(e, module);

				} 
				if(UtilValidate.isNotEmpty(contactMechId)){
					try{
		        GenericValue OrderContactMech = delegator.makeValue("OrderContactMech", FastMap.newInstance());
		        OrderContactMech.set("orderId", result.get("orderId"));
		        OrderContactMech.set("contactMechPurposeTypeId", "SHIPPING_LOCATION");
		        OrderContactMech.set("contactMechId", contactMechId);
            	delegator.createOrStore(OrderContactMech);
					}catch (Exception ex) {
						request.setAttribute("_ERROR_MESSAGE_", "Error while storing shipping Details for Order: "+result.get("orderId"));	  	 
			        }
				}
		request.setAttribute("_EVENT_MESSAGE_", "Entry successful for party: "+partyId+" and  PO :"+result.get("orderId"));	
		request.setAttribute("orderId", orderId); 
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
	  	String shipToPartyId = (String) context.get("shipToPartyId");
		String issueToDeptId = (String) context.get("issueToDeptId");
	  	List<Map> termsList = (List)context.get("termsList");
	  	List<Map> otherChargesAdjustment = (List)context.get("adjustmentDetail");
	  	List<Map> otherTermDetail = (List)context.get("otherTerms");
	  	String incTax = (String)context.get("incTax");
	  	boolean beganTransaction = false;
	  	String PONumber=(String) context.get("PONumber");
        Timestamp orderDate = (Timestamp)context.get("orderDate");
        Timestamp estimatedDeliveryDate = (Timestamp)context.get("estimatedDeliveryDate");
		String orderName = (String)context.get("orderName");
		String fileNo = (String)context.get("fileNo");
		String quotationNo = (String)context.get("quotationNo");
		String refNo = (String)context.get("refNo");
		String orderId = (String)context.get("orderId");	
	  	String currencyUomId = "INR";
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		String fromDate = (String)context.get("fromDate");
		String thruDate = (String)context.get("thruDate");
		String billToPartyId = (String) context.get("billFromVendorPartyId");
		String districtGeoId = (String) context.get("districtGeoId");
		if(UtilValidate.isEmpty(billToPartyId)){
			billToPartyId="Company";
		}
		
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
			cart.setShipToCustomerPartyId(shipToPartyId);
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
			if(UtilValidate.isNotEmpty(quotationNo)){
				cart.setOrderAttribute("QUOTATION_NUMBER",quotationNo);
			}
			if(UtilValidate.isNotEmpty(districtGeoId)){
				cart.setOrderAttribute("DST_ADDR",districtGeoId);
			}
		} catch (Exception e) {
			
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}

			try{
				beganTransaction = TransactionUtil.begin(7200);
				
				String productId = "";
				
				String remarks = "";
				int count = 0;
				for (Map<String, Object> prodQtyMap : productQtyList) {
					List taxList=FastList.newInstance();
					
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal unitPrice = BigDecimal.ZERO;
					BigDecimal unitListPrice = BigDecimal.ZERO;
					BigDecimal vatPercent = BigDecimal.ZERO;
					BigDecimal vatAmount = BigDecimal.ZERO;
					BigDecimal cstAmount = BigDecimal.ZERO;
					BigDecimal cstPercent = BigDecimal.ZERO;
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
					
					if(UtilValidate.isNotEmpty(prodQtyMap.get("remarks"))){
						remarks = (String)prodQtyMap.get("remarks");
					}
					
					if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
						quantity = (BigDecimal)prodQtyMap.get("quantity");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
						unitPrice = (BigDecimal)prodQtyMap.get("unitPrice");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
						unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
						vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
						vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
						cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
						cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("orderItemSeqId"))){
						orderItemSeqId = (String)prodQtyMap.get("orderItemSeqId");
					}
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
					}*/

					/*if(bedAmount.compareTo(BigDecimal.ZERO)>0){
		        		Map taxDetailMap = FastMap.newInstance();
			    		taxDetailMap.put("taxType", "BED_PUR");
			    		taxDetailMap.put("amount", bedAmount);
			    		taxDetailMap.put("percentage", bedPercent);
			    		taxList.add(taxDetailMap);
					}*/
					/*if(vatAmount.compareTo(BigDecimal.ZERO)>0){
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
					}*/
					/*if(bedcessAmount.compareTo(BigDecimal.ZERO)>0){
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
					}*/
					if(vatPercent.compareTo(BigDecimal.ZERO)>0){
		        		
		        		vatAmount = ((quantity.multiply(unitPrice)).multiply(vatPercent)).divide(new BigDecimal("100"));
		        		
		        		Map taxDetailMap = FastMap.newInstance();
		        		taxDetailMap.put("orderAdjustmentTypeId","VAT_PUR");
		        		taxDetailMap.put("sourcePercentage",vatPercent);
		        		taxDetailMap.put("amount",vatAmount);
						//taxDetailMap.put("taxAuthGeoId", partyGeoId);
			    		taxList.add(taxDetailMap);
					}
					if(cstPercent.compareTo(BigDecimal.ZERO)>0){
						
						cstAmount = ((quantity.multiply(unitPrice)).multiply(cstPercent)).divide(new BigDecimal("100"));
						
		        		Map taxDetailMap = FastMap.newInstance();
		        		taxDetailMap.put("orderAdjustmentTypeId","CST_PUR");
		        		taxDetailMap.put("sourcePercentage",cstPercent);
		        		taxDetailMap.put("amount",cstAmount);
		        		taxDetailMap.put("taxAuthGeoId", "IND");
			    		
			    		taxList.add(taxDetailMap);
					}
					
					ShoppingCartItem item = null;
					
					ShoppingCartItem cartitem = null;
					
					
					try{
						
						int itemIndx = cart.addItem(count, ShoppingCartItem.makeItem(count, productId, null, quantity, unitPrice,
					            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
					            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
						
						item = cart.findCartItem(itemIndx);
						
						item.setOrderItemAttribute("remarks",remarks);
						
						if( (UtilValidate.isNotEmpty(orderItemSeqId)) && (UtilValidate.isNotEmpty(orderId))){
							item.setAssociatedOrderId(orderId);
							item.setAssociatedOrderItemSeqId(orderItemSeqId);
							item.setOrderItemAssocTypeId("BackToBackOrder");
						}
						count++;
						
						//item.setTaxDetails(taxList);
						
						for(int i=0; i<taxList.size(); i++){
							Map taxMap = (Map) taxList.get(i);
							if(  ((BigDecimal) taxMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
								GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxMap);
				 				item.addAdjustment(orderAdjustment);
								
				 				unitListPrice.add((BigDecimal) taxMap.get("amount"));
							}
						}
						
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
				
				//update PurposeType
				try{
				GenericValue orderHeaderPurpose = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				orderHeaderPurpose.set("purposeTypeId", "BRANCH_PURCHASE");
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
	   	
		public static Map<String, Object> createOrderAssoc(DispatchContext ctx,Map<String, ? extends Object> context) {
			Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			String orderId = (String) context.get("orderId");
			String toOrderId = (String) context.get("toOrderId");
			String orderAssocTypeId = "BackToBackOrder";
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> result = ServiceUtil.returnSuccess();
			try{
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", toOrderId), false);
				if(UtilValidate.isEmpty(orderHeader)){
					Debug.logError("Please Enter Valid PO Number", module);
					return ServiceUtil.returnError("Please Enter Valid PO Number");
				}
				//orderAssocTypeId = orderHeader.getString("orderTypeId");
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
		
  		public static Map<String, Object> approvePurchaseOrderWithEmail(DispatchContext dctx, Map context) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Locale locale = (Locale) context.get("locale");
			String salesChannelEnumId = (String) context.get("salesChannelEnumId");
			String partyId=(String) context.get("partyId");
			String orderId = (String) context.get("orderId");
			String newStatus = (String) context.get("statusId");
			String smsContent = "";
			String salesOrderId = " ";
		
			// get the order header
			GenericValue orderHeader = null;
			try {
				orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				Debug.log("orderAssocList ==============="+orderAssocList);
				if(UtilValidate.isNotEmpty(orderAssocList)){
					salesOrderId = (EntityUtil.getFirst(orderAssocList)).getString("toOrderId");
				}
	        } catch (GenericEntityException e) {
	            Debug.logError(e, "Cannot get OrderHeader record", module);
	        }
	        if (orderHeader == null) {
	            Debug.logError("OrderHeader came back as null", module);
				return ServiceUtil.returnError("OrderHeader came back as null"+orderId);
	        }
	        String orderHeaderStatusId = orderHeader.getString("statusId");
	        Timestamp orderDate = orderHeader.getTimestamp("orderDate");
	        String orderDateStr=UtilDateTime.toDateString(orderDate,"dd-MM-yyyy");
	        // now set the new order status
            if (newStatus != null && !newStatus.equals(orderHeaderStatusId)) {
                Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", newStatus, "userLogin", userLogin);
                Map<String, Object> newSttsResult = null;
                try {
                    newSttsResult = dispatcher.runSync("changeOrderStatus", serviceContext);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the changeOrderStatus service", module);
                }
                if (ServiceUtil.isError(newSttsResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
                }
            }
	        
	        List<GenericValue> orl = null;  
	        // boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
			// sending Mail to supplier
	        String suppPartyId=null;
	        try {
	        	orl = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "SUPPLIER_AGENT"));
	        } catch (GenericEntityException e) {
	        	Debug.logError(e, module);
	        }
	         
	        if (UtilValidate.isNotEmpty(orl)) {
	        	GenericValue orderRole = EntityUtil.getFirst(orl);
	        	suppPartyId = orderRole.getString("partyId");
	        }
			String senderEmail = null;
			if(UtilValidate.isNotEmpty(suppPartyId)){
				
				try{
			        Map<String, Object> originEmail = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", suppPartyId, "userLogin", userLogin));
			        if (UtilValidate.isNotEmpty(originEmail.get("emailAddress"))) {
			            senderEmail = (String) originEmail.get("emailAddress");
			        }
				}catch (GenericServiceException e) {
	                Debug.logError(e, "Problem while getting email address", module);
	            }
				
				Map sendMailParams = FastMap.newInstance();
				String productDetails="";
				try{
	                List<GenericValue> items = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	                //Debug.log("items==============================="+items);
	                for (GenericValue item : items) {
	                	String productId = item.getString("productId");
	                	BigDecimal qty=item.getBigDecimal("quantity");
	                	String qtystrng="0";
	                	String desc="";
	                	if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(qty)) {
	                		qtystrng=String.valueOf(qty);                     
	                		GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
	                		desc=product.getString("description");
	                	}
	                	productDetails=productDetails+" "+desc+","+qtystrng;
	                	smsContent = smsContent + " " + qtystrng + " KGs of " + desc + ",";
	                }
	                //smsContent = smsContent +" For future enquiries quote the P.O number.";
	                //Debug.log("smsContent================================"+smsContent);
				}catch(GenericEntityException ex){
					Debug.log("Problem in fetching orderItems");
				}
			
				if(UtilValidate.isNotEmpty(senderEmail)){
	
			        /*sendMailParams.put("sendTo", "harish@vasista.in");
			        sendMailParams.put("sendFrom", UtilProperties.getPropertyValue("general.properties", "defaultFromEmailAddress"));
			        sendMailParams.put("subject", "Purchase Order"+orderId);
			        sendMailParams.put("contentType", "text/html");
			        sendMailParams.put("userLogin", userLogin);  */
					String Msg=productDetails+" You are requested to submit the bills in quadruplicate towards the supply of said materials. Also please quote the Purchase Order No and Date in all your Letters,Delivery, Notes, and Invoices etc";
			        String Msgbody="Dear "+org.ofbiz.party.party.PartyHelper.getPartyName(delegator,suppPartyId, false)+",  \n"+Msg;
			        sendMailParams.put("body", Msgbody);
			        //Debug.log("sendMailParams====================================="+sendMailParams);
		          
			        try{ 
			        	Map partyEmailInfo =FastMap.newInstance();
			        	partyEmailInfo.put("orderId",orderId);
			        	partyEmailInfo.put("sendTo", senderEmail);
			        	partyEmailInfo.put("sendFrom", UtilProperties.getPropertyValue("general.properties", "defaultFromEmailAddress"));
			        	partyEmailInfo.put("partyId", suppPartyId);
			        	partyEmailInfo.put("sendCc", "vikram@vasista.in");
			        	partyEmailInfo.put("subject", "Purchase Order"+orderId);
			        	partyEmailInfo.put("userLogin", userLogin);
			        	partyEmailInfo.put("bodyText", Msgbody);
			        	Map partyInfoListResult = dispatcher.runSync("sendPurchaseOrderEmailToParty", partyEmailInfo);
			        	if (ServiceUtil.isError(partyInfoListResult)) {
			        		return ServiceUtil.returnError("Unable to send to  Purchase Order Email To Party "+ suppPartyId);
						}else{
							Debug.log("Successfully Sent Purchase Order  Email To Party "+suppPartyId +" Email " +senderEmail);
						}
			        }catch(GenericServiceException e1){
		            	Debug.log("Problem in sending email");
			        }
			        
			        /*try{
		                Map resultCtxMap = dispatcher.runSync("sendMail", sendMailParams, 360, true);
		                if(ServiceUtil.isError(resultCtxMap)){
		                	Debug.log("Problem in calling service sendMail");
		                }
		            }catch(GenericServiceException e1){
		            	Debug.log("Problem in sending email");
					}*/
				 
				} 
			}
			
			String suppName=org.ofbiz.party.party.PartyHelper.getPartyName(delegator,suppPartyId, false);
			
			String orderApprovalMessageToWeaver = UtilProperties.getMessage("ProductUiLabels", "PoApprovalMessageToWeaver", locale);
			orderApprovalMessageToWeaver = orderApprovalMessageToWeaver.replaceAll("poNumber", orderId);
			orderApprovalMessageToWeaver = orderApprovalMessageToWeaver.replaceAll("orderId", salesOrderId);
			orderApprovalMessageToWeaver = orderApprovalMessageToWeaver.replaceAll("supplierName", suppName);
			Debug.log("orderApprovalMessageToWeaver =============="+orderApprovalMessageToWeaver);
			
			// send sms to weaver
			List<GenericValue> corl = null;
			String customerId=null;
			try {
				corl = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "SHIP_TO_CUSTOMER"));
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			
			if (UtilValidate.isNotEmpty(corl)) {
				GenericValue custOrderRole = EntityUtil.getFirst(corl);
				customerId = custOrderRole.getString("partyId");
			}
			String customerName=org.ofbiz.party.party.PartyHelper.getPartyName(delegator,customerId, false);
			
			Map<String, Object> getTelParams = FastMap.newInstance();
			if(UtilValidate.isEmpty(customerName)){
				customerName=customerId;
			}
			
        	getTelParams.put("partyId", customerId);
            getTelParams.put("userLogin", userLogin); 
            try{
            	Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
	            }
	            String contactNumberTo = (String) serviceResult.get("contactNumber");            
	            String countryCode = (String) serviceResult.get("countryCode");
	            if(UtilValidate.isEmpty(contactNumberTo)){
	            	contactNumberTo = "9502532897";
	            }
	            //contactNumberTo = "9440625565";
	            Debug.log("contactNumberTo = "+contactNumberTo);
	            if(UtilValidate.isNotEmpty(contactNumberTo)){
	            	 if(UtilValidate.isNotEmpty(countryCode)){
	            		 contactNumberTo = countryCode + contactNumberTo;
	            	 }
	            	 Debug.log("contactNumberTo ===== "+contactNumberTo);
	            	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                 sendSmsParams.put("contactNumberTo", contactNumberTo);
	                 sendSmsParams.put("text", orderApprovalMessageToWeaver); 
	                 //Debug.log("sendSmsParams====================="+sendSmsParams);
	                 serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	                 if (ServiceUtil.isError(serviceResult)) {
	                     Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                     return serviceResult;
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
             
            String orderApprovalMessageToSupplier = UtilProperties.getMessage("ProductUiLabels", "PoApprovalMessageToSupplier", locale);
            orderApprovalMessageToSupplier = orderApprovalMessageToSupplier.replaceAll("orderId", orderId);
            orderApprovalMessageToSupplier = orderApprovalMessageToSupplier.replaceAll("societyName", customerName);
            orderApprovalMessageToSupplier = orderApprovalMessageToSupplier.replaceAll("material", smsContent);
			Debug.log("orderApprovalMessageToSupplier =============="+orderApprovalMessageToSupplier);
			
            // send sms to supplier
            
            Map<String, Object> getSupplierTelParams = FastMap.newInstance();
            getSupplierTelParams.put("partyId", suppPartyId);
            getSupplierTelParams.put("userLogin", userLogin);                    	
            try{
            	Map serviceResult = dispatcher.runSync("getPartyTelephone", getSupplierTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
	            } 
	            String contactNumberTo = (String) serviceResult.get("contactNumber");
	            String countryCode = (String) serviceResult.get("countryCode");
	            if(UtilValidate.isEmpty(contactNumberTo)){
	            	contactNumberTo = "9502532897";
	            }
	            //contactNumberTo = "9440625565";
	            Debug.log("contactNumberTo = "+contactNumberTo);
	            if(UtilValidate.isNotEmpty(contactNumberTo)){
	            	 if(UtilValidate.isNotEmpty(countryCode)){
	            		 contactNumberTo = countryCode + contactNumberTo;
	            	 }
	            	 Debug.log("contactNumberTo ===== "+contactNumberTo);
	            	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                 sendSmsParams.put("contactNumberTo", contactNumberTo);
	                 sendSmsParams.put("text", orderApprovalMessageToSupplier); 
	                 serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	                 if (ServiceUtil.isError(serviceResult)) {
	                     Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                     return serviceResult;
	                 }
	            	
	            }
            }catch(GenericServiceException e1){
	         	Debug.log("Problem in sending sms to the supplier");
			}
            result.put("salesChannelEnumId", salesChannelEnumId);
            return result;
		}
  		
  		public static Map<String, Object> getOrderItemSummary(DispatchContext dctx, Map context) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Locale locale = (Locale) context.get("locale");
			String orderId = (String) context.get("orderId");
	        List productIdsList = FastList.newInstance();
    		Map productSummaryMap = FastMap.newInstance();
    		
    		
    		String branchId = null;
    		List<GenericValue> billFromVendors = null; 
    		try {
    			billFromVendors = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR"));
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			
			if (UtilValidate.isNotEmpty(billFromVendors)) {
				branchId = (EntityUtil.getFirst(billFromVendors)).getString("partyId");
			}
			
			String supplierId = null;
    		List<GenericValue> suppliers = null; 
    		try {
    			suppliers = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "SUPPLIER"));
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			
			if (UtilValidate.isNotEmpty(suppliers)) {
				supplierId = (EntityUtil.getFirst(suppliers)).getString("partyId");
			}
    		
			String supplierGeoId = null;
			List supplierContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, supplierId, false, "TAX_CONTACT_MECH");
	        if(UtilValidate.isNotEmpty(supplierContactMechValueMaps)){
	        	supplierGeoId = (String)((GenericValue) ((Map) supplierContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
	        }
	        
	        String branchGeoId = null;
	        List branchContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, branchId, false, "TAX_CONTACT_MECH");
	        if(UtilValidate.isNotEmpty(branchContactMechValueMaps)){
	        	branchGeoId = (String)((GenericValue) ((Map) branchContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
	        }
    		
			try{
                List<GenericValue> items = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
                Set orderProductsSet = new HashSet(EntityUtil.getFieldListFromEntityList(items, "productId", true));    		
                productIdsList = new ArrayList(orderProductsSet);
               Iterator<String> it = productIdsList.iterator();
               while (it.hasNext()) {
                	String productId = (String) it.next();                    
    				List itemSeqDetail = FastList.newInstance();
    				String Unit="";
    				BigDecimal vatPercent = BigDecimal.ZERO;
					BigDecimal cstPercent = BigDecimal.ZERO;
    				
    				if( (UtilValidate.isNotEmpty(supplierGeoId)) && (UtilValidate.isNotEmpty(branchGeoId))   ){
    		        	
    		        	Map prodCatTaxCtx = UtilMisc.toMap("userLogin",userLogin);	  	
    					prodCatTaxCtx.put("productId", productId);
    					prodCatTaxCtx.put("taxAuthGeoId", branchGeoId);
    					//prodCatTaxCtx.put("taxAuthorityRateTypeId", orderTaxType);
    				  	try{
    				  		Map resultCtx = dispatcher.runSync("calculateTaxesByGeoId",prodCatTaxCtx);  	
    				  		Debug.log("resultCtx =========="+resultCtx);
    				  		
    				  		if(supplierGeoId.equals(branchGeoId)){
    				  			vatPercent = (BigDecimal) resultCtx.get("vatPercent");
        				  		Debug.log("vatPercent =========="+vatPercent);
    				  		}
    				  		else{
    				  			cstPercent = (BigDecimal) resultCtx.get("cstPercent");
        				  		Debug.log("cstPercent =========="+cstPercent);
    				  		}
    				  		
    				  	}catch (GenericServiceException e) {
    				  		Debug.logError(e , module);
    				  		return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
    				  	}
    		        	
    		        }
    				
                    
			    	List<GenericValue> headerItems = EntityUtil.filterByCondition(items, EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
			    	BigDecimal prdQuantity=BigDecimal.ZERO;
			    	BigDecimal prdAmount=BigDecimal.ZERO;
			    	BigDecimal unitListPrice=BigDecimal.ZERO;
			    	BigDecimal bedPercent=BigDecimal.ZERO;
			    	BigDecimal bundleQuantity=BigDecimal.ZERO;
			    	BigDecimal baleAmount=BigDecimal.ZERO;
			    	BigDecimal bundleUnitListPrice=BigDecimal.ZERO;

			    	//BigDecimal cstPercent=BigDecimal.ZERO;
			    	//BigDecimal vatPercent=BigDecimal.ZERO;
			    	
			    	
	                List<GenericValue> itemAttrs = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

			    	
			    	for (int i = 0; i < headerItems.size(); i++) {						
						GenericValue eachProductList = (GenericValue)headerItems.get(i);
						BigDecimal quantity=(BigDecimal)eachProductList.getBigDecimal("quantity");
						unitListPrice=(BigDecimal)eachProductList.getBigDecimal("unitPrice");
						List<GenericValue> ItemAtrs = EntityUtil.filterByCondition(itemAttrs, EntityCondition.makeCondition("orderItemSeqId",EntityOperator.EQUALS,eachProductList.get("orderItemSeqId")));
						BigDecimal balequantity=BigDecimal.ZERO;
						String yarUOM="";
						for (int j = 0; j < ItemAtrs.size(); j++) {	
							GenericValue ItemAtrsList = (GenericValue)ItemAtrs.get(j);

							if("BALE_QTY".equals((String)ItemAtrsList.get("attrName"))){
								 balequantity=new BigDecimal((String)ItemAtrsList.get("attrValue"));
							}
							if("BANDLE_UNITPRICE".equals((String)ItemAtrsList.get("attrName"))){
								bundleUnitListPrice=new BigDecimal((String)ItemAtrsList.get("attrValue"));
							}
							if("YARN_UOM".equals((String)ItemAtrsList.get("attrName"))){
								yarUOM=(String)ItemAtrsList.get("attrValue");
							}
						}
						Unit=yarUOM;
						if("Bale".equals(yarUOM)){
							balequantity=balequantity.multiply(new BigDecimal(40));
						}else if("Half-Bale".equals(yarUOM)){
							balequantity=balequantity.multiply(new BigDecimal(20));
						}else{
							balequantity=balequantity;
						}
						String orderItemSeqId=(String)eachProductList.get("orderItemSeqId");
						itemSeqDetail.add(orderItemSeqId);
						BigDecimal amount=unitListPrice.multiply(quantity);
						prdQuantity =prdQuantity.add(quantity);
						bundleQuantity=bundleQuantity.add(balequantity);
						prdAmount =prdAmount.add(amount);
						
			    	}
		       		Map productDetailsMap = FastMap.newInstance();

		       		productDetailsMap.put("unitListPrice",unitListPrice);
		       		productDetailsMap.put("quantity",prdQuantity);
		       		productDetailsMap.put("Unit",Unit);
		       		productDetailsMap.put("bundleQuantity",bundleQuantity);
		       		productDetailsMap.put("bundleUnitListPrice",bundleUnitListPrice);
		       		productDetailsMap.put("amount",prdAmount);
		       		productDetailsMap.put("bedPercent",bedPercent);
		       		productDetailsMap.put("cstPercent",cstPercent);
		       		productDetailsMap.put("vatPercent",vatPercent);
		       		productDetailsMap.put("itemSeqList",itemSeqDetail);
		       		
		       		
		       		
			    	productSummaryMap.put(productId,productDetailsMap);
               
			    	//Debug.log("prdQuantity============================================"+prdQuantity);
			    	//Debug.log("prdAmount============================================"+prdAmount);               
               
               }
		    	//Debug.log("productSummaryMap============================================"+productSummaryMap);
                //smsContent = smsContent +" For future enquiries quote the P.O number.";
                //Debug.log("smsContent================================"+smsContent);
			}catch(GenericEntityException ex){
				Debug.log("Problem in fetching orderItems");
			}
			result.put("productSummaryMap",productSummaryMap);
			return result;
  		}	
  		
  		
  		public static Map<String, Object> getBoHeader(DispatchContext dctx, Map context) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Locale locale = (Locale) context.get("locale");
			String branchId = (String) context.get("branchId");
	        List productIdsList = FastList.newInstance();
    		Map boHeaderMap = FastMap.newInstance();
			List<GenericValue> tenantConfigCheck = FastList.newInstance();
			try{
				List condList=FastList.newInstance();
				condList.add(EntityCondition.makeCondition("propertyName", EntityOperator.LIKE,"%"+branchId+"%"));
				condList.add(EntityCondition.makeCondition("propertyTypeEnumId", EntityOperator.EQUALS, "COMPANY_HEADER"));
				condList.add(EntityCondition.makeCondition("propertyValue", EntityOperator.EQUALS, "Y"));
				EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
				tenantConfigCheck = delegator.findList("TenantConfiguration",cond, null , null, null, false);
				if (UtilValidate.isNotEmpty(tenantConfigCheck)) {
					for (int i = 0; i < tenantConfigCheck.size(); i++) {						
						GenericValue eachProductList = (GenericValue)tenantConfigCheck.get(i);					
						String header=(String)eachProductList.get("description");
						boHeaderMap.put("header"+i,header);
			    	}
				}
			}catch(GenericEntityException ex){
				Debug.log("Problem in fetching orderItems");
			}
			result.put("boHeaderMap",boHeaderMap);
			return result;
  		}	
  			
  		
  		
	    
}
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
	
	public static Map<String, Object> createDepotInvoice(DispatchContext ctx,Map<String, ? extends Object> context) {
		
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
				Debug.log("#####resultCtx#########"+resultCtx);
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
			      //approve invoice
			      Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
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
	

	    
}
package in.vasista.vbiz.byproducts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.io.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.accounting.tax.TaxAuthorityServices;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.DateTimeConverters;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.network.LMSSalesHistoryServices;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderServices;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.price.PriceServices;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.base.util.UtilMisc;


public class ByProductServices {
	
	public static final String module = ByProductServices.class.getName();
	
	public static String processIndentEntry(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);		
		String salesChannel = "WEB_SALES_CHANNEL";
		String partyId = "";
		String approveOrder="";		
		String orderDateStr = (String) request.getParameter("orderDate");
		/*String indentTypeStr = (String) request.getParameter("indentType");*/
		String productStoreId = (String)request.getParameter("productStoreId");
		partyId = (String) request.getParameter("partyId");	
		String currencyUomId = UtilHttp.getCurrencyUom(request);
		String orderName = (String) request.getParameter("orderName");
        String productId = null;
		String quantityStr = null;
		Timestamp effectiveDate = null;
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		BigDecimal quantity = BigDecimal.ZERO;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> resultMap = FastMap.newInstance();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		GenericValue subscription = null;		
		if (UtilValidate.isNotEmpty(orderDateStr)) { 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				effectiveDate = new java.sql.Timestamp(sdf.parse(orderDateStr).getTime());
				effectiveDate = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ orderDateStr, module);
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				return "error";
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ orderDateStr, module);
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				return "error";
			}
		}
		if (UtilValidate.isEmpty(partyId)) {
			request.setAttribute("_ERROR_MESSAGE_","Customer Id should not be empty");
			return "error";
		}
		// Get the parameters as a MAP, remove the productId and quantity
		// params.
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("Nothing has been selected for = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_","No selection has been made" );
			return "error";
		} 
		GenericValue product =null;
		/*try {		
			product = delegator.findOne("Product",UtilMisc.toMap("productId",paramMap.get("productId"+ UtilHttp.MULTI_ROW_DELIMITER + "0")),false);
			List<GenericValue> prodCatalogCategoryList = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,product.getString("primaryProductCategoryId")),null, null, null, false);
			List<GenericValue> productStoreCatalogList = delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("prodCatalogId",EntityOperator.EQUALS,(String) prodCatalogCategoryList.get(0).getString("prodCatalogId")), null, null, null, false);
			//productStoreId = (String) productStoreCatalogList.get(0).getString("productStoreId");
		} catch (Exception e) {
			Debug.logError(e, "Problems getting productStore for product: "+ product.getString("productId"), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}*/
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
		cart.setOrderType("SALES_ORDER");
		cart.setChannelType(salesChannel);		
		cart.setBillToCustomerPartyId(partyId);
		cart.setPlacingCustomerPartyId(partyId);
		cart.setShipToCustomerPartyId(partyId);		
		cart.setEndUserCustomerPartyId(partyId);
		cart.setEstimatedDeliveryDate(effectiveDate);
		try {
			cart.setUserLogin(userLogin, dispatcher);
		} catch (Exception e) {
			Debug.logError(e, "Unable to create user login", module);
			request.setAttribute("_ERROR_MESSAGE_","Unable to create user login");
			return "error";
		}		
		for (int i = 0; i < rowCount; i++) {
			List<GenericValue> subscriptionProductsList = FastList.newInstance();
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.remove("productId" + thisSuffix);
			}			
			if (paramMap.containsKey("quantity" + thisSuffix)) {
				quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
			}

			if ((quantityStr == null) || (quantityStr.equals(""))) {
				continue;
			}
			try {
				quantity = new BigDecimal(quantityStr);
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing quantity string: "+ quantityStr, module);
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				return "error";
			}
        	try { 
        		Map<String, Object> priceResult;
                Map<String, Object> priceContext = FastMap.newInstance();
                priceContext.put("userLogin", userLogin);                                                                 
                priceContext.put("productStoreId", productStoreId);                    
                priceContext.put("productId", productId);
                priceContext.put("partyId", partyId);
                priceContext.put("priceDate", effectiveDate);       
                
        		
        		priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);            			
                if (ServiceUtil.isError(priceResult)) {
                    Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
    				request.setAttribute("_ERROR_MESSAGE_",	"There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));
    				return "error";
                }  
                
        		cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null, 
        				new BigDecimal(quantityStr), (BigDecimal)priceResult.get("price"),
                        null, null, null, null, null, null, null,
                        null, null, null, null, null, null, dispatcher,
                        cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
        		
            } catch (Exception exc) {
                Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
				request.setAttribute("_ERROR_MESSAGE_",	"Error adding product with id " + productId + " to the cart: " + exc.getMessage());
				return "error";
            }			
		}

		cart.setDefaultCheckoutOptions(dispatcher);
		CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator,cart);
		try {
			checkout.calcAndAddTax();
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
		String orderId = (String) orderCreateResult.get("orderId");

		/*try {
			GenericValue createdOrder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			if (createdOrder != null) {
				createdOrder.put("orderSubType", indentTypeStr);
				createdOrder.store();
            }
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		if (UtilValidate.isNotEmpty(orderId)) {
			Debug.logInfo("Created test order with id: " + orderId, module);
			
			request.setAttribute("orderId", orderId);
			request.setAttribute("roleTypeId", "BILL_TO_CUSTOMER");
		}	
		return "success";
	}
	
	public static Map<String, Object> byProdSetOrderStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");

        Map<String, Object> input = FastMap.newInstance();
        input.put("statusId", statusId);
        input.put("orderId", orderId);
        input.put("setItemStatus", "Y");
        input.put("userLogin", userLogin);
        Map<String, Object> resultMap = null;
        try {
        	resultMap = dispatcher.runSync("changeOrderStatus", input);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (ServiceUtil.isError(resultMap)) {
        	Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
            return resultMap;
        }

        successResult.put("oldStatusId", resultMap.get("oldStatusId"));
        successResult.put("orderStatusId", resultMap.get("orderStatusId"));
        successResult.put("orderTypeId", resultMap.get("orderTypeId"));
        successResult.put("needsInventoryIssuance", resultMap.get("needsInventoryIssuance"));
        successResult.put("grandTotal", resultMap.get("grandTotal"));
        //Debug.logInfo("For setOrderStatus successResult is " + successResult, module);
        
       /* try{            	
    		resultMap = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", orderId,"userLogin", userLogin));
    		if (ServiceUtil.isError(resultMap)) {
                Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(resultMap), module);
        		return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(resultMap));          	            
            } 
        }catch (GenericServiceException e) {
            Debug.logError(e, module);
        } */
        //successResult.put("invoiceId", resultMap.get("invoiceId"));
        return successResult;
    }
	
	
	public static Map<String, Object> createPostPayment(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();  
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map result = FastMap.newInstance(); 
    	List invoiceIdList = FastList.newInstance();
    	String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
    	String organizationPartyId = (String) context.get("organizationPartyId");
    	String paymentPurposeType = (String) context.get("paymentPurposeType");
    	String chequeDate = (String) context.get("effectiveDate");
    	String entryDate = (String) context.get("paymentDate");
    	String issuingAuthority = (String) context.get("issuingAuthority");
    	String issuingAuthorityBranch = (String) context.get("issuingAuthorityBranch");
    	String paymentRefNum = (String) context.get("paymentRefNum");
		String statusId = "PMNT_RECEIVED";            
	    BigDecimal amount = (BigDecimal) context.get("amount");
	    String paymentTypeId = (String) context.get("paymentTypeId");
	    String partyIdTo ="Company";
	    String partyIdFrom =(String) context.get("partyIdFrom");
	    String facilityId = (String) context.get("facilityId");
	    facilityId = facilityId.toUpperCase();
	    Timestamp effectiveDate = null;
	    Timestamp paymentDate = null;
	    String finAccountFlag = null;
	    if(paymentMethodTypeId.contains("CASH")){
	    	issuingAuthority = null;
	    	issuingAuthorityBranch = null;
	    }
	    if(UtilValidate.isEmpty(facilityId)){
	    	Debug.logError("FacilityId is empty", module);
			return ServiceUtil.returnError("FacilityId Empty");
	    }
	    if(UtilValidate.isEmpty(partyIdFrom)){
	    	GenericValue partyFacility;
			try {
				partyFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false );
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
			if(UtilValidate.isNotEmpty(partyFacility)){
				partyIdFrom = partyFacility.getString("ownerPartyId");
			}
	    }
	   Map resultMap = FastMap.newInstance();
	   
		List statusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_PAID","INVOICE_WRITEOFF");
	    List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom));
        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, statusList));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        List invoices = null;
        try{
        	invoiceIdList = delegator.findList("Invoice", condition, UtilMisc.toSet("invoiceId"), null, null, false);
        	invoices = EntityUtil.getFieldListFromEntityList(invoiceIdList,"invoiceId",true);
        }catch(GenericEntityException e){
        	e.printStackTrace();
        }
        if(UtilValidate.isNotEmpty(chequeDate)){
        	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    		try {
    			effectiveDate = new java.sql.Timestamp(formatter.parse(chequeDate).getTime());
    		} catch (ParseException e) {
    			Debug.logError(e, "Problems parsing to sqlTimestamp", module);
    			return ServiceUtil.returnError("Problem parsing to sqlTimestamp");
    		}
        }else{
        	effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
        }
        if(UtilValidate.isNotEmpty(entryDate)){
        	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    		try {
    			paymentDate = new java.sql.Timestamp(formatter.parse(entryDate).getTime());
    		} catch (ParseException e) {
    			Debug.logError(e, "Problems parsing to sqlTimestamp", module);
    			return ServiceUtil.returnError("Problem parsing to sqlTimestamp");
    		}
        }else{
        	paymentDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
        }
        Debug.log("date paymentdate : "+paymentDate+"\t chequedate : "+effectiveDate);
        Map input = UtilMisc.toMap("userLogin", userLogin, "partyId", partyIdFrom, "facilityId", facilityId, "paymentPurposeType", paymentPurposeType, "organizationPartyId", organizationPartyId, "effectiveDate", effectiveDate, "paymentDate", paymentDate, "paymentTypeId", paymentTypeId, "paymentMethodTypeId", paymentMethodTypeId, "statusId", statusId, "paymentRefNum", paymentRefNum, "issuingAuthority", issuingAuthority, "issuingAuthorityBranch", issuingAuthorityBranch, "amount", amount, "invoices", invoices);
        String paymentId = null;
        try {
        	resultMap = dispatcher.runSync("createPaymentAndApplicationForInvoices", input);
        	if (ServiceUtil.isError(resultMap)) {
        		Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
        		ServiceUtil.returnError("Payment Unsuccessful");
        		return resultMap;
        	}
        	paymentId = (String) resultMap.get("paymentId");	
        	result.put("paymentId", paymentId);
        }catch (GenericServiceException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        result = ServiceUtil.returnSuccess("Payment successfully done : "+paymentId);
        return result;
	}
	public static Map<String, Object> makeAdvancePayment(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        BigDecimal amount = (BigDecimal) context.get("amount");
        Locale locale = (Locale) context.get("locale");     
        String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
        String paymentType = (String) context.get("paymentTypeId");
        String paymentRefNum = (String) context.get("paymentRefNum");
        String issuingAuthority = (String) context.get("issuingAuthority");
        String paymentPurposeType = (String) context.get("paymentPurposeType");
        String chequeDate = (String) context.get("effectiveDate");
        String entryDate = (String) context.get("paymentDate");
        String issuingAuthorityBranch = (String) context.get("issuingAuthorityBranch");
        String partyIdTo = (String) context.get("organizationPartyId");
        String partyIdFrom = (String) context.get("partyIdFrom");
        String facilityId = (String) context.get("facilityId");
        Timestamp effectiveDate = null;
        Timestamp paymentDate = null;
        String finAccountFlag = null;
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String paymentId="";
        if(UtilValidate.isNotEmpty(chequeDate)){
        	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    		try {
    			effectiveDate = new java.sql.Timestamp(formatter.parse(chequeDate).getTime());
    		} catch (ParseException e) {
    			Debug.logError(e, "Problems parsing to sqlTimestamp", module);
    			return ServiceUtil.returnError("Problem parsing to sqlTimestamp");
    		}
        }else{
        	effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
        }
        if(UtilValidate.isNotEmpty(entryDate)){
        	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    		try {
    			paymentDate = new java.sql.Timestamp(formatter.parse(entryDate).getTime());
    		} catch (ParseException e) {
    			Debug.logError(e, "Problems parsing to sqlTimestamp", module);
    			return ServiceUtil.returnError("Problem parsing to sqlTimestamp");
    		}
        }else{
        	paymentDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
        }
        GenericValue facilityParty = null;
		try {
			facilityParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		if(UtilValidate.isEmpty(facilityParty)){
			Debug.logError("No Payment party exists", module);
            return ServiceUtil.returnError("No Payment party exists");
		}
		else{
			if(UtilValidate.isEmpty(partyIdFrom)){
				partyIdFrom = facilityParty.getString("ownerPartyId");
			}
		}
        try {
        	Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", paymentType);        	
            paymentCtx.put("paymentMethodTypeId", paymentMethodTypeId);
            paymentCtx.put("partyIdFrom", partyIdFrom);
            paymentCtx.put("partyIdTo", partyIdTo);
            paymentCtx.put("paymentRefNum", paymentRefNum);
            if(UtilValidate.isNotEmpty(issuingAuthority)){
            	paymentCtx.put("issuingAuthority", issuingAuthority.toUpperCase());
            }            
            if(UtilValidate.isNotEmpty(issuingAuthorityBranch)){
            	paymentCtx.put("issuingAuthorityBranch", issuingAuthorityBranch.toUpperCase());
            }
            paymentCtx.put("effectiveDate", effectiveDate);
            paymentCtx.put("paymentPurposeType", paymentPurposeType);
            paymentCtx.put("facilityId", facilityId.toUpperCase());            
            paymentCtx.put("statusId", "PMNT_RECEIVED");            
            paymentCtx.put("amount", amount);
            paymentCtx.put("paymentDate", paymentDate);
            paymentCtx.put("effectiveDate", effectiveDate);
            paymentCtx.put("userLogin", userLogin);
            paymentCtx.put("createdByUserLogin", userLogin.getString("userLoginId"));
            paymentCtx.put("lastModifiedByUserLogin",  userLogin.getString("userLoginId"));
            paymentCtx.put("createdDate", UtilDateTime.nowTimestamp());
            paymentCtx.put("lastModifiedDate", UtilDateTime.nowTimestamp());
            Map<String, Object> paymentResult = dispatcher.runSync("createPayment", paymentCtx);
            if (ServiceUtil.isError(paymentResult)) {
            	Debug.logError(paymentResult.toString(), module);    			
                return ServiceUtil.returnError(null, null, null, paymentResult);
            }
            paymentId = (String)paymentResult.get("paymentId");
            }catch (Exception e) {
            Debug.logError(e, e.toString(), module);
            return ServiceUtil.returnError(e.toString());
        }
		Map result = ServiceUtil.returnSuccess("Payment successfully done.");
		result.put("paymentId",paymentId);
        return result;
    }
	
	public static String processStockTransfer(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);		

		String custRequestId = (String)request.getParameter("custRequestId");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		String facilityIdTo = null;
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> resultMap = FastMap.newInstance();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		GenericValue custRequest = null;	
		if (UtilValidate.isNotEmpty(custRequestId)) { // 2011-12-25 18:09:45
			try {
				custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				return "error";
			}
		}
		
		GenericValue productStore = null;
		try {
			productStore = custRequest.getRelatedOne("ProductStore");
		} catch (GenericEntityException e1) {
			Debug.logError(e1, "Problems getting productStore Details with custRequestId: "+ custRequestId, module);
			request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
			return "error";
		}
		String fromFacilityId = productStore.getString("inventoryFacilityId");
		String requestingParty = custRequest.getString("fromPartyId");
		facilityIdTo = custRequest.getString("facilityId");
		
		List conditionList = FastList.newInstance();
		/*List conditionList = UtilMisc.toList(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, requestingParty));
        conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "STOCKIST"));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
        List<GenericValue> facilityParties;
		try {
			facilityParties = delegator.findList("FacilityParty", condition, null, null, null, false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, module);
			request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
			return "error";
		}
        facilityParties = EntityUtil.filterByDate(facilityParties, nowTimeStamp);
		
		conditionList.clear();
		if(facilityParties.size() > 0){
        	GenericValue facilityParty = facilityParties.get(0);
        	facilityIdTo = (String) facilityParty.get("facilityId");
		}else{
			Debug.logError("There are no 'active parties' for the given Booth", module);
			request.setAttribute("_ERROR_MESSAGE_", "There are no 'active parties' for the given Booth");
			return "error";
		}*/
		
		String productId = null;
		
		String inventoryItemId = null;
		String inventoryTransferId = null;
		List inventoryTransferIdList = FastList.newInstance();
		Map inputMap = FastMap.newInstance();
		
		if (UtilValidate.isNotEmpty(custRequest)){
			
			 List<GenericValue> custRequestItems;
		        try {
		        	custRequestItems = custRequest.getRelated("CustRequestItem");
		        } catch (GenericEntityException e) {
		            Debug.logError(e, module);
					request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
					return "error";
		        }
			    
		        for (GenericValue custRequestItem : custRequestItems) {
		        	
		        	String custRequestItemSeqId = (String) custRequestItem.get("custRequestItemSeqId");
		        	Map inventoryItemMap = FastMap.newInstance();
		        	BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
		        	BigDecimal requestedQuantity = BigDecimal.ZERO;
		        	inventoryItemId = null;
		        	
						productId = (String) custRequestItem.get("productId");
		        	
		        		requestedQuantity = (BigDecimal) custRequestItem.get("quantity");
					
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, fromFacilityId));
					conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
					EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					
					List<GenericValue> inventoryItems;
					try {
						inventoryItems = delegator.findList("InventoryItem", condition1 , null, null, null, false );
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
						request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
						return "error";
					}
					
					for (GenericValue inventoryItem : inventoryItems) {
						
						if(UtilValidate.isNotEmpty(inventoryItem.get("availableToPromiseTotal"))){
							if((((BigDecimal) inventoryItem.get("availableToPromiseTotal")).compareTo(requestedQuantity)>= 0) && UtilValidate.isNotEmpty(inventoryItem.get("availableToPromiseTotal"))){
								inventoryItemId = (String) inventoryItem.get("inventoryItemId");
							}
							if( ((BigDecimal)(inventoryItem.get("availableToPromiseTotal")) ).compareTo(BigDecimal.ZERO) >= 0 ){
								
								String tempInventoryItemId = null;
								BigDecimal tempAvailableToPromiseTotal = BigDecimal.ZERO;
								tempInventoryItemId = (String) inventoryItem.get("inventoryItemId");
								tempAvailableToPromiseTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");
								
								inventoryItemMap.put(tempInventoryItemId, tempAvailableToPromiseTotal);
								availableToPromiseTotal = availableToPromiseTotal.add(tempAvailableToPromiseTotal);
							}
						}
					}	
					
					if(UtilValidate.isNotEmpty(inventoryItemId)){
						Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", inventoryItemId, "custRequestId", custRequestId, "statusId","IXF_COMPLETE", "facilityId",fromFacilityId, "facilityIdTo", facilityIdTo, "xferQty", requestedQuantity.toString());
						Map<String, Object> inventoryXferResult;
						try {
							inventoryXferResult = dispatcher.runSync("createInventoryTransfer", input);
							if (ServiceUtil.isError(inventoryXferResult)) {
								Debug.logError("There was an error while creating  Inventory Transfer: " + ServiceUtil.getErrorMessage(result), module);
								request.setAttribute("_ERROR_MESSAGE_", "There was an error while creating  Inventory Transfer: " + ServiceUtil.getErrorMessage(inventoryXferResult));
								return "error";
				            }
							inventoryTransferId = (String) inventoryXferResult.get("inventoryTransferId");
							inventoryTransferIdList.add(inventoryTransferId);
							
						} catch (GenericServiceException e) {
							Debug.logError(e, module);
							request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
							return "error";
						}
					}
					else if(UtilValidate.isEmpty(inventoryItemId) && ((availableToPromiseTotal).compareTo(requestedQuantity) >= 0)){
						
						Iterator invItemIter = inventoryItemMap.entrySet().iterator();
						while (invItemIter.hasNext()) {
							BigDecimal tempXferQty = BigDecimal.ZERO;
							Map.Entry invItemEntry = (Entry) invItemIter.next();
							
							BigDecimal tempATP = (BigDecimal) invItemEntry.getValue();
							if((requestedQuantity).compareTo(tempATP) >= 0){
								tempXferQty = tempATP;
								requestedQuantity = requestedQuantity.subtract(tempATP);
							}else{
								tempXferQty = requestedQuantity;
							}
							if(tempXferQty.equals(BigDecimal.ZERO)){
								continue;
							}
							Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", invItemEntry.getKey(), "custRequestId", custRequestId, "statusId","IXF_COMPLETE", "facilityId",fromFacilityId, "facilityIdTo", facilityIdTo, "xferQty", tempXferQty);
							Map<String, Object> inventoryXferResult;
							try {
								inventoryXferResult = dispatcher.runSync("createInventoryTransfer", input);
								if (ServiceUtil.isError(inventoryXferResult)) {
									Debug.logError("There was an error while creating  Inventory Transfer: " + ServiceUtil.getErrorMessage(result), module);
									request.setAttribute("_ERROR_MESSAGE_", "There was an error while creating  Inventory Transfer: " + ServiceUtil.getErrorMessage(inventoryXferResult));
									return "error";
					            }
								inventoryTransferId = (String) inventoryXferResult.get("inventoryTransferId");
								inventoryTransferIdList.add(inventoryTransferId);
							} catch (GenericServiceException e) {
								Debug.logError(e, module);
								request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
								return "error";
							}
						}
					}
					else{
						Debug.logError("There is no enough stock left for the product" + productId + "to transfer", "");
						request.setAttribute("_ERROR_MESSAGE_", "There is no enough stock left for the product " + productId + " to transfer" + ServiceUtil.getErrorMessage(result));
						return "error";
					}
					inputMap = UtilMisc.toMap("userLogin", userLogin, "custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId, "statusId","CRQ_COMPLETED");
					Map<String, Object> custItemStatusResult;
					try {
						custItemStatusResult = dispatcher.runSync("updateCustRequestItem", inputMap);
						if (ServiceUtil.isError(custItemStatusResult)) {
							Debug.logError("There was an error while updating CustRequestItem status to 'completed': " + ServiceUtil.getErrorMessage(result), module);
							request.setAttribute("_ERROR_MESSAGE_", "There was an error while updating CustRequestItem status to 'completed': " + ServiceUtil.getErrorMessage(result));
							return "error";
						}
					} catch (GenericServiceException e) {
						Debug.logError(e, module);
						request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
						return "error";
					}
		        }
		        inputMap.clear();
		    	inputMap = UtilMisc.toMap("userLogin", userLogin, "custRequestId", custRequestId, "statusId","CRQ_COMPLETED");
		    	Map<String, Object> custReqStatusResult;
				try {
					custReqStatusResult = dispatcher.runSync("updateCustRequest", inputMap);
					if (ServiceUtil.isError(custReqStatusResult)) {
						Debug.logError("There was an error while updating CustRequest status to 'completed': " + ServiceUtil.getErrorMessage(result), module);
						request.setAttribute("_ERROR_MESSAGE_", "There was an error while updating CustRequest status to 'completed': " + ServiceUtil.getErrorMessage(result));
						return "error";
					}	
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
					request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
					return "error";
				}
				
		    	request.setAttribute("inventoryTransferId", inventoryTransferId);
				request.setAttribute("inventoryTransferIdList", inventoryTransferIdList);
		}
		return "success";
	}
	
	public static String createCustReqAndItems(HttpServletRequest request,HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);		
		
		String fromPartyId = (String) request.getParameter("fromPartyId");
		String custRequestName = (String) request.getParameter("custRequestName");
		String responseRequiredDate = (String) request.getParameter("responseRequiredDate");
		/*String priority = (String) request.getParameter("priority");*/
		
		String custRequestId = null;
		
        String productId = null;
		String quantityStr = null;
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		BigDecimal quantity = BigDecimal.ZERO;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> resultMap = FastMap.newInstance();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		if (UtilValidate.isEmpty(fromPartyId)) {
			request.setAttribute("_ERROR_MESSAGE_","Customer Id should not be empty");
			return "error";
		}
		
		Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "fromPartyId", fromPartyId, "custRequestName",custRequestName, "responseRequiredDate",responseRequiredDate);
		Map<String, Object> custRequestResult;
		try {
			custRequestResult = dispatcher.runSync("createCustRequest", input);
			if (ServiceUtil.isError(custRequestResult)) {
				Debug.logError("There was an error while creating  custRequest record: " + ServiceUtil.getErrorMessage(result), module);
				request.setAttribute("_ERROR_MESSAGE_", "There was an error while creating  custRequest record: " + ServiceUtil.getErrorMessage(custRequestResult));
				return "error";
            }
			custRequestId = (String) custRequestResult.get("custRequestId");
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("Nothing has been selected for = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_","No selection has been made" );
			return "error";
		} 
		GenericValue product =null;
			
		for (int i = 0; i < rowCount; i++) {
			List<GenericValue> subscriptionProductsList = FastList.newInstance();
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.remove("productId" + thisSuffix);
			}			
			if (paramMap.containsKey("quantity" + thisSuffix)) {
				quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
			}

			if ((quantityStr == null) || (quantityStr.equals(""))) {
				continue;
			}
			try {
				quantity = new BigDecimal(quantityStr);
			} catch (Exception e) {
				Debug.logError(e, "Problems parsing quantity string: "+ quantityStr, module);
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				return "error";
			}
        	
			Timestamp sqlTimestamp = null;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			try {
				sqlTimestamp = new java.sql.Timestamp(formatter.parse(responseRequiredDate).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Problems parsing to sqlTimestamp", module);
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				return "error";
			}
			
    		Map<String, Object> custReqItemResult;
            Map<String, Object> custReqItemInput = FastMap.newInstance();
            custReqItemInput.put("userLogin", userLogin);                                                                 
            custReqItemInput.put("custRequestId", custRequestId);                    
            custReqItemInput.put("productId", productId);
            custReqItemInput.put("quantity", quantity);
            custReqItemInput.put("requiredByDate", sqlTimestamp);
            
            try {
            	custReqItemResult = dispatcher.runSync("createCustRequestItem", custReqItemInput);
    			if (ServiceUtil.isError(custReqItemResult)) {
    				Debug.logError("There was an error while creating  custRequestItem record: " + ServiceUtil.getErrorMessage(result), module);
    				request.setAttribute("_ERROR_MESSAGE_", "There was an error while creating  custRequestItem record: " + ServiceUtil.getErrorMessage(custReqItemResult));
    				return "error";
                }
    		} catch (GenericServiceException e) {
    			Debug.logError(e, module);
    			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
    			return "error";
    		}
		}

		request.setAttribute("custRequestId", custRequestId);
		return "success";
	}
	
	public static String addItemsToCustRequest(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);		
		
		String fromPartyId = (String) request.getParameter("fromPartyId");
		String custRequestName = (String) request.getParameter("custRequestName");
		String custRequestId = (String) request.getParameter("custRequestId");
		String responseRequiredDate = (String) request.getParameter("responseRequiredDate");
		/*String priority = (String) request.getParameter("priority");*/
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		
        String productId = null;
		String quantityStr = null;
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		BigDecimal quantity = BigDecimal.ZERO;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> resultMap = FastMap.newInstance();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		GenericValue custReq = null;
		try {
			custReq = delegator.findOne("CustRequest",UtilMisc.toMap("custRequestId", custRequestId),false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, "Problems getting CustRequest Details with custRequestId: "+ custRequestId, module);
			request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
			return "error";
		}
		responseRequiredDate = custReq.getString("responseRequiredDate");
		
		List<GenericValue> custRequestItems;
        try {
        	custRequestItems = custReq.getRelated("CustRequestItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
        }
		Timestamp sqlTimestamp = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sqlTimestamp = new java.sql.Timestamp(formatter.parse(responseRequiredDate).getTime());
		} catch (ParseException e) {
		}
		
		Iterator paramMapIter = paramMap.entrySet().iterator();
		while (paramMapIter.hasNext()) {
			Map.Entry prodEntry = (Entry) paramMapIter.next();
			if(UtilValidate.isNotEmpty(prodEntry.getValue()) && (!(prodEntry.getKey()).equals("custRequestId"))){
				productId = (String) prodEntry.getKey();
				quantityStr = (String) prodEntry.getValue();
				try {
					quantity = new BigDecimal(quantityStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing quantity string: "+ quantityStr, module);
					request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
					return "error";
				}
				
				for (GenericValue custRequestItem : custRequestItems) {
		        	String custRequestItemSeqId = (String) custRequestItem.get("custRequestItemSeqId");
		        	String existingProdId = null;
		        	BigDecimal existingQuantity = BigDecimal.ZERO;
		        	
		        	if (UtilValidate.isNotEmpty(custRequestItem.get("productId"))) {
		        		existingProdId = (String) custRequestItem.get("productId");
					}
		        	if (UtilValidate.isNotEmpty(custRequestItem.get("quantity"))) {
		        		existingQuantity = (BigDecimal) custRequestItem.get("quantity");
					}
		        	if(existingProdId.equals(productId)){
		        		quantity = quantity.add(existingQuantity);
		        		
		        		GenericValue custReqItemToBeRemoved = null;
		        		try {
							custReqItemToBeRemoved = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
							delegator.removeValue(custReqItemToBeRemoved);
		        		} catch (GenericEntityException e1) {
							Debug.logError(e1, "Problems getting CustRequestItem Details with custRequestId: "+ custRequestId + "and custRequestItemSeqId" + custRequestItemSeqId, module);
							request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
							return "error";
						}
		        	}
				}
				
				Map<String, Object> custReqItemResult;
	            Map<String, Object> custReqItemInput = FastMap.newInstance();
	            custReqItemInput.put("userLogin", userLogin);                                                                 
	            custReqItemInput.put("custRequestId", custRequestId);                    
	            custReqItemInput.put("productId", productId);
	            custReqItemInput.put("quantity", quantity);
	            custReqItemInput.put("requiredByDate", sqlTimestamp);
	            
	            try {
	            	custReqItemResult = dispatcher.runSync("createCustRequestItem", custReqItemInput);
	    			if (ServiceUtil.isError(custReqItemResult)) {
	    				Debug.logError("There was an error while creating  custRequestItem record: " + ServiceUtil.getErrorMessage(result), module);
	    				request.setAttribute("_ERROR_MESSAGE_", "There was an error while creating  custRequestItem record: " + ServiceUtil.getErrorMessage(custReqItemResult));
	    				return "error";
	                }
	    		} catch (GenericServiceException e) {
	    			Debug.logError(e, module);
	    			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	    			return "error";
	    		}
			}
		}
		
		return "success";
	}
	
	public static Map<String, Object> getCustReqATP(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");	
        
        String custRequestId = (String) context.get("custRequestId");  
        String productId = null;
        Map ATPTotalsMap = FastMap.newInstance();
        
        GenericValue custRequest = null;	
		if (UtilValidate.isNotEmpty(custRequestId)) { // 2011-12-25 18:09:45
			try {
				custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching details for custRequestId " + custRequestId);
			}
		}
        
		GenericValue productStore = null;
		try {
			productStore = custRequest.getRelatedOne("ProductStore");
		} catch (GenericEntityException e1) {
			Debug.logError(e1, module);
			return ServiceUtil.returnError("Problems getting productStore Details with custRequestId: " + custRequestId);
		}
		String facilityId = productStore.getString("inventoryFacilityId");
		
		if (UtilValidate.isNotEmpty(custRequest)){
			List<GenericValue> custRequestItems;
	        try {
	        	custRequestItems = custRequest.getRelated("CustRequestItem");
	        } catch (GenericEntityException e) {
	        	Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching details for custRequestItem with custRequestId" + custRequestId);
	        }
		
	        for (GenericValue custRequestItem : custRequestItems) {
	        	
	        	String custRequestItemSeqId = (String) custRequestItem.get("custRequestItemSeqId");
	        	Map inventoryItemMap = FastMap.newInstance();
	        	BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
	        	BigDecimal requestedQuantity = BigDecimal.ZERO;
	        	
	        	if (UtilValidate.isNotEmpty(custRequestItem.get("productId"))) {
					productId = (String) custRequestItem.get("productId");
				}
	        	
	        	List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
				conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				List<GenericValue> inventoryItems;
				try {
					inventoryItems = delegator.findList("InventoryItem", condition , null, null, null, false );
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error fetching details for InventoryItem with productId " + productId + "in "+facilityId );
				}
				for (GenericValue inventoryItem : inventoryItems) {
					BigDecimal tempAvailableToPromiseTotal = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(inventoryItem.get("availableToPromiseTotal"))){
						tempAvailableToPromiseTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");
						availableToPromiseTotal = availableToPromiseTotal.add(tempAvailableToPromiseTotal);
					}
				}
				ATPTotalsMap.put(productId, availableToPromiseTotal);
	        }
		}
		Map<String, Object> result = FastMap.newInstance(); 
		result.put("ATPTotalsMap", ATPTotalsMap);
		
        return result;        
    }
	
	public static Map<String, Object> getInventory(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");	
        Locale locale = (Locale) context.get("locale");
        
        String productStoreId = (String) context.get("productStoreId");
        String facilityId = (String) context.get("facilityId");
        String productId = (String) context.get("productId");
        
        Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
        if(UtilValidate.isEmpty(effectiveDate)){
        	effectiveDate = UtilDateTime.nowTimestamp();
        }
		Timestamp dayEnd = UtilDateTime.getDayEnd(effectiveDate, TimeZone.getDefault(), locale);
        
        Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
        
        List facilityList = FastList.newInstance();
        List inventoryTotalsList = FastList.newInstance();
        List conditionList = FastList.newInstance();
        
        Map<String, Object> facilityInventoryMap = new TreeMap<String, Object>();
        Map<String, Object> prodMap = new TreeMap<String, Object>();
        Map<String, Object> prodTotalsMap = new TreeMap<String, Object>();
        
        if (UtilValidate.isEmpty(productStoreId) && UtilValidate.isEmpty(facilityId)) {
            Debug.logError("One of 'ProductStoreId' or 'FacilityId' should be present", module);
            return ServiceUtil.returnError("One of 'ProductStoreId' or 'FacilityId' should be present");        	
        }
        if (UtilValidate.isNotEmpty(facilityId)) {
        	GenericValue facility = null;
        	try {
				facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching facility " + productStoreId);
			}
			if (UtilValidate.isEmpty(facility)) {
				Debug.logError("Invalid facility" + facilityId, module);
				return ServiceUtil.returnError("Invalid facility " + facilityId);
			}
			
        	
        }
        
        
        if (UtilValidate.isNotEmpty(productStoreId) && UtilValidate.isEmpty(facilityId)) {
        	/*GenericValue productStore = null;	
			try {
				productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching details for productStoreId " + productStoreId);
			}
			
			List<GenericValue> productStoreFacilities;
	        try {
	        	productStoreFacilities = productStore.getRelated("ProductStoreFacility");
	        } catch (GenericEntityException e) {
	        	Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching details from Entity ProductStoreFacility");
	        }
	        
	        productStoreFacilities = EntityUtil.filterByDate(productStoreFacilities, nowTimeStamp);
	        
	        for (GenericValue eachFacility : productStoreFacilities) {
	        	facilityList.add(eachFacility.get("facilityId"));
	        }*/
	        
	        List<GenericValue> parlours = null;
	        try {
	        	parlours = delegator.findList("Facility", EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "PARLOUR"),null, null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List parlourIdsList = EntityUtil.getFieldListFromEntityList(parlours, "facilityId", false);
			facilityList.addAll(parlourIdsList);
        }
        else{
        	facilityList.add(facilityId);
        }
        
        for(int i=0 ; i< facilityList.size() ; i++){
    		
    		List productList = FastList.newInstance();
    		String facId = (String) facilityList.get(i);
    		
    		if (UtilValidate.isEmpty(productId)) {
    			conditionList.clear();
    			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facId));
    			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    			List<GenericValue> facilityProducts;
    			
    			try {
    				facilityProducts = delegator.findList("ProductFacility", condition , null, null, null, false );
    			} catch (GenericEntityException e) {
    				Debug.logError(e, module);
    				return ServiceUtil.returnError("Error fetching details for facilityProducts with facilityId " + facId);
    			}
    			for (GenericValue eachProduct : facilityProducts) {
    	        	productList.add(eachProduct.get("productId"));
    	        }
    		}
    		else{
    			productList.add(productId);
    		}
    		
    		for(int j=0 ; j< productList.size() ; j++){
    			
    			BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
        		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
        		String prodId = (String) productList.get(j);
        		
        		conditionList.clear();
    			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
    			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facId));
    			conditionList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
    			/*conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));*/
    			EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    			List<GenericValue> inventoryItems;
    			try {
    				inventoryItems = delegator.findList("InventoryItem", condition1 , null, null, null, false );
    			} catch (GenericEntityException e) {
    				Debug.logError(e, module);
    				return ServiceUtil.returnError("Error fetching details for InventoryItem with productId " + productId + "in "+facilityId );
    			}
    			
    			
    			for (GenericValue inventoryItem : inventoryItems) {
    				
    				if(UtilValidate.isNotEmpty(inventoryItem.get("availableToPromiseTotal"))){
    					BigDecimal tempAvailableToPromiseTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");
        				availableToPromiseTotal = availableToPromiseTotal.add(tempAvailableToPromiseTotal);
    				}
    				if(UtilValidate.isNotEmpty(inventoryItem.get("quantityOnHandTotal"))){
    					BigDecimal tempQuantityOnHandTotal = (BigDecimal) inventoryItem.get("quantityOnHandTotal");
        				quantityOnHandTotal = quantityOnHandTotal.add(tempQuantityOnHandTotal);
    				}
    			}
    			
    			/*if((availableToPromiseTotal.equals(BigDecimal.ZERO)) && (quantityOnHandTotal.equals(BigDecimal.ZERO))){
					continue;
				}*/
    			
    			Map tempProdInvMap = FastMap.newInstance();
    			tempProdInvMap.put("availableToPromiseTotal", availableToPromiseTotal);
    			tempProdInvMap.put("quantityOnHandTotal", quantityOnHandTotal);
    			
    			Map tempMap = FastMap.newInstance();
    			tempMap.putAll(tempProdInvMap); 
    			
    			prodMap.put("facilityId", facId);
    			prodMap.put(prodId, tempMap);
    			
    			Map tempTotalsMap = FastMap.newInstance();
    			tempTotalsMap.putAll(tempProdInvMap); 
    			
				if (prodTotalsMap.get(prodId) == null) {
					prodTotalsMap.put(prodId, tempTotalsMap);
				}
				else{
					Map prodInvMap = (Map)prodTotalsMap.get(prodId);
					BigDecimal updateATP = (BigDecimal) prodInvMap.get("availableToPromiseTotal");
					BigDecimal updateQOH = (BigDecimal) prodInvMap.get("quantityOnHandTotal");
					
					updateATP = updateATP.add(availableToPromiseTotal);
					updateQOH = updateQOH.add(quantityOnHandTotal);
					
					prodInvMap.put("availableToPromiseTotal", updateATP);
					prodInvMap.put("quantityOnHandTotal", updateQOH);
					
					prodTotalsMap.put(prodId, prodInvMap);
				}
    			
    			tempProdInvMap.clear();
    		}
    		
    		Map tempFacMap = FastMap.newInstance();
    		tempFacMap.putAll(prodMap);
    		
			facilityInventoryMap.put(facId, tempFacMap);
			prodMap.clear();
			
    	}
    	
    	Map tempInvTotMap = FastMap.newInstance();
    	tempInvTotMap.putAll(prodTotalsMap);
    	
    	facilityInventoryMap.put("InventoryTotals", tempInvTotMap);
    	
		Map<String, Object> result = FastMap.newInstance(); 
		result.put("facilityInventoryMap", facilityInventoryMap);
		
        return result;        
    }
		
	public static String ViewOrderRequest(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);		
		String custRequestId = (String)request.getParameter("custRequestId");
		String statusId = (String)request.getParameter("statusId");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		List<GenericValue> orderList = null;
		List<GenericValue> indentList = null;
		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        String orderId = "";
        try{
        	indentList = delegator.findList("CustRequest", condition, null, null, null, false);
        	orderList = delegator.findList("OrderHeader", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),null, null, null, false);
        }
        catch(GenericEntityException e){
			Debug.logError("No custRequestId Found "+ custRequestId, module);
			return "error";
		}
    	if((UtilValidate.isNotEmpty(indentList)) && (UtilValidate.isNotEmpty(orderList))){
    		for (GenericValue eachItem : orderList) {
   				orderId = eachItem.getString("orderId");
    		}
    		request.setAttribute("orderId", orderId);
    		return "order";
   		}
    	else{
    		request.setAttribute("custRequestId", custRequestId);
    		return "indent";
    	}
	}
	
	public static Map<String, Object> deleteCustRequestItem(DispatchContext ctx, Map<String, ? extends Object> context){
      	Map<String, Object> result = FastMap.newInstance();
      	Delegator delegator = ctx.getDelegator();
      	Locale locale = (Locale) context.get("locale");
      	GenericValue userLogin = (GenericValue) context.get("userLogin");
      	LocalDispatcher dispatcher = ctx.getDispatcher();
      	String custRequestId = (String)context.get("custRequestId");
      	String custRequestItemSeqId = (String)context.get("custRequestItemSeqId");
      	List conditionList = UtilMisc.toList(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
        conditionList.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, custRequestItemSeqId));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);   
      	try{
      		 GenericValue custRequestItem= delegator.findByPrimaryKey("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId,"custRequestItemSeqId", custRequestItemSeqId)); 
      		 List<GenericValue> custRequestItemNotes = delegator.findList("CustRequestItemNote", condition, null, null, null, false);
             if(!custRequestItemNotes.isEmpty()){
      		   for (GenericValue custRequestItemNote: custRequestItemNotes) {
                 GenericPK itemNotePK = delegator.makePK("CustRequestItemNote");
                 itemNotePK.setPKFields(custRequestItemNote);
                 delegator.removeByPrimaryKey(itemNotePK);
      		   } 
      		 }
             if(custRequestItem!=null){
             GenericPK itemPK = delegator.makePK("CustRequestItem");
		     itemPK.setPKFields(custRequestItem);
		     delegator.removeByPrimaryKey(itemPK);
             result = ServiceUtil.returnSuccess("Item Removed Successfully");
             }
      	}catch (GenericEntityException e) {
            String errMsg = "Error removing IndentItem : " + e.toString();
            result = ServiceUtil.returnError("Error removing IndentItem");
        }
      	return result;
     }
	
	public static Map<String, Object> getProdStoreProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");	
        
        Timestamp salesDate = UtilDateTime.nowTimestamp();
        if(!UtilValidate.isEmpty(context.get("salesDate"))){
        	salesDate =  (Timestamp) context.get("salesDate");  
         }
        Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
        
        String productStoreId = (String) context.get("productStoreId"); 
        String isVariant = (String) context.get("isVariant");
        if(UtilValidate.isEmpty(isVariant)){
        	isVariant = "Y";
        }
        
        GenericValue productStore = null;	
		if (UtilValidate.isNotEmpty(productStoreId)) { // 2011-12-25 18:09:45
			try {
				productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching details for productStoreId " + productStoreId);
			}
		}
        
		List<GenericValue> catalogIds = null;
		if (UtilValidate.isNotEmpty(productStore)){
	        try {
	        	catalogIds = productStore.getRelated("ProductStoreCatalog");
	        } catch (GenericEntityException e) {
	        	Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching details for ProductStoreCatalog with productStoreId" + productStoreId);
	        }
		}
		catalogIds = EntityUtil.filterByDate(catalogIds, dayBegin);
		List catalogIdsFieldList = EntityUtil.getFieldListFromEntityList(catalogIds, "prodCatalogId", false);
		
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, catalogIdsFieldList));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		List<GenericValue> catalogCategoryIds;
		try {
			catalogCategoryIds = delegator.findList("ProdCatalogCategory", condition , UtilMisc.toSet("productCategoryId"), null, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error fetching details for ProdCatalogCategory with productStoreId " + productStoreId);
		}
		List categoryIdsFieldList = EntityUtil.getFieldListFromEntityList(catalogCategoryIds, "productCategoryId", false);
        
        conditionList.clear();
        
		conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryIdsFieldList));
		/*conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_EQUAL, "BYPROD"));*/
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
		conditionList.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, isVariant));
		EntityCondition prodCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		/*EntityFindOptions findOptions = new EntityFindOptions();
        findOptions.setDistinct(true);*/
		List<GenericValue> categoryProducts = null;
		try {
			categoryProducts = delegator.findList("ProductAndCategoryMember", prodCondition , UtilMisc.toSet("productId", "productName", "primaryProductCategoryId", "productCategoryId", "quantityIncluded"), null, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error fetching details for ProdCatalogCategory with productStoreId " + productStoreId);
		}
		categoryProducts = EntityUtil.filterByDate(categoryProducts, dayBegin);
		Map prodMap = FastMap.newInstance();
		List categoryProdList = FastList.newInstance();
		String categoryId = null;
		String productId = null;
		for (GenericValue categoryProd : categoryProducts) {
			productId = categoryProd.getString("productId");
			categoryId = categoryProd.getString("productCategoryId");
			if(prodMap.containsKey(categoryId)){
				categoryProdList = (List)prodMap.get(categoryId);
				List tempList = FastList.newInstance();
				tempList.addAll(categoryProdList);
				tempList.add(productId);
				prodMap.put(categoryId, tempList);
				categoryProdList.clear();
			}else{
				
				List prodList = FastList.newInstance();
				prodList.add(productId);
				
				List tempList = FastList.newInstance();
				tempList.addAll(prodList);
				
				prodMap.put(categoryId, tempList);
				
				prodList.clear();
			}
		}
		
		List productIdsList = EntityUtil.getFieldListFromEntityList(categoryProducts, "productId", true);
		Map<String, Object> result = FastMap.newInstance(); 
		result.put("productList", categoryProducts);
		result.put("categoryProduct", prodMap);
		result.put("productIdsList", productIdsList);
        return result;        
    }
	public static String ShipOrderItemsForIndent(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  String shipmentId = (String) request.getParameter("shipmentId");	 
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	  	  List custTimePeriodList = FastList.newInstance();  	 
	  	  if (shipmentId == "") {
	  			request.setAttribute("_ERROR_MESSAGE_","shipment Id is empty");
	  			return "error";
	  		}
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
	  		  return "success";
	  	  }
	  	  String inventoryItemId = null;
	  	  String quantityStr = null;
	  	  String orderId = null;
	  	  String orderItemSeqId = null;
	  	  String shipGroupSeqId = null;
	  	  BigDecimal quantity = BigDecimal.ZERO;
	  	  for (int i = 0; i < rowCount; i++) {
	  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
	  			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
	  		  }	
	  		  if(UtilValidate.isNotEmpty(quantityStr)){
	  			  try {
		  			  quantity = new BigDecimal(quantityStr);
		  		  } catch (Exception e) {
		  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
		  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
		  			  return "error";
		  		  }
	  			  if (paramMap.containsKey("inventoryItemId" + thisSuffix)) {
		  			inventoryItemId = (String) paramMap.get("inventoryItemId"+thisSuffix);
		  		  }
		  		  else {
		  			  request.setAttribute("_ERROR_MESSAGE_", "Missing inventoryItemId");
		  			  return "error";			  
		  		  }
		  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
		  			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
		  		  }	
		  		  else {
		  			  request.setAttribute("_ERROR_MESSAGE_", "Missing quantity");
		  			  return "error";			  
		  		  }		  
		  		  if (paramMap.containsKey("orderId" + thisSuffix)) {
		  			  orderId = (String) paramMap.get("orderId" + thisSuffix);
		  		  }
		  		  else {
		  			  request.setAttribute("_ERROR_MESSAGE_", "Missing orderId");
		  			  return "error";			  
		  		  }	
		  		  if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
		  			orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
		  		  }	
		  		  else {
		  			  request.setAttribute("_ERROR_MESSAGE_", "Missing orderItemSeqId");
		  			  return "error";			  
		  		  }	
		  		  if (paramMap.containsKey("shipmentId" + thisSuffix)) {
		  			  shipmentId = (String) paramMap.get("shipmentId" + thisSuffix);
		  		  }	
		  		  else {
		  			  request.setAttribute("_ERROR_MESSAGE_", "Missing shipmentId");
		  			  return "error";			  
		  		  }	
		  		  if (paramMap.containsKey("shipGroupSeqId" + thisSuffix)) {
		  			shipGroupSeqId = (String) paramMap.get("shipGroupSeqId" + thisSuffix);
		  		  }	
		  		  else {
		  			  request.setAttribute("_ERROR_MESSAGE_", "Missing shipGroupSeqId");
		  			  return "error";			  
		  		  }	
				  Map inputMap = FastMap.newInstance();
				  inputMap.put("inventoryItemId", inventoryItemId);
				  inputMap.put("quantity", quantity);
				  inputMap.put("userLogin",userLogin);
				  inputMap.put("orderId",orderId);
				  inputMap.put("orderItemSeqId", orderItemSeqId);
				  inputMap.put("shipGroupSeqId", shipGroupSeqId);				  
				  inputMap.put("shipmentId", shipmentId);
				  try {
			            Map<String, Object> serviceResult = dispatcher.runSync("issueOrderItemShipGrpInvResToShipment", inputMap);
			        } catch (GenericServiceException e) {
			            Debug.logError(e, "Trouble calling issueOrderItemShipGrpInvResToShipment service;[" + shipmentId + "]", module);
			            return "error";
			        }  
	  		  }
	  		  
	  	 }//end row count for loop
	  	List conditionList = UtilMisc.toList(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
      EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);   
    	try{
    		 GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
    		 String statusId = (String)orderHeader.get("statusId");
    		 if(statusId.equals("ORDER_COMPLETED")){
    			String custRequestId = (String)orderHeader.get("custRequestId");
    			List requestConditionList = UtilMisc.toList(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
    	        EntityCondition cond = EntityCondition.makeCondition(requestConditionList, EntityOperator.AND);
    	        GenericValue custRequest = delegator.findByPrimaryKey("CustRequest", UtilMisc.toMap("custRequestId", custRequestId));
    	        custRequest.set("statusId", "CRQ_COMPLETED");
    	        custRequest.store();
    		 }
    	}
		catch (GenericEntityException e) {
			Debug.logError(e, "Error updating custRequest status", module);
			return "error";
		}
	  	Map inMap = FastMap.newInstance();
	  	inMap.put("shipmentId", shipmentId);
	  	inMap.put("userLogin", userLogin);
	  	try {
          Map<String, Object> serviceResultss = dispatcher.runSync("createInvoiceAndSettlePayments", inMap);
      } catch (GenericServiceException e) {
          Debug.logError(e, "Trouble calling issueOrderItemShipGrpInvResToShipment service;[" + shipmentId + "]", module);
          return "error";
      }
	  	 return "success";     
	}
	public static Map<String, Object> createPhysicalInventoryAndStockVariance(DispatchContext ctx, Map<String, ? extends Object> context){
      	Map<String, Object> result = FastMap.newInstance();
      	Delegator delegator = ctx.getDelegator();
      	Locale locale = (Locale) context.get("locale");
      	GenericValue userLogin = (GenericValue) context.get("userLogin");
      	LocalDispatcher dispatcher = ctx.getDispatcher();
      	String productId = (String)context.get("productId");
      	String facilityId = (String)context.get("facilityId");
      	String varianceReasonId = (String)context.get("varianceReasonId");
      	BigDecimal quantity = (BigDecimal)context.get("quantity");
      	if((!varianceReasonId.equals("VAR_FOUND"))||(!varianceReasonId.equals("VAR_MISSHIP_ORDERED"))){
      		quantity = quantity.negate();
      	}
      	String comments = (String)context.get("comments");
      	Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
      	if(UtilValidate.isEmpty(effectiveDate)){
      		effectiveDate = UtilDateTime.nowTimestamp();
      	}
      	String physicalInventoryId = null;
      	String partyId = null;
      	
      	GenericValue facility;
		try {
			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, module);
			return ServiceUtil.returnError("Error finding Facility");
		}
		 
	    if(UtilValidate.isNotEmpty(facility)){
	    	partyId = (String) facility.get("ownerPartyId");  
	    }	
      	List<GenericValue> inventoryItems = null;
      	List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
		EntityCondition invCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		List<String> orderBy = UtilMisc.toList("datetimeReceived", "inventoryItemId");
		try {
			inventoryItems = delegator.findList("InventoryItem", invCondition , null, orderBy, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error finding inventory item ");
		}
		GenericValue inventoryItem = null;
		if(UtilValidate.isNotEmpty(inventoryItems)){
			inventoryItem = inventoryItems.get(0);
		}
		String inventoryItemId = (String) inventoryItem.get("inventoryItemId");
		
		Map<String, Object> varianceCtx = UtilMisc.<String, Object>toMap("inventoryItemId", inventoryItemId);
		varianceCtx.put("userLogin", userLogin);
		varianceCtx.put("availableToPromiseVar",quantity);
		varianceCtx.put("quantityOnHandVar", quantity);
		varianceCtx.put("varianceReasonId", varianceReasonId);
		varianceCtx.put("comments", comments);
		varianceCtx.put("partyId", partyId);
        try{
        	Map<String, Object> varianceResult = dispatcher.runSync("createPhysicalInventoryAndVariance",varianceCtx);
        	if (ServiceUtil.isError(varianceResult)) {
        		Debug.logError(varianceResult.toString(), module);
                return ServiceUtil.returnError(null, null, null, varianceResult);
            }	 
        	physicalInventoryId = (String) varianceResult.get("physicalInventoryId");
        }catch(GenericServiceException e){
        	 Debug.logError(e, e.toString(), module);
            return ServiceUtil.returnError(e.toString());
        } 
        try {
			Map populateSaleQty = dispatcher.runSync("populateInventorySummary", UtilMisc.<String, Object>toMap("facilityId", facilityId, "effectiveDate", effectiveDate, "productId", productId, "adjustments", quantity, "userLogin", userLogin));
        } catch (GenericServiceException e1) {
			Debug.logError(e1, module);
			return ServiceUtil.returnError("Error populating InventorySummary");
		}
      	result = ServiceUtil.returnSuccess("Variance Entry Successful");
      	result.put("physicalInventoryId", physicalInventoryId);
      	return result;
    }
	public static Map<String, Object> createByProdShipment(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        String estimatedDeliveryDateString = (String) context.get("estimatedDeliveryDate");
        String routeId = (String) context.get("routeId");
        String tripId = (String) context.get("tripId");
        List routesList = FastList.newInstance();
        
        Timestamp estimatedDeliveryDate = null;
        String shipmentTypeId = (String) context.get("shipmentTypeId");
        String subscriptionTypeId = "AM";
        if(shipmentTypeId.equals("PM_SHIPMENT")){
        	subscriptionTypeId = "PM";       	
        }
        String facilityGroupId = (String) context.get("facilityGroupId");
        String shipmentId = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			estimatedDeliveryDate = new java.sql.Timestamp(sdf.parse(estimatedDeliveryDateString).getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: "+ estimatedDeliveryDateString, module);			
			return ServiceUtil.returnError("Failed to Generate TruckSheet ,Cannot parse date string:" + e);
			// effectiveDate = UtilDateTime.nowTimestamp();
		}
		
		//checking for one route  or all routes
		if(UtilValidate.isNotEmpty(routeId) && (routeId.equals("AllRoutes"))){
			routesList = (List) getByproductRoutes(delegator).get("routeIdsList");
        }else{
        	routesList.add(routeId);
        }
		
        //checking for indents if indents are not there for facility(route) then throw an error
		
		List<GenericValue> subscriptionProductsList = FastList.newInstance();         
        try {        
        	List conditionList = UtilMisc.toList(
    			EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, estimatedDeliveryDate));
            conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
            		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, estimatedDeliveryDate)));
            conditionList.add(EntityCondition.makeCondition("quantity", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
            conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
            conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.IN, routesList));
            if(UtilValidate.isNotEmpty(tripId)){
            	conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
            }
           	
            EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
            List<String> orderBy = UtilMisc.toList("subscriptionId", "productSubscriptionTypeId","-productId"); 
        	subscriptionProductsList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, orderBy, null, false);
        	
        	if(UtilValidate.isEmpty(subscriptionProductsList)){
        		Debug.logError("No indents found for route :'"+routeId+"'", module);
                return ServiceUtil.returnError("No Indents found for route :"+routeId);        		
            }
        	routesList = EntityUtil.getFieldListFromEntityList(subscriptionProductsList, "sequenceNum", true);
        }catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Subscription Products", module);
            return ServiceUtil.returnError("Failed to Generate TruckSheet ,Cannot parse date string:" + e);
            //::TODO:: set shipment status
        }
		
        // checking if shipment exists for the specified day then return without creating orders.
		estimatedDeliveryDate = UtilDateTime.getDayStart(estimatedDeliveryDate);
        Timestamp dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDate);
        Timestamp dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDate);
        List conditionList = FastList.newInstance();
        List shipmentList = FastList.newInstance();
        GenericValue facilityGroup = null;
        
        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN , UtilMisc.toList("SHIPMENT_CANCELLED", "GENERATION_FAIL")));
        conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS ,shipmentTypeId));
    	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
    	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
      	conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.IN, routesList));
      	if(UtilValidate.isNotEmpty(tripId)){
      		conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
      	}
   		List existingRoutesList = FastList.newInstance();
    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    	try {
    		shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
    		existingRoutesList = EntityUtil.getFieldListFromEntityList(shipmentList, "routeId", true);
    		if(UtilValidate.isNotEmpty(existingRoutesList)){
        		routesList.removeAll(existingRoutesList);
        	}
        	if(UtilValidate.isEmpty(routesList)){
        		 Debug.logError("Failed to generate a trucksheet ,allready generated  for:"+routeId, module);             
                 return ServiceUtil.returnError("Failed to generate a trucksheet ,allready generated  for:"+routeId);
        	}
    	}catch (GenericEntityException e) {
    		 Debug.logError(e, module);             
             return ServiceUtil.returnError("Failed to find ShipmentList " + e);
		}      
    	
    	List shipmentIdsList = FastList.newInstance();
		for(int i = 0; i < routesList.size(); i++){
			
			GenericValue newEntity = delegator.makeValue("Shipment");
	        if(UtilValidate.isNotEmpty(facilityGroup)){
	        	newEntity.set("originFacilityId", facilityGroup.getString("ownerFacilityId"));
	        }
	        newEntity.set("estimatedShipDate", estimatedDeliveryDate);
	        newEntity.set("shipmentTypeId", shipmentTypeId);
	        newEntity.set("routeId", routesList.get(i));
	        if(UtilValidate.isNotEmpty(tripId)){
	        	newEntity.set("tripNum", tripId);
	        }
	        newEntity.set("statusId", "IN_PROCESS");
	        newEntity.set("createdDate", nowTimeStamp);
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	        try {
	            delegator.createSetNextSeqId(newEntity);            
	            shipmentId = (String) newEntity.get("shipmentId");
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError("Failed to create a new shipment " + e);            
	        }  	   
	              	       	
	        shipmentIdsList.add(shipmentId);
		}
		 try {
	        	Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("shipmentIds", shipmentIdsList, 
	        			"estimatedDeliveryDate", estimatedDeliveryDate,"userLogin", userLogin);
	        	runSACOContext.put("facilityGroupId", facilityGroupId);
	        	runSACOContext.put("subscriptionTypeId", subscriptionTypeId);
	        	
	            dispatcher.runAsync("runSubscriptionAutoCreateByprodOrders", runSACOContext);
	        } catch (GenericServiceException e) {
	            Debug.logError(e, "Error calling runSubscriptionAutoCreateOrders service", module);
	            return ServiceUtil.returnError(e.getMessage());
	        }  
		result.put("shipmentIdsList", shipmentIdsList);
        // attempt to create a Shipment entity
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
	
	public static Map<String, Object> runSubscriptionAutoCreateByprodOrders(DispatchContext dctx, Map<String,  Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<GenericValue> subscriptionList=FastList.newInstance();
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        Timestamp estimatedDeliveryDate = (Timestamp) context.get("estimatedDeliveryDate");
        List shipmentIds = (List) context.get("shipmentIds");
        //String facilityGroupId = (String) context.get("facilityGroupId");
       // String routeId = (String) context.get("routeId");
       String productStoreId = (String) (getByprodFactoryStore(delegator)).get("factoryStoreId");
       int orderCounter = 0;
       double elapsedSeconds;
       Timestamp startTimestamp = UtilDateTime.nowTimestamp();
       boolean beganTransaction = false;
       boolean generationFailed = false;
       for(int shipNo=0 ; shipNo <shipmentIds.size() ;  shipNo++){
    	   String shipmentId = (String)shipmentIds.get(shipNo);
    	   GenericValue shipment;
           try {
        	    beganTransaction = TransactionUtil.begin(72000);
	           	shipment=delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
	           	if (shipment == null) {
	           		Debug.logError("Shipment does not exist " + shipmentId, module);
	           		return ServiceUtil.returnError("Shipment does not exist " + shipmentId);                    	
	           	}
           }catch (GenericEntityException e) {
	       		Debug.logError(e, "Error getting shipment " + shipmentId, module);
	       		return ServiceUtil.returnError("Error getting shipment " + shipmentId + ": " + e);         	
           }
           String routeId = shipment.getString("routeId");
           String tripId = shipment.getString("tripNum");
           String shipmentTypeId = shipment.getString("shipmentTypeId");
           String subscriptionTypeId = (String) context.get("subscriptionTypeId");
           
           List<GenericValue> subscriptionProductsList = FastList.newInstance();         
           try {        
           		List conditionList = UtilMisc.toList(
       			EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, estimatedDeliveryDate));
               conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
               		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, estimatedDeliveryDate)));
               conditionList.add(EntityCondition.makeCondition("quantity", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
               conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
               if(UtilValidate.isNotEmpty(routeId)){
               		conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
               }
               if(UtilValidate.isNotEmpty(tripId)){
              		conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
               }
              
              	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
               List<String> orderBy = UtilMisc.toList("subscriptionId", "productSubscriptionTypeId","-productId"); 
           	subscriptionProductsList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, orderBy, null, false);
           }catch (GenericEntityException e) {
               Debug.logError(e, "Problem getting Subscription Products", module);
               //::TODO:: set shipment status
           }
           
           String tempSubId = "";
           String tempTypeId = "";
           String subId;
           String typeId;
           
           generationFailed = false;
          // BigDecimal totalQuantity = BigDecimal.ZERO;
           List<GenericValue> orderSubProdsList = FastList.newInstance();  
           
           for (int j = 0; j < subscriptionProductsList.size(); j++) {
           	subId = subscriptionProductsList.get(j).getString("subscriptionId");
           	typeId = subscriptionProductsList.get(j).getString("productSubscriptionTypeId");
               	if (tempSubId == "") {
               		tempSubId = subId;
               		tempTypeId = typeId;        		
               	}
                   /*condition: "!(typeId.startsWith(tempTypeId))" is to generate same order for CASH_FS and CASH*/
               	if (!tempSubId.equals(subId) || (!tempTypeId.equals(typeId))) {
   					if(UtilValidate.isNotEmpty(orderSubProdsList.get(0).getString("categoryTypeEnum")) && (orderSubProdsList.get(0).getString("categoryTypeEnum")).equals("PARLOUR")){
   						context.put("subscriptionProductsList", orderSubProdsList);
   						context.put("productStoreId", productStoreId);	
   						result = receiveParlorInventory(dctx, context);
   						if (ServiceUtil.isError(result)) {
   		        			Debug.logError("Unable to Transfer Stock: " + ServiceUtil.getErrorMessage(result), module);
   		        			generationFailed = true;
   		        		}
   					}else{
   						context.put("subscriptionProductsList", orderSubProdsList);
   						context.put("shipmentId" , shipmentId);
   						result = createSalesOrderSubscriptionProductType(dctx, context);
   						if (ServiceUtil.isError(result)) {
                   			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
                   			generationFailed = true;
                   			break;
                   		}
   					}
               		
               		BigDecimal quantity = (BigDecimal)result.get("quantity");                		
               		/*if (quantity != null) {
               			totalQuantity = totalQuantity.add(quantity);
               		}*/
               		orderCounter++;
               		if ((orderCounter % 10) == 0) {
               			elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
               			Debug.logImportant("Completed " + orderCounter + " orders [ in " + elapsedSeconds + " seconds]", module);
               		}
               		orderSubProdsList.clear();
               		tempSubId = subId;
               		tempTypeId = typeId;
               	}
               	orderSubProdsList.add(subscriptionProductsList.get(j));
           }
           
           if (orderSubProdsList.size() > 0 && !generationFailed) {
   			
   			if(UtilValidate.isNotEmpty(orderSubProdsList.get(0).getString("categoryTypeEnum")) && (orderSubProdsList.get(0).getString("categoryTypeEnum")).equals("PARLOUR")){
   				context.put("subscriptionProductsList", orderSubProdsList);
   				context.put("productStoreId", productStoreId);				
   				result = receiveParlorInventory(dctx, context);
   				if (ServiceUtil.isError(result)) {
        			Debug.logError("Unable to Transfer Stock: " + ServiceUtil.getErrorMessage(result), module);
        			generationFailed = true;
   	        	}
   			}else{
   				context.put("subscriptionProductsList", orderSubProdsList);
   				context.put("shipmentId" , shipmentId);
   				result = createSalesOrderSubscriptionProductType(dctx, context); 
   				if (ServiceUtil.isError(result)) {
   	    			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
   	    			generationFailed = true;
   	    		}  
   			}

   			/*BigDecimal quantity = (BigDecimal)result.get("quantity");
       		if (quantity != null) {
       			totalQuantity = totalQuantity.add(quantity);
       		}   */ 		
       		orderCounter++;
           }
           
   		
   		if (generationFailed) {
   			/*List<GenericValue> shipmentReceipts;
   	        try {
   	        	shipmentReceipts = shipment.getRelated("ShipmentReceipt");
   	        } catch (GenericEntityException e) {
   	        	Debug.logError("Unable to get ShipmentReceipt"+e, module);
   	    		return ServiceUtil.returnError("Unable to get ShipmentReceipt");
   	        }
   	        for (GenericValue shipmentReceipt : shipmentReceipts) {
   	        	List<GenericValue> inventoryItemDetails;
   		        try {
   		        	String inventoryItemId = shipmentReceipt.getString("inventoryItemId");
   		        	inventoryItemDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS,inventoryItemId),null, null, null, false);
   		        	delegator.removeAll(inventoryItemDetails);
   		        } catch (GenericEntityException e) {
   		        	Debug.logError("Unable to remove inventoryItemDetails"+e, module);
   		    		return ServiceUtil.returnError("Unable to remove inventoryItemDetails");
   		        }
   	        }
   	        
   	        GenericValue inventoryItem = null;
   	        
   	        for (GenericValue shipmentReceipt : shipmentReceipts) {
   				try {
   					inventoryItem = shipmentReceipt.getRelatedOne("InventoryItem");
   					delegator.removeValue(shipmentReceipt);
   					delegator.removeValue(inventoryItem);
   				} catch (GenericEntityException e) {
   					Debug.logError("Unable to remove inventoryItem"+e, module);
   		    		return ServiceUtil.returnError("Unable to remove inventoryItem");
   				}
   	        }*/
   			try {
                // only rollback the transaction if we started one...
                TransactionUtil.rollback();
            } catch (Exception e2) {
                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
            }
   			shipment.set("statusId", "GENERATION_FAIL");
   		}else {
   			shipment.set("statusId", "GENERATED");	
   		}
        try {
           	shipment.store();
        }catch (GenericEntityException e) {
       		Debug.logError(e, "Failed to update shipment status " + shipmentId, module);
       		return ServiceUtil.returnError("Failed to update shipment status " + shipmentId + ": " + e);          	
           }
       } // shipment list
       if(!generationFailed){
    	   elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
     	   Debug.logImportant("Completed " + orderCounter + " orders [ in " + elapsedSeconds + " seconds]", module);
       }
      
       return ServiceUtil.returnSuccess();
    }
	
	public static Map<String, Object> createSalesOrderSubscriptionProductType(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String currencyUomId = (String) context.get("defaultOrganizationPartyCurrencyUomId");
        if(UtilValidate.isEmpty(currencyUomId)){
        	currencyUomId ="INR";
        }
        Timestamp estimatedDeliveryDate = (Timestamp) context.get("estimatedDeliveryDate");
        String shipmentId = (String) context.get("shipmentId");
        List<GenericValue> subscriptionProductsList = UtilGenerics.checkList(context.get("subscriptionProductsList"));
        Map<String, Object> resultMap = FastMap.newInstance();
		//BigDecimal quantity = BigDecimal.ZERO;        
        String productStoreId = (String) (getByprodFactoryStore(delegator)).get("factoryStoreId");
        
        String salesChannel = (String) context.get("salesChannel");       
        if (UtilValidate.isEmpty(salesChannel)) {
        	salesChannel = "BYPROD_SALES_CHANNEL";
        }      

        if (UtilValidate.isEmpty(subscriptionProductsList)) {
            //Debug.logInfo("No subscription to create orders, finished", module);
            return resultMap;
        }   
       
        String productSubscriptionTypeId = subscriptionProductsList.get(0).getString("productSubscriptionTypeId");
        
        String partyId = subscriptionProductsList.get(0).getString("ownerPartyId");
        String facilityId = subscriptionProductsList.get(0).getString("facilityId");
        
       List conditionList = FastList.newInstance();
        
         conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.LIKE, "PM_RC_%"));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		List<GenericValue> partyClassificationList = null;
		try{
			partyClassificationList = delegator.findList("PartyClassification", condition, null, null, null, false);
		}catch(GenericEntityException e){
			Debug.logError("No partyRole found for given partyId:"+ partyId, module);
			return ServiceUtil.returnError("No partyRole found for given partyId");
		}
		
		String productPriceTypeId = null;
		
		if (UtilValidate.isNotEmpty(partyClassificationList)) {
			GenericValue partyClassification = partyClassificationList.get(0);
			productPriceTypeId = (String) partyClassification.get("partyClassificationGroupId");
			//productPriceTypeId = productPriceTypeId+"_PRICE";
		}
		conditionList.clear();
		
        ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale, currencyUomId);
        cart.setOrderType("SALES_ORDER");
        cart.setChannelType(salesChannel);
        cart.setProductStoreId(productStoreId);

        cart.setBillToCustomerPartyId(partyId);
        cart.setPlacingCustomerPartyId(partyId);
        cart.setShipToCustomerPartyId(partyId);
        cart.setEndUserCustomerPartyId(partyId);
        cart.setFacilityId(facilityId);
        cart.setEstimatedDeliveryDate(estimatedDeliveryDate);
        cart.setProductSubscriptionTypeId(productSubscriptionTypeId);
        cart.setShipmentId(shipmentId);
        try {
            cart.setUserLogin(userLogin, dispatcher);
        } catch (Exception exc) {
            Debug.logError("Error setting userLogin in the cart: " + exc.getMessage(), module);
    		return ServiceUtil.returnError("Error setting userLogin in the cart: " + exc.getMessage());          	            
        }      
        Iterator<GenericValue> i = subscriptionProductsList.iterator();
        while (i.hasNext()) {
            GenericValue subscriptionProduct = i.next();
            if (subscriptionProduct != null) {
            	try { 
            		Map<String, Object> priceResult;
                    Map<String, Object> priceContext = FastMap.newInstance();
                    priceContext.put("userLogin", userLogin);   
                    priceContext.put("productStoreId", productStoreId);                    
                    priceContext.put("productId", subscriptionProduct.getString("productId"));
                    priceContext.put("partyId", partyId);
                    priceContext.put("facilityId", subscriptionProduct.getString("facilityId")); 
                    priceContext.put("priceDate", estimatedDeliveryDate);
                    priceContext.put("facilityCategory", subscriptionProduct.getString("categoryTypeEnum"));
                    
            		priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);    
                    if (ServiceUtil.isError(priceResult)) {
                        Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
                		return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
                    }  
                    BigDecimal totalPrice = (BigDecimal)priceResult.get("totalPrice");
                    List taxList = (List)priceResult.get("taxList");
            		List<ShoppingCartItem> tempCartItems =cart.findAllCartItems(subscriptionProduct.getString("productId"));
            		ShoppingCartItem item = null;
            		if(tempCartItems.size() >0){
            			 item = tempCartItems.get(0);
                         item.setQuantity(item.getQuantity().add(new BigDecimal(subscriptionProduct.getString("quantity"))), dispatcher, cart);
                         item.setBasePrice((BigDecimal)priceResult.get("basicPrice"));
                         
                       
                        
            			
            		}else{
            			int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), subscriptionProduct.getString("productId"), null, 
                				new BigDecimal(subscriptionProduct.getString("quantity")), (BigDecimal)priceResult.get("basicPrice"),
                                null, null, null, null, null, null, null,
                                null, null, null, null, null, null, dispatcher,
                                cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE)); 
            			
            			item = cart.findCartItem(itemIndx);
            			
            			
            		}
            		 item.setListPrice(totalPrice);
            		item.setTaxDetails(taxList);
            		// populate tax amount percent here 
            	/*	item.setVatPercent();
                    item.setVatAmount();
                    item.setBedPercent();
                    item.setBedAmount();
                    item.setBedcessPercent();
                    item.setBedcessAmount();
                    item.setBedseccessPercent();
                    item.setBedseccessPercent();*/
					/*GenericValue productDetail = delegator.findOne("Product", UtilMisc.toMap("productId", subscriptionProduct.getString("productId")), false);
					if (productDetail != null) {
						BigDecimal tempQuantity  = subscriptionProduct.getBigDecimal("quantity").multiply(productDetail.getBigDecimal("quantityIncluded"));
						quantity = quantity.add(tempQuantity);
					}*/
            		
                } catch (Exception exc) {
                    Debug.logError("Error adding product with id " + subscriptionProduct.getString("productId") + " to the cart: " + exc.getMessage(), module);
            		return ServiceUtil.returnError("Error adding product with id " + subscriptionProduct.getString("productId") + " to the cart: " + exc.getMessage());          	            
                }
               
            }  
            // for now comment out the  inventory logic
            
            
           /* Map saleMap = FastMap.newInstance();
            saleMap.put("facilityId", "AMBATTURPFTRY");
            saleMap.put("userLogin",userLogin);
            saleMap.put("productId",subscriptionProduct.getString("productId"));
            saleMap.put("quantity", (subscriptionProduct.getBigDecimal("quantity")).negate());
            saleMap.put("effectiveDate", estimatedDeliveryDate);
            saleMap.put("isSale", Boolean.TRUE);
			try {
				Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", saleMap);
				if(ServiceUtil.isError(serviceResult)){
					Debug.logError("Trouble in adjustInventory service", module);
					return ServiceUtil.returnError("Trouble in adjustInventory service");
				}
			} catch (GenericServiceException e) {
				Debug.logError(e, "Trouble calling adjustInventory service", module);
				return ServiceUtil.returnError("Trouble calling adjustInventory service");
			}*/
            
        }      
        cart.setDefaultCheckoutOptions(dispatcher);
        ProductPromoWorker.doPromotions(cart, dispatcher);
        CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
        
        
		List<GenericValue> applicableTaxTypes = null;
		try {
        	applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive InventoryItem ", module);
    		return ServiceUtil.returnError("Failed to retrive InventoryItem " + e);
		}
		List applicableTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);
		
        List<GenericValue> prodPriceType = null;
        
        conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, productPriceTypeId+"%"));
		conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, applicableTaxTypeList));
		EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        
        try {
        	prodPriceType = delegator.findList("ProductPriceAndType", condition1, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive InventoryItem ", module);
    		return ServiceUtil.returnError("Failed to retrive InventoryItem " + e);
		}
       
        try {
			checkout.calcAndAddTax(prodPriceType);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			Debug.logError(e, "Failed to add tax amount ", module);
    		return ServiceUtil.returnError("Failed to add tax amount  " + e);
		}
        Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
        String orderId = (String) orderCreateResult.get("orderId");
        // handle employee subsidies here 
        if(productSubscriptionTypeId.equals("EMP_SUBSIDY")){
        	Map empSubdCtx = UtilMisc.toMap("userLogin",userLogin);	  	
        	empSubdCtx.put("orderId", orderId);
   	  	 	  	 
   	  	 	try{
   	  	 		Map result = dispatcher.runSync("adjustEmployeeSubsidyForOrder",empSubdCtx);  		  		 
   	  	 		if (ServiceUtil.isError(result)) {
   	  	 			String errMsg =  ServiceUtil.getErrorMessage(result);
   	  	 			Debug.logError(errMsg , module);       				
   	  	 			return result;
   	  	 		}
   			 
   	  	 	}catch (Exception e) {
   	  			  Debug.logError(e, "Problem while doing Stock Transfer for Relacement", module);     
   	  			  return resultMap;			  
   	  	 	}
        }
        // approve the order
        if (UtilValidate.isNotEmpty(orderId)) {
            boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);       
            
            try{            	
        		resultMap = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", orderId,"userLogin", userLogin));
        		if (ServiceUtil.isError(resultMap)) {
                    Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(resultMap), module);
            		return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(resultMap));          	            
                } 
	        	Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", resultMap.get("invoiceId"));
	             invoiceCtx.put("userLogin", userLogin);
	             invoiceCtx.put("statusId","INVOICE_READY");
	             /*try{
	             	Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
	             	if (ServiceUtil.isError(invoiceResult)) {
	             		Debug.logError(invoiceResult.toString(), module);
	                     return ServiceUtil.returnError(null, null, null, invoiceResult);
	                 }	             	
	             }catch(GenericServiceException e){
	             	 Debug.logError(e, e.toString(), module);
	                 return ServiceUtil.returnError(e.toString());
	             } */       		
        		// apply invoice if any adavance payments from this  party
				  			            
				/*Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", (String)resultMap.get("invoiceId"),"userLogin", userLogin));
				if (ServiceUtil.isError(resultPaymentApp)) {						  
	        	   Debug.logError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp), module);			             
	               return ServiceUtil.returnError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp));  
		        }*/				           
		          
            }catch (Exception e) {
                Debug.logError(e, module);
            } 
            // handle Replacement here 
          /*  if(productSubscriptionTypeId.equals("REPLACEMENT_BYPROD")){
            	List<GenericValue> transProductList = FastList.newInstance();
            	
            	for(GenericValue SubscriptionProduct : subscriptionProductsList){
            		GenericValue tempSubscriptionProduct = delegator.makeValue("SubscriptionFacilityAndSubscriptionProduct");
            		tempSubscriptionProduct.putAll(SubscriptionProduct);
            		tempSubscriptionProduct.put("facilityId", SubscriptionProduct.getString("destinationFacilityId"));
            		transProductList.add(tempSubscriptionProduct);
            	}
            	
            	Map transferCtx = UtilMisc.toMap("userLogin",userLogin);	  	
       	  	 	transferCtx.put("shipmentId", shipmentId);
       	  	 	transferCtx.put("estimatedDeliveryDate", estimatedDeliveryDate);
       	  	 	transferCtx.put("subscriptionProductsList", transProductList);	  	 
       	  	 	try{
       	  	 		Map result = dispatcher.runSync("receiveParlorInventory",transferCtx);  		  		 
       	  	 		if (ServiceUtil.isError(result)) {
       	  	 			String errMsg =  ServiceUtil.getErrorMessage(result);
       	  	 			Debug.logError(errMsg , module);       				
       	  	 			return result;
       	  	 		}
       			 
       	  	 	}catch (Exception e) {
       	  			  Debug.logError(e, "Problem while doing Stock Transfer for Relacement", module);     
       	  			  return resultMap;			  
       	  	 	}
            }*/
            
           
            resultMap.put("orderId", orderId);
            //resultMap.put("quantity", quantity);  
            //Debug.logInfo("quantity=" + quantity, module);                    
            
        }        
        return resultMap;   
    }
	
	public static Map<String, Object> receiveParlorInventory(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp estimatedDeliveryDate = (Timestamp) context.get("estimatedDeliveryDate");
        String shipmentId = (String) context.get("shipmentId");
        Map<String, Object> resultMap = FastMap.newInstance();
        List<GenericValue> subscriptionProductsList = UtilGenerics.checkList(context.get("subscriptionProductsList"));
        
        String facilityId = subscriptionProductsList.get(0).getString("facilityId");
        String productSubscriptionTypeId = subscriptionProductsList.get(0).getString("productSubscriptionTypeId");
        String ownerPartyId = "Company";
        String inventoryItemTypeId = "NON_SERIAL_INV_ITEM";
        BigDecimal quantityRejected = BigDecimal.ZERO;
        BigDecimal requestedQuantity = BigDecimal.ZERO;  
        
        List<GenericValue> invoiceItemTypeList = null;
        
        List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, "TAX10"));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.LIKE, "%_SALE"));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        
        try {
        	invoiceItemTypeList = delegator.findList("InvoiceItemType", condition, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive InventoryItem ", module);
    		return ServiceUtil.returnError("Failed to retrive InventoryItem " + e);
		}
        
		List invItemTypeList = EntityUtil.getFieldListFromEntityList(invoiceItemTypeList, "invoiceItemTypeId", false);
		
        Map<String, Object> createInvoiceContext = FastMap.newInstance();
        createInvoiceContext.put("partyId", ownerPartyId);
        createInvoiceContext.put("partyIdFrom", "TAX10");
        createInvoiceContext.put("invoiceDate", estimatedDeliveryDate);
        createInvoiceContext.put("dueDate", estimatedDeliveryDate);
        createInvoiceContext.put("facilityId", facilityId);
        createInvoiceContext.put("shipmentId", shipmentId);
        createInvoiceContext.put("invoiceTypeId", "STATUTORY_OUT");
        createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
        createInvoiceContext.put("userLogin", userLogin);

        String productStoreId = (String) (getByprodFactoryStore(delegator)).get("factoryStoreId");
        String productId = null;  
        String inventoryItemId = null;
        String inventoryTransferId = null;
		List inventoryTransferIdList = FastList.newInstance();
		
		List<List<GenericValue>> itemAdjustments = FastList.newInstance();
		
        Iterator<GenericValue> i = subscriptionProductsList.iterator();
        while (i.hasNext()) {
            GenericValue subscriptionProduct = i.next();
            if (subscriptionProduct != null) {
            	
            	Map inventoryItemMap = FastMap.newInstance();
	        	BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
	        	requestedQuantity = BigDecimal.ZERO;
	        	inventoryItemId = null;
	        	
            	productId = subscriptionProduct.getString("productId");
            	requestedQuantity = subscriptionProduct.getBigDecimal("quantity");
            	
            	//::TODO:: TEMP need to fix (Unit Price)
            	
            	Map<String, Object> priceResult;
                Map<String, Object> priceContext = FastMap.newInstance();
                priceContext.put("userLogin", userLogin);   
                priceContext.put("productStoreId", productStoreId);                    
                priceContext.put("productId", productId);
                priceContext.put("priceDate", estimatedDeliveryDate);
                if(UtilValidate.isNotEmpty(productSubscriptionTypeId) && productSubscriptionTypeId.equalsIgnoreCase("REPLACEMENT_BYPROD")){
                	Map categoryProductsMap = (Map) ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct");
                	List unionProductList = (List) categoryProductsMap.get("UNION_PRODUCTS");
                	if(unionProductList.contains(productId)){
                		priceContext.put("productPriceTypeId", "PM_RC_U_PRICE");
    				}
                	else{
                		priceContext.put("productPriceTypeId", "PM_RC_W_PRICE");
                	}
                }
                else{
                	priceContext.put("productPriceTypeId", "PM_RC_P_PRICE");
                }
                priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);    
                if (ServiceUtil.isError(priceResult)) {
              	  	Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
              	  	return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));
                } 
                BigDecimal unitCost = (BigDecimal)priceResult.get("price");
                
                Map stockXferMap = FastMap.newInstance();
                stockXferMap.put("facilityId", "AMBATTURPFTRY");
                stockXferMap.put("userLogin",userLogin);
                stockXferMap.put("productId",productId);
                stockXferMap.put("quantity", requestedQuantity.negate());
                stockXferMap.put("effectiveDate", estimatedDeliveryDate);
                stockXferMap.put("isXferOut", Boolean.TRUE);
    			try {
    				Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", stockXferMap);
    				if(ServiceUtil.isError(serviceResult)){
    					Debug.logError("Trouble in adjustInventory service", module);
    					return ServiceUtil.returnError("Trouble in adjustInventory service");
    				}
    			} catch (GenericServiceException e) {
    				Debug.logError(e, "Trouble calling adjustInventory service", module);
    				return ServiceUtil.returnError("Trouble calling adjustInventory service");
    			}
                
                
                Map inputMap = FastMap.newInstance();
    			inputMap.put("facilityId", facilityId);
    			inputMap.put("userLogin",userLogin);
    			inputMap.put("productId",productId);
    			inputMap.put("quantity", requestedQuantity);
    			inputMap.put("unitCost", unitCost);
    			inputMap.put("shipmentId", shipmentId);
    			inputMap.put("effectiveDate", estimatedDeliveryDate);
    			inputMap.put("isReceipt", Boolean.TRUE);
    			inputMap.put("productSubscriptionTypeId", productSubscriptionTypeId);
        		
    			try {
    				Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
    				if(ServiceUtil.isError(serviceResult)){
    					Debug.logError("Trouble in adjustInventory service", module);
    					return ServiceUtil.returnError("Trouble in adjustInventory service");
    				}
    			} catch (GenericServiceException e) {
    				Debug.logError(e, "Trouble calling adjustInventory service", module);
    				return ServiceUtil.returnError("Trouble calling adjustInventory service");
    			}
                
				GenericValue product;
				try {
					product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
				} catch (GenericEntityException e) {
					Debug.logError(e, e.toString(), module);
	                return ServiceUtil.returnError(e.toString());
				}
		         GenericValue productStore;
				try {
					productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
				} catch (GenericEntityException e) {
					Debug.logError(e, e.toString(), module);
	                return ServiceUtil.returnError(e.toString());
				}
				List<GenericValue> taxList = null;
				
					List<GenericValue> prodPriceType = null;
			        
			        conditionList.clear();
			        conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "PM_RC_P"+"%"));
					conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, invItemTypeList));
					EntityCondition priceCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			        
			        try {
			        	prodPriceType = delegator.findList("ProductPriceAndType", priceCondition, null, null, null, false);
					} catch (GenericEntityException e) {
						Debug.logError(e, "Failed to retrive InventoryItem ", module);
			    		return ServiceUtil.returnError("Failed to retrive InventoryItem " + e);
					}
					String taxTypeId = null;
					
					if(UtilValidate.isEmpty(prodPriceType)){
						continue;
					}
					taxList = TaxAuthorityServices.getTaxAdjustmentByType(delegator, product, productStore, null, requestedQuantity, BigDecimal.ZERO, BigDecimal.ZERO, null, prodPriceType);
					itemAdjustments.add(taxList);
					
            }  
        }
        List<List<GenericValue>> consolidatedTaxList = null;
        List<List<GenericValue>> itemAdj = null;
        
        if(UtilValidate.isNotEmpty(itemAdjustments)){
        	 consolidatedTaxList = TaxAuthorityServices.consolidateItemAdjustments(itemAdjustments);
        	 itemAdj = UtilGenerics.checkList(consolidatedTaxList.get(itemAdjustments.size() - 1));
        }
       
        if (itemAdj != null) {
            for (int x = 0; x < itemAdj.size(); x++) {
            	
                GenericValue adjs = (GenericValue) itemAdj.get(x);
                
                String invoiceId = null;
                try {
                	Map<String, Object> createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
                    invoiceId = (String) createInvoiceResult.get("invoiceId");
                    if (ServiceUtil.isError(createInvoiceResult)) {
                        return ServiceUtil.returnError("There was an error while creating Invoice" + ServiceUtil.getErrorMessage(createInvoiceResult));
                    }
                } catch (GenericServiceException e) {
        			Debug.logError(e, e.toString(), module);
                     return ServiceUtil.returnError(e.toString());
        		}
                
                Map input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);
                input.put("invoiceItemTypeId", adjs.get("orderAdjustmentTypeId")); 
                input.put("quantity", BigDecimal.ONE);
                input.put("amount", adjs.get("amount"));
                
                Map<String, Object> serviceResults;
    			try {
    				serviceResults = dispatcher.runSync("createInvoiceItem", input);
    			} catch (GenericServiceException e) {
    				Debug.logError(e, e.toString(), module);
                    return ServiceUtil.returnError(e.toString());
    			}
                if (ServiceUtil.isError(serviceResults)) {
                	return ServiceUtil.returnError("Unable to create Invoice Item", null, null, serviceResults);
                }
                
                Map<String, Object> setInvoiceStatusResult;
        		try {
        			setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", "INVOICE_READY", "userLogin", userLogin));
        			 if (ServiceUtil.isError(setInvoiceStatusResult)) {
        		          return ServiceUtil.returnError("Unable to set Invoice Status", null, null, setInvoiceStatusResult);
        		     }
        		} catch (GenericServiceException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            }
        }
       
		return resultMap;   
	}
	
/*public static Map<String, Object> stockTransfer(DispatchContext dctx, Map<String, ? extends Object> context,List<GenericValue> subscriptionProductsList) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp estimatedDeliveryDate = (Timestamp) context.get("estimatedDeliveryDate");
        String shipmentId = (String) context.get("shipmentId");
        Map<String, Object> resultMap = FastMap.newInstance();
		      
        String fromFacilityId="AMBATTURPFTRY";
        String facilityId = subscriptionProductsList.get(0).getString("facilityId");
        
        BigDecimal requestedQuantity = BigDecimal.ZERO;  
        String productId = null;  
        String inventoryItemId = null;
        String inventoryTransferId = null;
		List inventoryTransferIdList = FastList.newInstance();
		
        Iterator<GenericValue> i = subscriptionProductsList.iterator();
        while (i.hasNext()) {
            GenericValue subscriptionProduct = i.next();
            if (subscriptionProduct != null) {
            	
            	Map inventoryItemMap = FastMap.newInstance();
	        	BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
	        	requestedQuantity = BigDecimal.ZERO;
	        	inventoryItemId = null;
	        	
            	productId = subscriptionProduct.getString("productId");
            	requestedQuantity = subscriptionProduct.getBigDecimal("quantity");
            	
            	List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, fromFacilityId));
				EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				
				List<GenericValue> inventoryItems;
				try {
					inventoryItems = delegator.findList("InventoryItem", condition1 , null, null, null, false );
				} catch (GenericEntityException e) {
					Debug.logError(e, "Failed to retrive InventoryItem " + productId, module);
		    		return ServiceUtil.returnError("Failed to retrive InventoryItem " + productId + ": " + e);
				}
				
				for (GenericValue inventoryItem : inventoryItems) {
					
					if(UtilValidate.isNotEmpty(inventoryItem.get("availableToPromiseTotal"))){
						if((((BigDecimal) inventoryItem.get("availableToPromiseTotal")).compareTo(requestedQuantity)>= 0) && UtilValidate.isNotEmpty(inventoryItem.get("availableToPromiseTotal"))){
							inventoryItemId = (String) inventoryItem.get("inventoryItemId");
						}
						if( ((BigDecimal)(inventoryItem.get("availableToPromiseTotal")) ).compareTo(BigDecimal.ZERO) >= 0 ){
							
							String tempInventoryItemId = null;
							BigDecimal tempAvailableToPromiseTotal = BigDecimal.ZERO;
							tempInventoryItemId = (String) inventoryItem.get("inventoryItemId");
							tempAvailableToPromiseTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");
							
							inventoryItemMap.put(tempInventoryItemId, tempAvailableToPromiseTotal);
							availableToPromiseTotal = availableToPromiseTotal.add(tempAvailableToPromiseTotal);
						}
					}
				}	
				
				if(UtilValidate.isNotEmpty(inventoryItemId)){
					Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", inventoryItemId,"shipmentId", shipmentId, "statusId","IXF_COMPLETE", "facilityId",fromFacilityId, "facilityIdTo", facilityId, "xferQty", requestedQuantity.toString());
					Map<String, Object> inventoryXferResult;
					try {
						inventoryXferResult = dispatcher.runSync("createInventoryTransfer", input);
						if (ServiceUtil.isError(inventoryXferResult)) {
							Debug.logError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(inventoryXferResult), module);			             
				            return ServiceUtil.returnError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(inventoryXferResult));  
			            }
						inventoryTransferId = (String) inventoryXferResult.get("inventoryTransferId");
						inventoryTransferIdList.add(inventoryTransferId);
						
					} catch (GenericServiceException e) {
						Debug.logError(e, e.toString(), module);
		                 return ServiceUtil.returnError(e.toString());
					}
				}
				else if(UtilValidate.isEmpty(inventoryItemId) && ((availableToPromiseTotal).compareTo(requestedQuantity) >= 0)){
					
					Iterator invItemIter = inventoryItemMap.entrySet().iterator();
					while (invItemIter.hasNext()) {
						BigDecimal tempXferQty = BigDecimal.ZERO;
						Map.Entry invItemEntry = (Entry) invItemIter.next();
						
						BigDecimal tempATP = (BigDecimal) invItemEntry.getValue();
						if((requestedQuantity).compareTo(tempATP) >= 0){
							tempXferQty = tempATP;
							requestedQuantity = requestedQuantity.subtract(tempATP);
						}else{
							tempXferQty = requestedQuantity;
						}
						if(tempXferQty.equals(BigDecimal.ZERO)){
							continue;
						}
						Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", invItemEntry.getKey(), "shipmentId", shipmentId, "statusId","IXF_COMPLETE", "facilityId",fromFacilityId, "facilityIdTo", facilityId, "xferQty", tempXferQty);
						Map<String, Object> inventoryXferResult;
						try {
							inventoryXferResult = dispatcher.runSync("createInventoryTransfer", input);
							if (ServiceUtil.isError(inventoryXferResult)) {
								Debug.logError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(inventoryXferResult), module);			             
					            return ServiceUtil.returnError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(inventoryXferResult));  
				            
				            }
							inventoryTransferId = (String) inventoryXferResult.get("inventoryTransferId");
							inventoryTransferIdList.add(inventoryTransferId);
						} catch (GenericServiceException e) {
							Debug.logError(e, e.toString(), module);
			                 return ServiceUtil.returnError(e.toString());
						}
					}
				}
				else{
					Debug.logError("There was an error while  adjusting advance payment" , module);			             
		            return ServiceUtil.returnError("There was an error while  adjusting advance payment");  
				}
               
            }  
        }
		return resultMap;   
	}*/
	
	public static Map<String, Object> cancelByProdShipment(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        Date nowDate=UtilDateTime.nowDate(); 
        String shipmentId = (String) context.get("shipmentId");
        String shipmentTypeId = (String) context.get("shipmentTypeId");
        List conditionList= FastList.newInstance(); 
        List boothOrdersList = FastList.newInstance();
        List boothOrderIdsList = FastList.newInstance();
        List boothInvoiceIdsList = FastList.newInstance();
        GenericValue shipment = null;
       // Date shipDate = nowDate;
        String routeId = (String)context.get("routeId");
        String tripId = (String)context.get("tripId");
        
        List routesList = FastList.newInstance();
        if((UtilValidate.isNotEmpty(routeId)) && (routeId.equalsIgnoreCase("AllRoutes")) ){
        	routesList = (List) getByproductRoutes(delegator).get("routeIdsList");
        }else if(UtilValidate.isNotEmpty(routeId)){
        	routesList.add(routeId);
        }
        
        Timestamp estimatedShipDate = (Timestamp)context.get("estimatedDeliveryDate");
        Timestamp dayBegin = UtilDateTime.getDayStart(estimatedShipDate);
        Timestamp dayEnd = UtilDateTime.getDayEnd(estimatedShipDate);
        List shipmentList = FastList.newInstance();
        if(UtilValidate.isEmpty(shipmentId)  && (UtilValidate.isNotEmpty(routesList))){
        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
        	if(UtilValidate.isNotEmpty(tripId)){
        		conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS , tripId));
        	}
        	conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS , shipmentTypeId));
  	        conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.IN ,routesList));
  	        conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
  	        conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
  	        EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
  	     
  	      
  	        try {
	    	    shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
	        }catch (GenericEntityException e) {
	            Debug.logError("Unable to get Shipment record from DataBase"+e, module);
	    	    return ServiceUtil.returnError("Unable to get Shipment record from DataBase "); 
		    } 
        }else{
        	conditionList.clear();
        	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS , shipmentId));
        	EntityCondition shipCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	try {
        		shipmentList = delegator.findList("Shipment", shipCondition, null,null, null, false);
	        }catch (GenericEntityException e) {
	            Debug.logError("Unable to get Shipment record from DataBase"+e, module);
	    	    return ServiceUtil.returnError("Unable to get Shipment record from DataBase "); 
	         
		    }
        }
        if(UtilValidate.isEmpty(shipmentList)){
        	Debug.logError("ShipmentId or routeId should be given", module);
     		return ServiceUtil.returnError("ShipmentId or routeId should be given"); 
	    }
        
        boolean enableCancelAfterShipDate = Boolean.FALSE;
		try{
			 GenericValue tenantConfigEnableCancelAfterShipDate = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableCancelAfterShipDate"), false);
			 if (UtilValidate.isNotEmpty(tenantConfigEnableCancelAfterShipDate) && (tenantConfigEnableCancelAfterShipDate.getString("propertyValue")).equals("Y")) {
				 enableCancelAfterShipDate = Boolean.TRUE;
			 }
		} catch (GenericEntityException e) {
			 Debug.logError(e, module);             
		}		
		
        if(!enableCancelAfterShipDate && nowDate.after(dayBegin)){        	
        	Debug.logError("Truck sheet cancel not allowed after shipment date", module);
    		return ServiceUtil.returnError("Truck sheet cancel not allowed after Shipment date"); 
        }

        for(int i = 0; i < shipmentList.size(); i++){
        	
        	shipment = (GenericValue) shipmentList.get(i);
        	shipmentId = (String) shipment.get("shipmentId");
        	         
        	shipment.set("statusId", "CANCEL_INPROCESS");
        	try{
        		shipment.store();    		
        	}catch (Exception e) {
    			// TODO: handle exception
        		Debug.logError("Unable to Store Shipment Status"+e, module);
        		return ServiceUtil.returnError("Unable to Store Shipment Status"); 
    		}
        	
        }
        
        try{
    		dispatcher.runAsync("cancelByProdShipmentInternal", UtilMisc.toMap("shipmentIds", EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", true),"userLogin", userLogin));    		
        }catch (GenericServiceException e) {
			// TODO: handle exception
    		Debug.logError("Unable to get records from DataBase"+e, module);
    		return ServiceUtil.returnError(e.getMessage()); 
		}  
    	
        return result;
    }  
    public static Map<String, Object> cancelByProdShipmentInternal(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        Date nowDate=UtilDateTime.nowDate(); 
        List shipmentIds = (List) context.get("shipmentIds");
       
        List boothOrdersList = FastList.newInstance();
        List parlourInvList = FastList.newInstance();
        List boothOrderIdsList = FastList.newInstance();
        List boothInvoiceIdsList = FastList.newInstance();
        List parlourInvoiceIdsList = FastList.newInstance();
        GenericValue shipment = null;
        Date shipDate = nowDate;
        for(int k=0; k<shipmentIds.size(); k++){
        	String shipmentId = (String)shipmentIds.get(k);
        	try{
            	// To change shipment status here we are getting shipment Generic Value
            	shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
            }catch (GenericEntityException e) {
    			// TODO: handle exception
        		Debug.logError("Unable to get Shipment record from DataBase"+e, module);
        		return ServiceUtil.returnError("Unable to get Shipment record from DataBase "); 
    		}   
            List conditionList= FastList.newInstance(); 
        	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
        	
        	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	EntityFindOptions findOptions = new EntityFindOptions();
    		findOptions.setDistinct(true);
    		
    		try{			
        		List boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition,  UtilMisc.toSet("orderId", "productId", "quantity", "estimatedDeliveryDate") , null, null, false);
        		/*for(int i = 0; i < boothOrderItemsList.size(); i++){
        			GenericValue orderItem = (GenericValue) boothOrderItemsList.get(i);
        			String productId = (String) orderItem.get("productId");
        			BigDecimal requestedQuantity = (BigDecimal) orderItem.get("quantity");
            		Timestamp effectiveDate = (Timestamp) orderItem.get("estimatedDeliveryDate");
            		Map inputMap = FastMap.newInstance();
        			
            		inputMap.put("facilityId", "AMBATTURPFTRY");
        			inputMap.put("userLogin",userLogin);
        			inputMap.put("productId",productId);
        			inputMap.put("quantity", requestedQuantity);
        			inputMap.put("effectiveDate", effectiveDate);
        			inputMap.put("isSale", Boolean.TRUE);
        			try {
        				Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
        				if(ServiceUtil.isError(serviceResult)){
        					Debug.logError("Trouble in adjustInventory service", module);
        					return ServiceUtil.returnError("Trouble in adjustInventory service");
        				}
        			} catch (GenericServiceException e) {
        				Debug.logError(e, "Trouble calling adjustInventory service", module);
        				return ServiceUtil.returnError("Trouble calling adjustInventory service");
        			}
        		}
        		*/
        		Set orderIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(boothOrderItemsList, "orderId", true));
        		boothOrderIdsList = new ArrayList(orderIdsSet);
        		//to get all invoices for the shipment 
        		boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", condition, UtilMisc.toSet("orderId","originFacilityId","invoiceId") , UtilMisc.toList("originFacilityId"), findOptions , false);   
        		Set invoiceIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(boothOrdersList, "invoiceId", true));    		
        	    boothInvoiceIdsList = new ArrayList(invoiceIdsSet);

        	    parlourInvList = delegator.findList("Invoice", condition, null , null, null, false);
        	    Set parlourInvSet = new HashSet(EntityUtil.getFieldListFromEntityList(parlourInvList, "invoiceId", true));
        	    parlourInvoiceIdsList = new ArrayList(parlourInvSet);
        	   
        	    boothInvoiceIdsList.addAll(parlourInvoiceIdsList);
        	    
        	}catch (GenericEntityException e) {
    			// TODO: handle exception
        		Debug.logError("Unable to get records from DataBase"+e, module);
        		return ServiceUtil.returnError("Unable to get records from DataBase "); 
    		} 
        	
        	try{
        		result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", boothOrderIdsList,"userLogin", userLogin));
    			if (ServiceUtil.isError(result)) {
    	        	   Debug.logError("There was an error while Cancel  the Orders: " + ServiceUtil.getErrorMessage(result), module);	               
    	               return ServiceUtil.returnError("There was an error while Cancel  the Orders: ");  
    	         } 			
        		 result = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", boothInvoiceIdsList, "statusId","INVOICE_CANCELLED","userLogin", userLogin));
        		 
        		 if (ServiceUtil.isError(result)) {
        			 Debug.logError("There was an error while Cancel  the invoices: " + ServiceUtil.getErrorMessage(result), module);	               
    	             return ServiceUtil.returnError("There was an error while Cancel  the invoices: ");   			 
        		 }	        	  
    	        	    		
        	}catch (GenericServiceException e) {
    			// TODO: handle exception
        		Debug.logError("Unable to Cancel bulk orders"+e, module);
        		return ServiceUtil.returnError("Unable to Cancel bulk order");  
    		} 
        	
        /*	List<GenericValue> shipmentReceipts;
            try {
            	shipmentReceipts = shipment.getRelated("ShipmentReceipt");
            } catch (GenericEntityException e) {
            	Debug.logError("Unable to get ShipmentReceipt"+e, module);
        		return ServiceUtil.returnError("Unable to get ShipmentReceipt");
            }
        	
            for(int i=0; i<shipmentReceipts.size(); i++){
            	
            	GenericValue inventoryItem = null;
         		try {
         			inventoryItem = shipmentReceipts.get(i).getRelatedOne("InventoryItem");
         		} catch (GenericEntityException e) {
         			Debug.logError(e, module); 
         		}
            	
         		Map stockXferMap = FastMap.newInstance();
         		stockXferMap.put("facilityId", "AMBATTURPFTRY");
         		stockXferMap.put("productId",inventoryItem.get("productId"));
         		stockXferMap.put("userLogin", userLogin);
         		stockXferMap.put("effectiveDate", shipment.get("estimatedShipDate"));
         		stockXferMap.put("quantity", ((BigDecimal) shipmentReceipts.get(i).get("quantityAccepted")));
         		stockXferMap.put("isXferOut", Boolean.TRUE);
    			
    			try {
    				Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", stockXferMap);
    				if(ServiceUtil.isError(serviceResult)){
    					Debug.logError("Trouble in adjustInventory service", module);
    					return ServiceUtil.returnError("Trouble in adjustInventory service");
    				}
    			} catch (GenericServiceException e) {
    				Debug.logError(e, "Trouble calling adjustInventory service", module);
    				return ServiceUtil.returnError("Trouble calling adjustInventory service");
    			}
    			
            	Map inputMap = FastMap.newInstance();
            	inputMap.put("facilityId", inventoryItem.get("facilityId"));
    			inputMap.put("productId",inventoryItem.get("productId"));
    			inputMap.put("userLogin", userLogin);
    			inputMap.put("effectiveDate", shipment.get("estimatedShipDate"));
    			inputMap.put("quantity", ((BigDecimal) shipmentReceipts.get(i).get("quantityAccepted")).negate());
    			inputMap.put("isReceipt", Boolean.TRUE);
    			
    			try {
    				Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
    				if(ServiceUtil.isError(serviceResult)){
    					Debug.logError("Trouble in adjustInventory service", module);
    					return ServiceUtil.returnError("Trouble in adjustInventory service");
    				}
    			} catch (GenericServiceException e) {
    				Debug.logError(e, "Trouble calling adjustInventory service", module);
    				return ServiceUtil.returnError("Trouble calling adjustInventory service");
    			}
    			
            }*/
            
            /*try {
    			delegator.removeAll(shipmentReceipts);
    		} catch (GenericEntityException e) {
    			Debug.logError("Unable to remove shipmentReceipts"+e, module);
        		return ServiceUtil.returnError("Unable to remove shipmentReceipts");
    		}*/
            
        	shipment.set("statusId", "SHIPMENT_CANCELLED");
        	try{
        		shipment.store();    		
        	}catch (Exception e) {
    			// TODO: handle exception
        		Debug.logError("Unable to Store Shipment Status"+e, module);
        		return ServiceUtil.returnError("Unable to Store Shipment Status"); 
    		}
        }
        
        return result;
    }
    
    public static String processDeliveryScheduleCorrection(HttpServletRequest request, HttpServletResponse response) {
		  Delegator delegator = (Delegator) request.getAttribute("delegator");
		  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		  Locale locale = UtilHttp.getLocale(request);
		  String facilityId = null;	 
		  String effectiveDateStr = (String) request.getParameter("effectiveDate");
		  String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");;
          String shipmentTypeId = (String) request.getParameter("shipmentTypeId");;
		  String productId = null;
		  String quantityStr = null;
		  Timestamp effectiveDate=null;
		  Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		  BigDecimal quantity = BigDecimal.ZERO;
		  List orderList = FastList.newInstance();	
		  List boothOrderItemsList = FastList.newInstance();	
		  List orderItemListToUpdate = FastList.newInstance();
		  List invoiceItemListToUpdate = FastList.newInstance();
		  List conditionList  = FastList.newInstance();
		  Map<String, Object> result = ServiceUtil.returnSuccess();
		  HttpSession session = request.getSession();
		  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		  GenericValue orderInfo = null;
		  GenericValue facility = null;
		  String routeId = null;
		  String productStoreId="";
		  Boolean newItemFlag = false;
		  boolean beganTransaction = false;
		  boolean isParlour = false;
		  
		  List<GenericValue> orderItemList = FastList.newInstance();
		  String partyId = null;
		  String currencyUomId = "INR";
		  Map<String, Object> resultMap = FastMap.newInstance();
		  
		  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		  if (rowCount < 1) {
			  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_","No rows to process");
			  return "success";
		  }
		  
		  if (paramMap.containsKey("boothId")) {
	  			facilityId = (String) paramMap.get("boothId");
	  	  }	
	  	  else {
	  			request.setAttribute("_ERROR_MESSAGE_", "Missing boothId");
	  			return "error";			  
		  }
		  if (paramMap.containsKey("routeId")) {
	  			routeId = (String) paramMap.get("routeId");
	  	  }	
	  	  else {
	  			request.setAttribute("_ERROR_MESSAGE_", "Missing routeId");
	  			return "error";			  
		  }
		  try {
			  beganTransaction = TransactionUtil.begin();
			  if(UtilValidate.isNotEmpty(effectiveDateStr)){
		  		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
		  		  try {
		  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
		  		  } catch (ParseException e) {
		  			  Debug.logError(e, "Cannot parse date string: " + effectiveDate, module);
		  			 TransactionUtil.rollback();
					 return "error";
		  		  } catch (NullPointerException e) {
		  			  Debug.logError(e, "Cannot parse date string: " + effectiveDate, module);
		  			 TransactionUtil.rollback();
					 return "error";
		  		  }
			  }
		  	  else{
		  		  effectiveDate = UtilDateTime.nowTimestamp();
		  	  }
			  /*effectiveDate = UtilDateTime.nowTimestamp();
			  effectiveDate = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);*/
			  if (facilityId == "") {
				  request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
				  TransactionUtil.rollback();
				  return "error";
			  }
			  else{
				  try {
					  facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), false);
					 
	     			  if(UtilValidate.isEmpty(facility)){
     					  request.setAttribute("_ERROR_MESSAGE_", "Booth"+"'"+facilityId+"'"+" does not exist");
     					  TransactionUtil.rollback();
     					  return "error";
	     			  }	
		     			
		     		  if(UtilValidate.isNotEmpty(facility.getString("categoryTypeEnum")) && (facility.getString("categoryTypeEnum").equals("PARLOUR"))){
		     			  isParlour = true;
		     	      }
		     			
		     		  partyId = facility.getString("ownerPartyId");
		     		  productStoreId = (String) (getByprodFactoryStore(delegator)).get("factoryStoreId");

		     		  if(UtilValidate.isEmpty(productStoreId)){
				          request.setAttribute("_ERROR_MESSAGE_", "No product store ");
				          TransactionUtil.rollback();
						  return "error";		        	  
				      }
		     			
				  } catch (Exception e) {
					  Debug.logError(e, "Cannot find facility or Store Id: " + e.getMessage(), module);
					  TransactionUtil.rollback();
					  return "error";
				  }
			  }
		    
			  String shipmentId = "";
				 
			  Timestamp dayBegin = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
			  Timestamp dayEnd = UtilDateTime.getDayEnd(effectiveDate, TimeZone.getDefault(), locale);
		      List shipmentList = FastList.newInstance();
		        
		      conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
		      conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS ,shipmentTypeId));
		      conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS ,routeId));
		      conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
		      conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
		      EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		      try {
		    	  shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
		      }catch (GenericEntityException e) {
		          Debug.logError(e, module);
		          request.setAttribute("_ERROR_MESSAGE_", "un able to get shipment id.");
		          TransactionUtil.rollback();
		          return "error";
			  } 
		      if(UtilValidate.isEmpty(shipmentList)){		
		    	  request.setAttribute("_ERROR_MESSAGE_", "no shipment done for the specified day.");
		    	  TransactionUtil.rollback();
		          return "error";
		      }
		      shipmentId = EntityUtil.getFirst(shipmentList).getString("shipmentId");
		      ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale, currencyUomId);
			      
		      String orderId = "";
			  if(!isParlour){
		    	  conditionList.clear();
				  conditionList = UtilMisc.toList(
						  EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
				  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				  conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
				  conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
				  /*conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.NOT_EQUAL, routeId));*/
				  EntityCondition ohCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);	
				  orderList=delegator.findList("OrderHeaderFacAndItemBillingInv", ohCondition, null, UtilMisc.toList("-orderId"), null, false);
				  boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", ohCondition, null, UtilMisc.toList("-orderId"), null, false);
				  /*for(int i = 0; i < boothOrderItemsList.size(); i++){
		    			GenericValue orderItem = (GenericValue) boothOrderItemsList.get(i);
		    			
		        		Map saleMap = FastMap.newInstance();
		    			
		        		saleMap.put("facilityId", "AMBATTURPFTRY");
		        		saleMap.put("userLogin",userLogin);
		        		saleMap.put("productId",orderItem.get("productId"));
		        		saleMap.put("quantity", orderItem.get("quantity"));
		        		saleMap.put("effectiveDate", orderItem.get("estimatedDeliveryDate"));
		        		saleMap.put("isSale", Boolean.TRUE);
		    			
		    			try {
	        			  	Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", saleMap);
	        				if(ServiceUtil.isError(serviceResult)){
	        					Debug.logError("Trouble in adjustInventory service", module);
	        					request.setAttribute("_ERROR_MESSAGE_",	"Trouble in adjustInventory service " + ServiceUtil.getErrorMessage(serviceResult));
								return "error";
	        				}
		        		} catch (GenericServiceException e) {
	        				Debug.logError(e, "Trouble calling adjustInventory service", module);
	        				request.setAttribute("_ERROR_MESSAGE_",	"Trouble calling adjustInventory service ");
							return "error";
		        		}
		    	  }
				  */
				  if(UtilValidate.isNotEmpty(orderList)){
					  orderInfo = EntityUtil.getFirst(orderList);
					  orderId = (String)orderInfo.getString("orderId");
					  try{
				    		result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", UtilMisc.toList(orderId),"userLogin", userLogin));
							if (ServiceUtil.isError(result)) {
								request.setAttribute("_ERROR_MESSAGE_",	"unable to correct Delivery Schedule" + orderId);
								TransactionUtil.rollback();
					            return "error";  
					         } 			
				    		 result = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", UtilMisc.toList((String)orderInfo.getString("invoiceId")), "statusId","INVOICE_CANCELLED","userLogin", userLogin));
				    		 
				    		 if (ServiceUtil.isError(result)) {
				    			 request.setAttribute("_ERROR_MESSAGE_",	"unable to correct Delivery Schedule" + orderId);
				    			 TransactionUtil.rollback();
				                 return "error";    			 
				    		 }	        	  
				      }catch (GenericServiceException e) {
				    	  Debug.logError(e, e.toString(), module);
			              request.setAttribute("_ERROR_MESSAGE_",	"unable to correct Delivery Schedule" + e.toString() + " due to ");
			              TransactionUtil.rollback();
			              return "error";  
					  } 
				  }
		        cart.setOrderType("SALES_ORDER");
		        cart.setChannelType("BYPROD_SALES_CHANNEL");
		        cart.setProductStoreId(productStoreId);

		        cart.setBillToCustomerPartyId(partyId);
		        cart.setPlacingCustomerPartyId(partyId);
		        cart.setShipToCustomerPartyId(partyId);
		        cart.setEndUserCustomerPartyId(partyId);
		        cart.setFacilityId(facilityId);
		        cart.setEstimatedDeliveryDate(effectiveDate);
		        cart.setProductSubscriptionTypeId(productSubscriptionTypeId);
		        cart.setShipmentId(shipmentId);
		        try {
		            cart.setUserLogin(userLogin, dispatcher);
		        } catch (Exception exc) {
		            Debug.logError("Error setting userLogin in the cart: " + exc.getMessage(), module);
		            request.setAttribute("_ERROR_MESSAGE_", "Error setting userLogin in the cart: " + exc.getMessage());
		            TransactionUtil.rollback();
		            return "error";
		        }
		      }
			  boolean skipOrder = false; 
			  for (int i = 0; i < rowCount; i++) {
				  
				  List<GenericValue> invoiceItemList = FastList.newInstance();
				  GenericValue invoiceItem = null;
				  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				  if (paramMap.containsKey("productId" + thisSuffix)) {
					  productId = (String) paramMap.remove("productId" + thisSuffix);
				  }
				  if (paramMap.containsKey("quantity" + thisSuffix)) {
					  quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
				  }
				  if ((quantityStr == null) || (quantityStr.equals(""))) {
					  continue;
				  }
				  try {
					  quantity = new BigDecimal(quantityStr);
				  } catch (Exception e) {
					  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
					  quantity = BigDecimal.ZERO;
				  }
				  if(quantity.compareTo(BigDecimal.ZERO)< 1 && !isParlour){
					  continue;
				  }
				  skipOrder = true;
				  Map<String, Object> priceResult;
                  Map<String, Object> priceContext = FastMap.newInstance();
                  priceContext.put("userLogin", userLogin);   
                  priceContext.put("productStoreId", productStoreId);                    
                  priceContext.put("productId", productId);
                  priceContext.put("facilityId", facilityId);
                  priceContext.put("priceDate", dayBegin);
                  if(isParlour){
                	  priceContext.put("productPriceTypeId", "PM_RC_P_PRICE");
                  }else{
                	  priceContext.put("partyId", partyId);
                  }
          		  priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);    
                  if (ServiceUtil.isError(priceResult)) {
                	  Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
	    				request.setAttribute("_ERROR_MESSAGE_",	"There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));
	    				TransactionUtil.rollback();
	    				return "error";           	            
                  } 
                 /* BigDecimal unitCost = (BigDecimal)priceResult.get("price");*/
				  
				  if(isParlour){
					  
					  conditionList.clear();
					  conditionList = UtilMisc.toList( EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
					  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
					  conditionList.add(EntityCondition.makeCondition("isCancelled",  EntityOperator.EQUALS, null));
					  
					  EntityCondition shipRecCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);	
					  List<GenericValue> shipReceiptItems = delegator.findList("ShipmentReceiptAndItem", shipRecCond, null, null, null, false);
					  
					  if(UtilValidate.isEmpty(shipReceiptItems)){
						  
						  if((quantity.compareTo(BigDecimal.ZERO))>0){
							  Map stockXferMap = FastMap.newInstance();
							  stockXferMap.put("facilityId", "AMBATTURPFTRY");
							  stockXferMap.put("userLogin",userLogin);
							  stockXferMap.put("productId",productId);
							  stockXferMap.put("quantity", quantity.negate());
							  stockXferMap.put("effectiveDate", effectiveDate);
							  stockXferMap.put("isXferOut", Boolean.TRUE);
			            		
			        		  try {
			        			  Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", stockXferMap);
			        				if(ServiceUtil.isError(serviceResult)){
			        					Debug.logError("Trouble in adjustInventory service", module);
			        					request.setAttribute("_ERROR_MESSAGE_",	"Trouble in adjustInventory service " + ServiceUtil.getErrorMessage(serviceResult));
										return "error";
			        		  }
			        		  } catch (GenericServiceException e) {
			        				Debug.logError(e, "Trouble calling adjustInventory service", module);
			        				request.setAttribute("_ERROR_MESSAGE_",	"Trouble calling adjustInventory service ");
									return "error";
			        		  }
				              
							  Map inputMap = FastMap.newInstance();
			        			inputMap.put("facilityId", facilityId);
			        			inputMap.put("userLogin",userLogin);
			        			inputMap.put("productId",productId);
			        			inputMap.put("quantity", quantity);
			        			inputMap.put("shipmentId", shipmentId);
			        			inputMap.put("effectiveDate", effectiveDate);
			        			inputMap.put("isReceipt", Boolean.TRUE);
			            		
			        			try {
			        				Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
			        				if(ServiceUtil.isError(serviceResult)){
			        					Debug.logError("Trouble in adjustInventory service", module);
			        					request.setAttribute("_ERROR_MESSAGE_",	"Trouble in adjustInventory service " + ServiceUtil.getErrorMessage(serviceResult));
			        					 TransactionUtil.rollback();
			        					return "error";
			        				}
			        			} catch (GenericServiceException e) {
			        				Debug.logError(e, "Trouble calling adjustInventory service", module);
			        				request.setAttribute("_ERROR_MESSAGE_",	"Trouble calling adjustInventory service ");
			        				 TransactionUtil.rollback();
									return "error";
			        			}
							  BigDecimal quantityRejected = BigDecimal.ZERO;
						  }
					  }
					  else{
						  GenericValue shipRecItem = shipReceiptItems.get(0);
						  BigDecimal existingQty = (BigDecimal) shipRecItem.get("quantityAccepted");
						  BigDecimal diffQty = quantity.subtract(existingQty);
						  
						  String receiptId = shipRecItem.getString("receiptId");
						  GenericValue shipReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
						
						  shipReceipt.put("quantityAccepted", quantity);
						  shipReceipt.store();
						  
						  Map stockXferMap = FastMap.newInstance();
						  stockXferMap.put("facilityId", "AMBATTURPFTRY");
						  stockXferMap.put("userLogin",userLogin);
						  stockXferMap.put("productId",productId);
						  stockXferMap.put("quantity", diffQty.negate());
						  stockXferMap.put("effectiveDate", effectiveDate);
						  stockXferMap.put("isXferOut", Boolean.TRUE);
		            		
		        		  try {
		        			  Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", stockXferMap);
		        				if(ServiceUtil.isError(serviceResult)){
		        					Debug.logError("Trouble in adjustInventory service", module);
		        					request.setAttribute("_ERROR_MESSAGE_",	"Trouble in adjustInventory service " + ServiceUtil.getErrorMessage(serviceResult));
		        					 TransactionUtil.rollback();
		        					return "error";
		        		  }
		        		  } catch (GenericServiceException e) {
		        				Debug.logError(e, "Trouble calling adjustInventory service", module);
		        				request.setAttribute("_ERROR_MESSAGE_",	"Trouble calling adjustInventory service ");
		        				 TransactionUtil.rollback();
								return "error";
		        		  }
						  
				          Map inputMap = FastMap.newInstance();
						  inputMap.put("facilityId", facilityId);
						  inputMap.put("productId",productId);
						  inputMap.put("userLogin", userLogin);
						  inputMap.put("quantity", diffQty);
						  inputMap.put("isReceipt", Boolean.TRUE);
						  inputMap.put("effectiveDate", effectiveDate);
							
						  try {
		        			  Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
		        			  if(ServiceUtil.isError(serviceResult)){
		        				  Debug.logError("Trouble in adjustInventory service", module);
		        				  request.setAttribute("_ERROR_MESSAGE_",	"Trouble in adjustInventory service " + ServiceUtil.getErrorMessage(serviceResult));
		        				  TransactionUtil.rollback();
		        				  return "error";
		        			  }
		        		  } catch (GenericServiceException e) {
		        			  Debug.logError(e, "Trouble calling adjustInventory service", module);
		        			  request.setAttribute("_ERROR_MESSAGE_",	"Trouble calling adjustInventory service ");
		        			  TransactionUtil.rollback();
							  return "error";
		        		  }
						 
		        		  if((diffQty.add(existingQty)).compareTo(BigDecimal.ZERO) == 0){
		        			  List exprList = FastList.newInstance();
		        			  exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		        			  exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		        			  exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		        			  EntityCondition exprCond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
							  List<GenericValue> shipementReceiptsAndItem = delegator.findList("ShipmentReceiptAndItem", exprCond, null, null, null, false);
							  if(UtilValidate.isNotEmpty(shipementReceiptsAndItem)){
								  GenericValue shipReceipts = EntityUtil.getFirst(shipementReceiptsAndItem) ;
								  String rcptId = shipReceipts.getString("receiptId");
								  GenericValue shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", rcptId), false);
								  if(UtilValidate.isNotEmpty(shipmentReceipt)){
									  shipmentReceipt.set("isCancelled", "Y");
									  delegator.store(shipmentReceipt);
								  }
							  }
						  }
		        		  
					  }
				  }
				  else{
					  try{
						  /*Map saleMap = FastMap.newInstance();
						  saleMap.put("facilityId", "AMBATTURPFTRY");
						  saleMap.put("productId",productId);
						  saleMap.put("userLogin", userLogin);
						  saleMap.put("quantity", quantity.negate());
						  saleMap.put("isSale", Boolean.TRUE);
						  saleMap.put("effectiveDate", effectiveDate);
							
						  try {
		        			  Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", saleMap);
		        			  if(ServiceUtil.isError(serviceResult)){
		        				  Debug.logError("Trouble in adjustInventory service", module);
		        				  request.setAttribute("_ERROR_MESSAGE_",	"Trouble in adjustInventory service " + ServiceUtil.getErrorMessage(serviceResult));
								  return "error";
		        			  }
		        		  } catch (GenericServiceException e) {
		        			  Debug.logError(e, "Trouble calling adjustInventory service", module);
		        			  request.setAttribute("_ERROR_MESSAGE_",	"Trouble calling adjustInventory service ");
							  return "error";
		        		  }*/
		        		  
						  List<ShoppingCartItem> tempCartItems =cart.findAllCartItems(productId);
		            		if(tempCartItems.size() >0){
		            			 ShoppingCartItem item = tempCartItems.get(0);
		                         item.setQuantity(item.getQuantity().add(quantity), dispatcher, cart);
		                         item.setBasePrice((BigDecimal)priceResult.get("basicPrice"));
		            			
		            		}else{
		            			cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null,quantity, (BigDecimal)priceResult.get("basicPrice"),
		                                null, null, null, null, null, null, null,
		                                null, null, null, null, null, null, dispatcher,
		                                cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE)); 
		            		} 
							  
					    }  
					  	catch (Exception exc) {
		                    Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
							request.setAttribute("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), exc);
		                }
				  }
				  
			  }//end row count for loop
			  cart.setDefaultCheckoutOptions(dispatcher);
		        CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
				  
			  if(!isParlour && skipOrder){
			        
			        List condList = FastList.newInstance();
			        
			        condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			        condList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.LIKE, "PM_RC_%"));
					EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
					
					List<GenericValue> partyClassificationList = null;
			        try {
						partyClassificationList = delegator.findList("PartyClassification", cond, null, null, null, false);
					}catch(GenericEntityException e){
						Debug.logError("No partyRole found for given partyId:"+ partyId, module);
						 TransactionUtil.rollback();
						 return "error";
					}
					
					String productPriceTypeId = null;
					
					if (UtilValidate.isNotEmpty(partyClassificationList)) {
						GenericValue partyClassification = partyClassificationList.get(0);
						productPriceTypeId = (String) partyClassification.get("partyClassificationGroupId");
					}
				  
					List<GenericValue> applicableTaxTypes = null;
					try {
			        	applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
					} catch (GenericEntityException e) {
						Debug.logError(e, "Failed to retrive ProductPriceType ", module);
						 TransactionUtil.rollback();
						 return "error";
					}
					List applicableTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);
					
			        List<GenericValue> prodPriceType = null;
			        
			        condList.clear();
			        condList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, productPriceTypeId+"%"));
			        condList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, applicableTaxTypeList));
					EntityCondition condition1 = EntityCondition.makeCondition(condList,EntityOperator.AND);
			        
			        try {
			        	prodPriceType = delegator.findList("ProductPriceAndType", condition1, null, null, null, false);
					} catch (GenericEntityException e) {
						Debug.logError(e, "Failed to retrive ProductPriceAndType ", module);
						request.setAttribute("_ERROR_MESSAGE_",	"Failed to retrive ProductPriceAndType ");
						 TransactionUtil.rollback();
						 return "error";
					}
					
			        try {
			        	if(UtilValidate.isNotEmpty(prodPriceType)){
			        		checkout.calcAndAddTax(prodPriceType);
			        	}else{
			        		checkout.calcAndAddTax();
			        	}
						
					} catch (Exception e1) {
						Debug.logError(e1.toString(), module);
						request.setAttribute("_ERROR_MESSAGE_",	"Error while calculating tax amount for order");
						 TransactionUtil.rollback();
						 return "error";
					}
			        Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
			        String newOrderId = (String) orderCreateResult.get("orderId");
			        // approve the order
			        if (UtilValidate.isNotEmpty(newOrderId)) {
			            boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, newOrderId);       
			            
			            try{            	
			        		resultMap = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", newOrderId,"userLogin", userLogin));
			        		if (ServiceUtil.isError(resultMap)) {
			        			Debug.logError("There was an error while receiving Inventory: " + ServiceUtil.getErrorMessage(resultMap), module);
			    				request.setAttribute("_ERROR_MESSAGE_",	"There was an error while receiving Inventory: " + ServiceUtil.getErrorMessage(resultMap));
			    				return "error";          	            
			                } 
				        	/*Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", resultMap.get("invoiceId"));
				             invoiceCtx.put("userLogin", userLogin);
				             invoiceCtx.put("statusId","INVOICE_READY");
				             try{
				             	Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
				             	if (ServiceUtil.isError(invoiceResult)) {
				             		Debug.logError("There was an error while receiving Inventory: " + ServiceUtil.getErrorMessage(invoiceResult), module);
				    				request.setAttribute("_ERROR_MESSAGE_",	"There was an error while receiving Inventory: " + ServiceUtil.getErrorMessage(invoiceResult));
				    				TransactionUtil.rollback();
				    				return "error";
				                 }	             	
				             }catch(GenericServiceException e){
				            	 Debug.logError(e, module);
				            	 request.setAttribute("_ERROR_MESSAGE_",e.toString());
				    			 TransactionUtil.rollback();
				    			 return "error";
				             }      */  		
			        		// apply invoice if any adavance payments from this  party
							  			            
							Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", (String)resultMap.get("invoiceId"),"userLogin", userLogin));
							if (ServiceUtil.isError(resultPaymentApp)) {						  
								Debug.logError("There was an error while receiving Inventory: " + ServiceUtil.getErrorMessage(resultPaymentApp), module);
			    				request.setAttribute("_ERROR_MESSAGE_",	"There was an error while receiving Inventory: " + ServiceUtil.getErrorMessage(resultPaymentApp));
			    				TransactionUtil.rollback();
			    				return "error"; 
					        }				           
					          
			             //OrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, "ORDER_COMPLETED", "ITEM_APPROVED", "ITEM_COMPLETED", null);           	 
			            }catch (GenericServiceException e) {
			                Debug.logError(e, module);
			                request.setAttribute("_ERROR_MESSAGE_",e.toString());
			    			 TransactionUtil.rollback();
			    			 return "error";
			            } 
			        }        
			  }
		  } catch (GenericEntityException e) {
	            try {
	                // only rollback the transaction if we started one...
	                TransactionUtil.rollback(beganTransaction, "Error saving gate pass", e);
	            } catch (GenericEntityException e2) {
	                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	                request.setAttribute("_ERROR_MESSAGE_",e2.toString());
	    			 return "error";
	            }

	            Debug.logError(e, "An entity engine error occurred while saving gate pass order", module);
	        } finally {
	            // only commit the transaction if we started one... this will throw an exception if it fails
	            try {
	                TransactionUtil.commit(beganTransaction);
	            } catch (GenericEntityException e) {
	                Debug.logError(e, "Could not commit transaction for entity engine error occurred while saving gate pass order", module);
	                request.setAttribute("_ERROR_MESSAGE_",e.toString());
	    			return "error";
	            }
	        }
	       request.setAttribute("supplyDate", effectiveDateStr);
	       request.setAttribute("_EVENT_MESSAGE_", "Successfully made correction for party: "+facilityId);
		  return "success";     
	 }
    
    public static String createSubscriptionIndent(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	  	  
	  	  List custTimePeriodList = FastList.newInstance();  	 
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
	  	  String boothId = null;
	  	  String destinationFacilityId = null;
	  	  String productSubscriptionTypeId = null;
	  	  String routeId = (String) request.getParameter("routeId");
	  	  String productId = null;
	  	  String quantityStr = null;
	  	  BigDecimal quantity = BigDecimal.ZERO;
	  	  GenericValue newValue = null;
	  	  String subscriptionId = null;
	  	  boolean beganTransaction = false;
	  	  try{
	  		  beganTransaction = TransactionUtil.begin();
	  		  if (paramMap.containsKey("boothId")) {
	  			  boothId = (String) paramMap.get("boothId");
			  }	
			  if(UtilValidate.isEmpty(boothId)){
				  request.setAttribute("_ERROR_MESSAGE_", "Missing facilityId");
				  Debug.logError("Missing facilityId", module);
				  TransactionUtil.rollback();
				  return "error";			  
			  }	
	  		  if (paramMap.containsKey("destinationFacilityId")) {
	  			destinationFacilityId = (String) paramMap.get("destinationFacilityId");
			  }	
		  	  Timestamp effectiveDate=null;
		  	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
		  		  try {
		  			  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
		  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
		  		  } catch (ParseException e) {
		  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
		              // effectiveDate = UtilDateTime.nowTimestamp();
		  		  } catch (NullPointerException e) {
		  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
		               //effectiveDate = UtilDateTime.nowTimestamp();
		  		  }
		  	  }	  	  
			  if (paramMap.containsKey("productSubscriptionTypeId")) {
				  productSubscriptionTypeId = (String) paramMap.get("productSubscriptionTypeId");
			  }
			  if(UtilValidate.isEmpty(productSubscriptionTypeId)){
				  Debug.logError("Missing productSubscriptionTypeId", module);
				  request.setAttribute("_ERROR_MESSAGE_", "Missing productSubscriptionTypeId");
				  TransactionUtil.rollback();
				  return "error";			  
			  }
			  if(UtilValidate.isNotEmpty(routeId)){
				  GenericValue rootFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", routeId), false);
				  if(UtilValidate.isEmpty(rootFacility)){
					  request.setAttribute("_ERROR_MESSAGE_", routeId+" doesn't exists");
					  TransactionUtil.rollback();
					  return "error";
				  }
			  }
			  else{
				  request.setAttribute("_ERROR_MESSAGE_", routeId+" doesn't exists");
				  TransactionUtil.rollback();
				  return "error";
			  }
			  List conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
			  conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
			  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
			  List<GenericValue> subscription = null;
			  try {
				  subscription = delegator.findList("Subscription", condition, null, null, null, false);
			  }catch (GenericEntityException e1) {
				  Debug.logError(e1, module);
				  request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
				  TransactionUtil.rollback();
				  return "error";
			  }
			  subscription = EntityUtil.filterByDate(subscription, effectiveDate);
			  if(UtilValidate.isNotEmpty(subscription)){
				  if(subscription.size() == 1){
					  GenericValue subscribe = subscription.get(0);
					  subscriptionId =  subscribe.getString("subscriptionId");
				  }
			  }
			  if(UtilValidate.isEmpty(subscriptionId)){
				  Debug.logError("There are no 'active subscriptions' for Party Code  :"+boothId, module);
				  request.setAttribute("_ERROR_MESSAGE_", "There are no 'active subscriptions' for Party Code  :"+boothId);
				  TransactionUtil.rollback();
				  return "error";
			  }
			  conditionList.clear();
			  conditionList = UtilMisc.toList(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
			  conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
			  conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
			  conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.getDayStart(effectiveDate)));
			  conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.getDayEnd(effectiveDate)));
			  EntityCondition condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			  try{
				  List subscriptionprodList = delegator.findList("SubscriptionProduct", condition1, null, null, null, false);
				  if(UtilValidate.isNotEmpty(subscriptionprodList)){
					  int rows_deleted = delegator.removeAll(subscriptionprodList);
					  Debug.log(rows_deleted+" rows deleted from subscription entity");
				  }
			  }catch (GenericEntityException e1) {
				  Debug.logError(e1, module);
				  request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
				  TransactionUtil.rollback();
				  return "error";
			  }
		  	  for (int i = 0; i < rowCount; i++) {
		  		  productId = null;
		  		  quantity = null;
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
		  			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("productId" + thisSuffix)) {
		  			productId = (String) paramMap.get("productId"+thisSuffix);
		  		  }
		  		  
		  		  if(UtilValidate.isNotEmpty(quantityStr)){
		  			  try {
			  			  quantity = new BigDecimal(quantityStr);
			  		  } catch (Exception e) {
			  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
			  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
			  			  TransactionUtil.rollback();
			  			  return "error";
			  		  }
		  		  }
		  		  if(UtilValidate.isEmpty(quantity)){
		  			Debug.logError("quantity is empty for the product "+productId, module);
		  			request.setAttribute("_ERROR_MESSAGE_", "quantity is empty for the product "+productId);
		  			TransactionUtil.rollback();
		  			return "error";  
		  		  }
		  		  
		  		  if(quantity.compareTo(BigDecimal.ZERO)>0  && UtilValidate.isNotEmpty(productId)){
		  			    
			  			newValue = delegator.makeValue("SubscriptionProduct");
			  			newValue.set("productId", productId);
			  			newValue.set("quantity", quantity);
			  			newValue.set("fromDate", UtilDateTime.getDayStart(effectiveDate));
			  			newValue.set("thruDate", UtilDateTime.getDayEnd(effectiveDate));		  				
			  			newValue.set("subscriptionId", subscriptionId);
			  			newValue.set("productSubscriptionTypeId", productSubscriptionTypeId);
			  			newValue.set("sequenceNum", routeId);
			  			newValue.set("destinationFacilityId", destinationFacilityId);
			  			newValue.put("createdByUserLogin",userLogin.get("userLoginId"));
			  			newValue.put("createdDate",nowTimeStamp);   
			  			newValue.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
			  			newValue.put("lastModifiedDate",nowTimeStamp); 		  				
			  			try {
			  				delegator.create(newValue);
			  			} catch (GenericEntityException e) {
			  				Debug.logError("Error in storing Indent for Product : "+productId+ "\t"+e.toString(),module);
					  		request.setAttribute("_ERROR_MESSAGE_", "Error in storing Indent for Product : "+productId);
					  		TransactionUtil.rollback();
					  		return "error";			  				
			  			}
		  		  }
		  	 }//end of loop	
	  	  } catch (GenericEntityException e) {
	  		  try {
	  			  // only rollback the transaction if we started one...
	  			  TransactionUtil.rollback(beganTransaction, "Error saving subscription product", e);
	  		  } catch (GenericEntityException e2) {
	  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		  }
	  		  Debug.logError("An entity engine error occurred while saving  subscription indent", module);
	  	  } finally {
	  		  // only commit the transaction if we started one... this will throw an exception if it fails
	  		  try {
	  			  TransactionUtil.commit(beganTransaction);
	  		  } catch (GenericEntityException e) {
	  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while saving subscription product", module);
	  		  }
	  	  }
	  	  request.setAttribute("supplyDate", effectiveDateStr);
	  	  request.setAttribute("routeId", routeId);
	  	  request.setAttribute("_EVENT_MESSAGE_", "Successfully made entry from party: "+boothId);
	  	  return "success";     
	}
    
    public static String subscriptionIndentCorrection(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	  	  
	  	  List custTimePeriodList = FastList.newInstance();  	 
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  String boothId = null;
	  	  String productSubscriptionTypeId = null;
	  	  String productId = null;
	  	  String routeId = null;
	  	  String route = null;
	  	  String productSubscription = null;
	  	  String quantityStr = null;
	  	  BigDecimal quantity = BigDecimal.ZERO;
	  	  GenericValue newValue = null;
	  	  String subscriptionId = null;
	  	  boolean beganTransaction = false;
	  	  String effectiveDateStr = null;
	  	  try{
	  		  beganTransaction = TransactionUtil.begin();
	  		  if (paramMap.containsKey("productId")) {
	  			  productId = (String) paramMap.get("productId");
			  }	
			  if(UtilValidate.isEmpty(productId)){
				  Debug.logError("Missing Product Code", module);
				  request.setAttribute("_ERROR_MESSAGE_", "Missing productId");
				  TransactionUtil.rollback();
				  return "error";			  
			  }
	  		  if (paramMap.containsKey("routeId")) {
	  			  routeId = (String) paramMap.get("routeId");
			  }	
		  	  effectiveDateStr = (String) request.getParameter("effectiveDate");
		  	  Timestamp effectiveDate=null;
		  	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
		  		  try {
		  			  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
		  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
		  		  } catch (ParseException e) {
		  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
		  		  } catch (NullPointerException e) {
		  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
		  		  }
		  	  }	  	  
			  
		  	  for (int i = 0; i < rowCount; i++) {
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
		  			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("boothId" + thisSuffix)) {
		  			  boothId = (String) paramMap.get("boothId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("route" + thisSuffix)) {
		  			  route = (String) paramMap.get("route"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("productSubscription" + thisSuffix)) {
		  			  productSubscription = (String) paramMap.get("productSubscription"+thisSuffix);
		  		  }
		  		  if(UtilValidate.isNotEmpty(quantityStr)){
		  			  try {
			  			  quantity = new BigDecimal(quantityStr);
			  		  } catch (Exception e) {
			  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
			  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
			  			  TransactionUtil.rollback();
			  			  return "error";
			  		  }
		  		  }
		  		  if(UtilValidate.isEmpty(boothId) && (quantity.compareTo(BigDecimal.ZERO)<0 && quantityStr != "" && UtilValidate.isEmpty(route))){
	  			    	Debug.logError("Error processing details", module);
			  			request.setAttribute("_ERROR_MESSAGE_", "Error processing details details");
			  			TransactionUtil.rollback();
			  			return "error";		  				
			  	  }
			  	  else{
			  		  List conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
					  conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
					  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
					  List<GenericValue> subscription = null;
					  try {
						  subscription = delegator.findList("Subscription", condition, null, null, null, false);
					  }catch (GenericEntityException e1) {
						  Debug.logError(e1, module);
						  request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
						  TransactionUtil.rollback();
						  return "error";
					  }
					  subscription = EntityUtil.filterByDate(subscription, effectiveDate);
					  if(UtilValidate.isNotEmpty(subscription)){
						  if(subscription.size() == 1){
							  GenericValue subscribe = subscription.get(0);
							  subscriptionId =  subscribe.getString("subscriptionId");
						  }
					  }else{
						  Debug.logError("There are no 'active subscriptions' for the given facility", module);
						  request.setAttribute("_ERROR_MESSAGE_", "There are no 'active subscriptions' for the given facility");
						  TransactionUtil.rollback();
						  return "error";
					  }
			  		  List conditionsList = UtilMisc.toList(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			  		  conditionsList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS , subscriptionId));
			  		  conditionsList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS , route));
			  		  conditionsList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS , productSubscription));
					  conditionsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.getDayStart(effectiveDate)));
					  conditionsList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.getDayEnd(effectiveDate)));
					  EntityCondition condition1 = EntityCondition.makeCondition(conditionsList, EntityOperator.AND); 
					  try{
						  List subscriptionProdList = delegator.findList("SubscriptionProduct", condition1, null, null, null, false);
						  if(UtilValidate.isNotEmpty(subscriptionProdList)){
							  GenericValue subscriptionProd = (GenericValue)subscriptionProdList.get(0);
							  BigDecimal preRevisedQuantity = (BigDecimal)subscriptionProd.get("preRevisedQuantity");
							  BigDecimal originalQuantity = (BigDecimal)subscriptionProd.get("quantity");
							  BigDecimal preQuantity = BigDecimal.ZERO;
							  BigDecimal checkQuant = null;
							  if(UtilValidate.isEmpty(originalQuantity)){
								  originalQuantity = BigDecimal.ZERO;
							  }
							  checkQuant = quantity.subtract(originalQuantity);
							  if(checkQuant.compareTo(BigDecimal.ZERO)!= 0){
								
								  if(UtilValidate.isEmpty(preRevisedQuantity)){
									  subscriptionProd.set("preRevisedQuantity", originalQuantity);
								  }
								  subscriptionProd.set("quantity", quantity);
								  subscriptionProd.set("lastModifiedDate", UtilDateTime.nowTimestamp());
								  subscriptionProd.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
								  subscriptionProd.store();  
							  }
						  }
					  }catch (GenericEntityException e1) {
						  Debug.logError(e1, module);
						  request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
						  TransactionUtil.rollback();
						  return "error";
					  }
		  		  }
		  	 }//end of loop	
	  	  } catch (GenericEntityException e) {
	  		  try {
	  			  // only rollback the transaction if we started one...
	  			  TransactionUtil.rollback(beganTransaction, "Error saving gate pass", e);
	  		  } catch (GenericEntityException e2) {
	  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		  }
	  		  Debug.logError(e, "An entity engine error occurred while saving gate pass order", module);
	  	  } finally {
	  		  // only commit the transaction if we started one... this will throw an exception if it fails
	  		  try {
	  			  TransactionUtil.commit(beganTransaction);
	  		  } catch (GenericEntityException e) {
	  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while saving gate pass order", module);
	  		  }
	  	  }
	  	  request.setAttribute("supplyDate", effectiveDateStr);
	  	  request.setAttribute("_EVENT_MESSAGE_", "Successfully Adjusted the quantities for product : "+productId);
	  	  return "success";     
	}
    
    public static Map<String, Object> requestToOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String custRequestId = (String) context.get("custRequestId");
        Locale locale = (Locale) context.get("locale");     
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String salesChannel = "BYPROD_SALES_CHANNEL";
		String partyId = "";
		String approveOrder="";
		String currencyUomId = "INR";
        GenericValue indentDetailList = null;
        try{
        	indentDetailList=delegator.findOne("CustRequest",UtilMisc.toMap("custRequestId", custRequestId), false);
        }catch(GenericEntityException e){
        	 Debug.logError(e, e.toString(), module);
             return ServiceUtil.returnError(e.toString());
        }

        String productStoreId = indentDetailList.getString("productStoreId");
		String custRequestTypeId = indentDetailList.getString("custRequestTypeId");
		String orderDateStr = indentDetailList.getString("custRequestDate");
		
		String priceDateStr = UtilDateTime.nowTimestamp().toString();
		partyId = indentDetailList.getString("fromPartyId");	
		String orderName = indentDetailList.getString("custRequestName");
		
        String productId = null;
		Timestamp effectiveDate = null;
		String orderId = "";
		Timestamp priceDate = null;
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		BigDecimal quantity = BigDecimal.ZERO;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> resultMap = FastMap.newInstance();
		GenericValue subscription = null;		
		if (UtilValidate.isNotEmpty(orderDateStr)) { // 2011-12-25 18:09:45
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				effectiveDate = new java.sql.Timestamp(sdf.parse(orderDateStr).getTime());
				effectiveDate = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
				
				priceDate = new java.sql.Timestamp(sdf.parse(priceDateStr).getTime());
				priceDate = UtilDateTime.getDayStart(priceDate, TimeZone.getDefault(), locale);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ orderDateStr, module);
				/*request.setAttribute("_ERROR_MESSAGE_", e.getMessage());*/
				/*return "error";*/
				return ServiceUtil.returnError("Error");
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ orderDateStr, module);
				/*request.setAttribute("_ERROR_MESSAGE_", e.getMessage());*/
				/*return "error";*/
				return ServiceUtil.returnError("Error");
			}
		}
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create order without partyId: "+ partyId, module);
			return ServiceUtil.returnError("No PartyId found for creating order");
			/*request.setAttribute("_ERROR_MESSAGE_","Customer Id should not be empty");
			return "error";*/
		}
		GenericValue product =null;
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
		cart.setOrderType("SALES_ORDER");
		cart.setChannelType(salesChannel);		
		cart.setBillToCustomerPartyId(partyId);
		cart.setPlacingCustomerPartyId(partyId);
		cart.setShipToCustomerPartyId(partyId);		
		cart.setEndUserCustomerPartyId(partyId);
		cart.setEstimatedDeliveryDate(effectiveDate);
		cart.setOrderDate(effectiveDate);
		try {
			cart.setUserLogin(userLogin, dispatcher);
		} catch (Exception e) {
			Debug.logError(e, "Unable to create user login", module);
			/*request.setAttribute("_ERROR_MESSAGE_","Unable to create user login");
			return "error";*/
		}
		List<GenericValue> indentItemList = null;
		try{
			indentItemList = delegator.findList("CustRequestItem", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),null, null, null, false);
		}catch(GenericEntityException e){
			Debug.logError("Cannot create order without partyId: "+ partyId, module);
			return ServiceUtil.returnError("No PartyId found for creating order");
		}
		String custRequestItemSeqId = "";
		if(UtilValidate.isEmpty(indentItemList)){
			Debug.logError("No Items Found in Indent: "+ partyId, module);
			return ServiceUtil.returnError("No Items Found in Indent");
		}
		else{
			for(GenericValue eachItem : indentItemList){
				List<GenericValue> subscriptionProductsList = FastList.newInstance();
				quantity = (BigDecimal)eachItem.get("quantity");
				productId = (String)eachItem.get("productId");
				try { 
	        		Map<String, Object> priceResult;
	                Map<String, Object> priceContext = FastMap.newInstance();
	                priceContext.put("userLogin", userLogin);                                                                 
	                priceContext.put("productStoreId", productStoreId);                    
	                priceContext.put("productId", productId);
	                priceContext.put("partyId", partyId);
	                priceContext.put("priceDate", priceDate);
	                priceContext.put("productPriceTypeId", "PM_RC_P_PRICE");
	        		
	        		priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);  
	                if (ServiceUtil.isError(priceResult)) {
	                    Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
	                    return ServiceUtil.returnError("There was an error while calculating the price: "+ServiceUtil.getErrorMessage(priceResult));
	                }  
	        		cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null, 
	        				quantity, (BigDecimal)priceResult.get("price"),
	                        null, null, null, null, null, null, null,
	                        null, null, null, null, null, null, dispatcher,
	                        cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
	            } catch (Exception exc) {
	                Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
	                return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: " + exc.getMessage());
	            }
			}
			cart.setDefaultCheckoutOptions(dispatcher);
			CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator,cart);
			try {
				checkout.calcAndAddTax();
			} catch (GeneralException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
			orderId = (String) orderCreateResult.get("orderId");

			try {
				GenericValue createdOrder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if (createdOrder != null) {
					/*createdOrder.put("orderSubType", custRequestTypeId);*/
					createdOrder.put("custRequestId", custRequestId);
					createdOrder.store();
	            }
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (UtilValidate.isNotEmpty(orderId)) {
				boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
				result.put("orderId", orderId);
				
			}
			
		}
		Map<String, Object> shipmentResult = ServiceUtil.returnSuccess();
        Map<String, Object> shipInput = FastMap.newInstance();
        shipInput.put("userLogin", userLogin);   
        shipInput.put("primaryOrderId", orderId);
        shipInput.put("primaryShipGroupSeqId", "0001");
        shipInput.put("statusId", "SHIPMENT_INPUT");
        try{
        	shipmentResult = dispatcher.runSync("createShipment", shipInput);
        }
        catch (GenericServiceException e) {
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.getMessage());
        }
        String shipmentId = (String)shipmentResult.get("shipmentId");
        result.put("shipmentId",shipmentId);
        result.put("orderId", orderId);
		return result;
	}
    
    public static Map<String, Object> getByprodFactoryStore(Delegator delegator){
        
    	Map<String, Object> result = FastMap.newInstance(); 
    	String productStoreGroupId = "BYPRODUCTS";
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
    
    public static String processStockXfer(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);		

		String custRequestId = (String)request.getParameter("custRequestId");
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		
	  	  Timestamp effectiveDate=null;
	  	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
	  		  try {
	  			  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
	  		  } catch (ParseException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	              // effectiveDate = UtilDateTime.nowTimestamp();
	  		  } catch (NullPointerException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	               //effectiveDate = UtilDateTime.nowTimestamp();
	  		  }
	  	  }	 
		
		if(UtilValidate.isEmpty(effectiveDate)){
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> resultMap = FastMap.newInstance();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String fromFacilityId = (String)request.getParameter("fromFacility");
		String facilityIdTo = (String)request.getParameter("toFacility");
		String productId = (String)request.getParameter("productId");
		String quantityStr = (String)request.getParameter("quantity");
		
		Map inventoryItemMap = FastMap.newInstance();
    	BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
    	BigDecimal requestedQuantity = BigDecimal.ZERO;
		
    	if(UtilValidate.isNotEmpty(quantityStr)){
			  try {
				  requestedQuantity = new BigDecimal(quantityStr);
	  		  } catch (Exception e) {
	  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
	  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
	  			  return "error";
	  		  }
		}
    	
		List conditionList = FastList.newInstance();
		
		String inventoryItemId = null;
		String inventoryTransferId = null;
		Map inputMap = FastMap.newInstance();
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, fromFacilityId));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		List<GenericValue> inventoryItems;
		try {
			inventoryItems = delegator.findList("InventoryItem", condition , null, null, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		
		for (GenericValue inventoryItem : inventoryItems) {
			
			if(UtilValidate.isNotEmpty(inventoryItem.get("availableToPromiseTotal"))){
				if((((BigDecimal) inventoryItem.get("availableToPromiseTotal")).compareTo(requestedQuantity)>= 0) && UtilValidate.isNotEmpty(inventoryItem.get("availableToPromiseTotal"))){
					inventoryItemId = (String) inventoryItem.get("inventoryItemId");
				}
				if( ((BigDecimal)(inventoryItem.get("availableToPromiseTotal")) ).compareTo(BigDecimal.ZERO) >= 0 ){
					
					String tempInventoryItemId = null;
					BigDecimal tempAvailableToPromiseTotal = BigDecimal.ZERO;
					tempInventoryItemId = (String) inventoryItem.get("inventoryItemId");
					tempAvailableToPromiseTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");
					
					inventoryItemMap.put(tempInventoryItemId, tempAvailableToPromiseTotal);
					availableToPromiseTotal = availableToPromiseTotal.add(tempAvailableToPromiseTotal);
				}
			}
		}	
		
		if(UtilValidate.isNotEmpty(inventoryItemId)){
			Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", inventoryItemId, "custRequestId", custRequestId, "statusId","IXF_COMPLETE", "facilityId",fromFacilityId, "facilityIdTo", facilityIdTo, "xferQty", requestedQuantity.toString(), "receiveDate", effectiveDate);
			Map<String, Object> inventoryXferResult;
			try {
				inventoryXferResult = dispatcher.runSync("createInventoryTransfer", input);
				if (ServiceUtil.isError(inventoryXferResult)) {
					Debug.logError("There was an error while creating  Inventory Transfer: " + ServiceUtil.getErrorMessage(result), module);
					request.setAttribute("_ERROR_MESSAGE_", "There was an error while creating  Inventory Transfer: " + ServiceUtil.getErrorMessage(inventoryXferResult));
					return "error";
	            }
				inventoryTransferId = (String) inventoryXferResult.get("inventoryTransferId");
				
			} catch (GenericServiceException e) {
				Debug.logError(e, module);
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				return "error";
			}
		}
		else if(UtilValidate.isEmpty(inventoryItemId) && ((availableToPromiseTotal).compareTo(requestedQuantity) >= 0)){
			
			Iterator invItemIter = inventoryItemMap.entrySet().iterator();
			while (invItemIter.hasNext()) {
				BigDecimal tempXferQty = BigDecimal.ZERO;
				Map.Entry invItemEntry = (Entry) invItemIter.next();
				
				BigDecimal tempATP = (BigDecimal) invItemEntry.getValue();
				if((requestedQuantity).compareTo(tempATP) >= 0){
					tempXferQty = tempATP;
					requestedQuantity = requestedQuantity.subtract(tempATP);
				}else{
					tempXferQty = requestedQuantity;
				}
				if(tempXferQty.equals(BigDecimal.ZERO)){
					continue;
				}
				Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", invItemEntry.getKey(), "custRequestId", custRequestId, "statusId","IXF_COMPLETE", "facilityId",fromFacilityId, "facilityIdTo", facilityIdTo, "xferQty", tempXferQty, "receiveDate", effectiveDate);
				Map<String, Object> inventoryXferResult;
				try {
					inventoryXferResult = dispatcher.runSync("createInventoryTransfer", input);
					if (ServiceUtil.isError(inventoryXferResult)) {
						Debug.logError("There was an error while creating  Inventory Transfer: " + ServiceUtil.getErrorMessage(result), module);
						request.setAttribute("_ERROR_MESSAGE_", "There was an error while creating  Inventory Transfer: " + ServiceUtil.getErrorMessage(inventoryXferResult));
						return "error";
		            }
					inventoryTransferId = (String) inventoryXferResult.get("inventoryTransferId");
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
					request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
					return "error";
				}
			}
	    	request.setAttribute("inventoryTransferId", inventoryTransferId);
		}
		try {
			Map populateSaleQty = dispatcher.runSync("populateInventorySummary", UtilMisc.<String, Object>toMap("facilityId", fromFacilityId, "effectiveDate", effectiveDate, "productId", productId, "xferOut", requestedQuantity, "userLogin", userLogin));
			Map populateReceivedQty = dispatcher.runSync("populateInventorySummary", UtilMisc.<String, Object>toMap("facilityId", facilityIdTo, "effectiveDate", effectiveDate, "productId", productId, "xferIn", requestedQuantity, "userLogin", userLogin));
		} catch (GenericServiceException e1) {
			Debug.logError(e1, module);
			request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
			return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", "Stock transfer from parlor ["+fromFacilityId+"] to parlor ["+facilityIdTo+"]  is successful");
		return "success";
	}
    
    public static String createParlorSalesIndent(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	Locale locale = UtilHttp.getLocale(request);
	  	Map<String, Object> result = ServiceUtil.returnSuccess();
	  	HttpSession session = request.getSession();
	  	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	  	Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	String facilityId = null;
	  	Map<String, Object> inputParamMap = FastMap.newInstance();
	  	inputParamMap.put("userLogin", userLogin);
	  	String orderId = null;
	  	String requireProductInvCheck = "";
	  	Map<String, Object> orderItemMap = FastMap.newInstance();
	  	List<GenericValue> orderItems = null;
	  	BigDecimal qtyToCheck = BigDecimal.ZERO;
	  	String errMsg = "";
	  	int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	if (rowCount < 1) {
	  		Debug.logError("No rows to process, as rowCount = " + rowCount, module);
	  		request.setAttribute("_ERROR_MESSAGE_", "No products to process order");
	  		return "error";
	  	}
	  	GenericValue tenantCheck = null;
	  	try{
	  		tenantCheck = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyName","enableInvCheck","propertyTypeEnumId","INV_CHECK"), false);
    	}catch(GenericEntityException e){
			Debug.logError("Error fetching TenantConfiguration " + e.getMessage(), module);
			return "error";
		}
	  	String productId = null;
	  	String quantityStr = null;
	  	BigDecimal quantity = BigDecimal.ZERO;
	  	GenericValue newValue = null;
	  	orderId = (String) paramMap.get("orderId");
	  	if (paramMap.containsKey("boothId")) {
	  		facilityId = (String) paramMap.get("boothId");
	  		GenericValue facility = null;
	  		try{
	  			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
	  		}catch(GenericEntityException e){
	  			Debug.logError("Error fetching facility " + e.getMessage(), module);
	  			return "error";
	  		}
	  		String categoryTypeEnum = (String)facility.get("categoryTypeEnum");
	  		if(!categoryTypeEnum.equals("PARLOUR")){
	  			request.setAttribute("_ERROR_MESSAGE_", "This Booth is not of type Parlour");
				return "error";	
	  		}
  		}	
  		else {
  			request.setAttribute("_ERROR_MESSAGE_", "Missing boothId");
  			return "error";			  
  		}
  		inputParamMap.put("facilityId", facilityId);
  		inputParamMap.put("orderId", orderId);
  		inputParamMap.put("productPriceTypeId", "PM_RC_P");
  		String effDate = (String)paramMap.get("effectiveDate");
  		Timestamp effectiveDate=null;
	  	if (UtilValidate.isNotEmpty(effDate)) { //2011-12-25 18:09:45
	  		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
	  		  try {
	  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effDate).getTime());
	  		  } catch (ParseException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + effDate, module);
	              // effectiveDate = UtilDateTime.nowTimestamp();
	  		  } catch (NullPointerException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + effDate, module);
	               //effectiveDate = UtilDateTime.nowTimestamp();
	  		  }
	  	  }
	  	  else{
	  		effectiveDate = UtilDateTime.nowTimestamp();
	  	  }
	  	if(UtilValidate.isNotEmpty(orderId)){
	  		try{
	  			List condList = FastList.newInstance();
	  			condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	  			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
				EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
				orderItems = delegator.findList("OrderItem", cond, null,null,null, false);
	  		}catch(GenericEntityException e){
	  			request.setAttribute("_ERROR_MESSAGE_", "Cannot fetch order items for order: "+orderId);
				return "error";
	  		}
	  		if(UtilValidate.isNotEmpty(orderItems)){
	  			for(GenericValue eachItem : orderItems){
	  				String prod = eachItem.getString("productId");
	  				BigDecimal qty = (BigDecimal)eachItem.get("quantity");
	  				orderItemMap.put(prod, qty);
	  			}
	  		}
	  	}
	  	Timestamp startDate = UtilDateTime.getDayStart(effectiveDate);
	  	Timestamp endDate = UtilDateTime.getDayEnd(effectiveDate);
	  	inputParamMap.put("supplyDate", effectiveDate);
  		Map prodQuant = FastMap.newInstance();
  		List conditionList = FastList.newInstance();
  		for (int i = 0; i < rowCount; i++) {
  			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
  			if (paramMap.containsKey("quantity" + thisSuffix)) {
  				quantityStr = (String) paramMap.get("quantity" + thisSuffix);
  			}
  			if (paramMap.containsKey("productId" + thisSuffix)) {
  				productId = (String) paramMap.get("productId"+thisSuffix);
  			}
  			if(UtilValidate.isNotEmpty(quantityStr)){
  				try {
  					quantity = new BigDecimal(quantityStr);
  					qtyToCheck = quantity;
  				} catch (Exception e) {
  					Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
  					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
  					return "error";
  				}
  			}
  			String inventoryCheck = (String)tenantCheck.get("propertyValue"); 
  			try{
  				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
  				if(UtilValidate.isNotEmpty(product.getString("requireInventory"))){
  					requireProductInvCheck = product.getString("requireInventory");
  				}
  			}catch(GenericEntityException e){
  					Debug.logError(e, "Not a valid product : " + productId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Not a valid product : " + productId);
					return "error";
  			}
  			
  			if(UtilValidate.isNotEmpty(productId) && (quantity.compareTo(BigDecimal.ZERO)>0 && quantityStr != "")){
  				if(orderItemMap.containsKey(productId)){
  					BigDecimal originalQty = (BigDecimal)orderItemMap.get(productId);
  					qtyToCheck = quantity.subtract(originalQty);
  					
  				}
  				if(inventoryCheck.equalsIgnoreCase("Y") && qtyToCheck.compareTo(BigDecimal.ZERO)>0 && !requireProductInvCheck.equalsIgnoreCase("N")){
  					conditionList.clear();
  					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
  					conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
  					conditionList.add(EntityCondition.makeCondition("saleDate", EntityOperator.LESS_THAN_EQUAL_TO, startDate));
  					EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
  					List<GenericValue> inventorySummReport = null;
  					try{
  						inventorySummReport = delegator.findList("InventorySummary", cond, null , UtilMisc.toList("-saleDate"), null, false);
  					} catch(GenericEntityException e) {
  						request.setAttribute("_ERROR_MESSAGE_", "Not Enough Stock available for product: "+productId);
  						return "error";
  					}
  					if(UtilValidate.isEmpty(inventorySummReport)){
  						Debug.logError("No Enough Stock for product: "+productId, module);
  						/*request.setAttribute("_ERROR_MESSAGE_", "Not Enough Stock available for product: "+productId);*/
  						errMsg += "Not Enough stock available for product : "+productId+"; ";
  						/*return "error";*/
  					}
  					else{
  						GenericValue inventoryValue =  EntityUtil.getFirst(inventorySummReport);
  						if(UtilValidate.isNotEmpty(inventoryValue)){
  							BigDecimal availableStock = (BigDecimal)inventoryValue.get("closingBalance");
  							BigDecimal stock = availableStock.subtract(qtyToCheck);
  							if(stock.compareTo(BigDecimal.ZERO) >= 0){
  								prodQuant.put(productId, quantity);
  							}
  							else{
  								errMsg += "Not Enough stock available for product : "+productId+"; ";
  							}
  						}
  					}
  	  			}
  				else{
  					prodQuant.put(productId, quantity);
  				}
  				
  			}
  		}//end of loop
  		inputParamMap.put("prodQuant", prodQuant);
  		GenericValue orderHeader = null;
  		if(UtilValidate.isNotEmpty(orderId) && rowCount ==1 && UtilValidate.isEmpty(prodQuant)){
  			try{
  				orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId),false);
  			}
  			catch(GenericEntityException e){
  				Debug.logError("ERROR in fetching order header", module);
				request.setAttribute("_ERROR_MESSAGE_", "ERROR in fetching order header");
				return "error";
  			}
  			String orderStatus = "";
  			String storeId = "";
  			if(UtilValidate.isNotEmpty(orderHeader))
  			{
  				orderStatus = orderHeader.getString("statusId");
  				storeId = orderHeader.getString("originFacilityId");
  			}
  			try {
				Map<String, Object> cancelResult = dispatcher.runSync("cancelParlorSalesOrder", UtilMisc.toMap("userLogin", userLogin, "statusId", orderStatus,"orderId", orderId, "productStoreId", storeId));
				if(ServiceUtil.isError(cancelResult)){
					Debug.logError("ERROR in cancelParlorSalesOrder service", module);
					request.setAttribute("_ERROR_MESSAGE_", (String)cancelResult.get("errorMessage"));
					return "error";
				}
			} catch (GenericServiceException e) {
				Debug.logError(e, "Trouble calling cancelParlorSalesOrder service;", module);
				request.setAttribute("_ERROR_MESSAGE_", "Trouble calling cancelParlorSalesOrder service");
				return "error";
			}
			return "success";
  		}
  		if(UtilValidate.isEmpty(prodQuant)){
  			Debug.logError("No Products to Process for Order: ", module);
			request.setAttribute("_ERROR_MESSAGE_", "No Products to Process for Order ");
			return "error";
  		}
  		else{
  			try {
				Map<String, Object> serviceResult = dispatcher.runSync("processParlorSalesOrder", inputParamMap);
				if(ServiceUtil.isError(serviceResult)){
					Debug.logError("ERROR in processParlorSalesOrder service", module);
					request.setAttribute("_ERROR_MESSAGE_", (String)serviceResult.get("errorMessage"));
					return "error";
				}
			} catch (GenericServiceException e) {
				Debug.logError(e, "Trouble calling processParlorSalesOrder service;", module);
				request.setAttribute("_ERROR_MESSAGE_", "Trouble calling processParlorSalesOrder service");
				return "error";
			}
  		}
  		request.setAttribute("supplyDate", effDate);
	    
	    if(UtilValidate.isNotEmpty(errMsg)){
	    	request.setAttribute("_ERROR_MESSAGE_", errMsg);
	    	return "error";
	    }
	    request.setAttribute("_EVENT_MESSAGE_", "Sales order for parlor ["+facilityId+"] is successful");
		return "success";     
	}
    public static Map<String, Object> processParlorSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Map prodQuant = (Map) context.get("prodQuant");
        String facilityId = (String) context.get("facilityId");
        String orderId = (String) context.get("orderId");
        String productPriceTypeId = (String) context.get("productPriceTypeId");
        Timestamp supplyDate = (Timestamp) context.get("supplyDate");
        Locale locale = (Locale) context.get("locale");
        String productStoreId = null;
	  	String salesChannel = "PARLOR_SALES_CHANNEL";
	  	String partyId = "";
	  	String currencyUomId = "INR";
	  	String shipmentId = null;
	  	String shipmentTypeId = "BYPRODUCTS_PRSALE";
  		Timestamp priceDate = null;
  		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
  		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
  		String productId =null;
  		
  		List<GenericValue> productStore = null;
  		
  		if(UtilValidate.isNotEmpty(orderId)){
  			
  		  	List<GenericValue> OrderList = null;
  	   		try{
  	   			OrderList = delegator.findList("OrderHeader", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
  	  		} catch (GenericEntityException e) {
  	  			Debug.logError(e, module);
  	  			return ServiceUtil.returnError("Error fetching OrderHeader");
  	 	    }
  		  	String statusId = "";
  			if(UtilValidate.isNotEmpty(OrderList)){
  				statusId = OrderList.get(0).getString("statusId");
  				Debug.log("status for the order : "+statusId);
  			}
  			Map<String, Object> cancelOrderInput = FastMap.newInstance();
  			cancelOrderInput.put("orderId", orderId);
  			cancelOrderInput.put("statusId", statusId);
  			cancelOrderInput.put("productStoreId", facilityId);
  			cancelOrderInput.put("userLogin", userLogin);
  			Map<String, Object> cancelResultMap = FastMap.newInstance();
  			try {
  				cancelResultMap = dispatcher.runSync("cancelParlorSalesOrder", cancelOrderInput);
  			} catch (GenericServiceException e) {
  				Debug.logError(e, module);
  				return ServiceUtil.returnError("Error calling the service cancelParlorSalesOrder");
  			}
  			if (ServiceUtil.isError(cancelResultMap)) {
  				Debug.logError("There was an error while cancelling order: " + ServiceUtil.getErrorMessage(cancelResultMap), module);
  				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(cancelResultMap));
  		    }
  		}
  		
  		if(UtilValidate.isNotEmpty(facilityId)){
  			GenericValue facility = null;
  			try{
  				productStore = delegator.findList("ProductStore", EntityCondition.makeCondition("inventoryFacilityId", EntityOperator.EQUALS, facilityId), null, null, null, false);	
  				productStoreId = (String)productStore.get(0).getString("productStoreId");
  				facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
  				if(UtilValidate.isNotEmpty(facility)){
  					partyId = (String)facility.getString("ownerPartyId");
  				}
  			}catch(GenericEntityException e){
  				Debug.logError("Error fetching order Items " + e.getMessage(), module);
  				return ServiceUtil.returnError("unable to get product store Id");
  			}
  		}
  		// lets get the shipment if already exits else create new one
	  	List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, UtilDateTime.getDayStart(effectiveDate), UtilDateTime.getDayEnd(effectiveDate), shipmentTypeId);
	  	if(UtilValidate.isEmpty(shipmentList)){
	  		 GenericValue newEntity = delegator.makeValue("Shipment");
        	
             newEntity.set("estimatedShipDate", effectiveDate);
             newEntity.set("shipmentTypeId", shipmentTypeId);
             newEntity.set("statusId", "GENERATED");
             newEntity.set("createdDate", nowTimeStamp);
             newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
             newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
             newEntity.set("lastModifiedDate",nowTimeStamp);
             try {
                 delegator.createSetNextSeqId(newEntity);            
                shipmentId = (String) newEntity.get("shipmentId");
                
             } catch (GenericEntityException e) {
                 Debug.logError(e, module);
         		 return ServiceUtil.returnError("unable to create shipment id");
             }  
	  		
	  	}else{
	  		shipmentId = (String)shipmentList.get(0);
	  	}
  		priceDate = UtilDateTime.getDayStart(effectiveDate);
  		if (UtilValidate.isEmpty(partyId)) {
  			Debug.logError("Cannot create order without partyId: "+ partyId, module);
  			return ServiceUtil.returnError("partyId is empty");
  		}
  		GenericValue product =null;
  		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
  		try {
  			cart.setOrderType("SALES_ORDER");
  			cart.setChannelType(salesChannel);		
  			cart.setBillToCustomerPartyId(partyId);
  			cart.setPlacingCustomerPartyId(partyId);
  			cart.setShipToCustomerPartyId(partyId);		
  			cart.setEndUserCustomerPartyId(partyId);
  			cart.setShipmentId(shipmentId);
  			cart.setEstimatedDeliveryDate(effectiveDate);
  			cart.setOrderDate(effectiveDate);
  			cart.setUserLogin(userLogin, dispatcher);
  			cart.setFacilityId(facilityId);
  		} catch (Exception e) {
  			Debug.logError(e, "Error in setting cart parameters", module);
  			return ServiceUtil.returnError("Error in setting cart parameters");
  		}
		Iterator prodMapIter = prodQuant.entrySet().iterator();
  		while (prodMapIter.hasNext()) {
  			Map.Entry eachEntry = (Entry) prodMapIter.next();
  			productId = (String) eachEntry.getKey();
  			BigDecimal quantity = (BigDecimal) eachEntry.getValue();
  					
			Map<String, Object> priceResult;
			Map<String, Object> priceContext = FastMap.newInstance();
			priceContext.put("userLogin", userLogin);                                                                 
			priceContext.put("productStoreId", facilityId);                    
			priceContext.put("productId", productId);
			priceContext.put("partyId", partyId);
			priceContext.put("priceDate", priceDate);
			priceContext.put("productPriceTypeId", productPriceTypeId+"_PRICE");
			priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext); 
			if (ServiceUtil.isError(priceResult)) {
  				Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
  				return ServiceUtil.returnError("There was an error while calculating the price");
			}
			 try{
				 cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null,	quantity, (BigDecimal)priceResult.get("price"),
				            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
				            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
				 
			 }  
			 catch (Exception exc) {
				 Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
				 return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: ");
             }
  			
  		}//end of while
  		cart.setDefaultCheckoutOptions(dispatcher);
  		CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator,cart);
  		/*try {
  			checkout.calcAndAddTax();
  		} catch (GeneralException e1) {
		// TODO Auto-generated catch block
  			Debug.logError(e1, module);
			return ServiceUtil.returnError("Error calculating Taxes");
  		}*/
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
       
        try {
			checkout.calcAndAddTax(prodPriceType);
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
  		Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
  		orderId = (String) orderCreateResult.get("orderId");
  		String orderItemSeqId = null;
  		List conditionsList = FastList.newInstance();
  		if (UtilValidate.isNotEmpty(orderId)) {
  			Debug.logInfo("Created order with id: " + orderId, module);
  			conditionsList.clear();
  			conditionsList = UtilMisc.toList(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
  			conditionsList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL , "VAT_SALE"));
  			EntityCondition condition1 = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);   
  			try{
  				List<GenericValue> orderedAdjustItems = delegator.findList("OrderAdjustment", condition1, null, null, null, false);
  				orderItemSeqId = null;
  				if(UtilValidate.isNotEmpty(orderedAdjustItems)){
  					GenericValue orderAdjustItem = EntityUtil.getFirst(orderedAdjustItems);
  					orderItemSeqId = orderAdjustItem.getString("orderItemSeqId");
  					BigDecimal totalAmount = BigDecimal.ZERO;
  					for(GenericValue eachAdjustItem : orderedAdjustItems){
  						BigDecimal amount = (BigDecimal)eachAdjustItem.get("amount");
  						totalAmount = totalAmount.add(amount);
  					}
  					conditionsList.clear();
  		  			conditionsList = UtilMisc.toList(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
  		  			conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "ITEM_APPROVED"));
  		  		    conditionsList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS , orderItemSeqId));
  		  			EntityCondition cond = EntityCondition.makeCondition(conditionsList, EntityOperator.AND); 
  		  			List<GenericValue> orderItem = delegator.findList("OrderItem", cond, null, null, null, false);
  		  			if(UtilValidate.isNotEmpty(orderItem)){
  		  				GenericValue OrdItem = (GenericValue)orderItem.get(0);
  		  				OrdItem.set("unitPrice", totalAmount);
  		  				OrdItem.store();
  		  			}
  					int rows_deleted = delegator.removeAll(orderedAdjustItems);
  					Debug.log(rows_deleted+" rows deleted from OrderAdjustment entity");
  				}
  			}catch (GenericEntityException e1) {
  				Debug.logError(e1, module);
  				return ServiceUtil.returnError("Error Adjusting Order Taxes");
  			}
  			boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
  		}
  		else{
  			Debug.logInfo("Order is empty: " + orderId, module);
  			return ServiceUtil.returnError("No order generated");
  		}
  		String inventoryItemId = null;
  		List<GenericValue> orderItems = null;
  		try{
  			orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
 		} catch (GenericEntityException e) {
 			Debug.logError(e, module);
 			return ServiceUtil.returnError("No order found with orderId :"+orderId);
	    }
        for (GenericValue orderItem : orderItems) {
        	Map inventoryItemMap = FastMap.newInstance();
        	BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
        	BigDecimal requestedQuantity = BigDecimal.ZERO;
        	List conditionList = FastList.newInstance();
        	inventoryItemId = null;
			productId = (String) orderItem.get("productId");
			orderItemSeqId = (String) orderItem.get("orderItemSeqId");
    		requestedQuantity = (BigDecimal) orderItem.get("quantity");
    		
    		Map inputMap = FastMap.newInstance();
			inputMap.put("facilityId", facilityId);
			inputMap.put("userLogin",userLogin);
			inputMap.put("productId",productId);
			inputMap.put("quantity", requestedQuantity.negate());
			inputMap.put("isSale", Boolean.TRUE);
			inputMap.put("effectiveDate", priceDate);
			try {
				Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
				if(ServiceUtil.isError(serviceResult)){
					Debug.logError("Trouble in adjustInventory service", module);
					return ServiceUtil.returnError("Trouble in adjustInventory service");
				}
			} catch (GenericServiceException e) {
				Debug.logError(e, "Trouble calling adjustInventory service", module);
				return ServiceUtil.returnError("Trouble calling adjustInventory service");
			}
			
		}
 	
        /* generate invoice	*/
        String invoiceId = null; 
        Map<String, Object> invoiceInput = FastMap.newInstance();
        invoiceInput.put("orderId", orderId);
        invoiceInput.put("userLogin", userLogin);
        Map<String, Object> resultInvoiceMap = null;
        try {
        	resultInvoiceMap = dispatcher.runSync("createInvoiceForOrderAllItems", invoiceInput);
        } catch (GenericServiceException e) {
        	Debug.logError(e, module);
        	return ServiceUtil.returnError("ERROR in calling createInvoiceForOrderAllItems service");
        }
        if (ServiceUtil.isError(resultInvoiceMap)) {
        	Debug.logError(ServiceUtil.getErrorMessage(resultInvoiceMap), module);
        	return ServiceUtil.returnError("Error in service createInvoiceForOrderAllItems");
        }
        else{
        	invoiceId = (String) resultInvoiceMap.get("invoiceId");
        	Map<String, Object> changeInvoicestatusInput = FastMap.newInstance();
        	changeInvoicestatusInput.put("invoiceId", invoiceId);
        	changeInvoicestatusInput.put("statusId", "INVOICE_READY");
        	changeInvoicestatusInput.put("userLogin", userLogin);
 	        Map<String, Object> resultMap = null;
 	        try {
 	        	resultMap = dispatcher.runSync("setInvoiceStatus", changeInvoicestatusInput);
 	        } catch (GenericServiceException e) {
 	        	Debug.logError(e, module);
 	        	return ServiceUtil.returnError("Error in calling service setInvoiceStatus");
 	        }
 	        if(ServiceUtil.isError(resultMap)){
 	        	return ServiceUtil.returnError("Error in service setInvoiceStatus");
 	        }
        }
        if(UtilValidate.isNotEmpty(invoiceId)){
        	Map<String, Object> applyPaymentInput = FastMap.newInstance();
        	applyPaymentInput.put("invoiceId", invoiceId);
        	applyPaymentInput.put("userLogin", userLogin);
        	Map<String, Object> resultPaymentMap = null;
        	try {
        		resultPaymentMap = dispatcher.runSync("settleInvoiceAndPayments", applyPaymentInput);
        	} catch (GenericServiceException e) {
        		Debug.logError(e, module);
        		return ServiceUtil.returnError("Error in calling service settleInvoiceAndPayments");
        	}
        	if (ServiceUtil.isError(resultPaymentMap)) {
        		Debug.logError(ServiceUtil.getErrorMessage(resultPaymentMap), module);
        		return ServiceUtil.returnError("Error in service settleInvoiceAndPayments");
        	}
        }
        result.put("orderId", orderId);
        return result;
    }
    public static Map<String, Object> cancelParlorSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");
        String productStoreId = (String) context.get("productStoreId");
        if(UtilValidate.isEmpty(orderId)){
			return ServiceUtil.returnError("orderId is null" + orderId);
        }
        else{
        	boolean cancelled = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);
        	List<GenericValue> orderItems = null;
	  		try{
	  			orderItems = delegator.findList("OrderHeaderAndItems", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
     		} catch (GenericEntityException e) {
     			Debug.logError(e, module);
        		return ServiceUtil.returnError("Error in fetching Order :" + orderId);
		    }
     		if(UtilValidate.isEmpty(orderItems)){
     			Debug.logError("Error ", module);
        		return ServiceUtil.returnError("No Order Items available to update inventory for cancelled order:" + orderId);
     		}else{ 			
     			
 				for (GenericValue orderItem : orderItems) {
    	        	Map inventoryItemMap = FastMap.newInstance();
    	        	BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
    	        	BigDecimal requestedQuantity = BigDecimal.ZERO;
    	        	List conditionList = FastList.newInstance();
    	        	String inventoryItemId = null;
    				String productId = (String) orderItem.get("productId");
            		requestedQuantity = (BigDecimal) orderItem.get("quantity");
            		Timestamp effectiveDate = (Timestamp) orderItem.get("orderDate");
            		
            		Map inputMap = FastMap.newInstance();
        			inputMap.put("facilityId", productStoreId);
        			inputMap.put("userLogin",userLogin);
        			inputMap.put("productId",productId);
        			inputMap.put("quantity", requestedQuantity);
        			inputMap.put("effectiveDate", effectiveDate);
        			inputMap.put("isSale", Boolean.TRUE);

        			try {
        				Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
        				if(ServiceUtil.isError(serviceResult)){
        					Debug.logError("Trouble in adjustInventory service", module);
        					return ServiceUtil.returnError("Trouble in adjustInventory service");
        				}
        			} catch (GenericServiceException e) {
        				Debug.logError(e, "Trouble calling adjustInventory service", module);
        				return ServiceUtil.returnError("Trouble calling adjustInventory service");
        			}
    			}
     		}
		}
        List<GenericValue> OrderItemBillingList = null;
        String invoiceId = null;
  		try{
  			OrderItemBillingList = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
 		} catch (GenericEntityException e) {
 			Debug.logError(e, module);
    		return ServiceUtil.returnError("Error in fetching Order Item billing :" + orderId);
	    }
 		if(UtilValidate.isEmpty(OrderItemBillingList)){
 			Debug.logError("OrderItemBillingList is empty", module);
    		return ServiceUtil.returnError("No invoice found for order :" + orderId);
 		}
 		else{
 			invoiceId = OrderItemBillingList.get(0).getString("invoiceId");
 			
 			Map<String, Object> cancelInvoiceInput = FastMap.newInstance();
 	       	cancelInvoiceInput.put("invoiceId", invoiceId);
 	        cancelInvoiceInput.put("statusId", "INVOICE_CANCELLED");
 	       	cancelInvoiceInput.put("userLogin", userLogin);
 	        Map<String, Object> resultMap = null;
 	        try {
 	        	resultMap = dispatcher.runSync("setInvoiceStatus", cancelInvoiceInput);
 	        	if(ServiceUtil.isError(resultMap)){
 	        		Debug.logError("Error in service setInvoiceStatus while cancelling invoice", module);
 	 	        	return ServiceUtil.returnError("Error in service setInvoiceStatus while cancelling invoice :" + invoiceId);
 	        	}
 	        } catch (GenericServiceException e) {
 	        	Debug.logError(e, module);
 	        	return ServiceUtil.returnError("Error in cancelling invoice :" + invoiceId);
 	        }
 	        Debug.log("invoiceId cancelled is : "+invoiceId);
 		}
        return result;
    }
   
    public static Map<String, Object> getByproductRoutes(Delegator delegator) {
    	List<String> routes= FastList.newInstance();
    	List<GenericValue> facilities = null;
    	try {
    		facilities = delegator.findList("Facility", EntityCondition.makeCondition(EntityOperator.AND, "facilityTypeId", "ROUTE"), null, UtilMisc.toList("sequenceNum"), null, false);
            routes = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();        
        result.put("routesList", facilities);
        result.put("routeIdsList", routes);

        return result;
    }
    public static Map<String, Object> getByProdRouteBooths(Delegator delegator,String routeId){
    	return getByProdRouteBooths(delegator, routeId, null, null);
	}
    public static Map<String, Object> getByProdRouteBooths(Delegator delegator,String routeId, Timestamp effectiveDate){
    	return getByProdRouteBooths(delegator, routeId, null, effectiveDate);
	}

    public static Map<String, Object> getByProdRouteBooths(Delegator delegator,String routeId, String boothCategory, Timestamp effectiveDate){
    	List<String> boothIds = FastList.newInstance(); 
    	List<GenericValue> booths = null;
    	
    	if(UtilValidate.isEmpty(effectiveDate)){
    		effectiveDate = UtilDateTime.nowTimestamp();
    	}
    	
    	try {
    		List conditionList= FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("byProdRouteId", EntityOperator.EQUALS , routeId));
			conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "BOOTH"));
			conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	    			 EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
			if (!UtilValidate.isEmpty(boothCategory)) {
				conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS , boothCategory));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    		booths = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
    		//booths = EntityUtil.filterByDate(booths, effectiveDate);
    		
            boothIds = EntityUtil.getFieldListFromEntityList(booths, "facilityId", false);          	
       
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
    	Map<String, Object> result = ServiceUtil.returnSuccess(); 
    	result.put("boothList", booths);
        result.put("boothIdsList", boothIds);
    	
    	return result;
	}
    
    public static Map<String, Object> createByprodSOorGiftBooth(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String ownerPartyId = null;		
		String address1 = null;
		String address2 = null;
		String contactMechId = null;
		String partyId = null;
		String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
		String facilityId = (String) context.get("boothId");		
		String byProdRouteId = (String) context.get("routeId");
		String categoryTypeEnum = "BYPROD_SO";
		String subscriptionTypeId = "BYPRODUCTS";
		String groupName = (String) context.get("groupName");
		String name = (String) context.get("name");		
		String facilityName = (String) context.get("facilityName");
		String contactNumber = (String) context.get("contactNumber");
		String pinNumber = (String) context.get("pinNumber");
		address1 = (String) context.get("address1");
		address2 = (String) context.get("address2");
		String partyClassificationGroupId = "PM_RC_S";
		
		if(UtilValidate.isEmpty(address2)){
		    address2 = (String) context.get("address1");
		}
		if(productSubscriptionTypeId.equals("GIFT_BYPROD")){
			categoryTypeEnum = "BYPROD_GIFT";
			partyClassificationGroupId = "PM_RC_G";
		}
		if(productSubscriptionTypeId.equals("REPLACEMENT_BYPROD")){
			categoryTypeEnum = "REPLACEMENT_BYPROD";
			partyClassificationGroupId = "PM_RC_G";
		}
		Map<String, Object> resultMap = FastMap.newInstance();
		Map<String, Object> input = FastMap.newInstance();
		groupName = (String) context.get("name");
		
		input.put("userLogin", userLogin);
		input.put("facilityId", facilityId);
		input.put("parentFacilityId", byProdRouteId);
		input.put("byProdRouteId", byProdRouteId);
		input.put("categoryTypeEnum", categoryTypeEnum);
		input.put("subscriptionTypeId", subscriptionTypeId);
		input.put("groupName", groupName);		
		input.put("contactNumber", contactNumber);
		input.put("address1", address1);
		input.put("address2", address2);
		input.put("facilityName", facilityName);
		input.put("city", address2);
		input.put("postalCode", pinNumber);
		GenericValue facility = null;
		try{
			resultMap =  dispatcher.runSync("createBooth", input);
			 if (ServiceUtil.isError(resultMap)) {
				 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	            return resultMap;
	        }
			 String resultFacilityId = (String) resultMap.get("facilityId");
			 facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId" , resultFacilityId), false);
			 Map partyClsCtx = FastMap.newInstance();
			 partyClsCtx.put("userLogin", userLogin);
			 partyClsCtx.put("partyId", facility.getString("ownerPartyId"));
			 partyClsCtx.put("partyClassificationGroupId", partyClassificationGroupId);
			 resultMap =  dispatcher.runSync("createPartyClassification", partyClsCtx);
			 if (ServiceUtil.isError(resultMap)) {
				 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	            return resultMap;
	        }
		}catch (Exception e) {
			// TODO: handle exception
			  Debug.logError(e.toString(), module);
			  return ServiceUtil.returnError(e.toString());
		}		
		 
		return result;
		
    } 
    
    public static Map<String, Object> getBoothOwnerContactInfo(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		Map contactInfo = FastMap.newInstance();
		try{
			
			GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
			if(UtilValidate.isEmpty(facility)){
				Debug.logError("No Facility Found with id :"+facilityId, module);
				return result;
			}
			String partyId = facility.getString("ownerPartyId");
			contactInfo.put("byProdRouteId",facility.getString("byProdRouteId"));
			String ownerName = PartyHelper.getPartyName(delegator, partyId, false);
			contactInfo.put("name",ownerName); 
			Map<String, Object> getTelParams = FastMap.newInstance();
	        	getTelParams.put("partyId", partyId);
	            getTelParams.put("userLogin", userLogin);                    	
	            Map<String, Object> serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	            	 Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                return ServiceUtil.returnSuccess();
	            } 	            
	            if(!UtilValidate.isEmpty(serviceResult.get("contactNumber"))){
	            	contactInfo.put("contactNumber",(String) serviceResult.get("contactNumber"));        	
	            	
	            }
	            serviceResult = dispatcher.runSync("getPartyPostalAddress", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	            	 Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                return ServiceUtil.returnSuccess();
	            } 	            
	            if(!UtilValidate.isEmpty(serviceResult.get("address1"))){
	            	contactInfo.put("address1",(String) serviceResult.get("address1"));        	
	            	
	            }
	            if(!UtilValidate.isEmpty(serviceResult.get("address2"))){
	            	contactInfo.put("address2",(String) serviceResult.get("address2"));      	
	            	
	            }
	            if(!UtilValidate.isEmpty(serviceResult.get("postalCode"))){
	            	contactInfo.put("postalCode",(String) serviceResult.get("postalCode"));      	
	            	
	            }
			
			
		}catch (Exception e) {
			// TODO: handle exception
			 Debug.logError(e.toString(), module);
			 return ServiceUtil.returnError(e.toString());
		}
		result.put("contactInfo", contactInfo);
		return result;
    }	
    public static Map<String, Object> createByProduct(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        String productId = ((String) context.get("productId")).toUpperCase();
        Timestamp pricesEffectiveDate = UtilDateTime.getDayStart((Timestamp)context.get("pricesEffectiveDate"));
        
        Map<String, Object> createProductMap = FastMap.newInstance();
        createProductMap.put("productId", productId);
        createProductMap.put("productTypeId", ((String) context.get("productTypeId")).toUpperCase());
        createProductMap.put("productName", ((String) context.get("productName")).toUpperCase());
        createProductMap.put("internalName", ((String) context.get("internalName")).toUpperCase());
        createProductMap.put("brandName", ((String)context.get("brandName")).toUpperCase());
        createProductMap.put("description", context.get("description"));
        createProductMap.put("quantityUomId", context.get("quantityUomId"));
        createProductMap.put("quantityIncluded", context.get("quantityIncluded"));
        createProductMap.put("piecesIncluded", context.get("piecesIncluded"));
        createProductMap.put("taxable", context.get("taxable"));
        createProductMap.put("chargeShipping", context.get("chargeShipping"));
        createProductMap.put("autoCreateKeywords", context.get("autoCreateKeywords"));
        createProductMap.put("isVirtual", context.get("isVirtual"));
        createProductMap.put("isVariant", context.get("isVariant"));
        createProductMap.put("userLogin", userLogin);
        Map<String, Object> resultMap = null;
        try {
        	resultMap = dispatcher.runSync("createProduct", createProductMap);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (ServiceUtil.isError(resultMap)) {
        	Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
            return resultMap;
        }
        
        /*****  CREATING BYPROD CATEGORY  *****/
        GenericValue productCategoryMember = null;
        productCategoryMember = delegator.makeValue("ProductCategoryMember");
        productCategoryMember.put("productId", productId);
        productCategoryMember.put("productCategoryId", "BYPROD");
        productCategoryMember.put("fromDate", pricesEffectiveDate);
		try {
			productCategoryMember.create();
        } catch (GenericEntityException e) {
        	Debug.logError(e, module);
			return ServiceUtil.returnError("Trouble in creating 'BYPROD' category member for product: "+productId);
        }
        
        /*****  GETTING ALL EXISTING PRICE TYPES  *****/
        List priceTypeCondList = FastList.newInstance();
        priceTypeCondList.add(EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS, "PM_RC"));
		EntityCondition priceTypeCond = EntityCondition.makeCondition(priceTypeCondList,EntityOperator.AND);
		
		List<GenericValue> priceTypes;
		try {
			priceTypes = delegator.findList("PartyClassificationGroup", priceTypeCond , UtilMisc.toSet("partyClassificationGroupId"), null, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Trouble in getting priceTypes");
		}
        
		List priceTypeList = FastList.newInstance();
		priceTypeList = EntityUtil.getFieldListFromEntityList(priceTypes, "partyClassificationGroupId", true);
        
		SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
		String newFormatDate = newFormat.format(pricesEffectiveDate);
		
		/*****  CREATING PRICES FOR ALL EXISTING PRICE TYPES *****/
        for(int i=0; i<priceTypeList.size(); i++){
        	
        	String priceType = (String) priceTypeList.get(i);
        	BigDecimal price = BigDecimal.ZERO;
        	BigDecimal taxAmount = BigDecimal.ZERO;
        	BigDecimal taxPercentage = BigDecimal.ZERO;
        	
        	
        	Map<String, Object> createPriceMap = FastMap.newInstance();
        	createPriceMap.put("productId", productId);
        	createPriceMap.put("priceType", priceType);
        	createPriceMap.put("currencyUomId", "INR");
        	createPriceMap.put("productStoreGroupId", "_NA_");
        	createPriceMap.put("fromDate", newFormatDate);
        	createPriceMap.put("userLogin", userLogin);
            Map<String, Object> priceResultMap = null;
            try {
            	priceResultMap = dispatcher.runSync("createNewPrice", createPriceMap);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        	
            if (ServiceUtil.isError(resultMap)) {
            	Debug.logError(ServiceUtil.getErrorMessage(priceResultMap), module);
                return resultMap;
            }
        }
        result.put("productId", productId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    public static Map<String, Object> updateByProductPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        String productId = (String) context.get("productId");
        Timestamp fromDate = UtilDateTime.getDayStart((Timestamp)context.get("fromDate"));
        Timestamp thruDate = null;
        if(UtilValidate.isNotEmpty(context.get("thruDate"))){
        	thruDate = UtilDateTime.getDayEnd((Timestamp)context.get("thruDate"));
        }
        	
        String uom = "INR";
        if(UtilValidate.isNotEmpty(context.get("currencyUomId"))){
        	uom = (String) context.get("currencyUomId");
        }
        String productStoreGroupId = "_NA_";
        if(UtilValidate.isNotEmpty(context.get("productStoreGroupId"))){
        	productStoreGroupId = (String) context.get("productStoreGroupId");
        }
        
        String priceType = (String) context.get("productPriceTypeId");
        BigDecimal vatPercentage = (BigDecimal)context.get("VAT_SALE_Rate");
        BigDecimal componentPrice = (BigDecimal)context.get("price");
        
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        try{
        	List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, priceType),EntityOperator.OR,
    				EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, "SALE")));
    		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDate));
    		conditionList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, productStoreGroupId));
    		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		List<GenericValue> productPrices = delegator.findList("ProductPrice", cond, null, null, null, false);
    		boolean priceFlag = false;
    		boolean taxFlag = false;
    		for(GenericValue productPrice : productPrices){
    			String purposeId = productPrice.getString("productPricePurposeId");
    			String prodPriceTypeId = productPrice.getString("productPriceTypeId");
    			BigDecimal price = productPrice.getBigDecimal("price");
    			BigDecimal taxPercentage = productPrice.getBigDecimal("taxPercentage");
    			if(prodPriceTypeId.contains("SALE")){
    				if(!(vatPercentage.compareTo(taxPercentage)==0)){
    					taxFlag = true;
    				}
    			}
    			else{
    				if(!(componentPrice.compareTo(price)==0)){
    					priceFlag = true;
    				}
    			}
    		}
    		if(priceFlag || taxFlag){
    			if(fromDate.compareTo(UtilDateTime.getDayStart(nowTimestamp)) == 0){
    				for(GenericValue productPrice : productPrices){
    					BigDecimal taxPercent = productPrice.getBigDecimal("taxPercentage");
    					BigDecimal prodPrice = productPrice.getBigDecimal("price");
    					if(taxFlag && UtilValidate.isEmpty(prodPrice)){
    						productPrice.put("taxPercentage", vatPercentage);
    	    			}
    	    			if(priceFlag && UtilValidate.isEmpty(taxPercent)){
    	    				productPrice.put("price", componentPrice);
    	    			}
    	    			
    	    			productPrice.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
    	    			productPrice.put("lastModifiedDate", UtilDateTime.nowTimestamp());
    	    			productPrice.store();
    	    		}
    			}
    			else{
    				for(GenericValue productPrice : productPrices){
    	    			productPrice.put("thruDate", UtilDateTime.addDaysToTimestamp(UtilDateTime.getDayEnd(nowTimestamp), -1));
    	    			productPrice.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
    	    			productPrice.put("lastModifiedDate", UtilDateTime.nowTimestamp());
    	    			productPrice.store();
    	    		}
    				for(GenericValue createProductPrice : productPrices){
    	    			createProductPrice.put("fromDate", UtilDateTime.getDayStart(nowTimestamp));
    	    			createProductPrice.put("thruDate", null);
    	    			BigDecimal tempTaxPercent = createProductPrice.getBigDecimal("taxPercentage");
    	    			BigDecimal tempPrice = createProductPrice.getBigDecimal("price");
    	    			if(UtilValidate.isNotEmpty(tempTaxPercent)){
    	    				createProductPrice.put("taxPercentage", vatPercentage);
    	    			}
    	    			if(UtilValidate.isNotEmpty(tempPrice)){
    	    				createProductPrice.put("price", componentPrice);
    	    			}
    	    			createProductPrice.put("createdByUserLogin", userLogin.get("userLoginId"));
    	    			createProductPrice.put("createdDate", nowTimestamp);
    	    			createProductPrice.create();
    	    		}
    			}
    		}
        }catch(Exception e){
        	Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        result.put("productId", productId);
        result.put("fromDate", fromDate);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
	public static Map<String, Object> deleteByProductPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();       
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = new HashMap<String, Object>();
	    Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
	    String productId = (String) context.get("productId");
	    Timestamp fromDate = (Timestamp) context.get("fromDate");
	    String priceType = (String) context.get("productPriceTypeId");
	    String productStoreGroupId = (String) context.get("productStoreGroupId");
	    String currencyUomId = (String) context.get("currencyUomId");
		
		if(UtilValidate.isNotEmpty(currencyUomId)){
			currencyUomId = "INR";
		}
		if(UtilValidate.isNotEmpty(productStoreGroupId)){
			productStoreGroupId = "_NA_";
		}
		
		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, priceType),EntityOperator.OR,
				EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, "SALE")));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDate));
		conditionList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, productStoreGroupId));
		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try{
			List<GenericValue> productPrice = delegator.findList("ProductPrice", cond, null, null, null, false);
			if(UtilValidate.isNotEmpty(productPrice)){
				int rows_deleted = delegator.removeAll(productPrice);
			}
		}catch(GenericEntityException e){
			Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
		}
		result.put("productId", productId);
	    result.put("productPriceTypeId", priceType);
	    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
	    return result;
	}


	public static Map<String, Object> getByproductParlours(Delegator delegator) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess(); 
	    	result = getByproductParlours(delegator, null);
	    	return result;
	    }
	    public static Map<String, Object> getByproductParlours(Delegator delegator, Timestamp effectiveDate) {
	    	List<String> parlours= FastList.newInstance();
	    	List<GenericValue> facilities = null;
	    	
	    	if(UtilValidate.isEmpty(effectiveDate)){
	    		effectiveDate = UtilDateTime.nowTimestamp();
	    	}
	    	
	    	try {
	    		List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "PARLOUR"));
				conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null),EntityOperator.OR,
		    			 EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN, effectiveDate)));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		facilities = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
	            parlours = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("parloursList", facilities);
	        result.put("parlourIdsList", parlours);
	
	        return result;
    }
    
    // This will return All boothsList 
    public static Map<String, Object> getAllByproductBooths(Delegator delegator) {
    	Map<String, Object> result = ServiceUtil.returnSuccess(); 
    	result = getAllByproductBooths(delegator, null);
    	return result;
    }    
	public static Map<String, Object> getAllByproductBooths(Delegator delegator, Timestamp effectiveDate){
	    Map<String, Object> result = FastMap.newInstance(); 
	    List<String> boothIds = FastList.newInstance(); 
    	List<GenericValue> booths = null;
    	
    	if(UtilValidate.isEmpty(effectiveDate)){
    		effectiveDate = UtilDateTime.nowTimestamp();
    	}
    	
		try {
			List conditionList= FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "BOOTH"));
			conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL , null));
			conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null),EntityOperator.OR,
		    			 EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN, effectiveDate)));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    		
			booths = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
            boothIds = EntityUtil.getFieldListFromEntityList(booths, "facilityId", false);
	    	
            result.put("boothsList", booths);
	    	result.put("boothsIdsList", boothIds);
		
		} catch (GenericEntityException e) {
	        Debug.logError(e, module);
	    }
		return result;
	}
    

    
    public static Map<String, Object> getPartyFinAccountInfo(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyId");
		Map accountInfo = FastMap.newInstance();
		try{
			
			GenericValue party = delegator.findOne("Party",UtilMisc.toMap("partyId",partyId),false);
			if(UtilValidate.isEmpty(party)){
				Debug.logError("No Party Found with id :"+partyId, module);
				return ServiceUtil.returnError("No Party found with Id:"+party.getString("partyId"));
			}
			List<GenericValue> partyFinAccounts = null;
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
			conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    		partyFinAccounts = delegator.findList("FinAccount", condition, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);
			if(UtilValidate.isEmpty(partyFinAccounts)){
				Debug.logError("No Financial Accounts available for the Party:"+partyId, module);
				return ServiceUtil.returnSuccess();
			}
			else{
				GenericValue finAccountDetail = EntityUtil.getFirst(partyFinAccounts);
				if(UtilValidate.isNotEmpty(finAccountDetail.getString("finAccountName"))){
					accountInfo.put("finAccountName", finAccountDetail.getString("finAccountName"));        	
	            }
				else{
					accountInfo.put("finAccountName", "");
				}
				if(UtilValidate.isNotEmpty(finAccountDetail.getString("finAccountBranch"))){
					accountInfo.put("finAccountBranch", finAccountDetail.getString("finAccountBranch"));        	
	            }
				else{
					accountInfo.put("finAccountBranch", "");
				}
				accountInfo.put("finAccountId", finAccountDetail.getString("finAccountId"));
				
			}
		}catch (Exception e) {
			// TODO: handle exception
			 Debug.logError(e.toString(), module);
			 return ServiceUtil.returnError(e.toString());
		}
		result.put("accountInfo", accountInfo);
		return result;
    }
    
	public static Map<String, Object> receiveInventoryIntoAllParlours(DispatchContext dctx, Map<String, Object> context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");	
		
		Timestamp receiveTime = UtilDateTime.nowTimestamp();
		String ownerPartyId = "Company";
	    String inventoryItemTypeId = "NON_SERIAL_INV_ITEM";
	    BigDecimal quantityRejected = BigDecimal.ZERO;
	    BigDecimal requestedQuantity = new BigDecimal(100000);
	    
	    List parlourIdsList = FastList.newInstance();
	
		String parlourId = (String) context.get("parlourId");	
		
		List existingParloursList = (List) getByproductParlours(delegator).get("parlourIdsList");
		
		HashSet existingParloursSet = new HashSet(existingParloursList);
		
		if(UtilValidate.isNotEmpty(parlourId)){
			if(!existingParloursSet.contains(parlourId) ){
				return ServiceUtil.returnError("Invalid Parlour Id");
			}
			parlourIdsList.add(parlourId);
		}
		else{
			parlourIdsList = existingParloursList;
		}
		
		String productStoreId = (String) getByprodFactoryStore(delegator).get("factoryStoreId");
		List<GenericValue> productsList = (List<GenericValue>) ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productList");
		
		List productIdsList = EntityUtil.getFieldListFromEntityList(productsList, "productId", false);
		
		for(int i=0; i<parlourIdsList.size(); i++){
			
			for(int j=0; j<productIdsList.size(); j++){
				
				 Map<String, Object> receiveInventoryResult;
	                Map<String, Object> receiveInventoryContext = FastMap.newInstance();
	                receiveInventoryContext.put("userLogin", userLogin);   
	                receiveInventoryContext.put("ownerPartyId", ownerPartyId);                    
	                receiveInventoryContext.put("productId", productIdsList.get(j));
	                receiveInventoryContext.put("inventoryItemTypeId", inventoryItemTypeId); 
	                receiveInventoryContext.put("facilityId", parlourIdsList.get(i));
	                receiveInventoryContext.put("datetimeReceived", receiveTime);
	                receiveInventoryContext.put("quantityAccepted", requestedQuantity);
	                receiveInventoryContext.put("unitCost", 1);
	                receiveInventoryContext.put("quantityRejected", quantityRejected);
	              
	                try {
						receiveInventoryResult = dispatcher.runSync("receiveInventoryProduct", receiveInventoryContext);
						if (ServiceUtil.isError(receiveInventoryResult)) {
							Debug.logError("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
							return ServiceUtil.returnError(null, null, null, receiveInventoryResult);
			            }
					} catch (GenericServiceException e) {
						Debug.logError(e, module);
					}
			}
			Debug.logImportant("Completed receiving Inventory for" + parlourIdsList.get(i) +"--> "+ i , module);
		}
		
		Map result = ServiceUtil.returnSuccess("Inventory Received successfully");
		return result;
	}
	
	public static Map<String, Object> receiveOpeningBalanceForParlours(DispatchContext dctx, Map<String, Object> context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");	
		String ownerPartyId = "Company";
	    String inventoryItemTypeId = "NON_SERIAL_INV_ITEM";
	    BigDecimal quantityRejected = BigDecimal.ZERO;
	    Boolean isUpdateOB = (Boolean) context.get("isUpdateOB");
	    if(UtilValidate.isEmpty(isUpdateOB)){
	    	isUpdateOB = false;
	    }
	    
	    Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	    Timestamp receiveTime = UtilDateTime.getDayStart(nowTimestamp, TimeZone.getDefault(), locale);
	    Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(nowTimestamp, -1), TimeZone.getDefault(), locale);
	    
		List<GenericValue> inventoryOpeningBalance;
		try {
			inventoryOpeningBalance = delegator.findList("InvOpeningBalance", null , null, null, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		Map facilityOpeningBalance = FastMap.newInstance();
		Map facilityTimeMap = FastMap.newInstance();
		
		for (int i = 0; i < inventoryOpeningBalance.size(); i++) {
			
			String facilityId = (String) inventoryOpeningBalance.get(i).get("facilityId");
			if(UtilValidate.isNotEmpty(inventoryOpeningBalance.get(i).get("effectiveDate"))){
				receiveTime = UtilDateTime.getDayStart(((Timestamp)(inventoryOpeningBalance.get(i).get("effectiveDate"))), TimeZone.getDefault(), locale);
			}
			if(UtilValidate.isEmpty(facilityTimeMap.get(facilityId))){
				facilityTimeMap.put(facilityId, receiveTime);
			}
			
			Map prodQtyMap = FastMap.newInstance();
			prodQtyMap.put("facilityId", facilityId);
			prodQtyMap.put("productId", inventoryOpeningBalance.get(i).get("productId"));
			prodQtyMap.put("qty", inventoryOpeningBalance.get(i).get("quantity"));
			
			Map tempOpeningBalanceMap = FastMap.newInstance();
			tempOpeningBalanceMap.putAll(prodQtyMap);
			
			if(UtilValidate.isEmpty(facilityOpeningBalance.get(facilityId))){
				
				List facilityOpeningBalanceList = FastList.newInstance();
				facilityOpeningBalanceList.add(tempOpeningBalanceMap);
				
				facilityOpeningBalance.put(facilityId, facilityOpeningBalanceList);
			}
			else{
				List updateList = FastList.newInstance();
				
				updateList = (List) facilityOpeningBalance.get(facilityId);
				updateList.add(tempOpeningBalanceMap);
				
				List tempUpdateList = FastList.newInstance();
				tempUpdateList.addAll(updateList);
				
				facilityOpeningBalance.put(facilityId, tempUpdateList);
			}
			
		}
		Iterator OBIter = facilityOpeningBalance.entrySet().iterator();
		while (OBIter.hasNext()) {
			Map.Entry invItemEntry = (Entry) OBIter.next();
			String facilityId = (String) invItemEntry.getKey();
			List prodList = (List) invItemEntry.getValue();
			
			if(UtilValidate.isNotEmpty(facilityTimeMap.get("facilityId"))){
				receiveTime = (Timestamp) facilityTimeMap.get("facilityId");
				previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(receiveTime, -1), TimeZone.getDefault(), locale);
			}
			
			Map inventoryTotalsMap = FastMap.newInstance();
			
			try {
				Map facilityInventoryMap = dispatcher.runSync("getInventory", UtilMisc.<String, Object>toMap("facilityId", facilityId, "effectiveDate", previousDayEnd,"userLogin", userLogin));
				inventoryTotalsMap = (Map) facilityInventoryMap.get("facilityInventoryMap");
			} catch (GenericServiceException e1) {
				Debug.logError(e1, module);
	            return ServiceUtil.returnError(e1.getMessage());
			}
			
			for (int i = 0; i < prodList.size(); i++) {
				
				String productId =  (String) ((Map) prodList.get(i)).get("productId");
				BigDecimal quantity = (BigDecimal) ((Map) prodList.get(i)).get("qty");
				
				BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
				
				if(UtilValidate.isNotEmpty(inventoryTotalsMap.get(facilityId))){
					Map prodMap  = (Map) inventoryTotalsMap.get(facilityId);
					if(UtilValidate.isNotEmpty(prodMap.get(productId))){
						Map InventoryMap = (Map) prodMap.get(productId);
						quantityOnHandTotal = (BigDecimal) InventoryMap.get("quantityOnHandTotal");
					}
				}
				
				BigDecimal receiveQty = quantity.subtract(quantityOnHandTotal);
				
				if(isUpdateOB){
					try {
						Map populateSaleQty = dispatcher.runSync("populateInventorySummary", UtilMisc.<String, Object>toMap("facilityId", facilityId, "effectiveDate", UtilDateTime.addDaysToTimestamp(receiveTime, -1), "productId", productId, "isUpdateOB", isUpdateOB, "updatedOBQty",quantity, "userLogin", userLogin));
					} catch (GenericServiceException e1) {
						Debug.logError(e1, module);
				        return ServiceUtil.returnError(e1.toString());
					}
				}
				else{
					GenericValue inventorySummary = null;
	    			inventorySummary = delegator.makeValue("InventorySummary");
	    			inventorySummary.put("saleDate", UtilDateTime.addDaysToTimestamp(receiveTime, -1) );
	    			inventorySummary.put("facilityId", facilityId);
	    			inventorySummary.put("isUpdated", "Y");
	    			inventorySummary.put("productId", productId); 
	    			inventorySummary.put("openingBalance", quantity);
	    			inventorySummary.put("receipts", BigDecimal.ZERO);
	    			inventorySummary.put("sales", BigDecimal.ZERO); 
	    			inventorySummary.put("xferIn", BigDecimal.ZERO);
	    			inventorySummary.put("xferOut", BigDecimal.ZERO);
	    			inventorySummary.put("adjustments", BigDecimal.ZERO); 
	    			inventorySummary.put("closingBalance", quantity);
	    			try {
						inventorySummary.create();
					} catch (GenericEntityException e) {
						Debug.logError(e, e.toString(), module);
				        return ServiceUtil.returnError(e.toString());
					} 
				}
				
				if(receiveQty.compareTo(BigDecimal.ZERO) == 0){
					continue;
				}
				
			    Map<String, Object> receiveInventoryResult;
                Map<String, Object> receiveInventoryContext = FastMap.newInstance();
                receiveInventoryContext.put("userLogin", userLogin);   
                receiveInventoryContext.put("ownerPartyId", ownerPartyId);                    
                receiveInventoryContext.put("productId", productId);
                receiveInventoryContext.put("inventoryItemTypeId", inventoryItemTypeId); 
                receiveInventoryContext.put("facilityId", facilityId);
                receiveInventoryContext.put("datetimeReceived", receiveTime);
                receiveInventoryContext.put("quantityAccepted", receiveQty);
                receiveInventoryContext.put("unitCost", 1);
                receiveInventoryContext.put("quantityRejected", quantityRejected);
                try {
					receiveInventoryResult = dispatcher.runSync("receiveInventoryProduct", receiveInventoryContext);
					if (ServiceUtil.isError(receiveInventoryResult)) {
						Debug.logError("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
						return ServiceUtil.returnError(null, null, null, receiveInventoryResult);
		            }
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
			        return ServiceUtil.returnError(e.toString());

			    }
    			
			}
			
		}	
		
		Map result = ServiceUtil.returnSuccess("Inventory Successfully Updated");
		return result;
	}

	public static String processSupplDeleverySchdule(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  String boothId = (String) request.getParameter("boothId");	 
	  	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
	  	  String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
	  	  String subscriptionTypeId = "BYPRODUCTS";
	  	  String shipmentTypeId = "BYPRODUCTS_SUPPL"; 
	  	  // String salesChannel = "BYPROD_SALES_CHANNEL";
	  	  String productId = null;
	  	  String quantityStr = null;
	  	  Timestamp effectiveDate=null;
	  	  Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
	  	  BigDecimal quantity = BigDecimal.ZERO;
	  	  List<GenericValue> subscriptionList=FastList.newInstance();
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	  	  GenericValue subscription = null;
	  	  GenericValue facility = null;
	  	  List custTimePeriodList = FastList.newInstance();  	 
	  	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
	  		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
	  		  try {
	  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
	  		  } catch (ParseException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	              // effectiveDate = UtilDateTime.nowTimestamp();
	  		  } catch (NullPointerException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	               //effectiveDate = UtilDateTime.nowTimestamp();
	  		  }
	  	  }
	  	  if (boothId == "") {
	  			request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
	  			return "error";
	  		}
	      
	      // Get the parameters as a MAP, remove the productId and quantity params.
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
	  		  return "success";
	  	  }
	  	  try{
	  		  facility=delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), false);
	  		  if(UtilValidate.isEmpty(facility)){
	  			  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
	  			  return "error";
	  		  }
	  			  
	  	  }catch (GenericEntityException e) {
	  		  Debug.logError(e, "Booth does not exist", module);
	  		  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+ boothId +"'"+" does not exist");
	  		  return "error";
	  	  }
	  	  try {
	  		  List conditionList =FastList.newInstance();
	  		  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));			 
	          conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
	          
			EntityCondition subCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
	  		  subscriptionList=delegator.findList("SubscriptionAndFacility", subCond, null, null, null, false);
	  		  subscriptionList = EntityUtil.filterByDate(subscriptionList ,effectiveDate);
	  		  if(UtilValidate.isEmpty(subscriptionList)){
	  			  request.setAttribute("_ERROR_MESSAGE_", "Booth subscription does not exist");
	  			  return "error";     		
	  		  }
	  		  subscription = EntityUtil.getFirst(subscriptionList);
	  	  }  catch (GenericEntityException e) {
	  		  Debug.logError(e, "Problem getting Booth subscription", module);
	  		  request.setAttribute("_ERROR_MESSAGE_", "Problem getting Booth subscription");
	  		  return "error";
	  	  }
	  	// attempt to create a Shipment entity       
	        String shipmentId = "";
	        Timestamp dayBegin = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
	        Timestamp dayEnd = UtilDateTime.getDayEnd(effectiveDate, TimeZone.getDefault(), locale);
	        List conditionList = FastList.newInstance();
	        
	     // lets get the shipment if already exits else create new one
		  	List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, UtilDateTime.getDayStart(effectiveDate), UtilDateTime.getDayEnd(effectiveDate), shipmentTypeId);
	        if(UtilValidate.isEmpty(shipmentList)){
	        	 GenericValue newEntity = delegator.makeValue("Shipment");        	 
	             newEntity.set("estimatedShipDate", effectiveDate);
	             newEntity.set("shipmentTypeId", shipmentTypeId);
	             newEntity.set("statusId", "GENERATED");
	             newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	             newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	             try {
	                 delegator.createSetNextSeqId(newEntity);            
	                shipmentId = (String) newEntity.get("shipmentId");
	                
	             } catch (GenericEntityException e) {
	                 Debug.logError(e, module);
	                 request.setAttribute("_ERROR_MESSAGE_", "un able to create shipment id.");
	                 
	     			return "error";                 
	             }  
	        	
	        }else{
	        	shipmentId = (String)shipmentList.get(0);
	        }
	        request.setAttribute("shipmentId",shipmentId);
	        
	        
	        
	  	  List<GenericValue> subscriptionProductsList =FastList.newInstance();
	  	  
	  	  for (int i = 0; i < rowCount; i++) {
	  		  GenericValue subscriptionFacilityProduct = delegator.makeValue("SubscriptionFacilityAndSubscriptionProduct");
	  		  subscriptionFacilityProduct.set("facilityId", subscription.get("facilityId"));
	  		  subscriptionFacilityProduct.set("subscriptionId", subscription.get("subscriptionId"));
	  		  subscriptionFacilityProduct.set("categoryTypeEnum", subscription.get("categoryTypeEnum"));
	  		  subscriptionFacilityProduct.set("ownerPartyId", subscription.get("ownerPartyId"));
	  		  subscriptionFacilityProduct.set("productSubscriptionTypeId", productSubscriptionTypeId);
	  		  
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
	  		  try {
	  			  quantity = new BigDecimal(quantityStr);
	  		  } catch (Exception e) {
	  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
	  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
	  			  return "error";
	  		  } 
	  		  
	  		  productQtyMap.put("productId", productId);
	  		  productQtyMap.put("quantity", quantity);	  		
	  		 // productQtyList.add(productQtyMap);
	  		subscriptionFacilityProduct.set("productId", productId);
	  		subscriptionFacilityProduct.set("quantity", quantity);
	  		subscriptionProductsList.add(subscriptionFacilityProduct);
	  	  }//end row count for loop
	  	  
	  	if( UtilValidate.isEmpty(subscriptionProductsList)){
	  		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	  		  return "success";
	  	}
	  	 Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin",userLogin);	  	
	  	 processChangeIndentHelperCtx.put("shipmentId", shipmentId);
	  	 processChangeIndentHelperCtx.put("estimatedDeliveryDate", effectiveDate);
	  	 processChangeIndentHelperCtx.put("subscriptionProductsList", subscriptionProductsList);	  	 
	  	 try{
	  		 if((subscription.get("categoryTypeEnum")).equals("PARLOUR")){
	  			result = dispatcher.runSync("receiveParlorInventory",processChangeIndentHelperCtx);
	  			 
	  		 }else{
	  			result = dispatcher.runSync("createSalesOrderSubscriptionProductType",processChangeIndentHelperCtx);
	  		 }	  		 
			 if (ServiceUtil.isError(result)) {
				String errMsg =  ServiceUtil.getErrorMessage(result);
				Debug.logError(errMsg , module);
				request.setAttribute("_ERROR_MESSAGE_",errMsg);
				return "error";
			 }
			 
	  	 }catch (Exception e) {
	  			  Debug.logError(e, "Problem creating entry in Supplementary Indent " + boothId, module);     
	  			  request.setAttribute("_ERROR_MESSAGE_", "Problem creating entry in Supplementary Indent  For :" + boothId);
	  			  return "error";			  
	  		  }
	  	  request.setAttribute("_EVENT_MESSAGE_", "Successfully made entry from party: "+boothId);	  	 
	  	  return "success";     
	}	
	public static Map<String, Object> calculateByProductsPrice(Delegator delegator, LocalDispatcher dispatcher, Map<String, ? extends Object> context) {
		    Map<String, Object> result = FastMap.newInstance();
		   
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    String facilityId = (String) context.get("facilityId");
		    String productId = (String) context.get("productId"); 
		    String partyId = (String) context.get("partyId");            
		    String productStoreId = (String) context.get("productStoreId");
		    String shipmentTypeId = (String) context.get("shipmentTypeId");
		    Timestamp priceDate = (Timestamp) context.get("priceDate");
		    String productStoreGroupId = "_NA_";
		    String productPriceTypeId = (String) context.get("productPriceTypeId");
		    String facilityCategory = (String) context.get("facilityCategory");
		    GenericValue product;
		    GenericValue facility;
			try {
				product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
				if(UtilValidate.isEmpty(facilityCategory)){
					facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);
					facilityCategory = facility.getString("categoryTypeEnum");
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, e.toString(), module);
		        return ServiceUtil.returnError(e.toString());
			}
			
			GenericValue productStore;
			try {
				productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
			} catch (GenericEntityException e) {
				Debug.logError(e, e.toString(), module);
		        return ServiceUtil.returnError(e.toString());
			}
			
		    if (UtilValidate.isEmpty(priceDate)) {
		    	priceDate = UtilDateTime.nowTimestamp();
		    }
		    List conditionList = FastList.newInstance();
		    
		    /*if((UtilValidate.isEmpty(partyId)) && (UtilValidate.isEmpty(facilityId)) && (UtilValidate.isEmpty(productPriceTypeId))){
				Debug.logWarning("No 'partyId' or 'facilityId' Found", module);
				return result;
			}*/
		    if(UtilValidate.isEmpty(productPriceTypeId)){
		    	 // lets take DEFAULT_PRICE as default priceType any special priceType for facility Or party, this will override with the special type
		    	  productPriceTypeId = "DEFAULT_PRICE";
		    	  
		         /*if ((UtilValidate.isNotEmpty(facilityId))) {
		 			try {
		 				facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
		 				if(UtilValidate.isEmpty(facility)){
		 					Debug.logError("No Facility Found with id :"+facilityId, module);
		 					return ServiceUtil.returnError("No Facility Found with id :"+facilityId);
		 				}
		 				if( (UtilValidate.isNotEmpty(facility.get("categoryTypeEnum"))) && (facility.get("categoryTypeEnum").equals("PARLOUR"))){
		 					productPriceTypeId = "PM_RC_P_PRICE";
		 				}		 				
		 				partyId = facility.getString("ownerPartyId");
		 				facilityCategory = facility.getString("categoryTypeEnum");
		 				
		 			 }catch (GenericEntityException e) {
		 				Debug.logError("No facility found for given facilityId:"+ facilityId, module);
		     			return ServiceUtil.returnError("No partyRole found for given partyId");
		 			 }
		 			
		         }*/
		         
		         /*if(UtilValidate.isNotEmpty(partyId)){
		        	 conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		     		 conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.LIKE, "PM_RC_%"));
		     		 EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		     		
		     		 List<GenericValue> partyClassificationList = null;
		     		 try{
		     			 partyClassificationList = delegator.findList("PartyClassification", condition, null, null, null, false);
		     		 }catch(GenericEntityException e){
		     			Debug.logError("No partyRole found for given partyId:"+ partyId, module);
		     			return ServiceUtil.returnError("No partyRole found for given partyId");
		     		 }
		     		 if (UtilValidate.isNotEmpty(partyClassificationList)) {
		     			 GenericValue partyClassification = partyClassificationList.get(0);
		     			 productPriceTypeId = (String) partyClassification.get("partyClassificationGroupId");
		     		 }
		          }*/
		    	
		    }
		    if (productPriceTypeId.contains("_PRICE")) {
		    	String[] prodPriceSplit = productPriceTypeId.split("_PRICE");
		    	productPriceTypeId = prodPriceSplit[0];
		    }
		    
		    List<GenericValue> applicableTaxTypes = null;
			try {
		    	applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive ProductPriceType ", module);
				return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
			}
			List applicableTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);
			
			List<GenericValue> prodPriceType = null;
		    
		    conditionList.clear();
		    conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, productPriceTypeId+"%"));
			conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, applicableTaxTypeList));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, priceDate)));
			
			EntityCondition priceCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		    
		    try {
		    	prodPriceType = delegator.findList("ProductPriceAndType", priceCondition, null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive InventoryItem ", module);
				return ServiceUtil.returnError("Failed to retrive InventoryItem " + e);
			}
			String currencyDefaultUomId = (String) context.get("currencyUomId");
		    if (UtilValidate.isEmpty(currencyDefaultUomId)) {
		        currencyDefaultUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "INR");
		    }
		
		    String productPricePurposeId = (String) context.get("productPricePurposeId");
		    if (UtilValidate.isEmpty(productPricePurposeId)) {
		        productPricePurposeId = "COMPONENT_PRICE";
		    }       
		     
		    List<EntityCondition> productPriceEcList = FastList.newInstance();
		    productPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		    productPriceEcList.add(EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, productPricePurposeId));
		    productPriceEcList.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyDefaultUomId));
		    productPriceEcList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, productStoreGroupId));
		    productPriceEcList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, priceDate)));
			
		    EntityCondition productPriceEc = EntityCondition.makeCondition(productPriceEcList, EntityOperator.AND);
		    // for prices, get all ProductPrice entities for this productId and currencyUomId
		    List<GenericValue> productPrices = null;
		    try {
		        productPrices = delegator.findList("ProductPrice", productPriceEc, null, UtilMisc.toList("-fromDate"), null, true);
		    } catch (GenericEntityException e) {
		        Debug.logError(e, "An error occurred while getting the product prices", module);
		    }
		    productPrices = EntityUtil.filterByDate(productPrices, priceDate);
			List<GenericValue> prodPrices = EntityUtil.filterByCondition(productPrices, EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, productPriceTypeId+"_%"));
		    if (UtilValidate.isEmpty(prodPrices)) {
		    	return ServiceUtil.returnError("Missing price for '" + productId + "' [" + productPriceTypeId + "]");            	
		    }
		    
		    GenericValue resultPrice;
		    
			resultPrice = EntityUtil.getFirst(prodPrices);
			if (prodPrices != null && prodPrices.size() > 1) {
				if (Debug.infoOn()) Debug.logInfo("There is more than one price with the currencyUomId " + currencyDefaultUomId + " and productId " + productId + ", using the latest found with price: " + resultPrice.getBigDecimal("price"), module);
			}
			
			BigDecimal discountAmount = BigDecimal.ZERO;
		    // lets look for party specific discount rates if any and adjust the discount
		    try {
	    		//GenericValue product = resultPrice.getRelatedOne("Product");
	    		BigDecimal quantityIncluded = product.getBigDecimal("quantityIncluded");        	
	    		Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin,"partyId", partyId);
	    		String rateTypeId;
	    		inputRateAmt.put("rateCurrencyUomId", currencyDefaultUomId);    		
	        	if (UtilValidate.isEmpty(facilityCategory)) {        		
	        		rateTypeId = "VENDOR_DEDUCTION";
	        			
	        	}else {
	        		rateTypeId = facilityCategory + "_MRGN";
	        	}
	        		inputRateAmt.put("rateCurrencyUomId", currencyDefaultUomId);  
	        		inputRateAmt.put("rateTypeId", rateTypeId);
	        		inputRateAmt.put("periodTypeId", "RATE_HOUR");
	        		inputRateAmt.put("fromDate", priceDate);
	        		inputRateAmt.put("productId", productId);
	        		Map<String, Object> serviceResults = dispatcher.runSync("getPartyDiscountAmount", inputRateAmt);
	        		
	        		if (ServiceUtil.isError(serviceResults)) {
	        			Debug.logError( "Unable to determine discount for [" + partyId +"]========="+facilityCategory, module);
	        			//return ServiceUtil.returnError("Unable to determine discount for " + facilityCategory, null, null, serviceResults);
	        		}else if(UtilValidate.isNotEmpty(serviceResults.get("rateAmount"))){
	        				discountAmount = (BigDecimal)serviceResults.get("rateAmount");
	        			      			
	        		}
	        		Debug.logInfo( "PartyId ==========["+partyId+"]=========discountAmount   :"+discountAmount, module);
	        		// since the discounts are per litre, adjust proportionally
	        		//discountAmount = discountAmount.multiply(quantityIncluded);
	        	/*
	        	// Sometimes Vendors are also given discounts i.e. deduction at source. Check for 
	        	// VENDOR_DEDUCTION rate type (NOTE: VENDOR_DEDUCTION will override any other eligible
	        	// discounts from above)
	        	if (facilityCategory.equals("VENDOR")) {
					rateTypeId = "VENDOR_DEDUCTION"; 
					inputRateAmt.put("rateTypeId", rateTypeId);
					inputRateAmt.put("fromDate", priceDate);
					inputRateAmt.put("productId", productId);
	        		Map<String, Object> serviceResults = dispatcher.runSync("getRateAmount", inputRateAmt);
	        		if (ServiceUtil.isError(serviceResults)) {
	        			Debug.logError( "Unable to determine deduction for [" + partyId +"]========="+facilityCategory, module);
	        			return ServiceUtil.returnError("Unable to determine deduction for " + facilityCategory, null, null, serviceResults);
	        		}     
	        		discountAmount = (BigDecimal)serviceResults.get("rateAmount");
	        		// since the discounts are per litre, adjust proportionally
	        		discountAmount = discountAmount.multiply(quantityIncluded);				
	        	}*/
	        }catch (GenericServiceException e) {
    			Debug.logError(e, "Unable to get margin/discount: " + e.getMessage(), module);
    	        return ServiceUtil.returnError("Unable to get margin/discount: " + e.getMessage());
	        }
	        BigDecimal basicPrice = resultPrice.getBigDecimal("price").subtract(discountAmount);
			List<GenericValue> taxList = TaxAuthorityServices.getTaxAdjustmentByType(delegator, product, productStore, null, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, null, prodPriceType);
			List taxDetailList = FastList.newInstance();
			BigDecimal totalExciseDuty = BigDecimal.ZERO;
			BigDecimal totalTaxAmt = BigDecimal.ZERO;
			
			for (GenericValue taxItem : taxList) {
				
				String taxType = (String) taxItem.get("orderAdjustmentTypeId");
				BigDecimal amount = BigDecimal.ZERO;
				BigDecimal percentage = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(taxItem.get("amount"))){
					amount = (BigDecimal) taxItem.get("amount");
				}
				if(UtilValidate.isNotEmpty(taxItem.get("sourcePercentage")) && amount.compareTo(BigDecimal.ZERO)== 0){
					percentage = (BigDecimal) taxItem.get("sourcePercentage");
					if(UtilValidate.isNotEmpty(percentage) && UtilValidate.isNotEmpty(basicPrice)){
						amount = (basicPrice.multiply(percentage)).divide(new BigDecimal(100));
					}
				}
				
				if(!taxType.equals("VAT_SALE")){
					totalExciseDuty = totalExciseDuty.add(amount);
				}
				
				totalTaxAmt = totalTaxAmt.add(amount);
				
				Map taxDetailMap = FastMap.newInstance();
				
				taxDetailMap.put("taxType", taxType);
				taxDetailMap.put("amount", amount);
				taxDetailMap.put("percentage", percentage);
			
				Map tempDetailMap = FastMap.newInstance();
				tempDetailMap.putAll(taxDetailMap);
				
				taxDetailList.add(tempDetailMap);
			}
		    
		    BigDecimal price = basicPrice.add(totalExciseDuty);
		    BigDecimal totalPrice = basicPrice.add(totalTaxAmt);
		    
		    result.put("basicPrice", basicPrice);
		    result.put("price", price);
		    result.put("totalPrice", totalPrice);
		    result.put("taxList", taxDetailList);
		    return result;
	}
	
	public static Map<String, Object> getPartyDiscountAmount(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.nowTimestamp();
	    }
		
		Timestamp dayStart = UtilDateTime.getDayStart(fromDate, TimeZone.getDefault(), locale);
		
		String partyId = (String) context.get("partyId");
		String productId = (String) context.get("productId");
		String periodTypeId = (String) context.get("periodTypeId");
		String rateTypeId = (String) context.get("rateTypeId");
		String rateCurrencyUomId = (String) context.get("rateCurrencyUomId");
		GenericValue rateAmountEntry =null;
		
		List<GenericValue> amountList = FastList.newInstance();
		BigDecimal rateAmount = BigDecimal.ZERO;
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
			conditionList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
			conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List<GenericValue> allParamRateAmount = delegator.findList("RateAmount", condition , null, null, null, false);
			amountList = EntityUtil.filterByDate(allParamRateAmount, dayStart);
			if(UtilValidate.isEmpty(amountList)){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "_NA_"));
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
				conditionList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
				conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
				EntityCondition partyCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				List<GenericValue> partyRateAmount = delegator.findList("RateAmount", partyCond, null, null, null, false);
				amountList = EntityUtil.filterByDate(partyRateAmount, dayStart);
				if(UtilValidate.isEmpty(amountList)){
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "_NA_"));
					conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
					conditionList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
					conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
					EntityCondition productCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					List<GenericValue> prodcuRateAmount = delegator.findList("RateAmount", productCond, null, null, null, false);
					amountList = EntityUtil.filterByDate(prodcuRateAmount, dayStart);
					if(UtilValidate.isEmpty(amountList)){
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "_NA_"));
						conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "_NA_"));
						conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
						conditionList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
						conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
						EntityCondition rateCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
						List<GenericValue> rateTypeRateAmount = delegator.findList("RateAmount", rateCond, null, null, null, true);
						amountList = EntityUtil.filterByDate(rateTypeRateAmount, dayStart);
					}
				}
			}
			
			if(UtilValidate.isNotEmpty(amountList)){
				rateAmountEntry = EntityUtil.getFirst(amountList);
				rateAmount = rateAmountEntry.getBigDecimal("rateAmount");
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("rateAmount", rateAmount);
		result.put("rateAmountEntry", rateAmountEntry);
		return result;
	}
	
	
	public static Map<String, Object> populateInventorySummary(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		Boolean isUpdateOB = (Boolean) context.get("isUpdateOB");
		BigDecimal updatedOBQty = (BigDecimal) context.get("updatedOBQty");
		BigDecimal adjustments = (BigDecimal) context.get("adjustments");
		
		if (UtilValidate.isEmpty(effectiveDate)) {
			effectiveDate = UtilDateTime.nowTimestamp();
	    }
		Timestamp latestTimeStamp =  UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), 1);
		
		int totalDays=UtilDateTime.getIntervalInDays(effectiveDate,latestTimeStamp);
		
		if(totalDays < 0){
			return ServiceUtil.returnError("Process Not Allowed for this Day");
		}
		
		Timestamp dayStart = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
		Timestamp dayEnd = UtilDateTime.getDayEnd(effectiveDate, TimeZone.getDefault(), locale);
		
		String facilityId = (String) context.get("facilityId");
		String productId = (String) context.get("productId");
		
		BigDecimal receivedQty = (BigDecimal) context.get("receivedQty");
		BigDecimal saleQty = (BigDecimal) context.get("saleQty");
		BigDecimal xferIn = (BigDecimal) context.get("xferIn");
		BigDecimal xferOut = (BigDecimal) context.get("xferOut");
		if(UtilValidate.isEmpty(receivedQty)){
			receivedQty = BigDecimal.ZERO;
		}
		if(UtilValidate.isEmpty(saleQty)){
			saleQty = BigDecimal.ZERO;
		}
		if(UtilValidate.isEmpty(xferIn)){
			xferIn = BigDecimal.ZERO;
		}
		if(UtilValidate.isEmpty(xferOut)){
			xferOut = BigDecimal.ZERO;
		}
		if(UtilValidate.isEmpty(adjustments)){
			adjustments = BigDecimal.ZERO;
		}
		BigDecimal openingBalance = BigDecimal.ZERO;
	
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		conditionList.add(EntityCondition.makeCondition("saleDate", EntityOperator.LESS_THAN, dayStart));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		List<String> orderBy = UtilMisc.toList("-saleDate");
		
		List<GenericValue> pastInventorySummary;
		try {
			pastInventorySummary = delegator.findList("InventorySummary", condition , null, orderBy, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error fetching details for InventoryItem with productId " + productId + "in "+facilityId );
		}
		
		GenericValue lastestInventorySummary = null;
		if(UtilValidate.isNotEmpty(isUpdateOB)){
			openingBalance = updatedOBQty;
		}
		else{
			
			if(UtilValidate.isNotEmpty(pastInventorySummary)){
				lastestInventorySummary = pastInventorySummary.get(0);
				openingBalance = (BigDecimal) lastestInventorySummary.get("closingBalance");
			}
			else{
				
				Map facilityInventoryMap = FastMap.newInstance();
				try {
					facilityInventoryMap = dispatcher.runSync("getInventory", UtilMisc.<String, Object>toMap("facilityId", facilityId, "productId", productId, "effectiveDate", UtilDateTime.addDaysToTimestamp(dayStart, -1), "userLogin", userLogin));
					Map inventoryTotalsMap = (Map) facilityInventoryMap.get("facilityInventoryMap");
				} catch (GenericServiceException e1) {
					Debug.logError(e1, module);
		            return ServiceUtil.returnError(e1.getMessage());
				}
				Map inventoryMap = (Map) ((Map) ((Map) facilityInventoryMap.get("facilityInventoryMap")).get(facilityId)).get(productId);
				BigDecimal quantityOnHand =BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(inventoryMap.get("quantityOnHandTotal"))){
					quantityOnHand = (BigDecimal) inventoryMap.get("quantityOnHandTotal");
				}			
				
				openingBalance = BigDecimal.ZERO;
				BigDecimal closingBalance = quantityOnHand;
				lastestInventorySummary = delegator.makeValue("InventorySummary");
				lastestInventorySummary.put("saleDate", UtilDateTime.addDaysToTimestamp(dayStart, -1) );
				lastestInventorySummary.put("facilityId", facilityId);
				lastestInventorySummary.put("productId", productId); 
				lastestInventorySummary.put("openingBalance", openingBalance);
				lastestInventorySummary.put("receipts", BigDecimal.ZERO);
				lastestInventorySummary.put("sales", BigDecimal.ZERO);
				lastestInventorySummary.put("adjustments", BigDecimal.ZERO);
				lastestInventorySummary.put("closingBalance", closingBalance);
				try {
					lastestInventorySummary.create();
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.toString());
				} 
				openingBalance = closingBalance;
			}
			
		}
		
		for(int i = 0; i < (totalDays+1); i++){
			try {
	    		GenericValue inventorySummary = delegator.findOne("InventorySummary", UtilMisc.toMap("saleDate", dayStart, "facilityId", facilityId, "productId", productId), false);
	    		if (inventorySummary == null) {
	    			
	    			openingBalance = openingBalance;
	    			BigDecimal closingBalance = ((((openingBalance.add(receivedQty)).add(saleQty)).add(adjustments)).add(xferIn)).subtract(xferOut);
	    			
	    			inventorySummary = delegator.makeValue("InventorySummary");
	    			inventorySummary.put("saleDate", dayStart );
	    			inventorySummary.put("facilityId", facilityId);
	    			inventorySummary.put("productId", productId); 
	    			inventorySummary.put("openingBalance", openingBalance);
	    			inventorySummary.put("receipts", receivedQty);
	    			inventorySummary.put("sales", saleQty.negate());
	    			inventorySummary.put("xferIn", xferIn);
	    			inventorySummary.put("xferOut", xferOut);
	    			inventorySummary.put("adjustments", adjustments);
	    			inventorySummary.put("closingBalance", closingBalance);
	    			inventorySummary.create(); 
	    			openingBalance = closingBalance;
	            }
	    		else { 
	    			
	    			if(UtilValidate.isEmpty(isUpdateOB)){
	    				if( (UtilValidate.isNotEmpty(inventorySummary.get("isUpdated")) ) && ((inventorySummary.get("isUpdated")).equals("Y")) ){
		    				continue;
		    			}
	    			}
	    			BigDecimal receipts = (BigDecimal) inventorySummary.get("receipts");
	    			receipts = receipts.add(receivedQty);
	    			
	    			BigDecimal sales = ((BigDecimal) inventorySummary.get("sales")).negate();
	    			sales = sales.add(saleQty);
	    			
	    			BigDecimal updateXferIn = BigDecimal.ZERO;
	    			if(UtilValidate.isNotEmpty(inventorySummary.get("xferIn"))){
	    				updateXferIn = (BigDecimal) inventorySummary.get("xferIn");
	    			}
	    			updateXferIn = updateXferIn.add(xferIn);
	    			
	    			BigDecimal updateXferOut = BigDecimal.ZERO;
	    			if(UtilValidate.isNotEmpty(inventorySummary.get("xferOut"))){
	    				updateXferOut = (BigDecimal) inventorySummary.get("xferOut");
	    			}
	    			updateXferOut = updateXferOut.add(xferOut);
	    			
	    			BigDecimal updateAdjustment = BigDecimal.ZERO;
	    			if(UtilValidate.isNotEmpty(inventorySummary.get("adjustments"))){
	    				updateAdjustment = (BigDecimal) inventorySummary.get("adjustments");
	    			}
	    			updateAdjustment = updateAdjustment.add(adjustments);
	    			
	    			BigDecimal closingBalance = ((((openingBalance.add(receipts)).add(sales)).add(updateAdjustment)).add(updateXferIn)).subtract(updateXferOut);

	    			inventorySummary.put("openingBalance", openingBalance);
	    			inventorySummary.put("receipts", receipts);
	    			inventorySummary.put("sales", sales.negate());
	    			inventorySummary.put("xferIn", updateXferIn);
	    			inventorySummary.put("xferOut", updateXferOut);
	    			inventorySummary.put("adjustments", updateAdjustment);
	    			inventorySummary.put("closingBalance", closingBalance);
	    			
	    			inventorySummary.store();
	    			openingBalance = closingBalance;
	            }
	    		
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	    	dayStart =  UtilDateTime.addDaysToTimestamp(dayStart, 1);
			receivedQty = BigDecimal.ZERO;
			saleQty = BigDecimal.ZERO;
			xferIn = BigDecimal.ZERO;
			xferOut = BigDecimal.ZERO;
			adjustments = BigDecimal.ZERO;
		}
		
		return result;
	}
	
	public static Map<String, Object> adjustInventory(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		if (UtilValidate.isEmpty(effectiveDate)) {
			effectiveDate = UtilDateTime.nowTimestamp();
	    }
		Timestamp dayStart = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
		Timestamp dayEnd = UtilDateTime.getDayEnd(effectiveDate, TimeZone.getDefault(), locale);
		
		String facilityId = (String) context.get("facilityId");
		String productId = (String) context.get("productId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		String shipmentId = (String) context.get("shipmentId");
		BigDecimal unitCost = (BigDecimal) context.get("unitCost");
		String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
		if (UtilValidate.isEmpty(unitCost)) {
			unitCost = BigDecimal.ZERO;
	    }
		GenericValue invItem = (GenericValue) context.get("inventoryItem");
		Boolean isSale = (Boolean) context.get("isSale");
		if (UtilValidate.isEmpty(isSale)) {
			isSale = Boolean.FALSE;
	    }
		Boolean isReceipt = (Boolean) context.get("isReceipt");
		if (UtilValidate.isEmpty(isReceipt)) {
			isReceipt = Boolean.FALSE;
	    }
		Boolean isXferIn = (Boolean) context.get("isXferIn");
		if (UtilValidate.isEmpty(isXferIn)) {
			isXferIn = Boolean.FALSE;
	    }
		Boolean isXferOut = (Boolean) context.get("isXferOut");
		if (UtilValidate.isEmpty(isXferOut)) {
			isXferOut = Boolean.FALSE;
	    }
		
		BigDecimal receivedQty = BigDecimal.ZERO;
		BigDecimal saleQty = BigDecimal.ZERO;
		BigDecimal xferIn = BigDecimal.ZERO;
		BigDecimal xferOut = BigDecimal.ZERO;
		if(isSale){
			saleQty = quantity;
		}
		if(isReceipt){
			receivedQty = quantity;
		}
		if(isXferIn){
			xferIn = quantity;
		}
		if(isXferOut){
			xferOut = quantity;
		}
		
		List<GenericValue> inventoryItems = null;
		Map inventoryItemMap = FastMap.newInstance();
    	BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		
    	if( (quantity.compareTo(BigDecimal.ZERO) >= 0) && (UtilValidate.isEmpty(invItem))){
    		Map<String, Object> receiveInventoryResult;
			Map<String, Object> receiveInventoryContext = FastMap.newInstance();
            receiveInventoryContext.put("userLogin", userLogin);   
            receiveInventoryContext.put("productId", productId);
            receiveInventoryContext.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM"); 
            receiveInventoryContext.put("facilityId", facilityId);
            receiveInventoryContext.put("datetimeReceived", dayStart);
            receiveInventoryContext.put("quantityAccepted", quantity);
            receiveInventoryContext.put("unitCost", unitCost);
            receiveInventoryContext.put("quantityRejected", BigDecimal.ZERO);
            if (UtilValidate.isNotEmpty(productSubscriptionTypeId)) {
            	receiveInventoryContext.put("productSubscriptionTypeId", productSubscriptionTypeId);
            }
            if(UtilValidate.isNotEmpty(shipmentId)){
            	receiveInventoryContext.put("shipmentId", shipmentId);
            }
          
            try {
				receiveInventoryResult = dispatcher.runSync("receiveInventoryProduct", receiveInventoryContext);
				if (ServiceUtil.isError(receiveInventoryResult)) {
					Debug.logError("There was an error while receiving Inventory {" + receiveInventoryContext + "} " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);			             
		            return ServiceUtil.returnError("There was an error while receiving Inventory" + ServiceUtil.getErrorMessage(receiveInventoryResult));  
	            }
			} catch (GenericServiceException e) {
				Debug.logError(e, e.toString(), module);
                 return ServiceUtil.returnError(e.toString());
			}
    	}
    	else{
    		
    		if(UtilValidate.isEmpty(invItem)){
    			
    	    	List conditionList = FastList.newInstance();
    			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    			conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    			EntityCondition invCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    			
    			List<String> orderBy = UtilMisc.toList("-datetimeReceived", "inventoryItemId");
    			try {
    				inventoryItems = delegator.findList("InventoryItem", invCondition , null, orderBy, null, false );
    			} catch (GenericEntityException e) {
    				Debug.logError(e, module);
    				return ServiceUtil.returnError("Error finding inventory item ");
    			}
    			for (GenericValue inventoryItem : inventoryItems) {
        			if(UtilValidate.isNotEmpty(inventoryItem.get("quantityOnHandTotal"))){
        				if(((BigDecimal)(inventoryItem.get("quantityOnHandTotal")) ).compareTo(BigDecimal.ZERO) >= 0 ){
        					String tempInventoryItemId = null;
        					BigDecimal tempQuantityOnHandTotal = BigDecimal.ZERO;
        					tempInventoryItemId = (String) inventoryItem.get("inventoryItemId");
        					tempQuantityOnHandTotal = (BigDecimal) inventoryItem.get("quantityOnHandTotal");
        					inventoryItemMap.put(tempInventoryItemId, tempQuantityOnHandTotal);
        					quantityOnHandTotal = quantityOnHandTotal.add(tempQuantityOnHandTotal);
        				}
        			}
        		}
    		}
    		else{
    			
    			if(UtilValidate.isEmpty(facilityId)){
    				try {
    					GenericValue fac = invItem.getRelatedOne("Facility");
    					facilityId = fac.getString("facilityId");
    				} catch (GenericEntityException e1) {
    					Debug.logError(e1, "Problems getting productStore Details with custRequestId: ", module);
    					return ServiceUtil.returnError("Error finding facId ");
    				}
    			}
    			if(UtilValidate.isEmpty(productId)){
    				productId = (String) invItem.get("productId");
    			}
    			
    			//inventoryItems.add(invItem);
    			quantityOnHandTotal = (BigDecimal) invItem.get("quantityOnHandTotal");
    			inventoryItemMap.put(invItem.get("inventoryItemId"), quantityOnHandTotal);
    		}

    		if((quantityOnHandTotal).compareTo(quantity) >= 0){
    			Iterator invItemIter = inventoryItemMap.entrySet().iterator();
    			GenericValue inventoryItemRow = null;
    			boolean checkFlag = true;
    			while (invItemIter.hasNext() && checkFlag) {
    				BigDecimal tempXferQty = BigDecimal.ZERO;
    				Map.Entry invItemEntry = (Entry) invItemIter.next();
    				String productInvId = (String) invItemEntry.getKey();
    				BigDecimal tempATP = (BigDecimal) invItemEntry.getValue();
    				if((quantity).compareTo(tempATP) >= 0){
    					tempXferQty = tempATP;
    					quantity = quantity.subtract(tempATP);
    				}else{
    					tempXferQty = quantity;
    					quantity = BigDecimal.ZERO;
    				}
    				if(tempXferQty.equals(BigDecimal.ZERO)){
    					//continue;
    					checkFlag = false;
    				}
    				if(checkFlag == true){
    					Map inputMap = FastMap.newInstance();
    					inputMap.put("inventoryItemId", productInvId);
    					inputMap.put("userLogin",userLogin);
    					inputMap.put("quantityOnHandDiff",tempXferQty);
    					inputMap.put("accountingQuantityDiff",tempXferQty);
    					try {
    						Map<String, Object> serviceResult = dispatcher.runSync("createInventoryItemDetail", inputMap);
    						if(ServiceUtil.isError(serviceResult)){
    							Debug.logError("Trouble in createInventoryItemDetail service", module);
    							return ServiceUtil.returnError("Trouble in createInventoryItemDetail service");
    						}
    					} catch (GenericServiceException e) {
    						Debug.logError(e, "Trouble calling createInventoryItemDetail service", module);
    						return ServiceUtil.returnError("Trouble calling createInventoryItemDetail service");
    					}
    				}
    				
    			}
    		}
    		else{
    			Debug.logError("No enough stock left for Sale for product: [" + productId+"]", "");
    			return ServiceUtil.returnError("No enough stock left for Sale for product: [" + productId+"]");
    		}
    		
    	}
    	result.put("facilityId",facilityId);
		result.put("productId",productId);
		result.put("receivedQty",receivedQty);
		result.put("saleQty",saleQty);
		result.put("xferIn",xferIn);
		result.put("xferOut",xferOut.negate());
		result.put("effectiveDate",dayStart);
		
		return result;
	}
	
	public static Map<String, Object> createByProductFacility(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String ownerPartyId = null;
		String contactMechId = null;
		
		String facilityId = (String) context.get("facilityId");
		String facilityTypeId = (String) context.get("facilityTypeId");
		String facilityName = (String) context.get("facilityName");
		String zoneId = (String) context.get("zoneId");
		String city = (String) context.get("city");
		String categoryTypeEnum = (String) context.get("categoryTypeEnum");
		String groupName = (String) context.get("inchargeName");
		String byProdRouteId = (String) context.get("byProdRouteId");
		String description = (String) context.get("description");
		String subscriptionTypeId = (String) context.get("subscriptionTypeId");
		String pinCode = (String) context.get("pinCode");
		String address1 = (String) context.get("address1");
		String address2 = (String) context.get("address2");
		String contactNumber = (String) context.get("contactNumber");
		BigDecimal deposit = (BigDecimal)context.get("deposit");
		String useEcs = (String) context.get("useEcs");
		String finAccountBranch = (String) context.get("bankBranch");
		String finAccountName = (String) context.get("bankName");
		String openedDate = (String) context.get("openedDate");
		String closedDate = (String) context.get("closedDate");
		Timestamp openDate = null;
		Timestamp closeDate = null;
		
		if (UtilValidate.isNotEmpty(openedDate)) { 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				openDate = new java.sql.Timestamp(sdf.parse(openedDate).getTime());
				openDate = UtilDateTime.getDayStart(openDate);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ openedDate, module);
				return ServiceUtil.returnError("Cannot parse date string: "+ openedDate);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ openedDate, module);
				return ServiceUtil.returnError("Cannot parse empty date string ");
			}
		}
        else{
        	openDate = UtilDateTime.nowTimestamp();
        }
		
		if (UtilValidate.isNotEmpty(closedDate)) { 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				closeDate = new java.sql.Timestamp(sdf.parse(closedDate).getTime());
				closeDate = UtilDateTime.getDayStart(closeDate);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ closedDate, module);
				return ServiceUtil.returnError("Cannot parse date string: "+ closedDate);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ closedDate, module);
				return ServiceUtil.returnError("Cannot parse empty date string ");
			}
		}
		facilityId = facilityId.toUpperCase();
		Map<String, Object> resultMap = FastMap.newInstance();
		Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> classificationMap = FastMap.newInstance();
		GenericValue facility;
		GenericValue route;
		GenericValue zone;
		classificationMap = UtilMisc.toMap("AVM_FROS", "PM_RC_V", "DEWS_PARLOURS", "PM_RC_D", "FROS", "PM_RC_F", "INSTITUTIONS", "PM_RC_C", "KFROS","PM_RC_F", "MCCS", "PM_RC_M", "PARLOUR", "PM_RC_P", "SP_SALES", "PM_RC_S", "WHOLESALE_DEALERS", "PM_RC_W");
		try {
			facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			route = delegator.findOne("Facility", UtilMisc.toMap("facilityId", byProdRouteId), false);
			zone = delegator.findOne("Facility", UtilMisc.toMap("facilityId", zoneId), false);
			if(UtilValidate.isNotEmpty(facility)){
				Debug.logError("Party Code Already Exists!", module);
				return ServiceUtil.returnError("Party Code Already Exists!");
			}
			if(UtilValidate.isEmpty(route)){
				Debug.logError("Route doesn't Exists!", module);
				return ServiceUtil.returnError("Route ["+byProdRouteId+"] doesn't Exists!");
			}
			if(UtilValidate.isEmpty(zone)){
				Debug.logError("Zone does not Exists!", module);
				return ServiceUtil.returnError("Zone ["+zoneId+"] doesn't  Exists!");
			}
			if(categoryTypeEnum == null){
				Debug.logError("Category is missing", module);
				return ServiceUtil.returnError("Category is missing");
			}
			
			if(groupName == null){
				Debug.logError("groupName is missing", module);
				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
				return ServiceUtil.returnError("Incharge Name is missing");
			}
			input = UtilMisc.toMap("groupName",context.get("inchargeName"));
			resultMap = dispatcher.runSync("createPartyGroup", input);
			if (ServiceUtil.isError(resultMap)) {
				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
				return ServiceUtil.returnError("Error creating party group");
            }
			ownerPartyId = (String) resultMap.get("partyId"); 
			if(UtilValidate.isNotEmpty(ownerPartyId)){
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", ownerPartyId), false);
				if(UtilValidate.isNotEmpty(partyGroup)){
					partyGroup.put("securityDeposit", deposit);
					partyGroup.store();
				}
				String rateType = (String)classificationMap.get(categoryTypeEnum);
				if(UtilValidate.isNotEmpty(rateType)){
					input = UtilMisc.toMap("partyClassificationGroupId", rateType, "userLogin", userLogin, "partyId", ownerPartyId, "fromDate", UtilDateTime.nowTimestamp());
					resultMap = dispatcher.runSync("createPartyClassification", input);
					if (ServiceUtil.isError(resultMap)) {
						Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		                return resultMap;
		            }
				}
			}
			Object tempInput = "BOOTH_OWNER";
			input = UtilMisc.toMap("userLogin", userLogin, "partyId", ownerPartyId, "roleTypeId", tempInput);
			resultMap = dispatcher.runSync("createPartyRole", input);
			if (ServiceUtil.isError(resultMap)) {
				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                return resultMap;
            }
			
			if (UtilValidate.isNotEmpty(address1) && UtilValidate.isNotEmpty(city)){
				input = UtilMisc.toMap("userLogin", userLogin, "partyId",ownerPartyId, "address1",address1, "address2", address2, "city", (String)context.get("city"), "stateProvinceGeoId", (String)context.get("stateProvinceGeoId"), "postalCode", pinCode, "contactMechId", contactMechId);
				resultMap =  dispatcher.runSync("createPartyPostalAddress", input);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                return resultMap;
	            }
				contactMechId = (String) resultMap.get("contactMechId");
				tempInput = "BILLING_LOCATION";
				input = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "partyId",ownerPartyId, "contactMechPurposeTypeId", tempInput);
				resultMap =  dispatcher.runSync("createPartyContactMechPurpose", input);
				if (ServiceUtil.isError(resultMap)) {
				    Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                return resultMap;
	            }
			 }
			
			if(UtilValidate.isNotEmpty(contactNumber)){
				Map inputCtx = UtilMisc.toMap("partyId", ownerPartyId);
				inputCtx.put("contactNumber", contactNumber);
				inputCtx.put("userLogin", userLogin); 
				Map<String, Object> serviceResult = ServiceUtil.returnSuccess();
	            serviceResult = dispatcher.runSync("createPartyTelecomNumber", inputCtx);
	            if (ServiceUtil.isError(serviceResult)) {
	            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
	            } 
	            contactMechId = (String)serviceResult.get("contactMechId");
	            Map createPartyContactMechPurposeMap = UtilMisc.toMap("userLogin", userLogin);
	            createPartyContactMechPurposeMap.put("contactMechId", contactMechId);
	            createPartyContactMechPurposeMap.put("partyId", ownerPartyId);
	            createPartyContactMechPurposeMap.put("contactMechPurposeTypeId" , "PHONE_MOBILE");
	            serviceResult = dispatcher.runSync("createPartyContactMechPurpose", createPartyContactMechPurposeMap);
	            
	            if (ServiceUtil.isError(serviceResult)) {
	            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
	            } 
			}			
			 GenericValue newSubscription = delegator.makeValue("Subscription");			
			 newSubscription.put("subscriptionTypeId", subscriptionTypeId );
			 newSubscription.put("facilityId", facilityId);
			 if (UtilValidate.isEmpty((String)context.get("facilityName"))){
				Debug.logError("Name of the Booth is Missing", module);
			   	return ServiceUtil.returnError("Name of the Booth is Missing");
			 }
			 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", ownerPartyId, "openedDate", openDate, "closedDate", closeDate, "facilityId", facilityId, "facilityTypeId", "BOOTH",
					 "categoryTypeEnum", categoryTypeEnum, "byProdRouteId", byProdRouteId, "zoneId", zoneId, "facilityName", (String)context.get("facilityName"));   
			 resultMap =  dispatcher.runSync("createFacility", input);
			 if (ServiceUtil.isError(resultMap)) {
				 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
				 return ServiceUtil.returnError("Error Creating Facility :"+facilityId);
             }
			 String resultFacilityId = (String) resultMap.get("facilityId");
			 delegator.createSetNextSeqId(newSubscription);
			 if(categoryTypeEnum.equals("PARLOUR")){
				 input = UtilMisc.toMap("userLogin", userLogin, "productStoreId", resultFacilityId, "storeName", facilityName, "companyName", "AAVIN", "payToPartyId", "Company",
						 "manualAuthIsCapture", "N", "prorateShipping", "Y", "prorateTaxes", "Y", "viewCartOnAdd", "N", "autoSaveCart", "N","autoApproveReviews", "N", "isDemoStore", "Y", 
						 "isImmediatelyFulfilled", "N","inventoryFacilityId", resultFacilityId, "oneInventoryFacility", "Y","checkInventory","N", "reserveInventory", "N","reserveOrderEnumId", "INVRO_FIFO_REC",
						 "requireInventory", "N", "balanceResOnOrderCreation", "N", "defaultLocaleString", "en_US", "defaultCurrencyUomId", "INR", "allowPassword", "Y", "explodeOrderItems", "N",
						 "checkGcBalance","N","retryFailedAuths","Y", "headerApprovedStatus", "ORDER_APPROVED", "itemApprovedStatus", "ITEM_APPROVED", "digitalItemApprovedStatus", "ITEM_APPROVED",
						 "headerDeclinedStatus", "ORDER_REJECTED","itemDeclinedStatus", "ITEM_REJECTED","headerCancelStatus", "ORDER_SENT", "itemCancelStatus", "ITEM_CANCELLED", "usePrimaryEmailUsername", "N",
						 "requireCustomerRole", "N", "autoInvoiceDigitalItems", "Y", "reqShipAddrForDigItems", "Y", "showCheckoutGiftOptions","Y", "selectPaymentTypePerItem", "N","showPricesWithVatTax", "N",
						 "showTaxIsExempt", "Y", "enableAutoSuggestionList", "N", "enableDigProdUpload", "N", "prodSearchExcludeVariants", "Y", "autoOrderCcTryExp", "Y","autoOrderCcTryOtherCards", "Y",
						 "autoOrderCcTryLaterNsf", "Y", "autoApproveInvoice", "Y", "autoApproveOrder", "Y", "shipIfCaptureFails", "Y", "reqReturnInventoryReceive", "N", "addToCartRemoveIncompat", "Y",
						 "addToCartReplaceUpsell", "Y", "splitPayPrefPerShpGrp", "Y","showOutOfStockProducts","Y");
				 resultMap =  dispatcher.runSync("createProductStore", input);
				 if (ServiceUtil.isError(resultMap)) {
					 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
					 return ServiceUtil.returnError("Error creating product store :"+facilityId);
	             }
				 String productStoreId = (String)resultMap.get("productStoreId");
				 if(UtilValidate.isNotEmpty(productStoreId)){
					 /*GenericValue productStore = delegator.findOne("ProductStore",UtilMisc.toMap("productStoreId", productStoreId), false);
					 productStore.set("productStoreId", facilityId);
					 productStore.store();*/
					 Map<String, Object> productInputMap = FastMap.newInstance();
					 List<GenericValue> productList = ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
					 List<String> products = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
					 Debug.log("productList @@@@@@@@@@@@@@@@@@"+products);
					 for(String product : products){
						 productInputMap = UtilMisc.toMap("userLogin", userLogin, "productId", product, "facilityId", facilityId, "minimumStock", BigDecimal.ZERO, "lastInventoryCount", BigDecimal.ZERO);
						 resultMap = dispatcher.runSync("createProductFacility", productInputMap);
						 if(ServiceUtil.isError(resultMap)){
							 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
							 return ServiceUtil.returnError("Error creating product facility :"+product);
						 }
					 }
				 }
			 }
			 
			 if (UtilValidate.isNotEmpty(finAccountName)){
				 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", ownerPartyId,"finAccountTypeId","BANK_ACCOUNT", "finAccountBranch", finAccountBranch, "finAccountName" , finAccountName);
				 resultMap = dispatcher.runSync("createFinAccount", input);
				 if (ServiceUtil.isError(resultMap)) {
					 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                 return resultMap;
	             }
			 }
			 String finAccountId = (String) resultMap.get("finAccountId");
			 /*input = UtilMisc.toMap("userLogin", userLogin, "partyId", ownerPartyId, "facilityId", facilityId, "parentFacilityId", parentFacilityId);
			 resultMap = dispatcher.runSync("updateFacilityParty", input);
			 if (ServiceUtil.isError(resultMap)) {
				 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                 return resultMap;
             }
			 result = ServiceUtil.returnSuccess("Booth "+resultFacilityId+ " is successfully created");
			 result.put("facilityId", resultFacilityId);*/
			 
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error while populating FacilityParty" + e);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}
		result = ServiceUtil.returnSuccess("Successfully created facility :"+facilityId);
		result.put("facilityId", facilityId);
		return result;
	}
	
	public static Map<String, Object> UpdateByProductFacility(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String ownerPartyId = null;
		String contactMechId = null;
		String facilityId = (String) context.get("facilityId");
		String facilityTypeId = (String) context.get("facilityTypeId");
		String facilityName = (String) context.get("facilityName");
		String zoneId = (String) context.get("zoneId");
		String city = (String) context.get("city");
		String categoryTypeEnum = (String) context.get("categoryTypeEnum");
		String partyId = (String) context.get("inchargeName");
		String byProdRouteId = (String) context.get("byProdRouteId");
		String pinCode = (String) context.get("pinCode");
		String address1 = (String) context.get("address1");
		String address2 = (String) context.get("address2");
		String contactNumber = (String) context.get("contactNumber");
		BigDecimal deposit = (BigDecimal)context.get("deposit");
		String finAccountId = (String) context.get("finAccountId");
		String finAccountBranch = (String) context.get("bankBranch");
		String finAccountName = (String) context.get("bankName");
		String openedDate = (String) context.get("openedDate");
		String closedDate = (String) context.get("closedDate");
		Timestamp openDate = null;
		Timestamp closeDate = null;
		
		if (UtilValidate.isNotEmpty(openedDate)) { 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				openDate = new java.sql.Timestamp(sdf.parse(openedDate).getTime());
				openDate = UtilDateTime.getDayStart(openDate);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ openedDate, module);
				return ServiceUtil.returnError("Cannot parse date string: "+ openedDate);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ openedDate, module);
				return ServiceUtil.returnError("Cannot parse empty date string ");
			}
		}
        
		
		if (UtilValidate.isNotEmpty(closedDate)) { 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				closeDate = new java.sql.Timestamp(sdf.parse(closedDate).getTime());
				closeDate = UtilDateTime.getDayStart(closeDate);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ closedDate, module);
				return ServiceUtil.returnError("Cannot parse date string: "+ closedDate);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ closedDate, module);
				return ServiceUtil.returnError("Cannot parse empty date string ");
			}
		}
        
		
		try{
			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			GenericValue route = delegator.findOne("Facility", UtilMisc.toMap("facilityId", byProdRouteId), false);
			GenericValue zone = delegator.findOne("Facility", UtilMisc.toMap("facilityId", zoneId), false);
			if(UtilValidate.isEmpty(route)){
				Debug.logError("Route doesn't Exists!", module);
				return ServiceUtil.returnError("Route ["+byProdRouteId+"] doesn't Exists!");
			}
			if(UtilValidate.isEmpty(zone)){
				Debug.logError("Zone does not Exists!", module);
				return ServiceUtil.returnError("Zone ["+zoneId+"] doesn't  Exists!");
			}
			if(UtilValidate.isEmpty(facility)){
				Debug.logError("Facility ["+facilityId+"] doesn't Exists!", module);
				return ServiceUtil.returnError("Facility ["+facilityId+"] doesn't Exists!");
			}else{
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				if(UtilValidate.isNotEmpty(zoneId)){
					facility.set("zoneId", zoneId);
				}
				if(UtilValidate.isNotEmpty(byProdRouteId)){
					facility.set("byProdRouteId", byProdRouteId);
				}
				if(UtilValidate.isNotEmpty(facilityName)){
					facility.set("facilityName", facilityName);
				}
				if(UtilValidate.isNotEmpty(openDate)){
					facility.set("openedDate", openDate);
				}
				facility.set("closedDate", closeDate);
				facility.store();
			}
			
			if(UtilValidate.isNotEmpty(finAccountId)){
				GenericValue finAccount = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", finAccountId), false);
				if(UtilValidate.isNotEmpty(finAccountBranch)){
					finAccount.set("finAccountBranch", finAccountBranch);
				}
				if(UtilValidate.isNotEmpty(finAccountName)){
					finAccount.set("finAccountName", finAccountName);
				}
				finAccount.store();
			}
			
			if(UtilValidate.isNotEmpty(partyId)){
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				if(UtilValidate.isNotEmpty(partyGroup)){
					if(UtilValidate.isNotEmpty(deposit)){
						partyGroup.set("securityDeposit", deposit);
						partyGroup.store();
					}
				}
				/*List<GenericValue> partyContactMech = delegator.findList("PartyAndContactMech", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false);
				Map<String, Object> resultMap = FastMap.newInstance();
				Map<String, Object> input = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(partyContactMech)){
					for(GenericValue eachMech : partyContactMech){
						contactMechId = (String)eachMech.get("contactMechId");
						String contactMechTypeId = (String)eachMech.get("contactMechTypeId");
						Debug.log("contactMechId #################"+contactMechId);
						Debug.log("contactMechTypeId #################"+contactMechTypeId);
						if(contactMechTypeId.equalsIgnoreCase("POSTAL_ADDRESS")){
							if(UtilValidate.isNotEmpty(address1) ){
								input = UtilMisc.toMap("userLogin", userLogin, "address1", address1, "postalCode", pinCode, "city", city, "contactMechId", contactMechId);
								resultMap = dispatcher.runSync("updatePartyPostalAddress", input);
								if(ServiceUtil.isError(resultMap)){
									Debug.logError("Error while updating address1 !", module);
									return ServiceUtil.returnError("Error while updating address1 !");
								}
							}
							if(UtilValidate.isNotEmpty(address2) ){
								input = UtilMisc.toMap("userLogin", userLogin, "address1", address1, "postalCode", pinCode, "address2", address2, "contactMechId", contactMechId);
								resultMap = dispatcher.runSync("updatePartyPostalAddress", input);
								if(ServiceUtil.isError(resultMap)){
									Debug.logError("Error while updating address2 !", module);
									return ServiceUtil.returnError("Error while updating address2 !");
								}
							}
							if(UtilValidate.isNotEmpty(city) ){
								input = UtilMisc.toMap("userLogin", userLogin, "address1", address1, "city", city, "postalCode", pinCode,"contactMechId", contactMechId);
								resultMap = dispatcher.runSync("updatePartyPostalAddress", input);
								if(ServiceUtil.isError(resultMap)){
									Debug.logError("Error while updating city !", module);
									return ServiceUtil.returnError("Error while updating city !");
								}
							}
							if(UtilValidate.isNotEmpty(pinCode) ){
								input = UtilMisc.toMap("userLogin", userLogin, "address1", address1, "city", city, "postalCode", pinCode, "contactMechId", contactMechId);
								resultMap = dispatcher.runSync("updatePartyPostalAddress", input);
								if(ServiceUtil.isError(resultMap)){
									Debug.logError("Error while updating pinCode !", module);
									return ServiceUtil.returnError("Error while updating pinCode !");
								}
							}
						}
						if(contactMechTypeId.equalsIgnoreCase("TELECOM_NUMBER")){
							if(UtilValidate.isNotEmpty(contactNumber) ){
								input = UtilMisc.toMap("userLogin", userLogin, "contactNumber", contactNumber, "contactMechId", contactMechId);
								resultMap = dispatcher.runSync("updatePartyTelecomNumber", input);
								if(ServiceUtil.isError(resultMap)){
									Debug.logError("Error while updating contactNumber !", module);
									return ServiceUtil.returnError("Error while updating contactNumber !");
								}
							}
						}
					}
				}*/
			}
		} /*catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error while populating FacilityParty" + e);
		}*/ catch (Exception e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}
		result = ServiceUtil.returnSuccess("Successfully updated facility :"+facilityId);
		result.put("facilityId", facilityId);
		return result;
	}
	public static Map<String, Object> processChangeIndentRoute(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("boothId");
		String routeId = (String) context.get("routeId");
		String newRouteId = (String) context.get("newRouteId");
		String supplyDate = (String) context.get("supplyDate");
		String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
		List subscription = FastList.newInstance();
		GenericValue newRoute = null;
		String subscriptionId = "";
		List exprList = null;
		List<GenericValue> subscriptionProduct = null;
		List<GenericValue> subscriptionProductOldRoute = null;
		List<GenericValue> subscriptionProductNewRoute = null;
		Timestamp supplyDateTimestamp = null;
		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
        conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        
        try{
        	subscription = delegator.findList("Subscription", condition, null, null, null, false);
        	newRoute = delegator.findOne("Facility",UtilMisc.toMap("facilityId", newRouteId), false);
        	if(UtilValidate.isEmpty(newRoute)){
        		Debug.logError("Route : "+newRouteId+" does not exist", module);
				return ServiceUtil.returnError("Route : "+newRouteId+" does not exist");
        	}
        	if(routeId.equals(newRouteId)){
    			return ServiceUtil.returnSuccess("Route changed successfull "+routeId);
            }
        	if(UtilValidate.isEmpty(subscription)){
        		Debug.logError("No Subscription for the Party Code :"+facilityId, module);
				return ServiceUtil.returnError("No Subscription for the Party Code :"+facilityId);
        	}
        	else{
        		GenericValue subscProd = EntityUtil.getFirst(subscription);
        		subscriptionId = subscProd.getString("subscriptionId");
        	}
        }catch(GenericEntityException e){
        	Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
        }
        if (UtilValidate.isNotEmpty(supplyDate)) { 
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
			try {
				supplyDateTimestamp = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
				supplyDateTimestamp = UtilDateTime.getDayStart(supplyDateTimestamp);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ supplyDate, module);
				return ServiceUtil.returnError("Cannot parse date string: "+ supplyDate);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ supplyDate, module);
				return ServiceUtil.returnError("Cannot parse empty date string ");
			}
		}
        else{
        	return ServiceUtil.returnError("Provide supply date to change the route");
        }
        List shipmentList = null;
        
        exprList = UtilMisc.toList(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
        exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
        EntityCondition cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
        
        List condList = UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
    	condList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
    	condList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS ,routeId));
    	condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, supplyDateTimestamp));
    	condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO , supplyDateTimestamp));
    	EntityCondition cond1 = EntityCondition.makeCondition(condList,EntityOperator.AND);
    	
        try{
        	shipmentList = delegator.findList("Shipment", cond1, null , null, null, false);
        	if(UtilValidate.isNotEmpty(shipmentList)){
        		Debug.logInfo(" Delivery Schedule already generated for the route : "+routeId, "");
        		return ServiceUtil.returnError(" Delivery Schedule already generated for the route : "+routeId);
        	}
        	subscriptionProduct = delegator.findList("SubscriptionProduct", cond, null, null, null, false);
        	subscriptionProduct = EntityUtil.filterByDate(subscriptionProduct, supplyDateTimestamp);
        	subscriptionProductOldRoute = EntityUtil.filterByCondition(subscriptionProduct, EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
        	
        	if(UtilValidate.isEmpty(subscriptionProductOldRoute)){
        		Debug.logError("No products indented for party code : "+facilityId+" in route :"+routeId, module);
    			return ServiceUtil.returnError("No products indented for party code : "+facilityId+" in route :"+routeId);
        	}
        	
        	subscriptionProductNewRoute = EntityUtil.filterByCondition(subscriptionProduct, EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, newRouteId));
        	
        	if(UtilValidate.isEmpty(subscriptionProductNewRoute)){
        		List<GenericValue> tempList = FastList.newInstance();
           		for(GenericValue eachSubProd : subscriptionProductOldRoute){
           			GenericValue tempSubProd = delegator.makeValue("SubscriptionProduct");
           			tempSubProd.putAll(eachSubProd);
           			tempSubProd.set("sequenceNum", newRouteId);
           			tempSubProd.set("lastModifiedDate", UtilDateTime.nowTimestamp());
           			tempSubProd.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
            		tempList.add(tempSubProd);
           		}
           		delegator.storeAll(tempList);
           		int rows_deleted = delegator.removeAll(subscriptionProductOldRoute);
    			Debug.log(rows_deleted+" rows deleted from subscription entity");
        	}
        	else{
        		List<GenericValue> tempList = FastList.newInstance();
        		for(GenericValue eachSubProd : subscriptionProductNewRoute){
        			GenericValue tempSubProd = delegator.makeValue("SubscriptionProduct");
           			tempSubProd.putAll(eachSubProd);
           			tempSubProd.set("sequenceNum", newRouteId);
           			tempSubProd.set("lastModifiedDate", UtilDateTime.nowTimestamp());
           			tempSubProd.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
            		tempList.add(tempSubProd);
           		}
        		for(GenericValue eachSubProdOld : subscriptionProductOldRoute){
        			String productId = eachSubProdOld.getString("productId");
        			BigDecimal quantity = eachSubProdOld.getBigDecimal("quantity");
        			BigDecimal totalQuant = BigDecimal.ZERO;
        			List productExistList = EntityUtil.filterByCondition(tempList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        			if(UtilValidate.isNotEmpty(productExistList)){
        				GenericValue productExist = EntityUtil.getFirst(productExistList);
        				BigDecimal lastQuant = (BigDecimal)productExist.get("quantity");
        				totalQuant = lastQuant.add(quantity);
        				productExist.set("quantity", totalQuant);
        				tempList.add(productExist);
        			}
        			else{
        				GenericValue tempSubProd = delegator.makeValue("SubscriptionProduct");
               			tempSubProd.putAll(eachSubProdOld);
               			tempSubProd.set("sequenceNum", newRouteId);
               			tempSubProd.set("lastModifiedDate", UtilDateTime.nowTimestamp());
               			tempSubProd.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
                		tempList.add(tempSubProd);
        			}
           		}
           		int rows_deleted_old = delegator.removeAll(subscriptionProductOldRoute);
           		Debug.log(rows_deleted_old+" rows deleted from subscription for route "+routeId);
           		int rows_deleted_new = delegator.removeAll(subscriptionProductNewRoute);
           		Debug.log(rows_deleted_new+" rows deleted from subscription for route "+newRouteId);
           		delegator.storeAll(tempList);
        	}
       		
        }catch(GenericEntityException e){
        	Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
        }
		result = ServiceUtil.returnSuccess("Successfully updated route :"+newRouteId+" for Party Code : "+facilityId);
		return result;
	}
	
	
	public static Map<String, Object> processChangeIndentParty(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("boothId");
		String routeId = (String) context.get("routeId");
		String newBoothId = (String) context.get("newBoothId");
		String supplyDate = (String) context.get("supplyDate");
		String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
		Locale locale = (Locale) context.get("locale");
        String currencyUomId = (String) context.get("defaultOrganizationPartyCurrencyUomId");
        String oldBoothSubscriptionId ="";
        String newBoothSubscriptionId ="";
        Timestamp supplyDateTimestamp = null;
        if (UtilValidate.isNotEmpty(supplyDate)) { 
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
			try {
				supplyDateTimestamp = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
				supplyDateTimestamp = UtilDateTime.getDayStart(supplyDateTimestamp);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ supplyDate, module);
				return ServiceUtil.returnError("Cannot parse date string: "+ supplyDate);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ supplyDate, module);
				return ServiceUtil.returnError("Cannot parse empty date string ");
			}
		}
        else{
        	return ServiceUtil.returnError("Provide supply date to change the route");
        }
        List shipmentList = null;
        
        List condList = UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
    	condList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
    	condList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS ,routeId));
    	condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, supplyDateTimestamp));
    	condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO , supplyDateTimestamp));
    	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
    	
    	try{
        	shipmentList = delegator.findList("Shipment", cond, null , null, null, false);
        	if(UtilValidate.isNotEmpty(shipmentList)){
        		Debug.logInfo(" Delivery Schedule already generated for the route : "+routeId, "");
        		return ServiceUtil.returnError(" Delivery Schedule already generated for the route : "+routeId);
        	}
        	
        	Map subscriptionMap = ByProductNetworkServices.getByProductSubscriptionId(delegator, facilityId);
        	oldBoothSubscriptionId = (String)subscriptionMap.get("subscriptionId");
        	Map newBoothsubscriptionMap = ByProductNetworkServices.getByProductSubscriptionId(delegator, newBoothId);
        	newBoothSubscriptionId = (String)newBoothsubscriptionMap.get("subscriptionId");
        }catch(GenericEntityException e){
        	Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
        }
        if(UtilValidate.isNotEmpty(newBoothSubscriptionId) && UtilValidate.isNotEmpty(oldBoothSubscriptionId)){
        	try{
        		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
        		conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, oldBoothSubscriptionId));
        		conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
        		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        		List<GenericValue> oldBoothSubscriptionProduct = delegator.findList("SubscriptionProduct", condition, null, null, null, false);
        		oldBoothSubscriptionProduct = EntityUtil.filterByDate(oldBoothSubscriptionProduct, supplyDateTimestamp);
        		
        		List conditionList1 = UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
        		conditionList1.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, newBoothSubscriptionId));
        		conditionList1.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
        		EntityCondition condition1 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
        		List<GenericValue> newBoothSubscriptionProduct = delegator.findList("SubscriptionProduct", condition1, null, null, null, false);
        		newBoothSubscriptionProduct = EntityUtil.filterByDate(newBoothSubscriptionProduct, supplyDateTimestamp);
        		if(UtilValidate.isEmpty(oldBoothSubscriptionProduct)){
        			Debug.logError("No indent items in the party", module);
            		return ServiceUtil.returnError("No indent items in the party");
        		}
        		else{
        			List<GenericValue> tempList = FastList.newInstance();
        			GenericValue genericSubProd = delegator.makeValue("SubscriptionProduct");
        			Map prodQuantMap = FastMap.newInstance();
        			for(GenericValue subProd : oldBoothSubscriptionProduct){
        				String productId = subProd.getString("productId");
        				BigDecimal quantity = subProd.getBigDecimal("quantity");
        				prodQuantMap.put(productId, quantity);
        			}
        			if(UtilValidate.isEmpty(newBoothSubscriptionProduct)){
        				for(GenericValue eachEntry : oldBoothSubscriptionProduct){
        					genericSubProd = (GenericValue)eachEntry.clone();
        					genericSubProd.set("subscriptionId", newBoothSubscriptionId);
        					genericSubProd.set("lastModifiedDate", UtilDateTime.nowTimestamp());
        					genericSubProd.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
                    		tempList.add(genericSubProd);
        				}
        				int rows_stored = delegator.storeAll(tempList);
                   		int rows_deleted = delegator.removeAll(oldBoothSubscriptionProduct);
            			Debug.log(rows_deleted+" rows deleted from subscription entity");
        			}
        			else{

        				for(GenericValue eachSubProdNew : newBoothSubscriptionProduct){
                			String prodId = eachSubProdNew.getString("productId");
                			BigDecimal qty= eachSubProdNew.getBigDecimal("quantity");
                			if(prodQuantMap.containsKey(prodId)){
                				BigDecimal quant = (BigDecimal)prodQuantMap.get(prodId);
                				BigDecimal totalQuant = quant.add(qty);
                				GenericValue tempSubProd = delegator.makeValue("SubscriptionProduct");
                       			tempSubProd.putAll(eachSubProdNew);
                       			tempSubProd.set("subscriptionId", newBoothSubscriptionId);
                       			tempSubProd.set("quantity", totalQuant);
                       			tempSubProd.set("lastModifiedDate", UtilDateTime.nowTimestamp());
                       			tempSubProd.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
                        		tempList.add(tempSubProd);
                        		prodQuantMap.remove(prodId);
                        		genericSubProd = eachSubProdNew; 
                			}
                   		}
                		Iterator prodMapIter = prodQuantMap.entrySet().iterator();
        				while (prodMapIter.hasNext()) {
        					Map.Entry prodMap = (Entry) prodMapIter.next();
        					String byprod = (String)prodMap.getKey();
        					BigDecimal quantities = (BigDecimal)prodMap.getValue();
        				
        					genericSubProd.set("productId", byprod);
        					genericSubProd.set("quantity", quantities);
        					genericSubProd.set("subscriptionId", newBoothSubscriptionId);
        					genericSubProd.set("lastModifiedDate", UtilDateTime.nowTimestamp());
        					genericSubProd.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
                    		tempList.add(genericSubProd);
        				}
                		delegator.storeAll(tempList);
                   		int rows_deleted = delegator.removeAll(oldBoothSubscriptionProduct);
            			Debug.log(rows_deleted+" rows deleted from subscription entity");
        				
        			}
        		}
        		
        	}catch(GenericEntityException e){
        		Debug.logError(e, module);
        		return ServiceUtil.returnError(e.toString());
        	}
        }
        return result;
	}
	
	public static Map<String, Object> processChangePartyCode(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("boothId");
		String routeId = (String) context.get("routeId");
		String newBoothId = (String) context.get("newBoothId");
		String supplyDate = (String) context.get("supplyDate");
		String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
		Locale locale = (Locale) context.get("locale");
        String currencyUomId = (String) context.get("defaultOrganizationPartyCurrencyUomId");
        
        if(UtilValidate.isEmpty(currencyUomId)){
        	currencyUomId ="INR";
        }
        if(UtilValidate.isNotEmpty(newBoothId)){
        	newBoothId = newBoothId.toUpperCase();
        }
        if(UtilValidate.isNotEmpty(facilityId)){
        	facilityId = facilityId.toUpperCase();
        }
		List subscription = FastList.newInstance();
		Timestamp supplyDateTimestamp = null;
		String oldFacCategory = "";
		Map productQuantMap = FastMap.newInstance();
		List prodList = FastList.newInstance();
		String newFacCategory = "";
		String shipmentId = "";
		GenericValue shipment = null;
		if (UtilValidate.isNotEmpty(supplyDate)) { 
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
			try {
				supplyDateTimestamp = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
				supplyDateTimestamp = UtilDateTime.getDayStart(supplyDateTimestamp);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ supplyDate, module);
				return ServiceUtil.returnError("Cannot parse date string: "+ supplyDate);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ supplyDate, module);
				return ServiceUtil.returnError("Cannot parse empty date string ");
			}
		}
        else{
        	return ServiceUtil.returnError("Provide supply date to change the party code");
        }
		try{
			GenericValue oldFacilityType = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
			GenericValue newFacilityType = delegator.findOne("Facility", UtilMisc.toMap("facilityId", newBoothId),false);
			if(UtilValidate.isNotEmpty(oldFacilityType) && UtilValidate.isNotEmpty(newFacilityType)){
				oldFacCategory = oldFacilityType.getString("categoryTypeEnum");
				newFacCategory = newFacilityType.getString("categoryTypeEnum");
			}
			else{
				return ServiceUtil.returnError("Invalid Facility");
			}
		}catch(GenericEntityException e){
        	Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
        }
		try{
			
			List shipmentCondition = UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
			shipmentCondition.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS, "BYPRODUCTS"));
			shipmentCondition.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, supplyDateTimestamp));
			shipmentCondition.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, supplyDateTimestamp));
			shipmentCondition.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
			EntityCondition condition = EntityCondition.makeCondition(shipmentCondition, EntityOperator.AND);
			
			List shipmentList = delegator.findList("Shipment", condition, null ,null, null, false);
			if(UtilValidate.isEmpty(shipmentList)){
				Debug.logError("No Shipment found for the date: "+supplyDate, module);
				return ServiceUtil.returnError("No Shipment found for the date: "+supplyDate);
			}else{
				shipment = EntityUtil.getFirst(shipmentList);
				shipmentId = shipment.getString("shipmentId");
			}
			
			if(oldFacCategory.equalsIgnoreCase("PARLOUR")){
			
				List invoices = null;
				List invoiceCondition = FastList.newInstance(); 
				List parlourInvList = FastList.newInstance();
				List parlourInvoiceIdsList = FastList.newInstance();
				invoiceCondition.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				invoiceCondition.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
				EntityCondition condition1 = EntityCondition.makeCondition(invoiceCondition,EntityOperator.AND);
	    	    parlourInvList = delegator.findList("Invoice", condition1, null , null, null, false);
	    	    parlourInvList = EntityUtil.getFieldListFromEntityList(parlourInvList, "invoiceId", true);
	    		result = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", parlourInvList, "statusId","INVOICE_CANCELLED","userLogin",userLogin));
	    		if (ServiceUtil.isError(result)) {
	    			 Debug.logError("There was an error while Cancelling the invoices:" + ServiceUtil.getErrorMessage(result), module);	               
		             return ServiceUtil.returnError("There was an error while Cancelling the invoices: ");   			 
	    		}	        	  
	    		
	    		List conditionList = FastList.newInstance();
	    		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
				conditionList.add(EntityCondition.makeCondition("isCancelled",  EntityOperator.EQUALS, null));
				  
				EntityCondition shipRecCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);	
				List<GenericValue> shipmentReceipts = delegator.findList("ShipmentReceiptAndItem", shipRecCond, null, null, null, false);
				
	    		for(int i=0; i<shipmentReceipts.size(); i++){
	    			String prodId = shipmentReceipts.get(i).getString("productId");
	    			BigDecimal quant = shipmentReceipts.get(i).getBigDecimal("quantityAccepted");
	    			prodList.add(prodId);
	    			productQuantMap.put(prodId, quant);
	    			Map inputMap = FastMap.newInstance();
	    			inputMap.put("facilityId", facilityId);
	    			inputMap.put("productId", prodId);
	    			inputMap.put("userLogin", userLogin);
	    			inputMap.put("effectiveDate", supplyDateTimestamp);
	    			inputMap.put("quantity", ((BigDecimal)shipmentReceipts.get(i).get("quantityAccepted")).negate());
	    			inputMap.put("isReceipt", Boolean.TRUE);

	    			Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
					if(ServiceUtil.isError(serviceResult)){
						Debug.logError("Trouble in adjustInventory service", module);
						return ServiceUtil.returnError("Trouble in adjustInventory service");
					}
					GenericValue shipmentReceipt = shipmentReceipts.get(i);
					shipmentReceipt.set("isCancelled", "Y");
					shipmentReceipt.store();
	    		}

			}
			else{
				
				List boothOrderCondition = UtilMisc.toList(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				boothOrderCondition.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
				boothOrderCondition.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
				boothOrderCondition.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
				EntityCondition cond = EntityCondition.makeCondition(boothOrderCondition,EntityOperator.AND);
				List boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", cond, null, null, null, false);
				for(int i=0; i<boothOrderItemsList.size(); i++){
					GenericValue eachOrderItem = (GenericValue)boothOrderItemsList.get(i);
	    			String prodId = eachOrderItem.getString("productId");
	    			BigDecimal quant = eachOrderItem.getBigDecimal("quantity");
	    			prodList.add(prodId);
	    			productQuantMap.put(prodId,quant);
	    		}

				List boothOrderId = EntityUtil.getFieldListFromEntityList(boothOrderItemsList, "orderId", true);
	    		//to get all invoices for the shipment
				
				List boothInvoiceId = delegator.findList("OrderHeaderFacAndItemBillingInv", cond, null, null, null, false);   
				boothInvoiceId = EntityUtil.getFieldListFromEntityList(boothInvoiceId, "invoiceId", true);    		

				result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", boothOrderId,"userLogin", userLogin));
				if (ServiceUtil.isError(result)) {
			       	   Debug.logError("There was an error while Cancel  the Orders:" + ServiceUtil.getErrorMessage(result), module);	               
			           return ServiceUtil.returnError("There was an error while Cancel  the Orders: ");  
			    } 			
		    	
				result = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", boothInvoiceId, "statusId","INVOICE_CANCELLED","userLogin", userLogin));
        		if (ServiceUtil.isError(result)) {
		    		 Debug.logError("There was an error while Cancel  the invoices: " + ServiceUtil.getErrorMessage(result), module);	               
			         return ServiceUtil.returnError("There was an error while Cancel the invoices: ");   			 
        		}
			}
		}catch(GenericEntityException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());  
		}
		
		try{
			
			String productStoreId = (String) (getByprodFactoryStore(delegator)).get("factoryStoreId");
			List<List<GenericValue>> itemAdjustments = FastList.newInstance();
			GenericValue facilityParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", newBoothId),false);
			String partyId = "";
			if(UtilValidate.isNotEmpty(facilityParty)){
				partyId = facilityParty.getString("ownerPartyId");
			}
			if(newFacCategory.equalsIgnoreCase("PARLOUR")){
				
				List conditionList = FastList.newInstance();
				conditionList = UtilMisc.toList( EntityCondition.makeCondition("productId", EntityOperator.IN, prodList));
				conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, newBoothId));
				conditionList.add(EntityCondition.makeCondition("isCancelled",  EntityOperator.EQUALS, null));
				  
				EntityCondition shipRecCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);	
				List<GenericValue> shipReceiptItems = delegator.findList("ShipmentReceiptAndItem", shipRecCond, null, null, null, false);
				  
				List<GenericValue> invoiceItemTypeList = null;
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, "TAX10"));
				conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.LIKE, "%_SALE"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

				invoiceItemTypeList = delegator.findList("InvoiceItemType",condition, null, null, null, false);
		        
				List invItemTypeList = EntityUtil.getFieldListFromEntityList(invoiceItemTypeList, "invoiceItemTypeId", false);
				
				Map<String, Object> createInvoiceContext = FastMap.newInstance();
		        createInvoiceContext.put("partyId", partyId);
		        createInvoiceContext.put("partyIdFrom", "TAX10");
		        createInvoiceContext.put("invoiceDate", supplyDateTimestamp);
		        createInvoiceContext.put("dueDate", supplyDateTimestamp);
		        createInvoiceContext.put("facilityId", newBoothId);
		        createInvoiceContext.put("shipmentId", shipmentId);
		        createInvoiceContext.put("invoiceTypeId", "STATUTORY_OUT");
		        createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
		        createInvoiceContext.put("userLogin", userLogin);
		        
				Iterator prodItemIter = productQuantMap.entrySet().iterator();
				
				while (prodItemIter.hasNext()) {
					
					Map.Entry prodIter = (Entry) prodItemIter.next();
					String prodId = (String)prodIter.getKey();
					BigDecimal quantity = (BigDecimal)prodIter.getValue();
					BigDecimal actualQty = BigDecimal.ZERO;
					List<GenericValue> prodShipReceipt = null;
					if(UtilValidate.isNotEmpty(shipReceiptItems)){
						prodShipReceipt = EntityUtil.filterByAnd(shipReceiptItems, UtilMisc.toMap("productId", prodId));
					}
					if (UtilValidate.isNotEmpty(prodShipReceipt)) {
				    	GenericValue shipRecItem = (GenericValue) prodShipReceipt.get(0);
				    	actualQty = (shipRecItem.getBigDecimal("quantityAccepted")).add(quantity);
				    	//BigDecimal diffQty = actualQty.subtract(shipRecItem.getBigDecimal("quantityAccepted"));
				    	
				    	String receiptId = shipRecItem.getString("receiptId");
						GenericValue shipReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
						shipReceipt.put("quantityAccepted", actualQty);
						shipReceipt.store();
				    	
						Map inputMap = FastMap.newInstance();
						inputMap.put("facilityId", newBoothId);
						inputMap.put("productId",prodId);
						inputMap.put("userLogin", userLogin);
						inputMap.put("quantity", quantity);
						inputMap.put("isReceipt", Boolean.TRUE);
						inputMap.put("effectiveDate", supplyDateTimestamp);
							
						try {
		        		     Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
		        			 if(ServiceUtil.isError(serviceResult)){
		        				 Debug.logError("Trouble in adjustInventory service", module);
								 return ServiceUtil.returnError("Trouble in adjustInventory service");
		        			 }
		        		} catch (GenericServiceException e) {
		        			 Debug.logError("Trouble in adjustInventory service", module);
							 return ServiceUtil.returnError("Trouble in adjustInventory service");
		        		}
				    	
					}
					else{
						Map<String, Object> priceResult;
				        Map<String, Object> priceContext = FastMap.newInstance();
				        priceContext.put("userLogin", userLogin);   
				        priceContext.put("productStoreId", productStoreId);               
				        priceContext.put("productId", prodId);
				        priceContext.put("priceDate", supplyDateTimestamp);
				        if(UtilValidate.isNotEmpty(productSubscriptionTypeId) && productSubscriptionTypeId.equalsIgnoreCase("REPLACEMENT_BYPROD")){
				        	Map categoryProductsMap = (Map) ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(),UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct");
				        	List unionProductList = (List)categoryProductsMap.get("UNION_PRODUCTS");
				        	if(unionProductList.contains(prodId)){
				        		priceContext.put("productPriceTypeId", "PM_RC_U_PRICE");
							}
				        	else{
				        		priceContext.put("productPriceTypeId", "PM_RC_W_PRICE");
				        	}
				        }
				        else{
				        	priceContext.put("facilityId", newBoothId);
				        }
				        priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);    
				        if (ServiceUtil.isError(priceResult)) {
				      	  	Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
				      	  	return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));
				        } 
				        BigDecimal unitCost = (BigDecimal)priceResult.get("price");

				        Map inputMap = FastMap.newInstance();
						inputMap.put("facilityId", newBoothId);
						inputMap.put("userLogin",userLogin);
						inputMap.put("productId",prodId);
						inputMap.put("quantity", quantity);
						inputMap.put("unitCost", unitCost);
						inputMap.put("shipmentId", shipmentId);
						inputMap.put("effectiveDate", supplyDateTimestamp);
						inputMap.put("isReceipt", Boolean.TRUE);
						inputMap.put("productSubscriptionTypeId", productSubscriptionTypeId);

						Map<String, Object> serviceResult = dispatcher.runSync("adjustInventory", inputMap);
						if(ServiceUtil.isError(serviceResult)){
							Debug.logError("Trouble in adjustInventory service", module);
							return ServiceUtil.returnError("Trouble in adjustInventory service");
						} 
					}
					

					List<GenericValue> prodPriceType = null;
			        
			        conditionList.clear();
			        conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
					conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "PM_RC_P"+"%"));
					conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, invItemTypeList));
					EntityCondition priceCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			        
			        prodPriceType = delegator.findList("ProductPriceAndType", priceCondition, null, null, null, false);
					String taxTypeId = null;
					
					if(UtilValidate.isEmpty(prodPriceType)){
						continue;
					}
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", prodId),false);
					GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", newBoothId),false);
					List<GenericValue> taxList = TaxAuthorityServices.getTaxAdjustmentByType(delegator, product, productStore, null, quantity, BigDecimal.ZERO, BigDecimal.ZERO, null, prodPriceType);
					itemAdjustments.add(taxList);
				}
				
				List<List<GenericValue>> consolidatedTaxList = null;
		        List<List<GenericValue>> itemAdj = null;
		        
		        if(UtilValidate.isNotEmpty(itemAdjustments)){
		        	 consolidatedTaxList = TaxAuthorityServices.consolidateItemAdjustments(itemAdjustments);
		        	 itemAdj = UtilGenerics.checkList(consolidatedTaxList.get(itemAdjustments.size() - 1));
		        }

		        if (itemAdj != null) {
		            for (int x = 0; x < itemAdj.size(); x++) {
		            	
		                GenericValue adjs = (GenericValue) itemAdj.get(x);
		                String invoiceId = null;
	                	Map<String, Object> createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
	                    invoiceId = (String) createInvoiceResult.get("invoiceId");
	                    if (ServiceUtil.isError(createInvoiceResult)) {
	                        return ServiceUtil.returnError("There was an error while creating Invoice" + ServiceUtil.getErrorMessage(createInvoiceResult));
	                    }
		                Map input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);
		                input.put("invoiceItemTypeId", adjs.get("orderAdjustmentTypeId")); 
		                input.put("quantity", BigDecimal.ONE);
		                input.put("amount", adjs.get("amount"));
		                
		                Map<String, Object> serviceResults;
	    				serviceResults = dispatcher.runSync("createInvoiceItem", input);
		                if (ServiceUtil.isError(serviceResults)) {
		                	return ServiceUtil.returnError("Unable to create Invoice Item", null, null, serviceResults);
		                }
		                Map<String, Object> setInvoiceStatusResult;
	        			setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", "INVOICE_READY", "userLogin", userLogin));
           			    if (ServiceUtil.isError(setInvoiceStatusResult)) {
           			    	return ServiceUtil.returnError("Unable to set Invoice Status", null, null, setInvoiceStatusResult);
		        		}
		            }
		        }
			}
			else{
				
				ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
	  			cart.setOrderType("SALES_ORDER");
	  			cart.setChannelType("BYPROD_SALES_CHANNEL");		
	  			cart.setBillToCustomerPartyId(partyId);
	  			cart.setPlacingCustomerPartyId(partyId);
	  			cart.setShipToCustomerPartyId(partyId);		
	  			cart.setEndUserCustomerPartyId(partyId);
	  			cart.setShipmentId(shipmentId);
	  			cart.setProductSubscriptionTypeId(productSubscriptionTypeId);
	  			cart.setEstimatedDeliveryDate(supplyDateTimestamp);
	  			cart.setOrderDate(supplyDateTimestamp);
	  			cart.setUserLogin(userLogin, dispatcher);
	  			cart.setFacilityId(newBoothId);
				
				List exprList = FastList.newInstance();
				exprList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, newBoothId));
				exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				exprList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
				exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
				exprList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
				EntityCondition previousOrderCondition = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		        List<GenericValue> existOrderForNewFacility = delegator.findList("OrderHeaderItemProductShipmentAndFacility", previousOrderCondition, UtilMisc.toSet("orderId","productId","quantity"), null, null, false);
				if(UtilValidate.isNotEmpty(existOrderForNewFacility)){
					for(int i=0;i<existOrderForNewFacility.size();i++){
						GenericValue prodMap = existOrderForNewFacility.get(i);
						String existProd = (String)prodMap.get("productId");
						BigDecimal existQuant = (BigDecimal)prodMap.get("quantity");
						if(productQuantMap.containsKey(existProd)){
							BigDecimal tempQuant = (BigDecimal)productQuantMap.get(existProd);
							BigDecimal totalQuant = tempQuant.add(existQuant);
							productQuantMap.put(existProd, totalQuant);
						}
						else{
							productQuantMap.put(existProd, existQuant);
						}
					}
				}
				List existOrderForTheDay = EntityUtil.getFieldListFromEntityList(existOrderForNewFacility, "orderId", true);
	    		//to get all invoices for the shipment
				if(UtilValidate.isNotEmpty(existOrderForTheDay)){
					String existOrderId = (String)existOrderForTheDay.get(0);
					List existInvoice = delegator.findList("OrderHeaderFacAndItemBillingInv", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, existOrderId), null, null, null, false);   
					List existInvoiceList = EntityUtil.getFieldListFromEntityList(existInvoice, "invoiceId", true);    		
					if(UtilValidate.isNotEmpty(existInvoiceList)){
						result = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", existInvoiceList, "statusId","INVOICE_CANCELLED","userLogin", userLogin));
		        		if (ServiceUtil.isError(result)) {
				    		 Debug.logError("There was an error while Cancel  the invoices: " + ServiceUtil.getErrorMessage(result), module);	               
					         return ServiceUtil.returnError("There was an error while Cancel the invoices: ");   			 
		        		}
					}
					result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", existOrderForTheDay,"userLogin", userLogin));
					if (ServiceUtil.isError(result)) {
				       	   Debug.logError("There was an error while Cancel  the Orders:" + ServiceUtil.getErrorMessage(result), module);	               
				           return ServiceUtil.returnError("There was an error while Cancel  the Orders: ");  
				    } 			
				}
				Iterator prodMapIter = productQuantMap.entrySet().iterator();
		  		while (prodMapIter.hasNext()) {
		  			Map.Entry eachEntry = (Entry) prodMapIter.next();
		  			String productId = (String) eachEntry.getKey();
		  			BigDecimal quantity = (BigDecimal) eachEntry.getValue();
		  					
					Map<String, Object> priceResult;
					Map<String, Object> priceContext = FastMap.newInstance();
					priceContext.put("userLogin", userLogin);                 
					priceContext.put("productStoreId", productStoreId);           
					priceContext.put("productId", productId);
					priceContext.put("partyId", partyId);
					priceContext.put("priceDate", supplyDateTimestamp);
					priceContext.put("facilityId", newBoothId);
					priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext); 
					if (ServiceUtil.isError(priceResult)) {
		  				Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
		  				return ServiceUtil.returnError("There was an error while calculating the price");
					}
					 try{
						 cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null,	quantity, (BigDecimal)priceResult.get("price"),
						            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
						            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
					 }  
					 catch (Exception exc) {
						 Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
						 return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: ");
		             }
		  			
		  		}//end of while
		  		cart.setDefaultCheckoutOptions(dispatcher);
		  		CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator,cart);
		  		
		  		List<GenericValue> applicableTaxTypes = null;
		  		
		  		applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
				
		  		List applicableTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);
				
		  		List conditionList = FastList.newInstance();
		        
		        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.LIKE, "PM_RC_%"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				
		  		List<GenericValue> partyClassificationList = null;
				try{
					partyClassificationList = delegator.findList("PartyClassification", condition, null, null, null, false);
				}catch(GenericEntityException e){
					Debug.logError("No partyRole found for given partyId:"+ partyId, module);
					return ServiceUtil.returnError("No partyRole found for given partyId");
				}
				
				String productPriceTypeId = null;
				
				if (UtilValidate.isNotEmpty(partyClassificationList)) {
					GenericValue partyClassification = partyClassificationList.get(0);
					productPriceTypeId = (String) partyClassification.get("partyClassificationGroupId");
					/*productPriceTypeId = productPriceTypeId+"_PRICE";*/
				}
		  		
		  		List<GenericValue> prodPriceType = null;
		        List condList = FastList.newInstance();
		        condList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, productPriceTypeId+"%"));
		        condList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, applicableTaxTypeList));
				EntityCondition condition2 = EntityCondition.makeCondition(condList,EntityOperator.AND);

				prodPriceType = delegator.findList("ProductPriceAndType", condition2, null, null, null, false);
				
				checkout.calcAndAddTax(prodPriceType);
				
				Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
		  		String orderId = (String) orderCreateResult.get("orderId");
		  		
		  		 // approve the order
		        if (UtilValidate.isNotEmpty(orderId)) {
		            boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);   
		            
		            Map<String, Object> resultMap = null;
		            try{            	
		        		resultMap = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", orderId,"userLogin", userLogin));
		        		if (ServiceUtil.isError(resultMap)) {
		        			Debug.logError("There was an error while creating Invoice: " + ServiceUtil.getErrorMessage(resultMap), module);
		    				return ServiceUtil.returnError("There was an error while creating Invoice: ");
		                } 
			        	Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", resultMap.get("invoiceId"));
			            invoiceCtx.put("userLogin", userLogin);
			            invoiceCtx.put("statusId","INVOICE_READY");
			            try{
			                Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
			             	if (ServiceUtil.isError(invoiceResult)) {
			             		Debug.logError("There was an error while setting Invoice status: " + ServiceUtil.getErrorMessage(invoiceResult), module);
			    				return ServiceUtil.returnError("There was an error while setting Invoice status: ");
			                }	             	
			            }catch(GenericServiceException e){
			            	 Debug.logError(e, module);
			            }        		
		        		// apply invoice if any adavance payments from this  party
						  			            
						Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", (String)resultMap.get("invoiceId"),"userLogin", userLogin));
						if (ServiceUtil.isError(resultPaymentApp)) {						  
							Debug.logError("There was an error while settling Invoice Payments: " + ServiceUtil.getErrorMessage(resultPaymentApp), module);
		    				return ServiceUtil.returnError("There was an error while settling Invoice Payments: ");
				        }				           
				          
		            }catch (GenericServiceException e) {
		                Debug.logError(e, module);
		            } 
		        }
		  		
			}
			
		}catch(GenericEntityException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());  
		}
		catch (GeneralException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		result = ServiceUtil.returnSuccess("Successfully changed order for "+facilityId+" to Party Code : "+newBoothId);
		return result;
	}
	
	public static String makeTransporterPayment(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
	      Timestamp todayDayStart = UtilDateTime.getDayStart(nowTimeStamp);
	  	  List custTimePeriodList = FastList.newInstance();  	 
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  String paidAmountStr = (String) request.getParameter("paidAmt");
	  	  String shortAmountStr = (String) request.getParameter("shortAmt");
	  	  BigDecimal paidAmount = BigDecimal.ZERO;
	  	  BigDecimal shortAmount = BigDecimal.ZERO;
	  	  if(UtilValidate.isNotEmpty(paidAmountStr)){
			  paidAmount = new BigDecimal(paidAmountStr);
		  }
	  	  if(UtilValidate.isNotEmpty(shortAmountStr)){
			  shortAmount = new BigDecimal(shortAmountStr);
		  } 
	  	  
	  	  String partyCode = (String) request.getParameter("partyCode");
	  	  boolean beganTransaction = false;
	  	  try{
	  		  beganTransaction = TransactionUtil.begin();
	  		  BigDecimal dealerGrandPayment = BigDecimal.ZERO;
		  	  for (int i = 0; i < rowCount; i++){
		  		  
		  		  String facilityId = "";
		  		  String paymentMethodTypeId = "";
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  BigDecimal amount = BigDecimal.ZERO;
		  		  String amountStr = "";
		  		  BigDecimal pastAmount = BigDecimal.ZERO;
		  		  String pastAmountStr = "";
		  		  if (paramMap.containsKey("facilityId" + thisSuffix)) {
		  			  facilityId = (String) paramMap.get("facilityId" + thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("paymentMethodTypeId" + thisSuffix)) {
		  			paymentMethodTypeId = (String) paramMap.get("paymentMethodTypeId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("amount" + thisSuffix)) {
		  			amountStr = (String) paramMap.get("amount"+thisSuffix);
		  		  }
		  		  if(UtilValidate.isNotEmpty(amountStr)){
		  			  try {
			  			  amount = new BigDecimal(amountStr);
			  		  } catch (Exception e) {
			  			  Debug.logError(e, "Problems parsing amount string: " + amountStr, module);
			  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + amountStr);
			  			  TransactionUtil.rollback();
			  			  return "error";
			  		  }
		  		  }
		  		  if (paramMap.containsKey("pastAmount" + thisSuffix)) {
		  			pastAmountStr = (String) paramMap.get("pastAmount"+thisSuffix);
		  		  }
		  		  if(UtilValidate.isNotEmpty(pastAmountStr)){
		  				pastAmount = new BigDecimal(pastAmountStr);
			  		  
		  		  }
		  		  
		  		  BigDecimal totalAmount = amount.add(pastAmount);
		  		  dealerGrandPayment = dealerGrandPayment.add(totalAmount);
		  		  if(totalAmount.compareTo(BigDecimal.ZERO)>0){
		  			  
		  			  List invoiceIdList = FastList.newInstance();
		  			  String statusId = "PMNT_RECEIVED";            
		  			  String paymentTypeId = "SALES_PAYIN";
		  			  String partyIdFrom = "";
		  			  if(UtilValidate.isEmpty(partyIdFrom)){
		  				  GenericValue partyFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false );
		  				  if(UtilValidate.isNotEmpty(partyFacility)){
		  					partyIdFrom = partyFacility.getString("ownerPartyId");
		  				  }
		  			  }
		  		   
		  			  Map resultMap = FastMap.newInstance();
		  			  List statusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_PAID","INVOICE_WRITEOFF");
		  			  List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom));
		  			  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, statusList));
		  			  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		  			  List invoices = delegator.findList("Invoice", condition, UtilMisc.toSet("invoiceId"), UtilMisc.toList("-dueDate"), null, false);
		  	          invoices = EntityUtil.getFieldListFromEntityList(invoices,"invoiceId",true);
		  	          Map input = UtilMisc.toMap("userLogin", userLogin, "partyId", partyIdFrom, "facilityId", facilityId, "paymentPurposeType", null, "organizationPartyId", "Company", "effectiveDate", todayDayStart, "paymentDate", nowTimeStamp, "paymentTypeId", paymentTypeId, "paymentMethodTypeId", paymentMethodTypeId, "statusId", statusId, "paymentRefNum", null, "issuingAuthority", null, "issuingAuthorityBranch", null, "amount", totalAmount, "invoices", invoices);
		  	          String paymentId = null;
		  	          resultMap = dispatcher.runSync("createPaymentAndApplicationForInvoices", input);
		  	          if (ServiceUtil.isError(resultMap)) {
		  	        	  Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			  			  request.setAttribute("_ERROR_MESSAGE_", "Payment Unsuccessful for Dealer: " +facilityId);
			  			  TransactionUtil.rollback();
			  			  return "error";
		  	          }
		  	          paymentId = (String) resultMap.get("paymentId");
		  	          Debug.log("payment created for facility =="+facilityId+"-->"+paymentId);
		  		  }
		  	 }//end of loop
		  	 GenericValue routeFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",partyCode),false);
		  	 if(UtilValidate.isNotEmpty(routeFacility)){
		  		 String boothType = routeFacility.getString("facilityTypeId");
		  		 if(boothType.equals("ROUTE")){
		  			 if(shortAmount.compareTo(BigDecimal.ZERO) > 0){
		  				
		  				Map inputCtx = UtilMisc.toMap("userLogin", userLogin, "supplyDate", todayDayStart, "facilityId", partyCode, "amount", shortAmount);
			  	        String invoiceId = null;
			  	        result = dispatcher.runSync("CreateTransporterDue", inputCtx);
			  	        if (ServiceUtil.isError(result)) {
			  	          Debug.logError(ServiceUtil.getErrorMessage(result), module);
				  		  request.setAttribute("_ERROR_MESSAGE_", "Transporter short payment invoice unsuccessful: "+partyCode);
				  		  TransactionUtil.rollback();
				  		  return "error";
			  	        }
			  	        invoiceId = (String) result.get("invoiceId");
			  	        Debug.log("short payment invoice =="+partyCode+"-->"+invoiceId);
		  			 }
		  			 
		  			 if(paidAmount.compareTo(dealerGrandPayment)>0){
		  				
		  				Map inputMap = UtilMisc.toMap("userLogin", userLogin, "facilityId", partyCode, "amount", paidAmount.subtract(dealerGrandPayment));
			  	        String paymentId = null;
			  	        result = dispatcher.runSync("createTransporterDuePayment", inputMap);
			  	        if (ServiceUtil.isError(result)) {
			  	          Debug.logError(ServiceUtil.getErrorMessage(result), module);
				  		  request.setAttribute("_ERROR_MESSAGE_", "Transporter payment unsuccessful: "+partyCode);
				  		  TransactionUtil.rollback();
				  		  return "error";
			  	        }
			  	        paymentId = (String) result.get("paymentId");
			  	        Debug.log("transporter payment =="+partyCode+"-->"+paymentId);
			  	        
		  			 }
		  		 }
		  	 }
	  	  } catch (GenericEntityException e) {
	  		  try {
	  			  // only rollback the transaction if we started one...
	  			  TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  		  } catch (GenericEntityException e2) {
	  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		  }
	  		  Debug.logError("An entity engine error occurred while fetching data", module);
	  	  }
	  	  catch (GenericServiceException e) {
	  		  try {
	  			  // only rollback the transaction if we started one...
	  			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
	  		  } catch (GenericEntityException e2) {
	  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		  }
	  		  Debug.logError("An entity engine error occurred while calling services", module);
	  	  }
	  	  finally {
	  		  // only commit the transaction if we started one... this will throw an exception if it fails
	  		  try {
	  			  TransactionUtil.commit(beganTransaction);
	  		  } catch (GenericEntityException e) {
	  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  		  }
	  	  }
	  	  request.setAttribute("_EVENT_MESSAGE_", "Successfully made entry from party: "+partyCode);
	  	  return "success";     
	}
	public static String processDSCorrectionMIS(HttpServletRequest request, HttpServletResponse response) {
  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
  	  Locale locale = UtilHttp.getLocale(request);
  	  String boothId = (String) request.getParameter("boothId");
  	  String tripId = (String) request.getParameter("tripId");
  	  String routeId = (String) request.getParameter("routeId");
  	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
  	  String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
  	  String subscriptionTypeId = (String) request.getParameter("subscriptionTypeId");
  	  String shipmentTypeId = "AM_SHIPMENT";
  	  if(UtilValidate.isNotEmpty(request.getParameter("shipmentTypeId"))){
  		  shipmentTypeId = (String) request.getParameter("shipmentTypeId");
  	  }
  	  String productId = null;
  	  String quantityStr = null;
  	  String sequenceNum = null;	  
  	  Timestamp effectiveDate=null;
  	  Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
  	  BigDecimal quantity = BigDecimal.ZERO;
  	  List<GenericValue> subscriptionList=FastList.newInstance();
  	  Map<String, Object> result = ServiceUtil.returnSuccess();
  	  HttpSession session = request.getSession();
  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
  	  GenericValue subscription = null;
  	  GenericValue facility = null;
  	  List custTimePeriodList = FastList.newInstance(); 
  	  List conditionList =FastList.newInstance();
  	  String shipmentId = "";
  	  String orderId = "";
  	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
  		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
  		  try {
  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
  		  } catch (ParseException e) {
  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
              // effectiveDate = UtilDateTime.nowTimestamp();
  		  } catch (NullPointerException e) {
  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
               //effectiveDate = UtilDateTime.nowTimestamp();
  		  }
  	  }
  	  if (boothId == "") {
  			request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
  			return "error";
  		}
      
      // Get the parameters as a MAP, remove the productId and quantity params.
  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
  	  if (rowCount < 1) {
  		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
  		  return "success";
  	  }
  	  try{
  		  facility=delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), false);
  		  if(UtilValidate.isEmpty(facility)){
  			  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
  			  return "error";
  		  }
  		  //lets override productSubscriptionTypeId based on facility category
  		  if(facility.getString("categoryTypeEnum").equals("SO_INST")){
  			  productSubscriptionTypeId = "SPECIAL_ORDER";
  		  }else if(facility.getString("categoryTypeEnum").equals("CR_INST")){
  			 productSubscriptionTypeId = "CREDIT";
 		  }
  	  }catch (GenericEntityException e) {
  		  Debug.logError(e, "Booth does not exist", module);
  		  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
  		  return "error";
  	  }
  	  try {
  		  
  		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
  		  conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, subscriptionTypeId+"_SHIPMENT"));
  		  conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.EQUALS, effectiveDate));
  		  conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
  		  if(UtilValidate.isNotEmpty(tripId)){
  			  conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
  		  }
  		  EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
  		  List<GenericValue> shipments = delegator.findList("Shipment", condition, UtilMisc.toSet("shipmentId"), null, null, false);
  		  if(UtilValidate.isEmpty(shipments)){
  			  Debug.logError("No shipment found for the dealer "+boothId, module);
      		  request.setAttribute("_ERROR_MESSAGE_", "No shipment found for the dealer "+boothId);
      		  return "error";
  		  }
  		  shipmentId = (EntityUtil.getFirst(shipments)).getString("shipmentId");
	    		
  		  conditionList.clear();
  		  conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, boothId));
  		  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
  		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
  		  EntityCondition orderCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
  		  List<GenericValue> orderHeader = delegator.findList("OrderHeader", orderCond, null, null, null, false);
  		  if(UtilValidate.isEmpty(orderHeader)){
  			  Debug.logError("No Order found for dealer  "+boothId, module);
  			  request.setAttribute("_ERROR_MESSAGE_", "No Order found for dealer  "+boothId);
  			  return "error";     		
  		  }
  		  orderId = (EntityUtil.getFirst(orderHeader)).getString("orderId");
  	  }  catch (GenericEntityException e) {
  		  Debug.logError(e, "Problem fetching data from Entity", module);
  		  request.setAttribute("_ERROR_MESSAGE_", "Problem fetching data from Entity");
  		  return "error";	
  	  }
  	
  	  List<Map>productQtyList =FastList.newInstance();
  	  for (int i = 0; i < rowCount; i++) {
  		  Map<String  ,Object> productQtyMap = FastMap.newInstance();
  		  
  		  List<GenericValue> subscriptionProductsList = FastList.newInstance();
  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
  		  if (paramMap.containsKey("productId" + thisSuffix)) {
  			  productId = (String) paramMap.get("productId" + thisSuffix);
  		  }
  		  else {
  			  request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
  			  return "error";			  
  		  }
  		  if (paramMap.containsKey("sequenceNum" + thisSuffix)) {
  			  sequenceNum = (String) paramMap.get("sequenceNum" + thisSuffix);
  		  }	
  		  else {
  			  request.setAttribute("_ERROR_MESSAGE_", "Missing sequence number");
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
  		  try {
  			  quantity = new BigDecimal(quantityStr);
  		  } catch (Exception e) {
  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
  			  return "error";
  		  } 
  		  
  		  productQtyMap.put("productId", productId);
  		  productQtyMap.put("quantity", quantity);
  		  productQtyMap.put("sequenceNum", sequenceNum);
  		  productQtyList.add(productQtyMap);
  	 }//end row count for loop
  
  	 Map processDSCorrectionHelperCtx = UtilMisc.toMap("userLogin",userLogin);
  	 processDSCorrectionHelperCtx.put("orderId", orderId);
  	 processDSCorrectionHelperCtx.put("shipmentId", shipmentId);
  	 processDSCorrectionHelperCtx.put("boothId", boothId);
  	 processDSCorrectionHelperCtx.put("shipmentTypeId", shipmentTypeId);
  	 processDSCorrectionHelperCtx.put("effectiveDate", effectiveDate);
  	 processDSCorrectionHelperCtx.put("productQtyList", productQtyList);
  	 processDSCorrectionHelperCtx.put("productSubscriptionTypeId", productSubscriptionTypeId);
  	 String indentChanged = "";
  	 try{
  		 result = dispatcher.runSync("processDSCorrectionHelper",processDSCorrectionHelperCtx);
  		 
  		 if (ServiceUtil.isError(result)) {
  			 String errMsg =  ServiceUtil.getErrorMessage(result);
  			 Debug.logError(errMsg , module);
  			 request.setAttribute("_ERROR_MESSAGE_",errMsg);
  			 return "error";
  		 }
  		 indentChanged = (String)result.get("indentChangeFlag");
		 
  	 }catch (Exception e) {
  		 Debug.logError(e, "Problem updating order for dealer " + boothId, module);     
  		 request.setAttribute("_ERROR_MESSAGE_", "Problem updating order for dealer " + boothId);
  		 return "error";			  
  	 }	 
  	 request.setAttribute("indentChangeFlag", indentChanged);
  	 return "success";     
	}
	
	public static Map<String, Object> updateCrateQtyConfig(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Double liters = (Double) context.get("liters");
        Locale locale = (Locale) context.get("locale");     
        String uomId = (String) context.get("uomId");
        String uomIdTo = (String) context.get("uomIdTo");
        Map result = ServiceUtil.returnSuccess();
        if(UtilValidate.isEmpty(liters)){
        	Debug.logError("Liters field is Empty, nothing to update", module);
            return ServiceUtil.returnError("Liters field is Empty, nothing to update");
        }
        if(UtilValidate.isEmpty(uomId) || UtilValidate.isEmpty(uomIdTo)){
   			Debug.logError("uomId is Empty", module);
   			return ServiceUtil.returnError("uomId is Empty");
   		}
        GenericValue uomConversion = null;
		try {
			uomConversion = delegator.findOne("UomConversion", UtilMisc.toMap("uomId", uomId, "uomIdTo", uomIdTo), false);
			if(UtilValidate.isEmpty(uomConversion)){
				GenericValue newEntity = delegator.makeValue("UomConversion");
				newEntity.set("uomId", uomId);
				newEntity.set("uomIdTo", uomIdTo);
				newEntity.set("conversionFactor", liters);
				newEntity.create();
			}
			else{
				uomConversion.set("conversionFactor", liters);
				uomConversion.store();
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
        result = ServiceUtil.returnSuccess("Successfully Updated!!");
        return result;
    }
	
	
	public static Map<String, Object> adjustEmployeeSubsidyForOrder(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");     
        Map result = ServiceUtil.returnSuccess();
        BigDecimal subPercent = new BigDecimal("50");
        BigDecimal subAmount = BigDecimal.ZERO;
		try {
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			List<GenericValue> orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
			for(GenericValue orederItem : orderItems){
				BigDecimal itemSubAmt = orederItem.getBigDecimal("unitPrice").multiply(subPercent.divide(new BigDecimal("100")));
				subAmount = (subAmount.add(itemSubAmt)).multiply(orederItem.getBigDecimal("quantity"));
			}
			// add employee subsidy adjustment "EMPSUBSID_ADJUSTMENT"
			String orderAdjustmentTypeId = "EMPSUBSID_ADJUSTMENT";
			 Map createOrderAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
	    	 createOrderAdjustmentCtx.put("orderId", orderId);
	    	 createOrderAdjustmentCtx.put("orderAdjustmentTypeId", orderAdjustmentTypeId);    	
	    	 createOrderAdjustmentCtx.put("amount", subAmount.negate());
	    	 result = dispatcher.runSync("createOrderAdjustment", createOrderAdjustmentCtx);
	     	 if (ServiceUtil.isError(result)) {
	                Debug.logWarning("There was an error while creating  the adjustment: " + ServiceUtil.getErrorMessage(result), module);
	         		return ServiceUtil.returnError("There was an error while creating the adjustment: " + ServiceUtil.getErrorMessage(result));          	            
	         } 
		
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
        result = ServiceUtil.returnSuccess("Successfully added the adjustment!!");
        result.put("orderId", orderId);
        return result;
    }
}
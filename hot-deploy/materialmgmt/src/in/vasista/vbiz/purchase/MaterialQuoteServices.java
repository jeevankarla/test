package in.vasista.vbiz.purchase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

public class MaterialQuoteServices {

	public static final String module = MaterialQuoteServices.class.getName();
	
	public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
	public static final BigDecimal ONE_BASE = BigDecimal.ONE;
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
	public static int salestaxFinalDecimals = UtilNumber.getBigDecimalScale("salestax.final.decimals");
	public static int salestaxCalcDecimals = 2;//UtilNumber.getBigDecimalScale("salestax.calc.decimals");
	
	public static int salestaxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
	
	public static String createQuoteForEnquiry(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String issueDateStr = (String) request.getParameter("issueDate");
	    String quoteType = (String) request.getParameter("quoteType");
	    String quoteName = (String) request.getParameter("quoteName");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	    String partyId = (String) request.getParameter("partyId");
	  	
	    String validFromDateStr = (String) request.getParameter("validFromDate");
	    String validThruDateStr = (String) request.getParameter("validThruDate");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create quotation without partyId: "+ partyId, module);
			return "error";
		}
		Timestamp issueDate = null;
		Timestamp validFromDate = null;
		Timestamp validThruDate = null;
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
	  	if(UtilValidate.isNotEmpty(issueDateStr)){
	  		try {
	  			issueDate = new java.sql.Timestamp(sdf.parse(issueDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + issueDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + issueDateStr, module);
		  	}
	  	}
	  	else{
	  		issueDate = UtilDateTime.nowTimestamp();
	  	}
	  	if(UtilValidate.isNotEmpty(validFromDateStr)){
	  		try {
	  			validFromDate = new java.sql.Timestamp(sdf.parse(validFromDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + validFromDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + validFromDateStr, module);
		  	}
	  	}
	  	else{
	  		validFromDate = UtilDateTime.nowTimestamp();
	  	}
	  	if(UtilValidate.isNotEmpty(validThruDateStr)){
	  		try {
	  			validThruDate = new java.sql.Timestamp(sdf.parse(validThruDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + validThruDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + validThruDateStr, module);
		  	}
	  	}
	  	
		boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			GenericValue party = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "SUPPLIER"), false);
			if(UtilValidate.isEmpty(party)){
				Debug.logError("Quotation allowed only for party with role supplier : "+partyId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Quotation allowed only for party with role supplier : "+partyId);
				TransactionUtil.rollback();
		  		return "error";
			}
			
			String productId = "";
	        String quantityStr = "";
	        String custRequestId = "";
	        String custRequestItemSeqId = "";
	        String priceStr = "";
			BigDecimal quantity = BigDecimal.ZERO;
			BigDecimal price = BigDecimal.ZERO;
			List<Map> quoteItemList = FastList.newInstance();
			
			for (int i = 0; i < rowCount; i++) {
				  
				Map quoteItemMap = FastMap.newInstance();
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
				
				if (paramMap.containsKey("price" + thisSuffix)) {
					priceStr = (String) paramMap.get("price" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing product price");
					return "error";			  
				}		  
				if(UtilValidate.isNotEmpty(priceStr)){
					price = new BigDecimal(priceStr);
				}
				if (paramMap.containsKey("custRequestId" + thisSuffix)) {
					custRequestId = (String) paramMap.get("custRequestId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing custRequestId");
					return "error";			  
				}
				if (paramMap.containsKey("custRequestItemSeqId" + thisSuffix)) {
					custRequestItemSeqId = (String) paramMap.get("custRequestItemSeqId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing custRequestItemSeqId");
					return "error";			  
				}
				quoteItemMap.put("custRequestId",custRequestId);
				quoteItemMap.put("custRequestItemSeqId", custRequestItemSeqId);
				quoteItemMap.put("productId", productId);
		        quoteItemMap.put("quantity",quantity);
		        quoteItemMap.put("price", price);
		        
		        quoteItemList.add(quoteItemMap);
			}
			
			Map inputCtx = FastMap.newInstance();
			inputCtx.put("quoteItemList", quoteItemList);
			inputCtx.put("quoteTypeId", quoteType);
			inputCtx.put("quoteName", quoteName);
			inputCtx.put("issueDate", issueDate);
			inputCtx.put("validFromDate", validFromDate);
			inputCtx.put("validThruDate", validThruDate);
			inputCtx.put("partyId", partyId);
			inputCtx.put("userLogin", userLogin);
			
			result = dispatcher.runSync("createQuoteAndItems", inputCtx);
			
			if(ServiceUtil.isError(result)){
				Debug.logError(ServiceUtil.getErrorMessage(result), module);
	  			request.setAttribute("_ERROR_MESSAGE_", "Error creating Quote and Items for Supplier : " +partyId);
	  			TransactionUtil.rollback();
	  			return "error";
			}
			
		}
		catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_", "Error in fetching data ");
			try {
				TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  		return "error";
	  	}
  	  	catch (GenericServiceException e) {
  	  		request.setAttribute("_ERROR_MESSAGE_", "Error in service createQuoteAndItems ");
  	  		try {
  			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
  	  		} catch (GenericEntityException e2) {
  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  	  		}
  	  		Debug.logError("An service engine error occurred while calling services", module);
  	  		return "error";
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
	
	public static Map<String,Object> setQuoteAndItemStatus(DispatchContext ctx, Map<String, ? extends Object> context){
    	Map<String, Object> result = FastMap.newInstance();
        Map<String, Object> inMap  = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String,Object> quoteResult = FastMap.newInstance();
        String quoteId = (String)context.get("quoteId");
        String quoteItemSeqId = (String)context.get("quoteItemSeqId");
        String statusId = (String)context.get("statusId");
        try{
        	if(UtilValidate.isEmpty(quoteId)){
        		Debug.logError("Error in  setting Quote status for empty quoteId", module);
  	  			return ServiceUtil.returnError("Error in  setting Quote status for empty quoteId");
        	}
        	if(UtilValidate.isEmpty(statusId)){
        		Debug.logError("Error in  setting Quote Item status for empty status", module);
  	  			return ServiceUtil.returnError("Error in  setting Quote status for empty status");
        	}
        	GenericValue quote = null;
        	String oldStatusId = "";
        	if(UtilValidate.isNotEmpty(quoteItemSeqId)){
        		quote = delegator.findOne("QuoteItem", UtilMisc.toMap("quoteId", quoteId, "quoteItemSeqId", quoteItemSeqId), false);
        		oldStatusId = quote.getString("statusId");
        	}
        	else{
        		quote = delegator.findOne("Quote", UtilMisc.toMap("quoteId", quoteId), false);
        		oldStatusId = quote.getString("statusId");
        	}
        	if(!(statusId.equals("QTITM_CREATED") || statusId.equals("QUO_CREATED"))){
        		GenericValue statusValidChange = delegator.findOne("StatusValidChange", UtilMisc.toMap("statusId",oldStatusId, "statusIdTo", statusId), false);
            	if(UtilValidate.isEmpty(statusValidChange)){
            		Debug.logError("Not a valid status change for quote", module);
      	  			return ServiceUtil.returnError("Not a valid status change for quote");
            	}
        	}
        	
        	quote.set("statusId", statusId);
        	quote.store();
        	
        	result = dispatcher.runSync("createQuoteStatus", UtilMisc.toMap("userLogin", userLogin, "quoteId", quoteId, "quoteItemSeqId", quoteItemSeqId, "statusId", statusId));
        	if(ServiceUtil.isError(result)){
        		Debug.logError("Error updating QuoteStatus", module);
  	  			return ServiceUtil.returnError("Error updating QuoteStatus");
        	}
        	
        }catch(Exception e){
        	Debug.logError("Error updating quote status", module);
		    return ServiceUtil.returnError("Error updating quote status");
        }
		return result;
    }//End of Service
	

	public static Map<String,Object> createQuoteAndItems(DispatchContext ctx, Map<String, ? extends Object> context){
    	Map<String, Object> result = FastMap.newInstance();
        Map<String, Object> inMap  = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String,Object> quoteResult = FastMap.newInstance();
        List<Map> quoteItemList = (List) context.get("quoteItemList");
        String partyId = (String)context.get("partyId");
        String quoteTypeId = (String)context.get("quoteTypeId");
        String quoteName = (String)context.get("quoteName");
        Timestamp validFromDate = (Timestamp)context.get("validFromDate");
        Timestamp validThruDate = (Timestamp)context.get("validThruDate");
        Timestamp issueDate = (Timestamp)context.get("issueDate");
        try{
        	Map quoteInputCtx = FastMap.newInstance();
        	quoteInputCtx.put("userLogin", userLogin);
        	quoteInputCtx.put("partyId", partyId);
        	quoteInputCtx.put("quoteTypeId", quoteTypeId);
        	quoteInputCtx.put("quoteName", quoteName);
        	quoteInputCtx.put("validFromDate", validFromDate);
        	quoteInputCtx.put("validThruDate", validThruDate);
        	quoteInputCtx.put("issueDate", issueDate);
        	quoteResult = dispatcher.runSync("createQuote", quoteInputCtx);
        	if(ServiceUtil.isError(quoteResult)){
        		Debug.logError("Error in  creating Quote for enquiry", module);
  	  			return ServiceUtil.returnError("Error in  creating Quote for enquiry");
        	}
        }catch(GenericServiceException e){
        	Debug.logError("Error Creating quote", module);
		    return ServiceUtil.returnError("Error Creating quote");
        }
        String quoteId=(String)quoteResult.get("quoteId");
    	for(Map tempMap : quoteItemList){
    		Map inputctx = FastMap.newInstance();
    		inputctx.put("userLogin",userLogin);
    		inputctx.put("custRequestId",(String) tempMap.get("custRequestId"));
    		inputctx.put("custRequestItemSeqId", (String) tempMap.get("custRequestItemSeqId"));
    		inputctx.put("productId", (String) tempMap.get("productId"));
    		inputctx.put("quantity", (BigDecimal) tempMap.get("quantity"));
    		inputctx.put("quoteId",quoteId);
    		inputctx.put("quoteUnitPrice", (BigDecimal) tempMap.get("price"));
      	  	Map<String,Object> quoteItemResult = FastMap.newInstance();
      	  	try{
      	  		quoteItemResult = dispatcher.runSync("createQuoteItem", inputctx); 
      	  		
      	  		if(ServiceUtil.isError(quoteItemResult)){
      	  			Debug.logError("Problem creating quoteItem for enquiry :"+(String) tempMap.get("custRequestId"), module);
      	  			return ServiceUtil.returnError("Problem creating quoteItem for enquiry :"+(String) tempMap.get("custRequestId"));
      	  		}
      	  	}catch(GenericServiceException e){
      	    	Debug.log("Error while creating Quote Item");
      	    	return ServiceUtil.returnError("Error Creating quote Item");
      	    }
      	  	
    	}
    	result = ServiceUtil.returnSuccess("Items added to quote Successfully with Id: "+quoteId);
		return result;
    }//End of Service
	
}
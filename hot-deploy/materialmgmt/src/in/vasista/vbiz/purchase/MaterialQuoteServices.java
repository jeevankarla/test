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
import org.ofbiz.accounting.util.UtilAccounting;
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
	public static int purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
	public static int purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
	
	public static int purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
	
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
			
			String quoteId = (String)result.get("quoteId");
			
			request.setAttribute("quoteId", quoteId);
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
	
	public static Map<String,Object> changeQuoteItemStatus(DispatchContext ctx, Map<String, ? extends Object> context){
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String quoteId = (String)context.get("quoteId");
        String quoteItemSeqId = (String)context.get("quoteItemSeqId");
        String statusId = (String)context.get("statusId");
        String custRequestId = (String)context.get("custRequestId");
        String comments = (String)context.get("comments");
        
        try{
        	Map inputMap = FastMap.newInstance();
        	inputMap.put("userLogin", userLogin);
        	inputMap.put("quoteId", quoteId);
        	inputMap.put("quoteItemSeqId", quoteItemSeqId);
        	inputMap.put("statusId", statusId);
        	inputMap.put("comments", comments);
        	
        	result = dispatcher.runSync("setQuoteAndItemStatus", inputMap);
        	if(ServiceUtil.isError(result)){
        		Debug.logError("Error updating QuoteStatus", module);
  	  			return ServiceUtil.returnError("Error updating QuoteStatus");
        	}
        	
        }catch(Exception e){
        	Debug.logError("Error updating quote status", module);
		    return ServiceUtil.returnError("Error updating quote status");
        }
        result = ServiceUtil.returnSuccess("Updated Quote Status!");
        result.put("custRequestId", custRequestId);
		return result;
    }//End of Service
	
	public static Map<String,Object> quoteNegotiateAndStatusChange(DispatchContext ctx, Map<String, ? extends Object> context){
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String quoteId = (String)context.get("quoteId");
        String quoteItemSeqId = (String)context.get("quoteItemSeqId");
        BigDecimal quoteUnitPrice = (BigDecimal)context.get("quoteUnitPrice");
        String custRequestId = (String)context.get("custRequestId");
        try{
        	
        	GenericValue quoteItem = delegator.findOne("QuoteItem", UtilMisc.toMap("quoteId", quoteId, "quoteItemSeqId", quoteItemSeqId), false);
        	
        	quoteItem.set("quoteUnitPrice", quoteUnitPrice);
        	quoteItem.store();
        	
        	Map inputMap = FastMap.newInstance();
        	inputMap.put("userLogin", userLogin);
        	inputMap.put("quoteId", quoteId);
        	inputMap.put("quoteItemSeqId", quoteItemSeqId);
        	inputMap.put("statusId", "QTITM_NEGOTIATION");
        	
        	result = dispatcher.runSync("setQuoteAndItemStatus", inputMap);
        	if(ServiceUtil.isError(result)){
        		Debug.logError("Error updating QuoteStatus", module);
  	  			return ServiceUtil.returnError("Error updating QuoteStatus");
        	}
        	
        }catch(Exception e){
        	Debug.logError("Error updating quote status", module);
		    return ServiceUtil.returnError("Error updating quote status");
        }
        result = ServiceUtil.returnSuccess("Updated Quote Status!");
        result.put("custRequestId", custRequestId);
		return result;
    }//End of Service
	
	
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
        String comments = (String)context.get("comments");
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
        	
        	List condList=FastList.newInstance();
			List<GenericValue> quoteItems = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
			
			if(statusId.equals("QTITM_REJECTED")){
				condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"QTITM_REJECTED"));
			}
			
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			quoteItems = delegator.findList("QuoteItem", cond,null,null,null,false);
        	if(quoteItems.size()==0 && statusId.equals("QTITM_REJECTED")){
        		GenericValue setQuote=delegator.findOne("Quote", UtilMisc.toMap("quoteId", quoteId), false);
        		setQuote.set("statusId","QUO_REJECTED");
        		setQuote.store();
        	}
        	result = dispatcher.runSync("createQuoteStatus", UtilMisc.toMap("userLogin", userLogin, "quoteId", quoteId, "quoteItemSeqId", quoteItemSeqId, "statusId", statusId,"comments",comments));
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
	
	public static String updateQuotesStatusOfEnquiry(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String custRequestId = (String) request.getParameter("custRequestId");
	    String quoteStatusId = (String) request.getParameter("quoteStatusId");
	    String comments = (String) request.getParameter("comments");
	    String quoteItemStatusId = (String) request.getParameter("quoteItemStatusId");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if (UtilValidate.isEmpty(quoteStatusId)) {
			Debug.logError("Quote status is empty", module);
			return "error";
		}
		if (UtilValidate.isEmpty(quoteItemStatusId)) {
			Debug.logError("Quote item status is empty", module);
			return "error";
		}
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}

		boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			String quoteId = "";
			Map quoteInputCtx = FastMap.newInstance();
			
			for (int i = 0; i < rowCount; i++) {
				  
				Map quoteItemMap = FastMap.newInstance();
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				
				if (paramMap.containsKey("quoteId" + thisSuffix)) {
					quoteId = (String) paramMap.get("quoteId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing Quote Id");
					return "error";			  
				}
				GenericValue quote = delegator.findOne("Quote", UtilMisc.toMap("quoteId", quoteId), false);
				
				List<GenericValue> quoteItems = delegator.findList("QuoteItem", EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId), null, null, null, false);
				
				quoteInputCtx.clear();
				quoteInputCtx.put("quoteId", quoteId);
				quoteInputCtx.put("statusId", quoteStatusId);
				quoteInputCtx.put("comments", comments);
				quoteInputCtx.put("userLogin", userLogin);
				result = dispatcher.runSync("setQuoteAndItemStatus", quoteInputCtx);
				if(ServiceUtil.isError(result)){
					Debug.logError(ServiceUtil.getErrorMessage(result), module);
		  			request.setAttribute("_ERROR_MESSAGE_", "Error updating quote status to  : " +quoteStatusId+" for QuoteId : "+quoteId);
		  			TransactionUtil.rollback();
		  			return "error";
				}
				for(GenericValue quoteItem : quoteItems){
					quoteInputCtx.clear();
					quoteInputCtx.put("quoteId", quoteId);
					quoteInputCtx.put("quoteItemSeqId", quoteItem.getString("quoteItemSeqId"));
					quoteInputCtx.put("statusId", quoteItemStatusId);
					quoteInputCtx.put("comments", comments);
					quoteInputCtx.put("userLogin", userLogin);
					result = dispatcher.runSync("setQuoteAndItemStatus", quoteInputCtx);
					if(ServiceUtil.isError(result)){
						Debug.logError(ServiceUtil.getErrorMessage(result), module);
			  			request.setAttribute("_ERROR_MESSAGE_", "Error updating quote item status to  : " +quoteItemStatusId+" for QuoteId : "+quoteId);
			  			TransactionUtil.rollback();
			  			return "error";
					}
				}
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
  	  		request.setAttribute("_ERROR_MESSAGE_", "Error in service setQuoteAndItemStatus ");
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
		request.setAttribute("_EVENT_MESSAGE_", "Quote status changed successfully");
		request.setAttribute("custRequestId", custRequestId);
		return "success";
	}
	
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
        	quoteInputCtx.put("createdByUserLogin", userLogin.getString("userLoginId"));
        	quoteInputCtx.put("createdDate", UtilDateTime.nowTimestamp());
        	quoteInputCtx.put("lastModifiedDate", UtilDateTime.nowTimestamp());
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
    	result.put("quoteId", quoteId);
		return result;
    }//End of Service
	
	public static Map<String, Object> createPOForQuote(DispatchContext ctx, Map<String, ? extends Object> context){ 
		Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String currencyUomId = (String) context.get("defaultOrganizationPartyCurrencyUomId");
    	String quoteId = (String)context.get("quoteId");
    	GenericValue quote =null;
    	String partyId ="";
    	String billFromPartyId ="Company";
    	String billToPartyId="";
    	BigDecimal quantity = BigDecimal.ZERO;
    	BigDecimal quoteUnitPrice = BigDecimal.ZERO;
    	String salesChannel = (String) context.get("salesChannelEnumId");
    	List<GenericValue> quoteItemList = FastList.newInstance();
    	//getting productStoreId 
		String productStoreId = (String) (in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
         if (UtilValidate.isEmpty(salesChannel)) {
             salesChannel = "MATERIAL_PUR_CHANNEL";
         }     
    	 try {  
    		quote=delegator.findOne("Quote",UtilMisc.toMap("quoteId", quoteId), false);
    		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "QTITM_QUALIFIED"));
	        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		quoteItemList = delegator.findList("QuoteItem", condition, null, UtilMisc.toList("quoteItemSeqId"), null, false);
    		partyId = quote.getString("partyId");
    		
         	/*GenericValue product=delegator.findOne("Product",UtilMisc.toMap("productId", (String)quoteItemList.get(0).getString("productId")), false);*/
         	
         } catch (GenericEntityException e) {
             Debug.logError(e, "Problem getting product store Id", module);
     		return ServiceUtil.returnError("Problem getting product store Id: " + e);          	
             
         }
    	 ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale, currencyUomId);
         cart.setOrderType("PURCHASE_ORDER");
         cart.setChannelType(salesChannel);
         cart.setProductStoreId(productStoreId);
         cart.setBillToCustomerPartyId("Company");
         cart.setPlacingCustomerPartyId(billFromPartyId);
         cart.setShipToCustomerPartyId(billFromPartyId);
         cart.setEndUserCustomerPartyId(billFromPartyId);
		//cart.setShipmentId(shipmentId);
		//for PurchaseOrder we have to use for SupplierId
		if(UtilValidate.isNotEmpty(billToPartyId)){
			 cart.setBillFromVendorPartyId(billToPartyId);
		}else{
			cart.setBillFromVendorPartyId(partyId);
		}
	    cart.setShipFromVendorPartyId(partyId);
	    cart.setSupplierAgentPartyId(partyId);
        cart.setQuoteId(quoteId); 
        try {
             cart.setUserLogin(userLogin, dispatcher);
         } catch (Exception exc) {
             Debug.logWarning("Error setting userLogin in the cart: " + exc.getMessage(), module);
     		return ServiceUtil.returnError("Error setting userLogin in the cart: " + exc.getMessage());          	            
         }   
         Iterator<GenericValue> i = quoteItemList.iterator();
         String custRequestId="";
         List taxList=FastList.newInstance();
         List quoteTermVatList=FastList.newInstance();
         List quoteTermBedList=FastList.newInstance();
         List quoteTermCstList=FastList.newInstance();
         while (i.hasNext()) {
             GenericValue quoteItem = i.next();
             if (quoteItem != null) {
            	 custRequestId=quoteItem.getString("custRequestId");            	 
             	try { 
                    
             		ShoppingCartItem item = null;
    				int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), quoteItem.getString("productId"), null, quoteItem.getBigDecimal("quantity"), quoteItem.getBigDecimal("quoteUnitPrice"),
    					            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
    					            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
    					
    					item = cart.findCartItem(itemIndx);
    					
    					quantity = quoteItem.getBigDecimal("quantity");
    					quoteUnitPrice = quoteItem.getBigDecimal("quoteUnitPrice");
    					String quoteItemSeqId = quoteItem.getString("quoteItemSeqId");
    					if(UtilValidate.isNotEmpty(quoteId) && UtilValidate.isNotEmpty(quoteItemSeqId)){
    						//Vat Here
    						List qouteTermVatCondList = UtilMisc.toList(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
    						qouteTermVatCondList.add(EntityCondition.makeCondition("quoteItemSeqId", EntityOperator.EQUALS, quoteItemSeqId));
    						qouteTermVatCondList.add(EntityCondition.makeCondition("uomId", EntityOperator.EQUALS, "PERCENT"));
    						qouteTermVatCondList.add(EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "VAT_PUR"));
    				        EntityCondition quotVatCond = EntityCondition.makeCondition(qouteTermVatCondList, EntityOperator.AND);
    				        quoteTermVatList = delegator.findList("QuoteTerm", quotVatCond, null, null, null, false);
    				        if(UtilValidate.isNotEmpty(quoteTermVatList)){
    				        	GenericValue quoteTermVat = EntityUtil.getFirst(quoteTermVatList); 
    				        	if(UtilValidate.isNotEmpty(quoteTermVat)){
    				        		BigDecimal vatPercent = quoteTermVat.getBigDecimal("termValue");
            					    if(UtilValidate.isNotEmpty(vatPercent)){
            							Map<String,Object> inVatRateMap = UtilAccounting.getInclusiveTaxRate(quoteUnitPrice,vatPercent);
            							BigDecimal vatUnitRate = (BigDecimal)inVatRateMap.get("taxAmount");
            							if(UtilValidate.isNotEmpty(vatUnitRate)){
            								BigDecimal vatAmount = vatUnitRate.multiply(quantity);
            								if(vatAmount.compareTo(BigDecimal.ZERO)>0){
            									vatAmount=vatAmount.setScale(purchaseTaxCalcDecimals, purchaseTaxRounding);
                    			        		Map taxDetailMap = FastMap.newInstance();
                    				    		taxDetailMap.put("taxType", "VAT_PUR");
                    				    		taxDetailMap.put("amount", vatAmount);
                    				    		taxDetailMap.put("percentage", vatPercent);
                    				    		taxList.add(taxDetailMap);
                    				    		item.setTaxDetails(taxList);
                    					    }
            							}
            						}
    				        	}
    				        }
    				        //Bed Here
    				        List qouteTermBedCondList = UtilMisc.toList(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
    				        qouteTermBedCondList.add(EntityCondition.makeCondition("quoteItemSeqId", EntityOperator.EQUALS, quoteItemSeqId));
    				        qouteTermBedCondList.add(EntityCondition.makeCondition("uomId", EntityOperator.EQUALS, "PERCENT"));
    				        qouteTermBedCondList.add(EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "BED_PUR"));
    				        EntityCondition quotBedCond = EntityCondition.makeCondition(qouteTermBedCondList, EntityOperator.AND);
    				        quoteTermBedList = delegator.findList("QuoteTerm", quotBedCond, null, null, null, false);
    				        if(UtilValidate.isNotEmpty(quoteTermBedList)){
    				        	GenericValue quoteTermBed = EntityUtil.getFirst(quoteTermBedList); 
    				        	if(UtilValidate.isNotEmpty(quoteTermBed)){
    				        		BigDecimal bedPercent = quoteTermBed.getBigDecimal("termValue");
            					    if(UtilValidate.isNotEmpty(bedPercent)){
            							Map<String,Object> inBedRateMap = UtilAccounting.getInclusiveTaxRate(quoteUnitPrice,bedPercent);
            							BigDecimal bedUnitRate = (BigDecimal)inBedRateMap.get("taxAmount");
            							if(UtilValidate.isNotEmpty(bedUnitRate)){
            								BigDecimal bedAmount = bedUnitRate.multiply(quantity);
            								if(bedAmount.compareTo(BigDecimal.ZERO)>0){
            									bedAmount=bedAmount.setScale(purchaseTaxCalcDecimals, purchaseTaxRounding);
                    			        		Map taxDetailMap = FastMap.newInstance();
                    				    		taxDetailMap.put("taxType", "BED_PUR");
                    				    		taxDetailMap.put("amount", bedAmount);
                    				    		taxDetailMap.put("percentage", bedPercent);
                    				    		taxList.add(taxDetailMap);
                    				    		item.setTaxDetails(taxList);
                    					    }
            								
        									Map<String,Object> inBedCessRateMap = UtilAccounting.getInclusiveTaxRate(bedUnitRate,new BigDecimal(2));
        									BigDecimal bedCessUnitRate = (BigDecimal)inBedCessRateMap.get("taxAmount");
        									BigDecimal bedCessAmount = bedCessUnitRate.multiply(quantity);
        									if(bedCessAmount.compareTo(BigDecimal.ZERO)>0){
        										bedCessAmount=bedCessAmount.setScale(purchaseTaxCalcDecimals, purchaseTaxRounding);
                    			        		Map taxDetailMap = FastMap.newInstance();
                    				    		taxDetailMap.put("taxType", "BEDCESS_PUR");
                    				    		taxDetailMap.put("amount", bedCessAmount);
                    				    		taxDetailMap.put("percentage", new BigDecimal(2));
                    				    		taxList.add(taxDetailMap);
                    				    		item.setTaxDetails(taxList);
                    					    }
        									
        									Map<String,Object> inBedSecCessRateMap = UtilAccounting.getInclusiveTaxRate(bedUnitRate,new BigDecimal(1));
        									BigDecimal bedSecCessUnitRate = (BigDecimal)inBedSecCessRateMap.get("taxAmount");
        									BigDecimal bedSecCessAmount = bedSecCessUnitRate.multiply(quantity);
        									if(bedSecCessAmount.compareTo(BigDecimal.ZERO)>0){
        										bedSecCessAmount=bedSecCessAmount.setScale(purchaseTaxCalcDecimals, purchaseTaxRounding);
                    			        		Map taxDetailMap = FastMap.newInstance();
                    				    		taxDetailMap.put("taxType", "BEDSECCESS_PUR");
                    				    		taxDetailMap.put("amount", bedSecCessAmount);
                    				    		taxDetailMap.put("percentage", new BigDecimal(1));
                    				    		taxList.add(taxDetailMap);
                    				    		item.setTaxDetails(taxList);
                    					    }
            							}
            						}
    				        	}
    				        }
    				        //Cst Here
    				        List qouteTermCstCondList = UtilMisc.toList(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
    				        qouteTermCstCondList.add(EntityCondition.makeCondition("quoteItemSeqId", EntityOperator.EQUALS, quoteItemSeqId));
    				        qouteTermCstCondList.add(EntityCondition.makeCondition("uomId", EntityOperator.EQUALS, "PERCENT"));
    				        qouteTermCstCondList.add(EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "CST_PUR"));
    				        EntityCondition quotCstCond = EntityCondition.makeCondition(qouteTermCstCondList, EntityOperator.AND);
    				        quoteTermCstList = delegator.findList("QuoteTerm", quotCstCond, null, null, null, false);
    				        if(UtilValidate.isNotEmpty(quoteTermCstList)){
    				        	GenericValue quoteTermCst = EntityUtil.getFirst(quoteTermCstList); 
    				        	if(UtilValidate.isNotEmpty(quoteTermCst)){
    				        		BigDecimal cstPercent = quoteTermCst.getBigDecimal("termValue");
            					    if(UtilValidate.isNotEmpty(cstPercent)){
            							Map<String,Object> inCstRateMap = UtilAccounting.getInclusiveTaxRate(quoteUnitPrice,cstPercent);
            							BigDecimal cstUnitRate = (BigDecimal)inCstRateMap.get("taxAmount");
            							if(UtilValidate.isNotEmpty(cstUnitRate)){
            								BigDecimal cstAmount = cstUnitRate.multiply(quantity);
            								if(cstAmount.compareTo(BigDecimal.ZERO)>0){
            									cstAmount=cstAmount.setScale(purchaseTaxCalcDecimals, purchaseTaxRounding);
                    			        		Map taxDetailMap = FastMap.newInstance();
                    				    		taxDetailMap.put("taxType", "CST_PUR");
                    				    		taxDetailMap.put("amount", cstAmount);
                    				    		taxDetailMap.put("percentage", cstPercent);
                    				    		taxList.add(taxDetailMap);
                    				    		item.setTaxDetails(taxList);
                    					    }
            							}
            						}
    				        	}
    				        }
    					}
    					//item.setListPrice(totalPrice);
    					//item.setTaxDetails(taxList);
    	        		item.setQuoteId(quoteItem.getString("quoteId"));
    	        		item.setQuoteItemSeqId(quoteItem.getString("quoteItemSeqId"));
             		
                 } catch (Exception exc) {
                     Debug.logWarning("Error adding product with id " + quoteItem.getString("productId") + " to the cart: " + exc.getMessage(), module);
             		return ServiceUtil.returnError("Error adding product with id " + quoteItem.getString("productId") + " to the cart: " + exc.getMessage());          	            
                 }
                
             }  
         }
         GenericValue custReqDetails = null;
         String custRequestName=null;
         if(UtilValidate.isNotEmpty(custRequestId)){
             try{
        	        custReqDetails = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId",custRequestId), false);
        	        if(UtilValidate.isNotEmpty(custReqDetails)){ 
        	            custRequestName=(String) custReqDetails.get("custRequestName");
        	         }
    	        }
               catch(Exception e) {
    	  		     Debug.logError("Error While fecting data from CustRequest", module);
    	  		     return ServiceUtil.returnError("Error While fecting data from CustRequest");
    	  	   }
          
         }
         if(UtilValidate.isNotEmpty(custRequestId)){
				cart.setOrderAttribute("REF_NUMBER",custRequestId);
         }
         if(UtilValidate.isNotEmpty(custRequestName)){
				cart.setOrderAttribute("FILE_NUMBER",custRequestName);
         }
      
         cart.setDefaultCheckoutOptions(dispatcher);
         CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
         Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
         String orderId = (String) orderCreateResult.get("orderId");
         
         try { 
        	 
        	 List<GenericValue> otherChargesTerms = delegator.findList("TermType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "OTHERS"), UtilMisc.toSet("termTypeId"), null, null, false);
        	 List<String> otherChargesIds = EntityUtil.getFieldListFromEntityList(otherChargesTerms, "termTypeId", true);
        	 
             List qouteTermCondList = UtilMisc.toList(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
             qouteTermCondList.add(EntityCondition.makeCondition("termTypeId", EntityOperator.IN, otherChargesIds));
             EntityCondition quoteCond = EntityCondition.makeCondition(qouteTermCondList, EntityOperator.AND);
             List<GenericValue> quoteTermList = delegator.findList("QuoteTerm", quoteCond, null, null, null, false);
             if(UtilValidate.isNotEmpty(quoteTermList)){
            	 
            	for(GenericValue eachTerm : quoteTermList){
            		BigDecimal otherAmt = eachTerm.getBigDecimal("termValue");
            		String termTypeId = eachTerm.getString("termTypeId");
            		String uomId = eachTerm.getString("uomId");
            		if(UtilValidate.isNotEmpty(otherAmt) && otherAmt.compareTo(BigDecimal.ZERO)>0){
            			// handle percentage 
            			Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
             	    	adjustCtx.put("orderId", orderId);
             	    	adjustCtx.put("orderAdjustmentTypeId", termTypeId);
             	    	adjustCtx.put("amount", otherAmt);
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
             
         }catch(Exception e) {
   	  		Debug.logError("Error While creating The QuoteItem", module);
   	  		return ServiceUtil.returnError("Error While creating The QuoteItem");
   	  	 }
         
         try{
        	 for(GenericValue eachQuoteItem : quoteItemList){
            	 Map inputMap = FastMap.newInstance();
            	 inputMap.put("userLogin", userLogin);
            	 inputMap.put("quoteId", eachQuoteItem.getString("quoteId"));
            	 inputMap.put("quoteItemSeqId", eachQuoteItem.getString("quoteItemSeqId"));
            	 inputMap.put("statusId", "QTITM_ORDERED");
    	        	
    	         Map resultStatus = dispatcher.runSync("setQuoteAndItemStatus", inputMap);
    	         if(ServiceUtil.isError(resultStatus)){
            		Debug.logError("Error updating QuoteStatus", module);
      	  			return ServiceUtil.returnError("Error updating QuoteStatus");
    	         }
             }
         }catch(GenericServiceException e){
        	 Debug.logError("Error While updating quote item status to ORDERED", module);
    	  	 return ServiceUtil.returnError("Error While updating quote item status to ORDERED");
         }
            boolean isOrdered=false;
	 		Map statusCtx = FastMap.newInstance();
	 		statusCtx.put("custRequestId", custRequestId);
	 		statusCtx.put("userLogin", userLogin);
	 		try {
		 		Map<String, Object> enquiryResult = (Map)dispatcher.runSync("enquiryStatusValidation", statusCtx);
		 		isOrdered=(Boolean)enquiryResult.get("isOrdered");
	 		}catch(Exception e){
 	        	Debug.logError("Error in enquiryStatusValidation Service", module);
 			    return ServiceUtil.returnError("Error in enquiryStatusValidation Service");
 	        }
	 		if (isOrdered) {
	 			statusCtx.clear();
	 			statusCtx.put("statusId", "ENQ_ORDERED");
	 			statusCtx.put("custRequestId", custRequestId);
	 			statusCtx.put("userLogin", userLogin);
	 			try{
		 			Map<String, Object> resultCtx = dispatcher.runSync("setRequestStatus", statusCtx);
		 			if (ServiceUtil.isError(resultCtx)) {
		 				Debug.logError("Error While Updating Enquiry Status: " + custRequestId, module);
		 				return resultCtx;
		 			}
	 			}catch(Exception e){
	 	        	Debug.logError("Error While Updating Enquiry Status:" + custRequestId, module);
	 			    return ServiceUtil.returnError("Error While Updating Enquiry Status:");
	 	        }
	 		}
         result = ServiceUtil.returnSuccess("Created Purchase Order for Quote : "+quoteId);
         result.put("orderId", orderId);
        // result.put("custRequestName", custRequestName);
         return result;
    }
	
	public static String updateQuoteForEnquiry(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String issueDateStr = (String) request.getParameter("issueDate");
	    String quoteName = (String) request.getParameter("quoteName");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	    String partyId = (String) request.getParameter("partyId");
	  	String quoteId = (String) request.getParameter("quoteId");
	    String validFromDateStr = (String) request.getParameter("validFromDate");
	    String validThruDateStr = (String) request.getParameter("validThruDate");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		Timestamp issueDate=null;
		Timestamp validFromDate=null;
		Timestamp validThruDate=null;
		
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
	  	else{
	  		validThruDate = UtilDateTime.nowTimestamp();
	  	}
	  	String quoteItemSeqId="";
        String quantityStr = "";
        String priceStr = "";
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal quoteUnitPrice = BigDecimal.ZERO;
		List<Map> quoteItemList = FastList.newInstance();
	  	
	  	for (int i = 0; i < rowCount; i++) {
	  		Map quoteItemMap = FastMap.newInstance(); 
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			if (paramMap.containsKey("quoteItemSeqId" + thisSuffix)) {
				quoteItemSeqId = (String) paramMap.get("quoteItemSeqId" + thisSuffix);
			}
			else {
				request.setAttribute("_ERROR_MESSAGE_", "Missing quoteItemSeqId");
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
				quoteUnitPrice = new BigDecimal(priceStr);
			}
			quoteItemMap.put("quoteItemSeqId", quoteItemSeqId);
			quoteItemMap.put("quantity",quantity);
	        quoteItemMap.put("quoteUnitPrice", quoteUnitPrice);
	        
	        quoteItemList.add(quoteItemMap);
		
	  	}
	  	try{
	  		GenericValue quotes=delegator.findOne("Quote",UtilMisc.toMap("quoteId",quoteId),false);
	  		if(UtilValidate.isNotEmpty(quotes)){
	  			try{
	  				Timestamp issDate=(Timestamp) quotes.get("issueDate");
	  				Timestamp fromDate=(Timestamp) quotes.get("validFromDate");
	  				Timestamp thruDate=(Timestamp) quotes.get("validThruDate");
	  				String quName = (String) quotes.get("quoteName");
	  				String id = (String) quotes.get("partyId");
	  				if(!partyId.equals(id)){
	  					quotes.set("partyId",partyId);
	  				}
	  				if(!quoteName.equals(quName)){
	  					quotes.set("quoteName",quoteName);
	  				}
	  				if((issueDate.compareTo(issDate)>0) || (issueDate.compareTo(issDate)<0)){
	  					quotes.set("issueDate",issueDate);
	  				}
	  				if((validFromDate.compareTo(fromDate)>0) || (validFromDate.compareTo(fromDate)<0)){
	  					quotes.set("validFromDate",validFromDate);
	  				}
	  				if((validThruDate.compareTo(thruDate)>0) || (validThruDate.compareTo(thruDate)<0)){
	  					quotes.set("validThruDate",validThruDate);
	  				}
	  				quotes.set("lastModifiedDate",nowTimeStamp);
  					quotes.set("lastModifiedByUserLogin",userLogin.getString("userLoginId"));
	  				quotes.store();
	  			}catch(Exception e){
	  				request.setAttribute("_ERROR_MESSAGE_", "Error While Updating The Quote");
	  	  	  		Debug.logError("Error While Updating The Quote", module);
	  	  	  		return "Error While Updating The Quote";
	  			}
	  		}
	  		
		  	if(UtilValidate.isNotEmpty(quoteItemList)){
		  		for(Map tempMap : quoteItemList){
		  			quantity=(BigDecimal)tempMap.get("quantity");
		  			quoteUnitPrice=(BigDecimal)tempMap.get("quoteUnitPrice");
		  			try{
				  		GenericValue quoteItems=delegator.findOne("QuoteItem",UtilMisc.toMap("quoteId",quoteId,"quoteItemSeqId",tempMap.get("quoteItemSeqId")),false);
				  		BigDecimal qty=BigDecimal.ZERO;
				  		BigDecimal unitPrice=BigDecimal.ZERO;
				  		qty=(BigDecimal) quoteItems.get("quantity");
				  		unitPrice=(BigDecimal) quoteItems.get("quoteUnitPrice");
				  		if(!quantity.equals(qty)){
				  			quoteItems.set("quantity",quantity);
				  		}
				  		if(!quoteUnitPrice.equals(unitPrice)){
				  			quoteItems.set("quoteUnitPrice",quoteUnitPrice);
				  		}
				  		quoteItems.store();
		  			}catch(Exception e){
		  				request.setAttribute("_ERROR_MESSAGE_", "Error While Updating The QuoteItem");
		  	  	  		Debug.logError("Error While Updating The QuoteItem", module);
		  	  	  		return "Error While Updating The QuoteItem";
		  			}
			  	}
		  	}
	  	} catch(Exception e) {
  	  		request.setAttribute("_ERROR_MESSAGE_", "Error While Updating The QuoteItem");
  	  		Debug.logError("Error While Updating The QuoteItem", module);
  	  		return "Error While Updating The QuoteItem";
  	  	}
	  	request.setAttribute("_EVENT_MESSAGE_","Successfully Quotation Updated..!");
		return "Success";
	}
}
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
import java.util.Map.Entry;

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
	public static Map<String,Object> getQuoteItemAndTermsMap(DispatchContext ctx, Map<String, ? extends Object> context){
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String quoteId = (String)context.get("quoteId");
        try{
        	List<GenericValue> quoteItems = delegator.findList("QuoteItem", EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId), null, null, null, false);
        	
        	List<GenericValue> termTypes = delegator.findList("TermType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, UtilMisc.toList("TAX", "OTHERS")), null, null, null, false);
        	List<String> termTypeIds = EntityUtil.getFieldListFromEntityList(termTypes, "termTypeId", true);
        	
        	List conditionList = FastList.newInstance();
        	conditionList.add(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
        	conditionList.add(EntityCondition.makeCondition("termTypeId", EntityOperator.IN, termTypeIds));
        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        	List<GenericValue> quoteTerms = delegator.findList("QuoteTerm", condition, null, null, null, false);
        	List<GenericValue> taxTermTypes = EntityUtil.filterByCondition(termTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "TAX"));
        	List taxTermIds = EntityUtil.getFieldListFromEntityList(taxTermTypes, "termTypeId", true);
        	List<GenericValue> taxTerms = EntityUtil.filterByCondition(quoteTerms, EntityCondition.makeCondition("termTypeId", EntityOperator.IN, taxTermIds));
        	List<GenericValue> otherTermTypes = EntityUtil.filterByCondition(termTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "OTHERS"));
        	List otherTermIds = EntityUtil.getFieldListFromEntityList(otherTermTypes, "termTypeId", true);
        	List<GenericValue> otherTerms = EntityUtil.filterByCondition(quoteTerms, EntityCondition.makeCondition("termTypeId", EntityOperator.IN, otherTermIds));
        	List<Map> otherCharges = FastList.newInstance();
        	List<Map> productQty = FastList.newInstance();
        	Map productItemRef = FastMap.newInstance();
        	for(GenericValue quoteItem : quoteItems){
        		Map tempMap = FastMap.newInstance();
        		tempMap.put("productId", quoteItem.getString("productId"));
        		tempMap.put("quantity", quoteItem.getBigDecimal("quantity"));
        		tempMap.put("unitPrice", quoteItem.getBigDecimal("quoteUnitPrice"));
        		tempMap.put("vatPercent", BigDecimal.ZERO);
        		tempMap.put("cstPercent", BigDecimal.ZERO);
        		tempMap.put("bedPercent", BigDecimal.ZERO);
        		productItemRef.put(quoteItem.getString("productId"), tempMap);
        	}
        	for(GenericValue taxTerm : taxTerms){
        		String quoteItemSeqId = taxTerm.getString("quoteItemSeqId");
        		String termTypeId = taxTerm.getString("termTypeId");
        		List<GenericValue> iterateValues = FastList.newInstance();
        		if(UtilValidate.isNotEmpty(quoteItemSeqId) && !(quoteItemSeqId.equals("_NA_"))){
        			List<GenericValue> quoteItm = EntityUtil.filterByCondition(quoteItems, EntityCondition.makeCondition("quoteItemSeqId", EntityOperator.EQUALS, quoteItemSeqId));
        			if(UtilValidate.isNotEmpty(quoteItm)){
        				GenericValue item = EntityUtil.getFirst(quoteItm);
        				iterateValues.add(item);
        			}
        		}
        		else{
        			iterateValues.addAll(quoteItems);
        		}
        		for(GenericValue item : iterateValues){
    				String prodId = item.getString("productId");
    				Map prodQtyMap = (Map)productItemRef.get(prodId);
    				if(termTypeId.equals("VAT_PUR")){
    					prodQtyMap.put("vatPercent", taxTerm.getBigDecimal("termValue"));
    				}
    				if(termTypeId.equals("CST_PUR")){
    					prodQtyMap.put("cstPercent", taxTerm.getBigDecimal("termValue"));
    				}
    				if(termTypeId.equals("BED_PUR")){
    					prodQtyMap.put("bedPercent", taxTerm.getBigDecimal("termValue"));
    				}
    				productItemRef.put(prodId, prodQtyMap);
        		}
        	}
        	Iterator prodItemsIter = productItemRef.entrySet().iterator();
			while (prodItemsIter.hasNext()) {
				Map.Entry tempEntry = (Entry) prodItemsIter.next();
				Map eachItem = (Map) tempEntry.getValue();
				productQty.add(eachItem);
			}
			for(GenericValue otherTerm : otherTerms){
				
				String applicableTo = "ALL";
				String sequenceId = otherTerm.getString("quoteItemSeqId");
				if(UtilValidate.isNotEmpty(sequenceId) && !(sequenceId.equals("_NA_"))){
					List<GenericValue> quoteItm = EntityUtil.filterByCondition(quoteItems, EntityCondition.makeCondition("quoteItemSeqId", EntityOperator.EQUALS, sequenceId));
        			if(UtilValidate.isNotEmpty(quoteItm)){
        				applicableTo = (EntityUtil.getFirst(quoteItm)).getString("productId");
        			}
				}
        		Map tempMap = FastMap.newInstance();
        		tempMap.put("otherTermId", otherTerm.getString("termTypeId"));
        		tempMap.put("applicableTo", applicableTo);
        		tempMap.put("termValue", otherTerm.getBigDecimal("termValue"));
        		tempMap.put("uomId", otherTerm.getString("uomId"));
        		tempMap.put("termDays", otherTerm.getLong("termDays"));
        		tempMap.put("description", otherTerm.getString("description"));
        		otherCharges.add(tempMap);
        	}
			result.put("otherCharges", otherCharges);
			result.put("productQty", productQty);
        }
        catch(Exception e){
        	Debug.logError("Error updating quote status", module);
		    return ServiceUtil.returnError("Error updating quote status");
        }
      //otherCharges = [{otherTermId, applicableTo(ALL/RM1201), termValue, uomId, termDays, description}]
    	//productQty = [{productId, quantity, unitPrice, bedPercent, cstPercent, vatPercent}]
    	
        return result;
	}
	
	public static Map<String,Object> calculateQuoteGrandTotal(DispatchContext ctx, Map<String, ? extends Object> context){
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String quoteId = (String)context.get("quoteId");
        try{
        	
        	List<GenericValue> quoteItems = delegator.findList("QuoteItem", EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId), null, null, null, false);
        	Map resultCtx = getQuoteItemAndTermsMap(ctx, UtilMisc.toMap("quoteId", quoteId, "userLogin", userLogin));
        	if(ServiceUtil.isError(resultCtx)){
				String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				return ServiceUtil.returnError(errMsg);
			}
			List<Map> otherCharges = (List)resultCtx.get("otherCharges");
			List<Map> productQty = (List)resultCtx.get("productQty");
			
			resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", productQty, "otherCharges", otherCharges, "userLogin", userLogin, "incTax", ""));
			if(ServiceUtil.isError(resultCtx)){
				String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				return ServiceUtil.returnError(errMsg);
			}
			BigDecimal grandTotal = (BigDecimal)resultCtx.get("grandTotal");
			List<Map> itemDetail = (List)resultCtx.get("itemDetail");
			
			GenericValue quote = delegator.findOne("Quote", UtilMisc.toMap("quoteId", quoteId), false);
			quote.set("grandTotal", grandTotal);
			quote.store();
			
			for(Map eachItem : itemDetail){
				String productId = (String)eachItem.get("productId");
				BigDecimal unitListPrice = (BigDecimal) eachItem.get("unitListPrice");
				List<GenericValue> quotItm = EntityUtil.filterByCondition(quoteItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)); 
				if(UtilValidate.isNotEmpty(quotItm)){
					GenericValue eachQuoteItem = EntityUtil.getFirst(quotItm);
					BigDecimal newItemTotal = (unitListPrice.multiply(eachQuoteItem.getBigDecimal("quantity"))).setScale(2, BigDecimal.ROUND_HALF_UP);
					if(UtilValidate.isNotEmpty(eachQuoteItem.getBigDecimal("itemTotal"))){
						BigDecimal extItemTotal = (eachQuoteItem.getBigDecimal("itemTotal")).setScale(2, BigDecimal.ROUND_HALF_UP);
						if(UtilValidate.isEmpty(extItemTotal) || (UtilValidate.isNotEmpty(extItemTotal) && extItemTotal.compareTo(newItemTotal)!=0)){
							eachQuoteItem.set("itemTotal", newItemTotal);
							eachQuoteItem.store();
						}
					}
					else{
						eachQuoteItem.set("itemTotal", newItemTotal);
						eachQuoteItem.store();
					}
					
				}
			}        	
        }catch(Exception e){
        	Debug.logError("Error calculating quote total", module);
		    return ServiceUtil.returnError("Error calculating quote total");
        }
        result = ServiceUtil.returnSuccess("Quote total calculated successfully");
		return result;
    }//End of Service
	
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
    	String salesChannel = (String) context.get("salesChannelEnumId");
    	List<GenericValue> quoteItemList = FastList.newInstance();
    	List<Map> productItemDetail = FastList.newInstance();
    	List<Map> orderAdjustmentDetail = FastList.newInstance();
    	List<Map> otherChargesTermDetail = FastList.newInstance();
    	BigDecimal grandTotal = BigDecimal.ZERO;
    	//getting productStoreId 
		String productStoreId = (String) (in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
        if (UtilValidate.isEmpty(salesChannel)) {
             salesChannel = "MATERIAL_PUR_CHANNEL";
        }
        String custRequestId = "";
        List<GenericValue> quoteTerm = FastList.newInstance();
    	try {
    		
    		quote=delegator.findOne("Quote",UtilMisc.toMap("quoteId", quoteId), false);
    		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "QTITM_QUALIFIED"));
	        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		quoteItemList = delegator.findList("QuoteItem", condition, null, UtilMisc.toList("quoteItemSeqId"), null, false);
    		partyId = quote.getString("partyId");
    		
    		if(UtilValidate.isNotEmpty(quoteItemList)){
    			custRequestId = (EntityUtil.getFirst(quoteItemList)).getString("custRequestId");
    		}
    		
    		List<GenericValue> termTypes = delegator.findList("TermType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, UtilMisc.toList("TAX", "OTHERS")), null, null, null, false);
    		List termTypeIds = EntityUtil.getFieldListFromEntityList(termTypes, "termTypeId", true);
    		conditionList.clear();
    		conditionList.add(EntityCondition.makeCondition("termTypeId", EntityOperator.NOT_IN, termTypeIds));
    		conditionList.add(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
    		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		quoteTerm = delegator.findList("QuoteTerm", cond, null, null, null, false);
    		Map resultCtx = getQuoteItemAndTermsMap(ctx, UtilMisc.toMap("quoteId", quoteId, "userLogin", userLogin));
			if(ServiceUtil.isError(resultCtx)){
				String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				return ServiceUtil.returnError(errMsg);
			}
			
			List<Map> otherCharges = (List)resultCtx.get("otherCharges");
			List<Map> productQty = (List)resultCtx.get("productQty");
			resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", productQty, "otherCharges", otherCharges, "userLogin", userLogin, "incTax", ""));
			if(ServiceUtil.isError(resultCtx)){
				String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				return ServiceUtil.returnError(errMsg);
			}
			List<Map> itemDetail = (List)resultCtx.get("itemDetail");
			List<Map> adjustmentDetail = (List)resultCtx.get("adjustmentDetail");
			Map productAdjustmentPerUnit = (Map)resultCtx.get("productAdjustmentPerUnit");
			
			List<Map> revisedProdQty = FastList.newInstance();
			for(GenericValue quoteItem : quoteItemList){
				String productId  = quoteItem.getString("productId");
				for(Map eachItem : itemDetail){
					String itemProdId = (String)eachItem.get("productId");
					if(itemProdId.equals(productId)){
						Map tempMap = FastMap.newInstance();
						tempMap.put("productId", productId);
						tempMap.put("quantity", quoteItem.getBigDecimal("quantity"));
						tempMap.put("unitPrice", quoteItem.getBigDecimal("quoteUnitPrice"));
						tempMap.put("vatPercent", (BigDecimal)eachItem.get("vatPercent"));
						tempMap.put("bedPercent", (BigDecimal)eachItem.get("bedPercent"));
						tempMap.put("cstPecent", (BigDecimal)eachItem.get("cstPercent"));
						revisedProdQty.add(tempMap);
					}
				}
			}
	    	List<Map> revisedOtherCharges = FastList.newInstance();
			for(Map eachOthrCharge : adjustmentDetail){
				Map tempMap = FastMap.newInstance();
				String adjTypeId = (String)eachOthrCharge.get("adjustmentTypeId");
				tempMap.put("otherTermId", adjTypeId);
				String applicableTo = (String)eachOthrCharge.get("applicableTo");
				BigDecimal totalAdjAmt = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(applicableTo) && applicableTo.equals("_NA_")){
					tempMap.put("applicableTo", "ALL");
					for(Map eachProd : revisedProdQty){
						String prodId = (String)eachProd.get("productId");
						BigDecimal qty = (BigDecimal)eachProd.get("quantity");
						Map prodAdjUnitAmt = (Map)productAdjustmentPerUnit.get(prodId);
						if(UtilValidate.isNotEmpty(prodAdjUnitAmt) && UtilValidate.isNotEmpty(prodAdjUnitAmt.get(adjTypeId))){
							BigDecimal adjAmt = qty.multiply((BigDecimal)prodAdjUnitAmt.get(adjTypeId));
							totalAdjAmt = totalAdjAmt.add(adjAmt);
						}
						
					}
				} 
				else{
					List<GenericValue> quoteItm = EntityUtil.filterByCondition(quoteItemList, EntityCondition.makeCondition("quoteItemSeqId", EntityOperator.EQUALS, applicableTo));
					if(UtilValidate.isNotEmpty(quoteItm)){
						
						String prodId = (EntityUtil.getFirst(quoteItm)).getString("productId");
						Map prodAdjUnitAmt = (Map)productAdjustmentPerUnit.get(prodId);
						tempMap.put("applicableTo", prodId);
						BigDecimal qty = (BigDecimal)(EntityUtil.getFirst(quoteItm)).get("quantity");
						if(UtilValidate.isNotEmpty(prodAdjUnitAmt) && UtilValidate.isNotEmpty(prodAdjUnitAmt.get(adjTypeId))){
							BigDecimal adjAmt = qty.multiply((BigDecimal)prodAdjUnitAmt.get(adjTypeId));
							totalAdjAmt = totalAdjAmt.add(adjAmt);
						}
					}
				}
				tempMap.put("termValue", totalAdjAmt);
				tempMap.put("uomId", "INR");
				tempMap.put("termDays", null);
				tempMap.put("description", "");
				revisedOtherCharges.add(tempMap);
				
			}
			
			resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", revisedProdQty, "otherCharges", revisedOtherCharges, "userLogin", userLogin, "incTax", ""));
			if(ServiceUtil.isError(resultCtx)){
				String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				return ServiceUtil.returnError(errMsg);
			}
			productItemDetail = (List)resultCtx.get("itemDetail");
			orderAdjustmentDetail = (List)resultCtx.get("adjustmentDetail");
	    	otherChargesTermDetail = (List)resultCtx.get("termsDetail");
	    	grandTotal = (BigDecimal)resultCtx.get("grandTotal");
         } catch (Exception e) {
             Debug.logError(e, "Problem getting product store Id", module);
     		return ServiceUtil.returnError("Problem getting product store Id: " + e);          	
             
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
        
    	 ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale, currencyUomId);
         cart.setOrderType("PURCHASE_ORDER");
         cart.setChannelType(salesChannel);
         cart.setProductStoreId(productStoreId);
         cart.setBillToCustomerPartyId("Company");
         cart.setPlacingCustomerPartyId(billFromPartyId);
         cart.setShipToCustomerPartyId(billFromPartyId);
         cart.setEndUserCustomerPartyId(billFromPartyId);
		 if(UtilValidate.isNotEmpty(billToPartyId)){
			 cart.setBillFromVendorPartyId(billToPartyId);
		 }else{
			cart.setBillFromVendorPartyId(partyId);
		 }
		 if(UtilValidate.isNotEmpty(custRequestId)){
			cart.setOrderAttribute("REF_NUMBER",custRequestId);
		 }
		 if(UtilValidate.isNotEmpty(custRequestName)){
			cart.setOrderAttribute("FILE_NUMBER",custRequestName);
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
        
        
         String productId = "";
		
		 for (Map<String, Object> prodQtyMap : productItemDetail) {
			List taxList=FastList.newInstance();
			
			String quoteItemSeqId = "";
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
			
			
			if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
				productId = (String)prodQtyMap.get("productId");
				
				List<GenericValue> quoteItms = EntityUtil.filterByCondition(quoteItemList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				if(UtilValidate.isNotEmpty(quoteItms)){
					quoteItemSeqId = (EntityUtil.getFirst(quoteItms)).getString("quoteItemSeqId");
				}
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
			if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
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

			if(bedAmount.compareTo(BigDecimal.ZERO)>0){
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
			
			ShoppingCartItem item = null;
			try{
				int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null, quantity, unitPrice,
			            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
			            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
			
				item = cart.findCartItem(itemIndx);
				item.setListPrice(unitListPrice);
				item.setTaxDetails(taxList);
				
				item.setQuoteId(quoteId);
        		item.setQuoteItemSeqId(quoteItemSeqId);
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
	
		String orderId = (String) orderCreateResult.get("orderId");
		try {
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("orderItemSeqId", "productId"), null, null, false);
			for(GenericValue eachTerm : quoteTerm){
				Map termCreateCtx = FastMap.newInstance();
				termCreateCtx.put("userLogin", userLogin);
				termCreateCtx.put("orderId", orderId);
				termCreateCtx.put("termTypeId", (String)eachTerm.get("termTypeId"));
				termCreateCtx.put("description", (String)eachTerm.get("description"));
				Map orderTermResult = dispatcher.runSync("createOrderTerm",termCreateCtx);
				if (ServiceUtil.isError(orderTermResult)) {
					String errMsg =  ServiceUtil.getErrorMessage(orderTermResult);
					Debug.logError(errMsg, "While Creating Order Payment/Delivery Term",module);
					return ServiceUtil.returnError(" Error While Creating Order Payment/Delivery Term !"+errMsg);
				}
			}
			for(Map eachTermItem : otherChargesTermDetail){
				
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
			for(Map eachAdj : orderAdjustmentDetail){
				
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
			
		}catch (Exception e) {
 			Debug.logError(e, "Error while creating order adjustment for orderId :"+orderId, module);
 			return ServiceUtil.returnError("Error while creating order adjustment for orderId :"+orderId);			  
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
	  				if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(id)){
	  					if(!partyId.equals(id)){
	  						quotes.set("partyId",partyId);
	  					}
	  				}
	  				if(UtilValidate.isNotEmpty(quoteName)){
	  					if(UtilValidate.isNotEmpty(quName)){
	  						if(!quoteName.equals(quName)){
	  							quotes.set("quoteName",quoteName);
	  						}
	  					}else{
	  						quotes.set("quoteName",quoteName);
	  					}
	  				}
	  				if(UtilValidate.isNotEmpty(issueDate)){
	  					if(UtilValidate.isNotEmpty(issDate)){
	  						if((issueDate.compareTo(issDate)>0) || (issueDate.compareTo(issDate)<0)){
	  							quotes.set("issueDate",issueDate);
	  						}
	  					}else{
	  						quotes.set("issueDate",issueDate);
	  					}
	  				}
	  				if(UtilValidate.isNotEmpty(validFromDate)){
	  					if(UtilValidate.isNotEmpty(fromDate)){	
	  						if((validFromDate.compareTo(fromDate)>0) || (validFromDate.compareTo(fromDate)<0)){
	  							quotes.set("validFromDate",validFromDate);
	  					}
	  					}else{
	  						quotes.set("validFromDate",validFromDate);
	  					}
	  				}
	  				if(UtilValidate.isNotEmpty(validThruDate)){
	  					if(UtilValidate.isNotEmpty(thruDate)){
	  						if((validThruDate.compareTo(thruDate)>0) || (validThruDate.compareTo(thruDate)<0)){
	  							quotes.set("validThruDate",validThruDate);
	  						}
	  					}else{
	  						quotes.set("validThruDate",validThruDate);
	  					}
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
	
	public static String updateQuotesForEvaluation(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> resultCtx = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
	      Timestamp todayDayStart = UtilDateTime.getDayStart(nowTimeStamp);
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  String quoteId = "";
	  	  String quoteItemSeqId = "";
	  	  String custRequestId = "";
	  	  String statusId = "";
	  	  String comments = "";
	  	 
			  	for (int i = 0; i < rowCount; i++){
		  		  
			  	  Map paymentMap = FastMap.newInstance();
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  
		  		  BigDecimal amount = BigDecimal.ZERO;
		  		  String amountStr = "";
		  		  
		  		  if (paramMap.containsKey("quoteId" + thisSuffix)) {
		  			quoteId = (String) paramMap.get("quoteId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("quoteItemSeqId" + thisSuffix)) {
		  			quoteItemSeqId = (String) paramMap.get("quoteItemSeqId"+thisSuffix);
		  		  }
		  		   if (paramMap.containsKey("custRequestId" + thisSuffix)) {
		  			custRequestId = (String) paramMap.get("custRequestId"+thisSuffix);
		  		  }
		  		   if (paramMap.containsKey("statusId" + thisSuffix)) {
		  			statusId = (String) paramMap.get("statusId"+thisSuffix);
		  		  }
		  		try{
					Map evaluationMap = FastMap.newInstance();
					evaluationMap.put("quoteId", quoteId);
					evaluationMap.put("quoteItemSeqId", quoteItemSeqId);
					evaluationMap.put("custRequestId", custRequestId);
					evaluationMap.put("statusId", statusId);
					evaluationMap.put("comments", comments);
					evaluationMap.put("userLogin", userLogin);
					evaluationMap.put("locale", locale);
					resultCtx = dispatcher.runSync("changeQuoteItemStatus", evaluationMap);
					custRequestId = (String)resultCtx.get("custRequestId");
					if (ServiceUtil.isError(resultCtx)) {
						Debug.logError("Evaluation Failed for Quote: " + quoteId+":"+quoteItemSeqId, module);
						return "error";
						}
					} catch (Exception e) {
						// TODO: handle exception
						Debug.logError(e, module);
						request.setAttribute("_ERROR_MESSAGE_", " Quote Evaluation Failed ");
			  			return "error";
					}
			  	}
		         request.setAttribute("_EVENT_MESSAGE_", "Quote Status Updated  successfully");
		         request.setAttribute("custRequestId", custRequestId);
			return "success";
		}
	
	public static String updateQuotesForNegotiation(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      String quoteId = request.getParameter("quoteId");
	      String quoteItemSeqId = request.getParameter("quoteItemSeqId");
	      String custRequestId = request.getParameter("custRequestId");
	      BigDecimal quoteUnitPrice = new BigDecimal(request.getParameter("quoteUnitPrice"));
	      try{
	    	  Map negotiateMapCtx = FastMap.newInstance();
	    	  negotiateMapCtx.put("quoteId",quoteId);
	    	  negotiateMapCtx.put("quoteItemSeqId",quoteItemSeqId);
	    	  negotiateMapCtx.put("custRequestId",custRequestId);
	    	  negotiateMapCtx.put("quoteUnitPrice",quoteUnitPrice);
	    	  negotiateMapCtx.put("userLogin",userLogin);
	    	  negotiateMapCtx.put("locale",locale);
	    	  Map resultCtx = dispatcher.runSync("quoteNegotiateAndStatusChange", negotiateMapCtx);
	      }catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e, module);
				request.setAttribute("_ERROR_MESSAGE_", " Quote Negotiation Failed ");
	  			return "error";
			}
	      request.setAttribute("_EVENT_MESSAGE_", "Quote Negotiation successfully Accepted!!");
	      return "success";
	}

}
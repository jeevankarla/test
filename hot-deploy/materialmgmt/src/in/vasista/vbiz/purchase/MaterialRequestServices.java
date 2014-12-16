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

public class MaterialRequestServices {

	public static final String module = MaterialRequestServices.class.getName();
	
	public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
	public static final BigDecimal ONE_BASE = BigDecimal.ONE;
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
	public static int salestaxFinalDecimals = UtilNumber.getBigDecimalScale("salestax.final.decimals");
	public static int salestaxCalcDecimals = 2;//UtilNumber.getBigDecimalScale("salestax.calc.decimals");
	
	public static int salestaxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
	

	public static String processCustRequestItems(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String requestDateStr = (String) request.getParameter("requestDate");
	    String requestName = (String) request.getParameter("custRequestName");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	    String partyId = (String) request.getParameter("partyId");
	  	String custRequestTypeId = (String) request.getParameter("custRequestTypeId");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create request without partyId: "+ partyId, module);
			return "error";
		}
		if(UtilValidate.isEmpty(requestName)){
			requestName = "_NA_";
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
			
			Map<String,Object> custRequestInMap = FastMap.newInstance();
			custRequestInMap.put("custRequestTypeId",custRequestTypeId);
			custRequestInMap.put("userLogin",userLogin);
			custRequestInMap.put("currencyUomId","INR");
			custRequestInMap.put("maximumAmountUomId","INR");
			custRequestInMap.put("fromPartyId",partyId);
			custRequestInMap.put("custRequestName",requestName);
			custRequestInMap.put("custRequestDate",requestDate);
	        Map resultMap = dispatcher.runSync("createCustRequest",custRequestInMap);
	        
	        if (ServiceUtil.isError(resultMap)) {
	        	Debug.logError("Problem Filing Request for party :"+partyId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problem Filing Request for party :"+partyId);	
				TransactionUtil.rollback();
		  		return "error";
	        }
	        String custRequestId = (String)resultMap.get("custRequestId");
	        
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
			
				Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("custRequestId",custRequestId);
		        itemInMap.put("statusId","CRQ_DRAFT");
		        itemInMap.put("userLogin",userLogin);
		        itemInMap.put("productId",productId);
		        itemInMap.put("quantity",quantity);
		        itemInMap.put("origQuantity",quantity);
		        resultMap = dispatcher.runSync("createCustRequestItem",itemInMap);
		        
		        if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError("Problem creating Request Item for party :"+partyId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem creating Request Item for party :"+partyId);	
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
	
	public static Map<String, Object> approveRequestByHOD(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String statusId = (String) context.get("statusId");
		String custRequestId = (String) context.get("custRequestId");
		String custRequestItemSeqId = (String) context.get("custRequestItemSeqId");
		BigDecimal acceptedQty = (BigDecimal) context.get("quantity");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		if(UtilValidate.isEmpty(acceptedQty) || (UtilValidate.isNotEmpty(acceptedQty) && acceptedQty.compareTo(BigDecimal.ZERO)<=0)){
			return ServiceUtil.returnError("Cannot Accept Quantity ZERO ");
		}
		try{
			GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
			
			custRequestItem.set("quantity", acceptedQty);
			custRequestItem.store();
			
			Map statusItemCtx = FastMap.newInstance();
			statusItemCtx.put("statusId", statusId);
			statusItemCtx.put("custRequestId", custRequestId);
			statusItemCtx.put("custRequestItemSeqId", custRequestItemSeqId);
			statusItemCtx.put("userLogin", userLogin);
			Map resultCtx = dispatcher.runSync("setCustRequestItemStatus", statusItemCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("RequestItem set status failed for Request: " + custRequestId+":"+custRequestItemSeqId, module);
				return resultCtx;
			}
			
			Map statusCtx = FastMap.newInstance();
			statusCtx.put("statusId", statusId);
			statusCtx.put("custRequestId", custRequestId);
			statusCtx.put("userLogin", userLogin);
			resultCtx = dispatcher.runSync("setCustRequestStatus", statusCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("RequestItem set status failed for Request: " + custRequestId, module);
				return resultCtx;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> setCustRequestItemStatus(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String statusId = (String) context.get("statusId");
		String custRequestId = (String) context.get("custRequestId");
		String custRequestItemSeqId = (String) context.get("custRequestItemSeqId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map result = ServiceUtil.returnSuccess();
		try{
			GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId),  false);
			
			if(UtilValidate.isEmpty(custRequestItem)){
				return ServiceUtil.returnError("No CustRequestItem found with Id "+custRequestId+" : "+custRequestItemSeqId);
			}
			
			String oldStatusId = custRequestItem.getString("statusId");
			if(!oldStatusId.equals(statusId)){
				GenericValue statusValidChange = delegator.findOne("StatusValidChange", UtilMisc.toMap("statusId", oldStatusId, "statusIdTo", statusId), false);
				if(UtilValidate.isEmpty(statusValidChange)){
					Debug.logError("Not a Valid status change for the request", module);
					return ServiceUtil.returnError("Not a Valid status change for the request");
				}
			}
			custRequestItem.set("statusId", statusId);
			custRequestItem.store();
			
			Map inputCtx = FastMap.newInstance();
			inputCtx.put("custRequestId", custRequestId);
			inputCtx.put("custRequestItemSeqId", custRequestItemSeqId);
			inputCtx.put("statusId", statusId);
			inputCtx.put("userLogin", userLogin);
			Map<String, Object> resultMap = createCustRequestStatus(ctx, inputCtx);
			if(ServiceUtil.isError(resultMap)){
				Debug.logError("Error in updating CustRequestItemStatus entity", module);
				return ServiceUtil.returnError("Error in updating CustRequestItemStatus entity");
			}

		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("custRequestId", custRequestId);
		result.put("custRequestItemSeqId", custRequestItemSeqId);
		return result;
	}
	
	public static Map<String, Object> checkRequestStatusByItems(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String custRequestId = (String) context.get("custRequestId");
		String custRequestItemSeqId = (String) context.get("custRequestItemSeqId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map result = ServiceUtil.returnSuccess();
		try{
			
			
			List<GenericValue> custRequestItems = delegator.findList("CustRequestItem", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId), null, null, null, false);
			int itemsSize = custRequestItems.size();
			if(UtilValidate.isEmpty(custRequestItems)){
				return ServiceUtil.returnError("No CustRequestItems found with Id "+custRequestId);
			}
			Map inputCtx = FastMap.newInstance();
			List<GenericValue> cancelledItems = EntityUtil.filterByCondition(custRequestItems, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_CANCELLED"));
			String statusIdTo = "";
			if(cancelledItems.size()==itemsSize){
				statusIdTo = "CRQ_CANCELLED";
			}
			
			List<GenericValue> rejectedItems = EntityUtil.filterByCondition(custRequestItems, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_REJECTED"));
			
			if(rejectedItems.size()==itemsSize){
				statusIdTo = "CRQ_REJECTED";
			}
			
			List<GenericValue> completedItems = EntityUtil.filterByCondition(custRequestItems, EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("CRQ_REJECTED", "CRQ_CANCELLED", "CRQ_COMPLETED")));
			
			if(completedItems.size()==itemsSize){
				statusIdTo = "CRQ_COMPLETED";
			}
			
			if(UtilValidate.isNotEmpty(statusIdTo)){
				inputCtx.put("statusId", statusIdTo);
				inputCtx.put("custRequestId", custRequestId);
				inputCtx.put("userLogin", userLogin);
				
				result = dispatcher.runSync("setCustRequestStatus", inputCtx);
				if(ServiceUtil.isError(result)){
					Debug.logError("Error changing status for request with Id : "+custRequestId, module);
					return ServiceUtil.returnError("Error changing status for request with Id : "+custRequestId);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	
	
	public static Map<String, Object> createCustRequestStatus(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String statusId = (String) context.get("statusId");
		String custRequestId = (String) context.get("custRequestId");
		String custRequestItemSeqId = (String) context.get("custRequestItemSeqId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			
			GenericValue newEntity = delegator.makeValue("CustRequestStatus");
	        newEntity.set("custRequestId", custRequestId);
	        newEntity.set("custRequestItemSeqId", custRequestItemSeqId);
	        newEntity.set("statusId", statusId);
	        newEntity.set("statusDatetime", UtilDateTime.nowTimestamp());
	        newEntity.set("changedByUserLogin", userLogin.getString("userLoginId"));
            delegator.createSetNextSeqId(newEntity);            
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Failed to create a new shipment " + e);            
        }
		return result;
	}
	
	public static Map<String, Object> issueInventoryItemToCustRequestItem(DispatchContext ctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        String custRequestId = (String)context.get("custRequestId");
        String custRequestItemSeqId = (String)context.get("custRequestItemSeqId");
        String facilityId = (String)context.get("facilityId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
        	
        	GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
        	
        	if (custRequestItem == null) {
                return ServiceUtil.returnError("No Request found for Id : "+custRequestId+" and seqId : "+custRequestItemSeqId);
            }
        	String productId = custRequestItem.getString("productId");
        	BigDecimal quantity = custRequestItem.getBigDecimal("quantity");
        	
        	BigDecimal requestedQty = quantity;
        	
            GenericValue product = ProductWorker.findProduct(delegator, productId);
            if (product == null) {
                return ServiceUtil.returnError("Product Not Found with Id : "+productId);
            }
            
            /* We need to get facilityId from ProductFacility */
            if(UtilValidate.isEmpty(facilityId)){
            	List<GenericValue> productFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
            	if(UtilValidate.isEmpty(productFacility)){
            		return ServiceUtil.returnError("Product not configured to any ProductFacility");
            	}
            	GenericValue prodFacility = EntityUtil.getFirst(productFacility);
            	facilityId = prodFacility.getString("facilityId");
            }
            
            Map<String, ? extends Object> findCurrInventoryParams =  UtilMisc.toMap("productId", productId, "facilityId", facilityId);
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            // Call issuance service
            
            Map<String, Object> resultCtx = dispatcher.runSync("getInventoryAvailableByFacility", findCurrInventoryParams);
            if (ServiceUtil.isError(resultCtx)) {
                return ServiceUtil.returnError("Problem getting inventory level of the request for product Id :"+productId);
            }
            Object atpObj = resultCtx.get("availableToPromiseTotal");
            BigDecimal atp = BigDecimal.ZERO;
            if (atpObj != null) {
                atp = new BigDecimal(atpObj.toString());
            }
            if (requestedQty.compareTo(atp) > 0) {
                return ServiceUtil.returnError("Available Inventory level for productId : "+productId + " is "+atp);
            }
            
            List conditionList = FastList.newInstance();
            conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            conditionList.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
            if(UtilValidate.isNotEmpty(facilityId)){
            	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
            }
            EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            List<GenericValue> inventoryItems = delegator.findList("InventoryItem", condition, null, UtilMisc.toList("datetimeReceived"), null, false);
            Iterator<GenericValue> itr = inventoryItems.iterator();
            while ((requestedQty.compareTo(BigDecimal.ZERO) > 0) && itr.hasNext()) {
                GenericValue inventoryItem = itr.next();
                String inventoryItemId = inventoryItem.getString("inventoryItemId");
                atp = inventoryItem.getBigDecimal("availableToPromiseTotal");
                findCurrInventoryParams = UtilMisc.toMap("inventoryItemId", inventoryItemId);
                BigDecimal issueQuantity = null;
                if (requestedQty.compareTo(atp) > 0) {	
                    issueQuantity = atp;
                } else {
                    issueQuantity = requestedQty;
                }
                Map<String, Object> itemIssuanceCtx = FastMap.newInstance();
                itemIssuanceCtx.put("userLogin", userLogin);
                itemIssuanceCtx.put("inventoryItemId", inventoryItemId);
                itemIssuanceCtx.put("custRequestId", custRequestId);
                itemIssuanceCtx.put("custRequestItemSeqId", custRequestItemSeqId);
                itemIssuanceCtx.put("quantity", issueQuantity);
                // Call issuance service
                resultCtx = dispatcher.runSync("createIssuanceForCustRequestItem",itemIssuanceCtx);
                if (ServiceUtil.isError(resultCtx)) {
                    return ServiceUtil.returnError("Error in service issueInventoryItemToCustRequest");
                }
                requestedQty = requestedQty.subtract(issueQuantity);
            }
            result = ServiceUtil.returnSuccess("Successfully issued item :"+productId);
        } catch (GenericEntityException e) {
            Debug.logError("Problem in retriving data from database", module);
        } catch (GenericServiceException e) {
            Debug.logError("Problem in calling service issueInventoryItemToCustRequest", module);
            return ServiceUtil.returnError("Problem in calling service issueInventoryItemToCustRequest");
        }
        return result;
    }
	
	
	public static Map<String, Object> createIssuanceForCustRequestItem(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String custRequestId = (String) context.get("custRequestId");
		String custRequestItemSeqId = (String) context.get("custRequestItemSeqId");
		String inventoryItemId = (String) context.get("inventoryItemId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			
			if(UtilValidate.isEmpty(quantity) || (UtilValidate.isNotEmpty(quantity) && quantity.compareTo(BigDecimal.ZERO)<= 0)){
				return ServiceUtil.returnError("Not issuing InventoryItem to Request "+custRequestId+" : "+custRequestItemSeqId+", because the quantity to issue "+quantity+" is less than or equal to 0");
			}
			GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
			
			GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
			
			BigDecimal inventoryATP = inventoryItem.getBigDecimal("availableToPromiseTotal");
			
			if(quantity.compareTo(inventoryATP)>0){
				return ServiceUtil.returnError("Not issuing InventoryItem to Request "+custRequestId+" : "+custRequestItemSeqId+", because the quantity to issue "+quantity+" is greater than the quantity left to issue (i.e "+inventoryATP+") for inventoryItemId : "+inventoryItemId);
			}
			/*Create Item Issuance*/
			Map itemIssueCtx = FastMap.newInstance();
			itemIssueCtx.put("custRequestId", custRequestId);
			itemIssueCtx.put("custRequestItemSeqId", custRequestItemSeqId);
			itemIssueCtx.put("userLogin", userLogin);
			itemIssueCtx.put("inventoryItemId", inventoryItemId);
			itemIssueCtx.put("productId", custRequestItem.getString("productId"));
			itemIssueCtx.put("quantity", quantity);
			itemIssueCtx.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
			itemIssueCtx.put("modifiedByUserLoginId", userLogin.getString("userLoginId"));
			itemIssueCtx.put("modifiedDateTime", UtilDateTime.nowTimestamp());
			Map resultCtx = dispatcher.runSync("createItemIssuance", itemIssueCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Problem creating item issuance for requested item", module);
				return resultCtx;
			}
			
			String itemIssuanceId = (String)resultCtx.get("itemIssuanceId");
			/*Decrement inventory*/
			Map createInvDetail = FastMap.newInstance();
			createInvDetail.put("custRequestId", custRequestId);
			createInvDetail.put("custRequestItemSeqId", custRequestItemSeqId);
			createInvDetail.put("userLogin", userLogin);
			createInvDetail.put("inventoryItemId", inventoryItemId);
			createInvDetail.put("itemIssuanceId", itemIssuanceId);
			createInvDetail.put("quantityOnHandDiff", quantity.negate());
			createInvDetail.put("availableToPromiseDiff", quantity.negate());
			resultCtx = dispatcher.runSync("createInventoryItemDetail", createInvDetail);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Problem decrementing inventory for requested item ", module);
				return resultCtx;
			}
			
			Map itemStatusCtx = FastMap.newInstance();
			itemStatusCtx.put("custRequestId", custRequestId);
			itemStatusCtx.put("custRequestItemSeqId", custRequestItemSeqId);
			itemStatusCtx.put("userLogin", userLogin);
			itemStatusCtx.put("statusId", "CRQ_ISSUED");
			resultCtx = dispatcher.runSync("setCustRequestItemStatus", itemStatusCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Problem changing status for requested item ", module);
				return resultCtx;
			}
			
			
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		catch (GenericServiceException e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> createRequirementForCustRequestItem(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String custRequestId = (String) context.get("custRequestId");
		String custRequestItemSeqId = (String) context.get("custRequestItemSeqId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			
			GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
			
			GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_CREATED"));
			conditionList.add(EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, "PRODUCT_REQUIREMENT"));
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, custRequestItem.getString("productId")));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> requirements = delegator.findList("Requirement", condition, null, null, null, false);
			
			Map resultCtx = FastMap.newInstance();
			String requirementId = "";
			if(UtilValidate.isNotEmpty(requirements)){
				GenericValue requirement = EntityUtil.getFirst(requirements);
				requirementId = requirement.getString("requirementId");
				BigDecimal qty = requirement.getBigDecimal("quantity");
				requirement.set("quantity", qty.add(custRequestItem.getBigDecimal("quantity")));
				requirement.set("lastModifiedDate", UtilDateTime.nowTimestamp());
				requirement.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				requirement.store();
				
				Map associateReqCtx = FastMap.newInstance();
				associateReqCtx.put("userLogin", userLogin);
				associateReqCtx.put("requirementId", requirementId);
				associateReqCtx.put("custRequestId", custRequestId);
				associateReqCtx.put("custRequestItemSeqId", custRequestItemSeqId);
				resultCtx = dispatcher.runSync("associatedRequirementWithRequestItem", associateReqCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("Problem associating requirement to requested item : "+requirementId+" : ["+custRequestId+" : "+custRequestItemSeqId+"]", module);
					return resultCtx;
				}
				
			}
			else{
				/*Create requirement*/
				Map itemIssueCtx = FastMap.newInstance();
				itemIssueCtx.put("custRequestId", custRequestId);
				itemIssueCtx.put("custRequestItemSeqId", custRequestItemSeqId);
				itemIssueCtx.put("userLogin", userLogin);
				itemIssueCtx.put("productId", custRequestItem.getString("productId"));
				itemIssueCtx.put("requirementStartDate", UtilDateTime.nowTimestamp());
				itemIssueCtx.put("requirementTypeId", custRequest.getString("custRequestTypeId"));
				itemIssueCtx.put("quantity", custRequestItem.getBigDecimal("quantity"));
				resultCtx = dispatcher.runSync("createRequirement", itemIssueCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("Problem creating requirement for requested item : "+custRequestId+" : "+custRequestItemSeqId, module);
					return resultCtx;
				}
				
				requirementId = (String)resultCtx.get("requirementId");
				
			}
			
			
			/* change cust request item status*/
			Map itemStatusCtx = FastMap.newInstance();
			itemStatusCtx.put("custRequestId", custRequestId);
			itemStatusCtx.put("custRequestItemSeqId", custRequestItemSeqId);
			itemStatusCtx.put("userLogin", userLogin);
			itemStatusCtx.put("statusId", "CRQ_INPROCESS");
			resultCtx = dispatcher.runSync("setCustRequestItemStatus", itemStatusCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Problem changing status for requested item ", module);
				return resultCtx;
			}
			
			
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		catch (GenericServiceException e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result = ServiceUtil.returnSuccess("Successfully create requirement for the request "+custRequestId+" : "+custRequestItemSeqId);
		return result;
	}
	
	//send Requirements for Enquiry
	public static String draftEnquiryForApprovedRequirementsEvent(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		
		Timestamp requestDate = null;
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
	  	
		boolean beganTransaction = false;
		try{
			
	        List requirementIds = FastList.newInstance();
	        String requirementId = "";
			for (int i = 0; i < rowCount; i++) {
				  
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (!(paramMap.containsKey("requirementId" + thisSuffix)) || UtilValidate.isEmpty(paramMap.get("requirementId" + thisSuffix))) {
					request.setAttribute("_ERROR_MESSAGE_", "Missing requirement id");
					return "error";
					
				}
				if (UtilValidate.isNotEmpty(paramMap.get("requirementId" + thisSuffix))) {
					requirementId = (String) paramMap.get("requirementId" + thisSuffix);
					requirementIds.add(requirementId);
				}
			}
			//
			Map<String,Object> enquiryRequestInMap = FastMap.newInstance();
			enquiryRequestInMap.put("userLogin",userLogin);
			enquiryRequestInMap.put("requirementList",requirementIds);
			
	        Map resultMap = dispatcher.runSync("draftEnquiryForApprovedRequirements",enquiryRequestInMap);
	        
	        if (ServiceUtil.isError(resultMap)) {
	        	Debug.logError("Problem Filing Enquiry for requirements :"+requirementIds, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problem Filing Enquiry for requirements :"+requirementIds);	
				TransactionUtil.rollback();
		  		return "error";
	        }
	        String custRequestId = (String)resultMap.get("custRequestId");
	        request.setAttribute("_EVENT_MESSAGE_", "Successfully made Enquiry ID:"+custRequestId);
			
		}catch (GenericEntityException e) {
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
		return "success";
    }
	
	public static String updateRequestAcknowledgmentStatus(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    HttpSession session = request.getSession();
	    String custRequestItemStatusId = (String) request.getParameter("custRequestItemStatusId");
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
	  	
	  	
		boolean beganTransaction = false;
		try{
			
	        String custRequestId = "";
	        String custRequestItemSeqId = "";
	        Map statusCtx = FastMap.newInstance();
			for (int i = 0; i < rowCount; i++) {
				  
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				
				if (paramMap.containsKey("custRequestId" + thisSuffix)) {
					custRequestId = (String) paramMap.get("custRequestId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing custRequeset Id");
				}
				
				if (paramMap.containsKey("custRequestItemSeqId" + thisSuffix)) {
					custRequestItemSeqId = (String) paramMap.get("custRequestItemSeqId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing custRequestItemSeqId");
				}
				
				statusCtx.clear();
				statusCtx.put("statusId", custRequestItemStatusId);
				statusCtx.put("custRequestId", custRequestId);
				statusCtx.put("custRequestItemSeqId", custRequestItemSeqId);
				statusCtx.put("userLogin", userLogin);
				Map resultCtx = dispatcher.runSync("setCustRequestItemStatus", statusCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("RequestItem set status failed for Request: " + custRequestId+" : "+custRequestItemSeqId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem changing request status :"+custRequestId+":"+custRequestItemSeqId);	
					TransactionUtil.rollback();
					return "error";
				}
			}
			
	        request.setAttribute("_EVENT_MESSAGE_", "User Department acknowledgement successful");
			
		}catch (GenericEntityException e) {
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
		return "success";
    }
	
	
	public static Map<String, Object> draftEnquiryForApprovedRequirements(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List requirementList = (List) context.get("requirementIds");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		String custRequestTypeId = "RF_PUR_QUOTE";
		result = ServiceUtil.returnSuccess("Successfully create Enquiry for the requirements");
		try{
			
			/*GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
			
			GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
			*/
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("requirementId", EntityOperator.IN, requirementList));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> requirements = delegator.findList("Requirement", condition, UtilMisc.toSet("requirementId","productId","quantity"), null, null, false);
			
			Map resultCtx = FastMap.newInstance();
			if(UtilValidate.isEmpty(requirements)){
				Debug.logError("Requirents not found.", module);
				return ServiceUtil.returnError("Requirents not found.");
			}
			
			Map<String,Object> custRequestInMap = FastMap.newInstance();
			custRequestInMap.put("custRequestTypeId",custRequestTypeId);
			custRequestInMap.put("userLogin",userLogin);
			custRequestInMap.put("currencyUomId","INR");
			custRequestInMap.put("maximumAmountUomId","INR");
			custRequestInMap.put("fromPartyId","Company");
			custRequestInMap.put("custRequestDate",UtilDateTime.nowTimestamp());
	        Map resultMap = dispatcher.runSync("createCustRequest",custRequestInMap);
	        
	        if (ServiceUtil.isError(resultMap)) {
	        	Debug.logError("Problem Filing Enquiry.", module);
		  		return ServiceUtil.returnError("Problem Filing Enquiry.");
	        }
	        String custRequestId = (String)resultMap.get("custRequestId");
	        result.put("custRequestId", custRequestId);
	        
			for(GenericValue requirement: requirements){
				String productId = requirement.getString("productId");
				BigDecimal quantity = requirement.getBigDecimal("quantity");
				String requirementId = requirement.getString("requirementId");
				Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("custRequestId",custRequestId);
		        itemInMap.put("statusId","CRQ_DRAFT");
		        itemInMap.put("userLogin",userLogin);
		        itemInMap.put("productId",productId);
		        itemInMap.put("quantity",quantity);
		        resultMap = dispatcher.runSync("createCustRequestItem",itemInMap);
		        if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError("Problem Filing Enquiry.", module);
			  		return ServiceUtil.returnError("Problem Filing Enquiry.");
		        }
		        String custRequestItemSeqId = (String)resultMap.get("custRequestItemSeqId");
				Map associateReqCtx = FastMap.newInstance();
				associateReqCtx.put("userLogin", userLogin);
				associateReqCtx.put("requirementId", requirementId);
				associateReqCtx.put("custRequestId", custRequestId);
				associateReqCtx.put("custRequestItemSeqId", custRequestItemSeqId);
				resultCtx = dispatcher.runSync("associatedRequirementWithRequestItem", associateReqCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("Problem associating requirement to requested item : "+requirementId+" : ["+custRequestId+" : "+custRequestItemSeqId+"]", module);
					return resultCtx;
				}
				requirement.set("statusId", "REQ_IN_ENQUIRY");
				delegator.store(requirement);
			}
		
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		catch (GenericServiceException e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		return result;
	}
	
}
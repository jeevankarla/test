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
import org.ofbiz.service.ModelService;
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
	    String responseDateStr = (String) request.getParameter("responseDate");
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
		Timestamp responseDate = null;
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
	  	if(UtilValidate.isNotEmpty(responseDateStr)){
	  		try {
	  			responseDate = new java.sql.Timestamp(sdf.parse(responseDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + responseDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + responseDateStr, module);
		  	}
	  	}else{
	  		responseDate = UtilDateTime.nowTimestamp();
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
			custRequestInMap.put("responseRequiredDate",responseDate);
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
		        itemInMap.put("description","");
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
	
	public static Map<String, Object> processCustRequestParty(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String custRequestId = (String) context.get("custRequestId");
		String roleTypeId="SUPPLIER";
		//String fromDate= UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "-dd/MM/yyyy HH:mm:ss");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		List<GenericValue> custReqItemDetails = FastList.newInstance();
		List<GenericValue> supplierDetails = FastList.newInstance();
		List productIds=FastList.newInstance();

		condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
		EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
		try{

			custReqItemDetails = delegator.findList("CustRequestItem", cond, null,null, null, false);
		   productIds=EntityUtil.getFieldListFromEntityList(custReqItemDetails, "productId", true);			
			 supplierDetails = delegator.findList("SupplierProduct", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null,null, null, false);		
				for(GenericValue custReq : supplierDetails){
					Map inputCtx = FastMap.newInstance();
					inputCtx.put("partyId", custReq.getString("partyId"));
					inputCtx.put("custRequestId", custRequestId);
					inputCtx.put("roleTypeId",roleTypeId);
		//			inputCtx.put("fromDate", fromDate);
					inputCtx.put("userLogin", userLogin);
					Map resultCtx = dispatcher.runSync("createCustRequestParty", inputCtx);
					if (ServiceUtil.isError(resultCtx)) {
						Debug.logError("RequestItem set status failed for Request: " + custRequestId+" : "+custReq.getString("partyId"), module);
						return ServiceUtil.returnError("Error occuring while calling createCustRequestParty service:");
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
		String description = (String) context.get("description");
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
			statusItemCtx.put("description", description);
			statusItemCtx.put("custRequestItemSeqId", custRequestItemSeqId);
			statusItemCtx.put("userLogin", userLogin);
			statusItemCtx.put("description", "");
			Map resultCtx = dispatcher.runSync("setCustRequestItemStatus", statusItemCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("RequestItem set status failed for Request: " + custRequestId+":"+custRequestItemSeqId, module);
				return resultCtx;
			}
			
			List condList=FastList.newInstance();
			List<GenericValue> custRequestItems = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_DRAFT"));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			custRequestItems = delegator.findList("CustRequestItem", cond,null,null,null,false);
			if(custRequestItems.size()==0){
				Map statusCtx = FastMap.newInstance();
				statusCtx.put("statusId", statusId);
				statusCtx.put("custRequestId", custRequestId);
				statusCtx.put("userLogin", userLogin);
				resultCtx = dispatcher.runSync("setCustRequestStatus", statusCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("RequestItem set status failed for Request: " + custRequestId, module);
					return resultCtx;
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result = ServiceUtil.returnSuccess("Indent Successfully Accepted!!");
		return result;
	}
	
	public static Map<String, Object> setCustRequestItemStatus(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String statusId = (String) context.get("statusId");
		String custRequestId = (String) context.get("custRequestId");
		String custRequestItemSeqId = (String) context.get("custRequestItemSeqId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String description = (String) context.get("description");
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
			
			List condList=FastList.newInstance();
			List<GenericValue> custRequestItems = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
			if(statusId.equals("CRQ_ISSUED")){
			condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_SUBMITTED"), EntityOperator.OR, EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"CRQ_DRAFT")));
			}
			if(statusId.equals("CRQ_REJECTED")){
				condList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("CRQ_SUBMITTED","CRQ_DRAFT","CRQ_ISSUED","CRQ_COMPLETED")));
			}
			if(statusId.equals("CRQ_COMPLETED")){
				condList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("CRQ_SUBMITTED","CRQ_DRAFT","CRQ_ISSUED")));
			}
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			custRequestItems = delegator.findList("CustRequestItem", cond,null,null,null,false);
			if(custRequestItems.size()==0){
				GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId),  false);
				custRequest.set("statusId", statusId);
				custRequest.store();
			}
			if((custRequestItems.size()>0) && (statusId.equals("CRQ_REJECTED"))){
				GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId),  false);
				custRequest.set("statusId", "CRQ_SUBMITTED");
				custRequest.store();
			}
			
			Map inputCtx = FastMap.newInstance();
			inputCtx.put("custRequestId", custRequestId);
			inputCtx.put("custRequestItemSeqId", custRequestItemSeqId);
			inputCtx.put("comments", description);
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
		if((statusId).equals("CRQ_REJECTED")){
			result = ServiceUtil.returnSuccess("Indent Successfully Rejected!!");
		}
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
		String comments = (String) context.get("comments");
		Map result = ServiceUtil.returnSuccess();
		try{
			
			GenericValue newEntity = delegator.makeValue("CustRequestStatus");
	        newEntity.set("custRequestId", custRequestId);
	        newEntity.set("custRequestItemSeqId", custRequestItemSeqId);
	        newEntity.set("statusId", statusId);
	        newEntity.set("comments", comments);
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
        BigDecimal toBeIssuedQty =(BigDecimal)context.get("toBeIssuedQty");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
        	
        	GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
        	
        	if (custRequestItem == null) {
                return ServiceUtil.returnError("No Request found for Id : "+custRequestId+" and seqId : "+custRequestItemSeqId);
            }
        	String productId = custRequestItem.getString("productId");
        	BigDecimal quantity = custRequestItem.getBigDecimal("quantity");
        	
        	BigDecimal requestedQty = quantity;
        	
        	
        	if (UtilValidate.isNotEmpty(toBeIssuedQty)) {
        		requestedQty=toBeIssuedQty;
        		
            }
        	if(quantity.compareTo(toBeIssuedQty) < 0){
        		Debug.logError("You can't issue more than requested Quantity", module);
        		return ServiceUtil.returnError("You can't issue more than requested Quantity");
        	}
            GenericValue product = ProductWorker.findProduct(delegator, productId);
            if (product == null) {
                return ServiceUtil.returnError("Product Not Found with Id : "+productId);
            }
            
            String shipmentId = "";
            try{
    			
    			GenericValue newEntity = delegator.makeValue("Shipment");
    	        newEntity.set("shipmentTypeId", "ISSUANCE_SHIPMENT");
    	        newEntity.set("statusId", "GENERATED");
    	        newEntity.set("estimatedShipDate", UtilDateTime.nowTimestamp());
    	        newEntity.set("createdByUserLogin", userLogin.getString("userLoginId"));
    	        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
    	        newEntity.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
    	        newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
                delegator.createSetNextSeqId(newEntity);
                
                shipmentId = newEntity.getString("shipmentId");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Failed to create a new shipment " + e);            
            }
            Debug.log("shipmentId #######################"+shipmentId);
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
            // Call issuance service
            
            Map<String, Object> resultCtx = dispatcher.runSync("getInventoryAvailableByFacility", findCurrInventoryParams);
            if (ServiceUtil.isError(resultCtx)) {
            	Debug.logError("Problem getting inventory level of the request for product Id :"+productId, module);
                return ServiceUtil.returnError("Problem getting inventory level of the request for product Id :"+productId);
            }
            Object qohObj = resultCtx.get("quantityOnHandTotal");
            BigDecimal qoh = BigDecimal.ZERO;
            if (qohObj != null) {
            	qoh = new BigDecimal(qohObj.toString());
            }
            Debug.log("toBeIssuedQty=========="+toBeIssuedQty);
            if (toBeIssuedQty.compareTo(qoh) > 0) {
            	Debug.logError("Available Inventory level for productId : "+productId + " is "+qoh, module);
                return ServiceUtil.returnError("Available Inventory level for productId : "+productId + " is "+qoh);
            }
            
            List conditionList = FastList.newInstance();
            conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
            if(UtilValidate.isNotEmpty(facilityId)){
            	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
            }
            EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            List<GenericValue> inventoryItems = delegator.findList("InventoryItem", condition, null, UtilMisc.toList("datetimeReceived"), null, false);
            Iterator<GenericValue> itr = inventoryItems.iterator();
            while ((requestedQty.compareTo(BigDecimal.ZERO) > 0) && itr.hasNext()) {
                GenericValue inventoryItem = itr.next();
                String inventoryItemId = inventoryItem.getString("inventoryItemId");
                qoh = inventoryItem.getBigDecimal("quantityOnHandTotal");
                findCurrInventoryParams = UtilMisc.toMap("inventoryItemId", inventoryItemId);
                BigDecimal issueQuantity = null;
                if (requestedQty.compareTo(qoh) >= 0) {	
                    issueQuantity = qoh;
                } else {
                    issueQuantity = requestedQty;
                }
                Map<String, Object> itemIssuanceCtx = FastMap.newInstance();
                itemIssuanceCtx.put("userLogin", userLogin);
                itemIssuanceCtx.put("inventoryItemId", inventoryItemId);
                itemIssuanceCtx.put("shipmentId", shipmentId);
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
		String shipmentId = (String) context.get("shipmentId");
		BigDecimal requestedQuantity = BigDecimal.ZERO;
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		GenericValue custRequestItem=null;
		Map result = ServiceUtil.returnSuccess();
		try{
			
			if(UtilValidate.isEmpty(quantity) || (UtilValidate.isNotEmpty(quantity) && quantity.compareTo(BigDecimal.ZERO)<= 0)){
				return ServiceUtil.returnError("Not issuing InventoryItem to Request "+custRequestId+" : "+custRequestItemSeqId+", because the quantity to issue "+quantity+" is less than or equal to 0");
			}
			 custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
			
			requestedQuantity = custRequestItem.getBigDecimal("quantity");
			
			GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
			
			BigDecimal inventoryQOH = inventoryItem.getBigDecimal("quantityOnHandTotal");
			
			if(quantity.compareTo(inventoryQOH)>0){
				return ServiceUtil.returnError("Not issuing InventoryItem to Request "+custRequestId+" : "+custRequestItemSeqId+", because the quantity to issue "+quantity+" is greater than the quantity left to issue (i.e "+inventoryQOH+") for inventoryItemId : "+inventoryItemId);
			}
			//caliculating issuence Qty
			BigDecimal issuedQty=BigDecimal.ZERO;
			List filterIssuenceReq = FastList.newInstance();
			filterIssuenceReq.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
			filterIssuenceReq.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, custRequestItemSeqId));
			if(UtilValidate.isNotEmpty(shipmentId)){
				filterIssuenceReq.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			}
			EntityCondition filterIssuenceCond = EntityCondition.makeCondition(filterIssuenceReq, EntityOperator.AND);
			List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", filterIssuenceCond, UtilMisc.toSet("quantity"), UtilMisc.toList("-issuedDateTime"), null, false);
			
			Iterator<GenericValue> itrIssList = itemIssuanceList.iterator();
            while (itrIssList.hasNext()) {
                GenericValue itrIssItem = itrIssList.next();
                issuedQty =issuedQty.add(itrIssItem.getBigDecimal("quantity"));
            }
            issuedQty=issuedQty.add(quantity);
            
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
			
			GenericValue itemIssuance = delegator.findOne("ItemIssuance", UtilMisc.toMap("itemIssuanceId", itemIssuanceId), false);
			itemIssuance.set("shipmentId", shipmentId);
			itemIssuance.store();
			
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
			//comparing issuedQty and requestedQty
			if(issuedQty.compareTo(requestedQuantity)==0){
				Map itemStatusCtx = FastMap.newInstance();
				itemStatusCtx.put("custRequestId", custRequestId);
				itemStatusCtx.put("custRequestItemSeqId", custRequestItemSeqId);
				itemStatusCtx.put("userLogin", userLogin);
				itemStatusCtx.put("description", "");
				itemStatusCtx.put("statusId", "CRQ_ISSUED");
				resultCtx = dispatcher.runSync("setCustRequestItemStatus", itemStatusCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("Problem changing status for requested item ", module);
					return resultCtx;
				}
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
		BigDecimal custmQuantity = (BigDecimal) context.get("custmQuantity");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		String requirementId = "";

		try{
			
			GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
			
			GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
			String facilityId = "";
			String productId = custRequestItem.getString("productId");
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_CREATED"));
			conditionList.add(EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, "PRODUCT_REQUIREMENT"));
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> requirements = delegator.findList("Requirement", condition, null, null, null, false);
			
			List<GenericValue> prodFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
			
			if(UtilValidate.isEmpty(prodFacility)){
				Debug.logError("Product is not mapped to any Store", module);
				return ServiceUtil.returnError("Product is not mapped to any Store");
			}
			
			facilityId = (EntityUtil.getFirst(prodFacility)).getString("facilityId");
			
			Map resultCtx = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(requirements)){
				GenericValue requirement = EntityUtil.getFirst(requirements);
				requirementId = requirement.getString("requirementId");
				BigDecimal qty = requirement.getBigDecimal("quantity");
				requirement.set("quantity", qty.add(custmQuantity));
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
				itemIssueCtx.put("facilityId", facilityId);
				itemIssueCtx.put("productId", custRequestItem.getString("productId"));
				itemIssueCtx.put("requirementStartDate", UtilDateTime.nowTimestamp());
				itemIssueCtx.put("requirementTypeId", custRequest.getString("custRequestTypeId"));
				itemIssueCtx.put("quantity", custmQuantity);
				resultCtx = dispatcher.runSync("createRequirement", itemIssueCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("Problem creating requirement for requested item : "+custRequestId+" : "+custRequestItemSeqId, module);
					return resultCtx;
				}
				
				requirementId = (String)resultCtx.get("requirementId");
				
			}
			/*if(UtilValidate.isNotEmpty(requirementId)){
				Map emailRequirementCtx = FastMap.newInstance();
				emailRequirementCtx.put("userLogin", userLogin);
				emailRequirementCtx.put("requirementId", requirementId);
				resultCtx = dispatcher.runSync("emailRequirement", emailRequirementCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("Problem creating requirement for requested item : "+custRequestId+" : "+custRequestItemSeqId, module);
					return resultCtx;
				}
			}*/
			
			/* change cust request item status*/
			Map itemStatusCtx = FastMap.newInstance();
			itemStatusCtx.put("custRequestId", custRequestId);
			itemStatusCtx.put("custRequestItemSeqId", custRequestItemSeqId);
			itemStatusCtx.put("userLogin", userLogin);
			itemStatusCtx.put("description", "");
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
		result = ServiceUtil.returnSuccess("Successfully created requirement: "+requirementId+" for the request "+custRequestId+" : "+custRequestItemSeqId);
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
				statusCtx.put("description", "");
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
		String custRequestName = (String) context.get("enquiryName");
		String requestDateStr = (String) context.get("requestDate");
		String openDateStr = (String) context.get("requestDate");
		String closedDateStr = (String) context.get("closedDate");
		Map result = ServiceUtil.returnSuccess();
		String custRequestTypeId = "RF_PUR_QUOTE";
		Timestamp custRequestDate = null;
		Timestamp openDateTime = null;
		Timestamp closedDateTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
	  	if(UtilValidate.isNotEmpty(requestDateStr)){
	  		try {
	  			custRequestDate = new java.sql.Timestamp(sdf.parse(requestDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + requestDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + requestDateStr, module);
		  	}
	  	}
	  	else{
	  		custRequestDate = UtilDateTime.nowTimestamp();
	  	}
	  	if(UtilValidate.isNotEmpty(openDateStr)){
	  		try {
	  			openDateTime = new java.sql.Timestamp(sdf.parse(openDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + openDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + openDateStr, module);
		  	}
	  	}
	  	else{
	  		openDateTime = UtilDateTime.nowTimestamp();
	  	}
	  	if(UtilValidate.isNotEmpty(closedDateStr)){
	  		try {
	  			closedDateTime = new java.sql.Timestamp(sdf.parse(closedDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + closedDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + closedDateStr, module);
		  	}
	  	}
	  	else{
	  		closedDateTime = UtilDateTime.nowTimestamp();
	  		closedDateTime = UtilDateTime.getDayEnd(closedDateTime);
	  		closedDateTime = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(closedDateTime, 7));
	  	}
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
			custRequestInMap.put("custRequestName",custRequestName);
			custRequestInMap.put("currencyUomId","INR");
			custRequestInMap.put("maximumAmountUomId","INR");
			custRequestInMap.put("fromPartyId","Company");
			custRequestInMap.put("statusId","ENQ_CREATED");
			custRequestInMap.put("custRequestDate",custRequestDate);
			custRequestInMap.put("openDateTime",openDateTime);
			custRequestInMap.put("closedDateTime",closedDateTime);
	        Map resultMap = dispatcher.runSync("createCustRequest",custRequestInMap);
	        
	        if (ServiceUtil.isError(resultMap)) {
	        	Debug.logError("Problem Filing Enquiry.", module);
		  		return ServiceUtil.returnError("Problem Filing Enquiry.");
	        }
	        String custRequestId = (String)resultMap.get("custRequestId");
	        
			for(GenericValue requirement: requirements){
				String productId = requirement.getString("productId");
				BigDecimal quantity = requirement.getBigDecimal("quantity");
				String requirementId = requirement.getString("requirementId");
				Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("custRequestId",custRequestId);
		        itemInMap.put("statusId","ENQ_CREATED");
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
				/*requirement.set("statusId", "REQ_IN_ENQUIRY");
				delegator.store(requirement);*/
				Map reqMap = FastMap.newInstance();
				reqMap.put("userLogin",userLogin);
				reqMap.put("requirementId",requirementId);
				reqMap.put("statusId","REQ_IN_ENQUIRY");
				resultCtx = dispatcher.runSync("updateRequirement", reqMap);
				if(ServiceUtil.isError(resultCtx)){
					return resultCtx;
				}
			}
		result = ServiceUtil.returnSuccess("Enquiry created Successfully for the requirements...!Enquiry No:"+custRequestId);
		result.put("custRequestId", custRequestId);
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
	 public static Map<String, Object> emailRequirement(DispatchContext ctx, Map<String, ? extends Object> context) {
		 Delegator delegator = ctx.getDelegator();
		 LocalDispatcher dispatcher = ctx.getDispatcher();
		 String defaultScreenLocation = "component://materialmgmt/widget/MaterialPurchaseScreens.xml#RequirementEmail";
		 Locale locale = (Locale) context.get("locale");
		 GenericValue userLogin = (GenericValue)context.get("userLogin");
		 String requirementId = (String)context.get("requirementId");
		 String errMsg ="";
        boolean emailSent = true;
        String bodyScreenLocation = defaultScreenLocation;
        Map<String, Object> result = ServiceUtil.returnSuccess();
        // set the needed variables in new context
        Map<String, Object> bodyParameters = FastMap.newInstance();
        bodyParameters.put("requirementId", requirementId);
        bodyParameters.put("locale", locale);
        bodyParameters.put("userLogin", userLogin);

        try {
        	GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
        	bodyParameters.put("quantity", requirement.getBigDecimal("quantity"));
        	Map<String, Object> serviceContext = FastMap.newInstance();
            serviceContext.put("bodyScreenUri", bodyScreenLocation);
            serviceContext.put("bodyParameters", bodyParameters);
            serviceContext.put("subject", "Requirement Raised for Product :"+requirement.getString("productId"));
            serviceContext.put("sendFrom", UtilProperties.getPropertyValue("general.properties", "defaultFromEmailAddress"));
            serviceContext.put("sendCc", UtilProperties.getPropertyValue("general.properties", "defaultFromEmailAddress"));
            serviceContext.put("contentType", "text/html");
            serviceContext.put("sendTo", "nagababu@vasista.in");
           // serviceContext.put("partyId", party.getString("partyId"));
            serviceContext.put("partyId", userLogin.getString("partyId"));
        	result = dispatcher.runSync("sendMailFromScreen", serviceContext);

            if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
                errMsg = ServiceUtil.getErrorMessage(result);
                Debug.logError(errMsg, module);
                emailSent = false;
                return result;
            }
        } catch (Exception e) {
            Debug.logWarning(e, "", module);
            errMsg = e.toString();
            Debug.logError(errMsg, module);
            emailSent = false;
            return result;
        }
           
        return ServiceUtil.returnSuccess();
    }
	 //cancel issuence for request   
	 
	 public static Map<String, Object> cancelIssuenceForCustRequest(DispatchContext ctx, Map<String, Object> context) {
	        LocalDispatcher dispatcher = ctx.getDispatcher();
	        Delegator delegator = ctx.getDelegator();
	        Locale locale = (Locale) context.get("locale");
	        String custRequestId = (String)context.get("custRequestId");
	        String custRequestItemSeqId = (String)context.get("custRequestItemSeqId");
	        String itemIssuanceId = (String)context.get("itemIssuanceId");
	        String shipmentId = (String)context.get("shipmentId");
	        String facilityId = (String)context.get("facilityId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        try {
	        	String productId="";
	        	/*GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
	        	
	        	if (custRequestItem == null) {
	                return ServiceUtil.returnError("No Request found for Id : "+custRequestId+" and seqId : "+custRequestItemSeqId);
	            }*/
	        	GenericValue issuanceAndShipmentAndCustRequest= null;
	        	List<GenericValue> issuanceAndShipmentAndCustRequestList = delegator.findList("IssuanceAndShipmentAndCustRequest", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false);
	        	
	        	if(UtilValidate.isNotEmpty(issuanceAndShipmentAndCustRequestList)){
	        		issuanceAndShipmentAndCustRequest = EntityUtil.getFirst(issuanceAndShipmentAndCustRequestList);
	        	    productId=issuanceAndShipmentAndCustRequest.getString("productId");
	        	}
	        	
                List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false);
	            for(GenericValue itemShipIssuence:itemIssuanceList){
	            	
	                itemIssuanceId=itemShipIssuence.getString("itemIssuanceId");
	            	BigDecimal actQuantity = itemShipIssuence.getBigDecimal("quantity");
	            	BigDecimal cancelQuantity = actQuantity;
	                
	            	//update isssuenceItem
	            	Map itemIssueCtx = FastMap.newInstance();
					itemIssueCtx.put("cancelQuantity", cancelQuantity);
					itemIssueCtx.put("itemIssuanceId", itemIssuanceId);
					itemIssueCtx.put("userLogin", userLogin);
					itemIssueCtx.put("modifiedByUserLoginId", userLogin.getString("userLoginId"));
					itemIssueCtx.put("modifiedDateTime", UtilDateTime.nowTimestamp());
					Map resultCtx = dispatcher.runSync("updateItemIssuance", itemIssueCtx);
					if (ServiceUtil.isError(resultCtx)) {
						Debug.logError("Problem updateItemIssuance item issuance for requested item", module);
						return resultCtx;
					}
	            	
	            	String inventoryItemId=itemShipIssuence.getString("inventoryItemId");
	            	//update inventery details.
	            	Map createInvDetail = FastMap.newInstance();
					createInvDetail.put("userLogin", userLogin);
					createInvDetail.put("inventoryItemId", inventoryItemId);
					createInvDetail.put("itemIssuanceId", itemIssuanceId);
					createInvDetail.put("quantityOnHandDiff", actQuantity);
					createInvDetail.put("availableToPromiseDiff", actQuantity);
					Map invResultCtx = dispatcher.runSync("createInventoryItemDetail", createInvDetail);
					if (ServiceUtil.isError(invResultCtx)) {
						Debug.logError("Problem Incrementing inventory for requested item ", module);
						return invResultCtx;
					}
					
	            }
				//set previous status
					Map itemStatusCtx = FastMap.newInstance();
					itemStatusCtx.put("custRequestId", custRequestId);
					itemStatusCtx.put("custRequestItemSeqId", custRequestItemSeqId);
					itemStatusCtx.put("userLogin", userLogin);
					itemStatusCtx.put("description", "");
					itemStatusCtx.put("statusId", "CRQ_SUBMITTED");
					Map crqResultCtx = dispatcher.runSync("setCustRequestItemStatus", itemStatusCtx);
					if (ServiceUtil.isError(crqResultCtx)) {
						Debug.logError("Problem changing status for requested item ", module);
						return crqResultCtx;
					}
					//updating shipment
		            try{
		            	GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
		            	shipment.set("statusId","SHIPMENT_CANCELLED");
		            	shipment.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
		            	shipment.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		                delegator.store(shipment);
		            } catch (GenericEntityException e) {
		                Debug.logError(e, module);
		                return ServiceUtil.returnError("Failed to update shipment " + e);            
		            }
		            
	            result = ServiceUtil.returnSuccess("Successfully Canceled item :"+productId+" for Indent Number:"+custRequestId);
	        } catch (GenericEntityException e) {
	            Debug.logError("Problem in retriving data from database", module);
	        } catch (GenericServiceException e) {
	            Debug.logError("Problem in calling service issueInventoryItemToCustRequest", module);
	            return ServiceUtil.returnError("Problem in calling service issueInventoryItemToCustRequest");
	        }
	        return result;
	    }
	 
}
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
	public static int purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
	public static int purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
	
	public static int purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
	

	public static String processCustRequestItems(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    String requestDateStr = (String) request.getParameter("requestDate");
	    String responseDateStr = (String) request.getParameter("responseDate");
	    String requestName = (String) request.getParameter("custRequestName");
	    String custRequestId="";
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
	         custRequestId = (String)resultMap.get("custRequestId");
	        
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
		request.setAttribute("_EVENT_MESSAGE_", "Successfully made request entries ...!IndentNo:"+custRequestId );
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
	
	 public static String makeMassApproval(HttpServletRequest request, HttpServletResponse response) {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			DispatchContext dctx =  dispatcher.getDispatchContext();
			Locale locale = UtilHttp.getLocale(request);
		  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		  	 Map<String, Object> result = ServiceUtil.returnSuccess();
		  	  HttpSession session = request.getSession();
		  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		  	  if (rowCount < 1) {
		  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
				  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
		  		  return "error";
		  	  }
		  	  String statusId  = "";
		  	  String custRequestId = "";
		  	  String custRequestItemSeqId = "";
		  	  String qty="";
		  	BigDecimal quantity=BigDecimal.ZERO;
		  	  String description = "";
		  	Map invoiceAmountMap = FastMap.newInstance();
		  	List invoicesList = FastList.newInstance();
			
			  	for (int i = 0; i < rowCount; i++){
		  		  
			  	  Map paymentMap = FastMap.newInstance();
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  if (paramMap.containsKey("custRequestId" + thisSuffix)) {
		  			custRequestId = (String) paramMap.get("custRequestId"+thisSuffix);
		  		  }
		  		 if (paramMap.containsKey("statusId" + thisSuffix)) {
		  			statusId = (String) paramMap.get("statusId"+thisSuffix);
			  		  }
		  		 if (paramMap.containsKey("custRequestItemSeqId" + thisSuffix)) {
		  			custRequestItemSeqId = (String) paramMap.get("custRequestItemSeqId"+thisSuffix);
				  		  }
		  		 if (paramMap.containsKey("description" + thisSuffix)) {
		  			description = (String) paramMap.get("description"+thisSuffix);
			  		  }
		  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
		  			qty = (String) paramMap.get("quantity"+thisSuffix);
		  		  }
		  		  if(UtilValidate.isNotEmpty(qty)){
					  try {
						  quantity = new BigDecimal(qty);
			  		  } catch (Exception e) {
			  			  Debug.logError(e, "Problems parsing quantity string: " + qty, module);
			  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + qty);
			  			  return "error";
			  		  }
		  		  }
				if(UtilValidate.isEmpty(quantity) || (UtilValidate.isNotEmpty(quantity) && quantity.compareTo(BigDecimal.ZERO)<=0)){
					request.setAttribute("_ERROR_MESSAGE_", "Cannot Accept Quantity ZERO for"+custRequestId+"--!");	  		  
			  		  return "error";
				}
				try{
					GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
					
					custRequestItem.set("quantity", quantity);
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
						return "error";
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
							return "error";
						}
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					Debug.logError(e, module);
					 request.setAttribute("_ERROR_MESSAGE_", "RequestItem set status failed for Request: --- ");
		  			  return "error";
				}
		 }
		  		 result = ServiceUtil.returnSuccess("indent Approval successfully done");
		         request.setAttribute("_EVENT_MESSAGE_", "Indent's Successfully Accepted!!");
			return "success";
	 }
	
	 
	 public static String makeMassReject(HttpServletRequest request, HttpServletResponse response) {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			DispatchContext dctx =  dispatcher.getDispatchContext();
			Locale locale = UtilHttp.getLocale(request);
		  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		  	 Map<String, Object> result = ServiceUtil.returnSuccess();
		  	  HttpSession session = request.getSession();
		  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin"); 
		  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		  	  if (rowCount < 1) {
		  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
				  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
		  		  return "error";
		  	  }
		  	  String statusId  = "";
		  	  String custRequestId = "";
		  	  String custRequestItemSeqId = "";
		  	  String qty="";
		  	BigDecimal quantity=BigDecimal.ZERO;
		  	  String description = "";
		  	for (int i = 0; i < rowCount; i++){
			  	  Map paymentMap = FastMap.newInstance();
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  if (paramMap.containsKey("custRequestId" + thisSuffix)) {
		  			custRequestId = (String) paramMap.get("custRequestId"+thisSuffix);
		  		  }
		  		 if (paramMap.containsKey("statusId" + thisSuffix)) {
		  			statusId = (String) paramMap.get("statusId"+thisSuffix);
			  		  }
		  		 if (paramMap.containsKey("custRequestItemSeqId" + thisSuffix)) {
		  			custRequestItemSeqId = (String) paramMap.get("custRequestItemSeqId"+thisSuffix);
				  		  }
		  		 if (paramMap.containsKey("description" + thisSuffix)) {
			  			description = (String) paramMap.get("description"+thisSuffix);
				  		  }
		  		try{
					GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId),  false);
					
					if(UtilValidate.isEmpty(custRequestItem)){
						Debug.logError("No CustRequestItem found with Id " + custRequestId+":"+custRequestItemSeqId, module);
						return "error";
					}
					String oldStatusId = custRequestItem.getString("statusId");
					if(!oldStatusId.equals(statusId)){
						GenericValue statusValidChange = delegator.findOne("StatusValidChange", UtilMisc.toMap("statusId", oldStatusId, "statusIdTo", statusId), false);
						if(UtilValidate.isEmpty(statusValidChange)){
							Debug.logError("Not a Valid status change for the request", module);
							request.setAttribute("_ERROR_MESSAGE_", "Not a Valid status change for the request"+custRequestId+"--!");	  		  
							 return "error";
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
					if((custRequestItems.size()>0) && (statusId.equals("CRQ_SUBMITTED"))){
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
					Map<String, Object> resultMap = createCustRequestStatus(dctx, inputCtx);
					if(ServiceUtil.isError(resultMap)){
						Debug.logError("Error in updating CustRequestItemStatus entity", module);
						 request.setAttribute("_ERROR_MESSAGE_", "Error in updating CustRequestItemStatus entity");
						 return "error";
					}

				} catch (Exception e) {
					// TODO: handle exception
					Debug.logError(e, module);
		  			  return "error";
				}
				result.put("custRequestId", custRequestId);
				result.put("custRequestItemSeqId", custRequestItemSeqId);
				if((statusId).equals("CRQ_REJECTED")){
					result = ServiceUtil.returnSuccess("Indent Successfully Rejected!!");
			         request.setAttribute("_EVENT_MESSAGE_", "Indent's Successfully Rejected!!");

				}
		         request.setAttribute("_EVENT_MESSAGE_", "Indent's Successfully Rejected!!");
		  	}
		  	return "success";
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
			
			List<GenericValue> custRequests = FastList.newInstance();
			condList.clear();
			condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_INPROCESS"));
			EntityCondition condition = EntityCondition.makeCondition(condList,EntityOperator.AND);
			custRequests = delegator.findList("CustRequest", condition,null,null,null,false);
			
			if((custRequestItems.size()==0) && (custRequests.size()==0)){
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
				condList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("CRQ_SUBMITTED","CRQ_DRAFT","CRQ_INPROCESS")));
			}
			if(statusId.equals("CRQ_REJECTED")){
				condList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("CRQ_SUBMITTED","CRQ_DRAFT","CRQ_ISSUED","CRQ_COMPLETED","CRQ_INPROCESS")));
			}
			if(statusId.equals("CRQ_COMPLETED")){
				condList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("CRQ_SUBMITTED","CRQ_DRAFT","CRQ_ISSUED","CRQ_INPROCESS")));
			}
			if(statusId.equals("CRQ_INPROCESS")){
				condList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("CRQ_SUBMITTED","CRQ_DRAFT")));
			}
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			custRequestItems = delegator.findList("CustRequestItem", cond,null,null,null,false);
			if(custRequestItems.size()==0){
				GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId),  false);
				custRequest.set("statusId", statusId);
				custRequest.store();
			}
			if((custRequestItems.size()>0) && (((statusId.equals("CRQ_ISSUED")) || (statusId.equals("CRQ_REJECTED")) || (statusId.equals("CRQ_SUBMITTED"))))){
				List<GenericValue> custRequestItemInProcess = FastList.newInstance();
				List<GenericValue> custRequestItemInDraft = FastList.newInstance();
				condList.clear();
				condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_INPROCESS"));
				EntityCondition condition = EntityCondition.makeCondition(condList,EntityOperator.AND);
				custRequestItemInProcess = delegator.findList("CustRequestItem", condition,null,null,null,false);
				
				condList.clear();
				condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_DRAFT"));
				EntityCondition draftCondition = EntityCondition.makeCondition(condList,EntityOperator.AND);
				custRequestItemInDraft = delegator.findList("CustRequestItem", draftCondition,null,null,null,false);
				if(custRequestItemInDraft.size()==0){
					if(custRequestItemInProcess.size()>0){
						GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId),  false);
						custRequest.set("statusId", "CRQ_INPROCESS");
						custRequest.store();
					}else{
						GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId),  false);
						custRequest.set("statusId", "CRQ_SUBMITTED");
						custRequest.store();
					}
				}
			}
			/*if((custRequestItems.size()>0) && (statusId.equals("CRQ_REJECTED"))){
				GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId),  false);
				custRequest.set("statusId", "CRQ_SUBMITTED");
				custRequest.store();
			}
			if((custRequestItems.size()>0) && (statusId.equals("CRQ_SUBMITTED"))){
				GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId),  false);
				custRequest.set("statusId", "CRQ_SUBMITTED");
				custRequest.store();
			}*/
			
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
        String shipmentTypeId = (String) context.get("shipmentTypeId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String createNewShipment = (String)context.get("createNewShipment");
        String shipmentId = "";	
        try {
        	 if(UtilValidate.isEmpty(toBeIssuedQty) || (UtilValidate.isNotEmpty(toBeIssuedQty) && toBeIssuedQty.compareTo(BigDecimal.ZERO)==0)){
     			return ServiceUtil.returnError("Cannot Accept Quantity ZERO or Empty");
     		 }
             if(UtilValidate.isNotEmpty(toBeIssuedQty) && toBeIssuedQty.compareTo(BigDecimal.ZERO)==-1){
     			return ServiceUtil.returnError("Negative Value Not Allowed");
     		 }
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
            
            /* Here we are adding this condition to pickup existing shipmentId , when one product is drawn from
             * multiple facilities.  
             */
            if((UtilValidate.isNotEmpty(createNewShipment) && createNewShipment.equalsIgnoreCase("N")) ){
            	List<GenericValue> shipmentList = FastList.newInstance();
            	List shipmentCondList = FastList.newInstance();
            	shipmentCondList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
            	shipmentCondList.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, custRequestItemSeqId));
            	shipmentCondList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            	
            	Timestamp beginDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
            	Timestamp endDate   = UtilDateTime.nowTimestamp();
            	EntityCondition shipmentCondition = EntityCondition.makeCondition(shipmentCondList, EntityOperator.AND);
            	try{
            		shipmentList = delegator.findList("ItemIssuance", shipmentCondition, null, UtilMisc.toList("-issuedDateTime"), null, false);
            	}catch(Exception e){
            		Debug.logError("Error while getting request Shipment ::"+e,module);
            		result = ServiceUtil.returnError("Error while getting request Shipment ::"+e.getMessage());
            		return result;
            	}
            	if(UtilValidate.isNotEmpty(shipmentList)){
            		GenericValue shipmentDet = EntityUtil.getFirst(shipmentList);
            		if(UtilValidate.isNotEmpty(shipmentDet)){
            			shipmentId = (String) shipmentDet.get("shipmentId");
            		}
            		
            	}
            }
           if(UtilValidate.isEmpty(shipmentId)){
	            try{
	    			GenericValue newEntity = delegator.makeValue("Shipment");
	    	        newEntity.set("shipmentTypeId", shipmentTypeId);	
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
           }
            // We need to get facilityId from ProductFacility 
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
            if (toBeIssuedQty.compareTo(qoh) > 0) {
            	Debug.logError("Available Inventory level for productId : "+productId + " is "+qoh, module);
                return ServiceUtil.returnError("Available Inventory level for productId : "+productId + " is "+qoh);
            }
            
            List<GenericValue> inventoryItems = FastList.newInstance();
            //receiptItems for items 
            List conditionList = FastList.newInstance();
            conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            /*conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_ACCEPTED"));*/
            conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
            if(UtilValidate.isNotEmpty(facilityId)){
            	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
            }
            EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            List<GenericValue> receiptInventoryItems = delegator.findList("ShipmentReceiptAndItem", condition, null, UtilMisc.toList("datetimeReceived"), null, false);
            
            List<GenericValue> qcAcceptedReceipts = EntityUtil.filterByCondition(receiptInventoryItems,EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_ACCEPTED"));
            List<String> inventoryItemIdsExl = EntityUtil.getFieldListFromEntityList(receiptInventoryItems, "inventoryId", true);
            // OB inventory Items
            conditionList.clear();
            conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
            if(UtilValidate.isNotEmpty(inventoryItemIdsExl)){
            	conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.NOT_IN, inventoryItemIdsExl));
            }
            EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            List<GenericValue> inventoryOBItems = delegator.findList("InventoryItem", condExpr, null, UtilMisc.toList("datetimeReceived"), null, false);
            if(UtilValidate.isNotEmpty(inventoryOBItems)){
            	inventoryItems.addAll(inventoryOBItems);
            }
            
            if(UtilValidate.isNotEmpty(qcAcceptedReceipts)){
            	inventoryItems.addAll(qcAcceptedReceipts);
            }
            
            if (UtilValidate.isEmpty(inventoryItems)) {
            	Debug.logError("Unable to process issue this item : "+productId + " still in QC ", module);
                return ServiceUtil.returnError("Unable to process issue this item : "+productId + " still in QC ");
            }
            Debug.log("inventoryItems ##################"+inventoryItems);
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
               // Debug.log("===itemIssuanceId=="+resultCtx.get("itemIssuanceId")+"==Shipment="+resultCtx.get("shipmentId"));
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
        result.put("shipmentId", shipmentId);
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
			//we have to ignore this condition bcz issuance can be more than one shipment
			/*if(UtilValidate.isNotEmpty(shipmentId)){
				filterIssuenceReq.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			}*/
			EntityCondition filterIssuenceCond = EntityCondition.makeCondition(filterIssuenceReq, EntityOperator.AND);
			List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", filterIssuenceCond, null, UtilMisc.toList("-issuedDateTime"), null, false);
			Iterator<GenericValue> itrIssList = itemIssuanceList.iterator();
            while (itrIssList.hasNext()) {
                GenericValue itrIssItem = itrIssList.next();
                issuedQty =issuedQty.add(itrIssItem.getBigDecimal("quantity"));
                //subtract cancelQuantity if any for the same issuance
                if(UtilValidate.isNotEmpty(itrIssItem.getBigDecimal("cancelQuantity"))){
                	issuedQty=issuedQty.subtract(itrIssItem.getBigDecimal("cancelQuantity"));
                }
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
			//send out parameters
			result.put("itemIssuanceId", itemIssuanceId);
			result.put("shipmentId", shipmentId);
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
		       String fromPartyId = "";
		       String productId = "";
		       String quantityStr="";
		       String fromFacilityId = "";
		   BigDecimal quantity = BigDecimal.ZERO;

		       Map statusCtx = FastMap.newInstance();
		for (int i = 0; i < rowCount; i++) {
			 
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			if (paramMap.containsKey("custRequestId" + thisSuffix)) {
			custRequestId = (String) paramMap.get("custRequestId" + thisSuffix);
			}else {
			request.setAttribute("_ERROR_MESSAGE_", "Missing custRequeset Id");
			}
			if (paramMap.containsKey("custRequestItemSeqId" + thisSuffix)) {
			custRequestItemSeqId = (String) paramMap.get("custRequestItemSeqId" + thisSuffix);
			}else {
			request.setAttribute("_ERROR_MESSAGE_", "Missing custRequestItemSeqId");
			}
			if (paramMap.containsKey("fromPartyId" + thisSuffix)) {
			fromPartyId = (String) paramMap.get("fromPartyId" + thisSuffix);
			}else {
			request.setAttribute("_ERROR_MESSAGE_", "Missing fromPartyId");
			}
			if (paramMap.containsKey("productId" + thisSuffix)) {
			productId = (String) paramMap.get("productId" + thisSuffix);
			}else {
			request.setAttribute("_ERROR_MESSAGE_", "Missing productId");
			}
			if (paramMap.containsKey("quantity" + thisSuffix)) {
			quantityStr = (String) paramMap.get("quantity" + thisSuffix);
			}else {
			request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
			return "error";	 
			}	 
			if (paramMap.containsKey("facilityId" + thisSuffix)) {
				fromFacilityId = (String) paramMap.get("facilityId" + thisSuffix);
				}
			if(UtilValidate.isNotEmpty(quantityStr)){
			quantity = new BigDecimal(quantityStr);
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
			List<GenericValue> tenantConfigCheck = FastList.newInstance();
			List condList=FastList.newInstance();
			condList.add(EntityCondition.makeCondition("propertyName", EntityOperator.EQUALS, "enableDeptInvTrack"));
			condList.add(EntityCondition.makeCondition("propertyTypeEnumId", EntityOperator.EQUALS, "DEPT_INV_TRACK"));
			condList.add(EntityCondition.makeCondition("propertyValue", EntityOperator.EQUALS, "Y"));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			tenantConfigCheck = delegator.findList("TenantConfiguration",cond, null , null, null, false);
			if(UtilValidate.isNotEmpty(tenantConfigCheck)){
				condList.clear();
				EntityCondition condition=null;
				if(UtilValidate.isNotEmpty(fromFacilityId)){
					condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, fromFacilityId));
				}else{
					condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, fromPartyId));
					condList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT"));
				}
				condList.add(EntityCondition.makeCondition("enableDeptInvCheck", EntityOperator.EQUALS, "Y"));
				condition = EntityCondition.makeCondition(condList,EntityOperator.AND);
				        GenericValue facilityCheckParty= null;
				List<GenericValue> facilityCheckParties = delegator.findList("Facility",condition, null , null, null, false);
				String facilityId="";
				if(UtilValidate.isNotEmpty(facilityCheckParties)){
				   facilityCheckParty = EntityUtil.getFirst(facilityCheckParties);
				   facilityId=facilityCheckParty.getString("facilityId");
				   try {
			               Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("productId", productId,
			                       "inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
			               serviceContext.put("facilityId", facilityId);
			               serviceContext.put("datetimeReceived", UtilDateTime.nowTimestamp());
			               serviceContext.put("userLogin", userLogin);
			               Map<String, Object> resultService = dispatcher.runSync("createInventoryItem", serviceContext);
			               String inventoryItemId = (String)resultService.get("inventoryItemId");
			               serviceContext.clear();
			               serviceContext.put("inventoryItemId", inventoryItemId);
			               serviceContext.put("custRequestId", custRequestId);
			               serviceContext.put("custRequestItemSeqId", custRequestItemSeqId);
			               serviceContext.put("availableToPromiseDiff", quantity);
			               serviceContext.put("quantityOnHandDiff", quantity);
			               serviceContext.put("userLogin", userLogin);
			               resultService = dispatcher.runSync("createInventoryItemDetail", serviceContext);
			           }catch (Exception e) {
			        	   Debug.logError(e, "Canot add inventory to dept: " + e.toString(), module);
			           } 
				}
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
	        /**
	         * Here we are trying to cancel the MilkTransfer if it exists.
	         */
	         if(ServiceUtil.isSuccess(result)){
	        	 try{
	        		 List<GenericValue> milkTransfersList = delegator.findList("MilkTransfer",EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,shipmentId),null,null,null,false);
	        		 if(UtilValidate.isNotEmpty(milkTransfersList)){
	        			 GenericValue milkTransfer = EntityUtil.getFirst(milkTransfersList);
	        			 milkTransfer.set("statusId","MXF_CANCELLED");
	        			 try{
	        				 delegator.store(milkTransfer);
	        			 }catch(Exception e){
	        				 Debug.logError("Error while restoring Transfer status ::"+e,module);
	        				 result = ServiceUtil.returnError("Error while restoring Transfer status ::"+e.getMessage());
	        			 }
	        		 }
	        		 
	        	 }catch(Exception e){
	        		 Debug.logError("Error while cancelling related MilkTransfers ::"+e, module);
	        		 result = ServiceUtil.returnError("Error while cancelling related MilkTransfers ::"+e.getMessage()); 
	        	 }
	         }
	        return result;
	   }
	 public static Map<String, Object> cancelEnquiry(DispatchContext ctx,Map<String, ? extends Object> context) {
			Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			String statusId = (String) context.get("statusId");
			String custRequestId = (String) context.get("custRequestId");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map result = ServiceUtil.returnSuccess("Enquiry Successfully Rejected");
			try{
				GenericValue requirement= null;
				String requirementId="";
				List<GenericValue> requirementCustRequest = delegator.findList("RequirementCustRequest", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId), null, null, null, false);
				if(UtilValidate.isNotEmpty(requirementCustRequest)){
					for(GenericValue reqCustRequest : requirementCustRequest){
						requirementId = reqCustRequest.getString("requirementId");
						requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
						requirement.set("statusId", "REQ_APPROVED");
						requirement.store();
					}
				}
				
				Map statusCtx = FastMap.newInstance();
				statusCtx.put("statusId", statusId);
				statusCtx.put("custRequestId", custRequestId);
				statusCtx.put("userLogin", userLogin);
				Map resultCtx = dispatcher.runSync("setRequestStatus", statusCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("Request set status failed for RequestId: " + custRequestId, module);
					return resultCtx;
				}
			} catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
			return result;
		}
	 public static Map<String, Object> enquiryStatusValidation(DispatchContext ctx,Map<String, ? extends Object> context) {
			Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			String custRequestId = (String) context.get("custRequestId");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			boolean isOrdered = true;
			Map<String, Object> result = FastMap.newInstance();
			List<GenericValue> quoteAndItemAndCustRequest = FastList.newInstance();
			List condList=FastList.newInstance();
			try{
				condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				condList.add(EntityCondition.makeCondition("qiStatusId", EntityOperator.EQUALS, "QTITM_ORDERED"));
				EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
				quoteAndItemAndCustRequest = delegator.findList("QuoteAndItemAndCustRequest",cond, null , null, null, false);
				List<GenericValue> custRequestItems = delegator.findList("CustRequestItem",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId), null , null, null, false);
				String custRequestItemSeqId="";
				EntityCondition condtion=null;
				if (UtilValidate.isNotEmpty(custRequestItems)){
					for(GenericValue custRequestItem:custRequestItems){
						custRequestItemSeqId=custRequestItem.getString("custRequestItemSeqId");
						condList.clear();
						condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
						condList.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, custRequestItemSeqId));
						condtion = EntityCondition.makeCondition(condList,EntityOperator.AND);
						List<GenericValue> quotes = EntityUtil.filterByCondition(quoteAndItemAndCustRequest, condtion);
						if (UtilValidate.isEmpty(quotes)){
							isOrdered = false;
							break;
						}
					}
				}
				result.put("isOrdered", isOrdered);
		    }catch (Exception e) {
					// TODO: handle exception
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
			}
		    return result;
	 }
    public static String issueSelectedRequests(HttpServletRequest request, HttpServletResponse response) {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
		  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		  	  Locale locale = UtilHttp.getLocale(request);
		  	  Map<String, Object> resultCtx = ServiceUtil.returnSuccess();
		  	  HttpSession session = request.getSession();
		  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		  	  if (rowCount < 1) {
		  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
				  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
		  		  return "error";
		  	  }
		  	  String custRequestId = "";
		  	  String custRequestItemSeqId = "";
		  	  BigDecimal toBeIssuedQty = BigDecimal.ZERO;
		  	  String qty="";
		  	  String facilityId = "";
		  	  String shipmentTypeId = "";
		  	
				  	for (int i = 0; i < rowCount; i++){
			  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			  		  
			  		  if(paramMap.containsKey("shipmentTypeId" + thisSuffix)){
			  			shipmentTypeId = (String) paramMap.get("shipmentTypeId" + thisSuffix);
			  		  }
			  		if(paramMap.containsKey("facilityId" + thisSuffix)){
			  			facilityId = (String) paramMap.get("facilityId" + thisSuffix);
			  		  }
			  		  
			  		  if (paramMap.containsKey("custRequestId" + thisSuffix)) {
			  			custRequestId = (String) paramMap.get("custRequestId"+thisSuffix);
			  		  }
			  		  if (paramMap.containsKey("custRequestItemSeqId" + thisSuffix)) {
			  			custRequestItemSeqId = (String) paramMap.get("custRequestItemSeqId"+thisSuffix);
			  		  }
			  		   if (paramMap.containsKey("toBeIssuedQty" + thisSuffix)) {
			  			 qty = (String) paramMap.get("toBeIssuedQty"+thisSuffix);
			  		  }
			  		   if(qty.contains(","))
			  		   {
			  			 qty = qty.replace(",", "");
			  		   }
			  		 if(UtilValidate.isNotEmpty(qty)){
						  try {
							  toBeIssuedQty = new BigDecimal(qty);
				  		  } catch (Exception e) {
				  			  Debug.logError(e, "Problems parsing quantity string: " + qty, module);
				  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + qty);
				  			  return "error";
				  		  }
			  		  }
					if(UtilValidate.isEmpty(toBeIssuedQty) || (UtilValidate.isNotEmpty(toBeIssuedQty) && toBeIssuedQty.compareTo(BigDecimal.ZERO)<=0)){
						request.setAttribute("_ERROR_MESSAGE_", "Cannot Accept Quantity ZERO for"+custRequestId+"--!");	  		  
				  		  return "error";
					}
			  		try{
						Map issuanceMapCtx = FastMap.newInstance();
						issuanceMapCtx.put("custRequestId", custRequestId);
						issuanceMapCtx.put("custRequestItemSeqId", custRequestItemSeqId);
						issuanceMapCtx.put("toBeIssuedQty", toBeIssuedQty);
						issuanceMapCtx.put("facilityId", facilityId);
						issuanceMapCtx.put("shipmentTypeId", shipmentTypeId);
						issuanceMapCtx.put("userLogin", userLogin);
						issuanceMapCtx.put("locale", locale);
						resultCtx = dispatcher.runSync("issueProductForRequest", issuanceMapCtx);
						if (ServiceUtil.isError(resultCtx)) {
							Debug.logError("Issuance Failed in Service for Indent : " + custRequestId+":"+custRequestItemSeqId, module);
							return "error";
							}
						} catch (Exception e) {
							// TODO: handle exception
							Debug.logError(e, module);
							request.setAttribute("_ERROR_MESSAGE_", " Issuance Request Failed ");
				  			return "error";
						}
				  	}
			         request.setAttribute("_EVENT_MESSAGE_", "successfully Issued "+rowCount+" Selected Materials");
				return "success";
			}
    public static Map<String, Object> sendRequirementsForGroup(DispatchContext ctx,Map<String, ? extends Object> context) {
			Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			List requirementList = (List) context.get("requirementIds");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map result = ServiceUtil.returnSuccess();
			String groupTypeId = "ENQ_REQ_GROUP";
			String statusId = "REQ_GRP_CREATED";
			Timestamp createdDate = UtilDateTime.nowTimestamp();
			String requirementGroupId="";
			if(UtilValidate.isEmpty(requirementList)){
				Debug.logError("No Requirements Selected to Process.", module);
				return ServiceUtil.returnError("No Requirements Selected to Process.");
			}
			try{
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("requirementId", EntityOperator.IN, requirementList));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> requirements = delegator.findList("Requirement", condition, UtilMisc.toSet("requirementId"), null, null, false);
				if(UtilValidate.isEmpty(requirements)){
					Debug.logError("Requirements not found.", module);
					return ServiceUtil.returnError("Requirements not found.");
				}	
				
				GenericValue requirementGroup = delegator.makeValue("RequirementGroup");
				requirementGroup.set("groupTypeId", groupTypeId);
				requirementGroup.set("statusId", statusId);
				requirementGroup.set("createdDate", createdDate);
				requirementGroup.set("createdByUserLogin", userLogin.getString("userLoginId"));
				requirementGroup.set("lastModifiedDate", UtilDateTime.nowTimestamp());
				requirementGroup.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				delegator.createSetNextSeqId(requirementGroup);
				if(UtilValidate.isNotEmpty(requirementGroup)){
					requirementGroupId = requirementGroup.getString("requirementGroupId");
				}
				
				for(GenericValue requirement: requirements){
					String requirementId = requirement.getString("requirementId");
					GenericValue requirementGroupMember = delegator.makeValue("RequirementGroupMember");
					requirementGroupMember.set("requirementGroupId",requirementGroupId);
					requirementGroupMember.set("requirementId",requirementId);
					requirementGroupMember.create();
				}
				
				} catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}
			result=ServiceUtil.returnSuccess("Successfully Requirement Group Created.! Group Id:"+requirementGroupId);
			return result;
    }
    public static Map<String, Object> approveRequirementGroup(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String requirementGroupId = (String) context.get("requirementGroupId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		Timestamp approvedDate = UtilDateTime.nowTimestamp();
		if(UtilValidate.isEmpty(requirementGroupId)){
			Debug.logError("Requirements Group Id not found.", module);
			return ServiceUtil.returnError("Requirements Group Id not found.");
		}
		try{
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("requirementGroupId", EntityOperator.EQUALS, requirementGroupId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> requirements = delegator.findList("RequirementGroupMember", condition, null, null, null, false);
			if(UtilValidate.isEmpty(requirements)){
				Debug.logError("RequirementGroup not found.", module);
				return ServiceUtil.returnError("RequirementGroup not found.");
			}	
			
			for(GenericValue requirement: requirements){
				String requirementId = requirement.getString("requirementId");
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("requirementGroupId", EntityOperator.NOT_EQUAL, requirementGroupId));
				conditionList.add(EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId));
				EntityCondition rGMCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				List<GenericValue> requirementGroupMember = delegator.findList("RequirementGroupMember",rGMCondition, UtilMisc.toSet("requirementGroupId"), null, null, false);
				if(UtilValidate.isNotEmpty(requirementGroupMember)){
					for(GenericValue reqmntGroup:requirementGroupMember){
						String requGroupId = reqmntGroup.getString("requirementGroupId");
						GenericValue requirementGroup = delegator.findOne("RequirementGroup",UtilMisc.toMap("requirementGroupId",requGroupId),false);
						String statusId = requirementGroup.getString("statusId");
						if(statusId.equals("REQ_GRP_APPROVED")){
							Debug.logError(requirementId+" already approved in the Group."+requGroupId, module);
							return ServiceUtil.returnError(requirementId+" already approved in the Group."+requGroupId);
						}
					}
				}
			}
			GenericValue requirementGroup = delegator.findOne("RequirementGroup",UtilMisc.toMap("requirementGroupId",requirementGroupId),false);
			requirementGroup.set("approverPartyId", userLogin.getString("partyId"));
			requirementGroup.set("statusId", "REQ_GRP_APPROVED");
			requirementGroup.set("approvedDate",approvedDate);
			requirementGroup.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			requirementGroup.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
			requirementGroup.store();
			} catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		result=ServiceUtil.returnSuccess("Successfully Approved the Requirement Group :"+requirementGroupId);
		return result;
    }
    
    public static Map<String, Object> rejectRequirementGroup(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String requirementGroupId = (String) context.get("requirementGroupId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		Timestamp approvedDate = UtilDateTime.nowTimestamp();
		if(UtilValidate.isEmpty(requirementGroupId)){
			Debug.logError("Requirements Group Id not found.", module);
			return ServiceUtil.returnError("Requirements Group Id not found.");
		}
		try{
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("requirementGroupId", EntityOperator.EQUALS, requirementGroupId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> requirements = delegator.findList("RequirementGroupMember", condition, null, null, null, false);
			if(UtilValidate.isEmpty(requirements)){
				Debug.logError("RequirementGroup not found.", module);
				return ServiceUtil.returnError("RequirementGroup not found.");
			}	
			
			GenericValue requirementGroup = delegator.findOne("RequirementGroup",UtilMisc.toMap("requirementGroupId",requirementGroupId),false);
			requirementGroup.set("statusId", "REQ_GRP_CANCELLED");
			requirementGroup.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			requirementGroup.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
			requirementGroup.store();
			} catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		result=ServiceUtil.returnSuccess("Successfully Rejected the Requirement Group :"+requirementGroupId);
		return result;
    }
}
package in.vasista.vbiz.tankersales;

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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.ofbiz.base.util.GeneralException;

import net.sf.json.JSONObject;

import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.order.order.OrderServices;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.party.party.PartyHelper;
import java.util.Calendar;
//import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;









import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.order.shoppingcart.ShoppingCart;
//import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
//import org.ofbiz.product.product.ProductWorker;
//import org.ofbiz.accounting.util.UtilAccounting;
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
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.base.conversion.JSONConverters.JSONToList;
import org.ofbiz.entity.util.EntityFindOptions;

import in.vasista.vbiz.procurement.ProcurementNetworkServices;

public class TankerSalesServices {

	public static final String module = TankerSalesServices.class.getName();
	
	public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
	public static final BigDecimal ONE_BASE = BigDecimal.ONE;
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
	public static int purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
	public static int purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
	
	public static int purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
	
	
	public static String CreateTankerSalesSOEvent(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	   
		String orderId = (String) request.getParameter("orderId");
		String billToPartyId = (String) request.getParameter("billToPartyId");
		String ShipToPartyId = (String) request.getParameter("ShipToPartyId");
		
		String issueToDeptId = (String) request.getParameter("issueToDeptId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String SONumber=(String) request.getParameter("SONumber");
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
		
		String partyIdFrom = "";
		Map processOrderContext = FastMap.newInstance();
		String salesChannel = (String)request.getParameter("salesChannel");
		Debug.log("salesChannel =================="+salesChannel);
		salesChannel = "TANKER_SALES_CHANNEL";
		Debug.log("salesChannel =================="+salesChannel);
		
		
		String billFromPartyId="Company";
		
		String productId = null;
		String quantityStr = null;
		String unitPriceStr=null;
		
		Timestamp effectiveDate=null;
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal unitPrice = BigDecimal.ZERO;
		Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();
		Timestamp orderDate = UtilDateTime.nowTimestamp();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy"); 
		
		try {
			if(UtilValidate.isNotEmpty(SONumber)){
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", SONumber), false);
				if (UtilValidate.isNotEmpty(orderHeader)) { 
				String statusId = orderHeader.getString("statusId");
				if(statusId.equals("ORDER_CANCELLED")){
					Debug.logError("Cannot create SalesOrder for cancelled orderId : "+SONumber, module);
					request.setAttribute("_ERROR_MESSAGE_", "Cannot create SalesOrder for cancelled orderId : "+SONumber);	
			  		return "error";
				}}
			  }
		}catch(GenericEntityException e){
			Debug.logError("Cannot create SalesOrder for cancelled orderId : "+SONumber, module);
			request.setAttribute("_ERROR_MESSAGE_", "Cannot create SalesOrder for cancelled orderId : "+SONumber);	
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
		
		if (billToPartyId == "") {
			request.setAttribute("_ERROR_MESSAGE_","Party Id is empty");
			return "error";
		}
		try{
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", billToPartyId), false);
			if(UtilValidate.isEmpty(party)){
				request.setAttribute("_ERROR_MESSAGE_","Not a valid Party");
				return "error";
			}
		}catch(GenericEntityException e){
			Debug.logError(e, "Error fetching partyId " + billToPartyId, module);
			request.setAttribute("_ERROR_MESSAGE_","Invalid party Id");
			return "error";
		}
		if(UtilValidate.isNotEmpty(request.getAttribute("estimatedDeliveryDate"))) {
			effectiveDate = (Timestamp) request.getAttribute("estimatedDeliveryDate");
		}

		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		
		List termsList = FastList.newInstance();
		String eventResult = getMaterialPOValue(request, response);
		if(eventResult.equals("error")){
			Debug.logError("Problems getting so data from grid", module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems getting so data from grid");
			return "error";
		}
		BigDecimal grandTotal = (BigDecimal)request.getAttribute("grandTotal");
		List<Map> otherTermDetail = (List)request.getAttribute("termsDetail");
		List<Map> itemDetail = (List)request.getAttribute("itemDetail");
		List<Map> adjustmentDetail = (List)request.getAttribute("adjustmentDetail");
		if( UtilValidate.isEmpty(itemDetail)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		
		for (int i = 0; i < rowCount; i++) {
			String termTypeId =null;
			String  termDaysStr = null;
			String termValueStr = null;
			String termUom = null;
			Long termDays = Long.valueOf(0);
			String termDescription = null;
			BigDecimal termValue = BigDecimal.ZERO;
			Map<String, Object> termTypeMap = FastMap.newInstance();
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			if (paramMap.containsKey("paymentTermTypeId" + thisSuffix)) {
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
			
		}
		
		for (int i = 0; i < rowCount; i++) {
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
		
		//getting productStoreId 
		String productStoreId = (String) (TankerSalesServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("productQtyList", itemDetail);
		processOrderContext.put("orderTypeId", orderTypeId);
		processOrderContext.put("orderId", orderId);
		processOrderContext.put("termsList", termsList);
		processOrderContext.put("partyId", billToPartyId);
		processOrderContext.put("grandTotal", grandTotal);
		processOrderContext.put("otherTerms", otherTermDetail);
		processOrderContext.put("adjustmentDetail", adjustmentDetail);
		processOrderContext.put("billFromPartyId", billFromPartyId);
		processOrderContext.put("billToPartyId", billToPartyId);
		processOrderContext.put("ShipToPartyId", ShipToPartyId);
		
		processOrderContext.put("issueToDeptId", issueToDeptId);
		processOrderContext.put("supplyDate", effectiveDate);
		processOrderContext.put("salesChannel", salesChannel);
		processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
		processOrderContext.put("productStoreId", productStoreId);
		processOrderContext.put("SONumber", SONumber);
		processOrderContext.put("orderName", orderName);
		processOrderContext.put("fileNo", fileNo);
		processOrderContext.put("refNo", refNo);
		processOrderContext.put("orderDate", orderDate);
		processOrderContext.put("fromDate", fromDate);
		processOrderContext.put("thruDate", thruDate);
		processOrderContext.put("estimatedDeliveryDate", estimatedDeliveryDate);
		processOrderContext.put("incTax", incTax);
		if(UtilValidate.isNotEmpty(orderId)){
			result = updateTankerSaleSO(dctx, processOrderContext);
			if(ServiceUtil.isError(result)){
				Debug.logError("Unable to update order: " + ServiceUtil.getErrorMessage(result), module);
				request.setAttribute("_ERROR_MESSAGE_", "Unable to update order  :" + orderId+"....! "+ServiceUtil.getErrorMessage(result));
				return "error";
			}
		}
		else{
			result = CreateTankerSaleSO(dctx, processOrderContext);
			if(ServiceUtil.isError(result)){
				Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
				request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + billToPartyId+"....! "+ServiceUtil.getErrorMessage(result));
				return "error";
			}
		}
		
		/*if(UtilValidate.isNotEmpty(SONumber)){
		Map<String, Object> orderAssocMap = FastMap.newInstance();
		orderAssocMap.put("orderId", result.get("orderId"));
		orderAssocMap.put("toOrderId", SONumber);
		orderAssocMap.put("userLogin", userLogin);
		result = createOrderAssoc(dctx,orderAssocMap);
			if(ServiceUtil.isError(result)){
				Debug.logError("Unable do Order Assoc: " + ServiceUtil.getErrorMessage(result), module);
				request.setAttribute("_ERROR_MESSAGE_", "Unable do Order Assoc...! "+ServiceUtil.getErrorMessage(result));
				return "error";
			}
		}*/
		request.setAttribute("_EVENT_MESSAGE_", "Entry successful for party: "+billToPartyId+" and  SO :"+result.get("orderId"));	  	 
		request.setAttribute("orderId", result.get("orderId")); 
		return "success";
	}
		
   public static Map<String, Object> CreateTankerSaleSO(DispatchContext ctx,Map<String, ? extends Object> context) {
		
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
	  	Debug.log("salesChannel ========333333 ======"+salesChannel);
	  	Map taxTermsMap = (Map) context.get("taxTermsMap");
	  	String partyId = (String) context.get("partyId");
	  	String billToPartyId = (String) context.get("billToPartyId");
	  	String ShipToPartyId = (String) context.get("ShipToPartyId");
	  	
		String issueToDeptId = (String) context.get("issueToDeptId");
	  	List<Map> termsList = (List)context.get("termsList");
	  	List<Map> otherChargesAdjustment = (List)context.get("adjustmentDetail");
	  	List<Map> otherTermDetail = (List)context.get("otherTerms");
	  	String incTax = (String)context.get("incTax");
	  	boolean beganTransaction = false;
	  	String SONumber=(String) context.get("SONumber");
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
		String billFromPartyId="Company";
				
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
			cart.setExternalId(SONumber);
	        cart.setProductStoreId(productStoreId);
			cart.setChannelType(salesChannel);
			cart.setBillToCustomerPartyId(billToPartyId);
			cart.setPlacingCustomerPartyId(billToPartyId);
			
			//changed here
			
			cart.setShipToCustomerPartyId(ShipToPartyId);
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
			if(UtilValidate.isNotEmpty(SONumber))
				cart.setOrderAttribute("SO_NUMBER",SONumber);
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
			
		} catch (Exception e) {
			
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}
		
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			String productId = "";
			
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
				
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
					productId = (String)prodQtyMap.get("productId");
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
			
			/*//before save OrderRole save partyRole
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
			*/
			//update PurposeType
			/*try{
			GenericValue orderHeaderPurpose = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			orderHeaderPurpose.set("purposeTypeId", salesChannel);
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
		result.put("orderId", orderId);
		return result;

	}
   	
   	
   	public static Map<String, Object> updateTankerSaleSO(DispatchContext ctx,Map<String, ? extends Object> context) {
		
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
	  	String SONumber=(String) context.get("SONumber");
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
			orderHeader.set("externalId", SONumber);
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
				
				
				if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
					productId = (String)prodQtyMap.get("productId");
				}
				
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
   	public static Map<String, Object> getPurchaseFactoryStore(Delegator delegator){
	
        
    	Map<String, Object> result = FastMap.newInstance(); 
    	String productStoreGroupId = "PURCHASE_PRODUCTS";
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
		        String quantityStr = "";
				String unitPriceStr = "";
				String vatPercentStr = "";
				String bedPercentStr = "";
				String cstPercentStr = "";
				BigDecimal unitPrice = BigDecimal.ZERO;
		        BigDecimal quantity = BigDecimal.ZERO;
		        BigDecimal bedPercent = BigDecimal.ZERO;
		        BigDecimal cstPercent = BigDecimal.ZERO;
		        BigDecimal vatPercent = BigDecimal.ZERO;
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
				}
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
				}
				if (paramMap.containsKey("unitPrice" + thisSuffix)) {
					unitPriceStr = (String) paramMap.get("unitPrice" + thisSuffix);
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
					productDetail.put("quantity", quantity);
					productDetail.put("unitPrice", unitPrice);
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
   	public static String getMaterialSOValue(HttpServletRequest request, HttpServletResponse response) {
		
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
		        String quantityStr = "";
				String unitPriceStr = "";
				String vatPercentStr = "";
				String bedPercentStr = "";
				String cstPercentStr = "";
				BigDecimal unitPrice = BigDecimal.ZERO;
		        BigDecimal quantity = BigDecimal.ZERO;
		        BigDecimal bedPercent = BigDecimal.ZERO;
		        BigDecimal cstPercent = BigDecimal.ZERO;
		        BigDecimal vatPercent = BigDecimal.ZERO;
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
				}
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
				}
				if (paramMap.containsKey("unitPrice" + thisSuffix)) {
					unitPriceStr = (String) paramMap.get("unitPrice" + thisSuffix);
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
					productDetail.put("quantity", quantity);
					productDetail.put("unitPrice", unitPrice);
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
   	public static Map<String, Object> cancelSOStatus(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
        
		String statusId = (String) context.get("statusId");
		String orderId = (String) context.get("orderId");
		String changeReason = (String) context.get("changeReason");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		try{
			
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			
			if(UtilValidate.isEmpty(orderHeader)){
				Debug.logError("Order doesn't exists with Id : "+orderId , module);
  	 			return ServiceUtil.returnError("Order doesn't exists with Id : "+orderId);
			}
			
			Map statusCtx = FastMap.newInstance();
			statusCtx.put("statusId", statusId);
			statusCtx.put("orderId", orderId);
			statusCtx.put("userLogin", userLogin);
			Map resultCtx = OrderServices.setOrderStatus(ctx, statusCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Order set status failed for orderId: " + orderId, module);
				return resultCtx;
			}
			String oldStatusId = (String)resultCtx.get("oldStatusId");
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
			
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
   	
   	public static Map<String, Object> getOrderDetails(DispatchContext dctx, Map<String, ? extends Object> context) {
       	
   		Delegator delegator = dctx.getDelegator();
       
   		LocalDispatcher dispatcher = dctx.getDispatcher();   
   		Map<String, Object> result = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
   		
        List conditionList = FastList.newInstance();
        String orderId = (String) context.get("orderId");
        GenericValue orderHeader = null;
   		try {
   			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
   		} catch (GenericEntityException e) {
   			Debug.logError("Unable to Fetch availableBatchs"+e, module);
       		return ServiceUtil.returnError("Unable to Fetch availableBatchs"); 
   		}
   		
   		List<GenericValue> orderItemsList = null;
 		try {
 			orderItemsList = delegator.findList("OrderItem", EntityCondition.makeCondition(EntityOperator.AND, "orderId", orderId), null, null, null, false);
 		} catch (GenericEntityException e) {
 			Debug.logError("Unable to Fetch OrderItem"+e, module);
     		return ServiceUtil.returnError("Unable to Fetch OrderItem"); 
 		}
   		
 		List<GenericValue> orderRoleList = null;
 		try {
 			orderRoleList = delegator.findList("OrderRole", EntityCondition.makeCondition(EntityOperator.AND, "orderId", orderId), null, null, null, false);
 		} catch (GenericEntityException e) {
 			Debug.logError("Unable to Fetch OrderRole"+e, module);
     		return ServiceUtil.returnError("Unable to Fetch OrderRole"); 
 		}
   		
 		List<GenericValue> shipToParty = EntityUtil.filterByCondition(orderRoleList, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
 		List<GenericValue> billToParty = EntityUtil.filterByCondition(orderRoleList, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
 		
 		//String shipToPartyId = (EntityUtil.getFirst(shipToParty)).getString("partyId");
 		
 		//vendorName = PartyHelper.getPartyName(delegator,boothFacility.getString("ownerPartyId"), false);
		
 		String shipToPartyName= PartyHelper.getPartyName(delegator, (EntityUtil.getFirst(billToParty)).getString("partyId"), false);
 		String billToPartyName= PartyHelper.getPartyName(delegator, (EntityUtil.getFirst(billToParty)).getString("partyId"), false);
 		
 		Map shipmentDetailsMap = FastMap.newInstance();
		shipmentDetailsMap.put("shipToParty", (EntityUtil.getFirst(shipToParty)).get("partyId"));
		shipmentDetailsMap.put("billToParty", (EntityUtil.getFirst(billToParty)).get("partyId"));
		shipmentDetailsMap.put("shipToPartyName", shipToPartyName);
		shipmentDetailsMap.put("billToPartyName", billToPartyName);
		shipmentDetailsMap.put("productId", (EntityUtil.getFirst(orderItemsList)).get("productId"));
		//shipmentDetailsMap.put("productName", product.get("description"));
 		
   		/*List availableBatchList = FastList.newInstance();
   		for(int i=0; i<availableBatchs.size(); i++){
   			GenericValue eachBatch = availableBatchs.get(i);
   			Map batchDetailsMap = FastMap.newInstance();
   			batchDetailsMap.put("batchId", eachBatch.get("batchId"));
   			batchDetailsMap.put("batchNo", eachBatch.get("batchNo"));
   			
   			Timestamp startDate = (Timestamp) eachBatch.get("startDate");
   			String startDateStr = new SimpleDateFormat("dd/MMMMM/yyyy").format(startDate);
   			batchDetailsMap.put("startDate", startDateStr);
   			
   			Timestamp endDate = (Timestamp) eachBatch.get("endDate");
   			String endDateStr = new SimpleDateFormat("dd/MMMMM/yyyy").format(endDate);
   			batchDetailsMap.put("endDate", endDateStr);
   			
   			Map tempBatchMap = FastMap.newInstance();
   			tempBatchMap.putAll(batchDetailsMap);
   			
   			availableBatchList.add(tempBatchMap);

   		}*/
		
		List productsList = FastList.newInstance();
		for(int i=0; i<orderItemsList.size(); i++){
   			GenericValue orderItem = orderItemsList.get(i);
   			Map orderItemMap = FastMap.newInstance();
   			orderItemMap.put("orderId", orderItem.get("orderId"));
   			orderItemMap.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
   			orderItemMap.put("productId", orderItem.get("productId"));
   			orderItemMap.put("quantity", orderItem.get("quantity"));
   			orderItemMap.put("unitPrice", orderItem.get("unitPrice"));
   			orderItemMap.put("unitListPrice", orderItem.get("unitListPrice"));
   			
   			GenericValue product = null;
   	   		try {
   	   			product = delegator.findOne("Product", UtilMisc.toMap("productId", orderItemMap.get("productId")), false);
   	   		} catch (GenericEntityException e) {
   	   			Debug.logError("Unable to Fetch availableBatchs"+e, module);
   	       		return ServiceUtil.returnError("Unable to Fetch availableBatchs"); 
   	   		}
   	   		
   	   		orderItemMap.put("productName", product.get("description"));
			
   			
   			Map tempBatchMap = FastMap.newInstance();
   			tempBatchMap.putAll(orderItemMap);
   			
   			productsList.add(tempBatchMap);

   		}
		
		result.put("productsList", productsList);
 		result.put("orderHeader", orderHeader);
   		result.put("orderItemsList", orderItemsList);
   		result.put("orderRoleList", orderRoleList);
   		result.put("shipmentDetailsMap", shipmentDetailsMap);
   		return result;
       
   	}
   	
   	public static Map<String, Object> createMilkTankerReceiptEntryTS(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Tanker Recipt Created Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String receivedProductId = (String) context.get("receivedProductId");
   	 	String tankerNo = (String) context.get("tankerNo");
   	 	String sendDateStr = (String) context.get("sendDate");
   	 	String entryDateStr = (String) context.get("entryDate");
   	 	String sendTime = (String) context.get("sendTime");
   	 	String entryTime = (String) context.get("entryTime");
   	 	String dcNo=(String)context.get("dcNo");
   	 	String productId = (String) context.get("productId");
   	 	String isCipChecked = (String)context.get("isCipChecked");
   	 	Debug.log("isCipChecked =========="+isCipChecked);
   	 	String partyId = (String) context.get("partyId");
	 	String partyIdTo = (String) context.get("partyIdTo");
	 	String sealCheck = (String) context.get("sealCheck");
	 	String vehicleStatusId = (String) context.get("vehicleStatusId");
	 	String milkTransferId = (String)context.get("milkTransferId");
	 	
	 	String orderId = (String) context.get("orderId");
	 	String billToPartyId = (String) context.get("billToPartyId");
	 	
	 	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yyyyHHmm");
	 	try{
	 		//we need to check the transfer status of the give tanker
	 		Map tankerInMap = FastMap.newInstance();
	 		tankerInMap.put("tankerNo", tankerNo);
	 		tankerInMap.put("userLogin", userLogin);
	 		tankerInMap.put("reqStatusId","MXF_INIT");
	 		
	 		Map getTankerDetailsMap = getTankerRecordNumber(dctx,tankerInMap);
	 		if(ServiceUtil.isSuccess(getTankerDetailsMap) && UtilValidate.isEmpty(milkTransferId)){
	 			Debug.logError("Exisiting receipt is not completed of the tanker :"+tankerNo,module);
	 			resultMap = ServiceUtil.returnError("Exisiting receipt is not completed of the tanker :"+tankerNo);
	 			return resultMap;
	 		}
	 		// we need to create vehicle trip and vehicle trip status
	 		
	 		//creating vehicle trip
	 		Map vehicleTripMap = FastMap.newInstance();
	 		vehicleTripMap.put("vehicleId", tankerNo);
	 		vehicleTripMap.put("partyId", partyId);
	 		vehicleTripMap.put("userLogin",userLogin);
	 		Map vehicleTripResultMap = dispatcher.runSync("createVehicleTrip", vehicleTripMap);
	 		
	 		if(ServiceUtil.isError(vehicleTripResultMap)){
	 			Debug.logError("Error While Creating vehicleTrip :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
	 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip ");
	 			return resultMap;
	 		}
	 		String sequenceNum = (String)vehicleTripResultMap.get("sequenceNum");
	 		
	 		Map vehicleTripStatusMap = FastMap.newInstance();
	 		vehicleTripStatusMap.putAll(vehicleTripResultMap);
	 		vehicleTripStatusMap.put("statusId","MR_VEHICLE_IN");
	 		if(UtilValidate.isNotEmpty(vehicleStatusId)){
	 			vehicleTripStatusMap.put("statusId",vehicleStatusId);
	 		}
	 		vehicleTripStatusMap.put("userLogin",userLogin);
	 		Timestamp entryDate = UtilDateTime.nowTimestamp();
	 		if(UtilValidate.isNotEmpty(sendDateStr)){
	 			if(UtilValidate.isNotEmpty(sendTime)){
	 				entryDateStr = entryDateStr.concat(entryTime);
	 			}
	 			entryDate = new java.sql.Timestamp(sdf.parse(entryDateStr).getTime());
	 		}
	 		Timestamp sendDate = UtilDateTime.nowTimestamp();
	 		if(UtilValidate.isNotEmpty(sendDateStr)){
	 			if(UtilValidate.isNotEmpty(sendTime)){
	 				sendDateStr = sendDateStr.concat(sendTime);
	 			}
	 			sendDate = new java.sql.Timestamp(sdf.parse(sendDateStr).getTime());
	 		}
	 		if(entryDate.compareTo(sendDate)<0){
	 			Debug.logError("Dispatch date time should not  greater than or Equal to entryDate and time ", module);
	 			resultMap = ServiceUtil.returnError("Dispatch date time should not  greater than or Equal to entryDate and time ");
	 			return resultMap;
	 		}
	 		vehicleTripStatusMap.put("estimatedStartDate",entryDate);
	 		vehicleTripStatusMap.remove("responseMessage");
	 		Map vehicleStatusResultMap = dispatcher.runSync("createVehicleTripStatus", vehicleTripStatusMap);
	 		if(ServiceUtil.isError(vehicleTripResultMap)){
	 			Debug.logError("Error While Creating vehicleTripStatus :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
	 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip Status");
	 			return resultMap;
	 		}
	 		GenericValue newTransfer = null; 
	 		if(UtilValidate.isNotEmpty(milkTransferId)){
		 		try{
		 			newTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId",milkTransferId),false);
		 			
		 		}catch(GenericEntityException e){
		 			Debug.logError("Error while getting Transfer Details for :: "+milkTransferId,module);
		 			return ServiceUtil.returnError("Error while getting Transfer Details for :: "+milkTransferId);
		 		}
		 	}
	 		if(UtilValidate.isEmpty(newTransfer)){
	 			newTransfer = delegator.makeValue("MilkTransfer");
	 		}
	 		newTransfer.set("containerId", tankerNo);
	 		newTransfer.set("sequenceNum", sequenceNum);
	 		newTransfer.set("sendDate", sendDate);
	 		newTransfer.set("dcNo", dcNo);
	 		if(UtilValidate.isNotEmpty(isCipChecked)){
	 			newTransfer.set("isCipChecked", isCipChecked);
	 		}
	 		newTransfer.set("productId",productId);
	 		newTransfer.set("partyId", partyId);
	 		newTransfer.set("isSealChecked", sealCheck);
 			newTransfer.set("statusId", "MXF_INPROCESS");
	 		newTransfer.set("partyIdTo", partyIdTo);
	 		newTransfer.set("createdByUserLogin", (String)userLogin.get("userLoginId"));
	 		newTransfer.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));
	 		if(UtilValidate.isEmpty(milkTransferId)){
	 			delegator.createSetNextSeqId(newTransfer);
	 		}else{
	 			delegator.createOrStore(newTransfer);
	 		}
	 		resultMap = ServiceUtil.returnSuccess("Successfully Created Tanker Receipt with Record Number :"+newTransfer.get("milkTransferId"));
	 	}catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error while creating record==========="+e,module);
	 		resultMap = ServiceUtil.returnError("Error while creating record==========="+e.getMessage());
		}
    	return resultMap;
   
   	}
		
   	public static Map<String, Object> getTankerRecordNumber(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map resultMap = FastMap.newInstance();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String tankerNo = (String) context.get("tankerNo");
   	 	String milkTransferId ="";
   	 	String reqStatusId = (String)context.get("reqStatusId");
		try{
	   	 	List transfersCondList = FastList.newInstance();
			transfersCondList.add(EntityCondition.makeCondition("containerId",EntityOperator.EQUALS,tankerNo));
			List statusList = UtilMisc.toList("MXF_INPROCESS");
			statusList.add("MXF_REJECTED");
			if(UtilValidate.isNotEmpty(reqStatusId)){
				statusList.add(reqStatusId);
			}
			transfersCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.IN,statusList));
			EntityCondition transferCondtion = EntityCondition.makeCondition(transfersCondList,EntityJoinOperator.AND) ;
	        List orderBy = UtilMisc.toList("-milkTransferId");
			List transfersList = delegator.findList("MilkTransfer", transferCondtion,null, orderBy, null, false);
			if(UtilValidate.isNotEmpty(transfersList)){
				GenericValue transferRecord = EntityUtil.getFirst(transfersList);
			 	String mtStatusId = (String) transferRecord.get("statusId");
				milkTransferId =(String)transferRecord.get("milkTransferId"); 
				resultMap = ServiceUtil.returnSuccess();
				resultMap.put("dcNo", transferRecord.get("dcNo"));
				resultMap.put("productId", transferRecord.get("productId"));
				resultMap.put("milkTransferId", transferRecord.get("milkTransferId"));
				resultMap.put("vehicleId", transferRecord.get("containerId"));
				
				BigDecimal grossWeight = new BigDecimal(0);
				if(UtilValidate.isNotEmpty(transferRecord.get("grossWeight"))){
					grossWeight = (BigDecimal)transferRecord.get("grossWeight");
				}
				BigDecimal tareWeight = new BigDecimal(0);
				if(UtilValidate.isNotEmpty(transferRecord.get("tareWeight"))){
					tareWeight = (BigDecimal)transferRecord.get("tareWeight");
				}
				resultMap.put("grossWeight", grossWeight);
				resultMap.put("tareWeight", tareWeight);
				resultMap.put("sequenceNum", transferRecord.get("sequenceNum"));
				resultMap.put("partyId", transferRecord.get("partyId"));
				resultMap.put("partyIdTo", transferRecord.get("partyIdTo"));
				resultMap.put("isSealChecked",transferRecord.get("isSealChecked"));
				resultMap.put("isCipChecked",transferRecord.get("isCipChecked"));
				
				resultMap.put("milkTransfer",transferRecord);
				List<GenericValue> milkTransferItemsList = FastList.newInstance();
				try{
					milkTransferItemsList = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId), null, null, null, false);
				}catch(Exception e){
					Debug.logError("Error while getting transferItems List ::"+e,module);
					return ServiceUtil.returnError("Error while getting transferItems List ::"+e.getMessage());
				}
				
				if(UtilValidate.isNotEmpty(milkTransferItemsList)){
					GenericValue milkTransferItem = EntityUtil.getFirst(milkTransferItemsList);
					resultMap.put("milkTransferItem",milkTransferItem);
				}
				java.sql.Date sendDateFormat = new java.sql.Date(((Timestamp)transferRecord.get("sendDate")).getTime());
				String sendDateStr = UtilDateTime.toDateString(sendDateFormat,"dd-MM-yyyy");
				String sendTimeStr = UtilDateTime.toDateString(sendDateFormat,"HHmm");
				resultMap.put("sendDateStr", sendDateStr);
				resultMap.put("sendTimeStr", sendTimeStr);
				if(mtStatusId.equalsIgnoreCase("MXF_REJECTED")){
	 	        	List vehicleTripCond = FastList.newInstance();
	 	        	vehicleTripCond.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,transferRecord.get("containerId")));
	 	        	vehicleTripCond.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,transferRecord.get("sequenceNum")));
	 	        	vehicleTripCond.add(EntityCondition.makeCondition("estimatedEndDate",EntityOperator.EQUALS,null));
	 	        	EntityCondition vehicleTripCondion = EntityCondition.makeCondition(vehicleTripCond,EntityJoinOperator.AND) ;
	 	   			List vehicleTripStatusList = delegator.findList("VehicleTripStatus", vehicleTripCondion,null, null, null, false);
	 		 		if(UtilValidate.isEmpty(vehicleTripStatusList)){
	 					resultMap = ServiceUtil.returnError("No record Found");
	
	 		 		  }
 	        	}

				return resultMap;
			}else{
				resultMap = ServiceUtil.returnError("No record Found");
				return resultMap;
			}
		}catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError("Error while getting tankerDetails "+e, module);
			resultMap = ServiceUtil.returnError("Error while getting tankerDetails");
			return resultMap;
		}
   	 	
   	 	
	}
   	public static Map<String,Object> updateInternalMilkTransfer(DispatchContext dctx,Map<String,? extends Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Tanker Recipt Updated Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String tankerNo = (String) context.get("tankerNo");
   	 	Debug.log("tankerNo ======"+tankerNo);
	 	String statusId = (String) context.get("vehicleStatusId");
	 	Debug.log("statusId ======"+statusId);
	 	String milkTransferId = (String) context.get("milkTransferId");
	 	Debug.log("milkTransferId ======"+milkTransferId);
	 	String isCipChecked = (String)context.get("isCipChecked");
	 	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yyyyHHmm");
	 	
		String replaceVehicleStatusString = "MR_ISSUE_";
		String replaceWithStatusString = "";
		String vehicleStatusId = (String) context.get("vehicleStatusId");
		Debug.log("vehicleStatusId ======"+vehicleStatusId);
		GenericValue milkTransfer = null;
    	try{
    		milkTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId", milkTransferId),false);
    	}catch(Exception e){
    		Debug.logError("Error while getting Transfer Details"+e, module);
    		return ServiceUtil.returnError("Error while getting Transfer Details "+e.getMessage());
    	}
    	if(UtilValidate.isEmpty(milkTransfer)){
    		Debug.logError("Milk Transfer not Found with Id :"+milkTransferId, module);
    		return ServiceUtil.returnError("Milk Transfer not Found with Id :"+milkTransferId);
    	}
    	String sequenceNum = (String) milkTransfer.get("sequenceNum");
    	
    	Map updateVehStatusInMap = FastMap.newInstance();
    	updateVehStatusInMap.put("userLogin", userLogin);
    	updateVehStatusInMap.put("statusId", vehicleStatusId);
    	updateVehStatusInMap.put("vehicleId", tankerNo);
    	updateVehStatusInMap.put("sequenceNum", sequenceNum);
    	updateVehStatusInMap.put("replaceVehicleStatusString", replaceVehicleStatusString);
    	Map updateVehStatResultMap = FastMap.newInstance();
		if(vehicleStatusId.equalsIgnoreCase("TS_CIP")){
			
			Timestamp now = UtilDateTime.nowTimestamp();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(now.getTime());
			cal.set(Calendar.SECOND, 0);  
			cal.set(Calendar.MILLISECOND, 0);
			now = new Timestamp(cal.getTime().getTime());
			
			updateVehStatusInMap.put("estimatedStartDate", now);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	Debug.log("updateVehStatResultMap ======"+updateVehStatResultMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        	resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
			}
 	       
 	        if(UtilValidate.isNotEmpty(isCipChecked)){
 	        	milkTransfer.set("isCipChecked", isCipChecked);
 	        }
 	        
 	        try{
 	        	delegator.store(milkTransfer);
 	        }catch(Exception e){
 	        	Debug.logError("Error While "+e,module);
	        	resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
 	        }
		}else if(vehicleStatusId.equalsIgnoreCase("TS_TARE_WEIGHT")){
			BigDecimal tareWeight = (BigDecimal)context.get("tareWeight");
 			String tareDateStr = (String) context.get("tareDate"); 
 	        String tareTime = (String) context.get("tareTime");
 	        Timestamp tareDate = UtilDateTime.nowTimestamp();
 	        try{
 		 		if(UtilValidate.isNotEmpty(tareDateStr)){
 		 			if(UtilValidate.isNotEmpty(tareTime)){
 		 				tareDateStr = tareDateStr.concat(tareTime);
 		 			}
 		 			tareDate = new java.sql.Timestamp(sdf.parse(tareDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + tareDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",tareDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	Debug.log("updateVehStatResultMap ======"+updateVehStatResultMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        	resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
			}
 	        milkTransfer.set("tareWeight", tareWeight);
 	        try{
 	        	delegator.store(milkTransfer);
 	        }catch(Exception e){
 	        	Debug.logError("Error While "+e,module);
	        	resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
 	        }
		}else if(vehicleStatusId.equalsIgnoreCase("TS_QC")){
			String testDateStr = (String) context.get("testDate"); 
 	        String testTime = (String) context.get("testTime");
 	        String productId = (String) context.get("productId");
 	        Timestamp testDate = UtilDateTime.nowTimestamp();
 	        try{
 		 		if(UtilValidate.isNotEmpty(testDateStr)){
 		 			if(UtilValidate.isNotEmpty(testTime)){
 		 				testDateStr = testDateStr.concat(testTime);
 		 			}
 		 			testDate = new java.sql.Timestamp(sdf.parse(testDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + testDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",testDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
			}
			String qcComments = (String) context.get("qcComments");
 	        milkTransfer.set("productId",productId);
 	        milkTransfer.set("fat",(BigDecimal)context.get("sendFat"));
			milkTransfer.set("snf",(BigDecimal)context.get("sendSnf"));
			milkTransfer.set("sendLR",(BigDecimal)context.get("sendCLR"));
			//milkTransfer.set("receivedFat",(BigDecimal)context.get("recdFat"));
			//milkTransfer.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
			//milkTransfer.set("comments", qcComments);
			milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			
			//here we need to create transferItem
			
			try{
				GenericValue MilkTransferItem = delegator.makeValue("MilkTransferItem");
				MilkTransferItem.set("milkTransferId",milkTransferId);
				MilkTransferItem.set("fat",(BigDecimal)context.get("sendFat"));
				MilkTransferItem.set("snf",(BigDecimal)context.get("sendSnf"));
				MilkTransferItem.set("sendTemparature",(BigDecimal)context.get("sendTemp"));
				MilkTransferItem.set("sendAcidity",(BigDecimal)context.get("sendAcid"));
				
				MilkTransferItem.set("sendLR",(BigDecimal)context.get("sendCLR"));
				MilkTransferItem.set("sendCob",(String)context.get("sendCob"));
				MilkTransferItem.set("sendOrganoLepticTest",(String)context.get("sendOrganoLepticTest"));
				MilkTransferItem.set("sendSedimentTest",(String)context.get("sendSedimentTest"));
				
				MilkTransferItem.set("createdByUserLogin", userLogin.get("userLoginId"));
				MilkTransferItem.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				
				delegator.setNextSubSeqId(MilkTransferItem,"sequenceNum",5,1);
				delegator.create(MilkTransferItem);
			}catch(Exception e){
				Debug.logError("Error while storing qc details "+e,module);
				return ServiceUtil.returnError("Error while storing qc details "+e.getMessage());
			}
			try{
				delegator.store(milkTransfer);
			}catch(Exception e){
				Debug.logError("Error occured ::"+e,module);
				return ServiceUtil.returnError("Error occured ::"+e.getMessage());
			}
			
		}else if(vehicleStatusId.equalsIgnoreCase("TS_GROSS_WEIGHT")){
			
			Long numberOfCells = (Long)context.get("numberOfCells");
 			BigDecimal grossWeight = (BigDecimal)context.get("grossWeight");
 			BigDecimal dispatchWeight = (BigDecimal)context.get("dispatchWeight");
 			String grossDateStr = (String) context.get("grossDate"); 
 	        String grossTime = (String) context.get("grossTime");
 	        Timestamp grossDate = UtilDateTime.nowTimestamp();
 	        String driverName = (String) context.get("driverName");
 	        
 	        try{
 		 		if(UtilValidate.isNotEmpty(grossDateStr)){
 		 			if(UtilValidate.isNotEmpty(grossTime)){
 		 				grossDateStr = grossDateStr.concat(grossTime);
 		 			}
 		 			grossDate = new java.sql.Timestamp(sdf.parse(grossDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + grossDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",grossDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
			}
 	        String sealCheck = (String) context.get("sealCheck");
			milkTransfer.set("dispatchWeight", dispatchWeight);
			milkTransfer.set("grossWeight", grossWeight);
			milkTransfer.set("numberOfCells", numberOfCells);
			if(UtilValidate.isNotEmpty(sealCheck)){
				milkTransfer.set("isSealChecked",sealCheck);
			}
			milkTransfer.set("driverName", driverName);
			milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			try{
				delegator.store(milkTransfer);
			}catch(Exception e){
				Debug.logError ("Error while storing grossweight details ::"+e,module);
				return ServiceUtil.returnError("Error while storing grossweight details ::"+e.getMessage());
			}
			
		}else if(vehicleStatusId.equalsIgnoreCase("TS_ACK_QC")){
			replaceVehicleStatusString = "MR_ISSUE_AQC";
			replaceWithStatusString = "ACK QC";
			
			
			updateVehStatusInMap.put("replaceWithStatusString",replaceWithStatusString);
			updateVehStatusInMap.put("replaceVehicleStatusString",replaceVehicleStatusString);
			
			String testDateStr = (String) context.get("testDate"); 
 	        String testTime = (String) context.get("testTime");
 	        String productId = (String) context.get("productId");
 	        Timestamp testDate = UtilDateTime.nowTimestamp();
 	        try{
 		 		if(UtilValidate.isNotEmpty(testDateStr)){
 		 			if(UtilValidate.isNotEmpty(testTime)){
 		 				testDateStr = testDateStr.concat(testTime);
 		 			}
 		 			testDate = new java.sql.Timestamp(sdf.parse(testDateStr).getTime());
 		 		}
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + testDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",testDate);
 	        updateVehStatusInMap.put("estimatedEndDate",testDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
			}
 	       GenericValue MilkTransferItem = null;
 	      List<GenericValue> milkTransferItemsList = FastList.newInstance();
			try{
				milkTransferItemsList = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId), null, null, null, false);
			}catch(Exception e){
				Debug.logError("Error while getting transferItems List ::"+e,module);
				return ServiceUtil.returnError("Error while getting transferItems List ::"+e.getMessage());
			}
			
			if(UtilValidate.isNotEmpty(milkTransferItemsList)){
				MilkTransferItem = EntityUtil.getFirst(milkTransferItemsList);
			}
			String qcComments = (String) context.get("qcComments");
 	        milkTransfer.set("receiveDate",testDate);
			milkTransfer.set("receivedFat",(BigDecimal)context.get("recdFat"));
			milkTransfer.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
			
			BigDecimal recdFat = (BigDecimal) milkTransfer.get("receivedFat");
			BigDecimal recdSnf = (BigDecimal) milkTransfer.get("receivedSnf");
			
			BigDecimal sendFat = (BigDecimal) milkTransfer.get("fat");
			BigDecimal sendSnf = (BigDecimal) milkTransfer.get("snf");
			
			BigDecimal tareWeight = (BigDecimal) milkTransfer.get("tareWeight");
			BigDecimal grossWeight = (BigDecimal) milkTransfer.get("grossWeight");
			
			BigDecimal sendKgFat = BigDecimal.ZERO;
			BigDecimal sendKgSnf = BigDecimal.ZERO;
			BigDecimal recdKgFat = BigDecimal.ZERO;
			BigDecimal recdKgSnf = BigDecimal.ZERO;
			
			if(UtilValidate.isNotEmpty(tareWeight) && UtilValidate.isNotEmpty(grossWeight)){
				BigDecimal qtyKgs = grossWeight.subtract(tareWeight);
				BigDecimal qtyLtrs = BigDecimal.ZERO;
				sendKgFat =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,sendFat));
				sendKgSnf =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,sendSnf));
				recdKgFat =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,recdFat));
				recdKgSnf =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,recdSnf));
				
				milkTransfer.set("quantity",qtyKgs);
				milkTransfer.set("receivedQuantity",qtyKgs);
				milkTransfer.set("quantityLtrs",qtyLtrs);
				milkTransfer.set("receivedQuantityLtrs",qtyLtrs);
				
				milkTransfer.set("sendKgFat",sendKgFat);
				milkTransfer.set("sendKgSnf",sendKgSnf);
				milkTransfer.set("receivedKgFat",recdKgFat);
				milkTransfer.set("receivedKgSnf",recdKgSnf);
				
				MilkTransferItem.set("quantity",qtyKgs);
				MilkTransferItem.set("receivedQuantity",qtyKgs);
				MilkTransferItem.set("quantityLtrs",qtyLtrs);
				MilkTransferItem.set("receivedQuantityLtrs",qtyLtrs);
				
				MilkTransferItem.set("sendKgFat",sendKgFat);
				MilkTransferItem.set("sendKgSnf",sendKgSnf);
				MilkTransferItem.set("receivedKgFat",recdKgFat);
				MilkTransferItem.set("receivedKgSnf",recdKgSnf);
				
				MilkTransferItem.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				
				
			}
			
			milkTransfer.set("receivedFat",(BigDecimal)context.get("recdFat"));
			milkTransfer.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
			
			milkTransfer.set("comments", qcComments);
			milkTransfer.set("statusId", "MXF_RECD");
			milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			
			//here we need to create transferItem
			
			try{
				
				MilkTransferItem.set("milkTransferId",milkTransferId);
				MilkTransferItem.set("receivedFat",(BigDecimal)context.get("recdFat"));
				MilkTransferItem.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
				MilkTransferItem.set("receivedTemparature",(BigDecimal)context.get("recdTemp"));
				MilkTransferItem.set("receivedAcidity",(BigDecimal)context.get("recdAcid"));
				
				MilkTransferItem.set("receivedLR",(BigDecimal)context.get("recdCLR"));
				MilkTransferItem.set("receivedCob",(String)context.get("recdCob"));
				MilkTransferItem.set("recdOrganoLepticTest",(String)context.get("recdOrganoLepticTest"));
				MilkTransferItem.set("receivedSedimentTest",(String)context.get("recdSedimentTest"));
				
				delegator.store(MilkTransferItem);
			}catch(Exception e){
				Debug.logError("Error while updating qc details "+e,module);
				return ServiceUtil.returnError("Error while updating qc details "+e.getMessage());
			}
			try{
				delegator.store(milkTransfer);
			}catch(Exception e){
				Debug.logError("Error occured ::"+e,module);
				return ServiceUtil.returnError("Error occured ::"+e.getMessage());
			}
			
			// Change Shipment Status to delivered
			
			String shipmentId = (String) milkTransfer.get("shipmentId");
			Debug.log("shipmentId ==============="+shipmentId);
			if(UtilValidate.isNotEmpty(shipmentId)){
		 		try{
		 			GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
		 			Debug.log("shipment ==============="+shipment);
		 			shipment.set("statusId", "SHIPMENT_DELIVERED");
		 			Debug.log("shipment ========222======="+shipment);
		 			try{
						delegator.store(shipment);
						Debug.log("Test test ===============");
					}catch(Exception e){
						Debug.logError("Error occured ::"+e,module);
						return ServiceUtil.returnError("Error occured ::"+e.getMessage());
					}
		 		}catch(GenericEntityException e){
		 			Debug.logError("Error while fetching Shipment :: "+shipmentId,module);
		 			return ServiceUtil.returnError("Error while fetching Shipment :: "+shipmentId);
		 		}
		 	}
			
			
		}
		
		return resultMap ;
	}// End of the service
   	
   	public static Map<String, Object> createShipmentForTankerSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Tanker Recipt Created Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	
   	 	String tankerNo = (String) context.get("tankerNo");
   	 	Debug.log("tankerNo ========="+tankerNo);
   	 	String extTankerName = (String) context.get("extTankerName");
   	 	Debug.log("extTankerName ========="+extTankerName);
   	 	
   	 	String sendDateStr = (String) context.get("sendDate");
   	 	String sendTime = (String) context.get("sendTime");
   	 	/*String entryDateStr = (String) context.get("entryDate");*/
   	 	/*String entryTime = (String) context.get("entryTime");*/
   	 	//String dcNo=(String)context.get("dcNo");
   	 	String productId = (String) context.get("productId");
   	 	String routeId = (String) context.get("routeId");
	 	
   	 	
   	 	String partyId = (String) context.get("partyId");
	 	String partyIdTo = (String) context.get("partyIdTo");
	 	String billToPartyId = (String) context.get("billToPartyId");
	 	
	 	String vehicleStatusId = (String) context.get("vehicleStatusId");
	 	String milkTransferId = (String)context.get("milkTransferId");
	 	
	 	String orderId = (String) context.get("orderId");
	 	String orderItemSeqId = (String) context.get("orderItemSeqId");
	 	
	 	// Check if they choose existing Tanker or given an external Tanker No.
	 	
	 	if(UtilValidate.isEmpty(tankerNo)){
	 		
	 		if(UtilValidate.isEmpty(extTankerName)){
	 			Debug.logError("No Tanker Was Selected",module);
	 			resultMap = ServiceUtil.returnError("No Tanker Was Selected");
	 			return resultMap;
	 		}
	 		
	 		List<GenericValue> existingExtVehicles = FastList.newInstance();
	        try{
	        	existingExtVehicles = delegator.findList("Vehicle", EntityCondition.makeCondition("vehicleNumber", EntityOperator.EQUALS, extTankerName), null, null, null, false);
	        }
	        catch (Exception e) {
		 		Debug.logError(e, "Error creating invoice for shipment !", module);
		 		return ServiceUtil.returnError("Failed to create invoice for the shipment " + e);	  
	        }
	 		
	        if(UtilValidate.isNotEmpty(existingExtVehicles)){
	        	tankerNo = (EntityUtil.getFirst(existingExtVehicles)).getString("vehicleId");
	        }
	        else{
	        	GenericValue vehicle = delegator.makeValue("Vehicle");   
		 		vehicle.set("vehicleName", extTankerName);
		 		vehicle.set("vehicleNumber", extTankerName);
		 		Debug.log("vehicle ========="+vehicle);
		 		try {
		 			delegator.createSetNextSeqId(vehicle);
	            } catch (GenericEntityException e) {
	                ServiceUtil.returnError(e.getMessage());
	            }
		 		
		 		tankerNo = vehicle.getString("vehicleId");
		 		Debug.log("tankerNo ========="+tankerNo);
		 		
		 		GenericValue vehicleRole = delegator.makeValue("VehicleRole");   
		 		vehicleRole.set("vehicleId", tankerNo);
		 		vehicleRole.set("roleTypeId", "EXTERNAL_VEHICLE");
		 		vehicleRole.set("partyId", partyIdTo);
		 		vehicleRole.set("facilityId", "SONAI_DAIRY");
		 		vehicleRole.set("fromDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
		 		Debug.log("vehicleRole ========="+vehicleRole);
		 		try {
		 			vehicleRole.create();
	            } catch (GenericEntityException e) {
	                ServiceUtil.returnError(e.getMessage());
	            }
	        }
	        
	 		
	 		
	 	}
	 	
	 	Debug.log("tankerNo ===2222======"+tankerNo);
	 	//we need to check the transfer status of the give tanker
 		Map tankerInMap = FastMap.newInstance();
 		tankerInMap.put("tankerNo", tankerNo);
 		tankerInMap.put("userLogin", userLogin);
 		tankerInMap.put("reqStatusId","MXF_INIT");
 		
 		Map getTankerDetailsMap = getTankerRecordNumber(dctx,tankerInMap);
 		Debug.log("getTankerDetailsMap ========="+getTankerDetailsMap);
 		if(ServiceUtil.isSuccess(getTankerDetailsMap) && UtilValidate.isEmpty(milkTransferId)){
 			Debug.logError("Exisiting receipt is not completed of the tanker :"+tankerNo,module);
 			resultMap = ServiceUtil.returnError("Exisiting receipt is not completed of the tanker :"+tankerNo);
 			return resultMap;
 		}
	 	
	 	
	 	if(UtilValidate.isEmpty(productId)){
	 		try{
	 			GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
	 			Debug.log("orderItem ========="+orderItem);
		 		productId = orderItem.getString("productId");
	 		}catch(GenericEntityException e){
	 			Debug.logError("Error while fetching OrderItem :: "+orderId,module);
	 			return ServiceUtil.returnError("Error while fetching OrderItem :: "+orderId);
	 		}
	 	}
	 	
	 	BigDecimal shipQty = (BigDecimal) context.get("shipQty");
	 	BigDecimal insuranceQty = (BigDecimal) context.get("insuranceQty");
	 	
	 	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yyyyHHmm");
	 	Timestamp sendDate = UtilDateTime.nowTimestamp();
 		if(UtilValidate.isNotEmpty(sendDateStr)){
 			if(UtilValidate.isNotEmpty(sendTime)){
 				sendDateStr = sendDateStr.concat(sendTime);
 			}
 			try {
 				sendDate = new java.sql.Timestamp(sdf.parse(sendDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + sendDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + sendDateStr, module);
			}
 		}
 		
	 	
 		String shipmentId = null;
 		Map<String, Object> newShipResp = FastMap.newInstance();
	 	Map<String, Object> newShipment = FastMap.newInstance();
        newShipment.put("originFacilityId", "SONAI_DAIRY");
        newShipment.put("primaryOrderId", orderId);
        newShipment.put("shipmentTypeId", "TANKER_SALES");
        newShipment.put("statusId", "SHIPMENT_INPUT");
        newShipment.put("routeId", routeId);
        newShipment.put("userLogin", userLogin);
        newShipment.put("partyIdTo", partyIdTo);
        newShipment.put("partyIdFrom", partyId);
        newShipment.put("estimatedShipDate", sendDate);
        newShipment.put("vehicleId", tankerNo);
        
        try{
        	newShipResp = dispatcher.runSync("createShipment", newShipment);
        	shipmentId = (String)newShipResp.get("shipmentId");
  	 		if (ServiceUtil.isError(newShipResp)) {
  	 			String errMsg =  ServiceUtil.getErrorMessage(newShipResp);
  	 			Debug.logError(errMsg , module);
  	 			return ServiceUtil.returnError(" Error While Creating Shipment !");
  	 		}
  	 	}catch (Exception e) {
  	 		Debug.logError(e, "Error While Creating Shipment !", module);
  	 		return ServiceUtil.returnError("Failed to create a new shipment " + e);	  
	  	}
	 	
        Map<String,Object> itemInMap = FastMap.newInstance();
        itemInMap.put("shipmentId",shipmentId);
        itemInMap.put("userLogin",userLogin);
        itemInMap.put("productId",productId);
        itemInMap.put("quantity",shipQty);
        String shipmentItemSeqId = null;
        try{
        	newShipResp = dispatcher.runSync("createShipmentItem",itemInMap);
        	shipmentItemSeqId = (String)newShipResp.get("shipmentItemSeqId");
  	 		if (ServiceUtil.isError(newShipResp)) {
  	 			String errMsg =  ServiceUtil.getErrorMessage(newShipResp);
  	 			Debug.logError(errMsg , module);
  	 			return ServiceUtil.returnError(" Error While Creating Shipment Item !");
  	 		}
  	 	}catch (Exception e) {
  	 		Debug.logError(e, "Error While Creating Shipment !", module);
  	 		return ServiceUtil.returnError("Failed to create a new shipment item " + e);	  
	  	}
        
        Map<String,Object> orderShipmentMap = FastMap.newInstance();
        orderShipmentMap.put("shipmentId",shipmentId);
        orderShipmentMap.put("shipmentItemSeqId",shipmentItemSeqId);
        orderShipmentMap.put("orderId",orderId);
        orderShipmentMap.put("orderItemSeqId",orderItemSeqId);
        orderShipmentMap.put("shipGroupSeqId",shipmentItemSeqId);
        orderShipmentMap.put("quantity",shipQty);
        orderShipmentMap.put("userLogin",userLogin);
        try{
        	newShipResp = dispatcher.runSync("createOrderShipment",orderShipmentMap);
  	 		if (ServiceUtil.isError(newShipResp)) {
  	 			String errMsg =  ServiceUtil.getErrorMessage(newShipResp);
  	 			Debug.logError(errMsg , module);
  	 			return ServiceUtil.returnError(" Error While Creating Order Shipment !");
  	 		}
  	 	}catch (Exception e) {
  	 		Debug.logError(e, "Error While Creating Order Shipment !", module);
  	 		return ServiceUtil.returnError("Failed to create a new order shipment " + e);	  
	  	}
        
        Map itemIssueCtx = FastMap.newInstance();
        itemIssueCtx.put("userLogin", userLogin);
		itemIssueCtx.put("orderId", orderId);
		itemIssueCtx.put("orderItemSeqId", orderItemSeqId);
		itemIssueCtx.put("shipmentId", shipmentId);
		itemIssueCtx.put("shipmentItemSeqId", shipmentItemSeqId);
		itemIssueCtx.put("productId", productId);
		itemIssueCtx.put("quantity", shipQty);
		itemIssueCtx.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
		itemIssueCtx.put("modifiedByUserLoginId", userLogin.getString("userLoginId"));
		itemIssueCtx.put("modifiedDateTime", UtilDateTime.nowTimestamp());
		Debug.log("itemIssueCtx ================="+itemIssueCtx);
		try{
			Map resultCtx = dispatcher.runSync("createItemIssuance", itemIssueCtx);
			Debug.log("resultCtx ================="+resultCtx);
			if (ServiceUtil.isError(resultCtx)) {
				Debug.logError("Problem creating item issuance for requested item", module);
				return resultCtx;
			}
	   	}catch (Exception e) {
	 		Debug.logError(e, "Error While Creating Item Issuance !", module);
	 		return ServiceUtil.returnError("Failed to create a Item Issuance " + e);	  
	  	}
		
        
        
        
        
        
        
        
	 	try{
	 		
	 		//creating vehicle trip
	 		Map vehicleTripMap = FastMap.newInstance();
	 		vehicleTripMap.put("vehicleId", tankerNo);
	 		vehicleTripMap.put("partyId", partyId);
	 		vehicleTripMap.put("shipmentId", shipmentId);
	 		vehicleTripMap.put("userLogin",userLogin);
	 		vehicleTripMap.put("estimatedStartDate", sendDate);
	 		Map vehicleTripResultMap = dispatcher.runSync("createVehicleTrip", vehicleTripMap);
	 		if(ServiceUtil.isError(vehicleTripResultMap)){
	 			Debug.logError("Error While Creating vehicleTrip :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
	 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip ");
	 			return resultMap;
	 		}
	 		String sequenceNum = (String)vehicleTripResultMap.get("sequenceNum");
	 		
	 		Map vehicleTripStatusMap = FastMap.newInstance();
	 		vehicleTripStatusMap.putAll(vehicleTripResultMap);
	 		vehicleTripStatusMap.put("statusId","TS_SHIPMENT_PLANNED");
	 		if(UtilValidate.isNotEmpty(vehicleStatusId)){
	 			vehicleTripStatusMap.put("statusId",vehicleStatusId);
	 		}
	 		vehicleTripStatusMap.put("userLogin",userLogin);
	 		
	 		vehicleTripStatusMap.put("estimatedStartDate",sendDate);
	 		vehicleTripStatusMap.remove("responseMessage");
	 		Map vehicleStatusResultMap = dispatcher.runSync("createVehicleTripStatus", vehicleTripStatusMap);
	 		if(ServiceUtil.isError(vehicleTripResultMap)){
	 			Debug.logError("Error While Creating vehicleTripStatus :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
	 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip Status");
	 			return resultMap;
	 		}
	 		
	 		GenericValue newTransfer = null; 
	 		if(UtilValidate.isNotEmpty(milkTransferId)){
		 		try{
		 			newTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId",milkTransferId),false);
		 			
		 		}catch(GenericEntityException e){
		 			Debug.logError("Error while getting Transfer Details for :: "+milkTransferId,module);
		 			return ServiceUtil.returnError("Error while getting Transfer Details for :: "+milkTransferId);
		 		}
		 	}
	 		
	 		if(UtilValidate.isEmpty(newTransfer)){
	 			newTransfer = delegator.makeValue("MilkTransfer");
	 		}
	 		newTransfer.set("containerId", tankerNo);
	 		newTransfer.set("sequenceNum", sequenceNum);
	 		newTransfer.set("sendDate", sendDate);
	 		newTransfer.set("dcNo", shipmentId);
	 		newTransfer.set("productId",productId);
	 		newTransfer.set("quantity",shipQty);
	 		newTransfer.set("partyId", partyId);
	 		newTransfer.set("insuranceQty", insuranceQty);
	 		newTransfer.set("shipmentId", shipmentId);
 			newTransfer.set("statusId", "MXF_INPROCESS");
	 		newTransfer.set("partyIdTo", partyIdTo);
	 		newTransfer.set("createdByUserLogin", (String)userLogin.get("userLoginId"));
	 		newTransfer.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));
	 		if(UtilValidate.isEmpty(milkTransferId)){
	 			delegator.createSetNextSeqId(newTransfer);
	 		}else{
	 			delegator.createOrStore(newTransfer);
	 		}
	 		resultMap = ServiceUtil.returnSuccess("Successfully Created Tanker Receipt with Record Number :"+newTransfer.get("milkTransferId"));
	 		
	 	}catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error while creating record==========="+e,module);
	 		resultMap = ServiceUtil.returnError("Error while creating record==========="+e.getMessage());
		}
	 	
    	return resultMap;
   
   	}
   	
   	
   	public static Map<String, Object> CreateTankerSalesEvent(DispatchContext ctx,Map<String, ? extends Object> context) {
		
	    Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Locale locale = (Locale) context.get("locale");
		String orderTypeId = (String)context.get("orderTypeId");
	    Timestamp SoDate = (Timestamp) context.get("SoDate");
	  	String billToPartyId = (String) context.get("billToPartyId");
	  	String ShipToPartyId = (String) context.get("ShipToPartyId");
	  	if(UtilValidate.isEmpty(billToPartyId)){
	  		billToPartyId = ShipToPartyId;
	  	}
	  	
	    Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
	    String productId = (String) context.get("productId");
	    BigDecimal quantity = (BigDecimal) context.get("quantity");
	    BigDecimal unitPrice = (BigDecimal) context.get("unitPrice");
	    
	    BigDecimal fatPercent = (BigDecimal) context.get("fat");
	    BigDecimal snfPercent = (BigDecimal) context.get("snf");
	    
	    if(UtilValidate.isEmpty(fatPercent)){
	    	fatPercent = new BigDecimal(3.5);
	    }
	    if(UtilValidate.isEmpty(snfPercent)){
	    	snfPercent = new BigDecimal(8.5);
	    }
	    Debug.log("fatPercent ====="+fatPercent);
	    Debug.log("snfPercent ====="+snfPercent);
	    
	    String billFromPartyId="Company";
	    String productStoreId = "STORE";
	    
	    // Calculate Order Price Based on set Parameters(KG Fat and KG Snf)
	    
        List componentPriceList = FastList.newInstance();
        BigDecimal fatPremium = BigDecimal.ZERO;
        BigDecimal snfPremium = BigDecimal.ZERO;
        BigDecimal payablePrice = BigDecimal.ZERO;
        BigDecimal fatPrice = BigDecimal.ZERO;
        BigDecimal snfPrice = BigDecimal.ZERO;
        BigDecimal kgFatPrice = BigDecimal.ZERO;
        BigDecimal kgSnfPrice = BigDecimal.ZERO;
        
	    Map priceCtx = FastMap.newInstance();
		priceCtx.put("userLogin",userLogin);
		priceCtx.put("productId",productId);
		priceCtx.put("partyId",billToPartyId);
	    priceCtx.put("priceDate",effectiveDate);
		priceCtx.put("fatPercent", fatPercent);
		priceCtx.put("snfPercent", snfPercent);
		//Map priceResult = PriceServices.getProcurementProductPrice(dctx,priceCtx);
		Debug.log("priceCtx ====="+priceCtx);
		try{
			Map servResult = dispatcher.runSync("calculateProcurementProductPrice", priceCtx);
			Debug.log("create Order Result ====="+servResult);
			if(UtilValidate.isNotEmpty(servResult.get("componentPriceList"))){
				componentPriceList = (List) servResult.get("componentPriceList");
			}
			if(UtilValidate.isNotEmpty(servResult.get("fatPremium"))){
				fatPremium = (BigDecimal) servResult.get("fatPremium");
			}
			if(UtilValidate.isNotEmpty(servResult.get("snfPremium"))){
				snfPremium = (BigDecimal) servResult.get("snfPremium");
			}
			if(UtilValidate.isNotEmpty(servResult.get("payablePrice"))){
				payablePrice = (BigDecimal) servResult.get("payablePrice");
			}
			if(UtilValidate.isNotEmpty(servResult.get("kgSnfPrice"))){
				kgSnfPrice = (BigDecimal) servResult.get("kgSnfPrice");
			}
			if(UtilValidate.isNotEmpty(servResult.get("kgFatPrice"))){
				kgFatPrice = (BigDecimal) servResult.get("kgFatPrice");
			}
			
			if (ServiceUtil.isError(servResult)) {
				Debug.logError("Error getting procurement Price ", module);
				return ServiceUtil.returnError("Error getting procurement Price : ");
			}
	   	}catch (Exception e) {
	 		Debug.logError(e, "Error getting procurement Price !", module);
	 		return ServiceUtil.returnError("Error getting procurement Price " + e);	  
	  	}
	    
		BigDecimal totalUnitPrice = BigDecimal.ZERO;
		if(!productId.equals("RAW_MILK")){
			totalUnitPrice = kgFatPrice.add(kgSnfPrice);
		}
		else{
			totalUnitPrice = payablePrice;
		}
		Debug.log("payablePrice ====="+payablePrice);
		Debug.log("totalUnitPrice ====="+totalUnitPrice);
		
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,"INR");
  		try {
  			cart.setOrderType("SALES_ORDER");
  			cart.setChannelType("TANKER_SALES_CHANNEL");		
  			cart.setBillToCustomerPartyId(billToPartyId);
  			cart.setPlacingCustomerPartyId(billToPartyId);
  			cart.setShipToCustomerPartyId(ShipToPartyId);		
  			cart.setEndUserCustomerPartyId(billToPartyId);
  			cart.setBillFromVendorPartyId(billFromPartyId);
  			cart.setShipFromVendorPartyId(billToPartyId);
  			cart.setUserLogin(userLogin, dispatcher);
  		} catch (Exception e) {
  			Debug.logError(e, "Error in setting cart parameters", module);
  			return ServiceUtil.returnError("Error in setting cart parameters");
  		}
		
  		try{
        	ShoppingCartItem cartItem = null;
        	cartItem = ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null, quantity, totalUnitPrice,
			           null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
			           cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE);

			cart.addItemToEnd(cartItem);
			 
		}  
		catch (Exception exc) {
			Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
			return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: ");
		}	

  		 cart.setDefaultCheckoutOptions(dispatcher);
	  		CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator,cart);
         
	  		try {
				checkout.calcAndAddTax();
			} catch (GeneralException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        //Debug.log("checkou============="+checkout.createOrder(userLogin));

	        Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
	        if (ServiceUtil.isError(orderCreateResult)) {
				String errMsg =  ServiceUtil.getErrorMessage(orderCreateResult);
				Debug.logError(errMsg, "While Creating Order",module);
				return ServiceUtil.returnError(" Error While Creating Order !"+errMsg);
			}
	  		String orderId = (String) orderCreateResult.get("orderId");
	  		
	  		result.put("orderId", orderId);
	  		
	  		result = ServiceUtil.returnSuccess("Successfully Created SO order" +orderId);

		return result;
	}
   	
   	public static Map<String, Object> raiseInvoiceFromShipment(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Invoice Successfully Raised on Tanker Sales");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	
   	 	String productId = (String) context.get("productId");
   	 	String partyId = (String) context.get("partyId");
	 	String partyIdTo = (String) context.get("partyIdTo");
   	 	
	 	String milkTransferId = (String)context.get("milkTransferId");
   	 	
	 	String shipmentId = (String) context.get("shipmentId");
   	 	
	 	BigDecimal quantity = (BigDecimal) context.get("quantity");
   	 	BigDecimal receivedKgSnf = (BigDecimal) context.get("receivedKgSnf");
	 	BigDecimal receivedKgFat = (BigDecimal) context.get("receivedKgFat");
	 	
	 	BigDecimal fat = (BigDecimal) context.get("fat");
	 	BigDecimal snf = (BigDecimal) context.get("snf");
	 	
	 	BigDecimal receivedFat = (BigDecimal) context.get("receivedFat");
	 	BigDecimal receivedSnf = (BigDecimal) context.get("receivedSnf");
	 	
	 	
	 	// Calculate Price
	 	
	 	if(UtilValidate.isEmpty(fat)){
	    	fat = new BigDecimal(3.5);
	    }
	    if(UtilValidate.isEmpty(snf)){
	    	snf = new BigDecimal(8.5);
	    }
	    Debug.log("fatPercent ====="+fat);
	    Debug.log("snfPercent ====="+snf);
	    
	    String billFromPartyId="Company";
	    String productStoreId = "STORE";
	    
	    // Calculate Order Price Based on set Parameters(KG Fat and KG Snf)
	    
	    BigDecimal minSnf = new BigDecimal(7.5);
        BigDecimal maxSnf = new BigDecimal(12.0);
	    
        List componentPriceList = FastList.newInstance();
        BigDecimal fatPremium = BigDecimal.ZERO;
        BigDecimal snfPremium = BigDecimal.ZERO;
        BigDecimal payablePrice = BigDecimal.ZERO;
        BigDecimal kgFatPrice = BigDecimal.ZERO;
        BigDecimal kgSnfPrice = BigDecimal.ZERO;
        
	    Map priceCtx = FastMap.newInstance();
		priceCtx.put("userLogin",userLogin);
		priceCtx.put("productId",productId);
		priceCtx.put("partyId",partyIdTo);
	    priceCtx.put("priceDate",UtilDateTime.nowTimestamp());
		priceCtx.put("fatPercent", fat);
		priceCtx.put("snfPercent", snf);
		//Map priceResult = PriceServices.getProcurementProductPrice(dctx,priceCtx);
		Debug.log("priceCtx ====="+priceCtx);
		try{
			Map servResult = dispatcher.runSync("calculateProcurementProductPrice", priceCtx);
			Debug.log("create Order Result ====="+servResult);
			if(UtilValidate.isNotEmpty(servResult.get("componentPriceList"))){
				componentPriceList = (List) servResult.get("componentPriceList");
			}
			if(UtilValidate.isNotEmpty(servResult.get("fatPremium"))){
				fatPremium = (BigDecimal) servResult.get("fatPremium");
			}
			if(UtilValidate.isNotEmpty(servResult.get("snfPremium"))){
				snfPremium = (BigDecimal) servResult.get("snfPremium");
			}
			if(UtilValidate.isNotEmpty(servResult.get("payablePrice"))){
				payablePrice = (BigDecimal) servResult.get("payablePrice");
				Debug.log("create Order Result ====="+payablePrice);
			}
			if(UtilValidate.isNotEmpty(servResult.get("kgSnfPrice"))){
				kgSnfPrice = (BigDecimal) servResult.get("kgSnfPrice");
			}
			if(UtilValidate.isNotEmpty(servResult.get("kgFatPrice"))){
				kgFatPrice = (BigDecimal) servResult.get("kgFatPrice");
			}
			if (ServiceUtil.isError(servResult)) {
				Debug.logError("Error getting procurement Price ", module);
				return ServiceUtil.returnError("Error getting procurement Price : ");
			}
	   	}catch (Exception e) {
	 		Debug.logError(e, "Error getting procurement Price !", module);
	 		return ServiceUtil.returnError("Error getting procurement Price " + e);	  
	  	}
	    
		BigDecimal totalUnitPrice = BigDecimal.ZERO;
		if(!productId.equals("RAW_MILK")){
			totalUnitPrice = kgFatPrice.add(kgSnfPrice);
		}
		else{
			totalUnitPrice = payablePrice;
		}
		
		Debug.log("payablePrice ====="+payablePrice);
		Debug.log("totalUnitPrice ====="+totalUnitPrice);
	 	
		// create the invoice record
        String invoiceId = null;
        
		Map<String, Object> createInvoiceContext = FastMap.newInstance();
        createInvoiceContext.put("partyId", partyIdTo);
        createInvoiceContext.put("partyIdFrom", partyId);
        createInvoiceContext.put("invoiceDate", UtilDateTime.nowTimestamp());
        /*createInvoiceContext.put("dueDate", dueDate);*/
        createInvoiceContext.put("invoiceTypeId", "SALES_INVOICE");
        // start with INVOICE_IN_PROCESS, in the INVOICE_READY we can't change the invoice (or shouldn't be able to...)
        createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
        createInvoiceContext.put("purposeTypeId", "TANKERSALES_INVOICE");
        createInvoiceContext.put("shipmentId", shipmentId);
        createInvoiceContext.put("currencyUomId", "INR");
        createInvoiceContext.put("userLogin", userLogin);
         
        // store the invoice first
        try{
        	Map<String, Object> createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
        	Debug.log("createInvoiceResult ====="+createInvoiceResult);
            if (ServiceUtil.isError(createInvoiceResult)) {
            	Debug.logError("Error creating invoice for shipment : "+shipmentId, module);
				return ServiceUtil.returnError("Error creating invoice for shipment : "+shipmentId);
            }
            invoiceId = (String) createInvoiceResult.get("invoiceId");
            Debug.log("invoiceId ====="+invoiceId);
	   	}catch (Exception e) {
		 		Debug.logError(e, "Error creating invoice for shipment !", module);
		 		return ServiceUtil.returnError("Failed to create invoice for the shipment " + e);	  
	  	}
	 	
        List<GenericValue> shipmentItems = FastList.newInstance();
        try{
        	shipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false);
        }
        catch (Exception e) {
	 		Debug.logError(e, "Error creating invoice for shipment !", module);
	 		return ServiceUtil.returnError("Failed to create invoice for the shipment " + e);	  
        }
        
        for (GenericValue currentValue : shipmentItems) {
        	Debug.log("currentValue ====="+currentValue);
            BigDecimal billingAmount = totalUnitPrice;
            Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
            createInvoiceItemContext.put("invoiceId", invoiceId);
            createInvoiceItemContext.put("invoiceItemSeqId", currentValue.get("shipmentItemSeqId"));
            createInvoiceItemContext.put("invoiceItemTypeId", "INV_FPROD_ITEM");
            createInvoiceItemContext.put("quantity", quantity);
            createInvoiceItemContext.put("amount", billingAmount);
            createInvoiceItemContext.put("productId", productId);
            createInvoiceItemContext.put("unitPrice", billingAmount);
            createInvoiceItemContext.put("unitListPrice", billingAmount);
            createInvoiceItemContext.put("userLogin", userLogin);
            
            try{
            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
            	Debug.log("createInvoiceItemResult ====="+createInvoiceItemResult);
                if (ServiceUtil.isError(createInvoiceItemResult)) {
                	Debug.logError("Error creating invoice item for shipment !", module);
    		 		return ServiceUtil.returnError("Failed to create invoice item for the shipment ");	
                }
	        }catch (Exception e) {
		 		Debug.logError(e, "Error creating invoice item for shipment !", module);
		 		return ServiceUtil.returnError("Failed to create invoice item for the shipment " + e);	  
		  	}
            
            // create the ShipmentItemBilling record
            GenericValue shipmentItemBilling = delegator.makeValue("ShipmentItemBilling", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", currentValue.get("shipmentItemSeqId")));
            shipmentItemBilling.put("shipmentId", shipmentId);
            shipmentItemBilling.put("shipmentItemSeqId", currentValue.get("shipmentItemSeqId"));
            Debug.log("shipmentItemBilling ====="+shipmentItemBilling);
            try {
            	shipmentItemBilling.create();
            } catch (GenericEntityException e) {
                ServiceUtil.returnError(e.getMessage());
            }
            
        }
        
        
        
        
        Debug.log("shipmentId ====="+shipmentId);
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
	 	
   	 	
	 	/*Map inputCtx = FastMap.newInstance();
		
		inputCtx.put("shipmentId", shipmentId);
		inputCtx.put("userLogin", userLogin);*/
		/*try{
			Map result = dispatcher.runSync("createInvoicesFromShipment", inputCtx);
			if (ServiceUtil.isError(result)) {
				Debug.logError("Error creating invoice for shipment : "+shipmentId, module);
				return ServiceUtil.returnError("Error creating invoice for shipment : "+shipmentId);
			}
	   	}catch (Exception e) {
		 		Debug.logError(e, "Error creating invoice for shipment !", module);
		 		return ServiceUtil.returnError("Failed to create invoice for the shipment " + e);	  
	  	}*/
	 	
	 	resultMap.put("shipmentId", shipmentId);
	 	resultMap.put("milkTransferId", milkTransferId);
	 	
    	return resultMap;
   
   	}
   	
   	/*public static Map<String, Object> getTankerSalesProductPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productId = (String) context.get("productId");    
        //String facilityId = (String) context.get("facilityId");
        String categoryTypeEnum = (String) context.get("categoryTypeEnum");
        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");           
        Timestamp priceDate = (Timestamp) context.get("priceDate");
        String uomId = null;
        String lrFormulaId=null;
        // pass in this flag to, get special  premium price
        String isPremiumChart = (String) context.get("isPremiumChart");
        if (UtilValidate.isEmpty(priceDate)) {
        	priceDate = UtilDateTime.nowTimestamp();
        }
        BigDecimal fatPercent = (BigDecimal) context.get("fatPercent");
        BigDecimal snfPercent = (BigDecimal) context.get("snfPercent");
        List<BigDecimal> fatPercentList = FastList.newInstance();
        List<BigDecimal> snfPercentList = FastList.newInstance();
        if (UtilValidate.isEmpty(fatPercent) && UtilValidate.isEmpty(snfPercent)) {
        	BigDecimal minSnf = new BigDecimal(7.5);
            BigDecimal maxSnf = new BigDecimal(12.0);
            
            while(minSnf.compareTo(maxSnf) < 0){	
            	snfPercentList.add(minSnf.setScale(1,0));
            	minSnf = minSnf.add(new BigDecimal(0.1));
            }
            
            BigDecimal minFat = new BigDecimal(2.5);
            BigDecimal  maxFat= new BigDecimal(9);
            while(minFat.compareTo(maxFat) < 0){	
            	fatPercentList.add(minFat.setScale(1,0));
            	minFat = minFat.add(new BigDecimal(0.1));
            }
            
        }  
        BigDecimal defaultRate = BigDecimal.ZERO;		
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal premium = BigDecimal.ZERO; 
        String useTotalSolids ="N";
        result.put("defaultRate", defaultRate);          
        result.put("price", price);  
        result.put("premium", premium);
        result.put("useTotalSolids", useTotalSolids);
        Map priceChartMap = FastMap.newInstance();
    	try {
    		// Fist get the appropriate Procurement Chart 
   		 	GenericValue priceChart = fetchPriceChart(dctx, UtilMisc.toMap("priceDate", priceDate, "facilityId", facilityId ,"categoryTypeEnum", categoryTypeEnum ,"supplyTypeEnumId" , supplyTypeEnumId ,"isPremiumChart" ,isPremiumChart));  
   		 	if(UtilValidate.isEmpty(priceChart)){
   		 		Debug.logInfo("No valid price chart found!", module);
          		return ServiceUtil.returnError("No valid price chart found!");  
   		 	} 
   		 	if(UtilValidate.isNotEmpty(priceChart.get("uomId"))){
		 		uomId = priceChart.getString("uomId");
		 	}
   		 	if(UtilValidate.isNotEmpty(priceChart.get("lrFormulaId"))){
   		 		lrFormulaId = priceChart.getString("lrFormulaId");
   		 	}
   		 	// Compute price from the chart based on the input parameters
   		 	
   	        BigDecimal tempFatPercent =BigDecimal.ZERO;
    		BigDecimal tempSnfPercent =BigDecimal.ZERO;
    		Map priceInMap =FastMap.newInstance();
    		priceInMap.putAll(context);
    		priceInMap.put("priceChartId",  priceChart.getString("procPriceChartId"));
    		if(UtilValidate.isEmpty(fatPercent) && UtilValidate.isEmpty(snfPercent)){
    			for(int j=0; j<fatPercentList.size(); j++){
    				tempFatPercent = fatPercentList.get(j);
    				priceInMap.put("fatPercent", tempFatPercent);
		 			Map tempSnfPriceMap = FastMap.newInstance();
		   		 	for(int i=0; i<snfPercentList.size();i++){
		   		 		tempSnfPercent = snfPercentList.get(i);
		   		 		price = BigDecimal.ZERO;
		   		 		priceInMap.put("snfPercent", tempSnfPercent);
		   		 		
		   		 		result = calculateProcurementProductPrice(dctx ,priceInMap);
		   		 		
		   		 		price = (BigDecimal)result.get("price");
		   		 		useTotalSolids = (String)result.get("useTotalSolids");
	   		 			tempSnfPriceMap.put(tempSnfPercent, price.setScale(2,BigDecimal.ROUND_HALF_UP));
	   		 		} // end of FatList for loop  		 		
	   		 		Map tempSnfPrice = FastMap.newInstance();
	   		 		tempSnfPrice.putAll(tempSnfPriceMap);
	   		 		priceChartMap.put(tempFatPercent,tempSnfPrice);
   		 		}// end of SnfList for loop
    		}else{    			
    			priceInMap.put("snfPercent", snfPercent);
    			priceInMap.put("fatPercent", fatPercent);
   		 		result = calculateProcurementProductPrice(dctx ,priceInMap);
   		 		if(UtilValidate.isNotEmpty(result.get("uomId"))){
   		 			uomId = (String)result.get("uomId"); 
   		 		}
   		 		
   		 		price = (BigDecimal)result.get("price");
   		 		premium = (BigDecimal)result.get("premium");
   		 		useTotalSolids = (String)result.get("useTotalSolids");   		 		
    		}
    		
   		 	
    	} catch (Exception e) {
           Debug.logError(e, module);
           return ServiceUtil.returnError(e.getMessage());
    	}	     
    	result.put("priceChartMap", priceChartMap);
    	result.put("snfPercentList", snfPercentList);
        result.put("price", price);
        result.put("uomId", uomId);
        result.put("premium", premium);  
        result.put("lrFormulaId", lrFormulaId);
        result.put("useTotalSolids", useTotalSolids);
		if (Debug.infoOn()) {
			Debug.logInfo("result =" + result, module);
		}        
        return result;
    }*/
   	
   	
   	
   	
}
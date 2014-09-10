package in.vasista.vbiz.purchase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import in.vasista.vbiz.byproducts.ByProductServices;

public class PurchaseStoreServices {

public static final String module = PurchaseStoreServices.class.getName();

   
public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
public static final BigDecimal ONE_BASE = BigDecimal.ONE;
public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
public static int salestaxFinalDecimals = UtilNumber.getBigDecimalScale("salestax.final.decimals");
public static int salestaxCalcDecimals = 2;//UtilNumber.getBigDecimalScale("salestax.calc.decimals");

public static int salestaxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
	    
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

public static String processPurchaseOrder(HttpServletRequest request, HttpServletResponse response) {
	Delegator delegator = (Delegator) request.getAttribute("delegator");
	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	DispatchContext dctx =  dispatcher.getDispatchContext();
	Locale locale = UtilHttp.getLocale(request);
	String partyId = (String) request.getParameter("partyId");
	Map resultMap = FastMap.newInstance();
	List invoices = FastList.newInstance(); 
	String vehicleId = (String) request.getParameter("vehicleId");
	String effectiveDateStr = (String) request.getParameter("effectiveDate");
	String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
	String shipmentTypeId = (String) request.getParameter("shipmentTypeId");

	String freightChargesStr=(String) request.getParameter("freightCharges");
	String discountStr=(String) request.getParameter("discount");
	String PONumber=(String) request.getParameter("PONumber");
	String mrnNumber=(String) request.getParameter("mrnNumber");
	String SInvNumber=(String) request.getParameter("SInvNumber");
	
	String subscriptionTypeId = "AM";
	String partyIdFrom = "";
	String shipmentId = "";
	Map processOrderContext = FastMap.newInstance();
	String salesChannel = (String)request.getParameter("salesChannel");
   
	if(UtilValidate.isNotEmpty(shipmentTypeId) && shipmentTypeId.equals("RM_DIRECT_SHIPMENT") && UtilValidate.isEmpty(salesChannel)){
		salesChannel = "RM_DIRECT_CHANNEL";      	
	}
	if(UtilValidate.isNotEmpty(shipmentTypeId) && shipmentTypeId.equals("ICP_DIRECT_SHIPMENT") && UtilValidate.isEmpty(salesChannel)){
		salesChannel = "ICP_NANDINI_CHANNEL";      	
	}
  
	String productId = null;
	String batchNo = null;
	String quantityStr = null;
	String unitPriceStr=null;
	String vatStr=null;
	String exciseStr=null;
	String cstStr=null;
	Timestamp effectiveDate=null;
	BigDecimal quantity = BigDecimal.ZERO;
	BigDecimal uPrice = BigDecimal.ZERO;
	BigDecimal vat = BigDecimal.ZERO;
	BigDecimal cst = BigDecimal.ZERO;
	BigDecimal excise = BigDecimal.ZERO;
	
	
	Map<String, Object> result = ServiceUtil.returnSuccess();
	HttpSession session = request.getSession();
	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
		try {
			effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
		} catch (NullPointerException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
		}
	}
	else{
		effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
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
	if(UtilValidate.isNotEmpty(request.getAttribute("estimatedDeliveryDate"))) {
		effectiveDate = (Timestamp) request.getAttribute("estimatedDeliveryDate");
	}

	Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	if (rowCount < 1) {
		Debug.logError("No rows to process, as rowCount = " + rowCount, module);
		return "success";
	}
	BigDecimal freightCharges = BigDecimal.ZERO;
	BigDecimal discount = BigDecimal.ZERO;
	try {
		if (!freightChargesStr.equals("")) {
			freightCharges = new BigDecimal(freightChargesStr);
			}
	} catch (Exception e) {
		Debug.logError(e, "Problems parsing freightCharges string: " + freightChargesStr, module);
		request.setAttribute("_ERROR_MESSAGE_", "Problems parsing freightCharges string: " + freightChargesStr);
		return "error";
	}
	try {
		if (!discountStr.equals("")) {
		discount = new BigDecimal(discountStr);
		}
	} catch (Exception e) {
		Debug.logError(e, "Problems parsing discount string: " + discountStr, module);
		request.setAttribute("_ERROR_MESSAGE_", "Problems parsing discount string: " + discountStr);
		return "error";
	}
  
	List indentProductList = FastList.newInstance();
	for (int i = 0; i < rowCount; i++) {
	  
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
		
		/*if (paramMap.containsKey("batchNo" + thisSuffix)) {
			batchNo = (String) paramMap.get("batchNo" + thisSuffix);
		}
		else {
			request.setAttribute("_ERROR_MESSAGE_", "Missing Batch Number");
			return "error";			  
		}*/
		if (paramMap.containsKey("UPrice" + thisSuffix)) {
		   unitPriceStr = (String) paramMap.get("UPrice" + thisSuffix);
		}
		if (paramMap.containsKey("VAT" + thisSuffix)) {
			vatStr = (String) paramMap.get("VAT" + thisSuffix);
		}
		if (paramMap.containsKey("excise" + thisSuffix)) {
			exciseStr = (String) paramMap.get("excise" + thisSuffix);
		}
		if (paramMap.containsKey("CST" + thisSuffix)) {
			cstStr = (String) paramMap.get("CST" + thisSuffix);
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
			if (!exciseStr.equals("")) {
			excise = new BigDecimal(exciseStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing excise string: " + exciseStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing excise string: " + exciseStr);
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
	
		productQtyMap.put("productId", productId);
		productQtyMap.put("quantity", quantity);
		productQtyMap.put("unitPrice", uPrice);
		productQtyMap.put("vatPercentage", vat);
		productQtyMap.put("cstPercentage", cst);
		productQtyMap.put("excisePercentage", excise);
		//productQtyMap.put("batchNo", batchNo);
		indentProductList.add(productQtyMap);
	}//end row count for loop
	if( UtilValidate.isEmpty(indentProductList)){
		Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
		request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
		return "success";
	}
	String productStoreId = (String) (getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
 
	processOrderContext.put("userLogin", userLogin);
	processOrderContext.put("productQtyList", indentProductList);
	processOrderContext.put("partyId", partyId);
	processOrderContext.put("supplyDate", effectiveDate);
	processOrderContext.put("salesChannel", salesChannel);
	processOrderContext.put("vehicleId", vehicleId);
	processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
	processOrderContext.put("shipmentTypeId", shipmentTypeId);
	processOrderContext.put("productStoreId", productStoreId);
	processOrderContext.put("freightCharges", freightCharges);
	processOrderContext.put("discount", discount);
	processOrderContext.put("mrnNumber", mrnNumber);
	processOrderContext.put("PONumber", PONumber);
	processOrderContext.put("SInvNumber", SInvNumber);
	

	result = createPurchaseOrder(dctx, processOrderContext);
	if(ServiceUtil.isError(result)){
		Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
		request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
		return "error";
	}
	
	request.setAttribute("_EVENT_MESSAGE_", "Entry successful for party: "+partyId);	  	 
	return "success";
}
public static Map<String, Object> createPurchaseOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
	Delegator delegator = dctx.getDelegator();
    LocalDispatcher dispatcher = dctx.getDispatcher();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    Map<String, Object> result = ServiceUtil.returnSuccess();
    List<Map> productQtyList = (List) context.get("productQtyList");
    Timestamp supplyDate = (Timestamp) context.get("supplyDate");
    Locale locale = (Locale) context.get("locale");
    String productStoreId = (String) context.get("productStoreId");
  	String salesChannel = (String) context.get("salesChannel");
  	String vehicleId = (String) context.get("vehicleId");
  	String partyId = (String) context.get("partyId");
  	//fright Charges
  	BigDecimal freightCharges = (BigDecimal) context.get("freightCharges");
  	BigDecimal discount = (BigDecimal) context.get("discount");
	/*BigDecimal freightCharges = BigDecimal.ZERO;
	BigDecimal discount = BigDecimal.ZERO;*/
  	String mrnNumber = (String) context.get("mrnNumber");
  	String PONumber=(String) context.get("PONumber");
  	String SInvNumber = (String) context.get("SInvNumber");
	
  	//Debug.log("=AFTER==CONTEXT==freightCharges="+freightCharges+"=discount="+discount+"==mrnNumber=="+mrnNumber+"=PONumber="+PONumber+"=SInvNumber="+SInvNumber);
  	String currencyUomId = "INR";
  	String shipmentId = (String) context.get("shipmentId");
  	String shipmentTypeId = (String) context.get("shipmentTypeId");
	Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
	Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
	String orderId = "";
	String billToPartyId="Company";
	if(UtilValidate.isEmpty(shipmentId)){
		GenericValue newDirShip = delegator.makeValue("Shipment");        	 
		newDirShip.set("estimatedShipDate", effectiveDate);
		newDirShip.set("shipmentTypeId", shipmentTypeId);
		newDirShip.set("statusId", "GENERATED");
		newDirShip.set("vehicleId", vehicleId);
		newDirShip.set("createdDate", nowTimeStamp);
		newDirShip.set("createdByUserLogin", userLogin.get("userLoginId"));
		newDirShip.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		/*try {
			delegator.createSetNextSeqId(newDirShip);            
			shipmentId = (String) newDirShip.get("shipmentId");
		} catch (GenericEntityException e) {
			Debug.logError("Error in creating shipmentId for DirectOrder", module);
			return ServiceUtil.returnError("Error in creating shipmentId for DirectOrder");
		}  */
	}
	if (UtilValidate.isEmpty(partyId)) {
		Debug.logError("Cannot create order without partyId: "+ partyId, module);
		return ServiceUtil.returnError("partyId is empty");
	}
	GenericValue product =null;
	String productPriceTypeId = null;
	GenericValue shipment = null;
	try{
		shipment=delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
		
	}catch(GenericEntityException e){
		Debug.logError("Error in fetching shipment : "+ shipmentId, module);
		return ServiceUtil.returnError("Error in fetching shipment : "+shipmentId);
	}
	ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
	
	try {
		cart.setOrderType("PURCHASE_ORDER");
       // cart.setIsEnableAcctg("N");
		cart.setExternalId(PONumber);
        cart.setProductStoreId(productStoreId);
		cart.setChannelType(salesChannel);
		cart.setBillToCustomerPartyId(billToPartyId);
		cart.setPlacingCustomerPartyId(billToPartyId);
		cart.setShipToCustomerPartyId(billToPartyId);
		cart.setEndUserCustomerPartyId(billToPartyId);
		//cart.setShipmentId(shipmentId);
		//for PurchaseOrder we have to use for SupplierId
	    cart.setBillFromVendorPartyId(partyId);
	    cart.setShipFromVendorPartyId(partyId);
	    cart.setSupplierAgentPartyId(partyId);
		
		cart.setEstimatedDeliveryDate(effectiveDate);
		cart.setOrderDate(effectiveDate);
		cart.setUserLogin(userLogin, dispatcher);
	} catch (Exception e) {
		
		Debug.logError(e, "Error in setting cart parameters", module);
		return ServiceUtil.returnError("Error in setting cart parameters");
	}
	String productId = "";
	BigDecimal quantity = BigDecimal.ZERO;
	String batchNo = "";
	BigDecimal unitPrice = BigDecimal.ZERO;
	BigDecimal vat = BigDecimal.ZERO;
	BigDecimal cst = BigDecimal.ZERO;
	BigDecimal excise = BigDecimal.ZERO;
	List<GenericValue> prodPriceTypeList = FastList.newInstance();
	
	for (Map<String, Object> prodQtyMap : productQtyList) {
		List taxList=FastList.newInstance();
		BigDecimal totalTaxAmt =  BigDecimal.ZERO;
		
		if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
			productId = (String)prodQtyMap.get("productId");
		}
		if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
			quantity = (BigDecimal)prodQtyMap.get("quantity");
		}
		if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
			unitPrice = (BigDecimal)prodQtyMap.get("unitPrice");
		}
		BigDecimal tempPrice = BigDecimal.ZERO;
		tempPrice = tempPrice.add(unitPrice);
		
		if(unitPrice.compareTo(BigDecimal.ZERO)>0){
			if(UtilValidate.isNotEmpty(prodQtyMap.get("excisePercentage"))){
				excise = (BigDecimal)prodQtyMap.get("excisePercentage");
				BigDecimal taxRate = excise;
				BigDecimal taxAmount = BigDecimal.ZERO;
	        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
	        		taxAmount = (unitPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	        		Map taxDetailMap = FastMap.newInstance();
		    		taxDetailMap.put("taxType", "BED_PUR");
		    		//taxDetailMap.put("taxType", "BED_SALE");
		    		taxDetailMap.put("amount", taxAmount);
		    		taxDetailMap.put("percentage", taxRate);
		    		taxList.add(taxDetailMap);

		    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
		    		newProdPriceType.set("fromDate", effectiveDate);
		    		newProdPriceType.set("parentTypeId", "TAX");
		    		newProdPriceType.set("productId", productId);
		    		newProdPriceType.set("productStoreGroupId", "_NA_");
		    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
		    		newProdPriceType.set("productPriceTypeId", "BED_PUR");
		    		newProdPriceType.set("taxPercentage", taxRate);
		    		newProdPriceType.set("taxAmount", taxAmount);
		    		newProdPriceType.set("currencyUomId", "INR");
		    		prodPriceTypeList.add(newProdPriceType);
	        	}
	        	tempPrice=tempPrice.add(taxAmount);
	        	totalTaxAmt=totalTaxAmt.add(taxAmount);
			}
			//Debug.log("=tempPrice=="+tempPrice+"=totalTaxAmt="+totalTaxAmt);
			if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercentage"))){
				BigDecimal taxRate = (BigDecimal)prodQtyMap.get("vatPercentage");
				BigDecimal taxAmount = BigDecimal.ZERO;
	        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
	        		taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	        		Map taxDetailMap = FastMap.newInstance();
		    		//taxDetailMap.put("taxType", "VAT_SALE");
		    		taxDetailMap.put("taxType", "VAT_PUR");
		    		taxDetailMap.put("amount", taxAmount);
		    		taxDetailMap.put("percentage", taxRate);
		    		taxList.add(taxDetailMap);
		        	/*if(taxPrice.compareTo(BigDecimal.ZERO)>0){
		        		taxAmount = itemQuantity.multiply(taxPrice).setScale(salestaxCalcDecimals, salestaxRounding);
		        	}*/
		    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
		    		newProdPriceType.set("fromDate", effectiveDate);
		    		newProdPriceType.set("parentTypeId", "TAX");
		    		newProdPriceType.set("productId", productId);
		    		newProdPriceType.set("productStoreGroupId", "_NA_");
		    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
		    		newProdPriceType.set("productPriceTypeId", "VAT_PUR");
		    		newProdPriceType.set("taxPercentage", taxRate);
		    		newProdPriceType.set("taxAmount", taxAmount);
		    		newProdPriceType.set("currencyUomId", "INR");
		    		prodPriceTypeList.add(newProdPriceType);
	        	}
	        	totalTaxAmt=totalTaxAmt.add(taxAmount);
	        	
	        	
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercentage"))){
				cst = (BigDecimal)prodQtyMap.get("cstPercentage");
				BigDecimal taxRate = cst;
				BigDecimal taxAmount = BigDecimal.ZERO;
	        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
	        		taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	        		Map taxDetailMap = FastMap.newInstance();
		    		//taxDetailMap.put("taxType", "CST_SALE");
		    		taxDetailMap.put("taxType", "CST_PUR");
		    		taxDetailMap.put("amount", taxAmount);
		    		taxDetailMap.put("percentage", taxRate);
		    		taxList.add(taxDetailMap);
		    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
		    		newProdPriceType.set("fromDate", effectiveDate);
		    		newProdPriceType.set("parentTypeId", "TAX");
		    		newProdPriceType.set("productId", productId);
		    		newProdPriceType.set("productStoreGroupId", "_NA_");
		    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
		    		newProdPriceType.set("productPriceTypeId", "CST_PUR");
		    		newProdPriceType.set("taxPercentage", taxRate);
		    		newProdPriceType.set("taxAmount", taxAmount);
		    		newProdPriceType.set("currencyUomId", "INR");
		    		prodPriceTypeList.add(newProdPriceType);
	        	}
	        	totalTaxAmt=totalTaxAmt.add(taxAmount);
			}
		}
	
		BigDecimal totalPrice = unitPrice.add(totalTaxAmt);
		//BigDecimal totalTaxAmt = BigDecimal.ZERO;
		//Debug.log("==totalPrice==="+totalPrice+"==totalTaxAmt="+totalTaxAmt+"=unitPrice="+unitPrice);
		//List taxList = (List)priceResult.get("taxList");
		//Debug.log("=========taxList====="+taxList);
		ShoppingCartItem item = null;
		try{
			int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null,	quantity, unitPrice,
			            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
			            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
			
			item = cart.findCartItem(itemIndx);
			item.setListPrice(totalPrice);
    		item.setTaxDetails(taxList);
		}
		catch (Exception exc) {
			Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
			return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: ");
        }
		
	}
	cart.setDefaultCheckoutOptions(dispatcher);
    ProductPromoWorker.doPromotions(cart, dispatcher);
    CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);

	//Debug.log("=========prodPriceTypeList====="+prodPriceTypeList);
	
	try {
		checkout.calcAndAddTax(prodPriceTypeList);
	} catch (Exception e1) {
	// TODO Auto-generated catch block
		Debug.logError(e1, "Error in CalcAndAddTax",module);
	}
	Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
	orderId = (String) orderCreateResult.get("orderId");
	//let's create Fright Adhustment here
	// handle employee subsidies here 
    //if(productSubscriptionTypeId.equals("EMP_SUBSIDY")){
		if(freightCharges.compareTo(BigDecimal.ZERO)>0){
	    	Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
	    	adjustCtx.put("orderId", orderId);
	    	adjustCtx.put("adjustmentTypeId", "FREIGHT_CHARGES");
	    	adjustCtx.put("adjustmentAmount", freightCharges);
	    	Map adjResultMap=FastMap.newInstance();
		  	 	try{
		  	 		 adjResultMap = dispatcher.runSync("createAdjustmentForPurchaseOrder",adjustCtx);  		  		 
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
		if(discount.compareTo(BigDecimal.ZERO)>0){
	    	Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
	    	adjustCtx.put("orderId", orderId);
	    	adjustCtx.put("adjustmentTypeId", "DISCOUNT");
	    	adjustCtx.put("adjustmentAmount", discount);
	    	Map adjResultMap=FastMap.newInstance();
		  	 	try{
		  	 		 adjResultMap = dispatcher.runSync("createAdjustmentForPurchaseOrder",adjustCtx);  		  		 
		  	 		if (ServiceUtil.isError(adjResultMap)) {
		  	 			String errMsg =  ServiceUtil.getErrorMessage(adjResultMap);
		  	 			Debug.logError(errMsg , module);
		  	 		 return ServiceUtil.returnError(" Error While discount Adjustment for Purchase Order !");
		  	 		}
		  	 	}catch (Exception e) {
		  			  Debug.logError(e, "Error While Creating discount Adjustment for Purchase Order ", module);
		  			  return adjResultMap;			  
		  	 	}
	    }
		
		/*String mrnNumber = (String) context.get("mrnNumber");
	  	String PONumber=(String) context.get("PONumber");
	  	String SInvNumber = (String) context.get("SInvNumber");*/
		try{
		GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
		orderAttribute.set("orderId", orderId);
		orderAttribute.set("attrName", "MRN_NUMBER");
		orderAttribute.set("attrValue", mrnNumber);
		delegator.createOrStore(orderAttribute);
		}catch (Exception e) {
			  Debug.logError(e, "Error While Creating Attribute(MRN_NUMBER)  for Purchase Order ", module);
			  return ServiceUtil.returnError("Error While Creating Attribute(MRN_NUMBER)  for Purchase Order : "+orderId);
  	 	}
		try{
			GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
			orderAttribute.set("orderId", orderId);
			orderAttribute.set("attrName", "SUP_INV_NUMBER");
			orderAttribute.set("attrValue", SInvNumber);
			delegator.createOrStore(orderAttribute);
			}catch (Exception e) {
				  Debug.logError(e, "Error While Creating Attribute(SUP_INV_NUMBER)  for Purchase Order ", module);
				  return ServiceUtil.returnError("Error While Creating Attribute(SUP_INV_NUMBER)  for Purchase Order : "+orderId);
	  	 	}
		try{
			GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
			orderAttribute.set("orderId", orderId);
			orderAttribute.set("attrName", "PO_NUMBER");
			orderAttribute.set("attrValue", PONumber);
			delegator.createOrStore(orderAttribute);
			}catch (Exception e) {
				  Debug.logError(e, "Error While Creating Attribute(PO_NUMBER)  for Purchase Order ", module);
				  return ServiceUtil.returnError("Error While Creating Attribute(PO_NUMBER)  for Purchase Order : "+orderId);
	  	 	}
			//update PurposeType
			try{
			GenericValue orderHeaderPurpose = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			orderHeaderPurpose.set("purposeTypeId", salesChannel);
			orderHeaderPurpose.store();
			}catch (Exception e) {
				  Debug.logError(e, "Error While Updating purposeTypeId for Order ", module);
				  return ServiceUtil.returnError("Error While Updating purposeTypeId for Order : "+orderId);
	  	 	}
		
	// let's handle order rounding here
    /*try{   
    	Map roundAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
    	roundAdjCtx.put("orderId", orderId);
  	 	result = dispatcher.runSync("adjustRoundingDiffForOrder",roundAdjCtx);  		  		 
  	 	if (ServiceUtil.isError(result)) {
  	 		String errMsg =  ServiceUtil.getErrorMessage(result);
  	 		Debug.logError(errMsg , module);
  	 		shipment.set("statusId", "GENERATION_FAIL");
	      	  	shipment.store();
	      	  	return ServiceUtil.returnError(errMsg+"==Error While  Rounding Order !");
	 		}
	 	}catch (Exception e) {
	 		Debug.logError(e, "Error while Creating Order", module);
  		try{
  			shipment.set("statusId", "GENERATION_FAIL");
      		shipment.store();
        }catch (Exception ex) {
        	Debug.logError(ex, module);        
            return ServiceUtil.returnError(ex.toString());
		}
        return ServiceUtil.returnError(e+"==Error While  Rounding Order !");
  		//return resultMap;			  
  	}*/
    // approve the order
    if (UtilValidate.isNotEmpty(orderId)) {
        boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
       	try{
       		result = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", orderId,"eventDate", effectiveDate,"userLogin", userLogin));
        	if (ServiceUtil.isError(result)) {
        		Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(result), module);
            	return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(result));          	            
            }
        	Debug.log("result invoiceId  #################################"+result);
        	Boolean enableAdvancePaymentApp  = Boolean.FALSE;
        	try{        	 	
        		GenericValue tenantConfigEnableAdvancePaymentApp = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableAdvancePaymentApp"), true);
           		if (UtilValidate.isNotEmpty(tenantConfigEnableAdvancePaymentApp) && (tenantConfigEnableAdvancePaymentApp.getString("propertyValue")).equals("Y")) {
           			enableAdvancePaymentApp = Boolean.TRUE;
           		} 
   	        }catch (GenericEntityException e) {
   	        	Debug.logError(e, module);
   			}
      		if(context.get("enableAdvancePaymentApp") != null){
      			enableAdvancePaymentApp = (Boolean)context.get("enableAdvancePaymentApp");
       		}
   	      	if(enableAdvancePaymentApp){
   	      		Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", result.get("invoiceId"));
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
	            // apply invoice if any adavance payments from this  party
     			Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", (String)result.get("invoiceId"),"userLogin", userLogin));
     			if (ServiceUtil.isError(resultPaymentApp)) {						  
     				Debug.logError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp), module);			             
     	            return ServiceUtil.returnError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp));  
     		    }
    		}//end of advance payment appl   
        }catch (Exception e) {
        	Debug.logError(e, module);
            try{
            	shipment.set("statusId", "GENERATION_FAIL");
         		shipment.store();
            }catch (Exception ex) {
            	Debug.logError(e, module);        
            	return ServiceUtil.returnError(e.toString());
   			}
            return ServiceUtil.returnError(e.toString()); 
        }
    }
	result.put("orderId", orderId);
	Debug.log("result successful  #################################");
	return result;
}
public static Map<String, Object> createAdjustmentForPurchaseOrder(DispatchContext dctx, Map<String, ? extends Object> context){
    Delegator delegator = dctx.getDelegator();
    LocalDispatcher dispatcher = dctx.getDispatcher();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String adjustmentTypeId = (String) context.get("adjustmentTypeId");
    //String adjustmentAmount = (String) context.get("adjustmentAmount");
    BigDecimal adjustmentAmount = (BigDecimal) context.get("adjustmentAmount");
    String orderId = (String) context.get("orderId");
    Locale locale = (Locale) context.get("locale");     
    Map result = ServiceUtil.returnSuccess();
    BigDecimal subPercent = new BigDecimal("50");
    BigDecimal subAmount = BigDecimal.ZERO;
    //Debug.log("adjustmentAmount==FOR==>"+adjustmentAmount+"=adjustmentTypeId=="+adjustmentTypeId);
	try {
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		/*List<GenericValue> orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
		for(GenericValue orederItem : orderItems){
			Map<String, Object> priceContext = FastMap.newInstance();
            priceContext.put("userLogin", userLogin);   
            //priceContext.put("productStoreId", orderHeader.getString("productStoreId"));                    
            //priceContext.put("productId", orederItem.getString("productId"));
            //priceContext.put("partyId", "_NA_");
            //priceContext.put("facilityId", orderHeader.getString("originFacilityId")); 
            //priceContext.put("priceDate", orderHeader.getTimestamp("estimatedDeliveryDate"));
            
            //Debug.log("priceContext==================="+priceContext);
            Map priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);            			
            if (ServiceUtil.isError(priceResult)) {
                Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
        		return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
            }  
            BigDecimal defaultPrice = (BigDecimal)priceResult.get("totalPrice");
            BigDecimal marginAmount = (orederItem.getBigDecimal("unitPrice")).subtract(defaultPrice);
			BigDecimal itemSubAmt = (orederItem.getBigDecimal("unitPrice").multiply(subPercent.divide(new BigDecimal("100")))).add(marginAmount);
			subAmount = (subAmount.add(itemSubAmt)).multiply(orederItem.getBigDecimal("quantity"));
			
		}*/
		
		if("FREIGHT_CHARGES".equalsIgnoreCase(adjustmentTypeId)){
			String orderAdjustmentTypeId = "COGS_ITEM16";
			 Map createOrderAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
	    	 createOrderAdjustmentCtx.put("orderId", orderId);
	    	 createOrderAdjustmentCtx.put("orderAdjustmentTypeId", orderAdjustmentTypeId);    	
	    	 createOrderAdjustmentCtx.put("amount", adjustmentAmount);
	    	 //Debug.log("createOrderAdjustmentCtx==For====FRIGHT_CHARGES=="+createOrderAdjustmentCtx);
	    	 result = dispatcher.runSync("createOrderAdjustment", createOrderAdjustmentCtx);
	     	 if (ServiceUtil.isError(result)) {
	                Debug.logWarning("There was an error while creating  the adjustment: " + ServiceUtil.getErrorMessage(result), module);
	         		return ServiceUtil.returnError("There was an error while creating the adjustment: " + ServiceUtil.getErrorMessage(result));          	            
	         } 
		}
		if("DISCOUNT".equalsIgnoreCase(adjustmentTypeId)){
			String orderAdjustmentTypeId = "COGS_ITEM17";
			 Map createOrderAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
	    	 createOrderAdjustmentCtx.put("orderId", orderId);
	    	 createOrderAdjustmentCtx.put("orderAdjustmentTypeId", orderAdjustmentTypeId);    	
	    	 createOrderAdjustmentCtx.put("amount", adjustmentAmount.negate());//minus discount value from total order
	    	 //Debug.log("createOrderAdjustment==FOR====DISCOUNT=="+createOrderAdjustmentCtx);
	    	 result = dispatcher.runSync("createOrderAdjustment", createOrderAdjustmentCtx);
	     	 if (ServiceUtil.isError(result)) {
	                Debug.logWarning("There was an error while creating  the adjustment: " + ServiceUtil.getErrorMessage(result), module);
	         		return ServiceUtil.returnError("There was an error while creating the adjustment: " + ServiceUtil.getErrorMessage(result));          	            
	         } 
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
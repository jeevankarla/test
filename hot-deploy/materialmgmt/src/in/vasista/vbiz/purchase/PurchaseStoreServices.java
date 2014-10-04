package in.vasista.vbiz.purchase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
	String SInvoiceDateStr=(String) request.getParameter("SInvoiceDate");
	String insurenceStr=(String) request.getParameter("insurence");
	
	
	
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
	
	Timestamp effectiveDate=null;
	BigDecimal quantity = BigDecimal.ZERO;
	BigDecimal uPrice = BigDecimal.ZERO;
	
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
	Timestamp SInvoiceDate=null;
	if (UtilValidate.isNotEmpty(SInvoiceDateStr)) { //2011-12-25 18:09:45
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
		try {
			SInvoiceDate = new java.sql.Timestamp(sdf.parse(SInvoiceDateStr).getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse SupplierInvoice Date String: " + SInvoiceDateStr, module);
		} catch (NullPointerException e) {
			Debug.logError(e, "Cannot parse SupplierInvoice Date String: " + SInvoiceDateStr, module);
		}
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
	BigDecimal insurence = BigDecimal.ZERO;
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
	try {
		if (!insurenceStr.equals("")) {
			insurence = new BigDecimal(insurenceStr);
		}
	} catch (Exception e) {
		Debug.logError(e, "Problems parsing insurence string: " + insurenceStr, module);
		request.setAttribute("_ERROR_MESSAGE_", "Problems parsing insurence string: " + insurenceStr);
		return "error";
	}
  
	List indentProductList = FastList.newInstance();
	for (int i = 0; i < rowCount; i++) {
		
		String vatStr=null;
		String exciseStr=null;
		String bedCessStr=null;
		String bedSecCessStr=null;
		String cstStr=null;
		
		BigDecimal vat = BigDecimal.ZERO;
		BigDecimal cst = BigDecimal.ZERO;
		BigDecimal excise = BigDecimal.ZERO;
		BigDecimal bedCessAmount = BigDecimal.ZERO;
		BigDecimal bedSecCessAmount = BigDecimal.ZERO;
		
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
		if (paramMap.containsKey("bedCess" + thisSuffix)) {
			bedCessStr = (String) paramMap.get("bedCess" + thisSuffix);
		}
		if (paramMap.containsKey("bedSecCess" + thisSuffix)) {
			bedSecCessStr = (String) paramMap.get("bedSecCess" + thisSuffix);
		}
		
		if (paramMap.containsKey("CST" + thisSuffix)) {
			cstStr = (String) paramMap.get("CST" + thisSuffix);
		}
		//percenatge of TAXes
		
		String VatPercentStr=null;
		String ExcisePercentStr=null;
		String bedCessPercentStr=null;
		String bedSecCessPercentStr=null;
		String CSTPercentStr=null;
		
		BigDecimal vatPercent=BigDecimal.ZERO;
		BigDecimal excisePercent=BigDecimal.ZERO;
		BigDecimal bedCessPercent=BigDecimal.ZERO;
		BigDecimal bedSecCessPercent=BigDecimal.ZERO;
		BigDecimal cstPercent=BigDecimal.ZERO; 
		
		if (paramMap.containsKey("VatPercent" + thisSuffix)) {
			VatPercentStr = (String) paramMap.get("VatPercent" + thisSuffix);
		}
		if (paramMap.containsKey("ExcisePercent" + thisSuffix)) {
			ExcisePercentStr = (String) paramMap.get("ExcisePercent" + thisSuffix);
		}
		if (paramMap.containsKey("bedCessPercent" + thisSuffix)) {
			bedCessPercentStr = (String) paramMap.get("bedCessPercent" + thisSuffix);
		}
		if (paramMap.containsKey("bedSecCessPercent" + thisSuffix)) {
			bedSecCessPercentStr = (String) paramMap.get("bedSecCessPercent" + thisSuffix);
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
			if (!exciseStr.equals("")) {
			excise = new BigDecimal(exciseStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing excise string: " + exciseStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing excise string: " + exciseStr);
			return "error";
		}
		try {
			if (!bedCessStr.equals("")) {
				bedCessAmount = new BigDecimal(bedCessStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing bedCess string: " + bedCessStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bedCess string: " + bedCessStr);
			return "error";
		}
		try {
			if (!bedSecCessStr.equals("")) {
				bedSecCessAmount = new BigDecimal(bedSecCessStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing bedSecCess string: " + bedSecCessStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bedSecCess string: " + bedSecCessStr);
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
			if (!ExcisePercentStr.equals("")) {
				excisePercent = new BigDecimal(ExcisePercentStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing excisePercent string: " + ExcisePercentStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing excisePercent string: " + ExcisePercentStr);
			return "error";
		}
		try {
			if (!bedCessPercentStr.equals("")) {
				bedCessPercent = new BigDecimal(bedCessPercentStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing bedCessPercent string: " + bedCessPercentStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bedCessPercent string: " + bedCessPercentStr);
			return "error";
		}
		try {
			if (!bedSecCessPercentStr.equals("")) {
				bedSecCessPercent = new BigDecimal(bedSecCessPercentStr);
			}
		} catch (Exception e) {
			Debug.logError(e, "Problems parsing bedSecCessPercent string: " + bedSecCessPercentStr, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing bedSecCessPercent string: " + bedSecCessPercentStr);
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
		
		productQtyMap.put("productId", productId);
		productQtyMap.put("quantity", quantity);
		productQtyMap.put("unitPrice", uPrice);
		/*productQtyMap.put("vatPercentage", vat);
		productQtyMap.put("cstPercentage", cst);
		productQtyMap.put("excisePercentage", excise);*/
		productQtyMap.put("vatAmount", vat);
		productQtyMap.put("bedAmount", excise);
		productQtyMap.put("bedCessAmount",bedCessAmount );
		productQtyMap.put("bedSecCessAmount", bedSecCessAmount);
		productQtyMap.put("cstAmount", cst);
		//productQtyMap.put("batchNo", batchNo);
		productQtyMap.put("vatPercent", vatPercent);
		productQtyMap.put("excisePercent", excisePercent);
		productQtyMap.put("bedCessPercent",bedCessPercent );
		productQtyMap.put("bedSecCessPercent", bedSecCessPercent);
		productQtyMap.put("cstPercent", cstPercent);
	
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
	processOrderContext.put("insurence", insurence);
	processOrderContext.put("mrnNumber", mrnNumber);
	processOrderContext.put("PONumber", PONumber);
	processOrderContext.put("SInvNumber", SInvNumber);
	processOrderContext.put("SInvoiceDate", SInvoiceDate);
	

	result = createPurchaseOrder(dctx, processOrderContext);
	if(ServiceUtil.isError(result)){
		Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
		request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
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
  	boolean beganTransaction = false;
  	//fright Charges
  	BigDecimal freightCharges = (BigDecimal) context.get("freightCharges");
  	BigDecimal discount = (BigDecimal) context.get("discount");
	/*BigDecimal freightCharges = BigDecimal.ZERO;
	BigDecimal discount = BigDecimal.ZERO;*/
  	String mrnNumber = (String) context.get("mrnNumber");
  	String PONumber=(String) context.get("PONumber");
  	String SInvNumber = (String) context.get("SInvNumber");
	BigDecimal insurence = (BigDecimal) context.get("insurence");

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
	BigDecimal bedCessAmount = BigDecimal.ZERO;
	BigDecimal bedSecCessAmount = BigDecimal.ZERO;

	List<GenericValue> prodPriceTypeList = FastList.newInstance();
	try{
	beganTransaction = TransactionUtil.begin(7200);
	
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
			if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
				excise = (BigDecimal)prodQtyMap.get("bedAmount");
		        BigDecimal excisePercent=(BigDecimal)prodQtyMap.get("excisePercent");
				
				BigDecimal taxRate = excise;
				BigDecimal taxAmount = BigDecimal.ZERO;
				
	        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
	        		//taxAmount = (unitPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	        		taxAmount = (taxRate).setScale(salestaxCalcDecimals, salestaxRounding);
	        		Map taxDetailMap = FastMap.newInstance();
		    		taxDetailMap.put("taxType", "BED_PUR");
		    		//taxDetailMap.put("taxType", "BED_SALE");
		    		taxDetailMap.put("amount", taxAmount);
		    		taxDetailMap.put("percentage", excisePercent);
		    		taxList.add(taxDetailMap);

		    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
		    		newProdPriceType.set("fromDate", effectiveDate);
		    		newProdPriceType.set("parentTypeId", "TAX");
		    		newProdPriceType.set("productId", productId);
		    		newProdPriceType.set("productStoreGroupId", "_NA_");
		    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
		    		newProdPriceType.set("productPriceTypeId", "BED_PUR");
		    		newProdPriceType.set("taxPercentage", excisePercent);
		    		newProdPriceType.set("taxAmount", taxAmount);
		    		newProdPriceType.set("currencyUomId", "INR");
		    		prodPriceTypeList.add(newProdPriceType);
	        	}
	        	tempPrice=tempPrice.add(taxAmount);
	        	totalTaxAmt=totalTaxAmt.add(taxAmount);
			}
			/*productQtyMap.put("vatPercent", vatPercent);
			productQtyMap.put("excisePercent", excisePercent);
			productQtyMap.put("bedCessPercent",bedCessPercent );
			productQtyMap.put("bedSecCessPercent", bedSecCessPercent);
			productQtyMap.put("cstPercent", cstPercent);*/
			
			if(UtilValidate.isNotEmpty(prodQtyMap.get("bedCessAmount"))){
			    BigDecimal taxRate = (BigDecimal)prodQtyMap.get("bedCessAmount");
			    BigDecimal bedCessPercent=(BigDecimal)prodQtyMap.get("bedCessPercent");
				BigDecimal taxAmount = BigDecimal.ZERO;
	        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
	        		//taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	        		taxAmount=taxRate.setScale(salestaxCalcDecimals, salestaxRounding);
	        		Map taxDetailMap = FastMap.newInstance();
		    		taxDetailMap.put("taxType", "BEDCESS_PUR");
		    		taxDetailMap.put("amount", taxAmount);
		    		taxDetailMap.put("percentage", bedCessPercent);
		    		taxList.add(taxDetailMap);
		    		
		    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
		    		newProdPriceType.set("fromDate", effectiveDate);
		    		newProdPriceType.set("parentTypeId", "TAX");
		    		newProdPriceType.set("productId", productId);
		    		newProdPriceType.set("productStoreGroupId", "_NA_");
		    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
		    		newProdPriceType.set("productPriceTypeId", "BEDCESS_PUR");
		    		newProdPriceType.set("taxPercentage", bedCessPercent);
		    		newProdPriceType.set("taxAmount", taxAmount);
		    		newProdPriceType.set("currencyUomId", "INR");
		    		prodPriceTypeList.add(newProdPriceType);
	        	}
	        	totalTaxAmt=totalTaxAmt.add(taxAmount);
			}
			
		    if(UtilValidate.isNotEmpty(prodQtyMap.get("bedSecCessAmount"))){
			    BigDecimal taxRate = (BigDecimal)prodQtyMap.get("bedSecCessAmount");
			    BigDecimal bedSecCessPercent=(BigDecimal)prodQtyMap.get("bedSecCessPercent");
				BigDecimal taxAmount = BigDecimal.ZERO;
	        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
	        		//taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	        		taxAmount=taxRate.setScale(salestaxCalcDecimals, salestaxRounding);
	        		Map taxDetailMap = FastMap.newInstance();
		    		taxDetailMap.put("taxType", "BEDSECCESS_PUR");
		    		taxDetailMap.put("amount", taxAmount);
		    		taxDetailMap.put("percentage", bedSecCessPercent);
		    		taxList.add(taxDetailMap);
		        	
		    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
		    		newProdPriceType.set("fromDate", effectiveDate);
		    		newProdPriceType.set("parentTypeId", "TAX");
		    		newProdPriceType.set("productId", productId);
		    		newProdPriceType.set("productStoreGroupId", "_NA_");
		    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
		    		newProdPriceType.set("productPriceTypeId", "BEDSECCESS_PUR");
		    		newProdPriceType.set("taxPercentage", bedSecCessPercent);
		    		newProdPriceType.set("taxAmount", taxAmount);
		    		newProdPriceType.set("currencyUomId", "INR");
		    		prodPriceTypeList.add(newProdPriceType);
	        	}
	        	totalTaxAmt=totalTaxAmt.add(taxAmount);
			}
		    if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
			    BigDecimal taxRate = (BigDecimal)prodQtyMap.get("vatAmount");
			    BigDecimal vatPercent=(BigDecimal)prodQtyMap.get("vatPercent");
				BigDecimal taxAmount = BigDecimal.ZERO;
	        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
	        		//taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	        		taxAmount=taxRate.setScale(salestaxCalcDecimals, salestaxRounding);
	        		Map taxDetailMap = FastMap.newInstance();
		    		//taxDetailMap.put("taxType", "VAT_SALE");
		    		taxDetailMap.put("taxType", "VAT_PUR");
		    		taxDetailMap.put("amount", taxAmount);
		    		taxDetailMap.put("percentage", vatPercent);
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
		    		newProdPriceType.set("taxPercentage", vatPercent);
		    		newProdPriceType.set("taxAmount", taxAmount);
		    		newProdPriceType.set("currencyUomId", "INR");
		    		prodPriceTypeList.add(newProdPriceType);
	        	}
	        	totalTaxAmt=totalTaxAmt.add(taxAmount);
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
				cst = (BigDecimal)prodQtyMap.get("cstAmount");
				BigDecimal cstPercent=(BigDecimal)prodQtyMap.get("cstPercent");
				BigDecimal taxRate = cst;
				BigDecimal taxAmount = BigDecimal.ZERO;
	        	if(taxRate.compareTo(BigDecimal.ZERO)>0){
	        		//taxAmount = (tempPrice.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	        		taxAmount=taxRate.setScale(salestaxCalcDecimals, salestaxRounding);
	        		Map taxDetailMap = FastMap.newInstance();
		    		//taxDetailMap.put("taxType", "CST_SALE");
		    		taxDetailMap.put("taxType", "CST_PUR");
		    		taxDetailMap.put("amount", taxAmount);
		    		taxDetailMap.put("percentage", cstPercent);
		    		taxList.add(taxDetailMap);
		    		GenericValue newProdPriceType = delegator.makeValue("ProductPriceAndType");        	 
		    		newProdPriceType.set("fromDate", effectiveDate);
		    		newProdPriceType.set("parentTypeId", "TAX");
		    		newProdPriceType.set("productId", productId);
		    		newProdPriceType.set("productStoreGroupId", "_NA_");
		    		newProdPriceType.set("productPricePurposeId", "PURCHASE");
		    		newProdPriceType.set("productPriceTypeId", "CST_PUR");
		    		newProdPriceType.set("taxPercentage", cstPercent);
		    		newProdPriceType.set("taxAmount", taxAmount);
		    		newProdPriceType.set("currencyUomId", "INR");
		    		prodPriceTypeList.add(newProdPriceType);
	        	}
	        	totalTaxAmt=totalTaxAmt.add(taxAmount);
			}
		}
	
		//BigDecimal totalPrice = unitPrice.add(totalTaxAmt);
		
		BigDecimal totalPrice = unitPrice;//as of now For PurchaseOrder listPrice is same like unitPrice
		
		//BigDecimal totalTaxAmt = BigDecimal.ZERO;
		//Debug.log("==totalPrice==="+totalPrice+"==totalTaxAmt="+totalTaxAmt+"=unitPrice="+unitPrice);
		//List taxList = (List)priceResult.get("taxList");
		//Debug.log("=========taxList====="+taxList);
		//Debug.log("==prodPriceTypeList=====>"+prodPriceTypeList);
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
		//checkout.calcAndAddTax(prodPriceTypeList);
		checkout.calcAndAddTaxPurchase(prodPriceTypeList);
	} catch (Exception e1) {
	// TODO Auto-generated catch block
		Debug.logError(e1, "Error in CalcAndAddTax",module);
		return ServiceUtil.returnError(" Error While Creating Adjustment for Purchase Order !");
	}
	
	Map<String, Object> orderCreateResult=checkout.createOrder(userLogin);
	Debug.log("===orderCreateResult=====>"+orderCreateResult);
	if (ServiceUtil.isError(orderCreateResult)) {
		String errMsg =  ServiceUtil.getErrorMessage(orderCreateResult);
		Debug.logError(errMsg, "While Creating Order",module);
		return ServiceUtil.returnError(" Error While Creating Order !"+errMsg);
	}
		
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
		if(insurence.compareTo(BigDecimal.ZERO)>0){
	    	Map adjustCtx = UtilMisc.toMap("userLogin",userLogin);	  	
	    	adjustCtx.put("orderId", orderId);
	    	adjustCtx.put("adjustmentTypeId", "INSURENCE");
	    	adjustCtx.put("adjustmentAmount", insurence);
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
			//supplier invoice date
			try{
				GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
				orderAttribute.set("orderId", orderId);
				orderAttribute.set("attrName", "SUP_INV_DATE");
				orderAttribute.set("attrValue", UtilDateTime.toDateString((Timestamp)context.get("SInvoiceDate"),null));
				delegator.createOrStore(orderAttribute);
				}catch (Exception e) {
					  Debug.logError(e, "Error While Creating Attribute(SUP_INV_DATE)  for Purchase Order ", module);
					  return ServiceUtil.returnError("Error While Creating Attribute(SUP_INV_DATE)  for Purchase Order : "+orderId);
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
    try{   
    	Map roundAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
    	roundAdjCtx.put("orderId", orderId);
  	 	result = dispatcher.runSync("adjustRoundingDiffForOrder",roundAdjCtx);  		  		 
  	 	if (ServiceUtil.isError(result)) {
  	 		String errMsg =  ServiceUtil.getErrorMessage(result);
  	 		Debug.logError(errMsg , module);
	      	  	return ServiceUtil.returnError(errMsg+"==Error While  Rounding Order !");
	 		}
	 	}catch (Exception e) {
	 		Debug.logError(e, "Error while Creating Order", module);
        return ServiceUtil.returnError(e+"==Error While  Rounding Order !");
  		//return resultMap;			  
  	}
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
	            //raising DebitNote Here for UNITS PURCHASES  
	            
	            if("INTER_PRCHSE_CHANNEL".equals(salesChannel)){
		    		  Map paymentInputMap = FastMap.newInstance();
			  		  paymentInputMap.put("userLogin", userLogin);
			  		  paymentInputMap.put("paymentTypeId", "EXPENSE_PAYOUT");
			  		 // paymentInputMap.put("paymentType", "SALES_PAYIN");
			  		  paymentInputMap.put("paymentMethodTypeId", "DEBITNOTE_TRNSF");
			  		  paymentInputMap.put("paymentPurposeType","INTER_PRCHSE_CHANNEL");
			  		  paymentInputMap.put("statusId", "PMNT_NOT_PAID");
			  		  paymentInputMap.put("invoiceIds",UtilMisc.toList((String)result.get("invoiceId")));
			  		  Map paymentResult = dispatcher.runSync("createCreditNoteOrDebitNoteForInvoice", paymentInputMap);
			  		  if(ServiceUtil.isError(paymentResult)){
		    			     Debug.logError(paymentResult.toString(), module);
		    			     return ServiceUtil.returnError("There was an error in service createCreditNoteOrDebitNoteForInvoice" + ServiceUtil.getErrorMessage(paymentResult));  
			  		  }
			  		  List paymentIds = (List)paymentResult.get("paymentsList");
  	                  Debug.log("+++++++===paymentIds===AfterDEbitNote=="+paymentIds);
		        }else{
	     			Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", (String)result.get("invoiceId"),"userLogin", userLogin));
	     			if (ServiceUtil.isError(resultPaymentApp)) {						  
	     				Debug.logError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp), module);			             
	     	            return ServiceUtil.returnError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp));  
	     		    }
		        }
    		}//end of advance payment appl   
        }catch (Exception e) {
        	Debug.logError(e, module);
            return ServiceUtil.returnError(e.toString()); 
        }
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
		if("INSURENCE".equalsIgnoreCase(adjustmentTypeId)){
			String orderAdjustmentTypeId = "COGS_ITEM18";
			 Map createOrderAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
	    	 createOrderAdjustmentCtx.put("orderId", orderId);
	    	 createOrderAdjustmentCtx.put("orderAdjustmentTypeId", orderAdjustmentTypeId);    	
	    	 createOrderAdjustmentCtx.put("amount", adjustmentAmount);//minus discount value from total order
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

public static Map<String, Object> cancelPurchaseOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
	Delegator delegator = dctx.getDelegator();
    LocalDispatcher dispatcher = dctx.getDispatcher();       
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    Map<String, Object> result = new HashMap<String, Object>();
    String orderId = (String) context.get("orderId");
    String statusId = (String) context.get("statusId");
    String partyId = (String) context.get("partyId");
    String salesChannelEnumId=null;
    if(UtilValidate.isEmpty(orderId)){
		return ServiceUtil.returnError("orderId is null" + orderId);
    }
    else{
    	List<GenericValue> orderItems = null;
  		try{
  		  List conditionList = UtilMisc.toList(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
		  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
  			orderItems = delegator.findList("OrderHeader",condition, null, null, null, false);
 		} catch (GenericEntityException e) {
 			Debug.logError(e, module);
    		return ServiceUtil.returnError("Error in fetching Order :" + orderId);
	    }
 		if(UtilValidate.isEmpty(orderItems)){
 			Debug.logError("Error ", module);
    		return ServiceUtil.returnError("No Order Items available to Cacnel Order:" + orderId+" for SupplierId:"+partyId);
 		}else{ 	
 			for (GenericValue orderItem : orderItems) {
 				orderId=orderItem.getString("orderId");
 				salesChannelEnumId=orderItem.getString("salesChannelEnumId");
 				boolean cancelled = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);//order Cancelation helper
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
 		 			 if("INTER_PRCHSE_CHANNEL".equals(salesChannelEnumId)){
 		 				 try{
 		 				List<GenericValue> paymentAppList = delegator.findList("PaymentApplication", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId), null, null, null, false);
	 		 				if(!UtilValidate.isEmpty(paymentAppList)){
	 		 				String paymentId=paymentAppList.get(0).getString("paymentId");
	 		 				Debug.log("+++++++===paymentId==in =InterUnitPurchases=="+paymentId);
	 		 				Map<String, Object> removePaymentApplResult = dispatcher.runSync("voidPayment", UtilMisc.toMap("userLogin" ,userLogin ,"paymentId" ,paymentId));
	 						 if (ServiceUtil.isError(removePaymentApplResult)) {
	 				            	Debug.logError(removePaymentApplResult.toString(), module);    			
	 				                return ServiceUtil.returnError(null, null, null, removePaymentApplResult);
	 				            }
	 		 				}
 		 				 }catch (Exception e) {
 		 		 			Debug.logError(e, module);
 		 		    		return ServiceUtil.returnError("Error in fetching PaymentId for invoiceId: "+invoiceId);
 		 			    }
 		 			 }
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
 			 }
 		}
	}
    return ServiceUtil.returnSuccess("Order Canceled Successfully for Party:" +partyId+" !");

}

}
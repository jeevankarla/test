package in.vasista.vbiz.depotsales;

import in.vasista.vbiz.depotsales.DepotPurchaseServices;
import in.vasista.vbiz.depotsales.DepotHelperServices;
import in.vasista.vbiz.depotsales.DepotSalesServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import org.ofbiz.order.order.OrderServices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.party.PartyHelper;

public class SupplierApiServices {
	
	public static final String module = SupplierApiServices.class.getName();
	private static int decimals;
	private static int rounding;
    public static final String resource = "AccountingUiLabels";
    
    public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
	public static final BigDecimal ONE_BASE = BigDecimal.ONE;
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
    public static int purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
	public static int purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
	public static int purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
    
    
    
    static {
        decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        //if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
    
    public static Map<String, Object> getSupplierDetails(DispatchContext dctx,Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		
		String partyId = (String) context.get("partyId");
		GenericValue partyDetail = null;
		Map resultMap = FastMap.newInstance();
		try{
			partyDetail = delegator.findOne("PartyRoleDetailAndPartyDetail", UtilMisc.toMap("partyId", partyId,"roleTypeId","SUPPLIER"), false);
			if(UtilValidate.isEmpty(partyDetail)){
				Debug.logError("Not a valid party", module);
				return ServiceUtil.returnError("Not a valid party");
			}
			resultMap.put("partyId",partyDetail.get("partyId"));
			resultMap.put("partyName",partyDetail.get("groupName"));
			
			Map inputMap = FastMap.newInstance();
			Map addressMap = FastMap.newInstance();
			inputMap.put("partyId", partyDetail.get("partyId"));
			inputMap.put("userLogin", userLogin);
			try{
				addressMap  = dispatcher.runSync("getPartyPostalAddress", inputMap);
			} catch(Exception e){
				Debug.logError("Not a valid party", module);
			}
			String contactNumber = "";
			String email = "";
			try{
				Map<String, Object> getTelParams = FastMap.newInstance();
	        	getTelParams.put("partyId", partyDetail.get("partyId"));
				Map<String, Object> serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	            	 Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	            } 
	            if(UtilValidate.isNotEmpty(serviceResult.get("contactNumber"))){
	            	contactNumber = (String) serviceResult.get("contactNumber");
	            	/*if(!UtilValidate.isEmpty(serviceResult.get("countryCode"))){
	            		contactNumber = (String) serviceResult.get("countryCode") +" "+ (String) serviceResult.get("contactNumber");
	            	}*/
	            }
			}
            catch (Exception e) {
				Debug.logError(e, "Error fetching contact number from getPartyTelephone service", module);
			}
			try{
		        Map<String, Object> partyEmail = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", partyDetail.get("partyId"), "userLogin", userLogin));
		        if (UtilValidate.isNotEmpty(partyEmail.get("emailAddress"))) {
		            email = (String) partyEmail.get("emailAddress");
		        }
			}catch (GenericServiceException e) {
                Debug.logError(e, "Problem while getting email address", module);
            }
			
			resultMap.put("addressMap",addressMap);
			resultMap.put("contactNumber",contactNumber);
			resultMap.put("email",email);
			
			String cstNum = "";
			String tanNum = "";
			String panNum = "";
			
			List condList = FastList.newInstance();
			List<GenericValue> PartyIdentification = null;
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyDetail.get("partyId")));
			condList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.IN, UtilMisc.toList("CST_NUMBER","PAN_NUMBER","TAN_NUMBER")));
	    	try{
	    		PartyIdentification = delegator.findList("PartyIdentification", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	    		if(UtilValidate.isNotEmpty(PartyIdentification)){
	    			List cstList = EntityUtil.filterByCondition(PartyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CST_NUMBER"));
	    			if(UtilValidate.isNotEmpty(cstList)){
	    				cstNum = (String) (EntityUtil.getFirst(cstList)).get("idValue");
	    			}
	    			List tanList = EntityUtil.filterByCondition(PartyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TAN_NUMBER"));
	    			if(UtilValidate.isNotEmpty(tanList)){
	    				tanNum = (String) (EntityUtil.getFirst(tanList)).get("idValue");
	    			}
	    			List panList = EntityUtil.filterByCondition(PartyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PAN_NUMBER"));
	    			if(UtilValidate.isNotEmpty(panList)){
	    				panNum = (String) (EntityUtil.getFirst(panList)).get("idValue");
	    			}
	    		}
	    	} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
	    	resultMap.put("cstNum",cstNum);
	    	resultMap.put("tanNum",tanNum);
	    	resultMap.put("panNum",panNum);
			
			result.put("supplierDetails",resultMap);
		}catch(GenericEntityException e){
			Debug.logError("Not a valid party", module);
			return ServiceUtil.returnError("Not a valid party");
		}
		
		return result;
    }
    
    public static Map<String, Object> getSupplierPO(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map result = ServiceUtil.returnSuccess();
        
        String partyId = (String)context.get("partyId");
        //OrderHeaderAndSequenceAndRoles
        
        List conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        //conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
        conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
        conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
        EntityListIterator orderHeaderList = null;
        try{
	        orderHeaderList = delegator.find("OrderHeaderAndSequenceAndRoles", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, null);
	    } catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
        GenericValue eachHeader;
        Map ordersMap = FastMap.newInstance();
        while( orderHeaderList != null && (eachHeader = orderHeaderList.next()) != null) {
        	String eachOrderId = (String) eachHeader.get("orderId");
        	Map tempData = FastMap.newInstance();
        	tempData.put("orderDate", String.valueOf(eachHeader.get("orderDate")).substring(0,10));
        	tempData.put("orderId",eachOrderId);
        	tempData.put("orderNo",eachHeader.get("orderNo"));
        	tempData.put("statusId",eachHeader.get("statusId"));
        	List<GenericValue> shipReceiptList = null;
        	try{
        		shipReceiptList = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
        	
        	
        	List orderItems = FastList.newInstance();
        	List itemCondList = FastList.newInstance();
		    itemCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
		    itemCondList.add(EntityCondition.makeCondition("orderItemTypeId", EntityOperator.EQUALS, "PRODUCT_ORDER_ITEM"));
		    try{
		    	List<GenericValue> orderItemsList = delegator.findList("OrderItem", EntityCondition.makeCondition(itemCondList, EntityOperator.AND), null, null, null, false);
		    	if(UtilValidate.isNotEmpty(orderItemsList)){
					for(GenericValue eachItem:orderItemsList){
						String productId = eachItem.getString("productId");
						String itemName = eachItem.getString("itemDescription");
						String spec = "";
						try{
							GenericValue orderItemAttribute = delegator.findOne("OrderItemAttribute", UtilMisc.toMap("orderId", eachOrderId,"orderItemSeqId",eachItem.get("orderItemSeqId"),"attrName","remarks"), false);
							if(UtilValidate.isNotEmpty(orderItemAttribute)){
								spec = orderItemAttribute.getString("attrValue");
							}
						}catch(GenericEntityException e){
							Debug.logError(e, module);
						}
						BigDecimal unitPrice = eachItem.getBigDecimal("unitPrice");
						BigDecimal quantity = eachItem.getBigDecimal("quantity");
						BigDecimal shippedQuantity = BigDecimal.ZERO;
						List shipCondList = FastList.newInstance();
						shipCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
						shipCondList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.get("productId")));
						shipCondList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.get("orderItemSeqId")));
						//List<GenericValue> shipReceiptList = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(shipCondList, EntityOperator.AND), null, null, null, false);
						List<GenericValue> shipList = EntityUtil.filterByCondition(shipReceiptList, EntityCondition.makeCondition(shipCondList, EntityOperator.AND));
						for(GenericValue eachShip:shipList){
							shippedQuantity = shippedQuantity.add(eachShip.getBigDecimal("quantityAccepted"));
						}
						
						BigDecimal balQuantity = quantity.subtract(shippedQuantity);
						Map itemMap = FastMap.newInstance();
						itemMap.put("productId",productId);
						itemMap.put("itemName",itemName);
						itemMap.put("specification",spec);
						itemMap.put("unitPrice",unitPrice.setScale(decimals, rounding));
						itemMap.put("quantity",quantity.setScale(decimals, rounding));
						itemMap.put("shippedQuantity",shippedQuantity.setScale(decimals, rounding));
						itemMap.put("balQuantity",balQuantity.setScale(decimals, rounding));
						orderItems.add(itemMap);
					}
				}
		    } catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
        	tempData.put("orderItemsList", orderItems);
        	
        	Map shipmentHistory = FastMap.newInstance();
			List<String> shipmentIdList = (List) EntityUtil.getFieldListFromEntityList(shipReceiptList, "shipmentId", true);
			for(String shipmentId:shipmentIdList){
				List custCondList = FastList.newInstance();
				custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
				custCondList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				List<GenericValue> shipmentDetails = EntityUtil.filterByCondition(shipReceiptList, EntityCondition.makeCondition(custCondList, EntityOperator.AND));
				
				List shipmentDetailList = FastList.newInstance();
				for(GenericValue eachDetail:shipmentDetails){
					Map detailMap = FastMap.newInstance();
					GenericValue orderItemDetail = null;
					try{
						orderItemDetail = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", eachOrderId,"orderItemSeqId",eachDetail.get("orderItemSeqId")), false);
					}catch(GenericEntityException e){
						Debug.logError(e, module);
					}
					BigDecimal quantity = eachDetail.getBigDecimal("quantityAccepted");
					BigDecimal unitPrice = orderItemDetail.getBigDecimal("unitPrice");
					
					detailMap.put("itemName",orderItemDetail.get("itemDescription"));
					detailMap.put("productId",eachDetail.get("productId"));
					detailMap.put("orderItemSeqId",eachDetail.get("orderItemSeqId"));
					detailMap.put("quantity",quantity.setScale(decimals, rounding));
					detailMap.put("unitPrice",unitPrice.setScale(decimals, rounding));
					
					GenericValue Shipment = null;
					try{
						Shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
					}catch(GenericEntityException e){
						Debug.logError(e, module);
					}
					
					List conditionlist = FastList.newInstance();
					conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
					conditionlist.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachDetail.get("orderItemSeqId")));
					conditionlist.add(EntityCondition.makeCondition("changeTypeEnumId", EntityOperator.EQUALS, "ODR_ITM_AMEND"));
					conditionlist.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO, Shipment.get("createdDate")));
					EntityCondition conditionMain1=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
					
					try{
						FastList<GenericValue> OrderItemChangeDetails = FastList.newInstance();
						OrderItemChangeDetails = (FastList)delegator.findList("OrderItemChange", conditionMain1 , null ,UtilMisc.toList("changeDatetime"), null, false );
						if(UtilValidate.isNotEmpty(OrderItemChangeDetails)){
							GenericValue lastItemChange = OrderItemChangeDetails.getLast();
							unitPrice = lastItemChange.getBigDecimal("unitPrice");
							detailMap.put("unitPrice",unitPrice.setScale(decimals, rounding));
						}
						
					}catch(GenericEntityException e){
						Debug.logError(e, module);
					}
					detailMap.put("itemAmount",(quantity.multiply(unitPrice)).setScale(decimals, rounding));
					shipmentDetailList.add(detailMap);
				}
				shipmentHistory.put(shipmentId,shipmentDetailList);
			}
			tempData.put("shipmentHistory",shipmentHistory);
        	ordersMap.put(eachOrderId,tempData);
        }
        result.put("supplierPOList",ordersMap);
        return result;
    }
    
    public static Map<String, Object> createSupplierDispatch(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map result = ServiceUtil.returnSuccess("Successfully made shipment");
        
	    String receiptDateStr = (String) context.get("receiptDate");
	    String orderId = (String) context.get("orderId");
	    String vehicleId = (String) context.get("vehicleId");
	    String lrNumber = (String) context.get("lrNumber");
	    String destination = (String) context.get("destination");
	    
	    String carrierName = (String) context.get("carrierName");
	    if(UtilValidate.isEmpty(carrierName)){
	    	carrierName = "_NA_";
	    }
	    String hideQCflow = (String) context.get("hideQCflow");
	    String supplierInvoiceId = (String) context.get("suppInvoiceId");
	    String supplierInvoiceDateStr = (String) context.get("suppInvoiceDate");
	    String withoutPO = (String) context.get("withoutPO");
	    //GRN on PO then override this supplier with PO supplier
	    String supplierId = (String) context.get("supplierId");
	    String deliveryChallanDateStr = (String) context.get("deliveryChallanDate");
	    String lrDateStr = (String) context.get("lrDate");
	    String deliveryChallanNo = (String) context.get("deliveryChallanNo");
	    String remarks = (String) context.get("remarks");
	    String freightCharges = (String) context.get("freightCharges");
	    
	    String allowedGraterthanTheOrdered = (String) context.get("allowedGraterthanTheOrdered");
	    
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		String shipmentId ="";
		String receiptId="";
		String purposeTypeId="";
		GenericValue shipmentReceipt=null;
		String smsContent="";
		if (UtilValidate.isEmpty(orderId) && UtilValidate.isEmpty(withoutPO)) {
			Debug.logError("Cannot process receipts without orderId: "+ orderId, module);
			return ServiceUtil.returnError("Cannot process receipts without orderId: "+ orderId);
		}
		Timestamp receiptDate = null;
		Timestamp supplierInvoiceDate = null;
		
		List<Map<String, Object>> shipmentItems = (List<Map<String, Object>>) context.get("shipmentItems");
		Debug.log("shipmentItems ==================="+shipmentItems);
		String infoString = "createSupplierDispatch:: shipmentItems: " + shipmentItems;
		Debug.logInfo(infoString, module);
		if (shipmentItems.isEmpty()) {
			Debug.logError("No shipmentItems items found; " + infoString, module);
			return ServiceUtil.returnError("No shipmentItems items found; "+ infoString);
		}
		
		SimpleDateFormat SimpleDF = new SimpleDateFormat("dd:mm:yyyy hh:mm");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
		receiptDate = UtilDateTime.nowTimestamp();
		Timestamp estimatedDate = UtilDateTime.addDaysToTimestamp(receiptDate,6);
		String estimatedDateStr=UtilDateTime.toDateString(estimatedDate,"dd-MM-yyyy");
        DateFormat givenFormatter = new SimpleDateFormat("dd:MM:yyyy hh:mm");
        DateFormat reqformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(UtilValidate.isNotEmpty(receiptDateStr)){
	        try {
	        Date givenReceiptDate = (Date)givenFormatter.parse(receiptDateStr);
	        receiptDate = new java.sql.Timestamp(givenReceiptDate.getTime());
	        }catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
		  	}
        }
        
	  	if(UtilValidate.isNotEmpty(supplierInvoiceDateStr)){
	  		try {
	  			SimpleDateFormat dateSdf = new SimpleDateFormat("dd MMMMM, yyyy");    
	  			supplierInvoiceDate = new java.sql.Timestamp(dateSdf.parse(supplierInvoiceDateStr).getTime());
	  			
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + supplierInvoiceDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + supplierInvoiceDateStr, module);
		  	}
	  	}
	  	Timestamp lrDateTimeStamp = null;
	  	if(UtilValidate.isNotEmpty(lrDateStr)){
	  		try {
	  			SimpleDateFormat dateSdf = new SimpleDateFormat("dd MMMMM, yyyy");    
	  			lrDateTimeStamp = new java.sql.Timestamp(dateSdf.parse(lrDateStr).getTime());
	  			
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + lrDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + lrDateStr, module);
		  	}
	  	}
	  	
	  	Timestamp deliveryChallanDate=null;
	  	if(UtilValidate.isNotEmpty(deliveryChallanDateStr)){
	  		try {
	  			SimpleDateFormat dateSdf = new SimpleDateFormat("dd MMMMM, yyyy");    
	  			deliveryChallanDate = new java.sql.Timestamp(dateSdf.parse(deliveryChallanDateStr).getTime());
		  	} catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + deliveryChallanDateStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + deliveryChallanDateStr, module);
		  	}
	  	}else{
		  	 deliveryChallanDate=UtilDateTime.nowTimestamp();
	  	}
	  	BigDecimal poValue = BigDecimal.ZERO;
	  	boolean beganTransaction = false;
		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			List conditionList = FastList.newInstance();
			List<GenericValue> extPOItems = FastList.newInstance();
			List<GenericValue> extReciptItems = FastList.newInstance();
			
			boolean directPO = Boolean.TRUE;
			String extPOId = "";
			List productList = FastList.newInstance();
			
			if((UtilDateTime.getIntervalInDays(UtilDateTime.getDayStart(receiptDate), UtilDateTime.getDayStart(nowTimeStamp))) != 0){
	    		Debug.logError("Check local system date", module);
				TransactionUtil.rollback();
		  		return ServiceUtil.returnError("Check local system date");
			}
			String originFacilityId ="";
			GenericValue orderHeader = null;
			if(UtilValidate.isNotEmpty(orderId)){
				orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				
				String statusId = orderHeader.getString("statusId");
				String orderTypeId = orderHeader.getString("orderTypeId");
				purposeTypeId = orderHeader.getString("purposeTypeId");
				if(UtilValidate.isNotEmpty(orderHeader.getString("originFacilityId")))
				  {
					originFacilityId = orderHeader.getString("originFacilityId");
				  }
				if(statusId.equals("ORDER_CANCELLED")){
					Debug.logError("Cannot create GRN for cancelled orders : "+orderId, module);
					TransactionUtil.rollback();
					return ServiceUtil.returnError("Cannot create GRN for cancelled orders : "+orderId);
				}
				poValue = orderHeader.getBigDecimal("grandTotal");
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, orderTypeId));
				List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				
				if(UtilValidate.isNotEmpty(orderAssoc)){
					extPOId = (EntityUtil.getFirst(orderAssoc)).getString("toOrderId");
					directPO = Boolean.FALSE;
				}
				if(UtilValidate.isNotEmpty(extPOId)){
					List<GenericValue> annualContractPOAsso = delegator.findList("OrderAssoc", EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, extPOId), UtilMisc.toSet("orderId"), null, null, false);
					List orderIds = EntityUtil.getFieldListFromEntityList(annualContractPOAsso, "orderId", true);
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
					conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, extPOId));
					extPOItems = delegator.findList("OrderItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
					
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
					conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
					extReciptItems = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				}
				
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> orderRole = delegator.findList("OrderRole", condition, null, null, null, false);
				
				if(UtilValidate.isEmpty(orderRole)){
					Debug.logError("No Vendor for the order : "+orderId, module);
					TransactionUtil.rollback();
					return ServiceUtil.returnError("No Vendor for the order : "+orderId);
				}
					
				supplierId = (EntityUtil.getFirst(orderRole)).getString("partyId");
				
			}
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			List changeExprList = FastList.newInstance();
			changeExprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			changeExprList.add(EntityCondition.makeCondition("effectiveDatetime", EntityOperator.LESS_THAN_EQUAL_TO, receiptDate));
			EntityCondition condExpr1 = EntityCondition.makeCondition(changeExprList, EntityOperator.AND);
			List<GenericValue> orderItemChanges = delegator.findList("OrderItemChange", condExpr1, null, UtilMisc.toList("-effectiveDatetime"), null, false);
			
			Timestamp effectiveDatetime = null;
			if(UtilValidate.isNotEmpty(orderItemChanges)){
				effectiveDatetime = (EntityUtil.getFirst(orderItemChanges)).getTimestamp("effectiveDatetime");
				orderItemChanges = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("effectiveDatetime", EntityOperator.EQUALS, effectiveDatetime));
			}

			Map orderItemSeq = FastMap.newInstance();
			for(GenericValue orderItemsValues : orderItems){
				String productId = orderItemsValues.getString("productId");
				String orderItemSeqId = orderItemsValues.getString("orderItemSeqId");
				orderItemSeq.put(productId, orderItemSeqId);
			}

			List orderAdjustmentTypes = EntityUtil.getFieldListFromEntityList(orderAdjustments, "orderAdjustmentTypeId", true);
			
			productList = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
			GenericValue newEntity = delegator.makeValue("Shipment");
	        newEntity.set("estimatedShipDate", lrDateTimeStamp);
	        if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("BRANCH_PURCHASE")){
		        newEntity.set("shipmentTypeId", "BRANCH_SHIPMENT");
	        }else{
	        	newEntity.set("shipmentTypeId", "DEPOT_SHIPMENT");
	        }
	        newEntity.set("statusId", "DISPATCHED");
	        newEntity.put("vehicleId",vehicleId);
	        newEntity.put("lrNumber",lrNumber);
	        newEntity.put("destination",destination);
	        newEntity.put("carrierName",carrierName);
	        newEntity.put("partyIdFrom",supplierId);
	        newEntity.put("supplierInvoiceId",supplierInvoiceId);
	        newEntity.put("supplierInvoiceDate",supplierInvoiceDate);
	        newEntity.put("estimatedReadyDate",lrDateTimeStamp);
	        newEntity.put("deliveryChallanNumber",deliveryChallanNo);
	        newEntity.put("description",remarks);
	        newEntity.put("deliveryChallanDate",deliveryChallanDate);
	        newEntity.put("primaryOrderId",orderId);
	        if(UtilValidate.isNotEmpty(freightCharges))
            newEntity.set("estimatedShipCost", new BigDecimal(freightCharges));
	        newEntity.set("createdDate", nowTimeStamp);
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            delegator.createSetNextSeqId(newEntity);            
            shipmentId = (String) newEntity.get("shipmentId");
	       
			/*List<Map> prodQtyList = FastList.newInstance();*/
			
            BigDecimal landingCharges = BigDecimal.ZERO;
			for (int i = 0; i < shipmentItems.size(); i++) {
				
				Map shipmentItem = shipmentItems.get(i);
				
				String productId = "";
		        String quantityStr = "";
		        String deliveryChallanQtyStr = "";
		        String dispatchedQty = "";
		        String orderItemSeqId = "";
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal deliveryChallanQty = BigDecimal.ZERO;
				BigDecimal oldRecvdQty = BigDecimal.ZERO;
				Map productQtyMap = FastMap.newInstance();
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (UtilValidate.isNotEmpty( shipmentItem.get("quantity"))) {
					if (UtilValidate.isNotEmpty( shipmentItem.get("productId"))) {
						productId = (String) shipmentItem.get("productId");
					}
					else {
						return ServiceUtil.returnError("Missing product id");			  
					}
					if (productId.equals("") || UtilValidate.isEmpty(productId)) {
						return ServiceUtil.returnError("Missing product id");
					}
	
					if (UtilValidate.isNotEmpty( shipmentItem.get("quantity"))) {
						quantityStr = (String) shipmentItem.get("quantity");
					}
					else {
						return ServiceUtil.returnError("Missing product quantity");
					}	
					if (UtilValidate.isNotEmpty( shipmentItem.get("orderItemSeqId"))) {
						orderItemSeqId = (String) shipmentItem.get("orderItemSeqId");
					}
					if(UtilValidate.isNotEmpty(quantityStr)){
						quantity = new BigDecimal(quantityStr);
					}
					//DC qty here
					if (UtilValidate.isNotEmpty( shipmentItem.get("deliveryChallanQty"))) {
						deliveryChallanQtyStr = (String) shipmentItem.get("deliveryChallanQty");
					}
					if(UtilValidate.isNotEmpty(deliveryChallanQtyStr)){
						deliveryChallanQty = new BigDecimal(deliveryChallanQtyStr);
					}else{
						deliveryChallanQty = quantity;
					}
					//old recived qty oldRecvdQty
					if (UtilValidate.isNotEmpty( shipmentItem.get("dispatchedQty"))) {
						dispatchedQty = (String) shipmentItem.get("dispatchedQty");
					}
					if(UtilValidate.isNotEmpty(dispatchedQty)){
						oldRecvdQty = new BigDecimal(dispatchedQty);
	
					}
					if(UtilValidate.isEmpty(withoutPO)){
						if(directPO){
							GenericValue checkOrderItem = null;
							GenericValue ordChangesApo = null;
							BigDecimal orderChangeQty=BigDecimal.ZERO;
							BigDecimal orderQty=BigDecimal.ZERO;
							String orderSeqNo="";
							List<GenericValue> ordItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
							if(UtilValidate.isNotEmpty(orderItemSeqId)){
								orderSeqNo=(String)orderItemSeq.get(productId);
								List<GenericValue> ordersequenceChangeList = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
					 			if(UtilValidate.isNotEmpty(ordersequenceChangeList)){
					 				orderChangeQty = (EntityUtil.getFirst(ordersequenceChangeList)).getBigDecimal("quantity");
	
					 			}	
							}
							if(UtilValidate.isNotEmpty(ordItems)){
								checkOrderItem = EntityUtil.getFirst(ordItems);
	
							}
							
							if(UtilValidate.isNotEmpty(checkOrderItem)){
								     orderQty = checkOrderItem.getBigDecimal("quantity");
							    if(orderChangeQty.compareTo(BigDecimal.ZERO)>0 ){
									 orderQty = orderChangeQty;
							    }
								BigDecimal checkQty = (orderQty.multiply(new BigDecimal(1.1))).setScale(0, BigDecimal.ROUND_CEILING);
								BigDecimal maxQty=oldRecvdQty.add(quantity);
								Debug.log("=orderQty=="+orderQty+"==checkQty="+checkQty+"==maxQty=="+maxQty+"==quantity="+quantity);
								//if(quantity.compareTo(checkQty)>0){
								if(UtilValidate.isEmpty(allowedGraterthanTheOrdered) || (UtilValidate.isNotEmpty(allowedGraterthanTheOrdered) && "Y".equals(allowedGraterthanTheOrdered))){								
									if(maxQty.compareTo(checkQty)>0){	
										Debug.logError("Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId, module);
										
										TransactionUtil.rollback();
										return ServiceUtil.returnError("Quantity cannot be more than 10%("+checkQty+") for PO : "+orderId);	
									}
								}
							}
						}
						else{
							List<GenericValue> poItems = EntityUtil.filterByCondition(extPOItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
							List<GenericValue> receiptItems = EntityUtil.filterByCondition(extReciptItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
							BigDecimal poQty = BigDecimal.ZERO;
							if(UtilValidate.isNotEmpty(poItems)){
								GenericValue poItem = EntityUtil.getFirst(poItems);
								poQty = poItem.getBigDecimal("quantity");
								
							}
							BigDecimal receiptQty = BigDecimal.ZERO;
							for(GenericValue item : receiptItems){
								receiptQty = receiptQty.add(item.getBigDecimal("quantityAccepted"));
							}
							BigDecimal checkQty = poQty.subtract(receiptQty);
							
							if(quantity.compareTo(checkQty)>0){
								Debug.logError("Quantity cannot be more than ARC/CPC for PO : "+orderId, module);
								TransactionUtil.rollback();
								return ServiceUtil.returnError("Quantity cannot be more than ARC/CPC for PO : "+orderId);
							}
						}
	
					}
		        }
          		String termTypeId = "";
				BigDecimal amount = BigDecimal.ZERO;
				String amountStr = "";
				if (UtilValidate.isNotEmpty( shipmentItem.get("termTypeId"))) {
					termTypeId = (String) shipmentItem.get("termTypeId");
				}
			  
				if (UtilValidate.isNotEmpty( shipmentItem.get("amount"))) {
					amountStr = (String) shipmentItem.get("amount");
				}
					  
				if(UtilValidate.isNotEmpty(amountStr)){
					amount = new BigDecimal(amountStr);
				}
				
				if(UtilValidate.isNotEmpty(termTypeId) && amount.compareTo(BigDecimal.ZERO)>0){
					if(!orderAdjustmentTypes.contains(termTypeId)){
						landingCharges = landingCharges.add(amount);
					}
					GenericValue shipmentAttribute = delegator.makeValue("ShipmentAttribute");
					shipmentAttribute.set("shipmentId", shipmentId);
					shipmentAttribute.set("attrName", termTypeId);
					shipmentAttribute.set("attrValue", amountStr);
					delegator.createOrStore(shipmentAttribute);
				}
				//Debug.log("quantityStr======="+quantityStr);
				GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
				String	desc=product.getString("description");
				//Debug.log("desc============"+desc);
				 smsContent = smsContent + quantityStr + " KGs of " + desc + ",";
           if (UtilValidate.isNotEmpty( shipmentItem.get("productId"))) {
				Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("shipmentId",shipmentId);
		        itemInMap.put("userLogin",userLogin);
		        itemInMap.put("productId",productId);
		        itemInMap.put("quantity",quantity);
		        Map resultMap = dispatcher.runSync("createShipmentItem",itemInMap);
		        
		        if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError("Problem creating shipment Item for orderId :"+orderId, module);
					TransactionUtil.rollback();
					return ServiceUtil.returnError("Problem creating shipment Item for orderId :"+orderId);	
		        }
		        
		        String shipmentItemSeqId = (String)resultMap.get("shipmentItemSeqId");
		        //List<GenericValue> productsFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
				List<GenericValue> filteredOrderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				GenericValue ordItm = null;
				if(UtilValidate.isNotEmpty(filteredOrderItem)){
					ordItm = EntityUtil.getFirst(filteredOrderItem);
				    orderItemSeqId = ordItm.getString("orderItemSeqId");
					List<GenericValue> itemChanges = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
					if(UtilValidate.isNotEmpty(itemChanges)){
						ordItm = EntityUtil.getFirst(itemChanges);
					}
				}
				
				/*List<GenericValue> filterProdFacility = EntityUtil.filterByCondition(productsFacility, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				List prodFacilityIds = EntityUtil.getFieldListFromEntityList(filterProdFacility, "facilityId", true);
				List facilityConditionList = FastList.newInstance();
				facilityConditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "STORE"));
				facilityConditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
				facilityConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,prodFacilityIds));
				EntityCondition facilityCondition = EntityCondition.makeCondition(facilityConditionList, EntityOperator.AND);
				List<GenericValue> facilities = delegator.findList("Facility", facilityCondition, null, null, null, false);
				GenericValue facilityProd = EntityUtil.getFirst(facilities);*/
				//Product should mapped to any one of facility
				/* if (UtilValidate.isEmpty(facilityProd)) {
			        	Debug.logError("Problem creating shipment Item for ProductId :"+productId+" Not Mapped To Store Facility !", module);
						request.setAttribute("_ERROR_MESSAGE_", "Problem creating shipment Item for ProductId :"+productId+" Not Mapped To Store Facility!");	
						TransactionUtil.rollback();
				  		return "error";
			        }*/
				Map inventoryReceiptCtx = FastMap.newInstance();
				
				inventoryReceiptCtx.put("userLogin", userLogin);
				inventoryReceiptCtx.put("productId", productId);
				inventoryReceiptCtx.put("datetimeReceived", receiptDate);
				inventoryReceiptCtx.put("quantityAccepted", quantity);
				inventoryReceiptCtx.put("quantityRejected", BigDecimal.ZERO);
				if(deliveryChallanQty.compareTo(BigDecimal.ZERO)>0){
					inventoryReceiptCtx.put("deliveryChallanQty",deliveryChallanQty);
		        }
				inventoryReceiptCtx.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				inventoryReceiptCtx.put("ownerPartyId", supplierId);
				/*inventoryReceiptCtx.put("consolidateInventoryReceive", "Y");*/
				if(UtilValidate.isNotEmpty(originFacilityId)){

				inventoryReceiptCtx.put("facilityId", originFacilityId);
				}else{
					inventoryReceiptCtx.put("facilityId", "BRANCH1");
				}
				//facilityProd.getString("facilityId"));
				inventoryReceiptCtx.put("unitCost", BigDecimal.ZERO);
				if(UtilValidate.isNotEmpty(ordItm)){
					inventoryReceiptCtx.put("unitCost", ordItm.getBigDecimal("unitListPrice"));
					inventoryReceiptCtx.put("orderId", ordItm.getString("orderId"));
					inventoryReceiptCtx.put("orderItemSeqId", ordItm.getString("orderItemSeqId"));
				}
				/*inventoryReceiptCtx.put("shipmentId", shipmentId);
				inventoryReceiptCtx.put("shipmentItemSeqId", shipmentItemSeqId);*/
				Map<String, Object> receiveInventoryResult;
				receiveInventoryResult = dispatcher.runSync("receiveInventoryProduct", inventoryReceiptCtx);
				
				if (ServiceUtil.isError(receiveInventoryResult)) {
					Debug.logWarning("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
					TransactionUtil.rollback();
					return ServiceUtil.returnError("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult));
	            }
				
				receiptId = (String)receiveInventoryResult.get("receiptId");
				shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", receiptId), false);
				if(UtilValidate.isNotEmpty(shipmentReceipt)){
					shipmentReceipt.set("shipmentId", shipmentId);
					shipmentReceipt.set("shipmentItemSeqId", shipmentItemSeqId);
					shipmentReceipt.store();
				}
				//storing shipment receipt status Here 
				if(UtilValidate.isNotEmpty(receiptId)){
					GenericValue shipmentReceiptStatus = delegator.makeValue("ShipmentReceiptStatus");
					shipmentReceiptStatus.set("receiptId", receiptId);
					shipmentReceiptStatus.set("statusId", (String) shipmentReceipt.get("statusId"));
					shipmentReceiptStatus.set("changedByUserLogin", userLogin.getString("userLoginId"));
					shipmentReceiptStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
					delegator.createSetNextSeqId(shipmentReceiptStatus);
				}
           }
              
           }
			
			if(UtilValidate.isNotEmpty(orderId) && landingCharges.compareTo(BigDecimal.ZERO)>0){
				List condExpr = FastList.newInstance();
				condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				condExpr.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				EntityCondition cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
				List<GenericValue> shipReceipts = delegator.findList("ShipmentReceipt", cond, null, null, null, false);
				
				// recalculating landing cost for handling transportation/frieght at actuals and installation charges
				for(GenericValue shipReceipt : shipReceipts){
					condExpr.clear();
					condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipReceipt.getString("orderId")));
					condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, shipReceipt.getString("orderItemSeqId")));
					EntityCondition condition = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
					
					List<GenericValue> orderItem = EntityUtil.filterByCondition(orderItems, condition);
					
					condExpr.clear();
					condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipReceipt.getString("orderId")));
					condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, shipReceipt.getString("orderItemSeqId")));
					EntityCondition condition1 = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
					
					List<GenericValue> ordItmChange = EntityUtil.filterByCondition(orderItemChanges, condition);

					
					if(UtilValidate.isNotEmpty(orderItem)){
						BigDecimal unitListPrice = (EntityUtil.getFirst(orderItem)).getBigDecimal("unitListPrice");
						if(UtilValidate.isNotEmpty(ordItmChange)){
							unitListPrice = (EntityUtil.getFirst(ordItmChange)).getBigDecimal("unitListPrice");
						}
						BigDecimal qty = shipReceipt.getBigDecimal("quantityAccepted");
						
						BigDecimal itemValue = unitListPrice.multiply(qty);
						BigDecimal calcAmt = (itemValue.multiply(landingCharges)).divide(poValue, purchaseTaxFinalDecimals, purchaseTaxRounding);
						BigDecimal adjUnitAmt = calcAmt.divide(qty, purchaseTaxFinalDecimals, purchaseTaxRounding);
						BigDecimal updatedLandingCost = unitListPrice.add(adjUnitAmt);
						String inventoryItemId = shipReceipt.getString("inventoryItemId");
						GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
						inventoryItem.set("unitCost", updatedLandingCost);
						inventoryItem.store();
					}
					
				}
			}
			GenericValue orderHeaderDetails = null;
			orderHeaderDetails = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
	        String statusId = orderHeaderDetails.getString("statusId");
	        if(statusId.equals("ORDER_COMPLETED")){
				List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("toOrderId"), null, null, false);
				String toOrderId ="";
				if(UtilValidate.isNotEmpty(orderAssoc)){
					toOrderId = (EntityUtil.getFirst(orderAssoc)).getString("toOrderId");
				}
	        	Map statusCtx = FastMap.newInstance();
	 			statusCtx.put("statusId", statusId);
	 			statusCtx.put("orderId", toOrderId);
	 			statusCtx.put("userLogin", userLogin);
	 			Map resultCtx = OrderServices.setOrderStatus(dctx, statusCtx);
	 			if (ServiceUtil.isError(resultCtx)) {
	 				Debug.logError("Order set status failed for orderId: " + orderId, module);
	 				return ServiceUtil.returnError("Order set status failed for orderId: " + orderId);
	 			} 
	        }
	        if(UtilValidate.isNotEmpty(hideQCflow)&&("Y".equals(hideQCflow))){
				Map inputMap = FastMap.newInstance();
				inputMap.put("statusIdTo","SR_QUALITYCHECK");
				inputMap.put("receiptId",receiptId);
				inputMap.put("partyId","DEPOT");
				inputMap.put("userLogin",userLogin);
				Map resultMap = dispatcher.runSync("sendReceiptQtyForQC",inputMap);
				
				if (ServiceUtil.isError(resultMap)) {
					Debug.logWarning("There was an error while sending to QC: " + ServiceUtil.getErrorMessage(resultMap), module);
					//request.setAttribute("_ERROR_MESSAGE_", "There was an error sending to QC: " + ServiceUtil.getErrorMessage(resultMap));	
					TransactionUtil.rollback();
					return ServiceUtil.returnError("There was an error sending to QC: " + ServiceUtil.getErrorMessage(resultMap));
	            }
				inputMap.clear();
				resultMap.clear();
				
				inputMap.put("statusIdTo","SR_ACCEPTED");
				inputMap.put("receiptId",receiptId);
				inputMap.put("shipmentId",shipmentId);
				inputMap.put("shipmentItemSeqId",shipmentReceipt.get("shipmentItemSeqId") );
				inputMap.put("quantityAccepted",shipmentReceipt.get("quantityAccepted"));
				inputMap.put("userLogin",userLogin);
				resultMap = dispatcher.runSync("acceptReceiptQtyByQC", inputMap);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logWarning("There was an error while Accepting in QC: " + ServiceUtil.getErrorMessage(resultMap), module);
					TransactionUtil.rollback();
					return ServiceUtil.returnError("There was an error while Accepting in QC: " + ServiceUtil.getErrorMessage(resultMap));
	            }
			}
		}
		catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  			return ServiceUtil.returnError("Could not rollback transaction");
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  		return ServiceUtil.returnError("An entity engine error occurred while fetching data");

	  	}
  	  	catch (GenericServiceException e) {
  	  		try {
  			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
  	  		} catch (GenericEntityException e2) {
  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  			  return ServiceUtil.returnError("Could not rollback transaction: ");
  	  		}
  	  		Debug.logError("An entity engine error occurred while calling services", module);
  	  		return ServiceUtil.returnError("An entity engine error occurred while calling services");
  	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beganTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			return ServiceUtil.returnError("Could not commit transaction for entity engine error occurred while fetching data");
	  		}
	  	}
		try {
        	List condExpr = FastList.newInstance();
			condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condExpr.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
			EntityCondition cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
			List<GenericValue> orderRoleList = delegator.findList("OrderRole", cond, null, null, null, false);
		 
			GenericValue orderRoleFirstList = EntityUtil.getFirst(orderRoleList);
			String partyIdTo = (String) orderRoleFirstList.getString("partyId");
			GenericValue ShipmentList = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);  
			
			
			 if (UtilValidate.isNotEmpty(ShipmentList)) {
	 			  
				 ShipmentList.set("shipmentId",shipmentId);
				 ShipmentList.set("partyIdTo",partyIdTo);
				 
 		        try {
 		        	ShipmentList.store();
 		        } catch (GenericEntityException e) {
		  			Debug.logError(e, "Could not Update partyIdTo in Shipment entity", module);
		  			return ServiceUtil.returnError("Could not get date from OrderAttribute");
 		        }
			 }
        } catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not get date from OrderAttribute", module);
	  			return ServiceUtil.returnError("Could not get date from OrderAttribute");
    }

		String rlatedId="";
		String customerId="";
		List condiList = FastList.newInstance();
		try{
			condiList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condiList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
			List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition(condiList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(orderAssoc)){
				rlatedId = (EntityUtil.getFirst(orderAssoc)).getString("toOrderId");
			
			}
		}catch (GenericEntityException e) {
			Debug.logError(e, "Could not get date from OrderAssoc", module);
		}
		if(UtilValidate.isNotEmpty(rlatedId)){
			try{
			condiList.clear();
			condiList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, rlatedId));
			condiList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
			EntityCondition condi = EntityCondition.makeCondition(condiList, EntityOperator.AND);
			List<GenericValue> orderRoleForCustomer = delegator.findList("OrderRole", condi, null, null, null, false);
			if(UtilValidate.isEmpty(orderRoleForCustomer)){
				Debug.logError("No Vendor for the order : "+rlatedId, module);
				TransactionUtil.rollback();
				return ServiceUtil.returnError("No Vendor for the order : "+rlatedId);
			}
			customerId = (EntityUtil.getFirst(orderRoleForCustomer)).getString("partyId");
			}catch (GenericEntityException e) {
				Debug.logError(e, "Could not get date from OrderRole", module);
			}
		}
		String shipmentMessageToWeaver = UtilProperties.getMessage("ProductUiLabels", "ShipmentMessageToWeaver", locale);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("orderId", rlatedId);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("material", smsContent);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("transporter", carrierName);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("lrNo", lrNumber);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("lrDate", deliveryChallanDateStr);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("expectedDeliveryDate", estimatedDateStr);
		shipmentMessageToWeaver = shipmentMessageToWeaver.replaceAll("estimatedReadyDate", lrDateStr);
		if(UtilValidate.isNotEmpty(customerId)){
			String customerName=org.ofbiz.party.party.PartyHelper.getPartyName(delegator,supplierId, false);
			Map<String, Object> getTelParams = FastMap.newInstance();
			if(UtilValidate.isEmpty(customerName)){
				customerName=supplierId;
			}
        	getTelParams.put("partyId", customerId);
            getTelParams.put("userLogin", userLogin); 
            try{
            	Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	            	return ServiceUtil.returnError("some error occured");
	            }
	            String contactNumberTo = (String) serviceResult.get("contactNumber");            
	            String countryCode = (String) serviceResult.get("countryCode");
	            if(UtilValidate.isEmpty(contactNumberTo)){
	            	contactNumberTo = "7330776928";
	            }
	            if(UtilValidate.isEmpty(carrierName)){
	            	carrierName = "_";
	            }
	            contactNumberTo = "7330776928";
	            Debug.log("contactNumberTo = "+contactNumberTo);
	            if(UtilValidate.isNotEmpty(contactNumberTo)){
	            	 if(UtilValidate.isNotEmpty(countryCode)){
	            		 contactNumberTo = countryCode + contactNumberTo;
	            	 }
	            	 Debug.log("contactNumberTo ===== "+contactNumberTo);
	            	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                 sendSmsParams.put("contactNumberTo", contactNumberTo);
	                 sendSmsParams.put("text", shipmentMessageToWeaver); 
	                 serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	                 if (ServiceUtil.isError(serviceResult)) {
	                     Debug.logError("unable to send Sms", module);
	                 }
	            }
            }catch(GenericServiceException e1){
	         	Debug.log("Problem in sending sms to user agency");
			}
		}
		String Scheam = "";
 		try{
     		List<GenericValue> orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			List<GenericValue> scheamList = EntityUtil.filterByCondition(orderAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
			if(UtilValidate.isNotEmpty(scheamList)){
				GenericValue orderScheme = EntityUtil.getFirst(scheamList);
				Scheam = (String) orderScheme.get("attrValue");
			}
 		}catch(Exception e){
			Debug.logError("problem While Fetching OrderAttribute : "+e, module);
		} 
 		if(UtilValidate.isNotEmpty(Scheam) && Scheam != "General")
 		{
	        GenericValue Shipment = null;
	  		try{
	  			Shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId",shipmentId),false);
	  			GenericValue ShipmentReimbursement = delegator.makeValue("ShipmentReimbursement");
				BigDecimal estimatedShipCost=Shipment.getBigDecimal("estimatedShipCost");
				Timestamp receiptDate1=Shipment.getTimestamp("estimatedShipDate");
				if(UtilValidate.isEmpty(receiptDate1))
					receiptDate1=UtilDateTime.nowTimestamp();
  				if(UtilValidate.isNotEmpty(estimatedShipCost)){
	  				ShipmentReimbursement.set("shipmentId", shipmentId);
	  				ShipmentReimbursement.set("receiptAmount", estimatedShipCost);
	  				ShipmentReimbursement.set("receiptDate", receiptDate1);
					delegator.createSetNextSeqId(ShipmentReimbursement);
  				}
	  		}catch(GenericEntityException e){
				Debug.logError(e, "Failed to get Shipment ", module);
			}
 		}
 		Map shipmentResult = FastMap.newInstance();
 		shipmentResult.put("shipmentId",shipmentId);
 		result.put("shipmentResult",shipmentResult);
		return result;
    }
}
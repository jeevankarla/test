package in.vasista.vbiz.depotsales;
import java.text.DateFormat;

import in.vasista.vbiz.depotsales.DepotPurchaseServices;
import in.vasista.vbiz.depotsales.DepotHelperServices;
import in.vasista.vbiz.depotsales.DepotSalesServices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericDelegator;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.util.Iterator;

import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.order.order.OrderReadHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.party.party.PartyHelper;

import java.util.Map.Entry;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

public class DepotSalesApiServices{

   
	public static final String module = DepotSalesApiServices.class.getName();
	private static int decimals;
	private static int rounding;
    public static final String resource = "AccountingUiLabels";
    
    static {
        decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        //if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
    
    /*
     * Security check to make userLogin partyId must equal facility owner party Id if the user
     * is a retailer (has MOB_RTLR_DB_VIEW). If user is a sales rep (MOB_SREP_DB_VIEW permission), 
     * then we just return true.
     */
    static boolean hasFacilityAccess(DispatchContext dctx, Map<String, ? extends Object> context) {  
        Security security = dctx.getSecurity();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	GenericValue party = (GenericValue) context.get("party");
        if (security.hasEntityPermission("MOB_SREP_DB", "_VIEW", userLogin)) {
            return true;
        } 		
        if (security.hasEntityPermission("MOB_RTLR_DB", "_VIEW", userLogin)) {
        	if (userLogin != null && userLogin.get("partyId") != null) {
        		String userLoginParty = (String)userLogin.get("partyId");
        		String ownerParty = (String)party.get("partyId");
        		if (userLoginParty.equals(ownerParty)) {
        			return true;
        		}
        	}
        }
    	return false;
    }
    
    public static Map<String, Object> getWeaverIndents(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        
        String orderId = (String)context.get("orderId");
        String partyIdFrom = (String)context.get("partyIdFrom");
        String partyId = (String)context.get("partyId");
        String tallyRefNO = (String)context.get("tallyRefNO");
        String orderNo = (String)context.get("orderNo");
        Timestamp orderDate = (Timestamp)context.get("estimatedDeliveryDate");
        Timestamp orderThruDate = (Timestamp)context.get("estimatedDeliveryThruDate");
        //List branchList = (List)context.get("branchList");
        
        GenericValue party = null;
  		try{
  			party = delegator.findOne("Party",UtilMisc.toMap("partyId",partyId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching party " +partyId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching party " + partyId);	   
  		}
        if (!hasFacilityAccess(dctx, UtilMisc.toMap("userLogin", userLogin, "party", party))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access Indents: " + partyId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        }
        
        String statusId = (String)context.get("statusId");
        String purposeTypeId = (String)context.get("purposeTypeId");
        String indentDateSort = (String)context.get("indentDateSort");
        
        Timestamp orderDateStart = null;
        Timestamp orderDateEnd = null;
        Timestamp transDate = null;
        if(UtilValidate.isNotEmpty(orderDate)){
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	try {
        		transDate = new java.sql.Timestamp(sdf.parse(orderDate+" 00:00:00").getTime());
        	} catch (ParseException e) {
        		Debug.logError(e, "Cannot parse date string: " + orderDate, "");
        	}
        	orderDateStart = UtilDateTime.getDayStart(transDate);
        	orderDateEnd = UtilDateTime.getDayEnd(transDate);
        }


        Timestamp transThruDate = null;
        if(UtilValidate.isNotEmpty(orderThruDate)){
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	try {
        		transThruDate = new java.sql.Timestamp(sdf.parse(orderThruDate+" 00:00:00").getTime());
        	} catch (ParseException e) {
        		Debug.logError(e, "Cannot parse date string: " + orderThruDate, "");
        	}
        	orderDateEnd = UtilDateTime.getDayEnd(transThruDate);
        }
        
        List conditionList = FastList.newInstance();
        
        if(UtilValidate.isNotEmpty(orderId)){
        	conditionList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.LIKE, "%"+orderId + "%"));
        }
        if(UtilValidate.isNotEmpty(tallyRefNO)){
        	conditionList.add(EntityCondition.makeCondition("tallyRefNo" ,EntityOperator.LIKE, "%"+tallyRefNO + "%"));
        }
        if(UtilValidate.isNotEmpty(statusId)){
        	conditionList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.EQUALS, statusId));
        }
        else{
        	conditionList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL,"ORDER_CANCELLED"));
        }
        conditionList.add(EntityCondition.makeCondition("purposeTypeId" ,EntityOperator.EQUALS, "BRANCH_SALES"));
        conditionList.add(EntityCondition.makeCondition("shipmentId" ,EntityOperator.EQUALS, null)); // Review
        if(UtilValidate.isNotEmpty(orderDate)){
        	conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, orderDateStart));
        	conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, orderDateEnd));
        }

        // Order Filteration based on vendor
        List billFromVendorOrderRoles = FastList.newInstance();
        List custCondList = FastList.newInstance();
        List branchbasedIds = FastList.newInstance();
        
       /* if(branchList.size()==1){
    		custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchList));
	    	custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	    	try{
	    		billFromVendorOrderRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(custCondList, EntityOperator.AND), null, null, null, false);
	    	} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
	    	
	    	branchbasedIds = EntityUtil.getFieldListFromEntityList(billFromVendorOrderRoles, "orderId", true);
	    	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, branchbasedIds));
    	}*/
        
        // Order Filteration based on customer
        
        custCondList.clear();
        //give preference to ShipToCustomer
        if(UtilValidate.isNotEmpty(branchbasedIds)){
        	custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, branchbasedIds));
        }
        if(UtilValidate.isNotEmpty(partyId)){
        	custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        }
        custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
        
        List orderRoles = FastList.newInstance();
        try{
        	orderRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(custCondList, EntityOperator.AND), null, null, null, false);
        }catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
        
        if(UtilValidate.isEmpty(orderRoles)){
        	custCondList.clear();
        	if(UtilValidate.isNotEmpty(branchbasedIds)){
            	custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, branchbasedIds));
            }
        	if(UtilValidate.isNotEmpty(partyId)){
        		custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        	}
        	custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
	        try{
	        	orderRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(custCondList, EntityOperator.AND), null, null, null, false);
	        } catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
        }

        List customerBasedOrderIds = EntityUtil.getFieldListFromEntityList(orderRoles, "orderId", true);
        if(UtilValidate.isNotEmpty(customerBasedOrderIds)){
        	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, customerBasedOrderIds));
        }
        EntityListIterator orderHeaderList = null;
        try{
        	List orderBy = UtilMisc.toList("-createdStamp");
	        orderHeaderList = delegator.find("OrderHeader", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, orderBy, null);
	    } catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
        
        List<GenericValue> statusItemList = null;
	  	try{
	  		statusItemList = delegator.findList("StatusItem",EntityCondition.makeCondition("statusTypeId",EntityOperator.IN, UtilMisc.toList("ORDER_STATUS", "ORDER_ITEM_STATUS")), null, null, null, false);
	   	}catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive StatusItem ", module);
			return ServiceUtil.returnError("Failed to retrive StatusItem " + e);
		}
        List orderList = FastList.newInstance();  
        GenericValue eachHeader; 
        while( orderHeaderList != null && (eachHeader = orderHeaderList.next()) != null) {
        	
    		String eachOrderId = (String) eachHeader.get("orderId");
    	
    		List orderParty = EntityUtil.filterByCondition(orderRoles, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
    		String orderPartyId = "";
    		if(UtilValidate.isNotEmpty(orderParty)){
    			orderPartyId = (String) (EntityUtil.getFirst(orderParty)).get("partyId");
    		}
    	
    		List billFromOrderParty = EntityUtil.filterByCondition(billFromVendorOrderRoles, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
    		String billFromVendorPartyId = "";
    		if(UtilValidate.isNotEmpty(billFromOrderParty)){
    			billFromVendorPartyId = (String) (EntityUtil.getFirst(billFromOrderParty)).get("partyId");
    		}
    	
    		String partyName = PartyHelper.getPartyName(delegator, orderPartyId, false);
    		Map tempData = FastMap.newInstance();
    		tempData.put("partyId", orderPartyId);
    		tempData.put("billFromVendorPartyId", billFromVendorPartyId);
    		tempData.put("partyName", partyName);
    		if(UtilValidate.isNotEmpty(eachHeader.get("tallyRefNo"))){
    			tempData.put("tallyRefNo", eachHeader.get("tallyRefNo"));
    		}	
    		else{
    			tempData.put("tallyRefNo", "NA");
    		}
    		
    		String eachOrderNo ="NA";
    		try{
	    		List orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , eachOrderId)  , null, null, null, false );
		    	if(UtilValidate.isNotEmpty(orderHeaderSequences)){
		    		eachOrderNo = (String)(EntityUtil.getFirst(orderHeaderSequences)).get("orderNo");
		    	}
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    	
    		List exprCondList = FastList.newInstance();
    		exprCondList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, eachOrderId));
    		exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
    		EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
    		GenericValue orderAssoc = null; 
    		try{
    			orderAssoc = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    	
    		String POorder="NA";
    		String isgeneratedPO="N";
    		if(UtilValidate.isNotEmpty(orderAssoc)){
    			POorder=(String)orderAssoc.get("orderId");
    			isgeneratedPO = "Y";
    		}
    		
    		String poSquenceNo="NA";
    		try{
	    		List poOrderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , POorder)  , null, null, null, false );
	    		if(UtilValidate.isNotEmpty(poOrderHeaderSequences)){
	    			poSquenceNo = (String)(EntityUtil.getFirst(poOrderHeaderSequences)).get("orderNo");
	    		}
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    	
    		tempData.put("POorder", POorder);
    		tempData.put("poSquenceNo", poSquenceNo);
    		tempData.put("isgeneratedPO", isgeneratedPO);
    		
    		String supplierPartyId = "";
    		String productStoreId = "";
    		String supplierPartyName = "";
    		
	    	List exprList = FastList.newInstance();
	    	exprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderId));
	    	exprList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
	    	try{
	    	
		    	GenericValue supplierDetails = EntityUtil.getFirst(delegator.findList("OrderRole", EntityCondition.makeCondition(exprList, EntityOperator.AND), null,null,null, false));
		    	if(UtilValidate.isNotEmpty(supplierDetails)){
		    		supplierPartyId = (String)supplierDetails.get("partyId");
		    	}
		    	if(UtilValidate.isNotEmpty(supplierPartyId)){
		    		supplierPartyName = PartyHelper.getPartyName(delegator, supplierPartyId, false);
		    	}
	    	} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    	
	    	productStoreId = (String) eachHeader.get("productStoreId");
    	
	    	tempData.put("supplierPartyId", supplierPartyId);
	    	tempData.put("storeName", productStoreId);
	    	tempData.put("supplierPartyName", supplierPartyName);
	    	tempData.put("orderNo", eachOrderNo);
	    	tempData.put("orderId", eachOrderId);
	    	tempData.put("orderDate", String.valueOf(eachHeader.get("estimatedDeliveryDate")).substring(0,10));
	    	GenericValue filteredOrderStatus = EntityUtil.getFirst(EntityUtil.filterByCondition(statusItemList, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, eachHeader.get("statusId"))));
	    	tempData.put("statusId", filteredOrderStatus.getString("description"));

	    	if("APPROVE_LEVEL1".equals(eachHeader.get("statusId")) || "APPROVE_LEVEL2".equals(eachHeader.get("statusId")) || "APPROVE_LEVEL3".equals(eachHeader.get("statusId"))){
	    		tempData.put("statusId", "Draft Proposal");
	    	}
	    	
	    	if(UtilValidate.isNotEmpty(eachHeader.getBigDecimal("grandTotal"))){
	    		tempData.put("orderTotal", eachHeader.getBigDecimal("grandTotal"));
	    	}
	    	
	    	List conditonList = FastList.newInstance();
	    	conditonList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, eachOrderId));
	    	List orderPaymentPreference = FastList.newInstance();
	    	try{
	    		orderPaymentPreference = delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition(conditonList, EntityOperator.AND), null, null, null ,false);
	    	} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
	    	BigDecimal paidAmt = BigDecimal.ZERO;
    	
	    	List paymentIdsOfIndentPayment = FastList.newInstance();
    	
	    	if(UtilValidate.isNotEmpty(orderPaymentPreference)){
    	
	    		List orderPreferenceIds = EntityUtil.getFieldListFromEntityList(orderPaymentPreference, "orderPaymentPreferenceId", true);
     
		    	conditonList.clear();
		    	conditonList.add(EntityCondition.makeCondition("paymentPreferenceId" ,EntityOperator.IN,orderPreferenceIds));
		    	conditonList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
		    	
		    	try{
		    		List paymentList = delegator.findList("Payment", EntityCondition.makeCondition(conditonList, EntityOperator.AND), null, null, null ,false);
		    		for (int i=0; i<paymentList.size(); i++) {
		    			GenericValue eachPayment = (GenericValue) paymentList.get(i);
			    		paidAmt = paidAmt.add(eachPayment.getBigDecimal("amount"));
			    	}
		    		if(UtilValidate.isNotEmpty(paymentList)){
		    			paymentIdsOfIndentPayment = EntityUtil.getFieldListFromEntityList(paymentList,"paymentId", true);
		    		}
		    	} catch (GenericEntityException e) {
	    			Debug.logError(e, module);
	    		}
    	
	    	}
    	
	    	conditonList.clear();
	    	conditonList.add(EntityCondition.makeCondition("orderId" , EntityOperator.EQUALS, eachOrderId));
	    	try{
	    		List OrderItemBillingList = delegator.findList("OrderItemBilling", EntityCondition.makeCondition(conditonList, EntityOperator.AND), null, null, null ,false);
	    		List invoiceIds = EntityUtil.getFieldListFromEntityList(OrderItemBillingList,"invoiceId", true);
	        	
		    	if(UtilValidate.isNotEmpty(invoiceIds)){
		    		conditonList.clear();
		    		conditonList.add(EntityCondition.makeCondition("invoiceId" ,EntityOperator.IN,invoiceIds));
		    		List paymentApplicationList = delegator.findList("PaymentApplication", EntityCondition.makeCondition(conditonList, EntityOperator.AND), null, null, null ,false);
		    		for (int i=0; i<paymentApplicationList.size(); i++) {
		    			GenericValue eachPayment = (GenericValue) paymentApplicationList.get(i);
		    			if(!paymentIdsOfIndentPayment.contains(eachPayment.get("paymentId"))){
		    				paidAmt = paidAmt.add(eachPayment.getBigDecimal("amountApplied"));
		    			}
		    		}
		    	}
	    	} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
	    	
	    	tempData.put("paidAmt", paidAmt);
	    	BigDecimal grandTOT = eachHeader.getBigDecimal("grandTotal");
	    	BigDecimal balance = grandTOT.subtract(paidAmt);
	    	tempData.put("balance", balance);
	    	BigDecimal totDiscountAmt = BigDecimal.ZERO;	    	
	    	conditonList.clear();
	    	conditonList.add(EntityCondition.makeCondition("orderId" , EntityOperator.EQUALS, eachOrderId));
	    	conditonList.add(EntityCondition.makeCondition("orderItemTypeId" , EntityOperator.EQUALS, "PRODUCT_ORDER_ITEM"));
	    	conditonList.add(EntityCondition.makeCondition("productId" , EntityOperator.NOT_EQUAL, null));
	    	List orderItems = FastList.newInstance();
	    	
	    	BigDecimal totalOrderQty = BigDecimal.ZERO;
	    	BigDecimal totalShippedQty = BigDecimal.ZERO;
	    	
	    	try{
	    		List<GenericValue> orderItemList = delegator.findList("OrderItem", EntityCondition.makeCondition(conditonList, EntityOperator.AND), null, null, null ,false);
	    		if(UtilValidate.isNotEmpty(orderItemList)){
	    			List<GenericValue> orderItemDetailList = delegator.findList("OrderItemDetail", EntityCondition.makeCondition("orderId" , EntityOperator.EQUALS, eachOrderId), null, null, null ,false);
	    			List<GenericValue> orderAdjList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId" , EntityOperator.EQUALS, eachOrderId), null, null, null ,false);
	    			for(GenericValue eachItem:orderItemList){
		    			Map itemDetailMap = FastMap.newInstance();
		    			BigDecimal quantity = BigDecimal.ZERO;
		    			BigDecimal unitPrice = BigDecimal.ZERO;
		    			BigDecimal vatPercent = BigDecimal.ZERO;
		    			BigDecimal vatAmount = BigDecimal.ZERO;	
		    			BigDecimal cstPercent = BigDecimal.ZERO;
		    			BigDecimal cstAmount = BigDecimal.ZERO;
		    			BigDecimal discountAmount = BigDecimal.ZERO;
		    			BigDecimal shippedQty = BigDecimal.ZERO;
		    			BigDecimal totalAmount = BigDecimal.ZERO;
		    			BigDecimal otherCharges = BigDecimal.ZERO;
		    			BigDecimal baleQty = BigDecimal.ZERO;
		    			BigDecimal bundleWeight = BigDecimal.ZERO;
		    			BigDecimal netAmount = BigDecimal.ZERO;
		    			String specification = "";
		    			String uom = "";
		    			itemDetailMap.put("productId",eachItem.getString("productId"));
		    			itemDetailMap.put("itemDescription",eachItem.getString("itemDescription"));
		    			itemDetailMap.put("orderItemSeqId",eachItem.getString("orderItemSeqId"));
		    			itemDetailMap.put("orderItemTypeId",eachItem.getString("orderItemTypeId"));
		    			GenericValue filteredItemStatus = EntityUtil.getFirst(EntityUtil.filterByCondition(statusItemList, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, eachHeader.get("statusId"))));
		    			List<GenericValue> filteredItemDetail = EntityUtil.filterByCondition(orderItemDetailList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.get("orderItemSeqId")));
		    			itemDetailMap.put("statusId", filteredItemStatus.getString("description"));
		    			if(UtilValidate.isNotEmpty(eachItem.getBigDecimal("quantity"))){
		    				quantity = eachItem.getBigDecimal("quantity");
		    			}
		    			if(UtilValidate.isNotEmpty(eachItem.getBigDecimal("unitPrice"))){
		    				unitPrice = eachItem.getBigDecimal("unitPrice");
		    			}
		    			if(UtilValidate.isNotEmpty(eachItem.getBigDecimal("vatPercent"))){
		    				vatPercent = eachItem.getBigDecimal("vatPercent");
		    			}
		    			if(UtilValidate.isNotEmpty(eachItem.getBigDecimal("vatAmount"))){
		    				vatAmount = eachItem.getBigDecimal("vatAmount");
		    			}
		    			if(UtilValidate.isNotEmpty(eachItem.getBigDecimal("cstPercent"))){
		    				cstPercent = eachItem.getBigDecimal("cstPercent");
		    			}
		    			if(UtilValidate.isNotEmpty(eachItem.getBigDecimal("cstAmount"))){
		    				cstAmount = eachItem.getBigDecimal("cstAmount");
		    			}
		    			if(UtilValidate.isNotEmpty(filteredItemDetail)){
		    				for(GenericValue eachItemDetaildis:filteredItemDetail){
		    					discountAmount = discountAmount.add(eachItemDetaildis.getBigDecimal("discountAmount"));
		    					totDiscountAmt = totDiscountAmt.add(eachItemDetaildis.getBigDecimal("discountAmount"));
		    					baleQty = baleQty.add(eachItemDetaildis.getBigDecimal("baleQuantity"));
		    					bundleWeight = bundleWeight.add(eachItemDetaildis.getBigDecimal("bundleWeight"));
		    					if(UtilValidate.isNotEmpty(eachItemDetaildis.getString("Uom"))){
		    						uom = eachItemDetaildis.getString("Uom");
		    					}
		    					if(UtilValidate.isNotEmpty(eachItemDetaildis.getString("remarks"))){
		    						specification = eachItemDetaildis.getString("remarks");
		    					}
		    				}
		    			}
		    			List<EntityCondition> orderAdjCond = FastList.newInstance();
		    			orderAdjCond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.getString("orderItemSeqId")));
		    			orderAdjCond.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL, "TEN_PERCENT_SUBSIDY"));
		    			List<GenericValue> filteredOrderAdj = EntityUtil.filterByCondition(orderAdjList, EntityCondition.makeCondition(orderAdjCond, EntityOperator.AND));
		    			if(UtilValidate.isNotEmpty(filteredOrderAdj)){
		    				for(GenericValue eachOrderAdj:filteredOrderAdj){
		    					otherCharges = otherCharges.add(eachOrderAdj.getBigDecimal("amount"));
		    				}
		    			}
		    			totalAmount = quantity.multiply(unitPrice);
		    			itemDetailMap.put("specification",specification);
		    			itemDetailMap.put("uom",uom);
		    			itemDetailMap.put("quantity",quantity.setScale(decimals, rounding));
		    			
		    			totalOrderQty = totalOrderQty.add(quantity);
		    			
		    			itemDetailMap.put("unitPrice",unitPrice.setScale(decimals, rounding));
		    			itemDetailMap.put("totalAmount",totalAmount.setScale(decimals, rounding));
		    			itemDetailMap.put("baleQty",baleQty.setScale(decimals, rounding));
		    			itemDetailMap.put("bundleWeight",bundleWeight.setScale(decimals, rounding));
		    			itemDetailMap.put("vatPercent",vatPercent.setScale(decimals, rounding));
		    			itemDetailMap.put("vatAmount",vatAmount.setScale(decimals, rounding));
		    			itemDetailMap.put("cstPercent",cstPercent.setScale(decimals, rounding));
		    			itemDetailMap.put("cstAmount",cstAmount.setScale(decimals, rounding));
		    			itemDetailMap.put("discountAmount",discountAmount.setScale(decimals, rounding));
		    			itemDetailMap.put("otherCharges",otherCharges.setScale(decimals, rounding));
		    			netAmount = totalAmount.add(discountAmount);
		    			netAmount = netAmount.add(otherCharges);
		    			itemDetailMap.put("netAmount",netAmount.setScale(decimals, rounding));
		    			List<EntityCondition> orderCondList = FastList.newInstance();
		    			orderCondList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, eachOrderId));
		    			orderCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
		    			List<GenericValue> orderAssc = delegator.findList("OrderAssoc", EntityCondition.makeCondition(orderCondList, EntityOperator.AND), null, null, null, false);
		    			if(UtilValidate.isNotEmpty(orderAssc)){
		    				GenericValue orderAssocValue = EntityUtil.getFirst(orderAssc);
		    				String poOrderId = orderAssocValue.getString("orderId");
		    				List<EntityCondition> shipmentCondList = FastList.newInstance();
		    				shipmentCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId));
		    				shipmentCondList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.getString("productId")));
		    				shipmentCondList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_RECEIVED","SR_ACCEPTED")));
			    			List<GenericValue> shipmentReceipts = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(shipmentCondList, EntityOperator.AND), null, null, null, false);
			    			if(UtilValidate.isNotEmpty(shipmentReceipts)){
			    				for(GenericValue eachShipmentReceipt:shipmentReceipts){
			    					shippedQty = shippedQty.add(eachShipmentReceipt.getBigDecimal("quantityAccepted"));
			    				}
			    			}
		    			}
		    			itemDetailMap.put("shippedQty",shippedQty.setScale(decimals, rounding));
		    			totalShippedQty = totalShippedQty.add(shippedQty);
		    			orderItems.add(itemDetailMap);
		    		}
	    		}
	    	} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
	    	tempData.put("totDiscountAmt", totDiscountAmt.setScale(decimals, rounding));
	    	tempData.put("orderItemsList", orderItems);

    		if(POorder != "NA"){
	    		List<GenericValue> shipReceiptList = null;
	    		try{
	        		shipReceiptList = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, POorder), null, null, null, false);
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
				}
	    		if(UtilValidate.isNotEmpty(shipReceiptList)){
	    			tempData.put("statusId", "Partially Dispatched");
	    			if(totalOrderQty.equals(totalShippedQty)){
	    				tempData.put("statusId",filteredOrderStatus.getString("description"));
	    				try{
	    					List<GenericValue> shipmentList = delegator.findList("Shipment", EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, POorder), UtilMisc.toSet("statusId"), null, null, false);
	    					for(GenericValue eachShipmentStatus:shipmentList){
	    						String tempStatus = eachShipmentStatus.getString("statusId");
		    					if(!(tempStatus.equals("DOCUMENTS_ENDORSED"))){
		    						tempData.put("statusId", "Dispatched");
		    						break;
		    					}
		    				}
	    				}catch (GenericEntityException e) {
	    					Debug.logError(e, module);
	    				}
	    			}
	    		}
	    	}
	    	
    		if("Created".equals(tempData.get("statusId"))){
    			tempData.put("statusId", "Indent Received");
    		}
    		if("Draft Proposal".equals(tempData.get("statusId"))){
    			tempData.put("statusId", "Indent In Process");
    		}
    		if("Approved".equals(tempData.get("statusId"))){
    			tempData.put("statusId", "PO Issued");
    		}
    		
    		
	    	String transporterName = "";
	    	String transporterId = "";
	    	String scheme = "";
	    	try{
	    		GenericValue OrderAttribute = delegator.findOne("OrderAttribute",UtilMisc.toMap("orderId",eachOrderId,"attrName","TRANSPORTER_PREF"),false);
	    		GenericValue schemeDetails = delegator.findOne("OrderAttribute",UtilMisc.toMap("orderId",eachOrderId,"attrName","SCHEME_CAT"),false);
	    		if(UtilValidate.isNotEmpty(schemeDetails)){
	    			scheme = schemeDetails.getString("attrValue");
	    			if(scheme.equalsIgnoreCase("MGPS_10Pecent")){
	    				scheme = "MGPS+10%"; 
	    			}
	    		}
	    		
	    		if(UtilValidate.isNotEmpty(OrderAttribute)){
	    			transporterName = OrderAttribute.getString("attrValue");
	    			if(transporterName != PartyHelper.getPartyName(delegator, transporterName, false)){
	    				transporterId = transporterName;
	    				transporterName = PartyHelper.getPartyName(delegator, transporterName, false);
	    			}else{
	    				try{
	    					List<GenericValue> PartyNameList = delegator.findList("PartyNameView", EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, transporterName), null, null, null, false);
	    					if(UtilValidate.isNotEmpty(PartyNameList)){
	    						transporterId = EntityUtil.getFirst(PartyNameList).getString("partyId");
	    					}
		    			}catch(GenericEntityException e){
		    				Debug.logWarning(e, module);
		    			}
	    			}
	    			
	    		}
	  		}catch(GenericEntityException e){
	  			Debug.logWarning("Error fetching OrderAttribute with order Id" +eachOrderId + " " +  e.getMessage(), module);
	  		}
	    	tempData.put("transporterName",transporterName);
	    	tempData.put("transporterId",transporterName);
	    	tempData.put("scheme",scheme);
	    	orderList.add(tempData);
	    }
		
        if (orderHeaderList != null) {
            try {
            	orderHeaderList.close();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        
        //Debug.log("orderList =========== "+orderList);
		Map result = FastMap.newInstance();  
    	Map orderDetailsMap = FastMap.newInstance();  
    	orderDetailsMap.put("orderList", orderList);
		result.put("indentSearchResults", orderDetailsMap);
    	return result;
    } 
 	
    public static Map<String, Object> getWeaverPayments(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		
  		String partyIdFrom = (String) context.get("partyId");
  		String paramPaymentId = (String) context.get("paymentId");
		List paymentSearchResultsList = FastList.newInstance();
		
		if (UtilValidate.isEmpty(partyIdFrom)) {
			Debug.logError("Empty party Id", module);
			return ServiceUtil.returnError("Empty Empty party Id");	   
		}
		GenericValue party = null;
  		try{
  			party = delegator.findOne("Party",UtilMisc.toMap("partyId",partyIdFrom),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching party " +partyIdFrom + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching party " + partyIdFrom);	   
  		}
        if (!hasFacilityAccess(ctx, UtilMisc.toMap("userLogin", userLogin, "party", party))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access Payments: " + partyIdFrom, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        }
		
		List condList= FastList.newInstance();
		condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
		if(UtilValidate.isNotEmpty(paramPaymentId))
		condList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, paramPaymentId));
		condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
		//condList.add(EntityCondition.makeCondition("paymentTypeId" ,EntityOperator.EQUALS, "ONACCOUNT_PAYIN"));		
		List<GenericValue> paymentList = null;
		try {
			paymentList = delegator.findList("Payment", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive paymentList ", module);
			return ServiceUtil.returnError("Failed to retrive paymentList " + e);
		}
		 
    	List paymentIds = EntityUtil.getFieldListFromEntityList(paymentList, "paymentId", true);
    	List paymentApplicationList =  FastList.newInstance();
    	try {
		condList.clear();
		condList.add(EntityCondition.makeCondition("paymentId" ,EntityOperator.IN,paymentIds));
		 paymentApplicationList = delegator.findList("PaymentApplication", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null ,false);
    	} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive PaymentApplication ", module);
			return ServiceUtil.returnError("Failed to retrive PaymentApplication " + e);
		}
		
    	 if(UtilValidate.isNotEmpty(paymentList)){
    	
		for (int i=0; i<paymentList.size(); i++) {
			
			Map tempMap = FastMap.newInstance();
			GenericValue eachPaymentList = (GenericValue) paymentList.get(i);
			String paymentId = (String) eachPaymentList.get("paymentId");
			BigDecimal paymentAmt = eachPaymentList.getBigDecimal("amount");
			List eachpaymentApplication = EntityUtil.filterByCondition(paymentApplicationList, EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, paymentId));
			BigDecimal appliedAmt = BigDecimal.ZERO;

	        if(UtilValidate.isNotEmpty(eachpaymentApplication)){
	        	
	        	for (int j=0; j<eachpaymentApplication.size(); j++) {
	        		
	        		GenericValue eachPaymentAppList = (GenericValue) eachpaymentApplication.get(j);
	    	        BigDecimal amountApplied = eachPaymentAppList.getBigDecimal("amountApplied");
	        		appliedAmt = appliedAmt.add(amountApplied);

	        	}
	            	
	        	if(paymentAmt.doubleValue() != appliedAmt.doubleValue()){
	        		
	        		tempMap.put("paymentId",paymentId);
	        		tempMap.put("paidAmount",eachPaymentList.get("amount"));
	        		tempMap.put("balanceAmount",paymentAmt.subtract(appliedAmt));
	        		tempMap.put("paymentMethodTypeId",eachPaymentList.get("paymentMethodTypeId"));
	        		
	        		
	        		
	        		String menthodType = "";
	        		try {
	        			condList.clear();
	        			condList.add(EntityCondition.makeCondition("paymentMethodTypeId" ,EntityOperator.EQUALS,eachPaymentList.get("paymentMethodTypeId")));
	        			//condList.add(EntityCondition.makeCondition("parentTypeId" ,EntityOperator.EQUALS,"MONEY"));
	        			List PaymentMethodType = delegator.findList("PaymentMethodType", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null ,false);
	        			 menthodType = (String) (EntityUtil.getFirst(PaymentMethodType)).get("description");
	        		
	        		} catch (GenericEntityException e) {
	        				Debug.logError(e, "Failed to retrive PaymentApplication ", module);
	        				return ServiceUtil.returnError("Failed to retrive PaymentApplication " + e);
	        			}
	        		
	        		tempMap.put("menthodTypeDescription",menthodType);
	        		tempMap.put("paymentDate",eachPaymentList.get("paymentDate"));
	        		tempMap.put("partyIdFrom",eachPaymentList.get("partyIdFrom"));
	        		tempMap.put("partyIdTo",eachPaymentList.get("partyIdTo"));
	        		tempMap.put("statusId",eachPaymentList.get("statusId"));
	        		
	        		paymentSearchResultsList.add(tempMap);
	        	}
	        }else{
	        	
	        	tempMap.put("paymentId",paymentId);
        		tempMap.put("paidAmount",eachPaymentList.get("amount"));
        		tempMap.put("balanceAmount",paymentAmt.subtract(appliedAmt));
        		
        		tempMap.put("paymentMethodTypeId","");
        		String menthodType = "";
        		if(UtilValidate.isNotEmpty(eachPaymentList.get("paymentMethodTypeId"))){
        			tempMap.put("paymentMethodTypeId",eachPaymentList.get("paymentMethodTypeId"));
            		try {
            			condList.clear();
            			condList.add(EntityCondition.makeCondition("paymentMethodTypeId" ,EntityOperator.EQUALS,eachPaymentList.get("paymentMethodTypeId")));
            			//condList.add(EntityCondition.makeCondition("parentTypeId" ,EntityOperator.EQUALS,"MONEY"));
            			List PaymentMethodType = delegator.findList("PaymentMethodType", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null ,false);
            			 menthodType = (String) (EntityUtil.getFirst(PaymentMethodType)).get("description");
            		
            		} catch (GenericEntityException e) {
        				Debug.logError(e, "Failed to retrive PaymentApplication ", module);
        				return ServiceUtil.returnError("Failed to retrive PaymentApplication " + e);
        			}
        		}
        		tempMap.put("paymentMethodTypeId",menthodType);
        		tempMap.put("menthodTypeDescription",menthodType);
        		tempMap.put("paymentDate",eachPaymentList.get("paymentDate"));
        		tempMap.put("partyIdFrom",eachPaymentList.get("partyIdFrom"));
        		tempMap.put("partyIdTo",eachPaymentList.get("partyIdTo"));
        		tempMap.put("statusId",eachPaymentList.get("statusId"));
        		
        		paymentSearchResultsList.add(tempMap);
	        	
	        }
		}
		
    }

		Map paymentSearchResults = FastMap.newInstance();
		paymentSearchResults.put("paymentSearchResultsList",paymentSearchResultsList);

		result.put("paymentSearchResults",paymentSearchResults);
		
        return result;
		
	}
    
    public static Map<String, Object> getSuppliers(DispatchContext ctx,Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
    	String partyId = (String) context.get("partyId");
    	String partyTypeId = (String) context.get("partyTypeId");
    	String roleTypeId = (String) context.get("roleTypeId");
    	String groupName = (String) context.get("groupName");
    	List conditionList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(partyId)){
        	conditionList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.LIKE, "%"+partyId+"%"));
        }
        if(UtilValidate.isNotEmpty(partyTypeId)){
        	conditionList.add(EntityCondition.makeCondition("partyTypeId" ,EntityOperator.EQUALS, partyTypeId));
        }
        else{
        	conditionList.add(EntityCondition.makeCondition("partyTypeId" ,EntityOperator.EQUALS,"PARTY_GROUP"));
        }
        if(UtilValidate.isNotEmpty(roleTypeId)){
        	conditionList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, roleTypeId));
        }
        else{
        	conditionList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS,"SUPPLIER"));
        }
        if(UtilValidate.isNotEmpty(groupName)){
        	conditionList.add(EntityCondition.makeCondition("groupName" ,EntityOperator.LIKE, "%"+groupName+"%"));
        }
        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"), 
					EntityOperator.OR, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));
        EntityListIterator partyListIter = null;
		try {
			partyListIter = delegator.find("PartyRoleDetailAndPartyDetail", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to get suppliersList ", module);
			return ServiceUtil.returnError("Failed to get suppliersList " + e);
		}
		GenericValue eachParty = null;
		Map suppliersMap = FastMap.newInstance();
		while( partyListIter != null && (eachParty = partyListIter.next()) != null) {
			Map partyDetail = FastMap.newInstance();
			partyDetail.put("partyId",eachParty.get("partyId"));
			partyDetail.put("groupName",eachParty.get("groupName"));
			partyDetail.put("roleTypeId",eachParty.get("roleTypeId"));
			partyDetail.put("partyTypeId",eachParty.get("partyTypeId"));
			
			Map inputMap = FastMap.newInstance();
			Map addressMap = FastMap.newInstance();
			inputMap.put("partyId", eachParty.get("partyId"));
			inputMap.put("userLogin", userLogin);
			try{
				addressMap  = dispatcher.runSync("getPartyPostalAddress", inputMap);
			} catch(Exception e){
				Debug.logError("Not a valid party", module);
			}
			String contactNumber = "";
			try{
				Map<String, Object> getTelParams = FastMap.newInstance();
	        	getTelParams.put("partyId", eachParty.get("partyId"));
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
			partyDetail.put("contactNumber",contactNumber);
			partyDetail.put("addressMap",addressMap);
			partyDetail.put("productCategory","silk");
			suppliersMap.put(eachParty.get("partyId"),partyDetail);
		}
		if (partyListIter != null) {
            try {
            	partyListIter.close();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
		result.put("suppliersMap",suppliersMap);
		result.put("suppliersMapSize",Integer.valueOf(suppliersMap.size()));
        return result;
    }
    
    public static Map<String, Object> getProducts(DispatchContext ctx,Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		
    	Timestamp salesDate = UtilDateTime.nowTimestamp();
		if (!UtilValidate.isEmpty(context.get("salesDate"))) {
			salesDate = (Timestamp) context.get("salesDate");
		}
		Timestamp dayBegin = UtilDateTime.getDayStart(salesDate);
		String productId = (String) context.get("productId");
		String primaryProductCategoryId = (String) context.get("primaryProductCategoryId");
		String productName = (String) context.get("productName");
		
		List<GenericValue> productCategory = null;
		Map<String,String> productCategoryMap = FastMap.newInstance();
		try {
			productCategory = delegator.findList("ProductCategory", null, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrieve productCategory List ", module);
			return ServiceUtil.returnError("Failed to retrieve productCategory List " + e);
		}
		if(UtilValidate.isNotEmpty(productCategory)){
			for(GenericValue eachCategory:productCategory){
				productCategoryMap.put(eachCategory.getString("productCategoryId"),eachCategory.getString("primaryParentCategoryId"));
			}
		}		
		
		List<GenericValue> productCategoryAndMember = null;
	  	try{
	  		productCategoryAndMember = delegator.findList("ProductCategoryAndMember",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), null, null, null, false);
	   	}catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive ProductCategory ", module);
			return ServiceUtil.returnError("Failed to retrive ProductCategory " + e);
		}
		
		List conditionList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(productId)){
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId));
		}
		else{
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, "_NA_"));
		}
		if(UtilValidate.isNotEmpty(primaryProductCategoryId)){
			conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.LIKE, "%"+primaryProductCategoryId+"%"));
		}
		if(UtilValidate.isNotEmpty(productName)){
			conditionList.add(EntityCondition.makeCondition("productName",EntityOperator.LIKE, "%"+productName+"%"));
		}
		conditionList.add(EntityCondition.makeCondition("isVirtual",EntityOperator.NOT_EQUAL, "Y"));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS, null), EntityOperator.OR,
				EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN, dayBegin)));
		EntityListIterator productListIter = null;
		try {
			productListIter = delegator.find("Product", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to get products List ", module);
			return ServiceUtil.returnError("Failed to get products List " + e);
		}
		GenericValue eachProduct = null;
		Map productsMap = FastMap.newInstance();
		while(productListIter != null && (eachProduct = productListIter.next()) != null) {
			Map schemeCategoryMap = FastMap.newInstance();
			String  primaryParentCategoryId = "";
			if(UtilValidate.isNotEmpty(eachProduct.get("primaryProductCategoryId"))){
				primaryParentCategoryId = (String)productCategoryMap.get(eachProduct.get("primaryProductCategoryId"));
			}
			List<GenericValue> filteredProds = EntityUtil.filterByCondition(productCategoryAndMember,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,eachProduct.get("productId")));
			if(UtilValidate.isNotEmpty(filteredProds)){
				for(GenericValue eachValue:filteredProds){
					Map schemeDetail = FastMap.newInstance();
					schemeDetail.put("productCategoryId",eachValue.getString("productCategoryId"));
					schemeDetail.put("categoryName",eachValue.getString("categoryName"));
					schemeDetail.put("description",eachValue.getString("description"));
					schemeCategoryMap.put(eachValue.getString("productCategoryId"),schemeDetail);
				}
			}
			Map productDetail = FastMap.newInstance();
			productDetail.put("productId",eachProduct.get("productId"));
			productDetail.put("productTypeId",eachProduct.get("productTypeId"));
			productDetail.put("primaryProductCategoryId",eachProduct.get("primaryProductCategoryId"));
			productDetail.put("internalName",eachProduct.get("internalName"));
			productDetail.put("brandName",eachProduct.get("brandName"));
			productDetail.put("productName",eachProduct.get("productName"));
			productDetail.put("description",eachProduct.get("description"));
			productDetail.put("quantityUomId",eachProduct.get("quantityUomId"));
			productDetail.put("quantityIncluded",eachProduct.get("quantityIncluded"));
			productDetail.put("primaryParentCategoryId",primaryParentCategoryId);
			productDetail.put("schemeCategoryMap",schemeCategoryMap);
			productsMap.put(eachProduct.get("productId"),productDetail);
		}
		if (productListIter != null) {
            try {
            	productListIter.close();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
		result.put("productsMap",productsMap);
		result.put("productsMapSize",Integer.valueOf(productsMap.size()));
        return result;
    }
    
    public static Map<String, Object> createBranchSalesIndent(DispatchContext ctx,Map<String, ? extends Object> context) {
    	
    	Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess("Indent items successfully processed.");
    	
		//Locale locale = UtilHttp.getLocale(request);
		String partyId = (String) context.get("partyId");
		String supplierPartyId=(String) context.get("supplierPartyId");
		String billToCustomer = (String) context.get("partyId");
		
		Map resultMap = FastMap.newInstance();
		
		String effectiveDateStr = (String) context.get("effectiveDate");
		Debug.log("effectiveDateStr ==================="+effectiveDateStr);
		String productStoreId = (String) context.get("productStoreId");
		String referenceNo = (String) context.get("referenceNo");
		String tallyReferenceNo = (String) context.get("tallyReferenceNo");
		//String ediTallyRefNo = (String) context.get("ediTallyRefNo");
		String contactMechId = (String) context.get("contactMechId");
		String belowContactMechId = (String) context.get("newContactMechId");
		String transporterId = (String) context.get("transporterId");
		String manualQuotaStr = (String) context.get("manualQuota");

		
		String cfcId = (String) context.get("cfcId");
		if(UtilValidate.isNotEmpty(cfcId)){
			productStoreId = cfcId;
		}
		if(UtilValidate.isEmpty(productStoreId)){
			productStoreId = "PANIPAT";
			//return ServiceUtil.returnError("Branch can not be empty");
		}
		String orderTaxType = (String) context.get("orderTaxType");
		String schemeCategory = (String) context.get("schemeCategory");
		String billingType = (String) context.get("billingType");
		String schemePartyId=partyId;
		
		String orderId = (String) context.get("orderId");
		String partyGeoId = (String) context.get("partyGeoId");
		String PONumber = (String) context.get("PONumber");
		//String promotionAdjAmt = (String)context.get("promotionAdjAmt");
		String orderMessage=(String) context.get("orderMessage");
		//String disableAcctgFlag = (String) context.get("disableAcctgFlag");
		String disableAcctgFlag = "N";
		String salesChannel = (String) context.get("salesChannel");
		String onBeHalfOf = "N";
		BigDecimal manualQuota = BigDecimal.ZERO;
		
		Map processOrderContext = FastMap.newInstance();
		
		Timestamp effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		
		/*if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
			try {
				effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
				Debug.log("effectiveDate ==================="+effectiveDate);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
			}
		}
		else{
			effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}*/
		if (UtilValidate.isNotEmpty(manualQuotaStr) && !(manualQuotaStr.equals("NaN"))) {
			manualQuota =new BigDecimal(manualQuotaStr);
		}
		if (partyId == "") {
			Debug.logError("Party Id is empty", module);
			return ServiceUtil.returnError("Party Id is empty");
		}
		try{
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			if(UtilValidate.isEmpty(party)){
				Debug.logError("Not a valid party", module);
				return ServiceUtil.returnError("Not a valid party");
			}
		}catch(GenericEntityException e){
			Debug.logError("Not a valid party", module);
			return ServiceUtil.returnError("Not a valid party");
		}
		/*if(UtilValidate.isNotEmpty(context.get("estimatedDeliveryDate"))) {
			effectiveDate = (Timestamp) context.get("estimatedDeliveryDate");
		}*/
		
		List<Map<String, Object>> indentItems = (List<Map<String, Object>>) context.get("indentItems");
		Debug.log("indentItems ==================="+indentItems);
		String infoString = "processChangeIndent:: indentItems: " + indentItems;
		Debug.logInfo(infoString, module);
		if (indentItems.isEmpty()) {
			Debug.logError("No indent items found; " + infoString, module);
			return ServiceUtil.returnError("No indent items found; "+ infoString);
		}
		
		List productIds = FastList.newInstance();
		List indentProductList = FastList.newInstance();
		List indentItemProductList = FastList.newInstance();
		Map<String, Object> indentResults = FastMap.newInstance();
		Map consolMap=FastMap.newInstance();
		for (int i = 0; i < indentItems.size(); ++i) {
			Map productQtyMap = FastMap.newInstance();
			Map indentItem = indentItems.get(i);
			Debug.log("indentItem ==================="+indentItem);
			BigDecimal quantity = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty( indentItem.get("quantity"))){
				String quantityStr = (String) indentItem.get("quantity");
				try {
					quantity = new BigDecimal(quantityStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing quantity string: "+ quantityStr, module);
					return ServiceUtil.returnError("Problems parsing quantity string: "+ quantityStr);
				}
			}
			
			BigDecimal baleQuantity = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty( indentItem.get("baleQuantity"))){
				String baleQuantityStr =  (String) indentItem.get("baleQuantity");
				try {
					baleQuantity = new BigDecimal(baleQuantityStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing quantity string: "+ baleQuantityStr, module);
					return ServiceUtil.returnError("Problems parsing quantity string: "+ baleQuantityStr);
				}
			}
			
			BigDecimal bundleWeight = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty( indentItem.get("bundleWeight"))){
				String bundleWeightStr =  (String) indentItem.get("bundleWeight");
				try {
					bundleWeight = new BigDecimal(bundleWeightStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing bundleWeight string: "+ bundleWeightStr, module);
					return ServiceUtil.returnError("Problems parsing quantity string: "+ bundleWeightStr);
				}
			}
			
			String bundleUnitPrice = null;
			if(UtilValidate.isNotEmpty( indentItem.get("bundleUnitPrice"))){
				bundleUnitPrice =  (String) indentItem.get("bundleUnitPrice");
				/*try {
					bundleUnitPrice = new BigDecimal(bundleUnitPriceStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing bundleUnitPrice string: "+ bundleUnitPriceStr, module);
					return ServiceUtil.returnError("Problems parsing quantity string: "+ bundleUnitPriceStr);
				}*/
			}
			
			BigDecimal basicPrice = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty( indentItem.get("basicPrice"))){
				String basicPriceStr =  (String) indentItem.get("basicPrice");
				try {
					basicPrice = new BigDecimal(basicPriceStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing basicPrice string: "+ basicPriceStr, module);
					return ServiceUtil.returnError("Problems parsing quantity string: "+ basicPriceStr);
				}
			}
			
			BigDecimal serviceCharge = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty( indentItem.get("serviceCharge"))){
				String serviceChargeStr =  (String) indentItem.get("serviceCharge");
				try {
					serviceCharge = new BigDecimal(serviceChargeStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing serviceCharge string: "+ serviceChargeStr, module);
					return ServiceUtil.returnError("Problems parsing quantity string: "+ serviceChargeStr);
				}
			}
			
			BigDecimal serviceChargeAmt = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty( indentItem.get("serviceChargeAmt"))){
				String serviceChargeAmtStr =  (String) indentItem.get("serviceChargeAmt");
				try {
					serviceChargeAmt = new BigDecimal(serviceChargeAmtStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing serviceChargeAmt string: "+ serviceChargeAmtStr, module);
					return ServiceUtil.returnError("Problems parsing serviceChargeAmt string: "+ serviceChargeAmtStr);
				}
			}
			
			String productId = (String)indentItem.get("productId");
			Debug.log("productId ==================="+productId);
			Debug.log("quantity ==================="+quantity);
			Debug.log("basicPrice ==================="+basicPrice);
			
			//consolidation logic
			if (UtilValidate.isNotEmpty(indentItem.get("customerId"))) {
				String customerId = (String) indentItem.get("customerId");
				onBeHalfOf="Y";
				if (UtilValidate.isNotEmpty(consolMap.get(productId))){
					
					BigDecimal tempbaleqty=BigDecimal.ZERO;
					BigDecimal tempquantity=BigDecimal.ZERO;
					BigDecimal tempbundleWeight=BigDecimal.ZERO;
					Map tempconsolMap=(Map)consolMap.get(productId);
					tempbaleqty=baleQuantity.add((BigDecimal)tempconsolMap.get("baleQuantity"));
					tempbundleWeight=bundleWeight.add((BigDecimal)tempconsolMap.get("bundleWeight"));
					tempquantity=quantity.add((BigDecimal)tempconsolMap.get("quantity"));
					tempconsolMap.put("quantity",tempquantity);
					tempconsolMap.put("baleQuantity",tempbaleqty);
					tempconsolMap.put("bundleWeight",tempbundleWeight);
				}else{
					Map tempconsolMap= FastMap.newInstance();
					tempconsolMap.put("productId", productId);
					tempconsolMap.put("quantity", quantity);
					//tempconsolMap.put("customerId", customerId);
					tempconsolMap.put("remarks", indentItem.get("remarks"));
					tempconsolMap.put("baleQuantity", baleQuantity);
					tempconsolMap.put("bundleWeight", bundleWeight);
					tempconsolMap.put("bundleUnitPrice", bundleUnitPrice);				
					tempconsolMap.put("yarnUOM", indentItem.get("yarnUOM"));
					tempconsolMap.put("baleQuantity", baleQuantity);
					tempconsolMap.put("bundleWeight", bundleWeight);
					tempconsolMap.put("basicPrice", basicPrice);
					tempconsolMap.put("taxRateList", indentItem.get("taxRateList"));
					tempconsolMap.put("serviceCharge", serviceCharge);
					tempconsolMap.put("serviceChargeAmt", serviceChargeAmt);
					tempconsolMap.put("applicableTaxType", indentItem.get("applicableTaxType"));
					tempconsolMap.put("checkE2Form", indentItem.get("checkE2Form"));
					tempconsolMap.put("checkCForm", indentItem.get("checkCForm"));
					//tempconsolMap.put("quotaAvbl", quotaAvbl);
					consolMap.put(productId,tempconsolMap);						
				}
			}
			
			productIds.add(productId);
			productQtyMap.put("productId", indentItem.get("productId"));
			productQtyMap.put("quantity", quantity);
			productQtyMap.put("customerId", indentItem.get("customerId"));
			productQtyMap.put("remarks", indentItem.get("remarks") );
			productQtyMap.put("baleQuantity", baleQuantity);
			productQtyMap.put("bundleWeight", bundleWeight);
			productQtyMap.put("bundleUnitPrice", bundleUnitPrice);				
			productQtyMap.put("yarnUOM", indentItem.get("yarnUOM"));
			productQtyMap.put("basicPrice", basicPrice);
			productQtyMap.put("taxRateList", indentItem.get("taxRateList"));
			productQtyMap.put("serviceCharge", serviceCharge);
			productQtyMap.put("serviceChargeAmt", serviceChargeAmt);
			
			productQtyMap.put("applicableTaxType", indentItem.get("applicableTaxType"));
			productQtyMap.put("checkE2Form", indentItem.get("checkE2Form"));
			productQtyMap.put("checkCForm", indentItem.get("checkCForm"));
			//productQtyMap.put("quotaAvbl", indentItem.get("quotaAvbl"));
			
			indentProductList.add(productQtyMap);
			indentItemProductList.add(productQtyMap);
		}// end of loop
		
		if("Y".equals(onBeHalfOf)){
			indentProductList.clear();
			Iterator eachProductIter = consolMap.entrySet().iterator();
	       	 
	       	 while (eachProductIter.hasNext()) {
	       		Map.Entry entry = (Entry)eachProductIter.next();
				//String productId = (String)entry.getKey();
				Map eachproductMap=(Map)entry.getValue();
				indentProductList.add(eachproductMap);
			}			
		}
		
		
		Debug.log("indentItemProductList ==================="+indentItemProductList);
		Debug.log("indentProductList ==================="+indentProductList);
		
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("onBeHalfOf", onBeHalfOf);
		processOrderContext.put("schemeCategory", schemeCategory);
		processOrderContext.put("productQtyList", indentProductList);
		processOrderContext.put("indentItemProductList", indentItemProductList);
		processOrderContext.put("partyId", partyId);
		processOrderContext.put("schemePartyId", schemePartyId);
		processOrderContext.put("supplierPartyId", supplierPartyId);
		processOrderContext.put("billToCustomer", billToCustomer);
		processOrderContext.put("contactMechId", contactMechId);
		processOrderContext.put("belowContactMechId", belowContactMechId);
		processOrderContext.put("transporterId", transporterId);
		processOrderContext.put("productIds", productIds);
		processOrderContext.put("supplyDate", effectiveDate);
		processOrderContext.put("salesChannel", salesChannel);
		processOrderContext.put("orderTaxType", orderTaxType);
		processOrderContext.put("orderId", orderId);
		processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
		processOrderContext.put("productStoreId", productStoreId);
		processOrderContext.put("referenceNo", referenceNo);
		processOrderContext.put("tallyRefNo", tallyReferenceNo);
		//processOrderContext.put("ediTallyRefNo", ediTallyRefNo);
		processOrderContext.put("PONumber", PONumber);
		//processOrderContext.put("promotionAdjAmt", promotionAdjAmt);
		processOrderContext.put("orderMessage", orderMessage);
		//processOrderContext.put("orderAdjChargesList", orderAdjChargesList);
		processOrderContext.put("disableAcctgFlag", disableAcctgFlag);
		processOrderContext.put("manualQuota", manualQuota);
		Map svcResult = FastMap.newInstance(); 
		Debug.log("processOrderContext ==================="+processOrderContext);
		try{
			//in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)
			svcResult = in.vasista.vbiz.depotsales.DepotSalesServices.processBranchSalesOrder(ctx, processOrderContext);
			Debug.log("svcResult ==================="+svcResult);
			if(ServiceUtil.isError(svcResult)){
				Debug.logError("Unable to generate order  For party :" + partyId, module);
				return ServiceUtil.returnError("Unable to generate order  For party :" + partyId);
			}
		
			orderId = (String)svcResult.get("orderId");
			if(UtilValidate.isEmpty(orderId)){
				Debug.logError("Unable to generate order  For party :" + partyId, module);
				return ServiceUtil.returnError("Unable to generate order  For party :" + partyId);
			}
			if(UtilValidate.isNotEmpty(billingType) && billingType.equals("onBehalfOf")){
				 Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId, "partyId", schemePartyId, "roleTypeId", "ON_BEHALF_OF");
				 try {
					 List conditions = FastList.newInstance();
					 conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					 conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ON_BEHALF_OF"));
					 List <GenericValue> orderRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
					 if(UtilValidate.isEmpty(orderRoles)){
						 GenericValue value = delegator.makeValue("OrderRole", fields);
						 delegator.create(value);
					 }
				 } catch (GenericEntityException e) {
					 Debug.logError(e, "Could not add role to order for OnBeHalf", module);
					 return ServiceUtil.returnError("Could not add role to order for OnBeHalf Party" + schemePartyId);
				 }
			}
		
			if(UtilValidate.isNotEmpty(supplierPartyId)){
				try{
					GenericValue supplierOrderRole	=delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", supplierPartyId, "roleTypeId", "SUPPLIER"));
					delegator.createOrStore(supplierOrderRole);
				}catch (Exception e) {
					Debug.logError(e, "Error While Creating OrderRole(SUPPLIER)  for  Sale Indent ", module);
					return ServiceUtil.returnError("Error While Creating OrderRole(SUPPLIER)  for Sale Indent  : "+orderId);
		  	 	}
			}
			if(UtilValidate.isNotEmpty(orderTaxType)){
				try{
					GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
					orderAttribute.set("orderId", orderId);
					orderAttribute.set("attrName", "INDET_TAXTYPE");
					orderAttribute.set("attrValue", orderTaxType);
					delegator.createOrStore(orderAttribute);
				}catch (GenericEntityException e) {
					Debug.logError(" Could not add Attribute tax type", module);
					return ServiceUtil.returnError(" Could not add Attribute tax type");
				}
			}
			if(UtilValidate.isNotEmpty(schemeCategory)){
				try{
					GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
					orderAttribute.set("orderId", orderId);
					orderAttribute.set("attrName", "SCHEME_CAT");
					orderAttribute.set("attrValue", schemeCategory);
					delegator.createOrStore(orderAttribute);
				}catch (GenericEntityException e) {
					Debug.logError(" Could not add Attribute SchemeCategory", module);
					return ServiceUtil.returnError(" Could not add Attribute SchemeCategory");
				}
			}
			Map resultCtx = FastMap.newInstance();
			resultCtx = dispatcher.runSync("createOrderHeaderSequence",UtilMisc.toMap("orderId", orderId ,"userLogin",userLogin, "orderHeaderSequenceTypeId","DEPOT_SALE_SEQUENCE"));
			if(ServiceUtil.isError(resultCtx)){
				Debug.logError("Problem while Creating  Sequence for orderId:"+orderId, module);
				return ServiceUtil.returnError("Problem while Creating  Sequence for orderId:"+orderId);
			}
		}catch(Exception e){
			Debug.logError("Order Creation Failed", module);
			return ServiceUtil.returnError("Order Creation Failed");
		}
		indentResults.put("numIndentItems", indentProductList.size());
		indentResults.put("orderId", orderId);
		
		GenericValue OrderHeader = null;
		try{
			OrderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId",orderId),false);
		} catch(GenericEntityException e){
  			Debug.logWarning("Error fetching order " +orderId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching Order " + orderId);	   
  		}
		BigDecimal amountToPay = OrderHeader.getBigDecimal("grandTotal");
		indentResults.put("amount",amountToPay);
		result.put("indentResults", indentResults);		
  		return result;  
		
	}
    
    public static Map<String, Object> getWeaverDetails(DispatchContext ctx,Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Empty party Id", module);
			return ServiceUtil.returnError("Empty Empty party Id");	   
		}
		GenericValue party = null;
  		try{
  			party = delegator.findOne("Party",UtilMisc.toMap("partyId",partyId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching party " +partyId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching party " + partyId);	   
  		}
        if (!hasFacilityAccess(ctx, UtilMisc.toMap("userLogin", userLogin, "party", party))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access Weaver Details: " + partyId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        }
		
		Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");
		String partyName = "";
		String partyType = "";
		String passBookNo = "";
		String issueDate = "";
		String isDepot = "NO";
		String DOA = "";
		BigDecimal totalLooms = BigDecimal.ZERO;
		
		Map inputMap = FastMap.newInstance();
		Map addressMap = FastMap.newInstance();
		inputMap.put("partyId", partyId);
		inputMap.put("userLogin", userLogin);
		try{
			addressMap  = dispatcher.runSync("getPartyPostalAddress", inputMap);
		} catch(Exception e){
			Debug.logError("Not a valid party", module);
			return ServiceUtil.returnError("Not a valid party");
		}
		
		partyName = PartyHelper.getPartyName(delegator, partyId, false);
		try{
			GenericValue partyIdentification = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", "PSB_NUMER"), false);
			if(UtilValidate.isEmpty(partyIdentification)){
				Debug.logError("Not a valid party", module);
				//return ServiceUtil.returnError("Not a valid party");
			}
			passBookNo = partyIdentification.getString("idValue");
			issueDate = partyIdentification.getString("issueDate");
			
			issueDate = UtilDateTime.toDateString(UtilDateTime.toTimestamp(partyIdentification.getDate("issueDate")),"dd-MM-yyyy");
		}catch(GenericEntityException e){
			Debug.logError("Not a valid party", module);
			return ServiceUtil.returnError("Not a valid party");
		}
		
		try {
            List<GenericValue> facility = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS , partyId), null, null, null, false);
            if(UtilValidate.isNotEmpty(facility)){
            	GenericValue facilityDetail = EntityUtil.getFirst(facility);
            	isDepot = "YES";
            	DOA = UtilDateTime.toDateString(facilityDetail.getTimestamp("openedDate"),"dd-MM-yyyy");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
		
		
		try {
            List<GenericValue> partyClassification = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , partyId), null, null, null, false);
            if(UtilValidate.isNotEmpty(partyClassification)){
            	GenericValue partyDetail = EntityUtil.getFirst(partyClassification);
            	GenericValue partyClassificationGroup = delegator.findOne("PartyClassificationGroup",UtilMisc.toMap("partyClassificationGroupId", partyDetail.getString("partyClassificationGroupId")), false);
            	if(UtilValidate.isNotEmpty(partyClassificationGroup)){
            		partyType = partyClassificationGroup.getString("description");
            	}
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
		
		
		List<GenericValue> loomTypes = null;	
		try {
			loomTypes = delegator.findList("LoomType",null,null,null,null,false);
		} catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
		List<GenericValue> partyLoomDetails = null;
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate), 
	  					EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
			
			partyLoomDetails = delegator.findList("PartyLoom",EntityCondition.makeCondition(conditionList, EntityOperator.AND),null,null,null,false);
		} catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
		
		Map resultCtx = FastMap.newInstance();
		Map productCategoryQuotasMap = FastMap.newInstance();
		Map usedQuotaMap = FastMap.newInstance();
		Map eligibleQuota = FastMap.newInstance();
		try {
			resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId,"effectiveDate",effectiveDate));
		} catch(Exception e){
			Debug.logError("Problem while getting Quota details with:"+partyId, module);
			return ServiceUtil.returnError("Problem while getting Quota details with partyId:"+partyId);
		}
		productCategoryQuotasMap = (Map)resultCtx.get("schemesMap");
		usedQuotaMap = (Map)resultCtx.get("usedQuotaMap");
		eligibleQuota = (Map)resultCtx.get("eligibleQuota");
		
		
		Map loomDetails = FastMap.newInstance();
		for(GenericValue eachLoomType:loomTypes){
			String loomTypeId = eachLoomType.getString("loomTypeId");
			String description = eachLoomType.getString("description");
			BigDecimal loomQty = BigDecimal.ZERO;
			BigDecimal loomQuota = BigDecimal.ZERO;
			BigDecimal avlQuota = BigDecimal.ZERO;
			BigDecimal usedQuota = BigDecimal.ZERO;
			List<GenericValue> filteredPartyLooms = EntityUtil.filterByCondition(partyLoomDetails,EntityCondition.makeCondition("loomTypeId",EntityOperator.EQUALS,loomTypeId));
			for(GenericValue eachPartyLoom:filteredPartyLooms){
				loomQty = eachPartyLoom.getBigDecimal("quantity");
			}
			totalLooms = totalLooms.add(loomQty);
			loomQuota = (BigDecimal) eligibleQuota.get(loomTypeId);
			avlQuota = (BigDecimal) productCategoryQuotasMap.get(loomTypeId);
			usedQuota = (BigDecimal) usedQuotaMap.get(loomTypeId);
			Map loomDetailMap = FastMap.newInstance();
			loomDetailMap.put("loomTypeId",loomTypeId);
			loomDetailMap.put("description",description);
			loomDetailMap.put("loomQty",loomQty.setScale(decimals, rounding).intValue());
			loomDetailMap.put("loomQuota",loomQuota.setScale(decimals, rounding).intValue());
			loomDetailMap.put("avlQuota",avlQuota.setScale(decimals, rounding).intValue());
			loomDetailMap.put("usedQuota",usedQuota.setScale(decimals, rounding).intValue());
			loomDetails.put(eachLoomType.getString("loomTypeId"),loomDetailMap);
		}
		
		Map customerBranch = FastMap.newInstance();
		try {
			customerBranch = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId));
		} catch(Exception e){
			Debug.logError("Problem while getting Customer Branch details with:"+partyId, module);
			return ServiceUtil.returnError("Problem while getting Customer Branch details with partyId:"+partyId);
		}
		List<GenericValue> productStoreList = (List)customerBranch.get("productStoreList");
		List customerBranchList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(productStoreList)){
			for(GenericValue eachProdStore:productStoreList){
				/*Map tempMap = FastMap.newInstance();
				tempMap.put("productStoreId",eachProdStore.getString("productStoreId"));
				tempMap.put("storeName",eachProdStore.getString("storeName"));
				tempMap.put("companyName",eachProdStore.getString("companyName"));
				tempMap.put("title",eachProdStore.getString("title"));
				tempMap.put("payToPartyId",eachProdStore.getString("payToPartyId"));
				customerBranchMap.put(eachProdStore.getString("productStoreId"),tempMap);*/
				customerBranchList.add(eachProdStore.getString("productStoreId"));
			}
		}
		
		Map resultMap = FastMap.newInstance();
		resultMap.put("partyId",partyId);
		resultMap.put("partyName",partyName);
		resultMap.put("addressMap",addressMap);
		resultMap.put("partyType",partyType);
		resultMap.put("passBookNo",passBookNo);
		resultMap.put("issueDate",issueDate);
		resultMap.put("isDepot",isDepot);
		resultMap.put("DOA",DOA);
		resultMap.put("loomDetails",loomDetails);
		resultMap.put("totalLooms",totalLooms.setScale(decimals, rounding).intValue());
		resultMap.put("customerBranchList",customerBranchList);
		result.put("weaverDetails",resultMap);
		return result;
    }
    
    public static Map<String, Object> cancelIndent(DispatchContext ctx,Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess("Indent successfully Cancelled");
		String partyId = (String) context.get("partyId");
		String orderId = (String) context.get("orderId");
		String salesChannelEnumId = (String) context.get("salesChannelEnumId");
		
		Map inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		inputMap.put("partyId", partyId);
		inputMap.put("orderId", orderId);
		if(UtilValidate.isNotEmpty(salesChannelEnumId)){
			inputMap.put("salesChannelEnumId", salesChannelEnumId);
		}
		
		try{
			result  = dispatcher.runSync("cancelIndentOrder", inputMap);
		} catch(Exception e){
			Debug.logError("Problem occured while Cancelling Indent with: "+partyId, module);
			return ServiceUtil.returnError("Problem while Cancelling Indent with: "+partyId);
		}
		
		if (ServiceUtil.isError(result)) {
			String errMsg = ServiceUtil.getErrorMessage(result);
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
        }
		return result;
    }
    
    public static Map<String, Object> getTransporters(DispatchContext ctx,Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		Map transportersMap = FastMap.newInstance();
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS ,"BRANCH_TRANSPORTER"));
		EntityListIterator PartyRelationship = null;
		try {
			PartyRelationship = delegator.find("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to get PartyRelationship ", module);
		}
		if(UtilValidate.isNotEmpty(PartyRelationship)){
			List partyIdList = EntityUtil.getFieldListFromEntityListIterator(PartyRelationship, "partyIdTo", true);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN ,partyIdList));
			EntityListIterator partyList = null;
			try {
				partyList = delegator.find("Party", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, null);
				if(UtilValidate.isNotEmpty(partyList)){
					GenericValue eachParty;
					while( partyList != null && (eachParty = partyList.next()) != null) {
						Map transporterDetail = FastMap.newInstance();
						String partyName = PartyHelper.getPartyName(delegator, eachParty.getString("partyId"), false);
						transporterDetail.put("partyId",eachParty.getString("partyId"));
						transporterDetail.put("partyName",partyName);
						Map addressMap = FastMap.newInstance();
						Map inputMap = FastMap.newInstance();
						inputMap.put("partyId", eachParty.get("partyId"));
						inputMap.put("userLogin", userLogin);
						try{
							addressMap  = dispatcher.runSync("getPartyPostalAddress", inputMap);
						} catch(Exception e){
							Debug.logError("Not a valid party", module);
						}
						transporterDetail.put("addressMap",addressMap);
						String contactNumber = "";
						try{
							Map<String, Object> getTelParams = FastMap.newInstance();
				        	getTelParams.put("partyId", eachParty.get("partyId"));
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
						transporterDetail.put("contactNumber",contactNumber);
						transportersMap.put(eachParty.getString("partyId"),transporterDetail);
					}
					if (partyList != null) {
			            try {
			            	partyList.close();
			            } catch (GenericEntityException e) {
			                Debug.logWarning(e, module);
			            }
			        }
				}
			}
			catch(GenericEntityException e){
				Debug.logError(e, "Failed to get party list ", module);
			}
		}
		result.put("transportersMap",transportersMap);
		return result;
    }
    
    public static Map<String, Object> getIndentShipments(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Map result = ServiceUtil.returnSuccess();
        String orderId = (String)context.get("orderId");
        String poOrderId = null;
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId));
		conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
		try{
			List<GenericValue> orderAssoc = delegator.findList("OrderAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(orderAssoc)){
				poOrderId = (EntityUtil.getFirst(orderAssoc)).getString("orderId");
			}
		}catch(GenericEntityException e){
			Debug.logError(e, "Failed to get Order Assoc", module);
		}
		List<GenericValue> shipments = null;
		Map shipmentHistory = FastMap.newInstance();
		try{
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("primaryOrderId",EntityOperator.EQUALS,poOrderId));
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"SHIPMENT_CANCELLED"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			shipments = delegator.findList("Shipment",condition,null,null,null,false);
			if(UtilValidate.isNotEmpty(shipments)){
				List<GenericValue> shipReceiptList = null;
	        	try{
	        		shipReceiptList = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId), null, null, null, false);
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
				}
				for(GenericValue eachShipment:shipments){
					String shipmentId = eachShipment.getString("shipmentId");
					List custCondList = FastList.newInstance();
					custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId));
					custCondList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
					List<GenericValue> shipmentDetails = EntityUtil.filterByCondition(shipReceiptList, EntityCondition.makeCondition(custCondList, EntityOperator.AND));
					List shipmentItemList = FastList.newInstance();
					Map shipmentDetailMap = FastMap.newInstance();
					for(GenericValue eachDetail:shipmentDetails){
						Map detailMap = FastMap.newInstance();
						GenericValue orderItemDetail = null;
						try{
							orderItemDetail = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", poOrderId,"orderItemSeqId",eachDetail.get("orderItemSeqId")), false);
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
						List conditionlist = FastList.newInstance();
						conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId));
						conditionlist.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachDetail.get("orderItemSeqId")));
						conditionlist.add(EntityCondition.makeCondition("changeTypeEnumId", EntityOperator.EQUALS, "ODR_ITM_AMEND"));
						conditionlist.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO, eachShipment.get("createdDate")));
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
						shipmentItemList.add(detailMap);
					}
					shipmentDetailMap.put("shipmentItems",shipmentItemList);
					String destination = "";
					try{
						GenericValue poOrderAttr = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", poOrderId,"attrName","DST_ADDR"), false);
						if(UtilValidate.isNotEmpty(poOrderAttr)){
							destination = poOrderAttr.getString("attrValue");
						}
					}catch(GenericEntityException e){
						Debug.logError(e, module);
					}
					shipmentDetailMap.put("destination",destination);
					shipmentDetailMap.put("supplierInvoiceDate",eachShipment.get("supplierInvoiceDate"));
					shipmentDetailMap.put("supplierInvoiceId",eachShipment.getString("supplierInvoiceId"));
					shipmentDetailMap.put("lrDate",eachShipment.get("supplierInvoiceDate"));
					shipmentDetailMap.put("lrNumber",eachShipment.getString("lrNumber"));
					shipmentDetailMap.put("carrierName",eachShipment.getString("carrierName"));
					shipmentDetailMap.put("vehicleId",eachShipment.getString("vehicleId"));
					shipmentDetailMap.put("freightCharges","0");
					shipmentDetailMap.put("remarks","");				
					shipmentDetailMap.put("shipmentDate",UtilDateTime.toDateString(eachShipment.getTimestamp("estimatedShipDate"),"dd MMM yyyy"));
					shipmentHistory.put(shipmentId,shipmentDetailMap);
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		result.put("shipments",shipmentHistory);
		return result;
    }
    
    public static Map<String, Object> getDepotStock(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        Map result = ServiceUtil.returnSuccess();
        String partyId = (String)context.get("partyId");
        
        Map resultCtx = FastMap.newInstance();
        List productStoreList = FastList.newInstance();
        try{
        	resultCtx  = dispatcher.runSync("getCustomerBranch", UtilMisc.toMap("userLogin",userLogin));
        	productStoreList = (List) resultCtx.get("productStoreList");
		} catch(Exception e){
			Debug.logError("Not a valid party", module);
		}
        
        List branchList = EntityUtil.getFieldListFromEntityList(productStoreList, "payToPartyId", true);
        List conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, branchList));
        conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
        conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
        conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "BRANCH_CUSTOMER"));
        List<GenericValue> partyRelationList = null;
        try {
        	partyRelationList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("partyIdFrom"), null, false);
        } catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
        List roList = EntityUtil.getFieldListFromEntityList(partyRelationList, "partyIdFrom", true);
        conditionList.clear();
        conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, roList));
        conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
        conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
        conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "BRANCH_CUSTOMER"));
        try{
        	partyRelationList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("partyIdTo"), null, false);
        } catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
        branchList = EntityUtil.getFieldListFromEntityList(partyRelationList, "partyIdTo", true);
        
        List purorderIds = FastList.newInstance();
        conditionList.clear();
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchList));
        conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("BILL_TO_CUSTOMER")));
        List<GenericValue> OrderRoleList = null;
        try{
        	OrderRoleList = (FastList)delegator.findList("OrderRole", EntityCondition.makeCondition(conditionList, EntityOperator.AND) , null ,null, null, false );
        	purorderIds = EntityUtil.getFieldListFromEntityList(OrderRoleList, "orderId", true);
        } catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
        conditionList.clear();
        conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "DEPOT_SHIPMENT"));
        if(UtilValidate.isNotEmpty(purorderIds)){
        	conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.IN, purorderIds));
        }
        conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"GOODS_RECEIVED"));
        
        List<GenericValue> shipmentList = null;
        List shipmentIds = FastList.newInstance();
        try{
        	shipmentList = delegator.findList("Shipment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("shipmentId"), null, false);
        } catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
        shipmentIds = EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", true);
        List shipmentIdsList = FastList.newInstance();
        List<GenericValue> shipmentListForPOInvoiceId = null;
        for (GenericValue eachShipment:shipmentList) {
      	  conditionList.clear();
      	  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, eachShipment.getString("shipmentId")));
      	  conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
      	  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
      	  try{
      		shipmentListForPOInvoiceId = delegator.findList("Invoice", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
      	  } catch (GenericEntityException e) {
  			Debug.logError(e, module);
      	  }
      	  if(UtilValidate.isNotEmpty(shipmentListForPOInvoiceId)){shipmentIdsList.add(eachShipment.getString("shipmentId"));}
      	  
        }
        conditionList.clear();
    	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"SR_ACCEPTED"));
    	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN,shipmentIdsList));
    	List<GenericValue> shipmentReceiptList = null;
    	try{
        	shipmentReceiptList = delegator.findList("ShipmentReceiptAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("receiptId"), null, false);
    	} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
    	List<GenericValue> inventoryItemIdsList = null;
    	inventoryItemIdsList= EntityUtil.getFieldListFromEntityList(shipmentReceiptList, "inventoryItemId", true);
    	
    	conditionList.clear();
    	conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
    	conditionList.add(EntityCondition.makeCondition("inventoryItemTypeId", EntityOperator.EQUALS, "NON_SERIAL_INV_ITEM"));
    	conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.IN, inventoryItemIdsList));
    	List<GenericValue> physicalInventory = null;
    	try{
    		physicalInventory = delegator.findList("InventoryItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("productId"), null, false);
    	} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
    	conditionList.clear();
    	conditionList.add(EntityCondition.makeCondition("inventoryItemTypeId", EntityOperator.EQUALS, "NON_SERIAL_INV_ITEM"));
    	conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.IN, inventoryItemIdsList));
    	
    	Map atpMap = FastMap.newInstance();
    	Map qohMap = FastMap.newInstance();
    	List productIds = FastList.newInstance();
    	for(GenericValue eachInv:physicalInventory){
    		productIds.add(eachInv.getString("productId"));
    	}
    	conditionList.clear();
    	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(physicalInventory, "facilityId", true)));
    	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
    	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
    	try{
        	productStoreList = delegator.findList("FacilityAndProductStoreFacility", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
    	} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
    	
    	Map stockMap = FastMap.newInstance();
    	for(GenericValue iter:physicalInventory){
    		Map invMap = FastMap.newInstance();
    		List<GenericValue> inventoryShipmentList = EntityUtil.filterByCondition(shipmentReceiptList, EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, iter.getString("inventoryItemId")));
    		GenericValue inventoryItem = null;		
    		try{		
    			inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", iter.getString("inventoryItemId")), false);
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    		
    		conditionList.clear();
    		conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS , iter.getString("inventoryItemId")));
    		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_EQUAL, null));
    		List<GenericValue> inventoryItemDetails = null;
    		try{
        		inventoryItemDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(conditionList, EntityOperator.AND),null, null, null, false );
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    		BigDecimal bookedQuantity = BigDecimal.ZERO;
    		conditionList.clear();
    		conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS , iter.getString("inventoryItemId")));
    		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.NOT_EQUAL, null));
    		conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.NOT_EQUAL, null));
    		List<GenericValue> tempInvDetail = null;
    		try{
    			tempInvDetail = delegator.findList("InventoryItemDetail",  EntityCondition.makeCondition(conditionList, EntityOperator.AND),null, null, null, false );
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    		
    		String specification = "";
    		if(UtilValidate.isNotEmpty(tempInvDetail)){
    			GenericValue tempDetail = EntityUtil.getFirst(tempInvDetail);
    			try{
    				GenericValue tempOrderDetail = delegator.findOne("OrderItemAttribute", UtilMisc.toMap("orderId", tempDetail.get("orderId"),"orderItemSeqId",tempDetail.get("orderItemSeqId"),"attrName","REMARKS"), false);
    				if(UtilValidate.isNotEmpty(tempOrderDetail)){
        				specification = tempOrderDetail.getString("attrValue");
        			}
    			} catch (GenericEntityException e) {
        			Debug.logError(e, module);
        		}
    			
    		}
    		invMap.put("specification", specification);
    		
    		conditionList.clear();
    		conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS , "ORDRITEM_INVENTORY_ID"));
    		conditionList.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, iter.getString("inventoryItemId")));
    		List<GenericValue>  OrderItemAttribute = null;
    		try {
    			OrderItemAttribute = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND),null, null, null, false );
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    		List relaventOrderIds = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(OrderItemAttribute)){
    			relaventOrderIds = EntityUtil.getFieldListFromEntityList(OrderItemAttribute, "orderId", true);
    		}
    		conditionList.clear();
    		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN , relaventOrderIds));
    		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
    		List<GenericValue> orderIdsWithOutCancelledList = null; 
    		try {
    			orderIdsWithOutCancelledList = delegator.findList("OrderHeader", EntityCondition.makeCondition(conditionList, EntityOperator.AND),null, null, null, false );
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    		List activeOrderIds = EntityUtil.getFieldListFromEntityList(orderIdsWithOutCancelledList, "orderId", true);
    		List bookedOrdersList = EntityUtil.filterByCondition(orderIdsWithOutCancelledList, EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, null));
    		List bookedOrderIds = EntityUtil.getFieldListFromEntityList(bookedOrdersList, "orderId", true);
    		conditionList.clear();
    		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN , bookedOrderIds));
    		List<GenericValue> OrderItemDetailList = null;
    		try {
    			OrderItemDetailList = delegator.findList("OrderItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND),null, null, null, false );
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    		 
    		
    		for (GenericValue eachOrderItem:OrderItemDetailList) {
    			bookedQuantity = bookedQuantity.add(eachOrderItem.getBigDecimal("quantity"));
    		}
    		
    		invMap.put("bookedQuantity", bookedQuantity.setScale(decimals, rounding));
    		
    		BigDecimal avalQty = (iter.getBigDecimal("quantityOnHandTotal")).subtract(bookedQuantity);
    		invMap.put("qty", avalQty.setScale(decimals, rounding));	
    		
    		String uom ="";
    		BigDecimal bundleWeight = BigDecimal.ZERO;
    		BigDecimal bundleUnitPrice = BigDecimal.ZERO;
    		
    		if(UtilValidate.isNotEmpty(inventoryItemDetails)){
    		   GenericValue inventoryItemDet = EntityUtil.getFirst(inventoryItemDetails);
    		   uom =inventoryItemDet.getString("uom");
    		   bundleWeight = inventoryItemDet.getBigDecimal("bundleWeight");
    		   bundleUnitPrice = inventoryItemDet.getBigDecimal("bundleUnitPrice");
    		}
    		invMap.put("uom", uom);
    		invMap.put("bundleWeight", bundleWeight.setScale(decimals, rounding));
    		invMap.put("bundleUnitPrice", bundleUnitPrice.setScale(decimals, rounding));
    		List<GenericValue> inventoryProdStore = EntityUtil.filterByCondition(productStoreList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, inventoryItem.getString("facilityId")));
    		GenericValue shipmentReceiptEach = EntityUtil.getFirst(inventoryShipmentList);
    		String shipmentId = "";
    		if(UtilValidate.isNotEmpty(shipmentReceiptEach)) {
    			invMap.put("shipmentId", shipmentReceiptEach.getString("shipmentId"));
    			shipmentId = shipmentReceiptEach.getString("shipmentId");
    			try {
	    			GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentReceiptEach.getString("shipmentId")), false);
	    			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", inventoryItem.getString("facilityId")), false);
	    			
	    			
	    			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", iter.getString("productId")), false);
	    			String partyName=PartyHelper.getPartyName(delegator, shipment.getString("partyIdFrom"), false);
	    			invMap.put("shipmentTypeId", shipment.getString("shipmentTypeId"));
	    			invMap.put("fromPartyId", shipment.getString("partyIdFrom"));
	    			String poRefNum = "";
	    			GenericValue orderAttributes = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId",shipment.getString("primaryOrderId"),"attrName","REF_NUMBER"), false);
	    			
	    			if(UtilValidate.isNotEmpty(orderAttributes)){
	    				poRefNum = orderAttributes.getString("attrValue");
	    			}
	    			
	    			conditionList.clear();
	    			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipment.getString("primaryOrderId")));
	    			conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"BILL_TO_CUSTOMER"));
	    			List<GenericValue> OrderRole= delegator.findList("OrderRole", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	    			GenericValue OrderRoleValue = EntityUtil.getFirst(OrderRole);
	    			String branchId = OrderRoleValue.getString("partyId");
	    			invMap.put("productStoreId", branchId);
	    			
	    			GenericValue PartyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", branchId), false);
	    			
	    			String branchName = PartyGroup.getString("groupName");
	    			
	    			invMap.put("branchName", branchName);
	    			
	    			invMap.put("poRefNum", poRefNum);
	    			invMap.put("facilityId", inventoryItem.getString("facilityId"));
	    			
	    			if(UtilValidate.isNotEmpty(inventoryProdStore)){
	    				GenericValue inventoryProdStoreRecord =  EntityUtil.getFirst(inventoryProdStore);
	    				invMap.put("branchId", inventoryProdStoreRecord.getString("productStoreId"));
	    			}
	    			
	    			invMap.put("Depot", facility.getString("facilityName"));
	    			invMap.put("supplier", partyName);
	    			invMap.put("productName", product.getString("productName"));
	    			invMap.put("estimatedShipDate", shipment.getString("estimatedShipDate"));
	    			BigDecimal price = iter.getBigDecimal("unitCost");
	    			invMap.put("price",price.setScale(decimals, rounding));
	    			invMap.put("productId",iter.getString("productId"));
	    			invMap.put("inventoryId",iter.getString("inventoryItemId"));
				} catch (GenericEntityException e) {
        			Debug.logError(e, module);
        		}
    		}else{
    			invMap.put("shipmentId", "");
    		}
    		if (avalQty.compareTo(BigDecimal.ZERO) > 0){
    			stockMap.put(shipmentId,invMap);
    		}
    	}
    	result.put("stockMap",stockMap);
        return result;
    }
}
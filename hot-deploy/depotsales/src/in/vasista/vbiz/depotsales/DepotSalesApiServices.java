package in.vasista.vbiz.depotsales;
import java.text.DateFormat;

import in.vasista.vbiz.depotsales.DepotPurchaseServices;
import in.vasista.vbiz.depotsales.DepotHelperServices;

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
	        orderHeaderList = delegator.find("OrderHeader", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, null);
	    } catch (GenericEntityException e) {
			Debug.logError(e, module);
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
    		GenericValue orderAss = null; 
    		try{
    			orderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    		}
    	
    		String POorder="NA";
    		String isgeneratedPO="N";
    		if(UtilValidate.isNotEmpty(orderAss)){
    			POorder=(String)orderAss.get("orderId");
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
	    	tempData.put("statusId", eachHeader.get("statusId"));
    	
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
	    	orderList.add(tempData);
	    }
		
        if (orderHeaderList != null) {
            try {
            	orderHeaderList.close();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        
        Debug.log("orderList =========== "+orderList);
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

				
		List condList= FastList.newInstance();
		condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
		if(UtilValidate.isNotEmpty(paramPaymentId))
		condList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, paramPaymentId));
		condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
		
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
			partyDetail.put("addressMap",addressMap);
			
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
			
			BigDecimal bundleUnitPrice = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty( indentItem.get("bundleUnitPrice"))){
				String bundleUnitPriceStr =  (String) indentItem.get("bundleUnitPrice");
				try {
					bundleUnitPrice = new BigDecimal(bundleUnitPriceStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing bundleUnitPrice string: "+ bundleUnitPriceStr, module);
					return ServiceUtil.returnError("Problems parsing quantity string: "+ bundleUnitPriceStr);
				}
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
		result.put("indentResults", indentResults);		
  		return result;  
		
	}
    
    public static Map<String, Object> getWeaverDetails(DispatchContext ctx,Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
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
				return ServiceUtil.returnError("Not a valid party");
			}
			passBookNo = partyIdentification.getString("idValue");
			issueDate = partyIdentification.getString("issueDate");
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
			partyLoomDetails = delegator.findList("PartyLoom",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId),null,null,null,false);
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
				totalLooms = totalLooms.add(loomQty);
			}
			loomQuota = (BigDecimal) eligibleQuota.get(loomTypeId);
			avlQuota = (BigDecimal) productCategoryQuotasMap.get(loomTypeId);
			usedQuota = (BigDecimal) usedQuotaMap.get(loomTypeId);
			Map loomDetailMap = FastMap.newInstance();
			loomDetailMap.put("loomTypeId",loomTypeId);
			loomDetailMap.put("description",description);
			loomDetailMap.put("loomQty",loomQty);
			loomDetailMap.put("loomQuota",loomQuota);
			loomDetailMap.put("avlQuota",avlQuota);
			loomDetailMap.put("usedQuota",usedQuota);
			loomDetails.put(eachLoomType.getString("loomTypeId"),loomDetailMap);
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
		resultMap.put("totalLooms",totalLooms);
		result.put("weaverDetails",resultMap);
		return result;
    }
    
}
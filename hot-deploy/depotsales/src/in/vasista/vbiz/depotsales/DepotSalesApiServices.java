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
	        			condList.add(EntityCondition.makeCondition("parentTypeId" ,EntityOperator.EQUALS,"MONEY"));
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
        			condList.add(EntityCondition.makeCondition("parentTypeId" ,EntityOperator.EQUALS,"MONEY"));
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
    
}
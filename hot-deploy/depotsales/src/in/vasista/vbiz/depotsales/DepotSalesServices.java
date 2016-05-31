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
import java.util.Map.Entry;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

public class DepotSalesServices{

   public static final String module = DepotSalesServices.class.getName();
   private static int decimals;
   private static int rounding;
    public static final String resource = "AccountingUiLabels";
    
    static {
        decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        //if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
    public static Map<String, Object> approveDepotOrder(DispatchContext dctx, Map context) {
  		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
  		LocalDispatcher dispatcher = dctx.getDispatcher();
  		Map<String, Object> result = ServiceUtil.returnSuccess();
  		GenericValue userLogin = (GenericValue) context.get("userLogin");
  		String salesChannelEnumId = (String) context.get("salesChannelEnumId");
  		String partyId=(String) context.get("partyId");
  		String orderId = (String) context.get("orderId");
  		Locale locale = (Locale) context.get("locale");
  		String smsContent = "";
  		String productDetails="";
  		 String POOrderId="";
  		try{
  			List<GenericValue> items = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
  			for (GenericValue item : items) {
  				String productId = item.getString("productId");
  				BigDecimal qty=item.getBigDecimal("quantity");
  				String qtystrng="0";
  				String desc="";
  				if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(qty)) {
  					qtystrng=String.valueOf(qty);                     
  					GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
  					desc=product.getString("description");
  				}
  				productDetails=productDetails+" "+desc+","+qtystrng;
  				smsContent = smsContent + qtystrng + " KGs of " + desc + ",";
  			}
  		}catch(GenericEntityException ex){
  			Debug.log("Problem in fetching orderItems");
  		}
  		try{
  			List<GenericValue> creditPartRoleList=delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId",partyId,"roleTypeId","CR_INST_CUSTOMER"));
  			GenericValue creditPartyRole = EntityUtil.getFirst(creditPartRoleList);
  			 
  			if(UtilValidate.isEmpty(creditPartyRole)){
  				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), false);
  				BigDecimal orderTotal = orderHeader.getBigDecimal("grandTotal");
  				Timestamp obDate=UtilDateTime.nowTimestamp();
  				if(UtilValidate.isNotEmpty(orderHeader.getTimestamp("estimatedDeliveryDate"))){
  					obDate=	orderHeader.getTimestamp("estimatedDeliveryDate");
  					obDate=UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(obDate, 1));
  				}
  				BigDecimal arPartyOB  =BigDecimal.ZERO;
  				Map arOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , UtilMisc.toMap("userLogin", userLogin, "tillDate",obDate, "partyId",partyId)));
  				
  				if(UtilValidate.isNotEmpty(arOpeningBalanceRes)){
  					arPartyOB=(BigDecimal)arOpeningBalanceRes.get("openingBalance");
  					if (arPartyOB.compareTo(BigDecimal.ZERO) < 0) {
  						arPartyOB=arPartyOB.negate();
  					}
  				}
  					 /*if (arPartyOB.compareTo(orderTotal) < 0) {
  						 Debug.logError("Available Balance:"+arPartyOB+" Less Than The Order Amount:"+orderTotal+" For Party:"+ partyId, module);
  						 return ServiceUtil.returnError("Available Balance:"+arPartyOB+" Less Than The Order Amount:"+orderTotal+" For Party:"+ partyId);
  					 }*/
  			}
  			boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
  			// Approving  Associated Purchase order
  			 List condList= FastList.newInstance();;
  	           condList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId));
  			   EntityCondition condExpress = EntityCondition.makeCondition(condList, EntityOperator.AND);
  			   List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", condExpress, null, null, null, false);
  			  if(UtilValidate.isNotEmpty(orderAssocList)){
  			    POOrderId = (EntityUtil.getFirst(orderAssocList)).getString("orderId");
  			 /*boolean POapproved = OrderChangeHelper.approveOrder(dispatcher, userLogin, POOrderId);*/
  	  			Map<String, Object> approvePOParams = FastMap.newInstance();
  	  		approvePOParams.put("orderId",POOrderId);
  	  		approvePOParams.put("partyId",partyId);
  	  		approvePOParams.put("statusId","ORDER_APPROVED");
  	  		approvePOParams.put("salesChannelEnumId",salesChannelEnumId);
  	  		approvePOParams.put("userLogin",userLogin);
  	  		Map approveServiceResult = dispatcher.runSync("approvePurchaseOrderWithEmail", approvePOParams);
		         if (ServiceUtil.isError(approveServiceResult)) {
		             return ServiceUtil.returnError(ServiceUtil.getErrorMessage(approveServiceResult));
		         }   
  			    
  			  }
  	         // end approving  Associated Purchase order
  	         
  			String indentApprovalMessage = UtilProperties.getMessage("ProductUiLabels", "IndentApprovalMessage", locale);
  			indentApprovalMessage = indentApprovalMessage.replaceAll("orderId", orderId);
  			indentApprovalMessage = indentApprovalMessage.replaceAll("material", smsContent);
  			Map<String, Object> getTelParams = FastMap.newInstance();
          	getTelParams.put("partyId", partyId);
              getTelParams.put("userLogin", userLogin);                    	
              Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
              if (ServiceUtil.isError(serviceResult)) {
                  return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
              } 
              String contactNumberTo = (String) serviceResult.get("contactNumber");
              String countryCode = (String) serviceResult.get("countryCode");
              if(UtilValidate.isEmpty(contactNumberTo)){
              	contactNumberTo = "9502532897";
              }
              if(UtilValidate.isNotEmpty(contactNumberTo)){
              	 if(UtilValidate.isNotEmpty(countryCode)){
              		 contactNumberTo = countryCode + contactNumberTo;
              	 }
              	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
                   sendSmsParams.put("contactNumberTo", contactNumberTo);
                   sendSmsParams.put("text", indentApprovalMessage); 
                   serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
                   if (ServiceUtil.isError(serviceResult)) {
                       Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
                       return serviceResult;
                   }
              }
              Timestamp nowTimestamp=UtilDateTime.nowTimestamp();
              String orderStatusSeqId = delegator.getNextSeqId("OrderStatus");
              GenericValue orderStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", orderStatusSeqId));
              orderStatus.set("orderId", orderId);
              orderStatus.set("statusId", "ORDER_APPROVED");
              orderStatus.set("statusDatetime", nowTimestamp);
              orderStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
			  delegator.createOrStore(orderStatus);
  	   }catch(Exception e){
  			Debug.logError(e.toString(), module);
  			return ServiceUtil.returnError(e.toString());
  		}
         result.put("salesChannelEnumId", salesChannelEnumId);
         result.put("orderId", POOrderId);
         return result;
  	}
   
   
   public static Map<String, Object> CreditapproveDepotOrder(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String salesChannelEnumId = (String) context.get("salesChannelEnumId");
		String partyId=(String) context.get("partyId");
		String orderId = (String) context.get("orderId");
		try{
			
			List<GenericValue> creditPartRoleList=delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId",partyId,"roleTypeId","CR_INST_CUSTOMER"));
			
			 GenericValue creditPartyRole = EntityUtil.getFirst(creditPartRoleList);
			 
			 if(UtilValidate.isEmpty(creditPartyRole)){
					GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), false);
					BigDecimal orderTotal = orderHeader.getBigDecimal("grandTotal");
					Timestamp obDate=UtilDateTime.nowTimestamp();
					if(UtilValidate.isNotEmpty(orderHeader.getTimestamp("estimatedDeliveryDate"))){
						obDate=	orderHeader.getTimestamp("estimatedDeliveryDate");
						obDate=UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(obDate, 1));
					}
				
					BigDecimal arPartyOB  =BigDecimal.ZERO;
					Map arOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , UtilMisc.toMap("userLogin", userLogin, "tillDate",obDate, "partyId",partyId)));
					if(UtilValidate.isNotEmpty(arOpeningBalanceRes)){
						arPartyOB=(BigDecimal)arOpeningBalanceRes.get("openingBalance");
						 if (arPartyOB.compareTo(BigDecimal.ZERO) < 0) {
							 arPartyOB=arPartyOB.negate();
						 }
					 }
					 /*if (arPartyOB.compareTo(orderTotal) < 0) {
						 Debug.logError("Available Balance:"+arPartyOB+" Less Than The Order Amount:"+orderTotal+" For Party:"+ partyId, module);
						 return ServiceUtil.returnError("Available Balance:"+arPartyOB+" Less Than The Order Amount:"+orderTotal+" For Party:"+ partyId);
					 }*/
					GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
					orderAttribute.set("orderId", orderId);
					orderAttribute.set("attrName", "CREDIT_APPROVE");
					orderAttribute.set("attrValue", "Y");
					delegator.createOrStore(orderAttribute);
			 }
           boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
           List condList= FastList.newInstance();;
           condList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId));
		   EntityCondition condExpress = EntityCondition.makeCondition(condList, EntityOperator.AND);
		   List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", condExpress, null, null, null, false);
		   String POOrderId = (EntityUtil.getFirst(orderAssocList)).getString("orderId");
//           boolean POapproved = OrderChangeHelper.approveOrder(dispatcher, userLogin, POOrderId);
		   Map<String, Object> approvePOParams = FastMap.newInstance();
 	  		approvePOParams.put("orderId",POOrderId);
 	  		approvePOParams.put("partyId",partyId);
 	  		approvePOParams.put("statusId","ORDER_APPROVED");
 	  		approvePOParams.put("salesChannelEnumId",salesChannelEnumId);
 	  		approvePOParams.put("userLogin",userLogin);
 	  		Map approveServiceResult = dispatcher.runSync("approvePurchaseOrderWithEmail", approvePOParams);
		         if (ServiceUtil.isError(approveServiceResult)) {
		             return ServiceUtil.returnError(ServiceUtil.getErrorMessage(approveServiceResult));
		         }
		         Timestamp nowTimestamp=UtilDateTime.nowTimestamp();
		         String orderStatusSeqId = delegator.getNextSeqId("OrderStatus");
	              GenericValue orderStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", orderStatusSeqId));
	              orderStatus.set("orderId", orderId);
	              orderStatus.set("statusId", "ORDER_APPROVED");
	              orderStatus.set("statusDatetime", nowTimestamp);
	              orderStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
				  delegator.createOrStore(orderStatus);     
		         
	   }catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
      result.put("salesChannelEnumId", salesChannelEnumId);
      return result;
	}
   
   public static String processDepotSaleEvent(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String partyId = (String) request.getParameter("partyId");
		String billToCustomer = (String)request.getParameter("billToCustomer");//using For Amul Sales
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String productStoreId = (String) request.getParameter("productStoreId");
		String orderTaxType = (String) request.getParameter("orderTaxType");
		String orderId = (String) request.getParameter("orderId");
		String PONumber = (String) request.getParameter("PONumber");
		String promotionAdjAmt = (String) request.getParameter("promotionAdjAmt");
		String orderMessage=(String) request.getParameter("orderMessage");
		String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
		String disableAcctgFlag = (String) request.getParameter("disableAcctgFlag");
		String subscriptionTypeId = "AM";
		String partyIdFrom = "";
		String shipmentId = "";
		Map processOrderContext = FastMap.newInstance();
		String salesChannel = (String)request.getParameter("salesChannel");
		if(UtilValidate.isEmpty(productSubscriptionTypeId)){
			productSubscriptionTypeId = "CASH";      	
		}
		
		String productId = null;
		String batchNo = null;
		String daysToStore = null;
		String quantityStr = null;
		String basicPriceStr = null;
		String vatPriceStr = null;
		String bedPriceStr = null;
		String cstPriceStr = null;
		String tcsPriceStr = null;
		String serTaxPriceStr = null;
		Timestamp effectiveDate=null;
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal basicPrice = BigDecimal.ZERO;
		BigDecimal cstPrice = BigDecimal.ZERO;
		BigDecimal tcsPrice = BigDecimal.ZERO;
		BigDecimal vatPrice = BigDecimal.ZERO;
		BigDecimal bedPrice = BigDecimal.ZERO;
		BigDecimal serviceTaxPrice = BigDecimal.ZERO;
		//percentage fields
		String bedPercentStr = null;
		String vatPercentStr = null;
		String cstPercentStr = null;
		String tcsPercentStr = null;
		String serviceTaxPercentStr = null;
		
		BigDecimal bedPercent=BigDecimal.ZERO;
		BigDecimal vatPercent=BigDecimal.ZERO;
		BigDecimal cstPercent=BigDecimal.ZERO;
		BigDecimal tcsPercent=BigDecimal.ZERO;
		BigDecimal serviceTaxPercent=BigDecimal.ZERO;
		
		
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
			return "error";
		}
		List productIds = FastList.newInstance();
		List indentProductList = FastList.newInstance();
		for (int i = 0; i < rowCount; i++) {
		  
			Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productInput= (String) paramMap.get("productId" + thisSuffix);
			//invoke if only not empty
			if (UtilValidate.isNotEmpty(productInput)) {

				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
					productIds.add(productId);
				} else {
					request.setAttribute("_ERROR_MESSAGE_",
							"Missing product id");
					return "error";
				}

				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap
							.get("quantity" + thisSuffix);
				} else {
					request.setAttribute("_ERROR_MESSAGE_",
							"Missing product quantity");
					return "error";
				}
				if (quantityStr.equals("")) {
					request.setAttribute("_ERROR_MESSAGE_",
							"Empty product quantity");
					return "error";
				}
				if (paramMap.containsKey("batchNo" + thisSuffix)) {
					batchNo = (String) paramMap.get("batchNo" + thisSuffix);
				}
				if (paramMap.containsKey("daysToStore" + thisSuffix)) {
					daysToStore = (String) paramMap.get("daysToStore"
							+ thisSuffix);
				}

				if (paramMap.containsKey("basicPrice" + thisSuffix)) {
					basicPriceStr = (String) paramMap.get("basicPrice"
							+ thisSuffix);
				}
				if (paramMap.containsKey("vatPrice" + thisSuffix)) {
					vatPriceStr = (String) paramMap
							.get("vatPrice" + thisSuffix);
				}
				if (paramMap.containsKey("bedPrice" + thisSuffix)) {
					bedPriceStr = (String) paramMap
							.get("bedPrice" + thisSuffix);
				}
				if (paramMap.containsKey("cstPrice" + thisSuffix)) {
					cstPriceStr = (String) paramMap
							.get("cstPrice" + thisSuffix);
				}
				if (paramMap.containsKey("tcsPrice" + thisSuffix)) {
					tcsPriceStr = (String) paramMap
							.get("tcsPrice" + thisSuffix);
				}
				if (paramMap.containsKey("serviceTaxPrice" + thisSuffix)) {
					serTaxPriceStr = (String) paramMap.get("serviceTaxPrice"
							+ thisSuffix);
				}

				if (paramMap.containsKey("bedPercent" + thisSuffix)) {
					bedPercentStr = (String) paramMap.get("bedPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("vatPercent" + thisSuffix)) {
					vatPercentStr = (String) paramMap.get("vatPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("cstPercent" + thisSuffix)) {
					cstPercentStr = (String) paramMap.get("cstPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("tcsPercent" + thisSuffix)) {
					tcsPercentStr = (String) paramMap.get("tcsPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("serviceTaxPercent" + thisSuffix)) {
					serviceTaxPercentStr = (String) paramMap
							.get("serviceTaxPercent" + thisSuffix);
				}

				try {
					quantity = new BigDecimal(quantityStr);
					if (UtilValidate.isNotEmpty(basicPriceStr)) {
						basicPrice = new BigDecimal(basicPriceStr);
					}
					if (UtilValidate.isNotEmpty(cstPriceStr)) {
						cstPrice = new BigDecimal(cstPriceStr);
					}
					if (UtilValidate.isNotEmpty(tcsPriceStr)) {
						tcsPrice = new BigDecimal(tcsPriceStr);
					}
					if (UtilValidate.isNotEmpty(bedPriceStr)) {
						bedPrice = new BigDecimal(bedPriceStr);
					}
					if (UtilValidate.isNotEmpty(vatPriceStr)) {
						vatPrice = new BigDecimal(vatPriceStr);
					}
					if (UtilValidate.isNotEmpty(serTaxPriceStr)) {
						serviceTaxPrice = new BigDecimal(serTaxPriceStr);
					}

					if (UtilValidate.isNotEmpty(bedPercentStr)) {
						bedPercent = new BigDecimal(bedPercentStr);
					}
					if (UtilValidate.isNotEmpty(vatPercentStr)) {
						vatPercent = new BigDecimal(vatPercentStr);
					}
					if (UtilValidate.isNotEmpty(cstPercentStr)) {
						cstPercent = new BigDecimal(cstPercentStr);
					}
					if (UtilValidate.isNotEmpty(tcsPercentStr)) {
						tcsPercent = new BigDecimal(tcsPercentStr);
					}
					if (UtilValidate.isNotEmpty(serviceTaxPercentStr)) {
						serviceTaxPercent = new BigDecimal(serviceTaxPercentStr);
					}
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing quantity string: "
							+ quantityStr, module);
					request.setAttribute("_ERROR_MESSAGE_",
							"Problems parsing quantity string: " + quantityStr);
					return "error";
				}

				productQtyMap.put("productId", productId);
				productQtyMap.put("quantity", quantity);
				productQtyMap.put("batchNo", batchNo);
				productQtyMap.put("daysToStore", daysToStore);
				productQtyMap.put("basicPrice", basicPrice);
				productQtyMap.put("bedPrice", bedPrice);
				productQtyMap.put("cstPrice", cstPrice);
				productQtyMap.put("tcsPrice", tcsPrice);
				productQtyMap.put("vatPrice", vatPrice);
				productQtyMap.put("serviceTaxPrice", serviceTaxPrice);

				productQtyMap.put("bedPercent", bedPercent);
				productQtyMap.put("vatPercent", vatPercent);
				productQtyMap.put("cstPercent", cstPercent);
				productQtyMap.put("tcsPercent", tcsPercent);
				productQtyMap.put("serviceTaxPercent", serviceTaxPercent);

				indentProductList.add(productQtyMap);

			}//end of productQty check
		}//end row count for loop
	  
		if( UtilValidate.isEmpty(indentProductList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "error";
		}
		//adding list of adjustments
		List orderAdjChargesList = FastList.newInstance();
		for (int i = 0; i < rowCount; i++) {
			Map orderAdjMap = FastMap.newInstance();
			String orderAdjTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			if (paramMap.containsKey("orderAdjTypeId" + thisSuffix)) {
				orderAdjTypeId = (String) paramMap.get("orderAdjTypeId" + thisSuffix);
			}
			
			if(UtilValidate.isNotEmpty(orderAdjTypeId)){
				if (paramMap.containsKey("adjAmt" + thisSuffix)) {
					adjAmtStr = (String) paramMap.get("adjAmt" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing Adjustment Amount");
					return "error";			  
				}
				try {
					adjAmt = new BigDecimal(adjAmtStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
				
				orderAdjMap.put("orderAdjTypeId", orderAdjTypeId);
				orderAdjMap.put("adjAmount", adjAmt);
				orderAdjChargesList.add(orderAdjMap);	

			}
			//end of adjustment check
		}
		
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("productQtyList", indentProductList);
		processOrderContext.put("partyId", partyId);
		processOrderContext.put("billToCustomer", billToCustomer);
		processOrderContext.put("productIds", productIds);
		processOrderContext.put("supplyDate", effectiveDate);
		processOrderContext.put("salesChannel", salesChannel);
		processOrderContext.put("orderTaxType", orderTaxType);
		processOrderContext.put("orderId", orderId);
		processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
		processOrderContext.put("productStoreId", productStoreId);
		processOrderContext.put("PONumber", PONumber);
		processOrderContext.put("promotionAdjAmt", promotionAdjAmt);
		processOrderContext.put("orderMessage", orderMessage);
		processOrderContext.put("orderAdjChargesList", orderAdjChargesList);
		processOrderContext.put("disableAcctgFlag", disableAcctgFlag);
		
		try{
		result = processDepotSaleOrder(dctx, processOrderContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
			return "error";
		}
		
		orderId = (String)result.get("orderId");
		if(UtilValidate.isEmpty(orderId)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
			return "error";
		}
		
		Map resultCtx = FastMap.newInstance();
		
				resultCtx = dispatcher.runSync("createOrderHeaderSequence",UtilMisc.toMap("orderId", orderId ,"userLogin",userLogin, "orderHeaderSequenceTypeId","DEPOT_SALE_SEQUENCE"));
				if(ServiceUtil.isError(resultCtx)){
					Debug.logError("Problem while Creating  Sequence for orderId:"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem while Creating  Sequence for orderId: : "+orderId);
					return "error";
				}
			}catch(Exception e){
				Debug.logError(e, module);
				return "error";
			}
		
		
		request.setAttribute("_EVENT_MESSAGE_", "Order Entry successfully for party : "+partyId);
		return "success";
	}
   
   	public static Map<String, Object> processDepotSaleOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    List<Map> productQtyList = (List) context.get("productQtyList");
	    Timestamp supplyDate = (Timestamp) context.get("supplyDate");
	    Locale locale = (Locale) context.get("locale");
	    String productStoreId = (String) context.get("productStoreId");
	  	String salesChannel = (String) context.get("salesChannel");
	  	List productIds = (List) context.get("productIds");
	  	String orderTaxType = (String) context.get("orderTaxType");
	  	String partyId = (String) context.get("partyId");
		String billToCustomer = (String) context.get("billToCustomer");
	  	String orderId = (String) context.get("orderId");
	  	String PONumber = (String) context.get("PONumber");
	  	String promotionAdjAmt = (String) context.get("promotionAdjAmt");
	  	String orderMessage = (String) context.get("orderMessage");
	  	String disableAcctgFlag = (String) context.get("disableAcctgFlag");
	  	List<Map> orderAdjChargesList = (List) context.get("orderAdjChargesList");
	  	String currencyUomId = "INR";
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		boolean isSale = Boolean.TRUE;
		boolean batchNumExists = Boolean.FALSE;
		boolean daysToStoreExists = Boolean.FALSE;
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create order without partyId: "+ partyId, module);
			return ServiceUtil.returnError("partyId is empty");
		}
		List conditionList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(orderId)){
			
			boolean indentNotChanged = true; 
			Map resultCtx = ByProductNetworkServices.getOrderDetails(dctx, UtilMisc.toMap("userLogin", userLogin, "orderId", orderId));
			Map orderDetails = (Map)resultCtx.get("orderDetails");
			List<GenericValue> extOrderItems = (List)orderDetails.get("orderItems");
			
		/*	List<Map> prevQtyList = FastList.newInstance();
			for(GenericValue extItem : extOrderItems){
				Map prevQtyMap = FastMap.newInstance();
				prevQtyMap.put("productId", extItem.getString("productId"));
				prevQtyMap.put("quantity", extItem.getBigDecimal("quantity"));
				prevQtyList.add(prevQtyMap);
			}
			if((UtilValidate.isNotEmpty(prevQtyList) &&  !prevQtyList.equals(productQtyList))){
				indentNotChanged = false;
	        }*/
			
			try{
				if(!indentNotChanged){
					result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", UtilMisc.toList(orderId),"userLogin", userLogin));
					if (ServiceUtil.isError(result)) {
						Debug.logError("Problem cancelling orders in Correction", module);	 		  		  
				 		return ServiceUtil.returnError("Problem cancelling orders in Correction");
					} 
				}
				else{
					List condList = FastList.newInstance();
					
					for(Map prodBatch : productQtyList){
						String prod = (String)prodBatch.get("productId");
						String batchNum = null;
						if(UtilValidate.isNotEmpty(prodBatch.get("batchNo"))){
							batchNum = (String)prodBatch.get("batchNo");
						}
						List<GenericValue> orderItem = EntityUtil.filterByCondition(extOrderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prod));
						if(UtilValidate.isNotEmpty(orderItem) && batchNum != null){
							String orderItemSeqId = (EntityUtil.getFirst(orderItem)).getString("orderItemSeqId");
							
							GenericValue orderItemBatch = delegator.makeValue("OrderItemAttribute");
							orderItemBatch.set("orderId", orderId);
							orderItemBatch.set("orderItemSeqId", orderItemSeqId);
							orderItemBatch.set("attrName", "batchNumber");
							orderItemBatch.set("attrValue", batchNum);
							delegator.createOrStore(orderItemBatch);
							
						}
						
					}
					
				}
				  			
			}catch (GenericServiceException e) {
				  Debug.logError(e, e.toString(), module);
				  return ServiceUtil.returnError("Problem cancelling order");
			}
			catch (GenericEntityException e1) {
				  Debug.logError(e1, e1.toString(), module);
				  return ServiceUtil.returnError("Failed fetching existing order details");
			}
		}
		GenericValue product =null;
		String productPriceTypeId = null;
		String geoTax = "";
		if(UtilValidate.isNotEmpty(orderTaxType)){
			if(orderTaxType.equals("INTER")){
				geoTax = "CST";
			}else{
				geoTax = "VAT";
			}
		}
		
		BigDecimal promoAmt = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(promotionAdjAmt)){
			promoAmt = new BigDecimal(promotionAdjAmt);
		}
		
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
		
		try {
			//get inventoryFacility details through productStore.
			String  inventoryFacilityId="";
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if(UtilValidate.isNotEmpty(productStore)){
			inventoryFacilityId=productStore.getString("inventoryFacilityId");
			}
			cart.setOrderType("SALES_ORDER");
			cart.setIsEnableAcctg("Y");
			if("Y".equals(disableAcctgFlag)){
				cart.setIsEnableAcctg("N");
			}
	        cart.setExternalId(PONumber);
	        cart.setProductStoreId(productStoreId);
			cart.setChannelType(salesChannel);
			cart.setPurposeTypeId("DEPOT_SALES");
			cart.setOrderId(orderId);
			//cart.setBillToCustomerPartyId("GCMMF");
			cart.setBillToCustomerPartyId(partyId);
			cart.setFacilityId(inventoryFacilityId);//for store inventory we need this so that inventoryItem query by this orginFacilityId
			if(UtilValidate.isNotEmpty(billToCustomer)){
				cart.setBillToCustomerPartyId(billToCustomer);
			}
			cart.setPlacingCustomerPartyId(partyId);
			cart.setShipToCustomerPartyId(partyId);
			cart.setEndUserCustomerPartyId(partyId);
			//cart.setShipmentId(shipmentId);
			cart.setEstimatedDeliveryDate(effectiveDate);
			cart.setOrderDate(effectiveDate);
			cart.setUserLogin(userLogin, dispatcher);
			//cart.setOrderMessage(orderMessage);
		} catch (Exception e) {
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}
		
		List<GenericValue> applicableTaxTypes = null;
		try {
			applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive ProductPriceType ", module);
			return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
		}
		
	  	List applTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);
	  	if(UtilValidate.isNotEmpty(geoTax)){
			if(geoTax.equals("VAT")){
				applTaxTypeList.remove("CST_SALE");
			}
			else{
				applTaxTypeList.remove("VAT_SALE");
			}
		}
	  	if(UtilValidate.isEmpty(geoTax) && UtilValidate.isNotEmpty(salesChannel) && (salesChannel.equals("INTUNIT_TR_CHANNEL") || salesChannel.equals("ICP_TRANS_CHANNEL"))){
	  		isSale = Boolean.FALSE;
		}
	  	List<GenericValue> productPriceTaxCalc = FastList.newInstance();
		List<GenericValue> prodPriceType = null;
		
	  	List condsList = FastList.newInstance();
	  	
	  	condsList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	  	condsList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.IN, applTaxTypeList));
	  	condsList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "TAX"));
		EntityCondition priceCond = EntityCondition.makeCondition(condsList,EntityOperator.AND);
		
		try {
			prodPriceType = delegator.findList("ProductPriceAndType", priceCond, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive ProductPriceType ", module);
			return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
		}
		prodPriceType = EntityUtil.filterByDate(prodPriceType, effectiveDate);

		String productId = "";
		BigDecimal quantity = BigDecimal.ZERO;
		String batchNo = "";
		String daysToStore = "";
		for (Map<String, Object> prodQtyMap : productQtyList) {
			
			BigDecimal basicPrice = BigDecimal.ZERO;
			BigDecimal bedPrice = BigDecimal.ZERO;
			BigDecimal vatPrice = BigDecimal.ZERO;
			BigDecimal cstPrice = BigDecimal.ZERO;
			BigDecimal tcsPrice = BigDecimal.ZERO;
			BigDecimal serviceTaxPrice = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
				productId = (String)prodQtyMap.get("productId");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
				quantity = (BigDecimal)prodQtyMap.get("quantity");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("batchNo"))){
				batchNo = (String)prodQtyMap.get("batchNo");
				batchNumExists = true;
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("daysToStore"))){
				daysToStore = (String)prodQtyMap.get("daysToStore");
				daysToStoreExists = true;
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("basicPrice"))){
				basicPrice = (BigDecimal)prodQtyMap.get("basicPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("bedPrice"))){
				bedPrice = (BigDecimal)prodQtyMap.get("bedPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPrice"))){
				vatPrice = (BigDecimal)prodQtyMap.get("vatPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPrice"))){
				cstPrice = (BigDecimal)prodQtyMap.get("cstPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("tcsPrice"))){
				tcsPrice = (BigDecimal)prodQtyMap.get("tcsPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("serviceTaxPrice"))){
				serviceTaxPrice = (BigDecimal)prodQtyMap.get("serviceTaxPrice");
			}
			
			//add percentages
			BigDecimal bedPercent=BigDecimal.ZERO;
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO;
			BigDecimal tcsPercent=BigDecimal.ZERO;
			BigDecimal serviceTaxPercent=BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(prodQtyMap.get("bedPercent"))){
				bedPercent = (BigDecimal)prodQtyMap.get("bedPercent");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
				vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
				cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("tcsPercent"))){
				tcsPercent = (BigDecimal)prodQtyMap.get("tcsPercent");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("serviceTaxPercent"))){
				serviceTaxPercent = (BigDecimal)prodQtyMap.get("serviceTaxPercent");
			}
			
			Map<String, Object> priceResult;
			Map<String, Object> priceContext = FastMap.newInstance();
			priceContext.put("userLogin", userLogin);
			priceContext.put("productId", productId);	
			
			priceContext.put("priceDate", effectiveDate);
			priceContext.put("geoTax", geoTax);
			priceContext.put("productStoreId", productStoreId);
			if(UtilValidate.isNotEmpty(basicPrice) && basicPrice.compareTo(BigDecimal.ZERO)>0){
				priceContext.put("basicPrice", basicPrice);
				priceContext.put("bedPrice", bedPrice);
				priceContext.put("vatPrice", vatPrice);
				priceContext.put("cstPrice", cstPrice);
				priceContext.put("tcsPrice", tcsPrice);
				priceContext.put("serviceTaxPrice", serviceTaxPrice);
				priceContext.put("bedPercent", bedPercent);
				priceContext.put("vatPercent", vatPercent);
				priceContext.put("cstPercent", cstPercent);
				priceContext.put("tcsPercent", tcsPercent);
				priceContext.put("serviceTaxPercent", serviceTaxPercent);
				priceResult = ByProductNetworkServices.calculateUserDefinedProductPrice(delegator, dispatcher, priceContext);
			}
			else{
				priceContext.put("isSale", isSale);
				priceContext.put("partyId", partyId);
				priceResult = ByProductNetworkServices.calculateStoreProductPrices(delegator, dispatcher, priceContext);
			}
			
			if (ServiceUtil.isError(priceResult)) {
				Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
				return ServiceUtil.returnError("There was an error while calculating the price");
			}
			BigDecimal totalPrice = (BigDecimal)priceResult.get("totalPrice");
			List<Map> taxList = (List)priceResult.get("taxList");
				ShoppingCartItem item = null;
				try{
					int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null,	quantity, (BigDecimal)priceResult.get("basicPrice"),
					            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
					            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
					
					item = cart.findCartItem(itemIndx);
					item.setListPrice(totalPrice);
					item.setOrderItemAttribute("INDENTQTY_FOR:"+productId+"",quantity.toString());
					item.setOrderItemAttribute("productId",productId);
					//item.setAttribute(productId,quantity);
	        		item.setTaxDetails(taxList);
				}
				catch (Exception exc) {
					Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
					return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: ");
		        }
				List<GenericValue> productTaxes = EntityUtil.filterByCondition(prodPriceType, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				for (Map eachTaxType : taxList) {
					String taxId = (String)eachTaxType.get("taxType");
					BigDecimal amount = (BigDecimal) eachTaxType.get("amount");
					List<GenericValue> productTaxTypesList = EntityUtil.filterByCondition(productTaxes, EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, taxId));
					if(UtilValidate.isNotEmpty(productTaxTypesList)){
						GenericValue prodTaxType = EntityUtil.getFirst(productTaxTypesList);
						if(UtilValidate.isNotEmpty(amount) && amount.compareTo(BigDecimal.ZERO)>0){
							prodTaxType.set("price", amount);
						}
						productPriceTaxCalc.add(prodTaxType);
					}
					else{
						GenericValue productPrice = delegator.makeValue("ProductPrice");        	 
						productPrice.set("productId", productId);
						productPrice.set("productPriceTypeId", taxId);
						productPrice.set("productPricePurposeId", "SALE_PRICE");
						productPrice.set("productStoreGroupId", "_NA_");
						productPrice.set("currencyUomId", "INR");
						productPrice.set("price", amount);
						productPriceTaxCalc.add(productPrice);
					}
					
				}
			}
		cart.setDefaultCheckoutOptions(dispatcher);
		//ProductPromoWorker.doPromotions(cart, dispatcher);
		CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
		try {
			if(isSale || UtilValidate.isNotEmpty(productPriceTaxCalc)){
				checkout.calcAndAddTax(productPriceTaxCalc);
			}
			
		} catch (Exception e1) {
		// TODO Auto-generated catch block
			Debug.logError(e1, "Error in CalcAndAddTax",module);
		}
		Map<String, Object> orderCreateResult= FastMap.newInstance();
		//if orderId empty call createOrder other wise editOrder
		if(UtilValidate.isEmpty(orderId)){
			orderCreateResult = checkout.createOrder(userLogin);
		}else{
			orderCreateResult = checkout.editOrder(userLogin);
		}
		if(UtilValidate.isNotEmpty(orderCreateResult)){
			orderId = (String) orderCreateResult.get("orderId");
		}
		if(promoAmt.compareTo(BigDecimal.ZERO)>0){
			Map promoAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			promoAdjCtx.put("orderId", orderId);
			promoAdjCtx.put("promoAdjAmt", promoAmt);
		  	 	  	 
		  	try{
		  		Map resultCtx = dispatcher.runSync("adjustPromotionAmtForOrder",promoAdjCtx);  		  		 
		  		if (ServiceUtil.isError(resultCtx)) {
		  	 		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
		  	 		Debug.logError(errMsg , module);
		  		}	
	         }catch (GenericServiceException e) {
	        	 Debug.logError(e , module);
	             return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
	         }
		}
		//creating adjustemnts by list
		if(UtilValidate.isNotEmpty(orderAdjChargesList)){
			Map inputAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			inputAdjCtx.put("orderId", orderId);
			inputAdjCtx.put("orderAdjChargesList", orderAdjChargesList);
			result = in.vasista.vbiz.byproducts.icp.ICPServices.createOrderAdjustmentByTypeList(dctx, inputAdjCtx);
			if (ServiceUtil.isError(result)) {
				Debug.logError("Unable to generate Adjustments: " + ServiceUtil.getErrorMessage(result), module);
				 return ServiceUtil.returnError(" Unable to generate Adjustments:");
	  		}	
		}
		
		if(UtilValidate.isNotEmpty(orderId) && (batchNumExists || daysToStoreExists)){
			try{
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if(geoTax.equals("CST")){
					orderHeader.set("isInterState", "N");
				}else{
					orderHeader.set("isInterState", "Y");
				}
				orderHeader.store();
				
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("orderId", "productId", "quantity", "orderItemSeqId"), null, null, false);
				for(GenericValue orderItem : orderItems){
					if(UtilValidate.isNotEmpty(productQtyList)){
						Map batchMap = (Map)productQtyList.get(0);
						GenericValue newItemAttr = delegator.makeValue("OrderItemAttribute");        	 
						newItemAttr.set("orderId", orderItem.getString("orderId"));
						newItemAttr.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
						newItemAttr.set("attrName", "batchNumber");
						newItemAttr.set("attrValue", (String)batchMap.get("batchNo"));
						newItemAttr.create();
						
						GenericValue newDayStoreAttr = delegator.makeValue("OrderItemAttribute");        	 
						newDayStoreAttr.set("orderId", orderItem.getString("orderId"));
						newDayStoreAttr.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
						newDayStoreAttr.set("attrName", "daysToStore");
						newDayStoreAttr.set("attrValue", (String)batchMap.get("daysToStore"));
						newDayStoreAttr.create();
						
						productQtyList.remove(0);
					}
				}
			}catch (GenericEntityException e) {
				Debug.logError("Error in creating shipmentId for DirectOrder", module);
				return ServiceUtil.returnError("Error in creating shipmentId for DirectOrder");
			}
			
		}
		//store OrderMessage
		if(UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(orderMessage )){
			try{
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				orderHeader.set("orderMessage", orderMessage.trim());
				orderHeader.store();
			}catch (GenericEntityException e) {
				Debug.logError("Error While Saving Order Message ", module);
				return ServiceUtil.returnError("Error While Saving Order Message");
			}
			
		}
		
		result.put("orderId", orderId);
		return result;
   }
   
   public static String processDepotSalesReturnEntry(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String partyId = (String) request.getParameter("partyId");
		String screenFlag = (String)request.getParameter("screenFlag");//using For Amul Sales
		String productStoreId = (String)request.getParameter("productStoreId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String receiveInventory = (String) request.getParameter("receiveInventory");
		String returnHeaderTypeId = (String) request.getParameter("returnHeaderTypeId");
		String subscriptionTypeId = "AM";
		String partyIdFrom = "";
		String shipmentId = "";
		String productId="";
		BigDecimal quantity=BigDecimal.ZERO;
		String returnReasonId="";
		Timestamp effectiveDate=null;
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		String quantityStr="";
		String salesChannel = (String)request.getParameter("salesChannel");

		Map<String, Object> result = ServiceUtil.returnSuccess();
		 Map<String  ,Object> returnHeaderMap = FastMap.newInstance();
		 List detailReturnsList = FastList.newInstance();
		 
		 
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
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	  		  return "success";
	  	  }
	  	
	  	  
	  	  for (int i = 0; i < rowCount; i++) {
	  		 
	  		 
	  		List<GenericValue> subscriptionProductsList = FastList.newInstance();
	  		 Map<String  ,Object> productQtyMap = FastMap.newInstance();
	  		
	  		  
	  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	  		  if (paramMap.containsKey("productId" + thisSuffix)) {
	  			  productId = (String) paramMap.get("productId" + thisSuffix);
	  			productQtyMap.put("productId", productId);
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
	  		  if (paramMap.containsKey("returnReasonId" + thisSuffix)) {
	  			returnReasonId = (String) paramMap.get("returnReasonId" + thisSuffix);
	  			productQtyMap.put("returnReasonId", returnReasonId);
	  		  }
	  		  try {
	  			  quantity = new BigDecimal(quantityStr);
	  			productQtyMap.put("quantity", quantity);
	  		  } catch (Exception e) {
	  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
	  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
	  			  return "error";
	  		  } 
	  		detailReturnsList.add(productQtyMap);
	  	}
	  	 
	  	returnHeaderMap.put("returnHeaderTypeId",returnHeaderTypeId);
  		returnHeaderMap.put("fromPartyId",partyId);
  		returnHeaderMap.put("productStoreId",productStoreId);
  		returnHeaderMap.put("effectiveDate",effectiveDate);
  		returnHeaderMap.put("needsInventoryReceive", receiveInventory);
  		returnHeaderMap.put("userLogin", userLogin);
  		returnHeaderMap.put("detailReturnsList", detailReturnsList);
	  	  
	  	try{
			result = dispatcher.runSync("createDepotSalesReturnHeader", returnHeaderMap);
	    	  if (ServiceUtil.isError(result)) {
	    		  request.setAttribute("_ERROR_MESSAGE_", "Error Occurred in Service");
	  			  return "error";	    	            
	          } 
		}
		catch (GenericServiceException e) {
			 Debug.logError(e, "Error Occured in creating Return Header: ", module);
 			  request.setAttribute("_ERROR_MESSAGE_", "Error Occured in creating Return Header: ");
 			  return "error";
		}
	  	String returnId=(String)result.get("returnId");
	  	request.setAttribute("_EVENT_MESSAGE_", "Return Successfully created for PartyId: "+partyId+", returnId: "+returnId);
		return "success";
	}
   
   
   public static Map<String, Object> createDepotSalesReturnHeader(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	    String returnHeaderTypeId= (String)context.get("returnHeaderTypeId");
	    String fromPartyId = (String)context.get("fromPartyId");
	    String productStoreId = (String)context.get("productStoreId");
	    Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");
	    String needsInventoryReceive= (String)context.get("needsInventoryReceive");
	    List detailReturnsList = (List) context.get("detailReturnsList");
	    String returnId="";
	    String geoTax = "";
	    BigDecimal paymentAmount = BigDecimal.ZERO;
	    String paymentId = "";

	    boolean isSale = Boolean.TRUE;
	   
	    if((UtilValidate.isEmpty(returnHeaderTypeId)) || UtilValidate.isEmpty(fromPartyId)){
	    	Debug.logError("Error creating ReturnHeader: " + ServiceUtil.getErrorMessage(result), module);
			  return ServiceUtil.returnError("Error creating ReturnHeader: returnHeaderTypeId or fromPartyId is Empty" + ServiceUtil.getErrorMessage(result));     
	    }
	    
	    try{
	    	GenericValue newEntryReturnHeader = delegator.makeValue("ReturnHeader");
	    	newEntryReturnHeader.set("returnHeaderTypeId", returnHeaderTypeId);
	    	newEntryReturnHeader.set("fromPartyId", fromPartyId);
	    	newEntryReturnHeader.set("toPartyId", "Company");
	    	newEntryReturnHeader.set("needsInventoryReceive", needsInventoryReceive);
	    	newEntryReturnHeader.set("statusId", "RETURN_ACCEPTED");
	    	newEntryReturnHeader.set("entryDate", nowTimestamp);
	    	newEntryReturnHeader.set("createdBy", userLogin.get("userLoginId"));
	    	delegator.createSetNextSeqId(newEntryReturnHeader);
	    	
	    	returnId = (String) newEntryReturnHeader.get("returnId");
	    	 result.put("returnId",returnId);
	    }
	    catch (Exception e) {
			  Debug.logError(e, "Problem Creating the Return Header for party " + fromPartyId, module);		  
			  return ServiceUtil.returnError("Problem Creating the Return Header for party " + fromPartyId);			  
		  }
	    
	   //Creating ReturnItem
	    try{
	    		    	
	    	Iterator<Map> itr = detailReturnsList.iterator();
	    	while (itr.hasNext()) {
	            Map detailReturn = itr.next();
	           //calculating price------------
	            Map<String, Object> priceResult;
	            Map<String, Object> priceContext = FastMap.newInstance();
				priceContext.put("userLogin", userLogin);
				priceContext.put("productId", detailReturn.get("productId"));	
				priceContext.put("priceDate", effectiveDate);
				priceContext.put("geoTax", geoTax);
				priceContext.put("productStoreId", productStoreId);
				priceContext.put("isSale", isSale);
				priceContext.put("partyId", fromPartyId);
				priceResult = ByProductNetworkServices.calculateStoreProductPrices(delegator, dispatcher, priceContext);
			if (ServiceUtil.isError(priceResult)) {
				Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
				return ServiceUtil.returnError("There was an error while calculating the price");
				}
                List taxList = (List)priceResult.get("taxList");
                BigDecimal vatPercent=BigDecimal.ZERO; 
                BigDecimal vatAmount=BigDecimal.ZERO; 
                for(int m=0;m<taxList.size(); m++){
               		 Map taxComp = (Map)taxList.get(m);
               		 String taxType= (String) taxComp.get("taxType");
               		 BigDecimal percentage = (BigDecimal) taxComp.get("percentage");
               		 BigDecimal amount = (BigDecimal) taxComp.get("amount");
               		 if(taxType.startsWith("VAT_")){
               			vatPercent = percentage;
               			vatAmount = amount;
               		 }
                }
	            //------------------------------------
	            
	            GenericValue returnItem = delegator.makeValue("ReturnItem");
	    		returnItem.put("returnReasonId", "RTN_DEFECTIVE_ITEM");
	    		if(UtilValidate.isNotEmpty(detailReturn.get("returnReasonId"))){
	    			returnItem.put("returnReasonId", detailReturn.get("returnReasonId"));
	    		}
	            returnItem.put("statusId", "RETURN_ACCEPTED");
	    		returnItem.put("returnId", returnId);
	    		returnItem.put("productId", detailReturn.get("productId"));
	    		returnItem.put("returnQuantity", detailReturn.get("quantity"));
	    		returnItem.put("returnTypeId", "RTN_REFUND");
	    		returnItem.put("returnItemTypeId", "RET_FPROD_ITEM");
	     		returnItem.put("returnPrice", priceResult.get("totalPrice"));
	     		returnItem.put("returnBasicPrice", priceResult.get("basicPrice"));
	    		returnItem.put("vatAmount", vatAmount);
	    		returnItem.put("vatPercent", vatPercent);
	   		    delegator.setNextSubSeqId(returnItem, "returnItemSeqId", 5, 1);
	    		delegator.create(returnItem);
	    		
	    		paymentAmount = paymentAmount.add(new BigDecimal((priceResult.get("totalPrice")).toString()));
	    		
	    		//creating Inventory for Damaged goods
	    		try {
	                Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("productId", detailReturn.get("productId"),
	                        "inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
	                serviceContext.put("facilityId", "DAMAGED_STORE");
	                if("RTN_INDNT_FAULT".equals(detailReturn.get("returnReasonId"))){
	                	serviceContext.put("facilityId", "STORE");
	                }
	                serviceContext.put("datetimeReceived", UtilDateTime.nowTimestamp());
	                serviceContext.put("comments", "Created by Sales Returns Damaged Goods " + returnId);
	                serviceContext.put("userLogin", userLogin);
	                serviceContext.put("unitCost", BigDecimal.ZERO);
	                serviceContext.put("userLogin", userLogin);
	                Map<String, Object> resultService = dispatcher.runSync("createInventoryItem", serviceContext);
	                String inventoryItemId = (String)resultService.get("inventoryItemId");
	               
	                serviceContext.clear();
	                serviceContext.put("inventoryItemId", inventoryItemId);
	                serviceContext.put("returnId", returnId);
	                serviceContext.put("availableToPromiseDiff", detailReturn.get("quantity"));
	                serviceContext.put("quantityOnHandDiff", detailReturn.get("quantity"));
	                serviceContext.put("userLogin", userLogin);
	                resultService = dispatcher.runSync("createInventoryItemDetail", serviceContext);
	                
	            } catch (Exception exc) {
	                return ServiceUtil.returnError(exc.getMessage());
	            }

	    	}
	    	
	    	Map paymentCtx = FastMap.newInstance();
        	paymentCtx.put("paymentTypeId", "SALES_PAYIN");
            paymentCtx.put("paymentMethodTypeId", "CREDITNOTE_PAYIN");
            paymentCtx.put("partyIdTo", "Company");
            paymentCtx.put("partyIdFrom", fromPartyId);
           
           // paymentCtx.put("isEnableAcctg", context.get("isEnableAcctg"));
            if (!UtilValidate.isEmpty(effectiveDate) ) {
                paymentCtx.put("effectiveDate", effectiveDate);                        	
            }
            paymentCtx.put("paymentDate", UtilDateTime.nowTimestamp());
            
            paymentCtx.put("statusId", "PMNT_RECEIVED");
            paymentCtx.put("paymentPurposeType", "NHDC_RECEIPT");
            paymentCtx.put("amount", paymentAmount);
            paymentCtx.put("userLogin", userLogin);
            
            paymentCtx.put("createdByUserLogin", userLogin.getString("userLoginId"));
            paymentCtx.put("createdDate", UtilDateTime.nowTimestamp());
            
            Map<String, Object> paymentResult = dispatcher.runSync("createPayment", paymentCtx);
            if (ServiceUtil.isError(paymentResult)) {
            	Debug.logError(paymentResult.toString(), module);    			
                return ServiceUtil.returnError(null, null, null, paymentResult);
            }
            paymentId = (String)paymentResult.get("paymentId");

	    }
	    catch(Exception e){
	    	 Debug.logError(e, "Problem Creating the Return Item for party " + fromPartyId, module);		  
			  return ServiceUtil.returnError("Problem Creating the Return Item for party " + fromPartyId);			
	    }

	    return result;
   }
   
   public static String processBranchSaleEvent(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String partyId = (String) request.getParameter("partyId");
		String supplierPartyId ="";
		supplierPartyId=(String) request.getParameter("suplierPartyId");
		String billToCustomer = (String)request.getParameter("billToCustomer");//using For Amul Sales
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String productStoreId = (String) request.getParameter("productStoreId");
		String referenceNo = (String) request.getParameter("referenceNo");
		String tallyReferenceNo = (String) request.getParameter("tallyReferenceNo");
		Debug.log("referenceNo ===="+referenceNo);
		String contactMechId = (String) request.getParameter("contactMechId");
		String belowContactMechId = (String) request.getParameter("belowContactMechId");
		String transporterId = (String) request.getParameter("transporterId");
		
		String cfcId = (String) request.getParameter("cfcId");
		if(UtilValidate.isNotEmpty(cfcId)){
			productStoreId = cfcId;
		}
		//String productStoreId = "STORE";
		String orderTaxType = (String) request.getParameter("orderTaxType");
		String schemeCategory = (String) request.getParameter("schemeCategory");
		String billingType = (String) request.getParameter("billingType");
		String schemePartyId=partyId;
		/*if(UtilValidate.isNotEmpty(billingType) && billingType.equals("onBehalfOf")){
			 partyId = (String) request.getParameter("societyPartyId");
			 
		}*/
		String orderId = (String) request.getParameter("orderId");
		String partyGeoId = (String) request.getParameter("partyGeoId");
		String PONumber = (String) request.getParameter("PONumber");
		String promotionAdjAmt = (String) request.getParameter("promotionAdjAmt");
		String orderMessage=(String) request.getParameter("orderMessage");
		String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
		String disableAcctgFlag = (String) request.getParameter("disableAcctgFlag");
		String subscriptionTypeId = "AM";
		String partyIdFrom = "";
		String shipmentId = "";
		Map processOrderContext = FastMap.newInstance();
		String salesChannel = (String)request.getParameter("salesChannel");
		if(UtilValidate.isEmpty(productSubscriptionTypeId)){
			productSubscriptionTypeId = "CASH";      	
		}
		
		String productId = null;
		String customerId = null;
		String remarks = null;
		String productFeatureId = null;
		String batchNo = null;
		String daysToStore = null;
		String quantityStr = null;
		String baleQuantityStr = null;
		String yarnUOMStr = null;
		String bundleWeightStr = null;
		String basicPriceStr = null;
		String bundleUnitPriceStr=null;
		/*String vatPriceStr = null;
		String bedPriceStr = null;
		String cstPriceStr = null;
		String tcsPriceStr = null;
		String serTaxPriceStr = null;*/
		String taxListStr = null;
		String serviceChgStr = null;
		String serviceChgAmtStr = null;
		
		Timestamp effectiveDate=null;
		
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal baleQuantity = BigDecimal.ZERO;
		BigDecimal bundleWeight = BigDecimal.ZERO;
		String yarnUOM="";
		BigDecimal basicPrice = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal serviceChargeAmt = BigDecimal.ZERO;
		/*BigDecimal cstPrice = BigDecimal.ZERO;
		BigDecimal tcsPrice = BigDecimal.ZERO;
		BigDecimal vatPrice = BigDecimal.ZERO;
		BigDecimal bedPrice = BigDecimal.ZERO;
		BigDecimal serviceTaxPrice = BigDecimal.ZERO;*/
		//percentage fields
		/*String bedPercentStr = null;
		String vatPercentStr = null;
		String cstPercentStr = null;
		String tcsPercentStr = null;
		String serviceTaxPercentStr = null;*/
		
		/*BigDecimal bedPercent=BigDecimal.ZERO;
		BigDecimal vatPercent=BigDecimal.ZERO;
		BigDecimal cstPercent=BigDecimal.ZERO;
		BigDecimal tcsPercent=BigDecimal.ZERO;
		BigDecimal serviceTaxPercent=BigDecimal.ZERO;*/
		
		String applicableTaxType = null;
		String checkE2Form = null;
		String checkCForm = null;
		
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
			return "error";
		}
		Debug.log("paramMap ============ "+paramMap);
		List productIds = FastList.newInstance();
		List indentProductList = FastList.newInstance();
		List indentItemProductList = FastList.newInstance();
		Map consolMap=FastMap.newInstance();
        String onBeHalfOf="N";
		for (int i = 0; i < rowCount; i++) {
		  
			List taxRateList = FastList.newInstance();
			Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productInput= (String) paramMap.get("productId" + thisSuffix);
			//invoke if only not empty
			if (UtilValidate.isNotEmpty(productInput)) {

				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
					productIds.add(productId);
				} else {
					request.setAttribute("_ERROR_MESSAGE_",
							"Missing product id");
					return "error";
				}
				if (paramMap.containsKey("customerId" + thisSuffix)) {
					customerId = (String) paramMap.get("customerId" + thisSuffix);
				}
				if (paramMap.containsKey("baleQuantity" + thisSuffix)) {
					baleQuantityStr = (String) paramMap
							.get("baleQuantity" + thisSuffix);
				} else {
					request.setAttribute("_ERROR_MESSAGE_",
							"Missing product quantity");
					return "error";
				}
				if (paramMap.containsKey("remarks" + thisSuffix)) {
					remarks = (String) paramMap.get("remarks" + thisSuffix);
				}
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap
							.get("quantity" + thisSuffix);
				} else {
					request.setAttribute("_ERROR_MESSAGE_",
							"Missing product quantity");
					return "error";
				}
				if (paramMap.containsKey("bundleWeight" + thisSuffix)) {
					bundleWeightStr = (String) paramMap
							.get("bundleWeight" + thisSuffix);
				}
				if (paramMap.containsKey("yarnUOM" + thisSuffix)) {
					yarnUOMStr = (String) paramMap
							.get("yarnUOM" + thisSuffix);
				}
				
				if (paramMap.containsKey("batchNo" + thisSuffix)) {
					batchNo = (String) paramMap.get("batchNo" + thisSuffix);
				}
				if (paramMap.containsKey("daysToStore" + thisSuffix)) {
					daysToStore = (String) paramMap.get("daysToStore"
							+ thisSuffix);
				}
				if (paramMap.containsKey("serviceCharge" + thisSuffix)) {
					serviceChgStr = (String) paramMap.get("serviceCharge"
							+ thisSuffix);
				}
				if (paramMap.containsKey("serviceChargeAmt" + thisSuffix)) {
					serviceChgAmtStr = (String) paramMap.get("serviceChargeAmt"
							+ thisSuffix);
				}
				
				if (paramMap.containsKey("applicableTaxType" + thisSuffix)) {
					applicableTaxType = (String) paramMap.get("applicableTaxType"
							+ thisSuffix);
				}
				if (paramMap.containsKey("checkE2Form" + thisSuffix)) {
					checkE2Form = (String) paramMap.get("checkE2Form"
							+ thisSuffix);
				}
				if (paramMap.containsKey("checkCForm" + thisSuffix)) {
					checkCForm = (String) paramMap.get("checkCForm"
							+ thisSuffix);
				}
				
				if (paramMap.containsKey("taxList" + thisSuffix)) {
					taxListStr = (String) paramMap.get("taxList"
							+ thisSuffix);
					
					String[] taxList = taxListStr.split(",");
					for (int j = 0; j < taxList.length; j++) {
						String taxType = taxList[j];
						Map taxRateMap = FastMap.newInstance();
						taxRateMap.put("orderAdjustmentTypeId",taxType);
						taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
						taxRateMap.put("amount",BigDecimal.ZERO);
						taxRateMap.put("taxAuthGeoId", partyGeoId);
						
						if (paramMap.containsKey(taxType + thisSuffix)) {
							String taxPercentage = (String) paramMap.get(taxType + thisSuffix);
							if(UtilValidate.isNotEmpty(taxPercentage) && !(taxPercentage.equals("NaN"))){
								taxRateMap.put("sourcePercentage",new BigDecimal(taxPercentage));
							}
							
						}
						if (paramMap.containsKey(taxType+ "_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(taxType+ "_AMT" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								taxRateMap.put("amount",new BigDecimal(taxAmt));
							}
						}
						Map tempTaxMap = FastMap.newInstance();
						tempTaxMap.putAll(taxRateMap);
						
						taxRateList.add(tempTaxMap);
					}
				}
				if (paramMap.containsKey("unitPrice" + thisSuffix)) {
					basicPriceStr = (String) paramMap.get("unitPrice"
							+ thisSuffix);
				}
				if (paramMap.containsKey("bundleUnitPrice" + thisSuffix)) {
					bundleUnitPriceStr = (String) paramMap.get("bundleUnitPrice"
							+ thisSuffix);
				}
				/*if (paramMap.containsKey("vatPrice" + thisSuffix)) {
					vatPriceStr = (String) paramMap
							.get("vatPrice" + thisSuffix);
				}
				if (paramMap.containsKey("bedPrice" + thisSuffix)) {
					bedPriceStr = (String) paramMap
							.get("bedPrice" + thisSuffix);
				}
				if (paramMap.containsKey("cstPrice" + thisSuffix)) {
					cstPriceStr = (String) paramMap
							.get("cstPrice" + thisSuffix);
				}
				if (paramMap.containsKey("tcsPrice" + thisSuffix)) {
					tcsPriceStr = (String) paramMap
							.get("tcsPrice" + thisSuffix);
				}
				if (paramMap.containsKey("serviceTaxPrice" + thisSuffix)) {
					serTaxPriceStr = (String) paramMap.get("serviceTaxPrice"
							+ thisSuffix);
				}

				if (paramMap.containsKey("bedPercent" + thisSuffix)) {
					bedPercentStr = (String) paramMap.get("bedPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("vatPercent" + thisSuffix)) {
					vatPercentStr = (String) paramMap.get("vatPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("cstPercent" + thisSuffix)) {
					cstPercentStr = (String) paramMap.get("cstPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("tcsPercent" + thisSuffix)) {
					tcsPercentStr = (String) paramMap.get("tcsPercent"
							+ thisSuffix);
				}
				if (paramMap.containsKey("serviceTaxPercent" + thisSuffix)) {
					serviceTaxPercentStr = (String) paramMap
							.get("serviceTaxPercent" + thisSuffix);
				}*/

				try {
					quantity = new BigDecimal(quantityStr);
					if(UtilValidate.isNotEmpty(baleQuantityStr) && !(baleQuantityStr.equals("NaN"))){
					baleQuantity = new BigDecimal(baleQuantityStr);
					}
					if (UtilValidate.isNotEmpty(bundleWeightStr)) {
					bundleWeight = new BigDecimal(bundleWeightStr);
					}
					if (UtilValidate.isNotEmpty(yarnUOMStr)) {
						yarnUOM = yarnUOMStr;
					}
					if (UtilValidate.isNotEmpty(basicPriceStr)) {
						basicPrice = new BigDecimal(basicPriceStr);
					}
					if (UtilValidate.isNotEmpty(serviceChgStr)) {
						serviceCharge = new BigDecimal(serviceChgStr);
					}
					if (UtilValidate.isNotEmpty(serviceChgAmtStr)) {
						serviceChargeAmt = new BigDecimal(serviceChgAmtStr);
					}
					/*if (UtilValidate.isNotEmpty(cstPriceStr)) {
						cstPrice = new BigDecimal(cstPriceStr);
					}
					if (UtilValidate.isNotEmpty(tcsPriceStr)) {
						tcsPrice = new BigDecimal(tcsPriceStr);
					}
					if (UtilValidate.isNotEmpty(bedPriceStr)) {
						bedPrice = new BigDecimal(bedPriceStr);
					}
					if (UtilValidate.isNotEmpty(vatPriceStr)) {
						vatPrice = new BigDecimal(vatPriceStr);
					}
					if (UtilValidate.isNotEmpty(serTaxPriceStr)) {
						serviceTaxPrice = new BigDecimal(serTaxPriceStr);
					}

					if (UtilValidate.isNotEmpty(bedPercentStr)) {
						bedPercent = new BigDecimal(bedPercentStr);
					}
					if (UtilValidate.isNotEmpty(vatPercentStr)) {
						vatPercent = new BigDecimal(vatPercentStr);
					}
					if (UtilValidate.isNotEmpty(cstPercentStr)) {
						cstPercent = new BigDecimal(cstPercentStr);
					}
					if (UtilValidate.isNotEmpty(tcsPercentStr)) {
						tcsPercent = new BigDecimal(tcsPercentStr);
					}
					if (UtilValidate.isNotEmpty(serviceTaxPercentStr)) {
						serviceTaxPercent = new BigDecimal(serviceTaxPercentStr);
					}*/
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing quantity string: "
							+ quantityStr, module);
					request.setAttribute("_ERROR_MESSAGE_",
							"Problems parsing quantity string: " + quantityStr);
					return "error";
				}
				
				//consolidation logic
				if (UtilValidate.isNotEmpty(customerId)) {
					onBeHalfOf="Y";
					if (UtilValidate.isNotEmpty(consolMap.get(productId))){
						
						BigDecimal tempbaleqty=BigDecimal.ZERO;
						BigDecimal tempquantity=BigDecimal.ZERO;
						BigDecimal tempbundleWeight=BigDecimal.ZERO;
						Map tempconsolMap=(Map)consolMap.get(productId);
						Debug.log("tempconsolMap=========================="+tempconsolMap);
						tempbaleqty=baleQuantity.add((BigDecimal)tempconsolMap.get("baleQuantity"));
						tempbundleWeight=bundleWeight.add((BigDecimal)tempconsolMap.get("bundleWeight"));
						tempquantity=quantity.add((BigDecimal)tempconsolMap.get("quantity"));
						Debug.log("tempquantity=========================="+tempquantity+"=tempbaleqty======"+tempbaleqty+"=tempbundleWeight====="+tempbundleWeight);
						tempconsolMap.put("quantity",tempquantity);
						tempconsolMap.put("baleQuantity",tempbaleqty);
						tempconsolMap.put("bundleWeight",tempbundleWeight);
					}else{
						Map tempconsolMap= FastMap.newInstance();
						tempconsolMap.put("productId", productId);
						tempconsolMap.put("quantity", quantity);
						//tempconsolMap.put("customerId", customerId);
						tempconsolMap.put("remarks", remarks);
						tempconsolMap.put("baleQuantity", baleQuantity);
						tempconsolMap.put("bundleWeight", bundleWeight);
						tempconsolMap.put("bundleUnitPrice", bundleUnitPriceStr);				
						tempconsolMap.put("yarnUOM", yarnUOM);
						tempconsolMap.put("baleQuantity", baleQuantity);
						tempconsolMap.put("bundleWeight", bundleWeight);
						tempconsolMap.put("bundleUnitPrice", bundleUnitPriceStr);				
						tempconsolMap.put("yarnUOM", yarnUOM);
						tempconsolMap.put("batchNo", batchNo);
						tempconsolMap.put("daysToStore", daysToStore);
						tempconsolMap.put("basicPrice", basicPrice);
						tempconsolMap.put("taxRateList", taxRateList);
						tempconsolMap.put("serviceCharge", serviceCharge);
						tempconsolMap.put("serviceChargeAmt", serviceChargeAmt);
						tempconsolMap.put("applicableTaxType", applicableTaxType);
						tempconsolMap.put("checkE2Form", checkE2Form);
						tempconsolMap.put("checkCForm", checkCForm);
						consolMap.put(productId,tempconsolMap);						
					}
				}
				

				productQtyMap.put("productId", productId);
				productQtyMap.put("quantity", quantity);
				productQtyMap.put("customerId", customerId);
				productQtyMap.put("remarks", remarks);
				productQtyMap.put("baleQuantity", baleQuantity);
				productQtyMap.put("bundleWeight", bundleWeight);
				productQtyMap.put("bundleUnitPrice", bundleUnitPriceStr);				
				productQtyMap.put("yarnUOM", yarnUOM);
				productQtyMap.put("batchNo", batchNo);
				productQtyMap.put("daysToStore", daysToStore);
				productQtyMap.put("basicPrice", basicPrice);
				productQtyMap.put("taxRateList", taxRateList);
				productQtyMap.put("serviceCharge", serviceCharge);
				productQtyMap.put("serviceChargeAmt", serviceChargeAmt);
				
				productQtyMap.put("applicableTaxType", applicableTaxType);
				productQtyMap.put("checkE2Form", checkE2Form);
				productQtyMap.put("checkCForm", checkCForm);
				
				/*productQtyMap.put("bedPrice", bedPrice);
				productQtyMap.put("cstPrice", cstPrice);
				productQtyMap.put("tcsPrice", tcsPrice);
				productQtyMap.put("vatPrice", vatPrice);
				productQtyMap.put("serviceTaxPrice", serviceTaxPrice);

				productQtyMap.put("bedPercent", bedPercent);
				productQtyMap.put("vatPercent", vatPercent);
				productQtyMap.put("cstPercent", cstPercent);
				productQtyMap.put("tcsPercent", tcsPercent);
				productQtyMap.put("serviceTaxPercent", serviceTaxPercent);
*/
				indentProductList.add(productQtyMap);
				indentItemProductList.add(productQtyMap);

			}//end of productQty check
		}//end row count for loop
	  
		if( UtilValidate.isEmpty(indentProductList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "error";
		}
		//adding list of adjustments
		List orderAdjChargesList = FastList.newInstance();
		for (int i = 0; i < rowCount; i++) {
			Map orderAdjMap = FastMap.newInstance();
			String orderAdjTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			if (paramMap.containsKey("orderAdjTypeId" + thisSuffix)) {
				orderAdjTypeId = (String) paramMap.get("orderAdjTypeId" + thisSuffix);
			}
			
			if(UtilValidate.isNotEmpty(orderAdjTypeId)){
				if (paramMap.containsKey("adjAmt" + thisSuffix)) {
					adjAmtStr = (String) paramMap.get("adjAmt" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing Adjustment Amount");
					return "error";			  
				}
				try {
					adjAmt = new BigDecimal(adjAmtStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
				
				orderAdjMap.put("orderAdjTypeId", orderAdjTypeId);
				orderAdjMap.put("adjAmount", adjAmt);
				orderAdjChargesList.add(orderAdjMap);	

			}
			//end of adjustment check
		}
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
		processOrderContext.put("userLogin", userLogin);
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
		processOrderContext.put("PONumber", PONumber);
		processOrderContext.put("promotionAdjAmt", promotionAdjAmt);
		processOrderContext.put("orderMessage", orderMessage);
		processOrderContext.put("orderAdjChargesList", orderAdjChargesList);
		processOrderContext.put("disableAcctgFlag", disableAcctgFlag);
		
		try{
		result = processBranchSalesOrder(dctx, processOrderContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
			return "error";
		}
		
		orderId = (String)result.get("orderId");
		if(UtilValidate.isEmpty(orderId)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
			return "error";
		}
		if(UtilValidate.isNotEmpty(billingType) && billingType.equals("onBehalfOf")){
			 Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId, "partyId", schemePartyId, "roleTypeId", "ON_BEHALF_OF");
			 try {
			 GenericValue value = delegator.makeValue("OrderRole", fields);
			 delegator.create(value);
			 } catch (GenericEntityException e) {
				 request.setAttribute("_ERROR_MESSAGE_"," Could not add role to order for OnBeHalf ");
					Debug.logError(e, "Could not add role to order for OnBeHalf  party " + schemePartyId, module);
					return "error";
			 }
		}
		
		if(UtilValidate.isNotEmpty(supplierPartyId)){
			try{
				GenericValue supplierOrderRole	=delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", supplierPartyId, "roleTypeId", "SUPPLIER"));
				delegator.createOrStore(supplierOrderRole);
			}catch (Exception e) {
				  Debug.logError(e, "Error While Creating OrderRole(SUPPLIER)  for  Sale Indent ", module);
				  request.setAttribute("_ERROR_MESSAGE_", "Error While Creating OrderRole(SUPPLIER)  for Sale Indent  : "+orderId);
					return "error";
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
				 request.setAttribute("_ERROR_MESSAGE_"," Could not add Attribute tax type ");
					Debug.logError(e, "Could not add role to order for OnBeHalf  party " + orderId, module);
					return "error";
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
					 request.setAttribute("_ERROR_MESSAGE_"," Could not add Attribute SchemeCategory ");
						Debug.logError(e, "Could not add role to order for SchemeCategory " + orderId, module);
						return "error";
				 }
		}
		Map resultCtx = FastMap.newInstance();
		
				resultCtx = dispatcher.runSync("createOrderHeaderSequence",UtilMisc.toMap("orderId", orderId ,"userLogin",userLogin, "orderHeaderSequenceTypeId","DEPOT_SALE_SEQUENCE"));
				if(ServiceUtil.isError(resultCtx)){
					Debug.logError("Problem while Creating  Sequence for orderId:"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem while Creating  Sequence for orderId: : "+orderId);
					return "error";
				}
			}catch(Exception e){
				Debug.logError(e, module);
				return "error";
			}
		request.setAttribute("orderId",orderId);
		
		request.setAttribute("_EVENT_MESSAGE_", "Order Entry successfully for party : "+partyId);
		return "success";
	}
   	public static Map<String, Object> processBranchSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    String schemeCategory = (String) context.get("schemeCategory");
	    List<Map> productQtyList = (List) context.get("productQtyList");
	    List<Map> indentItemProductList = (List) context.get("indentItemProductList");
	    Timestamp supplyDate = (Timestamp) context.get("supplyDate");
	    Locale locale = (Locale) context.get("locale");
	    String productStoreId = (String) context.get("productStoreId");
	  	String salesChannel = (String) context.get("salesChannel");
	  	List productIds = (List) context.get("productIds");
	  	String orderTaxType = (String) context.get("orderTaxType");
	  	String partyGeoId = (String) context.get("partyGeoId");
	  	String partyId = (String) context.get("partyId");
	  	String contactMechId = (String) context.get("contactMechId");
	  	String belowContactMechId = (String) context.get("belowContactMechId");
	  	String transporterId = (String) context.get("transporterId");
	  	String schemePartyId = (String) context.get("schemePartyId");
		String billToCustomer = (String) context.get("billToCustomer");
	  	String orderId = (String) context.get("orderId");
	  	String referenceNo = (String) context.get("referenceNo");
	  	String promotionAdjAmt = (String) context.get("promotionAdjAmt");
	  	String orderMessage = (String) context.get("orderMessage");
	  	String disableAcctgFlag = (String) context.get("disableAcctgFlag");
	  	List<Map> orderAdjChargesList = (List) context.get("orderAdjChargesList");
	  	String currencyUomId = "INR";
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		boolean isSale = Boolean.TRUE;
		boolean batchNumExists = Boolean.FALSE;
		boolean daysToStoreExists = Boolean.FALSE;
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Cannot create order without partyId: "+ partyId, module);
			return ServiceUtil.returnError("partyId is empty");
		}
		List conditionList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(orderId)){
			
			boolean indentNotChanged = true; 
			Map resultCtx = ByProductNetworkServices.getOrderDetails(dctx, UtilMisc.toMap("userLogin", userLogin, "orderId", orderId));
			Map orderDetails = (Map)resultCtx.get("orderDetails");
			List<GenericValue> extOrderItems = (List)orderDetails.get("orderItems");
			
			try{
				if(!indentNotChanged){
					result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", UtilMisc.toList(orderId),"userLogin", userLogin));
					if (ServiceUtil.isError(result)) {
						Debug.logError("Problem cancelling orders in Correction", module);	 		  		  
				 		return ServiceUtil.returnError("Problem cancelling orders in Correction");
					} 
				}
				else{
					List condList = FastList.newInstance();
					
					for(Map prodBatch : productQtyList){
						String prod = (String)prodBatch.get("productId");
						String batchNum = null;
						if(UtilValidate.isNotEmpty(prodBatch.get("batchNo"))){
							batchNum = (String)prodBatch.get("batchNo");
						}
						List<GenericValue> orderItem = EntityUtil.filterByCondition(extOrderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prod));
						if(UtilValidate.isNotEmpty(orderItem) && batchNum != null){
							String orderItemSeqId = (EntityUtil.getFirst(orderItem)).getString("orderItemSeqId");
							
							GenericValue orderItemBatch = delegator.makeValue("OrderItemAttribute");
							orderItemBatch.set("orderId", orderId);
							orderItemBatch.set("orderItemSeqId", orderItemSeqId);
							orderItemBatch.set("attrName", "batchNumber");
							orderItemBatch.set("attrValue", batchNum);
							delegator.createOrStore(orderItemBatch);
							
						}
						
					}
					//cancelling quota for  order 
					if(schemeCategory.equals("MGPS_10Pecent")){
						List condsList = FastList.newInstance();
						condsList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
						condsList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
						try{
						List<GenericValue> orderItemAndAdjustmentList =  delegator.findList("OrderItemAndAdjustment",EntityCondition.makeCondition(condsList,EntityOperator.AND),null, null, null, true);   
						
						if(UtilValidate.isNotEmpty(orderItemAndAdjustmentList)&& orderItemAndAdjustmentList.size()>0){
							List schemeCategoryIds = FastList.newInstance();
						  	try{
						  		List productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), UtilMisc.toSet("productCategoryId"), null, null, false);
						  		schemeCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);
						   	}catch (GenericEntityException e) {
								Debug.logError(e, "Failed to retrive ProductCategory ", module);
								return ServiceUtil.returnError("Failed to retrive ProductCategory " + e);
							}	 	
							for(GenericValue orderItemAndAdjustment : orderItemAndAdjustmentList){
								
								condsList.clear();
								condsList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
							  	condsList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemAndAdjustment.get("orderItemSeqId")));
							  	condsList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "quotaQty"));
							  	BigDecimal quota =BigDecimal.ZERO;
							  	try {
									List<GenericValue> OrderItemAttributeList = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition(condsList,EntityOperator.AND), UtilMisc.toSet("attrValue"), null, null, true);
									if(UtilValidate.isEmpty(OrderItemAttributeList) || OrderItemAttributeList.size()==0){
										continue;
									}
									GenericValue OrderItemAttribute=OrderItemAttributeList.get(0);
									quota = new BigDecimal((String)OrderItemAttribute.get("attrValue"));
								} catch (GenericEntityException e) {
									Debug.logError(e, "Failed to retrive ProductPriceType ", module);
									return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
								}
							  	Map partyBalanceHistoryContext = FastMap.newInstance();
								partyBalanceHistoryContext = UtilMisc.toMap("partyId",partyId,"orderItemAndAdjustment",orderItemAndAdjustment,"schemeCategoryIds",schemeCategoryIds,"schemeCategory",schemeCategory,"quota",quota, "userLogin", userLogin);
							  	dispatcher.runSync("cancelPartyQuotaBalanceHistory", partyBalanceHistoryContext);
							}
						}
						}catch (GenericEntityException e) {
							Debug.logError(e, "Failed to retrive ProductCategory ", module);
							return ServiceUtil.returnError("Failed to retrive ProductCategory " + e);
						}
					}
					
				}
				  			
			}catch (GenericServiceException e) {
				  Debug.logError(e, e.toString(), module);
				  return ServiceUtil.returnError("Problem cancelling order");
			}
			catch (GenericEntityException e1) {
				  Debug.logError(e1, e1.toString(), module);
				  return ServiceUtil.returnError("Failed fetching existing order details");
			}
			//orderId=null;
		}
		GenericValue product =null;
		String productPriceTypeId = null;
		String geoTax = "";
		
		/*if(UtilValidate.isNotEmpty(orderTaxType)){
			if(orderTaxType.equals("INTER")){
				geoTax = "CST";
			}else{
				geoTax = "VAT";
			}
		}*/
		
		BigDecimal promoAmt = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(promotionAdjAmt)){
			promoAmt = new BigDecimal(promotionAdjAmt);
		}
		
	  	// Get Scheme Categories
	  	List schemeCategoryIds = FastList.newInstance();
	  	try{
	  		List productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), UtilMisc.toSet("productCategoryId"), null, null, false);
	  		schemeCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);
	   	}catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive ProductCategory ", module);
			return ServiceUtil.returnError("Failed to retrive ProductCategory " + e);
		}
	  	
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale,currencyUomId);
		
		try {
			//get inventoryFacility details through productStore.
			String  inventoryFacilityId="";
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if(UtilValidate.isNotEmpty(productStore)){
			inventoryFacilityId=productStore.getString("inventoryFacilityId");
			}
			cart.setOrderType("SALES_ORDER");
			cart.setIsEnableAcctg("Y");
			if("Y".equals(disableAcctgFlag)){
				cart.setIsEnableAcctg("N");
			}
	        cart.setExternalId(referenceNo);
	        if(UtilValidate.isNotEmpty(contactMechId))
			  cart.setOrderAttribute("SHIPPING_PREF",belowContactMechId);
	        else if(UtilValidate.isNotEmpty(belowContactMechId))
	          cart.setOrderAttribute("SHIPPING_PREF",belowContactMechId);
	        
	        if(UtilValidate.isNotEmpty(transporterId))
				  cart.setOrderAttribute("TRANSPORTER_PREF",transporterId);
	        
	        cart.setProductStoreId(productStoreId);
			cart.setChannelType(salesChannel);
			//cart.setOrderId(orderId);
			//cart.setBillToCustomerPartyId("GCMMF");
			cart.setBillToCustomerPartyId(partyId);
			cart.setFacilityId(inventoryFacilityId);//for store inventory we need this so that inventoryItem query by this orginFacilityId
			if(UtilValidate.isNotEmpty(billToCustomer)){
				cart.setBillToCustomerPartyId(billToCustomer);
			}
			cart.setPlacingCustomerPartyId(partyId);
			cart.setShipToCustomerPartyId(partyId);
			cart.setEndUserCustomerPartyId(partyId);
			//cart.setShipmentId(shipmentId);
			cart.setEstimatedDeliveryDate(effectiveDate);
			cart.setOrderDate(effectiveDate);
			cart.setUserLogin(userLogin, dispatcher);
			//cart.setOrderMessage(orderMessage);
		} catch (Exception e) {
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}
		
		String productId = "";
		String remarks = "";
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal baleQuantity = BigDecimal.ZERO;
		BigDecimal bundleWeight = BigDecimal.ZERO;
		String yarnUOM="";
		String batchNo = "";
		String daysToStore = "";
		List<GenericValue> productPriceTaxCalc = FastList.newInstance();
		
		//int groupSeqCount = 1;
		//String groupSequenceId = "";
		
		for (Map<String, Object> prodQtyMap : productQtyList) {
			String customerId = "";
			BigDecimal basicPrice = BigDecimal.ZERO;
			BigDecimal taxPercent = BigDecimal.ZERO;
			BigDecimal serviceCharge = BigDecimal.ZERO;
			BigDecimal serviceChargeAmt = BigDecimal.ZERO;
			String bundleUnitPrice="";
			
			String applicableTaxType = "";
			String checkE2Form = "";
			String checkCForm = "";
			/*BigDecimal bedPrice = BigDecimal.ZERO;
			BigDecimal vatPrice = BigDecimal.ZERO;
			BigDecimal cstPrice = BigDecimal.ZERO;
			BigDecimal tcsPrice = BigDecimal.ZERO;
			BigDecimal serviceTaxPrice = BigDecimal.ZERO;*/
			
			//groupSequenceId = String.format("%02d", Integer.parseInt(groupSequenceId + groupSeqCount));
			//cart.addItemGroup("PROD_ASSOC_GRP", groupSequenceId);
			
			
			if(UtilValidate.isNotEmpty(prodQtyMap.get("serviceCharge"))){
				serviceCharge = (BigDecimal)prodQtyMap.get("serviceCharge");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("serviceChargeAmt"))){
				serviceChargeAmt = (BigDecimal)prodQtyMap.get("serviceChargeAmt");
			}
			List taxRateList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
				productId = (String)prodQtyMap.get("productId");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("taxRateList"))){
				taxRateList = (List)prodQtyMap.get("taxRateList");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("customerId"))){
				customerId = (String)prodQtyMap.get("customerId");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("remarks"))){
				remarks = (String)prodQtyMap.get("remarks");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("baleQuantity"))){
				baleQuantity = (BigDecimal)prodQtyMap.get("baleQuantity");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("bundleUnitPrice"))){
				bundleUnitPrice = (String)prodQtyMap.get("bundleUnitPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
				 quantity = (BigDecimal)prodQtyMap.get("quantity");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("yarnUOM"))){
				yarnUOM = (String)prodQtyMap.get("yarnUOM");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("applicableTaxType"))){
				applicableTaxType = (String)prodQtyMap.get("applicableTaxType");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("checkE2Form"))){
				checkE2Form = (String)prodQtyMap.get("checkE2Form");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("checkCForm"))){
				checkCForm = (String)prodQtyMap.get("checkCForm");
			}
			
			if(UtilValidate.isNotEmpty(prodQtyMap.get("bundleWeight"))){
				bundleWeight = (BigDecimal)prodQtyMap.get("bundleWeight");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("batchNo"))){
				batchNo = (String)prodQtyMap.get("batchNo");
				batchNumExists = true;
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("daysToStore"))){
				daysToStore = (String)prodQtyMap.get("daysToStore");
				daysToStoreExists = true;
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("basicPrice"))){
				basicPrice = (BigDecimal)prodQtyMap.get("basicPrice");
			}
			if(UtilValidate.isNotEmpty(prodQtyMap.get("taxPercent"))){
				taxPercent = (BigDecimal)prodQtyMap.get("taxPercent");
			}
			
					
			//add percentages
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO;
						
			BigDecimal taxPercentage = BigDecimal.ZERO;
			//List taxAuthProdCatList = FastList.newInstance();
			
			// Auto Calculate Taxes
			/*Map prodCatTaxCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			prodCatTaxCtx.put("productId", productId);
			prodCatTaxCtx.put("taxAuthGeoId", partyGeoId);
			prodCatTaxCtx.put("taxAuthorityRateTypeId", orderTaxType);
		  	try{
		  		Map resultCtx = dispatcher.runSync("calculateTaxesByGeoId",prodCatTaxCtx);  	
		  		Debug.log("resultCtx =========="+resultCtx);
		  		taxPercentage = (BigDecimal) resultCtx.get("taxPercentage");
		  		Debug.log("taxPercentage =========="+taxPercentage);
		  		taxAuthProdCatList = (List) resultCtx.get("taxAuthProdCatList");
		  		Debug.log("taxAuthProdCatList =========="+taxAuthProdCatList);
		  	}catch (GenericServiceException e) {
		  		Debug.logError(e , module);
		  		return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
		  	}*/
		  	
			BigDecimal totalPrice = BigDecimal.ZERO;
				
			// Scheme Calculation
			List productCategoriesList = FastList.newInstance();
			List condsList = FastList.newInstance();
		  	condsList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		  	condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, schemeCategoryIds));
		  	condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
		  	condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
			try {
				List<GenericValue> prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(condsList,EntityOperator.AND), UtilMisc.toSet("productCategoryId"), null, null, true);
				productCategoriesList = EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productCategoryId", true);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive ProductPriceType ", module);
				return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
			}
			BigDecimal quota = BigDecimal.ZERO;
			// Get first productCategoriesList. We got productCategoryId here
			
			if(schemeCategory.equals("MGPS_10Pecent")){
				
				String schemeId="TEN_PERCENT_MGPS";
				String productCategoryId=(String)productCategoriesList.get(0);
				
				Map partyBalanceHistoryContext = FastMap.newInstance();
				partyBalanceHistoryContext = UtilMisc.toMap("schemeId",schemeId,"partyId",partyId,"productCategoryId",productCategoryId,"dateTimeStamp", supplyDate,"quantity",quantity,"userLogin", userLogin);
				
				if(UtilValidate.isNotEmpty(customerId)){
					partyBalanceHistoryContext.put("partyId",customerId);
				}
						
				try {
					Map<String, Object> resultMapquota = dispatcher.runSync("createPartyQuotaBalanceHistory", partyBalanceHistoryContext);
					quota=(BigDecimal)resultMapquota.get("quota");
				} catch (Exception e) {
					Debug.logError(e, "Failed to retrive PartyQuotaBalanceHistory ", module);
					return ServiceUtil.returnError("Failed to retrive PartyQuotaBalanceHistory " + e);
				}
			}	
			
			// Populate Shopping Cart With Items.
			// If ordered quantity is more than the available quota, we will split the cart items into two. one with quota qty and rest in other cart item.
			ShoppingCartItem item = null;
			try{
				
				item = ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null, quantity, basicPrice,
			            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
			            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE);
				
				item.setOrderItemAttribute("BALE_QTY",baleQuantity.toString());
				item.setOrderItemAttribute("productId",productId);
				if(UtilValidate.isNotEmpty(yarnUOM)){
					item.setOrderItemAttribute("YARN_UOM",yarnUOM.toString());
				}
				if(UtilValidate.isNotEmpty(bundleWeight)){
					item.setOrderItemAttribute("BUNDLE_WGHT",bundleWeight.toString());
				}
				if(UtilValidate.isNotEmpty(customerId)){
					item.setOrderItemAttribute("WIEVER_CUSTOMER",customerId);
				}
				if(UtilValidate.isNotEmpty(remarks)){
					item.setOrderItemAttribute("REMARKS",remarks.toString());
				}
				if(UtilValidate.isNotEmpty(bundleUnitPrice)){
					item.setOrderItemAttribute("BANDLE_UNITPRICE",bundleUnitPrice.toString());
				}
				if(UtilValidate.isNotEmpty(applicableTaxType)){
					item.setOrderItemAttribute("applicableTaxType",applicableTaxType);
				}
				if(UtilValidate.isNotEmpty(checkE2Form)){
					item.setOrderItemAttribute("checkE2Form",checkE2Form);
				}
				if(UtilValidate.isNotEmpty(checkCForm)){
					item.setOrderItemAttribute("checkCForm",checkCForm);
				}
				
				
				if(quota.compareTo(BigDecimal.ZERO)>0){
					
					// Have to get these details from schemes. Temporarily hard coding it.
					BigDecimal schemePercent = new BigDecimal("10");
					BigDecimal percentModifier = schemePercent.movePointLeft(2);
					item.setOrderItemAttribute("quotaQty",quota.toString());
					
					BigDecimal discountAmount = BigDecimal.ZERO;
					if(quantity.compareTo(quota)>0){
						discountAmount = ((quota.multiply(basicPrice)).multiply(percentModifier)).negate();
						item.setOrderItemAttribute("quotaQty",quota.toString());
					}
					else{
						discountAmount = ((quantity.multiply(basicPrice)).multiply(percentModifier)).negate();
						item.setOrderItemAttribute("quotaQty",quantity.toString());
					}
					GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
			                UtilMisc.toMap("orderAdjustmentTypeId", "TEN_PERCENT_SUBSIDY", "amount", discountAmount,
			                        "description", "10 Percent Subsidy on eligible product categories"));
					item.addAdjustment(orderAdjustment);
					
					totalPrice.add(discountAmount);
				}
				
				// Tax Handling
				
				Debug.log("size ====== ====== "+taxRateList.size());
				for(int i=0; i<taxRateList.size(); i++){
					Map taxMap = (Map) taxRateList.get(i);
					if(  ((BigDecimal) taxMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
						GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxMap);
		 				item.addAdjustment(orderAdjustment);
						
		 				totalPrice.add((BigDecimal) taxMap.get("amount"));
					}
				}
				
				// Service Charge
				if(serviceChargeAmt.compareTo(BigDecimal.ZERO)>0){
					GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
			                UtilMisc.toMap("orderAdjustmentTypeId", "SERVICE_CHARGE", "amount", serviceChargeAmt, "sourcePercentage", serviceCharge,
			                        "description", "Service Charge"));
					item.addAdjustment(orderAdjustment);
					
					totalPrice.add(serviceChargeAmt);
				}
				
				item.setListPrice(totalPrice);
				
				//Debug.log("groupSequenceId =============="+groupSequenceId);
				//item.setItemGroup(groupSequenceId, cart);
				
				//item.setTaxDetails(taxList);
				cart.addItemToEnd(item);
				
				
				/*if(serviceCharge.compareTo(BigDecimal.ZERO)>0){
					item = ShoppingCartItem.makeItem(Integer.valueOf(0), "SERVICE_CHARGE", null, quantity, serviceChargeAmt.divide(quantity),
				            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
				            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE);
					item.setOrderItemAttribute("ServiceChargePercent", serviceCharge.toString());
					item.setItemGroup(groupSequenceId, cart);
					cart.addItemToEnd(item);
				}
				groupSeqCount++;*/
			}
			catch (Exception exc) {
				Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
				return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: ");
	        }
			
		}
		cart.setDefaultCheckoutOptions(dispatcher);
		//ProductPromoWorker.doPromotions(cart, dispatcher);
		
		CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
		/*try {
			if(isSale || UtilValidate.isNotEmpty(productPriceTaxCalc)){
				checkout.calcAndAddTax(productPriceTaxCalc);
			}
			
		} catch (Exception e1) {
		// TODO Auto-generated catch block
			Debug.logError(e1, "Error in CalcAndAddTax",module);
		}*/
		
		Map<String, Object> orderCreateResult= FastMap.newInstance();
		//if orderId empty call createOrder other wise editOrder
		if(UtilValidate.isEmpty(orderId)){
			orderCreateResult = checkout.createOrder(userLogin);
		}else{
			cart.setOrderId(orderId);
			checkout = new CheckOutHelper(dispatcher, delegator, cart);
			orderCreateResult = checkout.editOrder(userLogin);
			String PoOrderId ="";
			try{
				List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("orderId"), null, null, false);
				 PoOrderId = (EntityUtil.getFirst(orderAssocList)).getString("orderId");	
				if(UtilValidate.isNotEmpty(PoOrderId)){
		            Map<String, Object> svcCtx = UtilMisc.<String, Object>toMap("orderId", PoOrderId, "userLogin", userLogin);
		            try {
		                dispatcher.runSync("cancelOrderItem", svcCtx);
		            } catch (GenericServiceException e) {
		                Debug.logError(e, "Problem calling service cancelOrderItem: " + svcCtx, module);
		            }
					
		    		Map processContext = FastMap.newInstance();
		    		processContext.put("userLogin",userLogin);
		    		processContext.put("SalesOrder",orderId);
		    		processContext.put("PurchaseOrder",PoOrderId);
		            result = updateIndentSummaryPO(dctx, processContext);
					if(ServiceUtil.isError(result)){
						Debug.logError("Unable to update order: " + ServiceUtil.getErrorMessage(result), module);
			             return ServiceUtil.returnError(" Unable to update related Purchase order  :");
					}
		            
				}
			}catch (GenericEntityException e) {
				Debug.logError("Error in creating shipmentId for DirectOrder", module);
				return ServiceUtil.returnError("Error in creating shipmentId for DirectOrder");
			}
		}
		if(UtilValidate.isNotEmpty(orderCreateResult)){
			orderId = (String) orderCreateResult.get("orderId");
		}
		if(promoAmt.compareTo(BigDecimal.ZERO)>0){
			Map promoAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			promoAdjCtx.put("orderId", orderId);
			promoAdjCtx.put("promoAdjAmt", promoAmt);
		  	try{
		  		Map resultCtx = dispatcher.runSync("adjustPromotionAmtForOrder",promoAdjCtx);  		  		 
		  		if (ServiceUtil.isError(resultCtx)) {
		  	 		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
		  	 		Debug.logError(errMsg , module);
		  		}	
	         }catch (GenericServiceException e) {
	        	 Debug.logError(e , module);
	             return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
	         }
		}
		
		//creating adjustments by list
		if(UtilValidate.isNotEmpty(orderAdjChargesList)){
			Map inputAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			inputAdjCtx.put("orderId", orderId);
			inputAdjCtx.put("orderAdjChargesList", orderAdjChargesList);
			result = in.vasista.vbiz.byproducts.icp.ICPServices.createOrderAdjustmentByTypeList(dctx, inputAdjCtx);
			if (ServiceUtil.isError(result)) {
				Debug.logError("Unable to generate Adjustments: " + ServiceUtil.getErrorMessage(result), module);
				 return ServiceUtil.returnError(" Unable to generate Adjustments:");
	  		}	
		}
		
		GenericValue orderHeader = null;
		//update PurposeType
		try{
			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			orderHeader.set("purposeTypeId", "BRANCH_SALES");
			orderHeader.store();
		}catch (Exception e) {
			  Debug.logError(e, "Error While Updating purposeTypeId for Order ", module);
			  return ServiceUtil.returnError("Error While Updating purposeTypeId for Order : "+orderId);
  	 	}
		
		 List<GenericValue> orderItemValue = FastList.newInstance();
		 try{
			if(UtilValidate.isNotEmpty(orderId)){
				orderItemValue = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			}
		 }catch (Exception e) {
			  Debug.logError(e, "Error While Fetching OrderItem ", module);
			  return ServiceUtil.returnError("Error While  Fetching OrderItem : "+orderId);
 	 	}

		for (Map<String, Object> prodItemMap : indentItemProductList) {
			String customerId = "";
			//BigDecimal basicPrice = BigDecimal.ZERO;
			String prodId="";
			String  budlUnitPriceStr="";
			BigDecimal budlWeight=BigDecimal.ZERO;
			BigDecimal budlUnitPrice=BigDecimal.ZERO;
			BigDecimal blQuantity=BigDecimal.ZERO;
			BigDecimal Kgquantity=BigDecimal.ZERO;
			String Uom="";
			String specification="";

			if(UtilValidate.isNotEmpty(prodItemMap.get("productId"))){
				prodId = (String)prodItemMap.get("productId");
			}
			if(UtilValidate.isNotEmpty(prodItemMap.get("customerId"))){
				customerId = (String)prodItemMap.get("customerId");
			}
			if(UtilValidate.isNotEmpty(prodItemMap.get("remarks"))){
				specification = (String)prodItemMap.get("remarks");
			}
			if(UtilValidate.isNotEmpty(prodItemMap.get("baleQuantity"))){
				blQuantity =(BigDecimal)  prodItemMap.get("baleQuantity");
			}
			
			if(UtilValidate.isNotEmpty(prodItemMap.get("bundleUnitPrice"))){
				budlUnitPriceStr = (String)(prodItemMap.get("bundleUnitPrice"));
				budlUnitPrice = new BigDecimal(budlUnitPriceStr);
			}
			if(UtilValidate.isNotEmpty(prodItemMap.get("quantity"))){
				Kgquantity =  (BigDecimal)(prodItemMap.get("quantity"));
			}
			if(UtilValidate.isNotEmpty(prodItemMap.get("yarnUOM"))){
				Uom = (String)prodItemMap.get("yarnUOM");
			}
			if(UtilValidate.isNotEmpty(prodItemMap.get("bundleWeight"))){
				budlWeight =  (BigDecimal)prodItemMap.get("bundleWeight");
			}
        	GenericValue filteredOrderItem = EntityUtil.getFirst(EntityUtil.filterByCondition(orderItemValue, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId)));
			Map<String, Object> orderItemDetail = FastMap.newInstance();
			String orderItemSeqId="";
			
			orderItemSeqId=(String)filteredOrderItem.get("orderItemSeqId");
			//orderItemDetail.put("",);
			orderItemDetail.put("orderId",orderId);
			orderItemDetail.put("orderItemSeqId",orderItemSeqId);
			orderItemDetail.put("userLogin",userLogin);
			orderItemDetail.put("partyId",customerId);
			orderItemDetail.put("Uom",Uom);
			orderItemDetail.put("productId",prodId);
			orderItemDetail.put("baleQuantity",blQuantity);
			orderItemDetail.put("bundleWeight",budlWeight);
			orderItemDetail.put("bundleUnitPrice",budlUnitPrice);
			orderItemDetail.put("remarks",specification);
			orderItemDetail.put("quotaQuantity",Kgquantity);
			orderItemDetail.put("changeUserLogin",userLogin.getString("userLoginId"));

			try{
				Map resultMap = dispatcher.runSync("createOrderItemDetail",orderItemDetail);
		        
		        if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError("Problem creating order Item  change for orderId :"+orderId, module);
		        	return ServiceUtil.returnError("Problem creating order Item  Detail for orderId :"+orderId);	
		        }
			}catch(Exception e){
		  		Debug.logError(e, "Error in Order Item Detail, module");
		  		return ServiceUtil.returnError( "Error in Order Item Detail");
		  	}
		}
		
		if(UtilValidate.isNotEmpty(orderId) && (batchNumExists || daysToStoreExists)){
			try{
				//GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if(orderTaxType.equals("CST_SALE")){
					orderHeader.set("isInterState", "N");
				}else{
					orderHeader.set("isInterState", "Y");
				}
				orderHeader.store();
				
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("orderId", "productId", "quantity", "orderItemSeqId"), null, null, false);
				for(GenericValue orderItem : orderItems){
					if(UtilValidate.isNotEmpty(productQtyList)){
						Map batchMap = (Map)productQtyList.get(0);
						GenericValue newItemAttr = delegator.makeValue("OrderItemAttribute");        	 
						newItemAttr.set("orderId", orderItem.getString("orderId"));
						newItemAttr.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
						newItemAttr.set("attrName", "batchNumber");
						newItemAttr.set("attrValue", (String)batchMap.get("batchNo"));
						newItemAttr.create();
						
						GenericValue newDayStoreAttr = delegator.makeValue("OrderItemAttribute");        	 
						newDayStoreAttr.set("orderId", orderItem.getString("orderId"));
						newDayStoreAttr.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
						newDayStoreAttr.set("attrName", "daysToStore");
						newDayStoreAttr.set("attrValue", (String)batchMap.get("daysToStore"));
						newDayStoreAttr.create();
						
						productQtyList.remove(0);
					}
				}
			}catch (GenericEntityException e) {
				Debug.logError("Error in creating shipmentId for DirectOrder", module);
				return ServiceUtil.returnError("Error in creating shipmentId for DirectOrder");
			}
			
		}
		//store OrderMessage
		if(UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(orderMessage )){
			try{
				//GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				orderHeader.set("orderMessage", orderMessage.trim());
				orderHeader.store();
			}catch (GenericEntityException e) {
				Debug.logError("Error While Saving Order Message ", module);
				return ServiceUtil.returnError("Error While Saving Order Message");
			}
			
		}
		
		result.put("orderId", orderId);
		return result;
   	}
   
   	public static Map<String, Object> getPartySchemeEligibility(DispatchContext dctx, Map<String, ? extends Object> context) {
		
   		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Locale locale = null;
		TimeZone timeZone = null;
		locale = Locale.getDefault();
		timeZone = TimeZone.getDefault();
		
	    String partyId= (String)context.get("partyId");
	    Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");
	    if(UtilValidate.isEmpty(effectiveDate)){
	    	effectiveDate = UtilDateTime.nowTimestamp();
	    }
	    
	    List conditionList = FastList.newInstance();
	    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	    conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
				EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
		
		List<GenericValue> partyLooms = null;
		try {
			partyLooms = delegator.findList("PartyLoom", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive SchemeParty ", module);
			return ServiceUtil.returnError("Failed to retrive SchemeParty " + e);
		}
	    // Scheme applicability for party can be based on partyId, PartyClassificationGroup or both.
	    
	    // Get All the schemes applicable for the party.
		List<GenericValue> partyApplicableSchemes = null;
		try {
			partyApplicableSchemes = delegator.findList("SchemeParty", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive SchemeParty ", module);
			return ServiceUtil.returnError("Failed to retrive SchemeParty " + e);
		}
	    List partySchemeIds = EntityUtil.getFieldListFromEntityList(partyApplicableSchemes, "schemeId", true);
		// Get All the schemes applicable for the PartyClassificationGroup.
		List<GenericValue> partyClassificationList = null;
		try {
			partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), UtilMisc.toSet("partyClassificationGroupId"), null, null,false);
		} catch (GenericEntityException e) {
			Debug.logError("Unable to get records from PartyClassificationGroup" + e,module);
			return ServiceUtil.returnError("Unable to get records from PartyClassificationGroup");
		}
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(partyClassificationList, "partyClassificationGroupId", true)));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
				EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
		
		List<GenericValue> groupApplicableSchemes = null;
		try {
			groupApplicableSchemes = delegator.findList("SchemePartyClassificationGroup", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive SchemeParty ", module);
			return ServiceUtil.returnError("Failed to retrive SchemeParty " + e);
		}
		List partyGrpSchemeIds = EntityUtil.getFieldListFromEntityList(groupApplicableSchemes, "schemeId", true);
		partySchemeIds.addAll(partyGrpSchemeIds);
		
		Set partySchemeIdsSet = new HashSet(partySchemeIds);
		partySchemeIds = new ArrayList(partySchemeIdsSet);
		
		Map schemesMap = FastMap.newInstance();
		
		Timestamp periodBegin = null;
		Timestamp periodEnd = null;
		
		/*List condList = FastList.newInstance();
		condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_YEAR"));
		//condList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
		condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,effectiveDate));
		condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,effectiveDate));
		//EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			Debug.log("condList =============="+condList);
			List<GenericValue> customTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
			
			Debug.log("customTimePeriod =============="+customTimePeriod);
			
			GenericValue fiscalPeriod = EntityUtil.getFirst(customTimePeriod);
			Debug.log("fiscalPeriod =============="+fiscalPeriod);
			periodBegin = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fiscalPeriod.getDate("fromDate")));
			periodEnd = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(fiscalPeriod.getDate("thruDate")));
			
			Debug.log("periodBegin =============="+periodBegin);
			Debug.log("periodEnd =============="+periodEnd);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			ServiceUtil.returnError(e.toString());
		}*/
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date date = dateFormat.parse("01/04/2015");
			long time = date.getTime();
			periodBegin = new Timestamp(time);
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: ", module);
		} catch (NullPointerException e) {
			Debug.logError(e, "Cannot parse date string: ", module);
		}
		
		try {
			Date endDate = dateFormat.parse("31/03/2016");
			long endTime = endDate.getTime();
			periodEnd = new Timestamp(endTime);
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: ", module);
		} catch (NullPointerException e) {
			Debug.logError(e, "Cannot parse date string: ", module);
		}
		
		/*List condPeriodList = FastList.newInstance();
		condPeriodList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"FISCAL_YEAR"));
		condPeriodList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
		condPeriodList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate));
		condPeriodList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS ,"Company"));
		EntityCondition periodCond = EntityCondition.makeCondition(condPeriodList,EntityOperator.AND); 	
		Debug.log("periodCond =============="+periodCond);
		try {
			
			List<GenericValue> fiscalYearList = delegator.findList("CustomTimePeriod", periodCond, null, null, null, false);
			Debug.log("fiscalYearList =============="+fiscalYearList);
			
			GenericValue fiscalPeriod = EntityUtil.getFirst(fiscalYearList);
			Debug.log("fiscalPeriod =============="+fiscalPeriod);
			periodBegin = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(fiscalPeriod.getDate("fromDate")));
			periodEnd = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(fiscalPeriod.getDate("thruDate")));
			
			Debug.log("periodBegin =============="+periodBegin);
			Debug.log("periodEnd =============="+periodEnd);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive Fiscal Year for scheme calculation", module);
			return ServiceUtil.returnError("Failed to retrive Fiscal Year for scheme calculation" + e);
		}*/
		
		for(int i=0; i<partySchemeIds.size(); i++){
			String schemeId = (String) partySchemeIds.get(i);
			// Scheme applicability for products are based on product, productCategory or both.
			
			// Get Product Categories that are applicable for the scheme.
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("schemeId", EntityOperator.EQUALS, schemeId));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
			List<GenericValue> productCategoryApplicableSchemes = null;
			try {
				productCategoryApplicableSchemes = delegator.findList("SchemeProductCategory", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive SchemeProduct ", module);
				return ServiceUtil.returnError("Failed to retrive SchemeProduct " + e);
			}
			// List partySchemeIds = EntityUtil.getFieldListFromEntityList(partyApplicableSchemes, "schemeId", true);
			
			// Get products that are applicable for the scheme. 
		
		//TODO Handle Schemes based on SchemeProduct. Not required currently
			/*List<GenericValue> productApplicableSchemes = null;
			try {
				productApplicableSchemes = delegator.findList("SchemeProduct", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive SchemeProduct ", module);
				return ServiceUtil.returnError("Failed to retrive SchemeProduct " + e);
			}*/
		    //List partySchemeIds = EntityUtil.getFieldListFromEntityList(partyApplicableSchemes, "schemeId", true);
			
			// Temporarily doing this only for 10% Scheme. Keep updating as the requirement comes in
			
			Map productCategoryQuatasMap = FastMap.newInstance();
			List productCategoryQuatasList = FastList.newInstance();
			if(schemeId.equals("TEN_PERCENT_MGPS")){
				
				for(int j=0; j<productCategoryApplicableSchemes.size(); j++){
					GenericValue schemeProductCategory = productCategoryApplicableSchemes.get(j);
					String productCategoryId = schemeProductCategory.getString("productCategoryId");
					//Timestamp monthStart = UtilDateTime.getMonthStart(effectiveDate);
				    //Timestamp monthEnd = UtilDateTime.getMonthEnd(effectiveDate);
				    
					// Get relevant looms qty party possess and calculate quota
					List catPartyLooms = EntityUtil.filterByCondition(partyLooms, EntityCondition.makeCondition("loomTypeId", EntityOperator.EQUALS, productCategoryId));
					if(UtilValidate.isNotEmpty(catPartyLooms)){
						
						// Get productCategoryMembers
						List productIdsList = FastList.newInstance();
						
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
						conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
						conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
								EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
						try {
							List<GenericValue> prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionList,EntityOperator.AND), UtilMisc.toSet("productCategoryId"), null, null, true);
							productIdsList = EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productId", true);
						} catch (GenericEntityException e) {
							Debug.logError(e, "Failed to retrive ProductCategoryMember ", module);
							return ServiceUtil.returnError("Failed to retrive ProductCategoryMember " + e);
						}
						
						BigDecimal periodTime = BigDecimal.ONE;
						if( UtilValidate.isNotEmpty(schemeProductCategory.get("periodTime")) ){
							periodTime = (BigDecimal)schemeProductCategory.get("periodTime");
						}
						
						// calculate the quota already used for the month and reduce it from the actual quota.
						
						BigDecimal allocatedQuotaPerMonth = ((BigDecimal)schemeProductCategory.get("maxQty")).multiply( (BigDecimal)(((GenericValue)catPartyLooms.get(0)).get("quantity")));
						BigDecimal allocatedQuotaAdvances = periodTime.multiply(allocatedQuotaPerMonth);
						BigDecimal availableQuota = BigDecimal.ZERO;
						
						// Two months advance can be taken at any time. I am trying to iterate through each month, and see if he has taken any advances. With this I will get the current month's quota.
						
						BigDecimal outstandingQuotaAvailable = BigDecimal.ZERO;
						BigDecimal quotaAdvanceUsed = BigDecimal.ZERO;
						
						// If advances are allowed(periodTime > 1), check if the party has taken any advance.
						if(periodTime.compareTo(BigDecimal.ONE)>0){
							
							Timestamp periodMonthStart = periodBegin;
							Timestamp periodMonthEnd = null;
							
							Calendar startCalendar=Calendar.getInstance();
							startCalendar.setTime(UtilDateTime.toSqlDate(periodBegin));
				    		Calendar endCalendar=Calendar.getInstance();
				    		endCalendar.setTime(UtilDateTime.toSqlDate(effectiveDate));
							
				    		int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
				    		int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
				    		
							/*int currentMonth = UtilDateTime.getMonth(tempMonthStart, timeZone, locale);
							int noOfPastMonths = currentMonth - periodStartMonth;*/
							
				    		//Check Quota Advances taken for current month
				    		
							
							for(int k=0; k<diffMonth; k++){
								Timestamp monthIterStartDate = UtilDateTime.getMonthStart(periodMonthStart);
								Timestamp monthIterEndDate = UtilDateTime.getMonthEnd(periodMonthStart, timeZone, locale);
								conditionList.clear();
								conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
								conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("BILL_TO_CUSTOMER", "ON_BEHALF_OF")));
								conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthIterStartDate));
								conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, monthIterEndDate));
								conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
								
								List<GenericValue> orderHeaderAndRoles = null;
								try {
									orderHeaderAndRoles = delegator.findList("OrderHeaderAndRoles", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
								} catch (GenericEntityException e) {
									Debug.logError(e, "Failed to retrive OrderHeader ", module);
									return ServiceUtil.returnError("Failed to retrive OrderHeader " + e);
								}
								
								List orderIds = EntityUtil.getFieldListFromEntityList(orderHeaderAndRoles,"orderId", true);
								
								BigDecimal totalQuotaUsedUp = BigDecimal.ZERO;
								if(UtilValidate.isNotEmpty(orderIds)){
									conditionList.clear();
									conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
									if(UtilValidate.isNotEmpty(productIdsList)){
										conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdsList));
									}
									
									conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
									conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
									List<GenericValue> orderItemAndAdjustment = null;
									try {
										orderItemAndAdjustment = delegator.findList("OrderItemAndAdjustment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
									} catch (GenericEntityException e) {
										Debug.logError(e, "Failed to retrive OrderHeader ", module);
										return ServiceUtil.returnError("Failed to retrive OrderHeader " + e);
									}
									
									for(int a=0; a<orderItemAndAdjustment.size(); a++){
										totalQuotaUsedUp = totalQuotaUsedUp.add( (BigDecimal)((GenericValue)orderItemAndAdjustment.get(a)).get("quantity") );
									}
								}
								
								totalQuotaUsedUp = totalQuotaUsedUp.add(quotaAdvanceUsed);
								
								if(allocatedQuotaPerMonth.compareTo(totalQuotaUsedUp)>0){
									quotaAdvanceUsed = BigDecimal.ZERO;
								}
								else{
									quotaAdvanceUsed = totalQuotaUsedUp.subtract(allocatedQuotaPerMonth);
								}
								
								periodMonthStart = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(monthIterEndDate, 1));
								
							}
							
						}
						
						// Current month quota operations
						if(quotaAdvanceUsed.compareTo(allocatedQuotaPerMonth)>=0){
							outstandingQuotaAvailable = BigDecimal.ZERO;
						}
						else{
							conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
							conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("BILL_TO_CUSTOMER", "ON_BEHALF_OF")));
							conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getMonthStart(effectiveDate)));
							conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getMonthEnd(effectiveDate, timeZone, locale)));
							conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
							
							List<GenericValue> orderHeaderAndRoles = null;
							try {
								orderHeaderAndRoles = delegator.findList("OrderHeaderAndRoles", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
							} catch (GenericEntityException e) {
								Debug.logError(e, "Failed to retrive OrderHeader ", module);
								return ServiceUtil.returnError("Failed to retrive OrderHeader " + e);
							}
							
							List orderIds = EntityUtil.getFieldListFromEntityList(orderHeaderAndRoles,"orderId", true);
							
							BigDecimal totalQuotaUsedUp = BigDecimal.ZERO;
							if(UtilValidate.isNotEmpty(orderIds)){
								conditionList.clear();
								conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
								if(UtilValidate.isNotEmpty(productIdsList)){
									conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdsList));
								}
								
								conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
								conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
								List<GenericValue> orderItemAndAdjustment = null;
								try {
									orderItemAndAdjustment = delegator.findList("OrderItemAndAdjustment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
								} catch (GenericEntityException e) {
									Debug.logError(e, "Failed to retrive OrderHeader ", module);
									return ServiceUtil.returnError("Failed to retrive OrderHeader " + e);
								}
								
								for(int a=0; a<orderItemAndAdjustment.size(); a++){
									totalQuotaUsedUp = totalQuotaUsedUp.add( (BigDecimal)((GenericValue)orderItemAndAdjustment.get(a)).get("quantity") );
								}
							}
							
							outstandingQuotaAvailable = (allocatedQuotaAdvances.subtract(totalQuotaUsedUp)).subtract(quotaAdvanceUsed);
							
						}
						
						
						// The below commented code is to implement period wise quota.
						/*if(periodTime.compareTo(BigDecimal.ONE)>0){
							// get current period
							
							Timestamp periodMonthStart = periodBegin;
							Timestamp periodMonthEnd = null;
							int periodCount = 12/(periodTime.intValue());
							for(int k=0; k<periodCount; k++){
								Timestamp tempMonthStart = UtilDateTime.getMonthStart(periodMonthStart, 0, (periodTime.intValue() - 1));
								periodMonthEnd = UtilDateTime.getMonthEnd(tempMonthStart, timeZone, locale);
								if(periodMonthEnd.compareTo(effectiveDate) < 0){
									periodMonthStart = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(periodMonthEnd, 1));
								}
								else{
									if( (periodMonthStart.compareTo(effectiveDate) ) <= 0  ){
										break;
									}
								}
							}
							monthStart = periodMonthStart;
							if(UtilValidate.isNotEmpty(periodMonthEnd)){
								monthEnd = periodMonthEnd;
							}
							
						}*/
						
						
						
						Map productCategoryQuotaMap = FastMap.newInstance();
						productCategoryQuotaMap.put("productCategoryId", productCategoryId);
						productCategoryQuotaMap.put("quotaPerMonth", allocatedQuotaPerMonth);
						productCategoryQuotaMap.put("quotaAvailableThisMonth", allocatedQuotaAdvances);
						productCategoryQuotaMap.put("availableQuota", outstandingQuotaAvailable);
						productCategoryQuotaMap.put("categoryQuota", (BigDecimal)schemeProductCategory.get("maxQty"));
						productCategoryQuotaMap.put("looms",  (BigDecimal)((GenericValue)catPartyLooms.get(0)).get("quantity"));
						
						// Add logic to calculate quota already used for the period
						Map tempCatMap = FastMap.newInstance();
						tempCatMap.putAll(productCategoryQuotaMap);
						
						productCategoryQuatasMap.put(productCategoryId, tempCatMap);
					}
				}
			}
			
			Map tempSchemeMap = FastMap.newInstance();
			tempSchemeMap.putAll(productCategoryQuatasMap);
			
			schemesMap.put(schemeId, tempSchemeMap);
		    
		}
		result.put("schemesMap", schemesMap);
	    return result;
   
   	}
   	
	
   	public static Map<String, Object> createOrderPayment(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Locale locale = (Locale) context.get("locale");
		
	    String paymentDate = (String) context.get("paymentDate");
	    String chequeDate = (String) context.get("chequeDate");
	    String orderId = (String) context.get("orderId");
	  	String partyId = (String) context.get("partyId");
	  	String paymentMethodTypeId = (String) context.get("paymentTypeId");
	  	String amount = (String) context.get("amount");
	  	String comments = (String) context.get("comments");
	  	String paymentRefNum = (String) context.get("paymentRefNum");
	  	String issuingAuthority = (String) context.get("issuingAuthority");
	  	String inFavourOf = (String) context.get("inFavourOf");
	  	List advancePayments = (List) context.get("advPayments");
	  	List allStatus = (List) context.get("allStatus");
	  	List advPaymentIds = (List) context.get("advPaymentIds");
	  	if(UtilValidate.isNotEmpty(amount)){
	  	Map<String, Object> serviceContext = UtilMisc.toMap("orderId", orderId, "paymentMethodTypeId", paymentMethodTypeId,"statusId","PMNT_RECEIVED", "userLogin", userLogin);
	  	String orderPaymentPreferenceId = null;
	  	Map<String, Object> createCustPaymentFromPreferenceMap = new HashMap();
	  	
	  	Timestamp eventDate = null;
	  	Timestamp chequeDateTS = null;
	  	
	      if (UtilValidate.isNotEmpty(paymentDate)) {
		      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
				try {
					eventDate = new java.sql.Timestamp(sdf.parse(paymentDate).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				}
			}
	      
	      if (UtilValidate.isNotEmpty(chequeDate)) {
		      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
				try {
					chequeDateTS = new java.sql.Timestamp(sdf.parse(chequeDate).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				}
			}
	      
	     
	  	try {
	    	 result = dispatcher.runSync("createOrderPaymentPreference", serviceContext);
	         orderPaymentPreferenceId = (String) result.get("orderPaymentPreferenceId");
	         Map<String, Object> serviceCustPaymentContext = UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId,"amount",amount,"eventDate",eventDate,"paymentRefNum",paymentRefNum,"issuingAuthority",issuingAuthority,"comments",comments,"inFavourOf",inFavourOf,"instrumentDate",chequeDateTS,"userLogin", userLogin);
	         createCustPaymentFromPreferenceMap = dispatcher.runSync("createCustPaymentFromPreference", serviceCustPaymentContext);
	         String paymentId = (String)createCustPaymentFromPreferenceMap.get("paymentId");
	        /* GenericValue orderPreferencePaymentApplication = delegator.makeValue("OrderPreferencePaymentApplication");
	        
	         orderPreferencePaymentApplication.set("orderPaymentPreferenceId", orderPaymentPreferenceId);
	         orderPreferencePaymentApplication.set("paymentId",paymentId);
	         orderPreferencePaymentApplication.set("amountApplied", new BigDecimal(amount));
				delegator.createSetNextSeqId(orderPreferencePaymentApplication);*/
	  	
	  	} catch (Exception e) {
				 Debug.logError(e, e.toString(), module);
				  return ServiceUtil.returnError("AccountingTroubleCallingCreateOrderPaymentPreferenceService");	
	  	}
       }
        if(UtilValidate.isNotEmpty(allStatus)){
	         
	         for (int i = 0; i < advancePayments.size(); i++) {
	        	 
		         if(allStatus.contains(String.valueOf(i))){
		        	 BigDecimal balance = BigDecimal.ZERO;
			         BigDecimal grandTotal = BigDecimal.ZERO;
			         
			         try {
				         GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), false);
				         
				         grandTotal = orderHeader.getBigDecimal("grandTotal");
				         
				         List conditionList = FastList.newInstance();
				         conditionList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, orderId));
				         conditionList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
				         EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				    	 List<GenericValue> OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
				    	 List<String> orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);
				         
				        conditionList.clear();
			   	        conditionList.add(EntityCondition.makeCondition("orderPaymentPreferenceId", EntityOperator.IN, orderPreferenceIds));
			   	        EntityCondition conditionPay = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   	        List<GenericValue> OrderPreferencePaymentApplication = delegator.findList("OrderPreferencePaymentApplication", conditionPay, null, null, null, false);
					   	    
			   	        BigDecimal paidAmount = BigDecimal.ZERO;
				         
			   	        if(UtilValidate.isNotEmpty(OrderPreferencePaymentApplication))
			   	        {
					        for (GenericValue eachValue : OrderPreferencePaymentApplication) {
					        	paidAmount = paidAmount.add(eachValue.getBigDecimal("amountApplied"));
							}  
			   	        }
			   	         balance = grandTotal.subtract(paidAmount);  
			       }catch (Exception e) {
						 Debug.logError(e, e.toString(), module);
						  return ServiceUtil.returnError("Error While Calculating Applied Amount");	
			  	      }
			        if(balance.floatValue()!=0){  
			         
			            Map<String, Object> serviceContext = UtilMisc.toMap("orderId", orderId, "paymentMethodTypeId", paymentMethodTypeId,"statusId","PMNT_CONFIRMED", "userLogin", userLogin);
				 	  	String orderPaymentPreferenceId = null;
				 	  	Map<String, Object> createCustPaymentFromPreferenceMap = new HashMap();
				 	  	try {
				 	    	 result = dispatcher.runSync("createOrderPaymentPreference", serviceContext);
				 	         orderPaymentPreferenceId = (String) result.get("orderPaymentPreferenceId");
			                }
				 	  	   catch (Exception e) {
							 Debug.logError(e, e.toString(), module);
							  return ServiceUtil.returnError("Error While Creating OrderPayment Preference");	
				  	      }
				 	  	try{
				 	  		
				 	  	   GenericValue orderPreferencePaymentApplication = delegator.makeValue("OrderPreferencePaymentApplication");
				 	  	   String frompaymentId =(String)advPaymentIds.get(i);
				 	  	   String advPay = (String)advancePayments.get(i);
				 	  	   orderPreferencePaymentApplication.set("orderPaymentPreferenceId", orderPaymentPreferenceId);
				           orderPreferencePaymentApplication.set("paymentId",frompaymentId);
				           
				           if(balance.floatValue()>Integer.parseInt(advPay)){
				           orderPreferencePaymentApplication.set("amountApplied", new BigDecimal(advPay));
				           }else{
				        	   orderPreferencePaymentApplication.set("amountApplied", balance);   
				           }
				           delegator.createSetNextSeqId(orderPreferencePaymentApplication);
				 	  	}catch (Exception e) {
							 Debug.logError(e, e.toString(), module);
							  return ServiceUtil.returnError("Error While Creating Order Preference Payment Application");	
				  	      }
			        }
			 	  	
		         }
			}
        }
        if(UtilValidate.isEmpty(amount) && UtilValidate.isEmpty(allStatus))
        {
        	return ServiceUtil.returnError("Please Enter Amount");
        }else{
	  	 result = ServiceUtil.returnSuccess("Successfully Payment Has Been Created For"+orderId);
        }
	  	 return result;
   }
	
   	
   	
   	public static Map<String, Object> createInvoiceApplyPayment(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Locale locale = (Locale) context.get("locale");
		
	    String paymentDate = (String) context.get("paymentDate");
	    String chequeDate = (String) context.get("chequeDate");
	    String invoiceId = (String) context.get("invoiceId");
	  	String partyIdFrom = (String) context.get("partyIdFrom");
	  	String partyIdTo = (String) context.get("partyIdTo");
	  	String paymentMethodTypeId = (String) context.get("paymentTypeId");
	  	String amount = (String) context.get("amount");
	  	String comments = (String) context.get("comments");
	  	String paymentRefNum = (String) context.get("paymentRefNum");
	  	String issuingAuthority = (String) context.get("issuingAuthority");
	  	String inFavourOf = (String) context.get("inFavour");
	  	List advancePayments = (List) context.get("advPayments");
	  	List allStatus = (List) context.get("allStatus");
	  	List advPaymentIds = (List) context.get("advPaymentIds");
	  	
	  	
	  	if(UtilValidate.isNotEmpty(amount)){
	  	Timestamp eventDate = null;
	  	Timestamp chequeDateTS = null;
	  	
	  	
	      if (UtilValidate.isNotEmpty(paymentDate)) {
		      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
				try {
					eventDate = new java.sql.Timestamp(sdf.parse(paymentDate).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				}
			}
	      
	      if (UtilValidate.isNotEmpty(chequeDate)) {
		      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
				try {
					chequeDateTS = new java.sql.Timestamp(sdf.parse(chequeDate).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				}
			}
	      if (UtilValidate.isEmpty(chequeDate)) {
	    	  chequeDateTS = UtilDateTime.nowTimestamp();
	      }
	      
	      if (UtilValidate.isEmpty(paymentDate)) {
	    	  eventDate = UtilDateTime.nowTimestamp();
	      }
	      
          Map<String, Object> paymentParams = new HashMap<String, Object>();
              paymentParams.put("paymentTypeId", "INDENTADV_PAYIN");
              paymentParams.put("paymentMethodTypeId", paymentMethodTypeId);
             // paymentParams.put("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
              paymentParams.put("amount", new BigDecimal(amount));
              paymentParams.put("statusId", "PMNT_RECEIVED");
              paymentParams.put("paymentDate", eventDate);
              paymentParams.put("instrumentDate", chequeDateTS);
              paymentParams.put("partyIdFrom", partyIdFrom);
           //   paymentParams.put("currencyUomId", productStore.getString("defaultCurrencyUomId"));
              paymentParams.put("partyIdTo", partyIdTo);
         
          if (paymentRefNum != null) {
              paymentParams.put("paymentRefNum", paymentRefNum);
          }
          if (issuingAuthority != null) {
              paymentParams.put("issuingAuthority", issuingAuthority);
          }
          if (comments != null) {
              paymentParams.put("comments", comments);
          }
          paymentParams.put("userLogin", userLogin);
         
          Map<String, Object> paymentDetailsMap = FastMap.newInstance();
          String paymentId = "";
          try{
             paymentDetailsMap = dispatcher.runSync("createPayment", paymentParams);
             if (UtilValidate.isNotEmpty(paymentDetailsMap)) {
        	    paymentId = (String) paymentDetailsMap.get("paymentId");
        	    GenericValue paymentAttribute = delegator.makeValue("PaymentAttribute", UtilMisc.toMap("paymentId", paymentId, "attrName", "INFAVOUR_OF"));
 	  	        paymentAttribute.put("attrValue",inFavourOf);
 	  	        paymentAttribute.create();
               }
             
	             Map newPayappl = UtilMisc.toMap("userLogin",userLogin);
	           	newPayappl.put("invoiceId", invoiceId);
	           	newPayappl.put("paymentId", paymentId);
	           	newPayappl.put("amountApplied", new BigDecimal(amount));
           	
           	 Map<String, Object> paymentApplResult = dispatcher.runSync("createPaymentApplication",newPayappl);
          	  if (ServiceUtil.isError(paymentApplResult)) {
                  return ServiceUtil.returnError(ServiceUtil.getErrorMessage(paymentApplResult), null, null, paymentApplResult);
              }
             
          }catch (Exception e) {
			 Debug.logError(e, e.toString(), module);
			  return ServiceUtil.returnError("Error While Creating Payment");	
	  	   }
  	           
       }
        if(UtilValidate.isEmpty(amount))
        {
        	return ServiceUtil.returnError("Please Enter Amount");
        }else{
	  	 result = ServiceUtil.returnSuccess("Successfully Payment Has Been Created For"+invoiceId);
        }
	  	 return result;
   }
	
   	
   	
	public static String processInventorySalesOrder(HttpServletRequest request, HttpServletResponse response) {
   		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		String partyId = (String) request.getParameter("partyId");
		String productId = (String) request.getParameter("productId");
		String quantityStr =(String) request.getParameter("quantity");
		String unitCostStr = (String) request.getParameter("unitCost");
		String effectiveDateStr = (String) request.getParameter("effectiveDate");
		String productStoreId = (String) request.getParameter("productStoreId");
		String disableAcctgFlag = (String) request.getParameter("disableAcctgFlag");
		String salesChannel = (String) request.getParameter("salesChannel");
		String orderId = (String) request.getParameter("orderId");
		String inventoryItemId = (String) request.getParameter("inventoryItemId");
		
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal unitCost = BigDecimal.ZERO;
		Timestamp effectiveDate=null;
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		List productIds = FastList.newInstance();
		if(UtilValidate.isNotEmpty(productId)){
			productIds.add(productId);
		}
		if (UtilValidate.isEmpty(productStoreId)) {
			request.setAttribute("_ERROR_MESSAGE_",	"Branch(ProductStore) is missing.");
			return "error";
		}
		
		try{
			if(UtilValidate.isNotEmpty(unitCostStr)){
				unitCost = new BigDecimal(unitCostStr);
			}
			if(UtilValidate.isNotEmpty(quantityStr)){
				quantity = new BigDecimal(quantityStr);
			}
			if (UtilValidate.isNotEmpty(effectiveDateStr)) {
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
		}
		catch (Exception e) {
			Debug.logError(e, "Problems parsing quantity/unitCost/effectiveDate string.");
			request.setAttribute("_ERROR_MESSAGE_",	"Problems parsing quantity string.");
			return "error";
		}
		
		
		
		
		
		List indentProductList = FastList.newInstance();
		Map<String  ,Object> productQtyMap = FastMap.newInstance();
		Map processOrderContext = FastMap.newInstance();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		productQtyMap.put("productId", productId);
		productQtyMap.put("quantity", quantity);
		productQtyMap.put("basicPrice", unitCost);
		/*productQtyMap.put("batchNo", batchNo);
		productQtyMap.put("daysToStore", daysToStore);
		productQtyMap.put("bedPrice", bedPrice);
		productQtyMap.put("cstPrice", cstPrice);
		productQtyMap.put("tcsPrice", tcsPrice);
		productQtyMap.put("vatPrice", vatPrice);
		productQtyMap.put("serviceTaxPrice", serviceTaxPrice);
		productQtyMap.put("bedPercent", bedPercent);
		productQtyMap.put("vatPercent", vatPercent);
		productQtyMap.put("cstPercent", cstPercent);
		productQtyMap.put("tcsPercent", tcsPercent);
		productQtyMap.put("serviceTaxPercent", serviceTaxPercent);*/

		indentProductList.add(productQtyMap);
		
		processOrderContext.put("userLogin", userLogin);
		processOrderContext.put("productQtyList", indentProductList);
		processOrderContext.put("partyId", partyId);
		processOrderContext.put("billToCustomer", partyId);
		processOrderContext.put("productIds", productIds);
		processOrderContext.put("supplyDate", effectiveDate);
		processOrderContext.put("salesChannel", salesChannel);
		//processOrderContext.put("orderTaxType", orderTaxType);
		//processOrderContext.put("orderId", orderId);
		//processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
		processOrderContext.put("productStoreId", productStoreId);
		/*//processOrderContext.put("PONumber", PONumber);
		processOrderContext.put("promotionAdjAmt", promotionAdjAmt);
		processOrderContext.put("orderMessage", orderMessage);
		processOrderContext.put("orderAdjChargesList", orderAdjChargesList);*/
		processOrderContext.put("disableAcctgFlag", disableAcctgFlag);
		
		try{
		result = processDepotSaleOrder(dctx, processOrderContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
			return "error";
		}
		
		orderId = (String)result.get("orderId");
		if(UtilValidate.isEmpty(orderId)){
			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId);
			return "error";
		}
		
		Map resultCtx = FastMap.newInstance();
		
				resultCtx = dispatcher.runSync("createOrderHeaderSequence",UtilMisc.toMap("orderId", orderId ,"userLogin",userLogin, "orderHeaderSequenceTypeId","DEPOT_SALE_SEQUENCE"));
				if(ServiceUtil.isError(resultCtx)){
					Debug.logError("Problem while Creating  Sequence for orderId:"+orderId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problem while Creating  Sequence for orderId: : "+orderId);
					return "error";
				}
			//Creating Order Item Attribute
			if(UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(inventoryItemId)){
				List<GenericValue> orderItems = null;
				orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId), null, null, null, false);
				if(UtilValidate.isNotEmpty(orderItems)){
					for(GenericValue orderItemEntry : orderItems){
		   				String orderItemSeqId=orderItemEntry.getString("orderItemSeqId");
					try{
	   					GenericValue newEntity = delegator.makeValue("OrderItemAttribute");
	   			        newEntity.set("orderId", orderId);	
	   			        newEntity.set("orderItemSeqId", orderItemSeqId);
	   			        newEntity.set("attrName", "ORDRITEM_INVENTORY_ID");
	   			        newEntity.set("attrValue", inventoryItemId);
	   		            delegator.create(newEntity);
	   		        } catch (GenericEntityException e) {
	   		            Debug.logError(e, module);
	   		            request.setAttribute("_ERROR_MESSAGE_", "Failed to create OrderItemAttribute for orderId: : "+orderId);
						return "error";
	   		        	}
					}
				}
				
			}
			
			}catch(Exception e){
				Debug.logError(e, module);
				return "error";
			}
		
		request.setAttribute("_EVENT_MESSAGE_", "Sale Indent successful for party : "+partyId+" OrderId: "+orderId);
		return "success";
		
   		
   	}
	//Issuance for Depot SalesOrder
   	public static Map<String, Object> createIssuanceForDepotOrder(DispatchContext ctx,Map<String, ? extends Object> context) {
   		
   		Delegator delegator = ctx.getDelegator();
   		LocalDispatcher dispatcher = ctx.getDispatcher();
   		String orderId = (String) context.get("orderId");
   		String inventoryItemId = (String) context.get("inventoryItemId");
   		String shipmentId = (String) context.get("shipmentId");
   	    String facilityId = (String)context.get("facilityId");
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    String shipmentTypeId = (String) context.get("shipmentTypeId");
   	    Map<String, Object> result = ServiceUtil.returnSuccess();
   	    Timestamp issuedDateTime = (Timestamp) context.get("issuedDateTime");
   		try{
   			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), false);
   			
   			/*if(UtilValidate.isNotEmpty(issuedDateTime) && UtilValidate.isNotEmpty(orderHeader.getTimestamp("estimatedDeliveryDate"))){
   				issuedDateTime=	orderHeader.getTimestamp("estimatedDeliveryDate");
   			}*/
   			if(UtilValidate.isEmpty(issuedDateTime)){
   				 issuedDateTime=UtilDateTime.nowTimestamp();
   			}
   			//create shipment if it not exists
   			if(UtilValidate.isEmpty(shipmentId)){
   		        try{
   					GenericValue newEntity = delegator.makeValue("Shipment");
   					if(UtilValidate.isEmpty(shipmentTypeId)){
   						shipmentTypeId="ISSUANCE_SHIPMENT";
   					}
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
   			//setting shipment to order
   			orderHeader.set("shipmentId",shipmentId);
   			orderHeader.store();
   			
   		    List conditionList = FastList.newInstance();
   	        conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
   	        /*conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_ACCEPTED"));*/
   	        conditionList.add(EntityCondition.makeCondition("quantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
   	       
   	        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
   	        List<GenericValue> issuedOrderItems = delegator.findList("OrderItem", condition, null, UtilMisc.toList("orderItemSeqId"), null, false);
   	        
   	        for(GenericValue orderItem : issuedOrderItems){
   				Map prevQtyMap = FastMap.newInstance();
   				String orderItemSeqId=orderItem.getString("orderItemSeqId");
   				BigDecimal requestedQuantity = orderItem.getBigDecimal("quantity");
   				String productId=orderItem.getString("productId");
   				
   				GenericValue orderItemInventoryItem = delegator.findOne("OrderItemAttribute", UtilMisc.toMap("orderId", orderId,"orderItemSeqId",orderItemSeqId,"attrName","ORDRITEM_INVENTORY_ID"), false);
   				if(UtilValidate.isEmpty(orderItemInventoryItem)){
   						return ServiceUtil.returnError("Not issuing InventoryItem for OrderId....! "+orderId);
   				}
   				if(UtilValidate.isEmpty(inventoryItemId)){
   					inventoryItemId=orderItemInventoryItem.getString("attrValue");
   				}
   				GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
   				BigDecimal inventoryQOH = inventoryItem.getBigDecimal("quantityOnHandTotal");
   				if(requestedQuantity.compareTo(inventoryQOH)>0){
   					return ServiceUtil.returnError("Not issuing InventoryItem to orderId "+orderId+" : "+orderItemSeqId+", because the quantity to issue "+requestedQuantity+" is greater than the quantity left to issue (i.e "+inventoryQOH+") for inventoryItemId : "+inventoryItemId);
   				}
   				
   				if(UtilValidate.isEmpty(requestedQuantity) || (UtilValidate.isNotEmpty(requestedQuantity) && requestedQuantity.compareTo(BigDecimal.ZERO)<= 0)){
   					return ServiceUtil.returnError("Not issuing InventoryItem to orderId "+orderId+" : "+orderItemSeqId+", because the quantity to issue "+requestedQuantity+" is less than or equal to 0");
   				}
   		

   				/*//caliculating issuence Qty
   				BigDecimal issuedQty=BigDecimal.ZERO;
   				List filterIssuenceReq = FastList.newInstance();
   				filterIssuenceReq.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
   				filterIssuenceReq.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
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
   		        issuedQty=issuedQty.add(quantity);*/
   		        
   				/*Create Item Issuance*/
   				Map itemIssueCtx = FastMap.newInstance();
   				itemIssueCtx.put("orderId", orderId);
   				itemIssueCtx.put("orderItemSeqId", orderItemSeqId);
   				itemIssueCtx.put("userLogin", userLogin);
   				itemIssueCtx.put("inventoryItemId", inventoryItemId);
   				itemIssueCtx.put("productId", productId);
   				itemIssueCtx.put("quantity", requestedQuantity);
   				itemIssueCtx.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
   				itemIssueCtx.put("modifiedByUserLoginId", userLogin.getString("userLoginId"));
   				itemIssueCtx.put("modifiedDateTime", UtilDateTime.nowTimestamp());
   				if(UtilValidate.isNotEmpty(issuedDateTime)){
   					 itemIssueCtx.put("issuedDateTime", issuedDateTime);
   				}
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
   				createInvDetail.put("orderId", orderId);
   				createInvDetail.put("orderItemSeqId", orderItemSeqId);
   				createInvDetail.put("userLogin", userLogin);
   				createInvDetail.put("inventoryItemId", inventoryItemId);
   				createInvDetail.put("itemIssuanceId", itemIssuanceId);
   				createInvDetail.put("quantityOnHandDiff", requestedQuantity.negate());
   				createInvDetail.put("availableToPromiseDiff", requestedQuantity.negate());
   				if(UtilValidate.isNotEmpty(issuedDateTime)){
   					 itemIssueCtx.put("effectiveDate", issuedDateTime);
   				}
   				resultCtx = dispatcher.runSync("createInventoryItemDetail", createInvDetail);
   				if (ServiceUtil.isError(resultCtx)) {
   					Debug.logError("Problem decrementing inventory for requested item ", module);
   					return resultCtx;
   				}
   				//comparing issuedQty and requestedQty
   				/*if(issuedQty.compareTo(requestedQuantity)==0){
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
   				}*/
   		    }
   	        result = ServiceUtil.returnSuccess("Successfully Issued Selected Order :"+orderId);	
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
   	
   	
   	public static Map<String, Object> preferenceCancel(DispatchContext dctx, Map<String, ? extends Object> context){
   	    Delegator delegator = dctx.getDelegator();
   	    LocalDispatcher dispatcher = dctx.getDispatcher();
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    Locale locale = (Locale) context.get("locale");     
	    Map result = ServiceUtil.returnSuccess();
   	    
   	    List preferenceIds = (List) context.get("preferenceIds");
     	List allStatus = (List) context.get("allStatus");
     	
   	    for (int i = 0; i < preferenceIds.size(); i++) {
			
   	    	if((allStatus.contains(String.valueOf(i)) == true)){
			     try {	
			        GenericValue OrderPaymentPreferenceList = delegator.findOne("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", preferenceIds.get(i)), false);
					OrderPaymentPreferenceList.set("statusId","PMNT_VOID");
					OrderPaymentPreferenceList.store();
			        
			     }catch (Exception e) {
					Debug.logError(e, "Error While change OrderPaymentPreference Status", module);
					return ServiceUtil.returnError("Error While change OrderPaymentPreference Status");
				}
   	    	}
		}
   	    result = ServiceUtil.returnSuccess("Successfully Changed Payment Status!!");
   	    return result;
   	}
   	
   	public static Map<String, Object> realizeStatus(DispatchContext dctx, Map<String, ? extends Object> context){
   	    Delegator delegator = dctx.getDelegator();
   	    LocalDispatcher dispatcher = dctx.getDispatcher();
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    List orderPaymentPreferenceId = (List) context.get("paymentPreferenceId");
     	List allStatus = (List) context.get("allStatus");
     	List paymentStatus = (List) context.get("paymentStatus");
   	    Locale locale = (Locale) context.get("locale");     
   	    Map result = ServiceUtil.returnSuccess();
   		try {
   			/*List conditionList = FastList.newInstance();
   			conditionList.add(EntityCondition.makeCondition("paymentPreferenceId", EntityOperator.EQUALS,paymentPreferenceId));
   			List<GenericValue> PaymentList = null;
   			try {
   				
   				PaymentList = delegator.findList("Payment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
   				GenericValue PaymentFirstList = EntityUtil.getFirst(PaymentList);
   				 String paymentId = (String)PaymentFirstList.get("paymentId");
   				 Map<String, Object> setPaymentStatusMap = UtilMisc.<String, Object>toMap("userLogin", userLogin);
   				 setPaymentStatusMap.put("paymentId", paymentId);
   				 setPaymentStatusMap.put("statusId", "PMNT_CONFIRMED");
   				 Map<String, Object> pmntResults = dispatcher.runSync("setPaymentStatus", setPaymentStatusMap);
   				 if (ServiceUtil.isError(pmntResults)) {
   					 Debug.logError(pmntResults.toString(), module);
   					 return ServiceUtil.returnError(null, null, null, pmntResults);
   				 }*/
   		
   			   for(int i=0;i<orderPaymentPreferenceId.size();i++){
   				   String prference = (String)orderPaymentPreferenceId.get(i);
   				   // String status = (String)allStatus.get(i);
				    String payStatus = (String)paymentStatus.get(i);
   				    if((allStatus.contains(String.valueOf(i)) == true)){
   				    	
   				         if(!(prference.isEmpty()) && (allStatus.contains(String.valueOf(i)))){
   				   
								 try {
									    GenericValue orderPaymentPreferenceList = delegator.findOne("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId.get(i)), false);
									    if (UtilValidate.isNotEmpty(orderPaymentPreferenceList)) {
						 				 
											 if((payStatus.equals("PMNT_VOID"))){
												 orderPaymentPreferenceList.set("statusId","PMNT_VOID");
											 }
											 else{
											   orderPaymentPreferenceList.set("statusId","PMNT_CONFIRMED");
											 }
											 
											 try {
							 		        	orderPaymentPreferenceList.store();
							 		        } catch (GenericEntityException e) {
							 		            Debug.logError(e, module);
							 		            return ServiceUtil.returnError(e.getMessage());
							 		        }
						 			   }
								  } catch (GenericEntityException e) {
							            Debug.logError(e, module);
							            return ServiceUtil.returnError(e.getMessage());
							      }
   				         }//if
		   				if( !(prference.isEmpty()) && (allStatus.contains(String.valueOf(i))) && (payStatus.equals("PMNT_VOID"))){
		   					
		   		   			List conditionList = FastList.newInstance();
		   		   			conditionList.add(EntityCondition.makeCondition("paymentPreferenceId", EntityOperator.EQUALS,prference));
		   		   			List<GenericValue> PaymentList = null;
		   		   			try {
		   		   				
		   		   				PaymentList = delegator.findList("Payment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		   		   				GenericValue PaymentFirstList = EntityUtil.getFirst(PaymentList);
		   		   				 String paymentId = (String)PaymentFirstList.get("paymentId");
		   		   				 Map<String, Object> setPaymentStatusMap = UtilMisc.<String, Object>toMap("userLogin", userLogin);
		   		   				 setPaymentStatusMap.put("paymentId", paymentId);
		   		   				 setPaymentStatusMap.put("statusId", "PMNT_VOID");
		   		   				 Map<String, Object> pmntResults = dispatcher.runSync("setPaymentStatus", setPaymentStatusMap);
		   		   				 if (ServiceUtil.isError(pmntResults)) {
		   		   					 Debug.logError(pmntResults.toString(), module);
		   		   					 return ServiceUtil.returnError(null, null, null, pmntResults);
		   		   				 }
			   		         } catch (GenericEntityException e) {
					            Debug.logError(e, module);
					            return ServiceUtil.returnError(e.getMessage());
		        		     }
		   					
		   				 }
   				   
   				    }
   			   }//for
   		} catch (Exception e) {
   			Debug.logError(e, module);
   			return ServiceUtil.returnError(e.toString());
   		}
   	    result = ServiceUtil.returnSuccess("Successfully Changed Payment Status!!");
   	    return result;
   	}
   	public static Map<String, Object> createStockXferRequest(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String transferDate = (String)context.get("transferDate");
        String fromFacilityId = (String)context.get("fromFacilityId");
        String toFacilityId = (String)context.get("toFacilityId");
        BigDecimal xferQty = (BigDecimal)context.get("xferQty");
        String productId = (String)context.get("productId");
        String statusId = "IXF_REQUESTED";
        String inventoryItemId = (String)context.get("inventoryItemId");
        String transferGroupTypeId = "INTERNAL_XFER";
        String comments = (String)context.get("comments");
        String workEffortId = (String)context.get("workEffortId");
        String transferGroupId = (String) context.get("transferGroupId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if(UtilValidate.isEmpty(transferGroupTypeId)){
       	 transferGroupTypeId = "_NA_";
        }
        
        Timestamp xferDate = null;
        try {
       	 
       	 SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
       	 if(UtilValidate.isNotEmpty(transferDate)){
       		 try {
       			 xferDate = new java.sql.Timestamp(sdf.parse(transferDate).getTime());
         		 } catch (ParseException e) {
         			Debug.logError(e, "Cannot parse date string: "+ transferDate, module);			
         			return ServiceUtil.returnError("Cannot parse date string: "+ transferDate);
         		 }
       	 }else{
       		 xferDate = UtilDateTime.nowTimestamp();
       	 }
       	 if(xferQty.compareTo(BigDecimal.ZERO)<1){
       		 Debug.logError("Transfer Qty cannot be less than 1", module);
  	  			return ServiceUtil.returnError("Transfer Qty cannot be less than 1");
       	 }
       	 List conditionList = FastList.newInstance();
       	 Map<String, Object> resultCtx = ServiceUtil.returnSuccess();
       	 /*Map<String, ? extends Object> serviceCtx =  UtilMisc.toMap("productId", productId, "facilityIdTo", toFacilityId, "quantity", xferQty, "userLogin", userLogin);
            Map<String, Object> resultCtx = dispatcher.runSync("validateProductionTransfers", serviceCtx);
       	 if (ServiceUtil.isError(resultCtx)) {
       		 Debug.logError("Error ::"+ServiceUtil.getErrorMessage(resultCtx), module);
                return ServiceUtil.returnError("Error ::"+ServiceUtil.getErrorMessage(resultCtx));
            }
       	 if(UtilValidate.isEmpty(toFacilityId)){
       		 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, toFacilityId));
       		 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
       		 EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            	 List<GenericValue> productFacility = delegator.findList("ProductFacility", cond, null, null, null, false);
            	 if(UtilValidate.isEmpty(productFacility)){
            		Debug.logError("ProductId[ "+productId+" ] is not associated to store :"+toFacilityId, module);	
            		return ServiceUtil.returnError("ProductId[ "+productId+" ] is not associated to store :"+toFacilityId);
            	 }
         }*/
       	 
       	 boolean departmentTransfers = Boolean.FALSE;
       	 if(UtilValidate.isNotEmpty(toFacilityId)){
       		 
       		 GenericValue sendFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", fromFacilityId), false);
       		 GenericValue receiptFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", toFacilityId), false);
       		 
       		 if((sendFacility.getString("ownerPartyId")).equals(receiptFacility.getString("ownerPartyId"))){
       			 departmentTransfers = Boolean.TRUE;
       		 }
       	 }
       	 
         Map<String, ? extends Object> findCurrInventoryParams =  UtilMisc.toMap("productId", productId, "facilityId", fromFacilityId);
            
            resultCtx = dispatcher.runSync("getInventoryAvailableByFacility", findCurrInventoryParams);
        if (ServiceUtil.isError(resultCtx)) {
        	Debug.logError("Problem getting inventory level of the request for product Id :"+productId, module);
            return ServiceUtil.returnError("Problem getting inventory level of the request for product Id :"+productId);
        }
        Object qohObj = resultCtx.get("availableToPromiseTotal");
        BigDecimal qoh = BigDecimal.ZERO;
        if (qohObj != null) {
        	qoh = new BigDecimal(qohObj.toString());
        }
        
        if (xferQty.compareTo(qoh) > 0) {
        	Debug.logError("Available Inventory level for productId : "+productId + " is "+qoh, module);
            return ServiceUtil.returnError("Available Inventory level for productId : "+productId + " is "+qoh);
        }
       	 
         conditionList.clear();
       	 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, fromFacilityId));
       	 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
       	 if(UtilValidate.isNotEmpty(inventoryItemId)){	
       		 conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId));
       	 }
       	 conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
       	 conditionList.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
       	 EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
       	 List<GenericValue> inventoryItems = delegator.findList("InventoryItem", condition, null, UtilMisc.toList("datetimeReceived"), null, false);
       	 BigDecimal requestedQty = xferQty;

       	 if(UtilValidate.isEmpty(transferGroupId)){
	        	 GenericValue newEntity = delegator.makeValue("InventoryTransferGroup");
	        	 newEntity.set("transferGroupTypeId", transferGroupTypeId);
	        	 newEntity.set("workEffortId", workEffortId);
	        	 newEntity.set("comments", comments);
	 	         newEntity.set("statusId", "IXF_REQUESTED");
	 	         delegator.createSetNextSeqId(newEntity);
	 	         transferGroupId = (String) newEntity.get("transferGroupId");
       	 }
       	 
       	 List<GenericValue> inventoryItemDetail = null;
       	 Iterator<GenericValue> itr = inventoryItems.iterator();
       	 
       	 while ((requestedQty.compareTo(BigDecimal.ZERO) > 0) && itr.hasNext()) {
                GenericValue inventoryItem = itr.next();
                String invItemId = inventoryItem.getString("inventoryItemId");
                BigDecimal itemQOH = inventoryItem.getBigDecimal("availableToPromiseTotal");
                BigDecimal xferQuantity = null;
                if (requestedQty.compareTo(itemQOH) >= 0) {	
               	 xferQuantity = itemQOH;
                } else {
               	 xferQuantity = requestedQty;
                }
                
                
                Map inputCtx = FastMap.newInstance();
                inputCtx.put("statusId", statusId);
                inputCtx.put("comments", comments);
                inputCtx.put("facilityId", fromFacilityId);
                inputCtx.put("facilityIdTo", toFacilityId);
                inputCtx.put("inventoryItemId", invItemId);
                inputCtx.put("sendDate", xferDate);
                inputCtx.put("xferQty", xferQuantity);
                inputCtx.put("userLogin", userLogin);
                Map resultMap = dispatcher.runSync("createInventoryTransfer", inputCtx);
    	  		 if(ServiceUtil.isError(resultMap)){
    	  			Debug.logError("Error in processing transfer entry ", module);
    	  			return ServiceUtil.returnError("Error in processing transfer entry ");
    	  		 }
    	  		 
    	  		 requestedQty = requestedQty.subtract(xferQuantity);
    	  		 
    	  		String inventoryTransferId = (String)resultMap.get("inventoryTransferId");
    	  		
    	  		Timestamp dayStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    	  		Timestamp dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());

    	  		conditionList.clear();
    	  		conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, invItemId));
    	  		conditionList.add(EntityCondition.makeCondition("availableToPromiseDiff", EntityOperator.EQUALS, xferQuantity.negate()));
    	  		conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
    	  		conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
    	  		EntityCondition invDetailCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    	  		inventoryItemDetail = delegator.findList("InventoryItemDetail", invDetailCond, null, UtilMisc.toList("-effectiveDate"), null, false);
    	  		if(UtilValidate.isNotEmpty(inventoryItemDetail)){
    	  			GenericValue invItemDet = EntityUtil.getFirst(inventoryItemDetail);
    	  			invItemDet.set("inventoryTransferId", inventoryTransferId);
    	  			invItemDet.set("effectiveDate", xferDate);
	  				if(UtilValidate.isNotEmpty(workEffortId)){
	  					invItemDet.set("workEffortId", workEffortId);
	  				}
	  				invItemDet.store();
    	  		}
    	  		
    	  		GenericValue newEntityMember = delegator.makeValue("InventoryTransferGroupMember");
    	  		newEntityMember.set("transferGroupId", transferGroupId);
    	  		newEntityMember.set("inventoryTransferId", inventoryTransferId);
    	  		newEntityMember.set("inventoryItemId", invItemId);
    	  		newEntityMember.set("productId", productId);
   	        newEntityMember.set("xferQty", xferQuantity);
   	        newEntityMember.set("createdDate", xferDate);
   	        newEntityMember.set("createdByUserLogin", userLogin.get("userLoginId"));
   	        newEntityMember.set("lastModifiedDate", UtilDateTime.nowTimestamp());
   	        newEntityMember.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
   	        newEntityMember.create();
         }
       	 if(departmentTransfers){
       		 
       		 Map inputCtx = FastMap.newInstance();
                inputCtx.put("statusId", "IXF_COMPLETE");
                inputCtx.put("transferGroupId", transferGroupId);
                inputCtx.put("xferDate", xferDate);
                inputCtx.put("userLogin", userLogin);
                Map resultMap = dispatcher.runSync("updateInternalDeptTransferStatus", inputCtx);
    	  		 if(ServiceUtil.isError(resultMap)){
    	  			Debug.logError("Error in processing transfer entry ", module);
    	  			return ServiceUtil.returnError("Error in processing transfer entry ");
    	  		 }
	         }else{
	        	 String statusIdTo = "IXF_EN_ROUTE";
	        	 GenericValue transferGroup = delegator.findOne("InventoryTransferGroup", UtilMisc.toMap("transferGroupId", transferGroupId), false);
		  		  
		  		  String oldStatusId = transferGroup.getString("statusId");
		  		  GenericValue statusItem = delegator.findOne("StatusValidChange", UtilMisc.toMap("statusId", oldStatusId, "statusIdTo", statusIdTo), false);
		  		  if(UtilValidate.isEmpty(statusItem)){
		  			  Debug.logError("Not a valid status change", module);
		  			  return ServiceUtil.returnError("Not a valid status change");
		  		  }
		  		  transferGroup.set("statusId", statusIdTo);
		  		  transferGroup.store();
		  		  
		  		  List<GenericValue> transferGroupMembers = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("transferGroupId", EntityOperator.EQUALS, transferGroupId), null, null, null, false);
		  		  
		  		  List<String> inventoryTransferIds = EntityUtil.getFieldListFromEntityList(transferGroupMembers, "inventoryTransferId", true);
		  		  
		  		  List<GenericValue> inventoryTransfers = delegator.findList("InventoryTransfer", EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN, inventoryTransferIds), null, null, null, false);
		  		  
		  		  for(GenericValue eachTransfer : inventoryTransfers){
		  			  
		  			  Map inputCtx = FastMap.newInstance();
		              inputCtx.put("inventoryTransferId", eachTransfer.getString("inventoryTransferId"));
		              inputCtx.put("inventoryItemId", eachTransfer.getString("inventoryItemId"));
		              inputCtx.put("statusId", statusIdTo);
		              inputCtx.put("userLogin", userLogin);
		              Map resultInvTrCtx = dispatcher.runSync("updateInventoryTransfer", inputCtx);
		              if(ServiceUtil.isError(resultInvTrCtx)){
		            	  Debug.logError("Error updating inventory transfer status : "+eachTransfer.getString("inventoryTransferId"), module);
		            	  return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultInvTrCtx));
		              }
		  		  }     
	         }
        }
        catch(Exception e){
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.toString());
        }
        
        result = ServiceUtil.returnSuccess("Sucessfully initated transfer");
        return result;
    }
   	public static String updateTransferGroupStatus(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  boolean beginTransaction = false;
	  	  
	  	  try{
	  		  beginTransaction = TransactionUtil.begin();
		  	  for (int i = 0; i < rowCount; i++){
		  		  
		  		  String transferGroupId = "";
		  		  String statusId = "";
		  		  String toFacilityId="";
		  		  String transferQtyStr = "";
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  if (paramMap.containsKey("transferGroupId" + thisSuffix)) {
		  			  transferGroupId = (String) paramMap.get("transferGroupId" + thisSuffix);
		  		  }else{
		  			Debug.logError("Transfer Group Id cannot be empty", module);
		  			request.setAttribute("_ERROR_MESSAGE_", "Transfer Id cannot be empty");
              	TransactionUtil.rollback();
          		return "error";
		  		  }
		  		  
		  		  if (paramMap.containsKey("statusId" + thisSuffix)) {
		  			  statusId = (String) paramMap.get("statusId"+thisSuffix);
		  		  }
		  		  else{
		  			Debug.logError("status cannot be empty", module);
		  			request.setAttribute("_ERROR_MESSAGE_", "status cannot be empty");
              	TransactionUtil.rollback();
          		return "error";
		  		  }
		  		  if (paramMap.containsKey("toFacilityId" + thisSuffix)) {
		  			toFacilityId = (String) paramMap.get("toFacilityId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("transferQty" + thisSuffix)) {
		  			transferQtyStr = (String) paramMap.get("transferQty"+thisSuffix);
		  		  }
		  		  BigDecimal transferQty = BigDecimal.ZERO;
		  		  if(UtilValidate.isNotEmpty(transferQtyStr)){
		  			transferQty =  new BigDecimal(transferQtyStr);
		  		  }
		  		  GenericValue transferGroup = delegator.findOne("InventoryTransferGroup", UtilMisc.toMap("transferGroupId", transferGroupId), false);
		  		  
		  		  String oldStatusId = transferGroup.getString("statusId");
		  		  String workEffortId = transferGroup.getString("workEffortId");
		  		  String transferTypeId = transferGroup.getString("transferGroupTypeId");
		  		  GenericValue statusItem = delegator.findOne("StatusValidChange", UtilMisc.toMap("statusId", oldStatusId, "statusIdTo", statusId), false);
		  		  
		  		  if(UtilValidate.isEmpty(statusItem)){
		  			  Debug.logError("Not a valid status change", module);
		  			  request.setAttribute("_ERROR_MESSAGE_", "Not a valid status change");
		  			  TransactionUtil.rollback();
		  			  return "error";
		  		  }
		  		  
		  		  transferGroup.set("statusId", statusId);
		  		  transferGroup.store();
		  		  
		  		  List<GenericValue> transferGroupMembers = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("transferGroupId", EntityOperator.EQUALS, transferGroupId), null, null, null, false);
		  		  
		  		  List<String> inventoryTransferIds = EntityUtil.getFieldListFromEntityList(transferGroupMembers, "inventoryTransferId", true);
		  		  
		  		  List<GenericValue> inventoryTransfers = delegator.findList("InventoryTransfer", EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN, inventoryTransferIds), null, null, null, false);
		  		  BigDecimal totalIssuedQty = BigDecimal.ZERO;
		  		  Timestamp sendDate =null;
		  		  Timestamp receivedDate =null;
		  		  for(GenericValue eachTransGroup : transferGroupMembers){
		  			  if(UtilValidate.isNotEmpty(eachTransGroup.getBigDecimal("xferQty"))){
		  				totalIssuedQty = totalIssuedQty.add(eachTransGroup.getBigDecimal("xferQty"));
		  			  }
		  		  }
         		  sendDate = (EntityUtil.getFirst(inventoryTransfers)).getTimestamp("sendDate");
         		  receivedDate = (EntityUtil.getFirst(inventoryTransfers)).getTimestamp("receiveDate");
         		  String facilityIdTo = (EntityUtil.getFirst(inventoryTransfers)).getString("facilityIdTo");
         		  
         		  GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityIdTo), false);
         		  
         		    //shipment
        	  	  
         		  	GenericValue newEntity = delegator.makeValue("Shipment");
         		  	if(UtilValidate.isNotEmpty(receivedDate)){
         		  		newEntity.set("estimatedShipDate", receivedDate);
         		  	}else{
         		  		newEntity.set("estimatedShipDate", nowTimeStamp);
         		  	}
         	        
         	        newEntity.set("shipmentTypeId", "TRANSFER_SHIPMENT");
         	        newEntity.set("statusId", "GENERATED");
         	        if(UtilValidate.isNotEmpty(facility)){
         	        	newEntity.put("partyIdFrom",((String)facility.get("ownerPartyId")));
         	        }
         	        newEntity.put("deliveryChallanDate",sendDate);
         	        newEntity.set("createdDate", nowTimeStamp);
         	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
         	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
         	        delegator.createSetNextSeqId(newEntity);            
         	        String shipmentId = (String) newEntity.get("shipmentId");
         		  
         		  String prod = "";
		  		  if((transferQty.compareTo(BigDecimal.ZERO) >0) && (transferQty.compareTo(totalIssuedQty) >0) && !statusId.equals("IXF_CANCELLED")){
		  			  BigDecimal diffTrnsQty = transferQty.subtract(totalIssuedQty);
		  			  GenericValue invTransfer = EntityUtil.getFirst(inventoryTransfers);
		  			  GenericValue tranferGrpMber = EntityUtil.getFirst(transferGroupMembers);
		  			  transferGroup.set("statusId", "IXF_REQUESTED");
			  		  transferGroup.store();
			  		  
			  		  prod = tranferGrpMber.getString("productId");
			  		  
		  			  Map inputXfer = FastMap.newInstance();
		  			  inputXfer.put("xferQty",diffTrnsQty);
		  			  inputXfer.put("fromFacilityId",invTransfer.getString("facilityId"));
		  			  inputXfer.put("toFacilityId",invTransfer.getString("facilityIdTo"));
		  			  inputXfer.put("productId",tranferGrpMber.getString("productId"));
		  			  inputXfer.put("statusId", "IXF_REQUESTED");
		  			  inputXfer.put("transferGroupId",transferGroupId);
		  			  inputXfer.put("userLogin", userLogin);
		  			  Map resultXfer = dispatcher.runSync("createStockXferRequest", inputXfer);
			  		  if(ServiceUtil.isError(resultXfer)){
			  			  Debug.logError("Error while changing quantity."+transferGroupId, module);
			  			  request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultXfer));
			  			  TransactionUtil.rollback();
			  			  return "error";
			  		  }
		  		  }
		  		
		  		  transferGroup.set("statusId", statusId);
		  		  transferGroup.store();
		  		  
		  		  transferGroupMembers.clear();
		  		  transferGroupMembers = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("transferGroupId", EntityOperator.EQUALS, transferGroupId), null, null, null, false);
		  		  inventoryTransferIds.clear();
		  		  inventoryTransferIds = EntityUtil.getFieldListFromEntityList(transferGroupMembers, "inventoryTransferId", true);
		  		  inventoryTransfers.clear();
		  		  inventoryTransfers = delegator.findList("InventoryTransfer", EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN, inventoryTransferIds), null, null, null, false);
		  		  
		  		  for(GenericValue eachTransfer : inventoryTransfers){
		  			  
		  		    //shipmentitem
		  			  
		  			Map<String,Object> itemInMap = FastMap.newInstance();
			        itemInMap.put("shipmentId",shipmentId);
			        itemInMap.put("userLogin",userLogin);
			        itemInMap.put("productId",prod);
			        itemInMap.put("quantity",transferQty);
			        Map resultMap = dispatcher.runSync("createShipmentItem",itemInMap);
			        
			        if (ServiceUtil.isError(resultMap)) {
			        	Debug.logError("Problem creating shipment Item for orderId :", module);
						request.setAttribute("_ERROR_MESSAGE_", "Problem creating shipment Item :");	
						TransactionUtil.rollback();
				  		return "error";
			        }
			        String shipmentItemSeqId = (String)resultMap.get("shipmentItemSeqId");  
		  			  
		  			// shipemtreceipt
			        
			        GenericValue shipmentReceipt = delegator.makeValue("ShipmentReceipt");
			        shipmentReceipt.set("inventoryItemId", eachTransfer.getString("inventoryItemId"));
			        shipmentReceipt.put("shipmentId",shipmentId);
			        shipmentReceipt.put("shipmentItemSeqId",shipmentItemSeqId);
			        shipmentReceipt.set("productId", prod);
			        shipmentReceipt.set("statusId", "SR_ACCEPTED");
			        shipmentReceipt.put("datetimeReceived",sendDate);
         	        delegator.createSetNextSeqId(shipmentReceipt);            
         	        String receiptId = (String) shipmentReceipt.get("receiptId");
			        
		  			if(UtilValidate.isNotEmpty(toFacilityId)){
		  				if(!toFacilityId.equalsIgnoreCase(eachTransfer.getString("facilityIdTo"))){
		  					eachTransfer.set("facilityIdTo", toFacilityId);
		  					 if(UtilValidate.isNotEmpty(sendDate)){
		  						eachTransfer.set("receiveDate", sendDate);
					  		  }
		  					eachTransfer.store();
		  				}
		  			}
		  			 
		  			Map inputCtx = FastMap.newInstance();
		            inputCtx.put("inventoryTransferId", eachTransfer.getString("inventoryTransferId"));
		            inputCtx.put("inventoryItemId", eachTransfer.getString("inventoryItemId"));
		            inputCtx.put("statusId", statusId);
		            if(UtilValidate.isNotEmpty(sendDate)){
		            	inputCtx.put("receiveDate", sendDate);
			  		}
			  		inputCtx.put("userLogin", userLogin);
		            Map resultCtx = dispatcher.runSync("updateInventoryTransfer", inputCtx);
		            if(ServiceUtil.isError(resultCtx)){
		              Debug.logError("Error updating inventory transfer status : "+eachTransfer.getString("inventoryTransferId"), module);
		              request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultCtx));
		              TransactionUtil.rollback();
		              return "error";
		            }
		              
		            if(UtilValidate.isNotEmpty(statusId) && statusId.equals("IXF_COMPLETE") && transferTypeId.equals("RETURN_XFER") && UtilValidate.isNotEmpty(transferGroup.get("workEffortId"))){
		            		
		            String facIdTo = eachTransfer.getString("facilityIdTo");
		            	  GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", eachTransfer.getString("inventoryItemId")), false);
		            	  inventoryItem.set("facilityId", facIdTo);
		            	  inventoryItem.store();
		            }
		              
		            if(UtilValidate.isNotEmpty(statusId) && statusId.equals("IXF_CANCELLED") && transferTypeId.equals("RETURN_XFER") && UtilValidate.isNotEmpty(transferGroup.get("workEffortId"))){
		            	  List<GenericValue> tranferGroupMembers = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("transferGroupId", EntityOperator.EQUALS, transferGroupId), null, null, null, false);
		            	  for(GenericValue eachXferGroup : tranferGroupMembers){
		            		  inputCtx.clear();
			            	  inputCtx.put("inventoryItemId", eachXferGroup.getString("inventoryItemId"));
			            	  inputCtx.put("availableToPromiseDiff", (eachXferGroup.getBigDecimal("xferQty")).negate());
			            	  inputCtx.put("quantityOnHandDiff", (eachXferGroup.getBigDecimal("xferQty")).negate());
			            	  inputCtx.put("userLogin", userLogin);
				              Map resultService = dispatcher.runSync("createInventoryItemDetail", inputCtx);
				              if(ServiceUtil.isError(resultService)){
				            	  Debug.logError("Error while rejecting return for production run : "+workEffortId, module);
				            	  request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultService));
				            	  TransactionUtil.rollback();
				            	  return "error";
				              }
		            	  }
		              }
		  		  }
		  		if((transferQty.compareTo(BigDecimal.ZERO) >0) && (transferQty.compareTo(totalIssuedQty) <0) && !statusId.equals("IXF_CANCELLED")){
		  			  BigDecimal diffTrnsQty =totalIssuedQty.subtract(transferQty);
		  			  GenericValue invXfer = EntityUtil.getFirst(inventoryTransfers);
		  			  GenericValue invXferGrpMber = EntityUtil.getFirst(transferGroupMembers);
		  			  List<String> facilityList = FastList.newInstance();
		  			  String fromFacilityId = invXfer.getString("facilityId");
		  			  facilityList.add(fromFacilityId);
		  			  String toFacltId = invXfer.getString("facilityIdTo");
		  			  facilityList.add(toFacltId);
		  			  String productId = invXferGrpMber.getString("productId");
		  			  String comments = "Quantity reduced for "+transferGroupId;
			  		  for(GenericValue eachTransfer : inventoryTransfers){
				  			  eachTransfer.set("statusId","IXF_REQUESTED");
				  			  eachTransfer.store();
			  		  }	  
		  			  transferGroup.set("statusId", "IXF_REQUESTED");
			  		  transferGroup.store();
			  		  Map inputXfer = FastMap.newInstance();
		  			  inputXfer.put("xferQty",diffTrnsQty);
		  			  inputXfer.put("fromFacilityId",toFacltId);
		  			  inputXfer.put("toFacilityId",fromFacilityId);
		  			  inputXfer.put("productId",productId);
		  			  inputXfer.put("statusId", "IXF_REQUESTED");
		  			  inputXfer.put("transferGroupId",transferGroupId);
		  			  inputXfer.put("userLogin", userLogin);
		  			  Map resultXfer = dispatcher.runSync("createStockXferRequest", inputXfer);
			  		  if(ServiceUtil.isError(resultXfer)){
			  			  Debug.logError("Error while reducing quantity."+transferGroupId, module);
			  			  request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultXfer));
			  			  TransactionUtil.rollback();
			  			  return "error";
			  		  }
			  		  transferGroup.set("statusId", statusId);
			  		  transferGroup.store();
			  		  transferGroupMembers.clear();
			  		  transferGroupMembers = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("transferGroupId", EntityOperator.EQUALS, transferGroupId), null, null, null, false);
			  		  inventoryTransferIds.clear();
			  		  inventoryTransferIds = EntityUtil.getFieldListFromEntityList(transferGroupMembers, "inventoryTransferId", true);
			  		  inventoryTransfers.clear();
			  		  inventoryTransfers = delegator.findList("InventoryTransfer", EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN, inventoryTransferIds), null, null, null, false);
			  		for(GenericValue eachTransfer : inventoryTransfers){
			  			  Map inputCtx = FastMap.newInstance();
			              inputCtx.put("inventoryTransferId", eachTransfer.getString("inventoryTransferId"));
			              inputCtx.put("inventoryItemId", eachTransfer.getString("inventoryItemId"));
			              inputCtx.put("statusId", statusId);
			              inputCtx.put("userLogin", userLogin);
			              Map resultCtx = dispatcher.runSync("updateInventoryTransfer", inputCtx);
			              if(ServiceUtil.isError(resultCtx)){
			            	  Debug.logError("Error updating inventory transfer status : "+eachTransfer.getString("inventoryTransferId"), module);
			            	  request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultCtx));
			            	  TransactionUtil.rollback();
			            	  return "error";
			              }
			  		}    
		  			
		  		  }
		  	  }
	  	  }
	  	catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beginTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  			request.setAttribute("_ERROR_MESSAGE_", e2.toString());
	  			return "error";
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  	}
	  	catch (GenericServiceException e) {
	  		try {
			  TransactionUtil.rollback(beginTransaction, "Error while calling services", e);
	  		} catch (GenericEntityException e2) {
			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
			  request.setAttribute("_ERROR_MESSAGE_", e2.toString());
			  return "error";
	  		}
	  		Debug.logError("An entity engine error occurred while calling services", module);
	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beginTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			request.setAttribute("_ERROR_MESSAGE_", e.toString());
	  			return "error";
	  		}
	  	}
	  	request.setAttribute("_EVENT_MESSAGE_", "Entry Successfully");
		return "success";  
   }
   	public static Map<String, Object> getFacilityDetails(DispatchContext dctx, Map<String, ? extends Object> context){
   	    Delegator delegator = dctx.getDelegator();
   	    LocalDispatcher dispatcher = dctx.getDispatcher();
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    Map result = ServiceUtil.returnSuccess();
   	    
   	    List<GenericValue> facility = null;
   	    // add this condition after depot configuration is done, EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT")
	   	 try{
	   		facility = delegator.findList("Facility", null, null, null, null, false);
	   	 } catch (Exception e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
		 }	
   	    
   	  result.put("fromFacility",facility); 
   	  result.put("toFacility",facility); 
   	  return result;
     	
   	}
   	
   	public static Map<String, Object> populateBranchDepots(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        Map<String, Object> result = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
       
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        String productStoreId = (String) context.get("productStoreId");
        
        // If productStoreId is not empty, fetch only courses related to the productStore
        List depotsList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(productStoreId)){
        	List condList =FastList.newInstance();
   	    	condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
   	    	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
   	    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimeStamp)));
   	    	EntityCondition prodStrFacCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
   	    	try{
   	    		depotsList = delegator.findList("FacilityAndProductStoreFacility", prodStrFacCondition, null, null, null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
   	    }
        String ownerPartyId = "";
        if(UtilValidate.isNotEmpty(depotsList)){
        	ownerPartyId = (String)EntityUtil.getFirst(depotsList).get("ownerPartyId");
        }
        result.put("ownerPartyId", ownerPartyId);
		result.put("depotsList", depotsList);
        return result;
    }
   	
   	public static Map<String, Object> getCustomerBranch(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        Map<String, Object> result = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
       
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        
        String partyId = (String) context.get("partyId");
        if(UtilValidate.isEmpty(partyId)){
        	partyId = (String) userLogin.get("partyId");
        }
        List productStoreList = FastList.newInstance();

        // Get all CFC's
        List cfcList = FastList.newInstance();
        List condList =FastList.newInstance();
    	condList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, "CFC"));
    	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimeStamp)));
    	try{
    		cfcList = delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
    	}catch (GenericEntityException e) {
			// TODO: handle exception
    		Debug.logError(e, module);
		}
        
        if(UtilValidate.isNotEmpty(partyId)){
        	
        	List conditionList = FastList.newInstance();
  			conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
  			conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN,UtilMisc.toList( "GROUP_ROLLUP","BRANCH_CUSTOMER")));
  			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, UtilMisc.toList("EMPANELLED_CUSTOMER", "BRANCH_EMPLOYEE") ));
  	        conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
  	        
  			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimeStamp)));
			
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  	
			List orgsList = FastList.newInstance();
			try{
				orgsList = delegator.findList("PartyRelationship", condition, null, UtilMisc.toList("partyIdFrom"), null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
        	
			if(UtilValidate.isNotEmpty(orgsList)){
				
	   	    	condList.clear();
	   	    	condList.add(EntityCondition.makeCondition("payToPartyId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(orgsList, "partyIdFrom", true)));
	   	    	condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.NOT_IN, EntityUtil.getFieldListFromEntityList(cfcList, "productStoreId", true)));
	   	    	EntityCondition prodStrCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
	   	    	try{
	   	    		productStoreList = delegator.findList("ProductStore", prodStrCondition, null, null, null, false);
	   	    	}catch (GenericEntityException e) {
	   				// TODO: handle exception
	   	    		Debug.logError(e, module);
	   			}
			}
        	
   	    }
        
        if(UtilValidate.isEmpty(productStoreList)){
        	try{
        		condList.clear();
        		if(UtilValidate.isNotEmpty(cfcList)){
        			condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.NOT_IN, EntityUtil.getFieldListFromEntityList(cfcList, "productStoreId", true)));
        		}
   	    		productStoreList = delegator.findList("ProductStore", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
        }
		
		result.put("productStoreList", productStoreList);
        return result;
    }
   	public static Map<String, Object> getChildCategories(DispatchContext dctx, Map<String, ? extends Object> context){
   	    Delegator delegator = dctx.getDelegator();
   	    LocalDispatcher dispatcher = dctx.getDispatcher();
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    String primaryParentCategoryId = (String) context.get("primaryParentCategoryId");
   	    Map result = ServiceUtil.returnSuccess();
   	    
   	    List<GenericValue> productCategory = null;
   	    try{
   	    	productCategory = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS,primaryParentCategoryId), null, null, null, false);
   	    } catch (Exception e) {
   	    	Debug.logError(e, module);
   	    	return ServiceUtil.returnError(e.toString());
   	    }
   	    
   	    List productCategoryAssocTypeIdsList = FastList.newInstance();
   	    List<GenericValue> assocProdCategories = null;
	    try{
	    	assocProdCategories = delegator.findList("ProductCategoryAssoc", EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,primaryParentCategoryId), null, null, null, false);
	    	if(UtilValidate.isNotEmpty(assocProdCategories)){
	    		productCategoryAssocTypeIdsList = EntityUtil.getFieldListFromEntityList(assocProdCategories, "productCategoryAssocTypeId", true);
	    	}
	    } catch (Exception e) {
	    	Debug.logError(e, module);
	    	return ServiceUtil.returnError(e.toString());
	    }
   	    
	    Map productCatAssocMap = FastMap.newInstance();
	   	for(int i=0; i<productCategoryAssocTypeIdsList.size(); i++){
	   		String productCatAssocType = (String) productCategoryAssocTypeIdsList.get(i);
	   		productCatAssocMap.put(productCatAssocType, EntityUtil.filterByCondition(assocProdCategories, EntityCondition.makeCondition("productCategoryAssocTypeId", EntityOperator.EQUALS, productCatAssocType)));
	   	}
	    
	   	List<GenericValue> productCategoryAssocTypeList = null;
	   	try{
	   		productCategoryAssocTypeList = delegator.findList("ProductCategoryAssocType", EntityCondition.makeCondition("productCategoryAssocTypeId", EntityOperator.IN, productCategoryAssocTypeIdsList), null, null, null, false);
	   	} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
	   	
   	    result.put("childCategoriesList",productCategory);
   	    result.put("productCatAssocMap",productCatAssocMap);
   	    result.put("productCategoryAssocTypeList",productCategoryAssocTypeList);
   	    return result;
   	}
   	
	public static Map<String, Object> getAttributeTypes(DispatchContext dctx, Map<String, ? extends Object> context){
   	    Delegator delegator = dctx.getDelegator();
   	    LocalDispatcher dispatcher = dctx.getDispatcher();
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    String productCategoryId = (String) context.get("productCategoryId");
   	    Map result = ServiceUtil.returnSuccess();
   	    
	   	List<GenericValue> productCategoryAttribute = null;
	   	try{
	   		productCategoryAttribute = delegator.findList("ProductCategoryAttribute", EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,productCategoryId), null, UtilMisc.toList("sequenceId"), null, false);
	   	} catch (Exception e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
		}	
	   	
	   	List productAttributeTypesList = EntityUtil.getFieldListFromEntityList(productCategoryAttribute, "attrTypeId", true);
	   	
	   	Map productAttributesMap = FastMap.newInstance();
	   	for(int i=0; i<productAttributeTypesList.size(); i++){
	   		String productAttributeType = (String) productAttributeTypesList.get(i);
	   		productAttributesMap.put(productAttributeType, EntityUtil.filterByCondition(productCategoryAttribute, EntityCondition.makeCondition("attrTypeId", EntityOperator.EQUALS, productAttributeType)));
	   	}
	   	
	   	List condList =FastList.newInstance();
    	condList.add(EntityCondition.makeCondition("attrTypeId", EntityOperator.IN, productAttributeTypesList));
    	condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
	   	
	   	List<GenericValue> productCategoryAttributeTypesList = null;
	   	try{
	   		productCategoryAttributeTypesList = delegator.findList("ProductCategoryAttributeType", EntityCondition.makeCondition(condList, EntityOperator.AND), null, UtilMisc.toList("sequenceId"), null, false);
	   	} catch (Exception e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
		}	
	   	
	   	result.put("productCategoryAttributeTypesList",productCategoryAttributeTypesList);    
	   	result.put("productAttributeTypesList",productAttributeTypesList);
	   	result.put("productAttributesMap",productAttributesMap);
	   	return result;
	     	
   	}
	
	/*public static Map<String, Object> CreateNewProduct(DispatchContext dctx, Map<String, ? extends Object> context){
   	    Delegator delegator = dctx.getDelegator();
   	    LocalDispatcher dispatcher = dctx.getDispatcher();
   	    Map result = ServiceUtil.returnSuccess();
   	    
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    String productCategoryId = (String) context.get("productCategoryId");
   	    String childProductCategoryId = (String) context.get("childProductCategoryId");
   	    String subChildProductCategoryId = (String) context.get("subChildProductCategoryId");
   	    String packingTypes = (String) context.get("packingTypes");
   	    String spinningTypes = (String) context.get("spinningTypes");
   	    String processingTypes = (String) context.get("processingTypes");
   	    String ply = (String) context.get("ply");
   	    String count = (String) context.get("count");
   	    String uomTypes = (String) context.get("uomTypes");
   	 
   	    
   	    Debug.log("packingTypes====="+packingTypes);
	   	Debug.log("spinningTypes====="+spinningTypes);
	   	Debug.log("processingTypes====="+processingTypes);
	   	Debug.log("ply====="+ply);
	   	Debug.log("count====="+count);
   	    
   	    
   	    List<GenericValue> productCategoryAttribute = null;
   	 
	   	try{
	   		productCategoryAttribute = delegator.findList("ProductCategoryAttribute", null, null, null, null, false);
	   	} catch (Exception e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
		}
   	    
	   	Debug.log("productCategoryAttribute====="+productCategoryAttribute);
	   	
	   	List<GenericValue> packingParentType = EntityUtil.filterByCondition(productCategoryAttribute, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, packingTypes));
	   	List<GenericValue> spinningParentType = EntityUtil.filterByCondition(productCategoryAttribute, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, spinningTypes));
	   	List<GenericValue> processingParentType = EntityUtil.filterByCondition(productCategoryAttribute, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, processingTypes));
	   	List<GenericValue> plyandcountParentType = EntityUtil.filterByCondition(productCategoryAttribute, EntityCondition.makeCondition("attrName", EntityOperator.IN, UtilMisc.toList(ply,count)));
	   	List<GenericValue> uomParentType = EntityUtil.filterByCondition(productCategoryAttribute, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, uomTypes));
	   	
	   	Debug.log("packingParentType====="+packingParentType);
	   	Debug.log("spinningParentType====="+spinningParentType);
	   	Debug.log("processingParentType====="+processingParentType);
	   	Debug.log("plyandcountParentType====="+plyandcountParentType);
	   	Debug.log("uomParentType====="+uomParentType);
	   	
	   	String productName = "";
	   	if(productCategoryId.equals("COTTON")){
	   		productName = ply + "/" + count + uomTypes + processingTypes +  spinningTypes + packingTypes + subChildProductCategoryId + productCategoryId;
	   	}
	   	
	   	Debug.log("productName====="+productName);
	   	
	   	
	   	
	   	
	   	String productId = null;
	   	GenericValue newEntity = delegator.makeValue("Product");
		if(UtilValidate.isNotEmpty(productName)){
        	newEntity.set("productName", productName);
        }
	   	
	   	try {
            delegator.createSetNextSeqId(newEntity);            
            productId = (String) newEntity.get("productId");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
	   	
	   	//Create product attributes
	   	
	   	Debug.log("productId======"+productId);
	   	
	   	GenericValue productAttribute = delegator.makeValue("ProductAttribute");
	   	try{
		   	if(UtilValidate.isNotEmpty(productId)){
		   		productAttribute.set("productId", productId);
	        }
		   	if(UtilValidate.isNotEmpty(packingParentType)){
		   		String attrName = (String)EntityUtil.getFirst(packingParentType).get("attrType");
		   		productAttribute.set("attrName", attrName);
		   		productAttribute.set("attrValue", packingTypes);
		   		delegator.createOrStore(productAttribute);
	        }
		   	Debug.log("1111111111111");
		   	if(UtilValidate.isNotEmpty(spinningParentType)){
		   		String attrName = (String)EntityUtil.getFirst(spinningParentType).get("attrType");
		   		productAttribute.set("attrName", attrName);
		   		productAttribute.set("attrValue", spinningTypes);
		   		delegator.createOrStore(productAttribute);
	        }
		   	Debug.log("2222222222222222");
		   	if(UtilValidate.isNotEmpty(processingParentType)){
		   		String attrName = (String)EntityUtil.getFirst(processingParentType).get("attrType");
		   		productAttribute.set("attrName", attrName);
		   		productAttribute.set("attrValue", processingTypes);
		   		delegator.createOrStore(productAttribute);
	        }
		   	Debug.log("3333333333333333333");
		   	if(UtilValidate.isNotEmpty(plyandcountParentType)){
		   		String attrName = (String)EntityUtil.getFirst(plyandcountParentType).get("attrType");
		   		productAttribute.set("attrName", attrName);
		   		productAttribute.set("attrValue", ply);
		   		delegator.createOrStore(productAttribute);
	        }
		   	Debug.log("4444444444444");
		   	if(UtilValidate.isNotEmpty(plyandcountParentType)){
		   		String attrName = (String)EntityUtil.getFirst(plyandcountParentType).get("attrType");
		   		productAttribute.set("attrName", attrName);
		   		productAttribute.set("attrValue", count);
		   		delegator.createOrStore(productAttribute);
	        }
		   	Debug.log("555555555555555");
		   	if(UtilValidate.isNotEmpty(uomParentType)){
		   		String attrName = (String)EntityUtil.getFirst(uomParentType).get("attrType");
		   		productAttribute.set("attrName", attrName);
		   		productAttribute.set("attrValue", uomTypes);
		   		delegator.createOrStore(productAttribute);
	        }
   	    
	   	} catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
   	    
   	    return result;
  	
	}*/
	
	public static String CreateNewProduct(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		HttpSession session = request.getSession();
	  	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map result = ServiceUtil.returnSuccess();
		
		String productCategoryId = (String) request.getParameter("productCategoryId");
		String childProductCategoryId = (String) request.getParameter("childProductCategoryId");
		
		List productCategoryAttributeTypesList = FastList.newInstance();
		String productId = null;
	   	try{
			Map attTypesMap = FastMap.newInstance();
			attTypesMap.put("userLogin", userLogin);
			attTypesMap.put("productCategoryId", childProductCategoryId);
			result = dispatcher.runSync("getAttributeTypes", attTypesMap);
			if (ServiceUtil.isError(result)) {
				request.setAttribute("_ERROR_MESSAGE_","Error Creating Product ");
				return "error";
			}
			productCategoryAttributeTypesList = (List) result.get("productCategoryAttributeTypesList");
		}
		catch (Exception e) {
			Debug.logError(e, "Error Creating Product ", module);
			request.setAttribute("_ERROR_MESSAGE_","Error Creating Product ");
			return "error";
	    }
		
	   	productCategoryAttributeTypesList = EntityUtil.orderBy(productCategoryAttributeTypesList, UtilMisc.toList("namingSeqId"));
	   	
		if(UtilValidate.isEmpty(productCategoryAttributeTypesList)){
			Debug.logError("No Product Attributes were configured for "+childProductCategoryId, module);
			request.setAttribute("_ERROR_MESSAGE_","No Product Attributes were configured"+childProductCategoryId);
			return "error";
		}
		String categoryName = "";
		if(UtilValidate.isNotEmpty(childProductCategoryId)){
	   		try{
				GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", childProductCategoryId), false);
				if(UtilValidate.isEmpty(productCategory)){
					request.setAttribute("_ERROR_MESSAGE_","Not a valid Party");
					return "error";
				}
				categoryName = (String) productCategory.get("categoryName");
			}catch(GenericEntityException e){
				Debug.logError(e, "Error fetching ProductCategory", module);
				request.setAttribute("_ERROR_MESSAGE_","Error fetching ProductCategory");
				return "error";
			}
	   	}
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String schemeCategoryId = (String) paramMap.get("SCHEME_APPLICABILITY");
		
		// SPECIAL CONDITIONS FOR LINEN, HAVE TO CALCULATE BOTH UOMS TO SHOW IN ITS NAME		
		String leaCount = "";
		String nmCount = "";
		if(productCategoryId.equals("LINEN")){
			if( (paramMap.get("UOM")).equals("NM")){
				nmCount = (String) paramMap.get("COUNT");
				leaCount = ((new BigDecimal(nmCount)).divide(new BigDecimal("0.6"))).setScale(0,rounding).toString();
			}
			if((paramMap.get("UOM")).equals("LEA")){
				leaCount = (String) paramMap.get("COUNT");
				nmCount = ((new BigDecimal(leaCount)).multiply(new BigDecimal("0.6"))).setScale(0,rounding).toString();
			}
		}	
				
		// Product Name Creation
		String productName = "";
		for(int i=0; i<productCategoryAttributeTypesList.size(); i++){
			String attribute = (String) ((GenericValue) productCategoryAttributeTypesList.get(i)).get("attrTypeId");
			String attrValue = (String) paramMap.get(attribute);
			if(UtilValidate.isEmpty(attrValue) || (attrValue.equals("N"))){
				continue;
			}
			if(attribute.equals("PACKING")){
				productName = productName + categoryName + " ";
			}
			
			if( (attribute.equals("PLY")) && (attrValue.equals("1")) ){
				continue;
			}
			
			if(productCategoryId.equals("LINEN")){
				if( attribute.equals("COUNT") ){
					productName = productName + leaCount + "LEA" +"/"+ nmCount + "NM" + " ";
					continue;
				}
				if( attribute.equals("UOM") ){
					continue;
				}
			}
			
			productName = productName + attrValue;
			if(attribute.equals("PLY")){
				if( (productCategoryId.equals("COTTON")) || (productCategoryId.equals("WOOL")) || (productCategoryId.equals("STAPLE")) || (productCategoryId.equals("ACRYLIC")) || (productCategoryId.equals("BLENDED")) || (productCategoryId.equals("POLYESTER"))){
					productName = productName + "/";
				}
				else{
					productName = productName + "PLY ";
				}
			}
			else if(attribute.equals("COUNT")){
				
			}
			else{
				productName = productName + " ";
			}
		}
		
		// Check if the product already exists
		try {
    		List existingProdList = delegator.findList("Product", EntityCondition.makeCondition("productName", EntityOperator.EQUALS, productName), null, null, null, false);
		
    		if(UtilValidate.isNotEmpty(existingProdList)){
    			String existingProdId = (String)(EntityUtil.getFirst(existingProdList)).get("productId");
    			Debug.logError("This Product Already Exists with ID : "+existingProdId, module);
    			request.setAttribute("_ERROR_MESSAGE_","This Product Already Exists with ID : "+existingProdId);
    			return "error";
    		}	
		
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive Product", module);
			request.setAttribute("_ERROR_MESSAGE_","Failed to retrive Product");
			return "error";
		}
		
		// Product Creation
	   	try{
			Map newProduct = FastMap.newInstance();
			newProduct.put("userLogin", userLogin);
			newProduct.put("productTypeId", "FINISHED_GOOD");
			newProduct.put("primaryProductCategoryId", childProductCategoryId);
			newProduct.put("internalName", productName);
			newProduct.put("brandName", productName);
			newProduct.put("productName", productName);
			newProduct.put("description", productName);
			newProduct.put("isVirtual", "N");
			newProduct.put("isVariant", "Y");
			newProduct.put("quantityIncluded",new BigDecimal(1));
			newProduct.put("quantityUomId", "WT_kg");
			newProduct.put("longDescription", productName);
			result = dispatcher.runSync("createProduct", newProduct);
			productId = (String)result.get("productId");
			if (ServiceUtil.isError(result)) {
				request.setAttribute("_ERROR_MESSAGE_","Error Creating Product ");
				return "error";
			}
		}
		catch (Exception e) {
			Debug.logError(e, "Error Creating Product ", module);
			request.setAttribute("_ERROR_MESSAGE_","Error Creating Product ");
			return "error";
	    }
	   	
	   	// Add product to its category
	   	Map productCatgMap = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(childProductCategoryId)){
			try{
				productCatgMap.put("productCategoryId", childProductCategoryId);
				productCatgMap.put("productId", productId);
				productCatgMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
				productCatgMap.put("userLogin", userLogin);
				result = dispatcher.runSync("addProductToCategory", productCatgMap);
			}
			catch(Exception e){
				Debug.logError(e, "Error Creating Product Attribute", module);
				request.setAttribute("_ERROR_MESSAGE_","Error Creating Product Attribute");
				return "error";
			}
		}
	   	
	   	// Add product to one of Scheme category as well
		productCatgMap.clear();
		if(UtilValidate.isNotEmpty(schemeCategoryId)){
			try{
				productCatgMap.put("productCategoryId", schemeCategoryId);
				productCatgMap.put("productId", productId);
				productCatgMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
				productCatgMap.put("userLogin", userLogin);
				result = dispatcher.runSync("addProductToCategory", productCatgMap);
			}
			catch(Exception e){
				Debug.logError(e, "Error Creating Product Attribute", module);
				request.setAttribute("_ERROR_MESSAGE_","Error Creating Product Attribute");
				return "error";
			}
		}
	   	
	   	// Prod Attributes Creation
		for(int i=0; i<productCategoryAttributeTypesList.size(); i++){
			String attribute = (String) ((GenericValue) productCategoryAttributeTypesList.get(i)).get("attrTypeId");
			String attrValue = (String) paramMap.get(attribute);
			
			try{
				Map productAttributeMap = FastMap.newInstance();
				productAttributeMap.put("productId",productId);
				productAttributeMap.put("attrName", attribute);
				productAttributeMap.put("attrValue",attrValue);
				productAttributeMap.put("userLogin", userLogin);
				result = dispatcher.runSync("createProductAttribute", productAttributeMap);
				if (ServiceUtil.isError(result)) {
					Debug.logError("Error Creating Product Attribute "+attribute, module);
					request.setAttribute("_ERROR_MESSAGE_","Error Creating Product Attribute "+attribute);
					return "error";
				}
			}
			catch(Exception e){
				Debug.logError(e, "Error Creating Product Attribute "+attribute, module);
				request.setAttribute("_ERROR_MESSAGE_","Error Creating Product Attribute "+attribute);
				return "error";
			}
			
		}
		
		request.setAttribute("_EVENT_MESSAGE_", "Product Successfully Created : "+productId);
		return "success";
	}
	public static Map<String, Object> getCategoryMembers(DispatchContext dctx, Map<String, ? extends Object> context){
   	    Delegator delegator = dctx.getDelegator();
   	    LocalDispatcher dispatcher = dctx.getDispatcher();
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    String productCategoryId = (String) context.get("productCategoryId");
   	    Map result = ServiceUtil.returnSuccess();
   	    Timestamp now = UtilDateTime.nowTimestamp();
   	    
   	    List<GenericValue> productCategoryMember = null;
   	    
   	    List condsList = FastList.newInstance();
	  	condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
	  	condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, now));
	  	condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
				EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, now)));
		
		EntityCondition priceCond = EntityCondition.makeCondition(condsList,EntityOperator.AND);
		
	   	try{
	   		productCategoryMember = delegator.findList("ProductAndCategoryMember", EntityCondition.makeCondition(condsList,EntityOperator.AND), null, UtilMisc.toList("productId", "productCategoryId", "productName"), null, false);
	   	} catch (Exception e) {
	   		Debug.logError(e, module);
	   		return ServiceUtil.returnError(e.toString());
	   	}	
	   	result.put("productCategoryMembers",productCategoryMember);  
	   	return result;
   	}
	public static Map<String, Object> getProductFeatures(DispatchContext dctx, Map<String, ? extends Object> context){
   	    Delegator delegator = dctx.getDelegator();
   	    LocalDispatcher dispatcher = dctx.getDispatcher();
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    String productCategoryId = (String) context.get("productCategoryId");
   	    Map result = ServiceUtil.returnSuccess();
   	    Timestamp now = UtilDateTime.nowTimestamp();
   	    
   	    List<GenericValue> productFeatureCategoryList = null;
   	    List productFeatureCategoryIdsList = FastList.newInstance();
   	    Map featuresByCategoryMap = FastMap.newInstance();
   	    
   	    try {
   	    	List<GenericValue> productFeatureCategoryAppls = delegator.findByAndCache("ProductFeatureCategoryAppl", UtilMisc.toMap("productCategoryId", productCategoryId));
   	    	productFeatureCategoryAppls = EntityUtil.filterByDate(productFeatureCategoryAppls, true);
   	    	productFeatureCategoryIdsList = EntityUtil.getFieldListFromEntityList(productFeatureCategoryAppls, "productFeatureCategoryId", true);
   	    	
   	    	try {
   	    		productFeatureCategoryList = delegator.findList("ProductFeatureCategory", EntityCondition.makeCondition("productFeatureCategoryId", EntityOperator.IN, productFeatureCategoryIdsList), null, null, null, false);
   			} catch (GenericEntityException e) {
   				Debug.logError(e, "Failed to retrive ProductPriceType ", module);
   				return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
   			}
   	    	
   	    	if (productFeatureCategoryAppls != null) {
   	    		for (GenericValue productFeatureCategoryAppl: productFeatureCategoryAppls) {
   	    			List<GenericValue> productFeatures = delegator.findByAndCache("ProductFeature", UtilMisc.toMap("productFeatureCategoryId", productFeatureCategoryAppl.get("productFeatureCategoryId")));
   	    			featuresByCategoryMap.put(((String)productFeatureCategoryAppl.get("productFeatureCategoryId")), productFeatures);
   	    		}
   	    	}
   	    } catch (GenericEntityException e) {
   	    	Debug.logError(e, "Error getting feature categories associated with the category with ID: " + productCategoryId, module);
   	    }	
	   	result.put("featuresByCategoryMap",featuresByCategoryMap); 
	   	result.put("productFeatureCategoryIdsList",productFeatureCategoryIdsList);
	   	result.put("productFeatureCategoryList",productFeatureCategoryList); 
	   	return result;
   	}

	
	public static String createNewProductAndFeatures(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map resultMap = FastMap.newInstance();
		String productCategoryId = (String) request.getParameter("productCategoryId");
		String virtualProductId = (String) request.getParameter("childProductCategoryId");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		/*Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}*/
		
		List productFeatureCategoryList = FastList.newInstance();
		
		try{
			result = dispatcher.runSync("getProductFeatures", UtilMisc.toMap("productCategoryId", productCategoryId));
			if (ServiceUtil.isError(result)) {
				request.setAttribute("_ERROR_MESSAGE_", "Error Occurred in Service");
				return "error";	    	            
			} 
			productFeatureCategoryList = EntityUtil.getFieldListFromEntityList( (List<GenericValue>) result.get("productFeatureCategoryList"), "productFeatureCategoryId", true);
		}
		catch (GenericServiceException e) {
			 Debug.logError(e, "Error Occured in fetching product features: ", module);
			 request.setAttribute("_ERROR_MESSAGE_", "Error Occured in fetching product features: ");
			 return "error";
		}
		String productFeatureIds = "";
		for(int i=0; i<productFeatureCategoryList.size(); i++){
			String productFeatureCategoryId = (String) productFeatureCategoryList.get(i);
			String productFeatureId = (String) request.getParameter(productFeatureCategoryId);
			if(UtilValidate.isNotEmpty(productFeatureId)){
				productFeatureIds += "|" + productFeatureId;
			}
			
		}
		try{
			result = dispatcher.runSync("quickAddVariant", UtilMisc.toMap("productId", virtualProductId, "productFeatureIds", productFeatureIds));
			if (ServiceUtil.isError(result)) {
				request.setAttribute("_ERROR_MESSAGE_", "Error Occurred in Service");
				return "error";	    	            
			} 
		}
		catch (GenericServiceException e) {
			 Debug.logError(e, "Error Occured in fetching product features: ", module);
			 request.setAttribute("_ERROR_MESSAGE_", "Error Occured in fetching product features: ");
			 return "error";
		}
		
		request.setAttribute("_EVENT_MESSAGE_", "Product creation successfull: ");
		return "success";
	}
	
	public static String processDepotSaleInvoice(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String vehicleId = (String) request.getParameter("vehicleId");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purposeTypeId = "MATERIAL_PUR_CHANNEL";
		GenericValue shipment=null;
		Timestamp invoiceDate = null;
		Timestamp suppInvDate = null;
		
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if (UtilValidate.isNotEmpty(invoiceDateStr)) { //2011-12-25 18:09:45
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
			try {
				invoiceDate = new java.sql.Timestamp(sdf.parse(invoiceDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + invoiceDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + invoiceDateStr, module);
			}
		}
		else{
			invoiceDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
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
		try{
			shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId",shipmentId), false);
		}catch(GenericEntityException e){
			Debug.logError(e, "Error fetching shipment " + shipmentId, module);
			request.setAttribute("_ERROR_MESSAGE_","Invalid shipment Id");
			return "error";
		}
	
		String statusId = (String)shipment.getString("statusId");

		if(UtilValidate.isNotEmpty(shipment) && statusId.equals("SHIPMENT_CANCELLED")){
			Debug.logError("Unable to generate Shipment: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate shipment  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";	
		}
		partyId = (String)shipment.getString("partyIdTo");
		String primaryOrderId = (String)shipment.getString("primaryOrderId");
		BigDecimal grandTotal = BigDecimal.ZERO;
		List<GenericValue> orderParty=null;
		   if (UtilValidate.isNotEmpty(shipmentId)) {
	           Map<String, Object> createInvoiceContext = FastMap.newInstance();
	           createInvoiceContext.put("partyId", partyId);
	           try {
					orderParty = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", primaryOrderId, "roleTypeId", "BILL_TO_CUSTOMER"));
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
				}
				
				if (UtilValidate.isNotEmpty(orderParty)) {
					GenericValue custOrderRole = EntityUtil.getFirst(orderParty);
					partyIdFrom = custOrderRole.getString("partyId");
				}
	           createInvoiceContext.put("partyIdFrom", partyIdFrom);
	           createInvoiceContext.put("shipmentId", shipmentId);
	           createInvoiceContext.put("invoiceTypeId", "SALES_INVOICE");
	           createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
	           createInvoiceContext.put("userLogin", userLogin);

	           // store the invoice first
	           Map<String, Object> createInvoiceResult = FastMap.newInstance();
	         
	           try{
		            createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
			        if(ServiceUtil.isError(createInvoiceResult)){
						Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
						request.setAttribute("_ERROR_MESSAGE_", "Unable to generate invoice  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
						return "error";
					}
	           } catch (Exception e) {
					Debug.logError(e, "Problems while calling createInvoice : " + partyId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems while calling createInvoice : " + partyId);
					return "error";
			   }
		           
		  // call service, not direct entity op: delegator.create(invoice);
		 String invoiceId = (String) createInvoiceResult.get("invoiceId");
	     List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			EntityCondition ficondExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> shipmentItemList =null;
			try{
				shipmentItemList = delegator.findList("ShipmentItem", ficondExpr, null, null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Problems while getting shipmentItems : " + shipmentId, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems while getting shipmentItems : " + shipmentId);
				return "error";
			}
			for (int i = 0; i < shipmentItemList.size(); i++) {
				GenericValue eachShipmentList = (GenericValue)shipmentItemList.get(i);
				GenericValue orderItem=null;
				try{
				 orderItem = delegator.findOne("OrderItem",UtilMisc.toMap("orderId",primaryOrderId,"orderItemSeqId",eachShipmentList.get("shipmentItemSeqId")), false);
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
				}
		 // grandTotal = grandTotal.add(eachShipmentList.getBigDecimal("amount").multiply(eachShipmentList.getBigDecimal("quantity")));
		   Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	       createInvoiceItemContext.put("invoiceId",invoiceId);
	       createInvoiceItemContext.put("invoiceItemTypeId", "INV_RAWPROD_ITEM");
	       createInvoiceItemContext.put("description", eachShipmentList.get("description"));
	       createInvoiceItemContext.put("quantity",eachShipmentList.get("quantity"));
	       createInvoiceItemContext.put("amount",orderItem.getBigDecimal("unitPrice"));
	       createInvoiceItemContext.put("productId", eachShipmentList.get("productId"));
	       createInvoiceItemContext.put("unitPrice", orderItem.getBigDecimal("unitPrice"));
	       createInvoiceItemContext.put("unitListPrice", orderItem.getBigDecimal("unitListPrice"));
	       //createInvoiceItemContext.put("uomId", "");
	       createInvoiceItemContext.put("userLogin", userLogin);
	       try{
	           Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	           if(ServiceUtil.isError(createInvoiceItemResult)){
	      			Debug.logError("Unable to create invoiceItems for invoice: " + ServiceUtil.getErrorMessage(result), module);
	      			request.setAttribute("_ERROR_MESSAGE_", "Unable to Change invoice Status For Shipment :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(result));
	      			return "error";
	      		}
	          } catch (Exception e) {
					Debug.logError(e, "Problems while calling createInvoiceItem : " + invoiceId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems while calling createInvoiceItem : " + invoiceId);
					return "error";
				}
				}
	       
			String nextStatusId = "INVOICE_READY";
	           try {
	                   Map<String, Object> setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", nextStatusId, "userLogin", userLogin));
	                   if(ServiceUtil.isError(setInvoiceStatusResult)){
	           			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
	           			request.setAttribute("_ERROR_MESSAGE_", "Unable to Change invoice Status For Shipment :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(result));
	           			return "error";
	           		}
	           } catch (Exception e) {
					Debug.logError(e, "Problems while changing Status: " + invoiceId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + invoiceId);
					return "error";
				}
	            nextStatusId = "INVOICE_APPROVED";
	           try {
	                   Map<String, Object> setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", nextStatusId, "userLogin", userLogin));
	                   if(ServiceUtil.isError(createInvoiceResult)){
	           			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
	           			request.setAttribute("_ERROR_MESSAGE_", "Unable to Change invoice Status For Shipment :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(result));
	           			return "error";
	           		}
	           } catch (Exception e) {
					Debug.logError(e, "Problems while changing Status: " + invoiceId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + invoiceId);
					return "error";
				}
		   try{
			   conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId));
			   EntityCondition condExpress = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", condExpress, null, null, null, false);
			   String actualOrderId = (EntityUtil.getFirst(orderAssocList)).getString("toOrderId");
			   conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
			   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_VOID"));
			   EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List<GenericValue> orderPreferenceList = delegator.findList("OrderPaymentPreference", condExpr, null, null, null, false);
			   List orderPreferenceIdList = EntityUtil.getFieldListFromEntityList(orderPreferenceList, "orderPaymentPreferenceId", true);
			   conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("orderPaymentPreferenceId", EntityOperator.IN, orderPreferenceIdList));
			   EntityCondition condExpretion = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List<GenericValue> paymentList = delegator.findList("OrderPreferencePaymentApplication", condExpretion, null, null, null, false);
			  //enericValue orderHeaderList = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", primaryOrderId), false);
			  BigDecimal paidAmount = BigDecimal.ZERO;
			  if (UtilValidate.isNotEmpty(paymentList)) {
				for (GenericValue eachPayment : paymentList) {
					 BigDecimal eachAmount = (BigDecimal)eachPayment.get("amountApplied");
					 paidAmount = paidAmount.add(eachAmount);
					 Map newPayappl = UtilMisc.toMap("userLogin",userLogin);
	            	 newPayappl.put("invoiceId", invoiceId);
	            	 newPayappl.put("paymentId", eachPayment.get("paymentId"));
	            	 newPayappl.put("amountApplied", eachAmount);
		             Map<String, Object> paymentApplResult = dispatcher.runSync("createPaymentApplication",newPayappl);
		             if(ServiceUtil.isError(paymentApplResult)){
	           			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(paymentApplResult), module);
	           			//request.setAttribute("_ERROR_MESSAGE_", "Unable to Create Payment Application For :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(paymentApplResult));
	           			return "error";
	           		}
				}
			}
			BigDecimal balance = grandTotal.subtract(paidAmount);
			//for Sent SMS 
			Map<String, Object> getTelParams = FastMap.newInstance();
	    	getTelParams.put("partyId", partyId);
	        getTelParams.put("userLogin", userLogin);                    	
	        Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	        if (ServiceUtil.isError(serviceResult)) {
	        	Debug.logError("Unable to get telephone number for party : " + ServiceUtil.getErrorMessage(result), module);
				//request.setAttribute("_ERROR_MESSAGE_", "Problems while getting Telephone for: " + partyId);
				//return "error";
	        } 
	        String contactNumberTo = (String) serviceResult.get("contactNumber");
	        String countryCode = (String) serviceResult.get("countryCode");
	        Debug.log("contactNumberTo = "+contactNumberTo);
	        Debug.log("contactNumberTo = "+contactNumberTo);
	        if(UtilValidate.isEmpty(contactNumberTo)){
	        	contactNumberTo = "9502532897";
	        }
	        if(UtilValidate.isNotEmpty(contactNumberTo)){
	        	 if(UtilValidate.isNotEmpty(countryCode)){
	        		 contactNumberTo = countryCode + contactNumberTo;
	        	 }
	        	 String grandTotalStr=String.valueOf(grandTotal.intValue());
	        	 String paidAmountStr=String.valueOf(paidAmount.intValue());
	        	 String balanceStr=String.valueOf(balance.intValue());
	        	 String invoiceMsgToWeaver = UtilProperties.getMessage("ProductUiLabels", "InvoiceMsgToWeaver", locale);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("orderId", actualOrderId);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("InvoiceValue", grandTotalStr);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("advancePaid", paidAmountStr);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("balancePayable", balanceStr);
	        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("invoiceNo", invoiceId);
	        	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	             sendSmsParams.put("contactNumberTo", contactNumberTo);
	             sendSmsParams.put("text", invoiceMsgToWeaver); 
	             serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	             if (ServiceUtil.isError(serviceResult)) {
	            	 Debug.logError("Unable to Send SMS: " + ServiceUtil.getErrorMessage(result), module);
	        			//request.setAttribute("_ERROR_MESSAGE_", "Unable to Change invoice Status For Shipment :" + invoiceId+"....! "+ServiceUtil.getErrorMessage(result));
	        			//return "error";
	             }
	        }
		    
		   }catch (Exception e) {
				Debug.logError(e, "Problems while Calculating balance Amount for order: " + partyId, module);
				//request.setAttribute("_ERROR_MESSAGE_", "Problems while Calculating balance Amount for order: " + partyId);
				return "error";
			}
		   
		   }
		   request.setAttribute("_EVENT_MESSAGE_", "Sales Invoice created sucessfully : "+partyId);   
	/*
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		
		List productQtyList = FastList.newInstance();
		List invoiceAdjChargesList = FastList.newInstance();
		String applicableTo = "ALL";
		for (int i = 0; i < rowCount; i++) {
			
			Map prodQtyMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String vatStr=null;
			String cstStr=null;
			
			BigDecimal vat = BigDecimal.ZERO;
			BigDecimal cst = BigDecimal.ZERO;
			//percenatge of TAXes
			
			String VatPercentStr=null;
			String CSTPercentStr=null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO; 
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productId = "";
			String quantityStr = "";
			String unitPriceStr = "";
			
			BigDecimal quantity = BigDecimal.ZERO;
			BigDecimal uPrice = BigDecimal.ZERO;
			
			if (paramMap.containsKey("invoiceItemTypeId" + thisSuffix)) {
				invoiceItemTypeId = (String) paramMap.get("invoiceItemTypeId" + thisSuffix);
			}
			
			if (paramMap.containsKey("applicableTo" + thisSuffix)) {
				applicableTo = (String) paramMap.get("applicableTo" + thisSuffix);
			}
			
			if (paramMap.containsKey("adjAmt" + thisSuffix)) {
				adjAmtStr = (String) paramMap.get("adjAmt" + thisSuffix);
			}
			
			if(UtilValidate.isNotEmpty(adjAmtStr)){
				try {
					adjAmt = new BigDecimal(adjAmtStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			
			
			if(UtilValidate.isNotEmpty(invoiceItemTypeId) && adjAmt.compareTo(BigDecimal.ZERO)>0){
				Map invItemMap = FastMap.newInstance();
				invItemMap.put("otherTermId", invoiceItemTypeId);
				invItemMap.put("termValue", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
				invoiceAdjChargesList.add(invItemMap);	
			}
			
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.get("productId" + thisSuffix);
			}
			
			if(UtilValidate.isNotEmpty(productId)){
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
				}
				if(UtilValidate.isEmpty(quantityStr)){
					request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
					return "error";	
				}
				
				if (paramMap.containsKey("UPrice" + thisSuffix)) {
				   unitPriceStr = (String) paramMap.get("UPrice" + thisSuffix);
				}
				if (paramMap.containsKey("VAT" + thisSuffix)) {
					vatStr = (String) paramMap.get("VAT" + thisSuffix);
				}
				
				if (paramMap.containsKey("CST" + thisSuffix)) {
					cstStr = (String) paramMap.get("CST" + thisSuffix);
				}
				
				if (paramMap.containsKey("VatPercent" + thisSuffix)) {
					VatPercentStr = (String) paramMap.get("VatPercent" + thisSuffix);
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
					if (!CSTPercentStr.equals("")) {
						cstPercent = new BigDecimal(CSTPercentStr);
					}
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing CSTPercent string: " + CSTPercentStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + CSTPercentStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(productId)){
				prodQtyMap.put("productId", productId);
				prodQtyMap.put("quantity", quantity);
				prodQtyMap.put("unitPrice", uPrice);
				prodQtyMap.put("vatAmount", vat);
				prodQtyMap.put("cstAmount", cst);
				prodQtyMap.put("vatPercent", vatPercent);
				prodQtyMap.put("cstPercent", cstPercent);
				prodQtyMap.put("bedPercent", BigDecimal.ZERO);
				productQtyList.add(prodQtyMap);
			}
		}//end row count for loop
		if( UtilValidate.isEmpty(productQtyList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		Map processInvoiceContext = FastMap.newInstance();
		processInvoiceContext.put("userLogin", userLogin);
		processInvoiceContext.put("productQtyList", productQtyList);
		processInvoiceContext.put("partyId", partyId);
		processInvoiceContext.put("purposeTypeId", purposeTypeId);
		processInvoiceContext.put("vehicleId", vehicleId);
		processInvoiceContext.put("orderId", orderId);
		processInvoiceContext.put("shipmentId", shipmentId);
		processInvoiceContext.put("invoiceDate", invoiceDate);
		processInvoiceContext.put("invoiceAdjChargesList", invoiceAdjChargesList);
		if(UtilValidate.isNotEmpty(isDisableAcctg)){
			processInvoiceContext.put("isDisableAcctg", isDisableAcctg);
		}
		result = createDepotSaleInvoice(dctx, processInvoiceContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate invoice  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";
		}
		
		String invoiceId =  (String)result.get("invoiceId");
		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId);	  	 
	*/	
		return "success";
}

	
	public static Map<String, Object> createDepotSaleInvoice(DispatchContext ctx,Map<String, ? extends Object> context) {
	
	    Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    List<Map> productQtyList = (List) context.get("productQtyList");
	    List<Map> invoiceAdjChargesList = (List) context.get("invoiceAdjChargesList");
	    Timestamp invoiceDate = (Timestamp) context.get("invoiceDate");
	    Locale locale = (Locale) context.get("locale");
	  	String purposeTypeId = (String) context.get("purposeTypeId");
	  	String vehicleId = (String) context.get("vehicleId");
	  	String partyIdFrom = (String) context.get("partyId");
	  	String orderId = (String) context.get("orderId");
	  	String isDisableAcctg = (String) context.get("isDisableAcctg");
	  	String shipmentId = (String) context.get("shipmentId");
	  	boolean beganTransaction = false;
	  	String currencyUomId = "INR";
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		String partyId="Company";
        List<GenericValue> orderParty = null;  

		try {
			orderParty = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER"));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		
		if (UtilValidate.isNotEmpty(orderParty)) {
			GenericValue custOrderRole = EntityUtil.getFirst(orderParty);
			partyId = custOrderRole.getString("partyId");
		}
		if (UtilValidate.isEmpty(partyIdFrom)) {
			Debug.logError("Cannot create invoice without partyId: "+ partyIdFrom, module);
			return ServiceUtil.returnError("partyId is empty");
		}

		try{
			beganTransaction = TransactionUtil.begin(7200);
			
			if(UtilValidate.isEmpty(shipmentId)){
				Debug.logError("ShipmentId required to create invoice ", module);
				return ServiceUtil.returnError("ShipmentId required to create invoice ");
			}
			
			GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
			
			if(UtilValidate.isNotEmpty(shipment) && shipment.equals("SHIPMENT_CANCELLED")){
				Debug.logError("Cannot create invoice for cancelled shipment", module);
				return ServiceUtil.returnError("Cannot create invoice for cancelled shipment");
			}
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
			EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> shipmentReceipts = delegator.findList("ShipmentReceipt", condExpr, null, null, null, false);
			if(UtilValidate.isEmpty(shipmentReceipts)){
				Debug.logError("GRN not found for the shipment: "+shipmentId, module);
				return ServiceUtil.returnError("GRN not found for the shipment: "+shipmentId);
			}
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF")));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> invoices = delegator.findList("Invoice", condition, null, null, null, false);
			
			if(UtilValidate.isNotEmpty(invoices)){
				Debug.logError("Invoices already generated for shipment : "+shipmentId, module);
				return ServiceUtil.returnError("Invoices already generated for shipment : "+shipmentId);
			}
			Map resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", productQtyList, "otherCharges", invoiceAdjChargesList, "userLogin", userLogin, "incTax", ""));
			if(ServiceUtil.isError(resultCtx)){
  		  		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
  		  		Debug.logError(errMsg , module);
  		  		return ServiceUtil.returnError(errMsg);
			}
			List<Map> itemDetails = (List)resultCtx.get("itemDetail");
			List<Map> adjustmentDetail = (List)resultCtx.get("adjustmentDetail");
			Map input = FastMap.newInstance();
			input.put("userLogin", userLogin);
	        input.put("invoiceTypeId", "PURCHASE_INVOICE");        
	        input.put("partyIdFrom", partyIdFrom);	
	        input.put("statusId", "INVOICE_IN_PROCESS");	
	        input.put("currencyUomId", currencyUomId);
	        input.put("invoiceDate", invoiceDate);
	        input.put("dueDate", invoiceDate); 	        
	        input.put("partyId", partyId);
	        input.put("purposeTypeId", purposeTypeId);
	        if(UtilValidate.isNotEmpty(isDisableAcctg)){
		        input.put("isEnableAcctg", "N");
			}
	        input.put("createdByUserLogin", userLogin.getString("userLoginId"));
	        input.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
	        result = dispatcher.runSync("createInvoice", input);
			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("Error while creating invoice for party : "+partyId, null, null, result);
			}
			
			String invoiceId = (String)result.get("invoiceId");
			for (Map<String, Object> prodQtyMap : itemDetails) {
				
				String productId = "";
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal amount = BigDecimal.ZERO;
				Map invoiceItemCtx = FastMap.newInstance();
				BigDecimal unitListPrice = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
					productId = (String)prodQtyMap.get("productId");
					invoiceItemCtx.put("productId", productId);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
					quantity = (BigDecimal)prodQtyMap.get("quantity");
					invoiceItemCtx.put("quantity", quantity);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
					amount = (BigDecimal)prodQtyMap.get("unitPrice");
					BigDecimal unitPrice = amount;
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedUnitRate"))){
						unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedUnitRate"));
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessUnitRate"))){
						unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedcessUnitRate"));
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessUnitRate"))){
						unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedseccessUnitRate"));
					}
					invoiceItemCtx.put("amount", unitPrice);
					invoiceItemCtx.put("unitPrice", unitPrice);
					
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
					unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
					invoiceItemCtx.put("unitListPrice", unitListPrice);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
					BigDecimal vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
					if(vatPercent.compareTo(BigDecimal.ZERO)>0){
						invoiceItemCtx.put("vatPercent", vatPercent);
					}
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
					BigDecimal vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
					if(vatAmount.compareTo(BigDecimal.ZERO)>0){
						invoiceItemCtx.put("vatAmount", vatAmount);
					}
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
					BigDecimal cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
					if(cstPercent.compareTo(BigDecimal.ZERO)>0){
						invoiceItemCtx.put("cstPercent", cstPercent);
					}
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
					BigDecimal cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
					if(cstAmount.compareTo(BigDecimal.ZERO)>0){
						invoiceItemCtx.put("cstAmount", cstAmount);
					}
				}
				invoiceItemCtx.put("invoiceId", invoiceId);
				invoiceItemCtx.put("invoiceItemTypeId", "INV_RAWPROD_ITEM");
				invoiceItemCtx.put("userLogin", userLogin);
				result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
				
				if (ServiceUtil.isError(result)) {
					Debug.logError("Error creating Invoice item for product : "+productId, module);	
					return ServiceUtil.returnError("Error creating Invoice item for product : "+productId);
				}
				String invItemSeqId = (String) result.get("invoiceItemSeqId");
				
				List<GenericValue> receipts = EntityUtil.filterByCondition(shipmentReceipts, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				if(UtilValidate.isNotEmpty(receipts)){
					String inventoryItemId = (EntityUtil.getFirst(receipts)).getString("inventoryItemId");
					
					GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
					
					if(UtilValidate.isNotEmpty(inventoryItem)){
						inventoryItem.set("unitCost", unitListPrice);
						inventoryItem.store();
					}
				}
			}
			
			for (Map<String, Object> adjustMap : adjustmentDetail) {
				
				String adjustmentTypeId = "";
				BigDecimal amount = BigDecimal.ZERO;
				Map invoiceItemCtx = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(adjustMap.get("adjustmentTypeId"))){
					adjustmentTypeId = (String)adjustMap.get("adjustmentTypeId");
					invoiceItemCtx.put("invoiceItemTypeId", adjustmentTypeId);
				}
				if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					invoiceItemCtx.put("amount", amount);
				}
				if(UtilValidate.isNotEmpty(adjustmentTypeId) && !(amount.compareTo(BigDecimal.ZERO) == 0)){
					invoiceItemCtx.put("invoiceId", invoiceId);
					invoiceItemCtx.put("quantity", BigDecimal.ONE);
					invoiceItemCtx.put("userLogin", userLogin);
					result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
					if (ServiceUtil.isError(result)) {
						Debug.logError("Error creating Invoice item for Item : "+adjustmentTypeId, module);	
						return ServiceUtil.returnError("Error creating Invoice item for Item : "+adjustmentTypeId);
					}
					String invItemSeqId = (String) result.get("invoiceItemSeqId");
				}
				
			}
			
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			invoice.set("shipmentId", shipmentId);
			invoice.store();
			
			result.put("invoiceId", invoiceId);
			 // creating invoiceRole for order
			 Map<String, Object> createInvoiceRoleContext = FastMap.newInstance();
		        createInvoiceRoleContext.put("invoiceId", result.get("invoiceId"));
		        createInvoiceRoleContext.put("userLogin", userLogin);
		   
		    	   List condLIst = FastList.newInstance();
		    	   condLIst.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					EntityCondition condExpr1 = EntityCondition.makeCondition(condLIst, EntityOperator.AND);
					List<GenericValue> orderRoles = delegator.findList("OrderRole", condExpr1, null, null, null, false);
		   
		      for (GenericValue orderRole : orderRoles) {
			            createInvoiceRoleContext.put("partyId", orderRole.getString("partyId"));
			            createInvoiceRoleContext.put("roleTypeId", orderRole.getString("roleTypeId"));
			            Map<String, Object> createInvoiceRoleResult = dispatcher.runSync("createInvoiceRole", createInvoiceRoleContext);
			            if (ServiceUtil.isError(createInvoiceRoleResult)) {
			            	Debug.logError("Error creating InvoiceRole  for orderId : "+orderId, module);	
							return ServiceUtil.returnError("Error creating Invoice Role for orderId : "+orderId);
			            }
			        }
		      //approve invoice
		      Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
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
		return result;

	}
	
	public static Map<String, Object> getSchemeTimePeriodId(DispatchContext dctx, Map context) {
		 GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		 LocalDispatcher dispatcher = dctx.getDispatcher();
		 String periodTypeId = (String) context.get("periodTypeId");
		 String orderBy = (String) context.get("orderBy");
	     Timestamp fromDate = (Timestamp) context.get("fromDate");
	     Timestamp thruDate = (Timestamp) context.get("thruDate");
	     List<GenericValue> schemeTimePeriodList =FastList.newInstance();
	     List<GenericValue> yearSchemeTimePeriodList =FastList.newInstance();
	     String parentPeriodId=null;
		 List conditionList = FastList.newInstance();
		 Map<String, Object> result = ServiceUtil.returnSuccess();
		 String schemeTimePeriodId = "";
		try{
			 conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"TEN_PERC_SCH_YEAR"));
			 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(fromDate.getTime())),EntityOperator.OR,EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,new java.sql.Date(fromDate.getTime()))));
			 conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(thruDate.getTime())));				  
			 EntityCondition schemeyearCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			 yearSchemeTimePeriodList = delegator.findList("SchemeTimePeriod", schemeyearCondition, null, null, null,false); 
			 
			if(UtilValidate.isNotEmpty(yearSchemeTimePeriodList) && yearSchemeTimePeriodList.size()>0){
				 GenericValue yearSchemeTimePeriod=yearSchemeTimePeriodList.get(0);

				 parentPeriodId=(String)yearSchemeTimePeriod.get("schemeTimePeriodId");
				
			 }else{
				 return ServiceUtil.returnError("There no active Year scheme Time Periods"); 
			 }
			  conditionList.clear();
			  conditionList.add(EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS,parentPeriodId));
			  conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,periodTypeId));
			  conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(fromDate.getTime())),EntityOperator.OR,EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,new java.sql.Date(fromDate.getTime()))));
			  conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(thruDate.getTime())));				  
			  EntityCondition schemeCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			  schemeTimePeriodList = delegator.findList("SchemeTimePeriod", schemeCondition, null, UtilMisc.toList(orderBy+"periodNum", orderBy+"fromDate"), null,false); 
			  if(UtilValidate.isNotEmpty(schemeTimePeriodList)){
				 List<GenericValue> resultSchemeTimePeriodList =FastList.newInstance();
				 for (GenericValue schemeTimePeriodObj : schemeTimePeriodList) {
					 resultSchemeTimePeriodList.add((GenericValue)schemeTimePeriodObj);
				}
			      result.put("schemeTimePeriodIdList",resultSchemeTimePeriodList);
			 }
			 if(UtilValidate.isEmpty(schemeTimePeriodList)){
				  Debug.logError( "There no active cust scheme Periods.", module);	 				 
				  return ServiceUtil.returnError("There no active scheme Time Periods");
			 }
			
		}catch(Exception e){
			Debug.logError(e, module);
			Debug.logError(e, "Error in getting scheme time period", module);	 		  		  
	  		return ServiceUtil.returnError("Error in getting scheme time period");
		}
		return result;
	}	
	
	public static Map<String, Object> cancelIndentOrder(DispatchContext dctx, Map context) {	
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");
		String partyId = (String) context.get("partyId");
		String salesChannelEnumId = (String) context.get("salesChannelEnumId");	
		String schemeCategory="MGPS_10Pecent";
		try{
			if(UtilValidate.isNotEmpty(orderId)){
				result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", UtilMisc.toList(orderId),"userLogin", userLogin));
				if (ServiceUtil.isError(result)) {
					Debug.logError("Problem cancelling orders in Correction", module);	 		  		  
			 		return ServiceUtil.returnError("Problem cancelling orders in Correction");
				} 
				
				List condsList = FastList.newInstance();
				condsList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
				condsList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
				try{
				List<GenericValue> orderItemAndAdjustmentList =  delegator.findList("OrderItemAndAdjustment",EntityCondition.makeCondition(condsList,EntityOperator.AND),null, null, null, true);   
				
				if(UtilValidate.isNotEmpty(orderItemAndAdjustmentList)&& orderItemAndAdjustmentList.size()>0){
					List schemeCategoryIds = FastList.newInstance();
				  	try{
				  		List productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), UtilMisc.toSet("productCategoryId"), null, null, false);
				  		schemeCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);
				   	}catch (GenericEntityException e) {
						Debug.logError(e, "Failed to retrive ProductCategory ", module);
						return ServiceUtil.returnError("Failed to retrive ProductCategory " + e);
					}	 	
					for(GenericValue orderItemAndAdjustment : orderItemAndAdjustmentList){
						
						condsList.clear();
						condsList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					  	condsList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemAndAdjustment.get("orderItemSeqId")));
					  	condsList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "quotaQty"));
					  	BigDecimal quota =BigDecimal.ZERO;
					  	try {
							List<GenericValue> OrderItemAttributeList = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition(condsList,EntityOperator.AND), UtilMisc.toSet("attrValue"), null, null, true);
							if(UtilValidate.isEmpty(OrderItemAttributeList) || OrderItemAttributeList.size()==0){
								continue;
							}
							GenericValue OrderItemAttribute=OrderItemAttributeList.get(0);
							quota = new BigDecimal((String)OrderItemAttribute.get("attrValue"));
						} catch (GenericEntityException e) {
							Debug.logError(e, "Failed to retrive ProductPriceType ", module);
							return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
						}
					  	Map partyBalanceHistoryContext = FastMap.newInstance();
						partyBalanceHistoryContext = UtilMisc.toMap("partyId",partyId,"orderItemAndAdjustment",orderItemAndAdjustment,"schemeCategoryIds",schemeCategoryIds,"schemeCategory",schemeCategory,"quota",quota, "userLogin", userLogin);
					  	dispatcher.runSync("cancelPartyQuotaBalanceHistory", partyBalanceHistoryContext);
					}
				}
				}catch (GenericEntityException e) {
					Debug.logError(e, "Failed to retrive ProductCategory ", module);
					return ServiceUtil.returnError("Failed to retrive ProductCategory " + e);
				}
			}
			  			
		}catch (GenericServiceException e) {
			  Debug.logError(e, e.toString(), module);
			  return ServiceUtil.returnError("Problem cancelling order");
		}
		result.put("salesChannelEnumId", salesChannelEnumId);
		return result;
	}
	
	
	public static Map<String, Object> cancelPartyQuotaBalanceHistory(DispatchContext dctx, Map context){	
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue orderItemAndAdjustment=(GenericValue)context.get("orderItemAndAdjustment");
		List schemeCategoryIds=(List) context.get("schemeCategoryIds");
		String schemeCategory=(String) context.get("schemeCategory");
		Timestamp fromDate = (Timestamp) orderItemAndAdjustment.get("orderDate");
		Timestamp thruDate = (Timestamp) orderItemAndAdjustment.get("orderDate");
		String productId=(String) orderItemAndAdjustment.get("productId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId=(String) context.get("partyId");
		BigDecimal quota=(BigDecimal) context.get("quota");
		List condsList = FastList.newInstance();
		if(schemeCategory.equals("MGPS_10Pecent")){
			String periodTypeId="TEN_PERC_PERIOD";
			
			List productCategoriesList = FastList.newInstance();
			condsList.clear();
		  	condsList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		  	condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, schemeCategoryIds));
		  	condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
		  	condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate)));
			try {
				List<GenericValue> prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(condsList,EntityOperator.AND), UtilMisc.toSet("productCategoryId"), null, null, true);
				productCategoriesList = EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productCategoryId", true);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive ProductPriceType ", module);
				return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
			}
			List<GenericValue>  schemeTimePeriodIdList =  FastList.newInstance();
			Map<String, Object> resultMap = ServiceUtil.returnSuccess();
			try {
			resultMap=dispatcher.runSync("getSchemeTimePeriodId", UtilMisc.toMap("periodTypeId",periodTypeId,"fromDate",fromDate,"thruDate",thruDate,"orderBy","-","userLogin", userLogin));  
			} catch (Exception e) {
				Debug.logError(e, "Failed to get getSchemeTimePeriodId ", module);
				return ServiceUtil.returnError("Failed to get getSchemeTimePeriodId " + e);
			}
			if (ServiceUtil.isError(resultMap)) {
	        	Debug.logError("Error getting Scheme Time Period", module);	
	            return ServiceUtil.returnError("Error getting Scheme Time Period");
				}
			schemeTimePeriodIdList=(List<GenericValue>)resultMap.get("schemeTimePeriodIdList");
			String schemeId="TEN_PERCENT_MGPS";
			String productCategoryId=(String)productCategoriesList.get(0);
			
			Map partyBalanceHistoryContext = FastMap.newInstance();
			partyBalanceHistoryContext = UtilMisc.toMap("schemeId",schemeId,"partyId",partyId,"productCategoryId",productCategoryId,"schemeTimePeriodIdList", schemeTimePeriodIdList,"quota",quota,"userLogin", userLogin);
				
			try { 	
				Map<String, Object> resultMapquota = dispatcher.runSync("cancelQuota", partyBalanceHistoryContext);
				quota=(BigDecimal)resultMapquota.get("quota");
			} catch (Exception e) {
				Debug.logError(e, "Failed to retrive PartyQuotaBalanceHistory ", module);
				return ServiceUtil.returnError("Failed to retrive PartyQuotaBalanceHistory " + e);
			}
		}
		return result;
	}

	
	public static Map<String, Object> cancelQuota(DispatchContext dctx, Map context) {
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String productCategoryId =(String) context.get("productCategoryId");
		String schemeId=(String) context.get("schemeId");
		String partyId=(String) context.get("partyId");
		BigDecimal quota = (BigDecimal)context.get("quota");
		String schemeTimePeriodId=null;
		List<GenericValue> schemeTimePeriodIdList=(List<GenericValue>) context.get("schemeTimePeriodIdList");
		try{
		List condsList = FastList.newInstance();
		condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,productCategoryId));
		List<GenericValue> schemeProductCategoryList =  delegator.findList("SchemeProductCategory",EntityCondition.makeCondition(condsList,EntityOperator.AND),null, null, null, true);   
		GenericValue schemeProductCategory=schemeProductCategoryList.get(0);
		if(UtilValidate.isNotEmpty(schemeProductCategory)){
			BigDecimal periodTime = (BigDecimal)schemeProductCategory.get("periodTime");	
			if(UtilValidate.isEmpty(periodTime)){
				periodTime=BigDecimal.ONE;
			}
			if(UtilValidate.isNotEmpty(periodTime) && periodTime.compareTo(BigDecimal.ZERO)>0){
			//if periodTime is exist
				List schemeTimePeriodIds = EntityUtil.getFieldListFromEntityList(schemeTimePeriodIdList, "schemeTimePeriodId", true);
				condsList.clear();
				condsList.add(EntityCondition.makeCondition("schemeTimePeriodId", EntityOperator.IN,schemeTimePeriodIds));
				condsList.add(EntityCondition.makeCondition("usedQuota", EntityOperator.GREATER_THAN,BigDecimal.ZERO));
				condsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
				schemeTimePeriodIdList =  delegator.findList("PartyQuotaBalanceHistoryAndTimePeriod",EntityCondition.makeCondition(condsList,EntityOperator.AND),null, UtilMisc.toList("-periodNum"), null, true);   

				//test
				
			  for(int i=0;i< periodTime.intValueExact();i++){
					  if(i==schemeTimePeriodIdList.size()){
						  break;
					  }
				  schemeTimePeriodId=(String)((GenericValue)schemeTimePeriodIdList.get(i)).get("schemeTimePeriodId");
				  Map<String, Object> resultPartyQuotaBalanceHistoryMap = getPartyQuotaBalanceHistory(dctx,UtilMisc.toMap("productCategoryId",productCategoryId,"partyId",partyId,"schemeTimePeriodId",schemeTimePeriodId));	
					GenericValue partyQuotaBalanceHistory=(GenericValue)resultPartyQuotaBalanceHistoryMap.get("partyQuotaBalanceHistory");
					
					//if partyQuotaBalanceHistory is exist
					if(UtilValidate.isNotEmpty(partyQuotaBalanceHistory)){
						//getting balancequota
						BigDecimal quotaEligibility=(BigDecimal)partyQuotaBalanceHistory.get("quotaEligibility");
						BigDecimal usedQuota=(BigDecimal)partyQuotaBalanceHistory.get("usedQuota");
						//if quota>0
						if(quota.compareTo(BigDecimal.ZERO)>0){
							if(usedQuota.compareTo(BigDecimal.ZERO)==0){
								continue;
							}
							//if quota>quotaEligibility
							if(quota.compareTo(usedQuota)>=0){
								quota=quota.subtract(usedQuota);
								partyQuotaBalanceHistory.set("balancequota",(BigDecimal)partyQuotaBalanceHistory.get("quotaEligibility"));
								partyQuotaBalanceHistory.set("usedQuota",BigDecimal.ZERO);
								partyQuotaBalanceHistory.store();
							}//if quotaEligibility > quota
						    else{
						    	BigDecimal totalUsedQuota=((BigDecimal)partyQuotaBalanceHistory.get("usedQuota")).subtract(quota);
						    	BigDecimal totalBalanceQuota=((BigDecimal)partyQuotaBalanceHistory.get("balancequota")).add(quota);
						    	partyQuotaBalanceHistory.set("usedQuota",totalUsedQuota);
						    	partyQuotaBalanceHistory.set("balancequota",totalBalanceQuota);
								partyQuotaBalanceHistory.store();
								quota=BigDecimal.ZERO;
						     }
							
						}//if quota == 0
						else{ break; }
			  }
		
			}
		}

		
			
		}
	}catch (GenericEntityException e) {
		Debug.logError(e, "Failed to retrive ProductCategory ", module);
		return ServiceUtil.returnError("Failed to retrive ProductCategory " + e);
	}
		return result;

	}
	public static Map<String, Object> createPartyQuotaBalanceHistory(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productCategoryId =(String) context.get("productCategoryId");
		String schemeId=(String) context.get("schemeId");
		String partyId=(String) context.get("partyId");
		Timestamp dateTimeStamp=(Timestamp) context.get("dateTimeStamp");
		BigDecimal quota = BigDecimal.ZERO;
		BigDecimal quantity=(BigDecimal)context.get("quantity");
		BigDecimal remainingQty=quantity;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		String schemeTimePeriodId=null;
		BigDecimal partyLooms = BigDecimal.ZERO;
		try{
			
			String periodTypeId="TEN_PERC_PERIOD";
			List<GenericValue>  schemeTimePeriodIdList =  FastList.newInstance();
			Map<String, Object> resultMap = ServiceUtil.returnSuccess();
			try {
			resultMap=dispatcher.runSync("getSchemeTimePeriodId", UtilMisc.toMap("periodTypeId",periodTypeId,"fromDate",dateTimeStamp,"thruDate",dateTimeStamp,"orderBy","","userLogin", userLogin));  
			} catch (Exception e) {
				Debug.logError(e, "Failed to get getSchemeTimePeriodId ", module);
				return ServiceUtil.returnError("Failed to get getSchemeTimePeriodId " + e);
			}
			if (ServiceUtil.isError(resultMap)) {
	        	Debug.logError("Error getting Scheme Time Period", module);	
	            return ServiceUtil.returnError("Error getting Scheme Time Period");
				}
			schemeTimePeriodIdList=(List<GenericValue>)resultMap.get("schemeTimePeriodIdList");
			
			
			List condsList = FastList.newInstance();
			condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,productCategoryId));
			List<GenericValue> schemeProductCategoryList =  delegator.findList("SchemeProductCategory",EntityCondition.makeCondition(condsList,EntityOperator.AND),null, null, null, true);   
			GenericValue schemeProductCategory=schemeProductCategoryList.get(0);
			
			//get PartyLoom start  
			condsList.clear();
			condsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			condsList.add(EntityCondition.makeCondition("loomTypeId", EntityOperator.EQUALS, productCategoryId));
			condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dateTimeStamp));
			condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
							EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dateTimeStamp)));
					
					try {
						List<GenericValue> partyLoomsList = delegator.findList("PartyLoom", EntityCondition.makeCondition(condsList, EntityOperator.AND), null, null, null, false);
						if(UtilValidate.isNotEmpty(partyLoomsList)){
							partyLooms=(BigDecimal)partyLoomsList.get(0).get("quantity");
						}
					} catch (GenericEntityException e) {
						Debug.logError(e, "Failed to retrive SchemeParty ", module);
						return ServiceUtil.returnError("Failed to retrive SchemeParty " + e);
					}
			//get PartyLoom ends 
			
			if(UtilValidate.isNotEmpty(schemeProductCategory) && partyLooms.compareTo(BigDecimal.ZERO)>0){
				BigDecimal periodTime = (BigDecimal)schemeProductCategory.get("periodTime");	
				if(UtilValidate.isNotEmpty(periodTime) && periodTime.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(schemeTimePeriodIdList) &&  schemeTimePeriodIdList.size()>0 ){
				//if periodTime is exist
					//periodTime does not match with scheme time period count
//					if(periodTime.intValueExact()>schemeTimePeriodIdList.size()){
//						return ServiceUtil.returnError("schemeTimePeriod count does not match with SchemeProductCategory periodTime. ");
//					}
					
				  for(int i=0;i< periodTime.intValueExact();i++){
					  if(i==schemeTimePeriodIdList.size()){
						  break;
					  }
					schemeTimePeriodId=(String)((GenericValue)schemeTimePeriodIdList.get(i)).get("schemeTimePeriodId");
					Map<String, Object> resultPartyQuotaBalanceHistoryMap = getPartyQuotaBalanceHistory(dctx,UtilMisc.toMap("productCategoryId",productCategoryId,"partyId",partyId,"schemeTimePeriodId",schemeTimePeriodId));	
					GenericValue partyQuotaBalanceHistory=(GenericValue)resultPartyQuotaBalanceHistoryMap.get("partyQuotaBalanceHistory");
					//if partyQuotaBalanceHistory is exist
					if(UtilValidate.isNotEmpty(partyQuotaBalanceHistory)){
						//getting balancequota
						quota=quota.add((BigDecimal)partyQuotaBalanceHistory.get("balancequota"));
						//if quota>0
						if(quota.compareTo(BigDecimal.ZERO)>0){
							//if quantity>quota
							if(remainingQty.compareTo(quota)>0){
								remainingQty=remainingQty.subtract(quota);
						        partyQuotaBalanceHistory.set("usedQuota",(BigDecimal)partyQuotaBalanceHistory.get("quotaEligibility"));
								partyQuotaBalanceHistory.set("balancequota",BigDecimal.ZERO);
								partyQuotaBalanceHistory.store();
							}//if quota > quantity
						    else{
						    	BigDecimal totalUsedQuota=((BigDecimal)partyQuotaBalanceHistory.get("usedQuota")).add(remainingQty);
						    	BigDecimal totalBalanceQuota=((BigDecimal)partyQuotaBalanceHistory.get("quotaEligibility")).subtract(totalUsedQuota);
						    	partyQuotaBalanceHistory.set("usedQuota",totalUsedQuota);
						    	partyQuotaBalanceHistory.set("balancequota",totalBalanceQuota);
								partyQuotaBalanceHistory.store();
								remainingQty=BigDecimal.ZERO;
						     }
							
						}//if quota == 0
						else{ continue; }
						
					} //if partyQuotaBalanceHistory is not exist create new row
					else{
						
						BigDecimal maxQty =((BigDecimal)schemeProductCategory.get("maxQty")).multiply(partyLooms);
						BigDecimal usedQuota = maxQty;
						BigDecimal balancequota = BigDecimal.ZERO;
						//if remainingQty < maxQty
						if(remainingQty.compareTo(maxQty)<0){
							usedQuota = remainingQty;
							balancequota = maxQty.subtract(remainingQty);
							remainingQty=BigDecimal.ZERO;
						 }
						//if remainingQty > maxQty
						else{
							remainingQty=remainingQty.subtract(maxQty);
						}
						
						GenericValue newItemAttr = delegator.makeValue("PartyQuotaBalanceHistory");        	 
						newItemAttr.set("schemeId", schemeId);
						newItemAttr.set("productCategoryId",productCategoryId);
						newItemAttr.set("partyId", partyId);
						newItemAttr.set("schemeTimePeriodId", schemeTimePeriodId);
						newItemAttr.set("quotaEligibility", maxQty);
						newItemAttr.set("usedQuota", usedQuota);
						newItemAttr.set("balancequota", balancequota);
						newItemAttr.set("createdStamp", nowTimeStamp);
						newItemAttr.set("createdTxStamp", nowTimeStamp);
						newItemAttr.create();
						quota=quota.add(usedQuota);
						if(remainingQty.compareTo(BigDecimal.ZERO)==0){
							break;
						}
					}
				  }
				}
				//if periodTime is not exist
				else{
					schemeTimePeriodId=(String)((GenericValue)schemeTimePeriodIdList.get(0)).get("schemeTimePeriodId");
					Map<String, Object> resultPartyQuotaBalanceHistoryMap = getPartyQuotaBalanceHistory(dctx,UtilMisc.toMap("productCategoryId",productCategoryId,"partyId",partyId,"schemeTimePeriodId",schemeTimePeriodId));	
					GenericValue partyQuotaBalanceHistory=(GenericValue)resultPartyQuotaBalanceHistoryMap.get("partyQuotaBalanceHistory");
					//if partyQuotaBalanceHistory is exist
					if(UtilValidate.isNotEmpty(partyQuotaBalanceHistory)){
						//getting balancequota
						quota=quota.add((BigDecimal)partyQuotaBalanceHistory.get("balancequota"));
						//if quota>0
						if(quota.compareTo(BigDecimal.ZERO)>0){
							//if quantity>quota
							if(remainingQty.compareTo(quota)>0){
								remainingQty=remainingQty.subtract(quota);
						        partyQuotaBalanceHistory.set("usedQuota",(BigDecimal)partyQuotaBalanceHistory.get("quotaEligibility"));
								partyQuotaBalanceHistory.set("balancequota",BigDecimal.ZERO);
								partyQuotaBalanceHistory.store();
							}//if quota > quantity
						    else{
						    	
						    	BigDecimal totalUsedQuota=((BigDecimal)partyQuotaBalanceHistory.get("usedQuota")).add(remainingQty);
						    	BigDecimal totalBalanceQuota=((BigDecimal)partyQuotaBalanceHistory.get("quotaEligibility")).subtract(totalUsedQuota);
						    	partyQuotaBalanceHistory.set("usedQuota",totalUsedQuota);
						    	partyQuotaBalanceHistory.set("balancequota",totalBalanceQuota);
								partyQuotaBalanceHistory.store();
								remainingQty=BigDecimal.ZERO;
						     }
							
						}						
					} //if partyQuotaBalanceHistory is not exist create new row
					else{
						BigDecimal maxQty =((BigDecimal)schemeProductCategory.get("maxQty")).multiply(partyLooms);
						BigDecimal usedQuota = maxQty;
						BigDecimal balancequota = BigDecimal.ZERO;
						
						//if remainingQty < maxQty
						if(remainingQty.compareTo(maxQty)<0){
							usedQuota = remainingQty;
							balancequota = maxQty.subtract(remainingQty);
							remainingQty=BigDecimal.ZERO;
						 }
						//if remainingQty > maxQty
						else{
							remainingQty=remainingQty.subtract(maxQty);
						}

						GenericValue newItemAttr = delegator.makeValue("PartyQuotaBalanceHistory");        	 
						newItemAttr.set("schemeId", schemeId);
						newItemAttr.set("productCategoryId",productCategoryId);
						newItemAttr.set("partyId", partyId);
						newItemAttr.set("schemeTimePeriodId", schemeTimePeriodId);
						newItemAttr.set("quotaEligibility", maxQty);
						newItemAttr.set("usedQuota", usedQuota);
						newItemAttr.set("balancequota", balancequota);
						newItemAttr.set("createdStamp", nowTimeStamp);
						newItemAttr.set("createdTxStamp", nowTimeStamp);
						newItemAttr.create();
						quota=quota.add(usedQuota);
					}
				}
			}
			result.put("quota",quota);
		}catch(Exception e){
			Debug.logError(e, module);
			Debug.logError(e, "Error in getting scheme time period", module);	 		  		  
		}
		return result;
	}	
	
	public static Map<String, Object> getPartyQuotaBalanceHistory(DispatchContext dctx, Map context) {
	    GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    List condsList = FastList.newInstance();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String schemeId="TEN_PERCENT_MGPS";
		String partyId=(String) context.get("partyId");
		String productCategoryId=(String) context.get("productCategoryId");
		String schemeTimePeriodId=(String) context.get("schemeTimePeriodId");
		condsList.add(EntityCondition.makeCondition("schemeId", EntityOperator.EQUALS,schemeId ));
		condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,productCategoryId));
		condsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		condsList.add(EntityCondition.makeCondition("schemeTimePeriodId", EntityOperator.EQUALS, schemeTimePeriodId));
		EntityCondition cond = EntityCondition.makeCondition(condsList, EntityOperator.AND);
 		List<GenericValue> partyQuotaBalanceHistoryList=FastList.newInstance();
 		try {	
 			partyQuotaBalanceHistoryList = delegator.findList("PartyQuotaBalanceHistory",cond,null, null, null, true);   
 		} catch (Exception e) {
			Debug.logError(e, "Failed to create PartyQuotaBalanceHistory ", module);
			return ServiceUtil.returnError("Failed to create PartyQuotaBalanceHistory " + e);
		}
 		if(UtilValidate.isNotEmpty(partyQuotaBalanceHistoryList)){
			GenericValue partyQuotaBalanceHistory=(GenericValue)partyQuotaBalanceHistoryList.get(0);
			result.put("partyQuotaBalanceHistory",partyQuotaBalanceHistory);
 		}
		return result;
	}
	
	public static Map<String, Object> getPartyAvailableQuotaBalanceHistory(DispatchContext dctx, Map context) {
	   GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
	   LocalDispatcher dispatcher = dctx.getDispatcher();
	   Map<String, Object> result = ServiceUtil.returnSuccess();
	   Map<String, Object> productCategoryQuotamap = ServiceUtil.returnSuccess();
	   Map<String, Object> usedQuotaMap = ServiceUtil.returnSuccess();
	   List conditionList = FastList.newInstance();
		String schemeId="TEN_PERCENT_MGPS";
		String partyId=(String) context.get("partyId");
		Timestamp effectiveDate=(Timestamp) context.get("effectiveDate");
		String productCategoryId=(String) context.get("productCategoryId");
		if(UtilValidate.isEmpty(effectiveDate)){
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		//get schemeTime period List start
		
		String periodTypeId="TEN_PERC_PERIOD";
		List<GenericValue>  schemeTimePeriodIdList =  FastList.newInstance();
		Map<String, Object> resultMap = ServiceUtil.returnSuccess();
		try {
			
			resultMap=dispatcher.runSync("getSchemeTimePeriodId", UtilMisc.toMap("periodTypeId",periodTypeId,"fromDate",effectiveDate,"thruDate",effectiveDate,"orderBy","","userLogin", userLogin));   
		} catch (Exception e) {
			Debug.logError(e, "Failed to get getSchemeTimePeriodId ", module);
			return ServiceUtil.returnError("Failed to get getSchemeTimePeriodId " + e);
		}
		if (ServiceUtil.isError(resultMap)) {
	   	Debug.logError("Error getting Scheme Time Period", module);	
	       return ServiceUtil.returnError("Error getting Scheme Time Period");
			}
		  schemeTimePeriodIdList=(List<GenericValue>)resultMap.get("schemeTimePeriodIdList");
		
		//schemeTime period List ends 

		//get SchemeProductCategory List start
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("schemeId", EntityOperator.EQUALS, schemeId));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
				EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
		if(UtilValidate.isNotEmpty(productCategoryId)){
			conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
		}
		List<GenericValue> productCategoryApplicableSchemes = null;
		try {
			productCategoryApplicableSchemes = delegator.findList("SchemeProductCategory", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive SchemeProduct ", module);
			return ServiceUtil.returnError("Failed to retrive SchemeProduct " + e);
		}
		
		if(UtilValidate.isNotEmpty(schemeTimePeriodIdList)){
			BigDecimal partyLooms = BigDecimal.ZERO;
			for(int j=0; j<productCategoryApplicableSchemes.size(); j++){
				GenericValue schemeProductCategory = productCategoryApplicableSchemes.get(j);
				productCategoryId = schemeProductCategory.getString("productCategoryId");
				BigDecimal usedQuata=BigDecimal.ZERO;
				BigDecimal periodTime=(BigDecimal)schemeProductCategory.get("periodTime");
				partyLooms = BigDecimal.ZERO;
				//get PartyLoom start  
				conditionList.clear();
			    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			    conditionList.add(EntityCondition.makeCondition("loomTypeId", EntityOperator.EQUALS, productCategoryId));
			    conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
						EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
				
				try {
					List<GenericValue> partyLoomsList = delegator.findList("PartyLoom", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
					if(UtilValidate.isNotEmpty(partyLoomsList)){
						partyLooms=(BigDecimal)partyLoomsList.get(0).get("quantity");
					}
				} catch (GenericEntityException e) {
					Debug.logError(e, "Failed to retrive SchemeParty ", module);
					return ServiceUtil.returnError("Failed to retrive SchemeParty " + e);
				}
				//get PartyLoom ends 
				
				BigDecimal availableQuata=((BigDecimal)schemeProductCategory.get("maxQty")).multiply(partyLooms);
				
				if(UtilValidate.isNotEmpty(periodTime)){
					availableQuata=availableQuata.multiply(periodTime);
					
	//				//periodTime does not match with scheme time period count
	//				if(periodTime.intValueExact()>schemeTimePeriodIdList.size()){
	//					return ServiceUtil.returnError("schemeTimePeriod count does not match with SchemeProductCategory periodTime. ");
	//				}
				  for(int i=0;i< periodTime.intValueExact();i++){
					if(schemeTimePeriodIdList.size()>i){
						String schemeTimePeriodId=(String)((GenericValue)schemeTimePeriodIdList.get(i)).get("schemeTimePeriodId");
						Map<String, Object> resultPartyQuotaBalanceHistoryMap = getPartyQuotaBalanceHistory(dctx,UtilMisc.toMap("productCategoryId",productCategoryId,"partyId",partyId,"schemeTimePeriodId",schemeTimePeriodId));	
						GenericValue partyQuotaBalanceHistory=(GenericValue)resultPartyQuotaBalanceHistoryMap.get("partyQuotaBalanceHistory");
						//if partyQuotaBalanceHistory is exist
						if(UtilValidate.isNotEmpty(partyQuotaBalanceHistory)){
							//getting balancequota
							usedQuata=usedQuata.add((BigDecimal)partyQuotaBalanceHistory.get("usedQuota"));
						}
						else{
							break;
						}
					}
					
				  }
	//				usedQuata=getUsedQuataFromQuotaBalanceHistory(dctx,UtilMisc.toMap("periodTypeId",periodTypeId,"fromDate",effectiveDate,"thruDate",effectiveDate,"userLogin", userLogin));
				}
				else{
					String schemeTimePeriodId=(String)((GenericValue)schemeTimePeriodIdList.get(0)).get("schemeTimePeriodId");
					Map<String, Object> resultPartyQuotaBalanceHistoryMap = getPartyQuotaBalanceHistory(dctx,UtilMisc.toMap("productCategoryId",productCategoryId,"partyId",partyId,"schemeTimePeriodId",schemeTimePeriodId));	
					GenericValue partyQuotaBalanceHistory=(GenericValue)resultPartyQuotaBalanceHistoryMap.get("partyQuotaBalanceHistory");
					if(UtilValidate.isNotEmpty(partyQuotaBalanceHistory)){
						//getting balancequota
						usedQuata=usedQuata.add((BigDecimal)partyQuotaBalanceHistory.get("usedQuota"));
					}
				}
				availableQuata=availableQuata.subtract(usedQuata);
				usedQuotaMap.put(productCategoryId,usedQuata);
				productCategoryQuotamap.put(productCategoryId,availableQuata);
				
			}
			result.put("schemesMap",productCategoryQuotamap);
			result.put("usedQuotaMap",usedQuotaMap);
		}
		else{
			// we need to insert SchemeTimePeriods
		}
	
		return result;
	}
	
	public static Map<String, Object> createSOForQuote(DispatchContext ctx, Map<String, ? extends Object> context){ 
		 
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String currencyUomId = (String) context.get("defaultOrganizationPartyCurrencyUomId");
		String quoteId = (String)context.get("quoteId");
		String orderTaxType = "INTRA";
		String schemeCategory = "MGPS_10Pecent";
		String billingType = "Direct";
		GenericValue quote =null;
		String orderId = null;
		String PONumber = null;
		String promotionAdjAmt = null;
		String orderMessage = null;
		String billFromPartyId ="Company";
		String disableAcctgFlag ="Y";
		List orderAdjChargesList = FastList.newInstance();
		Map processOrderContext = FastMap.newInstance();
		String salesChannel = (String) context.get("salesChannelEnumId");
		List<GenericValue> quoteItemList = FastList.newInstance();
		String supplierPartyId = "";
		String partyId = ""; 
		String productStoreId = "";
		String custRequestId = "";
		try {
			
			List productIds = FastList.newInstance();
			List<GenericValue> inventoryItems = FastList.newInstance();
			quote=delegator.findOne("Quote",UtilMisc.toMap("quoteId", quoteId), false);
			List conditionList = UtilMisc.toList(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId));
	        conditionList.add(EntityCondition.makeCondition("qiStatusId", EntityOperator.EQUALS, "QTITM_QUALIFIED"));
	        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			quoteItemList = delegator.findList("QuoteAndItemAndCustRequest", condition, null, UtilMisc.toList("quoteItemSeqId"), null, false);
			productIds = EntityUtil.getFieldListFromEntityList(quoteItemList, "productId", true);
			supplierPartyId = quote.getString("partyId");
			GenericValue quoteItems = EntityUtil.getFirst(quoteItemList);
			custRequestId = quoteItems.getString("custRequestId");
	    	List<GenericValue> requirements = delegator.findList("RequirementCustRequest", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId), null, null, null, false);
	    	String requirementId = (EntityUtil.getFirst(requirements)).getString("requirementId");
	    	List<GenericValue> requirementRole = delegator.findList("RequirementRole", EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId), null, null, null, false);
		    partyId = (EntityUtil.getFirst(requirementRole)).getString("partyId");
			List<GenericValue> partyRelations = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId), null, null, null, false);
			String partyIdFrom = (EntityUtil.getFirst(partyRelations)).getString("partyIdFrom");
			GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyIdFrom), false);
			if(UtilValidate.isNotEmpty(partyGroup)){
				productStoreId =partyGroup.getString("groupName");
			}
			List indentProductList = FastList.newInstance();
			String schemePartyId=partyId;
			String productId = null;
			String remarks = null;
			String productFeatureId = null;
			String batchNo = null;
			String daysToStore = null;
			BigDecimal quantity = BigDecimal.ZERO;
			BigDecimal unitPrice = BigDecimal.ZERO;
			BigDecimal baleQuantity = BigDecimal.ZERO;
			BigDecimal bundleWeight = BigDecimal.ZERO;
			String yarnUOM="Bale";
			BigDecimal basicPrice = BigDecimal.ZERO;
			BigDecimal cstPrice = BigDecimal.ZERO;
			BigDecimal tcsPrice = BigDecimal.ZERO;
			BigDecimal vatPrice = BigDecimal.ZERO;
			BigDecimal bedPrice = BigDecimal.ZERO;
			BigDecimal serviceTaxPrice = BigDecimal.ZERO;
	        if(UtilValidate.isNotEmpty(quoteItemList)){
	        	Iterator<GenericValue> itr = quoteItemList.iterator();
	        	 while (itr.hasNext()) {
	        	    Map<String  ,Object> productQtyMap = FastMap.newInstance(); 
	                GenericValue quoteItem = itr.next();
	                productId = quoteItem.getString("productId");
	                quantity = quoteItem.getBigDecimal("quantity");
	                unitPrice = quoteItem.getBigDecimal("quoteUnitPrice");
	                Map<String, Object> quoteItemCtx = FastMap.newInstance();
	                productQtyMap.put("productId", productId);
					productQtyMap.put("quantity", quantity);
					productQtyMap.put("customerId", partyId);
					productQtyMap.put("remarks", remarks);
					productQtyMap.put("baleQuantity", baleQuantity);
					productQtyMap.put("bundleWeight", bundleWeight);
					productQtyMap.put("yarnUOM", yarnUOM);
					productQtyMap.put("batchNo", batchNo);
					productQtyMap.put("daysToStore", daysToStore);
					productQtyMap.put("basicPrice", unitPrice);
					productQtyMap.put("bedPrice", bedPrice);
					productQtyMap.put("cstPrice", cstPrice);
					productQtyMap.put("tcsPrice", tcsPrice);
					productQtyMap.put("vatPrice", vatPrice);
					productQtyMap.put("serviceTaxPrice", serviceTaxPrice);
					indentProductList.add(productQtyMap);
	            }
	        }
	        if( UtilValidate.isEmpty(indentProductList)){
				Debug.logWarning("indentProductList is Empty", module);
			}
	        processOrderContext.put("userLogin", userLogin);
			processOrderContext.put("productQtyList", indentProductList);
			processOrderContext.put("partyId", partyId);
			processOrderContext.put("schemePartyId", "");
			processOrderContext.put("supplierPartyId", supplierPartyId);
			processOrderContext.put("billToCustomer", partyId);
			processOrderContext.put("productIds", productIds);
			processOrderContext.put("supplyDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
			processOrderContext.put("salesChannel", salesChannel);
			processOrderContext.put("orderTaxType", orderTaxType);
			processOrderContext.put("orderId", orderId);
			processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
			processOrderContext.put("productStoreId", productStoreId);
			processOrderContext.put("PONumber", "");
			processOrderContext.put("promotionAdjAmt", "");
			processOrderContext.put("orderMessage", "");
			processOrderContext.put("orderAdjChargesList", orderAdjChargesList);
			processOrderContext.put("disableAcctgFlag", disableAcctgFlag);
			try{
				result = processBranchSalesOrder(ctx, processOrderContext);
				if(ServiceUtil.isError(result)){
					Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
				}
				
				orderId = (String)result.get("orderId");
				if(UtilValidate.isEmpty(orderId)){
					Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
				}
				if(UtilValidate.isNotEmpty(billingType) && billingType.equals("onBehalfOf")){
					 Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId, "partyId", schemePartyId, "roleTypeId", "ON_BEHALF_OF");
					 try {
					 GenericValue value = delegator.makeValue("OrderRole", fields);
					 delegator.create(value);
					 } catch (GenericEntityException e) {
							Debug.logError(e, "Could not add role to order for OnBeHalf  party " + schemePartyId, module);
					 }
				}
				
				if(UtilValidate.isNotEmpty(supplierPartyId)){
					try{
						GenericValue supplierOrderRole	=delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", supplierPartyId, "roleTypeId", "SUPPLIER"));
						delegator.createOrStore(supplierOrderRole);
					}catch (Exception e) {
						  Debug.logError(e, "Error While Creating OrderRole(SUPPLIER)  for  Sale Indent ", module);
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
							Debug.logError(e, "Could not add role to order for OnBeHalf  party " + orderId, module);
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
						Debug.logError(e, "Could not add role to order for SchemeCategory " + orderId, module);
				    }
				}
				Map resultCtx = FastMap.newInstance();
				resultCtx = dispatcher.runSync("createOrderHeaderSequence",UtilMisc.toMap("orderId", orderId ,"userLogin",userLogin, "orderHeaderSequenceTypeId","DEPOT_SALE_SEQUENCE"));
				if(ServiceUtil.isError(resultCtx)){
					Debug.logError("Problem while Creating  Sequence for orderId:"+orderId, module);
				}
			}catch(Exception e){
				Debug.logError(e, module);
			}
			
	     }catch(Exception e){
	    	 Debug.logError("Order Entry successfully for party : "+partyId, module);
	 		 return ServiceUtil.returnError("Order Entry successfully for party : "+partyId);
		 }
			
		result.put("orderId", orderId);
		return result;
		
	 }
	
   	public static Map<String, Object> getAllChildProductCategoriesAndMembers(DispatchContext dctx, Map<String, ? extends Object> context){
   	    Delegator delegator = dctx.getDelegator();
   	    LocalDispatcher dispatcher = dctx.getDispatcher();
   	    GenericValue userLogin = (GenericValue) context.get("userLogin");
   	    String productCategoryId = (String) context.get("productCategoryId");
   	    Map result = ServiceUtil.returnSuccess();
   	    
   	    List<GenericValue> completeProductCategoryMembers = FastList.newInstance();
   	    List completeProductIdsList = FastList.newInstance();
   	    
   	    List categoriesList = FastList.newInstance();
   	    categoriesList.add(productCategoryId);
   	    
   	    List completeChildCategoriesList = FastList.newInstance();
   	    List baseProductCategoriesList = FastList.newInstance();
   	    
   	    List childCategoriesList = FastList.newInstance();
   	    
   	    for(int i=0; i<categoriesList.size(); i++){
			String categoryId = (String) categoriesList.get(i);
			
			// Get Category Members
			Map prodCatMemCtx = UtilMisc.toMap("userLogin",userLogin);	  	
			prodCatMemCtx.put("productCategoryId", categoryId);
		  	try{
		  		Map resultCtx = dispatcher.runSync("getCategoryMembers",prodCatMemCtx);  		  		 
		  		if (ServiceUtil.isError(resultCtx)) {
		  	 		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
		  	 		Debug.logError(errMsg , module);
		  		}	
		  		List<GenericValue> productCategoryMembers = (List)resultCtx.get("productCategoryMembers");
		  		if(UtilValidate.isNotEmpty(productCategoryMembers)){
		  			completeProductCategoryMembers.addAll(productCategoryMembers);
		  			completeProductIdsList.addAll(EntityUtil.getFieldListFromEntityList(productCategoryMembers, "productId", true));
		  		}
		  		
		  	}catch (GenericServiceException e) {
		  		Debug.logError(e , module);
		  		return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
		  	}
			
			Map prodCatCtx = UtilMisc.toMap("userLogin",userLogin);	  	
	   	    prodCatCtx.put("primaryParentCategoryId", categoryId);
		  	try{
		  		Map resultCtx = dispatcher.runSync("getChildCategories",prodCatCtx);  		  		 
		  		if (ServiceUtil.isError(resultCtx)) {
		  	 		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
		  	 		Debug.logError(errMsg , module);
		  		}	
		  		List<GenericValue> productCategories = (List)resultCtx.get("childCategoriesList");
		  		if(UtilValidate.isNotEmpty(productCategories)){
		  			childCategoriesList = EntityUtil.getFieldListFromEntityList(productCategories, "productCategoryId", true);
		  			completeChildCategoriesList.addAll(childCategoriesList);
		  		}
		  		else{
		  			baseProductCategoriesList.add(categoryId);
		  		}
		  		
		  	}catch (GenericServiceException e) {
		  		Debug.logError(e , module);
		  		return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
		  	}
		  	if(i == (categoriesList.size()-1)){
		  		if(UtilValidate.isNotEmpty(childCategoriesList)){
		  			List tempChildCategoriesList = FastList.newInstance();
		  			tempChildCategoriesList.addAll(childCategoriesList);
		  			childCategoriesList.clear();
		  			categoriesList = tempChildCategoriesList;
		  			i = -1;
		  		}
		  	}
		  	
   	    }	
   	    result.put("completeProductCategoryMembers",completeProductCategoryMembers);
   	    result.put("completeProductIdsList",completeProductIdsList);
   	    result.put("completeChildCategoriesList",completeChildCategoriesList);
   	    return result;
   	}
   	

	public static Map<String, Object> getTaxApplicabilityDetails(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        Map<String, Object> result = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
       
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        String productStoreId = (String) context.get("productStoreId");
        String supplierPartyId = (String) context.get("supplierPartyId");
        String partyId = (String) context.get("partyId");
        String payToPartyId = null;
        
        Debug.log("productStoreId = ========"+productStoreId);
        Debug.log("supplierPartyId = ========"+supplierPartyId);
        Debug.log("partyId = ========"+partyId);
        
        try{
        	GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if(UtilValidate.isNotEmpty(productStore)){
				payToPartyId = productStore.getString("payToPartyId");
			}
		}catch(GenericEntityException e){
			Debug.logError(e, "Error Fetching Product Store", module);
		}
        
        String partyGeoId = null;
        String branchGeoId = null;
        String supplierGeoId = null;
        String checkForE2Form = "N";
        String taxTypeApplicable = null;
        String titleTransferEnumId = null;
        String taxType = null;
        
        List partyContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false, "TAX_CONTACT_MECH");
        if(UtilValidate.isNotEmpty(partyContactMechValueMaps)){
        	partyGeoId = (String)((GenericValue) ((Map) partyContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
        }
        
        List supplierContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, supplierPartyId, false, "TAX_CONTACT_MECH");
        if(UtilValidate.isNotEmpty(supplierContactMechValueMaps)){
        	supplierGeoId = (String)((GenericValue) ((Map) supplierContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
        }
        
        List branchContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, payToPartyId, false, "TAX_CONTACT_MECH");
        if(UtilValidate.isNotEmpty(branchContactMechValueMaps)){
        	branchGeoId = (String)((GenericValue) ((Map) branchContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
        }
        
        Debug.log("partyGeoId = ========"+partyGeoId);
        Debug.log("supplierGeoId = ========"+supplierGeoId);
        Debug.log("branchGeoId = ========"+branchGeoId);
        
        
        if( (UtilValidate.isNotEmpty(partyGeoId)) && (UtilValidate.isNotEmpty(supplierGeoId)) && (UtilValidate.isNotEmpty(branchGeoId))   ){
        	if(partyGeoId.equals(branchGeoId)){
        		taxType = "Intra-State";
        		if(partyGeoId.equals(supplierGeoId)){
            		// Vat is applicable
        			taxTypeApplicable = "VAT_SALE";
        			
        			/*List taxCondList= FastList.newInstance();;
        			taxCondList.add(EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.EQUALS, partyGeoId));
        			taxCondList.add(EntityCondition.makeCondition("taxAuthorityRateTypeId", EntityOperator.EQUALS, taxTypeApplicable));
    			    EntityCondition condExpress = EntityCondition.makeCondition(taxCondList, EntityOperator.AND);
        			
        			List<GenericValue> applicableTaxTypes = null;
        			try {
        				applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
        			} catch (GenericEntityException e) {
        				Debug.logError(e, "Failed to retrive ProductPriceType ", module);
        				return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
        			}*/
            	}
        		else{
        			// check for A2 form
        			checkForE2Form = "Y";
        			taxTypeApplicable = "VAT_SALE";
        			titleTransferEnumId = "E2_SALE";
        		}
        	}
        	else{
        		taxType = "Inter-State";
        		taxTypeApplicable = "CST_SALE";
        		titleTransferEnumId = "CST_NOCFORM";
        	}
        }
        
        Map geoIdsMap = FastMap.newInstance();
        geoIdsMap.put("partyGeoId", partyGeoId);
        geoIdsMap.put("supplierGeoId", supplierGeoId);
        geoIdsMap.put("branchGeoId", branchGeoId);
        geoIdsMap.put("taxTypeApplicable", taxTypeApplicable);
        geoIdsMap.put("titleTransferEnumId", titleTransferEnumId);
        geoIdsMap.put("taxType", taxType);
        geoIdsMap.put("checkForE2Form", checkForE2Form);
        Debug.log("geoIdsMap = ========"+geoIdsMap);
		result.put("geoIdsMap", geoIdsMap);
        return result;
    }
	
	public static Map<String, Object> calculateTaxesByGeoId(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        Map<String, Object> result = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
       
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        String taxAuthGeoId = (String) context.get("taxAuthGeoId");
        String taxAuthorityRateTypeId = (String) context.get("taxAuthorityRateTypeId");
        String productId = (String) context.get("productId");
        BigDecimal taxPercentage = BigDecimal.ZERO;
        
        /*if( (UtilValidate.isNotEmpty(taxAuthorityRateTypeId)) && (taxAuthorityRateTypeId.equals("CST_SALE")) ){
        	taxAuthGeoId = "IND";
        }*/
        
        List taxCondList= FastList.newInstance();
		taxCondList.add(EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.IN, UtilMisc.toList(taxAuthGeoId, "IND")));
		if(UtilValidate.isNotEmpty(taxAuthorityRateTypeId)){
			taxCondList.add(EntityCondition.makeCondition("taxAuthorityRateTypeId", EntityOperator.EQUALS, taxAuthorityRateTypeId));
		}
		List<GenericValue> applicableTaxList = null;
		try {
			applicableTaxList = delegator.findList("TaxAuthorityRateProduct", EntityCondition.makeCondition(taxCondList, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive ProductPriceType ", module);
			return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
		}
		List productCategoriesList = EntityUtil.getFieldListFromEntityList(applicableTaxList, "productCategoryId", true);
        String taxAuthProdCat = null;
		
		for(int i=0; i<productCategoriesList.size(); i++){
			String productCategoryId = (String) productCategoriesList.get(i);
			Map prodCatCtx = UtilMisc.toMap("userLogin",userLogin);	  	
        	prodCatCtx.put("productCategoryId", productCategoryId);
		  	try{
		  		Map resultCtx = dispatcher.runSync("getAllChildProductCategoriesAndMembers",prodCatCtx);  	
		  		List completeProductIdsList = (List) resultCtx.get("completeProductIdsList");
		  		if(completeProductIdsList.contains(productId)){
		  			taxAuthProdCat = productCategoryId;
		  			break;
		  		}
		  		if (ServiceUtil.isError(resultCtx)) {
		  	 		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
		  	 		Debug.logError(errMsg , module);
		  		}	
		  	}catch (GenericServiceException e) {
		  		Debug.logError(e , module);
		  		return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
		  	}
		}
		
		List<GenericValue> taxAuthProdCatList = FastList.newInstance();
		
		if(UtilValidate.isEmpty(taxAuthProdCat)){
			result.put("taxPercentage", taxPercentage);
			result.put("taxAuthProdCatList", taxAuthProdCatList);
	        return result;
		}
		
		taxAuthProdCatList = EntityUtil.filterByCondition(applicableTaxList, EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, taxAuthProdCat));
		if(UtilValidate.isEmpty(taxAuthProdCatList)){
			result.put("taxPercentage", taxPercentage);
			result.put("taxAuthProdCatList", taxAuthProdCatList);
	        return result;
		}
		
		GenericValue taxAuthProdCatValue = EntityUtil.getFirst(taxAuthProdCatList);
		
		if(UtilValidate.isNotEmpty(taxAuthProdCatValue.get("taxPercentage"))){
			taxPercentage = (BigDecimal) taxAuthProdCatValue.get("taxPercentage");
		}
		
		BigDecimal vatPercent = BigDecimal.ZERO;
		
		List vatCondList= FastList.newInstance();
		vatCondList.add(EntityCondition.makeCondition("taxAuthorityRateTypeId", EntityOperator.EQUALS, "VAT_SALE"));
		vatCondList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		List vatTaxes = EntityUtil.filterByCondition(taxAuthProdCatList, EntityCondition.makeCondition(vatCondList, EntityOperator.AND));
		Debug.log("vatTaxes ============"+vatTaxes);
		if(UtilValidate.isNotEmpty(vatTaxes)){
			vatPercent = (BigDecimal)(EntityUtil.getFirst(vatTaxes)).get("taxPercentage");
		}
		
		List vatSurchargeList = FastList.newInstance();
		vatCondList.clear();
		vatCondList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "VAT_SALE"));
		try {
			List vatSurcharges = delegator.findList("TaxAuthorityRateType", EntityCondition.makeCondition(vatCondList, EntityOperator.AND), null, null, null, false);
			Debug.log("vatSurcharges ============"+vatSurcharges);
			
			if(UtilValidate.isNotEmpty(vatSurcharges)){
				vatCondList.clear();
				vatCondList.add(EntityCondition.makeCondition("taxAuthorityRateTypeId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(vatSurcharges, "taxAuthorityRateTypeId", true)));
				vatCondList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
				vatSurchargeList = EntityUtil.filterByCondition(taxAuthProdCatList, EntityCondition.makeCondition(vatCondList, EntityOperator.AND));
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive TaxAuthorityRateType ", module);
			return ServiceUtil.returnError("Failed to retrive TaxAuthorityRateType " + e);
		}
				
				
		BigDecimal cstPercent = BigDecimal.ZERO;
		
		List cstCondList= FastList.newInstance();
		cstCondList.add(EntityCondition.makeCondition("taxAuthorityRateTypeId", EntityOperator.EQUALS, "CST_SALE"));
		cstCondList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		List cstTaxes = EntityUtil.filterByCondition(taxAuthProdCatList, EntityCondition.makeCondition(cstCondList, EntityOperator.AND));
		Debug.log("cstTaxes ============"+cstTaxes);
		if(UtilValidate.isNotEmpty(cstTaxes)){
			cstPercent = (BigDecimal)(EntityUtil.getFirst(cstTaxes)).get("taxPercentage");
		}
		
		List cstSurchargeList = FastList.newInstance();
		cstCondList.clear();
		cstCondList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "CST_SALE"));
		try {
			List cstSurcharges = delegator.findList("TaxAuthorityRateType", EntityCondition.makeCondition(cstCondList, EntityOperator.AND), null, null, null, false);
			Debug.log("cstSurcharges ============"+cstSurcharges);
			if(UtilValidate.isNotEmpty(cstSurchargeList)){
				cstCondList.clear();
				cstCondList.add(EntityCondition.makeCondition("taxAuthorityRateTypeId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(cstSurcharges, "taxAuthorityRateTypeId", true)));
				cstCondList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
				cstSurchargeList = EntityUtil.filterByCondition(taxAuthProdCatList, EntityCondition.makeCondition(cstCondList, EntityOperator.AND));
			}
		
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive TaxAuthorityRateType ", module);
			return ServiceUtil.returnError("Failed to retrive TaxAuthorityRateType " + e);
		}
		
		
		
		Debug.log("vatPercent ============"+vatPercent);
		Debug.log("cstPercent ============"+cstPercent);
		
		Debug.log("taxPercentage ============"+taxPercentage);
		Debug.log("taxAuthProdCatList ============"+taxAuthProdCatList);
		result.put("vatPercent", vatPercent);
		result.put("vatSurcharges", vatSurchargeList);
		result.put("cstSurcharges", cstSurchargeList);
		result.put("cstPercent", cstPercent);
		result.put("taxPercentage", taxPercentage);
		result.put("taxAuthProdCatList", taxAuthProdCatList);
        return result;
    }
   	
	//	sales invoice generation based on shipment
	
	public static Map<String, Object> createBranchSaleInvoice(DispatchContext ctx,Map<String, ? extends Object> context) {
	   
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Locale locale = (Locale) context.get("locale");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    String shipmentId = (String) context.get("shipmentId");
	    String orderId = (String) context.get("orderId");
	    String billToPartyId = (String) context.get("billToPartyId");
	    String purchaseInvoiceId = (String) context.get("purchaseInvoiceId");
	    List<GenericValue> items = null;
	    try {
	    	items = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, UtilMisc.toList("orderItemSeqId"), null, false);
        } catch (GenericEntityException e) {
            String errMsg = UtilProperties.getMessage(resource, "AccountingProblemGettingItemsFromShipments", locale);
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
	    Debug.log("items ======"+items);
	    if (items.size() == 0) {
            Debug.logInfo("No items issued for shipments", module);
            return ServiceUtil.returnSuccess();
        }
	    
	    List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, purchaseInvoiceId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
		List<GenericValue> invoiceItemList =null;
		try{
	      invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		} catch (Exception e) {
			String errMsg = UtilProperties.getMessage(resource, "AccountingProblemGettingItemsFromShipments", locale);
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
		}
	    
	    GenericValue orderHeader = null;
	    try {
	    	orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
	    } catch (GenericEntityException e) {
            String errMsg = UtilProperties.getMessage(resource, "AccountingProblemGettingItemsFromShipments", locale);
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
	    if (orderHeader == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,"AccountingNoOrderHeader",locale));
        }
	    
	    OrderReadHelper orh = new OrderReadHelper(orderHeader);
	    
	    
	    List<GenericValue> salesOrderitems = null;
	    try {
	    	salesOrderitems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, UtilMisc.toList("orderItemSeqId"), null, false);
	    } catch (GenericEntityException e) {
            String errMsg = UtilProperties.getMessage(resource, "AccountingProblemGettingItemsFromShipments", locale);
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
	    
	    Map shipProdQtyMap = FastMap.newInstance();
	    
	    for (GenericValue receipt : items) {
	    	String productId = (String) receipt.get("productId");
	    	BigDecimal receiptQty = receipt.getBigDecimal("quantityAccepted");
	    	
	    	if(UtilValidate.isEmpty(shipProdQtyMap.get(productId))){
	    		shipProdQtyMap.put(productId, receiptQty);
	    	}
	    	else{
	    		BigDecimal updateQty = (BigDecimal) shipProdQtyMap.get(productId);
	    		shipProdQtyMap.put(productId, receiptQty.add(updateQty));
	    	}
	    	
	    }
	    List<GenericValue> toBillItems = FastList.newInstance();
	    
	    for(Object productKey : shipProdQtyMap.keySet()){
	    	String productId = productKey.toString();
	    	BigDecimal receiptQty = (BigDecimal) shipProdQtyMap.get(productId); 
	    	
	    	if(receiptQty.compareTo(BigDecimal.ZERO) > 0){
	    		List<GenericValue> salesOrderProdItems = EntityUtil.filterByCondition(salesOrderitems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        	
		    	for(int i=0; i<salesOrderProdItems.size(); i++){
	        		GenericValue eachItem = (GenericValue) salesOrderProdItems.get(i);
	        		
	        		BigDecimal itemQty = OrderReadHelper.getOrderItemQuantity(eachItem);
	        		BigDecimal billedQuantity = OrderReadHelper.getOrderItemInvoicedQuantity(eachItem);
	        		BigDecimal billAvail = itemQty.subtract(billedQuantity);
	        		// Now that we know unbilled qty's we can prepare billing items
	                
	                if (receiptQty != null && receiptQty.compareTo(billAvail) > 0) {
	                	
	                	if(i == (salesOrderProdItems.size()-1) ){
	                		eachItem.set("quantity", billedQuantity.add(receiptQty));
		                	toBillItems.add(eachItem);
	                	}
	                	else{
	                		eachItem.set("quantity", billedQuantity.add(billAvail));
		                	toBillItems.add(eachItem);
	                	}
	                	receiptQty = receiptQty.subtract(billAvail).setScale(2, RoundingMode.HALF_UP);
	                } else {
	                	eachItem.set("quantity", billedQuantity.add(receiptQty));
	                	toBillItems.add(eachItem);
	                	break;
	                }
	                
	        	}
	    	}
	    	
	    }
	    
	    String invoiceId = null;
        // Raise Invoice for the unbilled items	    
        Map<String, Object> serviceContext = UtilMisc.toMap("orderId", orderId,"billItems", toBillItems, "eventDate", context.get("eventDate"), "userLogin", context.get("userLogin"));
        serviceContext.put("shipmentId",shipmentId);
        try {
            Map<String, Object> servResult = dispatcher.runSync("createInvoiceForOrderOrig", serviceContext);
            invoiceId = (String) servResult.get("invoiceId");
        } catch (GenericServiceException e) {
            String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
       /* try{
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			Debug.log("invoice========================="+invoice);
			invoice.set("shipmentId", shipmentId);
			invoice.store();
			Debug.log("invoice========================="+invoice);
		}catch (GenericEntityException e) {
			Debug.logError("Error in updating shipment in invoice", module);
			return ServiceUtil.returnError("Error in updating shipment in invoice");
		}*/
        
        // Indentify the adjustments applied to purchase invoice and apply them to the sales invoice
        
        /*List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
		List<GenericValue> invoiceItemList =null;
		try{
	      invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		} catch (Exception e) {
			String errMsg = UtilProperties.getMessage(resource, "AccountingProblemGettingItemsFromShipments", locale);
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
		}
		Debug.log("sales invoiceItemList ======"+invoiceItemList);*/
        
        for(int i=0; i<invoiceItemList.size(); i++){
        	
        	GenericValue eachItem = invoiceItemList.get(i);
        	
        	Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
            createInvoiceItemContext.put("invoiceId",invoiceId);
            createInvoiceItemContext.put("invoiceItemTypeId", eachItem.get("invoiceItemTypeId"));
            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
            createInvoiceItemContext.put("description", eachItem.get("description"));
            createInvoiceItemContext.put("quantity",eachItem.get("quantity"));
            createInvoiceItemContext.put("amount",eachItem.getBigDecimal("amount"));
            createInvoiceItemContext.put("productId", eachItem.get("productId"));
            createInvoiceItemContext.put("userLogin", userLogin);
            try{
            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
            	if(ServiceUtil.isError(createInvoiceItemResult)){
            		String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
                    Debug.logError(errMsg, module);
                    return ServiceUtil.returnError(errMsg);
          		}
            } catch (Exception e) {
            	String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
    		}
        }
        
        
        
        
        
        BigDecimal invoiceAmount = BigDecimal.ZERO;
        BigDecimal outstandingAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        
        try {
			Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("userLogin", userLogin, "invoiceId",invoiceId));
			if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
				Debug.logError(getInvoicePaymentInfoListResult.toString(),module);
				return ServiceUtil.returnError(null, null, null,getInvoicePaymentInfoListResult);
			}
			Map invoicePaymentInfo = (Map) ((List) getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
			outstandingAmount = (BigDecimal) invoicePaymentInfo.get("outstandingAmount");
			invoiceAmount = (BigDecimal) invoicePaymentInfo.get("amount");
			paidAmount = (BigDecimal) invoicePaymentInfo.get("paidAmount");
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
        
        //for Sent SMS 
        String contactNumberTo = null;
        String countryCode = null;
        
		Map<String, Object> getTelParams = FastMap.newInstance();
    	getTelParams.put("partyId", billToPartyId);
        getTelParams.put("userLogin", userLogin);    
        try {
	        Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	        if (ServiceUtil.isError(serviceResult)) {
	        	Debug.logError("Unable to get telephone number for party : " + ServiceUtil.getErrorMessage(result), module);
				//request.setAttribute("_ERROR_MESSAGE_", "Problems while getting Telephone for: " + partyId);
				//return "error";
	        } 
	        contactNumberTo = (String) serviceResult.get("contactNumber");
	        countryCode = (String) serviceResult.get("countryCode");
        } catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}    
         
        Debug.log("contactNumberTo = "+contactNumberTo);
        Debug.log("contactNumberTo = "+contactNumberTo);
        if(UtilValidate.isEmpty(contactNumberTo)){
        	contactNumberTo = "9502532897";
        }
        if(UtilValidate.isNotEmpty(contactNumberTo)){
        	 if(UtilValidate.isNotEmpty(countryCode)){
        		 contactNumberTo = countryCode + contactNumberTo;
        	 }
        	 String grandTotalStr=String.valueOf(invoiceAmount.intValue());
        	 String paidAmountStr=String.valueOf(paidAmount.intValue());
        	 String balanceStr=String.valueOf(outstandingAmount.intValue());
        	 String invoiceMsgToWeaver = UtilProperties.getMessage("ProductUiLabels", "InvoiceMsgToWeaver", locale);
        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("orderId", orderId);
        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("InvoiceValue", grandTotalStr);
        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("advancePaid", paidAmountStr);
        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("balancePayable", balanceStr);
        	 invoiceMsgToWeaver = invoiceMsgToWeaver.replaceAll("invoiceNo", invoiceId);
        	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
             sendSmsParams.put("contactNumberTo", contactNumberTo);
             sendSmsParams.put("text", invoiceMsgToWeaver); 
             try {
            	 Map serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	             if (ServiceUtil.isError(serviceResult)) {
	            	 Debug.logError("Unable to Send SMS: " + ServiceUtil.getErrorMessage(result), module);
	             }
             } catch (Exception e) {
     			Debug.logError(e, module);
     			return ServiceUtil.returnError(e.toString());
     		}    
        }
        
        
        result = ServiceUtil.returnSuccess("Sales Invoice Has been successfully created"+invoiceId);
        result.put("invoiceId", invoiceId);
        return result;
	}
	
	
	public static Map<String, Object> createBranchSaleTransEntries(DispatchContext ctx, Map<String, ? extends Object> context) {
	    Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();

	    Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String sNo = (String) context.get("sNo");
		List<GenericValue> BranchSaleTransactionsList=FastList.newInstance();
	    try{
	        if(UtilValidate.isNotEmpty(sNo)){
		    	    BranchSaleTransactionsList = delegator.findList("BranchSaleTransactionInfo", EntityCondition.makeCondition("sNo", EntityOperator.EQUALS, sNo), null,null, null, false);

	        }else{
	    	    BranchSaleTransactionsList = delegator.findList("BranchSaleTransactionInfo", null, null,null, null, false);
	        }
	    List externalIndentNosList = EntityUtil.getFieldListFromEntityList(BranchSaleTransactionsList, "indentNo", true);
	    HttpServletRequest request = (HttpServletRequest) context.get("request");
	    HttpServletResponse response = (HttpServletResponse) context.get("response");
	    
	    if (BranchSaleTransactionsList != null) {
	      for (GenericValue BrachTrans: BranchSaleTransactionsList) {
	    	  String partyId="";
	    	    String billToCustomer ="";
	    	    Timestamp  effectiveDate = null;
	    	    String shipmentTypeId ="";
	    	    String salesChannel ="";
		         partyId =BrachTrans.getString("partyId");
				 billToCustomer = BrachTrans.getString("partyId");//using For Amul Sales
				Map resultMap = FastMap.newInstance();
				List invoices = FastList.newInstance(); 
				effectiveDate =  BrachTrans.getTimestamp("effectiveDate");
				shipmentTypeId = (String) BrachTrans.get("shipmentTypeId");
				salesChannel = (String)BrachTrans.getString("salesChannel");

				String remarks = (String)BrachTrans.get("remarks");
				String productId = (String) BrachTrans.getString("productId");
		        BigDecimal baleQuantity = (BigDecimal) BrachTrans.get("baleQuantity");
				String yarnUOM = (String) BrachTrans.get("yarnUOM");
		        BigDecimal bundleWeight = (BigDecimal) BrachTrans.get("bundleWeight");
		        BigDecimal quantity = (BigDecimal) BrachTrans.get("quantity");
		        BigDecimal unitPrice = (BigDecimal) BrachTrans.get("unitPrice");
				String suplierPartyId = (String) BrachTrans.get("suplierPartyId");
				String societyPartyId = (String) BrachTrans.get("societyPartyId");
				String productStoreId = (String) BrachTrans.get("productStoreId");
				String orderTaxType = (String) BrachTrans.getString("orderTaxType");
				String disableAcctgFlag = (String) BrachTrans.getString("disableAcctgFlag");
				String schemeCategory = (String) BrachTrans.get("schemeCategory");
		        String orderTypeId ="";
				if(UtilValidate.isNotEmpty(BrachTrans.get("orderTypeId"))){
					 orderTypeId = (String) BrachTrans.get("orderTypeId");
				}
				String billingType = (String) BrachTrans.get("billingType");
				String entryOrderId = (String) BrachTrans.getString("orderId");
		        String orderId ="";
				String subscriptionTypeId = "AM";
				String partyIdFrom = "";
				String shipmentId = "";
				String productSubscriptionTypeId="";
				if(UtilValidate.isEmpty(productSubscriptionTypeId)){
					productSubscriptionTypeId = "CASH";      	
				}
				Map processOrderContext = FastMap.newInstance();
				BigDecimal basicPrice = BigDecimal.ZERO;
				BigDecimal cstPrice = BigDecimal.ZERO;
				BigDecimal tcsPrice = BigDecimal.ZERO;
				BigDecimal vatPrice = BigDecimal.ZERO;
				BigDecimal bedPrice = BigDecimal.ZERO;
				BigDecimal serviceTaxPrice = BigDecimal.ZERO;
				//percentage fields
				String bedPercentStr = null;
				String vatPercentStr = null;
				String cstPercentStr = null;
				String tcsPercentStr = null;
				String serviceTaxPercentStr = null;
				
				BigDecimal bedPercent=BigDecimal.ZERO;
				BigDecimal vatPercent=BigDecimal.ZERO;
				BigDecimal cstPercent=BigDecimal.ZERO;
				BigDecimal tcsPercent=BigDecimal.ZERO;
				BigDecimal serviceTaxPercent=BigDecimal.ZERO;

				String daysToStore="";
				List indentProductList = FastList.newInstance();
				List productIds = FastList.newInstance();
				productIds.add(productId);
				Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
				productQtyMap.put("productId", productId);
				productQtyMap.put("quantity", quantity);
				productQtyMap.put("customerId", partyId);
				productQtyMap.put("remarks", remarks);
				productQtyMap.put("baleQuantity", baleQuantity);
				productQtyMap.put("bundleWeight", bundleWeight);
				productQtyMap.put("yarnUOM", yarnUOM);
				productQtyMap.put("batchNo", "");
				productQtyMap.put("daysToStore", daysToStore);
				productQtyMap.put("basicPrice", unitPrice);
				productQtyMap.put("bedPrice", bedPrice);
				productQtyMap.put("cstPrice", cstPrice);
				productQtyMap.put("tcsPrice", tcsPrice);
				productQtyMap.put("vatPrice", vatPrice);
				productQtyMap.put("serviceTaxPrice", serviceTaxPrice);

				productQtyMap.put("bedPercent", bedPercent);
				productQtyMap.put("vatPercent", vatPercent);
				productQtyMap.put("cstPercent", cstPercent);
				productQtyMap.put("tcsPercent", tcsPercent);
				productQtyMap.put("serviceTaxPercent", serviceTaxPercent);

				indentProductList.add(productQtyMap);
				List orderAdjChargesList = FastList.newInstance();
				Map<String, Object> result = ServiceUtil.returnSuccess();

				processOrderContext.put("userLogin", userLogin);
				processOrderContext.put("productQtyList", indentProductList);
				processOrderContext.put("partyId", partyId);
				processOrderContext.put("schemePartyId", "");
				processOrderContext.put("supplierPartyId", suplierPartyId);
				processOrderContext.put("billToCustomer", partyId);
				processOrderContext.put("productIds", productIds);
				processOrderContext.put("supplyDate", effectiveDate);
				processOrderContext.put("salesChannel", salesChannel);
				processOrderContext.put("orderTaxType", orderTaxType);
				processOrderContext.put("orderId", orderId);
				processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
				processOrderContext.put("productStoreId", productStoreId);
				processOrderContext.put("PONumber", "");
				processOrderContext.put("promotionAdjAmt", "");
				processOrderContext.put("orderMessage", "");
				processOrderContext.put("orderAdjChargesList", orderAdjChargesList);
				processOrderContext.put("disableAcctgFlag", disableAcctgFlag);
				processOrderContext.put("schemeCategory", schemeCategory);

				
				
				try{
						result = processBranchSalesOrder(ctx, processOrderContext);
				
						if(ServiceUtil.isError(result)){
							Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
							return ServiceUtil.returnError(null, null, null,result);
						}
				
						orderId = (String)result.get("orderId");
						if(UtilValidate.isNotEmpty(orderId)){
						
						String schemePartyId="";
						if(UtilValidate.isNotEmpty(billingType) && billingType.equals("onBehalfOf")){
							 Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId, "partyId", schemePartyId, "roleTypeId", "ON_BEHALF_OF");
							 try {
							 GenericValue value = delegator.makeValue("OrderRole", fields);
							 delegator.create(value);
							 } catch (GenericEntityException e) {
					  			  Debug.logError(e, "Could not roleback transaction for entity engine error occurred while fetching data", module);
		
							 }
						}
						
						if(UtilValidate.isNotEmpty(suplierPartyId)){
							try{
								GenericValue supplierOrderRole	=delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", suplierPartyId, "roleTypeId", "SUPPLIER"));
								delegator.createOrStore(supplierOrderRole);
							}catch (Exception e) {
								  Debug.logError(e, "Error While Creating OrderRole(SUPPLIER)  for  Sale Indent ", module);
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
									Debug.logError(e, "Could not add role to order for OnBeHalf  party " + orderId, module);
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
										Debug.logError(e, "Could not add role to order for SchemeCategory " + orderId, module);
								 }
						}
						Map resultCtx = FastMap.newInstance();
						
								resultCtx = dispatcher.runSync("createOrderHeaderSequence",UtilMisc.toMap("orderId", orderId ,"userLogin",userLogin, "orderHeaderSequenceTypeId","DEPOT_SALE_SEQUENCE"));
								if(ServiceUtil.isError(resultCtx)){
									Debug.logError("Problem while Creating  Sequence for orderId:"+orderId, module);
								}
					}
			}catch(Exception e){
						Debug.logError(e, module);
					}
				if(UtilValidate.isNotEmpty(orderId)){

			// create Payment
				
		 String amount = (BrachTrans.getBigDecimal("amount")).toString();
	        BigDecimal receivedAmount =  BrachTrans.getBigDecimal("amount");

	        Debug.log("======================bundleWeight=================="+amount);

	        String paymentTypeId = (String) BrachTrans.get("paymentTypeId");
	        Debug.log("======================quantity=================="+paymentTypeId);

	        Timestamp paymentDate = (Timestamp) BrachTrans.getTimestamp("paymentDate");
	        Timestamp depositDate = (Timestamp) BrachTrans.getTimestamp("depositDate");

	        Debug.log("======================paymentDate=================="+paymentDate);

			String inFavourOf = (String) BrachTrans.get("inFavourOf");
	        Debug.log("======================inFavourOf=================="+inFavourOf);
			String paymentRefNum = (String) BrachTrans.get("paymentRefNum");
			String issuingAuthority = (String) BrachTrans.get("issuingAuthority");
			String paymentDateStr=UtilDateTime.toDateString(paymentDate, "dd/MM/yyyy");
				Map paymentContext = FastMap.newInstance();
				paymentContext.put("partyId",partyId);
				paymentContext.put("paymentTypeId",paymentTypeId);
				paymentContext.put("paymentDate",paymentDateStr);
				paymentContext.put("amount",amount);
				paymentContext.put("balance","");
				paymentContext.put("inFavourOf",inFavourOf);
				paymentContext.put("paymentRefNum",paymentRefNum);
				paymentContext.put("issuingAuthority",issuingAuthority);
				paymentContext.put("orderId",orderId);
				paymentContext.put("grandTotal","");
				paymentContext.put("userLogin",userLogin);

				
				try{
					result = createOrderPayment(ctx, paymentContext);
				if(ServiceUtil.isError(result)){
					Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
				}
				}catch(Exception e){
					Debug.logError(e, module);
				}
				Debug.log("result================payment================"+result);
				List<GenericValue> orderPaymentPreferences = null;
                try {
                    orderPaymentPreferences = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId));
                } catch (GenericEntityException e) {
                    String errMsg = UtilProperties.getMessage(resource, "AccountingProblemGettingOrderPaymentPreferences", locale);
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
                String paymentId = null;
                String orderPaymentPreferenceId=null;
      			if (UtilValidate.isNotEmpty(orderPaymentPreferences)) {
	                GenericValue cardOrderPaymentPref = EntityUtil.getFirst(orderPaymentPreferences);
	                if (cardOrderPaymentPref != null) {
	                	orderPaymentPreferenceId = cardOrderPaymentPref.getString("orderPaymentPreferenceId");
	                }
	                if (orderPaymentPreferenceId != null) {
	                	try{
	                
		                	List condExpretionList=FastList.newInstance() ;
		                	condExpretionList.add(EntityCondition.makeCondition("orderPaymentPreferenceId", EntityOperator.EQUALS, orderPaymentPreferenceId));
		      			   	EntityCondition condExpretion = EntityCondition.makeCondition(condExpretionList, EntityOperator.AND);
		      			   	List<GenericValue> paymentList = delegator.findList("OrderPreferencePaymentApplication", condExpretion, null, null, null, false);
		
		      			   	//enericValue orderHeaderList = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", primaryOrderId), false);
		      			
		      			   	BigDecimal paidAmount = BigDecimal.ZERO;
		      			
			      			if (UtilValidate.isNotEmpty(paymentList)) {
			      				for (GenericValue eachPayment : paymentList) {
			      	      			if (UtilValidate.isNotEmpty(eachPayment) && UtilValidate.isNotEmpty(eachPayment.get("paymentId"))) {      				
			      		            	paymentId=(String)eachPayment.get("paymentId"); 
			      	      			}
			      				}
			      			}
	                	}catch (GenericEntityException e) {
	      				  Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
				             return ServiceUtil.returnError(" Error While Fetching PaymentId "+e);
	            	    }
	
	                }
      			}
				Debug.log("paymentId================payment================"+paymentId);
				if(UtilValidate.isNotEmpty(paymentId)){
						//payment deposit into bank
						Map paymentDepositContext = FastMap.newInstance();
						List<String> paymentIds = FastList.newInstance();
						paymentIds.add(paymentId);
						paymentDepositContext.put("organizationPartyId","COMPANY");
						paymentDepositContext.put("userLogin", userLogin);
				        String finAccountId = (String) BrachTrans.get("finAccountId");
				        if(UtilValidate.isEmpty(finAccountId)){
				        	paymentDepositContext.put("finAccountId","FIN_ACCNT1");
				        }else{
							paymentDepositContext.put("finAccountId",finAccountId);
				        }
						paymentDepositContext.put("paymentMethodTypeId","");
						paymentDepositContext.put("cardType","");
						paymentDepositContext.put("partyIdFrom","");
						paymentDepositContext.put("fromDate","");
						paymentDepositContext.put("thruDate","");
						paymentDepositContext.put("paymentGroupTypeId","BATCH_PAYMENT");
						if(UtilValidate.isNotEmpty(depositDate)){
							paymentDepositContext.put("transactionDate",depositDate);
						}else{
							paymentDepositContext.put("transactionDate",paymentDate);
						}
						paymentDepositContext.put("paymentIds",paymentIds);
						Map paymentDepositResult=FastMap.newInstance();;
						try{
							 paymentDepositResult= dispatcher.runSync("depositWithdrawPayments",paymentDepositContext);
					  		if (ServiceUtil.isError(paymentDepositResult)) {
					  	 		String errMsg =  ServiceUtil.getErrorMessage(paymentDepositResult);
					  	 		Debug.logError(errMsg , module);
								return ServiceUtil.returnError(null, null, null,paymentDepositResult);

					  		}	
				         }catch (GenericServiceException e) {
				        	 Debug.logError(e , module);
				             return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
				         }
						
						Debug.log("paymentDepositResult======================"+paymentDepositResult);
						String finAccountTransId=(String)paymentDepositResult.get("finAccountTransId");
						
						Debug.log("finAccountTransId======================"+finAccountTransId);
						if(UtilValidate.isNotEmpty(finAccountTransId)){
							//payment deposit into bank
							Map paymentReconsileContext = FastMap.newInstance();
							List<String> finAccountTransIds = FastList.newInstance();
							finAccountTransIds.add(finAccountTransId);
							paymentReconsileContext.put("organizationPartyId","Company");
							paymentReconsileContext.put("userLogin", userLogin);
							paymentReconsileContext.put("finAccountId","FIN_ACCNT10");
							paymentReconsileContext.put("bulkReconsileName","Create Reconcile");
							paymentReconsileContext.put("paymentGroupTypeId","BATCH_PAYMENT");
							paymentReconsileContext.put("statusId","FINACT_TRNS_APPROVED");
							paymentReconsileContext.put("finAccountTransIds",finAccountTransIds);
							Map paymentReconsileResult=FastMap.newInstance();;
							try{
								paymentReconsileResult= dispatcher.runSync("createReconsileAndUpdateFinAccountTrans",paymentReconsileContext);
						  		if (ServiceUtil.isError(paymentReconsileResult)) {
						  	 		String errMsg =  ServiceUtil.getErrorMessage(paymentReconsileResult);
						  	 		Debug.logError(errMsg , module);
									return ServiceUtil.returnError(null, null, null,paymentReconsileResult);

						  		}	
					         }catch (GenericServiceException e) {
					        	 Debug.logError(e , module);
					             return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
					         }
							
							Debug.log("paymentReconsileResult======================"+paymentReconsileResult);
						}
						
		            	
					}
				
				
				
				//generate PO
				String PoOrderId="";
				Map<String, Object> resultContatMap = FastMap.newInstance();
				Map<String, Object> input = FastMap.newInstance();
				Map<String, Object> outMap = FastMap.newInstance();

				String orderName ="";
				if(UtilValidate.isEmpty(effectiveDate)){
				 effectiveDate = UtilDateTime.nowTimestamp();
				}
				Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
				List<Map> itemDetail = FastList.newInstance();
				String orderDateStr="";
				String effectiveDateStr="";

				Timestamp orderDate = UtilDateTime.nowTimestamp();
				BigDecimal grandTotal =BigDecimal.ZERO;
				//Map resultMap = FastMap.newInstance();
				try {
					if(UtilValidate.isNotEmpty(orderId)){
						GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
						if (UtilValidate.isNotEmpty(orderHeader)) { 
							String statusId = orderHeader.getString("statusId");
							//orderTypeId=orderHeader.getString("orderTypeId");
							orderName=orderHeader.getString("orderName");
							estimatedDeliveryDate=orderHeader.getTimestamp("estimatedDeliveryDate");
							grandTotal=orderHeader.getBigDecimal("grandTotal");
							if(statusId.equals("ORDER_CANCELLED")){
								Debug.logError("Cannot create PurchaseOrder for cancelled orderId : "+orderId, module);
								
							}
						}
						
						List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

						Map orderItemSeq = FastMap.newInstance();
						for(GenericValue orderItemsValues : orderItems){
							 productId = orderItemsValues.getString("productId");
							 quantity=orderItemsValues.getBigDecimal("quantity");
							BigDecimal unitListPrice=BigDecimal.ZERO;
							 unitPrice=BigDecimal.ZERO;
							if(UtilValidate.isNotEmpty(orderItemsValues.getBigDecimal("unitListPrice"))){
								unitListPrice=orderItemsValues.getBigDecimal("unitListPrice");
							}
							if(UtilValidate.isNotEmpty(orderItemsValues.getBigDecimal("unitPrice"))){
								unitPrice=orderItemsValues.getBigDecimal("unitPrice");
							}
							Map tempMap=FastMap.newInstance();
							tempMap.put("productId",productId);
							tempMap.put("quantity",quantity);
							tempMap.put("unitPrice",unitPrice);
							tempMap.put("unitListPrice",unitListPrice);
							itemDetail.add(tempMap);
							String orderItemSeqId = orderItemsValues.getString("orderItemSeqId");
							orderItemSeq.put(productId, orderItemSeqId);
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
					
						Map processPOContext = FastMap.newInstance();

						//String productStoreId = (String) (in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
						//String productStoreId ="STORE";
						List termsList = FastList.newInstance();
						List otherTermDetail = FastList.newInstance();
						List adjustmentDetail = FastList.newInstance();

						processPOContext.put("userLogin", userLogin);
						processPOContext.put("productQtyList", itemDetail);
						processPOContext.put("orderTypeId","PURCHASE_ORDER");
						processPOContext.put("orderId", orderId);
						processPOContext.put("termsList", termsList);
						processPOContext.put("partyId",suplierPartyId );
						processPOContext.put("grandTotal", grandTotal);
						processPOContext.put("otherTerms", otherTermDetail);
						processPOContext.put("adjustmentDetail", adjustmentDetail);
						processPOContext.put("billFromPartyId", suplierPartyId);
						processPOContext.put("issueToDeptId", "");
						processPOContext.put("shipToPartyId", partyId);
						processPOContext.put("billFromVendorPartyId", "INT7");
						processPOContext.put("supplyDate", effectiveDate);
						processPOContext.put("salesChannel", "MATERIAL_PUR_CHANNEL");
						processPOContext.put("enableAdvancePaymentApp", Boolean.TRUE);
						processPOContext.put("productStoreId", productStoreId);
						processPOContext.put("PONumber", orderId);
						processPOContext.put("orderName", orderName);
						//processOrderContext.put("fileNo", fileNo);
						//processOrderContext.put("refNo", refNo);
						processPOContext.put("orderDate", orderDate);
						//processOrderContext.put("fromDate", (String)UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
						//processOrderContext.put("thruDate", (String)UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()));
						processPOContext.put("estimatedDeliveryDate", estimatedDeliveryDate);
						processPOContext.put("incTax", "Y");
						result = DepotPurchaseServices.CreateMaterialPO(ctx, processPOContext);
						if(ServiceUtil.isError(result)){
							Debug.logError("Unable to generate Purchase order: "+orderId+"=" + ServiceUtil.getErrorMessage(result), module);
							return ServiceUtil.returnError(null, null, null,result);
						}
						Debug.log("result======================"+result);
						PoOrderId=(String)result.get("orderId");;
					}
				}catch(GenericEntityException e){
					Debug.logError("Cannot create PurchaseOrder for cancelled orderId : "+orderId, module);
	     			return ServiceUtil.returnError(e.toString());

				}
				//making order Association with sales and Po
				if(UtilValidate.isNotEmpty(PoOrderId)){
						Map<String, Object> orderAssocMap = FastMap.newInstance();
						orderAssocMap.put("orderId", PoOrderId);
						orderAssocMap.put("toOrderId", orderId);
						orderAssocMap.put("userLogin", userLogin);
						result = DepotPurchaseServices.createOrderAssoc(ctx,orderAssocMap);
						if(ServiceUtil.isError(result)){
							Debug.logError("Unable do Order Assoc: " + ServiceUtil.getErrorMessage(result), module);
							return ServiceUtil.returnError(null, null, null,result);
						}
					 
//						Approve levels
						//						Approve1

						 Map<String, Object> serviceApprResult = null;
	                        try {
	                        	serviceApprResult = dispatcher.runSync("changeOrderStatus", UtilMisc.toMap("orderId", orderId, "statusId", "APPROVE_LEVEL1", "userLogin", userLogin));
	                        } catch (GenericServiceException e) {
	                            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
	                            return ServiceUtil.returnError(e.getMessage());
	                        }
	                        if (ServiceUtil.isError(serviceApprResult)) {
	                            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceApprResult));
	                        }
	                        //						Approve2
	                        try {
	                        	serviceApprResult = dispatcher.runSync("changeOrderStatus", UtilMisc.toMap("orderId", orderId, "statusId", "APPROVE_LEVEL2", "userLogin", userLogin));
	                        } catch (GenericServiceException e) {
	                            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
	                            return ServiceUtil.returnError(e.getMessage());
	                        }
	                        if (ServiceUtil.isError(serviceApprResult)) {
	                            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceApprResult));
	                        }
	                        //						Approve3
	                        try {
	                        	serviceApprResult = dispatcher.runSync("changeOrderStatus", UtilMisc.toMap("orderId", orderId, "statusId", "APPROVE_LEVEL3", "userLogin", userLogin));
	                        } catch (GenericServiceException e) {
	                            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
	                            return ServiceUtil.returnError(e.getMessage());
	                        }
	                        if (ServiceUtil.isError(serviceApprResult)) {
	                            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceApprResult));
	                        }
						
						
						
							
						if(grandTotal.compareTo(receivedAmount) < 0){					
				
							// credit APPROVE
							try{
									Map<String, Object> approvePoContext = FastMap.newInstance();
									approvePoContext.put("userLogin", userLogin);
									approvePoContext.put("orderId", orderId);
									approvePoContext.put("partyId", partyId);
									approvePoContext.put("salesChannelEnumId", "BRANCH_CHANNEL");
									approvePoContext.put("locale", locale);
									result = CreditapproveDepotOrder(ctx, approvePoContext);
									if(ServiceUtil.isError(result)){
										Debug.logError("Unable to do Credit Approve for Order " + ServiceUtil.getErrorMessage(result), module);
										return ServiceUtil.returnError(null, null, null,result);
									}
							}catch (Exception e) {
								Debug.logError(e, "Failed to approve purchase Order ", module);
				     			return ServiceUtil.returnError(e.toString());

							}
						}else{
							// approving Purchase Order
							
							try{
									Map<String, Object> approvePoContext = FastMap.newInstance();
									approvePoContext.put("userLogin", userLogin);
									approvePoContext.put("orderId", orderId);
									approvePoContext.put("partyId", partyId);
									approvePoContext.put("salesChannelEnumId", "BRANCH_CHANNEL");
									approvePoContext.put("locale", locale);
									result = approveDepotOrder(ctx, approvePoContext);
									if(ServiceUtil.isError(result)){
										Debug.logError("Unable to Approve Purchase Order " + ServiceUtil.getErrorMessage(result), module);
										return ServiceUtil.returnError(null, null, null,result);
									}
							}catch (Exception e) {
								Debug.logError(e, "Failed to approve purchase Order ", module);
				     			return ServiceUtil.returnError(e.toString());

							}
						}
						//generating shipment
						try{

								String vehicleId = (String) BrachTrans.get("vehicleId");
								String suppInvoiceId = (String) BrachTrans.get("suppInvoiceId");
								String lrNumber = (String) BrachTrans.get("lrNumber");
								String carrierName = (String) BrachTrans.get("carrierName");
								String deliveryChallanNo = (String) BrachTrans.get("deliveryChallanNo");
								Timestamp receiptDate = null;
								BigDecimal shippedQuantity = (BigDecimal) BrachTrans.get("shippedQuantity");
								receiptDate =  BrachTrans.getTimestamp("receiptDate");
								Timestamp  suppInvoiceDate =  BrachTrans.getTimestamp("suppInvoiceDate");
								String withoutPO="N";
								String receiptId="";
								String purposeTypeId="";
								String supplierId="";
								GenericValue shipmentReceipt=null;
								String smsContent="";
								if (UtilValidate.isEmpty(PoOrderId)) {
									Debug.logError("Cannot process receipts without orderId: "+ PoOrderId, module);
									return ServiceUtil.returnError(null, null, null,result);
								}
								Timestamp supplierInvoiceDate = null;
								SimpleDateFormat SimpleDF = new SimpleDateFormat("dd:mm:yyyy hh:mm");
								SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM, yyyy");
								receiptDate = UtilDateTime.nowTimestamp();
								Timestamp estimatedDate = UtilDateTime.addDaysToTimestamp(receiptDate,6);
								String estimatedDateStr=UtilDateTime.toDateString(estimatedDate,"dd-MM-yyyy");
								String receiptDateStr=UtilDateTime.toDateString(receiptDate,"dd-MM-yyyy");
								DateFormat givenFormatter = new SimpleDateFormat("dd:MM:yyyy hh:mm");
								DateFormat reqformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								if(UtilValidate.isNotEmpty(receiptDateStr)){
									try {
											Date givenReceiptDate = (Date)givenFormatter.parse(receiptDateStr);
											receiptDate = new java.sql.Timestamp(givenReceiptDate.getTime());
									}catch (ParseException e) {
										Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
						     			//return ServiceUtil.returnError(e.toString());

									} catch (NullPointerException e) {
										Debug.logError(e, "Cannot parse date string: " + receiptDateStr, module);
						     			//return ServiceUtil.returnError(e.toString());

									}
								}
								Timestamp deliveryChallanDate=UtilDateTime.nowTimestamp();
								Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
								BigDecimal poValue = BigDecimal.ZERO;
								try{
										List conditionList = FastList.newInstance();
										List<GenericValue> extPOItems = FastList.newInstance();
										List<GenericValue> extReciptItems = FastList.newInstance();
										boolean directPO = Boolean.TRUE;
										String extPOId = "";
										List productList = FastList.newInstance();
										if((UtilDateTime.getIntervalInDays(UtilDateTime.getDayStart(receiptDate), UtilDateTime.getDayStart(nowTimeStamp))) != 0){
											Debug.logError("Check local system date", module);
											TransactionUtil.rollback();
										}
										String originFacilityId ="";
										GenericValue orderHeader = null;
										BigDecimal landingCharges = BigDecimal.ZERO;
										if(UtilValidate.isNotEmpty(PoOrderId)){
											orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", PoOrderId), false);
											String statusId = orderHeader.getString("statusId");
											orderTypeId = orderHeader.getString("orderTypeId");
											purposeTypeId = orderHeader.getString("purposeTypeId");
											if(UtilValidate.isNotEmpty(orderHeader.getString("originFacilityId")))
											{
												originFacilityId = orderHeader.getString("originFacilityId");
											}
											if(statusId.equals("ORDER_CANCELLED")){
												Debug.logError("Cannot create GRN for cancelled orders : "+PoOrderId, module);
												TransactionUtil.rollback();
												return ServiceUtil.returnError("Cannot create GRN for cancelled orders : "+PoOrderId);
											}
											poValue = orderHeader.getBigDecimal("grandTotal");
											conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, PoOrderId));
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
											conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, PoOrderId));
											conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
											EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
											List<GenericValue> orderRole = delegator.findList("OrderRole", condition, null, null, null, false);
			  				
											if(UtilValidate.isEmpty(orderRole)){
												Debug.logError("No Vendor for the order : "+PoOrderId, module);
												TransactionUtil.rollback();
											}
			  					
											supplierId = (EntityUtil.getFirst(orderRole)).getString("partyId");
										}
										List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, PoOrderId), null, null, null, false);
										List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, PoOrderId), null, null, null, false);
										List changeExprList = FastList.newInstance();
										changeExprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, PoOrderId));
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
											productId = orderItemsValues.getString("productId");
											String orderItemSeqId = orderItemsValues.getString("orderItemSeqId");
											orderItemSeq.put(productId, orderItemSeqId);
										}
										
										List orderAdjustmentTypes = EntityUtil.getFieldListFromEntityList(orderAdjustments, "orderAdjustmentTypeId", true);
					
										productList = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
										GenericValue newEntity = delegator.makeValue("Shipment");
								        newEntity.set("estimatedShipDate", receiptDate);
								        if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("BRANCH_PURCHASE")){
									        newEntity.set("shipmentTypeId", "BRANCH_SHIPMENT");
								        }else{
								        	newEntity.set("shipmentTypeId", "DEPOT_SHIPMENT");
								        }
								        newEntity.set("statusId", "DISPATCHED");
								        newEntity.set("shipmentTypeId", "BRANCH_SHIPMENT");
								        newEntity.put("vehicleId",vehicleId);
								        newEntity.put("lrNumber",lrNumber);
								        newEntity.put("carrierName",carrierName);
								        newEntity.put("partyIdFrom",supplierId);
								        newEntity.put("partyIdTo",partyId);
								        newEntity.put("supplierInvoiceId",suppInvoiceId);
								        newEntity.put("supplierInvoiceDate",supplierInvoiceDate);
								        newEntity.put("deliveryChallanNumber",deliveryChallanNo);
								        newEntity.put("description",remarks);
								        newEntity.put("deliveryChallanDate",deliveryChallanDate);
								        newEntity.put("primaryOrderId",PoOrderId);
								        newEntity.set("createdDate", nowTimeStamp);
								        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
								        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
								        Debug.log("newEntity========================="+newEntity);
							            delegator.createSetNextSeqId(newEntity);            
							            shipmentId = (String) newEntity.get("shipmentId");
							            if(UtilValidate.isEmpty(shipmentId)){
							            	Debug.logError("Problem creating shipment for orderId :"+orderId, module);
							     			return ServiceUtil.returnError("Problem creating shipment for orderId :"+orderId);
							            }
							            Debug.log("shipmentId==================="+shipmentId);
							            if(UtilValidate.isNotEmpty(shipmentId)){
							            	//Debug.log("quantityStr======="+quantityStr);
							            	GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
							            	String	desc=product.getString("description");
							            	//Debug.log("desc============"+desc);
							            	Map<String,Object> itemInMap = FastMap.newInstance();
							            	itemInMap.put("shipmentId",shipmentId);
							            	itemInMap.put("userLogin",userLogin);
							            	itemInMap.put("productId",productId);
							            	itemInMap.put("quantity",shippedQuantity);
							            	resultMap = dispatcher.runSync("createShipmentItem",itemInMap);
							            	if (ServiceUtil.isError(resultMap)) {
							            		Debug.logError("Problem creating shipment Item for orderId :"+orderId, module);
							            		TransactionUtil.rollback();
							            	}
							            	String shipmentItemSeqId = (String)resultMap.get("shipmentItemSeqId");
							            	Debug.log("shipmentItemSeqId==========================="+shipmentItemSeqId);
							            	List<GenericValue> filteredOrderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, "00001"));
							            	GenericValue ordItm = null;
							            	String orderItemSeqId="";
							            	if(UtilValidate.isNotEmpty(filteredOrderItem)){
							            		ordItm = EntityUtil.getFirst(filteredOrderItem);
							            		orderItemSeqId = ordItm.getString("orderItemSeqId");
							            		List<GenericValue> itemChanges = EntityUtil.filterByCondition(orderItemChanges, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, "00001"));
							            		if(UtilValidate.isNotEmpty(itemChanges)){
							            			ordItm = EntityUtil.getFirst(itemChanges);
							            		}
							            	}
											Map inventoryReceiptCtx = FastMap.newInstance();
											
											inventoryReceiptCtx.put("userLogin", userLogin);
											inventoryReceiptCtx.put("productId", productId);
											inventoryReceiptCtx.put("datetimeReceived", receiptDate);
											inventoryReceiptCtx.put("quantityAccepted", shippedQuantity);
											inventoryReceiptCtx.put("quantityRejected", BigDecimal.ZERO);
											if(shippedQuantity.compareTo(BigDecimal.ZERO)>0){
												inventoryReceiptCtx.put("deliveryChallanQty",shippedQuantity);
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
												return ServiceUtil.returnError(null, null, null,receiveInventoryResult);

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
											
											Map shipmentStatusChange = FastMap.newInstance();
											
											shipmentStatusChange.put("shipmentId",shipmentId);
											shipmentStatusChange.put("statusId","DOCUMENTS_RECEIVED");
											shipmentStatusChange.put("userLogin",userLogin);
						
										    receiveInventoryResult = dispatcher.runSync("updateShipment", shipmentStatusChange);
											
											if (ServiceUtil.isError(receiveInventoryResult)) {
												Debug.logWarning("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
												TransactionUtil.rollback();
												return ServiceUtil.returnError(null, null, null,receiveInventoryResult);

								            }
						
											shipmentStatusChange.put("shipmentId",shipmentId);
											shipmentStatusChange.put("statusId","DOCUMENTS_ENDORSED");
											 receiveInventoryResult = dispatcher.runSync("updateShipment", shipmentStatusChange);
											
											if (ServiceUtil.isError(receiveInventoryResult)) {
												Debug.logWarning("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
												return ServiceUtil.returnError(null, null, null,receiveInventoryResult);

											}
											Map shipmentStatusAccept = FastMap.newInstance();
						
											shipmentStatusAccept.put("shipmentId",shipmentId);
											shipmentStatusAccept.put("statusIdTo","SR_QUALITYCHECK");
											shipmentStatusAccept.put("partyId",partyId);
											shipmentStatusAccept.put("userLogin",userLogin);
											 receiveInventoryResult = dispatcher.runSync("shipmentSendForQC", shipmentStatusAccept);
											
											if (ServiceUtil.isError(receiveInventoryResult)) {
												Debug.logWarning("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
												return ServiceUtil.returnError(null, null, null,receiveInventoryResult);

											}
											
											// raising sales invoice
											Map salesInvoiceMap = FastMap.newInstance();
						
											salesInvoiceMap.put("shipmentId",shipmentId);
											//salesInvoiceMap.put("partyId",supplierId);
											Debug.log("orderId==========================="+orderId);				
						
											salesInvoiceMap.put("orderId",orderId);
											salesInvoiceMap.put("userLogin",userLogin);
											salesInvoiceMap.put("billToPartyId",partyId);
											salesInvoiceMap.put("locale",locale);
											Map saleInoiceResult = dispatcher.runSync("createBranchSaleInvoice", salesInvoiceMap);
						
											if (ServiceUtil.isError(saleInoiceResult)) {
												Debug.logWarning("There was an error sales Invoice: " + ServiceUtil.getErrorMessage(saleInoiceResult), module);
												return ServiceUtil.returnError(null, null, null,saleInoiceResult);
				
											}
											Debug.log("saleInoiceResult==========================="+saleInoiceResult);				
												
										// raising purchase invoice
										Map purchaseInvoiceMap = FastMap.newInstance();
					
										purchaseInvoiceMap.put("shipmentId",shipmentId);
										//salesInvoiceMap.put("partyId",supplierId);
										Debug.log("PoOrderId==========================="+PoOrderId);				
					
										purchaseInvoiceMap.put("orderId",PoOrderId);
										purchaseInvoiceMap.put("userLogin",userLogin);
										purchaseInvoiceMap.put("billToPartyId",partyId);
										purchaseInvoiceMap.put("locale",locale);
										Map purInoiceResult = dispatcher.runSync("createBranchSaleInvoice", purchaseInvoiceMap);
					
										if (ServiceUtil.isError(purInoiceResult)) {
											Debug.logWarning("There was an error sales Invoice: " + ServiceUtil.getErrorMessage(purInoiceResult), module);
							            	return ServiceUtil.returnError(null, null, null,purInoiceResult);
			
							            }
										Debug.log("purInoiceResult======purchase====================="+purInoiceResult);	
											
							    
							}
			  		}catch (GenericServiceException e) {
		    	  		Debug.logError("Shipment status change error: for Order "+orderId, module);
		     			return ServiceUtil.returnError(e.toString());
		    	  	}
					
				}catch (Exception e) {
					Debug.logError(e, "error while generating Shipment for order"+orderId, module);
	     			return ServiceUtil.returnError(e.toString());
				}
				}
				
				}
	      }
	  }
	    }catch (GenericEntityException e) {
				  Debug.logError(e, "Could not commit transaction", module);
	     			return ServiceUtil.returnError("Could not commit transaction"+e.toString());

	    }
	    
	    
	 	        return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> getBranchCfcList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        Map<String, Object> result = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
       
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        String productStoreId = (String) context.get("productStoreId");
        
        // If productStoreId is not empty, fetch only courses related to the productStore
        List depotsList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(productStoreId)){
        	List condList =FastList.newInstance();
   	    	condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
   	    	condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "CFC"));
   	    	
   	    	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
   	    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimeStamp)));
   	    	EntityCondition prodStrFacCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
   	    	try{
   	    		depotsList = delegator.findList("FacilityAndProductStoreFacility", prodStrFacCondition, null, null, null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
   	    }
        String ownerPartyId = "";
        List branchCfcList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(depotsList)){
        	
        	// Get all CFC's
        	
        	List condList =FastList.newInstance();
        	
   	    	condList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, "CFC"));
   	    	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
   	    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimeStamp)));
   	    	List cfcList = FastList.newInstance();
   	    	try{
   	    		cfcList = delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
        	
   	    	Debug.log("cfcList ================"+cfcList);
   	    	
        	condList.clear();
        	
        	condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(depotsList, "facilityId", true)));
        	condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(cfcList, "productStoreId", true)));
   	    	condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "CFC"));
   	    	
   	    	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
   	    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimeStamp)));
   	    	try{
   	    		branchCfcList = delegator.findList("FacilityAndProductStoreFacility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
        	
        }
        Debug.log("branchCfcList ================"+branchCfcList);
        //result.put("ownerPartyId", ownerPartyId);
		result.put("cfcList", branchCfcList);
        return result;
    }
		 
	public static Map<String, Object> processPayShipmentReambursement(DispatchContext ctx, Map<String, ? extends Object> context){ 
				Map<String, Object> result = FastMap.newInstance();
				Delegator delegator = ctx.getDelegator();
				Locale locale = (Locale) context.get("locale");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				LocalDispatcher dispatcher = ctx.getDispatcher();
				String shipmentId = (String)context.get("shipmentId");
				BigDecimal invoiceEligablityAmount = BigDecimal.ZERO;
				BigDecimal receiptEligablityAmount = BigDecimal.ZERO;
				BigDecimal totalReceiptAmount = BigDecimal.ZERO;
				BigDecimal reimbursementEligibilityPercentage = new BigDecimal(2);
				
				try{
		        String reambursementString=(String)context.get("payReimbursementList");
		        JSONArray reambursementList =new JSONArray(reambursementString);
		        List<String> reambursementIds = FastList.newInstance();
		        for (int i = 0; i < reambursementList.length(); i++) {
		        	JSONObject reambursementObject =(JSONObject)reambursementList.get(i);
		        	reambursementIds.add((String)reambursementObject.getString("claimId"));
		        }
		        List conditionList = FastList.newInstance();
		        boolean flag=false;
		        if(UtilValidate.isNotEmpty(reambursementIds)){
					conditionList.add(EntityCondition.makeCondition("claimId",EntityOperator.NOT_IN,reambursementIds));
					flag=true;
		        }else if(UtilValidate.isEmpty(reambursementList) || reambursementList.length()==0){
		        	flag=true;
		        }
		         if(flag){
					conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,shipmentId));
					EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        	List<GenericValue> reambursementIdsList = delegator.findList("ShipmentReimbursement", condition, null, null, null, false);
		        	if(UtilValidate.isNotEmpty(reambursementIdsList)){
		        		delegator.removeAll(reambursementIdsList);
		        	}
		         }
		        for (int i = 0; i < reambursementList.length(); i++) {
		        	JSONObject reambursementObject =(JSONObject)reambursementList.get(i);
		        	String  claimId = (String)reambursementObject.getString("claimId");
		        	String receiptNo = (String)reambursementObject.getString("receiptNo");
		        	String description = (String)reambursementObject.getString("description");
		        	 SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		        	 Date date = format.parse((String)reambursementObject.getString("receiptDate"));
		        	 Timestamp receiptDate = new Timestamp(date.getTime());
		        	BigDecimal receiptAmount =new BigDecimal((String)reambursementObject.getString("receiptAmount"));
		        	totalReceiptAmount=totalReceiptAmount.add(receiptAmount);
		        	
		        	if(UtilValidate.isEmpty(claimId)){
		        		GenericValue newItemAttr = delegator.makeValue("ShipmentReimbursement");        	 
						newItemAttr.set("shipmentId", shipmentId);
						newItemAttr.set("receiptNo",receiptNo);
						newItemAttr.set("receiptAmount", receiptAmount);
						newItemAttr.set("receiptDate",receiptDate);
						newItemAttr.set("description", description);
						delegator.createSetNextSeqId(newItemAttr);
		        	}
		        }
		        receiptEligablityAmount=totalReceiptAmount;
		        conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,shipmentId));
				conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"PURCHASE_INVOICE"));
//				conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"INVOICE_PAID"));
				EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				List<GenericValue> invoiceAndItemList=delegator.findList("InvoiceAndItem",condition,null,null,null,false);
				BigDecimal invoiceAmount = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(invoiceAndItemList)){
					for(int i=0;i<invoiceAndItemList.size();i++){
						GenericValue invoiceAndItem=invoiceAndItemList.get(i);
						 BigDecimal  quantity=invoiceAndItem.getBigDecimal("quantity");
						 BigDecimal amount=invoiceAndItem.getBigDecimal("amount");
						 invoiceAmount=invoiceAmount.add(quantity.multiply(amount));
					}
					
				}
				invoiceEligablityAmount=(invoiceAmount.multiply(reimbursementEligibilityPercentage)).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
				BigDecimal finalEligablityAmount=receiptEligablityAmount.compareTo(invoiceEligablityAmount)>0?invoiceEligablityAmount:receiptEligablityAmount;
				GenericValue shipmentObj=delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
				shipmentObj.set("claimAmount",finalEligablityAmount);
				shipmentObj.set("claimStatus","APPLYED");
				if(finalEligablityAmount.compareTo(BigDecimal.ZERO)<=0){
					shipmentObj.set("claimStatus","");
				}
				shipmentObj.store();
				 }catch(Exception e){
					 Debug.logError("Order Entry successfully for party : "+e, module);
				 } 
				result=ServiceUtil.returnSuccess("Shipment reambursement Receipts added successfully !!");
			return result;
	 }

	public static Map<String, Object> processPayDepotReimbursement(DispatchContext ctx, Map<String, ? extends Object> context){ 
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String facilityId = (String)context.get("facilityId");
		String partyId = (String)context.get("partyId");
		String schemeTimePeriodId=(String)context.get("selectedTimePeriodId");
		BigDecimal invoiceEligablityAmount = BigDecimal.ZERO;
		BigDecimal receiptEligablityAmount = BigDecimal.ZERO;
		BigDecimal totalReceiptAmount = BigDecimal.ZERO;
		BigDecimal reimbursementEligibilityPercentage = new BigDecimal(2);
		
		try{
        String reambursementString=(String)context.get("payReimbursementList");
        JSONArray reambursementList =new JSONArray(reambursementString);
        List<String> reambursementIds = FastList.newInstance();

        for (int i = 0; i < reambursementList.length(); i++) {
        	JSONObject reambursementObject =(JSONObject)reambursementList.get(i);
        	reambursementIds.add((String)reambursementObject.getString("claimId"));
        }
        List conditionList = FastList.newInstance();
        boolean flag=false;
        if(UtilValidate.isNotEmpty(reambursementIds)){
			conditionList.add(EntityCondition.makeCondition("claimId",EntityOperator.NOT_IN,reambursementIds));
			flag=true;
        }else if(UtilValidate.isEmpty(reambursementList) || reambursementList.length()==0){
        	flag=true;
        }
         if(flag){
			conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
			conditionList.add(EntityCondition.makeCondition("schemeTimePeriodId",EntityOperator.EQUALS,schemeTimePeriodId));
			EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	List<GenericValue> reambursementIdsList = delegator.findList("DepotReimbursementReceipt", condition, null, null, null, false);
        	if(UtilValidate.isNotEmpty(reambursementIdsList)){
        		delegator.removeAll(reambursementIdsList);
        	}
         }
         SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < reambursementList.length(); i++) {
        	JSONObject reambursementObject =(JSONObject)reambursementList.get(i);
        	String  claimId = (String)reambursementObject.getString("claimId");
        	String description = (String)reambursementObject.getString("description");
        	String claimTypeId = (String)reambursementObject.getString("claimType");
        	BigDecimal receiptAmount =new BigDecimal((String)reambursementObject.getString("receiptAmount"));
        	totalReceiptAmount=totalReceiptAmount.add(receiptAmount);
        	if(UtilValidate.isEmpty(claimId)){
        		GenericValue newItemAttr = delegator.makeValue("DepotReimbursementReceipt");        	 
				newItemAttr.set("facilityId", facilityId);
				newItemAttr.set("partyId", partyId);
				newItemAttr.set("schemeTimePeriodId", schemeTimePeriodId);
				newItemAttr.set("receiptAmount", receiptAmount);
				Date fDate = format.parse((String)reambursementObject.getString("fromDate"));
				Date tDate = format.parse((String)reambursementObject.getString("thruDate"));
				newItemAttr.set("fromDate", new Timestamp(fDate.getTime()));
				newItemAttr.set("thruDate", new Timestamp(tDate.getTime()));
				newItemAttr.set("description", description);
				newItemAttr.set("claimType", claimTypeId);
				newItemAttr.set("statusId", "APPLYED");
				delegator.createSetNextSeqId(newItemAttr);
        	}
        }
        
		 }catch(Exception e){
			 Debug.logError("-----------error-------------- : "+e.toString(), module);
		 } 
		result=ServiceUtil.returnSuccess("Depot reambursement Receipts added successfully !!");
		return result;
	}	
	public static Map<String, Object> updateIndentSummaryPO(DispatchContext ctx, Map<String, ? extends Object> context){ 
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String salesOrderId = (String)context.get("SalesOrder");
		String purchaseOrderId = (String)context.get("PurchaseOrder");
	  	boolean beganTransaction = false;
		try{
			Map<String, Object> orderDtlMap = FastMap.newInstance();
			orderDtlMap.put("orderId", salesOrderId);
			orderDtlMap.put("userLogin", userLogin);
			result = dispatcher.runSync("getOrderItemSummary",orderDtlMap);
			if(ServiceUtil.isError(result)){
				Debug.logError("Unable get Order item: " + ServiceUtil.getErrorMessage(result), module);
				return ServiceUtil.returnError(null, null, null,result);
			}
			Map productSummaryMap = (Map)result.get("productSummaryMap");
			Iterator eachProductIter = productSummaryMap.entrySet().iterator();
			try{
				beganTransaction = TransactionUtil.begin(7200);
			
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, purchaseOrderId), null, null, null, false);
				BigDecimal totalBedAmount = BigDecimal.ZERO;
				BigDecimal totalBedCessAmount = BigDecimal.ZERO;
				BigDecimal totalBedSecCessAmount = BigDecimal.ZERO;
				BigDecimal totalVatAmount = BigDecimal.ZERO;
				BigDecimal totalCstAmount = BigDecimal.ZERO;
				
				GenericValue orderItemValue = null;
				while(eachProductIter.hasNext()) {
					Map.Entry entry = (Entry)eachProductIter.next();
					String productId = (String)entry.getKey();
					Map prodQtyMap=(Map)entry.getValue();
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
					
					
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
						productId = (String)prodQtyMap.get("productId");
					}*/
					
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
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
						unitPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
						orderItemValue.put("unitPrice", unitPrice);
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
						unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
						orderItemValue.put("unitListPrice", unitListPrice);
					}
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
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
*/
					orderItemValue.set("changeByUserLoginId", userLogin.getString("userLoginId"));
					orderItemValue.set("changeDatetime", UtilDateTime.nowTimestamp());
					orderItemValue.store();
				}
/*				
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
				}*/
				Map resetTotalCtx = UtilMisc.toMap("userLogin",userLogin);	  	
				resetTotalCtx.put("orderId", purchaseOrderId);
				resetTotalCtx.put("userLogin", userLogin);
				Map resetMap=FastMap.newInstance();
	  	 		try{
	  	 			resetMap = dispatcher.runSync("resetGrandTotal",resetTotalCtx);  		  		 
		  	 		if (ServiceUtil.isError(resetMap)) {
		  	 			String errMsg =  ServiceUtil.getErrorMessage(resetMap);
		  	 			Debug.logError(errMsg , module);
		  	 			return ServiceUtil.returnError(" Error While reseting order totals for Purchase Order !"+purchaseOrderId);
		  	 		}
	  	 		}catch (Exception e) {
	  	 			Debug.logError(e, " Error While reseting order totals for Purchase Order !"+purchaseOrderId, module);
	  	 			return resetMap;			  
	  	 		}
	  	 		

		        if(UtilValidate.isNotEmpty(purchaseOrderId)){
			  		
			  		result = DepotHelperServices.getOrderItemAndTermsMapForCalculation(ctx, UtilMisc.toMap("userLogin", userLogin, "orderId", purchaseOrderId));
					Debug.log("resultMap==============11======================"+result);

			  		if (ServiceUtil.isError(result)) {
		  		  		String errMsg =  ServiceUtil.getErrorMessage(result);
		  		  		Debug.logError(errMsg , module);
		  	 			return ServiceUtil.returnError(" Error While getting Order Adjestment Details !"+errMsg);
		  		  	}
					List<Map> otherCharges = (List)result.get("otherCharges");
					List<Map> productQty = (List)result.get("productQty");
					result = DepotHelperServices.getMaterialItemValuationDetails(ctx, UtilMisc.toMap("userLogin", userLogin, "productQty", productQty, "otherCharges", otherCharges, "incTax", ""));
					if(ServiceUtil.isError(result)){
		  		  		String errMsg =  ServiceUtil.getErrorMessage(result);
		  		  		Debug.logError(errMsg , module);
		  	 			return ServiceUtil.returnError(" Error While getting getMaterialItemValuationDetails !"+errMsg);
					}
					List<Map> itemDetails = (List)result.get("itemDetail");
									
					
					
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
		 }catch(Exception e){
			 Debug.logError("-----------error-------------- : "+e.toString(), module);
		 } 
		result=ServiceUtil.returnSuccess("Depot reambursement Receipts added successfully !!");
		return result;
	}	
	
	public static Map<String, Object> editPartyLooms(DispatchContext ctx, Map<String, ? extends Object> context){ 
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		String numberOfCattle = (String)context.get("numberOfCattle");
		String loomTypeId = (String)context.get("loomTypeId");
		
		Debug.log("partyId=========================="+partyId);
		Debug.log("numberOfCattle=========================="+numberOfCattle);
		Debug.log("loomTypeId=========================="+loomTypeId);
		
		try{
		
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		conditionList.add(EntityCondition.makeCondition("loomTypeId",EntityOperator.EQUALS,loomTypeId));
		EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		List<GenericValue> PartyLoomList = delegator.findList("PartyLoom", condition, null, null, null, false);
		  

		Timestamp nowTimestamp =UtilDateTime.nowTimestamp();
		
		if(UtilValidate.isNotEmpty(PartyLoomList)){
			GenericValue partyLoom = EntityUtil.getFirst(PartyLoomList);

			partyLoom.set("quantity", new BigDecimal(numberOfCattle));
			partyLoom.store();
		}else{
				
				GenericValue newPartyLoom = delegator.makeValue("PartyLoom");        	 
				newPartyLoom.set("partyId", partyId);
				newPartyLoom.set("loomTypeId", loomTypeId);
				newPartyLoom.set("fromDate", nowTimestamp);
				newPartyLoom.set("quantity", new BigDecimal(numberOfCattle));
				newPartyLoom.create();
				
			}
		}catch(Exception e) {
				// TODO: handle exception
	    		Debug.logError(e, module);
			}	
		
	return result;
}
 	public static Map<String, Object> getPartyQuotaAnalytics(DispatchContext dctx, Map<String, ? extends Object> context)  throws GenericEntityException  {
   		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Locale locale = null;
		TimeZone timeZone = null;
		locale = Locale.getDefault();
		timeZone = TimeZone.getDefault();
		List<String> partyIds  =null;
		Map partyMap = FastMap.newInstance();
	    String partyIdFrom=(String)context.get("partyId");
	    String categoryId=(String)context.get("categoryId");
	    String branchId=(String)context.get("branchId");
	    List conditionList = FastList.newInstance();
	    List<GenericValue> partyList = FastList.newInstance();

	           EntityListIterator entityListIte =null;
	       try {
		        
		        DynamicViewEntity dynamicView =new DynamicViewEntity();
		        dynamicView.addMemberEntity("PIF", "PartyIdentification");
                dynamicView.addMemberEntity("PRS", "PartyRelationship");
                dynamicView.addAliasAll("PIF", null, null);
                dynamicView.addAliasAll("PRS", null, null);
                dynamicView.addViewLink("PIF","PRS", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId","partyIdTo"));

                conditionList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS, branchId));
                conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom" ,EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
                conditionList.add(EntityCondition.makeCondition("roleTypeIdTo" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
               if(UtilValidate.isNotEmpty(partyIdFrom)){
           		conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdFrom));
       	        }
                
				EntityCondition condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				entityListIte = delegator.findListIteratorByCondition(dynamicView, condition1, null, null, null, null);
				partyList=entityListIte.getCompleteList();
				 partyIds =EntityUtil.getFieldListFromEntityList(partyList,"partyIdTo", true);
			}catch(Exception e){
				Debug.logError(e, module);
				Debug.logError(e, "Error in getting owner Party Id sList", module);	 		  		  
		  		return ServiceUtil.returnError("Error in owner Party Ids");
			}
		        finally{
		        	entityListIte.close();
				}

	    Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");
	    if(UtilValidate.isEmpty(effectiveDate)){
	    	effectiveDate = UtilDateTime.nowTimestamp();
	    }
	    int l=0;
	    for (String partyId : partyIds) {
	    	

		    conditionList.clear();
		    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		    conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,effectiveDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));

			
			List<GenericValue> partyLooms = null;
			try {
				partyLooms = delegator.findList("PartyLoom", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive SchemeParty ", module);
				return ServiceUtil.returnError("Failed to retrive SchemeParty " + e);
			}
		    // Scheme applicability for party can be based on partyId, PartyClassificationGroup or both.
		    
		    // Get All the schemes applicable for the party.
			List<GenericValue> partyApplicableSchemes = null;
			try {
				partyApplicableSchemes = delegator.findList("SchemeParty", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive SchemeParty ", module);
				return ServiceUtil.returnError("Failed to retrive SchemeParty " + e);
			}
		    List partySchemeIds = EntityUtil.getFieldListFromEntityList(partyApplicableSchemes, "schemeId", true);
			// Get All the schemes applicable for the PartyClassificationGroup.
			List<GenericValue> partyClassificationList = null;
			try {
				partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), UtilMisc.toSet("partyClassificationGroupId"), null, null,false);
			} catch (GenericEntityException e) {
				Debug.logError("Unable to get records from PartyClassificationGroup" + e,module);
				return ServiceUtil.returnError("Unable to get records from PartyClassificationGroup");
			}
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(partyClassificationList, "partyClassificationGroupId", true)));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
			
			List<GenericValue> groupApplicableSchemes = null;
			try {
				groupApplicableSchemes = delegator.findList("SchemePartyClassificationGroup", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive SchemeParty ", module);
				return ServiceUtil.returnError("Failed to retrive SchemeParty " + e);
			}
			List partyGrpSchemeIds = EntityUtil.getFieldListFromEntityList(groupApplicableSchemes, "schemeId", true);
			partySchemeIds.addAll(partyGrpSchemeIds);
			Set partySchemeIdsSet = new HashSet(partySchemeIds);
			partySchemeIds = new ArrayList(partySchemeIdsSet);
			
			Map schemesMap = FastMap.newInstance();
			
			Timestamp periodBegin = null;
			Timestamp periodEnd = null;
			
			try{
				 conditionList.clear();
				 conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"TEN_PERC_SCH_YEAR"));
				 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(effectiveDate.getTime())),EntityOperator.OR,EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,new java.sql.Date(effectiveDate.getTime()))));
				 conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(effectiveDate.getTime())));				  
				 EntityCondition schemeyearCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				 List<GenericValue> yearSchemeTimePeriodList = delegator.findList("SchemeTimePeriod", schemeyearCondition, null, null, null,false); 
				 
				if(UtilValidate.isNotEmpty(yearSchemeTimePeriodList) && yearSchemeTimePeriodList.size()>0){
					 GenericValue yearSchemeTimePeriod=yearSchemeTimePeriodList.get(0);
					 periodBegin=UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date)yearSchemeTimePeriod.get("fromDate")).getTime())) ;
					 periodEnd=UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date)yearSchemeTimePeriod.get("thruDate")).getTime())) ;
				 }else{
					 return ServiceUtil.returnError("There no active Year scheme Time Periods"); 
				 }
			}catch (Exception e) {
						Debug.logError(e, "Cannot parse date string: ", module);
					}
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			for(int i=0; i<partySchemeIds.size(); i++){
				String schemeId = (String) partySchemeIds.get(i);
				// Scheme applicability for products are based on product, productCategory or both.
				
				// Get Product Categories that are applicable for the scheme.
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("schemeId", EntityOperator.EQUALS, schemeId));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
						EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
				if(UtilValidate.isNotEmpty(categoryId)){
					conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId));
				}
				List<GenericValue> productCategoryApplicableSchemes = null;
				try {
					productCategoryApplicableSchemes = delegator.findList("SchemeProductCategory", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				} catch (GenericEntityException e) {
					Debug.logError(e, "Failed to retrive SchemeProduct ", module);
					return ServiceUtil.returnError("Failed to retrive SchemeProduct " + e);
				}
				// Temporarily doing this only for 10% Scheme. Keep updating as the requirement comes in
				
				Map productCategoryQuatasMap = FastMap.newInstance();
				List productCategoryQuatasList = FastList.newInstance();
				if(schemeId.equals("TEN_PERCENT_MGPS")){
					
					for(int j=0; j<productCategoryApplicableSchemes.size(); j++){
						GenericValue schemeProductCategory = productCategoryApplicableSchemes.get(j);
						String productCategoryId = schemeProductCategory.getString("productCategoryId");

						// Get relevant looms qty party possess and calculate quota
						List catPartyLooms = EntityUtil.filterByCondition(partyLooms, EntityCondition.makeCondition("loomTypeId", EntityOperator.EQUALS, productCategoryId));
						if(UtilValidate.isNotEmpty(catPartyLooms)){
							
							// Get productCategoryMembers
							List productIdsList = FastList.newInstance();
					
							conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
							conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
							conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
									EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
							try {
								List<GenericValue> prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionList,EntityOperator.AND), UtilMisc.toSet("productId"), null, null, true);
								productIdsList = EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productId", true);
		
							} catch (GenericEntityException e) {
								Debug.logError(e, "Failed to retrive ProductCategoryMember ", module);
								return ServiceUtil.returnError("Failed to retrive ProductCategoryMember " + e);
							}
							BigDecimal periodTime = BigDecimal.ONE;
							if( UtilValidate.isNotEmpty(schemeProductCategory.get("periodTime")) ){
								periodTime = (BigDecimal)schemeProductCategory.get("periodTime");
							}
							// calculate the quota already used for the month and reduce it from the actual quota.
							
							BigDecimal allocatedQuotaPerMonth = ((BigDecimal)schemeProductCategory.get("maxQty")).multiply( (BigDecimal)(((GenericValue)catPartyLooms.get(0)).get("quantity")));
							BigDecimal allocatedQuotaAdvances = periodTime.multiply(allocatedQuotaPerMonth);
							BigDecimal availableQuota = BigDecimal.ZERO;
							// Two months advance can be taken at any time. I am trying to iterate through each month, and see if he has taken any advances. With this I will get the current month's quota.
							
							BigDecimal outstandingQuotaAvailable = BigDecimal.ZERO;
							BigDecimal quotaAdvanceUsed = BigDecimal.ZERO;
							Timestamp monthIterStartDate = null;
							Timestamp monthIterEndDate = null;
							if(periodTime.compareTo(BigDecimal.ONE)>0){
								Timestamp periodMonthStart = periodBegin;
								Timestamp periodMonthEnd = null;
								
								Calendar startCalendar=Calendar.getInstance();
								startCalendar.setTime(UtilDateTime.toSqlDate(periodBegin));
					    		Calendar endCalendar=Calendar.getInstance();
					    		endCalendar.setTime(UtilDateTime.toSqlDate(effectiveDate));
								
					    		int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
					    		int diffMonth = (diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH))+1;
								
					    		//Check Quota Advances taken for current month
					    		
								for(int k=0; k<diffMonth; k++){
									monthIterStartDate = UtilDateTime.getMonthStart(periodMonthStart);
									monthIterEndDate = UtilDateTime.getMonthEnd(periodMonthStart, timeZone, locale);
									if((k+1)==diffMonth){
										monthIterEndDate=effectiveDate;
									}
							
									BigDecimal totalQuotaUsedUp = BigDecimal.ZERO;
									try {
										Map<String, Object> usedQuotaResultMap=dispatcher.runSync("getUsedQuotaFromOrders", UtilMisc.toMap("partyId",partyId,"fromDate",monthIterStartDate,"thruDate",monthIterEndDate,"productIds",productIdsList,"userLogin", userLogin));  
										totalQuotaUsedUp =(BigDecimal)usedQuotaResultMap.get("usedQuota");
									} catch (Exception e) {
											Debug.logError(e, "Failed to get getSchemeTimePeriodId ", module);
											return ServiceUtil.returnError("Failed to get getSchemeTimePeriodId " + e);
										}
									totalQuotaUsedUp = totalQuotaUsedUp.add(quotaAdvanceUsed);
									
									
									if((allocatedQuotaPerMonth).compareTo(totalQuotaUsedUp)>0){
										quotaAdvanceUsed = BigDecimal.ZERO;
										outstandingQuotaAvailable = allocatedQuotaPerMonth.subtract(totalQuotaUsedUp);
									}
									else{
										quotaAdvanceUsed = totalQuotaUsedUp.subtract(allocatedQuotaPerMonth);
										outstandingQuotaAvailable =quotaAdvanceUsed;
									}
									
									periodMonthStart = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(monthIterEndDate, 1));
									
								}

							}
//							// Current month quota operations
//							if(quotaAdvanceUsed.compareTo(allocatedQuotaPerMonth)>=0){
//								outstandingQuotaAvailable = BigDecimal.ZERO;
//							}
							else{
								
								monthIterStartDate = UtilDateTime.getMonthStart(effectiveDate);
								monthIterEndDate =effectiveDate;
								
								BigDecimal totalQuotaUsedUp = BigDecimal.ZERO;
								try {
									Map<String, Object> usedQuotaResultMap=dispatcher.runSync("getUsedQuotaFromOrders", UtilMisc.toMap("partyId",partyId,"fromDate",monthIterStartDate,"thruDate",monthIterEndDate,"productIds",productIdsList,"userLogin", userLogin));  
									totalQuotaUsedUp =(BigDecimal)usedQuotaResultMap.get("usedQuota");
								} catch (Exception e) {
										Debug.logError(e, "Failed to get getSchemeTimePeriodId ", module);
										return ServiceUtil.returnError("Failed to get getSchemeTimePeriodId " + e);
									}
					
								outstandingQuotaAvailable = (allocatedQuotaAdvances.subtract(totalQuotaUsedUp)).subtract(quotaAdvanceUsed);
								
							}
							outstandingQuotaAvailable=outstandingQuotaAvailable.setScale(0, BigDecimal.ROUND_DOWN);
							Map productCategoryQuotaMap = FastMap.newInstance();
							productCategoryQuotaMap.put("productCategoryId", productCategoryId);
							productCategoryQuotaMap.put("quotaPerMonth", allocatedQuotaPerMonth);
							productCategoryQuotaMap.put("quotaAvailableThisMonth", allocatedQuotaAdvances);
							String quotaAvailable =outstandingQuotaAvailable.toPlainString();
							
							productCategoryQuotaMap.put("availableQuota", quotaAvailable);
							productCategoryQuotaMap.put("categoryQuota", (BigDecimal)schemeProductCategory.get("maxQty"));
							productCategoryQuotaMap.put("looms",  (BigDecimal)((GenericValue)catPartyLooms.get(0)).get("quantity"));
							
							// Add logic to calculate quota already used for the period
							Map tempCatMap = FastMap.newInstance();
							tempCatMap.putAll(productCategoryQuotaMap);
							
							productCategoryQuatasMap.put(productCategoryId, tempCatMap);
						}
					}
				}
				Map tempSchemeMap = FastMap.newInstance();
				tempSchemeMap.putAll(productCategoryQuatasMap);
				
				schemesMap.put(schemeId, tempSchemeMap);
			    
			}
			partyMap.put(partyId, schemesMap);
			
//			if(l==10){
//				break;
//			}
	    	l++;
	    }

		result.put("quotaMap",partyMap);

	    return result;
   
   	}
 	
 	public static Map<String, Object> getUsedQuotaFromOrders(DispatchContext dctx, Map<String, ? extends Object> context){
   		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
 		Map<String, Object> result = ServiceUtil.returnSuccess();
 		String partyId= (String)context.get("partyId");
 		Timestamp monthIterStartDate = (Timestamp)context.get("fromDate");
 		Timestamp monthIterEndDate = (Timestamp)context.get("thruDate");
 		List productIds=(List)context.get("productIds");
 		List conditionList = FastList.newInstance();
 		if(UtilValidate.isEmpty(productIds)){
 			
 			String productCategoryId= (String)context.get("productCategoryId");
			conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, monthIterStartDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthIterEndDate)));
			try {
				List<GenericValue> prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionList,EntityOperator.AND), UtilMisc.toSet("productId"), null, null, true);
				productIds = EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productId", true);
	
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive ProductCategoryMember ", module);
				return ServiceUtil.returnError("Failed to retrive ProductCategoryMember " + e);
			}
 		}
 		
 		conditionList.clear(); 
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("BILL_TO_CUSTOMER", "ON_BEHALF_OF")));
		conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthIterStartDate));
		conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, monthIterEndDate));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
		
		List<GenericValue> orderHeaderAndRoles = null;
		try {
			
			orderHeaderAndRoles = delegator.findList("OrderHeaderAndRoles", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive OrderHeader ", module);
			return ServiceUtil.returnError("Failed to retrive OrderHeader " + e);
		}
		
		List orderIds = EntityUtil.getFieldListFromEntityList(orderHeaderAndRoles,"orderId", true);
		BigDecimal totalQuotaUsedUp = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(orderIds)){
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
			conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "quotaQty"));
			List<GenericValue> orderItemAndAdjustment = null;
			try {
				orderItemAndAdjustment = delegator.findList("OrderItemAndOrderItemAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive OrderHeader ", module);
				return ServiceUtil.returnError("Failed to retrive OrderHeader " + e);
			}
	
			for(int a=0; a<orderItemAndAdjustment.size(); a++){
				
				totalQuotaUsedUp = totalQuotaUsedUp.add(new BigDecimal((String)((GenericValue)orderItemAndAdjustment.get(a)).get("attrValue")));
			}
		}
		result.put("usedQuota",totalQuotaUsedUp);
		return result;
 		
 	}
	
}
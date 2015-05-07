package in.vasista.vbiz.depotsales;

import java.math.BigDecimal;
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

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

public class DepotSalesServices{

   public static final String module = DepotSalesServices.class.getName();

   public static Map<String, Object> approveDepotOrder(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String salesChannelEnumId = (String) context.get("salesChannelEnumId");
		String partyId=(String) context.get("partyId");
		String orderId = (String) context.get("orderId");
		//Debug.log("====Before Approving Depot Order==============partyId===>"+partyId);
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
					 if (arPartyOB.compareTo(orderTotal) < 0) {
						 Debug.logError("Available Balance:"+arPartyOB+" Less Than The Order Amount:"+orderTotal+" For Party:"+ partyId, module);
						 return ServiceUtil.returnError("Available Balance:"+arPartyOB+" Less Than The Order Amount:"+orderTotal+" For Party:"+ partyId);
					 }
			 }
            boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
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
			cart.setOrderId(orderId);
			Debug.log("====orderId==="+orderId);
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
       ProductPromoWorker.doPromotions(cart, dispatcher);
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
		Debug.log("==orderId===After==Creation==="+orderId);
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
		//Debug.log("=====orderAdjChargesList="+orderAdjChargesList);
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
	    	}
	    }
	    catch(Exception e){
	    	 Debug.logError(e, "Problem Creating the Return Item for party " + fromPartyId, module);		  
			  return ServiceUtil.returnError("Problem Creating the Return Item for party " + fromPartyId);			
	    }

	    return result;
   }
   
   
   
   
   
   
}
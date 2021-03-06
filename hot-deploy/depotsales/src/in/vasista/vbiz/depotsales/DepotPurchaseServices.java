package in.vasista.vbiz.depotsales;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONArray;

import net.sf.json.JSONObject;

import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.order.order.OrderReadHelper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericDelegator;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.json.JSONArray;
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
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.conversion.JSONConverters.JSONToList; 

import net.sf.json.JSONSerializer; 

import java.util.Iterator;
import java.util.Collection;


public class DepotPurchaseServices{

   public static final String module = DepotPurchaseServices.class.getName();
   
   private static int decimals;
   private static int rounding;
    public static final String resource = "AccountingUiLabels";
    
    static {
        decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        //if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
      
	public static String processDepotPurchaseInvoice(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		String purposeTypeIdFromDc = (String) request.getParameter("purposeTypeId");
		
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String vehicleId = (String) request.getParameter("vehicleId");
		String tallyrefNo = (String) request.getParameter("tallyrefNo");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purposeTypeId = "YARN_SALE";
		
		String purchaseTitleTransferEnumId = (String) request.getParameter("purchaseTitleTransferEnumId");
		String purchaseTaxType = (String) request.getParameter("purchaseTaxType");
		
		String purTaxListStr = null;
		
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
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		List<GenericValue> orderItems=null;
		try{
		 orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
/*		
		if(UtilValidate.isNotEmpty(tallyrefNo)){
				  List conditionList = FastList.newInstance();
		
				  conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
				  conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS,"BackToBackOrder"));
				  EntityCondition assoc = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				  try{
				 // List  OrderAssocList = delegator.findList("OrderAssoc", assoc, null, null, null,false); 
				  
				   //if(UtilValidate.isNotEmpty(OrderAssocList)){
		
				 // GenericValue OrderAssoc = EntityUtil.getFirst(OrderAssocList);
				  //String actualOrderId = (String)OrderAssoc.get("toOrderId");
				  
				  GenericValue	OrderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId", orderId), false);
		
				  OrderHeader.set("tallyRefNo",tallyrefNo);
				  OrderHeader.store();
				// }
				
			}catch (Exception e) {
				Debug.logError(e, "Problems While Updating Tally Ref No: " + tallyrefNo, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems While Updating Tally Ref No: " + tallyrefNo);
				return "error";
			}
		}	
		*/
		
		String orderAdjustmentsListStr = null;
		String discOrderAdjustmentsListStr = null;
		
		List productQtyList = FastList.newInstance();
		List invoiceAdjChargesList = FastList.newInstance();
		List invoiceDiscountsList = FastList.newInstance();
		List<String> orderItemSeqIdList = FastList.newInstance();
		
		String applicableTo = "ALL";
		String applicableToDisc = "ALL";
		for (int i = 0; i < rowCount; i++) {
			
			List purTaxRateList = FastList.newInstance();
			Map prodQtyMap = FastMap.newInstance();
			List orderAdjustmentList = FastList.newInstance();
			List discOrderAdjustmentList = FastList.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			String assessableValue = "";
			String discAssessableValue = "";
			String invoiceItemDiscTypeId = "";
			String orderItemSeqId = "";
			String adjAmtDiscStr = "";
			String discQtyStr = "";
			BigDecimal adjDiscAmt = BigDecimal.ZERO;
			BigDecimal discQty = BigDecimal.ZERO;
			
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
			
			
			
			if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
				orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
				
				
				String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							adjTypeMap.put("amount",new BigDecimal(taxAmt));
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}
						}
					}
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					orderAdjustmentList.add(tempAdjMap);
				}
			}
			//Debug.log("assessableAdjustmentAmount ================="+assessableAdjustmentAmount);
			
			if (paramMap.containsKey("discOrderAdjustmentsList" + thisSuffix)) {
				discOrderAdjustmentsListStr = (String) paramMap.get("discOrderAdjustmentsList"+ thisSuffix);
				
				String[] orderAdjustmentsList = discOrderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							BigDecimal taxAmtBd = new BigDecimal(taxAmt);
							if(taxAmtBd.compareTo(BigDecimal.ZERO)>0){
								taxAmtBd = taxAmtBd.negate();
							}
							adjTypeMap.put("amount",taxAmtBd);
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}
						}
					}
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					discOrderAdjustmentList.add(tempAdjMap);
				}
			}
			//Debug.log("assessableAdjustmentAmount ================="+assessableAdjustmentAmount);
			
			
			
			
			/*if (paramMap.containsKey("invoiceItemTypeId" + thisSuffix)) {
				invoiceItemTypeId = (String) paramMap.get("invoiceItemTypeId" + thisSuffix);
			}

			if (paramMap.containsKey("applicableTo" + thisSuffix)) {
				applicableTo = (String) paramMap.get("applicableTo" + thisSuffix);
			}

			if (paramMap.containsKey("adjAmt" + thisSuffix)) {
				adjAmtStr = (String) paramMap.get("adjAmt" + thisSuffix);
			}
			if (paramMap.containsKey("assessableValue" + thisSuffix)) {
				assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
			}
			Debug.log("assessableValue ============="+assessableValue);
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
				invItemMap.put("adjustmentTypeId", invoiceItemTypeId);
				invItemMap.put("amount", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
				if(UtilValidate.isNotEmpty(assessableValue) && (assessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				Debug.log("invItemMap ============="+invItemMap);
				
				Map tempInvItemMap = FastMap.newInstance();
				tempInvItemMap.putAll(invItemMap);
				
				
				invoiceAdjChargesList.add(tempInvItemMap);	
				Debug.log("invoiceAdjChargesList ============="+invoiceAdjChargesList);
			}
			
			if (paramMap.containsKey("invoiceItemTypeDiscId" + thisSuffix)) {
				invoiceItemDiscTypeId = (String) paramMap.get("invoiceItemTypeDiscId" + thisSuffix);
			}

			if (paramMap.containsKey("applicableToDisc" + thisSuffix)) {
				applicableToDisc = (String) paramMap.get("applicableToDisc" + thisSuffix);
			}

			if (paramMap.containsKey("adjDiscAmt" + thisSuffix)) {
				adjAmtDiscStr = (String) paramMap.get("adjDiscAmt" + thisSuffix);
			}
			
			if (paramMap.containsKey("discQty" + thisSuffix)) {
				discQtyStr = (String) paramMap.get("discQty" + thisSuffix);
			}
			if (paramMap.containsKey("assessableValueDisc" + thisSuffix)) {
				discAssessableValue = (String) paramMap.get("assessableValueDisc" + thisSuffix);
			}
			Debug.log("discAssessableValue ============="+discAssessableValue);
			if(UtilValidate.isNotEmpty(adjAmtDiscStr)){
				try {
					adjDiscAmt = new BigDecimal(adjAmtDiscStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(discQtyStr)){
				try {
					discQty = new BigDecimal(discQtyStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + discQtyStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + discQtyStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(invoiceItemDiscTypeId) && adjDiscAmt.compareTo(BigDecimal.ZERO)>0){
				
				BigDecimal adjQty = BigDecimal.ONE;
				if(UtilValidate.isNotEmpty(discQty)){
					if(discQty.doubleValue()>0){
						//adjDiscAmt = adjDiscAmt.divide(discQty);
						double tempAdjAmt = adjDiscAmt.doubleValue()/discQty.doubleValue();
						adjDiscAmt = new BigDecimal(tempAdjAmt);
						adjQty = discQty;
					}
				}
				
				Map invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemDiscTypeId);
				invItemMap.put("amount", adjDiscAmt);
				invItemMap.put("quantity", adjQty);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableToDisc);
				if(UtilValidate.isNotEmpty(discAssessableValue) && (discAssessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				Debug.log("invItemMap ============="+invItemMap);
				Map tempInvItemMap = FastMap.newInstance();
				tempInvItemMap.putAll(invItemMap);
				
				invoiceDiscountsList.add(tempInvItemMap);	
				Debug.log("invoiceDiscountsList ============="+invoiceDiscountsList);
			}*/
			
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
				/*if (paramMap.containsKey("oritemseq" + thisSuffix)) {
					orderItemSeqId = (String) paramMap.get("oritemseq" + thisSuffix);
					
					orderItemSeqIdList.add(orderItemSeqId);
				}*/
				
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
				
				// Purchase tax list
				
				if (paramMap.containsKey("purTaxList" + thisSuffix)) {
					purTaxListStr = (String) paramMap.get("purTaxList"
							+ thisSuffix);
					
					String[] taxList = purTaxListStr.split(",");
					for (int j = 0; j < taxList.length; j++) {
						String taxType = taxList[j];
						Map taxRateMap = FastMap.newInstance();
						String purTaxType = taxType.replace("_SALE", "_PUR");
						taxRateMap.put("orderAdjustmentTypeId",purTaxType);
						taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
						taxRateMap.put("amount",BigDecimal.ZERO);
						//taxRateMap.put("taxAuthGeoId", partyGeoId);
						
						if (paramMap.containsKey(taxType + "_PUR" + thisSuffix)) {
							String taxPercentage = (String) paramMap.get(taxType + "_PUR" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxPercentage) && !(taxPercentage.equals("NaN"))){
								taxRateMap.put("sourcePercentage",new BigDecimal(taxPercentage));
							}
							
						}
						if (paramMap.containsKey(taxType+ "_PUR_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(taxType+ "_PUR_AMT" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								taxRateMap.put("amount",new BigDecimal(taxAmt));
							}
						}
						
						Map tempTaxMap = FastMap.newInstance();
						tempTaxMap.putAll(taxRateMap);
						
						purTaxRateList.add(tempTaxMap);
					}
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
			GenericValue orderItemValue = null;

			if(UtilValidate.isNotEmpty(productId)){
				/*try{
					
					List itemAssocCond1 = FastList.newInstance();
					itemAssocCond1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					itemAssocCond1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
					itemAssocCond1.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					
					List<GenericValue> orderItem = delegator.findList("OrderItem", EntityCondition.makeCondition(itemAssocCond1, EntityOperator.AND), null, UtilMisc.toList("orderItemSeqId"), null, false);
					if(UtilValidate.isNotEmpty(orderItem)){
						orderItemValue = EntityUtil.getFirst(orderItem);
						orderItemValue.put("unitPrice", uPrice);
						orderItemValue.put("unitListPrice", uPrice);
						orderItemValue.store();
					}else{
						continue;
					}
				}catch (Exception e) {
					  Debug.logError(e, "Error While Updating purchase Order ", module);
					  request.setAttribute("_ERROR_MESSAGE_", "Unable to update Purchase Order :" + orderId+"....! ");
						return "error";
				}*/
				prodQtyMap.put("productId", productId);
				prodQtyMap.put("quantity", quantity);
				prodQtyMap.put("unitPrice", uPrice);
				prodQtyMap.put("vatAmount", vat);
				prodQtyMap.put("cstAmount", cst);
				prodQtyMap.put("vatPercent", vatPercent);
				prodQtyMap.put("cstPercent", cstPercent);
				prodQtyMap.put("bedPercent", BigDecimal.ZERO);
				prodQtyMap.put("purTaxRateList", purTaxRateList);
				prodQtyMap.put("orderAdjustmentList", orderAdjustmentList);
				prodQtyMap.put("discOrderAdjustmentList", discOrderAdjustmentList);
				productQtyList.add(prodQtyMap);
			}
		}//end row count for loop
		
		if( UtilValidate.isEmpty(productQtyList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		
		// Get Purpose type based on product
		
		String invProdId = (String) ((Map) productQtyList.get(0)).get("productId");

		/*try{
	  		Map resultCtx = dispatcher.runSync("getPurposeTypeForProduct", UtilMisc.toMap("productId", invProdId, "userLogin", userLogin));  	
	  		purposeTypeId = (String)resultCtx.get("purposeTypeId");
	  	}catch (GenericServiceException e) {
	  		Debug.logError("Unable to analyse purpose type: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to analyse purpose type :"+ServiceUtil.getErrorMessage(result));
			return "error";
	  	}*/
		
		Map processInvoiceContext = FastMap.newInstance();
		processInvoiceContext.put("userLogin", userLogin);
		processInvoiceContext.put("productQtyList", productQtyList);
		processInvoiceContext.put("partyId", partyId);
		processInvoiceContext.put("tallyrefNo", tallyrefNo);
		processInvoiceContext.put("purposeTypeId", purposeTypeId);
		processInvoiceContext.put("vehicleId", vehicleId);
		processInvoiceContext.put("orderId", orderId);
		processInvoiceContext.put("shipmentId", shipmentId);
		processInvoiceContext.put("invoiceDate", invoiceDate);
		processInvoiceContext.put("purchaseTitleTransferEnumId", purchaseTitleTransferEnumId);
		processInvoiceContext.put("purchaseTaxType", purchaseTaxType);
		processInvoiceContext.put("purposeTypeIdFromDc", purposeTypeIdFromDc);
		
		//processInvoiceContext.put("saleTitleTransferEnumId", saleTitleTransferEnumId);
		//processInvoiceContext.put("invoiceAdjChargesList", invoiceAdjChargesList);
		//processInvoiceContext.put("invoiceDiscountsList", invoiceDiscountsList);
		
		if(UtilValidate.isNotEmpty(isDisableAcctg)){
			processInvoiceContext.put("isDisableAcctg", isDisableAcctg);
		}
		result = createDepotInvoice(dctx, processInvoiceContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate invoice  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";
		}
		
		String invoiceId =  (String)result.get("invoiceId");
		/*if(UtilValidate.isNotEmpty(invoiceId)){
			GenericValue Shipment = null;
			GenericValue invoice = null;
	        try{
	        	 Shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
	             String supplierInvoiceId = Shipment.getString("supplierInvoiceId");
	 			 Timestamp supplierInvoiceDate = (Timestamp)Shipment.get("supplierInvoiceDate");
	 			 invoice = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId", invoiceId), false); 
	             invoice.set("millInvoiceNo", supplierInvoiceId);
	             invoice.set("millInvoiceDate",supplierInvoiceDate);
	             try {
	                 delegator.createOrStore(invoice);
	             } catch (GenericEntityException e) {
	                 Debug.logError(e, module);
	                 request.setAttribute("_ERROR_MESSAGE_", "Error while storing referenceNumber and referenceDate in Invoice");
	                 return "error";
	             }
	         }catch(Exception e){
	             Debug.logError("failed to store storing referenceNumber and referenceDate::"+invoiceId, module);
	              request.setAttribute("_ERROR_MESSAGE_", "failed to store referenceNumber and referenceDate::"+invoiceId);
	              return "error";
	         }
		}*/
		
		
		
		//============================================Rounding Off===============================
		  List<GenericValue> InvoiceItem = null;
	    	
		    List conditionList = FastList.newInstance();
	    	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
	    	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
	    	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
	    	 try{
	    	   InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	    	 
	    	 }catch(GenericEntityException e){
					Debug.logError(e, "Failed to retrive InvoiceItem ", module);
				}
		    
		    
	    	 if(UtilValidate.isNotEmpty(InvoiceItem)){
	        	 
	    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
	    		 
	        	for(GenericValue eachInvoiceItem : InvoiceItem){
	        	
	        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
	        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
	        		BigDecimal itemValue = quantity.multiply(amount);
	        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
	        		
	        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
	        		
	        		eachInvoiceItem.set("itemValue",roundedAmount);
	        		
	        		try{
	        		eachInvoiceItem.store();
	        		}catch(GenericEntityException e){
	        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
	        		}
	        	}
	        	
	        	try{
	        		
	        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
	        		
	        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
	        	InvoiceHeader.store();
	        	}catch(GenericEntityException e){
	    			Debug.logError(e, "Failed to Populate Invoice ", module);
	    		}
	        	
	    	 }
	    	 
	    	 
	    	 
	    		/* //=======================Change InvoiceRole==============================
             String roPartyId = null;
             
	        try{
        		
        		GenericValue InvoiceHeaderForRole = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
        		
        		 String partyIdTo = InvoiceHeaderForRole.getString("partyId");
            	 String partyIdFrom1 =InvoiceHeaderForRole.getString("partyIdFrom");
             	 List<GenericValue> partyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo), null, null, null, false);
					 GenericValue partyRel = EntityUtil.getFirst(partyRelationship);
					 roPartyId = partyRel.getString("partyIdFrom");
					 InvoiceHeaderForRole.set("partyId",roPartyId);
					 InvoiceHeaderForRole.store();
                 GenericValue invoiceRole = delegator.findOne("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId,"partyId",partyIdTo,"roleTypeId","BILL_TO_CUSTOMER"), false);
                 invoiceRole.remove();
                 try{
                	 GenericValue billToCustomerRole = delegator.makeValue("InvoiceRole");
                	 billToCustomerRole.set("invoiceId", invoiceId);
                	 billToCustomerRole.set("partyId", roPartyId);
                	 billToCustomerRole.set("roleTypeId", "BILL_TO_CUSTOMER");
                     delegator.createOrStore(billToCustomerRole);
                 } catch (GenericEntityException e) {
                	 Debug.logError(e, "Failed to Populate Invoice ", module);
                 }	
                 GenericValue supplierAgentRole = delegator.findOne("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId,"partyId",partyIdFrom1,"roleTypeId","SUPPLIER_AGENT"), false);
                 supplierAgentRole.remove();
                 try{
                	 GenericValue billToCustRole = delegator.makeValue("InvoiceRole");
                	 billToCustRole.set("invoiceId", invoiceId);
                	 billToCustRole.set("partyId", partyIdTo);
                	 billToCustRole.set("roleTypeId", "SUPPLIER_AGENT");
                     delegator.createOrStore(billToCustRole);
                 } catch (GenericEntityException e) {
                	 Debug.logError(e, "Failed to Populate Invoice ", module);
                 }
        		
                 
                 try{
                	 GenericValue billToCustRole1 = delegator.makeValue("InvoiceRole");
                	 billToCustRole1.set("invoiceId", invoiceId);
                	 billToCustRole1.set("partyId", "Company");
                	 billToCustRole1.set("roleTypeId", "ACCOUNTING");
                     delegator.createOrStore(billToCustRole1);
                 } catch (GenericEntityException e) {
                	 Debug.logError(e, "Failed to Populate Invoice ", module);
                 }
                 
                 try{
                	 GenericValue billToCustRole2 = delegator.makeValue("InvoiceRole");
                	 billToCustRole2.set("invoiceId", invoiceId);
                	 billToCustRole2.set("partyId", partyIdTo);
                	 billToCustRole2.set("roleTypeId", "INTERNAL_ORGANIZATIO");
                     delegator.createOrStore(billToCustRole2);
                 } catch (GenericEntityException e) {
                	 Debug.logError(e, "Failed to Populate Invoice ", module);
                 }
        		
        		
        		
        	}catch(GenericEntityException e){
    			Debug.logError(e, "Failed to Populate Invoice ", module);
    		}*/
	    	 
	    	try{
	  			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("invoiceId", invoiceId));
	  			if (ServiceUtil.isError(serviceResult)) {
	  				request.setAttribute("_ERROR_MESSAGE_", "Error While Updateing Indent Summary Details");
	  				return "error";
	             }
	   		}catch(GenericServiceException e){
	 			Debug.logError(e, "Error While Updateing Indent Summary Details ", module);
	 		}
	 		String invoiceSeq = org.ofbiz.accounting.invoice.InvoiceServices.getInvoiceSequence(delegator, invoiceId);
	 		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId+" and Sequence : "+invoiceSeq);	  	 
	 		return "success";
	 	}
	

	
	
	public static String processNewDepotSalesInvoice(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String vehicleId = (String) request.getParameter("vehicleId");
		String tallyrefNo = (String) request.getParameter("tallyrefNo");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purchaseInvoiceId = (String) request.getParameter("purchaseInvoiceId");
		String purposeTypeIdField = (String) request.getParameter("purposeTypeId");
		
		
		String saleTitleTransferEnumId = (String) request.getParameter("saleTitleTransferEnumId");
		String saleTaxType = (String) request.getParameter("saleTaxType");
		
		String purposeTypeId = "YARN_SALE";
	  
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
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		List<GenericValue> orderItems=null;
		try{
		 orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		
	/*	if(UtilValidate.isNotEmpty(tallyrefNo)){
				  List conditionList = FastList.newInstance();
		
				  conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
				  conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS,"BackToBackOrder"));
				  EntityCondition assoc = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				  try{
				 // List  OrderAssocList = delegator.findList("OrderAssoc", assoc, null, null, null,false); 
				  
				   //if(UtilValidate.isNotEmpty(OrderAssocList)){
		
				 // GenericValue OrderAssoc = EntityUtil.getFirst(OrderAssocList);
				  //String actualOrderId = (String)OrderAssoc.get("toOrderId");
				  
				  GenericValue	OrderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId", orderId), false);
		
				  OrderHeader.set("tallyRefNo",tallyrefNo);
				  OrderHeader.store();
				// }
				
			}catch (Exception e) {
				Debug.logError(e, "Problems While Updating Tally Ref No: " + tallyrefNo, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems While Updating Tally Ref No: " + tallyrefNo);
				return "error";
			}
		}	*/
		
		List productQtyList = FastList.newInstance();
		List<Map> invoiceAdjChargesList = FastList.newInstance();
		List<Map> invoiceDiscountsList = FastList.newInstance();
		
	    List<GenericValue> toBillItems = FastList.newInstance();
	    
	    List<String> orderItemSeqIdList = FastList.newInstance();
	    
	    List ignoreAdjustmentsList = FastList.newInstance();
	    
	    String taxListStr = null;
		String orderAdjustmentsListStr = null;
		String discOrderAdjustmentsListStr = null;
		
		String applicableTo = "ALL";
		String applicableToDisc = "ALL";
		
		BigDecimal SERVICE_CHARGEBigDecimal = BigDecimal.ZERO;
		
		Map itemAdjMap = FastMap.newInstance();
		for (int i = 0; i < rowCount; i++) {
			
			List taxRateList = FastList.newInstance();
			List orderAdjustmentList = FastList.newInstance();
			List discOrderAdjustmentList = FastList.newInstance();
			
			Map prodQtyMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String tenPercentStr = "";
			BigDecimal tenPercentSubsidy = BigDecimal.ZERO;
			
			String invoiceItemDiscTypeId = "";
			String adjAmtDiscStr = "";
			String discQtyStr = "";
			BigDecimal adjDiscAmt = BigDecimal.ZERO;
			BigDecimal discQty = BigDecimal.ZERO;
			
			String vatStr=null;
			String cstStr=null;
			
			BigDecimal vat = BigDecimal.ZERO;
			BigDecimal cst = BigDecimal.ZERO;
			//percenatge of TAXes
			
			String VatPercentStr=null;
			String CSTPercentStr=null;
			String orderItemSeq = null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO; 
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productId = "";
			String quantityStr = "";
			String unitPriceStr = "";
			String SERVICE_CHARGEStr = "";
			String orderItemSeqId = "";
			String assessableValue = "";
			String discAssessableValue = "";
			
			BigDecimal quantity = BigDecimal.ZERO;
			BigDecimal uPrice = BigDecimal.ZERO;
			
			
			
			
			
			/*if (paramMap.containsKey("invoiceItemTypeId" + thisSuffix)) {
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
			if (paramMap.containsKey("assessableValue" + thisSuffix)) {
				assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
			}
			//Debug.logassessableValue ============="+assessableValue);
			if(UtilValidate.isNotEmpty(invoiceItemTypeId) && adjAmt.compareTo(BigDecimal.ZERO)>0){
				  
				ignoreAdjustmentsList.add(invoiceItemTypeId);
				
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemTypeId);
				invItemMap.put("amount", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
				if(UtilValidate.isNotEmpty(assessableValue) && (assessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceAdjChargesList.add(invItemMap);	
			}
			
			
			if (paramMap.containsKey("invoiceItemTypeDiscId" + thisSuffix)) {
				invoiceItemDiscTypeId = (String) paramMap.get("invoiceItemTypeDiscId" + thisSuffix);
			}
			
			if (paramMap.containsKey("applicableToDisc" + thisSuffix)) {
				applicableToDisc = (String) paramMap.get("applicableToDisc" + thisSuffix);
			}
			
			if (paramMap.containsKey("adjDiscAmt" + thisSuffix)) {
				adjAmtDiscStr = (String) paramMap.get("adjDiscAmt" + thisSuffix);
			}
			
			if (paramMap.containsKey("discQty" + thisSuffix)) {
				discQtyStr = (String) paramMap.get("discQty" + thisSuffix);
			}
			if (paramMap.containsKey("assessableValueDisc" + thisSuffix)) {
				discAssessableValue = (String) paramMap.get("assessableValueDisc" + thisSuffix);
			}
			//Debug.logdiscAssessableValue ============="+discAssessableValue);
			if(UtilValidate.isNotEmpty(adjAmtDiscStr)){
				try {
					adjDiscAmt = new BigDecimal(adjAmtDiscStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(discQtyStr)){
				try {
					discQty = new BigDecimal(discQtyStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + discQtyStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + discQtyStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(invoiceItemDiscTypeId) && adjDiscAmt.compareTo(BigDecimal.ZERO)>0){
				
				BigDecimal adjQty = BigDecimal.ONE;
				if(UtilValidate.isNotEmpty(discQty)){
					if(discQty.doubleValue()>0){
						//adjDiscAmt = adjDiscAmt.divide(discQty);
						double tempAdjAmt = adjDiscAmt.doubleValue()/discQty.doubleValue();
						adjDiscAmt = new BigDecimal(tempAdjAmt);
						adjQty = discQty;
					}
				}
				
				ignoreAdjustmentsList.add(invoiceItemDiscTypeId);
				
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemDiscTypeId);
				invItemMap.put("amount", adjDiscAmt);
				invItemMap.put("quantity", adjQty);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableToDisc);
				if(UtilValidate.isNotEmpty(discAssessableValue) && (discAssessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceDiscountsList.add(invItemMap);	
			}
			*/
			
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.get("productId" + thisSuffix);
			}
			
			if (paramMap.containsKey("oritemseq" + thisSuffix)) {
				orderItemSeqId = (String) paramMap.get("oritemseq" + thisSuffix);
				
				orderItemSeqIdList.add(orderItemSeqId);
			}
			
			if(UtilValidate.isNotEmpty(productId)){
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
				}
				
				/*if (paramMap.containsKey("SERVICE_CHARGE" + thisSuffix)) {
					SERVICE_CHARGEStr = (String) paramMap.get("SERVICE_CHARGE" + thisSuffix);
					
					//Debug.logSERVICE_CHARGEStr============="+SERVICE_CHARGEStr);
				}*/
				
				if(UtilValidate.isEmpty(quantityStr)){
					request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
					return "error";	
				}
				
				if (paramMap.containsKey("UPrice" + thisSuffix)) {
				   unitPriceStr = (String) paramMap.get("UPrice" + thisSuffix);
				}
				
				if (paramMap.containsKey("VAT" + thisSuffix)) {
					vatStr = (String) paramMap.get("VAT" + thisSuffix);
					
					//Debug.logvatStr=============="+vatStr);
					
				}
				
				if (paramMap.containsKey("CST" + thisSuffix)) {
					cstStr = (String) paramMap.get("CST" + thisSuffix);
					
					//Debug.logcstStr=============="+cstStr);
					
				}
				
				if (paramMap.containsKey("VatPercent" + thisSuffix)) {
					VatPercentStr = (String) paramMap.get("VatPercent" + thisSuffix);
					
					//Debug.logVatPercentStr=============="+VatPercentStr);
					
				}
				
				if (paramMap.containsKey("CSTPercent" + thisSuffix)) {
					CSTPercentStr = (String) paramMap.get("CSTPercent" + thisSuffix);
					
					//Debug.logCSTPercentStr=============="+CSTPercentStr);
					
				}
				
				if (paramMap.containsKey("oritemseq" + thisSuffix)) {
					orderItemSeq = (String) paramMap.get("oritemseq" + thisSuffix);
				}
				
				if (paramMap.containsKey("taxList" + thisSuffix)) {
					taxListStr = (String) paramMap.get("taxList"
							+ thisSuffix);
					
					//Debug.logtaxListStr=============="+taxListStr);

					
					String[] taxList = taxListStr.split(",");
					for (int j = 0; j < taxList.length; j++) {
						String taxType = taxList[j];
						Map taxRateMap = FastMap.newInstance();
						taxRateMap.put("orderAdjustmentTypeId",taxType);
						taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
						taxRateMap.put("amount",BigDecimal.ZERO);
						if(taxType.equals("SERVICE_CHARGE"))
						taxRateMap.put("description","Service Charge");
						//taxRateMap.put("taxAuthGeoId", partyGeoId);
						
						//Debug.log("taxType================"+taxType);
						
						//Debug.log("taxType===11111============="+paramMap.get(taxType + thisSuffix));
						
						//Debug.log("taxType===32323============="+paramMap.get(taxType+ "_AMT" + thisSuffix));
						
						if (paramMap.containsKey(taxType + thisSuffix)) {
							String taxPercentage = (String) paramMap.get(taxType + thisSuffix);
							
							//Debug.log("taxPercentage================"+taxPercentage);
							
							if(UtilValidate.isNotEmpty(taxPercentage) && !(taxPercentage.equals("NaN"))){
								taxRateMap.put("sourcePercentage",new BigDecimal(taxPercentage));
							}
							
							//Debug.log("taxPercentage================"+taxPercentage);
							
						}
						if (paramMap.containsKey(taxType+ "_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(taxType+ "_AMT" + thisSuffix);
							
							//Debug.log("taxAmt================"+taxAmt);
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								taxRateMap.put("amount",new BigDecimal(taxAmt));
							}
						}
						
						List<GenericValue> ordAdj = null;
						try {
						    List ordAdjCond = FastList.newInstance();
						    ordAdjCond.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
						    ordAdjCond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeq));
						    ordAdjCond.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, taxType));
				    		
						    ordAdj = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(ordAdjCond, EntityOperator.AND), null, UtilMisc.toList("orderAdjustmentId"), null, false);
						    if(UtilValidate.isNotEmpty(ordAdj)){
								String orderAdjustmentId = (String)((GenericValue)EntityUtil.getFirst(ordAdj)).get("orderAdjustmentId");
								taxRateMap.put("orderAdjustmentId",orderAdjustmentId);
							}
						} catch (GenericEntityException e) {
					    	return "error";
				        }
						
						/*Map tempTaxMap = FastMap.newInstance();
						tempTaxMap.putAll(taxRateMap);
						
						taxRateList.add(tempTaxMap);*/
						
						GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxRateMap);
						taxRateList.add(orderAdjustment);
						ignoreAdjustmentsList.add(taxType);
					}
				}
				
				Debug.log("vamsi================");
				
				if (paramMap.containsKey("tenPercent" + thisSuffix)) {
					tenPercentStr = (String) paramMap.get("tenPercent" + thisSuffix);
				}
				
				if(UtilValidate.isNotEmpty(tenPercentStr)){
					try {
						tenPercentSubsidy = new BigDecimal(tenPercentStr);
						if(tenPercentSubsidy.compareTo(BigDecimal.ZERO)>0){
							tenPercentSubsidy = tenPercentSubsidy.negate();
						}
						
						Map taxRateMap = FastMap.newInstance();
						taxRateMap.put("orderAdjustmentTypeId","TEN_PERCENT_SUBSIDY");
						taxRateMap.put("sourcePercentage",new BigDecimal("10"));
						taxRateMap.put("amount",tenPercentSubsidy);
						
						try {
						    List ordAdjCond = FastList.newInstance();
						    ordAdjCond.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
						    ordAdjCond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeq));
						    ordAdjCond.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
				    		
						    List<GenericValue> tenPercAdj = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(ordAdjCond, EntityOperator.AND), null, UtilMisc.toList("orderItemSeqId"), null, false);
						    
						    if(UtilValidate.isNotEmpty(tenPercAdj)){
								String orderAdjustmentId = (String)((GenericValue)EntityUtil.getFirst(tenPercAdj)).get("orderAdjustmentId");
								taxRateMap.put("orderAdjustmentId",orderAdjustmentId);
							}
						    
						} catch (GenericEntityException e) {
					    	return "error";
				        }
						
						
						
						/*Map tempTaxMap = FastMap.newInstance();
						tempTaxMap.putAll(taxRateMap);*/
						
						GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxRateMap);
						taxRateList.add(orderAdjustment);
						
						ignoreAdjustmentsList.add("TEN_PERCENT_SUBSIDY");
						
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing ten percent subsidy string: " + tenPercentStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing ten percent subsidy string: " + tenPercentStr);
						return "error";
					}
				}
				
				
				
				if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
					orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
					
					//Debug.logorderAdjustmentsListStr=============="+orderAdjustmentsListStr);
					
					String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
					for (int j = 0; j < orderAdjustmentsList.length; j++) {
						String orderAdjustmentType = orderAdjustmentsList[j];
						Map adjTypeMap = FastMap.newInstance();
						adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
						adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
						adjTypeMap.put("amount",BigDecimal.ZERO);
						//adjTypeMap.put("taxAuthGeoId", partyGeoId);
						
						if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
							String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
							if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
								adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
							}
							
						}
						if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								adjTypeMap.put("amount",new BigDecimal(taxAmt));
							}
						}
						if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
							String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
							if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
								if(isAssessableValue.equals("TRUE")){
									adjTypeMap.put("isAssessableValue", "Y");
									//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
								}
							}
						}
						Map tempAdjMap = FastMap.newInstance();
						tempAdjMap.putAll(adjTypeMap);
						
						
						GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", tempAdjMap);
						taxRateList.add(orderAdjustment);
						
						ignoreAdjustmentsList.add(orderAdjustmentType);
						
						orderAdjustmentList.add(tempAdjMap);
					}
				}
				////Debug.logassessableAdjustmentAmount ================="+assessableAdjustmentAmount);
				
				if (paramMap.containsKey("discOrderAdjustmentsList" + thisSuffix)) {
					discOrderAdjustmentsListStr = (String) paramMap.get("discOrderAdjustmentsList"+ thisSuffix);
					
					//Debug.logdiscOrderAdjustmentsListStr================="+discOrderAdjustmentsListStr);
					
					String[] orderAdjustmentsList = discOrderAdjustmentsListStr.split(",");
					for (int j = 0; j < orderAdjustmentsList.length; j++) {
						String orderAdjustmentType = orderAdjustmentsList[j];
						Map adjTypeMap = FastMap.newInstance();
						adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
						adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
						adjTypeMap.put("amount",BigDecimal.ZERO);
						//adjTypeMap.put("taxAuthGeoId", partyGeoId);
						
						
						//Debug.logorderAdjustmentType + thisSuffix========12121========="+orderAdjustmentType + thisSuffix);
						
						if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
							String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
							
							//Debug.logparamMap========12121========="+paramMap.keySet());
							
							//Debug.logadjPercentage========12121========="+adjPercentage);
							if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
								adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
							}
							
						}
						if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								BigDecimal taxAmtBd = new BigDecimal(taxAmt);
								if(taxAmtBd.compareTo(BigDecimal.ZERO)>0){
									taxAmtBd = taxAmtBd.negate();
								}
								adjTypeMap.put("amount",taxAmtBd);
							}
						}
						if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
							String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
							if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
								if(isAssessableValue.equals("TRUE")){
									adjTypeMap.put("isAssessableValue", "Y");
									//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
								}
							}
						}
						Map tempAdjMap = FastMap.newInstance();
						tempAdjMap.putAll(adjTypeMap);
						
						GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", tempAdjMap);
						taxRateList.add(orderAdjustment);
						
						discOrderAdjustmentList.add(tempAdjMap);
						ignoreAdjustmentsList.add(orderAdjustmentType);
					}
				}
				////Debug.logassessableAdjustmentAmount ================="+assessableAdjustmentAmount);
				
				//Debug.logtaxRateList =================== "+taxRateList);
				
				
				
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
						
						//Debug.logvat================"+vat);
						
					}
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing VAT string: " + vatStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing VAT string: " + vatStr);
					return "error";
				}

				try {
					if (!cstStr.equals("")) {
						cst = new BigDecimal(cstStr);
						
						//Debug.logcst================"+cst);
						
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
						
						//Debug.logvatPercent================"+vatPercent);
						
					}
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing VatPercent string: " + VatPercentStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing VatPercent string: " + VatPercentStr);
					return "error";
				}
				
				try {
					if (!CSTPercentStr.equals("")) {
						cstPercent = new BigDecimal(CSTPercentStr);
						
						//Debug.logcstPercent================"+cstPercent);
						
					}
					
					
					
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing CSTPercent string: " + CSTPercentStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + CSTPercentStr);
					return "error";
				}
				
				
				/*try {
					if (!SERVICE_CHARGEStr.equals("")) {
						SERVICE_CHARGEBigDecimal = new BigDecimal(SERVICE_CHARGEStr);
						//Debug.logSERVICE_CHARGEBigDecimal================"+SERVICE_CHARGEBigDecimal);
					}
					}catch (Exception e) {
						Debug.logError(e, "Problems parsing CSTPercent string: " + SERVICE_CHARGEStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + SERVICE_CHARGEStr);
						return "error";
					}*/
					
				
			}
			GenericValue orderItemValue = null;

			if(UtilValidate.isNotEmpty(productId)){
				try{
					
					List itemAssocCond1 = FastList.newInstance();
					itemAssocCond1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					itemAssocCond1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeq));
					itemAssocCond1.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					
					List<GenericValue> orderItem = delegator.findList("OrderItem", EntityCondition.makeCondition(itemAssocCond1, EntityOperator.AND), null, UtilMisc.toList("orderItemSeqId"), null, false);
					if(UtilValidate.isNotEmpty(orderItem)){
						orderItemValue = EntityUtil.getFirst(orderItem);
						orderItemValue.put("unitPrice", uPrice);
						orderItemValue.put("unitListPrice", uPrice);
						orderItemValue.store();
					}else{
						continue;
					}
				}catch (Exception e) {
					  Debug.logError(e, "Error While Updating purchase Order ", module);
					  request.setAttribute("_ERROR_MESSAGE_", "Unable to update Purchase Order :" + orderId+"....! ");
						return "error";
				}
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
			
			//=====================================================================================

			List<GenericValue> salesOrderitems = null;
			
			 try {
				     List itemAssocCond = FastList.newInstance();
			    	itemAssocCond.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			    	itemAssocCond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeq));
			    	itemAssocCond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		    		
			      	salesOrderitems = delegator.findList("OrderItem", EntityCondition.makeCondition(itemAssocCond, EntityOperator.AND), null, UtilMisc.toList("orderItemSeqId"), null, false);
			    } catch (GenericEntityException e) {
			    	/* Debug.logError("AccountingTroubleCallingCreateInvoiceForOrderService: " + ServiceUtil.getErrorMessage(salesOrderitems), module);
			  			request.setAttribute("_ERROR_MESSAGE_", "Unable to analyse purpose type :"+ServiceUtil.getErrorMessage(salesOrderitems));
	*/		  			return "error";
		        }

				GenericValue eachItem = null;
			 
				if(UtilValidate.isNotEmpty(salesOrderitems)){
					eachItem = EntityUtil.getFirst(salesOrderitems);
					
					BigDecimal billedQuantity = OrderReadHelper.getOrderItemInvoicedQuantity(eachItem);

					
					if(UtilValidate.isNotEmpty(quantity))
					eachItem.set("quantity", quantity.add(billedQuantity));
					if(UtilValidate.isNotEmpty(uPrice))
					eachItem.set("unitPrice", uPrice);
					//eachItem.set("taxRateList", taxRateList);
					
					itemAdjMap.put(orderItemSeq, taxRateList);
					
					Debug.log("quantity=====From========"+quantity);
					
					Debug.log("uPrice=====From========"+uPrice);
						
					toBillItems.add(eachItem);
        
			}
			
		}//end row count for loop
		
		if( UtilValidate.isEmpty(productQtyList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		
		//Debug.logvamsi======begin================");
		
		//Debug.logignoreAdjustmentsList ========vamsi 121212========="+ignoreAdjustmentsList);
	
		//Debug.logitemAdjMap =========vadfafd12121212========"+itemAdjMap);
		
		//Timestamp nowTimeStamp = 
		  //String invoiceId = null;
		
		if(UtilValidate.isNotEmpty(purposeTypeIdField) && purposeTypeIdField.equals("DIES_AND_CHEM_SALE"))
			purposeTypeId = "DIES_AND_CHEM_SALE";
		else
			purposeTypeId = "YARN_SALE";
		
		  Map<String, Object> serviceContext = UtilMisc.toMap("orderId", orderId,"billItems", toBillItems, "eventDate", invoiceDate, "userLogin", userLogin, "ignoreAdjustmentsList", ignoreAdjustmentsList, "itemAdjMap", itemAdjMap,"purposeTypeId",purposeTypeId);
          serviceContext.put("shipmentId",shipmentId);
          try {
               result = dispatcher.runSync("createInvoiceForOrderOrig", serviceContext);
              String invoiceId1 = (String) result.get("invoiceId");
          } catch (GenericServiceException e) {
              
              Debug.logError("AccountingTroubleCallingCreateInvoiceForOrderService: " + ServiceUtil.getErrorMessage(result), module);
  			request.setAttribute("_ERROR_MESSAGE_", "Unable to analyse purpose type :"+ServiceUtil.getErrorMessage(result));
  			return "error";
        }
		
		// Get Purpose type based on product
		
		String invProdId = (String) ((Map) productQtyList.get(0)).get("productId");
		
		/*try{
	  		Map resultCtx = dispatcher.runSync("getPurposeTypeForProduct", UtilMisc.toMap("productId", invProdId, "userLogin", userLogin));  	
	  		purposeTypeId = (String)resultCtx.get("purposeTypeId");
	  	}catch (GenericServiceException e) {
	  		Debug.logError("Unable to analyse purpose type: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to analyse purpose type :"+ServiceUtil.getErrorMessage(result));
			return "error";
	  	}*/
		
		
		
		/*Map processInvoiceContext = FastMap.newInstance();
		processInvoiceContext.put("userLogin", userLogin);
		processInvoiceContext.put("productQtyList", productQtyList);
		processInvoiceContext.put("partyId", partyId);
		processInvoiceContext.put("purposeTypeId", purposeTypeId);
		processInvoiceContext.put("vehicleId", vehicleId);
		processInvoiceContext.put("orderId", orderId);
		processInvoiceContext.put("shipmentId", shipmentId);
		processInvoiceContext.put("invoiceDate", invoiceDate);
		processInvoiceContext.put("invoiceAdjChargesList", invoiceAdjChargesList);
		processInvoiceContext.put("invoiceDiscountsList", invoiceDiscountsList);
		
		if(UtilValidate.isNotEmpty(isDisableAcctg)){
			processInvoiceContext.put("isDisableAcctg", isDisableAcctg);
		}
		//result = createDepotSalesInvoice(dctx, processInvoiceContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate invoice  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";
		}*/
		
		String invoiceId =  (String)result.get("invoiceId");
		
		// Invoice Attributes (Taxes)
		Map<String, Object> createInvAttribute = UtilMisc.toMap(
                "invoiceId", invoiceId,
                "invoiceAttrName", "saleTitleTransferEnumId",
                "invoiceAttrValue", saleTitleTransferEnumId, 
                "userLogin", userLogin);

        try {
        	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
        } catch (GenericServiceException gse) {
            Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
        }
		
        createInvAttribute = UtilMisc.toMap(
                "invoiceId", invoiceId,
                "invoiceAttrName", "saleTaxType",
                "invoiceAttrValue", saleTaxType, 
                "userLogin", userLogin);

        try {
        	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
        } catch (GenericServiceException gse) {
            Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
        }
		
		
	    if(UtilValidate.isNotEmpty(purposeTypeId)){
       	 try{
    	    	GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
    	    	invoice.set("purposeTypeId", purposeTypeId);
    	    	if(UtilValidate.isNotEmpty(tallyrefNo))
    	    	invoice.set("referenceNumber", tallyrefNo);
    	    	
    	    	invoice.store();
    		} catch (Exception e) {
    	       Debug.logError(e, "Error in fetching InvoiceItem ", module);
   			  request.setAttribute("_ERROR_MESSAGE_", "Error in populating invoice purpose :" + purchaseInvoiceId+"....! ");
   				return "error";
    		}

	    }
	    
	    
	  //============================================Service Charge===============================
	    
	    if(UtilValidate.isNotEmpty(SERVICE_CHARGEBigDecimal) && SERVICE_CHARGEBigDecimal.compareTo(BigDecimal.ZERO)>0){/*
    	Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	    
	    createInvoiceItemContext.put("invoiceId",invoiceId);
        createInvoiceItemContext.put("invoiceItemTypeId", "INVOICE_ITM_ADJ");
        createInvoiceItemContext.put("parentInvoiceId", invoiceId);
        createInvoiceItemContext.put("description", "Service Charge");
        createInvoiceItemContext.put("quantity",BigDecimal.ONE);
        createInvoiceItemContext.put("amount",SERVICE_CHARGEBigDecimal);
        createInvoiceItemContext.put("productId", "");
        createInvoiceItemContext.put("userLogin", userLogin);
        try{
        	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
    	    //Debug.logcreateInvoiceItemResult========Service Charge========="+createInvoiceItemResult);

        	if(ServiceUtil.isError(createInvoiceItemResult)){
      			  request.setAttribute("_ERROR_MESSAGE_", "Error in populating invoice purpose :" + invoiceId+"....! ");
      				return "error";
      		}
        } catch (Exception e) {
        	request.setAttribute("_ERROR_MESSAGE_", "Error in populating Service Charge : ");
			return "error";
		}
	    
        
        List<GenericValue> InvoiceServiceItem = null;
        String invoiceItemSeqId = "";
	    List conditionList = FastList.newInstance();
    	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
    	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INVOICE_ITM_ADJ"));
    	conditionList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"Service Charge"));
    	 try{
    		 InvoiceServiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
    		 
      		GenericValue InvoiceService = EntityUtil.getFirst(InvoiceServiceItem);

      		invoiceItemSeqId = (String)InvoiceService.get("invoiceItemSeqId");
    		 
    	 }catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
			}
        
        
    	 List<GenericValue> orderAdjServiceCharge = null;
     	
    	 String orderAdjustmentId = "";
 	    conditionList.clear();
     	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
     	conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS,"SERVICE_CHARGE"));
     	 try{
     		orderAdjServiceCharge = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
       		GenericValue orderAdjServiceChargeItem = EntityUtil.getFirst(orderAdjServiceCharge);
       		 orderAdjustmentId = (String)orderAdjServiceChargeItem.get("orderAdjustmentId");
     		 
     	 }catch(GenericEntityException e){
 				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
 			}
    	 
    	 

   		GenericValue newItemAttr = delegator.makeValue("OrderAdjustmentBilling");    
   		
				newItemAttr.set("orderAdjustmentId", orderAdjustmentId);
				newItemAttr.set("invoiceId", invoiceId);
				newItemAttr.set("invoiceItemSeqId", invoiceItemSeqId);
				newItemAttr.set("quantity", BigDecimal.ZERO);
				newItemAttr.set("amount",SERVICE_CHARGEBigDecimal);
				 try{
			  	   newItemAttr.create();
				 }catch (Exception e) {
					 request.setAttribute("_ERROR_MESSAGE_", "Error in populating OrderAdjustmentBilling : ");
						return "error";
		 	 	}
    	 
    	 
        
	    */}
	    
	  //============================================Rounding Off===============================
	    
	    List<GenericValue> InvoiceItem = null;
    	
	    List conditionList = FastList.newInstance();
    	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
    	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
    	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
    	 try{
    	   InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
    	 
    	 }catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
			}
	    
	    
    	 if(UtilValidate.isNotEmpty(InvoiceItem)){
        	 
    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
    		 
        	for(GenericValue eachInvoiceItem : InvoiceItem){
        	
        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
        		BigDecimal itemValue = quantity.multiply(amount);
        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
        		
        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
        		
        		eachInvoiceItem.set("itemValue",roundedAmount);
        		
        		try{
        		eachInvoiceItem.store();
        		}catch(GenericEntityException e){
        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
        		}
        	}
        	
        	try{
        		
        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
        		
        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
        	InvoiceHeader.store();
        	}catch(GenericEntityException e){
    			Debug.logError(e, "Failed to Populate Invoice ", module);
    		}
        	
        	
        	//===============populate Claim Amount in Shipment=======
        	
        	
        	
        	 conditionList.clear();
     		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
     		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
     		//conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderIteSeq));
     		List<GenericValue> OrderItemBillingAndInvoiceAndInvoiceItem =null;
     		try{
     			OrderItemBillingAndInvoiceAndInvoiceItem = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
     	      
     		} catch (Exception e) {
        		  Debug.logError(e, "Error in fetching InvoiceItem ", module);
     			  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching Order Item billing :" + invoiceId+"....! ");
     				return "error";
     		}
     		
     		
     		GenericValue OrderItemBillingAndInvoice = EntityUtil.getFirst(OrderItemBillingAndInvoiceAndInvoiceItem);
     		String SaleOrderId = OrderItemBillingAndInvoice.getString("orderId");
        	
     		String Scheam = "";
     		try{
	     		List<GenericValue> orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, SaleOrderId), null, null, null, false);
				
				List<GenericValue> scheamList = EntityUtil.filterByCondition(orderAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
				
				if(UtilValidate.isNotEmpty(scheamList)){
					GenericValue orderScheme = EntityUtil.getFirst(scheamList);
					Scheam = (String) orderScheme.get("attrValue");
				}
     		 }catch(Exception e){
				 Debug.logError("problem While Fetching OrderAttribute : "+e, module);
			 } 
        	
			
			if(UtilValidate.isNotEmpty(Scheam) && Scheam != "General"){
				
				BigDecimal reimbursementEligibilityPercentage = new BigDecimal(2);
				
				BigDecimal DepotreimbursementEligibilityPercentage = new BigDecimal(2);
				
				try{
				GenericValue shipmentObj=delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
				
				
	  			String partyIdTo = shipmentObj.getString("partyIdTo");
	  			
	  			
	  			//=====================party is IsDepo or not
				
				BigDecimal receiptEligablityAmount=shipmentObj.getBigDecimal("estimatedShipCost");
				
				BigDecimal invoiceEligablityAmount=(invoiceGrandTotal.multiply(reimbursementEligibilityPercentage)).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
				BigDecimal finalEligablityAmount=receiptEligablityAmount.compareTo(invoiceEligablityAmount)>0?invoiceEligablityAmount:receiptEligablityAmount;
				
				
				finalEligablityAmount = (finalEligablityAmount.setScale(0, rounding));
				
				shipmentObj.set("claimAmount",finalEligablityAmount);
				shipmentObj.set("claimStatus","APPLYED");
				if(finalEligablityAmount.compareTo(BigDecimal.ZERO)<=0){
					shipmentObj.set("claimStatus","");
				}
				shipmentObj.store();
				 }catch(Exception e){
					 Debug.logError("populate claim in Shipment Reimbursment : "+e, module);
				 } 
				
			}
    	 }
    	 
    	 
    	 
		   try{
			   
			  /* conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId));
			   EntityCondition condExpress = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", condExpress, null, null, null, false);
			
		   
			   String actualOrderId = (EntityUtil.getFirst(orderAssocList)).getString("toOrderId");*/
			   
			    conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
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
			
			
		    
		   }catch (Exception e) {
				Debug.logError(e, "Problems while Calculating balance Amount for order: " + partyId, module);
				//request.setAttribute("_ERROR_MESSAGE_", "Problems while Calculating balance Amount for order: " + partyId);
				return "error";
			}
    	 
		
		   /*		   
		   //===========================Change the Roles in invoice=========================================
	    	 
   	    String roPartyId = null;
   	 
            try{
       		
       		GenericValue InvoiceHeaderForRole = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
       		
       		
       		 String partyIdFrom1 = InvoiceHeaderForRole.getString("partyIdFrom");
           	 List<GenericValue> partyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdFrom1), null, null, null, false);
				 GenericValue partyRel = EntityUtil.getFirst(partyRelationship);
				 roPartyId = partyRel.getString("partyIdFrom");
				 InvoiceHeaderForRole.set("partyIdFrom",roPartyId);
				 InvoiceHeaderForRole.store();
                GenericValue invoiceRole = delegator.findOne("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId,"partyId",partyIdFrom1,"roleTypeId","BILL_FROM_VENDOR"), false);
                String custmentPartyId = invoiceRole.getString("partyId");
          	     invoiceRole.remove();
          	     
          	     
          	  try{
            	 GenericValue billFromVendorRole = delegator.makeValue("InvoiceRole");
            	 billFromVendorRole.set("invoiceId", invoiceId);
            	 billFromVendorRole.set("partyId", roPartyId);
            	 billFromVendorRole.set("roleTypeId", "BILL_FROM_VENDOR");
                 delegator.createOrStore(billFromVendorRole);
                 GenericValue customerAgentRole = delegator.makeValue("InvoiceRole");
                 customerAgentRole.set("invoiceId", invoiceId);
                 customerAgentRole.set("partyId", custmentPartyId);
                 customerAgentRole.set("roleTypeId", "CUSTOMER_AGENT");
                 delegator.createOrStore(customerAgentRole);
             } catch (GenericEntityException e) {
           	  Debug.logError(e, "Failed to Populate Invoice ", module);
             }	
             
          	  
          	 try{
            	 GenericValue billToCustRole1 = delegator.makeValue("InvoiceRole");
            	 billToCustRole1.set("invoiceId", invoiceId);
            	 billToCustRole1.set("partyId", "Company");
            	 billToCustRole1.set("roleTypeId", "ACCOUNTING");
                 delegator.createOrStore(billToCustRole1);
             } catch (GenericEntityException e) {
            	 Debug.logError(e, "Failed to Populate Invoice ", module);
             }
             
             try{
            	 GenericValue billToCustRole2 = delegator.makeValue("InvoiceRole");
            	 billToCustRole2.set("invoiceId", invoiceId);
            	 billToCustRole2.set("partyId", custmentPartyId);
            	 billToCustRole2.set("roleTypeId", "INTERNAL_ORGANIZATIO");
                 delegator.createOrStore(billToCustRole2);
             } catch (GenericEntityException e) {
            	 Debug.logError(e, "Failed to Populate Invoice ", module);
             }
       		
       		
       	}catch(GenericEntityException e){
   			Debug.logError(e, "Failed to Populate Invoice ", module);
   		}
   	 
	   //======================================================================================
    	 */
	   
	   try{
 			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("invoiceId", invoiceId));
 			if (ServiceUtil.isError(serviceResult)) {
 				request.setAttribute("_ERROR_MESSAGE_", "Error While Updateing Indent Summary Details");
 				return "error";
            }
  		}catch(GenericServiceException e){
			Debug.logError(e, "Error While Updateing Indent Summary Details ", module);
		} 
		   

    	String invoiceSeq = org.ofbiz.accounting.invoice.InvoiceServices.getInvoiceSequence(delegator, invoiceId);
 		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId+" and Sequence : "+invoiceSeq);
 		request.setAttribute("invoiceId",invoiceId);
 	
 		return "success";
 	}

	
	
	public static String processEditSalesInvoice(HttpServletRequest request, HttpServletResponse response) {
		
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String invoiceId = (String) request.getParameter("invoiceId");
		String tallyrefNo = (String) request.getParameter("tallyrefNo");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purchaseInvoiceId = (String) request.getParameter("purchaseInvoiceId");
		
		String saleTitleTransferEnumId = (String) request.getParameter("saleTitleTransferEnumId");
		String saleTaxType = (String) request.getParameter("saleTaxType");
		
		String purposeTypeId = "YARN_SALE";
	  
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
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		List<GenericValue> orderItems=null;
		try{
			orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		
		String branchId = "";
		   
		 try {
				List conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "COST_CENTER_ID"));
		    	List <GenericValue> invoiceRoles = delegator.findList("InvoiceRole", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		    	if(UtilValidate.isNotEmpty(invoiceRoles)){
		    		
		    		GenericValue invoiceRoles1 = EntityUtil.getFirst(invoiceRoles);
		    		
		    		branchId = invoiceRoles1.getString("partyId");
		    		
		    	}
		 }catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		
		// Remove existing invoice items
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, UtilMisc.toList("INV_FPROD_ITEM", "TEN_PERCENT_SUBSIDY")));
		List<GenericValue> invoiceItemList =null;
		try{
			invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(invoiceItemList)){
				
				for(int i=0; i<invoiceItemList.size(); i++){
					GenericValue removeItem = (GenericValue)invoiceItemList.get(i);
					
					List adjBillCondList = FastList.newInstance();
					adjBillCondList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, removeItem.get("invoiceId")));
					adjBillCondList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, removeItem.get("invoiceItemSeqId")));
					
					
					List<GenericValue> orderAdjBillings = delegator.findList("OrderAdjustmentBilling", EntityCondition.makeCondition(adjBillCondList, EntityOperator.AND), null, null, null, false);
					if(UtilValidate.isNotEmpty(orderAdjBillings)){
						delegator.removeAll(orderAdjBillings);
					}
					delegator.removeValue(removeItem);
				}
				
				
			}
		} catch (Exception e) {
   		  	Debug.logError(e, "Error in fetching InvoiceItem ", module);
			request.setAttribute("_ERROR_MESSAGE_", "Error in fetching Order Item billing :" + invoiceId+"....! ");
			return "error";
		}
		
		
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		List<GenericValue> InvoiceAttributeList =null;
		try{
			InvoiceAttributeList = delegator.findList("InvoiceAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(InvoiceAttributeList)){
				delegator.removeAll(InvoiceAttributeList);
			}
		} catch (Exception e) {
   		  	Debug.logError(e, "Error in fetching InvoiceAttribute ", module);
			request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceAttribute :" + invoiceId+"....! ");
			return "error";
		}
		
		
	// Invoice Attributes (Taxes)
			Map<String, Object> createInvAttribute = UtilMisc.toMap(
	                "invoiceId", invoiceId,
	                "invoiceAttrName", "saleTitleTransferEnumId",
	                "invoiceAttrValue", saleTitleTransferEnumId, 
	                "userLogin", userLogin);

	        try {
	        	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
	        } catch (GenericServiceException gse) {
	            Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
	        }
			
	        createInvAttribute = UtilMisc.toMap(
	                "invoiceId", invoiceId,
	                "invoiceAttrName", "saleTaxType",
	                "invoiceAttrValue", saleTaxType, 
	                "userLogin", userLogin);

	        try {
	        	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
	        } catch (GenericServiceException gse) {
	            Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
	        }
		
		List productQtyList = FastList.newInstance();
		List<Map> invoiceAdjChargesList = FastList.newInstance();
		List<Map> invoiceDiscountsList = FastList.newInstance();
		
	    List<GenericValue> toBillItems = FastList.newInstance();
	    
	    List<String> orderItemSeqIdList = FastList.newInstance();
	    BigDecimal SERVICE_CHARGEBigDecimal = BigDecimal.ZERO;
		
		String applicableTo = "ALL";
		String applicableToDisc = "ALL";
		for (int i = 0; i < rowCount; i++) {
			
			Map prodQtyMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String invoiceItemDiscTypeId = "";
			String adjAmtDiscStr = "";
			String discQtyStr = "";
			
			List taxRateList = FastList.newInstance();
			String assessableValue = "";
			String discAssessableValue = "";
			
			BigDecimal adjDiscAmt = BigDecimal.ZERO;
			BigDecimal discQty = BigDecimal.ZERO;
			
			String vatStr=null;
			String cstStr=null;
			
			BigDecimal vat = BigDecimal.ZERO;
			BigDecimal cst = BigDecimal.ZERO;
			//percenatge of TAXes
			
			String VatPercentStr=null;
			String CSTPercentStr=null;
			String orderItemSeq = null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO; 
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productId = "";
			String quantityStr = "";
			String unitPriceStr = "";
			String orderItemSeqId = "";
			String invoiceItemSeqId = "";
			String SERVICE_CHARGEStr = "";
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
			if (paramMap.containsKey("assessableValue" + thisSuffix)) {
				assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
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
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemTypeId);
				invItemMap.put("amount", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
				if(UtilValidate.isNotEmpty(assessableValue) && (assessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceAdjChargesList.add(invItemMap);	
			}
			
			
			if (paramMap.containsKey("invoiceItemTypeDiscId" + thisSuffix)) {
				invoiceItemDiscTypeId = (String) paramMap.get("invoiceItemTypeDiscId" + thisSuffix);
			}
			
			if (paramMap.containsKey("applicableToDisc" + thisSuffix)) {
				applicableToDisc = (String) paramMap.get("applicableToDisc" + thisSuffix);
			}
			
			if (paramMap.containsKey("adjDiscAmt" + thisSuffix)) {
				adjAmtDiscStr = (String) paramMap.get("adjDiscAmt" + thisSuffix);
			}
			
			if (paramMap.containsKey("discQty" + thisSuffix)) {
				discQtyStr = (String) paramMap.get("discQty" + thisSuffix);
			}
			if (paramMap.containsKey("assessableValueDisc" + thisSuffix)) {
				discAssessableValue = (String) paramMap.get("assessableValueDisc" + thisSuffix);
			}
			//Debug.log("discAssessableValue ============="+discAssessableValue);
			if(UtilValidate.isNotEmpty(adjAmtDiscStr)){
				try {
					adjDiscAmt = new BigDecimal(adjAmtDiscStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(discQtyStr)){
				try {
					discQty = new BigDecimal(discQtyStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + discQtyStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + discQtyStr);
					return "error";
				}
			}
			if(UtilValidate.isNotEmpty(invoiceItemDiscTypeId) && adjDiscAmt.compareTo(BigDecimal.ZERO)>0){
				
				BigDecimal adjQty = BigDecimal.ONE;
				if(UtilValidate.isNotEmpty(discQty)){
					if(discQty.doubleValue()>0){
						//adjDiscAmt = adjDiscAmt.divide(discQty);
						double tempAdjAmt = adjDiscAmt.doubleValue()/discQty.doubleValue();
						adjDiscAmt = new BigDecimal(tempAdjAmt);
						adjQty = discQty;
					}
				}
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemDiscTypeId);
				invItemMap.put("amount", adjDiscAmt);
				invItemMap.put("quantity", adjQty);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableToDisc);
				if(UtilValidate.isNotEmpty(discAssessableValue) && (discAssessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceDiscountsList.add(invItemMap);	
			}
			
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.get("productId" + thisSuffix);
			}
			
			if (paramMap.containsKey("oritemseq" + thisSuffix)) {
				orderItemSeqId = (String) paramMap.get("oritemseq" + thisSuffix);
				
				orderItemSeqIdList.add(orderItemSeqId);
			}
			
			if(UtilValidate.isNotEmpty(productId)){
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
				}
				if(UtilValidate.isEmpty(quantityStr)){
					request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
					return "error";	
				}
				
				if (paramMap.containsKey("SERVICE_CHARGE" + thisSuffix)) {
					SERVICE_CHARGEStr = (String) paramMap.get("SERVICE_CHARGE" + thisSuffix);
					
					//Debug.logSERVICE_CHARGEStr============="+SERVICE_CHARGEStr);
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
				if (paramMap.containsKey("oritemseq" + thisSuffix)) {
					orderItemSeq = (String) paramMap.get("oritemseq" + thisSuffix);
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
				
				/*try {
					if (!SERVICE_CHARGEStr.equals("")) {
						SERVICE_CHARGEBigDecimal = new BigDecimal(SERVICE_CHARGEStr);
						//Debug.logSERVICE_CHARGEBigDecimal================"+SERVICE_CHARGEBigDecimal);
					}
					}catch (Exception e) {
						Debug.logError(e, "Problems parsing SERVICE_CHARGEStr string: " + SERVICE_CHARGEStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing SERVICE_CHARGEStr string: " + SERVICE_CHARGEStr);
						return "error";
					}*/

				/*try {
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
				}*/
				
				if (paramMap.containsKey("invoiceItemSeqId" + thisSuffix)) {
					invoiceItemSeqId = (String) paramMap.get("invoiceItemSeqId" + thisSuffix);
				}
				Debug.log("invoiceItemSeqId =============="+invoiceItemSeqId);
				
				String istemSeq=String.format("%05d", i+1);
				
				if (paramMap.containsKey("tenPercent" + thisSuffix)) {
					String tenPercentStr = (String) paramMap.get("tenPercent" + thisSuffix);
					if(UtilValidate.isNotEmpty(tenPercentStr)){
						List tenPercCondList = FastList.newInstance();
						tenPercCondList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
						tenPercCondList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqId));
						tenPercCondList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
						try {
							BigDecimal tenPercentSubsidy = new BigDecimal(tenPercentStr);
							tenPercentSubsidy = tenPercentSubsidy.abs();
							if(tenPercentSubsidy.compareTo(BigDecimal.ZERO)>0){
								tenPercentSubsidy = tenPercentSubsidy.negate();
							}
							
							List<GenericValue> tenPercItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(tenPercCondList, EntityOperator.AND), null, null, null, false);
							
							Debug.log("tenPercItemList =============="+tenPercItemList);
							
							GenericValue tenPercItem = EntityUtil.getFirst(tenPercItemList);
							if(UtilValidate.isNotEmpty(tenPercItem)){
								tenPercItem.set("amount", tenPercentSubsidy);
								tenPercItem.store();
							}
														
						} catch (Exception e) {
							Debug.logError(e, "Problems parsing ten percent subsidy string: " + tenPercentStr, module);
							request.setAttribute("_ERROR_MESSAGE_", "Problems parsing ten percent subsidy string: " + tenPercentStr);
							return "error";
						}
					}
				}//subsidy
			}
			
			
			
			
			 //======================Edit Taxes List==================
		    
		    
		    if (paramMap.containsKey("taxList" + thisSuffix)) {
				String taxListStr = (String) paramMap.get("taxList"
						+ thisSuffix);
				
				//Debug.log("taxListStr =============="+taxListStr);
				
				String[] taxList = taxListStr.split(",");
				for (int j = 0; j < taxList.length; j++) {
					String taxType = taxList[j];
					//Debug.log("taxType =============="+taxType);
					
					Map<String, Object> createInvoiceItemContext = FastMap.newInstance();

		        	BigDecimal amount = BigDecimal.ZERO;
		        	createInvoiceItemContext.put("invoiceId",invoiceId);
		        	
		        	if(taxType.equals("SERVICE_CHARGE")){
		            createInvoiceItemContext.put("invoiceItemTypeId", "INVOICE_ITM_ADJ");
		        	createInvoiceItemContext.put("description", "Service Charge");
		        	}else{
		        	createInvoiceItemContext.put("invoiceItemTypeId", taxType);
		        	}
		        	
		            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
		            createInvoiceItemContext.put("parentInvoiceItemSeqId", invoiceItemSeqId);
		            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
		            createInvoiceItemContext.put("userLogin", userLogin);
		            
		            if (paramMap.containsKey(taxType + thisSuffix)) {
		            	String taxAmt = (String) paramMap.get(taxType + thisSuffix);
			            	if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								if((new BigDecimal(taxAmt)).compareTo(BigDecimal.ZERO)>0){
									createInvoiceItemContext.put("sourcePercentage",new BigDecimal(taxAmt));
							}
						}
		            }
		            
		            if (paramMap.containsKey(taxType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(taxType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							if((new BigDecimal(taxAmt)).compareTo(BigDecimal.ZERO)>0){
								createInvoiceItemContext.put("amount",new BigDecimal(taxAmt));
								createInvoiceItemContext.put("costCenterId",branchId.trim());
								try{
					            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
					            	
					            	if(ServiceUtil.isError(createInvoiceItemResult)){
					            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
										return "error";
					          		}
					            } catch (Exception e) {
					            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
									return "error";
					    		}
						}
							
						}		
					}
		            
		            
					
				}
			}
			
			
			
			//=======================edit invoice Adjustmnets=========================
			
		    List orderAdjustmentList = FastList.newInstance();
		    
			if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
				String orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
				
				String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							adjTypeMap.put("amount",new BigDecimal(taxAmt));
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}else{
								adjTypeMap.put("isAssessableValue", "N");
							}
						}else{
							adjTypeMap.put("isAssessableValue", "N");
						}
					}else{
						adjTypeMap.put("isAssessableValue", "N");
					}
					
					
					
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					orderAdjustmentList.add(tempAdjMap);
				}
				
				
			   //=============now populate the values=================
				
				
				for(int j=0; j<orderAdjustmentList.size(); j++){
					Map adjMap = (Map) orderAdjustmentList.get(j);
					if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
						Map adjItemCtx = FastMap.newInstance();
						adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
						adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
						adjItemCtx.put("quantity", BigDecimal.ONE);
						adjItemCtx.put("invoiceId", invoiceId);
						adjItemCtx.put("costCenterId",branchId.trim());
						adjItemCtx.put("parentInvoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceItemSeqId", invoiceItemSeqId);
						if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
							adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
						}
						if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
							adjItemCtx.put("isAssessableValue", "Y");
						}
						adjItemCtx.put("userLogin", userLogin);
						
						try{
			            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", adjItemCtx);
			            	
			            	if(ServiceUtil.isError(createInvoiceItemResult)){
			            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
								return "error";
			          		}
			            } catch (Exception e) {
			            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
			    		}
					}
				}
				
			//==================End of The OrderAdjustments====================
			
				
			}
			
			
			//=======================edit Invoice Discounts=========================
			List discOrderAdjustmentList = FastList.newInstance();
			
			
			
			if (paramMap.containsKey("discOrderAdjustmentsList" + thisSuffix)) {
				String discOrderAdjustmentsListStr = (String) paramMap.get("discOrderAdjustmentsList"+ thisSuffix);
				
				//Debug.log("discOrderAdjustmentsListStr====================="+discOrderAdjustmentsListStr);
				
				String[] orderAdjustmentsList = discOrderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							BigDecimal taxAmtBd = new BigDecimal(taxAmt);
							if(taxAmtBd.compareTo(BigDecimal.ZERO)>0){
								taxAmtBd = taxAmtBd.negate();
							}
							adjTypeMap.put("amount",taxAmtBd);
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}else{
								adjTypeMap.put("isAssessableValue", "N");
							}
						}else{
							adjTypeMap.put("isAssessableValue", "N");
						}
					}else{
						adjTypeMap.put("isAssessableValue", "N");
					}
					
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					discOrderAdjustmentList.add(tempAdjMap);
					
				}
				
				//Debug.log("discOrderAdjustmentList====================="+discOrderAdjustmentList);

				//====now population===============
				
				for(int j=0; j<discOrderAdjustmentList.size(); j++){
					Map adjMap = (Map) discOrderAdjustmentList.get(j);
					
					//Debug.log("adjMap====================="+adjMap);

					if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)<0){
						Map adjItemCtx = FastMap.newInstance();
						
						//Debug.log("orderAdjustmentTypeId====================="+adjMap.get("orderAdjustmentTypeId"));

						adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
						adjItemCtx.put("costCenterId",branchId.trim());
						//Debug.log("amount====================="+adjMap.get("amount"));
						
						adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
						adjItemCtx.put("quantity", BigDecimal.ONE);
						adjItemCtx.put("invoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceId", invoiceId);
						adjItemCtx.put("userLogin", userLogin);
						adjItemCtx.put("parentInvoiceItemSeqId", invoiceItemSeqId);
						
						//Debug.log("sourcePercentage====================="+adjMap.get("sourcePercentage"));

						
						if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
							adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
						}
						
						
						//Debug.log("isAssessableValue====================="+adjMap.get("isAssessableValue"));

						
						if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
							adjItemCtx.put("isAssessableValue", "Y");
							
							
						}
						
						//Debug.log("isAssessableValue===========3232=========="+adjMap.get("isAssessableValue"));

						//Debug.log("adjItemCtx====================="+adjItemCtx);

						
						try{
			            	Map<String, Object> createInvoiceItemResult1 = dispatcher.runSync("createInvoiceItem", adjItemCtx);
			            	
							//Debug.log("createInvoiceItemResult1====================="+createInvoiceItemResult1);
			            	
			            } catch (Exception e) {
			            	 Debug.logError(e, "Error in fetching InvoiceItem ", module);
							return "error";
			    		}
					}
				}
				
				
				
			}
			
              //============End of THe Invoice Discounts==========			
			
		}//=============loopEnd===============
		
		//Debug.log("purposeTypeId====================="+purposeTypeId);

		
	    if(UtilValidate.isNotEmpty(purposeTypeId)){
       	 	try{
    	    	GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
    	    	if(UtilValidate.isNotEmpty(tallyrefNo))
    	    	invoice.set("referenceNumber", tallyrefNo);
    	    	if(UtilValidate.isNotEmpty(invoiceDate)){
    	    		invoice.set("invoiceDate", invoiceDate);
    	    	}
    	    	
    	    	invoice.store();
    		} catch (Exception e) {
    	       Debug.logError(e, "Error in fetching InvoiceItem ", module);
   			  request.setAttribute("_ERROR_MESSAGE_", "Error in populating invoice purpose :" + invoiceId+"....! ");
   				return "error";
    		}

	    }
		
	    
	 

	    
	    
	    
	    
	    
	/*	if(UtilValidate.isNotEmpty(invoiceAdjChargesList)){
	        for (Map<String, Object> adjustMap : invoiceAdjChargesList) {
				
	        	Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	
	        	BigDecimal amount = BigDecimal.ZERO;
	        	
	        	
	        	createInvoiceItemContext.put("invoiceId",invoiceId);
	            createInvoiceItemContext.put("invoiceItemTypeId", adjustMap.get("adjustmentTypeId"));
	            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
	            //createInvoiceItemContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
	            //createInvoiceItemContext.put("description", eachItem.get("description"));
	            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
	            if (UtilValidate.isNotEmpty(adjustMap.get("assessableValue"))) {
					String isAssessableValue = (String) adjustMap.get("assessableValue");
					if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
						if(isAssessableValue.equals("TRUE") || isAssessableValue.equals("Y")){
							createInvoiceItemContext.put("isAssessableValue", "Y");
						}
					}
				}
	            
	            if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					createInvoiceItemContext.put("amount",amount);
				}
	            
	            //createInvoiceItemContext.put("productId", eachItem.get("productId"));
	            createInvoiceItemContext.put("userLogin", userLogin);
	            try{
	            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	            	
	            	if(ServiceUtil.isError(createInvoiceItemResult)){
	            		String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
	                    Debug.logError(errMsg, module);
	                    return ServiceUtil.returnError(errMsg);
						  request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
	                    
	          		}
	            } catch (Exception e) {
	            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
					return "error";
	    		}
				
			}
		
		}*/
        
		
	/*	if(UtilValidate.isNotEmpty(invoiceDiscountsList)){
		
			for (Map<String, Object> adjustMap : invoiceDiscountsList) {
				
				Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	        	BigDecimal amount = BigDecimal.ZERO;
	        	
	        	createInvoiceItemContext.put("invoiceId",invoiceId);
	            createInvoiceItemContext.put("invoiceItemTypeId", adjustMap.get("adjustmentTypeId"));
	            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
	            //createInvoiceItemContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
	            //createInvoiceItemContext.put("description", eachItem.get("description"));
	            createInvoiceItemContext.put("quantity", adjustMap.get("quantity"));
	            
	            if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					createInvoiceItemContext.put("amount",amount.negate());
				}
	            if (UtilValidate.isNotEmpty(adjustMap.get("assessableValue"))) {
					String isAssessableValue = (String) adjustMap.get("assessableValue");
					if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
						if(isAssessableValue.equals("TRUE") || isAssessableValue.equals("Y")){
							createInvoiceItemContext.put("isAssessableValue", "Y");
						}
					}
				}
	            //createInvoiceItemContext.put("productId", eachItem.get("productId"));
	            createInvoiceItemContext.put("userLogin", userLogin);
	            try{
	            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	            	if(ServiceUtil.isError(createInvoiceItemResult)){
	            		String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
	                    Debug.logError(errMsg, module);
	                    return ServiceUtil.returnError(errMsg);
						  request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
	                    
	          		}
	            } catch (Exception e) {
	            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
					return "error";
	    		}
			}
		
		}*/
		//==============update in Purchase=====================
	    
	    
	    if(UtilValidate.isNotEmpty(SERVICE_CHARGEBigDecimal) && SERVICE_CHARGEBigDecimal.compareTo(BigDecimal.ZERO)>0){/*
	    	Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
		    
		    createInvoiceItemContext.put("invoiceId",invoiceId);
	        createInvoiceItemContext.put("invoiceItemTypeId", "INVOICE_ITM_ADJ");
	        createInvoiceItemContext.put("parentInvoiceId", invoiceId);
	        createInvoiceItemContext.put("description", "Service Charge");
	        createInvoiceItemContext.put("quantity",BigDecimal.ONE);
	        createInvoiceItemContext.put("amount",SERVICE_CHARGEBigDecimal);
	        createInvoiceItemContext.put("productId", "");
	        createInvoiceItemContext.put("userLogin", userLogin);
	        try{
	        	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	    	    //Debug.logcreateInvoiceItemResult========Service Charge========="+createInvoiceItemResult);

	        	if(ServiceUtil.isError(createInvoiceItemResult)){
	      			  request.setAttribute("_ERROR_MESSAGE_", "Error in populating invoice purpose :" + invoiceId+"....! ");
	      				return "error";
	      		}
	        } catch (Exception e) {
	        	request.setAttribute("_ERROR_MESSAGE_", "Error in populating Service Charge : ");
				return "error";
			}
		    
	        
	        List<GenericValue> InvoiceServiceItem = null;
	        String invoiceItemSeqId = "";
		    List conditionList1 = FastList.newInstance();
		    conditionList1.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		    conditionList1.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INVOICE_ITM_ADJ"));
		    conditionList1.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"Service Charge"));
	    	 try{
	    		 InvoiceServiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList1, EntityOperator.AND), null, null, null, false);
	    		 
	      		GenericValue InvoiceService = EntityUtil.getFirst(InvoiceServiceItem);

	      		invoiceItemSeqId = (String)InvoiceService.get("invoiceItemSeqId");
	    		 
	    	 }catch(GenericEntityException e){
					Debug.logError(e, "Failed to retrive InvoiceItem ", module);
				}
	        
	        
	    	 List<GenericValue> orderAdjServiceCharge = null;
	     	
	    	 String orderAdjustmentId = "";
	 	    conditionList.clear();
	     	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	     	conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS,"SERVICE_CHARGE"));
	     	 try{
	     		orderAdjServiceCharge = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	       		GenericValue orderAdjServiceChargeItem = EntityUtil.getFirst(orderAdjServiceCharge);
	       		 orderAdjustmentId = (String)orderAdjServiceChargeItem.get("orderAdjustmentId");
	     		 
	     	 }catch(GenericEntityException e){
	 				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
	 			}
	    	 
	    	 

	   		GenericValue newItemAttr = delegator.makeValue("OrderAdjustmentBilling");    
	   		
					newItemAttr.set("orderAdjustmentId", orderAdjustmentId);
					newItemAttr.set("invoiceId", invoiceId);
					newItemAttr.set("invoiceItemSeqId", invoiceItemSeqId);
					newItemAttr.set("quantity", BigDecimal.ZERO);
					newItemAttr.set("amount",SERVICE_CHARGEBigDecimal);
					 try{
				  	   newItemAttr.create();
					 }catch (Exception e) {
						 request.setAttribute("_ERROR_MESSAGE_", "Error in populating OrderAdjustmentBilling : ");
							return "error";
			 	 	}
	    	 
	    	 
	        
		    */}
		
		
		
		
  //============================================Rounding Off===============================
	    
	    List<GenericValue> InvoiceItem = null;
    	
	    List conditionList1 = FastList.newInstance();
	    conditionList1.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
	    conditionList1.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
    	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
    	 try{
    	   InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList1, EntityOperator.AND), null, null, null, false);
    	 
    	 }catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
			}
	    
	    
    	 if(UtilValidate.isNotEmpty(InvoiceItem)){
        	 
    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
    		 
        	for(GenericValue eachInvoiceItem : InvoiceItem){
        	
        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
        		BigDecimal itemValue = quantity.multiply(amount);
        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
        		
        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
        		
        		eachInvoiceItem.set("itemValue",roundedAmount);
        		eachInvoiceItem.set("costCenterId",branchId);
        		try{
        		eachInvoiceItem.store();
        		}catch(GenericEntityException e){
        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
        		}
        	}
        	
        	try{
        		
        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
        		
        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
        	InvoiceHeader.store();
        	}catch(GenericEntityException e){
    			Debug.logError(e, "Failed to Populate Invoice ", module);
    		}
        	
    	 }
    	 try{
  			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("invoiceId", invoiceId));
  			if (ServiceUtil.isError(serviceResult)) {
  				request.setAttribute("_ERROR_MESSAGE_", "Error While Updateing Indent Summary Details");
  				return "error";
             }
   		}catch(GenericServiceException e){
 			Debug.logError(e, "Error While Updateing Indent Summary Details ", module);
 		}
		

		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId);	  	 
	
		return "success";
	}
	
	
	
	public static String processDepotEditSalesInvoice(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String invoiceId = (String) request.getParameter("invoiceId");
		String tallyrefNo = (String) request.getParameter("tallyrefNo");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purchaseInvoiceId = (String) request.getParameter("purchaseInvoiceId");
		
		String saleTitleTransferEnumId = (String) request.getParameter("saleTitleTransferEnumId");
		String saleTaxType = (String) request.getParameter("saleTaxType");
		
		String purposeTypeId = "DEPOT_YARN_SALE";
	  
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
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		List<GenericValue> orderItems=null;
		try{
			orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		
		
		String branchId = "";
		
		 try {
				List conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "COST_CENTER_ID"));
		    	List <GenericValue> invoiceRoles = delegator.findList("InvoiceRole", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		    	if(UtilValidate.isNotEmpty(invoiceRoles)){
		    		
		    		GenericValue invoiceRoles1 = EntityUtil.getFirst(invoiceRoles);
		    		
		    		branchId = invoiceRoles1.getString("partyId");
		    		
		    	}
		 }catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		
		// Remove existing invoice items
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, UtilMisc.toList("INV_FPROD_ITEM", "TEN_PERCENT_SUBSIDY")));
		List<GenericValue> invoiceItemList =null;
		try{
			invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(invoiceItemList)){
				
				for(int i=0; i<invoiceItemList.size(); i++){
					GenericValue removeItem = (GenericValue)invoiceItemList.get(i);
					
					List adjBillCondList = FastList.newInstance();
					adjBillCondList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, removeItem.get("invoiceId")));
					adjBillCondList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, removeItem.get("invoiceItemSeqId")));
					
					
					List<GenericValue> orderAdjBillings = delegator.findList("OrderAdjustmentBilling", EntityCondition.makeCondition(adjBillCondList, EntityOperator.AND), null, null, null, false);
					if(UtilValidate.isNotEmpty(orderAdjBillings)){
						delegator.removeAll(orderAdjBillings);
					}
					delegator.removeValue(removeItem);
				}
				
				
			}
		} catch (Exception e) {
   		  	Debug.logError(e, "Error in fetching InvoiceItem ", module);
			request.setAttribute("_ERROR_MESSAGE_", "Error in fetching Order Item billing :" + invoiceId+"....! ");
			return "error";
		}
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		List<GenericValue> InvoiceAttributeList =null;
		try{
			InvoiceAttributeList = delegator.findList("InvoiceAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(InvoiceAttributeList)){
				delegator.removeAll(InvoiceAttributeList);
			}
		} catch (Exception e) {
   		  	Debug.logError(e, "Error in fetching InvoiceAttribute ", module);
			request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceAttribute :" + invoiceId+"....! ");
			return "error";
		}
		
		
	// Invoice Attributes (Taxes)
			Map<String, Object> createInvAttribute = UtilMisc.toMap(
	                "invoiceId", invoiceId,
	                "invoiceAttrName", "saleTitleTransferEnumId",
	                "invoiceAttrValue", saleTitleTransferEnumId, 
	                "userLogin", userLogin);

	        try {
	        	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
	        } catch (GenericServiceException gse) {
	            Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
	        }
			
	        createInvAttribute = UtilMisc.toMap(
	                "invoiceId", invoiceId,
	                "invoiceAttrName", "saleTaxType",
	                "invoiceAttrValue", saleTaxType, 
	                "userLogin", userLogin);

	        try {
	        	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
	        } catch (GenericServiceException gse) {
	            Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
    }
		
		
		
		List productQtyList = FastList.newInstance();
		List<Map> invoiceAdjChargesList = FastList.newInstance();
		List<Map> invoiceDiscountsList = FastList.newInstance();
		
	    List<GenericValue> toBillItems = FastList.newInstance();
	    
	    List<String> orderItemSeqIdList = FastList.newInstance();
		
		String applicableTo = "ALL";
		String applicableToDisc = "ALL";
		for (int i = 0; i < rowCount; i++) {
			
			Map prodQtyMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String invoiceItemDiscTypeId = "";
			String adjAmtDiscStr = "";
			String discQtyStr = "";
			
			List taxRateList = FastList.newInstance();
			String assessableValue = "";
			String discAssessableValue = "";
			
			BigDecimal adjDiscAmt = BigDecimal.ZERO;
			BigDecimal discQty = BigDecimal.ZERO;
			
			String vatStr=null;
			String cstStr=null;
			
			BigDecimal vat = BigDecimal.ZERO;
			BigDecimal cst = BigDecimal.ZERO;
			//percenatge of TAXes
			
			String VatPercentStr=null;
			String CSTPercentStr=null;
			String orderItemSeq = null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO; 
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productId = "";
			String quantityStr = "";
			String unitPriceStr = "";
			String orderItemSeqId = "";
			String invoiceItemSeqId = "";
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
			if (paramMap.containsKey("assessableValue" + thisSuffix)) {
				assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
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
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemTypeId);
				invItemMap.put("amount", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
				if(UtilValidate.isNotEmpty(assessableValue) && (assessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceAdjChargesList.add(invItemMap);	
			}
			
			
			if (paramMap.containsKey("invoiceItemTypeDiscId" + thisSuffix)) {
				invoiceItemDiscTypeId = (String) paramMap.get("invoiceItemTypeDiscId" + thisSuffix);
			}
			
			if (paramMap.containsKey("applicableToDisc" + thisSuffix)) {
				applicableToDisc = (String) paramMap.get("applicableToDisc" + thisSuffix);
			}
			
			if (paramMap.containsKey("adjDiscAmt" + thisSuffix)) {
				adjAmtDiscStr = (String) paramMap.get("adjDiscAmt" + thisSuffix);
			}
			
			if (paramMap.containsKey("discQty" + thisSuffix)) {
				discQtyStr = (String) paramMap.get("discQty" + thisSuffix);
			}
			if (paramMap.containsKey("assessableValueDisc" + thisSuffix)) {
				discAssessableValue = (String) paramMap.get("assessableValueDisc" + thisSuffix);
			}
			//Debug.log("discAssessableValue ============="+discAssessableValue);
			if(UtilValidate.isNotEmpty(adjAmtDiscStr)){
				try {
					adjDiscAmt = new BigDecimal(adjAmtDiscStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(discQtyStr)){
				try {
					discQty = new BigDecimal(discQtyStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + discQtyStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + discQtyStr);
					return "error";
				}
			}
			if(UtilValidate.isNotEmpty(invoiceItemDiscTypeId) && adjDiscAmt.compareTo(BigDecimal.ZERO)>0){
				
				BigDecimal adjQty = BigDecimal.ONE;
				if(UtilValidate.isNotEmpty(discQty)){
					if(discQty.doubleValue()>0){
						//adjDiscAmt = adjDiscAmt.divide(discQty);
						double tempAdjAmt = adjDiscAmt.doubleValue()/discQty.doubleValue();
						adjDiscAmt = new BigDecimal(tempAdjAmt);
						adjQty = discQty;
					}
				}
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemDiscTypeId);
				invItemMap.put("amount", adjDiscAmt);
				invItemMap.put("quantity", adjQty);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableToDisc);
				if(UtilValidate.isNotEmpty(discAssessableValue) && (discAssessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceDiscountsList.add(invItemMap);	
			}
			
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.get("productId" + thisSuffix);
			}
			
			if (paramMap.containsKey("oritemseq" + thisSuffix)) {
				orderItemSeqId = (String) paramMap.get("oritemseq" + thisSuffix);
				
				orderItemSeqIdList.add(orderItemSeqId);
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
				if (paramMap.containsKey("oritemseq" + thisSuffix)) {
					orderItemSeq = (String) paramMap.get("oritemseq" + thisSuffix);
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

				/*try {
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
				}*/
				
				if (paramMap.containsKey("invoiceItemSeqId" + thisSuffix)) {
					invoiceItemSeqId = (String) paramMap.get("invoiceItemSeqId" + thisSuffix);
				}
				String istemSeq=String.format("%05d", i+1);
				if (paramMap.containsKey("tenPercent" + thisSuffix)) {
					String tenPercentStr = (String) paramMap.get("tenPercent" + thisSuffix);
					if(UtilValidate.isNotEmpty(tenPercentStr)){
						List tenPercCondList = FastList.newInstance();
						tenPercCondList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
						tenPercCondList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqId));
						tenPercCondList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
						try {
							BigDecimal tenPercentSubsidy = new BigDecimal(tenPercentStr);
							tenPercentSubsidy = tenPercentSubsidy.abs();
							if(tenPercentSubsidy.compareTo(BigDecimal.ZERO)>0){
								tenPercentSubsidy = tenPercentSubsidy.negate();
							}
							
							//Debug.log("tenPercentSubsidy =============="+tenPercentSubsidy);

							
							List<GenericValue> tenPercItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(tenPercCondList, EntityOperator.AND), null, null, null, false);
							
							//Debug.log("tenPercItemList =============="+tenPercItemList);
							
							GenericValue tenPercItem = EntityUtil.getFirst(tenPercItemList);
							if(UtilValidate.isNotEmpty(tenPercItem)){
								tenPercItem.set("amount", tenPercentSubsidy);
								tenPercItem.store();
							}
														
						} catch (Exception e) {
							Debug.logError(e, "Problems parsing ten percent subsidy string: " + tenPercentStr, module);
							request.setAttribute("_ERROR_MESSAGE_", "Problems parsing ten percent subsidy string: " + tenPercentStr);
							return "error";
						}
					}
				}//subsidy
			}
			
			
			
			 //======================Edit Taxes List==================
		    
		    
		    if (paramMap.containsKey("taxList" + thisSuffix)) {
				String taxListStr = (String) paramMap.get("taxList"
						+ thisSuffix);
				
				//Debug.log("taxListStr =============="+taxListStr);
				
				String[] taxList = taxListStr.split(",");
				for (int j = 0; j < taxList.length; j++) {
					String taxType = taxList[j];
					//Debug.log("taxType =============="+taxType);
					
					Map<String, Object> createInvoiceItemContext = FastMap.newInstance();

		        	BigDecimal amount = BigDecimal.ZERO;
		        	createInvoiceItemContext.put("invoiceId",invoiceId);
		            createInvoiceItemContext.put("invoiceItemTypeId", taxType);
		            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
		            createInvoiceItemContext.put("parentInvoiceItemSeqId", invoiceItemSeqId);
		            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
		            createInvoiceItemContext.put("userLogin", userLogin);
		            
		            if (paramMap.containsKey(taxType + thisSuffix)) {
		            	String taxAmt = (String) paramMap.get(taxType + thisSuffix);
			            	if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								if((new BigDecimal(taxAmt)).compareTo(BigDecimal.ZERO)>0){
									createInvoiceItemContext.put("sourcePercentage",new BigDecimal(taxAmt));
							}
						}
		            }
		            
		            if (paramMap.containsKey(taxType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(taxType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							
							if((new BigDecimal(taxAmt)).compareTo(BigDecimal.ZERO)>0){
							createInvoiceItemContext.put("amount",new BigDecimal(taxAmt));
							createInvoiceItemContext.put("costCenterId",branchId);
							
							try{
				            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
				            	
				            	if(ServiceUtil.isError(createInvoiceItemResult)){
				            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
									return "error";
				          		}
				            } catch (Exception e) {
				            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
								return "error";
				    		}
						}
						}
					}
		            
		            
					
				}
			}
			
			
			
			//=======================edit invoice Adjustmnets=========================
			
		    List orderAdjustmentList = FastList.newInstance();
		    
			if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
				String orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
				
				String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							adjTypeMap.put("amount",new BigDecimal(taxAmt));
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}else{
								adjTypeMap.put("isAssessableValue", "N");
							}
						}else{
							adjTypeMap.put("isAssessableValue", "N");
						}
					}else{
						adjTypeMap.put("isAssessableValue", "N");
					}
					
					
					
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					orderAdjustmentList.add(tempAdjMap);
				}
				
				
			   //=============now populate the values=================
				
				
				for(int j=0; j<orderAdjustmentList.size(); j++){
					Map adjMap = (Map) orderAdjustmentList.get(j);
					if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
						Map adjItemCtx = FastMap.newInstance();
						adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
						adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
						adjItemCtx.put("quantity", BigDecimal.ONE);
						adjItemCtx.put("invoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceItemSeqId", invoiceItemSeqId);
						adjItemCtx.put("costCenterId",branchId);
						if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
							adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
						}
						if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
							adjItemCtx.put("isAssessableValue", "Y");
						}
						adjItemCtx.put("userLogin", userLogin);
						
						try{
			            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", adjItemCtx);
			            	
			            	if(ServiceUtil.isError(createInvoiceItemResult)){
			            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
								return "error";
			          		}
			            } catch (Exception e) {
			            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
			    		}
					}
				}
				
			//==================End of The OrderAdjustments====================
			
				
			}
			
			
			//=======================edit Invoice Discounts=========================
			List discOrderAdjustmentList = FastList.newInstance();
			
			
			
			if (paramMap.containsKey("discOrderAdjustmentsList" + thisSuffix)) {
				String discOrderAdjustmentsListStr = (String) paramMap.get("discOrderAdjustmentsList"+ thisSuffix);
				
				//Debug.log("discOrderAdjustmentsListStr====================="+discOrderAdjustmentsListStr);
				
				String[] orderAdjustmentsList = discOrderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							BigDecimal taxAmtBd = new BigDecimal(taxAmt);
							if(taxAmtBd.compareTo(BigDecimal.ZERO)>0){
								taxAmtBd = taxAmtBd.negate();
							}
							adjTypeMap.put("amount",taxAmtBd);
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}else{
								adjTypeMap.put("isAssessableValue", "N");
							}
						}else{
							adjTypeMap.put("isAssessableValue", "N");
						}
					}else{
						adjTypeMap.put("isAssessableValue", "N");
					}
					
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					discOrderAdjustmentList.add(tempAdjMap);
					
				}
				
				//Debug.log("discOrderAdjustmentList====================="+discOrderAdjustmentList);

				//====now population===============
				
				for(int j=0; j<discOrderAdjustmentList.size(); j++){
					Map adjMap = (Map) discOrderAdjustmentList.get(j);
					
					//Debug.log("adjMap====================="+adjMap);

					if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)<0){
						Map adjItemCtx = FastMap.newInstance();
						
						//Debug.log("orderAdjustmentTypeId====================="+adjMap.get("orderAdjustmentTypeId"));

						adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
						
						//Debug.log("amount====================="+adjMap.get("amount"));
						
						adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
						adjItemCtx.put("quantity", BigDecimal.ONE);
						adjItemCtx.put("invoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceId", invoiceId);
						adjItemCtx.put("userLogin", userLogin);
						adjItemCtx.put("parentInvoiceItemSeqId", invoiceItemSeqId);
						adjItemCtx.put("costCenterId",branchId);
						
						//Debug.log("sourcePercentage====================="+adjMap.get("sourcePercentage"));

						
						if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
							adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
						}
						
						
						//Debug.log("isAssessableValue====================="+adjMap.get("isAssessableValue"));

						
						if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
							adjItemCtx.put("isAssessableValue", "Y");
							
							
						}
						
						//Debug.log("isAssessableValue===========3232=========="+adjMap.get("isAssessableValue"));

						//Debug.log("adjItemCtx====================="+adjItemCtx);

						
						try{
			            	Map<String, Object> createInvoiceItemResult1 = dispatcher.runSync("createInvoiceItem", adjItemCtx);
			            	
							//Debug.log("createInvoiceItemResult1====================="+createInvoiceItemResult1);
			            	
			            } catch (Exception e) {
			            	 Debug.logError(e, "Error in fetching InvoiceItem ", module);
							return "error";
			    		}
					}
				}
				
				
				
			}
			
              //============End of THe Invoice Discounts==========			
			
		}//=============loopEnd===============
		
		//Debug.log("purposeTypeId====================="+purposeTypeId);

		
	    if(UtilValidate.isNotEmpty(purposeTypeId)){
       	 	try{
    	    	GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
    	    	if(UtilValidate.isNotEmpty(tallyrefNo))
    	    	invoice.set("referenceNumber", tallyrefNo);
    	    	if(UtilValidate.isNotEmpty(invoiceDate)){
    	    		invoice.set("invoiceDate", invoiceDate);
    	    	}
    	    	
    	    	invoice.store();
    		} catch (Exception e) {
    	       Debug.logError(e, "Error in fetching InvoiceItem ", module);
   			  request.setAttribute("_ERROR_MESSAGE_", "Error in populating invoice purpose :" + invoiceId+"....! ");
   				return "error";
    		}

	    }
		
	    
	 

	    
	    
	    
	    
	    
	/*	if(UtilValidate.isNotEmpty(invoiceAdjChargesList)){
	        for (Map<String, Object> adjustMap : invoiceAdjChargesList) {
				
	        	Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	
	        	BigDecimal amount = BigDecimal.ZERO;
	        	
	        	
	        	createInvoiceItemContext.put("invoiceId",invoiceId);
	            createInvoiceItemContext.put("invoiceItemTypeId", adjustMap.get("adjustmentTypeId"));
	            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
	            //createInvoiceItemContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
	            //createInvoiceItemContext.put("description", eachItem.get("description"));
	            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
	            if (UtilValidate.isNotEmpty(adjustMap.get("assessableValue"))) {
					String isAssessableValue = (String) adjustMap.get("assessableValue");
					if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
						if(isAssessableValue.equals("TRUE") || isAssessableValue.equals("Y")){
							createInvoiceItemContext.put("isAssessableValue", "Y");
						}
					}
				}
	            
	            if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					createInvoiceItemContext.put("amount",amount);
				}
	            
	            //createInvoiceItemContext.put("productId", eachItem.get("productId"));
	            createInvoiceItemContext.put("userLogin", userLogin);
	            try{
	            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	            	
	            	if(ServiceUtil.isError(createInvoiceItemResult)){
	            		String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
	                    Debug.logError(errMsg, module);
	                    return ServiceUtil.returnError(errMsg);
						  request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
	                    
	          		}
	            } catch (Exception e) {
	            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
					return "error";
	    		}
				
			}
		
		}*/
        
		
	/*	if(UtilValidate.isNotEmpty(invoiceDiscountsList)){
		
			for (Map<String, Object> adjustMap : invoiceDiscountsList) {
				
				Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	        	BigDecimal amount = BigDecimal.ZERO;
	        	
	        	createInvoiceItemContext.put("invoiceId",invoiceId);
	            createInvoiceItemContext.put("invoiceItemTypeId", adjustMap.get("adjustmentTypeId"));
	            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
	            //createInvoiceItemContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
	            //createInvoiceItemContext.put("description", eachItem.get("description"));
	            createInvoiceItemContext.put("quantity", adjustMap.get("quantity"));
	            
	            if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					createInvoiceItemContext.put("amount",amount.negate());
				}
	            if (UtilValidate.isNotEmpty(adjustMap.get("assessableValue"))) {
					String isAssessableValue = (String) adjustMap.get("assessableValue");
					if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
						if(isAssessableValue.equals("TRUE") || isAssessableValue.equals("Y")){
							createInvoiceItemContext.put("isAssessableValue", "Y");
						}
					}
				}
	            //createInvoiceItemContext.put("productId", eachItem.get("productId"));
	            createInvoiceItemContext.put("userLogin", userLogin);
	            try{
	            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	            	if(ServiceUtil.isError(createInvoiceItemResult)){
	            		String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
	                    Debug.logError(errMsg, module);
	                    return ServiceUtil.returnError(errMsg);
						  request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
	                    
	          		}
	            } catch (Exception e) {
	            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
					return "error";
	    		}
			}
		
		}*/
		//==============update in Purchase=====================
		
		
		
		
  //============================================Rounding Off===============================
	    
	    List<GenericValue> InvoiceItem = null;
    	
	    List conditionList1 = FastList.newInstance();
	    conditionList1.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
	    conditionList1.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
    	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
    	 try{
    	   InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList1, EntityOperator.AND), null, null, null, false);
    	 
    	 }catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
			}
	    
	    
    	 if(UtilValidate.isNotEmpty(InvoiceItem)){
        	 
    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
    		 
        	for(GenericValue eachInvoiceItem : InvoiceItem){
        	
        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
        		BigDecimal itemValue = quantity.multiply(amount);
        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
        		
        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
        		
        		eachInvoiceItem.set("itemValue",roundedAmount);
        		eachInvoiceItem.set("costCenterId", branchId);
        		try{
        		eachInvoiceItem.store();
        		}catch(GenericEntityException e){
        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
        		}
        	}
        	
        	try{
        		
        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
        		
        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
        	InvoiceHeader.store();
        	}catch(GenericEntityException e){
    			Debug.logError(e, "Failed to Populate Invoice ", module);
    		}
        	
    	 }
		
		

		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId);	  	 
	
		return "success";
	}
	
	
	
	
	public static String processEditPurchaseInvoice(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String invoiceId = (String) request.getParameter("invoiceId");
		String tallyrefNo = (String) request.getParameter("tallyrefNo");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purchaseInvoiceId = (String) request.getParameter("purchaseInvoiceId");
		
		String purchaseTitleTransferEnumId = (String) request.getParameter("purchaseTitleTransferEnumId");
		String purchaseTaxType = (String) request.getParameter("purchaseTaxType");
		
		String purposeTypeId = "YARN_SALE";
	  
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
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		List<GenericValue> orderItems=null;
		try{
			orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		
		String branchId = "";
		
		 try {
			List conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "COST_CENTER_ID"));
			List <GenericValue> invoiceRoles = delegator.findList("InvoiceRole", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(invoiceRoles)){
				
				GenericValue invoiceRoles1 = EntityUtil.getFirst(invoiceRoles);
				
				branchId = invoiceRoles1.getString("partyId");
		    		
		    	}
		 }catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		
		// Remove existing invoice items
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, UtilMisc.toList("INV_RAWPROD_ITEM")));
		List<GenericValue> invoiceItemList =null;
		try{
			invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(invoiceItemList)){
				
				for(int i=0; i<invoiceItemList.size(); i++){
					GenericValue removeItem = (GenericValue)invoiceItemList.get(i);
					
					List adjBillCondList = FastList.newInstance();
					adjBillCondList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, removeItem.get("invoiceId")));
					adjBillCondList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, removeItem.get("invoiceItemSeqId")));
					
					
					List<GenericValue> orderAdjBillings = delegator.findList("OrderAdjustmentBilling", EntityCondition.makeCondition(adjBillCondList, EntityOperator.AND), null, null, null, false);
					if(UtilValidate.isNotEmpty(orderAdjBillings)){
						delegator.removeAll(orderAdjBillings);
					}
					delegator.removeValue(removeItem);
				}
				
				
			}
		} catch (Exception e) {
   		  	Debug.logError(e, "Error in fetching InvoiceItem ", module);
			request.setAttribute("_ERROR_MESSAGE_", "Error in fetching Order Item billing :" + invoiceId+"....! ");
			return "error";
		}
		
		
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
			List<GenericValue> InvoiceAttributeList =null;
			try{
				InvoiceAttributeList = delegator.findList("InvoiceAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				if(UtilValidate.isNotEmpty(InvoiceAttributeList)){
					delegator.removeAll(InvoiceAttributeList);
				}
			} catch (Exception e) {
	   		  	Debug.logError(e, "Error in fetching InvoiceAttribute ", module);
				request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceAttribute :" + invoiceId+"....! ");
				return "error";
	         }

              // Invoice Attributes (Taxes)
				Map<String, Object> createInvAttribute = UtilMisc.toMap(
                        "invoiceId", invoiceId,
                        "invoiceAttrName", "purchaseTitleTransferEnumId",
                        "invoiceAttrValue", purchaseTitleTransferEnumId, 
                        "userLogin", userLogin);

                try {
                	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
                } catch (GenericServiceException gse) {
                    Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
                }
				
                createInvAttribute = UtilMisc.toMap(
                        "invoiceId", invoiceId,
                        "invoiceAttrName", "purchaseTaxType",
                        "invoiceAttrValue", purchaseTaxType, 
                        "userLogin", userLogin);

                try {
                	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
                } catch (GenericServiceException gse) {
                    Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
        }
		
		List productQtyList = FastList.newInstance();
		List<Map> invoiceAdjChargesList = FastList.newInstance();
		List<Map> invoiceDiscountsList = FastList.newInstance();
		
	    List<GenericValue> toBillItems = FastList.newInstance();
	    
	    List<String> orderItemSeqIdList = FastList.newInstance();
		
		String applicableTo = "ALL";
		String applicableToDisc = "ALL";
		for (int i = 0; i < rowCount; i++) {
			
			Map prodQtyMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String invoiceItemDiscTypeId = "";
			String adjAmtDiscStr = "";
			String discQtyStr = "";
			
			List taxRateList = FastList.newInstance();
			String assessableValue = "";
			String discAssessableValue = "";
			
			BigDecimal adjDiscAmt = BigDecimal.ZERO;
			BigDecimal discQty = BigDecimal.ZERO;
			
			String vatStr=null;
			String cstStr=null;
			
			BigDecimal vat = BigDecimal.ZERO;
			BigDecimal cst = BigDecimal.ZERO;
			//percenatge of TAXes
			
			String VatPercentStr=null;
			String CSTPercentStr=null;
			String orderItemSeq = null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO; 
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productId = "";
			String quantityStr = "";
			String unitPriceStr = "";
			String orderItemSeqId = "";
			String invoiceItemSeqId = "";
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
			if (paramMap.containsKey("assessableValue" + thisSuffix)) {
				assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
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
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemTypeId);
				invItemMap.put("amount", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
				if(UtilValidate.isNotEmpty(assessableValue) && (assessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceAdjChargesList.add(invItemMap);	
			}
			
			
			if (paramMap.containsKey("invoiceItemTypeDiscId" + thisSuffix)) {
				invoiceItemDiscTypeId = (String) paramMap.get("invoiceItemTypeDiscId" + thisSuffix);
			}
			
			if (paramMap.containsKey("applicableToDisc" + thisSuffix)) {
				applicableToDisc = (String) paramMap.get("applicableToDisc" + thisSuffix);
			}
			
			if (paramMap.containsKey("adjDiscAmt" + thisSuffix)) {
				adjAmtDiscStr = (String) paramMap.get("adjDiscAmt" + thisSuffix);
			}
			
			if (paramMap.containsKey("discQty" + thisSuffix)) {
				discQtyStr = (String) paramMap.get("discQty" + thisSuffix);
			}
			if (paramMap.containsKey("assessableValueDisc" + thisSuffix)) {
				discAssessableValue = (String) paramMap.get("assessableValueDisc" + thisSuffix);
			}
			////Debug.log("discAssessableValue ============="+discAssessableValue);
			if(UtilValidate.isNotEmpty(adjAmtDiscStr)){
				try {
					adjDiscAmt = new BigDecimal(adjAmtDiscStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(discQtyStr)){
				try {
					discQty = new BigDecimal(discQtyStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + discQtyStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + discQtyStr);
					return "error";
				}
			}
			if(UtilValidate.isNotEmpty(invoiceItemDiscTypeId) && adjDiscAmt.compareTo(BigDecimal.ZERO)>0){
				
				BigDecimal adjQty = BigDecimal.ONE;
				if(UtilValidate.isNotEmpty(discQty)){
					if(discQty.doubleValue()>0){
						//adjDiscAmt = adjDiscAmt.divide(discQty);
						double tempAdjAmt = adjDiscAmt.doubleValue()/discQty.doubleValue();
						adjDiscAmt = new BigDecimal(tempAdjAmt);
						adjQty = discQty;
					}
				}
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemDiscTypeId);
				invItemMap.put("amount", adjDiscAmt);
				invItemMap.put("quantity", adjQty);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableToDisc);
				if(UtilValidate.isNotEmpty(discAssessableValue) && (discAssessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceDiscountsList.add(invItemMap);	
			}
			
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.get("productId" + thisSuffix);
			}
			
			if (paramMap.containsKey("oritemseq" + thisSuffix)) {
				orderItemSeqId = (String) paramMap.get("oritemseq" + thisSuffix);
				
				orderItemSeqIdList.add(orderItemSeqId);
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
				if (paramMap.containsKey("oritemseq" + thisSuffix)) {
					orderItemSeq = (String) paramMap.get("oritemseq" + thisSuffix);
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

				/*try {
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
				}*/
				
				if (paramMap.containsKey("invoiceItemSeqId" + thisSuffix)) {
					invoiceItemSeqId = (String) paramMap.get("invoiceItemSeqId" + thisSuffix);
				}
				////Debug.log("invoiceItemSeqId =============="+invoiceItemSeqId);
				
				
				if (paramMap.containsKey("tenPercent" + thisSuffix)) {
					String tenPercentStr = (String) paramMap.get("tenPercent" + thisSuffix);
					if(UtilValidate.isNotEmpty(tenPercentStr)){
						List tenPercCondList = FastList.newInstance();
						tenPercCondList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
						tenPercCondList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
						try {
							BigDecimal tenPercentSubsidy = new BigDecimal(tenPercentStr);
							if(tenPercentSubsidy.compareTo(BigDecimal.ZERO)>0){
								tenPercentSubsidy = tenPercentSubsidy.negate();
							}
							
							List<GenericValue> tenPercItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(tenPercCondList, EntityOperator.AND), null, null, null, false);
							GenericValue tenPercItem = EntityUtil.getFirst(tenPercItemList);
							if(UtilValidate.isNotEmpty(tenPercItem)){
								tenPercItem.set("amount", tenPercentSubsidy);
								tenPercItem.store();
							}
														
						} catch (Exception e) {
							Debug.logError(e, "Problems parsing ten percent subsidy string: " + tenPercentStr, module);
							request.setAttribute("_ERROR_MESSAGE_", "Problems parsing ten percent subsidy string: " + tenPercentStr);
							return "error";
						}
					}
				}//subsidy
			}
			
			
			
			
			 //======================Edit Taxes List==================
		    
		    
		   /* if (paramMap.containsKey("taxList" + thisSuffix)) {
				String taxListStr = (String) paramMap.get("taxList"
						+ thisSuffix);
				
				////Debug.log("taxListStr =============="+taxListStr);
				
				String[] taxList = taxListStr.split(",");
				for (int j = 0; j < taxList.length; j++) {
					String taxType = taxList[j];
					////Debug.log("taxType =============="+taxType);
					
					Map<String, Object> createInvoiceItemContext = FastMap.newInstance();

		        	BigDecimal amount = BigDecimal.ZERO;
		        	createInvoiceItemContext.put("invoiceId",invoiceId);
		            createInvoiceItemContext.put("invoiceItemTypeId", taxType);
		            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
		            createInvoiceItemContext.put("parentInvoiceItemSeqId", invoiceItemSeqId);
		            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
		            createInvoiceItemContext.put("userLogin", userLogin);
		            if (paramMap.containsKey(taxType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(taxType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							createInvoiceItemContext.put("amount",new BigDecimal(taxAmt));
							try{
				            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
				            	
				            	if(ServiceUtil.isError(createInvoiceItemResult)){
				            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
									return "error";
				          		}
				            } catch (Exception e) {
				            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
								return "error";
				    		}
						}
					}
		            
		            
					
				}
			}*/
			
			
			if (paramMap.containsKey("purTaxList" + thisSuffix)) {
				String purTaxListStr = (String) paramMap.get("purTaxList"
						+ thisSuffix);
				
				String[] taxList = purTaxListStr.split(",");
				for (int j = 0; j < taxList.length; j++) {
					String taxType = taxList[j];
					Map taxRateMap = FastMap.newInstance();
					String purTaxType = taxType.replace("_SALE", "_PUR");
					
					
					Map<String, Object> createInvoiceItemContext = FastMap.newInstance();

		        	BigDecimal amount = BigDecimal.ZERO;
		        	createInvoiceItemContext.put("invoiceId",invoiceId);
		            createInvoiceItemContext.put("invoiceItemTypeId", purTaxType);
		            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
		            createInvoiceItemContext.put("parentInvoiceItemSeqId", invoiceItemSeqId);
		            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
		            createInvoiceItemContext.put("userLogin", userLogin);
					
					
					/*taxRateMap.put("orderAdjustmentTypeId",purTaxType);
					taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
					taxRateMap.put("amount",BigDecimal.ZERO);
					//taxRateMap.put("taxAuthGeoId", partyGeoId);
*/					
					if (paramMap.containsKey(taxType + "_PUR" + thisSuffix)) {
						String taxPercentage = (String) paramMap.get(taxType + "_PUR" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxPercentage) && !(taxPercentage.equals("NaN"))){
							 createInvoiceItemContext.put("sourcePercentage", new BigDecimal(taxPercentage));
						}
					}
					if (paramMap.containsKey(taxType+ "_PUR_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(taxType+ "_PUR_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							
							if((new BigDecimal(taxAmt)).compareTo(BigDecimal.ZERO)>0){
							
							 createInvoiceItemContext.put("amount", new BigDecimal(taxAmt));
							 createInvoiceItemContext.put("costCenterId", branchId);
							 
							 try{
					            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
					            	
					            	if(ServiceUtil.isError(createInvoiceItemResult)){
					            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
										return "error";
					          		}
					            } catch (Exception e) {
					            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
									return "error";
					    		}
						}
						}
					}
					
				}
			}
			
			
			
			//=======================edit invoice Adjustmnets=========================
			
		    List orderAdjustmentList = FastList.newInstance();
		    
			if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
				String orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
				
				String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							adjTypeMap.put("amount",new BigDecimal(taxAmt));
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}else{
								adjTypeMap.put("isAssessableValue", "N");
							}
						}else{
							adjTypeMap.put("isAssessableValue", "N");
						}
					}else{
						adjTypeMap.put("isAssessableValue", "N");
					}
					
					
					
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					orderAdjustmentList.add(tempAdjMap);
				}
				
				
			   //=============now populate the values=================
				
				
				for(int j=0; j<orderAdjustmentList.size(); j++){
					Map adjMap = (Map) orderAdjustmentList.get(j);
					if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
						Map adjItemCtx = FastMap.newInstance();
						adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
						adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
						adjItemCtx.put("quantity", BigDecimal.ONE);
						adjItemCtx.put("invoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceItemSeqId", invoiceItemSeqId);
						adjItemCtx.put("costCenterId", branchId);
						if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
							adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
						}
						if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
							adjItemCtx.put("isAssessableValue", "Y");
						}
						adjItemCtx.put("userLogin", userLogin);
						
						try{
			            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", adjItemCtx);
			            	
			            	if(ServiceUtil.isError(createInvoiceItemResult)){
			            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
								return "error";
			          		}
			            } catch (Exception e) {
			            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
			    		}
					}
				}
				
			//==================End of The OrderAdjustments====================
			
				
			}
			
			
			//=======================edit Invoice Discounts=========================
			List discOrderAdjustmentList = FastList.newInstance();
			
			
			
			if (paramMap.containsKey("discOrderAdjustmentsList" + thisSuffix)) {
				String discOrderAdjustmentsListStr = (String) paramMap.get("discOrderAdjustmentsList"+ thisSuffix);
				
				////Debug.log("discOrderAdjustmentsListStr====================="+discOrderAdjustmentsListStr);
				
				String[] orderAdjustmentsList = discOrderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							BigDecimal taxAmtBd = new BigDecimal(taxAmt);
							if(taxAmtBd.compareTo(BigDecimal.ZERO)>0){
								taxAmtBd = taxAmtBd.negate();
							}
							adjTypeMap.put("amount",taxAmtBd);
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}else{
								adjTypeMap.put("isAssessableValue", "N");
							}
						}else{
							adjTypeMap.put("isAssessableValue", "N");
						}
					}else{
						adjTypeMap.put("isAssessableValue", "N");
					}
					
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					discOrderAdjustmentList.add(tempAdjMap);
					
				}
				
				////Debug.log("discOrderAdjustmentList====================="+discOrderAdjustmentList);

				//====now population===============
				
				for(int j=0; j<discOrderAdjustmentList.size(); j++){
					Map adjMap = (Map) discOrderAdjustmentList.get(j);
					
					////Debug.log("adjMap====================="+adjMap);

					if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)<0){
						Map adjItemCtx = FastMap.newInstance();
						
						////Debug.log("orderAdjustmentTypeId====================="+adjMap.get("orderAdjustmentTypeId"));

						adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
						
						////Debug.log("amount====================="+adjMap.get("amount"));
						
						adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
						adjItemCtx.put("quantity", BigDecimal.ONE);
						adjItemCtx.put("invoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceId", invoiceId);
						adjItemCtx.put("userLogin", userLogin);
						adjItemCtx.put("parentInvoiceItemSeqId", invoiceItemSeqId);
						adjItemCtx.put("costCenterId", branchId);
						////Debug.log("sourcePercentage====================="+adjMap.get("sourcePercentage"));

						
						if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
							adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
						}
						
						
						////Debug.log("isAssessableValue====================="+adjMap.get("isAssessableValue"));

						
						if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
							adjItemCtx.put("isAssessableValue", "Y");
							
							
						}
						
						////Debug.log("isAssessableValue===========3232=========="+adjMap.get("isAssessableValue"));

						////Debug.log("adjItemCtx====================="+adjItemCtx);

						
						try{
			            	Map<String, Object> createInvoiceItemResult1 = dispatcher.runSync("createInvoiceItem", adjItemCtx);
			            	
							////Debug.log("createInvoiceItemResult1====================="+createInvoiceItemResult1);
			            	
			            } catch (Exception e) {
			            	 Debug.logError(e, "Error in fetching InvoiceItem ", module);
							return "error";
			    		}
					}
				}
				
				
				
			}
			
              //============End of THe Invoice Discounts==========			
			
		}//=============loopEnd===============
		
		////Debug.log("purposeTypeId====================="+purposeTypeId);

		
	    if(UtilValidate.isNotEmpty(purposeTypeId)){
       	 	try{
    	    	GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
    	    	if(UtilValidate.isNotEmpty(tallyrefNo))
    	    	invoice.set("referenceNumber", tallyrefNo);
    	    	if(UtilValidate.isNotEmpty(invoiceDate)){
    	    		invoice.set("invoiceDate", invoiceDate);
    	    	}
    	    	
    	    	invoice.store();
    		} catch (Exception e) {
    	       Debug.logError(e, "Error in fetching InvoiceItem ", module);
   			  request.setAttribute("_ERROR_MESSAGE_", "Error in populating invoice purpose :" + invoiceId+"....! ");
   				return "error";
    		}

	    }
		
	    
	 

	    
	    
	    
	    
	    
	/*	if(UtilValidate.isNotEmpty(invoiceAdjChargesList)){
	        for (Map<String, Object> adjustMap : invoiceAdjChargesList) {
				
	        	Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	
	        	BigDecimal amount = BigDecimal.ZERO;
	        	
	        	
	        	createInvoiceItemContext.put("invoiceId",invoiceId);
	            createInvoiceItemContext.put("invoiceItemTypeId", adjustMap.get("adjustmentTypeId"));
	            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
	            //createInvoiceItemContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
	            //createInvoiceItemContext.put("description", eachItem.get("description"));
	            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
	            if (UtilValidate.isNotEmpty(adjustMap.get("assessableValue"))) {
					String isAssessableValue = (String) adjustMap.get("assessableValue");
					if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
						if(isAssessableValue.equals("TRUE") || isAssessableValue.equals("Y")){
							createInvoiceItemContext.put("isAssessableValue", "Y");
						}
					}
				}
	            
	            if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					createInvoiceItemContext.put("amount",amount);
				}
	            
	            //createInvoiceItemContext.put("productId", eachItem.get("productId"));
	            createInvoiceItemContext.put("userLogin", userLogin);
	            try{
	            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	            	
	            	if(ServiceUtil.isError(createInvoiceItemResult)){
	            		String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
	                    Debug.logError(errMsg, module);
	                    return ServiceUtil.returnError(errMsg);
						  request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
	                    
	          		}
	            } catch (Exception e) {
	            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
					return "error";
	    		}
				
			}
		
		}*/
        
		
	/*	if(UtilValidate.isNotEmpty(invoiceDiscountsList)){
		
			for (Map<String, Object> adjustMap : invoiceDiscountsList) {
				
				Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	        	BigDecimal amount = BigDecimal.ZERO;
	        	
	        	createInvoiceItemContext.put("invoiceId",invoiceId);
	            createInvoiceItemContext.put("invoiceItemTypeId", adjustMap.get("adjustmentTypeId"));
	            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
	            //createInvoiceItemContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
	            //createInvoiceItemContext.put("description", eachItem.get("description"));
	            createInvoiceItemContext.put("quantity", adjustMap.get("quantity"));
	            
	            if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					createInvoiceItemContext.put("amount",amount.negate());
				}
	            if (UtilValidate.isNotEmpty(adjustMap.get("assessableValue"))) {
					String isAssessableValue = (String) adjustMap.get("assessableValue");
					if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
						if(isAssessableValue.equals("TRUE") || isAssessableValue.equals("Y")){
							createInvoiceItemContext.put("isAssessableValue", "Y");
						}
					}
				}
	            //createInvoiceItemContext.put("productId", eachItem.get("productId"));
	            createInvoiceItemContext.put("userLogin", userLogin);
	            try{
	            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	            	if(ServiceUtil.isError(createInvoiceItemResult)){
	            		String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
	                    Debug.logError(errMsg, module);
	                    return ServiceUtil.returnError(errMsg);
						  request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
	                    
	          		}
	            } catch (Exception e) {
	            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
					return "error";
	    		}
			}
		
		}*/
		//==============update in Purchase=====================
		
  //============================================Rounding Off===============================
	    
	    List<GenericValue> InvoiceItem = null;
    	
	    List conditionList1 = FastList.newInstance();
	    conditionList1.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
	    conditionList1.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
    	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
    	 try{
    	   InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList1, EntityOperator.AND), null, null, null, false);
    	 
    	 }catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
			}
	    
	    
    	 if(UtilValidate.isNotEmpty(InvoiceItem)){
        	 
    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
    		 
        	for(GenericValue eachInvoiceItem : InvoiceItem){
        	
        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
        		BigDecimal itemValue = quantity.multiply(amount);
        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
        		
        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
        		
        		eachInvoiceItem.set("itemValue",roundedAmount);
        		eachInvoiceItem.set("costCenterId", branchId);
        		
        		try{
        		eachInvoiceItem.store();
        		}catch(GenericEntityException e){
        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
        		}
        	}
        	
        	try{
        		
        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
        		
        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
        	InvoiceHeader.store();
        	}catch(GenericEntityException e){
    			Debug.logError(e, "Failed to Populate Invoice ", module);
    		}
        	
    	 }
		
    	 try{
  			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("invoiceId", invoiceId));
  			if (ServiceUtil.isError(serviceResult)) {
  				request.setAttribute("_ERROR_MESSAGE_", "Error While Updateing Indent Summary Details");
  				return "error";
             }
   		}catch(GenericServiceException e){
 			Debug.logError(e, "Error While Updateing Indent Summary Details ", module);
 		}

		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId);	  	 
	
		return "success";
	}
	
	
	
	public static String processEditPurchaseInvoiceDEPOT(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String invoiceId = (String) request.getParameter("invoiceId");
		String tallyrefNo = (String) request.getParameter("tallyrefNo");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purchaseInvoiceId = (String) request.getParameter("purchaseInvoiceId");
		
		String purchaseTitleTransferEnumId = (String) request.getParameter("purchaseTitleTransferEnumId");
		String purchaseTaxType = (String) request.getParameter("purchaseTaxType");
		
		String purposeTypeId = "DEPOT_YARN_SALE";
	  
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
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		List<GenericValue> orderItems=null;
		try{
			orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		
		String branchId = "";
		
		 try {
				List conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "COST_CENTER_ID"));
		    	List <GenericValue> invoiceRoles = delegator.findList("InvoiceRole", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		    	if(UtilValidate.isNotEmpty(invoiceRoles)){
		    		
		    		GenericValue invoiceRoles1 = EntityUtil.getFirst(invoiceRoles);
		    		
		    		branchId = invoiceRoles1.getString("partyId");
		    		
		    	}
		 }catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		
		// Remove existing invoice items
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, UtilMisc.toList("INV_RAWPROD_ITEM")));
		List<GenericValue> invoiceItemList =null;
		try{
			invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(invoiceItemList)){
				
				for(int i=0; i<invoiceItemList.size(); i++){
					GenericValue removeItem = (GenericValue)invoiceItemList.get(i);
					
					List adjBillCondList = FastList.newInstance();
					adjBillCondList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, removeItem.get("invoiceId")));
					adjBillCondList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, removeItem.get("invoiceItemSeqId")));
					
					
					List<GenericValue> orderAdjBillings = delegator.findList("OrderAdjustmentBilling", EntityCondition.makeCondition(adjBillCondList, EntityOperator.AND), null, null, null, false);
					if(UtilValidate.isNotEmpty(orderAdjBillings)){
						delegator.removeAll(orderAdjBillings);
					}
					delegator.removeValue(removeItem);
				}
				
				
			}
		} catch (Exception e) {
   		  	Debug.logError(e, "Error in fetching InvoiceItem ", module);
			request.setAttribute("_ERROR_MESSAGE_", "Error in fetching Order Item billing :" + invoiceId+"....! ");
			return "error";
		}
		
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		List<GenericValue> InvoiceAttributeList =null;
		try{
			InvoiceAttributeList = delegator.findList("InvoiceAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(InvoiceAttributeList)){
				delegator.removeAll(InvoiceAttributeList);
			}
		} catch (Exception e) {
   		  	Debug.logError(e, "Error in fetching InvoiceAttribute ", module);
			request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceAttribute :" + invoiceId+"....! ");
			return "error";
         }


           // Invoice Attributes (Taxes)
				Map<String, Object> createInvAttribute = UtilMisc.toMap(
                        "invoiceId", invoiceId,
                        "invoiceAttrName", "purchaseTitleTransferEnumId",
                        "invoiceAttrValue", purchaseTitleTransferEnumId, 
                        "userLogin", userLogin);

                try {
                	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
                } catch (GenericServiceException gse) {
                    Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
                }
				
                createInvAttribute = UtilMisc.toMap(
                        "invoiceId", invoiceId,
                        "invoiceAttrName", "purchaseTaxType",
                        "invoiceAttrValue", purchaseTaxType, 
                        "userLogin", userLogin);

                try {
                	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
                } catch (GenericServiceException gse) {
                    Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
        }
		
		List productQtyList = FastList.newInstance();
		List<Map> invoiceAdjChargesList = FastList.newInstance();
		List<Map> invoiceDiscountsList = FastList.newInstance();
		
	    List<GenericValue> toBillItems = FastList.newInstance();
	    
	    List<String> orderItemSeqIdList = FastList.newInstance();
		
		String applicableTo = "ALL";
		String applicableToDisc = "ALL";
		for (int i = 0; i < rowCount; i++) {
			
			Map prodQtyMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String invoiceItemDiscTypeId = "";
			String adjAmtDiscStr = "";
			String discQtyStr = "";
			
			List taxRateList = FastList.newInstance();
			String assessableValue = "";
			String discAssessableValue = "";
			
			BigDecimal adjDiscAmt = BigDecimal.ZERO;
			BigDecimal discQty = BigDecimal.ZERO;
			
			String vatStr=null;
			String cstStr=null;
			
			BigDecimal vat = BigDecimal.ZERO;
			BigDecimal cst = BigDecimal.ZERO;
			//percenatge of TAXes
			
			String VatPercentStr=null;
			String CSTPercentStr=null;
			String orderItemSeq = null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO; 
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productId = "";
			String quantityStr = "";
			String unitPriceStr = "";
			String orderItemSeqId = "";
			String invoiceItemSeqId = "";
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
			if (paramMap.containsKey("assessableValue" + thisSuffix)) {
				assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
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
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemTypeId);
				invItemMap.put("amount", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
				if(UtilValidate.isNotEmpty(assessableValue) && (assessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceAdjChargesList.add(invItemMap);	
			}
			
			
			if (paramMap.containsKey("invoiceItemTypeDiscId" + thisSuffix)) {
				invoiceItemDiscTypeId = (String) paramMap.get("invoiceItemTypeDiscId" + thisSuffix);
			}
			
			if (paramMap.containsKey("applicableToDisc" + thisSuffix)) {
				applicableToDisc = (String) paramMap.get("applicableToDisc" + thisSuffix);
			}
			
			if (paramMap.containsKey("adjDiscAmt" + thisSuffix)) {
				adjAmtDiscStr = (String) paramMap.get("adjDiscAmt" + thisSuffix);
			}
			
			if (paramMap.containsKey("discQty" + thisSuffix)) {
				discQtyStr = (String) paramMap.get("discQty" + thisSuffix);
			}
			if (paramMap.containsKey("assessableValueDisc" + thisSuffix)) {
				discAssessableValue = (String) paramMap.get("assessableValueDisc" + thisSuffix);
			}
			Debug.log("discAssessableValue ============="+discAssessableValue);
			if(UtilValidate.isNotEmpty(adjAmtDiscStr)){
				try {
					adjDiscAmt = new BigDecimal(adjAmtDiscStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(discQtyStr)){
				try {
					discQty = new BigDecimal(discQtyStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + discQtyStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + discQtyStr);
					return "error";
				}
			}
			if(UtilValidate.isNotEmpty(invoiceItemDiscTypeId) && adjDiscAmt.compareTo(BigDecimal.ZERO)>0){
				
				BigDecimal adjQty = BigDecimal.ONE;
				if(UtilValidate.isNotEmpty(discQty)){
					if(discQty.doubleValue()>0){
						//adjDiscAmt = adjDiscAmt.divide(discQty);
						double tempAdjAmt = adjDiscAmt.doubleValue()/discQty.doubleValue();
						adjDiscAmt = new BigDecimal(tempAdjAmt);
						adjQty = discQty;
					}
				}
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemDiscTypeId);
				invItemMap.put("amount", adjDiscAmt);
				invItemMap.put("quantity", adjQty);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableToDisc);
				if(UtilValidate.isNotEmpty(discAssessableValue) && (discAssessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceDiscountsList.add(invItemMap);	
			}
			
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.get("productId" + thisSuffix);
			}
			
			if (paramMap.containsKey("oritemseq" + thisSuffix)) {
				orderItemSeqId = (String) paramMap.get("oritemseq" + thisSuffix);
				
				orderItemSeqIdList.add(orderItemSeqId);
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
				if (paramMap.containsKey("oritemseq" + thisSuffix)) {
					orderItemSeq = (String) paramMap.get("oritemseq" + thisSuffix);
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

				/*try {
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
				}*/
				
				if (paramMap.containsKey("invoiceItemSeqId" + thisSuffix)) {
					invoiceItemSeqId = (String) paramMap.get("invoiceItemSeqId" + thisSuffix);
				}
				Debug.log("invoiceItemSeqId =============="+invoiceItemSeqId);
				
				
				if (paramMap.containsKey("tenPercent" + thisSuffix)) {
					String tenPercentStr = (String) paramMap.get("tenPercent" + thisSuffix);
					if(UtilValidate.isNotEmpty(tenPercentStr)){
						List tenPercCondList = FastList.newInstance();
						tenPercCondList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
						tenPercCondList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
						try {
							BigDecimal tenPercentSubsidy = new BigDecimal(tenPercentStr);
							if(tenPercentSubsidy.compareTo(BigDecimal.ZERO)>0){
								tenPercentSubsidy = tenPercentSubsidy.negate();
							}
							
							List<GenericValue> tenPercItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(tenPercCondList, EntityOperator.AND), null, null, null, false);
							GenericValue tenPercItem = EntityUtil.getFirst(tenPercItemList);
							if(UtilValidate.isNotEmpty(tenPercItem)){
								tenPercItem.set("amount", tenPercentSubsidy);
								tenPercItem.store();
							}
														
						} catch (Exception e) {
							Debug.logError(e, "Problems parsing ten percent subsidy string: " + tenPercentStr, module);
							request.setAttribute("_ERROR_MESSAGE_", "Problems parsing ten percent subsidy string: " + tenPercentStr);
							return "error";
						}
					}
				}//subsidy
			}
			
			
			
			
			 //======================Edit Taxes List==================
		    
		    
		   /* if (paramMap.containsKey("taxList" + thisSuffix)) {
				String taxListStr = (String) paramMap.get("taxList"
						+ thisSuffix);
				
				Debug.log("taxListStr =============="+taxListStr);
				
				String[] taxList = taxListStr.split(",");
				for (int j = 0; j < taxList.length; j++) {
					String taxType = taxList[j];
					Debug.log("taxType =============="+taxType);
					
					Map<String, Object> createInvoiceItemContext = FastMap.newInstance();

		        	BigDecimal amount = BigDecimal.ZERO;
		        	createInvoiceItemContext.put("invoiceId",invoiceId);
		            createInvoiceItemContext.put("invoiceItemTypeId", taxType);
		            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
		            createInvoiceItemContext.put("parentInvoiceItemSeqId", invoiceItemSeqId);
		            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
		            createInvoiceItemContext.put("userLogin", userLogin);
		            if (paramMap.containsKey(taxType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(taxType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							createInvoiceItemContext.put("amount",new BigDecimal(taxAmt));
							try{
				            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
				            	
				            	if(ServiceUtil.isError(createInvoiceItemResult)){
				            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
									return "error";
				          		}
				            } catch (Exception e) {
				            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
								return "error";
				    		}
						}
					}
		            
		            
					
				}
			}
			*/
			
		     if (paramMap.containsKey("purTaxList" + thisSuffix)) {
					String purTaxListStr = (String) paramMap.get("purTaxList"
							+ thisSuffix);
					
					String[] taxList = purTaxListStr.split(",");
					for (int j = 0; j < taxList.length; j++) {
						String taxType = taxList[j];
						Map taxRateMap = FastMap.newInstance();
						String purTaxType = taxType.replace("_SALE", "_PUR");
						
						
						Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	
			        	BigDecimal amount = BigDecimal.ZERO;
			        	createInvoiceItemContext.put("invoiceId",invoiceId);
			            createInvoiceItemContext.put("invoiceItemTypeId", purTaxType);
			            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
			            createInvoiceItemContext.put("parentInvoiceItemSeqId", invoiceItemSeqId);
			            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
			            createInvoiceItemContext.put("userLogin", userLogin);
						
						
						/*taxRateMap.put("orderAdjustmentTypeId",purTaxType);
						taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
						taxRateMap.put("amount",BigDecimal.ZERO);
						//taxRateMap.put("taxAuthGeoId", partyGeoId);
	*/					
						if (paramMap.containsKey(taxType + "_PUR" + thisSuffix)) {
							String taxPercentage = (String) paramMap.get(taxType + "_PUR" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxPercentage) && !(taxPercentage.equals("NaN"))){
								 createInvoiceItemContext.put("sourcePercentage", new BigDecimal(taxPercentage));
							}
						}
						if (paramMap.containsKey(taxType+ "_PUR_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(taxType+ "_PUR_AMT" + thisSuffix);
							
							
							
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								
								if((new BigDecimal(taxAmt)).compareTo(BigDecimal.ZERO)>0){
								
								 createInvoiceItemContext.put("amount", new BigDecimal(taxAmt));
								 createInvoiceItemContext.put("costCenterId", branchId);
								 
								 try{
						            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
						            	
						            	if(ServiceUtil.isError(createInvoiceItemResult)){
						            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
											return "error";
						          		}
						            } catch (Exception e) {
						            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
										return "error";
						    		}
							}
							
							}
						}
						
					}
				}

			
			
			
			
			
			
			
			
			
			
			//=======================edit invoice Adjustmnets=========================
			
		    List orderAdjustmentList = FastList.newInstance();
		    
			if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
				String orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
				
				String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							adjTypeMap.put("amount",new BigDecimal(taxAmt));
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}else{
								adjTypeMap.put("isAssessableValue", "N");
							}
						}else{
							adjTypeMap.put("isAssessableValue", "N");
						}
					}else{
						adjTypeMap.put("isAssessableValue", "N");
					}
					
					
					
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					orderAdjustmentList.add(tempAdjMap);
				}
				
				
			   //=============now populate the values=================
				
				
				for(int j=0; j<orderAdjustmentList.size(); j++){
					Map adjMap = (Map) orderAdjustmentList.get(j);
					if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
						Map adjItemCtx = FastMap.newInstance();
						adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
						adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
						adjItemCtx.put("quantity", BigDecimal.ONE);
						adjItemCtx.put("invoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceItemSeqId", invoiceItemSeqId);
						adjItemCtx.put("costCenterId", branchId);
						if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
							adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
						}
						if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
							adjItemCtx.put("isAssessableValue", "Y");
						}
						adjItemCtx.put("userLogin", userLogin);
						
						try{
			            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", adjItemCtx);
			            	
			            	if(ServiceUtil.isError(createInvoiceItemResult)){
			            		request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
								return "error";
			          		}
			            } catch (Exception e) {
			            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
			    		}
					}
				}
				
			//==================End of The OrderAdjustments====================
			
				
			}
			
			
			//=======================edit Invoice Discounts=========================
			List discOrderAdjustmentList = FastList.newInstance();
			
			
			
			if (paramMap.containsKey("discOrderAdjustmentsList" + thisSuffix)) {
				String discOrderAdjustmentsListStr = (String) paramMap.get("discOrderAdjustmentsList"+ thisSuffix);
				
				Debug.log("discOrderAdjustmentsListStr====================="+discOrderAdjustmentsListStr);
				
				String[] orderAdjustmentsList = discOrderAdjustmentsListStr.split(",");
				for (int j = 0; j < orderAdjustmentsList.length; j++) {
					String orderAdjustmentType = orderAdjustmentsList[j];
					Map adjTypeMap = FastMap.newInstance();
					adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
					adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
					adjTypeMap.put("amount",BigDecimal.ZERO);
					//adjTypeMap.put("taxAuthGeoId", partyGeoId);
					
					if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
						String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
						if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
							adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
						}
						
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
						String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
						if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
							BigDecimal taxAmtBd = new BigDecimal(taxAmt);
							if(taxAmtBd.compareTo(BigDecimal.ZERO)>0){
								taxAmtBd = taxAmtBd.negate();
							}
							adjTypeMap.put("amount",taxAmtBd);
						}
					}
					if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
						String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
						if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
							if(isAssessableValue.equals("TRUE")){
								adjTypeMap.put("isAssessableValue", "Y");
								//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
							}else{
								adjTypeMap.put("isAssessableValue", "N");
							}
						}else{
							adjTypeMap.put("isAssessableValue", "N");
						}
					}else{
						adjTypeMap.put("isAssessableValue", "N");
					}
					
					Map tempAdjMap = FastMap.newInstance();
					tempAdjMap.putAll(adjTypeMap);
					
					discOrderAdjustmentList.add(tempAdjMap);
					
				}
				
				Debug.log("discOrderAdjustmentList====================="+discOrderAdjustmentList);

				//====now population===============
				
				for(int j=0; j<discOrderAdjustmentList.size(); j++){
					Map adjMap = (Map) discOrderAdjustmentList.get(j);
					
					Debug.log("adjMap====================="+adjMap);

					if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)<0){
						Map adjItemCtx = FastMap.newInstance();
						
						Debug.log("orderAdjustmentTypeId====================="+adjMap.get("orderAdjustmentTypeId"));

						adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
						
						Debug.log("amount====================="+adjMap.get("amount"));
						
						adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
						adjItemCtx.put("quantity", BigDecimal.ONE);
						adjItemCtx.put("invoiceId", invoiceId);
						adjItemCtx.put("parentInvoiceId", invoiceId);
						adjItemCtx.put("userLogin", userLogin);
						adjItemCtx.put("parentInvoiceItemSeqId", invoiceItemSeqId);
						adjItemCtx.put("costCenterId", branchId);
						Debug.log("sourcePercentage====================="+adjMap.get("sourcePercentage"));

						
						if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
							adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
						}
						
						
						Debug.log("isAssessableValue====================="+adjMap.get("isAssessableValue"));

						
						if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
							adjItemCtx.put("isAssessableValue", "Y");
							
							
						}
						
						Debug.log("isAssessableValue===========3232=========="+adjMap.get("isAssessableValue"));

						Debug.log("adjItemCtx====================="+adjItemCtx);

						
						try{
			            	Map<String, Object> createInvoiceItemResult1 = dispatcher.runSync("createInvoiceItem", adjItemCtx);
			            	
							Debug.log("createInvoiceItemResult1====================="+createInvoiceItemResult1);
			            	
			            } catch (Exception e) {
			            	 Debug.logError(e, "Error in fetching InvoiceItem ", module);
							return "error";
			    		}
					}
				}
				
				
				
			}
			
              //============End of THe Invoice Discounts==========			
			
		}//=============loopEnd===============
		
		Debug.log("purposeTypeId====================="+purposeTypeId);

		
	    if(UtilValidate.isNotEmpty(purposeTypeId)){
       	 	try{
    	    	GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
    	    	if(UtilValidate.isNotEmpty(tallyrefNo))
    	    	invoice.set("referenceNumber", tallyrefNo);
    	    	if(UtilValidate.isNotEmpty(invoiceDate)){
    	    		invoice.set("invoiceDate", invoiceDate);
    	    	}
    	    	
    	    	invoice.store();
    		} catch (Exception e) {
    	       Debug.logError(e, "Error in fetching InvoiceItem ", module);
   			  request.setAttribute("_ERROR_MESSAGE_", "Error in populating invoice purpose :" + invoiceId+"....! ");
   				return "error";
    		}

	    }
		
	    
	 

	    
	    
	    
	    
	    
	/*	if(UtilValidate.isNotEmpty(invoiceAdjChargesList)){
	        for (Map<String, Object> adjustMap : invoiceAdjChargesList) {
				
	        	Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	
	        	BigDecimal amount = BigDecimal.ZERO;
	        	
	        	
	        	createInvoiceItemContext.put("invoiceId",invoiceId);
	            createInvoiceItemContext.put("invoiceItemTypeId", adjustMap.get("adjustmentTypeId"));
	            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
	            //createInvoiceItemContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
	            //createInvoiceItemContext.put("description", eachItem.get("description"));
	            createInvoiceItemContext.put("quantity", BigDecimal.ONE);
	            if (UtilValidate.isNotEmpty(adjustMap.get("assessableValue"))) {
					String isAssessableValue = (String) adjustMap.get("assessableValue");
					if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
						if(isAssessableValue.equals("TRUE") || isAssessableValue.equals("Y")){
							createInvoiceItemContext.put("isAssessableValue", "Y");
						}
					}
				}
	            
	            if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					createInvoiceItemContext.put("amount",amount);
				}
	            
	            //createInvoiceItemContext.put("productId", eachItem.get("productId"));
	            createInvoiceItemContext.put("userLogin", userLogin);
	            try{
	            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	            	
	            	if(ServiceUtil.isError(createInvoiceItemResult)){
	            		String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
	                    Debug.logError(errMsg, module);
	                    return ServiceUtil.returnError(errMsg);
						  request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
	                    
	          		}
	            } catch (Exception e) {
	            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
					return "error";
	    		}
				
			}
		
		}*/
        
		
	/*	if(UtilValidate.isNotEmpty(invoiceDiscountsList)){
		
			for (Map<String, Object> adjustMap : invoiceDiscountsList) {
				
				Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	        	BigDecimal amount = BigDecimal.ZERO;
	        	
	        	createInvoiceItemContext.put("invoiceId",invoiceId);
	            createInvoiceItemContext.put("invoiceItemTypeId", adjustMap.get("adjustmentTypeId"));
	            createInvoiceItemContext.put("parentInvoiceId", invoiceId);
	            //createInvoiceItemContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
	            //createInvoiceItemContext.put("description", eachItem.get("description"));
	            createInvoiceItemContext.put("quantity", adjustMap.get("quantity"));
	            
	            if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					createInvoiceItemContext.put("amount",amount.negate());
				}
	            if (UtilValidate.isNotEmpty(adjustMap.get("assessableValue"))) {
					String isAssessableValue = (String) adjustMap.get("assessableValue");
					if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
						if(isAssessableValue.equals("TRUE") || isAssessableValue.equals("Y")){
							createInvoiceItemContext.put("isAssessableValue", "Y");
						}
					}
				}
	            //createInvoiceItemContext.put("productId", eachItem.get("productId"));
	            createInvoiceItemContext.put("userLogin", userLogin);
	            try{
	            	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	            	if(ServiceUtil.isError(createInvoiceItemResult)){
	            		String errMsg = UtilProperties.getMessage(resource, "AccountingTroubleCallingCreateInvoiceForOrderService", locale);
	                    Debug.logError(errMsg, module);
	                    return ServiceUtil.returnError(errMsg);
						  request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
							return "error";
	                    
	          		}
	            } catch (Exception e) {
	            	request.setAttribute("_ERROR_MESSAGE_", "Error in populating InvoiceItem : ");
					return "error";
	    		}
			}
		
		}*/
		//==============update in Purchase=====================
		
		
		
		
  //============================================Rounding Off===============================
	    
	    List<GenericValue> InvoiceItem = null;
    	
	    List conditionList1 = FastList.newInstance();
	    conditionList1.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
	    conditionList1.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
    	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
    	 try{
    	   InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList1, EntityOperator.AND), null, null, null, false);
    	 
    	 }catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
			}
	    
	    
    	 if(UtilValidate.isNotEmpty(InvoiceItem)){
        	 
    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
    		 
        	for(GenericValue eachInvoiceItem : InvoiceItem){
        	
        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
        		BigDecimal itemValue = quantity.multiply(amount);
        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
        		
        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
        		
        		eachInvoiceItem.set("itemValue",roundedAmount);
        		eachInvoiceItem.set("costCenterId",branchId);
        		
        		try{
        		eachInvoiceItem.store();
        		}catch(GenericEntityException e){
        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
        		}
        	}
        	
        	try{
        		
        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
        		
        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
        	InvoiceHeader.store();
        	}catch(GenericEntityException e){
    			Debug.logError(e, "Failed to Populate Invoice ", module);
    		}
        	
    	 }
		
    	 
    	//============================================Rounding Off===============================
			  List<GenericValue> InvoiceItemR = null;
		    	
			    conditionList.clear();
		    	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		    	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
		    	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
		    	 try{
		    		 InvoiceItemR = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		    	 
		    	 }catch(GenericEntityException e){
						Debug.logError(e, "Failed to retrive InvoiceItem ", module);
					}
			    
		    	
		    	 if(UtilValidate.isNotEmpty(InvoiceItemR)){
		        	 
		    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
		    		 
		        	for(GenericValue eachInvoiceItem : InvoiceItemR){
		        	
		        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
		        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
		        		BigDecimal itemValue = quantity.multiply(amount);
		        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
		        		
		        		
		        		
		        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
		        		
		        		eachInvoiceItem.set("itemValue",roundedAmount);
		        		eachInvoiceItem.set("costCenterId", branchId);
		        		try{
		        		eachInvoiceItem.store();
		        		}catch(GenericEntityException e){
		        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
		        		}
		        	}
		        	
		        	try{
		        		
		        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
		        		
		        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
		        	InvoiceHeader.store();
		        	}catch(GenericEntityException e){
		    			Debug.logError(e, "Failed to Populate Invoice ", module);
		    		}
		        	
		    	 }
		    	 
			

		    	 //================= get purchase Invoice Details===============================
		    	  		   
		    	             conditionList.clear();
		    	  			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		    	  			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, UtilMisc.toList("INV_RAWPROD_ITEM","TEN_PERCENT_SUBSIDY")));
		    	  			List<GenericValue> invoiceItemListAdj =null;
		    	  			
		    	  			try{
		    	  				invoiceItemListAdj = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		    	  		      
		    	  			} catch (Exception e) {
		    	  	   		  Debug.logError(e, "Error in fetching InvoiceItem ", module);
		    	  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
		    	  					return "error";
		    	  			}

		    	  			if(UtilValidate.isNotEmpty(invoiceItemListAdj)){
		    	  			
		    	  			
		    	  			
		    	  			BigDecimal invoiceAdjTotal = BigDecimal.ZERO;
		    	  			for (int i = 0; i < invoiceItemListAdj.size(); i++) {
		    	  				
		    					GenericValue eachInvoiceList = (GenericValue)invoiceItemListAdj.get(i);
		    					invoiceAdjTotal = invoiceAdjTotal.add(eachInvoiceList.getBigDecimal("amount"));
		    	           
		    	  			}
		    	  			
		    	  			Debug.log("invoiceItemListAdj=========vamsi==============="+invoiceItemListAdj);
		    	  			
		    	  			conditionList.clear();
		    	  			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		    	  			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_RAWPROD_ITEM"));
		    	  			List<GenericValue> invoiceItemList1 =null;
		    	  			
		    	  			try{
		    	  				invoiceItemList1 = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		    	  		      
		    	  			} catch (Exception e) {
		    	  	   		  Debug.logError(e, "Error in fetching InvoiceItem ", module);
		    	  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
		    	  					return "error";
		    	  			}
		    	  			
		    	  			
		    	  		Debug.log("invoiceItemList=========vamsi==============="+invoiceItemList1);
		    	  			
		    	             for (int i = 0; i < invoiceItemList1.size(); i++) {
		    	  				
		    					GenericValue eachInvoiceList = (GenericValue)invoiceItemList1.get(i);
		    					
		    					String invoiceIdItem = eachInvoiceList.getString("invoiceId");
		    					
		    					String invoiceItemSeqIdItem = eachInvoiceList.getString("invoiceItemSeqId");
		    					
		    					BigDecimal quantity = eachInvoiceList.getBigDecimal("quantity");
		    					
		    					BigDecimal amount = eachInvoiceList.getBigDecimal("amount");
		    					
		    					BigDecimal itemTotal = quantity.multiply(amount);
		    					
		    					Debug.log("itemTotal=============="+itemTotal);
		    					
		    					Debug.log("invoiceAdjTotal=============="+invoiceAdjTotal);
		    					
		    					
		    					conditionList.clear();
	  		    	  			conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, invoiceId));
	  		    	  		    conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqIdItem));
	  		    	  		    conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
	  		    	  		
	  		    	  			List<GenericValue> invoiceItemAdjList =null;
	  		    	  			
	  		    	  			try{
	  		    	  		      invoiceItemAdjList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	  		    	  		      
	  		    	  			} catch (Exception e) {
	  		    	  	   		  Debug.logError(e, "Error in fetching InvoiceItem ", module);
	  		    	  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
	  		    	  					return "error";
	  		    	  			}
	  		    	  			
	  		    	  		Debug.log("invoiceItemAdjList=======2323==vamsi==============="+invoiceItemAdjList);
	  		    	  			
	  		    	  		    BigDecimal AdjWithInvoAmt = BigDecimal.ZERO;
		    					
	  		    	  			for (GenericValue eachItem : invoiceItemAdjList) {
	  		    	  			   AdjWithInvoAmt = AdjWithInvoAmt.add(eachItem.getBigDecimal("itemValue"));
							}
	  		    	  			
	  		    	  		Debug.log("AdjWithInvoAmt=======2323==vamsi==============="+AdjWithInvoAmt);
		    					
	  		    	  		BigDecimal addToInventory = BigDecimal.ZERO;
	  		    	  		
	  		    	  	    double addToUnitPrice = 0;
	  		    	  	      addToUnitPrice = AdjWithInvoAmt.doubleValue()/quantity.doubleValue();
	  		    	  	      addToInventory = new BigDecimal(addToUnitPrice);
		    					
		    					//=====================get relevent Order and Seq==========================
		    					
		    					conditionList.clear();
		    		  			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceIdItem));
		    		  			conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqIdItem));
		    		  			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		    		  			List<GenericValue> OrderItemBillingAndInvoiceAndInvoiceItemList =null;
		    		  			
		    		  			try{
		    		  				OrderItemBillingAndInvoiceAndInvoiceItemList = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		    		  		      
		    		  			} catch (Exception e) {
		    		  	   		  //Debug.logError(e, "Error in fetching InvoiceItem ", module);
		    		  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
		    		  					return "error";
		    		  			}
		    					
		    		  			GenericValue OrderItemBillingAndInvoiceAndInvoice = EntityUtil.getFirst(OrderItemBillingAndInvoiceAndInvoiceItemList);
		    					
		    		  			String orderIdBill = "";
		    		  			String orderItemSeqIdBill = "";
		    		  			if(UtilValidate.isNotEmpty(OrderItemBillingAndInvoiceAndInvoice)){
		    		  				orderIdBill = (String)OrderItemBillingAndInvoiceAndInvoice.get("orderId");
		    		  				orderItemSeqIdBill = (String)OrderItemBillingAndInvoiceAndInvoice.get("orderItemSeqId");
		    		  			}
		    		  			
		    		  			conditionList.clear();
		    		  			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderIdBill));
		    		  			conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqIdBill));
		    		  			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
		    		  			List<GenericValue> ShipmentAndReceiptList =null;
		    		  			
		    		  			try{
		    		  				ShipmentAndReceiptList = delegator.findList("ShipmentAndReceipt", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		    		  		      
		    		  			} catch (Exception e) {
		    		  	   		  //Debug.logError(e, "Error in fetching InvoiceItem ", module);
		    		  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
		    		  					return "error";
		    		  			}
		    	                GenericValue ShipmentAndReceipt = EntityUtil.getFirst(ShipmentAndReceiptList);
		    	      			Debug.log("addToInventory====================="+addToInventory);

		    		  			String inventoryItemId = "";
		    		  			if(UtilValidate.isNotEmpty(ShipmentAndReceipt)){
		    		  				inventoryItemId = (String)ShipmentAndReceipt.get("inventoryItemId");
		    		  			}
		    	      			Debug.log("inventoryItemId====================="+inventoryItemId);

		    		  			// Map<String, Object> inventoryItemMap = UtilMisc.toMap("inventoryItemId", inventoryItemId,"unitCost",addToInventory, "userLogin", userLogin);
		    		  	          try {
		    		  	              // result = dispatcher.runSync("updateInventoryItem", inventoryItemMap);
		    		  	      			////Debug.log("result====================="+result);
		    			  	      		////Debug.log("inventoryItemId====================="+inventoryItemId);

		    		  	      		 GenericValue InventoryItem = delegator.findOne("InventoryItem",UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
		    		  	   		
		    		  	      		 BigDecimal availablePrice = InventoryItem.getBigDecimal("unitCost");
		    		  	      		 
		    		  	      	     Debug.log("amount====================="+amount);

		    		  	      		 
		    		  	      	     Debug.log("InventoryItem====================="+InventoryItem);
		    		  	      		InventoryItem.set("unitCost",addToInventory.add(amount));
		    		  	      	    InventoryItem.store();
		    		  	               
		    		  	          } catch (Exception e) {
		    		  	        	  
		    		  	        	 //Debug.logError(e, "Error while populating updateInventoryItem ", module);
		    		  				  request.setAttribute("_ERROR_MESSAGE_", "Error while populating updateInventoryItem :" + invoiceId+"....! ");
		    		  					return "error";
		    		  	          }
		    	  			 }
		    	  			
		    	  		  } 
		    	 
    	 
    	 
    	 
    	 
		

		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId);	  	 
	
		return "success";
	}
	
	
	
	public static String processDepotSalesInvoice(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		//String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String partyIdFrom = "";
		//String shipmentId = (String) request.getParameter("shipmentId");
	  
		String strInvoiceId = (String) request.getParameter("invoiceId");
		
		GenericValue shipment =null;
		GenericValue invoiceList=null;
		
		try{
		 invoiceList = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", strInvoiceId), false);
		
		 shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId",invoiceList.get("shipmentId")), false);
		}catch (Exception e) {
			Debug.logError(e, "Problems while changing Status: " + strInvoiceId, module);
			request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + strInvoiceId);
			return "error";
		}
		
		String partyId = (String)shipment.getString("partyIdTo");
		
		String shipmentId = (String)shipment.getString("shipmentId");
		
		String primaryOrderId = (String)shipment.getString("primaryOrderId");
		
		if(UtilValidate.isNotEmpty(shipment) && shipment.equals("SHIPMENT_CANCELLED")){
			Debug.logError("Unable to generate Shipment: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate shipment  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";	
			}
		
		
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
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
	           
	//invoiceitemmmmm
	           
	           List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, strInvoiceId));
				EntityCondition ficondExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> invoiceItemList =null;
				
				try{
			      invoiceItemList = delegator.findList("InvoiceItem", ficondExpr, null, null, null, false);
				} catch (Exception e) {
					Debug.logError(e, "Problems while getting invoiceItems : " + invoiceId, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems while getting invoiceItems : " + invoiceId);
					return "error";
				}
				
				for (int i = 0; i < invoiceItemList.size(); i++) {
					
				
					GenericValue eachInvoiceList = (GenericValue)invoiceItemList.get(i);
					
			  grandTotal = grandTotal.add(eachInvoiceList.getBigDecimal("amount").multiply(eachInvoiceList.getBigDecimal("quantity")));
	           Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	           createInvoiceItemContext.put("invoiceId",invoiceId);
	           createInvoiceItemContext.put("invoiceItemTypeId", eachInvoiceList.get("invoiceItemTypeId"));
	           createInvoiceItemContext.put("description", eachInvoiceList.get("description"));
	           createInvoiceItemContext.put("quantity",eachInvoiceList.get("quantity"));
	           createInvoiceItemContext.put("amount",eachInvoiceList.get("amount"));
	           createInvoiceItemContext.put("productId", eachInvoiceList.get("productId"));
	           createInvoiceItemContext.put("unitPrice", eachInvoiceList.get("unitPrice"));
	           createInvoiceItemContext.put("unitListPrice", eachInvoiceList.get("unitListPrice"));
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
	        Debug.log("countryCode ===="+countryCode);
	        Debug.log("contactNumberTo = "+contactNumberTo);
	        /*if(UtilValidate.isEmpty(contactNumberTo)){
	        	contactNumberTo = "7330776928";
	        }*/
	        //contactNumberTo = "7330776928";
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
	        	 
	        	 Debug.log("invoiceMsgToWeaver =============="+invoiceMsgToWeaver);
	        	 
	        	 
	        	 
	        	 Debug.log("contactNumberTo ===== "+contactNumberTo);
	        	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	             sendSmsParams.put("contactNumberTo", contactNumberTo);
	             sendSmsParams.put("text", invoiceMsgToWeaver); 
	             Debug.log("sendSmsParams========================"+sendSmsParams);
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
		
		return "success";

	}  
	
	
	
	
	public static Map<String, Object> createDepotInvoice(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		    Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    Map<String, Object> result = ServiceUtil.returnSuccess();
		    List<Map> productQtyList = (List) context.get("productQtyList");
		    //List<Map> invoiceAdjChargesList = (List) context.get("invoiceAdjChargesList");
		    //List<Map> invoiceDiscountsList = (List) context.get("invoiceDiscountsList");
		    
		    Timestamp invoiceDate = (Timestamp) context.get("invoiceDate");
		    Locale locale = (Locale) context.get("locale");
		  	String purposeTypeId = (String) context.get("purposeTypeId");
		  	String vehicleId = (String) context.get("vehicleId");
		  	String partyIdFrom = (String) context.get("partyId");
		  	String orderId = (String) context.get("orderId");
		  	String isDisableAcctg = (String) context.get("isDisableAcctg");
		  	String shipmentId = (String) context.get("shipmentId");
		  	String tallyrefNo = (String) context.get("tallyrefNo");
		  	String purposeTypeIdFromDc = (String) context.get("purposeTypeIdFromDc");
		  	
			String purchaseTitleTransferEnumId = (String) context.get("purchaseTitleTransferEnumId");
			String purchaseTaxType = (String) context.get("purchaseTaxType");
		  	
		  	boolean beganTransaction = false;
		  	String currencyUomId = "INR";
			Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
			String partyId="Company";
	        List<GenericValue> orderParty = null;  

			try {
				orderParty = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR"));
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
				conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.NOT_EQUAL, "SALES_INVOICE"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> invoices = delegator.findList("Invoice", condition, null, null, null, false);
				
				if(UtilValidate.isNotEmpty(invoices)){
					Debug.logError("Invoices already generated for shipment : "+shipmentId, module);
					return ServiceUtil.returnError("Invoices already generated for shipment : "+shipmentId);
				}
				
				/*Map resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", productQtyList, "otherCharges", invoiceAdjChargesList, "userLogin", userLogin, "incTax", ""));
				if(ServiceUtil.isError(resultCtx)){
	  		  		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
	  		  		Debug.logError(errMsg , module);
	  		  		return ServiceUtil.returnError(errMsg);
				}*/
				//Debug.log("#####resultCtx#########"+resultCtx);
				//List<Map> itemDetails = (List)resultCtx.get("itemDetail");
				//List<Map> adjustmentDetail = (List)resultCtx.get("adjustmentDetail");
				
				//invoiceDiscountsList
				
				
				//===========get RO for branch================
	            
	            String roFroBranch = "";
	            conditionList.clear();
    			conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdFrom));
    			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT" ));
    	        conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
    	        conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(invoiceDate)));
    			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
    					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(invoiceDate))));
				EntityCondition conditionRel = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  	
				try{
					List<GenericValue> orgsListS = delegator.findList("PartyRelationship", conditionRel, null, UtilMisc.toList("partyIdFrom"), null, false);
					GenericValue orgsList = EntityUtil.getFirst(orgsListS);
					roFroBranch = orgsList.getString("partyIdFrom");
					
	   	    	}catch (GenericEntityException e) {
	   				// TODO: handle exception
	   	    		Debug.logError(e, module);
	   			}

				//Debug.log("roFroBranch===================="+roFroBranch);
				
				
				Map input = FastMap.newInstance();
				input.put("userLogin", userLogin);
		        input.put("invoiceTypeId", "PURCHASE_INVOICE");        
		        input.put("partyIdFrom", partyId);	
		        if(UtilValidate.isNotEmpty(tallyrefNo))
		        input.put("referenceNumber", tallyrefNo);	
		        input.put("statusId", "INVOICE_IN_PROCESS");	
		        input.put("currencyUomId", currencyUomId);
		        input.put("invoiceDate", invoiceDate);
		        input.put("dueDate", invoiceDate); 	        
		        input.put("partyId",roFroBranch );
		        input.put("costCenterId",partyIdFrom);
		        input.put("shipmentId",shipmentId );
		        if(UtilValidate.isNotEmpty(purposeTypeIdFromDc))
		        input.put("purposeTypeId", purposeTypeIdFromDc);	
		        else
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
				
				
				// Invoice Attributes (Taxes)
				Map<String, Object> createInvAttribute = UtilMisc.toMap(
                        "invoiceId", invoiceId,
                        "invoiceAttrName", "purchaseTitleTransferEnumId",
                        "invoiceAttrValue", purchaseTitleTransferEnumId, 
                        "userLogin", userLogin);

                try {
                	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
                } catch (GenericServiceException gse) {
                    Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
                }
				
                createInvAttribute = UtilMisc.toMap(
                        "invoiceId", invoiceId,
                        "invoiceAttrName", "purchaseTaxType",
                        "invoiceAttrValue", purchaseTaxType, 
                        "userLogin", userLogin);

                try {
                	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
                } catch (GenericServiceException gse) {
                    Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
                }
				
				Map itemSeqMap = FastMap.newInstance();
				int i=0;
				for (Map<String, Object> prodQtyMap : productQtyList) {
					
					String productId = "";
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal amount = BigDecimal.ZERO;
					Map invoiceItemCtx = FastMap.newInstance();
					//Map vatItemCtx = FastMap.newInstance();
					//Map cstItemCtx = FastMap.newInstance();
					Map taxItemCtx = FastMap.newInstance();
					
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
						/*if(UtilValidate.isNotEmpty(prodQtyMap.get("bedUnitRate"))){
							unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedUnitRate"));
						}
						if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessUnitRate"))){
							unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedcessUnitRate"));
						}
						if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessUnitRate"))){
							unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedseccessUnitRate"));
						}*/
						invoiceItemCtx.put("amount", unitPrice);
						invoiceItemCtx.put("unitPrice", unitPrice);
						
					}
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
						unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
						invoiceItemCtx.put("unitListPrice", unitListPrice);
					}*/
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
						BigDecimal vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
						if(vatPercent.compareTo(BigDecimal.ZERO)>0){
							vatItemCtx.put("vatPercent", vatPercent);
						}
					}*/
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
						BigDecimal vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
						if(vatAmount.compareTo(BigDecimal.ZERO)>0){
							vatItemCtx.put("invoiceItemTypeId", "VAT_PUR");
							vatItemCtx.put("amount", vatAmount);
							vatItemCtx.put("quantity", BigDecimal.ONE);
							vatItemCtx.put("description", "VAT");
						}
						
					}*/
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
						BigDecimal cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
						if(cstPercent.compareTo(BigDecimal.ZERO)>0){
							invoiceItemCtx.put("cstPercent", cstPercent);
						}
					}*/
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
						BigDecimal cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
						if(cstAmount.compareTo(BigDecimal.ZERO)>0){
							cstItemCtx.put("invoiceItemTypeId", "CST_PUR");
							cstItemCtx.put("amount", cstAmount);
							cstItemCtx.put("quantity", BigDecimal.ONE);
							cstItemCtx.put("description", "CST");
						}
					}*/
					invoiceItemCtx.put("invoiceId", invoiceId);
					invoiceItemCtx.put("invoiceItemTypeId", "INV_RAWPROD_ITEM");
					invoiceItemCtx.put("costCenterId", partyIdFrom);
					invoiceItemCtx.put("userLogin", userLogin);
					result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
					
					if (ServiceUtil.isError(result)) {
						Debug.logError("Error creating Invoice item for product : "+productId, module);	
						return ServiceUtil.returnError("Error creating Invoice item for product : "+productId);
					}
					String invItemSeqId = (String) result.get("invoiceItemSeqId");
					
					
					GenericValue shipmentRece = (GenericValue)shipmentReceipts.get(i);
					String orderItemSeqNo = (String)shipmentRece.get("orderItemSeqId");
					
					Map<String, Object> createOrderItemBillingContext = FastMap.newInstance();
	                createOrderItemBillingContext.put("invoiceId", invoiceId);
	                createOrderItemBillingContext.put("invoiceItemSeqId", invItemSeqId);
	                createOrderItemBillingContext.put("orderId", orderId);
	                createOrderItemBillingContext.put("orderItemSeqId", orderItemSeqNo);
	               /* //createOrderItemBillingContext.put("itemIssuanceId", itemIssuanceId);
	                createOrderItemBillingContext.put("quantity", billingQuantity);
	                createOrderItemBillingContext.put("amount", billingAmount);
*/	                createOrderItemBillingContext.put("userLogin", userLogin);
	               /* if ((shipmentReceipt != null) && (shipmentReceipt.getString("receiptId") != null)) {
	                    createOrderItemBillingContext.put("shipmentReceiptId", shipmentReceipt.getString("receiptId"));
	                }*/

	                Map<String, Object> createOrderItemBillingResult = dispatcher.runSync("createOrderItemBilling", createOrderItemBillingContext);
	                if (ServiceUtil.isError(createOrderItemBillingResult)) {
	                	Debug.logError("Error creating OrderItem Billing", module);	
						return ServiceUtil.returnError("Error creating OrderItem Billing");
	                }
					
					itemSeqMap.put(productId, invItemSeqId);
					
					List purTaxRateList = FastList.newInstance();
					if(UtilValidate.isNotEmpty(prodQtyMap.get("purTaxRateList"))){
						purTaxRateList = (List)prodQtyMap.get("purTaxRateList");
					}
					
					for(int j=0; j<purTaxRateList.size(); j++){
						Map taxMap = (Map) purTaxRateList.get(j);
						if(  ((BigDecimal) taxMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
							taxItemCtx.put("invoiceItemTypeId", taxMap.get("orderAdjustmentTypeId"));
							taxItemCtx.put("amount", (BigDecimal) taxMap.get("amount"));
							taxItemCtx.put("quantity", BigDecimal.ONE);
							//vatItemCtx.put("description", "VAT");
							
							taxItemCtx.put("invoiceId", invoiceId);
							taxItemCtx.put("parentInvoiceId", invoiceId);
							taxItemCtx.put("parentInvoiceItemSeqId", invItemSeqId);
							taxItemCtx.put("costCenterId", partyIdFrom);
							taxItemCtx.put("sourcePercentage", (BigDecimal) taxMap.get("sourcePercentage"));
							taxItemCtx.put("userLogin", userLogin);
							result = dispatcher.runSync("createInvoiceItem", taxItemCtx);
							if (ServiceUtil.isError(result)) {
								Debug.logError("Error creating Invoice item for Item : VAT PUR", module);	
								return ServiceUtil.returnError("Error creating Invoice item for Item : VAT PUR");
							}
						}
					}
					
					List orderAdjustmentList = FastList.newInstance();
					if(UtilValidate.isNotEmpty(prodQtyMap.get("orderAdjustmentList"))){
						orderAdjustmentList = (List)prodQtyMap.get("orderAdjustmentList");
					}
					
					for(int j=0; j<orderAdjustmentList.size(); j++){
						Map adjMap = (Map) orderAdjustmentList.get(j);
						if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
							Map adjItemCtx = FastMap.newInstance();
							adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
							adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
							adjItemCtx.put("quantity", BigDecimal.ONE);
							adjItemCtx.put("invoiceId", invoiceId);
							adjItemCtx.put("parentInvoiceId", invoiceId);
							adjItemCtx.put("parentInvoiceItemSeqId", invItemSeqId);
							adjItemCtx.put("costCenterId", partyIdFrom);
							if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
								adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
							}
							if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
								adjItemCtx.put("isAssessableValue", "Y");
							}
							adjItemCtx.put("userLogin", userLogin);
							result = dispatcher.runSync("createInvoiceItem", adjItemCtx);
							if (ServiceUtil.isError(result)) {
								Debug.logError("Error creating Invoice item for Item : VAT PUR", module);	
								return ServiceUtil.returnError("Error creating Invoice item for Item : VAT PUR");
							}
						}
					}
					
					List discOrderAdjustmentList = FastList.newInstance();
					if(UtilValidate.isNotEmpty(prodQtyMap.get("discOrderAdjustmentList"))){
						discOrderAdjustmentList = (List)prodQtyMap.get("discOrderAdjustmentList");
					}
					
					for(int j=0; j<discOrderAdjustmentList.size(); j++){
						Map adjMap = (Map) discOrderAdjustmentList.get(j);
						if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)<0){
							Map adjItemCtx = FastMap.newInstance();
							adjItemCtx.put("invoiceItemTypeId", adjMap.get("orderAdjustmentTypeId"));
							adjItemCtx.put("amount", (BigDecimal) adjMap.get("amount"));
							adjItemCtx.put("quantity", BigDecimal.ONE);
							adjItemCtx.put("invoiceId", invoiceId);
							adjItemCtx.put("parentInvoiceId", invoiceId);
							adjItemCtx.put("parentInvoiceItemSeqId", invItemSeqId);
							adjItemCtx.put("costCenterId", partyIdFrom);
							if(UtilValidate.isNotEmpty(adjMap.get("sourcePercentage"))){
								adjItemCtx.put("sourcePercentage", adjMap.get("sourcePercentage"));
							}
							if(UtilValidate.isNotEmpty(adjMap.get("isAssessableValue")) && ( (adjMap.get("isAssessableValue")).equals("Y"))  ){
								adjItemCtx.put("isAssessableValue", "Y");
							}
							adjItemCtx.put("userLogin", userLogin);
							result = dispatcher.runSync("createInvoiceItem", adjItemCtx);
							if (ServiceUtil.isError(result)) {
								Debug.logError("Error creating Invoice item for Item : VAT PUR", module);	
								return ServiceUtil.returnError("Error creating Invoice item for Item : VAT PUR");
							}
						}
					}
					
					
					
					/*
					if(UtilValidate.isNotEmpty(vatItemCtx)){
						vatItemCtx.put("invoiceId", invoiceId);
						vatItemCtx.put("parentInvoiceId", invoiceId);
						vatItemCtx.put("parentInvoiceItemSeqId", invItemSeqId);
						vatItemCtx.put("userLogin", userLogin);
						result = dispatcher.runSync("createInvoiceItem", vatItemCtx);
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Item : VAT PUR", module);	
							return ServiceUtil.returnError("Error creating Invoice item for Item : VAT PUR");
						}
					}
					
					if(UtilValidate.isNotEmpty(cstItemCtx)){
						cstItemCtx.put("invoiceId", invoiceId);
						cstItemCtx.put("parentInvoiceId", invoiceId);
						cstItemCtx.put("parentInvoiceItemSeqId", invItemSeqId);
						cstItemCtx.put("userLogin", userLogin);
						result = dispatcher.runSync("createInvoiceItem", cstItemCtx);
						
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Item: CST PUR", module);	
							return ServiceUtil.returnError("Error creating Invoice item for Item: CST PUR");
						}
					}
					*/
				/*	List<GenericValue> receipts = EntityUtil.filterByCondition(shipmentReceipts, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					if(UtilValidate.isNotEmpty(receipts)){
						String inventoryItemId = (EntityUtil.getFirst(receipts)).getString("inventoryItemId");
						
						GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
						
						if(UtilValidate.isNotEmpty(inventoryItem)){
							inventoryItem.set("unitCost", unitListPrice);
							inventoryItem.store();
						}
					}*/
					
					i++;
				}
				
				/*for (Map<String, Object> adjustMap : invoiceAdjChargesList) {
					
					String adjustmentTypeId = "";
					String applicableTo = "";
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
					
					if(UtilValidate.isNotEmpty(adjustMap.get("assessableValue")) && ( (adjustMap.get("assessableValue")).equals("Y"))  ){
						invoiceItemCtx.put("isAssessableValue", "Y");
					}
					
					if(UtilValidate.isNotEmpty(adjustmentTypeId) && !(amount.compareTo(BigDecimal.ZERO) == 0)){
						invoiceItemCtx.put("invoiceId", invoiceId);
						invoiceItemCtx.put("quantity", BigDecimal.ONE);
						invoiceItemCtx.put("userLogin", userLogin);
						
						invoiceItemCtx.put("parentInvoiceId", invoiceId);
						if( (UtilValidate.isNotEmpty(adjustMap.get("applicableTo")))  &&    (!(adjustMap.get("applicableTo")).equals("ALL"))  ){
							applicableTo = (String)adjustMap.get("applicableTo");
							if( UtilValidate.isNotEmpty(itemSeqMap.get(applicableTo))) {
								String seqId = (String)itemSeqMap.get(applicableTo);
								invoiceItemCtx.put("parentInvoiceItemSeqId", seqId);
							}
						}

						result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Item : "+adjustmentTypeId, module);	
							return ServiceUtil.returnError("Error creating Invoice item for Item : "+adjustmentTypeId);
						}
						String invItemSeqId = (String) result.get("invoiceItemSeqId");
					}
					
				}
				
				for (Map<String, Object> adjustMap : invoiceDiscountsList) {
					
					String adjustmentTypeId = "";
					String applicableTo = "";
					BigDecimal amount = BigDecimal.ZERO;
					Map invoiceItemCtx = FastMap.newInstance();
					if(UtilValidate.isNotEmpty(adjustMap.get("adjustmentTypeId"))){
						adjustmentTypeId = (String)adjustMap.get("adjustmentTypeId");
						invoiceItemCtx.put("invoiceItemTypeId", adjustmentTypeId);
					}
					if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
						amount = (BigDecimal)adjustMap.get("amount");
						invoiceItemCtx.put("amount", amount.negate());
					}
					if(UtilValidate.isNotEmpty(adjustMap.get("assessableValue")) && ( (adjustMap.get("assessableValue")).equals("Y"))  ){
						invoiceItemCtx.put("isAssessableValue", "Y");
					}
					if(UtilValidate.isNotEmpty(adjustmentTypeId) && !(amount.compareTo(BigDecimal.ZERO) == 0)){
						invoiceItemCtx.put("invoiceId", invoiceId);
						invoiceItemCtx.put("quantity", adjustMap.get("quantity"));
						invoiceItemCtx.put("userLogin", userLogin);
						
						invoiceItemCtx.put("parentInvoiceId", invoiceId);
						if( (UtilValidate.isNotEmpty(adjustMap.get("applicableTo")))  &&    (!(adjustMap.get("applicableTo")).equals("ALL"))  ){
							applicableTo = (String)adjustMap.get("applicableTo");
							if( UtilValidate.isNotEmpty(itemSeqMap.get(applicableTo))) {
								String seqId = (String)itemSeqMap.get(applicableTo);
								invoiceItemCtx.put("parentInvoiceItemSeqId", seqId);
							}
						}
						
						result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
						if (ServiceUtil.isError(result)) {
							Debug.logError("Error creating Invoice item for Item : "+adjustmentTypeId, module);	
							return ServiceUtil.returnError("Error creating Invoice item for Item : "+adjustmentTypeId);
						}
						String invItemSeqId = (String) result.get("invoiceItemSeqId");
					}
					
				}*/
				
				GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
				/*invoice.set("shipmentId", shipmentId);
				invoice.store();
				*/
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
			    	  
			    	  String roleTypeId = orderRole.getString("roleTypeId");
		            	
		            	if(roleTypeId.equals("BILL_TO_CUSTOMER"))
		                  createInvoiceRoleContext.put("partyId", roFroBranch);
		            	else
		            	  createInvoiceRoleContext.put("partyId", orderRole.getString("partyId"));
			    	  
			            createInvoiceRoleContext.put("roleTypeId", orderRole.getString("roleTypeId"));
			            Map<String, Object> createInvoiceRoleResult = dispatcher.runSync("createInvoiceRole", createInvoiceRoleContext);
			            if (ServiceUtil.isError(createInvoiceRoleResult)) {
			            	Debug.logError("Error creating InvoiceRole  for orderId : "+orderId, module);	
							return ServiceUtil.returnError("Error creating Invoice Role for orderId : "+orderId);
			            }
			      }
			      
			      try{
		              	 GenericValue billFromVendorRole = delegator.makeValue("InvoiceRole");
		              	 billFromVendorRole.set("invoiceId", invoiceId);
		              	 billFromVendorRole.set("partyId", partyIdFrom);
		              	 billFromVendorRole.set("roleTypeId", "COST_CENTER_ID");
		              	 billFromVendorRole.set("datetimePerformed", UtilDateTime.nowTimestamp());
		                   delegator.createOrStore(billFromVendorRole);
		               } catch (GenericEntityException e) {
		             	  Debug.logError(e, "Failed to Populate Invoice ", module);
		               }	
			      
			      
			      //approve invoice
			      /*Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
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
		            } */ 
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

	
	
	public static Map<String, Object> createDepotSalesInvoice(DispatchContext ctx,Map<String, ? extends Object> context) {
		
	    Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    List<Map> productQtyList = (List) context.get("productQtyList");
	    List<Map> invoiceAdjChargesList = (List) context.get("invoiceAdjChargesList");
	    List<Map> invoiceDiscountsList = (List) context.get("invoiceDiscountsList");
	    
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
			orderParty = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR"));
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
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.NOT_EQUAL, "SALES_INVOICE"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> invoices = delegator.findList("Invoice", condition, null, null, null, false);
			/*if(UtilValidate.isNotEmpty(invoices)){
				Debug.logError("Invoices already generated for shipment : "+shipmentId, module);
				return ServiceUtil.returnError("Invoices already generated for shipment : "+shipmentId);
			}*/
			
			/*Map resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", productQtyList, "otherCharges", invoiceAdjChargesList, "userLogin", userLogin, "incTax", ""));
			if(ServiceUtil.isError(resultCtx)){
  		  		String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
  		  		Debug.logError(errMsg , module);
  		  		return ServiceUtil.returnError(errMsg);
			}*/
			//Debug.log("#####resultCtx#########"+resultCtx);
			//List<Map> itemDetails = (List)resultCtx.get("itemDetail");
			//List<Map> adjustmentDetail = (List)resultCtx.get("adjustmentDetail");
			
			//invoiceDiscountsList

			Map input = FastMap.newInstance();
			input.put("userLogin", userLogin);
	        input.put("invoiceTypeId", "SALES_INVOICE");        
	        input.put("partyIdFrom", partyId);	
	        input.put("statusId", "INVOICE_IN_PROCESS");	
	        input.put("currencyUomId", currencyUomId);
	        input.put("invoiceDate", invoiceDate);
	        input.put("dueDate", invoiceDate); 	        
	        input.put("partyId",partyIdFrom );
	        input.put("shipmentId",shipmentId );
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

			Map itemSeqMap = FastMap.newInstance();
			int i=0;
			for (Map<String, Object> prodQtyMap : productQtyList) {
				
				String productId = "";
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal amount = BigDecimal.ZERO;
				Map invoiceItemCtx = FastMap.newInstance();
				Map vatItemCtx = FastMap.newInstance();
				Map cstItemCtx = FastMap.newInstance();
				
				BigDecimal unitListPrice = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
					productId = (String)prodQtyMap.get("productId");
					invoiceItemCtx.put("productId", productId);
				}
			    GenericValue Product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				String productName = (String)Product.get("productName");
				if(UtilValidate.isNotEmpty(prodQtyMap.get("quantity"))){
					quantity = (BigDecimal)prodQtyMap.get("quantity");
					invoiceItemCtx.put("quantity", quantity);
				}
				if(UtilValidate.isNotEmpty(prodQtyMap.get("unitPrice"))){
					amount = (BigDecimal)prodQtyMap.get("unitPrice");
					BigDecimal unitPrice = amount;
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("bedUnitRate"))){
						unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedUnitRate"));
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedcessUnitRate"))){
						unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedcessUnitRate"));
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("bedseccessUnitRate"))){
						unitPrice = unitPrice.add((BigDecimal)prodQtyMap.get("bedseccessUnitRate"));
					}*/
					invoiceItemCtx.put("amount", unitPrice);
					invoiceItemCtx.put("unitPrice", unitPrice);
					
				}
				/*if(UtilValidate.isNotEmpty(prodQtyMap.get("unitListPrice"))){
					unitListPrice = (BigDecimal)prodQtyMap.get("unitListPrice");
					invoiceItemCtx.put("unitListPrice", unitListPrice);
				}*/
				/*if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
					BigDecimal vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
					if(vatPercent.compareTo(BigDecimal.ZERO)>0){
						vatItemCtx.put("vatPercent", vatPercent);
					}
				}*/
				if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
					BigDecimal vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
					if(vatAmount.compareTo(BigDecimal.ZERO)>0){
						vatItemCtx.put("invoiceItemTypeId", "VAT_PUR");
						vatItemCtx.put("amount", vatAmount);
						vatItemCtx.put("quantity", BigDecimal.ONE);
						vatItemCtx.put("description", "VAT");
					}
					
				}
				/*if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
					BigDecimal cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
					if(cstPercent.compareTo(BigDecimal.ZERO)>0){
						invoiceItemCtx.put("cstPercent", cstPercent);
					}
				}*/
				if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
					BigDecimal cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
					if(cstAmount.compareTo(BigDecimal.ZERO)>0){
						cstItemCtx.put("invoiceItemTypeId", "CST_PUR");
						cstItemCtx.put("amount", cstAmount);
						cstItemCtx.put("quantity", BigDecimal.ONE);
						cstItemCtx.put("description", "CST");
					}
				}
				invoiceItemCtx.put("invoiceId", invoiceId);
				invoiceItemCtx.put("invoiceItemTypeId", "INV_FPROD_ITEM");
				invoiceItemCtx.put("description", productName);
				invoiceItemCtx.put("userLogin", userLogin);
				result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
				
				if (ServiceUtil.isError(result)) {
					Debug.logError("Error creating Invoice item for product : "+productId, module);	
					return ServiceUtil.returnError("Error creating Invoice item for product : "+productId);
				}
				String invItemSeqId = (String) result.get("invoiceItemSeqId");
				
				
				GenericValue shipmentRece = (GenericValue)shipmentReceipts.get(i);
				String orderItemSeqNo = (String)shipmentRece.get("orderItemSeqId");
				
				Map<String, Object> createOrderItemBillingContext = FastMap.newInstance();
                createOrderItemBillingContext.put("invoiceId", invoiceId);
                createOrderItemBillingContext.put("invoiceItemSeqId", invItemSeqId);
                createOrderItemBillingContext.put("orderId", orderId);
                createOrderItemBillingContext.put("orderItemSeqId", orderItemSeqNo);
               /* //createOrderItemBillingContext.put("itemIssuanceId", itemIssuanceId);
                createOrderItemBillingContext.put("quantity", billingQuantity);
                createOrderItemBillingContext.put("amount", billingAmount);
*/	                createOrderItemBillingContext.put("userLogin", userLogin);
               /* if ((shipmentReceipt != null) && (shipmentReceipt.getString("receiptId") != null)) {
                    createOrderItemBillingContext.put("shipmentReceiptId", shipmentReceipt.getString("receiptId"));
                }*/

                Map<String, Object> createOrderItemBillingResult = dispatcher.runSync("createOrderItemBilling", createOrderItemBillingContext);
                if (ServiceUtil.isError(createOrderItemBillingResult)) {
                	Debug.logError("Error creating OrderItem Billing", module);	
					return ServiceUtil.returnError("Error creating OrderItem Billing");
                }
				
				itemSeqMap.put(productId, invItemSeqId);
				
				if(UtilValidate.isNotEmpty(vatItemCtx)){
					vatItemCtx.put("invoiceId", invoiceId);
					vatItemCtx.put("parentInvoiceId", invoiceId);
					vatItemCtx.put("parentInvoiceItemSeqId", invItemSeqId);
					vatItemCtx.put("userLogin", userLogin);
					result = dispatcher.runSync("createInvoiceItem", vatItemCtx);
					if (ServiceUtil.isError(result)) {
						Debug.logError("Error creating Invoice item for Item : VAT PUR", module);	
						return ServiceUtil.returnError("Error creating Invoice item for Item : VAT PUR");
					}
				}
				if(UtilValidate.isNotEmpty(cstItemCtx)){
					cstItemCtx.put("invoiceId", invoiceId);
					cstItemCtx.put("parentInvoiceId", invoiceId);
					cstItemCtx.put("parentInvoiceItemSeqId", invItemSeqId);
					cstItemCtx.put("userLogin", userLogin);
					result = dispatcher.runSync("createInvoiceItem", cstItemCtx);
					if (ServiceUtil.isError(result)) {
						Debug.logError("Error creating Invoice item for Item: CST PUR", module);	
						return ServiceUtil.returnError("Error creating Invoice item for Item: CST PUR");
					}
				}
				
				List<GenericValue> receipts = EntityUtil.filterByCondition(shipmentReceipts, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				if(UtilValidate.isNotEmpty(receipts)){
					String inventoryItemId = (EntityUtil.getFirst(receipts)).getString("inventoryItemId");
					
					GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
					
					if(UtilValidate.isNotEmpty(inventoryItem)){
						inventoryItem.set("unitCost", unitListPrice);
						inventoryItem.store();
					}
				}
				
				i++;
			}
			
			
			
			for (Map<String, Object> adjustMap : invoiceAdjChargesList) {
				
				String adjustmentTypeId = "";
				String applicableTo = "";
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
					
					invoiceItemCtx.put("parentInvoiceId", invoiceId);
					if( (UtilValidate.isNotEmpty(adjustMap.get("applicableTo")))  &&    (!(adjustMap.get("applicableTo")).equals("ALL"))  ){
						applicableTo = (String)adjustMap.get("applicableTo");
						if( UtilValidate.isNotEmpty(itemSeqMap.get(applicableTo))) {
							String seqId = (String)itemSeqMap.get(applicableTo);
							invoiceItemCtx.put("parentInvoiceItemSeqId", seqId);
						}
					}
					
					result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
					if (ServiceUtil.isError(result)) {
						Debug.logError("Error creating Invoice item for Item : "+adjustmentTypeId, module);	
						return ServiceUtil.returnError("Error creating Invoice item for Item : "+adjustmentTypeId);
					}
					String invItemSeqId = (String) result.get("invoiceItemSeqId");
				}
				
			}
			
			for (Map<String, Object> adjustMap : invoiceDiscountsList) {
				
				String adjustmentTypeId = "";
				String applicableTo = "";
				BigDecimal amount = BigDecimal.ZERO;
				Map invoiceItemCtx = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(adjustMap.get("adjustmentTypeId"))){
					adjustmentTypeId = (String)adjustMap.get("adjustmentTypeId");
					invoiceItemCtx.put("invoiceItemTypeId", adjustmentTypeId);
				}
				if(UtilValidate.isNotEmpty(adjustMap.get("amount"))){
					amount = (BigDecimal)adjustMap.get("amount");
					invoiceItemCtx.put("amount", amount.negate());
				}
				if(UtilValidate.isNotEmpty(adjustmentTypeId) && !(amount.compareTo(BigDecimal.ZERO) == 0)){
					invoiceItemCtx.put("invoiceId", invoiceId);
					invoiceItemCtx.put("quantity", adjustMap.get("quantity"));
					invoiceItemCtx.put("userLogin", userLogin);
					
					invoiceItemCtx.put("parentInvoiceId", invoiceId);
					if( (UtilValidate.isNotEmpty(adjustMap.get("applicableTo")))  &&    (!(adjustMap.get("applicableTo")).equals("ALL"))  ){
						applicableTo = (String)adjustMap.get("applicableTo");
						if( UtilValidate.isNotEmpty(itemSeqMap.get(applicableTo))) {
							String seqId = (String)itemSeqMap.get(applicableTo);
							invoiceItemCtx.put("parentInvoiceItemSeqId", seqId);
						}
					}
					
					result = dispatcher.runSync("createInvoiceItem", invoiceItemCtx);
					if (ServiceUtil.isError(result)) {
						Debug.logError("Error creating Invoice item for Item : "+adjustmentTypeId, module);	
						return ServiceUtil.returnError("Error creating Invoice item for Item : "+adjustmentTypeId);
					}
					String invItemSeqId = (String) result.get("invoiceItemSeqId");
				}
				
			}
			
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			/*invoice.set("shipmentId", shipmentId);
			invoice.store();
			*/
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
		      /*Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
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
	            } */ 
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

	
	public static String CreatePOByOrder(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		//old PO flow starts from here
		String partyId = (String) request.getParameter("supplierId");
		String billFromVendorPartyId = (String) request.getParameter("billToPartyId");
		String productStoreId = (String) request.getParameter("productStoreId");
		String orderId = (String) request.getParameter("orderId");
		String refNo = (String) request.getParameter("refNo");
		String quotationNo = (String) request.getParameter("quotationNo");
		String orderDateStr = (String) request.getParameter("orderDate");
		String dyesChemicals = (String) request.getParameter("dyesChemicals");
		
		//Debug.log("orderDateStr=================="+orderDateStr);
		
		String effectiveDateStr = (String) request.getParameter("orderDate");
		String partyIdTo = (String) request.getParameter("shipToPartyId");
		String partyGeoId = (String) request.getParameter("supplierGeoId");
		
		String purchaseTitleTransferEnumId = (String) request.getParameter("purchaseTitleTransferEnumId");
		String purchaseTaxType = (String) request.getParameter("purchaseTaxType");
		
		// contact Details
		String city = (String) request.getParameter("city");
		String address1 = (String) request.getParameter("address1");
		String address2 = (String) request.getParameter("address2");
		String country = (String) request.getParameter("country");
		String postalCode = (String) request.getParameter("postalCode");
		String stateProvinceGeoId = (String) request.getParameter("stateProvinceGeoId");
		String districtGeoId = (String) request.getParameter("districtGeoId");
		String contactMechId = null;

		Map<String, Object> resultContatMap = FastMap.newInstance();
		Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();

		String orderTypeId="";
		String orderName ="";
		Timestamp effectiveDate = UtilDateTime.nowTimestamp();
		Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		
		SimpleDateFormat sdfPo = new SimpleDateFormat("dd MMMMM, yyyy");   
		
		List<Map> itemDetail = FastList.newInstance();
		List<Map> otherChargesList = FastList.newInstance();
		Timestamp orderDate = UtilDateTime.nowTimestamp();
		BigDecimal grandTotal =BigDecimal.ZERO;
		Map resultMap = FastMap.newInstance();
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
						request.setAttribute("_ERROR_MESSAGE_", "Cannot create PurchaseOrder for cancelled orderId : "+orderId);	
				  		return "error";
					}
				}
				String productId = null;
				String orderItemSeqId = null;
				String batchNo = null;
				String daysToStore = null;
				String quantityStr = null;
				String basicPriceStr = null;
				String vatPriceStr = null;
				String cessPriceStr = null;
				String bedPriceStr = null;
				String cstPriceStr = null;
				String unitPriceStr = null;
				String remarksStr = null;
				String serTaxPriceStr = null;
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal basicPrice = BigDecimal.ZERO;
				BigDecimal cstPrice = BigDecimal.ZERO;
				BigDecimal unitPrice = BigDecimal.ZERO;
				BigDecimal vatPrice = BigDecimal.ZERO;
				BigDecimal bedPrice = BigDecimal.ZERO;
				BigDecimal cessPrice = BigDecimal.ZERO;
				BigDecimal serviceTaxPrice = BigDecimal.ZERO;
				//percentage fields
				String bedPercentStr = null;
				String cessPercentStr = null;
				String vatPercentStr = null;
				String cstPercentStr = null;
				String tcsPercentStr = null;
				String serviceTaxPercentStr = null;
				
				String purTaxListStr = null;
				
				BigDecimal bedPercent=BigDecimal.ZERO;
				BigDecimal cessPercent=BigDecimal.ZERO;
				BigDecimal vatPercent=BigDecimal.ZERO;
				BigDecimal cstPercent=BigDecimal.ZERO;
				BigDecimal tcsPercent=BigDecimal.ZERO;
				BigDecimal serviceTaxPercent=BigDecimal.ZERO;
				
				String orderAdjustmentsListStr = null;

				Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
				int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
				if (rowCount < 1) {
					Debug.logError("No rows to process, as rowCount = " + rowCount, module);
					return "error";
				}
				List productIds = FastList.newInstance();
				List indentProductList = FastList.newInstance();
				for (int i = 0; i < rowCount; i++) {
					
					List purTaxRateList = FastList.newInstance();
					List orderAdjustmentList = FastList.newInstance();
					
					Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
					String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
					
					
					String termTypeInput= (String) paramMap.get("otherTermId" + thisSuffix);
					if (UtilValidate.isNotEmpty(termTypeInput)) {
						String otherTermId = "";
						String applicableTo = "ALL";
						String termValueStr = "";
						String termDaysStr = "";
						String description = "";
						String uomId = "INR";
						String assessableValue = "";
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
						if (paramMap.containsKey("assessableValue" + thisSuffix)) {
							assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
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
							Debug.log("otherTermId =============="+otherTermId + " == " + termValue);
							otherChargesDetail.put("otherTermId", otherTermId);
							otherChargesDetail.put("adjustmentValue", termValue);
							otherChargesDetail.put("applicableTo", applicableTo);
							otherChargesDetail.put("termDays", termDays);
							otherChargesDetail.put("uomId", uomId);
							otherChargesDetail.put("assessableValue", assessableValue);
							otherChargesDetail.put("description", description);
							otherChargesList.add(otherChargesDetail);
						}
					}
					
					
					
					
					
					
					
					
					
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
						
						if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
							orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
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
						
						if (paramMap.containsKey("unitPrice" + thisSuffix)) {
							unitPriceStr = (String) paramMap.get("unitPrice"
									+ thisSuffix);
						}
						
						
						if (paramMap.containsKey("remarks" + thisSuffix)) {
							remarksStr = (String) paramMap.get("remarks"
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
						

						if (paramMap.containsKey("bedPercent" + thisSuffix)) {
							bedPercentStr = (String) paramMap.get("bedPercent"
									+ thisSuffix);
						}
						if (paramMap.containsKey("cessPercent" + thisSuffix)) {
							bedPercentStr = (String) paramMap.get("cessPercent"
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
						
// Purchase tax list
						
						if (paramMap.containsKey("purTaxList" + thisSuffix)) {
							purTaxListStr = (String) paramMap.get("purTaxList"
									+ thisSuffix);
							
							String[] taxList = purTaxListStr.split(",");
							for (int j = 0; j < taxList.length; j++) {
								String taxType = taxList[j];
								Map taxRateMap = FastMap.newInstance();
								String purTaxType = taxType.replace("_SALE", "_PUR");
								taxRateMap.put("orderAdjustmentTypeId",purTaxType);
								taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
								taxRateMap.put("amount",BigDecimal.ZERO);
								taxRateMap.put("taxAuthGeoId", partyGeoId);
								
								if (paramMap.containsKey(taxType + "_PUR" + thisSuffix)) {
									String taxPercentage = (String) paramMap.get(taxType + "_PUR" + thisSuffix);
									if(UtilValidate.isNotEmpty(taxPercentage) && !(taxPercentage.equals("NaN"))){
										taxRateMap.put("sourcePercentage",new BigDecimal(taxPercentage));
									}
									
								}
								if (paramMap.containsKey(taxType+ "_PUR_AMT" + thisSuffix)) {
									String taxAmt = (String) paramMap.get(taxType+ "_PUR_AMT" + thisSuffix);
									if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
										taxRateMap.put("amount",new BigDecimal(taxAmt));
									}
								}
								
								Map tempTaxMap = FastMap.newInstance();
								tempTaxMap.putAll(taxRateMap);
								
								purTaxRateList.add(tempTaxMap);
							}
						}
						
						if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
							orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
							
							String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
							for (int j = 0; j < orderAdjustmentsList.length; j++) {
								String orderAdjustmentType = orderAdjustmentsList[j];
								Map adjTypeMap = FastMap.newInstance();
								adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
								adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
								adjTypeMap.put("amount",BigDecimal.ZERO);
								//adjTypeMap.put("taxAuthGeoId", partyGeoId);
								
								if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
									String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
									if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
										adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
									}
									
								}
								if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
									String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
									if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
										adjTypeMap.put("amount",new BigDecimal(taxAmt));
									}
								}
								if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
									String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
									if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
										if(isAssessableValue.equals("TRUE")){
											adjTypeMap.put("isAssessableValue", "Y");
											//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
										}
									}
								}
								Map tempAdjMap = FastMap.newInstance();
								tempAdjMap.putAll(adjTypeMap);
								
								orderAdjustmentList.add(tempAdjMap);
							}
						}
						//Debug.log("assessableAdjustmentAmount ================="+assessableAdjustmentAmount);
						
						try {
							quantity = new BigDecimal(quantityStr);
							if (UtilValidate.isNotEmpty(unitPriceStr)) {
								unitPrice = new BigDecimal(unitPriceStr);
							}
							if (UtilValidate.isNotEmpty(basicPriceStr)) {
								basicPrice = new BigDecimal(basicPriceStr);
							}
							if (UtilValidate.isNotEmpty(cstPriceStr)) {
								cstPrice = new BigDecimal(cstPriceStr);
							}
							
							if (UtilValidate.isNotEmpty(bedPriceStr)) {
								bedPrice = new BigDecimal(bedPriceStr);
							}
							if (UtilValidate.isNotEmpty(cessPriceStr)) {
								cessPrice = new BigDecimal(cessPriceStr);
							}
							if (UtilValidate.isNotEmpty(vatPriceStr)) {
								vatPrice = new BigDecimal(vatPriceStr);
							}
							

							if (UtilValidate.isNotEmpty(bedPercentStr)) {
								bedPercent = new BigDecimal(bedPercentStr);
							}
							if (UtilValidate.isNotEmpty(cessPercentStr)) {
								cessPercent = new BigDecimal(cessPercentStr);
							}
							if (UtilValidate.isNotEmpty(vatPercentStr)) {
								vatPercent = new BigDecimal(vatPercentStr);
							}
							if (UtilValidate.isNotEmpty(cstPercentStr)) {
								cstPercent = new BigDecimal(cstPercentStr);
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
						productQtyMap.put("unitPrice", unitPrice);
						productQtyMap.put("remarks", remarksStr);
						productQtyMap.put("unitListPrice", unitPrice);
						productQtyMap.put("basicPrice", basicPrice);
						productQtyMap.put("bedPrice", bedPrice);
						productQtyMap.put("cessPrice", bedPrice);
						productQtyMap.put("cstPrice", cstPrice);
						productQtyMap.put("vatPrice", vatPrice);
						productQtyMap.put("bedPercent", bedPercent);
						productQtyMap.put("cessPercent", bedPercent);
						productQtyMap.put("vatPercent", vatPercent);
						productQtyMap.put("cstPercent", cstPercent);
						productQtyMap.put("orderItemSeqId", orderItemSeqId);
						productQtyMap.put("purTaxRateList", purTaxRateList);
						productQtyMap.put("orderAdjustmentList", orderAdjustmentList);
						
						itemDetail.add(productQtyMap);

					}//end of productQty check
				}//end row count for loop
				//Debug.log("indentProductList============================"+indentProductList);
				
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

				Map orderItemSeq = FastMap.newInstance();
				/*for(GenericValue orderItemsValues : orderItems){
					String productId = orderItemsValues.getString("productId");
					BigDecimal quantity=orderItemsValues.getBigDecimal("quantity");
					BigDecimal unitListPrice=BigDecimal.ZERO;
					BigDecimal unitPrice=BigDecimal.ZERO;
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
				}*/
			
				if (UtilValidate.isNotEmpty(orderDateStr)) { 
					try {
						orderDate = new java.sql.Timestamp(sdfPo.parse(orderDateStr).getTime());
					} catch (ParseException e) {
						Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
					} catch (NullPointerException e) {
						Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
					}
				}
			
				if (UtilValidate.isNotEmpty(effectiveDateStr)) { 
					try {
						effectiveDate = new java.sql.Timestamp(sdfPo.parse(effectiveDateStr).getTime());
					} catch (ParseException e) {
						Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
					} catch (NullPointerException e) {
						Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
					}
				}else{
					effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				}
				
				Map processOrderContext = FastMap.newInstance();

				//String productStoreId = (String) (in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
				//String productStoreId ="STORE";
				List termsList = FastList.newInstance();
				List otherTermDetail = FastList.newInstance();
				List adjustmentDetail = FastList.newInstance();

				processOrderContext.put("userLogin", userLogin);
				processOrderContext.put("productQtyList", itemDetail);
				processOrderContext.put("orderTypeId", orderTypeId);
				processOrderContext.put("orderId", orderId);
				processOrderContext.put("termsList", termsList);
				processOrderContext.put("partyId", partyId);
				processOrderContext.put("grandTotal", grandTotal);
				processOrderContext.put("otherTerms", otherTermDetail);
				processOrderContext.put("otherChargesList", otherChargesList);
				processOrderContext.put("adjustmentDetail", adjustmentDetail);
				processOrderContext.put("billFromPartyId", partyId);
				processOrderContext.put("issueToDeptId", "");
				processOrderContext.put("shipToPartyId", partyIdTo);
				processOrderContext.put("billFromVendorPartyId", billFromVendorPartyId);
				processOrderContext.put("supplyDate", effectiveDate);
				processOrderContext.put("salesChannel", "WEB_SALES_CHANNEL");
				processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
				processOrderContext.put("productStoreId", productStoreId);
				processOrderContext.put("PONumber", orderId);
				processOrderContext.put("refNo", refNo);
				processOrderContext.put("dyesChemicals", dyesChemicals);
				processOrderContext.put("quotationNo", quotationNo);
				processOrderContext.put("orderName", orderName);
				processOrderContext.put("districtGeoId", districtGeoId);
				//processOrderContext.put("refNo", refNo);
				processOrderContext.put("orderDate", orderDate);
				//processOrderContext.put("fromDate", (String)UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
				//processOrderContext.put("thruDate", (String)UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()));
				processOrderContext.put("estimatedDeliveryDate", estimatedDeliveryDate);
				processOrderContext.put("incTax", "Y");
				
				processOrderContext.put("purchaseTitleTransferEnumId", purchaseTitleTransferEnumId);
				processOrderContext.put("purchaseTaxType", purchaseTaxType);
				result = CreateMaterialPO(dctx, processOrderContext);
				if(ServiceUtil.isError(result)){
					Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
					request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
					return "error";
				}
			  
			}
		}catch(GenericEntityException e){
			Debug.logError("Cannot create PurchaseOrder for cancelled orderId : "+orderId, module);
			request.setAttribute("_ERROR_MESSAGE_", "Cannot create PurchaseOrder for cancelled orderId : "+orderId);	
			return "error";
		}
		
		/*//creating supplier product here
				try{
					Map suppProdResult = FastMap.newInstance();
					if(UtilValidate.isNotEmpty(result.get("orderId"))){
						Map suppProdMap = FastMap.newInstance();
						suppProdMap.put("userLogin", userLogin);
						suppProdMap.put("orderId", result.get("orderId"));
						suppProdResult = dispatcher.runSync("createSupplierProductFromOrder",suppProdMap);
						if(ServiceUtil.isError(suppProdResult)){
							Debug.logError("Unable do create supplier product: " + ServiceUtil.getErrorMessage(suppProdResult), module);
							request.setAttribute("_ERROR_MESSAGE_", "Unable do create supplier product...! "+ServiceUtil.getErrorMessage(suppProdResult));
							return "error";
						}
					}
				}catch (Exception e1) {
					Debug.logError(e1, "Error in supplier product",module);
					request.setAttribute("_ERROR_MESSAGE_", "Unable do create supplier product...! ");
					return "error";
				}*/
				
				if(UtilValidate.isNotEmpty(orderId)){
					Map<String, Object> orderAssocMap = FastMap.newInstance();
					orderAssocMap.put("orderId", result.get("orderId"));
					orderAssocMap.put("toOrderId", orderId);
					orderAssocMap.put("userLogin", userLogin);
					result = createOrderAssoc(dctx,orderAssocMap);
					if(ServiceUtil.isError(result)){
						Debug.logError("Unable do Order Assoc: " + ServiceUtil.getErrorMessage(result), module);
						request.setAttribute("_ERROR_MESSAGE_", "Unable do Order Assoc...! "+ServiceUtil.getErrorMessage(result));
						return "error";
					}
				}
				if(UtilValidate.isNotEmpty(orderId)){
				    Map<String, Object> orderStatusMap = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", "DRAFTPO_PROPOSAL", "userLogin", userLogin);
				    Map<String, Object> statusResult = null;
			        try{
				         statusResult = dispatcher.runSync("changeOrderStatus", orderStatusMap);
				    }catch (Exception e) {
					     Debug.logError("Problems adjusting order header status for order #" + orderId, module);
	                }
			    }

				try{
					if (UtilValidate.isNotEmpty(address1)){
						input = UtilMisc.toMap("userLogin", userLogin, "partyId",partyIdTo, "address1",address1, "address2", address2, "city", city, "stateProvinceGeoId", stateProvinceGeoId, "postalCode", postalCode, "contactMechPurposeTypeId","SHIPPING_LOCATION");
						resultContatMap =  dispatcher.runSync("createPartyPostalAddress", input);
						
						if (ServiceUtil.isError(resultContatMap)) {
							Debug.logError(ServiceUtil.getErrorMessage(resultContatMap), module);
			            }
						contactMechId = (String) resultContatMap.get("contactMechId");

						Object tempInput = "SHIPPING_LOCATION";
						input = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "partyId",partyIdTo, "contactMechPurposeTypeId", tempInput);
						resultContatMap =  dispatcher.runSync("createPartyContactMechPurpose", input);
						if (ServiceUtil.isError(resultContatMap)) {
						    Debug.logError(ServiceUtil.getErrorMessage(resultContatMap), module);
			            }
						partyIdTo = (String) resultContatMap.get("partyId"); 
						
					 }else{
						 
						 try {
			                    GenericValue orderParty = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyIdTo));
			                    Collection<GenericValue> shippingContactMechList = ContactHelper.getContactMech(orderParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
			                    if (UtilValidate.isNotEmpty(shippingContactMechList)) {
			                        GenericValue shippingContactMech = (shippingContactMechList.iterator()).next();
			                        contactMechId= shippingContactMech.getString("contactMechId");
			                    }
			                } catch (GenericEntityException e) {
			                    Debug.logError(e, "Error setting shippingContactMechId in setDefaultCheckoutOptions() method.", module);
			                }
						 
					 }
					
					
					}catch (GenericServiceException e) {
						Debug.logError(e, module);

					} 
					if(UtilValidate.isNotEmpty(contactMechId)){
						try{
			        GenericValue OrderContactMech = delegator.makeValue("OrderContactMech", FastMap.newInstance());
			        OrderContactMech.set("orderId", result.get("orderId"));
			        OrderContactMech.set("contactMechPurposeTypeId", "SHIPPING_LOCATION");
			        OrderContactMech.set("contactMechId", contactMechId);
	            	delegator.createOrStore(OrderContactMech);
						}catch (Exception ex) {
							request.setAttribute("_ERROR_MESSAGE_", "Error while storing shipping Details for Order: "+result.get("orderId"));	  	 
				        }
					}
				
					String multiaddress = (String) request.getParameter("multiaddress");

					
					if(UtilValidate.isNotEmpty(multiaddress)){
					
					JSONArray multiAddressArray=null;
					     try{		
					         multiAddressArray =new JSONArray(multiaddress);
						  

					         int shipGroupSeqId = 1;
						  for (int i = 0; i < multiAddressArray.length(); i++) {
							  
						    	String multiAddressArrayStr = String.valueOf( multiAddressArray.get(i));
					
					               JSONObject multiAddressMap = new JSONObject();
					               multiAddressMap = (JSONObject) JSONSerializer.toJSON(multiAddressArrayStr);
					
					               String Mcity = (String) multiAddressMap.get("city");
					               String quantityStr = (String) multiAddressMap.get("quantity");
					               String estiDateStr = (String) multiAddressMap.get("estiDate");
					               
					               
					               Timestamp estimatedShipDate = null;
					               if (UtilValidate.isNotEmpty(estiDateStr)) { //2011-12-25 18:09:45
					       			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");             
					       			try {
					       				estimatedShipDate = new java.sql.Timestamp(sdf1.parse(estiDateStr).getTime());
					       			} catch (ParseException e) {
					       				Debug.logError(e, "Cannot parse date string: " + estiDateStr, module);
					       			} catch (NullPointerException e) {
					       				Debug.logError(e, "Cannot parse date string: " + estiDateStr, module);
					       			}
					       		}
					       		else{
					       			estimatedShipDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
					       		}
					              
					               if(UtilValidate.isNotEmpty(Mcity)){
										try{
									        GenericValue OrderItemShipGroup = delegator.makeValue("OrderItemShipGroup");
									        OrderItemShipGroup.set("orderId", result.get("orderId"));
									        String shipGroupSeq = String.format("%05d", shipGroupSeqId);
									        OrderItemShipGroup.set("shipGroupSeqId", shipGroupSeq);
									        OrderItemShipGroup.set("city", Mcity);
									        if(UtilValidate.isNotEmpty(quantityStr))
									        OrderItemShipGroup.set("quantity", new BigDecimal(quantityStr));
									        OrderItemShipGroup.set("estimatedShipDate", estimatedShipDate);
							           	    delegator.createOrStore(OrderItemShipGroup);
							           	    
							           	     shipGroupSeqId++;
										}catch (Exception ex) {
											request.setAttribute("_ERROR_MESSAGE_", "Error while storing OrderItemShipGroup Details for Order: "+orderId);	  	 
								        }
									}
						    }
						
						 }catch(Exception e){
							 Debug.logError("JSON Array Parsing Error : "+e, module);
						 } 
					     
					}
					try{
			 			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("orderId", result.get("orderId")));
			 			if (ServiceUtil.isError(serviceResult)) {
			 				Debug.logError("Error While Updateing Indent Summary Details", module);
			            }
			  		}catch(GenericServiceException e){
						Debug.logError(e, "Error While Updateing Indent Summary Details ", module);
					}
					
					
			request.setAttribute("_EVENT_MESSAGE_", "Entry successful for party: "+partyId+" and  PO :"+result.get("orderId"));	
			request.setAttribute("orderId", orderId); 
			return "success";
	
	}
	

	
	
	
  public static String CreatePOByOrderDC(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		//old PO flow starts from here
		String partyId = (String) request.getParameter("supplierId");
		String billFromVendorPartyId = (String) request.getParameter("billToPartyId");
		String productStoreId = (String) request.getParameter("productStoreId");
		String orderId = (String) request.getParameter("orderId");
		String refNo = (String) request.getParameter("refNo");
		String quotationNo = (String) request.getParameter("quotationNo");
		String orderDateStr = (String) request.getParameter("orderDate");
		String dyesChemicals = (String) request.getParameter("dyesChemicals");
		
		//Debug.log("orderDateStr=================="+orderDateStr);
		
		String effectiveDateStr = (String) request.getParameter("orderDate");
		String partyIdTo = (String) request.getParameter("shipToPartyId");
		String partyGeoId = (String) request.getParameter("supplierGeoId");
		
		String purchaseTitleTransferEnumId = (String) request.getParameter("purchaseTitleTransferEnumId");
		String purchaseTaxType = (String) request.getParameter("purchaseTaxType");
		
		// contact Details
		String city = (String) request.getParameter("city");
		String address1 = (String) request.getParameter("address1");
		String address2 = (String) request.getParameter("address2");
		String country = (String) request.getParameter("country");
		String postalCode = (String) request.getParameter("postalCode");
		String stateProvinceGeoId = (String) request.getParameter("stateProvinceGeoId");
		String districtGeoId = (String) request.getParameter("districtGeoId");
		String contactMechId = null;

		Map<String, Object> resultContatMap = FastMap.newInstance();
		Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();

		String orderTypeId="";
		String orderName ="";
		Timestamp effectiveDate = UtilDateTime.nowTimestamp();
		Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		
		SimpleDateFormat sdfPo = new SimpleDateFormat("dd MMMMM, yyyy");   
		
		List<Map> itemDetail = FastList.newInstance();
		List<Map> otherChargesList = FastList.newInstance();
		Timestamp orderDate = UtilDateTime.nowTimestamp();
		BigDecimal grandTotal =BigDecimal.ZERO;
		Map resultMap = FastMap.newInstance();
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
						request.setAttribute("_ERROR_MESSAGE_", "Cannot create PurchaseOrder for cancelled orderId : "+orderId);	
				  		return "error";
					}
				}
				String productId = null;
				String orderItemSeqId = null;
				String batchNo = null;
				String daysToStore = null;
				String quantityStr = null;
				String basicPriceStr = null;
				String vatPriceStr = null;
				String cessPriceStr = null;
				String bedPriceStr = null;
				String cstPriceStr = null;
				String unitPriceStr = null;
				String remarksStr = null;
				String serTaxPriceStr = null;
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal basicPrice = BigDecimal.ZERO;
				BigDecimal cstPrice = BigDecimal.ZERO;
				BigDecimal unitPrice = BigDecimal.ZERO;
				BigDecimal vatPrice = BigDecimal.ZERO;
				BigDecimal bedPrice = BigDecimal.ZERO;
				BigDecimal cessPrice = BigDecimal.ZERO;
				BigDecimal serviceTaxPrice = BigDecimal.ZERO;
				//percentage fields
				String bedPercentStr = null;
				String cessPercentStr = null;
				String vatPercentStr = null;
				String cstPercentStr = null;
				String tcsPercentStr = null;
				String serviceTaxPercentStr = null;
				String packingStr = null;
				String packetsStr = null;
				
				String purTaxListStr = null;
				
				BigDecimal bedPercent=BigDecimal.ZERO;
				BigDecimal cessPercent=BigDecimal.ZERO;
				BigDecimal vatPercent=BigDecimal.ZERO;
				BigDecimal cstPercent=BigDecimal.ZERO;
				BigDecimal tcsPercent=BigDecimal.ZERO;
				BigDecimal serviceTaxPercent=BigDecimal.ZERO;
				
				BigDecimal packing=BigDecimal.ZERO;
				BigDecimal packets=BigDecimal.ZERO;
				
				String orderAdjustmentsListStr = null;

				Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
				int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
				if (rowCount < 1) {
					Debug.logError("No rows to process, as rowCount = " + rowCount, module);
					return "error";
				}
				List productIds = FastList.newInstance();
				List indentProductList = FastList.newInstance();
				for (int i = 0; i < rowCount; i++) {
					
					List purTaxRateList = FastList.newInstance();
					List orderAdjustmentList = FastList.newInstance();
					
					Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
					String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
					
					
					String termTypeInput= (String) paramMap.get("otherTermId" + thisSuffix);
					if (UtilValidate.isNotEmpty(termTypeInput)) {
						String otherTermId = "";
						String applicableTo = "ALL";
						String termValueStr = "";
						String termDaysStr = "";
						String description = "";
						String uomId = "INR";
						String assessableValue = "";
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
						if (paramMap.containsKey("assessableValue" + thisSuffix)) {
							assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
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
							Debug.log("otherTermId =============="+otherTermId + " == " + termValue);
							otherChargesDetail.put("otherTermId", otherTermId);
							otherChargesDetail.put("adjustmentValue", termValue);
							otherChargesDetail.put("applicableTo", applicableTo);
							otherChargesDetail.put("termDays", termDays);
							otherChargesDetail.put("uomId", uomId);
							otherChargesDetail.put("assessableValue", assessableValue);
							otherChargesDetail.put("description", description);
							otherChargesList.add(otherChargesDetail);
						}
					}
					
					
					
					
					
					
					
					
					
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
						
						if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
							orderItemSeqId = (String) paramMap.get("orderItemSeqId" + thisSuffix);
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
						
						if (paramMap.containsKey("unitPrice" + thisSuffix)) {
							unitPriceStr = (String) paramMap.get("unitPrice"
									+ thisSuffix);
						}
						
						
						if (paramMap.containsKey("remarks" + thisSuffix)) {
							remarksStr = (String) paramMap.get("remarks"
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
						

						if (paramMap.containsKey("bedPercent" + thisSuffix)) {
							bedPercentStr = (String) paramMap.get("bedPercent"
									+ thisSuffix);
						}
						if (paramMap.containsKey("cessPercent" + thisSuffix)) {
							bedPercentStr = (String) paramMap.get("cessPercent"
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
						if (paramMap.containsKey("Packaging" + thisSuffix)) {
							packingStr = (String) paramMap
									.get("Packaging" + thisSuffix);
						}
						if (paramMap.containsKey("packets" + thisSuffix)) {
							packetsStr = (String) paramMap
									.get("packets" + thisSuffix);
						}
						
// Purchase tax list
						
						if (paramMap.containsKey("purTaxList" + thisSuffix)) {
							purTaxListStr = (String) paramMap.get("purTaxList"
									+ thisSuffix);
							
							String[] taxList = purTaxListStr.split(",");
							for (int j = 0; j < taxList.length; j++) {
								String taxType = taxList[j];
								Map taxRateMap = FastMap.newInstance();
								String purTaxType = taxType.replace("_SALE", "_PUR");
								taxRateMap.put("orderAdjustmentTypeId",purTaxType);
								taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
								taxRateMap.put("amount",BigDecimal.ZERO);
								taxRateMap.put("taxAuthGeoId", partyGeoId);
								
								if (paramMap.containsKey(taxType + "_PUR" + thisSuffix)) {
									String taxPercentage = (String) paramMap.get(taxType + "_PUR" + thisSuffix);
									if(UtilValidate.isNotEmpty(taxPercentage) && !(taxPercentage.equals("NaN"))){
										taxRateMap.put("sourcePercentage",new BigDecimal(taxPercentage));
									}
									
								}
								if (paramMap.containsKey(taxType+ "_PUR_AMT" + thisSuffix)) {
									String taxAmt = (String) paramMap.get(taxType+ "_PUR_AMT" + thisSuffix);
									if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
										taxRateMap.put("amount",new BigDecimal(taxAmt));
									}
								}
								
								Map tempTaxMap = FastMap.newInstance();
								tempTaxMap.putAll(taxRateMap);
								
								purTaxRateList.add(tempTaxMap);
							}
						}
						
						if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
							orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
							
							String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
							for (int j = 0; j < orderAdjustmentsList.length; j++) {
								String orderAdjustmentType = orderAdjustmentsList[j];
								Map adjTypeMap = FastMap.newInstance();
								adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
								adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
								adjTypeMap.put("amount",BigDecimal.ZERO);
								//adjTypeMap.put("taxAuthGeoId", partyGeoId);
								
								if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
									String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
									if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
										adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
									}
									
								}
								if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
									String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
									if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
										adjTypeMap.put("amount",new BigDecimal(taxAmt));
									}else if(orderAdjustmentType.equals("PRICE_DISCOUNT")){
										adjTypeMap.put("amount",new BigDecimal(taxAmt).negate());
									}
								}  
								if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
									String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
									if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
										if(isAssessableValue.equals("TRUE")){
											adjTypeMap.put("isAssessableValue", "Y");
											//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
										}
									}
								}
								Map tempAdjMap = FastMap.newInstance();
								tempAdjMap.putAll(adjTypeMap);
								
								orderAdjustmentList.add(tempAdjMap);
							}
						}
						//Debug.log("assessableAdjustmentAmount ================="+assessableAdjustmentAmount);
						
						try {
							quantity = new BigDecimal(quantityStr);
							if (UtilValidate.isNotEmpty(unitPriceStr)) {
								unitPrice = new BigDecimal(unitPriceStr);
							}
							if (UtilValidate.isNotEmpty(basicPriceStr)) {
								basicPrice = new BigDecimal(basicPriceStr);
							}
							if (UtilValidate.isNotEmpty(cstPriceStr)) {
								cstPrice = new BigDecimal(cstPriceStr);
							}
							
							if (UtilValidate.isNotEmpty(bedPriceStr)) {
								bedPrice = new BigDecimal(bedPriceStr);
							}
							if (UtilValidate.isNotEmpty(cessPriceStr)) {
								cessPrice = new BigDecimal(cessPriceStr);
							}
							if (UtilValidate.isNotEmpty(vatPriceStr)) {
								vatPrice = new BigDecimal(vatPriceStr);
							}
							

							if (UtilValidate.isNotEmpty(bedPercentStr)) {
								bedPercent = new BigDecimal(bedPercentStr);
							}
							if (UtilValidate.isNotEmpty(cessPercentStr)) {
								cessPercent = new BigDecimal(cessPercentStr);
							}
							if (UtilValidate.isNotEmpty(vatPercentStr)) {
								vatPercent = new BigDecimal(vatPercentStr);
							}
							if (UtilValidate.isNotEmpty(cstPercentStr)) {
								cstPercent = new BigDecimal(cstPercentStr);
							}
							if (UtilValidate.isNotEmpty(packingStr)) {
								packing = new BigDecimal(packingStr);
							}
							if (UtilValidate.isNotEmpty(packetsStr)) {
								packets = new BigDecimal(packetsStr);
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
						productQtyMap.put("unitPrice", unitPrice);
						productQtyMap.put("remarks", remarksStr);
						productQtyMap.put("unitListPrice", unitPrice);
						productQtyMap.put("basicPrice", basicPrice);
						productQtyMap.put("bedPrice", bedPrice);
						productQtyMap.put("cessPrice", bedPrice);
						productQtyMap.put("cstPrice", cstPrice);
						productQtyMap.put("vatPrice", vatPrice);
						productQtyMap.put("bedPercent", bedPercent);
						productQtyMap.put("cessPercent", bedPercent);
						productQtyMap.put("vatPercent", vatPercent);
						productQtyMap.put("cstPercent", cstPercent);
						productQtyMap.put("orderItemSeqId", orderItemSeqId);
						productQtyMap.put("purTaxRateList", purTaxRateList);
						productQtyMap.put("orderAdjustmentList", orderAdjustmentList);
						productQtyMap.put("packing", packing);
						productQtyMap.put("packets", packets);
						
						
						itemDetail.add(productQtyMap);

					}//end of productQty check
				}//end row count for loop
				//Debug.log("indentProductList============================"+indentProductList);
				
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

				Map orderItemSeq = FastMap.newInstance();
				/*for(GenericValue orderItemsValues : orderItems){
					String productId = orderItemsValues.getString("productId");
					BigDecimal quantity=orderItemsValues.getBigDecimal("quantity");
					BigDecimal unitListPrice=BigDecimal.ZERO;
					BigDecimal unitPrice=BigDecimal.ZERO;
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
				}*/
			
				if (UtilValidate.isNotEmpty(orderDateStr)) { 
					try {
						orderDate = new java.sql.Timestamp(sdfPo.parse(orderDateStr).getTime());
					} catch (ParseException e) {
						Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
					} catch (NullPointerException e) {
						Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
					}
				}
			
				if (UtilValidate.isNotEmpty(effectiveDateStr)) { 
					try {
						effectiveDate = new java.sql.Timestamp(sdfPo.parse(effectiveDateStr).getTime());
					} catch (ParseException e) {
						Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
					} catch (NullPointerException e) {
						Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
					}
				}else{
					effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				}
				
				Map processOrderContext = FastMap.newInstance();

				//String productStoreId = (String) (in.vasista.vbiz.purchase.PurchaseStoreServices.getPurchaseFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
				//String productStoreId ="STORE";
				List termsList = FastList.newInstance();
				List otherTermDetail = FastList.newInstance();
				List adjustmentDetail = FastList.newInstance();

				processOrderContext.put("userLogin", userLogin);
				processOrderContext.put("productQtyList", itemDetail);
				processOrderContext.put("orderTypeId", orderTypeId);
				processOrderContext.put("orderId", orderId);
				processOrderContext.put("termsList", termsList);
				processOrderContext.put("partyId", partyId);
				processOrderContext.put("grandTotal", grandTotal);
				processOrderContext.put("otherTerms", otherTermDetail);
				processOrderContext.put("otherChargesList", otherChargesList);
				processOrderContext.put("adjustmentDetail", adjustmentDetail);
				processOrderContext.put("billFromPartyId", partyId);
				processOrderContext.put("issueToDeptId", "");
				processOrderContext.put("shipToPartyId", partyIdTo);
				processOrderContext.put("billFromVendorPartyId", billFromVendorPartyId);
				processOrderContext.put("supplyDate", effectiveDate);
				processOrderContext.put("salesChannel", "WEB_SALES_CHANNEL");
				processOrderContext.put("enableAdvancePaymentApp", Boolean.TRUE);
				processOrderContext.put("productStoreId", productStoreId);
				processOrderContext.put("PONumber", orderId);
				processOrderContext.put("refNo", refNo);
				processOrderContext.put("dyesChemicals", dyesChemicals);
				processOrderContext.put("quotationNo", quotationNo);
				processOrderContext.put("orderName", orderName);
				processOrderContext.put("districtGeoId", districtGeoId);
				//processOrderContext.put("refNo", refNo);
				processOrderContext.put("orderDate", orderDate);
				//processOrderContext.put("fromDate", (String)UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
				//processOrderContext.put("thruDate", (String)UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()));
				processOrderContext.put("estimatedDeliveryDate", estimatedDeliveryDate);
				processOrderContext.put("incTax", "Y");
				
				processOrderContext.put("purchaseTitleTransferEnumId", purchaseTitleTransferEnumId);
				processOrderContext.put("purchaseTaxType", purchaseTaxType);
				result = CreateMaterialPO(dctx, processOrderContext);
				if(ServiceUtil.isError(result)){
					Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
					request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
					return "error";
				}
			  
			}
		}catch(GenericEntityException e){
			Debug.logError("Cannot create PurchaseOrder for cancelled orderId : "+orderId, module);
			request.setAttribute("_ERROR_MESSAGE_", "Cannot create PurchaseOrder for cancelled orderId : "+orderId);	
			return "error";
		}
		
		/*//creating supplier product here
				try{
					Map suppProdResult = FastMap.newInstance();
					if(UtilValidate.isNotEmpty(result.get("orderId"))){
						Map suppProdMap = FastMap.newInstance();
						suppProdMap.put("userLogin", userLogin);
						suppProdMap.put("orderId", result.get("orderId"));
						suppProdResult = dispatcher.runSync("createSupplierProductFromOrder",suppProdMap);
						if(ServiceUtil.isError(suppProdResult)){
							Debug.logError("Unable do create supplier product: " + ServiceUtil.getErrorMessage(suppProdResult), module);
							request.setAttribute("_ERROR_MESSAGE_", "Unable do create supplier product...! "+ServiceUtil.getErrorMessage(suppProdResult));
							return "error";
						}
					}
				}catch (Exception e1) {
					Debug.logError(e1, "Error in supplier product",module);
					request.setAttribute("_ERROR_MESSAGE_", "Unable do create supplier product...! ");
					return "error";
				}*/
				
				if(UtilValidate.isNotEmpty(orderId)){
					Map<String, Object> orderAssocMap = FastMap.newInstance();
					orderAssocMap.put("orderId", result.get("orderId"));
					orderAssocMap.put("toOrderId", orderId);
					orderAssocMap.put("userLogin", userLogin);
					result = createOrderAssoc(dctx,orderAssocMap);
					if(ServiceUtil.isError(result)){
						Debug.logError("Unable do Order Assoc: " + ServiceUtil.getErrorMessage(result), module);
						request.setAttribute("_ERROR_MESSAGE_", "Unable do Order Assoc...! "+ServiceUtil.getErrorMessage(result));
						return "error";
					}
				}
				
				if(UtilValidate.isNotEmpty(orderId)){
				    Map<String, Object> orderStatusMap = UtilMisc.<String, Object>toMap("orderId", result.get("orderId"), "userLogin", userLogin);
				    Map<String, Object> statusResult = null;
			        try{
				         statusResult = dispatcher.runSync("populateSaleItemPrice", orderStatusMap);
				    }catch (Exception e) {
					     Debug.logError("Problems adjusting order header status for order #" + orderId, module);
	                }
			    }
				
				if(UtilValidate.isNotEmpty(orderId)){
				    Map<String, Object> orderStatusMap = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", "DRAFTPO_PROPOSAL", "userLogin", userLogin);
				    Map<String, Object> statusResult = null;
			        try{
				         statusResult = dispatcher.runSync("changeOrderStatus", orderStatusMap);
				    }catch (Exception e) {
					     Debug.logError("Problems adjusting order header status for order #" + orderId, module);
	                }
			    }

				try{
					if (UtilValidate.isNotEmpty(address1)){
						input = UtilMisc.toMap("userLogin", userLogin, "partyId",partyIdTo, "address1",address1, "address2", address2, "city", city, "stateProvinceGeoId", stateProvinceGeoId, "postalCode", postalCode, "contactMechPurposeTypeId","SHIPPING_LOCATION");
						resultContatMap =  dispatcher.runSync("createPartyPostalAddress", input);
						
						if (ServiceUtil.isError(resultContatMap)) {
							Debug.logError(ServiceUtil.getErrorMessage(resultContatMap), module);
			            }
						contactMechId = (String) resultContatMap.get("contactMechId");

						Object tempInput = "SHIPPING_LOCATION";
						input = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "partyId",partyIdTo, "contactMechPurposeTypeId", tempInput);
						resultContatMap =  dispatcher.runSync("createPartyContactMechPurpose", input);
						if (ServiceUtil.isError(resultContatMap)) {
						    Debug.logError(ServiceUtil.getErrorMessage(resultContatMap), module);
			            }
						partyIdTo = (String) resultContatMap.get("partyId"); 
						
					 }else{
						 
						 try {
			                    GenericValue orderParty = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyIdTo));
			                    Collection<GenericValue> shippingContactMechList = ContactHelper.getContactMech(orderParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
			                    if (UtilValidate.isNotEmpty(shippingContactMechList)) {
			                        GenericValue shippingContactMech = (shippingContactMechList.iterator()).next();
			                        contactMechId= shippingContactMech.getString("contactMechId");
			                    }
			                } catch (GenericEntityException e) {
			                    Debug.logError(e, "Error setting shippingContactMechId in setDefaultCheckoutOptions() method.", module);
			                }
						 
					 }
					
					
					}catch (GenericServiceException e) {
						Debug.logError(e, module);

					} 
					if(UtilValidate.isNotEmpty(contactMechId)){
						try{
			        GenericValue OrderContactMech = delegator.makeValue("OrderContactMech", FastMap.newInstance());
			        OrderContactMech.set("orderId", result.get("orderId"));
			        OrderContactMech.set("contactMechPurposeTypeId", "SHIPPING_LOCATION");
			        OrderContactMech.set("contactMechId", contactMechId);
	            	delegator.createOrStore(OrderContactMech);
						}catch (Exception ex) {
							request.setAttribute("_ERROR_MESSAGE_", "Error while storing shipping Details for Order: "+result.get("orderId"));	  	 
				        }
					}
				
					String multiaddress = (String) request.getParameter("multiaddress");

					
					if(UtilValidate.isNotEmpty(multiaddress)){
					
					JSONArray multiAddressArray=null;
					     try{		
					         multiAddressArray =new JSONArray(multiaddress);
						  

					         int shipGroupSeqId = 1;
						  for (int i = 0; i < multiAddressArray.length(); i++) {
							  
						    	String multiAddressArrayStr = String.valueOf( multiAddressArray.get(i));
					
					               JSONObject multiAddressMap = new JSONObject();
					               multiAddressMap = (JSONObject) JSONSerializer.toJSON(multiAddressArrayStr);
					
					               String Mcity = (String) multiAddressMap.get("city");
					               String quantityStr = (String) multiAddressMap.get("quantity");
					               String estiDateStr = (String) multiAddressMap.get("estiDate");
					               
					               
					               Timestamp estimatedShipDate = null;
					               if (UtilValidate.isNotEmpty(estiDateStr)) { //2011-12-25 18:09:45
					       			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");             
					       			try {
					       				estimatedShipDate = new java.sql.Timestamp(sdf1.parse(estiDateStr).getTime());
					       			} catch (ParseException e) {
					       				Debug.logError(e, "Cannot parse date string: " + estiDateStr, module);
					       			} catch (NullPointerException e) {
					       				Debug.logError(e, "Cannot parse date string: " + estiDateStr, module);
					       			}
					       		}
					       		else{
					       			estimatedShipDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
					       		}
					              
					               if(UtilValidate.isNotEmpty(Mcity)){
										try{
									        GenericValue OrderItemShipGroup = delegator.makeValue("OrderItemShipGroup");
									        OrderItemShipGroup.set("orderId", result.get("orderId"));
									        String shipGroupSeq = String.format("%05d", shipGroupSeqId);
									        OrderItemShipGroup.set("shipGroupSeqId", shipGroupSeq);
									        OrderItemShipGroup.set("city", Mcity);
									        if(UtilValidate.isNotEmpty(quantityStr))
									        OrderItemShipGroup.set("quantity", new BigDecimal(quantityStr));
									        OrderItemShipGroup.set("estimatedShipDate", estimatedShipDate);
							           	    delegator.createOrStore(OrderItemShipGroup);
							           	    
							           	     shipGroupSeqId++;
										}catch (Exception ex) {
											request.setAttribute("_ERROR_MESSAGE_", "Error while storing OrderItemShipGroup Details for Order: "+orderId);	  	 
								        }
									}
						    }
						
						 }catch(Exception e){
							 Debug.logError("JSON Array Parsing Error : "+e, module);
						 } 
					     
					}
					try{
			 			Map serviceResult  = dispatcher.runSync("getIndentAndUpdateIndenSummaryDetails", UtilMisc.toMap("orderId", result.get("orderId")));
			 			if (ServiceUtil.isError(serviceResult)) {
			 				Debug.logError("Error While Updateing Indent Summary Details", module);
			            }
			  		}catch(GenericServiceException e){
						Debug.logError(e, "Error While Updateing Indent Summary Details ", module);
					}
					
					
			request.setAttribute("_EVENT_MESSAGE_", "Entry successful for party: "+partyId+" and  PO :"+result.get("orderId"));	
			request.setAttribute("orderId", orderId); 
			return "success";
	
	}
	
	
	
	
	
	   
	public static Map<String, Object> CreateMaterialPO(DispatchContext ctx,Map<String, ? extends Object> context) {
			
			
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
	  	Map taxTermsMap = (Map) context.get("taxTermsMap");
	  	String partyId = (String) context.get("partyId");
	  	String billFromPartyId = (String) context.get("billFromPartyId");
	  	String shipToPartyId = (String) context.get("shipToPartyId");
		String issueToDeptId = (String) context.get("issueToDeptId");
	  	List<Map> termsList = (List)context.get("termsList");
	  	List<Map> otherChargesList = (List) context.get("otherChargesList");
	  	List<Map> otherChargesAdjustment = (List)context.get("adjustmentDetail");
	  	List<Map> otherTermDetail = (List)context.get("otherTerms");
	  	String incTax = (String)context.get("incTax");
	  	boolean beganTransaction = false;
	  	String PONumber=(String) context.get("PONumber");
        Timestamp orderDate = (Timestamp)context.get("orderDate");
        Timestamp estimatedDeliveryDate = (Timestamp)context.get("estimatedDeliveryDate");
		String orderName = (String)context.get("orderName");
		String fileNo = (String)context.get("fileNo");
		String quotationNo = (String)context.get("quotationNo");
		String refNo = (String)context.get("refNo");
		String dyesChemicals = (String)context.get("dyesChemicals");
		String orderId = (String)context.get("orderId");	
	  	String currencyUomId = "INR";
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp effectiveDate = UtilDateTime.getDayStart(supplyDate);
		String fromDate = (String)context.get("fromDate");
		String thruDate = (String)context.get("thruDate");
		String billToPartyId = (String) context.get("billFromVendorPartyId");
		String districtGeoId = (String) context.get("districtGeoId");
		
		String purchaseTitleTransferEnumId = (String) context.get("purchaseTitleTransferEnumId");
		String purchaseTaxType = (String) context.get("purchaseTaxType");
		
		if(UtilValidate.isEmpty(billToPartyId)){
			billToPartyId="Company";
		}
		
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
			cart.setExternalId(PONumber);
	        cart.setProductStoreId(productStoreId);
			cart.setChannelType(salesChannel);
			cart.setBillToCustomerPartyId(billToPartyId);
			cart.setPlacingCustomerPartyId(billToPartyId);
			cart.setShipToCustomerPartyId(shipToPartyId);
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
			
			if(UtilValidate.isNotEmpty(dyesChemicals) && dyesChemicals.equals("dyesChemicals"))
			cart.setPurposeTypeId("DC_PURCHASE");
			else
			cart.setPurposeTypeId("BRANCH_PURCHASE");
			
			cart.setUserLogin(userLogin, dispatcher);
			
			//set attributes here
			if(UtilValidate.isNotEmpty(PONumber))
				cart.setOrderAttribute("PO_NUMBER",PONumber);
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
			if(UtilValidate.isNotEmpty(quotationNo)){
				cart.setOrderAttribute("QUOTATION_NUMBER",quotationNo);
			}
			if(UtilValidate.isNotEmpty(districtGeoId)){
				cart.setOrderAttribute("DST_ADDR",districtGeoId);
			}
			if(UtilValidate.isNotEmpty(purchaseTitleTransferEnumId)){
				cart.setOrderAttribute("purchaseTitleTransferEnumId",purchaseTitleTransferEnumId);
			}
			if(UtilValidate.isNotEmpty(purchaseTaxType)){
				cart.setOrderAttribute("purchaseTaxType",purchaseTaxType);
			}
		} catch (Exception e) {
			
			Debug.logError(e, "Error in setting cart parameters", module);
			return ServiceUtil.returnError("Error in setting cart parameters");
		}

			try{
				beganTransaction = TransactionUtil.begin(7200);
				
				String productId = "";
				
				String remarks = "";
				int count = 0;
				BigDecimal totalBasicAmount = BigDecimal.ZERO;
				for (Map<String, Object> prodQtyMap : productQtyList) {
					List taxList=FastList.newInstance();
					
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal unitPrice = BigDecimal.ZERO;
					BigDecimal unitListPrice = BigDecimal.ZERO;
					BigDecimal vatPercent = BigDecimal.ZERO;
					BigDecimal vatAmount = BigDecimal.ZERO;
					BigDecimal cstAmount = BigDecimal.ZERO;
					BigDecimal cstPercent = BigDecimal.ZERO;
					//BigDecimal cessAmount = BigDecimal.ZERO;
					BigDecimal cessPercent = BigDecimal.ZERO;
					String orderItemSeqId = null;
					/*BigDecimal bedAmount = BigDecimal.ZERO;
					BigDecimal bedPercent = BigDecimal.ZERO;
					BigDecimal bedcessPercent = BigDecimal.ZERO;
					BigDecimal bedcessAmount = BigDecimal.ZERO;
					BigDecimal bedseccessAmount = BigDecimal.ZERO;
					BigDecimal bedseccessPercent = BigDecimal.ZERO;*/
					BigDecimal packing=BigDecimal.ZERO;
					BigDecimal packets=BigDecimal.ZERO;
					
					
					if(UtilValidate.isNotEmpty(prodQtyMap.get("productId"))){
						productId = (String)prodQtyMap.get("productId");
					}
					
					if(UtilValidate.isNotEmpty(prodQtyMap.get("remarks"))){
						remarks = (String)prodQtyMap.get("remarks");
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
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cessPercent"))){
						cessPercent = (BigDecimal)prodQtyMap.get("cessPercent");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatAmount"))){
						vatAmount = (BigDecimal)prodQtyMap.get("vatAmount");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("vatPercent"))){
						vatPercent = (BigDecimal)prodQtyMap.get("vatPercent");
					}
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("cstAmount"))){
						cstAmount = (BigDecimal)prodQtyMap.get("cstAmount");
					}*/
					if(UtilValidate.isNotEmpty(prodQtyMap.get("cstPercent"))){
						cstPercent = (BigDecimal)prodQtyMap.get("cstPercent");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("orderItemSeqId"))){
						orderItemSeqId = (String)prodQtyMap.get("orderItemSeqId");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("packing"))){
						packing = (BigDecimal)prodQtyMap.get("packing");
					}
					if(UtilValidate.isNotEmpty(prodQtyMap.get("packets"))){
						packets = (BigDecimal)prodQtyMap.get("packets");
					}
					
					/*if(UtilValidate.isNotEmpty(prodQtyMap.get("bedAmount"))){
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
					}*/

					/*if(bedAmount.compareTo(BigDecimal.ZERO)>0){
		        		Map taxDetailMap = FastMap.newInstance();
			    		taxDetailMap.put("taxType", "BED_PUR");
			    		taxDetailMap.put("amount", bedAmount);
			    		taxDetailMap.put("percentage", bedPercent);
			    		taxList.add(taxDetailMap);
					}*/
					/*if(vatAmount.compareTo(BigDecimal.ZERO)>0){
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
					}*/
					/*if(bedcessAmount.compareTo(BigDecimal.ZERO)>0){
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
					}*/
					
					List purTaxRateList = FastList.newInstance();
					if(UtilValidate.isNotEmpty(prodQtyMap.get("purTaxRateList"))){
						purTaxRateList = (List)prodQtyMap.get("purTaxRateList");
					}
					
					List orderAdjustmentList = FastList.newInstance();
					if(UtilValidate.isNotEmpty(prodQtyMap.get("orderAdjustmentList"))){
						orderAdjustmentList = (List)prodQtyMap.get("orderAdjustmentList");
					}
					
					BigDecimal basePrice = BigDecimal.ZERO;
					BigDecimal itemAmount = quantity.multiply(unitPrice);
					
					basePrice = itemAmount;
					if(cessPercent.compareTo(BigDecimal.ZERO)>0){
						BigDecimal cessAmount = (itemAmount.multiply(cessPercent)).divide(new BigDecimal("100"));
						basePrice = itemAmount.add(cessAmount);
						
						Map taxDetailMap = FastMap.newInstance();
		        		taxDetailMap.put("orderAdjustmentTypeId","CESS_PUR");
		        		taxDetailMap.put("sourcePercentage",cessPercent);
		        		taxDetailMap.put("amount",cessAmount);
						//taxDetailMap.put("taxAuthGeoId", partyGeoId);
			    		taxList.add(taxDetailMap);
					}
					
					if(vatPercent.compareTo(BigDecimal.ZERO)>0){
		        		
		        		vatAmount = ((basePrice).multiply(vatPercent)).divide(new BigDecimal("100"));
		        		
		        		Map taxDetailMap = FastMap.newInstance();
		        		taxDetailMap.put("orderAdjustmentTypeId","VAT_PUR");
		        		taxDetailMap.put("sourcePercentage",vatPercent);
		        		taxDetailMap.put("amount",vatAmount);
						//taxDetailMap.put("taxAuthGeoId", partyGeoId);
			    		taxList.add(taxDetailMap);
					}
					if(cstPercent.compareTo(BigDecimal.ZERO)>0){
						
						cstAmount = ((basePrice).multiply(cstPercent)).divide(new BigDecimal("100"));
						
		        		Map taxDetailMap = FastMap.newInstance();
		        		taxDetailMap.put("orderAdjustmentTypeId","CST_PUR");
		        		taxDetailMap.put("sourcePercentage",cstPercent);
		        		taxDetailMap.put("amount",cstAmount);
		        		taxDetailMap.put("taxAuthGeoId", "IND");
			    		
			    		taxList.add(taxDetailMap);
					}
					
					ShoppingCartItem item = null;
					
					ShoppingCartItem cartitem = null;
					
					
					try{
						
						int itemIndx = cart.addItem(count, ShoppingCartItem.makeItem(count, productId, null, quantity, unitPrice,
					            null, null, null, null, null, null, null, null, null, null, null, null, null, dispatcher,
					            cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE));
						
						item = cart.findCartItem(itemIndx);
						
						item.setOrderItemAttribute("remarks",remarks);
						
						if(UtilValidate.isNotEmpty(packing))
						item.setOrderItemAttribute("packQuantity",packing.toString());
						
						if(UtilValidate.isNotEmpty(packets))
						item.setOrderItemAttribute("packets",packets.toString());
						
						
						if( (UtilValidate.isNotEmpty(orderItemSeqId)) && (UtilValidate.isNotEmpty(orderId))){
							item.setAssociatedOrderId(orderId);
							item.setAssociatedOrderItemSeqId(orderItemSeqId);
							item.setOrderItemAssocTypeId("BackToBackOrder");
						}
						count++;
						
						totalBasicAmount = totalBasicAmount.add(quantity.multiply(unitPrice));
						
						/*for(Map chargesMap : otherChargesList){

							Map chgDetailMap = FastMap.newInstance();
							
							BigDecimal adjustmentValue = (BigDecimal) chargesMap.get("adjustmentValue");
							BigDecimal adjAmt = (BigDecimal) chargesMap.get("adjustmentValue");
							String assessableValue = (String) chargesMap.get("assessableValue");

							if(UtilValidate.isNotEmpty(chargesMap.get("uomId")) && chargesMap.get("uomId").equals("PERCENT")){
								chgDetailMap.put("sourcePercentage",adjustmentValue);
								adjAmt = (adjustmentValue.divide(new BigDecimal("100"))).multiply(unitPrice);
								adjAmt = adjAmt.multiply(quantity);
							}
							//adjAmt = adjAmt.multiply(quantity);
							
							if(UtilValidate.isNotEmpty(chargesMap.get("applicableTo")) && !chargesMap.get("applicableTo").equals("ALL")){
								if(chargesMap.get("applicableTo").equals(productId)){
									
									chgDetailMap.put("orderAdjustmentTypeId",chargesMap.get("otherTermId"));
									chgDetailMap.put("description",chargesMap.get("otherTermId"));
									chgDetailMap.put("amount",adjAmt);
									if(assessableValue.equals("checked")){
										chgDetailMap.put("isAssessableValue","Y");
									}
									
									//chgDetailMap.put("taxAuthGeoId", "IND");
									
									GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", chgDetailMap);
					 				item.addAdjustment(orderAdjustment);
									
					 				unitListPrice.add(adjAmt);
					 				otherChargesList.remove(chargesMap);
								}
							}
							
						}*/
						//item.setTaxDetails(taxList);
						
						for(int i=0; i<taxList.size(); i++){
							Map taxMap = (Map) taxList.get(i);
							if(  ((BigDecimal) taxMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
								GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxMap);
				 				item.addAdjustment(orderAdjustment);
								
				 				unitListPrice.add((BigDecimal) taxMap.get("amount"));
							}
						}
						
						for(int i=0; i<purTaxRateList.size(); i++){
							Map taxMap = (Map) purTaxRateList.get(i);
							if(  ((BigDecimal) taxMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
								GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxMap);
								item.addAdjustment(orderAdjustment);
								
								unitListPrice.add((BigDecimal) taxMap.get("amount"));
							}
						}
						
						for(int i=0; i<orderAdjustmentList.size(); i++){
							Map adjMap = (Map) orderAdjustmentList.get(i);
							if(  ((BigDecimal) adjMap.get("amount")).compareTo(BigDecimal.ZERO)>0){
								GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", adjMap);
								item.addAdjustment(orderAdjustment);
								
								unitListPrice.add((BigDecimal) adjMap.get("amount"));
							}else if(adjMap.get("orderAdjustmentTypeId").equals("PRICE_DISCOUNT")){
								GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", adjMap);
								item.addAdjustment(orderAdjustment);
								
								unitListPrice.add((BigDecimal) adjMap.get("amount"));
							}
						}
						
						
						
						item.setListPrice(unitListPrice);
		    		
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
				/*for(Map eachAdj : otherChargesAdjustment){
					
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
				
				//List wholeAdjustmentList = FastList.newInstance();
				//wholeAdjustmentList.addAll(otherChargesList);
				
				/*for(Map chargesMap : otherChargesList){

					Map chgDetailMap = FastMap.newInstance();
					
					BigDecimal adjustmentValue = (BigDecimal) chargesMap.get("adjustmentValue");
					BigDecimal adjAmt = (BigDecimal) chargesMap.get("adjustmentValue");
					String assessableValue = (String) chargesMap.get("assessableValue");
					if(UtilValidate.isNotEmpty(chargesMap.get("uomId")) && chargesMap.get("uomId").equals("PERCENT")){
						chgDetailMap.put("sourcePercentage",adjustmentValue);
						adjAmt = (adjustmentValue.divide(new BigDecimal("100"))).multiply(totalBasicAmount);
					}
					chgDetailMap.put("orderId",orderId);
					chgDetailMap.put("orderAdjustmentTypeId",chargesMap.get("otherTermId"));
					chgDetailMap.put("description",chargesMap.get("otherTermId"));
					if(assessableValue.equals("checked")){
						chgDetailMap.put("isAssessableValue","Y");
					}
					chgDetailMap.put("amount",adjAmt);
					chgDetailMap.put("userLogin",userLogin);
					//chgDetailMap.put("taxAuthGeoId", "IND");
					Map adjResultMap=FastMap.newInstance();
					try{
			  	 		adjResultMap = dispatcher.runSync("createOrderAdjustment",chgDetailMap);  		  		 
			  	 		if (ServiceUtil.isError(adjResultMap)) {
			  	 			String errMsg =  ServiceUtil.getErrorMessage(adjResultMap);
			  	 			Debug.logError(errMsg , module);
			  	 			return ServiceUtil.returnError(" Error While Creating Adjustment for Purchase Order !");
			  	 		}
			  	 	}catch (Exception e) {
			  	 		Debug.logError(e, "Error While Creating Adjustment for Purchase Order ", module);
				  		return adjResultMap;			  
				  	}
					otherChargesList.remove(chargesMap);
				}*/
				
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
				
				//before save OrderRole save partyRole
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
				
				//update PurposeType
			/*	try{
					GenericValue orderHeaderPurpose = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
					if(UtilValidate.isNotEmpty(dyesChemicals) && dyesChemicals.equals("dyesChemicals"))
					orderHeaderPurpose.set("purposeTypeId", "DC_PURCHASE");
					else
				    orderHeaderPurpose.set("purposeTypeId", "BRANCH_PURCHASE");
					
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
	   	
		public static Map<String, Object> createOrderAssoc(DispatchContext ctx,Map<String, ? extends Object> context) {
			Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			String orderId = (String) context.get("orderId");
			String toOrderId = (String) context.get("toOrderId");
			String orderAssocTypeId = "BackToBackOrder";
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> result = ServiceUtil.returnSuccess();
			try{
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", toOrderId), false);
				if(UtilValidate.isEmpty(orderHeader)){
					Debug.logError("Please Enter Valid PO Number", module);
					return ServiceUtil.returnError("Please Enter Valid PO Number");
				}
				//orderAssocTypeId = orderHeader.getString("orderTypeId");
				GenericValue orderAssoc = delegator.findOne("OrderAssoc", UtilMisc.toMap("orderId", orderId, "toOrderId", toOrderId, "orderAssocTypeId", orderAssocTypeId), false);
				if(UtilValidate.isEmpty(orderAssoc)){
					GenericValue newEntity = delegator.makeValue("OrderAssoc");
					newEntity.set("orderId", orderId);
					newEntity.set("toOrderId", toOrderId);
					newEntity.set("orderAssocTypeId", orderAssocTypeId);
					newEntity.create();
				}else{
					String oldOrderId = orderAssoc.getString("orderId");
					String oldtoOrderId = orderAssoc.getString("toOrderId");
					String oldorderAssocTypeId = orderAssoc.getString("orderAssocTypeId");
					if(!oldOrderId.equals(orderId)){
						orderAssoc.set("orderId",orderId);
					}
					if(!oldtoOrderId.equals(toOrderId)){
						orderAssoc.set("toOrderId",toOrderId);
					}
					if(!oldorderAssocTypeId.equals(orderAssocTypeId)){
						orderAssoc.set("orderAssocTypeId",orderAssocTypeId);
					}
					orderAssoc.store();
				}
			} catch(Exception e){
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
			result.put("orderId", orderId);
			return result;
		}
		
  		public static Map<String, Object> approvePurchaseOrderWithEmail(DispatchContext dctx, Map context) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Locale locale = (Locale) context.get("locale");
			String salesChannelEnumId = (String) context.get("salesChannelEnumId");
			String partyId=(String) context.get("partyId");
			String orderId = (String) context.get("orderId");
			String newStatus = (String) context.get("statusId");
			String smsContent = "";
			String salesOrderId = " ";
		
			// get the order header
			GenericValue orderHeader = null;
			try {
				orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				if(UtilValidate.isNotEmpty(orderAssocList)){
					salesOrderId = (EntityUtil.getFirst(orderAssocList)).getString("toOrderId");
				}
	        } catch (GenericEntityException e) {
	            Debug.logError(e, "Cannot get OrderHeader record", module);
	        }
	        if (orderHeader == null) {
	            Debug.logError("OrderHeader came back as null", module);
				return ServiceUtil.returnError("OrderHeader came back as null"+orderId);
	        }
	        String orderHeaderStatusId = orderHeader.getString("statusId");
	        Timestamp orderDate = orderHeader.getTimestamp("orderDate");
	        String orderDateStr=UtilDateTime.toDateString(orderDate,"dd-MM-yyyy");
	        // now set the new order status
            if (newStatus != null && !newStatus.equals(orderHeaderStatusId)) {
                Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", newStatus, "userLogin", userLogin);
                Map<String, Object> newSttsResult = null;
                try {
                    newSttsResult = dispatcher.runSync("changeOrderStatus", serviceContext);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the changeOrderStatus service", module);
                }
                if (ServiceUtil.isError(newSttsResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
                }
            }
	        
	        List<GenericValue> orl = null;  
	        // boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
			// sending Mail to supplier
	        String suppPartyId=null;
	        try {
	        	orl = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "SUPPLIER_AGENT"));
	        } catch (GenericEntityException e) {
	        	Debug.logError(e, module);
	        }
	         
	        if (UtilValidate.isNotEmpty(orl)) {
	        	GenericValue orderRole = EntityUtil.getFirst(orl);
	        	suppPartyId = orderRole.getString("partyId");
	        }
			String senderEmail = null;
			if(UtilValidate.isNotEmpty(suppPartyId)){
				
				try{
			        Map<String, Object> originEmail = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", suppPartyId, "userLogin", userLogin));
			        if (UtilValidate.isNotEmpty(originEmail.get("emailAddress"))) {
			            senderEmail = (String) originEmail.get("emailAddress");
			        }
				}catch (GenericServiceException e) {
	                Debug.logError(e, "Problem while getting email address", module);
	            }
				
				Map sendMailParams = FastMap.newInstance();
				String productDetails="";
				try{
	                List<GenericValue> items = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	                //Debug.log("items==============================="+items);
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
	                	smsContent = smsContent + " " + qtystrng + " KGs of " + desc + ",";
	                }
	                //smsContent = smsContent +" For future enquiries quote the P.O number.";
	                //Debug.log("smsContent================================"+smsContent);
				}catch(GenericEntityException ex){
					Debug.log("Problem in fetching orderItems");
				}
			
	/*			if(UtilValidate.isNotEmpty(senderEmail)){
	
			        sendMailParams.put("sendTo", "harish@vasista.in");
			        sendMailParams.put("sendFrom", UtilProperties.getPropertyValue("general.properties", "defaultFromEmailAddress"));
			        sendMailParams.put("subject", "Purchase Order"+orderId);
			        sendMailParams.put("contentType", "text/html");
			        sendMailParams.put("userLogin", userLogin);  
					String Msg=productDetails+" You are requested to submit the bills in quadruplicate towards the supply of said materials. Also please quote the Purchase Order No and Date in all your Letters,Delivery, Notes, and Invoices etc";
			        String Msgbody="Dear "+org.ofbiz.party.party.PartyHelper.getPartyName(delegator,suppPartyId, false)+",  \n"+Msg;
			        sendMailParams.put("body", Msgbody);
			        //Debug.log("sendMailParams====================================="+sendMailParams);
		          
			        try{ 
			        	Map partyEmailInfo =FastMap.newInstance();
			        	partyEmailInfo.put("orderId",orderId);
			        	partyEmailInfo.put("sendTo", "vamsikrishna@vasista.in");
			        	partyEmailInfo.put("sendFrom", UtilProperties.getPropertyValue("general.properties", "defaultFromEmailAddress"));
			        	partyEmailInfo.put("partyId", suppPartyId);
			        	partyEmailInfo.put("sendCc", "vikram@vasista.in");
			        	partyEmailInfo.put("subject", "Purchase Order"+orderId);
			        	partyEmailInfo.put("userLogin", userLogin);
			        	partyEmailInfo.put("bodyText", Msgbody);
			        	//Map partyInfoListResult = dispatcher.runSync("sendPurchaseOrderEmailToParty", partyEmailInfo);
			        	if (ServiceUtil.isError(partyInfoListResult)) {
			        		return ServiceUtil.returnError("Unable to send to  Purchase Order Email To Party "+ suppPartyId);
						}else{
							Debug.log("Successfully Sent Purchase Order  Email To Party "+suppPartyId +" Email " +senderEmail);
						}
			        }catch(GenericServiceException e1){
		            	Debug.log("Problem in sending email");
			        }
			        
			        try{
		                Map resultCtxMap = dispatcher.runSync("sendMail", sendMailParams, 360, true);
		                if(ServiceUtil.isError(resultCtxMap)){
		                	Debug.log("Problem in calling service sendMail");
		                }
		            }catch(GenericServiceException e1){
		            	Debug.log("Problem in sending email");
					}
				 
				} */
			}
			
			String suppName=org.ofbiz.party.party.PartyHelper.getPartyName(delegator,suppPartyId, false);
			
			String orderApprovalMessageToWeaver = UtilProperties.getMessage("ProductUiLabels", "PoApprovalMessageToWeaver", locale);
			orderApprovalMessageToWeaver = orderApprovalMessageToWeaver.replaceAll("poNumber", orderId);
			orderApprovalMessageToWeaver = orderApprovalMessageToWeaver.replaceAll("orderId", salesOrderId);
			orderApprovalMessageToWeaver = orderApprovalMessageToWeaver.replaceAll("supplierName", suppName);
			
			// send sms to weaver
			List<GenericValue> corl = null;
			String customerId=null;
			try {
				corl = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "SHIP_TO_CUSTOMER"));
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			
			if (UtilValidate.isNotEmpty(corl)) {
				GenericValue custOrderRole = EntityUtil.getFirst(corl);
				customerId = custOrderRole.getString("partyId");
			}
			String customerName=org.ofbiz.party.party.PartyHelper.getPartyName(delegator,customerId, false);
			
			Map<String, Object> getTelParams = FastMap.newInstance();
			if(UtilValidate.isEmpty(customerName)){
				customerName=customerId;
			}
			
        	getTelParams.put("partyId", customerId);
            getTelParams.put("userLogin", userLogin); 
            try{
            	Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
	            }
	            String contactNumberTo = (String) serviceResult.get("contactNumber");            
	            String countryCode = (String) serviceResult.get("countryCode");
	           /* if(UtilValidate.isEmpty(contactNumberTo)){
	            	contactNumberTo = "7330776928";
	            }*/
	            //contactNumberTo = "7330776928";
	            Debug.log("contactNumberTo = "+contactNumberTo);
	            if(UtilValidate.isNotEmpty(contactNumberTo)){
	            	 if(UtilValidate.isNotEmpty(countryCode)){
	            		 contactNumberTo = countryCode + contactNumberTo;
	            	 }
	            	 Debug.log("contactNumberTo ===== "+contactNumberTo);
	            	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                 sendSmsParams.put("contactNumberTo", contactNumberTo);
	                 sendSmsParams.put("text", orderApprovalMessageToWeaver); 
	                 //Debug.log("sendSmsParams====================="+sendSmsParams);
	                 serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	                 if (ServiceUtil.isError(serviceResult)) {
	                     Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                     return serviceResult;
	                 }
	            	
	            	/*Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                sendSmsParams.put("contactNumberTo", contactNumberTo);
	                sendSmsParams.put("text", "Order placed on M/s. "+ suppPartyId +" against your Indent No. "+orderId);            
	                serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);       
	                if (ServiceUtil.isError(serviceResult)) {
	                    Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                    return serviceResult;
	                }*/
	            }
            }catch(GenericServiceException e1){
	         	Debug.log("Problem in sending sms to user agency");
			}
             
            String orderApprovalMessageToSupplier = UtilProperties.getMessage("ProductUiLabels", "PoApprovalMessageToSupplier", locale);
            orderApprovalMessageToSupplier = orderApprovalMessageToSupplier.replaceAll("orderId", orderId);
            orderApprovalMessageToSupplier = orderApprovalMessageToSupplier.replaceAll("societyName", customerName);
            orderApprovalMessageToSupplier = orderApprovalMessageToSupplier.replaceAll("material", smsContent);
			
            // send sms to supplier
            
            Map<String, Object> getSupplierTelParams = FastMap.newInstance();
            getSupplierTelParams.put("partyId", suppPartyId);
            getSupplierTelParams.put("userLogin", userLogin);                    	
            try{
            	Map serviceResult = dispatcher.runSync("getPartyTelephone", getSupplierTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
	            } 
	            String contactNumberTo = (String) serviceResult.get("contactNumber");
	            String countryCode = (String) serviceResult.get("countryCode");
	           /* if(UtilValidate.isEmpty(contactNumberTo)){
	            	contactNumberTo = "7330776928";
	            }*/
	           // contactNumberTo = "7097476291";
	            Debug.log("contactNumberTo = "+contactNumberTo);
	            if(UtilValidate.isNotEmpty(contactNumberTo)){
	            	 if(UtilValidate.isNotEmpty(countryCode)){
	            		 contactNumberTo = countryCode + contactNumberTo;
	            	 }
	            	 Debug.log("contactNumberTo ===== "+contactNumberTo);
	            	 Map<String, Object> sendSmsParams = FastMap.newInstance();      
	                 sendSmsParams.put("contactNumberTo", contactNumberTo);
	                 sendSmsParams.put("text", orderApprovalMessageToSupplier); 
	                 serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);  
	                 if (ServiceUtil.isError(serviceResult)) {
	                     Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                     return serviceResult;
	                 }
	            	
	            }
            }catch(GenericServiceException e1){
	         	Debug.log("Problem in sending sms to the supplier");
			}
            result.put("salesChannelEnumId", salesChannelEnumId);
            return result;
		}
  		
  		public static Map<String, Object> getOrderItemSummary(DispatchContext dctx, Map context) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Locale locale = (Locale) context.get("locale");
			String orderId = (String) context.get("orderId");
	        List productIdsList = FastList.newInstance();
    		Map productSummaryMap = FastMap.newInstance();
    		
    		
    		String branchId = null;
    		List<GenericValue> billFromVendors = null; 
    		try {
    			billFromVendors = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR"));
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			
			if (UtilValidate.isNotEmpty(billFromVendors)) {
				branchId = (EntityUtil.getFirst(billFromVendors)).getString("partyId");
			}
			
			String supplierId = null;
    		List<GenericValue> suppliers = null; 
    		try {
    			suppliers = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "SUPPLIER"));
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			
			if (UtilValidate.isNotEmpty(suppliers)) {
				supplierId = (EntityUtil.getFirst(suppliers)).getString("partyId");
			}
    		
			String supplierGeoId = null;
			List supplierContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, supplierId, false, "TAX_CONTACT_MECH");
	        if(UtilValidate.isNotEmpty(supplierContactMechValueMaps)){
	        	supplierGeoId = (String)((GenericValue) ((Map) supplierContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
	        }
	        
	        String branchGeoId = null;
	        List branchContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, branchId, false, "TAX_CONTACT_MECH");
	        if(UtilValidate.isNotEmpty(branchContactMechValueMaps)){
	        	branchGeoId = (String)((GenericValue) ((Map) branchContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
	        }
    		
			try{
                List<GenericValue> items = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
                Set orderProductsSet = new HashSet(EntityUtil.getFieldListFromEntityList(items, "productId", true));    		
                productIdsList = new ArrayList(orderProductsSet);
               Iterator<String> it = productIdsList.iterator();
               while (it.hasNext()) {
                	String productId = (String) it.next();                    
    				List itemSeqDetail = FastList.newInstance();
    				String Unit="";
    				BigDecimal vatPercent = BigDecimal.ZERO;
					BigDecimal cstPercent = BigDecimal.ZERO;
    				
    				if( (UtilValidate.isNotEmpty(supplierGeoId)) && (UtilValidate.isNotEmpty(branchGeoId))   ){
    		        	
    		        	Map prodCatTaxCtx = UtilMisc.toMap("userLogin",userLogin);	  	
    					prodCatTaxCtx.put("productId", productId);
    					prodCatTaxCtx.put("taxAuthGeoId", branchGeoId);
    					//prodCatTaxCtx.put("taxAuthorityRateTypeId", orderTaxType);
    				  	try{
    				  		Map resultCtx = dispatcher.runSync("calculateTaxesByGeoId",prodCatTaxCtx);  	
    				  		
    				  		if(supplierGeoId.equals(branchGeoId)){
    				  			vatPercent = (BigDecimal) resultCtx.get("vatPercent");
    				  		}
    				  		else{
    				  			cstPercent = (BigDecimal) resultCtx.get("cstPercent");
    				  		}
    				  		
    				  	}catch (GenericServiceException e) {
    				  		Debug.logError(e , module);
    				  		return ServiceUtil.returnError(e+" Error While Creation Promotion for order");
    				  	}
    		        	
    		        }
    				
                    
			    	List<GenericValue> headerItems = EntityUtil.filterByCondition(items, EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
			    	BigDecimal prdQuantity=BigDecimal.ZERO;
			    	BigDecimal prdAmount=BigDecimal.ZERO;
			    	BigDecimal unitListPrice=BigDecimal.ZERO;
			    	BigDecimal bedPercent=BigDecimal.ZERO;
			    	BigDecimal bundleQuantity=BigDecimal.ZERO;
			    	BigDecimal baleAmount=BigDecimal.ZERO;
			    	BigDecimal bundleUnitListPrice=BigDecimal.ZERO;

			    	//BigDecimal cstPercent=BigDecimal.ZERO;
			    	//BigDecimal vatPercent=BigDecimal.ZERO;
			    	
			    	
	                List<GenericValue> itemAttrs = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

			    	
			    	for (int i = 0; i < headerItems.size(); i++) {						
						GenericValue eachProductList = (GenericValue)headerItems.get(i);
						BigDecimal quantity=(BigDecimal)eachProductList.getBigDecimal("quantity");
						unitListPrice=(BigDecimal)eachProductList.getBigDecimal("unitPrice");
						List<GenericValue> ItemAtrs = EntityUtil.filterByCondition(itemAttrs, EntityCondition.makeCondition("orderItemSeqId",EntityOperator.EQUALS,eachProductList.get("orderItemSeqId")));
						BigDecimal balequantity=BigDecimal.ZERO;
						String yarUOM="";
						for (int j = 0; j < ItemAtrs.size(); j++) {	
							GenericValue ItemAtrsList = (GenericValue)ItemAtrs.get(j);

							if("BALE_QTY".equals((String)ItemAtrsList.get("attrName"))){
								 balequantity=new BigDecimal((String)ItemAtrsList.get("attrValue"));
							}
							if("BANDLE_UNITPRICE".equals((String)ItemAtrsList.get("attrName"))){
								bundleUnitListPrice=new BigDecimal((String)ItemAtrsList.get("attrValue"));
							}
							if("YARN_UOM".equals((String)ItemAtrsList.get("attrName"))){
								yarUOM=(String)ItemAtrsList.get("attrValue");
							}
						}
						Unit=yarUOM;
						if("Bale".equals(yarUOM)){
							balequantity=balequantity.multiply(new BigDecimal(40));
						}else if("Half-Bale".equals(yarUOM)){
							balequantity=balequantity.multiply(new BigDecimal(20));
						}else{
							balequantity=balequantity;
						}
						String orderItemSeqId=(String)eachProductList.get("orderItemSeqId");
						itemSeqDetail.add(orderItemSeqId);
						BigDecimal amount=unitListPrice.multiply(quantity);
						prdQuantity =prdQuantity.add(quantity);
						bundleQuantity=bundleQuantity.add(balequantity);
						prdAmount =prdAmount.add(amount);
						
			    	}
		       		Map productDetailsMap = FastMap.newInstance();

		       		productDetailsMap.put("unitListPrice",unitListPrice);
		       		productDetailsMap.put("quantity",prdQuantity);
		       		productDetailsMap.put("Unit",Unit);
		       		productDetailsMap.put("bundleQuantity",bundleQuantity);
		       		productDetailsMap.put("bundleUnitListPrice",bundleUnitListPrice);
		       		productDetailsMap.put("amount",prdAmount);
		       		productDetailsMap.put("bedPercent",bedPercent);
		       		productDetailsMap.put("cstPercent",cstPercent);
		       		productDetailsMap.put("vatPercent",vatPercent);
		       		productDetailsMap.put("itemSeqList",itemSeqDetail);
		       		
		       		
		       		
			    	productSummaryMap.put(productId,productDetailsMap);
               
               }
			}catch(GenericEntityException ex){
				Debug.log("Problem in fetching orderItems");
			}
			result.put("productSummaryMap",productSummaryMap);
			return result;
  		}	
  		
  		
  		public static Map<String, Object> getBoHeader(DispatchContext dctx, Map context) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Locale locale = (Locale) context.get("locale");
			String branchId = (String) context.get("branchId");
	        List productIdsList = FastList.newInstance();
    		Map boHeaderMap = FastMap.newInstance();
			List<GenericValue> tenantConfigCheck = FastList.newInstance();
			try{
				List condList=FastList.newInstance();
				condList.add(EntityCondition.makeCondition("propertyName", EntityOperator.LIKE,"%"+branchId+"_HEADER%"));
				condList.add(EntityCondition.makeCondition("propertyTypeEnumId", EntityOperator.EQUALS, "COMPANY_HEADER"));
				condList.add(EntityCondition.makeCondition("propertyValue", EntityOperator.EQUALS, "Y"));
				EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
				tenantConfigCheck = delegator.findList("TenantConfiguration",cond, null , null, null, false);
				if (UtilValidate.isNotEmpty(tenantConfigCheck)) {
					for (int i = 0; i < tenantConfigCheck.size(); i++) {						
						GenericValue eachProductList = (GenericValue)tenantConfigCheck.get(i);					
						String header=(String)eachProductList.get("description");
						boHeaderMap.put("header"+i,header);
			    	}
				}
			}catch(GenericEntityException ex){
				Debug.log("Problem in fetching orderItems");
			}
			result.put("boHeaderMap",boHeaderMap);
			return result;
  		}	
  			
  		public static String processDepotSalesPurchaseInvoice(HttpServletRequest request, HttpServletResponse response) {
  			Delegator delegator = (Delegator) request.getAttribute("delegator");
  			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
  			DispatchContext dctx =  dispatcher.getDispatchContext();
  			Locale locale = UtilHttp.getLocale(request);
  			Map<String, Object> result = ServiceUtil.returnSuccess();
  			String partyId = (String) request.getParameter("partyId");
  			Map resultMap = FastMap.newInstance();
  			List invoices = FastList.newInstance(); 
  			String vehicleId = (String) request.getParameter("vehicleId");
  			String tallyrefNo = (String) request.getParameter("tallyrefNo");
  			String invoiceDateStr = (String) request.getParameter("invoiceDate");
  			String orderId = (String) request.getParameter("orderId");
  			String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
  			String partyIdFrom = "";
  			String shipmentId = (String) request.getParameter("shipmentId");
  			String purposeTypeIdForDC = (String) request.getParameter("purposeTypeId");
  			String purposeTypeId = "DEPOT_YARN_SALE";
  			
  			String purchaseTitleTransferEnumId = (String) request.getParameter("purchaseTitleTransferEnumId");
  			String purchaseTaxType = (String) request.getParameter("purchaseTaxType");
  			
  			String purTaxListStr = null;
  			
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
  			
  			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
  			int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
  			if (rowCount < 1) {
  				Debug.logError("No rows to process, as rowCount = " + rowCount, module);
  				return "success";
  			}
  			List<GenericValue> orderItems=null;
  			try{
  			 orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
  			}catch (GenericEntityException e) {
  				Debug.logError(e, module);
  			}
  	/*		
  			if(UtilValidate.isNotEmpty(tallyrefNo)){
  					  List conditionList = FastList.newInstance();
  			
  					  conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
  					  conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS,"BackToBackOrder"));
  					  EntityCondition assoc = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
  					  try{
  					 // List  OrderAssocList = delegator.findList("OrderAssoc", assoc, null, null, null,false); 
  					  
  					   //if(UtilValidate.isNotEmpty(OrderAssocList)){
  			
  					 // GenericValue OrderAssoc = EntityUtil.getFirst(OrderAssocList);
  					  //String actualOrderId = (String)OrderAssoc.get("toOrderId");
  					  
  					  GenericValue	OrderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId", orderId), false);
  			
  					  OrderHeader.set("tallyRefNo",tallyrefNo);
  					  OrderHeader.store();
  					// }
  					
  				}catch (Exception e) {
  					Debug.logError(e, "Problems While Updating Tally Ref No: " + tallyrefNo, module);
  					request.setAttribute("_ERROR_MESSAGE_", "Problems While Updating Tally Ref No: " + tallyrefNo);
  					return "error";
  				}
  			}	
  			*/
  			
  			String orderAdjustmentsListStr = null;
  			String discOrderAdjustmentsListStr = null;
  			
  			List productQtyList = FastList.newInstance();
  			List invoiceAdjChargesList = FastList.newInstance();
  			List invoiceDiscountsList = FastList.newInstance();
  			
  			String applicableTo = "ALL";
  			String applicableToDisc = "ALL";
  			for (int i = 0; i < rowCount; i++) {
  				
  				List purTaxRateList = FastList.newInstance();
  				Map prodQtyMap = FastMap.newInstance();
  				List orderAdjustmentList = FastList.newInstance();
  				List discOrderAdjustmentList = FastList.newInstance();
  				
  				String invoiceItemTypeId = "";
  				String adjAmtStr = "";
  				BigDecimal adjAmt = BigDecimal.ZERO;
  				String assessableValue = "";
  				String discAssessableValue = "";
  				String invoiceItemDiscTypeId = "";
  				String adjAmtDiscStr = "";
  				String discQtyStr = "";
  				BigDecimal adjDiscAmt = BigDecimal.ZERO;
  				BigDecimal discQty = BigDecimal.ZERO;
  				
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
  				
  				
  				
  				if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
  					orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
  					
  					
  					String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
  					for (int j = 0; j < orderAdjustmentsList.length; j++) {
  						String orderAdjustmentType = orderAdjustmentsList[j];
  						Map adjTypeMap = FastMap.newInstance();
  						adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
  						adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
  						adjTypeMap.put("amount",BigDecimal.ZERO);
  						//adjTypeMap.put("taxAuthGeoId", partyGeoId);
  						
  						if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
  							String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
  							if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
  								adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
  							}
  							
  						}
  						if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
  							String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
  							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
  								adjTypeMap.put("amount",new BigDecimal(taxAmt));
  							}
  						}
  						if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
  							String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
  							if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
  								if(isAssessableValue.equals("TRUE")){
  									adjTypeMap.put("isAssessableValue", "Y");
  									//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
  								}
  							}
  						}
  						Map tempAdjMap = FastMap.newInstance();
  						tempAdjMap.putAll(adjTypeMap);
  						
  						orderAdjustmentList.add(tempAdjMap);
  					}
  				}
  				////Debug.log("assessableAdjustmentAmount ================="+assessableAdjustmentAmount);
  				
  				if (paramMap.containsKey("discOrderAdjustmentsList" + thisSuffix)) {
  					discOrderAdjustmentsListStr = (String) paramMap.get("discOrderAdjustmentsList"+ thisSuffix);
  					
  					String[] orderAdjustmentsList = discOrderAdjustmentsListStr.split(",");
  					for (int j = 0; j < orderAdjustmentsList.length; j++) {
  						String orderAdjustmentType = orderAdjustmentsList[j];
  						Map adjTypeMap = FastMap.newInstance();
  						adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
  						adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
  						adjTypeMap.put("amount",BigDecimal.ZERO);
  						//adjTypeMap.put("taxAuthGeoId", partyGeoId);
  						
  						if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
  							String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
  							if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
  								adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
  							}
  							
  						}
  						if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
  							String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
  							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
  								BigDecimal taxAmtBd = new BigDecimal(taxAmt);
  								if(taxAmtBd.compareTo(BigDecimal.ZERO)>0){
  									taxAmtBd = taxAmtBd.negate();
  								}
  								adjTypeMap.put("amount",taxAmtBd);
  							}
  						}
  						if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
  							String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
  							if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
  								if(isAssessableValue.equals("TRUE")){
  									adjTypeMap.put("isAssessableValue", "Y");
  									//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
  								}
  							}
  						}
  						Map tempAdjMap = FastMap.newInstance();
  						tempAdjMap.putAll(adjTypeMap);
  						
  						discOrderAdjustmentList.add(tempAdjMap);
  					}
  				}
  				////Debug.log("assessableAdjustmentAmount ================="+assessableAdjustmentAmount);
  				
  				
  				
  				
  				/*if (paramMap.containsKey("invoiceItemTypeId" + thisSuffix)) {
  					invoiceItemTypeId = (String) paramMap.get("invoiceItemTypeId" + thisSuffix);
  				}

  				if (paramMap.containsKey("applicableTo" + thisSuffix)) {
  					applicableTo = (String) paramMap.get("applicableTo" + thisSuffix);
  				}

  				if (paramMap.containsKey("adjAmt" + thisSuffix)) {
  					adjAmtStr = (String) paramMap.get("adjAmt" + thisSuffix);
  				}
  				if (paramMap.containsKey("assessableValue" + thisSuffix)) {
  					assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
  				}
  				//Debug.log("assessableValue ============="+assessableValue);
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
  					invItemMap.put("adjustmentTypeId", invoiceItemTypeId);
  					invItemMap.put("amount", adjAmt);
  					invItemMap.put("uomId", "INR");
  					invItemMap.put("applicableTo", applicableTo);
  					if(UtilValidate.isNotEmpty(assessableValue) && (assessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
  						invItemMap.put("assessableValue", "Y");
  					}
  					//Debug.log("invItemMap ============="+invItemMap);
  					
  					Map tempInvItemMap = FastMap.newInstance();
  					tempInvItemMap.putAll(invItemMap);
  					
  					
  					invoiceAdjChargesList.add(tempInvItemMap);	
  					//Debug.log("invoiceAdjChargesList ============="+invoiceAdjChargesList);
  				}
  				
  				if (paramMap.containsKey("invoiceItemTypeDiscId" + thisSuffix)) {
  					invoiceItemDiscTypeId = (String) paramMap.get("invoiceItemTypeDiscId" + thisSuffix);
  				}

  				if (paramMap.containsKey("applicableToDisc" + thisSuffix)) {
  					applicableToDisc = (String) paramMap.get("applicableToDisc" + thisSuffix);
  				}

  				if (paramMap.containsKey("adjDiscAmt" + thisSuffix)) {
  					adjAmtDiscStr = (String) paramMap.get("adjDiscAmt" + thisSuffix);
  				}
  				
  				if (paramMap.containsKey("discQty" + thisSuffix)) {
  					discQtyStr = (String) paramMap.get("discQty" + thisSuffix);
  				}
  				if (paramMap.containsKey("assessableValueDisc" + thisSuffix)) {
  					discAssessableValue = (String) paramMap.get("assessableValueDisc" + thisSuffix);
  				}
  				//Debug.log("discAssessableValue ============="+discAssessableValue);
  				if(UtilValidate.isNotEmpty(adjAmtDiscStr)){
  					try {
  						adjDiscAmt = new BigDecimal(adjAmtDiscStr);
  					} catch (Exception e) {
  						Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
  						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
  						return "error";
  					}
  				}
  				
  				if(UtilValidate.isNotEmpty(discQtyStr)){
  					try {
  						discQty = new BigDecimal(discQtyStr);
  					} catch (Exception e) {
  						Debug.logError(e, "Problems parsing amount string: " + discQtyStr, module);
  						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + discQtyStr);
  						return "error";
  					}
  				}
  				
  				if(UtilValidate.isNotEmpty(invoiceItemDiscTypeId) && adjDiscAmt.compareTo(BigDecimal.ZERO)>0){
  					
  					BigDecimal adjQty = BigDecimal.ONE;
  					if(UtilValidate.isNotEmpty(discQty)){
  						if(discQty.doubleValue()>0){
  							//adjDiscAmt = adjDiscAmt.divide(discQty);
  							double tempAdjAmt = adjDiscAmt.doubleValue()/discQty.doubleValue();
  							adjDiscAmt = new BigDecimal(tempAdjAmt);
  							adjQty = discQty;
  						}
  					}
  					
  					Map invItemMap = FastMap.newInstance();
  					invItemMap.put("adjustmentTypeId", invoiceItemDiscTypeId);
  					invItemMap.put("amount", adjDiscAmt);
  					invItemMap.put("quantity", adjQty);
  					invItemMap.put("uomId", "INR");
  					invItemMap.put("applicableTo", applicableToDisc);
  					if(UtilValidate.isNotEmpty(discAssessableValue) && (discAssessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
  						invItemMap.put("assessableValue", "Y");
  					}
  					//Debug.log("invItemMap ============="+invItemMap);
  					Map tempInvItemMap = FastMap.newInstance();
  					tempInvItemMap.putAll(invItemMap);
  					
  					invoiceDiscountsList.add(tempInvItemMap);	
  					//Debug.log("invoiceDiscountsList ============="+invoiceDiscountsList);
  				}*/
  				
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
  					
  					// Purchase tax list
  					
  					if (paramMap.containsKey("purTaxList" + thisSuffix)) {
  						purTaxListStr = (String) paramMap.get("purTaxList"
  								+ thisSuffix);
  						
  						String[] taxList = purTaxListStr.split(",");
  						for (int j = 0; j < taxList.length; j++) {
  							String taxType = taxList[j];
  							Map taxRateMap = FastMap.newInstance();
  							String purTaxType = taxType.replace("_SALE", "_PUR");
  							taxRateMap.put("orderAdjustmentTypeId",purTaxType);
  							taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
  							taxRateMap.put("amount",BigDecimal.ZERO);
  							//taxRateMap.put("taxAuthGeoId", partyGeoId);
  							
  							if (paramMap.containsKey(taxType + "_PUR" + thisSuffix)) {
  								String taxPercentage = (String) paramMap.get(taxType + "_PUR" + thisSuffix);
  								if(UtilValidate.isNotEmpty(taxPercentage) && !(taxPercentage.equals("NaN"))){
  									taxRateMap.put("sourcePercentage",new BigDecimal(taxPercentage));
  								}
  								
  							}
  							if (paramMap.containsKey(taxType+ "_PUR_AMT" + thisSuffix)) {
  								String taxAmt = (String) paramMap.get(taxType+ "_PUR_AMT" + thisSuffix);
  								if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
  									taxRateMap.put("amount",new BigDecimal(taxAmt));
  								}
  							}
  							
  							Map tempTaxMap = FastMap.newInstance();
  							tempTaxMap.putAll(taxRateMap);
  							
  							purTaxRateList.add(tempTaxMap);
  						}
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
  				GenericValue orderItemValue = null;

  				if(UtilValidate.isNotEmpty(productId)){
  					try{
  						List<GenericValue> orderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));				
  						if(UtilValidate.isNotEmpty(orderItem)){
  							orderItemValue = EntityUtil.getFirst(orderItem);
  							orderItemValue.put("unitPrice", uPrice);
  							orderItemValue.put("unitListPrice", uPrice);
  							orderItemValue.store();
  						}else{
  							continue;
  						}
  					}catch (Exception e) {
  						  Debug.logError(e, "Error While Updating purchase Order ", module);
  						  request.setAttribute("_ERROR_MESSAGE_", "Unable to update Purchase Order :" + orderId+"....! ");
  							return "error";
  					}
  					prodQtyMap.put("productId", productId);
  					prodQtyMap.put("quantity", quantity);
  					prodQtyMap.put("unitPrice", uPrice);
  					prodQtyMap.put("vatAmount", vat);
  					prodQtyMap.put("cstAmount", cst);
  					prodQtyMap.put("vatPercent", vatPercent);
  					prodQtyMap.put("cstPercent", cstPercent);
  					prodQtyMap.put("bedPercent", BigDecimal.ZERO);
  					prodQtyMap.put("purTaxRateList", purTaxRateList);
  					prodQtyMap.put("orderAdjustmentList", orderAdjustmentList);
  					prodQtyMap.put("discOrderAdjustmentList", discOrderAdjustmentList);
  					productQtyList.add(prodQtyMap);
  				}
  			}//end row count for loop
  			
  			if( UtilValidate.isEmpty(productQtyList)){
  				Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
  				request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
  				return "success";
  			}
  			
  			// Get Purpose type based on product
  			
  			String invProdId = (String) ((Map) productQtyList.get(0)).get("productId");

  			/*try{
  		  		Map resultCtx = dispatcher.runSync("getPurposeTypeForProduct", UtilMisc.toMap("productId", invProdId, "userLogin", userLogin));  	
  		  		purposeTypeId = (String)resultCtx.get("purposeTypeId");
  		  	}catch (GenericServiceException e) {
  		  		Debug.logError("Unable to analyse purpose type: " + ServiceUtil.getErrorMessage(result), module);
  				request.setAttribute("_ERROR_MESSAGE_", "Unable to analyse purpose type :"+ServiceUtil.getErrorMessage(result));
  				return "error";
  		  	}*/
  			
  			Map processInvoiceContext = FastMap.newInstance();
  			processInvoiceContext.put("userLogin", userLogin);
  			processInvoiceContext.put("productQtyList", productQtyList);
  			processInvoiceContext.put("partyId", partyId);
  			processInvoiceContext.put("tallyrefNo", tallyrefNo);
  			if(UtilValidate.isNotEmpty(purposeTypeIdForDC)){
  				processInvoiceContext.put("purposeTypeId", "DEPOT_DIES_CHEM_SALE");
  			}
  			else{
  				processInvoiceContext.put("purposeTypeId", "DEPOT_YARN_SALE");
  			}
  			processInvoiceContext.put("vehicleId", vehicleId);
  			processInvoiceContext.put("orderId", orderId);
  			processInvoiceContext.put("shipmentId", shipmentId);
  			processInvoiceContext.put("invoiceDate", invoiceDate);
  			processInvoiceContext.put("purchaseTitleTransferEnumId", purchaseTitleTransferEnumId);
  			processInvoiceContext.put("purchaseTaxType", purchaseTaxType);
  			//processInvoiceContext.put("saleTitleTransferEnumId", saleTitleTransferEnumId);
  			//processInvoiceContext.put("invoiceAdjChargesList", invoiceAdjChargesList);
  			//processInvoiceContext.put("invoiceDiscountsList", invoiceDiscountsList);
  			
  			if(UtilValidate.isNotEmpty(isDisableAcctg)){
  				processInvoiceContext.put("isDisableAcctg", isDisableAcctg);
  			}
  			result = createDepotInvoice(dctx, processInvoiceContext);
  			if(ServiceUtil.isError(result)){
  				Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
  				request.setAttribute("_ERROR_MESSAGE_", "Unable to generate invoice  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
  				return "error";
  			}
  			
  			String invoiceId =  (String)result.get("invoiceId");
  			/*if(UtilValidate.isNotEmpty(invoiceId)){
  				GenericValue Shipment = null;
  				GenericValue invoice = null;
  		        try{
  		        	 Shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
  		             String supplierInvoiceId = Shipment.getString("supplierInvoiceId");
  		 			 Timestamp supplierInvoiceDate = (Timestamp)Shipment.get("supplierInvoiceDate");
  		 			 invoice = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId", invoiceId), false); 
  		             invoice.set("millInvoiceNo", supplierInvoiceId);
  		             invoice.set("millInvoiceDate",supplierInvoiceDate);
  		             try {
  		                 delegator.createOrStore(invoice);
  		             } catch (GenericEntityException e) {
  		                 Debug.logError(e, module);
  		                 request.setAttribute("_ERROR_MESSAGE_", "Error while storing referenceNumber and referenceDate in Invoice");
  		                 return "error";
  		             }
  		         }catch(Exception e){
  		             Debug.logError("failed to store storing referenceNumber and referenceDate::"+invoiceId, module);
  		              request.setAttribute("_ERROR_MESSAGE_", "failed to store referenceNumber and referenceDate::"+invoiceId);
  		              return "error";
  		         }
  			}*/

  			//============================================Rounding Off===============================
  			  List<GenericValue> InvoiceItem = null;
  		    	
  			    List conditionList = FastList.newInstance();
  		    	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
  		    	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
  		    	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
  		    	 try{
  		    	   InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
  		    	 
  		    	 }catch(GenericEntityException e){
  						Debug.logError(e, "Failed to retrive InvoiceItem ", module);
  					}
  			    
  		    	
  		    	 if(UtilValidate.isNotEmpty(InvoiceItem)){
  		        	 
  		    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
  		    		 
  		        	for(GenericValue eachInvoiceItem : InvoiceItem){
  		        	
  		        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
  		        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
  		        		BigDecimal itemValue = quantity.multiply(amount);
  		        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
  		        		
  		        		
  		        		
  		        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
  		        		
  		        		eachInvoiceItem.set("itemValue",roundedAmount);
  		        		
  		        		try{
  		        		eachInvoiceItem.store();
  		        		}catch(GenericEntityException e){
  		        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
  		        		}
  		        	}
  		        	
  		        	try{
  		        		
  		        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
  		        		
  		        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
  		        	InvoiceHeader.store();
  		        	}catch(GenericEntityException e){
  		    			Debug.logError(e, "Failed to Populate Invoice ", module);
  		    		}
  		        	
  		    	 }
  		    	 
  			

  		    	 //================= get purchase Invoice Details===============================
  		    	  		   
  		    	             conditionList.clear();
  		    	  			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
  		    	  			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, UtilMisc.toList("INV_RAWPROD_ITEM","TEN_PERCENT_SUBSIDY")));
  		    	  			List<GenericValue> invoiceItemListAdj =null;
  		    	  			
  		    	  			try{
  		    	  				invoiceItemListAdj = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
  		    	  		      
  		    	  			} catch (Exception e) {
  		    	  	   		  Debug.logError(e, "Error in fetching InvoiceItem ", module);
  		    	  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
  		    	  					return "error";
  		    	  			}

  		    	  			
  		    	  			//Debug.log("invoiceItemListAdj==================="+invoiceItemListAdj);
  		    	  			
  		    	  			if(UtilValidate.isNotEmpty(invoiceItemListAdj)){
  		    	  			
  		    	  			
  		    	  			
  		    	  			BigDecimal invoiceAdjTotal = BigDecimal.ZERO;
  		    	  			for (int i = 0; i < invoiceItemListAdj.size(); i++) {
  		    	  				
  		    					GenericValue eachInvoiceList = (GenericValue)invoiceItemListAdj.get(i);
  		    					invoiceAdjTotal = invoiceAdjTotal.add(eachInvoiceList.getBigDecimal("amount"));
  		    	           
  		    	  			}
  		    	  			
  		    	  			//Debug.log("invoiceItemListAdj=========vamsi==============="+invoiceItemListAdj);
  		    	  			
  		    	  			conditionList.clear();
  		    	  			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
  		    	  			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_RAWPROD_ITEM"));
  		    	  			List<GenericValue> invoiceItemList =null;
  		    	  			
  		    	  			try{
  		    	  		      invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
  		    	  		      
  		    	  			} catch (Exception e) {
  		    	  	   		  Debug.logError(e, "Error in fetching InvoiceItem ", module);
  		    	  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
  		    	  					return "error";
  		    	  			}
  		    	  			
  		    	  			
  		    	  		//Debug.log("invoiceItemList=========vamsi==============="+invoiceItemList);
  		    	  			
  		    	             for (int i = 0; i < invoiceItemList.size(); i++) {
  		    	  				
  		    					GenericValue eachInvoiceList = (GenericValue)invoiceItemList.get(i);
  		    					
  		    					String invoiceIdItem = eachInvoiceList.getString("invoiceId");
  		    					
  		    					String invoiceItemSeqIdItem = eachInvoiceList.getString("invoiceItemSeqId");
  		    					
  		    					BigDecimal quantity = eachInvoiceList.getBigDecimal("quantity");
  		    					
  		    					BigDecimal amount = eachInvoiceList.getBigDecimal("amount");
  		    					
  		    					BigDecimal itemTotal = quantity.multiply(amount);
  		    					
  		    					//Debug.log("itemTotal=============="+itemTotal);
  		    					
  		    					//Debug.log("invoiceAdjTotal=============="+invoiceAdjTotal);
  		    					
  		    					
  		    					conditionList.clear();
  	  		    	  			conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, invoiceId));
  	  		    	  		    conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqIdItem));
  	  		    	  		    conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
  	  		    	  		
  	  		    	  			List<GenericValue> invoiceItemAdjList =null;
  	  		    	  			
  	  		    	  			try{
  	  		    	  		      invoiceItemAdjList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
  	  		    	  		      
  	  		    	  			} catch (Exception e) {
  	  		    	  	   		  Debug.logError(e, "Error in fetching InvoiceItem ", module);
  	  		    	  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
  	  		    	  					return "error";
  	  		    	  			}
  	  		    	  			
  	  		    	  		//Debug.log("invoiceItemAdjList=======2323==vamsi==============="+invoiceItemAdjList);
  	  		    	  			
  	  		    	  		    BigDecimal AdjWithInvoAmt = BigDecimal.ZERO;
  		    					
  	  		    	  			for (GenericValue eachItem : invoiceItemAdjList) {
  	  		    	  			   AdjWithInvoAmt = AdjWithInvoAmt.add(eachItem.getBigDecimal("itemValue"));
								}
  	  		    	  			
  	  		    	  		//Debug.log("AdjWithInvoAmt=======2323==vamsi==============="+AdjWithInvoAmt);
  		    					
  	  		    	  		BigDecimal addToInventory = BigDecimal.ZERO;
  	  		    	  		
  	  		    	  	    double addToUnitPrice = 0;
  	  		    	  	      addToUnitPrice = AdjWithInvoAmt.doubleValue()/quantity.doubleValue();
  	  		    	  	      addToInventory = new BigDecimal(addToUnitPrice);
  		    					
  		    					//=====================get relevent Order and Seq==========================
  		    					
  		    					conditionList.clear();
  		    		  			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceIdItem));
  		    		  			conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqIdItem));
  		    		  			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
  		    		  			List<GenericValue> OrderItemBillingAndInvoiceAndInvoiceItemList =null;
  		    		  			
  		    		  			try{
  		    		  				OrderItemBillingAndInvoiceAndInvoiceItemList = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
  		    		  		      
  		    		  			} catch (Exception e) {
  		    		  	   		  //Debug.logError(e, "Error in fetching InvoiceItem ", module);
  		    		  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
  		    		  					return "error";
  		    		  			}
  		    					
  		    		  			GenericValue OrderItemBillingAndInvoiceAndInvoice = EntityUtil.getFirst(OrderItemBillingAndInvoiceAndInvoiceItemList);
  		    					
  		    		  			String orderIdBill = "";
  		    		  			String orderItemSeqIdBill = "";
  		    		  			if(UtilValidate.isNotEmpty(OrderItemBillingAndInvoiceAndInvoice)){
  		    		  				orderIdBill = (String)OrderItemBillingAndInvoiceAndInvoice.get("orderId");
  		    		  				orderItemSeqIdBill = (String)OrderItemBillingAndInvoiceAndInvoice.get("orderItemSeqId");
  		    		  			}
  		    		  			
  		    		  			conditionList.clear();
  		    		  			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderIdBill));
  		    		  			conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqIdBill));
  		    		  			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
  		    		  			List<GenericValue> ShipmentAndReceiptList =null;
  		    		  			
  		    		  			try{
  		    		  				ShipmentAndReceiptList = delegator.findList("ShipmentAndReceipt", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
  		    		  		      
  		    		  			} catch (Exception e) {
  		    		  	   		  //Debug.logError(e, "Error in fetching InvoiceItem ", module);
  		    		  				  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching InvoiceItem :" + invoiceId+"....! ");
  		    		  					return "error";
  		    		  			}
  		    	                GenericValue ShipmentAndReceipt = EntityUtil.getFirst(ShipmentAndReceiptList);
  		    	      			//Debug.log("addToInventory====================="+addToInventory);

  		    		  			String inventoryItemId = "";
  		    		  			if(UtilValidate.isNotEmpty(ShipmentAndReceipt)){
  		    		  				inventoryItemId = (String)ShipmentAndReceipt.get("inventoryItemId");
  		    		  			}
  		    	      			//Debug.log("inventoryItemId====================="+inventoryItemId);

  		    		  			// Map<String, Object> inventoryItemMap = UtilMisc.toMap("inventoryItemId", inventoryItemId,"unitCost",addToInventory, "userLogin", userLogin);
  		    		  	          try {
  		    		  	              // result = dispatcher.runSync("updateInventoryItem", inventoryItemMap);
  		    		  	      			//////Debug.log("result====================="+result);
  		    			  	      		//////Debug.log("inventoryItemId====================="+inventoryItemId);

  		    		  	      		 GenericValue InventoryItem = delegator.findOne("InventoryItem",UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
  		    		  	   		
  		    		  	      		 BigDecimal availablePrice = InventoryItem.getBigDecimal("unitCost");
  		    		  	      		 
  		    		  	      	     //Debug.log("amount====================="+amount);

  		    		  	      		 
  		    		  	      	     //Debug.log("InventoryItem====================="+InventoryItem);
  		    		  	      		InventoryItem.set("unitCost",addToInventory.add(amount));
  		    		  	      	    InventoryItem.store();
  		    		  	               
  		    		  	          } catch (Exception e) {
  		    		  	        	  
  		    		  	        	 //Debug.logError(e, "Error while populating updateInventoryItem ", module);
  		    		  				  request.setAttribute("_ERROR_MESSAGE_", "Error while populating updateInventoryItem :" + invoiceId+"....! ");
  		    		  					return "error";
  		    		  	          }
  		    	  			 }
  		    	  			
  		    	  		  } 
  		    	  			
  		    	  
  		    	  			
  		    		    	 
  		  	    		/* //=======================Change InvoiceRole==============================
  		               String roPartyId = null;
  		               
  		  	        try{
  		          		
  		          		GenericValue InvoiceHeaderForRole = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
  		          		
  		          		 String partyIdTo = InvoiceHeaderForRole.getString("partyId");
  		              	 String partyIdFrom1 =InvoiceHeaderForRole.getString("partyIdFrom");
  		               	 List<GenericValue> partyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo), null, null, null, false);
  		  					 GenericValue partyRel = EntityUtil.getFirst(partyRelationship);
  		  					 roPartyId = partyRel.getString("partyIdFrom");
  		  					 InvoiceHeaderForRole.set("partyId",roPartyId);
  		  					 InvoiceHeaderForRole.store();
  		                   GenericValue invoiceRole = delegator.findOne("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId,"partyId",partyIdTo,"roleTypeId","BILL_TO_CUSTOMER"), false);
  		                   invoiceRole.remove();
  		                   try{
  		                  	 GenericValue billToCustomerRole = delegator.makeValue("InvoiceRole");
  		                  	 billToCustomerRole.set("invoiceId", invoiceId);
  		                  	 billToCustomerRole.set("partyId", roPartyId);
  		                  	 billToCustomerRole.set("roleTypeId", "BILL_TO_CUSTOMER");
  		                       delegator.createOrStore(billToCustomerRole);
  		                   } catch (GenericEntityException e) {
  		                  	 Debug.logError(e, "Failed to Populate Invoice ", module);
  		                   }	
  		                   GenericValue supplierAgentRole = delegator.findOne("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId,"partyId",partyIdFrom1,"roleTypeId","SUPPLIER_AGENT"), false);
  		                   supplierAgentRole.remove();
  		                   try{
  		                  	 GenericValue billToCustRole = delegator.makeValue("InvoiceRole");
  		                  	 billToCustRole.set("invoiceId", invoiceId);
  		                  	 billToCustRole.set("partyId", partyIdTo);
  		                  	 billToCustRole.set("roleTypeId", "SUPPLIER_AGENT");
  		                       delegator.createOrStore(billToCustRole);
  		                   } catch (GenericEntityException e) {
  		                  	 Debug.logError(e, "Failed to Populate Invoice ", module);
  		                   }
  		          		
  		          		
						try{
						 GenericValue billToCustRole1 = delegator.makeValue("InvoiceRole");
						 billToCustRole1.set("invoiceId", invoiceId);
						 billToCustRole1.set("partyId", "Company");
						 billToCustRole1.set("roleTypeId", "ACCOUNTING");
						     delegator.createOrStore(billToCustRole1);
						 } catch (GenericEntityException e) {
							 Debug.logError(e, "Failed to Populate Invoice ", module);
						 }
						 
						 try{
							 GenericValue billToCustRole2 = delegator.makeValue("InvoiceRole");
						 billToCustRole2.set("invoiceId", invoiceId);
						 billToCustRole2.set("partyId", partyIdTo);
						 billToCustRole2.set("roleTypeId", "INTERNAL_ORGANIZATIO");
						     delegator.createOrStore(billToCustRole2);
						 } catch (GenericEntityException e) {
							 Debug.logError(e, "Failed to Populate Invoice ", module);
						 }
						
  		          		
  		          		
  		          		
  		          	}catch(GenericEntityException e){
  		      			Debug.logError(e, "Failed to Populate Invoice ", module);
  		      		}*/
  		  	    	 		
  		    	  			
  		    	  			
  		    	 
  		 		String invoiceSeq = org.ofbiz.accounting.invoice.InvoiceServices.getInvoiceSequence(delegator, invoiceId);
  		 		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId+" and Sequence : "+invoiceSeq);	  	 
  		 		
  		 		return "success";
  		 	}
  		
  		
  		public static String processNewSalesInvoiceDepot(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = (String) request.getParameter("partyId");
		Map resultMap = FastMap.newInstance();
		List invoices = FastList.newInstance(); 
		String vehicleId = (String) request.getParameter("vehicleId");
		String tallyrefNo = (String) request.getParameter("tallyrefNo");
		String invoiceDateStr = (String) request.getParameter("invoiceDate");
		String orderId = (String) request.getParameter("orderId");
		String isDisableAcctg = (String) request.getParameter("isDisableAcctg");
		String partyIdFrom = "";
		String shipmentId = (String) request.getParameter("shipmentId");
		String purchaseInvoiceId = (String) request.getParameter("purchaseInvoiceId");
		
		String saleTitleTransferEnumId = (String) request.getParameter("saleTitleTransferEnumId");
		String saleTaxType = (String) request.getParameter("saleTaxType");
		
		String purposeTypeId = (String) request.getParameter("purposeTypeId");
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
		
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "success";
		}
		List<GenericValue> orderItems=null;
		try{
		 orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		
	/*	if(UtilValidate.isNotEmpty(tallyrefNo)){
				  List conditionList = FastList.newInstance();
		
				  conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
				  conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS,"BackToBackOrder"));
				  EntityCondition assoc = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				  try{
				 // List  OrderAssocList = delegator.findList("OrderAssoc", assoc, null, null, null,false); 
				  
				   //if(UtilValidate.isNotEmpty(OrderAssocList)){
		
				 // GenericValue OrderAssoc = EntityUtil.getFirst(OrderAssocList);
				  //String actualOrderId = (String)OrderAssoc.get("toOrderId");
				  
				  GenericValue	OrderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId", orderId), false);
		
				  OrderHeader.set("tallyRefNo",tallyrefNo);
				  OrderHeader.store();
				// }
				
			}catch (Exception e) {
				Debug.logError(e, "Problems While Updating Tally Ref No: " + tallyrefNo, module);
				request.setAttribute("_ERROR_MESSAGE_", "Problems While Updating Tally Ref No: " + tallyrefNo);
				return "error";
			}
		}	*/
		
		List productQtyList = FastList.newInstance();
		List<Map> invoiceAdjChargesList = FastList.newInstance();
		List<Map> invoiceDiscountsList = FastList.newInstance();
		
	    List<GenericValue> toBillItems = FastList.newInstance();
	    
	    List<String> orderItemSeqIdList = FastList.newInstance();
	    
	    List ignoreAdjustmentsList = FastList.newInstance();
	    
	    String taxListStr = null;
		String orderAdjustmentsListStr = null;
		String discOrderAdjustmentsListStr = null;
		
		String applicableTo = "ALL";
		String applicableToDisc = "ALL";
		
		BigDecimal SERVICE_CHARGEBigDecimal = BigDecimal.ZERO;
		
		Map itemAdjMap = FastMap.newInstance();
		for (int i = 0; i < rowCount; i++) {
			
			List taxRateList = FastList.newInstance();
			List orderAdjustmentList = FastList.newInstance();
			List discOrderAdjustmentList = FastList.newInstance();
			
			Map prodQtyMap = FastMap.newInstance();
			
			String invoiceItemTypeId = "";
			String adjAmtStr = "";
			BigDecimal adjAmt = BigDecimal.ZERO;
			
			String tenPercentStr = "";
			BigDecimal tenPercentSubsidy = BigDecimal.ZERO;
			
			String invoiceItemDiscTypeId = "";
			String adjAmtDiscStr = "";
			String discQtyStr = "";
			BigDecimal adjDiscAmt = BigDecimal.ZERO;
			BigDecimal discQty = BigDecimal.ZERO;
			
			String vatStr=null;
			String cstStr=null;
			
			BigDecimal vat = BigDecimal.ZERO;
			BigDecimal cst = BigDecimal.ZERO;
			//percenatge of TAXes
			
			String VatPercentStr=null;
			String CSTPercentStr=null;
			String orderItemSeq = null;
			
			BigDecimal vatPercent=BigDecimal.ZERO;
			BigDecimal cstPercent=BigDecimal.ZERO; 
			
			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			
			String productId = "";
			String quantityStr = "";
			String unitPriceStr = "";
			String SERVICE_CHARGEStr = "";
			String orderItemSeqId = "";
			String assessableValue = "";
			String discAssessableValue = "";
			
			BigDecimal quantity = BigDecimal.ZERO;
			BigDecimal uPrice = BigDecimal.ZERO;
			
			
			
			
			
			/*if (paramMap.containsKey("invoiceItemTypeId" + thisSuffix)) {
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
			if (paramMap.containsKey("assessableValue" + thisSuffix)) {
				assessableValue = (String) paramMap.get("assessableValue" + thisSuffix);
			}
			//Debug.logassessableValue ============="+assessableValue);
			if(UtilValidate.isNotEmpty(invoiceItemTypeId) && adjAmt.compareTo(BigDecimal.ZERO)>0){
				  
				ignoreAdjustmentsList.add(invoiceItemTypeId);
				
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemTypeId);
				invItemMap.put("amount", adjAmt);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableTo);
				if(UtilValidate.isNotEmpty(assessableValue) && (assessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceAdjChargesList.add(invItemMap);	
			}
			
			
			if (paramMap.containsKey("invoiceItemTypeDiscId" + thisSuffix)) {
				invoiceItemDiscTypeId = (String) paramMap.get("invoiceItemTypeDiscId" + thisSuffix);
			}
			
			if (paramMap.containsKey("applicableToDisc" + thisSuffix)) {
				applicableToDisc = (String) paramMap.get("applicableToDisc" + thisSuffix);
			}
			
			if (paramMap.containsKey("adjDiscAmt" + thisSuffix)) {
				adjAmtDiscStr = (String) paramMap.get("adjDiscAmt" + thisSuffix);
			}
			
			if (paramMap.containsKey("discQty" + thisSuffix)) {
				discQtyStr = (String) paramMap.get("discQty" + thisSuffix);
			}
			if (paramMap.containsKey("assessableValueDisc" + thisSuffix)) {
				discAssessableValue = (String) paramMap.get("assessableValueDisc" + thisSuffix);
			}
			//Debug.logdiscAssessableValue ============="+discAssessableValue);
			if(UtilValidate.isNotEmpty(adjAmtDiscStr)){
				try {
					adjDiscAmt = new BigDecimal(adjAmtDiscStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + adjAmtStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + adjAmtStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(discQtyStr)){
				try {
					discQty = new BigDecimal(discQtyStr);
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing amount string: " + discQtyStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + discQtyStr);
					return "error";
				}
			}
			
			if(UtilValidate.isNotEmpty(invoiceItemDiscTypeId) && adjDiscAmt.compareTo(BigDecimal.ZERO)>0){
				
				BigDecimal adjQty = BigDecimal.ONE;
				if(UtilValidate.isNotEmpty(discQty)){
					if(discQty.doubleValue()>0){
						//adjDiscAmt = adjDiscAmt.divide(discQty);
						double tempAdjAmt = adjDiscAmt.doubleValue()/discQty.doubleValue();
						adjDiscAmt = new BigDecimal(tempAdjAmt);
						adjQty = discQty;
					}
				}
				
				ignoreAdjustmentsList.add(invoiceItemDiscTypeId);
				
				Map<String, Object> invItemMap = FastMap.newInstance();
				invItemMap.put("adjustmentTypeId", invoiceItemDiscTypeId);
				invItemMap.put("amount", adjDiscAmt);
				invItemMap.put("quantity", adjQty);
				invItemMap.put("uomId", "INR");
				invItemMap.put("applicableTo", applicableToDisc);
				if(UtilValidate.isNotEmpty(discAssessableValue) && (discAssessableValue.equals("true") || (assessableValue.equals("Y")) ) ){
					invItemMap.put("assessableValue", "Y");
				}
				invoiceDiscountsList.add(invItemMap);	
			}
			*/
			
			if (paramMap.containsKey("productId" + thisSuffix)) {
				productId = (String) paramMap.get("productId" + thisSuffix);
			}
			
			if (paramMap.containsKey("oritemseq" + thisSuffix)) {
				orderItemSeqId = (String) paramMap.get("oritemseq" + thisSuffix);
				
				orderItemSeqIdList.add(orderItemSeqId);
			}
			
			if(UtilValidate.isNotEmpty(productId)){
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
				}
				
				if (paramMap.containsKey("SERVICE_CHARGE" + thisSuffix)) {
					SERVICE_CHARGEStr = (String) paramMap.get("SERVICE_CHARGE" + thisSuffix);
					
					//Debug.logSERVICE_CHARGEStr============="+SERVICE_CHARGEStr);
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
					
					//Debug.logvatStr=============="+vatStr);
					
				}
				
				if (paramMap.containsKey("CST" + thisSuffix)) {
					cstStr = (String) paramMap.get("CST" + thisSuffix);
					
					//Debug.logcstStr=============="+cstStr);
					
				}
				
				if (paramMap.containsKey("VatPercent" + thisSuffix)) {
					VatPercentStr = (String) paramMap.get("VatPercent" + thisSuffix);
					
					//Debug.logVatPercentStr=============="+VatPercentStr);
					
				}
				
				if (paramMap.containsKey("CSTPercent" + thisSuffix)) {
					CSTPercentStr = (String) paramMap.get("CSTPercent" + thisSuffix);
					
					//Debug.logCSTPercentStr=============="+CSTPercentStr);
					
				}
				
				if (paramMap.containsKey("oritemseq" + thisSuffix)) {
					orderItemSeq = (String) paramMap.get("oritemseq" + thisSuffix);
				}
				
				if (paramMap.containsKey("taxList" + thisSuffix)) {
					taxListStr = (String) paramMap.get("taxList"
							+ thisSuffix);
					
					//Debug.logtaxListStr=============="+taxListStr);

					
					String[] taxList = taxListStr.split(",");
					for (int j = 0; j < taxList.length; j++) {
						String taxType = taxList[j];
						Map taxRateMap = FastMap.newInstance();
						taxRateMap.put("orderAdjustmentTypeId",taxType);
						taxRateMap.put("sourcePercentage",BigDecimal.ZERO);
						taxRateMap.put("amount",BigDecimal.ZERO);
						//taxRateMap.put("taxAuthGeoId", partyGeoId);
						
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
						
						List<GenericValue> ordAdj = null;
						try {
						    List ordAdjCond = FastList.newInstance();
						    ordAdjCond.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
						    ordAdjCond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeq));
						    ordAdjCond.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, taxType));
				    		
						    ordAdj = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(ordAdjCond, EntityOperator.AND), null, UtilMisc.toList("orderAdjustmentId"), null, false);
						    if(UtilValidate.isNotEmpty(ordAdj)){
								String orderAdjustmentId = (String)((GenericValue)EntityUtil.getFirst(ordAdj)).get("orderAdjustmentId");
								taxRateMap.put("orderAdjustmentId",orderAdjustmentId);
							}
						} catch (GenericEntityException e) {
					    	return "error";
				        }
						
						/*Map tempTaxMap = FastMap.newInstance();
						tempTaxMap.putAll(taxRateMap);
						
						taxRateList.add(tempTaxMap);*/
						
						GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxRateMap);
						taxRateList.add(orderAdjustment);
						ignoreAdjustmentsList.add(taxType);
					}
				}
				
				if (paramMap.containsKey("tenPercent" + thisSuffix)) {
					tenPercentStr = (String) paramMap.get("tenPercent" + thisSuffix);
				}
				
				if(UtilValidate.isNotEmpty(tenPercentStr)){
					try {
						tenPercentSubsidy = new BigDecimal(tenPercentStr);
						if(tenPercentSubsidy.compareTo(BigDecimal.ZERO)>0){
							tenPercentSubsidy = tenPercentSubsidy.negate();
						}
						
						Map taxRateMap = FastMap.newInstance();
						taxRateMap.put("orderAdjustmentTypeId","TEN_PERCENT_SUBSIDY");
						taxRateMap.put("sourcePercentage",new BigDecimal("10"));
						taxRateMap.put("amount",tenPercentSubsidy);
						
						try {
						    List ordAdjCond = FastList.newInstance();
						    ordAdjCond.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
						    ordAdjCond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeq));
						    ordAdjCond.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
				    		
						    List<GenericValue> tenPercAdj = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(ordAdjCond, EntityOperator.AND), null, UtilMisc.toList("orderItemSeqId"), null, false);
						    
						    if(UtilValidate.isNotEmpty(tenPercAdj)){
								String orderAdjustmentId = (String)((GenericValue)EntityUtil.getFirst(tenPercAdj)).get("orderAdjustmentId");
								taxRateMap.put("orderAdjustmentId",orderAdjustmentId);
							}
						    
						} catch (GenericEntityException e) {
					    	return "error";
				        }
						
						
						
						/*Map tempTaxMap = FastMap.newInstance();
						tempTaxMap.putAll(taxRateMap);*/
						
						GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", taxRateMap);
						taxRateList.add(orderAdjustment);
						
						ignoreAdjustmentsList.add("TEN_PERCENT_SUBSIDY");
						
					} catch (Exception e) {
						Debug.logError(e, "Problems parsing ten percent subsidy string: " + tenPercentStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing ten percent subsidy string: " + tenPercentStr);
						return "error";
					}
				}
				
				
				
				if (paramMap.containsKey("orderAdjustmentsList" + thisSuffix)) {
					orderAdjustmentsListStr = (String) paramMap.get("orderAdjustmentsList"+ thisSuffix);
					
					//Debug.logorderAdjustmentsListStr=============="+orderAdjustmentsListStr);
					
					String[] orderAdjustmentsList = orderAdjustmentsListStr.split(",");
					for (int j = 0; j < orderAdjustmentsList.length; j++) {
						String orderAdjustmentType = orderAdjustmentsList[j];
						Map adjTypeMap = FastMap.newInstance();
						adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
						adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
						adjTypeMap.put("amount",BigDecimal.ZERO);
						//adjTypeMap.put("taxAuthGeoId", partyGeoId);
						
						if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
							String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
							if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
								adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
							}
							
						}
						if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								adjTypeMap.put("amount",new BigDecimal(taxAmt));
							}
						}
						if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
							String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
							if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
								if(isAssessableValue.equals("TRUE")){
									adjTypeMap.put("isAssessableValue", "Y");
									//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
								}
							}
						}
						Map tempAdjMap = FastMap.newInstance();
						tempAdjMap.putAll(adjTypeMap);
						
						
						GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", tempAdjMap);
						taxRateList.add(orderAdjustment);
						
						ignoreAdjustmentsList.add(orderAdjustmentType);
						
						orderAdjustmentList.add(tempAdjMap);
					}
				}
				////Debug.logassessableAdjustmentAmount ================="+assessableAdjustmentAmount);
				
				if (paramMap.containsKey("discOrderAdjustmentsList" + thisSuffix)) {
					discOrderAdjustmentsListStr = (String) paramMap.get("discOrderAdjustmentsList"+ thisSuffix);
					
					//Debug.logdiscOrderAdjustmentsListStr================="+discOrderAdjustmentsListStr);
					
					String[] orderAdjustmentsList = discOrderAdjustmentsListStr.split(",");
					for (int j = 0; j < orderAdjustmentsList.length; j++) {
						String orderAdjustmentType = orderAdjustmentsList[j];
						Map adjTypeMap = FastMap.newInstance();
						adjTypeMap.put("orderAdjustmentTypeId",orderAdjustmentType);
						adjTypeMap.put("sourcePercentage",BigDecimal.ZERO);
						adjTypeMap.put("amount",BigDecimal.ZERO);
						//adjTypeMap.put("taxAuthGeoId", partyGeoId);
						
						
						//Debug.logorderAdjustmentType + thisSuffix========12121========="+orderAdjustmentType + thisSuffix);
						
						if (paramMap.containsKey(orderAdjustmentType + thisSuffix)) {
							String adjPercentage = (String) paramMap.get(orderAdjustmentType + thisSuffix);
							
							//Debug.logparamMap========12121========="+paramMap.keySet());
							
							//Debug.logadjPercentage========12121========="+adjPercentage);
							if(UtilValidate.isNotEmpty(adjPercentage) && !(adjPercentage.equals("NaN"))){
								adjTypeMap.put("sourcePercentage",new BigDecimal(adjPercentage));
							}
							
						}
						if (paramMap.containsKey(orderAdjustmentType+ "_AMT" + thisSuffix)) {
							String taxAmt = (String) paramMap.get(orderAdjustmentType+ "_AMT" + thisSuffix);
							if(UtilValidate.isNotEmpty(taxAmt) && !(taxAmt.equals("NaN"))){
								BigDecimal taxAmtBd = new BigDecimal(taxAmt);
								if(taxAmtBd.compareTo(BigDecimal.ZERO)>0){
									taxAmtBd = taxAmtBd.negate();
								}
								adjTypeMap.put("amount",taxAmtBd);
							}
						}
						if (paramMap.containsKey(orderAdjustmentType+ "_INC_BASIC" + thisSuffix)) {
							String isAssessableValue = (String) paramMap.get(orderAdjustmentType+ "_INC_BASIC" + thisSuffix);
							if(UtilValidate.isNotEmpty(isAssessableValue) && !(isAssessableValue.equals("NaN"))){
								if(isAssessableValue.equals("TRUE")){
									adjTypeMap.put("isAssessableValue", "Y");
									//assessableAdjustmentAmount = assessableAdjustmentAmount.add((BigDecimal) adjTypeMap.get("amount"));
								}
							}
						}
						Map tempAdjMap = FastMap.newInstance();
						tempAdjMap.putAll(adjTypeMap);
						
						GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", tempAdjMap);
						taxRateList.add(orderAdjustment);
						
						discOrderAdjustmentList.add(tempAdjMap);
						ignoreAdjustmentsList.add(orderAdjustmentType);
					}
				}
				////Debug.logassessableAdjustmentAmount ================="+assessableAdjustmentAmount);
				
				//Debug.logtaxRateList =================== "+taxRateList);
				
				
				
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
						
						//Debug.logvat================"+vat);
						
					}
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing VAT string: " + vatStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing VAT string: " + vatStr);
					return "error";
				}

				try {
					if (!cstStr.equals("")) {
						cst = new BigDecimal(cstStr);
						
						//Debug.logcst================"+cst);
						
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
						
						//Debug.logvatPercent================"+vatPercent);
						
					}
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing VatPercent string: " + VatPercentStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing VatPercent string: " + VatPercentStr);
					return "error";
				}
				
				try {
					if (!CSTPercentStr.equals("")) {
						cstPercent = new BigDecimal(CSTPercentStr);
						
						//Debug.logcstPercent================"+cstPercent);
						
					}
					
					
					
				} catch (Exception e) {
					Debug.logError(e, "Problems parsing CSTPercent string: " + CSTPercentStr, module);
					request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + CSTPercentStr);
					return "error";
				}
				
				
				try {
					if (!SERVICE_CHARGEStr.equals("")) {
						SERVICE_CHARGEBigDecimal = new BigDecimal(SERVICE_CHARGEStr);
						//Debug.logSERVICE_CHARGEBigDecimal================"+SERVICE_CHARGEBigDecimal);
					}
					}catch (Exception e) {
						Debug.logError(e, "Problems parsing CSTPercent string: " + SERVICE_CHARGEStr, module);
						request.setAttribute("_ERROR_MESSAGE_", "Problems parsing CSTPercent string: " + SERVICE_CHARGEStr);
						return "error";
					}
					
				
			}
			GenericValue orderItemValue = null;

			if(UtilValidate.isNotEmpty(productId)){
				try{
					List<GenericValue> orderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));				
					if(UtilValidate.isNotEmpty(orderItem)){
						orderItemValue = EntityUtil.getFirst(orderItem);
						orderItemValue.put("unitPrice", uPrice);
						orderItemValue.put("unitListPrice", uPrice);
						orderItemValue.store();
					}else{
						continue;
					}
				}catch (Exception e) {
					  Debug.logError(e, "Error While Updating purchase Order ", module);
					  request.setAttribute("_ERROR_MESSAGE_", "Unable to update Purchase Order :" + orderId+"....! ");
						return "error";
				}
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
			
			//=====================================================================================

			List<GenericValue> salesOrderitems = null;
			
			 try {
				     List itemAssocCond = FastList.newInstance();
			    	itemAssocCond.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			    	itemAssocCond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeq));
			    	itemAssocCond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		    		
			      	salesOrderitems = delegator.findList("OrderItem", EntityCondition.makeCondition(itemAssocCond, EntityOperator.AND), null, UtilMisc.toList("orderItemSeqId"), null, false);
			    } catch (GenericEntityException e) {
			    	/* Debug.logError("AccountingTroubleCallingCreateInvoiceForOrderService: " + ServiceUtil.getErrorMessage(salesOrderitems), module);
			  			request.setAttribute("_ERROR_MESSAGE_", "Unable to analyse purpose type :"+ServiceUtil.getErrorMessage(salesOrderitems));
	*/		  			return "error";
		        }

				GenericValue eachItem = null;
			 
				if(UtilValidate.isNotEmpty(salesOrderitems)){
					eachItem = EntityUtil.getFirst(salesOrderitems);
					
					BigDecimal billedQuantity = OrderReadHelper.getOrderItemInvoicedQuantity(eachItem);

					
					if(UtilValidate.isNotEmpty(quantity))
					eachItem.set("quantity", quantity.add(billedQuantity));
					if(UtilValidate.isNotEmpty(uPrice))
					eachItem.set("unitPrice", uPrice);
					//eachItem.set("taxRateList", taxRateList);
					
					itemAdjMap.put(orderItemSeq, taxRateList);
					
					
						
					toBillItems.add(eachItem);
        
			}
			
		}//end row count for loop
		
		if( UtilValidate.isEmpty(productQtyList)){
			Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
			request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
			return "success";
		}
		
		//Debug.logvamsi======begin================");
		
		//Debug.logignoreAdjustmentsList ========vamsi 121212========="+ignoreAdjustmentsList);
	
		//Debug.logitemAdjMap =========vadfafd12121212========"+itemAdjMap);
		
		//Timestamp nowTimeStamp = 
		  //String invoiceId = null;
		if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("DEPOT_DIES_CHEM_SALE"))
			purposeTypeId = "DEPOT_DIES_CHEM_SALE";
		else
			purposeTypeId = "DEPOT_YARN_SALE";
		try{
		  GenericValue	OrderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId", orderId), false);

		  shipmentId = (String) OrderHeader.getString("shipmentId");
		} catch (Exception e) {
            Debug.logError(e, "Problem calling the OrderHeader Entity", module);
        }
		  Map<String, Object> serviceContext = UtilMisc.toMap("orderId", orderId,"billItems", toBillItems, "eventDate", invoiceDate, "userLogin", userLogin, "ignoreAdjustmentsList", ignoreAdjustmentsList, "itemAdjMap", itemAdjMap,"purposeTypeId",purposeTypeId);
          serviceContext.put("shipmentId",shipmentId);
          try {
               result = dispatcher.runSync("createInvoiceForOrderOrig", serviceContext);
              String invoiceId1 = (String) result.get("invoiceId");
          } catch (GenericServiceException e) {
              
              Debug.logError("AccountingTroubleCallingCreateInvoiceForOrderService: " + ServiceUtil.getErrorMessage(result), module);
  			request.setAttribute("_ERROR_MESSAGE_", "Unable to analyse purpose type :"+ServiceUtil.getErrorMessage(result));
  			return "error";
        }
		
		// Get Purpose type based on product
		
		String invProdId = (String) ((Map) productQtyList.get(0)).get("productId");
		
		/*try{
	  		Map resultCtx = dispatcher.runSync("getPurposeTypeForProduct", UtilMisc.toMap("productId", invProdId, "userLogin", userLogin));  	
	  		purposeTypeId = (String)resultCtx.get("purposeTypeId");
	  	}catch (GenericServiceException e) {
	  		Debug.logError("Unable to analyse purpose type: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to analyse purpose type :"+ServiceUtil.getErrorMessage(result));
			return "error";
	  	}*/
		
		
		
		/*Map processInvoiceContext = FastMap.newInstance();
		processInvoiceContext.put("userLogin", userLogin);
		processInvoiceContext.put("productQtyList", productQtyList);
		processInvoiceContext.put("partyId", partyId);
		processInvoiceContext.put("purposeTypeId", purposeTypeId);
		processInvoiceContext.put("vehicleId", vehicleId);
		processInvoiceContext.put("orderId", orderId);
		processInvoiceContext.put("shipmentId", shipmentId);
		processInvoiceContext.put("invoiceDate", invoiceDate);
		processInvoiceContext.put("invoiceAdjChargesList", invoiceAdjChargesList);
		processInvoiceContext.put("invoiceDiscountsList", invoiceDiscountsList);
		
		if(UtilValidate.isNotEmpty(isDisableAcctg)){
			processInvoiceContext.put("isDisableAcctg", isDisableAcctg);
		}
		//result = createDepotSalesInvoice(dctx, processInvoiceContext);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable to generate invoice: " + ServiceUtil.getErrorMessage(result), module);
			request.setAttribute("_ERROR_MESSAGE_", "Unable to generate invoice  For party :" + partyId+"....! "+ServiceUtil.getErrorMessage(result));
			return "error";
		}*/
		
		String invoiceId =  (String)result.get("invoiceId");
		
		// Invoice Attributes (Taxes)
		Map<String, Object> createInvAttribute = UtilMisc.toMap(
                "invoiceId", invoiceId,
                "invoiceAttrName", "saleTitleTransferEnumId",
                "invoiceAttrValue", saleTitleTransferEnumId, 
                "userLogin", userLogin);

        try {
        	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
        } catch (GenericServiceException gse) {
            Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
        }
		
        createInvAttribute = UtilMisc.toMap(
                "invoiceId", invoiceId,
                "invoiceAttrName", "saleTaxType",
                "invoiceAttrValue", saleTaxType, 
                "userLogin", userLogin);

        try {
        	dispatcher.runSync("createInvoiceAttribute", createInvAttribute);
        } catch (GenericServiceException gse) {
            Debug.logError(gse, "Problem calling the createInvoiceAttribute service", module);
        }
		
	    
       	 try{
    	    	GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
    	    	if(UtilValidate.isNotEmpty(purposeTypeId) && purposeTypeId.equals("DEPOT_DIES_CHEM_SALE"))
    				purposeTypeId = "DEPOT_DIES_CHEM_SALE";
    			else
    				purposeTypeId = "DEPOT_YARN_SALE";
    	    	
    	    	invoice.set("purposeTypeId", purposeTypeId);
    	    	if(UtilValidate.isNotEmpty(tallyrefNo))
    	    	invoice.set("referenceNumber", tallyrefNo);
    	    	
    	    	invoice.store();
    		} catch (Exception e) {
    	       Debug.logError(e, "Error in fetching InvoiceItem ", module);
   			  request.setAttribute("_ERROR_MESSAGE_", "Error in populating invoice purpose :" + purchaseInvoiceId+"....! ");
   				return "error";
    		}

	   
	    
	    
	  //============================================Service Charge===============================
	    
	    if(UtilValidate.isNotEmpty(SERVICE_CHARGEBigDecimal) && SERVICE_CHARGEBigDecimal.compareTo(BigDecimal.ZERO)>0){
    	Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
	    
	    createInvoiceItemContext.put("invoiceId",invoiceId);
        createInvoiceItemContext.put("invoiceItemTypeId", "INVOICE_ITM_ADJ");
        createInvoiceItemContext.put("parentInvoiceId", invoiceId);
        createInvoiceItemContext.put("description", "Service Charge");
        createInvoiceItemContext.put("quantity",BigDecimal.ONE);
        createInvoiceItemContext.put("amount",SERVICE_CHARGEBigDecimal);
        createInvoiceItemContext.put("productId", "");
        createInvoiceItemContext.put("userLogin", userLogin);
        try{
        	Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
    	    //Debug.logcreateInvoiceItemResult========Service Charge========="+createInvoiceItemResult);

        	if(ServiceUtil.isError(createInvoiceItemResult)){
      			  request.setAttribute("_ERROR_MESSAGE_", "Error in populating invoice purpose :" + invoiceId+"....! ");
      				return "error";
      		}
        } catch (Exception e) {
        	request.setAttribute("_ERROR_MESSAGE_", "Error in populating Service Charge : ");
			return "error";
		}
	    
        
        List<GenericValue> InvoiceServiceItem = null;
        String invoiceItemSeqId = "";
	    List conditionList = FastList.newInstance();
    	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
    	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INVOICE_ITM_ADJ"));
    	conditionList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"Service Charge"));
    	 try{
    		 InvoiceServiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
    		 
      		GenericValue InvoiceService = EntityUtil.getFirst(InvoiceServiceItem);

      		invoiceItemSeqId = (String)InvoiceService.get("invoiceItemSeqId");
    		 
    	 }catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
			}
        
        
    	 List<GenericValue> orderAdjServiceCharge = null;
     	
    	 String orderAdjustmentId = "";
 	    conditionList.clear();
     	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
     	conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS,"SERVICE_CHARGE"));
     	 try{
     		orderAdjServiceCharge = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
       		GenericValue orderAdjServiceChargeItem = EntityUtil.getFirst(orderAdjServiceCharge);
       		 orderAdjustmentId = (String)orderAdjServiceChargeItem.get("orderAdjustmentId");
     		 
     	 }catch(GenericEntityException e){
 				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
 			}
    	 
    	 

   		GenericValue newItemAttr = delegator.makeValue("OrderAdjustmentBilling");    
   		
				newItemAttr.set("orderAdjustmentId", orderAdjustmentId);
				newItemAttr.set("invoiceId", invoiceId);
				newItemAttr.set("invoiceItemSeqId", invoiceItemSeqId);
				newItemAttr.set("quantity", BigDecimal.ZERO);
				newItemAttr.set("amount",SERVICE_CHARGEBigDecimal);
				 try{
			  	   newItemAttr.create();
				 }catch (Exception e) {
					 request.setAttribute("_ERROR_MESSAGE_", "Error in populating OrderAdjustmentBilling : ");
						return "error";
		 	 	}
    	 
    	 
        
	    }
	    
	  //============================================Rounding Off===============================
	    
	    List<GenericValue> InvoiceItem = null;
    	
	    List conditionList = FastList.newInstance();
    	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
    	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
    	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
    	 try{
    	   InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
    	 
    	 }catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
			}
	    
	    
    	 if(UtilValidate.isNotEmpty(InvoiceItem)){
        	 
    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
    		 
        	for(GenericValue eachInvoiceItem : InvoiceItem){
        	
        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
        		BigDecimal itemValue = quantity.multiply(amount);
        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
        		
        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
        		
        		eachInvoiceItem.set("itemValue",roundedAmount);
        		
        		try{
        		eachInvoiceItem.store();
        		}catch(GenericEntityException e){
        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
        		}
        	}
        	
        	try{
        		
        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
        		
        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
        	InvoiceHeader.store();
        	}catch(GenericEntityException e){
    			Debug.logError(e, "Failed to Populate Invoice ", module);
    		}
        	
        	
        	//===============populate Claim Amount in Shipment=======
        	
        	
        	
        	 conditionList.clear();
     		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
     		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
     		//conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderIteSeq));
     		List<GenericValue> OrderItemBillingAndInvoiceAndInvoiceItem =null;
     		try{
     			OrderItemBillingAndInvoiceAndInvoiceItem = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
     	      
     		} catch (Exception e) {
        		  Debug.logError(e, "Error in fetching InvoiceItem ", module);
     			  request.setAttribute("_ERROR_MESSAGE_", "Error in fetching Order Item billing :" + invoiceId+"....! ");
     				return "error";
     		}
     		
     		
     		GenericValue OrderItemBillingAndInvoice = EntityUtil.getFirst(OrderItemBillingAndInvoiceAndInvoiceItem);
     		String SaleOrderId = OrderItemBillingAndInvoice.getString("orderId");
        	
     		String Scheam = "";
     		try{
	     		List<GenericValue> orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, SaleOrderId), null, null, null, false);
				
				List<GenericValue> scheamList = EntityUtil.filterByCondition(orderAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
				
				if(UtilValidate.isNotEmpty(scheamList)){
					GenericValue orderScheme = EntityUtil.getFirst(scheamList);
					Scheam = (String) orderScheme.get("attrValue");
				}
     		 }catch(Exception e){
				 Debug.logError("problem While Fetching OrderAttribute : "+e, module);
			 } 
        	
			
			if(UtilValidate.isNotEmpty(Scheam) && Scheam != "General"){
				
				BigDecimal reimbursementEligibilityPercentage = new BigDecimal(2);
				
				BigDecimal DepotreimbursementEligibilityPercentage = new BigDecimal(2);
				
				try{
				GenericValue shipmentObj=delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
				
				
	  			String partyIdTo = shipmentObj.getString("partyIdTo");
	  			
	  			
	  			//=====================party is IsDepo or not
				
				BigDecimal receiptEligablityAmount=shipmentObj.getBigDecimal("estimatedShipCost");
				
				BigDecimal invoiceEligablityAmount=(invoiceGrandTotal.multiply(reimbursementEligibilityPercentage)).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
				BigDecimal finalEligablityAmount=receiptEligablityAmount.compareTo(invoiceEligablityAmount)>0?invoiceEligablityAmount:receiptEligablityAmount;
				
				
				finalEligablityAmount = (finalEligablityAmount.setScale(0, rounding));
				
				shipmentObj.set("claimAmount",finalEligablityAmount);
				shipmentObj.set("claimStatus","APPLYED");
				if(finalEligablityAmount.compareTo(BigDecimal.ZERO)<=0){
					shipmentObj.set("claimStatus","");
				}
				shipmentObj.store();
				 }catch(Exception e){
					 Debug.logError("populate claim in Shipment Reimbursment : "+e, module);
				 } 
				
			}
    	 }
    	 
    	 
		   /*		   
		   //===========================Change the Roles in invoice=========================================
	    	 
 	    String roPartyId = null;
 	 
          try{
     		
     		GenericValue InvoiceHeaderForRole = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);	
     		
     		
     		 String partyIdFrom1 = InvoiceHeaderForRole.getString("partyIdFrom");
         	 List<GenericValue> partyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdFrom1), null, null, null, false);
				 GenericValue partyRel = EntityUtil.getFirst(partyRelationship);
				 roPartyId = partyRel.getString("partyIdFrom");
				 InvoiceHeaderForRole.set("partyIdFrom",roPartyId);
				 InvoiceHeaderForRole.store();
              GenericValue invoiceRole = delegator.findOne("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId,"partyId",partyIdFrom1,"roleTypeId","BILL_FROM_VENDOR"), false);
              String custmentPartyId = invoiceRole.getString("partyId");
        	     invoiceRole.remove();
        	     
        	     
        	  try{
          	 GenericValue billFromVendorRole = delegator.makeValue("InvoiceRole");
          	 billFromVendorRole.set("invoiceId", invoiceId);
          	 billFromVendorRole.set("partyId", roPartyId);
          	 billFromVendorRole.set("roleTypeId", "BILL_FROM_VENDOR");
               delegator.createOrStore(billFromVendorRole);
               GenericValue customerAgentRole = delegator.makeValue("InvoiceRole");
               customerAgentRole.set("invoiceId", invoiceId);
               customerAgentRole.set("partyId", custmentPartyId);
               customerAgentRole.set("roleTypeId", "CUSTOMER_AGENT");
               delegator.createOrStore(customerAgentRole);
           } catch (GenericEntityException e) {
         	  Debug.logError(e, "Failed to Populate Invoice ", module);
           }	
           
           
            try{
            	 GenericValue billToCustRole1 = delegator.makeValue("InvoiceRole");
            	 billToCustRole1.set("invoiceId", invoiceId);
            	 billToCustRole1.set("partyId", "Company");
            	 billToCustRole1.set("roleTypeId", "ACCOUNTING");
                 delegator.createOrStore(billToCustRole1);
             } catch (GenericEntityException e) {
            	 Debug.logError(e, "Failed to Populate Invoice ", module);
             }
             
             try{
            	 GenericValue billToCustRole2 = delegator.makeValue("InvoiceRole");
            	 billToCustRole2.set("invoiceId", invoiceId);
            	 billToCustRole2.set("partyId", custmentPartyId);
            	 billToCustRole2.set("roleTypeId", "INTERNAL_ORGANIZATIO");
                 delegator.createOrStore(billToCustRole2);
             } catch (GenericEntityException e) {
            	 Debug.logError(e, "Failed to Populate Invoice ", module);
             }
     		
     		
     	}catch(GenericEntityException e){
 			Debug.logError(e, "Failed to Populate Invoice ", module);
 		}
 	 
	   //======================================================================================
  	 */
		   

    	String invoiceSeq = org.ofbiz.accounting.invoice.InvoiceServices.getInvoiceSequence(delegator, invoiceId);
 		request.setAttribute("_EVENT_MESSAGE_", "Invoice created with Id : "+invoiceId+" and Sequence : "+invoiceSeq);
 		request.setAttribute("invoiceId",invoiceId);
 	
 		return "success";
  		}
 	
  		
  	    
  	    public static Map<String, Object> sendEmaiFromAnalytics(DispatchContext ctx, Map context) {
  	    	Delegator delegator = ctx.getDelegator();
  			LocalDispatcher dispatcher = ctx.getDispatcher();
  			GenericValue userLogin = (GenericValue) context.get("userLogin");
  			Map result = ServiceUtil.returnSuccess();
  			
  			String sendFrom = (String) context.get("sendFrom");
  			String sendTo = (String) context.get("sendTo");
  			String subject = (String) context.get("subject");
  			String sendCc = (String) context.get("sendCc");
  			String bodyText = (String) context.get("bodyText");
  			
  			
  			 try{ 
		        	Map partyEmailInfo =FastMap.newInstance();
		        	partyEmailInfo.put("sendTo", sendTo);
		        	partyEmailInfo.put("sendFrom", UtilProperties.getPropertyValue("general.properties", "defaultFromEmailAddress"));
		        	partyEmailInfo.put("sendCc", sendCc);
		        	partyEmailInfo.put("subject", subject);
		        	partyEmailInfo.put("userLogin", userLogin);
		        	partyEmailInfo.put("bodyText", bodyText);
		        	Map partyInfoListResult = dispatcher.runSync("sendMailFromScreen", partyEmailInfo);
		        	if (ServiceUtil.isError(partyInfoListResult)) {
		        		return ServiceUtil.returnError("Unable to send Email To  "+ sendTo);
					}else{
						Debug.log("Successfully Sent Email  To" +sendTo);
					}
		        }catch(GenericServiceException e1){
	            	Debug.log("Problem in sending email");
		        }
  			
  			
  			result = ServiceUtil.returnSuccess("Successfully Sent Email  To :" +sendTo);
  	        
  			return result;
  	    
  	    }    
  	    
  	    
  	    
  	  public static Map<String, Object> increaseInventoryQuantity(DispatchContext ctx, Map context) {
	    	Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map result = ServiceUtil.returnSuccess();
			
			String orderId = (String) context.get("orderId");
			String orderItemSeqId = (String) context.get("orderItemSeqId");
			BigDecimal givenQuantity = (BigDecimal) context.get("quantity");
			BigDecimal differenceQuantity = (BigDecimal) context.get("differenceQuantity");
			String shipmentId = (String) context.get("shipmentId");
			
			
			
			List conList1 = FastList.newInstance();
			
			List<GenericValue> OrderItem =null;
			List<GenericValue> OrderItemDetail =null;
			try {
			
			conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
			conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
     		EntityCondition cond1=EntityCondition.makeCondition(conList1,EntityOperator.AND);
			OrderItem = delegator.findList("OrderItem", cond1, null, null, null, false);
			
			}catch(Exception e1){
            	Debug.log("Problem in sending OrderItem");
	        }
			
			GenericValue OrderItem1 = EntityUtil.getFirst(OrderItem);
			
			BigDecimal quantity0 = OrderItem1.getBigDecimal("quantity");
			
			if(quantity0.doubleValue() < givenQuantity.doubleValue()){
			
			try{
			conList1.clear();
			conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
			conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
     		EntityCondition cond2=EntityCondition.makeCondition(conList1,EntityOperator.AND);
     		OrderItemDetail = delegator.findList("OrderItemDetail", cond2, null, null, null, false);
			}catch(Exception e1){
            	Debug.log("Problem in sending OrderItem");
	        }
			
     		GenericValue OrderItemDetail1 = EntityUtil.getFirst(OrderItemDetail);
			 
			if(UtilValidate.isNotEmpty(OrderItem1)){
			
			 BigDecimal quantity = OrderItem1.getBigDecimal("quantity");	
				
			 try {
			 if(quantity.doubleValue() < givenQuantity.doubleValue()){
				 OrderItem1.set("quantity", givenQuantity);
				 OrderItem1.store();
			 }
			 }catch(Exception e1){
	            	Debug.log("Problem in sending OrderItem");
		        }
			 
			 
			}
			
			if(UtilValidate.isNotEmpty(OrderItemDetail1)){
				
				 BigDecimal quantity = OrderItemDetail1.getBigDecimal("quantity");	
			 try{		
				 if(quantity.doubleValue() < givenQuantity.doubleValue()){
					 OrderItemDetail1.set("quantity", givenQuantity);
					 OrderItemDetail1.store();
				 }
				 
			}catch(Exception e1){
            	Debug.log("Problem in sending OrderItem");
	        }
				 
			}
			 
			/*List<GenericValue> Shipment = null;
			
			 try{ 
				    List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, orderId));
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
					EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<String> orderBy2 = UtilMisc.toList("-shipmentId");
					 Shipment = delegator.findList("Shipment", condExpr, null, orderBy2, null, false);
					if(UtilValidate.isEmpty(Shipment)){
						Debug.logError("GRN not found for the shipment: "+orderId, module);
						return ServiceUtil.returnError("GRN not found for the shipment: "+orderId);
					}
		        }catch(Exception e1){
	            	Debug.log("Problem in sending email");
		        }
			
			 GenericValue ShipmentFirst = EntityUtil.getFirst(Shipment);
			 
			 String shipmentId = ShipmentFirst.getString("shipmentId");
			 String shipmentItemSeqId = ShipmentFirst.getString("shipmentItemSeqId");*/
			 
			
			 
			 
			 if(UtilValidate.isNotEmpty(shipmentId)){
			 
				 
				 try {
		        	 Map<String, Object> updateShipmentCtx = FastMap.newInstance();
		        	 updateShipmentCtx.put("userLogin", context.get("userLogin"));
		        	 updateShipmentCtx.put("shipmentId", shipmentId);
		        	 updateShipmentCtx.put("shipmentItemSeqId","00001");   
		        	 updateShipmentCtx.put("quantity",givenQuantity);
		             dispatcher.runSync("updateShipmentItem", updateShipmentCtx);
		        }
		        catch (GenericServiceException e) {
		    		Debug.logError(e, "Failed to update shipment status " + shipmentId, module);
		    		return ServiceUtil.returnError("Failed to update shipment status " + shipmentId + ": " + e);          	
		        }
				 
				 
				 
				 List<GenericValue> ShipmentReceipt = null;
				 String inventoryItemId = null;
			 
			 try{ 
				    List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
					conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
					EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					ShipmentReceipt = delegator.findList("ShipmentReceipt", condExpr, null, null, null, false);
					
					GenericValue ShipmentReceiptFirst = EntityUtil.getFirst(ShipmentReceipt);
					
					 inventoryItemId = ShipmentReceiptFirst.getString("inventoryItemId");
					
					if(UtilValidate.isEmpty(ShipmentReceiptFirst)){
						Debug.logError("GRN not found for the shipment: "+orderId, module);
						return ServiceUtil.returnError("GRN not found for the shipment: "+orderId);
					}
					
					ShipmentReceiptFirst.set("quantityAccepted", givenQuantity);
					ShipmentReceiptFirst.set("deliveryChallanQty", givenQuantity);
					ShipmentReceiptFirst.store();
					
		        }catch(Exception e1){
	            	Debug.log("Problem in sending email");
		        }
			 
			 
		/*	 BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
			 BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
			 
			 try{
				 GenericValue InventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
				 
				  availableToPromiseTotal = InventoryItem.getBigDecimal("availableToPromiseTotal");
				  quantityOnHandTotal = InventoryItem.getBigDecimal("quantityOnHandTotal");
				 
				 BigDecimal increaseAvailableQuantity = givenQuantity.subtract(quantityOnHandTotal);
				 
				 availableToPromiseTotal = availableToPromiseTotal.add(increaseAvailableQuantity);
				 
				 InventoryItem.set("quantityOnHandTotal", givenQuantity);
				 InventoryItem.set("availableToPromiseTotal", availableToPromiseTotal);
				 InventoryItem.store();
				   
				}catch(GenericEntityException e){
					Debug.logError(e, "Error fetching partyId " + inventoryItemId, module);
				}
			 
			 try{
				 GenericValue InventoryItemDetail = delegator.findOne("InventoryItemDetail", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
				 
				 InventoryItemDetail.set("quantityOnHandTotal", givenQuantity);
				 InventoryItemDetail.store();
				   
				}catch(GenericEntityException e){
					Debug.logError(e, "Error fetching partyId " + inventoryItemId, module);
				}
			    
			 List<GenericValue> InventoryItemDetail = null;
			 
			 try{ 
				    List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId));
					EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					InventoryItemDetail = delegator.findList("InventoryItemDetail", condExpr, null, null, null, false);
					
					GenericValue InventoryItemDetailFirst = EntityUtil.getFirst(InventoryItemDetail);
					
					
					if(UtilValidate.isEmpty(InventoryItemDetailFirst)){
						Debug.logError("GRN not found for the shipment: "+orderId, module);
						return ServiceUtil.returnError("GRN not found for the shipment: "+orderId);
					}
					
					InventoryItemDetailFirst.set("quantityOnHandDiff", givenQuantity);
					InventoryItemDetailFirst.set("availableToPromiseTotal", availableToPromiseTotal);
					InventoryItemDetailFirst.store();
					
		        }catch(Exception e1){
	            	Debug.log("Problem in sending email");
		        }
			 */
			 
			 Map<String, Object> resultCtx = null;
			 
			 Map createInvDetail = FastMap.newInstance();
				createInvDetail.put("userLogin", userLogin);
				createInvDetail.put("inventoryItemId", inventoryItemId);
				//createInvDetail.put("shipmentId", shipmentId);
				//createInvDetail.put("itemIssuanceId", itemIssuanceId);
				createInvDetail.put("effectiveDate", UtilDateTime.nowTimestamp());
				createInvDetail.put("quantityOnHandDiff", differenceQuantity);
				createInvDetail.put("availableToPromiseDiff", differenceQuantity);
				
				try{
				resultCtx = dispatcher.runSync("createInventoryItemDetail", createInvDetail);
         } catch (Exception e) {
				Debug.logError(e, module);
			}
				if (ServiceUtil.isError(resultCtx)) {
					/*Debug.logError("Problem decrementing inventory for requested item ", module);
 	        	request.setAttribute("_ERROR_MESSAGE_", "Problem decrementing inventory for requested item ");
 	        	TransactionUtil.rollback();
 	        	return "error";*/
 	        	
 	        	return ServiceUtil.returnError("Problem decrementing inventory for requested item");
				}
			 
			 
			 }
			 
			}
	        
			return result;
	    
	    } 
  	  
  	  
  	 public static Map<String, Object> quantityOverrideService(DispatchContext ctx, Map context) {
	    	Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map result = ServiceUtil.returnSuccess();
			
			String orderId = (String) context.get("orderId");
			String orderItemSeqId = (String) context.get("orderItemSeqId");
			BigDecimal givenQuantity = (BigDecimal) context.get("quantity");
			String shipmentId = (String) context.get("shipmentId");
			
			
			
			List conList1 = FastList.newInstance();
			
			List<GenericValue> OrderItem =null;
			List<GenericValue> OrderItemDetail =null;
			try {
			
			conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
			conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
  		    EntityCondition cond1=EntityCondition.makeCondition(conList1,EntityOperator.AND);
			OrderItem = delegator.findList("OrderItem", cond1, null, null, null, false);
			
			}catch(Exception e1){
         	Debug.log("Problem in sending OrderItem");
	        }
			
			GenericValue OrderItem1 = EntityUtil.getFirst(OrderItem);
			
			
			if(UtilValidate.isNotEmpty(givenQuantity)){
			
				
				try{
					conList1.clear();
					conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
					conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
		     		EntityCondition cond2=EntityCondition.makeCondition(conList1,EntityOperator.AND);
		     		OrderItemDetail = delegator.findList("OrderItemDetail", cond2, null, null, null, false);
					}catch(Exception e1){
		            	Debug.log("Problem in sending OrderItem");
			        }
					
		     		GenericValue OrderItemDetail1 = EntityUtil.getFirst(OrderItemDetail);
					 
					if(UtilValidate.isNotEmpty(OrderItem1)){
						
					 try {
						 OrderItem1.set("quantity", givenQuantity);
						 OrderItem1.store();
					 }catch(Exception e1){
			            	Debug.log("Problem in sending OrderItem");
				        }
					 
					 
					}
					
					if(UtilValidate.isNotEmpty(OrderItemDetail1)){
					 try{		
							 OrderItemDetail1.set("quantity", givenQuantity);
							 OrderItemDetail1.store();
					}catch(Exception e1){
		            	Debug.log("Problem in sending OrderItem");
			        }
						 
					}
				
				//=============================PO===========================//
					String POOrderId =null;	
				try{		
				   List condList= FastList.newInstance();;
				   condList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId));
				   EntityCondition condExpress = EntityCondition.makeCondition(condList, EntityOperator.AND);
				   List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", condExpress, null, null, null, false);
				POOrderId = (EntityUtil.getFirst(orderAssocList)).getString("orderId");
				
				}catch(Exception e1){
					Debug.log("Problem in sending OrderItem");
				}
			   
				//Debug.log("POOrderId================="+POOrderId);
				
				List<GenericValue> poOrderItem =null;
				List<GenericValue> poOrderItemDetail =null;
				
				try {
				conList1.clear();	
				conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,POOrderId));
				conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
				EntityCondition cond2=EntityCondition.makeCondition(conList1,EntityOperator.AND);
				poOrderItem = delegator.findList("OrderItem", cond2, null, null, null, false);
				
				}catch(Exception e1){
				Debug.log("Problem in sending OrderItem");
				}
				
				GenericValue poOrderItem1 = EntityUtil.getFirst(poOrderItem);
				
				try{
					conList1.clear();
					conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,POOrderId));
					conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
		     		EntityCondition cond2=EntityCondition.makeCondition(conList1,EntityOperator.AND);
		     		poOrderItemDetail = delegator.findList("OrderItemDetail", cond2, null, null, null, false);
					}catch(Exception e1){
		            	Debug.log("Problem in sending OrderItem");
			        }
					
		     		GenericValue poOrderItemDetail1 = EntityUtil.getFirst(poOrderItemDetail);
		     		
		     		
		     		if(UtilValidate.isNotEmpty(poOrderItem1)){
						
						 try {
							 poOrderItem1.set("quantity", givenQuantity);
							 poOrderItem1.store();
						 }catch(Exception e1){
				            	Debug.log("Problem in sending OrderItem");
					        }
						 
						 
						}
						
						if(UtilValidate.isNotEmpty(poOrderItemDetail1)){
						 try{		
							 poOrderItemDetail1.set("quantity", givenQuantity);
							 poOrderItemDetail1.store();
						}catch(Exception e1){
			            	Debug.log("Problem in sending OrderItem");
				        }
							 
						}

			
						
						 try {
				        	 Map<String, Object> updateShipmentCtx = FastMap.newInstance();
				        	 updateShipmentCtx.put("userLogin", context.get("userLogin"));
				        	 updateShipmentCtx.put("shipmentId", shipmentId);
				        	 updateShipmentCtx.put("shipmentItemSeqId","00001");   
				        	 updateShipmentCtx.put("quantity",givenQuantity);
				             dispatcher.runSync("updateShipmentItem", updateShipmentCtx);
				        }
				        catch (GenericServiceException e) {
				    		Debug.logError(e, "Failed to update shipment status " + shipmentId, module);
				    		return ServiceUtil.returnError("Failed to update shipment status " + shipmentId + ": " + e);          	
				        }
						 List conditionList = FastList.newInstance();
						 List<GenericValue> ShipmentReceipt =null;
						 try{ 
								conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, POOrderId));
								conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
								conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
								conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
								EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
								ShipmentReceipt = delegator.findList("ShipmentReceipt", condExpr, null, null, null, false);
								
								GenericValue ShipmentReceiptFirst = EntityUtil.getFirst(ShipmentReceipt);
								
								if(UtilValidate.isEmpty(ShipmentReceiptFirst)){
									Debug.logError("GRN not found for the shipment: "+orderId, module);
									return ServiceUtil.returnError("GRN not found for the shipment: "+orderId);
								}
								
								ShipmentReceiptFirst.set("quantityAccepted", givenQuantity);
								ShipmentReceiptFirst.set("deliveryChallanQty", givenQuantity);
								ShipmentReceiptFirst.store();
								
					        }catch(Exception e1){
				            	Debug.log("Problem in sending email");
					        } 
						 
						 //==============================invoices===========================
						 
						String saleInvoiceId = null; 
						try{ 
					    conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
						conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF")));
						conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
						EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						List<GenericValue> Saleinvoices = delegator.findList("Invoice", condition, null, null, null, false);
						GenericValue Saleinvoice = EntityUtil.getFirst(Saleinvoices);
						saleInvoiceId = Saleinvoice.getString("invoiceId");
						
						}catch(Exception e1){
			            	Debug.log("Problem in sending email");
				        } 
						
						String purchaseInvoiceId = null; 
						
						try{ 
						    conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
							conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF")));
							conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
							EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							List<GenericValue> purchaseinvoices = delegator.findList("Invoice", condition, null, null, null, false);
							GenericValue purchaseinvoice = EntityUtil.getFirst(purchaseinvoices);
							purchaseInvoiceId = purchaseinvoice.getString("invoiceId");
							
							}catch(Exception e1){
				            	Debug.log("Problem in sending email");
					        } 
						
						if(UtilValidate.isNotEmpty(saleInvoiceId)){
						 List<GenericValue> saleInvoiceItem = null;
							
						 conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, saleInvoiceId));
					    conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_FPROD_ITEM"));
						 try{
						   saleInvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
						 
						   GenericValue saleInvoiceI = EntityUtil.getFirst(saleInvoiceItem);
						   
						   
						   if(UtilValidate.isNotEmpty(saleInvoiceI)){
								 try{		
									 saleInvoiceI.set("quantity", givenQuantity);
									 saleInvoiceI.store();
								}catch(Exception e1){
					            	Debug.log("Problem in sending OrderItem");
						        }
									 
								}
						   
						 }catch(GenericEntityException e){
								Debug.logError(e, "Failed to retrive InvoiceItem ", module);
						}
						 
						 
						 if(UtilValidate.isNotEmpty(saleInvoiceItem)){
				        	 
				    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
				    		 
				        	for(GenericValue eachInvoiceItem : saleInvoiceItem){
				        	
				        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
				        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
				        		BigDecimal itemValue = quantity.multiply(amount);
				        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
				        		
				        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
				        		
				        		eachInvoiceItem.set("itemValue",roundedAmount);
				        		
				        		try{
				        		eachInvoiceItem.store();
				        		}catch(GenericEntityException e){
				        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
				        		}
				        	}
				        	
				        	try{
				        		
				        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",saleInvoiceId),false);	
				        		
				        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
				        	InvoiceHeader.store();
				        	}catch(GenericEntityException e){
				    			Debug.logError(e, "Failed to Populate Invoice ", module);
				    		}
				        	
				    	 }
						 
						 
						 
						 
						 
						}
						
						if(UtilValidate.isNotEmpty(purchaseInvoiceId)){
							 List<GenericValue> purcahseInvoiceItem = null;
								
							 conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, purchaseInvoiceId));
						    conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_RAWPROD_ITEM"));
							 try{
								 purcahseInvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
							 
							   GenericValue purcahseIn = EntityUtil.getFirst(purcahseInvoiceItem);
							   
							   
							   if(UtilValidate.isNotEmpty(purcahseIn)){
									 try{		
										 purcahseIn.set("quantity", givenQuantity);
										 purcahseIn.store();
									}catch(Exception e1){
						            	Debug.log("Problem in sending OrderItem");
							        }
										 
									}
							   
							 }catch(GenericEntityException e){
									Debug.logError(e, "Failed to retrive InvoiceItem ", module);
							}
				
						
							 if(UtilValidate.isNotEmpty(purcahseInvoiceItem)){
					        	 
					    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
					    		 
					        	for(GenericValue eachInvoiceItem : purcahseInvoiceItem){
					        	
					        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
					        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
					        		BigDecimal itemValue = quantity.multiply(amount);
					        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
					        		
					        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
					        		
					        		eachInvoiceItem.set("itemValue",roundedAmount);
					        		
					        		try{
					        		eachInvoiceItem.store();
					        		}catch(GenericEntityException e){
					        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
					        		}
					        	}
					        	
					        	try{
					        		
					        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",purchaseInvoiceId),false);	
					        		
					        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
					        	InvoiceHeader.store();
					        	}catch(GenericEntityException e){
					    			Debug.logError(e, "Failed to Populate Invoice ", module);
					    		}
					        	
					    	 }		 
						
						
					
							 
							 
							 
							 
							}
			}
			return result;
	    
	    } 
  	    
  	 
  	 public static Map<String, Object> priceOverrideService(DispatchContext ctx, Map context) {
	    	Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map result = ServiceUtil.returnSuccess();
			
			String orderId = (String) context.get("orderId");
			String orderItemSeqId = (String) context.get("orderItemSeqId");
			BigDecimal givenQuantity = (BigDecimal) context.get("price");
			//String shipmentId = (String) context.get("shipmentId");
			
			
			
			List conList1 = FastList.newInstance();
			
			List<GenericValue> OrderItem =null;
			List<GenericValue> OrderItemDetail =null;
			try {
			
			conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
			conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
		    EntityCondition cond1=EntityCondition.makeCondition(conList1,EntityOperator.AND);
			OrderItem = delegator.findList("OrderItem", cond1, null, null, null, false);
			
			}catch(Exception e1){
      	    Debug.log("Problem in sending OrderItem");
	        }
			
			GenericValue OrderItem1 = EntityUtil.getFirst(OrderItem);
			
			
			if(UtilValidate.isNotEmpty(givenQuantity)){
			
				
				try{
					conList1.clear();
					conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId));
					conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
		     		EntityCondition cond2=EntityCondition.makeCondition(conList1,EntityOperator.AND);
		     		OrderItemDetail = delegator.findList("OrderItemDetail", cond2, null, null, null, false);
					}catch(Exception e1){
		            	Debug.log("Problem in sending OrderItem");
			        }
					
		     		GenericValue OrderItemDetail1 = EntityUtil.getFirst(OrderItemDetail);
					 
					if(UtilValidate.isNotEmpty(OrderItem1)){
						
					 try {
						 OrderItem1.set("unitPrice", givenQuantity);
						 OrderItem1.set("unitListPrice", givenQuantity);
						 OrderItem1.store();
					 }catch(Exception e1){
			            	Debug.log("Problem in sending OrderItem");
				        }
					 
					 
					}
					
					if(UtilValidate.isNotEmpty(OrderItemDetail1)){
					 try{		
							 OrderItemDetail1.set("unitPrice", givenQuantity);
							 OrderItemDetail1.set("unitListPrice", givenQuantity);
							 OrderItemDetail1.store();
					}catch(Exception e1){
		            	Debug.log("Problem in sending OrderItem");
			        }
						 
					}
				
				//=============================PO===========================//
					String POOrderId =null;	
				try{		
				   List condList= FastList.newInstance();;
				   condList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId));
				   EntityCondition condExpress = EntityCondition.makeCondition(condList, EntityOperator.AND);
				   List<GenericValue> orderAssocList = delegator.findList("OrderAssoc", condExpress, null, null, null, false);
				POOrderId = (EntityUtil.getFirst(orderAssocList)).getString("orderId");
				
				}catch(Exception e1){
					Debug.log("Problem in sending OrderItem");
				}
			   
				//Debug.log("POOrderId================="+POOrderId);
				
				List<GenericValue> poOrderItem =null;
				List<GenericValue> poOrderItemDetail =null;
				
				try {
				conList1.clear();	
				conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,POOrderId));
				conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
				EntityCondition cond2=EntityCondition.makeCondition(conList1,EntityOperator.AND);
				poOrderItem = delegator.findList("OrderItem", cond2, null, null, null, false);
				
				}catch(Exception e1){
				Debug.log("Problem in sending OrderItem");
				}
				
				GenericValue poOrderItem1 = EntityUtil.getFirst(poOrderItem);
				
				try{
					conList1.clear();
					conList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,POOrderId));
					conList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId));
		     		EntityCondition cond2=EntityCondition.makeCondition(conList1,EntityOperator.AND);
		     		poOrderItemDetail = delegator.findList("OrderItemDetail", cond2, null, null, null, false);
					}catch(Exception e1){
		            	Debug.log("Problem in sending OrderItem");
			        }
					
		     		GenericValue poOrderItemDetail1 = EntityUtil.getFirst(poOrderItemDetail);
		     		
		     		
		     		if(UtilValidate.isNotEmpty(poOrderItem1)){
						
						 try {
							 poOrderItem1.set("unitPrice", givenQuantity);
							 poOrderItem1.set("unitListPrice", givenQuantity);
							 poOrderItem1.store();
						 }catch(Exception e1){
				            	Debug.log("Problem in sending OrderItem");
					        }
						 
						 
						}
						
						if(UtilValidate.isNotEmpty(poOrderItemDetail1)){
						 try{		
							 poOrderItemDetail1.set("unitPrice", givenQuantity);
							 poOrderItemDetail1.set("unitListPrice", givenQuantity);
							 poOrderItemDetail1.store();
						}catch(Exception e1){
			            	Debug.log("Problem in sending OrderItem");
				        }
							 
						}

			
						
						 /*try {
				        	 Map<String, Object> updateShipmentCtx = FastMap.newInstance();
				        	 updateShipmentCtx.put("userLogin", context.get("userLogin"));
				        	 updateShipmentCtx.put("shipmentId", shipmentId);
				        	 updateShipmentCtx.put("shipmentItemSeqId","00001");   
				        	 updateShipmentCtx.put("quantity",givenQuantity);
				             dispatcher.runSync("updateShipmentItem", updateShipmentCtx);
				        }
				        catch (GenericServiceException e) {
				    		Debug.logError(e, "Failed to update shipment status " + shipmentId, module);
				    		return ServiceUtil.returnError("Failed to update shipment status " + shipmentId + ": " + e);          	
				        }
						 List conditionList = FastList.newInstance();
						 List<GenericValue> ShipmentReceipt =null;
						 try{ 
								conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, POOrderId));
								conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
								conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
								conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
								EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
								ShipmentReceipt = delegator.findList("ShipmentReceipt", condExpr, null, null, null, false);
								
								GenericValue ShipmentReceiptFirst = EntityUtil.getFirst(ShipmentReceipt);
								
								if(UtilValidate.isEmpty(ShipmentReceiptFirst)){
									Debug.logError("GRN not found for the shipment: "+orderId, module);
									return ServiceUtil.returnError("GRN not found for the shipment: "+orderId);
								}
								
								ShipmentReceiptFirst.set("quantityAccepted", givenQuantity);
								ShipmentReceiptFirst.set("deliveryChallanQty", givenQuantity);
								ShipmentReceiptFirst.store();
								
					        }catch(Exception e1){
				            	Debug.log("Problem in sending email");
					        } 
						 */
						 //==============================invoices===========================
						
						 List conditionList = FastList.newInstance();
						
						 List<String> shipmentIds = null;
						try{ 
							conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, POOrderId));
							conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
							EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							List<GenericValue> Shipment = delegator.findList("Shipment", condition, null, null, null, false);
							
						    shipmentIds = EntityUtil.getFieldListFromEntityList(Shipment, "shipmentId", true);
							
							}catch(Exception e1){
				            	Debug.log("Problem in sending email");
					        } 
						
						
						
						
						
						for (String shipmentId : shipmentIds) {
							
						String saleInvoiceId = null; 
						try{ 
					    conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
						conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF")));
						conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
						EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						List<GenericValue> Saleinvoices = delegator.findList("Invoice", condition, null, null, null, false);
						GenericValue Saleinvoice = EntityUtil.getFirst(Saleinvoices);
						saleInvoiceId = Saleinvoice.getString("invoiceId");
						
						}catch(Exception e1){
			            	Debug.log("Problem in sending email");
				        } 
						
						String purchaseInvoiceId = null; 
						
						try{ 
						    conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
							conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF")));
							conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
							EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							List<GenericValue> purchaseinvoices = delegator.findList("Invoice", condition, null, null, null, false);
							GenericValue purchaseinvoice = EntityUtil.getFirst(purchaseinvoices);
							purchaseInvoiceId = purchaseinvoice.getString("invoiceId");
							
							}catch(Exception e1){
				            	Debug.log("Problem in sending email");
					        } 
						
						if(UtilValidate.isNotEmpty(saleInvoiceId)){
						 List<GenericValue> saleInvoiceItem = null;
							
						 conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, saleInvoiceId));
					    conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_FPROD_ITEM"));
						 try{
						   saleInvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
						 
						   GenericValue saleInvoiceI = EntityUtil.getFirst(saleInvoiceItem);
						   
						   
						   if(UtilValidate.isNotEmpty(saleInvoiceI)){
								 try{		
									 saleInvoiceI.set("amount", givenQuantity);
									 saleInvoiceI.store();
								}catch(Exception e1){
					            	Debug.log("Problem in sending OrderItem");
						        }
									 
								}
						   
						 }catch(GenericEntityException e){
								Debug.logError(e, "Failed to retrive InvoiceItem ", module);
						}
						 
						 
						 if(UtilValidate.isNotEmpty(saleInvoiceItem)){
				        	 
				    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
				    		 
				        	for(GenericValue eachInvoiceItem : saleInvoiceItem){
				        	
				        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
				        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
				        		BigDecimal itemValue = quantity.multiply(amount);
				        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
				        		
				        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
				        		
				        		eachInvoiceItem.set("itemValue",roundedAmount);
				        		
				        		try{
				        		eachInvoiceItem.store();
				        		}catch(GenericEntityException e){
				        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
				        		}
				        	}
				        	
				        	try{
				        		
				        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",saleInvoiceId),false);	
				        		
				        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
				        	InvoiceHeader.store();
				        	}catch(GenericEntityException e){
				    			Debug.logError(e, "Failed to Populate Invoice ", module);
				    		}
				        	
				    	 }
						 
						 
						}
						
						if(UtilValidate.isNotEmpty(purchaseInvoiceId)){
							 List<GenericValue> purcahseInvoiceItem = null;
								
							 conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, purchaseInvoiceId));
						    conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_RAWPROD_ITEM"));
							 try{
								 purcahseInvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
							 
							   GenericValue purcahseIn = EntityUtil.getFirst(purcahseInvoiceItem);
							   
							   
							   if(UtilValidate.isNotEmpty(purcahseIn)){
									 try{		
										 purcahseIn.set("amount", givenQuantity);
										 purcahseIn.set("unitPrice", givenQuantity);
										 purcahseIn.store();
									}catch(Exception e1){
						            	Debug.log("Problem in sending OrderItem");
							        }
										 
									}
							   
							 }catch(GenericEntityException e){
									Debug.logError(e, "Failed to retrive InvoiceItem ", module);
							}
				
						
							 if(UtilValidate.isNotEmpty(purcahseInvoiceItem)){
					        	 
					    		 BigDecimal invoiceGrandTotal = BigDecimal.ZERO;
					    		 
					        	for(GenericValue eachInvoiceItem : purcahseInvoiceItem){
					        	
					        		BigDecimal quantity = eachInvoiceItem.getBigDecimal("quantity");
					        		BigDecimal amount = eachInvoiceItem.getBigDecimal("amount");
					        		BigDecimal itemValue = quantity.multiply(amount);
					        		BigDecimal roundedAmount = (itemValue.setScale(0, rounding));
					        		
					        		invoiceGrandTotal = invoiceGrandTotal.add(roundedAmount);
					        		
					        		eachInvoiceItem.set("itemValue",roundedAmount);
					        		
					        		try{
					        		eachInvoiceItem.store();
					        		}catch(GenericEntityException e){
					        			Debug.logError(e, "Failed to Populate InvoiceItem ", module);
					        		}
					        	}
					        	
					        	try{
					        		
					        		GenericValue InvoiceHeader = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",purchaseInvoiceId),false);	
					        		
					        	InvoiceHeader.set("invoiceGrandTotal",invoiceGrandTotal);
					        	InvoiceHeader.store();
					        	}catch(GenericEntityException e){
					    			Debug.logError(e, "Failed to Populate Invoice ", module);
					    		}
					        	
					    	 }		 
							}
						
						}
			}
			return result;
	    
	    } 
  	 
  	 
	    
  	 public static Map<String, Object> invalidShipments(DispatchContext dctx, Map context) {
   		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
   		LocalDispatcher dispatcher = dctx.getDispatcher();
   		Map<String, Object> result = ServiceUtil.returnSuccess();
   		GenericValue userLogin = (GenericValue) context.get("userLogin");
   		String itemType = (String) context.get("itemType");
   		String decimals = (String) context.get("decimals");
   		String roundType = (String) context.get("roundType");
   		String places = (String) context.get("places");
   		
   		String ro = (String) context.get("ro");
   		
   		String invoiceId = (String) context.get("invoiceId");
   		
   		Locale locale = (Locale) context.get("locale");
   		
   		List<GenericValue> shipmentList = null;
   		List<GenericValue> PartyRelationship = null;
   		List<GenericValue> Invoice = null;
   		List branchList =  FastList.newInstance();
   		List ShipementIds =  FastList.newInstance();
   		
   		
   		List conditionList = FastList.newInstance();
   		conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, ro));
   		conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
   		
   		try{
   		PartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("partyIdTo"), null, null, false);

   	     branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
   		}catch(GenericEntityException e){
 			Debug.logError(e, "Failed to retrive PartyRelationship ", module);
 		}
   		
   		
  		 try{
  			conditionList.clear();
  		    if(UtilValidate.isNotEmpty(branchList))	
  		    	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
  		     if(UtilValidate.isNotEmpty(invoiceId))	
  			    conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
  			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
  		        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
  		        conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));
  			 
  		     Invoice = delegator.findList("Invoice", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId"), null, null, false);
 				
 			}catch(GenericEntityException e){
 				Debug.logError(e, "Failed to retrive Shipment ", module);
 			}
   		 
  		  
  		if(UtilValidate.isNotEmpty(Invoice)){
  			
 	        for(GenericValue eachInvoice : Invoice){
   				
 	        	String eacinvoiceId = eachInvoice.getString("invoiceId");
 	        	
 	        	String shipmentId = eachInvoice.getString("shipmentId");
 	        	
 	        	try{
 	        	
 	        	 if(UtilValidate.isNotEmpty(shipmentId)){	
 	        	List<GenericValue> shipment = null;
 	        	
 	        	conditionList.clear();
 	        	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
 	        	//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
 	        	 
 	        		shipment = delegator.findList("Shipment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
 	        	 
 	        		if(UtilValidate.isEmpty(shipment))
 	        		 ShipementIds.add(shipmentId);
 	        		
 	        	 }
 	        		
 	        	 }catch(GenericEntityException e){
 	 				Debug.logError(e, "Failed to retrive InvoiceItem ", module);
 	 			}
 	        	 
 	        	 
 	        	
 	        	 try{
 	     			conditionList.clear();
 	     		   if(UtilValidate.isNotEmpty(shipmentId)){	
 	     			 List<GenericValue> PurChseInvoice = null;
 	    			    conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
 	     			    conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
 	     		        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
 	     		        conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));
 	     			 
 	     		      PurChseInvoice = delegator.findList("Invoice", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId"), null, null, false);
 	    				
 	     		   if(UtilValidate.isEmpty(PurChseInvoice))
 	     			 ShipementIds.add(shipmentId); 
 	     		     
 	     		   }
 	    			}catch(GenericEntityException e){
 	    				Debug.logError(e, "Failed to retrive Shipment ", module);
 	    			}
 	        	 
   			}
  			
  		}
  		 
   		
     	  result.put("shipmentIds",ShipementIds);
          
          return result;
   	}
     
    
	 public static Map<String, Object> populateSaleItemPrice(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String POorderId = (String) context.get("orderId");
		Locale locale = (Locale) context.get("locale");
		
		 try{
		List condLIst = FastList.newInstance();
		condLIst.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, POorderId));
		EntityCondition condExpr1 = EntityCondition.makeCondition(condLIst, EntityOperator.AND);
		List<GenericValue> orderItem = delegator.findList("OrderItem", condExpr1, null, null, null, false);
		
		Debug.log("orderItem============"+orderItem);
		
		for (GenericValue eachItem : orderItem) {
			
			
			String toOrderId = eachItem.getString("orderId");
			String toOrderItemSeqId = eachItem.getString("orderItemSeqId");
			
			Debug.log("toOrderId============"+toOrderId);
			
			Debug.log("toOrderItemSeqId============"+toOrderItemSeqId);
			
			List condList = FastList.newInstance();
	    	condList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, toOrderId));
	    	condList.add(EntityCondition.makeCondition("toOrderItemSeqId", EntityOperator.EQUALS, toOrderItemSeqId));
	    	condList.add(EntityCondition.makeCondition("orderItemAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
	        List<GenericValue> orderItemAssocList = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("orderId","orderItemSeqId"), null, null, false);

	        Debug.log("orderItemAssocList============"+orderItemAssocList);
			
	        GenericValue orderItemAssoc = EntityUtil.getFirst(orderItemAssocList);
	        
	        Debug.log("orderItemAssoc============"+orderItemAssoc);

	        
	        String orderId = orderItemAssoc.getString("orderId");
	        String orderItemSeqId = orderItemAssoc.getString("orderItemSeqId");
			
			
			GenericValue orderItemSale = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",orderId,"orderItemSeqId",orderItemSeqId), false);
			
			Debug.log("orderItemSale============"+orderItemSale);
			
			if(UtilValidate.isNotEmpty(orderItemSale)){
				orderItemSale.set("unitPrice",eachItem.getBigDecimal("unitPrice"));
				orderItemSale.set("unitListPrice",eachItem.getBigDecimal("unitListPrice"));
				
				orderItemSale.store();
				
			}
			
			condList.clear();
	    	condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	    	condList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
	        List<GenericValue> OrderItemDetail = delegator.findList("OrderItemDetail", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);

	        for (GenericValue eachDeatail : OrderItemDetail) {
	        	eachDeatail.set("unitPrice",eachItem.getBigDecimal("unitPrice"));
	        	eachDeatail.store();
			}
			
			
		} 
		
		 }catch(GenericEntityException e){
				Debug.logError(e, "Failed to retrive Shipment ", module);
			}
		
		
		 
		
 	  //result.put("shipmentIds",ShipementIds);
      
      return result;
	}
  	 
  		
	    
}
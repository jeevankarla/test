package in.vasista.vbiz.byproducts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.DateTimeConverters;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderServices;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.price.PriceServices;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.product.product.ProductEvents;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.invoice.InvoiceServices;

import in.vasista.vbiz.byproducts.ByProductServices;


public class ByProductNetworkServices {
	public static final String module = ByProductNetworkServices.class.getName();
	private static BigDecimal ZERO = BigDecimal.ZERO;
    private static int decimals;
    private static int rounding;
    private static String obInvoiceType = "OBINVOICE_IN";
    public static final String resource_error = "OrderErrorUiLabels";
    static {
        decimals = 2;//UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }    
	
	 public static List<GenericValue> getByProductProducts(DispatchContext dctx, Map<String, ? extends  Object> context){
    	 Timestamp salesDate = UtilDateTime.nowTimestamp();    	
         Delegator delegator = dctx.getDelegator();
         LocalDispatcher dispatcher = dctx.getDispatcher();
         if(!UtilValidate.isEmpty(context.get("salesDate"))){
        	salesDate =  (Timestamp) context.get("salesDate");  
         }
        Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
    	List<GenericValue> productList =FastList.newInstance();
    	List condList =FastList.newInstance();
    	condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, "MILK_MILKPRODUCTS"));
    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
    			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
    	EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
    	try{
    		productList =delegator.findList("ProductAndCategoryMember", discontinuationDateCondition,null, null, null, false);
    	}catch (GenericEntityException e) {
			// TODO: handle exception
    		Debug.logError(e, module);
		} 
    	return productList;
	}
	 /*
	  * Helper that returns the   all products which are configured as prodcuts not yet closed
	  * 
	  * 
	  */
	 public static List<GenericValue> getAllProducts(DispatchContext dctx, Map<String, ? extends  Object> context){
    	 Timestamp salesDate = UtilDateTime.nowTimestamp();    	
         Delegator delegator = dctx.getDelegator();
         LocalDispatcher dispatcher = dctx.getDispatcher();
         if(!UtilValidate.isEmpty(context.get("salesDate"))){
        	salesDate =  (Timestamp) context.get("salesDate");  
         }
        Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
    	List<GenericValue> productList =FastList.newInstance();
    	List condList =FastList.newInstance();
    	condList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
    	condList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
    			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
    	EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
    	try{
    		productList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
    	}catch (GenericEntityException e) {
			// TODO: handle exception
    		Debug.logError(e, module);
		} 
    	return productList;
	}
	 /*
	  * Helper that returns the product -> productCategory (GenericValue of type ProductAndCategoryMember) map.  Since products can have multiple
	  * categorization schemes, the categoryType id is expected to be passed in (BYPROD_MFG_LOC,
	  * BYPROD_CAT, etc);
	  * 
	  * 
	  */
	 public static Map getProductCategoryMap(Delegator delegator, String categoryId)  {
		 Map productCatMap = FastMap.newInstance();
		 
    	 Timestamp salesDate = UtilDateTime.nowTimestamp();    	
    	 Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
    	 List<GenericValue> productList =FastList.newInstance();
    	 try{
    		
    		 if (UtilValidate.isNotEmpty(categoryId)) {
    			 List condList =FastList.newInstance();
    			 condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId));
    			 condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
        			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
    			 EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);      
    			 productList =delegator.findList("ProductAndCategoryMember", condition,null, null, null, false);
    			 for (int i = 0; i < productList.size(); ++i) {
    				 productCatMap.put(productList.get(i).get("productId"), productList.get(i));
    			 }
    		 }
    	 }catch (GenericEntityException e) {
			// TODO: handle exception
    		Debug.logError(e, module);
    	 } 		 
		 return productCatMap;
	 }
	 public static Map getByProductFacilityCategoryAndClassification(Delegator delegator, Timestamp effectiveDate)  {
		 
		 Map facilityDetail = FastMap.newInstance();
		 if(UtilValidate.isEmpty(effectiveDate)){
			 effectiveDate = UtilDateTime.nowTimestamp();
		 }
		 List<GenericValue> partyClassification = FastList.newInstance();
    	 List<GenericValue> facilityList =FastList.newInstance();
    	 try{
    		 //facilityList = delegator.findList("Facility", EntityCondition.makeCondition("byProdRouteId", EntityOperator.NOT_EQUAL, null), null, null, null, false);
    		 facilityList = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "BOOTH"), null, null, null, false);
    		 if (UtilValidate.isNotEmpty(facilityList)) {
    			 for(int i=0;i<facilityList.size();i++){
    				 Map facilityTemp = FastMap.newInstance();
    				 GenericValue facility = facilityList.get(i);
    				 String facilityId = facility.getString("facilityId");
    				 String category = facility.getString("categoryTypeEnum");
    				 String ownerPartyId = facility.getString("ownerPartyId");
    				 if(UtilValidate.isNotEmpty(ownerPartyId)){
    					 List condList =FastList.newInstance();
    	    			 condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerPartyId));
    	    			 EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);  
    	    			 partyClassification =delegator.findList("PartyClassification", condition,UtilMisc.toSet("partyClassificationGroupId"), null, null, false);
    	    			 partyClassification = EntityUtil.filterByDate(partyClassification, effectiveDate);
    	    			 if(UtilValidate.isNotEmpty(partyClassification)){
    	    				 facilityTemp.put("ownerPartyId", ownerPartyId);
    	    				 facilityTemp.put("categoryTypeEnum", category);
    	    				 String classificationId = EntityUtil.getFirst(partyClassification).getString("partyClassificationGroupId");
    	    				 facilityTemp.put("partyClassification", classificationId);
    	    				 facilityDetail.put(facilityId, facilityTemp);
    	    			 }
    				 }
    			 }
    		 }
    	 }catch (GenericEntityException e) {
    		Debug.logError(e, module);
    		return ServiceUtil.returnError(e.toString());
    	 }
		 return facilityDetail;
	 }
	 
	 public static Map getByProductSales(DispatchContext dctx, Timestamp fromDate, Timestamp thruDate, String salesChannel, String categoryTypeEnum, List productList, List facilityList) {
		 
		 Delegator delegator = dctx.getDelegator();
		 LocalDispatcher dispatcher = dctx.getDispatcher();
		/* List shipmentList = FastList.newInstance();
		 if(UtilValidate.isNotEmpty(salesChannel)){
			 if(salesChannel.equalsIgnoreCase("BYPROD_SALES_CHANNEL")){
				 shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, fromDate, thruDate, "BYPRODUCTS");
			 }
			 else{
				 shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, fromDate, thruDate, "BYPRODUCTS_PRSALE");
			 }
		 }
		 else{
			 shipmentList = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
		 }*/
		 List shipmentList = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
		 Map result = FastMap.newInstance();
		 int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate)+1);
    	 List orderedItems = FastList.newInstance();
    	 List conditionList = FastList.newInstance();
    	 conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
    	 conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
    	 if(UtilValidate.isNotEmpty(categoryTypeEnum)){
    		 conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, categoryTypeEnum));
    	 }
    	 if(UtilValidate.isNotEmpty(productList)){
   			 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.IN, productList)));
    	 }
    	 if(UtilValidate.isNotEmpty(facilityList)){
   			 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityList)));
    	 }
    	 EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    	 try{
    		 orderedItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, UtilMisc.toSet("estimatedDeliveryDate","quantity","productId","originFacilityId","shipmentId", "categoryTypeEnum"), null, null, false);
    	 }catch(GenericEntityException e){
    		 Debug.logError(e, e.toString(), module);
             return ServiceUtil.returnError(e.toString());
    	 }
    	 Map totalProductQuant = FastMap.newInstance();
    	 List daywiseSales = FastList.newInstance();
    	 Map daySale = FastMap.newInstance();
    	 for(int k =0;k<intervalDays;k++){
    		
    		List condList = FastList.newInstance(); 
 			Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
 			Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
 			Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate); 
 			List dayWiseShipmentList = ByProductNetworkServices.getByProdShipmentIds(delegator, dayStart, dayEnd);
 			
 			List<GenericValue> daywiseParlourOrders = EntityUtil.filterByCondition(orderedItems, EntityCondition.makeCondition("shipmentId", EntityOperator.IN, dayWiseShipmentList));
 			List tempDayPartyList = FastList.newInstance();
 			tempDayPartyList = EntityUtil.getFieldListFromEntityList(daywiseParlourOrders, "originFacilityId", true);
 			List dayPartyList = FastList.newInstance();
 			for(int j=0;j<tempDayPartyList.size();j++){
 				String booth = (String)tempDayPartyList.get(j);
 				booth = booth.toUpperCase();
 				if(!dayPartyList.contains(booth)){
 					dayPartyList.add(booth);
 				}
 			}
 			Map partySale = FastMap.newInstance();
 			String boothId = "";
 			if(UtilValidate.isNotEmpty(dayPartyList)){
 				for(int i=0;i<dayPartyList.size();i++){
 					boothId = (String)dayPartyList.get(i);
 					Map productQuant = FastMap.newInstance();
 					List daywiseBoothwiseSale = EntityUtil.filterByCondition(daywiseParlourOrders, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("originFacilityId"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)boothId).toUpperCase())));
 					for(int j=0 ; j<daywiseBoothwiseSale.size();j++){
 						GenericValue eachOrderItem = (GenericValue)daywiseBoothwiseSale.get(j);
 						String productId = eachOrderItem.getString("productId");
 						BigDecimal quantity = eachOrderItem.getBigDecimal("quantity");
 						if(productQuant.containsKey(productId)){
 							BigDecimal tempQty = (BigDecimal)productQuant.get(productId);
 							BigDecimal totalQty = tempQty.add(quantity);
 							productQuant.put(productId, totalQty);
 						}else{
 							productQuant.put(productId,quantity);
 						}
 						if(totalProductQuant.containsKey(productId)){
 							BigDecimal tempTotQty = (BigDecimal)totalProductQuant.get(productId);
 							BigDecimal totalQuant = tempTotQty.add(quantity);
 							totalProductQuant.put(productId, totalQuant);
 						}
 						else{
 							totalProductQuant.put(productId, quantity);
 						}
 					}
 					partySale.put(boothId, productQuant);
 				}
 			}
 			daySale.put(dayStart, partySale);
 			daywiseSales.add(daySale);
 		 }
    	 result.put("totalSales", totalProductQuant);
    	 result.put("datewiseSales", daywiseSales);
		 return result;
	 }
	 
	 public static Map getDayWiseTotalSales(DispatchContext dctx, Timestamp fromDate, Timestamp thruDate, String salesChannel, List productList)  {
		 
		 Delegator delegator = dctx.getDelegator();
		 LocalDispatcher dispatcher = dctx.getDispatcher();
		     
		 Map result = FastMap.newInstance();
		 int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate)+1);
    	 List orderedItems = FastList.newInstance();
    	 List conditionList = FastList.newInstance();
    	 conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
    	 conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
    	 conditionList.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, "BYPROD_SALES_CHANNEL"));
    	 if(UtilValidate.isNotEmpty(productList)){
    		 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
    	 }
    	 conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
    	 conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
    	 EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    	 try{
    		 orderedItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, UtilMisc.toSet("estimatedDeliveryDate","quantity","productId","originFacilityId","ownerPartyId"), null, null, false);
    	 }catch(GenericEntityException e){
    		 Debug.logError(e, e.toString(), module);
             return ServiceUtil.returnError(e.toString());
    	 }
    	 List shipmentList = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
    	// List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, fromDate, thruDate, "BYPRODUCTS");
    	 conditionList.clear(); 
 		 conditionList.add(EntityCondition.makeCondition("shipmentId",  EntityOperator.IN, shipmentList));
 		 conditionList.add(EntityCondition.makeCondition("isCancelled",  EntityOperator.EQUALS, null));
 		 if(UtilValidate.isNotEmpty(productList)){
   		    conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
 		 }
 		 conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId",  EntityOperator.EQUALS, "CASH_BYPROD"));
 		 EntityCondition shipReceiptCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 		 List<GenericValue> shipReceiptList = FastList.newInstance();
    	 try{
    		 shipReceiptList = delegator.findList("ShipmentReceiptAndItem", shipReceiptCond, UtilMisc.toSet("facilityId", "productId", "datetimeReceived", "quantityAccepted"), UtilMisc.toList("productId"), null, false);
    	 }catch(GenericEntityException e){
    		 Debug.logError(e, e.toString(), module);
             return ServiceUtil.returnError(e.toString());
    	 }
    	 
    	 /*==============GET PRICES BASED ON PARTY CLASSIFICATION=============*/
	    	Map classificationMap = FastMap.newInstance();
	    	
	    	List<GenericValue> partyClassificationList = null;
	    	try{
	    		partyClassificationList = delegator.findList("PartyClassificationGroup", EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS,"PM_RC"),UtilMisc.toSet("partyClassificationGroupId"), null, null, false);
	    	}catch (GenericEntityException e) {
	    		Debug.logError("Unable to get records from PartyClassificationGroup"+e, module);
	    		return ServiceUtil.returnError("Unable to get records from PartyClassificationGroup"); 
			}
	    	List partyClassifications = EntityUtil.getFieldListFromEntityList(partyClassificationList, "partyClassificationGroupId", true);
	    	for(int i=0; i<partyClassifications.size(); i++){
	    		Map productsPrice = (Map) ByProductReportServices.getByProductPricesForPartyClassification(dctx, UtilMisc.toMap("partyClassificationId", partyClassifications.get(i))).get("productsPrice");
				classificationMap.put(partyClassifications.get(i), productsPrice);
	    	}
	    /*==============END=============*/
    	 Map dayWiseSale = FastMap.newInstance();
    	 
    	 Map totalSalesMap = FastMap.newInstance();
    	 
    	 BigDecimal totalBasicPrice = BigDecimal.ZERO;
  		 BigDecimal grandTotalPrice = BigDecimal.ZERO;
  		 BigDecimal totalVatAmt = BigDecimal.ZERO;
  		 BigDecimal totalExDuty = BigDecimal.ZERO;
  		 BigDecimal totalEdCess = BigDecimal.ZERO;
  		 BigDecimal totalHigherSecCess = BigDecimal.ZERO;
  		 BigDecimal grandTotalExcise = BigDecimal.ZERO;
    	 
    	 for(int k =0; k<intervalDays; k++){
    		
 			Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
 			Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
 			Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate); 
 			
 			List<GenericValue> daywiseParlourOrders = EntityUtil.filterByCondition(orderedItems, EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart),EntityOperator.AND,
 	    			 EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
 			
 			
 			List<GenericValue> daywiseShipmentReceipts = EntityUtil.filterByCondition(shipReceiptList, EntityCondition.makeCondition(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart),EntityOperator.AND,
	    			 EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
 			
 			Map salesMap = FastMap.newInstance();
 			
 			BigDecimal basicPrice = BigDecimal.ZERO;
     		BigDecimal totalPrice = BigDecimal.ZERO;
     		     		
     		BigDecimal exDutyPrecent = BigDecimal.ZERO;
     		BigDecimal edCessPrecent = BigDecimal.ZERO;
     		BigDecimal higherSecCessPercent = BigDecimal.ZERO;
     		
     		for(int i=0; i<daywiseShipmentReceipts.size(); i++){
 				
 				GenericValue eachReceiptItem = (GenericValue)daywiseShipmentReceipts.get(i);
				
 				String productId = eachReceiptItem.getString("productId");
				BigDecimal quantity = eachReceiptItem.getBigDecimal("quantityAccepted");
				//String ownerPartyId = eachOrderItem.getString("ownerPartyId");
				
	     		Map priceMap = (Map) classificationMap.get("PM_RC_P");
	     		Map prodPriceMap = (Map)priceMap.get(productId);
	     		
	     		BigDecimal basicValue = ((BigDecimal)prodPriceMap.get("basicPrice")).multiply(quantity);
	     		BigDecimal totalValue = ((BigDecimal)prodPriceMap.get("totalAmount")).multiply(quantity);
	     			     		
	     		exDutyPrecent = (BigDecimal)prodPriceMap.get("bedPercentage");
	     		edCessPrecent = (BigDecimal)prodPriceMap.get("bedCessPercent");
	     		higherSecCessPercent = (BigDecimal)prodPriceMap.get("bedsecPercent");
	     		
	     		basicPrice = basicPrice.add(basicValue);
	     		totalPrice = totalPrice.add(totalValue);
	     		
	     		     		
	     		totalBasicPrice = totalBasicPrice.add(basicValue);
	     		grandTotalPrice = grandTotalPrice.add(totalValue);

 			}
     		
 			for(int i=0; i<daywiseParlourOrders.size(); i++){
 				
 				GenericValue eachOrderItem = (GenericValue)daywiseParlourOrders.get(i);
				
 				String productId = eachOrderItem.getString("productId");
				BigDecimal quantity = eachOrderItem.getBigDecimal("quantity");
				String ownerPartyId = eachOrderItem.getString("ownerPartyId");
				
				String partyClassificationTypeId = null;
	     		List<GenericValue> partyClassification = null;
	     		try{
	     			List<GenericValue> partyClassificationGroup = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerPartyId), null, null, null, false);
    				if(UtilValidate.isNotEmpty(partyClassificationGroup)){
    					partyClassificationTypeId = (String) EntityUtil.getFirst(partyClassificationGroup).get("partyClassificationGroupId");
    				}
	     		}catch(GenericEntityException e){
	     			Debug.logError("No partyRole found for given partyId:"+ ownerPartyId, module);
	     			return ServiceUtil.returnError("No partyRole found for given partyId");
	     		}
				
	     		Map priceMap = (Map) classificationMap.get(partyClassificationTypeId);
	     		Map prodPriceMap = (Map)priceMap.get(productId);
	     		
	     		BigDecimal basicValue = ((BigDecimal)prodPriceMap.get("basicPrice")).multiply(quantity);
	     		BigDecimal totalValue = ((BigDecimal)prodPriceMap.get("totalAmount")).multiply(quantity);
	     		
	     		exDutyPrecent = (BigDecimal)prodPriceMap.get("bedPercentage");
	     		edCessPrecent = (BigDecimal)prodPriceMap.get("bedCessPercent");
	     		higherSecCessPercent = (BigDecimal)prodPriceMap.get("bedsecPercent");
	     		
	     		basicPrice = basicPrice.add(basicValue);
	     		totalPrice = totalPrice.add(totalValue);
	     		
	     		totalBasicPrice = totalBasicPrice.add(basicValue);
	     		grandTotalPrice = grandTotalPrice.add(totalValue);

 			}
 			
 			salesMap.put("basicPrice", basicPrice);
 			salesMap.put("totalPrice", totalPrice);
 			
 			salesMap.put("exDutyPrecent", exDutyPrecent);
 			salesMap.put("edCessPrecent", edCessPrecent);
 			salesMap.put("higherSecCessPer", higherSecCessPercent);
 			
 			Map tempDayWiseSales = FastMap.newInstance();
 			tempDayWiseSales.putAll(salesMap);
 			
 			String dayStartStr = dayStart.toString();
 			
 			if (dayStartStr.contains("00:00:00.0")) {
 				String[] dayStartStrSplit = dayStartStr.split("00:00:00.0");
 				dayStartStr = dayStartStrSplit[0];
 			}
 			dayWiseSale.put(dayStartStr, tempDayWiseSales);
 		 }
    	 grandTotalExcise = (totalExDuty.add(totalEdCess)).add(totalHigherSecCess);
    	 
    	 totalSalesMap.put("basicPrice", totalBasicPrice);
    	 totalSalesMap.put("totalPrice", grandTotalPrice);
    	 
    	 Map tempTotalSales = FastMap.newInstance();
    	 tempTotalSales.putAll(totalSalesMap);
    	 
    	 dayWiseSale.put("totalSales", tempTotalSales);
    	 
    	 result.put("dayWiseSale", dayWiseSale);
		 return result;
	 }
	 
	 public static Map getByProductSubscriptionId(Delegator delegator, String facilityId)  {
		 
		 Map result = FastMap.newInstance();
		 String subscriptionId = "";
    	 List<GenericValue> subscriptionList =FastList.newInstance();
    	 try{
    		 List condList =FastList.newInstance();
    		 condList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
    		 condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    		 EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);      
    		 subscriptionList = delegator.findList("Subscription", condition, null, null, null, false);
    		 subscriptionList = EntityUtil.filterByDate(subscriptionList, UtilDateTime.nowTimestamp());	 
    		 if(UtilValidate.isNotEmpty(subscriptionList)){
    			 GenericValue subscription = EntityUtil.getFirst(subscriptionList);
    			 subscriptionId = subscription.getString("subscriptionId");
    		 }
    	 }catch (GenericEntityException e) {
			// TODO: handle exception
    		Debug.logError(e, module);
    	 }
    	 result.put("subscriptionId", subscriptionId);
		 return result;
	 }
	 
	 public static List getByProdShipmentIds(Delegator delegator,Timestamp fromDate,Timestamp thruDate){
		 List shipments = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate, null);
		 return shipments;
	 }
	 
	 public static List getByProdShipmentIds(Delegator delegator,Timestamp fromDate,Timestamp thruDate,List routeIds){
			
			List conditionList= FastList.newInstance(); 
			List shipmentList =FastList.newInstance();
			List shipments = FastList.newInstance();
			Timestamp dayBegin = UtilDateTime.nowTimestamp();
			Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
			try{
				List<GenericValue> shipmentTypeIds = delegator.findList("ShipmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.IN ,UtilMisc.toList("LMS_SHIPMENT","LMS_SHIPMENT_SUPPL")) , null , null, null, false);
				conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN , EntityUtil.getFieldListFromEntityList(shipmentTypeIds, "shipmentTypeId", true)));
			}catch (Exception e) {
				Debug.logError(e, "Exception while getting shipment ids ", module);		   
			}
			
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
			if(UtilValidate.isNotEmpty(routeIds)){
				conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.IN , routeIds));
			}
			if(!UtilValidate.isEmpty(fromDate)){
				dayBegin = UtilDateTime.getDayStart(fromDate);
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
			}
			
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
			try {
				shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
			}catch (Exception e) {
				Debug.logError(e, "Exception while getting shipment ids ", module);		   
			}
			if(!UtilValidate.isEmpty(shipmentList)){
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}		
			
			return shipments;
	}
	   
	 public static List getDayShipmentIds(Delegator delegator,Timestamp fromDate, Timestamp thruDate,String shipmentTypeId,List routeIds){
			//TO DO:for now getting one shipment id  we need to get pm and am shipment id irrespective of Shipment type Id
			List conditionList= FastList.newInstance();
			List shipmentList =FastList.newInstance();
			List shipments = FastList.newInstance();
			Timestamp dayBegin = UtilDateTime.getDayStart(fromDate);
			Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);		
			if(UtilValidate.isNotEmpty(shipmentTypeId) && ("AM".equals(shipmentTypeId))){
				conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN , UtilMisc.toList("AM_SHIPMENT")));
				if(UtilValidate.isNotEmpty(routeIds)){
					conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.IN , routeIds));
				}
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
				try {
					shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
				}catch (Exception e) {
					Debug.logError(e, "Unable to get shipment list: ", module);		   
				}
				if(!UtilValidate.isEmpty(shipmentList)){
					shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
				}
			}
			else if(UtilValidate.isNotEmpty(shipmentTypeId) && ("PM".equals(shipmentTypeId))){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN , UtilMisc.toList("PM_SHIPMENT")));
				if(UtilValidate.isNotEmpty(routeIds)){
					conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.IN , routeIds));
				}
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.addDaysToTimestamp(dayBegin, -1)));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.addDaysToTimestamp(dayEnd, -1)));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
				try {
					shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
				}catch (Exception e) {
					Debug.logError(e, "Unable to get shipment list: ", module);		   
				}
				if(!UtilValidate.isEmpty(shipmentList)){
					shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
				}
			}else{
				conditionList.clear();
				//conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS , shipmentTypeId));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
				try {
					shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
				}catch (Exception e) {
					Debug.logError(e, "Unable to get shipment list: ", module);		  	   
				}			
				shipments = EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false);	
			}
			return shipments;
		} 
	 
	
	public static Map<String, Object> getByProductActiveFacilities(Delegator delegator, Timestamp effectiveDate){
		    
		Map<String, Object> result = FastMap.newInstance(); 
		List<String> boothIds = FastList.newInstance(); 
	    List<GenericValue> booths = null;
	    	
	    if(UtilValidate.isEmpty(effectiveDate)){
	    	effectiveDate = UtilDateTime.nowTimestamp();
	    }
		result = ByProductServices.getAllByproductBooths(delegator, effectiveDate);
		List excludeList = UtilMisc.toList("REPLACEMENT_BYPROD","BYPROD_GIFT","BYPRODUCTS","BYPROD_SO","SP_SALES");
		if(UtilValidate.isNotEmpty(result.get("boothsList"))){
			booths = (List)result.get("boothsList");
			booths = EntityUtil.filterByCondition(booths, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_IN, excludeList));
			booths = EntityUtil.filterByCondition(booths, EntityCondition.makeCondition("byProdRouteId", EntityOperator.NOT_EQUAL, null));
			boothIds = EntityUtil.getFieldListFromEntityList(booths, "facilityId", true);
		}
		result.put("boothsList", booths);
	    result.put("boothsIdsList", boothIds);
		return result;
	}
	 
	 public static List getByProdShipmentIdsByType(Delegator delegator,Timestamp fromDate,Timestamp thruDate ,String shipmentTypeId){
		 List shipments = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, fromDate, thruDate, shipmentTypeId, null);
		 return shipments;
	 }
	 public static List getByProdShipmentIdsByType(Delegator delegator,Timestamp fromDate,Timestamp thruDate ,String shipmentTypeId, List routeIds){
			
			List conditionList= FastList.newInstance(); 
			List shipmentList =FastList.newInstance();
			List shipments = FastList.newInstance();
			Timestamp dayBegin = UtilDateTime.nowTimestamp();
			Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS,shipmentTypeId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
			if(UtilValidate.isNotEmpty(routeIds)){
				conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.IN , routeIds));
			}
			if(!UtilValidate.isEmpty(fromDate)){
				dayBegin = UtilDateTime.getDayStart(fromDate);
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
			}
			
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
			try {
				shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
			}catch (Exception e) {
				Debug.logError(e, "Exception while getting shipment ids ", module);		   
			}
			if(!UtilValidate.isEmpty(shipmentList)){
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}		
			
			return shipments;
		}
	 
	 public static Map<String, Object> getFacilityFieldStaff(DispatchContext dctx, Map<String, ? extends  Object> context ){
		    
			Map<String, Object> result = FastMap.newInstance();
			List conditionList= FastList.newInstance(); 
			List<String> boothIds = FastList.newInstance(); 
		    List<GenericValue> facilityPartyList = null;
		    Timestamp saleDate = UtilDateTime.nowTimestamp();
		    Delegator delegator = dctx.getDelegator();
		    Map<String, String> facilityFieldStaffMap = FastMap.newInstance();
		    Map<String, List> fieldStaffAndFacility = FastMap.newInstance();
		    if(UtilValidate.isNotEmpty(context.get("saleDate"))){
		    	saleDate = (Timestamp)context.get("saleDate");
		    }
		    
		    conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"FIELD_STAFF"));
		    EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
		    try {
		    	facilityPartyList =delegator.findList("FacilityParty", condition, null , null, null, false);
		    	facilityPartyList = EntityUtil.filterByDate(facilityPartyList, saleDate);
		    	for(GenericValue facilityParty : facilityPartyList){
		    		facilityFieldStaffMap.put(facilityParty.getString("facilityId"), facilityParty.getString("partyId"));
		    		if(UtilValidate.isNotEmpty(fieldStaffAndFacility.get(facilityParty.getString("partyId")))){
		    			List facilityList = fieldStaffAndFacility.get(facilityParty.getString("partyId"));
		    			facilityList.add(facilityParty.getString("facilityId"));
		    			fieldStaffAndFacility.put(facilityParty.getString("partyId"), facilityList);
		    			
		    		}else{
		    			fieldStaffAndFacility.put(facilityParty.getString("partyId"), UtilMisc.toList(facilityParty.getString("facilityId")));
		    		}
		    	}
		    	
			}catch (Exception e) {
				Debug.logError(e, "Exception while getting FaclityParty ", module);		   
			}
			result.put("facilityFieldStaffMap", facilityFieldStaffMap);
			result.put("fieldStaffAndFacility", fieldStaffAndFacility);
			return result;
	}
	
	 public static Map<String, Object> getProductPricesByDate(Delegator delegator, LocalDispatcher dispatcher, Map<String, ? extends Object> context) {
	        Map<String, Object> result = FastMap.newInstance();
	        Map<String, Object> priceByDateMap = FastMap.newInstance();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");          
	        String productStoreId = (String) context.get("productStoreId");
	        String facilityId = (String) context.get("facilityId");
	        String partyId = (String) context.get("partyId");
	        Timestamp fromDate = (Timestamp) context.get("fromDate");
	        String facilityCategory = (String) context.get("facilityCategory");
	        Map prodPriceMap = FastMap.newInstance();
	        Timestamp priceDate = UtilDateTime.getDayStart(fromDate);
	        if (UtilValidate.isEmpty(fromDate)) {
	            Debug.logError("fromDate cannot be empty", module);
	            return ServiceUtil.returnError("fromDate cannot be empty");        	
	        }        
	        String productStoreGroupId = "";
	        String productPriceTypeId = (String) context.get("productPriceTypeId");
	       
	        if (UtilValidate.isEmpty(productPriceTypeId)) {
	        	productPriceTypeId = "DEFAULT_PRICE";
	        }
	        
	       /* if(UtilValidate.isEmpty(facilityCategory)){
	        	facilityCategory ="DEFAULT";
	        }*/
	        
	        String currencyDefaultUomId = (String) context.get("currencyUomId");
	        if (UtilValidate.isEmpty(currencyDefaultUomId)) {
	            currencyDefaultUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "INR");
	        }
	        
	        Timestamp dayBegin =UtilDateTime.getDayStart(fromDate);
	    	List<GenericValue> productList =FastList.newInstance();
	    	List condList =FastList.newInstance();
	    	condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, UtilMisc.toList("CONTINUES_INDENT","DAILY_INDENT")));
	    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	    			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
	    	EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
	    	  List<String> orderBy = UtilMisc.toList("sequenceNum");
	    	  
	    	try{
	    		
	    		productList =delegator.findList("ProductAndCategoryMember", discontinuationDateCondition,UtilMisc.toSet("productId"),orderBy, null, false);
	    	}catch (GenericEntityException e) {
				// TODO: handle exception
	    		Debug.logError(e, module);
			} 
	       List productIdsList=FastList.newInstance();
	       productIdsList = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
	       if(UtilValidate.isNotEmpty(productIdsList)){
	    	   for(int i = 0 ; i<productIdsList.size();i++){
	    		    String eachProd = (String)productIdsList.get(i);
	        		Map<String, Object> priceContext = FastMap.newInstance();
	        		priceContext.put("userLogin", userLogin);   
	        		priceContext.put("productStoreId", productStoreId);                    
	        		priceContext.put("productId", eachProd);
	        		priceContext.put("priceDate", priceDate);
	        		priceContext.put("facilityId", facilityId);
	        		priceContext.put("partyId", partyId);
	        		priceContext.put("facilityCategory", facilityCategory);
	        		Map priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext); 
	        		if (ServiceUtil.isError(priceResult)) {
	        			Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
	        			return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
	        		}
	        		prodPriceMap.put(eachProd, priceResult.get("totalPrice"));
	    	   }
	       }
	        result.put("priceMap", prodPriceMap);
	        return result;
	    }
	 
	 	public static Map<String, Object> getBoothChandentIndent(DispatchContext dctx, Map<String, ? extends Object> context){
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String supplyDateStr = (String) context.get("supplyDate"); 
	        String boothId = (String) context.get("boothId");
	        String routeId = (String) context.get("routeId");
	        String screenFlag = (String) context.get("screenFlag");
	        String tripId = (String) context.get("tripId");
	        Boolean isEnableProductSubscription = Boolean.FALSE;
	        String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
	        String subscriptionTypeId = (String) context.get("subscriptionTypeId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        if(UtilValidate.isNotEmpty(context.get("isEnableProductSubscription"))){
	           isEnableProductSubscription=(Boolean) context.get("isEnableProductSubscription");
	        }
	        Map result = ServiceUtil.returnSuccess(); 
	        List changeIndentProductList = FastList.newInstance();
	        List changeQtyList = FastList.newInstance();
	        Map<String ,BigDecimal> prevQuantityMap =FastMap.newInstance();
	        /*List<GenericValue> contIndentProducts = FastList.newInstance();
	        List<GenericValue> dayIndentProducts = FastList.newInstance();
	        List<String> crateIndentProductList = FastList.newInstance();
	        List<String> packetIndentProductList = FastList.newInstance();
	        */
	        Map prodIndentQtyCat = FastMap.newInstance();
	        Map qtyInPieces = FastMap.newInstance();
	        List productList = FastList.newInstance();
	        List conditionList = FastList.newInstance();
	        String productStoreId = (String)(ByProductServices.getByprodFactoryStore(delegator)).get("factoryStoreId");
	        String partyId = "";
	        String facilityCategory = "";
	        Timestamp supplyDate = null;
	        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
	        if(UtilValidate.isNotEmpty(supplyDateStr)){
		  		  try {
		  			supplyDate = new java.sql.Timestamp(sdf.parse(supplyDateStr).getTime());
		  		  } catch (ParseException e) {
		  			  Debug.logError(e, "Cannot parse date string: " + supplyDateStr, module);
		  		  } catch (NullPointerException e) {
		  			  Debug.logError(e, "Cannot parse date string: " + supplyDateStr, module);
		  		  }
			  }
		  	  else{
		  		supplyDate = UtilDateTime.nowTimestamp();
		  	  }
	          Timestamp dayBegin = UtilDateTime.getDayStart(supplyDate);
  		      Timestamp dayEnd = UtilDateTime.getDayStart(supplyDate);
  		
  		      try{
	    		
	    		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), true);
	    		if(UtilValidate.isEmpty(facility)){
	    			Debug.logError("Invalid  Booth Id", module);
	    			return ServiceUtil.returnError("Invalid  Booth Id");    				
	    		}
	    		boolean isActive = EntityUtil.isValueActive(facility , dayBegin, "openedDate", "closedDate");
	    		if(!isActive){
	    			Debug.logError("is not active facility "+boothId, module);    			
	    			return ServiceUtil.returnError("The  facility ' "+ boothId+"' is not Active."); 
	    		}
	    		partyId = facility.getString("ownerPartyId");
	    		facilityCategory = facility.getString("categoryTypeEnum");
	    		//lets override productSubscriptionTypeId based on facility category
	    		
	    		if(UtilValidate.isEmpty(routeId)){
	    			Map boothCtxMap = FastMap.newInstance();
	    			boothCtxMap.putAll(context);
	    			boothCtxMap.put("supplyDate", supplyDate);
	    			Map boothDetails = (Map)(getBoothRoute(dctx, boothCtxMap)).get("boothDetails");
	    			routeId = (String)boothDetails.get("routeId");
	    		}
	    		
	    		List prevshipmentIds = getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(supplyDate, "yyyy-MM-dd HH:mm:ss"),subscriptionTypeId, routeId);
	    		if(UtilValidate.isNotEmpty(prevshipmentIds)){
	    			return ServiceUtil.returnError("Trucksheet already generated for the route :"+ routeId); 
	    		}
	    		 if(!isEnableProductSubscription){
	    			if(UtilValidate.isEmpty(facility.getString("categoryTypeEnum"))){
	    				productSubscriptionTypeId = "CASH";
	    			} 
	    			else{
	    				if(facility.getString("categoryTypeEnum").equals("SO_INST")){
	  	    			  productSubscriptionTypeId = "SPECIAL_ORDER";
	  	    		  	}
	    				if(facility.getString("categoryTypeEnum").equals("CR_INST")){
	  	    			 productSubscriptionTypeId = "CREDIT";
	  	    		  	}
	    			}
	    		}
	    		BigDecimal securityDeposit = BigDecimal.ZERO;  
	    		if(UtilValidate.isNotEmpty(facility.get("securityDeposit"))){
	    			securityDeposit = facility.getBigDecimal("securityDeposit");
	    		}
	    		result.put("securityDeposit", securityDeposit);
	    		result.put("boothName", facility.getString("facilityName"));
	    		Map inputCtx = FastMap.newInstance();
	    		inputCtx.put("userLogin", userLogin);
	    		inputCtx.put("supplyDate",dayBegin);
	    		inputCtx.put("facilityId",boothId);
	    		Map qtyResultMap = getFacilityIndentQtyCategories(dctx, inputCtx);
	    		prodIndentQtyCat = (Map)qtyResultMap.get("indentQtyCategory");
	    		qtyInPieces = (Map)qtyResultMap.get("qtyInPieces");
	    		result.put("prodIndentQtyCat", prodIndentQtyCat);
	    		result.put("qtyInPieces", qtyInPieces);
	    		/*contIndentProducts = ProductWorker.getProductsByCategory(delegator ,"CONTINUES_INDENT" ,UtilDateTime.getDayStart(supplyDate));
	    		dayIndentProducts = ProductWorker.getProductsByCategory(delegator ,"DAILY_INDENT" ,UtilDateTime.getDayStart(supplyDate));
	    		
	    		crateIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"CRATE_INDENT" ,UtilDateTime.getDayStart(supplyDate)), "productId", true);
	    		packetIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"PACKET_INDENT" ,UtilDateTime.getDayStart(supplyDate)), "productId", true);
	    		*/
	    	     conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,boothId));
	    	     if(subscriptionTypeId.equals("AM")){
	    	    		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId) ,EntityOperator.OR ,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
	    	     }else{
	    	        	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
	    	     }
	    	     if(UtilValidate.isNotEmpty(tripId)){
	    	    	 result.put("tripId", tripId);
	    	    	 conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
	    	     }
	    	    EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		List<GenericValue> subscriptionList = EntityUtil.filterByDate(delegator.findList("Subscription", condition, null, null, null, false) , UtilDateTime.addDaysToTimestamp(dayBegin, -1));
	    		if(UtilValidate.isEmpty(subscriptionList)){
	    			Debug.logError("No Active Data For given Booth", module);
	    			return ServiceUtil.returnError("No Active Data For given Booth");
	    				
	    		}
	    		result.put("routeId", routeId);
	    		result.put("tempRouteId",routeId);
	    		String subscriptionId = (EntityUtil.getFirst(subscriptionList)).getString("subscriptionId");
	    		
	    		conditionList.clear();
	    		conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
	    		conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
	    		if(!screenFlag.equals("indentAlt")){
	    			conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
	    		}
	    		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(dayBegin, -1))) , EntityOperator.OR ,EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null) ));
	    		EntityCondition cond= EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    		
	    		List<GenericValue> subProdList =delegator.findList("SubscriptionProduct", cond, null,null, null, false);
	    		subProdList = EntityUtil.filterByDate(subProdList , dayBegin);
	    		if(UtilValidate.isNotEmpty(subProdList)){
	    			productList.addAll(EntityUtil.getFieldListFromEntityList(subProdList, "productId", true));
	    		}
	    		List<String> orderBy = UtilMisc.toList("-sequenceNum");
	    		productList = delegator.findList("Product",EntityCondition.makeCondition("productId" , EntityOperator.IN, productList), null, orderBy, null, true);
	    		String qtyCategory = "";
	    		for(GenericValue product : subProdList){
	    			Map changeQuantityMap =FastMap.newInstance();
	    			//result.put("tempRouteId", product.getString("sequenceNum"));
	    			String productId = product.getString("productId");
	    			if(UtilValidate.isNotEmpty(prodIndentQtyCat)){
	    				qtyCategory = (String)prodIndentQtyCat.get(productId);
	    			}
	    			if(screenFlag.equals("indentAlt")){
	    				Map tempMap = FastMap.newInstance();
		    			tempMap.put("quantity", product.getBigDecimal("quantity"));
		    			tempMap.put("seqRouteId", product.getString("sequenceNum"));
		    			changeQuantityMap.put(productId, tempMap);
	    			}
	    			else{
	    				changeQuantityMap.put(productId, product.getBigDecimal("quantity"));
	    			}
	    			changeQtyList.add(changeQuantityMap);
	    			
	    			//BigDecimal quantityIncluded = (EntityUtil.getFirst(EntityUtil.filterByAnd(productList, UtilMisc.toMap("productId",productId)))).getBigDecimal("quantityIncluded");
	    			/*if(UtilValidate.isEmpty(changeQuantityMap.get(productId))){
	    				changeQuantityMap.put(productId, product.getBigDecimal("quantity")); 
	    				if(UtilValidate.isNotEmpty(qtyCategory) && (qtyCategory.equals("CRATE") || qtyCategory.equals("CAN")) && !productSubscriptionTypeId.equals("EMP_SUBSIDY")){//if(crateIndentProductList.contains(productId)){
	    					if(UtilValidate.isEmpty(product.getBigDecimal("crateQuantity"))){
	    						product.set("crateQuantity", convertPacketsToCrates(dctx, UtilMisc.toMap("userLogin", userLogin, "productId", productId, "packetQuantity", product.getBigDecimal("quantity"))));
	    						product.store();
	    					}else{
	    						changeQuantityMap.put(productId, product.getBigDecimal("crateQuantity"));
	    					}
	    					
	    				}
	    				   				
	    			}else{
	    				changeQuantityMap.put(productId, (product.getBigDecimal("quantity")).add(prevQuantityMap.get(productId)));
	    				if(UtilValidate.isNotEmpty(qtyCategory) && (qtyCategory.equals("CRATE") || qtyCategory.equals("CAN"))){//if(crateIndentProductList.contains(productId)){
	    					changeQuantityMap.put(productId, product.getBigDecimal("crateQuantity"));
	    				}
	    			}*/
	            }
	    		
	    		
	    	}catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}
	    	
	        //productList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("salesDate", dayBegin));
	    	
	       /* if(dayIndentProducts.size() <=  contIndentProducts.size()){
	        	productList = contIndentProducts;
	        }else{
	        	productList = dayIndentProducts;
	        }
	        GenericValue dayIndentProduct= null;
	        GenericValue conIndentProduct= null;
	        */
	    	for(int i= (changeQtyList.size()-1);i >=0 ; i--){
	        	Map tempChangeProdMap = FastMap.newInstance();
	        	Map changeQuantityMap = (Map)changeQtyList.get(i);
	        	Iterator tempIter = changeQuantityMap.entrySet().iterator();
	        	String productId = "";
				while (tempIter.hasNext()) {
					Map.Entry tempEntry = (Entry) tempIter.next();
		        	productId = (String)tempEntry.getKey();
				}
				GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(productList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)));
	        	/*if(UtilValidate.isNotEmpty(dayIndentProducts) &&  (dayIndentProducts.size() > i)){
	        		dayIndentProduct = dayIndentProducts.remove(i);        	
	        	}
	        	if(UtilValidate.isNotEmpty(contIndentProducts) && (contIndentProducts.size() >i)){
	        		conIndentProduct = contIndentProducts.remove(i);
	        		
	        	}*/
	        	
	        	/*if(UtilValidate.isNotEmpty(conIndentProduct)){*/
	        	
	        		tempChangeProdMap.put("id",productId);
	            	tempChangeProdMap.put("cProductId", productId);
	            	tempChangeProdMap.put("cProductName", product.getString("brandName")+" [ "+product.getString("description")+"]");
	            	tempChangeProdMap.put("cQuantity","");
	            	if(screenFlag.equals("indentAlt")){
	            		tempChangeProdMap.put("seqRouteId","");
	            	}
	            	//String qtyIndentCat = (String)prodIndentQtyCat.get(productId);
	            	/*if(UtilValidate.isNotEmpty(qtyIndentCat) && qtyIndentCat.equals("CRATE")){
	            		tempChangeProdMap.put("indentProdCat","CR");
	            	}
	            	else if(UtilValidate.isNotEmpty(qtyIndentCat) && qtyIndentCat.equals("CAN")){
	            		tempChangeProdMap.put("indentProdCat","CN");
	            	}
	            	else{
	            		tempChangeProdMap.put("indentProdCat","P");
	            	}*/
	            	
	            	/*if(crateIndentProductList.contains(productId)){
	            		tempChangeProdMap.put("indentProdCat","C");
	            	}
	            	
	            	if(packetIndentProductList.contains(productId)){
	            		tempChangeProdMap.put("indentProdCat","P");
	            	}*/
	            	if(UtilValidate.isNotEmpty(changeQuantityMap.get(productId))){
	            		
	            		if(screenFlag.equals("indentAlt")){
	            			tempChangeProdMap.put("cQuantity", (BigDecimal)((Map)changeQuantityMap.get(productId)).get("quantity"));
	            			tempChangeProdMap.put("seqRouteId", (String)((Map)changeQuantityMap.get(productId)).get("seqRouteId"));
	            		}
	            		else{
	            			tempChangeProdMap.put("cQuantity", (BigDecimal)changeQuantityMap.get(productId));
	            		}
	            		
	            		/*Map prodQtyMap = (Map)changeQuantityMap.get(productId);
	            		Map tempMap = FastMap.newInstance();
	            		tempMap.put("quantity", prodQtyMap.get("quantity"));
	            		tempMap.put("crateQuantity", prodQtyMap.get("crateQuantity"));
	            		tempChangeProdMap.put("cQuantity", tempMap);*/
	            	}/*else{
	            		tempChangeProdMap.put("cQuantity", prevQuantityMap.get(productId));
	            	}*/
	            	/*if(UtilValidate.isNotEmpty(prevQuantityMap.get(productId))){
	            		tempChangeProdMap.put("cPrevQuantity", prevQuantityMap.get(productId));
	            	}*/         	
	            	
	        	/*}*/
	        	
	        	/*if(UtilValidate.isNotEmpty(dayIndentProduct)){
	        		tempChangeProdMap.put("id",dayIndentProduct.getString("productId"));
	            	tempChangeProdMap.put("dProductId", dayIndentProduct.getString("productId"));
	            	tempChangeProdMap.put("dProductName", dayIndentProduct.getString("productName")+"["+dayIndentProduct.getString("productId")+"]");
	            	tempChangeProdMap.put("dQuantity","");
	            	if(UtilValidate.isNotEmpty(changeQuantityMap.get(dayIndentProduct.getString("productId")))){
	            		tempChangeProdMap.put("dQuantity", changeQuantityMap.get(dayIndentProduct.getString("productId")));
	            	}
	            	 
	        	}       */ 	
	        	
	        	changeIndentProductList.add(tempChangeProdMap);
	        }
	    	//Collections.reverse(changeIndentProductList);
	    	//Collections.sort(changeIndentProductList);
	    	// lets populate route totals
	    	try{
	    		conditionList.clear();
    			conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
	    		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
	    		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(dayBegin, -1))) , EntityOperator.OR ,EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null) ));
	    		if(UtilValidate.isNotEmpty(tripId)){
	    			conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.LESS_THAN_EQUAL_TO, tripId));
	    		}
	    		EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    		  
	    		List<GenericValue> subProdList =delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condExpr, null,null, null, false);
	    		
	    		subProdList = EntityUtil.filterByDate(subProdList , dayBegin);
	    		/* Hard coded the categories ... get the few categories by type for indent totals*/
	    		
	    		result.put("routeCapacity", BigDecimal.ZERO);
	    		
	    		GenericValue route = delegator.findOne("Facility", UtilMisc.toMap("facilityId", routeId), false);
    			result.put("routeName", route.getString("facilityName"));
    			result.put("routeCapacity", route.getBigDecimal("facilitySize"));
	    		List condProdCatList = UtilMisc.toList("MILK","CURD", "FMILK_CATEGORY");
	    		
	    		conditionList.clear();
	    		conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, condProdCatList));
	    		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(subProdList, "productId", true)));
	    		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    		List<GenericValue> productCategoryList = delegator.findList("ProductAndCategoryMember", cond, null, null, null, true);
	    		
	    		
	    		BigDecimal routeCrateTotal = BigDecimal.ZERO;
	    		Map<String ,BigDecimal> categoryTotals = FastMap.newInstance();
	    		for(GenericValue subProd : subProdList){
	    			String productId = subProd.getString("productId");
	    			GenericValue product = EntityUtil.getFirst(EntityUtil.filterByAnd(productCategoryList, UtilMisc.toMap("productId", productId)));
	    			if(UtilValidate.isNotEmpty(product)){
	    				BigDecimal qtyInc = product.getBigDecimal("quantityIncluded");
	        			BigDecimal qty = subProd.getBigDecimal("quantity");
	        			String category = product.getString("productCategoryId");
	        			if(UtilValidate.isEmpty(categoryTotals.get(category))){
	        				categoryTotals.put(category ,BigDecimal.ZERO);
	        			}
	        			categoryTotals.put(category ,(categoryTotals.get(category)).add(qty.multiply(qtyInc)));
	    			}
	    			
	    			if(UtilValidate.isNotEmpty(subProd.getBigDecimal("crateQuantity"))){
	    				routeCrateTotal = routeCrateTotal.add(subProd.getBigDecimal("crateQuantity"));
	    			}
	    		}
	    		int totalCrates = routeCrateTotal.intValue();
	    		result.put("routeCrateTotal", totalCrates);
	    		
	    		Map prodPriceMap = FastMap.newInstance();
	    		Map inputProductRate = FastMap.newInstance();
	    		inputProductRate.put("productStoreId", productStoreId);
	    		inputProductRate.put("fromDate",dayBegin);
	    		inputProductRate.put("facilityId",boothId);
	    		inputProductRate.put("partyId",partyId);
	    		inputProductRate.put("facilityCategory",facilityCategory);
	    		inputProductRate.put("userLogin",userLogin);
	    		Map priceResultMap = getProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
	    		prodPriceMap = (Map)priceResultMap.get("priceMap");
	    		result.put("productPrice", prodPriceMap);
	    		
	    		BigDecimal totalMilkInLtrs = BigDecimal.ZERO;
	    		List categoryList = FastList.newInstance();
	    		for ( Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()){
	    			Map categoryTotalMap = FastMap.newInstance();
		        	String categoryId = (String)entry.getKey();
		        	BigDecimal qtyLtr = (BigDecimal)entry.getValue();
		        	totalMilkInLtrs = totalMilkInLtrs.add(qtyLtr);
		        	categoryTotalMap.put("categoryId", categoryId);
		        	categoryTotalMap.put("totalLtr", qtyLtr);
		        	categoryList.add(categoryTotalMap);
	    		}
	    		result.put("routeTotalLtr", totalMilkInLtrs);
	    		result.put("categoryTotals", categoryList);
	    	}catch (Exception e) {
	    		Debug.logError(e, e.getMessage());
			}
	    	    	//changeIndentProductList = UtilMisc.sortMaps(changeIndentProductList ,UtilMisc.toList("cPrevQuantity","cProductName" ,"dProductName"));
			result.put("changeIndentProductList", changeIndentProductList);
			return result;
	    }
	 	public static Map<String ,Object>  processDSCorrectionHelper(DispatchContext dctx, Map<String, ? extends Object> context){
			  Delegator delegator = dctx.getDelegator();
		      LocalDispatcher dispatcher = dctx.getDispatcher();
		      Locale locale = (Locale) context.get("locale");
		      GenericValue userLogin = (GenericValue) context.get("userLogin");
		      Map<String, Object> result = ServiceUtil.returnSuccess();
		      String productSubscriptionTypeId = (String)context.get("productSubscriptionTypeId");
		      String subscriptionId = (String)context.get("subscriptionId");
		      String boothId = (String)context.get("boothId");
		      String orderId = (String)context.get("orderId");
		      String shipmentId = (String)context.get("shipmentId");
		      String shipmentTypeId = (String)context.get("shipmentTypeId");
		      Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");	      
		      List<Map> productQtyList = (List)context.get("productQtyList");
		      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		      String currencyUomId = "INR";
		      String change = "NotChanged";
		      String partyId = "";
		      String productStoreId = "";
		      String productPriceTypeId = "";
		      List<GenericValue> orderList = FastList.newInstance();
		      List conditionList = FastList.newInstance();
		      List<GenericValue> orderInv = FastList.newInstance();
		      try{
		    	  productStoreId = (String)ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
			      orderList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			      orderInv = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("invoiceId"), null, null, false);
			   	  
			      GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", boothId), false);
			      partyId = facility.getString("ownerPartyId");
		      }catch(GenericEntityException e){
		    	  Debug.logError(e, e.toString(), module);
		    	  return ServiceUtil.returnError("Error Fetching Orders Item for dealer"+boothId);
		      }
			  boolean orderChangeFlag = false;	      
			  for(int i=0; i< productQtyList.size() ; i++){
				  Map productQtyMap = productQtyList.get(i);
				  String productId = (String)productQtyMap.get("productId");
				  String sequenceNum = (String)productQtyMap.get("sequenceNum");
				  BigDecimal quantity = (BigDecimal)productQtyMap.get("quantity");
				  GenericValue orderItemInfo = EntityUtil.getFirst(EntityUtil.filterByAnd(orderList, UtilMisc.toMap("productId", productId)));
				  if(UtilValidate.isEmpty(orderItemInfo)){
					  orderChangeFlag = true;
				  }
				  else{
					  BigDecimal qty = orderItemInfo.getBigDecimal("quantity");
					  if(quantity.compareTo(qty) != 0){
						 orderChangeFlag = true;
					  }
				  }
			  }
			      
			  if(orderChangeFlag){
			   	  change = "Changed";
			   	  String invoiceId = (EntityUtil.getFirst(orderInv)).getString("invoiceId");
				  try{
					  result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", UtilMisc.toList(orderId),"userLogin", userLogin));
					  if (ServiceUtil.isError(result)) {
						  Debug.logError("Problem cancelling orders in Correction", module);	 		  		  
				 		  return ServiceUtil.returnError("Problem cancelling orders in Correction");
					  } 			
					  
					  result = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", UtilMisc.toList(invoiceId), "statusId", "INVOICE_CANCELLED", "userLogin", userLogin));
				   	  if (ServiceUtil.isError(result)) {
				   		  Debug.logError("Problem cancelling invoice in Correction", module);	 		  		  
				   		  return ServiceUtil.returnError("Problem cancelling invoice in Correction");
					  }	        	  
				  }catch (GenericServiceException e) {
					  Debug.logError(e, e.toString(), module);
					  return ServiceUtil.returnError("Problem cancelling orders and invoice in Correction");
				  }
				  
				  ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale, currencyUomId);
				      
				  cart.setOrderType("SALES_ORDER");
				  cart.setChannelType("BYPROD_SALES_CHANNEL");
				  cart.setProductStoreId(productStoreId);
				  cart.setBillToCustomerPartyId(partyId);
				  cart.setPlacingCustomerPartyId(partyId);
				  cart.setShipToCustomerPartyId(partyId);
				  cart.setEndUserCustomerPartyId(partyId);
				  cart.setFacilityId(boothId);
				  cart.setEstimatedDeliveryDate(effectiveDate);
				  cart.setProductSubscriptionTypeId(productSubscriptionTypeId);
				  cart.setShipmentId(shipmentId);
				  try {
					  cart.setUserLogin(userLogin, dispatcher);
				  } catch (Exception exc) {
					  Debug.logError("Error setting userLogin in the cart: " + exc.getMessage(), module);
					  return ServiceUtil.returnError("Error setting userLogin in the cart: " + exc.getMessage());
				  }
				      
				  for(int i=0; i< productQtyList.size() ; i++){
					  Map productQtyMap = productQtyList.get(i);
					  String productId = (String)productQtyMap.get("productId");
					  String sequenceNum = (String)productQtyMap.get("sequenceNum");
					  BigDecimal quantity = (BigDecimal)productQtyMap.get("quantity");
					  Map<String, Object> priceResult;
			          Map<String, Object> priceContext = FastMap.newInstance();
			          
			          priceContext.put("userLogin", userLogin);   
			          priceContext.put("productStoreId", productStoreId);                    
			          priceContext.put("productId", productId);
			          priceContext.put("facilityId", boothId);
			          priceContext.put("priceDate", effectiveDate);
			          priceContext.put("partyId", partyId);
			      	  priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);    
			          if (ServiceUtil.isError(priceResult)) {
			        	  Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
						  return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));
			          }
			          BigDecimal totalPrice = (BigDecimal)priceResult.get("totalPrice");
	                  List taxList = (List)priceResult.get("taxList");
			          try{
			        	  List<ShoppingCartItem> tempCartItems =cart.findAllCartItems(productId);
			        	  ShoppingCartItem item = null;
			        	  if(tempCartItems.size() >0){
			        		  item = tempCartItems.get(0);
			                  item.setQuantity(item.getQuantity().add(quantity), dispatcher, cart);
			                  item.setBasePrice((BigDecimal)priceResult.get("basicPrice"));
			            			
			        	  }else{
			        		  cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), productId, null,quantity, (BigDecimal)priceResult.get("basicPrice"),
			                                null, null, null, null, null, null, null,
			                                null, null, null, null, null, null, dispatcher,
			                                cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE)); 
			        	  } 
			        	  item.setListPrice(totalPrice);
			        	  item.setTaxDetails(taxList);
			          }catch (Exception exc) {
			        	  Debug.logError("Error adding product with id " + productId + " to the cart: " + exc.getMessage(), module);
			        	  return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart");
			          }
				  }
				  cart.setDefaultCheckoutOptions(dispatcher);
				  CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
				  
				  List<GenericValue> applicableTaxTypes = null;
				  try {
					  applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "TAX"), null, null, null, false);
				  } catch (GenericEntityException e) {
					  Debug.logError(e, "Failed to retrive ProductPriceType ", module);
					  return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
				  }
				  
				  List applicableTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);
				  List<GenericValue> prodPriceType = null;
				        
				  conditionList.clear();
				  conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, productPriceTypeId+"%"));
				  conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, applicableTaxTypeList));
				  EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				        
				  try {
					  prodPriceType = delegator.findList("ProductPriceAndType", condition1, null, null, null, false);
				  } catch (GenericEntityException e) {
					  Debug.logError(e, "Failed to retrive ProductPriceAndType ", module);
					  return ServiceUtil.returnError("Failed to retrive ProductPriceAndType " + e);
				  }
				       
				  try {
					  checkout.calcAndAddTax(prodPriceType);
				  } catch (Exception e) {
					  Debug.logError(e, "Failed to add tax amount ", module);
					  return ServiceUtil.returnError("Failed to add tax amount  " + e);
				  }
				  Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
				  String newOrderId = (String) orderCreateResult.get("orderId");
		        // approve the order
				  if (UtilValidate.isNotEmpty(newOrderId)) {
					  boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, newOrderId);       
					  try{            	
						  result = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", newOrderId,"userLogin", userLogin));
						  if (ServiceUtil.isError(result)) {
							  Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(result), module);
							  return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(result));          	            
				          } 
					  }catch (Exception e) {
						  Debug.logError(e, module);
						  return ServiceUtil.returnError("Error calling the service createInvoiceForAllItems");
					  } 
				  }
			   }
			  result = ServiceUtil.returnSuccess();
			  result.put("indentChangeFlag", change);
			  return result;  
	    }
	 	
	 	public static Map getOpeningBalanceForBooth(DispatchContext dctx, Map<String, ? extends Object> context){
	    	//Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues ,boolean isPendingDues){
			//TO DO:for now getting one shipment id  we need to get pmand am shipment id irrespective of Shipment type Id
	    	Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    String facilityId = (String) context.get("facilityId");
		    List<String> facilityIds = (List<String>) context.get("facilityIds");
		    Timestamp saleDate = (Timestamp) context.get("saleDate");	  
		    List exprListForParameters = FastList.newInstance();
			List boothPaymentsList = FastList.newInstance();
			List boothOrdersList = FastList.newInstance();
			Set shipmentIds = FastSet.newInstance();		 
			Map openingBalanceMap = FastMap.newInstance();
			BigDecimal invoicesTotalAmount = BigDecimal.ZERO;
			BigDecimal invoicesTotalDueAmount = BigDecimal.ZERO;
			shipmentIds = new HashSet(getShipmentIds(delegator ,null ,saleDate));
			Timestamp dayBegin = UtilDateTime.getDayStart(saleDate);		
			List categoryTypeEnumList = UtilMisc.toList("SO_INST","CR_INST");
			boolean enableSoCrPmntTrack = Boolean.FALSE;		
			List prevPmInvIdList = FastList.newInstance();
			try{
				 GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSoCrPmntTrack"), true);
				 if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack) && (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
					 enableSoCrPmntTrack = Boolean.TRUE;
				 	} 
			}catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
			 }			
		    if(enableSoCrPmntTrack){
				//exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
				exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH") , EntityOperator.OR , EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN, categoryTypeEnumList)));
		    }else{
		    	exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH") , EntityOperator.OR , EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN, categoryTypeEnumList)));
			}
		    exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.NOT_EQUAL, "CARD"));
			
			if(facilityId != null){			
				try{
					GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);				
					if(facilityDetail == null || (!facilityDetail.getString("facilityTypeId").equals("BOOTH")) ){
						Debug.logInfo("facilityId '"+facilityId+ "'is not a Booth or Zone ", "");
						return ServiceUtil.returnError("facilityId '"+facilityId+ "'is not a Booth or Zone ");
					}
					facilityIds =FastList.newInstance();
					facilityIds.add(facilityId);
					//exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
				}catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
				}			
			}		
			exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
			// lets check the tenant configuration for enableSameDayPmEntry
			// if not same day entry exclude prev day PM Sales invoices from opening balance
			Boolean enableSameDayPmEntry = Boolean.FALSE;
			try{
				 GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSameDayPmEntry"), false);
				 if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry) && (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
					 enableSameDayPmEntry = Boolean.TRUE;
				}
			 }catch (GenericEntityException e) {
				// TODO: handle exception
				 Debug.logError(e, module);
			}		 
			//get all invoices for this facility that either haven't been paid  or  have been paid after the opening balance date
			 if(!enableSameDayPmEntry){			 
				 Timestamp prevDay = UtilDateTime.addDaysToTimestamp(dayBegin, -1);
				 List prevshipmentIds = getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(prevDay, "yyyy-MM-dd HH:mm:ss"),"PM");
				 if(UtilValidate.isNotEmpty(prevshipmentIds)){			 
				 // get previous PM Invoice ids
					 List exprListForPrevPMSales = FastList.newInstance();
						exprListForPrevPMSales.addAll(exprListForParameters);
						List invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");
						exprListForPrevPMSales.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, prevshipmentIds));
						//exprListForPrevPMSales.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN, dayBegin));
						exprListForPrevPMSales.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN, invoiceStatusList));
						EntityCondition	cond = EntityCondition.makeCondition(exprListForPrevPMSales, EntityOperator.AND);
						EntityFindOptions findOptions = new EntityFindOptions();
						findOptions.setDistinct(true);
						try{	
							List<GenericValue> boothPmOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", cond, UtilMisc.toSet("invoiceId") ,null, findOptions, false);
							prevPmInvIdList = EntityUtil.getFieldListFromEntityList(boothPmOrdersList, "invoiceId", true);
						}catch(GenericEntityException e){
							Debug.logError(e, module);	
							return ServiceUtil.returnError(e.toString());
						}
						
					exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_EQUAL, null) ,EntityOperator.AND ,EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_IN, prevshipmentIds)));	
				 
				 }else{
					 exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_EQUAL, null));
				 }
				 
			 }else{
				 exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_EQUAL, null));
			 }
				
			List invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");
			exprListForParameters.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN, dayBegin));
			exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN, invoiceStatusList));
			exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("paidDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
			EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			try{			
				boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, UtilMisc.toSet("invoiceId") ,null, findOptions, false);
				//get Opening invoices before given sale date and added to boothOrderList
				List<GenericValue> obInvoiceList = (List)getOpeningBalanceInvoices(dctx,UtilMisc.toMap("facilityIds",facilityIds,"thruDate", UtilDateTime.addDaysToTimestamp(dayBegin, -1),"isForCalOB","Y")).get("invoiceList");
				boothOrdersList.addAll(obInvoiceList);
			}catch(GenericEntityException e){
				Debug.logError(e, module);	
				return ServiceUtil.returnError(e.toString());
			}
			BigDecimal openingBalance = BigDecimal.ZERO;
			BigDecimal invoicePendingAmount = BigDecimal.ZERO;
			BigDecimal advancePaymentAmount = BigDecimal.ZERO;
			if (!UtilValidate.isEmpty(boothOrdersList)) {
				Set invoiceIdSet = new  HashSet(EntityUtil.getFieldListFromEntityList(boothOrdersList, "invoiceId", false));
				List invoiceIds = new ArrayList(invoiceIdSet);
				//First compute the total invoice outstanding amount as of opening balance date.
				for(int i =0 ; i< invoiceIds.size(); i++){
					String invoiceId = (String)invoiceIds.get(i);
					List<GenericValue> pendingInvoiceList =  FastList.newInstance();
					List exprList = FastList.newInstance(); 
					exprList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));				
					exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("paidDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
					exprList.add(EntityCondition.makeCondition("pmPaymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
					EntityCondition	cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);				
					try{			
						pendingInvoiceList = delegator.findList("InvoiceAndApplAndPayment", cond, null ,null, null, false);		        
					// no payment applications then add invoice total amount to OB or unapplied amount.										
						Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("userLogin",userLogin,"invoiceId",invoiceId));
						if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
				            Debug.logError(getInvoicePaymentInfoListResult.toString(), module);    			
				            return ServiceUtil.returnError(null, null, null, getInvoicePaymentInfoListResult);
				        }
						Map invoicePaymentInfo = (Map)((List)getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
						BigDecimal outstandingAmount = (BigDecimal)invoicePaymentInfo.get("outstandingAmount");	
						invoicePendingAmount = invoicePendingAmount.add(outstandingAmount);				
						for( GenericValue pendingInvoice : pendingInvoiceList){							
							invoicePendingAmount = invoicePendingAmount.add(pendingInvoice.getBigDecimal("amountApplied"));
						}
					
					}catch(Exception e){
						Debug.logError(e, module);	
						return ServiceUtil.returnError(e.toString());
					}
				}
			}
			//Now handle any unapplied payments as of opening balance date
			// Here first get the payments that were made before opening balance date and have been partially applied.  
			// Compute the amount that has been applied after opening balance date plus any unapplied amount
			List exprList = FastList.newInstance();
			List<GenericValue> pendingPaymentsList = FastList.newInstance();
			exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds));
			if(UtilValidate.isNotEmpty(prevPmInvIdList)){
				exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),EntityOperator.OR,EntityCondition.makeCondition("invoiceId", EntityOperator.IN,prevPmInvIdList)));
			}else{
				exprList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
			}
			
			//exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("paidDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(saleDate))));
			exprList.add(EntityCondition.makeCondition("pmPaymentDate", EntityOperator.LESS_THAN, dayBegin));
			EntityCondition	cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);		
			try{			
				pendingPaymentsList = delegator.findList("InvoiceAndApplAndPayment", cond, null ,null, null, false);
			}catch(GenericEntityException e){
				Debug.logError(e, module);	
				return ServiceUtil.returnError(e.toString());
			}		
			Set paymentSet = new HashSet(EntityUtil.getFieldListFromEntityList(pendingPaymentsList, "paymentId", false));
			for( GenericValue pendingPayments : pendingPaymentsList){
				/*count=count+1;
					Debug.log("i======"+count);
					Debug.log("pendingPayments======"+pendingPayments);
					Debug.log("======***********============");
					Debug.log("dueDate======"+pendingPayments.getTimestamp("dueDate"));
					Debug.log("pmPaymentDate======"+pendingPayments.getTimestamp("pmPaymentDate"));
					Debug.log("paidDate======"+pendingPayments.getTimestamp("paidDate"));
					Debug.log("paymentapplicationId======"+pendingPayments.getString("paymentApplicationId"));*/
					if(UtilValidate.isEmpty(pendingPayments.getTimestamp("paidDate")) || (UtilDateTime.getDayStart(pendingPayments.getTimestamp("paidDate"))).equals(UtilDateTime.getDayStart(pendingPayments.getTimestamp("invoiceDate")))
																					  || (pendingPayments.getTimestamp("paidDate")).compareTo(dayBegin) >=0 ){
						advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amountApplied"));
					}
					
				
			}
			
			List paymentList = new ArrayList(paymentSet);
			for(int i =0 ; i< paymentList.size(); i++){
				try{				
					Map result = dispatcher.runSync("getPaymentNotApplied", UtilMisc.toMap("userLogin",userLogin,"paymentId",(String)paymentList.get(i)));
					advancePaymentAmount = advancePaymentAmount.add((BigDecimal)result.get("unAppliedAmountTotal"));
				}catch(GenericServiceException e){
					Debug.logError(e, module);	
					return ServiceUtil.returnError(e.toString());
				}
				
			}		
			//here get the all the zero application paymentId's
			List<String> zeroAppPaymentIds = EntityUtil.getFieldListFromEntityList(pendingPaymentsList, "paymentId", true);		
			// Next get payments that were made before opening balance date and have zero applications
			exprList.clear();
			exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds));
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentApplicationId", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition(EntityCondition.makeCondition("isFullyApplied", EntityOperator.EQUALS, null),EntityOperator.OR ,EntityCondition.makeCondition("isFullyApplied", EntityOperator.EQUALS, "N"))));
			exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED","PMNT_NOT_PAID")));
			exprList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN, dayBegin));
			// exculde all the zero payment application payments
			if(UtilValidate.isNotEmpty(zeroAppPaymentIds)){
				
				exprList.add(EntityCondition.makeCondition("paymentId", EntityOperator.NOT_IN, zeroAppPaymentIds));
			}
			
			
			EntityCondition	paymentCond = EntityCondition.makeCondition(exprList, EntityOperator.AND);	
			try{	
				 EntityFindOptions findOption = new EntityFindOptions();
		         findOption.setDistinct(true);
				pendingPaymentsList = delegator.findList("PaymentAndApplicationLftJoin", paymentCond, UtilMisc.toSet("paymentId") ,null, findOption, false);	
				for( GenericValue pendingPayments : pendingPaymentsList){
					Map result = dispatcher.runSync("getPaymentNotApplied", UtilMisc.toMap("userLogin",userLogin,"paymentId",pendingPayments.getString("paymentId")));
					advancePaymentAmount = advancePaymentAmount.add((BigDecimal)result.get("unAppliedAmountTotal"));
					//advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amount"));
				}
			}catch(Exception e){
				Debug.logError(e, module);	
				return ServiceUtil.returnError(e.toString());
			}
			openingBalance = invoicePendingAmount.subtract(advancePaymentAmount);
			openingBalanceMap.put("openingBalance", openingBalance);				
			return openingBalanceMap;
		}
	 	
	 	public static Map populatePartyAccountingHistory(DispatchContext dctx, Map<String, ? extends Object> context){
	    	Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    String customTimePeriodId = (String) context.get("customTimePeriodId");
		    Timestamp fromDateTime = null;
		    Timestamp thruDateTime = null;
		    List conditionList = FastList.newInstance();
		    String periodTypeId = "";
		    Timestamp prevFromDate = null;
			Timestamp prevThruDate = null;
			Timestamp checkDate = null;
			String organizationPartyId = "";
			String previousCustomTimePeriodId = "";
			Map result = ServiceUtil.returnSuccess();
		    try{
	 	    	GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
	 	    	if(UtilValidate.isNotEmpty(customTimePeriod)){
	 	    		periodTypeId = customTimePeriod.getString("periodTypeId");
	 	    		organizationPartyId = customTimePeriod.getString("organizationPartyId");
	 	    		fromDateTime = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
	 	 			thruDateTime = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
	 	    	}
	 	    	else{
	 	    		Debug.logError("Custom time period doesnot exists", module);    			
		 		    return ServiceUtil.returnError("Custom time period doesnot exists");
	 	    	}
	 	    	checkDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(fromDateTime, -1));
	 	    	result = dispatcher.runSync("findLastClosedDate", UtilMisc.toMap("organizationPartyId", organizationPartyId, "onlyFiscalPeriods", Boolean.FALSE, "periodTypeId", periodTypeId,"userLogin", userLogin));
	 	    	if(ServiceUtil.isError(result)){
		 	    	Debug.logError("Error in service findLastClosedDate ", module);    			
		 		    return ServiceUtil.returnError("Error in service findLastClosedDate");
		 	    }
	 	    	previousCustomTimePeriodId = ((GenericValue)result.get("lastClosedTimePeriod")).getString("customTimePeriodId");
	 	    	if(UtilValidate.isNotEmpty(previousCustomTimePeriodId)){
	 	    		GenericValue previousCustomPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", previousCustomTimePeriodId), false);
	 	    		prevFromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(previousCustomPeriod.getDate("fromDate")));
	 				prevThruDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(previousCustomPeriod.getDate("thruDate")));
	 				if(!(checkDate.compareTo(prevThruDate)==0)){
	 			   		Debug.logError("Previous custom time period is not yet closed ", module);    			
	 	 		        return ServiceUtil.returnError("Previous custom time period is not yet closed ");
	 				}
	 	    	}
		    	
	 	    }catch(GenericEntityException e){		   
		        Debug.logError("Error while fetching custom time period Id"+e.getMessage(),module);
		        return ServiceUtil.returnError("Error while fetching custom time period Id");
	    	}catch(GenericServiceException e){
	    		Debug.logError("Error calling service findLastClosedDate"+e.getMessage(),module);
	    		return ServiceUtil.returnError("Error in service findLastClosedDate");
	    	}
	 	    
	 	    List<GenericValue> facilityDetails = (List<GenericValue>)getAllBooths(delegator).get("boothsDetailsList");
	 	    
	 	    try{
	 	    	String boothId = "";
	 	 	    String ownerPartyId = "";
	 	 	    for(GenericValue facility : facilityDetails){
	 	 	    	boothId = facility.getString("facilityId");
	 	 	    	ownerPartyId = facility.getString("ownerPartyId");
	 	 	    	BigDecimal OB = BigDecimal.ZERO;
	 	 	    	GenericValue partyHistory = delegator.findOne("PartyAccountingHistory", UtilMisc.toMap("customTimePeriodId", previousCustomTimePeriodId, "partyId", ownerPartyId, "facilityId", boothId), false);
		 	 	 	if(UtilValidate.isNotEmpty(partyHistory)) {   	
	 	 	    		OB = partyHistory.getBigDecimal("endingBalance");
		 	 	 	}
	 	 	    	else
	 	 	    	{
		 	 	    	result = (Map)getOpeningBalanceForBooth(dctx, UtilMisc.toMap("userLogin", userLogin, "saleDate", fromDateTime, "facilityId", boothId));
		 	 	    	if(ServiceUtil.isError(result)){
		 	 	    		Debug.logError("Error in service getOpeningBalanceForBooth ", module);    			
		 	 		        return ServiceUtil.returnError("Error in service getOpeningBalanceForBooth");
		 	 	    	}
		 	 	    	OB = (BigDecimal)result.get("openingBalance");
		 	 	    }
	 	 	    	List boothIds = FastList.newInstance();
	 	 	    	boothIds.add(boothId);
	 	 	    	result = (Map)getPeriodTotals(dctx, UtilMisc.toMap("userLogin", userLogin, "facilityIds", boothIds, "fromDate", fromDateTime, "thruDate", thruDateTime));
	 	 	    	if(ServiceUtil.isError(result)){
	 	 	    		Debug.logError("Error in service getPeriodTotals ", module);    			
	 		            return ServiceUtil.returnError("Error in service getPeriodTotals");
	 	 	    	}
	 	 	    	BigDecimal totalSale = (BigDecimal)result.get("totalRevenue");
	 	 	    	result = (Map)getBoothPaidPayments(dctx, UtilMisc.toMap("fromDate", fromDateTime ,"thruDate", thruDateTime, "facilityId" , boothId));
	 	 			
	 	 			if(ServiceUtil.isError(result)){
	 	 	    		Debug.logError("Error in service getBoothPaidPayments ", module);    			
	 		            return ServiceUtil.returnError("Error in service getBoothPaidPayments");
	 	 	    	}

	 	 			BigDecimal paymentTotal = (BigDecimal)result.get("invoicesTotalAmount");
	 	 			BigDecimal endingBal = totalSale.subtract(paymentTotal);
	 	 	    	endingBal = endingBal.add(OB);
	 	 	    	GenericValue acctgHistory = delegator.makeValue("PartyAccountingHistory");
	 	 	    	acctgHistory.put("customTimePeriodId", customTimePeriodId);
	 	 	    	acctgHistory.put("partyId", ownerPartyId);
	 	 	    	acctgHistory.put("facilityId", boothId);
	 	 	    	acctgHistory.put("debits", totalSale);
	 	 	    	acctgHistory.put("credits", paymentTotal);
	 	 	    	acctgHistory.put("endingBalance", endingBal);
	 	 	    	delegator.createOrStore(acctgHistory);  
	 	 	    }
	 	    }catch(Exception e){
	 	    	Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());	
	 	    }
	 	    result = ServiceUtil.returnSuccess("Successfully populated parties accouting history for the period");
	 	    return result;
		}
	 	
	 	public static Map<String, Object> getBoothOrderDetails(DispatchContext dctx, Map<String, ? extends Object> context){
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String supDate = (String) context.get("supplyDate"); 
	        String boothId = (String) context.get("boothId");
	        String routeId = (String) context.get("routeId");
	        String tripId = (String) context.get("tripId");
	        String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
	        String subscriptionTypeId = (String) context.get("subscriptionTypeId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map result = ServiceUtil.returnSuccess(); 
	        Timestamp supplyDate = null;
	        Timestamp dayBegin = null;
	        Timestamp dayEnd = null;
	        List changeIndentProductList = FastList.newInstance();
	        
	        try{
	        	if (UtilValidate.isNotEmpty(supDate)) { 
	    			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
	    			try {
	    				supplyDate = new java.sql.Timestamp(sdf.parse(supDate).getTime());
	    			} catch (ParseException e) {
	    				Debug.logError("Cannot parse date string: "+supDate, module);
		    			return ServiceUtil.returnError("Cannot parse date string"); 
	    			} catch (NullPointerException e) {
	    				Debug.logError("Cannot parse date string: "+supDate, module);
		    			return ServiceUtil.returnError("Cannot parse date string"); 
	    			}
	    		}
	        	dayBegin = UtilDateTime.getDayStart(supplyDate);
	        	dayEnd = UtilDateTime.getDayEnd(supplyDate);
	        }
	        catch(Exception e){
	        	Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(e.toString());
	        }
	         
	        
	        Map<String ,BigDecimal> changeQuantityMap =FastMap.newInstance();
	        Map<String ,BigDecimal> prevQuantityMap =FastMap.newInstance();
	        Map prodIndentQtyCat = FastMap.newInstance();
	        Map qtyInPieces = FastMap.newInstance();
	        List productList = FastList.newInstance();
	        List conditionList = FastList.newInstance();
	        String productStoreId = (String)(ByProductServices.getByprodFactoryStore(delegator)).get("factoryStoreId");
	        String partyId = "";
	        String facilityCategory = "";
	    	try{
	    		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), true);
	    		
	    		if(UtilValidate.isEmpty(facility)){
	    			Debug.logError("Invalid  Booth Id", module);
	    			return ServiceUtil.returnError("No dealer exists with ["+boothId+"]");    				
	    		}
	    		boolean isActive = EntityUtil.isValueActive(facility , dayBegin, "openedDate", "closedDate");
	    		if(!isActive){
	    			Debug.logError("Is not active facility "+boothId, module);    			
	    			return ServiceUtil.returnError("The  dealer ' "+ facility.getString("facilityName")+"' is not Active."); 
	    		}
	    		partyId = facility.getString("ownerPartyId");
	    		facilityCategory = facility.getString("categoryTypeEnum");
	    		//lets override productSubscriptionTypeId based on facility category
	    		if(facility.getString("categoryTypeEnum").equals("SO_INST")){
	    			productSubscriptionTypeId = "SPECIAL_ORDER";
	    		}else if(facility.getString("categoryTypeEnum").equals("CR_INST")){
	    			 productSubscriptionTypeId = "CREDIT";
	    		}else{
	    			  productSubscriptionTypeId = "CASH";
	    		}
	    		BigDecimal securityDeposit = BigDecimal.ZERO;  
	    		if(UtilValidate.isNotEmpty(facility.get("securityDeposit"))){
	    			securityDeposit = facility.getBigDecimal("securityDeposit");
	    		}
	    		result.put("securityDeposit", securityDeposit);
	    		result.put("boothName", facility.getString("facilityName"));
	    		
	    		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	    		conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, subscriptionTypeId+"_SHIPMENT"));
	    		conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.EQUALS, dayBegin));
	    		conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
	    		if(UtilValidate.isNotEmpty(tripId)){
	    			conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
	    		}
	    		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		List<GenericValue> shipments = delegator.findList("Shipment", condition, UtilMisc.toSet("shipmentId"), null, null, false);
	    		if(UtilValidate.isEmpty(shipments)){
	    			Debug.logError("No orders found for the given shipment details "+boothId, module);    			
	    			return ServiceUtil.returnError("No orders found for the given shipment details "+boothId); 
	    		}
	    		String shipmentId = (EntityUtil.getFirst(shipments)).getString("shipmentId");
	    		conditionList.clear();
	    	    conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS , "ORDER_APPROVED"));
	    	    conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS , shipmentId));
	    	    conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS , boothId));
	    	    EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    	    List<GenericValue> orderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", cond, null, null, null, false);
	    	    if(UtilValidate.isEmpty(orderItems)){
	    			Debug.logError("No orders found for the dealer "+boothId, module);    			
	    			return ServiceUtil.returnError("No orders found for the dealer "+boothId); 
	    		}
	    	    List productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
	    	    List<GenericValue> products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	    	    if(UtilValidate.isEmpty(routeId)){
	    			Map boothDetails = (Map)(getBoothRoute(dctx, context)).get("boothDetails");
	    			routeId = (String)boothDetails.get("routeId");
	    		}    		
	    		
	    	    result.put("routeId", routeId);
	    	    for(GenericValue orderItem: orderItems){
		        	Map tempChangeProdMap = FastMap.newInstance();
		        	String productId = orderItem.getString("productId");
		        	String prodName = (EntityUtil.getFirst(EntityUtil.filterByAnd(products, UtilMisc.toMap("productId",productId)))).getString("description");
		        	BigDecimal quantity = orderItem.getBigDecimal("quantity");
		        	tempChangeProdMap.put("id",productId);
		            tempChangeProdMap.put("cProductId", productId);
		            tempChangeProdMap.put("cProductName", prodName);
		            tempChangeProdMap.put("cQuantity",quantity);
	            	changeIndentProductList.add(tempChangeProdMap);
		        }
	    	}catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}
	    	
	    	
	    	// lets populate route totals
	    	try{
	    		
	    		Map prodPriceMap = FastMap.newInstance();
	    		Map inputProductRate = FastMap.newInstance();
	    		inputProductRate.put("productStoreId", productStoreId);
	    		inputProductRate.put("fromDate",dayBegin);
	    		inputProductRate.put("facilityId",boothId);
	    		inputProductRate.put("partyId",partyId);
	    		inputProductRate.put("facilityCategory",facilityCategory);
	    		inputProductRate.put("userLogin",userLogin);
	    		Map priceResultMap = getProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
	    		prodPriceMap = (Map)priceResultMap.get("priceMap");
	    		result.put("productPrice", prodPriceMap);
	    	
	    	}catch (Exception e) {
	    		Debug.logError(e, e.getMessage());
			}
			result.put("changeIndentProductList", changeIndentProductList);
			return result;
	    }
	 
	 	public static Map<String, Object> getRouteIssuanceDetails(DispatchContext dctx, Map<String, ? extends Object> context){
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String supplyDate = (String) context.get("supplyDate"); 
	        String routeId = (String) context.get("routeId");
	        String tripId = (String) context.get("tripId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map result = ServiceUtil.returnSuccess(); 
	        List routeProductList = FastList.newInstance();
	        List conditionList = FastList.newInstance();
	        String productStoreId = (String)(ByProductServices.getByprodFactoryStore(delegator)).get("factoryStoreId");
	        Timestamp issueDate = null;
	        try{
	        	
	        	if (UtilValidate.isNotEmpty(supplyDate)) { 
	    			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
	    			try {
	    				issueDate = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
	    			} catch (ParseException e) {
	    				Debug.logError("Cannot parse date string: "+supplyDate, module);
		    			return ServiceUtil.returnError("Cannot parse date string"); 
	    			} catch (NullPointerException e) {
	    				Debug.logError("Cannot parse date string: "+supplyDate, module);
		    			return ServiceUtil.returnError("Cannot parse date string"); 
	    			}
	    		}
	        	issueDate = UtilDateTime.getDayStart(issueDate);
	        	GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",routeId), true);
	    		if(UtilValidate.isEmpty(facility)){
	    			Debug.logError("Invalid  Route Id", module);
	    			return ServiceUtil.returnError("Invalid  Route Id");    				
	    		}
	    		boolean isActive = EntityUtil.isValueActive(facility , issueDate, "openedDate", "closedDate");
	    		if(!isActive){
	    			Debug.logError("is not active facility "+routeId, module);    			
	    			return ServiceUtil.returnError("The  facility ' "+ routeId+"' is not Active."); 
	    		}
	    		List<GenericValue> products = delegator.findList("Product", null, null, null, null, false);
	    		result.put("routeName", facility.getString("description"));
	    		result.put("routeId", routeId);
	        	conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
	        	if(UtilValidate.isNotEmpty(tripId)){
	        		conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
	        	}
	        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	        	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, issueDate));
	        	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(issueDate)));
	        	EntityCondition shipCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	        	List<GenericValue> shipment = delegator.findList("Shipment", shipCond, null, null, null , false);
	        	String shipmentId = "";
	        	if(UtilValidate.isNotEmpty(shipment)){
	        		shipmentId = (String)((GenericValue)EntityUtil.getFirst(shipment)).get("shipmentId");
	        	}
	        	else{
	        		Debug.logError("No shipment found for the Route"+ routeId+" for the given date"+issueDate, module);    			
	    			return ServiceUtil.returnError("No shipment found for the Route and Trip ["+ routeId+" - "+tripId+"] for the given date"+issueDate); 
	        	}
	        	List<GenericValue> itemIssuance = delegator.findList("ItemIssuance", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false); 
	        	for(GenericValue itemIssue : itemIssuance){
		        	Map tempProdMap = FastMap.newInstance();
		        	String productId = itemIssue.getString("productId");
		        	GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)));
		        	BigDecimal quantity = itemIssue.getBigDecimal("quantity");
		        	if(quantity.compareTo(BigDecimal.ZERO)>0){
		        		tempProdMap.put("id",productId);
			        	tempProdMap.put("cProductId", productId);
			        	tempProdMap.put("cProductName", product.getString("description"));
			        	tempProdMap.put("cQuantity", quantity);
			        	routeProductList.add(tempProdMap);
		        	}
		        }
	    	}catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}
	    	
			result.put("routeProductList", routeProductList);
			return result;
	    }
	 	
	 	public static Map<String, Object> getOrderReturnItems(DispatchContext dctx, Map<String, ? extends Object> context){
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String supplyDateStr = (String) context.get("supplyDate"); 
	        String boothId = (String) context.get("boothId");
	        String routeId = (String) context.get("routeId");
	        String tripId = (String) context.get("tripId");
	        String returnHeaderTypeId = (String) context.get("returnHeaderTypeId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map result = ServiceUtil.returnSuccess(); 
	        List conditionList = FastList.newInstance();
	        Timestamp supplyDate = null;
	    	try{
	    		if (UtilValidate.isNotEmpty(supplyDateStr)) { 
	    			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
	    			try {
	    				supplyDate = new java.sql.Timestamp(sdf.parse(supplyDateStr).getTime());
	    			} catch (ParseException e) {
	    				Debug.logError("Cannot parse date string: "+supplyDateStr, module);
		    			return ServiceUtil.returnError("Cannot parse date string"); 
	    			} catch (NullPointerException e) {
	    				Debug.logError("Cannot parse date string: "+supplyDateStr, module);
		    			return ServiceUtil.returnError("Cannot parse date string"); 
	    			}
	    		}
	    		if(UtilValidate.isEmpty(returnHeaderTypeId)){
	    			Debug.logError("Return type is empty", module);
	    			return ServiceUtil.returnError("Return type is empty");
	    		}
	    		if(UtilValidate.isEmpty(supplyDate)){
	    			Debug.logError("supply date is empty", module);
	    			return ServiceUtil.returnError("supply date is empty");
	    		}
	    		Timestamp dayStart =UtilDateTime.getDayStart(supplyDate);
	    		Timestamp dayEnd =UtilDateTime.getDayEnd(supplyDate);
	    		List orderReturnList = FastList.newInstance();
	    		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
    			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
    			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
    			conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
    			conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
    			EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			List<GenericValue> shipment = delegator.findList("Shipment", cond, UtilMisc.toSet("shipmentId"), null, null, false);
    			String shipmentId = "";
    			if(UtilValidate.isNotEmpty(shipment)){
    				shipmentId = ((GenericValue)EntityUtil.getFirst(shipment)).getString("shipmentId");
    			}
    			conditionList.clear();
    			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
    			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("RETURN_CANCELLED")));
    			EntityCondition expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			List<GenericValue> returnHeader = delegator.findList("ReturnHeader", expr, null, null, null, false);
    			if(UtilValidate.isNotEmpty(returnHeader)){
    				String returnId = ((GenericValue)EntityUtil.getFirst(returnHeader)).getString("returnId");
    				Debug.logError("One return allowed for one shipment, Cancel return :"+returnId+" and re-enter the return" , module);
	    			return ServiceUtil.returnError("One return allowed for one shipment, Cancel return :"+returnId+" and re-enter the return");
    			}
    				
    			Map orderedQty = FastMap.newInstance();
    			conditionList.clear();
    			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
    			conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, boothId));
    			conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED", "ORDER_REJECTED")));
    			EntityCondition orderCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			List<GenericValue> orderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCond, UtilMisc.toSet("productId", "quantity", "productName"), null, null, false);

    			Map productNames = FastMap.newInstance();
    			String productId = "";
    			BigDecimal quantity = BigDecimal.ZERO;
    			for(GenericValue orderItem : orderItems){
    				productId = orderItem.getString("productId");
    				quantity = orderItem.getBigDecimal("quantity");
    				if(orderedQty.containsKey(productId)){
    					BigDecimal tempQty = (BigDecimal)orderedQty.get(productId);
    					tempQty = tempQty.add(quantity);
    					orderedQty.put(productId, tempQty);
    				}
    				else{
    					orderedQty.put(productId, quantity);
    				}
    				productNames.put(productId, orderItem.getString("productName"));
    			}
    			Iterator orderItemIter = orderedQty.entrySet().iterator();
				while (orderItemIter.hasNext()) {
					Map.Entry orderItemEntry = (Entry) orderItemIter.next();
					Map tempMap = FastMap.newInstance();
		        	productId = (String)orderItemEntry.getKey();
		        	BigDecimal qty = (BigDecimal)orderItemEntry.getValue();
		        	tempMap.put("cProductId", productId);
		        	tempMap.put("cProductName", productNames.get(productId));
		        	tempMap.put("cQuantity", qty);
		        	tempMap.put("returnQuantity", "");
		        	orderReturnList.add(tempMap);
				}
				result.put("orderReturnList", orderReturnList);
	    	}catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}
			return result;
	    }
	 	public static Map<String, Object> finalizeOrders(DispatchContext dctx, Map<String, ? extends Object> context){
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String estimatedShipDateStr = (String)context.get("estimatedShipDate");	
	        String shipmentId = (String) context.get("shipmentId"); 
	        String shipmentTypeId = (String) context.get("shipmentTypeId"); 
	        
	        String routeId = (String) context.get("routeId");
	        String tripId = (String) context.get("tripId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Timestamp estimatedShipDate =null;	
	   
	        estimatedShipDate=Timestamp.valueOf(estimatedShipDateStr);
	        Map result = ServiceUtil.returnSuccess("Successfully created Invoices  For Selected Shipment!");
	        Map resultMap = FastMap.newInstance();
	        List<String> shipmentIds = FastList.newInstance();
	        List<String> routeIdsList = FastList.newInstance();
	        List conditionList = FastList.newInstance();
	    	Map	productTotals =  FastMap.newInstance();
	    	Map	issuanceProductTotals =  FastMap.newInstance();
	    	List<String> boothOrderIdsList=FastList.newInstance();
	    	Map resultCompareMap=FastMap.newInstance();
	    	 boolean isComparsionFaild=false;
           try{
	        if(UtilValidate.isNotEmpty(shipmentId)){
	        	if(shipmentId.equals("allRoutes")){
		        	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, estimatedShipDate));
		        	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(estimatedShipDate)));
		        	conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, shipmentTypeId));
		        	EntityCondition shipCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		        	List<GenericValue> shipments = delegator.findList("Shipment", shipCond, null, null, null , false);
		        	List<String> shipIds = EntityUtil.getFieldListFromEntityList(shipments, "shipmentId", false);
	        		shipmentIds.addAll(shipIds);
	        		//routeIdsList.addAll(EntityUtil.getFieldListFromEntityList(shipments, "routeId", false));
	        	}else{
	        		 GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId",shipmentId) , false);
	        		shipmentIds.add((String)shipment.get("shipmentId"));
	        		//routeIdsList.add(shipment.get("shipmentId"));
	        	}
	        	
	        }
	    	if(UtilValidate.isNotEmpty(shipmentIds)){
	    	Timestamp	dayBegin = UtilDateTime.getDayStart(estimatedShipDate);
	    	Timestamp dayEnd = UtilDateTime.getDayEnd(estimatedShipDate);
	    	Map inputMap=FastMap.newInstance();
	    	inputMap.put("shipmentIds",shipmentIds);
	    	inputMap.put("fromDate",dayBegin);
	    	inputMap.put("thruDate",dayEnd);
	    	//if success method will return false otherwise true
	    	 resultCompareMap =compareOrdersAndItemIssuence(dctx,inputMap);
	    	 String isFailedStr=(String) resultCompareMap.get("isFailed");
	    		Debug.log("==isComparsionFaild==:"+isComparsionFaild+"==for ShipmentId===="+shipmentIds+"==resultcomapreMap=="+resultCompareMap);
	    	if(isFailedStr.equals("Y")){
	    		 isComparsionFaild=true;
	    		 Debug.logError("==for ShipmentId===="+shipmentIds+"==resultcomapreMap=="+resultCompareMap, module);
	    		  return ServiceUtil.returnError("Failed to create invoice..!"+resultCompareMap.get("failedProductItemsMap"));
	    	}
	    
	    	if(!isComparsionFaild){
	    	   conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
				conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(dayBegin)));
				conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(dayEnd)));
				if (UtilValidate.isNotEmpty(shipmentIds)) {
					conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
				}
				// conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, shipmentTypeId));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List shipmentOrderItemsList = delegator.findList("OrderHeader", condition, null, null, null, false);
				Set orderIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(shipmentOrderItemsList, "orderId", true));
				boothOrderIdsList = new ArrayList(orderIdsSet);
				
				for (Object orderId : boothOrderIdsList) {
		            try{        
		        		resultMap = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", orderId,"userLogin", userLogin));
		        		if (ServiceUtil.isError(resultMap)) {
		                    Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(resultMap), module);
		            		return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(resultMap));          	            
		                } 
			        	/*Map<String, Object> invoiceCtx = UtilMisc.<String, Object>toMap("invoiceId", resultMap.get("invoiceId"));
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
			             }      */  		
		        		// apply invoice if any adavance payments from this  party
						  			            
						Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", (String)resultMap.get("invoiceId"),"userLogin", userLogin));
						if (ServiceUtil.isError(resultPaymentApp)) {						  
			        	   Debug.logError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp), module);			             
			               return ServiceUtil.returnError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp));  
				        }				           
				          
		            }catch (Exception e) {
		                Debug.logError(e, module);
		            } 
		            resultMap.put("orderId", orderId);
		        
				}//end of OrderIteration
	    	   }//if close
	 	      }
           }catch (Exception e) {
        	   Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(e.toString());
           }
       	//result.put("routeProductList", routeProductList);
           
		return result;
	 	}
		public static Map<String, Object>  compareOrdersAndItemIssuence(DispatchContext dctx, Map<String, ? extends Object> context){
		     Delegator delegator = dctx.getDelegator();
		        LocalDispatcher dispatcher = dctx.getDispatcher();
		        List<String> shipmentIds =(List<String>) context.get("shipmentIds");	
		        Timestamp fromDate = (Timestamp)context.get("fromDate");
		        Timestamp  thruDate = (Timestamp)context.get("thruDate");
		        Map result = ServiceUtil.returnSuccess(); 
		        Map resultMap = FastMap.newInstance();
			    List conditionList = FastList.newInstance();
		    	Map	productTotals =  FastMap.newInstance();
		    	Map	issuanceProductTotals =  FastMap.newInstance();
		    	Map	failedProductItemsMap =  FastMap.newInstance();
		    	boolean isComparisonFailed=  false;
		    	try{
	    			 List boothsList =null; //= NetworkServices.getRouteBooths(delegator , (String)shipmentObj.get("routeId"));
				    Map boothWiseMap = FastMap.newInstance();
				    Map 	dayTotals = getPeriodTotals(dctx,UtilMisc.toMap("shipmentIds",shipmentIds, "facilityIds",boothsList,"fromDate",fromDate, "thruDate",thruDate));
					if(UtilValidate.isNotEmpty(dayTotals)){
					productTotals = (Map)dayTotals.get("productTotals");		
	 	            }
		    	//get Issueance Details
		    	conditionList.clear();
		    	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	        	EntityCondition itemIssueanceCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	        	List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", itemIssueanceCond, null, null, null , false);
	        	//List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", itemIssueanceCond, null, UtilMisc.toList("productId"), false);
	        	for(GenericValue itemIssueance : itemIssuanceList){
	        		BigDecimal quantity = itemIssueance.getBigDecimal("quantity");
	        		String issueProductId = itemIssueance.getString("productId");
		        	if(quantity.compareTo(BigDecimal.ZERO)>0){
		        		BigDecimal tempQuantity = BigDecimal.ZERO;
		        		if(UtilValidate.isEmpty(issuanceProductTotals.get(issueProductId))){
		        			//issuanceProductTotals.put(issueProductId,quantity);
		        			tempQuantity =quantity;
		        		}else{
		        			tempQuantity=tempQuantity.add((BigDecimal)issuanceProductTotals.get(issueProductId));
		        			
		        		}
		        		issuanceProductTotals.put(issueProductId,tempQuantity);
		        	}
		 	    }//for close
	        	//comparison of issuance and ProdTotals 
	        
	        	Iterator prodIter = productTotals.entrySet().iterator();
				while (prodIter.hasNext()) {
					Map.Entry entry =(Map.Entry) prodIter.next();
					  String productId =(String)entry.getKey();
					  Map prodTotalMap= (Map)productTotals.get(productId);
					  BigDecimal prodQty= (BigDecimal)prodTotalMap.get("total");	
					  GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId",productId) , false);
					  BigDecimal pktTotal=(new BigDecimal(1)).divide((BigDecimal)product.get("quantityIncluded"));
					  BigDecimal actQtyTotal= prodQty.multiply(pktTotal);
					  BigDecimal itemIssuQty= (BigDecimal) issuanceProductTotals.get(productId);
					 if(UtilValidate.isNotEmpty(itemIssuQty)){
						 if(actQtyTotal.compareTo(itemIssuQty)!=0){
							 Map itemInnerMap=FastMap.newInstance();
							itemInnerMap.put("ordrQty",actQtyTotal);
							itemInnerMap.put("issuanceQty",itemIssuQty);
							failedProductItemsMap.put(productId,itemInnerMap);
							isComparisonFailed=true;
		 	             }
				      }
				}//end of while
		    	}catch(Exception e) {
		    		  Debug.logError(e.toString(), module);
						return ServiceUtil.returnError(e.toString());
		    		
		    	}
		    	resultMap.put("failedProductItemsMap",failedProductItemsMap);
		    	if(isComparisonFailed){// true for failure
		    		resultMap.put("isFailed","Y");
		    	}else{
		    		resultMap.put("isFailed","N");
		    	}
				 return resultMap;
		}
		
		
//NetworkServices Methods     
		   /**
	     * Helper method to get booth details for given boothId
	     * The foll. details will be returned in a map: boothId, boothName, vendorName, routeName, zoneName, distributorName
	     * @param ctx the dispatch context
	     * @param context 
	     * @return boothDetail map
	     */
	    public static List getBoothList(Delegator delegator ,String facilityId){
	   	 	List boothList = FastList.newInstance();
		   	try{
					GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);				
					if(facilityDetail == null || (!facilityDetail.getString("facilityTypeId").equals("ZONE") &&  !facilityDetail.getString("facilityTypeId").equals("ROUTE") && !facilityDetail.getString("facilityTypeId").equals("BOOTH")) ){
						Debug.logInfo("facilityId '"+facilityId+ "'is not a Booth or Zone ", "");
						boothList = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("Facility", UtilMisc.toMap("facilityTypeId", "BOOTH")), "facilityId", true);
						return boothList;
					}
					if(facilityDetail.getString("facilityTypeId").equals("ZONE")){					
						boothList = getZoneBooths(delegator,facilityId);
					}else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")){
						boothList = getRouteBooths(delegator,facilityId);
					}else{
						boothList.add(facilityId);
					}
					
				}catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
				}		
			return boothList;
	   }   
	    
	    
	    public static Map<String, Object> getBoothDetails(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
		    LocalDispatcher dispatcher = ctx.getDispatcher();    	
			String boothId = (String) context.get("boothId"); 
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");		
	        Map<String, Object> result = FastMap.newInstance(); 
	        GenericValue boothFacility;
	        GenericValue routeFacility;
	        GenericValue zoneFacility;  
	        GenericValue distributorFacility; 
	        String vendorName = "";
	        String vendorPhone = "";
	        try {
	        	boothFacility = delegator.findOne("Facility",true, UtilMisc.toMap("facilityId", boothId));
	        	if (boothFacility == null) {
	                Debug.logError("Invalid boothId " + boothId, module);
	                return ServiceUtil.returnError("Invalid boothId " + boothId);         		
	        	}
	        	vendorName = PartyHelper.getPartyName(delegator, boothFacility.getString("ownerPartyId"), false);
	            Map<String, Object> getTelParams = FastMap.newInstance();
	        	getTelParams.put("partyId", boothFacility.getString("ownerPartyId"));
	            getTelParams.put("userLogin", userLogin); 
	            Map<String, Object> serviceResult= dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isSuccess(serviceResult)) {
	                vendorPhone = (String) serviceResult.get("contactNumber");            
	            } 
	            String contactNumberTo = (String) serviceResult.get("countryCode") + (String) serviceResult.get("contactNumber");            

//	        	routeFacility = boothFacility.getRelatedOneCache("ParentFacility");
//	        	zoneFacility = routeFacility.getRelatedOneCache("ParentFacility");
//	        	distributorFacility = zoneFacility.getRelatedOneCache("ParentFacility");
	        }
	        catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());          	
	        }    
	    	catch (Exception e) {
	    		Debug.logError(e, "Problem getting booth details", module);
	    		return ServiceUtil.returnError(e.getMessage());
	    	}        
	        Map<String, Object> boothDetails = FastMap.newInstance();
	        boothDetails.put("boothId", boothId);
	        boothDetails.put("boothName", boothFacility.getString("facilityName"));
	        boothDetails.put("categoryTypeEnum", boothFacility.getString("categoryTypeEnum"));
	        boothDetails.put("vendorName", vendorName);
	        boothDetails.put("vendorPhone", vendorPhone);
	        
//	        boothDetails.put("routeName", routeFacility.getString("facilityName"));
//	        boothDetails.put("routeId", routeFacility.getString("facilityId"));
//	        boothDetails.put("zoneName", zoneFacility.getString("facilityName"));
//	        boothDetails.put("zoneId", zoneFacility.getString("facilityId"));
//	        boothDetails.put("isUpcountry", zoneFacility.getString("isUpcountry"));
//	        boothDetails.put("distributorName", distributorFacility.getString("facilityName"));
//	        boothDetails.put("distributorId", distributorFacility.getString("facilityId"));
	        result.put("boothDetails", boothDetails);
	        return result;
	    }
		
	  public static Map<String, Object> getBoothRoute(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			String boothId = (String) context.get("boothId");
			String supplyTime = (String) context.get("subscriptionTypeId");
			Timestamp supplyDate = (Timestamp) context.get("supplyDate");
			if(UtilValidate.isEmpty(supplyDate)){
				supplyDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			}
	        Map<String, Object> result = FastMap.newInstance(); 
	        GenericValue boothFacility;
	        GenericValue routeFacility;
	        GenericValue zoneFacility;  
	        GenericValue distributorFacility; 
	        String vendorName;
	        try {
	        	boothFacility = delegator.findOne("Facility",true, UtilMisc.toMap("facilityId", boothId));
	        	if (boothFacility == null) {
	                Debug.logError("Invalid boothId " + boothId, module);
	                return ServiceUtil.returnError("Invalid boothId " + boothId);         		
	        	}
	        	List condList = FastList.newInstance();
	        	condList.add(EntityCondition.makeCondition("facilityGroupTypeId", EntityOperator.EQUALS ,"RT_BOOTH_GROUP"));
	        	condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,boothId));
	        	EntityCondition cond = EntityCondition.makeCondition(condList ,EntityOperator.AND);
	        	List<GenericValue> rtGroupMember = delegator.findList("FacilityGroupAndMemberAndFacility", cond, null, null, null, true);
	        	rtGroupMember = EntityUtil.filterByDate(rtGroupMember, supplyDate);
	        	condList.clear();
	        	String supplyTimeFacilityGroupSuffix =  "_RT_GROUP";
	        	String supplyTimeFacilityGroup =  "AM_RT_GROUP";
	        	if(UtilValidate.isNotEmpty(supplyTime)){
	        	  supplyTimeFacilityGroup =  supplyTime+supplyTimeFacilityGroupSuffix;
	        	}
	        	condList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS ,supplyTimeFacilityGroup));
	        	condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN , EntityUtil.getFieldListFromEntityList(rtGroupMember, "ownerFacilityId", true)));
	        	
	        	EntityCondition condGroup = EntityCondition.makeCondition(condList ,EntityOperator.AND);
	        	List<GenericValue> rtGroup = delegator.findList("FacilityGroupAndMemberAndFacility", condGroup, null, null, null, true);
	        	rtGroup = EntityUtil.filterByDate(rtGroup, supplyDate);
	        	routeFacility = EntityUtil.getFirst(rtGroup);
	        
	        }
	        catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());          	
	        } 
	        
	        Map<String, Object> boothDetails = FastMap.newInstance();
	        boothDetails.put("routeId", routeFacility.getString("facilityId"));
	        boothDetails.put("boothId", boothId);
	        result.put("boothDetails", boothDetails);
	        
	        return result;
	    }
		// This will return the list of boothIds for the given zone and (optional) booth category type
		public static List getZoneRoutes(Delegator delegator,String zoneId){
	    	List<String> routeIds = FastList.newInstance();  
	    	try {
	    		List<GenericValue> routes = delegator.findList("Facility", EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, zoneId), null, UtilMisc.toList("facilityId"), null, false);
	            routeIds = EntityUtil.getFieldListFromEntityList(routes, "facilityId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	    	return routeIds;
		}
	    
		// This will return the list of boothIds for the given zone 
		public static List getZoneBooths(Delegator delegator,String zoneId){
	    	return getZoneBooths(delegator, zoneId, null);
		}

		// This will return All boothsList 
		public static Map<String, Object> getAllBooths(Delegator delegator){
		    Map<String, Object> result = FastMap.newInstance(); 
		    List boothsList = FastList.newInstance();
		    List boothsDetailsList = FastList.newInstance();
			try {
				List<GenericValue> booths = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "BOOTH"), null, UtilMisc.toList("facilityId"), null, false);
		        Iterator<GenericValue> boothIter = booths.iterator();
		    	while(boothIter.hasNext()) {
		            GenericValue booth = boothIter.next();
		            boothsList.add(booth.get("facilityId"));
		            boothsDetailsList.add(booth);
		    	}
		    	result.put("boothsList", boothsList);
		    	result.put("boothsDetailsList", boothsDetailsList);
		    	
			} catch (GenericEntityException e) {
		        Debug.logError(e, module);
		    }
			return result;
		}
		
		// This will return the list of boothIds for the given zone and (optional) booth category type
		public static List getZoneBooths(Delegator delegator,String zoneId, String boothCategory){
	    	List<String> boothIds = FastList.newInstance();  

	    	try {
	    		List<GenericValue> routes = delegator.findList("Facility", EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, zoneId), null, UtilMisc.toList("facilityId"), null, false);
	            List routeIds = EntityUtil.getFieldListFromEntityList(routes, "facilityId", false);
	            if (!routeIds.isEmpty()) {
	        		List conditionList= FastList.newInstance();
	    			conditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.IN , routeIds));
	    			if (!UtilValidate.isEmpty(boothCategory)) {
	    				conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS , boothCategory));
	    			}
	    			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	            	
	        		List<GenericValue> booths = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
	                boothIds = EntityUtil.getFieldListFromEntityList(booths, "facilityId", false);            	
	            }
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	    	return boothIds;
		}
		// This will return the list of boothIds for the given zone 
			public static List getRouteBooths(Delegator delegator,String routeId){
		    	return getRouteBooths(delegator, routeId, null);
			}
		// This will return the list of boothIds for the given route and (optional) booth category type
			public static List getRouteBooths(Delegator delegator,String routeId, String boothCategory){
		    	List<String> boothIds = FastList.newInstance();  
		    	boothIds = (List)(getRouteBooths(delegator,UtilMisc.toMap("routeId",routeId,"categoryTypeEnum",boothCategory))).get("boothIdsList");
		    	return boothIds;
			}
			
			// This will return the list of boothIds for the given route and (optional) booth category type
			public static Map getRouteBooths(Delegator delegator, Map<String, ? extends Object>context){
				List<String> boothIds = FastList.newInstance(); 
		    	List<GenericValue> booths = FastList.newInstance();
		    	Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");
		    	String boothCategory = (String)context.get("boothCategory");
		    	String routeId = (String)context.get("routeId");
		    	if(UtilValidate.isEmpty(effectiveDate)){
		    		effectiveDate = UtilDateTime.nowTimestamp();
		    	}
		    	
		    	try {
		    		List condList = FastList.newInstance();
		    		if(UtilValidate.isNotEmpty(boothCategory)){
		    			condList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS ,boothCategory));
		    		}
		    		
		        	condList.add(EntityCondition.makeCondition("facilityGroupTypeId", EntityOperator.EQUALS ,"RT_BOOTH_GROUP"));
		        	condList.add(EntityCondition.makeCondition("ownerFacilityId", EntityOperator.EQUALS ,routeId));
		        	EntityCondition cond = EntityCondition.makeCondition(condList ,EntityOperator.AND);
		        	booths = delegator.findList("FacilityGroupAndMemberAndFacility", cond, null, null, null, true);
		        	 booths = EntityUtil.filterByDate(booths, effectiveDate);   
		        	 boothIds = EntityUtil.getFieldListFromEntityList(booths, "facilityId", true);
		       
		    	} catch (GenericEntityException e) {
		            Debug.logError(e, module);
		        }
		    	Map<String, Object> result = ServiceUtil.returnSuccess(); 
		    	result.put("boothsList", booths);
		        result.put("boothIdsList", boothIds);
		        return result;
			}
			public static Map getBoothsRouteMap(Delegator delegator, Map<String, ? extends Object>context){
				List<String> boothIds = FastList.newInstance(); 
		    	List<GenericValue> booths = FastList.newInstance();
		    	Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");
		    	String boothCategory = (String)context.get("boothCategory");
		    	String facilityId = (String)context.get("facilityId");
		    	Map boothRouteIdsMap =FastMap.newInstance(); 
		    	
		    	if(UtilValidate.isEmpty(effectiveDate)){
		    		effectiveDate = UtilDateTime.nowTimestamp();
		    	}
		    	 String facilityTypeId="";
		    	
		    	try {
		    		List condList = FastList.newInstance();
		    		//get type of Facility
		    		GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);	
			    	if(UtilValidate.isNotEmpty(facilityDetail)){
			    	facilityTypeId= facilityDetail.getString("facilityTypeId");
			    	}
			    	
		    		if(UtilValidate.isNotEmpty(boothCategory)){
		    			condList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS ,boothCategory));
		    		}
		        	condList.add(EntityCondition.makeCondition("facilityGroupTypeId", EntityOperator.EQUALS ,"RT_BOOTH_GROUP"));
		        	if((UtilValidate.isNotEmpty(facilityTypeId))&& facilityTypeId.equals("BOOTH")){
		        		condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,facilityId));
		        	}
		        	if((UtilValidate.isNotEmpty(facilityTypeId))&& facilityTypeId.equals("ROUTE")){
		        		condList.add(EntityCondition.makeCondition("ownerFacilityId", EntityOperator.EQUALS ,facilityId));
		        	}
		        	
		        	EntityCondition cond = EntityCondition.makeCondition(condList ,EntityOperator.AND);
		        	booths = delegator.findList("FacilityGroupAndMemberAndFacility", cond, null, null, null, true);
		        	 booths = EntityUtil.filterByDate(booths, effectiveDate); 
		        	 for(GenericValue facilityBooth: booths){
		        	 boothRouteIdsMap.put(facilityBooth.getString("facilityId"),facilityBooth.getString("ownerFacilityId"));
		        	 }
		        	 boothIds = EntityUtil.getFieldListFromEntityList(booths, "facilityId", true);
		       
		    	} catch (GenericEntityException e) {
		            Debug.logError(e, module);
		        }
		    	Map<String, Object> result = ServiceUtil.returnSuccess(); 
		    	result.put("boothsList", booths);
		        result.put("boothIdsList", boothIds);
		        result.put("boothRouteIdsMap", boothRouteIdsMap);
		        return result;
			}
		// This will return the mapping of zone to booths (for all zones) 
			// e.g. {"TR": {"name": "Tarnaka", "boothIds":["100","101"]},...}	
		public static Map<String, Object> getAllZonesBoothsMap(Delegator delegator){
	        Map<String, Object> result = FastMap.newInstance(); 
	    	try {
	    		List<GenericValue> zones = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "ZONE"), null, UtilMisc.toList("facilityId"), null, false);
	            Iterator<GenericValue> zoneIter = zones.iterator();
	        	while(zoneIter.hasNext()) {
	                GenericValue zone = zoneIter.next();
	                List boothIds = getZoneBooths(delegator, zone.getString("facilityId"));
	                Map <String, Object> zoneMap = FastMap.newInstance();
	                zoneMap.put("name", zone.getString("facilityName"));
	                zoneMap.put("distributorId", zone.getString("parentFacilityId"));
	                zoneMap.put("boothIds", boothIds);  
	                result.put(zone.getString("facilityId"), zoneMap);
	        	}
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	    	return result;
		}

		// This is essentially a transpose of earlier zones to booths map
		public static Map<String, Object> getAllBoothsZonesMap(Delegator delegator){
	        Map<String, Object> result = new TreeMap<String, Object>(); 
	        Map<String, Object> zonesMap = getAllZonesBoothsMap(delegator);
	        for ( Map.Entry<String, Object> entry : zonesMap.entrySet() ) {
	        	Map<String, Object> zoneValue = (Map<String, Object>)entry.getValue();
	        	List boothIds = (List)zoneValue.get("boothIds");
	        	Iterator<String> boothIter = boothIds.iterator();
	        	while (boothIter.hasNext()) {
	                Map <String, Object> boothMap = FastMap.newInstance();
	                boothMap.put("name", zoneValue.get("name"));
	                boothMap.put("distributorId", zoneValue.get("distributorId"));
	                boothMap.put("zoneId", entry.getKey());         		
	        		result.put(boothIter.next(), boothMap);
	        	}
	        }
	    	return result;
		}		
		public static Map<String, Object> getAllBoothsRegionsMap(DispatchContext ctx,Map<String, ? extends Object> context){
			 Delegator delegator = ctx.getDelegator();
			 Map<String, Object> result = new TreeMap<String, Object>(); 
		    
		     List<GenericValue> regions = null;
		     try{
		    	 regions = delegator.findList("FacilityGroup", EntityCondition.makeCondition("facilityGroupTypeId", EntityOperator.EQUALS, "REGION_TYPE"), null, null, null, false);
		     }catch (Exception e) {
				// TODO: handle exception
		       Debug.logError(e, module);	
			}
		    Map boothsRegionsMap = FastMap.newInstance();
		    Map groupMemberCtx = FastMap.newInstance();
		    Map<String ,List> regionBoothMap = FastMap.newInstance();
		 	for(GenericValue region : regions){
		 		try{
		 		List<GenericValue> regionMembers = delegator.findList("FacilityGroupMemberAndFacility", EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, region.getString("facilityGroupId")), null, null, null, false);
		 		regionBoothMap.put(region.getString("facilityGroupId"), EntityUtil.getFieldListFromEntityList(regionMembers, "facilityId", true));
		 		}catch (Exception e) {
					// TODO: handle exception
			       Debug.logError(e, module);	
				}
		 	}
		 	List<GenericValue> boothsList =   (List<GenericValue>)getAllBooths(delegator).get("boothsDetailsList");
		    for (GenericValue booth: boothsList ) { 
		    	    Map <String, Object> boothMap = FastMap.newInstance();
	                String boothId= booth.getString("facilityId");
	                String zoneId =booth.getString("zoneId");
	                boothMap.put("name", booth.getString("facilityName"));
	                boothMap.put("zoneId", zoneId);
	                boothMap.put("regionId", "");
	                //lets populate regionId here
	                if(UtilValidate.isNotEmpty(regionBoothMap)){
	                	for ( Map.Entry<String, List> regionEntry : regionBoothMap.entrySet() ) {
	                		  List regionBooths= regionEntry.getValue();
	                		  if(regionBooths.contains(zoneId)){
	                			  boothMap.put("regionId", regionEntry.getKey());
	                		  }
	                	}
	                }                
	        	result.put(boothId, boothMap);       	
		    }
		    return result;
		}
		
		public static List getShipmentIdsByAMPM(Delegator delegator,String estimatedDeliveryDateString,String subscriptionType){
			return getShipmentIdsByAMPM(delegator, estimatedDeliveryDateString, subscriptionType, null);
		}

		public static List getShipmentIdsByAMPM(Delegator delegator,String estimatedDeliveryDateString,String subscriptionType, String routeId){
			
			List shipmentIds = FastList.newInstance();
			if(!subscriptionType.equals("AM") && !subscriptionType.equals("PM")){			
				return shipmentIds;			
			}
			if(subscriptionType.equals("AM")){
				shipmentIds = getShipmentIds(delegator , estimatedDeliveryDateString,"AM_SHIPMENT", routeId);
				shipmentIds.addAll(getShipmentIds(delegator , estimatedDeliveryDateString,"AM_SHIPMENT_SUPPL"));
			}else{
				shipmentIds = getShipmentIds(delegator , estimatedDeliveryDateString,"PM_SHIPMENT", routeId);			
				shipmentIds.addAll(getShipmentIds(delegator , estimatedDeliveryDateString,"PM_SHIPMENT_SUPPL"));			
			}		
			return shipmentIds;
		}
		public static List getShipmentIds(Delegator delegator,String estimatedDeliveryDateString,String shipmentTypeId){
			//TO DO:for now getting one shipment id  we need to get pm and am shipment id irrespective of Shipment type Id
			return getShipmentIds(delegator,estimatedDeliveryDateString,shipmentTypeId, null); 
		
		}
		// This will return the list of ShipmentIds for the selected 
		public static List getShipmentIds(Delegator delegator,String estimatedDeliveryDateString,String shipmentTypeId, String routeId){
			//TO DO:for now getting one shipment id  we need to get pm and am shipment id irrespective of Shipment type Id
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();
			String estimatedDeliveryDateStr =estimatedDeliveryDateString;		
			List conditionList= FastList.newInstance();
			List shipmentList =FastList.newInstance();
			List shipments = FastList.newInstance();
			try {
				estimatedDeliveryDate = new java.sql.Timestamp(sdf.parse(estimatedDeliveryDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + estimatedDeliveryDateStr, module);		   
			}
			Timestamp dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDate);
			Timestamp dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDate);		
			if(shipmentTypeId == null){
				conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN , UtilMisc.toList("AM_SHIPMENT","AM_SHIPMENT_SUPPL")));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
				if(UtilValidate.isNotEmpty(routeId)){
					conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS , routeId));
				}
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
				try {
					shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
				}catch (Exception e) {
					Debug.logError(e, "Cannot parse date string: " + estimatedDeliveryDateStr, module);		   
				}
				if(!UtilValidate.isEmpty(shipmentList)){
					shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
				}
				
				conditionList.clear();
				condition.reset();
				
				// lets check the tenant configuration for enableSameDayPmEntry
				Boolean enableSameDayPmEntry = Boolean.FALSE;
				try{
					 GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSameDayPmEntry"), false);
					 if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry) && (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
						 enableSameDayPmEntry = Boolean.TRUE;
					}
				 }catch (GenericEntityException e) {
					// TODO: handle exception
					 Debug.logError(e, module);
				}
				
				conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN , UtilMisc.toList("PM_SHIPMENT","PM_SHIPMENT_SUPPL")));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
				if(!enableSameDayPmEntry){
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.addDaysToTimestamp(dayBegin, -1)));
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.addDaysToTimestamp(dayEnd, -1)));
				}else{
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
				}
				if(UtilValidate.isNotEmpty(routeId)){
					conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS , routeId));
				}
				condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
				try {
					shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
				}catch (Exception e) {
					Debug.logError(e, "Cannot parse date string: " + estimatedDeliveryDateStr, module);		   
				}
				if(!UtilValidate.isEmpty(shipmentList)){
					shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
				}			
				
			}else{
				conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS , shipmentTypeId));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
				if(UtilValidate.isNotEmpty(routeId)){
					conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS , routeId));
				}
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
				try {
					shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
				}catch (Exception e) {
					Debug.logError(e, "Cannot parse date string: " + estimatedDeliveryDateStr, module);		   
				}			
				shipments = EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false);	
			}
			return shipments;
		}    
		 /**
	     * Helper method will return all the shipmentId's for given fromDate ,thruDate
	     * if fromDate is empty then it will return all shipments ids till thruDate
	     * 
	     *
	     * @return shipments List
	     */
		public static List getShipmentIds(Delegator delegator,Timestamp fromDate,Timestamp thruDate){
			
			List conditionList= FastList.newInstance();
			List shipmentList =FastList.newInstance();
			List shipments = FastList.newInstance();
			Timestamp dayBegin = UtilDateTime.nowTimestamp();
			Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN , UtilMisc.toList("AM_SHIPMENT","AM_SHIPMENT_SUPPL")));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
			if(!UtilValidate.isEmpty(fromDate)){
				dayBegin = UtilDateTime.getDayStart(fromDate);
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
			}
			
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
			try {
				shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
			}catch (Exception e) {
				Debug.logError(e, "Exception while getting shipment ids ", module);		   
			}
			if(!UtilValidate.isEmpty(shipmentList)){
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}
			
			conditionList.clear();
			condition.reset();		
			// Enable LMS PM Sales entry for sameday
			//Enable LMS PM Sales(if the property set to 'Y' then ,Day  NetSales  = 'AM Sales+Prev.Day PM Sales'   otherwise NetSales = 'AM Sales+ PM Sales')
			Boolean enableSameDayPmEntry = Boolean.FALSE;
			try{
				 GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSameDayPmEntry"), false);
				 if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry) && (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
					 enableSameDayPmEntry = Boolean.TRUE;
				}
			 }catch (GenericEntityException e) {
				// TODO: handle exception
				 Debug.logError(e, module);
			}
			
			
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN , UtilMisc.toList("PM_SHIPMENT","PM_SHIPMENT_SUPPL")));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
			if(!UtilValidate.isEmpty(fromDate)){
				if(!enableSameDayPmEntry){
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.addDaysToTimestamp(dayBegin, -1)));
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.addDaysToTimestamp(dayEnd, -1)));
				}else{
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO , dayEnd));
				}
				
			}
			
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
			try {
				shipmentList =delegator.findList("Shipment", condition, null , null, null, false);
			}catch (Exception e) {
				Debug.logError(e, "Exception while getting shipment ids ", module);			   
			}
			if(!UtilValidate.isEmpty(shipmentList)){
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}			
			
			
			return shipments;
		}    
	    
	    /**
	     * Get all routes
	     * @param ctx the dispatch context
	     * @param context 
	     * @return a List of routes
	     */
	    public static Map<String, Object> getRoutes(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	List<String> routes= FastList.newInstance();
	    	try {
	    		List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "ROUTE"), null, UtilMisc.toList("facilityId"), null, false);
	            routes = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("routesList", routes);

	        return result;
	    }    
	    /**
	     * Get all zones
	     * @param ctx the dispatch context
	     * @param context 
	     * @return a List of routes
	     */
	    public static Map<String, Object> getZones(Delegator delegator) {
	    	
	    	List<String> zones= FastList.newInstance();
	    	try {
	    		List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "ZONE"), null, UtilMisc.toList("facilityId"), null, false);
	    		zones = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("zonesList", zones);
	        return result;
	    }    
	 public static Map<String, Object> getZonesComissionRates(DispatchContext dctx,Map<String, ? extends Object> context) {
			Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
		 	Map zonesComissionRates= FastMap.newInstance();
	    	Map<String, Object> zonesMap= getZones(delegator);
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	List zonesList = (List)zonesMap.get("zonesList");
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
	    	inputRateAmt.put("periodTypeId", "RATE_HOUR");
	    	inputRateAmt.put("rateCurrencyUomId", "INR");
	    	try {
	    		for(int i=0;i<zonesList.size();i++){
	    			inputRateAmt.put("rateTypeId", zonesList.get(i)+"_ZN_MRGN");	
	    			result = dispatcher.runSync("getRateAmount", inputRateAmt);
	    			zonesComissionRates.put(zonesList.get(i), result.get("rateAmount"));    			
	    		}
	    	} catch (GenericServiceException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	    	result = ServiceUtil.returnSuccess();       
	        result.put("zonesComissionRates", zonesComissionRates);
	        return result;
	    }    

	    public static Map getBoothPaidPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
			BigDecimal invoicesTotalAmount = BigDecimal.ZERO;		        
			List boothPaymentsList = FastList.newInstance();
	    	Map boothsPaymentsDetail = FastMap.newInstance();
	        String facilityId = (String) context.get("facilityId");
	        String userLoginId = (String) context.get("userLoginId"); 
	        String paymentDate = (String) context.get("paymentDate");
	        Timestamp fromDate = UtilDateTime.nowTimestamp();
	        Timestamp thruDate = UtilDateTime.nowTimestamp();
	        List facilityIdsList=FastList.newInstance();
	        Map boothRouteIdsMap = FastMap.newInstance();
	        
	        String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
	        List paymentIds = (List) context.get("paymentIds");
	        boolean onlyCurrentDues= Boolean.FALSE;
	        if(context.get("onlyCurrentDues") != null){
	        	onlyCurrentDues = (Boolean)context.get("onlyCurrentDues");
	        }
	        if(onlyCurrentDues){
	        	boothsPaymentsDetail = getCurrentDuesBoothPaidPayments( dctx , context);
	        	return boothsPaymentsDetail;
	        }
	        Map boothRouteResultMap =getBoothsRouteMap(delegator,UtilMisc.toMap("facilityId",facilityId,"effectiveDate",fromDate));
			facilityIdsList=(List)boothRouteResultMap.get("boothIdsList");
			//get All booths RouteIds
			if(UtilValidate.isNotEmpty(boothRouteResultMap)){
				boothRouteIdsMap=(Map)boothRouteResultMap.get("boothRouteIdsMap");//to get routeIds
			}
			Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
			if(paymentDate != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				   
				}		
			}
	        Locale locale = (Locale) context.get("locale");
			Timestamp dayBegin = UtilDateTime.getDayStart(paymentTimestamp, TimeZone.getDefault(), locale);
			Timestamp dayEnd = UtilDateTime.getDayEnd(paymentTimestamp, TimeZone.getDefault(), locale);
			List exprList = FastList.newInstance();
			//get Payments for period if fromDate and thruDate available in params
			if(!UtilValidate.isEmpty(context.get("fromDate"))){
	        	fromDate = (Timestamp)context.get("fromDate");
	        	dayBegin = UtilDateTime.getDayStart(fromDate);
	        }
	        if(!UtilValidate.isEmpty(context.get("thruDate"))){
	        	thruDate = (Timestamp)context.get("thruDate");
	        	dayEnd = UtilDateTime.getDayEnd(thruDate);
	        }		

			if(facilityId != null){			
				try{
					GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);				
					if(facilityDetail == null || (!facilityDetail.getString("facilityTypeId").equals("ZONE") && (!facilityDetail.getString("facilityTypeId").equals("ROUTE")) &&  !facilityDetail.getString("facilityTypeId").equals("BOOTH")) ){
						Debug.logInfo("facilityId '"+facilityId+ "'is not a Booth or Zone ", "");
						return ServiceUtil.returnError("facilityId '"+facilityId+ "'is not a Booth or Zone ");
					}
					if(facilityDetail.getString("facilityTypeId").equals("ZONE")){					
						exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIdsList));
					}else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")){
						exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIdsList));
					}else{
						exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
					}
					
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("facilityId '"+facilityId+ "' error");				
				}			
			}		
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin), EntityOperator.AND, EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
			exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
			if (!UtilValidate.isEmpty(userLoginId)) {
				exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS, userLoginId));
			}
			if (!UtilValidate.isEmpty(paymentMethodTypeId)) {
				exprList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, paymentMethodTypeId));
			}
			if (!UtilValidate.isEmpty(paymentIds)) {
				exprList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIds));
			}
			EntityCondition condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);		
			List paymentsList = FastList.newInstance();
			try {                                       
				paymentsList = delegator.findList("PaymentAndFacility", condition, null, UtilMisc.toList("-lastModifiedDate"), null, false);			
				String tempFacilityId = "";	
				Map tempPayment = FastMap.newInstance();
				for (int i = 0; i < paymentsList.size(); i++) {				
					GenericValue boothPayment = (GenericValue)paymentsList.get(i);				
					if(tempFacilityId == ""){
						tempFacilityId = boothPayment.getString("facilityId");
						tempPayment.put("facilityId", boothPayment.getString("facilityId"));
						tempPayment.put("routeId", boothPayment.getString("parentFacilityId"));
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))) {
							tempPayment.put("routeId", boothRouteIdsMap.get(tempFacilityId));
						}
						tempPayment.put("paymentDate",  boothPayment.getTimestamp("paymentDate"));
						tempPayment.put("paymentId",  boothPayment.getString("paymentId"));
						tempPayment.put("paymentMethodTypeId", boothPayment.getString("paymentMethodTypeId"));				
						tempPayment.put("amount",BigDecimal.ZERO);
						tempPayment.put("userId", boothPayment.getString("createdByUserLogin"));			
					}					
					if (!(tempFacilityId.equals(boothPayment.getString("facilityId"))))  {				
						//populating paymentMethodTypeId for paid invoices										
						boothPaymentsList.add(tempPayment);
						tempFacilityId = boothPayment.getString("facilityId");
						tempPayment =FastMap.newInstance();
						tempPayment.put("facilityId", boothPayment.getString("facilityId"));
						tempPayment.put("routeId", boothPayment.getString("parentFacilityId"));
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))) {
							tempPayment.put("routeId", boothRouteIdsMap.get(tempFacilityId));
						}
						tempPayment.put("paymentDate",  boothPayment.getTimestamp("paymentDate"));
						tempPayment.put("paymentId",  boothPayment.getString("paymentId"));
						tempPayment.put("paymentMethodTypeId", boothPayment.getString("paymentMethodTypeId"));				
						tempPayment.put("amount",boothPayment.getBigDecimal("amount"));
						tempPayment.put("userId", boothPayment.getString("createdByUserLogin"));					
						
					}else{				
						tempPayment.put("amount", (boothPayment.getBigDecimal("amount")).add((BigDecimal)tempPayment.get("amount")));					
					}					
					if((i == paymentsList.size()-1)){						
						boothPaymentsList.add(tempPayment);					
					}
				}			
			}
			catch(GenericEntityException e){
				Debug.logError(e, module);	
				return ServiceUtil.returnError(e.toString());
			}
			// rounding off booth amounts		
			List tempPaymentsList =FastList.newInstance();
			for(int i=0; i<boothPaymentsList.size();i++){
				Map entry = FastMap.newInstance();
				entry.putAll((Map)boothPaymentsList.get(i));
				BigDecimal roundingAmount = ((BigDecimal)entry.get("amount")).setScale(0, rounding);
				entry.put("amount" ,roundingAmount);
				invoicesTotalAmount = invoicesTotalAmount.add(roundingAmount);			
				tempPaymentsList.add(entry);		
			}
			boothsPaymentsDetail.put("invoicesTotalAmount", invoicesTotalAmount);
			boothsPaymentsDetail.put("boothPaymentsList", tempPaymentsList);		
			return boothsPaymentsDetail;   
	    }
	   
	    public static Map getCurrentDuesBoothPaidPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Locale locale = (Locale) context.get("locale");
			BigDecimal invoicesTotalAmount = BigDecimal.ZERO;		        
			List boothPaymentsList = FastList.newInstance();
	    	Map boothsPaymentsDetail = FastMap.newInstance();
	        String facilityId = (String) context.get("facilityId");
	        String userLoginId = (String) context.get("userLoginId"); 
	        String paymentDate = (String) context.get("paymentDate");
	        String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
	        List paymentIds = (List) context.get("paymentIds");        
			Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
			if(paymentDate != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				   
				}		
			}
	        
			Timestamp dayBegin = UtilDateTime.getDayStart(paymentTimestamp, TimeZone.getDefault(), locale);
			Timestamp dayEnd = UtilDateTime.getDayEnd(paymentTimestamp, TimeZone.getDefault(), locale);
			List exprList = FastList.newInstance();		
			List shipmentIds = getShipmentIds(delegator , UtilDateTime.toDateString(paymentTimestamp, "yyyy-MM-dd HH:mm:ss"),null);	
			List invoiceIds = FastList.newInstance();
			boolean enableSoCrPmntTrack = Boolean.FALSE;
			try{
				 GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSoCrPmntTrack"), true);
				 if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack) && (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
					 enableSoCrPmntTrack = Boolean.TRUE;
				 	} 
			}catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
				}			
		   if(enableSoCrPmntTrack){
				exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
		    }else{
				exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH"));
			}		
			exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, new ArrayList(shipmentIds)));
			EntityCondition	paramCond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			try{			
				List boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null , UtilMisc.toList("parentFacilityId","originFacilityId","-estimatedDeliveryDate"), findOptions, false);
				invoiceIds = EntityUtil.getFieldListFromEntityList(boothOrdersList, "invoiceId", false);
			}catch(GenericEntityException e){
				Debug.logError(e, module);	
				return ServiceUtil.returnError(e.toString());
			}		
			exprList.clear();
			exprList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
			if(facilityId != null){			
				try{
					GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);				
					if(facilityDetail == null || (!facilityDetail.getString("facilityTypeId").equals("ZONE") && (!facilityDetail.getString("facilityTypeId").equals("ROUTE")) &&  !facilityDetail.getString("facilityTypeId").equals("BOOTH")) ){
						Debug.logInfo("facilityId '"+facilityId+ "'is not a Booth or Zone ", "");
						return ServiceUtil.returnError("facilityId '"+facilityId+ "'is not a Booth or Zone ");
					}
					if(facilityDetail.getString("facilityTypeId").equals("ZONE")){					
						exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, getZoneBooths(delegator,facilityId)));
					}else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")){
						exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, getRouteBooths(delegator,facilityId)));
					}else{
						exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
					}
					
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("facilityId '"+facilityId+ "' error");				
				}			
			}		
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin), EntityOperator.AND, EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
			exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
			if (!UtilValidate.isEmpty(userLoginId)) {
				exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS, userLoginId));
			}
			if (!UtilValidate.isEmpty(paymentMethodTypeId)) {
				exprList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, paymentMethodTypeId));
			}
			if (!UtilValidate.isEmpty(paymentIds)) {
				exprList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIds));
			}
			EntityCondition condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
			List paymentsList = FastList.newInstance();
			try {                                       
				paymentsList = delegator.findList("PaymentFacilityAndApplication", condition, null, UtilMisc.toList("-lastModifiedDate"), null, false);
				for (int i = 0; i < paymentsList.size(); i++) {
					GenericValue boothPayment = (GenericValue)paymentsList.get(i);
					Map tempPayment = FastMap.newInstance();				
					tempPayment.put("facilityId", boothPayment.getString("facilityId"));
					tempPayment.put("routeId", boothPayment.getString("parentFacilityId"));
					tempPayment.put("paymentDate",  boothPayment.getTimestamp("paymentDate"));
					tempPayment.put("paymentId",  boothPayment.getString("paymentId"));
					tempPayment.put("paymentMethodTypeId", boothPayment.getString("paymentMethodTypeId"));				
					tempPayment.put("amount", boothPayment.getBigDecimal("amountApplied"));
					tempPayment.put("userId", boothPayment.getString("createdByUserLogin"));
					boothPaymentsList.add(tempPayment);										
				}
			}
			catch(GenericEntityException e){
				Debug.logError(e, module);	
				return ServiceUtil.returnError(e.toString());
			}
			// rounding off booth amounts		
			List tempPaymentsList =FastList.newInstance();
			for(int i=0; i<boothPaymentsList.size();i++){
				Map entry = FastMap.newInstance();
				entry.putAll((Map)boothPaymentsList.get(i));
				BigDecimal roundingAmount = ((BigDecimal)entry.get("amount")).setScale(0, rounding);
				entry.put("amount" ,roundingAmount);
				invoicesTotalAmount = invoicesTotalAmount.add(roundingAmount);			
				tempPaymentsList.add(entry);		
			}
			boothsPaymentsDetail.put("invoicesTotalAmount", invoicesTotalAmount);
			boothsPaymentsDetail.put("boothPaymentsList", tempPaymentsList);		
			return boothsPaymentsDetail;   
	    }
	    //This method  will give only the Pending Payments
	    public static Map getBoothPayments(Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues){
	    	Map boothsPaymentsDetail= getBoothReceivablePayments(delegator ,dispatcher ,userLogin,paymentDate, invoiceStatusId ,facilityId ,paymentMethodTypeId ,onlyCurrentDues ,Boolean.TRUE);
	    	
	    	return boothsPaymentsDetail;
	    }
	    public static Map getBoothReceivablePayments(Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues ,boolean isPendingDues){
			Map<String , Object> context = FastMap.newInstance();
			context.put("userLogin", userLogin);
			context.put("invoiceStatusId", invoiceStatusId);
			context.put("facilityId", facilityId);
			context.put("onlyCurrentDues", onlyCurrentDues);
			context.put("isPendingDues", isPendingDues);		
			Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
			if(paymentDate != null){
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
				   
				}	
							
			}
			if(onlyCurrentDues){
				context.put("fromDate", UtilDateTime.getDayStart(paymentTimestamp));			
			}else{
				context.put("fromDate", null);			
			}
			context.put("thruDate", UtilDateTime.getDayEnd(paymentTimestamp));
	    	Map boothsPaymentsDetail = getBoothReceivablePaymentsForPeriod(dispatcher.getDispatchContext() ,context);
	    	    	
			return boothsPaymentsDetail;
		}
	    
	    public static Map<String, Object> getPaymentMethodTypeForBooth(DispatchContext dctx, Map context) {		
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
	    	Timestamp fromDate = (Timestamp) context.get("fromDate");
	    	String facilityId = (String) context.get("facilityId");
			List facilityIds = FastList.newInstance();
			Map facilityPaymentMethod = FastMap.newInstance(); 
			if(UtilValidate.isEmpty(fromDate)){
				fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			}
				try{
					Map facilityOwner = FastMap.newInstance();
					if(UtilValidate.isEmpty(facilityId)){
						//return ServiceUtil.returnSuccess();
					 facilityIds = (List)getAllBooths(delegator).get("boothsList");
					}else{
					    GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
						if(facility.getString("facilityTypeId").equals("ROUTE")){
							facilityIds = getRouteBooths(delegator,facilityId);
						}else{
							 facilityIds.add(facilityId);
						}
					}
					List<GenericValue> facilityOwnerParty = delegator.findList("Facility",EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds) , null, UtilMisc.toList("facilityId"), null, false);
					List ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilityOwnerParty, "ownerPartyId", false);
					for(GenericValue eachBooth : facilityOwnerParty){
						facilityOwner.put(eachBooth.getString("ownerPartyId"), eachBooth.getString("facilityId"));
					}
					//PartyProfileDefaultAndFacilityAndMethodType
					
					List paymentTypeConditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.IN, ownerPartyIds));
					paymentTypeConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
					paymentTypeConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayEnd(fromDate))));
			        EntityCondition paymentTypeCondition = EntityCondition.makeCondition(paymentTypeConditionList, EntityOperator.AND);
			        List<GenericValue> paymentTypeList = delegator.findList("PartyProfileDefault", paymentTypeCondition, null, null, null, false);
			        paymentTypeList = EntityUtil.filterByDate(paymentTypeList, UtilDateTime.getDayStart(fromDate));
					for(GenericValue eachBoothMeth : paymentTypeList){
						String owner = eachBoothMeth.getString("partyId");
						facilityPaymentMethod.put(facilityOwner.get(owner), eachBoothMeth.getString("defaultPayMeth"));
						//facilityPaymentDescrption.put(facilityOwner.get(owner), eachBoothMeth.getString("defaultPayMeth"));
					}
					result.put("partyPaymentMethod", facilityPaymentMethod);
		        }catch (GenericEntityException e) {
		            	Debug.logError(e, module);
		         }
	    	return result;
	    	
	    	
		}
	    public static Map getBoothReceivablePaymentsForPeriod(DispatchContext dctx, Map<String, ? extends Object> context){
	    	//Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues ,boolean isPendingDues){
			//TO DO:for now getting one shipment id  we need to get pmand am shipment id irrespective of Shipment type Id
		    Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");	    
		    String invoiceStatusId = (String) context.get("invoiceStatusId");
		    String facilityId = (String) context.get("facilityId");	   
		    Boolean onlyCurrentDues = (Boolean) context.get("onlyCurrentDues");	   
		    Boolean isPendingDues = (Boolean) context.get("isPendingDues");
		    Timestamp fromDate = null;	    
		    if(!UtilValidate.isEmpty(context.get("fromDate"))){
		    	 fromDate = (Timestamp) context.get("fromDate");
		    }	   
		    Timestamp thruDate = (Timestamp) context.get("thruDate");
		    List exprListForParameters = FastList.newInstance();
			List boothPaymentsList = FastList.newInstance();
			List boothOrdersList = FastList.newInstance();
			List facilityIdsList = FastList.newInstance();
			Map boothRouteResultMap = FastMap.newInstance();
			Map boothRouteIdsMap = FastMap.newInstance();
			Set shipmentIds = FastSet.newInstance();		 
			Map boothsPaymentsDetail = FastMap.newInstance();
			BigDecimal invoicesTotalAmount = BigDecimal.ZERO;
			BigDecimal invoicesTotalDueAmount = BigDecimal.ZERO;		
			if(thruDate != null){				
				shipmentIds = new HashSet(getShipmentIds(delegator ,fromDate,thruDate));			
			}
			boothRouteResultMap =getBoothsRouteMap(delegator,UtilMisc.toMap("facilityId",facilityId,"effectiveDate",fromDate));
			facilityIdsList=(List)boothRouteResultMap.get("boothIdsList");
			
			if(UtilValidate.isNotEmpty(boothRouteResultMap)){
				boothRouteIdsMap=(Map)boothRouteResultMap.get("boothRouteIdsMap");//to get routeIds
			}
			
			 Map partyPaymentMethodDesc=(Map)getPartyProfileDafult(dispatcher.getDispatchContext(),UtilMisc.toMap("boothIds", UtilMisc.toList(facilityIdsList),"supplyDate",fromDate)).get("partyPaymentMethodDesc");
			 
			//Map paymentMethod = (Map)getPaymentMethodTypeForBooth(dctx, UtilMisc.toMap("facilityId", facilityId, "userLogin", userLogin,"fromDate",fromDate)).get("partyPaymentMethod");
			Set currentDayShipments = new HashSet(getShipmentIds(delegator ,UtilDateTime.toDateString(thruDate, "yyyy-MM-dd HH:mm:ss"),null));
			boolean enableSoCrPmntTrack = Boolean.FALSE;
			
			try{
				 GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSoCrPmntTrack"), true);
				 if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack) && (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
					 enableSoCrPmntTrack = Boolean.TRUE;
				 	} 
			}catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
			 }			
		    if(enableSoCrPmntTrack){
				exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
			}else{
				exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH"));
			}
			
			//exprListForParameters.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(paymentTimestamp)));
			if(facilityId != null){			
				try{
					GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);				
					if(facilityDetail == null || (!facilityDetail.getString("facilityTypeId").equals("ZONE") &&  !facilityDetail.getString("facilityTypeId").equals("ROUTE") && !facilityDetail.getString("facilityTypeId").equals("BOOTH")) ){
						Debug.logInfo("facilityId '"+facilityId+ "'is not a Booth or Zone ", "");
						return ServiceUtil.returnError("facilityId '"+facilityId+ "'is not a Booth or Zone ");
					}
					
					if(facilityDetail.getString("facilityTypeId").equals("ZONE")){
						exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN,facilityIdsList ));
					}else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")){
						 exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIdsList));
					}else{
						exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
					}
					
				}catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
				}			
			}
			
			if(invoiceStatusId != null){			
				exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.EQUALS, invoiceStatusId));
			}else{
				List invoiceStatusList=FastList.newInstance();
				if(UtilValidate.isNotEmpty(isPendingDues) && isPendingDues){
					invoiceStatusList = UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF");
					
				}else{
					invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");
				}
				exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN, invoiceStatusList));
			}
			
			exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, new ArrayList(shipmentIds)));			
			
			// filter out booths owned by the APDDCF
			exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, "Company"));
			EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			try{			
				boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null , UtilMisc.toList("originFacilityId","-estimatedDeliveryDate"), findOptions, false);
			}catch(GenericEntityException e){
				Debug.logError(e, module);	
				return ServiceUtil.returnError(e.toString());
			}
			List<GenericValue> obInvoiceList = (List)getOpeningBalanceInvoices(dctx,UtilMisc.toMap("facilityId",facilityId,"fromDate", fromDate ,"thruDate" , thruDate)).get("invoiceList");
			boothOrdersList.addAll(obInvoiceList);
			boothOrdersList = EntityUtil.orderBy(boothOrdersList, UtilMisc.toList("originFacilityId","-estimatedDeliveryDate"));
			Map<String, Object> totalAmount =FastMap.newInstance();
			
			if (!UtilValidate.isEmpty(boothOrdersList)) {
				List invoiceIds = EntityUtil.getFieldListFromEntityList(boothOrdersList, "invoiceId", false);
				
				String tempFacilityId = "";		
				Map tempPayment = FastMap.newInstance();
				
				for(int i =0 ; i< boothOrdersList.size(); i++){
					GenericValue boothPayment = (GenericValue)boothOrdersList.get(i);				
					List paymentApplicationList =FastList.newInstance();
					Map invoicePaymentInfoMap =FastMap.newInstance();
					BigDecimal outstandingAmount =BigDecimal.ZERO;
					String invoiceTypeId ="";
					try{
						invoiceTypeId = boothPayment.getRelatedOne("Invoice").getString("invoiceTypeId");
					}catch (Exception e) {
						// TODO: handle exception
					}
					invoicePaymentInfoMap.put("invoiceId", boothPayment.getString("invoiceId"));
					invoicePaymentInfoMap.put("userLogin",userLogin);
					try{
						Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", invoicePaymentInfoMap);
						if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
				            	Debug.logError(getInvoicePaymentInfoListResult.toString(), module);    			
				                return ServiceUtil.returnError(null, null, null, getInvoicePaymentInfoListResult);
				            }
						Map invoicePaymentInfo = (Map)((List)getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
						outstandingAmount = (BigDecimal)invoicePaymentInfo.get("outstandingAmount");
						if(UtilValidate.isNotEmpty(isPendingDues) && !isPendingDues){
							outstandingAmount = (BigDecimal)invoicePaymentInfo.get("amount");
						}					
						
					}catch (GenericServiceException e) {
						// TODO: handle exception
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.toString());
					}	
					if(tempFacilityId == ""){
						tempFacilityId = boothPayment.getString("originFacilityId");
						tempPayment =FastMap.newInstance();
						tempPayment.put("facilityId", tempFacilityId);
						tempPayment.put("routeId", boothPayment.getString("parentFacilityId"));
						if(UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))){
							tempPayment.put("routeId", boothRouteIdsMap.get(tempFacilityId));
						}
						tempPayment.put("supplyDate",  boothPayment.getTimestamp("estimatedDeliveryDate"));
						if(UtilValidate.isNotEmpty(partyPaymentMethodDesc.get(tempFacilityId))){
							tempPayment.put("paymentMethodType", partyPaymentMethodDesc.get(tempFacilityId));
						}
						tempPayment.put("grandTotal", BigDecimal.ZERO);
						tempPayment.put("totalDue", BigDecimal.ZERO);				
					}					
					if (!(tempFacilityId.equals(boothPayment.getString("originFacilityId"))))  {				
						//populating paymentMethodTypeId for paid invoices										
						boothPaymentsList.add(tempPayment);
						tempFacilityId = boothPayment.getString("originFacilityId");
						tempPayment =FastMap.newInstance();
						tempPayment.put("facilityId", boothPayment.getString("originFacilityId"));
						tempPayment.put("routeId", boothPayment.getString("parentFacilityId"));
						if(UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))){
							tempPayment.put("routeId", boothRouteIdsMap.get(tempFacilityId));
						}
						if(UtilValidate.isNotEmpty(partyPaymentMethodDesc.get(tempFacilityId))){
							tempPayment.put("paymentMethodType", partyPaymentMethodDesc.get(tempFacilityId));
						}
						tempPayment.put("grandTotal", BigDecimal.ZERO);
						tempPayment.put("totalDue", BigDecimal.ZERO);					
						if(currentDayShipments.contains(boothPayment.getString("shipmentId")) || ( (thruDate.compareTo(UtilDateTime.getDayEnd(boothPayment.getTimestamp("estimatedDeliveryDate"))) == 0) && invoiceTypeId.equals(obInvoiceType))){
							tempPayment.put("grandTotal", outstandingAmount);
							tempPayment.put("totalDue", outstandingAmount);							
						}else{
							tempPayment.put("totalDue", outstandingAmount);
						}
						
						tempPayment.put("supplyDate",  boothPayment.getTimestamp("estimatedDeliveryDate"));
						
					}else{
						if(currentDayShipments.contains(boothPayment.getString("shipmentId")) || ( (thruDate.compareTo(UtilDateTime.getDayEnd(boothPayment.getTimestamp("estimatedDeliveryDate"))) == 0) && invoiceTypeId.equals(obInvoiceType))){
								tempPayment.put("grandTotal", outstandingAmount.add((BigDecimal)tempPayment.get("grandTotal")));
							tempPayment.put("totalDue", outstandingAmount.add((BigDecimal)tempPayment.get("totalDue")));						
						}else{
							tempPayment.put("totalDue", outstandingAmount.add((BigDecimal)tempPayment.get("totalDue")));
						}
							
					}					
					if((i == boothOrdersList.size()-1)){						
						boothPaymentsList.add(tempPayment);					
					}
				}		
				
			}
			// here rounding the booth amounts		
			List tempPaymentsList =FastList.newInstance();
			//tempPaymentsList.addAll(boothPaymentsList);
			for(int i=0; i<boothPaymentsList.size();i++){
				Map entry = FastMap.newInstance();
				entry.putAll((Map)boothPaymentsList.get(i));
				BigDecimal roundingAmount = ((BigDecimal)entry.get("grandTotal")).setScale(0, rounding);
				BigDecimal roundingTotalDueAmount = ((BigDecimal)entry.get("totalDue")).setScale(0, rounding);
				entry.put("grandTotal" ,roundingAmount);
				entry.put("totalDue" ,roundingTotalDueAmount);
				invoicesTotalAmount = invoicesTotalAmount.add(roundingAmount);
				invoicesTotalDueAmount = invoicesTotalDueAmount.add(roundingTotalDueAmount);
				tempPaymentsList.add(entry);			
			}
			boothsPaymentsDetail.put("invoicesTotalAmount", invoicesTotalAmount);
			boothsPaymentsDetail.put("invoicesTotalDueAmount", invoicesTotalDueAmount);
			boothsPaymentsDetail.put("boothPaymentsUnRoundedList", boothPaymentsList);		
			boothsPaymentsDetail.put("boothPaymentsList", tempPaymentsList);		
			return boothsPaymentsDetail;
		}
	    /**
	     * Get all payment pending booths
	     * @param ctx the dispatch context
	     * @param context 
	     * @return a List Stop Ship booths List
	     */
	    public static Map<String, Object> getStopShipList(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	Timestamp supplyDate = (Timestamp) context.get("supplyDate");
	    	String facilityId = (String) context.get("facilityId");
			List boothList = FastList.newInstance();
			List excludeStopShipBooths = FastList.newInstance();
			Map<String, Object> boothPayments = FastMap.newInstance();  
			if(supplyDate == null){
				supplyDate =UtilDateTime.getNextDayStart(UtilDateTime.nowTimestamp());
			}
			if(facilityId != null){			
				try{
					GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);				
					if(facilityDetail == null){
						Debug.logError("Booth " +facilityId+ " does not exist! ", module);
						return ServiceUtil.returnError("Booth " +facilityId+ " does not exist! ");
					}			
			
				}catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
					 return ServiceUtil.returnError(e.getMessage());
				}			
			}
			boothPayments = getBoothPayments(delegator, ctx.getDispatcher(), userLogin,
					UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"), null, facilityId ,null ,Boolean.FALSE);
		    List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
		   Map absenteeOverrideMap = getAbsenteeOverrideBooths(ctx , UtilMisc.toMap("overrideSupplyDate",supplyDate));
		   List absenteeOverrideList = (List)absenteeOverrideMap.get("boothList");
		   try{
			   excludeStopShipBooths = EntityUtil.getFieldListFromEntityList(delegator.findList("Facility", EntityCondition.makeCondition("excludeStopShipCheck" ,EntityOperator.EQUALS ,"Y"), null, null, null, true), "facilityId" ,true); 
		   }catch (Exception e) {
			// TODO: handle exception
			   Debug.logError(e, module);			
	           return ServiceUtil.returnError(e.getMessage());        	
		}	
		   excludeStopShipBooths.addAll(absenteeOverrideList);
		   Set excludeStopBoothSet = new HashSet(excludeStopShipBooths);
		   List tempPaymentsList = FastList.newInstance();
		    for (int i=0 ;i<boothPaymentsList.size(); i++) {
		        Map<String, Object> boothMap = FastMap.newInstance();  
		        String tempBoothId = (String)((Map)boothPaymentsList.get(i)).get("facilityId");
		        if(!excludeStopBoothSet.contains(tempBoothId)){	        		    	 
			    	boothList.add(tempBoothId);
			    	tempPaymentsList.add(boothPaymentsList.get(i));
		        }
		    	
		    }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("boothList", boothList);
	        result.put("boothPendingPaymentsList", tempPaymentsList);

	        return result;
	    }

	    /**
	     * Get all payment pending booths
	     * @param ctx the dispatch context
	     * @param context 
	     * @return a List of payment pending booths
	     */
	    public static Map<String, Object> getDaywiseBoothDues(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	String facilityId = (String) context.get("facilityId");
	    	
			List boothDuesList = FastList.newInstance();

			List exprListForParameters = FastList.newInstance();
			Map<String, Object> boothDuesDetail = FastMap.newInstance();
			List boothOrdersList = FastList.newInstance();
			BigDecimal totalAmount = BigDecimal.ZERO;		
		
			 	
			
			boolean enableSoCrPmntTrack = Boolean.FALSE;
			try{
				 GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSoCrPmntTrack"), true);
				 if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack) && (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
					 enableSoCrPmntTrack = Boolean.TRUE;
				 	} 
			}catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
			 }			
		    if(enableSoCrPmntTrack){
				exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
			}else{
				exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH"));
			}
			
			if(UtilValidate.isEmpty(facilityId)){	
				Debug.logError("Facility Id cannot be empty", module);
				return ServiceUtil.returnError("Facility Id cannot be empty");							
			}
			
			try{
				GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);				
				if(facilityDetail == null ||  !facilityDetail.getString("facilityTypeId").equals("BOOTH") ){
						Debug.logInfo("facilityId '"+facilityId+ "'is not a Booth ", "");
						return ServiceUtil.returnError("facilityId '"+facilityId+ "'is not a Booth");
				}			
			} catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.toString());				
			}

			exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
			exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF")));

		
			// filter out booths owned by the APDDCF
			exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, "Company"));
			EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);

			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			try{			
				boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null , UtilMisc.toList("estimatedDeliveryDate"), findOptions, false);
			} catch(GenericEntityException e){
				Debug.logError(e, module);	
				return ServiceUtil.returnError(e.toString());
			}
			List<GenericValue> obInvoiceList = (List)getOpeningBalanceInvoices(ctx,UtilMisc.toMap("facilityId", facilityId)).get("invoiceList");
	    	boothOrdersList.addAll(obInvoiceList);    	
	    	boothOrdersList = EntityUtil.orderBy(boothOrdersList, UtilMisc.toList("parentFacilityId","originFacilityId","estimatedDeliveryDate"));
			if (!UtilValidate.isEmpty(boothOrdersList)) {
				Timestamp firstDate = ((GenericValue)boothOrdersList.get(0)).getTimestamp("estimatedDeliveryDate");
				firstDate = UtilDateTime.getDayStart(firstDate);
				Timestamp lastDate = ((GenericValue)boothOrdersList.get(boothOrdersList.size() - 1)).getTimestamp("estimatedDeliveryDate");
				lastDate = UtilDateTime.getDayEnd(lastDate);
				Timestamp iterDate = firstDate;
				while (iterDate.compareTo(lastDate) < 0) {			
					Map<String, Object> boothPayments = FastMap.newInstance();   	
					boothPayments = getBoothPayments(delegator, ctx.getDispatcher(), userLogin,
		    			UtilDateTime.toDateString(iterDate, "yyyy-MM-dd HH:mm:ss"), null, facilityId ,null ,Boolean.TRUE);
					List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
					if (!UtilValidate.isEmpty(boothPaymentsList)) {
						Map tempDetail = (Map)boothPaymentsList.get(0);
						Map boothDue = FastMap.newInstance();
						boothDue.put("supplyDate", iterDate);
						boothDue.put("amount", tempDetail.get("grandTotal"));
						boothDuesList.add(boothDue);
						totalAmount = totalAmount.add((BigDecimal)tempDetail.get("grandTotal"));					
					}
					iterDate = UtilDateTime.addDaysToTimestamp(iterDate, 1);
				}
			}
			boothDuesDetail.put("totalAmount", totalAmount);
			boothDuesDetail.put("boothDuesList", boothDuesList);
	        return boothDuesDetail;    	
	    }
	    
	    /**
	     * Get all absentee Override booths
	     * @param ctx the dispatch context
	     * @param context 
	     * @return a List of Override booths
	     * @throws ConversionException 
	     */
	    public static Map<String, Object> getAbsenteeOverrideBooths(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			List exprListForParameters = FastList.newInstance();
			List boothList = FastList.newInstance();		 
			List<GenericValue> overrideList = FastList.newInstance();;
			Timestamp supplyDate = (Timestamp)context.get("overrideSupplyDate");
			if(supplyDate == null){
				supplyDate = UtilDateTime.nowTimestamp();
			}
			try{
				exprListForParameters.add(EntityCondition.makeCondition("supplyDate", EntityOperator.EQUALS, ((new DateTimeConverters.TimestampToSqlDate()).convert(supplyDate))));		
			} catch(ConversionException e){
	            Debug.logError(e, module);			
	            return ServiceUtil.returnError(e.getMessage());        	
			}	
			EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
			
			try{
				overrideList = delegator.findList("AbsenteeOverrideAndFacility", paramCond, null , UtilMisc.toList("boothId"), null, false);
	            if(!UtilValidate.isEmpty(overrideList)){
	            	boothList = EntityUtil.getFieldListFromEntityList(overrideList, "boothId" ,true);            	
	            }
			} catch(GenericEntityException e){
	            Debug.logError(e, module);			
	            return ServiceUtil.returnError(e.getMessage());        	
			}    	

	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("overrideList", overrideList);
	        result.put("boothList", boothList);

	        return result;
	    }  

	    /**
	     * Get booth dues
	     * @param ctx the dispatch context
	     * @param context 
	     * @return a List of booths that have payments due for the given route
	     */
	    public static Map<String, Object> getBoothDues(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			String boothId = (String) context.get("boothId");  
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	
	//Debug.logInfo("userLogin= " + userLogin, module);
	        
	        if (UtilValidate.isEmpty(boothId)) {
	            Debug.logError("Booth Id cannot be empty", module);
	            return ServiceUtil.returnError("Booth Id cannot be empty");        	
	        }
	        
	    	Map<String, Object> result= getBoothDetails(ctx, context);   	
	    	if (ServiceUtil.isError(result)) {
	            Debug.logError("Error fetching details for Booth Id " + boothId, module);
	            return ServiceUtil.returnError("Error fetching details for Booth Id " + boothId);     		
	    	}
	    	Map<String, Object> boothDues= (Map)result.get("boothDetails");
	    	Map<String, Object> boothTotalDues= FastMap.newInstance();
	    	boothTotalDues.putAll((Map)result.get("boothDetails"));
	    	BigDecimal unRoundedAmount = ZERO;
	    	BigDecimal unRoundedtotalDueAmount = ZERO;
	    	BigDecimal roundedAmount = ZERO;
	    	BigDecimal roundedtotalDueAmount = ZERO;
	    	Map<String, Object> boothPayments = FastMap.newInstance();   	
	    	 boothPayments = getBoothPayments(delegator, ctx.getDispatcher(), userLogin,
	    			UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"), null, boothId ,null ,Boolean.TRUE);
	        List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
	        List boothPaymentsUnRoundedList = (List) boothPayments.get("boothPaymentsUnRoundedList");
	        if (boothPaymentsList.size() != 0) {
	        	Map boothPayment = (Map)boothPaymentsList.get(0);
	        	roundedAmount = (BigDecimal)boothPayment.get("grandTotal");
	        	roundedtotalDueAmount = (BigDecimal)boothPayment.get("totalDue");
	        }
	        if (boothPaymentsUnRoundedList.size() != 0) {
	        	Map boothPayment = (Map)boothPaymentsUnRoundedList.get(0);
	        	unRoundedAmount = (BigDecimal)boothPayment.get("grandTotal");
	        	unRoundedtotalDueAmount = (BigDecimal)boothPayment.get("totalDue");
	        }
	    	boothDues.put("amount", roundedAmount.doubleValue());
	    	boothTotalDues.put("amount", unRoundedAmount.doubleValue());
	    	boothTotalDues.put("totalDueAmount", unRoundedtotalDueAmount.doubleValue());
	        result = ServiceUtil.returnSuccess();        
	        result.put("boothDues", boothDues);
	        result.put("boothTotalDues", boothTotalDues);

	        return result;        
	    }
	    /**
	     * Get booth dues running Total
	     * @param ctx the dispatch context
	     * @param context 
	     * @return a List of booths that have payments due for the given route
	     */
	    public static Map<String, Object> getBoothDuesRunningTotal(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();		
			List boothIds = (List) context.get("boothIds");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        BigDecimal boothRunningTotal = BigDecimal.ZERO;       
	        Map<String, Object> result =ServiceUtil.returnSuccess();
	        
	        if ( UtilValidate.isEmpty(boothIds)) {
	            Debug.logError("Booth Id's cannot be empty", module);
	            return ServiceUtil.returnError("Booth Id cannot be empty");        	
	        } 
	    	
	    	result = ServiceUtil.returnSuccess();
	    	if(!UtilValidate.isEmpty(boothIds)){    		
	    		for(int i=0 ; i< boothIds.size() ; i++){
	    			Map boothResult = getBoothDues(ctx,UtilMisc.<String, Object>toMap("boothId", 
	    					boothIds.get(i), "userLogin", userLogin)); 
	        		if (!ServiceUtil.isError(boothResult)) {
	        			Map boothDues = (Map)boothResult.get("boothDues");
	        			if ((Double)boothDues.get("amount") != 0) {
	        				boothRunningTotal = boothRunningTotal.add( new BigDecimal((Double)boothDues.get("amount")));
	        			}
	        		}
	    		}
	    		 result.put("boothRunningTotal", boothRunningTotal);
	    	}
	        return result;        
	    } 
	    /**
	     * Get route dues
	     * @param ctx the dispatch context
	     * @param context 
	     * @return a List of booths that have payments due for the given route
	     */
	    public static Map<String, Object> getRouteDues(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	
	    	
			String routeId = (String) context.get("routeId");    	
	        if (UtilValidate.isEmpty(routeId)) {
	            Debug.logError("Route Id cannot be empty", module);
	            return ServiceUtil.returnError("Route Id cannot be empty");        	
	        }
	    	List<Map<String, Object>> booths= FastList.newInstance();
	    	try {
	    		List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, routeId), null, UtilMisc.toList("facilityId"), null, false);

	    		for (GenericValue facility: facilities) {
	        		Map boothResult = getBoothDues(ctx,UtilMisc.<String, Object>toMap("boothId", 
	        				facility.getString("facilityId"), "userLogin", userLogin)); 
	        		if (!ServiceUtil.isError(boothResult)) {
	        			Map boothDues = (Map)boothResult.get("boothDues");
	        			if ((Double)boothDues.get("amount") != 0) {
	        				booths.add(boothDues);
	        			}
	        		}
	            }
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("boothList", booths);

	        return result;
	    }     
	    /**
	     * Get the sales order totals for the given period.  The totals are also segmented into products and zones for
	     * reporting purposes
	     * @param ctx the dispatch context
	     * @param context context map
	     * @return totals map
	     * 
	     * ::TODO:: consolidate DayTotals, PeriodTotals and DaywiseTotals functions
	     */
	    public static Map<String, Object> getPeriodTotals(DispatchContext ctx, Map<String, ? extends Object> context ) {
	    	Delegator delegator = ctx.getDelegator();
	        List<String> facilityIds = (List<String>) context.get("facilityIds");
	        List<String> shipmentIds = (List<String>) context.get("shipmentIds");
	        Timestamp fromDate = (Timestamp) context.get("fromDate");
	        if (UtilValidate.isEmpty(fromDate)) {
	            Debug.logError("fromDate cannot be empty", module);
	            return ServiceUtil.returnError("fromDate cannot be empty");        	
	        }        
	        Timestamp thruDate = (Timestamp) context.get("thruDate");  
	        if (UtilValidate.isEmpty(thruDate)) {
	            Debug.logError("thruDate cannot be empty", module);
	            return ServiceUtil.returnError("thruDate cannot be empty");        	
	        }           
	        String subscriptionType = (String) context.get("subscriptionType");
	        Boolean onlyVendorAndPTCBooths = (Boolean) context.get("onlyVendorAndPTCBooths");        
	    	List<GenericValue> orderItems= FastList.newInstance();
	    	Map productAttributes = new TreeMap<String, Object>();    
	    	List productSubscriptionTypeList = FastList.newInstance();
	    	Map<String , String> dayShipmentMap = FastMap.newInstance();
	    	try {
	    		List exprListForParameters = FastList.newInstance();
	    		exprListForParameters.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "FAT"));
	    		exprListForParameters.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SNF"));
	    		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.OR);    		
	    		List<GenericValue>  productAttribtutesList = delegator.findList("ProductAttribute", paramCond, null, null, null, false);
	            Iterator<GenericValue> productAttrIter = productAttribtutesList.iterator();
	        	while(productAttrIter.hasNext()) {
	                GenericValue productAttrItem = productAttrIter.next();        		
	        		if (!productAttributes.containsKey(productAttrItem.getString("productId"))) {
	        			productAttributes.put(productAttrItem.getString("productId"), new TreeMap<String, Object>());
	        		}
	        		Map value = (Map)productAttributes.get(productAttrItem.getString("productId"));
	        		value.put(productAttrItem.getString("attrName"), productAttrItem.getString("attrValue"));
	        	}
	        	
	        	productSubscriptionTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "SUB_PROD_TYPE"), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"), null, false);

	//Debug.logInfo("productAttributes=" + productAttributes, module);
	        	if (UtilValidate.isEmpty(shipmentIds)){
	        		shipmentIds = getShipmentIds(delegator, fromDate, thruDate);
	           	}
	    		
	        	// lets populate sales date shipmentId Map
	        	int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate))+1;
	        	for(int i=0 ; i< intervalDays ; i++){
	        		Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDate, i);
	        		List dayShipments = getShipmentIds(delegator, saleDate, saleDate);
	        		for(int j=0 ; j< dayShipments.size() ; j++){
	        			dayShipmentMap.put((String)dayShipments.get(j), UtilDateTime.toDateString(saleDate ,"yyyy-MM-dd"));
	        		}
	        	}
	//Debug.logInfo("salesDate=" + salesDate + "shipmentIds=" + shipmentIds, module);
	            List conditionList= FastList.newInstance(); 
	        	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	        	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));    		
	        	if (!UtilValidate.isEmpty(onlyVendorAndPTCBooths)){
	        		if (onlyVendorAndPTCBooths.booleanValue()) {
	        			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("categoryTypeEnum", "VENDOR"), EntityCondition.makeCondition("categoryTypeEnum", "PTC")));
	        		}
	        	}
	            if (!UtilValidate.isEmpty(facilityIds)) {
	            	conditionList.add(EntityCondition.makeCondition("originFacilityId",EntityOperator.IN, facilityIds));
	            }            
	        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		//Debug.logInfo("condition=" + condition, module);  
	        	if(!UtilValidate.isEmpty(shipmentIds)){        		
	        		orderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, null, null, false);
	        	}

	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	    	BigDecimal totalQuantity = ZERO;
	    	BigDecimal totalRevenue = ZERO;
	    	BigDecimal totalPacket = ZERO;
	    	BigDecimal totalFat = ZERO;
	    	BigDecimal totalSnf = ZERO;
	    	
	    	Map<String, Object> boothZoneMap = FastMap.newInstance();
	    	boothZoneMap = getAllBoothsZonesMap(delegator); 

	//Debug.logInfo("boothZoneMap=" + boothZoneMap, module);
	    	Map<String, Object> boothTotals = new TreeMap<String, Object>();
	    	//Map<String, Object> zoneTotals = new TreeMap<String, Object>();
	    	//Map<String, Object> distributorTotals = new TreeMap<String, Object>();
	    	Map<String, Object> productTotals = new TreeMap<String, Object>();
	    	Map<String, Object> supplyTypeTotals = new TreeMap<String, Object>();
	    	Map<String, Object> dayWiseTotals = new TreeMap<String, Object>();
	        Iterator<GenericValue> itemIter = orderItems.iterator();
	    	while(itemIter.hasNext()) {
	            GenericValue orderItem = itemIter.next();
	            String prodSubscriptionTypeId = orderItem.getString("productSubscriptionTypeId");
	            BigDecimal quantity  = orderItem.getBigDecimal("quantity");
	            BigDecimal packetQuantity  = orderItem.getBigDecimal("quantity");
	            BigDecimal price  = orderItem.getBigDecimal("unitListPrice"); 
	            BigDecimal revenue = price.multiply(quantity);
	            totalRevenue = totalRevenue.add(revenue);
	            totalPacket = totalPacket.add(packetQuantity);
	            quantity = quantity.multiply(orderItem.getBigDecimal("quantityIncluded"));
	    		totalQuantity = totalQuantity.add(quantity);   
	    		BigDecimal fat = ZERO;
	    		BigDecimal snf = ZERO;
	    		String productName = orderItem.getString("productName");
				String productId = orderItem.getString("productId");
				
				Map prodAttrMap = (Map)productAttributes.get(orderItem.getString("productId"));
	//Debug.logInfo("orderItem=" + orderItem, module); 
				if (prodAttrMap != null) {
					double fatPercent = Double.parseDouble((String)prodAttrMap.get("FAT"));
					fat = quantity.multiply(BigDecimal.valueOf(fatPercent));
					fat = fat.multiply(BigDecimal.valueOf(1.03));
					fat = fat.divide(BigDecimal.valueOf(100));   
					double snfPercent = Double.parseDouble((String)prodAttrMap.get("SNF"));
					snf = quantity.multiply(BigDecimal.valueOf(snfPercent));
					snf = snf.multiply(BigDecimal.valueOf(1.03));
					snf = snf.divide(BigDecimal.valueOf(100));  				
				}
	    		totalFat = totalFat.add(fat);   
	    		totalSnf = totalSnf.add(snf);     		
	    			
				Map zone = (Map)boothZoneMap.get(orderItem.getString("originFacilityId"));
				// Handle booth totals    			
				String boothId = orderItem.getString("originFacilityId");
				if (boothTotals.get(boothId) == null) {
					Map<String, Object> newMap = FastMap.newInstance();

					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("totalRevenue", revenue);
					newMap.put("excludeIncentive", orderItem.getString("excludeIncentive"));
					newMap.put("categoryTypeEnum", orderItem.getString("categoryTypeEnum"));
					Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
					Map<String, Object> iteratorMap = FastMap.newInstance();
			    	while(typeIter.hasNext()) {
			    		// initialize type maps
			            GenericValue type = typeIter.next();
	    				Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
	    				supplyTypeDetailsMap.put("name", type.getString("enumId"));
	    				supplyTypeDetailsMap.put("total", ZERO);
	    				supplyTypeDetailsMap.put("packetQuantity", ZERO);
	    				supplyTypeDetailsMap.put("totalRevenue", ZERO);
	    				iteratorMap.put(type.getString("enumId"), supplyTypeDetailsMap);
	    				newMap.put("supplyTypeTotals", iteratorMap);
	    			}
			    	Map supplyTypeMap =  (Map)newMap.get("supplyTypeTotals");
			    	Map supplyTypeDetailsMap = (Map)supplyTypeMap.get(prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeMap.put(prodSubscriptionTypeId, supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);
					
					Map<String, Object> productItemMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();

					productItemMap.put("name", productName);
					productSupplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
					productSupplyTypeDetailsMap.put("total", quantity);
					productSupplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					productSupplyTypeDetailsMap.put("totalRevenue", revenue);
					productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), productSupplyTypeDetailsMap);
					productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
	                productItemMap.put("total", quantity);
	                productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("totalRevenue", revenue);
					
					Map<String, Object> productMap = FastMap.newInstance();
					productMap.put(productId, productItemMap);
					newMap.put("productTotals", productMap);
					boothTotals.put(boothId, newMap);
				}
				else {
					Map boothMap = (Map)boothTotals.get(boothId);
					BigDecimal runningTotal = (BigDecimal)boothMap.get("total");
					runningTotal = runningTotal.add(quantity);
					BigDecimal runningPacketTotal = (BigDecimal)boothMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					
					boothMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal)boothMap.get("totalRevenue");
					
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					boothMap.put("totalRevenue", runningTotalRevenue);
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					boothMap.put("packetQuantity", runningPacketTotal);    			    				
					// next handle type totals
					Map tempMap = (Map)boothMap.get("supplyTypeTotals");
					Map typeMap = (Map)tempMap.get(prodSubscriptionTypeId);
					BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
					typeRunningTotal = typeRunningTotal.add(quantity);
					BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
					typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);
					
					BigDecimal typeRunningPacketTotal = (BigDecimal) typeMap.get("packetQuantity");
					typeRunningPacketTotal = typeRunningPacketTotal.add(packetQuantity);
					
					typeMap.put("name", prodSubscriptionTypeId);
					typeMap.put("total", typeRunningTotal);
					typeMap.put("packetQuantity", typeRunningPacketTotal);
					typeMap.put("totalRevenue", typeRunningTotalRevenue);
					// next handle product totals
					Map boothProductTotals = (Map)boothMap.get("productTotals");
					Map productMap = (Map)boothProductTotals.get(productId);
					if(UtilValidate.isEmpty(productMap)){
						Map<String, Object> productItemMap = FastMap.newInstance();
						Map<String, Object> supplyTypeMap = FastMap.newInstance();
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	    				productItemMap.put("name", productName);
	    				productItemMap.put("supplyTypeTotals", supplyTypeMap);
	    				productItemMap.put("total", quantity);
	    				productItemMap.put("packetQuantity", packetQuantity);
	    				productItemMap.put("totalRevenue", revenue);
	    				boothProductTotals.put(productId, productItemMap);
	    				
					}else{
						BigDecimal productRunningTotal = (BigDecimal)productMap.get("total");
						 productRunningTotal = productRunningTotal.add(quantity);
	    				productMap.put("total", productRunningTotal);
	    				BigDecimal productRunningTotalRevenue = (BigDecimal)productMap.get("totalRevenue");
	    				productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
	    				productMap.put("totalRevenue", productRunningTotalRevenue);
	    				
	    				BigDecimal productRunningPacketTotals = (BigDecimal)productMap.get("packetQuantity");
	    				productRunningPacketTotals = productRunningPacketTotals.add(packetQuantity);
	    				productMap.put("packetQuantity", productRunningPacketTotals);
	    				
	    				
	    				Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
	    				if(supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId") )!= null){
	    					Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
	    					BigDecimal runningTotalproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("total");
	        				runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
	        				BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("totalRevenue");
	        				runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);
	        				BigDecimal runningPacketTotalproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("packetQuantity");
	        				runningPacketTotalproductSubscriptionType = runningPacketTotalproductSubscriptionType.add(packetQuantity);
	        				
	        				supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	        				supplyTypeDetailsMap.put("total", runningTotalproductSubscriptionType);
	        				supplyTypeDetailsMap.put("packetQuantity", runningPacketTotalproductSubscriptionType);
	        				supplyTypeDetailsMap.put("totalRevenue", runningTotalRevenueproductSubscriptionType);
	        				supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	        				productMap.put("supplyTypeTotals", supplyTypeMap);
	        				boothProductTotals.put(productId, productMap);
	        				
	    				}else{
	    					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();	    					
	    					supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	    					supplyTypeDetailsMap.put("total", quantity);
	    					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
	    					supplyTypeDetailsMap.put("totalRevenue", revenue);
	    					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	    					productMap.put("supplyTypeTotals", supplyTypeMap);
	    					boothProductTotals.put(productId, productMap);
	    				}
	    			}
				}
				//handle dayWise Totals			 			
				String currentSaleDate = dayShipmentMap.get(orderItem.getString("shipmentId"));
				if (dayWiseTotals.get(currentSaleDate) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("totalRevenue", revenue); 
					newMap.put("excludeIncentive", orderItem.getString("excludeIncentive"));
					newMap.put("categoryTypeEnum", orderItem.getString("categoryTypeEnum"));
			        Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
			        Map<String, Object> iteratorMap = FastMap.newInstance();
			    	while(typeIter.hasNext()) {
			    		// initialize type maps
			            GenericValue type = typeIter.next();    				
	    				Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
	    				supplyTypeDetailsMap.put("name", type.getString("enumId"));
	    				supplyTypeDetailsMap.put("total", ZERO);
	    				supplyTypeDetailsMap.put("packetQuantity", ZERO);
	    				supplyTypeDetailsMap.put("totalRevenue", ZERO);
	    				iteratorMap.put(type.getString("enumId"), supplyTypeDetailsMap);
	    				newMap.put("supplyTypeTotals", iteratorMap);
					}
			    	Map supplyTypeMap =  (Map)newMap.get("supplyTypeTotals");
			    	Map supplyTypeDetailsMap = (Map)supplyTypeMap.get(prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeMap.put(prodSubscriptionTypeId, supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);
					
					Map<String, Object> productItemMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();
					productItemMap.put("name", productName);
					productSupplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
					productSupplyTypeDetailsMap.put("total", quantity);
					productSupplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					productSupplyTypeDetailsMap.put("totalRevenue", revenue);
					productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), productSupplyTypeDetailsMap);
					productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
	                productItemMap.put("total", quantity);
	                productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("totalRevenue", revenue);
					
					Map<String, Object> productMap = FastMap.newInstance();
					productMap.put(productId, productItemMap);
					newMap.put("productTotals", productMap);
					dayWiseTotals.put(currentSaleDate, newMap);
				}
				else {
					Map dayWiseMap = (Map)dayWiseTotals.get(currentSaleDate);
					BigDecimal runningTotal = (BigDecimal)dayWiseMap.get("total");
					runningTotal = runningTotal.add(quantity);
					dayWiseMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal)dayWiseMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					dayWiseMap.put("totalRevenue", runningTotalRevenue);
					BigDecimal runningPacketTotal = (BigDecimal)dayWiseMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					dayWiseMap.put("packetQuantity", runningPacketTotal);
					// next handle type totals
					Map tempMap = (Map)dayWiseMap.get("supplyTypeTotals");
					Map typeMap = (Map)tempMap.get(prodSubscriptionTypeId);
					BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
					typeRunningTotal = typeRunningTotal.add(quantity);
					BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
					typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);
					BigDecimal typeRunningPacketTotal = (BigDecimal) typeMap.get("packetQuantity");
					typeRunningPacketTotal = typeRunningPacketTotal.add(packetQuantity);
					
					typeMap.put("name", prodSubscriptionTypeId);
					typeMap.put("total", typeRunningTotal);
					typeMap.put("totalRevenue", typeRunningTotalRevenue);
					typeMap.put("packetQuantity", typeRunningPacketTotal);
					// next handle product totals
					Map dayWiseProductTotals = (Map)dayWiseMap.get("productTotals");
					Map productMap = (Map)dayWiseProductTotals.get(productId);
					
					if(UtilValidate.isEmpty(productMap)){
						Map<String, Object> productItemMap = FastMap.newInstance();
						Map<String, Object> supplyTypeMap = FastMap.newInstance();
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	    				productItemMap.put("name", productName);
	    				productItemMap.put("supplyTypeTotals", supplyTypeMap);
	    				productItemMap.put("total", quantity);
	    				productItemMap.put("packetQuantity", packetQuantity);
	    				productItemMap.put("totalRevenue", revenue);
	    				dayWiseProductTotals.put(productId, productItemMap);
	    				
					}else{
						BigDecimal productRunningTotal = (BigDecimal)productMap.get("total");
						 productRunningTotal = productRunningTotal.add(quantity);
	    				productMap.put("total", productRunningTotal);
	    				BigDecimal productRunningTotalRevenue = (BigDecimal)productMap.get("totalRevenue");
	    				productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
	    				productMap.put("totalRevenue", productRunningTotalRevenue);
	    				BigDecimal productRunningPacketTotal = (BigDecimal)productMap.get("packetQuantity");
	    				productRunningPacketTotal = productRunningPacketTotal.add(packetQuantity);
	    				productMap.put("packetQuantity", productRunningPacketTotal);
	    				
	    				Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
	    				if(supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId") )!= null){
	    					Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
	    					BigDecimal runningTotalproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("total");
	        				runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
	        				
	        				BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("totalRevenue");
	        				runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);
	        				
	        				BigDecimal runningTotalPacketSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("packetQuantity");
	        				runningTotalPacketSubscriptionType = runningTotalPacketSubscriptionType.add(packetQuantity);
	        				
	        				supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	        				supplyTypeDetailsMap.put("total", runningTotalproductSubscriptionType);
	        				supplyTypeDetailsMap.put("packetQuantity", runningTotalPacketSubscriptionType);
	        				supplyTypeDetailsMap.put("totalRevenue", runningTotalRevenueproductSubscriptionType);
	        				supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	        				productMap.put("supplyTypeTotals", supplyTypeMap);
	        				dayWiseProductTotals.put(productId, productMap);
	        				
	    				}else{
	    					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();	    					
	    					supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	    					supplyTypeDetailsMap.put("total", quantity);
	    					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
	    					supplyTypeDetailsMap.put("totalRevenue", revenue);
	    					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	    					productMap.put("supplyTypeTotals", supplyTypeMap);
	    					dayWiseProductTotals.put(productId, productMap);
	    				}
	    			}
				}	
				/*// Handle zone totals
				String zoneName = (String)zone.get("name");
				String zoneId = (String)zone.get("zoneId");
				if (zoneTotals.get(zoneId) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					newMap.put("name", zoneName);
					newMap.put("total", quantity);
					newMap.put("totalRevenue", revenue); 
			        Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
			    	while(typeIter.hasNext()) {
			    		// initialize type maps
			            GenericValue type = typeIter.next();    				
	    				Map<String, Object> typeMap = FastMap.newInstance();
	    				typeMap.put("total", ZERO);
	    				typeMap.put("totalRevenue", ZERO);      				
	    				newMap.put(type.getString("enumId"), typeMap);
					}
					Map typeMap = (Map)newMap.get(prodSubscriptionTypeId);
					typeMap.put("total", quantity);
					typeMap.put("totalRevenue", revenue);      				
					newMap.put(prodSubscriptionTypeId, typeMap);
					zoneTotals.put(zoneId, newMap);
				}
				else {
					Map zoneMap = (Map)zoneTotals.get(zoneId);
					BigDecimal runningTotal = (BigDecimal)zoneMap.get("total");
					runningTotal = runningTotal.add(quantity);
					zoneMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal)zoneMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					zoneMap.put("totalRevenue", runningTotalRevenue);    			    				
					// next handle type totals
					Map typeMap = (Map)zoneMap.get(prodSubscriptionTypeId);
					runningTotal = (BigDecimal) typeMap.get("total");
					runningTotal = runningTotal.add(quantity);
					typeMap.put("total", runningTotal);
					runningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					typeMap.put("totalRevenue", runningTotalRevenue);	
				}
				// Handle distributor totals
				//distributorTotals
				String distributorId = (String)zone.get("distributorId");    		
				if (distributorTotals.get(distributorId) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					try{
						GenericValue distributorDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", distributorId), false);
						
						newMap.put("name", distributorDetail.getString("facilityName"));
						newMap.put("total", quantity);
	    				newMap.put("totalRevenue", revenue); 
	    		        Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
	    		    	while(typeIter.hasNext()) {
	    		    		// initialize type maps
	    		            GenericValue type = typeIter.next();    				
	        				Map<String, Object> typeMap = FastMap.newInstance();
	        				typeMap.put("total", ZERO);
	        				typeMap.put("totalRevenue", ZERO);      				
	        				newMap.put(type.getString("enumId"), typeMap);
	    				}
	    				Map typeMap = (Map)newMap.get(prodSubscriptionTypeId);
	    				typeMap.put("total", quantity);
	    				typeMap.put("totalRevenue", revenue);      				
	    				newMap.put(prodSubscriptionTypeId, typeMap);
	    				distributorTotals.put(distributorId, newMap);
					} catch (GenericEntityException e) {
						// TODO: handle exception
						 Debug.logError(e, module);
					} 				
					
				}
				else {
					Map distributorMap = (Map)distributorTotals.get(distributorId);
					BigDecimal runningTotal = (BigDecimal)distributorMap.get("total");
					runningTotal = runningTotal.add(quantity);
					distributorMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal)distributorMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					distributorMap.put("totalRevenue", runningTotalRevenue);
					// next handle type totals
					Map typeMap = (Map)distributorMap.get(prodSubscriptionTypeId);
					runningTotal = (BigDecimal) typeMap.get("total");
					runningTotal = runningTotal.add(quantity);
					typeMap.put("total", runningTotal);
					runningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					typeMap.put("totalRevenue", runningTotalRevenue);	    				
				}*/
				// Handle product totals
				if (productTotals.get(productId) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					newMap.put("name", productName);
					Map<String, Object> supplyTypeMap = FastMap.newInstance();
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);
					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("totalRevenue", revenue);
					newMap.put("totalFat", fat);
					newMap.put("totalSnf", snf);
					productTotals.put(productId, newMap);
				}
				else {
					Map productMap = (Map)productTotals.get(productId);
					BigDecimal runningTotal = (BigDecimal)productMap.get("total");
					runningTotal = runningTotal.add(quantity);
					productMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal)productMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					productMap.put("totalRevenue", runningTotalRevenue);
					BigDecimal runningPacketTotal = (BigDecimal)productMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					productMap.put("packetQuantity", runningPacketTotal);
					
					BigDecimal runningTotalFat = (BigDecimal)productMap.get("totalFat");
					runningTotalFat = runningTotalFat.add(fat);
					productMap.put("totalFat", runningTotalFat);
					BigDecimal runningTotalSnf = (BigDecimal)productMap.get("totalSnf");
					runningTotalSnf = runningTotalSnf.add(snf);
					productMap.put("totalSnf", runningTotalSnf);
					Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
					if(supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId") )!= null){
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap = (Map<String, Object>) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
						BigDecimal runningTotalproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("total");
						BigDecimal runningPacketTotalSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("packetQuantity");
						BigDecimal runningRevenueproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("totalRevenue");
						runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
	    				runningRevenueproductSubscriptionType = runningRevenueproductSubscriptionType.add(revenue);
	    				runningPacketTotalSubscriptionType = runningPacketTotalSubscriptionType.add(packetQuantity);
	    				supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	    				supplyTypeDetailsMap.put("total", runningTotalproductSubscriptionType);
	    				supplyTypeDetailsMap.put("packetQuantity", runningPacketTotalSubscriptionType);
	    				supplyTypeDetailsMap.put("totalRevenue", runningRevenueproductSubscriptionType);
	    				supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
	    				productMap.put("supplyTypeTotals", supplyTypeMap);
					}else{
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
						productMap.put("supplyTypeTotals", supplyTypeMap);
					}

				}
				// Handle supply type totals
				if (supplyTypeTotals.get(prodSubscriptionTypeId) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					newMap.put("name", prodSubscriptionTypeId);
					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("totalRevenue", revenue); 
					supplyTypeTotals.put(prodSubscriptionTypeId, newMap);
				}
				else {
					Map supplyTypeMap = (Map)supplyTypeTotals.get(prodSubscriptionTypeId);
					BigDecimal runningTotal = (BigDecimal)supplyTypeMap.get("total");
					runningTotal = runningTotal.add(quantity);
					supplyTypeMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal)supplyTypeMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					supplyTypeMap.put("totalRevenue", runningTotalRevenue);  
					BigDecimal runningPacketTotal = (BigDecimal)supplyTypeMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					supplyTypeMap.put("packetQuantity", runningPacketTotal);  
				}
			}
	    	  	
			totalQuantity = totalQuantity.setScale(decimals, rounding);  
			totalRevenue = totalRevenue.setScale(decimals, rounding);
			totalPacket = totalPacket.setScale(decimals, rounding);
			totalFat = totalFat.setScale(decimals, rounding);    
			totalSnf = totalSnf.setScale(decimals, rounding);    
			
			/*// set scale
	        for ( Map.Entry<String, Object> entry : zoneTotals.entrySet() ) {
	        	Map<String, Object> zoneValue = (Map<String, Object>)entry.getValue();
	        	BigDecimal tempVal = (BigDecimal)zoneValue.get("total");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	zoneValue.put("total", tempVal);
	        	tempVal = (BigDecimal)zoneValue.get("totalRevenue");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	zoneValue.put("totalRevenue", tempVal);	  
		        Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
		    	while(typeIter.hasNext()) {
		    		// initialize type maps
		            GenericValue type = typeIter.next();    				
					Map<String, Object> typeMap = (Map)zoneValue.get(type.getString("enumId"));
		        	BigDecimal tempVal2 = (BigDecimal)typeMap.get("total"); 
		        	tempVal2 = tempVal2.setScale(decimals, rounding);     	        	
					typeMap.put("total", tempVal2);
					tempVal2 = (BigDecimal)typeMap.get("totalRevenue"); 
		        	tempVal2 = tempVal2.setScale(decimals, rounding); 
					typeMap.put("totalRevenue", tempVal2);      				
				}	        	
	        }
	        for ( Map.Entry<String, Object> entry : distributorTotals.entrySet() ) {
	        	Map<String, Object> distributorValue = (Map<String, Object>)entry.getValue();
	        	BigDecimal tempVal = (BigDecimal)distributorValue.get("total");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	distributorValue.put("total", tempVal);
	        	tempVal = (BigDecimal)distributorValue.get("totalRevenue");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	distributorValue.put("totalRevenue", tempVal);
		        Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
		    	while(typeIter.hasNext()) {
		    		// initialize type maps
		            GenericValue type = typeIter.next();    				
					Map<String, Object> typeMap = (Map)distributorValue.get(type.getString("enumId"));
		        	BigDecimal tempVal2 = (BigDecimal)typeMap.get("total"); 
		        	tempVal2 = tempVal2.setScale(decimals, rounding);     	        	
					typeMap.put("total", tempVal2);
					tempVal2 = (BigDecimal)typeMap.get("totalRevenue"); 
		        	tempVal2 = tempVal2.setScale(decimals, rounding); 
					typeMap.put("totalRevenue", tempVal2);      				
				}		        	
	        }	   */     
	        for ( Map.Entry<String, Object> entry : productTotals.entrySet() ) {
	        	Map<String, Object> productValue = (Map<String, Object>)entry.getValue();
	        	BigDecimal tempVal = (BigDecimal)productValue.get("total");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	productValue.put("total", tempVal);
	        	
	        	tempVal = (BigDecimal)productValue.get("packetQuantity");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	productValue.put("packetQuantity", tempVal);
	        	
	        	tempVal = (BigDecimal)productValue.get("totalRevenue");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	productValue.put("totalRevenue", tempVal);	    
	        	tempVal = (BigDecimal)productValue.get("totalFat");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	productValue.put("totalFat", tempVal);	
	        	tempVal = (BigDecimal)productValue.get("totalSnf");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	productValue.put("totalSnf", tempVal);		        	
	        }
	        for ( Map.Entry<String, Object> entry : supplyTypeTotals.entrySet() ) {
	        	Map<String, Object> supplyTypeValue = (Map<String, Object>)entry.getValue();
	        	BigDecimal tempVal = (BigDecimal)supplyTypeValue.get("total");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	supplyTypeValue.put("total", tempVal);
	        	
	        	tempVal = (BigDecimal)supplyTypeValue.get("packetQuantity");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	supplyTypeValue.put("packetQuantity", tempVal);
	        	
	        	tempVal = (BigDecimal)supplyTypeValue.get("totalRevenue");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	supplyTypeValue.put("totalRevenue", tempVal);	    	        	
	        }	        
			Map<String, Object> result = FastMap.newInstance();        
	        result.put("totalQuantity", totalQuantity);
	        result.put("totalRevenue", totalRevenue);
	        result.put("totalPacket", totalPacket);
	        result.put("totalFat", totalFat);   
	        result.put("totalSnf", totalSnf);                
	        //result.put("zoneTotals", zoneTotals);
	        result.put("boothTotals", boothTotals);
	        result.put("dayWiseTotals", dayWiseTotals);
	        //result.put("distributorTotals", distributorTotals);
	        result.put("productTotals", productTotals);      
	        result.put("supplyTypeTotals", supplyTypeTotals);                
	        return result;
	    } 

	    public static Map<String, Object> getFacilityGroupMemberList(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			String facilityGroupId = (String) context.get("facilityGroupId");
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			if(UtilValidate.isEmpty(fromDate)){
				fromDate = UtilDateTime.nowTimestamp();
			}
	        Map<String, Object> result = FastMap.newInstance(); 
	        List facilityIds = FastList.newInstance();
	        List<GenericValue> groupFacilityList = FastList.newInstance();
	        try {
	        	groupFacilityList = delegator.findList("FacilityGroupMemberAndFacility", EntityCondition.makeCondition("facilityGroupId" ,EntityOperator.EQUALS , facilityGroupId), null , null, null, false);
	        	groupFacilityList = EntityUtil.filterByDate(groupFacilityList ,fromDate);
	        	if (UtilValidate.isEmpty(groupFacilityList)) {
	        		result.put("facilityIds", facilityIds);
	                return result;         		
	        	}
	        	for(GenericValue facility : groupFacilityList){
	        		if(facility.getString("facilityTypeId").equals("ZONE")){
	        			facilityIds.addAll(getZoneBooths(delegator, facility.getString("facilityId")));
	        		}
	        		if(facility.getString("facilityTypeId").equals("ROUTE")){
	        			facilityIds.addAll(getRouteBooths(delegator, facility.getString("facilityId")));
	        		}
	        		if(facility.getString("facilityTypeId").equals("BOOTH")){
	        			facilityIds.add(facility.getString("facilityId"));
	        		}
	        	}
	        }
	        catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());          	
	        }    
	        
	        result.put("facilityIds", facilityIds);
	        return result;
	    }
	    
	    public static Map<String, Object> getFacilityByCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	
			String categoryTypeEnum = (String) context.get("categoryTypeEnum");
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			if(UtilValidate.isEmpty(fromDate)){
				fromDate = UtilDateTime.nowTimestamp();
			}
			List conditionList = FastList.newInstance();
			Map result = ServiceUtil.returnSuccess();
			try {
				if(UtilValidate.isNotEmpty(categoryTypeEnum)){
					conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, categoryTypeEnum));
				}
				conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate), EntityOperator.OR, EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null)));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
	        	List<GenericValue> facilities = delegator.findList("Facility", condition, null, null, null, false);
	        	
	        	List facilityIds = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", true);
	        	
	        	result.put("facilityIds", facilityIds);
	        	result.put("facilityDetails", facilities);
	        }
	        catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());          	
	        }    
	        
	        return result;
	    }
	    
	    public static Map<String, Object> getFacilityGroupDetail(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			String facilityGroupId = (String) context.get("facilityGroupId");
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			if(UtilValidate.isEmpty(fromDate)){
				fromDate = UtilDateTime.nowTimestamp();
			}
	        Map<String, Object> result = FastMap.newInstance(); 
	        List facilityIds = FastList.newInstance();       
	        try {
	        	facilityIds = (List)getFacilityGroupMemberList(ctx, UtilMisc.toMap("facilityGroupId",facilityGroupId ,"fromDate" , fromDate)).get("facilityIds");;
	        	if (UtilValidate.isEmpty(facilityIds)) {
	        		result.put("facilityIds", facilityIds);
	        		result.put("routeList", FastList.newInstance());
	        		result.put("zoneList", FastList.newInstance());
	                return result;         		
	        	} 
	        	List<GenericValue> facilityList = delegator.findList("Facility",EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds) , null, UtilMisc.toList("parentFacilityId"), null, false);
	        	List<GenericValue> routesfacilityList = delegator.findList("Facility",EntityCondition.makeCondition("facilityId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(facilityList, "parentFacilityId", true)) , null, UtilMisc.toList("parentFacilityId"), null, false);
	        	List<GenericValue> zonefacilityList = delegator.findList("Facility",EntityCondition.makeCondition("facilityId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(routesfacilityList, "parentFacilityId", true)) , null, UtilMisc.toList("parentFacilityId"), null, false);
	        	
	        	result.put("routeList", routesfacilityList);
	    		result.put("zoneList", zonefacilityList);   	
	        	
	        }
	        catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());          	
	        }    
	        
	        result.put("facilityIds", facilityIds);
	        return result;
	    }
	    public static Map<String, Object> getFacilityGroupDetailByOwnerFacility(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			String facilityId = (String) context.get("facilityId");
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			if(UtilValidate.isEmpty(fromDate)){
				fromDate = UtilDateTime.nowTimestamp();
			}
	        Map<String, Object> result = FastMap.newInstance(); 
	        List facilityIds = FastList.newInstance();
	       
	        try {
	        	
	        	GenericValue facilityGroup = EntityUtil.getFirst(delegator.findByAnd("FacilityGroup",UtilMisc.toMap("facilityGroupTypeId" ,"DAIRY_LMD_TYPE" ,"ownerFacilityId" , facilityId)));
	        	if(UtilValidate.isEmpty(facilityGroup)){
	        		return result;
	        	}
	        	Map<String, Object> groupDetail = getFacilityGroupDetail(ctx, UtilMisc.toMap("facilityGroupId", facilityGroup.getString("facilityGroupId"),"fromDate" , fromDate));   	
	        	result.putAll(groupDetail);
	        }
	        catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());          	
	        }
	        
	        return result;
	    }
	    public static Map<String, Object> getFacilityIndentQtyCategories(DispatchContext ctx, Map<String, ? extends Object> context) {
	        Map<String, Object> result = FastMap.newInstance();
	        Delegator delegator = ctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");          
	        String facilityId = (String) context.get("facilityId");
	        Timestamp supplyDate = (Timestamp) context.get("supplyDate");
	        if (UtilValidate.isEmpty(supplyDate)) {
	            supplyDate = UtilDateTime.nowTimestamp();      	
	        }        
	        Timestamp dayBegin =UtilDateTime.getDayStart(supplyDate);
	        Map<String, String> indentQtyCategory = FastMap.newInstance();
	        List<GenericValue> productList =FastList.newInstance();
	    	List<GenericValue> productCategory = FastList.newInstance();
	    	List productCategoryIds = FastList.newInstance();
	    	Map qtyInPiecesMap = FastMap.newInstance();
	    	BigDecimal crateLtrQty = BigDecimal.ZERO;
	        try{
	        	productCategory = delegator.findList("ProductCategory", EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "PACKING_PRODUCT"), UtilMisc.toSet("productCategoryId"), null, null, false);
	        	productCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);

	        	List condList =FastList.newInstance();
	        	condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	        	/*condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	    			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
	        	*/EntityCondition productsListCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
	        	List<String> orderBy = UtilMisc.toList("sequenceNum");
	    	
	    		productList =delegator.findList("ProductAndCategoryMember", productsListCondition,UtilMisc.toSet("productId", "productCategoryId"),orderBy, null, false);
	    		
	    		Map resultQtyMap = (Map)getProductCratesAndCans(ctx, UtilMisc.toMap("userLogin", userLogin));
		    	Map cratesMap = (Map)resultQtyMap.get("piecesPerCrate");
		    	Map cansMap = (Map)resultQtyMap.get("piecesPerCan");
		    	
	    		for(GenericValue productCat : productList){
	    			indentQtyCategory.put(productCat.getString("productId"), productCat.getString("productCategoryId"));
	    			if(UtilValidate.isNotEmpty(cratesMap) && UtilValidate.isNotEmpty(cratesMap.get(productCat.getString("productId")))){
	    				qtyInPiecesMap.put(productCat.getString("productId"), (BigDecimal)cratesMap.get(productCat.getString("productId")));
	    			}
	    			if(UtilValidate.isNotEmpty(cansMap) && UtilValidate.isNotEmpty(cansMap.get(productCat.getString("productId")))){
	    				qtyInPiecesMap.put(productCat.getString("productId"), (BigDecimal)cansMap.get(productCat.getString("productId")));
	    			}
	            }
	    		
	    		/*List<GenericValue> facWiseProdCat = delegator.findList("FacilityWiseProductCategory", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), null, null, null, false);
	    		facWiseProdCat = EntityUtil.filterByDate(facWiseProdCat, dayBegin);
	    		for(GenericValue facilityProdCat : facWiseProdCat){
	    			indentQtyCategory.put(facilityProdCat.getString("productId"), facilityProdCat.getString("productCategoryId"));
	            }*/
	    		
	    		/*GenericValue uomCrateConversion = delegator.findOne("UomConversion", UtilMisc.toMap("uomId","VLIQ_CRT", "uomIdTo", "VLIQ_L"),false);
	    		if(UtilValidate.isNotEmpty(uomCrateConversion)){
		    		crateLtrQty = new BigDecimal(uomCrateConversion.getDouble("conversionFactor"));
		    	}*/
	    		
	    	}catch (Exception e) {
	    		Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}
	    	result.put("qtyInPieces", qtyInPiecesMap);
	    	result.put("indentQtyCategory", indentQtyCategory);
	        return result;
	    }
	    /*public static BigDecimal convertPacketsToCrates(BigDecimal quantityIncluded , BigDecimal packetQuantity){
	    	BigDecimal crateQuantity = BigDecimal.ZERO;
	    	crateQuantity = (packetQuantity.multiply(quantityIncluded)).divide(new BigDecimal(12), 2 ,rounding);
	    	return crateQuantity;
	    }*/
	    public static BigDecimal convertPacketsToCrates(DispatchContext dctx, Map<String, ? extends Object> context){
	    	Delegator delegator = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map result = ServiceUtil.returnSuccess();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String productId = (String)context.get("productId");
			BigDecimal packetQuantity = (BigDecimal)context.get("packetQuantity");    
	    	BigDecimal crateQuantity = BigDecimal.ZERO;
	    	List productIds = FastList.newInstance();
	    	productIds.add(productId);
	    	result = (Map)getProductCratesAndCans(dctx, UtilMisc.toMap("userLogin", userLogin, "productIds", productIds));
	    	Map piecesPerCrate = (Map)result.get("piecesPerCrate");
	    	Map piecesPerCan = (Map)result.get("piecesPerCan");
		    if(UtilValidate.isNotEmpty(piecesPerCrate) && UtilValidate.isNotEmpty(piecesPerCrate.get(productId))){
		    	BigDecimal packetPerCrate = (BigDecimal)piecesPerCrate.get(productId);
		    	crateQuantity = packetQuantity.divide(packetPerCrate, 2 ,rounding);
		    }
		    if(UtilValidate.isNotEmpty(piecesPerCan) && UtilValidate.isNotEmpty(piecesPerCan.get(productId))){
		    	BigDecimal packetPerCan = (BigDecimal)piecesPerCan.get(productId);
		    	crateQuantity = packetQuantity.divide(packetPerCan, 2 ,rounding);
		    }
	    	return crateQuantity;
	    }
	    /*public static BigDecimal convertCratesToPackets(BigDecimal quantityIncluded , BigDecimal crateQuantity){
	    	BigDecimal packetQuantity = BigDecimal.ZERO;
	    	packetQuantity = ((new BigDecimal(12)).divide(quantityIncluded, 2, rounding)).multiply(crateQuantity);
	    	return packetQuantity;
	    }*/
	    public static BigDecimal convertCratesToPackets(DispatchContext dctx, Map<String, ? extends Object> context){
	    	Delegator delegator = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map result = ServiceUtil.returnSuccess();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String productId = (String)context.get("productId");
			BigDecimal crateQuantity = (BigDecimal)context.get("crateQuantity");    
	    	BigDecimal packetQuantity = BigDecimal.ZERO;
	    	
	    	List productIds = FastList.newInstance();
	    	productIds.add(productId);
	    	result = (Map)getProductCratesAndCans(dctx, UtilMisc.toMap("userLogin", userLogin, "productIds", productIds));
	    	Map piecesPerCrate = (Map)result.get("piecesPerCrate");
	    	Map piecesPerCan = (Map)result.get("piecesPerCan");
		    if(UtilValidate.isNotEmpty(piecesPerCrate) && UtilValidate.isNotEmpty(piecesPerCrate.get(productId))){
		    	BigDecimal packetPerCrate = (BigDecimal)piecesPerCrate.get(productId);
		    	packetQuantity = (packetPerCrate.multiply(crateQuantity)).setScale(0, BigDecimal.ROUND_HALF_UP);
		    }
		    if(UtilValidate.isNotEmpty(piecesPerCan) && UtilValidate.isNotEmpty(piecesPerCan.get(productId))){
		    	BigDecimal packetPerCan = (BigDecimal)piecesPerCan.get(productId);
		    	packetQuantity = (packetPerCan.multiply(crateQuantity)).setScale(0, BigDecimal.ROUND_HALF_UP);
		    }
	    	return packetQuantity;
	    }
	    
	    public static Map getOpeningBalanceInvoices(DispatchContext dctx, Map<String, ? extends Object> context){
	    	//Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues ,boolean isPendingDues){
			//TO DO:for now getting one shipment id  we need to get pmand am shipment id irrespective of Shipment type Id
		    Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
		    Map result = ServiceUtil.returnSuccess();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    String facilityId = (String) context.get("facilityId");
		    Timestamp fromDate =  (Timestamp)context.get("fromDate");
		    Timestamp thruDate =  (Timestamp)context.get("thruDate");
		    
		    List<GenericValue> pendingOBInvoiceList =  FastList.newInstance();
			List exprList = FastList.newInstance();
			List categoryTypeEnumList = UtilMisc.toList("SO_INST","CR_INST");
			boolean enableSoCrPmntTrack = Boolean.FALSE;
			try{
				 GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSoCrPmntTrack"), true);
				 if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack) && (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
					 enableSoCrPmntTrack = Boolean.TRUE;
				 	} 
			}catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
				}			
			if(!enableSoCrPmntTrack){
				//exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
				exprList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_IN, categoryTypeEnumList));
		    }
			if(UtilValidate.isNotEmpty(facilityId)){
				exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, getBoothList(delegator ,facilityId)));	
			}
			
			if(UtilValidate.isNotEmpty(fromDate)){
				exprList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));	
			}
			if(UtilValidate.isNotEmpty(thruDate)){
				exprList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));	
			}
			exprList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, obInvoiceType));		
			List invoiceStatusList = UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF");		
			exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,invoiceStatusList));
			
			EntityCondition	cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);				
			try{
				pendingOBInvoiceList = delegator.findList("InvoiceAndFacility", cond, null ,null, null, false);
			}catch (Exception e) {
				// TODO: handle exception
				
			}
			List<GenericValue> tempObInvoiceList =  FastList.newInstance();
			
			for (GenericValue obInvoice : pendingOBInvoiceList) {
				GenericValue tempObInvoice = delegator.makeValue("OrderHeaderFacAndItemBillingInv");
				//tempObInvoice.putAll(obInvoice);
				tempObInvoice.put("parentFacilityId", obInvoice.getString("parentFacilityId"));
				tempObInvoice.put("invoiceId", obInvoice.getString("invoiceId"));
				tempObInvoice.put("originFacilityId", obInvoice.getString("facilityId"));
				tempObInvoice.put("facilityName", obInvoice.getString("facilityName"));
				tempObInvoice.put("estimatedDeliveryDate", UtilDateTime.getDayStart(obInvoice.getTimestamp("invoiceDate")));
				tempObInvoiceList.add(tempObInvoice);
			}
					
			result.put("invoiceList", tempObInvoiceList);
			result.put("invoiceIds", EntityUtil.getFieldListFromEntityList(pendingOBInvoiceList, "invoiceId", true));	   
			return result;
		    
	    }
	    
	    public static List<GenericValue> getAllLmsAndByProdProducts(DispatchContext dctx, Map<String, ? extends  Object> context){
	      	 Timestamp salesDate = UtilDateTime.nowTimestamp();    	
	           Delegator delegator = dctx.getDelegator();
	           LocalDispatcher dispatcher = dctx.getDispatcher();
	           if(!UtilValidate.isEmpty(context.get("salesDate"))){
	          	salesDate =  (Timestamp) context.get("salesDate");  
	           }
	          Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
	      	List<GenericValue> productList =FastList.newInstance();
	      	List condList =FastList.newInstance();
	      	condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, UtilMisc.toList("LMS","BYPROD")));
	      	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	      			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
	      	EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
	      	  List<String> orderBy = UtilMisc.toList("sequenceNum");
	      	try{
	      		productList =delegator.findList("ProductAndCategoryMember", discontinuationDateCondition,null,orderBy, null, false);
	      	}catch (GenericEntityException e) {
	   			// TODO: handle exception
	      		Debug.logError(e, module);
	   		} 
	      	return productList;
	   	}
	    
	    public static Map calculateCratesForShipment(DispatchContext dctx, Map<String, ? extends Object> context){
	    	//Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues ,boolean isPendingDues){
			//TO DO:for now getting one shipment id  we need to get pmand am shipment id irrespective of Shipment type Id
		    Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
		    Map result = ServiceUtil.returnSuccess();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    String shipmentId = (String) context.get("shipmentId");
		    List exprList = FastList.newInstance();
		    Timestamp shipDate = null;
		    try{
		    	GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
		    	if(UtilValidate.isEmpty(shipment)){
		    		Debug.logError("Valid shipmentId should be given", module);
		     		return ServiceUtil.returnError("Valid shipmentId should be given");
		    	}
		    	shipDate = shipment.getTimestamp("estimatedShipDate");
		    }catch(GenericEntityException e){
		    	Debug.logError("Error fetching shipment details", module);
	    		return ServiceUtil.returnError("Error fetching shipment details"); 
		    }
		    Map resultCrates = (Map)getProductCratesAndCans(dctx, UtilMisc.toMap("userLogin",userLogin, "saleDate", shipDate));
		    
		    Map piecesPerCrate = (Map)resultCrates.get("piecesPerCrate");
		    Map piecesPerCan = (Map)resultCrates.get("piecesPerCan");
		    
		    List facilityList = FastList.newInstance();
			exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			EntityCondition	cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);				
			
			try{
				List<GenericValue> shippedOrderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", cond, null ,null, null, false);
				facilityList = EntityUtil.getFieldListFromEntityList(shippedOrderItems, "originFacilityId", true);
				String facilityId = "";
				BigDecimal totalCrateQty = BigDecimal.ZERO;
				BigDecimal totalCanQty = BigDecimal.ZERO;
				Map partyCratesMap = FastMap.newInstance();
				Map partyCansMap = FastMap.newInstance();
				Map productQtyInc = FastMap.newInstance();
				for(int i=0;i<facilityList.size();i++){
					facilityId = (String)facilityList.get(i);
					List<GenericValue> facilityOrderItems = EntityUtil.filterByCondition(shippedOrderItems, EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
					int partyCrates = 0;
					BigDecimal totalQty = BigDecimal.ZERO;
					Map prodQtyMap = FastMap.newInstance();
					String productId = "";
					for(GenericValue orderItem : facilityOrderItems){
						BigDecimal qtyInc = orderItem.getBigDecimal("quantityIncluded");
						productId = orderItem.getString("productId");
						BigDecimal qty = orderItem.getBigDecimal("quantity");
						productQtyInc.put(productId, qtyInc);
						if(prodQtyMap.containsKey(productId)){
							BigDecimal tempQty = (BigDecimal)prodQtyMap.get(productId);
							tempQty = tempQty.add(qty);
							prodQtyMap.put(productId, tempQty);
						}
						else{
							prodQtyMap.put(productId, qty);
						}
					}
					int partyTotalCrate = 0;
					int partyTotalCan = 0;
					Iterator prodQtyIter = prodQtyMap.entrySet().iterator();
					BigDecimal totalCalCrates = BigDecimal.ZERO;
					BigDecimal totalCalCans = BigDecimal.ZERO;
					while (prodQtyIter.hasNext()) {
						Map.Entry prodQtyEntry = (Entry) prodQtyIter.next();
			        	productId = (String)prodQtyEntry.getKey();
			        	BigDecimal qty = (BigDecimal)prodQtyEntry.getValue();
			        	BigDecimal prodCrates = BigDecimal.ZERO;
			        	BigDecimal prodCans = BigDecimal.ZERO;
			        	//calculate crates
			        	if(UtilValidate.isNotEmpty(piecesPerCrate) && piecesPerCrate.containsKey(productId)){
			        		prodCrates = (qty.divide((BigDecimal)piecesPerCrate.get(productId),2,BigDecimal.ROUND_HALF_UP));
			        	}
			        	BigDecimal tempTotalCrates = ((new BigDecimal(partyTotalCrate)).add(prodCrates));
			        	partyTotalCrate = ((new BigDecimal(partyTotalCrate)).add(prodCrates)).intValue();
			        	totalCalCrates = totalCalCrates.add(tempTotalCrates);
			        	
			        	//calculate cans
			        	if(UtilValidate.isNotEmpty(piecesPerCan) && piecesPerCan.containsKey(productId)){
			        		prodCans = (qty.divide((BigDecimal)piecesPerCan.get(productId),2,BigDecimal.ROUND_HALF_UP));
			        	}
			        	BigDecimal tempTotalCans = ((new BigDecimal(partyTotalCan)).add(prodCans));
			        	partyTotalCan = ((new BigDecimal(partyTotalCrate)).add(prodCrates)).intValue();
			        	totalCalCans = totalCalCans.add(tempTotalCans);
			        	
					}
					partyCratesMap.put(facilityId, partyTotalCrate);
					totalCrateQty = totalCrateQty.add(totalCalCrates);
					
					partyCansMap.put(facilityId, partyTotalCan);
					totalCanQty = totalCanQty.add(totalCalCans);
				}
				BigDecimal totCrate = (totalCrateQty).setScale(0, BigDecimal.ROUND_CEILING);
				BigDecimal totCan = (totalCanQty).setScale(0, BigDecimal.ROUND_CEILING);
				partyCansMap.put("totalCans", totCan);
				result.put("shipmentCans", partyCansMap);
				
				partyCratesMap.put("totalCrates", totCrate);
				result.put("shipmentCrates", partyCratesMap);
			}catch (Exception e) {
				Debug.logError("Error calculating crates for the shipment", module);
	    		return ServiceUtil.returnError("Error calculating crates for the shipment"); 
			}
			return result;
	    }
	    
	    public static Map<String, Object> getFacilityRateAmount(DispatchContext dctx, Map<String, ? extends Object> context){
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String facilityId = (String) context.get("facilityId");
	        String rateTypeId = (String) context.get("rateTypeId");
	        Timestamp fromDate = (Timestamp) context.get("fromDate");
	        GenericValue userLogin =(GenericValue)context.get("userLogin");
	        
	        String rateCurrencyUomId = "INR";
	        if(UtilValidate.isNotEmpty(context.get("rateCurrencyUomId"))){
	        	rateCurrencyUomId = (String)context.get("rateCurrencyUomId");
	        }
	        // if from date is null then lets take now timestamp as default 
	        if(UtilValidate.isEmpty(fromDate)){
	        	fromDate = UtilDateTime.nowTimestamp();
	        }
	        Map result = ServiceUtil.returnSuccess();
	        BigDecimal rateAmount = BigDecimal.ZERO;
	        String uomId = "";
	        //lets get the active rateAmount
	        List facilityRates = FastList.newInstance();
	        List exprList = FastList.newInstance();
	        //facility level 
	        exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	        exprList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
	        exprList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
	        
	        EntityCondition	paramCond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	        try{			
	        	facilityRates = delegator.findList("FacilityRate", paramCond, null , null, null, false);
				
			}catch(GenericEntityException e){
				Debug.logError(e, module);	
	            return ServiceUtil.returnError(e.toString());			
			}
			try{
				facilityRates = EntityUtil.filterByDate(facilityRates,fromDate);
				//if no rates at facility level then, lets check for default rate
				if(UtilValidate.isEmpty(facilityRates)){
					exprList.clear();
					//Default level
					exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, "_NA_"));
					exprList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
			        exprList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
			        
			        EntityCondition	cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
			        try{			
			        	facilityRates = delegator.findList("FacilityRate", paramCond, null , null, null, false);
						
					}catch(GenericEntityException e){
						Debug.logError(e, module);	
			            return ServiceUtil.returnError(e.toString());			
					}
				}			
				
				GenericValue validFacilityRate= EntityUtil.getFirst(facilityRates);
				if(UtilValidate.isNotEmpty(validFacilityRate)){
					if(UtilValidate.isNotEmpty(validFacilityRate.getString("acctgFormulaId"))){
						String acctgFormulaId =  validFacilityRate.getString("acctgFormulaId");
						BigDecimal slabAmount = (BigDecimal) context.get("slabAmount");
						uomId=validFacilityRate.getString("uomId");
						if(UtilValidate.isEmpty(slabAmount)){						
							slabAmount = BigDecimal.ZERO;
							Debug.logWarning("no slab amount found for acctgFormulaId taking zero as default ", module);
						}
						Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "acctgFormulaId",acctgFormulaId, "variableValues","QUANTITY="+"1", "slabAmount", slabAmount);
		    			Map<String, Object> incentivesResult = dispatcher.runSync("evaluateAccountFormula", input);
		        		if (ServiceUtil.isError(incentivesResult)) {
		        			Debug.logError("unable to evaluate AccountFormula"+acctgFormulaId, module);	
		                    return ServiceUtil.returnError("unable to evaluate AccountFormula"+acctgFormulaId);	
		                }
		        		double formulaValue = (Double) incentivesResult.get("formulaResult");
		        		rateAmount = new BigDecimal(formulaValue);
						
					}else{
						rateAmount = validFacilityRate.getBigDecimal("rateAmount");
					}
					
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e, module);	
	            return ServiceUtil.returnError(e.toString());
			}       
			result.put("rateAmount",rateAmount);
			result.put("uomId",uomId);
			 
	        return result;
	    } 
	  // Payment services
	    public static Map<String, Object> createPaymentForBooth(DispatchContext dctx, Map<String, ? extends Object> context){
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String facilityId = (String) context.get("facilityId");
	        String supplyDate = (String) context.get("supplyDate");        
	        BigDecimal paymentAmount = ProductEvents.parseBigDecimalForEntity((String) context.get("amount"));
	        Locale locale = (Locale) context.get("locale");     
	        String paymentMethodType = (String) context.get("paymentMethodTypeId");
	        String paymentLocationId = (String) context.get("paymentLocationId");                
	        String paymentRefNum = (String) context.get("paymentRefNum");
	        boolean useFifo = Boolean.FALSE;       
	        if(UtilValidate.isNotEmpty(context.get("useFifo"))){
	        	useFifo = (Boolean)context.get("useFifo");
	        }
	        String paymentType = "SALES_PAYIN";
	        String partyIdTo ="Company";
	        String paymentId = "";
	        boolean roundingAdjustmentFlag =Boolean.TRUE;
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        List exprListForParameters = FastList.newInstance();
	        List boothOrdersList = FastList.newInstance();
	        Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(supplyDate));
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + supplyDate, module);
	            return ServiceUtil.returnError(e.toString());		   
			}	
			//getting default payment methodtype if empty
			if(UtilValidate.isEmpty(paymentMethodType)){
				 Map partyProfileFacilityMap=(Map)getPartyProfileDafult(dispatcher.getDispatchContext(),UtilMisc.toMap("boothIds", UtilMisc.toList(facilityId),"supplyDate",paymentTimestamp)).get("partyProfileFacilityMap");
				if(UtilValidate.isNotEmpty(partyProfileFacilityMap.get(facilityId))){
				 paymentMethodType=(String)partyProfileFacilityMap.get(facilityId);
				}
			}
			if(UtilValidate.isEmpty(paymentMethodType)){
				Debug.logError("paymentMethod Configuration not Done=========== ", module);
				Debug.logError("paymentMethod Configuration not Done======For=====  "+facilityId, module);
				return ServiceUtil.returnError("paymentMethod Configuration not Done======For====="+facilityId);
			}
			// Do check for only past dues payments
			if(useFifo){
				try{
					List exprListForParameters1 = FastList.newInstance();
		    		exprListForParameters1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,userLogin.getString("partyId")));
		    		exprListForParameters1.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "FACILITY_CASHIER"));
		    		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters1, EntityOperator.AND);    		
		    		List<GenericValue>  faclityPartyList = delegator.findList("FacilityParty", paramCond, null, null, null, false);
		    		faclityPartyList = EntityUtil.filterByDate(faclityPartyList);
		    		if(UtilValidate.isEmpty(faclityPartyList)){
		    			Debug.logError("you Don't have permission to create payment, Facility Cashier role missing", module);
		            	return ServiceUtil.returnError("you Don't have permission to create payment, Facility Cashier role missing");	    			
		    		}
		    		paymentLocationId = (EntityUtil.getFirst(faclityPartyList)).getString("facilityId");
				}catch (Exception e) {
					// TODO: handle exception
					Debug.logError(e, module);
		        	return ServiceUtil.returnError(e.toString());
				}
			}
			//lets exclude future day shipment  orders
			//this is to exclude the pm sales invoices on that same day
	       List feaShipmentIds = getShipmentIds(delegator , UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp(paymentTimestamp, 1), "yyyy-MM-dd HH:mm:ss"),null);
	       if(UtilValidate.isNotEmpty(feaShipmentIds) && !useFifo){
	    	   exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_IN, feaShipmentIds));
	       }
	       
	       
	       exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));	
			
			exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF")));
			
			//checking tenant config to find invoices only for cash or all
			 boolean enableSoCrPmntTrack = Boolean.FALSE;
			 try{
				 GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSoCrPmntTrack"), true);
				 if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack) && (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
					 enableSoCrPmntTrack = Boolean.TRUE;
				 	} 
			 }catch (GenericEntityException e) {
					// TODO: handle exception
					Debug.logError(e, module);
				}		
			 List categoryTypeEnumList = UtilMisc.toList("SO_INST","CR_INST");
			    if(enableSoCrPmntTrack){
					//exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
					exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH") , EntityOperator.OR , EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN, categoryTypeEnumList)));
			    }else{
					exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH"));
				}
					
			/*exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));*/

			EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);

			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			List<String> orderBy = UtilMisc.toList("-estimatedDeliveryDate");
			try{
				// Here we are trying change the invoice order to apply (LIFO OR FIFO)
				if(useFifo){
					orderBy = UtilMisc.toList("estimatedDeliveryDate");
				}
				boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null , orderBy, findOptions, false);
				
			}catch(GenericEntityException e){
				Debug.logError(e, module);	
	            return ServiceUtil.returnError(e.toString());			
			}
	        
	        try {
	        	GenericValue facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
	        	String partyIdFrom = (String)facility.getString("ownerPartyId");
	        	
	        	Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", paymentType);
	        	// lets get the opening balance invoices if any
	        	List<GenericValue> obInvoiceList = (List)getOpeningBalanceInvoices(dctx,UtilMisc.toMap("facilityId", facilityId)).get("invoiceList");
	        	boothOrdersList.addAll(obInvoiceList);
	        	if(UtilValidate.isEmpty(boothOrdersList)){
					Debug.logError("paramCond==================== "+paramCond, module);
					Debug.logError("No dues found for the Booth "+facilityId, module);
					return ServiceUtil.returnError("No dues found for the Booth"+facilityId);
				}
	        	// Here we are trying change the invoice order to apply (LIFO OR FIFO)
	        	boothOrdersList = EntityUtil.orderBy(boothOrdersList, UtilMisc.toList("parentFacilityId","originFacilityId","-estimatedDeliveryDate"));
				if(useFifo){
					boothOrdersList = EntityUtil.orderBy(boothOrdersList, UtilMisc.toList("parentFacilityId","originFacilityId","estimatedDeliveryDate"));
				}        	
	        	List invoiceIds =  EntityUtil.getFieldListFromEntityList(boothOrdersList, "invoiceId", true);        	
	        	
	        	Map<String, Object> totalAmount =FastMap.newInstance();
				Map boothResult = getBoothDues(dctx,UtilMisc.<String, Object>toMap("boothId", 
						facility.getString("facilityId"), "userLogin", userLogin)); 
				if (!ServiceUtil.isError(boothResult)) {
					Map boothTotalDues = (Map)boothResult.get("boothTotalDues");
					BigDecimal amount = new BigDecimal(boothTotalDues.get("amount").toString());
					BigDecimal totalDueAmount = new BigDecimal(boothTotalDues.get("totalDueAmount").toString());        			
					if(roundingAdjustmentFlag){
						if((amount.subtract(paymentAmount)).compareTo(BigDecimal.ONE) < 0 && (amount.subtract(paymentAmount)).compareTo(BigDecimal.ZERO) > 0){
							paymentAmount = amount;
						}
						if((totalDueAmount.subtract(paymentAmount)).compareTo(BigDecimal.ONE) < 0 && (totalDueAmount.subtract(paymentAmount)).compareTo(BigDecimal.ZERO) > 0){
							paymentAmount = totalDueAmount;
						}    										
					}	
					
				}			
	            paymentCtx.put("paymentMethodTypeId", paymentMethodType);
	            paymentCtx.put("organizationPartyId", partyIdTo);
	            paymentCtx.put("partyId", partyIdFrom);
	            paymentCtx.put("facilityId", facilityId);
	            if (!UtilValidate.isEmpty(paymentLocationId) ) {
	                paymentCtx.put("paymentLocationId", paymentLocationId);                        	
	            }            
	            if (!UtilValidate.isEmpty(paymentRefNum) ) {
	                paymentCtx.put("paymentRefNum", paymentRefNum);                        	
	            }
	            paymentCtx.put("statusId", "PMNT_RECEIVED");
	            paymentCtx.put("isEnableAcctg", "N");
	            paymentCtx.put("amount", paymentAmount);
	            paymentCtx.put("userLogin", userLogin); 
	            paymentCtx.put("invoices", invoiceIds);
	    		
	            Map<String, Object> paymentResult = dispatcher.runSync("createPaymentAndApplicationForInvoices", paymentCtx);
	            if (ServiceUtil.isError(paymentResult)) {
	            	Debug.logError(paymentResult.toString(), module);
	                return ServiceUtil.returnError(null, null, null, paymentResult);
	            }
	            paymentId = (String)paymentResult.get("paymentId");
	            }catch (Exception e) {
	            Debug.logError(e, e.toString(), module);
	            return ServiceUtil.returnError(e.toString());
	        }       
			 Map result = ServiceUtil.returnSuccess("Payment successfully done.");
			 boolean enablePaymentSms = Boolean.FALSE;
			 try{
				 GenericValue tenantConfigEnablePaymentSms = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","SMS", "propertyName","enablePaymentSms"), true);
				 if (UtilValidate.isNotEmpty(tenantConfigEnablePaymentSms) && (tenantConfigEnablePaymentSms.getString("propertyValue")).equals("Y")) {
					 enablePaymentSms = Boolean.TRUE;
					}
			 }catch (GenericEntityException e) {
				// TODO: handle exception
				 Debug.logError(e, module);             
			}
			 result.put("enablePaymentSms",enablePaymentSms);
			 result.put("paymentId",paymentId);
			 result.put("paymentMethodTypeId",paymentMethodType);
			 
	        return result;
	    }  
	    /**
	     * Make booth payments
	     * @param ctx the dispatch context
	     * @param context 
	     * Note: This method is used only by eSeva.  Sometimes we can get more than one makePayment request
	     * for the same booth from the eSeva server. To eliminate this issue we don't allow more than one 
	     * payment for the same booth for the same day.
	     */
	    public static Map<String, Object> makeBoothPayments(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			String paymentChannel = (String) context.get("paymentChannel");     	
			String transactionId = (String) context.get("transactionId");     	
			String paymentLocationId = (String) context.get("paymentLocationId");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			List<Map<String, Object>> boothPayments = (List<Map<String, Object>>) context.get("boothPayments");
			String infoString = "makeBoothPayments:: " + "paymentChannel=" + paymentChannel 
				+";transactionId=" + transactionId + ";paymentLocationId=" + paymentLocationId 
				+ " " + boothPayments;
	Debug.logInfo(infoString, module);
			if (boothPayments.isEmpty()) {
	            Debug.logError("No payment amounts found; " + infoString, module);
	            return ServiceUtil.returnError("No payment amounts found; " + infoString);			
			}
			for (Map boothPayment: boothPayments) { 
	        	Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentMethodTypeId", paymentChannel);    		
	    		paymentCtx.put("userLogin", context.get("userLogin"));
	    		paymentCtx.put("facilityId", (String)boothPayment.get("boothId"));
	    		paymentCtx.put("supplyDate", UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"));
	            paymentCtx.put("paymentLocationId", paymentLocationId); 
	            paymentCtx.put("paymentRefNum", transactionId);                        	    		            
	    		paymentCtx.put("amount", ((Double)boothPayment.get("amount")).toString());
	    		
	        	Map<String, Object> paidPaymentCtx = UtilMisc.<String, Object>toMap("paymentMethodTypeId", paymentChannel);    
	        	paidPaymentCtx.put("paymentDate", UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd"));
				paidPaymentCtx.put("facilityId", (String)boothPayment.get("boothId"));
				
				Map boothsPaymentsDetail = getBoothPaidPayments( ctx , paidPaymentCtx);
				List boothPaymentsList = (List)boothsPaymentsDetail.get("boothPaymentsList");
				if (boothPaymentsList.size() > 0) {
		            Debug.logError("Already received payment for booth " + (String)boothPayment.get("boothId") + " from eSeva," +
		            		"hence skipping... Existing payment details:" + boothPaymentsList.get(0) + "; Current payment details:" +
		            		paymentCtx, module);
		            continue;
				}
				try{
					Map<String, Object> paymentResult =  dispatcher.runSync("createPaymentForBooth",paymentCtx);
					if (ServiceUtil.isError(paymentResult)) {
		    			Debug.logError("Payment failed for: " + infoString + "[" + paymentResult + "]", module);    			
		    			return paymentResult;
		    		}
					Debug.logInfo("Made following payment:" + paymentCtx, module);    		
				}catch (GenericServiceException e) {
					// TODO: handle exception
					Debug.logError(e, module);    			
					return ServiceUtil.returnError(e.getMessage());
				}
	        }		
	    	return ServiceUtil.returnSuccess();
	    }

	    /**
	     * Make booth payments
	     * @param ctx the dispatch context
	     * @param context 
	     */
	    public static Map<String, Object> massMakeBoothPayments(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
			List boothIds = (List) context.get("boothIds");     	
			GenericValue userLogin =(GenericValue)context.get("userLogin");
			String paymentLocationId = "";
			List paymentIds = FastList.newInstance();
			if (UtilValidate.isEmpty(boothIds)) {
	            Debug.logError("No payment amounts found; ", module);
	            return ServiceUtil.returnError("No payment amounts found; ");			
			}
			String paymentMethodType="";
			Map partyProfileFacilityMap=(Map)getPartyProfileDafult(dispatcher.getDispatchContext(),UtilMisc.toMap("boothIds",boothIds,"supplyDate",UtilDateTime.nowTimestamp())).get("partyProfileFacilityMap");
			
			
			for(int i=0 ; i< boothIds.size() ; i++){
				String boothId = (String)boothIds.get(i);
				Map boothResult = getBoothDues(ctx,UtilMisc.<String, Object>toMap("boothId", 
						boothIds.get(i), "userLogin", userLogin));
				if (ServiceUtil.isError(boothResult)) {
					Debug.logError("No payment amounts found; "+boothResult, module);
	            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(boothResult));
	            }
				try{
					List exprListForParameters = FastList.newInstance();
		    		exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,userLogin.getString("partyId")));
		    		exprListForParameters.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "FACILITY_CASHIER"));
		    		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);    		
		    		List<GenericValue>  faclityPartyList = delegator.findList("FacilityParty", paramCond, null, null, null, false);	    		
		    		if(UtilValidate.isEmpty(faclityPartyList)){
		    			Debug.logError("you Don't have permission to create payment, Facility Cashier role missing", module);
		            	return ServiceUtil.returnError("you Don't have permission to create payment, Facility Cashier role missing");	    			
		    		}
		    		faclityPartyList = EntityUtil.filterByDate(faclityPartyList);
		    		paymentLocationId = (EntityUtil.getFirst(faclityPartyList)).getString("facilityId");
				}catch (Exception e) {
					// TODO: handle exception
					Debug.logError(e, module);
	            	return ServiceUtil.returnError(e.toString());
				}
	    		Map boothDues = (Map)boothResult.get("boothDues");
	    		Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("userLogin", userLogin);
	    		//paymentCtx.put("paymentMethodTypeId", "CASH_PAYIN");
	    		
	    		if(UtilValidate.isNotEmpty(partyProfileFacilityMap.get(boothId))){
	   			 paymentMethodType=(String)partyProfileFacilityMap.get(boothId);
	   			paymentCtx.put("paymentMethodTypeId", paymentMethodType);
	   			}
	    		
	    		paymentCtx.put("facilityId", boothId);
	    		paymentCtx.put("supplyDate", UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"));
	            paymentCtx.put("paymentLocationId", paymentLocationId);                                   	    		            
	    		paymentCtx.put("amount", ((Double)boothDues.get("amount")).toString());
	    		try{
					Map<String, Object> paymentResult =  dispatcher.runSync("createPaymentForBooth",paymentCtx);
					if (ServiceUtil.isError(paymentResult)) {
		    			Debug.logError("Payment failed for:"+ paymentResult + "]", module);    			
		    			return paymentResult;
		    		}
					paymentIds.add(paymentResult.get("paymentId"));    		
				}catch (GenericServiceException e) {
					// TODO: handle exception
					Debug.logError(e, module);    			
					return ServiceUtil.returnError(e.getMessage());
				} 		
	    		
			}		 
	    	
			 Map result = ServiceUtil.returnSuccess("Payment successfully done.");
			result.put("paymentIds",paymentIds);
	        return result;
	    }
	    public static Map<String, Object> getPartyProfileDafult(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			List boothIds = (List) context.get("boothIds");
			Timestamp supplyDate = (Timestamp) context.get("supplyDate");
			if(UtilValidate.isEmpty(supplyDate)){
				supplyDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			}
	        Map<String, Object> result = FastMap.newInstance(); 
	        GenericValue boothFacility;
	        Map  partyProfileFacilityMap=FastMap.newInstance(); 
	        Map  partyPaymentMethodDesc=FastMap.newInstance(); 
	        try {
	        	List partyCondList = FastList.newInstance();
	        	partyCondList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN ,boothIds));
	        	partyCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(supplyDate)));
	        	partyCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayEnd(supplyDate))));
				
	        	EntityCondition partyCondition = EntityCondition.makeCondition(partyCondList ,EntityOperator.AND);
	        	/*List<GenericValue> facilityList = delegator.findList("Facility", facCond, null, null, null, true);
	        	List partyIdsList = EntityUtil.getFieldListFromEntityList(facilityList, "ownerPartyId", true);
	        	*/
	        	List<GenericValue> partyProfileDefaultList = delegator.findList("PartyProfileDefaultAndFacilityAndMethodType",partyCondition, null, null, null, true);
	        	partyProfileDefaultList = EntityUtil.filterByDate(partyProfileDefaultList, supplyDate);
	        	for(GenericValue partyProfileDafault:partyProfileDefaultList){
	        		partyProfileFacilityMap.put(partyProfileDafault.getString("facilityId"), partyProfileDafault.getString("defaultPayMeth"));
	        		partyPaymentMethodDesc.put(partyProfileDafault.getString("facilityId"), partyProfileDafault.getString("description"));
	        	}
    			 result.put("partyProfileFacilityMap",partyProfileFacilityMap);
    			 result.put("partyPaymentMethodDesc",partyPaymentMethodDesc);
	        }catch(GenericEntityException e){
				Debug.logError(e, module);	
	            return ServiceUtil.returnError(e.toString());			
			}
	        return result;
	    }
	    public static Map<String, Object> getVehicleRole(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			String facilityId = (String) context.get("facilityId");
			Timestamp supplyDate = (Timestamp) context.get("supplyDate");
			if(UtilValidate.isEmpty(supplyDate)){
				supplyDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			}
	        Map<String, Object> result = FastMap.newInstance(); 
	        GenericValue routeFacility;
	        GenericValue vehicleRole=null;
	        try {
	        	routeFacility = delegator.findOne("Facility",true, UtilMisc.toMap("facilityId", facilityId));
	        	if (routeFacility == null) {
	                Debug.logError("Invalid routeId " + facilityId, module);
	                return ServiceUtil.returnError("Invalid routeId " + facilityId);         		
	        	}
	        	List condList = FastList.newInstance();
	        	condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"ROUTE_VEHICLE"));
	        	condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,routeFacility.getString("facilityId")));
	        	EntityCondition cond = EntityCondition.makeCondition(condList ,EntityOperator.AND);
	        	List<GenericValue> vehicleRoleList = delegator.findList("VehicleRole", cond, null, null, null, true);
	        	vehicleRoleList = EntityUtil.filterByDate(vehicleRoleList, supplyDate);
    			 if(UtilValidate.isNotEmpty(vehicleRoleList)){
    				  vehicleRole = EntityUtil.getFirst(vehicleRoleList);
    			 }
    			 result.put("vehicleRole",vehicleRole);
	        }catch(GenericEntityException e){
				Debug.logError(e, module);	
	            return ServiceUtil.returnError(e.toString());			
			}
	        return result;
	    }
	    public static Map<String, Object> getProductCratesAndCans(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
			GenericValue userLogin =(GenericValue)context.get("userLogin");
			Timestamp saleDate = (Timestamp) context.get("saleDate");
			List productIds = (List) context.get("productIds");
			Map result = ServiceUtil.returnSuccess();
			if(UtilValidate.isEmpty(saleDate)){
				saleDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			}
			List conditionList = FastList.newInstance();
			Map<String, Object> cratesMap = FastMap.newInstance();
			Map cansMap = FastMap.newInstance();
			try{
				
				List attrNameList = UtilMisc.toList("CRATE", "CAN");
				conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.IN, attrNameList));
				if(UtilValidate.isNotEmpty(productIds)){
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
				}
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> productCratesCans = delegator.findList("ProductAttribute", condition, null, null, null, false);
				
				String attrName = "";
				for(GenericValue productAttr : productCratesCans){
					attrName = productAttr.getString("attrName");
					BigDecimal attrValue = new BigDecimal(productAttr.getString("attrValue"));
					
					if(attrName.equals("CRATE")){
						cratesMap.put(productAttr.getString("productId"), attrValue);
					}
					else{
						cansMap.put(productAttr.getString("productId"), attrValue);
					}
				}
				
			}catch(Exception e){
				Debug.logError(e, module);
	    		return ServiceUtil.returnError(e.toString());
			}
			result.put("piecesPerCrate",cratesMap);
			result.put("piecesPerCan",cansMap);
	        return result;
	    }
	    
	    public static Map<String, Object> getRouteVehicleForShipment(DispatchContext dctx, Map<String, ? extends Object> context){
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String supplyDateStr = (String) context.get("supplyDate"); 
	        String routeId = (String) context.get("routeId");
	        String tripId = (String) context.get("tripId");
	        String returnHeaderTypeId = (String) context.get("returnHeaderTypeId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map result = ServiceUtil.returnSuccess(); 
	        List conditionList = FastList.newInstance();
	        Timestamp supplyDate = null;
	    	try{
	    		if (UtilValidate.isNotEmpty(supplyDateStr)) { 
	    			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
	    			try {
	    				supplyDate = new java.sql.Timestamp(sdf.parse(supplyDateStr).getTime());
	    			} catch (ParseException e) {
	    				Debug.logError("Cannot parse date string: "+supplyDateStr, module);
		    			return ServiceUtil.returnError("Cannot parse date string"); 
	    			} catch (NullPointerException e) {
	    				Debug.logError("Cannot parse date string: "+supplyDateStr, module);
		    			return ServiceUtil.returnError("Cannot parse date string"); 
	    			}
	    		}
	    		if(UtilValidate.isEmpty(returnHeaderTypeId)){
	    			Debug.logError("Return type is empty", module);
	    			return ServiceUtil.returnError("Return type is empty");
	    		}
	    		if(UtilValidate.isEmpty(supplyDate)){
	    			Debug.logError("supply date is empty", module);
	    			return ServiceUtil.returnError("supply date is empty");
	    		}
	    		Timestamp dayStart =UtilDateTime.getDayStart(supplyDate);
	    		Timestamp dayEnd =UtilDateTime.getDayEnd(supplyDate);
	    		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
    			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
    			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
    			conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
    			conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
    			EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			List<GenericValue> shipment = delegator.findList("Shipment", cond, null, null, null, false);
    			String shipmentId = "";
    			String vehicleId = "";
    			if(UtilValidate.isNotEmpty(shipment)){
    				shipmentId = ((GenericValue)EntityUtil.getFirst(shipment)).getString("shipmentId");
    				vehicleId=((GenericValue)EntityUtil.getFirst(shipment)).getString("vehcileId");
    			}
    			conditionList.clear();
    			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
    			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("RETURN_CANCELLED")));
    			EntityCondition expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			List<GenericValue> returnHeader = delegator.findList("ReturnHeader", expr, null, null, null, false);
    			if(UtilValidate.isNotEmpty(returnHeader)){
    				String returnId = ((GenericValue)EntityUtil.getFirst(returnHeader)).getString("returnId");
    				Debug.logError("One return allowed for one shipment, Cancel return :"+returnId+" and re-enter the return" , module);
	    			return ServiceUtil.returnError("One return allowed for one shipment, Cancel return :"+returnId+" and re-enter the return");
    			}
				result.put("vehcileId", vehicleId);
				result.put("routeId", routeId);
	    	}catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}
			return result;
	    }
	    
	    public static Map getByProductPaymentDetails(DispatchContext dctx, Map<String, ? extends Object> context) {
			 
			 Delegator delegator = dctx.getDelegator();
			 LocalDispatcher dispatcher = dctx.getDispatcher();
			 Map result = FastMap.newInstance();
			 Timestamp fromDate = (Timestamp) context.get("fromDate");
			 Timestamp thruDate = (Timestamp) context.get("thruDate");
			 List facilityList = (List)context.get("facilityList");
			 
			 int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate)+1);
	    	 List payments = FastList.newInstance();
	    	 List conditionList = FastList.newInstance();
	    	 
	    	
	    	 if(UtilValidate.isEmpty(facilityList)){
	    		 facilityList = (List)getAllBooths(delegator).get("boothsList");
			 }
	    	// conditionList.add(EntityCondition.makeCondition("paymentPurposeType", EntityOperator.EQUALS, "BYPROD_PAYMENT"));
	    	 if(UtilValidate.isNotEmpty(facilityList)){
	   			 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityList)));
	    	 }
	    	 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, "Company")));
	    	 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
	    	 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)));
	    	 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_RECEIVED"),EntityOperator.OR, 
	    			 	EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_VOID")));
	    	 EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	 try{
	    		 payments = delegator.findList("Payment", condition, null, UtilMisc.toList("facilityId", "paymentDate"), null, false);
	    	 }catch(GenericEntityException e){
	    		 Debug.logError(e, e.toString(), module);
	             return ServiceUtil.returnError(e.toString());
	    	 }
	    	 List paymentParties = EntityUtil.getFieldListFromEntityList(payments, "facilityId", true);
	    	 Map paymentDetailsMap = FastMap.newInstance();
	    	 
	    	 if(UtilValidate.isNotEmpty(paymentParties)){
	    		 for(int i=0;i<paymentParties.size();i++){
	    			 Map dayPaymentDetail = FastMap.newInstance();
	    			 String facilityId = ((String)paymentParties.get(i)).toUpperCase();
	    			 List<GenericValue> facilityPayments = EntityUtil.filterByCondition(payments, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("facilityId"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)facilityId).toUpperCase())));
	    			 if(UtilValidate.isNotEmpty(facilityPayments)){
	    				 for(int j = 0; j<facilityPayments.size();j++){
	    					 GenericValue facilityPayment = (GenericValue)facilityPayments.get(j);
	    					 Map dataMap = FastMap.newInstance();
	    					 Map tempDayPayments = FastMap.newInstance();
	    					 Timestamp paymentDate = UtilDateTime.getDayStart((Timestamp)facilityPayment.get("paymentDate"));
	    					 Timestamp chequeDate = UtilDateTime.getDayStart((Timestamp)facilityPayment.get("effectiveDate"));
	    					 String paymentRefNum = facilityPayment.getString("paymentRefNum");
	    					 String issuingAuthority = facilityPayment.getString("issuingAuthority");
	    					 String issuingAuthorityBranch = facilityPayment.getString("issuingAuthorityBranch");
	    					 String statusId = facilityPayment.getString("statusId");
	    					 BigDecimal amount = (BigDecimal)facilityPayment.get("amount");
	    					 String paymentMethodTypeId = facilityPayment.getString("paymentMethodTypeId");
							 dataMap.put("paymentRefNum", paymentRefNum);
	    					 dataMap.put("issuingAuthority", issuingAuthority);
	    					 dataMap.put("issuingAuthorityBranch", issuingAuthorityBranch);
	    					 if(paymentMethodTypeId.equalsIgnoreCase("CASH_PAYIN")){
	    						 dataMap.put("chequeDate", "");
	    					 }else{
	    						 dataMap.put("chequeDate", chequeDate);
	    					 }
	    					 dataMap.put("amount", amount);
	    					 if(dayPaymentDetail.containsKey(paymentDate)){
	    						 Map tempDayData = FastMap.newInstance();
	    						 tempDayData = (Map)dayPaymentDetail.get(paymentDate);
	    						 List tempList = FastList.newInstance();
								 Map tempMap = FastMap.newInstance();
	    						 if(statusId.equals("PMNT_VOID")){
	    							 if(tempDayData.containsKey("chequeReturn")){
	    								 List tempChequeReturn = (List)tempDayData.get("chequeReturn");
	    								 tempChequeReturn.add(dataMap);
	    								 tempDayData.put("chequeReturn", tempChequeReturn);
	    							 }else{
	    								 tempList.add(dataMap);
	    								 tempMap.put("chequeReturn", tempList);
	    								 tempDayData.putAll(tempMap);
	    							 }
	    						 }else{
	    							 if(tempDayData.containsKey("payment")){
	    								 List tempPayment = (List)tempDayData.get("payment");
	    								 tempPayment.add(dataMap);
	    								 tempDayData.put("payment", tempPayment);
	    							 }else{
	    								 tempList.add(dataMap);
	    								 tempMap.put("payment", tempList);
	    								 tempDayData.putAll(tempMap);
	    							 }
	    						 }
	    						 dayPaymentDetail.put(paymentDate, tempDayData);
	    					 }
	    					 else{
	    						 List tempPayment = FastList.newInstance();
	    						 tempPayment.add(dataMap);
	    						 Map initMap = FastMap.newInstance();
	    						 if(statusId.equals("PMNT_VOID")){
	    							 initMap.put("chequeReturn", tempPayment);
	    						 }else{
	    							 initMap.put("payment", tempPayment);
	    						 }
	    						 dayPaymentDetail.put(paymentDate, initMap);
	    					 }
	    				 }
	    			 }
	    			 paymentDetailsMap.put(facilityId, dayPaymentDetail);
	    		 }
	    	 }
	    	 result.put("paymentDetails", paymentDetailsMap);
			 return result;
		 }
	    public static Map getByProductDayWiseInvoiceTotals(DispatchContext dctx, Map<String, ? extends Object> context) {
			 //Timestamp fromDate, Timestamp thruDate, List facilityList
			 Delegator delegator = dctx.getDelegator();
			 LocalDispatcher dispatcher = dctx.getDispatcher();
			 GenericValue userLogin = (GenericValue) context.get("userLogin");
			 Timestamp fromDate = (Timestamp) context.get("fromDate");
			 Timestamp thruDate = (Timestamp) context.get("thruDate");
			 List facilityList = (List)context.get("facilityList");
			 Map result = FastMap.newInstance();
			 List boothsList = FastList.newInstance();
			 if(UtilValidate.isEmpty(facilityList)){
				 boothsList = (List)getAllBooths(delegator).get("boothsList");
			 }
			 else{
				 boothsList.addAll(facilityList);
			 }
			 Map dayWisePartyInvoiceDetail = new TreeMap();
			 try{
				 List conditionList = FastList.newInstance();
		    	 conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.NOT_IN, UtilMisc.toList("PENALTY_IN","STATUTORY_OUT")));
	    		 conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	   			 conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	   			 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, boothsList));
	   			 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF")));
		    	 EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		 List<GenericValue> invoices = delegator.findList("Invoice", condition, UtilMisc.toSet("invoiceId","dueDate","facilityId"), null, null, false);
	    		 int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate)+1);
	    		 for(int k =0;k<intervalDays;k++){
	    	    		
	    	    	List condList = FastList.newInstance(); 
	    	 		Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
	    	 		Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
	    	 		Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate);
	    	 		List<GenericValue> daywiseInvoices = EntityUtil.filterByCondition(invoices, EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart),EntityOperator.AND, EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
	    	 		List dayPartyInvoices = (List)EntityUtil.getFieldListFromEntityList(daywiseInvoices, "facilityId", true);
	    	 		List dayPartyList = FastList.newInstance();
	    	 		for(int j=0;j<dayPartyInvoices.size();j++){
	    	 			String booth = (String)dayPartyInvoices.get(j);
	    	 			booth = booth.toUpperCase();
	    	 			if(!dayPartyList.contains(booth)){
	    	 				dayPartyList.add(booth);
	    	 			}
	    	 		}
	    	 		Map partySale = FastMap.newInstance();
	    	 		String boothId = "";
	    	 		Map invDetail = FastMap.newInstance();
	   	 			for(int i=0;i<dayPartyList.size();i++){
	   	 				boothId = (String)dayPartyList.get(i);
	   	 				List<GenericValue> daywiseBoothwiseSale = EntityUtil.filterByCondition(daywiseInvoices, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("facilityId"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)boothId).toUpperCase())));
	   	 				for(int j=0 ; j<daywiseBoothwiseSale.size();j++){
	   	 					GenericValue dayBoothSale = (GenericValue)daywiseBoothwiseSale.get(j);
	   	 					String invoiceId = dayBoothSale.getString("invoiceId");
	   	 					Timestamp dueDate = (Timestamp)dayBoothSale.get("dueDate");
	   	 					BigDecimal amount = (BigDecimal)InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
	   	 					if(invDetail.containsKey(boothId)){
	   	 						Map tempDetail = (Map)invDetail.get(boothId);
	   	 						BigDecimal tempAmount = (BigDecimal)tempDetail.get("amount");
	   	 						String tempInvoiceId = (String) tempDetail.get("invoiceId");
	   	 						tempDetail.put("invoiceId", tempInvoiceId+", "+invoiceId);
	   	 						tempDetail.put("amount", amount.add(tempAmount));
	   	 						invDetail.put(boothId, tempDetail);
	   	 					}
	   	 					else{
	   	 						Map detailMap = FastMap.newInstance();
	   	 						detailMap.put("invoiceId", invoiceId);
	   	 						detailMap.put("amount", amount);
	   	 						invDetail.put(boothId, detailMap);
	   	 					}
	   	 				}
	   	 			}
	   	 			dayWisePartyInvoiceDetail.put(dayStart, invDetail);
	    		 }
			 }catch(Exception e){
				 Debug.logError(e, e.toString(), module);
	             return ServiceUtil.returnError(e.toString());
			 }
	    	 result.put("dayWisePartyInvoiceDetail", dayWisePartyInvoiceDetail);
			 return result;
		 }
	    public static Map getByProductDaywisePenaltyTotals(DispatchContext dctx, Timestamp fromDate, Timestamp thruDate, List facilityList, GenericValue userLogin) {
			 
			 Delegator delegator = dctx.getDelegator();
			 LocalDispatcher dispatcher = dctx.getDispatcher();
			 Map result = FastMap.newInstance();
			 List boothsList = FastList.newInstance();
			 if(UtilValidate.isEmpty(facilityList)){
				 boothsList = (List)getAllBooths(delegator).get("boothsList");
			 }
			 else{
				 boothsList.addAll(facilityList);
			 }
			 Map daywiseBoothTotals = FastMap.newInstance();
			 int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate)+1);
			 List<GenericValue> invoices = null;
			 Map facilityPenalty = FastMap.newInstance();
			 Map penaltyPaymentReferences = FastMap.newInstance();
			 try{
				 List returnChequeExpr = FastList.newInstance();
				 returnChequeExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_VOID"));
		    	/* returnChequeExpr.add(EntityCondition.makeCondition("chequeReturns", EntityOperator.EQUALS, "Y"));*/
		    	 EntityCondition cond = EntityCondition.makeCondition(returnChequeExpr, EntityOperator.AND);
		    	 List<GenericValue> returnPayments = delegator.findList("Payment", cond, UtilMisc.toSet("paymentId", "paymentRefNum", "amount"), null, null, false);
		    	 if(UtilValidate.isNotEmpty(returnPayments)){
		    		 for(GenericValue returns : returnPayments){
		    			 Map tempMap = FastMap.newInstance();
		    			 String paymentId = returns.getString("paymentId");
		    			 String paymentRefNum = returns.getString("paymentRefNum");
		    			 BigDecimal amount = (BigDecimal)returns.get("amount");
		    			 tempMap.put("referenceNum", paymentRefNum);
		    			 tempMap.put("amount", amount);
		    			 penaltyPaymentReferences.put(paymentId, tempMap);
		    		 }
		    	 }
				
				 List conditionList = FastList.newInstance();
		    	 conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PENALTY_IN"));
		    	/* conditionList.add(EntityCondition.makeCondition("invoicePurposeType", EntityOperator.EQUALS, "BYPROD_INVOICE"));*/
	    		 conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	   			 conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	   			 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		    	 EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		 invoices = delegator.findList("Invoice", condition, UtilMisc.toSet("invoiceId","invoiceDate","facilityId","referenceNumber"), null, null, false);
	    		 
	    		 if(UtilValidate.isNotEmpty(invoices)){
					 List facilities = (List)EntityUtil.getFieldListFromEntityList(invoices, "facilityId", true);
					 if(UtilValidate.isNotEmpty(facilities)){
						 for(int i = 0 ;i<facilities.size();i++){
							 String facilityId = (String)facilities.get(i);
							 Map dayWisePenalty = FastMap.newInstance();
							 List<GenericValue> facilityInvoices = EntityUtil.filterByCondition(invoices, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
							 Map dayPenalty = FastMap.newInstance();
							 if(UtilValidate.isNotEmpty(facilityInvoices)){
								 for(int k =0; k<intervalDays; k++){
									 Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
									 Timestamp dayStart = 	UtilDateTime.getDayStart(supplyDate);
									 Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate);
									 List dayCond = FastList.newInstance();
									 dayCond.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
									 dayCond.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
									 List<GenericValue> dayPartyPenalty = (List)EntityUtil.filterByCondition(facilityInvoices, EntityCondition.makeCondition(dayCond));
									 List invoiceDetail = FastList.newInstance();
									 if(UtilValidate.isNotEmpty(dayPartyPenalty)){
										 for(int j=0;j<dayPartyPenalty.size();j++){
											 Map invoiceMap = FastMap.newInstance();
											 GenericValue eachPartyPenalty = dayPartyPenalty.get(j);
											 String invoiceId = eachPartyPenalty.getString("invoiceId");
											 String paymentRefNum = eachPartyPenalty.getString("referenceNumber");
											 BigDecimal amount = InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
											 invoiceMap.put("amount", amount);
											 invoiceMap.put("paymentId", paymentRefNum);
											 invoiceDetail.add(invoiceMap);
										 }
										 dayPenalty.put(dayStart, invoiceDetail);
									 }
								 }
							 }
							 facilityPenalty.put(facilityId, dayPenalty);
						 }
					 }
				}
			 }catch(Exception e){
				 Debug.logError(e, e.toString(), module);
	             return ServiceUtil.returnError(e.toString());
			 }
			 result.put("returnPaymentReferences", penaltyPaymentReferences);
	    	 result.put("facilityPenalty", facilityPenalty);
			 return result;
		 }
	    
}
	
	
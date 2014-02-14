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
import org.ofbiz.product.product.ProductWorker;



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
	 
	 
	 public static Map getByProductParlourDespatch(DispatchContext dctx, Timestamp fromDate, Timestamp thruDate)  {
		 
		 Delegator delegator = dctx.getDelegator();
		 LocalDispatcher dispatcher = dctx.getDispatcher();
		     
		 Map result = FastMap.newInstance();
		 int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate)+1);
    	 //List orderedItems = FastList.newInstance();
    	 
    	 List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, fromDate, thruDate, "BYPRODUCTS");
    	 List conditionList = FastList.newInstance(); 
 		 conditionList.add(EntityCondition.makeCondition("shipmentId",  EntityOperator.IN, shipmentList));
 		 conditionList.add(EntityCondition.makeCondition("isCancelled",  EntityOperator.EQUALS, null));
 		 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.NOT_IN, UtilMisc.toList("REPLACEMENT_BYPROD","BYPROD_GIFT"))));
 		 EntityCondition shipReceiptCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 		 List<GenericValue> shipReceiptList = FastList.newInstance();
    	 try{
    		 shipReceiptList = delegator.findList("ShipmentReceiptAndItem", shipReceiptCond, UtilMisc.toSet("facilityId", "productId", "datetimeReceived", "quantityAccepted"), UtilMisc.toList("productId"), null, false);
    	 }catch(GenericEntityException e){
    		 Debug.logError(e, e.toString(), module);
             return ServiceUtil.returnError(e.toString());
    	 }
    	 Map totalProductQuant = FastMap.newInstance();
    	 List daywiseDespatch = FastList.newInstance();
    	 Map dayDespatch = FastMap.newInstance();
    	 for(int k =0;k<intervalDays;k++){
    		
    		List condList = FastList.newInstance(); 
 			Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
 			Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
 			Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate); 
 			List<GenericValue> daywiseParlourOrders = EntityUtil.filterByCondition(shipReceiptList, EntityCondition.makeCondition(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart),EntityOperator.AND,
 	    			 EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
 			List dayPartyList = FastList.newInstance();
 			List tempDayPartyList = FastList.newInstance();
 			tempDayPartyList = EntityUtil.getFieldListFromEntityList(daywiseParlourOrders, "facilityId", true);
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
 		 			Map productQuant = FastMap.newInstance();
 					boothId = (String)dayPartyList.get(i);
 					List daywiseBoothwiseSale = EntityUtil.filterByCondition(daywiseParlourOrders, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("facilityId"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)boothId).toUpperCase())));
 					for(int j=0 ; j<daywiseBoothwiseSale.size();j++){
 						GenericValue eachOrderItem = (GenericValue)daywiseBoothwiseSale.get(j);
 						String productId = eachOrderItem.getString("productId");
 						BigDecimal quantity = eachOrderItem.getBigDecimal("quantityAccepted");
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
 			dayDespatch.put(dayStart, partySale);
 			daywiseDespatch.add(dayDespatch);
 		 }
    	 result.put("totalDespatch", totalProductQuant);
    	 result.put("datewiseDespatch", daywiseDespatch);
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
	        Timestamp supplyDate = (Timestamp) context.get("supplyDate"); 
	        String boothId = (String) context.get("boothId");
	        String routeId = (String) context.get("routeId");
	        String tripId = (String) context.get("tripId");
	        Boolean isEnableProductSubscription = Boolean.FALSE;
	        String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
	        String subscriptionTypeId = (String) context.get("subscriptionTypeId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        if(UtilValidate.isNotEmpty(context.get("isEnableProductSubscription"))){
	           isEnableProductSubscription=(Boolean) context.get("isEnableProductSubscription");
	        }
	        Map result = ServiceUtil.returnSuccess(); 
	        Timestamp dayBegin = UtilDateTime.getDayStart(supplyDate);
	        List changeIndentProductList = FastList.newInstance();
	        Map<String ,BigDecimal> changeQuantityMap =FastMap.newInstance();
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
	    		Map qtyResultMap = getFacilityIndentQtyCategories(delegator, dctx.getDispatcher(), inputCtx);
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
	    		if(UtilValidate.isEmpty(routeId)){
	    			Map boothDetails = (Map)(getBoothRoute(dctx, context)).get("boothDetails");
	    			routeId = (String)boothDetails.get("routeId");
	    		}   
	    		result.put("routeId", routeId);
	    		result.put("tempRouteId",routeId);
	    		String subscriptionId = (EntityUtil.getFirst(subscriptionList)).getString("subscriptionId");
	    		/*List<GenericValue> quotaSubProdList =EntityUtil.filterByDate(delegator.findByAnd("SubscriptionProduct", UtilMisc.toMap("subscriptionId", subscriptionId, "productSubscriptionTypeId" ,productSubscriptionTypeId ,"sequenceNum" ,routeId), null) ,UtilDateTime.addDaysToTimestamp(dayBegin, -1));
	    		productList.addAll(EntityUtil.getFieldListFromEntityList(quotaSubProdList, "productId", true));
	    		for(GenericValue product : quotaSubProdList){
	    			String productId = product.getString("productId");
	    			if(UtilValidate.isEmpty(prevQuantityMap.get(productId))){
	    				prevQuantityMap.put(productId, product.getBigDecimal("quantity"));    				
	    			}else{
	    				prevQuantityMap.put(productId, (product.getBigDecimal("quantity")).add(prevQuantityMap.get(productId))); 
	    			}
	            }
	    		// lets get any changes already made
	    		*/
	    		conditionList.clear();
	    		conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
	    		conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
	    		conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
	    		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(dayBegin, -1))) , EntityOperator.OR ,EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null) ));
	    		/*conditionList.add(EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
	    		conditionList.add(EntityCondition.makeCondition("lastModifiedDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp())));*/
	    		EntityCondition cond= EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    		List<GenericValue>   subProdList =delegator.findList("SubscriptionProduct", cond, null,null, null, false);
	    		subProdList = EntityUtil.filterByDate(subProdList , dayBegin);
	    		/*Debug.log("subProdList=========="+subProdList);
	    		Debug.log("subscriptionId=========="+subscriptionId);*/
	    		if(UtilValidate.isNotEmpty(subProdList)){
	    			productList.addAll(EntityUtil.getFieldListFromEntityList(subProdList, "productId", true));
	    		}
	    		List<String> orderBy = UtilMisc.toList("-sequenceNum");
	    		productList = delegator.findList("Product",EntityCondition.makeCondition("productId" , EntityOperator.IN, productList), null, orderBy, null, true);
	    		String qtyCategory = "";
	    		Debug.log("prodIndentQtyCat===="+prodIndentQtyCat);
	    		for(GenericValue product : subProdList){
	    			result.put("tempRouteId", product.getString("sequenceNum"));
	    			String productId = product.getString("productId");
	    			if(UtilValidate.isNotEmpty(prodIndentQtyCat)){
	    				qtyCategory = (String)prodIndentQtyCat.get(productId);
	    			}
	    			
	    			BigDecimal quantityIncluded = (EntityUtil.getFirst(EntityUtil.filterByAnd(productList, UtilMisc.toMap("productId",productId)))).getBigDecimal("quantityIncluded");
	    			if(UtilValidate.isEmpty(changeQuantityMap.get(productId))){
	    				changeQuantityMap.put(productId, product.getBigDecimal("quantity")); 
	    				if(UtilValidate.isNotEmpty(qtyCategory) && qtyCategory.equals("CRATE_INDENT")){//if(crateIndentProductList.contains(productId)){
	    					if(UtilValidate.isEmpty(product.getBigDecimal("crateQuantity"))){
	    						product.set("crateQuantity", convertPacketsToCrates(quantityIncluded,product.getBigDecimal("quantity")));
	    						product.store();
	    					}else{
	    						changeQuantityMap.put(productId, product.getBigDecimal("crateQuantity"));
	    					}
	    					
	    				}
	    				   				
	    			}else{
	    				changeQuantityMap.put(productId, (product.getBigDecimal("quantity")).add(prevQuantityMap.get(productId)));
	    				if(UtilValidate.isNotEmpty(qtyCategory) && qtyCategory.equals("CRATE_INDENT")){//if(crateIndentProductList.contains(productId)){
	    					changeQuantityMap.put(productId, product.getBigDecimal("crateQuantity"));
	    				}
	    			}
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
	    	
	    	for(int i= (productList.size()-1);i >=0 ; i--){
	        	Map tempChangeProdMap = FastMap.newInstance();
	        	GenericValue product = (GenericValue)productList.get(i);
	        	String productId = product.getString("productId");
	        	/*if(UtilValidate.isNotEmpty(dayIndentProducts) &&  (dayIndentProducts.size() > i)){
	        		dayIndentProduct = dayIndentProducts.remove(i);        	
	        	}
	        	if(UtilValidate.isNotEmpty(contIndentProducts) && (contIndentProducts.size() >i)){
	        		conIndentProduct = contIndentProducts.remove(i);
	        		
	        	}*/
	        	
	        	/*if(UtilValidate.isNotEmpty(conIndentProduct)){*/
	        	
	        		tempChangeProdMap.put("id",productId);
	            	tempChangeProdMap.put("cProductId", productId);
	            	tempChangeProdMap.put("cProductName", product.getString("description"));
	            	tempChangeProdMap.put("cQuantity","");
	            	String qtyIndentCat = (String)prodIndentQtyCat.get(productId);
	            	tempChangeProdMap.put("indentProdCat","C");
	            	if(UtilValidate.isNotEmpty(qtyIndentCat)){
	            		tempChangeProdMap.put("indentProdCat",(char)qtyIndentCat.charAt(0));
	            	}
	            	
	            	/*if(crateIndentProductList.contains(productId)){
	            		tempChangeProdMap.put("indentProdCat","C");
	            	}
	            	
	            	if(packetIndentProductList.contains(productId)){
	            		tempChangeProdMap.put("indentProdCat","P");
	            	}*/
	            	if(UtilValidate.isNotEmpty(changeQuantityMap.get(productId))){
	            		tempChangeProdMap.put("cQuantity", changeQuantityMap.get(productId));
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
	    		
	    		result.put("routeCapacity", 0);
	    		if(UtilValidate.isNotEmpty(routeId)){
	    			GenericValue route = delegator.findOne("Facility", UtilMisc.toMap("facilityId", routeId), false);
	    			result.put("routeName", route.getString("facilityName"));
	    			result.put("routeCapacity", route.getBigDecimal("facilitySize"));
	    		}
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
			  Debug.log("orderChangeFlag ####################"+orderChangeFlag);
			      
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
				  Debug.log("new order ID ###########################"+newOrderId);
		        // approve the order
				  if (UtilValidate.isNotEmpty(newOrderId)) {
					  boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, newOrderId);       
					  try{            	
						  result = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", newOrderId,"userLogin", userLogin));
						  if (ServiceUtil.isError(result)) {
							  Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(result), module);
							  return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(result));          	            
				          } 
						  Debug.log("new invoiceId ####################"+(String)result.get("invoiceId"));
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
	    		Debug.log("shipmentId ######################"+shipmentId);
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
			Debug.log("result #######################################"+result);
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
		            // handle Replacement here 
		            /*if(productSubscriptionTypeId.equals("REPLACEMENT_BYPROD")){
		            	List<GenericValue> transProductList = FastList.newInstance();
		            	
		            	for(GenericValue SubscriptionProduct : subscriptionProductsList){
		            		GenericValue tempSubscriptionProduct = delegator.makeValue("SubscriptionFacilityAndSubscriptionProduct");
		            		tempSubscriptionProduct.putAll(SubscriptionProduct);
		            		tempSubscriptionProduct.put("facilityId", SubscriptionProduct.getString("destinationFacilityId"));
		            		transProductList.add(tempSubscriptionProduct);
		            	}
		            	
		            	Map transferCtx = UtilMisc.toMap("userLogin",userLogin);	  	
		       	  	 	transferCtx.put("shipmentId", shipmentId);
		       	  	 	transferCtx.put("estimatedDeliveryDate", estimatedDeliveryDate);
		       	  	 	transferCtx.put("subscriptionProductsList", transProductList);	  	 
		       	  	 	try{
		       	  	 		Map result = dispatcher.runSync("receiveParlorInventory",transferCtx);  		  		 
		       	  	 		if (ServiceUtil.isError(result)) {
		       	  	 			String errMsg =  ServiceUtil.getErrorMessage(result);
		       	  	 			Debug.logError(errMsg , module);       				
		       	  	 			return result;
		       	  	 		}
		       			 
		       	  	 	}catch (Exception e) {
		       	  			  Debug.logError(e, "Problem while doing Stock Transfer for Relacement", module);     
		       	  			  return resultMap;			  
		       	  	 	}
		            }*/
		            
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
			
			List shipmentIds = FastList.newInstance();
			if(!subscriptionType.equals("AM") && !subscriptionType.equals("PM")){			
				return shipmentIds;			
			}
			if(subscriptionType.equals("AM")){
				shipmentIds = getShipmentIds(delegator , estimatedDeliveryDateString,"AM_SHIPMENT");
				shipmentIds.addAll(getShipmentIds(delegator , estimatedDeliveryDateString,"AM_SHIPMENT_SUPPL"));
			}else{
				shipmentIds = getShipmentIds(delegator , estimatedDeliveryDateString,"PM_SHIPMENT");			
				shipmentIds.addAll(getShipmentIds(delegator , estimatedDeliveryDateString,"PM_SHIPMENT_SUPPL"));			
			}		
			return shipmentIds;
		}
		// This will return the list of ShipmentIds for the selected 
		public static List getShipmentIds(Delegator delegator,String estimatedDeliveryDateString,String shipmentTypeId){
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
				paymentsList = delegator.findList("PaymentAndFacility", condition, null, UtilMisc.toList("-lastModifiedDate"), null, false);			
				String tempFacilityId = "";	
				Map tempPayment = FastMap.newInstance();
				for (int i = 0; i < paymentsList.size(); i++) {				
					GenericValue boothPayment = (GenericValue)paymentsList.get(i);				
					if(tempFacilityId == ""){
						tempFacilityId = boothPayment.getString("facilityId");
						tempPayment.put("facilityId", boothPayment.getString("facilityId"));
						tempPayment.put("routeId", boothPayment.getString("parentFacilityId"));
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
					GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
					if(facility.getString("facilityTypeId").equals("ROUTE")){
						facilityIds = getRouteBooths(delegator,facilityId);
					}
					else{
						 facilityIds.add(facilityId);
					}
					List<GenericValue> facilityOwnerParty = delegator.findList("Facility",EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds) , null, UtilMisc.toList("facilityId"), null, false);
					List ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilityOwnerParty, "ownerPartyId", false);
					for(GenericValue eachBooth : facilityOwnerParty){
						facilityOwner.put(eachBooth.getString("ownerPartyId"), eachBooth.getString("facilityId"));
					}
					List paymentTypeConditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.IN, ownerPartyIds));
					paymentTypeConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
					paymentTypeConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayEnd(fromDate))));
			        EntityCondition paymentTypeCondition = EntityCondition.makeCondition(paymentTypeConditionList, EntityOperator.AND);
			        List<GenericValue> paymentTypeList = delegator.findList("PartyProfileDefault", paymentTypeCondition, null, null, null, false);
			        paymentTypeList = EntityUtil.filterByDate(paymentTypeList, UtilDateTime.getDayStart(fromDate));
					for(GenericValue eachBoothMeth : paymentTypeList){
						String owner = eachBoothMeth.getString("partyId");
						facilityPaymentMethod.put(facilityOwner.get(owner), eachBoothMeth.getString("defaultPayMeth"));
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
			Set shipmentIds = FastSet.newInstance();		 
			Map boothsPaymentsDetail = FastMap.newInstance();
			BigDecimal invoicesTotalAmount = BigDecimal.ZERO;
			BigDecimal invoicesTotalDueAmount = BigDecimal.ZERO;		
			if(thruDate != null){				
				shipmentIds = new HashSet(getShipmentIds(delegator ,fromDate,thruDate));			
			}
			Map paymentMethod = (Map)getPaymentMethodTypeForBooth(dctx, UtilMisc.toMap("facilityId", facilityId, "userLogin", userLogin)).get("partyPaymentMethod");
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
						exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, getZoneBooths(delegator,facilityId)));
					}else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")){
						exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, getRouteBooths(delegator,facilityId)));
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
				boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null , UtilMisc.toList("parentFacilityId","originFacilityId","-estimatedDeliveryDate"), findOptions, false);
			}catch(GenericEntityException e){
				Debug.logError(e, module);	
				return ServiceUtil.returnError(e.toString());
			}
			List<GenericValue> obInvoiceList = (List)getOpeningBalanceInvoices(dctx,UtilMisc.toMap("facilityId",facilityId,"fromDate", fromDate ,"thruDate" , thruDate)).get("invoiceList");
			boothOrdersList.addAll(obInvoiceList);
			boothOrdersList = EntityUtil.orderBy(boothOrdersList, UtilMisc.toList("parentFacilityId","originFacilityId","-estimatedDeliveryDate"));
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
						tempPayment.put("supplyDate",  boothPayment.getTimestamp("estimatedDeliveryDate"));
						tempPayment.put("paymentMethodType", paymentMethod.get(tempFacilityId));
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
						tempPayment.put("paymentMethodType", paymentMethod.get(tempFacilityId));
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
	            BigDecimal price  = orderItem.getBigDecimal("unitListPrice"); 
	            BigDecimal revenue = price.multiply(quantity);
	            totalRevenue = totalRevenue.add(revenue);
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
	    				supplyTypeDetailsMap.put("totalRevenue", ZERO);
	    				iteratorMap.put(type.getString("enumId"), supplyTypeDetailsMap);
	    				newMap.put("supplyTypeTotals", iteratorMap);
	    			}
			    	
			    	Map supplyTypeMap =  (Map)newMap.get("supplyTypeTotals");
			    	Map supplyTypeDetailsMap = (Map)supplyTypeMap.get(prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeMap.put(prodSubscriptionTypeId, supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);
					
					Map<String, Object> productItemMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();

					productItemMap.put("name", productName);
					productSupplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
					productSupplyTypeDetailsMap.put("total", quantity);
					productSupplyTypeDetailsMap.put("totalRevenue", revenue);
					productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), productSupplyTypeDetailsMap);
					productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
	                productItemMap.put("total", quantity);
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
					boothMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal)boothMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					boothMap.put("totalRevenue", runningTotalRevenue);    			    				
					// next handle type totals
					Map tempMap = (Map)boothMap.get("supplyTypeTotals");
					Map typeMap = (Map)tempMap.get(prodSubscriptionTypeId);
					BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
					typeRunningTotal = typeRunningTotal.add(quantity);
					BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
					typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);
					
					typeMap.put("name", prodSubscriptionTypeId);
					typeMap.put("total", typeRunningTotal);
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
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	    				productItemMap.put("name", productName);
	    				productItemMap.put("supplyTypeTotals", supplyTypeMap);
	    				productItemMap.put("total", quantity);
	    				productItemMap.put("totalRevenue", revenue);
	    				boothProductTotals.put(productId, productItemMap);
	    				
					}else{
						BigDecimal productRunningTotal = (BigDecimal)productMap.get("total");
						 productRunningTotal = productRunningTotal.add(quantity);
	    				productMap.put("total", productRunningTotal);
	    				BigDecimal productRunningTotalRevenue = (BigDecimal)productMap.get("totalRevenue");
	    				productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
	    				productMap.put("totalRevenue", productRunningTotalRevenue);
	    				
	    				
	    				Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
	    				if(supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId") )!= null){
	    					Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
	    					
	    					BigDecimal runningTotalproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("total");
	        				runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
	        				
	        				BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("totalRevenue");
	        				runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);
	        				
	        				supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	        				supplyTypeDetailsMap.put("total", runningTotalproductSubscriptionType);
	        				supplyTypeDetailsMap.put("totalRevenue", runningTotalRevenueproductSubscriptionType);
	        				supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	        				productMap.put("supplyTypeTotals", supplyTypeMap);
	        				boothProductTotals.put(productId, productMap);
	        				
	    				}else{
	    					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();	    					
	    					supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	    					supplyTypeDetailsMap.put("total", quantity);
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
	    				supplyTypeDetailsMap.put("totalRevenue", ZERO);
	    				iteratorMap.put(type.getString("enumId"), supplyTypeDetailsMap);
	    				newMap.put("supplyTypeTotals", iteratorMap);
					}
			    	
			    	Map supplyTypeMap =  (Map)newMap.get("supplyTypeTotals");
			    	Map supplyTypeDetailsMap = (Map)supplyTypeMap.get(prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeMap.put(prodSubscriptionTypeId, supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);
					
					Map<String, Object> productItemMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();

					productItemMap.put("name", productName);
					productSupplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
					productSupplyTypeDetailsMap.put("total", quantity);
					productSupplyTypeDetailsMap.put("totalRevenue", revenue);
					productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), productSupplyTypeDetailsMap);
					productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
	                productItemMap.put("total", quantity);
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
					// next handle type totals
					Map tempMap = (Map)dayWiseMap.get("supplyTypeTotals");
					Map typeMap = (Map)tempMap.get(prodSubscriptionTypeId);
					BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
					typeRunningTotal = typeRunningTotal.add(quantity);
					BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
					typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);
					
					typeMap.put("name", prodSubscriptionTypeId);
					typeMap.put("total", typeRunningTotal);
					typeMap.put("totalRevenue", typeRunningTotalRevenue);
					
					// next handle product totals
					Map dayWiseProductTotals = (Map)dayWiseMap.get("productTotals");
					Map productMap = (Map)dayWiseProductTotals.get(productId);
					
					if(UtilValidate.isEmpty(productMap)){
						
						Map<String, Object> productItemMap = FastMap.newInstance();
						Map<String, Object> supplyTypeMap = FastMap.newInstance();
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	    				productItemMap.put("name", productName);
	    				productItemMap.put("supplyTypeTotals", supplyTypeMap);
	    				productItemMap.put("total", quantity);
	    				productItemMap.put("totalRevenue", revenue);
	    				dayWiseProductTotals.put(productId, productItemMap);
	    				
					}else{
						BigDecimal productRunningTotal = (BigDecimal)productMap.get("total");
						 productRunningTotal = productRunningTotal.add(quantity);
	    				productMap.put("total", productRunningTotal);
	    				BigDecimal productRunningTotalRevenue = (BigDecimal)productMap.get("totalRevenue");
	    				productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
	    				productMap.put("totalRevenue", productRunningTotalRevenue);
	    				
	    				
	    				Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
	    				if(supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId") )!= null){
	    					Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
	    					
	    					BigDecimal runningTotalproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("total");
	        				runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
	        				
	        				BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("totalRevenue");
	        				runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);
	        				
	        				supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	        				supplyTypeDetailsMap.put("total", runningTotalproductSubscriptionType);
	        				supplyTypeDetailsMap.put("totalRevenue", runningTotalRevenueproductSubscriptionType);
	        				supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
	        				productMap.put("supplyTypeTotals", supplyTypeMap);
	        				dayWiseProductTotals.put(productId, productMap);
	        				
	    				}else{
	    					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();	    					
	    					supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	    					supplyTypeDetailsMap.put("total", quantity);
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
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"), supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);
					newMap.put("total", quantity);
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
						BigDecimal runningRevenueproductSubscriptionType = (BigDecimal)supplyTypeDetailsMap.get("totalRevenue");
						runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
	    				runningRevenueproductSubscriptionType = runningRevenueproductSubscriptionType.add(revenue);
	    				supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
	    				supplyTypeDetailsMap.put("total", runningTotalproductSubscriptionType);
	    				supplyTypeDetailsMap.put("totalRevenue", runningRevenueproductSubscriptionType);
	    				supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
	    				productMap.put("supplyTypeTotals", supplyTypeMap);
					}else{
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
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
				}
			}
	    	  	
			totalQuantity = totalQuantity.setScale(decimals, rounding);  
			totalRevenue = totalRevenue.setScale(decimals, rounding);    
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
	        	tempVal = (BigDecimal)supplyTypeValue.get("totalRevenue");
	        	tempVal = tempVal.setScale(decimals, rounding); 
	        	supplyTypeValue.put("totalRevenue", tempVal);	    	        	
	        }	        

			
			Map<String, Object> result = FastMap.newInstance();        
	        result.put("totalQuantity", totalQuantity);
	        result.put("totalRevenue", totalRevenue);
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
	    public static Map<String, Object> getFacilityIndentQtyCategories(Delegator delegator, LocalDispatcher dispatcher, Map<String, ? extends Object> context) {
	        Map<String, Object> result = FastMap.newInstance();
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
	    	Map quantityIncludedMap = FastMap.newInstance();
	    	BigDecimal crateLtrQty = BigDecimal.ZERO;
	        try{
	        	productCategory = delegator.findList("ProductCategory", EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "PROD_INDENT_CAT"), UtilMisc.toSet("productCategoryId"), null, null, false);
	        	productCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);

	        	List condList =FastList.newInstance();
	        	condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	        	/*condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	    			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
	        	*/EntityCondition productsListCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
	        	List<String> orderBy = UtilMisc.toList("sequenceNum");
	    	
	    		productList =delegator.findList("ProductAndCategoryMember", productsListCondition,null,orderBy, null, false);
	    		
	    		for(GenericValue productCat : productList){
	    			indentQtyCategory.put(productCat.getString("productId"), productCat.getString("productCategoryId"));
	    			quantityIncludedMap.put(productCat.getString("productId"), (BigDecimal)productCat.get("quantityIncluded"));
	            }
	    		
	    		List<GenericValue> facWiseProdCat = delegator.findList("FacilityWiseProductCategory", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), null, null, null, false);
	    		facWiseProdCat = EntityUtil.filterByDate(facWiseProdCat, dayBegin);
	    		for(GenericValue facilityProdCat : facWiseProdCat){
	    			indentQtyCategory.put(facilityProdCat.getString("productId"), facilityProdCat.getString("productCategoryId"));
	            }
	    		
	    		GenericValue uomCrateConversion = delegator.findOne("UomConversion", UtilMisc.toMap("uomId","VLIQ_CRT", "uomIdTo", "VLIQ_L"),false);
	    		if(UtilValidate.isNotEmpty(uomCrateConversion)){
		    		crateLtrQty = new BigDecimal(uomCrateConversion.getDouble("conversionFactor"));
		    	}
	    		
	    	}catch (Exception e) {
	    		Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}
	    	Map qtyInPiecesMap = FastMap.newInstance();
	    	for ( Map.Entry<String, String> entry : indentQtyCategory.entrySet()){
				
	        	String prodId = (String)entry.getKey();
	        	String qtyCat = (String)entry.getValue();
	        	if(qtyCat.equals("CRATE_INDENT")){
	        		BigDecimal qtyInc = (BigDecimal)quantityIncludedMap.get(prodId);
	        		BigDecimal packetQty = (crateLtrQty.divide(qtyInc,2,rounding)).setScale(2, rounding);
	    			//BigDecimal packetQty = NetworkServices.convertCratesToPackets(qtyInc , BigDecimal.ONE);
	    			qtyInPiecesMap.put(prodId, packetQty);
	    		}
	    		else{
	    			qtyInPiecesMap.put(prodId, BigDecimal.ONE);
	    		}
			}
	    	result.put("qtyInPieces", qtyInPiecesMap);
	    	result.put("indentQtyCategory", indentQtyCategory);
	        return result;
	    }
	    public static BigDecimal convertPacketsToCrates(BigDecimal quantityIncluded , BigDecimal packetQuantity){
	    	BigDecimal crateQuantity = BigDecimal.ZERO;
	    	crateQuantity = (packetQuantity.multiply(quantityIncluded)).divide(new BigDecimal(12), 2 ,rounding);
	    	return crateQuantity;
	    }
	    public static BigDecimal convertCratesToPackets(BigDecimal quantityIncluded , BigDecimal crateQuantity){
	    	BigDecimal packetQuantity = BigDecimal.ZERO;
	    	packetQuantity = ((new BigDecimal(12)).divide(quantityIncluded, 2, rounding)).multiply(crateQuantity);
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
		    
		    List<GenericValue> crateIndentProductList = ProductWorker.getProductsByCategory(delegator ,"CRATE_INDENT" ,UtilDateTime.getDayStart(shipDate));
		    List crateProductIds = EntityUtil.getFieldListFromEntityList(crateIndentProductList, "productId", true);
		    /*Map prodQtyIncMap = FastMap.newInstance();
		    for(GenericValue crateIndentProduct : crateIndentProductList){
		    	prodQtyIncMap.put(crateIndentProduct.getString("productId"), crateIndentProduct.getBigDecimal("quantityIncluded"));
		    }*/
		    
		    List facilityList = FastList.newInstance();
		    exprList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, crateProductIds));		
			exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			EntityCondition	cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);				
			
			try{
				List<GenericValue> shippedOrderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", cond, null ,null, null, false);
				facilityList = EntityUtil.getFieldListFromEntityList(shippedOrderItems, "originFacilityId", true);
				String facilityId = "";
				BigDecimal totalCrateQty = BigDecimal.ZERO;
				Map partyCratesMap = FastMap.newInstance();
				for(int i=0;i<facilityList.size();i++){
					facilityId = (String)facilityList.get(i);
					List<GenericValue> facilityOrderItems = EntityUtil.filterByCondition(shippedOrderItems, EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
					int partyCrates = 0;
					BigDecimal totalQty = BigDecimal.ZERO;
					for(GenericValue orderItem : facilityOrderItems){
						BigDecimal qtyInc = orderItem.getBigDecimal("quantityIncluded");
						BigDecimal qty = orderItem.getBigDecimal("quantity");
						totalQty = totalQty.add((qty.multiply(qtyInc)));
					}
					partyCrates = (totalQty.divide(BigDecimal.valueOf(12.0))).intValue();
					partyCratesMap.put(facilityId, partyCrates);
					totalCrateQty = totalCrateQty.add(totalQty);
				}
				BigDecimal totCrate = (totalCrateQty.divide(BigDecimal.valueOf(12))).setScale(0, BigDecimal.ROUND_CEILING);
				partyCratesMap.put("totalCrates", totCrate);
				result.put("shipmentCrates", partyCratesMap);
			}catch (Exception e) {
				Debug.logError("Error fetching shipped order Items", module);
	    		return ServiceUtil.returnError("Error fetching shipped order Items"); 
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
}
	
	
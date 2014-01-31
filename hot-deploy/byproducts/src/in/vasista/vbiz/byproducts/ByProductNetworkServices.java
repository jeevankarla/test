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
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.network.NetworkServices;
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
import org.ofbiz.network.NetworkServices;

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
	        String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
	        String subscriptionTypeId = (String) context.get("subscriptionTypeId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
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
	    		
	    		Map inputCtx = FastMap.newInstance();
	    		inputCtx.put("userLogin", userLogin);
	    		inputCtx.put("supplyDate",dayBegin);
	    		inputCtx.put("facilityId",boothId);
	    		Map qtyResultMap = NetworkServices.getFacilityIndentQtyCategories(delegator, dctx.getDispatcher(), inputCtx);
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
	    			Map boothDetails = (Map)(NetworkServices.getBoothRoute(dctx, context)).get("boothDetails");
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
	    		for(GenericValue product : subProdList){
	    			result.put("tempRouteId", product.getString("sequenceNum"));
	    			String productId = product.getString("productId");
	    			qtyCategory = (String)prodIndentQtyCat.get(productId);
	    			BigDecimal quantityIncluded = (EntityUtil.getFirst(EntityUtil.filterByAnd(productList, UtilMisc.toMap("productId",productId)))).getBigDecimal("quantityIncluded");
	    			if(UtilValidate.isEmpty(changeQuantityMap.get(productId))){
	    				changeQuantityMap.put(productId, product.getBigDecimal("quantity")); 
	    				if(qtyCategory.equals("CRATE_INDENT")){//if(crateIndentProductList.contains(productId)){
	    					if(UtilValidate.isEmpty(product.getBigDecimal("crateQuantity"))){
	    						product.set("crateQuantity", NetworkServices.convertPacketsToCrates(quantityIncluded,product.getBigDecimal("quantity")));
	    						product.store();
	    					}else{
	    						changeQuantityMap.put(productId, product.getBigDecimal("crateQuantity"));
	    					}
	    					
	    				}
	    				   				
	    			}else{
	    				changeQuantityMap.put(productId, (product.getBigDecimal("quantity")).add(prevQuantityMap.get(productId)));
	    				if(qtyCategory.equals("CRATE_INDENT")){//if(crateIndentProductList.contains(productId)){
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
	            	tempChangeProdMap.put("indentProdCat",(char)qtyIndentCat.charAt(0));
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
	    			Map boothDetails = (Map)(NetworkServices.getBoothRoute(dctx, context)).get("boothDetails");
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
}
	
	